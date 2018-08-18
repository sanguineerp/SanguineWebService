package com.apos.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.apos.dao.clsPOSConfigSettingDao;
import com.apos.model.clsPOSConfigSettingHdModel;
import com.apos.model.clsPOSConfigSettingModel_ID;

@Service("clsPOSConfigSettingService")
public class clsPOSConfigSettingService{
	@Autowired
	private clsPOSConfigSettingDao objPOSConfigSettingDao;

	public String funSavePOSConfigSetting(JSONObject jObjPOSConfigSetting) throws Exception{
		
		clsPOSConfigSettingHdModel objModel = new clsPOSConfigSettingHdModel(new clsPOSConfigSettingModel_ID( jObjPOSConfigSetting.get("strClientCode").toString()));
		objModel.setStrServer(jObjPOSConfigSetting.get("strServer").toString());
		objModel.setStrDBName(jObjPOSConfigSetting.get("strDBName").toString());
		objModel.setStrUserID(jObjPOSConfigSetting.get("strUserID").toString());
		objModel.setStrPassword(jObjPOSConfigSetting.get("strPassword").toString());
		objModel.setStrIPAddress(jObjPOSConfigSetting.get("strIPAddress").toString());
		objModel.setStrPort(jObjPOSConfigSetting.get("strPort").toString());
		objModel.setStrBackupPath(jObjPOSConfigSetting.get("strBackupPath").toString());
		objModel.setStrExportPath(jObjPOSConfigSetting.get("strExportPath").toString());
		objModel.setStrImagePath(jObjPOSConfigSetting.get("strImagePath").toString());
		objModel.setStrHOWebServiceUrl(jObjPOSConfigSetting.get("strHOWebServiceUrl").toString());
		objModel.setStrMMSWebServiceUrl(jObjPOSConfigSetting.get("strMMSWebServiceUrl").toString());
		objModel.setStrOS(jObjPOSConfigSetting.get("strOS").toString());
		objModel.setStrDefaultPrinter(jObjPOSConfigSetting.get("strDefaultPrinter").toString());
		objModel.setStrPrinterType(jObjPOSConfigSetting.get("strPrinterType").toString());
		objModel.setStrTouchScreenMode(jObjPOSConfigSetting.get("strTouchScreenMode").toString());
		objModel.setStrServerFilePath(jObjPOSConfigSetting.get("strServerFilePath").toString());
		objModel.setStrSelectWaiterFromCardSwipe(jObjPOSConfigSetting.get("strSelectWaiterFromCardSwipe").toString());
		objModel.setStrMySQBackupFilePath(jObjPOSConfigSetting.get("strMySQBackupFilePath").toString());
		objModel.setStrHOCommunication(jObjPOSConfigSetting.get("strHOCommunication").toString());
		objModel.setStrAdvReceiptPrinter(jObjPOSConfigSetting.get("strAdvReceiptPrinter").toString());
		
		return objPOSConfigSettingDao.funSavePOSConfigSetting(objModel);
	}
	
	
	public JSONObject funLoadPOSConfigSetting(String clientCode) throws Exception
	{
		return objPOSConfigSettingDao.funLoadPOSConfigSetting(clientCode);
	}


}
