package com.apos.dao;

import java.util.Map;

import com.apos.model.clsPOSPromationMasterHdModel;

public interface intfPromotionMasterDao {
	public clsPOSPromationMasterHdModel funGetPromotionMasterData(String sql,Map<String,String> hmParameters) throws Exception;

	public boolean funCheckDuplicateBuyPromoItem(String promoItemCode,String promoCode,String posCode,String areaCode) throws Exception;
}
