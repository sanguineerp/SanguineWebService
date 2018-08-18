
package com.apos.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsKDSForKOTBookAndProcessDao;

	@Service("clsKDSForKOTBookAndProcessService")
	public class clsKDSForKOTBookAndProcessService {

		@Autowired
		clsKDSForKOTBookAndProcessDao	objKDSBookAndProcessDao;

		public JSONObject funGetKOTHdDtl()
		{
			return objKDSBookAndProcessDao.funGetKOTHdDtl();
		}

	public JSONObject funGetNewKOTSize()
	{
		return objKDSBookAndProcessDao.funGetNewKOTSize();
	}

	

public void funKOTOrderProcess(JSONObject jObj)
{
	
	try
	{

		JSONArray listOfBillsToBeProcess = (JSONArray) jObj.get("listOfKOTsToBeProcess");
		String userCode = jObj.getString("userCode");
		
		objKDSBookAndProcessDao.funKOTOrderProcess(listOfBillsToBeProcess, userCode);
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}

	
}
	
}
