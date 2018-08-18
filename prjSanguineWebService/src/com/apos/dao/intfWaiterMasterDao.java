package com.apos.dao;

import java.util.Map;

import com.apos.model.clsWaiterMasterModel;

public interface intfWaiterMasterDao {
	public clsWaiterMasterModel funGetWaiterMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
