package cn.edu.bit.cs.myfileexplorer;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.edu.bit.cs.myfileexplorer.model.DirectoryInfo;
import cn.edu.bit.cs.myfileexplorer.model.FileInfo;
import cn.edu.bit.cs.utils.FileUtils;
import cn.edu.bit.cs.utils.StringUtils;

/**
 * 完成选择文件夹，查看并打开文件，修改文件（夹）名，删除文件（夹）的功能
 * @author JinXuLiang
 *
 */
public class FileManager {
	
	/**
	 * 提取指定文件下的所有文件信息
	 * @param folderName
	 * @return
	 */
	public static List<FileInfo> getFileInfos(String folderName) {
		//如果参数为空或指定的文件夹不存在
		if(StringUtils.isNullOrEmpty(folderName)||
				FileUtils.isFileOrDirectoryExists(folderName)==false){
			return null;
		}
		File folder=new File(folderName);
		if(folder.isDirectory()){
			List<FileInfo> fileInfos=new ArrayList<FileInfo>();
			File[] files=folder.listFiles();
			if(files==null){
				return null;
			}
			for (File file:files ) {
				if(!file.isDirectory())
				fileInfos.add(new FileInfo(file.getName(), file.length(),file.getAbsolutePath()));
			}
			return fileInfos;
		}
		return null;
	}
	/**
	 * 提取指定路径下的所有子文件夹信息
	 * 无法提取时，返回null
	 * @param rootPath
	 * @return
	 */
	public static List<DirectoryInfo> getDirectoryInfos(String rootPath) {
		//如果参数为空或指定的文件夹不存在
				if(StringUtils.isNullOrEmpty(rootPath)||
						FileUtils.isFileOrDirectoryExists(rootPath)==false){
					return null;
				}
				File root=new File(rootPath);
				if(root.isDirectory()){
					List<DirectoryInfo> dirInfos=new ArrayList<DirectoryInfo>();
					File[] files=root.listFiles();
					if(files==null){
						return null;
					}
					for (File file:files ) {
						if(file.isDirectory()){
							DirectoryInfo directoryInfo=DirectoryInfo.newInstance(file.getPath());
							if(directoryInfo!=null){
								dirInfos.add(directoryInfo);
							}
						}
					}
					return dirInfos;
					
				}
				return null;
	}
}
