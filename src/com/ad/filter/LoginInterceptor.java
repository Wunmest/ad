package com.ad.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ad.domain.XResponse;

/**
 * 登录检测, 采用拦截器的形式
 * */
public class LoginInterceptor extends HandlerInterceptorAdapter {
	private static Log log = LogFactory.getLog(LoginInterceptor.class);
	private String[] excluded;
/*	
	{
		excluded = new String[]{"login.html"};
	}
*/
	public void setExcluded(String[] excluded){
		this.excluded = excluded;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException{
		
		// 打印请求头信息
		getHeadersInfo(request);
		// 客户端cookie
		Cookie[] cookies = request.getCookies();
		
		// excluded 中的地址不拦截
		String requestUri = request.getRequestURI();
		System.out.println("当前请求地址：" + requestUri);
		for(String uri : excluded){
			if(requestUri.contains(uri)){
				return true;
			}
		}
		// 用户已登录的话不拦截
		Object user = request.getSession().getAttribute("user");
		if(user != null){
			log.debug(user + "已登录.");
			return true;
		}
		// 其余情况均拦截
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.getWriter().write(new XResponse(1, "尚未登陆或无权限.").toString());
		return false;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception{
		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
		
	}
	
	/**
	 * request headers info
	 * */
	private void getHeadersInfo(HttpServletRequest request){
		Enumeration headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()){
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			log.debug(key + ":" + value);
		}
	}
	
	
}
