package com.cg.app.service;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.cg.app.constants.MonitorConstant;

@Service
public class MiddlewareServiceImpl implements MiddlewareService {

	public static final String BLANK = "";

	@Value("${refresh.endPoint:/refresh}")
	public String REFRESH_END_POINT;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private CloudFoundryOperations cfOps;

	@Autowired
	private Logger logger;

	@Override
	public void processPayload(String payload) {

		String modifiedFile = BLANK;
		String branchName = BLANK;

		JSONObject payloadObj = new JSONObject(payload);
		JSONArray commitTag = payloadObj.getJSONArray("commits");

		branchName = payloadObj.get("ref").toString().replaceAll("refs/heads/", "");
		String commit = commitTag.toString().substring(1, commitTag.toString().length() - 1);

		JSONObject commitObj = new JSONObject(commit);
		JSONArray modifiedTag = commitObj.getJSONArray("modified");
		if (null != modifiedTag) {
			modifiedFile = modifiedTag.toString().substring(1, modifiedTag.toString().length() - 1)
					.replaceAll(".yml", "").replaceAll("\"", "");
		}
		if (!"".equalsIgnoreCase(branchName) && !"".equalsIgnoreCase(modifiedFile)) {
			logger.info("Branch Name: " + branchName + " Modifed File: " + modifiedFile);

			switch (branchName) {
			case MonitorConstant.DEVELOP:
				refreshApplicationConfig(modifiedFile, branchName);
				break;
			case MonitorConstant.STAGE:
				refreshApplicationConfig(modifiedFile, branchName);
				break;
			case MonitorConstant.QA:
				refreshApplicationConfig(modifiedFile, branchName);
				break;
			default:
				break;
			}
		}
	}

	public void refreshApplicationConfig(String modifiedFile, String branchName) {

		logger.info("Refreshing application of " + modifiedFile + " file In the  " + branchName + " branch");

		cfOps.applications().list().subscribe(application -> {
			String applicationURLStr = BLANK;
			if (application.getName().equals(modifiedFile)) {
				applicationURLStr = application.getUrls().get(0);
				applicationURLStr = "http://" + applicationURLStr + REFRESH_END_POINT;
				System.out.println("App URL : " + applicationURLStr);
				Map<String, String> params = new HashMap<>();
				params.put("data", "Posting blank data");
				try {
					String response = restTemplate.postForObject(applicationURLStr, params, String.class);
					logger.info("Response Message : " + response);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
