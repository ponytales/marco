package marco.dao.rest.application;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import marco.dao.CartDao;
import marco.dto.CartDTO;

@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=.default)")
@Component(immediate=true)
public class CartResource {
	
	@Reference
	private CartDao cartDao;
	
	@GET
	@Path("{id:\\d+}")
	public String select (@PathParam("id") Long id) {
		CartDTO dto = cartDao.select(id);
		return dto.toString();
	}
	
	@POST
	public String create (@Context HttpServletRequest request) {
		return "not implemented yet";
	}
}
