package marco.search.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FacetRequest extends QueryRequest {
	private Set<String> facets = new HashSet<>();

	public Set<String> getFacets() {
		return facets;
	}

	public void setFacets(Collection<String> facets) {
		this.facets = new HashSet<>(facets);
	}
	
	public void addFacet(String facet) {
		facets.add(facet);
	}
	
	public void removeFacet(String facet) {
		facets.remove(facet);
	}
	
}
