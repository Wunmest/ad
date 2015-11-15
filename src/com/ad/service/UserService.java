package com.ad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.dao.entity.User;

@Service
public class UserService {

	@Autowired
	private User user;
	
	public boolean validate(User user){
		
		if(this.user.getUsername().equals(user.getUsername()) && this.user.getPassword().equals(user.getPassword())){
			
			return true;
		}
		
		return false;
	}
}
