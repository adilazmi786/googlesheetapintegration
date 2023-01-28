package com.beginnertechies.googleapi.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beginnertechies.googleapi.dto.GoogleSheetDTO;
import com.beginnertechies.googleapi.dto.GoogleSheetResponseDTO;
import com.beginnertechies.googleapi.util.GoogleApiUtil;

@Service
public class GoogleApiService {

	@Autowired
	private GoogleApiUtil googleApiUtil;

	public Map<Object, Object> readDataFromGoogleSheet() throws GeneralSecurityException, IOException {
		return googleApiUtil.getDataFromSheet();
	}

	public GoogleSheetResponseDTO createSheet(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
		return googleApiUtil.createGoogleSheet(request);
	}

}
