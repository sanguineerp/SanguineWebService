package com.apos.dao;

import java.util.Map;

import com.apos.model.clsOrderMasterModel;

public interface intfOrderMasterDao {
	public clsOrderMasterModel funGetOrderMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
