package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsTDHMasterService {
	
	public String funSaveTDH(JSONObject jObjTDH); 
	
	public JSONObject funLoadTDHMasterData(String searchCode,String clientCode);
	
	public JSONObject funLoadPOSTDHTableData(String searchCode,String clientCode);
	
	public JSONObject funLoadItemList(String searchCode,String clientCode);
	
	public JSONObject funloadPOSAllItemName(String clientCode);

}
