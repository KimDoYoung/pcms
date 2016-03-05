package kr.dcos.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * 클래스 관련 유틸리티
 * 
 * @author Kim Do Young
 *
 */
public class ClassUtil {
	
	/**
	 * 자바가 지원하는 원초적인 클래스타입인지 체크한다
	 * @param object
	 * @return 
	 */
	public static boolean isPrimitive(Object object){
		if(object instanceof String ||
				   object instanceof Double ||
				   object instanceof Integer ||
				   object instanceof BigDecimal ||
				   object instanceof Short ||
				   object instanceof Boolean ||
				   object instanceof Character ||
				   object instanceof Byte
				   ) {
					return true;
		}
		return false;
	}
	/**
	 * 클래스 clazz가 메소드를 가지고 있는지 체크한다.
	 * 있으면 true,없으면 false를 리턴한다
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static boolean hasMethod(Class<?> clazz,String methodName) {
		boolean hasMethod = false;
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				hasMethod = true;
				break;
			}
		}
		return hasMethod;
	}
	public static boolean hasMethod(Object object,String methodName) {
		Class<?> c = object.getClass();
		return hasMethod(c,methodName);
	}
	/**
	 * 객체 object로부터 fieldName의 값을 구해서 리턴한다
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Object getValueFromClass(Object object, String fieldName) {
		Object result=null;
		Field field = null;

		 Class<?> c = object.getClass();

		 try {
			field = c.getDeclaredField(fieldName);
			field.setAccessible(true);
			result = field.get(object);
			return result;
		} catch (NoSuchFieldException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
	public static String getTypeFromClass(Object object, String fieldName) {
		Class<?> clazz = object.getClass();
		Field aField = null;
		try {
			aField = clazz.getDeclaredField(fieldName);
			Class<?> typeClass = aField.getType();
			return typeClass.getName();
		} catch (NoSuchFieldException e) {
			;
		} catch (SecurityException e) {
			;
		}
		return "unknown";
	    
		
	}

}
