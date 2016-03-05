package kr.dcos.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * sql where condition <br>
 * this is similar SelectCondition of board module  <br>
 * 
 * @author Kim Do Young
 *
 */
public class SelectCondition extends PagedBase {
	//총합계가 구해지기 전까지 pagesize와 nowpage는 의미가 없다. 그래서 임시 저장소에 넣어 둔다
	int tmpNowPage = 0;
	int tmpPageSize = 10;
	//tableName for board , SelectCondion말고 다른 class를 만드느니 그냥...
	//tableName은 boardService에서만 사용한다.
	private String tableName; 

	private SearchOrder searchOrder ;
	
	//where Clause
	private List<WhereClause> whereClauseList;
	
	//추가필드에 대한 where Clause 리스트
	private List<WhereClause> chugaSearchList;
	
	//orderByClause list
	protected List<OrderByClause> orderByList;
	//
	// constructor
	//
	public SelectCondition(){
		super();
		searchOrder = new SearchOrder();
		whereClauseList = new ArrayList<WhereClause>();
		chugaSearchList = new ArrayList<WhereClause>();
		orderByList = new ArrayList<OrderByClause>();
	}
	public void addWhereClause(String fieldName, String operator,
			String value) {
		WhereClause where = new WhereClause(fieldName, operator, value	);
		whereClauseList.add(where);
	}
	public List<WhereClause> getWhereClauseList(){
		return whereClauseList;
	}
	public SearchOrder getSearchOrder() {
		return searchOrder;
	}
	//sjan rlfdjtj
	public SearchOrder getSo(){
		return searchOrder;
	}
	public void addOrderBy(String fieldName,String direction){
		orderByList.add(new OrderByClause(fieldName,direction));
	}
	/**
	 * order by 절의 문장을 리턴한다.
	 * order by list가 비어 있으면 null을 리턴한다
	 * @return
	 */
	public String getOrderBy() {
		if(orderByList.size()==0)return null;
		StringBuilder sb = new StringBuilder();
		for (OrderByClause orderByClause : orderByList) {
			sb.append(orderByClause.getFieldName() + " " + orderByClause.getDirection());
			sb.append(",");
		}
		if(sb.toString().endsWith(",")){ //,를 제거
			return sb.toString().substring(0,sb.toString().length()-1);
		}
		return sb.toString();
	}
	/**
	 * totalcount를 셋팅한다, a little utility
	 * @param totalCount
	 */
	public void setTotalCount(int totalCount) {
		getPageAttr().setTotalItemCount(totalCount);
		getPageAttr().setPageSize(tmpPageSize);
		getPageAttr().setNowPageNumber(tmpNowPage);
	}
	public int getTmpNowPage() {
		return tmpNowPage;
	}
	public void setTmpNowPage(int tmpNowPage) {
		this.tmpNowPage = tmpNowPage;
	}
	public int getTmpPageSize() {
		return tmpPageSize;
	}
	public void setTmpPageSize(int tmpPageSize) {
		this.tmpPageSize = tmpPageSize;
	}	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("where clause\n");
		for (WhereClause wc : whereClauseList) {
			sb.append(wc.toString());
			sb.append("\n");
		}
		sb.append("order by :" + getOrderBy());
		return sb.toString();
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<WhereClause> getChugaSearchList() {
		return chugaSearchList;
	}
	public void addChugaSearch(WhereClause whereClause) {
		chugaSearchList.add(whereClause);
		
	}
	public void setNowPageWithRequest() {
		getPageAttr().setNowPageNumber(tmpNowPage);
		
	}
	/**
	 * fieldName에 해당하는 order by 를 뺀다
	 * @param fieldName
	 */
	public void removeOrderby(String fieldName) {
		if(orderByList!=null){
			int foundIndex = -1;
			for(int i=0;i<orderByList.size();i++){
				if(orderByList.get(i).getFieldName().equalsIgnoreCase(fieldName)){
					foundIndex = i;break;
				}
			}
			if(foundIndex>=0){
				orderByList.remove(foundIndex);
			}
		}
	}
}
