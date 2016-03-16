package kr.kalpa.mboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.kalpa.mboard.MetaData.DataType;
import kr.kalpa.mboard.MetaData.Field;

public class MetaDataTest {
	private String metaString = null;
	@Before
	public void setUp() throws Exception {
		String resourcePath = "/metadata/example.meta";
		URL url = MetaData.class.getResource(resourcePath);
		assertNotNull(url);
		String filePath = url.getFile();
		File file = new File(filePath);
		metaString = FileUtils.readFileToString(file, "UTF-8");
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testValid() throws MBoardException {
		MetaData metaData  = new MetaData(metaString);
		
        assertEquals(metaData.validCheck(), "OK");
        metaData.setId(null);
        String s = metaData.validCheck();
        assertEquals(s, "id is empty");
	}

	/**
	 * 문자열로 받은 메타데이터를 정확히 해석하는가?
	 * @throws MBoardException
	 */
	@Test
	public void test() throws MBoardException {
		MetaData metaData  = new MetaData(metaString);
		assertEquals(metaData.getId(), "movies");
		assertEquals(metaData.getName(), "영화목록");
		assertEquals(metaData.getBoardType(), "borad1");
		assertEquals(metaData.getDataSourceId(), "oracle");
		assertEquals(metaData.getFields().size(),8);
		
		List<Field> list = metaData.getFields();
		assertEquals(list.get(0).id,"id");
		assertEquals(list.get(0).title,"아이디");
		assertEquals(list.get(0).dataType,DataType.String);
		assertEquals(list.get(0).size.toString(),"10");
		assertEquals(list.get(0).precision.toString(),"0");
		assertEquals(list.get(0).pilsuYn,"Y");

		assertEquals(list.get(5).id,"jumsu");
		assertEquals(list.get(5).title,"점수");
		assertEquals(list.get(5).dataType,DataType.Integer);
		assertEquals(list.get(5).size.toString(),"0");
		assertEquals(list.get(5).precision.toString(),"0");
		assertEquals(list.get(5).pilsuYn,"N");

		assertEquals(list.get(6).id,"cost");
		assertEquals(list.get(6).title,"가격");
		assertEquals(list.get(6).dataType,DataType.Double);
		assertEquals(list.get(6).size.toString(),"10");
		assertEquals(list.get(6).precision.toString(),"2");
		assertEquals(list.get(6).pilsuYn,"N");

		assertEquals(list.get(7).id,"upd_dt");
		assertEquals(list.get(7).title,"입력일시");
		assertEquals(list.get(7).dataType,DataType.DateTime);
		assertEquals(list.get(7).size.toString(),"0");
		assertEquals(list.get(7).precision.toString(),"0");
		assertEquals(list.get(7).pilsuYn,"N");
		
	}
	/**
	 * 리소스에 있는 파일로  받은 메타데이터를 정확히 해석하는가?
	 * @throws IOException
	 * @throws MBoardException
	 */
	@Test
	public void loadFileTest() throws IOException, MBoardException{
		String resourcePath = "/metadata/example.meta";
		URL url = MetaData.class.getResource(resourcePath);
		assertNotNull(url);
		String filePath = url.getFile();
		File file = new File(filePath);
		MetaData metaData  = new MetaData(file);
		assertEquals(metaData.getDataSourceType(), "database");
		assertEquals(metaData.getDataSourceId(), "oracle");
		assertEquals(metaData.getId(), "movies");
		assertEquals(metaData.getName(), "영화목록");
		assertEquals(metaData.getBoardType(), "borad1");
		assertEquals(metaData.getFields().size(),8);
		
		List<Field> list = metaData.getFields();
		assertEquals(list.get(0).id,"id");
		assertEquals(list.get(0).title,"아이디");
		assertEquals(list.get(0).dataType,DataType.String);
		assertEquals(list.get(0).size.toString(),"10");
		assertEquals(list.get(0).precision.toString(),"0");
		assertEquals(list.get(0).pilsuYn,"Y");

		assertEquals(list.get(5).id,"jumsu");
		assertEquals(list.get(5).title,"점수");
		assertEquals(list.get(5).dataType,DataType.Integer);
		assertEquals(list.get(5).size.toString(),"0");
		assertEquals(list.get(5).precision.toString(),"0");
		assertEquals(list.get(5).pilsuYn,"N");

		assertEquals(list.get(6).id,"cost");
		assertEquals(list.get(6).title,"가격");
		assertEquals(list.get(6).dataType,DataType.Double);
		assertEquals(list.get(6).size.toString(),"10");
		assertEquals(list.get(6).precision.toString(),"2");
		assertEquals(list.get(6).pilsuYn,"N");

		assertEquals(list.get(7).id,"upd_dt");
		assertEquals(list.get(7).title,"입력일시");
		assertEquals(list.get(7).dataType,DataType.DateTime);
		assertEquals(list.get(7).size.toString(),"0");
		assertEquals(list.get(7).precision.toString(),"0");
		assertEquals(list.get(7).pilsuYn,"N");		
	}
	/**
	 * 문자열로 받아서 해석한것 meta1, meta1을 file로 write 다시 읽어서  해석한 것 meta2
	 * meta1과 meta2가 같은 내용인가?
	 * @throws MBoardException 
	 * @throws IOException 
	 */
	@Test
	public void writeAndReadTest() throws MBoardException, IOException {
		MetaData meta1  = new MetaData(metaString);
		String resourcePath = "/metadata/meta2.meta";
		URL url = MetaData.class.getResource(resourcePath);
		
		String path = url.getFile();
		meta1.writeToFile(new File(path));
		
		MetaData meta2 = new MetaData(new File(path));
		
		assertEquals(meta1.getId(), meta2.getId());
		System.out.println(meta2.toString());
	}
}
