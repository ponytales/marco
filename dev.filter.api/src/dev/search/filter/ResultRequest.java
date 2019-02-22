package dev.search.filter;

import java.util.ArrayList;
import java.util.List;

public class ResultRequest extends QueryRequest {
	private Integer page = 1;
	private Integer interval = 25;
	private List<String> sortBy = null;
	private Boolean order = false;
	private List<String> fields = new ArrayList<>();
	
	public Integer getPage() {
		return page;
	}
	
	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = (interval == null || interval < 0) ? 25 : interval;
	}

	public List<String> getSortBy() {
		if (sortBy == null) {
			sortBy = new ArrayList<>();
		}
		return sortBy;
	}

	public void setSortBy(List<String> sortBy) {
		this.sortBy = sortBy;
	}

	public Boolean getOrder() {
		if (order == null) {
			order = false;
		}
		return order;
	}

	/**
	 * Sets ordering of results. Default is false (descending).
	 * @param order {@link Boolean} - TRUE = descending FALSE = ascending
	 */
	public void setOrder(Boolean order) {
		this.order = order == null ? false : order;
	}
	
	public List<String> getFields () {
		return fields;
	}
	
	public void addField (String field) {
		fields.add(field);
	}
	
	public void removeField (String field) {
		fields.remove(field);
	}
	
	public void setFields (List<String> fields) {
		this.fields = fields;
	}
	
	public void setField (String field) {
		this.fields = new ArrayList<>();
		fields.add(field);
	}
}
