package cn.edu.bit.cs.myfileexplorer.model;

import cn.edu.bit.cs.utils.FileUtils;

/**
 * 代表一个将用于显示的文件信息实体类
 * @author JinXuLiang
 *
 */
public class FileInfo extends DisplayInfoBase {

	private long length=0;

	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	public FileInfo(String fileName,long fileLength,String fileNameWithPath){
		super();
		name=fileName;
		length=fileLength;
		description= FileUtils.formetFileSize(fileLength);
		fullPath=fileNameWithPath;
	}
	

}
