package dev.persistence.impl.jpa.xa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dev.persistence.dto.PropertyDTO;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
		"slug",
		"strVal",
		"boolVal",
		"floatVal",
		"intVal"}))
public class PropertyEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String slug;
	
	private String	strVal;
	private Integer	intVal;
	private Float	floatVal;
	private Boolean	boolVal;
	
	public Long getId () {
		return id;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		if (slug == null || slug.isEmpty()) {
			throw new IllegalArgumentException("slug has to be set");
		}
		this.slug = slug;
	}

	public String getStrVal() {
		return strVal;
	}

	public void setStrVal(String strVal) {
		if (strVal == null) {
			throw new IllegalArgumentException("value must not be null");
		}
		this.intVal = null;
		this.floatVal = null;
		this.boolVal = null;
		this.strVal = strVal;
	}

	public Integer getIntVal() {
		return intVal;
	}

	public void setIntVal(Integer intVal) {
		if (intVal == null) {
			throw new IllegalArgumentException("value must not be null");
		}
		this.strVal = null;
		this.floatVal = null;
		this.boolVal = null;
		this.intVal = intVal;
	}

	public Float getFloatVal() {
		return floatVal;
	}

	public void setFloatVal(Float floatVal) {
		if (floatVal == null) {
			throw new IllegalArgumentException("value must not be null");
		}
		this.strVal = null;
		this.intVal = null;
		this.boolVal = null;
		this.floatVal = floatVal;
	}

	public Boolean getBoolVal() {
		return boolVal;
	}

	public void setBoolVal(Boolean boolVal) {
		if (boolVal == null) {
			throw new IllegalArgumentException("value must not be null");
		}
		this.strVal = null;
		this.intVal = null;
		this.floatVal = null;
		this.boolVal = boolVal;
	}
	
	public Object getVal() {
		if (strVal != null) {
			return strVal;
		} else if (intVal != null) {
			return intVal;
		} else if (floatVal != null) {
			return floatVal;
		} else if (boolVal != null) {
			return boolVal;
		}
		
		return null;
	}
	
	public void setVal (Object value) {
		if (value instanceof String) {
			setStrVal((String) value);
		} else if (value instanceof Integer) {
			setIntVal((Integer) value);
		} else if (value instanceof Float) {
			setFloatVal((Float) value);
		} else if (value instanceof Boolean) {
			setBoolVal((Boolean) value);
		} else {
			throw new IllegalArgumentException("unsupported type: " + value.getClass().getName());
		}
	}
	
	@PrePersist
	public void prePersist() {
		
		if (slug == null || slug.isEmpty()) {
			throw new RuntimeException("slug must not be empty");
		}
		
		if (strVal == null && intVal == null && floatVal == null && boolVal == null) {
			throw new RuntimeException("no value supplied");
		}
	}
	
	@PreUpdate
	private void preUpdate () {
		prePersist();
	}
	
	public PropertyDTO toDTO () {
		PropertyDTO dto = new PropertyDTO();
		
		return dto;
	}
	
	public static PropertyEntity fromDTO (PropertyDTO dto) {
		PropertyEntity entity = new PropertyEntity();
		entity.id = dto.id;
		if (dto.slug != null) {
			entity.setSlug(dto.slug);
		}
		
		// set value
		if (dto.strVal != null) {
			entity.setStrVal(dto.strVal);
		} else if (dto.intVal != null) {
			entity.setIntVal(dto.intVal);
		} else if (dto.floatVal != null) {
			entity.setFloatVal(dto.floatVal);
		} else if (dto.boolVal) {
			entity.setBoolVal(dto.boolVal);
		}
		
		return entity;
	}
	
	protected void populateDTO (PropertyDTO dto) {
		dto.id = this.id;
		dto.slug = this.slug;
		
		// set value
		if (strVal != null) {
			dto.strVal = strVal;
		} else if (intVal != null) {
			dto.intVal = intVal;
		} else if (floatVal != null) {
			dto.floatVal = floatVal;
		} else if (boolVal != null) {
			dto.boolVal = boolVal;
		}
	}
	
	protected static void populateEntity(PropertyEntity prop, PropertyDTO dto) {
		prop.id = dto.id;
		prop.slug = dto.slug;
		
		// set value
		if (dto.strVal != null) {
			prop.setStrVal(dto.strVal);
		} else if (dto.intVal != null) {
			prop.setIntVal(dto.intVal);
		} else if (dto.floatVal != null) {
			prop.setFloatVal(dto.floatVal);
		} else if (dto.boolVal) {
			prop.setBoolVal(dto.boolVal);
		}
	}
}
