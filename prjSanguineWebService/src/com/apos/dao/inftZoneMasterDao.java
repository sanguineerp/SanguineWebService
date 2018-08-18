package com.apos.dao;

import java.util.Map;

import com.apos.model.clsZoneMasterModel;



public interface inftZoneMasterDao {

	public clsZoneMasterModel funLoaddZoneMasterData(String sql,Map<String,String> hmParameters) throws Exception;
	
	public String funGetAllZoneForMaster(String clientCode);
}
