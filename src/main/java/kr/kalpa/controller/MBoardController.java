package kr.kalpa.controller;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinWorkerThread;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.servlet.ControllerMethod;
import kr.dcos.common.servlet.ForwardInfo;
import kr.dcos.common.servlet.RequestInfo;
import kr.dcos.common.utils.StrUtils;
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
				//2. 기존에 파일이 있다면 백업을 받는다.
				FileUtils.writeStringToFile(new File(filePath), metaString, "UTF-8");
				fi.addAttribute("metadata", metaString);
				fi.addAttribute("mode", "create");
				fi.addErrorMessage("result", "저장되었습니다:"+filePath);
			}else{
				fi.addErrorMessages("meta", results.split("\n"));
			}
		} catch (MBoardException e) {
			
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
}
