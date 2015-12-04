package cn.edu.bit.cs.myfileexplorer.model;

import cn.edu.bit.cs.utils.FileUtils;

/**
 * 用于封装文件与文件夹的一些统计信息
 * @author jinxuliang
 *
 */
public class FileAndDirectorySummary {
	/**
	 * 代表当前目录的完整路径
	 */
	private String fullPath="";
	
	
	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	/**
	 * 文件夹的总数量
	 */
	private int dirCount=0;
	/**
	 * 文件的数量
	 */
	private int fileCount=0;
	
	/**
	 * 文件的总容量
	 */
	private long totalFileLength=0;

	public int getDirCount() {
		return dirCount;
	}

	public void setDirCount(int dirCount) {
		this.dirCount = dirCount;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public long getTotalFileLength() {
		return totalFileLength;
	}

	public void setTotalFileLength(long totalFileLength) {
		this.totalFileLength = totalFileLength;
	}

	@Override
	public String toString() {
		return String.format("共有%1$d个子文件夹，%2$d个文件，%3$s",
                dirCount,fileCount,
                FileUtils.formetFileSize(totalFileLength));
	}
	
	

}
