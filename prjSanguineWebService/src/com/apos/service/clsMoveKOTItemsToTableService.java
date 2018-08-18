package com.apos.service;

import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMoveKOTItemsToTableDao;

@Service("clsMoveKOTItemsToTableService")
public class clsMoveKOTItemsToTableService {

	@Autowired
	clsMoveKOTItemsToTableDao	objMoveKOTItemDao;

	public JSONObject funGetBusyTableDtl(String loginPosCode)
	{
		return objMoveKOTItemDao.funGetBusyTableDtl(loginPosCode);
	}

public JSONObject funGetOpenKOTDtl(String tableNo, String loginPosCode)
{
	return objMoveKOTItemDao.funGetOpenKOTDtl(tableNo,loginPosCode);
}

public JSONObject funGetKOTItemsDtl(String KOTNo,String tableNo, String loginPosCode)
{
	return objMoveKOTItemDao.funGetKOTItemsDtl(KOTNo,tableNo,loginPosCode);
}


public void funSaveMoveKOT(JSONObject jObj)
{
	
	try
	{

		String clientCode = jObj.getString("clientCode");
		String posDate = jObj.getString("posDate");
		String userCode = jObj.getString("userCode");
		String loginPosCode=jObj.getString("loginPosCode");
		String busyTblNo = jObj.getString("busyTblNo");
		String tableNo = jObj.getString("tableNo");
		JSONArray listItemDtl = (JSONArray)jObj.get("listItemDtl");
		JSONObject arrKOTNo=(JSONObject)jObj.get("arrKOTNo");
		objMoveKOTItemDao.funSaveMoveKOTItemsToTable(loginPosCode, userCode, posDate, clientCode, busyTblNo, tableNo, listItemDtl);
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}

}
}