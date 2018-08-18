package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsPOSUserAccessService {

	public String funAddUpdatePOSUserAccess(JSONObject jObjUserAccess);
	
	public JSONObject funGetAllFormDetails(String clientCode);
	
	public JSONObject funGetUserAccessData(String userCode) throws Exception;
}
