package com.solr.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pinyougou.util.SolrUtil;

@ContextConfiguration(locations={"classpath*:spring/applicationContext-dao.xml","classpath*:spring/applicationContext-solr.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class Test01 {

	@Autowired
	private SolrUtil  solrUtil;
	
	@Test
	public void testImportData(){
		solrUtil.importData();
	}
	
}
