package com.apos.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsKDSBookAndProcessDao;

	@Service("clsKDSBookAndProcessService")
	public class clsKDSBookAndProcessService {

		@Autowired
		clsKDSBookAndProcessDao	objKDSBookAndProcessDao;

		public JSONObject funGetBillDtl()
		{
			return objKDSBookAndProcessDao.funGetBillDtl();
		}

	public JSONObject funGetNewBillSize()
	{
		return objKDSBookAndProcessDao.funGetNewBillSize();
	}

	

public void funBillOrderProcess(JSONObject jObj)
{
	
	try
	{

		JSONArray listOfBillsToBeProcess = (JSONArray) jObj.get("listOfBillsToBeProcess");
		String userCode = jObj.getString("userCode");
		
		objKDSBookAndProcessDao.funBillOrderProcess(listOfBillsToBeProcess, userCode);
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}

	
}
	
}
