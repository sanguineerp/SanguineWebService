package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsDayEndConsolidateDao;

@Service
public class clsDayEndConsolidateService {

	@Autowired
	clsDayEndConsolidateDao obDayEndConsolidateDao;
	
	public JSONObject funDayEndConsolidateGetUIData(JSONObject jObj)
	{
		return obDayEndConsolidateDao.funDayEndConsolidateGetUIData(jObj);
	}
	
	public JSONObject funShiftStartProcess(String shiftNo)
	{
		return obDayEndConsolidateDao.funShiftStartProcess(shiftNo);
	}
	public JSONObject funConsolidateDayEndProcess(String strPOSCode, String shiftNo,String strUserCode,String POSDate,String strClientCode,String EmailRepot)
	{
		return obDayEndConsolidateDao.funConsolidateDayEndProcess(strPOSCode,shiftNo,strUserCode,POSDate,strClientCode,EmailRepot);
	}
	
}
