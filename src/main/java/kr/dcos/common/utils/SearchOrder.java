package kr.dcos.common.utils;

/**
 * Cms의 list.jsp에 사용하는 검색조건과 order by,direction을 위해서 <br>
 * 필요한 정보를 가지고 있는다. <br>
 * EL로 jsp에 표현하기 위해서 SelectCondition에서 사용한다 <br>
 * 
 * @author Kim Do Young
 *
 */
public class SearchOrder {
	//검색조건
	private String searchColumn;
	private String searchOperator;
	private String searchKey;
	//날짜범위
	private String startDate;
	private String dateColumn;
	private String endDate;
	//정렬
	private String orderBy;
	private String orderByDirection;
	//Default 정렬
	private String defaultOrderBy;
	private String defaultOrderByDirection;
	
	public String getSearchColumn() {
		return searchColumn;
	}
	public void setSearchColumn(String searchColumn) {
		this.searchColumn = searchColumn;
	}
	public String getSearchOperator() {
		return searchOperator;
	}
	public void setSearchOperator(String searchOperator) {
		this.searchOperator = searchOperator;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getDateColumn() {
		return dateColumn;
	}
	public void setDateColumn(String dateColumn) {
		this.dateColumn = dateColumn;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getDefaultOrderBy() {
		return defaultOrderBy;
	}
	public void setDefaultOrderBy(String defaultOrderBy) {
		this.defaultOrderBy = defaultOrderBy;
	}
	public String getDefaultOrderByDirection() {
		return defaultOrderByDirection;
	}
	public void setDefaultOrderByDirection(String defaultOrderByDirection) {
		this.defaultOrderByDirection = defaultOrderByDirection;
	}
}
