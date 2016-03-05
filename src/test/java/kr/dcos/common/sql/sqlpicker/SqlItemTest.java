package kr.dcos.common.sql.sqlpicker;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.dcos.common.sql.sqlpicker.SqlItem;
import kr.dcos.common.sql.sqlpicker.SqlItem.SqlLine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlItemTest {
	
	private static Logger logger = LoggerFactory.getLogger(SqlItemTest.class);
	SqlItem item  = null;

	@Before
	public void setUp() throws Exception {
		item = new SqlItem();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() throws Exception {
		SqlItem item = new SqlItem();
		String sqlTemplate = "select * from #table# where name=$name$ and @ref1@";
		item.setSqlTemplate(sqlTemplate);
		
		
		logger.debug(item.toString());
	}
	
	
}
