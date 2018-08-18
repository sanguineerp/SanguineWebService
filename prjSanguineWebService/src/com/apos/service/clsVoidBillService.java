package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsVoidBillDao;

@Service("clsVoidBillService")
public class clsVoidBillService {
	
	@Autowired 
	clsVoidBillDao objVoidBillDao; 

  public JSONObject funLoadBillGrid(String dtPOSDate,String strPOSCode,String searchBillNo)
  {
	  return objVoidBillDao.funLoadBillGrid(dtPOSDate,strPOSCode,searchBillNo);
  }
  public JSONObject funSelectBill(String billNo,String strClientCode, String strPOSCode,String posDate){
	  
	  return objVoidBillDao.funSelectBill(billNo,strClientCode, strPOSCode,posDate);
  }
  
  public JSONObject funVoidItem(String reasonCode,String delTableNo,String billNo,String remarks,
			String userCode,String clientCode,String posDate,double taxAmt,String itemCode,double quantity,double amount,String itemName,String modItemCode,String strPosCode)
	{
		return objVoidBillDao.funVoidItem(  reasonCode, delTableNo, billNo, remarks,
				 userCode, clientCode, posDate, taxAmt,itemCode,quantity,amount,itemName,modItemCode,strPosCode);
	}
 
  public JSONObject funVoidBill(String posDate,String billNo,String favoritereason,String remark,String userCode,String strPOSCode,String strClientCode)
	{
		return objVoidBillDao.funVoidBill(  posDate, billNo, favoritereason, remark,userCode, strPOSCode, strClientCode);
	}
  
  
}
