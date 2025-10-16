package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PwidResolver {
	protected static final Log log = LogFactory.getLog(PwidResolver.class);

	public static final int ARCHIVEID_NOT_IDENTIFIED = -1;

    public static final int PWID_ARCHIVEID = 0;
    public static final int PWID_ARCHIVE_RESOLVEDURLBEGIN = 1;
    public static final String PWID_ARCHIVEID_WEBARCHIV_ONB_AC_AT = "webarchiv.onb.ac.at";
    public static final String PWID_ARCHIVEID_WEBARCHIV_DNB_DE = "webarchiv.dnb.de";
    public static final String PWID_ARCHIVEID_ARCHIVE_ORG = "archive.org";
    public static final String PWID_ARQUIVO_PT = "arquivo.pt";
    public static final String PWID_VEFSAFN_IS = "vefsafn.is";

	public static final String[][] WEBARCHIVES = {
			{ PWID_ARCHIVEID_WEBARCHIV_ONB_AC_AT, "webarchiv.onb.ac.at/web/" },
			{ PWID_ARCHIVEID_WEBARCHIV_DNB_DE, "webarchiv.dnb.de/playback/" },
			{ PWID_ARCHIVEID_ARCHIVE_ORG, "web.archive.org/web/" },
			{ PWID_ARQUIVO_PT, "arquivo.pt/wayback/" },
			{ PWID_VEFSAFN_IS, "vefsafn.is/" }
		};

    public static final String PWID_RESOLVERURL = "http://localhost:8080/resolve?pwid=";

    PWID pwid;
    boolean valid = false;
    boolean supported = false;
    boolean publicAvailable = false;
	PwidRegistry registry = null;
    int archive_id = -1;
	String archive_id_str = "";

    public PwidResolver(PWID aPwid) {
		if (aPwid == null) {
			return;
		}
		pwid = aPwid;
		init();
    }

    public PwidResolver(String aPwid) {
    	try {
			pwid = PWID.parsePWID(aPwid.trim());
			init();
		} catch (PwidParseException e) {
			log.error("PWID String is not valid");
		}
    }

    private void init() {
		valid = true;
		registry = new PwidRegistry();
		supported = registry.isArchiveSupported(pwid.getArchiveId());
		archive_id_str = pwid.getArchiveId();
		pwid.setResolvingUri(PWID_RESOLVERURL + pwid.getUrn());
		pwid.setResolvedUrl(getResolvedUrl());
    }

    public boolean isValid() {
    	return valid;
    }

    public boolean isSupported() {
    	return supported;
    }

    private String getCaptureWithUrl() {
    	return pwid.getTimestamp14() + "/" + pwid.getUri();
    }

    public String getResolvedUrl() {
		String baseUrl = registry.getReplayBaseUrl(pwid.getArchiveId());
    	if (baseUrl == null) {
    		return null;
    	}

    	return baseUrl + getCaptureWithUrl();
    }
}
