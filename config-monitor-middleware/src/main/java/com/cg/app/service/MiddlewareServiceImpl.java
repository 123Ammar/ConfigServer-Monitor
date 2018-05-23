package com.cg.app.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cg.app.constants.MonitorConstant;
import com.cg.app.dao.URLStoreDao;
import com.cg.app.model.URLStore;

@Service
public class MiddlewareServiceImpl implements MiddlewareService {

	public static final String BLANK = "";

	@Value("${refresh.endPoint:/refresh}")
	public String REFRESH_END_POINT;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	URLStoreDao urlStoreDao;
	
	@Autowired
	private Logger logger;

	@Override
	public boolean processPayload(String payload) {

		String modifiedFile = BLANK;
		String branchName = BLANK;
		boolean status = false;


		JSONObject payloadObj = new JSONObject(payload);
		JSONArray commitTag = payloadObj.getJSONArray("commits");

		branchName = payloadObj.get("ref").toString().replaceAll("refs/heads/", "");
		String commit = commitTag.toString().substring(1, commitTag.toString().length() - 1);

		JSONObject commitObj = new JSONObject(commit);
		JSONArray modifiedTag = commitObj.getJSONArray("modified");
		if (null != modifiedTag) {
			modifiedFile = modifiedTag.toString().substring(1, modifiedTag.toString().length() - 1).replaceAll(".yml", "").replaceAll("\"", "");
		}
		if (!"".equalsIgnoreCase(branchName) && !"".equalsIgnoreCase(modifiedFile)) {
			logger.info("Branch Name: " + branchName + " Modifed File: " + modifiedFile);

			switch (branchName) {
			case MonitorConstant.DEVELOP:
					status = refreshApplicationConfig(modifiedFile, branchName);
					return status;
			case MonitorConstant.STAGE:
				status = refreshApplicationConfig(modifiedFile, branchName);
				return status;
			case MonitorConstant.QA:
				status = refreshApplicationConfig(modifiedFile, branchName);
				return status;
			default:
				break;
			}
		
		}
		return status;
	}
	
	
	public boolean refreshApplicationConfig(String modifiedFile,String branchName) {
		
		String applicationURL = BLANK;
		
		URLStore urlStore = urlStoreDao.findByAppNameAndEnv(modifiedFile, branchName);		
		logger.info("Refreshing application of " + modifiedFile + " file In the  "+ MonitorConstant.DEVELOP + " branch");
		applicationURL = urlStore.getAppURL();
		if(!"".equalsIgnoreCase(applicationURL)) {
			applicationURL = urlStore.getAppURL() + REFRESH_END_POINT;
			System.out.println("App URL : " + applicationURL);
			Map<String, String> params = new HashMap<>();
			params.put("data", "Posting blank data");
			String response = restTemplate.postForObject(applicationURL, params, String.class);
			logger.info("Response Message : " + response);
			return true;
			
	}
		return false;
	}

}
