package dev.search.filter;

public interface Interceptor<T> {
	void intercept (T subject);
}
