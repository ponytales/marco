package dev.search.filter;

import java.util.List;

public class QueryRequest extends FilterRequest {
	private Integer page = 1;
	private Integer interval = 25;
	private List<String> sortBy = null;
	private Boolean order = null;
	
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
		return sortBy;
	}

	public void setSortBy(List<String> sortBy) {
		this.sortBy = sortBy;
	}

	public Boolean getOrder() {
		return order;
	}

	public void setOrder(Boolean order) {
		this.order = order;
	}
	
}
