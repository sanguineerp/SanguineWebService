
package com.apos.service;
import java.util.ArrayList;
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

import com.apos.dao.clsMenuHeadMasterDao;
import com.apos.dao.clsMenuItemMasterDao;
import com.apos.dao.intfPromotionMasterDao;
import com.apos.model.clsBuyPromotionDtlHdModel;
import com.apos.model.clsGetPromotionDtlHdModel;
import com.apos.model.clsMenuHeadMasterModel;
import com.apos.model.clsMenuItemMasterModel;
import com.apos.model.clsPOSPromationMasterHdModel;
import com.apos.model.clsPOSPromationMasterModel_ID;
import com.apos.model.clsPromotionDayTimeDtlHdModel;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service(value = "clsPromotionMasterService")

public class clsPromotionMasterServiceImpl implements clsPromotionMasterService{
	@Autowired
	private intfPromotionMasterDao objPromotionMasterDao;
	
	@Autowired
	private intfBaseService objService;
	
	@Autowired
	clsMenuHeadMasterDao objMenuHeadMasterDao;
	
	@Autowired
	clsMenuItemMasterDao objMenuItemMasterDao;
	
	@Autowired
	private clsUtilityFunctions utility;
	public String funAddUpdatePromotionMaster(JSONObject jObjTaxMaster){
		String strPromoCode = "";
		try
		{
		    
			strPromoCode = jObjTaxMaster.getString("strPromoCode");
			String strPromoName = jObjTaxMaster.getString("strPromoName");
		    String dteFromDate = jObjTaxMaster.getString("dteFromDate");
		    String dteToDate = jObjTaxMaster.getString("dteToDate");
		    
		    String strPromotionOn = jObjTaxMaster.getString("strPromotionOn");
		    String strPromoItemCode = jObjTaxMaster.getString("strPromoItemCode");
		    String strType = jObjTaxMaster.getString("strType");
		    String strOperator = jObjTaxMaster.getString("strOperator");
		    double dblBuyQty = jObjTaxMaster.getDouble("dblBuyQty");
		    String strGetPromoOn = jObjTaxMaster.getString("strGetPromoOn");
		    String strGetItemCode = jObjTaxMaster.getString("strGetItemCode");
		    double dblGetQty = jObjTaxMaster.getDouble("dblGetQty");
		    String strDiscountType = jObjTaxMaster.getString("strDiscountType");
		    double dblDiscount = jObjTaxMaster.getDouble("dblDiscount");
		    String posCode = jObjTaxMaster.getString("posCode");
		    String areaCode = jObjTaxMaster.getString("areaCode");
		    String strPromoNote = jObjTaxMaster.getString("strPromoNote");
		 
		   
		    String user = jObjTaxMaster.getString("User");
		    String clientCode = jObjTaxMaster.getString("ClientCode");
		    String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");
	    
		    if (strPromoCode.trim().isEmpty())
		    {
		    	long intCode =utility.funGetDocumentCodeFromInternal("PromotionCode");
						intCode++;
						if (intCode < 10)
						{
							strPromoCode = "PM00" + intCode;
						}
						else if (intCode < 100)
						{
							strPromoCode = "PM0" + intCode;
						}
						else if (intCode < 1000)
						{
							strPromoCode = "PM" + intCode;
						}
					
						else
						{
							strPromoCode = "PM001";
						}

		    }
		    
		    clsPOSPromationMasterHdModel objModel=new clsPOSPromationMasterHdModel(new clsPOSPromationMasterModel_ID(strPromoCode,clientCode));
		   
		    objModel.setDblBuyQty(dblBuyQty);
		    objModel.setDteFromDate(dteFromDate);
		    objModel.setDteToDate(dteToDate);
		    objModel.setStrDays("");
		    objModel.setStrGetItemCode(strGetItemCode);
		    objModel.setStrGetPromoOn(strGetPromoOn);
		    objModel.setStrOperator(strOperator);
		    objModel.setStrPOSCode(posCode);
		    objModel.setStrPromoItemCode(strPromoItemCode);
		    objModel.setStrPromoName(strPromoName);
		    objModel.setStrPromoNote(strPromoNote);
		    objModel.setStrPromotionOn(strPromotionOn);
		    objModel.setStrType(strType);
		    objModel.setTmeFromTime("");
		    objModel.setTmeToTime("");
		    objModel.setStrDataPostFlag("Y");
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrAreaCode(areaCode);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    
		 //   objPromotionMasterDao.funAddUpdatePromotionMaster(objModel);
		    
		    // save Buy Item Details
		    Set<clsBuyPromotionDtlHdModel> listBuyPromotionDtl = new HashSet<clsBuyPromotionDtlHdModel>();
			 
		    if(strPromotionOn.equalsIgnoreCase("Item"))
		    {
		    	
		    	clsBuyPromotionDtlHdModel objBuyPromoModel = new clsBuyPromotionDtlHdModel();
		    
		    	objBuyPromoModel.setStrBuyPromoItemCode(strPromoItemCode);
		    	objBuyPromoModel.setDblBuyItemQty(dblBuyQty);
		    	objBuyPromoModel.setStrDataPostFlag("");
		    	objBuyPromoModel.setStrOperator(strOperator);
		    	listBuyPromotionDtl.add(objBuyPromoModel);
		    	//objBuyItems.funSaveBuyItemDetails(strPromoItemCode,strPromoCode,dblBuyQty,strOperator,clientCode);
		    }
		    else if(strPromotionOn.equalsIgnoreCase("MenuHead"))
		    {
		    JSONArray settleList=jObjTaxMaster.getJSONArray("BuyItemDetails");
		   
		    for(int i=0;i<settleList.length();i++)
		    {
		    	JSONObject jObj = new JSONObject();
		    	jObj=settleList.getJSONObject(i);
		    	String strItemCode=jObj.getString("strItemCode");
		    	
		    	clsBuyPromotionDtlHdModel objBuyPromoModel = new clsBuyPromotionDtlHdModel();
			    
		    	objBuyPromoModel.setStrBuyPromoItemCode(strItemCode);
		    	objBuyPromoModel.setDblBuyItemQty(dblBuyQty);
		    	objBuyPromoModel.setStrDataPostFlag("");
		    	objBuyPromoModel.setStrOperator(strOperator);
		    	listBuyPromotionDtl.add(objBuyPromoModel);
		    	
		    	
		    }
		    
		   // objBuyItems.funSaveBuyItemsDetails(settleList,strPromoCode,dblBuyQty,strOperator,clientCode);
		    }
		    
		    objModel.setListBuyPromotionDtl(listBuyPromotionDtl);
		    //Save Get Item Details
		    Set<clsGetPromotionDtlHdModel> listGetPromotionDtl = new HashSet<clsGetPromotionDtlHdModel>();
		    if(strGetPromoOn.equalsIgnoreCase("Item"))
		    {
		    	clsGetPromotionDtlHdModel objGetPromoModel = new clsGetPromotionDtlHdModel();
		    	objGetPromoModel.setStrPromoItemCode(strGetItemCode);
		    	objGetPromoModel.setDblGetQty(dblGetQty);
		    	objGetPromoModel.setStrDataPostFlag("");
		    	objGetPromoModel.setDblDiscount(dblDiscount);
		    	objGetPromoModel.setStrDiscountType(strDiscountType);
		    	objGetPromoModel.setStrPromotionOn(strPromotionOn);
		    	
		    	listGetPromotionDtl.add(objGetPromoModel);
		    	 
		    
		    }
		    else if(strGetPromoOn.equalsIgnoreCase("MenuHead"))
		    {
		    
		    JSONArray getItemList=jObjTaxMaster.getJSONArray("GetItemDetails");
		    
		    for(int i=0;i<getItemList.length();i++)
		    {
		    	JSONObject jObj = new JSONObject();
		    	jObj=getItemList.getJSONObject(i);
		    	String strItemCode=jObj.getString("strItemCode");
		    	
		    	clsGetPromotionDtlHdModel objGetPromoModel = new clsGetPromotionDtlHdModel();
		    	objGetPromoModel.setStrPromoItemCode(strItemCode);
		    	objGetPromoModel.setDblGetQty(dblGetQty);
		    	objGetPromoModel.setStrDataPostFlag("");
		    	objGetPromoModel.setDblDiscount(dblDiscount);
		    	objGetPromoModel.setStrDiscountType(strDiscountType);
		    	objGetPromoModel.setStrPromotionOn(strPromotionOn);
		    	
		    	listGetPromotionDtl.add(objGetPromoModel);
		    }
		  
		    }
		    objModel.setListGetPromotionDtl(listGetPromotionDtl);
		    
		    JSONArray dayTimeList=jObjTaxMaster.getJSONArray("dayTimeDetails");
		    
		    Set<clsPromotionDayTimeDtlHdModel> listDayTimeDtl = new HashSet<clsPromotionDayTimeDtlHdModel>();
		    for(int i=0;i<dayTimeList.length();i++)
		    {
		    	JSONObject jObj = new JSONObject();
		    	jObj=dayTimeList.getJSONObject(i);
		    	String strDay=jObj.getString("strDay");
		    	String tmeFromTime=jObj.getString("tmeFromTime");
		    	String tmeToTime=jObj.getString("tmeToTime");
		    	
		    	clsPromotionDayTimeDtlHdModel objPromoDayTimeModel = new clsPromotionDayTimeDtlHdModel();
		    	objPromoDayTimeModel.setStrDay(strDay);
		    	objPromoDayTimeModel.setTmeFromTime(tmeFromTime);
		    	objPromoDayTimeModel.setTmeToTime(tmeToTime);
		    	objPromoDayTimeModel.setStrDataPostFlag("");
		    	listDayTimeDtl.add(objPromoDayTimeModel);
		   }
		    
		    objModel.setListDayTimeDtl(listDayTimeDtl);
		  
		    objService.funSave(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return strPromoCode; 
	    }
	
	public JSONObject funSelectedPromotionMasterData(String promoCode,String clientCode)
	{
		JSONObject jObjTaxMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("promoCode",promoCode);
			hmParameters.put("clientCode",clientCode);
			clsPOSPromationMasterHdModel objPromoModel = objPromotionMasterDao.funGetPromotionMasterData("getPromotionMaster", hmParameters);
			
			Set<clsBuyPromotionDtlHdModel> listBuyPromotionDtl =objPromoModel.getListBuyPromotionDtl();
			
			JSONArray jBuyData=new JSONArray();
			Iterator itr = listBuyPromotionDtl.iterator();
	        while(itr.hasNext())
	        {
	        	clsBuyPromotionDtlHdModel objSettle=(clsBuyPromotionDtlHdModel)itr.next();
					JSONObject jObjSettle=new JSONObject();
					jObjSettle.put("buyPromoItemCode",objSettle.getStrBuyPromoItemCode());
					
					jBuyData.put(jObjSettle);
					
				}
				Set<clsGetPromotionDtlHdModel> listGetPromotionDtl = objPromoModel.getListGetPromotionDtl();
				 JSONArray jGetData=new JSONArray();
				  itr = listGetPromotionDtl.iterator();
			        while(itr.hasNext())
			        {
						clsGetPromotionDtlHdModel objSettle=(clsGetPromotionDtlHdModel)itr.next();
						JSONObject jObjSettle=new JSONObject();
						jObjSettle.put("GetPromoItemCode",objSettle.getStrPromoItemCode());
						jObjSettle.put("GetQty",objSettle.getDblGetQty());
						jObjSettle.put("Discount",objSettle.getDblDiscount());
						jObjSettle.put("DiscountType",objSettle.getStrDiscountType());
						jGetData.put(jObjSettle);
						
					}
					Set<clsPromotionDayTimeDtlHdModel> listDayTimeDtl =objPromoModel.getListDayTimeDtl();
					 JSONArray jTimeData=new JSONArray();
					 itr = listDayTimeDtl.iterator();
				        while(itr.hasNext())
				        {
							clsPromotionDayTimeDtlHdModel objSettle=(clsPromotionDayTimeDtlHdModel)itr.next();
							JSONObject jObjSettle=new JSONObject();
							jObjSettle.put("Day",objSettle.getStrDay());
							jObjSettle.put("FromTime",objSettle.getTmeFromTime());
							jObjSettle.put("ToTime",objSettle.getTmeToTime());
							
							jTimeData.put(jObjSettle);
							
						}
				
				        jObjTaxMaster.put("strPromoCode",objPromoModel.getStrPromoCode());
				        jObjTaxMaster.put("strPromoName",objPromoModel.getStrPromoName()); 
				        jObjTaxMaster.put("strPOSCode",objPromoModel.getStrPOSCode());
				        jObjTaxMaster.put("dteFromDate",objPromoModel.getDteFromDate());
				        jObjTaxMaster.put("dteToDate",objPromoModel.getDteToDate());
		   
				        jObjTaxMaster.put("strPromotionOn",objPromoModel.getStrPromotionOn());
				        if(objPromoModel.getStrPromotionOn().equalsIgnoreCase("MenuHead"))
				        {
				        	hmParameters=new HashMap<String,String>();
				        	hmParameters.put("menuCode",objPromoModel.getStrPromoItemCode());
				        	hmParameters.put("clientCode",clientCode);
				        	clsMenuHeadMasterModel model = objMenuHeadMasterDao.funGetMenuHeadMasterData("getMenuHeadMaster", hmParameters);
					
				        	jObjTaxMaster.put("strPromoItemCode",objPromoModel.getStrPromoItemCode());
				        	jObjTaxMaster.put("strPromoItemName",model.getStrMenuName());
				        }
				        else
				        {
				        	hmParameters=new HashMap<String,String>();
				 			hmParameters.put("itemCode",objPromoModel.getStrPromoItemCode());
				 			hmParameters.put("clientCode",clientCode);
				 			
				 			clsMenuItemMasterModel objMenuItemMasterModel = objMenuItemMasterDao.funGetMenuItemMasterData("getMenuItemMaster", hmParameters);
				 			 
				 			jObjTaxMaster.put("strPromoItemCode",objPromoModel.getStrPromoItemCode());
				        	jObjTaxMaster.put("strPromoItemName",objMenuItemMasterModel.getStrItemName());
				        }
				        jObjTaxMaster.put("strType",objPromoModel.getStrType());
				        jObjTaxMaster.put("strOperator",objPromoModel.getStrOperator());
				        jObjTaxMaster.put("dblBuyQty",objPromoModel.getDblBuyQty());
				        jObjTaxMaster.put("strGetPromoOn",objPromoModel.getStrGetPromoOn());
				        jObjTaxMaster.put("areaCode",objPromoModel.getStrAreaCode());
				        if(objPromoModel.getStrGetPromoOn().equalsIgnoreCase("MenuHead"))
				        {
				        	hmParameters=new HashMap<String,String>();
				        	hmParameters.put("menuCode",objPromoModel.getStrGetItemCode());
				        	hmParameters.put("clientCode",clientCode);
				        	clsMenuHeadMasterModel model = objMenuHeadMasterDao.funGetMenuHeadMasterData("getMenuHeadMaster", hmParameters);
					
				        	jObjTaxMaster.put("strGetItemCode",objPromoModel.getStrGetItemCode());
				        	jObjTaxMaster.put("strGetItemName",model.getStrMenuName());
				        }
				        else
				        {
				        	hmParameters=new HashMap<String,String>();
				 			hmParameters.put("itemCode",objPromoModel.getStrGetItemCode());
				 			hmParameters.put("clientCode",clientCode);
				 			
				 			clsMenuItemMasterModel objMenuItemMasterModel = objMenuItemMasterDao.funGetMenuItemMasterData("getMenuItemMaster", hmParameters);
				 			 
				 			jObjTaxMaster.put("strGetItemCode",objPromoModel.getStrGetItemCode());
				        	jObjTaxMaster.put("strGetItemName",objMenuItemMasterModel.getStrItemName());
				        }
				        jObjTaxMaster.put("strPromoNote",objPromoModel.getStrPromoNote());
		   
				        jObjTaxMaster.put("BuyData",jBuyData);
				        jObjTaxMaster.put("GetData",jGetData);
				        jObjTaxMaster.put("TimeData",jTimeData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjTaxMaster;
	}
	
	public JSONObject funCheckDuplicateBuyPromoItem(String promoItemCode,String strPromoCode,String posCode,String areaCode) throws Exception
	{
		JSONObject jObj=new JSONObject();
		if(null==strPromoCode || strPromoCode.trim().isEmpty() )
		{
		 
		    	long intCode =utility.funGetDocumentCodeFromInternal("PromotionCode");
				intCode++;
				if (intCode < 10)
				{
					strPromoCode = "PM00" + intCode;
				}
				else if (intCode < 100)
				{
					strPromoCode = "PM0" + intCode;
				}
				else if (intCode < 1000)
				{
					strPromoCode = "PM" + intCode;
				}
				else
				{
					strPromoCode = "PM001";
				}
		    
		}
		 jObj.put("promoCode", strPromoCode);
		boolean flag=objPromotionMasterDao.funCheckDuplicateBuyPromoItem(promoItemCode, strPromoCode, posCode, areaCode);
		jObj.put("flag", flag);
		
		return jObj;
	}
}
