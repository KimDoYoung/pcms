package kr.dcos.common.utils.evaluator;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.dcos.common.utils.table.Column;
import kr.dcos.common.utils.table.Column.DataType;
import kr.dcos.common.utils.table.Row;
import kr.dcos.common.utils.table.Table;
import kr.dcos.common.utils.table.TableException;

public class EvaluatorTest {

	private Table inputTable = new Table();
	private Evaluator niceRowEvalator ;
	@Before
	public void setUp() throws Exception {
		String input =   "1,KED,A,	10,Y,N,1    \n"
			+"1,KED,B,	25,Y,N,1    \n"
			+"1,KED,C,	20	,Y,N,1  \n"
			+"1,KED,D,	25	,Y,N,1  \n"
			+"1,KED,E,	20	,Y,N,2  \n"
			+"1,A,B,	100	,Y,N,1  \n"
			+"1,C,D,	80	,Y,N,1  \n"
			+"1,C,G,	20	,Y,N,1  \n"
			+"1,가,KED,	80	,Y,N,1  \n"
			+"1,가,나,	20	,Y,N,1  \n"
			+"1,다,가,	60	,Y,N,1  \n"
			+"1,라,가,	10	,Y,N,1  \n"
			+"1,마,나,	30	,Y,N,1  \n"
			+"1,바,나,	20	,Y,N,1  \n"
			+"1,나,사,	40	,Y,N,1  \n"
			+"1,나,아,	30	,Y,N,1  \n"
			+"1,나,KED,	30	,Y,N,1  \n"
			+"1,D,E,0	,N,Y,2		\n";
		
		inputTable.setName("입력테이블");
//		inputTable.setAutoIncrement();
		inputTable.appendColumn(new Column("mAldConfNo",DataType.STRING, 13, false, false, "중기번호"));
		inputTable.appendColumn(new Column("kedcdPid",DataType.STRING, 13, false, false, "회사ID"));
		inputTable.appendColumn(new Column("sthKedcdPid",DataType.STRING, 13, false, false, "주주ID"));
		inputTable.appendColumn(new Column("eqrt",DataType.DOUBLE, 5, false, false, "지분율"));
		inputTable.appendColumn(new Column("ocYn",DataType.STRING, 1, false, false, "외감여부"));
		inputTable.appendColumn(new Column("spcRelYn",DataType.STRING, 1, false, false, "특수관계인여부"));
		inputTable.appendColumn(new Column("sthEnpTyp",DataType.STRING, 1, false, false, "회사의종류"));
		
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
	public void testNiceEvaluator() throws EvaluatorException {
		String s = inputTable.toString();
		System.out.println(s);
			
		niceRowEvalator = new Evaluator(new HashMap<String,Object>(), "1==1");
		assertNotNull(niceRowEvalator);
		niceRowEvalator = new Evaluator(new HashMap<String,Object>());
		assertNotNull(niceRowEvalator);
	}

	/**
	 * 사칙연산
	 * @throws EvaluatorException
	 * @throws KedTableException
	 */
	@Test 
	public void testEvalString() throws EvaluatorException, TableException {
		Row row = inputTable.getRow(0);
		row.setValue("ocyn", "Y");
		row.setValue("eqrt", 3.4);
		niceRowEvalator = new Evaluator(row.toMap());
		String s;

		s = niceRowEvalator.evalString("(1+2)+4");       
		assertTrue(Double.parseDouble(s) == 7);

		
		s = niceRowEvalator.evalString("1+2");       
		assertTrue(Double.parseDouble(s) == 3);


		// 더하기 연산
		s = niceRowEvalator.evalString("1+1");       assertEquals(s, "2.00");     //n + n
		s = niceRowEvalator.evalString("1+'23'");    assertEquals(s, "123");//n + s
		s = niceRowEvalator.evalString("1+eqrt");    assertEquals(s, "4.40");//n + f(n)
		s = niceRowEvalator.evalString("1+ocYn");    assertEquals(s, "1Y");//n + f(s)
		                                             
		s = niceRowEvalator.evalString("'1'+23");    assertEquals(s, "123");//s + n
		s = niceRowEvalator.evalString("'1'+'23'");  assertEquals(s, "123");//s + s
		s = niceRowEvalator.evalString("'1'+eqrt");  assertEquals(s, "13.4"); //s + f(n)
		s = niceRowEvalator.evalString("'1'+ocYn");  assertEquals(s, "1Y"); //s + f(s)
                                                     
		s = niceRowEvalator.evalString("eqrt+23");   assertEquals(s, "26.40");//f(n) + n
		s = niceRowEvalator.evalString("eqrt+'23'"); assertEquals(s, "3.423");//f(n) + s
		s = niceRowEvalator.evalString("eqrt+eqrt"); assertEquals(s, "6.80");//f(n) + f(n)
		s = niceRowEvalator.evalString("eqrt+ocYn"); assertEquals(s, "3.4Y");//f(n) + f(s)
                                                     
		s = niceRowEvalator.evalString("ocYn+23");   assertEquals(s, "Y23");//f(s) + n
		s = niceRowEvalator.evalString("ocYn+'23'"); assertEquals(s, "Y23");//f(s) + s
		s = niceRowEvalator.evalString("ocYn+eqrt"); assertEquals(s, "Y3.4");//f(s) + f(n)
		s = niceRowEvalator.evalString("ocYn+ocYn"); assertEquals(s, "YY");//f(s) + f(s)
		
		// 빼기 연산
		s = niceRowEvalator.evalString("2-1");       assertEquals(s, "1.00");     //n + n
		s = niceRowEvalator.evalString("4 - eqrt");    assertTrue(s.startsWith("0.60"));//n + f(n)                                                     
		s = niceRowEvalator.evalString("eqrt-3");	assertEquals(s,"0.40");//f(n) + n
		s = niceRowEvalator.evalString("eqrt-eqrt"); assertEquals(s, "0.00");//f(n) + f(n)

		// 곱하기 연산
		s = niceRowEvalator.evalString("2*3");       assertEquals(s, "6.00");     //n + n
		s = niceRowEvalator.evalString("4 * eqrt");    assertEquals(s,"13.60");//n + f(n)                                                     
		s = niceRowEvalator.evalString("eqrt* 4");	assertEquals(s,"13.60");//f(n) + n
		s = niceRowEvalator.evalString("eqrt*eqrt"); assertEquals(s, "11.56");//f(n) + f(n)
		// 나누기 연산
		s = niceRowEvalator.evalString("4/2");       assertEquals(s, "2.00");     //n + n
		s = niceRowEvalator.evalString("4 / eqrt");    assertEquals(s,"1.18");//n + f(n)                                                     
		s = niceRowEvalator.evalString("eqrt / 4");	assertEquals(s,"0.85");//f(n) + n
		s = niceRowEvalator.evalString("eqrt/eqrt"); assertEquals(s, "1.00");//f(n) + f(n)	
		
		// 나머지 연산
		s = niceRowEvalator.evalString("4%3");       assertEquals(s, "1.00");     //n + n
		s = niceRowEvalator.evalString("4%4");       assertEquals(s, "0.00");     //n + n
		s = niceRowEvalator.evalString("4%0");       assertEquals(s, "NaN");     //n + n
		s = niceRowEvalator.evalString("4%4");       assertEquals(s, "0.00");     //n + n
		s = niceRowEvalator.evalString("eqrt%eqrt"); assertEquals(s, "0.00");//f(n) + f(n)			
	}
	/**
	 * 비교연산
	 * @throws EvaluatorException
	 * @throws KedTableException
	 */
	@Test 
	public void testCompareOperation() throws EvaluatorException, TableException {
		Row row = inputTable.getRow(0);
		row.setValue("ocyn", "Y");
		row.setValue("eqrt", 3.4);
		niceRowEvalator = new Evaluator(row.toMap());
		boolean b;
		
		//bigger : >
		b = niceRowEvalator.evalBoolean("eqrt > 30");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("4>3");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4>4");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("eqrt>4");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("eqrt>3.4");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("eqrt>3.3");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4>eqrt");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("3.4>eqrt");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("4.4>eqrt");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("'222'>'111'");   assertTrue(b); //문자비교
		
		//lessthan : <
		b = niceRowEvalator.evalBoolean("eqrt < 30");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4<5");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4<3");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("4<4");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("eqrt<4");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("eqrt<3.4");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("eqrt<3.3");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("4<eqrt");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("3.4<eqrt");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("4.4<eqrt");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("'111' < '222'");   assertTrue(b); //문자비교
		
		//bigger_equal : >=
		b = niceRowEvalator.evalBoolean("4>=3");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4>=4");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("eqrt>=4");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("eqrt>=3.4");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("eqrt>=3.3");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4>=eqrt");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("3.4>=eqrt");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4.4>=eqrt");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("'222' >= '111'");   assertTrue(b); //문자비교
		b = niceRowEvalator.evalBoolean("'222' >= '222'");   assertTrue(b); //문자비교
		
		//less_equal : <=
		b = niceRowEvalator.evalBoolean("4<=3");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("4<=4");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("eqrt<=4");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("eqrt<=3.4");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("eqrt<=3.3");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("4<=eqrt");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("3.4<=eqrt");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("4.4<=eqrt");   assertFalse(b);
		
		//equal 문자와 문자, 숫자와 숫자만 비교할 수 있음
		b = niceRowEvalator.evalBoolean("'abc'=='abc'");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("'abc' eq 'abc'");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("'abc' eq 'abC'");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("1 eq 1");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("1==1.0");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("1.0==1");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("1.23==1.23");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("eqrt==eqrt");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("ocYn == 'Y'");   assertTrue(b);

		//not equal 문자와 문자, 숫자와 숫자만 비교할 수 있음
		b = niceRowEvalator.evalBoolean("'abc'!='abc'");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("'abc' ne 'abc'");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("'abc' ne 'abC'");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("1 ne 1");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("1!=1.0");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("1.0!=1");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("1.23!=1.23");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("eqrt!=eqrt");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("ocYn=='Y'");   assertTrue(b);

	}
	@Test 
	public void testLogicalOperation() throws EvaluatorException, TableException {
		Row row = inputTable.getRow(0);
		row.setValue("ocyn", "Y");
		row.setValue("eqrt", 3.4);
		Map<String,Object> o = row.toMap();
		for (Entry<String, Object> entry : o.entrySet()) {
			if(entry.getValue() instanceof String){
				System.out.println(entry.getKey() + " is String");
			}else if(entry.getValue() instanceof Double){
				System.out.println(entry.getKey() + " is Double");
			}else {
				System.out.println(entry.getKey() + " is unknown");
			}
		}
		niceRowEvalator = new Evaluator(row.toMap());
		boolean b;
		//and 
		b = niceRowEvalator.evalBoolean("true && true ");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("true and true ");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("false and true ");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("true and false ");   assertFalse(b);
		//or
		b = niceRowEvalator.evalBoolean("true || true ");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("true or true ");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("false || true ");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("true or false ");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("false or false ");   assertFalse(b);
		
		//not
		b = niceRowEvalator.evalBoolean("!true");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("not true");   assertFalse(b);
		b = niceRowEvalator.evalBoolean("!false");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("!false and !false");   assertTrue(b);
		b = niceRowEvalator.evalBoolean("!false && !false");   assertTrue(b);
		
	}
	/**
	 * 연산자 우선순위
	 * @throws EvaluatorException
	 * @throws KedTableException
	 */
	@Test 
	public void testStatement1() throws EvaluatorException, TableException {
		Row row = inputTable.getRow(0);
		row.setValue("ocyn", "Y");
		row.setValue("eqrt", 3.4);
		niceRowEvalator = new Evaluator(row.toMap());
		Double d1 = niceRowEvalator.evalNumber("1+2*3");
		Double d2 = niceRowEvalator.evalNumber("1+(2*3)");
		assertEquals(d1,d2);
		
		d1 = niceRowEvalator.evalNumber("eqrt * ( 30 / 100 ) ");
		d2 = niceRowEvalator.evalNumber("eqrt * 0.3 ");
		assertEquals(d1,d2);

		assertTrue(niceRowEvalator.evalBoolean(" (1+2*3) > 6"));
		assertTrue(niceRowEvalator.evalBoolean(" (eqrt * ( 30 / 100 )) < 2"));
	}

}
