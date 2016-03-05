package kr.dcos.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 집합연산용 유틸리티 클래스<br>
 * List 또는 Set에  대해서 <br>
 * 합집합(union), 교집합(intersection), 차집함(substract) 를 수행<br>
 * 
 * @author 김도영
 *
 */
public class SetUtil {

	/**
	 * 합집합 : list1과 list2의 합집합을 리턴한다.<br>
	 * 중복은 없음<br>
	 * @param <T>
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static <T> List<T> union(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<T>();
		set.addAll(list1);
		set.addAll(list2);
		return new ArrayList<T>(set);
	}
	public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<T>();
		set.addAll(set1);
		set.addAll(set2);
		return set;
	}

	/**
	 * 교집합: list1과 list2의 교집합을 구해서 새로운 리스트로 만든다<br>
	 * 중복은 없음
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<T>();
		for (T t : list1) {
			if(list2.contains(t)){
				set.add(t);
			}
		}
		List<T> list = new ArrayList<T>(set);
		return list;
	}

	/**
	 * 차집합을 구한다: list2에 존재하는 element는 list1에서 제거한다
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static <T> List<T> substract(List<T> list1, List<T> list2) {
		Set<T> set1 = new HashSet<T>(list1);
		Set<T> set2 = new HashSet<T>(list2);
		set1.removeAll(set2);
		return new ArrayList<T>(set1);
		
	}

	/**
	 * 교집합 : 집합1과 집합2의 교집합을 리턴한다 
	 * @param <T>
	 * @param set1 집합1
	 * @param set2 짒합2
	 * @return
	 */
	public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<T>();
		for (T t : set1){
			if(set2.contains(t)) set.add(t);
		}
		return set;
	}
	
}
