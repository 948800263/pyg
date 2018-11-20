package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.json.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
@Component
public class ItemSearchListener implements MessageListener{

	@Autowired
	private ItemSearchService itemSearchService;
	
	
	@Override
	public void onMessage(Message message) {
	
		try {
			TextMessage textMessage=(TextMessage)message;			
			String text = textMessage.getText();
			List<TbItem> list = (List<TbItem>) JSON.parse(text, TbItem.class);
			for(TbItem item:list){
				System.out.println(item.getId()+" "+item.getTitle());
				Map specMap= JSON.parse(item.getSpec(),Map.class);//将spec字段中的json字符串转换为map
				item.setSpecMap(specMap);//给带注解的字段赋值
			}			
			itemSearchService.importList(list);//导入	
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

}
