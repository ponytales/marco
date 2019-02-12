package dev.search.filter;

public interface FilterService {
	QueryResponse process (QueryRequest request);
	FacetResponse process (FacetRequest request);
	SuggestionResponse process (SuggestionRequest request);

	void addInterceptor (FilterRequestInterceptor interceptor);
	void addInterceptor (QueryRequestInterceptor interceptor);
	void addInterceptor (FacetRequestInterceptor interceptor);
	void addInterceptor (SuggestionRequestInterceptor interceptor);

	void removeInterceptor (FilterRequestInterceptor interceptor);
	void removeInterceptor (QueryRequestInterceptor interceptor);
	void removeInterceptor (FacetRequestInterceptor interceptor);
	void removeInterceptor (SuggestionRequestInterceptor interceptor);
}
