package com.cg.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="url_store",schema="nbc_cust")
@Entity
public class URLStore {
	
	@Id
	@Column(name="app_name")
	private String appName;
	
	@Column(name="app_url")
	private String appURL;
	
	@Column(name="env")
	private String env;
	
	public URLStore() {
		super();
	}

	public URLStore(String appName, String appURL, String env) {
		super();
		this.appName = appName;
		this.appURL = appURL;
		this.env = env;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppURL() {
		return appURL;
	}

	public void setAppURL(String appURL) {
		this.appURL = appURL;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	@Override
	public String toString() {
		return "URLStore [appName=" + appName + ", appURL=" + appURL + ", envStatus=" + env + "]";
	}
	
	

	
	
	
}
