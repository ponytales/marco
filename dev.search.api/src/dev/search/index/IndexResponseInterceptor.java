package dev.search.index;

public interface IndexResponseInterceptor {
	void process (IndexResponse response);
}
