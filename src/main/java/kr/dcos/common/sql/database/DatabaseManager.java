package kr.dcos.common.sql.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dcos.common.sql.SqlExecuter;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.dcos.common.sql.exception.SqlPickerException;
import kr.dcos.common.sql.sqlpicker.SqlPicker;
import kr.dcos.common.utils.ConvertUtil;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 데이터베이스관련 정보를 가지고 있는 클래스<br>
 * Singleton으로 작성됨 <br>
 * 
 * @author Kim Do Young
 *
 */
public class DatabaseManager {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private static final String dbConfigXml = "/database-config.xml";
	private boolean initialized=false;
	
	private Map<String,Database> dbMap; //dbInfo
	
	private volatile static DatabaseManager instance;
	
	public static DatabaseManager getInstance() throws SqlExecutorException {
		if (instance == null) {
			synchronized (DatabaseManager.class) {
				if (instance == null) {
					instance = new DatabaseManager();
					instance.setup();
				}
			}
		}
		return instance;
	}
	private DatabaseManager(){
		dbMap = new HashMap<String, Database>();
		//loadConfig(dbConfigXml);
	}
	/**
	 * database-config.xml을 읽어서 database 객체의 property들을 채운다. <br>
	 * database-config.xml 정해진 파일명이며 그 안의 각 element들은 정해져있다.
	 * 
	 * @throws SqlExecutorException
	 */
	public void setup() throws SqlExecutorException{
		if(initialized == false){
			loadConfig(dbConfigXml);
			initialized = true;
		}
	}
	/**
	 * XML로딩 database-config.xml을 다
	 * @param configXml
	 * @throws SqlExecutorException 
	 */
	private void loadConfig(String configXml) throws SqlExecutorException{
		 SAXBuilder builder = new SAXBuilder();
		 InputStream is = DatabaseManager.class.getResourceAsStream(configXml);
		 if(is == null){
			 logger.error(configXml+ " loading error");
		 }
		 // File xmlFile = new File(configXml);
		  try {
	 
			Document document = (Document) builder.build(is);
			Element rootNode =  document.getRootElement();
			List<Element> dbList = rootNode.getChildren("database");
			for (int i = 0; i < dbList.size(); i++) {
			   Database database = new Database();
			   
			   Element dbElement = dbList.get(i);
			   String id = dbElement.getAttribute("id").getValue();
			   
			   //basic-info
			   Element e = dbElement.getChild("basic-info");
			   String url = e.getChildText("url");
			   String userId = e.getChildText("userId");
			   String password = e.getChildText("password");
			   
			   BasicInfo basicInfo = new BasicInfo();
			   basicInfo.setUrl(url);
			   basicInfo.setUserId(userId);
			   basicInfo.setPassword(password);
			   database.setBasicInfo(basicInfo);
			   
			   //connection-pool-info
			   e = dbElement.getChild("connection-pool-info");
			   String driver = e.getChildText("driver");
			   Integer minConnection = ConvertUtil.toInteger(e.getChildText("minConnection"),10);
			   Integer maxConnectionSize = ConvertUtil.toInteger(e.getChildText("maxConnectionSize"),100);
			   Integer loginTimeout = ConvertUtil.toInteger(e.getChildText("loginTimeout"),100);
			   Integer maxWait = ConvertUtil.toInteger(e.getChildText("maxWait"),5000);		
			   Boolean defaultAutoCommit = ConvertUtil.toBoolean(e.getChildText("defaultAutoCommit"),false);
			   
			   ConnectionInfo connectionInfo = new ConnectionInfo();
			   connectionInfo.setDriver(driver);
			   connectionInfo.setMinConnection(minConnection);
			   connectionInfo.setMaxConnectionSize(maxConnectionSize);
			   connectionInfo.setLoginTimeout(loginTimeout);
			   connectionInfo.setMaxWait(maxWait);
			   connectionInfo.setDefaultAutoCommit(defaultAutoCommit);
			   
			   ConnectionManager cm = new ConnectionManager();
			   cm.setConnectionInfo(connectionInfo);
			   cm.setBasicInfo(basicInfo);
			   
			   database.setConnManager(cm);
			   
			   //sql-files
			   SqlExecuter sqlExecutor = new SqlExecuter();
			   e = dbElement.getChild("sql-files");
			   List<Element> listSqlFiles = e.getChildren();
			   for(int j=0;j<listSqlFiles.size();j++){
				   Element sqlElement = listSqlFiles.get(j);
				   if(sqlElement.getName().equalsIgnoreCase("file")){
					   File file = SqlPicker.getFileFromResourcePath(sqlElement.getValue());
					   sqlExecutor.loadFromResourceFile(file);
				   }else if(sqlElement.getName().equalsIgnoreCase("folder")){
					   File folder = SqlPicker.getFileFromResourcePath(sqlElement.getValue());
					   sqlExecutor.loadFromResourceFolder(folder);
				   }
			   }
			   database.setSqlExecuter(sqlExecutor);
			   dbMap.put(id, database);
			}
	 
		  } catch (IOException io) {
			 throw new SqlExecutorException(io.getMessage());
		  } catch (JDOMException jdomex) {
			  throw new SqlExecutorException(jdomex.getMessage());
		  } catch (SqlPickerException e) {
			  throw new SqlExecutorException(e.getMessage());
		}		

		
	}

	public  SqlExecuter getSqlExecutor(String databaseName) throws SqlExecutorException {
		Database database = dbMap.get(databaseName);
		if(database == null) {
			throw new SqlExecutorException(databaseName + " is not valid database name, check database_config.xml");
		}
		SqlExecuter sqlExec;
		sqlExec = database.getSqlExecuter();
		return sqlExec;
	}
	public Connection getConnection(String dbName) throws SqlExecutorException {
		if (dbMap.containsKey(dbName)) {
			Database db = dbMap.get(dbName);
			if (db != null) {
				return db.getConnManager().getConnection();
			}
		}
		return null;
	}
}

