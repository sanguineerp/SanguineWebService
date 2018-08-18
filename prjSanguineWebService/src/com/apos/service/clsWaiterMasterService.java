package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsWaiterMasterService {
	public JSONObject funGetWaiterList(String clientCode);
	
	public String funAddUpdateWaiterMaster(JSONObject objWaiterMaster);
	
	public JSONObject funSelectedWaiterMasterData(String waiterNo,String clientCode);
}
