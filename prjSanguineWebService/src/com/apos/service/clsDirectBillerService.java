package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsDirectBillerDao;

@Service("clsDirectBillerService")
public class clsDirectBillerService {

	@Autowired
	clsDirectBillerDao	objDirectBillerDao;
	

	public JSONObject funGetItemPricingDtl(String clientCode,String posDate ,String posCode)
	{
		return objDirectBillerDao.funGetItemPricingDtl(clientCode,posDate,posCode);
	}
	
	public JSONObject funGetCustomerHistory(String custCode,String fromDate,String toDate)
	{
		return objDirectBillerDao.funGetCustomerHistory(custCode, fromDate, toDate);
	}
}
