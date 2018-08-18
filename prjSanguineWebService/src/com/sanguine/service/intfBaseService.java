package com.sanguine.service;

import java.io.Serializable;
import java.util.List;

import com.sanguine.model.clsBaseModel;

public interface intfBaseService {

	public String funSave(clsBaseModel objBaseModel) throws Exception;
	
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key) throws Exception;
	
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode) throws Exception;
	
	public List funGetSerachList(String query,String clientCode) throws Exception;
	
	public List funGetList(StringBuilder query,String queryType) throws Exception;

}
