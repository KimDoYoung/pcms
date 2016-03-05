package kr.dcos.common.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Map의 내용을 문자열로 만들어주는 클래스
 * 
 * @author Kim Do Young
 *
 * @param <K>
 * @param <V>
 */
public class PrintMap <K,V>{
	private Map<K, V> map;

    public PrintMap(Map<K, V> map) {
        this.map = map;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<K, V> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();

    }
}
