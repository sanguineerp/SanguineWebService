package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsPromotionMasterService {
	public String funAddUpdatePromotionMaster(JSONObject jObjTaxMaster);
	
	public JSONObject funSelectedPromotionMasterData(String taxCode,String clientCode);
	
	public JSONObject funCheckDuplicateBuyPromoItem(String promoItemCode,String strPromoCode,String posCode,String areaCode) throws Exception;
}
