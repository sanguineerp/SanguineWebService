package com.apos.dao;

import java.util.Map;

import com.apos.model.clsCustomerAreaMasterModel;

public interface inftCustomerAreaMasterDao  {
	
	public clsCustomerAreaMasterModel funLoadCustomerAreaMasterData(String sql,Map<String,String> hmParameters) throws Exception;

	public String funGetAllCustomerAreaForMaster(String clientCode);
}
