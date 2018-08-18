package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.apos.model.clsWaiterMasterModel;
import com.apos.model.clsWaiterMasterModel_ID;
import com.apos.dao.intfWaiterMasterDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;


@Service(value = "clsWaiterMasterService")

public class clsWaiterMasterServiceImpl implements clsWaiterMasterService{
	@Autowired
	private intfWaiterMasterDao objWaiterMasterDao;

	@Autowired
	private intfBaseService objService;
	
	@Autowired
	clsUtilityFunctions utility;
	public String funAddUpdateWaiterMaster(JSONObject objWaiterMaster){
		String waiterNo = "";
		try
		{
		    
			waiterNo = objWaiterMaster.getString("WaiterNo");
			String WSName = objWaiterMaster.getString("WaiterShortName");
		    String WFName = objWaiterMaster.getString("WaiterFullName");
		    String operational = objWaiterMaster.getString("Operational");
		    String debitCardString = objWaiterMaster.getString("DebitCardString");
		    
		    String posName = objWaiterMaster.getString("POSCode");
		    String user = objWaiterMaster.getString("User");
		    String clientCode = objWaiterMaster.getString("ClientCode");
		    String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");
		    
		    if (waiterNo.trim().isEmpty())
		    {
		    	List list=utility.funGetDocumentCode("POSWaiterMaster");
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
							waiterNo = "W0" + intCode;
						}
						else if (intCode < 100)
						{
							waiterNo = "W" + intCode;
						}
						
						
					}
				    else
				    {
				    	waiterNo = "W01";
				    }
		    }
		    
		    clsWaiterMasterModel objModel=new clsWaiterMasterModel(new clsWaiterMasterModel_ID(waiterNo, clientCode));
		    objModel.setStrWShortName(WSName);
		    objModel.setStrWFullName(WFName);
		    objModel.setStrDebitCardString(debitCardString);
		    objModel.setStrPOSCode(posName);
		   
		    objModel.setStrOperational(operational);
		   
		    objModel.setStrStatus("Normal");
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    
		    objService.funSave(objModel);
		  //  objWaiterMasterDao.funAddUpdateWaiterMaster(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return waiterNo; 
	}

	@Override
	public JSONObject funSelectedWaiterMasterData(String waiterNo,String clientCode)
	{
		JSONObject jObjMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("waiterNo",waiterNo);
			hmParameters.put("clientCode",clientCode);
			clsWaiterMasterModel objOrderModel = objWaiterMasterDao.funGetWaiterMasterData("getWaiterMaster", hmParameters);
			
			jObjMaster.put("strWaiterNo",objOrderModel.getStrWaiterNo());
			jObjMaster.put("strWShortName",objOrderModel.getStrWShortName());
			jObjMaster.put("strWFullName",objOrderModel.getStrWFullName());
			jObjMaster.put("strOperational",objOrderModel.getStrOperational());
			jObjMaster.put("strDebitCardString",objOrderModel.getStrDebitCardString());
			jObjMaster.put("strPOSCode",objOrderModel.getStrPOSCode());
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMaster;
	}
	 public JSONObject funGetWaiterList(String clientCode)
		{
		 clsWaiterMasterModel model =new clsWaiterMasterModel();
			JSONObject jObj = new JSONObject();
		try{
			JSONArray jArrData=new JSONArray();
			 
			List list=objService.funLoadAll(model,clientCode);
			clsWaiterMasterModel objTaxModel = null;
				for(int cnt=0;cnt<list.size();cnt++)
				{
					objTaxModel= (clsWaiterMasterModel) list.get(cnt);
				    
				    JSONObject jArrDataRow = new JSONObject();
				    jArrDataRow.put("strWaiterNo",objTaxModel.getStrWaiterNo());
				    jArrDataRow.put("strWShortName",objTaxModel.getStrWShortName());
				   
				    jArrData.put(jArrDataRow);
				}
				jObj.put("waiterList", jArrData);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return jObj;
		
		}

}

