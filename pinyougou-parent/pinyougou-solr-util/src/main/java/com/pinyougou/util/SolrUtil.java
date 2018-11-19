package com.pinyougou.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.druid.support.json.JSONParser;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper  itemMapper;
	@Autowired
	private SolrTemplate  solrTemplate;
	
	//数据的导入
	public void importData(){
		TbItemExample example = new TbItemExample();
		example.createCriteria().andStatusEqualTo("1");
		//查询数据
		List<TbItem> list = itemMapper.selectByExample(example);
		
		for (TbItem tbItem : list) {
			Map map = (Map) JSON.parse(tbItem.getSpec());
			tbItem.setSpecMap(map);
		}
	
		
		//保存到solr里面
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}
//	public static void main(String[] args) {
//		System.out.println("--读取配置文件");
//		ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
//		System.out.println("--获取solr对象");
//		SolrUtil solrUtil=  (SolrUtil) context.getBean("solrUtil");
//		System.out.println("--执行导入");
//		solrUtil.importData();
//	}

}
