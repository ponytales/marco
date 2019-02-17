package dev.filter.solr.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import dev.search.filter.FacetRequest;
import dev.search.filter.FacetRequestInterceptor;
import dev.search.filter.FacetResponse;
import dev.search.filter.FilterRequestInterceptor;
import dev.search.filter.FilterService;
import dev.search.filter.QueryRequest;
import dev.search.filter.QueryRequestInterceptor;
import dev.search.filter.QueryResponse;
import dev.search.filter.SuggestionRequest;
import dev.search.filter.SuggestionRequestInterceptor;
import dev.search.filter.SuggestionResponse;

@Component
public class SolrFilterService implements FilterService {
	
	@Reference
	private SolrClient solr;
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<FilterRequestInterceptor> frInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<QueryRequestInterceptor> qrqInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<FacetRequestInterceptor> frqInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<SuggestionRequestInterceptor> srqInterceptors = new ArrayList<>();

	@Override
	public QueryResponse process(QueryRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FacetResponse process(FacetRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuggestionResponse process(SuggestionRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addInterceptor(FilterRequestInterceptor interceptor) {
		frInterceptors.add(interceptor);
	}

	@Override
	public void addInterceptor(QueryRequestInterceptor interceptor) {
		qrqInterceptors.add(interceptor);
	}

	@Override
	public void addInterceptor(FacetRequestInterceptor interceptor) {
		frqInterceptors.add(interceptor);
	}

	@Override
	public void addInterceptor(SuggestionRequestInterceptor interceptor) {
		srqInterceptors.add(interceptor);
	}

	@Override
	public void removeInterceptor(FilterRequestInterceptor interceptor) {
		frInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(QueryRequestInterceptor interceptor) {
		qrqInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(FacetRequestInterceptor interceptor) {
		frqInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(SuggestionRequestInterceptor interceptor) {
		srqInterceptors.remove(interceptor);
	}
}
