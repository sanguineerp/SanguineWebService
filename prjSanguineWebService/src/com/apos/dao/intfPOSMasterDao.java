package com.apos.dao;

import java.util.Map;

import com.apos.model.clsPOSMasterModel;

public interface intfPOSMasterDao {
	public clsPOSMasterModel funGetPOSMasterData(String sql,Map<String,String> hmParameters) throws Exception;
	
	
	public clsPOSMasterModel funGetPOSNameData(String strPOSName);
	
	public String funGetPOSName(String PosCode);

	
}
