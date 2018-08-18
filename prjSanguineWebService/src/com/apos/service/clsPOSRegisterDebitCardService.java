package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsPOSRegisterDebitCardService {
	
//public String funSaveZoneMaster(JSONObject jObjAreaMaster);
//
//public JSONObject funLoaddZoneMasterData(String searchCode,String clientCode);
//
//public String funGetAllZoneForMaster(String clientCode);
	public JSONObject funRegisterCard(JSONObject jObjRegisterDebitCardMaster);
	
	public JSONObject funDelistCard(JSONObject jObjRegisterDebitCardMaster);

	public JSONObject funCheckCardString(String cardString,String clientCode) throws Exception;
	
}
