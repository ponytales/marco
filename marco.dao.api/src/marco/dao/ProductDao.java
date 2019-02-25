package marco.dao;

import java.util.List;

import marco.dto.ProductDTO;

/**
 * Product DAO interface. Each modifying method
 * should emit corresponding PRE_* and POST_* events, so that components can react/extend
 * the functionality. For example some Indexer may want to update its database, if a
 * product gets modified ({@link marco.dao.DaoEvents#PRODUCT_PRE_UPDATE} or 
 * {@link marco.dao.DaoEvents#PRODUCT_POST_UPDATE}).
 * 
 * @author adrian
 * @see {@link marco.dao.DaoEvents}
 *
 */
public interface ProductDao extends CrudDao<Long,ProductDTO> {
	
	/**
	 * Find products specified in given category
	 * @param categoryId {@link java.lang.Long} ID of existing category
	 * @return {@link List}&lt;{@link marco.dto.ProductDTO}&gt; or {@code null} if category not found
	 */
	List<ProductDTO> findByCategory (Long categoryId);
	
}
