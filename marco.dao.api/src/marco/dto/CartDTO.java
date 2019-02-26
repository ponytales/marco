package marco.dto;

import java.util.ArrayList;
import java.util.List;

import org.osgi.dto.DTO;

public class CartDTO extends DTO {
	public String session;
	public List<PositionDTO> positions = new ArrayList<>();
}
