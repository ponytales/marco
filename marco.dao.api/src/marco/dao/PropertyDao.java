package marco.dao;

import marco.dto.PropertyDTO;

public interface PropertyDao<T extends PropertyDTO> extends CrudDao<Long,T> {
	
}
