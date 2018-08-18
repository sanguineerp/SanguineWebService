package com.apos.service;

import java.util.ArrayList;

import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsBillSettlementDao;

@Service("clsBillSettlementService")
public class clsBillSettlementService {
	@Autowired
	clsBillSettlementDao objBillSettlementDao; 
	
	public JSONObject funFillUnsettleBill(String clientCode,String posCode,String posDate)
	{
		return objBillSettlementDao.funFillUnsettleBill(clientCode,posCode,posDate);
	}
	
	public JSONObject funRowSelected(int rowCount,String clientCode,String posCode,String billNo,String tableNo,String billType,boolean superUser)
	{
		return objBillSettlementDao.funTableRowClicked(rowCount,clientCode,posCode,billNo,tableNo,billType,superUser);
	}
	public JSONObject funGetSettleButtons(String posCode,String userCode,String clientCode)
	 {
		return objBillSettlementDao.funGetSettleButtons(posCode,userCode,clientCode);
	 }
	 public JSONObject funFillGroupSubGroupList(ArrayList<String> arrListItemCode)
	 {
		 return objBillSettlementDao.funFillGroupSubGroupList(arrListItemCode);
	 }
	 public JSONObject funCheckPointsAgainstCustomer(String clientCode,String posCode,String CRMInterface,String customerMobile,
	    		 String voucherNo,String txtPaidAmt)
	 {
		 return objBillSettlementDao.funCheckPointsAgainstCustomer(clientCode, posCode, CRMInterface, customerMobile,voucherNo,txtPaidAmt);
	 }
	 
	 public JSONObject funGetDebitCardNo(String clientCode,String posCode,String voucherNo,String tableNo)
	 {
		 return objBillSettlementDao.funGetDebitCardNo(clientCode, posCode, voucherNo,tableNo);
	 }
}
