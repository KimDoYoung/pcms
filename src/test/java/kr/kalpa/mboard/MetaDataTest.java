package kr.kalpa.mboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.kalpa.mboard.MetaData.DataType;
import kr.kalpa.mboard.MetaData.Field;

public class MetaDataTest {
	private String metaString = ""
			+ " #                                      \n"
			+ " # 게시판 자동생성을 위한 메타데이터                   \n"
			+ " #                                      \n"
			+ " $id: movies                            \n"
			+ " $name:영화목록                              \n"
			+ " $type : borad1                         \n"
			+ " $desc:                                 \n"
			+ " 	 수집한 영화 목록들                         \n"
			+ " 	sosoosofowwof                        \n"
			+ " 	sofoososoeofw                        \n"
			+ " desc$                                  \n"
            + "                                        \n"
			+ " $fields:                               \n"
			+ "  id, 아이디, string, 10, Y                \n"
			+ "  hname, 한글 제목, string, 200, N          \n"
			+ "  ename, 영어 제목, string, 200, N          \n"
			+ "  year, 제작년도, string, 4, N              \n"
			+ "  country, 제작국가, string, 10,N           \n"
			+ "   jumsu, 점수, int,2,N          \n"
			+ "  cost, 가격, number, 10.2,N          \n"
			+ " upd_dt, 입력일시, date, 20,N          \n"
			
			+ " fields$"
			+"";
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws MBoardException {
		MetaData metaData  = new MetaData(metaString);
		assertEquals(metaData.getId(), "movies");
		assertEquals(metaData.getName(), "영화목록");
		assertEquals(metaData.getType(), "borad1");
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
	@Test
	public void loadFileTest() throws IOException, MBoardException{
		String resourcePath = "/metadata/example.meta";
		URL url = MetaData.class.getResource(resourcePath);
		assertNotNull(url);
		String filePath = url.getFile();
		File file = new File(filePath);
		MetaData metaData  = new MetaData(file);
		assertEquals(metaData.getId(), "movies");
		assertEquals(metaData.getName(), "영화목록");
		assertEquals(metaData.getType(), "borad1");
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
}
