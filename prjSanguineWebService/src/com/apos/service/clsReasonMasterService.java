package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsReasonMasterService { 
	
    public String funSaveUpdateReasonMaster(JSONObject jObjReasonMaster);
    
    public JSONObject funGetAllReasonMaster(String clientCode) throws Exception;
    
    public JSONObject funLoadReasoneMasterData(String searchCode,String clientCode);

    public JSONObject funLoadAllReasonMasterData(String clientCode);
	
}
