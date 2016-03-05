package kr.dcos.common.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

import kr.dcos.common.utils.ConvertUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 1. 환경설정의 값들을 DB에서 읽어서 저장하고 있다가 제공해준다. <br>
 * 2. singleton으로 작성한다 <br>
 * 3. wcm_config table 사용 <br>
 * 4. wcm_config에 들어 있는 내용들을 가지고 와서 map을 형성하고 값을 리턴해준다. <br>
 * 
 * @author Kim Do Young
 *
 */
public class ConfigManager {
	private static Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	private volatile static ConfigManager instance;
	private Properties prop;
	//private ConfigService service ;
	
	public static ConfigManager getInstance() {
		if (instance == null) {
			synchronized (ConfigManager.class) {
				if (instance == null) {
					instance = new ConfigManager();
				}
			}
		}
		return instance;
	}

	//
	// constructor
	//
	private ConfigManager() {
		prop = new Properties();
	}

	public void load(File file){
		prop.clear();
		try {
			prop.load(new BufferedReader(
			        new InputStreamReader(new FileInputStream(file),"UTF-8" )
			));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}
	public void put(String key,String value){
		prop.setProperty(key, value);
	}
	public String get(String key){
		return prop.getProperty(key);
	}
	@Override
	public String toString() {
		Enumeration<Object> keys = prop.keys();
		StringBuilder sb = new StringBuilder();
		while (keys.hasMoreElements()) {
		  String key = (String)keys.nextElement();
		  String value = (String)prop.get(key);
		  logger.debug(key + ": " + value);
		  sb.append(key + ":" + value +",");
		}
		if(sb.toString().length()>0){
			return sb.toString().substring(0,sb.toString().length()-1);
		}
		return sb.toString();
    }

	public Properties getConfig() {
		return prop;
	}

	public int getInteger(String strInteger) {
		return ConvertUtil.toInteger(strInteger);
	}

}
