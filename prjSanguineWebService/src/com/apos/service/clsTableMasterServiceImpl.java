package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfTableMasterDao;
import com.apos.model.clsTableMasterModel;
import com.apos.model.clsTableMasterModel_ID;
import com.apos.model.clsWaiterMasterModel;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service(value = "clsPOSTableMasterService")

public class clsTableMasterServiceImpl implements clsTableMasterService{
	@Autowired
	private intfTableMasterDao objTableMasterDao;


	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;
	
	public String funAddUpdateTableMaster(JSONObject jObjTableMaster){
		String tableCode = "";
		int intSeq=0;
		try
		{
		    
			tableCode = jObjTableMaster.getString("TableCode");
			String tableName = jObjTableMaster.getString("TableName");
		    String waiterName = jObjTableMaster.getString("WaiterName");
		    String areaName = jObjTableMaster.getString("AreaName");
		    int paxCapacity = jObjTableMaster.getInt("PaxCapacity");
		    String operational = jObjTableMaster.getString("Operational");
		    String posName = jObjTableMaster.getString("POSName");
		    String user = jObjTableMaster.getString("User");
		    String clientCode = jObjTableMaster.getString("ClientCode");
		    String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");
		    
		    if (tableCode.trim().isEmpty())
		    {
		    	
		    	List list=utility.funGetDocumentCode("POSTableMaster");
				 if (!list.get(0).toString().equals("0"))
					{
						String strCode = "0";
						Object[] obj=(Object[])list.get(0);
						String code = obj[0].toString();
						intSeq= (int)obj[1]+1;
						StringBuilder sb = new StringBuilder(code);
						String ss = sb.delete(0, 2).toString();
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
							tableCode = "TB000000" + intCode;
						}
						else if (intCode < 100)
						{
							tableCode = "TB00000" + intCode;
						}
						else if (intCode < 1000)
						{
							tableCode = "TB0000" + intCode;
						}
						else if (intCode < 10000)
						{
							tableCode = "TB000" + intCode;
						}
						else if (intCode < 100000)
						{
							tableCode = "TB00" + intCode;
						}
						else if (intCode < 1000000)
						{
							tableCode = "TB0" + intCode;
						}
						else if (intCode < 10000000)
						{
							tableCode = "TB" + intCode;
						}
					}
				    else
				    {
				    	tableCode = "TB0000001";
				    	intSeq=0;
				    }
		    }
		    
		    clsTableMasterModel objModel=new clsTableMasterModel(new clsTableMasterModel_ID(tableCode, clientCode));
		    objModel.setIntSequence(intSeq);
		    objModel.setStrTableName(tableName);
		    objModel.setStrWaiterNo(waiterName);
		    objModel.setStrPOSCode(posName);
		    objModel.setIntPaxNo(paxCapacity);
		    objModel.setStrOperational(operational);
		    objModel.setStrAreaCode(areaName);
		    objModel.setStrStatus("Normal");
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    
		    objService.funSave(objModel);
		 
		  
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return tableCode; 
	    }
	
	@Override
	public JSONObject funSelectedTableMasterData(String tableNo,String clientCode)
	{
		JSONObject jObjMaster=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("tableNo",tableNo);
			hmParameters.put("clientCode",clientCode);
			clsTableMasterModel objTableModel = objTableMasterDao.funGetTableMasterData("getTableMaster", hmParameters);
			
			jObjMaster.put("strTableNo",objTableModel.getStrTableNo()); 
			jObjMaster.put("strTableName",objTableModel.getStrTableName());
			jObjMaster.put("strAreaCode",objTableModel.getStrAreaCode());
			jObjMaster.put("strWaiterNo",objTableModel.getStrWaiterNo());
		    
			jObjMaster.put("intPaxNo",objTableModel.getIntPaxNo());
			jObjMaster.put("strOperational",objTableModel.getStrOperational());
			jObjMaster.put("strPOSCode",objTableModel.getStrPOSCode());
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMaster;
	}
	public JSONObject funGetTableDtl(String clientCode)
	{
		return objTableMasterDao.funGetTableDtl(clientCode);
	}

	
	public JSONObject funGetTableList(String posCode,String clientCode)
	{
		clsTableMasterModel model =new clsTableMasterModel();
		JSONObject jObj = new JSONObject();
	try{
		JSONArray jArrData=new JSONArray();
		 
		List list=objService.funLoadAll(model,clientCode);
		clsTableMasterModel objTaxModel = null;
			for(int cnt=0;cnt<list.size();cnt++)
			{
				objTaxModel= (clsTableMasterModel) list.get(cnt);
			    
			    JSONObject jArrDataRow = new JSONObject();
			    jArrDataRow.put("strTableNo",objTaxModel.getStrTableNo());
			    jArrDataRow.put("strTableName",objTaxModel.getStrTableName());
			    jArrData.put(jArrDataRow);
			}
			jObj.put("TableList", jArrData);
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}

	return jObj;
	
	}

	
	public String funSaveTableSequence(JSONObject jObjTableMaster){
		String tableCode = "";
		
		try
		{
		   
		    String clientCode = jObjTableMaster.getString("clientCode");
		  
		    
		    JSONArray tableList=jObjTableMaster.getJSONArray("TableDetails");
		    for(int i=0;i<tableList.length();i++)
		    {
		    	 JSONObject jObj = new JSONObject();
			    	jObj=tableList.getJSONObject(i);
			    	long sequence=jObj.getLong("Sequence");
			    	String tableNo=jObj.getString("TableNo");
			    	Map<String,String> hmParameters=new HashMap<String,String>();
					hmParameters.put("tableNo",tableNo);
					hmParameters.put("clientCode",clientCode);
					clsTableMasterModel objTableModel = objTableMasterDao.funGetTableMasterData("getTableMaster", hmParameters);
					
			    	objTableModel.setIntSequence(sequence);
			    
			    	objService.funSave(objTableModel);
		    }
		  
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return tableCode; 
	    }
	

}


