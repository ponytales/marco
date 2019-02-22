package dev.rest.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import dev.persistence.dao.StockrecordDao;
import dev.persistence.dto.StockrecordDTO;
import dev.search.filter.FilterService;
import dev.search.filter.ResultRequest;
import dev.search.filter.ResultResponse;

@Component(service=FilterResource.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=.default)")
@Path("products/filter")
public class FilterResource {
	
	@Reference
	private FilterService filter;
	
	@Reference
	private StockrecordDao stockrecordDao;
	
	private Map<Integer,Map<String,List<String>>> paramCache = new HashMap<>();
	
	@GET
	public String create (@Context UriInfo uri) {
		if (uri.getQueryParameters().isEmpty()) {
			return jsonMsg("no parameters specified");
		}
		
		Map<String,List<String>> params = new TreeMap<>();
		uri.getQueryParameters().forEach((k,values) -> {
			if (values.isEmpty()) {
				return;
			}
			params.put(k, values);
		});
		
		Integer hashcode = params.toString().hashCode();
		if (!paramCache.containsKey(hashcode)) {
			paramCache.put(hashcode, params);
		}
		
		return hashcode.toString();
	}
	
	@GET
	@Path("{id:-?\\d+}")
	public String results (@PathParam("id") Integer hashcode) {
		if (!paramCache.containsKey(hashcode)) {
			return jsonMsg("filter query with id " + hashcode + " not found");
		}
		
		ResultRequest request = new ResultRequest();
		request.setField("id");
		request.setParameters(new HashMap<>(paramCache.get(hashcode)));

		ResultResponse response = filter.process(request);
		List<StockrecordDTO> dtos = response.getResults().stream().map(doc ->
			stockrecordDao.select((Long) doc.get("id"))).collect(Collectors.toList());
		return jsonMsg(dtos.toString());
	}
	
	private String jsonMsg (String msg) {
		return "{ \"msg\": \"" + msg +"\" }";
	}
}
