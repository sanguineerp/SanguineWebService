
package com.apos.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMenuHeadMasterDao;
import com.apos.dao.clsPOSMultiBillSettleInCashDao;
import com.apos.model.clsMenuHeadMasterModel;
import com.apos.model.clsMenuHeadMasterModel_ID;
import com.apos.model.clsPosSettlementDetailsModel;
import com.apos.model.clsPosSettlementDetailsModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsPOSMultiBillSettleInCashService")
public class clsPOSMultiBillSettleInCashService
{

	@Autowired
	clsPOSMultiBillSettleInCashDao	objPOSMultiBillSettleInCashDao;

	public JSONObject funGetSettleBillDtlData(String clientCode,String posCode,String posDate)
	{
		return objPOSMultiBillSettleInCashDao.funGetSettleBillDtlData(clientCode,posCode,posDate);
	}
	public void funSettleBills(JSONObject jObjSettleBills)
	{
		 objPOSMultiBillSettleInCashDao.funSettleBills(jObjSettleBills);
	}
}	