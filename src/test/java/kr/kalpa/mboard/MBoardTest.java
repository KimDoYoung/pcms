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
import kr.kalpa.db.sql.SqlFactory;

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
	public void testCreateBoard() throws SqlExecutorException, MBoardException{
		MBoard mBoard = new MBoard(new MetaData(metaString));
		//mBoard.createBoard();
		MetaData metaData = mBoard.getMetaData();
		if(metaData.getDataSourceType().equals("database")){
			String databaseName = metaData.getDataSourceId();
			 
			String sqlString = SqlFactory.getSqlGenerator(databaseName).createTable(metaData);
			DatabaseManager.getInstance().getSqlExecutor(databaseName).executeDirect(sqlString);
			
		}		

	}

}
