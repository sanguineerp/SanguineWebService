package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsReprintDao;
import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsAreaMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;


@Service("clsReprintService")
public class clsReprintService
{
	@Autowired
	clsReprintDao	objclsReprintDao;

	public JSONObject funExecute(String posCode,String operationType,String kotFor)
	{
		return objclsReprintDao.funExecute(posCode,operationType,kotFor);
	}

	public void funViewButtonPressed(String code,String transactionType,String kotFor,String posCode,String clientCode,String posName,String webStockUserCode)
	{
		 objclsReprintDao.funViewButtonPressed(code,transactionType,kotFor,posCode,clientCode,posName,webStockUserCode);
	}


}