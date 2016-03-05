package kr.dcos.common.utils;

/**
 * 데이터형을 변환하는 유틸리티
 * @author Kim Do Young
 *
 */
public class ConvertUtil {
	
	public static Integer toInteger(Object o){
		if(o == null) {
			return null;
		}
		if(o instanceof String){
			return Integer.parseInt(o.toString());
		}else {
			return Integer.parseInt(o.toString());
		}
	}
	
	public static Integer toInteger(Object o,Integer defaultValue){
		if(o == null) {
			return defaultValue;
		}
		if(o instanceof String){
			return Integer.parseInt(o.toString());
		}else {
			return Integer.parseInt(o.toString());
		}
	}
	
	public static Boolean toBoolean(String boolString, boolean b) {
		if(boolString == null) { return b;}
		Boolean bool = Boolean.parseBoolean(boolString);
		return bool;
	}
	/**
	 * object를 double로 변경한다. 
	 * @param object
	 * @return
	 */
	public static Double toDouble(Object object) {
		if(object == null) return null;
		return Double.valueOf(object.toString());
	}
	public static Double toDouble(Object object,Double defaultValue) {
		if(object == null) return defaultValue;
		return Double.valueOf(object.toString());
	}
	
	public static String toString(Object o) {
		if(o == null) return "";
		
		return o.toString();
	}
}
