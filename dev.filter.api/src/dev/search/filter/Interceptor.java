package dev.search.filter;

public interface Interceptor<T extends FilterRequest> {
	void intercept (T subject);
}
