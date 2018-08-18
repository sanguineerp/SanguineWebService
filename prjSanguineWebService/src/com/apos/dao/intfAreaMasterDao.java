package com.apos.dao;

import java.util.Map;

import com.apos.model.clsAreaMasterModel;

public interface intfAreaMasterDao {

	public clsAreaMasterModel funGetAreaMasterData(String sql,Map<String,String> hmParameters) throws Exception;	
}
