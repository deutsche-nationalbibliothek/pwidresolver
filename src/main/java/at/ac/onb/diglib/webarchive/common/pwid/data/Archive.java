package at.ac.onb.diglib.webarchive.common.pwid.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Archive {
    public String label;
    public String archiveId;
    public Resolver replay; // should be ReplayGateway, but it behaves the same
    public Resolver resolver;
}
