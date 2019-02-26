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
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import marco.dao.DaoEvents;
import marco.dao.DaoException;
import marco.dao.PositionDao;
import marco.dao.txcontrol.jpa.xa.entity.CartEntity;
import marco.dao.txcontrol.jpa.xa.entity.PositionEntity;
import marco.dto.PositionDTO;

public class PositionDaoImpl implements PositionDao {
	
	@Reference
	private EventAdmin eventAdmin;
	
	@Reference
	private TransactionControl txControl;

	@Reference(target="(osgi.unit.name=marco.dao.txcontrol.jpa.xa-pu)")
	private JPAEntityManagerProvider provider;
	
	private EntityManager em;
	
	@Activate
	private void activate () {
		em = provider.getResource(txControl);
	}

	@Override
	public PositionDTO select(Long id) {
		PositionEntity pos = txControl.supports(() -> {
			try {
				return em.find(PositionEntity.class, id);
			} catch (EntityNotFoundException enfe) {
				return null;
			}
		});
		
		return pos.toDTO();
	}
	
	@Override
	public List<PositionDTO> select() {
		List<PositionEntity> positions = txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PositionEntity> query = builder.createQuery(PositionEntity.class);
			query.from(PositionEntity.class);
			try {
				return em.createQuery(query).getResultList();
			} catch (NoResultException e) {
				return new ArrayList<PositionEntity>();
			}
		});
		
		return positions.stream().map(PositionEntity::toDTO).collect(Collectors.toList());
	}
	
	@Override
	public List<PositionDTO> findByCart (Long cartId) {
		List<PositionEntity> positions = txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PositionEntity> query = builder.createQuery(PositionEntity.class);
			Root<PositionEntity> from = query.from(PositionEntity.class);
			query.where(builder.equal(from.get("cart"), cartId));
			try {
				return em.createQuery(query).getResultList();
			} catch (NoResultException e) {
				return new ArrayList<>();
			}
		});
		
		return positions.stream().map(PositionEntity::toDTO).collect(Collectors.toList());
	}

	@Override
	public void save(PositionDTO dto) {
		if (dto.id == null) {
			persist(dto);
		} else {
			update(dto);
		}
	}
	
	private void persist(PositionDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.CART_ID, dto.cartId);
		
		eventAdmin.sendEvent(new Event(DaoEvents.POSITION_PRE_PERSIST, props));
		
		try {
			txControl.required(() -> {
				CartEntity cart = em.find(CartEntity.class, dto.cartId);
				PositionEntity p = PositionEntity.fromDTO(cart, dto);
				em.persist(p);
				dto.id = p.getId();
				return null;
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.POSITION_POST_PERSIST, props));
			throw e;
		}
		
		props.put(DaoEvents.POSITION_ID, dto.id);
		eventAdmin.sendEvent(new Event(DaoEvents.POSITION_POST_PERSIST, props));
	}
	
	private void update(PositionDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.POSITION_ID, dto.id);
		
		eventAdmin.sendEvent(new Event(DaoEvents.POSITION_PRE_UPDATE, props));
		
		try {
			txControl.required(() -> {
				CartEntity cart = em.find(CartEntity.class, dto.cartId);
				PositionEntity p = PositionEntity.fromDTO(cart, dto);
				em.merge(p);
				return null;
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.POSITION_POST_UPDATE, props));
			throw e;
		}
		
		eventAdmin.sendEvent(new Event(DaoEvents.POSITION_POST_UPDATE, props));
	}

	@Override
	public boolean delete(Long positionId) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.POSITION_PRE_REMOVE, positionId);
		eventAdmin.sendEvent(new Event(DaoEvents.POSITION_PRE_REMOVE, props));
		
		try {
			int count = txControl.required(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaDelete<PositionEntity> delete = builder.createCriteriaDelete(PositionEntity.class);
				Root<PositionEntity> from = delete.from(PositionEntity.class);
				delete.where(builder.equal(from.get("id"), positionId));
				return em.createQuery(delete).executeUpdate();
			});
			
			if (count < 1) {
				props.put(DaoEvents.ERROR, false);
			}
			
			eventAdmin.sendEvent(new Event(DaoEvents.POSITION_POST_REMOVE, props));
			
			return count > 0 ? true : false;
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.PRODUCT_POST_REMOVE, props));
			throw new DaoException(e);
		}
	}
}
