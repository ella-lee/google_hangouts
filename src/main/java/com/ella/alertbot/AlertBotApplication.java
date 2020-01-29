package com.ella.alertbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.ella.alertbot.auth.AuthFilter;
import com.ella.alertbot.auth.JwtProvider;

@SpringBootApplication
public class AlertBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertBotApplication.class, args);
	}
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Bean
	public FilterRegistrationBean<AuthFilter> loggingFilter(){
	    FilterRegistrationBean<AuthFilter> registrationBean 
	      = new FilterRegistrationBean<>();
	         
	    registrationBean.setFilter(new AuthFilter(jwtProvider));
	    registrationBean.addUrlPatterns("/bot","/webhook","/token");
	         
	    return registrationBean;    
	}
}
