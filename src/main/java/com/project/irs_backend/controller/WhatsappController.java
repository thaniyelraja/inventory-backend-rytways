package com.project.irs_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.service.WhatsappService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WhatsappController {

	private final WhatsappService whatsappService;

	@GetMapping("/whatsapp/test")
	public String testWhatsapp(@RequestParam String phoneNumber) {
		whatsappService.sendMessage(phoneNumber);

		return "Whatsapp message sent";
	}

}
