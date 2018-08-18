

package com.apos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfRecipeMasterDao;
import com.apos.model.clsRecipeDtlModel;
import com.apos.model.clsRecipeMasterModel;
import com.apos.model.clsRecipeMasterModel_ID;
import com.apos.model.clsTableMasterModel;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsRecipeMasterService")
public class clsRecipeMasterServiceImpl implements clsRecipeMasterService{
	@Autowired
	private intfRecipeMasterDao objRecipeMasterDao;
	
	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;
	public String funAddUpdateRecipeMaster(JSONObject jObjAreaMaster) 
	{
		String recipeCode = "";
		try
		{

			recipeCode = jObjAreaMaster.getString("RecipeCode");
			String itemCode = jObjAreaMaster.getString("ItemCode");
			String fromDate = jObjAreaMaster.getString("FromDate");
			String toDate = jObjAreaMaster.getString("ToDate");
			String strPosCode = jObjAreaMaster.getString("strPosCode");
			
			String user = jObjAreaMaster.getString("User");
			String clientCode = jObjAreaMaster.getString("ClientCode");
			String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");

			if (recipeCode.trim().isEmpty())
			{
				long intCode =utility.funGetDocumentCodeFromInternal("Recipe");
					intCode++;
					if (intCode < 10)
					{
						recipeCode = "R000000" + intCode;
					}
					else if (intCode < 100)
					{
						recipeCode = "R00000" + intCode;
					}
					else if (intCode < 1000)
					{
						recipeCode = "R0000" + intCode;
					}
					else if (intCode < 10000)
					{
						recipeCode = "R000" + intCode;
					}
					else if (intCode < 100000)
					{
						recipeCode = "R00" + intCode;
					}
					else if (intCode < 1000000)
					{
						recipeCode = "R0" + intCode;
					}
					else if (intCode < 10000000)
					{
						recipeCode = "R" + intCode;
					}
				
					else
					{
						recipeCode = "R0000001";
					}

			}

			clsRecipeMasterModel objModel = new clsRecipeMasterModel(new clsRecipeMasterModel_ID(recipeCode, clientCode));
			
			objModel.setStrItemCode(itemCode);
			objModel.setDteFromDate(fromDate);
			objModel.setDteToDate(toDate);
			objModel.setStrPOSCode(strPosCode);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrDataPostFlag("");
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);
			
			
			 JSONArray delRecipeItemList=jObjAreaMaster.getJSONArray("ChildItemDtl");
			    
				List<clsRecipeDtlModel> listRecipeDtl = new ArrayList<clsRecipeDtlModel>();
			    for(int i=0;i<delRecipeItemList.length();i++)
			    {
			    	JSONObject jObj = new JSONObject();
			    	jObj=delRecipeItemList.getJSONObject(i);
			    	
			    	String childItemCode=jObj.getString("ItemCode");
			    	
			    	double dblValue=jObj.getDouble("Quantity");
			    	clsRecipeDtlModel objChildItemModel = new clsRecipeDtlModel();
			    	objChildItemModel.setStrChildItemCode(childItemCode);
			    	objChildItemModel.setDblQuantity(dblValue);
			    	
			    	objChildItemModel.setStrPOSCode(strPosCode);
			    	objChildItemModel.setStrDataPostFlag("Y");
			    	listRecipeDtl.add(objChildItemModel);
			    
			    }
			    
			    objModel.setListRecipeDtl(listRecipeDtl);
			    objService.funSave(objModel);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return recipeCode;
	}

	@Override
	public JSONObject funSelectedRecipeMasterData(String recipeCode,String clientCode)
	{
		JSONObject jObjMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("recipeCode",recipeCode);
			hmParameters.put("clientCode",clientCode);
			clsRecipeMasterModel objRecipeMasterModel = objRecipeMasterDao.funGetRecipeMasterData("getRecipeMaster", hmParameters);
			List<clsRecipeDtlModel> listRecipeDtl =objRecipeMasterModel.getListRecipeDtl();
			JSONArray jSettleData = new JSONArray();
			for(int i=0; i<listRecipeDtl.size(); i++)
				
			{
				clsRecipeDtlModel objSettle=(clsRecipeDtlModel)listRecipeDtl.get(i);
				JSONObject jObjSettle=new JSONObject();
				jObjSettle.put("ItemCode",objSettle.getStrChildItemCode());
			
				jObjSettle.put("Quantity",objSettle.getDblQuantity());
				jSettleData.put(jObjSettle);
				
			}
			jObjMaster.put("strRecipeCode",objRecipeMasterModel.getStrRecipeCode());
			jObjMaster.put("strItemCode",objRecipeMasterModel.getStrItemCode());
			jObjMaster.put("dteFromDate",objRecipeMasterModel.getDteFromDate());
			jObjMaster.put("dteToDate",objRecipeMasterModel.getDteToDate());
			jObjMaster.put("RecipeDtl",jSettleData);
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMaster;
	}
}
