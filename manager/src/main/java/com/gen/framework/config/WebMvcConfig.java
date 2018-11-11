package com.gen.framework.config;

import com.gen.framework.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter{
	

	@Autowired
	private LoginInterceptor loginInterceptor;

	public void addInterceptors(InterceptorRegistry registry) {
		//System.out.println(	123);
		System.out.println(loginInterceptor);
		registry.addInterceptor(loginInterceptor);

	}
}
