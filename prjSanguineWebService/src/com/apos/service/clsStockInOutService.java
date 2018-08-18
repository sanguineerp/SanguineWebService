package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsStockInOutDao;

@Service("clsStockInService")
public class clsStockInOutService 
{
	@Autowired
	clsStockInOutDao	objStockInDao;
	public String funSaveStockIn(JSONObject jObj)
	{
		return objStockInDao.funSaveStockIn(jObj);
	}
	
	public String funSaveStockOut(JSONObject jObj)
	{
		return objStockInDao.funSaveStockOut(jObj);
	}
	
	public JSONObject funGetStockInData(String userCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj=new JSONObject();
		
		jObj = objStockInDao.funGetStockInData(userCode);
		   
		return jObj;
	}
	
	public JSONObject funGetStockOutData(String userCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj=new JSONObject();
		
		jObj = objStockInDao.funGetStockOutData(userCode);
		   
		return jObj;
	}
	
}
