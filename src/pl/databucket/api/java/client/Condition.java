package pl.databucket.api.java.client;

import com.google.gson.annotations.SerializedName;

public class Condition {
	
	@SerializedName("left_source")
	private String leftSource;
	
	@SerializedName("left_value")
	private Object leftValue;
	
	@SerializedName("operator")
	private String operator;
	
	@SerializedName("right_source")
	private String rightSource;
	
	@SerializedName("right_value")
	private Object rightValue;
	
	public Condition(Source leftSource, Object leftValue, Operator operator, Source rightSource, Object rightValue) {
		this.leftSource = leftSource.toString();
		this.leftValue = leftSource.equals(Source.PROPERTY) ? "$." + leftValue : leftValue;
		this.operator = operator.toString();
		this.rightSource = rightSource.toString();
		this.rightValue = rightSource.equals(Source.PROPERTY) ? "$." + rightValue : rightValue;
	}

	public String getLeftSource() {
		return leftSource;
	}

	public Object getLeftValue() {
		return leftValue;
	}

	public String getOperator() {
		return operator;
	}

	public String getRightSource() {
		return rightSource;
	}

	public Object getRightValue() {
		return rightValue;
	}

}
