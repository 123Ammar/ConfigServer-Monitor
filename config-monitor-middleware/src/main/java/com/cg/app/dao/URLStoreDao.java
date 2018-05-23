package com.cg.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cg.app.model.URLStore;

@Repository
public interface URLStoreDao extends JpaRepository<URLStore, String> {
	
	public URLStore findByAppNameAndEnv(String appName,String env);
	
	
}
