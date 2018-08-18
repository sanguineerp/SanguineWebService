package com.apos.dao;

import java.util.List;
import java.util.Map;

import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsTaxMasterModel;

public interface intfDebitCardMasterDao {

	public clsDebitCardMasterHdModel funGetSelectedDebitCardMasterData(String sql,Map<String,String> hmParameters) throws Exception;
	
	public String funGetAllDebitCardForMaster(String clientCode);
}
