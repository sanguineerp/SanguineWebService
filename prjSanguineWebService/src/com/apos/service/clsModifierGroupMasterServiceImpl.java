package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsModifierGroupMasterDao;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.apos.model.clsModifierGroupMasterModel_ID;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service(value = "clsModifierGroupMasterService")
public class clsModifierGroupMasterServiceImpl implements clsModifierGroupMasterService{
	

	@Autowired
	private intfBaseService objSer;
	
	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	
	@Autowired
	clsModifierGroupMasterDao objModifierGroupMasterDao;

	@Override
	public String funSaveModifierGroupMaster(JSONObject jObjModifierGroupMaster)
	    {
		String modifierGroupCode = "";
		try
		{
		    
			modifierGroupCode = jObjModifierGroupMaster.getString("ModifierGroupCode");
		    String modifierGroupName = jObjModifierGroupMaster.getString("ModifierGroupName").toUpperCase();
		    String modifierGroupShortName = jObjModifierGroupMaster.getString("ModifierGroupShortName");
		    String minModifierSelection = jObjModifierGroupMaster.getString("MinModifierSelection");

		    String minItemLimit = jObjModifierGroupMaster.getString("MinItemLimit");
		    String maxModifierSelection = jObjModifierGroupMaster.getString("MaxModifierSelection");
		    String  maxItemLimit = jObjModifierGroupMaster.getString("MaxItemLimit");
		    String sequenceNo = jObjModifierGroupMaster.getString("SequenceNo");
		    String operationaltype = jObjModifierGroupMaster.getString("OperationType");
		    String user = jObjModifierGroupMaster.getString("User");
		    String clientCode = jObjModifierGroupMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		  
		    if (modifierGroupCode.trim().isEmpty())
		    {
		    	List list=objUtilityFunctions.funGetDocumentCode("POSModifierGroupMaster");
		    	if (!list.get(0).toString().equals("0"))
				{
				    String strCode = "0";
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
		            	modifierGroupCode = "MG00000" + intCode;
		            }
		            else if (intCode < 100)
		            {
		            	modifierGroupCode = "MG0000" + intCode;
		            }
		            else if (intCode < 1000)
		            {
		            	modifierGroupCode = "MG000" + intCode;
		            }
		            else if (intCode < 10000)
		            {
		            	modifierGroupCode = "MG00" + intCode;
		            }
		            else if (intCode < 100000)
		            {
		            	modifierGroupCode = "MG0" + intCode;
		            }
		            else if (intCode < 1000000)
		            {
		            	modifierGroupCode = "MG" + intCode;
		            }
		         }
		         else
		         {
		        	 modifierGroupCode = "MG000001";
		        }
				
		    }
		    
		    clsModifierGroupMasterHdModel objModel = new clsModifierGroupMasterHdModel(new clsModifierGroupMasterModel_ID(modifierGroupCode, clientCode));
		    objModel.setStrModifierGroupName(modifierGroupName);
		    objModel.setStrModifierGroupShortName(modifierGroupShortName);
		    objModel.setStrApplyMinItemLimit(minModifierSelection);
		    objModel.setIntItemMinLimit((int)Double.parseDouble(minItemLimit));
		    objModel.setIntItemMaxLimit((int)Double.parseDouble(maxItemLimit));
		    objModel.setIntSequenceNo((int)Double.parseDouble(sequenceNo));
		    objModel.setStrApplyMaxItemLimit(maxModifierSelection);
		    objModel.setStrOperational(operationaltype);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    objSer.funSave(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return modifierGroupCode;
	    }

	@Override
	public JSONObject funGetModifierGroupMasterData(String modGroupCode,String clientCode)throws Exception
	{
		// TODO Auto-generated method stub
		JSONObject jObjModGroup=new JSONObject();
		JSONArray jArrData = new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("modGroupCode",modGroupCode);
			hmParameters.put("clientCode",clientCode);
			
			clsModifierGroupMasterHdModel objModifierGroupMasterHdModell = objModifierGroupMasterDao.funGetItemModifierMasterData("getModifierGroupMaster", hmParameters);
			
			jArrData.put(objModifierGroupMasterHdModell.getStrModifierGroupCode());
			jArrData.put(objModifierGroupMasterHdModell.getStrModifierGroupName());
			jArrData.put(objModifierGroupMasterHdModell.getStrModifierGroupShortName());
			jArrData.put(objModifierGroupMasterHdModell.getStrApplyMaxItemLimit());
			jArrData.put(objModifierGroupMasterHdModell.getStrApplyMinItemLimit());
			jArrData.put(objModifierGroupMasterHdModell.getIntItemMaxLimit());
			jArrData.put(objModifierGroupMasterHdModell.getIntItemMinLimit());
			jArrData.put(objModifierGroupMasterHdModell.getIntSequenceNo());
			jArrData.put(objModifierGroupMasterHdModell.getStrOperational());
		
		    
			jObjModGroup.put("POSModifierGroupMaster", jArrData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjModGroup;
		
	}
}
