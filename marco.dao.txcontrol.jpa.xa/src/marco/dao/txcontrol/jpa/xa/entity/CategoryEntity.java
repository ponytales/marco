package marco.dao.txcontrol.jpa.xa.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import marco.dto.CategoryDTO;

@Entity
public class CategoryEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, unique=true)
	private String slug;
	
	@Column(nullable=false)
	private String name;
	private String plural;
	
	@ManyToOne
	private CategoryEntity parent;
	
	@OneToMany(mappedBy="parent",orphanRemoval=false)
	private Set<CategoryEntity> children = new HashSet<>();
	
	@OneToMany(mappedBy="category",orphanRemoval=false)
	private Set<ProductEntity> products = new HashSet<>();
	
	public Long getId () {
		return this.id;
	}
	
	public String getSlug () {
		return slug;
	}
	
	public void setSlug (String slug) {
		this.slug = slug;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlural () {
		return plural;
	}
	
	public void setPlural (String plural) {
		this.plural = plural;
	}
	
	public CategoryEntity getParent () {
		return parent;
	}
	
	public void setParent (CategoryEntity parent) {
		this.parent = parent;
	}
	
	public Set<ProductEntity> getProducts () {
		return products;
	}
	
	public void setProducts (Set<ProductEntity> products) {
		this.products = products;
	}
	
	public void addProduct (ProductEntity product) {
		products.add(product);
	}
	
	public void removeProduct (ProductEntity product) {
		products.remove(product);
	}
	
	public CategoryDTO toDTO() {
		CategoryDTO dto = new CategoryDTO();
		dto.id = id;
		dto.parentId = parent == null ? null : parent.getId();
		dto.slug = slug;
		dto.name = name;
		dto.plural = plural;
		dto.products = products.stream().map(ProductEntity::toDTO).collect(Collectors.toSet());
		return dto;
	}
	
	public static CategoryEntity fromDTO (CategoryEntity parent, CategoryDTO dto) {
		CategoryEntity category = new CategoryEntity();
		if (dto.id != null) {
			category.id = dto.id;
		}
		
		category.parent = parent;
		category.slug = dto.slug;
		category.name = dto.name;
		category.plural = dto.plural;
		category.products = dto.products.stream()
				.map(p -> ProductEntity.fromDTO(category,p))
				.collect(Collectors.toSet());
		
		return category;
	}
}
