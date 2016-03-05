package kr.dcos.common.sql.sqlpicker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.dcos.cmslatte.core.CmsLatte;
import kr.dcos.cmslatte.exception.CmsLatteException;
import kr.dcos.cmslatte.field.ArrayField;
import kr.dcos.cmslatte.field.ConstantField;
import kr.dcos.cmslatte.field.FieldChangGo;
import kr.dcos.cmslatte.utils.LatteUtil;
import kr.dcos.common.sql.SqlExecuter;
import kr.dcos.common.sql.exception.SqlPickerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Sql을 가지고 있는 xml파일을 읽어서 id로 찾아온다. <br>
 * 찾아온 sql 속의 변수(#variablenaem#)에<br>
 * parameter객체의 값으로 치환하여 sql을 만들어서 사용할 수 있게 한다<br>
 * SQL저장소에서 parameterType으로 넘겨받은 parameter를 넣어서 완성된 sql을 사용할 수 있게한다<br>
 * 
 * @author Kim Do Young
 *
 */
public class SqlPicker {

	
	private static Logger logger = LoggerFactory.getLogger(SqlPicker.class);
	
	private Map<String,SqlItem> sqlMap ;
	
	//sql-ref latte소스를 가지고 있는 map
	private Map<String,String> refMap;
	
	//사용안함
	private Map<String,String> aliasMap;
	/**
	 * 생성자
	 * @param xmlFileName
	 */
	public SqlPicker(String xmlFileName){
		sqlMap = new HashMap<String,SqlItem>();
		aliasMap = new HashMap<String,String>();
		refMap = new HashMap<String,String>();
		
		if(xmlFileName != null){
			try {
				load(xmlFileName);
			} catch (SqlPickerException e) {
				logger.error(e.getMessage());
			}
		}
	}
	public SqlPicker(){
		this(null);
	}
	/**
	 * 
	 * @param sqlId sqlId
	 * @param paramter sql문장을 만들때의 parameter
	 * @return
	 * @throws SqlPickerException 
	 * @throws Exception 
	 */
	public String sqlString(String sqlId,Object paramter) throws SqlPickerException {
		SqlItem sqlItem = sqlMap.get(sqlId);
		if(sqlItem==null){
			throw new SqlPickerException("sql id " +sqlId + " is not found");
		}
		String sql = "";
		if(sqlItem.getMethod().equals("Latte")){
			sql = createWithLatte(sqlItem,paramter);
		}else {
			sql = createWithBatis(sqlItem,paramter);
		}
		return sql.trim();
	}
	
	public String sqlString(String sqlId) throws SqlPickerException {
		return sqlString(sqlId,null);
	}
	
	/**
	 * sql 중에 <@ @>를 해석한다.
	 * @param sqlItem
	 * @param paramter
	 * @throws SqlPickerException 
	 */
	private String createWithLatte(SqlItem sqlItem, Object paramter) throws SqlPickerException {
		String latteCode = sqlItem.getSqlTemplate();
		String resultSql = translateWithLatte(latteCode,paramter);
		return resultSql;
		
	}
	/**
	 * 분리된 sqlline을 따라가면서 parameterObject의 value로 #var#를 채워서 
	 * ##와 $$만 사용가능하다.
	 * @param item
	 * @param parameterObject
	 * @return
	 * @throws SqlPickerException 
	 */
	@SuppressWarnings("unchecked")
	private String createWithBatis(SqlItem item, Object parameterObject) throws SqlPickerException {
		String[] tmpArray = null; //String을 배열로 사용하기 위해서
		int parameterIndex = 0;
		StringBuilder sb = new StringBuilder();
		for (SqlItem.SqlLine line : item.getList()) {
			if(line.mark == 'S'){
				sb.append(line.str);
			}else if(line.mark == 'P'){ //parameter
				String key = line.str;
				if(parameterObject == null){
					sb.append(key); //
					logger.error("parameter is null, need parameter object");
				}else if(parameterObject instanceof Map){
					Map<String,Object> map = (Map<String,Object>)parameterObject;
					String key2 = key.replaceAll("\\#", "");
					if(map.containsKey(key2)){
						String filteredString = filter(map,key2);
						sb.append(filteredString);
					}else{
						sb.append(key);
					}
				}else if(parameterObject instanceof Integer || parameterObject instanceof Double){
					sb.append(parameterObject.toString());
				}else if(parameterObject instanceof String){
					if(tmpArray == null){
						tmpArray = parameterObject.toString().split(",");
						parameterIndex = 0;
					}
					sb.append("'"+tmpArray[parameterIndex++%tmpArray.length]+"'");
				}else {
					String fieldName = key.replaceAll("\\#","");
					String value = getValueFromClass(parameterObject,fieldName);
					sb.append(value);
				}
			}else if(line.mark == 'R'){ //replace 문자열 대치
				String key = line.str;
				sb.append(getReplaceProcess(key, parameterObject));
			}else if(line.mark == 'L'){ //latte
				//refMap에서 찾아서.
				String key = line.str;
				String key2 = key.replaceAll("\\@", "");
				if(refMap.containsKey(key2)){
					String latteCode = refMap.get(key2); 
					String resultSql = translateWithLatte(latteCode,parameterObject);
					sb.append(resultSql);
				}else{
					sb.append(key);
				}
				
				//latte로 해석한다.
			}
		}
		return sb.toString();
	}
	/**
	 * map에서 찾아서 그 타입에 따라서 문자열을 만든다.
	 * @param map 
	 * @param key2
	 * @return
	 */
	private String filter(Map<String, Object> map, String key) {
		Object o = map.get(key);
		if(o  == null){
			return "null";
		}else if(o instanceof String){
			return "'"+o.toString()+"'";
		}else {
			return o.toString();
		}
	}
	/**
	 * paramterObject를 latte의 변수로 만든다.
	 * latteCode를 latte의 소스로 한다.
	 * latte로 해석하여 결과를 만들어 리턴한다.
	 * 
	 * @param latteCode
	 * @param parameterObject
	 * @return
	 * @throws SqlPickerException 
	 */
	@SuppressWarnings("unchecked")
	private String translateWithLatte(String latteCode, Object parameterObject) throws SqlPickerException {
		FieldChangGo changGo = new FieldChangGo();
		try {
			if(parameterObject != null){
				if(LatteUtil.isPrimitiveType(parameterObject)){ //원시타입
					logger.error("Latte can not accept primitive object as parameter:" + parameterObject.toString());
					throw new SqlPickerException("Latte can not accept primitive object as parameter");
				}else if(parameterObject instanceof Map){ //맵타입
					Map<String,Object> map = (Map<String,Object>)parameterObject;
//					changGo.putFieldWithMap(map);
					for (Entry<String,Object> entry : map.entrySet()) {
						String name = entry.getKey();
						Object obj  = entry.getValue();
						//changGo.putFieldWithMap(map);
						if(LatteUtil.isPrimitiveType(obj)){
							changGo.putField(new ConstantField(entry.getKey(), entry.getValue()));
						}else if(obj instanceof Object[]){
							ArrayField af = new ArrayField(name);
							af.append(obj);
							changGo.putField(af);
						}else if(obj instanceof List){
							ArrayField af = new ArrayField(name);
							af.append(obj);
							changGo.putField(af);							
						}
					}
				}else{ //POJO 타입
					changGo.putFieldsWithObject(parameterObject);
				}
			}
			//else if(LatteUtil.)
			CmsLatte latte = new CmsLatte(changGo);
			return latte.createPage(latteCode.trim());
		} catch (CmsLatteException e) {
			throw new SqlPickerException("code:["+latteCode.trim()+"]->"+ e.getMessage());
		}
	}
	/**
	 * paramterObject에서 propertyName으로 값을 가져온다.
	 * @param paramterObject
	 * @param fieldName
	 * @return
	 * @throws SqlPickerException 
	 */
	private String getValueFromClass(Object paramterObject, String fieldName) throws SqlPickerException {
		 
		Field field =null;
		Object value = null;
		try {
			field = paramterObject.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			value = field.get(paramterObject);
			
		} catch (SecurityException e) {
			throw new SqlPickerException(e.getMessage());
		} catch (NoSuchFieldException e) {
			throw new SqlPickerException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new SqlPickerException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new SqlPickerException(e.getMessage());
		}
		return value.toString();
	}
	public void load(String absFilePath) throws SqlPickerException {
	
		File file = new File(absFilePath);
		load(file);
	}
	/**
	 * xmlFileName의 파일을 읽어서 sqlMap을 채운다.
	 * @param xmlFilePath SQL을 담고 있는 xml형식의 파일
	 * @throws SqlPickerException 
	 */
	public void load(File file) throws SqlPickerException {

		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
		docBuildFact.setCoalescing(true);
		DocumentBuilder docBuild;
		try {
			docBuild = docBuildFact.newDocumentBuilder();
			
			Document doc = docBuild.parse(file);
			doc.getDocumentElement().normalize();
			loadSqls(doc);
			loadAlias(doc);
			loadRef(doc);
		} catch (ParserConfigurationException e) {
			logger.debug("",e);
		} catch (SAXException e) {
			logger.debug("",e);
		} catch (IOException e) {
			logger.debug("",e);
		}		
	}
	/**
	 * <ref id=...>를 해석한다.
	 * refMap을 채운다
	 * @param doc
	 * @throws SqlPickerException 
	 */
	private void loadRef(Document doc) throws SqlPickerException {
		NodeList refList = doc.getElementsByTagName("ref"); // for all in-jar
		for (int i = 0; i < refList.getLength(); i++) {
			Node refNode = refList.item(i);
			if (refNode.getNodeType() != Node.ELEMENT_NODE) continue;
			
			NamedNodeMap attrMap = refNode.getAttributes();
			Node idNode = attrMap.getNamedItem("id");
			//Node methodNode  = attrMap.getNamedItem("method");
			String id = null;
			//String method = null;
			if(idNode != null) {
				id = idNode.getNodeValue();
			}
//			if(methodNode != null){
//				method = methodNode.getNodeValue();
//			}
			String latteCode = null;
			NodeList childNodes = refNode.getChildNodes();
			for(int j=0; j<childNodes.getLength();j++){
				Node n = childNodes.item(j);
				if(n instanceof CharacterData){
		            CharacterData child = (CharacterData)n;
		            latteCode = child.getData();
		        }
			}
			
			if(id != null && latteCode != null){
				addRefItem(id,latteCode );
				
			}
		}
		
	}
	
	private void addRefItem(String id, String latteCode) {
		refMap.put(id, latteCode);
		
	}
	private void loadAlias(Document doc) throws SqlPickerException {
		NodeList aliasList = doc.getElementsByTagName("alias"); 
		for (int i = 0; i < aliasList.getLength(); i++) {
			Node sqlNode = aliasList.item(i);
			if (sqlNode.getNodeType() != Node.ELEMENT_NODE) continue;
			
			NamedNodeMap attrMap = sqlNode.getAttributes();
			Node idNode = attrMap.getNamedItem("name");
			String id = null;
			if(idNode != null) {
				id = idNode.getNodeValue();
			}
			String fullName = null;
			NodeList childNodes = sqlNode.getChildNodes();
			for(int j=0; j<childNodes.getLength();j++){
				Node n = childNodes.item(j);
				if(n instanceof CharacterData){
		            CharacterData child = (CharacterData)n;
		            fullName = child.getData();
		        }
			}
			
			if(id != null && fullName != null){
				addAlias(id, fullName);
				
			}			
		}
		
	}
	private void addAlias(String name, String fullName) throws SqlPickerException {
		if(aliasMap.containsKey(name)){
			throw new SqlPickerException("alias name " + name + " is already exist");
		}
		aliasMap.put(name, fullName);
	}
	private void loadSqls(Document doc) throws SqlPickerException {
		NodeList sqlList = doc.getElementsByTagName("sql"); // for all in-jar
		for (int i = 0; i < sqlList.getLength(); i++) {
			Node sqlNode = sqlList.item(i);
			if (sqlNode.getNodeType() != Node.ELEMENT_NODE) continue;
			
			NamedNodeMap attrMap = sqlNode.getAttributes();
			Node idNode = attrMap.getNamedItem("id");
			Node paraNode  = attrMap.getNamedItem("parameterType");
			Node methodNode = attrMap.getNamedItem("method");
			String id = null;
			String paramterTypeName = null;
			String method = null;
			if(idNode != null) {
				id = idNode.getNodeValue();
			}
			if(paraNode != null){
				paramterTypeName = paraNode.getNodeValue();
			}
			if(methodNode != null){
				method = methodNode.getNodeValue();
				if(method.toUpperCase().equals("LATTE")){
					method = "Latte";
				}else if(method.toUpperCase().equals("BATIS")){
					method = "Batis";
				}else{
					method = "Not Defined";
				}
			}else{
				method = "Not Defined";
			}
			String sql = null;
			NodeList childNodes = sqlNode.getChildNodes();
			for(int j=0; j<childNodes.getLength();j++){
				Node n = childNodes.item(j);
				if(n instanceof CharacterData){
		            CharacterData child = (CharacterData)n;
		            sql = child.getData();
		            if(method.equals("Not Defined")){
		            	method = "Batis";
		            	if(sql.contains("<@") && sql.contains("@>")){
		            		method = "Latte";
		            	}
		            }
		        }
			}
			
			if(id != null && sql != null){
				addSqlItem(id, method, sql);
				
			}
		}
		
	}
	private void addSqlItem(String id, String method, String sql) throws SqlPickerException {
		SqlItem item = new SqlItem();
		item.setId(id);
		//item.setParameterTypeName(paramterTypeName);
		item.setMethod(method);
		item.setSqlTemplate(sql);
		if(sqlMap.containsKey(id)){
			throw new SqlPickerException("sql id " + id + " is already exist");
		}
		sqlMap.put(id, item);
	}
	/**
	 * sql 의 갯수
	 * @return
	 */
	public int size() {
		return  sqlMap.size();
	}
	/**
	 * resourcePath로 부터 파일
	 * @param resourcePath
	 * @throws SqlPickerException 
	 */
	public void loadFromResource(String resourcePath) throws SqlPickerException {
		URL url = SqlExecuter.class.getResource(resourcePath);
		if (url == null){
			return;
		}
		String filePath = url.getFile();		
		//File file = new File(filePath);
		load(filePath);
		
	}
	public static File getFileFromResourcePath(String resourcePath) {
		URL url = SqlExecuter.class.getResource(resourcePath);
		if(url == null) return null;
		return new File(url.getFile());
	}
	/**
	 * sqlId의 sql을 가져와서 prepared statement를 리턴한다.
	 * 1. @@를 해석해서 문자열로 만든다.
	 * 2. $$를 해석해서 문자열로 대치한다.
	 * 3. ##를 ? 로 대치한다
	 * @param sqlId
	 * @param parameter 
	 * @return
	 * @throws SqlPickerException 
	 */
	public String getPreparedStatment(String sqlId, Object parameter) throws SqlPickerException {
		SqlItem sqlItem = sqlMap.get(sqlId);
		if(sqlItem==null){
			throw new SqlPickerException("sql id [" +sqlId + "] is not found");
		}
		String sql = "";
		if(sqlItem.getMethod().equalsIgnoreCase("latte")){
			throw new SqlPickerException("sql id: " +sqlId + "'s method is latte, latte mode can not support prepared statement");
		}else {
			sql = createPreparedStatement(sqlItem,parameter); //##를 listQuestion에 추가한다.
		}
		return sql.trim();
	}
	/**
	 * sqlId에 해당하는 sqlItem을 리턴한다
	 * 발견못했을 경우 null을 리턴한다
	 * @param sqlId
	 * @return
	 */
	public SqlItem getItem(String sqlId) {
		if(sqlMap.containsKey(sqlId)){
			return sqlMap.get(sqlId);
		}
		return null;
	}	
	/**
	 * ##를 ?으로 대치한 sql문장을 만든다.
	 * @param sqlItem
	 * @param parameterObject
	 * @return
	 * @throws SqlPickerException 
	 */
	private String createPreparedStatement(SqlItem sqlItem, Object parameterObject) throws SqlPickerException {
		StringBuilder sb = new StringBuilder();
		for (SqlItem.SqlLine line : sqlItem.getList()) {
			if(line.mark == 'S'){
				sb.append(line.str);
			}else if(line.mark == 'P'){ //parameter
				sb.append(" ? ");
			}else if(line.mark == 'R'){ //replace 문자열 대치
				String key = line.str;
				sb.append(getReplaceProcess(key,parameterObject));
			}else if(line.mark == 'L'){ //latte
				//refMap에서 찾아서.
				String key = line.str;
				String key2 = key.replaceAll("\\@", "");
				if(refMap.containsKey(key2)){
					String latteCode = refMap.get(key2); 
					String resultSql = translateWithLatte(latteCode,parameterObject);
					sb.append(resultSql);
				}else{
					sb.append(key);
				}
				
				//latte로 해석한다.
			}
		}
		return sb.toString();
	}
	/**
	 * $$를 처리해서 결과로 문자열을 리턴한다.
	 * 스트링일 경우 ''를 앞뒤에 붙인다.
	 * @param key
	 * @param parameterObject
	 * @return
	 * @throws SqlPickerException
	 */
	private String getReplaceProcess(String key, Object parameterObject)
			throws SqlPickerException {
		String[] tmpArray = null;
		int parameterIndex = 0;
		if (parameterObject == null) {
			return key;
		} else if (parameterObject instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) parameterObject;
			String key2 = key.replaceAll("\\$", "");
			if (map.containsKey(key2)) {
				return map.get(key2).toString();
			} else {
				return key;
			}
		} else if (parameterObject instanceof Integer
				|| parameterObject instanceof Double) {
			return parameterObject.toString();
		} else if (parameterObject instanceof String) {
			if (tmpArray == null) {
				tmpArray = parameterObject.toString().split(",");
				parameterIndex = 0;
			}
			return (tmpArray[parameterIndex++ % tmpArray.length]);
		} else {
			String fieldName = key.replaceAll("\\$", "");
			String value = getValueFromClass(parameterObject, fieldName);
			return (value);
		}
	}
}
