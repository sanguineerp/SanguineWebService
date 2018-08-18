package com.apos.service;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.inftZoneMasterDao;
import com.apos.model.clsZoneMasterModel;
import com.apos.model.clsZoneMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsZoneMasterService")
public class clsZoneMasterServiceImpl implements clsZoneMasterService{
	
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	clsUtilityFunctions utility;
	
	@Autowired
	inftZoneMasterDao objinftDao;

	public String funSaveZoneMaster(JSONObject jObjZoneMaster){
		
		
			String zoneCode = "";
			try
			{
			   
				zoneCode = jObjZoneMaster.getString("ZoneCode");
			    String zoneName = jObjZoneMaster.getString("ZoneName");
			    String user = jObjZoneMaster.getString("User");
			    String clientCode = jObjZoneMaster.getString("ClientCode");
			    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
			    if (zoneCode.trim().isEmpty())
			    {
			    	long code=utility.funGetDocumentCodeFromInternal("POSZoneMaster");
					 if(code>0)
					 {
						 zoneCode = "Z" + String.format("%04d", code);
					 }

			    }
			    			   			   			    
			    
			    clsZoneMasterModel objModel = new clsZoneMasterModel(new clsZoneMasterModel_ID(zoneCode,clientCode));
			   
			    objModel.setStrZoneName(zoneName);
		        objModel.setStrClientCode(clientCode);
			    objModel.setStrUserCreated(user);
			    objModel.setStrUserEdited(user);
			    objModel.setDteDateCreated(dateTime);
			    objModel.setDteDateEdited(dateTime);
			    objModel.setStrDataPostFlag("N");
			    
			    objDao.funSave(objModel);
			    
			   // zoneCode = objZoneMasterDao.funSaveZoneMaster(objModel);
			}
			catch (Exception e)
			{
			    e.printStackTrace();
			}
			return zoneCode;

	}

	@Override
	public JSONObject funLoaddZoneMasterData(String searchCode,String clientCode) {
		
			JSONObject jobjData=new JSONObject();
			
			try
			{
				Map<String,String> hmParameters=new HashMap<String,String>();
				hmParameters.put("zoneCode",searchCode);
				hmParameters.put("clientCode",clientCode);
				
				clsZoneMasterModel objModel = objinftDao.funLoaddZoneMasterData("getZone", hmParameters);														
				
				jobjData.put("strZoneCode",objModel.getStrZoneCode());
				jobjData.put("strZoneName",objModel.getStrZoneName());
			
				System.out.println();
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return jobjData;
		
	}
	
	public String funGetAllZoneForMaster(String clientCode)
	{
		return objinftDao.funGetAllZoneForMaster(clientCode);
	}


}
