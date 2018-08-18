package com.apos.dao;

import java.util.Map;

import com.apos.model.clsSubGroupMasterHdModel;

public interface intfSubGroupMasterDao {
	 public clsSubGroupMasterHdModel funGetSubGroupMasterData(String sql,Map<String,String> hmParameters) throws Exception;

}
