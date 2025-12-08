package com.portfolioapi.rest.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import dto.BaseDto;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/chat")
@Log4j2
public class ChatController {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${google.api.url}")
	private String googleApiUrl;

	@Value("${google.api.key}")
	private String apiKey;

	@PostMapping("/send")
	public BaseDto receiveChat(@RequestBody Map<String, Object> payload) {

		BaseDto baseDto = new BaseDto();

		String text = (String) payload.get("chatText");
		log.info("Received chat: " + text);

		if (text == null || text.isEmpty()) {
			baseDto.setStatusCode(1);
			baseDto.setResponseContent("No chat text provided!");
			log.info("No chat text provided!");
			return baseDto;
		}

		// Prepare request body for Google API
		Map<String, Object> requestBody = Map.of("contents",
				List.of(Map.of("parts", List.of(Map.of("text", text)), "role", "user")));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Goog-Api-Key", apiKey);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(googleApiUrl, request, String.class);

			baseDto.setStatusCode(0);
			baseDto.setResponseContent(response.getBody());
			log.info("Google API response: " + response.getBody());

		} catch (HttpClientErrorException e) {
			// Handle client errors (like 403)
			baseDto.setStatusCode(1);
			baseDto.setResponseContent("Error from Google API: " + e.getStatusCode() + " - " + e.getStatusText());
			log.info("Google API returned error: " + e.getMessage());

		} catch (Exception e) {
			// Handle other errors
			baseDto.setStatusCode(1);
			baseDto.setResponseContent("Internal error: " + e.getMessage());
			log.info("Error calling Google API: " + e.getMessage());
		}

		return baseDto;
	}
}
