package com.pinyougou.cart.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {

		@Reference(timeout=6000)
		private CartService cartService;
		
		@Autowired
		private  HttpServletRequest request;
		
		@Autowired
		private  HttpServletResponse response;
		
		
		/**
		 * 购物车列表
		 * @param request
		 * @return    查询 的时候 先查询cookie 看有没有数据，如果有看有没有登陆 ，如果没有登陆直接返回cookie中的数据，如果已经登陆了，那么要将cookie中的数据和redis中的数据进行合并返回合并后结果
		 */
		@RequestMapping("/findCartList")
		public List<Cart> findCartList(){
			
			//得到登陆人账号,判断当前是否有人登陆
			String username = SecurityContextHolder.getContext().getAuthentication().getName(); 
			String cartListString = util.CookieUtil.getCookieValue(request, "cartList","UTF-8");
			if(cartListString==null || cartListString.equals("")){
				cartListString="[]";
			}
			List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
			System.out.println(cartList_cookie);
			if(username.equals("anonymousUser")){//代表没登陆
				return cartList_cookie;	
			}else{
				List<Cart> cartList_redis =cartService.findCartListFromRedis(username);//从redis中提取	
				if(cartList_cookie.size()>0){//如果本地存在购物车
					//合并购物车
					cartList_redis=cartService.mergeCartList(cartList_redis, cartList_cookie);	
					//清除本地cookie的数据
					util.CookieUtil.deleteCookie(request, response, "cartList");
					//将合并后的数据存入redis 
					cartService.saveCartListToRedis(username, cartList_redis); 
				}			
				System.out.println("carListRedis"+cartList_redis);
				return cartList_redis;
			}
			
		}
		
		/**
		 * 添加商品到购物车
		 * @param request
		 * @param response
		 * @param itemId
		 * @param num
		 * @return
		 */
		@RequestMapping("/addGoodsToCartList")
		//@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
		public Result addGoodsToCartList(Long itemId,Integer num){
			response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			
			//得到登陆人账号,判断当前是否有人登陆
			String username = SecurityContextHolder.getContext().getAuthentication().getName(); 
				try {
					List<Cart> cartList =findCartList();//获取购物车列表
					cartList = cartService.addGoodsToCartList(cartList, itemId, num);	
					if(username.equals("anonymousUser")){
						util.CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
						return new Result(true, "添加成功");
					}else{
						cartService.saveCartListToRedis(username, cartList);
						return new Result(true, "添加成功");
					}
				} catch (Exception e) {
					e.printStackTrace();
					return new Result(false, "添加失败");
				}
			
		}	
		
}
