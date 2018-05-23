package com.cg.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cg.app.service.MiddlewareService;

@RestController
public class MiddlewareController {

    @Autowired
    private MiddlewareService middlewareService;
    
    @GetMapping("/")
    public String startApp() {
      return String.format("Config Monitoring service is started ");
    }
    
    @PostMapping("/processData")
    public String getGithubPayload(@RequestBody String payload) {
 
    	boolean refreshStatus = false;
    	
    	refreshStatus = middlewareService.processPayload(payload);
    	
    	if(refreshStatus) {
    		return "Application Successfully Refreshed";
    	}else {
    		return "Application Not Refreshed";
    	}
    	
    }
	
}
