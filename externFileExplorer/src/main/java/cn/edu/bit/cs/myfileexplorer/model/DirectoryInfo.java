package cn.edu.bit.cs.myfileexplorer.model;

import java.io.File;

import cn.edu.bit.cs.utils.StringUtils;

/**
 * 代表应用需处理的文件夹信息
 * @author JinXuLiang
 *
 */
public class DirectoryInfo extends DisplayInfoBase {
	/**
	 * 当前文件夹下包容多少个文件？
	 */
	private int fileCount=0;
	/**
	 * 当前文件夹下包容多少个子文件夹？
	 */
	private int childDirectoryCount=0;
	public DirectoryInfo(String directoryName,int fileCount,
			int childDirectoryCount,String directoryFullPath){
		name=directoryName;
		this.fileCount=fileCount;
		this.childDirectoryCount=childDirectoryCount;
		description=fileCount+"个文件,"+childDirectoryCount+"个子文件夹";
		fullPath=directoryFullPath;
	}

	/**
	 * 依据当前模式的不同，显示相应的信息
	 * @param mode
	 * @return
	 */
	public String getDescription(ExplorerMode mode){
		if(mode!=ExplorerMode.CHOOSE_DIRECTORY_SINGLE){
			return fileCount+"个文件,"+childDirectoryCount+"个子文件夹";
		}
	  return childDirectoryCount+"个子文件夹";
	}

    /**
     * 工厂方法，依据文件夹名字，统计相关信息，之后创建好DirectoryInfo对象返回给外界
     * @param directoryName
     * @return
     */
	public static DirectoryInfo newInstance(String directoryName) {
		if(StringUtils.isNotNullOrEmpty(directoryName)){
			File dir=new File(directoryName);
			if(!dir.isDirectory()){
				return null;
			}
			File[] files=dir.listFiles();
			if(files==null){
				return null;
			}
			int fileCount=0;
			int childDirectoryCount=0;
			for (File file : files) {
				if(file.isDirectory()){
					childDirectoryCount++;
				}
				else
				{
					fileCount++;
				}
			}
			return new DirectoryInfo(dir.getName(), fileCount, childDirectoryCount,dir.getAbsolutePath());
		}
		return null;
	}
}
