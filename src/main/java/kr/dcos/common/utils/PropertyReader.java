package kr.dcos.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Key=Value 값으로 된 파일을 읽어서 List 또는 Map으로 리턴한다. <br>
 * List는 ValueText를  <br>
 * Map 은 <String,String>으로 리턴한다  <br>
 * 
 * @author Kim Do Young
 *
 */
public class PropertyReader {
	
	private static Logger logger = LoggerFactory
			.getLogger(PropertyReader.class);
	

	private static List<String> list=new ArrayList<String>();

	private static void read(String path){
		read(path,"UTF-8");
	}
	private static void read(String path,String charsetName){
		list.clear();
		String s;
		try {
			s = FileUtils.readFileToString(new File(path), charsetName);
			String[] tmp = s.split("\n");
			for (String line : tmp) {
		        if(line.trim().startsWith("#")) continue;
		        if(line.trim().length()<1)continue;
		        list.add(line);
			}
		} catch (IOException e) {
			logger.error("",e.getMessage());
		}
		
	}
	//list로 읽어들인다. = 로 끊어 읽지 않는다.
	private static void read1(String path,String charsetName){
		list.clear();
		FileChannel fc = null;
		MappedByteBuffer byteBuffer = null;
		Scanner sc = null;
		
		try {
		    fc = new FileInputStream(path).getChannel();
		    
		    byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 
		       0, fc.size());
		    Charset charset = Charset.forName(charsetName);
		    CharsetDecoder decoder = charset.newDecoder();
		    CharBuffer charBuffer = decoder.decode(byteBuffer);
		   
		    // 라인 단위로 파일 읽음
		    sc = new Scanner(charBuffer).useDelimiter("\n");
		   
		    while (sc.hasNext()) {
		        String line = sc.next();     
		        if(line.trim().startsWith("#")) continue;
		        if(line.trim().length()<1)continue;
		        list.add(line);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sc != null)
					sc.close();
				if (fc != null)
					fc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static List<KeyValue> toList(String path,String encoding){
		read(path,encoding);
		List<KeyValue> vtList = new ArrayList<KeyValue>();
		for (String s : list) {
			String[] tmp = s.split("=");
			if(tmp.length>=2){
				vtList.add(new KeyValue(tmp[0].trim(), tmp[1].trim()));
			}
		}
		return vtList;
		
	}
	public static List<KeyValue> toList(String path){
		return toList(path,"UTF-8");
	}
}
