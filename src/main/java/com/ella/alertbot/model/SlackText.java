package com.ella.alertbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlackText {

	/**
	 * channel
	 * */
	private String channel;

	/**
	 * text
	 * */
	private String text;
	
	public SlackText(String text) {
		this.text = text;
	}
}