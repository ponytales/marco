package marco.search.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultResponse extends FilterResponse<ResultRequest>{
	
	private List<Map<String,Object>> results;
	
	public ResultResponse () {
		results = new ArrayList<>();
	}
	
	public ResultResponse (ResultRequest request, Exception e) {
		super(request,e);
		results = new ArrayList<>();
	}

	public ResultResponse(ResultRequest request, Exception exception, List<Map<String,Object>> results) {
		super(request, exception);
		this.results = results == null ? new ArrayList<>() : results;
	}
	
	public List<Map<String,Object>> getResults () {
		return this.results;
	}
}
