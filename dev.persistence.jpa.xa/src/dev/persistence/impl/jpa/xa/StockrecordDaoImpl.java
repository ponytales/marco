package dev.persistence.impl.jpa.xa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
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
import dev.persistence.dao.StockrecordDao;
import dev.persistence.dto.StockrecordDTO;
import dev.persistence.impl.jpa.xa.entity.ProductEntity;
import dev.persistence.impl.jpa.xa.entity.StockrecordEntity;

@Component(service = StockrecordDao.class, scope = ServiceScope.SINGLETON)
public class StockrecordDaoImpl implements StockrecordDao {
	
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
	public StockrecordDTO select(Long id) {
		return txControl.supports(() -> {
			try {
				return em.find(StockrecordEntity.class, id).toDTO();
			} catch (EntityNotFoundException e) {
				return null;
			}
		});
	}

	@Override
	public StockrecordDTO select(String slug) {
		return txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<StockrecordEntity> query =
					builder.createQuery(StockrecordEntity.class);
			Root<StockrecordEntity> from = query.from(StockrecordEntity.class);
			query.where(builder.equal(from.get("slug"), slug));
			return em.createQuery(query).getSingleResult().toDTO();
		});
	}

	@Override
	public List<StockrecordDTO> select() {
		return txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<StockrecordEntity> query =
					builder.createQuery(StockrecordEntity.class);
			query.from(StockrecordEntity.class);
			return em.createQuery(query).getResultList().stream()
					.map(StockrecordEntity::toDTO)
					.collect(Collectors.toList());
		});
	}

	@Override
	public void save(StockrecordDTO dto) {
		
		boolean doPersist = dto.id == null;
		
		Map<String,Object> props = new HashMap<>();
		if (doPersist) {
			props.put(DaoEvents.STOCKRECORD_SLUG, dto.slug);
			eventAdmin.sendEvent(new Event(DaoEvents.STOCKRECORD_PRE_PERSIST, props));
		} else {
			props.put(DaoEvents.STOCKRECORD_ID, dto.id);
			eventAdmin.sendEvent(new Event(DaoEvents.STOCKRECORD_PRE_UPDATE, props));
		}
		
		try {
			txControl.required(() -> {
				ProductEntity product = em.find(ProductEntity.class, dto.productId);
				StockrecordEntity stock = StockrecordEntity.fromDTO(product, dto);
				if (doPersist) {
					em.persist(stock);
					dto.id = stock.getId();
					props.put(DaoEvents.STOCKRECORD_ID, stock.getId());
				} else {
					em.merge(stock);
				}
				return null;
			});
			
			eventAdmin.sendEvent(new Event(doPersist
					? DaoEvents.STOCKRECORD_POST_PERSIST : DaoEvents.STOCKRECORD_POST_UPDATE,
					props));
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			if (doPersist) {
				eventAdmin.sendEvent(new Event(DaoEvents.STOCKRECORD_POST_PERSIST, props));
			} else {
				eventAdmin.sendEvent(new Event(DaoEvents.STOCKRECORD_POST_UPDATE, props));
			}
			throw new DaoException(e);
		}
	}

	@Override
	public boolean delete(Long id) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.STOCKRECORD_ID, id);
		eventAdmin.sendEvent(new Event(DaoEvents.STOCKRECORD_PRE_REMOVE, props));
		
		try {
			int count = txControl.required(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaDelete<StockrecordEntity> query =
						builder.createCriteriaDelete(StockrecordEntity.class);
				Root<StockrecordEntity> from = query.from(StockrecordEntity.class);
				query.where(builder.equal(from.get("id"), id));
				return em.createQuery(query).executeUpdate();
			});
			
			if (count < 1) {
				props.put(DaoEvents.ERROR, false);
			}
			
			eventAdmin.sendEvent(new Event(DaoEvents.STOCKRECORD_POST_REMOVE, props));
			return count > 0 ? true : false;
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.STOCKRECORD_POST_REMOVE, props));
			throw e;
		}
	}

	@Override
	public boolean delete(String slug) {
		return delete(select(slug).id);
	}

	@Override
	public List<StockrecordDTO> findByProduct(Long productId) {
		return txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<StockrecordEntity> query = builder.createQuery(StockrecordEntity.class);
			Root<StockrecordEntity> from = query.from(StockrecordEntity.class);
			query.where(builder.equal(from.get("product"), productId));
			return em.createQuery(query).getResultList().stream()
					.map(StockrecordEntity::toDTO)
					.collect(Collectors.toList());
		});
	}

}
