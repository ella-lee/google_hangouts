package com.ella.alertbot.auth;

import static com.ella.alertbot.auth.JwtConst.SERVERID;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ella.alertbot.config.SharedApplicationProperties;
import com.ella.alertbot.config.SharedApplicationProperties.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultJwtParser;

@SuppressWarnings("serial")
@Component("jwtProvider")
public class JwtProvider implements Serializable {

	private final Logger logger = LoggerFactory.getLogger(this.getClass()); // log 생성한 class 지정

	@Autowired
	SharedApplicationProperties properties;

	public String getKeyByServerId(String serverId) {

		String key = "";
		Key keys = properties.getKey();

		switch (serverId) {
		case "ellaWeb":
			key = keys.getEllaWeb();
			break;
		case "ellaWebDev":
			key = keys.getEllaWebDev();
			break;
		default:
			break;
		}
		
		return key;
	} 

	private Claims getAllClaimsFromToken(String token) {
		String[] splitToken = token.split("\\.");
		String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";

		DefaultJwtParser parser = new DefaultJwtParser();
	        Jwt<?, ?> jwt = parser.parse(unsignedToken);
	        Claims claims = (Claims) jwt.getBody();
		return claims;
	}
	
	private Boolean isTokenExpired(Date iat) {
		return !new Date().before(DateUtils.addSeconds(iat, 3600)); //iat+1hr 
	}

	public Boolean validateToken(String token) {
		
		try {
			final Claims claims = getAllClaimsFromToken(token);
			String serverId = claims.get(SERVERID).toString();
			String signingKey = getKeyByServerId(serverId);
			byte[] decodedKey = Base64.getDecoder().decode(signingKey);
			
			Jwts.parser().setSigningKey(decodedKey).parseClaimsJws(token).getBody();
 
			Date iat = claims.getIssuedAt();
			return !isTokenExpired(iat);
		} catch (SignatureException ex) {
			logger.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}

		return false;
	}

}
