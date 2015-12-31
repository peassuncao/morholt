package br.fapema.morholt.web.shared;



public class MyCompositeFilter implements FilterInterface {

	private static final long serialVersionUID = 6324419646246895557L;
	private Type type;
	private FilterInterface filter1, filter2;
	
	public MyCompositeFilter() {
		super();
	}
	
	public MyCompositeFilter(FilterInterface filter1, Type type, FilterInterface filter2) {
		super();
		this.filter1 = filter1;
		this.type = type;
		this.filter2 = filter2;
	}
	
	public enum Type {
		AND("AND"), OR("OR");
		public String value;
		Type(String value) {
			this.value = value;
		}
		
		public  static Type obainTypeFrom(String value) {
			for (Type type : values()) {
				if(type.value.equals(value)) {
					return type;
				}
			}
			throw new RuntimeException("wrong value passed to MyCompositeFilter.Type.obainType: " + value);
		}
	}
	public Type getType() {
		return type;
	}

	public FilterInterface getFilter1() {
		return filter1;
	}

	public FilterInterface getFilter2() {
		return filter2;
	}

	
	
}
