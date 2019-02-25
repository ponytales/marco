package marco.dao.txcontrol.jpa.xa.impl;

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

import marco.dao.CategoryDao;
import marco.dao.DaoEvents;
import marco.dao.DaoException;
import marco.dao.txcontrol.jpa.xa.entity.CategoryEntity;
import marco.dto.CategoryDTO;

@Component(service = CategoryDao.class, scope = ServiceScope.SINGLETON)
public class CategoryDaoImpl implements CategoryDao {
	
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
	public CategoryDTO select(Long id)  {
		try {
			return txControl.notSupported(() -> {
				try {
					return em.find(CategoryEntity.class, id).toDTO();
				} catch (EntityNotFoundException enfe) {
					return null;
				}
			});
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public CategoryDTO select(String slug) {
		try {
			return txControl.notSupported(() -> {
				try {
					CriteriaBuilder builder = em.getCriteriaBuilder();
					CriteriaQuery<CategoryEntity> query =
							builder.createQuery(CategoryEntity.class);
					Root<CategoryEntity> from = query.from(CategoryEntity.class);
					query.where(builder.equal(from.get("slug"), slug));
					return em.createQuery(query).getSingleResult().toDTO();
				} catch (NoResultException e) {
					return null;
				}
			});
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	@Override
	public void save(CategoryDTO dto) {
		if (dto.id == null) {
			persist(dto);
		} else {
			update(dto);
		}
	}
	
	private void persist(CategoryDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.CATEGORY_SLUG, dto.slug);
		eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_PRE_PERSIST, props));
		
		try {
			txControl.required(() -> {
				CategoryEntity parent = dto.parentId == null ? null : em.find(CategoryEntity.class, dto.parentId);
				CategoryEntity entity = CategoryEntity.fromDTO(parent, dto);
				em.persist(entity);
				dto.id = entity.getId();
				return entity.getId();
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_POST_PERSIST, props));
			throw new DaoException(e);
		}
		
		props.put(DaoEvents.CATEGORY_ID, dto.id);
		
		eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_POST_PERSIST, props));
	}
	
	private void update(CategoryDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.CATEGORY_ID, dto.id);
		eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_PRE_UPDATE, props));
		
		try {
			txControl.required(() -> {
				CategoryEntity parent = dto.parentId == null ? null : em.find(CategoryEntity.class, dto.parentId);
				CategoryEntity entity = CategoryEntity.fromDTO(parent, dto);
				em.merge(entity);
				return null;
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_POST_UPDATE, props));
			throw new DaoException(e);
		}

		eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_POST_UPDATE, props));
	}

	@Override
	public boolean delete(Long id) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.CATEGORY_ID, id);
		eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_PRE_REMOVE, props));
		
		try {
			int count = txControl.required(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaDelete<CategoryEntity> delete = builder.createCriteriaDelete(CategoryEntity.class);
				Root<CategoryEntity> from = delete.from(CategoryEntity.class);
				delete.where(builder.equal(from.get("id"), id));
				return em.createQuery(delete).executeUpdate();
			});
			
			if (count == 0) {
				props.put(DaoEvents.ERROR, false);
			}
			
			eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_POST_REMOVE, props));
			
			return count > 0 ? true : false;
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.CATEGORY_POST_REMOVE, props));
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(String slug) {
		CategoryDTO dto = select(slug);
		
		if (dto == null) {
			return false;
		}
		
		return delete(dto.id);
	}

	@Override
	public List<CategoryDTO> select() {
		try {
			return txControl.notSupported(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<CategoryEntity> query = builder.createQuery(CategoryEntity.class);
				query.from(CategoryEntity.class);
				return em.createQuery(query).getResultList().stream()
						.map(CategoryEntity::toDTO).collect(Collectors.toList());
			});
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
}
