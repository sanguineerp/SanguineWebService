package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;



public interface intfGroupMasterService {

	  public String funSaveGroupMaster(JSONObject jObjGroupMaster)throws Exception;

	  public List funGetAllGroup(String string)throws Exception;

	  public JSONObject funGetGroupMasterDtl(String groupCode, String clientCode) throws Exception;
	  
}
