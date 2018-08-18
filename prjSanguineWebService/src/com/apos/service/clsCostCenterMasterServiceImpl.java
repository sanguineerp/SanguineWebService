package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsCostCenterMasterDao;
import com.apos.dao.intfCostCenterMasterDao;
import com.apos.dao.intfTaxMasterDao;
import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsCostCenterMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsCostCenterMasterServiceImpl")
public class clsCostCenterMasterServiceImpl implements clsCostCenterMasterService
{
	@Autowired
	private intfCostCenterMasterDao objCostCenterMasterDao ;

	@Autowired
	private intfBaseDao objDao;
	
	@Autowired
	clsUtilityFunctions utility;
	

	public String funSaveUpdateCostCentersMaster(JSONObject jObjCostCenterMaster)
	{
		String costCenterCode = "";
		try
		{
		    
		    costCenterCode = jObjCostCenterMaster.getString("strCostCenterCode");
		    String costCenterName = jObjCostCenterMaster.getString("strCostCenterName");
		    String printerPort = jObjCostCenterMaster.getString("strPrinterPort");
		    String secondaryPrinterPort = jObjCostCenterMaster.getString("strSecondaryPrinterPort");
		    String printOnBothPrinters = jObjCostCenterMaster.getString("strPrintOnBothPrinters");
		   
		    
		    String labelOnKOT = jObjCostCenterMaster.getString("strLabelOnKOT");
		    String  user = jObjCostCenterMaster.getString("User");
		    String clientCode = jObjCostCenterMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
			
		    
		    String code="";
		    if (costCenterCode.trim().isEmpty())
			{
				List list=utility.funGetDocumentCode("POSCostCenterMaster");
				 if (!list.get(0).toString().equals("0"))
					{
						String strCode = "0";
						code = list.get(0).toString();
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
						if(intCode<10)
						{
						costCenterCode = "C0" + intCode;
						}
						else
						{
							costCenterCode = "C" + intCode;
						}
						
					}
				    else
				    {
				    	code="0";
				    	costCenterCode = "C01";
				    }
				
			}
					    
		    
		    clsCostCenterMasterModel objModel = new clsCostCenterMasterModel(new clsCostCenterMasterModel_ID(costCenterCode, clientCode));
		    objModel.setStrCostCenterName(costCenterName);
		    objModel.setStrPrinterPort(printerPort);
		    objModel.setStrSecondaryPrinterPort(secondaryPrinterPort);
		    objModel.setStrPrintOnBothPrinters(printOnBothPrinters);
		    objModel.setStrLabelOnKOT(labelOnKOT);
		    objModel.setStrClientCode(clientCode); 
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    //costCenterCode = objCostCenterMasterDao.funSaveCostCenterMaster(objModel);
		    objDao.funSave(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return costCenterCode;
	    }
	    
	public JSONObject funGetSelectedCostCenterMasterData(String costCenterCode,String clientCode)
	{
		JSONObject jObjCostCenterMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("costCenterCode",costCenterCode);
			hmParameters.put("clientCode",clientCode);
			clsCostCenterMasterModel model = objCostCenterMasterDao.funGetSelectedCostCenterMasterData("getCostCenterMaster", hmParameters);
			
			// Write code to convert model into json object.
			jObjCostCenterMaster.put("strCostCenterCode",model.getStrCostCenterCode());
			jObjCostCenterMaster.put("strCostCenterName",model.getStrCostCenterName());
			jObjCostCenterMaster.put("strPrinterPort",model.getStrPrinterPort());
			jObjCostCenterMaster.put("strSecondaryPrinterPort",model.getStrSecondaryPrinterPort());
			jObjCostCenterMaster.put("strPrintOnBothPrinters",model.getStrPrintOnBothPrinters());
			jObjCostCenterMaster.put("strLabelOnKOT",model.getStrLabelOnKOT());
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjCostCenterMaster;
	}
	
	 public String funGetAllCostCentersForMaster(String clientCode)
	    {
	    	return objCostCenterMasterDao.funGetAllCostCentersForMaster(clientCode);
	    }

    
	
}
