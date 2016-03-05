package kr.dcos.common.message;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CmsMessageManager is for display multi language 
 * with locale and keyword
 * using db
 * @author Kim Do Young
 *
 */
public class MessageManager {
	private static Logger logger = LoggerFactory.getLogger(MessageManager.class);
	private volatile static MessageManager instance;
	private String location = "/messages";
	private ClassLoader loader = null;
	private Locale[] locales = null;
	private ResourceBundle bundle;
	private boolean isLoad = false;
	
	public static MessageManager getInstance() {
		if (instance == null) {
			synchronized (MessageManager.class) {
				if (instance == null) {
					instance = new MessageManager();
				}
			}
		}
		return instance;
	}

	
	private MessageManager() {

		locales = Locale.getAvailableLocales();
		logger.info("MessageManager created...");
	}
	
	public void load(String location) {
		
		if(isLoad && this.location.equals(location)) return;
		this.location = location;
		this.isLoad = false;
		File file=null;
		try {
			file = new File(this.getClass().getResource(location).toURI());
			URL[] urls = {file.toURI().toURL()};
			loader = new URLClassLoader(urls);
			isLoad = true;
		} catch (URISyntaxException e) {
			logger.error(e.getMessage());
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		}
		
	}

	public String getString(Locale locale, String fullKey) {
		load(this.location);
		ResourceBundle bundle = ResourceBundle.getBundle("message",locale,loader);
		return bundle.getString(fullKey);
	}


	public String getString(String fullKey) {
		return getString(Locale.getDefault(),fullKey);
		
	}

	/**
	 * subKey에 해당하는 모든 key-value리스트를 리턴한다.
	 * @param locale
	 * @param keyPrefix
	 * @return
	 */
	public Map<String, String> getAll(Locale locale, String keyPrefix) {
		Map<String,String> map = new HashMap<String,String>();
		ResourceBundle bundle = ResourceBundle.getBundle("message",locale,loader);
		Enumeration<String> keys = bundle.getKeys();
		for (Enumeration<String> e = keys; keys.hasMoreElements();) {
	    	String key = e.nextElement();
	    	if (key.startsWith(keyPrefix)) {
	    		String shortKey = key.substring(keyPrefix.length());
	    		while(shortKey.startsWith(".")){
	    			shortKey = shortKey.substring(shortKey.indexOf('.')+1);
	    		}
	    		shortKey = shortKey.replaceAll("\\.", "_");
	    		map.put(shortKey, bundle.getString(key));
	    	}
	    }
		return map;
	}

	
//	private CmsMessageService cmsMessageService;
//	
//	private Set<String> noDataSet; //db에 없는 localeSubkey
//	private Map<String,List<CmsMessage>> cacheMap; // localeSubKey->CmsMessage List
	/**
	 * constructor
	 */

//	private String getLocaleSubKey(String locale, String subKey) {
//		return locale+"."+subKey;
//	}
//
//	/**
//	 * locale과 subkey user.list.jsp 와 같이 해서 해당하는 모든 메세지를 가져온다
//	 * 이미 cacheMap에 존재하는지 체크한다. 있으면 그냥 리턴
//	 * 없으면 nodataSet에서 찾아본다. 찾았다면 db에도 데이터가 없는 것이므로 null을 리턴한다
//	 * nodataSet에 없다면 아직 db에서 읽지 않은 것임으로 db에서 로드한다.
//	 * 
//	 * @param locale
//	 * @param subKey
//	 * @return
//	 */
//	public List<CmsMessage> getList(String locale, String subKey) {
//		String localeSubKey = getLocaleSubKey(locale,subKey);
//		if(cacheMap.containsKey(localeSubKey)){
//			logger.debug("Hit cache :" + localeSubKey);
//			return cacheMap.get(localeSubKey);
//		}else{
//			if(noDataSet.contains(localeSubKey)){ //
//				logger.debug("no data " + localeSubKey);
//				return null;
//			}else{ //get from db
//				CmsMessage cmsMessage = new CmsMessage();
//				cmsMessage.setLocale(locale);
//				cmsMessage.setKeyword(subKey);
//				List<CmsMessage> list = cmsMessageService.getList(cmsMessage);
//				if(list == null || list.size()<1){
//					noDataSet.add(localeSubKey);
//					logger.debug("nodataSet added "+localeSubKey);
//					return null;
//				}else{
//					cacheMap.put(localeSubKey, list);
//					return list;
//				}
//			}
//		}
//	}
//	/**
//	 * locale의 fullkey로 가져올려고 한다.
//	 * 1. subkey를 구한다.
//	 * 2. locale+subkey로 현재의 cache에 있는지 체크한다. 있으면 리턴
//	 * 3. locale+subkey로 nodata에 있는지 체크한다. 있으면 null
//	 * 4. db에서 locale과 subkey로 조회해온다.
//	 * @param locale
//	 * @param fullKey
//	 * @return
//	 */
//	public CmsMessage get(String locale,String fullKey){
//		int pos = fullKey.lastIndexOf('.');
//		String subKey = fullKey;
//		if(pos>=0){
//			subKey = fullKey.substring(0,pos);
//		}
//		String key = fullKey.substring(pos+1);
//		logger.debug("subkey:"+subKey);
//		logger.debug("key:"+key);
//		
//		String localeSubKey = getLocaleSubKey(locale, subKey);
//		if(cacheMap.containsKey(localeSubKey)){
//			for (CmsMessage cm : cacheMap.get(localeSubKey)) {
//				if(cm.getKeyword().equals(fullKey)){
//					return cm;
//				}
//			}
//			return null;
//		}
//		//noDataSet에 있다면 null
//		if(noDataSet.contains(localeSubKey)){
//			return null;
//		}
//		//db에서 가져와서 찾아본다
//		List<CmsMessage> list = getList(locale,subKey);
//		if(list == null || list.size()<1){
//			return null;
//		}
//		for (CmsMessage cm : list) {
//			if(cm.getKeyword().equals(fullKey)){
//				return cm;
//			}
//		}
//		return null;
//		
//	}
//	
//	/**
//	 * db가 갱신되었다면 reload를 호출함으로써 새로운 메세지를 반영한다
//	 * reload는 그냥, noDataSet와 cacheMap을 지움으로써 다시 메세지를 구할때 
//	 * 다시 db에서 가져오게 함으로서 구현한다
//	 */
//	public void reload(){
//		noDataSet.clear();
//		cacheMap.clear();
//	}
//	@Override
//	public String toString(){
//		StringBuilder sb = new StringBuilder();
//		sb.append("cacheMap size:"+cacheMap.size());
//		sb.append("\n");
//		sb.append("noDataSet size:"+noDataSet.size());
//		return sb.toString();
//	}
//	public String getMessage(String fullKey, String defaultValue) {
//		String localeString = ConfigManager.getInstance().get(ConfigKey.LOCALE);
//		
//		CmsMessage cm = get(localeString,fullKey);
//		if(cm == null){
//			return defaultValue;
//		}else{
//			return cm.getMessage();
//		}
//	}
}
