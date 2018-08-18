package com.apos.service;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.apos.dao.clsCustomerMasterDaoImpl;
import com.apos.dao.inftCustomerMasterDao;
import com.apos.model.clsCustomerMasterModel;
import com.apos.model.clsCustomerMasterModel_ID;
import com.apos.model.clsCustomerTypeMasterModel;
import com.apos.model.clsCustomerTypeMasterModel_ID;
import com.apos.model.clsReasonMasterModel;
import com.apos.model.clsSetupHdModel;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsCustomerMasterService")
public class clsCustomerMasterServiceImpl implements clsCustomerMasterService{
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	clsUtilityFunctions utility;
	

	@Autowired
	inftCustomerMasterDao objinftDao;
	
	public String funSaveCustomerMaster(JSONObject jObjCustomerMaster){
		String customerMasterCode = "";
		try
		{
		    System.out.println(jObjCustomerMaster);
		    customerMasterCode = jObjCustomerMaster.getString("strCustomerCode");
		    String strCustomerName = jObjCustomerMaster.getString("strCustomerName");
		    String strBuldingCode = jObjCustomerMaster.getString("strBuldingCode");
		    String strBuildingName = jObjCustomerMaster.getString("strBuildingName");
		    String strStreetName = jObjCustomerMaster.getString("strStreetName");
		    String strLandmark = jObjCustomerMaster.getString("strLandmark");
		    String strArea = jObjCustomerMaster.getString("strArea");
		    String strCity = jObjCustomerMaster.getString("strCity");
		    String strState = jObjCustomerMaster.getString("strState");
		    String intPinCode = jObjCustomerMaster.getString("intPinCode");
		    String intlongMobileNo = jObjCustomerMaster.getString("intlongMobileNo");
		    String strOfficeBuildingCode = jObjCustomerMaster.getString("strOfficeBuildingCode");
		    String strOfficeBuildingName = jObjCustomerMaster.getString("strOfficeBuildingName");
		    String strOfficeStreetName = jObjCustomerMaster.getString("strOfficeStreetName");
		//    String strOfficeLandmark = jObjCustomerMaster.getString("strOfficeLandmark");
		    String strOfficeArea = jObjCustomerMaster.getString("strOfficeArea");
		    String strOfficeCity = jObjCustomerMaster.getString("strOfficeCity");
		    String strOfficePinCode = jObjCustomerMaster.getString("strOfficePinCode");
		    String strOfficeState = jObjCustomerMaster.getString("strOfficeState");
		    String strOfficeNo = jObjCustomerMaster.getString("strOfficeNo");
		   // String strOfficeAddress = jObjCustomerMaster.getString("strOfficeAddress");
		    String strExternalCode = jObjCustomerMaster.getString("strExternalCode");
		    String strCustomerType = jObjCustomerMaster.getString("strCustomerType");
		    String dteDOB = jObjCustomerMaster.getString("dteDOB");
		    String strGender = jObjCustomerMaster.getString("strGender");
		    String dteAnniversary = jObjCustomerMaster.getString("dteAnniversary");
		    String strEmailId = jObjCustomerMaster.getString("strEmailId");
	
		    String user = jObjCustomerMaster.getString("User"); 
		    String clientCode = jObjCustomerMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		    if (customerMasterCode.trim().isEmpty())
		    {
		    	 long lastNo = 1;
			     String propertCode=clientCode.substring(4);
		    	//customerMasterCode = objCustomerMasterDao.funGenerateCustomerMasterCode(clientCode);
		    	//strBuldingCode = objCustomerMasterDao.funGenerateCustomerMasterBuildingCode();
		    	
		    	List list=utility.funGetDocumentCode("POSCustomerMaster");
				 if (!list.get(0).toString().equals("0"))
					{
		    	 	String strCode = "00";
		    	 	String code = list.get(0).toString();
	                StringBuilder sb = new StringBuilder(code);                
	               
	                strCode = sb.substring(1,sb.length());
	                
	                lastNo = Long.parseLong(strCode);
	                lastNo++;
	                customerMasterCode = propertCode+"C" + String.format("%07d", lastNo);				   
				}
				else
				{	
	              customerMasterCode = propertCode+"C" + String.format("%07d", lastNo);
				}
		    }
		    clsCustomerMasterModel objModel = new clsCustomerMasterModel(new clsCustomerMasterModel_ID(customerMasterCode,strBuldingCode,clientCode));
		    //clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel(new clsCustomerTypeMasterModel_ID(customerTypeMasterCode, clientCode));
		    objModel.setStrCustomerName(strCustomerName);
		    objModel.setStrBuldingCode(strBuldingCode);
		    objModel.setStrBuildingName(strBuildingName);
		    objModel.setStrStreetName(strStreetName);
		    objModel.setStrLandmark(strLandmark);
		    objModel.setStrArea(strArea);
		    objModel.setStrCity(strCity);
		    objModel.setStrState(strState);
		    objModel.setIntPinCode(intPinCode);
		    objModel.setLongMobileNo(intlongMobileNo);
		    objModel.setStrOfficeBuildingCode(strOfficeBuildingCode);
		    objModel.setStrOfficeBuildingName(strOfficeBuildingName);
		    objModel.setStrOfficeStreetName(strOfficeStreetName);
		    objModel.setStrOfficeLandmark("N");
		    objModel.setStrOfficeArea(strOfficeArea);
		    objModel.setStrOfficeCity(strOfficeCity);
		    objModel.setStrOfficePinCode(strOfficePinCode);
		    objModel.setStrOfficeState(strOfficeState);
		    objModel.setStrOfficeNo(strOfficeNo);
		    objModel.setStrOfficeAddress("N");
		    objModel.setStrExternalCode(strExternalCode);
		    objModel.setStrCustomerType(strCustomerType);
		    objModel.setDteDOB(dteDOB);    
		    objModel.setStrGender(strGender);
		    objModel.setDteAnniversary(dteAnniversary);
		    objModel.setStrEmailId(strEmailId);
            objModel.setStrClientCode(clientCode);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrCRMId("N");
		    objModel.setStrCustAddress("N");
		    objModel.setStrDataPostFlag("N");
		   
		    objDao.funSave(objModel);
		 //   customerMasterCode = objCustomerMasterDao.funSaveCustomerMaster(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return customerMasterCode;


	}

	@Override
	public JSONObject funLoadCustomeMasterData(String searchCode,String clientCode) {
			JSONObject jobjData=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("customerCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			clsCustomerMasterModel objModel = objinftDao.funLoadCustomeMasterData("getCustomerMaster", hmParameters);														
			
			jobjData.put("strCustomerCode",objModel.getStrCustomerCode());
			jobjData.put("strCustomerName",objModel.getStrCustomerName());
			jobjData.put("longMobileNo",objModel.getLongMobileNo());
			jobjData.put("strArea",objModel.getStrArea());
			jobjData.put("strBuldingCode",objModel.getStrBuldingCode());
			jobjData.put("strBuildingName",objModel.getStrBuildingName());
			jobjData.put("strStreetName",objModel.getStrStreetName());
			jobjData.put("strLandmark",objModel.getStrLandmark());
			jobjData.put("strCity",objModel.getStrCity());
			jobjData.put("strState",objModel.getStrState());
			jobjData.put("intPinCode",objModel.getIntPinCode());
			jobjData.put("strOfficeBuildingCode",objModel.getStrOfficeBuildingCode());
			jobjData.put("strOfficeBuildingName",objModel.getStrOfficeBuildingName());
			jobjData.put("strOfficeStreetName",objModel.getStrOfficeStreetName());
			jobjData.put("strOfficeArea",objModel.getStrOfficeArea());
			jobjData.put("strOfficeCity",objModel.getStrOfficeCity());
			jobjData.put("strOfficePinCode",objModel.getStrOfficePinCode());
			jobjData.put("strOfficeState",objModel.getStrOfficeState());
			jobjData.put("strOfficeNo",objModel.getStrOfficeNo());
			jobjData.put("strExternalCode",objModel.getStrExternalCode());
			jobjData.put("strCustomerType",objModel.getStrCustomerType());
			jobjData.put("dteDOB",objModel.getDteDOB());
			jobjData.put("strGender",objModel.getStrGender());
			jobjData.put("dteAnniversary",objModel.getDteAnniversary());
			jobjData.put("strEmailId",objModel.getStrEmailId());
			
			
		
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjData;
	}
	
	
	@Override
	public JSONObject funGetAllCityForMaster(String clientCode) throws Exception 
	{
		clsSetupHdModel objModel = new clsSetupHdModel();
		   JSONObject jObjLoadData = new JSONObject();
		   JSONArray jArrData = new JSONArray();
			
			List list =objDao.funLoadAll(objModel,clientCode);
			clsSetupHdModel objclsSetupHdModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objclsSetupHdModel = (clsSetupHdModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strCityName",objclsSetupHdModel.getStrCityName());			
				jArrData.put(objMenu);
	       }
			jObjLoadData.put("cityList", jArrData);
	       return jObjLoadData;

	   }

	
	@Override
	public JSONObject funGetAllStateForMaster(String clientCode) throws Exception 
	{
		clsSetupHdModel objModel = new clsSetupHdModel();
		   JSONObject jObjLoadData = new JSONObject();
		   JSONArray jArrData = new JSONArray();
			
			List list =objDao.funLoadAll(objModel,clientCode);
			clsSetupHdModel objclsSetupHdModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objclsSetupHdModel = (clsSetupHdModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strStateName",objclsSetupHdModel.getStrState());			
				jArrData.put(objMenu);
	       }
			jObjLoadData.put("stateList", jArrData);
	       return jObjLoadData;

	   }
	
	@Override
	public JSONObject funGetAllCountryForMaster(String clientCode) throws Exception 
	{
		clsSetupHdModel objModel = new clsSetupHdModel();
		   JSONObject jObjLoadData = new JSONObject();
		   JSONArray jArrData = new JSONArray();
			
			List list =objDao.funLoadAll(objModel,clientCode);
			clsSetupHdModel objclsSetupHdModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objclsSetupHdModel = (clsSetupHdModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strCountryName",objclsSetupHdModel.getStrCountry());			
				jArrData.put(objMenu);
	       }
			jObjLoadData.put("countryList", jArrData);
	       return jObjLoadData;

	   }
		
	
}


