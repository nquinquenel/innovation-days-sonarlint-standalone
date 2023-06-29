package com.example.sonarlintstandalone.controller;

import com.example.sonarlintstandalone.controller.params.IssueParams;
import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcherController {

  private Process process;
  private final Map<String, List<IssueParams>> issuesList;

  public WatcherController() {
    this.issuesList = new HashMap<>();
  }

  @Autowired
  private SimpMessagingTemplate simp;

  @PostMapping("/issues")
  public void retrieveIssue(@RequestBody Map<String, List<IssueParams>> message) {
    System.out.println("Issue received: " + message);
    var fileDisplayed = "";

    // Remove all issues for the files from parameters (to refresh them), keep others
    for (var file : message.keySet()) {
      fileDisplayed = file;
      var decodedFile = URLDecoder.decode(file, StandardCharsets.UTF_8);
      issuesList.remove(decodedFile);
    }

     for (var entry : message.entrySet()) {
       if (!entry.getValue().isEmpty()) {
         var decodedFile = URLDecoder.decode(entry.getKey(), StandardCharsets.UTF_8);
         var listForFile = issuesList.computeIfAbsent(decodedFile, k -> new ArrayList<>());
         listForFile.addAll(entry.getValue());
       }
    }

    simp.convertAndSend("/topic/issue", issuesList);

    var sum = message.values().stream().mapToInt(List::size).sum();
    if (sum > 0) {
      Toast.toast(ToastType.INFO, "SonarLint", sum + " new issues were found in file '" + URLDecoder.decode(fileDisplayed, StandardCharsets.UTF_8) + "'!");
    }
  }

  @PostMapping("/disconnect")
  public void disconnectServer() {
    if (process != null) {
      process.destroyForcibly();
      System.out.println("Process killed");
    }
  }

  @PostMapping("/connect")
  public void launchServer(@RequestBody String directoryPath) throws IOException {
    if (process != null) {
      process.destroyForcibly();
      System.out.println("Process killed");
    }

    try {
			process = new ProcessBuilder(new String[] {"/bin/bash", "-c","/usr/bin/java -jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-standalone/src/main/resources/slls.jar -workspace " + directoryPath + " -stdio -analyzers /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonargo.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonarjava.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonarjs.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonarphp.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonarpython.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonarhtml.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonarxml.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonartext.jar /home/nicolas.quinquenel/Repositories/innovation-days/sonarlint-vscode/analyzers/sonariac.jar"}).start();

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

      BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

      // Read the output from the command
      System.out.println("Here is the standard output of the command:\n");
      String s;
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }

			// Read any errors from the attempted command
      System.out.println("Here is the standard error of the command (if any):\n");
      while ((s = stdError.readLine()) != null) {
        System.out.println(s);
      }
    } finally {
      if (process != null) {
        process.destroyForcibly();
      }
    }

    System.out.println("Process " + process.pid() + " started");
  }

}
