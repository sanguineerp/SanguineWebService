package com.apos.service;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsPricingMasterDao;
import com.apos.model.clsGroupMasterModel;
import com.apos.model.clsGroupMasterModel_ID;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.apos.model.clsPricingMasterHdModel;
import com.apos.model.clsPricingMasterModel_ID;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsPricingMasterService")
public class clsPricingMasterService implements intfPricingMasterService
{
	@Autowired
	private clsPricingMasterDao	objPricingMasterDao;
	
	@Autowired
	intfBaseService obBaseService;
	
	@Override
	public String funSaveUpdatePricingMaster(JSONObject jObjPricingMaster)throws Exception
	{
		String strItemCode = "";
		try
		{

			strItemCode = jObjPricingMaster.getString("strItemCode");
			String strItemName = jObjPricingMaster.getString("strItemName");									
			String strPosCode = jObjPricingMaster.getString("strPosCode");				
			String strAreaCode = jObjPricingMaster.getString("strAreaCode");				
			String strHourlyPricing = jObjPricingMaster.getString("strHourlyPricing");					
			
			clsPricingMasterHdModel objModel=null;
			if(jObjPricingMaster.getString("longPricingId").trim().length()==0)
			{
				objModel=new  clsPricingMasterHdModel();
			}
			else
			{
				objModel=new  clsPricingMasterHdModel(new clsPricingMasterModel_ID(Long.parseLong(jObjPricingMaster.getString("longPricingId"))));
			}
			
			objModel.setStrItemCode(strItemCode);			
			objModel.setStrItemName(strItemName);
			objModel.setStrPosCode(strPosCode);
			objModel.setStrAreaCode(strAreaCode);
			objModel.setStrHourlyPricing(strHourlyPricing);
			
			objModel.setStrMenuCode(jObjPricingMaster.getString("strMenuCode"));
			objModel.setStrPopular(jObjPricingMaster.getString("strPopular"));
			objModel.setStrPriceMonday(jObjPricingMaster.getString("strPriceMonday"));
			objModel.setStrPriceTuesday(jObjPricingMaster.getString("strPriceTuesday"));
			objModel.setStrPriceWednesday(jObjPricingMaster.getString("strPriceWednesday"));
			objModel.setStrPriceThursday(jObjPricingMaster.getString("strPriceThursday"));
			objModel.setStrPriceFriday(jObjPricingMaster.getString("strPriceFriday"));
			objModel.setStrPriceSaturday( jObjPricingMaster.getString("strPriceSaturday"));
			objModel.setStrPriceSunday(jObjPricingMaster.getString("strPriceSunday"));			
			objModel.setDteFromDate(jObjPricingMaster.getString("dteFromDate"));
			objModel.setDteToDate(jObjPricingMaster.getString("dteToDate"));
			objModel.setTmeTimeFrom(jObjPricingMaster.getString("tmeTimeFrom"));
			objModel.setTmeTimeTo(jObjPricingMaster.getString("tmeTimeTo"));	
			objModel.setStrAMPMFrom(jObjPricingMaster.getString("strAMPMFrom"));			
			objModel.setStrAMPMTo(jObjPricingMaster.getString("strAMPMTo"));
			objModel.setStrCostCenterCode( jObjPricingMaster.getString("strCostCenterCode"));
			objModel.setStrTextColor(jObjPricingMaster.getString("strTextColor"));
			objModel.setStrUserCreated(jObjPricingMaster.getString("strUserCreated"));
			objModel.setStrUserEdited(jObjPricingMaster.getString("strUserEdited"));
			objModel.setDteDateCreated(jObjPricingMaster.getString("dteDateCreated"));
			objModel.setDteDateEdited(jObjPricingMaster.getString("dteDateEdited"));			
			objModel.setStrSubMenuHeadCode(jObjPricingMaster.getString("strSubMenuHeadCode"));	
			
			
			//objModel.setLongPricingId(Long.parseLong(jObjPricingMaster.getString("longPricingId")));
			
			strItemCode = obBaseService.funSave(objModel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return strItemCode;
	}
	@Override
	public String funCheckDuplicateItemPricing(JSONObject jObjPricingMaster)throws Exception
	{
		String isDuplicate = "false";
		try
		{
			isDuplicate = objPricingMasterDao.funCheckDuplicateItemPricing(jObjPricingMaster);
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return isDuplicate;
	}
	
	@Override
	public JSONObject  funGetMenuheadDtlForPromotionMaster(String menuCode)throws Exception
	{
		return objPricingMasterDao.funGetMenuheadDtlForPromotionMaster(menuCode);
	}
	
	@Override
	public JSONObject funGetMenuItemPricingMaster(String strPricingId,String clientCode) throws Exception {
		// TODO Auto-generated method stub
		JSONObject jObjMenuItemPricing=new JSONObject();
		JSONArray jArrData = new JSONArray();
		try
		{
			long pricingId=Long.parseLong(strPricingId);
			// we cant create map of long type so here directly pass the parameter(criteria) value to dao function ..not in map
			//Map<String,long> hmParameters=new HashMap<String,long>();
			//hmParameters.put("longPricingId",pricingId);
		//	hmParameters.put("clientCode",clientCode);
			
			clsPricingMasterHdModel objPricingMasterHdModel = objPricingMasterDao.funGetMenuItemPricingMaster("getMenuItemPricing", pricingId);
			
			 	jArrData.put(objPricingMasterHdModel.getStrItemCode());
			    jArrData.put(objPricingMasterHdModel.getStrItemName());
			    jArrData.put(objPricingMasterHdModel.getStrPosCode());
			    jArrData.put(objPricingMasterHdModel.getStrMenuCode());
			    jArrData.put(objPricingMasterHdModel.getStrPopular());
			    jArrData.put(objPricingMasterHdModel.getStrPriceMonday());
			    jArrData.put(objPricingMasterHdModel.getStrPriceTuesday());
			    jArrData.put(objPricingMasterHdModel.getStrPriceWednesday());
			    jArrData.put(objPricingMasterHdModel.getStrPriceThursday());
			    jArrData.put(objPricingMasterHdModel.getStrPriceFriday());
			    jArrData.put(objPricingMasterHdModel.getStrPriceSaturday());
			    jArrData.put(objPricingMasterHdModel.getStrPriceSunday());
			    jArrData.put(objPricingMasterHdModel.getDteFromDate());
			    jArrData.put(objPricingMasterHdModel.getDteToDate());
			    jArrData.put(objPricingMasterHdModel.getTmeTimeFrom());
			    jArrData.put(objPricingMasterHdModel.getStrAMPMFrom());
			    jArrData.put(objPricingMasterHdModel.getTmeTimeTo());
			    jArrData.put(objPricingMasterHdModel.getStrAMPMTo());
			    jArrData.put(objPricingMasterHdModel.getStrCostCenterCode());
			    jArrData.put(objPricingMasterHdModel.getStrTextColor());
			    jArrData.put(objPricingMasterHdModel.getStrUserCreated());
			    jArrData.put(objPricingMasterHdModel.getStrUserEdited());
			    jArrData.put(objPricingMasterHdModel.getDteDateCreated());
			    jArrData.put(objPricingMasterHdModel.getDteDateEdited());
			    jArrData.put(objPricingMasterHdModel.getStrAreaCode());
			    jArrData.put(objPricingMasterHdModel.getStrSubMenuHeadCode());
			    jArrData.put(objPricingMasterHdModel.getStrHourlyPricing());
			    jArrData.put(objPricingMasterHdModel.getLongPricingId());	
		    
			jObjMenuItemPricing.put("POSMenuItemPricingMaster", jArrData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMenuItemPricing;
		
	}
	
	
}
