package dev.search.index;

public interface IndexRequestInterceptor {
	void process (IndexRequest request);
}
