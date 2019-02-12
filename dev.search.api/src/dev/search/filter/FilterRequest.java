package dev.search.filter;

import java.util.HashMap;
import java.util.Map;

public class FilterRequest {
	private Map<String,Object> parameters = new HashMap<>();
	private Map<String,String> q = new HashMap<>();
	
	public Map<String,Object> getParameters () {
		return parameters;
	}
	
	public void setParameters (Map<String,Object> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(String parameter, Object value) {
		parameters.put(parameter, value);
	}
	
	public void removeParameter(String parameter) {
		this.parameters.remove(parameter);
	}

	public Map<String,String> getQ() {
		return q;
	}

	public void setQ(Map<String,String> q) {
		this.q = q;
	}
	
	public void addQ(String q, String value) {
		this.q.put(q, value);
	}
	
	public void removeQ(String name) {
		this.q.remove(name);
	}
}
