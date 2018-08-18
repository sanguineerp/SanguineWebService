package com.apos.service;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsAddKOTToBillDao;
import com.apos.dao.clsMoveKOTDao;

@Service("clsAddKOTToBillService")
public class clsAddKOTToBillService {
	
	@Autowired
	clsAddKOTToBillDao	objAddKOTToBillDao;



public JSONObject funGetKOTDtlForAddKOTTOBill(String posCode, String tableName)
{
	return objAddKOTToBillDao.funGetKOTDtlForAddKOTTOBill(posCode,tableName);
}

public JSONObject funGetUnsettleBillListForAddKOTTOBill(String posCode)
{
	return objAddKOTToBillDao.funGetUnsettleBillList(posCode);
}

public String funSaveAddKOTToBill(JSONObject jObj)
{
	return objAddKOTToBillDao.funAddKOTToBill(jObj);
}

}