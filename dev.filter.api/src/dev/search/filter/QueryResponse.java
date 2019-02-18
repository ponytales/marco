package dev.search.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryResponse extends FilterResponse<QueryRequest>{
	
	private List<Map<String,Object>> results;

	QueryResponse(QueryRequest request, Exception exception, List<Map<String,Object>> results) {
		super(request, exception);
		this.results = results == null ? new ArrayList<>() : results;
	}
	
	List<Map<String,Object>> getResults () {
		return this.results;
	}
}
