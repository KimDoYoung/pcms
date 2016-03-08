package kr.kalpa.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.servlet.ControllerMethod;
import kr.dcos.common.servlet.ForwardInfo;
import kr.dcos.common.servlet.RequestInfo;

public class MBoard {
	private static Logger logger = LoggerFactory.getLogger(MBoard.class);
	@ControllerMethod
	public ForwardInfo test(RequestInfo ri) {
		logger.debug(ri.toString());
		
		ForwardInfo fw = new ForwardInfo("/mboard/index.jsp");
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
}
