package com.ella.alertbot.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ella.alertbot.model.Event;
import com.ella.alertbot.model.SlackText;

@RestController
public class SlackBotController {

	private static final String CHANNEL_KEY = "C019T16N6JD"; //"D019T0VRX5X";
	private static final String DEV_CHANNEL_KEY = "G019T3P537B"; //"D019T0VRX5X";
	
	private static final String AUTH_KEY = "xoxb-ella";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass()); // log 생성한 class 지정

	@PostMapping("/slackbot")
	public void doPost(@RequestBody Event event, HttpServletResponse res) throws Exception {
		logger.info("slack bot start");
		
		String url = UriComponentsBuilder.newInstance()
				.scheme("https")
				.host("slack.com")
				.path("/api")
				.path("/chat.postMessage")
				.build()
				.toUriString();
		
		HttpHeaders header = new HttpHeaders();
		header.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		header.setContentType(MediaType.APPLICATION_JSON);
		header.setBearerAuth(AUTH_KEY);
		
		SimpleDateFormat customDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E) HH:mm:ss z", Locale.KOREA);
		String eventMsg = String.format("%s [%s, %s] : %s", 
				customDateFormat.format(event.getEventDT() == null ? new Date() : event.getEventDT()),
				event.getSender() == null ? "Unknown Sender" : event.getSender(),
				event.getCallerInformation() == null ? "Unknown CallerInformation" : event.getCallerInformation(), 
				event.getMessage() == null ? "No additional details provided for this alert" : event.getMessage());
		
		SlackText slackText = new SlackText(CHANNEL_KEY, eventMsg);
		HttpEntity<SlackText> entity = new HttpEntity<SlackText>(slackText, header);
		RestTemplate restTemplate = new RestTemplate();

		try {
			restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		} catch (Exception e) {
			logger.error("maxst-server-bot server error");
			logger.error(this.getClass().getSimpleName() + ", failed restTemplate.exchange", e);
		}
	}
}
