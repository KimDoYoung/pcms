package kr.dcos.common.sql.sqlpicker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 내부 클래스 SqlItem
 * 
 * @author Kim Do Young
 *
 */
public class SqlItem {
	public class SqlLine {
		char mark; //S : source, P : parameter , R: Reference
		String str; //sql 내용
		public SqlLine(char mark,String str){
			this.mark = mark;
			this.str = str;
		}
		public String toString(){
			return String.format("%s : %s",mark,str);
		}
	}
	public enum Method { Latte,Batis}
	
	private String id;
	private String sqlTemplate;
	//private String parameterTypeName;
	private String method;
	


	Pattern pattern = Pattern.compile("#\\w+#|\\$\\w+\\$|@\\w+@");
	
	private List<SqlLine> list;
	//public String resultTypeName;
	private List<String> questionList;
	
	
	public SqlItem(){
		list = new ArrayList<SqlLine>();
		questionList = new ArrayList<String>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}	
	
	public String getSqlTemplate() {
		return sqlTemplate;
	}
	public void setSqlTemplate(String sqlTemplate) {
		this.sqlTemplate = sqlTemplate;
		list.clear();
		split(sqlTemplate);
	}
	
	/**
	 * Batis형태의 sql문장을 
	 * variable 또는 Reference와 sql 문장 그 자체로 잘라내어 list를 채운다
	 * @param sqlTemplate
	 */
	private void split(String sqlTemplate) {

		Matcher matcher = pattern.matcher(sqlTemplate);

		// Check all occurance
		int index = 0;
		while (matcher.find()) {
			String s = sqlTemplate.substring(index, matcher.start());
			list.add(new SqlLine('S', s));

			//s = sqlTemplate.substring(matcher.start(), matcher.end());
			String variable = matcher.group();
			if(variable.startsWith("#")){
				list.add(new SqlLine('P', variable.trim()));
				questionList.add(variable.replaceAll("\\#", "").trim());
			}else if(variable.startsWith("$")){
				list.add(new SqlLine('R', variable.trim()));
			}else if(variable.startsWith("@")){
				list.add(new SqlLine('L', variable.trim()));
			}

			index = matcher.end();
		}
		if (index == 0) { // 발견하지 못함
			list.add(new SqlLine('S', sqlTemplate));
		} else if (index < sqlTemplate.length()) {
			String s = sqlTemplate.substring(index, sqlTemplate.length());
			list.add(new SqlLine('S', s));
		}
	}
	
//	public String getParameterTypeName() {
//		return parameterTypeName;
//	}
//	public void setParameterTypeName(String parameterTypeName) {
//		if(parameterTypeName == null){
//			this.parameterTypeName = "Map";
//		}else{
//			this.parameterTypeName = parameterTypeName;
//		}
//	}

	public List<SqlLine> getList() {
		return list;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+id);
		sb.append(" method:"+method);
		sb.append("\n");
		for (SqlLine line : list) {
			sb.append(line.toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}

	public int preparedNameCount() {
		return questionList.size();
		
	}

	public String getPreparedName(int index) {
		if(index >=0 && index < questionList.size()){
			return questionList.get(index);
		}
		return null;
	}


}