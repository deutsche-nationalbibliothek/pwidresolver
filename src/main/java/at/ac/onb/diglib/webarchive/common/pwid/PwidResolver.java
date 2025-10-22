package at.ac.onb.diglib.webarchive.common.pwid;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import at.ac.onb.diglib.webarchive.common.pwid.data.Archive;
import at.ac.onb.diglib.webarchive.common.pwid.data.Resolver;

public class PwidResolver {
	protected static final Log log = LogFactory.getLog(PwidResolver.class);

	PwidRegistry registry;
	Resolver defaultResolver;

    public PwidResolver(PwidRegistry registry, Resolver defaultResolver) {
		this.registry = registry;
		this.defaultResolver = defaultResolver;
    }

	public PWID resolve(String pwidString) throws PwidParseException {
		return resolve(PWID.parsePWID(pwidString));
	}

	public PWID resolve(PWID pwid) throws PwidParseException {
		pwid.setResolvingUri(getResolvingUri(pwid));
	    pwid.setResolvedUrl(getResolvedUrl(pwid));
		return pwid;
	}

	public static PWID resolveAny(String archiveString, PwidRegistry registry, Resolver defaultResolver) throws PwidParseException, PwidUnsupportedException {
        PWID pwid = null;
        PwidResolver resolver;
		if (archiveString.startsWith("urn")) {
			resolver = new PwidResolver(registry, defaultResolver);
			pwid = resolver.resolve(archiveString);
		} else {
			resolver = new PwidReverseResolver(registry, defaultResolver);
			pwid = resolver.resolve(archiveString);
		}
        if (!resolver.isSupported(pwid)) {
            throw new PwidUnsupportedException("The requested archive string is unsupported by this resolver: " + archiveString);
        }
		return pwid;
	}

	/**
	 * Check if a given pwid is supported by the registry.
	 */
    public boolean isSupported(PWID pwid) {
    	return registry.isArchiveSupported(pwid.getArchiveId());
    }

	/**
	 * Get the resolved replay URL for a given PWID.
	 * @param pwid
	 * @return The replay URL
	 */
    public String getResolvedUrl(PWID pwid) {
		Resolver replay = registry.getReplay(pwid.getArchiveId());
    	if (replay == null) {
    		return null;
		}

		return UriComponentsBuilder.fromUriString(replay.getBaseUrl()).path(renderResolverUriTemplate(replay.getPathPattern(), pwid)).build().toUriString();
    }

	/**
	 * Get the resolved replay URL for a given PWID.
	 * @param pwid
	 * @return The replay URL
	 */
    public String getResolvingUri(PWID pwid) {
		Archive archive = registry.getArchive(pwid.getArchiveId());
		if (archive == null) return null;
		Resolver resolver = archive.getResolver();
    	if (resolver == null) resolver = defaultResolver;

		return UriComponentsBuilder.fromUriString(resolver.getBaseUrl()).path(renderResolverUriTemplate(resolver.getPathPattern(), pwid)).build().toUriString();
    }

	public String renderResolverUriTemplate(String template, PWID pwid) {
		HashMap<String, Object> scopes = new HashMap<String, Object>();

		scopes.put("archive-id", pwid.getArchiveId());
		scopes.put("utc-year", dateTemplate("yyyy", pwid));
		scopes.put("utc-month", dateTemplate("MM", pwid));
		scopes.put("utc-day", dateTemplate("dd", pwid));
		scopes.put("utc-hour", dateTemplate("HH", pwid));
		scopes.put("utc-minute", dateTemplate("mm", pwid));
		scopes.put("utc-sec", dateTemplate("ss", pwid));
		scopes.put("archived-uri", pwid.getUri());
		scopes.put("pwid", pwid.getUrn());

		Writer writer = new StringWriter();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(new StringReader(template), "pathPattern");
		mustache.execute(writer, scopes);
		return writer.toString();
	}

	Callable<String> dateTemplate(String pattern, PWID pwid) {
		return new Callable<String>() {

			@Override
			public String call() throws Exception {
				SimpleDateFormat yearFormat = new SimpleDateFormat(pattern);
				yearFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				return yearFormat.format(pwid.timestamp);
			}
		};
	}
}
