package kr.kalpa.mboard;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.dcos.common.sql.database.DatabaseManager;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.dcos.common.utils.table.TableException;
import kr.kalpa.db.sql.SqlFactory;
import kr.kalpa.db.sql.SqlGenerator;

public class MBoardTest {

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
	public void testNew() throws MBoardException {
		MBoard mBoard = new MBoard(new MetaData(metaString));
		assertNotNull(mBoard);
		System.out.println(mBoard.toString());
	}
	@Test
	public void testCreateBoard() throws SqlExecutorException, MBoardException, TableException{
		MBoard mBoard = new MBoard(new MetaData(metaString));
		//mBoard.createBoard();
		MetaData metaData = mBoard.getMetaData();
		if(metaData.getDataSourceType().equals("database")){
			String databaseName = metaData.getDataSourceId();
			SqlGenerator sg = SqlFactory.getSqlGenerator(databaseName);
			assertNotNull(sg);
			String sqlString = sg.existTable(metaData.getId());
			int i = (Integer)DatabaseManager.getInstance().getSqlExecutor(databaseName).scalarDirect(sqlString);
			System.out.println("check exist or not table :" + metaData.getId());
			if(i > 0){ // 있으면 지운다.
				sqlString = sg.dropTable(metaData.getId());
				DatabaseManager.getInstance().getSqlExecutor(databaseName).executeDirect(sqlString);
				System.out.println("drop table :" + metaData.getId());
			}
			sqlString = sg.createTable(metaData);
			DatabaseManager.getInstance().getSqlExecutor(databaseName).executeDirect(sqlString);
			System.out.println("create table :" + metaData.getId());
			
		}		
	}

}
