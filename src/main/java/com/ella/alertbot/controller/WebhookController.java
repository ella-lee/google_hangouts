package com.ella.alertbot.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ella.alertbot.model.Event;
import com.ella.alertbot.model.Text;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;

@RestController
public class WebhookController {

	private static final String WEBHOOK_URL = "WEBHOOK_URL"; // Hangouts webhook url
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping("/webhook")
	public void doPost(@RequestBody Event event, HttpServletResponse res) throws GeneralSecurityException, IOException {

		try {
			logger.debug("webhook start");

			String strText = String.format("%s [%s,%s] : %s", event.getEventDT(), event.getSender(),
					event.getCallerInformation(), event.getMessage());

			Text text = new Text(strText);
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(text);

			GenericUrl url = new GenericUrl(WEBHOOK_URL);
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

			HttpContent content = new ByteArrayContent("application/json", jsonString.getBytes("UTF-8"));
			HttpRequest request = requestFactory.buildPostRequest(url, content);

			request.execute();
		} catch (Exception e) {
			logger.error("ella webhook server error", e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

	}

}
