package com.lzkj.aidlservice.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月24日 上午10:44:27 
 * @version 1.0 
 * @parameter zip工具类
 */
public class ZipUtile {
	private static volatile ZipUtile zipUtile;
	public static ZipUtile getInstance() {
		if (null == zipUtile) {
			synchronized (ZipUtile.class) {
				if (null == zipUtile) {
					zipUtile = new ZipUtile();
				}
			}
		}
		return zipUtile;
	}
	
	/** 
     * 创建ZIP文件 
     * @param sourcePath 文件或文件夹路径 
     * @param zipPath 生成的zip文件存在路径（包括文件名） 
	 * @throws IOException 
     */  
    public void createZip(String sourcePath, String zipPath) throws IOException {  
        FileOutputStream fos = null;  
        ZipOutputStream zos = null;  
        try {  
            fos = new FileOutputStream(zipPath);  
            zos = new ZipOutputStream(fos);  
            writeZip(new File(sourcePath), "", zos);  
        } catch (FileNotFoundException e) {  
        	new File(zipPath).delete();
        	e.printStackTrace();
        } finally {  
            if (zos != null) {  
                zos.close();  
            } 
        }  
    }  
      
    private void writeZip(File file, String parentPath, ZipOutputStream zos) {  
        if (file.exists()) {  
            //处理文件夹  
            if (file.isDirectory()) {  
                parentPath += file.getName() + File.separator;  
                File [] files = file.listFiles();  
                for(File f : files){  
                    writeZip(f, parentPath, zos);  
                }  
            } else {  
                FileInputStream fis=null;  
                DataInputStream dis=null;  
                try {  
                    fis = new FileInputStream(file);  
                    dis = new DataInputStream(new BufferedInputStream(fis));  
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());  
                    zos.putNextEntry(ze);  
                    //添加编码，如果不添加，当文件以中文命名的情况下，会出现乱码  
                    // ZipOutputStream的包一定是apache的ant.jar包。JDK也提供了打压缩包，但是不能设置编码  
                    zos.setEncoding("GBK");  
                    byte [] content=new byte[1024 * 1024];  
                    int len;  
                    while ((len = fis.read(content)) != -1) {  
                        zos.write(content, 0, len);  
                    }  
                    zos.flush();  
                } catch (IOException e) {  
                	e.printStackTrace();
                } finally {  
                    try {  
                        if (dis!=null) {  
                            dis.close();  
                        }  
                    } catch (IOException e){  
                    	e.printStackTrace();
                    }  
                }  
            }  
        }  
    }   
	
	/**
	 * 压缩文件夹到指定的路径
	 * @param zipFile    指定到压缩文件路径
	 * @param srcZipFolder 需要被压缩的文件夹路径
	 * @return 返回压缩以后的路径,如果zipFile的值为null,则默认在srcZipFolder目录下面
	 */
	private String zipFolder(File zipFile, File srcZipFolder) {
		if (null == srcZipFolder || !srcZipFolder.exists()) {
			return null;
		}
		try {
			File[] srcZipFileArray = null;
			if (null == zipFile) {
				String fileName = srcZipFolder.getName() + ".zip";
				if (srcZipFolder.isDirectory()) {
					srcZipFileArray = srcZipFolder.listFiles();
					zipFile = new File(srcZipFolder, fileName);
				} else if (srcZipFolder.isFile()){
					srcZipFileArray = new File[]{srcZipFolder};
					zipFile = new File(srcZipFolder.getParentFile(), fileName);
				}
			}
			
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			
			int size = srcZipFileArray.length;
			for (int i = 0; i < size; i++) {
				File srcFile = srcZipFileArray[i];
				
				zipOutputStream.putNextEntry(new ZipEntry(srcFile.getName()));
				
				FileInputStream fileInputStream = new FileInputStream(srcFile);
				int length = -1;
				byte[] buffer = new byte[1024 * 1024];
				while ((length = fileInputStream.read(buffer)) != -1) {
					zipOutputStream.write(buffer, 0, length);
				}
				
				zipOutputStream.flush();
				
				fileInputStream.close();
			}
			zipOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zipFile.getAbsolutePath();
	}
}
