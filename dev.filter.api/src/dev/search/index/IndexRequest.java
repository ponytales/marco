package dev.search.index;

import java.util.HashMap;
import java.util.Map;

public class IndexRequest {
	private Map<String,Object> fields = new HashMap<>();
	
	public Map<String,Object> getFields () {
		return fields;
	}
	
	public void setFields (Map<String,Object> fields) {
		this.fields = fields;
	}
}
