package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsDeliveryBoyMasterService {
	
	public JSONObject funSelectedDeliveryBoyMasterData(String promoCode,String clientCode);
	
	public String funAddUpdateDeliveryBoyMaster(JSONObject jObjAreaMaster);
	
	public JSONObject funGetAllDeliveryBoyMaster(String clientCode);
}
