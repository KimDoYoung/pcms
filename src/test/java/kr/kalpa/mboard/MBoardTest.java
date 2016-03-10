package kr.kalpa.mboard;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MBoardTest {

	private String metaString = ""
			+ " #                                      \n"
			+ " # 게시판 자동생성을 위한 메타데이터                   \n"
			+ " #                                      \n"
			+ " $id: movies                            \n"
			+ " $name:영화목                              \n"
			+ " $type : borad1                         \n"
			+ " $desc:                                 \n"
			+ " 	 수집한 영록화 목록들                         \n"
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
		MBoard mBoard = new MBoard(new MetaData(metaString));
		assertNotNull(mBoard);
		System.out.println(mBoard.toString());
	}

}
