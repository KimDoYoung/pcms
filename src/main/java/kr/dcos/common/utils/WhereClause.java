package kr.dcos.common.utils;

/**
 * SQL문장의 Where절 <br>
 * SelectCondition에서 사용한다  <br>
 * 
 * @author Kim Do Young
 *
 */
public class WhereClause {
	private String fieldName;
	private String operator;
	private Object value;
	
	public WhereClause(String fieldName,String operator,Object value){
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString(){
		return fieldName + " " + operator + " " + value.toString();
	}
}
