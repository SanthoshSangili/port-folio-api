//package com.portfolioapi.rest.config;
//
//import java.util.List;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//import lombok.Data;
//
//@Data
//@Configuration
//@ConfigurationProperties(prefix = "google")
//public class GoogleConfig {
//
//    private Api api = new Api();
//    private Config config = new Config();
//
//    @Data
//    public static class Api {
//        private String url;
//        private String key;
//    }
//
//    @Data
//    public static class Config {
//        private SystemInstruction systemInstruction = new SystemInstruction();
//    }
//
//    @Data
//    public static class SystemInstruction {
//        private String role;
//        private List<Part> parts;
//    }
//
//    @Data
//    public static class Part {
//        private String text;
//    }
//}

package com.portfolioapi.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

import java.util.Map;

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
