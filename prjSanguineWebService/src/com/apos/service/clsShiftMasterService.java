package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsShiftMasterService {
	
	public String funSaveShiftMaster(JSONObject jObjAreaMaster); 

	public JSONObject funLoadShiftMasterData(String searchCode,String clientCode);
}
