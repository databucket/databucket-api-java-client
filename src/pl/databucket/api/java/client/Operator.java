package pl.databucket.api.java.client;

public enum Operator {
	
	equal("="),
	grater(">"),
	graterEqual(">="),
	notin("NOT IN"),
	in("IN"),
	is("IS"),
	isnot("IS NOT"),
	less("<"), 
	lessEqual("<="),
	like("LIKE"), 
	notEqual("<>"), 
	notLike("NOT LIKE");

	private final String symbol;
	
	private Operator(String text) {
		this.symbol = text;
	}
	
	public String toString() {
		return symbol;
	}	
}
