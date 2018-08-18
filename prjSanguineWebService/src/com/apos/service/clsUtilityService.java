package com.apos.service;


public interface clsUtilityService {

	public String funGetPOSWiseDayEndData(String POSCode,String UserCode);
	
	public boolean funCheckPendingBills(String posCode,String POSDate);
		
	public boolean funCheckTableBusy(String posCode);
	
	public String funGetDBBackUpPath(String clientCode);
	
	public int funGetNextShiftNo(String posCode, int shiftNo,String strClientCode,String strUserCode);
    
	public int funGetNextShiftNoForShiftEnd(String posCode, int shiftNo,String strClientCode,String strUserCode);
    
	public int funShiftEndProcess(String status, String posCode, int shiftNo, String billDate,String strClientCode,String strUserCode);
	
	public int funDayEndflash(String clientCode,String posCode, String billDate, int shiftNo,String strUserCode);
	
	public int funShiftCardBalToRevenueTable(String posCode, String posDate,String strClientCode,String strUserCode) throws Exception;
	
	public int funPostSanguineCMSData(String posCode, String billDate,String ClientCode,String userCode);
	
	public int funPostBillDataToCMS(String posCode, String billDate,String ClientCode,String userCode) throws Exception;
	
	public String funGenerateNextCode();
	
	public int funCalculateDayEndCash(String posDate, int shiftCode, String posCode);
	
	public int funUpdateDayEndFields(String posDate, int shiftNo, String dayEnd, String posCode,String userCode);

	//public void funGenerateLinkupTextfile(ArrayList<ArrayList<String>> arrUnLinkedItemDtl, String fromDate, String toDate, String posName,String gClientName);

	 public boolean funInsertQBillData(String posCode,String clientCode);
	 
	 public void funInvokeHOWebserviceForTrans(String transType, String formName,String clientCode,String POSCode);
	 
	 public void funPostCustomerDataToHOPOS(String clientCode,String POSCode);
	 
	 public void funPostDayEndData(String newStartDate,int shiftCode,String strClientCode,String posCode);
	 
	 
}
