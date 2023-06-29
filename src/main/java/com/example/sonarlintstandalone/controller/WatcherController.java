package com.example.sonarlintstandalone.controller;

import com.example.sonarlintstandalone.controller.response.IssueResponse;
import com.example.sonarlintstandalone.controller.params.IssueParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcherController {

	@Autowired
	private SimpMessagingTemplate simp;

	@PostMapping("/issues")
	public void retrieveIssue(@RequestBody IssueParams message) {
		System.out.println("Issue received: " + message);
		simp.convertAndSend("/topic/issue", message);

	}

	@MessageMapping("/issue")
	@SendTo("/topic/issue")
	public IssueResponse processIssue(IssueParams message) {
		System.out.println("Issue processed: " + message.getMessage().toString());
		return new IssueResponse("Update found on file: " + message.getMessage());
	}

}
