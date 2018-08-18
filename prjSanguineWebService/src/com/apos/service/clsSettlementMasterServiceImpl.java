

package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfSettlementMasterDao;
import com.apos.model.clsOrderMasterModel;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsSettlementMasterModel;
import com.apos.model.clsSettlementMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsSettlementMasterService")
public class clsSettlementMasterServiceImpl implements clsSettlementMasterService
{
	@Autowired
	intfSettlementMasterDao	objSettlementMasterDao;

	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;
	public String funSaveUpdateSettlementMaster(JSONObject jObjAreaMaster)
	{
		String settlementCode = "";
		try
		{

			settlementCode = jObjAreaMaster.getString("SettlementCode");
			String settlementName = jObjAreaMaster.getString("SettlementName");
			String settlementType = jObjAreaMaster.getString("SettlementType");
			String billing = jObjAreaMaster.getString("Billing");
			String billPrintOnSettlement = jObjAreaMaster.getString("BillPrintOnSettlement");
			String advanceReceipt = jObjAreaMaster.getString("AdvanceReceipt");
			String applicable = jObjAreaMaster.getString("Applicable");
			double conversionRatio = jObjAreaMaster.getDouble("ConversionRatio");
			String accountCode = jObjAreaMaster.getString("AccountCode");
			String user = jObjAreaMaster.getString("User");
			String clientCode = jObjAreaMaster.getString("ClientCode");
			String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");

			if (settlementCode.trim().isEmpty())
			{
				List list=utility.funGetDocumentCode("POSSettlementMaster");
				 if (!list.get(0).toString().equals("0"))
					{
						String strCode = "0";
						String code = list.get(0).toString();
						StringBuilder sb = new StringBuilder(code);
						String ss = sb.delete(0, 1).toString();
						for (int i = 0; i < ss.length(); i++)
						{
							if (ss.charAt(i) != '0')
							{
								strCode = ss.substring(i, ss.length());
								break;
							}
						}
						int intCode = Integer.parseInt(strCode);
						intCode++;
						if (intCode < 10)
						{
							settlementCode = "S0" + intCode;
						}
						else if (intCode < 100)
						{
							settlementCode = "S" + intCode;
						}
						
						
					}
				    else
				    {
				    	settlementCode = "S01";
				    }
			}

			clsSettlementMasterModel objModel = new clsSettlementMasterModel(new clsSettlementMasterModel_ID(settlementCode, clientCode));
			
			objModel.setDblConvertionRatio(conversionRatio);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrAccountCode(accountCode);
			objModel.setStrAdvanceReceipt(advanceReceipt);
			objModel.setStrApplicable(applicable);
			objModel.setStrBilling(billing);
			objModel.setStrBillPrintOnSettlement(billPrintOnSettlement);
			objModel.setStrClientCode(clientCode);
			objModel.setStrDataPostFlag("N");
			objModel.setStrSettelmentCode(settlementCode);
			objModel.setStrSettelmentDesc(settlementName);
			objModel.setStrSettelmentType(settlementType);
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);
			objService.funSave(objModel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return settlementCode;
	}

	@Override
	public JSONObject funSelectedSettlementMasterData(String orderCode,String clientCode)
	{
		JSONObject jObjMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("settlementCode",orderCode);
			hmParameters.put("clientCode",clientCode);
			clsSettlementMasterModel objSettlementMasterModel = objSettlementMasterDao.funGetSettlementMasterData("getSettlementMaster", hmParameters);
			
			jObjMaster.put("strSettelmentCode",objSettlementMasterModel.getStrSettelmentCode());
		    jObjMaster.put("strSettelmentDesc",objSettlementMasterModel.getStrSettelmentDesc());
		    jObjMaster.put("strSettelmentType",objSettlementMasterModel.getStrSettelmentType());
		    jObjMaster.put("strApplicable",objSettlementMasterModel.getStrApplicable());
		    jObjMaster.put("strBilling",objSettlementMasterModel.getStrBilling());
		    jObjMaster.put("strAdvanceReceipt",objSettlementMasterModel.getStrAdvanceReceipt());
		    jObjMaster.put("strBillPrintOnSettlement",objSettlementMasterModel.getStrBillPrintOnSettlement());
		    jObjMaster.put("dblConvertionRatio",objSettlementMasterModel.getDblConvertionRatio());
		    jObjMaster.put("strAccountCode",objSettlementMasterModel.getStrAccountCode());
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMaster;
	}


	@Override
	public JSONObject funGetSettlementDtl(String clientCode)
{
		clsSettlementMasterModel model =new clsSettlementMasterModel();
	JSONObject jObj = new JSONObject();
try{
	JSONArray jArrData=new JSONArray();
	 
	List list=objService.funLoadAll(model,clientCode);
	clsSettlementMasterModel objModel = null;
		for(int cnt=0;cnt<list.size();cnt++)
		{
			JSONObject jobj = new JSONObject();
			
			objModel = (clsSettlementMasterModel) list.get(cnt);
			jobj.put("SettlementCode",objModel.getStrSettelmentCode());
			jobj.put("SettlementDesc",objModel.getStrSettelmentDesc());					
			jobj.put("ApplicableYN",true);					
			
			jArrData.put(jobj);
		}
		jObj.put("SettlementDtl", jArrData);
}
catch(Exception ex)
{
	ex.printStackTrace();
}

return jObj;
}
}

