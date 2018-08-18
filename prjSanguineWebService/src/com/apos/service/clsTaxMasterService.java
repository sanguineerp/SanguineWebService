package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsTaxMasterService {

	public String funGetAllTaxForMaster(String clientCode);
	
	public String funAddUpdateTaxMaster(JSONObject jObjTaxMaster);
	
	public JSONObject funSelectedTaxMasterData(String taxCode,String clientCode);
	
	
	
}
