package com.apos.dao;

import java.util.List;
import java.util.Map;

import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsFactoryMasterHdModel;
import com.apos.model.clsPOSRegisterDebitCardHdModel;
import com.apos.model.clsZoneMasterModel;



public interface intfRegisterDebitCardMasterDao {

	public clsDebitCardMasterHdModel funRegisterCard(String sql,Map<String,String> hmParameters) throws Exception;
	
	public clsPOSRegisterDebitCardHdModel funDelistCardForUpdate(String sql,Map<String,String> hmParameters) throws Exception;
	
	public long funDelistCardCount(String sql,Map<String,String> hmParameters) throws Exception;
	
	public List funCheckCardString(String sql,Map<String,String> hmParameters) throws Exception;
	
//	public String funGetAllZoneForMaster(String clientCode);
}
