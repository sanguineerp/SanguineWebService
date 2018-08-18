package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMakeBillDao;

@Service("clsMakeBillService")
public class clsMakeBillService {

	@Autowired
	clsMakeBillDao	objMakeBillDao;

	public JSONObject funLoadTableDtl(String clientCode,String posCode)
	{
		return objMakeBillDao.funLoadTableDtl(clientCode,posCode);
	}
	
	public JSONObject funLoadTableForArea(String areaCode,String posCode)
	{
		return objMakeBillDao.funLoadTableForArea(areaCode,posCode);
	}
	
	public JSONObject funFillItemTableDtl(String tableNo,String posCode)
	{
		return objMakeBillDao.funFillItemTableDtl(tableNo,posCode);
	}
}
