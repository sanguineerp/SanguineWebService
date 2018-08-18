package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMoveTableDao;
import com.apos.dao.clsPhysicalStockDao;

@Service("clsPhysicalStockService")
public class clsPhysicalStockService 
{
	@Autowired
	clsPhysicalStockDao	objPhysicalStockDao;

	public JSONObject funGetItemDetails(String itemCode,String posCode)
	{
		return objPhysicalStockDao.funGetItemDetails(itemCode,posCode);
	}
	
	public JSONObject funGetReasonCode(String ReasonCode,String Type)
	{
		return objPhysicalStockDao.funGetReasonCode(ReasonCode,Type);
	}
	public String funSavePhysicalStockPosting(JSONObject jObj)
	{
		return objPhysicalStockDao.funSavePhysicalStockPosting(jObj);
	}

	public JSONObject funGetItemForExport()
	{
		return objPhysicalStockDao.funGetItemForExport();
	}
	
	public JSONObject funGetItemList(String userCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj=new JSONObject();
		
		jObj = objPhysicalStockDao.funGetItemList(userCode);
		   
		return jObj;
	}
	
	public JSONObject funGetPhysicalStkData(String userCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj=new JSONObject();
		
		jObj = objPhysicalStockDao.funGetPhysicalStkData(userCode);
		   
		return jObj;
	}
	
}
