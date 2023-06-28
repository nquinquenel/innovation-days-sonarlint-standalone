package com.example.sonarlintstandalone.controller.params;

public class IssueParams {

	private String message;

	public IssueParams(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
