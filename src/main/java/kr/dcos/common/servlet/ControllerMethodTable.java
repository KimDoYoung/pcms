package kr.dcos.common.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.dcos.common.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 클래스.메소드 형태(Nogada.go)의 형태로 key값을, Value는 ISuhaengMethod이다. <br>
 * 키값은 모두 lowercase로 만들어서 넣는다.(위험하지만 jsp에서의 편의를 위해서)<br>
 * load메소드에서 web.xml에 기술되어 있는 config파일을 넘겨받아서서 class를 로딩한 후에<br>
 * 그 클래스 안에서  ControllerMethod annotation이 붙은 것을 골라서 테이블에 넣는다.<br>
 * 
 * @author Kim Do Young
 *
 */
public class ControllerMethodTable {
	
	private static Logger logger = LoggerFactory
			.getLogger(ControllerMethodTable.class);
	
	public class ControllerInfo {
		public String sessionCheck;
		public String className;
		public String alias;
	}

	private List<ControllerInfo> classNameList;
	//private Map<String,  IControllerMethod> map;
	private Map<String, CmsMvcMethodInfo> map;
	
	public ControllerMethodTable(){
		classNameList = new ArrayList<ControllerInfo>();
		map = new HashMap<String, CmsMvcMethodInfo>();
	}
	
	public void load(String configFile) {
		//xml에서 클래스를 읽어온다
		loadClassNameFromConfigFile(configFile);
		//class에서 SuhaengMethod를 찾아서 등록한다
		for (ControllerInfo controllerInfo : classNameList) {
			 String className = controllerInfo.className;
			 Class<?> clazz;
			try {
				//Controller클래스를 생성
				clazz = Class.forName(className);
				//Controller클래스의 Method를 뽑아낸다
				extractCmsFunctionsFromClass(controllerInfo,clazz);
			} catch (ClassNotFoundException e) {
				logger.error("class " + className + " is not exist, check class config file :"+configFile );
			}
		}
	}
	/**
	 * 
	 * @param configFile
	 */
	private void loadClassNameFromConfigFile(String configFile) {
		
		File docFile = new File(configFile);
		if(docFile.exists()==false){
			logger.warn(configFile + " is not exist");
			return;
		}
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuild;
		try {
			docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(docFile);
			doc.getDocumentElement().normalize();
			loadXmlClasses(doc);
		} catch (ParserConfigurationException e) {
			logger.debug("",e);
		} catch (SAXException e) {
			logger.debug("",e);
		} catch (IOException e) {
			logger.debug("",e);
		} finally{
			
		}
		
	}
	/**
	 * config xml을 해석한다.
	 * @param doc
	 */
	private void loadXmlClasses(Document doc) {
		NodeList constantList = doc.getElementsByTagName("classes"); // for all in-jar
		for (int i = 0; i < constantList.getLength(); i++) {
			Node childNode = constantList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			NodeList classList = childNode.getChildNodes(); // jar chidren
			for (int j = 0; j < classList.getLength(); j++) {
				Node classNode = classList.item(j);
				String sessionCheck ="Y";
				String alias = null;
				NamedNodeMap attrMap = classNode.getAttributes();
				if(attrMap != null){
					Node n = attrMap.getNamedItem("session-check");
					if(n!=null){
						sessionCheck = n.getNodeValue();
					}
					n = attrMap.getNamedItem("alias");
					if(n!=null) alias = n.getNodeValue();
				}				
				if (classNode.getNodeType() != Node.ELEMENT_NODE)	continue; // 
					NodeList childs = classNode.getChildNodes();
					
					if(childs != null && childs.getLength()>0){
						String className = childs.item(0).getNodeValue();
						ControllerInfo ci = new ControllerInfo();
						ci.sessionCheck = sessionCheck;
						ci.className = className;
						ci.alias = alias;
						classNameList.add(ci);
					}
			}
		}
	}

	/**
	 * clazz로 부터  ControllerMethod annotation이 붙어 있는 함수를 찾아서
	 * 테이블에 넣는다.
	 * @param clazz
	 */
	private void extractCmsFunctionsFromClass(ControllerInfo controllerInfo,final Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for (final Method method : methods) {
			if(method.isAnnotationPresent(ControllerMethod.class)){
				ControllerMethod annotation = method.getAnnotation(ControllerMethod.class);
				//String key = getKey(clazz,method.getName());// clazz.getName()+"."+method.getName(); //className.method

				IControllerMethod controllerMethod = new IControllerMethod() {
					public Object invoke(RequestInfo requestInfo) throws Throwable{
							Method methodToExec = null;
							try {
								methodToExec = clazz.getDeclaredMethod(method.getName(), new Class[]{RequestInfo.class});
								
							} catch (NoSuchMethodException e) {
								logger.error("",e);
							} catch (SecurityException e) {
								logger.error("",e);
							} catch (IllegalArgumentException e) {
								logger.error("",e);
							}
							//return  (ForwardInfo) methodToExec.invoke(clazz,new Class[]{requestInfo.class});
							return  methodToExec.invoke(clazz.newInstance(),requestInfo);
							
						} 
					}
				;
				CmsMvcMethodInfo mi0 = new CmsMvcMethodInfo();
				mi0.setClass(clazz);
				mi0.setSessionCheck(controllerInfo.sessionCheck);
				mi0.setMethodName(method.getName());
				mi0.setMethod(controllerMethod);
				putMapTable(mi0);
				//class alias
				if(controllerInfo.alias != null){
					String [] aliasArray = controllerInfo.alias.split(",");
					for (String name : aliasArray) {
						CmsMvcMethodInfo mi1 = new CmsMvcMethodInfo();
						mi1.setClass(clazz);
						mi1.setClassName(name);
						mi1.setSessionCheck(controllerInfo.sessionCheck);
						mi1.setMethodName(method.getName());
						mi1.setMethod(controllerMethod);	
						putMapTable(mi1);
					}
				}
				
				//Annotation 적용, method alias
				String s = annotation.alias();
				if(StrUtils.isNullOrEmpty(s)==false){
					String tmp[]  = s.split(",");
					for (String anotherName : tmp) {
						if(anotherName.trim().length()<1)continue;
						CmsMvcMethodInfo mi2 = new CmsMvcMethodInfo();
						mi2.setClass(clazz);
						mi2.setSessionCheck(controllerInfo.sessionCheck);
						mi2.setMethodName(anotherName);
						mi2.setMethod(controllerMethod);
						putMapTable(mi2);
					}
				}
			}
		}
	}
	/**
	 * key : "nogada.go"와 같은 키로 찾고, 
	 * 없으면 nogadacontroller.go로 찾아본다.
	 * 즉  controller를 생략가능하다
	 * @param controllerMethodName
	 * @return
	 */
	public CmsMvcMethodInfo get(String controllerMethodName){
		if(controllerMethodName == null || controllerMethodName.length()<1) return null;
		CmsMvcMethodInfo mi =  map.get(controllerMethodName.toLowerCase());
		if(mi == null){
			String[] tmp = controllerMethodName.split("\\.");
			if(tmp.length != 2) return null;
			String key = tmp[0]+"controller" + "." + tmp[1];
			mi = map.get(key.toLowerCase());
		}
		return mi;
	}
	public void putMapTable(CmsMvcMethodInfo methodInfo){
		String key = methodInfo.getKey();
		if(map.containsKey(key)){
			map.remove(key);
		}
		map.put(key,methodInfo);
	}
	/**
	 * 모든 list를 리턴한다
	 * @return
	 */
	public List<ControllerMethodName> getControllerList(){
		List<ControllerMethodName> list = new ArrayList<ControllerMethodName>();
		for (String key : map.keySet()) {
			int p = key.indexOf('.');
			String c = key.substring(0,p);
			String m = key.substring(p+1);
			ControllerMethodName cmn = new ControllerMethodName(c,m);
			cmn.setControllerName(c);
			cmn.setMethodName(m);
			list.add(cmn);
		}	
		return list;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (Entry<String, CmsMvcMethodInfo> entry : map.entrySet()) {
		    sb.append(entry.getKey()).append(":").append(entry.getValue().toString());
		    sb.append("\n");
		}
		return sb.toString();
	}
}
