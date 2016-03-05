package kr.dcos.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 한개의 리스트를 특정 키값으로 나누어서 List를 가지고 있는 리스트를 만든다
 * 1,a,b,c
 * 1,d,e,f
 * 2,a,b,c
 * 2,d,e,f
 * 2,g,h,i 와 같을 경우 1->list<map>, 2->list<map> 과 같은 구조로 만든다.
 * 
 * GroupList를 multi column으로 분할 할 수 있도록 수정함
 * 
 * @author Kim,Do Young
 *
 */
public class GroupList {
	
	private Map<String,Object> cashMap;
	private List< ArrayList< HashMap<String,Object> > > list ;

	public GroupList(List<HashMap<String, Object>> orgList, String[] fieldNameArray) {
		
		list = new ArrayList<ArrayList<HashMap<String,Object>>>();
		cashMap = new HashMap<String,Object>();
		
		for (HashMap<String, Object> orgMap : orgList) {
			String nameValue = "";
			for (String fieldName : fieldNameArray) {
				nameValue += String.valueOf( orgMap.get(fieldName) );
			}
			ArrayList<HashMap<String,Object>> foundList = getGroup(nameValue);
			if(foundList != null){
				foundList.add(orgMap);
			}else{
				ArrayList< HashMap<String,Object> > newList = new ArrayList<HashMap<String,Object>>();
				newList.add(orgMap);
				cashMap.put(nameValue, newList);
				list.add(newList);
			}
		}

	}

	public GroupList(List<HashMap<String,Object>> orgList,String fieldName){
		this(orgList,new String[]{fieldName});
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String,Object>> getGroup(String nameValue){
		if(cashMap.containsKey(nameValue)){
			return (ArrayList<HashMap<String, Object>>) cashMap.get(nameValue);
		}
		return null;
	}
	public int size() {
		return list.size();
	}
	public ArrayList<HashMap<String,Object>> get(int index) {
		if(index < 0 || index >= size()){
			return null;
		}
		return list.get(index);
	}
}
