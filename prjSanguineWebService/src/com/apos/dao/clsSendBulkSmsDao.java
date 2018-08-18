package com.apos.dao;


import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsPOSMasterModel;
import com.apos.service.clsSetupService;
import com.webservice.util.clsUtilityFunctions;


@Repository("clsSendBulkSmsDao")
@Transactional(value = "webPOSTransactionManager")
public class clsSendBulkSmsDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    @Autowired
	private clsSetupService objSetupService;
	
    @Autowired
    private clsUtilityFunctions objUtilityFunctions;
    
	public JSONObject funFillCustomerTable(String custTypeCode,String areaCode,String dobCheck,String txtSms)
	{
		JSONObject jObjData=new JSONObject();
		try
	    {
		
			String sql = "";
            String filter = "";
            int month = 0;
            int day = 0;

            if (dobCheck.equalsIgnoreCase("true")) {
                Date currentDate = new Date();
                month = currentDate.getMonth();
                day = currentDate.getDay();

                month++;
                day--;
            }

            sql = "select a.strCustomerName,a.longMobileNo,a.dteDOB,c.strCustType,b.strBuildingName,a.strCustomerCode\n"
                    + "from tblcustomermaster a\n"
                    + "left outer join tblbuildingmaster b on a.strBuldingCode=b.strBuildingCode\n"
                    + "left outer join tblcustomertypemaster c on c.strCustTypeCode=a.strCustomerType ";

            if (custTypeCode.equalsIgnoreCase("All")) {
                if (areaCode.equalsIgnoreCase("All")) {
                    if (dobCheck.equalsIgnoreCase("true")) {
                        filter = filter + " where MONTH(a.dteDOB) = '" + month + "' AND DAY(a.dteDOB) ='" + day + "' ";
                    }
                } else {
                    filter = filter + " where b.strBuildingCode='" + areaCode + "' ";
                    if (dobCheck.equalsIgnoreCase("true")) {
                        filter = filter + " and MONTH(a.dteDOB) = '" + month + "' AND DAY(a.dteDOB) ='" + day + "' ";
                    }
                }
            } else {
                filter = filter + " where c.strCustTypeCode='" + custTypeCode + "' ";
                if (areaCode.equalsIgnoreCase("All")) {
                    if (dobCheck.equalsIgnoreCase("true")) {
                        filter = filter + " and MONTH(a.dteDOB) = '" + month + "' AND DAY(a.dteDOB) ='" + day + "' ";
                    }
                } else {
                    filter = filter + " and b.strBuildingCode='" + areaCode + "' ";
                    if (dobCheck.equalsIgnoreCase("true")) {
                        filter = filter + " and MONTH(a.dteDOB) = '" + month + "' AND DAY(a.dteDOB) ='" + day + "' ";
                    }
                }
            }
            filter = filter + " group by a.strCustomerCode";

            sql = sql + filter;
            System.out.println("cust sql=" + sql);
           
	
            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            List resultSet = query.list();
            
            JSONArray jArrData=new JSONArray();
            String customerCode="";
            
            if(resultSet!=null)
            {
            	for(int i=0; i<resultSet.size(); i++)
      			{
      				Object[] obj = (Object[]) resultSet.get(i);
      				
      				JSONObject jObj=new JSONObject();
      				
      				customerCode = obj[5].toString();
      				int noOfTimeVisited = funGetNoOfTimesVisited(customerCode);
      				jObj.put("strCustomerName",Array.get(obj, 0));
      				jObj.put("longMobileNo",Array.get(obj, 1));
      				jObj.put("dteDOB",Array.get(obj, 2));
      				jObj.put("strCustType",Array.get(obj, 3));
      				jObj.put("strBuildingName",Array.get(obj, 4));
					//jObj.put("strCustomerCode",Array.get(obj, 5));
      				jObj.put("noOfTimesVisited", noOfTimeVisited);
					jObj.put("txtSms", txtSms);
					jArrData.put(jObj);
					
					
      			}
            	jObjData.put("customerTblData", jArrData);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
		finally
		{
			return jObjData;
		}
		
	}
	
	private int funGetNoOfTimesVisited(String customerCode) {
        int noOfTimesCustomerVisited = 0;
        try {
            String sql = "select count(*) from tblbillhd a\n"
                    + "left outer join tblqbillhd b on a.strCustomerCode=b.strCustomerCode\n"
                    + "left outer join tblcustomermaster c on a.strCustomerCode=c.strCustomerCode\n"
                    + "where a.strCustomerCode='" + customerCode + "' ";
            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            List<BigInteger> resultSet = query.list();
            
            if (resultSet!=null) 
            {
            	for(int i=0; i<resultSet.size(); i++)
      			{
            		BigInteger obj = (BigInteger) resultSet.get(i);
            		noOfTimesCustomerVisited = Integer.parseInt(obj.toString());
      			}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noOfTimesCustomerVisited;
    }
    
	public JSONObject funSendBulkSMS(String txtTestMobileNo,String clientCode,String posCode,String txtSms)
	{
		ArrayList<String> mobileNumberList = new ArrayList<String>();
        mobileNumberList.add(txtTestMobileNo);
		
        JSONObject jObjResult=new JSONObject();
		
		
		try
        {
			 jObjResult.put("returnResult", "false");
			 JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gClientTelNo"); 
			
            String fromTelNo =(String)objSetupParameter.get("gClientTelNo");
            String[] sp = fromTelNo.split(",");
            if (sp.length > 0)
            {
                fromTelNo = sp[0];
            }

            JSONObject objSetupParameter1=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gSMSType"); 
            JSONObject objSetupParameter2=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gSMSApi"); 
            String smsApi =(String)objSetupParameter2.get("gSMSApi");
            if (objSetupParameter1.get("gSMSType").toString().equalsIgnoreCase("Cellx"))
            {
                for (int i = 0; i < mobileNumberList.size(); i++)
                {
                    if ((!mobileNumberList.get(i).isEmpty()))
                    {
                    	String smsURL = smsApi.replace("<to>", mobileNumberList.get(i)).replace("<from>",fromTelNo).replace("<MSG>",txtSms).replaceAll("", "%20");
                       
                        System.out.println(objUtilityFunctions.funSendSMS(smsURL));
                        jObjResult.put("returnResult", "true");
                    }
                }

                return jObjResult;
            }
            else if (objSetupParameter1.get("gSMSType").toString().equalsIgnoreCase("Sinfini"))
            {
                for (int i = 0; i < mobileNumberList.size(); i++)
                {
                	if(!mobileNumberList.get(i).isEmpty())
                	{
                    
                        String smsURL = smsApi.replace("<PHONE>",mobileNumberList.get(i)).replace("<MSG>", txtSms).replaceAll(" ", "%20");
                        objUtilityFunctions.funSendSMS(smsURL);
                        jObjResult.put("returnResult", "true");
                    }
                }

                return jObjResult;
            }
            else if (objSetupParameter1.get("gSMSType").toString().equalsIgnoreCase("Infyflyer"))
            {
                for (int i = 0; i < mobileNumberList.size(); i++)
                {
                    if (!mobileNumberList.get(i).isEmpty())
                    {
                        String smsURL = smsApi.replace("<PHONE>",mobileNumberList.get(i)).replace("<MSG>", txtSms).replaceAll(" ", "%20");
                        objUtilityFunctions.funSendSMS(smsURL);
                        jObjResult.put("returnResult", "true");
                    }
                }

                return jObjResult;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
           
            return jObjResult;
        }

        return jObjResult;
	
		
	}
	
	public JSONObject funSendSMS(JSONObject jObj)
	{
		String[] arrItemDtl = null;
		JSONObject jObjResult=new JSONObject();
		
		
		try
		{
			
		 jObjResult.put("returnResult", "false");
			
		String clientCode=jObj.getString("clientCode");	
		String posCode=jObj.getString("posCode");
		JSONArray arrKOTItemDtlList=(JSONArray)jObj.get("jArr");
		for(int j=0; j<arrKOTItemDtlList.length(); j++)
		{
			   
		String itemDtl=arrKOTItemDtlList.getString(j);
		
		 arrItemDtl= itemDtl.split("_");
		
		}
		 JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gClientTelNo"); 
		String fromTelNo =(String)objSetupParameter.get("gClientTelNo");
        String[] sp = fromTelNo.split(",");
        if (sp.length > 0)
        {
            fromTelNo = sp[0];
        }

        JSONObject objSetupParameter1=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gSMSType"); 
        JSONObject objSetupParameter2=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gSMSApi"); 
        String smsApi =(String)objSetupParameter2.get("gSMSApi");
        String mobileNo="",message="";
        for (int i = 0; i < arrItemDtl.length; i++)
        {
             if (null!=arrItemDtl)
             {
                 mobileNo=arrItemDtl[0];
                 message=arrItemDtl[2];
             }
         }
        if (objSetupParameter1.get("gSMSType").toString().equalsIgnoreCase("Cellx"))
        {
        	String smsURL = smsApi.replace("<PHONE>", mobileNo).replace("<MSG>", message).replaceAll(" ", "%20");
            objUtilityFunctions.funSendSMS(smsURL);
            jObjResult.put("returnResult", "true");
        }
        else if (objSetupParameter1.get("gSMSType").toString().equalsIgnoreCase("Sinfini"))
        {
           String smsURL = smsApi.replace("<PHONE>", mobileNo).replace("<MSG>", message).replaceAll(" ", "%20");
           objUtilityFunctions.funSendSMS(smsURL);
           jObjResult.put("returnResult", "true");
        }
        else if (objSetupParameter1.get("gSMSType").toString().equalsIgnoreCase("Infyflyer"))
        {
        	String smsURL = smsApi.replace("<PHONE>", mobileNo).replace("<MSG>", message).replaceAll(" ", "%20");
            objUtilityFunctions.funSendSMS(smsURL);
            jObjResult.put("returnResult", "true");
        }
            return jObjResult;
        
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return jObjResult;
		}
		
	}
	
	
	
	
	
}
