package com.example.sonarlintstandalone.controller.response;

public class IssueResponse {

	private String content;

	public IssueResponse() {
	}

	public IssueResponse(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
