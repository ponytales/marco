package dev.search.filter;

public interface RequestInterceptor<T extends FilterRequest> {
	void intercept (T subject);
}
