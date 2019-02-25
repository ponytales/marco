package marco.dao;

import java.util.List;

import marco.dto.StockrecordDTO;

public interface StockrecordDao extends CrudDao<Long, StockrecordDTO> {
	
	List<StockrecordDTO> findByProduct(Long productId);
	
}
