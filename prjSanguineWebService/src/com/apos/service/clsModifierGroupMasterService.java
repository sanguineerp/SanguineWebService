package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsModifierGroupMasterService {
	public String funSaveModifierGroupMaster(JSONObject jObjModifierGroupMaster);
	public JSONObject funGetModifierGroupMasterData(String modCode,String clientCode) throws Exception;
}
