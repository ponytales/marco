package dev.search.filter;

import java.util.ArrayList;
import java.util.List;

public class SuggestionResponse extends FilterResponse<SuggestionRequest> {
	private List<String> suggestions;
	
	public SuggestionResponse(SuggestionRequest request, Exception exception) {
		super(request, exception);
		suggestions = new ArrayList<>();
	}
	
	public SuggestionResponse(SuggestionRequest request, Exception exception, List<String> suggestions) {
		super(request, exception);
		this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
	}
	
	public void addSuggestion (String suggestion) {
		this.suggestions.add(suggestion);
	}
	
	public void setSuggestions (List<String> suggestions) {
		this.suggestions = suggestions;
	}
}
