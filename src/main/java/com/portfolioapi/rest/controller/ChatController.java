package com.portfolioapi.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin(origins = "*")
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

		// ------------------------------------------------
		// CHAT BOT SEND
		// ------------------------------------------------

		try {
			List<Map<String, Object>> messages = (List<Map<String, Object>>) payload.get("messages");

			List<Map<String, Object>> chatHistry = (List<Map<String, Object>>) payload.get("contents");

			log.info("chatHistry: {}", chatHistry);

			if (messages == null || messages.isEmpty()) {
				dto.setStatusCode(1);
				dto.setMessage("No messages provided!");
				return dto;
			}

			// SYSTEM MESSAGE
			Map<String, Object> systemMessage = new HashMap<>();
			systemMessage.put("role", googleConfig.getConfig().getSystemInstruction().getRole());
			systemMessage.put("content", googleConfig.getConfig().getSystemInstruction().getContent());

			messages.add(0, systemMessage);

			// FINAL GROQ REQUEST BODY
			Map<String, Object> finalRequestBody = new HashMap<>();
			finalRequestBody.put("model", googleConfig.getConfig().getModel());
			finalRequestBody.put("messages", messages);

			// HEADERS
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(googleConfig.getApi().getKey());

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(finalRequestBody, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(googleConfig.getApi().getUrl(), request,
					String.class);

			Object jsonResponse = objectMapper.readValue(response.getBody(), Object.class);

			dto.setStatusCode(0);
			dto.setResponseContent(jsonResponse);

			// ------------------------------------------------
			// TELEGRAM SEND
			// ------------------------------------------------

			// jsonResponse is the full response object
			Map<String, Object> map = (Map<String, Object>) jsonResponse;

			// Get "choices" directly from the top-level map
			List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");

			if (choices != null && !choices.isEmpty()) {
				// Get first choice
				Map<String, Object> firstChoice = choices.get(0);

				// Get message map
				Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

				// Get AI response content
				String aiResponse = message != null ? (String) message.get("content") : null;

				log.info("AI Response: " + aiResponse);

				// Check if it contains "outside my scope"
				if (aiResponse != null && aiResponse.toLowerCase().contains("outside my scope")) {
//					String lastUserMessage = messages.get(messages.size() - 1).get("content").toString();

					log.info("googleConfig.getTelegramApiUrl(): {}", googleConfig.getApi().getTelUrl());

					// Build the formatted string for Telegram
					StringBuilder sb = new StringBuilder();
					for (Map<String, Object> content : chatHistry) {
						sb.append("role: ").append(content.get("role")).append("\n");
						sb.append("message: ").append(content.get("message")).append("\n");
					}

					// Append the AI response **at the end**
					if (aiResponse != null) {
						sb.append("role: system\n");
						sb.append("message: ").append(aiResponse).append("\n");
					}

					// Remove trailing newline
					String formattedMessage = sb.toString().trim();

					// Send to Telegram
					Map<String, Object> telegramBody = new HashMap<>();
					telegramBody.put("chat_id", googleConfig.getApi().getTelCID());
					telegramBody.put("text", formattedMessage);

					HttpHeaders telegramHeaders = new HttpHeaders();
					telegramHeaders.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<Map<String, Object>> telegramRequest = new HttpEntity<>(telegramBody, telegramHeaders);

					restTemplate.postForEntity(googleConfig.getApi().getTelUrl(), telegramRequest, String.class);
					log.info("Received payload: {}", payload);
				} else {
					log.error("Response is inside scope");
				}
			} else {
				log.error("No choices found in response");
			}

			return dto;

		} catch (Exception e) {
			dto.setStatusCode(1);
			dto.setErrorDescription(e.getMessage());
			return dto;
		}
	}

//	@PostMapping("/send")
//	public BaseDto receiveChat(@RequestBody Map<String, Object> payload) {
//
//		BaseDto dto = new BaseDto();
//
//		if (payload == null || payload.isEmpty()) {
//			dto.setStatusCode(1);
//			dto.setMessage("No contents provided!");
//			return dto;
//		}
//
//		log.info("Received payload: {}", payload);
//
//		List<Map<String, Object>> uiContents = (List<Map<String, Object>>) payload.get("contents");
//
//		if (uiContents == null) {
//			dto.setStatusCode(1);
//			dto.setMessage("UI contents missing!");
//			return dto;
//		}
//
//		Map<String, Object> systemMessage = Map.of("role", googleConfig.getConfig().getSystemInstruction().getRole(),
//				"parts", googleConfig.getConfig().getSystemInstruction().getParts());
//
//		Map<String, Object> finalRequestBody = new HashMap<>();
//		finalRequestBody.put("system_instruction", systemMessage);
//		finalRequestBody.put("contents", uiContents);
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("X-Goog-Api-Key", googleConfig.getApi().getKey());
//
//		HttpEntity<Object> request = new HttpEntity<>(finalRequestBody, headers);
//
//		try {
//			ResponseEntity<String> response = restTemplate.postForEntity(googleConfig.getApi().getUrl(), request,
//					String.class);
//
//			Object jsonResponse = objectMapper.readValue(response.getBody(), Object.class);
//
//			dto.setStatusCode(0);
//			dto.setResponseContent(jsonResponse);
//			return dto;
//
//		} catch (Exception e) {
//			log.error("API Error: ", e);
//			dto.setStatusCode(1);
//			dto.setErrorDescription(e.getMessage());
//			return dto;
//		}
//	}

}
