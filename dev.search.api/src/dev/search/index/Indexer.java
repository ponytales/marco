package dev.search.index;

public interface Indexer {
	IndexResponse index(IndexRequest request);
	
	void addInterceptor (IndexRequestInterceptor interceptor);
	void addInterceptor (IndexResponseInterceptor interceptor);
}
