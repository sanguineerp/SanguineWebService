package com.apos.dao;

import java.util.Map;

import com.apos.model.clsAdvanceOrderMasterModel;

public interface intfAdvanceOrderTypeMasterDao {
	public clsAdvanceOrderMasterModel funGetAdvOrderMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
