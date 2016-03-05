package kr.dcos.common.utils.compress;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

public class Compress {
	private static boolean debug = true;

	public void unzip(File zippedFile) throws IOException {
		unzip(zippedFile, Charset.defaultCharset().name());
	}

	public void unzip(File zippedFile, String charsetName) throws IOException {
		unzip(zippedFile, zippedFile.getParentFile(), charsetName);
	}

	public void unzip(File zippedFile, File destDir) throws IOException {
		unzip(new FileInputStream(zippedFile), destDir, Charset
				.defaultCharset().name());
	}

	public void unzip(File zippedFile, File destDir, String charsetName)
			throws IOException {
		unzip(new FileInputStream(zippedFile), destDir, charsetName);
	}

	public void unzip(InputStream is, File destDir) throws IOException {
		unzip(is, destDir, Charset.defaultCharset().name());
	}

	public void unzip(InputStream is, File destDir, String charsetName)
			throws IOException {
		ZipArchiveInputStream zis;
		ZipArchiveEntry entry;
		String name;
		File target;
		int nWritten = 0;
		BufferedOutputStream bos;
		byte[] buf = new byte[1024 * 8];

		zis = new ZipArchiveInputStream(is, charsetName, false);
		while ((entry = zis.getNextZipEntry()) != null) {
			name = entry.getName();
			target = new File(destDir, name);
			if (entry.isDirectory()) {
				target.mkdirs(); /* does it always work? */
				debug("dir  : " + name);
			} else {
				target.createNewFile();
				bos = new BufferedOutputStream(new FileOutputStream(target));
				while ((nWritten = zis.read(buf)) >= 0) {
					bos.write(buf, 0, nWritten);
				}
				bos.close();
				debug("file : " + name);
			}
		}
		zis.close();
	}

	/**
	 * compresses the given file(or dir) and creates new file under the same
	 * directory.
	 * 
	 * @param src
	 *            file or directory
	 * @throws IOException
	 */
	public void zip(File src) throws IOException {
		zip(src, Charset.defaultCharset().name(), true);
	}

	/**
	 * zips the given file(or dir) and create
	 * 
	 * @param src
	 *            file or directory to compress
	 * @param includeSrc
	 *            if true and src is directory, then src is not included in the
	 *            compression. if false, src is included.
	 * @throws IOException
	 */
	public void zip(File src, boolean includeSrc) throws IOException {
		zip(src, Charset.defaultCharset().name(), includeSrc);
	}

	/**
	 * compresses the given src file (or directory) with the given encoding
	 * 
	 * @param src
	 * @param charSetName
	 * @param includeSrc
	 * @throws IOException
	 */
	public void zip(File src, String charSetName, boolean includeSrc)
			throws IOException {
		zip(src, src.getParentFile(), charSetName, includeSrc);
	}

	/**
	 * compresses the given src file(or directory) and writes to the given
	 * output stream.
	 * 
	 * @param src
	 * @param os
	 * @throws IOException
	 */
	public void zip(File src, OutputStream os) throws IOException {
		zip(src, os, Charset.defaultCharset().name(), true);
	}

	/**
	 * compresses the given src file(or directory) and create the compressed
	 * file under the given destDir.
	 * 
	 * @param src
	 * @param destDir
	 * @param charSetName
	 * @param includeSrc
	 * @throws IOException
	 */
	public void zip(File src, File destDir, String charSetName,
			boolean includeSrc) throws IOException {
		String fileName = src.getName();
		if (!src.isDirectory()) {
			int pos = fileName.lastIndexOf(".");
			if (pos > 0) {
				fileName = fileName.substring(0, pos);
			}
		}
		fileName += ".zip";

		File zippedFile = new File(destDir, fileName);
		if (!zippedFile.exists())
			zippedFile.createNewFile();
		zip(src, new FileOutputStream(zippedFile), charSetName, includeSrc);
	}

	public void zip(File src, OutputStream os, String charsetName,
			boolean includeSrc) throws IOException {
		ZipArchiveOutputStream zos = new ZipArchiveOutputStream(os);
		zos.setEncoding(charsetName);
		FileInputStream fis;

		int length;
		ZipArchiveEntry ze;
		byte[] buf = new byte[8 * 1024];
		String name;

		Stack<File> stack = new Stack<File>();
		File root;
		if (src.isDirectory()) {
			if (includeSrc) {
				stack.push(src);
				root = src.getParentFile();
			} else {
				File[] fs = src.listFiles();
				for (int i = 0; i < fs.length; i++) {
					stack.push(fs[i]);
				}
				root = src;
			}
		} else {
			stack.push(src);
			root = src.getParentFile();
		}

		while (!stack.isEmpty()) {
			File f = stack.pop();
			name = toPath(root, f);
			if (f.isDirectory()) {
				debug("dir  : " + name);
				File[] fs = f.listFiles();
				for (int i = 0; i < fs.length; i++) {
					if (fs[i].isDirectory())
						stack.push(fs[i]);
					else
						stack.add(0, fs[i]);
				}
			} else {
				debug("file : " + name);
				ze = new ZipArchiveEntry(name);
				zos.putArchiveEntry(ze);
				fis = new FileInputStream(f);
				while ((length = fis.read(buf, 0, buf.length)) >= 0) {
					zos.write(buf, 0, length);
				}
				fis.close();
				zos.closeArchiveEntry();
			}
		}
		zos.close();
	}
	/**
	 * 여러개의 파일을 한개의 zip파일로 만든다
	 * @param files 압축의 대상이 되는 File객체들 리스트 
	 * @param zipFileName 압축되어 만들어질 zip파일
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ArchiveException 
	 */
	public static  File zip(List<File> files, String zipFileName, String baseFolder) throws FileNotFoundException, IOException, ArchiveException {
		String folder = "";
		if(baseFolder == null || baseFolder.length() < 1) {
			folder = "";
		}else {
			if(folder.endsWith(File.separator)==false){
				folder = baseFolder+File.separator;
			}
		}
		//만들어질 파일 File
		File zipFile = new File(zipFileName);
        OutputStream zip_output = new FileOutputStream(zipFile);
        ArchiveOutputStream logical_zip = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, zip_output);
        
        for (File file : files){
        	logical_zip.putArchiveEntry(new ZipArchiveEntry(folder+file.getName()));
        	IOUtils.copy(new FileInputStream(file), logical_zip);
        	logical_zip.closeArchiveEntry();
        	
        }
        logical_zip.finish(); 
        zip_output.close();
		return zipFile;
	}
	public static  File zip(List<File> files, String zipFileName) throws FileNotFoundException, IOException, ArchiveException {
		return zip(files,zipFileName, null);
	}
	/**
	 * fileList의 파일들을 대상으로 압축하여 byte array로 리턴해 준다.
	 * 사용법: byte[] zipBytes = compress.zipInMemory(fileList,"대림산업");
	 *		FileOutputStream fos = new FileOutputStream("c:\\대림.zip");
	 *		fos.write(zipBytes);
	 *		fos.close();	 * 
	 * @param fileList
	 * @param baseFolder : zip안에서의 folder명
	 * @return zip된 bytearray
	 * @throws ArchiveException
	 * @throws IOException
	 */
	public byte[] zipInMemory(List<File> fileList, String baseFolder)
			throws ArchiveException, IOException {
		String folder = "";
		if (baseFolder == null || baseFolder.length() < 1) {
			folder = "";
		} else {
			if (folder.endsWith(File.separator) == false) {
				folder = baseFolder + File.separator;
			}else{
				folder = baseFolder;
			}
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ArchiveOutputStream zipOutputStream = new ArchiveStreamFactory()
				.createArchiveOutputStream(ArchiveStreamFactory.ZIP, bos);
		ZipArchiveEntry zipEntry = null;
		for (File file : fileList) {
			zipEntry = new ZipArchiveEntry(folder + file.getName());
			zipOutputStream.putArchiveEntry(zipEntry);
			zipOutputStream.write(toByteArray(file));
			zipOutputStream.closeArchiveEntry();
		}
		zipOutputStream.close();
		return bos.toByteArray();
	}
	/**
	 * List of Map으로 정보를 담아온다
	 * @param fileList
	 * @param baseFolder
	 * @return
	 * @throws ArchiveException
	 * @throws IOException
	 */
	public byte[] zipInMemory2(List<HashMap<String,Object>> fileList, String baseFolder)
			throws ArchiveException, IOException {
		String folder = "";
		if (baseFolder == null || baseFolder.length() < 1) {
			folder = "";
		} else {
			if (folder.endsWith(File.separator) == false) {
				folder = baseFolder + File.separator;
			}else{
				folder = baseFolder;
			}
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ArchiveOutputStream zipOutputStream = new ArchiveStreamFactory()
				.createArchiveOutputStream(ArchiveStreamFactory.ZIP, bos);
		ZipArchiveEntry zipEntry = null;
		for (HashMap<String,Object> map : fileList) {
			File file = (File)map.get("file");
			String orgFileName = (String)map.get("orgFileName");
			zipEntry = new ZipArchiveEntry(folder + orgFileName);
			zipOutputStream.putArchiveEntry(zipEntry);
			zipOutputStream.write(toByteArray(file));
			zipOutputStream.closeArchiveEntry();
		}
		zipOutputStream.close();
		return bos.toByteArray();
	}
	public static byte[] toByteArray(File file) throws IOException{
		InputStream input = new  FileInputStream(file);
		return IOUtils.toByteArray(input);
	}
	public static byte[] toByteArray(String fileName) throws IOException{
		InputStream input = new  FileInputStream(fileName);
		return IOUtils.toByteArray(input);
	}
	private String toPath(File root, File dir) {
		String path = dir.getAbsolutePath();
		path = path.substring(root.getAbsolutePath().length()).replace(
				File.separatorChar, '/');
		if (path.startsWith("/"))
			path = path.substring(1);
		if (dir.isDirectory() && !path.endsWith("/"))
			path += "/";
		return path;
	}

	private static void debug(String msg) {
		if (debug)
			System.out.println(msg);
	}


}
