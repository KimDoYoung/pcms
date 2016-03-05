package kr.dcos.common.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * jsp form action Multipart form-data로 보낼때 <br>
 * Map에 일반 데이터를 넣고.<br>
 * List<FileJeongBo> list에 저장한 파일정보를 넣는다<br>
 * 
 * @author Kim Do Young
 *
 */
public class MultipartFormData {
	
	private Map<String,Object> map;
	private List<FileJeongBo> list;
	
	public MultipartFormData(){
		map = new HashMap<String,Object>();
		list = new ArrayList<FileJeongBo>();
	}
	
	private Object getInput(final String name){
		if(map.containsKey(name)){
			return map.get(name);
		}else{
			return null;
		}
	}
	public List<FileJeongBo> getFileJeongBoList(){
		return list;
	}
	public FileJeongBo getFileJeongBo(int index){
		if(index>=0 && index < list.size()){
			return list.get(index);
		}
		return null;
	}
	public FileJeongBo getFileJeongBo(String tagname){
		for (FileJeongBo fj : list) {
			if(fj.getHtmlTagName().equals(tagname)){
				return fj;
			}
		}
		return null;
	}
	/**
	 * 이미 같은 이름으로 있으면 배열로 해서 넣는다
	 * @param propertyName
	 * @param o
	 */
	@SuppressWarnings("unchecked")
	public void addInput(String propertyName, String o) {
		
		Object alreadyValue = getInput(propertyName);
		if(alreadyValue != null){
			if(alreadyValue instanceof String){
				List<String> list =  new ArrayList<String>();
				list.add(alreadyValue.toString());
				list.add(o);
				map.put(propertyName, list);
			}else if(alreadyValue instanceof Collection){
				((List<String>)alreadyValue).add(o);
			}
		}else{
			map.put(propertyName, o);
		}
		
		
	}

	public void addFileJeongBo(FileJeongBo fileJeongBo) {
		list.add(fileJeongBo);
	}
	public int fileCount(){
		return list.size();
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(String propertyName) {
		Object o = getInput(propertyName);
		if(o == null) return Collections.EMPTY_LIST;
		if(o instanceof String){
			List<String> list =  new ArrayList<String>();	
			list.add(o.toString());
			return list;
		}else if (o instanceof Collection){
			return (List<String>)o;
		}
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public String getString(String propertyName) {
		Object o = getInput(propertyName);
		if(o == null) return null;
		if(o instanceof String){
			return o.toString();
		}else if(o instanceof Collection){
			List<String> list = (List<String>)o;
			if(list.size()>0){
				return list.get(0);
			}
			return null;
		}
		return null;
	}


}
