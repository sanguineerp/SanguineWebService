package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface intfSubMenuHeadMasterService {
	public String funSaveSumMenuMaster(JSONObject jObjSubMenuMaster)throws Exception;
	
	public String funGetAllSubMenuHeadForMaster(String clientCode)throws Exception;



}
