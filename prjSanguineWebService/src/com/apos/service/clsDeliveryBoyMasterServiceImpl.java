package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfDeliveryBoyMasterDao;
import com.apos.model.clsDeliveryBoyMasterModel;
import com.apos.model.clsDeliveryBoyChargesModel;
import com.apos.model.clsDeliveryBoyMasterModel_ID;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsDeliveryBoyMasterServiceImpl")
public class clsDeliveryBoyMasterServiceImpl implements clsDeliveryBoyMasterService{
	@Autowired
	private intfDeliveryBoyMasterDao objDeliveryBoyMasterDao;
	
	

	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;
	
	@Override
	public String funAddUpdateDeliveryBoyMaster(JSONObject jObjAreaMaster)
	{
		String dpCode = "";
		try
		{

			dpCode = jObjAreaMaster.getString("DPCode");
			String dpName = jObjAreaMaster.getString("DPName");
			String strOperational = jObjAreaMaster.getString("Operational");
			String user = jObjAreaMaster.getString("User");
			String clientCode = jObjAreaMaster.getString("ClientCode");
			String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");

			if (dpCode.trim().isEmpty())
			{
				List list=utility.funGetDocumentCode("POSDeliveryBoyMaster");
				
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
							dpCode = "DB00000" + intCode;
						}
						else if (intCode < 100)
						{
							dpCode = "DB0000" + intCode;
						}
						else if (intCode < 1000)
						{
							dpCode = "DB000" + intCode;
						}
						else if (intCode < 10000)
						{
							dpCode = "DB00" + intCode;
						}
						else if (intCode < 100000)
						{
							dpCode = "DB0" + intCode;
						}
						else if (intCode < 1000000)
						{
							dpCode = "DB" + intCode;
						}
					}
					else
					{
						dpCode = "DB000001";
					}
			}

			clsDeliveryBoyMasterModel objModel = new clsDeliveryBoyMasterModel(new clsDeliveryBoyMasterModel_ID(dpCode, clientCode));
			objModel.setStrOperational(strOperational);
			objModel.setStrDPName(dpName);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrDataPostFlag("");
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);
			objModel.setStrAddressLine1("");
			objModel.setStrAddressLine2("");
			objModel.setStrAddressLine3("");
			objModel.setStrCity("");
			objModel.setStrDBCategoryCode("");
			objModel.setStrDeliveryArea("");
			objModel.setStrState("");
			
			 JSONArray delBoyChargesList=jObjAreaMaster.getJSONArray("DeliveryBoyCharges");
		    List<clsDeliveryBoyChargesModel> listDeliveryChargesDtl = new ArrayList<clsDeliveryBoyChargesModel>();
		    for(int i=0;i<delBoyChargesList.length();i++)
		    {
		    	JSONObject jObj = new JSONObject();
		    	jObj=delBoyChargesList.getJSONObject(i);
		    	
		    	String areaCode=jObj.getString("AreaCode");
		    	
		    	double dblValue=jObj.getDouble("Incentives");
		    	clsDeliveryBoyChargesModel objDelChargesModel = new clsDeliveryBoyChargesModel();
		    	objDelChargesModel.setDblValue(dblValue);
		    	objDelChargesModel.setStrCustAreaCode(areaCode);
		    	objDelChargesModel.setStrUserCreated(user);
		    	objDelChargesModel.setStrUserEdited(user);
		    	objDelChargesModel.setDteDateCreated(dateTime);
		    	objDelChargesModel.setDteDateEdited(dateTime);
		    	objDelChargesModel.setStrDataPostFlag("Y");
		    	listDeliveryChargesDtl.add(objDelChargesModel);
		    
		    }
		    
		    objModel.setListDeliveryChargesDtl(listDeliveryChargesDtl);
		    objService.funSave(objModel);
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return dpCode;
	}

	@Override
public JSONObject funSelectedDeliveryBoyMasterData(String dpCode,String clientCode)
{
	JSONObject jObjMaster=new JSONObject();
	
	try
	{
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("dpCode",dpCode);
		hmParameters.put("clientCode",clientCode);
		clsDeliveryBoyMasterModel objDelBoyMasterModel = objDeliveryBoyMasterDao.funGetDeliveryBoyMasterData("getDeliveryBoyMaster", hmParameters);
		
		List<clsDeliveryBoyChargesModel> listDeliveryChargesDtl =objDelBoyMasterModel.getListDeliveryChargesDtl();
		
		JSONArray jSettleData=new JSONArray();
		for(int i=0; i<listDeliveryChargesDtl.size(); i++)
			
		{
			clsDeliveryBoyChargesModel objSettle=(clsDeliveryBoyChargesModel)listDeliveryChargesDtl.get(i);
			JSONObject jObjSettle=new JSONObject();
			jObjSettle.put("AreaCode",objSettle.getStrCustAreaCode());
		
			jObjSettle.put("Incentives",objSettle.getDblValue());
			jSettleData.put(jObjSettle);
			
		}
		
		jObjMaster.put("strDPCode",objDelBoyMasterModel.getStrDPCode());
		jObjMaster.put("strDPName",objDelBoyMasterModel.getStrDPName());
		jObjMaster.put("strOperational",objDelBoyMasterModel.getStrOperational());
		jObjMaster.put("SettleData",jSettleData);
	
		
		
		System.out.println();
	}catch(Exception ex)
	{
		ex.printStackTrace();
	}
	
	return jObjMaster;
}

public JSONObject funGetAllDeliveryBoyMaster(String clientCode)
{
	clsDeliveryBoyMasterModel model =new clsDeliveryBoyMasterModel();
	JSONObject jObj = new JSONObject();
try{
	JSONArray jArrData=new JSONArray();
	 
	List list=objService.funLoadAll(model,clientCode);
	clsDeliveryBoyMasterModel objTaxModel = null;
		for(int cnt=0;cnt<list.size();cnt++)
		{
			objTaxModel= (clsDeliveryBoyMasterModel) list.get(cnt);
		    
		    JSONObject jArrDataRow = new JSONObject();
		    jArrDataRow.put("DBCode",objTaxModel.getStrDPCode());
		    jArrDataRow.put("DBName",objTaxModel.getStrDPName());
		   
		    jArrData.put(jArrDataRow);
		}
		jObj.put("DeliveryBoy", jArrData);
}
catch(Exception ex)
{
	ex.printStackTrace();
}

return jObj;
}

}

