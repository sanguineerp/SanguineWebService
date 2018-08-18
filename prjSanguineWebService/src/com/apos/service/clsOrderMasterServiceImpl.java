
package com.apos.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfOrderMasterDao;
import com.apos.model.clsBuyPromotionDtlHdModel;
import com.apos.model.clsGetPromotionDtlHdModel;
import com.apos.model.clsOrderMasterModel;
import com.apos.model.clsOrderMasterModel_ID;
import com.apos.model.clsPOSPromationMasterHdModel;
import com.apos.model.clsPromotionDayTimeDtlHdModel;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsOrderMasterService")
public class clsOrderMasterServiceImpl implements clsOrderMasterService
{
	@Autowired
	intfOrderMasterDao	objOrderMasterDao;

	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;
	public String funAddUpdatePOSOrderMaster(JSONObject jObjAreaMaster)
	{
		String orderCode = "";
		try
		{

			orderCode = jObjAreaMaster.getString("OrderCode");
			String strOrderDesc = jObjAreaMaster.getString("OrderDesc");
			String uptoTime = jObjAreaMaster.getString("uptoTime");
			String posCode = jObjAreaMaster.getString("POSCode");
			String user = jObjAreaMaster.getString("User");
			String clientCode = jObjAreaMaster.getString("ClientCode");
			String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");

			if (orderCode.trim().isEmpty())
			{
				
					long intCode =utility.funGetDocumentCodeFromInternal("Order");
					intCode++;
					if (intCode < 10)
					{
						orderCode = "OR00000" + intCode;
					}
					else if (intCode < 100)
					{
						orderCode = "OR0000" + intCode;
					}
					else if (intCode < 1000)
					{
						orderCode = "OR000" + intCode;
					}
					else if (intCode < 10000)
					{
						orderCode = "OR00" + intCode;
					}
					else if (intCode < 100000)
					{
						orderCode = "OR0" + intCode;
					}
					else if (intCode < 1000000)
					{
						orderCode = "OR" + intCode;
					}
				
				else
				{
					orderCode = "OR000001";
				}
			}

			clsOrderMasterModel objModel = new clsOrderMasterModel(new clsOrderMasterModel_ID(orderCode, clientCode));
			objModel.setStrOrderDesc(strOrderDesc);
			objModel.setTmeUpToTime(uptoTime);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrDataPostFlag("");
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);
			objModel.setStrPOSCode(posCode);
			
			objService.funSave(objModel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return orderCode;
	}

	@Override
	public JSONObject funSelectedOrderMasterData(String orderCode,String clientCode)
	{
		JSONObject jObjMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("orderCode",orderCode);
			hmParameters.put("clientCode",clientCode);
			clsOrderMasterModel objOrderModel = objOrderMasterDao.funGetOrderMasterData("getOrderMaster", hmParameters);
			
			jObjMaster.put("strOrderCode",objOrderModel.getStrOrderCode());
			jObjMaster.put("strOrderDesc",objOrderModel.getStrOrderDesc());
			jObjMaster.put("tmeUpToTime",objOrderModel.getTmeUpToTime());
			jObjMaster.put("strPOSCode",objOrderModel.getStrPOSCode());
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMaster;
	}

}
