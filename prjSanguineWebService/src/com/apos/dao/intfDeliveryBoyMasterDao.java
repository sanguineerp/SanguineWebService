package com.apos.dao;

import java.util.Map;

import com.apos.model.clsDeliveryBoyMasterModel;

public interface intfDeliveryBoyMasterDao {
	public clsDeliveryBoyMasterModel funGetDeliveryBoyMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
