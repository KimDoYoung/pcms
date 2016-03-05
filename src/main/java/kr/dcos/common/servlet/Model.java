package kr.dcos.common.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 모델 클래스
 * 
 * @author Kim Do Young
 *
 */
public class Model {
	Map<String, Object> map;
    
    public Model() {
            map = new HashMap<String,Object>();
    }
    
    public Object getAttribute(String name){
    	return map.get(propertyName(name));
    }

	public void addAttribute(String name, Object value) {
		map.put(propertyName(name),value);
	}
	/**
	 * 첫글자를 소문자로 한다
	 * @param name
	 * @return
	 */
	private String propertyName(String name) {
		return name.substring(0,1).toLowerCase()+name.substring(1);
	}

	public Map<String,Object> getMap() {
		return map;
	}
	/**
	 * msg로 시작하는 모든 attribute를 제거한다
	 */
	public void deleteAllMessage() {
		for (Entry<String, Object> entry : map.entrySet()) {
			if(entry.getKey().startsWith("msg")){
				map.remove(entry.getKey());
			}
		}
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : map.entrySet()) {
			sb.append(entry.getKey()+"->"+entry.getValue().toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
