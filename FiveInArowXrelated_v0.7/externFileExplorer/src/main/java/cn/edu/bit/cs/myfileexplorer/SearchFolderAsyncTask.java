package cn.edu.bit.cs.myfileexplorer;

import android.os.AsyncTask;

import java.util.Collections;
import java.util.List;

import cn.edu.bit.cs.myfileexplorer.model.DirectoryInfo;
import cn.edu.bit.cs.myfileexplorer.model.DisplayInfoBase;
import cn.edu.bit.cs.myfileexplorer.model.FileAndDirectorySummary;
import cn.edu.bit.cs.myfileexplorer.model.FileInfo;

public class SearchFolderAsyncTask extends AsyncTask<String, String, Void> {
	
	public interface IExplorerUIComponent{
		/**
		 * 显示文件及文件夹信息
		 */
		void showFileAndDirectoryInfos(List<DirectoryInfo> dirs, List<FileInfo> files);
		/**
		 * 显示进度信息
		 * @param processInfo
		 */
		void showProgress(String processInfo);
		/**
		 * 将一些统计信息发给UI控件
		 * @param summary
		 */
		void setFileAndDirectorySummaryFields(FileAndDirectorySummary summary);
		
	}

	/**
	 * 用于保存文件与文件夹信息
	 */
	private List<FileInfo> files = null;
	private List<DirectoryInfo> dirs=null;
	/**
	 * 是否只显示文件夹？
	 */
	private boolean showOnlyFolder=false;
	/**
	 * 用于显示文件夹与文件信息的UI控件（比如Activity或Fragment）
	 */
	private IExplorerUIComponent uiComponent=null;
	
	public SearchFolderAsyncTask(IExplorerUIComponent uiComponent,boolean onlyFolder) {
		this.uiComponent=uiComponent;
		showOnlyFolder=onlyFolder;
	}
	
	private FileAndDirectorySummary summary=new FileAndDirectorySummary();
	@Override
	protected Void doInBackground(String... params) {
		String initialPath=params[0];
		
		dirs = FileManager.getDirectoryInfos(initialPath);
		summary.setFullPath(initialPath);
	
		if(!showOnlyFolder){
			files=FileManager.getFileInfos(initialPath);
		}
		
		if (dirs != null) {
			summary.setDirCount(dirs.size());
		}
		
		if (files != null) {
			summary.setFileCount(files.size());
			long totalLength=0;
			for (DisplayInfoBase fileInfo : files) {
				totalLength+=((FileInfo)fileInfo).getLength();
			}
			summary.setTotalFileLength(totalLength);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if(dirs!=null){
			Collections.sort(dirs);
		}
		if(files!=null){
			Collections.sort(files);
		}
		
		if(uiComponent!=null){
			uiComponent.showFileAndDirectoryInfos(dirs,files);
			uiComponent.setFileAndDirectorySummaryFields(summary);
		}
		
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(String... values) {
	
		if(uiComponent!=null){
			uiComponent.showProgress(values[0]);
		}
		super.onProgressUpdate(values);
	}

	
	
}
