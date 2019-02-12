package dev.persistence.dao;

import java.util.List;

import dev.persistence.dto.ProductDTO;

/**
 * Product DAO interface. Each modifying method
 * should emit corresponding PRE_* and POST_* events, so that components can react/extend
 * the functionality. For example some Indexer may want to update its database, if a
 * product gets modified ({@link dev.persistence.dao.DaoEvents#PRODUCT_PRE_UPDATE} or 
 * {@link dev.persistence.dao.DaoEvents#PRODUCT_POST_UPDATE}).
 * 
 * @author adrian
 * @see {@link dev.persistence.dao.DaoEvents}
 *
 */
public interface ProductDao extends CrudDao<Long,ProductDTO> {
	
	/**
	 * Find products specified in given category
	 * @param categoryId {@link java.lang.Long} ID of existing category
	 * @return {@link List}&lt;{@link dev.persistence.dto.ProductDTO}&gt; or {@code null} if category not found
	 */
	List<ProductDTO> findByCategory (Long categoryId);
	
}
