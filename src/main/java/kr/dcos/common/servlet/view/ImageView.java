package kr.dcos.common.servlet.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.dcos.common.servlet.Model;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.MimeTable;

public class ImageView implements View {
	
	private static Logger logger = LoggerFactory.getLogger(ImageView.class);
	

	@SuppressWarnings("unchecked")
	@Override
	public void render(Object model, HttpServletRequest request,
			HttpServletResponse response, boolean include) throws Exception {
		if(model instanceof Model){
			model = ((Model)model).getMap();
			HashMap<String,Object> hashMap = (HashMap<String, Object>) model;
			
			ResovlerInfo info = (ResovlerInfo)hashMap.get("info");
			
			if(info != null){
				if(info.getInputStream() != null){
					sendImageFromStream(response,info);
				}else if(info.getFullPath() != null){
					sendImageFromDisk(response,info);
				}
			}else {
				logger.error("ImageResovlverInfo is null");
			}
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
	private void sendImageFromStream(HttpServletResponse response, ResovlerInfo imageResolverInfo) {
		InputStream inputStream = imageResolverInfo.getInputStream();
		String mimeType = imageResolverInfo.getMimeType();
		response.setContentType(mimeType);
		sendStreamAndClose(response, inputStream);	
	}

	private void sendImageFromDisk(HttpServletResponse response, ResovlerInfo imageResolverInfo)  {
		
		String fullPath = imageResolverInfo.getFullPath();
		String mimeType = imageResolverInfo.getMimeType();
		
		File imageFile = new File(fullPath);
		
		if(mimeType == null){
			mimeType = getMimeType(imageFile);
		}
		
		if(imageFile.canRead()){
			;
		}else{
			logger.error(fullPath + " is not exist or can not read");
			return;
		}
		
		response.setContentType(mimeType);
		InputStream fis=null;
		try {
			fis = new FileInputStream(imageFile);
			sendStreamAndClose(response, fis);
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} finally{
			try {
				if(fis != null) fis.close();
			} catch (IOException e) {
				logger.error("ImageView Error InputStream close:", e);
			}
		}
		
	}

	private void sendStreamAndClose(HttpServletResponse response, InputStream inputStream) {
		try {
			if(inputStream != null){
				IOUtils.copy(inputStream, response.getOutputStream());
				//response.getOutputStream().flush();
			}
		} catch (IOException e) {
			logger.error("ImageView Error:", e);
		} 
	}


}
