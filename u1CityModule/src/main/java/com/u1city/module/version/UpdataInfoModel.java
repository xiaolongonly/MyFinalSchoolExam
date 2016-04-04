package com.u1city.module.version;

/*
 * 系统版本更新
 * @author joan
 * @date 2014-12-8
 * @time PM 16:58:33 
 * 
 */
public class UpdataInfoModel {
	
	private String version="";  
	private String url="";  
	private String description="";  
	
	public String getVersion() {  
		return version;  
	}  
	public void setVersion(String version) {  
		this.version = version;  
	}  
	public String getUrl() {  
		return url;  
	}  
	public void setUrl(String url) {  
		this.url = url;  
	}  
	public String getDescription() {  
		return description;  
	}  
	public void setDescription(String description) {  
		this.description = description;  
	}  
}
