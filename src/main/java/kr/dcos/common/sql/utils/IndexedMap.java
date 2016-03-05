package kr.dcos.common.sql.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap에서 index로 찾기를 지원하기 위한 Map
 *  
 * @author Kim Do Young
 *
 * @param <K>
 * @param <V>
 */
public class IndexedMap<K,V> extends HashMap<K,V>{

	private Map<Integer,K> indexMap = new HashMap<Integer,K>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 8576175136675337797L;
	private int currentIndex = 0;

	@Override
	public V put(K key, V val) {
		super.put(key, val);
		indexMap.put(currentIndex++, key);
		return val;
	}
	public V add(K key, V val) {
		return put(key,val);
	}
	public void setByIndex(Integer idx, V val) {
		if(idx>=0 && idx < currentIndex){
			K key = indexMap.get(idx);
			super.put(key,val);
		}
	}
	public V getByIndex(int i) {
		if(indexMap.containsKey(i)){
			return super.get(indexMap.get(i));
		}else{
			return null;
		}
	}
	public void clear(){
		super.clear();
		indexMap.clear();
	}

}