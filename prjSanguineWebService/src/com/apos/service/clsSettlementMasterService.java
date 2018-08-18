package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsSettlementMasterService {

	public String funSaveUpdateSettlementMaster(JSONObject jObjMaster);
	
	public JSONObject funSelectedSettlementMasterData(String orderCode,String clientCode);
	
	public JSONObject funGetSettlementDtl(String clientCode);
}
