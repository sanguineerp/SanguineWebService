package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsCustomerHistoryFlashDao;


@Service("clsCustomerHistoryFlashService")
public class clsCustomerHistoryFlashService
{
	@Autowired
	clsCustomerHistoryFlashDao	objclsCustomerHistoryFlashDao;

	//public JSONObject FunFillAllTables(String posCode,String reportType,String selectedTab,String fromDate,String toDate,String custCode,String webStockUserCode)
	public JSONObject funFillAllTables(JSONObject jObj)
	{
		return objclsCustomerHistoryFlashDao.funFillAllTables(jObj);
	}

	
}