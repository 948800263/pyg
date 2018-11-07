package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

public class Specification implements Serializable{
	
	private TbSpecification specification;
	private List<TbSpecificationOption> specificationOptions;
	
	public Specification() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Specification(TbSpecification specification, List<TbSpecificationOption> specificationOptions) {
		super();
		this.specification = specification;
		this.specificationOptions = specificationOptions;
	}


	public TbSpecification getSpecification() {
		return specification;
	}
	public void setSpecification(TbSpecification specification) {
		this.specification = specification;
	}
	public List<TbSpecificationOption> getSpecificationOptions() {
		return specificationOptions;
	}
	public void setSpecificationOptions(List<TbSpecificationOption> specificationOptions) {
		this.specificationOptions = specificationOptions;
	}
	
	

}
