package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMoveKOTDao;
import com.apos.model.clsOrderMasterModel;
import com.apos.model.clsOrderMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsMoveKOTService")
public class clsMoveKOTService {
	
	@Autowired
	clsMoveKOTDao	objMoveKOTDao;



public JSONObject funGetOpenKOTDtl(String tableNo, String POSCode, String loginPosCode)
{
	return objMoveKOTDao.funGetOpenKOTDtl(tableNo,POSCode,loginPosCode);
}


public JSONObject funGetTableDtl(String POSCode){
	
	return objMoveKOTDao.funGetTableDtl(POSCode);
}

public void funSaveMoveKOT(JSONObject jObj)
{
	
	try
	{

		String openTableNo = jObj.getString("openTableNo");
		String KOTNo = jObj.getString("KOTNo");
		String tableNo = jObj.getString("TableNo");
		
		objMoveKOTDao.funSaveMoveKOT(KOTNo, tableNo, openTableNo);
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}

	
}


}