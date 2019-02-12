package dev.persistence.dao;

import java.util.List;

import org.osgi.dto.DTO;

public interface CrudDao<PK,T extends DTO> {
	T select(PK pk);
	T select(String slug);
	List<T> select();
	void save(T dto);
	boolean delete(PK pk);
	boolean delete(String slug);
}
