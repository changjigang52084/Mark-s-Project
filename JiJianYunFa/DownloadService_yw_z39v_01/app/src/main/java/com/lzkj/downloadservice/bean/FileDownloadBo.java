package com.lzkj.downloadservice.bean;

import android.os.Parcel;
import android.os.Parcelable;


/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月11日 下午7:02:43 
 * @version 1.0 
 * @parameter 文件下载的bean
 */
public class FileDownloadBo implements Parcelable {
	public int currentThreadNum;
	public String httpUrl;
	public String filePath;
	public int endPosition;
	public int startPosition;
	public int fileSize;
	public int threadSize;
	public int downloadType;
	public FileDownloadBo(int currentThreadNum,String httpUrl,String filePath,int endPosition,int startPosition,int fileSize,int threadSize, int downloadType) {
		this.currentThreadNum = currentThreadNum;
		this.httpUrl = httpUrl;
		this.filePath    = filePath;
		this.endPosition = endPosition;
		this.startPosition   = startPosition;
		this.fileSize       = fileSize;
		this.threadSize		 = threadSize;
		this.downloadType = downloadType;
	}
	
	   @Override
	   public int describeContents() {
	        return 0;
	    }
	    
	    @Override
	    public void writeToParcel(Parcel out, int flags)
	    {
	    	out.writeInt(currentThreadNum);
	        out.writeString(httpUrl);
	        out.writeString(filePath);
	        out.writeInt(endPosition);
	        out.writeInt(startPosition);
	        out.writeInt(fileSize);
	        out.writeInt(threadSize);
	        out.writeInt(downloadType);
	    }
	    
	    public static final Creator<FileDownloadBo> CREATOR = new Creator<FileDownloadBo>()
	    {
	        @Override
	        public FileDownloadBo[] newArray(int size)
	        {
	            return new FileDownloadBo[size];
	        }
	        
	        @Override
	        public FileDownloadBo createFromParcel(Parcel in)
	        {
	            return new FileDownloadBo(in);
	        }
	    };
	    
	    public FileDownloadBo(Parcel in)
	    {
	    	currentThreadNum = in.readInt();
	    	httpUrl = in.readString();
	    	filePath = in.readString();
	    	endPosition = in.readInt();
	    	startPosition = in.readInt();
	    	fileSize = in.readInt();
	    	threadSize = in.readInt();
	    	downloadType = in.readInt();
	    }
	/**
	 * 获取当前下载的线程index
	 * @return
	 */
	public int getCurrentThreadNum() {
		return currentThreadNum;
	}
	public void setCurrentThreadNum(int currentThreadNum) {
		this.currentThreadNum = currentThreadNum;
	}
	/**
	 * 获取下载地址
	 * @return
	 */
	public String getHttpUrl() {
		return httpUrl;
	}
	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}
	/**
	 * 获取文件的地址
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * 文件下载的结束位置
	 * @return
	 */
	public int getEndPosition() {
		return endPosition;
	}
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
	/**
	 * 文件下载的开始位置
	 * @return
	 */
	public int getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	/**
	 * 获取文件的大小
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * 获取总线程数
	 * @return
	 */
	public int getThreadSize() {
		return threadSize;
	}
	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
	}
	/**
	 * 获取下载类型
	 * @return
	 */
	public int getDownloadType() {
		return downloadType;
	}
	public void setDownloadType(int downloadType) {
		this.downloadType = downloadType;
	}
}
