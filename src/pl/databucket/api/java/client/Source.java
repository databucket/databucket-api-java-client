package pl.databucket.api.java.client;

public enum Source {
	CONST("const"),
	FIELD("field"), 
	PROPERTY("property"), 
	FUNCTION("function");

	private final String sourceName;
	
	private Source(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public String toString() {
		return sourceName;
	}	
}
