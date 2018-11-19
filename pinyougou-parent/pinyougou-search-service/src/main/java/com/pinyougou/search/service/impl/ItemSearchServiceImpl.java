package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.SolrRealtimeGetRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.util.SolrUtil;
@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService{

	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public Map<String, Object> search(Map searchMap) {
		
		//高亮
		SimpleHighlightQuery query = new SimpleHighlightQuery();
		HighlightOptions   options = new HighlightOptions().addField("item_title");
		options.setSimplePrefix("<font color='red'>");
		options.setSimplePostfix("</font>");
		query.setHighlightOptions(options);
		//添加查询条件
		String keywords = ((String) searchMap.get("keywords")).replace(" ", "");
		Criteria criteria=new Criteria("item_keywords").is(keywords);
		query.addCriteria(criteria);
		//添加分类
		if(!"".equals(searchMap.get("category"))){
			Criteria fc = new Criteria("item_category").is(searchMap.get("category"));
			SimpleFilterQuery q2 = new SimpleFilterQuery(fc );
			query.addFilterQuery(q2);
		}
		//添加品牌
		if(!"".equals(searchMap.get("brand"))){
			Criteria fc = new Criteria("item_brand").is(searchMap.get("brand"));
			SimpleFilterQuery q3 = new SimpleFilterQuery(fc );
			query.addFilterQuery(q3);
		}
		//添加品牌
		if(!"".equals(searchMap.get("spec"))){
			Map<String,String> map = (Map<String, String>) searchMap.get("spec");
			for (String  key: map.keySet()) {
				Criteria fc = new Criteria("item_spec_"+key).is(map.get(key));
				SimpleFilterQuery q4 = new SimpleFilterQuery(fc);
				query.addFilterQuery(q4);
			}
		}
		//添加价格
		if(!"".equals(searchMap.get("price"))){
			String p = (String) searchMap.get("price");
			String[] split = p.split("-");
			Criteria fc = null;
			if(split[1].equals("*")){
				fc = new Criteria("item_price").greaterThan(split[0]);
			}else{
			    fc = new Criteria("item_price").between(split[0], split[1]);
			}
			SimpleFilterQuery q3 = new SimpleFilterQuery(fc );
			query.addFilterQuery(q3);
		}
		//增加排序条件
		String sortField = (String) searchMap.get("sortField");
		if(sortField!=null && !sortField.equals("")){
			String sort = (String)searchMap.get("sort");
			if(sort.equals("ASC")){
				Sort  sort1 = new Sort(Sort.Direction.ASC, "item_"+sortField);
				query.addSort(sort1);
			}else{
				Sort  sort1 = new Sort(Sort.Direction.DESC, "item_"+sortField);
				query.addSort(sort1);
			}
		}
		
		//获取pageNo和PageSize
		Integer pageNo = (Integer) searchMap.get("pageNo");
		Integer pageSize = (Integer) searchMap.get("pageSize");
		
		query.setOffset((pageNo-1)*pageSize);
		query.setRows(pageSize);
		
		
		
		//执行查询
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		List<TbItem> list = page.getContent();
		Map map = new HashMap<>();
		
		for(HighlightEntry<TbItem> h:page.getHighlighted()){//循环高亮入口集合
			TbItem item = h.getEntity();//获取原实体类	
			if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
			}			
		}		
		//2.根据关键字查询商品分类
		List categoryList = searchCategoryList(searchMap);
		map.put("categoryList",categoryList);	
		
		//添加返回分页所需条件
		map.put("totalPages", page.getTotalPages());//返回总页数
		map.put("total", page.getTotalElements());//返回总记录数
		
		
		map.put("rows", list);
		//3.根据category的名字来查询品牌和规格
		//判断category有没有传递过来
		String  cate = (String) searchMap.get("category");
		if(!"".equals(cate)){
			map.putAll(searchBrandAndSpecList(cate));
		}else{
			if(categoryList.size()>0){
				map.putAll(searchBrandAndSpecList((String)categoryList.get(0)));
			}
		}
		return map;
	}
	
	
	/**
	 * 查询分类列表  
	 * @param searchMap
	 * @return
	 */
	private  List searchCategoryList(Map searchMap){
		List<String> list=new ArrayList();	
		Query query=new SimpleQuery();		
		//按照关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//设置分组选项   
		GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//获取指定的分裂结果
		GroupResult<TbItem> result = page.getGroupResult("item_category");
		//获取分类结果集  
		Page<GroupEntry<TbItem>> page2 = result.getGroupEntries();
		//获取分类结果的内容
		List<GroupEntry<TbItem>> list2 = page2.getContent();
		//遍历所有的GroupEntry 获取value  
		for (GroupEntry<TbItem> groupEntry : list2) {
			list.add(groupEntry.getGroupValue()); //把查询到的value保存到list集合中并返回
		}
		
		return list;
	}
	
	/**
	 * 查询品牌
	 * 
	 * */
	private Map searchBrandAndSpecList(String category){
		
		Long typeid = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		
		List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeid);
		
		List specList = (List) redisTemplate.boundHashOps("specList").get(typeid);
		
		Map map = new HashMap<>();
		map.put("brandList", brandList);
		map.put("specList", specList);
		return map;	
		
	}


	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}


	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		Query query=new SimpleQuery();		
		Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
