package at.ac.onb.diglib.webarchive.common.pwid;

public enum PwidCoverage {
	PART("part", "the single archived element, e.g. a pdf, a html text, an image"),
	PAGE("page", "the full context as a page, e.g. a html page with referred images");
	
    private final String value;
    private final String description;
    
    private PwidCoverage(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }
    
    public String getName() {
        return description;
    }
}
