package kr.dcos.common.utils.table;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.dcos.common.utils.evaluator.Evaluator;
import kr.dcos.common.utils.table.Column.DataType;

public class TableTest {
	private Table inputTable = new Table();
	private Evaluator niceRowEvalator ;

	@Before
	public void setUp() throws Exception {
		String input =   "1, Hong, 160, 80.5, 2016-01-01 \n"
			+"2, Kim, 170, 50.5, 2016-01-02 \n"
			+"3, Jeong, 180, 60.4, 2016-01-03 \n"
			+"4, Park, 156, 73.2, 2016-01-04 \n"
			+"5, Won, 125, 93.5, 2016-01-05 \n"
			;
		assertEquals(inputTable.getName(),"unknown");
		inputTable.setName("입력테이블");
		assertEquals(inputTable.getName(),"입력테이블");
//			inputTable.setAutoIncrement();
		inputTable.appendColumn(new Column("id",DataType.STRING, 10, true, true, "아이디"));
		inputTable.appendColumn(new Column("name",DataType.STRING, 13, false, false, "성명"));
		inputTable.appendColumn(new Column("height",DataType.INTEGER, 13, false, false, "키"));
		inputTable.appendColumn(new Column("score",DataType.DOUBLE, 5, false, false, "점수"));
		inputTable.appendColumn(new Column("upt_dt",DataType.DATE, 10, false, false, "입력일"));
		
		String[] lines = input.split("\n");
		try {
			for (String line : lines) {
				Row row = inputTable.newRow();
				row.setValues(line, ",");
				inputTable.appendRow(row);				
			}
		} catch (Exception e) {
		}		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBasic1() throws TableException {
		//인덱스로 접근 및 컬럼명으로 접근 가능하다.
		assertEquals(inputTable.getRow(0).getString(1), inputTable.getRow(0).getString("name"));
		
		//row count 는 5이다.
		assertEquals(inputTable.getRowSize(), 5);
		
		//데이터 가져오기
		assertEquals(inputTable.getRow(0).getInteger("height"), 160);
		assertEquals(inputTable.getRow(4).getInteger("height"), 125);
		assertEquals(inputTable.getRow(4).getString("score"), "93.5");
		assertTrue(inputTable.getRow(4).getDouble("score") < 93.51);
		
		assertEquals(inputTable.getRow(4).getDate("upt_dt"), "93.5");
	}

}
