package cn.edu.bit.cs.utils;

import java.io.UnsupportedEncodingException;

/**
 * 封装一些编码中常用的功能
 * @author JinXuLiang
 *
 */
public class StringUtils {

	/**
	 * 当字符串为null或空串时，返回true
	 * @param string
	 * @return
	 */
	public static boolean isNullOrEmpty(String string) {
		if(string==null || string.length()==0){
			return true;
		}
		return false;
	}
	
	public static boolean isNotNullOrEmpty(String string) {
		if(string==null || string.length()==0){
			return false;
		}
		return true;
	}
	/**
	 * 删除字符串中的所有空格
	 * @param string
	 * @return
	 */
	public static String clearAllSpace(String string) {
		if(isNullOrEmpty(string)){
			return "";
		}
		return string.replaceAll(" ", "");
		
	}
	/**
	 * 一个字符是否是中文字符
	 * @param c
	 * @return
	 */
	public static boolean isChineseChar(char c) {

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

            return true;

        }

        return false;

    }

	/*
	 * 一个字串中是否包容中文字符？
	 */
	 public static boolean isChineseCharInString(String string) {

	        char[] ch = string.toCharArray();

	        for (int i = 0; i < ch.length; i++) {
	            char c = ch[i];
	            if (isChineseChar(c) == true) {
	                      return true;
	            }
	        }
	        return false;
	    }

	 /**
	  * 将一个字符串按照指定的编码转换为字节数组，主要有以下几种：
	  * ISO-8859-1 US-ASCII UTF-16 UTF-16BE  UTF-16LE UTF-8 
	  * 如果原始字串为空，返回null
	  * 如果encode参数为空，则使用UTF-8编码
	  * @param originalString
	  * @param encode
	  * @return
	  * @throws java.io.UnsupportedEncodingException
	  */
	 public static byte[] stringToByte(String originalString,String encode) throws UnsupportedEncodingException{
		 if(isNullOrEmpty(originalString))
			 return null;
		 if(isNullOrEmpty(encode)){
			 encode="UTF-8";
		 }
		 return originalString.getBytes(encode);
	 }
	 
	 /**
		 * 将十六进制数字字串转换为byte数组
		 * 
		 * @param hexString
		 * @return
		 */
		public static byte[] HexStringtoByteArray(String hexString) {
			int len = hexString.length() / 2;
			byte[] result = new byte[len];
			for (int i = 0; i < len; i++)
				result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
						16).byteValue();

			return result;
		}

		/**
		 * 将byte数据转换为十六进制数字字串
		 * 
		 * @param databuffer
		 *            要被转换的字节数组
		 * @return
		 */
		public static String byteArraytoHexString(byte[] databuffer) {
			if (databuffer == null)
				throw new NullPointerException();
			StringBuffer result = new StringBuffer(2 * databuffer.length);
			for (int i = 0; i < databuffer.length; i++) {
				appendHex(result, databuffer[i]);
			}
			return result.toString();
		}

		/**
		 * 将一个字节转换为十六进制表示，并追加到StringBuffer尾部
		 */
		private static void appendHex(StringBuffer sb, byte byteValue) {
			String HEX = "0123456789ABCDEF";
			sb.append(HEX.charAt((byteValue >> 4) & 0x0f)).append(
					HEX.charAt(byteValue & 0x0f));
		}
		
		/**
		 * 限制字符串为指定的长度，超过长度时，截去最后三个字符，换为三个英文点号
		 * @param str
		 * @param maxStringLength
		 * @return
		 */
		public static String getLimitedLengthString(String str,int maxStringLength) {
			
			if(StringUtils.isNullOrEmpty(str) ||  maxStringLength>str.length()){
				return str;
			}
			StringBuffer sb=new StringBuffer();
			sb.append(str.substring(0, maxStringLength-3));
			sb.append("...");
			return sb.toString();
		}

		/**
		 * 判断一个字符串是否是整数
		 * @param numString
		 * @return
		 */
		public static boolean isInteger(String numString) {
			if(isNullOrEmpty(numString))
				return false;
			try {
				Integer.parseInt(numString);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
}
