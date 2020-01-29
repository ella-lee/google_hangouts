package com.ella.alertbot.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ella.alertbot.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.chat.v1.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

@RestController
public class ChatBotController {

	private final List<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/chat.bot");
	private static final String RESPONSE_URL_TEMPLATE = "https://chat.googleapis.com/v1/spaces/%s/messages";
	private static final String SPACE_NAME = "SPACE_NAME"; // Hangouts space name

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping("/bot")
	public void doPost(@RequestBody Event event, HttpServletResponse res) throws Exception {

		try {
			logger.debug("bot start");

			Message message = new Message();
			String eventMsg = String.format("%s [%s,%s] : %s", event.getEventDT(), event.getSender(),
					event.getCallerInformation(), event.getMessage());
			message.setText(eventMsg);

			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(message);

			String spaceNameUrl = String.format(RESPONSE_URL_TEMPLATE, SPACE_NAME);
			GenericUrl url = new GenericUrl(spaceNameUrl);

			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			GoogleCredentials clientSecrets = ServiceAccountCredentials
					.fromStream(ChatBotController.class.getResourceAsStream("/service-acct.json")).createScoped(SCOPE);

			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(clientSecrets);
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory(requestInitializer);

			HttpContent content = new ByteArrayContent("application/json", jsonString.getBytes("UTF-8"));
			HttpRequest request = requestFactory.buildPostRequest(url, content);

			request.execute();
		} catch (Exception e) {
			logger.error("ella alertbot server error", e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

	}

}
