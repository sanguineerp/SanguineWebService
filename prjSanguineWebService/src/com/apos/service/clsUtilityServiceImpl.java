package com.apos.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsUtilityDao;

@Service
public class clsUtilityServiceImpl implements clsUtilityService{

	@Autowired
	clsUtilityDao obUtilityDao;
	public String funGetPOSWiseDayEndData(String POSCode,String UserCode)
	{
		return obUtilityDao.funGetPOSWiseDayEndData(POSCode, UserCode);
	}
	public boolean funCheckPendingBills(String posCode,String POSDate)
	{
		return obUtilityDao.funCheckPendingBills(posCode, POSDate);
	}
	public boolean funCheckTableBusy(String posCode)
	{
		return obUtilityDao.funCheckTableBusy(posCode);
	}
	public String funGetDBBackUpPath(String clientCode)
	{
		return obUtilityDao.funGetDBBackUpPath(clientCode);
	}
	public int funGetNextShiftNo(String posCode, int shiftNo,String strClientCode,String strUserCode)
    {
		return obUtilityDao.funGetNextShiftNo(posCode, shiftNo, strClientCode, strUserCode);
    }
	public int funGetNextShiftNoForShiftEnd(String posCode, int shiftNo,String strClientCode,String strUserCode)
    {
		return obUtilityDao.funGetNextShiftNoForShiftEnd(posCode, shiftNo, strClientCode, strUserCode);
    }
	public int funShiftEndProcess(String status, String posCode, int shiftNo, String billDate,String strClientCode,String strUserCode)
	{
		return obUtilityDao.funShiftEndProcess(status, posCode, shiftNo, billDate, strClientCode, strUserCode);
	}
	public int funDayEndflash(String clientCode,String posCode, String billDate, int shiftNo,String strUserCode)
	{
		return obUtilityDao.funDayEndflash(clientCode, posCode, billDate, shiftNo, strUserCode);
	}
	public int funShiftCardBalToRevenueTable(String posCode, String posDate,String strClientCode,String strUserCode) throws Exception
	{
		return obUtilityDao.funShiftCardBalToRevenueTable(posCode, posDate, strClientCode, strUserCode);
	}
	public int funPostSanguineCMSData(String posCode, String billDate,String ClientCode,String userCode)
	{
		return obUtilityDao.funPostSanguineCMSData(posCode, billDate, ClientCode, userCode);	
	}
	public int funPostBillDataToCMS(String posCode, String billDate,String ClientCode,String userCode) throws Exception
	{
		return obUtilityDao.funPostBillDataToCMS(posCode, billDate, ClientCode, userCode);
	}
	public String funGenerateNextCode()
	{
		return obUtilityDao.funGenerateNextCode();
	}
	public int funCalculateDayEndCash(String posDate, int shiftCode, String posCode)
	{
		return obUtilityDao.funCalculateDayEndCash(posDate, shiftCode, posCode);
	}
	public int funUpdateDayEndFields(String posDate, int shiftNo, String dayEnd, String posCode,String userCode)
	{
		return obUtilityDao.funUpdateDayEndFields(posDate, shiftNo, dayEnd, posCode, userCode);
	}
	/*public void funGenerateLinkupTextfile(ArrayList<ArrayList<String>> arrUnLinkedItemDtl, String fromDate, String toDate, String posName,String gClientName)
	{
		obUtilityDao.funGenerateLinkupTextfile(arrUnLinkedItemDtl, fromDate, toDate, posName, gClientName);
	}*/
	
	 public boolean funInsertQBillData(String posCode,String clientCode)
	 {
		 return obUtilityDao.funInsertQBillData(posCode, clientCode);
	 }
	 public void funInvokeHOWebserviceForTrans(String transType, String formName,String clientCode,String POSCode)
	 {
		 obUtilityDao.funInvokeHOWebserviceForTrans(transType, formName, clientCode, POSCode);
	 }
	 public void funPostCustomerDataToHOPOS(String clientCode,String POSCode)
	 {
		 obUtilityDao.funPostCustomerDataToHOPOS(clientCode, POSCode);
	 }
	 public void funPostDayEndData(String newStartDate,int shiftCode,String strClientCode,String posCode)
	 {
		 obUtilityDao.funPostDayEndData(newStartDate, shiftCode, strClientCode, posCode);
	 }


}
