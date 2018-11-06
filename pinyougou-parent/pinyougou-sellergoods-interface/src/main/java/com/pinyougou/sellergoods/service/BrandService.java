package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;

public interface BrandService {

	public List<TbBrand> findAll();



	public PageResult findPage(Integer pageNum, Integer pageSize);



	public Result addBrand(TbBrand brand);



	public Result updateBrand(TbBrand brand);



	public Result delBrand(Long[] ids);



	public TbBrand getBrandById(Long id);



	public PageResult searchBrand(Integer pageNum, Integer pageSize, TbBrand brand);
	
}
