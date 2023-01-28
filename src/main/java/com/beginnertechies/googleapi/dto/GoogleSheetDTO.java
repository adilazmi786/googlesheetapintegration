package com.beginnertechies.googleapi.dto;

import java.util.List;

public class GoogleSheetDTO {

	private String sheetName;

	private List<List<Object>> dataToBeUpdated;

	private List<String> emails;

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<List<Object>> getDataToBeUpdated() {
		return dataToBeUpdated;
	}

	public void setDataToBeUpdated(List<List<Object>> dataToBeUpdated) {
		this.dataToBeUpdated = dataToBeUpdated;
	}

}
