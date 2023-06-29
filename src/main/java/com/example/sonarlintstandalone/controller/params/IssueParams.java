package com.example.sonarlintstandalone.controller.params;

public class IssueParams {

	private String fileName;
	private String message;
	private String severity;
	private String code;

	public IssueParams() {}

	public IssueParams(String fileName, String message, String severity, String code) {
		this.fileName = fileName;
		this.message = message;
		this.severity = severity;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
