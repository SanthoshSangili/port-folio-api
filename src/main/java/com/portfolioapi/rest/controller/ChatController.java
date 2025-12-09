package com.portfolioapi.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolioapi.rest.config.GoogleConfig;

import dto.BaseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/chat")
@Log4j2
@RequiredArgsConstructor
public class ChatController {

	private final RestTemplate restTemplate = new RestTemplate();
	private final GoogleConfig googleConfig;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostMapping("/send")
	public BaseDto receiveChat(@RequestBody Map<String, Object> payload) {

		BaseDto dto = new BaseDto();

		if (payload == null || payload.isEmpty()) {
			dto.setStatusCode(1);
			dto.setMessage("No contents provided!");
			return dto;
		}

		log.info("Received payload: {}", payload);

		List<Map<String, Object>> uiContents = (List<Map<String, Object>>) payload.get("contents");

		if (uiContents == null) {
			dto.setStatusCode(1);
			dto.setMessage("UI contents missing!");
			return dto;
		}

		Map<String, Object> systemMessage = Map.of("role", googleConfig.getConfig().getSystemInstruction().getRole(),
				"parts", googleConfig.getConfig().getSystemInstruction().getParts());

		Map<String, Object> finalRequestBody = new HashMap<>();
		finalRequestBody.put("system_instruction", systemMessage);
		finalRequestBody.put("contents", uiContents);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Goog-Api-Key", googleConfig.getApi().getKey());

		HttpEntity<Object> request = new HttpEntity<>(finalRequestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(googleConfig.getApi().getUrl(), request,
					String.class);

			Object jsonResponse = objectMapper.readValue(response.getBody(), Object.class);

			dto.setStatusCode(0);
			dto.setResponseContent(jsonResponse);
			return dto;

		} catch (Exception e) {
			log.error("API Error: ", e);
			dto.setStatusCode(1);
			dto.setErrorDescription(e.getMessage());
			return dto;
		}
	}

}
