package com.apos.dao;

import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import com.apos.model.clsReasonMasterModel;



public interface inftReasonMasterDao {

	public clsReasonMasterModel funLoadReasoneMasterData(String sql,Map<String,String> hmParameters) throws Exception;
	public JSONObject funLoadAllReasonMasterData(String clientCode);
}
