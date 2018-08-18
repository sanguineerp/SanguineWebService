package com.apos.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;







import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsFactoryMasterHdModel;
import com.apos.model.clsFactoryMasterModel_ID;
import com.apos.dao.clsFactoryMasterDao;
import com.apos.dao.intfCostCenterMasterDao;
import com.apos.dao.intfFactoryMasterDao;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsFactoryMasterServiceImpl")
public class clsFactoryMasterServiceImpl implements clsFactoryMasterService
{
	
	@Autowired
	private intfFactoryMasterDao objFactoryMasterDao ;

	@Autowired
	private intfBaseDao objDao;
	
	@Autowired
	clsUtilityFunctions utility;
	
	public String funSaveUpdateFactoryMaster(JSONObject jObjFactoryMaster)
	{
		String factoryCode = "";
		try
		{

			factoryCode = jObjFactoryMaster.getString("FactoryCode");
			String factoryName = jObjFactoryMaster.getString("FactoryName");
			String user = jObjFactoryMaster.getString("User");
			String clientCode = jObjFactoryMaster.getString("ClientCode");
			String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");

			if (factoryCode.trim().isEmpty())
			{
				List list=utility.funGetDocumentCode("POSFactoryMaster");
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

						factoryCode = "F00000" + intCode;

						
					}
				    else
				    {
				    	factoryCode = "F000001";
				    }
				
			}

			clsFactoryMasterHdModel objModel = new clsFactoryMasterHdModel(new clsFactoryMasterModel_ID(factoryCode, clientCode));
			objModel.setStrFactoryName(factoryName);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrDataPostFlag("");
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);

			 objDao.funSave(objModel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return factoryCode;
	}
	
	public JSONObject funGetSelectedFactoryMasterData(String factoryCode,String clientCode)
	{
		JSONObject jObjFactoryMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("factoryCode",factoryCode);
			hmParameters.put("clientCode",clientCode);
			clsFactoryMasterHdModel model = objFactoryMasterDao.funGetSelectedFactoryMasterData("getFactoryMaster", hmParameters);
			
			// Write code to convert model into json object.
			jObjFactoryMaster.put("strFactoryCode",model.getStrFactoryCode());
			jObjFactoryMaster.put("strFactoryName",model.getStrFactoryName());
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjFactoryMaster;
	}


}
