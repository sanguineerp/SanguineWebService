

package com.apos.service;



import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfAdvanceOrderTypeMasterDao;
import com.apos.model.clsAdvanceOrderMasterModel;
import com.apos.model.clsAdvanceOrderMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsAdvanceOrderTypeMasterService")
public class clsAdvanceOrderTypeMasterServiceImpl implements clsAdvanceOrderTypeMasterService
{
	@Autowired
	intfAdvanceOrderTypeMasterDao	objAdvOrderMasterDao;

	@Autowired
	private intfBaseDao objDao;
	
	@Autowired
	private clsUtilityFunctions utility;
	
	public String funSaveUpdateAdvOrderMaster(JSONObject jObjAreaMaster)
	{
		String advOrderCode = "";
		try
		{

			advOrderCode = jObjAreaMaster.getString("AdvOrderCode");
			String advOrderName = jObjAreaMaster.getString("AdvOrderName");
			String operational = jObjAreaMaster.getString("Operational");
			String strPosCode = jObjAreaMaster.getString("strPosCode");
			String user = jObjAreaMaster.getString("User");
			String clientCode = jObjAreaMaster.getString("ClientCode");
			String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");

			if (advOrderCode.trim().isEmpty())
			{
				
					long intCode =utility.funGetDocumentCodeFromInternal("AdvOrderType");
					intCode++;
					if (intCode < 10)
					{
						advOrderCode = "AT00" + intCode;
					}
					else if (intCode < 100)
					{
						advOrderCode = "AT0" + intCode;
					}
					else if (intCode < 1000)
					{
						advOrderCode = "AT" + intCode;
					}
				
				else
				{
					advOrderCode = "AT001";
				}
			
			}
			clsAdvanceOrderMasterModel objModel = new clsAdvanceOrderMasterModel(new clsAdvanceOrderMasterModel_ID(advOrderCode, clientCode));
			objModel.setStrAdvOrderTypeName(advOrderName);
			objModel.setStrOperational(operational);
			objModel.setStrPOSCode(strPosCode);
			
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

		return advOrderCode;
	}

	@Override
	public JSONObject funSelectedAdvOrderMasterData(String advOrderCode,String clientCode)
	{
		JSONObject jObjMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("advOrderCode",advOrderCode);
			hmParameters.put("clientCode",clientCode);
			clsAdvanceOrderMasterModel objOrderModel = objAdvOrderMasterDao.funGetAdvOrderMasterData("getAdvOrderMaster", hmParameters);
			
			jObjMaster.put("strAdvOrderCode",objOrderModel.getStrAdvOrderTypeCode());
			jObjMaster.put("strAdvOrderDesc",objOrderModel.getStrAdvOrderTypeName());
			jObjMaster.put("strOperational",objOrderModel.getStrOperational());
			jObjMaster.put("strPOSCode",objOrderModel.getStrPOSCode());
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMaster;
	}


}
