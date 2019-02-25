package marco.search.query;

public class FilterResponse<T extends FilterRequest> {
	private T request;
	private Exception exception;
	
	FilterResponse () {
		
	}
	
	FilterResponse(T request, Exception exception) {
		this.request = request;
		this.exception = exception;
	}
	
	public T getRequest() {
		return request;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public void setException (Exception exception) {
		this.exception = exception;
	}
}
