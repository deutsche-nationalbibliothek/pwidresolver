package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shared.JenaException;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.core.io.ClassPathResource;

import at.ac.onb.diglib.webarchive.common.pwid.data.Archive;
import at.ac.onb.diglib.webarchive.common.pwid.data.PWID_S;
import at.ac.onb.diglib.webarchive.common.pwid.data.Resolver;

import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class PwidRegistry {
	protected static final Log log = LogFactory.getLog(PwidRegistry.class);

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
			for (; archives.hasNext();) {
				Resource archiveRes = archives.next();
				try {
					log.debug("Registering " + archiveRes);
					Archive archive = readArchive(archiveRes);
					Webarchives.put(archive.getArchiveId(), archive);
					log.debug("Registered " + archive.getArchiveId());
					log.debug(archive);
				} catch (JenaException e) {
					log.info("Could not register " + archiveRes);
					log.debug(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Archive readArchive(Resource archive) {
		String archiveId = archive.getRequiredProperty(PWID_S.archiveId).getLiteral().getString();
		String label = null;
		Resolver replay = null;
		Resolver resolver = null;
		Statement labelStatement = archive.getProperty(RDFS.label);
		Statement replayStatement = archive.getProperty(PWID_S.replay);
		Statement resolverStatement = archive.getProperty(PWID_S.resolver);
		if (labelStatement != null)
			label = labelStatement.getLiteral().getString();
		if (replayStatement != null)
			replay = readResolver(replayStatement.getResource());
		if (resolverStatement != null)
			resolver = readResolver(resolverStatement.getResource());
		return new Archive(label, archiveId, replay, resolver);
	}

	private Resolver readResolver(Resource resolver) {
		String baseUrl = resolver.getRequiredProperty(PWID_S.baseUrl).getLiteral().getString();
		String pathPattern = resolver.getRequiredProperty(PWID_S.pathPattern).getLiteral().getString();
		String exampleIri = null;
		Statement exampleIriStatement = resolver.getProperty(PWID_S.exampleIri);
		if (exampleIriStatement != null)
			exampleIri = exampleIriStatement.getResource().getURI();
		return new Resolver(exampleIri, baseUrl, pathPattern);
	}

	public boolean isArchiveSupported(String archive_id) {
		return Webarchives.get(archive_id.toLowerCase()) != null;
	}

	public Resolver getReplay(String archive_id) {
		if (!isArchiveSupported(archive_id))
			return null;
		Archive archive = Webarchives.get(archive_id.toLowerCase());
		return archive.getReplay();
	}

	/**
	 * Get the registered archive. If it is not registered return null.
	 *
	 * @param archive_id
	 * @return the registered Archive object
	 */
	public Archive getArchive(String archive_id) {
		if (!isArchiveSupported(archive_id))
			return null;
		return Webarchives.get(archive_id.toLowerCase());
	}

	public Set<String> getArchiveIds() {
		return Webarchives.keySet();
	}

}
