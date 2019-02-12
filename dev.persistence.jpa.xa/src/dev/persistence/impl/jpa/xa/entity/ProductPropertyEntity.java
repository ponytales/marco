package dev.persistence.impl.jpa.xa.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import dev.persistence.dto.ProductPropertyDTO;

@Entity
public class ProductPropertyEntity extends PropertyEntity {

	@ManyToOne
	private ProductEntity product;
	
	public ProductEntity getProduct () {
		return product;
	}
	
	public void setProduct (ProductEntity product) {
		this.product = product;
	}
	
	public static ProductPropertyEntity fromDTO (ProductEntity product, ProductPropertyDTO dto) {
		ProductPropertyEntity prop = new ProductPropertyEntity();
		
		populateEntity(prop, dto);
		
		prop.product = product;
		
		return prop;
	}
	
	public ProductPropertyDTO toDTO () {
		ProductPropertyDTO dto = new ProductPropertyDTO();
		
		populateDTO(dto);
		
		dto.productId = product == null ? null : product.getId();
		
		return dto;
	}
}
