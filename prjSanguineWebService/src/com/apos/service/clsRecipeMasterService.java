package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsRecipeMasterService {

	public String funAddUpdateRecipeMaster(JSONObject jObjAreaMaster);
	
	public JSONObject funSelectedRecipeMasterData(String recipeCode,String clientCode);
}
