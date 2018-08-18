package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsAdvanceOrderTypeMasterService {
	public String funSaveUpdateAdvOrderMaster(JSONObject jObjAreaMaster);
	
	public JSONObject funSelectedAdvOrderMasterData(String advOrderCode,String clientCode);
}
