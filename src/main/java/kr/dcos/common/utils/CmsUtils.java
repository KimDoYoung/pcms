package kr.dcos.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsUtils {
	
	private static Logger logger = LoggerFactory.getLogger(CmsUtils.class);
	private static final HashSet<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	public static String UniqueId(String prefix){
		Random oRandom = new Random();
		int i = oRandom.nextInt(10000);
		return prefix + (new Integer(i).toString());
		
	}
	public static int longToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	public static String numberingFile(String fileName) {
		String fname = fileName;
		if((new File(fname)).exists() == false) return fname;
		Pattern p = Pattern.compile("(.*?)(\\d+)?(\\..*)?");
		do{
		    Matcher m = p.matcher(fname);
		    if(m.matches()){  //group 1 is the prefix, group 2 is the number, group 3 is the suffix
		    	fname = m.group(1) + (m.group(2)==null?1:(Integer.parseInt(m.group(2)) + 1)) + (m.group(3)==null?"":m.group(3));
		    }
		}while(new File(fname).exists()); //repeat until a new filename is generated
		return fname;
	}
	/**
	 * Class T의 Property명을 리스트에 담아서 리턴한다
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> List<String> propertyNamesOfClass(T t){
		List<String> list = new ArrayList<String>();
		java.lang.reflect.Field[] arrayField = t.getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : arrayField) {
		
			String propertyName = field.getName();
			if (propertyName.equals("serialVersionUID")
					|| propertyName.startsWith("pk")) {
				continue;
			}
			list.add(propertyName);
		
		}
		return list;
	}
	/**
	 * class의 값을 property이름으로 찾아서 리턴한다
	 * @param boardValidator
	 * @param validPropertyName
	 * @return
	 */
	public static <T> Object getValueOfClassWithPropertyName(T t, String propertyName) {
		Object value = null;
		try {
			java.lang.reflect.Field afield;
			afield = t.getClass().getDeclaredField(propertyName);
			afield.setAccessible(true);
			value = afield.get(t);
			return value;
		} catch (NoSuchFieldException e) {
			logger.error("", e);
		} catch (SecurityException e) {
			logger.error("", e);
		} catch (IllegalArgumentException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		}
		return value;
	}
	/**
	 * t의 propertyName에 해당하는 필드에 문자열 s를 넣는다.
	 * property의 타입이 Integer,String,Date인 경우 문자열을 변환해서 넣는다.
	 * 변환에 실패한 경우 넣지 않고 조용히 끝낸다
	 * @param t
	 * @param propertyName
	 * @param output
	 */
	public static <T> void setValueToClassWithPropertyName(T t,
			String propertyName, String output) {
		java.lang.reflect.Field afield;
		try {
			afield = t.getClass().getDeclaredField(propertyName);
			afield.setAccessible(true);
			Class<?> type = afield.getType();
			Object o = stringToObject(output,type);
			afield.set(t, o);	
		} catch (NoSuchFieldException e) {
			logger.error("",e);
		} catch (SecurityException e) {
			logger.error("",e);
		} catch (IllegalArgumentException e) {
			logger.error("",e);
		} catch (IllegalAccessException e) {
			logger.error("",e);
		}
	}
	private static Object stringToObject(String output, Class<?> type) {
		if(type.equals(String.class)){
			return output;
		}else if (type.equals(Integer.class)){
			try {
				return Integer.parseInt(output);
			} catch (Exception e) {
				return null;
			} 
		}else if(type.equals(java.util.Date.class)){
			try {
				return StrUtils.stringToDate(output);
			} catch (Exception e) {
				return null;
			} 			
		}else{
			return null;
		}
	}
	public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }
	private static HashSet<Class<?>> getWrapperTypes()
    {
        HashSet<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        ret.add(Date.class);
        return ret;
    }
	/**
	 * URL로 부터 데이터를 가져와서 String으로 리턴한다 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static String getStringFromUrl(URL url) throws IOException {
	        URLConnection conn = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                                    conn.getInputStream(),"UTF-8"));
	        StringWriter writer = new StringWriter();
	        
	        IOUtils.copy(in, writer);
	        
	        return writer.toString();        
	}	
}
