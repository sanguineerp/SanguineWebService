
package com.apos.service;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfAreaMasterDao;
import com.apos.dao.clsPOSMasterDaoImpl;
import com.apos.dao.clsTaxMasterDaoImpl;
import com.apos.dao.intfPOSMasterDao;
import com.apos.dao.intfTaxMasterDao;
import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsTaxMasterModel;
import com.apos.model.clsTaxMasterModel_ID;
import com.apos.model.clsTaxOnGroupModel;
import com.apos.model.clsTaxPosDetailsModel;
import com.apos.model.clsTaxSettlementDetailsModel;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service(value = "clsTaxMasterService")

public class clsTaxMasterServiceImpl implements clsTaxMasterService{
	@Autowired
	private intfTaxMasterDao objTaxMasterDao;
	
	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;
		
	  @Autowired
	    intfPOSMasterDao objPosDao;
	  

	    @Autowired
	    intfAreaMasterDao objAreaDao;
	public String funAddUpdateTaxMaster(JSONObject jObjTaxMaster){
		String taxCode = "";
		try
		{
		    
			taxCode = jObjTaxMaster.getString("TaxCode");
			String taxDesc = jObjTaxMaster.getString("TaxDesc");
		    String taxType = jObjTaxMaster.getString("TaxType");
		    String taxShortName = jObjTaxMaster.getString("TaxShortName");
		    double amount = jObjTaxMaster.getDouble("Amount");
		    String taxOnSP = jObjTaxMaster.getString("TaxOnSP");
		    double percent = jObjTaxMaster.getDouble("Percent");
		  
		    
		    String taxOnGD = jObjTaxMaster.getString("TaxOnGD");
		    String taxRounded = jObjTaxMaster.getString("TaxRounded");
		    String taxCalculation = jObjTaxMaster.getString("TaxCalculation");
		    String taxOnTax = jObjTaxMaster.getString("TaxOnTax");
		    String taxIndicator = jObjTaxMaster.getString("TaxIndicator");
		    String dteValidFrom = jObjTaxMaster.getString("ValidFrom");
		  
		    String dteValidTo = jObjTaxMaster.getString("ValidTo");
		    String itemType = jObjTaxMaster.getString("ItemType");
		    String operationType = jObjTaxMaster.getString("OperationType");
		    String areaCode = jObjTaxMaster.getString("AreaList");
		    String taxOnTaxCode = jObjTaxMaster.getString("TaxList");
		    String accountCode = jObjTaxMaster.getString("AccountCode");
		   
		    
		   
		    String user = jObjTaxMaster.getString("User");
		    String clientCode = jObjTaxMaster.getString("ClientCode");
		    String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");
	    
		    if (taxCode.trim().isEmpty())
		    {
		    	List list=utility.funGetDocumentCode("POSTaxMaster");
		    	
		    	  if (!list.get(0).toString().equals("0"))
					{
						String strCode = "0";
						String code = list.get(0).toString();
						StringBuilder sb = new StringBuilder(code);
						String ss = sb.delete(0, 1).toString();
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
							taxCode = "T0" + intCode;
						}
						else if (intCode < 100)
						{
							taxCode = "T" + intCode;
						}
						
					}
					else
					{
						taxCode = "T01";
					}
		    }
		    
		    clsTaxMasterModel objModel=new clsTaxMasterModel(new clsTaxMasterModel_ID(taxCode,clientCode));
		    
		    objModel.setStrAccountCode(accountCode);
		    objModel.setStrAreaCode(areaCode);
		    objModel.setStrDataPostFlag("Y");
		    objModel.setStrItemType(itemType);
		    objModel.setStrOperationType(operationType);
		    objModel.setStrTaxCalculation(taxCalculation);
		    objModel.setStrTaxDesc(taxDesc);
		    objModel.setStrTaxIndicator(taxIndicator);
		    objModel.setStrTaxOnGD(taxOnGD);
		    objModel.setStrTaxOnSP(taxOnSP);
		    objModel.setStrTaxOnTax(taxOnTax);
		    objModel.setStrTaxOnTaxCode(taxOnTaxCode);
		    objModel.setStrTaxRounded(taxRounded);
		    objModel.setStrTaxShortName(taxShortName);
		    objModel.setStrTaxType(taxType);
		    objModel.setDblAmount(amount);
		    objModel.setDblPercent(percent);
		    objModel.setDteValidFrom(dteValidFrom);
		    objModel.setDteValidTo(dteValidTo);
		    
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		 
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    
		    JSONArray settleList=jObjTaxMaster.getJSONArray("SettlementDetails");
		    Set<clsTaxSettlementDetailsModel> setSettlementDtl = new HashSet<clsTaxSettlementDetailsModel>();
		    
		    for(int i=0;i<settleList.length();i++)
		    {	
		    	JSONObject jObj = new JSONObject();
		    	jObj=settleList.getJSONObject(i);
		    	String settlementCode=jObj.getString("SettlementCode");
		    	String settlementDesc=jObj.getString("SettlementDesc");
		    	clsTaxSettlementDetailsModel objSettlementModel = new clsTaxSettlementDetailsModel();
		    	
		    	objSettlementModel.setStrSettlementCode(settlementCode);
		    	objSettlementModel.setStrApplicable("true");
		    	
		    	objSettlementModel.setStrSettlementName(settlementDesc);
		    	objSettlementModel.setStrUserEdited(user);
		    	objSettlementModel.setStrUserCreated(user);
		    	  
		    	objSettlementModel.setDteDateCreated(dateTime);
		    	
		    	objSettlementModel.setDteDateEdited(dateTime);
		    	objSettlementModel.setDteFrom(dteValidFrom);
		    	objSettlementModel.setDteTo(dteValidTo);
		    	setSettlementDtl.add(objSettlementModel);
		    	
		    }
		    objModel.setListsettlementDtl(setSettlementDtl);
		
		    JSONArray groupList=jObjTaxMaster.getJSONArray("GroupDetails");
		    Set<clsTaxOnGroupModel> setGroupDtl = new HashSet<clsTaxOnGroupModel>();
		    
		    for(int i=0;i<groupList.length();i++)
		    {	
		    	JSONObject jObj = new JSONObject();
		    	jObj=groupList.getJSONObject(i);
		    	String settlementCode=jObj.getString("GroupCode");
		    	String settlementDesc=jObj.getString("GroupName");
		    	clsTaxOnGroupModel objSettlementModel = new clsTaxOnGroupModel();
		    	
		    	objSettlementModel.setStrGroupCode(settlementCode);
		    	objSettlementModel.setStrApplicable("true");
		    	
		    	objSettlementModel.setStrGroupName(settlementDesc);
		    	objSettlementModel.setStrUserEdited(user);
		    	objSettlementModel.setStrUserCreated(user);
		    	  
		    	objSettlementModel.setDteDateCreated(dateTime);
		    	
		    	objSettlementModel.setDteDateEdited(dateTime);
		    	objSettlementModel.setDteFrom(dteValidFrom);
		    	objSettlementModel.setDteTo(dteValidTo);
		    	setGroupDtl.add(objSettlementModel);
		    	
		    }
		    objModel.setListTaxGroupDtl(setGroupDtl);
		    
		    JSONArray posList=jObjTaxMaster.getJSONArray("PosList");
		    Set<clsTaxPosDetailsModel> listTaxPosDtl = new HashSet<clsTaxPosDetailsModel>();
		    
		    for(int i=0;i<posList.length();i++)
		    {	
		    	JSONObject jObj = new JSONObject();
		    	jObj=posList.getJSONObject(i);
		    	String posCode=jObj.getString("PosCode");
		    	
		    	clsTaxPosDetailsModel objPosModel = new clsTaxPosDetailsModel();
		    	objPosModel.setStrTaxDesc(taxDesc);
		    	objPosModel.setStrPOSCode(posCode);
		    	//listTaxPosDtl.add(objPosModel);
		    	listTaxPosDtl.add(objPosModel);
		    	
		    }
		    
//		    objModel.setListTaxPosDtl(listTaxPosDtl);
		    objModel.setListTaxPosDtl(listTaxPosDtl);
		    
		   // objPOS.funSaveTaxPOSDetails(posList,taxCode,taxDesc);
		    objService.funSave(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return taxCode; 
	    }
	

	public JSONObject funSelectedTaxMasterData(String taxCode,String clientCode)
	{
		JSONObject jObjTaxMaster=new JSONObject();
		JSONArray jSettleData=new JSONArray();
		JSONArray jGroupData=new JSONArray();
		 JSONArray jPosData=new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("taxCode",taxCode);
			hmParameters.put("clientCode",clientCode);
			clsTaxMasterModel objTaxModel = objTaxMasterDao.funGetTaxMasterData("getTaxMaster", hmParameters);
			Set<clsTaxSettlementDetailsModel> listsettlementDtl =objTaxModel.getListsettlementDtl();
			 Iterator itr = listsettlementDtl.iterator();
		        while(itr.hasNext())
		        {
		        	clsTaxSettlementDetailsModel objSettle=(clsTaxSettlementDetailsModel)itr.next();
				JSONObject jObjSettle=new JSONObject();
				jObjSettle.put("SettlementCode",objSettle.getStrSettlementCode());
				jObjSettle.put("SettlementDesc",objSettle.getStrSettlementName());
				jObjSettle.put("ApplicableYN",true);
				jSettleData.put(jObjSettle);
				
			}
		    	Set<clsTaxOnGroupModel> listTaxGroupDtl =objTaxModel.getListTaxGroupDtl();
				  itr = listTaxGroupDtl.iterator();
			        while(itr.hasNext())
			        {
			        	clsTaxOnGroupModel objSettle=(clsTaxOnGroupModel)itr.next();
					JSONObject jObjSettle=new JSONObject();
					jObjSettle.put("GroupCode",objSettle.getStrGroupCode());
					jObjSettle.put("GroupName",objSettle.getStrGroupName());
				
					jGroupData.put(jObjSettle);
					
			        }
			Set<clsTaxPosDetailsModel> listTaxPosDtl=objTaxModel.getListTaxPosDtl();
			
			 itr = listTaxPosDtl.iterator();
		        while(itr.hasNext())
		        {
		        clsTaxPosDetailsModel objPos=(clsTaxPosDetailsModel)itr.next();;
				JSONObject jObjPosData=new JSONObject();
				jObjPosData.put("PosCode",objPos.getStrPOSCode());
				
				hmParameters=new HashMap<String,String>();
				hmParameters.put("posCode",objPos.getStrPOSCode());
				hmParameters.put("clientCode",clientCode);
				clsPOSMasterModel model = objPosDao.funGetPOSMasterData("getPOSMaster", hmParameters);
				jObjPosData.put("PosName",model.getStrPosName());
				jPosData.put(jObjPosData);
				
			}
			jObjTaxMaster.put("strAccountCode",objTaxModel.getStrAccountCode());
			 
			
			jObjTaxMaster.put("strItemType",objTaxModel.getStrItemType());
			jObjTaxMaster.put("strOperationType",objTaxModel.getStrOperationType());
			jObjTaxMaster.put("strTaxCalculation",objTaxModel.getStrTaxCalculation());
			jObjTaxMaster.put("strTaxCode",objTaxModel.getStrTaxCode());
			jObjTaxMaster.put("strTaxDesc",objTaxModel.getStrTaxDesc());
			jObjTaxMaster.put("dblAmount",objTaxModel.getDblAmount());
			jObjTaxMaster.put("dblPercent",objTaxModel.getDblPercent());
			jObjTaxMaster.put("strTaxIndicator",objTaxModel.getStrTaxIndicator());
			 jObjTaxMaster.put("strTaxOnGD",objTaxModel.getStrTaxOnGD());
			 jObjTaxMaster.put("strTaxOnSP",objTaxModel.getStrTaxOnSP());
			 jObjTaxMaster.put("strTaxOnTax",objTaxModel.getStrTaxOnTax());
			
			 jObjTaxMaster.put("strTaxRounded",objTaxModel.getStrTaxRounded());
			 jObjTaxMaster.put("strTaxShortName",objTaxModel.getStrTaxShortName());
			 jObjTaxMaster.put("strTaxType",objTaxModel.getStrTaxType());
			 jObjTaxMaster.put("dteValidFrom",objTaxModel.getDteValidFrom());
			 jObjTaxMaster.put("dteValidTo",objTaxModel.getDteValidTo());
			 jObjTaxMaster.put("SettlementDtl",jSettleData);
			 jObjTaxMaster.put("GroupDtl",jGroupData);
			 jObjTaxMaster.put("PosDtl",jPosData);
		    
		    //Area Data
		    String[] spArea = objTaxModel.getStrAreaCode().split(",");
			 JSONArray jAreaData=new JSONArray();
				for(int i=0; i<spArea.length; i++)
		
				{
					String areaCode=spArea[i];
					JSONObject jObjAreaData=new JSONObject();
					
					hmParameters=new HashMap<String,String>();
					hmParameters.put("areaCode",areaCode);
					hmParameters.put("clientCode",clientCode);
					clsAreaMasterModel model = objAreaDao.funGetAreaMasterData("getAreaMaster", hmParameters);
					
					jObjAreaData.put("AreaCode",areaCode);
					jObjAreaData.put("AreaName",model.getStrAreaName());
					jAreaData.put(jObjAreaData);
					
				}
			 
				jObjTaxMaster.put("AreaDtl",jAreaData);
		    
			 //Tax On Tax
			 
			 String[] spTaxOnTax = objTaxModel.getStrTaxOnTaxCode().split(",");
			 JSONArray jTaxData=new JSONArray();
				for(int i=0; i<spTaxOnTax.length; i++)
		
				{
					 taxCode=spTaxOnTax[i];
					JSONObject jObjTaxData=new JSONObject();
					jObjTaxData.put("TaxCode",taxCode);
					//Get Tax Desc From Code
					hmParameters=new HashMap<String,String>();
					hmParameters.put("taxCode",taxCode);
					hmParameters.put("clientCode",clientCode);
					clsTaxMasterModel model = objTaxMasterDao.funGetTaxMasterData("getTaxMaster", hmParameters);
					jObjTaxData.put("TaxDesc",model.getStrTaxDesc());
					jTaxData.put(jObjTaxData);
					
				}
			 
				jObjTaxMaster.put("TaxData",jTaxData);
		    
		
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjTaxMaster;
	}


	@Override
	public String funGetAllTaxForMaster(String clientCode) {
		
		clsTaxMasterModel model =new clsTaxMasterModel();
		JSONObject jObj = new JSONObject();
	try{
		JSONArray jArrData=new JSONArray();
		 
		List list=objService.funLoadAll(model,clientCode);
		 clsTaxMasterModel objTaxModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objTaxModel= (clsTaxMasterModel) list.get(cnt);
			    
			    JSONObject jArrDataRow = new JSONObject();
			    jArrDataRow.put("strTaxCode",objTaxModel.getStrTaxCode());
			    jArrDataRow.put("strTaxDesc",objTaxModel.getStrTaxDesc());
			   
			    jArrData.put(jArrDataRow);
			}
			jObj.put("TaxList", jArrData);
		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
	
	return jObj.toString();
	}
	
}

