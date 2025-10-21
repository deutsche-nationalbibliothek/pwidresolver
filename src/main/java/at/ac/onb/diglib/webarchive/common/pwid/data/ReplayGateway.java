package at.ac.onb.diglib.webarchive.common.pwid.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ReplayGateway {
    private String exampleIri;
    private String baseUrl;
    private String pathPattern;
}
