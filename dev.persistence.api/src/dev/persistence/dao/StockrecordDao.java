package dev.persistence.dao;

import java.util.List;

import dev.persistence.dto.StockrecordDTO;

public interface StockrecordDao extends CrudDao<Long, StockrecordDTO> {
	
	List<StockrecordDTO> findByProduct(Long productId);
	
}
