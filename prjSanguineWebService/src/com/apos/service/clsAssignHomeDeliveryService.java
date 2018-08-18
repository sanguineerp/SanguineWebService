
package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsAssignHomeDeliveryDao;
import com.apos.dao.clsCostCenterMasterDao;
import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsCostCenterMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsAssignHomeDeliveryService")
public class clsAssignHomeDeliveryService
{
	@Autowired
	private clsAssignHomeDeliveryDao	objAssignHomeDeliveryDao;

	public JSONObject funGetOpenBillAndDeliveryBoyDtl(String zoneCode,String areaCode,String clientCode)
	{
		return objAssignHomeDeliveryDao.funGetOpenBillAndDeliveryBoyDtl(zoneCode,areaCode,clientCode);
	}

	public void funSaveDelBoyBillDtl(JSONObject jObjAssignHomeDeliveryMaster)
	{
	objAssignHomeDeliveryDao.funSaveDelBoyBillDtl(jObjAssignHomeDeliveryMaster);
	}

	public JSONObject funSetBillAmountAndLooseCash(String billNo)
	{
	return objAssignHomeDeliveryDao.funSetBillAmountAndLooseCash(billNo);
	} 
	
//	public JSONObject funCheckBillAndDeliveryBoyDtl(String clientCode)
//	{
//	return objAssignHomeDeliveryDao.funCheckBillAndDeliveryBoyDtl(clientCode);
//	}  
}
