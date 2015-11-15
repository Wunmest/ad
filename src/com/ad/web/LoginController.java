package com.ad.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ad.dao.entity.User;
import com.ad.domain.XResponse;
import com.ad.service.UserService;


@Controller
public class LoginController {

	private Log log = LogFactory.getLog(LoginController.class);
	@Autowired
	private UserService userService;
	
	@ResponseBody
	@RequestMapping("/login")
	public XResponse login(HttpServletRequest request, HttpServletResponse response, User user){
		XResponse xResponse = new XResponse();
		
		if(userService.validate(user)){
			
			request.getSession().setAttribute("user", user);
		}else{
			
			xResponse.setReturnCode(1);
			xResponse.setReturnMessage("用户名或密码不正确.");
		}
		

		return xResponse;
	}
}
