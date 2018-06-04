package com.cg.app.service;

import java.util.List;

public interface MiddlewareService {

	public void processPayload(List<String> modifiedFileList, String branchName);
}
