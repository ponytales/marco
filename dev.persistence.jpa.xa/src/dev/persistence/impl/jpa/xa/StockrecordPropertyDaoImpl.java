package dev.persistence.impl.jpa.xa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
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
import dev.persistence.dao.StockrecordPropertyDao;
import dev.persistence.dto.StockrecordPropertyDTO;
import dev.persistence.impl.jpa.xa.entity.StockrecordEntity;
import dev.persistence.impl.jpa.xa.entity.StockrecordPropertyEntity;

@Component(service = StockrecordPropertyDao.class, scope = ServiceScope.SINGLETON)
public class StockrecordPropertyDaoImpl implements StockrecordPropertyDao {
	
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
	public StockrecordPropertyDTO select(Long id) {
		return txControl.supports(() -> {
			try {
				return em.find(StockrecordPropertyEntity.class, id).toDTO();
			} catch (EntityNotFoundException enfe) {
				return null;
			}
		});
	}

	@Override
	public StockrecordPropertyDTO select(String slug) {
		try {
			return txControl.supports(() -> {
				try {
					CriteriaBuilder builder = em.getCriteriaBuilder();
					CriteriaQuery<StockrecordPropertyEntity> query =
							builder.createQuery(StockrecordPropertyEntity.class);
					Root<StockrecordPropertyEntity> from =
							query.from(StockrecordPropertyEntity.class);
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
	public List<StockrecordPropertyDTO> select() {
		return txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<StockrecordPropertyEntity> query =
					builder.createQuery(StockrecordPropertyEntity.class);
			query.from(StockrecordPropertyEntity.class);
			return em.createQuery(query).getResultList().stream()
					.map(StockrecordPropertyEntity::toDTO).collect(Collectors.toList());
		});
	}

	@Override
	public void save(StockrecordPropertyDTO dto) {
		if (dto.id == null) {
			persist(dto);
		} else {
			update(dto);
		}
	}
	
	private void persist(StockrecordPropertyDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.PROPERTY_DENOMINATOR, "stockrecord");
		props.put(DaoEvents.PROPERTY_SLUG, dto.slug);
		eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_PRE_PERSIST, props));
		
		try {
			txControl.required(() -> {
				StockrecordEntity stock = em.find(StockrecordEntity.class, dto.stockrecordId);
				StockrecordPropertyEntity entity = StockrecordPropertyEntity.fromDTO(stock, dto);
				em.persist(entity);
				dto.id = entity.getId();
				return entity.getId();
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_PERSIST, props));
			throw new DaoException(e);
		}
		
		props.put(DaoEvents.PROPERTY_ID, dto.id);
		
		eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_PERSIST, props));
	}
	
	private void update(StockrecordPropertyDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.PROPERTY_DENOMINATOR, "stockrecord");
		props.put(DaoEvents.PROPERTY_ID, dto.id);
		eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_PRE_UPDATE, props));
		
		try {
			txControl.required(() -> {
				StockrecordEntity stock = em.find(StockrecordEntity.class, dto.stockrecordId);
				StockrecordPropertyEntity entity =
						StockrecordPropertyEntity.fromDTO(stock, dto);
				em.merge(entity);
				return null;
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_UPDATE, props));
			throw new DaoException(e);
		}

		eventAdmin.sendEvent(new Event(DaoEvents.PROPERTY_POST_UPDATE, props));
	}

	@Override
	public boolean delete(Long pk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(String slug) {
		// TODO Auto-generated method stub
		return false;
	}

}
