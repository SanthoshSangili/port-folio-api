package com.portfolioapi.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "google")
@Data
public class GoogleConfig {

	private Api api;
	private Config config;

	@Data
	public static class Api {
		private String url;
		private String key;
		private String telUrl;
		private String telCID;
	}

	@Data
	public static class Config {
		private SystemInstruction systemInstruction;
		private String model;
		private double temperature;
		private int maxCompletionTokens;
		private double topP;
		private boolean stream;
		private String reasoningEffort;
	}

	@Data
	public static class SystemInstruction {
		private String role;
		private String content;
	}

}
