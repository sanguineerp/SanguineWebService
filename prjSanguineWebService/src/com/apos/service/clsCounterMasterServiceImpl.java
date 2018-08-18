package com.apos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsCounterMasterDao;
import com.apos.dao.intfCounterMasterDao;
import com.apos.dao.intfFactoryMasterDao;
import com.apos.model.clsCounterMasterHdModel;
import com.apos.model.clsCounterMasterModel_ID;
import com.apos.model.clsFactoryMasterHdModel;
import com.apos.model.clsPosCounterDtlModel;
import com.apos.model.clsTaxSettlementDetailsModel;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsCounterMasterService")
public class clsCounterMasterServiceImpl implements clsCounterMasterService{

	@Autowired
	private intfCounterMasterDao objCounterMasterDao ;
	

	@Autowired
	private clsPosCounterDetailsService objSettle;

	@Autowired
	private intfBaseDao objDao;

	@Autowired
	clsUtilityFunctions utility;

	public String funSaveUpdateCounterMaster(JSONObject jObjCounterMaster) {
		String counterCode = "";
		try {
			counterCode = jObjCounterMaster.getString("CounterCode");
			String counterName = jObjCounterMaster.getString("CounterName");
			String operational = jObjCounterMaster.getString("Operational");
			String posName = jObjCounterMaster.getString("POSName");
			String userName = jObjCounterMaster.getString("UserName");
			String user = jObjCounterMaster.getString("User");
			String clientCode = jObjCounterMaster.getString("ClientCode");
			String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");

//			if (counterCode.trim().isEmpty()) {
//				counterCode = objCounterMasterDao.funGenerateCounterCode();
//			}

			if (counterCode.trim().isEmpty())
			{
				long code=utility.funGetDocumentCodeFromInternal("POSCounterMaster");
				 if (code>0)
					{
						String strCode = "0";
						
						counterCode = "CT0" + code;

					}
				    else
				    {
				    	counterCode = "CT01";
				    }
				
			}

			clsCounterMasterHdModel objModel = new clsCounterMasterHdModel(
					new clsCounterMasterModel_ID(counterCode, clientCode));
			objModel.setStrCounterName(counterName);
			objModel.setStrOperational(operational);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrDataPostFlag("");
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);
			objModel.setStrPOSCode(posName);
			objModel.setStrUserCode(userName);
			// objCounterMasterDao.funSaveUpdateCounterMaster(objModel);

			JSONArray jArrOb = jObjCounterMaster
					.getJSONArray("MenuHeadDetails");

//			objSettle.funSavePosCounterDetails(jArrOb, counterCode, clientCode);

			JSONObject jObj = new JSONObject();
			JSONObject jObjItem = new JSONObject();
			List<clsPosCounterDtlModel> listMenuDtl = new ArrayList<clsPosCounterDtlModel>();
			for (int i = 0; i < jArrOb.length(); i++) {
				jObj = jArrOb.getJSONObject(i);

				clsPosCounterDtlModel objMenuModel = new clsPosCounterDtlModel();
				objMenuModel.setStrMenuCode(jObj.getString("MenuCode"));

				listMenuDtl.add(objMenuModel);

			}

			objModel.setListMenuDtl(listMenuDtl);
			objDao.funSave(objModel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return counterCode;
	}

	public JSONObject funGetSelectedCounterMasterData(String counterCode,String clientCode)
	{
		JSONObject jObjCounterMaster=new JSONObject();
		JSONArray jMenuData=new JSONArray();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("counterCode",counterCode);
			hmParameters.put("clientCode",clientCode);
			clsCounterMasterHdModel model = objCounterMasterDao.funGetSelectedCounterMasterData("getCounterMaster", hmParameters);
			
			// Write code to convert model into json object.
//			jObjCounterMaster.put("strFactoryCode",model.getStrFactoryCode());
//			jObjCounterMaster.put("strFactoryName",model.getStrFactoryName());
			List<clsPosCounterDtlModel> listMenuDtl =model.getListMenuDtl();
			 Iterator itr = listMenuDtl.iterator();
		        while(itr.hasNext())
		        {
		        	clsPosCounterDtlModel objSettle=(clsPosCounterDtlModel)itr.next();
				JSONObject jObjMenu=new JSONObject();
				jObjMenu.put("MenuCode",objSettle.getStrMenuCode());
				
				jObjMenu.put("ApplicableYN",true);
				jMenuData.put(jObjMenu);
				
			}
		        jObjCounterMaster.put("strCounterCode",model.getStrCounterCode());
		        jObjCounterMaster.put("strCounterName",model.getStrCounterName());
		        jObjCounterMaster.put("strOperational",model.getStrOperational());
		        jObjCounterMaster.put("strUserCode",model.getStrUserCode());
		        jObjCounterMaster.put("strPOSCode",model.getStrPOSCode());
		        jObjCounterMaster.put("MenuDtl",jMenuData);
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjCounterMaster;
	}

}
