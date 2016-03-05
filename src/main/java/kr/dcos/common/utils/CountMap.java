package kr.dcos.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 동일한 단어의 갯수를 세기 위해서 작성
 * @author Kim Do Young
 *
 */
public class CountMap {
	private Map<String,Integer> map;
	private boolean ignoreCase;
	public CountMap(boolean ignoreCase) {
		map = new HashMap<String,Integer>();
		this.ignoreCase = ignoreCase;
	}
	public CountMap(){
		this(false);
	}
	
	public Map<String,Integer> getMap(){
		return map;
	}
	public void add(String key){
		String key1 = key;
		if(ignoreCase){
			key1 = key.toLowerCase();
		}
		if(map.containsKey(key1)){
			int i = map.get(key1);
			map.put(key1, i+1);
		}else{
			map.put(key1, 1);
		}
	}
	public int get(String key) {
		String key1 = key;
		if(ignoreCase){
			key1 = key.toLowerCase();
		}
		if(map.containsKey(key1)){
			return map.get(key1);
		}
		return -1;
	}
}
