package cn.edu.bit.cs.myfileexplorer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import cn.edu.bit.cs.myfileexplorer.R;
import cn.edu.bit.cs.myfileexplorer.model.DirectoryInfo;
import cn.edu.bit.cs.myfileexplorer.model.DisplayInfoBase;
import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;
import cn.edu.bit.cs.myfileexplorer.model.FileInfo;
import cn.edu.bit.cs.utils.StringUtils;

/**
 * 封装文件夹与文件数据对象集合 注意： 由于ListView中添加了一个或多个HeaderView，因此，外界检测到的列表项点击索引并不
 * 与底层数据对象的索引一致。 本类中的所有position参数，均直接针对真实数据集合而言的。向本类传入正确的索引，是外部调用者的责任。
 * 
 * @author jinxuliang
 * 
 */
public class FileListDataAdapter extends BaseAdapter {

	/**
	 * 两种不同的列表项类型
	 */
	private static final int FILE_ITEM = 0;
	private static final int DIRECTORY_ITEM = 1;

	/**
	 * 用于缓存文件表项内部的控件
	 * 
	 * @author jinxuliang
	 * 
	 */
	private static class FileViewHolder {
		public TextView nameTextView = null;
		public TextView descriptionTextView = null;
	}

	/**
	 * 用于缓存文件夹表项内部的控件
	 * 
	 * @author jinxuliang
	 * 
	 */
	private static class DirectoryViewHolder {
		public TextView nameTextView = null;
		public TextView descriptionTextView = null;
	}

	/**
	 * 保存所有的文件对象
	 */
	private List<FileInfo> files = null;
	/**
	 * 保存所有的文件夹对象
	 */
	private List<DirectoryInfo> dirs = null;
	/**
	 * 当前的显示模式
	 */
	private ExplorerMode curMode = null;

	public FileListDataAdapter(List<FileInfo> files, List<DirectoryInfo> dirs,
			ExplorerMode mode) {
		this.files = files;
		this.dirs = dirs;
		this.curMode = mode;
	}

	@Override
	public int getItemViewType(int position) {
		
		if (position >= dirs.size()) {
			return FILE_ITEM;
		}
		return DIRECTORY_ITEM;
	}

	@Override
	public int getViewTypeCount() {

		return 2;
	}

	@Override
	public int getCount() {
		int fileCount=(files==null)?0:files.size();
		int dirsCount=(dirs==null)?0:dirs.size();
		return dirsCount + fileCount;
	}

	/**
	 * 依据索引，返回文件或文件夹对象
	 * 
	 * @param position
	 * @return
	 */
	@Override
	public Object getItem(int position) {
		if (getItemViewType(position) == FILE_ITEM) {
			return files.get(position-dirs.size());
		}
		return dirs.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FileViewHolder fileViewHolder = null;
		DirectoryViewHolder directoryViewHolder = null;

		int currentViewType = getItemViewType(position);

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			if (currentViewType == FILE_ITEM) {
				convertView = inflater.inflate(R.layout.file_list_item, null);
				fileViewHolder = new FileViewHolder();
				fileViewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.fileExplorer_list_tvFileName);
				fileViewHolder.descriptionTextView = (TextView) convertView
						.findViewById(R.id.fileExplorer_list_tvFileDescription);
				convertView.setTag(fileViewHolder);
			} else {
				convertView = inflater.inflate(R.layout.directory_list_item,
						null);
				directoryViewHolder = new DirectoryViewHolder();
				directoryViewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.fileExplorer_list_tvFolderName);
				directoryViewHolder.descriptionTextView = (TextView) convertView
						.findViewById(R.id.fileExplorer_list_tvFolderDescription);
				convertView.setTag(directoryViewHolder);
			}

		}

		if (currentViewType == FILE_ITEM) {
			fillFileItem((FileInfo) getItem(position),
					(FileViewHolder) convertView.getTag());
		} else {
			fillDirectoryItem((DirectoryInfo) getItem(position),
					(DirectoryViewHolder) convertView.getTag());
		}
		return convertView;
	}

	private void fillDirectoryItem(DirectoryInfo info,
			DirectoryViewHolder directoryViewHolder) {
		if (info == null || directoryViewHolder == null) {
			return;
		}
		directoryViewHolder.nameTextView.setText(info.getName());
		directoryViewHolder.descriptionTextView.setText(info
				.getDescription(curMode));
	}

	private void fillFileItem(FileInfo info, FileViewHolder fileViewHolder) {
		if (info == null || fileViewHolder == null) {
			return;
		}

		fileViewHolder.nameTextView.setText(info.getName());

		fileViewHolder.descriptionTextView.setText(info.getDescription());
	}

	/**
	 * 将一个文件或文件夹数据对象加入到底层数据源中
	 * 第二个参数表明是否自动刷新，如果不自动刷新，则需要调用者手动调用notifyDataSetChanged()方法
	 * 
	 * @param directoryInfo
	 * @param autoRefresh
	 */
	public void addNewFileOrDirItem(DisplayInfoBase fileOrDir,
			boolean autoRefresh) {
		if (fileOrDir != null) {
			if (fileOrDir instanceof FileInfo) {
				files.add((FileInfo) fileOrDir);
			} else {
				dirs.add((DirectoryInfo) fileOrDir);
			}

			if (autoRefresh) {
				sortAndRefresh();
			}
		}

	}

	/**
	 * 删除文件夹或文件夹后，把对应的数据对象从底层数据源中移除
	 * 
	 * @param fileOrDir
	 * @param autoRefresh
	 */
	public boolean deleteDirOrFile(String fileOrDirName, boolean autoRefresh) {
		if (StringUtils.isNullOrEmpty(fileOrDirName)) {
			return false;
		}
		DisplayInfoBase info = new DisplayInfoBase();
		info.setName(fileOrDirName);
		int position = dirs.indexOf(info);
		if (position != -1) {
			dirs.remove((int) position);
			if (autoRefresh) {
				sortAndRefresh();
			}
			return true;
		} else {
			position -= dirs.size();
			position = files.indexOf(info);
			if (position != -1) {
				files.remove((int) position);
				if (autoRefresh) {
					sortAndRefresh();
				}
				return true;
			}

		}
		return false;
	}

	/**
	 * 自动排序并刷新
	 */
	public void sortAndRefresh() {
		if(dirs!=null){
			Collections.sort(dirs);
		}
		if(files!=null){
			Collections.sort(files);
		}
		
		notifyDataSetChanged();
	}

	/**
	 * 
	 * 判断指定文件或文件夹是否己经存在（同一文件夹下不允许出现相同名字的文件或文件夹，暂不支持文件名与子文件夹名一样的情况）
	 * 
	 * @param dirOrFileName
	 * @return
	 */
	public boolean isFileOrDirExisted(String dirOrFileName) {
		DisplayInfoBase info = new DisplayInfoBase();
		info.setName(dirOrFileName);
		
		if(dirs!=null && files!=null){
			return dirs.indexOf(info) != -1||files.indexOf(info)!=-1;
		}
		else
		{
			if(dirs!=null){
				return dirs.indexOf(info)!=-1;
			}
			if(files!=null){
				return files.indexOf(info)!=-1;
			}
		}
		return false;
		
	}

}
