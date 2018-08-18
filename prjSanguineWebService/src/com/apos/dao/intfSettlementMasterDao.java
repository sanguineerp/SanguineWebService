package com.apos.dao;

import java.util.Map;

import com.apos.model.clsSettlementMasterModel;

public interface intfSettlementMasterDao {
	
	public clsSettlementMasterModel funGetSettlementMasterData(String sql,Map<String,String> hmParameters) throws Exception;

}
