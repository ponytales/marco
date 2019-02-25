package marco.search.query;

public interface RequestInterceptor<T extends FilterRequest> {
	void intercept (T subject);
}
