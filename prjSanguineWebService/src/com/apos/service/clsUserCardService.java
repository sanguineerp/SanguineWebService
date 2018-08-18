package com.apos.service;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface clsUserCardService {

	public String funSaveUserCard(JSONObject jObjUserCardSwipe);
	
	public String funUpdateUserCardString(JSONObject jObjUserCardSwipe) throws JSONException;
	
	public JSONObject funGetUserCardData(String subGroupCode,String clientCode) throws Exception;
}
