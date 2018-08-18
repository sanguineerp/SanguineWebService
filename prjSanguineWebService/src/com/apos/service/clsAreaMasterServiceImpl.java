package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfAreaMasterDao;
import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsAreaMasterModel_ID;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsAreaMasterServiceImpl")
public class clsAreaMasterServiceImpl implements clsAreaMasterService
{
	@Autowired
	intfAreaMasterDao	objAreaMasterDao;

	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;
	public String funSaveUpdateAreaMaster(JSONObject jObjAreaMaster)
	{
		String areaCode = "";
		try
		{

			areaCode = jObjAreaMaster.getString("AreaCode");
			String areaName = jObjAreaMaster.getString("AreaName");
			String posName = jObjAreaMaster.getString("POSName");
			String user = jObjAreaMaster.getString("User");
			String clientCode = jObjAreaMaster.getString("ClientCode");
			String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");

			if (areaCode.trim().isEmpty())
			{
				long intCode =utility.funGetDocumentCodeFromInternal("Area");
						intCode++;
						if (intCode < 10)
						{
							areaCode = "A00" + intCode;
						}
						else if (intCode < 100)
						{
							areaCode = "A0" + intCode;
						}
						else if (intCode < 1000)
						{
							areaCode = "A" + intCode;
						}
						else
						{
							areaCode = "A001";
						}
				
			}

			clsAreaMasterModel objModel = new clsAreaMasterModel(new clsAreaMasterModel_ID(areaCode, clientCode));
			objModel.setStrAreaName(areaName);
			objModel.setStrPOSCode(posName);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrDataPostFlag("");
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);

			objService.funSave(objModel);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return areaCode;
	}

	@Override
	public JSONObject funSelectedAreaMasterData(String areaCode,String clientCode)
	{
		JSONObject jObjMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("areaCode",areaCode);
			hmParameters.put("clientCode",clientCode);
			clsAreaMasterModel objOrderModel = objAreaMasterDao.funGetAreaMasterData("getAreaMaster", hmParameters);
			
			jObjMaster.put("strAreaCode",objOrderModel.getStrAreaCode());
			jObjMaster.put("strAreaName",objOrderModel.getStrAreaName());
		
			jObjMaster.put("strPOSCode",objOrderModel.getStrPOSCode());
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMaster;
	}
	public String funGetAllAreaForMaster(String clientCode)
	{
		clsAreaMasterModel model =new clsAreaMasterModel();
		JSONObject jObj = new JSONObject();
	try{
		JSONArray jArrData=new JSONArray();
		 
		List list=objService.funLoadAll(model,clientCode);
		clsAreaMasterModel objAreaModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objAreaModel= (clsAreaMasterModel) list.get(cnt);
			    
			    JSONObject jArrDataRow = new JSONObject();
			    jArrDataRow.put("strAreaCode",objAreaModel.getStrAreaCode());
			    jArrDataRow.put("strAreaName",objAreaModel.getStrAreaName());
			   
			    jArrData.put(jArrDataRow);
			}
			jObj.put("AreaList", jArrData);
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}

	return jObj.toString();
	}
	


}
