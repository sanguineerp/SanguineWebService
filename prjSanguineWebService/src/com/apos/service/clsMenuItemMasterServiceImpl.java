package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMenuItemMasterDao;
import com.apos.model.clsMenuItemMasterModel;
import com.apos.model.clsMenuItemMasterModel_ID;
import com.apos.model.clsMenuItemPricingHdModel;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsMenuItemMasterService")
public class clsMenuItemMasterServiceImpl implements clsMenuItemMasterService
{
	
	@Autowired
	private intfBaseService objSer;
	
	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	
	@Autowired
	clsMenuItemMasterDao objMenuItemMasterDao;
	
	
	@Override
	public String funSaveItemMaster(JSONObject objItemMaster)
	{
		String itemCode="";
			
		try
		{			
			itemCode=objItemMaster.getString("ItemCode");
	        String itemName=objItemMaster.getString("ItemName");
	        String shortName=objItemMaster.getString("ShortName");
	        String externalCode=objItemMaster.getString("ExternalCode");
	        String itemForSale=objItemMaster.getString("ItemForSale");
	        String itemType=objItemMaster.getString("ItemType");
	        String subGroupCode=objItemMaster.getString("SubGroupCode");
	        String rawMaterial=objItemMaster.getString("RawMaterial");
	        String taxIndicator=objItemMaster.getString("TaxIndicator");
	        String purchaseRate=objItemMaster.getString("PurchaseRate");
	        String revenueHead=objItemMaster.getString("RevenueHead");
	        String salePrice=objItemMaster.getString("SalePrice");
	        String procDay=objItemMaster.getString("ProcDay");
	        String procTimeMin=objItemMaster.getString("ProcTimeMin");
	        String minLevel=objItemMaster.getString("MinLevel");
	        String maxLevel=objItemMaster.getString("MaxLevel");
	        String stockInEnable=objItemMaster.getString("StockInEnable");
	        String openItem=objItemMaster.getString("OpenItem");
	        String itemWiseKOTYN=objItemMaster.getString("ItemWiseKOTYN");
	        String itemDetails=objItemMaster.getString("ItemDetails");
	        String applyDiscount=objItemMaster.getString("ApplyDiscount");
	        String strUOM=objItemMaster.getString("UOM");
	        
	        String user=objItemMaster.getString("User");
	        String clientCode=objItemMaster.getString("ClientCode");
	        String dateTime=new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
	       
	        if (itemCode.trim().isEmpty())
		    {
	        		//itemCode = objMenuItemMasterDao.funGenerateItemCode();
	        	List list=objUtilityFunctions.funGetDocumentCode("POSMenuItemMaster");
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
		                itemCode = "I00000" + intCode;
		            }
		            else if (intCode < 100)
		            {
		                itemCode = "I0000" + intCode;
		            }
		            else if (intCode < 1000)
		            {
		                itemCode = "I000" + intCode;
		            }
		            else if (intCode < 10000)
		            {
		                itemCode = "I00" + intCode;
		            }
		            else if (intCode < 100000)
		            {
		                itemCode = "I0" + intCode;
		            }
		            else if (intCode < 1000000)
		            {
		                itemCode = "I" + intCode;
		            }
		        }
		        else
		        {
		            itemCode = "I000001";
		        }
		    }
	        clsMenuItemMasterModel objModel = new clsMenuItemMasterModel(new clsMenuItemMasterModel_ID(itemCode, clientCode));
		    objModel.setStrItemCode(itemCode);
		    objModel.setStrItemName(itemName);
		    objModel.setStrShortName(shortName);
		    objModel.setStrExternalCode(externalCode);
		    objModel.setStrItemForSale(itemForSale);
		    objModel.setStrItemType(itemType);
		    
		    objModel.setStrSubGroupCode(subGroupCode);
		    objModel.setStrRawMaterial(rawMaterial);
		    if (taxIndicator.trim().isEmpty())
		    {
		    	taxIndicator =" ";
		    }
		    objModel.setStrTaxIndicator(taxIndicator);
		    objModel.setDblPurchaseRate(Double.parseDouble(purchaseRate));
		    objModel.setStrRevenueHead(revenueHead);
		    objModel.setDblSalePrice(Double.parseDouble(salePrice));
		    objModel.setIntProcDay(Integer.parseInt(procDay));
		    objModel.setIntProcTimeMin(Integer.parseInt(procTimeMin));
		    objModel.setDblMinLevel(Double.parseDouble(minLevel));
		    objModel.setDblMaxLevel(Double.parseDouble(maxLevel));
		    objModel.setStrStockInEnable(stockInEnable);
		    objModel.setStrOpenItem(openItem);
		    objModel.setStrItemWiseKOTYN(itemWiseKOTYN);
		    objModel.setStrItemDetails(itemDetails);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDiscountApply(applyDiscount);
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrExciseBrandCode("");
		    objModel.setStrItemImage("");
		    objModel.setStrShortName("");
		    objModel.setStrNoDeliveryDays("");
		    objModel.setStrItemWeight("0.000");
		    objModel.setStrUrgentOrder("N");
		    objModel.setStrWSProdCode("NA");
		    objModel.setStrUOM(strUOM);
		    objModel.setImgImage("");
		   // objModel.setStr
		    
		    itemCode = objSer.funSave(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return itemCode;
	}
	
	
	@Override
	public JSONObject funGetMenuItemMasterData(String itemCode,String clientCode)throws Exception
	{
		// TODO Auto-generated method stub
		JSONObject jObjItemMaster=new JSONObject();
		JSONArray jArrData = new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("itemCode",itemCode);
			hmParameters.put("clientCode",clientCode);
			
			clsMenuItemMasterModel objMenuItemMasterModel = objMenuItemMasterDao.funGetMenuItemMasterData("getMenuItemMaster", hmParameters);
			 
			jArrData.put(objMenuItemMasterModel.getStrItemCode());
			jArrData.put(objMenuItemMasterModel.getStrItemName());
			jArrData.put(objMenuItemMasterModel.getStrShortName());
			jArrData.put(objMenuItemMasterModel.getStrExternalCode());
			jArrData.put(objMenuItemMasterModel.getStrItemForSale());
			
			jArrData.put(objMenuItemMasterModel.getStrItemType());
			jArrData.put(objMenuItemMasterModel.getStrSubGroupCode());
			jArrData.put(objMenuItemMasterModel.getStrRawMaterial());
			jArrData.put(objMenuItemMasterModel.getStrTaxIndicator());
			jArrData.put(objMenuItemMasterModel.getDblPurchaseRate());
			jArrData.put(objMenuItemMasterModel.getStrRevenueHead());
			
			jArrData.put(objMenuItemMasterModel.getDblSalePrice());
			jArrData.put(objMenuItemMasterModel.getIntProcDay());
			jArrData.put(objMenuItemMasterModel.getIntProcTimeMin());
			jArrData.put(objMenuItemMasterModel.getDblMinLevel());
			jArrData.put(objMenuItemMasterModel.getDblMaxLevel());
			
			jArrData.put(objMenuItemMasterModel.getStrStockInEnable());
			jArrData.put(objMenuItemMasterModel.getStrOpenItem());
			jArrData.put(objMenuItemMasterModel.getStrItemWiseKOTYN());
			jArrData.put(objMenuItemMasterModel.getStrItemDetails());
			jArrData.put(objMenuItemMasterModel.getStrDiscountApply());
			jArrData.put(objMenuItemMasterModel.getStrUOM());
		
		    
			jObjItemMaster.put("POSMenuItemMaster", jArrData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjItemMaster;
		
	}
	
	
	
	@Override
	public JSONArray  funGetAllRevenueHead(){

		//return objSer.funGetAllRevenueHead();
		return null;
	}
	
	@Override
	public JSONObject funFillItemTable(String clientCode)
	{
		JSONObject jObj = new JSONObject();
		jObj = objMenuItemMasterDao.funFillItemTable(clientCode);
		return jObj;
	}
	
}


