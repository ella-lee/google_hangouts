package com.ella.alertbot.auth;

import static com.ella.alertbot.auth.JwtConst.SHD_TOKEN_KEY;
import static com.ella.alertbot.auth.JwtConst.TOKEN_TYPE;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private JwtProvider jwtProvider;

	public AuthFilter(JwtProvider jwtProvider) {
		super();
		this.jwtProvider = jwtProvider;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String header = req.getHeader(SHD_TOKEN_KEY);
		String token = "";

		if (header != null && header.startsWith(TOKEN_TYPE)) {
			token = header.replace(TOKEN_TYPE, "");

			if (!jwtProvider.validateToken(token)) {
				logger.info("authentication failed");
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
			} else {
				logger.info("authentication completed");
				chain.doFilter(req, res);
			}
		} else {
			logger.warn("couldn't find token string");
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
		}

	}

}
