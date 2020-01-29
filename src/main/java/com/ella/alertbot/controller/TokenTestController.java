package com.ella.alertbot.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ella.alertbot.config.SharedApplicationProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class TokenTestController {

	@Autowired
	SharedApplicationProperties properties;
	
	@GetMapping("/token")
	public String getToken() {
		String ellaWebDevKey = properties.getKey().getEllaWebDev();
		
		return Jwts.builder()
				.claim("serverId", "ellaWebDev")
				.signWith(SignatureAlgorithm.HS256, ellaWebDevKey)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.compact();
	}
}
