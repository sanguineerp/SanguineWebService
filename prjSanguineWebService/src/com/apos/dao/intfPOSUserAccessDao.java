package com.apos.dao;

import org.codehaus.jettison.json.JSONObject;

public interface intfPOSUserAccessDao {

	public void funDeleteUserAccessDetails(String userCode);
	
	public void funDeleteSuperUserAccessDetails(String userCode);
	
	public JSONObject funGetAllFormDetails(String clientCode);
	
	 public JSONObject funGetUserAccessData(String sql,String userCode) throws Exception;
}
