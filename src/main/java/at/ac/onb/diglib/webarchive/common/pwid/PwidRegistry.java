package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import java.util.Hashtable;

public class PwidRegistry {
	protected static final Log log = LogFactory.getLog(PwidRegistry.class);

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

	Hashtable<String, String> Webarchives;

    public PwidRegistry() {
		init();
    }

    private void init() {
		Webarchives = new Hashtable<>();
		Webarchives.put(PWID_ARCHIVEID_WEBARCHIV_ONB_AC_AT, "https://webarchiv.onb.ac.at/web/");
		Webarchives.put(PWID_ARCHIVEID_WEBARCHIV_DNB_DE, "https://webarchiv.dnb.de/playback/");
		Webarchives.put(PWID_ARCHIVEID_ARCHIVE_ORG, "https://web.archive.org/web/");
		Webarchives.put(PWID_ARQUIVO_PT, "https://arquivo.pt/wayback/");
		Webarchives.put(PWID_VEFSAFN_IS, "https://vefsafn.is/");
    }

    public boolean isArchiveSupported(String archive_id) {
		return Webarchives.get(archive_id.toLowerCase()) != null;
    }

	public String getReplayBaseUrl(String archive_id) {
		return Webarchives.get(archive_id.toLowerCase());
	}

	public Set<String> getArchiveIds() {
		return Webarchives.keySet();
	}

}
