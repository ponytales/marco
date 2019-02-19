package dev.search.filter;

public interface ResponseInterceptor<T extends FilterResponse<?>> {
	void intercept (T response);
}
