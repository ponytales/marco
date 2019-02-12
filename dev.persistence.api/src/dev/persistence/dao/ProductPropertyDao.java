package dev.persistence.dao;

import java.util.List;

import dev.persistence.dto.ProductPropertyDTO;
import dev.persistence.dto.PropertyDTO;

public interface ProductPropertyDao extends PropertyDao<ProductPropertyDTO> {
	
	List<PropertyDTO> findByProduct(Long productId);

}
