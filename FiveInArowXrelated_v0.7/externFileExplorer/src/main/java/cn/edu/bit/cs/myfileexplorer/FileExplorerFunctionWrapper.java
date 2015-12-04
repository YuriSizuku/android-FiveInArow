
package cn.edu.bit.cs.myfileexplorer;

import android.app.ProgressDialog;

import cn.edu.bit.cs.myfileexplorer.model.DirectoryInfo;
import cn.edu.bit.cs.myfileexplorer.model.DisplayInfoBase;
import cn.edu.bit.cs.myfileexplorer.model.FileInfo;
import cn.edu.bit.cs.ui.MessageBox;
import cn.edu.bit.cs.ui.UIHelper;
import cn.edu.bit.cs.utils.FileUtils;
import cn.edu.bit.cs.utils.StringUtils;

/**
 * 封装一些文件管理器用到的功能
 * 
 * @author JinXuLiang
 * 
 */
public class FileExplorerFunctionWrapper {

	private FileExplorerFragment fragment = null;

	public FileExplorerFunctionWrapper(FileExplorerFragment fragment) {
		this.fragment = fragment;
	}

	private FileInfo fileToCopyOrCut=null;
	/**
	 * 复制文件
	 * @param file
	 */
	public void copyFile(FileInfo file) {
		if(file!=null){
			fileToCopyOrCut=file;
			isCut=false;
			UIHelper.toastShowMessageShort(fragment.getActivity(), "文件己准备好复制");
		}
	}
	
	public void cutFile(FileInfo fileInfo) {
		if(fileInfo!=null){
			fileToCopyOrCut=fileInfo;
			UIHelper.toastShowMessageShort(fragment.getActivity(), "文件己准备好移动");
			isCut=true;
		}
		
	}
	private ProgressDialog progressDialog=null;
	private boolean isCut=false;
	/**
	 * 粘贴文件
	 * @param destPath
	 * @param isMove
	 */
	public void pasteFile(String destPath){
		if(fileToCopyOrCut==null || StringUtils.isNullOrEmpty(destPath) ||
				FileUtils.isFileOrDirectoryExists(destPath)==false){
			return;
		}
		if(fragment.isFileOrDirExisted(fileToCopyOrCut.getName())){
			(new MessageBox(fragment.getActivity())).showSimpleMesage(fileToCopyOrCut.getName()+"已存在，请先删除它", "发现同名的文件或文件夹");
			return;
		}
		final String newFullPath=destPath.endsWith("/")?destPath+fileToCopyOrCut.getName():destPath+"/"+fileToCopyOrCut.getName();
		progressDialog=ProgressDialog.show(fragment.getActivity(), "请稍候……", "正在复制文件");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
					long copiedSize= FileUtils.copyFile(fileToCopyOrCut.getFullPath(),
                            newFullPath
                    );
					if(copiedSize==-1){
						UIHelper.toastShowMessageShort(fragment.getActivity(), "无法复制文件");
						return;
					}
					
				if(isCut){
					if(FileUtils.deleteFileOrFolder(fileToCopyOrCut.getFullPath())==false){
						UIHelper.toastShowMessageShort(fragment.getActivity(), "文件复制完成，但无法删除原始文件");
					}
					
				}
				fileToCopyOrCut.setFullPath(newFullPath);
				progressDialog.dismiss();
				fragment.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						fragment.addNewFileOrDirItem(fileToCopyOrCut);
						//还原初始状态
						isCut=false;
						fileToCopyOrCut=null;
					}
				});
			}
		}).start();
		
		
	}
	
	/**
	 * 打开一个输入框，要求用户输入新文件夹名，之后，创建此文件夹
	 * 
	 * @param curDirectory
	 * @return
	 */
 	public void createDirectory(final String curDirectory) {

		if (StringUtils.isNullOrEmpty(curDirectory)
				|| FileUtils.isFileOrDirectoryExists(curDirectory) == false) {
			return;
		}

		MessageBox messageBox = new MessageBox(fragment.getActivity());
		messageBox.showInputDialog("新建文件夹", "请输入新文件夹的名字（不能以数字打头）","",
				new MessageBox.ISimpleInputDialogButtonClick() {

					@Override
					public void doSomething(String userInput) {
						userInput = StringUtils.clearAllSpace(userInput);
						if (StringUtils.isNullOrEmpty(userInput) == false) {

							if (fragment.isFileOrDirExisted(userInput)) {
								new MessageBox(fragment.getActivity())
										.showSimpleMesage("此文件夹己存在", "提示信息");
								return;
							}
							String createdDirectoryFullPath = curDirectory
									.endsWith("/") ? curDirectory + userInput
									: curDirectory + "/" + userInput;
							try {
								if (FileUtils.mkdirs(createdDirectoryFullPath)) {
									DirectoryInfo createDirectoryInfoResult = new DirectoryInfo(
											userInput, 0, 0,
											createdDirectoryFullPath);
									fragment.addNewFileOrDirItem(createDirectoryInfoResult);
								}
								else {
									UIHelper.toastShowMessageShort(
                                            fragment.getActivity(), "无法在此位置创建文件夹");
								}
							} catch (Exception e) {
								UIHelper.toastShowMessageShort(
                                        fragment.getActivity(), e.getMessage());
							}
						}
					}
				}, null);

	}

	/**
	 * 给文件夹或文件改名
	 * 
	 * @param fileOrDir
	 * @param curDirectory
	 */
	public void renameFileOrDir(final DisplayInfoBase fileOrDir,
			final String curDirectory) {

		if (fileOrDir == null) {
			return;
		}

		MessageBox messageBox = new MessageBox(fragment.getActivity());
		messageBox.showInputDialog("改名", "请输入新名字（不能以数字打头）",fileOrDir.getName(),
				new MessageBox.ISimpleInputDialogButtonClick() {

					@Override
					public void doSomething(String userInput) {
						userInput = StringUtils.clearAllSpace(userInput);
						if (StringUtils.isNullOrEmpty(userInput) == false) {
							// 名字没改，不做任何处理
							if (userInput.equals(fileOrDir.getName())) {

								return;
							}
							String newName = "";
							if (fileOrDir instanceof FileInfo) {
								String fileExt = FileUtils.getExtensionName(
                                        fileOrDir.getFullPath(), true);
								if (fileExt != null) {
									newName = curDirectory.endsWith("/") ? curDirectory
											+ userInput
											: curDirectory + "/" + userInput
													+ fileExt;
									userInput+=fileExt;
								}
							} else {
								newName = curDirectory.endsWith("/") ? curDirectory
										+ userInput
										: curDirectory + "/" + userInput;
							}

							try {
								if (FileUtils.renameFileOrDirectory(
                                        fileOrDir.getFullPath(), newName)) {
									fileOrDir.setName(userInput);
									fileOrDir.setFullPath(newName);
									fragment.refreshFileListView();
								}
								else {
									UIHelper.toastShowMessageShort(fragment.getActivity(), "无法改名");
								}
							} catch (Exception e) {
								UIHelper.toastShowMessageShort(
                                        fragment.getActivity(), e.getMessage());
							}
						}
					}
				}, null);

	}

	/**
	 * 删除指定的文件夹或文件
	 * 
	 * @param dirOrFile
	 */
	public void deleteFileOrDir(final DisplayInfoBase dirOrFile) {
		if (dirOrFile == null) {
			return;
		}
		MessageBox messageBox = new MessageBox(fragment.getActivity());
		messageBox.showOKOrCancelDialog("真的删除：" + dirOrFile.getName() + "?",
				"删除文件夹", new MessageBox.IButtonClick() {

					@Override
					public void doSomething() {
						if(FileUtils.deleteFileOrFolder(dirOrFile.getFullPath())){
							fragment.deleteFileOrDirAndRefresh(dirOrFile.getName());
						}
						else {
						     UIHelper.toastShowMessageShort(fragment.getActivity(), "无法删除此文件夹");
						}
						
					}
				}, null);

	}
}
