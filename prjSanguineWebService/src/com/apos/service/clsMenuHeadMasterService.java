package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsMenuHeadMasterService {

	public JSONObject funLoadMasterDetails(String masterName, String clientCode) throws Exception;
	
	public String funSaveMenuHeadMaster(JSONObject objMenuHeadMaster)throws Exception;
	
	public String funGetAllMenuHeadForMaster(String clientCode)throws Exception;
	
	public JSONObject funGetMenuHeadMasterData(String menuHeadCode,String clientCode) throws Exception;
	
	public JSONObject funGetSubMenuHeadMasterData(String SubMenuHeadCode,String clientCode) throws Exception;
	
	
}
