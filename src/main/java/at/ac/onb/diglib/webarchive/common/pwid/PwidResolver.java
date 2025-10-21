package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private String getCaptureWithUrl(PWID pwid) {
    	return pwid.getTimestamp14() + "/" + pwid.getUri();
    }

    public String getResolvedUrl(PWID pwid) {
		String baseUrl = registry.getReplayBaseUrl(pwid.getArchiveId());
    	if (baseUrl == null) {
    		return null;
    	}

    	return baseUrl + getCaptureWithUrl(pwid);
    }
}
