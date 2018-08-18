package com.webservice.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsGroupSubGroupWiseSales;
import com.apos.service.clsSetupService;

@Repository("clsUtilityFunctions")
@Transactional(value = "webPOSTransactionManager")
public class clsUtilityFunctions {
	
	@Autowired
    SessionFactory webPOSSessionFactory;
	
	@Autowired
	private clsSetupService objSetupService;

	
	public String funGetAlphabet(int no)
	{
		String[] alphabets= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		return alphabets[no];
	}
	
	public String funGetTransactionCode(String initCode,String propCode,String startYear)
	{
		String transCode="";
		System.out.println("initCode:"+initCode);
		System.out.println("propCode:"+propCode);
		System.out.println("startYear:"+startYear);
		transCode=propCode;
		Date dt=new Date();
		String years=String.valueOf((dt.getYear()+1900)-Integer.parseInt(startYear));
		System.out.println("years:"+years);
		transCode=propCode+initCode+funGetAlphabet(Integer.parseInt(years))+funGetAlphabet(dt.getMonth());
		System.out.println("transCode:"+transCode);
		return transCode;
	}
	
	public String funGetCurrentDateTime(String pattern)
	{
		String currentDateTime="";
		Date dt=new Date();
		if(pattern.equals("yyyy-MM-dd"))
		{
			currentDateTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
		}
		else
		{
			currentDateTime=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dt);
		}
		return currentDateTime;
	}
	
	
	
	public void funWriteErrorLog(Exception ex)
    {
        System.out.println(ex.getMessage());
        clsGenerateErrorLogs obj = new clsGenerateErrorLogs(ex);
        Thread t = new Thread(obj);
        t.start();
    }
	
	
	 public long funCompareTime(String fromDate, String toDate)
	    {
	        long diff = 0;
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date d1 = null;
	        Date d2 = null;

	        try
	        {
	            d1 = format.parse(fromDate);
	            d2 = format.parse(toDate);

	            diff = d2.getTime() - d1.getTime();
	            long diffSeconds = diff / 1000 % 60;
	            long diffMinutes = diff / (60 * 1000) % 60;
	            long diffHours = diff / (60 * 60 * 1000) % 24;
	            long diffDays = diff / (24 * 60 * 60 * 1000);
	            String time = diffHours + ":" + diffMinutes + ":" + diffSeconds;
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            return diff;
	        }
	    }
	 
	 
	 public JSONObject funCheckName(String masterName, String name, String clientCode,String code) throws Exception
	    {
	    	JSONObject jObjSearchData = new JSONObject();
			String sql = "";
			List list = new ArrayList();
			
			switch(masterName)
			{
			case "POSAreaMaster":
				if(!code.equals("")){
					sql="select count(*) from tblareamaster a where a.strAreaCode !='"+ code+"' and a.strAreaName='" + name + "'and a.strClientCode='" + clientCode + "'";
				}else{
					sql = "select count(LOWER(strAreaName)) from tblareamaster where strAreaName='" + name + "' and strClientCode='" + clientCode + "'";
				}
			    break;
			case "POSMaster":
				if(!code.equals("")){
					sql="select count(*) from tblposmaster a where a.strPosCode !='"+ code+"' and a.strPosName='" + name + "' and a.strClientCode='" + clientCode + "'";
				}else{
			
			    sql = "select count(LOWER(strPosName)) from tblposmaster where strPosName='" + name +  "'";
				}
			break;
			case "POSWaiterMaster":
				if(!code.equals("")){
					sql="select count(*) from tblwaitermaster a where a.strWaiterNo !='"+ code+"' and a.strWShortName='" + name + "' a.and strClientCode='" + clientCode + "'";
				}else{
			    sql = "select count(LOWER(strWShortName)) from tblwaitermaster where strWShortName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSTableMaster":
				if(!code.equals("")){
					sql="select count(*) from tbltablemaster a where a.strTableNo !='"+ code+"' and a.strTableName='" + name + "' and a.strClientCode='" + clientCode + "'";
				}else{
				sql = "select count(LOWER(strTableName)) from tbltablemaster where strTableName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSSettlementMaster":
				if(!code.equals("")){
					sql="select count(*) from tblsettelmenthd a where a.strSettelmentCode !='"+ code+"' and a.strSettelmentDesc='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strSettelmentDesc)) from tblsettelmenthd where strSettelmentDesc='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSAdvanceOrderMaster":
				if(!code.equals("")){
					sql="select count(*) from tbladvanceordertypemaster a where a.strAdvOrderTypeCode !='"+ code+"' and a.strAdvOrderTypeName='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strAdvOrderTypeName)) from tbladvanceordertypemaster where strAdvOrderTypeName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			  case "POSDeliveryBoyMaster":
				if(!code.equals("")){
					sql="select count(*) from tbldeliverypersonmaster a where a.strDPCode !='"+ code+"' and a.strDPName='" + name + "' and a.strClientCode='" + clientCode + "'";		
				}else{
				sql = "select count(LOWER(strDPName)) from tbldeliverypersonmaster where strDPName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSOrderMaster":
				if(!code.equals("")){
					sql="select count(*) from tblordermaster a where a.strOrderCode !='"+ code+"' and a.strOrderDesc='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strOrderDesc)) from tblordermaster where strOrderDesc='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSPromotionMaster":
				if(!code.equals("")){
					sql="select count(*) from tblpromotionmaster a where a.strPromoCode !='"+ code+"' and a.strPromoName='" + name + "' and a.strClientCode='" + clientCode + "'";							
				}else{
				sql = "select count(LOWER(strPromoName)) from tblpromotionmaster where strPromoName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSCounterMaster":
				if(!code.equals("")){
					sql="select count(*) from tblcounterhd a where a.strCounterCode !='"+ code+"' and a.strCounterName='" + name + "' and a.strClientCode='" + clientCode + "'";		
				}else{
				sql = "select count(LOWER(strCounterName)) from tblcounterhd where strCounterName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSDebitCardTypeMaster":
				if(!code.equals("")){
					sql="select count(*) from tbldebitcardtype a where a.strCardTypeCode !='"+ code+"' and a.strCardName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strCardName)) from tbldebitcardtype where strCardName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break; 
			    
			case "POSRegisterDebitCardMaster":
				if(!code.equals("")){
					sql="select count(*) from tbldebitcardmaster a where a.strCardTypeCode !='"+ code+"' and a.strCardString='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql ="select strCardNo from tbldebitcardmaster where strCardString=='" + name + "'";
				}			
			break;
			
			case "POSZoneMaster":
				if(!code.equals("")){
					sql="select count(*) from tblzonemaster a where a.strZoneCode !='"+ code+"' and a.strZoneName='" + name + "' and a.strClientCode='" + clientCode + "'";		
				}else{
				sql = "select count(LOWER(strZoneName)) from tblzonemaster where strZoneName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
			break;
			case "POSCustomerTypeMaster":
				if(!code.equals("")){
					sql="select count(*) from tblcustomertypemaster a where a.strCustTypeCode !='"+ code+"' and a.strCustType='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
					sql = "select count(LOWER(strCustType)) from tblcustomertypemaster where strCustType='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;
			case "POSCustomerMaster":
				if(!code.equals("")){
					sql="select count(*) from tblcustomermaster a where a.strCustomerCode !='"+ code+"' and a.strCustomerName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
			
					sql = "select count(LOWER(strExternalCode)) from tblcustomermaster where strExternalCode='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;
			    
			case "POSMenuHead":
				if(!code.equals("")){
					sql="select count(*) from tblmenuhd a where a.strMenuCode !='"+ code+"' and a.strMenuName='" + name + "' and a.strClientCode='" + clientCode + "'";
				}else{
				sql = "select count(LOWER(strMenuName)) from tblmenuhd where strMenuName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;
			
			case "POSSubMenuHead":
				if(!code.equals("")){
					sql="select count(*) from tblsubmenuhead a where a.strSubMenuHeadCode !='"+ code+"' and a.strSubMenuHeadName='" + name + "' and a.strClientCode='" + clientCode + "'";	
				}else{
				sql = "select count(LOWER(strSubMenuHeadName)) from tblsubmenuhead where strSubMenuHeadName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
			    break;
			    
			case "POSModifier":
				if(!code.equals("")){
					sql="select count(*) from tblmodifiermaster a where a.strModifierCode !='"+ code+"' and a.strModifierName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strModifierName)) from tblmodifiermaster where strModifierName='" + URLDecoder.decode("-->"+name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
			    break;
			    
			case "POSMenuItem":
				if(!code.equals("")){
					sql="select count(*) from tblitemmaster a where a.strItemCode !='"+ code+"' and a.strItemName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strItemName)) from tblitemmaster where strItemName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}break;
			    
			case "POSModGroup":
				if(!code.equals("")){
					sql="select count(*) from tblmodifiergrouphd a where a.strModifierGroupCode !='"+ code+"' and a.strModifierGroupName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strModifierGroupName)) from tblmodifiergrouphd where strModifierGroupName='" + URLDecoder.decode(name, "UTF-8") +"' and strClientCode='" + clientCode + "'";
				}
				break;    
			    
			case "POSFactoryMaster":
				if(!code.equals("")){
					sql="select count(*) from tblfactorymaster a where a.strFactoryCode !='"+ code+"' and a.strFactoryName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strFactoryName)) from tblfactorymaster where strFactoryName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break; 
			    
			case "	":
				if(!code.equals("")){
				sql="select count(*) from tblcostcentermaster a where a.strCostCenterCode !='"+ code+"' and a.strCostCenterName='" + name + "' and a.strClientCode='" + clientCode + "'";					
				}else{
				sql = "select count(LOWER(strCostCenterName)) from tblcostcentermaster where strCostCenterName='" + name + "' and strClientCode='" + clientCode + "'";
				}
				break;  
			    
			case "POSGroupMaster":
				if(!code.equals("")){
					sql="select count(*) from tblgrouphd a where a.strGroupCode !='"+ code+"' and a.strGroupName='" + name + "' and a.strClientCode='" + clientCode + "'";					
					}else{
					sql = "select count(LOWER(strGroupName)) from tblgrouphd where strGroupName='" + name + "' and strClientCode='" + clientCode + "'";
					}
				break;
			    
		
    
			    		    
			}
			 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			 list = query.list();
			 
			 
			 jObjSearchData.put("count", list.get(0));
			return jObjSearchData;
	    
	    }
	 
	 
	 public String funGetDate(String pattern,String date)
		{
			String retDate=date;
			
			if(pattern.equals("yyyy-MM-dd"))  // From JSP to Database yyyy-MM-dd
			{
				String[] spDate=date.split("-");
				retDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
			}
			else if(pattern.equals("yyyy/MM/dd")) // From Database to JSP
			{
				String[] sp=date.split(" ");
				String[] spDate=sp[0].split("-");
				retDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
			}else if(pattern.equals("dd-MM-yyyy")) // For Jasper Report show
			{
				String[] sp=date.split(" ");
				String[] spDate=sp[0].split("-");
				retDate=spDate[2]+"-"+spDate[1]+"-"+spDate[0];
			}
			return retDate;
		}
	 
	 
	  public String funUpdateTableStatusToInrestoApp(String tableNo, String tableName, String tableSts,String clientCode,String posCode) throws Exception
	    {
	        String retValue = "";
	        JSONObject objJson = new JSONObject();
	        int tableStatus = 1;//unavailable

	        if (!tableNo.isEmpty())
	        {
	            if (tableSts.equalsIgnoreCase("Normal"))
	            {
	                tableStatus = 2;//free()Normal
	            }
	            else if (tableSts.equalsIgnoreCase("Occupied"))
	            {
	                tableStatus = 3;//occupied
	            }
	            else if (tableSts.equalsIgnoreCase("Billed"))
	            {
	                tableStatus = 4;//billing
	            }
	            JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gInrestoPOSId"); 
	      	   
	            objJson.put("tablename", tableName);
	            objJson.put("status", tableStatus);
	            objJson.put("restID", objSetupParameter.get("gInrestoPOSId"));

	             objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gInrestoPOSWebServiceURL"); 
	      	  
	      	    	
	            String hoURL = objSetupParameter.get("gInrestoPOSWebServiceURL") + "/updatetablefrompos";

	            URL url = new URL(hoURL);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json");
	            OutputStream os = conn.getOutputStream();
	            os.write(objJson.toString().getBytes());
	            os.flush();
	            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
	            {
	                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	            }
	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	            String output = "";

	            while ((output = br.readLine()) != null)
	            {
	                retValue += output;
	            }
	            conn.disconnect();
	        }

	        System.out.println("Table Status= " + tableSts + "\tRet Value= " + retValue);

	        return retValue;
	    }











	public void funSendSMS(String billno, String smsData, String transType,String clientCode,String posCode)
	    {
	        try
	        {
	            //String smsData=clsGlobalVarClass.gBillSettlementSMS;
	            String result = "", result1 = "", result2 = "", result3 = "", result4 = "", result5 = "", result6 = "", result7 = "";
	            String mainSms = "", sql = "";

	            if (transType.equalsIgnoreCase("Home Delivery"))
	            {
	                sql = "select c.strCustomerName,c.longMobileNo,a.dblGrandTotal "
	                        + " ,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y'),time(a.dteBillDate) "
	                        + " ,a.strUserCreated,ifnull(d.strDPName,'') "
	                        + " from tblbillhd a,tblcustomermaster c ,tblhomedelivery b "
	                        + " left outer join tbldeliverypersonmaster d on b.strDPCode=d.strDPCode "
	                        + " where a.strBillNo='" + billno + "' and a.strBillNo=b.strBillNo "
	                        + " and a.strCustomerCode=c.strCustomerCode ";
	            }
	            else
	            {
	                sql = "select ifnull(c.strCustomerName,''),ifnull(c.longMobileNo,'NA')"
	                        + " ,a.dblGrandTotal ,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y')"
	                        + " ,time(a.dteBillDate),a.strUserCreated,ifnull(d.strDPName,'') "
	                        + " from tblbillhd a left outer join tblhomedelivery b on a.strBillNo=b.strBillNo "
	                        + " left outer join tbldeliverypersonmaster d on b.strDPCode=d.strDPCode "
	                        + " left outer join tblcustomermaster c on a.strCustomerCode=c.strCustomerCode "
	                        + " where a.strBillNo='" + billno + "'";
	            }
	            //System.out.println(sql);
	            Query rs_SqlGetSMSData = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	           List list = rs_SqlGetSMSData.list();
	            
	           if (list!=null)
	   		{
	   			for(int i=0; i<list.size(); i++)
	   			{
	   				Object[] obj = (Object[]) list.get(i);
	   				String customerName = obj[0].toString();
	   				String sms = obj[1].toString();
	   				String billAmt=obj[2].toString();
	   				String date=obj[3].toString();
	   				String deliveryBoy=obj[6].toString();
	   				String user=obj[5].toString();
	   				String time=obj[4].toString();
	   				
	   				String strBillPrintOnSettlement = obj[2].toString();
	                int intIndex = smsData.indexOf("%%BILL NO");
	                if (intIndex != - 1)
	                {
	                    result = smsData.replaceAll("%%BILL NO", billno);
	                    mainSms = result;
	                }
	                int intIndex1 = mainSms.indexOf("%%CUSTOMER NAME");

	                if (intIndex1 != - 1)
	                {
	                    result1 = mainSms.replaceAll("%%CUSTOMER NAME", customerName);
	                    mainSms = result1;
	                }
	                int intIndex2 = mainSms.indexOf("%%BILL AMT");

	                if (intIndex2 != - 1)
	                {
	                    result2 = mainSms.replaceAll("%%BILL AMT", billAmt);
	                    mainSms = result2;
	                }
	                int intIndex3 = mainSms.indexOf("%%DATE");

	                if (intIndex3 != - 1)
	                {
	                    result3 = mainSms.replaceAll("%%DATE", date);
	                    mainSms = result3;
	                }
	                int intIndex4 = mainSms.indexOf("%%DELIVERY BOY");

	                if (intIndex4 != - 1)
	                {
	                    result4 = mainSms.replaceAll("%%DELIVERY BOY", deliveryBoy);
	                    mainSms = result4;
	                }
	                int intIndex5 = mainSms.indexOf("%%ITEMS");

	                if (intIndex5 != - 1)
	                {
	                    StringBuilder sbItems = new StringBuilder();
	                    sbItems.append("");
	                    if (clientCode.equals("117.001"))//prems
	                    {
	                        sql = "select a.strItemName from tblbilldtl a where a.strBillNo='" + billno + "' ";
	                        Query rsItems = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                         list = rsItems.list();
	                         if (list!=null)
	             	   		{
	             	   			for(i=0; i<list.size(); i++)
	             	   			{
	             	   				 obj = (Object[]) list.get(i);
	             	   				String item = obj[0].toString();
	                            sbItems.append(item);
	                            sbItems.append(",");
	                        }
	                        
	                        sbItems.deleteCharAt(sbItems.lastIndexOf(","));
	                    }

	                    result5 = mainSms.replaceAll("%%ITEMS", sbItems.toString());
	                    mainSms = result5;
	                }
	                int intIndex6 = mainSms.indexOf("%%USER");

	                if (intIndex6 != - 1)
	                {
	                    result6 = mainSms.replaceAll("%%USER", user);
	                    mainSms = result6;
	                }
	                int intIndex7 = mainSms.indexOf("%%TIME");

	                if (intIndex7 != - 1)
	                {
	                    result7 = mainSms.replaceAll("%%TIME", time);
	                    mainSms = result7;
	                }
	                JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gClientTelNo"); 
	          	    
	                
	                String fromTelNo = (String) objSetupParameter.get("gClientTelNo");
	                String[] sp = fromTelNo.split(",");
	                if (sp.length > 0)
	                {
	                    fromTelNo = sp[0];
	                }
	                 objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gSMSType");
	                
	                if (objSetupParameter.get("gSMSType").toString().equalsIgnoreCase("Cellx"))
	                {
	                    if (!sms.isEmpty())
	                    {
	                    	objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gSMSApi");
	                   
	                        //System.out.println(clsGlobalVarClass.gSMSApi);
	                        //System.out.println(mainSms);
	                        String smsURL = ((String) objSetupParameter.get("gSMSApi")).replace("<to>",sms).replace("<from>", fromTelNo).replace("<MSG>", mainSms).replaceAll(" ", "%20");
	                        //System.out.println(smsURL);
	                        //System.out.println(clsGlobalVarClass.funSendSMS(smsURL));
	                    }
	                }
	                else if (objSetupParameter.get("gSMSType").toString().equalsIgnoreCase("Sinfini"))
	                {
	                    String smsURL = ((String) objSetupParameter.get("gSMSApi")).replace("<PHONE>", sms).replace("<MSG>", mainSms).replaceAll(" ", "%20");
	                    funSendSMS(smsURL);
	                }
	                else if (objSetupParameter.get("gSMSType").toString().equalsIgnoreCase("Infyflyer"))
	                {
	                    //http://sms.infiflyer.co.in/httpapi/httpapi?token=a10bad827db08a4eeec726da63813747&sender=IPREMS&number=<PHONE>&route=2&type=1&sms=<MSG>
	                    String smsURL = ((String) objSetupParameter.get("gSMSApi")).replace("<PHONE>", sms).replace("<MSG>", mainSms).replaceAll(" ", "%20");
	                    funSendSMS(smsURL);
	                }
	            }
	        }
	   	 }
	        }
	        catch (Exception e)
	        {            
	            e.printStackTrace();
	        }
	   
	    }
	
	public static String funSendSMS(String url)
    {
        StringBuilder output = new StringBuilder();
        try
        {
            URL hp = new URL(url);
            //System.out.println(url);
            URLConnection hpCon = hp.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(hpCon.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                output.append(inputLine);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output.toString();
    }      
	
	

	 public String funGetCurrentTime()
	    {
	        String currentTime = "";
	        Date dt = new Date();
	        int hours = dt.getHours();
	        int minutes = dt.getMinutes();
	        if (hours > 12)
	        {
	            //hours=hours-12;
	            currentTime = hours + ":" + minutes + " PM";
	        }
	        else
	        {
	            currentTime = hours + ":" + minutes + " AM";
	        }
	        return currentTime;
	    }

	    public String funGetCurrentDate()
	    {
	        Calendar objDate = new GregorianCalendar();
	        String currentDate = (objDate.getTime().getYear() + 1900) + "-" + (objDate.getTime().getMonth() + 1) + "-" + objDate.getTime().getDate();
	        return currentDate;
	    }
	
	    public String funGetDayForPricing()
	    {
	        String day = "";
	       
	        String dayNames[] = new DateFormatSymbols().getWeekdays();
	        Calendar date2 = Calendar.getInstance();
	        String tempday = dayNames[date2.get(Calendar.DAY_OF_WEEK)];
	        switch (tempday)
	        {
	            case "Sunday":
	                day = "strPriceSunday";
	                break;

	            case "Monday":
	                day = "strPriceMonday";
	                break;

	            case "Tuesday":
	                day = "strPriceTuesday";
	                break;

	            case "Wednesday":
	                day = "strPriceWednesday";
	                break;

	            case "Thursday":
	                day = "strPriceThursday";
	                break;

	            case "Friday":
	                day = "strPriceFriday";
	                break;

	            case "Saturday":
	                day = "strPriceSaturday";
	                break;

	            default:
	                day = "strPriceSunday";
	        }
	        return day;
	    }
	    
	    
	    public List funGetDocumentCode(String masterName) throws Exception
	    {
			String sql = "";
			List list = new ArrayList();
			
			switch(masterName)
			{
				case "POSMenuHeadMaster":
				    sql = "select ifnull(max(strMenuCode),0) from tblmenuhd";
				    break;
				    
				case "POSItemModifierMaster":
				    sql = "select ifnull(max(strModifierCode),0) from tblmodifiermaster";
				    break;    
				    
				case "POSMenuItemMaster" :
				   sql = "select ifnull(max(strItemCode),0) from tblitemmaster";
				 break;  
				 
				case "POSModifierGroupMaster" :
					   sql = "select ifnull(max(strModifierGroupCode),0) from tblmodifiergrouphd";
				break;
				
				case "POSZoneMaster":
				    sql = "select ifnull(max(strZoneCode),0) from tblzonemaster";
				    break;
				    
				case "POSShiftMaster":
				    sql = "select ifnull(max(intShiftCode),0) from tblshiftmaster";
				    break;
				    
				case "POSCustomerMaster":
				    sql = "select max(right(strCustomerCode,8)) from tblcustomermaster";
				    break;
				    
				case "POSCustomerTypeMaster":
				    sql = "select ifnull(max(strCustTypeCode),0) from tblcustomertypemaster";
				    break;
				    
				case "POSReasoneMaster":
				    sql = "select max(strReasonCode) from tblreasonmaster";
				    break;
				    
				case "POSCounterMaster":
					sql = "select ifnull(max(strCounterCode),0) from tblcounterhd";
					break;   
					
				case "POSDebitCardMaster":
					sql = "select ifnull(max(strCardTypeCode),0) from tbldebitcardtype";
					break;    
					
				case "POSFactoryMaster":
				    sql = "select ifnull(max(a.strFactoryCode),0) strMaxFactoryCode from tblfactorymaster a";
				    break;   
				    
				case "POSCostCenterMaster":
				    sql = "select max(strCostCenterCode) from tblcostcentermaster";
				    break;
				    
				case "POSAreaMaster":
				    sql = "select ifnull(max(strAreaCode),0) from tblareamaster";
				    break;
				    	
				case "POSWaiterMaster":
				    sql = "select ifnull(max(strWaiterNo),0) from tblwaitermaster";
				    break;
			
				    
				case "POSDeliveryBoyMaster":
					sql = "select ifnull(max(strDPCode),0) from tbldeliverypersonmaster";
					break;
					
				case "POSMaster":
					sql = "select ifnull(max(strPosCode),0) from tblposmaster";
					break;
					
				case "POSTaxMaster":
					sql = "select ifnull(max(strTaxCode),0) from tbltaxhd";
					break;

				case "POSPromotionMaster":
					sql = "select ifnull(max(strPromoCode),0) from tblpromotionmaster";
					break;
					
				case "POSTableMaster":
					sql = "select ifnull(max(strTableNo),0),MAX(intSequence) from tbltablemaster";
					break;
					
				case "POSSettlementMaster":
					sql = "select ifnull(max(strSettelmentCode),0) from tblsettelmenthd";
					break;
					
				case "POSAdvOrderTypeMaster"://tblInternal
					sql = "select ifnull(max(strAdvOrderTypeCode),0) from tbladvanceordertypemaster";
					break;
					
				case "POSRecipeMaster"://tblInternal
					sql = "select ifnull(max(strRecipeCode),0) from tblrecipehd";
					break;
					
				case "POSOrderMaster"://tblInternal
					sql = "select ifnull(max(strOrderCode),0) from tblordermaster";
					break;    
				
				case "POSSubGroupMaster" :
					sql="select max(strSubGroupCode) from tblsubgrouphd";
					break;	
					
				case "POSSubMenuHead":
					sql = "select ifnull(max(strSubMenuHeadCode),0) from tblsubmenuhead";
					break;	
					
				case "POSGroupMaster" :
					sql = "select ifnull(max(strGroupCode),0) from tblgrouphd";
					break;	
					
				case "POSCustAreaMaster":
					sql = "select ifnull(max(strBuildingCode),0) from tblbuildingmaster";
				break;
			    
				case "POSTDHMaster":
					sql = "select ifnull(max(strTDHCode),0) from tbltdhhd";
					break;
    
					   
					   
			}
			
			 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			 list = query.list();
			 
			 return list;
	    }  
	    
	    
	    
	    public long funGetDocumentCodeFromInternal(String masterName) throws Exception
		{
			long code=0;
			String sql = "select dblLastNo from tblinternal where strTransactionType='"+masterName+"'";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			List list = query.list();
			if(list.size()==0)
			{
				sql = "insert into tblinternal values('"+masterName+"'," + 1 + ")";
				Query query1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				query1.executeUpdate();
				code=1;
			}
			else
			{
				code=Long.parseLong(list.get(0).toString());
				code=code+1;
				sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='"+masterName+"'";
				Query query2 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				query2.executeUpdate();
			}
			
			return code;
		}
	    
	


public long funCompareDate(String fromDate, String toDate)
	    {
	        long diff = 0;
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        Date d1 = null;
	        Date d2 = null;
	        try
	        {
	            d1 = format.parse(fromDate);
	            d2 = format.parse(toDate);
	            diff = d2.getTime() - d1.getTime();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return diff;
	    }

 private int funPrintTakeAwayForJasper(String billHdTableName, String billNo) throws Exception
	    {
	        int res = 0;
	        String sqlTakeAway = "select strOperationType from " + billHdTableName + " "
	                + " where strBillNo='" + billNo + "'";
	       Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTakeAway);
	       List rsBill = query.list();
	       String operationType="";
	        if(rsBill!=null)
	        {
	        	for(int i=0; i<rsBill.size(); i++)
				{
//	        		Object[] obj = (Object[]) rsBill.get(i);
//			
//			
//	        		operationType = (String) Array.get(obj,0);
	        		operationType = (String) rsBill.get(0);
	        		if(operationType.equals("TakeAway"))
	        		{
	        			res = 1;
	        		}
	        	}
	        	
	        }
	      
	        return res;
	    }
	  
	  private boolean funIsDirectBillerBill(String billNo, String billhd)
	    {
	        boolean flgIsDirectBillerBill = false;
	        try
	        {
	            String sql_checkDirectBillerBill = "select strTableNo,strOperationType "
	                    + " from " + billhd + " where strBillNo='"+billNo+"'  ";
	        
	            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_checkDirectBillerBill);
	          List listIsDirectBillBill=query.list();
	          String tableNo="",operationType="";
	          if(listIsDirectBillBill!=null)
	          {
	        	  for(int i=0; i<listIsDirectBillBill.size(); i++)
					{
		        		Object[] obj = (Object[]) listIsDirectBillBill.get(i);
				
		        		tableNo = (String) Array.get(obj,0);
		        		
		        	}
		        	
	                if (tableNo != null && tableNo.trim().isEmpty())
	                {
	                    flgIsDirectBillerBill = true;
	                }
	            }
	           
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return flgIsDirectBillerBill;
	    }
	  
	  private int funPrintPromoItemsInBill(String billNo, int billPrintSize, JSONArray listOfBillDetail) throws Exception
	    {
		  
		  JSONObject jObjBillDtl=new JSONObject();
	        String sqlBillPromoDtl = "select b.strItemName,a.dblQuantity,'0',dblRate "
	                + " from tblbillpromotiondtl a,tblitemmaster b "
	                + " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ";
            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillPromoDtl);
            List listBillPromoItemDtl =query.list();
	       String itemName="";
	       double qty=0,amt=0;
	        clsBillDtl objBillDtl = null;
	        if(listBillPromoItemDtl!=null)
	          {
	        	for(int i=0; i<listBillPromoItemDtl.size(); i++)
				{
	        		Object[] obj = (Object[]) listBillPromoItemDtl.get(i);
			
	        		itemName = (String) Array.get(obj,0);
	        		qty = (double) Array.get(obj,1);
	        		amt = (double) Array.get(obj,2);
	        		jObjBillDtl.put("qty", qty);
		        	jObjBillDtl.put("amt", amt);
		        	jObjBillDtl.put("itemName", itemName.toUpperCase());
		            listOfBillDetail.put(jObjBillDtl);
	        	}
	        	
	        }
	        return 1;
	    }
	  
	  private int funPrintPromoItemsInBillFormat2(String billNo, int billPrintSize,  JSONArray listOfBillDetail) throws Exception
	    {
		  
		 
	        String sqlBillPromoDtl = "select b.strItemName,a.dblQuantity,'0',dblRate "
	                + " from tblbillpromotiondtl a,tblitemmaster b "
	                + " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ";
          Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillPromoDtl);
          List listBillPromoItemDtl =query.list();
          clsBillDtl objBillDtl = null;
	       String itemName="";
	       double qty=0,amt=0;
	       
	        if(listBillPromoItemDtl!=null)
	          {
	        	for(int i=0; i<listBillPromoItemDtl.size(); i++)
				{
	        		Object[] obj = (Object[]) listBillPromoItemDtl.get(i);
			
	        		itemName = (String) Array.get(obj,0);
	        		qty = (double) Array.get(obj,1);
	        		amt = (double) Array.get(obj,2);
	        		objBillDtl = new clsBillDtl();
		            objBillDtl.setDblQuantity(qty);
		            objBillDtl.setDblAmount(amt);
		            objBillDtl.setStrItemName(itemName);
		            listOfBillDetail.put(objBillDtl);
	        	}
	        	
	        }
	        
	        return 1;
	    }
	  public JSONArray funPrintServiceVatNoForJasper(String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo) throws IOException
	    {
	        JSONArray listOfServiceVatDetail = new JSONArray();
	        clsBillDtl objBillDtl = null;
	        try
	        {
	        JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gUseVatAndServiceTaxFromPos");   
			String useVatAndServiceTaxFromPos=(String)objSetupParameter.get("gUseVatAndServiceTaxFromPos");
            

			
	        if (useVatAndServiceTaxFromPos.equals("Y"))
	        {
	        	JSONObject jObj=new JSONObject();

	            if (PrintVatNoPOS.equals("Y"))
	            {
	                objBillDtl = new clsBillDtl();
	                jObj.put("Vat No. :" , vatNo);
	                listOfServiceVatDetail.put(jObj);
	            }
	            if (printServiceTaxNo.equals("Y"))
	            {
	               jObj.put("Service Tax No.:" ,serviceTaxNo);
	                listOfServiceVatDetail.put(jObj);
	            }
	        }
	        else
	        {
	        	JSONObject jObj=new JSONObject();
	            if (PrintVatNoPOS != null)
	            {
	                jObj.put("Vat No. :",vatNo);
	                listOfServiceVatDetail.put(jObj);
	            }
	            if (serviceTaxNo.equalsIgnoreCase(""))
	            {
	               jObj.put("Service Tax No.:",serviceTaxNo);
	                listOfServiceVatDetail.put(jObj);
	            }
	        }
	      }
	       catch(Exception e)
	        {
	    	   e.printStackTrace();
	        }
	        
	        return listOfServiceVatDetail;
	    }
	  public String funPrintTextWithAlignment(String text, int totalLength, String alignment)
	    {
	        StringBuilder sbText = new StringBuilder();
	        if (alignment.equalsIgnoreCase("Center"))
	        {
	            int textLength = text.length();
	            int totalSpace = (totalLength - textLength) / 2;

	            for (int i = 0; i < totalSpace; i++)
	            {
	                sbText.append(" ");
	            }
	            sbText.append(text);
	        }
	        else if (alignment.equalsIgnoreCase("Left"))
	        {
	            sbText.setLength(0);
	            int textLength = text.length();
	            int totalSpace = (totalLength - textLength);
	            sbText.append(text);
	            for (int i = 0; i < totalSpace; i++)
	            {
	                sbText.append(" ");
	            }
	        }
	        else
	        {
	            sbText.setLength(0);
	            int textLength = text.length();
	            int totalSpace = (totalLength - textLength);
	            for (int i = 0; i < totalSpace; i++)
	            {
	                sbText.append(" ");
	            }
	            sbText.append(text);
	        }

	        return sbText.toString();
	    }

 
	    
		/*
		this is a customise function to calculate roundoff amount to X amount
		
		hash map returns roundoff amount and roundoff by amount
		*/
		public Map funCalculateRoundOffAmount(double settlementAmt,double gRoundOffTo)
		{
		   Map<String, Double> hm = new HashMap<>();
		
		   double roundOffTo = gRoundOffTo;
		   double roundOffSettleAmt = settlementAmt;
		   double remainderAmt = (settlementAmt % roundOffTo);
		   double roundOffToBy2 = roundOffTo / 2;
		   double x = 0.00;
		
		   if (remainderAmt <= roundOffToBy2)
		   {
		       x = (-1) * remainderAmt;
		
		       roundOffSettleAmt = (Math.floor(settlementAmt / roundOffTo) * roundOffTo);
		
		       //System.out.println(settleAmt + " " + roundOffSettleAmt + " " + x);
		   }
		   else
		   {
		        x=roundOffTo-remainderAmt;
		
		       roundOffSettleAmt = (Math.ceil(settlementAmt / roundOffTo) * roundOffTo);
		
		       // System.out.println(settleAmt + " " + roundOffSettleAmt + " " + x);
		   }
		
		   hm.put("roundOffAmt", roundOffSettleAmt);
		   hm.put("roundOffByAmt", x);
		
		   return hm;
		
		}
		
		public long funGetDebitCardNo(String masterName) throws Exception
		{
			long code=0;
			
			String sql = "select count(dblLastNo) from tblinternal where strTransactionType='CardNo'";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			List list = query.list();
			int cntDelBoyCategory=0;
			
			if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						
						BigInteger cnt =(BigInteger) list.get(i);
						cntDelBoyCategory = cnt.intValue();
					}
				}
			 if (cntDelBoyCategory > 0)
		     {
		         sql = "select dblLastNo from tblinternal where strTransactionType='CardNo'";
		         Query query1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		     	 list = query1.list(); 
		     	if (list!=null)
				{

					for(int i=0; i<list.size(); i++)
					{
						BigInteger lastNo = (BigInteger) list.get(i);
						code = lastNo.intValue();
						code = code + 1;
						
					}
				}
		     	}
		     else
		     {
		    	 code = 1;
		     }
			
			 String updateSql = "update tblinternal set dblLastNo=" + code + " "
		             + "where strTransactionType='CardNo'";
			 Query query2 = webPOSSessionFactory.getCurrentSession().createSQLQuery(updateSql);
				query2.executeUpdate();

		   	return code;
		}
		
		
		
		public JSONObject funPrintBill(String voucherNo, String billDate, boolean flgReprint, String POSCode,String clientCode,String posName,String webStockUserCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
	    {
		 JSONObject jObjResult = new JSONObject();
	        try
	        {
	            String reprintYN = "";
	            if (flgReprint)
	            {
	                reprintYN = "Reprint";
	            }
	            
	            JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gBillFormatType");
 			  
						
	            if (objSetupParameter.get("gBillFormatType").toString().equalsIgnoreCase("Jasper 1"))
	            {
	            	jObjResult=funGenerateBillForJasperFormat1(voucherNo, reprintYN, "", "sale", billDate,clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
	            }
	            else if (objSetupParameter.get("gBillFormatType").toString().equalsIgnoreCase("Jasper 2"))
	            {
	            	jObjResult=funGenerateBillForJasperFormat2(voucherNo, reprintYN, "", "sale", billDate,clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
	            }
	            else if (objSetupParameter.get("gBillFormatType").toString().equalsIgnoreCase("Jasper 3"))
	            {
	            	jObjResult = funGenerateBillForJasperFormat3(voucherNo, reprintYN, "", "sale", billDate,clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
	            }

	        }
	        catch (Exception e)
	        {
	           
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObjResult;
	        }
	    }

public JSONObject funGenerateBillForJasperFormat1(String billNo, String reprint, String formName, String transType, String billDate,String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
	    {
	        HashMap hm = new HashMap();

	        //clsUtility objUtility = new clsUtility();
	        
	        JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gHOPOSType");
	        JSONObject jObjRet=new JSONObject();
	        
	        JSONArray jArr =new JSONArray();
	        JSONArray jArr1 =new JSONArray();
	        
			
           
	        String Linefor5 = "  --------------------------------------";
	        try
	        {
	        	JSONObject jObj1 = new JSONObject();
	        	JSONObject jObj=new JSONObject();
	            String user = "";
	            String billhd;
	            String billdtl;
	            String billModifierdtl;
	            String billSettlementdtl;
	            String billtaxdtl;
	            String billDscFrom = "tblbilldiscdtl";
	            String billPromoDtl = "tblbillpromotiondtl";
	            String billType = " ";

	            if (objSetupParameter.get("gHOPOSType").toString().equalsIgnoreCase("HOPOS"))
	            {
	                billhd = "tblqbillhd";
	                billdtl = "tblqbilldtl";
	                billModifierdtl = "tblqbillmodifierdtl";
	                billSettlementdtl = "tblqbillsettlementdtl";
	                billtaxdtl = "tblqbilltaxdtl";
	                billDscFrom = "tblqbilldiscdtl";
	                billPromoDtl = "tblqbillpromotiondtl";
	            }
	            else
	            {
	                if ("sales report".equalsIgnoreCase(formName))
	                {
	                    billhd = "tblbillhd";
	                    billdtl = "tblbilldtl";
	                    billModifierdtl = "tblbillmodifierdtl";
	                    billSettlementdtl = "tblbillsettlementdtl";
	                    billtaxdtl = "tblbilltaxdtl";
	                    billDscFrom = "tblbilldiscdtl";
	                    billPromoDtl = "tblbillpromotiondtl";

	                    long dateDiff =funCompareDate(billDate,billDate);
	                    if (dateDiff > 0)
	                    {
	                        billhd = "tblqbillhd";
	                        billdtl = "tblqbilldtl";
	                        billModifierdtl = "tblqbillmodifierdtl";
	                        billSettlementdtl = "tblqbillsettlementdtl";
	                        billtaxdtl = "tblqbilltaxdtl";
	                        billDscFrom = "tblqbilldiscdtl";
	                        billPromoDtl = "tblqbillpromotiondtl";
	                    }

	                    String sql = "select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' ";
	                    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	        			
	        			List listBillTable = query.list();
	        			int billCnt;
	        			String billCount="";
	        			if (listBillTable!=null)
	     				{
	     					for(int i=0; i<listBillTable.size(); i++)
	     					{
	     						
	     						billCount =(String) listBillTable.get(i);
	                     
	     					}
	     				}
	                     billCnt = Integer.parseInt(billCount);
	                    if (billCnt == 0)
	                    {
	                        billhd = "tblqbillhd";
	                        billdtl = "tblqbilldtl";
	                        billModifierdtl = "tblqbillmodifierdtl";
	                        billSettlementdtl = "tblqbillsettlementdtl";
	                        billtaxdtl = "tblqbilltaxdtl";
	                        billDscFrom = "tblqbilldiscdtl";
	                        billPromoDtl = "tblqbillpromotiondtl";
	                    }
	                }
	                else
	                {
	                    billhd = "tblbillhd";
	                    billdtl = "tblbilldtl";
	                    billModifierdtl = "tblbillmodifierdtl";
	                    billSettlementdtl = "tblbillsettlementdtl";
	                    billtaxdtl = "tblbilltaxdtl";
	                    billPromoDtl = "tblbillpromotiondtl";
	                }
	            }
	           
	            BigDecimal subTotal = null;
	            BigDecimal grandTot = null;
	            BigDecimal advAmount = null;
	            BigDecimal deliveryCharge = null;
	            String customerCode = "";
	            double grandTotal=0.00;
	            boolean flag_DirectBiller = false;

	           // objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientCode");
	            if (clientCode.equalsIgnoreCase("117.001"))
	            {
	                if (POSCode.equals("P01"))
	                {
	                	jObj1.put("posWiseHeading", "THE PREM'S HOTEL");
	                }
	                else if (POSCode.equals("P02"))
	                {
	                	jObj1.put("posWiseHeading", "SWIG");
	                }
	            }

	            boolean isReprint = false;
	            if ("reprint".equalsIgnoreCase(reprint))
	            {
	                isReprint = true;
	                jObj1.put("duplicate", "[DUPLICATE]");
	            }
	            if (transType.equals("Void"))
	            {
	            	jObj1.put("voidedBill", "VOIDED BILL");
	            }

	            boolean flag_isHomeDelvBill = false;
	            String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
	                    + "from tblhomedelivery where strBillNo='" + billNo + "'";
	    
	            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_HomeDelivery);
	            List rs_HomeDelivery =query.list();
	            String billNumber="",customerName="",customerAddress = null;
	             if (null!=rs_HomeDelivery)
	             {
	             	JSONObject jObj2=new JSONObject();
	             	for(int i=0; i<rs_HomeDelivery.size(); i++)
	    			{
	           		Object[] obj = (Object[]) rs_HomeDelivery.get(i);
	    		
	    		
	           		billNumber = (String) Array.get(obj,0);
	           		customerCode=(String)Array.get(obj,1);
	           		customerAddress=(String)Array.get(obj,4);
	        	   
	    			}
	             }
//	            List<clsBillDtl> listOfHomeDeliveryDtl = new ArrayList<>();
	             JSONArray listOfHomeDeliveryDtl =new JSONArray();
	            clsBillDtl objBillDtl = new clsBillDtl();
	            if (rs_HomeDelivery.size()!=0)
                {
	            if (null!=rs_HomeDelivery)
	            {
	                flag_isHomeDelvBill = true;
	               
	                billType = "HOME DELIVERY";

	                String SQL_CustomerDtl = "";

	                if (customerAddress.equals("Home"))
	                {
	                    SQL_CustomerDtl = "select a.strCustomerName,a.strCustAddress,a.strStreetName"
	                            + " ,a.strLandmark,a.strArea,a.strCity,a.intPinCode,a.longMobileNo "
	                            + " from tblcustomermaster a left outer join tblbuildingmaster b "
	                            + " on a.strBuldingCode=b.strBuildingCode "
	                            + " where a.strCustomerCode='"+customerCode+"' ";
	                }
	                else
	                {
	                    SQL_CustomerDtl = "select a.strCustomerName,a.strCustAddress,a.strOfficeStreetName"
	                            + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
	                            + " from tblcustomermaster a "
	                            + " where a.strCustomerCode='"+customerCode+"' ";
	                }
	              
	                 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_CustomerDtl);
	               
	                List rs_CustomerDtl =query.list();
		           String custName="",custAddress="",officeStreetName="",officeLandmark="",officeArea="",officeCity="",officePinCode="",mobileNo="";
		             if (null!=rs_CustomerDtl)
		             {
		            	 JSONObject jObjList=new JSONObject();
		             	for(int i=0; i<rs_CustomerDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) rs_CustomerDtl.get(i);
		    		
		    		
		           		custName = (String) Array.get(obj,0);
		           		custAddress=(String)Array.get(obj,1);
		           		officeStreetName=(String)Array.get(obj, 2);
		           		officeLandmark=(String)Array.get(obj, 3);
		           		officeArea=(String)Array.get(obj, 4);
		           		officeCity=(String)Array.get(obj, 5);
		           		officePinCode=(String)Array.get(obj, 6);
		           		mobileNo=(String)Array.get(obj, 7);
		    			}
		             	jObj1.put("NAME",custName);
		             	
		             	jObjList.put("NAME", custName.toUpperCase());
	                    //listOfHomeDeliveryDtl.put(jObjList);
		             	
		             	
	                    // Building Name 
	                    String fulAddress = "";
	                    String fulAddress1 = "";
	                    String fullAddress = "";
	                    String add = custAddress;
	                    int strlen = add.length();
	                    String add1 = "";
	                    if (strlen < 28)
	                    {
	                        add1 = add.substring(0, strlen);
	                        if (!add1.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + add1;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + add1;
	                            }
	                        }
	                        fulAddress += "ADDRESS    :" + add1.toUpperCase();
	                    }
	                    else
	                    {
	                        add1 = add.substring(0, 28);
	                        if (!add1.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + add1;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + add1;
	                            }
	                        }
	                        fulAddress += "ADDRESS    :" + add1.toUpperCase();
	                        //objBillDtl.setStrItemName("ADDRESS    :"+add1.toUpperCase());
	                        // listOfHomeDeliveryDtl.add(objBillDtl);
	                    }

	                    // Street Name    
	                    String street = officeStreetName;
	                    String street1;
	                    int streetlen = street.length();
	                    for (int i = 0; i <= streetlen; i++)
	                    {
	                        int end = 0;
	                        end = i + 28;
	                        if (streetlen > end)
	                        {
	                            street1 = street.substring(i, end);
	                            if (!street1.isEmpty())
	                            {
	                                if (fullAddress.isEmpty())
	                                {
	                                    fullAddress += " " + street1;
	                                }
	                                else
	                                {
	                                    fullAddress += "," + " " + street1;
	                                }

	                            }
	                            fulAddress += "Street    :" + street1.toUpperCase();
	                            i = end;
	                        }
	                        else
	                        {
	                            street1 = street.substring(i, streetlen);
	                            if (!street1.isEmpty())
	                            {
	                                if (fullAddress.isEmpty())
	                                {
	                                    fullAddress += " " + street1;
	                                }
	                                else
	                                {
	                                    fullAddress += "," + " " + street1;
	                                }

	                                fulAddress += "Street    :" + street1.toUpperCase();
	                                i = streetlen + 1;
	                            }
	                        }
	                    }
	                    // Landmark Name    
	                    if (officeLandmark.trim().length() > 0)
	                    {
	                        if (!officeLandmark.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officeLandmark;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officeLandmark;
	                            }

	                        }
	                        fulAddress += "Landmark    :" + officeLandmark.toUpperCase();
	                    }

	                    // Area Name    
	                    if (officeArea.trim().length() > 0)
	                    {
	                        if (!officeLandmark.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officeLandmark;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officeLandmark;
	                            }
	                        }
	                    }

	                    // City Name    
	                    if (officeCity.trim().length() > 0)
	                    {
	                        if (!officeCity.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officeCity;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officeCity;
	                            }
	                        }
	                        fulAddress1 += "City    :" + officeCity.toUpperCase();
	                    }

	                    // Pin Code    
	                    if (officePinCode.trim().length() > 0)
	                    {
	                        if (!officePinCode.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officePinCode;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officePinCode;
	                            }
	                        }
	                        fulAddress1 += "Pin    :" + officePinCode.toUpperCase();
	                    }
	                    
 	                    jObjList.put("Address" ,fullAddress);
//	                    listOfHomeDeliveryDtl.put(jObjList);

	                    jObj1.put("FullAddress", fullAddress);
	                    if (mobileNo.isEmpty())
	                    {
	                    	JSONObject jObjList1=new JSONObject();
	                    	jObj1.put("MOBILE_NO", "");
	                    	
	                        objBillDtl = new clsBillDtl();
	                        jObjList.put("MOBILE_NO"," ");
//	                        listOfHomeDeliveryDtl.put(jObjList);
	                    }
	                    else
	                    {
	                    	JSONObject jObjList2=new JSONObject();
	                    	jObj1.put("MOBILE_NO", mobileNo);
	                        objBillDtl = new clsBillDtl();
	                        jObjList.put("MOBILE_NO",mobileNo);
//	                        listOfHomeDeliveryDtl.put(jObjList);
	                    }
	                    listOfHomeDeliveryDtl.put(jObjList);
	                }
	              

	                if (null != officeStreetName && officeStreetName.trim().length() > 0)
	                {
	                	JSONObject jObjList=new JSONObject();
	                	String[] delBoys = officeStreetName.split(",");
	                    StringBuilder strIN = new StringBuilder("(");
	                    for (int i = 0; i < delBoys.length; i++)
	                    {
	                        if (i == 0)
	                        {
	                            strIN.append("'" + delBoys[i] + "'");
	                        }
	                        else
	                        {
	                            strIN.append(",'" + delBoys[i] + "'");
	                        }
	                    }
	                    strIN.append(")");
	                    String SQL_DeliveryBoyDtl = "select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ;";
	                   
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_DeliveryBoyDtl);
	                    List rs_DeliveryBoyDtl = query.list();
	                    String dpName="";
	                   if (null!=rs_DeliveryBoyDtl)
	                   {
	                   
	                    strIN.setLength(0);
	                    for(int i=0; i<rs_DeliveryBoyDtl.size(); i++)
	        			{
	                    	Object[] obj = (Object[]) rs_DeliveryBoyDtl.get(i);
	        		
	        		
	                    	dpName = (String) Array.get(obj,2);
	            	  
	                   
	                        if (i == 0)
	                        {
	                            strIN.append(dpName.toUpperCase());
	                        }
	                        else
	                        {
	                            strIN.append("," + dpName.toUpperCase());
	                        }
	                    }
	                   } 

	                    if (strIN.toString().isEmpty())
	                    {
	                    	jObj1.put("DELV BOY", "");
	                    }
	                    else
	                    {
	                    	jObj1.put("DELV BOY", "Delivery Boy : " + strIN);
	                        objBillDtl = new clsBillDtl();
	                        jObjList.put("Delivery Boy : ",strIN);
	                        listOfHomeDeliveryDtl.put(jObjList);
	                    }
	                  
	                }
	                else
	                {
	                	jObj1.put("DELV BOY", "");
	                }
	            }
	        }
	           
	            int result = funPrintTakeAwayForJasper(billhd, billNo);
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress1");
	            String clientAddress1 = objSetupParameter.get("gClientAddress1").toString();
	             
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress2");
	            String clientAddress2 = objSetupParameter.get("gClientAddress2").toString();
	             
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress3");
	            String clientAddress3 = objSetupParameter.get("gClientAddress3").toString();
	             
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gCityName");		    
	            String cityName = objSetupParameter.get("gCityName").toString();
	            
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientName");		    
	            String clientName = objSetupParameter.get("gClientName").toString();
	            
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientTelNo");		    
	            String clientTelNo = objSetupParameter.get("gClientTelNo").toString();
	              
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientEmail");		    
	            String clientEmail = objSetupParameter.get("gClientEmail").toString();
	            
	            if (result == 1)
	            {
	                billType = "Take Away";
	            }
	             objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTaxInvoice");
 			    
 			    if(objSetupParameter.get("gPrintTaxInvoice").toString().equalsIgnoreCase("Y"))
	            {
 			    	jObj1.put("TAX_INVOICE", "TAX INVOICE");
	            }
 			  
	            if ( clientCode.equals("047.001") && POSCode.equals("P03"))
	            {
	            	jObj1.put("ClientName", "SHRI SHAM CATERERS");
	                String cAddr1 = "Flat No.7, Mon Amour,";
	                String cAddr2 = "Thorat Colony,Prabhat Road,";
	                String cAddr3 = " Erandwane, Pune 411 004.";
	                String cAddr4 = "Approved Caterers of";
	                String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
	                jObj1.put("ClientAddress1", cAddr1 + cAddr2);
	                jObj1.put("ClientAddress2", cAddr3 + cAddr4);
	                jObj1.put("ClientAddress3", cAddr5);
	            }
	            else if (clientCode.equals("047.001") && POSCode.equals("P02"))
	            {
	            	jObj1.put("ClientName", "SHRI SHAM CATERERS");
	                String cAddr1 = "Flat No.7, Mon Amour,";
	                String cAddr2 = "Thorat Colony,Prabhat Road,";
	                String cAddr3 = " Erandwane, Pune 411 004.";
	                String cAddr4 = "Approved Caterers of";
	                String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
	                jObj1.put("ClientAddress1", cAddr1 + cAddr2);
	                jObj1.put("ClientAddress2", cAddr3 + cAddr4);
	                jObj1.put("ClientAddress3", cAddr5);
	            }
	             
	            else if (clientCode.toString().equals("092.001") || clientCode.toString().equals("092.002") || clientCode.toString().equals("092.003"))//Shree Sound Pvt. Ltd.
	            {
	            	jObj1.put("ClientName", "SSPL");
	            	jObj1.put("ClientAddress1",new JSONObject(clientAddress1.toString()));
	            	jObj1.put("ClientAddress2", clientAddress2);
	            	jObj1.put("ClientAddress3", clientAddress3);

	                
	                if (cityName.trim().length() > 0)
	                {
	                    jObj1.put("ClientCity", cityName);
	                }
	            }
	            else
	            {
	            	jObj1.put("ClientName", clientName);
	            	jObj1.put("ClientAddress1", clientAddress1);
	            	jObj1.put("ClientAddress2", clientAddress2);
	            	jObj1.put("ClientAddress3", clientAddress3);

	                if (cityName.trim().length() > 0)
	                {
	                	jObj1.put("ClientCity", cityName);
	                }
	            }

	            jObj1.put("TEL NO", clientTelNo.toString());
	            jObj1.put("EMAIL ID", clientEmail);
	            jObj1.put("Line", Linefor5);

	            
	            String SQL_BillHD = "",billDt="",qty="";
	            String waiterName = "";
	            Date dt = null; 
	            double quantity=0;
	            String waiterNo = "";
	            String tblName = "";
	            String advDeposite="";
	            String sqlTblName = "";
	            String tabNo = "";
	            String posName="";
	            int paxNo = 0;
	            boolean flag_DirectBillerBlill = false;
	            boolean flgComplimentaryBill = false;
	            List listBillHD=query.list();
	            String sql = "select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
	                    + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' ";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            List listSettlementType=query.list();
	            if (null!=listSettlementType)
	            {
	                flgComplimentaryBill = true;
	            }
	           

	            if (funIsDirectBillerBill(billNo, billhd))
	            {
	                flag_DirectBillerBlill = true;
	                SQL_BillHD = "select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
	                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
	                        + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName "
	                        + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
	                        + "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
	                        + "where a.strBillNo='"+billNo+"'  ";
	                flag_DirectBiller = true;
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillHD);
	                 listBillHD=query.list();
	               
	                if (null!=listBillHD)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		 dt = (Date) Array.get(obj,0);
//		           		tabNo=(String) Array.get(obj,0);
		    			}
	                }	
	               // rs_BillHD.next();
	            }
	            else
	            {
	                SQL_BillHD = "select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
	                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
	                        + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo "
	                        + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
	                        + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
	                        + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
	                        + "where a.strBillNo='"+billNo+"' and b.strOperational='Y' ";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillHD);
	                 listBillHD=query.list();
	              
	                if (null!=listBillHD)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		    		
   		                tabNo = (String) Array.get(obj,0);
		           		waiterNo=(String)Array.get(obj,1);
		           		 dt=(Date)Array.get(obj,2);
		           		
		           		subTotal = (BigDecimal) Array.get(obj,5);
		           		grandTot = (BigDecimal) Array.get(obj,7);
		                user = (String) Array.get(obj,11);
		                deliveryCharge = (BigDecimal) Array.get(obj,12);
		                advAmount = (BigDecimal) Array.get(obj,13);
//		                advDeposite=(String) Array.get(obj,13);
		               
		                
		                paxNo=(int) Array.get(obj,16);
		                
		    			}
	                   
	                    if (waiterNo.equalsIgnoreCase("null") || waiterNo.equalsIgnoreCase(""))
	                    {
	                        waiterNo = "";
	                    }
	                    else
	                    {
	                       
	                        sql = "select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"'";
	                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                       List listQuery=query.list();
	                       if (listQuery!=null)
	   	                   {
	   	                	for(int i=0; i<listQuery.size(); i++)
	   		    			{
	   	                		waiterName = (String) listQuery.get(0);      		
	   		    			}  
	                        }
	                     }
	                }
	                sqlTblName = "select strTableName from tbltablemaster where strTableNo='"+tabNo+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTblName);
	                List listTblName=query.list();
	              
	                if (null!=listTblName)
	                {
	                	for(int i=0; i<listTblName.size(); i++)
   		    			{
   		           		
   		           		tblName = (String) listTblName.get(0);           		
   		    			}
	                    
	                }
	           
	         }
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTimeOnBillYN");
 			    
 			    String printTimeOnBillYN=(String)objSetupParameter.get("gPrintTimeOnBillYN");
	            // funPrintTakeAway(billhd, billNo, BillOut);
	            if (flag_DirectBillerBlill)
	            {
	            	if (null!=listBillHD)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		
		           		subTotal = (BigDecimal)Array.get(obj,3);
		           		grandTot = (BigDecimal)Array.get(obj,5);
		           		grandTotal=grandTot.doubleValue();
		                user = (String)Array.get(obj,9);
		                deliveryCharge =(BigDecimal)Array.get(obj,10);
		                advAmount = (BigDecimal)Array.get(obj,11);
		                posName = (String)Array.get(obj,13);
//		           		tabNo=(String) Array.get(obj,0);
		                
		    			}
	               }
	            	jObj1.put("POS", posName);
	            	jObj1.put("BillNo", billNo);

	                if (printTimeOnBillYN.equals("Y"))
	                {
	                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
	                     tabNo = ft.format(dt);
	                    jObj1.put("DATE_TIME", tabNo);
	                }
	                else
	                {
	                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
	                    tabNo = ft.format(dt);
	                    jObj1.put("DATE_TIME", tabNo);
	                }
	                
	                

	                
	            }
	            else
	            {
	            	jObj1.put("TABLE NAME", tblName);

	                if (waiterName.trim().length() > 0)
	                {
	                	jObj1.put("waiterName", waiterName);
	                }
	                jObj1.put("POS",posName); 
	                jObj1.put("BillNo", billNo);
	                jObj1.put("PaxNo", paxNo);

	                if (printTimeOnBillYN.equals("Y"))
	                {
	                	SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                    	billDt = ft.format(dt);
	                    jObj1.put("DATE_TIME", billDt);
	                	
	                }
	                else
	                {
	                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
	                    billDt = ft.format(dt);
	                    jObj1.put("DATE_TIME", billDt);
	                }
	                if (null!=listBillHD)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		
	                subTotal = (BigDecimal)Array.get(obj,5);
	                grandTot = (BigDecimal)Array.get(obj,7);
	                grandTotal=grandTot.doubleValue();
	                user = (String)Array.get(obj,11);
	                deliveryCharge = (BigDecimal)Array.get(obj,12);
	                advAmount = (BigDecimal)Array.get(obj,13);
		    			}
	                }
	            }

//	            List<clsBillDtl> listOfBillDetail = new ArrayList<>();
	            
	            JSONArray listOfBillDetail =new JSONArray();
//	            String SQL_BillDtl = "select sum(a.dblQuantity),a.strItemName "
//	            		 + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) "
//	            		 + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo "
//	                    + " from " + billdtl + " a "
//	                     + " where a.strBillNo='"+billNo+"' and a.tdhYN='N' "
//	                    + " group by a.strItemCode";
	            String SQL_BillDtl = "select sum(a.dblQuantity),a.strItemName "
	            		 + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo "
	                    + " from " + billdtl + " a "
	                     + " where a.strBillNo='"+billNo+"' and a.tdhYN='N' "
	                    + " group by a.strItemCode";
	                   
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillDtl);
	            List listBillDtl = query.list();
	          
	            BigDecimal saleQuantity = null;
				BigDecimal amount=null;
				double saleQty=0.00;
	            String itemCode="",itemName="";
                if (null!=listBillDtl)
                {
                	JSONObject jObjList=new JSONObject();
                	for(int i=0; i<listBillDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillDtl.get(i);
		           		saleQuantity = (BigDecimal) Array.get(obj,0);
		           		saleQty = saleQuantity.doubleValue();
		           		itemName = (String) Array.get(obj,1);
		           		amount = (BigDecimal) Array.get(obj,2); 
		           		itemCode = (String) Array.get(obj,3); 
		    			}
                    
                
	               
	                String sqlPromoBills = "select dblQuantity from " + billPromoDtl + " "
	                        + " where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "' "
	                        + " and strPromoType='ItemWise' ";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlPromoBills);
	                List listPromoItems=query.list();
	                
	                if (null!=listPromoItems)
	                {
	                	for(int i=0; i<listPromoItems.size(); i++)
			    			{
			           			Object[] obj = (Object[]) listPromoItems.get(i);
			           			quantity = (double) Array.get(obj,0);
			           			saleQty -= quantity;
			    			}
	                }
	                

	                 qty = String.valueOf(saleQty);
	                if (qty.contains("."))
	                {
	                    String decVal = qty.substring(qty.length() - 2, qty.length());
	                    if (Double.parseDouble(decVal) == 0)
	                    {
	                        qty = qty.substring(0, qty.length() - 2);
	                    }
	                }

	                if (saleQty > 0)
	                {
	                	if (null!=listBillDtl)
	                    {
	                    	
	                    	for(int i=0; i<listBillDtl.size(); i++)
	    		    			{
	    		           		Object[] obj = (Object[]) listBillDtl.get(i);
	    		           		JSONObject jObjList1=new JSONObject();
	    		           		saleQuantity = (BigDecimal) Array.get(obj,0);
	    		           		saleQty = saleQuantity.doubleValue();
	    		           		itemName = (String) Array.get(obj,1);
	    		           		amount = (BigDecimal) Array.get(obj,2); 
	    		           		itemCode = (String) Array.get(obj,3); 
	    		           		jObjList1.put("qty", qty);
			                	jObjList1.put("amount", amount);
			                	jObjList1.put("itemName", itemName);
			                	listOfBillDetail.put(jObjList1);
	    		    			}
	                    	
//	                        listOfBillDetail.put(jObjList);
	                    }	
	                	
	                	
	                

	                    objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintZeroAmtModifierOnBill");
	     			    
	     			    String printZeroAmtModifierOnBill=(String)objSetupParameter.get("gPrintZeroAmtModifierOnBill");
	                    
	                    String sqlModifier = "select count(*) "
	                            + "from " + billModifierdtl + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ";
	                    if (printZeroAmtModifierOnBill.equals("N"))
	                    {
	                        sqlModifier += " and  dblAmount !=0.00 ";
	                    }
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifier);
	                   List listCount=query.list();
	                   String countntRec="";
	                   int cntRecord = 0;
	                   if(null!=listCount)
	                   {
	                	   for(int i=0; i<listPromoItems.size(); i++)
			    			{
	                	   Object[] obj = (Object[]) listPromoItems.get(i);
	                	   countntRec = (String) Array.get(obj,0);   
	                	    cntRecord = Integer.parseInt(countntRec);
			    			}
	                   }
	                   
	                    if (cntRecord > 0)
	                    {
	                        sqlModifier = "select strModifierName,dblQuantity,dblAmount "
	                                + " from " + billModifierdtl + " "
	                                + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ";
	                        if (printZeroAmtModifierOnBill.equals("N"))
	                        {
	                            sqlModifier += " and  dblAmount !=0.00 ";
	                        }
	                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifier);
	                       List listmodifierRecord=query.list();
	                       String modifierName="";
	                       double amt=0;
	                       
	                       if(null!=listmodifierRecord)
	                        {
	                    	   for(int i=0; i<listmodifierRecord.size(); i++)
				    			{
		                	   Object[] obj = (Object[]) listmodifierRecord.get(i);
		                	   modifierName= (String) Array.get(obj,0);  
		                	   quantity = (double) Array.get(obj,1); 
		                	   amt=(double) Array.get(obj,2); 
		                	    
				    			}
	                            if (flgComplimentaryBill)
	                            {
	                            	jObjList.put("quantity", quantity);
	                            	jObjList.put("amount", 0);
	                            	jObjList.put("modifierName", modifierName.toUpperCase()); 
	                            	
	                                listOfBillDetail.put(jObjList);
	                            }
	                            else
	                            {
	                            	jObjList.put("quantity", quantity);
	                            	jObjList.put("amount", amt);
	                            	jObjList.put("modifierName", modifierName.toUpperCase());
		                            	
		                                listOfBillDetail.put(jObjList);
	                                
	                            }
	                        }
	                      
	                    }
	                }
	            }
	           

	            funPrintPromoItemsInBill(billNo, 4, listOfBillDetail);  // Print Promotion Items in Bill for this billno.

//	            List<clsBillDtl> listOfDiscountDtl = new ArrayList<>();
	            JSONArray listOfDiscountDtl =new JSONArray();
	            sql = "select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
	                    + "from " + billDscFrom + " a ,tblreasonmaster b "
	                    + "where  a.strDiscReasonCode=b.strReasonCode "
	                    + "and a.strBillNo='" + billNo + "' ";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            List listDisc=query.list();

	            boolean flag = true;
	            double dbl=0,discAmt=0;
	            String discAmount="";
	            String discOnValue="",reasonName="",discRemarks="";
	           if(null!=listDisc)
	            {
	        	   JSONObject jObjList=new JSONObject();
	        	   for(int i=0; i<listDisc.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listDisc.get(i);
	        		   dbl= (double) Array.get(obj,0);
	        		   discAmt= (double) Array.get(obj,1);
	        		   discAmount= (String)Array.get(obj,1);
	        		   discOnValue= (String) Array.get(obj,3);
	        		   reasonName= (String) Array.get(obj,4);
	        		   discRemarks= (String) Array.get(obj,5);
	    			}  
	                if (flag)
	                {
	                	 jObjList=new JSONObject();
	                	jObjList.put("Discount","Discount");
//	                   
//	                    listOfDiscountDtl.put(jObjList);
	                    flag = false;
	                }
	                
	                String discText = String.format("%.1f", dbl) + "%" + " On " + discOnValue + "";
	                if (discText.length() > 30)
	                {
	                    discText = discText.substring(0, 30);
	                }
	                else
	                {
	                    discText = String.format("%-30s", discText);
	                }

	                String discountOnItem = funPrintTextWithAlignment(discAmount, 8, "Right");
	                jObj1.put("Discount", discText + " " + discountOnItem);
	              
	                jObjList.put("discText", discText);
	                jObjList.put("discAmt", discAmt);
//	               
//	                listOfDiscountDtl.put(jObjList);

	                jObjList.put("Reason", reasonName);
	               // listOfDiscountDtl.put(jObjList);

	               
	                jObjList.put("Remark",discRemarks);
	                listOfDiscountDtl.put(jObjList);
	            }

//	            List<clsBillDtl> listOfTaxDetail = new ArrayList<>();
	            JSONArray listOfTaxDetail =new JSONArray();
	            String sql_Tax = "select b.strTaxDesc,sum(a.dblTaxAmount) "
	                    + " from " + billtaxdtl + " a,tbltaxhd b "
	                    + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
	                    + " group by a.strTaxCode";
	           
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Tax);
	            List listTax=query.list();
	            String taxDesc="";
	            double taxAmount=0;
	            
	            if(listTax.size()!=0)
	            {
	            if(null!=listTax)
	            {
	            	 JSONObject jObjList=new JSONObject();
	            	for(int i=0; i<listTax.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listTax.get(i);
	        		   taxDesc= (String) Array.get(obj,0);
	        		   taxAmount= (double) Array.get(obj,1);
	        		  
	    			}  
	                if (flgComplimentaryBill)
	                {
	                  
	                	jObjList.put("taxAmount", 0);
	                    jObjList.put("taxDesc", taxDesc);
	                    listOfTaxDetail.put(jObjList);
	                }
	                else
	                {
	                	jObjList.put("taxAmount", taxAmount);
	                	jObjList.put("taxDesc", taxDesc);
	                    listOfTaxDetail.put(jObjList);
	                    listOfTaxDetail.put(jObjList);
	                }
	            }
	            }
//	            List<clsBillDtl> listOfGrandTotalDtl = new ArrayList<>();
	            JSONArray listOfGrandTotalDtl =new JSONArray();
	            if (grandTotal> 0)
	            {
	            	 JSONObject jObjList=new JSONObject();
	                objBillDtl = new clsBillDtl();
	                objBillDtl.setDblAmount(grandTotal);
	                
	                jObjList.put("grandTotal", grandTotal);
	                listOfGrandTotalDtl.put(jObjList);
	            }

//	            List<clsBillDtl> listOfSettlementDetail = new ArrayList<>();
	            JSONArray listOfSettlementDetail =new JSONArray();
	            //settlement breakup part
	            String sqlSettlementBreakup = "select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
	                    + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
	                    + "where a.strBillNo='"+billNo+"' and a.strSettlementCode=b.strSettelmentCode";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlSettlementBreakup);
	            List listBill_Settlement=query.list();
	           double settleAmt=0;
	           String settleDesc="";
	           
	           if(null!=listBill_Settlement)
	            {
	        	   JSONObject jObjList=new JSONObject();
	        	   for(int i=0; i<listBill_Settlement.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listBill_Settlement.get(i);
	        		   settleAmt= (double) Array.get(obj,0);
	        		   settleDesc= (String) Array.get(obj,1);
	        		  
	    			 
	        	   
	                if (flgComplimentaryBill)
	                {
	                	
	                    jObjList.put("settleDesc",settleDesc);
	                   jObjList.put("settleAmt",0.00);
	                   // listOfSettlementDetail.put(jObjList);
	                }
	                else
	                {
//	                    objBillDtl = new clsBillDtl();
	                	//jObjList = new JSONObject();
	                   jObjList.put("settleDesc",settleDesc);
	                   jObjList.put("settleAmt",settleAmt);
	                   // listOfSettlementDetail.put(jObjList);
	                }
	                listOfSettlementDetail.put(jObjList);
	    			} 
	        	   
	    		} 
	          
	           
	            String sqlTenderAmt = "select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
	                    + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
	                    + " group by strBillNo";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTenderAmt);
	            List listTenderAmt=query.list();
	            double paidAmt=0,refundAmt=0;
	            
	            if (null!=listTenderAmt)
	            {
	            	 JSONObject jObjList=new JSONObject();
	            	 for(int i=0; i<listTenderAmt.size(); i++)
		    			{
		        		   Object[] obj = (Object[]) listTenderAmt.get(i);
		        		   paidAmt= (double) Array.get(obj,0);
		        		   refundAmt= (double) Array.get(obj,2);
		        		  
		    			
		            	if (flgComplimentaryBill)
		                {
		            		//jObjList = new JSONObject();
		            		jObjList.put("PAID AMT","PAID AMT");
		                   jObjList.put("paidAmt",0.00);
		                    //listOfSettlementDetail.put(jObjList);
		                }
		                else
		                {
		                	//jObjList = new JSONObject();
		                    jObjList.put("PAID AMT","PAID AMT");
		                    jObjList.put("paidAmt",paidAmt);
		                    //listOfSettlementDetail.put(jObjList);
		                    if (refundAmt > 0)
		                    {
		                    	//jObjList = new JSONObject();
		                        jObjList.put("REFUND AMT","REFUND AMT");
		                        jObjList.put("refundAmt",refundAmt);
		                       // listOfSettlementDetail.put(jObjList);
		                    }
		                }
		            	listOfSettlementDetail.put(jObjList);
		    			}	           
	            	 }
	       
	            if (flag_isHomeDelvBill)
	            {
	                String sql_count = "select count(*) from tblhomedelivery where strCustomerCode='"+customerCode+"'";
		            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_count);
	                List listCount=query.list();
	                long count = 0;
	                if (listCount!=null)
		            {
		            	for(int i=0; i<listCount.size(); i++)
		    			{
		        		   //Object[] obj = (Object[]) listCount.get(i);
//		        		   String cnt= (String) Array.get(obj,0);
//		        		   
		            		count=((BigInteger)listCount.get(0)).longValue();
		        		  
		    			}  
		            }
	               
	                jObj1.put("CUSTOMER_COUNT", count);
	            }

	            JSONArray listOfServiceVatDetail = funPrintServiceVatNoForJasper(clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
//	            List<clsBillDtl> listOfFooterDtl = new ArrayList<>();
	            
	            JSONObject jObjList=new JSONObject();
	            JSONArray listOfFooterDtl =new JSONArray();
	            
	            
	            jObjList.put("Thank", "THANK YOU AND VISIT AGAIN !!!");
	            listOfFooterDtl.put(jObjList);

	            jObj1.put("BillType", billType);
	            jObj1.put("listOfItemDtl", listOfBillDetail);
	            jObj1.put("listOfTaxDtl", listOfTaxDetail);
	            jObj1.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
	            jObj1.put("listOfServiceVatDetail", listOfServiceVatDetail);
	            jObj1.put("listOfFooterDtl", listOfFooterDtl);
	            jObj1.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
	            jObj1.put("listOfDiscountDtl", listOfDiscountDtl);
	            jObj1.put("listOfSettlementDetail", listOfSettlementDetail);
	            
	            int lengthListOfHomeDeliveryDtl=0;
	            lengthListOfHomeDeliveryDtl = listOfHomeDeliveryDtl.length();
	            jArr.put(jObj1);
	            jObjRet.put("jArr", jArr);
	            jObjRet.put("listOfBillDetail", listOfBillDetail);
	            jObjRet.put("result", result);
	            jObjRet.put("lengthListOfHomeDeliveryDtl", lengthListOfHomeDeliveryDtl);
	            jObjRet.put("format", "Jasper1");
	            jObjRet.put("flag_DirectBillerBlill", flag_DirectBillerBlill);
	            
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObjRet;
	    }


public JSONObject funGenerateBillForJasperFormat2(String billNo, String reprint, String formName, String transType, String billDate,String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
	    {
	    	HashMap hm = new HashMap();
	    	  JSONObject json = new JSONObject();
	    	  JSONArray jArr =new JSONArray();
	    	  
	         
	    	  long listOfHomeDeliveryDtlSize=0;    
	       // clsUtility objUtility = new clsUtility();
	    	JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gHOPOSType");
	        String Linefor5 = "  --------------------------------------";
	        try
	        {
//	        	JSONObject jObjList=new JSONObject();
	        	JSONObject jObj1 = new JSONObject();
	        	 JSONObject jObj=new JSONObject();
//	        	 List<clsBillDtl> listOfDiscountDtl = new ArrayList<>();
	        	
	            String user = "";
	            String billhd;
	            String billdtl;
	            String billModifierdtl;
	            String billSettlementdtl;
	            String billtaxdtl;
	            String billDscFrom = "tblbilldiscdtl";
	            String billPromoDtl = "tblbillpromotiondtl";
	            String billType = " ";

	            if (objSetupParameter.get("gHOPOSType").toString().equalsIgnoreCase("HOPOS"))
	            {
	                billhd = "tblqbillhd";
	                billdtl = "tblqbilldtl";
	                billModifierdtl = "tblqbillmodifierdtl";
	                billSettlementdtl = "tblqbillsettlementdtl";
	                billtaxdtl = "tblqbilltaxdtl";
	                billDscFrom = "tblqbilldiscdtl";
	                billPromoDtl = "tblqbillpromotiondtl";
	            }
	            else
	            {
	                if ("sales report".equalsIgnoreCase(formName))
	                {
	                    billhd = "tblbillhd";
	                    billdtl = "tblbilldtl";
	                    billModifierdtl = "tblbillmodifierdtl";
	                    billSettlementdtl = "tblbillsettlementdtl";
	                    billtaxdtl = "tblbilltaxdtl";
	                    billDscFrom = "tblbilldiscdtl";
	                    billPromoDtl = "tblbillpromotiondtl";

	                    long dateDiff =funCompareDate(billDate,billDate);
	                    if (dateDiff > 0)
	                    {
	                        billhd = "tblqbillhd";
	                        billdtl = "tblqbilldtl";
	                        billModifierdtl = "tblqbillmodifierdtl";
	                        billSettlementdtl = "tblqbillsettlementdtl";
	                        billtaxdtl = "tblqbilltaxdtl";
	                        billDscFrom = "tblqbilldiscdtl";
	                        billPromoDtl = "tblqbillpromotiondtl";
	                    }

	                    String sql = "select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' ";
	                   
	                    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	        			
		        			List listBillTable = query.list();
		        			int billCnt;
		        			String billCount="";
		        			if (listBillTable!=null)
		     				{
		     					for(int i=0; i<listBillTable.size(); i++)
		     					{
		     						
		     						billCount =(String) listBillTable.get(i);
		                     
		     					}
		     				}
		                     billCnt = Integer.parseInt(billCount);
		                    if (billCnt == 0)
		                    {
		                        billhd = "tblqbillhd";
		                        billdtl = "tblqbilldtl";
		                        billModifierdtl = "tblqbillmodifierdtl";
		                        billSettlementdtl = "tblqbillsettlementdtl";
		                        billtaxdtl = "tblqbilltaxdtl";
		                        billDscFrom = "tblqbilldiscdtl";
		                        billPromoDtl = "tblqbillpromotiondtl";
		                    }
	                }
	                else
	                {
	                    billhd = "tblbillhd";
	                    billdtl = "tblbilldtl";
	                    billModifierdtl = "tblbillmodifierdtl";
	                    billSettlementdtl = "tblbillsettlementdtl";
	                    billtaxdtl = "tblbilltaxdtl";
	                    billPromoDtl = "tblbillpromotiondtl";
	                }
	            }
	            
	            BigDecimal subTotal = null;
	            BigDecimal grandTot = null;
	            BigDecimal advAmount = null;
	            BigDecimal deliveryCharge = null;
	            String customerCode = "";
	            double grandTotal=0.00;
	            boolean flag_DirectBiller = false;

	            if (clientCode.equalsIgnoreCase("117.001"))
	            {
	                if (POSCode.equals("P01"))
	                {
	                	jObj1.put("posWiseHeading", "THE PREM'S HOTEL");
	                }
	                else if (POSCode.equals("P02"))
	                {
	                	jObj1.put("posWiseHeading", "SWIG");
	                }
	            }

	            boolean isReprint = false;
	            if ("reprint".equalsIgnoreCase(reprint))
	            {
	                isReprint = true;
	                jObj1.put("duplicate", "[DUPLICATE]");
	            }
	            if (transType.equals("Void"))
	            {
	            	jObj1.put("voidedBill", "VOIDED BILL");
	            }

	            boolean flag_isHomeDelvBill = false;
	            String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
	                    + "from tblhomedelivery where strBillNo='" + billNo + "'";
	            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_HomeDelivery);
	            List rs_HomeDelivery =query.list();
	            String billNumber="",customerName="",customerAddress = null;
	             if (rs_HomeDelivery!=null)
	             {
	             	JSONObject jObj2=new JSONObject();
	             	for(int i=0; i<rs_HomeDelivery.size(); i++)
	    			{
	           		Object[] obj = (Object[]) rs_HomeDelivery.get(i);
	    		
	    		
	           		billNumber = (String) Array.get(obj,0);
	           		customerCode=(String)Array.get(obj,1);
	           		customerAddress=(String)Array.get(obj,4);
	        	   
	    			}
	             }
//	            List<clsBillDtl> listOfHomeDeliveryDtl = new ArrayList<>();
	             JSONArray listOfHomeDeliveryDtl =new JSONArray();
	           
	            JSONObject jObjList=new JSONObject();
	            
	            String custName="",custAddress="",officeStreetName="",officeLandmark="",officeArea="",officeCity="",officePinCode="",mobileNo="";
	            if(rs_HomeDelivery.size()!=0)
	            {
	            if (rs_HomeDelivery!=null)
	            {
	                flag_isHomeDelvBill = true;
	               
	                billType = "HOME DELIVERY";

	                String SQL_CustomerDtl = "";

	                if (customerAddress.equals("Home"))
	                {
	                    SQL_CustomerDtl = "select a.strCustomerName,a.strCustAddress,a.strStreetName"
	                            + " ,a.strLandmark,a.strArea,a.strCity,a.intPinCode,a.longMobileNo "
	                            + " from tblcustomermaster a left outer join tblbuildingmaster b "
	                            + " on a.strBuldingCode=b.strBuildingCode "
	                            + " where a.strCustomerCode='"+customerCode+"' ";
	                }
	                else
	                {
	                    SQL_CustomerDtl = "select a.strCustomerName,a.strCustAddress,a.strOfficeStreetName"
	                            + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
	                            + " from tblcustomermaster a "
	                            + " where a.strCustomerCode='"+customerCode+"' ";
	                }
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_CustomerDtl);
		               
	                List rs_CustomerDtl =query.list();
		          
		             if (rs_CustomerDtl!=null)
		             {
		            	 jObjList = new JSONObject();
		             	for(int i=0; i<rs_CustomerDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) rs_CustomerDtl.get(i);
		    		
		    		
		           		custName = (String) Array.get(obj,0);
		           		custAddress=(String)Array.get(obj,1);
		           		officeStreetName=(String)Array.get(obj, 2);
		           		officeLandmark=(String)Array.get(obj, 3);
		           		officeArea=(String)Array.get(obj, 4);
		           		officeCity=(String)Array.get(obj, 5);
		           		officePinCode=(String)Array.get(obj, 6);
		           		mobileNo=(String)Array.get(obj, 7);
		    			}
		             	jObj1.put("NAME", custName);
//		             	jObjList = new JSONObject();
	                   jObjList.put("NAME",custName.toUpperCase());
	                   // listOfHomeDeliveryDtl.put(jObjList);
	                    // Building Name 
	                    String fulAddress = "";
	                    String fulAddress1 = "";
	                    String fullAddress = "";
	                    String add = custAddress;
	                    int strlen = add.length();
	                    String add1 = "";
	                    if (strlen < 28)
	                    {
	                        add1 = add.substring(0, strlen);
	                        if (!add1.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + add1;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + add1;
	                            }
	                        }
	                        fulAddress += "ADDRESS    :" + add1.toUpperCase();
	                    }
	                    else
	                    {
	                        add1 = add.substring(0, 28);
	                        if (!add1.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + add1;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + add1;
	                            }
	                        }
	                        fulAddress += "ADDRESS    :" + add1.toUpperCase();
	                        //objBillDtl.setStrItemName("ADDRESS    :"+add1.toUpperCase());
	                        // listOfHomeDeliveryDtl.add(objBillDtl);
	                    }

	                    // Street Name    
	                    String street = officeStreetName;
	                    String street1;
	                    int streetlen = street.length();
	                    for (int i = 0; i <= streetlen; i++)
	                    {
	                        int end = 0;
	                        end = i + 28;
	                        if (streetlen > end)
	                        {
	                            street1 = street.substring(i, end);
	                            if (!street1.isEmpty())
	                            {
	                                if (fullAddress.isEmpty())
	                                {
	                                    fullAddress += " " + street1;
	                                }
	                                else
	                                {
	                                    fullAddress += "," + " " + street1;
	                                }

	                            }
	                            fulAddress += "Street    :" + street1.toUpperCase();
	                            i = end;
	                        }
	                        else
	                        {
	                            street1 = street.substring(i, streetlen);
	                            if (!street1.isEmpty())
	                            {
	                                if (fullAddress.isEmpty())
	                                {
	                                    fullAddress += " " + street1;
	                                }
	                                else
	                                {
	                                    fullAddress += "," + " " + street1;
	                                }

	                                fulAddress += "Street    :" + street1.toUpperCase();
	                                i = streetlen + 1;
	                            }
	                        }
	                    }
	                    // Landmark Name    
	                    if (officeLandmark.trim().length() > 0)
	                    {
	                        if (!officeLandmark.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officeLandmark;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officeLandmark;
	                            }

	                        }
	                        fulAddress += "Landmark    :" + officeLandmark.toUpperCase();
	                    }

	                    // Area Name    
	                    if (officeArea.trim().length() > 0)
	                    {
	                        if (!officeLandmark.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officeLandmark;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officeLandmark;
	                            }
	                        }
	                    }

	                    // City Name    
	                    if (officeCity.trim().length() > 0)
	                    {
	                        if (!officeCity.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officeCity;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officeCity;
	                            }
	                        }
	                        fulAddress1 += "City    :" + officeCity.toUpperCase();
	                    }

	                    // Pin Code    
	                    if (officePinCode.trim().length() > 0)
	                    {
	                        if (!officePinCode.isEmpty())
	                        {
	                            if (fullAddress.isEmpty())
	                            {
	                                fullAddress += " " + officePinCode;
	                            }
	                            else
	                            {
	                                fullAddress += "," + " " + officePinCode;
	                            }
	                        }
	                        fulAddress1 += "Pin    :" + officePinCode.toUpperCase();
	                    }
//	                    objBillDtl = new clsBillDtl();
	                 //   jObjList = new JSONObject();
	                   jObjList.put("Address",fullAddress);
	                   // listOfHomeDeliveryDtl.put(jObjList);

	                    jObj1.put("FullAddress", fullAddress);
	                    if (mobileNo.isEmpty())
	                    {
	                    	jObj1.put("MOBILE_NO", "");
//	                    	jObjList = new JSONObject();
	                    	jObjList.put("MOBILE_NO"," ");
	                        //listOfHomeDeliveryDtl.put(jObjList);
	                    }
	                    else
	                    {
	                    	jObj1.put("MOBILE_NO", mobileNo);
//	                    	jObjList = new JSONObject();
	                    	jObjList.put("MOBILE_NO",mobileNo);
	                        //listOfHomeDeliveryDtl.put(jObjList);
	                    }
	                    listOfHomeDeliveryDtl.put(jObjList);
	                }
	            }

	                if (null != officeStreetName  && officeStreetName .trim().length() > 0)
	                {
	                    String[] delBoys = officeStreetName .split(",");
	                    StringBuilder strIN = new StringBuilder("(");
	                    for (int i = 0; i < delBoys.length; i++)
	                    {
	                        if (i == 0)
	                        {
	                            strIN.append("'" + delBoys[i] + "'");
	                        }
	                        else
	                        {
	                            strIN.append(",'" + delBoys[i] + "'");
	                        }
	                    }
	                    strIN.append(")");
	                    String SQL_DeliveryBoyDtl = "select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ;";
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_DeliveryBoyDtl);
	                    List rs_DeliveryBoyDtl = query.list();
	                    String dpName="";
	                   if (rs_DeliveryBoyDtl!=null)
	                   {
	                    strIN.setLength(0);
	                    for(int i=0; i<rs_DeliveryBoyDtl.size(); i++)
	        			{
	                    	Object[] obj = (Object[]) rs_DeliveryBoyDtl.get(i);
	        		
	        		
	                    	dpName = (String) Array.get(obj,2);
	            	  
	                   
	                       if (i == 0)
	                        {
	                            strIN.append(dpName.toUpperCase());
	                        }
	                        else
	                        {
	                            strIN.append("," + dpName.toUpperCase());
	                        }
	                    }
	                   }
	                    if (strIN.toString().isEmpty())
	                    {
	                    	jObj1.put("DELV BOY", "");
	                    }
	                    else
	                    {
	                    	jObj1.put("DELV BOY", "Delivery Boy : " + strIN);
	                    	//jObjList = new JSONObject();
	                    	jObjList.put("DELV BOY",strIN);
	                        listOfHomeDeliveryDtl.put(jObjList);
	                    }
	                   
	                }
	                else
	                {
	                	jObj1.put("DELV BOY", "");
	                }
	                //listOfHomeDeliveryDtl.put(jObjList);
	               // listOfHomeDeliveryDtlSize=listOfHomeDeliveryDtl.length();
	            }
	        

	            int result = funPrintTakeAwayForJasper(billhd, billNo);
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress1");
	            String clientAddress1 = objSetupParameter.get("gClientAddress1").toString();
	             
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress2");
	            String clientAddress2 = objSetupParameter.get("gClientAddress2").toString();
	             
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress3");
	            String clientAddress3 = objSetupParameter.get("gClientAddress3").toString();
	             
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gCityName");		    
	            String cityName = objSetupParameter.get("gCityName").toString();
	            
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientName");		    
	            String clientName = objSetupParameter.get("gClientName").toString();
	            
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientTelNo");		    
	            String clientTelNo = objSetupParameter.get("gClientTelNo").toString();
	              
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientEmail");		    
	            String clientEmail = objSetupParameter.get("gClientEmail").toString();
	            
	            
	            if (result == 1)
	            {
	                billType = "Take Away";
	            }
	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTaxInvoice");
			    
			    if(objSetupParameter.get("gPrintTaxInvoice").toString().equalsIgnoreCase("Y"))
	            {
			    	jObj1.put("TAX_INVOICE", "TAX INVOICE");
	            }
	            if ( clientCode.equals("047.001") && POSCode.equals("P03"))
	            {
	            	jObj1.put("ClientName", "SHRI SHAM CATERERS");
	                String cAddr1 = "Flat No.7, Mon Amour,";
	                String cAddr2 = "Thorat Colony,Prabhat Road,";
	                String cAddr3 = " Erandwane, Pune 411 004.";
	                String cAddr4 = "Approved Caterers of";
	                String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
	                jObj1.put("ClientAddress1", cAddr1 + cAddr2);
	                jObj1.put("ClientAddress2", cAddr3 + cAddr4);
	                jObj1.put("ClientAddress3", cAddr5);
	            }
	            else if (clientCode.equals("047.001") && POSCode.equals("P02"))
	            {
	            	jObj1.put("ClientName", "SHRI SHAM CATERERS");
	                String cAddr1 = "Flat No.7, Mon Amour,";
	                String cAddr2 = "Thorat Colony,Prabhat Road,";
	                String cAddr3 = " Erandwane, Pune 411 004.";
	                String cAddr4 = "Approved Caterers of";
	                String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
	                jObj1.put("ClientAddress1", cAddr1 + cAddr2);
	                jObj1.put("ClientAddress2", cAddr3 + cAddr4);
	                jObj1.put("ClientAddress3", cAddr5);
	            }
	            else if (clientCode.toString().equals("092.001") || clientCode.toString().equals("092.002") || clientCode.toString().equals("092.003"))//Shree Sound Pvt. Ltd.(Waters)
	            {
	            	jObj1.put("ClientName", "SSPL");
	            	jObj1.put("ClientAddress1",clientAddress1);
	            	jObj1.put("ClientAddress2", clientAddress2);
	            	jObj1.put("ClientAddress3", clientAddress3);

	                if (cityName.trim().length() > 0)
	                {
	                	jObj1.put("ClientCity", cityName);
	                }
	            }
	            else
	            {
	            	jObj1.put("ClientName",clientName);
	            	jObj1.put("ClientAddress1", clientAddress1);
	            	jObj1.put("ClientAddress2", clientAddress2);
	            	jObj1.put("ClientAddress3", clientAddress3);

	                if (cityName.trim().length() > 0)
	                {
	                	jObj1.put("ClientCity", cityName);
	                }
	            }

	            jObj1.put("TEL NO", clientTelNo);
	            jObj1.put("EMAIL ID", clientEmail);
	            jObj1.put("Line", Linefor5);

	            String SQL_BillHD = "",billDt="",qty="";
	            String waiterName = "";
	            Date dt = null; 
	            double quantity=0;
	            String waiterNo = "";
	            String tblName = "";
	            String advDeposite="";
	            String sqlTblName = "";
	            String tabNo = "";
	            String posName="";
	            int paxNo = 0;
	            boolean flag_DirectBillerBlill = false;
	            boolean flgComplimentaryBill = false;
	            List listBillHD=query.list();
	            String sql = "select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
	                    + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' ";
	            List listSettlementType=query.list();
	            if (listSettlementType!=null)
	            {
	                flgComplimentaryBill = true;
	            }
	          

	            if (funIsDirectBillerBill(billNo, billhd))
	            {
	                flag_DirectBillerBlill = true;
	                SQL_BillHD = "select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
	                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
	                        + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName "
	                        + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
	                        + "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
	                        + "where a.strBillNo='"+billNo+"' ";
	                flag_DirectBiller = true;
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillHD);
	                 listBillHD=query.list();
	               
	                if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		 dt = (Date) Array.get(obj,0);
//		           		tabNo=(String) Array.get(obj,0);
		    			}
	                }	
	            }
	            else
	            {
	                SQL_BillHD = "select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
	                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
	                        + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo "
	                        + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
	                        + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
	                        + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
	                        + "where a.strBillNo='"+billNo+"' and b.strOperational='Y' ";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillHD);
	                 listBillHD=query.list();
	               
	                if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		    		
		    		
		           		tabNo = (String) Array.get(obj,0);
		           		waiterNo=(String)Array.get(obj,1);
		           		 dt=(Date)Array.get(obj,2);
		           		
		           		subTotal = (BigDecimal) Array.get(obj,5);
		           		grandTot = (BigDecimal) Array.get(obj,7);
		                user = (String) Array.get(obj,11);
		                deliveryCharge = (BigDecimal) Array.get(obj,12);
		                advAmount = (BigDecimal) Array.get(obj,13);
		    			}
	                 
	                    if (waiterNo.equalsIgnoreCase("null") || waiterNo.equalsIgnoreCase(""))
	                    {
	                        waiterNo = "";
	                    }
	                    else
	                    {
	                        
	                        sql = "select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"'";
	                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		                       List listQuery=query.list();
		                       if (listQuery!=null)
		   	                   {
		   	                	for(int i=0; i<listQuery.size(); i++)
		   		    			{
		   		           	
		   	   		           	waiterName = (String)listQuery.get(0);           		
		   		    			}
		                        
		                        }
		                     }
		                }

	                sqlTblName = "select strTableName from tbltablemaster where strTableNo='"+tabNo+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTblName);
	                List listTblName=query.list();
	              
	                if (listTblName!=null)
	                {
	                	for(int i=0; i<listTblName.size(); i++)
 		    			{
 		           		
 		           		tblName = (String) listTblName.get(0);           		
 		    			}
	                    
	                }
	            }

	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTimeOnBillYN");
			    
			    String printTimeOnBillYN=(String)objSetupParameter.get("gPrintTimeOnBillYN");
	           
	            // funPrintTakeAway(billhd, billNo, BillOut);
	            if (flag_DirectBillerBlill)
	            {
	            	if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		
		           		subTotal = (BigDecimal)Array.get(obj,3);
		           		grandTot = (BigDecimal)Array.get(obj,5);
		           		grandTotal=grandTot.doubleValue();
		                user = (String)Array.get(obj,9);
		                deliveryCharge =(BigDecimal)Array.get(obj,10);
		                advAmount = (BigDecimal)Array.get(obj,11);
		                posName = (String)Array.get(obj,13);
//		           		tabNo=(String) Array.get(obj,0);
		    			}
	                }
	            	jObj1.put("POS", posName);
	            	jObj1.put("BillNo", billNo);

	                if (printTimeOnBillYN.equals("Y"))
	                {
	                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
	                    tabNo = ft.format(dt);
	                    jObj1.put("DATE_TIME",tabNo);
	                }
	                else
	                {
	                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
	                    tabNo = ft.format(dt);
	                    jObj1.put("DATE_TIME",tabNo);
	                }

	            }
	            else
	            {
	            	jObj1.put("TABLE NAME", tblName);

	                if (waiterName.trim().length() > 0)
	                {
	                	jObj1.put("waiterName", waiterName);
	                }
	                jObj1.put("POS",posName);
	                jObj1.put("BillNo", billNo);
	                jObj1.put("PaxNo", paxNo);

	                if (printTimeOnBillYN.equals("Y"))
	                {
	                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
	                    billDt = ft.format(dt);
	                    jObj1.put("DATE_TIME", billDt);
	                }
	                else
	                {
	                    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
	                    billDt = ft.format(dt);
	                    jObj1.put("DATE_TIME", billDt);
	                }
	                if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		
	                subTotal = (BigDecimal)Array.get(obj,5);
	                grandTot = (BigDecimal)Array.get(obj,7);
	                grandTotal=grandTot.doubleValue();
	                user = (String)Array.get(obj,11);
	                deliveryCharge = (BigDecimal)Array.get(obj,12);
	                advAmount = (BigDecimal)Array.get(obj,13);
		    			}
	                }
	            }

//	            List<clsBillDtl> listOfBillDetail = new ArrayList<>();
	            JSONArray listOfBillDetail = new JSONArray();
//	            String SQL_BillDtl = "select sum(a.dblQuantity),a.strItemName as ItemLine1"
//	                    + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) as ItemLine2"
//	                    + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,a.dblRate,sum(a.dblDiscountAmt)  "
//	                    + " from " + billdtl + " a "
//	                    + " where a.strBillNo='"+billNo+"' and a.tdhYN='N' "
//	                    + " group by a.strItemCode ;";
	            
	            String SQL_BillDtl="select sum(a.dblQuantity),a.strItemName "
	            		 + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,a.dblRate,sum(a.dblDiscountAmt)  "
		                    + " from " + billdtl + " a "
		                     + " where a.strBillNo='"+billNo+"' and a.tdhYN='N' "
		                    + " group by a.strItemCode";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillDtl);
	            List listBillDtl = query.list();
	          
	            BigDecimal saleQuantity = null;
				BigDecimal amount=null;
				double saleQty=0.00;
	            String itemCode="",itemName="";

              if (listBillDtl!=null)
	            {
//              	JSONObject jObjList=new JSONObject();
              	for(int i=0; i<listBillDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillDtl.get(i);
		           		saleQuantity = (BigDecimal) Array.get(obj,0);
		           		saleQty = saleQuantity.doubleValue();
		           		itemName = (String) Array.get(obj,1);
		           		amount = (BigDecimal) Array.get(obj,2); 
		           		itemCode = (String) Array.get(obj,3); 
		    			}
              	
	                String sqlPromoBills = "select dblQuantity from " + billPromoDtl + " "
	                        + " where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "' "
	                        + " and strPromoType='ItemWise' ";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlPromoBills);
	                List listPromoItems=query.list();
	                
	                if (listPromoItems!=null)
	                {
	                	for(int i=0; i<listPromoItems.size(); i++)
			    			{
			           			Object[] obj = (Object[]) listPromoItems.get(i);
			           			quantity = (double) Array.get(obj,0);
			           			saleQty -= quantity;
			    			}
	                }
	                qty = String.valueOf(saleQty);
	                if (qty.contains("."))
	                {
	                    String decVal = qty.substring(qty.length() - 2, qty.length());
	                    if (Double.parseDouble(decVal) == 0)
	                    {
	                        qty = qty.substring(0, qty.length() - 2);
	                    }
	                }
	               double dblAmount=0.00,rate=0.00,discountAmt=0.00;
	               BigDecimal bd=null,bd1=null,bd2=null;
	                if (saleQty > 0)
	                {
	                	
	                	if (listBillDtl!=null)
	                    {
	                    	
	                    	for(int i=0; i<listBillDtl.size(); i++)
	    		    			{
	    		           		Object[] obj = (Object[]) listBillDtl.get(i);
	    		           		itemName = (String) Array.get(obj,1);
	    		           		bd=(BigDecimal) Array.get(obj, 2);
	    		           		bd1=(BigDecimal) Array.get(obj, 5);
	    		           		bd2=(BigDecimal)Array.get(obj, 6);
	    		           		
	    		           		
	    		           		dblAmount = bd.doubleValue(); // The double you want
	    		           		rate = bd1.doubleValue(); 
	    		           		discountAmt = bd2.doubleValue(); 
	    		           		jObjList = new JSONObject();
	    		           		jObjList.put("saleQty",saleQty);
	    		           		jObjList.put("dblAmount",dblAmount);
	    		           		jObjList.put("itemName",itemName);
	    		           		jObjList.put("rate",rate);
	    		           		jObjList.put("discountAmt",discountAmt);

	    	                    listOfBillDetail.put(jObjList);

	    		    			}
	                    }	
	                    
	                	 objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintZeroAmtModifierOnBill");
		     			    
		     		    String printZeroAmtModifierOnBill=(String)objSetupParameter.get("gPrintZeroAmtModifierOnBill");
		                    
	                    String sqlModifier = "select count(*) "
	                            + "from " + billModifierdtl + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ";
	                    if (printZeroAmtModifierOnBill.equals("N"))
	                    {
	                        sqlModifier += " and  dblAmount !=0.00 ";
	                    }
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifier);
		                   List listCount=query.list();
		                   String countntRec="";
		                   int cntRecord = 0;
		                   if(listCount!=null)
		                   {
		                	   for(int i=0; i<listPromoItems.size(); i++)
				    			{
		                	   Object[] obj = (Object[]) listPromoItems.get(i);
		                	   countntRec = (String) Array.get(obj,0);   
		                	    cntRecord = Integer.parseInt(countntRec);
				    			}
		                   }
	                    
	                    if (cntRecord > 0)
	                    {
	                        sqlModifier = "select strModifierName,dblQuantity,dblAmount,dblRate,dblDiscAmt "
	                                + " from " + billModifierdtl + " "
	                                + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"'";
	                        if (printZeroAmtModifierOnBill.equals("N"))
	                        {
	                            sqlModifier += " and  dblAmount !=0.00 ";
	                        }
	                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifier);
		                       List listmodifierRecord=query.list();
		                       String modifierName="";
		                       double amt=0;
		                       
		                       if(listmodifierRecord!=null)
		                        {
		                    	   for(int i=0; i<listmodifierRecord.size(); i++)
					    			{
			                	   Object[] obj = (Object[]) listmodifierRecord.get(i);
			                	   modifierName= (String) Array.get(obj,0);  
			                	   quantity = (double) Array.get(obj,1); 
			                	   amt=(double) Array.get(obj,2); 
			                	   rate=(double) Array.get(obj,3);
			                	   discountAmt=(double) Array.get(obj,4);
					    			}
		                          
	                            if (flgComplimentaryBill)
	                            {
	                            	jObjList = new JSONObject();
	                                jObjList.put("quantity",quantity);
	                                jObjList.put("amt",0);
	                                jObjList.put("modifierName",modifierName.toUpperCase());
	                                jObjList.put("rate",rate);
	                                jObjList.put("discountAmt",discountAmt);
	                                listOfBillDetail.put(jObjList);
	                            }
	                            else
	                            {
	                            	jObjList = new JSONObject();
	                            	 jObjList.put("quantity",quantity);
	                            	 jObjList.put("amt",amt);
	                            	 jObjList.put("modifierName",modifierName.toUpperCase());
	                            	 jObjList.put("rate",rate);
	                            	 jObjList.put("discountAmt",discountAmt);
	                                listOfBillDetail.put(jObjList);
	                            }
	                        }
	                       
	                    }
	                }
	        }
	            
	               
	            funPrintPromoItemsInBillFormat2(billNo, 4, listOfBillDetail);  // Print Promotion Items in Bill for this billno.

//	            List<clsBillDtl> listOfDiscountDtl = new ArrayList<>();
	            JSONArray listOfDiscountDtl = new JSONArray();
	            sql = "select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
	                    + "from " + billDscFrom + " a ,tblreasonmaster b "
	                    + "where  a.strDiscReasonCode=b.strReasonCode "
	                    + "and a.strBillNo='" + billNo + "' ";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            List listDisc=query.list();

	            boolean flag = true;
	            double dbl=0,discAmt=0;
	            String discAmount="";
	            String discOnValue="",reasonName="",discRemarks="";
	           if(listDisc!=null)
	            {
	        	   jObjList = new JSONObject();
	        	   for(int i=0; i<listDisc.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listDisc.get(i);
	        		   dbl= (double) Array.get(obj,0);
	        		   discAmt= (double) Array.get(obj,1);
	        		   discAmount= (String)Array.get(obj,2);
	        		   discOnValue= (String) Array.get(obj,3);
	        		   reasonName= (String) Array.get(obj,4);
	        		   discRemarks= (String) Array.get(obj,5);
	    			}  
	          
	                if (flag)
	                {
	                	  
	                	jObjList = new JSONObject();
	                	jObjList.put("Discount","Discount");
	                    //listOfDiscountDtl.put(jObjList);
	                    flag = false;
	                }
	                
	                String discText = String.format("%.1f", dbl) + "%" + " On " + discOnValue + "";
	                if (discText.length() > 30)
	                {
	                    discText = discText.substring(0, 30);
	                }
	                else
	                {
	                    discText = String.format("%-30s", discText);
	                }

	                String discountOnItem = funPrintTextWithAlignment(discAmount, 8, "Right");
	                jObj1.put("Discount", discText + " " + discountOnItem);
	                jObjList.put("discText",discText);
	                //jObjList = new JSONObject();
	                jObjList.put("discAmt",discAmt);
	               // listOfDiscountDtl.put(jObjList);

	                //jObjList = new JSONObject();
	                jObjList.put("Reason",reasonName);
	                //listOfDiscountDtl.put(jObjList);

	                //jObjList = new JSONObject();
	                jObjList.put("Remark",discRemarks);
	                listOfDiscountDtl.put(jObjList);
	            }

//	            List<clsBillDtl> listOfTaxDetail = new ArrayList<>();
	           JSONArray listOfTaxDetail = new JSONArray();
	            String sql_Tax = "select b.strTaxDesc,sum(a.dblTaxAmount) "
	                    + " from " + billtaxdtl + " a,tbltaxhd b "
	                    + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
	                    + " group by a.strTaxCode";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Tax);
	            List listTax=query.list();
	            String taxDesc="";
	            double taxAmount=0;
	            
	            if(listTax!=null)
	            {
	            	for(int i=0; i<listTax.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listTax.get(i);
	        		   taxDesc= (String) Array.get(obj,0);
	        		   taxAmount= (double) Array.get(obj,1);
	        		  
	    			
	            	
	                if (flgComplimentaryBill)
	                {
	                	jObjList = new JSONObject();
	                	jObjList.put("taxAmount",0);
	                    jObjList.put("taxDesc",taxDesc);
	                    listOfTaxDetail.put(jObjList);
	                }
	                else
	                {
	                	jObjList = new JSONObject();
	                	jObjList.put("taxAmount",taxAmount);
	                    jObjList.put("taxDesc",taxDesc);
	                    listOfTaxDetail.put(jObjList);
	                }
	    			}
	            }
	           
//	            List<clsBillDtl> listOfGrandTotalDtl = new ArrayList<>();
	            JSONArray listOfGrandTotalDtl = new JSONArray();
	            if (grandTotal > 0)
	            {
	            	jObjList = new JSONObject();
	            	jObjList.put("grandTotal",grandTotal);
	                listOfGrandTotalDtl.put(jObjList);
	            }

//	            List<clsBillDtl> listOfSettlementDetail = new ArrayList<>();
	            JSONArray listOfSettlementDetail = new JSONArray();
	            //settlement breakup part
	            String sqlSettlementBreakup = "select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
	                    + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
	                    + "where a.strBillNo='"+billNo+"' and a.strSettlementCode=b.strSettelmentCode";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlSettlementBreakup);
	            List listBill_Settlement=query.list();
	           double settleAmt=0;
	           String settleDesc="";
	           
	           if(listBill_Settlement!=null)
	            {
	        	  jObjList=new JSONObject();
	        	   for(int i=0; i<listBill_Settlement.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listBill_Settlement.get(i);
	        		   settleAmt= (double) Array.get(obj,0);
	        		   settleDesc= (String) Array.get(obj,1);
	        		  
	    			 
	        	   
	                if (flgComplimentaryBill)
	                {
	                	
	                    jObjList.put("settleDesc",settleDesc);
	                   jObjList.put("settleAmt",0.00);
	                   // listOfSettlementDetail.put(jObjList);
	                }
	                else
	                {
//	                    objBillDtl = new clsBillDtl();
	                	//jObjList = new JSONObject();
	                   jObjList.put("settleDesc",settleDesc);
	                   jObjList.put("settleAmt",settleAmt);
	                   // listOfSettlementDetail.put(jObjList);
	                }
	                listOfSettlementDetail.put(jObjList);
	    			} 
	        	   
	            }
	           

	            String sqlTenderAmt = "select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
	                    + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
	                    + " group by strBillNo";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTenderAmt);
	            List listTenderAmt=query.list();
	            double paidAmt=0,refundAmt=0;
	            
	            if (listTenderAmt!=null)
	            {  
	            	//jObjList = new JSONObject();
	            	for(int i=0; i<listTenderAmt.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listTenderAmt.get(i);
	        		   paidAmt= (double) Array.get(obj,0);
	        		   refundAmt= (double) Array.get(obj,2);
	        		  
	    			
	            	if (flgComplimentaryBill)
	                {
	            		//jObjList = new JSONObject();
	            		jObjList.put("PAID AMT","PAID AMT");
	                   jObjList.put("paidAmt",0.00);
	                    //listOfSettlementDetail.put(jObjList);
	                }
	                else
	                {
	                	//jObjList = new JSONObject();
	                    jObjList.put("PAID AMT","PAID AMT");
	                    jObjList.put("paidAmt",paidAmt);
	                    //listOfSettlementDetail.put(jObjList);
	                    if (refundAmt > 0)
	                    {
	                    	//jObjList = new JSONObject();
	                        jObjList.put("REFUND AMT","REFUND AMT");
	                        jObjList.put("refundAmt",refundAmt);
	                       // listOfSettlementDetail.put(jObjList);
	                    }
	                }
	            	listOfSettlementDetail.put(jObjList);
	    			}
	            	
	            }
	            
	            if (flag_isHomeDelvBill)
	            {
	                String sql_count = "select count(*) from tblhomedelivery where strCustomerCode='"+customerCode+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_count);
	                List listCount=query.list();
	                long count = 0;
	                if (listCount!=null)
		            {
		            	for(int i=0; i<listCount.size(); i++)
		    			{
		        		   //Object[] obj = (Object[]) listCount.get(i);
//		        		   String cnt= (String) Array.get(obj,0);
//		        		   
		            		count=((BigInteger)listCount.get(0)).longValue();
		        		  
		    			}  
		            }
	                jObj1.put("CUSTOMER_COUNT", count);
	            }

	            String sql_count = "select b.longMobileNo,b.strCustomerName "
	                    + "from " + billhd + " a,tblcustomermaster b "
	                    + "where a.strCustomerCode=b.strCustomerCode "
	                    + "and a.strBillNo='" + billNo + "'";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_count);
	            List listCount=query.list();
              long count = 0;
             
//	            List<clsBillDtl> listOfCustomerDtl = new ArrayList<>();
              JSONArray listOfCustomerDtl = new JSONArray(); 
	            if (listCount!=null)
	            {
	            	for(int i=0; i<listCount.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listCount.get(i);
//	        		   String cnt= (String) Array.get(obj,0);
//	        		   
	            		mobileNo=(String) Array.get(obj,0);
	            		customerName=(String) Array.get(obj,1);
	    			}  
	            	jObjList = new JSONObject();
	               jObjList.put("CUSTOMER NAME:",customerName);
	               // listOfCustomerDtl.put(jObjList);
	                
//	                jObjList = new JSONObject();
	                jObjList.put("mobileNo",mobileNo);
	                listOfCustomerDtl.put(jObjList);
	              
	            }

	            JSONArray listOfServiceVatDetail = funPrintServiceVatNoForJasper(clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
//	            List<clsBillDtl> listOfFooterDtl = new ArrayList<>();
	            JSONArray listOfFooterDtl = new JSONArray();
	            jObjList = new JSONObject();
	            jObjList.put("Thank","THANK YOU AND VISIT AGAIN !!!");
	            listOfFooterDtl.put(jObjList);

	            jObj1.put("BillType", billType);
	            jObj1.put("listOfItemDtl", listOfBillDetail);
	            jObj1.put("listOfTaxDtl", listOfTaxDetail);
	            jObj1.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
	            jObj1.put("listOfServiceVatDetail", listOfServiceVatDetail);
	            jObj1.put("listOfFooterDtl", listOfFooterDtl);
	            jObj1.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
	            jObj1.put("listOfDiscountDtl", listOfDiscountDtl);
	            jObj1.put("listOfSettlementDetail", listOfSettlementDetail);
	            jObj1.put("listOfCustomerDtl", listOfCustomerDtl);
	            jArr.put(jObj1);
	          
	            json.put("listOfBillDetail", listOfBillDetail);
	            json.put("format", "Jasper2");
	            json.put("result", result);
	            json.put("jArr", jArr);
	            json.put("flag_DirectBillerBlill", flag_DirectBillerBlill);
//	            json.put("listOfHomeDeliveryDtlSize", listOfHomeDeliveryDtlSize);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return json;
	        }
	    } 
	    public JSONObject funGenerateBillForJasperFormat3(String billNo, String reprint, String formName, String transType, String billDate,String clientCode,String POSCode,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
        {

            HashMap hm = new HashMap();
            JSONObject json = new JSONObject();
	    	  JSONArray jArr =new JSONArray();
	    	  
            
           // clsUtility objUtility = new clsUtility();
            JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gHOPOSType");
            String Linefor5 = "  --------------------------------------";
            try
            {
//	        	JSONObject jObjList=new JSONObject();
	        	JSONObject jObj1 = new JSONObject();
                String user = "";
                String billhd;
                String billdtl;
                String billModifierdtl;
                String billSettlementdtl;
                String billtaxdtl;
                String billDscFrom = "tblbilldiscdtl";
                String billPromoDtl = "tblbillpromotiondtl";
                String billType = " ";

                if (objSetupParameter.get("gHOPOSType").toString().equalsIgnoreCase("HOPOS"))
                {
                    billhd = "tblqbillhd";
                    billdtl = "tblqbilldtl";
                    billModifierdtl = "tblqbillmodifierdtl";
                    billSettlementdtl = "tblqbillsettlementdtl";
                    billtaxdtl = "tblqbilltaxdtl";
                    billDscFrom = "tblqbilldiscdtl";
                    billPromoDtl = "tblqbillpromotiondtl";
                }
                else
                {
                    if ("sales report".equalsIgnoreCase(formName))
                    {
                        billhd = "tblbillhd";
                        billdtl = "tblbilldtl";
                        billModifierdtl = "tblbillmodifierdtl";
                        billSettlementdtl = "tblbillsettlementdtl";
                        billtaxdtl = "tblbilltaxdtl";
                        billDscFrom = "tblbilldiscdtl";
                        billPromoDtl = "tblbillpromotiondtl";

                        long dateDiff = funCompareDate(billDate, billDate);
                        if (dateDiff > 0)
                        {
                            billhd = "tblqbillhd";
                            billdtl = "tblqbilldtl";
                            billModifierdtl = "tblqbillmodifierdtl";
                            billSettlementdtl = "tblqbillsettlementdtl";
                            billtaxdtl = "tblqbilltaxdtl";
                            billDscFrom = "tblqbilldiscdtl";
                            billPromoDtl = "tblqbillpromotiondtl";
                        }

                        String sql = "select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' ";
                        Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	        			
	        			List listBillTable = query.list();
	        			int billCnt;
	        			String billCount="";
	        			if (listBillTable!=null)
	     				{
	     					for(int i=0; i<listBillTable.size(); i++)
	     					{
	     						
	     						billCount =(String) listBillTable.get(i);
	                     
	     					}
	     				}
	                     billCnt = Integer.parseInt(billCount);
                        if (billCnt == 0)
                        {
                            billhd = "tblqbillhd";
                            billdtl = "tblqbilldtl";
                            billModifierdtl = "tblqbillmodifierdtl";
                            billSettlementdtl = "tblqbillsettlementdtl";
                            billtaxdtl = "tblqbilltaxdtl";
                            billDscFrom = "tblqbilldiscdtl";
                            billPromoDtl = "tblqbillpromotiondtl";
                        }
                    }
                    else
                    {
                        billhd = "tblbillhd";
                        billdtl = "tblbilldtl";
                        billModifierdtl = "tblbillmodifierdtl";
                        billSettlementdtl = "tblbillsettlementdtl";
                        billtaxdtl = "tblbilltaxdtl";
                        billPromoDtl = "tblbillpromotiondtl";
                    }
              }
              
                BigDecimal subTotal = null;
	            BigDecimal grandTot = null;
	            BigDecimal advAmount = null;
	            BigDecimal deliveryCharge = null;
	            String customerCode = "";
	            double grandTotal=0.00;
	            boolean flag_DirectBiller = false;
                if (clientCode.equals("117.001"))
                {
                    if (POSCode.equals("P01"))
                    {
                    	jObj1.put("posWiseHeading", "THE PREM'S HOTEL");
                    }
                    else if (POSCode.equals("P02"))
                    {
                    	jObj1.put("posWiseHeading", "SWIG");
                    }
                }

                boolean isReprint = false;
                if ("reprint".equalsIgnoreCase(reprint))
                {
                    isReprint = true;
                    jObj1.put("duplicate", "[DUPLICATE]");
                }
                if (transType.equals("Void"))
                {
                	jObj1.put("voidedBill", "VOIDED BILL");
                }

                boolean flag_isHomeDelvBill = false;
                String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
                        + "from tblhomedelivery where strBillNo='"+billNo+"'";
                Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_HomeDelivery);
	            List rs_HomeDelivery =query.list();
	            String billNumber="",customerName="",customerAddress = null;
	             if (rs_HomeDelivery!=null)
	             {
	             	JSONObject jObj2=new JSONObject();
	             	for(int i=0; i<rs_HomeDelivery.size(); i++)
	    			{
	           		Object[] obj = (Object[]) rs_HomeDelivery.get(i);
	    		
	    		
	           		billNumber = (String) Array.get(obj,0);
	           		customerCode=(String)Array.get(obj,1);
	           		customerAddress=(String)Array.get(obj,4);
	        	   
	    			}
	             }
//	            List<clsBillDtl> listOfHomeDeliveryDtl = new ArrayList<>();
	             JSONArray listOfHomeDeliveryDtl =new JSONArray();
	           
	            JSONObject jObjList=new JSONObject();
                if (rs_HomeDelivery.size()!=0)
                {
                	if (rs_HomeDelivery!=null)
	                {
                    flag_isHomeDelvBill = true;
                  
                    billType = "HOME DELIVERY";

                    String SQL_CustomerDtl = "";

                    if (customerAddress.equals("Home"))
                    {
                        SQL_CustomerDtl = "select a.strCustomerName,a.strBuildingName,a.strStreetName"
                                + " ,a.strLandmark,a.strArea,a.strCity,a.intPinCode,a.longMobileNo "
                                + " from tblcustomermaster a left outer join tblbuildingmaster b "
                                + " on a.strBuldingCode=b.strBuildingCode "
                                + " where a.strCustomerCode='"+customerCode+"'";
                    }
                    else
                    {
                        SQL_CustomerDtl = "select a.strCustomerName,a.strOfficeBuildingName,a.strOfficeStreetName"
                                + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
                                + " from tblcustomermaster a "
                                + " where a.strCustomerCode='"+customerCode+"'";
                    }
                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_CustomerDtl);
		               
	                List rs_CustomerDtl =query.list();
		           String custName="",custAddress="",officeStreetName="",officeLandmark="",officeArea="",officeCity="",officePinCode="",mobileNo="";
		           if (rs_CustomerDtl!=null)
		             {
		            	 jObjList = new JSONObject();
		             	for(int i=0; i<rs_CustomerDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) rs_CustomerDtl.get(i);
		    		
		    		
		           		custName = (String) Array.get(obj,0);
		           		custAddress=(String)Array.get(obj,1);
		           		officeStreetName=(String)Array.get(obj, 2);
		           		officeLandmark=(String)Array.get(obj, 3);
		           		officeArea=(String)Array.get(obj, 4);
		           		officeCity=(String)Array.get(obj, 5);
		           		officePinCode=(String)Array.get(obj, 6);
		           		mobileNo=(String)Array.get(obj, 7);
		    			}
		             	jObj1.put("NAME", custName);
//		             	jObjList = new JSONObject();
	                   jObjList.put("NAME",custName.toUpperCase());
	                   // listOfHomeDeliveryDtl.put(jObjList);
	                    // Building Name 
	                    String fulAddress = "";
	                    String fulAddress1 = "";
	                    String fullAddress = "";
	                    String add = custAddress;
	                    int strlen = add.length();
	                    String add1 = "";
                        if (strlen < 28)
                        {
                            add1 = add.substring(0, strlen);
                            if (!add1.isEmpty())
                            {
                                if (fullAddress.isEmpty())
                                {
                                    fullAddress += " " + add1;
                                }
                                else
                                {
                                    fullAddress += "," + " " + add1;
                                }
                            }
                            fulAddress += "ADDRESS    :" + add1.toUpperCase();
                        }
                        else
                        {
                            add1 = add.substring(0, 28);
                            if (!add1.isEmpty())
                            {
                                if (fullAddress.isEmpty())
                                {
                                    fullAddress += " " + add1;
                                }
                                else
                                {
                                    fullAddress += "," + " " + add1;
                                }
                            }
                            fulAddress += "ADDRESS    :" + add1.toUpperCase();
                            //objBillDtl.setStrItemName("ADDRESS    :"+add1.toUpperCase());
                            // listOfHomeDeliveryDtl.add(objBillDtl);
                        }

                        // Street Name    
                        String street = officeStreetName;
                        String street1;
                        int streetlen = street.length();
                        for (int i = 0; i <= streetlen; i++)
                        {
                            int end = 0;
                            end = i + 28;
                            if (streetlen > end)
                            {
                                street1 = street.substring(i, end);
                                if (!street1.isEmpty())
                                {
                                    if (fullAddress.isEmpty())
                                    {
                                        fullAddress += " " + street1;
                                    }
                                    else
                                    {
                                        fullAddress += "," + " " + street1;
                                    }

                                }
                                fulAddress += "Street    :" + street1.toUpperCase();
                                i = end;
                            }
                            else
                            {
                                street1 = street.substring(i, streetlen);
                                if (!street1.isEmpty())
                                {
                                    if (fullAddress.isEmpty())
                                    {
                                        fullAddress += " " + street1;
                                    }
                                    else
                                    {
                                        fullAddress += "," + " " + street1;
                                    }

                                    fulAddress += "Street    :" + street1.toUpperCase();
                                    i = streetlen + 1;
                                }
                            }
                        }
                        // Landmark Name    
                        if (officeLandmark.trim().length() > 0)
                        {
                            if (!officeLandmark.isEmpty())
                            {
                                if (fullAddress.isEmpty())
                                {
                                    fullAddress += " " + officeLandmark;
                                }
                                else
                                {
                                    fullAddress += "," + " " + officeLandmark;
                                }

                            }
                            fulAddress += "Landmark    :" + officeLandmark.toUpperCase();
                        }

                        // Area Name    
                        if (officeArea.trim().length() > 0)
                        {
                            if (!officeLandmark.isEmpty())
                            {
                                if (fullAddress.isEmpty())
                                {
                                    fullAddress += " " + officeLandmark;
                                }
                                else
                                {
                                    fullAddress += "," + " " + officeLandmark;
                                }
                            }
                        }

                        // City Name    
                        if (officeCity.trim().length() > 0)
                        {
                            if (!officeCity.isEmpty())
                            {
                                if (fullAddress.isEmpty())
                                {
                                    fullAddress += " " + officeCity;
                                }
                                else
                                {
                                    fullAddress += "," + " " + officeCity;
                                }
                            }
                            fulAddress1 += "City    :" + officeCity.toUpperCase();
                        }

                        // Pin Code    
                        if (officePinCode.trim().length() > 0)
                        {
                            if (!officePinCode.isEmpty())
                            {
                                if (fullAddress.isEmpty())
                                {
                                    fullAddress += " " + officePinCode;
                                }
                                else
                                {
                                    fullAddress += "," + " " + officePinCode;
                                }
                            }
                            fulAddress1 += "Pin    :" + officePinCode.toUpperCase();
                        }
                        jObjList.put("Address",fullAddress);

                        jObj1.put("FullAddress", fullAddress);
                        if (mobileNo.isEmpty())
	                    {
	                    	jObj1.put("MOBILE_NO", "");
//	                    	jObjList = new JSONObject();
	                    	jObjList.put("MOBILE_NO"," ");
	                        //listOfHomeDeliveryDtl.put(jObjList);
	                    }
                        else
	                    {
	                    	jObj1.put("MOBILE_NO", mobileNo);
//	                    	jObjList = new JSONObject();
	                    	jObjList.put("MOBILE_NO",mobileNo);
	                        //listOfHomeDeliveryDtl.put(jObjList);
	                    }
	                    listOfHomeDeliveryDtl.put(jObjList);
                    }

                    if (null != officeStreetName  && officeStreetName.trim().length() > 0)
                    {
                        String[] delBoys = officeStreetName.split(",");
                        StringBuilder strIN = new StringBuilder("(");
                        for (int i = 0; i < delBoys.length; i++)
                        {
                            if (i == 0)
                            {
                                strIN.append("'" + delBoys[i] + "'");
                            }
                            else
                            {
                                strIN.append(",'" + delBoys[i] + "'");
                            }
                        }
                        
                        strIN.append(")");
                        String SQL_DeliveryBoyDtl = "select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ;";
                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_DeliveryBoyDtl);
	                    List rs_DeliveryBoyDtl = query.list();
	                    String dpName="";
	                   if (rs_DeliveryBoyDtl!=null)
	                   {
	                	   strIN.setLength(0);
                        for (int i = 0; i<rs_DeliveryBoyDtl.size(); i++)
                        {
                        	Object[] obj = (Object[]) rs_DeliveryBoyDtl.get(i);
        	        		
        	        		
	                    	dpName = (String) Array.get(obj,2);
                        	if (i == 0)
                            {
                                strIN.append(dpName.toUpperCase());
                            }
                            else
                            {
                                strIN.append("," + dpName.toUpperCase());
                            }
                        }
	                   }
                        if (strIN.toString().isEmpty())
                        {
                        	jObj1.put("DELV BOY", "");
                        }
                        else
                        {
                        	jObj1.put("DELV BOY", "Delivery Boy : " + strIN);
	                    	//jObjList = new JSONObject();
	                    	jObjList.put("DELV BOY",strIN);
	                        listOfHomeDeliveryDtl.put(jObjList);
                        }
                       
                    }
                    else
                    {
                    	jObj1.put("DELV BOY", "");
                    }
                }
                }
                    int result = funPrintTakeAwayForJasper(billhd, billNo);
    	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress1");
    	            String clientAddress1 = objSetupParameter.get("gClientAddress1").toString();
    	             
    	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress2");
    	            String clientAddress2 = objSetupParameter.get("gClientAddress2").toString();
    	             
    	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientAddress3");
    	            String clientAddress3 = objSetupParameter.get("gClientAddress3").toString();
    	             
    	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gCityName");		    
    	            String cityName = objSetupParameter.get("gCityName").toString();
    	            
    	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientName");		    
    	            String clientName = objSetupParameter.get("gClientName").toString();
    	            
    	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientTelNo");		    
    	            String clientTelNo = objSetupParameter.get("gClientTelNo").toString();
    	              
    	            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gClientEmail");		    
    	            String clientEmail = objSetupParameter.get("gClientEmail").toString();
    	            
                if (result == 1)
                {
                    billType = "Take Away";
                }
                objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTaxInvoice");
 			    
 			    if(objSetupParameter.get("gPrintTaxInvoice").toString().equalsIgnoreCase("Y"))
 			    {
 			    	jObj1.put("TAX_INVOICE", "TAX INVOICE");
                }
                if (clientCode.equals("047.001") && POSCode.equals("P03"))
                {
                	jObj1.put("ClientName", "SHRI SHAM CATERERS");
                    String cAddr1 = "Flat No.7, Mon Amour,";
                    String cAddr2 = "Thorat Colony,Prabhat Road,";
                    String cAddr3 = " Erandwane, Pune 411 004.";
                    String cAddr4 = "Approved Caterers of";
                    String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
                    jObj1.put("ClientAddress1", cAddr1 + cAddr2);
                    jObj1.put("ClientAddress2", cAddr3 + cAddr4);
                    jObj1.put("ClientAddress3", cAddr5);
                }
                else if (clientCode.equals("047.001") && POSCode.equals("P02"))
                {
                	jObj1.put("ClientName", "SHRI SHAM CATERERS");
                    String cAddr1 = "Flat No.7, Mon Amour,";
                    String cAddr2 = "Thorat Colony,Prabhat Road,";
                    String cAddr3 = " Erandwane, Pune 411 004.";
                    String cAddr4 = "Approved Caterers of";
                    String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
                    jObj1.put("ClientAddress1", cAddr1 + cAddr2);
                    jObj1.put("ClientAddress2", cAddr3 + cAddr4);
                    jObj1.put("ClientAddress3", cAddr5);
                }
                else if (clientCode.equals("092.001") ||clientCode.equals("092.002") || clientCode.equals("092.003"))//Shree Sound Pvt. Ltd.(Waters)
                {
                	jObj1.put("ClientName", "SSPL");
                	jObj1.put("ClientAddress1", clientAddress1);
                	jObj1.put("ClientAddress2", clientAddress2);
                	jObj1.put("ClientAddress3", clientAddress3);

                    if (cityName.trim().length() > 0)
                    {
                    	jObj1.put("ClientCity", cityName);
                    }
                }
                else
                {
                	jObj1.put("ClientName", clientName);
                	jObj1.put("ClientAddress1", clientAddress1);
                	jObj1.put("ClientAddress2", clientAddress2);
                	jObj1.put("ClientAddress3", clientAddress3);

                    if (cityName.trim().length() > 0)
                    {
                    	jObj1.put("ClientCity", cityName);
                    }
                }

                jObj1.put("TEL NO", String.valueOf(clientTelNo));
                jObj1.put("EMAIL ID", clientEmail);
                jObj1.put("Line", Linefor5);

               
                String SQL_BillHD = "",billDt="",qty="";
	            String waiterName = "";
	            Date dt = null; 
	            double quantity=0;
	            String waiterNo = "";
	            String tblName = "";
	            String advDeposite="";
	            String sqlTblName = "";
	            String tabNo = "";
	            String posName="";
	            int paxNo = 0;
	            boolean flag_DirectBillerBlill = false;
	            boolean flgComplimentaryBill = false;
	            List listBillHD=query.list();
	            String sql = "select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
	                    + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' ";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            List listSettlementType=query.list();
	            if (listSettlementType!=null)
	            {
	            	if(listSettlementType.size()!=0)
	            	{	
	                flgComplimentaryBill = true;
	            	}
	            }	
              

                if (funIsDirectBillerBill(billNo, billhd))
                {
                    flag_DirectBillerBlill = true;
                    SQL_BillHD = "select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                            + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                            + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName "
                            + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
                            + "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
                            + "where a.strBillNo='"+billNo+"' ";
                    flag_DirectBiller = true;
                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillHD);
	                 listBillHD=query.list();
	               
	                if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		 dt = (Date) Array.get(obj,0);
//		           		tabNo=(String) Array.get(obj,0);
		    			}
	                }	
                }     
                else
                {
                    SQL_BillHD = "select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                            + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                            + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo "
                            + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                            + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
                            + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
                            + "where a.strBillNo='"+billNo+"' and b.strOperational='Y' ";
                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillHD);
	                 listBillHD=query.list();
	               
	                if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		    		
		    		
		           		tabNo = (String) Array.get(obj,0);
		           		waiterNo=(String)Array.get(obj,1);
		           		 dt=(Date)Array.get(obj,2);
		           		
		           		subTotal = (BigDecimal) Array.get(obj,5);
		           		grandTot = (BigDecimal) Array.get(obj,7);
		                user = (String) Array.get(obj,11);
		                deliveryCharge = (BigDecimal) Array.get(obj,12);
		                advAmount = (BigDecimal) Array.get(obj,13);
//		                advDeposite=(String) Array.get(obj,13);
//		                posName=(String) Array.get(obj,15);
//		                String paxNumber=(String) Array.get(obj,16);
//		                paxNo=Integer.parseInt(paxNumber);
		    			}
	                  
	                    if (waiterNo.equalsIgnoreCase("null") || waiterNo.equalsIgnoreCase(""))
	                    {
	                        waiterNo = "";
	                    }
	                    else
	                    {
	                        
	                        sql = "select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"'";
	                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		                       List listQuery=query.list();
		                       if (listQuery!=null)
		   	                   {
		   	                	for(int i=0; i<listQuery.size(); i++)
		   		    			{
		   	                		waiterName = (String) listQuery.get(0);
//		   	   		           	waiterName = (String) Array.get(obj,0);           		
		   		    			}
		                        
		                        }
		                     }
		            }

                    sqlTblName = "select strTableName from tbltablemaster where strTableNo='"+tabNo+"'";
                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTblName);
	                List listTblName=query.list();
	              
	                if (listTblName!=null)
	                {
	                	for(int i=0; i<listTblName.size(); i++)
   		    			{
	                		tblName = (String) listTblName.get(0);
   		           		 		
   		    			}
	                    
	                }
	            }

                objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintTimeOnBillYN");
 			    
 			    String printTimeOnBillYN=(String)objSetupParameter.get("gPrintTimeOnBillYN");
	           
                // funPrintTakeAway(billhd, billNo, BillOut);
//                List<clsBillDtl> listSubTotal = new ArrayList<>();
 			    JSONArray listSubTotal = new JSONArray();
                if (flag_DirectBillerBlill)
                {

	            	if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		
		           		subTotal = (BigDecimal)Array.get(obj,3);
		           		grandTot = (BigDecimal)Array.get(obj,5);
		           		grandTotal=grandTot.doubleValue();
		                user = (String)Array.get(obj,9);
		                deliveryCharge =(BigDecimal)Array.get(obj,10);
		                advAmount = (BigDecimal)Array.get(obj,11);
		                posName = (String)Array.get(obj,13);
//		           		tabNo=(String) Array.get(obj,0);
		    			}
	                }
	            	jObj1.put("POS", posName);
	            	jObj1.put("BillNo", billNo);

                    if (printTimeOnBillYN.equals("Y"))
                    {
                    	 SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
 	                    tabNo = ft.format(dt);
 	                    jObj1.put("DATE_TIME",tabNo);
                    }
                    else
                    {
                    	 SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
 	                    tabNo = ft.format(dt);
 	                    jObj1.put("DATE_TIME",tabNo);
                    }
                     jObjList = new JSONObject();
                    jObjList.put("subTotal",subTotal);
                    listSubTotal.put(jObjList);
                  
                    
                }
                else
                {
                	jObj1.put("TABLE NAME", tblName);

                    if (waiterName.trim().length() > 0)
                    {
                    	jObj1.put("waiterName", waiterName);
                    }
                    jObj1.put("POS", posName);
                    jObj1.put("BillNo", billNo);
                    jObj1.put("PaxNo", paxNo);

                    if (printTimeOnBillYN.equals("Y"))
                    {
                    	SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                    	billDt = ft.format(dt);
	                    jObj1.put("DATE_TIME", billDt);
                    }
                    else
                    {
                    	SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                    	billDt = ft.format(dt);
	                    jObj1.put("DATE_TIME", billDt);
                    }

                    if (listBillHD!=null)
	                {
	                	for(int i=0; i<listBillHD.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillHD.get(i);
		           		
	                subTotal = (BigDecimal)Array.get(obj,5);
	                grandTot = (BigDecimal)Array.get(obj,7);
	                grandTotal=grandTot.doubleValue();
	                user = (String)Array.get(obj,11);
	                deliveryCharge = (BigDecimal)Array.get(obj,12);
	                advAmount = (BigDecimal)Array.get(obj,13);
		    			}
	                } 
                    jObjList = new JSONObject();
                    jObjList.put("subTotal",subTotal);
                    listSubTotal.put(jObjList);
                    
	            }

//                List<clsBillDtl> listOfFoodBillDetail = new ArrayList<>();
                JSONArray listOfFoodBillDetail = new JSONArray();
//                List<clsBillDtl> listOfLiqourBillDetail = new ArrayList<>();
                JSONArray listOfLiqourBillDetail = new JSONArray();
//                List<clsBillDtl> listGeneralBillDtl = new ArrayList<>();
                JSONArray listGeneralBillDtl = new JSONArray();
//                String SQL_BillDtl = "select sum(a.dblQuantity),a.strItemName as ItemLine1"
//                        + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) as ItemLine2"
//                        + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,b.strItemType  "
//                        + " from " + billdtl + " a,tblitemmaster b  "
//                        + " where a.strBillNo=? "
//                        + " and a.strItemCode=b.strItemCode "
//                        + " and a.tdhYN='N' "
//                        + " group by a.strItemCode ;";
                String SQL_BillDtl="select sum(a.dblQuantity),a.strItemName "
	            		 + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,b.strItemType"
		                 + " from " + billdtl + " a,tblitemmaster b  "
		                 + " where a.strBillNo='"+billNo+"'"
		                 + " and a.strItemCode=b.strItemCode "
            			 + " and a.tdhYN='N' "
		                 + " group by a.strItemCode";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_BillDtl);
	            List listBillDtl = query.list();
	          
	            BigDecimal saleQuantity = null;
				BigDecimal amount=null;
				double saleQty=0.00;
	            String itemCode="",itemName="",itemType="";

                if (listBillDtl!=null)
	            {
                	
//                	JSONObject jObjList=new JSONObject();
                	for(int i=0; i<listBillDtl.size(); i++)
		    			{
		           		Object[] obj = (Object[]) listBillDtl.get(i);
		           		saleQuantity = (BigDecimal) Array.get(obj,0);
		           		saleQty = saleQuantity.doubleValue();
		           		itemName = (String) Array.get(obj,1);
		           		amount = (BigDecimal) Array.get(obj,2); 
		           		itemCode = (String) Array.get(obj,3); 
		           		itemType= (String) Array.get(obj,5); 
		                if (itemType.equalsIgnoreCase("Food"))
	                    {
	                        listGeneralBillDtl = listOfFoodBillDetail;
	                    }
	                    else
	                    {
	                        listGeneralBillDtl = listOfLiqourBillDetail;
	                    }
		    		
                	
                	
              

                    String sqlPromoBills = "select dblQuantity from " + billPromoDtl + " "
                            + " where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlPromoBills);
	                List listPromoItems=query.list();
	                
	                if (listPromoItems!=null)
	                {
	                	for(int cnt=0; cnt<listPromoItems.size(); cnt++)
			    			{
			           			Object[] objPromoItems = (Object[]) listPromoItems.get(cnt);
			           			quantity = (double) Array.get(objPromoItems,0);
			           			saleQty -= quantity;
			    			}
	                }
	                qty = String.valueOf(saleQty);
	                if (qty.contains("."))
	                {
	                    String decVal = qty.substring(qty.length() - 2, qty.length());
	                    if (Double.parseDouble(decVal) == 0)
	                    {
	                        qty = qty.substring(0, qty.length() - 2);
	                    }
	                }
	               double dblAmount=0.00,rate=0.00,discountAmt=0.00;
	               BigDecimal bd=null,bd1=null,bd2=null;
                    if (saleQty > 0)
                    {
                    	
//                    	if (listBillDtl!=null)
//	                    {
//	                    	
//	                    	for(int j=0; j<listBillDtl.size(); j++)
//	    		    			{
//	    		           		Object[] objBillDtl = (Object[]) listBillDtl.get(i);
	    		           		itemName = (String) Array.get(obj,1);
	    		           		bd=(BigDecimal) Array.get(obj, 2);
	    		           				    		           		
	    		           		dblAmount = bd.doubleValue(); // The double you want
	    		           		
	    		           		jObjList = new JSONObject();
	    		           		jObjList.put("saleQty",saleQty);
	    		           		jObjList.put("dblAmount",dblAmount);
	    		           		jObjList.put("itemName",itemName);
	    		           		
	    		           		listGeneralBillDtl.put(jObjList);

//	    		    			}
//	                    }	
//                       

	                	 objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintZeroAmtModifierOnBill");
		     			    
		     		    String printZeroAmtModifierOnBill=(String)objSetupParameter.get("gPrintZeroAmtModifierOnBill");
		              
		     		   String sqlModifier = "select count(*) "
	                            + "from " + billModifierdtl + " where strBillNo='"+billNo+"' and left(strItemCode,7)='"+itemCode+"' ";
	                    if (printZeroAmtModifierOnBill.equals("N"))
	                    {
	                        sqlModifier += " and  dblAmount !=0.00 ";
	                    }
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifier);
		                   List listCount=query.list();
		                   String countntRec="";
		                   int cntRecord = 0;
		                   if(listCount!=null)
		                   {
		                	   for(int a=0; a<listPromoItems.size(); a++)
				    			{
		                	   Object[] objPromoItems = (Object[]) listPromoItems.get(a);
		                	   countntRec = (String) Array.get(objPromoItems,0);   
		                	    cntRecord = Integer.parseInt(countntRec);
				    			}
		                   }
	                    
                        if (cntRecord > 0)
                        {
                            sqlModifier = "select strModifierName,dblQuantity,dblAmount "
                                    + " from " + billModifierdtl + " "
                                    + " where strBillNo=? and left(strItemCode,7)=? ";
                            if (printZeroAmtModifierOnBill.equals("N"))
	                        {
	                            sqlModifier += " and  dblAmount !=0.00 ";
	                        }
                            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifier);
		                       List listmodifierRecord=query.list();
		                       String modifierName="";
		                       double amt=0;
		                       
		                       if(listmodifierRecord!=null)
		                        {
		                    	   for(int b=0; b<listmodifierRecord.size(); b++)
					    			{
			                	   Object[] objmodifierRecord = (Object[]) listmodifierRecord.get(b);
			                	   modifierName= (String) Array.get(objmodifierRecord,0);  
			                	   quantity = (double) Array.get(objmodifierRecord,1); 
			                	   amt=(double) Array.get(objmodifierRecord,2); 
			                	   }
		                          
	                            if (flgComplimentaryBill)
	                            {
	                            	jObjList = new JSONObject();
	                                jObjList.put("quantity",quantity);
	                                jObjList.put("amt",0);
	                                jObjList.put("modifierName",modifierName.toUpperCase());
	                                
	                                listGeneralBillDtl.put(jObjList);
	                            }
	                            else
	                            {
	                            	jObjList = new JSONObject();
	                            	 jObjList.put("quantity",quantity);
	                            	 jObjList.put("amt",amt);
	                            	 jObjList.put("modifierName",modifierName.toUpperCase());
	                            	
	                            	 listGeneralBillDtl.put(jObjList);
	                            }
	                        }
	                       
	                    }
	                }
	        }
            }
                funPrintPromoItemsInBill(billNo, 4, listGeneralBillDtl);  // Print Promotion Items in Bill for this billno.

//                List<clsBillDtl> listOfDiscountDtl = new ArrayList<>();
                JSONArray listOfDiscountDtl = new JSONArray();
                sql = "select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
                        + "from " + billDscFrom + " a ,tblreasonmaster b "
                        + "where  a.strDiscReasonCode=b.strReasonCode "
                        + "and a.strBillNo='" + billNo + "' ";
                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            List listDisc=query.list();

	            boolean flag = true;
	            double dbl=0,discAmt=0;
	            String discAmount="";
	            String discOnValue="",reasonName="",discRemarks="";
	           if(listDisc!=null)
	            {
	        	   jObjList = new JSONObject();
	        	   for(int i=0; i<listDisc.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listDisc.get(i);
	        		   dbl= (double) Array.get(obj,0);
	        		   discAmt= (double) Array.get(obj,1);
	        		   discAmount= (String)Array.get(obj,2);
	        		   discOnValue= (String) Array.get(obj,3);
	        		   reasonName= (String) Array.get(obj,4);
	        		   discRemarks= (String) Array.get(obj,5);
	    			}  
	          
	                if (flag)
	                {
	                	  
	                	jObjList = new JSONObject();
	                	jObjList.put("Discount","Discount");
	                    //listOfDiscountDtl.put(jObjList);
	                    flag = false;
	                }
	                
	                String discText = String.format("%.1f", dbl) + "%" + " On " + discOnValue + "";
	                if (discText.length() > 30)
	                {
	                    discText = discText.substring(0, 30);
	                }
	                else
	                {
	                    discText = String.format("%-30s", discText);
	                }

	                String discountOnItem = funPrintTextWithAlignment(discAmount, 8, "Right");
	                jObj1.put("Discount", discText + " " + discountOnItem);
	                jObjList.put("discText",discText);
	                //jObjList = new JSONObject();
	                jObjList.put("discAmt",discAmt);
	               // listOfDiscountDtl.put(jObjList);

	                //jObjList = new JSONObject();
	                jObjList.put("Reason",reasonName);
	                //listOfDiscountDtl.put(jObjList);

	                //jObjList = new JSONObject();
	                jObjList.put("Remark",discRemarks);
	                listOfDiscountDtl.put(jObjList);
	            }

//                List<clsBillDtl> listOfTaxDetail = new ArrayList<>();
                JSONArray listOfTaxDetail = new JSONArray(); 
                String sql_Tax = "select b.strTaxDesc,sum(a.dblTaxAmount) "
                        + " from " + billtaxdtl + " a,tbltaxhd b "
                        + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
                        + " group by a.strTaxCode";
                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Tax);
	            List listTax=query.list();
	            String taxDesc="";
	            BigDecimal taxAmt=null;
	            double taxAmount=0;
	            
	            if(listTax!=null)
	            {
	            	for(int i=0; i<listTax.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listTax.get(i);
	        		   taxDesc= (String) Array.get(obj,0);
	        		   taxAmt= (BigDecimal) Array.get(obj,1);
	        		   taxAmount = taxAmt.doubleValue();
	    			
	            	
	                if (flgComplimentaryBill)
	                {
	                	jObjList = new JSONObject();
	                	jObjList.put("taxAmount",0);
	                    jObjList.put("taxDesc",taxDesc);
	                    listOfTaxDetail.put(jObjList);
	                }
	                else
	                {
	                	jObjList = new JSONObject();
	                	jObjList.put("taxAmount",taxAmount);
	                    jObjList.put("taxDesc",taxDesc);
	                    listOfTaxDetail.put(jObjList);
	                }
	    			}
	            }
//	            List<clsBillDtl> listOfGrandTotalDtl = new ArrayList<>();
	            JSONArray listOfGrandTotalDtl = new JSONArray();
	            if (grandTotal > 0)
	            {
	            	jObjList = new JSONObject();
	            	jObjList.put("grandTotal",grandTotal);
	                listOfGrandTotalDtl.put(jObjList);
	            }

	            JSONArray listOfSettlementDetail = new JSONArray();
	            //settlement breakup part
	            String sqlSettlementBreakup = "select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
	                    + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
	                    + "where a.strBillNo='"+billNo+"' and a.strSettlementCode=b.strSettelmentCode";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlSettlementBreakup);
	            List listBill_Settlement=query.list();
	           double settleAmt=0;
	           String settleDesc="";
	           
	           if(listBill_Settlement!=null)
	            {
	        	  jObjList=new JSONObject();
	        	   for(int i=0; i<listBill_Settlement.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listBill_Settlement.get(i);
	        		   settleAmt= (double) Array.get(obj,0);
	        		   settleDesc= (String) Array.get(obj,1);
	        		  
	    			 
	        	   
	                if (flgComplimentaryBill)
	                {
	                	
	                    jObjList.put("settleDesc",settleDesc);
	                   jObjList.put("settleAmt",0.00);
	                   // listOfSettlementDetail.put(jObjList);
	                }
	                else
	                {
//	                    objBillDtl = new clsBillDtl();
	                	//jObjList = new JSONObject();
	                   jObjList.put("settleDesc",settleDesc);
	                   jObjList.put("settleAmt",settleAmt);
	                   // listOfSettlementDetail.put(jObjList);
	                }
	                listOfSettlementDetail.put(jObjList);
	    			} 
	        	   
	            }
                String sqlTenderAmt = "select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
                        + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
                        + " group by strBillNo";
                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTenderAmt);
	            List listTenderAmt=query.list();
	            double paidAmt=0,refundAmt=0;
	            
	            if (listTenderAmt!=null)
	            {  
	            	//jObjList = new JSONObject();
	            	for(int i=0; i<listTenderAmt.size(); i++)
	    			{
	        		   Object[] obj = (Object[]) listTenderAmt.get(i);
	        		   paidAmt= (double) Array.get(obj,0);
	        		   refundAmt= (double) Array.get(obj,2);
	        		  
	    			
	            	if (flgComplimentaryBill)
	                {
	            		//jObjList = new JSONObject();
	            		jObjList.put("PAID AMT","PAID AMT");
	                   jObjList.put("paidAmt",0.00);
	                    //listOfSettlementDetail.put(jObjList);
	                }
	                else
	                {
	                	//jObjList = new JSONObject();
	                    jObjList.put("PAID AMT","PAID AMT");
	                    jObjList.put("paidAmt",paidAmt);
	                    //listOfSettlementDetail.put(jObjList);
	                    if (refundAmt > 0)
	                    {
	                    	//jObjList = new JSONObject();
	                        jObjList.put("REFUND AMT","REFUND AMT");
	                        jObjList.put("refundAmt",refundAmt);
	                       // listOfSettlementDetail.put(jObjList);
	                    }
	                }
	            	listOfSettlementDetail.put(jObjList);
	    			}
	            	
	            }
                if (flag_isHomeDelvBill)
                {
                	String sql_count = "select count(*) from tblhomedelivery where strCustomerCode='"+customerCode+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_count);
	                List listCount=query.list();
	                long count = 0;
	                if (listCount!=null)
		            {
		            	for(int i=0; i<listCount.size(); i++)
		    			{
		        		   //Object[] obj = (Object[]) listCount.get(i);
//		        		   String cnt= (String) Array.get(obj,0);
//		        		   
		            		count=((BigInteger)listCount.get(0)).longValue();
		        		  
		    			}  
		            }
	                jObj1.put("CUSTOMER_COUNT", count);
	            }

                JSONArray listOfServiceVatDetail = funPrintServiceVatNoForJasper(clientCode,POSCode,PrintVatNoPOS,vatNo,printServiceTaxNo,serviceTaxNo);
//	            List<clsBillDtl> listOfFooterDtl = new ArrayList<>();
	            JSONArray listOfFooterDtl = new JSONArray();
	            jObjList = new JSONObject();
	            jObjList.put("Thank","THANK YOU AND VISIT AGAIN !!!");
	            listOfFooterDtl.put(jObjList);

	            jObj1.put("BillType", billType);
	            jObj1.put("listOfFoodBillDetail", listOfFoodBillDetail);
	            jObj1.put("listOfLiqourBillDetail", listOfLiqourBillDetail);
	            jObj1.put("listOfTaxDtl", listOfTaxDetail);
	            jObj1.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
	            jObj1.put("listOfServiceVatDetail", listOfServiceVatDetail);
	            jObj1.put("listOfFooterDtl", listOfFooterDtl);
	            jObj1.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
	            jObj1.put("listOfDiscountDtl", listOfDiscountDtl);
	            jObj1.put("listOfSettlementDetail", listOfSettlementDetail);
	            jObj1.put("listSubTotal", listSubTotal);
                jArr.put(jObj1);

                json.put("listOfFoodBillDetail", listOfFoodBillDetail);
	            json.put("format", "Jasper3");
	            json.put("result", result);
	            json.put("jArr", jArr);
	            json.put("flag_DirectBillerBlill", flag_DirectBillerBlill);
            }  
            catch (Exception e)
            {
                e.printStackTrace();
            }
			return json;
        }  



public JSONObject funGenerateTextDayEndReportPreview(String posCode, String billDate, String reprint,String clientCode,String gDayEndReportForm,String webStockUserCode)
	    {
		 	JSONObject jObjRet=new JSONObject();
	        
	        JSONArray jArr =new JSONArray();
		 try
		 {
		   JSONObject jObj1 = new JSONObject();
		   if ("reprint".equalsIgnoreCase(reprint))
	       {
		      jObj1.put("duplicate", "[DUPLICATE]");
		   }
		   jObj1.put("reportHeading","DAY END REPORT");
		   
		   String sqlDayEnd = "select  a.strPOSCode,b.strPosName,date(a.dtePOSDate),time(a.dteDayEndDateTime),a.dblTotalSale,\n"
                   + "a.dblFloat,a.dblCash,a.dblAdvance,  a.dblTransferIn,a.dblTotalReceipt,a.dblPayments,\n"
                   + "a.dblWithDrawal,a.dblTransferOut,a.dblTotalPay,  a.dblCashInHand,a.dblHDAmt,\n"
                   + "a.dblDiningAmt,a.dblTakeAway,a.dblNoOfBill,a.dblNoOfVoidedBill,\n"
                   + "a.dblNoOfModifyBill,a.dblRefund  ,a.dblTotalDiscount,\n"
                   + "a.intTotalPax,a.intNoOfTakeAway,a.intNoOfHomeDelivery,\n"
                   + "a.strUserCreated,a.strUserEdited, a.intNoOfNCKOT,a.intNoOfComplimentaryKOT,a.intNoOfVoidKOT from tbldayendprocess a ,  "
                   + " tblposmaster b where b.strPosCode=a.strPosCode "
                   + " and a.strPOSCode='"+posCode+"' and date(a.dtePOSDate)='"+billDate+"'";

           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDayEnd);
           List listDayend =query.list();
         String posCode1,posDate,posName,dayEndBy;
         BigDecimal homeDelivery;
         BigDecimal dining,takeAway,totalSales;
         BigDecimal discount,floatval,cash,advance,transferIn,totalReceipt;
         BigDecimal payment,withdrawal,transferOut,refund,totalPayments;
         BigDecimal cashInHand;
         BigDecimal noOfBills,noOfVoidedBills,noOfModifiedBills;
         int noOfPax;
		int noOfHomeDel;
		int noOfNcKot;
		int noOfTakeAway;
         int noOfComplimentaryBills;
		int noOfVoidKot;
         SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
         SimpleDateFormat ft1 = new SimpleDateFormat("hh:mm a ");
            if (listDayend!=null)
            {
               	for(int i=0; i<listDayend.size(); i++)
            	{
          		Object[] obj = (Object[]) listDayend.get(i);
          		posCode1 = (String) Array.get(obj,0);
          		jObj1.put("posCode1", posCode1);
          		
          		posName = (String) Array.get(obj,1);
          		jObj1.put("posName", posName);
          		
          		Date dt = (Date) Array.get(obj,2);
          		posDate = ft.format(dt);
           		jObj1.put("posDate", posDate);
           		
           		totalSales = (BigDecimal) Array.get(obj,4);
          		jObj1.put("totalSales", totalSales);
           		
          		floatval = (BigDecimal) Array.get(obj,5);
          		jObj1.put("floatval", floatval);
          		
          		cash = (BigDecimal) Array.get(obj,6);
          		jObj1.put("cash", cash);
          		
          		advance = (BigDecimal) Array.get(obj,7);
          		jObj1.put("advance", advance);


          		transferIn = (BigDecimal) Array.get(obj,8);
          		jObj1.put("transferIn", transferIn);

          		totalReceipt = (BigDecimal) Array.get(obj,9);
          		jObj1.put("totalReceipt", totalReceipt);

          		payment = (BigDecimal) Array.get(obj,10);
          		jObj1.put("payment", payment);

          		withdrawal = (BigDecimal) Array.get(obj,11);
          		jObj1.put("withdrawal", withdrawal);

          		transferOut = (BigDecimal) Array.get(obj,12);
          		jObj1.put("transferOut", transferOut);

          		totalPayments = (BigDecimal) Array.get(obj,13);
          		jObj1.put("totalPayments", totalPayments);
          		
          		cashInHand = (BigDecimal) Array.get(obj,14);
          		jObj1.put("cashInHand", cashInHand);
          		
          		homeDelivery = (BigDecimal) Array.get(obj,15);
          		jObj1.put("homeDelivery", homeDelivery);
          		
          		dining = (BigDecimal) Array.get(obj,16);
          		jObj1.put("dining", dining);
          		
          		takeAway = (BigDecimal) Array.get(obj,17);
          		jObj1.put("takeAway", takeAway);
          		
          		noOfBills = (BigDecimal) Array.get(obj,18);
          		jObj1.put("noOfBills", noOfBills);
          		
          		noOfVoidedBills = (BigDecimal) Array.get(obj,19);
          		jObj1.put("noOfVoidedBills", noOfVoidedBills);
          		
          		noOfModifiedBills = (BigDecimal) Array.get(obj,20);
          		jObj1.put("noOfModifiedBills", noOfModifiedBills);

          		refund = (BigDecimal) Array.get(obj,21);
          		jObj1.put("refund", refund);
          		
          		discount = (BigDecimal) Array.get(obj,22);
          		jObj1.put("discount", discount);
          		

          		noOfPax = (int) Array.get(obj,23);
          		jObj1.put("noOfPax", noOfPax);

          		noOfTakeAway = (int) Array.get(obj,24);
          		jObj1.put("noOfTakeAway", noOfTakeAway);
          		
          		noOfHomeDel = (int) Array.get(obj,25);
          		jObj1.put("noOfHomeDel", noOfHomeDel);
          		
          		dayEndBy =(String) Array.get(obj,27);
           		jObj1.put("dayEndBy", dayEndBy);
          		
          		noOfNcKot = (int) Array.get(obj,28);
          		jObj1.put("noOfNcKot", noOfNcKot);
          		
          		noOfComplimentaryBills = (int) Array.get(obj,29);
          		jObj1.put("noOfComplimentaryBills", noOfComplimentaryBills);
          		
          		noOfVoidKot = (int) Array.get(obj,30);
          		jObj1.put("noOfVoidKot", noOfVoidKot);
          		
          		
            	}
            }
            jObj1.put("BILLING SETTLEMENT BREAK UP","BILLING SETTLEMENT BREAK UP");
            String sql_SettelementBrkUP = "";
            JSONArray listSettelementBrkUP = new JSONArray();
			 if(gDayEndReportForm.equalsIgnoreCase("DayEndReport"))
             {
                sql_SettelementBrkUP = "select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
                        + " from  tblbillhd a, tblbillsettlementdtl b, tblsettelmenthd c  "
                        + " where a.strBillNo = b.strBillNo"
                        + " and b.strSettlementCode = c.strSettelmentCode and a.strPOSCode='"+posCode+"' "
                        + " and date(a.dteBillDate)='"+billDate+"' "
                        + " GROUP BY c.strSettelmentDesc;";
            }
            else
            {
                sql_SettelementBrkUP = "select c.strSettelmentDesc, SUM(b.dblSettlementAmt) "
                        + " from  tblqbillhd a, tblqbillsettlementdtl b, tblsettelmenthd c  "
                        + " where a.strBillNo = b.strBillNo"
                        + " and b.strSettlementCode = c.strSettelmentCode and a.strPOSCode='"+posCode+"' "
                        + " and date(a.dteBillDate) ='"+billDate+"' "
                        + " GROUP BY c.strSettelmentDesc;";
            }
            
			  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SettelementBrkUP);
	         List listSettelementBrkUp =query.list();
	         String settlementDesc="";
	         BigDecimal settlementAmount=null;
	         double totalAmt=0.00,settlementAmt=0.00;
	         if (listSettelementBrkUp!=null)
	         {
	        	
	               	for(int i=0; i<listSettelementBrkUp.size(); i++)
	            	{
	               	 JSONObject jObjList=new JSONObject();
	          		Object[] obj = (Object[]) listSettelementBrkUp.get(i);
	          		settlementDesc = (String) Array.get(obj,0);
	          		settlementAmount = (BigDecimal) Array.get(obj,1);
	          		settlementAmt = settlementAmount.doubleValue();
	          		totalAmt += settlementAmt;
	          		jObjList.put("settlementDesc", settlementDesc);
                    jObjList.put("settlementAmt", settlementAmt);
                	listSettelementBrkUP.put(jObjList);
	            	}
	               	jObj1.put("Total", totalAmt);
	               
	         }  
	         
	         
	         String billHd = "tblbillhd", billTaxDtl = "tblbilltaxdtl";
	         JSONArray listSettelementTaxDtl = new JSONArray();
             if (!gDayEndReportForm.equals("DayEndReport"))
             {
                 billHd = "tblqbillhd";
                 billTaxDtl = "tblqbilltaxdtl";
             }
             String sql_SettelementTax = "select b.strTaxDesc, sum(a.dblTaxableAmount), sum(a.dblTaxAmount) "
                     + "from " + billTaxDtl + " a, tbltaxhd b "
                     + "Where a.strTaxCode = b.strTaxCode and strBillNo IN "
                     + "(select strBillNo from " + billHd + " where strPOSCode = '"+posCode+"' and date(dteBillDate)= '"+billDate+"' ) "
                     + "Group By b.strTaxDesc";
            
            jObj1.put("taxDesc", "   TAX Des             Taxable   Tax Amt   ");
            String taxDesc="";
            BigDecimal taxableAmt=null,taxAmt=null;
            double taxableAmount=0.00,taxAmount=0.00;
            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SettelementTax);
	         List listSettelementTax =query.list();
	         if (listSettelementTax!=null)
	         {
	        	 
	               	for(int i=0; i<listSettelementTax.size(); i++)
	            	{
	               		JSONObject jObjList = new JSONObject();
	          		Object[] obj = (Object[]) listSettelementTax.get(i);
	          		taxDesc = (String) Array.get(obj,0);
	          		taxableAmt = (BigDecimal) Array.get(obj,1);
	          		taxableAmount = taxableAmt.doubleValue();
	          		taxAmt  = (BigDecimal) Array.get(obj,2);
	          		taxAmount = taxAmt.doubleValue();
	          		jObjList.put("taxDesc", taxDesc);
	          		jObjList.put("taxableAmount", taxableAmount);
	          		jObjList.put("taxAmount", taxAmount);
	          		listSettelementTaxDtl.put(jObjList);
	            	}
	               	
	         }
            
	       //group wise subtotal
	            String sqlBuilder="";
	            JSONArray listGroupAmtWithTaxDtl = new JSONArray();
	            Map<String, clsGroupSubGroupWiseSales> mapGroupWiseData = new HashMap<>();

	            //live group data
	            
	            sqlBuilder="SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
	                    + ", '" + webStockUserCode + "',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
	                    + ", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount) "
	                    + "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
	                    + "WHERE a.strBillNo=b.strBillNo "
	                    + "AND a.strPOSCode=f.strPOSCode "
	                    + "AND a.strClientCode=b.strClientCode "
	                    + "AND b.strItemCode=e.strItemCode "
	                    + "AND c.strGroupCode=d.strGroupCode "
	                    + "AND d.strSubGroupCode=e.strSubGroupCode "
	                    + "AND a.strPOSCode = '" + posCode + "' "
	                    + "AND DATE(a.dteBillDate)='" + billDate + "'"
	                    + "AND a.intShiftCode='1' "
	                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);
		         List listGroupData =query.list();
		         if (listGroupData!=null)
		         {
		               	for(int i=0; i<listGroupData.size(); i++)
		            	{
		          		Object[] obj = (Object[]) listGroupData.get(i);
		          		String groupCode = (String) Array.get(obj,0);
		          		String groupName = (String) Array.get(obj,1);
		          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
		          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
		          		
		          		
		          		if (mapGroupWiseData.containsKey(groupCode))
		                {
		                    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
		                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
		                }
		                else
		                {
		                    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
		                    objGroupWiseSales.setGroupCode(groupCode);
		                    objGroupWiseSales.setGroupName(groupName);
		                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);

		                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		                }
		            	}
		         }      	
		         
		       
		            sqlBuilder="SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
		                    + ",'" + webStockUserCode + "','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
		                    + "FROM tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
		                    + "WHERE a.strBillNo=b.strBillNo "
		                    + "AND a.strPOSCode=f.strPosCode "
		                    + "AND a.strClientCode=b.strClientCode  "
		                    + "AND LEFT(b.strItemCode,7)=d.strItemCode  "
		                    + "AND d.strSubGroupCode=e.strSubGroupCode "
		                    + "AND e.strGroupCode=c.strGroupCode  "
		                    + "AND b.dblamount>0 "
		                    + "AND a.strPOSCode = '" + posCode + "' "
		                    + "AND a.intShiftCode='1' "
		                    + "AND DATE(a.dteBillDate) = '" + billDate + "' "
		                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode";
		            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);
			          listGroupData =query.list();
			         if (listGroupData!=null)
			         {
			               	for(int i=0; i<listGroupData.size(); i++)
			            	{
			          		Object[] obj = (Object[]) listGroupData.get(i);
			          		String groupCode = (String) Array.get(obj,0);
			          		String groupName = (String) Array.get(obj,1);
			          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
			          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
			          		
			          		
			          		if (mapGroupWiseData.containsKey(groupCode))
			                {
			                    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
			                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
			                }
			                else
			                {
			                    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
			                    objGroupWiseSales.setGroupCode(groupCode);
			                    objGroupWiseSales.setGroupName(groupName);
			                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);

			                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
			                }
			            	}
			         }  
			         sqlBuilder="SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
			                    + ", '" + webStockUserCode + "',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
			                    + ", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount) "
			                    + "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
			                    + "WHERE a.strBillNo=b.strBillNo "
			                    + "AND a.strPOSCode=f.strPOSCode "
			                    + "AND a.strClientCode=b.strClientCode "
			                    + "AND b.strItemCode=e.strItemCode "
			                    + "AND c.strGroupCode=d.strGroupCode "
			                    + "AND d.strSubGroupCode=e.strSubGroupCode "
			                    + "AND a.strPOSCode = '" + posCode + "' "
			                    + "AND DATE(a.dteBillDate)='" + billDate + "'"
			                    + "AND a.intShiftCode='1' "
			                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode";
            
			         query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);
			          listGroupData =query.list();
			         if (listGroupData!=null)
			         {
			               	for(int i=0; i<listGroupData.size(); i++)
			            	{
			          		Object[] obj = (Object[]) listGroupData.get(i);
			          		String groupCode = (String) Array.get(obj,0);
			          		String groupName = (String) Array.get(obj,1);
			          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
			          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
			          		
			          		if (mapGroupWiseData.containsKey(groupCode))
			                {
			                    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
			                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
			                }
			                else
			                {
			                    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
			                    objGroupWiseSales.setGroupCode(groupCode);
			                    objGroupWiseSales.setGroupName(groupName);
			                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);

			                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
			                }
			            	}
			         }
			         sqlBuilder="SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
			                    + ",'" + webStockUserCode + "','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
			                    + "FROM tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
			                    + "WHERE a.strBillNo=b.strBillNo "
			                    + "AND a.strPOSCode=f.strPosCode "
			                    + "AND a.strClientCode=b.strClientCode  "
			                    + "AND LEFT(b.strItemCode,7)=d.strItemCode  "
			                    + "AND d.strSubGroupCode=e.strSubGroupCode "
			                    + "AND e.strGroupCode=c.strGroupCode  "
			                    + "AND b.dblamount>0 "
			                    + "AND a.strPOSCode = '" + posCode + "' "
			                    + "AND a.intShiftCode='1' "
			                    + "AND DATE(a.dteBillDate) = '" + billDate + "' "
			                    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode";
			         query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);
			          listGroupData =query.list();
			         if (listGroupData!=null)
			         {
			               	for(int i=0; i<listGroupData.size(); i++)
			            	{
			          		Object[] obj = (Object[]) listGroupData.get(i);
			          		String groupCode = (String) Array.get(obj,0);
			          		String groupName = (String) Array.get(obj,1);
			          		BigDecimal netTotalPlusTx = (BigDecimal) Array.get(obj,10);
			          		double netTotalPlusTax = netTotalPlusTx.doubleValue();
			          		
			          		if (mapGroupWiseData.containsKey(groupCode))
			                {
			                    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
			                    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
			                }
			                else
			                {
			                    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
			                    objGroupWiseSales.setGroupCode(groupCode);
			                    objGroupWiseSales.setGroupName(groupName);
			                    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);

			                    mapGroupWiseData.put(groupCode, objGroupWiseSales);
			                }
			            	}
			         }      	
			         
			         if (mapGroupWiseData.size() > 0)
			         {
			        	 jObj1.put("GroupAmtWithTax", "   Group                 Amount With Tax   ");
			        	 for (clsGroupSubGroupWiseSales objGroupWiseSales : mapGroupWiseData.values())
			             {
			        		 JSONObject jObjList = new JSONObject();
			        		 String groupName = objGroupWiseSales.getGroupName();
			        		 double dblNetTotalPlusTax = Math.rint(objGroupWiseSales.getDblNetTotalPlusTax());
			        		 jObjList.put("groupName", groupName);
			        		 jObjList.put("dblNetTotalPlusTax", dblNetTotalPlusTax);
			        		 listGroupAmtWithTaxDtl.put(jObjList);
//			        		 jObj1.put("groupName", groupName);
//			        		 jObj1.put("dblNetTotalPlusTax", dblNetTotalPlusTax);
			        		// funWriteTotal(objGroupWiseSales.getGroupName(), String.valueOf(Math.rint(objGroupWiseSales.getDblNetTotalPlusTax())), bufferedWriter, "");
			                  
			             }
			         }
			         jObj1.put("listGroupAmtWithTaxDtl", listGroupAmtWithTaxDtl);
			         jObj1.put("listSettelementBrkUP",listSettelementBrkUP);
			         jObj1.put("listSettelementTaxDtl", listSettelementTaxDtl); 
			      	 jArr.put(jObj1);
			     	 jObjRet.put("jArr", jArr);	         
		 }
		
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return jObjRet;
	    }
	 
	
	
}

