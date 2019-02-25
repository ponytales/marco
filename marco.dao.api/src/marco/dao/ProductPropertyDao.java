package marco.dao;

import java.util.List;

import marco.dto.ProductPropertyDTO;
import marco.dto.PropertyDTO;

public interface ProductPropertyDao extends PropertyDao<ProductPropertyDTO> {
	
	List<PropertyDTO> findByProduct(Long productId);

}
