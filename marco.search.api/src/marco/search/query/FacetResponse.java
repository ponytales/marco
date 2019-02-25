package marco.search.query;

import java.util.HashMap;
import java.util.Map;

public class FacetResponse extends FilterResponse<FacetRequest> {
	private Map<String,Map<String,Long>> facets;
	
	public FacetResponse () {
		
	}

	public FacetResponse(FacetRequest request, Exception exception) {
		super(request, exception);
		// TODO Auto-generated constructor stub
	}
	
	public FacetResponse(FacetRequest request, Exception exception, Map<String,Map<String,Long>> facets) {
		this.facets = facets == null ? new HashMap<>() : facets;
	}
	
	public Map<String, Map<String, Long>> getFacets () {
		return facets;
	}
	
	public void setFacets (Map<String, Map<String, Long>> facets) {
		this.facets = facets;
	}
	
	public void addFacet(String facet, Map<String,Long> counts ) {
		facets.put(facet, counts);
	}

}
