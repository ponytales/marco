package marco.dao;

import java.util.List;

import marco.dto.PositionDTO;

public interface PositionDao {
	PositionDTO	select (Long id);
	List<PositionDTO> select ();
	void save (PositionDTO dto);
	boolean delete (Long positionId);
	List<PositionDTO> findByCart(Long cartId);
}
