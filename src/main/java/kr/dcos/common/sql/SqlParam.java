package kr.dcos.common.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import kr.dcos.common.utils.PrintMap;

/**
 * SqlExecutor에서 수행 인자로 넘길때의 Parameter Class <br>
 * SqlPicker가 #para# 를 해석할 때사용한다<br>
 * 
 * @author Kim Do Young
 *
 */
public class SqlParam  {
	private Map<String,Object> map;
	
	public SqlParam(){
		this(null);
	}
	public <V> SqlParam(Map<String, V> map) {
		this.map = new HashMap<String, Object>();
		putAll(map);
	}
	
	public <V> void putAll(Map<String, V> map){
		if (map != null) {
			for (Entry<String, V> entry : map.entrySet()) {
				this.map.put(entry.getKey(), entry.getValue());
			}
		}
	}
	public void put(String key,Object object){
		map.put(key,object);
	}

	public Object get(String key){
		return map.get(key);
	}
	
	public Map<String,Object> getMap(){
		return map;
	}
	public int size() {
		return map.size();
	}
	@Override
	public String toString(){
		return new PrintMap<String,Object>(map).toString();
	}

}
