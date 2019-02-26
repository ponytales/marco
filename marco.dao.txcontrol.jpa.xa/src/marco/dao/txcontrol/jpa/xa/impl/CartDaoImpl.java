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
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import marco.dao.CartDao;
import marco.dao.DaoEvents;
import marco.dao.txcontrol.jpa.xa.entity.CartEntity;
import marco.dto.CartDTO;

public class CartDaoImpl implements CartDao {
	
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
	public CartDTO select(Long id) {
		return txControl.supports(() -> {
			try {
				return em.find(CartEntity.class, id).toDTO();
			} catch (EntityNotFoundException e) {
				return null;
			}
		});
	}

	@Override
	public CartDTO select(String session) {
		return txControl.supports(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CartEntity> query = builder.createQuery(CartEntity.class);
			Root<CartEntity> from = query.from(CartEntity.class);
			query.where(builder.equal(from.get("session"), session));
			try {
				return em.createQuery(query).getSingleResult().toDTO();
			} catch (NoResultException e) {
				return null;
			}
		});
	}

	@Override
	public List<CartDTO> select() {
		return txControl.notSupported(() -> {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CartEntity> query = builder.createQuery(CartEntity.class);
			query.from(CartEntity.class);
			return em.createQuery(query).getResultList().stream()
					.map(CartEntity::toDTO)
					.collect(Collectors.toList());
		});
	}

	@Override
	public void save(CartDTO dto) {
		if (dto.id == null) {
			persist(dto);
		} else {
			update(dto);
		}
	}

	private void persist(CartDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.CART_SESSION, dto.session);
		eventAdmin.sendEvent(new Event(DaoEvents.CART_PRE_PERSIST, props));
		
		try {
			txControl.required(() -> {
				CartEntity cart = CartEntity.fromDTO(dto);
				em.persist(cart);
				dto.id = cart.getId();
				return null;
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.CART_POST_PERSIST, props));
			throw e;
		}
		
		props.put(DaoEvents.CART_ID, dto.id);
		eventAdmin.sendEvent(new Event(DaoEvents.CART_POST_PERSIST, props));
	}

	private void update(CartDTO dto) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.CART_ID, dto.id);
		eventAdmin.sendEvent(new Event(DaoEvents.CART_PRE_UPDATE, props));
		
		try {
			txControl.required(() -> {
				CartEntity cart = CartEntity.fromDTO(dto);
				em.merge(cart);
				return null;
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.CART_POST_UPDATE, props));
			throw e;
		}
		
		eventAdmin.sendEvent(new Event(DaoEvents.CART_POST_PERSIST, props));
	}

	@Override
	public boolean delete(final Long id) {
		Map<String,Object> props = new HashMap<>();
		props.put(DaoEvents.CART_ID, id);
		eventAdmin.sendEvent(new Event(DaoEvents.CART_PRE_REMOVE, props));
		
		int count = 0;
		try {
			count = txControl.required(() -> {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaDelete<CartEntity> delete = builder.createCriteriaDelete(CartEntity.class);
				Root<CartEntity> from = delete.from(CartEntity.class);
				delete.where(builder.equal(from.get("id"), id));
				return em.createQuery(delete).executeUpdate();
			});
		} catch (Exception e) {
			props.put(DaoEvents.ERROR, true);
			eventAdmin.sendEvent(new Event(DaoEvents.CART_POST_REMOVE, props));
			throw e;
		}
		
		if (count < 1) {
			props.put(DaoEvents.ERROR, false);
		}
		
		eventAdmin.sendEvent(new Event(DaoEvents.CART_POST_REMOVE, props));
		
		return count > 0 ? true : false;
	}

}
