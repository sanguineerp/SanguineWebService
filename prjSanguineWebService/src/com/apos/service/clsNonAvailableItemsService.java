
package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsAssignHomeDeliveryDao;
import com.apos.dao.clsCostCenterMasterDao;
import com.apos.dao.clsNonAvailableItemsDao;
import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsCostCenterMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsNonAvailableItemsService")
public class clsNonAvailableItemsService
{
	@Autowired
	private clsNonAvailableItemsDao	objNonAvailableItemsDao;

	

	public void funRemoveNonAvailableItem(JSONObject jObj)
	{
		objNonAvailableItemsDao.funRemoveNonAvailableItem(jObj);
	}
	
	
	
}
