package kr.dcos.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 문자열날짜 관련 합수들
 * 날짜는 YYYYMMDD형태의 8 char를 기본으로 한다
 * 
 * @author Kim, Do Young
 *
 */
public class DateUtil {

	/**
	 * 오늘의 날짜를 pattern의 형태의 문자로 만들어서 리턴한다
	 * @param pattern
	 * @return
	 */
	public static String getDate(String pattern) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sd = new SimpleDateFormat(pattern);
		return sd.format(date);
	}
	/**
	 * 오늘의 날짜를 기본형태(YYYYMMDD)의 형태로 리턴한다
	 * @param pattern
	 * @return
	 */
	public static String getDate(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		return sd.format(date);
	}
	/**
	 * yyyymm의 형태에서 1달을 더한 문자열을 리턴한다
	 * "201301"과 같은 포맷의 문자열을 "201302"로 리턴한다.
	 * "201312"과 같은 포맷의 문자열을 "201401"로 리턴한다.
	 * @param yyyymm
	 * @return
	 */
	public static String addMonthString(String yyyymm) {
		String yy = yyyymm.substring(0,4);
		String mm = yyyymm.substring(4);
		int mm_plus_1 = Integer.parseInt(mm);
		int iYY = Integer.parseInt(yy);
		mm_plus_1++;
		if(mm_plus_1 > 12){
			mm_plus_1 = 1;
			iYY++;
		}
		return String.format("%4d%02d", iYY,mm_plus_1);
	}

	/**
	 * date는 yyyymmdd 문자열형태의 날짜이고 그것에 amountOfMonth 만큼 더한다.
	 * 마이너스 값을 줄 수 있다. 
	 * @param date
	 * @param amountOfMonth
	 * @return
	 * @throws Exception 
	 */
	public static String addMonths(String date, int amountOfMonth) throws Exception {
		Date d = toDate(date,"yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MONTH,	amountOfMonth);
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		return sd.format(cal.getTime());		
	}
	
	/**
	 * date에 날짜를 더한다
	 * @param date yyyyMMdd형태의 문자열
	 * @param amountOfDays  더할 날짜, 마이너스값을 허용한다
	 * @return yyyyMMdd형태의 문자열
	 * @throws Exception 
	 */
	public static String addDays(String date, int amountOfDays) throws Exception {
		Date d = toDate(date,"yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE,	amountOfDays);
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		return sd.format(cal.getTime());		
	}
	/**
	 * date는 yyyyMMdd의 형태여야한다.
	 * 문자열date를 날짜형으로 바꾸어 리턴한다
	 * @param date
	 * @return
	 * @throws DateUtilException 
	 */
	public static Date toDate(String date) throws Exception{
		return toDate(date,"yyyyMMdd");
	}
	/**
	 * 문자열 date를 format에 맞게끔 날짜type으로 바꾸어서 리턴한다.
	 * format문자열이 java의 날짜형식에 맞지 않을 경우 DateUtilException을 발생시킨다
	 * @param date
	 * @param format
	 * @return
	 * @throws DateUtilException
	 */
	public static Date toDate(String date, String format) throws Exception {
		if (date == null || format == null) 	return null;
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
		java.util.Date formattedDate = null;
		try {
			formattedDate = formatter.parse(date);
		} catch (ParseException e) {
			throw new Exception(date + " for " + format + " is not valid");
		}
		return formattedDate;
	}
	/**
	 * 현재 날짜시간을 리턴한다
	 * @return
	 */
	public static Date now() {
		return new Date();
	}

}
