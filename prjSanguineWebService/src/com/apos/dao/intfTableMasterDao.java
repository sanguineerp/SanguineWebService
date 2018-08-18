package com.apos.dao;

import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import com.apos.model.clsTableMasterModel;

public interface intfTableMasterDao {

	public JSONObject funGetTableDtl(String clientCode);
	
	public clsTableMasterModel funGetTableMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
