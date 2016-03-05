package kr.dcos.common.servlet;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.dcos.common.utils.CmsUtils;
import kr.dcos.common.utils.SelectCondition;
import kr.dcos.common.utils.SelectConditionDefaults;
import kr.dcos.common.utils.StrUtils;
import kr.dcos.common.utils.WhereClause;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 사용자측으로부터 받은 Request정보를 담고 있는 클래스
 *  
 * @author Kim Do Young
 *
 */
public class RequestInfo {
	
	private static Logger logger = LoggerFactory.getLogger(RequestInfo.class);
	

	private HttpServletRequest request;
	private HttpServletResponse response;

	private HttpMethod httpMethod;
	private String errorMessage;
	private String returnUrl;

	private String uploadFilePath;

	private MultipartFormData multipartFormData=null;

	//
	// constructor
	//
	public RequestInfo(HttpServletRequest request,HttpServletResponse response,HttpMethod httpMethod){
		this(request,response,httpMethod,"c:/temp/uploaded");
	}
	public RequestInfo(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method, String tempDir) {
		this.request=request;
		this.response = response;
		this.httpMethod = method;
		if(tempDir == null || tempDir.length()<1){
			this.uploadFilePath = "c:/temp/uploaded/";
		}else{
			this.uploadFilePath = tempDir; 
		}
	}
	//
	// getter , setter
	//
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}
	public String getErrorMessage() {
		if(errorMessage != null && errorMessage.length()>0){
			return errorMessage;	
		}else{
			errorMessage = getString("errorMessage");
			return errorMessage;
		}
	}
	public boolean isMultipartContent(){
		return ServletFileUpload.isMultipartContent(request);
	}
	/**
	 * 
	 * @param string
	 * @return
	 */
	public String getParameter(String name) {
		return getString(name,null);
	}

	public String getString(String name) {
		return getString(name,null); 
	}
		
	public String getString(String name,String defaultValue){
		
		//Attribute에서 찾아본다
		Object o = request.getAttribute(name);
		if(o != null){
			return o.toString();
		}
		
		//multipart
		if(ServletFileUpload.isMultipartContent(request) ){
			createMultipartFormData();
			String s = multipartFormData.getString(name);
			if( StrUtils.isNullOrEmpty(s)){
				return defaultValue;
			}
			return s;
		}
		//parameter
		String s = request.getParameter(name);
		if(StrUtils.isNullOrEmpty(s)){
			return defaultValue;
		}
		return s;
	}

	public Integer getInteger(String name) {
		String s = getString(name);
		if(StrUtils.isNullOrEmpty(s)){
			return null;
		}
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			return null;
		}
	}
	public Integer getInteger(String name, Integer defaultValue){
		Integer i = getInteger(name);
		if(i == null){
			return defaultValue;
		}
		return i;
	}
	/**
	 * 배열형태(tag의 name이 같은 것으로 왔었을 때 배열로 값을 리턴해 준다
	 * @param string
	 * @return
	 */
	public String[] getArray(String name) {
		if(isMultipartContent()){
			createMultipartFormData();
			List<String> list = multipartFormData.getList(name);
			return list.toArray(new String[list.size()]);			
		}else{
			String[] array = request.getParameterValues(name);
			return array;
		}
	}
	public List<String> getList(String name){
		if (isMultipartContent()) {
			createMultipartFormData();
			return multipartFormData.getList(name);
		} else {
			String[] array = request.getParameterValues(name);
			if (array != null) {
				List<String> list = new ArrayList<String>();
				for (String s : array) {
					list.add(s);
				}
				return list;
			}
			return Collections.emptyList();
		}
	}
	private void createMultipartFormData() {
		if(multipartFormData == null){
			multipartFormData = new MultipartFormData();
			loadMultipartFormData();
		}
	}
	public FileJeongBo getFile(String name){
		createMultipartFormData();
		return multipartFormData.getFileJeongBo(name);
	}
	/**
	 * multipart로 이미지가 올라왔었을 때의 리스트
	 * @return
	 */
	public List<FileJeongBo> getUploadedFileList() {
		if(isMultipartContent()){
			if(multipartFormData == null){
				multipartFormData = new MultipartFormData();
			}
			return multipartFormData.getFileJeongBoList();
		}
		return Collections.emptyList();
	}
	public FileJeongBo getFileJeongBo(String inputTagName) {
		for (FileJeongBo fileJeongBo : getUploadedFileList()) {
			if(fileJeongBo.getHtmlTagName().equals(inputTagName)){
				return fileJeongBo;
			}
		}
		return null;
	}	
	private void loadMultipartFormData(){
		
		final int THRESHOLD_SIZE = (1024*1024)*3;//3M
		final int MAX_FILE_SIZE =  (1024*1024)*40;//40M
		final int REQUEST_SIZE = (1024*1024)*50;//50M
		//if no upload file , just return;
		if (!ServletFileUpload.isMultipartContent(request)) {
			return;
		}
         
 		// configures some settings
 		DiskFileItemFactory factory = new DiskFileItemFactory();
 		factory.setSizeThreshold(THRESHOLD_SIZE);
 		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 		
 		ServletFileUpload upload = new ServletFileUpload(factory);
 		upload.setFileSizeMax(MAX_FILE_SIZE);
 		upload.setSizeMax(REQUEST_SIZE);
 		upload.setHeaderEncoding("UTF-8");
 		//TODO more path
        String uploadPath = this.uploadFilePath;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
 		
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> formItems = upload.parseRequest(request);
			for (FileItem fileItem : formItems) {
				if (fileItem.isFormField()){
					String propertyName = fileItem.getFieldName();
					String o = fileItem.getString("UTF-8");
					multipartFormData.addInput(propertyName,o);
					continue;
				}
				if (fileItem.getSize()<1){
					 continue;
				}
				//upload directory에 저장,TODO if exist uniqueName으로...
				String filePath =  CmsUtils.numberingFile(uploadPath  + fileItem.getName());
				File storedFile = new File(filePath);
				fileItem.write(storedFile);
				fileItem.delete();
				
				FileJeongBo fileJeongBo = new FileJeongBo();
				fileJeongBo.setHtmlTagName(fileItem.getFieldName());
				fileJeongBo.setFullPath(filePath);
				fileJeongBo.setFileName(fileItem.getName());
				fileJeongBo.setContentType(fileItem.getContentType());
				fileJeongBo.setSize(fileItem.getSize());
				logger.debug(fileJeongBo.toString());
				multipartFormData.addFileJeongBo(fileJeongBo);
			}
			//TODO 왜 CKEditor가 보낸 것을 못 잡아 낼까? get이라 그런가?
			@SuppressWarnings("unchecked")
			Map<String, String[]> parameters = request.getParameterMap();
			for(String parameter : parameters.keySet()) {
			     String[] values = parameters.get(parameter);
			     for (String value : values) {
					if(multipartFormData.getString(parameter)==null){
						multipartFormData.addInput(parameter, value);
					}
				}
			}
			//getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
		} catch (FileSizeLimitExceededException e) {
			
			String s = 	"upload file fail , file size is too big:["	+ e.getMessage() + "]";
			setErrorMessage(s);
			
		} catch (Exception e) {
			String s = 	"upload file fail :[" + e.getMessage() + "]";
			setErrorMessage(s);
		}	
	}

	/**
	 * 타입으로 클래스를 생성한다
	 * @param clazz
	 * @return
	 */
	public <T> T createPOJO(Class<T> clazz) {
	    try {
	        return clazz.newInstance(); 
	    } catch (InstantiationException e) {
	        throw new IllegalStateException(e);
	    } catch (IllegalAccessException e) {
	        throw new IllegalStateException(e);
	    }
	}
	/**
	 * 
	 * class clazz에 이미 값들이 들어가 있다.
	 * t의 각 필드를 돌면서 parameter에 값이 있으면 그 값을 obj에 쓰고 없다면 그냥 둔다
	 * Update동작에서 db에 있던 레코드를 가져와서 담고 jsp로 부터 받은 데이터만을 덮어쓰기위해서 만듬
	 * @param class1
	 * @param boardInDb
	 * @return
	 */
	public <T extends Object> T fillPOJO(Class<T> t, T clazz) {
		Field[] fields = clazz.getClass().getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if(fieldName.contains("serialVersionUID")) continue;
			try {
				field.setAccessible(true);
				//String s = request.getParameter(field.getName());
				String s = getString(field.getName(), null);
				if(s == null) continue;
				Class<?> type = field.getType();
				Object value = getDataInRequest(type, s);
				field.set(clazz, value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return clazz;
		
	}
	/**
	 * 클래스를 생성하고 request의 getParameter를 모두 돌면서 class의 값을 set해준다
	 * @param classType
	 * @return
	 */
	public <R extends Object> R getPOJO(Class<R> classType) {
		R c = createPOJO(classType);
		Field[] fields = c.getClass().getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if(fieldName.contains("serialVersionUID")) continue;
			
			logger.debug("fieldName:" + fieldName + " Type:"  + field.getType());
			try {
				field.setAccessible(true);
				if(ServletFileUpload.isMultipartContent(request) ){ //multipart
					field.set(c, getValueFromMultipartFormData(fieldName,field.getType()));
				}else{ //normal
					field.set(c, getValueFromParameter(fieldName, field.getType()));
				}
			} catch (IllegalArgumentException e) {
				logger.error("",e);
			} catch (IllegalAccessException e) {
				logger.error("",e);
			}
		}
		return c; 
	}
	private <T extends Object>  T getValueFromMultipartFormData(String name, Class<T> type) {
		String s = multipartFormData.getString(name);
		return getDataInRequest(type, s);
	}
	@SuppressWarnings("unchecked")
	private <T> T getDataInRequest(Class<T> type, String s) {
		if(type.equals(String.class)){ //String
			if(s==null)return null;
			return  (T) s;
		}else if(type.equals(Integer.class)){ //Ingeter
			if(s==null)return null;
			try{
				Integer i = Integer.parseInt(s);
				return (T) i;
			}catch(NumberFormatException e){
				return null;
			}
		}else if(type.equals(Date.class)){ //date
			if(s==null)return null;
			DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return (T) sdFormat.parse(s);
			} catch (ParseException e) {
				return null;
			}
		}else if(type.equals(Long.class)){ //Long
			if(s==null)return null;
			try{
				Long l = Long.parseLong(s);
				return (T)l;
			}catch(NumberFormatException  e){
				return null;
			}
			
		}else if(type.equals(Double.class)){ //double
			if(s==null)return null;
			try{
				Double d = Double.parseDouble(s);
				return (T)d;
			}catch(NumberFormatException e){
				return null;
			}
			
		}else if(type.equals(Boolean.class)){
			Boolean b = false;
			b = Boolean.parseBoolean(s);
			return (T)b;
			
		}else{
			logger.warn(type.toString() + " is unknown, change to string");
			return (T)s;
		}
	}
	/**
	 * request의 parameter에서 
	 * @param name
	 * @return 
	 * @return
	 */
	private <T extends Object>  T getValueFromParameter(String name,Class<T> type) {
		String s = request.getParameter(name);
		return getDataInRequest(type, s);
	}
	
	public HttpSession getSession(boolean newSession){
		return request.getSession(newSession);
	}
	public void setAttribute(String name, Object object) {
		request.setAttribute(name, object);
	}
	public Object getObject(String name){
		return request.getAttribute(name);
	}
	/**
	 * selectCondition을 만들어서 리턴한다. list메소드의 코드양을 줄이기 위해서 
	 * @return
	 */
	public SelectCondition getSelectCondition() {
		SelectConditionDefaults defaults = new SelectConditionDefaults();
		defaults.setOrderBy("create_date");
		defaults.setOrderByDirection("DESC");
		defaults.setPageSize(10);
		return this.getSelectCondition(defaults);
	}	
	public SelectCondition getSelectCondition(SelectConditionDefaults defaults) {
		
		Integer pageSize = getInteger("pageSize",defaults.getPageSize());
		Integer nowPage = getInteger("nowPage",1); 
		String  searchColumn = getString("searchColumn",null);
		String  searchOperator = getString("searchOperator","=");
		String  searchKey	=	getString("searchKey",null);
		String	orderBy		= 	getString("orderBy",null);
		String	orderByDirection	= 	getString("orderByDirection","DESC");
		
		String startDate = getString("startDate",null);
		String dateColumn = getString("searchDateColumn",null);
		String endDate = getString("endDate",null);
		
		//추가검색 필드들
		String[] chuga_fieldname = getArray("chuga_fieldname");
		String[] chuga_operator  = getArray("chuga_operator");
		String[] chuga_value 	 = getArray("chuga_value");
		
		//야매 create date의 db에서의 타입이 date 여서 그냥 비교는 안됨
		//TODO db에 따라서 달라지는지 체크
		if( dateColumn !=null && (dateColumn.equalsIgnoreCase("CREATE_DATE") ||
			dateColumn.equalsIgnoreCase("CREATE_DATE_MOD"))){
			if(startDate!=null){
				startDate += " 00:00:00";
			}
			if(endDate !=null){
				endDate += " 23:59:59";
			}
		}
		
		String defaultOrderBy = getString("defaultOrderBy",defaults.getOrderBy());
		String defaultOrderByDirection = getString("defaultOrderByDirection",defaults.getOrderByDirection());
		
		
		SelectCondition sc = new SelectCondition();
		//총합계가 구해지기 전까지 pagesize와 nowpage는 의미가 없다. 그래서 임시 저장소에 넣어 둔다
		sc.setTmpPageSize(pageSize);
		sc.setTmpNowPage(nowPage);
		
		//검색조건
		sc.getSearchOrder().setSearchColumn(searchColumn);
		sc.getSearchOrder().setSearchOperator(searchOperator);
		sc.getSearchOrder().setSearchKey(searchKey);
		//orderby
		sc.getSearchOrder().setOrderBy(orderBy);
		sc.getSearchOrder().setOrderByDirection(orderByDirection);
		//검색조건 날짜범위
		sc.getSearchOrder().setStartDate(startDate);
		sc.getSearchOrder().setEndDate(endDate);
		sc.getSearchOrder().setDateColumn(dateColumn);
		//default order by
		sc.getSearchOrder().setDefaultOrderBy(defaultOrderBy);
		sc.getSearchOrder().setDefaultOrderByDirection(defaultOrderByDirection);
		
		//추가search where clause를 넣는다.
		if(chuga_fieldname != null){
			for(int i=0;i<chuga_fieldname.length;i++){
				String fieldName = chuga_fieldname[i];
				String operator = chuga_operator[i];
				String value = chuga_value[i];
				if(!StrUtils.isNullOrEmpty(fieldName) && !StrUtils.isNullOrEmpty(operator) && !StrUtils.isNullOrEmpty(value)){
					sc.addChugaSearch(new WhereClause(fieldName, operator, value));
				}else{
					break; //순차적으로 쌓이니깐, 더 갈 필요없음.
				}
			}
			//where 절에도 넣는다. where절이 실제로 sql에 반영된다.
			for (WhereClause wc : sc.getChugaSearchList()) {
				sc.addWhereClause(wc.getFieldName(), wc.getOperator(), wc.getValue().toString());
			}
		}
		if(searchColumn != null && searchOperator != null && searchKey != null){
			sc.addWhereClause(searchColumn, searchOperator, searchKey);
		}
		if(dateColumn != null){
			if(startDate != null){
				sc.addWhereClause(dateColumn, ">=", startDate);
			}
			if(endDate != null){
				sc.addWhereClause(dateColumn, "<=", endDate);
			}
		}
		if(orderBy != null){
			sc.addOrderBy(orderBy, orderByDirection);
		}
		if(defaultOrderBy != null){
			sc.addOrderBy(defaultOrderBy, defaultOrderByDirection);
		}
		return sc;
	}
	/**
	 * SessionInfo를 찾아서 리턴한다. 없다는 것은 login하지 않았다는 뜻
	 * @return
	 */
//	public SessionInfo getSessionInfo() {
//		SessionInfo sessionInfo = (SessionInfo) request.getAttribute("sessionInfo");
//		return sessionInfo;
//	}
//
//	public String getUploadFilePath() {
//		return uploadFilePath;
//	}
//	public void setUploadFilePath(String uploadFilePath) {
//		this.uploadFilePath = uploadFilePath;
//	}
//	//repositry
//	public void setRepository(Repository repository) {
//		this.repository = repository; 
//	}
//	public Repository getRepository(){
//		return this.repository;
//	}
	
}
