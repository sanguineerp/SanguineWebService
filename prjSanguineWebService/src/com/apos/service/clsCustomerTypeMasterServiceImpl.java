package com.apos.service;

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

import com.apos.dao.clsCustomerTypeMasterDaoImpl;


import com.apos.dao.inftCustomerTypeMasterDao;
import com.apos.model.clsCustomerTypeMasterModel;
import com.apos.model.clsCustomerTypeMasterModel_ID;
import com.apos.model.clsGroupMasterModel;
import com.apos.model.clsGroupMasterModel_ID;
import com.apos.model.clsReasonMasterModel;
import com.apos.model.clsShiftMasterModel;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsCustomerTypeMasterService")
public class clsCustomerTypeMasterServiceImpl implements clsCustomerTypeMasterService {
	
	
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	private clsUtilityFunctions utility;
	
	@Autowired
	inftCustomerTypeMasterDao objinftDao;

	public String funSaveCustomerTypeMaster(JSONObject jObjCustomerTypeMaster){
		String customerTypeMasterCode = "";
		try
		{
		    System.out.println(jObjCustomerTypeMaster);
			customerTypeMasterCode = jObjCustomerTypeMaster.getString("CustomerTypeCode");
		    String customerType = jObjCustomerTypeMaster.getString("CustomerType");
		    Double discount = jObjCustomerTypeMaster.getDouble("Discount");
		    String user = jObjCustomerTypeMaster.getString("User");
		    String clientCode = jObjCustomerTypeMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd"); 
		    if (customerTypeMasterCode.trim().isEmpty())
		    {
		    	long lngCode=utility.funGetDocumentCodeFromInternal("POSCustomerTypeMaster");
		    	if(lngCode>0)
		    	{
		    		customerTypeMasterCode = "CT" + String.format("%03d", lngCode);
		    	}
		    }
		    clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel(new clsCustomerTypeMasterModel_ID(customerTypeMasterCode,clientCode));
		    //clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel(new clsCustomerTypeMasterModel_ID(customerTypeMasterCode, clientCode));
		    objModel.setStrCustType(customerType);
		    objModel.setDblDiscPer(discount);
		    objModel.setStrClientCode(clientCode);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    
		    objDao.funSave(objModel);
		    //customerTypeMasterCode = objCustomerTypeMasterDao.funSaveCustomerTypeMaster(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return customerTypeMasterCode;


	}

	@Override
	public JSONObject funLoadCustomerTypeMasterData(String searchCode,String clientCode) {
JSONObject jobjData=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("custTypeCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			clsCustomerTypeMasterModel objModel = objinftDao.funLoadCustomerTypeMasterData("getCustomerType", hmParameters);														
			
			jobjData.put("strCustTypeCode",objModel.getStrCustTypeCode());
			jobjData.put("strCustType",objModel.getStrCustType());
			jobjData.put("dblDiscPer",objModel.getDblDiscPer());
			
		
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjData;
	}


	
	@Override
	public JSONObject funGetAllCustomerType(String clientCode) throws Exception 
	{
		clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel();
		   JSONObject jObjLoadData = new JSONObject();
		   JSONArray jArrData = new JSONArray();
			
			List list =objDao.funLoadAll(objModel,clientCode);
			clsCustomerTypeMasterModel objclsCustomerTypeMasterModel =	 null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objclsCustomerTypeMasterModel = (clsCustomerTypeMasterModel) list.get(cnt);
				JSONObject obj = new JSONObject();
				obj.put("strCustomeTypeCode",objclsCustomerTypeMasterModel.getStrCustTypeCode());
				obj.put("strCustomeTypeName",objclsCustomerTypeMasterModel.getStrCustType());
				jArrData.put(obj);
	       }
			jObjLoadData.put("CustomerTypeList", jArrData);
	       return jObjLoadData;

	   }
	
	@Override
	public String funFillCustTypeCombo(String clientCode)
	{
		return objinftDao.funFillCustTypeCombo(clientCode);
	}
}

