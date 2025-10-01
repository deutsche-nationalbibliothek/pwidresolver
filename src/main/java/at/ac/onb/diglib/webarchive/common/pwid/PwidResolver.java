package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PwidResolver {
	protected static final Log log = LogFactory.getLog(PwidResolver.class);

	public static final int ARCHIVEID_NOT_IDENTIFIED = -1;

    public static final int PWID_ARCHIVEID = 0;
    public static final int PWID_ARCHIVE_RESOLVEDURLBEGIN = 1;
    public static final String PWID_ARCHIVEID_WEBARCHIV_ONB_AC_AT = "webarchiv.onb.ac.at";
    public static final String PWID_ARCHIVEID_ARCHIVE_ORG = "archive.org";
    public static final String PWID_ARQUIVO_PT = "arquivo.pt";
    public static final String PWID_VEFSAFN_IS = "vefsafn.is";

    public static final int ARCHIVEID_WEBARCHIV_ONB_AC_AT = 0;
    public static final int ARCHIVEID_ARCHIVE_ORG = 1;
    public static final int ARCHIVEID_ARQUIVO_PT = 2;
    public static final int ARCHIVEID_VEFSAFN_IS = 3;

	public static final String[][] WEBARCHIVES = {
			{ PWID_ARCHIVEID_WEBARCHIV_ONB_AC_AT, "webarchiv.onb.ac.at/web/" },
			{ PWID_ARCHIVEID_ARCHIVE_ORG, "web.archive.org/web/" },
			{ PWID_ARQUIVO_PT, "arquivo.pt/wayback/" },
			{ PWID_VEFSAFN_IS, "vefsafn.is/" }
		};
    
    public static final String PWID_RESOLVERURL = "https://webarchiv.onb.ac.at/resolve.jsp?pwid=";

    PWID pwid;
    boolean valid = false;
    boolean supported = false;
    boolean publicAvailable = false;
    int archive_id = -1;

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

    public int getArchiveId() {
    	return archive_id;
    }

    public boolean isOnbWebarchiv() {
    	if (archive_id == ARCHIVEID_WEBARCHIV_ONB_AC_AT) {
    		return true;
    	}

    	return false;
    }

    private static String removeProtocol(String aUrl) {
    	if (aUrl == null) {
    		return null;

    	}
		if (aUrl.startsWith("http://")) {
			aUrl = aUrl.substring(7);
		}
		else if (aUrl.startsWith("https://")) {
			aUrl = aUrl.substring(8);
		}

		return aUrl;
    }

    private boolean isArchiveSupported(String aArchiveId) {
    	for (int i=0; i < WEBARCHIVES.length; i++) {
    		if (WEBARCHIVES[i][PWID_ARCHIVEID].equals(aArchiveId.toLowerCase())) {
    			archive_id = i;
    			return true;
    		}
    	}

    	return false;
    }

    private static int identifyArchiveIdInUrl(String aUrl) {
    	for (int i=0; i < WEBARCHIVES.length; i++) {

    		aUrl = removeProtocol(aUrl);

    		if (aUrl.startsWith(WEBARCHIVES[i][PWID_ARCHIVE_RESOLVEDURLBEGIN])) {
    			return i;
    		}
    	}

    	return -1;
    }

    private void init() {
		valid = true;
		supported = isArchiveSupported(pwid.getArchiveId());
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
    	if (!supported) {
    		return null;
    	}

    	return "https://" + WEBARCHIVES[archive_id][PWID_ARCHIVE_RESOLVEDURLBEGIN] + getCaptureWithUrl();
    }
    public static PWID parseArchiveUrl(String aArchiveUrl) throws PwidParseException {
    	if (aArchiveUrl == null) {
    		throw new PwidParseException("archive url is null");
    	}

    	int archive_id;
    	if ((archive_id = identifyArchiveIdInUrl(aArchiveUrl)) == ARCHIVEID_NOT_IDENTIFIED) {
    		throw new PwidParseException("archive not identified");
    	}

    	String capture = null;
    	String uri = null;

    	try {
    		aArchiveUrl = removeProtocol(aArchiveUrl);
    		String beginStr = WEBARCHIVES[archive_id][PWID_ARCHIVE_RESOLVEDURLBEGIN];
    		capture = aArchiveUrl.substring(beginStr.length(), beginStr.length() + 14);
    		uri = aArchiveUrl.substring(beginStr.length() + 15);
    	}
    	catch(Exception e) {
    		throw new PwidParseException("problems parsig capture or url");
    	}

    	PWID pwid = null;

    	try {
        	pwid = new PWID(WEBARCHIVES[archive_id][PWID_ARCHIVEID], uri, capture, PwidCoverage.PART);
    	}
    	catch(Exception e) {
    		throw new PwidParseException(e.getMessage());
    	}

    	return pwid;
    }
}
