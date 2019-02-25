package marco.search.query;

/**
 * FilterService provides methods needed for search capabilities. It uses a Request / Response pattern
 * You can hook into with several interceptors.
 * 
 * @author adrian
 *
 */
public interface FilterService {
	ResultResponse process (ResultRequest request);
	FacetResponse process (FacetRequest request);
	SuggestionResponse process (SuggestionRequest request);
	
	void addInterceptor	(ResultRequestInterceptor interceptor);
	void addInterceptor (FacetRequestInterceptor interceptor);
	void addInterceptor (SuggestionRequestInterceptor interceptor);

	void addInterceptor (ResultResponseInterceptor interceptor);
	void addInterceptor (FacetResponseInterceptor interceptor);
	void addInterceptor (SuggestionResponseInterceptor interceptor);
	
	void removeInterceptor (ResultRequestInterceptor interceptor);
	void removeInterceptor (FacetRequestInterceptor interceptor);
	void removeInterceptor (SuggestionRequestInterceptor interceptor);
	
	void removeInterceptor (ResultResponseInterceptor interceptor);
	void removeInterceptor (FacetResponseInterceptor interceptor);
	void removeInterceptor (SuggestionResponseInterceptor interceptor);
}
