package com.apos.dao;

import java.util.List;
import java.util.Map;

import com.apos.model.clsPOSTDHModel;
import com.apos.model.clsPricingMasterHdModel;



public interface inftTDHMasterDao {
	
	public clsPOSTDHModel funLoadTDHMasterData(String sql,Map<String,String> hmParameters) throws Exception; 
	
	public clsPricingMasterHdModel funLoadPOSTDHTableData(String sql,Map<String,String> hmParameters) throws Exception;
	
	public List funLoadItemList(String sql,Map<String,String> hmParameters) throws Exception;

	public List funLoadALLItemNameList(String sql,Map<String, String> hmParameters)throws Exception;

	

}
