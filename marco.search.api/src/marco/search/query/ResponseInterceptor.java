package marco.search.query;

public interface ResponseInterceptor<T extends FilterResponse<?>> {
	void intercept (T response);
}
