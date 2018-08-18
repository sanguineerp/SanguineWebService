
package com.apos.service;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsDebitCardSettlementDetailsDao;
import com.apos.model.clsDebitCardSettlementDetailsModel;
import com.apos.model.clsDebitCardSettlementDetailsModel_ID;
import com.apos.model.clsPosSettlementDetailsModel;
import com.apos.model.clsPosSettlementDetailsModel_ID;


@Service("clsDebitCardSettlementDetailsService")
public class clsDebitCardSettlementDetailsService
{
	@Autowired
	clsDebitCardSettlementDetailsDao	objDebitCardSettlementDetailsDao;

	
	public void funSaveDebitCardSettlementDetails( JSONArray settleList,String cardTypeCode,String clientCode)
	 {
		 
	//	objDebitCardSettlementDetailsDao.funDeleteDebitCardSettlementDetails(cardTypeCode);
		 for(int i=0; i<settleList.length(); i++)
		    {
		    try
		    {
			 JSONObject jObj = new JSONObject();
		    	jObj=settleList.getJSONObject(i);
		    	String settlementCode=jObj.getString("SettlementCode");
		    	String applicable="true";
		    	clsDebitCardSettlementDetailsModel objSettlementModel= new clsDebitCardSettlementDetailsModel(new clsDebitCardSettlementDetailsModel_ID(cardTypeCode,settlementCode));
		    	objSettlementModel.setStrClientCode(clientCode);
		    	
		    	objSettlementModel.setStrApplicable(applicable);
		    	
		    	objSettlementModel.setStrDataPostFlag("N");
		    	
		    	objDebitCardSettlementDetailsDao.funSaveDebitCardSettlementDetails(objSettlementModel);
		    }
		    catch (Exception e)
			{
			    e.printStackTrace();
			}
		    }
		    
	 }
	
	public JSONObject funGetDebitCardSettlementDtl(String cardTypeCode,String clientCode)
	{
		return objDebitCardSettlementDetailsDao.funGetDebitCardSettlementDtl(cardTypeCode,clientCode);
	}
	
//	public JSONObject funGetDebitCardSettlement(String clientCode)
//	{
//		return objDebitCardSettlementDetailsDao.funGetDebitCardSettlement(clientCode);
//	}
//	///////////////////////////////
//	public JSONObject funGetDebitCardSettlement(String clientCode)
//	{
//		return objDebitCardSettlementDetailsDao.funGetDebitCardSettlement(clientCode);
//	}
//	///////////////////////
	
}
