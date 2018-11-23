package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num)throws Exception {
				//1.根据商品SKU ID查询SKU商品信息
				TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
				if(tbItem==null){
					throw new RuntimeException("商品不存在");
				}
				if(!tbItem.getStatus().equals("1")){
					throw new RuntimeException("商品状态无效");
				}
				//2.获取商家ID		
				String sellerId = tbItem.getSellerId();
				//3.根据商家ID判断购物车列表中是否存在该商家的购物车	
				Cart b = null;
				for (Cart cart : cartList) {
					if(sellerId.equals(cart.getSellerId())){
						b = cart;
					}
				}
				System.out.println("店家----"+b);
				if(b==null){
				//4.如果购物车列表中不存在该商家的购物车
				//4.1 新建购物车对象
					Cart cart = new Cart();
					cart.setSellerId(sellerId);
					cart.setSellerName(tbItem.getSeller());
					//创建对象
					TbOrderItem orderItem=new TbOrderItem();
					orderItem.setGoodsId(tbItem.getGoodsId());
					orderItem.setItemId(tbItem.getId());
					orderItem.setNum(num);
					orderItem.setPicPath(tbItem.getImage());
					orderItem.setPrice(tbItem.getPrice());
					orderItem.setSellerId(tbItem.getSellerId());
					orderItem.setTitle(tbItem.getTitle());
					orderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
					
					List orderItemList=new ArrayList();
					orderItemList.add(orderItem);
					cart.setOrderItemList(orderItemList);
				//4.2 将新建的购物车对象添加到购物车列表	
					cartList.add(cart);
				}else{
				//5.如果购物车列表中存在该商家的购物车		
				// 查询购物车明细列表中是否存在该商品
					List<TbOrderItem> orderItemList = b.getOrderItemList();
					TbOrderItem b1 = null;
					for (TbOrderItem tbOrderItem : orderItemList) {
						if(itemId.equals(tbOrderItem.getItemId())){
							System.out.println("---比较通过后---");
							b1 = tbOrderItem;
						}
					}
					System.out.println("是购物车列表---"+b1);
					if(b1==null){
					//5.1. 如果没有，新增购物车明细	
						//创建对象
						TbOrderItem orderItem=new TbOrderItem();
						orderItem.setGoodsId(tbItem.getGoodsId());
						orderItem.setItemId(tbItem.getId());
						orderItem.setNum(num);
						orderItem.setPicPath(tbItem.getImage());
						orderItem.setPrice(tbItem.getPrice());
						orderItem.setSellerId(tbItem.getSellerId());
						orderItem.setTitle(tbItem.getTitle());
						orderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
						b.getOrderItemList().add(orderItem);
					}else{
					//5.2. 如果有，在原购物车明细上添加数量，更改金
						b1.setNum(b1.getNum()+num);
						b1.setTotalFee(new BigDecimal(b1.getNum()*b1.getPrice().doubleValue()));
						//如果数量操作后小于等于0，则移除
						if(b1.getNum()<=0){
							b.getOrderItemList().remove(b1);//移除购物车明细	
						}
						//如果移除后cart的明细数量为0，则将cart移除
						if(b.getOrderItemList().size()==0){
							cartList.remove(b);
						}
					}
				}
		return cartList;
	}

	@Autowired
	private RedisTemplate<String, List<Cart>> redisTemplate;
	
	@Override
	public List<Cart> findCartListFromRedis(String username) {
	
		List<Cart> list = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		
		return list;
	}

	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis存入购物车数据....."+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
	}

	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		try{
			for(Cart cart: cartList2){
				for(TbOrderItem orderItem:cart.getOrderItemList()){
					cartList1= addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());		
				}			
			}		
			return cartList1;
		}catch(Exception e){
			return new ArrayList<>();
		}
	
	}

}
