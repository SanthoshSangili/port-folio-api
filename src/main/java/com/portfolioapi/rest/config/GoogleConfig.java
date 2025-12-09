package com.portfolioapi.rest.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "google")
public class GoogleConfig {

    private Api api = new Api();
    private Config config = new Config();

    @Data
    public static class Api {
        private String url;
        private String key;
    }

    @Data
    public static class Config {
        private SystemInstruction systemInstruction = new SystemInstruction();
    }

    @Data
    public static class SystemInstruction {
        private String role;
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }
}
