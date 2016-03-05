package kr.dcos.common.utils;

/**
 * SQL문자의 order by절
 * 
 * @author Kim Do Young
 *
 */
public class OrderByClause {
	private String fieldName;
	private String direction;
	
	public OrderByClause(String fieldName,String direction){
		this.fieldName = fieldName;
		this.direction = direction;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	@Override
	public String toString(){
		return fieldName + " " + direction;
	}
}
