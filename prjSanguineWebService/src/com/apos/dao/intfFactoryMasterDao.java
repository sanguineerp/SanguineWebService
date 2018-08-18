package com.apos.dao;

import java.util.List;
import java.util.Map;

import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsFactoryMasterHdModel;
import com.apos.model.clsTaxMasterModel;

public interface intfFactoryMasterDao {

	public clsFactoryMasterHdModel funGetSelectedFactoryMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
