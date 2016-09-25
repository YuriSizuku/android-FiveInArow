package cn.edu.bit.cs.myfileexplorer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.edu.bit.cs.myfileexplorer.R;
import cn.edu.bit.cs.myfileexplorer.model.DirectoryInfo;
import cn.edu.bit.cs.myfileexplorer.model.DisplayInfoBase;
import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;
import cn.edu.bit.cs.myfileexplorer.model.FileAndDirectorySummary;
import cn.edu.bit.cs.myfileexplorer.model.FileInfo;
import cn.edu.bit.cs.ui.HorizontalScrollList;
import cn.edu.bit.cs.ui.UIHelper;
import cn.edu.bit.cs.utils.FileUtils;
import cn.edu.bit.cs.utils.MIMEUtils;
import cn.edu.bit.cs.utils.StringUtils;

public class FileExplorerFragment
        extends Fragment
        implements
		SearchFolderAsyncTask.IExplorerUIComponent,
		HorizontalScrollList.ItemClickResponser {

	private ExplorerMode curMode = ExplorerMode.NORMAL;

	/**
	 * 当处于单选模式时，此字段保存当前选中的文件
	 */
	private String selectedPath = "";

	public String getSelectedPath() {
		return selectedPath;
	}

	private String initialPath = null;
	private String fileListBackgroundColorString="";

	private FileExplorerFunctionWrapper functionWrapper = null;

	private List<String> dirList = null;
	private ListView fileListView = null;
	private View listEmptyView = null;

	public ListView getFileListView() {
		return fileListView;
	}

	/**
	 * 用于引用“上一级文件夹”的根控件
	 */
	private View toUPView = null;

	private TextView infoTextView = null;

	private HorizontalScrollList dirScrollList = null;

	private FileListDataAdapter adapter = null;

	public FileListDataAdapter getAdapter() {
		return adapter;
	}

	private SearchFolderAsyncTask searchFolderAsyncTask = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		functionWrapper = new FileExplorerFunctionWrapper(this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflateMenuForExploreMode(menu, inflater);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * 依据当前模式实例化不同的菜单
	 * 
	 * @param menu
	 * @param inflater
	 */
	private void inflateMenuForExploreMode(Menu menu, MenuInflater inflater) {
		switch (curMode) {
		case NORMAL:
			inflater.inflate(R.menu.explorer_normal, menu);
			break;
		case CHOOSE_DIRECTORY_SINGLE:
			inflater.inflate(R.menu.explorer_single_dir_choice, menu);
			break;
		case CHOOSE_FILE_SINGLE:
			inflater.inflate(R.menu.explorer_single_file_choice, menu);
			break;

		default:
			break;
		}
	}

	/**
	 * // 将用户选择的文件(或文件夹）传回给调用者,并自动关闭Activity
	 */
	private void returnSelectedPathToInvoker() {
		int selectedPosition = fileListView.getCheckedItemPosition();
		DisplayInfoBase infoBase = (DisplayInfoBase) (fileListView
				.getItemAtPosition(selectedPosition));
		if (infoBase != null) {
			selectedPath = infoBase.getFullPath();
			Intent intent = new Intent();
			intent.putExtra(FileExplorerFragmentContants.SELECTED_PATH,
					selectedPath);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else {
			UIHelper.toastShowMessageShort(getActivity(), "请选择一个文件夹。");

		}

	}

	private LayoutInflater inflater = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_file_explorer_list,
				container, false);

		fileListBackgroundColorString=getArguments().getString(FileExplorerActivity.FILELIST_BACKGROUND);
		initFileListView(rootView);

		infoTextView = (TextView) rootView
				.findViewById(R.id.file_explorer_fragment_tvInfo);
		dirScrollList = (HorizontalScrollList) rootView
				.findViewById(R.id.file_explorer_fragment_dirScrollList);
		dirScrollList.setItemClickResponser(this);
		// 提取初始路径，在水平滑动列表上显示
		initialPath = getArguments().getString(
				FileExplorerFragmentContants.PATH_KEY);
		// 提取当前模式
		curMode = (ExplorerMode) (getArguments()
				.getSerializable(FileExplorerFragmentContants.EXPLORER_MODE_KEY));
		
		initForExploreMode();

		beginSearchDir(initialPath);
		return rootView;
	}

	/**
	 * 依据当前所处的模式，初始化系统
	 */
	private void initForExploreMode() {
		switch (curMode) {

		case CHOOSE_FILE_SINGLE:
			fileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			break;
		case NORMAL:
		case CHOOSE_DIRECTORY_SINGLE:
			setFileListViewLongClickListener();
			break;

		default:
			break;
		}
	}

	private void setFileListViewLongClickListener() {
		fileListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if (mActionMode != null) {
							return false;
						}
						fileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						mActionMode = getActivity().startActionMode(
								mActionModeCallback);

						fileListView.setItemChecked(position, true);

						return true;

					}
				});
	}

	/**
	 * 初始化ListView
	 * 
	 * @param rootView
	 */
	private void initFileListView(View rootView) {
		fileListView = (ListView) rootView
				.findViewById(R.id.file_explorer_fragment_lsvFiles);
		// 设置背景色
		if(StringUtils.isNotNullOrEmpty(fileListBackgroundColorString)){
			String[] colorValues = fileListBackgroundColorString.split(",");
			setfileListViewColor(
                    Integer.parseInt(colorValues[0]),
					Integer.parseInt(colorValues[1]),
					Integer.parseInt(colorValues[2]));
		}
		
		listEmptyView = rootView
				.findViewById(R.id.file_explorer_fragment_ListExmptyView);
		fileListView.setEmptyView(listEmptyView);
		// 将“到上层目录”项添加到ListView中。
		toUPView = inflater.inflate(R.layout.to_parent, null);
		toUPView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String parentPath = FileUtils.getParentPath(initialPath);
				if (parentPath != null) {
					initialPath = parentPath;
					beginSearchDir(initialPath);
				} else {
					UIHelper.toastShowMessageShort(getActivity(), "已经是最顶层文件夹");
				}

			}
		});
		fileListView.addHeaderView(toUPView);

		fileListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (adapter != null) {
							Object infoObject = adapter.getItem(position - 1);
							if (infoObject instanceof FileInfo) {
								// 非选中模式时，单击打开文件
								if (curMode == ExplorerMode.NORMAL
										&& mActionMode == null) {
									processFile((FileInfo) infoObject);
								}
							} else {
								// 非选中模式时，单击打开子文件夹
								if (mActionMode == null) {
									processDirectory((DirectoryInfo) infoObject);
								}

							}
						}

					}

				});
	}

	/**
	 * 在本Fragment中打开下级文件夹
	 *
	 */
	protected void processDirectory(DirectoryInfo infoObject) {
		if (infoObject == null) {
			return;
		}
		infoTextView.setText("正在打开指定的文件夹....");
		initialPath = infoObject.getFullPath();

		beginSearchDir(initialPath);
	}

	private void beginSearchDir(String path) {
		String[] dirs = path.split("/");
		dirList = new ArrayList<String>();
		// 移除所有空串
		for (int i = 0; i < dirs.length; i++) {
			if (StringUtils.isNotNullOrEmpty(dirs[i])) {
				dirList.add("/" + dirs[i]);
			}

		}
		dirs = dirList.toArray(new String[dirList.size()]);
		dirScrollList.loadData(dirs);
		// 启动异步搜索命令
		if (curMode == ExplorerMode.CHOOSE_DIRECTORY_SINGLE) {
			searchFolderAsyncTask = new SearchFolderAsyncTask(this, true);
		} else {
			searchFolderAsyncTask = new SearchFolderAsyncTask(this, false);
		}

		searchFolderAsyncTask.execute(initialPath);
	}

	/**
	 * 尝试打开文件
	 * 
	 * @param infoObject
	 */
	private void processFile(FileInfo infoObject) {
		if (infoObject == null) {
			return;
		}
		String fullPath = infoObject.getFullPath();
		File file = new File(fullPath);
		String MIME = null;
		String fileExt = FileUtils.getExtensionName(file.getName(), true);
		if (fileExt != null) {
			String tempString = MIMEUtils.getMIME(fileExt);
			if (tempString != null) {
				MIME = tempString;
			}

		}
		if (MIME == null) {
			UIHelper.toastShowMessageShort(getActivity(), "未知类型的文件，无法打开");
			return;
		}
		FileUtils.openFileByMIME(getActivity(), file, MIME);
	}

	static FileExplorerFragment newInstance(String initialPath,
			ExplorerMode explorerMode,String fileListBackgroundColor) {
		FileExplorerFragment frag = new FileExplorerFragment();
		Bundle args = new Bundle();
		args.putString(FileExplorerFragmentContants.PATH_KEY, initialPath);
		args.putSerializable(FileExplorerFragmentContants.EXPLORER_MODE_KEY,
				explorerMode);
		if(StringUtils.isNotNullOrEmpty(fileListBackgroundColor)){
			args.putString(FileExplorerActivity.FILELIST_BACKGROUND, fileListBackgroundColor);
		}
		
		frag.setArguments(args);
		return (frag);
	}

	@Override
	public void showFileAndDirectoryInfos(List<DirectoryInfo> dirs,
			List<FileInfo> files) {

		adapter = new FileListDataAdapter(files, dirs, curMode);
		fileListView.setAdapter(adapter);
		// 高亮显示最后一个文件夹
		final int selectedIndex = dirList.size() - 1;
		//为保证能正确滚动，使用延迟更新的方法
		dirScrollList.post(new Runnable() {

			@Override
			public void run() {
				dirScrollList.setSelection(selectedIndex);
				dirScrollList.scrollToShowItem(selectedIndex);

			}
		});

	}

	@Override
	public void showProgress(String processInfo) {

	}

	@Override
	public void setFileAndDirectorySummaryFields(FileAndDirectorySummary summary) {

		if (curMode != ExplorerMode.CHOOSE_DIRECTORY_SINGLE) {
			infoTextView.setText(summary.toString());
		} else {
			infoTextView.setText("包容子文件夹：" + summary.getDirCount() + "个");
		}

	}

	/**
	 * 点击水平文件夹栏时处理
	 * 
	 * @param item
	 */
	@Override
	public void onClick(View item) {
		int selectedIndex = dirScrollList.getSelectedIndex();
		if (selectedIndex == -1) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= selectedIndex; i++) {
			sb.append(dirList.get(i));
		}
		initialPath = sb.toString();
		// 如果当前正处于选择文件夹状态，则结束之
		if (mActionMode != null) {
			mActionMode.finish();
		}
		beginSearchDir(initialPath);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.fileExplorer_normal_Refresh) {
			beginSearchDir(initialPath);
		}
		if (id == R.id.fileExplorer_normal_newDir
				|| id == R.id.fileExplorer_singleDirChoice_newDir) {
			functionWrapper.createDirectory(initialPath);
		}
		if (id == R.id.fileExplorer_normal_Paste) {
			functionWrapper.pasteFile(initialPath);
		}
		if (id == R.id.fileExplorer_singleFileChoice_OK) {
			returnSelectedPathToInvoker();
		}
		if (id == R.id.fileExplorer_singleFileChoice_Cancel) {
			selectedPath = "";
			getActivity().finish();
		}
		return super.onOptionsItemSelected(item);
	}

	// -----------------------------
	// 长按文件夹表项，进入ActionBar上下文菜单状态
	// ----------------------------
	private ActionMode mActionMode = null;

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			fileListView.setItemChecked(fileListView.getCheckedItemPosition(),
					false);
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			inflateContextMenuForExploerMode(mode, menu);

			return true;
		}

		/**
		 * 依据当前模式实例化不同的上下文菜单
		 * 
		 * @param mode
		 * @param menu
		 */
		private void inflateContextMenuForExploerMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			if (curMode == ExplorerMode.NORMAL) {
				inflater.inflate(R.menu.explorer_normal_context, menu);
			}
			if (curMode == ExplorerMode.CHOOSE_DIRECTORY_SINGLE) {
				inflater.inflate(R.menu.explorer_single_dir_choice_context,
						menu);
			}
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int CheckedIndex = fileListView.getCheckedItemPosition();
			DisplayInfoBase info = (DisplayInfoBase) (fileListView
					.getItemAtPosition(CheckedIndex));
			int id = item.getItemId();
			if (id == R.id.fileExplorer_singleDirChoice_OK) {
				returnSelectedPathToInvoker();
			}
			if (id == R.id.fileExplorer_singleDirChoice_Cancel) {

				selectedPath = "";
				getActivity().finish();
			}
			if (id == R.id.fileExplorer_normal_Delete
					|| id == R.id.fileExplorer_singleDirChoice_Delete) {

				functionWrapper.deleteFileOrDir(info);
			}
			// --------------------------
			// 普通模式
			// --------------------------
			if (id == R.id.fileExplorer_normal_Copy) {
				if (info instanceof FileInfo) {
					mode.finish();
					functionWrapper.copyFile((FileInfo) info);
				} else {
					UIHelper.toastShowMessageShort(getActivity(),
                            "当前版本不支持文件夹的复制");
				}

			}
			if (id == R.id.fileExplorer_normal_Rename) {
				functionWrapper.renameFileOrDir(info, initialPath);
			}
			if (id == R.id.fileExplorer_normal_Cut) {
				if (info instanceof FileInfo) {
					mode.finish();
					functionWrapper.cutFile((FileInfo) info);
				} else {
					UIHelper.toastShowMessageShort(getActivity(),
                            "当前版本不支持移动文件夹");
				}
			}
			return true;
		}
	};

	// ------------------------------
	/**
	 * 加入一个新文件夹或文件数据项，并且自动刷新显示
	 *
	 */
	public void addNewFileOrDirItem(DisplayInfoBase newFileOrDir) {
		if (newFileOrDir != null) {
			adapter.addNewFileOrDirItem(newFileOrDir, true);
		}
	}

	/**
	 * 底层数据源中指定的文件夹名是否己经存在?
	 * 
	 * @param fileOrDir
	 * @return
	 */
	public boolean isFileOrDirExisted(String fileOrDir) {
		return adapter.isFileOrDirExisted(fileOrDir);
	}

	/**
	 * 删除指定的文件夹后，将相应数据对象从底层数据源中移除，并自动刷新显示
	 * 
	 * @param dirOrFileName
	 */
	public void deleteFileOrDirAndRefresh(String dirOrFileName) {
		boolean result = adapter.deleteDirOrFile(dirOrFileName, true);
		if (result) {
			int CheckedIndex = fileListView.getCheckedItemPosition();
			if (CheckedIndex != -1) {
				fileListView.setItemChecked(CheckedIndex, false);
			}
			UIHelper.toastShowMessageShort(getActivity(), dirOrFileName
                    + " 己删除");
		}

	}

	/**
	 * 刷新显示
	 */
	public void refreshFileListView() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	/**
	 * 设置文件列表的背景色
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void setfileListViewColor(int red,int green,int blue) {
		if(fileListView!=null){
			
			fileListView.setBackgroundColor(Color.argb(255, red, green, blue));
		}
		
	}

}
