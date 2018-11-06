package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;
@RestController
@RequestMapping("/brand")
public class BrandController {
	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}
	@RequestMapping("/findPage")
	public PageResult findPage(Integer pageNum,Integer pageSize){
		return brandService.findPage(pageNum, pageSize);
	}
	
	@RequestMapping("/addBrand")
	public Result addBrand(@RequestBody TbBrand brand){
		return brandService.addBrand(brand);
	}
	@RequestMapping("/updateBrand")
	public Result updateBrand(@RequestBody TbBrand brand){
		return brandService.updateBrand(brand);
	}
	@RequestMapping("/delBrand")
	public Result delBrand(Long[] ids){
		return brandService.delBrand(ids);
	}
	@RequestMapping("/getBrandById")
	public TbBrand getBrandById(Long id){
		return brandService.getBrandById(id);
	}
	@RequestMapping("/searchBrand")
	public PageResult searchBrand(Integer pageNum,Integer pageSize,@RequestBody TbBrand brand){
		
		return brandService.searchBrand(pageNum, pageSize, brand);
	}
	
}
