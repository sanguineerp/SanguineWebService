package com.apos.service;

import java.util.HashMap;
import java.util.List;




import java.util.Map;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsSubGroupMasterDao;
import com.apos.dao.intfSubGroupMasterDao;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.apos.model.clsSubGroupMasterHdModel;
import com.apos.model.clsSubGroupMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsSubGroupMasterService")
public class clsSubGroupMasterService implements intfSubGroupMasterService {
	
	@Autowired
	private intfSubGroupMasterDao objSubGroupMasterDao;
	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	@Autowired
	private intfBaseService objSer;
	
	public String funSaveSubGroupMaster(JSONObject jObjSubGroupMaster){

		String subGroupCode = "";
		try
		{
		    subGroupCode = jObjSubGroupMaster.getString("SubGroupCode");
		    String subgroupName = jObjSubGroupMaster.getString("SubGroupName");
		    String groupCode = jObjSubGroupMaster.getString("GroupCode");
		    String incentives = jObjSubGroupMaster.getString("Incentives");
		    String user = jObjSubGroupMaster.getString("User");
		    String clientCode = jObjSubGroupMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		    if (subGroupCode.trim().isEmpty())
		    {
		    	//subGroupCode = objSubGroupMasterDao.funGenerateSubGroupCode();
		    	List list=objUtilityFunctions.funGetDocumentCode("POSSubGroupMaster");
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
		                subGroupCode = "SG000000" + intCode;
		            }
		            else if (intCode < 100)
		            {
		                subGroupCode = "SG00000" + intCode;
		            }
		            else if (intCode < 1000)
		            {
		                subGroupCode = "SG0000" + intCode;
		            }
		            else if (intCode < 10000)
		            {
		                subGroupCode = "SG000" + intCode;
		            }
		            else if (intCode < 100000)
		            {
		                subGroupCode = "SG00" + intCode;
		            }
		            else if (intCode < 1000000)
		            {
		                subGroupCode = "SG0" + intCode;
		            }
		        }
		        else
		        {
		            subGroupCode = "SG0000001";
		        }
		    }
		    clsSubGroupMasterHdModel objModel = new clsSubGroupMasterHdModel(new clsSubGroupMasterModel_ID(subGroupCode , clientCode));
		    objModel.setStrSubGroupName(subgroupName);
		    objModel.setStrGroupCode(groupCode);
		   // objModel.setStrOperationalYN(operational);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrAccountCode("NA");
		    objModel.setStrFactoryCode("");
		    objModel.setStrIncentives(incentives);
		    subGroupCode = objSer.funSave(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return subGroupCode;		
	}
	
	@Override
	public JSONObject funGetSubGroupMasterData(String subGroupCode,
			String clientCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObjModGroup=new JSONObject();
		JSONArray jArrData = new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("subGroupCode",subGroupCode);
			hmParameters.put("clientCode",clientCode);
			clsSubGroupMasterHdModel objSubGroupModel = objSubGroupMasterDao.funGetSubGroupMasterData("getSubGroupMaster", hmParameters);
		    jArrData.put(objSubGroupModel.getStrSubGroupCode());
		    jArrData.put(objSubGroupModel.getStrSubGroupName());
		    jArrData.put(objSubGroupModel.getStrGroupCode());
		    jArrData.put(objSubGroupModel.getStrIncentives());
			
		    
			jObjModGroup.put("POSSubGroupMaster", jArrData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjModGroup;
	}

	
	@Override
	public JSONObject funGetAllSubGroupMaster(String clientCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObj=new JSONObject();
        JSONArray jArryObj=new JSONArray();
        try
        {
        	clsSubGroupMasterHdModel objModel=new clsSubGroupMasterHdModel();
        	
        	List objModelList =objSer.funLoadAll(objModel,clientCode);
		
        	for(int i=0; i<objModelList.size();i++)
        	{
        		JSONObject jOb = new JSONObject();
        		objModel = (clsSubGroupMasterHdModel) objModelList.get(i);
        		jOb.put("strSubGroupCode", objModel.getStrSubGroupCode());
        		jOb.put("strSubGroupName", objModel.getStrSubGroupName());
        		jOb.put("strGroupCode", objModel.getStrGroupCode());
        		jOb.put("strUserCreated", objModel.getStrUserCreated());
        		jOb.put("strUserEdited", objModel.getStrUserEdited());
        		jOb.put("dteDateCreated", objModel.getDteDateCreated());
        		jOb.put("dteDateEdited", objModel.getDteDateEdited());
        		jOb.put("strClientCode", objModel.getStrClientCode());
        		jOb.put("strDataPostFlag", objModel.getStrDataPostFlag());
        		jOb.put("strIncentives", objModel.getStrIncentives());
        		jOb.put("strAccountCode", objModel.getStrAccountCode());
        		jOb.put("strFactoryCode", objModel.getStrFactoryCode());
        		jArryObj.put(jOb);
        	}
        	jObj.put("allSGData", jArryObj);
    
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }

        return jObj;
	
	}


}

