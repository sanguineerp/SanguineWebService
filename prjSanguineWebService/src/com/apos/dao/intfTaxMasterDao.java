package com.apos.dao;

import java.util.List;
import java.util.Map;

import com.apos.model.clsTaxMasterModel;

public interface intfTaxMasterDao {

	public clsTaxMasterModel funGetTaxMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
