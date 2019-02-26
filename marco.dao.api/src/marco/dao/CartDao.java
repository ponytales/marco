package marco.dao;

import java.util.List;

import marco.dto.CartDTO;

public interface CartDao {
	CartDTO select (Long id);
	CartDTO select (String session);
	List<CartDTO> select ();
	void save (CartDTO dto);
	boolean delete (Long id);
	
}
