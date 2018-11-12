package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 审核和驳回
	 * 
	 * */
	@Override
	public void updateStatus(Long[] ids,String status){
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
	}
	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {	
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");//1是删除了  0是没删除
			goodsMapper.updateByPrimaryKey(goods);
		}	
	}
	
	
	
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		TbGoodsExample exam = new TbGoodsExample();
		exam.createCriteria().andIsDeleteIsNull();
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(exam);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);		
	}
	
	@Autowired
	private TbGoodsDescMapper descMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Override
	public void add(Goods goods) {
		 goods.getGoods().setAuditStatus("0");//设置初始化状态为 未申请状态
		
		 goodsMapper.insert(goods.getGoods());//添加商品
		 TbGoodsDesc desc = goods.getGoodsDesc();//添加商品详情
		 desc.setGoodsId(goods.getGoods().getId());//设置商品详情和商品的关联
		 descMapper.insert(desc);
		 
		 saveSKU(goods);//SKU添加
	}
	
	public void saveSKU(Goods goods){
		 for(TbItem item :goods.getItemList()){
				//标题
				String title= goods.getGoods().getGoodsName();   
				Map<String,Object> specMap = JSON.parseObject(item.getSpec());//Spec规格
				for(String key:specMap.keySet()){
					title+=" "+ specMap.get(key);
				}
				item.setTitle(title);		
				item.setGoodsId(goods.getGoods().getId());//商品SPU编号
				item.setSellerId(goods.getGoods().getSellerId());//商家编号
				item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
				item.setCreateTime(new Date());//创建日期
				item.setUpdateTime(new Date());//修改日期 
				
				//品牌名称
				TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
				item.setBrand(brand.getName());
				
				//分类名称
				TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
				item.setCategory(itemCat.getName());		
				
				//商家名称
				TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
				item.setSeller(seller.getNickName());		
				//图片地址（取spu的第一个图片）
				List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
				if(imageList.size()>0){
					item.setImage ( (String)imageList.get(0).get("url"));
				}		
				itemMapper.insert(item);
			}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//改商品信息
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//改商品详情
		descMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//改SKU
			//1、删除
			TbItemExample example = new TbItemExample();
			example.createCriteria().andGoodsIdEqualTo(new Long(goods.getGoods().getId()));
			itemMapper.deleteByExample(example);
			//2、添加
			saveSKU(goods);
	}	
	@Override
	public void update(TbGoods goods){
		//改商品信息
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		//把商品对象放进去
		goods.setGoods(goodsMapper.selectByPrimaryKey(id));
		//获取商品详情
		goods.setGoodsDesc(descMapper.selectByPrimaryKey(id));
		
		//获取SKU的信息
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> list = itemMapper.selectByExample(example);
		goods.setItemList(list);
		
		
		return goods;
	}

	
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//查询未删除的数据
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
