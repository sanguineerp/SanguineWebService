package com.sanguine.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanguine.dao.intfBaseDao;
import com.sanguine.model.clsBaseModel;
@Service("intfBaseService")
public class clsBaseServiceImpl implements intfBaseService{
	
	@Autowired
	intfBaseDao objBaseDao;
	
	@Override
	public String funSave(clsBaseModel objBaseModel) throws Exception
	{
	return objBaseDao.funSave(objBaseModel);
	}
	
	@Override
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key) throws Exception
	{
		return objBaseDao.funLoad(objBaseModel, key);
	}
	
	@Override
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode) throws Exception
	{
		return objBaseDao.funLoadAll(objBaseModel,clientCode);
	}
	
	@Override
	public List funGetSerachList(String query,String clientCode) throws Exception
	{
		return objBaseDao.funGetSerachList(query,clientCode);
	}
	
	public List funGetList(StringBuilder query,String queryType) throws Exception
	{
		return objBaseDao.funGetList(query, queryType);
	}
	
	public List funGetList(String query,String queryType) throws Exception
	{
		return objBaseDao.funGetList(query, queryType);
	}
	
	
	
	
	
	
	
}
