package com.apos.service;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.sanguine.model.clsBaseModel;

public interface clsFactoryMasterService {

	
	public String funSaveUpdateFactoryMaster(JSONObject jObjFactoryMaster);
	
	public JSONObject funGetSelectedFactoryMasterData(String factoryCode,String clientCode);
}
