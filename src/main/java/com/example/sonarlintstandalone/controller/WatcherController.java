package com.example.sonarlintstandalone.controller;

import com.example.sonarlintstandalone.controller.params.DirectoryParams;
import com.example.sonarlintstandalone.controller.response.IssueResponse;
import com.example.sonarlintstandalone.controller.params.IssueParams;
import com.example.sonarlintstandalone.watcher.WatchDir;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcherController {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private WatchDir watchDir;
	@Autowired
	private SimpMessagingTemplate simp;

	@PostMapping("/watch")
	public void watch(@RequestBody DirectoryParams directoryPath) throws Exception {
		try {
			var dir = Paths.get(directoryPath.getDirectory());
			watchDir = new WatchDir(dir, true, this);
			executor.submit(() -> watchDir.processEvents());
		} catch (NoSuchFileException e) {
			System.out.println("Directory " + directoryPath + " does not exist");
			throw e;
		}
	}

	@PostMapping("/unwatch")
	public void unwatch() {
		executor.shutdownNow();
	}

	@PostMapping("/issues")
	public void retrieveIssue(@RequestBody String issue) {
		System.out.println("yeee");
		simp.convertAndSend("/topic/issue", new IssueParams(issue));

	}

	@MessageMapping("/issue")
	@SendTo("/topic/issue")
	public IssueResponse processIssue(IssueParams message) {
		System.out.println("wouhou");
		return new IssueResponse("Update found on file: " + message.getMessage());
	}

}
