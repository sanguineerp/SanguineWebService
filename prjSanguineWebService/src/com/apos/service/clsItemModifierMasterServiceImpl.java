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
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;












import com.apos.dao.clsItemModifierMasterDao;
import com.apos.model.clsItemModifierMasterModel;
import com.apos.model.clsMenuItemPricingHdModel;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.apos.model.clsModifierMasterHdModel;
import com.apos.model.clsModifierMasterModel_ID;
import com.apos.model.clsSubMenuHeadMasterModel;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.model.clsBaseModel;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsItemModifierMasterService")
public class clsItemModifierMasterServiceImpl implements clsItemModifierMasterService{
	
	@Autowired
	private intfBaseService objSer; 

	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	
	@Autowired
	clsItemModifierMasterDao obItemModifierMasterDao;
	
	@Override
	public JSONObject funLoadModifierGroupMaster(String clientCode) throws Exception
	{
		JSONObject jObjLoadData = new JSONObject();
		JSONArray jArrData = new JSONArray();
		JSONObject jObj=new JSONObject();
		clsModifierGroupMasterHdModel objModel = new clsModifierGroupMasterHdModel();
			List list = objSer.funLoadAll(objModel,clientCode);
		
			/*clsModifierMasterHdModel objMF1=new clsModifierMasterHdModel();
			List list2 = objSer.funLoadAll(objMF1);*/
			
			/*clsModifierMasterHdModel objMF=new clsModifierMasterHdModel();
			clsBaseModel obj = objSer.funLoad(objMF, new clsModifierMasterModel_ID("M001", clientCode));
			clsModifierMasterHdModel objM=(clsModifierMasterHdModel)obj;
			objM.getListItemModifierDtl().size();
			*/
			clsModifierGroupMasterHdModel objModifierGroupMasterHdModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objModifierGroupMasterHdModel = (clsModifierGroupMasterHdModel) list.get(cnt);
				JSONObject objModItem=new JSONObject();
				objModItem.put("ModifierCode",objModifierGroupMasterHdModel.getStrModifierGroupCode());
				objModItem.put("ModifierName",objModifierGroupMasterHdModel.getStrModifierGroupName());
				jArrData.put(objModItem);
	       }
			jObjLoadData.put("ModifierGroup", jArrData);
	       return jObjLoadData;
		
	}
	@Override
	public String funSaveItemModifierMaster(JSONObject jObjItemModifierMaster)
	{
		String modifierCode = "";
		try
		{
			//modifierCode = jObjItemModifierMaster.getString("ModifierCode");
		    String modifierName = jObjItemModifierMaster.getString("ModifierName").toUpperCase();
		    String modifierDescription = jObjItemModifierMaster.getString("ModifierDescription");
		    String modifierGroup = jObjItemModifierMaster.getString("ModifierGroup");

		    String rate = jObjItemModifierMaster.getString("Rate");
		    String applicable = jObjItemModifierMaster.getString("Applicable");
		    String chargable = jObjItemModifierMaster.getString("Chargable");
		    	
		    String user = jObjItemModifierMaster.getString("User");
		    String clientCode = jObjItemModifierMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		    
		    JSONArray jArrOb=new JSONArray();
		    jArrOb=jObjItemModifierMaster.getJSONArray("ItemDtls");
		    JSONObject jObjItem=new JSONObject();
		    
		    if (modifierCode.trim().isEmpty())
		    {
		    	if(modifierName.startsWith("-->")){
		    		modifierName=modifierName.substring(0, 3);
		    	}
		    	List list=objUtilityFunctions.funGetDocumentCode("POSItemModifierMaster");
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
	                	modifierCode = "M00" + intCode;
	                }
	                else if (intCode < 100)
	                {
	                	modifierCode = "M0" + intCode;
	                }
	                else
	                {
	                	modifierCode = "M" + intCode;
	                }

	            }
	            else
	            {
	            	modifierCode = "M001";
	            }
				
		     }	
		    
		    clsModifierMasterHdModel objModel = new clsModifierMasterHdModel(new clsModifierMasterModel_ID(modifierCode, clientCode));
		    objModel.setStrModifierName(modifierName);
		    objModel.setDocCode(modifierCode);
		    objModel.setStrModifierDesc(modifierDescription);
		    objModel.setStrModifierGroupCode(modifierGroup);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    
		    Set<clsItemModifierMasterModel> setItemModifierDtl = new HashSet<clsItemModifierMasterModel>();
		    for(int i=0;i<jArrOb.length();i++)
		    {
		    	jObjItem=jArrOb.getJSONObject(i);
		    		    	
		    	clsItemModifierMasterModel objItemModel = new clsItemModifierMasterModel();
		    	objItemModel.setStrItemCode(jObjItem.getString("strItemCode"));
		    	objItemModel.setStrApplicable(applicable);
		    	objItemModel.setStrChargable(chargable);
		    	objItemModel.setDblRate(Double.parseDouble(jObjItem.getString("dblRate")));
		    	objItemModel.setStrDefaultModifier("N");
		    	setItemModifierDtl.add(objItemModel);
		    	//objItemModifierMasterDao.funSaveItemModifierMaster(objItemModel);
		    }
		    
		    objModel.setSetItemModifierDtl(setItemModifierDtl);
		    objSer.funSave(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return modifierCode;
	    }


	@Override
	public JSONObject funLoadItemPricing(String MenuCode) throws Exception
	   {
		   clsMenuItemPricingHdModel objModel = new clsMenuItemPricingHdModel();
		   JSONObject jObjLoadData = new JSONObject();
		 
		   JSONArray jArrData = new JSONArray();
		   List list=objSer.funGetSerachList("MenuItemPricingHdModel", MenuCode);
		   if(list.size()>0)
		   {
			   for(int i=0;i<list.size();i++)
	        	{
	        		Object ob[]=(Object[]) list.get(i);
	        		JSONObject objItemPricing=new JSONObject();
	        		objItemPricing.put("ItemCode",ob[1].toString());
					objItemPricing.put("ItemName",ob[0].toString());
					jArrData.put(objItemPricing);
				   
				}
			   jObjLoadData.put("MenuItemPricing", jArrData);
		   }
	       return jObjLoadData;

	   }
	
	@Override
	public JSONObject funGetItemModifierMasterData(String modCode,String clientCode)throws Exception
	{
		// TODO Auto-generated method stub
		JSONObject jObjSubMenuHead=new JSONObject();
		JSONArray jArrItemMod = new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("modCode",modCode);
			hmParameters.put("clientCode",clientCode);
		    
			clsModifierMasterHdModel objModifierMasterHdModel = obItemModifierMasterDao.funGetItemModifierMasterData("getModifierMaster", hmParameters);
			
			Set<clsItemModifierMasterModel> listItemModifierDtl = objModifierMasterHdModel.getSetItemModifierDtl();
			Iterator itr = listItemModifierDtl.iterator();
			
			jArrItemMod.put(objModifierMasterHdModel.getStrModifierCode());
			jArrItemMod.put(objModifierMasterHdModel.getStrModifierName());
			jArrItemMod.put(objModifierMasterHdModel.getStrModifierDesc());
			jArrItemMod.put(objModifierMasterHdModel.getStrModifierGroupCode());
			 if(itr.hasNext())
		        {
				 	clsItemModifierMasterModel objItemModifierMasterModel=(clsItemModifierMasterModel)itr.next();
				 	jArrItemMod.put(objItemModifierMasterModel.getDblRate());
					jArrItemMod.put(objItemModifierMasterModel.getStrApplicable());
					jArrItemMod.put(objItemModifierMasterModel.getStrChargable());
		        }
				 
		    
		    jObjSubMenuHead.put("POSItemModifierMaster", jArrItemMod);
			// Write code to convert model into json object.
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjSubMenuHead;
		
	}
	
}

