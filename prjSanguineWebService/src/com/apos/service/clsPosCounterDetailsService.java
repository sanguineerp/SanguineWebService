
package com.apos.service;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsPosCounterDetailsDao;
import com.apos.model.clsMenuHeadMasterModel;
import com.apos.model.clsMenuHeadMasterModel_ID;
import com.apos.model.clsPosCounterDetailsModel;
import com.apos.model.clsPosCounterDetailsModel_ID;
import com.apos.model.clsPosCounterDetailsModel;


@Service("clsPosCounterDetailsService")
public class clsPosCounterDetailsService
{
	@Autowired
	clsPosCounterDetailsDao	objPosCounterDetailsDao;


	public void funSavePosCounterDetails( JSONArray settleList,String counterCode,String clientCode)
	 {
		 
		
		 for(int i=0; i<settleList.length(); i++)
		    {
		    try
		    {
			 JSONObject jObj = new JSONObject();
		    	jObj=settleList.getJSONObject(i);
		    	String menuCode=jObj.getString("MenuCode");
		    	
		    	clsPosCounterDetailsModel objCounterDetailsModel= new clsPosCounterDetailsModel(new clsPosCounterDetailsModel_ID(counterCode,menuCode));
		    	objCounterDetailsModel.setStrClientCode(clientCode);
		    	
		    	
		    	
		    	
		    	
		    	objPosCounterDetailsDao.funSavePosCounterDetails(objCounterDetailsModel);
		    }
		    catch (Exception e)
			{
			    e.printStackTrace();
			}
		    }
		    
	 }
	
}
