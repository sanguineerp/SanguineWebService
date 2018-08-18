package com.apos.dao;

import java.util.List;
import java.util.Map;

import com.apos.model.clsGroupMasterModel;

public interface intfGroupMasterDao {
	 public clsGroupMasterModel funGetGroupMasterDtl(String sql,Map<String,String> hmParameters) throws Exception;
	 
	 public List<clsGroupMasterModel> funGetAllGroup(String strClientCode);
}
