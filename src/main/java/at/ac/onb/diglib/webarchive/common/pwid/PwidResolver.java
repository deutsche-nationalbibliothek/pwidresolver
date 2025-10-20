package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PwidResolver {
	protected static final Log log = LogFactory.getLog(PwidResolver.class);

    public static final String PWID_RESOLVERURL = "http://localhost:8080/resolve?pwid=";

	PwidRegistry registry;

    public PwidResolver() {
		registry = new PwidRegistry();
    }

	public PWID resolve(String pwidString) throws PwidParseException {
		return resolve(PWID.parsePWID(pwidString));
	}

	public PWID resolve(PWID pwid) throws PwidParseException {
		pwid.setResolvingUri(PWID_RESOLVERURL + pwid.getUrn());
	    pwid.setResolvedUrl(getResolvedUrl(pwid));
		return pwid;
	}

	public static PWID resolveAny(String archieString) throws PwidParseException, PwidUnsupportedException {
        PWID pwid = null;
        PwidResolver resolver;
		if (archieString.startsWith("urn")) {
			resolver = new PwidResolver();
			pwid = resolver.resolve(archieString);
		} else {
			resolver = new PwidReverseResolver();
			pwid = resolver.resolve(archieString);
		}
        if (!resolver.isSupported(pwid)) {
            throw new PwidUnsupportedException("The requested archive string is unsupported by this resolver: " + archieString);
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
