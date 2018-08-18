package com.apos.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public interface clsMenuItemMasterService {
	
	   
	public String funSaveItemMaster(JSONObject objItemMaster);
	
	public JSONArray  funGetAllRevenueHead();
	
	public JSONObject funGetMenuItemMasterData(String itemCode,String clientCode) throws Exception;
	
	public JSONObject funFillItemTable(String clientCode);
}


