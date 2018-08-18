package com.apos.dao;

import java.util.Map;

import com.apos.model.clsCostCenterMasterModel;

public interface intfCostCenterMasterDao {

	public clsCostCenterMasterModel funGetSelectedCostCenterMasterData(String sql,Map<String,String> hmParameters) throws Exception;
	public String funGetAllCostCentersForMaster(String clientCode);
}
