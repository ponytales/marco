package marco.search.index;

public class IndexResponse {
	private IndexRequest request;
	
	public IndexResponse (IndexRequest request) {
		this.request = request;
	}
	
	public IndexRequest getRequest () {
		return request;
	}
}
