package com.apos.service;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsCocktailWorldInterfaceDao;

@Service("clsCocktailWorldInterfaceService")
public class clsCocktailWorldInterfaceService
{
	@Autowired
	clsCocktailWorldInterfaceDao	objCocktailWorldInterfaceDao;

	
	public void funSaveCocktailWorldInterface(JSONObject jObj)
	{
		 objCocktailWorldInterfaceDao.funSaveCocktailWorldInterface(jObj);
	}

//	public JSONObject funGenerateSaleDataFile(String fromDate,String POSCode) throws IOException, JSONException
//	{
//		 return objCocktailWorldInterfaceDao.funGenerateSaleDataFile(fromDate,POSCode);
//	}
//	public JSONObject funGenerateMenuItemCodeFile()
//	{
//		 return objCocktailWorldInterfaceDao.funGenerateMenuItemCodeFile();
//	}

}
