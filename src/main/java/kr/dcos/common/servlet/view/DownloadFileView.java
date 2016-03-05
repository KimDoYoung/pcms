package kr.dcos.common.servlet.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.MimeTable;

import kr.dcos.common.servlet.Model;

public class DownloadFileView implements View  {
	private static Logger logger = LoggerFactory
			.getLogger(DownloadFileView.class);
	

	@SuppressWarnings("unchecked")
	@Override
	public void render(Object model, HttpServletRequest request,
			HttpServletResponse response, boolean include) throws Exception {
		
		if(model instanceof Model){
			model = ((Model)model).getMap();
			HashMap<String,Object> hashMap = (HashMap<String, Object>) model;
			ResovlerInfo info = (ResovlerInfo)hashMap.get("info");
			//String fullPath = info.getFullPath();
			download(response,info);
		}
	}
	/**
	 * File로 mimeType을 리턴한다.
	 * @param file
	 * @return
	 */
	public static String getMimeType(File file) {
		MimeTable mt = MimeTable.getDefaultTable();
		String mimeType = mt.getContentTypeFor(file.getName());
		 if (mimeType == null) 
         { 
             mimeType = "application/octet-stream"; 
         }
		return mimeType;
	}
	private void download(HttpServletResponse response, ResovlerInfo info) {
		String fullPath = info.getFullPath();
		if(fullPath == null){
			return;
		}
		File targetFile = new File(fullPath);
		if(!targetFile.isFile() ||  !targetFile.exists() ){ //존재하지 않으면
			logger.error("["+targetFile +"] is not found");
			return;
		}
		String mimeType = info.getMimeType();
		if(mimeType == null){
			mimeType = getMimeType(targetFile);
		}
		download(response,targetFile,mimeType);
	}
	private void download(HttpServletResponse response, File targetFile,
			String mimeType) {
		
		
		BufferedInputStream buf=null;
		OutputStream  myOut=null;
		   
		try {

			myOut = response.getOutputStream();
			response.setContentType(mimeType);

			response.addHeader("Content-Disposition",
					"attachment; filename=" + targetFile.getName());

			response.setContentLength((int) targetFile.length());

			buf = new BufferedInputStream(new FileInputStream(targetFile));
			
			int readBytes = 0;
			while ((readBytes = buf.read()) != -1){
				myOut.write(readBytes);
			}
		} catch (IOException ioe) {
			logger.error("",ioe);
		} finally {

			if (myOut != null) {
				try {
					myOut.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
			if (buf != null) {
				try {
					buf.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}

		}
		
	}


}
