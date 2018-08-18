package com.apos.dao;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsPOSConfigSettingHdModel;
import com.apos.model.clsPOSConfigSettingModel_ID;

@Repository("clsPOSConfigSettingDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSConfigSettingDao{

	@Autowired
	private SessionFactory WebPOSSessionFactory;

	@SuppressWarnings("finally")
	public String funSavePOSConfigSetting(clsPOSConfigSettingHdModel objModel) throws Exception
	{
		String save="";
		try
		{
		WebPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
		save="saved";
		}catch(Exception ex)
		{
			ex.printStackTrace();
			save="failed";
		}finally
		{
			return  save;
		}
		
	}
	
	
	public JSONObject funLoadPOSConfigSetting(String clientCode) throws Exception
	{
		JSONObject jObjData = new JSONObject();
		JSONArray jArrConfigData = new JSONArray();
		Query query = WebPOSSessionFactory.getCurrentSession().createQuery("from clsPOSConfigSettingHdModel where strClientCode= :clientCode");
		query.setParameter("clientCode", clientCode);
		List list = query.list();
		clsPOSConfigSettingHdModel objModel = null;
		if (list.size() > 0)
		{
				JSONObject jobj = new JSONObject();

				objModel = (clsPOSConfigSettingHdModel) list.get(0);
				jobj.put("strServer",objModel.getStrServer());
				jobj.put("strDBName",objModel.getStrDBName()); 
				jobj.put("strUserID",objModel.getStrUserID() ); 
				jobj.put("strPassword",objModel.getStrPassword() ); 
				jobj.put("strIPAddress",objModel.getStrIPAddress() );
				jobj.put("strPort",objModel.getStrPort() );
				jobj.put("strBackupPath",objModel.getStrBackupPath() );
				jobj.put("strExportPath",objModel.getStrExportPath() );
				jobj.put("strImagePath",objModel.getStrImagePath() );
				jobj.put("strHOWebServiceUrl",objModel.getStrHOWebServiceUrl() );
				jobj.put("strMMSWebServiceUrl",objModel.getStrMMSWebServiceUrl() );
				jobj.put("strOS",objModel.getStrOS() );
				jobj.put("strDefaultPrinter",objModel.getStrDefaultPrinter() );
				jobj.put("strPrinterType",objModel.getStrPrinterType() );
				jobj.put("strTouchScreenMode",objModel.getStrTouchScreenMode());
				jobj.put("strServerFilePath",objModel.getStrServerFilePath() );
				jobj.put("strSelectWaiterFromCardSwipe",objModel.getStrSelectWaiterFromCardSwipe() );
				jobj.put("strMySQBackupFilePath",objModel.getStrMySQBackupFilePath() );
				jobj.put("strHOCommunication",objModel.getStrHOCommunication() );
				jobj.put("strAdvReceiptPrinter",objModel.getStrAdvReceiptPrinter() );
				jobj.put("strClientCode",objModel.getStrClientCode());

				jArrConfigData.put(jobj);
		}
			jObjData.put("configSetting", jArrConfigData);
		return 	jObjData;
			
	}

	


}
