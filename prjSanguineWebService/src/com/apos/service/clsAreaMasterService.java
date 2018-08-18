package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsAreaMasterService {

	public String funSaveUpdateAreaMaster(JSONObject jObjAreaMaster);
	
	public String funGetAllAreaForMaster(String clientCode);
	
	public JSONObject funSelectedAreaMasterData(String areaCode,String clientCode);
	
	
}
