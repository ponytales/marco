package dev.rest.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.persistence.dao.CategoryDao;
import dev.persistence.dto.CategoryDTO;

@Component(service=CategoryResource.class, property = {
		"service.scope=singleton"
})
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=.default)")
@Path("categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private DiskFileItemFactory factory = new DiskFileItemFactory();
	private ServletFileUpload upload = new ServletFileUpload(factory);
	
	@Reference
	private CategoryDao categoryDao;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String select () {
		List<CategoryDTO> categories = categoryDao.select();
		return gson.toJson(categories);
	}
	
	@GET
	@Path("{id:\\d+}")
	public String select (Long id) {
		return gson.toJson(categoryDao.select(id));
	}
	
	@GET
	@Path("{slug}")
	public String select (String slug) {
		return gson.toJson(categoryDao.select(slug));
	}
	
	@POST
	@PUT
	@PATCH
	public String save (
			@FormParam("id") Long id,
			@FormParam("slug") String slug,
			@FormParam("name") String name,
			@FormParam("plural") String plural,
			@FormParam("parent") Long parentId ) {
		CategoryDTO c = new CategoryDTO();
		c.id = id;
		c.slug = slug;
		c.name = name;
		c.plural = plural;
		c.parentId = parentId;
		categoryDao.save(c);
		return gson.toJson(c);
	}
	
	@POST
	@Path("upload")
	public String upload (@Context HttpServletRequest request) {
		try {
			FileItemIterator it = upload.getItemIterator(request);
			while (it.hasNext()) {
				FileItemStream fis = it.next();
				if (!fis.isFormField()) {
					InputStream in = fis.openStream();
					InputStreamReader reader = new InputStreamReader(in);
					gson.fromJson(reader, Map.class);
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonMsg("not implemented yet");
	}
	
	@DELETE
	@Path("{id:\\d+}")
	public String delete (@PathParam("id") Long id) {
		return Boolean.valueOf(categoryDao.delete(id)).toString();
	}
	
	@DELETE
	@Path("{slug}")
	public String delete (@PathParam("slug") String slug) {
		return Boolean.valueOf(categoryDao.delete(slug)).toString();
	}
	
	private String jsonMsg(String msg) {
		return "{ \"msg\": \"" + msg + "\" }";
	}
}