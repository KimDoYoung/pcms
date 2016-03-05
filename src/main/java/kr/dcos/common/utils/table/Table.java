package kr.dcos.common.utils.table;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.dcos.common.utils.GroupList;
import kr.dcos.common.utils.evaluator.Evaluator;
import kr.dcos.common.utils.evaluator.EvaluatorException;
import kr.dcos.common.utils.table.Column.DataType;

import java.util.Map.Entry;


/**
 * 테이블 자료형
 * 관계형데이터베이스의 테이블을 모방한 자료형
 * 
 * @author 김도영
 *
 */
public class Table {
	class RowComparator implements Comparator<Map.Entry<String, Row>> {
		private Table.ORDER order = Table.ORDER.DESC;
		private String orderByColumnName;

		public RowComparator(String columnName, ORDER order) {
			this.orderByColumnName = columnName;
			this.order = order;
		}

		public int compare(Entry<String, Row> o1, Entry<String, Row> o2) {
			
			Row r1 = o1.getValue();
			Row r2 = o2.getValue();
			Object v1 = r1.getObject(orderByColumnName);
			Object v2 = r2.getObject(orderByColumnName);
			if(v1 instanceof Double && v2 instanceof Double){
				Double d1 = Double.parseDouble(v1.toString());
				Double d2 = Double.parseDouble(v2.toString());
				if(order == ORDER.DESC){
					return d2.compareTo(d1);
				}else{
					return d1.compareTo(d2);
				}
			}else{
				if(order == ORDER.DESC){
					return v1.toString().compareTo(v2.toString());
				}else{
					return v2.toString().compareTo(v1.toString());
				}
			}
		}
	}
	public static enum ORDER { DESC, ASC} ;
	public static enum STATIC {MAX,MIN,AVG,SUM};

	private String name;
	private LinkedHashMap<String, Column> columnHeader;
	private LinkedHashMap<String, Row> rowList;

	private List<String> pkList;

	private boolean defaultTrim = true;

	public Table() {
		this.name = "unknown";
		init();
	}

	public Table(String tableCreateScript) throws TableException {
		this("", tableCreateScript);
	}

	public Table(String name, String tableCreateScript)  throws TableException {
		this.name = name;
		init();
		String[] lines = tableCreateScript.split("\\n");
		for (String line : lines) {
			Column nc = new Column(line);
			columnHeader.put(nc.getName(), nc);
		}
		setPkList();
		if (pkList == null || pkList.size() < 1) {
			throw new TableException("primary key가 존재하지 않습니다.");
		}
	}

	/**
	 * pkString과 map으로 row를 추가한다
	 * 
	 * @param pkString
	 * @param map
	 * @throws TableException
	 */
	private void appendRow(String pkString, Row row) throws TableException {
		rowList.put(pkString, row);
	}

	private Table createTableWithList( List<HashMap<String, Object>> subList, String subPk)	throws TableException {
		Table nt = new Table();
		for (String pkStr : pkList) {
			nt.appendColumn(getColumn(pkStr));
		}
		Map<String, Object> map = subList.get(0);
		Object o = map.get(subPk);
		Column nc = new Column(subPk, getType(o), 0, false, true);
		nt.appendColumn(nc);

		nt.addFromResultsOfSql(subList);

		return nt;
	}

	private boolean existColumn(String columnName) {
		return getColumn(columnName.toLowerCase()) != null ? true : false;
	}

	/**
	 * 
	 * @param row
	 * @param map
	 * @throws TableException
	 */
	private void fillRow(Row row, Map<String, Object> map) throws TableException {
		for (Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey().toString();
			Object value = entry.getValue();
			if (columnHeader.containsKey(name) == false) {//column이 없으면 추가한다
				appendColumn(new Column(name, getType(value), 0, false, 	false));
			}
			row.setValue(name, value);
		}
	}

	/**
	 * 
	 * @param pkString
	 * @return
	 */
	private Row findRow(String pkString) {
		if (rowList.containsKey(pkString)) {
			return rowList.get(pkString);
		}
		return null;
	}

	/*
	 * map에서 pk의 값을 찾아서 1개의 문자열로 붙인 후 리턴
	 */
	private String getPkString(Map<String, Object> map) {
		String key = null;
		for (String pk : pkList) {
			if (map.get(pk) != null) {
				if (key == null)
					key = "";
				key += map.get(pk).toString();
			}
		}
		return key;
	}
	private String getPkString(Row row) {
		String key = "";
		for (Map.Entry<String, Column> entry : columnHeader.entrySet()) {
			Column column = entry.getValue();
			if (column.isPrimaryKey()) {
				key += row.getString(column.getName());
			}
		}
		//pk가 존재하지 않으면 그냥 번호를 붙인다.
		if (key.length() < 1){
			key = (getRowSize() + 1) + "";
		}
		return key;
	}
	/**
	 * 
	 * @return
	 */
	private String getPkStringFromAutoInc() {
		for (Column column : columnHeader.values()) {
			if (column.isAutoIncrement()) {
				return String.valueOf(column.getNextRowNum());
			}
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	private boolean hasAutoIncrementField() {
		for (Column column : columnHeader.values()) {
			if (column.isAutoIncrement())
				return true;
		}
		return false;
	}

	private void init() {
		columnHeader = new LinkedHashMap<String, Column>();
		rowList = new LinkedHashMap<String, Row>();
	}

	/**
	 * 
	 * @return
	 */
	private void setPkList() {
		if (pkList == null) {
			pkList = new ArrayList<String>();
		}
		pkList.clear();
		for (Map.Entry<String, Column> entry : columnHeader.entrySet()) {
			Column column = entry.getValue();
			if (column.isPrimaryKey()) {
				pkList.add(column.getName());
			}
		}
	}
	/**
	 * sql의 결과를 담고 있는 리스트로부터 row를 추가한다.
	 * 
	 * @param tmpList
	 * @throws TableException
	 */
	public void addFromResultsOfSql(List<HashMap<String, Object>> tmpList)	throws TableException {
		for (Map<String, Object> map : tmpList) {
			String pkString = getPkString(map);
			if (pkString == null) {
				if (hasAutoIncrementField() == false) {
					throw new TableException("pk is not found:"	+ map.toString());
				} else {
					pkString = getPkStringFromAutoInc();
				}
			}
			Row row = findRow(pkString);
			if (row == null) {
				row = newRow();
				fillRow(row, map);
				appendRow(pkString, row);
			} else {
				fillRow(row, map);
			}
		}
	}

	/**
	 * 
	 * @param columnName
	 * @param list
	 * @param subPk
	 * @throws TableException
	 */
	public void addTableFromResultsOfSql(String columnName,
			List<HashMap<String, Object>> list, String subPk)
			throws TableException {
		if (existColumn(columnName) == false) {
			Column kedColumn = new Column(columnName,	Column.DataType.KED_TABLE, 0, false, false);
			appendColumn(kedColumn);
		}
		GroupList groupList = new GroupList(list, pkList.toArray(new String[0]));
		for (int i = 0; i < groupList.size(); i++) {
			ArrayList<HashMap<String, Object>> subList = groupList.get(i);
			HashMap<String, Object> map = subList.get(0);
			String pkString = getPkString(map);

			if (pkString == null)
				throw new TableException("pk is not found:"	+ map.toString());

			Row row = findRow(pkString);
			if (row == null) {
				row = newRow();
				row.setValue(columnName, createTableWithList(subList, subPk));
				appendRow(pkString, row);
			} else {
				row.setValue(columnName, createTableWithList(subList, subPk));
			}
		}
	}

	/**
	 * 
	 * @param columnCollection
	 */
	public void appendAllColumns(Collection<Column> columnCollection) {
		for (Column column : columnCollection) {
			appendColumn(column);
		}
	}

	/**
	 * column을 추가한다
	 * 
	 * @param kedColumn
	 */
	public void appendColumn(Column column) {
		columnHeader.put(column.getName().toLowerCase(), column);
		setPkList();
	}

	public void appendRow(Row row) {
		String pk = getPkString(row);
		rowList.put(pk, row);

	}

	/**
	 * 테이블 t2의 모든 레코드들을 추가한다.<br>
	 * t2에는 있고 t1에 없는 컬럼들은 생성된다.
	 * t2에 t1의 pk가 없을 경우
	 * 주의)pk값이 일치하는 레코드는 나중에 나오는 레코드의 값으로 치환된다.<br>
	 * @param t2
	 * @throws TableException
	 */
	public void appendTable(Table t2) throws TableException {
		
		if(t2 == null) return;
		
		//t2에는 있고 나에게는 없는 column을 추가한다.
		for(Column col : t2.getColumns()){
			if(this.getColumn(col.getName()) == null){
				this.appendColumn(col.copyTo());
			}
		}
		for (Row row : t2.getRows()) {
			Row newRow = newRow();
			for(Column col : t2.getColumns()){
				Object value = row.getObject(col.getName());
				newRow.setValue(col.getName(), value);
			}
			appendRow(newRow);
		}
	}

	/**
	 * where절에 해당하는 레코드들을 대상으로 columnName의 값을 집합Set으로 리턴한다<br>
	 * 
	 * 참고) distinct 
	 * @param columnName
	 * @return
	 * @throws TableException 
	 */
	public Set<String> asSet(String columnName, String whereCondition) throws TableException {
		String[] array = distinct(columnName, whereCondition);
		return new HashSet<String>( Arrays.asList(array));
	}

	public int columnCount() {
		return columnHeader.size();
	}

	/**
	 * 
	 * @return
	 */
	public String columnDescToString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Column> entry : columnHeader.entrySet()) {
			Column column = entry.getValue();
			sb.append(column.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * whereCondition에 해당하는 레코드들 삭제한 테이블을 리턴한다.<br>
	 * 기존테이블은 유지된다.<br>
	 * @param whereCondition
	 * @throws EvaluatorException 
	 * @throws TableException 
	 */
	public Table delete(String whereCondition) throws EvaluatorException, TableException {
		Table newTable = this.newTableWithMySchema();

		Evaluator rowEvaluator = new Evaluator();
		rowEvaluator.setDebug(false);
		rowEvaluator.setStatement(whereCondition);
		
		Map<String,Object> dataMap = null;
		for (Row row : getRows()) {
			dataMap = row.toMap();

			boolean b = rowEvaluator.evalBoolean(dataMap);
			if (b) { //조건에 맞는 것은 skip
				;
			}else{
				Row newRow = newRow();
				newRow.setFromMap(dataMap);
				newTable.appendRow(newRow);
			}
		}
		return newTable;
	}

	/**
	 * where절에 해당하는 레코드들을 대상으로 columnName의 값을 중복배제한 후 배열로 리턴한다.<br>
	 * 테이블의 순서대로 정렬된다<br>
	 * 참고) asSet 
	 * @param columnName
	 * @return
	 * @throws TableException 
	 */
	public String[] distinct(String columnName, String whereCondition) throws TableException{
		String[] array = toArray(columnName, whereCondition);
		List<String> resultList = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (String s : array) {
			if(set.contains(s)){
				continue;
			}
			resultList.add(s);
			set.add(s);
		}
		return (String[]) resultList.toArray(new String[resultList.size()]);
	}

	/**
	 * 똑같은 스키마의 테이블을 만들고 데이터를 복사한다
	 * @return 복사된 새로운 테이블
	 * @throws TableException 
	 */
	public Table duplicate()  {
		Table newTable = newTableWithMySchema();
		try {
			
			for (Row row : this.getRows()) {
				Map<String, Object> map = row.toMap();
				Row newRow = newTable.newRow();
				newRow.setFromMap(map);
				newTable.appendRow(newRow);
			}
		} catch (Exception e) {
			newTable = null;
		}
		return newTable;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (columnHeader == null) {
			if (other.columnHeader != null)
				return false;
		} else if (!columnHeader.equals(other.columnHeader))
			return false;
		if (rowList == null) {
			if (other.rowList != null)
				return false;
		} else if (!rowList.equals(other.rowList))
			return false;
		return true;
	}

	/**
	 * idx에 해당하는 컬럼을 리턴한다
	 * 
	 * @param idx
	 * @return
	 */
	public Column getColumn(int idx) {
		if (idx >= 0 && idx < columnHeader.size()) {
			List<Column> l = new ArrayList<Column>(columnHeader.values());
			return l.get(idx);
		}
		return null;
	}

	/**
	 * 컬럼명으로 컬럼을 찾아서 리턴한다. 
	 * 찾지 못했을 경우 null을 리턴한다
	 * 
	 * @param columnName
	 * @return
	 */
	public Column getColumn(String columnName) {
		Column nc = columnHeader.get(columnName.toLowerCase());
		if (nc != null) {
			return nc;
		}

		for (Entry<String, Column> entry : columnHeader.entrySet()) {
			Column value = entry.getValue();
			if (value.getAlias() != null
					&& value.getAlias().equalsIgnoreCase(columnName)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * 컬럼명으로 컬럼을 찾아서 리턴한다. 
	 * 찾지 못했을 경우 빈문자열을 리턴한다
	 *  
	 * @param columnIndex
	 * @return
	 */
	public String getColumnName(int columnIndex) {
		Column column = getColumn(columnIndex);
		if (column == null)
			return "";
		return column.getName();

	}

	/**
	 * 모든 컬럼 Collection을 리턴한다
	 * @return
	 */
	public Collection<Column> getColumns() {
		return columnHeader.values();
	}

	/**
	 * column의 갯수를 리턴한다
	 * columnCount와 같은 역활을 한다
	 * @return
	 */
	public int getColumnsSize() {
		return columnHeader.size();
	}

	public String getName() {
		return name;
	}

	public List<String> getPkList() {
		return pkList;
	}

	/**
	 * 
	 * @param rowIndex
	 * @return
	 */
	public Row getRow(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < rowList.size()) {
			List<Row> l = new ArrayList<Row>(rowList.values());
			return l.get(rowIndex);
		}
		return null;
	}

	/**
	 * 
	 * @param pkValue
	 * @return
	 */
	public Map<String, Object> getRowAsMap(String pkValue) {
		Row row = findRow(pkValue);
		if (row != null) {
			return row.toMap();
		}
		return null;
	}

	/**
	 * 모든 레코드의 collection을 리턴한다
	 * @return
	 */
	public Collection<Row> getRows() {
		return rowList.values();
	}

	/**
	 * row의 갯수를 리턴한다
	 * @return
	 */
	public int getRowSize() {
		return rowList.size();
	}

	/**
	 * primaryKey값으로 row를 찾아서 리턴한다.
	 * 
	 * @param string
	 * @return
	 */
	public Row getRowWithPrimaryKey(String primaryKey) {
		return rowList.get(primaryKey);
	}

	/**
	 * 테이블을 대상으로 집합함수를 적용하여 결과를 리턴한다<br>
	 * SUM, MIN, MAX, AVG<br>
	 * 주의) 레코드가 없는 경우 MAX나 MIN값은 null을 AVG나 SUM은 0.0을 리턴한다<br>
	 * @param staticName  집합함수명 KedTable.STATIC.SUM  
	 * @param columnName
	 * @return
	 */
	public Double getStaticValue(STATIC staticName, String columnName){
		
		if(getRowSize() == 0 && (staticName == STATIC.MAX||staticName == STATIC.MIN)) return null;
		if(getRowSize() == 0 && (staticName == STATIC.AVG)) return 0.0;

		Double result = 0.0;
		
		switch (staticName) {
			case MAX:{
				result = Double.NEGATIVE_INFINITY;
				for (Row row : getRows()) {
					result = Math.max(result, row.getDouble(columnName));
				}
			}
				break;
			case MIN: {
				result = Double.POSITIVE_INFINITY;
				for (Row row : getRows()) {
					result = Math.min(result, row.getDouble(columnName));
				}				
			}
				break;
			case AVG : {
				Double sum = 0.0; 
				for (Row row : getRows()) {
					sum += row.getDouble(columnName);
				}	
				result = sum / this.getRowSize();
			}
			break;
			case SUM : {
				result = 0.0; 
				for (Row row : getRows()) {
					result += row.getDouble(columnName);
				}	
				
			}
			default:
				break;
		}
		return result;
	}

	/**
	 * 
	 * @param pkString
	 * @param columnName
	 * @return
	 */
	public Table getTable(String pkString, String columnName) {
		Row row = findRow(pkString);
		if (row != null) {
			return (Table) row.getObject(columnName);
		}
		return null;
	}
	public Column.DataType getType(Object value) {

		if (value == null) {
			return DataType.UNKNOWN;
		}
		if (value instanceof String) {
			return DataType.STRING;
		} else if (value instanceof Integer || value instanceof Short
				|| value instanceof Long || value instanceof Double
				|| value instanceof BigInteger) {
			return DataType.NUMBER;
		} else if (value instanceof Date) {
			return DataType.DATE;
		} else if (value instanceof Table) {
			return DataType.KED_TABLE;
		} else {
			return DataType.UNKNOWN;
		}
	}
	/**
	 * 
	 * @param pkValue
	 * @param columnName
	 * @return
	 */
	public Object getValueAsObject(String pkValue, String columnName) {
		Row row = getRowWithPrimaryKey(pkValue);
		if (row == null)
			return null;
		return row.getObject(columnName);
	}

	/**
	 * columnName의 값을 whereConditon조건으로 찾는다. <br>
	 * whereCondition은 KedRowEvaluator의 구문을 따른다.<br>
	 * 해당하는 조건이 없으면 null을 리턴한다.
	 * 
	 * @param columnName
	 * @param whereCondition
	 * @return
	 * @throws EvaluatorException 
	 */
	public Object getValueWithCondition(String columnName, String whereCondition)  {
		Map<String, Object> dataMap =null;
		try {
			Evaluator rowEvaluator = new Evaluator();
			rowEvaluator.setStatement(whereCondition);
			for (Row row : getRows()) {
				dataMap = row.toMap();
				boolean b = rowEvaluator.evalBoolean(dataMap);
				if (b) {
					return row.getObject(columnName);
				}
			}
			
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	/**
	 * columnName의 값을 whereConditon조건으로 찾는다. <br>
	 * whereCondition은 KedRowEvaluator의 구문을 따른다.<br>
	 * 해당하는 조건이 없으면 null을 리턴한다.<br>
	 * 
	 * @param columnName
	 * @param whereCondition
	 * @param defaultValue 조건에 해당하는 값이 존재하지 않을 때의 리턴값
	 * @return
	 */
	public Object getValueWithCondition(String columnName, String whereCondition, Object defaultValue) {
		Object value = getValueWithCondition(columnName, whereCondition);
		if(value != null) return value;
		return defaultValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Column col : columnHeader.values()) {
			result = prime * result + col.hashCode();
		}
		for (Row row : getRows()) {
			result = prime * result + row.hashCode();
		}
		return result;
	}

	/**
	 * t2와 inner join 한다. <br>
	 * join시 joinFields에 기술된 값들이 같은 레코드들만 조인한다.<br>
	 * 새로 생긴 테이블들의 필드명은 a., b.으로 구분하여 담긴다.<br>
	 * 
	 * @param t2 조인할 테이블
	 * @param joinFields 조인에 사용될 column명을 담은 배열
	 * @return
	 * @throws TableException
	 */
	public Table innerJoin(Table t2, String[] joinFields) throws TableException {
		Table newTable = new Table();
		for(Column col : this.getColumns()){
			Column newCol = col.copyTo();
			newCol.setName("a."+ newCol.getName());
			newTable.appendColumn(newCol);
		}	
		for(Column col : t2.getColumns()){
			Column newCol = col.copyTo();
			newCol.setName("b." + newCol.getName());
			newCol.setPrimaryKey(false);
			newTable.appendColumn(newCol);
		}
//		System.out.println(newTable.toString());
		String[] values = new String[joinFields.length];
		for (Row row : getRows()){
			for (int i = 0; i < joinFields.length; i++) {
				values[i] = row.getString(joinFields[i]);
			}
			for(Row row2 : t2.getRows()){
				boolean match = true;
				for (int i = 0; i < joinFields.length; i++) {
					if(values[i].equals(row2.getString(joinFields[i])) == false){
						match = false;
					}
				}
				if(match == true){ //레코드 추가
					Row newRow = newTable.newRow();
					int i = 0;
					for(i = 0; i < this.getColumnsSize(); i++){
						newRow.setValue(i, row.getObject(i), getColumn(i).getDataType());
					}
					int k = i-1;
					for(int j = 0; j < t2.getColumnsSize() ; j++){
						newRow.setValue(j+k, row2.getObject(j), t2.getColumn(j).getDataType());
					}
					newTable.appendRow(newRow);
				}
			}
			
		}
		return newTable;
	}
	public boolean isDefaultTrim() {
		return defaultTrim;
	}

	/**
	 * 새로운 row를 만들어 추가한다.
	 * 자동증가 옵션이 true이면 새로 만들어진 row의 pk는 자동적으로 번호가 붙는다. 
	 * @return 새로만들어진 KedRow
	 */
	public Row newRow() {
		Row newRow = new Row();
		newRow.setMyParentTable(this);
		newRow.autoIncrement();
		return newRow;
	}

	/**
	 * 테이블 스키마를 복제한다
	 * 내용은 복제되지 않는다
	 * @param t
	 * @return
	 */
	public Table newTableWithMySchema() {
		Table newTable = new Table();
		String myName = name;
		newTable.setName("copyed_from_" + myName);
		for (Column column : columnHeader.values()) {
			newTable.appendColumn(column);
		}
		return newTable;
	}

	/**
	 * 모든 row를 지운다
	 */
	public void removeAllRows() {
		for (Row row : getRows()) {
			rowList.remove(row.getPkString());
		}
	}

	public void removeDeletedRows() {
		List<Row> deletedRows = new ArrayList<Row>();
		for (Row row : getRows()) {
			if (row.isDeleted()) {
				deletedRows.add(row);
			}
		}
		for (Row row : deletedRows) {
			removeRow(row);
		}
	}

	/**
	 * row를 지운다
	 * 
	 * @param row
	 */
	public boolean removeRow(Row row) {
		String pk = getPkString(row);
		return (rowList.remove(pk) != null);
	}

	/**
	 * column명 배열들의 값들이 모두 같으면 1개의 레코드만 남기고 지운 테이블을 리턴한다.<br>
	 * 새로운 테이블을 만들어 리턴한다<br>
	 * @param columnNames
	 * @return
	 */
	public Table removeRowsWithSameValues(String[] columnNames) {
		
		Table newTable = newTableWithMySchema();
		try {
			Set<Integer> set = new HashSet<Integer>();
			for (Row row : this.getRows()) {
				Map<String, Object> map = row.toMap();
				String value = "";
				int hashcode = -1;
				for (String columnName : columnNames) {
					value += row.getString(columnName);
					hashcode = value.hashCode();
				}
				if(!set.contains(hashcode)){
					Row newRow = newTable.newRow();
					newRow.setFromMap(map);
					newTable.appendRow(newRow);	
					set.add(hashcode);
				}
			}
		} catch (Exception e) {
			newTable = null;
		}
		return newTable;
	}
	/**
	 * 주어진 조건으로 테이블의 row를 골라서 새로운 테이블로 리턴한다<br>
	 * 원래의 테이블은 그대로 존재한다<br>
	 * @param whereCondition
	 * @return
	 * @throws EvaluatorException 
	 * @throws TableException 
	 */
	public Table select(String whereCondition) throws EvaluatorException, TableException {
		Table newTable = this.newTableWithMySchema();

		Evaluator rowEvaluator = new Evaluator();
		rowEvaluator.setDebug(false);
		rowEvaluator.setStatement(whereCondition);
		
		Map<String,Object> dataMap = null;
		for (Row row : getRows()) {
			dataMap = row.toMap();

			boolean b = rowEvaluator.evalBoolean(dataMap);
			if (b) {
				Row newRow = newRow();
				newRow.setFromMap(dataMap);
				newTable.appendRow(newRow);
			}
		}
		return newTable;
	}

	/**
	 * in 조건을 사용하여 레코드들 골라내어 새로운 테이블로 만든다 <br>
	 * columnName의 값이  collection에 포함되어 있고 condition을 만족하는  레코드들로 새로운 테이블을 만들어서 리턴한다<br>
	 * condition 이 null 또는 비어 있는 문자열일 경우 condition 조건은 무효화 하면 레코드를 골라낸다 <br>
	 *  
	 * @param columnName : 컬럼명, 이 컬럼명의  값이 collection에 있을 때 포함된다 
	 * @param collection : 레코드를 골라내는 조건 
	 * @param condition  : KedRowEvaluator의 구문을 따른다.
	 * @return
	 * @throws TableException 
	 * @throws EvaluatorException 
	 */
	public <T> Table selectIn(String columnName, Collection<T> collection, String condition) throws TableException, EvaluatorException {
		Table newTable = this.newTableWithMySchema();

		Map<String,Object> dataMap = null;
		for (Row row : getRows()) {
			dataMap = row.toMap();
			Object o = row.getObject(columnName);
			if (collection.contains(o)) {
				
				Row newRow = newRow();
				newRow.setFromMap(dataMap);
				newTable.appendRow(newRow);
			}
		}
		if(condition != null && condition.length() > 0){
			return newTable.select(condition);
		}
		return newTable;
	}
	/**
	 * 테이블에서 최고값 또는 최저값을 갖는 레코드들을 골라낸다.<br>
	 * filterCondition null또는 비어 있는 문자열이 아니라면 filterConditon으로 골라낸 후에 <br>
	 * 최고값 또는 최저값을 찾아서 그 값을 갖는 레코드들로 새로운 테이블을 만들어서 리턴한다
	 * @param columnName
	 * @param staticName
	 * @param filterCondition
	 * @return
	 * @throws TableException 
	 * @throws EvaluatorException 
	 */
	public Table selectWithStatic(String columnName, STATIC staticName, String filterCondition) throws EvaluatorException, TableException {
		
		Table tmp = null;
		if(filterCondition != null && filterCondition.length() > 0 ){
			tmp = select(filterCondition);
		}else{
			tmp = this;
		}
		Double staticValue = tmp.getStaticValue(staticName, columnName);
		if(staticValue == null) return newTableWithMySchema();
		
		return tmp.select(columnName +" == " + staticValue);
	}
	
	/**
	 * 
	 * 
	 * @throws TableException
	 */
	public void setAutoIncrement() throws TableException {
		if (pkList != null && pkList.size() > 0) {
			throw new TableException("pk column is already defined");
		}
		Column pkColumn = new Column("RowNum", DataType.NUMBER, 0, true, true);
		pkColumn.setAutoIncrement();
		appendColumn(pkColumn);
	}

	public void setDefaultTrim(boolean defaultTrim) {
		this.defaultTrim = defaultTrim;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * whereConditon조건에 해당하는 레코드들을 찾아서 columnName의 값을 value로 바꾼다. <br>
	 * whereCondition은 KedRowEvaluator의 구문을 따른다.<br>
	 * @param columnName
	 * @param whereCondition
	 */
	public void setValueWithCondition(String columnName, String whereCondition, Object value) {
		Map<String, Object> dataMap =null;
		try {
			Evaluator rowEvaluator = new Evaluator();
			rowEvaluator.setStatement(whereCondition);
			for (Row row : getRows()) {
				dataMap = row.toMap();
				boolean b = rowEvaluator.evalBoolean(dataMap);
				if (b) {
					row.setValue(columnName, value);
				}
			}
			
		} catch (Exception e) {
			;
		}
	}

	
	/**
	 * 테이블의 row를 columnName으로 sorting한다<br>
	 * 테이블 자체를 소팅한다<br>
	 * 참고 orderBy<br> 
	 * @param columnName
	 */
	public void sort(String columnName, ORDER order) {
		Column nc = getColumn(columnName);
		if (nc == null){
			return;
		}
		List<Map.Entry<String, Row>> rows = new ArrayList<Map.Entry<String, Row>>(rowList.entrySet());
		RowComparator rowSorter = new RowComparator(columnName, order);
		
		Collections.sort(rows, rowSorter);
		LinkedHashMap<String, Row> sortedMap = new LinkedHashMap<String, Row>();
		for (Entry<String, Row> row : rows) {
			sortedMap.put(row.getKey(), row.getValue());
		}
		rowList = sortedMap;
	}

	
	/**
	 * columnNames배열의 컬럼들의 값이 같은 것들 끼리 테이블을  나누어서 
	 * 그 테이블들을 배열 만든다.
	 * 
	 * @return Table[]
	 */
	public Table[] split(String[] columnNames) {
		Map<String, Table> tableMap = new HashMap<String, Table>();
		for (Row row : getRows()) {
			String key = "";
			for (String name : columnNames) {
				key += row.getString(name);
			}
			if (tableMap.containsKey(key) == false) {
				Table newT = this.newTableWithMySchema();
				newT.appendRow(row);
				tableMap.put(key, newT);
			} else {
				tableMap.get(key).appendRow(row);
			}
		}
		return (Table[]) tableMap.values().toArray(new Table[tableMap.size()]);
	}

	/**
	 * whereConditon에 해당하는 레코드들에서 columnName에 해당하는 값을 배열로 리턴한다.<br>
	 * 테이블의 순서대로 배열의 값들이 생성된다 <br>
	 * whereConditon은 비어 있을 수 없으며 모든 레코드들을 대상으로 할 때는 1==1 또는 true로 하면 됨<br>
	 * example : kedTable.toArray("field1", "1==1")	; 
	 * 뽑아서 리턴한다
	 * @param columnName
	 * @param whereCondition
	 * @return
	 * @throws TableException 
	 */
	public String[] toArray(String columnName, String whereCondition) throws TableException {
		List<String> resultList = new ArrayList<String>();
		Map<String, Object> dataMap =null;
		try {
			Evaluator rowEvaluator = new Evaluator();
			rowEvaluator.setStatement(whereCondition);
			for (Row row : getRows()) {
				dataMap = row.toMap();
				boolean b = rowEvaluator.evalBoolean(dataMap);
				if (b) {
					resultList.add(row.getString(columnName));
				}
			}
			return resultList.toArray(new String[resultList.size()]);
			
		} catch (Exception e) {
			
			throw new TableException(e.getMessage());
		}
	}

	/**
	 * 
	 * @return
	 */
	public String toHtml() {
		StringBuilder sb = new StringBuilder();
		// head
		sb.append(String.format("<span id='span_%s'>%s</span>", name, name));
		sb.append(String.format("<table id='tbl_%s' border=1>", name));
		sb.append("<tr>");
		for (Map.Entry<String, Column> entry : columnHeader.entrySet()) {
			Column c = entry.getValue();
			if (c.getDesc().length() > 0) {
				sb.append(String.format("<td>%s<br/>(%s)</td>", c.getName(), c.getDesc()));
			} else {
				sb.append(String.format("<td>%s</td>", c.getName()));
			}
		}
		sb.append("</tr>");
		for (Map.Entry<String, Row> entry : rowList.entrySet()) {
			Row row = entry.getValue();
			for (int i = 0; i < columnHeader.size(); i++) {
				sb.append(String.format("<td>%s</td>", row.getString(i)));
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	/**
	 * 나중에 지울것
	 * @return
	 */
	public String toHtml2() {
		StringBuilder sb = new StringBuilder();
		// head
		sb.append(String.format("<span id='span_%s'>%s</span>", name, name));
		sb.append(String.format("<table id='tbl_%s' border=1>", name));
		for (Map.Entry<String, Column> entry : columnHeader.entrySet()) {
			sb.append("<tr>");
			Column c = entry.getValue();
			if (c.getDesc().length() > 0) {
				sb.append(String.format("<td>%s<br/>(%s)</td>", c.getName(), c.getDesc()));
			} else {
				sb.append(String.format("<td>%s</td>", c.getName()));
			}
			if (rowList.size() > 0) {
				Row row = getRow(0);
				sb.append(String.format("<td>%s</td>", row.getString(c.getName())));
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name:" + name);
		sb.append(String.format(", column count: %d, row count : %d", getColumnsSize(), getRowSize()));
		sb.append("\n");
		// head
		for (Map.Entry<String, Column> entry : columnHeader.entrySet()) {
			Column c = entry.getValue();
			if (c.getDesc() != null && c.getDesc().length() > 0) {
				sb.append(String.format("%s(%s)|", c.getName(), c.getDesc()));
			} else {
				sb.append(String.format("%s|", c.getName()));
			}
		}
		if (sb.toString().endsWith("|")) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("\n");
		for (Map.Entry<String, Row> entry : rowList.entrySet()) {
			Row row = entry.getValue();
			sb.append(row.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * whereCondition에 맞는 레코드의 갯수를 리턴한다
	 * @param whereCondition
	 * @return
	 * @throws EvaluatorException 
	 */
	public int countOf(String whereCondition) throws EvaluatorException {

		Evaluator rowEvaluator = new Evaluator();
		rowEvaluator.setDebug(false);
		rowEvaluator.setStatement(whereCondition);
		int  count = 0;
		Map<String,Object> dataMap = null;
		for (Row row : getRows()) {
			dataMap = row.toMap();
			boolean b = rowEvaluator.evalBoolean(dataMap);
			if (b) {
				count++;
			}
		}
		return count;
	}
}
