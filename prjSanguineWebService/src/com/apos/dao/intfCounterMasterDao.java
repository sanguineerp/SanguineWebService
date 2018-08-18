package com.apos.dao;

import java.util.List;
import java.util.Map;

import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsCounterMasterHdModel;
import com.apos.model.clsFactoryMasterHdModel;
import com.apos.model.clsTaxMasterModel;

public interface intfCounterMasterDao {

	public clsCounterMasterHdModel funGetSelectedCounterMasterData(String sql,Map<String,String> hmParameters) throws Exception;
}
