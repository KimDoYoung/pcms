package kr.dcos.common.utils;

import java.util.List;


public class JavascriptMaker {
	/**
	 * list를 comma로 엮어서 String으로 리턴한다
	 * String이면 ''로 감싼다
	 * @param list
	 * @return
	 */
	private static String join(List<Object> list) {
		StringBuilder sb = new StringBuilder();
		for (Object object : list) {
			if (object instanceof String){
				sb.append("'"+object+"',");
			}else{
				sb.append(object.toString()+",");
			}
		}
		String s = sb.toString();
		if(s.length()>0){
			return s.substring(0,s.length()-1);
		}
		return "";	
	}
	/**
	 * sorterMap으로 javascript의 array변수와 data define 코드를 만들어서 리턴한다
	 * var methodArray = new Array(3);
	 * methodArray['abc'] = new Array('a','b','c');
	 * methodArray['def'] = new Array('d','e','f');
	 * ajax로 처리하지 않고 동적으로 데이터를 선택하기 위해서
	 * @param varName
	 * @param sorterMap
	 * @return
	 */
	public static String getArrayDefineCode(String varName, SorterMap sorterMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("var "+varName+" = new Array("+sorterMap.getKeySize()+");\n");
		for (String key : sorterMap.getKeysList()) {
			List<Object> list = sorterMap.getValueList(key);
			String s = join(list);
			sb.append(varName+"['"+ key + "'] = new Array( "+s+");\n");
		}
		return sb.toString();
	}

}
