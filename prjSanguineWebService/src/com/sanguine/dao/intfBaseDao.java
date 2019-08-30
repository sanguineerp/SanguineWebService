package com.sanguine.dao;

import java.io.Serializable;
import java.util.List;

import com.sanguine.model.clsBaseModel;

public interface intfBaseDao {

	
	public String funSave(clsBaseModel objBaseModel);
	
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key);
	
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode);
	
	public List funGetSerachList(String query,String clientCode) throws Exception;
	
	public List funGetList(StringBuilder query,String queryType) throws Exception;
	
	public List funGetList(String query,String queryType) throws Exception;
}

