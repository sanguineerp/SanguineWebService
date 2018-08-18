package com.apos.service;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsSendBulkSmsDao;
import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsAreaMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsSendBulkSmsService")
public class clsSendBulkSmsService
{
	@Autowired
	clsSendBulkSmsDao	objSendBulkSmsDao;

	
	public JSONObject funFillCustomerTable(String custTypeCode,String areaCode,String dobCheck,String txtSms)
	{
		return objSendBulkSmsDao.funFillCustomerTable(custTypeCode,areaCode,dobCheck,txtSms);
	}

	public JSONObject funSendBulkSMS(String txtTestMobileNo,String clientCode,String posCode,String txtSms)
	{
		return objSendBulkSmsDao.funSendBulkSMS(txtTestMobileNo,clientCode,posCode,txtSms);
	}
	
	public JSONObject funSendSMS(JSONObject jObj)
	{
		return objSendBulkSmsDao.funSendSMS(jObj);
	}
}
