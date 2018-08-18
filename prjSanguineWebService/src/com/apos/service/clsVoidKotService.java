package com.apos.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsVoidKotDao;

@Service("clsVoidKotService")
public class clsVoidKotService {

	@Autowired 
	clsVoidKotDao objVoidKotdao;
	
	public JSONObject funLoadTable(String strPOSCode)
	{
		return objVoidKotdao.funLoadTable(strPOSCode);
	}
	
	public JSONObject funLoadReson()
	{
		return objVoidKotdao.funLoadReson();
	}
	public JSONObject funFillHelpGrid(String tableName,String tableNo,String strPosCode)
	{
		return objVoidKotdao.funFillHelpGrid(tableName,tableNo,strPosCode);
	}
	
	public JSONObject fillItemTableData(String KotNo,String tableNo,String strPosCode,String tableName)
	{
		return objVoidKotdao.fillItemTableData(KotNo,tableNo,strPosCode,tableName);
	}
	
	public JSONObject funDoneButtonClick(JSONObject jservice,String reasonCode,String delTableNo,String delKotNo,String remarks,
			String userCode,String clientCode,String voidedDate,double taxAmt)
	{
		return objVoidKotdao.funDoneButtonClick( jservice, reasonCode, delTableNo, delKotNo, remarks,
				 userCode, clientCode, voidedDate, taxAmt);
	}
	
	public JSONObject funClickFullVoidKot(String reasonCode,String Kot,String strClientCode,String voidedDate,String userCode,String voidKOTRemark) throws JSONException
	{
		return objVoidKotdao.funClickFullVoidKot( reasonCode, Kot, strClientCode,voidedDate, userCode, voidKOTRemark);
	}
}
