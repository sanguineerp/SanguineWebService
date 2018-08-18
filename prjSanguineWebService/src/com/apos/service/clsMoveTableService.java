package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMoveTableDao;

@Service("clsMoveTableService")
public class clsMoveTableService 
{
	@Autowired
	clsMoveTableDao	objMoveTableDao;

	public JSONObject funGetTableList(String posCode,String tableStatus)
	{
		return objMoveTableDao.funGetTableList(posCode, tableStatus);
	}
	public String funSaveMoveTable(JSONObject jObj)
	{
		return objMoveTableDao.funSaveMoveTable(jObj);
	}

	

}
