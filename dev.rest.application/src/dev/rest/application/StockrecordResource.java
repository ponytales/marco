package dev.rest.application;

import javax.ws.rs.Consumes;
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
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.persistence.dao.StockrecordDao;
import dev.persistence.dto.StockrecordDTO;

@Component(service=StockrecordResource.class)
@JaxrsResource
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("stockrecords")
public class StockrecordResource {
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Reference
	private StockrecordDao stockrecordDao;
	
	/**
	 * Select all stockrecords.
	 * @return
	 */
	@GET
	public String select () {
		return gson.toJson(stockrecordDao.select());
	}
	
	/**
	 * Select stockrecord by id.
	 * @return
	 */
	@GET
	@Path("{id:\\d+}")
	public String select(@PathParam("id") Long id) {
		StockrecordDTO dto = stockrecordDao.select(id);
		return gson.toJson(dto);
	}
	
	/**
	 * Select stockrecord by slug.
	 * @return
	 */
	@GET
	@Path("{slug}")
	public String select(@PathParam("slug") String slug) {
		StockrecordDTO dto = stockrecordDao.select(slug);
		return gson.toJson(dto);
	}
	
	/**
	 * Create a new stockrecord
	 * @return
	 */
	@POST
	public String create (
			@FormParam("slug") String slug,
			@FormParam("product") Long productId,
			@FormParam("price") Float price) {
		StockrecordDTO dto = new StockrecordDTO();
		dto.slug = slug;
		dto.productId = productId;
		dto.price = price;
		
		stockrecordDao.save(dto);
		
		return gson.toJson(dto);
	}
	
	@PUT
	@PATCH
	@Path("{id:\\d+}")
	public String update (
			@FormParam("id") Long id,
			@FormParam("slug") String slug,
			@FormParam("product") Long productId,
			@FormParam("price") Float price) {
		StockrecordDTO dto = stockrecordDao.select(id);
		if (dto == null) {
			return null;
		}
		if (slug != null) {
			dto.slug = slug;
		}
		if ( productId != null) {
			dto.productId = productId;
		}
		if (price != null) {
			dto.price = price;
		}
		return gson.toJson(dto);
	}
	
	@DELETE
	@Path("{id:\\d+}")
	public String delete(@PathParam("id") Long id) {
		return gson.toJson(stockrecordDao.delete(id));
	}
	
	@DELETE
	@Path("{slug}")
	public String delete(@PathParam("id") String slug) {
		return gson.toJson(stockrecordDao.delete(slug));
	}
}
