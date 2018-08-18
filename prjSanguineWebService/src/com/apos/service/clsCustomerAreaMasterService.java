package com.apos.service;

import javax.persistence.NamedQuery;

import org.codehaus.jettison.json.JSONObject;

public interface clsCustomerAreaMasterService {
	
	public String funSaveCustomerAreaMaster(JSONObject jObjCustomerAreaMaster);
	
	public JSONObject funLoadCustomerAreaMasterData(String searchCode,String clientCode);
	
	public String funGetAllCustomerAreaForMaster(String clientCode);

}
