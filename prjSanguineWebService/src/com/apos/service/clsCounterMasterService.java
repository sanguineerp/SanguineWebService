package com.apos.service;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.sanguine.model.clsBaseModel;

public interface clsCounterMasterService {

	
	public String funSaveUpdateCounterMaster(JSONObject jObjFactoryMaster);
	
	public JSONObject funGetSelectedCounterMasterData(String counterCode,String clientCode);
}
