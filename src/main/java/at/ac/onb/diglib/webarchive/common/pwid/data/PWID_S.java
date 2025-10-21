package at.ac.onb.diglib.webarchive.common.pwid.data;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class PWID_S {
	public static final String PREFIX = "https://w3id.org/pwid#";

	public static final Resource WebArchive = ResourceFactory.createResource(PREFIX + "WebArchive");
	public static final Property archiveId = ResourceFactory.createProperty(PREFIX + "archiveId");
	public static final Property replay = ResourceFactory.createProperty(PREFIX + "replay");
	public static final Property resolver = ResourceFactory.createProperty(PREFIX + "resolver");
	public static final Property exampleIri = ResourceFactory.createProperty(PREFIX + "exampleIri");
	public static final Property baseUrl = ResourceFactory.createProperty(PREFIX + "baseUrl");
	public static final Property pathPattern = ResourceFactory.createProperty(PREFIX + "pathPattern");
}
