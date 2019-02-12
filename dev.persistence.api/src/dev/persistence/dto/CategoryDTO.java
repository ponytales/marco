package dev.persistence.dto;

import java.util.HashSet;
import java.util.Set;

import org.osgi.dto.DTO;

public class CategoryDTO extends DTO {
	public Long id;
	public String slug;
	public String name;
	public String plural;
	public Set<ProductDTO> products = new HashSet<>();
	public Long parentId;
}
