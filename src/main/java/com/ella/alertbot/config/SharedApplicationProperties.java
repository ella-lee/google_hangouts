package com.ella.alertbot.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Configuration
@ConfigurationProperties(prefix = "ella.alertbot")
@PropertySource("classpath:application.properties")
public class SharedApplicationProperties implements InitializingBean {

	@Setter
	@Getter
	private Key key = new Key();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	@Getter
    @Setter
	public static class Key {
		private String ellaWeb; 
		private String ellaWebDev;
	}

	
}
