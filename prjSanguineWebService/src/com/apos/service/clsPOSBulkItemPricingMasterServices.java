package com.apos.service;


import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.apos.dao.clsCustomerTypeMasterDao;
import com.apos.dao.clsPOSBulkItemPricingMasterDao;
import com.apos.dao.clsZoneMasterDao;
import com.apos.model.clsZoneMasterModel;
import com.apos.model.clsZoneMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsPOSBulkItemPricingMasterServices")
public class clsPOSBulkItemPricingMasterServices {
	@Autowired
	private clsPOSBulkItemPricingMasterDao objclsPOSBulkItemPricingMasterDao;

	public JSONObject funRetriveBulkItemPricingMaster(JSONObject jObjMaster) throws Exception{
		JSONObject obj=null;
		obj=objclsPOSBulkItemPricingMasterDao.funRetriveBulkItemPricingMaster(jObjMaster);
		

			return obj;

	}

	public void funUpdateBulkItemPricingMaster(JSONObject jObjMaster) throws Exception{
	
		objclsPOSBulkItemPricingMasterDao.funUpdateBulkItemPricingMaster(jObjMaster);

	}
}
