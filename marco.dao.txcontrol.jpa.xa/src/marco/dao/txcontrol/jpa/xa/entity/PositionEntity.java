package marco.dao.txcontrol.jpa.xa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import marco.dto.PositionDTO;

@Entity
public class PositionEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(optional=false)
	private CartEntity cart;
	
	@ManyToOne(optional=false)
	private StockrecordEntity stockrecord;
	
	private Integer quantity;
	
	public Long getId () {
		return id;
	}
	
	public CartEntity getCart () {
		return cart;
	}
	
	public void setCart (CartEntity cart) {
		this.cart = cart;
	}

	public StockrecordEntity getStockrecord() {
		return stockrecord;
	}

	public void setStockrecord(StockrecordEntity stockrecord) {
		this.stockrecord = stockrecord;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public static PositionEntity fromDTO (CartEntity cart, PositionDTO dto) {
		PositionEntity position = new PositionEntity();
		position.cart = cart;
		position.quantity = dto.quantity;
		position.id = dto.id;
		return position;
	}
	
	public PositionDTO toDTO () {
		PositionDTO dto = new PositionDTO();
		dto.id = this.id;
		dto.cartId = this.cart == null ? null : this.cart.getId();
		dto.stockrecordId = this.stockrecord.getId();
		dto.quantity = this.quantity;
		return dto;
	}
}
