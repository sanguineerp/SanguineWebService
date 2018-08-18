
package com.apos.service;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsChangePasswordDao;
import com.apos.model.clsAreaMasterModel;

@Service("clsChnagePasswordService")
public class clsChangePasswordService
{
	@Autowired
	private clsChangePasswordDao	objChangePasswordDao;

	
	public void funSaveUserPassword(JSONObject jObj)
	{
		objChangePasswordDao.funSaveUserPassword(jObj);
	}

	public JSONObject funGetOldPassword(String userCode,String oldPass)
	{
		JSONObject jObj = new JSONObject();
		jObj = objChangePasswordDao.funGetOldPassword(userCode,oldPass);
		return jObj;
	}
	
}
