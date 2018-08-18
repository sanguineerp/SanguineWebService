package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsTableMasterService {

	public String funAddUpdateTableMaster(JSONObject jObjTableMaster);
	
	public JSONObject funSelectedTableMasterData(String tableNo,String clientCode);
	
	public JSONObject funGetTableDtl(String clientCode);
	
	public JSONObject funGetTableList(String posCode,String clientCode);
	
	public String funSaveTableSequence(JSONObject jObjTableMaster);
}
