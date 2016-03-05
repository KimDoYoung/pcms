package kr.dcos.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 문자열로 분류해서 리스트를 가지고 있게 한다. <br>
 * A-1        A- 1,2 <br>
 * A-2        B- 1,2,3   <br>
 * B-1  -->  <br>
 * B-2 <br>
 * B-3  <br>
 * 
 * @author Kim Do Young
 *
 */
public class SorterMap {
	private Map<String,List<Object>> map;
	private boolean ignoreCase;
	private CountMap countMap;
	
	public SorterMap(boolean ignoreCase){
		map = new HashMap<String,List<Object>>();
		this.ignoreCase = ignoreCase;
		countMap= new CountMap(ignoreCase);
	}
	public SorterMap(){
		this(false);
	}
	private String getKey(String key) {
		String key1 = key;
		if(ignoreCase){
			key1 = key.toLowerCase();
		}
		return key1;
	}
	
	public void add(String key,Object o){
		String key1 = getKey(key);
		if(map.containsKey(key1)){
			map.get(key1).add(o);
			countMap.add(key1);
		}else{
			List<Object> olist = new ArrayList<Object>();
			olist.add(o);
			map.put(key1, olist);
			countMap.add(key1);
		}
	}

	/**
	 * Key의 갯수
	 * @return
	 */
	public int getKeySize() {
		return map.size();
	}
	public int getValueSize(String key) {
		if(map.containsKey(getKey(key))){
			return map.get(getKey(key)).size();
		}else{
			return 0;
		}
	}
	public List<String> getKeysList() {
		List<String> list = new ArrayList<String>();
		for (String key : map.keySet()) {
			list.add(key);
		}
		return list;
	}
	public List<Object> getValueList(String key){
		return map.get(key);
	}
	public  Map<String,Integer> getCountMap(){
		return countMap.getMap();
	}
	
}
