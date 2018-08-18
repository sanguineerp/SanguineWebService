package com.apos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsTDHMasterDaoImpl;
import com.apos.dao.inftTDHMasterDao;
import com.apos.model.clsCustomerAreaMasterAmountModel;
import com.apos.model.clsCustomerAreaMasterModel;
import com.apos.model.clsMenuItemMasterModel;
import com.apos.model.clsPOSTDHDtlModel;
import com.apos.model.clsPOSTDHModel;
import com.apos.model.clsPOSTDHModel_ID;
import com.apos.model.clsPricingMasterHdModel;
import com.apos.model.clsZoneMasterModel;
import com.apos.model.clsZoneMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;



@Service("clsTDHMasterService")
public class clsTDHMasterServiceImpl  implements clsTDHMasterService{

	
	@Autowired
	 inftTDHMasterDao	objTDHMasterDao;
	
	
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	clsUtilityFunctions utility;

	
	public String funSaveTDH(JSONObject jObjTDH){
		
		JSONArray ArrayList=new JSONArray();
		String TDHCode = "";
		try
		{
			TDHCode = jObjTDH.getString("strTDHCode");
		    String strDescription = jObjTDH.getString("strDescription");
		    String strTDHOnMenuHead = jObjTDH.getString("strTDHOnMenuHead");
		    String strTDHOnItem = jObjTDH.getString("strTDHOnItem");
		    String strFreeQuantity = jObjTDH.getString("strFreeQuantity");
		    String strchkApplicable = jObjTDH.getString("strchkApplicable");
		    String strMenuHead = jObjTDH.getString("strMenuHead");
		    Long strMaxItemQuantity = Long.parseLong(jObjTDH.getString("strMaxItemQuantity"));		   		    		    
		    String user = jObjTDH.getString("User");
		    String clientCode = jObjTDH.getString("ClientCode");		   				    
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		    ArrayList=jObjTDH.getJSONArray("List");
		    if (TDHCode.trim().isEmpty())
		    {
		    	List list=utility.funGetDocumentCode("POSTDHMaster");
		    	if (!list.get(0).toString().equals("0"))
				{
				    String strCode = "00";
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
				    	TDHCode = "TD000000" + intCode;
				    }
				    else if (intCode < 100)
				    {
				    	TDHCode = "TD00000" + intCode;
				    }
				    else if (intCode < 1000)
				    {
				    	TDHCode = "TD0000" + intCode;
				    }
				    else if (intCode < 10000)
				    {
				    	TDHCode = "TD000" + intCode;
				    }
				    else if (intCode < 100000)
				    {
				    	TDHCode = "TD00" + intCode;
				    }
				    else if (intCode < 1000000)
				    {
				    	TDHCode = "TD0" + intCode;
				    }
				    else if (intCode < 10000000)
				    {
				    	TDHCode = "TD" + intCode;
				    }
				}
				else
				{
					TDHCode = "TD0000001";
				}
		    }
		    clsPOSTDHModel objModel = new clsPOSTDHModel(new clsPOSTDHModel_ID(TDHCode, clientCode));
		   
		    objModel.setStrTDHCode(TDHCode);
		    objModel.setStrDescription(strDescription);
		    objModel.setStrMenuCode(strTDHOnMenuHead);
		    objModel.setStrItemCode(strTDHOnItem);
		    objModel.setIntMaxQuantity(strMaxItemQuantity);
		    objModel.setStrApplicable(strchkApplicable);    
		    objModel.setStrComboItemYN("Y"); 
	        objModel.setStrClientCode(clientCode);
	        
		
	       // List<clsPOSTDHDtlModel> listTDHDtl = new ArrayList<clsPOSTDHDtlModel>();
	        Set<clsPOSTDHDtlModel> listTDHDtl = new HashSet<clsPOSTDHDtlModel>();
	    	 for(int i=0; i<ArrayList.length(); i++)
			    {
			
			    	
				 JSONObject jObj =ArrayList.getJSONObject(i);
				 clsPOSTDHDtlModel objModelTDHDtl = new clsPOSTDHDtlModel();	
			   
			    					   
				 objModelTDHDtl.setStrItemCode(strTDHOnItem);
				 objModelTDHDtl.setStrSubItemCode(jObj.getString("strItemCode"));
				 objModelTDHDtl.setIntSubItemQty(jObj.getInt("intSubItemQty"));
				 objModelTDHDtl.setStrDefaultYN(jObj.getString("strDefaultYN"));
				 objModelTDHDtl.setStrSubItemMenuCode(jObj.getString("strSubItemMenuCode"));
				
					    
							
		    				
					    listTDHDtl.add(objModelTDHDtl);					    

	       }
	    	 
	    	 objModel.setListTDHDtl(listTDHDtl);
				objDao.funSave(objModel);

		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return TDHCode;

}

	
	public JSONObject funLoadTDHMasterData(String searchCode,String clientCode)
	{
		JSONObject jobjDtl=new JSONObject();
		JSONArray jDtlData=new JSONArray();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("TDHCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			clsPOSTDHModel objTDHMasterModel = objTDHMasterDao.funLoadTDHMasterData("getTDH", hmParameters);
			
			
			Set<clsPOSTDHDtlModel> listTDHDtl =objTDHMasterModel.getListTDHDtl();
			
			Iterator itr = listTDHDtl.iterator();
		        while(itr.hasNext())
		        {
		        	clsPOSTDHDtlModel obj=(clsPOSTDHDtlModel)itr.next();
		        	JSONObject jObj=new JSONObject();
		        	
		        	jObj.put("strSubItemCode",obj.getStrSubItemCode());		        
		        	jObj.put("strDefaultYN",obj.getStrDefaultYN());
		        	jObj.put("intSubItemQty",obj.getIntSubItemQty());
		        	jObj.put("strSubItemMenuCode",obj.getStrSubItemMenuCode());
			
				jDtlData.put(jObj);
				
			}
			
		        jobjDtl.put("strTDHCode",objTDHMasterModel.getStrTDHCode());
		        jobjDtl.put("strDescription",objTDHMasterModel.getStrDescription());
		        jobjDtl.put("strMenuCode",objTDHMasterModel.getStrMenuCode());
		        jobjDtl.put("strItemCode",objTDHMasterModel.getStrItemCode());
		        jobjDtl.put("strApplicable",objTDHMasterModel.getStrApplicable());
		        jobjDtl.put("intMaxQuantity",objTDHMasterModel.getIntMaxQuantity());
		       		        			 
		        jobjDtl.put("TDHDtlData",jDtlData);
		    
		
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjDtl;
	}

	
	public JSONObject funLoadPOSTDHTableData(String searchCode,String clientCode)
	{
		JSONObject jobjDtl=new JSONObject();
		JSONArray jDtlData=new JSONArray();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("ItemCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			clsPricingMasterHdModel objTDHMasterModel = objTDHMasterDao.funLoadPOSTDHTableData("getItemPricingLoad", hmParameters);
			
			
			
		        jobjDtl.put("strItemCode",objTDHMasterModel.getStrItemCode());
		        jobjDtl.put("strItemName",objTDHMasterModel.getStrItemName());
		       
		    
		
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjDtl;
	}
	
	public JSONObject funLoadItemList(String searchCode,String clientCode)
	{
		JSONObject jobjDtl=new JSONObject();
		JSONArray jDtlData=new JSONArray();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("MenuCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			List<clsPricingMasterHdModel> objListModel = objTDHMasterDao.funLoadItemList("getItemList", hmParameters);
			
			for(int i=0;i<objListModel.size();i++)
			{
			clsPricingMasterHdModel objTDHMasterModel = (clsPricingMasterHdModel)objListModel.get(i);
				JSONObject job=new JSONObject();
				job.put("strItemCode",objTDHMasterModel.getStrItemCode());
				job.put("strItemName",objTDHMasterModel.getStrItemName());
		        jDtlData.put(job);
			}
		
			// Write code to convert model into json object.
			
			jobjDtl.put("ItemList", jDtlData);
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjDtl;
	}
		   
	
	
	public JSONObject funloadPOSAllItemName(String clientCode)
	{
		JSONObject jobjDtl=new JSONObject();
		JSONArray jDtlData=new JSONArray();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			
			hmParameters.put("clientCode",clientCode);
			
			List<clsMenuItemMasterModel> objListModel = objTDHMasterDao.funLoadALLItemNameList("getALLItemName", hmParameters);
			
			for(int i=0;i<objListModel.size();i++)
			{
				clsMenuItemMasterModel objTDHMasterModel = (clsMenuItemMasterModel)objListModel.get(i);
				JSONObject job=new JSONObject();
				job.put("strItemCode",objTDHMasterModel.getStrItemCode());
				job.put("strItemName",objTDHMasterModel.getStrItemName());
		        jDtlData.put(job);
			}
		
			// Write code to convert model into json object.
			
			jobjDtl.put("ItemList", jDtlData);
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjDtl;
	}
		
		   
}
