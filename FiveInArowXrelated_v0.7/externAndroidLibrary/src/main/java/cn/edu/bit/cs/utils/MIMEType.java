package cn.edu.bit.cs.utils;
/**
 * ��װMIME������Ϣ
 * @author JinXuLiang
 *
 */
public class MIMEType {
	private String fileExtension="";
	private String MIME="";
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public String getMIME() {
		return MIME;
	}
	public void setMIME(String mIME) {
		MIME = mIME;
	}
	public MIMEType(String fileExt, String MIME){
		this.fileExtension=fileExt;
		this.MIME=MIME;
	}
	@Override
	public boolean equals(Object o) {
		if(o==null || o instanceof MIMEType ==false){
			return false;
		}
		MIMEType other=(MIMEType)o;
		return other.fileExtension.equals(fileExtension);
	}
	@Override
	public int hashCode() {
		return fileExtension.hashCode();
	}
	

	
}
