package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsOrderMasterService {
	public String funAddUpdatePOSOrderMaster(JSONObject jObjAreaMaster);
	
	public JSONObject funSelectedOrderMasterData(String orderCode,String clientCode);
}
