package kr.dcos.common.utils.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.dcos.common.sql.utils.IndexedMap;
import kr.dcos.common.utils.table.Column.DataType;


/**
 * 테이블의 1개 row를 모방한다 
 * <br>여러개의 column을 가지고 있다
 * <br>값을 찾을 때 DB에서와 같이 대소문자를 구분하지 않는다
 * @author Kim,Do Young
 *
 */
public class Row {
	private boolean isDeleted;
	private Table myParentTable;
	private  Map<String,Object> map;
	
	public Row(){
//		this.map = new HashMap<String,Object>();
		this.map = new IndexedMap<String, Object>();
		this.isDeleted = false;
	}
	
	public void autoIncrement()  {
		for (Column column : myParentTable.getColumns()) {
		    if(column.isAutoIncrement()){
		    	try {
					setValue(column.getName(), column.getNextRowNum());
					column.incNextRowNum();
				} catch (TableException e) {
					;
				}
		    }
		}
	}

	public int columnSize() {
		return myParentTable.getColumnsSize();
	}

	public Double getDouble(String columnName) {
		return getDouble(columnName,null);
	}
	public Double getDouble(String columnName, Double defaultValue) {
		Object value = getObject(columnName);
		if(value == null) { 
			return defaultValue;
		}
		if (value instanceof Double){
			return (Double)value;
		}else {
			String s = value.toString();
			return Double.parseDouble(s);
		}
	}
	public int getInteger(String name) throws TableException {
		String value = getString(name);
		try{
			if(value.equals("0.0")) return 0;
			return Double.valueOf(value).intValue();
		}catch(NumberFormatException e){
			throw new TableException(e.getMessage());
		}
	}
	public int getInteger(String name, int defaultIntValue) {
		try {
			String value = getString(name);
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultIntValue;
		}
	}
	/**
	 * name필드에 저장된 값을 Long형으로 리턴한다.
	 *  
	 * @param name
	 * @return long형으로 변환된 값
	 * @throws TableException Long형으로 변환에 실패했었을 때 발생
	 */
	public long getLong(String name) throws TableException {
		String value = getString(name);
		try {
			if(value.endsWith(".0")){
				value = value.substring(0,value.indexOf('.'));
			}
			return Long.parseLong(value);
		} catch (Exception e) {
			throw new TableException(e.getMessage());
		}
	}
	/**
	 * 
	 * name에 해당하는 값을 구해서 Long형으로 리턴한다.<br>
	 * Long형으로 변환에 실패했을 때 defaultValue를 리턴한다
	 * {@link #getLong(String)}
	 * @param name
	 * @param defaultValue
	 * @return long형으로 변환 된 값
	 */
	public long getLong(String name, Long defaultValue){
		try {
			return getLong(name);
		} catch (TableException e) {
			return defaultValue;
		}
	}
	public Table getMyParentTable() {
		return myParentTable;
	}	
	/**
	 * column순으로 값들을 가져와서 Collection으로 만들어서 리턴한다.
	 * @return
	 */
	public Collection<Object> getObjects(){
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < myParentTable.getColumnsSize(); i++) {
			list.add(getObject(i));
		}
		return list;
	}
	/**
	 * 컬럼명 name의 값을 Object형태로 리턴한다. 
	 * @param name
	 * @return Object,  null 일경우 null을 그대로 리턴한다 
	 */
	public Object getObject(String name){
		String key = name.toLowerCase();
		if(map.containsKey(key)){
			return map.get(key);
		}else{
			return null;
		}
	}
	public Object getObject(int columnIndex){
		String name = myParentTable.getColumnName(columnIndex);
		return getObject(name);
	}
	
	/**
	 * pkString을 리턴한다
	 * @return String
	 */
	public String getPkString() {
		String key = "";
		for (Column column : myParentTable.getColumns()) {
		    if(column.isPrimaryKey()){
		    	key += getString(column.getName());
		    }
		}
		return key;
	}

	/**
	 * <pre>
	 * columnIndex에 해당하는 컬럼의 값을 문자열 형태로 리턴한다<br>
	 * 값이 null일 경우 ""을 리턴한다
	 * </pre>
	 * @param columnIndex
	 * @return columnIndex에 해당하는 값 
	 */
	public String getString(int columnIndex){
		String name = myParentTable.getColumnName(columnIndex);
		return getString(name);
	}
	/**
	 * 
	 * columnIndex에 해당하는 값을 문자열로 리턴한다.<br> 
	 * 값이 null또는 ""라면  defaultValue를 리턴한다<br>
	 * @param columnIndex
	 * @param defaultValue
	 * @return 문자열형태의 값
	 */
	public String getString(int columnIndex, String defaultValue) {
		String s = getString(columnIndex);
		if(s.length() < 1){
			return defaultValue;
		}
		return s;
	}
	/**
	 * columnName에 해당하는 값을 리턴한다 <br>
	 * 없다면 null을 리턴하지 않고 ""을 리턴한다
	 * @param columnName
	 * @return String
	 */
	public String getString(String columnName){
		return getString(columnName,"");
	}
	/**
	 * 없다면 null을 리턴하지 않고 ""을 리턴한다
	 * @param columnName
	 * @return String
	 */
	public String getString(String columnName, String defaultValue) {
		Column nc = myParentTable.getColumn(columnName);
		if(nc == null) return "";
		
		String key = nc.getName(); // columnName.toLowerCase();
		if(map.containsKey(key)){
			Object value = map.get(key);
			if(value == null){
				if(defaultValue == null || defaultValue.toString().length() < 1){
					//column자체에 default값이 있으면 그값을 리턴한다
					Object o = nc.getDefaultValue();
					if(o != null){
						if(myParentTable.isDefaultTrim()){
							return o.toString().trim();
						}else{
							return o.toString();
						}
					}
				}
				return defaultValue;
			}else{
				if(myParentTable.isDefaultTrim()){
					return map.get(key).toString().trim();
				}else{
					return map.get(key).toString();
				}
			}
		}else{
			//인자로 받는것이 우선한다
			if(defaultValue == null || defaultValue.toString().length() < 1){
				Object o = nc.getDefaultValue();
				if(o != null){
					if(myParentTable.isDefaultTrim()){
						return o.toString().trim();
					}else{
						return o.toString();
					}
				}
			}
			return defaultValue;
		}
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	/**
	 * map으로 부터 데이터를 가져와서 row를 채운다.<br>
	 * ifFieldValueIsNull 이 true인 경우 row의 각 필드값이 null인 경우에만 넣는다<br>
	 * ifFieldValueIsNull 이 false이면 row값이 이미 존재하여도 값을 map의 값으로 치환한다<br>
	 * @{link KedTable}  
	 * @param map
	 * @param ifFieldValueIsNull
	 * @throws TableException 
	 */
	public void setDataFromMap(Map<String, Object> map, boolean ifFieldValueIsNull) throws TableException {
		for (Map.Entry<String,Object> entry : map.entrySet()) {
			String fieldName = entry.getKey();
			Object fieldValue = entry.getValue();
			//필드가 존재하고
			if(myParentTable.getColumn(fieldName) != null){
				if(ifFieldValueIsNull){ //null인경우에만 넣는다
					if(getObject(fieldName)==null){
						setValue(fieldName, fieldValue);
					}
				}else{//현재 값이 있건없건 그냥 치환한다
					setValue(fieldName, fieldValue);
				}
			}
		}
		
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * map의 키를 column명으로 하여  값을 채운다
	 * rownum은 배제한다
	 * @param map
	 * @throws TableException 
	 * @throws TableException 
	 */
	public void setFromMap(Map<String, Object> map) throws TableException {
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			if (key.equalsIgnoreCase("rownum"))
				continue;
			Object value = map.get(key);
			setValue(key, value);
		}
	}

	public void setMyParentTable(Table myParentTable) {
		this.myParentTable = myParentTable;
	}
	
	public Row setValue(int index, Object value) throws TableException {
		String columnName = myParentTable.getColumnName(index);
		return setValue(columnName,value, myParentTable.getType(value));
	}
	public Row setValue(int index, Object value, Column.DataType dataType) throws TableException{
		String name = myParentTable.getColumnName(index);
		return setValue(name, value, dataType);
	}

	public Row setValue(String name, Object value) throws TableException {
		return setValue(name,value, myParentTable.getType(value));
	}

	/**
	 * 
	 * 컬럼명 name에 해당하는 컬럼에 값을 대입한다<br>
	 * 컬럼명 name이 존재하지않는다면 컬럼을 생성하고 값을 넣는다.<br>
	 * 또한 컬럼명이 다른 이름 matchFunction이라면 원래의 컬럼명에 넣는다<br>
	 * @param name
	 * @param value
	 * @param dataType
	 * @return
	 * @throws TableException
	 */
	public Row setValue(String name, Object value, Column.DataType dataType) throws TableException{
		//컬럼이 없으면 추가한다.
		Column nc = myParentTable.getColumn(name);
		if(nc == null){
//			if(myParentTable.getColumn(name) == null){
			nc = new Column(name,dataType,0,false,false);
			myParentTable.appendColumn(nc);
		}
		//있으면 그냥 넣는다.
		Object object = value;
		if(value != null && value instanceof String && dataType != DataType.STRING){
			if(dataType == DataType.NUMBER ){				
				object = Double.parseDouble(value.toString());
			}
			//TODO 좀더 정밀하게 수정할 것
		}
		map.put(nc.getName().toLowerCase(), object);
		return this;
	}

	/**
	 * columnIndex에 해당하는 컬럼에 값이 null일 경우에만 인자로 받은 값 value를 넣는다.
	 * @param columnIndex
	 * @param value
	 * @throws TableException
	 */
	public void setValueIfNull(int columnIndex, Object value) throws TableException {
		String name = myParentTable.getColumnName(columnIndex);
		Object o = getObject(name);
		if(o == null){
			setValue(name,value);
		}
	}


	public void setValueIfNull(String columnName, Object value) throws TableException {
		Object o = getObject(columnName);
		if(o == null){
			setValue(columnName,value);
		}
	}

	/**
	 * valueString을 delimeter로 잘라서 넣는다. <br>
	 * 값들은 trim 되어서 들어간다
	 * valueString은 컬럼의 순서대로 값이 존재해야한다.<br>
	 * 
	 * @param valueString
	 * @param delimeter
	 * @throws TableException 
	 */
	public void setValues(String valueString, String delimeter) throws TableException {
		String[] values = valueString.split("\\s*["+delimeter+"]\\s*", -1);
		for (int i = 0; i < values.length; i++) {
			Column.DataType dataType = myParentTable.getColumn(i).getDataType();
			if(myParentTable.getColumn(0).getName().equals("rownum")){
				this.setValue(i+1, values[i], dataType);
			}else{
				this.setValue(i, values[i], dataType);
			}
		}
		autoIncrement();
	}

	/**
	 * <p>row의 모든 내용을 map으로 만들어서 리턴한다.
	 * <p>key는 column명이고 value는 Object형태이다.
	 * @return Map<String,Object>
	 */
	public Map<String, Object> toMap() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		for (Column column : myParentTable.getColumns()) {
			hashMap.put(column.getName(), getObject(column.getName()));
		}
		return hashMap;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Row other = (Row) obj;
		return toString().equals(other.toString());
	}
	@Override 
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (Column column : myParentTable.getColumns()) {
			String value = getString(column.getName());
			sb.append(value+"|");
		} 
		if(sb.toString().endsWith("|")){
			return sb.toString().substring(0,sb.toString().length()-1);
		}
		return sb.toString();
	}

}
