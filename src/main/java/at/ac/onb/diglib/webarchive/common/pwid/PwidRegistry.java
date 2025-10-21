package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.core.io.ClassPathResource;

import at.ac.onb.diglib.webarchive.common.pwid.data.Archive;
import at.ac.onb.diglib.webarchive.common.pwid.data.PWID_S;
import at.ac.onb.diglib.webarchive.common.pwid.data.ReplayGateway;
import at.ac.onb.diglib.webarchive.common.pwid.data.Resolver;

import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class PwidRegistry {
	protected static final Log log = LogFactory.getLog(PwidRegistry.class);

    public static final String PWID_ARCHIVEID_WEBARCHIV_ONB_AC_AT = "webarchiv.onb.ac.at";
    public static final String PWID_ARCHIVEID_WEBARCHIV_DNB_DE = "webarchiv.dnb.de";
    public static final String PWID_ARCHIVEID_ARCHIVE_ORG = "archive.org";
    public static final String PWID_ARQUIVO_PT = "arquivo.pt";
    public static final String PWID_VEFSAFN_IS = "vefsafn.is";

	Hashtable<String, String> Webarchives_strings;
	Hashtable<String, Archive> Webarchives;


    public PwidRegistry() {
		init();
    }

    private void init() {
		Webarchives = new Hashtable<>();
		try {
			Model model = ModelFactory.createDefaultModel();
			InputStream registryFile = new ClassPathResource("public/pwid_registry.ttl").getInputStream();
			model.read(registryFile, null, "TURTLE");
			ResIterator archives = model.listResourcesWithProperty(RDF.type, PWID_S.WebArchive);
			for (; archives.hasNext(); ){
				Resource archive = archives.next();
				String archiveId = archive.getRequiredProperty(PWID_S.archiveId).getLiteral().getString();
				String label = archive.getRequiredProperty(RDFS.label).getLiteral().getString();
				Statement replay = archive.getRequiredProperty(PWID_S.replay);
				Statement resolver = archive.getRequiredProperty(PWID_S.resolver);
				Webarchives.put(archiveId, new Archive(label, archiveId, new ReplayGateway(), new Resolver()));
			}
			Webarchives_strings = new Hashtable<>();
			Webarchives_strings.put(PWID_ARCHIVEID_WEBARCHIV_ONB_AC_AT, "https://webarchiv.onb.ac.at/web/");
			Webarchives_strings.put(PWID_ARCHIVEID_WEBARCHIV_DNB_DE, "https://webarchiv.dnb.de/playback/");
			Webarchives_strings.put(PWID_ARCHIVEID_ARCHIVE_ORG, "https://web.archive.org/web/");
			Webarchives_strings.put(PWID_ARQUIVO_PT, "https://arquivo.pt/wayback/");
			Webarchives_strings.put(PWID_VEFSAFN_IS, "https://vefsafn.is/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public boolean isArchiveSupported(String archive_id) {
		return Webarchives_strings.get(archive_id.toLowerCase()) != null;
    }

	public String getReplayBaseUrl(String archive_id) {
		return Webarchives_strings.get(archive_id.toLowerCase());
	}

	public Set<String> getArchiveIds() {
		return Webarchives_strings.keySet();
	}

}
