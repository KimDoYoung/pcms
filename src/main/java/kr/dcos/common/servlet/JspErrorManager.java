package kr.dcos.common.servlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Jsp(View)에 넘겨줄 에러메세지를 가지고 있는 클래스 <br>
 * Key와 index두가지로 접근가능하다<br>
 * <pre>
 * 예) boardManager.error.get("name"); <br>
 *    for(int i=0;i<boardManager.error.size();i++){ <br>
 *    	display(boardManager.error.get(i)); <br>
 *    } <br>
 * </pre>
 *    
 * @author Kim Do Young
 *
 */
public class JspErrorManager {

	private class ErrorItem {
		String key;
		String message;
		public ErrorItem(String key,String message){
			this.key=key;
			this.message = message;
		}
	}
	private List<ErrorItem> list =null;

	public JspErrorManager(){
		list = new ArrayList<ErrorItem>();
	}
	
	public  int size() {
		return list.size();
	}
	public void clear(){
		list.clear();
	}
	public void add(String key,String message){
		if(message == null || message.length()<1) return;
		list.add(new ErrorItem(key,message));
	}
	public void add(String key,String[] errors){
		if(errors==null)return;
		for (String error : errors) {
			list.add(new ErrorItem(key,error));
		}
	}
	public String get(int index){
		if(index>=0 && index < list.size()){
			return list.get(index).message;
		}
		return "";
	}
	public String get(String key){
		for (ErrorItem item : list) {
			if(item.key.equalsIgnoreCase(key)){
				return item.message;
			}
		}
		return "";
	}
	public List<String> getList(){
		List<String> errlist = new ArrayList<String>();
		for (ErrorItem item : list) {
			errlist.add(item.message);
		}
		return errlist;
	}
	public boolean getHasError(){
		return hasError();
	}
	public boolean hasError() {
		return (size()> 0);
	}

}
