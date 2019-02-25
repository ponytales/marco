package marco.dto;

import java.util.ArrayList;
import java.util.List;

import org.osgi.dto.DTO;

public class ProductDTO extends DTO {
	public Long id;
	public Long categoryId;
	public String slug;
	public List<ProductPropertyDTO> properties = new ArrayList<>();
}
