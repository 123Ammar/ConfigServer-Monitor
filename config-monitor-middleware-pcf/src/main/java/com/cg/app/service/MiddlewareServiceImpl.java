package com.cg.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.AbstractUaaTokenProvider;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.cg.app.exception.RetryException;

@Service
public class MiddlewareServiceImpl implements MiddlewareService {

	public static final String BLANK = "";

	@Value("${refresh.endPoint:/refresh}")
	public String REFRESH_END_POINT;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private Logger logger;

	@Value("${cf.space}")
	String SPACE_DETAILS;

	@Value("${cf.organization}")
	String ORG_NAME;

	String spaceName = BLANK;

	@Value("${cf.username}")
	String USER;

	@Value("${cf.password}")
	String PASSWORD;
	
	@Value("${cf.apiHost}")
	String API_HOST;


	CloudFoundryOperations cfops = null;
	

	@Override
	public void processPayload(List<String> modifiedFileList, String branchName) {

		if (!"".equalsIgnoreCase(branchName) && modifiedFileList.size() > 0) {
			logger.info("Branch Name: " + branchName + " Modifed Files: " + modifiedFileList.toString());
				refreshApplicationConfig(modifiedFileList, branchName);
		}
	}

	public void refreshApplicationConfig(List<String> modifiedFileList, String branchName) {

		logger.info("Refreshing application of " + modifiedFileList.toString() + " file In the  [" + branchName + "] branch");

		spaceName = getSpaceDetails(branchName);
		
		if(!StringUtils.isEmpty(spaceName)) {
			getPCFServiceConnection(spaceName);
			
			try {
				  cfops.applications().list().subscribe(application -> { 
						  String applicationURLStr = BLANK; for (int i = 0; i < modifiedFileList.size(); i++)
						  {
							  if (application.getName().equals(modifiedFileList.get(i).replaceAll(".yml",""))) { 
								  applicationURLStr = application.getUrls().get(0);
								  applicationURLStr = "http://" + applicationURLStr + REFRESH_END_POINT; 
									  	Map<String, String>   params = new HashMap<>(); params.put("data", "Posting blank data");
								  		logger.info("Started Posting the data to the URL "+applicationURLStr);
								  		String response = restTemplate.postForObject(applicationURLStr, params, String.class);
								  		logger.info("Application Properties changed : " + response); 
							  }
						  }
				  });
				  
			}catch(Exception e) {
				throw new RetryException(e.getMessage());
			}
		}
	}
	

	public String getSpaceDetails(String branchName) {

		String spaceDetArr[] = SPACE_DETAILS.split(";");

		Map<String, String> mp = new HashMap<>();

		for (int i = 0; i < spaceDetArr.length; i++) {
			String[] space = null;
			space = spaceDetArr[i].split(",");

			for (int j = 0; j < space.length; j++) {
				mp.put(space[j], space[++j]);
			}
		}

		for (Map.Entry<String, String> entry : mp.entrySet()) {

			if (branchName.equalsIgnoreCase(entry.getKey())) {
				return entry.getValue().toString();

			}

		}
		return "";
	}

	public void getPCFServiceConnection(String space) {

		ConnectionContext connection = DefaultConnectionContext.builder()
				.apiHost(API_HOST)
				.build();

		AbstractUaaTokenProvider tokenProvider = createTokenProvider();

		ReactorCloudFoundryClient client = ReactorCloudFoundryClient.builder()
				.connectionContext(connection)
				.tokenProvider(tokenProvider)
				.build();

		UaaClient uaaClient = ReactorUaaClient.builder()
				.connectionContext(connection)
				.tokenProvider(tokenProvider)
				.build();

		DopplerClient doppler = ReactorDopplerClient.builder()
				.connectionContext(connection)
				.tokenProvider(tokenProvider)
				.build();

		cfops = DefaultCloudFoundryOperations.builder()
				.cloudFoundryClient(client)
				.dopplerClient(doppler)
				.uaaClient(uaaClient)
				.organization(ORG_NAME)
				.space(space)
				.build();
		
		
	}

		protected AbstractUaaTokenProvider createTokenProvider() {
				return PasswordGrantTokenProvider.builder()
						.username(USER)
						.password(PASSWORD)
						.build();
		
		
	}

}
