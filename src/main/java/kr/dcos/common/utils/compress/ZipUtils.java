package kr.dcos.common.utils.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class ZipUtils {
	

	/**
	 * 한개의 파일을 압축하여 zip파일을 만든다
	 * @param file zip하고자하는 원래의 파일
	 * @param zip  만들어질 zip파일
	 * @throws IOException
	 */
	public static void zip(File file, File zip) throws IOException {
		ZipOutputStream zos = null;
		try {
			String name = file.getName();
			zos = new ZipOutputStream(new FileOutputStream(zip));

			ZipEntry entry = new ZipEntry(name);
			zos.putNextEntry(entry);

			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				byte[] byteBuffer = new byte[1024];
				int bytesRead = -1;
				while ((bytesRead = fis.read(byteBuffer)) != -1) {
					zos.write(byteBuffer, 0, bytesRead);
				}
				zos.flush();
			} finally {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
			zos.closeEntry();

			zos.flush();
		} finally {
			try {
				zos.close();
			} catch (Exception e) {
			}
		}
	}
	public static  File zip(List<File> files, String zipFileName) {
		return zip(files,zipFileName, null);
	}
	/**
	 * 여러개의 파일을 한개의 zip파일로 만든다
	 * @param files 압축의 대상이 되는 File객체들 리스트 
	 * @param zipFileName 압축되어 만들어질 zip파일
	 * @return
	 */
	public static  File zip(List<File> files, String zipFileName, String baseFolder) {
		String folder = "";
		if(baseFolder == null || baseFolder.length() < 1) {
			folder = "";
		}else {
			if(folder.endsWith(File.separator)==false){
				folder = baseFolder+File.separator;
			}
		}
		File zipfile = new File(zipFileName);
		byte[] buf = new byte[1024 * 4];
	    try {
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
	        out.setLevel(Deflater.BEST_SPEED);
	        for (File file : files){
	            FileInputStream in = new FileInputStream(file);
	            out.putNextEntry(new ZipEntry(folder+file.getName()));
	            int len;
	            while((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.closeEntry();
	            in.close();
	        }
	        out.close();
	        return zipfile;
	    } catch (IOException ex) {
	        System.err.println(ex.getMessage());
	    }
	    return null;
	}
	public static byte[] toByteArray(File file) throws IOException{
		InputStream input = new  FileInputStream(file);
		return IOUtils.toByteArray(input);
	}
	public static byte[] toByteArray(String fileName) throws IOException{
		InputStream input = new  FileInputStream(fileName);
		return IOUtils.toByteArray(input);
	}
}
