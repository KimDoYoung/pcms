package kr.kalpa.controller;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinWorkerThread;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.servlet.ControllerMethod;
import kr.dcos.common.servlet.ForwardInfo;
import kr.dcos.common.servlet.RequestInfo;
import kr.dcos.common.sql.database.DatabaseManager;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.dcos.common.utils.StrUtils;
import kr.dcos.common.utils.table.TableException;
import kr.kalpa.db.sql.SqlFactory;
import kr.kalpa.db.sql.SqlGenerator;
import kr.kalpa.mboard.MBoard;
import kr.kalpa.mboard.MBoardException;
import kr.kalpa.mboard.MetaData;

public class MBoardController {
	private static Logger logger = LoggerFactory.getLogger(MBoardController.class);
	@ControllerMethod
	public ForwardInfo test(RequestInfo ri) {
		logger.debug(ri.toString());

		ForwardInfo fw = new ForwardInfo("/mboard/index.jsp");
		fw.setAttributesFromRequest(ri.getRequest());
		return  fw;			
	}
	/**
	 * jsp를 만들면서 그 페이지를 호출한다
	 * 
	 * 
	 * @param ri
	 * @return
	 * @throws IOException
	 */
	@ControllerMethod
	public ForwardInfo make(RequestInfo ri) throws IOException {
		String data = "abc홍길동123";
		String relativeWebPath = "/mboard/aaa.jsp";
		String absoluteDiskPath = ri.getServletContext().getRealPath(relativeWebPath);
			
		File file = new  File(absoluteDiskPath);
		FileUtils.writeStringToFile(file, data);
		ForwardInfo fw = new ForwardInfo("/mboard/aaa.jsp");
		return  fw;			
	}	
	@ControllerMethod
	public ForwardInfo createBoard(RequestInfo ri) throws IOException {
		String mode = ri.getParameter("mode");
		ForwardInfo fi = new ForwardInfo("/mboard/createBoard.jsp");
			
		if(StrUtils.isEmpty(mode)){
			fi.addAttribute("mode", "create");
			return fi;
		}
		//TODO 흐름을 생각해 볼것 
		String metaString = ri.getParameter("metadata");
		try {
			MetaData metaData = new MetaData(metaString);
			String results = metaData.validCheck();
			if(results.equals("OK")){
				//1. metaString을 저장한다. 위치는 /mboard/meta
				String filePath = ri.getRealPath("/mboard/meta-data/"+metaData.getId()+".meta");
				//2. 기존에 파일이 있다면 메세지로 간다
				File file = new File(filePath);
				fi.setPath("/common/message.jsp");
				if(file.exists()){
					fi.setAttribute("message", "같은 id로 이미 게시판이 존재합니다. 삭제한 후 다시 만들 수 있습니다. 또 수정할 수 있습니다");
					return fi;
				}
				//3. meta 데이터 저장
				FileUtils.writeStringToFile(file, metaString, "UTF-8");
				//4. create table
				createTable(metaData);
				//5. jsp파일들 생성.
				createJsps(metaData);
				//6. message.jsp로 이동
				fi.setAttribute("message",  "게시판이 생성되었습니다.meta file:"+filePath);
				
			}else{
				fi.addErrorMessages("meta", results.split("\n"));
			}
		} catch (MBoardException e) {
			fi.addErrorMessage("meta", e.getMessage());
		} catch (SqlExecutorException e) {
			fi.addErrorMessage("meta", e.getMessage());
		} catch (TableException e) {
			fi.addErrorMessage("meta", e.getMessage());
		}
		return fi;
//		//TODO create meta data file db에 넣을 것인가는 생각봐야함.
//		MetaData meta = (MetaData) ri.getPOJO(MetaData.class);
//		
//		MBoard mBoard = new MBoard(meta);
//		
//		System.out.println("what.....");
//		//TODO create data source
//		//TODO create jsp files
//		return null;
//	}
	}
	private void createJsps(MetaData metaData) {
		// TODO Auto-generated method stub
		
	}
	private void createTable(MetaData metaData) throws SqlExecutorException, TableException {
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
