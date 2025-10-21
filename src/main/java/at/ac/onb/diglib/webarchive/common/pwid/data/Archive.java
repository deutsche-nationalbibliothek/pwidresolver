package at.ac.onb.diglib.webarchive.common.pwid.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class Archive {
    private String label;
    private String archiveId;
    private Resolver replay; // should by ReplayGateway, but the behave the same
    private Resolver resolver;
}
