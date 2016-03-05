package kr.dcos.common.utils;

public class SelectConditionDefaults {
	private int pageSize;
	private String orderBy;
	private String orderByDirection;
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderByDirection() {
		return orderByDirection;
	}
	public void setOrderByDirection(String orderByDirection) {
		this.orderByDirection = orderByDirection;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}
