package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface intfPricingMasterService {

	public String funSaveUpdatePricingMaster(JSONObject jObjPricingMaster)throws Exception;
	
	public String funCheckDuplicateItemPricing(JSONObject jObjPricingMaster)throws Exception;
	
	public JSONObject  funGetMenuheadDtlForPromotionMaster(String menuCode)throws Exception;

	public JSONObject funGetMenuItemPricingMaster(String pricingId,String clientCode)throws Exception ;
	
}
