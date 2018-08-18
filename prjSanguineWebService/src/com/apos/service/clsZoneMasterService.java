package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsZoneMasterService {
	
public String funSaveZoneMaster(JSONObject jObjAreaMaster);

public JSONObject funLoaddZoneMasterData(String searchCode,String clientCode);

public String funGetAllZoneForMaster(String clientCode);

}
