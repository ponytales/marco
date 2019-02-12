package dev.persistence.impl.jpa.xa.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import dev.persistence.dto.StockrecordPropertyDTO;

@Entity
public class StockrecordPropertyEntity extends PropertyEntity {
	
	@ManyToOne
	private StockrecordEntity stockrecord;
	
	public StockrecordEntity getStockrecord () {
		return stockrecord;
	}
	
	public void setStockrecord (StockrecordEntity stockrecord) {
		this.stockrecord = stockrecord;
	}
	
	public StockrecordPropertyDTO toDTO () {
		StockrecordPropertyDTO dto = new StockrecordPropertyDTO();
		
		populateDTO(dto);
		dto.stockrecordId = stockrecord == null ? null : stockrecord.getId();
		
		return dto;
	}
	
	public static StockrecordPropertyEntity fromDTO (StockrecordEntity stockrecord, StockrecordPropertyDTO dto) {
		StockrecordPropertyEntity property = new StockrecordPropertyEntity();
		
		populateEntity(property, dto);
		
		dto.stockrecordId = stockrecord == null ? null : stockrecord.getId();
		
		return property;
	}
}
