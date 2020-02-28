package pl.databucket.api.java.client;

public enum SourceType {
	CONST("const"),
	FIELD("field"), 
	PROPERTY("property"), 
	FUNCTION("function");

	private final String sourceName;
	
	private SourceType(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public String toString() {
		return sourceName;
	}	
}
