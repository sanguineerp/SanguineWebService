package com.apos.service;

import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONObject;

public interface intfSubGroupMasterService {
	
	public String funSaveSubGroupMaster(JSONObject objSubGroupMaster)throws Exception;

	 public JSONObject funGetAllSubGroupMaster(String clientCode)throws Exception;
	public JSONObject funGetSubGroupMasterData(String subGroupCode,String clientCode) throws Exception;
}
