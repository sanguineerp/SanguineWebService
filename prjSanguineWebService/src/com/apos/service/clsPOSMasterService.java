package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

import com.apos.model.clsPOSMasterModel;

public interface clsPOSMasterService {
	public String funAddUpdatePOSMaster(JSONObject jObjPOSMaster);
	
	public clsPOSMasterModel funGetPOSNameData(String strPOSName);
	
	public String funGetAllPOSForMaster(String clientCode);
	
	public JSONObject funSelectedPOSMasterData(String posCode,String clientCode);
	
	public String funGetPrintVatNoPOS(String posCode);
	
	public String funGetVatNoPOS(String posCode);
	
	public String funGetPrintServiceTaxNoPOS(String posCode);
	
	public String funGetServiceTaxNoPOS(String posCode);
	
	public String funGetPOSName(String posCode);
	
}
