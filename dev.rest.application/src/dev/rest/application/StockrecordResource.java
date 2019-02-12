package dev.rest.application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

@Component
@JaxrsResource
public class StockrecordResource {
	public String save () {
		return null;
	}
}
