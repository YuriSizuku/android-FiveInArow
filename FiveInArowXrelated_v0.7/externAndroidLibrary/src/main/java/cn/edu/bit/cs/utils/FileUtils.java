package cn.edu.bit.cs.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.bit.cs.ui.UIHelper;

/**
 * 封装与文件操作相关的一些功能
 * 
 * @author JinXuLiang
 * 
 */
public class FileUtils {
	/**
	 * 使用nio中的Channel复制文件 成功返回文件的大小，不成功，返回-1
	 * 
	 * @throws java.io.IOException
	 */
	public static long copyFile(String sourceFilePath, String targetFilePath) {

		if (isFileOrDirectoryExists(sourceFilePath) == false)
			return -1;
		int length = 2097152; // 2M
		FileInputStream fi = null;
		FileChannel inC = null;
		FileOutputStream fo = null;
		FileChannel outC = null;
		long fileLength = 0;
		try {
			fi = new FileInputStream(sourceFilePath);

			inC = fi.getChannel();
			fo = new FileOutputStream(targetFilePath);
			outC = fo.getChannel();
			fileLength = inC.size();
			while (inC.position() < fileLength) {
				if ((inC.size() - inC.position()) < 20971520)
					length = (int) (inC.size() - inC.position());
				else
					length = 20971520;
				inC.transferTo(inC.position(), length, outC);
				inC.position(inC.position() + length);
			}
			inC.close();
			outC.close();
			fi.close();
			fo.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}


		return fileLength;

	}

	/**
	 * 创建文件夹 成功创建，返回true，如果不成功或文件夹己存在，返回false 如果文件夹不存在，则自动创建之
	 *
	 * @param path
	 */
	public static boolean mkdirs(String path) {
		if (StringUtils.isNullOrEmpty(path))
			return false;
		File dir = new File(path);
		if (dir.exists()) {
			return true;
		} else {
			return dir.mkdirs();
		}
	}

	/**
	 * 判断指定的文件或文件夹是否存在
	 *
	 * @param fileOrDirectoryNameWithPath
	 * @return
	 */
	public static boolean isFileOrDirectoryExists(
			String fileOrDirectoryNameWithPath) {
		if (StringUtils.isNullOrEmpty(fileOrDirectoryNameWithPath))
			return false;
		File file = new File(fileOrDirectoryNameWithPath);
		return file.exists();
	}

	/**
	 * 判断指定文件夹下是否有名为FileNameWithoutExt的文件，注意：文件名(不包含路径和扩展名） 如果有，返回其完整的文件名（包括扩展名）
	 * 如果没有，返回null
	 */
	public static String searchFileInDirectory_IgnoreExt(
			String fileNameWithoutExtAndPath, String searchPath) {
		if (StringUtils.isNullOrEmpty(fileNameWithoutExtAndPath)
				|| StringUtils.isNullOrEmpty(searchPath)) {
			return null;
		}
		File dir = new File(searchPath);
		if (dir.isDirectory()) {
			String[] fileNames = dir.list();
			for (String filename : fileNames) {
				// 要查找的文件名加上“.”,在真实文件名字串中的索引应该是0
				if (filename.indexOf(fileNameWithoutExtAndPath + ".") == 0) {
					return filename;
				}
			}
		}
		return null;
	}

	/**
	 * 将一个文件的所有内容读出，放到字节数组中 如果参数为空，返回null
	 *
	 * @param fileName
	 * @return
	 * @throws java.io.IOException
	 */
	public static byte[] readFileContent(String fileName) throws IOException {
		if (StringUtils.isNullOrEmpty(fileName)) {
			return null;
		}
		FileInputStream fs = new FileInputStream(fileName);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = fs.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}
		byteArrayOutputStream.close();
		fs.close();
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * 创建（或覆盖）文件，将二进制数据写入到此文件中
	 *
	 * @param data
	 * @param fileName
	 * @throws java.io.IOException
	 */
	public static void saveDataToFile(byte[] data, String fileName)
			throws IOException {
		if (data == null || data.length == 0) {
			return;
		}
		FileOutputStream fs = new FileOutputStream(fileName);
		fs.write(data);
		fs.close();
	}

	/**
	 * 创建或覆盖一个txt文件，并向其中写入一个字符串
	 *
	 * @param txtFileName
	 * @param fileContent
	 * @throws java.io.IOException
	 */
	public static void saveToTxtFile(String txtFileName, String fileContent)
			throws IOException {
		File file = new File(txtFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(fileContent);
		bw.close();
	}

	/**
	 * 按行读入文本文件，各行之间以“\r\n”分隔，但最后一行不包容“\r\n”，
	 *
	 * @param txtFileName
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readContentOf(String txtFileName) throws IOException {
		File file = new File(txtFileName);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		int lineIndex = 0;
		while ((line = br.readLine()) != null) {
			if (lineIndex != 0)
				sb.append("\r\n");
			sb.append(line);
			lineIndex++;
		}
		br.close();
		return sb.toString();
	}

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 *
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean deleteFileOrFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 *
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	private static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除指定文件夹下的所有文件，不包括子文件夹
	 * @param dir
	 */
	public static void deleteAllFilesOfDir(String dir) {
		if(isFileOrDirectoryExists(dir)){
			File dirFile = new File(dir);
			// 删除文件夹下的所有文件(包括子目录)
			File[] files = dirFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				// 删除子文件
				if (files[i].isFile()) {
					deleteFile(files[i].getAbsolutePath());
				}
			}
		}
	}
	/**
	 * 删除目录（文件夹）以及目录下的文件
	 *
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	private static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将一个文件或文件夹改名 成功返回true 注意：仅适用于在同一卷内修改文件名
	 *
	 * @param oldFileNameWithPath
	 * @param newFileNameWithPath
	 * @return
	 */
	public static boolean renameFileOrDirectory(String oldFileNameWithPath,
			String newFileNameWithPath) {
		File oldFile = new File(oldFileNameWithPath);
		File newFile = new File(newFileNameWithPath);
		return oldFile.renameTo(newFile);
	}

	/**
	 * 获取指定文件的大小 是文件夹，或文件不存在，均返回-1
	 *
	 * @param fileNameWithPath
	 * @return
	 * @throws Exception
	 */
	public static long getFileLength(String fileNameWithPath) {
		File file = new File(fileNameWithPath);
		if (file.isDirectory()) {
			return -1;
		}
		if (file.exists()) {
			return file.length();
		}
		return -1;

	}

	/**
	 * 获取指定文件夹的大小，如果指定的路径名不是文件夹，返回-1
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static long getDirectorySize(String path) throws Exception {
		File dir = new File(path);
		if (StringUtils.isNullOrEmpty(path) || dir.exists() == false
				|| dir.isDirectory() == false) {
			return -1;
		}
		long size = 0;
		File flist[] = dir.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getDirectorySize(flist[i].getAbsolutePath());
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/*** 转换文件大小单位(b/kb/mb/gb) ***/
	public static String formetFileSize(long size) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (size < 1024) {
			fileSizeString = size + "";
		} else if (size < 1048576) {
			fileSizeString = df.format((double) size / 1024) + "K";
		} else if (size < 1073741824) {
			fileSizeString = df.format((double) size / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) size / 1073741824) + "G";
		}
		return fileSizeString;
	}





	/**
	 * Java文件操作：获取文件扩展名 第二个参数确定是否包容“.”号 能成功地提取扩展名，返回之，所有其他情况，均返回null
	 * 支持包容路径的文件名或单独的文件名，其算法是从尾部开始查找“.”然后从此处开始截取最后子串
	 * @param filename
	 * @param includeDot
	 * @return
	 */
	public static String getExtensionName(String filename, boolean includeDot) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				if (includeDot) {
					return filename.substring(dot);
				} else {
					return filename.substring(dot + 1);
				}
			}

		}
		return null;
	}

	/**
	 * 从路径字符串中提取文件名,如果不成功，返回一个空串
	 *
	 * @param fileNameWithPath
	 * @return
	 */
	public static String getFileNameFromPathString(String fileNameWithPath) {
		if (StringUtils.isNullOrEmpty(fileNameWithPath)) {
			return "";
		}
		try {
			File file = new File(fileNameWithPath);
			return file.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 提取指定文件夹的上层文件夹 如果己经是最顶层文件夹了，则返回null
	 *
	 * @param curPath
	 * @return
	 */
	public static String getParentPath(String curPath) {
		//if(curPath.equals("/")) return curPath;
		if (StringUtils.isNullOrEmpty(curPath)) {
			return null;
		}
		String[] pathSegments = curPath.split("/");
		List<String> segments = new ArrayList<String>();
		for (String segment : pathSegments) {
			if (StringUtils.isNotNullOrEmpty(segment)) {
				segments.add(segment);
			}
		}
		int segmentCount = segments.size();
		if (segmentCount == 1) {//不能到根目录，暂时不管了
			return null;
		}
		StringBuilder sbBuilder = new StringBuilder();
		for (int i = 0; i < segmentCount - 1; i++) {
			sbBuilder.append(segments.get(i));
			sbBuilder.append("/");
		}
		return sbBuilder.toString();
	}

	/**
	 * 使用本机安装的应用依据MIME尝试打开特定的文件
	 * 无法打开或出错时，以toast通知用户
	 * @param file
	 * @param MIME
	 */
	public static void openFileByMIME(Context context,File file, String MIME) {
		if(file.exists()==false){
			return;
		}
		Intent intent = new Intent();

		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), MIME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			context.startActivity(Intent.createChooser(intent,"选择一个程序"));
		} catch (ActivityNotFoundException e) {
			UIHelper.toastShowMessageShort(context, "本台设备无法打开这种类型的文件:"
                    + MIME);
		} catch (Exception e) {
			UIHelper.toastShowMessageShort(context, e.getMessage());
		}
	}

}
