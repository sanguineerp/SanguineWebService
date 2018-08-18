package com.apos.dao;

import java.util.Map;

import com.apos.model.clsRecipeMasterModel;

public interface intfRecipeMasterDao {

	public clsRecipeMasterModel funGetRecipeMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
