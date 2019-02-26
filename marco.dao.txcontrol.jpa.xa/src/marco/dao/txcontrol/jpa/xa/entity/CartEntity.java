package marco.dao.txcontrol.jpa.xa.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import marco.dto.CartDTO;

@Entity
public class CartEntity {
	@Id
	@GeneratedValue
	private Long id;
	
	private String session;
	
	@OneToMany(mappedBy="cart")
	private List<PositionEntity> positions = new ArrayList<>();
	
	
	public Long getId () {
		return id;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
	
	public List<PositionEntity> getPositions () {
		return positions;
	}
	
	public void setPositions (List<PositionEntity> positions) {
		this.positions = positions;
	}
	
	public CartDTO toDTO () {
		CartDTO dto = new CartDTO();
		dto.id = this.id;
		dto.session = this.session;
		dto.positions = this.positions.stream().map(PositionEntity::toDTO).collect(Collectors.toList());
		return dto;
	}
	
	public static CartEntity fromDTO (CartDTO dto) {
		CartEntity cart = new CartEntity();
		cart.id = dto.id;
		cart.session = dto.session;
		cart.positions = dto.positions.stream().map(pos -> PositionEntity.fromDTO(cart, pos)).collect(Collectors.toList());
		return cart;
	}
}
