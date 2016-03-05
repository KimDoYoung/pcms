package kr.dcos.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * keyValue HTTP param으로 넘어오는 인자를 해석하여 key value Map으로 만들어 준다
 * 
 * @author Kim Do Young
 * 
 */
public class KeyValueMap {
	private Map<String, String> map;
	
	public KeyValueMap() {
		this(null);
	}
	public KeyValueMap(String keyvalue) {
		map = new HashMap<String, String>();
		parse(keyvalue);
	}
	
	private void parse(String keyvalue) {
		
		if(keyvalue==null || keyvalue.length()<1) return;
		
		String str = keyvalue.trim();
		String[] list = str.split("&");
		for (String s : list) {
			String[] kv = s.split("=");
			if(kv.length == 2){
				this.put(kv[0].trim().toUpperCase(), kv[1].trim());
			}else if(kv.length==1){
				this.put(kv[0].trim().toUpperCase(), "");
			}
		}
	}

	public String getString(String key) {
		return map.get(key.toUpperCase());
	}
	public String getString(String key,String defaultString) {
		String v = map.get(key.toUpperCase()).toString();
		if(v == null || v.length() < 1) return defaultString;
		return v;
	}

	public int getInteger(String key,int defaultInteger){
		String v = map.get(key.toUpperCase()).toString();
		if(v == null || v.length() < 1) return defaultInteger;
		return Integer.parseInt(v);
	}
	public int getInteger(String key){
		String v = map.get(key.toUpperCase()).toString();
		return Integer.parseInt(v);
	}
	public void put(String string, int i) {
		this.put(string, i+"");
	}
	public void put(String key,String value){
		map.put(key.toUpperCase(), value.trim());
	}



}
