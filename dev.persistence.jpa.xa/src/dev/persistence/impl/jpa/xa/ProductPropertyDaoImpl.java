package dev.persistence.impl.jpa.xa;

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

import dev.persistence.dao.DaoEvents;
import dev.persistence.dao.DaoException;
import dev.persistence.dao.ProductPropertyDao;
import dev.persistence.dto.ProductPropertyDTO;
import dev.persistence.dto.PropertyDTO;
import dev.persistence.impl.jpa.xa.entity.ProductEntity;
import dev.persistence.impl.jpa.xa.entity.ProductPropertyEntity;
import dev.persistence.impl.jpa.xa.entity.PropertyEntity;

/**
 * This template provides an example Data Access service using JPA.
 * Typically it should be exposed as an OSGi service by implementing a
 * Data Access Service interface.
 * 
 * <p>
 * This template expects a ready-configured JPAEntityManagerProvider service
 * to be registered. If this is not the case then a
 * JPAEntityManagerProviderFactory can be used in conjunction with the
 * EntityManagerFactoryBuilder to create a JPAEntityManagerProvider
 */


@Component(service=ProductPropertyDao.class, scope = ServiceScope.SINGLETON)
public class ProductPropertyDaoImpl implements ProductPropertyDao {

    @Reference
    TransactionControl txControl;
    
    @Reference
    EventAdmin eventAdmin;

    /** 
     * This target filter must select the persistence unit defined by
     * the persistence.xml in this bundle
     */
    @Reference(target="(osgi.unit.name=dev.persistence.jpa.xa-pu)")
    JPAEntityManagerProvider jpaEntityManagerProvider;

    EntityManager em;

    @Activate
    void activate(Map<String, Object> props) {
        em = jpaEntityManagerProvider.getResource(txControl);
    }
    
    @Override
    public ProductPropertyDTO select (Long id) {
    	try {
    		return txControl.supports(() -> {
    			try {
        			return em.find(ProductPropertyEntity.class, id).toDTO();
    			} catch (EntityNotFoundException e) {
    				return null;
    			}
    		});
    	} catch (Exception e) {
    		throw new DaoException(e);
    	}
    }

	@Override
	public ProductPropertyDTO select(String slug) {
		try {
			return txControl.supports(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ProductPropertyEntity> query =
						builder.createQuery(ProductPropertyEntity.class);
				Root<ProductPropertyEntity> from = query.from(ProductPropertyEntity.class);
				query.where(builder.equal(from.get("slug"), slug));
				try {
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
    public boolean delete(Long id) {
    	Map<String,Object> props = new HashMap<>();
    	props.put(DaoEvents.PROPERTY_DENOMINATOR, "product");
    	props.put(DaoEvents.PROPERTY_ID, id);
    	eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_PRE_REMOVE, props));
    	
    	try {
            int count = txControl.required(() -> {
                CriteriaBuilder builder = em.getCriteriaBuilder();

                CriteriaDelete<ProductPropertyEntity> query = builder
                		.createCriteriaDelete(ProductPropertyEntity.class);

                Root<ProductPropertyEntity> from = query.from(ProductPropertyEntity.class);

                query.where(builder.equal(from.get("id"), id));

                return em.createQuery(query).executeUpdate();
            });
            
            if (count < 1) {
            	props.put(DaoEvents.ERROR, false);
            }
            
            eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_REMOVE, props));
            return count > 0 ? true : false;
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
            eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_REMOVE, props));
			throw new DaoException(e);
		}
    }

	@Override
	public boolean delete(String slug) {
		return delete(select(slug).id);
	}

	@Override
	public void save(ProductPropertyDTO dto) {
		
		boolean doPersist = dto.id == null;
		
		Map<String,Object> props = new HashMap<>();
    	props.put(DaoEvents.PROPERTY_DENOMINATOR, "product");
		
		if (doPersist) {
			props.put(DaoEvents.PROPERTY_SLUG, dto.slug);
			eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_PRE_PERSIST, props));
		} else {
			props.put(DaoEvents.PROPERTY_ID, dto.id);
			eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_PRE_UPDATE, props));
		}
		
		try {
			txControl.required(() -> {
				ProductEntity product = em.find(ProductEntity.class, dto.productId);
				ProductPropertyEntity prop = ProductPropertyEntity.fromDTO(product, dto);
				if (doPersist) {
					em.persist(prop);
					dto.id = prop.getId();
					props.put(DaoEvents.PROPERTY_ID, prop.getId());
				} else {
					em.merge(prop);
				}
				return null;
			});
			
			eventAdmin.sendEvent(new Event(doPersist
					? DaoEvents.PROPERTY_POST_PERSIST : DaoEvents.PROPERTY_POST_UPDATE,
					props));
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			if (doPersist) {
				eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_PERSIST, props));
			} else {
				eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_UPDATE, props));
			}
			throw new DaoException(e);
		}
	}

	@Override
	public List<ProductPropertyDTO> select() {
		return txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ProductPropertyEntity> query = builder.createQuery(ProductPropertyEntity.class);
			query.from(ProductPropertyEntity.class);
			return em.createQuery(query).getResultList().stream()
					.map(ProductPropertyEntity::toDTO)
					.collect(Collectors.toList());
		});
	}

	@Override
	public List<PropertyDTO> findByProduct(Long productId) {
		return txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PropertyEntity> query = builder.createQuery(PropertyEntity.class);
			Root<PropertyEntity> from = query.from(PropertyEntity.class);
			query.where(builder.equal(from.get("product"), productId));
			return em.createQuery(query).getResultList().stream()
					.map(PropertyEntity::toDTO)
					.collect(Collectors.toList());
		});
	}
}
