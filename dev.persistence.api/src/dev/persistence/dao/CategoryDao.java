package dev.persistence.dao;

import java.util.List;

import dev.persistence.dto.CategoryDTO;

public interface CategoryDao extends CrudDao<Long,CategoryDTO> {
	
	/**
	 * Get category by given id. If category cant be found or an error occured
	 * returns {@code null}.
	 * 
	 * @param id {@link Long} primary key
	 * @return {@link CategoryDTO} or {@code null} if not existent
	 */
	CategoryDTO select (Long id);
	
	/**
	 * Get category by given unique slug. If category cant be found or an error occured
	 * returns {@code null}.
	 * 
	 * @param id {@link Long} primary key
	 * @return {@link CategoryDTO} or {@code null} if not existent
	 */
	CategoryDTO select (String slug);
	
	/**
	 * Get all categories managed by this implementation.
	 * 
	 * @return {@link List}&lt;{@link CategoryDTO}&gt;
	 */
	List<CategoryDTO> select ();
	
	/**
	 * Create
	 * 
	 * @return {@link List}&lt;{@link CategoryDTO}&gt;
	 */
	void save (CategoryDTO category);
	boolean delete (Long id);
	boolean delete (String slug);
}
