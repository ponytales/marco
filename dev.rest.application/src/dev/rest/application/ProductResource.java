package dev.rest.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

@Component(immediate=true, service=ProductResource.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=.default)")
@Path("products")
public class ProductResource {
	
	@GET
	public String hello () {
		return "Hello from ProductResource!";
	}
	
	@GET
	@Path("{name}")
	public String hello (@PathParam("name") String name) {
		return "Hello from ProductResource to " + name + "!";
	}
}
