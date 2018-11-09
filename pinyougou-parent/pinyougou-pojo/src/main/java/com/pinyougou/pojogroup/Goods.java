package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

public class Goods implements Serializable{
	
	//使用SPU
	private TbGoods goods;
	//使用详情介绍
	private TbGoodsDesc  goodsDesc;
	//SKU的设置
	private List<TbItem> itemList;
	
	
	
	public Goods(TbGoods goods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
		this.goods = goods;
		this.goodsDesc = goodsDesc;
		this.itemList = itemList;
	}

	public Goods() {
		// TODO Auto-generated constructor stub
	}
	
	public TbGoods getGoods() {
		return goods;
	}
	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}
	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public List<TbItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}
	
	
	
}
