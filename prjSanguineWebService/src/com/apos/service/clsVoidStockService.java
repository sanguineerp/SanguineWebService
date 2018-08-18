package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsVoidStockDao;

@Service("clsVoidStockService")
public class clsVoidStockService {
	
	@Autowired 
	clsVoidStockDao objVoidStockDao;
	
	public JSONObject  funLoadStockList(String posCode,String transType)
	{
		return objVoidStockDao.funLoadStockList(posCode,transType);
	}
	
	
	
	public JSONObject funLoadStockDtlData(String code,String transType)
	{
		return objVoidStockDao.funLoadStockDtlData(code,transType);
	}
	
	public void funVoidStockIn(String voidResaonCode,String transType,String stockCode,String userCode)
	{
		 objVoidStockDao.funVoidStockIn(voidResaonCode, transType, stockCode, userCode);
	}
}
