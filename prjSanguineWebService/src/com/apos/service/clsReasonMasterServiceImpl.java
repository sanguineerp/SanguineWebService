package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.inftReasonMasterDao;
import com.apos.model.clsReasonMasterModel;
import com.apos.model.clsReasonMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsReasonMasterService")
public class clsReasonMasterServiceImpl implements clsReasonMasterService
{
	
	
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	clsUtilityFunctions utility;
	
	@Autowired
	inftReasonMasterDao objinftDao;
    
    public String funSaveUpdateReasonMaster(JSONObject jObjReasonMaster)
    {
	String reasonCode = "";
	try
	{
	    
	    reasonCode = jObjReasonMaster.getString("ReasonCode");
	    String reasonName = jObjReasonMaster.getString("ReasonName");
	    String transferEntry = jObjReasonMaster.getString("TransferEntry");
	    String transferType = jObjReasonMaster.getString("TransferType");
	    String stockIn = jObjReasonMaster.getString("StockIn");
	    String stockOut = jObjReasonMaster.getString("StockOut");
	    String voidBill = jObjReasonMaster.getString("VoidBill");
	    String modifyBill = jObjReasonMaster.getString("ModifyBill");
	    String psp = jObjReasonMaster.getString("PSP");
	    String voidKOT = jObjReasonMaster.getString("VoidKOT");
	    String voidStockIn = jObjReasonMaster.getString("VoidStockIn");
	    String voidStockOut = jObjReasonMaster.getString("VoidStockOut");
	    String voidAdvanceOrder = jObjReasonMaster.getString("VoidAdvanceOrder");
	    String ncKOT = jObjReasonMaster.getString("NCKOT");
	    String complementary = jObjReasonMaster.getString("Complementary");
	    String cashManagement = jObjReasonMaster.getString("CashManagemnt");
	    String discount = jObjReasonMaster.getString("Discount");
	    String reprint = jObjReasonMaster.getString("Reprint");
	    String unsettleBill = jObjReasonMaster.getString("UnsettleBill");
	    String user = jObjReasonMaster.getString("User");
	    String clientCode = jObjReasonMaster.getString("ClientCode");
	    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
	    
	    if(reasonCode.trim().isEmpty())
	    {
	    	
		
		List list=utility.funGetDocumentCode("POSReasoneMaster");
		if (!list.get(0).toString().equals("0"))
		{
			String strCode = "00";
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
			reasonCode = "R0" + intCode;
		    }
		    else
		    {
			reasonCode = "R" + intCode;
		    }
		}
		else
		{
		    reasonCode = "R01";
		}
		
	    }
	    	    
	    clsReasonMasterModel objModel=new clsReasonMasterModel(new clsReasonMasterModel_ID(reasonCode, clientCode));
	    objModel.setStrReasonName(reasonName);
	    objModel.setDteDateCreated(dateTime);
	    objModel.setDteDateEdited(dateTime);
	    objModel.setStrCashMgmt(cashManagement);
	    objModel.setStrComplementary(complementary);
	    objModel.setStrDataPostFlag("N");
	    objModel.setStrDiscount(discount);
	    objModel.setStrKot("N");
	    objModel.setStrModifyBill(modifyBill);
	    objModel.setStrNCKOT(ncKOT);
	    objModel.setStrPSP(psp);
	    objModel.setStrReprint(reprint);	    
	    objModel.setStrStkIn(stockIn);
	    objModel.setStrStkOut(stockOut);
	    objModel.setStrTransferEntry(transferEntry);
	    objModel.setStrTransferType(transferType);
	    objModel.setStrUnsettleBill(unsettleBill);	    
	    objModel.setStrUserCreated(user);
	    objModel.setStrUserEdited(user);
	    objModel.setStrVoidAdvOrder(voidAdvanceOrder);
	    objModel.setStrVoidBill(voidBill);
	    objModel.setStrVoidStkIn(voidStockIn);
	    objModel.setStrVoidStkOut(voidStockOut);
	    
	    
	    objDao.funSave(objModel);
	   // objReasonMasterDao.funSaveUpdateReasonMaster(objModel);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return reasonCode;
    }
    
 
    
    
	@Override
	public JSONObject funGetAllReasonMaster(String clientCode) throws Exception 
	{
		clsReasonMasterModel objModel = new clsReasonMasterModel();
		   JSONObject jObjLoadData = new JSONObject();
		   JSONArray jArrData = new JSONArray();
			
			List list =objDao.funLoadAll(objModel,clientCode);
			clsReasonMasterModel objclsReasonMasterModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objclsReasonMasterModel = (clsReasonMasterModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strReasonCode",objclsReasonMasterModel.getStrReasonCode());
				objMenu.put("strReasonName",objclsReasonMasterModel.getStrReasonName());
				jArrData.put(objMenu);
	       }
			jObjLoadData.put("ReasonList", jArrData);
	       return jObjLoadData;

	   }




	@Override
	public JSONObject funLoadReasoneMasterData(String searchCode,
			String clientCode) {
		JSONObject jobjData=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("reasonCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			clsReasonMasterModel objModel = objinftDao.funLoadReasoneMasterData("getReason", hmParameters);														
			
			jobjData.put("strReasonCode",objModel.getStrReasonCode());
			jobjData.put("strReasonName",objModel.getStrReasonName());
			jobjData.put("strTransferEntry",objModel.getStrTransferEntry());
			jobjData.put("strTransferType",objModel.getStrTransferType());
			jobjData.put("strStkIn",objModel.getStrStkIn());
			jobjData.put("strStkOut",objModel.getStrStkOut());
			jobjData.put("strVoidBill",objModel.getStrVoidBill());
			jobjData.put("strModifyBill",objModel.getStrModifyBill());
			jobjData.put("strPSP",objModel.getStrPSP());
			jobjData.put("strKot",objModel.getStrKot());
			jobjData.put("strCashMgmt",objModel.getStrCashMgmt());
			jobjData.put("strVoidStkIn",objModel.getStrVoidStkIn());
			jobjData.put("strVoidStkOut",objModel.getStrVoidStkOut());
			jobjData.put("strUnsettleBill",objModel.getStrUnsettleBill());
			jobjData.put("strComplementary",objModel.getStrComplementary());
			jobjData.put("strDiscount",objModel.getStrDiscount());
			jobjData.put("strNCKOT",objModel.getStrNCKOT());
			jobjData.put("strVoidAdvOrder",objModel.getStrVoidAdvOrder());
			jobjData.put("strReprint",objModel.getStrReprint());

			
		
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjData;
	}




	@Override
	public JSONObject funLoadAllReasonMasterData(String clientCode) {
		// TODO Auto-generated method stub
		
		return objinftDao.funLoadAllReasonMasterData(clientCode);
		 
	}

}

