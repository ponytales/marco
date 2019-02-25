package marco.search.index;

import java.util.HashMap;
import java.util.Map;

public class IndexRequest {
	private Map<String,Object> parameters = new HashMap<>();
	private Map<String,Object> fields = new HashMap<>();
	
	public Map<String,Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String,Object> parameters) {
		this.parameters = parameters;
	}

	public Map<String,Object> getFields () {
		return fields;
	}
	
	public void setFields (Map<String,Object> fields) {
		this.fields = fields;
	}
}
