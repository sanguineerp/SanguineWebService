package com.apos.dao;

import java.util.Map;

import com.apos.model.clsCustomerMasterModel;



public interface inftCustomerMasterDao {

	public clsCustomerMasterModel funLoadCustomeMasterData(String sql,Map<String,String> hmParameters) throws Exception;
	
	public String funGetAllCountryForMaster(String clientCode);
	
	public String funGetAllStateForMaster(String clientCode);
	
	public String funGetAllCityForMaster(String clientCode);
	
}
