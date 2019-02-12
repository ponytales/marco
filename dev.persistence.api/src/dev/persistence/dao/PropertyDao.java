package dev.persistence.dao;

import dev.persistence.dto.PropertyDTO;

public interface PropertyDao<T extends PropertyDTO> extends CrudDao<Long,T> {
	
}
