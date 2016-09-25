package cn.edu.bit.cs.myfileexplorer.model;


import java.util.Date;

/**
 * 代表文件与目录的基类
 * 默认支持按名字排序与判等
 * @author JinXuLiang
 *
 */
public class DisplayInfoBase implements Comparable<DisplayInfoBase> {
	/**
	 * 代表本文件或文件夹的完整路径
	 */
	protected String fullPath="";
	
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	/**
	 * 文件或文件夹名
	 */
	protected String name="";
	/**
	 * 要显示给用户的创建或修改时间
	 */
	protected Date createOrModifyDate;
	/**
	 * 将显示在UI上的给用户看的提示信息
	 */
	protected String description="";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateOrModifyDate() {
		return createOrModifyDate;
	}
	public void setCreateOrModifyDate(Date createOrModifyDate) {
		this.createOrModifyDate = createOrModifyDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public int compareTo(DisplayInfoBase another) {
		
		return name.toUpperCase().compareTo(another.name.toUpperCase());
	}
	@Override
	public boolean equals(Object o) {
		if(o==null || o instanceof DisplayInfoBase ==false){
			return false;
		}
		return compareTo((DisplayInfoBase)o)==0;
	}
	@Override
	public int hashCode() {
		
		return name.hashCode();
	}
	
	
}
