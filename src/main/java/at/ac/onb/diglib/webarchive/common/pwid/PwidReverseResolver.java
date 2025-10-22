package at.ac.onb.diglib.webarchive.common.pwid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.ac.onb.diglib.webarchive.common.pwid.data.Resolver;

import java.net.URI;
import java.net.URISyntaxException;

public class PwidReverseResolver extends PwidResolver {
	protected static final Log log = LogFactory.getLog(PwidReverseResolver.class);

	public PwidReverseResolver(PwidRegistry registry, Resolver defaultResolver) {
		super(registry, defaultResolver);
	}

	public PWID resolve(String aArchiveUrl) throws PwidParseException {
		if (aArchiveUrl == null) {
    		throw new PwidParseException("archive url is null");
    	}

		try {
			URI playbackUri = new URI(aArchiveUrl);
			String authority = playbackUri.getAuthority();
			String path = playbackUri.getPath();
			log.info(authority);
			log.info(path);
			for (String archiveId : registry.getArchiveIds()) {
				Resolver replay = registry.getReplay(archiveId);
				if (replay != null) {
					URI replayBaseUri = new URI(replay.getBaseUrl());
					if(replayBaseUri.getAuthority().equals(authority) && path.startsWith(replayBaseUri.getPath())) {
						PWID pwid = parseArchiveUrl(archiveId, replayBaseUri, playbackUri);
						return super.resolve(pwid);
					}
				}
			}
		} catch(URISyntaxException e) {
			throw new PwidParseException("problems parsig capture or url");
		}
		throw new PwidParseException("archive not identified");
	}

    private PWID parseArchiveUrl(String archiveId, URI replayBaseUri, URI playbackUri) throws PwidParseException {
    	String capture = null;
    	String archivedUri = null;
		String path = playbackUri.getPath();

		int base_length = replayBaseUri.getPath().length();
		try {
			capture = path.substring(base_length, base_length + 14);
			archivedUri = path.substring(base_length + 15);
		} catch(Exception e) {
			throw new PwidParseException("problems parsig capture or url");
		}

    	try {
        	return new PWID(archiveId, archivedUri, capture, PwidCoverage.PART);
    	} catch(Exception e) {
    		throw new PwidParseException(e.getMessage());
    	}
    }
}
