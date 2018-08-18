package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsImportDatabaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service(value = "clsImportDatabaseService")
public class clsImportDatabaseService {
	
	@Autowired
	private clsImportDatabaseDao objImportDatabaseDao;
	
	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	
	public boolean funImportDatabase(JSONObject jObj)
	{
		try{
		clsImportDatabaseDao.clientCode = jObj.getString("ClientCode");
		clsImportDatabaseDao.dbURL = jObj.getString("dbURL");
		clsImportDatabaseDao.userCode= jObj.getString("User");
		clsImportDatabaseDao.posCode=jObj.getString("posCode");
		clsImportDatabaseDao.strUserName=jObj.getString("strUserName");
		clsImportDatabaseDao.strPassword=jObj.getString("strPassword");
		clsImportDatabaseDao.dateTime = objUtilityFunctions.funGetCurrentDateTime("yyyy-MM-dd");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return objImportDatabaseDao.funImportDatabase();
	}
}
