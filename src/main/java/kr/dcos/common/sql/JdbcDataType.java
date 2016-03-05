package kr.dcos.common.sql;

import java.util.HashMap;
import java.util.Map;

public enum JdbcDataType {
	//None,String,Integer,Double,Date
	
	UnKnown("UnKnown"), String("String"),Integer("Integer"),Double("Double"),Date("Date")
   ;
	private final String value;
	private JdbcDataType(String value) {
		this.value = value;
	}
	public String value(){return value;}
	private static final Map<String, JdbcDataType> StringToTypeMap = new HashMap<String, JdbcDataType>();
	static {
	    for (JdbcDataType type : JdbcDataType.values()) {
	        StringToTypeMap.put(type.value, type);
	    }
	}
	public static JdbcDataType fromString(String name) {
		
		JdbcDataType type = StringToTypeMap.get(name);
	    if (type == null){
	    	if(name.equals("int")||name.equals("short")) {
	    		return JdbcDataType.Integer;
	    	}else if(name.equals("float") || name.equals("double")){
	    		return JdbcDataType.Double;
	    	}else if(name.startsWith("java.lang.")){
	    		return JdbcDataType.fromString(name.substring("java.lang.".length()));
	    	}else if(name.startsWith("java.util.")){
	    		return JdbcDataType.fromString(name.substring("java.util.".length()));
	    	}
	        return JdbcDataType.UnKnown;
	    }
	    return type;
	}
	@Override
	public String toString(){
		return this.value();
	}
}
