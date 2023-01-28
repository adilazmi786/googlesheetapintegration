package com.beginnertechies.googleapi.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.beginnertechies.googleapi.dto.GoogleSheetDTO;
import com.beginnertechies.googleapi.dto.GoogleSheetResponseDTO;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

@Component
public class GoogleApiUtil {

	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens/path";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GoogleApiUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(
						new java.io.File(System.getProperty("user.home"), TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public Map<Object, Object> getDataFromSheet() throws GeneralSecurityException, IOException {
		// Build a new authorized API client service.
		final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
		final String range = "Class Data!A2:E";
		Sheets service = getSheetService();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		Map<Object, Object> storeDataFromGoogleSheet = new HashMap<>();
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			for (List row : values) {
				storeDataFromGoogleSheet.put(row.get(0), row.get(4));
			}
		}
		return storeDataFromGoogleSheet;
	}

	private Sheets getSheetService() throws GeneralSecurityException, IOException {
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		return new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
				.setApplicationName(APPLICATION_NAME).build();
	}

	private Drive getDriveService() throws GeneralSecurityException, IOException {
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		return new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
				.setApplicationName(APPLICATION_NAME).build();
	}

	public GoogleSheetResponseDTO createGoogleSheet(GoogleSheetDTO request)
			throws GeneralSecurityException, IOException {
		Sheets service = getSheetService();
		SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
		spreadsheetProperties.setTitle(request.getSheetName());
		SheetProperties sheetProperties = new SheetProperties();
		sheetProperties.setTitle(request.getSheetName());
		Sheet sheet = new Sheet().setProperties(sheetProperties);
		Spreadsheet spreadsheet = new Spreadsheet().setProperties(spreadsheetProperties)
				.setSheets(Collections.singletonList(sheet));
		Spreadsheet createdResponse = service.spreadsheets().create(spreadsheet).execute();
		final var googleSheetResponseDTO = new GoogleSheetResponseDTO();
		ValueRange valueRange = new ValueRange().setValues(request.getDataToBeUpdated());
		service.spreadsheets().values().update(createdResponse.getSpreadsheetId(), "A1", valueRange)
				.setValueInputOption("RAW").execute();
		googleSheetResponseDTO.setSpreadSheetId(createdResponse.getSpreadsheetId());
		googleSheetResponseDTO.setSpeadSheetUrl(createdResponse.getSpreadsheetUrl());

		Drive driveService = getDriveService();
		request.getEmails().forEach(emailAddress -> {
			Permission permission = new Permission().setType("user").setRole("writer").setEmailAddress(emailAddress);
			try {
				driveService.permissions().create(createdResponse.getSpreadsheetId(), permission)
						.setSendNotificationEmail(true).setEmailMessage("Google Sheet Permission testing");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return googleSheetResponseDTO;
	}
}
