package com.u1city.module.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author zhengjb
 * 商品分类
 * 一级分类中包含二级分类
 * */
public class GoodsTypeModel implements Serializable {   
	private String name;
	private String id;

	private ArrayList<SubGoodsTypeModel> twoTypes;

	private int total;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<SubGoodsTypeModel> getSubGoodsTypes() {
		return twoTypes;
	}

	public void setSubGoodsTypes(ArrayList<SubGoodsTypeModel> twoTypes) {
		this.twoTypes = twoTypes;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	/** 二级分类 */
	public static class SubGoodsTypeModel implements Serializable {    
		
		private String name;
		private String id;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
