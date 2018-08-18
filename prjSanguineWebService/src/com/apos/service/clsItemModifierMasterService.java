package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsItemModifierMasterService {
	public JSONObject funLoadModifierGroupMaster(String clientCode) throws Exception;
	
	public String funSaveItemModifierMaster(JSONObject jObjItemModifierMaster);

	public JSONObject funLoadItemPricing(String MenuCode) throws Exception;
	
	public JSONObject funGetItemModifierMasterData(String modCode,String clientCode) throws Exception;
}
