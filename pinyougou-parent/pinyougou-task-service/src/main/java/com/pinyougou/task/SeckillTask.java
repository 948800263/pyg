package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class SeckillTask {

	
	@Autowired
	private RedisTemplate   redisTemplate;
	
	@Autowired
	private TbSeckillGoodsMapper	seckillGoodsMapper;
	
	/**
	 * 增量刷新秒杀商品     作用是 把  新添加的秒杀商品进行上传到redis中
	 * 
	 * 每分钟执行一次
	 */
	@Scheduled(cron="0 * * * * ?")
	public void refreshSeckillGoods(){
		System.out.println("执行了任务调度"+new Date());			
		//查询所有的秒杀商品键集合   知道了已经有的所有的商品id
		List ids = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
		//查询正在秒杀的商品列表	查询时秒杀商品但是没有添加到redis中的数据
		// 1、条件1   必须审核通过
		// 2、条件2 必须和上面的id不重复
		// 3、条件3 不能过期
		// 4、条件4 必须有库存
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//审核通过
		criteria.andStockCountGreaterThan(0);//剩余库存大于0
		criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
		criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间		
		criteria.andIdNotIn(ids);//排除缓存中已经有的商品 		
		List<TbSeckillGoods> seckillGoodsList= seckillGoodsMapper.selectByExample(example);		
		//装入缓存    把查询到的数据放到 redis中
		for( TbSeckillGoods seckill:seckillGoodsList ){
			redisTemplate.boundHashOps("seckillGoods").put(seckill.getId(), seckill);
		}
		System.out.println("将"+seckillGoodsList.size()+"条商品装入缓存");
	}
	
	/* 检查哪些秒杀商品活动结束 了  如果结束 那么删除redis中的数据
	 * 删除是 每秒执行一次
	 *  */
	@Scheduled(cron="* * * * * ?")
	public void removeSeckillGoods(){
		System.out.println("移除秒杀商品任务在执行");
		//扫描缓存中秒杀商品列表，发现过期的移除
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
		for( TbSeckillGoods seckill:seckillGoodsList ){
			if(seckill.getEndTime().getTime()<new Date().getTime()  ){//如果结束日期小于当前日期，则表示过期
				seckillGoodsMapper.updateByPrimaryKey(seckill);//向数据库保存记录
				redisTemplate.boundHashOps("seckillGoods").delete(seckill.getId());//移除缓存数据
				System.out.println("移除秒杀商品"+seckill.getId());
			}			
		}
		System.out.println("移除秒杀商品任务结束");		
	}
}
