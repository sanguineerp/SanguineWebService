package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsDayEndWithoutDetailDao;
@Service
public class clsDayEndWithoutDetailServiceImpl implements clsDayEndWithoutDetailService{

	@Autowired
	clsDayEndWithoutDetailDao obDayEndWithoutDetailDao;
		
	@Override
	public JSONObject funStartDayProcessWithoutDetails(String strPOSCode,String shiftNo) 
	{
		
		return obDayEndWithoutDetailDao.funStartDayProcessWithoutDetails(strPOSCode,shiftNo);
	}
	public JSONObject funDayEndProcessWithoutDetails(String strPOSCode, String shiftNo,String strUserCode,String POSDate,String strClientCode)
	{
		return obDayEndWithoutDetailDao.funDayEndProcessWithoutDetails(strPOSCode, shiftNo, strUserCode, POSDate, strClientCode); 
	}
	

}
