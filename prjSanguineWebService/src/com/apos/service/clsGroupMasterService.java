package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfGroupMasterDao;
import com.apos.model.clsGroupMasterModel;
import com.apos.model.clsGroupMasterModel_ID;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service(value = "clsGroupMasterService")
public class clsGroupMasterService implements intfGroupMasterService
{
    
    @Autowired
    intfGroupMasterDao objGroupMasterDao;
    @Autowired
	private intfBaseService objSer;
    @Autowired
	clsUtilityFunctions objUtilityFunctions;
    public String funSaveGroupMaster(JSONObject jObjGroupMaster)
    {
	String groupCode = "";
	try
	{
	    
	    groupCode = jObjGroupMaster.getString("GroupCode");
	    String groupName = jObjGroupMaster.getString("GroupName");
	    String operational = jObjGroupMaster.getString("Operational");
	    String user = jObjGroupMaster.getString("User");
	    String clientCode = jObjGroupMaster.getString("ClientCode");
	    String shortName=jObjGroupMaster.getString("ShortName");
	    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
	    if (groupCode.trim().isEmpty())
	    {
	    	//groupCode = objGroupMasterDao.funGenerateGroupCode();
	    	List list=objUtilityFunctions.funGetDocumentCode("POSGroupMaster");
	    	if(list.size()>0)
	    	{
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
				    	groupCode = "G000000" + intCode;
				    }
				    else if (intCode < 100)
				    {
				    	groupCode = "G00000" + intCode;
				    }
				    else if (intCode < 1000)
				    {
				    	groupCode = "G0000" + intCode;
				    }
				    else if (intCode < 10000)
				    {
				    	groupCode = "G000" + intCode;
				    }
				    else if (intCode < 100000)
				    {
				    	groupCode = "G00" + intCode;
				    }
				    else if (intCode < 1000000)
				    {
				    	groupCode = "G0" + intCode;
				    }
				}
				else
				{
				    groupCode = "G0000001";
				}
	    	}
	    	else
			{
			    groupCode = "G0000001";
			}
	    	
	    }
	    clsGroupMasterModel objModel = new clsGroupMasterModel(new clsGroupMasterModel_ID(groupCode, clientCode));
	    objModel.setStrGroupName(groupName);
	    objModel.setStrOperationalYN(operational);
	    objModel.setStrGroupShortName(shortName);
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
	return groupCode;
    }

    
    public List<clsGroupMasterModel> funGetAllGroup(String strClientCode){

		return objGroupMasterDao.funGetAllGroup(strClientCode);
		
	}

	@Override
	public JSONObject funGetGroupMasterDtl(String groupCode, String clientCode)
			throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObjModGroup=new JSONObject();
		JSONArray jArrData = new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("groupCode",groupCode);
			hmParameters.put("clientCode",clientCode);
			
			clsGroupMasterModel  objModel = objGroupMasterDao.funGetGroupMasterDtl("getGroupMaster", hmParameters);
		
				
			    jArrData.put(objModel.getStrGroupCode());
			    jArrData.put(objModel.getStrGroupName());
			    jArrData.put(objModel.getStrOperationalYN());
			    jArrData.put(objModel.getStrGroupShortName());
		
			    jObjModGroup.put("POSGroupMaster", jArrData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjModGroup;
		
	}
    
}
