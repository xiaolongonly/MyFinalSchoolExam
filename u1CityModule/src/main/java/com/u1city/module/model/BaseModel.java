package com.u1city.module.model;

import java.io.Serializable;

/***************************************
 * 
 * @author lwli
 * @date 2014-12-3
 * @time 上午9:27:00 类说明:实体类父类，要求所有的实体类继承此类
 * 
 **************************************/
public class BaseModel implements Serializable {
	private int id;
	protected String name = "";
	private String title = "";
	private String content = "";
	private String date="";//日期
	protected String url = "";	
	private String picUrl = "";
	private String linkValue = "";
	private int linkId;
	protected int type;
	protected int placeholder;// 占位符
	private String placeholderStr;// 占位符
	protected double price;// 商品价格
	protected boolean isStore;// 是否已经收藏了

	public int getLinkId() {
		return linkId;
	}

	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isStore() {
		return isStore;
	}

	public void setStore(boolean isStore) {
		this.isStore = isStore;
	}

	public int getPlaceholder() {
		return placeholder;
	}

	public String getPlaceholderStr() {
		return placeholderStr;
	}

	public void setPlaceholderStr(String placeholderStr) {
		this.placeholderStr = placeholderStr;
	}

	public void setPlaceholder(int placeholder) {
		this.placeholder = placeholder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLinkValue() {
		return linkValue;
	}

	public void setLinkValue(String linkValue) {
		this.linkValue = linkValue;
	}

	@Override
	public String toString() {
		return "BaseModel [id=" + id + ", name=" + name + ", content=" + content + ", url=" + url + ", title=" + title + ", linkValue=" + linkValue + ", type=" + type + ", placeholder=" + placeholder
				+ ", placeholderStr=" + placeholderStr + ", price=" + price + ", isStore=" + isStore + ", picUrl=" + picUrl + "]";
	}

}
