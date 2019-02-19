package dev.filter.solr.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import dev.search.filter.FacetRequest;
import dev.search.filter.FacetRequestInterceptor;
import dev.search.filter.FacetResponse;
import dev.search.filter.FacetResponseInterceptor;
import dev.search.filter.FilterService;
import dev.search.filter.ResultRequest;
import dev.search.filter.ResultRequestInterceptor;
import dev.search.filter.ResultResponse;
import dev.search.filter.ResultResponseInterceptor;
import dev.search.filter.SuggestionRequest;
import dev.search.filter.SuggestionRequestInterceptor;
import dev.search.filter.SuggestionResponse;
import dev.search.filter.SuggestionResponseInterceptor;

@Component
public class SolrFilterService implements FilterService {
	
	@Reference
	private SolrClient solr;
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<ResultRequestInterceptor> rrqInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<ResultResponseInterceptor> rrsInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<FacetRequestInterceptor> frqInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<SuggestionRequestInterceptor> srqInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<FacetResponseInterceptor> frsInterceptors = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,bind="addInterceptor",unbind="removeInterceptor")
	private volatile List<SuggestionResponseInterceptor> srsInterceptors;

	@Override
	public ResultResponse process(ResultRequest request) {
		try {
			rrqInterceptors.forEach(i -> {
				i.intercept(request);
			});
		} catch (Exception e) {
			// some dude throw an exception
			// just to be safe - abort
			return new ResultResponse(request,e);
		}
		
		SolrQuery query = new SolrQuery();
		query.setQuery(calculateQ(request.getQ()));
		
		// apply fields
		if (request.getFields().isEmpty()) {
			query.addField("id");
		} else {
			request.getFields().forEach(f -> query.addField(f));
		}
		
		// apply sorting
		if (!request.getSortBy().isEmpty()) {
			request.getSortBy().forEach(sf -> {
				query.addSort(sf, request.getOrder() ? SolrQuery.ORDER.asc :SolrQuery.ORDER.desc);
			});
		}
		
		// apply pagination
		Integer page = request.getPage();
		Integer interval = request.getInterval();
		query.setStart((page-1)*interval);
		query.setRows(interval);
		
		ResultResponse response = null;
		try {
			org.apache.solr.client.solrj.response.QueryResponse resp = 
					new org.apache.solr.client.solrj.request.QueryRequest(query).process(solr);
			
			List<Map<String,Object>> results = resp.getResults().stream()
					.map(doc -> doc.getFieldValueMap())
					.collect(Collectors.toList());
			response = new ResultResponse(request, null, results);
		} catch (Exception e) {
			return new ResultResponse(request,e);
		}
		
		try {
			final ResultResponse f1 = response;
			rrsInterceptors.forEach(i -> {
				i.intercept(f1);
			});
			return f1;
		} catch (Exception e) {
			return new ResultResponse(request,e);
		}
	}
	
	@Override
	public FacetResponse process(FacetRequest request) {
		try {
			frqInterceptors.forEach(i -> {
				i.intercept(request);
			});
		} catch (Exception e) {
			// some dude throw an exception
			// just to be safe - abort
			return new FacetResponse(request,e);
		}
		
		SolrQuery query = new SolrQuery();
		query.setRows(0);
		
		if (request.getFacets().isEmpty()) {
			// facet query with no fields specified
			// just makes no sense...
			return new FacetResponse(request, new Exception("no facet specified"));
		} else if (request.getQ().isEmpty()) {
			query.setQuery("*:*");
		} else {
			query.setQuery(calculateQ(request.getQ()));
		}
		
		request.getFacets().forEach(f -> query.addFacetField(f));
		
		FacetResponse response = null;
		try {
			org.apache.solr.client.solrj.response.QueryResponse resp = 
					new org.apache.solr.client.solrj.request.QueryRequest(query).process(solr);
			
			Map<String,Map<String,Long>> facets =resp.getFacetFields().stream()
					.collect(Collectors.toMap(
							ff -> ff.getName(), 
							ff -> ff.getValues().stream()
								.collect(Collectors.toMap(
										c -> c.getName(),
										c -> c.getCount()))));
			response = new FacetResponse(request, null, facets);
		} catch (Exception e) {
			return new FacetResponse(request,e);
		}
		
		try {
			final FacetResponse f1 = response;
			frsInterceptors.forEach(i -> {
				i.intercept(f1);
			});
			return f1;
		} catch (Exception e) {
			return new FacetResponse(request,e);
		}
	}

	@Override
	public SuggestionResponse process(SuggestionRequest request) {
		// TODO: not implemented yet
		return null;
	}
	
	private String calculateQ (Map<String,String> q) {
		if (q.isEmpty()) {
			return null;
		}
		Iterator<String> it = q.values().iterator();
		String out = "(" + it.next() + ")";
		while (it.hasNext()) {
			out += "AND (" + it.next() + ")";
		}
		return out;
	}

	@Override
	public void addInterceptor(ResultRequestInterceptor interceptor) {
		rrqInterceptors.add(interceptor);
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
	public void addInterceptor(ResultResponseInterceptor interceptor) {
		rrsInterceptors.add(interceptor);
	}

	@Override
	public void addInterceptor(FacetResponseInterceptor interceptor) {
		frsInterceptors.add(interceptor);
	}

	@Override
	public void addInterceptor(SuggestionResponseInterceptor interceptor) {
		srsInterceptors.add(interceptor);
	}

	@Override
	public void removeInterceptor(ResultRequestInterceptor interceptor) {
		rrqInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(FacetRequestInterceptor interceptor) {
		frqInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(SuggestionRequestInterceptor interceptor) {
		srqInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(ResultResponseInterceptor interceptor) {
		rrsInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(FacetResponseInterceptor interceptor) {
		frsInterceptors.remove(interceptor);
	}

	@Override
	public void removeInterceptor(SuggestionResponseInterceptor interceptor) {
		srsInterceptors.remove(interceptor);
	}
}
