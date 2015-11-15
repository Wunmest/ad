package com.ad.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 登录检测, 采用过滤器的形式
 * */
public class LoginFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//不过滤的uri
		String[] excluded = new String[]{"login.html"};
		//当前请求的uri
		String uri = request.getRequestURI();
		
		if(Arrays.asList(excluded).contains(uri)){
			
		}
	}
}
