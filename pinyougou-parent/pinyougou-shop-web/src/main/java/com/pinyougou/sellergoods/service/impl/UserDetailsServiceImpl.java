package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService{
	
	
	private SellerService sellerService;
	

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		//先查询出他的密码
		TbSeller seller = sellerService.findOne(username);
		
		if(seller!=null&&seller.getStatus().equals("1")){
			
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			
			authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
			return new User(username,seller.getPassword(), authorities);
		}
		return null;
	}

}
