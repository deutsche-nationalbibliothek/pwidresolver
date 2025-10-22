package at.ac.onb.diglib.webarchive.common.pwid;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import at.ac.onb.diglib.webarchive.common.pwid.data.Resolver;

public class PwidResolver {
	protected static final Log log = LogFactory.getLog(PwidResolver.class);

	PwidRegistry registry;
	String resolverBaseUrl;

    public PwidResolver(PwidRegistry registry, String resolverBaseUrl) {
		this.registry = registry;
		this.resolverBaseUrl = resolverBaseUrl;
    }

	public PWID resolve(String pwidString) throws PwidParseException {
		return resolve(PWID.parsePWID(pwidString));
	}

	public PWID resolve(PWID pwid) throws PwidParseException {
		pwid.setResolvingUri(resolverBaseUrl + pwid.getUrn());
	    pwid.setResolvedUrl(getResolvedUrl(pwid));
		return pwid;
	}

	public static PWID resolveAny(String archiveString, PwidRegistry registry, String resolverBaseUrl) throws PwidParseException, PwidUnsupportedException {
        PWID pwid = null;
        PwidResolver resolver;
		if (archiveString.startsWith("urn")) {
			resolver = new PwidResolver(registry, resolverBaseUrl);
			pwid = resolver.resolve(archiveString);
		} else {
			resolver = new PwidReverseResolver(registry, resolverBaseUrl);
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

	public String renderResolverUriTemplate(String template, PWID pwid) {
		HashMap<String, Object> scopes = new HashMap<String, Object>();

		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
		SimpleDateFormat secFormat = new SimpleDateFormat("ss");
		yearFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		monthFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		hourFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		minuteFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		secFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		scopes.put("archive-id", pwid.getArchiveId());
		scopes.put("utc-year", yearFormat.format(pwid.timestamp));
		scopes.put("utc-month", monthFormat.format(pwid.timestamp));
		scopes.put("utc-day", dayFormat.format(pwid.timestamp));
		scopes.put("utc-hour", hourFormat.format(pwid.timestamp));
		scopes.put("utc-minute", minuteFormat.format(pwid.timestamp));
		scopes.put("utc-sec", secFormat.format(pwid.timestamp));
		scopes.put("archived-uri", pwid.getUri());
		scopes.put("pwid", pwid.getUrn());

		Writer writer = new StringWriter();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(new StringReader(template), "pathPattern");
		mustache.execute(writer, scopes);
		return writer.toString();
	}

}
