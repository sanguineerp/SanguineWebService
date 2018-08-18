
package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsCustomerMasterDao;
import com.apos.dao.clsTableReservationDao;
import com.apos.dao.inftCustomerAreaMasterDao;
import com.apos.model.clsCustomerAreaMasterModel;
import com.apos.model.clsCustomerAreaMasterModel_ID;
import com.apos.model.clsCustomerMasterModel;
import com.apos.model.clsCustomerMasterModel_ID;
import com.apos.model.clsTableReservationModel;
import com.apos.model.clsTableReservationModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;
@Service("clsTableReservationService")
public class clsTableReservationService {
	
	@Autowired
	clsTableReservationDao	objMoveKOTDao;

	@Autowired
	private inftCustomerAreaMasterDao objCustomerAreaMasterDao;
	
	@Autowired
	private clsCustomerMasterService objCustomerMasterService;

	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired
	private intfBaseService objBaseService;

	@Autowired
	clsUtilityFunctions utility;
	
	
	
	
public JSONObject funGetReservationDefault(String date, String loginPosCode)
{
	return objMoveKOTDao.funGetReservationDefault(date,loginPosCode);
}


public JSONObject funGetTableReservationDtl(String fromDate,String toDate, String fromTime, String toTime,  String loginPosCode)
{
	
	return objMoveKOTDao.funGetTableReservationDtl(fromDate, toDate, fromTime, toTime, loginPosCode);
}


public void funCancelTableReservation(String reservationNo, String tableNo)
{
	objMoveKOTDao.funCancelTableReservation(reservationNo, tableNo);;
}

public String funAddUpdateTableReservation(JSONObject jObjTaxMaster){
	String resCode = "";
	try
	{
	    
		resCode = jObjTaxMaster.getString("resCode");
		String contactNo = jObjTaxMaster.getString("ContactNo");
		String custCode = jObjTaxMaster.getString("CustCode");
		String custName = jObjTaxMaster.getString("CustName");
	    int intPax = jObjTaxMaster.getInt("intPax");
	    String strSmokingYN = jObjTaxMaster.getString("strSmokingYN");
	    String strCity = jObjTaxMaster.getString("City");
	    String resDate = jObjTaxMaster.getString("resDate");
	    String resTime = jObjTaxMaster.getString("resTime");
		String customerAreaCode = jObjTaxMaster.getString("BldgCode");
		String bldgName = jObjTaxMaster.getString("BldgName");
	   
	    
	    String strAMPM = jObjTaxMaster.getString("strAMPM");
	    String strInfo = jObjTaxMaster.getString("strInfo");
	    String strTableNo = jObjTaxMaster.getString("strTableNo");
	    String strPOSCode = jObjTaxMaster.getString("POSCode");
	 
	   
	    String user = jObjTaxMaster.getString("User");
	    String clientCode = jObjTaxMaster.getString("ClientCode");
	    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
    
	  
	    
	    
	    if(customerAreaCode.trim().isEmpty())
	   	{
	    JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, strPOSCode, "gCustAreaCompulsory"); 

    	if(objSetupParameter.get("gCustAreaCompulsory").toString().equalsIgnoreCase("Y"))
    	{
    		//bldgCode = objCustomerAreaMasterDao.funGenerateCustomerAreaCode();
    		
    		
    		List list=utility.funGetDocumentCode("POSCustAreaMaster");
	    	if (!list.get(0).toString().equals("0"))
			{
			    String strCode = "00";
			    String code = list.get(0).toString();
			    StringBuilder sb = new StringBuilder(code);
			    String ss = sb.delete(0, 2).toString();
			    for (int i = 0; i < ss.length(); i++)
			    {
					if (ss.charAt(i) != '0')
					{
					    strCode = ss.substring(i, ss.length());
					    break;
					}
			    }
			    
			    int intCode = Integer.parseInt(strCode);
			    intCode++;
			    if (intCode < 10)
			    {
			    	customerAreaCode = "B000000" + intCode;
			    }
			    else if (intCode < 100)
			    {
			    	customerAreaCode = "B00000" + intCode;
			    }
			    else if (intCode < 1000)
			    {
			    	customerAreaCode = "B0000" + intCode;
			    }
			    else if (intCode < 10000)
			    {
			    	customerAreaCode = "B000" + intCode;
			    }
			    else if (intCode < 100000)
			    {
			    	customerAreaCode = "B00" + intCode;
			    }
			    else if (intCode < 1000000)
			    {
			    	customerAreaCode = "B0" + intCode;
			    }
			   
			}
			else
			{
				customerAreaCode = "B0000001";
			}

   	    	
   	     clsCustomerAreaMasterModel obj = new clsCustomerAreaMasterModel(new clsCustomerAreaMasterModel_ID(customerAreaCode,clientCode));
	   	    
	   	    obj.setStrBuildingName(bldgName);
	   	    obj.setStrAddress("");
	   	    obj.setDblHomeDeliCharge(0);
	   	    obj.setStrZoneCode("");
	   	    obj.setDblDeliveryBoyPayOut(0);
	   	    
	   	    obj.setStrClientCode(clientCode);
	   	    obj.setStrUserCreated(user);
	   	    obj.setStrUserEdited(user);
	   	    obj.setDteDateCreated(dateTime);
	   	    obj.setDteDateEdited(dateTime);
	   	    obj.setStrDataPostFlag("N");
	   	//    objCustomerAreaMasterDao.funSaveCustomerAreaMaster(obj);
	   	 objBaseService.funSave(obj);
   	    }
	   	}
    	   else
    	   {
    		   clsCustomerAreaMasterModel obj = new clsCustomerAreaMasterModel(new clsCustomerAreaMasterModel_ID(customerAreaCode,clientCode));
   	   	    
   	   	    obj.setStrBuildingName(bldgName);
   	   	    obj.setStrAddress("");
   	   	    obj.setDblHomeDeliCharge(0);
   	   	    obj.setStrZoneCode("");
   	   	    obj.setDblDeliveryBoyPayOut(0);
   	   	    
   	   	    obj.setStrClientCode(clientCode);
   	   	    obj.setStrUserCreated(user);
   	   	    obj.setStrUserEdited(user);
   	   	    obj.setDteDateCreated(dateTime);
   	   	    obj.setDteDateEdited(dateTime);
   	   	    obj.setStrDataPostFlag("N");
   	   	  //  objCustomerAreaMasterDao.funSaveCustomerAreaMaster(obj);
   	   	    
   	   	    objBaseService.funSave(obj);
   	   	
    	   }
	    
	    
	    
	    //SaveUpdate Customer 
	    long lastNo = 1;
	    String propertCode=clientCode.substring(4);
		 custCode=objMoveKOTDao.funCheckCustomerExist(contactNo);
		 if (custCode.trim().isEmpty())
		 {
			 List list=utility.funGetDocumentCode("POSCustomerMaster");
			 if (!list.get(0).toString().equals("0"))
				{
	    	 	String strCode = "00";
	    	 	String code = list.get(0).toString();
                StringBuilder sb = new StringBuilder(code);                
               
                strCode = sb.substring(1,sb.length());
                
                lastNo = Long.parseLong(strCode);
                lastNo++;
                custCode = propertCode+"C" + String.format("%07d", lastNo);				   
			}
			else
			{	
				custCode = propertCode+"C" + String.format("%07d", lastNo);
			}
		 }
	
	
	    clsCustomerMasterModel objMaster = new clsCustomerMasterModel(new clsCustomerMasterModel_ID(custCode,customerAreaCode,clientCode));
	  
	    objMaster.setStrCustomerName(custName);
	    objMaster.setStrBuldingCode(customerAreaCode);
	    objMaster.setStrBuildingName(bldgName);
	    objMaster.setStrStreetName("");
	    objMaster.setStrLandmark("");
	    objMaster.setStrArea("");
	    objMaster.setStrCity(strCity);
	    objMaster.setStrState("");
	    objMaster.setIntPinCode("");
	    objMaster.setLongMobileNo(contactNo);
	    objMaster.setStrOfficeBuildingCode("");
	    objMaster.setStrOfficeBuildingName("");
	    objMaster.setStrOfficeStreetName("");
	    objMaster.setStrOfficeLandmark("N");
	    objMaster.setStrOfficeArea("");
	    objMaster.setStrOfficeCity("");
	    objMaster.setStrOfficePinCode("");
	    objMaster.setStrOfficeState("");
	    objMaster.setStrOfficeNo("");
	    objMaster.setStrOfficeAddress("N");
	    objMaster.setStrExternalCode("");
	    objMaster.setStrCustomerType("");
	    objMaster.setDteDOB("");    
	    objMaster.setStrGender("");
	    objMaster.setDteAnniversary("");
	    objMaster.setStrEmailId("");
        
        objMaster.setStrUserCreated(user);
	    objMaster.setStrUserEdited(user);
	    objMaster.setDteDateCreated(dateTime);
	    objMaster.setDteDateEdited(dateTime);
	    objMaster.setStrCRMId("N");
	    objMaster.setStrCustAddress("N");
	    objMaster.setStrDataPostFlag("N");
	    objBaseService.funSave(objMaster);
	    
	  
	    
	    
	    
	    
	  //saveUpdate Table Reservation  
	    if (resCode.trim().isEmpty())
	    {
	    	resCode = objMoveKOTDao.funGenerateReservationCode();
	    	
	    }
	    
	

	    clsTableReservationModel objModel=new clsTableReservationModel(new clsTableReservationModel_ID(resCode,custCode,clientCode));
	   	   
  	  
  	    objModel.setStrAMPM(strAMPM);
  	    objModel.setStrPosCode(strPOSCode);
  	    objModel.setStrSmoking(strSmokingYN);
  	    objModel.setStrSpecialInfo(strInfo);
  	    objModel.setStrTableNo(strTableNo);
  	    objModel.setIntPax(intPax);
  	    objModel.setDteDateCreated(dateTime);
  	    objModel.setDteDateEdited(dateTime);
  	    objModel.setStrDataPostFlag("");
  	    objModel.setStrUserCreated(user);
  	    objModel.setStrUserEdited(user);
  	  objModel.setDteResDate(resDate);
  	objModel.setStrCustomerCode(custCode);
  	objModel.setTmeResTime(resTime);
  	    objMoveKOTDao.funAddUpdateTableReservation(objModel);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return resCode; 
    }




}