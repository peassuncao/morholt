package br.fapema.morholt.web.shared;

import java.util.Date;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;

public class MyFilterPredicate implements FilterInterface, IsSerializable {
	private static final long serialVersionUID = -4538598479546676465L;
	public MyFilterPredicate() {
		super();
	}
	
	public void initialize(String name, MyFilterOperator type, String value) {
		this.type = type;
		this.name = name;
		this.stringValue = value;
		booleanValue = null;
		dateValue = null;
	}
	
	public void initialize(String name, MyFilterOperator type, Boolean value) {
		this.type = type;
		this.name = name;
		this.booleanValue = value;
		stringValue = null;
		dateValue = null;
	}
	
	public MyFilterPredicate(String name, MyFilterOperator type, String value) {
		super();
		this.type = type;
		this.name = name;
		this.stringValue = value;
		booleanValue = null;
		dateValue = null;
	}
	
	public MyFilterPredicate(String name, MyFilterOperator type, Boolean value) {
		super();
		this.type = type;
		this.name = name;
		this.booleanValue = value;
		stringValue = null;
		dateValue = null;
	}
	
	public MyFilterPredicate(String name, MyFilterOperator type, Date value) {
		super();
		this.type = type;
		this.name = name;
		this.dateValue = value;
		stringValue = null;
		booleanValue = null;
	}
	
	public enum MyFilterOperator {
		LESS("<"), LESS_EQUAL("<="), GREATER(">"), GREATER_EQUAL(">="), EQUAL("="), NOT_EQUAL("!=");
		public String value;
		MyFilterOperator(String value) {
			this.value = value;
		}
		
		public  static MyFilterOperator obainTypeFrom(String value) {
			for (MyFilterOperator type : values()) {
				if(type.value.equals(value)) {
					return type;
				}
			}
			throw new RuntimeException("wrong value passed to MyFilterPredicate.MyFilterOperator.obainType: " + value);
		}
	}
	private MyFilterOperator type;
	private String name;
	private String stringValue;
	private Date dateValue;
	private Boolean booleanValue;
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "-> name: " + name + " value: " + getValue() + " operator: '" + type.value + "'";
	}
	
	public Object getValue() {
		if(type == null) return null;
		if(stringValue == null && type.equals(MyFilterOperator.NOT_EQUAL)) return stringValue;
		if(stringValue != null) return stringValue;
		else if(dateValue != null) return dateValue;
		return booleanValue;
	}
	
	public MyFilterOperator getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public String getStringValue() {
		return stringValue;
	}
	
	//TODO ugly!
	public boolean isValueEmpty() {
		Object value = getValue();
		if(value == null && type == null) return true;
		if(value == null && type.equals(MyFilterOperator.NOT_EQUAL)) return false;
		if(value == null) return true;
		if(value instanceof String) return getStringValue().isEmpty();
		return false;
	}

	public void setType(MyFilterOperator type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStringValue(String value) {
		this.stringValue = value;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
}
