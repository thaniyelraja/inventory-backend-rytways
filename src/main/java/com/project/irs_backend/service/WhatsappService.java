package com.project.irs_backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappService {

	@Value("${whatsapp.phone-number-id}")
	private String phoneNumberId;

	@Value("${whatsapp.access-token}")
	private String accessToken;

	public void sendMessage(String phoneNumber) {

		String url = "https://graph.facebook.com/v25.0/" + phoneNumberId + "/messages";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of("messaging_product", "whatsapp", "to", phoneNumber, "type", "template",
				"template",
				Map.of("name", "jaspers_market_order_confirmation_v1", "language", Map.of("code", "en_US"),
						"components",
						List.of(Map.of("type", "body", "parameters",
								List.of(Map.of("type", "text", "text", "Prakash K"),
										Map.of("type", "text", "text", "123456"),
										Map.of("type", "text", "text", "Sep 21, 2026"))))));

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		System.out.println("WhatsApp API Response: " + response.getBody());
	}
}