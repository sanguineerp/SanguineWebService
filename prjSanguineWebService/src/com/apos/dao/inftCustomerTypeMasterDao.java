package com.apos.dao;

import java.util.Map;

import com.apos.model.clsCustomerTypeMasterModel;



public interface inftCustomerTypeMasterDao {

	public clsCustomerTypeMasterModel funLoadCustomerTypeMasterData(String sql,Map<String,String> hmParameters) throws Exception;
	
	 public String funFillCustTypeCombo(String clientCode);
	 

}
