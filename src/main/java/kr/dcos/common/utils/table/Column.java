package kr.dcos.common.utils.table;


/**
 * 
 * KedTable을 이루는 컬럼(필드) <br>
 * Data Type 으로 STRING,NUMBER,DATE,KED_TABLE 을 가지고 있다<br>
 * 
 * Primary Key인지 정의할 수 있다. <br>
 * NotNull인지 정의할 수 있다.<br>
 * Alias명을 정의할 수 있다.<br>
 * DefaultValue를 정의할 수 있다.<br>
 * 자동증가 컬럼인지 정의할 수 있다.<br>
 * 
 */
public class Column {
//	public enum DataType { UNKNOWN,STRING,NUMBER,DATE,KED_TABLE };
	//TODO NUMBER을 없앨것
	public enum DataType {
		UNKNOWN, STRING, INTEGER, DOUBLE, DATE, KED_TABLE//, NUMBER
	};
	
	private String name;
	private String desc;
	private DataType dataType;
	private int length;
	private boolean isNotNull;
	private boolean isPrimaryKey;
	private String alias; 
	private boolean isAutoIncrement;
	private int nextRowNum = 0;
	private Object defaultValue;
 
	
	public Column(String columnDefineString) throws TableException{
		//cmp_cd,i,7,false,false
		int pos = columnDefineString.indexOf("//");
		String refine = columnDefineString;
		if(pos > -1){
			refine = columnDefineString.substring(0,pos);
		}
		String [] tmp = refine.trim().split("[\\t,|]");
		if(tmp.length == 5 || tmp.length == 6 ){
			setColumnWithArray(tmp);
		}else{
			throw new TableException("column define string is not valie :"+columnDefineString);
		}
	}
	public Column(String name, DataType dataType, int length, boolean isNotNull,
			boolean isPrimaryKey)throws TableException {
		this(name,dataType,length,isNotNull,isPrimaryKey,"");
	}

	public Column(String name, DataType dataType, int length, boolean isNotNull,
			boolean isPrimaryKey,String desc)throws TableException {
		this.name = name.toLowerCase();
		this.dataType = dataType;
		this.length = length;
		this.isNotNull = isNotNull;
		this.isPrimaryKey = isPrimaryKey;
		this.desc = desc;
		this.isAutoIncrement = false;
		this.defaultValue = null;

	}


	private DataType getDataType(String s) {
		if(s.equalsIgnoreCase("i") || s.equalsIgnoreCase("int") || s.equalsIgnoreCase("integer")){
			return DataType.INTEGER;
		}else if(s.equals("d") || s.equals("double") || s.equals("number")) {
			return DataType.DOUBLE;
		}else if(s.equalsIgnoreCase("s") || s.equalsIgnoreCase("string")) {
			return DataType.STRING;
		}else if( s.equalsIgnoreCase("date")) {
			return DataType.DATE;
		}
		return null;
	}
	
    private void setColumnWithArray(String[] tmp) {
		this.name = tmp[0].trim().toLowerCase();
		this.dataType = getDataType(tmp[1].trim());
		this.length = Integer.parseInt(tmp[2].trim());
		boolean b = false;
		if(tmp[3].trim().replaceAll(" ","").equalsIgnoreCase("notnull")){
			b = true;
		}else{
			b = Boolean.parseBoolean(tmp[3].trim());
		}
		this.isNotNull = b;
		this.isPrimaryKey = Boolean.parseBoolean(tmp[4].trim());
		this.desc="";
		if(tmp.length == 6){
			this.desc = tmp[5].trim();
		}
	}


	/**
	 * clone
	 * @return
	 */
	public Column copyTo() {
		try {
			Column newCol = new Column(this.name,this.dataType,this.length, this.isNotNull, this.isPrimaryKey,this.desc);
			return newCol;
		} catch (TableException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Column other = (Column) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (dataType != other.dataType)
			return false;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (isNotNull != other.isNotNull)
			return false;
		if (isPrimaryKey != other.isPrimaryKey)
			return false;
		if (length != other.length)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public String getAlias() {
		return alias;
	}
	public DataType getDataType() {
		return dataType;
	}
	public Object getDefaultValue() {
		return this.defaultValue;
	}
	public String getDesc() {
		return desc;
	}
	public int getLength() {
		return length;
	}
	public String getName() {
		return name;
	}
	public int getNextRowNum() {
		return nextRowNum;
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	public void incNextRowNum(){
		nextRowNum++;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}
	public boolean isNotNull() {
		return isNotNull;
	}
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public void setAutoIncrement() {
		this.isAutoIncrement = true;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNotNull(boolean notNull) {
		this.isNotNull = notNull;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.isPrimaryKey = primaryKey;
	}
	@Override
	public String toString(){
		return String.format("%s%s %s %d %s %s %s ", 
				name,desc!=null ? "("+desc+")" : "",
				dataType.toString(),length, isNotNull ? "notnull" : "null" , isPrimaryKey ? "PK" : "", alias );
	}
	
}
