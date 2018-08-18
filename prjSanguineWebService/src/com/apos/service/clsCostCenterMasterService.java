package com.apos.service;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.sanguine.model.clsBaseModel;

public interface clsCostCenterMasterService {

	
	public String funSaveUpdateCostCentersMaster(JSONObject jObjCostCenterMaster);
	
	public JSONObject funGetSelectedCostCenterMasterData(String costCenterCode,String clientCode);
	
	public String funGetAllCostCentersForMaster(String clientCode);
}
