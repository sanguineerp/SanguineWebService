package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apos.dao.clsDayEndProcessDao;

@Service("clsDayEndProcessService")
@Transactional(value = "webPOSTransactionManager")
public class clsDayEndProcessService {
	@Autowired
	clsDayEndProcessDao objDayEndProcessDao;
	
	public JSONObject funDayEndProcessGetUIData(JSONObject jObj)
	{
		return objDayEndProcessDao.funDayEndProcessGetUIData(jObj);
	}
	
	public JSONObject funGetAllParameterValuesPOSWise(String clientCode, String posCode)
	{
		return objDayEndProcessDao.funGetAllParameterValuesPOSWise(clientCode,posCode);
	}
	
	public JSONObject funShiftStartProcess(String posCode, String shiftNo)
	{
		return objDayEndProcessDao.funShiftStartProcess(posCode,shiftNo);
	}
	
	public JSONObject funDayEndProcess(String posCode, String shiftNo,String strUserCode,String POSDate,String strClientCode,String EmailRepot)
	{
		return objDayEndProcessDao.funDayEndProcess(posCode,shiftNo,strUserCode,POSDate,strClientCode,EmailRepot);
	}
	
	public JSONObject funLoadAllReportsName(String strPOSCode,String strClientCode)
	{
		return objDayEndProcessDao.funLoadAllReportsName(strPOSCode,strClientCode);
	}
	
	public JSONObject funSendDayEndMailReports(JSONObject jsonData)
	{
		return objDayEndProcessDao.funSendDayEndMailReports(jsonData);
	}
}
