package dev.persistence.dto;

import java.util.ArrayList;
import java.util.List;

import org.osgi.dto.DTO;

public class StockrecordDTO extends DTO {
	public Long id;
	public String slug;
	public Long productId;
	public List<StockrecordPropertyDTO> properties = new ArrayList<>();
	public Float price;
}
