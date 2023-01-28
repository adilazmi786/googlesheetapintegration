package com.beginnertechies.googleapi.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.beginnertechies.googleapi.dto.GoogleSheetDTO;
import com.beginnertechies.googleapi.dto.GoogleSheetResponseDTO;
import com.beginnertechies.googleapi.service.GoogleApiService;

@RestController
public class DashboardController {

	@Autowired
	private GoogleApiService googleApiService;

	@GetMapping("/getData")
	public Map<Object, Object> readDataFromGoogleSheet() throws GeneralSecurityException, IOException {
		return googleApiService.readDataFromGoogleSheet();
	}

	@PostMapping("/createSheet")
	public GoogleSheetResponseDTO createGoogleSheet(@RequestBody GoogleSheetDTO request)
			throws GeneralSecurityException, IOException {
		return googleApiService.createSheet(request);
	}
}
