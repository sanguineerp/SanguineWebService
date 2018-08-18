package com.apos.service;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.sanguine.model.clsBaseModel;

public interface clsDebitCardMasterService {

	
	public String funAddUpdatePOSDebitCardMaster(JSONObject jObjDebitCardMaster);
	
	public JSONObject funGetSelectedDebitCardMasterData(String cardTypeCode,String clientCode);
	
	public String funGetAllPOSForMaster(String clientCode);
}
