package com.apos.service;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface clsCustomerTypeMasterService {
	
	public String funSaveCustomerTypeMaster(JSONObject jObjCustomerTypeMaster); 
	
	public JSONObject funGetAllCustomerType(String clientCode)  throws Exception ;
	
	public JSONObject funLoadCustomerTypeMasterData(String searchCode,String clientCode);
	
	public String funFillCustTypeCombo(String clientCode);


}
