package com.apos.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.intfPOSMasterDao;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsPOSMasterModel_ID;
import com.apos.model.clsPosSettlementDetailsModel;
import com.apos.model.clsReorderTimeModel;
import com.sanguine.service.intfBaseService;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Service("clsPOSMasterService")
public class clsPOSMasterServiceImpl implements clsPOSMasterService{
	@Autowired
	private intfPOSMasterDao objPOSMasterDao;
	
	@Autowired
	private intfBaseService objService;
	
	@Autowired
	private clsUtilityFunctions utility;

	public String funAddUpdatePOSMaster(JSONObject jObjPOSMaster){
		String posCode = "";
		try
		{
		    
			posCode = jObjPOSMaster.getString("PosCode");
			String posName = jObjPOSMaster.getString("PosName");
		    String posType = jObjPOSMaster.getString("PosType");
		    String debitCardTransactionYN = jObjPOSMaster.getString("DebitCardTransactionYN");
		    String propertyPosCode = jObjPOSMaster.getString("PropertyPOSCode");
		    String operational = jObjPOSMaster.getString("OperationalYN");
		    String counterWiseBilling = jObjPOSMaster.getString("CounterWiseBilling");
		  
		    
		    String delayedSettlementForDB = jObjPOSMaster.getString("DelayedSettlementForDB");
		    String billPrinterPort = jObjPOSMaster.getString("BillPrinterPort");
		    String advReceiptPrinterPort = jObjPOSMaster.getString("AdvReceiptPrinterPort");
		    String vatNo = jObjPOSMaster.getString("VatNo");
		    String printVatNo = jObjPOSMaster.getString("PrintVatNo");
		    String serviceTaxNo = jObjPOSMaster.getString("ServiceTaxNo");
		  
		    String printServiceTaxNo = jObjPOSMaster.getString("PrintServiceTaxNo");
		    String roundOff = jObjPOSMaster.getString("RoundOff");
		    String tip = jObjPOSMaster.getString("Tip");
		    String discount = jObjPOSMaster.getString("Discount");
		    String wSLocationCode = jObjPOSMaster.getString("WSLocationCode");
		    String exciseLicenceCode = jObjPOSMaster.getString("ExciseLicenceCode");
		    
		    
		    String enableShift = jObjPOSMaster.getString("EnableShift");
		    String  user = jObjPOSMaster.getString("User");
		    String clientCode = jObjPOSMaster.getString("ClientCode");
		    String dateTime = utility.funGetCurrentDateTime("yyyy-MM-dd");
	    
		    if (posCode.trim().isEmpty())
		    {
		    	List list=utility.funGetDocumentCode("POSMaster");
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
							posCode = "P0" + intCode;
						}
						else if (intCode < 100)
						{
							posCode = "P" + intCode;
						}
						
						
					}
				    else
				    {
				    	posCode = "P01";
				    }
		    }
		    
		    clsPOSMasterModel objModel=new clsPOSMasterModel(new clsPOSMasterModel_ID(posCode,clientCode));
		   
		    objModel.setStrPosName(posName);
		    objModel.setStrPosType(posType);
		    objModel.setStrDebitCardTransactionYN(debitCardTransactionYN);
		    objModel.setStrPropertyPOSCode(propertyPosCode);
		    objModel.setStrOperationalYN(operational);
		    objModel.setStrCounterWiseBilling(counterWiseBilling);
		    objModel.setStrDelayedSettlementForDB(delayedSettlementForDB);
		    objModel.setStrBillPrinterPort(billPrinterPort);
		    objModel.setStrAdvReceiptPrinterPort(advReceiptPrinterPort);
		    objModel.setStrVatNo(vatNo);
		    objModel.setStrPrintVatNo(printVatNo);
		    objModel.setStrServiceTaxNo(serviceTaxNo);
		    objModel.setStrPrintServiceTaxNo(printServiceTaxNo);
		    objModel.setStrRoundOff(roundOff);
		    objModel.setStrTip(tip);
		    objModel.setStrDiscount(discount);
		    objModel.setStrWSLocationCode(wSLocationCode);
		    objModel.setStrExciseLicenceCode(exciseLicenceCode);
		    objModel.setStrEnableShift(enableShift);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		 
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    
		    
		    JSONArray settleList=jObjPOSMaster.getJSONArray("SettlementDetails");
		    Set<clsPosSettlementDetailsModel> listsettlementDtl = new HashSet<clsPosSettlementDetailsModel>();
		    for(int i=0;i<settleList.length();i++)
		    {
		    	JSONObject jObj = new JSONObject();
		    	jObj=settleList.getJSONObject(i);
		    	String settlementCode=jObj.getString("SettlementCode");
		    	String settlementDesc=jObj.getString("SettlementDesc");
		    	
		    	clsPosSettlementDetailsModel objSettlementModel = new clsPosSettlementDetailsModel();
		    
		    	objSettlementModel.setStrSettlementCode(settlementCode);
		    	objSettlementModel.setStrSettlementDesc(settlementDesc);
		    	
		    	objSettlementModel.setStrDataPostFlag("Y");
		    	listsettlementDtl.add(objSettlementModel);
		    	
		    }
		    
		    objModel.setListsettlementDtl(listsettlementDtl);
		    
		    JSONArray reorderTimeList=jObjPOSMaster.getJSONArray("ReorderTime");
		    Set<clsReorderTimeModel> listReorderTimeDtl = new HashSet<clsReorderTimeModel>();
		    for(int i=0;i<reorderTimeList.length();i++)
		    {
		    	JSONObject jObj = new JSONObject();
		    
		    	jObj=reorderTimeList.getJSONObject(i);
		    	String fromTime=jObj.getString("FromTime");
		    	String toTime=jObj.getString("ToTime");
		    clsReorderTimeModel objReorderTimeModel = new clsReorderTimeModel();
		    
		   
		    objReorderTimeModel.setTmeFromTime(fromTime);
		    objReorderTimeModel.setTmeToTime(toTime);
	    	objReorderTimeModel.setStrUserCreated(user);
	    	objReorderTimeModel.setStrUserEdited(user);
	    	objReorderTimeModel.setDteDateCreated(dateTime);
	    	objReorderTimeModel.setDteDateEdited(dateTime);
	    	objReorderTimeModel.setStrDataPostFlag("Y");
		    	listReorderTimeDtl.add(objReorderTimeModel);
		    	
		    }
		    
		    objModel.setListReorderTimeDtl(listReorderTimeDtl);;
		    
		    objService.funSave(objModel);
		
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return posCode; 
	    }
	
	
	
	public clsPOSMasterModel funGetPOSNameData(String strPOSName){
		
		return objPOSMasterDao.funGetPOSNameData(strPOSName);
	}
	

	public JSONObject funSelectedPOSMasterData(String posCode,String clientCode)
	{
		JSONObject jObjPOSMaster=new JSONObject();
		JSONArray jSettleData=new JSONArray();
		 JSONArray jReorderTimeData=new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("posCode",posCode);
			hmParameters.put("clientCode",clientCode);
			clsPOSMasterModel objPOSModel = objPOSMasterDao.funGetPOSMasterData("getPOSMaster", hmParameters);
			Set<clsPosSettlementDetailsModel> listsettlementDtl =objPOSModel.getListsettlementDtl();
			 Iterator itr = listsettlementDtl.iterator();
		        while(itr.hasNext())
		        {
		        	clsPosSettlementDetailsModel objSettle=(clsPosSettlementDetailsModel)itr.next();
				JSONObject jObjSettle=new JSONObject();
				jObjSettle.put("SettlementCode",objSettle.getStrSettlementCode());
				jObjSettle.put("SettlementDesc",objSettle.getStrSettlementDesc());
				jObjSettle.put("ApplicableYN",true);
				jSettleData.put(jObjSettle);
				
			}
			Set<clsReorderTimeModel> listReorderTimeDtl=objPOSModel.getListReorderTimeDtl();
			
			 itr = listReorderTimeDtl.iterator();
		        while(itr.hasNext())
		        {
		        	clsReorderTimeModel objReorderTime=(clsReorderTimeModel)itr.next();
					JSONObject jObjReorderTime=new JSONObject();
					jObjReorderTime.put("FromTime",objReorderTime.getTmeFromTime());
					jObjReorderTime.put("ToTime",objReorderTime.getTmeToTime());
					
					jReorderTimeData.put(jObjReorderTime);
				
			}
		        jObjPOSMaster.put("strPosCode",objPOSModel.getStrPosCode());
			    jObjPOSMaster.put("strPosName",objPOSModel.getStrPosName()); 
			    jObjPOSMaster.put("strPosType",objPOSModel.getStrPosType());
			    jObjPOSMaster.put("strDebitCardTransactionYN",objPOSModel.getStrDebitCardTransactionYN());
			    jObjPOSMaster.put("strPropertyPOSCode",objPOSModel.getStrPropertyPOSCode());
			   
			    jObjPOSMaster.put("strCounterWiseBilling",objPOSModel.getStrCounterWiseBilling());
			    jObjPOSMaster.put("strDelayedSettlementForDB",objPOSModel.getStrDelayedSettlementForDB());
			    jObjPOSMaster.put("strBillPrinterPort",objPOSModel.getStrBillPrinterPort());
			    jObjPOSMaster.put("strAdvReceiptPrinterPort",objPOSModel.getStrAdvReceiptPrinterPort());
			    jObjPOSMaster.put("strOperationalYN",objPOSModel.getStrOperationalYN());
			    jObjPOSMaster.put("strVatNo",objPOSModel.getStrVatNo());
			    
			    jObjPOSMaster.put("strPrintVatNo",objPOSModel.getStrPrintVatNo());
			    jObjPOSMaster.put("strServiceTaxNo",objPOSModel.getStrServiceTaxNo());
			    jObjPOSMaster.put("strPrintServiceTaxNo",objPOSModel.getStrPrintServiceTaxNo());
			    
			    jObjPOSMaster.put("strRoundOff",objPOSModel.getStrRoundOff());
			    jObjPOSMaster.put("strTip",objPOSModel.getStrTip());
			    jObjPOSMaster.put("strDiscount",objPOSModel.getStrDiscount());
			    jObjPOSMaster.put("strWSLocationCode",objPOSModel.getStrWSLocationCode());
			    jObjPOSMaster.put("strExciseLicenceCode",objPOSModel.getStrExciseLicenceCode());
			    jObjPOSMaster.put("SettleData",jSettleData);
			    jObjPOSMaster.put("ReorderTimeData",jReorderTimeData);
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjPOSMaster;
	}


public String funGetAllPOSForMaster(String clientCode)
{
	clsPOSMasterModel model =new clsPOSMasterModel();
	JSONObject jObj = new JSONObject();
try{
	JSONArray jArrData=new JSONArray();
	 
	List list=objService.funLoadAll(model,clientCode);
	 clsPOSMasterModel objTaxModel = null;
		for(int cnt=0;cnt<list.size();cnt++)
		{
			objTaxModel= (clsPOSMasterModel) list.get(cnt);
		    
		    JSONObject jArrDataRow = new JSONObject();
		    jArrDataRow.put("strPosCode",objTaxModel.getStrPosCode());
		    jArrDataRow.put("strPosName",objTaxModel.getStrPosName());
		   
		    jArrData.put(jArrDataRow);
		}
		jObj.put("POSList", jArrData);
}
catch(Exception ex)
{
	ex.printStackTrace();
}

return jObj.toString();
}

public String funGetPrintVatNoPOS(String posCode)
{
	JSONObject objJSON=new JSONObject();
	JSONArray jArrData = new JSONArray();
	String printVatNo="";
	clsDatabaseConnection objDb=new clsDatabaseConnection();
   Connection posCon=null;
   Statement st=null;
   
   try
   {        	
   	posCon=objDb.funOpenAPOSCon("mysql","master");
       st = posCon.createStatement();
       
       long code = 0;
       String sql_getPrintVatNoPOS = "select a.strPrintVatNo from tblposmaster a "
       		+ " where a.strPosCode='"+posCode+"' ";
       System.out.println(sql_getPrintVatNoPOS);
       ResultSet rsGetPrintVatNoPOS = st.executeQuery(sql_getPrintVatNoPOS);

       if (rsGetPrintVatNoPOS.next())
       {
       	printVatNo =rsGetPrintVatNoPOS.getString(1);
       	
           System.out.println(printVatNo);
       }
       objJSON.put("printVatNo", printVatNo);
      
       
       st.close();
       posCon.close();
   }
   catch (Exception e)
   {
       e.printStackTrace();
   }
   finally
   {
   	return objJSON.toString();
   }
}

public String funGetVatNoPOS(String posCode)
{
	JSONObject objJSON=new JSONObject();
	String vatNo="";
	clsDatabaseConnection objDb=new clsDatabaseConnection();
   Connection posCon=null;
   Statement st=null;
   
   try
   {        	
   	posCon=objDb.funOpenAPOSCon("mysql","master");
       st = posCon.createStatement();
       
       long code = 0;
       String sql_getVatNoPOS = "select a.strVatNo from tblposmaster a "
       		+ " where a.strPosCode='"+posCode+"' ";
       System.out.println(sql_getVatNoPOS);
       ResultSet rsGetVatNoPOS = st.executeQuery(sql_getVatNoPOS);

       if (rsGetVatNoPOS.next())
       {
       	
       	vatNo=rsGetVatNoPOS.getString(1);
       	
           System.out.println(vatNo);
       }
     
       objJSON.put("vatNo", vatNo);
     
       
       st.close();
       posCon.close();
   }
   catch (Exception e)
   {
       e.printStackTrace();
   }
   finally
   {
   	return objJSON.toString();
   }
}	

public String funGetPrintServiceTaxNoPOS(String posCode)
{
	JSONObject objJSON=new JSONObject();
	String printServiceTaxNo="";
	clsDatabaseConnection objDb=new clsDatabaseConnection();
   Connection posCon=null;
   Statement st=null;
   
   try
   {        	
   	posCon=objDb.funOpenAPOSCon("mysql","master");
       st = posCon.createStatement();
       
       long code = 0;
       String sql_getPrintServiceTaxNoPOS = "select a.strPrintServiceTaxNo from tblposmaster a "
       		+ " where a.strPosCode='"+posCode+"' ";
       System.out.println(sql_getPrintServiceTaxNoPOS);
       ResultSet rsGetServiceTaxNoPOS = st.executeQuery(sql_getPrintServiceTaxNoPOS);

       if (rsGetServiceTaxNoPOS.next())
       {
       	
       	printServiceTaxNo=rsGetServiceTaxNoPOS.getString(1);
       	
           System.out.println(printServiceTaxNo);
       }
     
       objJSON.put("printServiceTaxNo", printServiceTaxNo);
     
       
       st.close();
       posCon.close();
   }
   catch (Exception e)
   {
       e.printStackTrace();
   }
   finally
   {
   	return objJSON.toString();
   }
}	
public String funGetServiceTaxNoPOS(String posCode)
{
	JSONObject objJSON=new JSONObject();
	String serviceTaxNo="";
	clsDatabaseConnection objDb=new clsDatabaseConnection();
   Connection posCon=null;
   Statement st=null;
   
   try
   {        	
   	posCon=objDb.funOpenAPOSCon("mysql","master");
       st = posCon.createStatement();
       
       long code = 0;
       String sql_getServiceTaxNoPOS = "select a.strServiceTaxNo from tblposmaster a "
       		+ " where a.strPosCode='"+posCode+"' ";
       System.out.println(sql_getServiceTaxNoPOS);
       ResultSet rsGetServiceTaxNoPOS = st.executeQuery(sql_getServiceTaxNoPOS);

       if (rsGetServiceTaxNoPOS.next())
       {
       	
       	serviceTaxNo=rsGetServiceTaxNoPOS.getString(1);
       	
           System.out.println(serviceTaxNo);
       }
     
       objJSON.put("serviceTaxNo", serviceTaxNo);
     
       
       st.close();
       posCon.close();
   }
   catch (Exception e)
   {
       e.printStackTrace();
   }
   finally
   {
   	return objJSON.toString();
   }
}	 

public String funGetPOSName(String posCode)
{
	return objPOSMasterDao.funGetPOSName(posCode);
}

}


