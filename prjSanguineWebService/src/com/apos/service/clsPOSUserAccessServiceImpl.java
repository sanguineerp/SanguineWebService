package com.apos.service;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsPOSUserAccessDaoImpl;
import com.apos.dao.intfPOSUserAccessDao;
import com.apos.model.clsSubGroupMasterHdModel;
import com.apos.model.clsSuperUserDetailHdModel;
import com.apos.model.clsUserDetailHdModel;
import com.apos.model.clsUserDetailModel_ID;
import com.sanguine.service.intfBaseService;


@Service(value = "clsPOSUserAccessServiceImpl")
public class clsPOSUserAccessServiceImpl implements clsPOSUserAccessService
{
	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private intfPOSUserAccessDao objUserAccessDao;
	
	
	public String funAddUpdatePOSUserAccess(JSONObject jObjUserAccess){
		String userCode = "",clientCode="",userType="";
		try
		{
		    
			userCode = jObjUserAccess.getString("UserCode");
			clientCode= jObjUserAccess.getString("ClientCode");
			userType= jObjUserAccess.getString("UserType");
			
		    JSONArray masterFormList=jObjUserAccess.getJSONArray("MasterFormDetails");
		    JSONArray transactionFormList=jObjUserAccess.getJSONArray("TransactionFormDetails");
		    JSONArray reportsFormList=jObjUserAccess.getJSONArray("ReportsFormDetails");
		    JSONArray utilitiesFormList=jObjUserAccess.getJSONArray("UtilitiesFormDetails");
		   // List<clsUserAccessDetailModel> listOfForms=new ArrayList<clsUserAccessDetailModel>();
		  
		   if(userType.equals("Super"))
		   {
			   objUserAccessDao.funDeleteSuperUserAccessDetails(userCode);
			   //For MasterForms
			    if(masterFormList.length()>0)
			    {
			    	funSaveSuperUserModel(userCode,masterFormList);
			    }
			    
			 	
				 //For TransactionForms
			    if(transactionFormList.length()>0)
			    {
			    	funSaveSuperUserModel(userCode,transactionFormList);
			    }
				 
			    //For ReportsForms
			    if(reportsFormList.length()>0)
			    {
			    	funSaveSuperUserModel(userCode,reportsFormList);
			    }
				 
			    //For UtilitiesForms
			    if(utilitiesFormList.length()>0)
			    {
			    	funSaveSuperUserModel(userCode,utilitiesFormList);
				}
		   }
		   else
		   {
			   objUserAccessDao.funDeleteUserAccessDetails(userCode);
			   //For MasterForms
			    if(masterFormList.length()>0)
			    {
			    	funSaveUserModel(userCode,masterFormList); 
			    }
			    
			 	
				 //For TransactionForms
			    if(transactionFormList.length()>0)
			    {
			    	funSaveUserModel(userCode,transactionFormList);
			    }
				 
			    //For ReportsForms
			    if(reportsFormList.length()>0)
			    {
			    	funSaveUserModel(userCode,reportsFormList);
			    }
				 
			    //For UtilitiesForms
			    if(utilitiesFormList.length()>0)
			    {
			    	funSaveUserModel(userCode,utilitiesFormList);
				}
		   }
		   
		 
		  
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return userCode; 
	    }
	

	
	private void funSaveUserModel(String userCode,JSONArray jsonArray)throws Exception
	{
		for(int i=0; i<jsonArray.length(); i++)
	    {
			JSONObject jObj = new JSONObject();
			jObj=jsonArray.getJSONObject(i);
			clsUserDetailHdModel objUserModel= new clsUserDetailHdModel(new clsUserDetailModel_ID(userCode,jObj.getString("FormName")));
			objUserModel.setStrUserCode(userCode);
	    	objUserModel.setStrFormName(jObj.getString("FormName"));
	    	objUserModel.setStrButtonName(jObj.getString("FormName"));
	    	objUserModel.setIntSequence(5);
	    	objUserModel.setStrAdd("true");
	    	objUserModel.setStrEdit("true");
	    	objUserModel.setStrDelete("true");
	    	objUserModel.setStrView("true");
	    	objUserModel.setStrPrint("true");
	    	objUserModel.setStrSave("true");
	    	objUserModel.setStrGrant(jObj.getString("Grant"));
	    	objUserModel.setStrTLA(jObj.getString("TLA"));
	    	objUserModel.setStrAuditing(jObj.getString("Audit"));
	    //	objUserAccessDao.funAddUpdatePOSUserAccess(objUserModel);
	    	objService.funSave(objUserModel);
	    }	
    	
	}
	private void funSaveSuperUserModel(String userCode,JSONArray jsonArray) throws Exception
	{
		for(int i=0; i<jsonArray.length(); i++)
	    {
			JSONObject jObj = new JSONObject();
	    	jObj=jsonArray.getJSONObject(i);
	    	
			clsSuperUserDetailHdModel objUserModel= new clsSuperUserDetailHdModel();
			objUserModel.setStrUserCode(userCode);
	    	objUserModel.setStrFormName(jObj.getString("FormName"));
	    	objUserModel.setStrButtonName(jObj.getString("FormName"));
	    	objUserModel.setIntSequence(5);
	    	objUserModel.setStrAdd("true");
	    	objUserModel.setStrEdit("true");
	    	objUserModel.setStrDelete("true");
	    	objUserModel.setStrView("true");
	    	objUserModel.setStrPrint("true");
	    	objUserModel.setStrSave("true");
	    	objUserModel.setStrGrant(jObj.getString("Grant"));
	    	objUserModel.setStrTLA(jObj.getString("TLA"));
	    	objUserModel.setStrAuditing(jObj.getString("Audit"));
	    	//objUserAccessDao.funAddUpdatePOSSuperUserAccess(objUserModel);
	    	
	    	objService.funSave(objUserModel);
	    }
	 }
	
	
	public JSONObject funGetAllFormDetails(String clientCode)
	{
		return objUserAccessDao.funGetAllFormDetails(clientCode);
	}
	
	
	@Override
	public JSONObject funGetUserAccessData(String userCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj=new JSONObject();
		
		jObj = objUserAccessDao.funGetUserAccessData("getUserAccess", userCode);
		   
		return jObj;
	}

	
}
