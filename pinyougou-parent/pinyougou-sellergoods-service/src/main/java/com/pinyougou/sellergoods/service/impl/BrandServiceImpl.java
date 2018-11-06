package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@Service
public class BrandServiceImpl implements BrandService{

	@Autowired
	private TbBrandMapper  brandMapper;
	
	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(new TbBrandExample());
	}
	
	@Override
	public PageResult findPage(Integer pageNum,Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page =  (Page<TbBrand>) brandMapper.selectByExample(new TbBrandExample());
		
		return new PageResult(page.getTotal(),page.getResult());
	}
	
	//增加品牌
	@Override
	public Result addBrand(TbBrand brand){
		try{
			brandMapper.insert(brand);
			return new Result(true, "添加成功");
		}catch(Exception ex){
			return new Result(false, "添加失败");
		}
	}
	//修改品牌
	@Override
	public Result updateBrand(TbBrand brand){
		try{
			brandMapper.updateByPrimaryKey(brand);
			return new Result(true, "修改成功");
		}catch(Exception ex){
			return new Result(false, "修改失败");
		}
	}
	//删除品牌
	@Override
	public Result delBrand(Long[] ids){
		try{
			for (Long id : ids) {
				brandMapper.deleteByPrimaryKey(id);
			}
			return new Result(true, "删除成功");
		}catch(Exception ex){
			return new Result(false, "删除失败");
		}
	}
	//根据id查询指定品牌
	@Override
	public TbBrand getBrandById(Long id){
			return brandMapper.selectByPrimaryKey(id);
	}
	//条件查询
	@Override
	public PageResult searchBrand(Integer pageNum,Integer pageSize,TbBrand brand) {
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample brandExample = new TbBrandExample();
		Criteria criteria = brandExample.createCriteria();
		
		if(brand!=null&&brand.getName()!=null&&!brand.getName().equals("")){
			criteria.andNameLike("%"+brand.getName()+"%");
		}
		if(brand!=null&&brand.getFirstChar()!=null&&!brand.getFirstChar().equals("")){
			criteria.andFirstCharEqualTo(brand.getFirstChar());
		}
		
		Page<TbBrand> page =  (Page<TbBrand>) brandMapper.selectByExample(brandExample);
		
		return new PageResult(page.getTotal(),page.getResult());
	}
	

}
