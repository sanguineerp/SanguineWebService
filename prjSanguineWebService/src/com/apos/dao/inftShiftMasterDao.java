package com.apos.dao;

import java.util.Map;

import com.apos.model.clsShiftMasterModel;



public interface inftShiftMasterDao {

	public clsShiftMasterModel funLoadShiftMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
