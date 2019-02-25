package marco.dao.txcontrol.jpa.xa.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import marco.dto.StockrecordDTO;

@Entity
public class StockrecordEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String slug;
	
	@ManyToOne(optional = false)
	private ProductEntity product;
	
	@OneToMany(mappedBy="stockrecord")
	private List<StockrecordPropertyEntity> properties = new ArrayList<>();
	
	@Column(nullable = false)
	private Float price;
	
	public Long getId() {
		return id;
	}
	
	public String getSlug () {
		return slug;
	}
	
	public void setSlug (String slug) {
		this.slug = slug;
	}
	
	public ProductEntity getProduct () {
		return product;
	}
	
	public void setProduct (ProductEntity product) {
		this.product = product;
	}
	
	public List<StockrecordPropertyEntity> getProperties () {
		return properties;
	}
	
	public void setProperties (List<StockrecordPropertyEntity> properties) {
		this.properties = properties;
	}
	
	public void addProperty (StockrecordPropertyEntity property) {
		this.properties.add(property);
	}
	
	public void removeProperty (StockrecordPropertyEntity property) {
		properties.remove(property);
	}
	
	public Float getPrice () {
		return price;
	}
	
	public void setPrice (Float price) {
		this.price = price;
	}
	
	public StockrecordDTO toDTO () {
		StockrecordDTO dto = new StockrecordDTO();
		dto.id = id;
		dto.slug = slug;
		dto.productId = product == null ? null : product.getId();
		dto.properties = properties.stream()
				.map(StockrecordPropertyEntity::toDTO)
				.collect(Collectors.toList());
		dto.price = price;
		return dto;
	}
	
	public static StockrecordEntity fromDTO (ProductEntity product, StockrecordDTO dto) {
		StockrecordEntity stock = new StockrecordEntity();
		stock.id = dto.id;
		stock.slug = dto.slug;
		stock.product = product;
		stock.price = dto.price;
		stock.properties = dto.properties.stream()
				.map(prop -> StockrecordPropertyEntity.fromDTO(stock, prop))
				.collect(Collectors.toList());
		return stock;
	}
}
