package com.example.sonarlintstandalone.controller;

import com.example.sonarlintstandalone.controller.params.IssueParams;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcherController {

	@Autowired
	private SimpMessagingTemplate simp;

	@PostMapping("/issues")
	public void retrieveIssue(@RequestBody List<IssueParams> message) {
		System.out.println("Issue received: " + message);
		simp.convertAndSend("/topic/issue", message);
	}

}
