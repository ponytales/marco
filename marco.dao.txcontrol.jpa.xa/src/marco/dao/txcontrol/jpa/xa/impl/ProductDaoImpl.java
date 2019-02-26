package marco.dao.txcontrol.jpa.xa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import marco.dao.DaoEvents;
import marco.dao.DaoException;
import marco.dao.ProductDao;
import marco.dao.txcontrol.jpa.xa.entity.CategoryEntity;
import marco.dao.txcontrol.jpa.xa.entity.ProductEntity;
import marco.dto.ProductDTO;

@Component(service = ProductDao.class, scope = ServiceScope.SINGLETON)
public class ProductDaoImpl implements ProductDao {
	
	private static final Logger log = LoggerFactory.getLogger(ProductDaoImpl.class);
	
	@Reference
	private EventAdmin eventAdmin;
	
	@Reference
	private TransactionControl txControl;
	
	@Reference(target="(osgi.unit.name=dev.persistence.jpa.xa-pu)")
	private JPAEntityManagerProvider provider;
	
	private EntityManager em;
	
	@Activate
	private void activate () {
		em = provider.getResource(txControl);
	}

	@Override
	public ProductDTO select(Long id) {
		try {
			return txControl.notSupported(() -> {
				try {
					return em.find(ProductEntity.class, id).toDTO();
				} catch (EntityNotFoundException e) {
					return null;
				}
			});
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public ProductDTO select(String slug) {
		try {
			return txControl.notSupported(() -> {
				try {
					CriteriaBuilder builder = em.getCriteriaBuilder();
					CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
					Root<ProductEntity> from = query.from(ProductEntity.class);
					query.where(builder.equal(from.get("slug"), slug));
					return em.createQuery(query).getSingleResult().toDTO();
				} catch (NoResultException nre) {
					return null;
				}
			});
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public List<ProductDTO> select() {
		try {
			return txControl.notSupported(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
				query.from(ProductEntity.class);
				return em.createQuery(query).getResultList().stream()
						.map(ProductEntity::toDTO)
						.collect(Collectors.toList());
			});
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public List<ProductDTO> findByCategory(Long categoryId) {
		return txControl.notSupported(() -> {
			try {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
				Root<ProductEntity> from = query.from(ProductEntity.class);
				query.where(builder.equal(from.get("category"), categoryId));
				return em.createQuery(query).getResultList().stream()
						.map(ProductEntity::toDTO)
						.collect(Collectors.toList());
			} catch (Exception e) {
				log.warn("error occured during select()");
				return new ArrayList<>();
			}
		});
	}
	
	@Override
	public void save (ProductDTO dto) {
		if (dto.id == null) {
			persist(dto);
		} else {
			update(dto);
		}
	}
	
	private boolean persist (ProductDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.PRODUCT_SLUG, dto.slug);
		eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_PRE_PERSIST,props));
		
		boolean success = txControl.required(() -> {
			try {
				CategoryEntity category = em.find(CategoryEntity.class, dto.categoryId);
				ProductEntity product = ProductEntity.fromDTO(category, dto);
				em.persist(product);
				dto.id = product.getId();
				return true;
			} catch (Exception e) {
				return false;
			}
		});
		
		if (success) {
			props.put(DaoEvents.PRODUCT_ID, dto.id);
		} else {
			props.put(DaoEvents.ERROR, true);
		}
		
		eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_POST_PERSIST,props));
		
		return success;
	}
	
	private boolean update (ProductDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.PRODUCT_ID, dto.id);
		eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_PRE_UPDATE,props));
		
		boolean success = txControl.required(() -> {
			try {
				CategoryEntity category = em.find(CategoryEntity.class, dto.categoryId);
				ProductEntity product = ProductEntity.fromDTO(category, dto);
				em.merge(product);
				return true;
			} catch (Exception e) {
				return false;
			}
		});
		
		if (!success) {
			props.put(DaoEvents.ERROR, true);
		}
		
		eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_POST_UPDATE, props));
		
		return success;
	}

	@Override
	public boolean delete(Long id) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.PRODUCT_ID, id);
		eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_PRE_REMOVE, props));
		
		try {
			int count = txControl.required(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaDelete<ProductEntity> query = builder.createCriteriaDelete(ProductEntity.class);
				Root<ProductEntity> from = query.from(ProductEntity.class);
				query.where(builder.equal(from.get("id"), id));
				return em.createQuery(query).executeUpdate();
			});
			
			if (count < 1) {
				props.put(DaoEvents.ERROR, false);
			}
			
			eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_POST_REMOVE, props));
			
			return count > 0 ? true : false;
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_POST_REMOVE, props));
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(String slug) {
		ProductDTO dto = select(slug);
		
		if (dto == null) {
			return false;
		}
		
		return delete(dto.id);
	}

}
