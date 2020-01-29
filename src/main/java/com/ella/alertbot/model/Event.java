package com.ella.alertbot.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class Event {

	/// <summary>
	/// 누가(어떤 서버의 어떤 앱의 프로세스Id(또는 스레드 Id)
	/// </summary>
	@JsonAlias({"sender", "Sender"})
	private String sender;

	/// <summary>
	/// 언제
	/// </summary>
	@JsonAlias({"eventDT", "EventDT"})
	private Date eventDT;

	/// <summary>
	/// 어디서(어떤 소스 파일의 라인과 같은 특정 위치)
	/// </summary>
	@JsonAlias({"callerInformation", "CallerInformation"})
	private String callerInformation;

	/// <summary>
	/// 무엇을(로그 메세지)
	/// </summary>
	@JsonAlias({"message", "Message"})
	private String message;
	
}
