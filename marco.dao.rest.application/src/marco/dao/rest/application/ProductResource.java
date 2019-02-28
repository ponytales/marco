package marco.dao.rest.application;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import marco.dao.ProductDao;
import marco.dto.ProductDTO;

@Component(immediate=true, service=ProductResource.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=.default)")
@Path("products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
	
	@Reference
	private ProductDao productDao;

	@GET
	public String select () {
		List<ProductDTO> products = productDao.select();
		return products.toString();
	}
	
	@GET
	@Path("{id:\\d+}")
	public String get(@PathParam("id") Long id) {
		return productDao.select(id).toString();
	}
	
	@POST
	@PUT
	@PATCH
	public String save (
			@FormParam("id") Long id,
			@FormParam("slug") String slug,
			@FormParam("category") Long categoryId) {
		ProductDTO dto = new ProductDTO();
		dto.id = id;
		dto.slug = slug;
		dto.categoryId = categoryId;
		productDao.save(dto);
		return dto.toString();
	}
	
	@DELETE
	@Path("{id:\\d+}")
	public String delete (@PathParam("id") Long id) {
		return Boolean.valueOf(productDao.delete(id)).toString();
	}
	
	@DELETE
	@Path("{slug}")
	public String delete (@PathParam("slug") String slug) {
		return Boolean.valueOf(productDao.delete(slug)).toString();
	}
}
