package com.cg.app.controller;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cg.app.exception.RetryException;
import com.cg.app.service.MiddlewareService;
import com.cg.app.util.EmailUtil;

@RestController
public class MiddlewareController {

	public static final String BLANK = "";

	@Autowired
	private MiddlewareService middlewareService;

	@Autowired
	private EmailUtil emailUtil;

	String branchName = BLANK;

	List<String> modifiedFileList = new ArrayList<>();

	@GetMapping("/")
	public String startApp() {
		return String.format("Config Monitoring service is started ");
	}

	@PostMapping("/processData")
	@Retryable(maxAttempts = 3, include = { RetryException.class, Exception.class })
	public void getGithubPayload(@RequestBody String payload) {

		JSONObject payloadObj = new JSONObject(payload);
		JSONArray commitTag = payloadObj.getJSONArray("commits");

		branchName = payloadObj.get("ref").toString().replaceAll("refs/heads/", "");
		String commitObjStr = commitTag.toString().substring(1, commitTag.toString().length() - 1);

		JSONObject commitObj = new JSONObject(commitObjStr);
		JSONArray modifiedTag = commitObj.getJSONArray("modified");

		for (int i = 0; i < modifiedTag.length(); i++) {
			modifiedFileList.add(modifiedTag.optString(i));
		}

		middlewareService.processPayload(modifiedFileList, branchName);
	}

	@Recover
	public void recover(RetryException e) {
		System.out.println("RetryException -occured Error Message " + e.getMessage());
		emailUtil.sendEmail("Failed to refresh the config changes for File : " + modifiedFileList.toString()
				+ " in the branch " + branchName);
	}

	@Recover
	public void recover(Exception e) {
		System.out.println("Exception -occured  " + e.getMessage());
		emailUtil.sendEmail("Failed to refresh the config changes for File : " + modifiedFileList.toString()
		+ " in the branch " + branchName);
	}

}
