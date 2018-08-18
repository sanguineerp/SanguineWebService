package com.apos.service;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface clsCustomerMasterService {

	
	public String funSaveCustomerMaster(JSONObject jObjCustomerMaster);
	
	public JSONObject funLoadCustomeMasterData(String searchCode,String clientCode);

	public JSONObject funGetAllCityForMaster(String clientCode)  throws Exception;
	
	public JSONObject funGetAllStateForMaster(String clientCode)  throws Exception;
	
	public JSONObject funGetAllCountryForMaster(String clientCode) throws Exception ;
	


}
