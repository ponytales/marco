package marco.dao.txcontrol.jpa.xa.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import marco.dto.ProductDTO;

@Entity
public class ProductEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String slug;
	
	@ManyToOne
	private CategoryEntity category;
	
	@OneToMany(mappedBy="product", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<ProductPropertyEntity> properties = new ArrayList<>();

	public Long getId () {
		return id;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public CategoryEntity getCategory () {
		return category;
	}
	
	public void setCategory (CategoryEntity category) {
		this.category = category;
	}

	public List<ProductPropertyEntity> getproperties () {
		return properties;
	}
	
	public void setProperties (List<ProductPropertyEntity> properties) {
		this.properties = properties;
	}
	
	public void addProperty (ProductPropertyEntity property) {
		properties.add(property);
	}
	
	public void removeproperty (PropertyEntity property) {
		properties.remove(property);
	}
	
	public static ProductEntity fromDTO (CategoryEntity category, ProductDTO dto) {
		ProductEntity product = new ProductEntity();
		product.id = dto.id;
		product.slug = dto.slug;
		product.category = category;
		product.properties = dto.properties.stream()
				.map(prop -> ProductPropertyEntity.fromDTO(product, prop))
				.collect(Collectors.toList());
		return product;
	}
	
	public ProductDTO toDTO () {
		ProductDTO dto = new ProductDTO();
		dto.id = id;
		dto.slug = slug;
		dto.categoryId = category == null ? null : category.getId();
		dto.properties = properties.stream()
				.map(ProductPropertyEntity::toDTO).collect(Collectors.toList());
		return dto;
	}
}
