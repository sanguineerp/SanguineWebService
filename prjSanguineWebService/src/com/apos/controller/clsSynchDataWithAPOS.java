package com.apos.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.POSLicence.controller.clsClientDetails;
import com.POSLicence.controller.clsEncryptDecryptClientCode;
import com.apos.bean.clsAPCReport;
import com.apos.bean.clsBillSeriesBillDtl;
import com.apos.bean.clsBillSettlementDtl;
import com.apos.bean.clsSalesFlashColumns;
import com.apos.listener.intfSynchDataWithAPOS;
import com.apos.util.clsAPOSKOT;
import com.apos.util.clsAPOSUtility;
import com.apos.util.clsConsolidatedKOTJasperGenerationForDirectBiller;
import com.apos.util.clsConsolidatedKOTJasperGenerationForMakeKOT;
import com.apos.util.clsKOTJasperFileGenerationForMakeKOT;
import com.apos.util.clsTextFileGenerator;
import com.apos.util.clsTextFormatVoidKOT;
import com.cmsws.controller.clsCMSIntegration;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsBillDtl;
import com.webservice.util.clsBillItemDtl;
import com.webservice.util.clsDirectBillerItemDtl;
import com.webservice.util.clsFileIOUtil;
import com.webservice.util.clsItemDtlForTax;
import com.webservice.util.clsPasswordEncryptDecreat;
import com.webservice.util.clsPromotionCalculation;
import com.webservice.util.clsPromotionItems;
import com.webservice.util.clsTaxCalculation;
import com.webservice.util.clsTaxCalculationDtls;
import com.webservice.util.clsUtilityFunctions;


@Controller
@Path("/APOSIntegration")
public class clsSynchDataWithAPOS implements intfSynchDataWithAPOS
{
	
	
	@Autowired
	clsTextFileGenerator obTextFileGenerator;
	
	@Autowired
	clsTextFormatVoidKOT objTextFormatVoidKOT;
	
	@Autowired
	clsAPOSKOT objAPOSKOTPrint;
	/*@Autowired
	private clsJasperBillPrinting objJasperBillPrinting;*/
	
	@Autowired 
	private clsKOTJasperFileGenerationForMakeKOT objKOTJasperFileGenerationForMakeKOT;
	
	@Autowired 
	private clsConsolidatedKOTJasperGenerationForMakeKOT objConsolidatedKOTJasperGenerationForMakeKOT;
	
	@Autowired 
	private clsConsolidatedKOTJasperGenerationForDirectBiller objConsolidatedKOTJasperGenerationForDirectBiller;
	
	@Autowired
	clsAPOSUtility objAPOSUtility;
	
	@Autowired
	clsUtilityController objUtilityController;
	
	Map<String, List<Map<String, clsBillSettlementDtl>>> mapPOSDtlForSettlement;
	Map<String, Map<String, clsBillItemDtl>> mapPOSItemDtl;
	Map<String, Map<String, clsBillItemDtl>> mapPOSMenuHeadDtl;
	private List<clsBillSeriesBillDtl> listBillSeriesBillDtl;
	//JSONObject jObjBillSeriesItemList;
	
	@SuppressWarnings("finally")
    @GET 
	@Path("/funInvokeAPOSWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetConnectionInfo()
	{
		String response = "false";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection aposCon=null;
		try
		{
			aposCon=objDb.funOpenAPOSCon("mysql","master");
		    response = "true";
		}
		catch(Exception e)
		{
			response="false";
			e.printStackTrace();
		}
		
		System.out.println("Check Server Config...");
		return Response.status(201).entity(response).build();
	}
	
	 
	
	@GET 
	@Path("/funCheckLicenceKey")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String funCheckLicenceKey(@QueryParam("strDeviceId") String deviceId,@QueryParam("strClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        String response="";
	
        Date objDate = new Date();
        int day = objDate.getDate();
        int month = objDate.getMonth() + 1;
        int year = objDate.getYear() + 1900;
        String currentDate = year + "-" + month + "-" + day;
        
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql="select * from tbllicencekey where strDeviceId='"+deviceId+"' and strClientCode='"+clientCode+"'";
            //System.out.println(sql);
            JSONArray arrObj=new JSONArray();
            ResultSet rsCheckLicence=st.executeQuery(sql);
           
            if(rsCheckLicence.next())
            {
            	sql= " Select * from tbllicencekey "
            	   + " where '"+currentDate+"' "
            	   + " between dteFromDate and dteToDate "
            	   + " and strDeviceId='"+deviceId+"' "
            	   + " and strClientCode='"+clientCode+"'";

                	ResultSet rsCheckDate=st.executeQuery(sql);
                	if(rsCheckDate.next())
                	{            			        
                        JSONObject obj=new JSONObject();
                        obj.put("Status", "Valid");
                        // obj.put("DeviceId", rsCheckLicence.getString(1));
                        //obj.put("ClientCode", rsCheckLicence.getString(2));
                         arrObj.put(obj);
                	}
                	else
                	{
                		JSONObject obj=new JSONObject();
                		obj.put("Status", "NotAvailable");
                        arrObj.put(obj);
                	}
                	rsCheckDate.close();
        
            	
            	/*JSONObject obj=new JSONObject();
            	obj.put("Status", "Valid");
               // obj.put("DeviceId", rsCheckLicence.getString(1));
               //obj.put("ClientCode", rsCheckLicence.getString(2));
                arrObj.put(obj);
            	*/
            }
            else
            {
            	JSONObject obj=new JSONObject();
        		obj.put("Status", "Invalid");
                arrObj.put(obj);
            }
          
            rsCheckLicence.close();
	        jObj.put("CheckLicence", arrObj);
	        st.close();
	        cmsCon.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj.toString();
	        }
	    }
	
	
	
@GET 
@Path("/funAuthenticateUser")
@Produces(MediaType.APPLICATION_JSON)
public JSONObject funAuthenticateUser(@QueryParam("strUserCode") String userCode,@QueryParam("strPassword") String password)
{
	clsDatabaseConnection objDb=new clsDatabaseConnection();
    Connection cmsCon=null;
    Statement st=null;
    JSONObject jObj=new JSONObject();
    
    Date objDate = new Date();
    int day = objDate.getDate();
    int month = objDate.getMonth() + 1;
    int year = objDate.getYear() + 1900;
    String currentDate = year + "-" + month + "-" + day;
		
    try {
    	cmsCon=objDb.funOpenAPOSCon("mysql","master");
        st = cmsCon.createStatement();
        String sql="";
        
        String encKey="04081977";
        //String encPassword = clsGlobalSingleObject.getObjPasswordEncryptDecreat().encrypt(encKey, password.trim().toUpperCase());
        clsPasswordEncryptDecreat objEncDec=new clsPasswordEncryptDecreat();
        String encPassword = objEncDec.encrypt(encKey, password.trim().toUpperCase());
        
        sql="select * from tbluserhd where strUserCode='"+userCode+"' and strPassword='"+encPassword+"'";
        System.out.println(sql);
        
        ResultSet rsLogin=st.executeQuery(sql);
        if(rsLogin.next())
        {
        	sql="select strUserName,strSuperType,strWaiterNo from tbluserhd "
        		+ "WHERE strUserCode = '" + userCode+"' and strPassword='" + encPassword + "' "
        		+ "AND dteValidDate>='" + currentDate + "'";
        	ResultSet rsLogin1=st.executeQuery(sql);
        	if(rsLogin1.next())
        	{  
                jObj.put("Status", "OK");
                jObj.put("UserCode", userCode);
        		jObj.put("UserName", rsLogin1.getString(1));
                jObj.put("SuperType", rsLogin1.getString(2));
                jObj.put("WaiterNo", rsLogin1.getString(3));
        	}
        	else
        	{
        		jObj.put("Status", "Expired");
        		jObj.put("UserCode", userCode);
        		jObj.put("UserName", "");
                jObj.put("SuperType","");
                jObj.put("WaiterNo", "");
        	}
        	rsLogin1.close();
        }
        else
        {
        	jObj.put("Status", "Invalid");
        	jObj.put("UserCode", userCode);
        	jObj.put("UserName", "");
            jObj.put("SuperType","");
            jObj.put("WaiterNo", "");
        }
        
        System.out.println(jObj);
        
        rsLogin.close();
        st.close();
        cmsCon.close();
            
        } 
        catch (Exception e) 
        {
        	clsUtilityFunctions objUtility = new clsUtilityFunctions();
    	    objUtility.funWriteErrorLog(e);
    	    e.printStackTrace();
        }
        finally
        {
        	return jObj;
        }
    }
	
	@GET 
	@Path("/funAuthenticateUserByScan")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funAuthenticateUserByScan(@QueryParam("DebitCardString") String strDebitCardString)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        
        Date objDate = new Date();
        int day = objDate.getDate();
        int month = objDate.getMonth() + 1;
        int year = objDate.getYear() + 1900;
        String currentDate = year + "-" + month + "-" + day;
			
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
           /* String encKey="04081977";
            clsPasswordEncryptDecreat objEncDec=new clsPasswordEncryptDecreat();
            String encPassword = objEncDec.encrypt(encKey, password.trim().toUpperCase());*/
            
            sql="select count(*) from tbluserhd WHERE strDebitCardString = '"+strDebitCardString+"'  ";
            
            //System.out.println(sql);
            
            JSONArray arrObj=new JSONArray();
            ResultSet rsLogin=st.executeQuery(sql);
            if(rsLogin.next())
            {
            	if(rsLogin.getInt(1)>=1)
            	{
            		sql="select strUserName,strSuperType,strUserCode from tbluserhd "
                			+ " where strDebitCardString='"+strDebitCardString+"' "
                			+ " and dteValidDate>='"+currentDate+"' ";
                	ResultSet rsLogin1=st.executeQuery(sql);
                	if(rsLogin1.next())
                	{            			        
                       // JSONObject obj=new JSONObject();
                		jObj.put("Status", "OK");
                		jObj.put("UserName", rsLogin1.getString(1));
                		jObj.put("SuperType", rsLogin1.getString(2));
                        jObj.put("UserCode", rsLogin1.getString(3));
                      //  jObj.put("WaiterNo", rsLogin1.getString(4));  
                        //arrObj.put(obj);
                	}
                	else
                	{
                		//JSONObject obj=new JSONObject();
                		jObj.put("Status", "Expired");
                        //arrObj.put(obj);
                	}
                	rsLogin1.close();
            		
            	}
            	else
            	{
            		
            		jObj.put("Status", "Invalid");
                   
            		
            	}
            	
            }
            rsLogin.close();
	       // jObj.put("LoginStatus", arrObj);
	        st.close();
	        cmsCon.close();
	            
	        } 
            catch (Exception e) 
            {
            	clsUtilityFunctions objUtility = new clsUtilityFunctions();
        	    objUtility.funWriteErrorLog(e);
        	    e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj;
	        }
	    }
	
	
	
	
	
	

	@GET 
	@Path("/funGetLastBillDate")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetLastBillDate()
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
		String lastBill="Not Found";
        try {
        	jObj.put("lastBilldate", lastBill);
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            sql = "select ifnull(max(date(a.dteBillDate)),0) from tblqbillhd a ";
            JSONArray arrObj=new JSONArray();
            ResultSet rsDate = st.executeQuery(sql);
            if (rsDate.next()) 
            {
            	/*JSONObject obj=new JSONObject();
            	obj.put("LastBillDate",rsDate.getString(1));
            	arrObj.put(obj);*/
            	lastBill=rsDate.getString(1);
            }
            rsDate.close();
            //jObj.put("LastBillDateDtl", arrObj);
            st.close();
            cmsCon.close();
            jObj.put("lastBilldate", lastBill);
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
        	//return lastBill;
        	//return Response.status(201).entity("true").build();
        	return jObj;
        }
    }
	
	

	@GET 
	@Path("/funGetTableList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetTableList(@QueryParam("POSCode") String POSCode,@QueryParam("CMSIntegration") boolean flgCMSIntegration
			,@QueryParam("memberAsTable") boolean flgMemAsTable)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        //JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            if(flgCMSIntegration)
            {
                if(flgMemAsTable)
                {
                    sql = "select strTableNo, strTableName, intSequence, strStatus, strAreaCode from tbltablemaster "
                        + " where (strPOSCode='" + POSCode + "' or strPOSCode='All') "
                        + " and strOperational='Y' and strStatus!='Normal' "
                        + " order by strTableName";
                }
                else
                {
                    sql = "select strTableNo, strTableName, intSequence, strStatus, strAreaCode from tbltablemaster "
                        + " where (strPOSCode='" + POSCode + "' or strPOSCode='All') "
                        + " and strOperational='Y' "
                        + " order by intSequence";
                }
            }
            else
            {
                sql = " select a.strTableNo, a.strTableName, a.intSequence, a.strStatus, a.strAreaCode ,ifnull(b.strWaiterNo,'')"
                	+ ",ifnull(c.strWShortName,''), ifnull(sum(b.dblAmount) ,0.00),ifnull(b.dblRedeemAmt,0),ifnull(b.strCardType,''),ifnull(b.intPaxNo,1),ifnull(b.strCardNo,'') ,ifnull(d.strWaiterNo ,'') "
                	+ " from tbltablemaster a left outer join tblitemrtemp b "
                	+ "	on a.strTableNo=b.strTableNo "
                	+ " left outer join tblwaitermaster c "
                	+ " left outer join tbluserhd d on c.strWaiterNo=d.strWaiterNo "
                	+ "	on b.strWaiterNo=c.strWaiterNo "
                	+ " where (a.strPOSCode='" + POSCode + "' or a.strPOSCode='All' ) "
                	+ "	and a.strOperational='Y' "
                	+ "	group by a.strTableNo "
                	+ "	order by a.intSequence ";
            }
            //System.out.println(sql);
            
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("TableName",rsTableInfo.getString(2));
            	obj.put("TableNo",rsTableInfo.getString(1));
            	obj.put("TableStatus",rsTableInfo.getString(4));
            	obj.put("AreaCode",rsTableInfo.getString(5));
            	obj.put("WaiterNo",rsTableInfo.getString(6));
            	obj.put("WaiterName",rsTableInfo.getString(7));
            	obj.put("KOTAmt",rsTableInfo.getString(8));
            	obj.put("CardBalance",rsTableInfo.getString(9));
            	obj.put("CardType",rsTableInfo.getString(10));
            	obj.put("PaxNo",rsTableInfo.getString(11));
            	obj.put("CardNo",rsTableInfo.getString(12));
            	obj.put("linkedWaiterNo",rsTableInfo.getString(13));
            	arrObj.put(obj);
            }
            rsTableInfo.close();
          //  jObj.put("TableList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	@GET 
	@Path("/funGetTableListUserWise")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetTableListUserWise(@QueryParam("POSCode") String POSCode,@QueryParam("CMSIntegration") boolean flgCMSIntegration
			,@QueryParam("memberAsTable") boolean flgMemAsTable,@QueryParam("userCode") String userCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        //JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            if(flgCMSIntegration)
            {
                if(flgMemAsTable)
                {
                    sql = "select strTableNo, strTableName, intSequence, strStatus, strAreaCode from tbltablemaster "
                        + " where (strPOSCode='" + POSCode + "' or strPOSCode='All') "
                        + " and strOperational='Y' and strStatus!='Normal' "
                        + " order by strTableName";
                }
                else
                {
                    sql = "select strTableNo, strTableName, intSequence, strStatus, strAreaCode from tbltablemaster "
                        + " where (strPOSCode='" + POSCode + "' or strPOSCode='All') "
                        + " and strOperational='Y' "
                        + " order by intSequence";
                }
            }
            else
            {
                sql = " select a.strTableNo, a.strTableName, a.intSequence, a.strStatus, a.strAreaCode ,ifnull(b.strWaiterNo,'')"
                	+ ",ifnull(c.strWShortName,''), ifnull(sum(b.dblAmount) ,0.00),ifnull(b.dblRedeemAmt,0),ifnull(b.strCardType,''),ifnull(b.intPaxNo,1),ifnull(b.strCardNo,'') ,ifnull(d.strWaiterNo ,'') "
                	+ " from tbltablemaster a left outer join tblitemrtemp b "
                	+ "	on a.strTableNo=b.strTableNo "
                	+ " left outer join tblwaitermaster c "
                	+ " left outer join tbluserhd d on c.strWaiterNo=d.strWaiterNo "
                	+ "	on b.strWaiterNo=c.strWaiterNo "
                	+ " where (a.strPOSCode='" + POSCode + "' or a.strPOSCode='All' ) "
                	+ "	and a.strOperational='Y' "
                	+ "	group by a.strTableNo "
                	+ "	order by a.intSequence ";
            }
            //System.out.println(sql);
            String linkedWaiter="";
            String sqluserWaiter="select a.strWaiterNo from tbluserhd a where a.strUserCode='"+userCode+"'";
            ResultSet rsuserWaiter = st.executeQuery(sqluserWaiter);
            while (rsuserWaiter.next()) 
            {
            	linkedWaiter=rsuserWaiter.getString(1);
            }
            rsuserWaiter.close();
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	if(rsTableInfo.getString(13).equals(linkedWaiter) || rsTableInfo.getString(13).equals("") ){
            		obj.put("TableName",rsTableInfo.getString(2));
                	obj.put("TableNo",rsTableInfo.getString(1));
                	obj.put("TableStatus",rsTableInfo.getString(4));
                	obj.put("AreaCode",rsTableInfo.getString(5));
                	obj.put("WaiterNo",rsTableInfo.getString(6));
                	obj.put("WaiterName",rsTableInfo.getString(7));
                	obj.put("KOTAmt",rsTableInfo.getString(8));
                	obj.put("CardBalance",rsTableInfo.getString(9));
                	obj.put("CardType",rsTableInfo.getString(10));
                	obj.put("PaxNo",rsTableInfo.getString(11));
                	obj.put("CardNo",rsTableInfo.getString(12));
                	obj.put("linkedWaiterNo",rsTableInfo.getString(13));
                	arrObj.put(obj);	
            	}
            }
            rsTableInfo.close();
          //  jObj.put("TableList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	@GET 
	@Path("/funGetTableStatus")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetTableStatus(@QueryParam("POSCode") String POSCode,@QueryParam("Type") String status,@QueryParam("AreaCode") String areaCode)
	{
		return objAPOSUtility.funGetTableStatusData(POSCode,status,areaCode);

	}
	

	
	@GET 
	@Path("/funGetWaiterList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetWaiterList(@QueryParam("POSCode") String POSCode,@QueryParam("UserCode") String UserCode
			,@QueryParam("Applicable") boolean flgApplicable)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
      //  JSONObject jObj=new JSONObject();
        JSONArray arrObj=null;
		
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            
            String sqlCheck="",sqlWaiter;
            
            if(flgApplicable)
    		{
            	 sqlCheck="select strWaiterNo from tbluserhd where strUserCode='"+UserCode+"' ";
                 ResultSet rsCheckWaiter = st.executeQuery(sqlCheck);
                 while (rsCheckWaiter.next()) 
                 {
                 	if(rsCheckWaiter.getString(1).trim().length()>0)
                     {
                 		sqlWaiter = "select strWaiterNo,strWShortName,strWFullName "
                                + " from tblwaitermaster where strOperational='Y' and strWaiterNo='"+rsCheckWaiter.getString(1)+"' ";
                    	
                    	arrObj=new JSONArray();
                        ResultSet rsWaiterInfo = st1.executeQuery(sqlWaiter);
                        while (rsWaiterInfo.next()) 
                        {
                        	JSONObject obj=new JSONObject();
                        	obj.put("WaiterName",rsWaiterInfo.getString(2));
                        	obj.put("WaiterNo",rsWaiterInfo.getString(1));
                        	arrObj.put(obj);
                        }
                        rsWaiterInfo.close();
                       // jObj.put("WaiterList", arrObj);
                        st1.close();
                     }
                     else
                     {
                    	 sqlWaiter = "select strWaiterNo,strWShortName,strWFullName "
                                 + " from tblwaitermaster where strOperational='Y'";
                     	
                     	 arrObj=new JSONArray();
                         ResultSet rsWaiterInfo = st1.executeQuery(sqlWaiter);
                         while (rsWaiterInfo.next()) 
                         {
                         	JSONObject obj=new JSONObject();
                         	obj.put("WaiterName",rsWaiterInfo.getString(2));
                         	obj.put("WaiterNo",rsWaiterInfo.getString(1));
                         	arrObj.put(obj);
                         }
                         rsWaiterInfo.close();
                        // jObj.put("WaiterList", arrObj);
                         st1.close();
                     } 
                 	
                 }
                 rsCheckWaiter.close();
                 st.close();
    		}
            else
            {
            	sqlWaiter = "select strWaiterNo,strWShortName,strWFullName "
                        + " from tblwaitermaster where strOperational='Y'";
            	
            	arrObj=new JSONArray();
                ResultSet rsWaiterInfo = st1.executeQuery(sqlWaiter);
                while (rsWaiterInfo.next()) 
                {
                	JSONObject obj=new JSONObject();
                	obj.put("WaiterName",rsWaiterInfo.getString(2));
                	obj.put("WaiterNo",rsWaiterInfo.getString(1));
                	arrObj.put(obj);
                }
                rsWaiterInfo.close();
               // jObj.put("WaiterList", arrObj);
                st1.close();
            }
            
           
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;
        }
    }
	
	
	
	@GET 
	@Path("/funGetBusyTableList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetBusyTableList(@QueryParam("POSCode") String POSCode,@QueryParam("CMSIntegration") boolean flgCMSIntegration)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            if(flgCMSIntegration)
            {
            	sql = "select b.strTableNo,a.strTableName "
            		+ "from tbltablemaster a,tblitemrtemp b "
                    + "where a.strTableNo=b.strTableNo and b.strPOSCode='"+POSCode+"' "
                    + "group by b.strTableNo order by a.strTableNo;";
            }
            else
            {
            	sql = "select b.strTableNo,a.strTableName "
            		+ "from tbltablemaster a,tblitemrtemp b "
                    + "where a.strTableNo=b.strTableNo and b.strPOSCode='"+POSCode+"' "
                    + "group by b.strTableNo order by a.strTableNo;";
            }
            //System.out.println(sql);
            
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("TableName",rsTableInfo.getString(2));
            	obj.put("TableNo",rsTableInfo.getString(1));
            	arrObj.put(obj);
            }
            rsTableInfo.close();
            jObj.put("TableList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;// jObj.toString();
        }
    }
	
	
	
	@GET 
	@Path("/funGetBusyTableListUserWise")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetBusyTableListUserWise(@QueryParam("POSCode") String POSCode,@QueryParam("CMSIntegration") boolean flgCMSIntegration,
			@QueryParam("userCode") String userCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            if(flgCMSIntegration)
            {
            	sql = "select b.strTableNo,a.strTableName "
            		+ "from tbltablemaster a,tblitemrtemp b "
                    + "where a.strTableNo=b.strTableNo and b.strPOSCode='"+POSCode+"' "
                    + "group by b.strTableNo order by a.strTableNo;";
            }
            else
            {
            	sql = "select b.strTableNo,a.strTableName,b.strWaiterNo "
            		+ "from tbltablemaster a,tblitemrtemp b "
                    + "where a.strTableNo=b.strTableNo and b.strPOSCode='"+POSCode+"' "
                    + "group by b.strTableNo order by a.strTableNo;";
            }
            //System.out.println(sql);
            
            String linkedWaiter="";
            String sqluserWaiter="select a.strWaiterNo from tbluserhd a where a.strUserCode='"+userCode+"'";
            ResultSet rsuserWaiter = st.executeQuery(sqluserWaiter);
            while (rsuserWaiter.next()) 
            {
            	linkedWaiter=rsuserWaiter.getString(1);
            }
            rsuserWaiter.close();
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	if(rsTableInfo.getString(3).equals(linkedWaiter)){
            		JSONObject obj=new JSONObject();
                	obj.put("TableName",rsTableInfo.getString(2));
                	obj.put("TableNo",rsTableInfo.getString(1));
                	arrObj.put(obj);
            	}
            	
            }
            rsTableInfo.close();
            jObj.put("TableList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;// jObj.toString();
        }
    }
	
	
	
	@GET 
	@Path("/funGetLiveKOTList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetLiveKOTList(@QueryParam("POSCode") String POSCode
			,@QueryParam("TableNo") String tableNo
			,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = "select a.strKOTNo,a.strTableNo,b.strTableName,ifnull(c.strWShortName,'') "
            	+ ",a.strPrintYN,a.strTakeAwayYesNo,a.strUserCreated,a.intPaxNo "
                + ",sum(a.dblAmount),a.strManualKOTNo,d.strPOSName,d.strPOSCode "
                + "from tblitemrtemp a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                + "left outer join  tblwaitermaster c on a.strWaiterNo=c.strWaiterNo "
                + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
                + "where a.strTableNo=b.strTableNo ";
            if(!POSCode.equals("All"))
            {
                sql+= " and a.strPOSCode='" + POSCode + "'";
            }
            if(!tableNo.equals("All"))
            {
                sql+=" and a.strTableNo='"+tableNo+"'";
            }
            sql+= " group by a.strKOTNo";
            
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("KOTNo",rsTableInfo.getString(1));
            	obj.put("TableNo",rsTableInfo.getString(2));
            	obj.put("TableName",rsTableInfo.getString(3));
            	obj.put("WaiterName",rsTableInfo.getString(4));
            	obj.put("TakeAwayYN",rsTableInfo.getString(6));
            	obj.put("User",rsTableInfo.getString(7));
            	obj.put("PAX",rsTableInfo.getString(8));
            	obj.put("Amount",rsTableInfo.getString(9));
            	obj.put("POSName",rsTableInfo.getString(11));
            	obj.put("POSCode",rsTableInfo.getString(12));
            	arrObj.put(obj);
            }
            rsTableInfo.close();
            jObj.put("KOTList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	
	@GET 
	@Path("/funGetRechargeSlip")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetRechargeSlipList(@QueryParam("POSCode") String POSCode
			,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = "  select a.intRechargeNo,a.strCardString,a.dblRechargeAmount,a.strRechargeSlipNo,a.strCardNo,ifnull(c.strCustomerName,''), "
            	+ " date(a.dteDateCreated),time(a.dteDateCreated) "
            	+ " from tbldebitcardrecharge a,tbldebitcardmaster b left outer join tblcustomermaster c on "
            	+ " b.strCustomerCode=c.strCustomerCode "
            	+ " where a.strCardString=b.strCardString " ;
            if(!POSCode.equals("All"))
            {
                sql+= " and a.strPOSCode='" + POSCode + "'";
            }
            //System.out.println("query="+sql);
            
            
            ResultSet rsRechargeInfo = st.executeQuery(sql);
            while (rsRechargeInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	
            	
            	obj.put("strDocNo",rsRechargeInfo.getString(1));   
            	obj.put("strCardString",rsRechargeInfo.getString(2));
            	obj.put("strAmount",rsRechargeInfo.getString(3));
            	obj.put("strDocSlipNo",rsRechargeInfo.getString(4));
            	obj.put("strCardNo",rsRechargeInfo.getString(5));
            	obj.put("strCustomerName",rsRechargeInfo.getString(6));
            	obj.put("strDate",rsRechargeInfo.getString(7));
            	obj.put("strTime",rsRechargeInfo.getString(8));
            	arrObj.put(obj);
            }
            rsRechargeInfo.close();
            jObj.put("RechargeSlipList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	
	@GET 
	@Path("/funPrintRechargeSlip")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetRechargeSlipList(@QueryParam("POSCode") String POSCode
			,@QueryParam("ClientCode") String clientCode,@QueryParam("RechargeNo") String rechargeNo)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = " select a.intRechargeNo,a.strCardString,a.dblRechargeAmount,a.strRechargeSlipNo,a.strCardNo,ifnull(c.strCustomerName,''), "
            	+ " date(a.dteDateCreated),time(a.dteDateCreated) "
            	+ " from tbldebitcardrecharge a,tbldebitcardmaster b left outer join tblcustomermaster c on "
            	+ " b.strCustomerCode=c.strCustomerCode "
            	+ " where a.strCardString=b.strCardString  "
            	+ " and a.intRechargeNo='"+rechargeNo+"' ";
            if(!POSCode.equals("All"))
	        {
            	sql+= " and a.strPOSCode='" + POSCode + "'";
	        }
            //System.out.println("query="+sql);
            
           
            ResultSet rsRechargeInfo = st.executeQuery(sql);
            while (rsRechargeInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	
           
            	
            	obj.put("strDocNo",rsRechargeInfo.getString(1));   
            	obj.put("strCardString",rsRechargeInfo.getString(2));
            	obj.put("strAmount",rsRechargeInfo.getString(3));
            	obj.put("strDocSlipNo",rsRechargeInfo.getString(4));
            	obj.put("strCardNo",rsRechargeInfo.getString(5));
            	obj.put("strCustomerName",rsRechargeInfo.getString(6));
            	obj.put("strDate",rsRechargeInfo.getString(7));
            	obj.put("strTime",rsRechargeInfo.getString(8));
            	arrObj.put(obj);
            }
            rsRechargeInfo.close();
            jObj.put("RechargeSlipList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	
	
	@GET 
	@Path("/funGetRefundSlip")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetRefundSlipList(@QueryParam("POSCode") String POSCode
			,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = " select a.strRefundNo,a.strCardString,a.dblRefundAmt,a.strRefundSlipNo,a.strCardNo "
            		+ " ,ifnull(c.strCustomerName,''),date(a.dteDateCreated),time(a.dteDateCreated) "
            		+ " from tbldebitcardrefundamt a,tbldebitcardmaster b left outer join tblcustomermaster c on "
            		+ " b.strCustomerCode=c.strCustomerCode "
            		+ " where a.strCardString=b.strCardString  ";
            if(!POSCode.equals("All"))
            {
                sql+= " and a.strPOSCode='" + POSCode + "'";
            }
            //System.out.println("query="+sql);
            
           
            ResultSet rsRefundInfo = st.executeQuery(sql);
            while (rsRefundInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("strDocNo",rsRefundInfo.getString(1));
            	obj.put("strCardString",rsRefundInfo.getString(2));
            	obj.put("strAmount",rsRefundInfo.getString(3));
            	obj.put("strDocSlipNo",rsRefundInfo.getString(4));
            	obj.put("strCardNo",rsRefundInfo.getString(5));
            	obj.put("strCustomerName",rsRefundInfo.getString(6));
            	obj.put("strDate",rsRefundInfo.getString(7));
            	obj.put("strTime",rsRefundInfo.getString(8));
            	arrObj.put(obj);
            }
            rsRefundInfo.close();
            jObj.put("RefundSlipList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	@GET 
	@Path("/funPrintRefundSlip")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetRefundSlipData(@QueryParam("POSCode") String POSCode
			,@QueryParam("ClientCode") String clientCode,@QueryParam("RefundNo") String refundNo)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = "  select a.strRefundNo,a.strCardString,a.dblRefundAmt,a.strRefundSlipNo,a.strCardNo "
            		+ ", ifnull(c.strCustomerName,''),date(a.dteDateCreated),time(a.dteDateCreated) "
            		+ " from tbldebitcardrefundamt a,tbldebitcardmaster b left outer join tblcustomermaster c on "
            		+ " b.strCustomerCode=c.strCustomerCode "
            		+ " where a.strCardString=b.strCardString  "
            		+ " and a.strRefundNo='"+refundNo+"' ";
            if(!POSCode.equals("All"))
            {
                sql+= " and a.strPOSCode='" + POSCode + "'";
            }
            //System.out.println("query="+sql);
           
            ResultSet rsRefundInfo = st.executeQuery(sql);
            while (rsRefundInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	
            
            	
            	obj.put("strDocNo",rsRefundInfo.getString(1));
            	obj.put("strCardString",rsRefundInfo.getString(2));
            	obj.put("strAmount",rsRefundInfo.getString(3));
            	obj.put("strDocSlipNo",rsRefundInfo.getString(4));
            	obj.put("strCardNo",rsRefundInfo.getString(5));
            	obj.put("strCustomerName",rsRefundInfo.getString(6));
            	obj.put("strDate",rsRefundInfo.getString(7));
            	obj.put("strTime",rsRefundInfo.getString(8));
            	arrObj.put(obj);
            }
            rsRefundInfo.close();
            jObj.put("RefundSlipList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	@GET 
	@Path("/funGetKOTData")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetKOTData(@QueryParam("POSCode") String POSCode
			,@QueryParam("KOTNo") String KOTNo
			,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
		
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
                        
            sql = "select a.strSerialNo,a.strTableNo,a.strCardNo,ifnull(a.dblRedeemAmt,0),a.strHomeDelivery,a.strCustomerCode,a.strPOSCode,a.strItemCode,a.strItemName,"
            		+ "sum(a.dblItemQuantity),sum(a.dblAmount),a.strWaiterNo,a.strKOTNo,a.intPaxNo,a.strPrintYN,a.strManualKOTNo,a.strUserCreated,a.strUserEdited,"
            		+ "a.dteDateCreated,a.dteDateEdited,a.strOrderBefore,a.strTakeAwayYesNo,a.tdhComboItemYN,a.strDelBoyCode,a.strNCKotYN,a.strCustomerName,a.strActiveYN,"
            		+ "a.dblBalance,a.dblCreditLimit,a.strCounterCode,a.strPromoCode,a.dblRate,a.strCardType "
                    + " from tblitemrtemp a  "
            		+ " where a.strKOTNo='"+KOTNo+"' "
            		+ " and a.strPOSCode='"+POSCode+"' "
            		+ " group by a.strItemCode,a.strPOSCode,a.strItemName  order by a.strSerialNo";	
            System.out.println(sql);
            
            JSONArray arrObj=new JSONArray();
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("SerialNo",rsTableInfo.getString(1));
            	obj.put("TableNo",rsTableInfo.getString(2));
            	obj.put("CardNo",rsTableInfo.getString(3));
            	obj.put("RedeemAmt",rsTableInfo.getString(4));
            	obj.put("HomeDelivery",rsTableInfo.getString(5));
            	obj.put("CustomerCode",rsTableInfo.getString(6));
            	obj.put("POSCode",rsTableInfo.getString(7));
            	obj.put("ItemCode",rsTableInfo.getString(8));
            	obj.put("ItemName",rsTableInfo.getString(9));
            	obj.put("ItemQty",rsTableInfo.getString(10));
            	obj.put("Amount",rsTableInfo.getString(11));
            	obj.put("WaiterNo",rsTableInfo.getString(12));
            	obj.put("KOTNo",rsTableInfo.getString(13));
            	obj.put("PAX",rsTableInfo.getString(14));
            	obj.put("PrintYN",rsTableInfo.getString(15));
            	obj.put("ManualKOTNo",rsTableInfo.getString(16));
            	obj.put("UserCreated",rsTableInfo.getString(17));
            	obj.put("UserEdited",rsTableInfo.getString(18));
            	obj.put("DateCreated",rsTableInfo.getString(19));
            	obj.put("DateEdited",rsTableInfo.getString(20));
            	obj.put("OrderBefore",rsTableInfo.getString(21));
            	obj.put("TakeAwayYN",rsTableInfo.getString(22));
            	obj.put("tdhComboItemYN",rsTableInfo.getString(23));
            	obj.put("DelBoyCode",rsTableInfo.getString(24));
            	obj.put("NCKOTYN",rsTableInfo.getString(25));
            	obj.put("CustomerName",rsTableInfo.getString(26));
            	obj.put("ActiveYN",rsTableInfo.getString(27));
            	obj.put("Balance",rsTableInfo.getString(28));
            	obj.put("CreditLimit",rsTableInfo.getString(29));
            	obj.put("CounterCode",rsTableInfo.getString(30));
            	obj.put("PromoCode",rsTableInfo.getString(31));
            	obj.put("Rate",rsTableInfo.getString(32));
            	obj.put("CardType",rsTableInfo.getString(33));
            	
            	arrObj.put(obj);
            }
            rsTableInfo.close();
            jObj.put("KOTData", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return jObj;
        }
    }
	
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funVoidKOT")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funVoidKOT(JSONObject objKOTData)
	{
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
		String response = "false";
		String kotNo="";
		int exe=0;
		JSONObject jObj=new JSONObject();
		try {
			
			String tableNo="",posCode="", posName="";
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
			
	        kotNo=objKOTData.get("KOTNo").toString();			
	        String voidType=objKOTData.get("VoidType").toString();
	        String deviceName=objKOTData.get("deviceName").toString(); 
	        String macAddress=objKOTData.get("macAddress").toString(); 
			
			if(voidType.equalsIgnoreCase("FullVoid"))
			{
				boolean flgData=false;
				
				JSONArray mJsonArrVoidKOT=(JSONArray)objKOTData.get("VoidKOTDtl");
				String user=mJsonArrVoidKOT.getJSONObject(0).getString("User");
				String clientCode=mJsonArrVoidKOT.getJSONObject(1).getString("ClientCode");
				String dateTime=mJsonArrVoidKOT.getJSONObject(2).getString("DateTime");
				String voidedDate=mJsonArrVoidKOT.getJSONObject(6).getString("VoidedDate");
				String reasonCode=mJsonArrVoidKOT.getJSONObject(3).getString("Reason");
				String type=mJsonArrVoidKOT.getJSONObject(4).getString("Type");
				String manualKOTNo=mJsonArrVoidKOT.getJSONObject(5).getString("ManualKOTNo");
			
				
				String insertVoidKOT = "INSERT INTO `tblvoidkot` (`strTableNo`, `strPOSCode`,"
					+ " `strItemCode`, `strItemName`, `dblItemQuantity`, `dblAmount`, "
					+ "`strWaiterNo`, `strKOTNo`, `intPaxNo`, `strType`, "
					+ "`strReasonCode`, `strUserCreated`, `dteDateCreated`, "
					+ "`dteVoidedDate`, `strDataPostFlag`, `strClientCode`,"
					+ " `strManualKOTNo`, `strPrintKOT`) VALUES";
				String sql="select * from tblitemrtemp where strKOTNo='"+kotNo+"'";
				ResultSet rs=st.executeQuery(sql);
				String sqlInsertVoidKOT="";
				while(rs.next())
				{
					posCode=rs.getString(7);
					tableNo=rs.getString(2);
					sqlInsertVoidKOT+=",('"+rs.getString(2)+"','"+rs.getString(7)+"','"+rs.getString(8)+"'"
						+ ",'"+rs.getString(9)+"','"+rs.getString(10)+"','"+rs.getString(11)+"'"
						+ ",'"+rs.getString(12)+"','"+kotNo+"','"+rs.getString(14)+"'"
						+ ",'"+type+"','"+reasonCode+"','"+user+"','"+dateTime+"'"
						+ ",'"+voidedDate+"','N','"+clientCode+"','"+manualKOTNo+"'"
						+ ",'Y')";
					tableNo=rs.getString(2);
					flgData=true;
				}
				if(flgData)
				{
					sqlInsertVoidKOT=sqlInsertVoidKOT.substring(1,sqlInsertVoidKOT.length());
					insertVoidKOT+=" "+sqlInsertVoidKOT;
					System.out.println("FullVoid=\t"+insertVoidKOT);
					st.executeUpdate(insertVoidKOT);
				}
				sql="delete from tblitemrtemp where strKOTNo='"+kotNo+"'";
				st.executeUpdate(sql);
				
				sql="update tbltablemaster set strStatus='Normal' where strTableNo='"+tableNo+"'";
	        	//System.out.println(sql);
	        	st.executeUpdate(sql);
	        	
	        	funRemotePrint(kotNo,posCode,tableNo,deviceName);
			}
			else
			{
				JSONArray mJsonArray=(JSONArray)objKOTData.get("KOTDtl");
				JSONArray mJsonArrVoidKOT=(JSONArray)objKOTData.get("VoidKOTDtl");
				
				String sql="delete from tblitemrtemp where strKOTNo='"+kotNo+"'";
				st.executeUpdate(sql);
				boolean flgData=false;
				JSONObject mJsonObject = new JSONObject();
				
				String insertQuery = "insert into tblitemrtemp(strSerialNo,strTableNo,strCardNo,strPosCode,strItemCode"
					+ ",strHomeDelivery,strCustomerCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo"
	                + ",strKOTNo,intPaxNo,strPrintYN,strUserCreated,strUserEdited,dteDateCreated"
	                + ",dteDateEdited,strTakeAwayYesNo,strNCKotYN,strCustomerName,strCounterCode"
	                + ",dblRate,strCardType,strDeviceMACAdd,strDeviceId) values ";
				
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
				    mJsonObject =(JSONObject) mJsonArray.get(i);
				    String itemName=mJsonObject.get("strItemName").toString();
				    //System.out.println(itemName);
				    double qty=Double.parseDouble(mJsonObject.get("dblItemQuantity").toString());
				    double rate=Double.parseDouble(mJsonObject.get("dblRate").toString());
				    double amt=qty*rate;
				    
				    tableNo=mJsonObject.get("strTableNo").toString();
				    posCode=mJsonObject.get("strPOSCode").toString();
				    posName=mJsonObject.get("strPOSName").toString();
				    kotNo=mJsonObject.get("strKOTNo").toString();
				    
				    insertQuery += "('"+mJsonObject.get("strSerialNo").toString()+"','"+mJsonObject.get("strTableNo").toString()+"','"+mJsonObject.get("strCardNo").toString()+"'"
				    	+ ",'" + mJsonObject.get("strPOSCode").toString() + "','" + mJsonObject.get("strItemCode").toString() + "'"
		                + ",'" + mJsonObject.get("strHomeDelivery").toString() + "','"+mJsonObject.get("strCustomerCode").toString()+"'"
	                   	+ ",'" + mJsonObject.get("strItemName").toString() + "','" + mJsonObject.get("dblItemQuantity").toString() + "'"
	                   	+ ",'" + amt + "','" + mJsonObject.get("strWaiterNo").toString() + "'"
	                 	+ ",'" + mJsonObject.get("strKOTNo").toString() + "','" + mJsonObject.get("intPaxNo").toString() + "'"
	            		+ ",'" + mJsonObject.get("strPrintYN").toString() + "','" + mJsonObject.get("strUserCreated").toString() + "'"
	            		+ ",'" + mJsonObject.get("strUserEdited").toString() + "','" + mJsonObject.get("dteDateCreated").toString() + "'"
	         			+ ",'" + mJsonObject.get("dteDateEdited").toString() + "','"+mJsonObject.get("strTakeAwayYesNo").toString()+"'"
	      				+ ",'"+ mJsonObject.get("strNCKotYN").toString() + "','"+mJsonObject.get("strCustomerName").toString()+"'"
	      				+ ",'"+mJsonObject.get("strCounterCode").toString()+"','"+mJsonObject.get("dblRate").toString()+"',"
	      				+ "'"+mJsonObject.get("strCardType").toString()+"','"+macAddress+"','"+deviceName+"'),";
				    flgData=true;
				}
				
				if(flgData)
				{
		            StringBuilder sb = new StringBuilder(insertQuery);
		            int index = sb.lastIndexOf(",");
		            insertQuery = sb.delete(index, sb.length()).toString();
					//System.out.println(insertQuery);
		            exe=st.executeUpdate(insertQuery);
		            //System.out.println("Exe= "+exe);
		            
		            /*if(exe>0)
		            {
		            	funGenerateTextFileForKOT(posCode, posName, tableNo, kotNo, "", "", "Dina", "Y");	            	
		            }*/
				}
				
				
				String insertVoidKOT = "INSERT INTO `tblvoidkot` (`strTableNo`, `strPOSCode`,"
						+ " `strItemCode`, `strItemName`, `dblItemQuantity`, `dblAmount`, "
						+ "`strWaiterNo`, `strKOTNo`, `intPaxNo`, `strType`, "
						+ "`strReasonCode`, `strUserCreated`, `dteDateCreated`, "
						+ "`dteVoidedDate`, `strDataPostFlag`, `strClientCode`,"
						+ " `strManualKOTNo`, `strPrintKOT`) VALUES";
					
				sql="";			
				flgData=false;
				String KOTNo="";
				
				JSONObject mJsonObjVoidKOT = new JSONObject();
				for (int i = 0; i < mJsonArrVoidKOT.length(); i++) 
				{
					mJsonObjVoidKOT =(JSONObject) mJsonArrVoidKOT.get(i);
				        
					KOTNo=mJsonObjVoidKOT.get("KOTNo").toString();
				
					String ClientCode=mJsonObjVoidKOT.get("ClientCode").toString();
						
					String deleteSql="delete from tblvoidkot "
						+ "where strKOTNo='"+KOTNo+"' and strClientCode='"+ClientCode+"'";
					//st.executeUpdate(deleteSql);
						
			        tableNo=mJsonObjVoidKOT.get("TableNo").toString();
					String POSCode=mJsonObjVoidKOT.get("POSCode").toString();
					String ItemCode=mJsonObjVoidKOT.get("ItemCode").toString();
					String ItemName=mJsonObjVoidKOT.get("ItemName").toString();
					double ItemQuantity=Double.parseDouble(mJsonObjVoidKOT.get("ItemQuantity").toString());
					double Amount=Double.parseDouble(mJsonObjVoidKOT.get("Amount").toString());
					String WaiterNo=mJsonObjVoidKOT.get("WaiterNo").toString();
					int PaxNo=Integer.parseInt(mJsonObjVoidKOT.get("PaxNo").toString());
					String Type=mJsonObjVoidKOT.get("Type").toString();
					String ReasonCode=mJsonObjVoidKOT.get("ReasonCode").toString();
					String UserCreated=mJsonObjVoidKOT.get("UserCreated").toString();
					String DateCreated=mJsonObjVoidKOT.get("DateCreated").toString();
					String VoidedDate=mJsonObjVoidKOT.get("VoidedDate").toString();
					String ManualKOTNo=mJsonObjVoidKOT.get("ManualKOTNo").toString();
					String PrintKOT=mJsonObjVoidKOT.get("PrintKOT").toString();
					    
					sql+=",('"+tableNo+"','"+POSCode+"','"+ItemCode+"','"+ItemName+"'"
				    	+ ",'"+ItemQuantity+"','"+Amount+"','"+WaiterNo+"','"+KOTNo+"'"
		    			+ ",'"+PaxNo+"','"+Type+"','"+ReasonCode+"','"+UserCreated+"'"
	   					+ ",'"+DateCreated+"','"+VoidedDate+"','N','"+ClientCode+"'"
	   					+ ",'"+ManualKOTNo+"','"+PrintKOT+"')";				    
					flgData=true;
				}
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insertVoidKOT+=" "+sql;
					//System.out.println("ItemVoid=\t"+insertVoidKOT);
					st.executeUpdate(insertVoidKOT);
					sql=" select count(strTableNo) from tblitemrtemp where strKOTNo='"+KOTNo+"' "
					   +" and strTableNo='"+tableNo+"' ";	
					ResultSet rs=st.executeQuery(sql);
					int count=0;
					if(rs.next())
					{
						count=rs.getInt(1);
					}
					if(count==0)
					{
						sql="delete from tblitemrtemp where strKOTNo='"+kotNo+"'";
						st.executeUpdate(sql);
						
						sql="update tbltablemaster set strStatus='Normal' where strTableNo='"+tableNo+"'";
			        	System.out.println(sql);
			        	st.executeUpdate(sql);
					}
					funRemotePrint(kotNo,posCode,tableNo,deviceName);
				}
			}	
            
			jObj.put("kotNo", kotNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(exe==0)
			kotNo="ERROR";
		
		try{
			jObj.put("kotNo", kotNo);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return jObj;//
	}
	
		
	
	@GET 
	@Path("/funGetTableBillData")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetTableBillData(@QueryParam("POSCode") String POSCode,@QueryParam("TableNo") String tableNo
			,@QueryParam("ClientCode") String clientCode,@QueryParam("POSStartDate") String POSStartDate
			,@QueryParam("ApplyPromotion") String applyPromotion,@QueryParam("ApplyPromotionToBill") String applyPromotionBill)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,stArea=null,stSetup=null;
        double subTotalForTax=0,itemTotal=0,modItemTotal=0;
        String operationTypeForTax="Dine In",promoItemWithAmtAndQty="",areaWisePromotions="",strAreaWisePricing="N";
        String areaCode="",areaCodeForPromo="";
        List<clsDirectBillerItemDtl> arrDirectBillerListItemDtls=new ArrayList<clsDirectBillerItemDtl>();
        List <clsPromotionItems> arrListPromoItemDtl=new ArrayList<clsPromotionItems>();
        Map<String, clsPromotionItems> hmPromoItemDtl = null;
    	Map<String, clsPromotionItems> hmPromoItem = new HashMap<String, clsPromotionItems>();
    	Map<String, clsPromotionItems> hmPromoItemsToDisplay = new HashMap<String, clsPromotionItems>();
    	HashMap<String, clsBillItemDtl>  mapPromoItemDisc = new HashMap<>();
        JSONObject jObj=new JSONObject();
		
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            stArea = cmsCon.createStatement();
            stSetup = cmsCon.createStatement();
            String sql="";
            
		            
            String AreaCodeForAll = "";
            sql = "select strAreaCode from tblareamaster where strAreaName='All';";                   
            ResultSet rsAreaCode = st.executeQuery(sql);
            if (rsAreaCode.next()) {
                AreaCodeForAll = rsAreaCode.getString(1);
            }
            rsAreaCode.close();
            
            String sqlSetup="select strDirectAreaCode,strAreaWisePromotions,strAreaWisePricing "
  				   + " from tblsetup where (strPOSCode='"+POSCode+"' or strPOSCode='All' ) ";
  				   
			System.out.println(sql);
		    ResultSet rsSetupValues=stSetup.executeQuery(sqlSetup);
		    if(rsSetupValues.next())
	        {
	    	   areaWisePromotions=rsSetupValues.getString(2);
	    	   strAreaWisePricing=rsSetupValues.getString(3);
	        }
		    rsSetupValues.close(); 
            
            sql = " select a.strTableNo,a.strWaiterNo,LEFT(a.strItemCode,7),a.strItemName,sum(a.dblItemQuantity),  "
                	+ " sum(a.dblAmount),a.dblRate,a.strKOTNo,a.strPOSCode,a.strCounterCode,b.strAreaCode,a.strCardNo, "
                	+ " a.strHomeDelivery,a.strTakeAwayYesNo,a.strCustomerCode,a.tmeOrderProcessing,a.tmeOrderPickup,a.intPaxNo  "
                	+ " from tblitemrtemp a,tblmenuitempricingdtl b,tblitemmaster c "
                	+ " where a.strItemCode=b.strItemCode "
                	+ " and a.strItemCode=c.strItemCode and a.strPOSCode=b.strPosCode "
                	+ " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) ";
                	if(strAreaWisePricing.equalsIgnoreCase("N")){
                		sql += " OR b.strAreaCode ='"+AreaCodeForAll+"' ";
                	}
                    
                	sql += ")";
            
                if(!POSCode.equals("All"))
                {
                    sql+= " and a.strPOSCode='" + POSCode + "'";
                }
                if(!tableNo.equals("All"))
                {
                    sql+=" and a.strTableNo='"+tableNo+"'";
                }
                sql+=" and b.strHourlyPricing='No' ";
                sql+= "group by a.strItemCode "
                	+ "order by a.strSerialNo,a.strItemName  ";
           System.out.println("sql="+sql);
           String sqlAreaCode = "select strAreaCode from tbltablemaster where strTableNo='" + tableNo + "'";
           ResultSet rsArea= stArea.executeQuery(sqlAreaCode);
           if (rsArea.next())
           {
        	   areaCodeForPromo = rsArea.getString(1);
           }
           rsArea.close();    
 			
            List<clsItemDtlForTax> arrListItemDtls=new ArrayList<clsItemDtlForTax>();
            clsItemDtlForTax objItemDtl=null;
            JSONArray arrObj=new JSONArray();
            ResultSet rsTableBillData = st.executeQuery(sql);
            while (rsTableBillData.next()) 
            {
            	JSONObject obj=new JSONObject();
            	String itemCode=rsTableBillData.getString(3);
            	String itemName=rsTableBillData.getString(4);
			    double amount=Double.valueOf(rsTableBillData.getString(6));
			    double quantity=Double.valueOf(rsTableBillData.getString(5));
            	
			    if(applyPromotion.equals("Yes"))
			    {
			    	if(applyPromotionBill.equals("Yes"))
				    {
			    	  clsPromotionCalculation objPromoCalculation=new clsPromotionCalculation();
			    	  hmPromoItemDtl = objPromoCalculation.funCalculatePromotions("MakeKOT", "", "",new ArrayList(),POSStartDate,POSCode,tableNo,arrDirectBillerListItemDtls,areaWisePromotions,areaCodeForPromo);
				            
			            double rate = amount / quantity;
		        		double freeAmount = 0.00;
		        		
			            if (null != hmPromoItemDtl)
		                {
		                    if (hmPromoItemDtl.containsKey(itemCode))
		                    {
		                        if (null != hmPromoItemDtl.get(itemCode))
		                        {
		                            clsPromotionItems objPromoItemsDtl = hmPromoItemDtl.get(itemCode);
		                            if (objPromoItemsDtl.getPromoType().equals("ItemWise"))
		                            {
		                                double freeQty = objPromoItemsDtl.getFreeItemQty();
		                                if (freeQty > 0)
		                                {
		                                    freeAmount = freeAmount + (rate * freeQty);
		                                    amount = amount - freeAmount;
		                                    hmPromoItem.put(itemCode, objPromoItemsDtl);
		                                    hmPromoItemDtl.remove(itemCode);
		                                    hmPromoItemsToDisplay.put(itemCode + "!" + itemName, objPromoItemsDtl);
		                                    clsPromotionItems objPromoItem=new clsPromotionItems();
		                                    objPromoItem.setItemCode(itemCode);
		                                    objPromoItem.setPromoCode(objPromoItemsDtl.getPromoCode());
		                                    objPromoItem.setPromoType(objPromoItemsDtl.getPromoType());
		                                    objPromoItem.setBillAmt(String.valueOf(freeAmount));
		                                    objPromoItem.setFreeItemQty(freeQty);
		                                    arrListPromoItemDtl.add(objPromoItem);
		                                    
		                                }
		                            }
		                            else if (objPromoItemsDtl.getPromoType().equals("Discount"))
		                            {
		                                double discA = 0;
		                                double discP = 0;
		                                if (objPromoItemsDtl.getDiscType().equals("Value"))
		                                {
		                                    discA = objPromoItemsDtl.getDiscAmt();
		                                    discP = (discA / amount) * 100;
		                                    hmPromoItem.put(itemCode, objPromoItemsDtl);
		                                    hmPromoItemDtl.remove(itemCode);
		                                    hmPromoItemsToDisplay.put(itemCode + "!" + itemName, objPromoItemsDtl);

		                                    clsBillItemDtl objItemPromoDiscount = new clsBillItemDtl();
		                                    objItemPromoDiscount.setItemCode(itemCode);
		                                    objItemPromoDiscount.setItemName(itemName);
		                                    objItemPromoDiscount.setDiscountAmount(discA);
		                                    objItemPromoDiscount.setDiscountPercentage(discP);
		                                    //amount=amount-discA;
		                                    objItemPromoDiscount.setAmount(amount);

		                                    mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
		                                }
		                                else
		                                {
		                                    discP = objPromoItemsDtl.getDiscPer();
		                                    discA = (discP / 100) * amount;
		                                    //amount=amount-(amount*(disc/100));
		                                    hmPromoItem.put(itemCode, objPromoItemsDtl);
		                                    hmPromoItemsToDisplay.put(itemCode + "!" + itemName, objPromoItemsDtl);
		                                    //hmPromoItem.put(itemCode, discP+"#"+promoCode);
		                                    hmPromoItemDtl.remove(itemCode);
		                                    clsBillItemDtl objItemPromoDiscount = new clsBillItemDtl();
		                                    objItemPromoDiscount.setItemCode(itemCode);
		                                    objItemPromoDiscount.setItemName(itemName);
		                                    objItemPromoDiscount.setDiscountAmount(discA);
		                                    objItemPromoDiscount.setDiscountPercentage(discP);
		                                   // amount=amount-discA;
		                                    objItemPromoDiscount.setAmount(amount);

		                                    mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
		                                }
		                            }
		                        }
		                    }
		                    if(promoItemWithAmtAndQty.isEmpty())
	                        {
	                        	promoItemWithAmtAndQty=itemCode+"#"+itemName+"#"+String.valueOf(amount)+"#"+String.valueOf(quantity);
	                        }
	                        else
	                        {
	                        	promoItemWithAmtAndQty=promoItemWithAmtAndQty+"!"+itemCode+"#"+itemName+"#"+String.valueOf(amount)+"#"+String.valueOf(quantity);
	                        }
                    

		                }
				    }
			    }
            	
	            
            	obj.put("TableNo",rsTableBillData.getString(1));
            	obj.put("WaiterNo",rsTableBillData.getString(2));
            	obj.put("ItemCode",itemCode);
            	obj.put("ItemName",itemName);
            	obj.put("ItemQty",String.valueOf(quantity));
            	obj.put("Amount",String.valueOf(amount));
            	obj.put("Rate",rsTableBillData.getString(7));
            	obj.put("KOTNo",rsTableBillData.getString(8));
            	obj.put("POSCode",rsTableBillData.getString(9));
            	obj.put("CounterCode",rsTableBillData.getString(10));
            	obj.put("AreaCode",rsTableBillData.getString(11));
            	obj.put("CardNo",rsTableBillData.getString(12)); 
            	obj.put("CustomerCode",rsTableBillData.getString(15)); 
            	obj.put("OrderProcessTime",rsTableBillData.getString(16)); 
            	obj.put("OrderPickupTime",rsTableBillData.getString(17)); 
            	obj.put("PaxNo",rsTableBillData.getString(18)); 
            	itemTotal=itemTotal+rsTableBillData.getDouble(6);
            	
            	
            
            	arrObj.put(obj);
            	
            	Statement st2 = cmsCon.createStatement();
	        	sql=" select LEFT(a.strItemCode,7),a.strItemName,sum(a.dblItemQuantity),sum(a.dblAmount) "
	        	   + " from tblitemrtemp a  "
	        	   + " where a.strItemCode like '"+rsTableBillData.getString(3)+"M%' and a.strKOTNo='"+rsTableBillData.getString(8)+"'  "
	        	   + " group by a.strItemCode,a.strItemName ";
	        	
	        	System.out.println(sql);
	        	ResultSet rsKOTModItemDtl=st2.executeQuery(sql);
	        	while(rsKOTModItemDtl.next())
	        	{
	        		JSONObject jObjKOTItemModDtl=new JSONObject();
	        		
	        		jObjKOTItemModDtl.put("TableNo",rsTableBillData.getString(1));
	        		jObjKOTItemModDtl.put("WaiterNo",rsTableBillData.getString(2));
	        		jObjKOTItemModDtl.put("ItemCode",rsKOTModItemDtl.getString(1));
	        		jObjKOTItemModDtl.put("ItemName",rsKOTModItemDtl.getString(2));
	            	jObjKOTItemModDtl.put("ItemQty",rsKOTModItemDtl.getString(3));
	            	jObjKOTItemModDtl.put("Amount",rsKOTModItemDtl.getString(4));
	            	jObjKOTItemModDtl.put("Rate",rsTableBillData.getString(7));
	            	jObjKOTItemModDtl.put("KOTNo",rsTableBillData.getString(8));
	            	jObjKOTItemModDtl.put("POSCode",rsTableBillData.getString(9));
	            	jObjKOTItemModDtl.put("CounterCode",rsTableBillData.getString(10));
	            	jObjKOTItemModDtl.put("AreaCode",rsTableBillData.getString(11));
	            	jObjKOTItemModDtl.put("CardNo",rsTableBillData.getString(12)); 
	            	jObjKOTItemModDtl.put("CustomerCode",rsTableBillData.getString(15)); 
	            	jObjKOTItemModDtl.put("OrderProcessTime",rsTableBillData.getString(16)); 
	            	jObjKOTItemModDtl.put("OrderPickupTime",rsTableBillData.getString(17));
	            	jObjKOTItemModDtl.put("PaxNo",rsTableBillData.getString(18)); 
	            	modItemTotal=modItemTotal+rsKOTModItemDtl.getDouble(4);
	            	objItemDtl=new clsItemDtlForTax();
	            	objItemDtl.setItemCode(rsKOTModItemDtl.getString(1));
	                objItemDtl.setItemName(rsKOTModItemDtl.getString(2));
	                objItemDtl.setAmount(rsKOTModItemDtl.getDouble(4));
	                objItemDtl.setDiscAmt(0);
	                objItemDtl.setDiscPer(0);
	                arrListItemDtls.add(objItemDtl);
	            	
	        		
	            	arrObj.put(jObjKOTItemModDtl);
	        	}
	        	
	        	rsKOTModItemDtl.close();
	        	st2.close();
            	areaCode=rsTableBillData.getString(11);
            	if(rsTableBillData.getString(13).equals("Yes"))
            	{
            		operationTypeForTax="HomeDelivery";
            	}
            	else if(rsTableBillData.getString(14).equals("Yes"))
            	{
            		operationTypeForTax="TakeAway";
            	}
            	objItemDtl=new clsItemDtlForTax();
                objItemDtl.setItemCode(rsTableBillData.getString(3));
                objItemDtl.setItemName(rsTableBillData.getString(4));
                objItemDtl.setAmount(rsTableBillData.getDouble(6));
                objItemDtl.setDiscAmt(0);
                objItemDtl.setDiscPer(0);
                arrListItemDtls.add(objItemDtl);
               // subTotalForTax+=rsTableBillData.getDouble(6);
            }
            rsTableBillData.close();
            jObj.put("BillItemDetails", arrObj);
            System.out.println(arrListItemDtls);
            JSONArray jArrBilTaxDtl=new JSONArray();
                        
            Date dt=new Date();            
            String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate();      
            subTotalForTax=itemTotal+modItemTotal;
            clsTaxCalculation objTaxCalculation=new clsTaxCalculation();
            List <clsTaxCalculationDtls> arrListTaxDtl=objTaxCalculation.funCalculateTax(arrListItemDtls,POSCode
                , POSStartDate, areaCode, operationTypeForTax, subTotalForTax, 0,"");
            
            for(int cnt=0;cnt<arrListTaxDtl.size();cnt++)
            {
            	clsTaxCalculationDtls obj=arrListTaxDtl.get(cnt);
            	System.out.println("Tax Dtl= "+obj.getTaxCode()+"\t"+obj.getTaxName()+"\t"+obj.getTaxAmount());
            	JSONObject jObjTaxDtl=new JSONObject();
            	jObjTaxDtl.put("TaxCode", obj.getTaxCode());
            	jObjTaxDtl.put("TaxName", obj.getTaxName());
            	jObjTaxDtl.put("TaxType", obj.getTaxCalculationType());
            	jObjTaxDtl.put("TaxableAmount",obj.getTaxableAmount());
            	jObjTaxDtl.put("TaxAmount", Math.rint(obj.getTaxAmount()));
            	jArrBilTaxDtl.put(jObjTaxDtl);
            	
            }
            jObj.put("BillTaxDetails", jArrBilTaxDtl);
            
            JSONArray jArrPromoBillItemDtl=new JSONArray();
            
            for(int cnt=0;cnt<arrListPromoItemDtl.size();cnt++)
            {
            	clsPromotionItems obj=arrListPromoItemDtl.get(cnt);
            	JSONObject jObjPromoDtl=new JSONObject();
            	jObjPromoDtl.put("ItemCode", obj.getItemCode());
            	jObjPromoDtl.put("PromoCode", obj.getPromoCode());
            	jObjPromoDtl.put("PromoType", obj.getPromoType());
            	jObjPromoDtl.put("PromoAmt",obj.getBillAmt());
            	jObjPromoDtl.put("PromoQty", String.valueOf(obj.getFreeItemQty()));
            	jArrPromoBillItemDtl.put(jObjPromoDtl);
            	
            }
            
            JSONArray jArrPromoDiscBillItemDtl=new JSONArray();
            
            if(mapPromoItemDisc.size()>0)
            {
            	for (Map.Entry<String, clsBillItemDtl> entry : mapPromoItemDisc.entrySet())
                {
            		clsBillItemDtl objBill=entry.getValue();
            		JSONObject jObjDiscPromoDtl=new JSONObject();
            		jObjDiscPromoDtl.put("ItemCode", objBill.getItemCode());
            		jObjDiscPromoDtl.put("ItemName", objBill.getItemName());
            		jObjDiscPromoDtl.put("PromoType", "Discount");
            		jObjDiscPromoDtl.put("DiscountAmt", objBill.getDiscountAmount());
            		jObjDiscPromoDtl.put("DiscountOnAmt", objBill.getAmount());
            		jObjDiscPromoDtl.put("DiscountPer",objBill.getDiscountPercentage());
            		jArrPromoDiscBillItemDtl.put(jObjDiscPromoDtl);
                }
            }
       
            jObj.put("BillTaxDetails", jArrBilTaxDtl);
            jObj.put("BillPromoDetails", jArrPromoBillItemDtl);
            jObj.put("BillPromoDiscDetails", jArrPromoDiscBillItemDtl);
            
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return jObj;
        }
    }

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funCalculateTax")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funCalculateTax(JSONObject objKOTTaxData)
	{
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        Statement st2=null;
		String taxAmt="0";
	    double subTotalForTax=0;
		double taxAmount=0.0;
		JSONObject jsTaxDtl=new JSONObject(); 
		try {
			
			String posCode="",areaCode="",operationType="",clientCode="";
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        st2 = cmsCon.createStatement();
	        List<clsItemDtlForTax> arrListItemDtls=new ArrayList<clsItemDtlForTax>();
			JSONArray mJsonArray=(JSONArray)objKOTTaxData.get("TaxDtl");
			String sql="";
		    String posDate="";
			ResultSet rs=null;
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				clsItemDtlForTax objItemDtl=new clsItemDtlForTax();
			    mJsonObject =(JSONObject) mJsonArray.get(i);
			    String itemName=mJsonObject.get("strItemName").toString();
			    String itemCode=mJsonObject.get("strItemCode").toString();
			    System.out.println(itemName);
			    double amt=Double.parseDouble(mJsonObject.get("dblAmount").toString());
			    operationType=mJsonObject.get("OperationType").toString();
			    posCode=mJsonObject.get("strPOSCode").toString();
			    areaCode=mJsonObject.get("AreaCode").toString();
			    posDate=mJsonObject.get("POSDate").toString();
			    clientCode=mJsonObject.get("strClientCode").toString();
			    if(areaCode.equals("")){
			    	sql="select strDirectAreaCode from tblsetup where strPOSCode='"+posCode+"' and strClientCode='"+clientCode+"'";
			        rs=st.executeQuery(sql);
					    while(rs.next())
					    {
					    	areaCode=rs.getString(1);
						}
			    }
			   
                objItemDtl.setItemCode(itemCode);
                objItemDtl.setItemName(itemName);
                objItemDtl.setAmount(amt);
                objItemDtl.setDiscAmt(0);
                objItemDtl.setDiscPer(0);
                arrListItemDtls.add(objItemDtl);
                subTotalForTax+=amt;
			   // tableNo=mJsonObject.get("strTableNo").toString();
			    
			}
			
			   Date dt=new Date();            
	            String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate();            
	            clsTaxCalculation objTaxCalculation=new clsTaxCalculation();
	            List <clsTaxCalculationDtls> arrListTaxDtl=objTaxCalculation.funCalculateTax(arrListItemDtls,posCode
	                , posDate, areaCode, operationType, subTotalForTax, 0,"");
	            JSONArray jAyyTaxList=new JSONArray();
	            JSONObject jsTax; 
	            for(int cnt=0;cnt<arrListTaxDtl.size();cnt++)
	            {
	            	jsTax=new JSONObject(); 
	            	clsTaxCalculationDtls obj=arrListTaxDtl.get(cnt);
	            	System.out.println("Tax Dtl= "+obj.getTaxCode()+"\t"+obj.getTaxName()+"\t"+obj.getTaxAmount());
	            	taxAmount+=obj.getTaxAmount();
	            	taxAmt=String.valueOf(taxAmount);
	            	jsTax.put("TaxName", obj.getTaxName());
	            	jsTax.put("TaxAmt", obj.getTaxAmount());
	            	jsTax.put("taxCode", obj.getTaxCode());
	            	jsTax.put("taxCalculationType", obj.getTaxCalculationType());
	            	jsTax.put("taxableAmount", obj.getTaxableAmount());
	            	
	            	jAyyTaxList.put(jsTax);
	            }
	            jsTaxDtl.put("listOfTax", jAyyTaxList);
	            jsTaxDtl.put("totalTaxAmt", taxAmt);
	           
	            st.close(); 
	            st2.close();
	            cmsCon.close(); 

            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return jsTaxDtl;//Response.status(201).entity(jsTaxDtl).build();		
	}
	
	
	
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funInsertBillTaxDtl")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funInsertBillTaxDtl(JSONObject objKOTTaxData)
	{
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        Statement st2=null;
		String taxAmt="";
	    double subTotalForTax=0;
		double taxAmount=0.0;
		String insert_qry="";
		 JSONObject jObj=new JSONObject();     
		try {
			
			String tableNo="",posCode="",areaCode="",operationType="";
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        st2 = cmsCon.createStatement();
	        List<clsItemDtlForTax> arrListItemDtls=new ArrayList<clsItemDtlForTax>();
			JSONArray mJsonArray=(JSONArray)objKOTTaxData.get("InsertTaxDtl");
			String sql="";
		    String insertQuery1="";
		    int paxNo=0;
		    String BillNo="";
		    String ClientCode="";
		    String billDate="";
		    int res;
			boolean flgData=false;
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				clsItemDtlForTax objItemDtl=new clsItemDtlForTax();
			    mJsonObject =(JSONObject) mJsonArray.get(i);
			    BillNo=mJsonObject.get("strBillNo").toString();
			    ClientCode=mJsonObject.get("strClientCode").toString();
			    String itemName=mJsonObject.get("strItemName").toString();
			    String itemCode=mJsonObject.get("strItemCode").toString();
			    System.out.println(itemName);
			    double amt=Double.parseDouble(mJsonObject.get("dblAmount").toString());
			    billDate=mJsonObject.get("BillDate").toString();
		
                objItemDtl.setItemCode(itemCode);
                objItemDtl.setItemName(itemName);
                objItemDtl.setAmount(amt);
                objItemDtl.setDiscAmt(0);
                objItemDtl.setDiscPer(0);
                arrListItemDtls.add(objItemDtl);
                subTotalForTax+=amt;
			   
			    
			   // tableNo=mJsonObject.get("strTableNo").toString();
			    posCode=mJsonObject.get("strPOSCode").toString();
			   
			    sql="select strOperationType,strAreaCode from tblbillhd where strBillNo='"+BillNo+"' ";
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
			    {
				   operationType=rs.getString(1);
				   areaCode=rs.getString(2);
				   
				   if(operationType.equals("DirectBiller"))
				   {
					   operationType="DineIn";
				   }
				   
				   
			    }
			    
			    rs.close();
			    
			}
			
			   Date dt=new Date();            
	            String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate();            
	            clsTaxCalculation objTaxCalculation=new clsTaxCalculation();
	            List <clsTaxCalculationDtls> arrListTaxDtl=objTaxCalculation.funCalculateTax(arrListItemDtls,posCode
	                , date, areaCode, operationType, subTotalForTax, 0,"");
	            
	            insert_qry = "INSERT INTO `tblbilltaxdtl` (`strBillNo`, `strTaxCode`,"
	    				+ " `dblTaxableAmount`, `dblTaxAmount`, `strClientCode`, "
	    				+ "`strDataPostFlag`,`dteBillDate`) VALUES";
	            
	         
	            sql="";
	            for(int cnt=0;cnt<arrListTaxDtl.size();cnt++)
	            {
	            	String deleteSql="delete from tblbilltaxdtl "
	    			    	+ "where strBillNo='"+BillNo+"' and strClientCode='"+ClientCode+"'";
	    			st.executeUpdate(deleteSql);
	    			
	            	clsTaxCalculationDtls obj=arrListTaxDtl.get(cnt);
	            	System.out.println("Tax Dtl= "+obj.getTaxCode()+"\t"+obj.getTaxName()+"\t"+obj.getTaxAmount());
	            	taxAmt+=obj.getTaxAmount();
	                sql+=",('"+BillNo+"','"+obj.getTaxCode()+"','"+obj.getTaxableAmount()+"','"+obj.getTaxAmount()+"','"
	    				    +ClientCode+"','N','"+billDate+"')";				    
	    			flgData=true;
	    			
	    		 	
	            }
	            
	            if(flgData)
				{
					sql=sql.substring(1,sql.length());
			        insert_qry+=" "+sql;
			    	System.out.println("Query="+insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
	      
	       jObj.put("taxAmount", taxAmt);
	       cmsCon.close(); 
           st.close(); 
           st2.close();
         
	       
            
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObj;//Response.status(201).entity(taxAmt).build();
	}
	
	
	
	
	@GET 
	@Path("/funGetBillList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetBillList(@QueryParam("ClientCode") String clientCode,@QueryParam("POSCode")String posCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        //JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = "SELECT a.strBillNo,a.dteBillDate,IFNULL(d.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(f.strDPName,''),a.dblGrandTotal,ifnull(g.strTableNo,''),ifnull(g.strTableName,'') "
            	+ " FROM tblbillhd a "
            	+ "left outer join tblhomedeldtl b on a.strBillNo=b.strBillNo "
            	+ "LEFT OUTER JOIN tblcustomermaster d ON a.strCustomerCode=d.strCustomerCode "
            	+ "left outer join tblbuildingmaster e on d.strBuldingCode=e.strBuildingCode "
            	+ "left outer join tbldeliverypersonmaster  f on  f.strDPCode=b.strDPCode "
            	+ "left outer join tbltablemaster g on a.strTableNo=g.strTableNo  "
            	+ "WHERE a.strBillNo NOT IN (SELECT strBillNo FROM tblbillsettlementdtl) "
            	+ "AND a.strPOSCode='"+posCode+"' ";
          
            System.out.println(sql);
            
            
           
            ResultSet rsBillList = st.executeQuery(sql);
            while (rsBillList.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("BillNo",rsBillList.getString(1));
            	obj.put("BillDate",rsBillList.getString(2));
            	obj.put("CustName",rsBillList.getString(3));
            	obj.put("AreaName",rsBillList.getString(4));
            	obj.put("DPName",rsBillList.getString(5));
            	obj.put("GrandTotal",rsBillList.getString(6));
            	obj.put("TableNo",rsBillList.getString(7));
            	obj.put("TableName",rsBillList.getString(8));
            
            	
            	arrObj.put(obj);
            }
            rsBillList.close();
          //  jObj.put("BillDetails", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	
	@GET 
	@Path("/funGetBillDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetBillDtl(@QueryParam("ClientCode") String clientCode,@QueryParam("BillNo") String billNo)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        Statement st1=null;
        Statement st2=null;
        Statement st3=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            st2 = cmsCon.createStatement();
            st3 = cmsCon.createStatement();
            String sql="";
            
            sql = " select a.strBillNo,a.dteBillDate,a.strPOSCode,a.strTableNo,a.dblSubTotal,a.dblGrandTotal, "
            		+ " a.dblDiscountAmt,a.dblTaxAmt,a.strWaiterNo,c.strPosName,ifnull(b.strTableName,''), "
            		+ " ifnull(a.strOperationType,''),ifnull(a.strCustomerCode,''),ifnull(d.strCustomerName,'') ,a.strCardNo  "
            		+ " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo  "
            		+ " left outer join tblcustomermaster d on left(a.strCustomerCode,8)=d.strCustomerCode  "
            		+ " left outer join tblposmaster c on a.strPOSCode=c.strPosCode   "
            		+ " where a.strBillNo='"+billNo+"' ";
		
            System.out.println(sql);
            
            
            
            ResultSet rsBillList = st.executeQuery(sql);
            while (rsBillList.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("BillNo",rsBillList.getString(1));
            	obj.put("BillDate",rsBillList.getString(2));
            	obj.put("POSCode",rsBillList.getString(3));
            	obj.put("TableNo",rsBillList.getString(4));
            	obj.put("SubTotal",rsBillList.getString(5));
            	obj.put("GrandTotal",rsBillList.getString(6));
            	obj.put("Discount",rsBillList.getString(7));
            	obj.put("TaxAmt",rsBillList.getString(8));
            	obj.put("WaiterNo",rsBillList.getString(9));
            	obj.put("POSName",rsBillList.getString(10));
            	obj.put("TableName",rsBillList.getString(11));
            	obj.put("OperationType",rsBillList.getString(12));
            	obj.put("CustomerCode",rsBillList.getString(13));
               	obj.put("CustomerName",rsBillList.getString(14));
             	obj.put("CardNo",rsBillList.getString(15));
            	
            	
             	String sqlCard="select a.strCardNo from tbldebitcardmaster a where a.strCardNo='"+rsBillList.getString(15)+"'";
             	ResultSet rsCard = st3.executeQuery(sqlCard);
             	if(rsCard.next())
             	{
             		obj.put("CardType","CashCard");
             	}
             	else
             	{
             		obj.put("CardType","Member");
             	}
             	rsCard.close();
             	st3.close();
             	
             	
            	String sql1= " select a.strItemCode,a.strItemName,a.dblQuantity,a.dblAmount  "
                           + " from tblbilldtl a "
                           + " where a.strBillNo='"+rsBillList.getString(1)+"' "  ;
            	
            	System.out.println("sql="+sql1) ;  
            	
    	            JSONArray arrjObjItem = new JSONArray();
    	            ResultSet rsItemList = st1.executeQuery(sql1);
    	            while (rsItemList.next()) 
    			    {
    	            	JSONObject jObjItemRows=new JSONObject();
    	            	jObjItemRows.put("ItemCode",rsItemList.getString(1));
    	            	jObjItemRows.put("ItemName",rsItemList.getString(2));
    	            	jObjItemRows.put("ItemQty",rsItemList.getString(3));
    	            	jObjItemRows.put("ItemAmt",rsItemList.getString(4));
    	            	arrjObjItem.put(jObjItemRows);
    			    
    			    
    	            	String sqlModifier= " select a.strItemCode,a.strModifierName,a.dblQuantity,a.dblAmount  "
    	                            + " from tblbillmodifierdtl a "
    	                            + " where a.strItemCode='"+rsItemList.getString(1)+"' and a.strBillNo='"+rsBillList.getString(1)+"' "  ;
    	             	
    	     	        System.out.println("sqlModifier="+sqlModifier);   
    	     	      
    	     	        ResultSet rsModItemList = st2.executeQuery(sqlModifier);
    	     	        while (rsModItemList.next()) 
    	     			  {
    	     	            	JSONObject jObjModItemRows=new JSONObject();
    	     	            	jObjModItemRows.put("ItemCode",rsModItemList.getString(1));
    	     	            	jObjModItemRows.put("ItemName",rsModItemList.getString(2));
    	     	            	jObjModItemRows.put("ItemQty",rsModItemList.getString(3));
    	     	            	jObjModItemRows.put("ItemAmt",rsModItemList.getString(4));
    	     	            	arrjObjItem.put(jObjModItemRows);
    	     			  }
    	     	        rsModItemList.close();
    	     	     }
    	            
    	            rsItemList.close();
    	            obj.put("ItemRows",arrjObjItem);
    	         
            	
            	arrObj.put(obj);
            }
            rsBillList.close();
            jObj.put("BillNoDetails", arrObj);
            st.close();
            st1.close();
            st2.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	
	@GET
	@Path("/funGetItemPriceDtlCounterWise")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetItemPriceDtlCounterWise(@QueryParam("POSCode") String POSCode
    	, @QueryParam("areaCode") String areaCode, @QueryParam("areaWisePricing") String areaWisePricing
    	, @QueryParam("menuHeadCode") String menuHeadCode, @QueryParam("fromDate") String fromDate
    	, @QueryParam("toDate") String toDate ,@QueryParam("flgAllItems") boolean flgAllItems
    	, @QueryParam("counterCode") String counterCode)
	{
		return funFetchItemPriceDtlCounterWise(areaWisePricing, menuHeadCode, areaCode, POSCode, fromDate, toDate,flgAllItems
				,counterCode);
	}
	
	private JSONArray funFetchItemPriceDtlCounterWise(String areaWisePricing, String menuHeadCode, String areaCode, String POSCode
			, String fromDate, String toDate, boolean flgAllItems, String counterCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        String gAreaCodeForTrans="";
        JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sqlArea = "select strAreaCode from tblareamaster where strAreaName='All'";
	        ResultSet rsArea=st.executeQuery(sqlArea);
	        if (rsArea.next())
	        {
	            gAreaCodeForTrans = rsArea.getString(1);
	        }
	        rsArea.close();
        
		String sql="";
		if (areaWisePricing.equals("N")) {
			
			sql="SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,a.strPriceWednesday"
					+ ",a.strPriceThursday,a.strPriceFriday,a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom"
					+ ",a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,a.strCostCenterCode,a.strHourlyPricing"
					+ ",a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate "
					+ "FROM tblmenuitempricingdtl a ,tblitemmaster b, tblcounterdtl d "
					+ "WHERE  (a.strAreaCode='"+areaCode+"' or a.strAreaCode='"+gAreaCodeForTrans+"' ) and a.strItemCode=b.strItemCode and a.strMenuCode=d.strMenuCode "
					+ " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All') "
	                + " and date(a.dteFromDate)<='"+fromDate+"' and date(a.dteToDate)>='"+toDate+"' ";
			
			if(!counterCode.equals("All")){
				sql=sql + "and d.strCounterCode='"+counterCode+"'";
			}
	              
            
            if(!flgAllItems)
            {
            	sql+=" and a.strMenuCode = '" + menuHeadCode + "' ";
            }
            sql+=" order by b.strItemName ";
        }
		else 
        {
			sql="SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
		            + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday,"
		            + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
		            + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate "
					+ "FROM tblmenuitempricingdtl a ,tblitemmaster b, tblcounterdtl d "
					+ "WHERE  (a.strAreaCode='"+areaCode+"' or a.strAreaCode='"+gAreaCodeForTrans+"' ) and a.strItemCode=b.strItemCode and a.strMenuCode=d.strMenuCode "
					+ " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All') "
	                + " and date(a.dteFromDate)<='"+fromDate+"' and date(a.dteToDate)>='"+toDate+"' "
					+ "and d.strCounterCode='"+counterCode+"'";
			
			if(!flgAllItems)
	        {
				sql+=" and a.strMenuCode = '" + menuHeadCode + "' ";
	        }
	        sql+=" order by b.strItemName ";
        }
		
		//System.out.println(sql);
		
		
        
        	 
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("ItemCode",rsMasterData.getString(1));
            	obj.put("ItemName",rsMasterData.getString(2));
            	obj.put("TextColor",rsMasterData.getString(3));
            	obj.put("PriceMonday",rsMasterData.getString(4));
            	obj.put("PriceTuesday",rsMasterData.getString(5));
            	obj.put("PriceWenesday",rsMasterData.getString(6));
            	obj.put("PriceThursday",rsMasterData.getString(7));
            	obj.put("PriceFriday",rsMasterData.getString(8));
            	obj.put("PriceSaturday",rsMasterData.getString(9));
            	obj.put("PriceSunday",rsMasterData.getString(10));
            	obj.put("TimeFrom",rsMasterData.getString(11));
            	obj.put("AMPMFrom",rsMasterData.getString(12));
            	obj.put("TimeTo",rsMasterData.getString(13));
            	obj.put("AMPMTo",rsMasterData.getString(14));
            	obj.put("CostCenterCode",rsMasterData.getString(15));
            	obj.put("HourlyPricing",rsMasterData.getString(16));
            	obj.put("SubMenuHeadCode",rsMasterData.getString(17));
            	obj.put("FromDate",rsMasterData.getString(18));
            	obj.put("ToDate",rsMasterData.getString(19));
           
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
            //jObj.put("tblmenuitempricingdtl", arrObj);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;//jObj.toString();
	}
	

	
	@GET
	@Path("/funGetItemPriceDtl")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetItemPriceDtl(@QueryParam("POSCode") String POSCode
    	, @QueryParam("areaCode") String areaCode, @QueryParam("areaWisePricing") String areaWisePricing
    	, @QueryParam("menuHeadCode") String menuHeadCode, @QueryParam("fromDate") String fromDate
    	, @QueryParam("toDate") String toDate ,@QueryParam("flgAllItems") boolean flgAllItems,@QueryParam("menuType") String menuType,@QueryParam("tableNo") String tableNo)
	{
		return funFetchItemPriceDtl(areaWisePricing, menuHeadCode, areaCode, POSCode, fromDate, toDate,flgAllItems,menuType,tableNo);
	}
	
	private JSONArray funFetchItemPriceDtl(String areaWisePricing, String menuHeadCode, String areaCode, String POSCode
			, String fromDate, String toDate, boolean flgAllItems,String menuType,String tableNo)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        String gAreaCodeForTrans="";
        JSONArray arrObj=new JSONArray();
        System.out.println("flg="+flgAllItems);
        System.out.println("FD="+fromDate);
        System.out.println("TD="+toDate);
        System.out.println("menuhd="+menuHeadCode);
        System.out.println("AreaWisePricing="+areaWisePricing);
        System.out.println("Areacode="+areaCode);
        System.out.println("POS="+POSCode);
        
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
        	
            String sqlArea = "select strAreaCode from tblareamaster where strAreaName='All'";
	        ResultSet rsArea=st.executeQuery(sqlArea);
	        if (rsArea.next())
	        {
	            gAreaCodeForTrans = rsArea.getString(1);
	        }
	     
	        if(!tableNo.isEmpty())
	        {
	        	String sqlTableArea = "select a.strAreaCode from tbltablemaster a,tblareamaster b "
	        			+ " where a.strTableNo='"+tableNo+"' and a.strAreaCode=b.strAreaCode";
	        	ResultSet rsTableArea=st1.executeQuery(sqlTableArea);
	 	        if (rsTableArea.next())
	 	        {
	 	        	areaCode = rsTableArea.getString(1);
	 	        }
	 	       rsTableArea.close();
	        }
	        
	        
	       
	        
			String sql="",sqlImg="";
			if (areaWisePricing.equals("N")) {
				sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
		                + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
		                + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
		                + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strExternalCode,b.strItemImage  "
		                + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
		                + " WHERE a.strItemCode=b.strItemCode and (a.strAreaCode='"+areaCode+"' or a.strAreaCode='"+gAreaCodeForTrans+"' ) "
		                + " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All') "
		                + " and date(a.dteFromDate)<='"+fromDate+"' and date(a.dteToDate)>='"+toDate+"' ";
	            
	            if(!flgAllItems)
	            {
	            	if(menuType.equals("MenuHead"))
	            	{
	            		sql+=" and a.strMenuCode = '" + menuHeadCode + "' ";
	            	}
	            	else
	            	{
	            		sql+=" and a.strSubMenuHeadCode = '" + menuHeadCode + "' ";
	            	
	            	}
	            	
	            }
	            sql+=" order by b.strItemName asc ";
	        }
			else 
	        {
				sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
		                + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday,"
		                + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
		                + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strExternalCode,b.strItemImage  "
		                + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
		                + " WHERE  (a.strAreaCode='"+areaCode+"' or a.strAreaCode='"+gAreaCodeForTrans+"' ) and a.strItemCode=b.strItemCode "
		                + " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All') "
		                + " and date(a.dteFromDate)<='"+fromDate+"' and date(a.dteToDate)>='"+toDate+"' ";
				
				if(!flgAllItems)
		        {
					sql+=" and a.strMenuCode = '" + menuHeadCode + "' ";
		        }
		        sql+=" order by b.strItemName asc ";
	        }
			
			System.out.println(sql);
		     
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("ItemCode",rsMasterData.getString(1));
            	obj.put("ItemName",rsMasterData.getString(2));
            	obj.put("TextColor",rsMasterData.getString(3));
            	obj.put("PriceMonday",rsMasterData.getString(4));
            	obj.put("PriceTuesday",rsMasterData.getString(5));
            	obj.put("PriceWenesday",rsMasterData.getString(6));
            	obj.put("PriceThursday",rsMasterData.getString(7));
            	obj.put("PriceFriday",rsMasterData.getString(8));
            	obj.put("PriceSaturday",rsMasterData.getString(9));
            	obj.put("PriceSunday",rsMasterData.getString(10));
            	obj.put("TimeFrom",rsMasterData.getString(11));
            	obj.put("AMPMFrom",rsMasterData.getString(12));
            	obj.put("TimeTo",rsMasterData.getString(13));
            	obj.put("AMPMTo",rsMasterData.getString(14));
            	obj.put("CostCenterCode",rsMasterData.getString(15));
            	obj.put("HourlyPricing",rsMasterData.getString(16));
            	obj.put("SubMenuHeadCode",rsMasterData.getString(17));
            	obj.put("FromDate",rsMasterData.getString(18));
            	obj.put("ToDate",rsMasterData.getString(19));
            	obj.put("ExternalCode",rsMasterData.getString(20));
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
          //  jObj.put("tblmenuitempricingdtl", arrObj);
            st.close();
            cmsCon.close();
            st1.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;//jObj.toString();
	}
	
	
	
	@GET
	@Path("/funGetItemPriceDtlForCustomerOrder")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetItemPriceDtlForCustomerOrder(@QueryParam("POSCode") String POSCode
    	, @QueryParam("areaCode") String areaCode, @QueryParam("areaWisePricing") String areaWisePricing
    	, @QueryParam("menuHeadCode") String menuHeadCode, @QueryParam("fromDate") String fromDate
    	, @QueryParam("toDate") String toDate ,@QueryParam("flgAllItems") boolean flgAllItems,@QueryParam("menuType") String menuType)
	{
		return objAPOSUtility.funFetchItemPriceDtlForCustomerOrder(areaWisePricing, menuHeadCode, areaCode, POSCode, fromDate, toDate,flgAllItems,menuType);
	}
	
	
	
	@GET
	@Path("/funGetMenuHeadList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetMenuHeadList(@QueryParam("POSCode") String POSCode,@QueryParam("clientCode") String clientCode )
	{
		return funFetchMenuHeadList(POSCode, clientCode);
	}
	
	private JSONObject funFetchMenuHeadList(String posCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONObject jObj=new JSONObject();
		//String sql="select strMenuCode,strMenuName from tblmenuhd where strClientCode='"+clientCode+"'";
        String sql="select distinct(a.strMenuCode),b.strMenuName "
        		+ " from tblmenuitempricingdtl a left outer join tblmenuhd b on a.strMenuCode=b.strMenuCode "
        		+ " where a.strPosCode='"+posCode+"' "
        		+ " and b.strOperational='Y' "
        		+ " and b.strClientCode='"+clientCode+"' ";
		
		JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("MenuCode",rsMasterData.getString(1));
            	obj.put("MenuName",rsMasterData.getString(2));
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
            jObj.put("MenuHeadList", arrObj);
            st.close();
            cmsCon.close();
            st1.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return jObj;
      //  jObj.toString();
	}
	
	
	@GET
	@Path("/funGetSubMenuList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetSubMenuList(@QueryParam("POSCode") String POSCode,@QueryParam("clientCode") String clientCode,@QueryParam("menuCode") String menuCode )
	{
		return funFetchSubMenuHeadList(POSCode, clientCode,menuCode);
	}
	
	private JSONObject funFetchSubMenuHeadList(String posCode,String clientCode,String menuCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
	    String sql="select b.strSubMenuHeadName,Trim(a.strSubMenuHeadCode) "
          + " from tblmenuitempricingdtl a "
          + " left outer join tblsubmenuhead b  on a.strSubMenuHeadCode=b.strSubMenuHeadCode "
          + " and a.strMenuCode=b.strMenuCode  "
          + " where b.strSubMenuHeadName is not null and b.strSubMenuOperational='Y'  "
          + " and a.strPosCode='"+posCode+"' "
          + " and a.strMenuCode='"+menuCode+"' group by a.strSubMenuHeadCode ";
	    
	    System.out.println("Sub Menu List:-"+sql);
		
		JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("SubMenuCode",rsMasterData.getString(2));
            	obj.put("SubMenuName",rsMasterData.getString(1));
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
            jObj.put("SubMenuList", arrObj);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return jObj;
        //jObj.toString();
	}
	
	
	
	@GET
	@Path("/funGetMenuHeadListForCustomerOrder")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetMenuHeadListForCustomerOrder(@QueryParam("POSCode") String POSCode,@QueryParam("clientCode") String clientCode )
	{
		return objAPOSUtility.funFetchMenuHeadListForCustomerOrder(POSCode, clientCode);
	}
	
	@GET
	@Path("/funGetCounterWiseMenu")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetCounterWiseMenuList(@QueryParam("POSCode") String POSCode,@QueryParam("CounterCode") String counterCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
		String sql="select a.strMenuCode,a.strMenuName "
			+ " from tblmenuhd a,tblcounterdtl b , tblcounterhd c "
			+ " where a.strMenuCode=b.strMenuCode and b.strCounterCode=c.strCounterCode and c.strOperational='Yes' "
			+ " and b.strCounterCode='"+counterCode+"' and c.strPOSCode='"+POSCode+"' " ;
		
		JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("MenuCode",rsMasterData.getString(1));
            	obj.put("MenuName",rsMasterData.getString(2));
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
            jObj.put("MenuHeadList", arrObj);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return jObj;
	}
	
	
	
	@GET
	@Path("/funGetModifierList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetModifierList(@QueryParam("ItemCode") String itemCode,@QueryParam("clientCode") String clientCode )
	{
		return objAPOSUtility.funFetchModifierList(itemCode, clientCode);
	}

	
	@GET
	@Path("/funGetPosMaster")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetPosMaster(@QueryParam("UserCode") String UserCode,@QueryParam("clientCode") String clientCode)
	{
		return funFetchPosMaster(UserCode,clientCode);
	}
	
	private JSONArray funFetchPosMaster(String UserCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection aposCon=null;
        Statement st=null;
        Statement st1=null;
        Statement st2=null;
        Statement st3=null;
        Statement st4=null;
        String sql="";
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
       
		try
		{
			aposCon=objDb.funOpenAPOSCon("mysql","master");
	        st = aposCon.createStatement();
	        st1 = aposCon.createStatement();
	        st2=aposCon.createStatement();
	        st3=aposCon.createStatement();
	        st4=aposCon.createStatement();
	        String sqlUserPOS="select strUserName,strSuperType,strPOSAccess "
	        		+ " from tbluserhd where strUserCode='"+UserCode+"' ";
	        
	        ResultSet rsPOS=st.executeQuery(sqlUserPOS);
	        if(rsPOS.next())
	        {
	        	String []arrPOSNamList=rsPOS.getString(3).split(",");
	            for(int cnt=0;cnt<arrPOSNamList.length;cnt++)
                {   System.out.println(arrPOSNamList[cnt]);
    		    	JSONObject obj=new JSONObject();
    		    	sql=" select * from tblposmaster a where a.strPosCode='"+arrPOSNamList[cnt]+"' ";
    		    	System.out.println(sql);
    		    	
    		    	 ResultSet rsMasterData=st.executeQuery(sql);
    		 	     while(rsMasterData.next())
    		 	        {
    		 	        	obj.put("PosCode",rsMasterData.getString(1));
    		 	           	obj.put("PosName",rsMasterData.getString(2));
    		 	           	obj.put("PosType",rsMasterData.getString(3));
    		 	           	obj.put("Counter",rsMasterData.getString(10));
    		 	           	obj.put("PrintVatNo",rsMasterData.getString(15));
    		 	           	obj.put("PrintServiceTaxNo",rsMasterData.getString(16));
    		 	           	obj.put("VatNo",rsMasterData.getString(17));
    		 	           	obj.put("ServiceTaxNo",rsMasterData.getString(18));
    		 	           
    		 	           	//Settlement Details
    		 	           	String sql1=" select a.strSettlementCode,a.strSettlementDesc,b.strSettelmentType,b.strBillPrintOnSettlement "
    		 	        		+ "from tblpossettlementdtl a,tblsettelmenthd b "
    		 	            	+ "where a.strSettlementCode=b.strSettelmentCode "
    		 	            	+ "and a.strPosCode='"+rsMasterData.getString(1)+"' ";
    		 	            
    		 	            JSONArray arrjObjSettle = new JSONArray();
    		 	            ResultSet rsSettle = st1.executeQuery(sql1);
    		 	            while (rsSettle.next()) 
    		 			    {
    		 	            	JSONObject jObjSettleRows=new JSONObject();
    		 			        jObjSettleRows.put("SettlementCode",rsSettle.getString(1));
    		 			        jObjSettleRows.put("SettlementDesc",rsSettle.getString(2));
    		 			        jObjSettleRows.put("SettlementType",rsSettle.getString(3));
    		 			        jObjSettleRows.put("BillPrintOnSettlementYN",rsSettle.getString(4));
    		 			        arrjObjSettle.put(jObjSettleRows);
    		 			    }
    		 	            rsSettle.close();
    		 	            obj.put("SettleRows",arrjObjSettle);
    		 	            	
    		 	            
    		             // Counter Details
    		 	            	
    		             	String sql2=  " select a.strCounterCode,a.strCounterName,a.strUserCode from tblcounterhd a  "
    		                        + " where a.strPosCode='"+rsMasterData.getString(1)+"' and a.strOperational='Yes' ;";
    		 	            	
    		             	JSONArray arrjObjCounter= new JSONArray();
    		             	ResultSet rsCounter = st2.executeQuery(sql2);
    		             	while (rsCounter.next()) 
    		 		        {
    		             		JSONObject jObjCounetrRows=new JSONObject();
    		             		jObjCounetrRows.put("CounterCode",rsCounter.getString(1));
    		             		jObjCounetrRows.put("CounterName",rsCounter.getString(2));
    		             		jObjCounetrRows.put("strUserCode",rsCounter.getString(3));
    		             		
    		             		String sql3= "   select b.strMenuCode,b.strMenuName  "
    		        				  +  "   from tblcounterdtl a,tblmenuhd b    "
    		        				  +  "   where a.strCounterCode='"+rsCounter.getString(1)+"'  "
    		        				  +  "  and a.strMenuCode=b.strMenuCode  ";
    		             		//System.out.println(sql3);
    		 	            		
    		             		JSONArray arrjObjMenu = new JSONArray();
    		 		            ResultSet rsMenu = st3.executeQuery(sql3);
    		 		            while (rsMenu.next()) 
    		 				    {
    		 		            	JSONObject jObjMenuRows=new JSONObject();
    		 		            	jObjMenuRows.put("strMenuCode",rsMenu.getString(1));
    		 		            	jObjMenuRows.put("strMenuName",rsMenu.getString(2));
    		 		            	arrjObjMenu.put(jObjMenuRows);
    		 				    }
    		 		            rsMenu.close();
    		 		            jObjCounetrRows.put("arrListMemuDtl",arrjObjMenu);
    		 		            arrjObjCounter.put(jObjCounetrRows);
    		 		        }
    		 	            rsCounter.close();
    		 	            obj.put("CounterDetails",arrjObjCounter);
    		 	            
    		 	            //Tax Details
    		 	            
    		 	        	String sql4= " select a.strTaxCode,a.strTaxDesc,b.strTaxType "
    		 						   + " from tbltaxposdtl a,tbltaxhd b "
    		 						   + " where a.strTaxCode=b.strTaxCode "
    		 						   + " and a.strPosCode='"+rsMasterData.getString(1)+"' ";
    		 		            
    		 		            JSONArray arrjObjTax = new JSONArray();
    		 		            ResultSet rsTax= st4.executeQuery(sql4);
    		 		            while (rsTax.next()) 
    		 				    {
    		 		            	JSONObject jObjTaxRows=new JSONObject();
    		 		            	jObjTaxRows.put("TaxCode",rsTax.getString(1));
    		 		            	jObjTaxRows.put("TaxDesc",rsTax.getString(2));
    		 		            	jObjTaxRows.put("TaxType",rsTax.getString(3));
    		 				        arrjObjTax.put(jObjTaxRows);
    		 				    }
    		 		            rsTax.close();
    		 		            obj.put("TaxRows",arrjObjTax);
    		 		            obj.put("Status","Success");
    	    		 	       
    	    		 	        
    	    		 	        //pos date
    	    		 	        String posDate="StartDay";
    	    		            sql = "select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd "
    	    		                + " from tbldayendprocess "
    	    		                + " where strPOSCode='"+ rsMasterData.getString(1) + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')";
    	    		            //System.out.println(sql);
    	    		            ResultSet rsDay=st1.executeQuery(sql);
    	    		            if(rsDay.next())
    	    		            {
    	    		            	String shiftEnd=rsDay.getString(3);
    	    		            	String dayEnd=rsDay.getString(4);
    	    		            	if(shiftEnd.equals("") && dayEnd.equals("N"))
    	    		            	{
    	    		            		posDate="StartDay";
    	    		            	}
    	    		            	else
    	    		            	{
    	    		            		posDate=rsDay.getString(1);
    	    		            	}
    	    		            }
    	    		            obj.put("POSDate",posDate);
    		 		            
    	    		            rsDay.close();
    	    		            
    	    		            arrObj.put(obj);
    		 	        }
    		 	        rsMasterData.close();
    		 	        
                     }
	         }
	        rsPOS.close();
	        jObj.put("PosMasterList", arrObj);
	        st.close();
	        st1.close();
	        st2.close();
	        st3.close();
	        aposCon.close();
	        st4.close();
		}
		catch(Exception e)
	    {
			e.printStackTrace();
			
			try
			{
				JSONObject obj=new JSONObject();
		        obj.put("Status","Error");
		        arrObj.put(obj);
		        jObj.put("PosMasterList", arrObj);
			}catch(Exception exx)
			{
				exx.printStackTrace();
			}
			
	    }
		return arrObj;
	}
	

	@GET
	@Path("/funGetItemMaster")
	@Produces(MediaType.APPLICATION_JSON)
	
	public String funGetItemMaster(@QueryParam("clientCode") String clientCode)
	{
		return funFetchItemMaster(clientCode);
	}

	private String funFetchItemMaster(String clientCode )
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection aposCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
		String sql=" select a.strItemCode, a.strItemName, a.strSubGroupCode, a.strExternalCode, a.dblSalePrice  "
				+  " from tblitemmaster a ";
		JSONArray arrObj=new JSONArray();
		
		 try
	        {
			 aposCon=objDb.funOpenAPOSCon("mysql","master");
	            st = aposCon.createStatement();
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
                while(rsMasterData.next())
                {
        	        JSONObject obj=new JSONObject();
        	        obj.put("ItemCode",rsMasterData.getString(1));
        	        obj.put("ItemName",rsMasterData.getString(2));
        	        obj.put("ItemSubGroupCode",rsMasterData.getString(3));
        	        obj.put("ItemExternalCode",rsMasterData.getString(4));
        	        obj.put("ItemSalePrice",rsMasterData.getString(5));
        	       // obj.put("ItemImage",rsMasterData.getString(6));
        	        arrObj.put(obj);
                }
                rsMasterData.close();
        
                jObj.put("ItemMasterList", arrObj);
                st.close();
                aposCon.close();
        
	     }catch(Exception e)
		 {
	    	 e.printStackTrace();
		 }
		 return jObj.toString();
	}
	
	
	@GET
	@Path("/funGetMainMenu")
	@Produces(MediaType.APPLICATION_JSON)	
	public JSONObject funGetMainMenu(@QueryParam("UserCode")String userCode,@QueryParam("ModuleType")String moduleType 
			,@QueryParam("clientCode") String clientCode,@QueryParam("SuperUser") boolean superUser 
			,@QueryParam("POSCode") String POSCode )
	{
		return funFetchMainMenu(userCode,moduleType,clientCode,superUser,POSCode);		
	}
	
	private JSONObject  funFetchMainMenu(String userCode,String moduleType,String  clientCode,boolean superUser, String POSCode)
	{
			
		clsDatabaseConnection objDb=new clsDatabaseConnection();
	    Connection aposCon=null;
	    Statement st=null;
	    Statement st1=null;
	    JSONObject jObj=new JSONObject();
	    String sql=null;
	    
	    if(userCode.equalsIgnoreCase("Sanguine"))
	    {
	    	if(moduleType.equals("T"))
	    	{
	    		sql=" select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	    				+ " FROM tblforms a WHERE (a.strModuleType='T' OR a.strModuleType='U' OR strModuleName='Customer Master') "
	    				+ " AND a.strModuleName!='NCKOT' AND a.strModuleName!='Complimentry Settlement'  "
	    				+ " AND a.strModuleName!='Discount On Bill' AND a.strModuleName!='NCKOT' "
	    				+ " AND a.strModuleName!='Take Away' AND a.strModuleName LIKE '%%';  ";
	    	}
	    	else if (moduleType.equals("M"))
	    	{
	    		sql=" SELECT DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	    				+ " FROM tblforms a WHERE (a.strModuleType='M' OR a.strModuleType='U') "
	    				+ " AND a.strModuleName<>'ReOrderTime' AND a.strModuleName<>'Customer Master' "
	    				+ " AND a.strModuleName LIKE '%%'  ";
	    	}
	    	else if (moduleType.equals("R"))
	    	{
	        	sql=" SELECT DISTINCT  a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	        			+ " FROM tblforms a  WHERE (a.strModuleType='R' OR a.strModuleType='U') "
	        			+ " AND a.strModuleName LIKE '%%'; ";
	    	}
	    	
	    	
	    }else  if(superUser)
	    {
	    	if(moduleType.equals("T"))
	    	{
	    		sql="select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	          	    + "from tblforms a,tblsuperuserdtl b  "
	           		+ " where b.strUserCode='"+userCode+"'  "
	           	    +  "and a.strModuleName=b.strFormName "
	           		+  "and  (a.strModuleType='T' or a.strModuleType='U'  or strModuleName='Customer Master')   "
	           	    + "order by b.intSequence ";
	    	}
	    	else if (moduleType.equals("M"))
	    	{
	    		sql="select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	          	    + "from tblforms a,tblsuperuserdtl b  "
	           		+ " where b.strUserCode='"+userCode+"'  "
	           	    +  "and a.strModuleName=b.strFormName "
	           		+  "and  (a.strModuleType='M' or a.strModuleType='U')   "
	           	    + "order by b.intSequence ";
	    	}
	    	else if (moduleType.equals("R"))
	    	{
	        	sql="select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	        	    + "from tblforms a,tblsuperuserdtl b  "
	        		+ " where b.strUserCode='"+userCode+"' "
	        	    +  "and a.strModuleName=b.strFormName "
	        		+  "and  (a.strModuleType='R' or a.strModuleType='U')   "
	        	    + "order by b.intSequence ";
	    	}
	    	else
	    	{
	    		sql="select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	    			+ "from tblforms a,tblsuperuserdtl b "
	    			+ "where b.strUserCode='"+userCode+"' "
	    			+ "and a.strModuleName=b.strFormName "
	    			+ "and (a.strModuleName='Direct Biller' or a.strModuleName='Make KOT' "
	    			+ "or a.strModuleName='VoidKot' "
	    			+ "or a.strModuleName='Make Bill' or a.strModuleName='Sales Report' "
	    			+ "or a.strModuleName='Reprint' or a.strModuleName='SettleBill' "
	    			+ "or a.strModuleName='TableStatusReport' or a.strModuleName='NCKOT' "
	    	        + "or a.strModuleName='Take Away' or a.strModuleName='Table Reservation' "
	    			+ "or a.strModuleName='POS Wise Sales' or a.strModuleName='Customer Order' "
	    			//+ "or a.strModuleName='Non Available Items' or a.strModuleName='Mini Make KOT' "
	    			+ "or a.strModuleName='Day End' or a.strModuleName='KDSForKOTBookAndProcess' "
	    			+ "or a.strModuleName='Kitchen Process System' or a.strModuleName='Change Settlement' or a.strModuleName='Customer Master'"
	    			+ " OR a.strModuleName='Dash Board') ";																		  
	    	}
	    }
	    else
	    {
	    	if(moduleType.equals("T"))
	    	{
	    		sql=" select DISTINCT a.strModuleName,a.strImageName,b.intSequence,a.strFormName,a.strRequestMapping  "
	    			+  "from tblforms a,tbluserdtl b  "
	    	        +  "where (a.strModuleType='T' or a.strModuleType='U' or strModuleName='Customer Master')  "
	    	        +  "and b.strUserCode='"+userCode+"'  "
	    	        +   "and a.strModuleName=b.strFormName  "
	    	        +   "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
	    	        +    "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true')  "
	    	        +  "order by b.intSequence ";
	    	}
	    	else if(moduleType.equals("M"))
	    	{
	    		sql=" select DISTINCT a.strModuleName,a.strImageName,b.intSequence,a.strFormName,a.strRequestMapping  "
	          	      +  "from tblforms a,tbluserdtl b  "
	          	      +  "where (a.strModuleType='M' or a.strModuleType='U')  "
	          	      +  "and b.strUserCode='"+userCode+"'  "
	          	      +  "and a.strModuleName=b.strFormName  "
	          	      +  "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
	          	      +  "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true')  "
	          	      +  "order by b.intSequence ";
	    	}
	    	else if (moduleType.equals("R"))
	    	{
	    		sql=" select DISTINCT a.strModuleName,a.strImageName,b.intSequence,a.strFormName,a.strRequestMapping  "
	    			+  "from tblforms a,tbluserdtl b  "
	            	+  "where (a.strModuleType='R' or a.strModuleType='U' )  "
	            	+  "and b.strUserCode='"+userCode+"'  "
	            	+  "and a.strModuleName=b.strFormName  "
	            	+  "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
	            	+  "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true')  "
	            	+  "order by b.intSequence ";
	    	}
	    	else 
	    	{
	    		/*sql="select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping "
	    			+ "from tblforms a,tbluserdtl b "
	    			+ "where b.strUserCode='"+userCode+"' "
	    			+ "and a.strModuleName=b.strFormName "
	    			+ "and (a.strModuleName='Direct Biller' or a.strModuleName='Make KOT' "
	    			+ "or a.strModuleName='RechargeDebitCard' or a.strModuleName='ShowCard' "
	    			+ "or a.strModuleName='VoidKot' or a.strModuleName='DebitCardRegister' "
	    			+ "or a.strModuleName='Make Bill' or a.strModuleName='Sales Report' "
	    			+ "or a.strModuleName='Reprint' or a.strModuleName='SettleBill'"
	    			+ "or a.strModuleName='TableStatusReport' or a.strModuleName='NCKOT' "
	    			+ "or a.strModuleName='Take Away' or a.strModuleName='Table Reservation') ";
	    		*/
	    		
	    		sql="select DISTINCT a.strModuleName,a.strImageName,a.strFormName,a.strRequestMapping  "
				+ " from tblforms a,tbluserdtl b "
				+ " where (a.strModuleType='T'  or a.strModuleType='U' or a.strModuleType='R'  or a.strModuleName='Customer Master')  "
				+ " and a.strModuleName!='Complimentry Settlement' and a.strModuleName!='Discount On Bill'  "
				+ " and b.strUserCode='"+userCode+"' and a.strModuleName like '%%' and a.strModuleName=b.strFormName "
				+ " and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
				+ " or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true' or b.strTLA='true' ) "
				+ " and a.strModuleName in ('Direct Biller','Make KOT','VoidKot'"
				+ ",'Make Bill','Sales Report','Reprint','SettleBill','TableStatusReport'"
				+ ",'NCKOT','Take Away','Table Reservation','POS Wise Sales','Customer Order'"
				//+ ",'Non Available Items','Mini Make KOT','Day End','KDSForKOTBookAndProcess','Kitchen Process System') "
				+ ",'Day End','KDSForKOTBookAndProcess','Kitchen Process System','Change Settlement','Customer Master','Dash Board') "
				+ " order by b.intSequence";
	    		
	    	}
	    }
	    
		JSONArray arrObj=new JSONArray();
		try
	    {
			aposCon=objDb.funOpenAPOSCon("mysql","master");
	        st = aposCon.createStatement();
	        st1 = aposCon.createStatement();
	        
	        //System.out.println(sql);
	        ResultSet rsMasterData=st.executeQuery(sql);
	        while(rsMasterData.next())
	        {
	        	JSONObject obj=new JSONObject();            	
	        	obj.put("ModuleName",rsMasterData.getString(1));
	        	obj.put("ImageName", rsMasterData.getString(2));
	        	obj.put("FormName", rsMasterData.getString(3));
	        	obj.put("RequestMapping", rsMasterData.getString(4));
	        	arrObj.put(obj);
	        }
	        rsMasterData.close();
	        
	        String posDate="StartDay";
	        String dayStatus="";
	        sql = "select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd "
	            + " from tbldayendprocess "
	            + " where strPOSCode='"+ POSCode + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')";
	        //System.out.println(sql);
	        rsMasterData=st1.executeQuery(sql);
	        if(rsMasterData.next())
	        {
	        	String shiftEnd=rsMasterData.getString(3);
	        	String dayEnd=rsMasterData.getString(4);
	        	if(shiftEnd.equals("") && dayEnd.equals("N"))
	        	{
	        		posDate=rsMasterData.getString(1);
	        		dayStatus = "StartDay";
	        	}
	        	else
	        	{
	        		posDate=rsMasterData.getString(1);
	        	}
	        }
	        jObj.put("POSDate",posDate);
	        jObj.put("MainMenuList", arrObj);
	        jObj.put("DayStatus",dayStatus);
	        if(true){
	        	JSONObject obj=new JSONObject();            	
	        	obj.put("ModuleName","Item Voice Capture");
	        	obj.put("ImageName", "imgItemVoiceSave");
	        	obj.put("FormName", "Item Voice Capture");
	        	obj.put("RequestMapping", "ItemVoiceCapture");
	        	arrObj.put(obj);
	        	/*if(superUser)
	        	{
	        		JSONObject obj1=new JSONObject();
		        	obj1.put("ModuleName","Dash Board");
		        	obj1.put("ImageName", "imgdashboard");
		        	obj1.put("FormName", "Dash Board");
		        	obj1.put("RequestMapping", "Dashboard");
		        	arrObj.put(obj1);
	        	}*/	
	        }
	        rsMasterData.close();
	        st1.close();
	        st.close();
	        aposCon.close();
	        
	    }catch(Exception e)
	    {
	        e.printStackTrace();
	    }
		return jObj;
				
		
	}
	
	
	@GET
	@Path("/funGetMainMenu1")
	@Produces(MediaType.APPLICATION_JSON)	
	public String funGetMainMenu1(@QueryParam("UserCode")String userCode,@QueryParam("ModuleType")String moduleType 
			,@QueryParam("clientCode") String clientCode,@QueryParam("SuperUser") boolean superUser 
			,@QueryParam("POSCode") String POSCode )
	{
		return funFetchMainMenu1(userCode,moduleType,clientCode,superUser,POSCode);		
	}
	
	private String  funFetchMainMenu1(String userCode,String moduleType,String  clientCode,boolean superUser, String POSCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection aposCon=null;
        Statement st=null;
        Statement st1=null;
        JSONObject jObj=new JSONObject();
        String sql=null;
        
        if(superUser)
        {
        	if(moduleType.equals("T"))
        	{
        		sql="select DISTINCT a.strModuleName,a.strImageName "
              	    + "from tblforms a,tblsuperuserdtl b  "
               		+ " where b.strUserCode='"+userCode+"'  "
               	    +  "and a.strModuleName=b.strFormName "
               		+  "and  (a.strModuleType='T' or a.strModuleType='U'  or strModuleName='Customer Master')   "
               	    + "order by b.intSequence ";
        	}
        	else if (moduleType.equals("M"))
        	{
        		sql="select DISTINCT a.strModuleName,a.strImageName "
              	    + "from tblforms a,tblsuperuserdtl b  "
               		+ " where b.strUserCode='"+userCode+"'  "
               	    +  "and a.strModuleName=b.strFormName "
               		+  "and  (a.strModuleType='M' or a.strModuleType='U')   "
               	    + "order by b.intSequence ";
        	}
        	else if (moduleType.equals("R"))
        	{
	        	sql="select DISTINCT a.strModuleName,a.strImageName "
	        	    + "from tblforms a,tblsuperuserdtl b  "
	        		+ " where b.strUserCode='"+userCode+"' "
	        	    +  "and a.strModuleName=b.strFormName "
	        		+  "and  (a.strModuleType='R' or a.strModuleType='U')   "
	        	    + "order by b.intSequence ";
        	}
        	else
        	{
        		sql="select DISTINCT a.strModuleName,a.strImageName "
        			+ "from tblforms a,tblsuperuserdtl b "
        			+ "where b.strUserCode='"+userCode+"' "
        			+ "and a.strModuleName=b.strFormName "
        			+ "and (a.strModuleName='ShowCard' or a.strModuleName='Sales Report'  ) ";
        	}
        }

        else
        {
        	if(moduleType.equals("T"))
        	{
        		sql=" select DISTINCT a.strModuleName,a.strImageName,b.intSequence  "
        			+  "from tblforms a,tbluserdtl b  "
        	        +  "where (a.strModuleType='T' or a.strModuleType='U' or strModuleName='Customer Master')  "
        	        +  "and b.strUserCode='"+userCode+"'  "
        	        +   "and a.strModuleName=b.strFormName  "
        	        +   "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
        	        +    "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true')  "
        	        +  "order by b.intSequence ";
        	}
        	else if(moduleType.equals("M"))
        	{
        		sql=" select DISTINCT a.strModuleName,a.strImageName,b.intSequence  "
              	      +  "from tblforms a,tbluserdtl b  "
              	      +  "where (a.strModuleType='M' or a.strModuleType='U')  "
              	      +  "and b.strUserCode='"+userCode+"'  "
              	      +  "and a.strModuleName=b.strFormName  "
              	      +  "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
              	      +  "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true')  "
              	      +  "order by b.intSequence ";
        	}
        	else if (moduleType.equals("R"))
        	{
        		sql=" select DISTINCT a.strModuleName,a.strImageName,b.intSequence  "
        			+  "from tblforms a,tbluserdtl b  "
                	+  "where (a.strModuleType='R' or a.strModuleType='U' )  "
                	+  "and b.strUserCode='"+userCode+"'  "
                	+  "and a.strModuleName=b.strFormName  "
                	+  "and (b.strAdd='true' or b.strEdit='true' or b.strDelete = 'true' or b.strView='true' "
                	+  "or b.strPrint = 'true' or b.strSave = 'true' or b.strGrant = 'true')  "
                	+  "order by b.intSequence ";
        	}
        	else 
        	{
        		sql="select DISTINCT a.strModuleName,a.strImageName "
        			+ "from tblforms a,tbluserdtl b "
        			+ "where b.strUserCode='"+userCode+"' "
        			+ "and a.strModuleName=b.strFormName "
        			+ "and (a.strModuleName='ShowCard' or a.strModuleName='Sales Report'  ) ";
        		
        	}
        }
        
        System.out.println(sql);
		JSONArray arrObj=new JSONArray();
		try
        {
			aposCon=objDb.funOpenAPOSCon("mysql","master");
            st = aposCon.createStatement();
            st1 = aposCon.createStatement();
            
            //System.out.println(sql);
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();            	
            	obj.put("ModuleName",rsMasterData.getString(1));
            	obj.put("ImageName", rsMasterData.getString(2));
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
            String posDate="StartDay";
            sql = "select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd "
                + " from tbldayendprocess "
                + " where strPOSCode='"+ POSCode + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')";
            //System.out.println(sql);
            rsMasterData=st1.executeQuery(sql);
            if(rsMasterData.next())
            {
            	String shiftEnd=rsMasterData.getString(3);
            	String dayEnd=rsMasterData.getString(4);
            	if(shiftEnd.equals("") && dayEnd.equals("N"))
            	{
            		posDate="StartDay";
            	}
            	else
            	{
            		posDate=rsMasterData.getString(1);
            	}
            }
            jObj.put("POSDate",posDate);
            jObj.put("MainMenuList", arrObj);
            
            rsMasterData.close();
            st1.close();
            st.close();
            aposCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
		return jObj.toString();
	}
	
	
	
	
	
	
	
	
	@GET
	@Path("/funShowCardDtl")
	@Produces(MediaType.APPLICATION_JSON)
    public String funGetTransactionDtl(@QueryParam("cardNo") String cardNo
    		, @QueryParam("FromDate") String fromDate, @QueryParam("ToDate") String toDate)
	{
		return funGetTransactionDtlFromCardNo(cardNo, fromDate, toDate);
	}
	
	private String funGetTransactionDtlFromCardNo(String cardNo, String fromDate, String toDate) {

		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		Statement st2 = null;
		Statement st3 = null;
		Statement st4 = null;
		Statement st5 = null;
		// String posobj=null;
		JSONObject jObj = new JSONObject();

		try {
			cmsCon = objDb.funOpenAPOSCon("mysql", "master");
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			st2 = cmsCon.createStatement();
			st3 = cmsCon.createStatement();
			st4 = cmsCon.createStatement();
			st5 = cmsCon.createStatement();

			JSONArray arrObj = new JSONArray();
			String sql = " select ifnull(b.strCustomerName,''), a.strCardNo "
					+ " from tbldebitcardmaster a left outer join tblcustomermaster b "
					+ " on a.strCustomerCode=b.strCustomerCode "
					+ " where a.strCardString='" + cardNo + "' ";

			ResultSet rsCustomerData = st.executeQuery(sql);
			while (rsCustomerData.next()) {
				JSONObject obj = new JSONObject();
				obj.put("CustomerName", rsCustomerData.getString(1));
				obj.put("CardNo", rsCustomerData.getString(2));
				arrObj.put(obj);

				// for recharge
				String sql1 = "  select a.intRechargeNo, a.dblRechargeAmount, date(a.dteDateCreated), b.strCardNo"
						+ ", c.strPosName,'R' as TransType "
						+ "  from tbldebitcardrecharge a,tbldebitcardmaster b, tblposmaster c  "
						+ "  where a.strCardNo=b.strCardNo "
						+ "  and a.strPOSCode=c.strPosCode  "
						+ "  and a.strCardString='" + cardNo + "' "
						+ "  and date(a.dteDateCreated) between '"+fromDate+"' and '"+toDate+"'";
				System.out.println(sql1);
				JSONArray arrjObjrecharge = new JSONArray();
				ResultSet rsRecharge = st1.executeQuery(sql1);
				while (rsRecharge.next()) 
				{
					JSONObject jObjRechargeRows = new JSONObject();
					jObjRechargeRows.put("RechargeNo", rsRecharge.getString(1));
					jObjRechargeRows.put("RechargeAmount",rsRecharge.getString(2));
					jObjRechargeRows.put("Date", rsRecharge.getString(3));
					jObjRechargeRows.put("CardNo", rsRecharge.getString(4));
					jObjRechargeRows.put("PosName", rsRecharge.getString(5));
					jObjRechargeRows.put("TransType", rsRecharge.getString(6));
					arrjObjrecharge.put(jObjRechargeRows);
				}
				rsRecharge.close();
				obj.put("Rechargedtl", arrjObjrecharge);

			// for redeem
			
				String sql2=" select a.strBillNo,'RD' as TransType,c.strCardNo,a.dblTransactionAmt,date(a.dteBillDate),f.strPosName"
					+ " ,ifnull(e.strCounterName,'') "
					+ " from tbldebitcardbilldetails a left outer join tbldebitcardmaster c on a.strCardNo=c.strCardNo "
					+ " left outer join tblbilldtl d on a.strBillNo=d.strBillNo "
					+ " left outer join tblcounterhd e on d.strCounterCode=e.strCounterCode "
					+ " left outer join tblposmaster f on a.strPOSCode=f.strPosCode "
					+ " where  c.strCardString='"+cardNo+"' "
					+ " and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
					+ " group by a.strBillNo";

				System.out.println(sql2);
				JSONArray arrjObjredeem = new JSONArray();
				ResultSet rsRedeem = st2.executeQuery(sql2);
				while (rsRedeem.next()) {
					JSONObject jObjRedeemRows = new JSONObject();
					jObjRedeemRows.put("BillNo", rsRedeem.getString(1));
					jObjRedeemRows.put("TransType", rsRedeem.getString(2));
					jObjRedeemRows.put("CardNo", rsRedeem.getString(3));
					jObjRedeemRows.put("TransactionAmt", rsRedeem.getString(4));
					jObjRedeemRows.put("Date", rsRedeem.getString(5));
					jObjRedeemRows.put("PosName", rsRedeem.getString(6));
					jObjRedeemRows.put("Counter", rsRedeem.getString(7));
				
					arrjObjredeem.put(jObjRedeemRows);
				}
				rsRedeem.close();
				obj.put("Redeemdtl", arrjObjredeem);
				
				
// for refund
				
			
			 String sql3= " select a.strRefundNo, a.dblRefundAmt, date(a.dteDateCreated), b.strCardNo, " 
					    + " ifnull(c.strPosName,''),'RF'as TransType  "
					    + " from tbldebitcardrefundamt a left outer join tbldebitcardmaster b "
						+ " on a.strCardString=b.strCardString  "
						+ " left outer join tblposmaster c  "
						+ " on a.strPOSCode=c.strPosCode  "
						+ " where  a.strCardString='"+cardNo+"' "
						+ " and date(a.dteDateCreated) "
						+ " between '"+fromDate+"' and '"+toDate+"' ";
				
				System.out.println(sql3);

				JSONArray arrjObjrefund = new JSONArray();
				ResultSet rsRefund = st3.executeQuery(sql3);
				while (rsRefund.next()) {
					JSONObject jObjRefundRows = new JSONObject();
					jObjRefundRows.put("RefundNo", rsRefund.getString(1));
					jObjRefundRows.put("RefundAmount", rsRefund.getString(2));
					jObjRefundRows.put("Date", rsRefund.getString(3));
					jObjRefundRows.put("CardNo", rsRefund.getString(4));
					jObjRefundRows.put("PosName", rsRefund.getString(5));
					jObjRefundRows.put("TransType", rsRefund.getString(6));
					arrjObjrefund.put(jObjRefundRows);
				}
				rsRedeem.close();
				obj.put("RefundDtl", arrjObjrefund);
				
				

				// for Open KOT
				
				
				 String sql4= " select b.strPosName,a.strTableNo,date(a.dteDateCreated),  " 
						    + " 'OKT',sum(a.dblAmount),c.strCardNo   "
						    + " from tblitemrtemp a,tblposmaster b,tbldebitcardmaster c   "
							+ " where a.strCardNo=c.strCardNo "
							+ " and c.strCardString='"+cardNo+"' "
							+ " and a.strPOSCode=b.strPosCode "
							+ " and a.strPrintYN='Y' and a.strNCKotYN='N' "
							+ " and date(a.dteDateCreated ) "
							+ " between '"+fromDate+"' and '"+toDate+"' "
							+ " group by a.strTableNo";
					
					System.out.println(sql4);

					JSONArray arrjObjOpenKOT = new JSONArray();
					ResultSet rsOpenKOT = st4.executeQuery(sql4);
					while (rsOpenKOT.next()) {
						JSONObject jObjOpenKOTRows = new JSONObject();
						jObjOpenKOTRows.put("PosName", rsOpenKOT.getString(1));
						jObjOpenKOTRows.put("TableNo", rsOpenKOT.getString(2));
						jObjOpenKOTRows.put("Date", rsOpenKOT.getString(3));
						jObjOpenKOTRows.put("TransType", rsOpenKOT.getString(4));
						jObjOpenKOTRows.put("Amount", rsOpenKOT.getString(5));
						jObjOpenKOTRows.put("CardNo", rsOpenKOT.getString(6));
						arrjObjOpenKOT.put(jObjOpenKOTRows);
					}
					rsOpenKOT.close();
					obj.put("OpenKOTDtl", arrjObjOpenKOT);
					
					
					
					
					// for Unsettle Bill
					
					
					 String sql5= " select c.strPosName,a.strBillNo,date(a.dteBillDate),'UB',  " 
							    + " a.dblGrandTotal,d.strCardNo  "
							    + " from tblbillhd a left outer join tbltablemaster b  "
							    + " on a.strTableNo=b.strTableNo ,tblposmaster c,tbldebitcardmaster d  "
							    + " where a.strCardNo=d.strCardNo "
								+ " and d.strCardString='"+cardNo+"' "
								+ " and a.strPOSCode=c.strPosCode   "
								+ " and date(a.dteBillDate ) "
								+ " between '"+fromDate+"' and '"+toDate+"'  "
								+ " and a.strBillNo not in (select strBillNo from tblbillsettlementdtl)";
						
						System.out.println(sql5);

						JSONArray arrjObjUnsettleBill = new JSONArray();
						ResultSet rsUnsettleBill = st5.executeQuery(sql5);
						while (rsUnsettleBill.next()) {
							JSONObject jObjUnsettleBillRows = new JSONObject();
							jObjUnsettleBillRows.put("PosName", rsUnsettleBill.getString(1));
							jObjUnsettleBillRows.put("BillNo", rsUnsettleBill.getString(2));
							jObjUnsettleBillRows.put("Date", rsUnsettleBill.getString(3));
							jObjUnsettleBillRows.put("TransType", rsUnsettleBill.getString(4));
							jObjUnsettleBillRows.put("Amount", rsUnsettleBill.getString(5));
							jObjUnsettleBillRows.put("CardNo", rsUnsettleBill.getString(6));
							arrjObjUnsettleBill.put(jObjUnsettleBillRows);
						}
						rsUnsettleBill.close();
						obj.put("UnsettleBillDtl", arrjObjUnsettleBill);
				
				
				
				
				
			}
			rsCustomerData.close();

			jObj.put("cardTransactinDetail", arrObj);
			st.close();
			st1.close();
			st2.close();
			st3.close();
			st4.close();
			st5.close();
			// st2.close();
			cmsCon.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jObj.toString();
	}
	
	
	
	@GET
	@Path("/funMemberDtl")
	@Produces(MediaType.APPLICATION_JSON)
    public String funGetMemberDtl(@QueryParam("cardNo") String cardNo,@QueryParam("clientCode") String clientCode)
	{
		return funGetMemberDtlFromCardNo(cardNo,clientCode);
	}
	
	private String funGetMemberDtlFromCardNo(String cardNo,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
          
        try
        {
	        cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
	        JSONArray arrObj=new JSONArray();
			String sql="select strStatus from tbldebitcardmaster where strCardString='"+cardNo+"'";
			System.out.println(sql);
			ResultSet rsCardInfo=st.executeQuery(sql);
			if(rsCardInfo.next())
			{
				if(rsCardInfo.getString(1).equals("Active"))
				{
					sql= " select a.dblRedeemAmt, ifnull(b.strCustomerName,''), ifnull(b.strCustomerCode,''), c.strCardName"
						+ ", c.dteExpiryDt, c.strCardTypeCode, a.strCardNo, c.strCashCard,c.strAuthorizeMemberCard"
						+ ",c.dblCardValueFixed "
						+ " from tbldebitcardmaster a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "						
						+  " left outer join tbldebitcardtype c on a.strCardTypeCode=c.strCardTypeCode "
						+ " where a.strCardString='"+cardNo+"' ";
					System.out.println(sql);
					
		            ResultSet rsCardBalance=st.executeQuery(sql);
		            while(rsCardBalance.next())
		            {
		            	JSONObject obj=new JSONObject();
		            	
		            	obj.put("cardBalance",rsCardBalance.getString(1));
		            	obj.put("custName",rsCardBalance.getString(2));
		            	obj.put("custCode",rsCardBalance.getString(3));
		            	obj.put("cardName",rsCardBalance.getString(4));
		            	obj.put("cardExpriryDate",rsCardBalance.getString(5));
		            	obj.put("cardTypeCode",rsCardBalance.getString(6));
		            	obj.put("cardNo",rsCardBalance.getString(7));
		            	obj.put("CashCard",rsCardBalance.getString(8));
		            	obj.put("AuthorizedCard",rsCardBalance.getString(9));
		            	if(clientCode.equals("074.001") || clientCode.equals("047.001") )
		            	{
		            		String cmsMemberCode=new clsCMSIntegration().funGetMemberCodeFromCMS(cardNo);		            	
		            		obj.put("RefrenceMemberCode",cmsMemberCode);
		            	}
		            		
		            	obj.put("CardDepositeAmt",rsCardBalance.getString(10));
		            	arrObj.put(obj);
		            }
		            rsCardBalance.close();
		            jObj.put("cardInfo", arrObj);
		            st.close();
		            cmsCon.close();
		        }
				else // Card is not active
				{
					JSONObject obj=new JSONObject();
					obj.put("cardBalance","Deactive");
					obj.put("custName","");
					arrObj.put(obj);
					jObj.put("cardInfo", arrObj);
				}
			}
			else // Invalid Card
			{
				JSONObject obj=new JSONObject();
				obj.put("cardBalance","InvalidCard");
				obj.put("custName","");
				arrObj.put(obj);
				jObj.put("cardInfo", arrObj);
			}
			   st.close();
	            cmsCon.close();
			
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return jObj.toString();
	}
	
	
	
	
// Function to get total balance on card for cash card
	private double funGetTotalBalanceOnDebitCard(String cardNo, String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        String sql="";
        double debitCardBalance=0;
        
		try
		{
			sql="select dblRedeemAmt from tbldebitcardmaster "
				+ "where strCardNo='"+cardNo+"' and strClientCode='"+clientCode+"' ";
			ResultSet rsBal=st.executeQuery(sql);
			if(rsBal.next())
			{
				debitCardBalance=rsBal.getDouble(1);				
			}
			rsBal.close();
			posCon.close();
			st.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return debitCardBalance;
		
	}
	
	
	
	
	@GET
	@Path("/funValidateCard")
	@Produces(MediaType.APPLICATION_JSON)
    public String funValidateCard(@QueryParam("cardNo") String cardNo,@QueryParam("UserCode") String userCode,@QueryParam("ClientCode") String clientCode,@QueryParam("CardType") String cardType)
	{
		String response="";
		if(cardType.equalsIgnoreCase("CashCard"))
		{			
			response=funGetCashCardDtl(cardNo,userCode,clientCode);
		}
		else
		{
			response=funGetMemberDtlFromCMS(cardNo,userCode,clientCode);
		}
		
		return response;
	}
	
	private String funGetMemberDtlFromCMS(String cardNo,String userCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        String sql="";
          
        try
        {
        	posCon=objDb.funOpenAPOSCon("mysql","master");
	        st = posCon.createStatement();
	        
	        JSONArray arrObj=new JSONArray();
			Date dt=new Date();
			String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate()+" "+dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds();
			JSONObject obj=new JSONObject();
				
			String cmsMemberCardDtl=new clsCMSIntegration().funGetMemberCardDtlsFromCMS(cardNo);
        	JSONObject jsonObjCMSMemberCardDtl=new JSONObject(cmsMemberCardDtl);
        	JSONArray mJsonArray = (JSONArray) jsonObjCMSMemberCardDtl.get("CMSMemberCardDtl");
            JSONObject mJsonObject = new JSONObject();
                
            for (int i = 0; i < mJsonArray.length(); i++) {
            	mJsonObject = (JSONObject) mJsonArray.get(i);

                System.out.println(mJsonObject.get("CustomerCode").toString());
                if(mJsonObject.get("CustomerCode").toString().equals("No Data"))
                {
                   	obj.put("CardType","MemberCard");
                  	obj.put("CustomerCode","Member Not Found");
                   	break;
                }
                else
                {
                   	obj.put("CardType","MemberCard");
                   	obj.put("CustomerCode",mJsonObject.get("CustomerCode").toString());
                    obj.put("CardNo",mJsonObject.get("CardNo").toString());
                    obj.put("CardString",mJsonObject.get("CardString").toString());
                    obj.put("CardStatus",mJsonObject.get("CardStatus").toString());
                    obj.put("Blocked",mJsonObject.get("Blocked").toString());
                    obj.put("MemberCode",mJsonObject.get("MemberCode").toString());
                    obj.put("MemberName",mJsonObject.get("MemberName").toString());
                        
                    Statement st1 = posCon.createStatement();
                    sql="delete from tblcustomermaster "
                   		+ "where strcustomercode = '"+mJsonObject.get("MemberCode").toString()+"' "
                		+ "and strClientCode='"+clientCode+"'";
                    st1.executeUpdate(sql);
                        
                    sql="insert into tblcustomermaster "
                    	+ "(strCustomerCode,strCustomerName,longMobileNo,strUserCreated"
            			+ ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode) "
            			+ "values ('"+mJsonObject.get("MemberCode").toString()+"'"
            			+ ",'"+mJsonObject.get("MemberName").toString()+"','0'"
            			+ ",'"+userCode+"','"+userCode+"','"+date+"','"+date+"'"
            			+ ",'"+clientCode+"') ";
                    st1.executeUpdate(sql);
                    st1.close();
                }
				
                arrObj.put(obj);
				jObj.put("CardInfo", arrObj);
				posCon.close();
				st.close();
			}
		
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return jObj.toString();
	}
	
	
	
	@GET
	@Path("/funValidateCardForSettle")
	@Produces(MediaType.APPLICATION_JSON)
    public String funValidateCardForSettle(@QueryParam("cardNo") String cardNo,@QueryParam("UserCode") String userCode,@QueryParam("ClientCode") String clientCode,@QueryParam("CardType") String cardType)
	{
		String response="";
		if(cardType.equalsIgnoreCase("CashCard"))
		{			
			response=funGetCashCardDetails(cardNo,userCode,clientCode);
		}
		else
		{
			response=funGetMemberDetailsFromCMS(cardNo,userCode,clientCode);
		}
		
		return response;
	}
	
	private String funGetMemberDetailsFromCMS(String cardNo,String userCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        String sql="";
          
        try
        {
        	posCon=objDb.funOpenAPOSCon("mysql","master");
	        st = posCon.createStatement();
	        
	        JSONArray arrObj=new JSONArray();
			Date dt=new Date();
			String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate()+" "+dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds();
			JSONObject obj=new JSONObject();
				
			String cmsMemberCardDtl=new clsCMSIntegration().funGetMemberCardNoDtlsFromCMS(cardNo);
        	JSONObject jsonObjCMSMemberCardDtl=new JSONObject(cmsMemberCardDtl);
        	JSONArray mJsonArray = (JSONArray) jsonObjCMSMemberCardDtl.get("CMSMemberCardDtl");
            JSONObject mJsonObject = new JSONObject();
                
            for (int i = 0; i < mJsonArray.length(); i++) {
            	mJsonObject = (JSONObject) mJsonArray.get(i);

                System.out.println(mJsonObject.get("CustomerCode").toString());
                if(mJsonObject.get("CustomerCode").toString().equals("No Data"))
                {
                   	obj.put("CardType","MemberCard");
                  	obj.put("CustomerCode","Member Not Found");
                   	break;
                }
                else
                {
                   	obj.put("CardType","MemberCard");
                   	obj.put("CustomerCode",mJsonObject.get("CustomerCode").toString());
                    obj.put("CardNo",mJsonObject.get("CardNo").toString());
                    obj.put("CardString",mJsonObject.get("CardString").toString());
                    obj.put("CardStatus",mJsonObject.get("CardStatus").toString());
                    obj.put("Blocked",mJsonObject.get("Blocked").toString());
                    obj.put("MemberCode",mJsonObject.get("MemberCode").toString());
                    obj.put("MemberName",mJsonObject.get("MemberName").toString());
                        
                    Statement st1 = posCon.createStatement();
                    sql="delete from tblcustomermaster "
                   		+ "where strcustomercode = '"+mJsonObject.get("MemberCode").toString()+"' "
                		+ "and strClientCode='"+clientCode+"'";
                    st1.executeUpdate(sql);
                        
                    sql="insert into tblcustomermaster "
                    	+ "(strCustomerCode,strCustomerName,longMobileNo,strUserCreated"
            			+ ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode) "
            			+ "values ('"+mJsonObject.get("MemberCode").toString()+"'"
            			+ ",'"+mJsonObject.get("MemberName").toString()+"','0'"
            			+ ",'"+userCode+"','"+userCode+"','"+date+"','"+date+"'"
            			+ ",'"+clientCode+"') ";
                    st1.executeUpdate(sql);
                    st1.close();
                }
				
                arrObj.put(obj);
				jObj.put("CardInfo", arrObj);
				posCon.close();
				st.close();
			}
		
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return jObj.toString();
	}
	
	
	
	private String funGetCashCardDtl(String cardNo,String userCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
          
        try
        {
        	posCon=objDb.funOpenAPOSCon("mysql","master");
	        st = posCon.createStatement();
	        
	        JSONArray arrObj=new JSONArray();
			String sql="select strStatus from tbldebitcardmaster "
				+ "where strCardString='"+cardNo+"'";
			System.out.println(sql);
			ResultSet rsCardInfo=st.executeQuery(sql);
			if(rsCardInfo.next())
			{
				if(rsCardInfo.getString(1).equals("Active"))
				{
					sql= " select a.dblRedeemAmt, ifnull(b.strCustomerName,''), ifnull(b.strCustomerCode,''), c.strCardName"
						+ ", c.dteExpiryDt, c.strCardTypeCode, a.strCardNo, c.strCashCard, c.dblCardValueFixed "
						+  " from tbldebitcardmaster a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
						+  " left outer join tbldebitcardtype c on a.strCardTypeCode=c.strCardTypeCode "
						+  " where a.strCardString='"+cardNo+"' ";
					System.out.println(sql);
					
		            ResultSet rsCardBalance=st.executeQuery(sql);
		            while(rsCardBalance.next())
		            {
		            	JSONObject obj=new JSONObject();
		            	
		            	obj.put("CardType","CashCard");
		            	obj.put("CardStatus","A");
		            	obj.put("CardBalance",rsCardBalance.getString(1));
		            	obj.put("CustName",rsCardBalance.getString(2));
		            	obj.put("CustCode",rsCardBalance.getString(3));
		            	obj.put("CardName",rsCardBalance.getString(4));
		            	obj.put("CardExpriryDate",rsCardBalance.getString(5));
		            	obj.put("CardTypeCode",rsCardBalance.getString(6));
		            	obj.put("CardNo",rsCardBalance.getString(7));
		            	obj.put("CashCard",rsCardBalance.getString(8));
		            	obj.put("CardDepositeValue",rsCardBalance.getString(9));		            
		            	arrObj.put(obj);
		            }
		            rsCardBalance.close();
		            jObj.put("CardInfo", arrObj);
		            st.close();
		            posCon.close();
		        }
				else // Card is not active
				{
					JSONObject obj=new JSONObject();
					obj.put("CardType","CashCard");
					obj.put("CardStatus","E");
					arrObj.put(obj);
					jObj.put("CardInfo", arrObj);
				}
			}
			else
			{
				JSONObject obj=new JSONObject();
				obj.put("CardType","CashCard");
              	obj.put("CustCode","Card Not Registered");
              	arrObj.put(obj);
				jObj.put("CardInfo", arrObj);
				
			}
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return jObj.toString();
	}
	
	
	
	
	private String funGetCashCardDetails(String cardNo,String userCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
          
        try
        {
        	posCon=objDb.funOpenAPOSCon("mysql","master");
	        st = posCon.createStatement();
	        
	        JSONArray arrObj=new JSONArray();
			String sql="select strStatus from tbldebitcardmaster "
				+ "where strCardNo='"+cardNo+"'";
			System.out.println(sql);
			ResultSet rsCardInfo=st.executeQuery(sql);
			if(rsCardInfo.next())
			{
				if(rsCardInfo.getString(1).equals("Active"))
				{
					sql= " select a.dblRedeemAmt, ifnull(b.strCustomerName,''), ifnull(b.strCustomerCode,''), c.strCardName"
						+ ", c.dteExpiryDt, c.strCardTypeCode, a.strCardNo, c.strCashCard, c.dblCardValueFixed "
						+  " from tbldebitcardmaster a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
						+  " left outer join tbldebitcardtype c on a.strCardTypeCode=c.strCardTypeCode "
						+  " where a.strCardNo='"+cardNo+"' ";
					System.out.println(sql);
					
		            ResultSet rsCardBalance=st.executeQuery(sql);
		            while(rsCardBalance.next())
		            {
		            	JSONObject obj=new JSONObject();
		            	
		            	obj.put("CardType","CashCard");
		            	obj.put("CardStatus","A");
		            	obj.put("CardBalance",rsCardBalance.getString(1));
		            	obj.put("CustName",rsCardBalance.getString(2));
		            	obj.put("CustCode",rsCardBalance.getString(3));
		            	obj.put("CardName",rsCardBalance.getString(4));
		            	obj.put("CardExpriryDate",rsCardBalance.getString(5));
		            	obj.put("CardTypeCode",rsCardBalance.getString(6));
		            	obj.put("CardNo",rsCardBalance.getString(7));
		            	obj.put("CashCard",rsCardBalance.getString(8));
		            	obj.put("CardDepositeValue",rsCardBalance.getString(9));		            	arrObj.put(obj);
		            }
		            rsCardBalance.close();
		            jObj.put("CardInfo", arrObj);
		            st.close();
		            posCon.close();
		        }
				else // Card is not active
				{
					JSONObject obj=new JSONObject();
					obj.put("CardType","CashCard");
					obj.put("CardStatus","E");
					arrObj.put(obj);
					jObj.put("CardInfo", arrObj);
				}
			}
			else
			{
				JSONObject obj=new JSONObject();
				obj.put("CardType","CashCard");
              	obj.put("CustCode","Card Not Registered");
              	arrObj.put(obj);
				jObj.put("CardInfo", arrObj);
				
			}
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return jObj.toString();
	}
	
	
	
	
	
	@GET
	@Path("/funCallSearchForm")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funCallSearchForm(@QueryParam("FormName") String formName,@QueryParam("POSCode") String POSCode
    		,@QueryParam("ClientCode") String clientCode)
	{
		return funInvokeSearchForm(formName,POSCode,clientCode);
	}
	
	
	private JSONArray funInvokeSearchForm(String formName,String POSCode,String clientCode)
	{
		String searchDetails="";
		JSONArray jArrSearchDetails=new JSONArray();
		switch(formName)
		{
			case "Customer":
				jArrSearchDetails=funGetCustomerData(formName, POSCode, clientCode);
				break;
				
			case "UnReservedTableList":
				jArrSearchDetails=funGetUnReservedTableList(formName, POSCode, clientCode);
				break;
				
			case "TableReservation":
				jArrSearchDetails=funGetTableReservationDetailList(formName, POSCode, clientCode);
				break;	
				
			case "CostCenter":
				jArrSearchDetails=funGetCostCenterData(formName, POSCode, clientCode);
				break;	
				
			case "Area":
				jArrSearchDetails=funGetAreaList(formName, POSCode, clientCode);
				break;	
		}
		
		return jArrSearchDetails;
	}
	
	
	private JSONArray funGetCustomerData(String formName,String POSCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();  
        try
        {
	        cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
	      
			String sql="select a.strCustomerCode,a.strCustomerName,b.strCustType,a.longMobileNo "
				+ "from tblcustomermaster a,tblcustomertypemaster b "
				+ "where a.strCustomerType=b.strCustTypeCode and a.strClientCode='"+clientCode+"' "
			    + " group by a.strCustomerCode ";
			System.out.println(sql);
			ResultSet rsCustInfo=st.executeQuery(sql);
			while(rsCustInfo.next())
			{
				JSONObject obj=new JSONObject();
		        obj.put("CustCode",rsCustInfo.getString(1));
		        obj.put("CustName",rsCustInfo.getString(2));
		        obj.put("CustType",rsCustInfo.getString(3));
		        if(rsCustInfo.getString(4).isEmpty())
		        {
		        	obj.put("CustomerMobileNo","0");
		        }
		        else
		        {
		        	obj.put("CustomerMobileNo",rsCustInfo.getString(4));
		        }
		        
		        arrObj.put(obj);		        
			}
			if(arrObj.length()==0)
			{
				JSONObject obj=new JSONObject();
				obj.put("CustCode","No Customer Found");
				arrObj.put(obj);
			}
			jObj.put("CustomerDetails", arrObj);
			rsCustInfo.close();
			cmsCon.close();
			st.close();
		
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return arrObj;//jObj.toString();
	}
	
	
	
	public JSONArray funGetUnReservedTableList(String formName,String POSCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            
            String sql="";
            sql = " select a.strTableNo,a.strTableName,a.strStatus from tbltablemaster a "
            	+ " where a.strStatus!='Reserve' and a.strOperational='Y' "
            	+ " and a.strClientCode='"+clientCode+"' and a.strPOSCode='"+POSCode+"' ";
            //System.out.println(sql);
            
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("TableNo",rsTableInfo.getString(1));
            	obj.put("TableName",rsTableInfo.getString(2));
            	obj.put("TableStatus",rsTableInfo.getString(3));
            	arrObj.put(obj);
            }
            if(arrObj.length()==0)
			{
				JSONObject obj=new JSONObject();
				obj.put("TableNo","No Table Found");
				arrObj.put(obj);
			}
			jObj.put("UnReservedTableList", arrObj);
			rsTableInfo.close();
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	
	public JSONArray funGetTableReservationDetailList(String formName,String POSCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            
            String sql="";
            sql=" select a.strResCode,ifnull(b.strCustomerCode,''),ifnull(b.strCustomerName,''),ifnull(b.strBuldingCode,''),  "
               + " ifnull(b.strBuildingName,''),b.strCity,ifnull(b.longMobileNo,0),ifnull(a.strTableNo,''),a.dteResDate,  "
               + " a.tmeResTime,a.intPax,a.strSmoking,a.strSpecialInfo,ifnull(d.strTableName,''),a.strAMPM   "
               + " from tblreservation a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
               + " left outer join tblbuildingmaster c on b.strBuldingCode=c.strBuildingCode  "
               + " left outer join tbltablemaster d  on a.strTableNo=d.strTableNo  "
               + " where a.strClientCode='"+clientCode+"' ";
           
            System.out.println(sql);
            
           
            ResultSet rsReservationInfo = st.executeQuery(sql);
            while (rsReservationInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("ReservationNo",rsReservationInfo.getString(1));
            	obj.put("CustomerCode",rsReservationInfo.getString(2));
            	obj.put("CustomerName",rsReservationInfo.getString(3));
            	obj.put("MobileNo",rsReservationInfo.getString(7));
            	obj.put("TableNo",rsReservationInfo.getString(8));
            	obj.put("TableName",rsReservationInfo.getString(14));
            	obj.put("PaxNo",rsReservationInfo.getString(11));
            	obj.put("ResDate",rsReservationInfo.getString(9));
            	obj.put("ResTime",rsReservationInfo.getString(10)+" "+rsReservationInfo.getString(15));
            	obj.put("Comments",rsReservationInfo.getString(13));
            	arrObj.put(obj);
            }
           
			//jObj.put("ReservationDtl", arrObj);
			rsReservationInfo.close();
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	
	private JSONArray funGetCostCenterData(String formName,String POSCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try
        {
	        cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
	      
	        String sql="select strCostCenterCode as CostCenter_Code,strCostCenterName as CostCenter_Name  "
	        		+ "from tblcostcentermaster where strClientCode='"+clientCode+"'  "
	        		+ "order by strCostCenterName";
			System.out.println(sql);
			ResultSet rsCustInfo=st.executeQuery(sql);
			while(rsCustInfo.next())
			{
				JSONObject obj=new JSONObject();
		        obj.put("CostCenterCode",rsCustInfo.getString(1));
		        obj.put("CostCenterName",rsCustInfo.getString(2));
		        
		        arrObj.put(obj);		        
			}
			if(arrObj.length()==0)
			{
				JSONObject obj=new JSONObject();
				obj.put("CostCenterCode","No CostCenter Found");
				arrObj.put(obj);
			}
			//jObj.put("CostCenterDetails", arrObj);
			rsCustInfo.close();
			cmsCon.close();
			st.close();
		
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return arrObj;//jObj.toString();
	}
	
	
	private JSONArray funGetAreaList(String formName,String POSCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try
        {
	        cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
	      
	        String sql="select strAreaCode as AreaCode,strAreaName as areaName from tblareamaster "
	        		+ "where strClientCode='"+clientCode+"' "
	        		+ "order by strAreaName";
			System.out.println(sql);
			ResultSet rsAreaInfo=st.executeQuery(sql);
			while(rsAreaInfo.next())
			{
				JSONObject obj=new JSONObject();
		        obj.put("AreaCode",rsAreaInfo.getString(1));
		        obj.put("AreaName",rsAreaInfo.getString(2));
		        
		        arrObj.put(obj);		        
			}
			if(arrObj.length()==0)
			{
				JSONObject obj=new JSONObject();
				obj.put("AreaCode","No Area Found");
				arrObj.put(obj);
			}
			//jObj.put("CostCenterDetails", arrObj);
			rsAreaInfo.close();
		    cmsCon.close();
		    st.close();
		    
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return arrObj;//jObj.toString();
	}
	
	
	
	public String funGetPreviousKOTDetailList(String formName,String POSCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
		
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            
            String sql="";
            sql="select a.strTableNo,a.strItemCode,a.strItemName,a.dblAmount,a.dblItemQuantity,a.dblRate from tblitemrtemp a where a.strTableNo='TB0000001'; ";
           
            System.out.println(sql);
            
            JSONArray arrObj=new JSONArray();
            ResultSet rsReservationInfo = st.executeQuery(sql);
            while (rsReservationInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("ReservationNo",rsReservationInfo.getString(1));
            	obj.put("CustomerCode",rsReservationInfo.getString(2));
            	obj.put("CustomerName",rsReservationInfo.getString(3));
            	obj.put("MobileNo",rsReservationInfo.getString(7));
            	obj.put("TableNo",rsReservationInfo.getString(8));
            	obj.put("TableName",rsReservationInfo.getString(14));
            	obj.put("PaxNo",rsReservationInfo.getString(11));
            	obj.put("ResDate",rsReservationInfo.getString(9));
            	obj.put("ResTime",rsReservationInfo.getString(10)+" "+rsReservationInfo.getString(15));
            	obj.put("Comments",rsReservationInfo.getString(13));
            	arrObj.put(obj);
            }
           
			jObj.put("ReservationDtl", arrObj);
			rsReservationInfo.close();
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return jObj.toString();
        }
    }
	
	
	@GET
	@Path("/funGetCustomerDetail")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetCustomerDetail(@QueryParam("MobileNo")String mobileNo ,@QueryParam("POSCode") String POSCode
    		,@QueryParam("ClientCode") String clientCode)
	{
		return funGetCustomerDetails(mobileNo,POSCode,clientCode);
	}
	
	private JSONArray funGetCustomerDetails(String mobileNo,String POSCode,String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        JSONArray arrObj=new JSONArray();
	        try
	        {
		        cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
		        
		        String sql=" select a.strCustomerCode,a.strCustomerName,a.strStreetName,a.strCustomerType,b.strCustType "
						+ " from tblcustomermaster a, tblcustomertypemaster b  "
						+ " where a.strCustomerType=b.strCustTypeCode "
						+ " and a.longMobileNo='"+mobileNo+"' ";
				System.out.println(sql);
				ResultSet rsCustInfo=st.executeQuery(sql);
				if(rsCustInfo.next())
				{
					JSONObject obj=new JSONObject();
			        obj.put("CustCode",rsCustInfo.getString(1));
			        obj.put("CustName",rsCustInfo.getString(2));
			        obj.put("BuildingName",rsCustInfo.getString(3));
			        obj.put("CustTypeCode",rsCustInfo.getString(4));
			        obj.put("CustType",rsCustInfo.getString(5));
			        arrObj.put(obj);		        
				}
				if(arrObj.length()==0)
				{
					/*JSONObject obj=new JSONObject();
					obj.put("CustCode","No Customer Found");
					arrObj.put(obj);*/
				}
//				jObj.put("CustomerDetails", arrObj);
				rsCustInfo.close();
			    cmsCon.close();
			    st.close();
			}catch(Exception e)
		    {
		        e.printStackTrace();
		    }
	        
	        return arrObj;//jObj.toString();
		}	
	
	
	
	
	
	
	@GET
	@Path("/funSaveNewCustomer")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject funSaveNewCustomerDtl(@QueryParam("CustomerName") String cutomerName
   		,@QueryParam("MobileNo") String mobileNo ,@QueryParam("CustType") String custType,@QueryParam("Address") String address
   		,@QueryParam("ClientCode") String clientCode,@QueryParam("UserCode") String userCode,@QueryParam("DateTime") String dateTime,@QueryParam("Email") String strEmail)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        int retRows=0;
        if(cutomerName.contains("%20")){
        	cutomerName=cutomerName.replaceAll("%20", " ");
        }
        if(address.contains("%20")){
        	address=address.replaceAll("%20", " ");
        }
        if(dateTime.contains("%20")){
        	dateTime=dateTime.replaceAll("%20", " ");
        }
        String customerCode=getCustomercode(clientCode);
		JSONObject jObj=new JSONObject();
		try 
		{
			if(funCheckMobileNoForCustomer(mobileNo))
			{
					jObj.put("CustomerStatus", "false");
					jObj.put("Reason", "MobileNo");
					System.out.println("Cust");
			}
			else
			{
					cmsCon=objDb.funOpenAPOSCon("mysql","master");
			        st = cmsCon.createStatement();
				
					String sql="insert into tblcustomermaster "
						+ "(strCustomerCode,strCustomerName,strBuldingCode,strBuildingName,strStreetName,strLandmark,strArea,strCity "
						+ ",strState,intPinCode,longMobileNo,longAlternateMobileNo,strOfficeBuildingCode,strOfficeBuildingName "
						+ ",strOfficeStreetName,strOfficeLandmark,strOfficeArea,strOfficeCity,strOfficePinCode,strOfficeState "
						+ ",strOfficeNo,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strDataPostFlag,strClientCode "
						+ ",strOfficeAddress,strExternalCode,strCustomerType,dteDOB,strGender,dteAnniversary,strEmailId,strCRMId) "
			            + "values('" + customerCode + "','" + cutomerName + "','','','"+address+"','','','' "
		           		+ ",'','','" + mobileNo + "','','','','','','','','','','','"+ userCode + "','"+ userCode + "','"+dateTime+"' "
		           		+ ",'"+dateTime+"','N','" + clientCode + "','N','','"+custType+"','','Male','','"+strEmail+"','' )";
					System.out.println(sql);
					
					retRows=st.executeUpdate(sql);
				
					if(retRows>0)
					{
						jObj.put("CustomerStatus", customerCode);
						jObj.put("Reason", "Success");
					}
					else
						{
							jObj.put("CustomerStatus", "false");
							jObj.put("Reason", "Error");
						}
					
			}	
			cmsCon.close();
			st.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try 
			{
				jObj.put("CustomerStatus", "false");
				jObj.put("Reason", "Exception");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		finally
		{
			return jObj;//.toString();
		}
	}

  
    private String getCustomercode(String clientCode)
    {
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        String customerCode = "", strCode = "", code = "";
        long lastNo = 1;
        String propertCode=clientCode.substring(4);
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
		
            String sql = "select count(*) from tblcustomermaster";
            ResultSet rsCustCode = st.executeQuery(sql);
            rsCustCode.next();
            int cntCustCode = rsCustCode.getInt(1);
            rsCustCode.close();

            if (cntCustCode > 0)
            {
                sql = "select max(right(strCustomerCode,8)) from tblcustomermaster";
                rsCustCode = st.executeQuery(sql);
                rsCustCode.next();
                code = rsCustCode.getString(1);
                StringBuilder sb = new StringBuilder(code);                
               
                strCode = sb.substring(1,sb.length());
                
                lastNo = Long.parseLong(strCode);
                lastNo++;
                customerCode = propertCode+"C" + String.format("%07d", lastNo);

                rsCustCode.close();
            }
            else
            {
                sql = "select longCustSeries from tblsetup";
                ResultSet rsCustSeries = st.executeQuery(sql);
                if (rsCustSeries.next())
                {
                    lastNo = Long.parseLong(rsCustSeries.getString(1));
                }
                rsCustSeries.close();
                customerCode = propertCode+"C" + String.format("%07d", lastNo);
             
            }
            cmsCon.close();
            st.close();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return customerCode;
    }
 	
	
	@GET
	@Path("/funGetCustomerType")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetCustomerTypeData(@QueryParam("ClientCode") String clientCode)
	{
		return funGetCustomerypeDetails();
	}
	
	private JSONArray funGetCustomerypeDetails()
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray(); 
        try
        {
	        cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
	        String sql= "select strCustTypeCode,strCustType from tblcustomertypemaster";
			System.out.println(sql);
			ResultSet rsCustType=st.executeQuery(sql);
			while(rsCustType.next())
			{
				JSONObject obj=new JSONObject();
				
		        obj.put("CustTypeCode",rsCustType.getString(1));
		        obj.put("CustType",rsCustType.getString(2));
		        
		        arrObj.put(obj);
		       // jObj.put("CustomerTypeDetails", arrObj);
			}
			rsCustType.close();
			cmsCon.close();
			st.close();
		
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return arrObj;//jObj.toString();
	}
	
	
	@GET
	@Path("/funGetCardTypeDetails")
	@Produces(MediaType.APPLICATION_JSON)
    public String funGetCardtypeData(@QueryParam("ClientCode") String clientCode)
	{
		return funGetCardTypeDetails(clientCode);
	}
	
	private String funGetCardTypeDetails(String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
          
        try
        {
	        cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
	        JSONArray arrObj=new JSONArray();
			String sql="select strCardTypeCode,strCardName,strAllowTopUp,dteExpiryDt"
				+ ",intValidityDays,strCustomerCompulsory,strCashCard "
				+ "from tbldebitcardtype where strClientCode='"+clientCode+"'";
			System.out.println(sql);
			ResultSet rsCardTypeInfo=st.executeQuery(sql);
			while(rsCardTypeInfo.next())
			{
				JSONObject obj=new JSONObject();
				String expired=funCheckCardTypeExpiry(rsCardTypeInfo.getString(4));
				 obj.put("CardTypeCode",rsCardTypeInfo.getString(1));
		        obj.put("CardTypeDesc",rsCardTypeInfo.getString(2));
		        obj.put("AllowTopUp",rsCardTypeInfo.getString(3));
		        obj.put("Exipired",expired);
		        obj.put("ValidityDays",rsCardTypeInfo.getString(5));
		        obj.put("CustCompulsory",rsCardTypeInfo.getString(6));
		        obj.put("CashCard",rsCardTypeInfo.getString(7));
		        arrObj.put(obj);
		        jObj.put("CardTypeDetails", arrObj);
			}
			rsCardTypeInfo.close();
			cmsCon.close();
			st.close();
		     
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return jObj.toString();
	}
	
	
	@GET
	@Path("/funGetPaymodeType")
	@Produces(MediaType.APPLICATION_JSON)
    public String funGetPayModeTypeData(@QueryParam("ClientCode") String clientCode,@QueryParam("CardTypeCode") String cardTypeCode)
	{
		return funGetPayModeTypeDetails(cardTypeCode);
	}
	
	private String funGetPayModeTypeDetails(String cardTypeCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
          
        try
        {
	        cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
	        JSONArray arrObj=new JSONArray();
			String sql= "  select b.strSettelmentDesc,b.strSettelmentType "
	                  + " from tbldebitcardsettlementdtl a,tblsettelmenthd b "
	                  + " where a.strSettlementCode=b.strSettelmentCode and a.strCardTypeCode='" +cardTypeCode+ "'";
			System.out.println(sql);
			ResultSet rsPaymentMode=st.executeQuery(sql);
			while(rsPaymentMode.next())
			{
				JSONObject obj=new JSONObject();
				
		        obj.put("SettleDesc",rsPaymentMode.getString(1));
		        obj.put("SettleType",rsPaymentMode.getString(2));
		        
		        arrObj.put(obj);
		        jObj.put("PaymodTypeDetails", arrObj);
			}
			rsPaymentMode.close();
			cmsCon.close();
			st.close();
		
		}catch(Exception e)
	    {
	        e.printStackTrace();
	    }
        
        return jObj.toString();
	}
	
	
	
	
	
	private String funCheckCardTypeExpiry(String validityDate)
	{
		String expired="N";
		long diff = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dtValidityDays = null;
        Date dtCurrentDate = null;

        try
        {
        	String currentDate=(new Date().getYear()+1900)+"-"+(new Date().getMonth()+1)+"-"+new Date().getDate();
        	dtValidityDays = format.parse(validityDate);
            dtCurrentDate = format.parse(currentDate);
            
            diff = dtValidityDays.getTime()-dtCurrentDate.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            String time = diffHours + ":" + diffMinutes + ":" + diffSeconds;

            if(diffDays<0)
            {
            	expired="Y";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		
		return expired;
	}
	
	
	@GET
	@Path("/funRegisterDebitCard")
	@Produces(MediaType.APPLICATION_JSON)
    public String funRegDebitCard(@QueryParam("CardTypeCode") String cardTypeCode,@QueryParam("CardNo") String cardString
   		,@QueryParam("RedeemAmt") String redeemAmt ,@QueryParam("CardStatus") String cardStatus
   		,@QueryParam("CustCode") String customerCode,@QueryParam("UserCode") String userCode
   		,@QueryParam("DateTime") String dateTime,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        int retRows=0;
		JSONObject jObj=new JSONObject();
		try 
		{
			if(funCheckCustomerForCard(customerCode))
			{
				jObj.put("DCRegStatus", "false");
				jObj.put("Reason", "Customer");
				System.out.println("Cust");
			}
			else if(funCheckForCard(cardString))
			{
				jObj.put("DCRegStatus", "false");
				jObj.put("Reason", "DebitCard");
				System.out.println("DC");
			}
			else
			{			
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
				
		        long lastNo=funGetDebitCardNo();
		        int totalLength=cardTypeCode.length();
		        int startIndex=totalLength-3;
		        String cardNo="";
		        cardNo=cardTypeCode.substring(startIndex, totalLength);
	            cardNo=cardNo+String.format("%06d", lastNo);
	            
				String sql="insert into tbldebitcardmaster "
					+ "(strCardTypeCode,strCardNo,dblRedeemAmt,strStatus,strUserCreated,dteDateCreated"
					+ ",strCustomerCode,strDataPostFlag,strClientCode,strCardString) "
		            + "values('" + cardTypeCode + "','" + cardNo + "','" + redeemAmt + "','" + cardStatus + "'"
	           		+ ",'" + userCode + "','"+ dateTime + "','" + customerCode + "','N','" + clientCode + "'"           				
	           		+ ",'"+cardString+"')";
				System.out.println(sql);
				
				retRows=st.executeUpdate(sql);
			
				if(retRows>0)
				{
					jObj.put("DCRegStatus", "true");
					jObj.put("Reason", "Success");
				}
				else
				{
					jObj.put("DCRegStatus", "false");
					jObj.put("Reason", "Error");
				}
			}
			cmsCon.close();
			st.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try 
			{
				jObj.put("DCRegStatus", "false");
				jObj.put("Reason", "Exception");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		finally
		{
			return jObj.toString();
		}
	}
	
	private long funGetDebitCardNo() throws Exception
    {
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection con=null;
        Statement st=null;
        
        con=objDb.funOpenAPOSCon("mysql","master");
        st = con.createStatement();
        
        long lastNo=0;
        String sql = "select count(dblLastNo) from tblinternal where strTransactionType='CardNo'";
        ResultSet rs = st.executeQuery(sql);
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        
        st.close();
        st = con.createStatement();
        if (count > 0) {
            sql = "select dblLastNo from tblinternal where strTransactionType='CardNo'";
            rs = st.executeQuery(sql);
            rs.next();
            long code = rs.getLong(1);
            code = code + 1;
            lastNo = code;
            rs.close();
        } else {
            lastNo = 1;
        }
        
        st.close();
        st = con.createStatement();
        String updateSql = "update tblinternal set dblLastNo=" + lastNo + " "
                + "where strTransactionType='CardNo'";
        st.executeUpdate(updateSql);
        con.close();
        st.close();
        
        return lastNo;
    }
	
	
	/**
     * This method is used to check customer for card
     *
     * @param customerCode
     * @return
     */
    private boolean funCheckCustomerForCard(String customerCode) {
        
    	boolean flgCustomerCount = false;
        clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection con=null;
        Statement st=null;
        
        try {
        	con=objDb.funOpenAPOSCon("mysql","master");
            st = con.createStatement();
            
            if(!customerCode.isEmpty())
            {
            	String sql = "select strCustomerCode from tbldebitcardmaster "
                        + "where strCustomerCode='" + customerCode + "' and strStatus='Active'";
            	System.out.println(sql);
                ResultSet rsCustomerData = st.executeQuery(sql);
                if (rsCustomerData.next()) 
                {                
                	flgCustomerCount = true;
                }
                rsCustomerData.close();
            }
            con.close();
            st.close();
           
            
        } catch (Exception e) {
            flgCustomerCount = true;
            e.printStackTrace();
        } finally {
            return flgCustomerCount;
        }
    }
	
	
    /**
     * This method is used to check for card
     *
     * @param debitCardNo
     * @return
     */
    private boolean funCheckForCard(String debitCardString) {
        
    	boolean flgCardNo = false;
        clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection con=null;
        Statement st=null;
        
        try {
        	con=objDb.funOpenAPOSCon("mysql","master");
            st = con.createStatement();
            
        	String sql = "select strCardString from tbldebitcardmaster where strCardString='" + debitCardString + "'";
        	System.out.println(sql);
        	ResultSet rs = st.executeQuery(sql);
            if (rs.next()) 
            {                
            	flgCardNo = true;
            }
            rs.close();
            con.close();
            st.close();
            
            
        } catch (Exception e) {
        	flgCardNo = true;
            e.printStackTrace();
        } finally {
            return flgCardNo;
        }
    }
    
	

	/**
     * This method is used to check mobile no for customer
     *
     * @param mobileNo
     * @return
     */
    private boolean funCheckMobileNoForCustomer(String mobileNo) {
        
    	boolean flgCustomerCount = false;
        clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection con=null;
        Statement st=null;
        
        try {
        	con=objDb.funOpenAPOSCon("mysql","master");
            st = con.createStatement();
            
            if(!mobileNo.isEmpty())
            {
            	String sql = "select longMobileNo from tblcustomermaster where longMobileNo='"+mobileNo+"';";
            	System.out.println(sql);
                ResultSet rsCustomerData = st.executeQuery(sql);
                if (rsCustomerData.next()) 
                {                
                	flgCustomerCount = true;
                }
                rsCustomerData.close();
            }
            con.close();
            st.close();
            
        } catch (Exception e) {
            flgCustomerCount = true;
            e.printStackTrace();
        } finally {
            return flgCustomerCount;
        }
    }
	
    
    
	
	@GET
	@Path("/funRechargeDebitCard")
	@Produces(MediaType.APPLICATION_JSON)
    public String funRechargeDebitCard(@QueryParam("CardTypeCode") String cardTypeCode ,@QueryParam("CardString") String cardString
   		,@QueryParam("Redeemable") String redeemable,@QueryParam("Complementary") String complementary
   		,@QueryParam("Amount") String amount,@QueryParam("DateTime") String dateTime
   		,@QueryParam("POSCode") String POSCode,@QueryParam("Remarks") String remarks,@QueryParam("RechargeType") String rechargeType
   		,@QueryParam("UserCode") String userCode,@QueryParam("ClientCode") String clientCode,@QueryParam("BalanceAmt") String balanceAmt
   		,@QueryParam("MemberCode") String memberCode,@QueryParam("RechargeText") String rechargeText,@QueryParam("CardNo") String cardNo,@QueryParam("SettleMode") String mode)
	{
		String rechargeNo=funGetRechargeNo();
		String redeemNo=funGetRedeemNo();
		String slipNo=funGetSlipNo();
		
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        int retRows=0;
		JSONObject jObj=new JSONObject();
		try 
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
			String sql = "insert into tbldebitcardrecharge (intRechargeNo,intRedeemNo,strCardTypeCode,strCardString,"
				+ "strRedeemable,strComplementary,dblRechargeAmount,strUserCreated,dteDateCreated,"
                + "strPOSCode,strRemarks,strRechargeType,strClientCode,strMemberCode,strRechargeSlipNo,strCardNo,strRechargeSettlmentMode) "
                + "values ('"+rechargeNo+"','"+redeemNo+"','"+cardTypeCode+ "'"+ ",'" + cardString + "'"
                + ",'"+ redeemable + "','" + complementary + "','"+ amount + "','" + userCode + "'"
                + ",'"+ dateTime + "','"+POSCode+"','"+remarks+"','"+rechargeType+"','"+clientCode+"','"+memberCode+"','"+slipNo+"','"+cardNo+"','"+mode+"')";
			retRows=st.executeUpdate(sql);
			System.out.println(memberCode);
			System.out.println(sql);
			String sql1 = "insert into tbldcrechargesettlementdtl (strRechargeNo,strSettlementCode,dblRechargeAmt,strCardNo,"
					 + "strType,strClientCode,strDataPostFlag) "
	                + "values ('"+rechargeNo+"','','"+amount+ "'"+ ",'" + cardNo + "'"
	                + ",'"+mode+"','"+clientCode+"','N')";
			System.out.println(sql1);
			st.executeUpdate(sql1);
			
			
			double redeemableAmount=Double.parseDouble(balanceAmt)+Double.parseDouble(amount);
			sql = "update tbldebitcardmaster set dblRedeemAmt='" + redeemableAmount+ "'"
				+ " ,strReachrgeRemark='"+rechargeText+"',strRefMemberCode='"+memberCode+"' where strCardString='" + cardString + "'";
			st.executeUpdate(sql);
		
			if(retRows>0)
			{
				jObj.put("DCRechargeStatus", "true");
				jObj.put("RechargeNo", rechargeNo);
				jObj.put("BalanceAmt", redeemableAmount);
				jObj.put("SlipNo", slipNo);
			}
			else
			{
				jObj.put("DCRechargeStatus", "false");
			}
			
			cmsCon.close();
			st.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try 
			{
				jObj.put("DCRechargeStatus", "false");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		finally
		{
			return jObj.toString();
		}
	}
	
	
	/**
	    * This method is used to get slip no
	    * @return string
	    */
	    private String funGetSlipNo()
	    {
	    	 String slipNo = "";
	         clsDatabaseConnection objDb=new clsDatabaseConnection();
	         Connection cmsCon=null;
	         Statement st=null;
	 		
	 		try {
	 			
	 			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	 	        st = cmsCon.createStatement();
	 	        
	             long code = 0;
	             String sql = "select dblLastNo from tblinternal where strTransactionType='SlipNo'";
	             ResultSet rsSlipNo= st.executeQuery(sql);
	             if (rsSlipNo.next())
	             {
	                 code = rsSlipNo.getLong(1);
	                 code = code + 1;
	                 slipNo = "SL" + String.format("%07d", code);
	                 sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='SlipNo'";
		             st.executeUpdate(sql);
	             }
	             else
	             {
	            	 slipNo = "SL0000001";
	            	 sql = "insert into tblinternal values('SlipNo'," + 1 + ")";
	            	 st.executeUpdate(sql);
	             }
	             rsSlipNo.close();
	             cmsCon.close();
	             st.close();
	            
	         }
	         catch (Exception e)
	         {
	             e.printStackTrace();
	         }
	        
	        return slipNo;
	    }
	
	
	
	
	/**
	    * This method is used to get recharge no
	    * @return string
	    */
	    private String funGetRechargeNo()
	    {
	    	/*clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection hoCon=null;
	        Statement st=null;
	    	
	        String selectQuery="",strCode="",rechargeNo="";
	        try 
	        {
	        	hoCon=objDb.funOpenAPOSCon("mysql","master");
		        st = hoCon.createStatement();
	            selectQuery = "select count(*) from tbldebitcardrecharge";
	            ResultSet rsCountSet1 = st.executeQuery(selectQuery);
	            rsCountSet1.next();
	            int cn = rsCountSet1.getInt(1);
	            rsCountSet1.close();
	            if (cn > 0) 
	            {
	                selectQuery = "select max(intRechargeNo) from tbldebitcardrecharge";
	                ResultSet rsCountSet = st.executeQuery(selectQuery);
	                rsCountSet.next();
	                String code = rsCountSet.getString(1);
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
	                    rechargeNo = "RC000000" + intCode;
	                }
	                else if (intCode < 100) 
	                {
	                    rechargeNo = "RC00000" + intCode;
	                }
	                else if (intCode < 1000) 
	                {
	                    rechargeNo = "RC0000" + intCode;
	                }
	                else if (intCode < 10000) 
	                {
	                    rechargeNo = "RC000" + intCode;
	                }
	                else if (intCode < 100000) 
	                {
	                    rechargeNo = "RC00" + intCode;
	                }
	                else if (intCode < 1000000) 
	                {
	                    rechargeNo = "RC0" + intCode;
	                }
	            } 
	            
	            else 
	            {
	                rechargeNo = "RC0000001";
	            }
	        } catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        */
	    	 String rechargeNo = "";
	         clsDatabaseConnection objDb=new clsDatabaseConnection();
	         Connection cmsCon=null;
	         Statement st=null;
	 		
	 		try {
	 			
	 			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	 	        st = cmsCon.createStatement();
	 	        
	             long code = 0;
	             String sql = "select dblLastNo from tblinternal where strTransactionType='RechargeNo'";
	             ResultSet rsRecharge= st.executeQuery(sql);
	             if (rsRecharge.next())
	             {
	                 code = rsRecharge.getLong(1);
	                 code = code + 1;
	                 rechargeNo = "RC" + String.format("%07d", code);
	                 sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='RechargeNo'";
		             st.executeUpdate(sql);
	             }
	             else
	             {
	            	 rechargeNo = "RC0000001";
	            	 sql = "insert into tblinternal values('RechargeNo'," + 1 + ")";
	            	 st.executeUpdate(sql);
	             }
	             rsRecharge.close();
	             cmsCon.close();
	             st.close();
	            
	         }
	         catch (Exception e)
	         {
	             e.printStackTrace();
	         }
	        
	        return rechargeNo;
	    }
	    /**
	     * This method is used to get redeem no
	     * @return string
	     */
	    private String funGetRedeemNo()
	    {
	    	 String redeemNo = "";
	         clsDatabaseConnection objDb=new clsDatabaseConnection();
	         Connection cmsCon=null;
	         Statement st=null;
	 		
	 		try {
	 			
	 			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	 	        st = cmsCon.createStatement();
	 	        
	             long code = 0;
	             String sql = "select dblLastNo from tblinternal where strTransactionType='RedeemNo'";
	             ResultSet rsRedeem= st.executeQuery(sql);
	             if (rsRedeem.next())
	             {
	                 code = rsRedeem.getLong(1);
	                 code = code + 1;
	                 redeemNo = "RD" + String.format("%07d", code);
	                 sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='RedeemNo'";
		             st.executeUpdate(sql);
	             }
	             else
	             {
	            	 redeemNo = "RD0000001";
	            	 sql = "insert into tblinternal values('RedeemNo'," + 1 + ")";
	            	 st.executeUpdate(sql);
	             }
	             rsRedeem.close();
	             cmsCon.close();
	             st.close();
	           
	         }
	         catch (Exception e)
	         {
	             e.printStackTrace();
	         }
	        
	    
	        return redeemNo;
	   }
	    
	    
	    /**
		    * This method is used to get refundSlip no
		    * @return string
		    */
		    private String funGetRefundSlipNo()
		    {
		    	 String refundSlipNo = "";
		         clsDatabaseConnection objDb=new clsDatabaseConnection();
		         Connection cmsCon=null;
		         Statement st=null;
		 		
		 		try {
		 			
		 			cmsCon=objDb.funOpenAPOSCon("mysql","master");
		 	        st = cmsCon.createStatement();
		 	        
		             long code = 0;
		             String sql = "select dblLastNo from tblinternal where strTransactionType='RefundSlipNo'";
		             ResultSet rsRefundSlipNo= st.executeQuery(sql);
		             if (rsRefundSlipNo.next())
		             {
		                 code = rsRefundSlipNo.getLong(1);
		                 code = code + 1;
		                 refundSlipNo = "RSL" + String.format("%07d", code);
		                 sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='RefundSlipNo'";
			             st.executeUpdate(sql);
		             }
		             else
		             {
		            	 refundSlipNo = "RSL0000001";
		            	 sql = "insert into tblinternal values('RefundSlipNo'," + 1 + ")";
		            	 st.executeUpdate(sql);
		             }
		             rsRefundSlipNo.close();
		             cmsCon.close();
		             st.close();
		            
		         }
		         catch (Exception e)
		         {
		             e.printStackTrace();
		         }
		        
		        return refundSlipNo;
		    }
		
	    
	   
		    
	
			  
			    
			    
			    
	    
	    
		@GET
		@Path("/funRefundDebitCardAmount")
		@Produces(MediaType.APPLICATION_JSON)
	    public String funRefundDebitCardAmount(@QueryParam("CardTypeCode") String cardTypeCode ,@QueryParam("CardString") String cardString
	   		,@QueryParam("CardNo") String cardNo,@QueryParam("CardBalance") String cardBalance
	   		,@QueryParam("RefundAmount") String refundAmount,@QueryParam("UserCode") String userCode
	   		,@QueryParam("DateTime") String dateTime,@QueryParam("ClientCode") String clientCode
	   		,@QueryParam("POSCode") String POSCode,@QueryParam("DataPostFlag") String dataPostFlag)
	   		
		{
			String refundNo=funGetRefundNo();
			String refundSlipNo=funGetRefundSlipNo();
			String pos=POSCode;
			System.out.println(pos);
			
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        int retRows=0;
			JSONObject jObj=new JSONObject();
			try 
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
		        System.out.println(pos);
				double redeemableAmount=Double.parseDouble(cardBalance)-Double.parseDouble(refundAmount);
				String sql = "update tbldebitcardmaster set dblRedeemAmt='" + redeemableAmount+ "' "
					+ "where strCardString='" + cardString + "'";
				st.executeUpdate(sql);
				
				sql = "insert into tbldebitcardrefundamt (strRefundNo,strCardTypeCode,strCardString,strCardNo,"
						+ "dblCardBalance,dblRefundAmt,strUserCreated,dteDateCreated,"
		                + "strClientCode,strDataPostFlag,strRefundSlipNo,strPOSCode) "
		                + "values ('"+refundNo+"','"+cardTypeCode+"','"+cardString+ "'"+ ",'" + cardNo + "'"
		                + ",'"+ cardBalance + "','" + refundAmount + "','"+ userCode + "','" + dateTime + "'"
		                + ",'"+ clientCode + "','"+dataPostFlag+"','"+refundSlipNo+"','"+POSCode+"')";
					retRows=st.executeUpdate(sql);
					//System.out.println(memberCode);
					System.out.println(sql);
			
				if(retRows>0)
				{
					jObj.put("DCRefundStatus", "true");
					jObj.put("RefundNo", refundNo);
					jObj.put("BalanceAmt", redeemableAmount);
					jObj.put("RefundSlipNo", refundSlipNo);
				
				}
				else
				{
					jObj.put("DCRefundStatus", "false");
				}
				st.close();
				cmsCon.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try 
				{
					jObj.put("DCRefundStatus", "false");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
			finally
			{
				return jObj.toString();
			}
		}
		
	   
		
		/**
	     * This method is used to get redeem no
	     * @return string
	     */
	    private String funGetRefundNo()
	    {
	    	 String refundNo = "";
	         clsDatabaseConnection objDb=new clsDatabaseConnection();
	         Connection cmsCon=null;
	         Statement st=null;
	 		
	 		try {
	 			
	 			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	 	        st = cmsCon.createStatement();
	 	        
	             long code = 0;
	             String sql = "select dblLastNo from tblinternal where strTransactionType='RefundNo'";
	             ResultSet rsRefund= st.executeQuery(sql);
	             if (rsRefund.next())
	             {
	                 code = rsRefund.getLong(1);
	                 code = code + 1;
	                 refundNo = "RF" + String.format("%07d", code);
	                 sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='RefundNo'";
		             st.executeUpdate(sql);
	             }
	             else
	             {
	            	 refundNo = "RF0000001";
	            	 sql = "insert into tblinternal values('refundNo'," + 1 + ")";
	            	 st.executeUpdate(sql);
	             }
	             rsRefund.close();
	             cmsCon.close();
	             st.close();
	           
	         }
	         catch (Exception e)
	         {
	             e.printStackTrace();
	         }
	        
	    
	        return refundNo;
	   }
	
	    
	   	@GET
		@Path("/funGetReasonList")
		@Produces(MediaType.APPLICATION_JSON)
	    public JSONArray funGetReasonList(@QueryParam("Type") String type,@QueryParam("clientCode") String clientCode )
		{
			return funFetchReasonList(type, clientCode);
		}
		
		private JSONArray funFetchReasonList(String type,String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			String sql="select strReasonCode,strReasonName from tblreasonmaster where  " + type + " ='Y' ";
			
			JSONArray arrObj=new JSONArray();
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            
	            ResultSet rsReasonList=st.executeQuery(sql);
	            while(rsReasonList.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("ReasonCode",rsReasonList.getString(1));
	            	obj.put("ReasonName",rsReasonList.getString(2));
	            	
	            	
	            	arrObj.put(obj);
	            }
	            rsReasonList.close();
	            
	            jObj.put("ReasonList", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return arrObj;
		}
	    
	    
	    
	    
	    
	    
	    @GET
		@Path("/funReprintKOT")
		@Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funReprintKOT(@QueryParam("POSCode") String POSCode ,@QueryParam("TableNo") String tableNo
	   		,@QueryParam("KOTNo") String KOTNo,@QueryParam("deviceName")String deviceName)
		{
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection posCon=null;
	        Statement st=null;
	        JSONObject jsRetMsg=new JSONObject();
	      
	        try
	        {
	        	jsRetMsg.put("msg", KOTNo);
	        	posCon=objDb.funOpenAPOSCon("mysql","master");
	        	st=posCon.createStatement();
	        	ResultSet rs=st.executeQuery("select strPOSName from tblposmaster where strPOSCode='"+POSCode+"'");
	        	if(rs.next())
	        	{
	        		funGenerateTextFileForKOT(POSCode, rs.getString(1), tableNo, KOTNo, "", "", "Dina", "Y",deviceName,"","",""); //strAreaWisePricing
	        	}
	        	rs.close();
		        st.close();
		        posCon.close();
	        
	        }catch(Exception e)	        
	        {
	        		jsRetMsg.put("msg", "ERROR in Printing KOT");
	        		e.printStackTrace();
	        }
	        finally
	        {
	        	return jsRetMsg;
	        }
		}
	    
	    
	    
	    @GET
		@Path("/funGetKOTDataForPrint")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funGetKOTDataForPrint(@QueryParam("KOTNo") String KOTNo, @QueryParam("POSCode") String POSCode)
	    {
	    	JSONObject jObjKOTData=new JSONObject();
	    	String itemCode="";
	    	
	    	
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
			
	        
			try {			
				
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
		        String sql="select c.strCostCenterCode, c.strCostCenterName, d.strTableName,ifnull(a.intPaxNo,1) "
		        	+ " from tblitemrtemp a,tblmenuitempricingdtl b,tblcostcentermaster c, tbltablemaster d "
		        	+ " where a.strItemCode=b.strItemCode and b.strCostCenterCode=c.strCostCenterCode "
		        	+ " and a.strTableNo=d.strTableNo "
		        	+ " and a.strKOTNo='"+KOTNo+"' and b.strPOSCode='"+POSCode+"' "
		        	+ " group by c.strCostCenterCode ";
		        ResultSet rsKOTDtl=st.executeQuery(sql);
		        
		        JSONArray jArrKOTHeaderInfo=new JSONArray();
		        while(rsKOTDtl.next())
		        {
		        	JSONObject jObjKOTHeaderInfo=new JSONObject();
		        	jObjKOTHeaderInfo.put("KOTNo",KOTNo);
		        	jObjKOTHeaderInfo.put("CostCenterName",rsKOTDtl.getString(2));
		        	jObjKOTHeaderInfo.put("TableName",rsKOTDtl.getString(3));
		        	jObjKOTHeaderInfo.put("PaxNo",rsKOTDtl.getString(4));
		        	
		        	
		        	JSONArray jArrKOTItemDtl=new JSONArray();

		        	JSONArray jArrKOTModItemDtl=new JSONArray();
		        	Statement st1 = cmsCon.createStatement();
		        	sql="select a.strItemName,a.dblItemQuantity, c.strCostCenterName, LEFT(a.strItemCode,7) "
		        		+ " from tblitemrtemp a,tblmenuitempricingdtl b,tblcostcentermaster c, tblitemmaster d "
		        		+ " where LEFT(a.strItemCode,7)=b.strItemCode and b.strCostCenterCode=c.strCostCenterCode "
		        		+ " and a.strItemCode=d.strItemCode and a.strKOTNo='"+KOTNo+"' and b.strPOSCode='"+POSCode+"' "
		        		+ " and c.strCostCenterCode='"+rsKOTDtl.getString(1)+"' and b.strHourlyPricing='No'";
		        	
		        	System.out.println(sql);
		        	ResultSet rsKOTItemDtl=st1.executeQuery(sql);
		        	while(rsKOTItemDtl.next())
		        	{
		        		JSONObject jObjKOTItemDtl=new JSONObject();
		        		jObjKOTItemDtl.put("ItemName",rsKOTItemDtl.getString(1));
		        		jObjKOTItemDtl.put("ItemQty",rsKOTItemDtl.getString(2));
		        		jArrKOTItemDtl.put(jObjKOTItemDtl);
		        	
		        		itemCode=rsKOTItemDtl.getString(4);
		        		
		        		
		        		Statement st2 = cmsCon.createStatement();
			        	sql=" select a.strItemName,sum(a.dblItemQuantity) "
			        	   + " from tblitemrtemp a  "
			        	   + " where a.strItemCode like '"+itemCode+"M%' and a.strKOTNo='"+KOTNo+"'  "
			        	   + " group by a.strItemCode,a.strItemName ";
			        	
			        	System.out.println(sql);
			        	ResultSet rsKOTModItemDtl=st2.executeQuery(sql);
			        	while(rsKOTModItemDtl.next())
			        	{
			        		JSONObject jObjKOTItemModDtl=new JSONObject();
			        		jObjKOTItemModDtl.put("ItemName",rsKOTModItemDtl.getString(1));
			        		jObjKOTItemModDtl.put("ItemQty",rsKOTModItemDtl.getString(2));
			        		jArrKOTItemDtl.put(jObjKOTItemModDtl);
			        	}
			        	
			        	rsKOTModItemDtl.close();
			        	st2.close();
		        		
		        		
			        	
		        	}
		        	rsKOTItemDtl.close();
		        	st1.close();
		        	
		        	
		        	
		        	
		        	
		        	jObjKOTHeaderInfo.put("ItemDtl",jArrKOTItemDtl);
		        	jObjKOTHeaderInfo.put("ModItemDtl",jArrKOTModItemDtl);
		        	jArrKOTHeaderInfo.put(jObjKOTHeaderInfo);
		        	
		        	jObjKOTData.put("KOTData",jArrKOTHeaderInfo);
		        }
		        rsKOTDtl.close();
		        cmsCon.close();
		        st.close();
	            
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
			
			return jObjKOTData;
	    }
	    
	    
	    
	    
	    
	    
	    
	    
    
    @SuppressWarnings("rawtypes")
	@POST
	@Path("/funSaveAndPrintKOT")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funSaveAndPrintKOT(JSONObject objKOTData)
	{
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        Statement st2=null,st3=null;
		String kotNO="K000001",printingResult="";
		int exe=0;
		JSONObject jObj=new JSONObject();
		try {
			
			String tableNo="",posCode="", posName="",customerCode="",deviceName="",macAddress="";
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        st2 = cmsCon.createStatement();
	        st3 = cmsCon.createStatement();
	        String sql="";
	       
			JSONArray mJsonArray=(JSONArray)objKOTData.get("KOTDtl");
			String lockTable=objKOTData.getString("lockTable");
			boolean isKOTSave=false;
			if(lockTable.equalsIgnoreCase("Y")){
				if(mJsonArray.length()>0){
					JSONObject mJsonObject =(JSONObject) mJsonArray.get(0);
					tableNo=mJsonObject.get("strTableNo").toString();
					String waiterNo=mJsonObject.get("strWaiterNo").toString();
					sql="select distinct b.strStatus,a.strWaiterNo from tblitemrtemp a,tbltablemaster b " 
							+" where a.strTableNo=b.strTableNo and a.strTableNo='"+tableNo+"';";
					ResultSet rs=st.executeQuery(sql);
					String tableStatus="",oldWaiter="";
					if(rs.next()){
						if(rs.getString(1).equalsIgnoreCase("Occupied")){
							if(rs.getString(2).equals(waiterNo)){
								isKOTSave=true;
							}
						}
					}else{
						isKOTSave=true;
					}
					
					
				}
				if(!isKOTSave){
					jObj.put("kotNO", " Not Punched.. Table Already Busy");
					jObj.put("printingResult", printingResult);
					return jObj;
				}
			}
			
			
		    String insertQuery1="";
		    int paxNo=0;
			boolean flgData=false;
			JSONObject mJsonObject = new JSONObject();
			kotNO=funGenerateKOTNo();
			String insertQuery = "insert into tblitemrtemp(strSerialNo,strTableNo,strCardNo,dblRedeemAmt,strPosCode,strItemCode"
				+ ",strHomeDelivery,strCustomerCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo"
                + ",strKOTNo,intPaxNo,strPrintYN,strUserCreated,strUserEdited,dteDateCreated"
                + ",dteDateEdited,strTakeAwayYesNo,strNCKotYN,strCustomerName,strCounterCode"
                + ",dblRate,strCardType,strDeviceMACAdd,strDeviceId) values ";
			
			
			 insertQuery1 = "insert into tblnonchargablekot "
      	        	+ " (strTableNo,strItemCode,dblQuantity,dblRate,strKOTNo,strEligibleForVoid,strClientCode,"
      	        	+ " strDataPostFlag,strReasonCode,strRemark,dteNCKOTDate,strUserCreated,strUserEdited,strPOSCode) "
      	        	+ " values " ;
			 
			 boolean flgNCKOT=false;
			
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
			    mJsonObject =(JSONObject) mJsonArray.get(i);
			    String itemName=mJsonObject.get("strItemName").toString();
			    System.out.println(itemName);
			    double qty=Double.parseDouble(mJsonObject.get("dblItemQuantity").toString());
			    double rate=Double.parseDouble(mJsonObject.get("dblRate").toString());
			    //double amt=qty*rate;
			    double amt=Double.parseDouble(mJsonObject.get("dblAmount").toString());
			    tableNo=mJsonObject.get("strTableNo").toString();
			    posCode=mJsonObject.get("strPOSCode").toString();
			    posName=mJsonObject.get("strPOSName").toString();
			    deviceName=mJsonObject.get("deviceName").toString();
			    macAddress=mJsonObject.get("macAddress").toString();
			    //customerCode=mJsonObject.get("strCustomerCode").toString();
			    
			    if(i==0)
			    {
			    	Statement st1 = cmsCon.createStatement();
			    	sql="select strCustomerCode from tblitemrtemp "
		    			+ "where strTableNo='"+tableNo+"' and strPOSCode='"+posCode+"'";
			    	ResultSet rsCust=st1.executeQuery(sql);
			    	if(rsCust.next())
			    	{
			    		customerCode=rsCust.getString(1);
			    	}
			    	else
			    	{
			    		customerCode=mJsonObject.get("strCustomerCode").toString();
			    	}
			    	rsCust.close();
			    	st1.close();
			    }
			    Date objDate = new Date();
			    String currentDate = mJsonObject.get("POSDate").toString()
     		                      + " "+ objDate.getHours() + ":" + objDate.getMinutes() + ":" +objDate.getSeconds();
			    
			    insertQuery +="('"+mJsonObject.get("strSerialNo").toString()+"','"+mJsonObject.get("strTableNo").toString()+"'"
			    	+ ",'" + mJsonObject.get("strCardNo").toString() + "','" + mJsonObject.get("dblRedeemAmt").toString() + "'"
	    			+ ",'" + mJsonObject.get("strPOSCode").toString() + "','" + mJsonObject.get("strItemCode").toString() + "'"
	                + ",'" + mJsonObject.get("strHomeDelivery").toString() + "','"+customerCode+"'"
                   	+ ",'" + mJsonObject.get("strItemName").toString() + "','" + mJsonObject.get("dblItemQuantity").toString() + "'"
                   	+ ",'" + amt + "','" + mJsonObject.get("strWaiterNo").toString() + "'"
                 	+ ",'" + kotNO + "','" + mJsonObject.get("intPaxNo").toString() + "'"
            		+ ",'" + mJsonObject.get("strPrintYN").toString() + "','" + mJsonObject.get("strUserCreated").toString() + "'"
            		+ ",'" + mJsonObject.get("strUserEdited").toString() + "','" + mJsonObject.get("dteDateCreated").toString()+ "'"
         			+ ",'" + mJsonObject.get("dteDateEdited").toString() + "','"+mJsonObject.get("strTakeAwayYesNo").toString()+"'"
      				+ ",'"+ mJsonObject.get("strNCKotYN").toString() + "','"+mJsonObject.get("strCustomerName").toString()+"'"
      				+ ",'"+mJsonObject.get("strCounterCode").toString()+"','"+mJsonObject.get("dblRate").toString()+"'"
      				+ ",'"+mJsonObject.get("strCardType").toString()+"','"+mJsonObject.get("macAddress").toString()+"','"+mJsonObject.get("deviceName").toString()+"'), ";
      			
			    
			    paxNo=Integer.parseInt(mJsonObject.get("intPaxNo").toString());
			    String NCKOTYN= mJsonObject.get("strNCKotYN").toString();
			    System.out.println("Nckotyn="+NCKOTYN);
			    if(NCKOTYN.equalsIgnoreCase("Y"))
	            {
	                 insertQuery1+= "('" +mJsonObject.get("strTableNo").toString()+ "','" +  mJsonObject.get("strItemCode").toString()+ "','"+mJsonObject.get("dblItemQuantity").toString()+ "'"
	         	        	+ ", '"+mJsonObject.get("dblRate").toString()+"','"+kotNO+"','Y','"+mJsonObject.get("strClientCode").toString()+"','N','"+mJsonObject.get("strReasonCode").toString()+"'"
	         	        	+ ", '"+mJsonObject.get("strRemark").toString()+"','"+currentDate+"','"+mJsonObject.get("strUserCreated").toString()+"','"+mJsonObject.get("strUserEdited").toString()+"'"
	         	        	+ ", '"+posCode +"'),";
	                 //System.out.println("query="+insertQuery1);
	                 flgNCKOT=true;
	            }
			    
			}
			System.out.println("query="+insertQuery1);
            StringBuilder sb = new StringBuilder(insertQuery);            
            int index = sb.lastIndexOf(",");
            insertQuery = sb.delete(index, sb.length()).toString();
            System.out.println(insertQuery);
            exe=st.executeUpdate(insertQuery);
            System.out.println("Exe= "+exe);
            sql="select strFireCommunication,strAreaWisePricing from tblsetup where strClientCode='"+mJsonObject.get("strClientCode").toString()+"' ";
        	String strFireComm="N",strAreaWisePricing="N";
        	ResultSet rsFire=st.executeQuery(sql);
	    	if(rsFire.next())
	    	{
	    		strFireComm=rsFire.getString(1);
	    		strAreaWisePricing=rsFire.getString(2);
	    	}
	    	rsFire.close();
            if(flgNCKOT)
            {
	            StringBuilder sb1 = new StringBuilder(insertQuery1);
	            int index1=sb1.lastIndexOf(",");            
	            insertQuery1 = sb1.delete(index1, sb1.length()).toString();
				System.out.println("query="+insertQuery1);            
	            st2.executeUpdate(insertQuery1);
	            sql = "update tbltablemaster set strStatus='Normal',intPaxNo='0' where strTableNo='"+tableNo+"'";
	            st3.executeUpdate(sql);
	            printingResult=funGenerateTextFileForKOT(posCode, posName, tableNo, kotNO, "", "", "Dina", "Y",deviceName,macAddress,strFireComm,strAreaWisePricing);
            }
            else{
            	if(exe>0)
                {
                	sql="update tbltablemaster set strStatus='Occupied',intPaxNo="+paxNo+" where strTableNo='"+tableNo+"'";
                	System.out.println(sql);
                	st.executeUpdate(sql);
                	
                	
			    	if(strFireComm.equals("N")){
			    		printingResult=funGenerateTextFileForKOT(posCode, posName, tableNo, kotNO, "", "", "Dina", "Y",deviceName,macAddress,strFireComm,strAreaWisePricing);	
			    	}
                }
            }
           st.close();
           cmsCon.close();
           st2.close();
           st3.close();
            
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(exe==0){
			kotNO="ERROR";			
		}

		try{
			jObj.put("kotNO", kotNO);
			jObj.put("printingResult", printingResult);
		}catch(Exception e){
			
		}
		
		
		
		return jObj;//Response.status(201).entity(kotNO+"#"+printingResult).build();		
	}
    
    
    private String funGenerateKOTNo()
    {
        String kotNo = "";
        clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
		
		try {
			
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        
            long code = 0;
            String sql = "select dblLastNo from tblinternal where strTransactionType='KOTNo'";
            ResultSet rsKOT = st.executeQuery(sql);
            if (rsKOT.next())
            {
                code = rsKOT.getLong(1);
                code = code + 1;
                kotNo = "KT" + String.format("%07d", code);
            }
            else
            {
                kotNo = "KT0000001";
            }
            rsKOT.close();
            sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='KOTNo'";
            st.executeUpdate(sql);
            cmsCon.close();
            st.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return kotNo;
    }
    
    
    
   
    private int funDebitCardTransaction(String billNo,String debitCardNo,double debitCardSettleAmt,String posCode,String posDate)
	{
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
    	Connection cmsCon=null;
    	Statement st=null;
    	int retRows=0;
    	try 
    	{
    		cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        String sqlDebitCardDetials = "insert into tbldebitcardbilldetails "
	        	+ " (strBillNo,strCardNo,dblTransactionAmt,strPOSCode,dteBillDate,strTransactionType) "
	        	+ " values ('" + billNo + "','" + debitCardNo+ "','"+debitCardSettleAmt+ "' "
	        	+ " ,'"+posCode + "','"+ posDate + "','Settle') ";
	        retRows=st.executeUpdate(sqlDebitCardDetials);
	        System.out.println(sqlDebitCardDetials);
    	} catch (Exception e) 
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		return retRows;
    	}
	}

    private int funUpdateDebitCardBalance(String debitCardNo,double debitCardSettleAmt) {
       
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
    	Connection cmsCon=null;
    	Statement st=null;
    	int retRows=0;
    	try 
    	{
       		cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        String sql = "select dblRedeemAmt from tbldebitcardmaster "
	        	+ "where strCardString='" +debitCardNo+ "'";
	        ResultSet rsS2 = st.executeQuery(sql);
	        System.out.println(sql);
	        if(rsS2.next())
	        {
	        	double amt = Double.parseDouble(rsS2.getString(1));
	        	rsS2.close();
	        	double updatedBal = amt - debitCardSettleAmt;
	        	sql = "update tbldebitcardmaster set dblRedeemAmt='" + updatedBal + "' "
	        		+ "where strCardString='" +debitCardNo+ "'";
	        	retRows=st.executeUpdate(sql);
	        	 System.out.println(sql);
	        }
	        else
	        {
	        	funUpdateDebitCardNoBalance(debitCardNo,debitCardSettleAmt);
	        }
	        cmsCon.close();
	        st.close();
	        
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return retRows;
   }
	
    
    private int funDebitCardNoTransaction(String billNo,String debitCardNo,double debitCardSettleAmt,String posCode,String posDate)
  	{
      	clsDatabaseConnection objDb=new clsDatabaseConnection();
      	Connection cmsCon=null;
      	Statement st=null;
      	int retRows=0;
      	try 
      	{
      		cmsCon=objDb.funOpenAPOSCon("mysql","master");
  	        st = cmsCon.createStatement();
  	        String sqlDebitCardDetials = "insert into tbldebitcardbilldetails "
  	        	+ " (strBillNo,strCardNo,dblTransactionAmt,strPOSCode,dteBillDate,strTransactionType) "
  	        	+ " values ('" + billNo + "','" + debitCardNo+ "','"+debitCardSettleAmt+ "' "
  	        	+ " ,'"+posCode + "','"+ posDate + "','Settle') ";
  	        retRows=st.executeUpdate(sqlDebitCardDetials);
  	        System.out.println(sqlDebitCardDetials);
  	        cmsCon.close();
  	        st.close();
      	} catch (Exception e) 
      	{
      		e.printStackTrace();
      	}
      	finally
      	{
      		return retRows;
      	}
  	}

      private int funUpdateDebitCardNoBalance(String debitCardNo,double debitCardSettleAmt) {
         
      	clsDatabaseConnection objDb=new clsDatabaseConnection();
      	Connection cmsCon=null;
      	Statement st=null;
      	int retRows=0;
      	try 
      	{
         		cmsCon=objDb.funOpenAPOSCon("mysql","master");
  	        st = cmsCon.createStatement();
  	        String sql = "select dblRedeemAmt from tbldebitcardmaster "
  	        	+ "where strCardNo='" +debitCardNo+ "'";
  	        ResultSet rsS2 = st.executeQuery(sql);
  	        System.out.println(sql);
  	        if(rsS2.next())
  	        {
  	        	double amt = Double.parseDouble(rsS2.getString(1));
  	        	rsS2.close();
  	        	double updatedBal = amt - debitCardSettleAmt;
  	        	sql = "update tbldebitcardmaster set dblRedeemAmt='" + updatedBal + "' "
  	        		+ "where strCardNo='" +debitCardNo+ "'";
  	        	retRows=st.executeUpdate(sql);
  	        	 System.out.println(sql);
  	        }
  	        rsS2.close();
  	        cmsCon.close();
  	        st.close();
      	} catch (Exception e) {
      		e.printStackTrace();
      	}
      	return retRows;
     }
  	
    
    
	
   	@GET
	@Path("/funPrintBill")
	@Produces(MediaType.APPLICATION_JSON)
   	public JSONObject funPrintBill(@QueryParam("billNo") String billNo,@QueryParam("cardNo") String cardString,@QueryParam("TransactionType") String transactionType)
	{
   		clsDatabaseConnection objDb=new clsDatabaseConnection();
   		Connection cmsCon=null;
   		Statement st=null;
       
   		JSONObject jObj=new JSONObject();
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        JSONArray arrObjBillHd=new JSONArray();
	        String sql="";
	        
	        if(transactionType.equals("SettleBill"))
	        {
	        	sql="select a.strBillNo,a.dteBillDate,a.strCustomerCode,ifnull(c.strCustomerName,'')"
					+ ",sum(b.dblSettlementAmt),a.dblsubtotal,a.dblDiscountAmt,a.dblDiscountPer"
					+ ",ifnull(d.strTableName,''),ifnull(e.strWShortName,''),ifnull(f.strUserName,'')"
					+ ",a.intPaxNo,a.strCardNo,a.strOperationType,a.strCustomerCode "
					+ " from tblbillhd a left outer join tblbillsettlementdtl b on a.strBillNo=b.strBillNo "
					+ " left outer join tblcustomermaster c on a.strCustomerCode=c.strCustomerCode "
					+ " left outer join tbltablemaster d on a.strTableNo=d.strTableNo "
					+ " left outer join tblwaitermaster e on a.strWaiterNo=e.strWaiterNo "
					+ " left outer join tbluserhd f on a.strUserCreated=f.strUserCode "
					+ " where a.strBillNo='"+billNo+"' "
					+ " group by b.strBillNo,date(a.dteBillDate)";
	        }
	        else
	        {
	        	sql="select a.strBillNo,a.dteBillDate,a.strCustomerCode,ifnull(c.strCustomerName,'')"
					+ ",sum(a.dblGrandTotal),a.dblsubtotal,a.dblDiscountAmt,a.dblDiscountPer "
					+ ",ifnull(d.strTableName,''),ifnull(e.strWShortName,''),ifnull(f.strUserName,'') "
					+ ",a.intPaxNo,a.strCardNo,a.strOperationType,a.strCustomerCode "
					+ " from tblbillhd a left outer join tblcustomermaster c on a.strCustomerCode=c.strCustomerCode "
					+ " left outer join tbltablemaster d on a.strTableNo=d.strTableNo "
					+ " left outer join tblwaitermaster e on a.strWaiterNo=e.strWaiterNo "
					+ " left outer join tbluserhd f on a.strUserCreated=f.strUserCode "
					+ " where a.strBillNo='"+billNo+"' "
					+ " group by a.strBillNo,date(a.dteBillDate)";
	        }
			System.out.println("Db Print Bill= "+sql);
			String operationType="";
			String customerCode="";
			String cardNo="";
			Double grandTotal=0.0;
			ResultSet rsBillHd=st.executeQuery(sql);
			if(rsBillHd.next())
			{
				JSONObject obj=new JSONObject();
				
				operationType=rsBillHd.getString(14);
				customerCode=rsBillHd.getString(15);
				cardNo=rsBillHd.getString(13);
				obj.put("BillNo",rsBillHd.getString(1));
				obj.put("BillDate",rsBillHd.getString(2));
				obj.put("MemCode",rsBillHd.getString(3));
				obj.put("MemName",rsBillHd.getString(4));
				obj.put("GrandTotal",rsBillHd.getString(5));
				obj.put("SubTotal",rsBillHd.getString(6));
				obj.put("DiscountAmt",rsBillHd.getString(7));
				obj.put("DiscountPer",rsBillHd.getString(8));
				obj.put("Table",rsBillHd.getString(9));
				obj.put("Waiter",rsBillHd.getString(10));
				obj.put("User",rsBillHd.getString(11));
				obj.put("PAX",rsBillHd.getString(12));
				obj.put("OperationType",rsBillHd.getString(14));
				obj.put("CustomerCode",rsBillHd.getString(15));
				grandTotal=Double.parseDouble(rsBillHd.getString(5));
				arrObjBillHd.put(obj);
				
				 JSONArray arrObjHomeDeliveryDtl=new JSONArray();
				//Home Deliver Details
				 if(operationType.equalsIgnoreCase("HomeDelivery"))
				 {
					 sql=" select a.strCustomerName,a.longMobileNo,a.strBuildingName from tblcustomermaster a "
						+ " where a.strCustomerCode='"+customerCode+"'";
					 System.out.println(sql);
					 
						ResultSet rsHomeDeliveryDtl=st.executeQuery(sql);
						while(rsHomeDeliveryDtl.next())
						{
							JSONObject objHomeDeliveryDtl=new JSONObject();
							objHomeDeliveryDtl.put("CustomerName",rsHomeDeliveryDtl.getString(1));
							objHomeDeliveryDtl.put("MobileNo",rsHomeDeliveryDtl.getString(2));
							objHomeDeliveryDtl.put("Address",rsHomeDeliveryDtl.getString(3));
							System.out.println(rsHomeDeliveryDtl.getString(1)+rsHomeDeliveryDtl.getString(2)+rsHomeDeliveryDtl.getString(3));
							
							arrObjHomeDeliveryDtl.put(objHomeDeliveryDtl);
						}
						rsHomeDeliveryDtl.close();
					 
				 }
				 jObj.put("HomeDeliveryDtl", arrObjHomeDeliveryDtl);
				
			// Item details 			
				sql="select a.strBillNo, b.strItemCode,b.strItemName,b.dblAmount,b.dblQuantity,b.dblRate,b.strKOTNo "
					+ " from tblbillhd a,tblbilldtl b "
					+ " where a.strBillNo=b.strBillNo and a.strBillNo='"+billNo+"'";
				
				JSONArray arrObjBillDtl=new JSONArray();
				ResultSet rsBillDtl=st.executeQuery(sql);
				while(rsBillDtl.next())
				{
					JSONObject objBillDtl=new JSONObject();
					objBillDtl.put("BillNo",rsBillDtl.getString(1));
					objBillDtl.put("ItemCode",rsBillDtl.getString(2));
					objBillDtl.put("ItemName",rsBillDtl.getString(3));
					objBillDtl.put("Amount",rsBillDtl.getString(4));
					objBillDtl.put("Quantity",rsBillDtl.getString(5));
					objBillDtl.put("Rate",rsBillDtl.getString(6));
					objBillDtl.put("Kot",rsBillDtl.getString(7));
					arrObjBillDtl.put(objBillDtl);
				}
				rsBillDtl.close();
				jObj.put("BillDtlInfo", arrObjBillDtl);
				
				
				
				// Item details 			
				sql="select a.strBillNo, b.strItemCode,b.strModifierName,b.dblAmount,b.dblQuantity,b.dblRate "
					+ " from tblbillhd a,tblbillmodifierdtl b "
					+ " where a.strBillNo=b.strBillNo and a.strBillNo='"+billNo+"'";
				
				JSONArray arrObjMoBillDtl=new JSONArray();
				ResultSet rsModBillDtl=st.executeQuery(sql);
				while(rsModBillDtl.next())
				{
					JSONObject objModBillDtl=new JSONObject();
					objModBillDtl.put("BillNo",rsModBillDtl.getString(1));
					objModBillDtl.put("ItemCode",rsModBillDtl.getString(2));
					objModBillDtl.put("ModItemName",rsModBillDtl.getString(3));
					objModBillDtl.put("Amount",rsModBillDtl.getString(4));
					objModBillDtl.put("Quantity",rsModBillDtl.getString(5));
					objModBillDtl.put("Rate",rsModBillDtl.getString(6));
					arrObjMoBillDtl.put(objModBillDtl);
				}
				rsModBillDtl.close();
				jObj.put("ModBillDtlInfo", arrObjMoBillDtl);
				
				
			//Tax details 	
				sql="select a.strBillNo, c.strTaxDesc, b.dblTaxableAmount, b.dblTaxAmount "
					+ " from tblbillhd a,tblbilltaxdtl b,tbltaxhd c "
					+ " where a.strBillNo=b.strBillNo and b.strTaxCode=c.strTaxCode "
					+ " and a.strBillNo='"+billNo+"'";
					
				JSONArray arrObjBillTaxDtl=new JSONArray();
				ResultSet rsBillTaxDtl=st.executeQuery(sql);
				while(rsBillTaxDtl.next())
				{
					JSONObject objBillTaxDtl=new JSONObject();
					objBillTaxDtl.put("BillNo",rsBillTaxDtl.getString(1));
					objBillTaxDtl.put("TaxDesc",rsBillTaxDtl.getString(2));
					objBillTaxDtl.put("TaxableAmt",rsBillTaxDtl.getString(3));
					objBillTaxDtl.put("TaxAmt",rsBillTaxDtl.getString(4));
					arrObjBillTaxDtl.put(objBillTaxDtl);
				}
				rsBillTaxDtl.close();
				jObj.put("BillTaxDtlInfo", arrObjBillTaxDtl);
				
				
			// Settlement details 	
				sql="select a.strBillNo, b.dblSettlementAmt,c.strSettelmentDesc "
					+ " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
					+ " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
					+ " and a.strBillNo='"+billNo+"'";
					
				JSONArray arrObjBillSettleDtl=new JSONArray();
				ResultSet rsBillSettleDtl=st.executeQuery(sql);
				while(rsBillSettleDtl.next())
				{
					JSONObject objBillSettleDtl=new JSONObject();
					objBillSettleDtl.put("BillNo",rsBillSettleDtl.getString(1));
					objBillSettleDtl.put("SettlementAmt",rsBillSettleDtl.getString(2));
					objBillSettleDtl.put("SettlementDesc",rsBillSettleDtl.getString(3));
					arrObjBillSettleDtl.put(objBillSettleDtl);
				}
				rsBillSettleDtl.close();
				jObj.put("BillSettleDtlInfo", arrObjBillSettleDtl);
				
				
				
			// Card Details
				
				if(!cardNo.isEmpty())
				{
					JSONObject jObjCardDtl=new JSONObject();				
					

					if(!cardString.isEmpty())
					{
						sql="select a.dblRedeemAmt,a.strReachrgeRemark,a.strCardString,a.strCardNo"
								+ ",a.strRefMemberCode "
								+ "from tbldebitcardmaster a where a.strCardString='"+cardString+"' ";
					}
					else
					{
						sql="select a.dblRedeemAmt,a.strReachrgeRemark,a.strCardString,a.strCardNo"
								+ ",a.strRefMemberCode "
								+ "from tbldebitcardmaster a where a.strCardNo='"+cardNo+"' ";
					}
				    
					System.out.println(sql);
					ResultSet rsCashCardDtl=st.executeQuery(sql);
					if(rsCashCardDtl.next())  // Cash Card
					{
						jObjCardDtl.put("CardType", "CashCard");
			                
			            double balance=Double.parseDouble(rsCashCardDtl.getString(1));
						jObjCardDtl.put("Balance",balance);
						jObjCardDtl.put("Opening Balance",balance+grandTotal);
						jObjCardDtl.put("Closing Balance",(balance));
						jObjCardDtl.put("CardNo",rsCashCardDtl.getString(4));
						jObjCardDtl.put("Ref.MemberCode",rsCashCardDtl.getString(5));
						String cmsMemberDtl=new clsCMSIntegration().funGetMemberDtlsFromCMS(rsCashCardDtl.getString(5));
					
			        	JSONObject jsonObjCMSMemberDtl=new JSONObject(cmsMemberDtl);
			        	JSONArray mJsonArray = (JSONArray) jsonObjCMSMemberDtl.get("CMSMemberDtl");
			            JSONObject mJsonObject = new JSONObject();
			            
			            for (int i = 0; i < mJsonArray.length(); i++) 
			            {
			            	mJsonObject = (JSONObject) mJsonArray.get(i);

			               // System.out.println(mJsonObject.get("CustomerCode").toString());
			                if(mJsonObject.get("MemberCode").toString().equals("No Data"))  // Only Cash Card
			                {
			                	jObjCardDtl.put("MemberCode","");
			                	jObjCardDtl.put("MemberName","");
			                }
			                else    // Both Member and Cash Card
			                {
			                	jObjCardDtl.put("MemberCode",mJsonObject.get("MemberCode").toString());
			                	jObjCardDtl.put("MemberName",mJsonObject.get("MemberName").toString());
			                	
			                }
			            }
						
						/*String cmsMemberCardDtl=new clsCMSIntegration().funGetMemberCardDtlsFromCMS(rsCashCardDtl.getString(3));
			        	JSONObject jsonObjCMSMemberCardDtl=new JSONObject(cmsMemberCardDtl);
			        	JSONArray mJsonArray = (JSONArray) jsonObjCMSMemberCardDtl.get("CMSMemberCardDtl");
			            JSONObject mJsonObject = new JSONObject();
			            
			            for (int i = 0; i < mJsonArray.length(); i++) 
			            {
			            	mJsonObject = (JSONObject) mJsonArray.get(i);

			                System.out.println(mJsonObject.get("CustomerCode").toString());
			                if(mJsonObject.get("CustomerCode").toString().equals("No Data"))  // Only Cash Card
			                {
			                	jObjCardDtl.put("MemberCode","");
			                	jObjCardDtl.put("MemberName","");
			                }
			                else    // Both Member and Cash Card
			                {
			                	jObjCardDtl.put("MemberCode",mJsonObject.get("MemberCode").toString());
			                	jObjCardDtl.put("MemberName",mJsonObject.get("MemberName").toString());
			                	jObjCardDtl.put("RefCardNo",mJsonObject.get("CardNo").toString());
			                }
			            }
			            */
					}
					else   // Plain Member Card
					{
						List listMemInfo=new clsCMSIntegration().funGetMemberInfoFromCMS(cardNo);
						if(listMemInfo.size()>0)
						{
							jObjCardDtl.put("CardType", "MemberCard");
							jObjCardDtl.put("MemberCode",listMemInfo.get(0));
		                	jObjCardDtl.put("MemberName",listMemInfo.get(1));
		                	jObjCardDtl.put("MemberCardNo",listMemInfo.get(2));
						}
						else
						{
							jObjCardDtl.put("CardType", "MemberCard");
							jObjCardDtl.put("MemberCode","");
		                	jObjCardDtl.put("MemberName","");
		                	jObjCardDtl.put("MemberCardNo","");
						}
					}				
					
					jObj.put("CardDetails", jObjCardDtl);
				}
			}
			
			jObj.put("BillHdInfo", arrObjBillHd);
			cmsCon.close();
			st.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return jObj;
		
	}
   
   
   
   
   /**
    * printBillWindows() method print to Default Printer.
    */
   private void funPrintBillWindows(String type, String billPrinterName) {
       try {
           //System.out.println("Print Bill");
           String filePath = System.getProperty("user.dir");
           String filename = "";
           
           if (type.equalsIgnoreCase("bill")) 
           {
               filename = (filePath + "/Temp/Temp_Bill.txt");
           }           
           else if (type.equalsIgnoreCase("dayend")) 
           {
               filename = (filePath + "/Temp/Temp_DayEndReport.txt");
           }
           /*else if (type.equalsIgnoreCase("Adv Receipt"))
           {
               filename = (filePath + "/Temp/Temp_Bill.txt");
               billPrinterName=clsGlobalVarClass.gAdvReceiptPrinterPort;
           }*/

           billPrinterName=billPrinterName.replaceAll("#", "\\\\");
           int printerIndex=0;
           PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
           DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
           PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
           for (int i = 0; i < printService.length; i++) 
           {
               //System.out.println("Sys="+printService[i].getName()+"\tBill Printer="+billPrinterName);
               if (billPrinterName.equalsIgnoreCase(printService[i].getName())) 
               {
                   //System.out.println("Bill Printer Sel="+billPrinterName);
                   printerIndex = i;
                   break;
               }
           }
           PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
           //DocPrintJob job = defaultService.createPrintJob();
           DocPrintJob job = printService[printerIndex].createPrintJob();
           FileInputStream fis = new FileInputStream(filename);
           DocAttributeSet das = new HashDocAttributeSet();
           Doc doc = new SimpleDoc(fis, flavor, das);
           job.print(doc, printerReqAtt);

       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   
    
    
    
    @GET
   	@Path("/funGenerateBillNo")
   	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetBillNo(@QueryParam("POSCode") String posCode)
    {
    	return funGenerateBillNo(posCode);
    }
    
    private JSONObject funGenerateBillNo(String posCode)
    {
    	JSONObject objJSON=new JSONObject();
    	String voucherNo="";
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        
        try
        {        	
        	posCon=objDb.funOpenAPOSCon("mysql","master");
	        st = posCon.createStatement();
            
            long code = 0;
            String sql_GetlastBillNo = "select strBillNo from tblstorelastbill "
            		+ "where strPosCode='" + posCode + "'";
            System.out.println(sql_GetlastBillNo);
            ResultSet rsLastBillNo = st.executeQuery(sql_GetlastBillNo);

            if (rsLastBillNo.next())
            {
                code = rsLastBillNo.getLong(1);
                code = code + 1;
                rsLastBillNo.close();

                voucherNo = posCode + String.format("%05d", code);
                System.out.println(voucherNo);
                String sql_Update_lastBill = "update tblstorelastbill set strBillNo='" + code + "' where strPosCode='" + posCode + "'";
                st.executeUpdate(sql_Update_lastBill);
            }
            else
            {
                rsLastBillNo.close();
                voucherNo = posCode + "00001";
                String sql_insert = "insert into tblstorelastbill values('" + posCode + "','1')";
                st.executeUpdate(sql_insert);
            }
            objJSON.put("BillNo", voucherNo);
            
            st.close();
            posCon.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	return objJSON;//.toString();
        }
    }
    
    
		
	

		
	private void funBuildSMS(String billno)
	    {
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			Connection cmsCon=null;
			Statement st = null;
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
	            String result = "", result1 = "", result2 = "", result3 = "", result4 = "", result5 = "", result6 = "", result7 = "",sendSMSYN="";
	            String mainSms = "", sql = "",operationType="",homeDeliverySMS="",billSettelementSMS="",sendHomeDeliverySMSYN="",sendBillSettlementSMSYN="" ;
	            String SMSType="",smsData="",telephoneNo="",SMSApi="";
	            
	            sql="select a.strHomeDeliverySMS,a.strBillStettlementSMS,a.strSendHomeDelSMS,a.strSendBillSettlementSMS,a.strSMSType,a.intTelephoneNo,a.strSMSApi from tblsetup a;";
	            ResultSet rs =st.executeQuery(sql);
	            while(rs.next())
	            {
	            	homeDeliverySMS=rs.getString(1);
	            	billSettelementSMS=rs.getString(2);
	            	sendHomeDeliverySMSYN=rs.getString(3);
	            	sendBillSettlementSMSYN=rs.getString(4);
	            	SMSType=rs.getString(5);
	            	telephoneNo=rs.getString(6);
	            	SMSApi=rs.getString(7);
	            }
	            rs.close();
	            
	            System.out.println(sql);
	            sql=" select strOperationType from tblbillhd where strBillNo='"+billno+"' ";
	            rs =st.executeQuery(sql);
	            if(rs.next())
	            {
	            	operationType=rs.getString(1);
	            	if(operationType.equalsIgnoreCase("HomeDelivery"))
	            	{
	            		if(sendHomeDeliverySMSYN.equalsIgnoreCase("Y"))
	            		{
		            		 smsData=homeDeliverySMS;
		            		 sendSMSYN="Y";
	            		}
	            		
	            	}
	            	else
	            	{
	            		if(sendBillSettlementSMSYN.equalsIgnoreCase("Y"))
	            		{
		            		smsData=billSettelementSMS;
		            		sendSMSYN="Y";
	            		}
	            		
	            	}
	            	
	            }
	            rs.close();
	            
	           if(sendSMSYN.equalsIgnoreCase("Y"))
	           {
		            if (operationType.equalsIgnoreCase("HomeDelivery"))
		            {
		                sql = "select c.strCustomerName,c.longMobileNo,a.dblGrandTotal "
		                    + " ,DATE_FORMAT(a.dteBillDate,'%m-%d-%Y'),time(a.dteBillDate) "
		                    + " ,a.strUserCreated,ifnull(d.strDPName,'') "
		                    + " from tblbillhd a,tblcustomermaster c ,tblhomedelivery b "
		                    + " left outer join tbldeliverypersonmaster d on b.strDPCode=d.strDPCode "
		                    + " where a.strBillNo='" + billno + "' and a.strBillNo=b.strBillNo "
		                    + " and a.strCustomerCode=c.strCustomerCode ";
		            }
		            else
		            {
		                sql = "select ifnull(c.strCustomerName,''),ifnull(c.longMobileNo,'NA')"
		                    + " ,a.dblGrandTotal ,DATE_FORMAT(a.dteBillDate,'%m-%d-%Y')"
		                    + " ,time(a.dteBillDate),a.strUserCreated,ifnull(d.strDPName,'') "
		                    + " from tblbillhd a left outer join tblhomedelivery b on a.strBillNo=b.strBillNo "
		                    + " left outer join tbldeliverypersonmaster d on b.strDPCode=d.strDPCode "
		                    + " left outer join tblcustomermaster c on a.strCustomerCode=c.strCustomerCode "
		                    + " where a.strBillNo='" + billno + "'";
		            }
	            } 
	            System.out.println(sql);
	            System.out.println("sms"+smsData);
	            ResultSet rs_SqlGetSMSData = st.executeQuery(sql);
	            while (rs_SqlGetSMSData.next())
	            {
	                int intIndex = smsData.indexOf("%%BILL NO");
	                if (intIndex != - 1)
	                {
	                    result = smsData.replaceAll("%%BILL NO", billno);
	                    mainSms = result;
	                }
	                int intIndex1 = mainSms.indexOf("%%CUSTOMER NAME");

	                if (intIndex1 != - 1)
	                {
	                    result1 = mainSms.replaceAll("%%CUSTOMER NAME", rs_SqlGetSMSData.getString(1));
	                    mainSms = result1;
	                }
	                int intIndex2 = mainSms.indexOf("%%BILL AMT");

	                if (intIndex2 != - 1)
	                {
	                    result2 = mainSms.replaceAll("%%BILL AMT", rs_SqlGetSMSData.getString(3));
	                    mainSms = result2;
	                }
	                int intIndex3 = mainSms.indexOf("%%DATE");

	                if (intIndex3 != - 1)
	                {
	                    result3 = mainSms.replaceAll("%%DATE", rs_SqlGetSMSData.getString(4));
	                    mainSms = result3;
	                }
	                int intIndex4 = mainSms.indexOf("%%DELIVERY BOY");

	                if (intIndex4 != - 1)
	                {
	                    result4 = mainSms.replaceAll("%%DELIVERY BOY", rs_SqlGetSMSData.getString(7));
	                    mainSms = result4;
	                }
	                int intIndex5 = mainSms.indexOf("%%ITEMS");

	                if (intIndex5 != - 1)
	                {
	                    result5 = mainSms.replaceAll("%%ITEMS", "");
	                    mainSms = result5;
	                }
	                int intIndex6 = mainSms.indexOf("%%USER");

	                if (intIndex6 != - 1)
	                {
	                    result6 = mainSms.replaceAll("%%USER", rs_SqlGetSMSData.getString(6));
	                    mainSms = result6;
	                }
	                int intIndex7 = mainSms.indexOf("%%TIME");

	                if (intIndex7 != - 1)
	                {
	                    result7 = mainSms.replaceAll("%%TIME", rs_SqlGetSMSData.getString(5));
	                    mainSms = result7;
	                }

	                String fromTelNo = telephoneNo;
	                String[] sp = fromTelNo.split(",");
	                if (sp.length > 0)
	                {
	                    fromTelNo = sp[0];
	                }

	                if (SMSType.equalsIgnoreCase("Cellx"))
	                {
	                    if (!rs_SqlGetSMSData.getString(2).isEmpty())
	                    {
	                        String smsURL = SMSApi.replace("<to>", rs_SqlGetSMSData.getString(2)).replace("<from>", fromTelNo).replace("<MSG>", mainSms).replaceAll(" ", "%20");
	                     
	                    }
	                }
	                else if (SMSType.equalsIgnoreCase("Sinfini"))
	                {
	                    String smsURL = SMSApi.replace("<PHONE>", rs_SqlGetSMSData.getString(2)).replace("<MSG>", mainSms).replaceAll(" ", "%20");
	                   funSendSMS(smsURL);
	                }
	                else if (SMSType.equalsIgnoreCase("Infyflyer"))
	                {
	                    //http://sms.infiflyer.co.in/httpapi/httpapi?token=a10bad827db08a4eeec726da63813747&sender=IPREMS&number=<PHONE>&route=2&type=1&sms=<MSG>
	                    String smsURL = SMSApi.replace("<PHONE>", rs_SqlGetSMSData.getString(2)).replace("<MSG>", mainSms).replaceAll(" ", "%20");
	                    funSendSMS(smsURL);
	                }
	            }
	            cmsCon.close();
	            st.close();
	            rs.close();
	            
	       
	        }catch (Exception e)
	        {            
	           // objUtility.funWriteErrorLog(e);
	            e.printStackTrace();
	        }
	    }
		

	private String funSendSMS(String url)
	    {
		 System.out.println("URL="+url);
	        StringBuilder output = new StringBuilder();
	        try
	        {
	            URL hp = new URL(url);
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
	        System.out.println("SMS="+output.toString());
	        return output.toString();
	    }
		
		
		

	@SuppressWarnings("finally")
	private String funInsertBillHdData(JSONArray mJsonArray)
	{
		String res="";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		String billNo="";
		boolean flgHomeDelivery=false;
		Connection cmsCon=null;
		Statement st = null;
		String operationType="";
		String insert_qry="";
		String insert_homedelivery_qry="";
		String sql="",deleteSql="",tableNo="",POSCode="",homedeliverysql="";
		Date objDate = new Date();
      
	    String currentDate = (objDate.getYear() + 1900) + "-" +(objDate.getMonth()+ 1) + "-" + objDate.getDate() 
		                      + " "+ objDate.getHours() + ":" + objDate.getMinutes() + ":" +objDate.getSeconds();
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			clsUtilityFunctions objUtility=new clsUtilityFunctions();
			
			
				
			
			insert_qry="INSERT INTO `tblbillhd` (`strBillNo`, "
				+ "`strAdvBookingNo`, `dteBillDate`, `strPOSCode`, "
				+ "`strSettelmentMode`, `dblDiscountAmt`, "
				+ "`dblDiscountPer`, `dblTaxAmt`, `dblSubTotal`, "
				+ "`dblGrandTotal`, `strTakeAway`, `strOperationType`, "
				+ "`strUserCreated`, `strUserEdited`, `dteDateCreated`, "
				+ "`dteDateEdited`, `strClientCode`, `strTableNo`,"
				+ " `strWaiterNo`, `strCustomerCode`, `strManualBillNo`,"
				+ " `intShiftCode`, `intPaxNo`, `strDataPostFlag`, "
				+ "`strReasonCode`, `strRemarks`, `dblTipAmount`, "
				+ "`dteSettleDate`, `strCounterCode`, `dblDeliveryCharges`, "
				+ "`strCouponCode`,`strAreaCode`,`strDiscountRemark`,`strTakeAwayRemarks`,`strCardNo`,`dblRoundOff`,`dtBillDate`,`dblUSDConverionRate`) "
				+ "VALUES";
			
			
			insert_homedelivery_qry="INSERT INTO  tblhomedelivery (strBillNo, "
					+ "strCustomerCode, strDPCode,dteDate,tmeTime,strPOSCode, "
					+ "strCustAddressLine1,strCustAddressLine2,strCustAddressLine3 , "
					+ "strCustAddressLine4,strCustCity,strDataPostFlag,strClientCode,dblHomeDeliCharge) "
					+ "VALUES";
			
			
			
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				mJsonObject =(JSONObject) mJsonArray.get(i);
		            
				billNo=mJsonObject.get("BillNo").toString();
				String clientCode=mJsonObject.get("ClientCode").toString();
				POSCode=mJsonObject.get("POSCode").toString();
			    double gRoundOffTo=0.00,grandTotalRoundOffBy=0.00,dblUSDConverionRate=0.00;
				
				
				deleteSql="delete from tblbillhd "
		            + "where strBillNo='"+billNo+"' and strClientCode='"+clientCode+"'";
				st.executeUpdate(deleteSql);
				
				double grandTotal=Double.parseDouble(mJsonObject.get("GrandTotal").toString());
				
				
				String sqlRoundOff=" SELECT a.dblRoundOff,a.dblUSDConverionRate FROM tblsetup a  WHERE a.strPOSCode='"+POSCode+"'  or a.strPOSCode='All' ";
		    	System.out.println(sqlRoundOff);
		    	
		    	 ResultSet rsGlobal=st.executeQuery(sqlRoundOff);
		 	     if(rsGlobal.next())
		 	      {
		 	    	gRoundOffTo=rsGlobal.getDouble(1);
		 	    	dblUSDConverionRate=rsGlobal.getDouble(2);
		 	      }
		 	    rsGlobal.close();
				
				
				 //start code to calculate roundoff amount and round off by amt
		        Map<String, Double> mapRoundOff = objUtility.funCalculateRoundOffAmount(grandTotal,gRoundOffTo);
		        grandTotal = mapRoundOff.get("roundOffAmt");
		        grandTotalRoundOffBy = mapRoundOff.get("roundOffByAmt");
		        //end code to calculate roundoff amount and round off by amt
				
				
				String advBookingNo=mJsonObject.get("AdvBookingNo").toString();
				String billDate=mJsonObject.get("BillDate").toString();
				if(billDate.contains("%20")){
					billDate=billDate.replace("%20"," ");
				}
				String date[]=billDate.split(" ");
				String billTime=date[1];
				String settelmentMode=mJsonObject.get("SettelmentMode").toString();
				double discountAmt=Double.parseDouble(mJsonObject.get("DiscountAmt").toString());
				double discountPer=Double.parseDouble(mJsonObject.get("DiscountPer").toString());
				double taxAmt=Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				double subTotal=Double.parseDouble(mJsonObject.get("SubTotal").toString());
				String takeAway=mJsonObject.get("TakeAway").toString();
				operationType=mJsonObject.get("OperationType").toString();
				String userCreated=mJsonObject.get("UserCreated").toString();
				String userEdited=mJsonObject.get("UserEdited").toString();
				String dateCreated=mJsonObject.get("DateCreated").toString();
				String dateEdited=mJsonObject.get("DateEdited").toString();
				tableNo=mJsonObject.get("TableNo").toString();
				String waiterNo=mJsonObject.get("WaiterNo").toString();
				String customerCode=mJsonObject.get("CustomerCode").toString();
				String manualBillNo=mJsonObject.get("ManualBillNo").toString();
				int shiftCode=Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				int paxNo=Integer.parseInt(mJsonObject.get("PaxNo").toString());
				String dataPostFlag=mJsonObject.get("DataPostFlag").toString();
				String reasonCode=mJsonObject.get("ReasonCode").toString();
				String remarks=mJsonObject.get("Remarks").toString();
				double tipAmount=Double.parseDouble(mJsonObject.get("TipAmount").toString());
				String settleDate=mJsonObject.get("SettleDate").toString();
				String counterCode=mJsonObject.get("CounterCode").toString();
				double deliveryCharges=Double.parseDouble(mJsonObject.get("DeliveryCharges").toString());
				String couponCode=mJsonObject.get("CouponCode").toString();
				String areaCode=mJsonObject.get("AreaCode").toString();
				String discRemark=mJsonObject.get("DiscountRemark").toString();
				String takeAwayRemark=mJsonObject.get("TakeAwayRemark").toString();
				String cardNo=mJsonObject.get("CardNo").toString();
				String billDateOnly=mJsonObject.get("BillDt").toString();
				
				    
				sql+=",('"+billNo+"','"+advBookingNo+"','"+billDate+"','"+POSCode+"','"+settelmentMode+"',"
					+ "'"+discountAmt+"','"+discountPer+"','"+taxAmt+"','"+subTotal+"','"+grandTotal+"',"
		    		+ "'"+takeAway+"','"+operationType+"','"+userCreated+"','"+userEdited+"','"+currentDate+"',"
    				+ "'"+currentDate+"','"+clientCode+"','"+tableNo+"','"+waiterNo+"','"+customerCode+"',"
					+ "'"+manualBillNo+"','"+shiftCode+"','"+paxNo+"','N','"+reasonCode+"',"
					+ "'"+remarks+"','"+tipAmount+"','"+settleDate+"','"+counterCode+"','"+deliveryCharges+"',"
					+ "'"+couponCode+"','"+areaCode+"','"+discRemark+"','"+takeAwayRemark+"',"
					+ "'"+cardNo+"','"+grandTotalRoundOffBy+"','"+billDateOnly+"','"+dblUSDConverionRate+"')";
				flgData=true;	
				
				res=billNo+"#"+"NotHomeDelivery";
				System.out.println("res for non homedelivery= "+res);
				if(operationType.equalsIgnoreCase("HomeDelivery"))
				{
					homedeliverysql+=",('"+billNo+"','"+customerCode+"','','"+billDate+"','"+billTime+"','"+POSCode+"',"
							+ "'Home','','','','','N','"+clientCode+"','"+deliveryCharges+"' )";
					flgHomeDelivery=true;
					res=billNo+"#"+"HomeDelivery";
					System.out.println("res for homedelivery= "+res);
					
				}
				
				//funInsertDebitCardBillDetails(customerCode);
			}			
			//System.out.println("flag= "+flgData+"\tTable= "+tableNo);
			
			if(flgData)
			{
				sql=sql.substring(1,sql.length());
				insert_qry+=" "+sql;
				
				try{
					System.out.println(insert_qry);
					st.executeUpdate(insert_qry);
					
					if(!tableNo.isEmpty())
					{
						sql="delete from tblitemrtemp where strTableNo='"+tableNo+"' "
							+ "and strPOSCode='"+POSCode+"' ";
						st.executeUpdate(sql);
						sql="update tbltablemaster set strStatus='Billed' "
							+ "where strTableNo='"+tableNo+"' and strPOSCode='"+POSCode+"'";
						st.executeUpdate(sql);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			else
			{
				res=billNo;
			}
			
			
			
			if(flgHomeDelivery)
			{
				homedeliverysql=homedeliverysql.substring(1,homedeliverysql.length());
				insert_homedelivery_qry+=" "+homedeliverysql;
				
				try{
					System.out.println(insert_homedelivery_qry);
					st.executeUpdate(insert_homedelivery_qry);
					
				} catch(Exception e)
				{
					e.printStackTrace();
				}
				
				
			}
			else
			{
				res=billNo;
			}
			
			
			cmsCon.close();
			st.close();
			
		} catch(Exception e)
		{
			res="Error";
			e.printStackTrace();
		}
		finally
        {
        	try {
				st.close();
				cmsCon.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}
        
        	if(operationType.equalsIgnoreCase("Home Delivery"))
			{
			    res=billNo+"#"+"HomeDelivery";
				System.out.println("res for homedelivery= "+res);
				
			}
        	else
        	{
        		res=billNo+"#"+"NotHomeDelivery";
        		
        	}
            return res;
            
        }
	}
	
	
	@SuppressWarnings("finally")
	private int funInsertBillDtlData(JSONArray mJsonArray,boolean isModifierPresentInBill)
	{
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		String insert_qry="";
		String sql="",deleteSql;
       
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			insert_qry = "INSERT INTO `tblbilldtl` (`strItemCode`, `strItemName`,"
				+ " `strBillNo`, `strAdvBookingNo`, `dblRate`, `dblQuantity`, "
				+ "`dblAmount`, `dblTaxAmount`, `dteBillDate`, `strKOTNo`, "
				+ "`strClientCode`, `strCustomerCode`, `tmeOrderProcessing`, "
				+ "`strDataPostFlag`, `strMMSDataPostFlag`, `strManualKOTNo`, "
				+ "`tdhYN`, `strPromoCode`,`strCounterCode`,`strWaiterNo`,`dblDiscountAmt`,`dblDiscountPer`,`dtBillDate`,`tmeOrderPickup`) "
				+ "VALUES";
			
			JSONObject mJsonObject = new JSONObject();
			String BillNo="",BillDate="";
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				mJsonObject =(JSONObject) mJsonArray.get(i);
		            
		        BillNo=mJsonObject.get("BillNo").toString();
		        String ClientCode=mJsonObject.get("ClientCode").toString();
		            
		        deleteSql="delete from tblbilldtl "
		          	+ "where strBillNo='"+BillNo+"' and strClientCode='"+ClientCode+"'";
					st.executeUpdate(deleteSql);
		            
		    	    String ItemCode=mJsonObject.get("ItemCode").toString();
				    String ItemName=mJsonObject.get("ItemName").toString();
				    String AdvBookingNo=mJsonObject.get("AdvBookingNo").toString();
				    double Rate=Double.parseDouble(mJsonObject.get("Rate").toString());
				    double Quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());
				    double Amount=Double.parseDouble(mJsonObject.get("Amount").toString());
				    double TaxAmount=Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				    BillDate=mJsonObject.get("BillDate").toString();
				    String KOTNo=mJsonObject.get("KOTNo").toString();
				    String CustomerCode=mJsonObject.get("CustomerCode").toString();
				    String OrderProcessing=mJsonObject.get("OrderProcessedTime").toString();
				    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
				    String MMSDataPostFlag=mJsonObject.get("MMSDataPostFlag").toString();
				    String ManualKOTNo=mJsonObject.get("ManualKOTNo").toString();
				    String tdhYN=mJsonObject.get("tdhYN").toString();
				    String promoCode=mJsonObject.get("PromoCode").toString();
				    String counterCode=mJsonObject.get("CounterCode").toString();
				    String waiterNo=mJsonObject.get("WaiterNo").toString();
				    String discountAmt=mJsonObject.get("DiscountAmt").toString();
				    String disAmt="0.0",disPer="0.0";
				    if(discountAmt.contains("#"))
			    	{
			    		disAmt=discountAmt.split("#")[0];
				    	disPer=discountAmt.split("#")[1];
			    	}
				    String billDateOnly=mJsonObject.get("BillDt").toString();
				    String OrderPickedUpTime=mJsonObject.get("OrderPickedUpdTime").toString();
				    
				    System.out.println(BillDate+" = "+BillNo);
				    
				    sql+=",('"+ItemCode+"','"+ItemName+"','"+BillNo+"','"+AdvBookingNo+"','"+Rate+"',"
			    		+ "'"+Quantity+"','"+Amount+"','"+TaxAmount+"','"+BillDate+"',"
	    				+ "'"+KOTNo+"','"+ClientCode+"','"+CustomerCode+"','"+OrderProcessing+"','N',"
						+ "'"+MMSDataPostFlag+"','"+ManualKOTNo+"','"+tdhYN+"','"+promoCode+"',"
						+ "'"+counterCode+"','"+waiterNo+"','"+disAmt+"','"+disPer+"','"+billDateOnly+"','"+OrderPickedUpTime+"')";
				    
				    flgData=true;
			}
			
			
			if(flgData)
			{
				sql=sql.substring(1,sql.length());
				insert_qry+=" "+sql;
				try{
					System.out.println(insert_qry);
					res=st.executeUpdate(insert_qry);
					if(!isModifierPresentInBill){
						//update bill items tax values
						objUtilityController.funUpdateBillDtlWithTaxValues( BillNo,"live",BillDate);
					}
					
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			else
			{
				res=1;
			}
			cmsCon.close();
			st.close();
			
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
		 finally
        {
        	try {
				st.close();
				cmsCon.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;
        }
	}
	
	
	@SuppressWarnings("finally")
	private int funInsertBillModifierDtlData(JSONArray mJsonArray)
	{
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		String insert_qry="";
		String sql="",deleteSql="";
       
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			insert_qry = "INSERT INTO `tblbillmodifierdtl` (`strBillNo`, `strItemCode`, "
				+ "`strModifierCode`, `strModifierName`, `dblRate`, `dblQuantity`, "
				+ "`dblAmount`, `strClientCode`, `strCustomerCode`, `strDataPostFlag`, "
				+ "`strMMSDataPostFlag`,`strDefaultModifierDeselectedYN`,`dblDiscPer`,`dblDiscAmt`,`dteBillDate`) VALUES";
			
			JSONObject mJsonObject = new JSONObject();
			String BillNo="",billDate="";
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				mJsonObject =(JSONObject) mJsonArray.get(i);
		    	BillNo=mJsonObject.get("BillNo").toString();
		    	String ClientCode=mJsonObject.get("ClientCode").toString();
		    	    
		    	deleteSql="delete from tblbillmodifierdtl "
		    	   	+ "where strBillNo='"+BillNo+"' and strClientCode='"+ClientCode+"'";
		    	st.executeUpdate(deleteSql);
		    	
				String ItemCode=mJsonObject.get("ItemCode").toString();
				String ModifierCode=mJsonObject.get("ModifierCode").toString();
				String ModifierName=mJsonObject.get("ModifierName").toString();
				double Rate=Double.parseDouble(mJsonObject.get("Rate").toString());
				double Quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());
				double Amount=Double.parseDouble(mJsonObject.get("Amount").toString());
				String CustomerCode=mJsonObject.get("CustomerCode").toString();
				String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
				String MMSDataPostFlag=mJsonObject.get("MMSDataPostFlag").toString();
				String DefaultModifierSelection=mJsonObject.get("DefaultModifierSelection").toString();
				double DiscPer=Double.parseDouble(mJsonObject.get("DiscPer").toString());
				double DiscAmt=Double.parseDouble(mJsonObject.get("DiscAmt").toString());
				billDate=mJsonObject.get("BillDate").toString();
				    
				sql+=",('"+BillNo+"','"+ItemCode+"','"+ModifierCode+"','"+ModifierName+"'"
					+ ",'"+Rate+"','"+Quantity+"','"+Amount+"','"+ClientCode+"','"+CustomerCode+"'"
					+ ",'N','"+MMSDataPostFlag+"','"+DefaultModifierSelection+"','"+DiscPer+"','"+DiscAmt+"','"+billDate+"')";
				    
				flgData=true;

			}
						
			if(flgData)
			{
				sql=sql.substring(1,sql.length());
				insert_qry+=" "+sql;
				try{
					System.out.println(insert_qry);
					res=st.executeUpdate(insert_qry);
					//update bill items tax values
					objUtilityController.funUpdateBillDtlWithTaxValues( BillNo,"live",billDate);
				} catch(Exception e){
					e.printStackTrace();
				}
				System.out.println(res);
			}
			else
			{
				res=1;
			}
			cmsCon.close();
			st.close();
			
			
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
		 finally
        {
        	try {
				st.close();
				cmsCon.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;
        }
	}
	
	
	@SuppressWarnings("finally")
	private int funInsertBillPromotionDtlData(JSONArray mJsonArray)
	{
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		String insert_qry="";
		String sql="",deleteSql="";
       
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			insert_qry = "insert into tblbillpromotiondtl "
                    + "(strBillNo,strItemCode,strPromotionCode,dblQuantity"
                    + ",dblRate,strClientCode,strDataPostFlag,strPromoType,dblAmount,dteBillDate) values ";
			
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{		            
		            mJsonObject =(JSONObject) mJsonArray.get(i);
		            String BillNo=mJsonObject.get("BillNo").toString();
		            String ClientCode=mJsonObject.get("ClientCode").toString();		            
		            
		            deleteSql="delete from tblbillpromotiondtl "
		            	+ "where strBillNo='"+BillNo+"' and strClientCode='"+ClientCode+"'";
		            st.executeUpdate(deleteSql);
		            
				    String ItemCode=mJsonObject.get("ItemCode").toString();
				    String PromotionCode=mJsonObject.get("PromoCode").toString();
				    double Quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());	
				    double Rate=Double.parseDouble(mJsonObject.get("Rate").toString());
				    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
				    String billDate=mJsonObject.get("BillDate").toString();
				    
				    sql+=",('" + BillNo + "','" + ItemCode + "','" + PromotionCode + "'"
                       + ",'" + Quantity + "','" + Rate + "','" +mJsonObject.get("ClientCode").toString()+ "'"
                       + ",'"+mJsonObject.get("DataPostFlag").toString()+"','" +mJsonObject.get("PromoType").toString()+ "','" +mJsonObject.get("Amount").toString()+ "','"+billDate+"')";
				    
				    flgData=true;

			}
						
			if(flgData)
			{
				sql=sql.substring(1,sql.length());
				insert_qry+=" "+sql;
				System.out.println("Promotion insert_qry:" +insert_qry);
				try{
					res=st.executeUpdate(insert_qry);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			else
			{
				res=1;
			}
			cmsCon.close();
			st.close();
			
			
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
		 finally
        {
        	try {
				st.close();
				cmsCon.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;        	
        }
	}
	
	
	
	// Function to save discount details in bill. tblbilldiscountdtl    
	@SuppressWarnings("finally")
    private int funInsertBillDiscountDetail(JSONArray mJsonArray)
    {
    	int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			StringBuilder sqlBillDiscDtl = new StringBuilder();
            sqlBillDiscDtl.append("insert into tblbilldiscdtl values ");
            double totalDiscAmt = 0.00, finalDiscPer = 0.00;

            JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{		            
		            mJsonObject =(JSONObject) mJsonArray.get(i);
		            String billNo=mJsonObject.get("BillNo").toString();
		            String clientCode=mJsonObject.get("ClientCode").toString();	
		            String userCode=mJsonObject.get("UserCode").toString();	
		            String reason=mJsonObject.get("Reason").toString();	
		            String remark=mJsonObject.get("Remark").toString();	
		            String currentDateTime=mJsonObject.get("CurrentDateTime").toString();	
		            String posDate=mJsonObject.get("POSDate").toString();	
		            String POSCode=mJsonObject.get("POSCode").toString();
		            double discAmt=Double.parseDouble(mJsonObject.get("DiscAmt").toString());
		            double discPer=Double.parseDouble(mJsonObject.get("DiscPer").toString());
		            double discOnAmt=Double.parseDouble(mJsonObject.get("DiscOnAmt").toString());
		            String discOnType=mJsonObject.get("DiscOnType").toString();
		            String discOnValue=mJsonObject.get("DiscOnValue").toString();
		            
		            if (i == 0)
	                {
	                    sqlBillDiscDtl.append("('" + billNo + "','" + POSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + reason + "','" + remark + "','" + userCode + "','" + userCode + "','" + currentDateTime + "','" + currentDateTime + "','" + clientCode + "','N','" + posDate+ "')");
	                }
	                else
	                {
	                    sqlBillDiscDtl.append(",('" + billNo + "','" + POSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + reason + "','" + remark + "','" + userCode + "','" + userCode + "','" + currentDateTime + "','" + currentDateTime + "','" + clientCode + "','N','" + posDate + "')");
	                }
		            totalDiscAmt += discAmt;
		            flgData=true;
			 } 
						
			if(flgData)
			{
				System.out.println("Discount insert_qry:" +sqlBillDiscDtl.toString());
				try{
					res=st.executeUpdate(sqlBillDiscDtl.toString());
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			else
			{
				res=1;
			}
		
			cmsCon.close();
			st.close();
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
	    finally
        {
        	try {
				st.close();
				cmsCon.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;        	
        }
    	
   
    }
	
	
	

	@SuppressWarnings("finally")
	private int funInsertBillComplementoryDtlData(JSONArray mJsonArray)
	{
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		String insert_qry="";
		String sql="",deleteSql;
       
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
		
			
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				mJsonObject =(JSONObject) mJsonArray.get(i);
		            
		        String billNo=mJsonObject.get("BillNo").toString();
		        String reasonCode=mJsonObject.get("reasonCode").toString();
		        String remark=mJsonObject.get("remark").toString();
		        String couponCode=mJsonObject.get("couponCode").toString();
		        String posCode=mJsonObject.get("posCode").toString();
		        String posDate=mJsonObject.get("posDate").toString();
		        String clientCode=mJsonObject.get("clientCode").toString();
		            
		    	res=funClearComplimetaryBillAmt(billNo,reasonCode,remark,couponCode,posCode,posDate,clientCode);
			}
		
			
	
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
		 finally
        {
        	try {
				st.close();
				cmsCon.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;
        }
	}
	
	
	
	  private int funClearComplimetaryBillAmt(String billNo,String reasonCode,String remark,String couponcode,String posCode,String posDate,String clientCode) throws Exception
	    {
		   clsDatabaseConnection objDb=new clsDatabaseConnection();
		   Connection cmsCon=null;
		   Statement st = null,st1=null;
		   int res=0;
		   try
			 {
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				st1 = cmsCon.createStatement();
				
				String sqlDelete = "delete from tblbillcomplementrydtl where strBillNo='" + billNo + "'";
				st.executeUpdate(sqlDelete);
               

		        String sqlInsertBillComDtl = "insert into tblbillcomplementrydtl(strItemCode,strItemName,strBillNo"
		                + ",strAdvBookingNo,dblRate,dblQuantity,dblAmount,dblTaxAmount,dteBilldate,strKOTNo"
		                + ",strClientCode,strCustomerCode,tmeOrderProcessing,strDataPostFlag,strMMSDataPostFlag"
		                + ",strManualKOTNo,tdhYN,strPromoCode,strCounterCode,strWaiterNo,dblDiscountAmt,dblDiscountPer"
		                + ",strSequenceNo,dtBillDate,tmeOrderPickup)"
		                + " select "
		                + "strItemCode,strItemName,strBillNo,strAdvBookingNo,dblRate,dblQuantity,"
		                + "dblAmount,dblTaxAmount,dteBilldate,strKOTNo,strClientCode,ifnull(strCustomerCode,''),"
		                + "tmeOrderProcessing,strDataPostFlag,strMMSDataPostFlag,strManualKOTNo,tdhYN,strPromoCode,"
		                + "strCounterCode,strWaiterNo,dblDiscountAmt,dblDiscountPer,strSequenceNo,dtBillDate,tmeOrderPickup "
		                + " from tblbilldtl where strBillNo='" + billNo + "' ";

		        st.executeUpdate(sqlInsertBillComDtl);
             

                 String sqlUpdate = "update tblbilltaxdtl set dblTaxableAmount=0.00,dblTaxAmount=0.00 "
                         + "where strBillNo='" + billNo + "'";
                 st.executeUpdate(sqlUpdate);
               

                 sqlUpdate = "update tblbillhd set dblTaxAmt=0.00,dblSubTotal=0.00"
                         + ",dblDiscountAmt=0.00,dblDiscountPer=0.00,strReasonCode='" + reasonCode + "'"
                         + ",strRemarks='" + remark + "',dblDeliveryCharges=0.00"
                         + ",strCouponCode='" + couponcode + "',dblGrandTotal=0.00,dblRoundOff=0.00 "
                         + "where strBillNo='" + billNo + "'";
                 st.executeUpdate(sqlUpdate);
               

                 sqlUpdate = "update tblbilldtl set dblAmount=0.00,dblDiscountAmt=0.00,dblDiscountPer=0.00,dblTaxAmount=0.00 "
                         + "where strBillNo='" + billNo + "'";
                 st.executeUpdate(sqlUpdate);
            

                 sqlUpdate = "update tblbillmodifierdtl set dblAmount=0.00,dblDiscPer=0.00,dblDiscAmt=0.00 where strBillNo='" + billNo + "'";
                 st.executeUpdate(sqlUpdate);
                

                 sqlUpdate = "update tblbillsettlementdtl set dblSettlementAmt=0.00,dblPaidAmt=0.00 where strBillNo='" + billNo + "'";
                 st.executeUpdate(sqlUpdate);
               

                 sqlUpdate = "update tblbillseriesbilldtl set dblGrandTotal=0.00 where strHdBillNo='" + billNo + "' "
                         + " and strPOSCode='" + posCode+ "' "
                         + " and date(dteBillDate)='" + posDate+ "' ";
                 st.executeUpdate(sqlUpdate);
                

                 st.executeUpdate("delete from tblbilldiscdtl where strBillNo='" + billNo + "' ");
              
                 res=1;
                 //send modified bill MSG
                 String sql = "select a.strSendSMSYN,a.longMobileNo "
                         + "from tblsmssetup a "
                         + "where (a.strPOSCode='" + posCode+ "' or a.strPOSCode='All') "
                         + "and a.strClientCode='" + clientCode + "' "
                         + "and a.strTransactionName='ComplementaryBill' "
                         + "and a.strSendSMSYN='Y'; ";
                 ResultSet rsSendSMS = st1.executeQuery(sql);
                 if (rsSendSMS.next())
                 {
                     String mobileNo = rsSendSMS.getString(2);//mobileNo

                    // funSendComplementaryBillSMS(billNo, mobileNo);

                 }
                 rsSendSMS.close();
			
	     } 
		 catch(Exception e)
		 {
			 res=0;
				e.printStackTrace();
		 }
		 finally
	      {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	   
	     }
	        return res;
	    }
	  
	  

	
	@SuppressWarnings("finally")
	private int funInsertCRMPointsData(JSONArray mJsonArray)
	{
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		String insert_qry="";
		String sql="",deleteSql="";
       
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			insert_qry = "INSERT INTO `tblcrmpoints` (`strBillNo`, `dblPoints`,`strTransactionId`"
				+ ", `strOutletUID`, `dblRedeemedAmt`, `longCustMobileNo`,`strClientCode`"
				+ ",`strDataPostFlag`,`dblValue`,`strCustomerId`,`dteBillDate`) VALUES";
			
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{		            
		            mJsonObject =(JSONObject) mJsonArray.get(i);
		            String billNo=mJsonObject.get("BillNo").toString();
		            String clientCode=mJsonObject.get("ClientCode").toString();		            
		            
		            deleteSql="delete from tblcrmpoints "
		            	+ "where strBillNo='"+billNo+"' and strClientCode='"+clientCode+"'";
		            st.executeUpdate(deleteSql);
		            
				    String crmPoints=mJsonObject.get("CRMPoints").toString();
				    String transId=mJsonObject.get("TransactionId").toString();
				    String outletUId=mJsonObject.get("OutletUID").toString();
				    String redeemedAmt=mJsonObject.get("RedeemedAmt").toString();
				    String custMobileNo=mJsonObject.get("CustomerMobileNo").toString();
				    String value=mJsonObject.get("Value").toString();
				    String custId=mJsonObject.get("CustomerId").toString();
				    String billDate=mJsonObject.get("BillDate").toString();
				    
				    sql+=",('"+billNo+"','"+crmPoints+"','"+transId+"','"+outletUId+"','"+redeemedAmt+"'"
				    	+ ",'"+custMobileNo+"','"+clientCode+"','N','"+value+"','"+custId+"','"+billDate+"')";
				    
				    flgData=true;

			}
						
			if(flgData)
			{
				sql=sql.substring(1,sql.length());
				insert_qry+=" "+sql;
				try{
					res=st.executeUpdate(insert_qry);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			else
			{
				res=1;
			}
			
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
		 finally
        {
        	try {
				st.close();
				cmsCon.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;
        }
	}
	
	
	@SuppressWarnings("finally")
	private int funInsertBillSettlementDtlData(JSONArray mJsonArray)
	{
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		String insert_qry="";
		String sql="",deleteSql="",billNo="";
       
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			insert_qry = "INSERT INTO `tblbillsettlementdtl` (`strBillNo`, `strSettlementCode`,"
					+ " `dblSettlementAmt`, `dblPaidAmt`, `strExpiryDate`, `strCardName`,"
					+ " `strRemark`, `strClientCode`, `strCustomerCode`, `dblActualAmt`, "
					+ "`dblRefundAmt`, `strGiftVoucherCode`, `strDataPostFlag`,`strFolioNo`,`strRoomNo`,`dteBillDate`) VALUES";
			
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				mJsonObject =(JSONObject) mJsonArray.get(i);
		    	    
		    	String BillNo=mJsonObject.get("BillNo").toString();
		    	String ClientCode=mJsonObject.get("ClientCode").toString();
		    	   
		    	deleteSql="delete from tblbillsettlementdtl "
		    		+ "where strBillNo='"+BillNo+"' and strClientCode='"+ClientCode+"'";
		    	st.executeUpdate(deleteSql);
		    	
				String SettlementCode=mJsonObject.get("SettlementCode").toString();
				double SettlementAmt=Double.parseDouble(mJsonObject.get("SettlementAmt").toString());
				double PaidAmt=Double.parseDouble(mJsonObject.get("PaidAmt").toString());
				String ExpiryDate=mJsonObject.get("ExpiryDate").toString();
				String CardName=mJsonObject.get("CardName").toString();
				String Remark=mJsonObject.get("Remark").toString();
				String CustomerCode=mJsonObject.get("CustomerCode").toString();
				double ActualAmt=Double.parseDouble(mJsonObject.get("ActualAmt").toString());
				double RefundAmt=Double.parseDouble(mJsonObject.get("RefundAmt").toString());
				String GiftVoucherCode=mJsonObject.get("GiftVoucherCode").toString();
				String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
				String debitCardNo=mJsonObject.get("DebitCardNo").toString();
				String debitCardString=mJsonObject.get("DebitCardString").toString();
				String POSCode=mJsonObject.get("POSCode").toString();
				String POSDate=mJsonObject.get("POSDate").toString();
				String settlementMode=mJsonObject.get("SettlementMode").toString();
				String folioNo=mJsonObject.get("FolioNo").toString();
				String roomNo=mJsonObject.get("RoomNo").toString();
				String billDate=mJsonObject.get("BillDate").toString();
				System.out.println(settlementMode);
				System.out.println(debitCardNo);
				System.out.println(debitCardString);
				if(settlementMode.equals("Debit Card"))
				{
						funDebitCardTransaction(BillNo,debitCardNo,SettlementAmt,POSCode,POSDate);
						funUpdateDebitCardBalance(debitCardString, SettlementAmt);
					
					
				}
				
				billNo=BillNo;
				sql+=",('"+BillNo+"','"+SettlementCode+"','"+SettlementAmt+"','"+PaidAmt+"','"+ExpiryDate+"','"
					+CardName+"','"+Remark+"','"+ClientCode+"','"+CustomerCode+"','"+ActualAmt+"'"
					+ ",'"+RefundAmt+"','"+GiftVoucherCode+"','"+DataPostFlag+"','"+folioNo+"','"+roomNo+"','"+billDate+"')";
				//System.out.println(insert_qry);
				
				flgData=true;

			}
						
			if(flgData)
			{
				
				sql=sql.substring(1,sql.length());
				insert_qry+=" "+sql;
				try{
					System.out.println(insert_qry);
					res=st.executeUpdate(insert_qry);
					
					String sqlUpdateTableStatus="update tbltablemaster a,tblbillhd b "
						+ "set a.strStatus='Normal' "
						+ "where a.strTableNo=b.strTableNo and b.strBillNo='"+billNo+"'";
					st.executeUpdate(sqlUpdateTableStatus);
					
				} catch(Exception e){
					e.printStackTrace();
				}
				System.out.println(res);
			}
			else
			{
				res=1;
			}
			
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
		 finally
        {
        	try {
				st.close();
				cmsCon.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;        	
        }
	}
	
	
	@SuppressWarnings("finally")
	private int funInsertBillTaxDtlData(JSONArray mJsonArray)
	{
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		boolean flgData=false;
		Connection cmsCon=null;
		Statement st = null;
		String insert_qry="";
		String sql="",deleteSql="";
       
		try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			insert_qry = "INSERT INTO `tblbilltaxdtl` (`strBillNo`, `strTaxCode`,"
				+ " `dblTaxableAmount`, `dblTaxAmount`, `strClientCode`, "
				+ "`strDataPostFlag`,`dteBillDate`) VALUES";
			
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) 
			{		            
		        mJsonObject =(JSONObject) mJsonArray.get(i);
			    	    
		    	String BillNo=mJsonObject.get("BillNo").toString();
		    	String ClientCode=mJsonObject.get("ClientCode").toString();
		    	
		    	deleteSql="delete from tblbilltaxdtl "
			    	+ "where strBillNo='"+BillNo+"' and strClientCode='"+ClientCode+"'";
			    st.executeUpdate(deleteSql);
		    	
				String TaxCode=mJsonObject.get("TaxCode").toString();
				double TaxableAmount=Double.parseDouble(mJsonObject.get("TaxableAmount").toString());				  			  
				double TaxAmount=Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
				String billDate=mJsonObject.get("BillDate").toString();
				    
				sql+=",('"+BillNo+"','"+TaxCode+"','"+TaxableAmount+"','"+TaxAmount+"','"
				    +ClientCode+"','"+DataPostFlag+"','"+billDate+"')";				    
				flgData=true;
			}
						
			if(flgData)
			{
				sql=sql.substring(1,sql.length());
				insert_qry+=" "+sql;
				try{
					res=st.executeUpdate(insert_qry);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			else
			{
				res=1;
			}
			
		} catch(Exception e)
		{
			res=0;
			e.printStackTrace();
		}
		 finally
        {
        	try {
				st.close();
				cmsCon.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            return res;
        }
	}
		
		@SuppressWarnings("finally")
		private int funInsertVoidBillHdData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblvoidbillhd` (`strPosCode`, `strReasonCode`, "
					+ "`strReasonName`, `strBillNo`, `dblActualAmount`, `dblModifiedAmount`, "
					+ "`dteBillDate`, `strTransType`, `dteModifyVoidBill`, `strTableNo`, "
					+ "`strWaiterNo`, `intShiftCode`, `strUserCreated`,`strUserEdited`"
					+ ", `strClientCode`, `strDataPostFlag`,`strRemark`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++)
				{
					mJsonObject =(JSONObject) mJsonArray.get(i);
					String billNo=mJsonObject.get("BillNo").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();
					
					deleteSql="delete from tblvoidbillhd "
					    + "where strBillNo='"+billNo+"' and strClientCode='"+clientCode+"'";
					st.executeUpdate(deleteSql);
					
			        String posCode=mJsonObject.get("PosCode").toString();
					String reasonCode=mJsonObject.get("ReasonCode").toString();
					String reasonName=mJsonObject.get("ReasonName").toString();
					double actualAmount=Double.parseDouble(mJsonObject.get("ActualAmount").toString());
					double modifiedAmount=Double.parseDouble(mJsonObject.get("ModifiedAmount").toString());
					String billDate=mJsonObject.get("BillDate").toString();
					String transType=mJsonObject.get("TransType").toString();
					String modifyVoidBill=mJsonObject.get("ModifyVoidBill").toString();
					String tableNo=mJsonObject.get("TableNo").toString();
					String waiterNo=mJsonObject.get("WaiterNo").toString();
					int shiftCode=Integer.parseInt(mJsonObject.get("ShiftCode").toString());
					String userCreated=mJsonObject.get("UserCreated").toString();
					String userEdited=mJsonObject.get("UserEdited").toString();
					String remark=mJsonObject.get("Remark").toString();

					sql+=",('"+posCode+"','"+reasonCode+"','"+reasonName+"','"+billNo+"'"
						+ ",'"+actualAmount+"','"+modifiedAmount+"','"+billDate+"'"
					    + ",'"+transType+"','"+modifyVoidBill+"','"+tableNo+"','"+waiterNo+"'"
					    + ",'"+shiftCode+"','"+userCreated+"','"+userEdited+"','"+clientCode+"'"
					    + ",'N','"+remark+"')";
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		
		@SuppressWarnings("finally")
		private int funInsertVoidBillDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblvoidbilldtl` (`strPosCode`, `strReasonCode`, "
					+ "`strReasonName`, `strItemCode`, `strItemName`, `strBillNo`, "
					+ "`intQuantity`, `dblAmount`, `dblTaxAmount`, `dteBillDate`, "
					+ "`strTransType`, `dteModifyVoidBill`, `strSettlementCode`, "
					+ "`dblSettlementAmt`, `dblPaidAmt`, `strTableNo`, `strWaiterNo`, "
					+ "`intShiftCode`, `strUserCreated`, `strClientCode`, `strDataPostFlag`,"
					+ " `strKOTNo`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
					mJsonObject =(JSONObject) mJsonArray.get(i);
			            
					String billNo=mJsonObject.get("BillNo").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();
					String TransType=mJsonObject.get("TransType").toString();
					
					deleteSql="delete from tblvoidbilldtl "
					    + " where strBillNo='"+billNo+"' and strClientCode='"+clientCode+"' "
					    + " and strTransType='"+TransType+"'";
					st.executeUpdate(deleteSql);
					
			        String PosCode=mJsonObject.get("PosCode").toString();
					String ReasonCode=mJsonObject.get("ReasonCode").toString();
					String ReasonName=mJsonObject.get("ReasonName").toString();
					String ItemCode=mJsonObject.get("ItemCode").toString();
					String ItemName=mJsonObject.get("ItemName").toString();					
					double Quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());
					double Amount=Double.parseDouble(mJsonObject.get("Amount").toString());
					double TaxAmount=Double.parseDouble(mJsonObject.get("TaxAmount").toString());
					String BillDate=mJsonObject.get("BillDate").toString();					
					String ModifyVoidBill=mJsonObject.get("ModifyVoidBill").toString();
					String SettlementCode=mJsonObject.get("SettlementCode").toString();
					double SettlementAmt=Double.parseDouble(mJsonObject.get("SettlementAmt").toString());
					double PaidAmt=Double.parseDouble(mJsonObject.get("PaidAmt").toString());  
					String TableNo=mJsonObject.get("TableNo").toString();  
					String WaiterNo=mJsonObject.get("WaiterNo").toString();
					String ShiftCode=mJsonObject.get("ShiftCode").toString();
					String UserCreated=mJsonObject.get("UserCreated").toString();					
					String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					String KOTNo=mJsonObject.get("KOTNo").toString();
					    
					sql+=",('"+PosCode+"','"+ReasonCode+"','"+ReasonName+"','"+ItemCode+"','"
					    +ItemName+"','"+billNo+"','"+Quantity+"','"+Amount+"','"+TaxAmount+"','"+BillDate+"','"+TransType+"','"
					    +ModifyVoidBill+"','"+SettlementCode+"','"+SettlementAmt+"','"+PaidAmt+"','"+TableNo+"','"
					    +WaiterNo+"','"+ShiftCode+"','"+UserCreated+"','"+clientCode+"','"+DataPostFlag+"','"+KOTNo+"')";
					    
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
	

		@SuppressWarnings("finally")
		private int funInsertVoidKotData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblvoidkot` (`strTableNo`, `strPOSCode`,"
					+ " `strItemCode`, `strItemName`, `dblItemQuantity`, `dblAmount`, "
					+ "`strWaiterNo`, `strKOTNo`, `intPaxNo`, `strType`, "
					+ "`strReasonCode`, `strUserCreated`, `dteDateCreated`, "
					+ "`dteVoidedDate`, `strDataPostFlag`, `strClientCode`,"
					+ " `strManualKOTNo`, `strPrintKOT`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
					mJsonObject =(JSONObject) mJsonArray.get(i);
			        
					String KOTNo=mJsonObject.get("KOTNo").toString();
					String ClientCode=mJsonObject.get("ClientCode").toString();
					
					deleteSql="delete from tblvoidkot "
					    + "where strKOTNo='"+KOTNo+"' and strClientCode='"+ClientCode+"'";
					st.executeUpdate(deleteSql);
					
			        String TableNo=mJsonObject.get("TableNo").toString();
					String POSCode=mJsonObject.get("POSCode").toString();
					String ItemCode=mJsonObject.get("ItemCode").toString();
					String ItemName=mJsonObject.get("ItemName").toString();
					double ItemQuantity=Double.parseDouble(mJsonObject.get("ItemQuantity").toString());
					double Amount=Double.parseDouble(mJsonObject.get("Amount").toString());
					String WaiterNo=mJsonObject.get("WaiterNo").toString();
					int PaxNo=Integer.parseInt(mJsonObject.get("PaxNo").toString());
					String Type=mJsonObject.get("Type").toString();
					String ReasonCode=mJsonObject.get("ReasonCode").toString();
					String UserCreated=mJsonObject.get("UserCreated").toString();
					String DateCreated=mJsonObject.get("DateCreated").toString();
					String VoidedDate=mJsonObject.get("VoidedDate").toString();
					String ManualKOTNo=mJsonObject.get("ManualKOTNo").toString();
					String PrintKOT=mJsonObject.get("PrintKOT").toString();
					    
					sql+=",('"+TableNo+"','"+POSCode+"','"+ItemCode+"','"+ItemName+"'"
				    	+ ",'"+ItemQuantity+"','"+Amount+"','"+WaiterNo+"','"+KOTNo+"'"
		    			+ ",'"+PaxNo+"','"+Type+"','"+ReasonCode+"','"+UserCreated+"'"
    					+ ",'"+DateCreated+"','"+VoidedDate+"','N','"+ClientCode+"'"
    					+ ",'"+ManualKOTNo+"','"+PrintKOT+"')";
					    
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			finally
	        {
	        	try {
					st.close();
					cmsCon.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		}
		
		
		
		@SuppressWarnings("finally")
		private int funInsertStkInHdData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblstkinhd` (`strStkInCode`, `strPOSCode`, `dteStkInDate`,"
					+ " `strReasonCode`, `strPurchaseBillNo`, `dtePurchaseBillDate`, `intShiftCode`,"
					+ " `strUserCreated`, `strUserEdited`, `dteDateCreated`, `dteDateEdited`, "
					+ "`strClientCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			        mJsonObject =(JSONObject) mJsonArray.get(i);
	
			        String stkInCode=mJsonObject.get("StkInCode").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();
						
					deleteSql="delete from tblstkinhd "
					    + "where strStkInCode='"+stkInCode+"' and strClientCode='"+clientCode+"'";
					st.executeUpdate(deleteSql);
			        
					String POSCode=mJsonObject.get("POSCode").toString();
					String StkInDate=mJsonObject.get("StkInDate").toString();
					String ReasonCode=mJsonObject.get("ReasonCode").toString();
					String PurchaseBillNo=mJsonObject.get("PurchaseBillNo").toString();
					String PurchaseBillDate=mJsonObject.get("PurchaseBillDate").toString();
					int ShiftCode=Integer.parseInt(mJsonObject.get("ShiftCode").toString());
					String UserCreated=mJsonObject.get("UserCreated").toString();
					String UserEdited=mJsonObject.get("UserEdited").toString();
					String DateCreated=mJsonObject.get("DateCreated").toString();
					String DateEdited=mJsonObject.get("DateEdited").toString();					
					String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    
					sql+=",('"+stkInCode+"','"+POSCode+"','"+StkInDate+"','"+ReasonCode+"','"
					    +PurchaseBillNo+"','"+PurchaseBillDate+"','"+ShiftCode+"','"+UserCreated+"','"+UserEdited
					    +"','"+DateCreated+"','"+DateEdited+"','"+clientCode+"','"+DataPostFlag+"')";
					    
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		
		@SuppressWarnings("finally")
		private int funInsertStkInDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
			insert_qry = "INSERT INTO `tblstkindtl` (`strStkInCode`, `strItemCode`, `dblQuantity`, "
						+ "`dblPurchaseRate`, `dblAmount`, `strClientCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			            mJsonObject =(JSONObject) mJsonArray.get(i);
	
			            String stkInCode=mJsonObject.get("StkInCode").toString();
						String clientCode=mJsonObject.get("ClientCode").toString();
							
						deleteSql="delete from tblstkindtl "
						    + "where strStkInCode='"+stkInCode+"' and strClientCode='"+clientCode+"'";
						st.executeUpdate(deleteSql);
						
					    String ItemCode=mJsonObject.get("ItemCode").toString();
					    double Quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());
					    double PurchaseRate=Double.parseDouble(mJsonObject.get("PurchaseRate").toString());
					    double Amount=Double.parseDouble(mJsonObject.get("Amount").toString());					    
					    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    
					    sql+=",('"+stkInCode+"','"+ItemCode+"','"+Quantity+"','"+PurchaseRate
					    +"','"+Amount+"','"+clientCode+"','"+DataPostFlag+"')";
					    
					    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		
		@SuppressWarnings("finally")
		private int funInsertStkOutHdData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
			insert_qry = "INSERT INTO `tblstkouthd` (`strStkOutCode`, `strPOSCode`,"
					+ " `dteStkOutDate`, `strReasonCode`, `strPurchaseBillNo`, "
					+ "`dtePurchaseBillDate`, `intShiftCode`, `strUserCreated`,"
					+ " `strUserEdited`, `dteDateCreated`, `dteDateEdited`, "
					+ "`strClientCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			            mJsonObject =(JSONObject) mJsonArray.get(i);
	
			            String stkOutCode=mJsonObject.get("StkOutCode").toString();
						String clientCode=mJsonObject.get("ClientCode").toString();
							
						deleteSql="delete from tblstkouthd "
						    + "where strStkOutCode='"+stkOutCode+"' and strClientCode='"+clientCode+"'";
						st.executeUpdate(deleteSql);
			            
					    String POSCode=mJsonObject.get("POSCode").toString();
					    String StkOutDate=mJsonObject.get("StkOutDate").toString();
					    String ReasonCode=mJsonObject.get("ReasonCode").toString();
					    String PurchaseBillNo=mJsonObject.get("PurchaseBillNo").toString();
					    String PurchaseBillDate=mJsonObject.get("PurchaseBillDate").toString();
					    int ShiftCode=Integer.parseInt(mJsonObject.get("ShiftCode").toString());
					    String UserCreated=mJsonObject.get("UserCreated").toString();
					    String UserEdited=mJsonObject.get("UserEdited").toString();
					    String DateCreated=mJsonObject.get("DateCreated").toString();
					    String DateEdited=mJsonObject.get("DateEdited").toString();
					    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    
					    sql+=",('"+stkOutCode+"','"+POSCode+"','"+StkOutDate+"','"+ReasonCode+"','"
					    +PurchaseBillNo+"','"+PurchaseBillDate+"','"+ShiftCode+"','"+UserCreated+"','"+UserEdited
					    +"','"+DateCreated+"','"+DateEdited+"','"+clientCode+"','"+DataPostFlag+"')";
					    
					    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		
		@SuppressWarnings("finally")
		private int funInsertStkOutDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
			insert_qry = "INSERT INTO `tblstkoutdtl` (`strStkOutCode`, `strItemCode`, `dblQuantity`, "
						+ "`dblPurchaseRate`, `dblAmount`, `strClientCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			            mJsonObject =(JSONObject) mJsonArray.get(i);
	
			            String stkOutCode=mJsonObject.get("StkOutCode").toString();
						String clientCode=mJsonObject.get("ClientCode").toString();
							
						deleteSql="delete from tblstkoutdtl "
						    + "where strStkOutCode='"+stkOutCode+"' and strClientCode='"+clientCode+"'";
						st.executeUpdate(deleteSql);			            
			            
					    String ItemCode=mJsonObject.get("ItemCode").toString();
					    double Quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());
					    double PurchaseRate=Double.parseDouble(mJsonObject.get("PurchaseRate").toString());
					    double Amount=Double.parseDouble(mJsonObject.get("Amount").toString());					    
					    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    
					    sql+=",('"+stkOutCode+"','"+ItemCode+"','"+Quantity+"','"+PurchaseRate
					    +"','"+Amount+"','"+clientCode+"','"+DataPostFlag+"')";
					    
					    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		
		
		@SuppressWarnings("finally")
		private int funInsertPspHdData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblpsphd` (`strPSPCode`, `strPOSCode`, "
					+ "`strStkInCode`, `strStkOutCode`, `strBillNo`, `dblStkInAmt`, "
					+ "`dblSaleAmt`, `intShiftCode`, `strUserCreated`, `strUserEdited`, "
					+ "`dteDateCreated`, `dteDateEdited`, `strClientCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			            mJsonObject =(JSONObject) mJsonArray.get(i);

			            String phyStkCode=mJsonObject.get("PSPCode").toString();
						String clientCode=mJsonObject.get("ClientCode").toString();
							
						deleteSql="delete from tblpsphd "
						    + "where strPSPCode='"+phyStkCode+"' and strClientCode='"+clientCode+"'";
						st.executeUpdate(deleteSql);
			            			            
					    String POSCode=mJsonObject.get("POSCode").toString();
					    String StkInCode=mJsonObject.get("StkInCode").toString();
					    String StkOutCode=mJsonObject.get("StkOutCode").toString();
					    String BillNo=mJsonObject.get("BillNo").toString();
					    double StkInAmt=Double.parseDouble(mJsonObject.get("StkInAmt").toString());
					    double SaleAmt=Double.parseDouble(mJsonObject.get("SaleAmt").toString());
					    int ShiftCode=Integer.parseInt(mJsonObject.get("ShiftCode").toString());
					    String UserCreated=mJsonObject.get("UserCreated").toString();				    
					    String UserEdited=mJsonObject.get("UserEdited").toString();
					    String DateCreated=mJsonObject.get("DateCreated").toString();
					    String DateEdited=mJsonObject.get("DateEdited").toString();					    
					    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    
					    sql+=",('"+phyStkCode+"','"+POSCode+"','"+StkInCode+"','"+StkOutCode+"'"
					    	+ ",'"+BillNo+"','"+StkInAmt+"','"+SaleAmt+"','"+ShiftCode+"'"
					    	+ ",'"+UserCreated+"','"+UserEdited+"','"+DateCreated+"','"+DateEdited+"'"
			    			+ ",'"+clientCode+"','"+DataPostFlag+"')";
					    
					    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		@SuppressWarnings("finally")
		private int funInsertPspDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblpspdtl` (`strPSPCode`, `strItemCode`,"
						+ " `dblPhyStk`, `dblCompStk`, `dblVariance`, `dblVairanceAmt`,"
						+ " `strClientCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
					mJsonObject =(JSONObject) mJsonArray.get(i);

			        String phyStkCode=mJsonObject.get("PSPCode").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();
							
					deleteSql="delete from tblpspdtl "
					    + "where strPSPCode='"+phyStkCode+"' and strClientCode='"+clientCode+"'";
					st.executeUpdate(deleteSql);
			            
					String ItemCode=mJsonObject.get("ItemCode").toString();
					double PhyStk=Double.parseDouble(mJsonObject.get("PhyStk").toString());
					double CompStk=Double.parseDouble(mJsonObject.get("CompStk").toString());
					double Variance=Double.parseDouble(mJsonObject.get("Variance").toString());
					double VarianceAmt=Double.parseDouble(mJsonObject.get("VarianceAmt").toString());					    
					String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    
					sql+=",('"+phyStkCode+"','"+ItemCode+"','"+PhyStk+"','"+CompStk+"','"+Variance+"','"
					   	+VarianceAmt+"','"+clientCode+"','"+DataPostFlag+"')";					    
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		}
		
		@SuppressWarnings("finally")
		private int funInsertAdvanceReceiptHdData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblqadvancereceipthd` (`strReceiptNo`, `strAdvBookingNo`,"
					+ " `strPOSCode`, `strSettelmentMode`, `dtReceiptDate`, `dblAdvDeposite`,"
					+ " `intShiftCode`, `strUserCreated`, `strUserEdited`, `dtDateCreated`,"
					+ " `dtDateEdited`, `strClientCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			            mJsonObject =(JSONObject) mJsonArray.get(i);
			            
			            String receiptNo=mJsonObject.get("ReceiptNo").toString();
						String clientCode=mJsonObject.get("ClientCode").toString();
								
						deleteSql="delete from tblqadvancereceipthd "
						    + "where strReceiptNo='"+receiptNo+"' and strClientCode='"+clientCode+"'";
						st.executeUpdate(deleteSql);
			            
					    String ItemCode=mJsonObject.get("AdvBookingNo").toString();
					    String POSCode=mJsonObject.get("POSCode").toString();
					    String SettelmentMode=mJsonObject.get("SettelmentMode").toString();
					    String ReceiptDate=mJsonObject.get("ReceiptDate").toString();
					    double AdvDeposite=Double.parseDouble(mJsonObject.get("AdvDeposite").toString());
					    int ShiftCode=Integer.parseInt(mJsonObject.get("ShiftCode").toString());
					    String UserCreated=mJsonObject.get("UserCreated").toString();
					    String UserEdited=mJsonObject.get("UserEdited").toString();
					    String DateCreated=mJsonObject.get("DateCreated").toString();
					    String DateEdited=mJsonObject.get("DateEdited").toString();					    
					    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    
					    sql+=",('"+receiptNo+"','"+ItemCode+"','"+POSCode+"','"+SettelmentMode
				    		+"','"+ReceiptDate+"','"+AdvDeposite+"','"+ShiftCode+"','"+UserCreated
				    		+"','"+UserEdited+"','"+DateCreated+"','"+DateEdited+"','"+clientCode
				    		+"','"+DataPostFlag+"')";
					    
					    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		@SuppressWarnings("finally")
		private int funInsertAdvanceReceiptDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblqadvancereceiptdtl` (`strReceiptNo`,"
					+ " `strSettlementCode`, `strCardNo`, `strExpirydate`, "
					+ "`strChequeNo`, `dteCheque`, `strBankName`, "
					+ "`dblAdvDepositesettleAmt`, `strRemark`, `dblPaidAmt`, "
					+ "`strClientCode`, `strDataPostFlag`, `dteInstallment`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			            mJsonObject =(JSONObject) mJsonArray.get(i);

			            String receiptNo=mJsonObject.get("ReceiptNo").toString();
						String clientCode=mJsonObject.get("ClientCode").toString();
								
						deleteSql="delete from tblqadvancereceiptdtl "
						    + "where strReceiptNo='"+receiptNo+"' and strClientCode='"+clientCode+"'";
						st.executeUpdate(deleteSql);
			            
					    String SettlementCode=mJsonObject.get("SettlementCode").toString();
					    String CardNo=mJsonObject.get("CardNo").toString();
					    String Expirydate=mJsonObject.get("Expirydate").toString();
					    String ChequeNo=mJsonObject.get("ChequeNo").toString();
					    String ChequeDate=mJsonObject.get("ChequeDate").toString();
					    String BankName=mJsonObject.get("BankName").toString();
					    double AdvDepositesettleAmt=Double.parseDouble(mJsonObject.get("AdvDepositesettleAmt").toString());
					    String Remark=mJsonObject.get("Remark").toString();
					    double PaidAmt=Double.parseDouble(mJsonObject.get("PaidAmt").toString());
					    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    String Installment=mJsonObject.get("Installment").toString();
					    
					    sql+=",('"+receiptNo+"','"+SettlementCode+"','"+CardNo+"','"+Expirydate
					    		+"','"+ChequeNo+"','"+ChequeDate+"','"+BankName+"','"+AdvDepositesettleAmt
					    		+"','"+Remark+"','"+PaidAmt+"','"+clientCode+"','"+DataPostFlag
					    		+"','"+Installment+"')";
					    
					    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		@SuppressWarnings("finally")
		private int funInsertAdvBookBillHdData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblqadvbookbillhd` (`strAdvBookingNo`, "
					+ "`dteAdvBookingDate`, `dteOrderFor`, `strPOSCode`, "
					+ "`strSettelmentMode`, `dblDiscountAmt`, `dblDiscountPer`,"
					+ " `dblTaxAmt`, `dblSubTotal`, `dblGrandTotal`,"
					+ " `strUserCreated`, `strUserEdited`, `dteDateCreated`, "
					+ "`dteDateEdited`, `strClientCode`, `strCustomerCode`, "
					+ "`intShiftCode`, `strMessage`, `strShape`, `strNote`, "
					+ "`strDataPostFlag`, `strDeliveryTime`, `strWaiterNo`, "
					+ "`strHomeDelivery`, `dblHomeDelCharges`, `strOrderType`, "
					+ "`strManualAdvOrderNo`, `strImageName`, `strSpecialsymbolImage`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{		            
			            mJsonObject =(JSONObject) mJsonArray.get(i);
			            
			            String advBookingNo=mJsonObject.get("AdvBookingNo").toString();
						String clientCode=mJsonObject.get("ClientCode").toString();
								
						deleteSql="delete from tblqadvbookbillhd "
						    + "where strAdvBookingNo='"+advBookingNo+"' and strClientCode='"+clientCode+"'";
						st.executeUpdate(deleteSql);
			            
					    String AdvBookingDate=mJsonObject.get("AdvBookingDate").toString();
					    String OrderFor=mJsonObject.get("OrderFor").toString();
					    String POSCode=mJsonObject.get("POSCode").toString();
					    String SettelmentMode=mJsonObject.get("SettelmentMode").toString();
					    double DiscountAmt=Double.parseDouble(mJsonObject.get("DiscountAmt").toString());
					    double DiscountPer=Double.parseDouble(mJsonObject.get("DiscountPer").toString());
					    double TaxAmt=Double.parseDouble(mJsonObject.get("TaxAmt").toString());
					    double SubTotal=Double.parseDouble(mJsonObject.get("SubTotal").toString());
					    double GrandTotal=Double.parseDouble(mJsonObject.get("GrandTotal").toString());					 
					    String UserCreated=mJsonObject.get("UserCreated").toString();
					    String UserEdited=mJsonObject.get("UserEdited").toString();
					    String Installment=mJsonObject.get("DateCreated").toString();
					    String DateEdited=mJsonObject.get("DateEdited").toString();					    
					    String CustomerCode=mJsonObject.get("CustomerCode").toString();
					    int ShiftCode=Integer.parseInt(mJsonObject.get("ShiftCode").toString());
					    String Message=mJsonObject.get("Message").toString();
					    String Shape=mJsonObject.get("Shape").toString();
					    String Note=mJsonObject.get("Note").toString();
					    String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					    String DeliveryTime=mJsonObject.get("DeliveryTime").toString();
					    String WaiterNo=mJsonObject.get("WaiterNo").toString();
					    String HomeDelivery=mJsonObject.get("HomeDelivery").toString();
					    double HomeDelCharges=Double.parseDouble(mJsonObject.get("HomeDelCharges").toString());	 
					    String OrderType=mJsonObject.get("OrderType").toString();
					    String ManualAdvOrderNo=mJsonObject.get("ManualAdvOrderNo").toString();
					    String ImageName=mJsonObject.get("ImageName").toString();
					    String SpecialsymbolImage=mJsonObject.get("SpecialsymbolImage").toString();
					    
					    sql+=",('"+advBookingNo+"','"+AdvBookingDate+"','"+OrderFor+"','"+POSCode
				    		+"','"+SettelmentMode+"','"+DiscountAmt+"','"+DiscountPer+"','"+TaxAmt
				    		+"','"+SubTotal+"','"+GrandTotal+"','"+UserCreated
				    		+"','"+UserEdited+"','"+Installment+"','"+DateEdited+"','"+clientCode
				    		+"','"+CustomerCode+"','"+ShiftCode+"','"+Message+"','"+Shape
				    		+"','"+Note+"','"+DataPostFlag+"','"+DeliveryTime+"','"+WaiterNo
				    		+"','"+HomeDelivery+"','"+HomeDelCharges+"','"+OrderType+"','"+ManualAdvOrderNo
				    		+"','"+ImageName+"','"+SpecialsymbolImage+"')";
					    
				    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		} 
		
		@SuppressWarnings("finally")
		private int funInsertAdvBookBillDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblqadvbookbilldtl` (`strItemCode`, "
					+ "`strItemName`, `strAdvBookingNo`, `dblQuantity`, `dblAmount`, "
					+ "`dblTaxAmount`, `dteAdvBookingDate`, `dteOrderFor`, `strClientCode`,"
					+ " `strCustomerCode`, `strDataPostFlag`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
					mJsonObject =(JSONObject) mJsonArray.get(i);
			            
			        String advBookingNo=mJsonObject.get("AdvBookingNo").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();
								
					deleteSql="delete from tblqadvbookbilldtl "
					    + "where strAdvBookingNo='"+advBookingNo+"' and strClientCode='"+clientCode+"'";
					st.executeUpdate(deleteSql);
			            
			        String ItemCode=mJsonObject.get("ItemCode").toString();
					String ItemName=mJsonObject.get("ItemName").toString();					    
					double Quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());
					double Amount=Double.parseDouble(mJsonObject.get("Amount").toString());
					double TaxAmount=Double.parseDouble(mJsonObject.get("TaxAmount").toString());
					String AdvBookingDate=mJsonObject.get("AdvBookingDate").toString();
					String OrderFor=mJsonObject.get("OrderFor").toString();					    
					String CustomerCode=mJsonObject.get("CustomerCode").toString();
					String DataPostFlag=mJsonObject.get("DataPostFlag").toString();
					   
					sql+=",('"+ItemCode+"','"+ItemName+"','"+advBookingNo+"','"+Quantity
					   	+"','"+Amount+"','"+TaxAmount+"','"+AdvBookingDate+"','"+OrderFor
					   	+"','"+clientCode+"','"+CustomerCode+"','"+DataPostFlag+"')";					    
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		}
		
		
		@SuppressWarnings("finally")
		private int funInsertAdvBookBillModiferDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblqadvordermodifierdtl` "
					+ "(`strAdvOrderNo`,`strItemCode`,`strModifierCode`, `strModifierName`"
					+ ", `dblQuantity`, `dblAmount`,`strClientCode`, `strCustomerCode`"
					+ ", `strUserCreated`, `strUserEdited`,`dteDateCreated`, `dteDateEdited`"
					+ ",`strDataPostFlag`) "
					+ "VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
					mJsonObject =(JSONObject) mJsonArray.get(i);
			            
			        String advBookingNo=mJsonObject.get("AdvBookingNo").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();
								
					deleteSql="delete from tblqadvordermodifierdtl "
					    + "where strAdvOrderNo='"+advBookingNo+"' and strClientCode='"+clientCode+"'";
					st.executeUpdate(deleteSql);
			            
			        String itemCode=mJsonObject.get("ItemCode").toString();
					String modifierCode=mJsonObject.get("ModifierCode").toString();
					String modifierName=mJsonObject.get("ModifierName").toString();
					double quantity=Double.parseDouble(mJsonObject.get("Quantity").toString());
					double amount=Double.parseDouble(mJsonObject.get("Amount").toString());					
					String custCode=mJsonObject.get("CustomerCode").toString();
					String userCreated=mJsonObject.get("UserCreated").toString();
					String userEdited=mJsonObject.get("UserEdited").toString();
					String dateCreated=mJsonObject.get("DateCreated").toString();
					String dateEdited=mJsonObject.get("DateEdited").toString();
					String dataPostFlag=mJsonObject.get("DataPostFlag").toString();
					   
					sql+=",('"+advBookingNo+"','"+itemCode+"','"+modifierCode+"','"+modifierName+"'"
						+ ",'"+quantity+"','"+amount+"','"+clientCode+"','"+custCode+"'"
						+ ",'"+userCreated+"','"+userEdited+"','"+dateCreated+"','"+dateEdited+"'"
						+ ",'"+dataPostFlag+"')";					    
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		}
		
		
		@SuppressWarnings("finally")
		private int funInsertHomeDeliveryData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				
				insert_qry = "INSERT INTO `tblhomedelivery` "
					+ "(`strBillNo`,`strCustomerCode`, `strDPCode`, `dteDate`, `tmeTime`"
					+ ",`strPOSCode`, `strCustAddressLine1`, `strCustAddressLine2`, `strCustAddressLine3`"
					+ ",`strCustAddressLine4`, `strCustCity`,`strDataPostFlag`, `strClientCode`) VALUES";
				
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
					mJsonObject =(JSONObject) mJsonArray.get(i);
			            
			        String billNo=mJsonObject.get("BillNo").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();

					deleteSql="delete from tblhomedelivery "
					    + "where strBillNo='"+billNo+"' and strClientCode='"+clientCode+"'";
					st.executeUpdate(deleteSql);
			        
					String custCode=mJsonObject.get("CustomerCode").toString();
			        String dpCode=mJsonObject.get("DPCode").toString();
					String date=mJsonObject.get("Date").toString();
					String time=mJsonObject.get("Time").toString();
					String posCode=mJsonObject.get("POSCode").toString();
					String custAddr1=mJsonObject.get("CustAddress1").toString();
					String custAddr2=mJsonObject.get("CustAddress2").toString();
					String custAddr3=mJsonObject.get("CustAddress3").toString();
					String custAddr4=mJsonObject.get("CustAddress4").toString();
					String custCity=mJsonObject.get("CustCity").toString();
					String dataPostFlag=mJsonObject.get("DataPostFlag").toString();
					   
					sql+=",('"+billNo+"','"+custCode+"','"+dpCode+"','"+date+"','"+time+"'"
						+ ",'"+posCode+"','"+custAddr1+"','"+custAddr2+"','"+custAddr3+"'"
						+ ",'"+custAddr4+"','"+custCity+"','"+dataPostFlag+"','"+clientCode+"')";
					flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			finally
	        {
	        	try {
					st.close();
					cmsCon.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		}
		
		
		
		@SuppressWarnings("finally")
		private int funInsertHomeDeliveryDtlData(JSONArray mJsonArray)
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
				st = cmsCon.createStatement();
				insert_qry = "INSERT INTO `tblhomedeldtl` "
					+ "(`strBillNo`,`strDPCode`, `strClientCode`,`strDataPostFlag`,`dteBillDate`) VALUES";
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
					mJsonObject =(JSONObject) mJsonArray.get(i);
			        String billNo=mJsonObject.get("BillNo").toString();
					String clientCode=mJsonObject.get("ClientCode").toString();
					String billdate=mJsonObject.get("BillDt").toString();
					if(billdate.contains("%20")){
						billdate=billdate.replaceAll("%20", " ");
			        }
					deleteSql="delete from tblhomedeldtl "
					    + "where strBillNo='"+billNo+"' and strClientCode='"+clientCode+"'";
					st.executeUpdate(deleteSql);
			        String dpCode=mJsonObject.get("DPCode").toString();
					String dataPostFlag=mJsonObject.get("DataPostFlag").toString();
					sql+=",('"+billNo+"','"+dpCode+"','"+clientCode+"','"+dataPostFlag+"','"+billdate+"')";
					flgData=true;
				}

				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try{
						res=st.executeUpdate(insert_qry);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		}
		
		
		
		
		
		private String funGetMenuItemPricingDetail(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="select a.strItemCode,a.strItemName,right(b.strPropertyPOSCode,3),a.strMenuCode,a.strPopular"
	            		+" ,a.strPriceMonday,a.strPriceTuesday,a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday"
                        +" ,a.strPriceSaturday,a.strPriceSunday,a.dteFromDate,a.dteToDate"
                        +" ,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,a.strCostCenterCode,a.strTextColor"
                        +" ,a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strAreaCode"
                        +" ,a.strSubMenuHeadCode,a.strHourlyPricing,a.longPricingId" 
                        +" from tblmenuitempricingdtl a, tblPOSMaster b "
                        +" where a.strPOSCode = b.strPOSCode and (b.strPropertyPOSCode ='"+propertyPOSCode+"'"
                        +" OR a.strPOSCode = 'All')"
                        +" and a.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            JSONArray arrObj=new JSONArray();
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("ItemCode",rsMasterData.getString(1));
	            	obj.put("ItemName",rsMasterData.getString(2));
	            	obj.put("PropertyPOSCode",rsMasterData.getString(3));
	            	obj.put("MenuCode",rsMasterData.getString(4));
	            	obj.put("Popular",rsMasterData.getString(5));
	            	obj.put("PriceMonday",rsMasterData.getString(6));
	            	obj.put("PriceTuesday",rsMasterData.getString(7));
	            	obj.put("PriceWenesday",rsMasterData.getString(8));
	            	obj.put("PriceThursday",rsMasterData.getString(9));
	            	obj.put("PriceFriday",rsMasterData.getString(10));
	            	obj.put("PriceSaturday",rsMasterData.getString(11));
	            	obj.put("PriceSunday",rsMasterData.getString(12));
	            	obj.put("FromDate",rsMasterData.getString(13));
	            	obj.put("ToDate",rsMasterData.getString(14));
	            	obj.put("TimeFrom",rsMasterData.getString(15));
	            	obj.put("AMPMFrom",rsMasterData.getString(16));
	            	obj.put("TimeTo",rsMasterData.getString(17));
	            	obj.put("AMPMTo",rsMasterData.getString(18));
	            	obj.put("CostCenterCode",rsMasterData.getString(19));
	            	obj.put("TextColor",rsMasterData.getString(20));
	            	obj.put("UserCreated",rsMasterData.getString(21));
	            	obj.put("UserEdited",rsMasterData.getString(22));
	            	obj.put("DateCreated",rsMasterData.getString(23));
	            	obj.put("DateEdited",rsMasterData.getString(24));
	            	obj.put("AreaCode",rsMasterData.getString(25));
	            	obj.put("SubMenuHeadCode",rsMasterData.getString(26));
	            	obj.put("HourlyPricing",rsMasterData.getString(27));
	            	obj.put("PricingId",rsMasterData.getString(28));
	           
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tblmenuitempricingdtl", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
		
        private String funGetMenuItemPricingHD(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="select right(b.strPropertyPOSCode,3),a.strMenuCode,a.strMenuName,a.strUserCreated,"
	                    + "a.strUserEdited,a.dteDateCreated,a.dteDateEdited from tblmenuitempricinghd a, tblPOSMaster b "
	                    + "where a.strPOSCode = b.strPOSCode and (b.strPropertyPOSCode = '"+propertyPOSCode+"' "
	                    + "OR a.strPOSCode = 'All')"
	                    + "and a.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("PropertyPOSCode",rsMasterData.getString(1));
	            	obj.put("MenuCode",rsMasterData.getString(2));
	            	obj.put("MenuName",rsMasterData.getString(3));	            	
	            	obj.put("UserCreated",rsMasterData.getString(4));
	            	obj.put("UserEdited",rsMasterData.getString(5));
	            	obj.put("DateCreated",rsMasterData.getString(6));
	            	obj.put("DateEdited",rsMasterData.getString(7));	            		            
	           
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tblmenuitempricinghd", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        private String funGetItemModifier(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="select a.strItemCode,a.strModifierCode,a.strChargable,a.dblRate,a.strApplicable"
	                    + " from "+masterName +" a, tblmodifiermaster b "
	                    + "where a.strModifierCode = b.strModifierCode and b.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("ItemCode",rsMasterData.getString(1));
	            	obj.put("ModifierCode",rsMasterData.getString(2));
	            	obj.put("Chargable",rsMasterData.getString(3));	            	
	            	obj.put("Rate",rsMasterData.getString(4));
	            	obj.put("Applicable",rsMasterData.getString(5));	            	            		           
	           
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tblitemmodofier", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        
        private String funGetSettlementTax(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="select a.strTaxCode,a.strSettlementCode,a.strSettlementName,a.strApplicable,a.dteFrom,a.dteTo,"
	                    + "a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited "
	                    + "from "+masterName +" a, tbltaxhd b "
	                    + "where a.strTaxCode = b.strTaxCode and b.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	            ResultSet rsMasterData=st.executeQuery(sql);
	            
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("TaxCode",rsMasterData.getString(1));
	            	obj.put("SettlementCode",rsMasterData.getString(2));
	            	obj.put("SettlementName",rsMasterData.getString(3));	            	
	            	obj.put("Applicable",rsMasterData.getString(4));
	            	obj.put("FromDate",rsMasterData.getString(5));
	            	obj.put("ToDate",rsMasterData.getString(1));
	            	obj.put("UserCreated",rsMasterData.getString(2));
	            	obj.put("UserEdited",rsMasterData.getString(3));	            	
	            	obj.put("DateCreated",rsMasterData.getString(4));
	            	obj.put("DateEdited",rsMasterData.getString(5));
	           
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tblsettlementtax", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        private String funGetTaxPOSDetail(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql=" select a.strTaxCode,right(c.strPropertyPOSCode,3),a.strTaxDesc "
	                     + " from tbltaxposdtl a, tbltaxhd b ,tblposmaster c "
	                     + " where a.strTaxCode = b.strTaxCode and a.strPOSCode=c.strPosCode and b.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("TaxCode",rsMasterData.getString(1));
	            	obj.put("POSCode",rsMasterData.getString(2));
	            	obj.put("TaxDesc",rsMasterData.getString(3));
	           
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tbltaxposdtl", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        
        private String funGetSuperUserDetail(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="select a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd,a.strEdit"
	                    + ",a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant "
	                    + "from "+masterName +" a, tbluserhd b "
	                    + "where a.strUserCode = b.strUserCode and b.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("UserCode",rsMasterData.getString(1));
	            	obj.put("FormName",rsMasterData.getString(2));
	            	obj.put("ButtonName",rsMasterData.getString(3));
	            	obj.put("Sequence",rsMasterData.getString(4));
	            	obj.put("Add",rsMasterData.getString(5));
	            	obj.put("Edit",rsMasterData.getString(6));
	            	obj.put("Delete",rsMasterData.getString(7));	            
	            	obj.put("View",rsMasterData.getString(8));
	            	obj.put("Print",rsMasterData.getString(9));
	            	obj.put("Save",rsMasterData.getString(10));
	            	obj.put("Grant",rsMasterData.getString(11));
	           
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tblsuperuserdtl", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        private String funGetUserDetail(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String  sql="select a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd,a.strEdit"
	            	+ ",a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant "
	                + "from "+masterName +" a, tbluserhd b "
	                + "where a.strUserCode = b.strUserCode and b.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("UserCode",rsMasterData.getString(1));
	            	obj.put("FormName",rsMasterData.getString(2));
	            	obj.put("ButtonName",rsMasterData.getString(3));
	            	obj.put("Sequence",rsMasterData.getString(4));
	            	obj.put("Add",rsMasterData.getString(5));
	            	obj.put("Edit",rsMasterData.getString(6));
	            	obj.put("Delete",rsMasterData.getString(7));	            
	            	obj.put("View",rsMasterData.getString(8));
	            	obj.put("Print",rsMasterData.getString(9));
	            	obj.put("Save",rsMasterData.getString(10));
	            	obj.put("Grant",rsMasterData.getString(11));
	           
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tbluserdtl", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        
        private String funGetUserHD(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String   sql="select a.strUserCode,a.strUserName,a.strPassword,a.strSuperType,a.dteValidDate,"
	                    + "right(b.strPropertyPOSCode,3),a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited, "
	                    + "a.strClientCode,strDataPostFlag,imgUserIcon,strImgUserIconPath "
	                    + "from "+masterName +" a, tblposmaster b "
	                    + "where (a.strPOSAccess=right(b.strPropertyPOSCode,3) or a.strPOSAccess='All POS') "
	                    + "and b.strPropertyPOSCode='"+propertyPOSCode+"'and a.dteDateEdited > '"+lastModifiedDate+"' ";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("UserCode",rsMasterData.getString(1));
	            	obj.put("UserName",rsMasterData.getString(2));
	            	obj.put("Password",rsMasterData.getString(3));
	            	obj.put("SuperType",rsMasterData.getString(4));
	            	obj.put("ValidDate",rsMasterData.getString(5));
	            	obj.put("POSAccess",rsMasterData.getString(6));
	            	obj.put("UserCreated",rsMasterData.getString(7));	            
	            	obj.put("UserEdited",rsMasterData.getString(8));
	            	obj.put("DateCreated",rsMasterData.getString(9));
	            	obj.put("DateEdited",rsMasterData.getString(10));
	            	obj.put("ClientCode",rsMasterData.getString(11));
	            	obj.put("DataPostFlag",rsMasterData.getString(12));
	            	obj.put("ImageIcon",rsMasterData.getString(13));
	            	obj.put("ImagePath",rsMasterData.getString(14));
	            	
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tbluserhd", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        private String funGetRecipeDetail(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String  sql="select a.strRecipeCode,a.strChildItemCode,a.dblQuantity,right(c.strPropertyPOSCode,3)"
	                    + ",a.strClientCode,a.strDataPostFlag "
	                    + "from tblrecipedtl a, tblrecipehd b ,tblposmaster c "
	                    + "where a.strrecipeCode = b.strrecipeCode and a.strPOSCode=c.strPosCode "
	                    + "and b.dteDateEdited > '"+lastModifiedDate+"'";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("RecipeCode",rsMasterData.getString(1));
	            	obj.put("ChildItemCode",rsMasterData.getString(2));
	            	obj.put("Quantity",rsMasterData.getString(3));
	            	obj.put("POSCode",rsMasterData.getString(4));
	            	obj.put("ClientCode",rsMasterData.getString(5));
	            	obj.put("DataPostFlag",rsMasterData.getString(6));
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tblrecipedtl", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        private String funGetCounterDetail(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String  sql="select b.strCounterCode,b.strMenuCode,b.strClientCode "
	                    + "from tblcounterhd a,tblcounterdtl b "
	                    + "where a.strCounterCode=b.strCounterCode "
	                    + "and a.dteDateEdited >= '"+lastModifiedDate+"'";
	            System.out.println(sql);	            
	            JSONArray arrObj=new JSONArray();
	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("CounterCode",rsMasterData.getString(1));
	            	obj.put("MenuCode",rsMasterData.getString(2));
	            	obj.put("ClientCode",rsMasterData.getString(3));
	            	
	            	arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put("tblcounterdtl", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
       
        private String funGetMasterDetail(String masterName,String propertyPOSCode,String lastModifiedDate) {
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String masterData="";
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String   sql="select * from "+masterName+" where dteDateEdited > '"+lastModifiedDate+"' "
                        + "order by dteDateEdited";
	            System.out.println(sql);
	            
	            JSONArray arrObj=new JSONArray();
	           	            
	            ResultSet rsMasterData=st.executeQuery(sql);
	            ResultSetMetaData resultSetMetaData=rsMasterData.getMetaData();
	            int columnCount=resultSetMetaData.getColumnCount();
	            
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	                for(int i=1;i<=columnCount;i++)
	                {
	                	obj.put("Column"+i,rsMasterData.getString(i));	            	             		            	 
	                }
	                arrObj.put(obj);
	            }
	            	            
	            rsMasterData.close();
	            
	            jObj.put(masterName, arrObj);
	            st.close();
	            cmsCon.close();
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObj.toString();
		}
        
        @SuppressWarnings("rawtypes")
    	@POST
    	@Path("/funPostMasterData")
    	@Consumes(MediaType.APPLICATION_JSON)
    	public Response funPostMastersData(JSONObject rootObject)
    	{
    		String response = "true";
    		Iterator keyIterator=rootObject.keys();
    		while(keyIterator.hasNext())
    		{
    			String key = keyIterator.next().toString();
    			JSONArray dataArrayObject=null;
    			try 
    			{
    				dataArrayObject = (JSONArray) rootObject.get(key);
    			}
    			catch (Exception e) 
    			{
    				e.printStackTrace();
    			}

    			if (key.equalsIgnoreCase("tbldayendprocess") && dataArrayObject !=null) 
    			{
    				System.out.println(key);
    				if (funInsertDayEndProcessData(dataArrayObject) > 0) 
    				{
    					response = "true";
    				} 
    				else
    				{
    					response = "false";
    				}
    			}
    			if (key.equalsIgnoreCase("tblcustomermaster") && dataArrayObject !=null) 
    			{
    				System.out.println(key);
    				if (funInsertCustomerMasterData(dataArrayObject) > 0) 
    				{
    					response = "true";
    				} 
    				else
    				{
    					response = "false";
    				}
    			}
    			if (key.equalsIgnoreCase("tblbuildingmaster") && dataArrayObject !=null) 
    			{
    				System.out.println(key);
    				if (funInsertCustomerAreaData(dataArrayObject) > 0) 
    				{
    					response = "true";
    				} 
    				else
    				{
    					response = "false";
    				}
    			}
    			if (key.equalsIgnoreCase("tblareawisedc") && dataArrayObject !=null) 
    			{
    				System.out.println(key);
    				if (funInsertDelChargesData(dataArrayObject) > 0) 
    				{
    					response = "true";
    				} 
    				else
    				{
    					response = "false";
    				}
    			}
    			
    			
    		}
    		return Response.status(201).entity(response).build();
    	}


		private int funInsertDayEndProcessData(JSONArray dataArrayObject) 
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null,st1=null;
			String insert_qry="";
			String sql="",sql1="",deleteSql="";
			String posCode="";
			String startDate="";
			String newShiftCode="";
			String userCreated="";
			String dateCreated="";
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
				st = cmsCon.createStatement();
				st1 = cmsCon.createStatement();
				
				insert_qry ="INSERT INTO tbldayendprocess  (strPOSCode, dtePOSDate, strDayEnd, dblTotalSale," 
                       +"dblNoOfBill, dblNoOfVoidedBill , dblNoOfModifyBill ,  dblHDAmt, dblDiningAmt," 
                       +"dblTakeAway, dblFloat, dblCash, dblAdvance, dblTransferIn, dblTotalReceipt,"
                       +"dblPayments,dblWithdrawal,dblTransferOut,dblTotalPay,  dblCashInHand,dblRefund,"
                       +"dblTotalDiscount,dblNoOfDiscountedBill, intShiftCode, strShiftEnd,intTotalPax, "
                       +"intNoOfTakeAway,  intNoOfHomeDelivery, strUserCreated, dteDateCreated, dteDayEndDateTime," 
                       +"strUserEdited,  strDataPostFlag)"
                       +"VALUES ";
				//System.out.println("dataArrayObject:"+dataArrayObject);
				JSONObject dataObject = new JSONObject();
				for (int i = 0; i < dataArrayObject.length(); i++) 
				{
					dataObject =(JSONObject) dataArrayObject.get(i);
			            
					 posCode=dataObject.get("POSCode").toString();
				     String posDate=dataObject.get("POSDate").toString();
				     
					deleteSql="delete from tbldayendprocess "
						+ "where dtePOSDate='"+posDate+"' and strPOSCode='"+posCode+"'";
					st.execute(deleteSql);
			            
					    String dayEnd=dataObject.get("DayEnd").toString();
					    String totalSale=dataObject.get("TotalSale").toString();
					    String noOfBill=dataObject.get("NoOfBill").toString();
					    String noOfVoidedBill=dataObject.get("NoOfVoidedBill").toString();
					    String noOfModifyBill=dataObject.get("NoOfModifyBill").toString();
					    String hdAmount=dataObject.get("HDAmt").toString();
					    String diningAmount=dataObject.get("DiningAmt").toString();
					    String takeAway=dataObject.get("TakeAway").toString();
					    String floated=dataObject.get("Float").toString();
					    String cash=dataObject.get("Cash").toString();
					    String Advance=dataObject.get("Advance").toString();
					    String transferIn=dataObject.get("TransferIn").toString();
					    String totalReceipt=dataObject.get("TotalReceipt").toString();
					    String payments=dataObject.get("Payments").toString();
					    String withdrwal=dataObject.get("Withdrawal").toString();
					    String transferOut=dataObject.get("TransferOut").toString();
					    String totalPay=dataObject.get("TotalPay").toString();
					    String cashInHand=dataObject.get("CashInHand").toString();
					    String refund=dataObject.get("Refund").toString();					 
					    String totalDiscount=dataObject.get("TotalDiscount").toString();
					    String noOfDiscountedBill=dataObject.get("NoOfDiscountedBill").toString();
					    String shiftCode=dataObject.get("ShiftCode").toString();
					    String shiftEnd=dataObject.get("ShiftEnd").toString();
					    String totalPax=dataObject.get("Tota lPax").toString();
					    String noOfTakeAway=dataObject.get("NoOfTakeAway").toString();
					    String noOfHomeDelivery=dataObject.get("NoOfHomeDelivery").toString();
					    userCreated=dataObject.get("UserCreated").toString();
					    dateCreated=dataObject.get("DateCreated").toString();
					    String dayEndTime=dataObject.get("DayEndDateTime").toString();
					    String userEdited=dataObject.get("UserEdited").toString();					 
					    String dataPostFlag=dataObject.get("DataPostFlag").toString();		
					    
					    
					    sql+=",('"+ posCode +"','"+ posDate +"', '"+ dayEnd +"', '"+ totalSale +"', '"+ noOfBill +"', '"+ noOfVoidedBill +"', '"+ noOfModifyBill +"', "
					       + " '"+ hdAmount +"','"+ diningAmount +"', '"+ takeAway +"', '"+ floated +"', '"+ cash +"', '"+ Advance +"', '"+ transferIn +"', '"+ totalReceipt +"', '"+ payments +"', '"+ withdrwal +"', "
					       + " '"+ transferOut +"', '"+ totalPay +"', '"+ cashInHand +"', '"+refund +"', '"+ totalDiscount +"', '"+ noOfDiscountedBill +"', '"+ shiftCode +"', '"+shiftEnd+"', '"+ totalPax +"', "
					       + " '"+ noOfTakeAway +"', '"+ noOfHomeDelivery +"', '"+ userCreated +"', '"+ dateCreated +"', '"+ dayEndTime +"', '"+ userEdited +"', '"+ dataPostFlag +"')";
					   
					    flgData=true;

				}
							
				if(flgData)
				{
					
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try
					{
						
						System.out.println("poscode:"+posCode);
						res=st.executeUpdate(insert_qry);
					
					         
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		
		private int funInsertCustomerMasterData(JSONArray dataArrayObject) 
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql;
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
				st = cmsCon.createStatement();
				
			insert_qry ="INSERT INTO tblcustomermaster (strCustomerCode, strCustomerName, strBuldingCode, strBuildingName"
					+ ", strStreetName, strLandmark, strArea, strCity, strState, intPinCode, longMobileNo"
					+ ", longAlternateMobileNo, strOfficeBuildingCode, strOfficeBuildingName, strOfficeStreetName"
					+ ", strOfficeLandmark, strOfficeArea, strOfficeCity,strOfficePinCode, strOfficeState"
					+ ", strOfficeNo, strUserCreated, strUserEdited, dteDateCreated, dteDateEdited, strDataPostFlag, strClientCode,strOfficeAddress"
					+ ", strExternalCode, strCustomerType, dteDOB, strGender,dteAnniversary,strEmailId, strCRMId) VALUES";
				
				JSONObject dataObject = new JSONObject();
				for (int i = 0; i < dataArrayObject.length(); i++) 
				{		            
					dataObject =(JSONObject) dataArrayObject.get(i);
			            
			            String customerCode=dataObject.get("CustomerCode").toString();
			            String clientCode=dataObject.get("ClientCode").toString();
			            
			            deleteSql="delete from tblcustomermaster "
			            	+ "where strCustomerCode='"+customerCode+"'"
			            	+ " and strClientCode='"+clientCode+"'";
                        st.execute(deleteSql);
			            
					    String customerName=dataObject.get("CustomerName").toString();
					    String buildingCode=dataObject.get("BuldingCode").toString();
					    String buildingName=dataObject.get("BuildingName").toString();
					    String streetName=dataObject.get("StreetName").toString();
					    String landmark=dataObject.get("Landmark").toString();
					    String area=dataObject.get("Area").toString();
					    String city=dataObject.get("City").toString();
					    String state=dataObject.get("State").toString();
					    String pinCode=dataObject.get("PinCode").toString();					 
					    String mobileNumber=dataObject.get("MobileNo").toString();
					    String alternateMobileNumber=dataObject.get("AlternateMobileNo").toString();
					    String officeBuildingCode=dataObject.get("OfficeBuildingCode").toString();
					    String officeBuildingName=dataObject.get("OfficeBuildingName").toString();
					    String officeStreetName=dataObject.get("OfficeStreetName").toString();
					    String officeLandmark=dataObject.get("OfficeLandmark").toString();
					    String officeArea=dataObject.get("OfficeArea").toString();
					    String officeCity=dataObject.get("OfficeCity").toString();
					    String officePinCode=dataObject.get("OfficePinCode").toString();
					    String officeState=dataObject.get("OfficeState").toString();
					    String officeNo=dataObject.get("OfficeNo").toString();					 
					    String userCreated=dataObject.get("UserCreated").toString();
					    String userEdited=dataObject.get("UserEdited").toString();
					    String dateCreated=dataObject.get("DateCreated").toString();
					    String dateEdited=dataObject.get("DateEdited").toString();
					    String dataPostFlag=dataObject.get("DataPostFlag").toString();					    
					    String officeAddress=dataObject.get("OfficeAddress").toString();
					    String externalCode=dataObject.get("ExternalCode").toString();
					    String customerType=dataObject.get("CustomerType").toString();
					    String dob=dataObject.get("DOB").toString();
					    String gender=dataObject.get("Gender").toString();
					    String anniversary=dataObject.get("Anniversary").toString();	
					    String eMailId=dataObject.get("EmailId").toString();	
					    String crmId=dataObject.get("CRMId").toString();					   
					    
					    sql+=",('"+ customerCode +"','"+ customerName +"', '"+ buildingCode +"', '"+ buildingName +"', '"+ streetName +"', '"+ landmark +"', '"+ area +"', "
					       + " '"+ city +"','"+ state +"', '"+ pinCode +"', '"+ mobileNumber +"', '"+ alternateMobileNumber +"', '"+ officeBuildingCode +"', '"+ officeBuildingName +"', '"+ officeStreetName +"', '"+ officeLandmark +"', '"+ officeArea +"', "
					       + " '"+ officeCity +"', '"+ officePinCode +"', '"+ officeState +"', '"+officeNo +"', '"+ userCreated +"', '"+ userEdited +"', '"+ dateCreated +"', '"+dateEdited+"', '"+ dataPostFlag +"', "
					       + " '"+ clientCode +"','"+ officeAddress +"', '"+ externalCode +"', '"+ customerType +"', '"+ dob +"', '"+ gender +"', '"+ anniversary +"','"+eMailId+"', '"+ crmId +"')";
					    
					    flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try
					{
						res=st.executeUpdate(insert_qry);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		} 
		
		
		private int funInsertCustomerAreaData(JSONArray dataArrayObject) 
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
				st = cmsCon.createStatement();
				
				insert_qry ="INSERT INTO tblbuildingmaster (strBuildingCode, strBuildingName "
					+ ", strAddress, strUserCreated, strUserEdited, dteDateCreated, dteDateEdited"
					+ ", dblHomeDeliCharge, strClientCode, strDataPostFlag) VALUES";
				
				JSONObject dataObject = new JSONObject();
				for (int i = 0; i < dataArrayObject.length(); i++)
				{
					dataObject =(JSONObject) dataArrayObject.get(i);
			        
					String buildingCode=dataObject.get("BuildingCode").toString();
					String clientCode=dataObject.get("ClientCode").toString();
					
					deleteSql="delete from tblbuildingmaster "
						+ " where strBuildingCode='"+buildingCode+"' "
						+ " and strClientCode='"+clientCode+"'";
                    st.execute(deleteSql);
                    
					String buildingName=dataObject.get("BuildingName").toString();
					String address=dataObject.get("Address").toString();
					String userCreated=dataObject.get("UserCreated").toString();
					String userEdited=dataObject.get("UserEdited").toString();
					String dateCreated=dataObject.get("DateCreated").toString();
					String dateEdited=dataObject.get("DateEdited").toString();
					String homeDeliCharge=dataObject.get("HomeDeliCharge").toString();					    
					String dataPostFlag=dataObject.get("DataPostFlag").toString();
					    
					sql+=",('"+buildingCode+"','"+buildingName+"','"+address+"','"+userCreated+"','"+userEdited+"',"
						+ "'"+dateCreated+"','"+dateEdited+"','"+homeDeliCharge+"','"+clientCode+"','"+dataPostFlag+"' )";
					    
					flgData=true;

				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try
					{
						res=st.executeUpdate(insert_qry);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			 finally
	        {
	        	try {
					st.close();
					cmsCon.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;        	
	        }
		}
		
		
		private int funInsertDelChargesData(JSONArray dataArrayObject) 
		{
			int res=0;
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			boolean flgData=false;
			Connection cmsCon=null;
			Statement st = null;
			String insert_qry="";
			String sql="",deleteSql="";
	       
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
				st = cmsCon.createStatement();
				
				insert_qry ="INSERT INTO tblareawisedc (strBuildingCode, dblKilometers "
					+ ", strSymbol,dblBillAmount,dblBillAmount1,dblDeliveryCharges"
					+ ",strUserCreated, strUserEdited, dteDateCreated, dteDateEdited"
					+ ", strClientCode, strDataPostFlag) VALUES";
				
				JSONObject dataObject = new JSONObject();
				for (int i = 0; i < dataArrayObject.length(); i++) 
				{		            
					dataObject =(JSONObject) dataArrayObject.get(i);
			            
		            String buildingCode=dataObject.get("BuildingCode").toString();
		            String clientCode=dataObject.get("ClientCode").toString();
		            
		            deleteSql="delete from tblareawisedc "
						+ " where strBuildingCode='"+buildingCode+"' "
						+ " and strClientCode='"+clientCode+"'";
	                st.execute(deleteSql);
		            
				    String kilometers=dataObject.get("Kilometers").toString();
				    String symbol=dataObject.get("Symbol").toString();
				    String billAmt=dataObject.get("BillAmount").toString();
				    String billAmt1=dataObject.get("BillAmount1").toString();
				    String delCharges=dataObject.get("DeliveryCharges").toString();
				    String userCreated=dataObject.get("UserCreated").toString();
				    String userEdited=dataObject.get("UserEdited").toString();
				    String dateCreated=dataObject.get("DateCreated").toString();
				    String dateEdited=dataObject.get("DateEdited").toString();
				    String dataPostFlag=dataObject.get("DataPostFlag").toString();
					    
				    sql+=",('"+buildingCode+"','"+kilometers+"','"+symbol+"','"+billAmt+"'"
				    	+ ",'"+billAmt1+"','"+delCharges+"','"+userCreated+"','"+userEdited+"',"
				    	+ "'"+dateCreated+"','"+dateEdited+"','"+clientCode+"','"+dataPostFlag+"' )";
				    
				    flgData=true;
				}
							
				if(flgData)
				{
					sql=sql.substring(1,sql.length());
					insert_qry+=" "+sql;
					System.out.println(insert_qry);
					try
					{
						res=st.executeUpdate(insert_qry);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					res=1;
				}
				
			} catch(Exception e)
			{
				res=0;
				e.printStackTrace();
			}
			finally
	        {
	        	try {
					st.close();
					cmsCon.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return res;
	        }
		}
		
		
		
		@SuppressWarnings("finally")
		@GET
		@Path("/funGetSystemTime")
		@Produces(MediaType.APPLICATION_JSON)
	    public String funGetSystemTime()
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			Connection cmsCon=null;
			Statement st = null;
			String systemTime="No";
			JSONObject jObj=new JSONObject();
			
			try
			{
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
				st=cmsCon.createStatement();
				ResultSet rs=st.executeQuery("select sysdate()");
	            if(rs.next())
	            {
	                systemTime=rs.getString(1);
	            }
	            
	            jObj.put("SystemTime", systemTime);
	            
			} catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
	        {
				if(null!=cmsCon)
				{
					try {
						cmsCon.close();
					} catch (SQLException e) {						
						e.printStackTrace();
					}
				}
	            return jObj.toString();
	        }
		}
		
		
		
		private void funCreateTempFolder() {
	        System.out.println("In Create Temp Folder");
	        String filePath = System.getProperty("user.dir");
	        File Text_KOT = new File(filePath + "/Temp");
	        if (!Text_KOT.exists()) {
	        	Text_KOT.mkdirs();
	        }
	    }
	    
	    
	    
	    /**
	    *
	    * @param tableNo
	    * @param KOTNo
	    * @param billNo
	    * @param reprint
	    * @param type
	    */
	   private String funGenerateTextFileForKOT(String POSCode, String POSName, String tableNo, String KOTNo, String billNo, String reprint
			   , String type, String printYN,String deviceName,String macAddress,String fireCommunication,String strAreaWisePricing) 
	   {
           String result="";
		   clsDatabaseConnection objDb=new clsDatabaseConnection();
	       Connection aposCon=null;
	       Statement st=null,st2=null;
		   
	       try 
	       {
	    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
		       st = aposCon.createStatement();
		       st2 = aposCon.createStatement();

	           switch (type) 
	           {
	           		case "Dina":
	           			
	           				String strConsolidatePrint="";
	           				String strPrintType = "";
	                	   String AreaCodeForAll = "";
		                   String sql = "select strAreaCode from tblareamaster where strAreaName='All';";                   
		                   ResultSet rsAreaCode = st.executeQuery(sql);
		                   if (rsAreaCode.next()) {
		                       AreaCodeForAll = rsAreaCode.getString(1);
		                   }
		                   rsAreaCode.close();
		                   String filter="";
		                   if(fireCommunication.equalsIgnoreCase("Y")){
		                	   filter=" having sum(a.dblPrintQty)>0 ";   
		                   }
		                   
		                   sql="SELECT a.strItemName,d.strCostCenterCode,d.strPrimaryPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName,a.strNCKotYN, "
		                   		+ " ifnull(e.strLabelOnKOT,'KOT') strLabelOnKOT ,sum(a.dblPrintQty),e.intPrimaryPrinterNoOfCopies,e.intSecondaryPrinterNoOfCopies,e.strPrintOnBothPrinters FROM tblitemrtemp a "
		                   		+ " LEFT OUTER JOIN tblmenuitempricingdtl c ON a.strItemCode = c.strItemCode "
		                   		+ " left outer join tblprintersetup d on c.strCostCenterCode=d.strCostCenterCode "
		                   		+ " LEFT OUTER JOIN tblcostcentermaster e ON c.strCostCenterCode=e.strCostCenterCode "
		                   		+ " WHERE a.strKOTNo='"+KOTNo+"' AND a.strTableNo='"+tableNo+"' AND (c.strPOSCode='"+POSCode+"' OR c.strPOSCode='All') "
		                   		+ " AND (c.strAreaCode IN ( SELECT strAreaCode FROM tbltablemaster WHERE strTableNo='"+tableNo+"') ";
	                   			if(strAreaWisePricing.equalsIgnoreCase("N")){
	                   				sql+= " OR c.strAreaCode ='"+AreaCodeForAll+"' ";
	                   			}
		                   		
	                   			sql+= ") group by d.strCostCenterCode ";
		                   
		                   sql+=filter;
		                   System.out.println(sql);
		                   ResultSet rsPrint = st.executeQuery(sql);
		                    
		                   while (rsPrint.next()) 
		                   {
		                	   
		                	sql="select a.strPrintType, a.strConsolidatedKOTPrinterPort from tblsetup a where a.strPOSCode='"+POSCode+"'  or a.strPOSCode='All' ";
		                   	ResultSet rsPrinterType=st2.executeQuery(sql);
		   			    	if(rsPrinterType.next())
		   			    	{
		   			    		strConsolidatePrint=rsPrinterType.getString(2);
		   			    		strPrintType = rsPrinterType.getString(1);
		   			    		if(rsPrinterType.getString(1).equalsIgnoreCase("Jasper")){
		   			    			int noOfCopiesPrimaryPrinter=rsPrint.getInt(9);
		   			    			int noOfCopiesSecPrinter=rsPrint.getInt(10);
		   			    			if(rsPrint.getString(11).equalsIgnoreCase("Y")){// both printer
		   			    				if(noOfCopiesSecPrinter>0){
		   			    					// single print on secondary
		   			    					// All copies on Secondary printer is duplicate - Reprint 
		   			    					/*objKOTJasperFileGenerationForMakeKOT.funGenerateJasperForTableWiseKOT(tableNo,
				   			    					rsPrint.getString(2), AreaCodeForAll, KOTNo, "Reprint", 
				   			    					"Not", rsPrint.getString(4), rsPrint.getString(5), printYN, rsPrint.getString(6), rsPrint.getString(7),
				   			    					POSName,POSCode,rsPrint.getInt(9),rsPrint.getInt(10),rsPrint.getString(11),deviceName, macAddress);
		   			    					*/
		   			    					for(int k=0;k<noOfCopiesSecPrinter;k++){
		   			    						// remaining reprint on secondary
		   			    						objKOTJasperFileGenerationForMakeKOT.funGenerateJasperForTableWiseKOT(tableNo,
					   			    					rsPrint.getString(2), AreaCodeForAll, KOTNo, "Reprint", 
					   			    					"Not", rsPrint.getString(4), rsPrint.getString(5), printYN, rsPrint.getString(6), rsPrint.getString(7),
					   			    					POSName,POSCode,rsPrint.getInt(9),rsPrint.getInt(10),rsPrint.getString(11),deviceName, macAddress);
		   			    					}
		   			    				}
		   			    			}
		   			    				
		   			    			//single print on Primary
	   			    				objKOTJasperFileGenerationForMakeKOT.funGenerateJasperForTableWiseKOT(tableNo,
		   			    					rsPrint.getString(2), AreaCodeForAll, KOTNo, reprint, 
		   			    					rsPrint.getString(3), "Not", rsPrint.getString(5), printYN, rsPrint.getString(6), rsPrint.getString(7),
		   			    					POSName,POSCode,rsPrint.getInt(9),rsPrint.getInt(10),rsPrint.getString(11),deviceName, macAddress);
	   			    				
		   			    				for(int k=0;k<noOfCopiesPrimaryPrinter-1;k++){
	   			    						// remaining reprint on Primary
	   			    						objKOTJasperFileGenerationForMakeKOT.funGenerateJasperForTableWiseKOT(tableNo,
				   			    					rsPrint.getString(2), AreaCodeForAll, KOTNo, "Reprint", 
				   			    					rsPrint.getString(3), "Not", rsPrint.getString(5), printYN, rsPrint.getString(6), rsPrint.getString(7),
				   			    					POSName,POSCode,rsPrint.getInt(9),rsPrint.getInt(10),rsPrint.getString(11),deviceName, macAddress);
	   			    					}
		   			    				
		   			    			
		   			    			
		   			    		}
		   			    		else{
		   			    		 result=objAPOSKOTPrint.funWriteKOTDetailsToTextFile(tableNo, rsPrint.getString(2), "", AreaCodeForAll, KOTNo, reprint,rsPrint.getString(3), rsPrint.getString(4)
			                    		   , rsPrint.getString(5),printYN,POSCode,POSName,rsPrint.getString(6),deviceName,macAddress,rsPrint.getString(7),fireCommunication,rsPrint.getInt(9),rsPrint.getInt(10),rsPrint.getString(11));
			                	  
		   			    		}
		   			    		
		   			    	}else{
		   			    	 result=objAPOSKOTPrint.funWriteKOTDetailsToTextFile(tableNo, rsPrint.getString(2), "", AreaCodeForAll, KOTNo, reprint,rsPrint.getString(3), rsPrint.getString(4)
		                    		   , rsPrint.getString(5),printYN,POSCode,POSName,rsPrint.getString(6),deviceName,macAddress,rsPrint.getString(7),fireCommunication,rsPrint.getInt(9),rsPrint.getInt(10),rsPrint.getString(11));
		                	  
		   			    	}
		                	  
		                   }
		                  
		                   rsPrint.close();
		                   if (!strConsolidatePrint.isEmpty())//print consolidated KOT only 
		                    {
		                	   if(strPrintType.equals("Jasper"))
		                		   objConsolidatedKOTJasperGenerationForMakeKOT.funConsolidatedKOTForMakeKOTJasperFileGeneration(tableNo, POSCode, POSName, reprint, printYN, "Consolidated KOT", KOTNo, strConsolidatePrint);
		                	   else   
		                		 objAPOSKOTPrint.funConsolidateKOTDetailsToTextFile(tableNo,POSCode,POSName,reprint,printYN,"Consolidated KOT",KOTNo,deviceName) ;
		                    }
		                  
	                   
		                             // rsPrint.close();
	                   break;

	                   /*
	               case "DirectBiller":

	                   sql = "select a.strItemName,c.strCostCenterCode,c.strPrinterPort"
	                       + ",c.strSecondaryPrinterPort,c.strCostCenterName "
	                       + " from tblbilldtl  a,tblmenuitempricingdtl b,tblcostcentermaster c "
	                       + " where a.strBillNo='"+billNo+"' and  a.strItemCode=b.strItemCode "
	                       + " and b.strCostCenterCode=c.strCostCenterCode and b.strPosCode='"+POSCode+"' "
	                       + " group by c.strCostCenterCode;";
	                   ResultSet rsPrintDirect = st.executeQuery(sql);
	                   while (rsPrintDirect.next()) {
	                       funGenerateTextFileForKOTDirectBiller(rsPrintDirect.getString(2), "", clsGlobalVarClass.gDirectAreaCode, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5));
	                       //fun_textFilePrintingKOT("KOT", "Dina", tableNo, rsPrintDirect.getString(2), "", AreaCode, KOTNo, rsPrintDirect.getString(3), rsPrintDirect.getString(4), reprint);
	                   }
	                   rsPrintDirect.close();
	                   st.close();
	                   break;*/
	           }
	          
	           aposCon.close();
	           st.close();
	           st2.close();
	           
	           
	       } catch (Exception e) 
	       {
	           e.printStackTrace();
	       }
	       return result;
	   }
	   
	
	   
	   @GET
	   @Path("/funGenerateBillTextFile")
	   @Produces(MediaType.APPLICATION_JSON)
	   public Response funGenerateBillTextFile(@QueryParam("BillNo") String billNo,@QueryParam("PosCode") String posCode,
			   @QueryParam("ClientCode") String clientCode,@QueryParam("reprint")String reprint,@QueryParam("strServerBillPrinterName") String strServerBillPrinterName)
	   {
		   System.out.println("Generate Bill Text File "+billNo+"\tReprint = "+reprint+"\tServerBillPrinterName = "+strServerBillPrinterName);
		   JSONObject jsonOb=new JSONObject();
		   Response.ResponseBuilder response =    Response.ok();
		   jsonOb=funCreateBillTextFile(response,billNo,posCode,clientCode,reprint,strServerBillPrinterName);
		   return Response.status(200).entity(jsonOb).build();
	   }
		
	   
	   private JSONObject funCreateBillTextFile(ResponseBuilder resp,String billNo,String posCode,String clientCode,String reprint,String strServerBillPrinterName)
	   {
		   JSONObject jOb=new JSONObject();
		   String status="false";
		   try 
	       {
			   //clsTextFileGenerator file=new clsTextFileGenerator();
			   obTextFileGenerator.funGenerateAndPrintBill(resp,billNo, posCode, clientCode,reprint,strServerBillPrinterName);
			   status="true";
			  
	       } catch (Exception e) 
	       {
	           e.printStackTrace();
	       }
		   
		   try {
				jOb.put("Status", status);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
	       return jOb;//"Status";
	   }
	   
	   @GET
	   @Path("/funGenerateDirectBillerKOTTextFile")
	   @Produces(MediaType.APPLICATION_JSON)
	   public JSONObject funGenerateDirectBillerKOTTextFile(@QueryParam("POSCode") String POSCode,@QueryParam("BillNo") String BillNo,@QueryParam("AreaCode") String areaCode,@QueryParam("reprint") String reprint)
	   {
		   return funCreateDirectBillerKOTTextFile(POSCode,BillNo,areaCode,reprint);
	   }
	   
	   private JSONObject funCreateDirectBillerKOTTextFile(String POSCode,String BillNo,String areaCode,String reprint)
	   {
		   clsDatabaseConnection objDb=new clsDatabaseConnection();
	       Connection aposCon=null;
	       Statement st=null;
		   JSONObject jOb=new JSONObject();
		   String status="status";
		   String strPrintType="",strConsolidatePrint="";
		   try 
	       {
			   aposCon=objDb.funOpenAPOSCon("mysql","master");
		       st = aposCon.createStatement();
			   String sql_Print="select a.strPrintType, a.strConsolidatedKOTPrinterPort from tblsetup a where a.strPOSCode='"+POSCode+"'  or a.strPOSCode='All' ";
               ResultSet rsPrinterType=st.executeQuery(sql_Print);
               if(rsPrinterType.next())
               {
             	  strPrintType = rsPrinterType.getString(1);
             	  strConsolidatePrint=rsPrinterType.getString(2);
               }
               rsPrinterType.close();
               
               if(strPrintType.equals("Jasper"))
        		   objConsolidatedKOTJasperGenerationForDirectBiller.funConsolidatedKOTForDirectBillerJasperFileGeneration(areaCode, BillNo, reprint, POSCode,strConsolidatePrint);
               else
               objAPOSKOTPrint.funGenerateTextFileForKOTForDirectBiller(POSCode,areaCode,BillNo,reprint,"Y") ;
			   
               jOb.put("result", status);
               aposCon.close();
               st.close();
	       } catch (Exception e) 
	       {
		    	   try {
					jOb.put("result", "fail");
		    	   	} catch (JSONException e1) {
						// TODO Auto-generated catch block
		    		   	e1.printStackTrace();
				
				}
	           e.printStackTrace();
	       }
		   
	       return jOb;
	   }
	   
		@GET
		@Path("/funSalesReport")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONArray funSalesReport(@QueryParam("POSCode") String posCode,@QueryParam("UserCode") String userCode,@QueryParam("FromDate") String fromDate
	    		, @QueryParam("ToDate") String toDate,@QueryParam("ReportType") String reportType)
		{

			return funGetSalesReportData(posCode,userCode,fromDate, toDate,reportType);
			
		}
		
		private JSONArray funGetSalesReportData(String posCode,String userCode,String fromDate, String toDate,String reportType)
		{
			JSONArray jsArrReportDetails=new JSONArray();
			switch(reportType)
			{
				case "SettlementWiseSales":
					jsArrReportDetails=funGetSettelmentReportData(posCode,userCode,fromDate, toDate,reportType);
				break;
					
				case "BillWiseSales":
					jsArrReportDetails=funGetBillwiseSalesReport(posCode,userCode,fromDate, toDate,reportType);
	                break;
	                
	            case "ItemWiseSales":
	            	jsArrReportDetails=funGetItemwiseReportData(posCode,userCode,fromDate, toDate,reportType);
	              
	                break;
	                
	            case "MenuWiseSales":
	            	jsArrReportDetails=funGetMenuwiseReportData(posCode,userCode,fromDate, toDate,reportType);
	              
	                break;
			}
			
			return jsArrReportDetails;
		}
		
		private JSONArray funGetSettelmentReportData(String posCode,String userCode,String fromDate, String toDate,String reportType) 
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			Statement st1 = null;
			StringBuilder sbLive=new StringBuilder();
	        StringBuilder sbQFile=new StringBuilder();
	        StringBuilder sbFilters=new StringBuilder();
			JSONObject jObj = new JSONObject();
			JSONArray arrSettelmentwiseObj = new JSONArray();
			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				st1 = cmsCon.createStatement();
				sbLive.setLength(0);
	            sbQFile.setLength(0);
	            sbFilters.setLength(0);
	           
				
				sbLive.append("SELECT d.strPOSCode,b.strSettelmentCode, IFNULL(d.strPOSName,'') AS strPOSName, IFNULL(b.strSettelmentDesc,'') AS strSettelmentDesc "
                        + " , IFNULL(SUM(a.dblSettlementAmt),0.00) AS dblSettlementAmt,'" + userCode + "'"
                        + " ,b.strSettelmentType "
                        + " from "
                        + " tblbillsettlementdtl a "
                        + " LEFT OUTER JOIN tblsettelmenthd b ON a.strSettlementCode=b.strSettelmentCode "
                        + " LEFT OUTER JOIN tblbillhd c on a.strBillNo=c.strBillNo and date(c.dteBillDate)=date(a.dteBillDate) and a.strClientCode=c.strClientCode "
                        + " LEFT OUTER JOIN tblposmaster d on c.strPOSCode=d.strPosCode "
                        + " WHERE date(a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                        + "AND a.dblSettlementAmt>0 ");
				
				 sbQFile.append("SELECT d.strPOSCode,b.strSettelmentCode, IFNULL(d.strPOSName,'') AS strPOSName, IFNULL(b.strSettelmentDesc,'') AS strSettelmentDesc "
	                        + " ,IFNULL(SUM(a.dblSettlementAmt),0.00) AS dblSettlementAmt,'" + userCode + "' "
	                        + " ,b.strSettelmentType "
	                        + " from "
	                        + " tblqbillsettlementdtl a "
	                        + " LEFT OUTER JOIN tblsettelmenthd b ON a.strSettlementCode=b.strSettelmentCode "
	                        + " LEFT OUTER JOIN tblqbillhd c on a.strBillNo=c.strBillNo  and date(c.dteBillDate)=date(a.dteBillDate)  and a.strClientCode=c.strClientCode "
	                        + " LEFT OUTER JOIN tblposmaster d on c.strPOSCode=d.strPosCode "
	                        + " WHERE  date(a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                        + " AND a.dblSettlementAmt>0 ");
				 
				 
				 if (!posCode.equals("All") )
	             {
	                 sbFilters.append(" AND d.strPOSCode = '" + posCode + "' ");                    
	             }
				 
	             sbFilters.append(" GROUP BY b.strSettelmentDesc, d.strPosCode");
	             sbLive.append(" ").append(sbFilters);
	             sbQFile.append(" ").append(sbFilters);
	             
	             System.out.println("SQL= "+sbQFile);
	             System.out.println("SQL= "+sbLive);
	             
	             
	             mapPOSDtlForSettlement = new LinkedHashMap<String, List<Map<String, clsBillSettlementDtl>>>();
	             //for live data
	             ResultSet liveResultSet = st.executeQuery(sbLive.toString()); 
	             funGenerateSettlementWiseSales(liveResultSet);
	             //for Q data
	             ResultSet qResultSet = st1.executeQuery(sbQFile.toString()); 
	            funGenerateSettlementWiseSales(qResultSet);
	             
	             
	            Iterator<Map.Entry<String, List<Map<String, clsBillSettlementDtl>>>> it = mapPOSDtlForSettlement.entrySet().iterator();
                while (it.hasNext())
                {
                    Map.Entry<String, List<Map<String, clsBillSettlementDtl>>> entry = it.next();
                    List<Map<String, clsBillSettlementDtl>> listOfSettelment = entry.getValue();
                    for (int i = 0; i < listOfSettelment.size(); i++)
                    {
                        clsBillSettlementDtl objSettlementDtl = listOfSettelment.get(i).entrySet().iterator().next().getValue();
                        JSONObject objSettle = new JSONObject();
						objSettle.put("posName", objSettlementDtl.getPosName());
						objSettle.put("payMode",objSettlementDtl.getStrSettlementName());
						objSettle.put("dblSettleAmt", objSettlementDtl.getDblSettlementAmt());
						arrSettelmentwiseObj.put(objSettle);
                    }
                }
				jObj.put("settelementReportDtl", arrSettelmentwiseObj);
	           
				st.close();
				st1.close();
				cmsCon.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return arrSettelmentwiseObj;
		}
		
		
		
		 private void funGenerateSettlementWiseSales(ResultSet resultSet)
		    {
			  try
		        {
		            while (resultSet.next())
		            {
		                String posCode = resultSet.getString("strPOSCode");
		                String posName = resultSet.getString("strPOSName");
		                String settlementCode = resultSet.getString("strSettelmentCode");
		                String settlementDesc = resultSet.getString("strSettelmentDesc");
		                double settlementAmt = resultSet.getDouble("dblSettlementAmt");
		                String settlementType = resultSet.getString("strSettelmentType");

		                if (mapPOSDtlForSettlement.containsKey(posCode))
		                {
		                    List<Map<String, clsBillSettlementDtl>> listOfSettlement = mapPOSDtlForSettlement.get(posCode);
		                    boolean isSettlementExists = false;
		                    int settlementIndex = 0;
		                    for (int i = 0; i < listOfSettlement.size(); i++)
		                    {
		                        if (listOfSettlement.get(i).containsKey(settlementCode))
		                        {
		                            isSettlementExists = true;
		                            settlementIndex = i;
		                            break;
		                        }
		                    }
		                    if (isSettlementExists)
		                    {
		                        Map<String, clsBillSettlementDtl> mapSettlementCodeDtl = listOfSettlement.get(settlementIndex);
		                        clsBillSettlementDtl objBillSettlementDtl = mapSettlementCodeDtl.get(settlementCode);
		                        objBillSettlementDtl.setStrSettlementCode(settlementCode);
		                        objBillSettlementDtl.setDblSettlementAmt(objBillSettlementDtl.getDblSettlementAmt() + settlementAmt);
		                        objBillSettlementDtl.setPosName(posName);
		                    }
		                    else
		                    {
		                        Map<String, clsBillSettlementDtl> mapSettlementCodeDtl = new LinkedHashMap<>();
		                        clsBillSettlementDtl objBillSettlementDtl = new clsBillSettlementDtl(settlementCode, settlementDesc, settlementAmt, posName, settlementType);
		                        mapSettlementCodeDtl.put(settlementCode, objBillSettlementDtl);
		                        listOfSettlement.add(mapSettlementCodeDtl);
		                    }
		                }
		                else
		                {
		                    List<Map<String, clsBillSettlementDtl>> listOfSettelment = new ArrayList<>();
		                    Map<String, clsBillSettlementDtl> mapSettlementCodeDtl = new LinkedHashMap<>();
		                    clsBillSettlementDtl objBillSettlementDtl = new clsBillSettlementDtl(settlementCode, settlementDesc, settlementAmt, posName, settlementType);
		                    mapSettlementCodeDtl.put(settlementCode, objBillSettlementDtl);
		                    listOfSettelment.add(mapSettlementCodeDtl);
		                    mapPOSDtlForSettlement.put(posCode, listOfSettelment);
		                }
		            }
		            
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		    }
	
	    public JSONArray funGetBillwiseSalesReport(String posCode,String userCode,String fromDate
	    		, String toDate, String reportType)
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			Statement st1 = null;
			StringBuilder sbSqlBillWiseLive = new StringBuilder();
		    StringBuilder sbSqlBillWiseQFile = new StringBuilder();
		    StringBuilder sbFilters=new StringBuilder();
			JSONObject jObj = new JSONObject();
			JSONArray arrBillwiseObj = new JSONArray();

			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				st1 = cmsCon.createStatement();
				sbSqlBillWiseLive.setLength(0);
				sbSqlBillWiseQFile.setLength(0);
				sbFilters.setLength(0);
	           	
				sbSqlBillWiseLive.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime "
	                    + " ,ifnull(b.strTableName,'') as TableName,f.strPOSName, ifnull(d.strSettelmentDesc,'') as payMode "
	                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt "
	                    + " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated "
	                    + " ,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strWaiterNo "
	                    + " ,a.strCustomerCode,a.dblDeliveryCharges,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA') "
	                    + " ,a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA'),intShiftCode"
	                    + " ,dblRoundOff,intBillSeriesPaxNo "
	                    + " from tblbillhd  a "
	                    + " left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
	                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
	                    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode  and date(a.dteBillDate)=date(c.dteBillDate)  "
	                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
	                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
	                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + "  ");
				
				
	 
				sbSqlBillWiseQFile.append("select a.strBillNo,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime"
	                    + " ,ifnull(b.strTableName,'') as TableName,f.strPOSName"
	                    + ""
	                    + ", ifnull(d.strSettelmentDesc,'') as payMode"
	                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
	                    + " ,ifnull(c.dblSettlementAmt,0.00),a.strUserCreated,a.strUserEdited,a.dteDateCreated"
	                    + " ,a.dteDateEdited,a.strClientCode,a.strWaiterNo,a.strCustomerCode,a.dblDeliveryCharges"
	                    + " ,ifnull(c.strRemark,''),ifnull(e.strCustomerName ,'NA')"
	                    + " ,a.dblTipAmount,'" + userCode + "',a.strDiscountRemark,ifnull(h.strReasonName ,'NA'),intShiftCode"
	                    + " ,dblRoundOff,a.intBillSeriesPaxNo "
	                    + " from tblqbillhd a left outer join  tbltablemaster b on a.strTableNo=b.strTableNo "
	                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
	                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode   and date(a.dteBillDate)=date(c.dteBillDate)  "
	                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
	                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
	                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
	                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
	                    + "  ");
				 
				 if (!posCode.equals("All") )
	             {
	                 sbFilters.append(" AND a.strPOSCode = '" + posCode + "' ");                    
	             }
				 
				 sbFilters.append(" order by date(a.dteBillDate),a.strBillNo,a.dteDateCreated asc");

	             System.out.println(sbFilters);
	             sbSqlBillWiseLive.append(" ").append(sbFilters);
	             sbSqlBillWiseQFile.append(" ").append(sbFilters);
	             
	             
	             
	             double totalDiscAmt = 0, totalSubTotal = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0, totalRoundOffAmt = 0;
	             boolean flgRecords = false;
	             int totalPAX = 0;

	             Map<String, List<clsSalesFlashColumns>> hmBillWiseSales = new HashMap<String, List<clsSalesFlashColumns>>();
	             int seqNo = 1;
	             //for live data
	             ResultSet rsBillWiseSales= st.executeQuery(sbSqlBillWiseLive.toString());
	             while (rsBillWiseSales.next())
	             {
	                 List<clsSalesFlashColumns> arrListBillWiseSales = new ArrayList<clsSalesFlashColumns>();
	                 flgRecords = true;
	                 String[] spDate = rsBillWiseSales.getString(2).split("-");
	                 String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];//billDate

	                 clsSalesFlashColumns objSalesFlashColumns = new clsSalesFlashColumns();
	                 objSalesFlashColumns.setStrField1(rsBillWiseSales.getString(1));
	                 objSalesFlashColumns.setStrField2(billDate);
	                 objSalesFlashColumns.setStrField3(rsBillWiseSales.getString(3));
	                 objSalesFlashColumns.setStrField4(rsBillWiseSales.getString(4));
	                 objSalesFlashColumns.setStrField5(rsBillWiseSales.getString(21));//Cust Name
	                 objSalesFlashColumns.setStrField6(rsBillWiseSales.getString(5));
	                 objSalesFlashColumns.setStrField7(rsBillWiseSales.getString(6));
	                 objSalesFlashColumns.setStrField8(rsBillWiseSales.getString(19));
	                 objSalesFlashColumns.setStrField9(rsBillWiseSales.getString(7));
	                 objSalesFlashColumns.setStrField10(rsBillWiseSales.getString(8));
	                 objSalesFlashColumns.setStrField11(rsBillWiseSales.getString(9));
	                 objSalesFlashColumns.setStrField12(rsBillWiseSales.getString(10));
	                 objSalesFlashColumns.setStrField13(rsBillWiseSales.getString(11));
	                 objSalesFlashColumns.setStrField14(rsBillWiseSales.getString(20));
	                 objSalesFlashColumns.setStrField15(rsBillWiseSales.getString(22));
	                 objSalesFlashColumns.setStrField16(rsBillWiseSales.getString(24));
	                 objSalesFlashColumns.setStrField17(rsBillWiseSales.getString(25));
	                 objSalesFlashColumns.setStrField18(rsBillWiseSales.getString(26));//shift
	                 objSalesFlashColumns.setStrField19(rsBillWiseSales.getString(27));//roundOff
	                 objSalesFlashColumns.setStrField20(rsBillWiseSales.getString(28));//intBillSeriesPaxNo

	                 objSalesFlashColumns.setSeqNo(seqNo++);

	                 if (null != hmBillWiseSales.get(rsBillWiseSales.getString(1) + "!" + billDate))
	                 {
	                     arrListBillWiseSales = hmBillWiseSales.get(rsBillWiseSales.getString(1) + "!" + billDate);
	                     objSalesFlashColumns.setStrField9("0");
	                     objSalesFlashColumns.setStrField10("0");
	                     objSalesFlashColumns.setStrField11("0");
	                     objSalesFlashColumns.setStrField12("0");
	                     objSalesFlashColumns.setStrField15("0");
	                     objSalesFlashColumns.setStrField19("0");//roundoff
	                     objSalesFlashColumns.setStrField20("0");//intBillSeriesPaxNo
	                 }
	                 arrListBillWiseSales.add(objSalesFlashColumns);
	                 hmBillWiseSales.put(rsBillWiseSales.getString(1) + "!" + billDate, arrListBillWiseSales);

	             }
	             rsBillWiseSales.close();

	             //for qfile data
	             rsBillWiseSales= st1.executeQuery(sbSqlBillWiseQFile.toString());
	             while (rsBillWiseSales.next())
	             {
	                 List<clsSalesFlashColumns> arrListBillWiseSales = new ArrayList<clsSalesFlashColumns>();
	                 flgRecords = true;

	                 String[] spDate = rsBillWiseSales.getString(2).split("-");
	                 String billDate = spDate[2] + "-" + spDate[1] + "-" + spDate[0];//billDate

	                 clsSalesFlashColumns objSalesFlashColumns = new clsSalesFlashColumns();
	                 objSalesFlashColumns.setStrField1(rsBillWiseSales.getString(1));
	                 objSalesFlashColumns.setStrField2(billDate);
	                 objSalesFlashColumns.setStrField3(rsBillWiseSales.getString(3));
	                 objSalesFlashColumns.setStrField4(rsBillWiseSales.getString(4));
	                 objSalesFlashColumns.setStrField5(rsBillWiseSales.getString(21));//Cust Name}
	                 objSalesFlashColumns.setStrField6(rsBillWiseSales.getString(5));
	                 objSalesFlashColumns.setStrField7(rsBillWiseSales.getString(6));
	                 objSalesFlashColumns.setStrField8(rsBillWiseSales.getString(19));
	                 objSalesFlashColumns.setStrField9(rsBillWiseSales.getString(7));
	                 objSalesFlashColumns.setStrField10(rsBillWiseSales.getString(8));
	                 objSalesFlashColumns.setStrField11(rsBillWiseSales.getString(9));
	                 objSalesFlashColumns.setStrField12(rsBillWiseSales.getString(10));
	                 objSalesFlashColumns.setStrField13(rsBillWiseSales.getString(11));
	                 objSalesFlashColumns.setStrField14(rsBillWiseSales.getString(20));
	                 objSalesFlashColumns.setStrField15(rsBillWiseSales.getString(22));
	                 objSalesFlashColumns.setStrField16(rsBillWiseSales.getString(24));
	                 objSalesFlashColumns.setStrField17(rsBillWiseSales.getString(25));
	                 objSalesFlashColumns.setStrField18(rsBillWiseSales.getString(26));//shift
	                 objSalesFlashColumns.setStrField19(rsBillWiseSales.getString(27));//roundOff
	                 objSalesFlashColumns.setStrField20(rsBillWiseSales.getString(28));//intBillSeriesPaxNo

	                 objSalesFlashColumns.setSeqNo(seqNo++);

	                 if (null != hmBillWiseSales.get(rsBillWiseSales.getString(1) + "!" + billDate))
	                 {
	                     arrListBillWiseSales = hmBillWiseSales.get(rsBillWiseSales.getString(1) + "!" + billDate);
	                     objSalesFlashColumns.setStrField9("0");
	                     objSalesFlashColumns.setStrField10("0");
	                     objSalesFlashColumns.setStrField11("0");
	                     objSalesFlashColumns.setStrField12("0");
	                     objSalesFlashColumns.setStrField15("0");
	                     objSalesFlashColumns.setStrField19("0");//roundoff
	                     objSalesFlashColumns.setStrField20("0");//intBillSeriesPaxNo
	                 }
	                 arrListBillWiseSales.add(objSalesFlashColumns);
	                 hmBillWiseSales.put(rsBillWiseSales.getString(1) + "!" + billDate, arrListBillWiseSales);
	             }
	             rsBillWiseSales.close();


	             List<clsSalesFlashColumns> arrTempListBillWiseSales = new ArrayList<clsSalesFlashColumns>();
	             for (Map.Entry<String, List<clsSalesFlashColumns>> entry : hmBillWiseSales.entrySet())
	             {
	                 for (clsSalesFlashColumns objSalesFlashColumns : entry.getValue())
	                 {
	                     clsSalesFlashColumns objTempSalesFlashColumns = new clsSalesFlashColumns();
	                     objTempSalesFlashColumns.setStrField1(objSalesFlashColumns.getStrField1());
	                     objTempSalesFlashColumns.setStrField2(objSalesFlashColumns.getStrField2());
	                     objTempSalesFlashColumns.setStrField3(objSalesFlashColumns.getStrField3());
	                     objTempSalesFlashColumns.setStrField4(objSalesFlashColumns.getStrField4());
	                     objTempSalesFlashColumns.setStrField5(objSalesFlashColumns.getStrField5());
	                     objTempSalesFlashColumns.setStrField6(objSalesFlashColumns.getStrField6());
	                     objTempSalesFlashColumns.setStrField7(objSalesFlashColumns.getStrField7());
	                     objTempSalesFlashColumns.setStrField8(objSalesFlashColumns.getStrField8());
	                     objTempSalesFlashColumns.setStrField9(objSalesFlashColumns.getStrField9());
	                     objTempSalesFlashColumns.setStrField10(objSalesFlashColumns.getStrField10());
	                     objTempSalesFlashColumns.setStrField11(objSalesFlashColumns.getStrField11());
	                     objTempSalesFlashColumns.setStrField12(objSalesFlashColumns.getStrField12());
	                     objTempSalesFlashColumns.setStrField13(objSalesFlashColumns.getStrField13());
	                     objTempSalesFlashColumns.setStrField14(objSalesFlashColumns.getStrField14());
	                     objTempSalesFlashColumns.setStrField15(objSalesFlashColumns.getStrField15());
	                     objTempSalesFlashColumns.setStrField16(objSalesFlashColumns.getStrField16());
	                     objTempSalesFlashColumns.setStrField17(objSalesFlashColumns.getStrField17());
	                     objTempSalesFlashColumns.setStrField18(objSalesFlashColumns.getStrField18());
	                     objTempSalesFlashColumns.setStrField19(objSalesFlashColumns.getStrField19());
	                     objTempSalesFlashColumns.setStrField20(objSalesFlashColumns.getStrField20());

	                     objTempSalesFlashColumns.setSeqNo(objSalesFlashColumns.getSeqNo());

	                     arrTempListBillWiseSales.add(objTempSalesFlashColumns);
	                 }
	             }

	             //sort arrTempListBillWiseSales 
	            // Collections.sort(arrTempListBillWiseSales, COMPARATOR);
	             Collections.sort(arrTempListBillWiseSales, new Comparator<clsSalesFlashColumns>()
	             {
            	 	public int compare(clsSalesFlashColumns o1, clsSalesFlashColumns o2)
     	        	{
	     	            return (int) (o1.getStrField2().compareTo(o2.getStrField2()));
     	        	}
	             });
	             
	             Collections.sort(arrTempListBillWiseSales, new Comparator<clsSalesFlashColumns>()
	             {
            	 	public int compare(clsSalesFlashColumns o1, clsSalesFlashColumns o2)
     	        	{
	     	            return (int) (o1.getStrField1().compareTo(o2.getStrField1()));
     	        	}
	             });
	    			
	    	             
	             for (clsSalesFlashColumns objSalesFlashColumns : arrTempListBillWiseSales)
	             {
	            	JSONObject objBill= new JSONObject();
					objBill.put("strbillno", objSalesFlashColumns.getStrField1());
					objBill.put("dtebilldate",objSalesFlashColumns.getStrField2());
					objBill.put("billtime", objSalesFlashColumns.getStrField3());
					objBill.put("tableName",objSalesFlashColumns.getStrField4());
					objBill.put("posName", objSalesFlashColumns.getStrField6());
					objBill.put("payMode", objSalesFlashColumns.getStrField7());
					objBill.put("Delivery Charge",objSalesFlashColumns.getStrField8());
					objBill.put("subTotal",objSalesFlashColumns.getStrField9());
					objBill.put("discountPer", objSalesFlashColumns.getStrField10());
					objBill.put("discountAmt", objSalesFlashColumns.getStrField11());
					objBill.put("taxAmt", objSalesFlashColumns.getStrField12());
					objBill.put("grandTotal", objSalesFlashColumns.getStrField13());
					if(objSalesFlashColumns.getStrField14().isEmpty())
			        {
						objBill.put("remarks","");
			        }
			        else
			        {
			        	objBill.put("remarks",objSalesFlashColumns.getStrField14());
			        }
					objBill.put("Tip", objSalesFlashColumns.getStrField15());
					if(objSalesFlashColumns.getStrField16().isEmpty())
			        {
						objBill.put("Disc Remarks","");
			        }
			        else
			        {
			        	objBill.put("Disc Remarks",objSalesFlashColumns.getStrField16());
			        }
					objBill.put("CustName", objSalesFlashColumns.getStrField5());
					
					arrBillwiseObj.put(objBill);
	             }
	           
				st.close();
				st1.close();
				cmsCon.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return arrBillwiseObj;
		}
	 
		
		
		
		/*private static Comparator<clsSalesFlashColumns> COMPARATOR = new Comparator<clsSalesFlashColumns>()
	    {
	        // This is where the sorting happens.
	        public int compare(clsSalesFlashColumns o1, clsSalesFlashColumns o2)
	        {
	            return (int) (o2.getSeqNo() - o1.getSeqNo());
	        }
	    };*/
		
		
		
	    private JSONArray funGetItemwiseReportData(String posCode,String userCode,String fromDate, String toDate,String reportType) 
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			Statement st1 = null;
			JSONObject jObj = new JSONObject();
			JSONArray arrItemwiseObj = new JSONArray();
			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				st1 = cmsCon.createStatement();
				String sqlFilters = "";
	           
				
				 String sqlLive = "select a.strItemCode,a.strItemName,c.strPOSName"
	                        + ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
	                        + ",sum(a.dblAmount)-sum(a.dblDiscountAmt),'" + userCode + "' "
	                        + ",sum(a.dblAmount),sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
	                        + "from tblbilldtl a,tblbillhd b,tblposmaster c\n"
	                        + "where a.strBillNo=b.strBillNo "
	                        + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate)  "
	                        + "and b.strPOSCode=c.strPosCode "
	                        + "and a.strClientCode=b.strClientCode "
	                        + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ";		
				
				 String sqlQFile = "select a.strItemCode,a.strItemName,c.strPOSName"
	                        + ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
	                        + ",sum(a.dblAmount)-sum(a.dblDiscountAmt),'" + userCode + "' "
	                        + ",sum(a.dblAmount),sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
	                        + "from tblqbilldtl a,tblqbillhd b,tblposmaster c\n"
	                        + "where a.strBillNo=b.strBillNo "
	                        + "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
	                        + "and b.strPOSCode=c.strPosCode "
	                        + "and a.strClientCode=b.strClientCode "
	                        + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ";
				 
				 if (!posCode.equals("All") )
	             {
					 sqlFilters += " AND b.strPOSCode = '" + posCode + "' ";                   
	             }
				 
				 sqlFilters += " group by a.strItemCode,c.strPOSName "
	                        + " order by b.dteBillDate ";
				 
				 sqlLive = sqlLive + " " + sqlFilters;
	             sqlQFile = sqlQFile + " " + sqlFilters;
	             mapPOSItemDtl = new LinkedHashMap<>();
	             //for item live
                ResultSet rsItemWiseSales =st.executeQuery(sqlLive); 
                funGenerateItemWiseSales(rsItemWiseSales,userCode,fromDate,toDate);
                
                rsItemWiseSales = st1.executeQuery(sqlQFile);
                funGenerateItemWiseSales(rsItemWiseSales,userCode,fromDate,toDate);
                
                Set<Entry<String, Map<String, clsBillItemDtl>>> set = mapPOSItemDtl.entrySet();
                List<Entry<String, Map<String, clsBillItemDtl>>> list = new ArrayList<Entry<String, Map<String, clsBillItemDtl>>>(set);
                Collections.sort(list, new Comparator<Map.Entry<String, Map<String, clsBillItemDtl>>>()
                {
                    @Override
                    public int compare(Entry<String, Map<String, clsBillItemDtl>> o1, Entry<String, Map<String, clsBillItemDtl>> o2)
                    {

                        Iterator<Entry<String, clsBillItemDtl>> it1 = o1.getValue().entrySet().iterator();
                        Iterator<Entry<String, clsBillItemDtl>> it2 = o2.getValue().entrySet().iterator();

                        if (it1.hasNext())
                        {
                            if (it1.next().getValue().getItemCode().substring(0, 7).equalsIgnoreCase(it1.next().getValue().getItemCode().substring(0, 7)))
                            {
                                return 0;
                            }
                            else
                            {
                                return 1;
                            }
                        }
                        return 0;
                    }

                });

                Iterator<Map.Entry<String, Map<String, clsBillItemDtl>>> posIterator = mapPOSItemDtl.entrySet().iterator();
                while (posIterator.hasNext())
                {
                    Map<String, clsBillItemDtl> mapItemDtl = posIterator.next().getValue();
                    Iterator<Map.Entry<String, clsBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
                    while (itemIterator.hasNext())
                    {
                        clsBillItemDtl objBillItemDtl = itemIterator.next().getValue();
                        JSONObject objItem= new JSONObject();
                    	objItem.put("strItemName", objBillItemDtl.getItemName());
						objItem.put("strItemCode", objBillItemDtl.getItemCode());
						objItem.put("posName", objBillItemDtl.getPosName());
						objItem.put("strQuantity", objBillItemDtl.getQuantity());
						objItem.put("grandTotal",  objBillItemDtl.getAmount());
						objItem.put("subTotal", objBillItemDtl.getSubTotal());
						objItem.put("discountAmt", objBillItemDtl.getDiscountAmount());
						objItem.put("dtebilldate", objBillItemDtl.getBillDateTime());
						
						arrItemwiseObj.put(objItem);      
                    }
                }
	             
				st.close();
				st1.close();
				cmsCon.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return arrItemwiseObj;
		}
		
		
		
   private void funGenerateItemWiseSales(ResultSet rsItemWiseSales,String userCode,String fromDate,String toDate)
    {
        try
        {
            while (rsItemWiseSales.next())
            {
                String itemCode = rsItemWiseSales.getString(1);//itemCode
                String itemName = rsItemWiseSales.getString(2);//itemName
                String posName = rsItemWiseSales.getString(3);//posName
                double qty = rsItemWiseSales.getDouble(4);//qty
                double salesAmt = rsItemWiseSales.getDouble(8);//salesAmount
                double subTotal = rsItemWiseSales.getDouble(6);//sunTotal
                double discAmt = rsItemWiseSales.getDouble(9);//discount
                String date = rsItemWiseSales.getString(10);//date
                String posCode = rsItemWiseSales.getString(11);//posCode

                String compare = itemCode;
                if (itemCode.contains("M"))
                {
                    compare = itemName;
                }
                else
                {
                    compare = itemCode;
                }

                if (mapPOSItemDtl.containsKey(posCode))
                {
                    Map<String, clsBillItemDtl> mapItemDtl = mapPOSItemDtl.get(posCode);
                    if (mapItemDtl.containsKey(compare))
                    {
                        clsBillItemDtl objItemDtl = mapItemDtl.get(compare);
                        objItemDtl.setQuantity(objItemDtl.getQuantity() + qty);
                        objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
                        objItemDtl.setSubTotal(objItemDtl.getSubTotal() + subTotal);
                        objItemDtl.setDiscountAmount(objItemDtl.getDiscountAmount() + discAmt);
                    }
                    else
                    {
                        clsBillItemDtl objItemDtl = new clsBillItemDtl(date, itemCode, itemName, qty, salesAmt, discAmt, posName, subTotal);
                        mapItemDtl.put(compare, objItemDtl);
                    }
                }
                else
                {
                    Map<String, clsBillItemDtl> mapItemDtl = new LinkedHashMap<>();
                    clsBillItemDtl objItemDtl = new clsBillItemDtl(date, itemCode, itemName, qty, salesAmt, discAmt, posName, subTotal);
                    mapItemDtl.put(compare, objItemDtl);
                    mapPOSItemDtl.put(posCode, mapItemDtl);
                }

                if (!itemCode.contains("M"))
                {
                   // funCreateModifierQuery(itemCode,userCode,posCode,fromDate,toDate);
                }
            }
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
		   
   private void funCreateModifierQuery(String itemCode,String userCode,String posCode,String fromDate,String toDate)
    {
	   clsDatabaseConnection objDb = new clsDatabaseConnection();
	   Connection aposCon = null;
	   Statement st = null,st1=null;
	   
        try
        {
        	aposCon = objDb.funOpenAPOSCon("mysql", "master");
			st = aposCon.createStatement();
			st1 = aposCon.createStatement();
			
            String sqlModLive = "select a.strItemCode,a.strModifierName,c.strPOSName"
                    + ",sum(a.dblQuantity),'0.0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + userCode + "' "
                    + ",sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                    + "from tblbillmodifierdtl a,tblbillhd b,tblposmaster c\n"
                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  \n"
                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + "and left(a.strItemCode,7)='" + itemCode + "' ";

            
            String sqlFilters = "";
            if (!posCode.equals("All") )
             {
				 sqlFilters += " AND b.strPOSCode = '" + posCode + "' ";                   
             }
          
            sqlFilters += " group by a.strItemCode,a.strModifierName,c.strPOSName  "
                    + " order by b.dteBillDate ";

            sqlModLive = sqlModLive + " " + sqlFilters;

            ResultSet rs =  st.executeQuery(sqlModLive.toString()); 
            funGenerateItemWiseSales(rs,userCode,fromDate,toDate);

            //qmodifiers
            String sqlModQFile = "select a.strItemCode,a.strModifierName,c.strPOSName"
                    + ",sum(a.dblQuantity),'0.0',sum(a.dblAmount)-sum(a.dblDiscAmt),'" + userCode + "' "
                    + ",sum(a.dblAmount),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
                    + "from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c\n"
                    + "where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  \n"
                    + "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + "and left(a.strItemCode,7)='" + itemCode + "' ";

            sqlModQFile = sqlModQFile + " " + sqlFilters;

            rs = st1.executeQuery(sqlModQFile.toString());
            funGenerateItemWiseSales(rs,userCode,fromDate,toDate);
            
            aposCon.close();
            st.close();
            st1.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }		
		
   private JSONArray funGetMenuwiseReportData(String posCode,String userCode,String fromDate, String toDate,String reportType) 
	{
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null,st1 = null,st2 = null,st3 = null;
		StringBuilder sbSqlLive = new StringBuilder();
	    StringBuilder sbSqlQFile = new StringBuilder();
	    StringBuilder sbFilters=new StringBuilder();
	    StringBuilder sbSql = new StringBuilder();
		JSONObject jObj = new JSONObject();
		String areaWisePricing="N";
		JSONArray arrMenuwiseObj = new JSONArray();
		try {
			cmsCon = objDb.funOpenAPOSCon("mysql", "master");
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			st2 = cmsCon.createStatement();
			st3 = cmsCon.createStatement();
			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbFilters.setLength(0);
			sbSql.append("select a.strAreaWisePricing from tblsetup a  ");
			if (!posCode.equals("All") )
             {
				sbSql.append(" where a.strPOSCode='"+posCode+"'  ");                   
             }
			sbSql.append(" group by a.strClientCode  ");  
			ResultSet rsSql=st1.executeQuery(sbSql.toString());
			if(rsSql.next())
			{
				areaWisePricing=rsSql.getString(1);
			}
			rsSql.close();
			st1.close();
			
			
			sbSqlLive.append("SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
                    + " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + userCode + "',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
                    + " FROM tblbilldtl a\n"
                    + " left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) and a.strClientCode=b.strClientCode "
                    + " left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
                    + " and b.strposcode =d.strposcode ");
			if (areaWisePricing.equals("Y"))
            {
                sbSqlLive.append(" and b.strAreaCode= d.strAreaCode ");
            }
            sbSqlLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
            sbSqlLive.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			
			
            sbSqlQFile.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
                    + "sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + userCode + "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode  "
                    + "FROM tblqbilldtl a\n"
                    + "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) and a.strClientCode=b.strClientCode "
                    + "left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + "left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
                    + " and b.strposcode =d.strposcode ");
            if (areaWisePricing.equals("Y"))
            {
                sbSqlQFile.append(" and b.strAreaCode= d.strAreaCode ");
            }
            sbSqlQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
            sbSqlQFile.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");

			
            String sqlModLive = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
                    + "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + userCode + "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
                    + "FROM tblbillmodifierdtl a\n"
                    + "left outer join tblbillhd b on a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
                    + "left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
                    + " and b.strposcode =d.strposcode ";
                if (areaWisePricing.equals("Y"))
                {
                    sqlModLive += " and b.strAreaCode= d.strAreaCode ";
                }
            sqlModLive += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
            sqlModLive += " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 ";

            
            String sqlModQFile = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
                    + "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + userCode + "',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
                    + "FROM tblqbillmodifierdtl a\n"
                    + "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) and a.strClientCode=b.strClientCode "
                    + "left outer join tblposmaster f on b.strposcode=f.strposcode "
                    + "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
                    + " and b.strposcode =d.strposcode ";
                if (areaWisePricing.equals("Y"))
                {
                    sqlModQFile += " and b.strAreaCode= d.strAreaCode ";
                }
            sqlModQFile += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
            sqlModQFile += " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0  ";

     
			 
			 if (!posCode.equals("All") )
             {
                 sbFilters.append(" AND b.strPOSCode = '" + posCode + "' ");                    
             }
			 
			 sbFilters.append(" Group by b.strPoscode, d.strMenuCode,e.strMenuName");
			 sbFilters.append(" order by b.strPoscode, d.strMenuCode,e.strMenuName");
            
			 sbSqlLive.append(sbFilters);
             sbSqlQFile.append(sbFilters);

             sqlModLive = sqlModLive + " " + sbFilters.toString();
             sqlModQFile = sqlModQFile + " " + sbFilters.toString();
             
             
             mapPOSMenuHeadDtl = new LinkedHashMap<>();

             ResultSet rsMenuHeadWiseSales = st.executeQuery(sbSqlLive.toString());
             funGenerateMenuHeadWiseSales(rsMenuHeadWiseSales);
             rsMenuHeadWiseSales = st.executeQuery(sqlModLive);
             funGenerateMenuHeadWiseSales(rsMenuHeadWiseSales);
             rsMenuHeadWiseSales = st.executeQuery(sbSqlQFile.toString()); 
             funGenerateMenuHeadWiseSales(rsMenuHeadWiseSales);
             rsMenuHeadWiseSales = st.executeQuery(sqlModQFile); 
             funGenerateMenuHeadWiseSales(rsMenuHeadWiseSales);
             
             
             Iterator<Map.Entry<String, Map<String, clsBillItemDtl>>> posIterator = mapPOSMenuHeadDtl.entrySet().iterator();
             while (posIterator.hasNext())
             {
                 Map<String, clsBillItemDtl> mapItemDtl = posIterator.next().getValue();
                 Iterator<Map.Entry<String, clsBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
                 while (itemIterator.hasNext())
                 {
                     clsBillItemDtl objBillItemDtl = itemIterator.next().getValue();
                     JSONObject objMenu= new JSONObject();
					 objMenu.put("strMenuCode", objBillItemDtl.getMenuCode());
					 objMenu.put("strMenuName", objBillItemDtl.getMenuName());
					 objMenu.put("posName", objBillItemDtl.getPosName());
					 objMenu.put("strQuantity", objBillItemDtl.getQuantity());
					 objMenu.put("grandTotal", objBillItemDtl.getAmount());
					 objMenu.put("subTotal", objBillItemDtl.getSubTotal());
					 objMenu.put("discountAmt", objBillItemDtl.getDiscountAmount());
					 
					arrMenuwiseObj.put(objMenu);
                 }
             }
   
			st.close();
			st1.close();
			st2.close();
			st3.close();
			cmsCon.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrMenuwiseObj;//jObj.toString();
	}
		
   private void funGenerateMenuHeadWiseSales(ResultSet rsMenuHeadWiseSales)
    {
        try
        {
            while (rsMenuHeadWiseSales.next())
            {
                String posCode = rsMenuHeadWiseSales.getString(10);//posCode
                String posName = rsMenuHeadWiseSales.getString(5);//posName
                String menuCode = rsMenuHeadWiseSales.getString(1);//menuCode
                String menuName = rsMenuHeadWiseSales.getString(2);//menuName                
                double qty = rsMenuHeadWiseSales.getDouble(3);//qty
                double salesAmt = rsMenuHeadWiseSales.getDouble(8);//salesAmt
                double subTotal = rsMenuHeadWiseSales.getDouble(4);//subTotal
                double discAmt = rsMenuHeadWiseSales.getDouble(9);//disc                 

                if (mapPOSMenuHeadDtl.containsKey(posCode))
                {
                    Map<String, clsBillItemDtl> mapItemDtl = mapPOSMenuHeadDtl.get(posCode);
                    if (mapItemDtl.containsKey(menuCode))
                    {
                        clsBillItemDtl objItemDtl = mapItemDtl.get(menuCode);
                        objItemDtl.setQuantity(objItemDtl.getQuantity() + qty);
                        objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
                        objItemDtl.setSubTotal(objItemDtl.getSubTotal() + subTotal);
                        objItemDtl.setDiscountAmount(objItemDtl.getDiscountAmount() + discAmt);
                    }
                    else
                    {
                        clsBillItemDtl objItemDtl = new clsBillItemDtl(qty, salesAmt, discAmt, posName, subTotal, menuCode, menuName);
                        mapItemDtl.put(menuCode, objItemDtl);
                    }
                }
                else
                {
                    Map<String, clsBillItemDtl> mapItemDtl = new LinkedHashMap<>();
                    clsBillItemDtl objItemDtl = new clsBillItemDtl(qty, salesAmt, discAmt, posName, subTotal, menuCode, menuName);
                    mapItemDtl.put(menuCode, objItemDtl);
                    mapPOSMenuHeadDtl.put(posCode, mapItemDtl);
                }
            }
            
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
		
		@GET
		@Path("/funPOSSalesReport")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONArray funPOSSalesReport(@QueryParam("FromDate") String fromDate,@QueryParam("ToDate") String toDate,@QueryParam("ReportType") String reportType)
		{
			return funGetPOSSalesReportData(fromDate, toDate,reportType);

		}
		
		private JSONArray funGetPOSSalesReportData(String fromDate, String toDate,String reportType)
		{
			JSONArray jsArrreportDetails=new JSONArray();
			switch(reportType)
			{
				case "POSWise":
					jsArrreportDetails=funGetPOSWiseSaleReportData(fromDate, toDate,reportType);
				break;
				
				case "ItemWise":
					jsArrreportDetails=funGetItemWiseSaleReportData(fromDate, toDate,reportType);
				break;
				
				case "MenuHeadWise":
					jsArrreportDetails=funGetMenuHeadWiseSaleReportData(fromDate, toDate, reportType);
				break;
				
				case "GroupWise":
					jsArrreportDetails=funGetGroupWiseSaleReportData(fromDate, toDate, reportType);
				break;
				case "SubGroupWise":
					jsArrreportDetails=funGetSubGroupWiseSaleReportData(fromDate, toDate, reportType);
				break;
			}
			
			return jsArrreportDetails;
		}
		
		
		private JSONArray funGetPOSWiseSaleReportData(String fromDate, String toDate,String reportType) 
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			// String posobj=null;
			StringBuilder sbSqlLive = new StringBuilder();
		    StringBuilder sbSqlQFile = new StringBuilder();
		    StringBuilder sbFilters=new StringBuilder();
		    HashMap<String,Double> mapPOSWiseSales=new HashMap<String,Double>();
		    HashMap<String,String> mapPOSData=new HashMap<String,String>();
		    
			JSONObject jObj = new JSONObject();
			JSONArray arrPOSwiseObj = new JSONArray();
			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbFilters.setLength(0);
				 String sql="";
				 String []arrFromdate=fromDate.split("/");
				 String []arrTodate=toDate.split("/");
				 fromDate=arrFromdate[2]+"/"+arrFromdate[1]+"/"+arrFromdate[0];
				 toDate=arrTodate[2]+"/"+arrTodate[1]+"/"+arrTodate[0];
	           
				
				
				/*
				sbSqlLive.append(" select a.strPOSCode,c.strPosName,date(a.dteBillDate),sum(b.dblSettlementAmt),sum(a.dblGrandTotal) "
						+ " from tblbillhd a, tblbillsettlementdtl b,tblposmaster c  "
						+ " where a.strBillNo=b.strBillNo  and a.strPOSCode=c.strPosCode "
						+ " and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"'  "
						+ " group by a.strPOSCode,date(a.dteBillDate) "
						+ " order by date(a.dteBillDate) ");
				
				
				sbSqlQFile.append(" select a.strPOSCode,c.strPosName,date(a.dteBillDate),sum(b.dblSettlementAmt),sum(a.dblGrandTotal)  "
						+ "from tblqbillhd a, tblqbillsettlementdtl b,tblposmaster c  "
						+ "where a.strBillNo=b.strBillNo  and a.strPOSCode=c.strPosCode "
						+ "and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"'  "
						+ "group by a.strPOSCode,date(a.dteBillDate) "
						+ "order by date(a.dteBillDate) ");*/
				
				 sbSqlLive.append("select  b.strPosCode,b.strPosName,sum(a.dblGrandTotal) "
	                        + "from tblbillhd a,tblposmaster b "
	                        + "where a.strPOSCode=b.strPosCode "
	                        + "and  date(a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
	                        + "group by b.strPosCode,b.strPosName "
	                        + "order by b.strPosCode,b.strPosName;   ");

                sbSqlQFile.append("select  b.strPosCode,b.strPosName,sum(a.dblGrandTotal) "
                        + "from tblqbillhd a,tblposmaster b "
                        + "where a.strPOSCode=b.strPosCode "
                        + "and  date(a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                        + "group by b.strPosCode,b.strPosName "
                        + "order by b.strPosCode,b.strPosName;   ");
				
				 ResultSet rsTemp= st.executeQuery(sbSqlLive.toString());
					
				 while (rsTemp.next()) 
				 {
				 	String posCode = rsTemp.getString(1);//posCode
	                String posName = rsTemp.getString(2);//posName                                
	                double salesAmt = rsTemp.getDouble(3);//salesAmt
	                mapPOSData.put(posName,posCode);
					  if (mapPOSWiseSales.containsKey(posName))
	                  {
	                      double oldSalesAmt = mapPOSWiseSales.get(posName);
	                      mapPOSWiseSales.put(posName, oldSalesAmt + salesAmt);
	                  }
	                  else
	                  {
	                	  mapPOSWiseSales.put(posName, salesAmt);
	                  }
			
			     }
				 rsTemp.close();
				 
				 rsTemp= st.executeQuery(sbSqlQFile.toString());
					
				 while (rsTemp.next()) 
					{
					 
					 	String posCode = rsTemp.getString(1);//posCode
		                String posName = rsTemp.getString(2);//posName                                
		                double salesAmt = rsTemp.getDouble(3);//salesAmt
		                mapPOSData.put(posName,posCode);
						  if (mapPOSWiseSales.containsKey(posName))
		                  {
		                      double oldSalesAmt = mapPOSWiseSales.get(posName);
		                      mapPOSWiseSales.put(posName, oldSalesAmt + salesAmt);
		                  }
		                  else
		                  {
		                	  mapPOSWiseSales.put(posName, salesAmt);
		                  }
					 
					 /*if(mapPOSWiseSales.size()>0)
					 {
						 if(mapPOSWiseSales.containsKey(rsTemp.getString(1)))
						 {
							 String value=mapPOSWiseSales.get(rsTemp.getString(1));
							 String []data=value.split("#");
							 double settleAmt=Double.valueOf(data[2])+rsTemp.getDouble(4);
							 double grandAmt=Double.valueOf(data[3])+rsTemp.getDouble(5);
							 String saleData=data[0]+"#"+data[1]+"#"+String.valueOf(settleAmt)+"#"+String.valueOf(grandAmt);
		                     mapPOSWiseSales.put(rsTemp.getString(1), saleData);
						 }
						 else
						 {
							 String saleData=rsTemp.getString(2)+"#"+rsTemp.getString(3)+"#"+String.valueOf(rsTemp.getDouble(4))+"#"+String.valueOf(rsTemp.getDouble(5));
		                     mapPOSWiseSales.put(rsTemp.getString(1), saleData);
						 }
					 }
					 else
					 {
						 String saleData=rsTemp.getString(2)+"#"+rsTemp.getString(3)+"#"+String.valueOf(rsTemp.getDouble(4))+"#"+String.valueOf(rsTemp.getDouble(5));
	                     mapPOSWiseSales.put(rsTemp.getString(1), saleData);
					 }
				*/
				    }
				   rsTemp.close();
				   
				   double totalSale = 0.00;
				   if(mapPOSWiseSales.size()>0)
				   {
					   Iterator<Map.Entry<String, Double>> itPOSSales = mapPOSWiseSales.entrySet().iterator();
		                while (itPOSSales.hasNext())
		                {
		                    Map.Entry<String, Double> entry = itPOSSales.next();
		                    String posName = entry.getKey();
		                    double sale = entry.getValue();

		                    totalSale += sale;
		                    JSONObject objPOS= new JSONObject();
		                    
		                    objPOS.put("posCode", mapPOSData.get(posName));
		                    objPOS.put("posName",posName);
		                    objPOS.put("grandTotal", sale);
		                    objPOS.put("dblSettleAmt",sale);
		                    arrPOSwiseObj.put(objPOS);
		                }

					   
				   }
	                				   
				  /* if(mapPOSWiseSales.size()>0)
				   {
					   for (Map.Entry<String, Double> entry : mapPOSWiseSales.entrySet())
			            {
			              String value = entry.getValue();
			              String []data=value.split("#");
			              JSONObject objPOS= new JSONObject();
			                objPOS.put("posCode", entry.getKey());
			                objPOS.put("posName", data[0]);
			                objPOS.put("dblSettleAmt",Double.valueOf(data[2]));
			                objPOS.put("grandTotal", Double.valueOf(data[3]));
							
			            }
				   }*/
	           
				jObj.put("poswsieSaleReport", arrPOSwiseObj);
	           
				st.close();
				cmsCon.close();

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		
			return arrPOSwiseObj;//jObj.toString();
		}	
		
		
		
		
		private JSONArray funGetItemWiseSaleReportData(String fromDate, String toDate,String reportType) 
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			// String posobj=null;
			StringBuilder sbSqlLive = new StringBuilder();
		    StringBuilder sbSqlQFile = new StringBuilder();
		    StringBuilder sbSqlModLive = new StringBuilder();
		    StringBuilder sbSqlModQFile = new StringBuilder();
		    StringBuilder sbFilters=new StringBuilder();
		    Map<String, Map<String, clsBillItemDtl>> mapPOSItemDtl = new LinkedHashMap<>();
		    HashMap<String, String> hmPOS = new HashMap<String, String>();
	        
		    
			JSONObject jObj = new JSONObject();
			JSONArray arrItemwiseObj = new JSONArray();
			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbFilters.setLength(0);
				 String sql="";
				 String []arrFromdate=fromDate.split("/");
				 String []arrTodate=toDate.split("/");
				 fromDate=arrFromdate[2]+"/"+arrFromdate[1]+"/"+arrFromdate[0];
				 toDate=arrTodate[2]+"/"+arrTodate[1]+"/"+arrTodate[0];
	           
				
				sbSqlLive.append("   select a.strItemCode,a.strItemName,c.strPOSName,sum(a.dblQuantity), "
						+ " sum(a.dblTaxAmount)   ,sum(a.dblAmount)-sum(a.dblDiscountAmt),'SANGUINE' ,sum(a.dblAmount),  "
						+ " sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode "
						+ " from tblbilldtl a,tblbillhd b,tblposmaster c  "
						+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  "
						+ " and date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'  "
						+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate ");
				
				
				sbSqlQFile.append(" select a.strItemCode,a.strItemName,c.strPOSName,sum(a.dblQuantity), "
						+ " sum(a.dblTaxAmount)  ,sum(a.dblAmount)-sum(a.dblDiscountAmt),'SANGUINE' ,sum(a.dblAmount),  "
						+ " sum(a.dblDiscountAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode  "
						+ " from tblqbilldtl a,tblqbillhd b,tblposmaster c  "
						+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  "
						+ " and date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'     "
						+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate");
						
				
				sbSqlModLive.append("  select a.strItemCode,a.strModifierName,c.strPOSName,sum(a.dblQuantity),'0.0',  "
						+ " sum(a.dblAmount)-sum(a.dblDiscAmt),'SANGUINE' ,sum(a.dblAmount),  sum(a.dblDiscAmt),"
						+ " DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode   "
						+ " from tblbillmodifierdtl a,tblbillhd b,tblposmaster c   "
						+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode   and a.dblamount>0   "
						+ " and date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'  "
						+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate");
				
				
				sbSqlModQFile.append("  select a.strItemCode,a.strModifierName,c.strPOSName,sum(a.dblQuantity),'0',  "
						+ " sum(a.dblAmount)-sum(a.dblDiscAmt),'SANGUINE' ,sum(a.dblAmount),  sum(a.dblDiscAmt),"
						+ " DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),b.strPOSCode  "
						+ " from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d  "
						+ " where a.strBillNo=b.strBillNo and b.strPOSCode=c.strPosCode  "
						+ " and a.strItemCode=d.strItemCode and a.dblamount>0  "
						+ " and date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"' "
						+ " group by a.strItemCode,c.strPOSName  order by b.dteBillDate ");
					
				
				ResultSet rsItemWiseSales = st.executeQuery(sbSqlLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                
                
                String sqlPOS = "select strPOSCode,strPOSName from tblposmaster "
                        + "order by strPOSName";
                ResultSet rsSales = st.executeQuery(sqlPOS);
                while (rsSales.next())
                {
                    hmPOS.put(rsSales.getString(1), rsSales.getString(2));
                }
                rsSales.close();
                
			   if(mapPOSItemDtl.size()>0)
				   {
				   Iterator<Map.Entry<String, Map<String, clsBillItemDtl>>> posIterator = mapPOSItemDtl.entrySet().iterator();
	                while (posIterator.hasNext() )
	                {
	                	Map.Entry<String,Map<String,clsBillItemDtl>> posEntry=posIterator.next();
	                    Map<String, clsBillItemDtl> mapItemDtl = posEntry.getValue();
	                    Iterator<Map.Entry<String, clsBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
	                    while (itemIterator.hasNext())
	                    {
	                    	Map.Entry<String,clsBillItemDtl> itemEntry=itemIterator.next();
	                        clsBillItemDtl objBillItemDtl = itemEntry.getValue();

	                        JSONObject objPOS= new JSONObject();
	                        JSONArray arrPOSDtls = new JSONArray();
			                objPOS.put("strItemCode", objBillItemDtl.getItemCode());
			                objPOS.put("strItemName", objBillItemDtl.getItemName());
			                if(hmPOS.size()>0)
			                {
			                	int count=1;
			                    for(Map.Entry<String,String> entry:hmPOS.entrySet())
			                    {  
			                    	if(entry.getKey().equals(posEntry.getKey()))
			                        {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",objBillItemDtl.getPosName());
						               jObjPos.put("dblAmount",objBillItemDtl.getAmount());
						               arrPOSDtls.put(jObjPos);
			                        }
			                       else
			                       {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",entry.getValue());
						               jObjPos.put("dblAmount","0.0");
						               arrPOSDtls.put(jObjPos);
			                       }
			                       count++;
			                    }
			                    objPOS.put("arrListPosDtl", arrPOSDtls);
			                }
			                arrItemwiseObj.put(objPOS);
			            }
	                }
				   }
	           
				jObj.put("ItemWiseReport", arrItemwiseObj);
	           
				st.close();
				cmsCon.close();

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		
			return arrItemwiseObj;//jObj.toString();
		}	
		
		  
		
		
		private JSONArray funGetMenuHeadWiseSaleReportData(String fromDate, String toDate,String reportType) 
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			// String posobj=null;
			StringBuilder sbSqlLive = new StringBuilder();
		    StringBuilder sbSqlQFile = new StringBuilder();
		    StringBuilder sbSqlModLive = new StringBuilder();
		    StringBuilder sbSqlModQFile = new StringBuilder();
		    StringBuilder sbFilters=new StringBuilder();
		    Map<String, Map<String, clsBillItemDtl>> mapPOSItemDtl = new LinkedHashMap<>();
		    HashMap<String, String> hmPOS = new HashMap<String, String>();
		    
			JSONObject jObj = new JSONObject();
			JSONArray arrMenuHeadwiseObj = new JSONArray();
			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbFilters.setLength(0);
				 String sql="";
				 String []arrFromdate=fromDate.split("/");
				 String []arrTodate=toDate.split("/");
				 fromDate=arrFromdate[2]+"/"+arrFromdate[1]+"/"+arrFromdate[0];
				 toDate=arrTodate[2]+"/"+arrTodate[1]+"/"+arrTodate[0];
	           
				
				sbSqlLive.append("  SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), "
						+ " sum(a.dblQuantity),  sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,"
						+ " 'SANGUINE',a.dblRate  ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode    "
						+ " FROM tblbilldtl a  left outer join tblbillhd b on a.strBillNo=b.strBillNo  "
						+ " left outer join tblposmaster f on b.strposcode=f.strposcode   "
						+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode   "
						+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode  "
						+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode  "
						+ " where date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+" ' "
						+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName");
		
				
				
				sbSqlQFile.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), "
						+ " sum(a.dblQuantity),  sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,"
						+ " 'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscountAmt),b.strPOSCode   "
						+ " FROM tblqbilldtl a  left outer join tblqbillhd b on a.strBillNo=b.strBillNo  "
						+ " left outer join tblposmaster f on b.strposcode=f.strposcode  "
						+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode   "
						+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode  "
						+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode  "
						+ " where date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"' "
						+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName");
				
				sbSqlModLive.append(" SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), "
						+ " sum(a.dblQuantity),  sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,"
						+ " 'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode  "
						+ " FROM tblbillmodifierdtl a  left outer join tblbillhd b on a.strBillNo=b.strBillNo  "
						+ " left outer join tblposmaster f on b.strposcode=f.strposcode  "
						+ " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode   "
						+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode  "
						+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode  "
						+ " where date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'   and a.dblAmount>0  "
						+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName ");
				
				sbSqlModQFile.append("  SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), "
						+ " sum(a.dblQuantity),  sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,"
						+ " 'SANGUINE',a.dblRate ,sum(a.dblAmount),sum(a.dblDiscAmt),b.strPOSCode   "
						+ " FROM tblqbillmodifierdtl a  left outer join tblqbillhd b on a.strBillNo=b.strBillNo "
						+ " left outer join tblposmaster f on b.strposcode=f.strposcode  "
						+ " left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
						+ " and b.strposcode =d.strposcode and b.strAreaCode= d.strAreaCode  "
						+ " left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode "
						+ " where date( b.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'   and a.dblAmount>0  "
						+ " Group by b.strPoscode, d.strMenuCode,e.strMenuName ");
				
				String sqlPOS = "select strPOSCode,strPOSName from tblposmaster "
	                        + "order by strPOSName";
                ResultSet rsSales = st.executeQuery(sqlPOS);
                while (rsSales.next())
                {
                    hmPOS.put(rsSales.getString(1), rsSales.getString(2));
                }
                rsSales.close();
				
				ResultSet rsItemWiseSales = st.executeQuery(sbSqlLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                
			   if(mapPOSItemDtl.size()>0)
				{
				   Iterator<Map.Entry<String, Map<String, clsBillItemDtl>>> posIterator = mapPOSItemDtl.entrySet().iterator();
	               while (posIterator.hasNext() )
	               {
	                	Map.Entry<String,Map<String,clsBillItemDtl>> posEntry=posIterator.next();
	                    Map<String, clsBillItemDtl> mapItemDtl = posEntry.getValue();
	                    Iterator<Map.Entry<String, clsBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
	                    while (itemIterator.hasNext())
	                    {
	                    	Map.Entry<String,clsBillItemDtl> itemEntry=itemIterator.next();
	                        clsBillItemDtl objBillItemDtl = itemEntry.getValue();

	                        JSONObject objPOS= new JSONObject();
	                        JSONArray arrPOSDtls = new JSONArray();
			                objPOS.put("strItemCode", objBillItemDtl.getItemCode());
			                objPOS.put("strItemName", objBillItemDtl.getItemName());
			                if(hmPOS.size()>0)
			                {
			                	int count=1;
			                    for(Map.Entry<String,String> entry:hmPOS.entrySet())
			                    {  
			                    	if(entry.getKey().equals(posEntry.getKey()))
			                        {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",objBillItemDtl.getPosName());
						               jObjPos.put("dblAmount",objBillItemDtl.getAmount());
						               arrPOSDtls.put(jObjPos);
			                        }
			                       else
			                       {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",entry.getValue());
						               jObjPos.put("dblAmount","0.0");
						               arrPOSDtls.put(jObjPos);
			                       }
			                       count++;
			                    }
			                    objPOS.put("arrListPosDtl", arrPOSDtls);
			                }
			                arrMenuHeadwiseObj.put(objPOS);
			            }
	               }
				}
				jObj.put("MenuheadwiseReport", arrMenuHeadwiseObj);
	           
				st.close();
				cmsCon.close();

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		
			return arrMenuHeadwiseObj;//jObj.toString();
		}
		
		
		
		
	   
		
		private JSONArray funGetGroupWiseSaleReportData(String fromDate, String toDate,String reportType) 
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			// String posobj=null;
			StringBuilder sbSqlLive = new StringBuilder();
		    StringBuilder sbSqlQFile = new StringBuilder();
		    StringBuilder sbSqlModLive = new StringBuilder();
		    StringBuilder sbSqlModQFile = new StringBuilder();
		    StringBuilder sbFilters=new StringBuilder();
		    Map<String, Map<String, clsBillItemDtl>> mapPOSItemDtl = new LinkedHashMap<>();
		    HashMap<String, String> hmPOS = new HashMap<String, String>();
		    
			JSONObject jObj = new JSONObject();
			JSONArray arrGroupwiseObj = new JSONArray();
			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbFilters.setLength(0);
				 String sql="";
				 String []arrFromdate=fromDate.split("/");
				 String []arrTodate=toDate.split("/");
				 fromDate=arrFromdate[2]+"/"+arrFromdate[1]+"/"+arrFromdate[0];
				 toDate=arrTodate[2]+"/"+arrTodate[1]+"/"+arrTodate[0];
	           
				
				sbSqlLive.append("   SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity), sum( b.dblAmount)-sum(b.dblDiscountAmt) ,"
						+ " f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode  "
						+ " FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f  "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode "
						+ " and c.strGroupCode=d.strGroupCode  and d.strSubGroupCode=e.strSubGroupCode  "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"' "
						+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode");
				
				
				sbSqlQFile.append(" SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity), sum( b.dblAmount)-sum(b.dblDiscountAmt) ,"
						+ " f.strPosName, 'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode   "
						+ " FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f  "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode and b.strItemCode=e.strItemCode "
						+ " and c.strGroupCode=d.strGroupCode  and d.strSubGroupCode=e.strSubGroupCode   "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"' "
						+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode ");
				
				
				sbSqlModLive.append("  select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),"
						+ " f.strPOSName,'SANGUINE','0' , sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode   "
						+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c   "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode   and LEFT(b.strItemCode,7)=d.strItemCode  "
						+ " and d.strSubGroupCode=e.strSubGroupCode  and e.strGroupCode=c.strGroupCode  and b.dblamount>0   "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'  "
						+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode");
				
				
				sbSqlModQFile.append("   select c.strGroupCode,c.strGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),"
						+ " f.strPOSName,'SANGUINE','0' ,  sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
						+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c   "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode   "
						+ " and d.strSubGroupCode=e.strSubGroupCode and e.strGroupCode=c.strGroupCode  and b.dblamount>0   "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'  "
						+ " GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode ");
				
				
				String sqlPOS = "select strPOSCode,strPOSName from tblposmaster "
                        + "order by strPOSName";
	            ResultSet rsSales = st.executeQuery(sqlPOS);
	            while (rsSales.next())
	            {
	                hmPOS.put(rsSales.getString(1), rsSales.getString(2));
	            }
	            rsSales.close();
				
				
				ResultSet rsItemWiseSales = st.executeQuery(sbSqlLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                
			   if(mapPOSItemDtl.size()>0)
				{
				   Iterator<Map.Entry<String, Map<String, clsBillItemDtl>>> posIterator = mapPOSItemDtl.entrySet().iterator();
	                while (posIterator.hasNext() )
	                {
	                	Map.Entry<String,Map<String,clsBillItemDtl>> posEntry=posIterator.next();
	                    Map<String, clsBillItemDtl> mapItemDtl = posEntry.getValue();
	                    Iterator<Map.Entry<String, clsBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
	                    while (itemIterator.hasNext())
	                    {
	                    	Map.Entry<String,clsBillItemDtl> itemEntry=itemIterator.next();
	                        clsBillItemDtl objBillItemDtl = itemEntry.getValue();

	                        JSONObject objPOS= new JSONObject();
	                        JSONArray arrPOSDtls = new JSONArray();
			                objPOS.put("strItemCode", objBillItemDtl.getItemCode());
			                objPOS.put("strItemName", objBillItemDtl.getItemName());
			                if(hmPOS.size()>0)
			                {
			                	int count=1;
			                    for(Map.Entry<String,String> entry:hmPOS.entrySet())
			                    {  
			                    	if(entry.getKey().equals(posEntry.getKey()))
			                        {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",objBillItemDtl.getPosName());
						               jObjPos.put("dblAmount",objBillItemDtl.getAmount());
						               arrPOSDtls.put(jObjPos);
			                        }
			                       else
			                       {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",entry.getValue());
						               jObjPos.put("dblAmount","0.0");
						               arrPOSDtls.put(jObjPos);
			                       }
			                       count++;
			                    }
			                    objPOS.put("arrListPosDtl", arrPOSDtls);
			                }
			                arrGroupwiseObj.put(objPOS);
			            }
	                }
				}
				jObj.put("GroupwiseReport", arrGroupwiseObj);
	           
				st.close();
				cmsCon.close();

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		
			return arrGroupwiseObj;//jObj.toString();
		}
		
		
		
		private JSONArray funGetSubGroupWiseSaleReportData(String fromDate, String toDate,String reportType) 
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection cmsCon = null;
			Statement st = null;
			// String posobj=null;
			StringBuilder sbSqlLive = new StringBuilder();
		    StringBuilder sbSqlQFile = new StringBuilder();
		    StringBuilder sbSqlModLive = new StringBuilder();
		    StringBuilder sbSqlModQFile = new StringBuilder();
		    StringBuilder sbFilters=new StringBuilder();
		    Map<String, Map<String, clsBillItemDtl>> mapPOSItemDtl = new LinkedHashMap<>();
		    HashMap<String, String> hmPOS = new HashMap<String, String>();
		    
			JSONObject jObj = new JSONObject();
			JSONArray arrSubgroupwiseObj = new JSONArray();
			try {
				cmsCon = objDb.funOpenAPOSCon("mysql", "master");
				st = cmsCon.createStatement();
				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbFilters.setLength(0);
				 String sql="";
				 String []arrFromdate=fromDate.split("/");
				 String []arrTodate=toDate.split("/");
				 fromDate=arrFromdate[2]+"/"+arrFromdate[1]+"/"+arrFromdate[0];
				 toDate=arrTodate[2]+"/"+arrTodate[1]+"/"+arrTodate[0];
	           
				
				sbSqlLive.append("   SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  , sum( b.dblAmount )-sum(b.dblDiscountAmt), "
						+ " f.strPosName,'SANGUINE',b.dblRate ,  sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode  "
						+ " from tblbillhd a,tblbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f   "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode   and b.strItemCode=d.strItemCode  "
						+ " and c.strSubGroupCode=d.strSubGroupCode   "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'   "
						+ " group by  c.strSubGroupCode, c.strSubGroupName, a.strPoscode ");
				
				
				sbSqlQFile.append("  SELECT c.strSubGroupCode, c.strSubGroupName, sum( b.dblQuantity )  ,  sum( b.dblAmount )-sum(b.dblDiscountAmt),"
						+ " f.strPosName,'SANGUINE',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode  "
						+ " from tblqbillhd a,tblqbilldtl b,tblsubgrouphd c,tblitemmaster d  ,tblposmaster f   "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode   and b.strItemCode=d.strItemCode "
						+ " and c.strSubGroupCode=d.strSubGroupCode  "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"' "
						+ " group by  c.strSubGroupCode, c.strSubGroupName, a.strPoscode");
				
				
				sbSqlModLive.append("  select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity), sum(b.dblAmount)-sum(b.dblDiscAmt),"
						+ " f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode   "
						+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c  "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode "
						+ "  and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0   "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'  "
						+ " group by  c.strSubGroupCode, c.strSubGroupName, a.strPoscode");
				
				
				sbSqlModQFile.append("   select c.strSubGroupCode,c.strSubGroupName,sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt), "
						+ " f.strPOSName,'SANGUINE','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode  "
						+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd c "
						+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  and LEFT(b.strItemCode,7)=d.strItemCode  "
						+ " and d.strSubGroupCode=c.strSubGroupCode  and b.dblamount>0  "
						+ " and date( a.dteBillDate ) BETWEEN '"+fromDate+"' and '"+toDate+"'  "
						+ " group by  c.strSubGroupCode, c.strSubGroupName, a.strPoscode ");
				
				String sqlPOS = "select strPOSCode,strPOSName from tblposmaster "
                        + "order by strPOSName";
	            ResultSet rsSales = st.executeQuery(sqlPOS);
	            while (rsSales.next())
	            {
	                hmPOS.put(rsSales.getString(1), rsSales.getString(2));
	            }
	            rsSales.close();
				
				
				ResultSet rsItemWiseSales = st.executeQuery(sbSqlLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModLive.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                rsItemWiseSales = st.executeQuery(sbSqlModQFile.toString());
                funGenerateItemWiseSales(rsItemWiseSales,mapPOSItemDtl,reportType);
                
			   if(mapPOSItemDtl.size()>0)
				{
				   Iterator<Map.Entry<String, Map<String, clsBillItemDtl>>> posIterator = mapPOSItemDtl.entrySet().iterator();
	                while (posIterator.hasNext() )
	                {
	                	Map.Entry<String,Map<String,clsBillItemDtl>> posEntry=posIterator.next();
	                    Map<String, clsBillItemDtl> mapItemDtl = posEntry.getValue();
	                    Iterator<Map.Entry<String, clsBillItemDtl>> itemIterator = mapItemDtl.entrySet().iterator();
	                    while (itemIterator.hasNext())
	                    {
	                    	Map.Entry<String,clsBillItemDtl> itemEntry=itemIterator.next();
	                        clsBillItemDtl objBillItemDtl = itemEntry.getValue();

	                        JSONObject objPOS= new JSONObject();
	                        JSONArray arrPOSDtls = new JSONArray();
			                objPOS.put("strItemCode", objBillItemDtl.getItemCode());
			                objPOS.put("strItemName", objBillItemDtl.getItemName());
			                if(hmPOS.size()>0)
			                {
			                	int count=1;
			                    for(Map.Entry<String,String> entry:hmPOS.entrySet())
			                    {  
			                    	if(entry.getKey().equals(posEntry.getKey()))
			                        {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",objBillItemDtl.getPosName());
						               jObjPos.put("dblAmount",objBillItemDtl.getAmount());
						               arrPOSDtls.put(jObjPos);
			                        }
			                       else
			                       {
			                    	   JSONObject jObjPos=new JSONObject();
						               jObjPos.put("PosName",entry.getValue());
						               jObjPos.put("dblAmount","0.0");
						               arrPOSDtls.put(jObjPos);
			                       }
			                       count++;
			                    }
			                    objPOS.put("arrListPosDtl", arrPOSDtls);
			                }
			                arrSubgroupwiseObj.put(objPOS);
			            }
	                }
				}
				jObj.put("SubgroupwiseReport", arrSubgroupwiseObj);
	           
				st.close();
				cmsCon.close();

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		
			return arrSubgroupwiseObj;//jObj.toString();
		}
		
		
		private void funGenerateItemWiseSales(ResultSet rsSales,Map<String, Map<String, clsBillItemDtl>> mapPOSItemDtl,String reportType)
	    {
	        try
	        {
	        	switch(reportType)
	    			{
	    				case "ItemWise":
	    					while (rsSales.next())
	    		            {
	    		                String itemCode = rsSales.getString(1);//itemCode
	    		                String itemName = rsSales.getString(2);//itemName
	    		                String posName = rsSales.getString(3);//posName
	    		                double qty = rsSales.getDouble(4);//qty
	    		                double salesAmt = rsSales.getDouble(8);//salesAmount
	    		                double subTotal = rsSales.getDouble(6);//sunTotal
	    		                double discAmt = rsSales.getDouble(9);//discount
	    		                String date = rsSales.getString(10);//date
	    		                String posCode = rsSales.getString(11);//posCode

	    		                if (mapPOSItemDtl.containsKey(posCode))
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = mapPOSItemDtl.get(posCode);
	    		                    if (mapItemDtl.containsKey(itemCode))
	    		                    {
	    		                        clsBillItemDtl objItemDtl = mapItemDtl.get(itemCode);
	    		                        objItemDtl.setQuantity(objItemDtl.getQuantity() + qty);
	    		                        objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
	    		                        objItemDtl.setSubTotal(objItemDtl.getSubTotal() + subTotal);
	    		                        objItemDtl.setDiscountAmount(objItemDtl.getDiscountAmount() + discAmt);
	    		                    }
	    		                    else
	    		                    {
	    		                        clsBillItemDtl objItemDtl = new clsBillItemDtl(date, itemCode, itemName, qty, salesAmt, discAmt, posName, subTotal);
	    		                        mapItemDtl.put(itemCode, objItemDtl);
	    		                    }
	    		                }
	    		                else
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = new LinkedHashMap<>();
	    		                    clsBillItemDtl objItemDtl = new clsBillItemDtl(date, itemCode, itemName, qty, salesAmt, discAmt, posName, subTotal);
	    		                    mapItemDtl.put(itemCode, objItemDtl);
	    		                    mapPOSItemDtl.put(posCode, mapItemDtl);
	    		                }
	    		            }
	    				break;
	    				
	    				case "MenuHeadWise":
	    					while (rsSales.next())
	    		            {
	    		                String posCode = rsSales.getString(10);//posCode
	    		                String posName = rsSales.getString(5);//posName
	    		                String menuCode = rsSales.getString(1);//menuCode
	    		                String menuName = rsSales.getString(2);//menuName                
	    		                double qty = rsSales.getDouble(3);//qty
	    		                double salesAmt = rsSales.getDouble(8);//salesAmt
	    		                double subTotal = rsSales.getDouble(4);//subTotal
	    		                double discAmt = rsSales.getDouble(9);//disc                 

	    		                if (mapPOSItemDtl.containsKey(posCode))
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = mapPOSItemDtl.get(posCode);
	    		                    if (mapItemDtl.containsKey(menuCode))
	    		                    {
	    		                        clsBillItemDtl objItemDtl = mapItemDtl.get(menuCode);
	    		                        objItemDtl.setQuantity(objItemDtl.getQuantity() + qty);
	    		                        objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
	    		                        objItemDtl.setSubTotal(objItemDtl.getSubTotal() + subTotal);
	    		                        objItemDtl.setDiscountAmount(objItemDtl.getDiscountAmount() + discAmt);
	    		                    }
	    		                    else
	    		                    {
	    		                        clsBillItemDtl objItemDtl = new clsBillItemDtl("", menuCode, menuName, qty, salesAmt, discAmt, posName, subTotal);
	    		                        mapItemDtl.put(menuCode, objItemDtl);
	    		                    }
	    		                }
	    		                else
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = new LinkedHashMap<>();
	    		                    clsBillItemDtl objItemDtl = new clsBillItemDtl("", menuCode, menuName, qty, salesAmt, discAmt, posName, subTotal);
	    		                    mapItemDtl.put(menuCode, objItemDtl);
	    		                    mapPOSItemDtl.put(posCode, mapItemDtl);
	    		                }

	    		            }
	    				break;
	    				
	    				
	    				case "GroupWise":
	    					while (rsSales.next())
	    		            {
	    						String groupCode = rsSales.getString(1);
	    		                String groupName = rsSales.getString(2);
	    		                String posName = rsSales.getString(5);
	    		                double salesAmt = rsSales.getDouble(8);
	    		                String posCode = rsSales.getString(10);               

	    		                if (mapPOSItemDtl.containsKey(posCode))
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = mapPOSItemDtl.get(posCode);
	    		                    if (mapItemDtl.containsKey(groupCode))
	    		                    {
	    		                        clsBillItemDtl objItemDtl = mapItemDtl.get(groupCode);
	    		                        objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
	    		                    }
	    		                    else
	    		                    {
	    		                        clsBillItemDtl objItemDtl = new clsBillItemDtl("", groupCode, groupName, 0, salesAmt, 0, posName, 0);
	    		                        mapItemDtl.put(groupCode, objItemDtl);
	    		                    }
	    		                }
	    		                else
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = new LinkedHashMap<>();
	    		                    clsBillItemDtl objItemDtl = new clsBillItemDtl("", groupCode, groupName, 0, salesAmt, 0, posName, 0);
	    		                    mapItemDtl.put(groupCode, objItemDtl);
	    		                    mapPOSItemDtl.put(posCode, mapItemDtl);
	    		                }

	    		            }
	    				break;
	    				
	    				case "SubGroupWise":
	    					while (rsSales.next())
	    		            {
	    						String subGroupCode = rsSales.getString(1);
	    		                String subGroupName = rsSales.getString(2);
	    		                String posName = rsSales.getString(5);
	    		                double salesAmt = rsSales.getDouble(8);
	    		                String posCode = rsSales.getString(10);               

	    		                if (mapPOSItemDtl.containsKey(posCode))
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = mapPOSItemDtl.get(posCode);
	    		                    if (mapItemDtl.containsKey(subGroupCode))
	    		                    {
	    		                        clsBillItemDtl objItemDtl = mapItemDtl.get(subGroupCode);
	    		                        objItemDtl.setAmount(objItemDtl.getAmount() + salesAmt);
	    		                    }
	    		                    else
	    		                    {
	    		                        clsBillItemDtl objItemDtl = new clsBillItemDtl("", subGroupCode, subGroupName, 0, salesAmt, 0, posName, 0);
	    		                        mapItemDtl.put(subGroupCode, objItemDtl);
	    		                    }
	    		                }
	    		                else
	    		                {
	    		                    Map<String, clsBillItemDtl> mapItemDtl = new LinkedHashMap<>();
	    		                    clsBillItemDtl objItemDtl = new clsBillItemDtl("", subGroupCode, subGroupName, 0, salesAmt, 0, posName, 0);
	    		                    mapItemDtl.put(subGroupCode, objItemDtl);
	    		                    mapPOSItemDtl.put(posCode, mapItemDtl);
	    		                }

	    		            }
	    				break;
	    			}
	            
	            
	            
	            
	            
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
		
		
		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funInsertDCTempTable")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveDebitDtl(JSONObject objDebitData)
		{
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
			String debitCard="SUCCESS";
			int exe=0;
			
			try {
				
				String tableNo="",cardNo="",strPrintYN="";
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
				
				JSONArray mJsonArray=(JSONArray)objDebitData.get("DebitDtl");
				String sql="";
				boolean flgData=false;
				JSONObject mJsonObject = new JSONObject();
				
				String insertQuery = "insert into tbldebitcardtabletemp(strTableNo,strCardNo,dblRedeemAmt,strPrintYN ) values ";
				
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
				    mJsonObject =(JSONObject) mJsonArray.get(i);
				    tableNo=mJsonObject.get("strTableNo").toString();
				    cardNo=mJsonObject.get("strCardNo").toString();
				    double reedemAmt=Double.parseDouble(mJsonObject.get("dblRedeemAmt").toString());
				    strPrintYN=mJsonObject.get("strPrintYN").toString();
				  
				    Date objDate = new Date();
				    String currentDate = (objDate.getYear() + 1900) + "-" +(objDate.getMonth()+ 1) + "-" + objDate.getDate() 
	     		                      + " "+ objDate.getHours() + ":" + objDate.getMinutes() + ":" +objDate.getSeconds();
				    
				    insertQuery += "('"+mJsonObject.get("strTableNo").toString()+"','"+mJsonObject.get("strCardNo").toString()+"'"
	      				+ ",'"+mJsonObject.get("dblRedeemAmt").toString()+"','"+mJsonObject.get("strPrintYN").toString()+"'),";
				}
				
	            StringBuilder sb = new StringBuilder(insertQuery);
	            int index = sb.lastIndexOf(",");
	            insertQuery = sb.delete(index, sb.length()).toString();
				System.out.println(insertQuery);
	            exe=st.executeUpdate(insertQuery);
	            System.out.println("Exe= "+exe);
	            
	             cmsCon.close();
	             st.close();
	            
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(exe==0)
				debitCard="ERROR";
			
			return Response.status(201).entity(debitCard).build();		
		}
		
		
		
		
		
		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveReservation")
		@Consumes(MediaType.APPLICATION_JSON)
		public JSONObject funSaveReservationDtl(JSONObject objDebitData)
		{
			 JSONObject jObj=new JSONObject();
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
			String reservationCode="";
			StringBuilder sqlQuery = new StringBuilder();
			int exe=0;
			
			try {
				
				String tableNo="",cardNo="",strPrintYN="";
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
				
				JSONArray mJsonArray=(JSONArray)objDebitData.get("ReservationDtl");
				String sql="";
				boolean flgData=false;
				JSONObject mJsonObject = new JSONObject();
				
				
				sqlQuery.setLength(0);
                sqlQuery.append("INSERT INTO tblreservation "
                        + "(strResCode,strCustomerCode,intPax,strSmoking,dteResDate"
                        + ",tmeResTime,strAMPM,strSpecialInfo,strTableNo,strUserCreated"
                        + ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strPosCode) "
                        + "VALUES ");
                for (int i = 0; i < mJsonArray.length(); i++) 
				{
				    mJsonObject =(JSONObject) mJsonArray.get(i);
				    Date objDate = new Date();
				    String currentDate = (objDate.getYear() + 1900) + "-" +(objDate.getMonth()+ 1) + "-" + objDate.getDate() 
	     		                      + " "+ objDate.getHours() + ":" + objDate.getMinutes() + ":" +objDate.getSeconds();
				    reservationCode=mJsonObject.get("strReservationCode").toString();
				    if(reservationCode.isEmpty())
				    {
				    	reservationCode=funGetReservationNo();
				    }
				    else
				    {
				    	String updateSql="select strTableNo from tblreservation where strResCode='"+reservationCode+"'";
				    	ResultSet rs= st.executeQuery(updateSql);
						if(rs.next())
						{
							updateSql="update tbltablemaster set strStatus='Normal' where strTableNo='"+rs.getString(1)+"'";
				        	st.executeUpdate(updateSql);
						}
				    	
				    	sql="delete from tblreservation where strResCode='"+reservationCode+"'";
						st.executeUpdate(sql);
				    }
				    tableNo=mJsonObject.get("strTableNo").toString();
				    sqlQuery.append("('" +reservationCode+ "', '" +mJsonObject.get("strCustomerCode").toString()+ "','" +mJsonObject.get("intPax").toString()+ "'"
	                        + ",'" +mJsonObject.get("strSmoking").toString()+ "','" +mJsonObject.get("dteResDate").toString()+ "', '" +mJsonObject.get("tmeResTime").toString()+ "'"
	                        + ",'" +mJsonObject.get("strAMPM").toString()+ "', '" +mJsonObject.get("strSpecialInfo").toString()+ "','" +mJsonObject.get("strTableNo").toString()+ "'"
	                        + ",'" +mJsonObject.get("strUserCreated").toString()+ "', '" +mJsonObject.get("strUserEdited").toString()+ "','" +mJsonObject.get("dteDateCreated").toString()+ "'"
	                        + ",'" +mJsonObject.get("dteDateEdited").toString()+ "', '" +mJsonObject.get("strClientCode").toString()+ "'"
	                        + ",'" +mJsonObject.get("strPosCode").toString()+ "') ");
				    
				}
				
	            System.out.println(sqlQuery.toString());
	            exe=st.executeUpdate(sqlQuery.toString());
	            System.out.println("Exe= "+exe);
	            reservationCode=reservationCode+"Success";
	            sql="update tbltablemaster set strStatus='Reserve' where strTableNo='"+tableNo+"'";
	        	System.out.println(sql);
	        	st.executeUpdate(sql);
	        	cmsCon.close();
	        	st.close();
	        	
	            
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(exe==0)
				reservationCode="ERROR";
			
			try{
				jObj.put("reservationCode", reservationCode);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return jObj;//Response.status(201).entity(reservationCode).build();		
		}
	    	
	   
	   
		private String funGetReservationNo()
	    {
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
			StringBuilder sqlQuery = new StringBuilder();
	        String reservationCode = "", strCode = "", code = "";
	        long lastNo = 1;
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
	            sqlQuery.setLength(0);
	            sqlQuery.append("select count(*) from tblreservation");
	            ResultSet rsCustCode = st.executeQuery(sqlQuery.toString());
	            rsCustCode.next();
	            int cntReserveCode = rsCustCode.getInt(1);
	            rsCustCode.close();

	            if (cntReserveCode > 0)
	            {
	                sqlQuery.setLength(0);
	                sqlQuery.append("select max(strResCode) from tblreservation");
	                rsCustCode = st.executeQuery(sqlQuery.toString());
	                rsCustCode.next();
	                code = rsCustCode.getString(1);
	                strCode = code.substring(2, code.length());
	                System.out.println("reservation Code=" + strCode);
	                lastNo = Long.parseLong(strCode);
	                lastNo++;
	                reservationCode = "RS" + String.format("%07d", lastNo);

	                rsCustCode.close();
	            }
	            else
	            {
	                reservationCode = "RS0000001";
	            }
	            cmsCon.close();
	            st.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return reservationCode;
	           
	    }
		
		
		
		
		@GET 
		@Path("/funGetReservationDetail")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONArray funGetReservationList(@QueryParam("POSCode") String posCode,@QueryParam("FromDate") String fromDate, @QueryParam("ToDate") String toDate,
				@QueryParam("FromTime") String fromTime,@QueryParam("ToTime") String toTime)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        JSONArray arrObj=new JSONArray();
	        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="";
	            
	            
	            sql = " select a.strResCode,b.longMobileNo,b.strCustomerName,a.strSmoking,c.strTableName,a.intPax ,a.dteResDate,a.tmeResTime,a.strSpecialInfo "
	            		+ " from tblreservation a, tblcustomermaster b,tbltablemaster c "
	            		+ " where a.strCustomerCode=b.strCustomerCode "
	            		+ " and a.strTableNo=c.strTableNo "
	            		+ " and date(a.dteResDate) between '" + fromDate + "' and '" + toDate + "' "
		                + " and  TIME_FORMAT(a.tmeResTime,'%r') >= '" + fromTime + "' <= '" + toTime + "' ";
	            		
	            
	            System.out.println("query="+sql);
	            
	            
	            ResultSet rsReservationInfo = st.executeQuery(sql);
	            while (rsReservationInfo.next()) 
	            {
	            	JSONObject obj=new JSONObject();
	            	obj.put("ReservationNo",rsReservationInfo.getString(1));
	            	obj.put("MobileNo",rsReservationInfo.getString(2));
	            	obj.put("CustomerName",rsReservationInfo.getString(3));
	            	obj.put("SmokingY/N",rsReservationInfo.getString(4));
	            	obj.put("TableName",rsReservationInfo.getString(5));
	            	obj.put("Pax",rsReservationInfo.getString(6));
	            	obj.put("ResDate",rsReservationInfo.getString(7));
	            	obj.put("ResTime",rsReservationInfo.getString(8));
	            	obj.put("Comments",rsReservationInfo.getString(9));
	            	arrObj.put(obj);
	            }
	            rsReservationInfo.close();
	            jObj.put("ReservationList", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return arrObj;
	        }
	    }
		
		@GET 
		@Path("/funGetPreviousKOTDetails")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetPreviousKOTDetails(@QueryParam("TableNo") String tableNo)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        JSONArray arrObj=new JSONArray();
	        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="";
	            
	            sql = "select a.strSerialNo,a.strTableNo,a.strCardNo,ifnull(a.dblRedeemAmt,0),a.strHomeDelivery,a.strCustomerCode,a.strPOSCode,a.strItemCode,a.strItemName,"
	            		+ " a.dblItemQuantity,a.dblAmount,a.strWaiterNo,a.strKOTNo,a.intPaxNo,a.strPrintYN,a.strManualKOTNo,a.strUserCreated,a.strUserEdited,"
	            		+ " a.dteDateCreated,a.dteDateEdited,a.strOrderBefore,a.strTakeAwayYesNo,a.tdhComboItemYN,a.strDelBoyCode,a.strNCKotYN,a.strCustomerName,a.strActiveYN,"
	            		+ " a.dblBalance,a.dblCreditLimit,a.strCounterCode,a.strPromoCode,a.dblRate,a.strCardType,DATE_FORMAT(a.dteDateCreated,'%H:%i'),a.strTableStatus "
		            	+ " from tblitemrtemp a where a.strTableNo='"+tableNo+"' and a.strNCKotYN='N' group by a.strKOTNo,a.strItemCode,a.strItemName,a.strSerialNo "; 
	            	
	          	
	            System.out.println("sql="+sql);
	           
	            
	            ResultSet rsTableKOTData = st.executeQuery(sql);
	            while (rsTableKOTData.next()) 
	            {
	            	JSONObject obj=new JSONObject();
	            	obj.put("TableNo",rsTableKOTData.getString(2));
	            	obj.put("CardNo",rsTableKOTData.getString(3));
	            	obj.put("RedeemAmt",rsTableKOTData.getString(4));
	            	obj.put("HomeDelivery",rsTableKOTData.getString(5));
	            	obj.put("CustomerCode",rsTableKOTData.getString(6));
	            	obj.put("POSCode",rsTableKOTData.getString(7));
	            	obj.put("ItemCode",rsTableKOTData.getString(8));
	            	obj.put("ItemName",rsTableKOTData.getString(9));
	            	obj.put("ItemQty",rsTableKOTData.getString(10));
	            	obj.put("Amount",rsTableKOTData.getString(11));
	            	obj.put("WaiterNo",rsTableKOTData.getString(12));
	            	obj.put("KOTNo",rsTableKOTData.getString(13));
	            	obj.put("PAX",rsTableKOTData.getString(14));
	            	obj.put("PrintYN",rsTableKOTData.getString(15));
	            	obj.put("ManualKOTNo",rsTableKOTData.getString(16));
	            	obj.put("UserCreated",rsTableKOTData.getString(17));
	            	obj.put("UserEdited",rsTableKOTData.getString(18));
	            	obj.put("DateCreated",rsTableKOTData.getString(19));
	            	obj.put("DateEdited",rsTableKOTData.getString(20));
	            	obj.put("OrderBefore",rsTableKOTData.getString(21));
	            	obj.put("TakeAwayYN",rsTableKOTData.getString(22));
	            	obj.put("tdhComboItemYN",rsTableKOTData.getString(23));
	            	obj.put("DelBoyCode",rsTableKOTData.getString(24));
	            	obj.put("NCKOTYN",rsTableKOTData.getString(25));
	            	obj.put("CustomerName",rsTableKOTData.getString(26));
	            	obj.put("ActiveYN",rsTableKOTData.getString(27));
	            	obj.put("Balance",rsTableKOTData.getString(28));
	            	obj.put("CreditLimit",rsTableKOTData.getString(29));
	            	obj.put("CounterCode",rsTableKOTData.getString(30));
	            	obj.put("PromoCode",rsTableKOTData.getString(31));
	            	obj.put("Rate",rsTableKOTData.getString(32));
	            	obj.put("CardType",rsTableKOTData.getString(33));
	            	obj.put("KOTTime",rsTableKOTData.getString(34));
	            	obj.put("BillingTableStatus",rsTableKOTData.getString(35));
	            	arrObj.put(obj);
	            }
	            rsTableKOTData.close();
	            jObj.put("PreviousKOTDtls", arrObj);
	         
	            st.close();
	            cmsCon.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj; //jObj.toString();
	        }
	    }
	
		
		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funCalculatePromotion")
		@Consumes(MediaType.APPLICATION_JSON)
		public JSONObject funCalculatePromotion(JSONObject objPromotionData)
		{
			JSONObject jsonPromotionCalculation=new JSONObject();
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        Statement st2=null;
	        List <clsPromotionItems> arrListPromoItemDtl=new ArrayList<clsPromotionItems>();
			String promoItemWithAmtAndQty="",itemCode="",itemName="",posDate="",areaWisePromotions="",directBillerAreaCode="";
			double dblPrice=0.0;
		    try 
			{
				HashMap<String, clsBillItemDtl>  mapPromoItemDisc = new HashMap<>();
				Map<String, clsPromotionItems> hmPromoItem = new HashMap<String, clsPromotionItems>();
				String tableNo="",posCode="",areaCode="",operationType="";
				double quantity = 0.00, amount = 0.00,discAmt = 0, discPer = 0;
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
		        st2 = cmsCon.createStatement();
		        Map<String, clsPromotionItems> hmPromoItemDtl = null;
		        Map<String, clsPromotionItems> hmPromoItemsToDisplay = new HashMap<String, clsPromotionItems>();
	            boolean flgApplyPromoOnBill = false;
	           // String gPOSStartDate="2016-09-02";
	            String gPOSStartDate="";
	          
	            
	            List<clsBillDtl> arrListItemDtls=new ArrayList<clsBillDtl>();
	            List<clsDirectBillerItemDtl> arrDirectBillerListItemDtls=new ArrayList<clsDirectBillerItemDtl>();
				JSONArray mJsonArray=(JSONArray)objPromotionData.get("PromotionDtl");
				//posCode="P01";
				posCode=(String)objPromotionData.get("POSCode");
				tableNo=(String)objPromotionData.get("TableNo");
				gPOSStartDate=(String)objPromotionData.get("POSDate");
				//tableNo="TB0000004";
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < mJsonArray.length(); i++) 
				{
					clsBillDtl objItemDtl=new clsBillDtl();
				    mJsonObject =(JSONObject) mJsonArray.get(i);
				    objItemDtl.setStrItemCode(mJsonObject.get("itemCode").toString());
	                objItemDtl.setStrItemName(mJsonObject.get("itemName").toString());
	                objItemDtl.setDblAmount(Double.parseDouble(mJsonObject.get("amount").toString()));
	                objItemDtl.setDblQuantity(Double.parseDouble(mJsonObject.get("quantity").toString()));
	                arrListItemDtls.add(objItemDtl);
	                
	                if(tableNo.isEmpty())
	                {
	                	mJsonObject =(JSONObject) mJsonArray.get(i);
	                	clsDirectBillerItemDtl objDBItemDtl=new clsDirectBillerItemDtl(mJsonObject.get("itemName").toString(),mJsonObject.get("itemCode").toString(),Double.parseDouble(mJsonObject.get("quantity").toString()),Double.parseDouble(mJsonObject.get("amount").toString()),false, "", "N", "", dblPrice, "","");
	 				    arrDirectBillerListItemDtls.add(objDBItemDtl);
	                }
	            }
	            
				String sql="";
			    String insertQuery1="";
			    int paxNo=0;
				boolean flgData=false;
				
				sql="select strDirectAreaCode,strAreaWisePromotions "
				   + " from tblsetup where (strPOSCode='"+posCode+"' or strPOSCode='All' ) ";
				   
				System.out.println(sql);
			    ResultSet rsSetupValues=st.executeQuery(sql);
			    if(rsSetupValues.next())
			       {
			    	   directBillerAreaCode=rsSetupValues.getString(1);
			    	   areaWisePromotions=rsSetupValues.getString(2);
			       }
			    rsSetupValues.close(); 
				
			       
			    Date dt=new Date();            
	            String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate(); 
	            clsPromotionCalculation objPromoCalculation=new clsPromotionCalculation();
	            if(tableNo.isEmpty())
	            {
	            	
	            	 hmPromoItemDtl = objPromoCalculation.funCalculatePromotions("DirectBiller", "", "",new ArrayList(),gPOSStartDate,posCode,tableNo,arrDirectBillerListItemDtls,areaWisePromotions,directBillerAreaCode);
	            }
	            else
	            {
	            	String sqlAreaCode = "select strAreaCode from tbltablemaster where strTableNo='" + tableNo + "'";
	                ResultSet rsAreaCode = st2.executeQuery(sqlAreaCode);
	                if (rsAreaCode.next())
	                {
	                    areaCode = rsAreaCode.getString(1);
	                }
	                rsAreaCode.close();
	                
	            	hmPromoItemDtl = objPromoCalculation.funCalculatePromotions("MakeKOT", "", "",new ArrayList(),gPOSStartDate,posCode,tableNo,arrDirectBillerListItemDtls,areaWisePromotions,areaCode);
	            }
	            
	            
	            if(arrListItemDtls.size()>0)
	            {
	            	for(int cnt=0;cnt<arrListItemDtls.size();cnt++)
	            	{
	            		clsBillDtl objItemDtl=arrListItemDtls.get(cnt);
	            		itemCode=objItemDtl.getStrItemCode();
					    itemName=objItemDtl.getStrItemName();
					    amount=objItemDtl.getDblAmount();
					    quantity=objItemDtl.getDblQuantity();
	            		double rate = amount / quantity;
	            		double freeAmount = 0.00;
	            		String promoCode="",promoType="";
	            		double promoFreeQty=0.0,promoFreeAmt=0.0;
	            		double discount=0.0,disPer=0.0;
			            if (null != hmPromoItemDtl)
	                    {
	                        if (hmPromoItemDtl.containsKey(itemCode))
	                        {
	                            if (null != hmPromoItemDtl.get(itemCode))
	                            {
	                                clsPromotionItems objPromoItemsDtl = hmPromoItemDtl.get(itemCode);
	                                if (objPromoItemsDtl.getPromoType().equals("ItemWise"))
	                                {
	                                    double freeQty = objPromoItemsDtl.getFreeItemQty();
	                                   
	                                    if (freeQty > 0)
	                                    {
	                                        freeAmount = freeAmount + (rate * freeQty);
	                                        amount = amount - freeAmount;
	                                        hmPromoItem.put(itemCode, objPromoItemsDtl);
	                                        hmPromoItemDtl.remove(itemCode);
	                                        hmPromoItemsToDisplay.put(itemCode + "!" + itemName, objPromoItemsDtl);
	                                        promoCode = objPromoItemsDtl.getPromoCode();
		                                    promoType= objPromoItemsDtl.getPromoType();
		                                    promoFreeAmt =freeAmount;
		                                    promoFreeQty =freeQty;
		                                    
	                                    }
	                                }
	                                else if (objPromoItemsDtl.getPromoType().equals("Discount"))
	                                {
	                                    double discA = 0;
	                                    double discP = 0;
	                                    if (objPromoItemsDtl.getDiscType().equals("Value"))
	                                    {
	                                        discA = objPromoItemsDtl.getDiscAmt();
	                                        discP = (discA / amount) * 100;
	                                        promoCode = objPromoItemsDtl.getPromoCode();
		                                    promoType= objPromoItemsDtl.getPromoType();
	                                        hmPromoItem.put(itemCode, objPromoItemsDtl);
	                                        hmPromoItemDtl.remove(itemCode);
	                                        hmPromoItemsToDisplay.put(itemCode + "!" + itemName, objPromoItemsDtl);

	                                        clsBillItemDtl objItemPromoDiscount = new clsBillItemDtl();
	                                        objItemPromoDiscount.setItemCode(itemCode);
	                                        objItemPromoDiscount.setItemName(itemName);
	                                        objItemPromoDiscount.setDiscountAmount(discA);
	                                        objItemPromoDiscount.setDiscountPercentage(discP);
	                                        objItemPromoDiscount.setAmount(amount);
	                                        discount=discA;
	                                        disPer=discP;
	                                        mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
	                                    }
	                                    else
	                                    {
	                                        discP = objPromoItemsDtl.getDiscPer();
	                                        discA = (discP / 100) * amount;
	                                        promoCode = objPromoItemsDtl.getPromoCode();
		                                    promoType= objPromoItemsDtl.getPromoType();
	                                        hmPromoItem.put(itemCode, objPromoItemsDtl);
	                                        hmPromoItemsToDisplay.put(itemCode + "!" + itemName, objPromoItemsDtl);
	                                        hmPromoItemDtl.remove(itemCode);
	                                        clsBillItemDtl objItemPromoDiscount = new clsBillItemDtl();
	                                        objItemPromoDiscount.setItemCode(itemCode);
	                                        objItemPromoDiscount.setItemName(itemName);
	                                        objItemPromoDiscount.setDiscountAmount(discA);
	                                        objItemPromoDiscount.setDiscountPercentage(discP);
	                                        objItemPromoDiscount.setAmount(amount);
	                                        discount=discA;
	                                        disPer=discP;
	                                        mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
	                                    }
	                                }
	                            }
	                        }
	                        if(promoItemWithAmtAndQty.isEmpty())
	                        {
	                        	promoItemWithAmtAndQty=itemCode+"#"+itemName+"#"+String.valueOf(amount)+"#"+String.valueOf(quantity)+"#"+promoCode+"#"+promoType+"#"+String.valueOf(promoFreeAmt)+"#"+String.valueOf(promoFreeQty)+"#"+String.valueOf(discount)+"#"+String.valueOf(disPer);
	                        }
	                        else
	                        {
	                        	promoItemWithAmtAndQty=promoItemWithAmtAndQty+"!"+itemCode+"#"+itemName+"#"+String.valueOf(amount)+"#"+String.valueOf(quantity)+"#"+promoCode+"#"+promoType+"#"+String.valueOf(promoFreeAmt)+"#"+String.valueOf(promoFreeQty)+"#"+String.valueOf(discount)+"#"+String.valueOf(disPer);
	                        }
	                        
	                    
	                    }
			        }
	            }
	            jsonPromotionCalculation.put("promoDtl", promoItemWithAmtAndQty);
	            cmsCon.close();
	            st.close();
	            rsSetupValues.close();
	            
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   return jsonPromotionCalculation;//Response.status(201).entity(promoItemWithAmtAndQty).build();		
		}
		
		
		
		
		
		
		@GET 
		@Path("/funPostPMSSettleData")
		@Produces(MediaType.APPLICATION_JSON)
		public String funPOSTRoomSettlementDtlToPMS(@QueryParam("ClientCode") String clientCode,@QueryParam("BillNo") String billNo,
				@QueryParam("POSCode") String POSCode,@QueryParam("POSDate") String POSDate,@QueryParam("SettleAmt") String SettleAmt
				,@QueryParam("FolioNo") String FolioNo,@QueryParam("RoomNo") String RoomNo,@QueryParam("GuestNo") String GuestNo)
		{

			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null,pmsCon=null;;
	        Statement st=null,st1=null;
	        JSONObject jObj=new JSONObject();
	        StringBuilder sbSql=new StringBuilder();
	        String response="false",updateBills="";
	        
	        
			
	        try {
	        	aposCon=objDb.funOpenAPOSCon("mysql","master");
	            st = aposCon.createStatement();
	            pmsCon=objDb.funOpenWebPMSCon("mysql","master");
	            st1 = pmsCon.createStatement();
	            String sql="";
	            JSONArray arrObj = new JSONArray();
	            
	            String sqlInsertPMSDtl = "insert into tblpmspostingbilldtl (strBillNo,strPOSCode,dteBillDate,dblSettleAmt"
                        + ",strFolioNo,strGuestCode,strRoomNo,strClientCode,strDataPostFlag,strPMSDataPostFlag) "
                        + "values ('" + billNo + "','" + POSCode + "','" + POSDate + "','" + Double.valueOf(SettleAmt) + "'"
                        + ",'" + FolioNo + "','" + GuestNo + "','" + RoomNo + "'"
                        + ",'" + clientCode + "','N','N')";
                System.out.println(sqlInsertPMSDtl);
                st.execute(sqlInsertPMSDtl);
	            
	            sql="select * from tblpmspostingbilldtl where strPMSDataPostFlag='N'";
	            ResultSet rs=st.executeQuery(sql);
	            while(rs.next())
	            {
	            	updateBills += ",'" + rs.getString(1) + "'";
	                JSONObject objFolioDtl = new JSONObject();
	                objFolioDtl.put("BillNo", rs.getString(1));
	                objFolioDtl.put("POSCode", rs.getString(2));
	                objFolioDtl.put("BillDate", rs.getString(3));
	                objFolioDtl.put("SettledAmt", rs.getString(4));
	                objFolioDtl.put("FolioNo", rs.getString(5));
	                objFolioDtl.put("GuestCode", rs.getString(6));
	                objFolioDtl.put("RoomNo", rs.getString(7));
	                arrObj.put(objFolioDtl);
	            }
	            rs.close();
	                    
	            jObj.put("FolioDtl", arrObj);
	            
	            
	            JSONArray mJsonArray = (JSONArray) jObj.get("FolioDtl");
				if(mJsonArray.length()>0)
				{
					for (int i = 0; i < mJsonArray.length(); i++) 
					{
						JSONObject mJsonObject =(JSONObject) mJsonArray.get(i);
				            
						billNo=mJsonObject.get("BillNo").toString().trim();
						POSCode=mJsonObject.get("POSCode").toString().trim();
						String billDate=mJsonObject.get("BillDate").toString().trim();
						String folioNo=mJsonObject.get("FolioNo").toString().trim();
						String guestCode=mJsonObject.get("GuestCode").toString().trim();
						String roomNo=mJsonObject.get("RoomNo").toString().trim();
						String settledAmt=mJsonObject.get("SettledAmt").toString().trim();
						
						
						sbSql.setLength(0);
						sbSql.append("delete from tblfoliodtl where strFolioNo='"+folioNo+"' and strDocNo='"+billNo+"' "
								+ "and strClientCode='"+clientCode+"'");
						st1.execute(sbSql.toString());
						
						sbSql.setLength(0);
						sbSql.append("insert into tblfoliodtl (strFolioNo,dteDocDate,strDocNo,strPerticulars"
							+ ",dblDebitAmt,dblCreditAmt,dblBalanceAmt,strRevenueType,strRevenueCode,strClientCode) "
							+ "values ('"+folioNo+"','"+billDate+"','"+billNo+"','POS Items',"+settledAmt+",0,0"
									+ ",'POS','"+POSCode+"','"+clientCode+"')");
						st1.execute(sbSql.toString());
					}
					response = "true";
				}
				
				
				
				if (response.equals("true"))
	            {
	                StringBuilder sbUpdate = new StringBuilder(updateBills);
	                if (updateBills.length() > 0)
	                {
	                    updateBills = sbUpdate.delete(0, 1).toString();
	                    sql="update tblpmspostingbilldtl set strPMSDataPostFlag='Y' where strBillNo in (" + updateBills + ")";
	    	        	st.executeUpdate(sql);
	                }
	            }
				aposCon.close();
				st.close();
				st1.close();
				rs.close();
	              
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj.toString();
	        }
	    }
		
		
		
		
		@GET
		@Path("/funGetPOS")
		@Produces(MediaType.APPLICATION_JSON)
		public String funGetPOS()
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			String sql=" select a.strPosCode, a.strPosName from tblposmaster a ";
			JSONArray arrObj=new JSONArray();
			
			 try
		        {
				 aposCon=objDb.funOpenAPOSCon("mysql","master");
		            st = aposCon.createStatement();
		            
		            ResultSet rsMasterData=st.executeQuery(sql);
	                while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strPosCode",rsMasterData.getString(1));
	        	        obj.put("strPosName",rsMasterData.getString(2));
	        	       
	        	        arrObj.put(obj);
	                }
	                rsMasterData.close();
	        
	                jObj.put("posList", arrObj);
	                st.close();
	                aposCon.close();
	        
		     }catch(Exception e)
			 {
		    	 e.printStackTrace();
			 }
			 return jObj.toString();
		}

		
		@GET
		@Path("/funGetPrinterList")
		@Produces(MediaType.APPLICATION_JSON)
		public String funGetPrinterList()
		{
			 JSONObject jObj=new JSONObject();
			 List<String> printerList=new ArrayList<String>();
			 try
		        {
		            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE; // MY FILE IS .txt TYPE
		            PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras);
		            printerList.add("");
		            for (int i = 0; i < printService.length; i++)
		            {
		                //System.out.println("Printer Names= "+printService[i].getName());
		                printerList.add(printService[i].getName());
		            }
		            jObj.put("printerList", printerList);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
	 
			 return jObj.toString();
		}

		
	
		@GET
	   	@Path("/funGetPOSDate")
	   	@Produces(MediaType.APPLICATION_JSON)
	    public String funGetPOSDate(@QueryParam("POSCode") String posCode)
	    {
	    	return funGetPOSTransactionDate(posCode);
	    }
	    
	    private String funGetPOSTransactionDate(String posCode)
	    {
	    	JSONObject objJSON=new JSONObject();
	    	String posDate="";
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection posCon=null;
	        Statement st=null;
	        
	        try
	        {        	
	        	posCon=objDb.funOpenAPOSCon("mysql","master");
		        st = posCon.createStatement();
	            
	            long code = 0;
	            String sql_getPOSDate = "select a.dtePOSDate from tbldayendprocess a "
	            		+ " where a.strPosCode='"+posCode+"' and a.strDayEnd='N' ";
	            System.out.println(sql_getPOSDate);
	            ResultSet rsGetPOSDate = st.executeQuery(sql_getPOSDate);

	            if (rsGetPOSDate.next())
	            {
	                posDate =rsGetPOSDate.getString(1);
	                System.out.println(posDate);
	            }
	            objJSON.put("POSDate", posDate);
	            
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
		
		
		
	    
	    @GET 
		@Path("/funCheckPOSLicenceKey")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funCheckPOSLicenceKey(@QueryParam("strClientCode") String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        String response="";

	        JSONArray arrObj=new JSONArray();
	        Date objDate = new Date();
	        int day = objDate.getDate();
	        int month = objDate.getMonth() + 1;
	        int year = objDate.getYear() + 1900;
	        String currentDate = year + "-" + month + "-" + day;
	        
	        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="";
	            
	            sql = "select strClientCode,strClientName from tblsetup;";
	            ResultSet rs = st.executeQuery(sql);
	            if (rs.next())
	            {
	                String tempClientCode = rs.getString(1);
	                String clientName = rs.getString(2);
	                
	                clsClientDetails.funAddClientCodeAndName();
	                Date POSExpiryDate = clsClientDetails.funCheckPOSLicense(tempClientCode, clientName);
	                if(null==POSExpiryDate)
	                {
	                	JSONObject obj=new JSONObject();
	                    obj.put("Status", "Invalid");
	                    obj.put("Msg", "Invalid POS. Please Contact Technical Support.");
	                    arrObj.put(obj);
	                }
	                else
	                {
		                long ExpiryDateTime = POSExpiryDate.getTime();
		                long TimeDifference = 0;
		                String billDate = "";
		                String sqlMaxBillDate = "select ifnull(max(date(dteBillDate)),0) from tblqbillhd";
		                ResultSet rsMaxBillDate = st.executeQuery(sqlMaxBillDate);
		                if (rsMaxBillDate.next())
		                {
		                	SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		                    Date systemDate = dFormat.parse(dFormat.format(new Date()));
		                	billDate = rsMaxBillDate.getString(1);
		                    if (!billDate.equals("0"))
		                    {
		                    	Date gMaxBillDate = dFormat.parse(billDate);
		                        TimeDifference = ExpiryDateTime - gMaxBillDate.getTime();
		                        long diffDays = TimeDifference / (24 * 60 * 60 * 1000);
		                        if (diffDays <= 15)
		                        {
		                        	JSONObject obj=new JSONObject();
		                            obj.put("Status", "MinDays");
		                            obj.put("Msg", diffDays + " Days Remaining For Licence to Expire");
		                            arrObj.put(obj);
		                        	//new frmOkPopUp(null, +diffDays + " Days Remaining For Licence to Expire", " Licence is Expired ", 1).setVisible(true);
		                        }
		                    }
		                    else
		                    {
		                    	TimeDifference = ExpiryDateTime - systemDate.getTime();
		                    }
		                    
		                    if (TimeDifference >= 0)
		                    {
		                    	JSONObject obj=new JSONObject();
		                        obj.put("Status", "Valid");
		                        obj.put("Msg","");
		                        arrObj.put(obj);
		                    }
		                    else
		                    {
		                    	JSONObject obj=new JSONObject();
		                        obj.put("Status", "Expired");
		                        obj.put("Msg", "License Expired. Please Contact Technical Support.");
		                        arrObj.put(obj);
		                        //new frmOkPopUp(null, "Please Contact Technical Support ", " Licence is Expired ", 1).setVisible(true);
		                    }
		                }
		                rsMaxBillDate.close();
	                }
	            }
	            else
	            {
	            	//new frmOkPopUp(null, "Invalid POS Please Contact Technical Support ", "Error", 1).setVisible(true);
	                JSONObject obj=new JSONObject();
	                obj.put("Status", "Invalid");
	                obj.put("Msg", "Invalid POS. Please Contact Technical Support.");
	                arrObj.put(obj);
	            }
	          
		        jObj.put("CheckLicence", arrObj);
		        st.close();
		        cmsCon.close();
		            
	        } catch (Exception e) {
		        e.printStackTrace();
		    }
		    finally
		    {
		        return Response.status(201).entity(jObj).build();
		    }
		}
	
	    @GET
		@Path("/funGetAllUserName")
		@Produces(MediaType.APPLICATION_JSON)
		public String funGetAllUserName()
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			String sql=" select strUserCode, strUserName from tbluserhd ";
			JSONArray arrObj=new JSONArray();
			
			 try
		        {
				 aposCon=objDb.funOpenAPOSCon("mysql","master");
		            st = aposCon.createStatement();
		            
		            ResultSet rsMasterData=st.executeQuery(sql);
		            while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strUserCode",rsMasterData.getString(1));
	        	        obj.put("strUserName",rsMasterData.getString(2));
	        	       
	        	        arrObj.put(obj);
	                }
	                rsMasterData.close();
	        
	                jObj.put("userList", arrObj);
	                st.close();
	                aposCon.close();
	        
		     }catch(Exception e)
			 {
		    	 e.printStackTrace();
			 }
			 return jObj.toString();
		}

		
	    @GET
	  		@Path("/funGetAllSettlement")
	  		@Produces(MediaType.APPLICATION_JSON)
	  		public String funGetAllSettlement()
	  		{
	  			clsDatabaseConnection objDb=new clsDatabaseConnection();
	  	        Connection aposCon=null;
	  	        Statement st=null;
	  	        JSONObject jObj=new JSONObject();
	  			String sql=" select strSettelmentCode,strSettelmentType from tblsettelmenthd ";
	  			JSONArray arrObj=new JSONArray();
	  			
	  			 try
	  		        {
	  				 aposCon=objDb.funOpenAPOSCon("mysql","master");
	  		            st = aposCon.createStatement();
	  		            
	  		            ResultSet rsMasterData=st.executeQuery(sql);
	  		          while(rsMasterData.next())
		                {
		        	        JSONObject obj=new JSONObject();
		        	        obj.put("strSettelmentCode",rsMasterData.getString(1));
		        	        obj.put("strSettlementName",rsMasterData.getString(2));
		        	       
		        	        arrObj.put(obj);
		                }
	  	                rsMasterData.close();
	  	        
	  	                jObj.put("settlementList", arrObj);
	  	                st.close();
	  	                aposCon.close();
	  	        
	  		     }catch(Exception e)
	  			 {
	  		    	 e.printStackTrace();
	  			 }
	  			 return jObj.toString();
	  		}
	    
	    @GET
  		@Path("/funGetAllCostCenter")
  		@Produces(MediaType.APPLICATION_JSON)
  		public String funGetAllCostCenter()
  		{
  			clsDatabaseConnection objDb=new clsDatabaseConnection();
  	        Connection aposCon=null;
  	        Statement st=null;
  	        JSONObject jObj=new JSONObject();
  			String sql=" select strCostCenterCode, strCostCenterName from tblcostcentermaster ";
  			JSONArray arrObj=new JSONArray();
  			
  			 try
  		        {
  				 aposCon=objDb.funOpenAPOSCon("mysql","master");
  		            st = aposCon.createStatement();
  		            
  		            ResultSet rsMasterData=st.executeQuery(sql);
  		          while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strCostCenterCode",rsMasterData.getString(1));
	        	        obj.put("strCostCenterName",rsMasterData.getString(2));
	        	       
	        	        arrObj.put(obj);
	                }
  	                rsMasterData.close();
  	        
  	                jObj.put("costCenterList", arrObj);
  	                st.close();
  	                aposCon.close();
  	        
  		     }catch(Exception e)
  			 {
  		    	 e.printStackTrace();
  			 }
  			 return jObj.toString();
  		}
	    
	    @GET
  		@Path("/funGetAllAdvOrderType")
  		@Produces(MediaType.APPLICATION_JSON)
  		public String funGetAllAdvOrderType()
  		{
  			clsDatabaseConnection objDb=new clsDatabaseConnection();
  	        Connection aposCon=null;
  	        Statement st=null;
  	        JSONObject jObj=new JSONObject();
  			String sql=" select strAdvOrderTypeCode,strAdvOrderTypeName from tbladvanceordertypemaster where strOperational='Yes' ";
  			JSONArray arrObj=new JSONArray();
  			
  			 try
  		        {
  				 aposCon=objDb.funOpenAPOSCon("mysql","master");
  		            st = aposCon.createStatement();
  		            
  		            ResultSet rsMasterData=st.executeQuery(sql);
  		          while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strAdvOrderTypeCode",rsMasterData.getString(1));
	        	        obj.put("strAdvOrderTypeName",rsMasterData.getString(2));
	        	       
	        	        arrObj.put(obj);
	                }
  	                rsMasterData.close();
  	        
  	                jObj.put("advOrderList", arrObj);
  	                st.close();
  	                aposCon.close();
  	        
  		     }catch(Exception e)
  			 {
  		    	 e.printStackTrace();
  			 }
  			 return jObj.toString();
  		}
	    
	 
	    @GET
		@Path("/funGetCostCenter")
		@Produces(MediaType.APPLICATION_JSON)
		 public String funCostCenterName()
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection aposCon=null;
		        Statement st=null;
		        JSONObject jObj=new JSONObject();
				String sql=" select strCostCenterCode, strCostCenterName from tblcostcentermaster ";
				JSONArray arrObj=new JSONArray();
				
				 try
			        {
					 aposCon=objDb.funOpenAPOSCon("mysql","master");
			            st = aposCon.createStatement();
			            
			            ResultSet rsMasterData=st.executeQuery(sql);
		                while(rsMasterData.next())
		                {
		        	        JSONObject obj=new JSONObject();
		        	        obj.put("strCostCenterCode",rsMasterData.getString(1));
		        	        obj.put("strCostCenterName",rsMasterData.getString(2));
		        	        
		        	       
		        	        arrObj.put(obj);
		                }
		                rsMasterData.close();
		        
		                jObj.put("CostCenterList", arrObj);
		                st.close();
		                aposCon.close();
		        
			     }catch(Exception e)
				 {
			    	 e.printStackTrace();
				 }
				 return jObj.toString();
			}    
	    
	    
	    
	    @GET 
		@Path("/funGetTerminalRegistrationDetails")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetTerminalRegistrationDetails(@QueryParam("MacAddress") String macAddress,@QueryParam("TerminalName") String terminalName,@QueryParam("ClientCode") String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        try {
	        	aposCon=objDb.funOpenAPOSCon("mysql","master");
	            st = aposCon.createStatement();
	            String sql="";
	           
	            
	            sql = " select count(strMACAddress) from tblregisterterminal  "
                        + " where strClientCode='" + clientCode + "' and strTerminalName='"+terminalName+"'";
	            ResultSet rs = st.executeQuery(sql);
	            if (rs.next()) 
	            {
	            	jObj.put("MaxTerminal",rs.getInt(1));
	            }
		        else
		        {
	              
		        	jObj.put("MaxTerminal",0);
            	   sql = " select count(strMACAddress) from tblregisterterminal  "
                        + " where strClientCode='" + clientCode + "' and strTerminalName='SPOS'";
            	   rs = st.executeQuery(sql);
		            if (rs.next()) 
		            {
		            	
		            }
		            else
		            {
	            	 sql = " CREATE TABLE `tblregisterterminal` ( "
                        +"	`strClientCode` VARCHAR(20) NOT NULL, "
                        +"	`strHOSTName` VARCHAR(30) NOT NULL, "
                        +"	`strMACAddress` VARCHAR(20) NOT NULL, "
                        + " `strTerminalName` VARCHAR(10) NOT NULL, "
                        + " `strUserCreated` VARCHAR(10) NOT NULL, "
                        +"	`dteDateCreated` DATETIME NOT NULL, "
                        +"	`strDataPostFlag` VARCHAR(1) NOT NULL DEFAULT 'N', "
                        +"	PRIMARY KEY (`strClientCode`, `strMACAddress`) "
                        +") "
                        +"COLLATE='utf8_general_ci' "
                        +"ENGINE=InnoDB "
                        +";";
	            	  st.executeUpdate(sql);
		            }
		           }
	            rs.close();
            	sql = " select * from tblregisterterminal where strClientCode='" + clientCode + "' "
                    + " and strMACAddress='" + macAddress + "' and strTerminalName='"+terminalName+"' ";
            	rs = st.executeQuery(sql);
	            if (rs.next()) 
	            {
	            	jObj.put("RegisterTerminal","True");
	            }
	            else
	            {
	            	jObj.put("RegisterTerminal","False");
	            }
	            rs.close();
	            
	            System.out.println("Terminal Details= "+jObj);
	            
	            st.close();
	            aposCon.close();
	            	            
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj;
	        }
	    }
		
	    
	    
	    @GET 
		@Path("/funRegisterTerminal")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funRegisterTerminal(@QueryParam("HostName") String hostName,@QueryParam("MacAddress") String macAddress,@QueryParam("ClientCode") String clientCode,@QueryParam("UserCode") String userCode,@QueryParam("TerminalName") String terminalName)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        String status="Fail";
	        JSONObject jObj=new JSONObject();
	        JSONArray arrObj=new JSONArray();
	        try {
	        	aposCon=objDb.funOpenAPOSCon("mysql","master");
	            st = aposCon.createStatement();
	            String sql="";
	            Date objDate = new Date();
	            int day = objDate.getDate();
	            int month = objDate.getMonth() + 1;
	            int year = objDate.getYear() + 1900;
	            String currentDate = year + "-" + month + "-" + day;
	            sql = " insert into tblregisterterminal(strClientCode,strHOSTName,strMACAddress,"
	            		+ " strTerminalName,strUserCreated,dteDateCreated,strDataPostFlag) "
                        + " values('" + clientCode + "','" + hostName + "','" + macAddress + "',"
                        + " '"+terminalName+"','"+userCode+"','"+currentDate+"','N') ";
		        st.executeUpdate(sql);
		        st.close();
	            aposCon.close();
	           // status="Success";
	            jObj.put("Status", "Success");
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	            jObj.put("Status", "Error");
	        }
	        finally
	        {
	        	//return Response.status(201).entity(status).build();
	        	return jObj;
	        }
	    }
	    
	    
	    
	    @GET
		   @Path("/funGenerateCheckKOTTextFile")
		   @Produces(MediaType.APPLICATION_JSON)
		   public JSONObject funGenerateCheckKOTTextFile(@QueryParam("tableNo") String tableNo,@QueryParam("POSCode") String posCode,@QueryParam("POSName") String posName)
		   {
	    	   //String result="";
	    	   return funCheckKOTDetailsToTextFile(tableNo,posCode,posName,"","Y","CHECK KOT"); 
		   }
			
		   
		   
	    
	    private JSONObject funCheckKOTDetailsToTextFile(String tableNo,String POSCode,String POSName,String Reprint,String printYN,String kotType) 
		   {       
	    	   JSONObject jObj=new JSONObject();
	    	   String result="";
			   clsDatabaseConnection objDb=new clsDatabaseConnection();
		       Connection aposCon=null;
		       Statement st=null;
		       clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
		       String line = "  --------------------------------------",tableName="",waiterName="",CostCenterName="",createdDate="";
			   int pax=0;
			   ArrayList<String>listKOT=new ArrayList<String>();
		       try 
		       {
		    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
			       st = aposCon.createStatement();
		           
			       // Get Setup Values for Printing.
			       
			       String areaWisePricing="N";
			       String printModQtyOnKOT="N";
			       String noOfLinesInKOTPrint="1";
			       String printShortNameOnKOT="N";
			       String printKOTYN="Y";
			       String multipleKOTPrint="N";
			       String multipleBillPrint="N";
			       String clientCode="";
			       String printKOTPrinter="N";
			       int columnSize=40;
			       
			       String AreaCodeForAll = "";
		            String sql = "select strAreaCode from tblareamaster where strAreaName='All';";                   
		            ResultSet rsAreaCode = st.executeQuery(sql);
		            if (rsAreaCode.next()) {
		                AreaCodeForAll = rsAreaCode.getString(1);
		            }
		            rsAreaCode.close();
				   
			       
			       sql="select strAreaWisePricing,strPrintShortNameOnKOT,strPrintModifierQtyOnKOT,strNoOfLinesInKOTPrint"
				       		+ ",strMultipleKOTPrintYN,strPrintKOTYN,strMultipleBillPrinting,intColumnSize,strClientCode,strKOTToLocalPrinter,strConsolidatedKOTPrinterPort "
				       		+ "from tblsetup where (strPOSCode='"+POSCode+"' or strPOSCode='All' ) ";
				   System.out.println(sql);
				       ResultSet rsSetupValues=st.executeQuery(sql);
				       if(rsSetupValues.next())
				       {
				    	   areaWisePricing=rsSetupValues.getString(1);
				    	   printShortNameOnKOT=rsSetupValues.getString(2);
					       printModQtyOnKOT=rsSetupValues.getString(3);
					       noOfLinesInKOTPrint=rsSetupValues.getString(4);
					       multipleKOTPrint=rsSetupValues.getString(5);
					       printKOTYN=rsSetupValues.getString(6);
					       multipleBillPrint=rsSetupValues.getString(7);
					       columnSize=rsSetupValues.getInt(8);
					       clientCode=rsSetupValues.getString(9);
					       printKOTPrinter=rsSetupValues.getString(11);
				       }
			       
			       if(kotType.equals("CHECK KOT"))
			       {
			    	  sql = "select strBillPrinterPort from tblposmaster where strPOSCode='" + POSCode + "'";
				        ResultSet rs = st.executeQuery(sql);
					    if (rs.next())
					    {
					    	printKOTPrinter=rs.getString(1);
					    }
					    rs.close();
			       }
			        
			       
			       
			       if(!printKOTPrinter.isEmpty())
			       {
			    	   funCreateTempFolder();
			           String filePath = System.getProperty("user.dir");
			           File textKOTFilePath;
			           if(kotType.equals("CHECK KOT"))
			           {
			        	   textKOTFilePath = new File(filePath + "/Temp/Temp_Check_KOT.txt");
			           }
			           else
			           {
			        	   textKOTFilePath = new File(filePath + "/Temp/Temp_Consolidated_KOT.txt");
			           }
			           System.out.println(textKOTFilePath.getAbsolutePath());
			           FileWriter fstream = new FileWriter(textKOTFilePath);
			           //BufferedWriter KotOut = new BufferedWriter(fstream);
			           BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textKOTFilePath),"UTF8"));
			           if ("Reprint".equalsIgnoreCase(Reprint)) 
			           {
			        	   objFileIOUtil.funPrintBlankSpace("DUPLICATE", KotOut, columnSize);
			               KotOut.write("DUPLICATE");
			               KotOut.newLine();
			           }
			           
			           
			           sql = " select a.dteDateCreated,a.strKOTNo,c.strTableName,c.intPaxNo,a.strWaiterNo,ifnull(b.strWShortName,'') "
		    	           	   + " from tblitemrtemp a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo,tbltablemaster c "
		    	           	   + " where a.strTableNo=c.strTableNo "
		    	           	   + " and a.strTableNo='"+tableNo+"' "
		    	           	   + " and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) OR c.strAreaCode ='"+AreaCodeForAll+"')"
		    	           	   + " and a.strPOSCode='"+POSCode+"' and c.strOperational='Y' "
		    	           	   + " group by a.strKOTNo "
		    	           	   + " order by a.strKOTNo,a.strSerialNo";
			           
				           st.close();
				           st=aposCon.createStatement();
				           ResultSet rs = st.executeQuery(sql);
				           while (rs.next()) 
				           {
				               tableName = rs.getString(3);
				               pax = rs.getInt(4);
				               waiterName=rs.getString(6);
				               createdDate=rs.getString(1);
				               listKOT.add(rs.getString(2));
				           }
			               rs.close();
			           
			           if(kotType.equals("CHECK KOT"))
				        {
			        	   objFileIOUtil.funPrintBlankSpace("CHECK KOT", KotOut, columnSize);
				           KotOut.write("CHECK KOT");
				           
				        }
				       else
				       {
				    	   objFileIOUtil.funPrintBlankSpace("Consolidated KOT", KotOut, columnSize);
				           KotOut.write("Consolidated KOT");
				           
				       }
			           
			           KotOut.newLine();
			           objFileIOUtil.funPrintBlankSpace(POSName, KotOut, columnSize);
			           KotOut.write(POSName);
			           KotOut.newLine();

			           System.out.println(pax);	           
			           KotOut.write(line);
			           KotOut.newLine();
			           KotOut.write("  TABLE NO :");
			           KotOut.write(tableName + "  ");
			           KotOut.write(" PAX   :");
			           System.out.println(pax);
			           KotOut.write(String.valueOf(pax));
			           System.out.println(pax);
			           KotOut.newLine();
			           KotOut.write("  WAITER NAME:" + "   " + waiterName);
		               KotOut.newLine();
		               KotOut.write("  DATE & TIME:" + createdDate);
			           
			           KotOut.newLine();
			           KotOut.write(line);
			           KotOut.newLine();
			           
			           
			           String itemName="c.strItemName";
			           if(printShortNameOnKOT.equals("Y"))
			           {
			               itemName="b.strShortName";
			           }
			           
			          // if(listKOT.size()>0)
			          // {
			        	 //  for(int cnt=0;cnt<listKOT.size();cnt++)
			        	 //  {
			        		   if(areaWisePricing.equals("Y"))
			    	           {
			    	               sql = " select LEFT(a.strItemCode,7),"+itemName+",sum(a.dblItemQuantity),d.strCostCenterCode,d.strPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName,a.strNCKotYN  "
			    	               	   + " from tblitemrtemp a left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode  "
			    	               	   + " left outer join tblcostcentermaster d on c.strCostCenterCode=d.strCostCenterCode,tblitemmaster b  "
			    	               	  // + " where a.strKOTNo='"+listKOT.get(cnt)+"' and a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"'  "
			    	               	 + " where  a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"' and c.strHourlyPricing='No' " 
			    	               	  + " and a.strItemCode=b.strItemCode and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ))  "
			    	               	   + " group by a.strItemCode";
			    	           }
			    	           else
			    	           {
			    	        	   sql = " select LEFT(a.strItemCode,7),"+itemName+",sum(a.dblItemQuantity),d.strCostCenterCode,d.strPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName,a.strNCKotYN  "
				    	               	   + " from tblitemrtemp a left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode  "
				    	               	   + " left outer join tblcostcentermaster d on c.strCostCenterCode=d.strCostCenterCode,tblitemmaster b  "
				    	               	  // + " where a.strKOTNo='"+listKOT.get(cnt)+"' and a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"'  "
				    	               	   + " where a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"' and c.strHourlyPricing='No' "
				    	               	   + " and a.strItemCode=b.strItemCode and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' )  OR c.strAreaCode ='"+AreaCodeForAll+"')  "
				    	               	   + " group by a.strItemCode";;
			    	           }
			    	           System.out.println(sql);
			    	           st.close();
			    	           st=aposCon.createStatement();
			    	           ResultSet rsKOTDetails = st.executeQuery(sql);
			    	           int count=0;
			    	           while (rsKOTDetails.next()) 
			    	           {
			    	        	   if(count==0)
			    	        	   {
			    	        		   /*KotOut.newLine();
			        	               KotOut.write("  KOT NO     :");
			        		           KotOut.write(listKOT.get(cnt) + "  ");
			        		           KotOut.newLine();
			        		           KotOut.write("  CostCenter Name     :");
			        		           KotOut.write(rsKOTDetails.getString(7) + "  ");
			        		           KotOut.newLine();
			        		           KotOut.write(line);
			        		           */
			        		           KotOut.newLine();
			        		           KotOut.write("  QTY         ITEM NAME  ");
			        		           KotOut.newLine();
			        		           KotOut.write(line);
			        		           KotOut.newLine();
			    	        	   }
			    	        	   
			    	        	   String itemqty = rsKOTDetails.getString(3);
			    	               if (itemqty.length() == 5) 
			    	               {
			    	                   KotOut.write(" " + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
			    	               }
			    	               else if (itemqty.length() == 4) 
			    	               {
			    	                   KotOut.write("  " + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
			    	               }
			    	               else if (itemqty.length() == 6) 
			    	               {
			    	                   KotOut.write("" + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
			    	               }
			    	               String itemCode=rsKOTDetails.getString(1);
			    	               String serialNo=rsKOTDetails.getString(5);
			    	               System.out.println("itemCode="+itemCode);
			    	               
			    	               String sqlModifier="select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
			    		                   //+ " where a.strItemCode like'"+rsKOTDetails.getString(1)+"M%' and a.strKOTNo='"+listKOT.get(cnt)+"' "
			    		                    + " where a.strItemCode like'"+rsKOTDetails.getString(1)+"M%'  "
			    		                   //+ " and strSerialNo like'"+rsKOTDetails.getString(5)+".%' "
			    		                   + " group by a.strItemCode,a.strItemName ";
			    	               System.out.println("sqlModifier="+sqlModifier);
			    	               
			    		           Statement st2=aposCon.createStatement();
			    	               ResultSet rsModifierItems=st2.executeQuery(sqlModifier);
			    	               while(rsModifierItems.next())
			    	               {
			    	            	   KotOut.newLine();
			    	                   String modQty=rsModifierItems.getString(2);
			    	                   String modifierName=rsModifierItems.getString(1);
			    	                       if(printModQtyOnKOT.equals("Y"))
			    	                       {
			    	                           if (modQty.length() == 5) 
			    	                           {
			    	                               KotOut.write(" " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 4) 
			    	                           {
			    	                               KotOut.write("  " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 6) 
			    	                           {
			    	                               KotOut.write("" + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
			    	                           }
			    	                       }
			    	                       else
			    	                       {
			    	                           if (modQty.length() == 5) 
			    	                           {
			    	                               KotOut.write("            " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 4) 
			    	                           {
			    	                               KotOut.write("             " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 6) 
			    	                           {
			    	                               KotOut.write("           " + rsModifierItems.getString(1));
			    	                           }
			    	                       }
			    	               }
			    	               st2.close();
			    	               rsModifierItems.close();
			    	               KotOut.newLine();
			    	               count++;
			    	               
			    	           }
			    	           rsKOTDetails.close();
			    	           KotOut.newLine();
			    	           KotOut.write(line);
			        	//   }
			          // }
			           
			           
			           for(int cntLines=0;cntLines<Integer.parseInt(noOfLinesInKOTPrint);cntLines++)
			           {
			               KotOut.newLine();
			           }
			           
			           KotOut.write("m");//windows
			           KotOut.close();
			           fstream.flush();
			           fstream.close();
			           
			           if(printYN.equals("Y"))
			           {
			        	   if(kotType.equals("CHECK KOT"))
				           {
			        		   result=obTextFileGenerator.funPrintKOTTextFile(printKOTPrinter, "", "kot", "", printKOTYN, "N","CheckKOT",0,0,"N",Reprint);
				           }
				           else
				           {
				        	   result=obTextFileGenerator.funPrintKOTTextFile(printKOTPrinter, "", "kot", "",printKOTYN, "N","ConsolidateKOT",0,0,"N",Reprint);
				           }
			        	   
			           }
			    	   
			       }
			       jObj.put("result", result);
		           
		       } 
		       catch (Exception e) 
		       {
		           
		           e.printStackTrace();
		       }
		       return jObj;
		   }
	


    @SuppressWarnings("rawtypes")
	@POST
	@Path("/funSaveNonAvailableItem")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveNonAvailableItem(JSONObject objKOTData)
	{
   	   clsDatabaseConnection objDb=new clsDatabaseConnection();
       Connection cmsCon=null;
       Statement st=null;
       Statement st2=null;
	   String result="false";
	   int exe=0;
		
		try {
			
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        st2 = cmsCon.createStatement();
			
			JSONArray mJsonArray=(JSONArray)objKOTData.get("NonAvailableItemDtl");
			String sql="";
		    String insertQuery1="";
		    int paxNo=0;
			boolean flgData=false;
			JSONObject mJsonObject = new JSONObject();
			
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
			    mJsonObject =(JSONObject) mJsonArray.get(i);
			    String itemCode=mJsonObject.get("strItemCode").toString();
			    String itemName=mJsonObject.get("strItemName").toString();
			    String clientCode=mJsonObject.get("strClientCode").toString();
			    String posCode=mJsonObject.get("strPOSCode").toString();
			    String date=mJsonObject.get("dteDate").toString();
			    
			    sql="insert into tblnonavailableitems (strItemCode,strItemName,strClientCode,dteDate,strPOSCode) "
	                    + "values ('"+itemCode+"','"+itemName+"','"+clientCode+"','"+date+"','"+posCode+"')";
	            exe=st.executeUpdate(sql);
	            System.out.println("Exe= "+exe);
	            result="true";
			    
			} 
			cmsCon.close();
			st.close();
			st2.close();
			
           
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return Response.status(201).entity(result).build();		
	}
	    
    
    
    @GET
	@Path("/funGetNonAvailableItems")
	@Produces(MediaType.APPLICATION_JSON)
	 public String funGetNonAvailableItems(@QueryParam("strClientCode") String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			String sql=" select strItemCode, strItemName from tblnonavailableitems where strClientCode='"+clientCode+"' ";
			JSONArray arrObj=new JSONArray();
			
			 try
		        {
				 aposCon=objDb.funOpenAPOSCon("mysql","master");
		            st = aposCon.createStatement();
		            
		            ResultSet rsMasterData=st.executeQuery(sql);
	                while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strItemCode",rsMasterData.getString(1));
	        	        obj.put("strItemName",rsMasterData.getString(2));
	        	        
	        	       
	        	        arrObj.put(obj);
	                }
	                rsMasterData.close();
	        
	                jObj.put("NonAvailableItemList", arrObj);
	                st.close();
	                aposCon.close();
	        
		     }catch(Exception e)
			 {
		    	 e.printStackTrace();
			 }
			 return jObj.toString();
		}

    @GET 
	@Path("/funGetSetupValues")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetSetupValues(@QueryParam("POSCode") String POSCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject obj=new JSONObject();
		
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = "select * from tblsetup a ";
            if(!POSCode.equals("All"))
            {
                sql+= " where (a.strPOSCode='" + POSCode + "'or a.strPOSCode='All') ";
            }
            System.out.println(sql);
            
           // JSONArray arrObj=new JSONArray();
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	System.out.println("Code= "+rsTableInfo.getString(1));
            	System.out.println("Name= "+rsTableInfo.getString(2));
            	//JSONObject obj=new JSONObject();
            	obj.put("ClientCode",rsTableInfo.getString(1));
            	obj.put("ClientName",rsTableInfo.getString(2));
            	obj.put("ClientAddressLine1",rsTableInfo.getString(3));
            	obj.put("ClientAddressLine2",rsTableInfo.getString(4));
            	obj.put("ClientAddressLine3",rsTableInfo.getString(5));
            	
            	obj.put("Email",rsTableInfo.getString(6));            	
            	obj.put("BillFooter",rsTableInfo.getString(7));
            	obj.put("BillFooterStatus",rsTableInfo.getString(8));
            	obj.put("BillPaperSize",rsTableInfo.getString(9));
            	obj.put("NegativeBilling",rsTableInfo.getString(10));
            	
            	obj.put("DayEnd",rsTableInfo.getString(11));
            	obj.put("PrintMode",rsTableInfo.getString(12));
            	obj.put("DiscountNote",rsTableInfo.getString(13));
            	obj.put("CityName",rsTableInfo.getString(14));
            	obj.put("State",rsTableInfo.getString(15));
            	
            	obj.put("Country",rsTableInfo.getString(16));
            	obj.put("TelephoneNo",rsTableInfo.getString(17));
            	obj.put("StartDate",rsTableInfo.getString(18));
            	obj.put("EndDate",rsTableInfo.getString(19));
            	obj.put("NatureOfBusinnes",rsTableInfo.getString(20));
            	
            	obj.put("MultipleBillPrinting",rsTableInfo.getString(21));
            	obj.put("EnableKOT",rsTableInfo.getString(22));
            	obj.put("PrintVatNo",rsTableInfo.getString(23));
            	obj.put("VatNo",rsTableInfo.getString(24));
            	obj.put("ShowBill",rsTableInfo.getString(25));
            	
            	obj.put("PrintServiceTaxNo",rsTableInfo.getString(26));
            	obj.put("ServiceTaxNo",rsTableInfo.getString(27));
            	obj.put("ManualBillNo",rsTableInfo.getString(28));
            	obj.put("MenuItemDispSeq",rsTableInfo.getString(29));
            	obj.put("SenderEmailId",rsTableInfo.getString(30));
            	
            	obj.put("EmailPassword",rsTableInfo.getString(31));
            	obj.put("ConfirmEmailPassword",rsTableInfo.getString(32));
            	obj.put("Body",rsTableInfo.getString(33));
            	obj.put("EmailServerName",rsTableInfo.getString(34));
            	obj.put("SMSApi",rsTableInfo.getString(35));
            	
            	obj.put("UserCreated",rsTableInfo.getString(36));
            	obj.put("UserEdited",rsTableInfo.getString(37));
            	obj.put("ManualBillNo",rsTableInfo.getString(38));
            	obj.put("DateCreated",rsTableInfo.getString(39));
            	obj.put("DateEdited",rsTableInfo.getString(40));
            	
            	obj.put("POSType",rsTableInfo.getString(41));
            	obj.put("WebServiceLink",rsTableInfo.getString(42));
            	obj.put("DataSendFrequency",rsTableInfo.getString(43));
            	obj.put("HOServerDate",rsTableInfo.getString(44));
            	obj.put("RFID",rsTableInfo.getString(45));
            	
            	obj.put("ServerName",rsTableInfo.getString(46));
            	obj.put("DBUserName",rsTableInfo.getString(47));
            	obj.put("DBPassword",rsTableInfo.getString(48));
            	obj.put("DatabaseName",rsTableInfo.getString(49));
            	obj.put("EnableKOTForDirectBiller",rsTableInfo.getString(50));
            	
            	obj.put("PinCode",rsTableInfo.getString(51));
            	obj.put("ChangeTheme",rsTableInfo.getString(52));
            	obj.put("MaxDiscount",rsTableInfo.getString(53));
            	obj.put("AreaWisePricing",rsTableInfo.getString(54));
            	obj.put("MenuItemSortingOn",rsTableInfo.getString(55));
            	
            	obj.put("DirectBillerAreaCode",rsTableInfo.getString(56));
            	obj.put("ColumnSize",rsTableInfo.getString(57));
            	obj.put("PrintType",rsTableInfo.getString(58));
            	obj.put("EditHomeDelivery",rsTableInfo.getString(59));
            	obj.put("SlabBasedHDCharges",rsTableInfo.getString(60));
            	
            	obj.put("SkipWaiterAndPax",rsTableInfo.getString(61));
            	obj.put("SkipWaiter",rsTableInfo.getString(62));
            	obj.put("DirectKOTPrintMakeKOT",rsTableInfo.getString(63));
            	obj.put("SkipPax",rsTableInfo.getString(64));
            	obj.put("CRMInterface",rsTableInfo.getString(65));
            	
            	obj.put("GetWebserviceURL",rsTableInfo.getString(66));
            	obj.put("PostWebserviceURL",rsTableInfo.getString(67));
            	obj.put("OutletUID",rsTableInfo.getString(68));
            	obj.put("POSID",rsTableInfo.getString(69));
            	obj.put("StockInOption",rsTableInfo.getString(70));
            	
            	obj.put("CustSeries",rsTableInfo.getString(71));
            	obj.put("AdvReceiptPrintCount",rsTableInfo.getString(72));
            	obj.put("HomeDeliverySMS",rsTableInfo.getString(73));
            	obj.put("BillSettlementSMS",rsTableInfo.getString(74));
            	obj.put("BillFormatType",rsTableInfo.getString(75));
            	
            	obj.put("ActivePromotions",rsTableInfo.getString(76));
            	obj.put("SendHomeDelSMS",rsTableInfo.getString(77));
            	obj.put("SendBillSettlementSMS",rsTableInfo.getString(78));
            	obj.put("SMSType",rsTableInfo.getString(79));
            	obj.put("PrintShortNameOnKOT",rsTableInfo.getString(80));
            	
            	obj.put("ShowCustHelp",rsTableInfo.getString(81));
            	obj.put("PrintOnVoidBill",rsTableInfo.getString(82));
            	obj.put("PostSalesDataToMMS",rsTableInfo.getString(83));
            	obj.put("CustAreaMasterCompulsory",rsTableInfo.getString(84));
            	obj.put("PriceFrom",rsTableInfo.getString(85));
            	
            	obj.put("ShowPrinterErrorMessage",rsTableInfo.getString(86));
            	obj.put("TouchScreenMode",rsTableInfo.getString(87));
            	obj.put("CardInterfaceType",rsTableInfo.getString(88));
            	obj.put("CMSIntegrationYN",rsTableInfo.getString(89));
            	obj.put("CMSWebServiceURL",rsTableInfo.getString(90));
            	
            	obj.put("ChangeQtyForExternalCode",rsTableInfo.getString(91));
            	obj.put("PointsOnBillPrint",rsTableInfo.getString(92));
            	obj.put("CMSPOSCode",rsTableInfo.getString(93));
            	obj.put("ManualAdvOrderNoCompulsory",rsTableInfo.getString(94));
            	obj.put("PrintManualAdvOrderNoOnBill",rsTableInfo.getString(95));
            	
            	obj.put("PrintModifierQtyOnKOT",rsTableInfo.getString(96));
            	obj.put("NoOfLinesInKOTPrint",rsTableInfo.getString(97));
            	obj.put("MultipleKOTPrintYN",rsTableInfo.getString(98));
            	obj.put("ItemQtyNumpad",rsTableInfo.getString(99));
            	obj.put("TreatMemberAsTable",rsTableInfo.getString(100));
            	
            	obj.put("KOTToLocalPrinter",rsTableInfo.getString(101));
            	//obj.put("ReportImage",rsTableInfo.getString(102));
            	obj.put("SettleBtnForDirectBillerBill",rsTableInfo.getString(103));
            	obj.put("DelBoySelCompulsoryOnDirectBiller",rsTableInfo.getString(104));
            	obj.put("CMSMemberForKOTJPOS",rsTableInfo.getString(105));
            	
            	obj.put("CMSMemberForKOTMPOS",rsTableInfo.getString(106));
            	obj.put("DontShowAdvOrderInOtherPOS",rsTableInfo.getString(107));
            	obj.put("PrintZeroAmtModifierInBill",rsTableInfo.getString(108));
            	obj.put("PrintKOTYN",rsTableInfo.getString(109));
            	obj.put("CreditCardSlipNoCompulsoryYN",rsTableInfo.getString(110));
            	
            	obj.put("CreditCardExpiryDateCompulsoryYN",rsTableInfo.getString(111));
            	obj.put("SelectWaiterFromCardSwipe",rsTableInfo.getString(112));
            	obj.put("MultiWaiterSelectionOnMakeKOT",rsTableInfo.getString(113));
            	obj.put("MoveTableToOtherPOS",rsTableInfo.getString(114));
            	obj.put("MoveKOTToOtherPOS",rsTableInfo.getString(115));
            	
            	obj.put("CalculateTaxOnMakeKOT",rsTableInfo.getString(116));
            	obj.put("ReceiverEmailId",rsTableInfo.getString(117));
            	obj.put("CalculateDiscItemWise",rsTableInfo.getString(118));
            	obj.put("TakewayCustomerSelection",rsTableInfo.getString(119));
            	obj.put("ShowItemStkColumnInDB",rsTableInfo.getString(120));
            	
            	obj.put("ItemType",rsTableInfo.getString(121));
            	obj.put("AllowNewAreaMasterFromCustMaster",rsTableInfo.getString(122));
            	obj.put("CustAddressSelectionForBill",rsTableInfo.getString(123));
            	obj.put("MemberCodeForKotInMposByCardSwipe",rsTableInfo.getString(133));
            	obj.put("PrintBillPopUp",rsTableInfo.getString(134));
            	obj.put("UseVatAndServiceTaxNoFromPOS",rsTableInfo.getString(135));
            	
            	obj.put("MemberCodeForMakeBillInMPOS",rsTableInfo.getString(136));
            	obj.put("ItemWiseKOTPrintYN",rsTableInfo.getString(137));
            	obj.put("LastPOSForDayEnd",rsTableInfo.getString(138));
            	obj.put("CMSPostingType",rsTableInfo.getString(139));
            	obj.put("PopUpToApplyPromotionsOnBill",rsTableInfo.getString(140));

            	obj.put("strSelectCustomerCodeFromCardSwipe",rsTableInfo.getString(141));
            	obj.put("strCheckDebitCardBalOnTransactions",rsTableInfo.getString(142));
            	obj.put("strSettlementsFromPOSMaster",rsTableInfo.getString(143));
            	obj.put("strShiftWiseDayEndYN",rsTableInfo.getString(144));
            	obj.put("strProductionLinkup",rsTableInfo.getString(145));
            	
            	obj.put("strLockDataOnShift",rsTableInfo.getString(146));
            	obj.put("strWSClientCode",rsTableInfo.getString(147));
            	obj.put("strPOSCode",rsTableInfo.getString(148));
            	obj.put("strEnableBillSeries",rsTableInfo.getString(149));
            	obj.put("EnablePMSIntegrationYN",rsTableInfo.getString(150));
            	
            	
            	
            	
            	obj.put("strPrintTimeOnBill",rsTableInfo.getString(151));
            	obj.put("strPrintTDHItemsInBill",rsTableInfo.getString(152));
            	obj.put("strPrintRemarkAndReasonForReprint",rsTableInfo.getString(153));
            	obj.put("intDaysBeforeOrderToCancel",rsTableInfo.getString(154));
            	obj.put("intNoOfDelDaysForAdvOrder",rsTableInfo.getString(155));
            	
            	obj.put("intNoOfDelDaysForUrgentOrder",rsTableInfo.getString(156));
            	obj.put("strSetUpToTimeForAdvOrder",rsTableInfo.getString(157));
            	obj.put("strSetUpToTimeForUrgentOrder",rsTableInfo.getString(158));
            	obj.put("strUpToTimeForAdvOrder",rsTableInfo.getString(159));
            	obj.put("strUpToTimeForUrgentOrder",rsTableInfo.getString(160));
            	
            	obj.put("strEnableBothPrintAndSettleBtnForDB",rsTableInfo.getString(161));
            	obj.put("strInrestoPOSIntegrationYN",rsTableInfo.getString(162));
            	obj.put("strInrestoPOSWebServiceURL",rsTableInfo.getString(163));
            	obj.put("strInrestoPOSId",rsTableInfo.getString(164));
            	obj.put("strInrestoPOSKey",rsTableInfo.getString(165));
            	obj.put("strCarryForwardFloatAmtToNextDay",rsTableInfo.getString(166));
            	
            	obj.put("strOpenCashDrawerAfterBillPrintYN",rsTableInfo.getString(167));
            	obj.put("strPropertyWiseSalesOrderYN",rsTableInfo.getString(168));
            	obj.put("strShowItemDetailsGrid",rsTableInfo.getString(170));
            	
            	
            	obj.put("strShowPopUpForNextItemQuantity",rsTableInfo.getString(171));
            	obj.put("strJioMoneyIntegration",rsTableInfo.getString(172));
            	obj.put("strJioWebServiceUrl",rsTableInfo.getString(173));
            	obj.put("strJioMID",rsTableInfo.getString(174));
            	obj.put("strJioTID",rsTableInfo.getString(175));
            	
            	obj.put("strJioActivationCode",rsTableInfo.getString(176));
            	obj.put("strJioDeviceID",rsTableInfo.getString(177));
            	obj.put("strNewBillSeriesForNewDay",rsTableInfo.getString(178));
            	obj.put("strShowReportsPOSWise",rsTableInfo.getString(179));
            	obj.put("strEnableDineIn",rsTableInfo.getString(180));
            	
            	obj.put("strAutoAreaSelectionInMakeKOT",rsTableInfo.getString(181));
            	obj.put("strConsolidatedKOTPrinterPort",rsTableInfo.getString(182));
            	obj.put("strScanQRYN",rsTableInfo.getString(187));
            	obj.put("strAreaWisePromotions",rsTableInfo.getString(188));
            	
            	
            	obj.put("strEnableTableReservationForCustomer",rsTableInfo.getString(191));
            	
            	obj.put("strAutoShowPopItems",rsTableInfo.getString(192));
            	obj.put("intShowPopItemsOfDays",rsTableInfo.getString(193));
            	obj.put("strPostSalesCostOrLoc",rsTableInfo.getString(194));
            	obj.put("strEffectOfSales",rsTableInfo.getString(195));
            	obj.put("strPOSWiseItemToMMSProductLinkUpYN",rsTableInfo.getString(196));
            	
            	
            	obj.put("strEnableMasterDiscount",rsTableInfo.getString(197));
            	obj.put("strEnableNFCInterface",rsTableInfo.getString(198));
            	obj.put("strBenowIntegrationYN",rsTableInfo.getString(199));
            	obj.put("strXEmail",rsTableInfo.getString(200));
            	
            	obj.put("strMerchantCode",rsTableInfo.getString(201));
            	obj.put("strAuthenticationKey",rsTableInfo.getString(202));
            	obj.put("strSalt",rsTableInfo.getString(203));
            	obj.put("strEnableLockTable",rsTableInfo.getString(204));
            	obj.put("strHomeDeliveryAreaForDirectBiller",rsTableInfo.getString(205));
            	
            	obj.put("strTakeAwayAreaForDirectBiller",rsTableInfo.getString(206));
            	obj.put("strRoundOffBillFinalAmt",rsTableInfo.getString(207));
            	obj.put("dblNoOfDecimalPlace",rsTableInfo.getString(208));
            	obj.put("strSendDBBackupOnClientMail",rsTableInfo.getString(209));
            	obj.put("strPrintOrderNoOnBillYN",rsTableInfo.getString(210));
            	
            	obj.put("strPrintDeviceAndUserDtlOnKOTYN",rsTableInfo.getString(211));
            	obj.put("strRemoveSCTaxCode",rsTableInfo.getString(212));
            	obj.put("strAutoAddKOTToBill",rsTableInfo.getString(213));
            	obj.put("strAreaWiseCostCenterKOTPrintingYN",rsTableInfo.getString(214));
            	obj.put("strWERAOnlineOrderIntegration",rsTableInfo.getString(215));
            	
            	obj.put("strWERAMerchantOutletId",rsTableInfo.getString(216));
            	obj.put("strWERAAuthenticationAPIKey",rsTableInfo.getString(217));
            	obj.put("strFireCommunication",rsTableInfo.getString(218));
            	
            	obj.put("dblUSDConverionRate",rsTableInfo.getString(219));
            	obj.put("strDBBackupMailReceiver",rsTableInfo.getString(220));
            	obj.put("strPrintMoveTableMoveKOTYN",rsTableInfo.getString(221));
            	obj.put("strPrintQtyTotal",rsTableInfo.getString(222));
            	obj.put("strShowReportsInCurrency",rsTableInfo.getString(223));
            	obj.put("strPOSToMMSPostingCurrency",rsTableInfo.getString(224));
            	obj.put("strPOSToWebBooksPostingCurrency",rsTableInfo.getString(225));
            	obj.put("strLockTableForWaiter",rsTableInfo.getString(226));
            	
            	
            	obj.put("Status","Success");
            	
            	//arrObj.put(obj);
            }
            rsTableInfo.close();
           // jObj.put("SetupValues", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
           // JSONObject obj=new JSONObject();
        	obj.put("Status","Error");
        	JSONArray arrObj=new JSONArray();
        	//arrObj.put(obj);
        	//jObj.put("SetupValues", arrObj);
        }
        finally
        {
        	return obj;
        }
    }
    
    
    
    @GET 
	@Path("/funGetSetupValues1")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetSetupValues1(@QueryParam("POSCode") String POSCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
		
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = "select * from tblsetup a ";
            if(!POSCode.equals("All"))
            {
                sql+= " where (a.strPOSCode='" + POSCode + "'or a.strPOSCode='All') ";
            }
            System.out.println(sql);
            
            JSONArray arrObj=new JSONArray();
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	System.out.println("Code= "+rsTableInfo.getString(1));
            	System.out.println("Name= "+rsTableInfo.getString(2));
            	JSONObject obj=new JSONObject();
            	obj.put("ClientCode",rsTableInfo.getString(1));
            	obj.put("ClientName",rsTableInfo.getString(2));
            	obj.put("ClientAddressLine1",rsTableInfo.getString(3));
            	obj.put("ClientAddressLine2",rsTableInfo.getString(4));
            	obj.put("ClientAddressLine3",rsTableInfo.getString(5));
            	
            	obj.put("Email",rsTableInfo.getString(6));            	
            	obj.put("BillFooter",rsTableInfo.getString(7));
            	obj.put("BillFooterStatus",rsTableInfo.getString(8));
            	obj.put("BillPaperSize",rsTableInfo.getString(9));
            	obj.put("NegativeBilling",rsTableInfo.getString(10));
            	
            	obj.put("DayEnd",rsTableInfo.getString(11));
            	obj.put("PrintMode",rsTableInfo.getString(12));
            	obj.put("DiscountNote",rsTableInfo.getString(13));
            	obj.put("CityName",rsTableInfo.getString(14));
            	obj.put("State",rsTableInfo.getString(15));
            	
            	obj.put("Country",rsTableInfo.getString(16));
            	obj.put("TelephoneNo",rsTableInfo.getString(17));
            	obj.put("StartDate",rsTableInfo.getString(18));
            	obj.put("EndDate",rsTableInfo.getString(19));
            	obj.put("NatureOfBusinnes",rsTableInfo.getString(20));
            	
            	obj.put("MultipleBillPrinting",rsTableInfo.getString(21));
            	obj.put("EnableKOT",rsTableInfo.getString(22));
            	obj.put("PrintVatNo",rsTableInfo.getString(23));
            	obj.put("VatNo",rsTableInfo.getString(24));
            	obj.put("ShowBill",rsTableInfo.getString(25));
            	
            	obj.put("PrintServiceTaxNo",rsTableInfo.getString(26));
            	obj.put("ServiceTaxNo",rsTableInfo.getString(27));
            	obj.put("ManualBillNo",rsTableInfo.getString(28));
            	obj.put("MenuItemDispSeq",rsTableInfo.getString(29));
            	obj.put("SenderEmailId",rsTableInfo.getString(30));
            	
            	obj.put("EmailPassword",rsTableInfo.getString(31));
            	obj.put("ConfirmEmailPassword",rsTableInfo.getString(32));
            	obj.put("Body",rsTableInfo.getString(33));
            	obj.put("EmailServerName",rsTableInfo.getString(34));
            	obj.put("SMSApi",rsTableInfo.getString(35));
            	
            	obj.put("UserCreated",rsTableInfo.getString(36));
            	obj.put("UserEdited",rsTableInfo.getString(37));
            	obj.put("ManualBillNo",rsTableInfo.getString(38));
            	obj.put("DateCreated",rsTableInfo.getString(39));
            	obj.put("DateEdited",rsTableInfo.getString(40));
            	
            	obj.put("POSType",rsTableInfo.getString(41));
            	obj.put("WebServiceLink",rsTableInfo.getString(42));
            	obj.put("DataSendFrequency",rsTableInfo.getString(43));
            	obj.put("HOServerDate",rsTableInfo.getString(44));
            	obj.put("RFID",rsTableInfo.getString(45));
            	
            	obj.put("ServerName",rsTableInfo.getString(46));
            	obj.put("DBUserName",rsTableInfo.getString(47));
            	obj.put("DBPassword",rsTableInfo.getString(48));
            	obj.put("DatabaseName",rsTableInfo.getString(49));
            	obj.put("EnableKOTForDirectBiller",rsTableInfo.getString(50));
            	
            	obj.put("PinCode",rsTableInfo.getString(51));
            	obj.put("ChangeTheme",rsTableInfo.getString(52));
            	obj.put("MaxDiscount",rsTableInfo.getString(53));
            	obj.put("AreaWisePricing",rsTableInfo.getString(54));
            	obj.put("MenuItemSortingOn",rsTableInfo.getString(55));
            	
            	obj.put("DirectBillerAreaCode",rsTableInfo.getString(56));
            	obj.put("ColumnSize",rsTableInfo.getString(57));
            	obj.put("PrintType",rsTableInfo.getString(58));
            	obj.put("EditHomeDelivery",rsTableInfo.getString(59));
            	obj.put("SlabBasedHDCharges",rsTableInfo.getString(60));
            	
            	obj.put("SkipWaiterAndPax",rsTableInfo.getString(61));
            	obj.put("SkipWaiter",rsTableInfo.getString(62));
            	obj.put("DirectKOTPrintMakeKOT",rsTableInfo.getString(63));
            	obj.put("SkipPax",rsTableInfo.getString(64));
            	obj.put("CRMInterface",rsTableInfo.getString(65));
            	
            	obj.put("GetWebserviceURL",rsTableInfo.getString(66));
            	obj.put("PostWebserviceURL",rsTableInfo.getString(67));
            	obj.put("OutletUID",rsTableInfo.getString(68));
            	obj.put("POSID",rsTableInfo.getString(69));
            	obj.put("StockInOption",rsTableInfo.getString(70));
            	
            	obj.put("CustSeries",rsTableInfo.getString(71));
            	obj.put("AdvReceiptPrintCount",rsTableInfo.getString(72));
            	obj.put("HomeDeliverySMS",rsTableInfo.getString(73));
            	obj.put("BillSettlementSMS",rsTableInfo.getString(74));
            	obj.put("BillFormatType",rsTableInfo.getString(75));
            	
            	obj.put("ActivePromotions",rsTableInfo.getString(76));
            	obj.put("SendHomeDelSMS",rsTableInfo.getString(77));
            	obj.put("SendBillSettlementSMS",rsTableInfo.getString(78));
            	obj.put("SMSType",rsTableInfo.getString(79));
            	obj.put("PrintShortNameOnKOT",rsTableInfo.getString(80));
            	
            	obj.put("ShowCustHelp",rsTableInfo.getString(81));
            	obj.put("PrintOnVoidBill",rsTableInfo.getString(82));
            	obj.put("PostSalesDataToMMS",rsTableInfo.getString(83));
            	obj.put("CustAreaMasterCompulsory",rsTableInfo.getString(84));
            	obj.put("PriceFrom",rsTableInfo.getString(85));
            	
            	obj.put("ShowPrinterErrorMessage",rsTableInfo.getString(86));
            	obj.put("TouchScreenMode",rsTableInfo.getString(87));
            	obj.put("CardInterfaceType",rsTableInfo.getString(88));
            	obj.put("CMSIntegrationYN",rsTableInfo.getString(89));
            	obj.put("CMSWebServiceURL",rsTableInfo.getString(90));
            	
            	obj.put("ChangeQtyForExternalCode",rsTableInfo.getString(91));
            	obj.put("PointsOnBillPrint",rsTableInfo.getString(92));
            	obj.put("CMSPOSCode",rsTableInfo.getString(93));
            	obj.put("ManualAdvOrderNoCompulsory",rsTableInfo.getString(94));
            	obj.put("PrintManualAdvOrderNoOnBill",rsTableInfo.getString(95));
            	
            	obj.put("PrintModifierQtyOnKOT",rsTableInfo.getString(96));
            	obj.put("NoOfLinesInKOTPrint",rsTableInfo.getString(97));
            	obj.put("MultipleKOTPrintYN",rsTableInfo.getString(98));
            	obj.put("ItemQtyNumpad",rsTableInfo.getString(99));
            	obj.put("TreatMemberAsTable",rsTableInfo.getString(100));
            	
            	obj.put("KOTToLocalPrinter",rsTableInfo.getString(101));
            	//obj.put("ReportImage",rsTableInfo.getString(102));
            	obj.put("SettleBtnForDirectBillerBill",rsTableInfo.getString(103));
            	obj.put("DelBoySelCompulsoryOnDirectBiller",rsTableInfo.getString(104));
            	obj.put("CMSMemberForKOTJPOS",rsTableInfo.getString(105));
            	
            	obj.put("CMSMemberForKOTMPOS",rsTableInfo.getString(106));
            	obj.put("DontShowAdvOrderInOtherPOS",rsTableInfo.getString(107));
            	obj.put("PrintZeroAmtModifierInBill",rsTableInfo.getString(108));
            	obj.put("PrintKOTYN",rsTableInfo.getString(109));
            	obj.put("CreditCardSlipNoCompulsoryYN",rsTableInfo.getString(110));
            	
            	obj.put("CreditCardExpiryDateCompulsoryYN",rsTableInfo.getString(111));
            	obj.put("SelectWaiterFromCardSwipe",rsTableInfo.getString(112));
            	obj.put("MultiWaiterSelectionOnMakeKOT",rsTableInfo.getString(113));
            	obj.put("MoveTableToOtherPOS",rsTableInfo.getString(114));
            	obj.put("MoveKOTToOtherPOS",rsTableInfo.getString(115));
            	
            	obj.put("CalculateTaxOnMakeKOT",rsTableInfo.getString(116));
            	obj.put("ReceiverEmailId",rsTableInfo.getString(117));
            	obj.put("CalculateDiscItemWise",rsTableInfo.getString(118));
            	obj.put("TakewayCustomerSelection",rsTableInfo.getString(119));
            	obj.put("ShowItemStkColumnInDB",rsTableInfo.getString(120));
            	
            	obj.put("ItemType",rsTableInfo.getString(121));
            	obj.put("AllowNewAreaMasterFromCustMaster",rsTableInfo.getString(122));
            	obj.put("CustAddressSelectionForBill",rsTableInfo.getString(123));
            	obj.put("MemberCodeForKotInMposByCardSwipe",rsTableInfo.getString(133));
            	obj.put("PrintBillPopUp",rsTableInfo.getString(134));
            	obj.put("UseVatAndServiceTaxNoFromPOS",rsTableInfo.getString(135));
            	
            	obj.put("MemberCodeForMakeBillInMPOS",rsTableInfo.getString(136));
            	obj.put("ItemWiseKOTPrintYN",rsTableInfo.getString(137));
            	obj.put("LastPOSForDayEnd",rsTableInfo.getString(138));
            	obj.put("CMSPostingType",rsTableInfo.getString(139));
            	obj.put("PopUpToApplyPromotionsOnBill",rsTableInfo.getString(140));

            	obj.put("strSelectCustomerCodeFromCardSwipe",rsTableInfo.getString(141));
            	obj.put("strCheckDebitCardBalOnTransactions",rsTableInfo.getString(142));
            	obj.put("strSettlementsFromPOSMaster",rsTableInfo.getString(143));
            	obj.put("strShiftWiseDayEndYN",rsTableInfo.getString(144));
            	obj.put("strProductionLinkup",rsTableInfo.getString(145));
            	
            	obj.put("strLockDataOnShift",rsTableInfo.getString(146));
            	obj.put("strWSClientCode",rsTableInfo.getString(147));
            	obj.put("strPOSCode",rsTableInfo.getString(148));
            	obj.put("strEnableBillSeries",rsTableInfo.getString(149));
            	obj.put("EnablePMSIntegrationYN",rsTableInfo.getString(150));
            	
            	
            	
            	
            	obj.put("strPrintTimeOnBill",rsTableInfo.getString(151));
            	obj.put("strPrintTDHItemsInBill",rsTableInfo.getString(152));
            	obj.put("strPrintRemarkAndReasonForReprint",rsTableInfo.getString(153));
            	obj.put("intDaysBeforeOrderToCancel",rsTableInfo.getString(154));
            	obj.put("intNoOfDelDaysForAdvOrder",rsTableInfo.getString(155));
            	
            	obj.put("intNoOfDelDaysForUrgentOrder",rsTableInfo.getString(156));
            	obj.put("strSetUpToTimeForAdvOrder",rsTableInfo.getString(157));
            	obj.put("strSetUpToTimeForUrgentOrder",rsTableInfo.getString(158));
            	obj.put("strUpToTimeForAdvOrder",rsTableInfo.getString(159));
            	obj.put("strUpToTimeForUrgentOrder",rsTableInfo.getString(160));
            	
            	obj.put("strEnableBothPrintAndSettleBtnForDB",rsTableInfo.getString(161));
            	obj.put("strInrestoPOSIntegrationYN",rsTableInfo.getString(162));
            	obj.put("strInrestoPOSWebServiceURL",rsTableInfo.getString(163));
            	obj.put("strInrestoPOSId",rsTableInfo.getString(164));
            	obj.put("strInrestoPOSKey",rsTableInfo.getString(165));
            	obj.put("strCarryForwardFloatAmtToNextDay",rsTableInfo.getString(166));
            	
            	obj.put("strOpenCashDrawerAfterBillPrintYN",rsTableInfo.getString(167));
            	obj.put("strPropertyWiseSalesOrderYN",rsTableInfo.getString(168));
            	obj.put("strShowItemDetailsGrid",rsTableInfo.getString(170));
            	
            	
            	obj.put("strShowPopUpForNextItemQuantity",rsTableInfo.getString(171));
            	obj.put("strJioMoneyIntegration",rsTableInfo.getString(172));
            	obj.put("strJioWebServiceUrl",rsTableInfo.getString(173));
            	obj.put("strJioMID",rsTableInfo.getString(174));
            	obj.put("strJioTID",rsTableInfo.getString(175));
            	
            	obj.put("strJioActivationCode",rsTableInfo.getString(176));
            	obj.put("strJioDeviceID",rsTableInfo.getString(177));
            	obj.put("strNewBillSeriesForNewDay",rsTableInfo.getString(178));
            	obj.put("strShowReportsPOSWise",rsTableInfo.getString(179));
            	obj.put("strEnableDineIn",rsTableInfo.getString(180));
            	
            	obj.put("strAutoAreaSelectionInMakeKOT",rsTableInfo.getString(181));
            	obj.put("strConsolidatedKOTPrinterPort",rsTableInfo.getString(182));
            	obj.put("strScanQRYN",rsTableInfo.getString(187));
            	obj.put("strAreaWisePromotions",rsTableInfo.getString(188));
            	obj.put("strEnableTableReservationForCustomer",rsTableInfo.getString(191));
            	
            	obj.put("Status","Success");
            	
            	arrObj.put(obj);
            }
            rsTableInfo.close();
            jObj.put("SetupValues", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject obj=new JSONObject();
        	obj.put("Status","Error");
        	JSONArray arrObj=new JSONArray();
        	arrObj.put(obj);
        	jObj.put("SetupValues", arrObj);
        }
        finally
        {
        	return jObj;
        }
    }
    
    
    
	@GET
	@Path("/funGetTDHList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetTDHList(@QueryParam("clientCode") String clientCode )
	{
		return funFetchTDHList(clientCode);
	}
	
	private JSONArray funFetchTDHList(String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONObject jObjMain=new JSONObject();
        JSONArray arrObjMain=new JSONArray();
        
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            int i=1;
           
            String tdhSql="select strItemCode,strTDHCode from tbltdhhd where strComboItemYN='Y' and strApplicable='Y' ";
            
            ResultSet rstdhHD=st.executeQuery(tdhSql);
            while(rstdhHD.next())
            {
            	JSONObject jObj=new JSONObject();
            	JSONArray arrObj=new JSONArray();
            	
            	jObj.put("TDHMainItemCode", rstdhHD.getString(1));
            	
            	 String comboItemDtl="select a.strItemName,b.strSubItemCode,b.intSubItemQty,b.strDefaultYN,b.strSubItemMenuCode from tblitemmaster a,tbltdhcomboitemdtl b "
                         + "where a.strItemCode=b.strSubItemCode and b.strItemCode='" + rstdhHD.getString(1) + "' and b.strTDHCode='"+rstdhHD.getString(2)+"'";
                
            	 ResultSet rscomboItemDtl=st1.executeQuery(comboItemDtl);
                 while(rscomboItemDtl.next())
                  {
                     JSONObject obj=new JSONObject();
                    
  	            	 obj.put("TDHItemCode",rscomboItemDtl.getString(2));
  	            	 obj.put("TDHItemName",rscomboItemDtl.getString(1));
  	            	 obj.put("Quantity",rscomboItemDtl.getString(3));
  	            	 obj.put("DefaultYN",rscomboItemDtl.getString(4));
  	            	 obj.put("SubMenuCode",rscomboItemDtl.getString(5));
  	            	
  	            	 arrObj.put(obj);
                      
                  }
                  rscomboItemDtl.close();
                  cmsCon.close();
                  st.close();
                  
                  jObj.put("TDHCombo "+i, arrObj);
                  arrObjMain.put(jObj);
                  i++;
                  
                  
            }
            rstdhHD.close();
            
            //jObjMain.put("TDHDataList", arrObjMain);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObjMain; //jObjMain.toString();
	}
	
	
	
	
	
	@GET
	@Path("/funGetTDHMenu")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetTDHMenu(@QueryParam("ClientCode") String clientCode,@QueryParam("ItemCode") String itemCode)
	{
		return funFetchTDHMenu(clientCode,itemCode);
	}
	
	private JSONArray funFetchTDHMenu(String clientCode,String itemCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONObject jObjMain=new JSONObject();
        JSONArray arrObjMain=new JSONArray();
        
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
           
            
            String sql_select = " select a.strSubItemMenuCode,ifnull(b.strMenuName,'NA'),ifnull(a.intSubItemQty,0) as MaxQty "
            		+ "from tbltdhcomboitemdtl a left outer join tblmenuhd b on a.strSubItemMenuCode=b.strMenuCode "
            		+ "left outer join tbltdhhd c on a.strTDHCode=c.strTDHCode "
            		+ "where c.strItemCode='"+itemCode+"' and c.strComboItemYN='Y' "
            		+ "group by a.strSubItemMenuCode ";
            
             ResultSet rsTDHDtl=st1.executeQuery(sql_select);
             while(rsTDHDtl.next())
               {
                     JSONObject obj=new JSONObject();
                    
  	            	 obj.put("ModifierCode",rsTDHDtl.getString(1));//SubItemMenuCode
  	            	 obj.put("ModifierName",rsTDHDtl.getString(2));//SubItemMenuCode
  	            	 obj.put("maxQty",rsTDHDtl.getString(3));
  	            	 
  	            	  arrObjMain.put(obj);
                      
                }
             rsTDHDtl .close();
                  
             //jObjMain.put("TDHSubItemMenuList", arrObjMain);
	         st.close();
	         cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObjMain;
	}
	
	
	

	
	
	@GET
	@Path("/funCheckAndGetModifierListForTDH")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funCheckAndGetModifierListForTDH(@QueryParam("ClientCode") String clientCode )
	{
		return funCheckAndFetchModifierListForTDH(clientCode);
	}
	
	private JSONArray funCheckAndFetchModifierListForTDH(String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null,st2=null;
        JSONObject jObjMain=new JSONObject();
        JSONArray arrObjMain=new JSONArray();
        
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            st2 = cmsCon.createStatement();
           
            
            String sql_select = "select strItemCode,intMaxQuantity from tbltdhhd where "
            		         + " strApplicable='Y' and strComboItemYN='N' and strClientCode='"+clientCode+"' ";
            
             ResultSet rsCheck=st.executeQuery(sql_select);
             while(rsCheck.next())
             {
            	 JSONObject jobjMainItem=new JSONObject();
            	 jobjMainItem.put("TDHMainItemCode",rsCheck.getString(1));
            	 jobjMainItem.put("TDHMainItemMaxQty",rsCheck.getString(2));
            	 JSONArray arrModifierGroup=new JSONArray();
            	 
            	  String sql_select_ModifierGroup = "select a.strModifierGroupCode,a.strModifierGroupShortName,a.strApplyMaxItemLimit,"
                         + "a.intItemMaxLimit,a.strApplyMinItemLimit,a.intItemMinLimit  from tblmodifiergrouphd a,tblmodifiermaster b,tblitemmodofier c "
                         + "where a.strOperational='YES' and a.strModifierGroupCode=b.strModifierGroupCode and "
                         + "b.strModifierCode=c.strModifierCode and c.strItemCode='" + rsCheck.getString(1) + "' group by a.strModifierGroupCode order by intSequenceNo;";
                
	              ResultSet rsModifierGroupDtl=st1.executeQuery(sql_select_ModifierGroup);
	              while(rsModifierGroupDtl.next())
	               {
	                     JSONObject jobjGroup=new JSONObject();
	                    
	                     jobjGroup.put("ModifierCode",rsModifierGroupDtl.getString(1));
	                     jobjGroup.put("ModifierName",rsModifierGroupDtl.getString(2));
	                     jobjGroup.put("ApplyMaxItemLimit",rsModifierGroupDtl.getString(3));
	                     jobjGroup.put("ApplyMinItemLimit",rsModifierGroupDtl.getString(5));
	                     jobjGroup.put("ItemMaxLimit",rsModifierGroupDtl.getString(4));
	                     jobjGroup.put("ItemMinLimit",rsModifierGroupDtl.getString(6));
	  	            	 
	  	            	
	  	            	
	  	            	JSONArray arrModifierItem=new JSONArray();
	  	            	
	  	            	
			  	            	 String sql_select_ModifierItem = "select a.strModifierName,a.strModifierCode,b.dblRate,a.strModifierGroupCode,"
			  	                		+ " c.strApplyMaxItemLimit,c.intItemMaxLimit,c.strApplyMinItemLimit,c.intItemMinLimit,b.strDefaultModifier  "
			  	                		+ " from tblmodifiermaster  a, tblitemmodofier b, tblmodifiergrouphd c where a.strModifierCode=b.strModifierCode "
			  	                		+ " and  a.strModifierGroupCode=c.strModifierGroupCode "
			  	                		+ " and a.strModifierGroupCode='"+rsModifierGroupDtl.getString(1)+"' and b.strItemCode='"+rsCheck.getString(1)+"' group by a.strModifierCode ";
			  	                
			  		             ResultSet rsModifierDtl=st2.executeQuery(sql_select_ModifierItem);
			  		             while(rsModifierDtl.next())
			  		             {
			  		                     JSONObject objModifierItem=new JSONObject();
			  		                    
				  		                 objModifierItem.put("ModifierCode",rsModifierDtl.getString(2));
				  		                 objModifierItem.put("ModifierName",rsModifierDtl.getString(1));
				  		                 objModifierItem.put("Rate",rsModifierDtl.getString(3));
				  		                 objModifierItem.put("ModifierGroupCode",rsModifierDtl.getString(4));
				  		             	 
			  		  	            	 
				  		                arrModifierItem.put(objModifierItem);
			  		                      
			  		              }
			  		              rsModifierDtl .close();
			  		              jobjGroup.put("ModifierItemList",arrModifierItem);
			  		              
			  		            arrModifierGroup.put(jobjGroup);
	  	           }
	               rsModifierGroupDtl .close();
	               jobjMainItem.put("ModifierGroupList",arrModifierGroup);
	               arrObjMain.put(jobjMainItem);
	               jObjMain.put("result","success");    
		              
  	          }
              rsCheck .close();
            //  jObjMain.put("ModifierDetails",arrObjMain);
	          st.close();
	          cmsCon.close();
            
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	try {
        		jObjMain.put("result","failed");    
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
        }
        return arrObjMain;
	}




	
	
	@GET
	@Path("/funGetItemDetailsByExternalCode")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetItemDetailsByExternalCode(@QueryParam("POSCode") String posCode,@QueryParam("ExternalCode") String exernalCode,@QueryParam("areaWisePricing") String areaWisePricing,@QueryParam("tableNo") String tableNo,@QueryParam("areaCode") String areaCode)
	{
		return funFetchItemDetailsByExternalCode(posCode,exernalCode,areaWisePricing,tableNo,areaCode);
	}
	
	private JSONArray funFetchItemDetailsByExternalCode(String POSCode,String exernalCode,String areaWisePricing,String tableNo,String areaCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONArray arrObj=new JSONArray();
        
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
           
            
            Date dtCurrentDate=new Date();
            String posDate=(dtCurrentDate.getYear()+1900)+"-"+(dtCurrentDate.getMonth()+1)
                +"-"+(dtCurrentDate.getDate());
            
            
            String gAreaCodeForTrans="";
            String sqlArea = "select strAreaCode from tblareamaster where strAreaName='All'";
	        ResultSet rsArea=st.executeQuery(sqlArea);
	        if (rsArea.next())
	        {
	            gAreaCodeForTrans = rsArea.getString(1);
	        }
	        rsArea.close();
	        
	        if(!tableNo.isEmpty())
	        {
	        	String sqlTableArea = "select a.strAreaCode from tbltablemaster a,tblareamaster b "
	        			+ " where a.strTableNo='"+tableNo+"' and a.strAreaCode=b.strAreaCode";
	        	ResultSet rsTableArea=st1.executeQuery(sqlTableArea);
	 	        if (rsTableArea.next())
	 	        {
	 	        	areaCode = rsTableArea.getString(1);
	 	        }
	 	       rsTableArea.close();
	        }
	       
        
			String sql_ItemDtl="";
			if (areaWisePricing.equals("N")) 
			 {
				
				sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
                        + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
                        + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
                        + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strExternalCode,b.strStockInEnable  "
                        + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
                        + " WHERE b.strExternalCode='" + exernalCode + "' and a.strItemCode=b.strItemCode  "
                        + " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All')"
                        + " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
                        + " ORDER BY b.strItemName ASC";
	         }
			 else 
	         {
				 sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
                         + "a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
                         + "a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
                         + "a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strExternalCode,b.strStockInEnable  "
                         + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
                         + "WHERE b.strExternalCode='" + exernalCode + "' and (a.strAreaCode='"+areaCode+"' or a.strAreaCode='" + gAreaCodeForTrans + "') and   a.strItemCode=b.strItemCode "
                         + " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All')"
                         + " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
                         + "ORDER BY b.strItemName ASC";
			 }	
			
			
               System.out.println(sql_ItemDtl);
			
			   
	           ResultSet rsMasterData=st1.executeQuery(sql_ItemDtl);
	            while(rsMasterData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	
	            	obj.put("ItemCode",rsMasterData.getString(1));
	            	obj.put("ItemName",rsMasterData.getString(2));
	            	obj.put("TextColor",rsMasterData.getString(3));
	            	obj.put("PriceMonday",rsMasterData.getString(4));
	            	obj.put("PriceTuesday",rsMasterData.getString(5));
	            	obj.put("PriceWenesday",rsMasterData.getString(6));
	            	obj.put("PriceThursday",rsMasterData.getString(7));
	            	obj.put("PriceFriday",rsMasterData.getString(8));
	            	obj.put("PriceSaturday",rsMasterData.getString(9));
	            	obj.put("PriceSunday",rsMasterData.getString(10));
	            	obj.put("TimeFrom",rsMasterData.getString(11));
	            	obj.put("AMPMFrom",rsMasterData.getString(12));
	            	obj.put("TimeTo",rsMasterData.getString(13));
	            	obj.put("AMPMTo",rsMasterData.getString(14));
	            	obj.put("CostCenterCode",rsMasterData.getString(15));
	            	obj.put("HourlyPricing",rsMasterData.getString(16));
	            	obj.put("SubMenuHeadCode",rsMasterData.getString(17));
	            	obj.put("FromDate",rsMasterData.getString(18));
	            	obj.put("ToDate",rsMasterData.getString(19));
	            	obj.put("ExternalCode",rsMasterData.getString(20));
	            	//obj.put("StockInEnable",rsMasterData.getString(21));
	            	
	            	arrObj.put(obj);
	            }
	            rsMasterData.close();
	            
	           // jObj.put("externalCodeDetails", arrObj);
	            st.close();
	            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;
	}
    
	
   
	
	
	   @GET
	   @Path("/funGenerateAndPrintBillForBlueToothPrinter")
	   @Produces(MediaType.APPLICATION_JSON)
	   public JSONObject funGenerateAndPrintBillForBlueToothPrinter(@QueryParam("BillNo") String billNo,@QueryParam("PosCode") String posCode,@QueryParam("ClientCode") String clientCode)
	   {
		   JSONObject jObj=new JSONObject();
		   try
		   {
			   StringBuilder sbPrintBill = new StringBuilder();
			   sbPrintBill=funCreateAndPrintBillForBlueToothPrinter(billNo,posCode,clientCode);
			   jObj.put("printData", sbPrintBill.toString());
		   }
		   catch(Exception e)
		   {
			   try
			   {
				   jObj.put("error", "Error");
			   }
			   catch(Exception e1)
			   {
				   e1.printStackTrace();
			   }
			 
			  
		   }
		   return jObj;
	   }
		
	   
	   private StringBuilder funCreateAndPrintBillForBlueToothPrinter(String billNo,String posCode,String clientCode)
	   {
		   StringBuilder sbPrintBill = new StringBuilder();
		   try 
	       {
			   //clsTextFileGenerator file=new clsTextFileGenerator();
			   //file.funGenerateTextFileForBill(billNo,posCode,clientCode);
			   sbPrintBill=obTextFileGenerator.funGenerateAndPrintBillForBlueToothPrinter(billNo, posCode, clientCode);
	           
	       } catch (Exception e) 
	       {
	           e.printStackTrace();
	       }
	       return sbPrintBill;
	   }
	   
	   
	   
	   @GET
	   @Path("/funGenerateDirectBillerKOTTextFileForBluetooth")
	   @Produces(MediaType.APPLICATION_JSON)
	   public JSONObject funGenerateDirectBillerKOTTextFileForBluetooth(@QueryParam("POSCode") String POSCode,@QueryParam("BillNo") String BillNo,@QueryParam("AreaCode") String areaCode)
	   {
		   JSONObject jObj=new JSONObject();
		   
		   try
		   {
			   JSONArray arrJsonKOT=new JSONArray();
			   arrJsonKOT=funCreateDirectBillerKOTTextFileForBluetooth(POSCode,BillNo,areaCode);
			   jObj.put("printKOTData", arrJsonKOT);
		   }
		   catch(Exception e)
		   {
			   try
			   {
				   jObj.put("error", "Error");
			   }
			   catch(Exception e1)
			   {
				   e1.printStackTrace();
			   }
			 
		   }
		   return jObj;
	   }
		
	   
	   private JSONArray funCreateDirectBillerKOTTextFileForBluetooth(String POSCode,String BillNo,String areaCode)
	   {
		   JSONArray arrJsonKOT=new JSONArray();
		   try 
	       {
			   arrJsonKOT=funGenerateTextFileForKOTForDirectBillerForBluetooth(POSCode,areaCode,BillNo, "","Y") ;
			 
	           
	       } catch (Exception e) 
	       {
	           e.printStackTrace();
	       }
	       return arrJsonKOT;
	   }
	   
	   
	   
	   private JSONArray funGenerateTextFileForKOTForDirectBillerForBluetooth(String POSCode,String areaCode,String billNo, String reprint, String printYN) 
	   {

		   clsDatabaseConnection objDb=new clsDatabaseConnection();
	       Connection aposCon=null;
	       Statement st=null;
	       String sql="";
	       StringBuilder sbPrintKot = new StringBuilder();
	       JSONArray arrJsonKOT=new JSONArray();
		   
	       try 
	       {
	    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
		       st = aposCon.createStatement();


	                   sql = "select a.strItemName,c.strCostCenterCode,c.strPrinterPort"
	                       + ",c.strSecondaryPrinterPort,c.strCostCenterName "
	                       + " from tblbilldtl  a,tblmenuitempricingdtl b,tblcostcentermaster c "
	                       + " where a.strBillNo='"+billNo+"' and  a.strItemCode=b.strItemCode "
	                       + " and b.strCostCenterCode=c.strCostCenterCode and b.strPosCode='"+POSCode+"' "
	                       + " group by c.strCostCenterCode;";
	                   
	                   System.out.println("Query="+sql);
	                   ResultSet rsPrintDirect = st.executeQuery(sql);
	                   int i=1;
	                   while (rsPrintDirect.next()) 
	                   {
	                	   JSONObject jObjKOT=new JSONObject();
	                	   sbPrintKot=objAPOSKOTPrint.funGenerateTextFileForKOTDirectBillerForBluetooth(rsPrintDirect.getString(2), "",areaCode, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5),POSCode,printYN);
	                	   jObjKOT.put("KOT "+i,sbPrintKot.toString());
	                	   arrJsonKOT.put(jObjKOT);
	                   }
	                   rsPrintDirect.close();
	                   st.close();
	                   
	                 
	                 
	           
	           
	       } catch (Exception e) 
	       {
	           e.printStackTrace();
	       }
	       
	       return arrJsonKOT;
	   }
	   
	   
	   
	   
	 
	   
	   
	   @GET 
	   @Path("/funGetKOTDetailsForKDS")
	   @Produces(MediaType.APPLICATION_JSON)
	   public JSONObject funGetKOTDetailsForKDS(@QueryParam("POSCode") String POSCode,@QueryParam("CostCenterCode") String CostCenterCode,@QueryParam("POSDate") String POSDate)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			
	        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="";
	            
	            
	            
	            String sqlBillDtl = "(SELECT a.strKOTNo,a.strItemCode,a.strItemName,a.dblRate, SUM(a.dblItemQuantity), SUM(a.dblAmount) "
	                     + ", DATE_FORMAT(DATE(a.dteDateCreated),'%d-%m-%Y') AS dteKOTDate, TIME(a.dteDateCreated) AS tmeKOTTime "
	                     + ",a.strTableNo,b.strTableName, IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.intProcTimeMin*60), IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.tmeTargetMiss*60),'RED','ORANGE'),'BLACK') "
	                     + ",'Order',a.strWaiterNo "
	                     + "FROM tblitemrtemp a "
	                     + ",tbltablemaster b "
	                     + ",tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e "
	                     + "WHERE LEFT(a.strItemCode,7)=c.strItemCode AND a.strNCKotYN='N' "
	                     + "AND a.tdhComboItemYN='N' AND a.strTableNo=b.strTableNo "
	                     + "AND a.strItemProcessed='N' AND c.strItemCode=d.strItemCode "
	                     + "AND a.strPOSCode=d.strPosCode AND (d.strPosCode='"+POSCode+"' OR d.strPosCode='All') "
	                     + "AND d.strCostCenterCode=e.strCostCenterCode AND e.strCostCenterCode='"+CostCenterCode+"' "
	                     + "GROUP BY a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
	                     + "ORDER BY a.dteDateCreated DESC, TIME(a.dteDateCreated) DESC) "
	                     + "union all( "
	                     + "SELECT a.strKOTNo,a.strItemCode,a.strItemName,SUM(a.dblAmount)/SUM(a.dblItemQuantity), SUM(a.dblItemQuantity), SUM(a.dblAmount) "
	                     + ", DATE_FORMAT(DATE(a.dteDateCreated),'%d-%m-%Y') AS dteKOTDate, TIME(a.dteDateCreated) AS tmeKOTTime "
	                     + ",a.strTableNo,b.strTableName, IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.intProcTimeMin*60), IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.tmeTargetMiss*60),'RED','ORANGE'),'BLACK') "
	                     + ",'Void',a.strWaiterNo "
	                     + "FROM tblvoidkot a "
	                     + ",tbltablemaster b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e "
	                     + "WHERE LEFT(a.strItemCode,7)=c.strItemCode AND a.strTableNo=b.strTableNo "
	                     + "AND c.strItemCode=d.strItemCode AND a.strPOSCode=d.strPosCode "
	                     + "AND (d.strPosCode='"+POSCode+"' OR d.strPosCode='All') AND d.strCostCenterCode=e.strCostCenterCode "
	                     + "AND e.strCostCenterCode='"+CostCenterCode+"' And date(a.dteVoidedDate)='"+POSDate+"'  "
	                     + "AND a.strItemProcessed='N' "
	                     + "GROUP BY a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
	                     + "ORDER BY a.dteDateCreated DESC, TIME(a.dteDateCreated) DESC"
	                     + ")ORDER BY strKOTNo DESC,dteKOTDate DESC ";
     
	            
	       /*     String sqlBillDtl = "(SELECT a.strKOTNo,a.strItemCode,a.strItemName,a.dblRate, SUM(a.dblItemQuantity), SUM(a.dblAmount) "
	                     + ", DATE_FORMAT(DATE(a.dteDateCreated),'%d-%m-%Y') AS dteKOTDate, TIME(a.dteDateCreated) AS tmeKOTTime "
	                     + ",a.strTableNo,b.strTableName, IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.intProcTimeMin*60), IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.tmeTargetMiss*60),'RED','ORANGE'),'BLACK') "
	                     + ",'Order',a.strWaiterNo "
	                     + "FROM tblitemrtemp a "
	                     + ",tbltablemaster b "
	                     + ",tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e "
	                     + "WHERE LEFT(a.strItemCode,7)=c.strItemCode AND a.strNCKotYN='N' "
	                     + "AND a.tdhComboItemYN='N' AND a.strTableNo=b.strTableNo "
	                     + "AND a.strItemProcessed='N' AND c.strItemCode=d.strItemCode "
	                     + "AND a.strPOSCode=d.strPosCode AND (d.strPosCode='"+POSCode+"' OR d.strPosCode='All') "
	                     + "AND d.strCostCenterCode=e.strCostCenterCode AND e.strCostCenterCode='"+CostCenterCode+"' "
	                     + "AND a.strItemCode NOT IN(SELECT strItemCode FROM tblkdsprocess WHERE strBP='P' "
	                     + "AND strKDSName='KOT' AND strCostCenterCode='"+CostCenterCode+"' AND a.strKOTNo=strDocNo)"
	                     + "GROUP BY a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
	                     + "ORDER BY a.dteDateCreated DESC, TIME(a.dteDateCreated) DESC) "
	                     + "union all( "
	                     + "SELECT a.strKOTNo,a.strItemCode,a.strItemName,SUM(a.dblAmount)/SUM(a.dblItemQuantity), SUM(a.dblItemQuantity), SUM(a.dblAmount) "
	                     + ", DATE_FORMAT(DATE(a.dteDateCreated),'%d-%m-%Y') AS dteKOTDate, TIME(a.dteDateCreated) AS tmeKOTTime "
	                     + ",a.strTableNo,b.strTableName, IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.intProcTimeMin*60), IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.tmeTargetMiss*60),'RED','ORANGE'),'BLACK') "
	                     + ",'Void',a.strWaiterNo "
	                     + "FROM tblvoidkot a "
	                     + ",tbltablemaster b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e "
	                     + "WHERE LEFT(a.strItemCode,7)=c.strItemCode AND a.strTableNo=b.strTableNo "
	                     + "AND c.strItemCode=d.strItemCode AND a.strPOSCode=d.strPosCode "
	                     + "AND (d.strPosCode='"+POSCode+"' OR d.strPosCode='All') AND d.strCostCenterCode=e.strCostCenterCode "
	                     + "AND e.strCostCenterCode='"+CostCenterCode+"' And date(a.dteVoidedDate)='"+POSDate+"'  "
	                     + "AND a.strItemProcessed='N' AND a.strItemCode NOT IN(SELECT strItemCode FROM tblkdsprocess WHERE strBP='P' "
	                     + "AND strKDSName='KOT' AND strCostCenterCode='"+CostCenterCode+"' AND a.strKOTNo=strDocNo) "
	                     + "GROUP BY a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
	                     + "ORDER BY a.dteDateCreated DESC, TIME(a.dteDateCreated) DESC"
	                     + ")ORDER BY strKOTNo DESC,dteKOTDate DESC ";
	            
	            
	            
	            sql = "(SELECT a.strKOTNo,a.strItemCode,a.strItemName,a.dblRate, SUM(a.dblItemQuantity), SUM(a.dblAmount) "
	                     + ", DATE_FORMAT(DATE(a.dteDateCreated),'%d-%m-%Y') AS dteKOTDate, TIME(a.dteDateCreated) AS tmeKOTTime "
	                     + ",a.strTableNo,b.strTableName, IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.intProcTimeMin*60), IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.tmeTargetMiss*60),'RED','ORANGE'),'BLACK') "
	                     + ",'Order' "
	                     + "FROM tblitemrtemp a "
	                     + ",tbltablemaster b "
	                     + ",tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e "
	                     + "WHERE LEFT(a.strItemCode,7)=c.strItemCode AND a.strNCKotYN='N' "
	                     + "AND a.tdhComboItemYN='N' AND a.strTableNo=b.strTableNo "
	                     + "AND a.strItemProcessed='N' AND c.strItemCode=d.strItemCode "
	                     + "AND a.strPOSCode=d.strPosCode AND (d.strPosCode='"+POSCode+"' OR d.strPosCode='All') "
	                     + "AND d.strCostCenterCode=e.strCostCenterCode AND e.strCostCenterCode='"+CostCenterCode+"' "
	                     + "AND a.strKOTNo NOT IN(SELECT strDocNo FROM tblkdsprocess WHERE strBP='P' AND strKDSName='KOT')"
	                     + "GROUP BY a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
	                     + "ORDER BY a.dteDateCreated DESC, TIME(a.dteDateCreated) DESC) "
	                     + "union all( "
	                     + "SELECT a.strKOTNo,a.strItemCode,a.strItemName,SUM(a.dblAmount)/SUM(a.dblItemQuantity), SUM(a.dblItemQuantity), SUM(a.dblAmount) "
	                     + ", DATE_FORMAT(DATE(a.dteDateCreated),'%d-%m-%Y') AS dteKOTDate, TIME(a.dteDateCreated) AS tmeKOTTime "
	                     + ",a.strTableNo,b.strTableName, IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.intProcTimeMin*60), IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), TIME(a.dteDateCreated)))>(c.tmeTargetMiss*60),'RED','ORANGE'),'BLACK') "
	                     + ",'Void' "
	                     + "FROM tblvoidkot a "
	                     + ",tbltablemaster b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e "
	                     + "WHERE LEFT(a.strItemCode,7)=c.strItemCode AND a.strTableNo=b.strTableNo "
	                     + "AND c.strItemCode=d.strItemCode AND a.strPOSCode=d.strPosCode "
	                     + "AND (d.strPosCode='"+POSCode+"' OR d.strPosCode='All') AND d.strCostCenterCode=e.strCostCenterCode "
	                     + "AND e.strCostCenterCode='"+CostCenterCode+"' And date(a.dteVoidedDate)='"+POSDate+"'  "
	                     + "AND a.strItemProcessed='N' AND a.strKOTNo NOT IN(SELECT strDocNo "
	                     + "FROM tblkdsprocess WHERE strBP='P' AND strKDSName='KOT') "
	                     + "GROUP BY a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
	                     + "ORDER BY a.dteDateCreated DESC, TIME(a.dteDateCreated) DESC"
	                     + ")ORDER BY strKOTNo ASC,dteKOTDate DESC ";
	            */
	            
	            System.out.println(sqlBillDtl);
	            
	            JSONArray arrObj=new JSONArray();
	            ResultSet rsKOTInfo = st.executeQuery(sqlBillDtl);
	            while (rsKOTInfo.next()) 
	            {
	            	JSONObject obj=new JSONObject();
	            	obj.put("KOTNo",rsKOTInfo.getString(1));
	            	obj.put("ItemCode",rsKOTInfo.getString(2));
	            	obj.put("ItemName",rsKOTInfo.getString(3));
	            	obj.put("Rate",rsKOTInfo.getString(4));
	            	obj.put("Qty",rsKOTInfo.getString(5));
	            	obj.put("Amount",rsKOTInfo.getString(6));
	            	obj.put("KOTDate",rsKOTInfo.getString(7));
	            	obj.put("KOTTime",rsKOTInfo.getString(8));
	            	obj.put("TableNo",rsKOTInfo.getString(9));
	            	obj.put("TableName",rsKOTInfo.getString(10));
	            	obj.put("CheckedTimeDiffResult",rsKOTInfo.getString(11));
	            	obj.put("ItemType",rsKOTInfo.getString(12));
	            	obj.put("WaiterNo",rsKOTInfo.getString(13));
	            	
	            	arrObj.put(obj);
	            }
	            rsKOTInfo.close();
	            jObj.put("KOTDetails", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj;
	        }
	    }
	   
	   
	   @SuppressWarnings("rawtypes")
		@POST
		@Path("/funUpdateProcessedKOTItemsForKDS")
		@Consumes(MediaType.APPLICATION_JSON)
		public JSONObject funUpdateProcessedKOTItemsForKDS(JSONObject jObjKDSData)
		{
		   JSONObject jObj=new JSONObject();
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null,st2=null,st3=null;
			String result="failed";
			String currentDateTime="";
			Date currentDate = new Date();
			currentDateTime = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
	                + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();

			try {
				
				String tableNo="",posCode="",areaCode="",operationType="",waiterNo="",itemCode="",kotDateTime="";
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
		        st2 = cmsCon.createStatement();
		        st3 = cmsCon.createStatement();
		        
		        String processingType=jObjKDSData.get("ProcessingType").toString();
		        String KOTNo=jObjKDSData.get("KOTNo").toString();
		        String userCode=jObjKDSData.get("UserCode").toString();
		        String costCenterCode=jObjKDSData.get("costCenterCode").toString();
		        String itemType=jObjKDSData.get("ItemType").toString();
		        String []currentTime=currentDateTime.split(" ");
		       
		        if(processingType.equals("Item"))
		        {
		        	
		        	waiterNo=jObjKDSData.get("waiterNo").toString();
				    kotDateTime=jObjKDSData.get("kotDateTime").toString();
				    itemCode=jObjKDSData.get("ItemCode").toString();
		        	
		        	String updateSql="";
		        	
		        	
		        	if(itemType.equals("Void"))
		        	{
		        		updateSql = "update tblvoidkot  set strItemProcessed='Y' "
		                        + "where strKOTNo='" + KOTNo + "' and strItemCode='" + itemCode + "' ";
		        	}
		        	else
		        	{
		        		updateSql = "update tblitemrtemp set strItemProcessed='Y',tmeOrderProcessing='" + currentTime[1] + "' "
		                        + " where strKOTNo='" + KOTNo + "' and strItemCode='" + itemCode + "' ";
		        	}
		        	
		        	st.executeUpdate(updateSql);
					
					/*StringBuilder sqlBillOrderProcess = new StringBuilder();
	                sqlBillOrderProcess.setLength(0);
	                sqlBillOrderProcess.append("insert into tblkdsprocess values");

	                String deleteQuery = "delete from tblkdsprocess  where strKDSName='KOT' and "
	                                    + " strDocNo='" + KOTNo + "' and strItemCode='"+itemCode+"' ";
	                st2.executeUpdate(deleteQuery);

	                sqlBillOrderProcess.append("('" + KOTNo + "','P','" + currentDateTime + "','" + currentDateTime + "','" + userCode + "','" + currentDateTime + "','" + userCode + "','KOT','"+itemCode+"','"+costCenterCode+"','"+waiterNo+"','"+kotDateTime+"')");  
	                st3.executeUpdate(sqlBillOrderProcess.toString());
					*/
					
		        	result="success";
		        }
		        else
		        {
		        	JSONArray mJsonArray=(JSONArray)jObjKDSData.get("KOTItemDtl");
		        	JSONObject mJsonObject = new JSONObject();
		        	StringBuilder sqlBillOrderProcess = new StringBuilder();
		        	StringBuilder sqlBillOrderProcessForInsert = new StringBuilder();
		        	//sqlBillOrderProcessForInsert.append("insert into tblkdsprocess values");
		        	
		        	
		        	for (int i = 0; i < mJsonArray.length(); i++) 
					{
					    mJsonObject =(JSONObject) mJsonArray.get(i);
					    
					    waiterNo=mJsonObject.get("waiterNo").toString();
					    kotDateTime=mJsonObject.get("kotDateTime").toString();
					    itemCode=mJsonObject.get("ItemCode").toString();
					    
					    
                        String updateSql="";
                        if(itemType.equals("Void"))
                        {
                            updateSql = "update tblvoidkot  set strItemProcessed='Y' "
                                + "where strKOTNo='" + KOTNo+ "' and strItemCode='" + itemCode + "' ";
                        }
                        else
                        {
                            updateSql = "update tblitemrtemp  set strItemProcessed='Y',tmeOrderProcessing='" + currentTime[1] + "' "
                                + "where strKOTNo='" + KOTNo + "' and strItemCode='" + itemCode + "' ";
                        }    
                        st.executeUpdate(updateSql);
					   
					  /*  sqlBillOrderProcess.setLength(0);
					    sqlBillOrderProcess.append("delete from tblkdsprocess where strKDSName='KOT' and "
	                             + " strDocNo='" + KOTNo + "' and strItemCode='"+itemCode+"' ");
			        	
			        	st.executeUpdate(sqlBillOrderProcess.toString());
			        	
			        	if (i == 0)
	                    {
			        		sqlBillOrderProcessForInsert.append("('" + KOTNo + "','P','" + currentDateTime + "','" + currentDateTime + "','" + userCode + "','" + currentDateTime+ "','" + userCode + "','KOT','"+itemCode+"','"+costCenterCode+"','"+waiterNo+"','"+kotDateTime+"')");
	                    }
	                    else
	                    {
	                    	sqlBillOrderProcessForInsert.append(",('" + KOTNo + "','P','" + currentDateTime + "','" + currentDateTime + "','" + userCode + "','" + currentDateTime + "','" + userCode + "','KOT','"+itemCode+"','"+costCenterCode+"','"+waiterNo+"','"+kotDateTime+"')");
	                    }
	                    */
					}    
		        	
		        	result="success";
		        }
				
		        jObj.put("result", result);
	            cmsCon.close();
	            st.close();
	            st2.close();
	            
			} catch (Exception e) 
			{
				// TODO Auto-generated catch block
				result="failed";
				try {
					jObj.put("result", result);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			return jObj;//Response.status(201).entity(result).build();		
		}
	   
	   
	   
	   @GET 
	   @Path("/funGetProcessedKOTDetailsForKPS")
	   @Produces(MediaType.APPLICATION_JSON)
	   public JSONObject funGetProcessedKOTDetailsForKPS(@QueryParam("POSCode") String POSCode,@QueryParam("WaiterNo") String WaiterNo,@QueryParam("POSDate") String POSDate)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	  
			
	        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="";
	            

	            
	           /* String sqlBillDtl = "SELECT a.strDocNo,a.strItemCode,b.strItemName,b.dblRate, SUM(b.dblItemQuantity), "
	            		+ " SUM(b.dblAmount), DATE_FORMAT(DATE(a.dteKOTDateAndTime),'%d-%m-%Y') AS dteKOTDate,a.strWaiterNo,TIME(a.dteKOTDateAndTime) "
	            		+ " from tblkdsprocess a,tblitemrtemp b,tblitemmaster c,tblmenuitempricingdtl d,tblcostcentermaster e,tblwaitermaster f "
	            		+ " where a.strDocNo=b.strKOTNo and a.strItemCode=b.strItemCode and a.strItemCode=c.strItemCode "
	            		+ " and b.strItemCode=c.strItemCode and c.strItemCode=d.strItemCode and d.strCostCenterCode=e.strCostCenterCode "
	            		+ " and f.strWaiterNo=a.strWaiterNo and f.strWaiterNo=b.strWaiterNo "
	            		+ " and f.strWaiterNo='"+WaiterNo+"' AND (d.strPosCode='"+POSCode+"' OR d.strPosCode='All') "
	            		+ " GROUP BY a.strDocNo,a.strItemCode "
	            		+ " ORDER BY a.dteKOTDateAndTime DESC, TIME(a.dteKOTDateAndTime) DESC; ";
	            */
	            String sqlBillDtl="SELECT a.strKOTNo,a.strItemCode,a.strItemName,a.dblRate, SUM(a.dblItemQuantity), "
	            		+ " SUM(a.dblAmount), DATE_FORMAT(DATE(a.dteDateCreated),'%d-%m-%Y') AS KOTDate,a.strWaiterNo,TIME(a.dteDateCreated) as KOTTime,"
	            		+ " a.tmeOrderProcessing AS OrderProcessTime,e.strWShortName,f.strTableNo,f.strTableName, "
	            		+ " IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), tmeOrderProcessing))>(b.intProcTimeMin*60), "
	            		+ " IF(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME(), tmeOrderProcessing))>(b.tmeTargetMiss*60),'RED','ORANGE'),'BLACK') "
	            		+ " from tblitemrtemp a,tblitemmaster b,tblmenuitempricingdtl c,tblcostcentermaster d,tblwaitermaster e,tbltablemaster f  "
	            		+ " where LEFT(a.strItemCode,7)=b.strItemCode and b.strItemCode=c.strItemCode "
	            		+ " and c.strCostCenterCode=d.strCostCenterCode and a.strWaiterNo=e.strWaiterNo and a.strTableNo=f.strTableNo "
	            		+ " and e.strWaiterNo='"+WaiterNo+"' and(c.strPosCode='"+POSCode+"' OR c.strPosCode='All') "
	            	    + " and a.strItemProcessed='Y' and a.strItemPickedUp='N' "
	            		+ " GROUP BY a.strTableNo,a.strKOTNo,a.strItemCode "
	            		+ " ORDER BY a.tmeOrderProcessing DESC ";
	            
	            
	            
	       
	            System.out.println(sqlBillDtl);
	            
	            JSONArray arrObj=new JSONArray();
	            ResultSet rsKOTInfo = st.executeQuery(sqlBillDtl);
	            while (rsKOTInfo.next()) 
	            {
	            	JSONObject obj=new JSONObject();
	            	obj.put("KOTNo",rsKOTInfo.getString(1));
	            	obj.put("ItemCode",rsKOTInfo.getString(2));
	            	obj.put("ItemName",rsKOTInfo.getString(3));
	            	obj.put("Rate",rsKOTInfo.getString(4));
	            	obj.put("Qty",rsKOTInfo.getString(5));
	            	obj.put("Amount",rsKOTInfo.getString(6));
	            	obj.put("KOTDate",rsKOTInfo.getString(7));
	            	obj.put("WaiterNo",rsKOTInfo.getString(8));
	            	obj.put("KOTTime",rsKOTInfo.getString(9));
	            	obj.put("ProcessTime",rsKOTInfo.getString(10));
	            	obj.put("WaiterName",rsKOTInfo.getString(11));
	            	obj.put("TableNo",rsKOTInfo.getString(12));
	            	obj.put("TableName",rsKOTInfo.getString(13));
	            	obj.put("CheckedTimeDiffResult",rsKOTInfo.getString(14));
	            	
	            	arrObj.put(obj);
	            }
	            rsKOTInfo.close();
	            jObj.put("KOTDetails", arrObj);
	            st.close();
	            cmsCon.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj;
	        }
	    }
	   
	   
	   
	   
	   @SuppressWarnings("rawtypes")
		@POST
		@Path("/funUpdatePickedUpKOTItemsForKPS")
		@Consumes(MediaType.APPLICATION_JSON)
		public JSONObject funUpdatePickedUpKOTItemsForKPS(JSONObject jObjKPSData)
		{
		    JSONObject jObj=new JSONObject();
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null,st2=null,st3=null;
			String result="failed";
			String currentDateTime="",updateSql="";
			Date currentDate = new Date();
			currentDateTime = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
	                + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();

			try {
				
				String tableNo="",posCode="",areaCode="",operationType="",waiterNo="",itemCode="",kotDateTime="";
				cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
		        st2 = cmsCon.createStatement();
		        st3 = cmsCon.createStatement();
		        
		        String processingType=jObjKPSData.get("ProcessingType").toString();
		        String KOTNo=jObjKPSData.get("KOTNo").toString();
		        String userCode=jObjKPSData.get("UserCode").toString();
		        waiterNo=jObjKPSData.get("waiterNo").toString();
		        String []currentTime=currentDateTime.split(" ");
		       
		        if(processingType.equals("Item"))
		        {
		        	itemCode=jObjKPSData.get("ItemCode").toString();
		        	updateSql = "update tblitemrtemp  set strItemPickedUp='Y',tmeOrderPickup='" + currentTime[1] + "' "
                            + "where strKOTNo='" + KOTNo + "' and strItemCode='" + itemCode + "' ";   
                    st.executeUpdate(updateSql);
					
					result="success";
		        }
		        else
		        {
		        	JSONArray mJsonArray=(JSONArray)jObjKPSData.get("KOTItemDtl");
		        	JSONObject mJsonObject = new JSONObject();
		        	
		        	for (int i = 0; i < mJsonArray.length(); i++) 
					{
					    mJsonObject =(JSONObject) mJsonArray.get(i);
					    
					   itemCode=mJsonObject.get("ItemCode").toString();
					   updateSql = "update tblitemrtemp  set strItemPickedUp='Y',tmeOrderPickup='" + currentTime[1] + "' "
                               + "where strKOTNo='" + KOTNo + "' and strItemCode='" + itemCode + "' ";   
                       st.executeUpdate(updateSql);
					}    
		        	
		        	result="success";
		        }
				
		        jObj.put("result", result);
		        cmsCon.close();
		        st.close();
		        st2.close();
		        st3.close();
		        
	            
			} catch (Exception e) 
			{
				// TODO Auto-generated catch block
				result="failed";
				try {
					jObj.put("result", result);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			return jObj;		
		}
	   
	   
	   
	   private void funRemotePrint(String KotNo,String POSCode,String KOTTableNo,String deviceName)
	    {
	    	
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        try
	        {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();

	            String sql = "select a.strItemName,c.strCostCenterCode,c.strPrinterPort,a.strItemCode "
	                    + "from tblvoidkot a,tblmenuitempricingdtl b,tblcostcentermaster c "
	                    + "where left(a.strItemCode,7)=b.strItemCode and b.strCostCenterCode=c.strCostCenterCode "
	                    + "and a.strKOTNo='" + KotNo + "' and a.strPrintKOT='Y' "
	                    + "and b.strPOSCode='" + POSCode + "' "
	                    + "group by c.strCostCenterCode";
	            ResultSet rsPrint = st.executeQuery(sql);
	            
	            while (rsPrint.next())
               {
                   String costCenterCode = rsPrint.getString(2);
                   objTextFormatVoidKOT.funGenerateVoidKOT(KOTTableNo, KotNo, "VoidKOT", costCenterCode,POSCode,deviceName);
	           }
	            rsPrint.close();
	            cmsCon.close();
	            st.close();
	            
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	   
	   
	   @GET 
		@Path("/funCheckAPOSLicence")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funCheckAPOSLicence(@QueryParam("clientCode") String clientCode,@QueryParam("physicalAddress") String physicalAddress,@QueryParam("hostName") String hostName,@QueryParam("userCode") String userCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        String response="";

	        JSONArray arrObj=new JSONArray();
	        Date objDate = new Date();
	        int day = objDate.getDate();
	        int month = objDate.getMonth() + 1;
	        int year = objDate.getYear() + 1900;
	        String currentDate = year + "-" + month + "-" + day;
	        
	        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="";
	            
	            sql = "select strClientCode,strClientName from tblsetup";
	            ResultSet rs = st.executeQuery(sql);
	            if (rs.next())
	            {
	                String tempClientCode = rs.getString(1);
	                String clientName = rs.getString(2);
	                
	                clsClientDetails.funAddClientCodeAndName();
	                Date POSExpiryDate = clsClientDetails.funCheckPOSLicense(tempClientCode, clientName);
	                if(null==POSExpiryDate)
	                {
	                	//JSONObject obj=new JSONObject();
	                	jObj.put("Status", "Invalid");
	                	jObj.put("Msg", "Invalid POS. Please Contact Technical Support.");
	                   // arrObj.put(obj);
	                }
	                else
	                {
		                long ExpiryDateTime = POSExpiryDate.getTime();
		                long TimeDifference = 0;
		                String billDate = "";
		                String sqlMaxBillDate = "select ifnull(max(date(dteBillDate)),0) from tblqbillhd";
		                ResultSet rsMaxBillDate = st.executeQuery(sqlMaxBillDate);
		                if (rsMaxBillDate.next())
		                {
		                	SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		                    Date systemDate = dFormat.parse(dFormat.format(new Date()));
		                	billDate = rsMaxBillDate.getString(1);
		                    if (!billDate.equals("0"))
		                    {
		                    	Date gMaxBillDate = dFormat.parse(billDate);
		                        TimeDifference = ExpiryDateTime - gMaxBillDate.getTime();
		                        long diffDays = TimeDifference / (24 * 60 * 60 * 1000);
		                        if (diffDays <= 15)
		                        {
		                        	jObj.put("Status", "MinDays");
		                        	jObj.put("Msg", diffDays + " Days Remaining For Licence to Expire");
		                        }
		                    }
		                    else
		                    {
		                    	TimeDifference = ExpiryDateTime - systemDate.getTime();
		                    }
		                    
		                    if (TimeDifference >= 0)
		                    {
		                    	
		                    	jObj=funValidateTerminalRegistrationDetails(physicalAddress,hostName,clientCode,userCode);
		                    }
		                    else
		                    {
		                    	jObj.put("Status", "Expired");
		                    	jObj.put("Msg", "License Expired. Please Contact Technical Support.");
		                    }
		                }
		                rsMaxBillDate.close();
	                }
	            }
	            else
	            {
	            	jObj.put("Status", "Invalid");
	            	jObj.put("Msg", "Invalid POS. Please Contact Technical Support.");
	               
	            }
	          
		       // jObj.put("CheckLicence", arrObj);
		        st.close();
		        cmsCon.close();
		            
	        } catch (Exception e) {
		        e.printStackTrace();
		    }
		    finally
		    {
		       if(!jObj.has("Status")){
		    	   try {
						jObj.put("Status", "Invalid");
						jObj.put("Msg", "Licence not found.");
					} catch (JSONException e) {
						e.printStackTrace();
					}   
		       }
		    	
            	
		        return jObj;
		    }
		}
       
	   
	   
    public JSONObject funValidateTerminalRegistrationDetails(String macAddress,String hostName,String clientCode,String userCode) 
   	{
   		JSONObject objResult=new JSONObject();
   		boolean hasTerminalLicence=false;
   		JSONObject jObj=funGetTerminalRegistrationDetails(macAddress,"APOS",clientCode);
   			
   			if (null != jObj) 
   			{

				try {
					
					String[] arrTerminalData = jObj.get("MaxTerminal").toString().split("\\.");
					int intMAXTerminalFromDB = Integer.parseInt(arrTerminalData[0]);
					String isRegistered = "No";
					int intMAXTerminalFromLicence = funGetMAXTerminalFromLicence(clientCode);
					if (intMAXTerminalFromDB == intMAXTerminalFromLicence) {
						if (jObj.get("RegisterTerminal").toString().equalsIgnoreCase("True")) 
						{
							hasTerminalLicence = true;
						} 
						else 
						{
							hasTerminalLicence = false;
						}

					} else if (intMAXTerminalFromDB < intMAXTerminalFromLicence) {
						if (jObj.get("RegisterTerminal").toString().equalsIgnoreCase("True")) 
						{
							hasTerminalLicence = true;
						}
						else
						{
							JSONObject res=funRegisterTerminal(hostName, macAddress, clientCode, userCode, "APOS");
							if(res.get("Status").toString().equalsIgnoreCase("success"))
							{
								hasTerminalLicence = true;
							}
							else
							{
								hasTerminalLicence = false;
							}	
						}
					} else {
						hasTerminalLicence = false;
					}
					if (hasTerminalLicence)
					{
						String posVersion=funSetPOSVerion(clientCode);
						objResult.put("Status", "success");
						objResult.put("POSVersion",posVersion);
					}
					else
					{
						if (intMAXTerminalFromDB > intMAXTerminalFromLicence)
						{
							objResult.put("Status", "Terminal Exceeded");
							objResult.put("POSVersion","NotFound");
						}
						else if (intMAXTerminalFromDB == intMAXTerminalFromLicence) 
						{
							objResult.put("Status", "Terminal Exceeded");
							objResult.put("POSVersion","NotFound");
						}
					}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
   		else 
   		{
   			try {
				objResult.put("Status", "Please Contact Technical Support!!");
				objResult.put("POSVersion","NotFound");
			} catch (JSONException e) {
				e.printStackTrace();
			}
				
		}
           return objResult;
   	}

    private int funGetMAXTerminalFromLicence(String clientCode) {
   		int intMAXTerminal = 0;
   		try {
   			clsClientDetails objClientDetails = clsClientDetails.hmClientDtl.get(clsEncryptDecryptClientCode.funEncryptClientCode(clientCode));
   			String trminal = clsEncryptDecryptClientCode.funDecryptClientCode(String.valueOf(objClientDetails.getIntMAXAPOSTerminals()));
   			intMAXTerminal=Integer.parseInt(trminal);
   			System.out.println("Total APOS Terminals :"+trminal);

   		} catch (Exception e) {
   			e.printStackTrace();
   		} finally {
   			return intMAXTerminal;
   		}
   	}
   	
   	private String funSetPOSVerion(String strClientCode) 
   	{
   		String posVersion="";
   		clsClientDetails objClientDetails = clsClientDetails.hmClientDtl.get(clsEncryptDecryptClientCode.funEncryptClientCode(strClientCode));
   		return posVersion = clsEncryptDecryptClientCode.funDecryptClientCode(objClientDetails.getPosVersion());
   	}
  
	
	@GET 
	@Path("/funLoadReprintBillList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funLoadReprintBillList(@QueryParam("POSCode") String POSCode,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = "select a.strbillno as BillNo,ifnull(b.strTableName,'ND') as TableName,"
            		+ " TIME_FORMAT(time(a.dteBillDate),'%h:%i') as Time ,a.strPOSCode as POSCode,a.dblGrandTotal as TotalAmount"
            		+ " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
            		+ "where a.strClientCode='"+clientCode+"' ";
            if(!POSCode.equals("All"))
            {
                sql+= " and a.strPOSCode ='" + POSCode + "'";
            }
            sql+= " order by a.strbillno DESC";
            
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("BillNo",rsTableInfo.getString(1));
            	obj.put("TableName",rsTableInfo.getString(2));
            	obj.put("Time",rsTableInfo.getString(3));
            	obj.put("POSCode",rsTableInfo.getString(4));
            	obj.put("Amount",rsTableInfo.getString(5));
            	arrObj.put(obj);
            }
            rsTableInfo.close();
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	
	
	@GET 
	@Path("/funLoadReprintDirectBillList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funLoadDirectBillList(@QueryParam("POSCode") String POSCode,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            sql = " select a.strbillno as BillNo,TIME_FORMAT(time(a.dteBillDate),'%h:%i') as Time ,"
            		+ " b.strPOSName as POS ,a.dblGrandTotal as TotalAmount "
            		+ " from tblbillhd a ,tblposmaster b  "
            		+ " where a.strPOSCode=b.strPOSCode  and a.strTableNo='' Or a.strTableNo='TB0000' ";
            
            if(!POSCode.equals("All"))
            {
                sql+= " and a.strPOSCode ='" + POSCode + "'";
            }
            sql+= " order by a.strbillno DESC";
            
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("BillNo",rsTableInfo.getString(1));
            	obj.put("Time",rsTableInfo.getString(2));
            	obj.put("POSCode",rsTableInfo.getString(3));
            	obj.put("Amount",rsTableInfo.getString(4));
            	arrObj.put(obj);
            }
            rsTableInfo.close();
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
    }
	

	@GET
	@Path("/funGetItemPriceDtlList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetItemPriceDtlList(@QueryParam("POSCode") String POSCode,@QueryParam("fromDate") String fromDate
    	, @QueryParam("toDate") String toDate )
	{
		return funFetchItemPriceDtlList(POSCode, fromDate, toDate);
	}
	
	private JSONArray funFetchItemPriceDtlList(String POSCode
			, String fromDate, String toDate)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        String gAreaCodeForTrans="";
        JSONArray arrObj=new JSONArray();
        System.out.println("FD="+fromDate);
        System.out.println("TD="+toDate);
        System.out.println("POS="+POSCode);
        
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
        	
			String sql="",sqlImg="";
		
			sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday, a.strPriceWednesday, "
				+ " a.strPriceThursday,a.strPriceFriday, a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo, "
				+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strExternalCode,b.strItemImage,a.strAreaCode "
				+ " ,a.strMenuCode,b.strItemVoiceCaptureText,IF(c.strItemCode!='','Y','N') "
				+ " FROM tblmenuitempricingdtl a,tblitemmaster b LEFT OUTER JOIN tblnonavailableitems c ON b.strItemCode=c.strItemCode AND c.strPOSCode='"+POSCode+"' AND DATE(c.dteDate)='"+fromDate+"' "
				+ " WHERE a.strItemCode=b.strItemCode  AND (a.strPosCode='"+POSCode+"' OR a.strPosCode='All') "
				+ " AND DATE(a.dteFromDate)<='"+fromDate+"' AND DATE(a.dteToDate)>='"+toDate+"' "
				+ " ORDER BY b.strItemName ,a.strHourlyPricing ASC ";

			System.out.println(sql);
		     
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("ItemCode",rsMasterData.getString(1));
            	obj.put("ItemName",rsMasterData.getString(2));
            	obj.put("TextColor",rsMasterData.getString(3));
            	obj.put("PriceMonday",rsMasterData.getString(4));
            	obj.put("PriceTuesday",rsMasterData.getString(5));
            	obj.put("PriceWenesday",rsMasterData.getString(6));
            	obj.put("PriceThursday",rsMasterData.getString(7));
            	obj.put("PriceFriday",rsMasterData.getString(8));
            	obj.put("PriceSaturday",rsMasterData.getString(9));
            	obj.put("PriceSunday",rsMasterData.getString(10));
            	obj.put("TimeFrom",rsMasterData.getString(11));
            	obj.put("AMPMFrom",rsMasterData.getString(12));
            	obj.put("TimeTo",rsMasterData.getString(13));
            	obj.put("AMPMTo",rsMasterData.getString(14));
            	obj.put("CostCenterCode",rsMasterData.getString(15));
            	obj.put("HourlyPricing",rsMasterData.getString(16));
            	obj.put("SubMenuHeadCode",rsMasterData.getString(17));
            	obj.put("FromDate",rsMasterData.getString(18));
            	obj.put("ToDate",rsMasterData.getString(19));
            	obj.put("ExternalCode",rsMasterData.getString(20));
            	obj.put("AreaCode",rsMasterData.getString(22));
            	obj.put("MenuCode",rsMasterData.getString(23));
            	obj.put("strVoiceTextSaved",rsMasterData.getString(24));
            	obj.put("NAStatus",rsMasterData.getString(25));
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
          //  jObj.put("tblmenuitempricingdtl", arrObj);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;//jObj.toString();
	}
	
	
	@POST
	@Path("/funGetBillSeriesList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetBillSeriesList(String POSCode,JSONArray jArrItemList )
	{
		StringBuilder sqlBuilder = new StringBuilder();
		//Map<String, List<clsBillItemDtl>> hmBillSeriesItemList = new HashMap<String, List<clsBillItemDtl>>();
		JSONObject jObjBillSeriesItemList=new JSONObject();
		JSONArray jArrBillSeriesItemList=new JSONArray();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
		try{
			
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            
			for(int i=0;i<jArrItemList.length();i++){
				JSONObject jObjItemData=(JSONObject)jArrItemList.get(i);
				
				sqlBuilder.setLength(0);
				sqlBuilder.append(" select * from tblbillseries where (strPOSCode='" + POSCode + "' or strPOSCode='All') ");
				ResultSet rsBillSeriesType = st.executeQuery(sqlBuilder.toString());
                boolean isExistsBillSeries = false;
                while (rsBillSeriesType.next())
                {
                	String billSeriesType = rsBillSeriesType.getString("strType");

                    sqlBuilder.setLength(0);
                    sqlBuilder.append("select a.strItemCode,a.strItemName,a.strRevenueHead,b.strPosCode,c.strMenuCode,c.strMenuName "
                            + " ,d.strSubGroupCode,d.strSubGroupName,e.strGroupCode,e.strGroupName "
                            + " from tblitemmaster a,tblmenuitempricingdtl b,tblmenuhd c,tblsubgrouphd d,tblgrouphd e "
                            + " where a.strItemCode=b.strItemCode and b.strMenuCode=c.strMenuCode "
                            + " and a.strSubGroupCode=d.strSubGroupCode and d.strGroupCode=e.strGroupCode ");
                    sqlBuilder.append(" and (b.strPosCode='" + POSCode + "' Or b.strPosCode='All') ");
                    sqlBuilder.append(" and a.strItemCode='" + jObjItemData.getString("ItemCode").substring(0, 7) + "' ");

                   
                    String filter = " e.strGroupCode ";
                    if (billSeriesType.equalsIgnoreCase("Group"))
                    {
                        filter = " e.strGroupCode ";
                    }
                    else if (billSeriesType.equalsIgnoreCase("Sub Group"))
                    {
                        filter = " d.strSubGroupCode ";
                    }
                    else if (billSeriesType.equalsIgnoreCase("Menu Head"))
                    {
                        filter = " c.strMenuCode ";
                    }
                    else if (billSeriesType.equalsIgnoreCase("Revenue Head"))
                    {
                        filter = " a.strRevenueHead ";
                    }
                    else
                    {
                        filter = "  ";
                    }
                    sqlBuilder.append(" and " + filter + " IN " + funGetCodes(rsBillSeriesType.getString("strCodes")));
                    sqlBuilder.append(" GROUP BY a.strItemCode; ");

                    ResultSet rsIsExists = st1.executeQuery(sqlBuilder.toString());
                
                    if (rsIsExists.next())
                    {
                    	
                        isExistsBillSeries = true;
                        if (jObjBillSeriesItemList.has(rsBillSeriesType.getString("strBillSeries")))
                        {
                        	JSONArray jArr=(JSONArray)jObjBillSeriesItemList.get(rsBillSeriesType.getString("strBillSeries"));
                        	//jObjItemData.put("billNo",billSeriesBillNo);
                        	jArr.put(jObjItemData);
                        	jObjBillSeriesItemList.put(rsBillSeriesType.getString("strBillSeries"), jArr);
                        }
                        else
                        {
                        	
                        	JSONArray jArr=new JSONArray();
                        	//jObjItemData.put("billNo",billSeriesBillNo);
                        	jArr.put(jObjItemData);
                        	jObjBillSeriesItemList.put(rsBillSeriesType.getString("strBillSeries"), jArr);
                            
                        }
                        break;
                    }
                }
                if (!isExistsBillSeries)
                {
                    if (jObjBillSeriesItemList.has("NoBillSeries"))
                    {
                    	JSONArray jArr=(JSONArray)jObjBillSeriesItemList.get("NoBillSeries");
                    	jArr.put(jObjItemData);
                    }
                    else
                    {
                    	JSONArray jArr=new JSONArray();
                    	jArr.put(jObjItemData);
                    	jObjBillSeriesItemList.put("NoBillSeries", jArr);
                      
                    }
                }
			}
			cmsCon.close();
			st.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return jObjBillSeriesItemList;
	}
	
	private String funGetCodes(String codes)
    {
        StringBuilder codeBuilder = new StringBuilder("(");
        try
        {
            String code[] = codes.split(",");
            for (int i = 0; i < code.length; i++)
            {
                if (i == 0)
                {
                    codeBuilder.append("'" + code[i] + "'");
                }
                else
                {
                    codeBuilder.append(",'" + code[i] + "'");
                }
            }
            codeBuilder.append(")");
        }
        catch (Exception e)
        {
            //objUtility.funWriteErrorLog(e);
            e.printStackTrace();
        }
        finally
        {
            return codeBuilder.toString();
        }
    }


	
@SuppressWarnings("rawtypes")
@POST
@Path("/funSaveAllBillData")
@Consumes(MediaType.APPLICATION_JSON)
public JSONObject funSaveAllBillData(JSONObject objBillData,@QueryParam("billSeries") String billSeries,@QueryParam("POSCode") String strPOSCode)
{

	JSONObject jObResponse=new JSONObject();
	JSONArray jArrResponse=new JSONArray();
	String data="";
	String billNo="";
	String checkHomeDelivery="";
	String response = "false";
	
	clsDatabaseConnection objDb=new clsDatabaseConnection();
	Connection cmsCon=null;
	Statement st = null;
	try
	{
		cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
		st = cmsCon.createStatement();
		
		if(billSeries.equals("Y")){
			jArrResponse =funInsertDataBillSeriesWise(objBillData,strPOSCode);
		}
		else
		{
			jArrResponse =funInsertBillData(objBillData,strPOSCode);

		}
		
		jObResponse.put("response", jArrResponse);
		
	}catch(Exception e){
		e.printStackTrace();
	}
	
	return jObResponse; // Response.status(201).entity(response).build();
}

private JSONArray funInsertDataBillSeriesWise(JSONObject objBillData,String strPOSCode){
	
		//JSONObject jObResponse=new JSONObject();
		JSONArray jArrResponse=new JSONArray();
		String data="";
		String billNo="",clientCode="",userCode="",posDate="";
		String checkHomeDelivery="";
		String response = "false";
		
		//String GenaratedBillSeriesNo=""; 
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		Connection cmsCon=null;
		Statement st = null;
		try{
			
			listBillSeriesBillDtl = new ArrayList<>();

			cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			
			
			// bill from direct biller and make bill -- for genarate new bill no
			if(objBillData.has("BillDtlData"))
			{
				JSONObject jObjBillSeriesItemList=new JSONObject();
				Iterator itrItemData=objBillData.keys();
				JSONArray mJsonArray =new JSONArray();
				while(itrItemData.hasNext())
				{
				
					String key = itrItemData.next().toString();
					if(!key.equals("BillPromoDiscountData")){
						mJsonArray = (JSONArray) objBillData.get(key);
					}
					JSONObject mJsonObject;
					if (key.equalsIgnoreCase("BillDtlData") && mJsonArray !=null) 
					{	
						 jObjBillSeriesItemList=funGetBillSeriesList(strPOSCode,mJsonArray);
					}
				}
				if(jObjBillSeriesItemList.length()>0)
				{
					 	if (jObjBillSeriesItemList.has("NoBillSeries"))
		                {
							JSONObject jObResponse=new JSONObject();
							jObResponse.put("NoBillSeries","No BillSeries found");
							jArrResponse.put(jObResponse);
						    return jArrResponse;
		                }
						Iterator billSeriesIt = jObjBillSeriesItemList.keys();
			            while (billSeriesIt.hasNext())
			            {
			            	JSONObject jObResponse=new JSONObject();
			            	double taxAmt=0,dblSubTotal=0,dblGrandTotal=0;
			            	JSONObject jObTaxItems=new JSONObject();
			            	String billSeriesBillNo="";
			            	String BSkey = billSeriesIt.next().toString();
							JSONArray mJsonBSArray=(JSONArray) jObjBillSeriesItemList.get(BSkey);
							billSeriesBillNo=funGenarateBillSeriesNo(strPOSCode,BSkey);
							
							jObResponse.put("GenaratedBillSeriesNo", billSeriesBillNo);
							/*if(GenaratedBillSeriesNo.equals("")){
								GenaratedBillSeriesNo=billSeriesBillNo;
							}else{
								GenaratedBillSeriesNo=GenaratedBillSeriesNo+"#"+billSeriesBillNo;
							}*/
							
							JSONObject jObTax,jObItemDtl;
							JSONArray jsonArrTax=new JSONArray();
							for(int j=0;j<mJsonBSArray.length();j++){
								jObItemDtl=mJsonBSArray.getJSONObject(j);
								//All item data send to tax calculation
								jObTax=new JSONObject();
								jObTax.put("strPOSCode",strPOSCode);
								jObTax.put("strItemCode",jObItemDtl.get("ItemCode"));
								jObTax.put("strItemName",jObItemDtl.get("ItemName"));
								jObTax.put("dblItemQuantity",jObItemDtl.get("Quantity"));
								jObTax.put("dblAmount",jObItemDtl.get("Amount"));
								jObTax.put("strClientCode",jObItemDtl.get("ClientCode"));
								jObTax.put("OperationType",jObItemDtl.get("OperationType"));//operationTypeFor Tax
								jObTax.put("AreaCode",jObItemDtl.get("AreaCode"));
		                        
								jObTax.put("POSDate",jObItemDtl.getString("BillDt"));
	
		                        jsonArrTax.put(jObTax);
		                        dblSubTotal+=jObItemDtl.getDouble("Amount");
		                        
		                      //update old bill no with new billseries bill no
		                        jObItemDtl.put("BillNo", billSeriesBillNo);
		                        
		                        //assign values to local variable from json object
		                        clientCode=jObItemDtl.getString("ClientCode");
		                        posDate=jObItemDtl.getString("BillDt");
		                        userCode=jObItemDtl.getString("UserCode");
		                        
							}
							jObTaxItems.put("TaxDtl", jsonArrTax);
							JSONObject jObjTaxData=funCalculateTax(jObTaxItems);
							if(jObjTaxData.length()>0)
							{
								JSONArray jArrTaxList=jObjTaxData.getJSONArray("listOfTax");
								String tax=jObjTaxData.getString("totalTaxAmt");
								if(tax.equals("")){
									taxAmt=0;
								}else{
									taxAmt=	Double.parseDouble(tax);	
								}
						        
							} 
							dblGrandTotal=taxAmt+dblSubTotal;
							
							clsBillSeriesBillDtl objBillSeriesBillDtl = new clsBillSeriesBillDtl();
				            objBillSeriesBillDtl.setStrHdBillNo(billSeriesBillNo);
				            objBillSeriesBillDtl.setStrBillSeries(BSkey);
				            objBillSeriesBillDtl.setDblGrandTotal(dblGrandTotal);
				            listBillSeriesBillDtl.add(objBillSeriesBillDtl);
							
							Iterator callfunction=objBillData.keys();
							while(callfunction.hasNext())
							{
								String key = callfunction.next().toString();
								JSONObject mJsonOb=new JSONObject();
								mJsonArray =new JSONArray();
								if(key.equals("BillPromoDiscountData")){
									
									mJsonOb=(JSONObject) objBillData.get(key); //In promotion json object have two more jsons..
								}
								else{
									mJsonArray = (JSONArray) objBillData.get(key);	
								}
								JSONObject mJsonObject;
								for (int i = 0; i < mJsonArray.length(); i++) 
								{
									//here update amount fields -- each bill
									mJsonObject =(JSONObject) mJsonArray.get(i);
									if(mJsonObject.has("BillNo")){
										mJsonObject.put("BillNo", billSeriesBillNo);	
									}
									if(mJsonObject.has("TaxAmount")){
										mJsonObject.put("TaxAmount", taxAmt);	
									}
									if(mJsonObject.has("SubTotal")){
										mJsonObject.put("SubTotal", dblSubTotal);	
									}
									if(mJsonObject.has("GrandTotal")){
										mJsonObject.put("GrandTotal", dblGrandTotal);	
									}
									
								}
								
								if (key.equalsIgnoreCase("BillHDData") && mJsonArray !=null) 
								{
									System.out.println("BillHd= "+key);
									data=funInsertBillHdData(mJsonArray);
									billNo=data.split("#")[0];
									System.out.println("BillNo="+billNo);
									checkHomeDelivery=data.split("#")[1];
									System.out.println("checkHomeDelivery="+checkHomeDelivery);
									jObResponse.put("BillHDData", data);
									/*if (funInsertBillHdData(mJsonArray) != null) 
									{
										response = "BillHd";
									} else{
										response = "false";
									}
									*/
									
								}
								else if (key.equalsIgnoreCase("BillDtlData") && mJsonBSArray !=null) 
								{
									System.out.println("BillDtl= "+key);
									boolean isModifierPresentInBill=false;
									if(objBillData.get("BillModifierData")!=null && objBillData.getJSONArray("BillModifierData").length()>0){
										isModifierPresentInBill=true;
									}
									
									if (funInsertBillDtlData(mJsonBSArray,isModifierPresentInBill) > 0) 
									{	
										jObResponse.put("BillDtl", "BillDtl");
									} else{
										jObResponse.put("BillDtl", "false");
									}
									
								}
								else if (key.equalsIgnoreCase("BillModifierData") && mJsonArray !=null) 
								{
									System.out.println(key);
									if (funInsertBillModifierDtlData(mJsonArray) > 0) 
									{
										jObResponse.put("BillModifier", "BillModifier");
										
									} else{
										jObResponse.put("BillModifier", "false");
										
									}
								}
								else if (key.equalsIgnoreCase("BillSettlementData") && mJsonArray !=null) 
								{
									System.out.println(key);
									//this code same as POS src
									JSONArray jArrBillSettlementDtl=new JSONArray();
									String settleName="";
									boolean isBillSettled = false;
									double billGrandTotalAmt = dblGrandTotal;
									for(int l=0;l<mJsonArray.length();l++){
										JSONObject jsob=mJsonArray.getJSONObject(l);
										settleName = jsob.getString("SettlementDesc");
										double settleAmt = 0;
					                    if (billGrandTotalAmt > jsob.getDouble("PaidAmt"))
					                    {
					                        settleAmt =jsob.getDouble("PaidAmt");
					                        jsob.put("PaidAmt","0.00");
					                    }
					                    else
					                    {
					                        settleAmt = billGrandTotalAmt;
					                        jsob.put("PaidAmt",String.valueOf(jsob.getDouble("PaidAmt") - settleAmt));
					                        isBillSettled = true;
					                    }
					                    billGrandTotalAmt = billGrandTotalAmt - settleAmt;
					                    jsob.put("SettlementAmt", settleAmt);
					                    jsob.put("PaidAmt", settleAmt);
					                    jsob.put("ActualAmt", settleAmt);
					                   
					                    jArrBillSettlementDtl.put(jsob);
									}
									if (funInsertBillSettlementDtlData(jArrBillSettlementDtl) > 0) 
									{
										jObResponse.put("BillSettlement", "BillSettlement");
										
									} else{
										jObResponse.put("BillSettlement", "false");
									}
								}
								else if (key.equalsIgnoreCase("BillTaxData") && mJsonBSArray !=null) 
								{
									/*sent bill wise item details to calculate tax*/
									System.out.println(key);
									if (funInsertBillTaxData(mJsonBSArray) > 0) 
									{
										jObResponse.put("BillTax", "BillTax");
									} else{
										jObResponse.put("BillTax", "false");
									}
								}
								else if (key.equalsIgnoreCase("BillPromoDiscountData") && mJsonOb !=null) 
								{
									Iterator itr=mJsonOb.keys();
									while(itr.hasNext())
									{
										//In Bill promotion JSONObject there are two JSONArrays .. so need to iterate them 
										JSONArray mJsonSubArray = (JSONArray) mJsonOb.get(itr.next().toString());	
										for(int m=0;m<mJsonSubArray.length();m++){
											mJsonSubArray.getJSONObject(m).put("BillNo",billSeriesBillNo);
										}
										if (funInsertBillPromotionDtlData(mJsonSubArray) > 0) 
										{
											jObResponse.put("BillPromoDiscountData", "false");
											
										} else{
											jObResponse.put("BillPromoDiscountData", "false");
										}
									}
								}
								else if (key.equalsIgnoreCase("BillDiscountDtl") && mJsonArray !=null) 
								{
									if (funInsertBillDiscountDetail(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("BillComplementaryData") && mJsonArray !=null) 
								{
									if (funInsertBillComplementoryDtlData(mJsonArray) > 0) 
									{
										jObResponse.put("BillComplementory", "BillComplementory");
									} else{
										jObResponse.put("BillComplementory", "false");
									}
								}
								else if (key.equalsIgnoreCase("CRMBillPoints") && mJsonArray !=null) 
								{
									if (funInsertCRMPointsData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("VoidBillHd") && mJsonArray !=null) 
								{
									System.out.println(key);
									if (funInsertVoidBillHdData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("VoidBillDtl") && mJsonArray !=null) 
								{
									if (funInsertVoidBillDtlData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("StkInHd") && mJsonArray !=null) 
								{
									if (funInsertStkInHdData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("StkInDtl") && mJsonArray !=null) 
								{
									System.out.println(key); 
									if (funInsertStkInDtlData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("StkOutHd") && mJsonArray !=null) 
								{
									if (funInsertStkOutHdData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								} else if (key.equalsIgnoreCase("StkOutDtl") && mJsonArray !=null) 
								{
									if (funInsertStkOutDtlData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								} else if (key.equalsIgnoreCase("VoidKot") && mJsonArray !=null) 
								{
									if (funInsertVoidKotData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								} else if (key.equalsIgnoreCase("PspHd") && mJsonArray !=null) 
								{
									if (funInsertPspHdData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("PspDtl") && mJsonArray !=null) 
								{
									if (funInsertPspDtlData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								} else if (key.equalsIgnoreCase("AdvanceReceiptHd") && mJsonArray !=null) 
								{
									System.out.println(key);
									if (funInsertAdvanceReceiptHdData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("AdvanceReceiptDtl") && mJsonArray !=null) 
								{
									if (funInsertAdvanceReceiptDtlData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								} else if (key.equalsIgnoreCase("AdvBookBillHd") && mJsonArray !=null) 
								{
									System.out.println(key); 
									if (funInsertAdvBookBillHdData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("AdvBookBillDtl") && mJsonArray !=null) 
								{
									System.out.println(key);
									if (funInsertAdvBookBillDtlData(mJsonArray) > 0) 
									{
										response = "true";
									} else{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("AdvBookBillModifierDtl") && mJsonArray !=null) 
								{
									System.out.println(key); 
									if (funInsertAdvBookBillModiferDtlData(mJsonArray) > 0) 
									{
										response = "true";
									}
									else
									{
										response = "false";
									}
								}
								else if (key.equalsIgnoreCase("HomeDelivery") && mJsonArray !=null) 
								{
									System.out.println(key); 
									if (funInsertHomeDeliveryData(mJsonArray) > 0) 
									{
										jObResponse.put("HomeDelivery", "true");
									} else{
										jObResponse.put("HomeDelivery", "false");
									}
								}
								else if (key.equalsIgnoreCase("HomeDeliveryDtl") && mJsonArray !=null) 
								{
									System.out.println(key); 
									if (funInsertHomeDeliveryDtlData(mJsonArray) > 0) 
									{
										jObResponse.put("HomeDeliveryDtl", "true");
									} else{
										jObResponse.put("HomeDeliveryDtl", "false");
									}
								}
								if(checkHomeDelivery.equals("HomeDelivery"))
								  {
									    funBuildSMS(billNo);
								  }
							}
			            jArrResponse.put(jObResponse);
			            }
				}
				
				//save bill series bill detail
				//ResultSet res=null;
				Date objDate=new Date();
				 String currentDate = (objDate.getYear() + 1900) + "-" +(objDate.getMonth()+ 1) + "-" + objDate.getDate() 
	                      + " "+ objDate.getHours() + ":" + objDate.getMinutes() + ":" +objDate.getSeconds();
				 String posDateTrans=posDate.split(" ")[0]+" "+currentDate.split(" ")[1];
                for (int i = 0; i < listBillSeriesBillDtl.size(); i++)
                {
                    clsBillSeriesBillDtl objBillSeriesBillDtl = listBillSeriesBillDtl.get(i);
                    String hdBillNo = objBillSeriesBillDtl.getStrHdBillNo();
                    double grandTotal = objBillSeriesBillDtl.getDblGrandTotal();

                    String sqlInsertBillSeriesDtl = "insert into tblbillseriesbilldtl "
                            + "(strPOSCode,strBillSeries,strHdBillNo,strDtlBillNos,dblGrandTotal,strClientCode,strDataPostFlag"
                            + ",strUserCreated,dteCreatedDate,strUserEdited,dteEditedDate,dteBillDate) "
                            + "values ('" + strPOSCode + "','" + objBillSeriesBillDtl.getStrBillSeries() + "'"
                            + ",'" + hdBillNo + "','" + funGetBillSeriesDtlBillNos(listBillSeriesBillDtl, hdBillNo) + "'"
                            + ",'" + grandTotal + "'" + ",'" + clientCode + "','N','" + userCode + "'"
                            + ",'" + currentDate + "','" + userCode + "'"
                            + ",'" + currentDate + "','" +posDateTrans+ "')";
                    
                    st.executeUpdate(sqlInsertBillSeriesDtl);
                    

                    String sql = "select * "
                            + "from tblbillcomplementrydtl a "
                            + "where a.strBillNo='" + hdBillNo + "' "
                            + "and date(a.dteBillDate)='" +posDateTrans+ "'; ";
                    ResultSet rsIsComplementary =  st.executeQuery(sql);
                    if (rsIsComplementary.next())
                    {
                        String sqlUpdate = "update tblbillseriesbilldtl set dblGrandTotal=0.00 where strHdBillNo='" + hdBillNo + "' "
                                + " and strPOSCode='" + strPOSCode + "' "
                                + " and date(dteBillDate)='" + posDateTrans + "' ";
                        st.executeUpdate(sqlUpdate);
                    }
                    rsIsComplementary.close();
                    
                }

			
			}
			else{ // bill from make bill-- bill settle list
				jArrResponse=funInsertBillData(objBillData,strPOSCode);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jArrResponse;
	}
	
private String funGetBillSeriesDtlBillNos(List<clsBillSeriesBillDtl> listBillSeriesBillDtl, String hdBillNo)
{
    StringBuilder sbDtllBillNos = new StringBuilder("");
    try{
        for (int i = 0; i < listBillSeriesBillDtl.size(); i++){
            if (listBillSeriesBillDtl.get(i).getStrHdBillNo().equals(hdBillNo)){
                continue;
            }
            else{
                if (sbDtllBillNos.length() == 0){
                    sbDtllBillNos.append(listBillSeriesBillDtl.get(i).getStrHdBillNo());
                }
                else{
                    sbDtllBillNos.append(",");
                    sbDtllBillNos.append(listBillSeriesBillDtl.get(i).getStrHdBillNo());
                }
            }
        }
    }
    catch (Exception e){
        e.printStackTrace();
    }
    return sbDtllBillNos.toString();
}





 private JSONArray funInsertBillData(JSONObject objBillData,String strPOSCode)
 {
	 	JSONArray jArrResponse=new JSONArray();
		String data="";
		String billNo="";
		String checkHomeDelivery="";
		String response = "false";
		
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		Connection cmsCon=null;
		Statement st = null;
	 try{
		 
		 	cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
			st = cmsCon.createStatement();
			Iterator callfunction=objBillData.keys();
			
			while(callfunction.hasNext())
			{
				JSONObject jsonResponse=new JSONObject();
				String key = callfunction.next().toString();
				JSONArray mJsonArray=null;
				JSONObject mJsonOb=new JSONObject();
				try {
					if(key.equals("BillPromoDiscountData")){
						mJsonOb=(JSONObject) objBillData.get(key);	
					}
					else
					{
						mJsonArray = (JSONArray) objBillData.get(key);	
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (key.equalsIgnoreCase("BillHDData") && mJsonArray !=null) 
				{
					System.out.println("BillHd= "+key);
					data=funInsertBillHdData(mJsonArray);
					billNo=data.split("#")[0];
					System.out.println("BillNo="+billNo);
					checkHomeDelivery=data.split("#")[1];
					System.out.println("checkHomeDelivery="+checkHomeDelivery);
					jsonResponse.put("BillHDData",billNo);
					/*if (funInsertBillHdData(mJsonArray) != null) 
					{
						response = "BillHd";
					} else{
						response = "false";
					}
					*/
					
				}
				else if (key.equalsIgnoreCase("BillDtlData") && mJsonArray !=null) 
				{
					System.out.println("BillDtl= "+key);
					boolean isModifierPresentInBill=false;
					if(objBillData.get("BillModifierData")!=null && objBillData.getJSONArray("BillModifierData").length()>0){
						isModifierPresentInBill=true;
					}
					if (funInsertBillDtlData(mJsonArray,isModifierPresentInBill) > 0) 
					{
						jsonResponse.put("BillDtl","BillDtl");
					} else{
						jsonResponse.put("BillDtl",false);
					}
				}
				else if (key.equalsIgnoreCase("BillModifierData") && mJsonArray !=null) 
				{
					System.out.println(key);
					if (funInsertBillModifierDtlData(mJsonArray) > 0) 
					{
						jsonResponse.put("BillModifier","BillModifier");
					} else{
						jsonResponse.put("BillModifier","false");
					}
				}
				else if (key.equalsIgnoreCase("BillSettlementData") && mJsonArray !=null) 
				{
					System.out.println(key); 
					if (funInsertBillSettlementDtlData(mJsonArray) > 0) 
					{
						jsonResponse.put("BillSettlement","BillSettlement");
					} else{
						jsonResponse.put("BillSettlement","false");
					}
				}
				else if (key.equalsIgnoreCase("BillTaxData") && mJsonArray !=null) 
				{
					System.out.println(key);
					if (funInsertBillTaxData(mJsonArray) > 0) 
					{
						jsonResponse.put("BillTax","BillTax");
					} else{
						jsonResponse.put("BillTax","false");
					}
				}
				else if (key.equalsIgnoreCase("BillPromoDiscountData") && mJsonOb !=null) 
				{
					Iterator itr=mJsonOb.keys();
					while(itr.hasNext())
					{
						String keyValue = itr.next().toString();
						JSONArray mJsonSubArray=null;
						mJsonSubArray = (JSONArray) mJsonOb.get(keyValue);	
						if(keyValue.equals("BillDiscountDtl")){
							if (funInsertBillDiscountDetail(mJsonSubArray) > 0) 
							{
								response = "true";
							} else{
								response = "false";
							}
						}else{
							if (funInsertBillPromotionDtlData(mJsonSubArray) > 0) 
							{
								response = "true";
							} else{
								response = "false";
							}
						}
						
					}
				}
				else if (key.equalsIgnoreCase("BillDiscountDtl") && mJsonArray !=null) 
				{
					if (funInsertBillDiscountDetail(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("BillComplementaryData") && mJsonArray !=null) 
				{
					if (funInsertBillComplementoryDtlData(mJsonArray) > 0) 
					{
						jsonResponse.put("BillComplementory","BillComplementory");
					} else{
						jsonResponse.put("BillComplementory","false");
					}
				}
				else if (key.equalsIgnoreCase("CRMBillPoints") && mJsonArray !=null) 
				{
					if (funInsertCRMPointsData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("VoidBillHd") && mJsonArray !=null) 
				{
					System.out.println(key);
					if (funInsertVoidBillHdData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("VoidBillDtl") && mJsonArray !=null) 
				{
					if (funInsertVoidBillDtlData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("StkInHd") && mJsonArray !=null) 
				{
					if (funInsertStkInHdData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("StkInDtl") && mJsonArray !=null) 
				{
					System.out.println(key); 
					if (funInsertStkInDtlData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("StkOutHd") && mJsonArray !=null) 
				{
					if (funInsertStkOutHdData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				} else if (key.equalsIgnoreCase("StkOutDtl") && mJsonArray !=null) 
				{
					if (funInsertStkOutDtlData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				} else if (key.equalsIgnoreCase("VoidKot") && mJsonArray !=null) 
				{
					if (funInsertVoidKotData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				} else if (key.equalsIgnoreCase("PspHd") && mJsonArray !=null) 
				{
					if (funInsertPspHdData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("PspDtl") && mJsonArray !=null) 
				{
					if (funInsertPspDtlData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				} else if (key.equalsIgnoreCase("AdvanceReceiptHd") && mJsonArray !=null) 
				{
					System.out.println(key);
					if (funInsertAdvanceReceiptHdData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("AdvanceReceiptDtl") && mJsonArray !=null) 
				{
					if (funInsertAdvanceReceiptDtlData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				} else if (key.equalsIgnoreCase("AdvBookBillHd") && mJsonArray !=null) 
				{
					System.out.println(key); 
					if (funInsertAdvBookBillHdData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("AdvBookBillDtl") && mJsonArray !=null) 
				{
					System.out.println(key);
					if (funInsertAdvBookBillDtlData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("AdvBookBillModifierDtl") && mJsonArray !=null) 
				{
					System.out.println(key); 
					if (funInsertAdvBookBillModiferDtlData(mJsonArray) > 0) 
					{
						response = "true";
					}
					else
					{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("HomeDelivery") && mJsonArray !=null) 
				{
					System.out.println(key); 
					if (funInsertHomeDeliveryData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				else if (key.equalsIgnoreCase("HomeDeliveryDtl") && mJsonArray !=null) 
				{
					System.out.println(key); 
					if (funInsertHomeDeliveryDtlData(mJsonArray) > 0) 
					{
						response = "true";
					} else{
						response = "false";
					}
				}
				if(checkHomeDelivery.equals("HomeDelivery"))
				  {
					    funBuildSMS(billNo);
				   }
				
				jArrResponse.put(jsonResponse);
			}
			cmsCon.close();
			
			st.close();
			
		 
	 }catch(Exception e){
		 e.printStackTrace();
	 }
	 return jArrResponse;
 }
 
public int funInsertBillTaxData(JSONArray mJsonArray)
{
	clsDatabaseConnection objDb=new clsDatabaseConnection();
    Connection cmsCon=null;
    Statement st=null;
    Statement st2=null;
	String taxAmt="";
    double subTotalForTax=0;
	double taxAmount=0.0;
	String insert_qry="";
	 JSONObject jObj=new JSONObject();   
	 int res=0;
	try {
		
		String tableNo="",posCode="",areaCode="",operationType="";
		cmsCon=objDb.funOpenAPOSCon("mysql","master");
        st = cmsCon.createStatement();
        st2 = cmsCon.createStatement();
        List<clsItemDtlForTax> arrListItemDtls=new ArrayList<clsItemDtlForTax>();
		//JSONArray mJsonArray=(JSONArray)objKOTTaxData.get("InsertTaxDtl");
		String sql="";
	    String insertQuery1="";
	    int paxNo=0;
	    String BillNo="";
	    String ClientCode="";
	    String billDate="";
	   
		boolean flgData=false;
		JSONObject mJsonObject = new JSONObject();
		for (int i = 0; i < mJsonArray.length(); i++) 
		{
			clsItemDtlForTax objItemDtl=new clsItemDtlForTax();
		    mJsonObject =(JSONObject) mJsonArray.get(i);
		    BillNo=mJsonObject.get("BillNo").toString();
		    ClientCode=mJsonObject.get("ClientCode").toString();
		    String itemName=mJsonObject.get("ItemName").toString();
		    String itemCode=mJsonObject.get("ItemCode").toString();
		    System.out.println(itemName);
		    double amt=Double.parseDouble(mJsonObject.get("Amount").toString());
		    billDate=mJsonObject.get("BillDate").toString();
	
            objItemDtl.setItemCode(itemCode);
            objItemDtl.setItemName(itemName);
            objItemDtl.setAmount(amt);
            objItemDtl.setDiscAmt(0);
            objItemDtl.setDiscPer(0);
            arrListItemDtls.add(objItemDtl);
            subTotalForTax+=amt;
		   
		    
		   // tableNo=mJsonObject.get("strTableNo").toString();
		    posCode=mJsonObject.get("POSCode").toString();
		   
		    sql="select strOperationType,strAreaCode from tblbillhd where strBillNo='"+BillNo+"' ";
		    ResultSet rs=st.executeQuery(sql);
		    while(rs.next())
		    {
			   operationType=rs.getString(1);
			   areaCode=rs.getString(2);
			   
			   if(operationType.equals("DirectBiller"))
			   {
				   operationType="DineIn";
			   }
			   
			   
		    }
		    
		    rs.close();
		    
		}
		
		   Date dt=new Date();            
            String date=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate();            
            clsTaxCalculation objTaxCalculation=new clsTaxCalculation();
            List <clsTaxCalculationDtls> arrListTaxDtl=objTaxCalculation.funCalculateTax(arrListItemDtls,posCode
                , date, areaCode, operationType, subTotalForTax, 0,"");
            
            insert_qry = "INSERT INTO `tblbilltaxdtl` (`strBillNo`, `strTaxCode`,"
    				+ " `dblTaxableAmount`, `dblTaxAmount`, `strClientCode`, "
    				+ "`strDataPostFlag`,`dteBillDate`) VALUES";
            
         
            sql="";
            for(int cnt=0;cnt<arrListTaxDtl.size();cnt++)
            {
            	String deleteSql="delete from tblbilltaxdtl "
    			    	+ "where strBillNo='"+BillNo+"' and strClientCode='"+ClientCode+"'";
    			st.executeUpdate(deleteSql);
    			
            	clsTaxCalculationDtls obj=arrListTaxDtl.get(cnt);
            	System.out.println("Tax Dtl= "+obj.getTaxCode()+"\t"+obj.getTaxName()+"\t"+obj.getTaxAmount());
            	taxAmt+=obj.getTaxAmount();
                sql+=",('"+BillNo+"','"+obj.getTaxCode()+"','"+obj.getTaxableAmount()+"','"+obj.getTaxAmount()+"','"
    				    +ClientCode+"','N','"+billDate+"')";				    
    			flgData=true;
    			
    		 	
            }
            
            if(flgData)
			{
				sql=sql.substring(1,sql.length());
		        insert_qry+=" "+sql;
		    	System.out.println("Query="+insert_qry);
				try{
					res=st.executeUpdate(insert_qry);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			else
			{
				res=1;
			}
       cmsCon.close();
       st.close();
       
       
       jObj.put("taxAmount", taxAmt);
        
	} catch (Exception e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return res;//Response.status(201).entity(taxAmt).build();
}
	
private String funGenarateBillSeriesNo(String strPOSCode,String key){
	JSONArray jAr=new JSONArray();
	String billSeriesBillNo="";
	clsDatabaseConnection objDb=new clsDatabaseConnection();
	Connection cmsCon=null;
	Statement st = null;
	try{
		cmsCon=objDb.funOpenAPOSCon("mysql","transaction");
		st = cmsCon.createStatement();
	
       
            //funGenerateBillNoForBillSeriesForDirectBiller(key, values);
			
			int billSeriesLastNo = 0;
            String sqlBillSeriesLastNo = "select a.intLastNo "
                    + "from tblbillseries a "
                    + "where a.strBillSeries='" + key + "' "
                    + "and (a.strPOSCode='" + strPOSCode + "' OR a.strPOSCode='All'); ";
            
            ResultSet rsBillSeriesLastNo = st.executeQuery(sqlBillSeriesLastNo);//clsGlobalVarClass.dbMysql.executeResultSet(sqlBillSeriesLastNo);
            if (rsBillSeriesLastNo.next())
            {
                billSeriesLastNo = rsBillSeriesLastNo.getInt("intLastNo");
            }
             billSeriesBillNo = key + "" + strPOSCode + "" + String.format("%05d", (billSeriesLastNo + 1));

            //update last bill series last no
            int a = st.executeUpdate("update tblbillseries "
                    + "set intLastNo='" + (billSeriesLastNo + 1) + "' "
                    + "where (strPOSCode='" + strPOSCode + "' OR strPOSCode='All') "
                    + "and strBillSeries='" + key + "' ");
            //last order no
            //int intLastOrderNo = objUtility2.funGetLastOrderNo();
            
            
        
		
	}catch(Exception e){
		e.printStackTrace();
	}
	
	
	return billSeriesBillNo;
}
	    
	@GET
	@Path("/funGenerateJasperBillFile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGenerateJasperBillFile(@QueryParam("BillNo") String billNo,@QueryParam("posCode") String posCode,
			@QueryParam("clientCode") String clientCode,@QueryParam("reprint") String reprint)
	{
		   JSONObject jsonOb=new JSONObject();
		    Response.ResponseBuilder response =    Response.ok();
	        return Response.status(200).entity(jsonOb).build();
	}
	

	@GET 
	@Path("/funLoadKOTWiseItemForFireComm")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funLoadKOTWiseItemForFireComm(@QueryParam("TableNo") String tableNo,@QueryParam("posCode") String posCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        boolean flagIsKOTPresent = false;
        JSONArray listKOTOnTable = new JSONArray();
        JSONArray listKOTItemsOnTable = new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            
            String sqlKOTDtl = "select strKOTNo "
        		    + " from tblitemrtemp "
        		    + " where (strPosCode='" + posCode + "' or strPosCode='All') "
        		    + " and strTableNo='" + tableNo + "' "
        		    + " and strPrintYN='Y' "
        		    + " and strNCKotYN='N' "
        		    + " and (dblItemQuantity-dblFiredQty)>0 "
        		    + " group by strKOTNo  "
        		    + " order by strKOTNo DESC";

        	    ResultSet rsKOTDtl =st.executeQuery(sqlKOTDtl);
        	    while (rsKOTDtl.next())
        	    {
        	    	listKOTOnTable.put(rsKOTDtl.getString(1));
        	    	flagIsKOTPresent = true;
        	    }
        	    rsKOTDtl.close();
        	    if (flagIsKOTPresent)
        	    {
        	    	String sqlTableItemDtl = "select strKOTNo,strTableNo,strWaiterNo"
        	    			+ " ,strItemName,strItemCode,dblItemQuantity,dblAmount"
        	    			+ " ,intPaxNo,strPrintYN,tdhComboItemYN,strSerialNo,strNcKotYN,dblRate"
        	    			+ " ,dblFiredQty,dblPrintQty "
        	    			+ " from tblitemrtemp where strTableNo='" + tableNo + "' "
        	    			+ " and (strPosCode='" + posCode + "' or strPosCode='All') "
        	    			+ " and strNcKotYN='N' "
        	    			+ " and (dblItemQuantity-dblFiredQty)>0 "
        	    			+ " order by strKOTNo desc ,strSerialNo";
        	    		ResultSet rsTableKOTData = st.executeQuery(sqlTableItemDtl);
        	    		while (rsTableKOTData.next())
        	    		{
        	    		    //clsMakeKotItemDtl obKOTItemDtl = new clsMakeKotItemDtl(rsTableItemDtl.getString(11), rsTableItemDtl.getString(1), rsTableItemDtl.getString(2), rsTableItemDtl.getString(3), rsTableItemDtl.getString(4), rsTableItemDtl.getString(5), rsTableItemDtl.getDouble(6), rsTableItemDtl.getDouble(7), rsTableItemDtl.getInt(8), rsTableItemDtl.getString(9), rsTableItemDtl.getString(10), false, "", "", "", "N", rsTableItemDtl.getDouble(13));
        	    		   
        	    		    //obKOTItemDtl.setDblFireQty(rsTableItemDtl.getDouble(14));
        	    		    //obKOTItemDtl.setDblPrintQty(rsTableItemDtl.getDouble(15));

        	    			JSONObject obj=new JSONObject();
        	    			obj.put("kotNo",rsTableKOTData.getString(1));
        	            	obj.put("TableNo",rsTableKOTData.getString(2));
        	            	obj.put("strWaiterNo",rsTableKOTData.getString(3));
        	            	obj.put("strItemName",rsTableKOTData.getString(4));
        	            	obj.put("strItemCode",rsTableKOTData.getString(5));
        	            	obj.put("dblItemQuantity",rsTableKOTData.getString(6));
        	            	obj.put("dblAmount",rsTableKOTData.getString(7));
        	            	obj.put("intPaxNo",rsTableKOTData.getString(8));
        	            	obj.put("strPrintYN",rsTableKOTData.getString(9));
        	            	obj.put("tdhComboItemYN",rsTableKOTData.getString(10));
        	            	obj.put("strSerialNo",rsTableKOTData.getString(11));
        	            	obj.put("strNcKotYN",rsTableKOTData.getString(12));
        	            	obj.put("dblRate",rsTableKOTData.getString(13));
        	            	obj.put("dblFiredQty",rsTableKOTData.getString(14));
        	            	obj.put("dblPrintQty",rsTableKOTData.getString(15));
        	            	
        	            	
        	            	if (rsTableKOTData.getDouble(7) >= 0)
        	    		    {
        	            		listKOTItemsOnTable.put(obj);
        	    		    }
        	    		}
        	    	
        	    }
          
            jObj.put("KOTOnTable", listKOTOnTable);
            jObj.put("KOTItemsOnTable", listKOTItemsOnTable);
            
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return jObj; 
        }
    }


	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funAddItemsToFire")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funAddItemsToFire(JSONObject objFireKOTDtl)
	{
		JSONObject jsonRes=new JSONObject();
		JSONArray jArr=new JSONArray();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
		try{
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
            String tableNo= objFireKOTDtl.getString("tableNo");
            String strPOSCode=objFireKOTDtl.getString("strPOSCode");
            String strPOSName=objFireKOTDtl.getString("strPOSName");
            String deviceName=objFireKOTDtl.getString("deviceName");
            String macAddress=objFireKOTDtl.getString("macAddress");
            String strAreaWisePricing=objFireKOTDtl.getString("strAreaWisePricing");
            JSONArray arrFireKOTItems=(JSONArray)objFireKOTDtl.get("fireItems");
            JSONObject obitemdtl=new JSONObject();
            Set<String> setOfKOTs = new HashSet<String>();
            st.executeUpdate("update tblitemrtemp set dblPrintQty=0 where strTableNo='" + tableNo + "' ");
            
            for(int i=0;i<arrFireKOTItems.length();i++){
            	obitemdtl=(JSONObject)arrFireKOTItems.get(i);
            	
            	String sqlUpdate = "update tblitemrtemp  "
        			    + "set dblFiredQty=dblFiredQty+" + obitemdtl.getString("fireQty") + " "
        			    + ",dblPrintQty='" + obitemdtl.getString("fireQty") + "' "
        			    + "where strTableNo='" + tableNo + "' "
        			    + "and strKOTNo='" + obitemdtl.getString("kotNo") + "' "
        			    + "and strItemCode='" + obitemdtl.getString("itemCode") + "' ";
            	
            	st.executeUpdate(sqlUpdate);
            	setOfKOTs.add(obitemdtl.getString("kotNo"));

            }
            
            Iterator<String> it = setOfKOTs.iterator();
    		while (it.hasNext())
    		{
    		    String KOTNO = it.next();
     		    String  printingResult=funGenerateTextFileForKOT(strPOSCode, strPOSName, tableNo, KOTNO, "", "", "Dina", "Y",deviceName,macAddress,"Y",strAreaWisePricing);
     		    jArr.put(KOTNO+"#"+printingResult);
    		}
    		
    		jsonRes.put("res", jArr);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return jsonRes;
	}
	
/*	
	@SuppressWarnings("finally")
    @GET 
	@Path("/funCheckKOTTableStatus")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funCheckTableStatus(@QueryParam("tableNo") String tableNo,@QueryParam("posCode") String posCode
			,@QueryParam("waiterNo") String waiterNo,@QueryParam("clientCode") String clientCode)
	{
		String tableStatus="not selected";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
		try{
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="select a.strStatus from tblkottablestatus a "
            		+ "where a.strTableNo='"+tableNo+"' and a.strWaiterNo='"+waiterNo+"' and a.strPOSCode='"+posCode+"' and a.strClientCode='"+clientCode+"';";
            ResultSet rs =st.executeQuery(sql);
            if(rs.next()){
            	tableStatus=rs.getString(1);
            }
    	    rs.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		 return Response.status(200).entity(tableStatus).build();
	}
	
	@GET
	@Path("/funUpdateKOTTableStatus")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funUpdateKOTTableStatus(@QueryParam("tableNo") String tableNo,@QueryParam("waiterNo") String waiterNo
			,@QueryParam("posCode") String posCode,@QueryParam("clientCode") String clientCode)
	{
		String res="";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
		try{
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="INSERT INTO `tblkottablestatus` (`strTableNo`, `strWaiterNo`, `strStatus`, `strPOSCode`, `strClientCode`)"
            		+ " VALUES ('"+tableNo+"', '"+waiterNo+"', 'selected', '"+posCode+"', '"+clientCode+"');";
           
            st.executeUpdate(sql);
            res="Success";
        }
		catch(Exception e){
			res="fail";
			e.printStackTrace();
		}
		return Response.status(200).entity(res).build();
	}
*/
	
	@SuppressWarnings("finally")
	@GET
	@Path("/funGetInstalledPrinterList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funGetInstalledPrinterList()
	{
		JSONArray arrPrinterList=new JSONArray(); 
		try{
			PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
		    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
		    for (int i = 0; i < printService.length; i++)
		    {
		    	arrPrinterList.put(printService[i].getName());
		    }
		}catch(Exception e){
			arrPrinterList.put("printer not found");
		}
		return arrPrinterList;
	}
	
	@POST
	@Path("/funUpdateitemVoicetext")
	@Produces(MediaType.APPLICATION_JSON)
	public String funUpdateitemVoicetext(JSONArray jsArray,@QueryParam("itemCode") String itemCode)
	{
		JSONArray arrPrinterList=new JSONArray(); 
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        String strRes="N";
		try{
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            StringBuilder sbSql=new StringBuilder();  
            ResultSet rs =null;
           
            String sql="select strItemVoiceCaptureText from tblitemmaster  where strItemCode='"+itemCode+"'";
            rs =st.executeQuery(sql);
            String strItemVoiceCaptureText="";
            if(rs.next()){
            	strItemVoiceCaptureText=rs.getString(1);
            	
            }
           
            for(int i=0;i<jsArray.length();i++){
            	sbSql.setLength(0);
            	String itemName=jsArray.getString(i);
            	if(itemName.contains("'")){
            		itemName=itemName.replace("'", "");
            	}
            	sbSql.append("select strItemVoiceCaptureText from tblitemmaster where strItemVoiceCaptureText like '%"+itemName+"%' ");
            	rs =st.executeQuery(sbSql.toString());
            	if(rs.next()){
            		
            	}else{
            		if(strItemVoiceCaptureText.length()>0){
                		strItemVoiceCaptureText=strItemVoiceCaptureText+","+itemName;
                	}else{
                		strItemVoiceCaptureText=itemName;
                	}
            	}
            	
            }
           sql="update tblitemmaster a set a.strItemVoiceCaptureText='"+strItemVoiceCaptureText+"' where a.strItemCode='"+itemCode+"'"; 
            st.executeUpdate(sql);
            strRes="Y";
		}
		catch(Exception e){
			e.printStackTrace();
			strRes="N";
		}
		return strRes;
	}
	
	@GET
	@Path("/funGetPOSWiseItemList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetPOSWiseItemList(@QueryParam("strClientCode") String strClientCode,@QueryParam("fromDate") String fromDate
    	, @QueryParam("toDate") String toDate )
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
        	String sql="";
			sql = " select a.strItemCode,a.strItemName,a.strItemVoiceCaptureText from tblitemmaster a " 
					+" where a.strClientCode='"+strClientCode+"' group by a.strItemName;";
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	obj.put("ItemCode",rsMasterData.getString(1));
            	obj.put("ItemName",rsMasterData.getString(2));
            	obj.put("strVoiceTextSaved",rsMasterData.getString(3));
            	arrObj.put(obj);
            }
            rsMasterData.close();
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;
	}
	
	
	   
    @SuppressWarnings("rawtypes")
	@POST
	@Path("/funSaveAndDownloadKOT")
    @Produces("application/pdf")
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response funSaveAndDownloadKOTForInternalPrinter(JSONObject objKOTData)
	{
    	javax.ws.rs.core.Response.ResponseBuilder responseBuilder=null;
    	clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        Statement st2=null,st3=null;
		String kotNO="K000001",printingResult="";
		int exe=0;
		JSONObject jObj=new JSONObject();
		try {
			
			String tableNo="",posCode="", posName="",customerCode="",deviceName="",macAddress="";
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        st2 = cmsCon.createStatement();
	        st3 = cmsCon.createStatement();
	        String sql="";
	       
			JSONArray mJsonArray=(JSONArray)objKOTData.get("KOTDtl");
			String lockTable=objKOTData.getString("lockTable");
			boolean isKOTSave=false;
			if(lockTable.equalsIgnoreCase("Y")){
				if(mJsonArray.length()>0){
					JSONObject mJsonObject =(JSONObject) mJsonArray.get(0);
					tableNo=mJsonObject.get("strTableNo").toString();
					String waiterNo=mJsonObject.get("strWaiterNo").toString();
					sql="select distinct b.strStatus,a.strWaiterNo from tblitemrtemp a,tbltablemaster b " 
							+" where a.strTableNo=b.strTableNo and a.strTableNo='"+tableNo+"';";
					ResultSet rs=st.executeQuery(sql);
					String tableStatus="",oldWaiter="";
					if(rs.next()){
						if(rs.getString(1).equalsIgnoreCase("Occupied")){
							if(rs.getString(2).equals(waiterNo)){
								isKOTSave=true;
							}
						}
					}else{
						isKOTSave=true;
					}
					
					
				}
				if(!isKOTSave){
					/*jObj.put("kotNO", " Not Punched.. Table Already Busy");
					jObj.put("printingResult", printingResult);
					return jObj;*/
				}
			}
			
			
		    String insertQuery1="";
		    int paxNo=0;
			boolean flgData=false;
			JSONObject mJsonObject = new JSONObject();
			kotNO=funGenerateKOTNo();
			String insertQuery = "insert into tblitemrtemp(strSerialNo,strTableNo,strCardNo,dblRedeemAmt,strPosCode,strItemCode"
				+ ",strHomeDelivery,strCustomerCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo"
                + ",strKOTNo,intPaxNo,strPrintYN,strUserCreated,strUserEdited,dteDateCreated"
                + ",dteDateEdited,strTakeAwayYesNo,strNCKotYN,strCustomerName,strCounterCode"
                + ",dblRate,strCardType,strDeviceMACAdd,strDeviceId) values ";
			
			
			 insertQuery1 = "insert into tblnonchargablekot "
      	        	+ " (strTableNo,strItemCode,dblQuantity,dblRate,strKOTNo,strEligibleForVoid,strClientCode,"
      	        	+ " strDataPostFlag,strReasonCode,strRemark,dteNCKOTDate,strUserCreated,strUserEdited,strPOSCode) "
      	        	+ " values " ;
			 
			 boolean flgNCKOT=false;
			
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
			    mJsonObject =(JSONObject) mJsonArray.get(i);
			    String itemName=mJsonObject.get("strItemName").toString();
			    System.out.println(itemName);
			    double qty=Double.parseDouble(mJsonObject.get("dblItemQuantity").toString());
			    double rate=Double.parseDouble(mJsonObject.get("dblRate").toString());
			    //double amt=qty*rate;
			    double amt=Double.parseDouble(mJsonObject.get("dblAmount").toString());
			    tableNo=mJsonObject.get("strTableNo").toString();
			    posCode=mJsonObject.get("strPOSCode").toString();
			    posName=mJsonObject.get("strPOSName").toString();
			    deviceName=mJsonObject.get("deviceName").toString();
			    macAddress=mJsonObject.get("macAddress").toString();
			    //customerCode=mJsonObject.get("strCustomerCode").toString();
			    
			    if(i==0)
			    {
			    	Statement st1 = cmsCon.createStatement();
			    	sql="select strCustomerCode from tblitemrtemp "
		    			+ "where strTableNo='"+tableNo+"' and strPOSCode='"+posCode+"'";
			    	ResultSet rsCust=st1.executeQuery(sql);
			    	if(rsCust.next())
			    	{
			    		customerCode=rsCust.getString(1);
			    	}
			    	else
			    	{
			    		customerCode=mJsonObject.get("strCustomerCode").toString();
			    	}
			    	rsCust.close();
			    	st1.close();
			    }
			    Date objDate = new Date();
			    String currentDate = mJsonObject.get("POSDate").toString()
     		                      + " "+ objDate.getHours() + ":" + objDate.getMinutes() + ":" +objDate.getSeconds();
			    
			    insertQuery +="('"+mJsonObject.get("strSerialNo").toString()+"','"+mJsonObject.get("strTableNo").toString()+"'"
			    	+ ",'" + mJsonObject.get("strCardNo").toString() + "','" + mJsonObject.get("dblRedeemAmt").toString() + "'"
	    			+ ",'" + mJsonObject.get("strPOSCode").toString() + "','" + mJsonObject.get("strItemCode").toString() + "'"
	                + ",'" + mJsonObject.get("strHomeDelivery").toString() + "','"+customerCode+"'"
                   	+ ",'" + mJsonObject.get("strItemName").toString() + "','" + mJsonObject.get("dblItemQuantity").toString() + "'"
                   	+ ",'" + amt + "','" + mJsonObject.get("strWaiterNo").toString() + "'"
                 	+ ",'" + kotNO + "','" + mJsonObject.get("intPaxNo").toString() + "'"
            		+ ",'" + mJsonObject.get("strPrintYN").toString() + "','" + mJsonObject.get("strUserCreated").toString() + "'"
            		+ ",'" + mJsonObject.get("strUserEdited").toString() + "','" + mJsonObject.get("dteDateCreated").toString()+ "'"
         			+ ",'" + mJsonObject.get("dteDateEdited").toString() + "','"+mJsonObject.get("strTakeAwayYesNo").toString()+"'"
      				+ ",'"+ mJsonObject.get("strNCKotYN").toString() + "','"+mJsonObject.get("strCustomerName").toString()+"'"
      				+ ",'"+mJsonObject.get("strCounterCode").toString()+"','"+mJsonObject.get("dblRate").toString()+"'"
      				+ ",'"+mJsonObject.get("strCardType").toString()+"','"+mJsonObject.get("macAddress").toString()+"','"+mJsonObject.get("deviceName").toString()+"'), ";
      			
			    
			    paxNo=Integer.parseInt(mJsonObject.get("intPaxNo").toString());
			    String NCKOTYN= mJsonObject.get("strNCKotYN").toString();
			    System.out.println("Nckotyn="+NCKOTYN);
			    if(NCKOTYN.equalsIgnoreCase("Y"))
	            {
	                 insertQuery1+= "('" +mJsonObject.get("strTableNo").toString()+ "','" +  mJsonObject.get("strItemCode").toString()+ "','"+mJsonObject.get("dblItemQuantity").toString()+ "'"
	         	        	+ ", '"+mJsonObject.get("dblRate").toString()+"','"+kotNO+"','Y','"+mJsonObject.get("strClientCode").toString()+"','N','"+mJsonObject.get("strReasonCode").toString()+"'"
	         	        	+ ", '"+mJsonObject.get("strRemark").toString()+"','"+currentDate+"','"+mJsonObject.get("strUserCreated").toString()+"','"+mJsonObject.get("strUserEdited").toString()+"'"
	         	        	+ ", '"+posCode +"'),";
	                 //System.out.println("query="+insertQuery1);
	                 flgNCKOT=true;
	            }
			    
			}
			System.out.println("query="+insertQuery1);
            StringBuilder sb = new StringBuilder(insertQuery);            
            int index = sb.lastIndexOf(",");
            insertQuery = sb.delete(index, sb.length()).toString();
            System.out.println(insertQuery);
            exe=st.executeUpdate(insertQuery);
            System.out.println("Exe= "+exe);
            sql="select strFireCommunication,strAreaWisePricing from tblsetup where strClientCode='"+mJsonObject.get("strClientCode").toString()+"' ";
        	String strFireComm="N",strAreaWisePricing="N";
        	ResultSet rsFire=st.executeQuery(sql);
	    	if(rsFire.next())
	    	{
	    		strFireComm=rsFire.getString(1);
	    		strAreaWisePricing=rsFire.getString(2);
	    	}
	    	rsFire.close();
            if(flgNCKOT)
            {
	            StringBuilder sb1 = new StringBuilder(insertQuery1);
	            int index1=sb1.lastIndexOf(",");            
	            insertQuery1 = sb1.delete(index1, sb1.length()).toString();
				System.out.println("query="+insertQuery1);            
	            st2.executeUpdate(insertQuery1);
	            sql = "update tbltablemaster set strStatus='Normal',intPaxNo='0' where strTableNo='"+tableNo+"'";
	            st3.executeUpdate(sql);
	            printingResult=funGenerateTextFileForKOT(posCode, posName, tableNo, kotNO, "", "", "Dina", "Y",deviceName,macAddress,strFireComm,strAreaWisePricing);
            }
            else{
            	if(exe>0)
                {
                	sql="update tbltablemaster set strStatus='Occupied',intPaxNo="+paxNo+" where strTableNo='"+tableNo+"'";
                	System.out.println(sql);
                	st.executeUpdate(sql);
                	
                	
			    	if(strFireComm.equals("N")){
			    		printingResult=funGenerateTextFileForKOT(posCode, posName, tableNo, kotNO, "", "", "Dina", "Y",deviceName,macAddress,strFireComm,strAreaWisePricing);	
			    	}
                }
            }
            
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(exe==0){
			kotNO="ERROR";			
		}

		try{
			jObj.put("kotNO", kotNO);
			jObj.put("printingResult", printingResult);
			
			String filePath = System.getProperty("user.dir");
			File file = new File(filePath + "/Temp/Temp_KOT.txt");
		    FileInputStream fileInputStream = new FileInputStream(file);
		    responseBuilder = javax.ws.rs.core.Response.ok((Object) fileInputStream);
		    responseBuilder.type("application/pdf");
		    //responseBuilder.header("Content-Disposition",  "attachment; filename=restfile.pdf");// direct download
		    responseBuilder.header("Content-Disposition",  " filename=Temp_KOT.txt");//open in browser
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	   return responseBuilder.build();
		//return jObj;//Response.status(201).entity(kotNO+"#"+printingResult).build();		
	}
    
    @SuppressWarnings("rawtypes")
	@GET
	@Path("/funDownloadBill")
    @Produces("application/pdf")
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response funDownloadBill(@QueryParam("billNo") String bill )
	{
    	javax.ws.rs.core.Response.ResponseBuilder responseBuilder=null;

		try{
			
			String filePath = System.getProperty("user.dir");
			File file = new File(filePath + "/Temp/TempBill.txt");
		    FileInputStream fileInputStream = new FileInputStream(file);
		    responseBuilder = javax.ws.rs.core.Response.ok((Object) fileInputStream);
		    responseBuilder.type("application/pdf");
		    //responseBuilder.header("Content-Disposition",  "attachment; filename=restfile.pdf");// direct download
		    responseBuilder.header("Content-Disposition",  " filename=TempBill.txt");//open in browser
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	   return responseBuilder.build();
	}
    
    
    @GET
	@Path("/funGetDateWiseSettleBillList")
	@Produces(MediaType.APPLICATION_JSON)
    public JSONArray funGetDateWiseSettleBillList(@QueryParam("BillDate") String strBillDate,@QueryParam("ClientCode") String strClientCode,@QueryParam("POSCode") String strPOSCode )
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
        	String sqlLive="";
        	String sqlQFile="";
        	
        	sqlLive = " SELECT a.strBillNo, a.dblGrandTotal, a.strSettelmentMode"
					+ " FROM tblbillhd a, tblbilldtl b, tblbillsettlementdtl c,tblsettelmenthd d"
					+ " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and a.strSettelmentMode=d.strSettelmentType"
					+ " and a.dtBillDate = '"+strBillDate+"' and a.strClientCode='"+strClientCode+"' and (strPOSCode='"+strPOSCode+"' or strPOSCode='All')"
					+ " group by a.strBillNo";
        	
        	ResultSet rsSettleBillData=st.executeQuery(sqlLive);
            while(rsSettleBillData.next())
            {
            	JSONObject obj=new JSONObject();
            	obj.put("BillNo",rsSettleBillData.getString(1));
            	obj.put("SettlementAmt",rsSettleBillData.getString(2));
            	obj.put("SettlementMode",rsSettleBillData.getString(3));
            	arrObj.put(obj);
            }
            rsSettleBillData.close();
        	
        	sqlQFile = " SELECT a.strBillNo, a.dblGrandTotal, a.strSettelmentMode"
					+ " FROM tblqbillhd a, tblqbilldtl b, tblqbillsettlementdtl c,tblsettelmenthd d"
					+ " where a.strBillNo=b.strBillNo and a.strBillNo=c.strBillNo and a.strSettelmentMode=d.strSettelmentType"
					+ " and a.dtBillDate = '"+strBillDate+"' and a.strClientCode='"+strClientCode+"' and (strPOSCode='"+strPOSCode+"' or strPOSCode='All')"
					+ " group by a.strBillNo";
            rsSettleBillData=st.executeQuery(sqlQFile);
            while(rsSettleBillData.next())
            {
            	JSONObject obj=new JSONObject();
            	obj.put("BillNo",rsSettleBillData.getString(1));
            	obj.put("SettlementAmt",rsSettleBillData.getString(2));
            	obj.put("SettlementMode",rsSettleBillData.getString(3));
            	arrObj.put(obj);
            }
            rsSettleBillData.close();
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;
	}
    

	@SuppressWarnings("rawtypes")
    @POST
	@Path("/funChangeSettleBillList")
	@Produces(MediaType.APPLICATION_JSON)
    public Response funChangeSettleBillList(@QueryParam("Type") String type,@QueryParam("BillDate") String strBillDate,
    		@QueryParam("ClientCode") String strClientCode,JSONObject jObjData,@QueryParam("POSCode") String strPOSCode )
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        String curDate="";
        int result;
        JSONObject jsonSettlementOptionBean=null;
        JSONArray arrObj=new JSONArray();
        String ret="false";
    	JSONObject mJsonObject=null;
    	ResultSet rsSettleBillData;
    	String strBillDt="";
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
        	String sqlLive="";
        	String sqlQFile="";
        	String billDate[]=strBillDate.split("-");
        	strBillDt=billDate[2] + "-" + billDate[1] + "-" + billDate[0];
        	/*
        	Iterator itrBillData=jObjData.keys();
        	JSONArray mJsonArray =new JSONArray();
        	while(itrBillData.hasNext())
			{
        		String key = itrBillData.next().toString();
        		jsonSettlementOptionBean = (JSONObject) jObjData.get(key);
        		
				
			}
        	*/
        	String settlementName="Cash";
        	JSONArray jArrBillSettData=new JSONArray();
        	if(jObjData.has("BillSettlementData")){
        		jArrBillSettData=(JSONArray)jObjData.get("BillSettlementData");
        		
        		funInsertBillSettlementDtlData(jArrBillSettData);
        	}
        	for (int i = 0; i < jArrBillSettData.length(); i++) 
			{
				mJsonObject =(JSONObject) jArrBillSettData.get(i);
			}
        	
        	if(jArrBillSettData.length()>1){
        		settlementName="MultiSettle";
        	}else{

        		JSONObject jsobSettname=(JSONObject)jArrBillSettData.get(0);
        		settlementName=jsobSettname.getString("SettlementMode");
        	}
        	
        	if(type.equals("Live"))
        	{
        		sqlLive = "update tblbillhd set strSettelmentMode='" + settlementName + "' "
                    + ",strTransactionType=CONCAT(strTransactionType,',','Change Settlement') "
                    + "where strBillNo='" + mJsonObject.get("BillNo") + "' "
                    + "and strPOSCode='" + strPOSCode + "' "
                    + "and date(dteBillDate)='" + strBillDt + "' "; 
        		int i=st.executeUpdate(sqlLive);
        		if(i>0)
        			ret="true";
                
        	}
        	else
        	{
        		sqlQFile = "update tblqbillhd set strSettelmentMode='" +settlementName + "' "
                        + ",strTransactionType=CONCAT(strTransactionType,',','Change Settlement') "
                        + "where strBillNo='" + mJsonObject.get("BillNo") + "' "
                        + "and strPOSCode='" + strPOSCode + "' "
                        + "and date(dteBillDate)='" + strBillDt + "' "; 
        		int i=st.executeUpdate(sqlQFile);
        		if(i>0)
        			ret="true";
        	}
        	
        	
        	
            
            /*// Insert BillSettlementDtl
            if(type.equals("Live"))
        	{
            	String sqlDelete = "delete from tblbillsettlementdtl "
                        + " where strBillNo='" +  jsonSettlementOptionBean.get("BillNo") + "' "
                        + " and date(dteBillDate)='" + strBillDate + "' ";
            	ResultSet rsSqlDelete=st.executeQuery(sqlDelete);
            	
            	String sqlInsertBillSettlementDtl = "insert into tblbillsettlementdtl "
                        + "(strBillNo,strSettlementCode,dblSettlementAmt,dblPaidAmt,strExpiryDate"
                        + ",strCardName,strRemark,strClientCode,strCustomerCode,dblActualAmt"
                        + ",dblRefundAmt,strGiftVoucherCode,strDataPostFlag,dteBillDate) "
                        + "values ('" + jsonSettlementOptionBean.get("strBillNo") + "'"
		                + ",'" + jsonSettlementOptionBean.get("strSettlementCode") + "'," + jsonSettlementOptionBean.get("dblSettlementAmt") + " "
		                + "," + jsonSettlementOptionBean.get("dblPaidAmt") + ",'" + jsonSettlementOptionBean.get("strExpiryDate") + "' "
		                + ",'" + jsonSettlementOptionBean.get("strCardName") + "','" + jsonSettlementOptionBean.get("strRemark") + "'"
		                + ",'" + jsonSettlementOptionBean.get("strClientCode") + "','" + jsonSettlementOptionBean.get("strCustomerCode") + "'"
		                + "," + jsonSettlementOptionBean.get("dblActualAmt") + "," + jsonSettlementOptionBean.get("dblRefundAmt") + ""
		                + ",'" + jsonSettlementOptionBean.get("strGiftVoucherCode") + "','" + jsonSettlementOptionBean.get("strDataPostFlag") + "','" + strBillDate + "'), ";
            	ResultSet rsSqlInsert=st.executeQuery(sqlInsertBillSettlementDtl);
        	}
        	else
        	{
        		String sqlDelete = "delete from tblqbillsettlementdtl "
                        + " where strBillNo='" +  jsonSettlementOptionBean.get("BillNo") + "' "
                        + " and date(dteBillDate)='" + strBillDate + "' ";
            	ResultSet rsSqlDelete=st.executeQuery(sqlDelete);
            	
            	String sqlInsertBillSettlementDtl = "insert into tblqbillsettlementdtl "
                        + "(strBillNo,strSettlementCode,dblSettlementAmt,dblPaidAmt,strExpiryDate"
                        + ",strCardName,strRemark,strClientCode,strCustomerCode,dblActualAmt"
                        + ",dblRefundAmt,strGiftVoucherCode,strDataPostFlag,dteBillDate) "
                        + "values ('" + jsonSettlementOptionBean.get("strBillNo") + "'"
		                + ",'" + jsonSettlementOptionBean.get("strSettlementCode") + "'," + jsonSettlementOptionBean.get("dblSettlementAmt") + " "
		                + "," + jsonSettlementOptionBean.get("dblPaidAmt") + ",'" + jsonSettlementOptionBean.get("strExpiryDate") + "' "
		                + ",'" + jsonSettlementOptionBean.get("strCardName") + "','" + jsonSettlementOptionBean.get("strRemark") + "'"
		                + ",'" + jsonSettlementOptionBean.get("strClientCode") + "','" + jsonSettlementOptionBean.get("strCustomerCode") + "'"
		                + "," + jsonSettlementOptionBean.get("dblActualAmt") + "," + jsonSettlementOptionBean.get("dblRefundAmt") + ""
		                + ",'" + jsonSettlementOptionBean.get("strGiftVoucherCode") + "','" + jsonSettlementOptionBean.get("strDataPostFlag") + "','" + strBillDate + "'), ";
            	ResultSet rsSqlInsert=st.executeQuery(sqlInsertBillSettlementDtl);
        	}*/
            
            st.close();
            cmsCon.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return Response.status(201).entity(ret).build();
	}

	@GET 
	@Path("/funGetDashboardList")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetDashboardList(@QueryParam("POSCode") String POSCode,@QueryParam("clientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONArray arrObj=new JSONArray();
        JSONObject dashboard = new JSONObject();
        NumberFormat formatter = new DecimalFormat("#0");
        NumberFormat format = new DecimalFormat("#0.00");
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="", POSDate="";
            
            
            sql = "select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd "
	                + " from tbldayendprocess "
	                + " where strPOSCode='"+ POSCode + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')";
	        //System.out.println(sql);
	        ResultSet rsDay=st.executeQuery(sql);
	        if(rsDay.next())
	        {
	            POSDate=rsDay.getString(1);
	        }
            
	        /* Table / PAX */
	        funFillTablePerPax(POSCode, POSDate, clientCode, st, arrObj);
            
            /* Open Orders / Value */
            funFillOpenOrders(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
            /* Sales Achieved */
            funFillSalesAchieved(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
            /* Average Per Cover */
            funFillAveragePerCover(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
	    	/* Hourly Average Sales */
            funFillHourlyAvgSales(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
            /* Month To Date Sales */
            funFillMonthToDateSales(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
            /* Previous Month Sales */
            funFillPreviousMonthSales(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
            /* Sales Growth */
            funFillSalesGrowth(POSCode, POSDate, clientCode, st, arrObj, format);
            
            /*Best Day */
            funFillBestDay(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
            /* Table Reservation */
            funFillTableReservation(POSCode, POSDate, clientCode, st, arrObj, formatter);
            
            dashboard.put("dashboard", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return dashboard;
        }
    }
	
	
	private void funFillTablePerPax(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj) throws Exception
	{
		String sql="SELECT COUNT(a.strTableNo), IFNULL(SUM(a.intPaxNo),0) FROM tbltablemaster a WHERE  a.strStatus='Occupied' "
				+ "AND a.strClientCode='"+clientCode+"'; ";
        ResultSet rsTableInfo = st.executeQuery(sql);
        JSONObject obj=new JSONObject();
        obj.put("TableHeader","Tables / PAX");
        obj.put("TableSubHeader","Number of Tables/Total PAX");
        if (rsTableInfo.next()) 
        {
        	obj.put("Table",rsTableInfo.getString(1));
        	obj.put("Pax",rsTableInfo.getString(2));
        }
        arrObj.put(obj);
        rsTableInfo.close();
	}
	
	
	private void funFillOpenOrders(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		String sql="SELECT SUM(a.dblAmount) FROM tblitemrtemp a, tbltablemaster b WHERE a.strTableNo=b.strTableNo "
				+ "AND b.strClientCode='"+clientCode+"'; ";
	    ResultSet rsOpenOrder = st.executeQuery(sql);
	    JSONObject objOpen=new JSONObject();
	    objOpen.put("TableHeader","Open Order Values");
	    objOpen.put("TableSubHeader","Number of Open Order Values");
	    if (rsOpenOrder.next()) 
	    {
	    	objOpen.put("OpenOrders",formatter.format(rsOpenOrder.getDouble(1)));
	    }
	    arrObj.put(objOpen);
	    rsOpenOrder.close();
	}
	
	
	private void funFillSalesAchieved(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		String sql="SELECT IFNULL(SUM(b.dblamount),0) FROM tblbillhd a,tblbilldtl b,tblitemmaster c WHERE a.strBillNo=b.strBillNo "
				+ "AND b.strItemcode=c.strItemCode AND a.dtBillDate='"+POSDate+"' AND a.strClientCode='"+clientCode+"'; ";
        ResultSet rsSales = st.executeQuery(sql);
        JSONObject objSales=new JSONObject();
        objSales.put("TableHeader","Sales Achieved");
        objSales.put("TableSubHeader","Total Amount");
        while (rsSales.next()) 
        {
        	objSales.put("SalesAchieved",formatter.format(rsSales.getDouble(1)));
        }
        arrObj.put(objSales);
        rsSales.close();
	}
	
	
	private void funFillAveragePerCover(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		StringBuilder sqlNonComplimentaryBuilder=new StringBuilder();
        StringBuilder sqlComplimentaryBuilder=new StringBuilder();
        Map<String, clsAPCReport> mapAPCReport = new HashMap<>(); 
        sqlNonComplimentaryBuilder.append("select a.strPOSCode,d.strPosName,date(a.dteBillDate) as Date, sum(a.dblGrandTotal) as DiningAmt,"
        		+ "sum(intBillSeriesPaxNo),'0' "
        		+ "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
        		+ "where Date(a.dteBillDate) between '"+POSDate+"' and '"+POSDate+"' and a.strPOSCode=d.strPosCode "
        		+ "and a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode and a.strOperationType='DineIn' "
        		+ "and date(a.dteBillDate)=date(b.dteBillDate) and c.strSettelmentType<>'Complementary' "
        		+ "and a.strSettelmentMode!='MultiSettle' and a.strClientCode='"+clientCode+"' "
        		+ "group by a.strPOSCode,d.strPosName,a.dteBillDate");
        
        sqlComplimentaryBuilder.append("select a.strPOSCode,d.strPosName,date(a.dteBillDate)  as Date,sum(a.dblGrandTotal) as DiningAmt,sum(intBillSeriesPaxNo),'0' "
        		+ "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblposmaster d "
        		+ "where Date(a.dteBillDate) between '"+POSDate+"' and '"+POSDate+"' and a.strPOSCode=d.strPosCode "
        		+ "and a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode and a.strOperationType='DineIn' "
        		+ "and date(a.dteBillDate)=date(b.dteBillDate) and c.strSettelmentType='Complementary' "
        		+ "and a.strClientCode='"+clientCode+"' "
        		+ "group by a.strPOSCode,d.strPosName,a.dteBillDate ");
        
        ResultSet rsNoComp = st.executeQuery(sqlNonComplimentaryBuilder.toString());
        String key="";
        while (rsNoComp.next()) 
        {
        	if (mapAPCReport.containsKey(key))
    	    {
        		clsAPCReport objAPCReport = mapAPCReport.get(key);
        		objAPCReport.setDblDiningAmt(objAPCReport.getDblDiningAmt() + rsNoComp.getDouble(4));//dining amt
        		objAPCReport.setDblPAXNo(objAPCReport.getDblPAXNo() + rsNoComp.getDouble(5));//PAX
        		mapAPCReport.put(key, objAPCReport);
    	    }
    	    else
    	    {
        		clsAPCReport objAPCReport = new clsAPCReport();
        		objAPCReport.setStrPOSCode(rsNoComp.getString(1));//posCode
        		objAPCReport.setStrPOSName(rsNoComp.getString(2));//posName
        		objAPCReport.setDteBillDate(rsNoComp.getString(3));//date
        		objAPCReport.setDblDiningAmt(rsNoComp.getDouble(4));//dining amt
        		objAPCReport.setDblPAXNo(rsNoComp.getDouble(5));//PAX

        		mapAPCReport.put(key, objAPCReport);
    	    }
        }
        rsNoComp.close();
        
      //for multi settle 
        sqlNonComplimentaryBuilder.setLength(0);
    	sqlNonComplimentaryBuilder.append("select a.strPOSCode,d.strPosName,date(a.dteBillDate) as Date,sum(a.dblGrandTotal) as DiningAmt"
    		+ ",sum(intBillSeriesPaxNo),'0' "
    		+ "from tblbillhd a,tblposmaster d "
    		+ "where Date(a.dteBillDate) between '" + POSDate + "' and '" + POSDate + "' and a.strPOSCode=d.strPosCode "
    		+ "and a.strOperationType='DineIn' and a.strSettelmentMode='MultiSettle' and a.strClientCode='"+clientCode+"' "
    		+ "group by a.strPOSCode,d.strPosName,a.dteBillDate ");
    	rsNoComp = st.executeQuery(sqlNonComplimentaryBuilder.toString());
    	while (rsNoComp.next()) 
        {
    		if (mapAPCReport.containsKey(key))
    	    {
	    		clsAPCReport objAPCReport = mapAPCReport.get(key);
	    		objAPCReport.setDblDiningAmt(objAPCReport.getDblDiningAmt() + rsNoComp.getDouble(4));//dining amt
	    		objAPCReport.setDblPAXNo(objAPCReport.getDblPAXNo() + rsNoComp.getDouble(5));//PAX
	    		mapAPCReport.put(key, objAPCReport);
	    	}
    	    else
    	    {
	    		clsAPCReport objAPCReport = new clsAPCReport();

	    		objAPCReport.setStrPOSCode(rsNoComp.getString(1));//posCode
	    		objAPCReport.setStrPOSName(rsNoComp.getString(2));//posName
	    		objAPCReport.setDteBillDate(rsNoComp.getString(3));//date
	    		objAPCReport.setDblDiningAmt(rsNoComp.getDouble(4));//dining amt
	    		objAPCReport.setDblPAXNo(rsNoComp.getDouble(5));//PAX

	    		mapAPCReport.put(key, objAPCReport);
    	    }
        }
    	rsNoComp.close();
    	
    	//truncate
    	st.executeQuery("truncate tblatvreport");
    	//insert non complimentary sales
    	
    	for (clsAPCReport objAPCReport : mapAPCReport.values())
    	{
    	    //insert non complimentary sales
    	    st.execute("Insert into tblatvreport "
    		    + "(strPosCode,strPosName,dteDate,dblDiningAmt,dblDiningNoBill,dblHDNoBill) "
    		    + "values('" + objAPCReport.getStrPOSCode() + "','" + objAPCReport.getStrPOSName() + "','" + objAPCReport.getDteBillDate() + "','" + objAPCReport.getDblDiningAmt() + "','" + objAPCReport.getDblPAXNo() + "','0') ");
    	}
    	st.execute("update tblatvreport set dblDiningAvg=  dblDiningAmt/dblDiningNoBill");
    	
    	ResultSet rsComplimentarySales = st.executeQuery(sqlComplimentaryBuilder.toString());
    	if(rsComplimentarySales.next())
    	{
    		st.execute("update tblatvreport set dblHDNoBill='" + rsComplimentarySales.getString(5) + "' "
    		    + " where strPosCode='" + rsComplimentarySales.getString(1) + "' and dteDate='" + rsComplimentarySales.getString(3) + "'  ");
    	}
    	rsComplimentarySales.close();
    	
        String sql="SELECT ifnull(a.dblDiningAvg,0) FROM tblatvreport a ";
        ResultSet rsAvgPerCover = st.executeQuery(sql);
        JSONObject objAvgPerCover=new JSONObject();
        objAvgPerCover.put("TableHeader","Average Per Cover");
        objAvgPerCover.put("TableSubHeader","Per Order");
        if (rsAvgPerCover.next()) 
        {
        	objAvgPerCover.put("AvgPerCover",formatter.format(rsAvgPerCover.getDouble(1)));
        	arrObj.put(objAvgPerCover);
        }
        else
        {
        	objAvgPerCover.put("AvgPerCover",0);
        	arrObj.put(objAvgPerCover);
        }
        rsAvgPerCover.close();
	}
	
	
	private void funFillHourlyAvgSales(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		String sql="SELECT SUM(a.dblGrandTotal) FROM tblbillhd a WHERE a.dteBillDate BETWEEN "
				+ " DATE_SUB(SYSDATE(), INTERVAL 1 HOUR) AND SYSDATE() AND a.strClientCode='"+clientCode+"' ORDER BY a.dteBillDate LIMIT 1; ";
	    ResultSet rsHourlyAvg = st.executeQuery(sql);
	    JSONObject objHourlyAvg=new JSONObject();
	    objHourlyAvg.put("TableHeader","Hourly Average Sales");
	    objHourlyAvg.put("TableSubHeader","Total Amount");
	    double amount=0;
	    while (rsHourlyAvg.next()) 
	    {
	    	objHourlyAvg.put("HourlyAverageSales",formatter.format(rsHourlyAvg.getDouble(1)));
	    }
	    rsHourlyAvg.close();
	    arrObj.put(objHourlyAvg);
	}
	
	
	private void funFillMonthToDateSales(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		String firstDateOfMonth= POSDate.split(" ")[0].split("-")[0]+"-"+POSDate.split(" ")[0].split("-")[1]+"-01";
	    String sql="SELECT SUM(grandtotal) grandTotal "
	    		+ "FROM ( SELECT SUM(c.dblSettlementAmt) grandtotal FROM tblbillhd a,tblbillsettlementdtl c "
	    		+ "WHERE a.strBillNo=c.strBillNo AND DATE(a.dtBillDate) BETWEEN '"+firstDateOfMonth+"' AND DATE_SUB('"+POSDate+"',INTERVAL 1 DAY) "
	    		+ "AND a.strClientCode='"+clientCode+"' "
	    		+ "UNION SELECT SUM(d.dblSettlementAmt) grandtotal FROM tblqbillhd b,tblqbillsettlementdtl d "
	    		+ "WHERE b.strBillNo=d.strBillNo AND DATE(b.dtBillDate) BETWEEN '"+firstDateOfMonth+"' AND DATE_SUB('"+POSDate+"',INTERVAL 1 DAY) "
	    		+ "AND b.strClientCode='"+clientCode+"') e; ";
	    ResultSet rsFirstDate = st.executeQuery(sql);
	    JSONObject objFirstDate=new JSONObject();
	    objFirstDate.put("TableHeader","Month to Date Sales");
	    objFirstDate.put("TableSubHeader","Current Month Sale Amount");
	    while (rsFirstDate.next()) 
	    {
	    	objFirstDate.put("MonthToDateSales",formatter.format(rsFirstDate.getDouble(1)));
	    }
	    rsFirstDate.close();
	    arrObj.put(objFirstDate);
	}
	
	
	private void funFillPreviousMonthSales(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		int month=Integer.parseInt(POSDate.split(" ")[0].split("-")[1]);
		month=month-1;
	    
	    String sql="SELECT MONTH(a.dteBillDate),IFNULL(SUM(a.dblGrandTotal),0) FROM tblqbillhd a where month(a.dteBillDate)='"+month+"' "
	    		+ "AND a.strClientCode='"+clientCode+"'; ";
	    ResultSet rsPrevMonth = st.executeQuery(sql);
	    JSONObject objPrevMonth=new JSONObject();
	    objPrevMonth.put("TableHeader","Last Month Sales");
	    objPrevMonth.put("TableSubHeader","Last Month Sale Amount");
	    while (rsPrevMonth.next()) 
	    {
	    	objPrevMonth.put("PreviousMonthSales",formatter.format(rsPrevMonth.getDouble(2)));
	    }
	    rsPrevMonth.close();
	    arrObj.put(objPrevMonth);
	}
	
	
	private void funFillSalesGrowth(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat format) throws Exception
	{
		String sql="SELECT SUM(a.dblGrandTotal) FROM tblbillhd a WHERE DATE(dteBillDate) = '"+POSDate+"' "
    		+ " and a.strClientCode='"+clientCode+"'; ";
	    ResultSet rsCurrent=st.executeQuery(sql);
	    JSONObject objPrevCurrent=new JSONObject();
	    objPrevCurrent.put("TableHeader","Sales Growth");
	    objPrevCurrent.put("TableSubHeader","This Week vs Last Week");
	    double current=0.0, previous=0.0;
	    while(rsCurrent.next())
	    {
	    	current=rsCurrent.getDouble(1);
	    }
	    rsCurrent.close();
	    
	    sql="SELECT SUM(a.dblGrandTotal) from tblqbillhd a where DATE(dteBillDate) = "
    		+ "DATE_SUB('"+POSDate+"',INTERVAL 7 DAY) and a.strClientCode='"+clientCode+"'; ";
	    ResultSet rsPrevious=st.executeQuery(sql);
	    while(rsPrevious.next())
	    {
	    	previous=rsPrevious.getDouble(1);
	    }
	    rsPrevious.close();
	    double percent=0;
	    if(previous<1)
	    	percent=100;
	    else
	    	percent=(current-previous)/previous * 100;
	    
	    objPrevCurrent.put("SalesGrowth", format.format(percent)+"%");
	    arrObj.put(objPrevCurrent);
	}
		
	
	private void funFillBestDay(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		String sql="SELECT sum(a.dblGrandTotal),DAYNAME(date(a.dteBillDate)) FROM tblqbillhd a where date(dteBillDate) "
        	+ "between date(DATE_SUB('"+POSDate+"', INTERVAL 7 DAY)) and '"+POSDate+"' "
        	+ " and a.strClientCode='"+clientCode+"' "
        	+ "group by date(dteBillDate) order by sum(a.dblGrandTotal) desc limit 1; ";
        ResultSet rsBestDay = st.executeQuery(sql);
        JSONObject objBestDay=new JSONObject();
        objBestDay.put("TableHeader","Best Day");
        objBestDay.put("TableSubHeader","This Week (Revenue)");
        while (rsBestDay.next()) 
        {
        	objBestDay.put("BestDay",rsBestDay.getString(2));
        	objBestDay.put("SaleValue",rsBestDay.getDouble(1));
        }
        rsBestDay.close();
        arrObj.put(objBestDay);
	}
	
	
	private void funFillTableReservation(String POSCode, String POSDate, String clientCode, Statement st, JSONArray arrObj, NumberFormat formatter) throws Exception
	{
		String sql="SELECT COUNT(a.strResCode) FROM tblreservation a, tbltablemaster b,tblcustomermaster c "
    		+ "WHERE a.strTableNo=b.strTableNo AND a.strCustomerCode=c.strCustomerCode "
    		+ "AND DATE(a.dteResDate)='"+POSDate+"' AND a.strClientCode='"+clientCode+"'; ";
        ResultSet rsTableReserve = st.executeQuery(sql);
        JSONObject objTableReserve=new JSONObject();
        objTableReserve.put("TableHeader","Table Reservations");
        objTableReserve.put("TableSubHeader","Today");
        while (rsTableReserve.next()) 
        {
        	objTableReserve.put("TableReservations",rsTableReserve.getString(1));
        }
        rsTableReserve.close();
        arrObj.put(objTableReserve);
	}
	
	
	@GET 
	@Path("/funValidateClientCode")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funValidateClientCode(@QueryParam("clientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject response = new JSONObject();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	        st = cmsCon.createStatement();
	        String sql="";
	            
	        sql="SELECT a.strClientCode FROM tblsetup a WHERE a.strClientCode='"+clientCode+"'; ";
	        ResultSet rsClientCode=st.executeQuery(sql);
	        if(rsClientCode.next())
	        {
	          	response.put("response",true);
	        }
	        else
	        {
	           	response.put("response",false);
	        }
	        rsClientCode.close();
	        st.close();
            cmsCon.close();
        }
        catch(Exception e)
	    {
	       	e.printStackTrace();
	    }
		return response;
	}
	
	@GET 
	@Path("/funTopTenSellingItems")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funTopTenSellingItems(@QueryParam("POSCode") String POSCode,@QueryParam("clientCode") String clientCode,@QueryParam("dashboardMic") boolean dashboardMic)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONArray response = new JSONArray();
        Map map = new HashMap();
        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="",POSDate="";
	            
	            sql = "select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd "
		                + " from tbldayendprocess "
		                + " where strPOSCode='"+ POSCode + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')";
		        //System.out.println(sql);
		        ResultSet rsDay=st.executeQuery(sql);
		        if(rsDay.next())
		        {
		            POSDate=rsDay.getString(1);
		        }
		        rsDay.close();
		        double subTotal=0;
		        sql="select sum(temp.SubTotal) from "
		        	+ "(select sum(a.dblAmount)-sum(a.dblDiscountAmt) as SubTotal from tblbilldtl a,tblbillhd b "
		        	+ "where a.strBillNo=b.strBillNo and date(b.dteBillDate)='2019-04-11' AND b.strClientCode='304.001' "
		        	+ "GROUP BY b.strPOSCode "
		        	+ "union all "
		        	+ "select sum(a.dblAmount)-sum(a.dblDiscountAmt) as SubTotal from tblqbilldtl a,tblqbillhd b "
		        	+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate) BETWEEN DATE(DATE_SUB('2019-04-11', INTERVAL 21 DAY)) AND '2019-04-11' "
		        	+ "GROUP BY b.strPOSCode "
		        	+ ") temp ";
	            ResultSet rsSubTotal=st.executeQuery(sql);
	            while(rsSubTotal.next())
	            {
	            	subTotal=rsSubTotal.getDouble(1);
	            }
	            rsSubTotal.close();
	            //Amount
	            if(dashboardMic)
	            {
	            	sql=" SELECT temp.ItemName, sum(temp.Amount), (sum(temp.Amount)/"+subTotal+")*100 FROM "
		            		+ "( SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount FROM tblbilldtl a, tblbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate)='"+POSDate+"' AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode "
		            		+ "UNION ALL "
		            		+ "SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount FROM tblqbilldtl a, tblqbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate) BETWEEN DATE(DATE_SUB('"+POSDate+"', INTERVAL 21 DAY)) AND '"+POSDate+"' "
		            		+ "AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode) "
		            		+ "temp group by ItemCode ORDER BY amount DESC LIMIT 10; ";
	            }
	            else
	            {
	            	sql=" SELECT temp.ItemName, sum(temp.Amount), (sum(temp.Amount)/"+subTotal+")*100,SUM(temp.Qty) FROM "
		            		+ "( SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount,SUM(a.dblQuantity) as Qty FROM tblbilldtl a, tblbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate)='"+POSDate+"' AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode "
		            		+ "UNION ALL "
		            		+ "SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount,SUM(a.dblQuantity) as Qty FROM tblqbilldtl a, tblqbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate) BETWEEN DATE(DATE_SUB('"+POSDate+"', INTERVAL 21 DAY)) AND '"+POSDate+"' "
		            		+ "AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode) "
		            		+ "temp group by ItemCode ORDER BY Qty DESC LIMIT 10; ";
	            }
		        ResultSet rsSellingItems=st.executeQuery(sql);
	            while(rsSellingItems.next())
	            {
	            	if(dashboardMic)
		            {
	            		JSONObject jObj=new JSONObject();
	            		jObj.put("name", rsSellingItems.getString(1));
		            	jObj.put("amount", rsSellingItems.getString(2));
		            	jObj.put("percent", rsSellingItems.getString(3));
		            	response.put(jObj);
		            }
	            	else
	            	{
	            		JSONObject jObj=new JSONObject();
	            		jObj.put("name", rsSellingItems.getString(1));
		            	jObj.put("amount", rsSellingItems.getString(4)); // Qty
		            	jObj.put("percent", rsSellingItems.getString(3));
		            	response.put(jObj);
	            	}
	            }
	            rsSellingItems.close();
	            st.close();
	            cmsCon.close();
	            
        	}
        	catch(Exception e)
	        {
	        	e.printStackTrace();	
	        }
		return response;
	}
	
	@GET 
	@Path("/funBottomTenSellingItems")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funBottomTenSellingItems(@QueryParam("POSCode") String POSCode,@QueryParam("clientCode") String clientCode,@QueryParam("dashboardMic") boolean dashboardMic)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONArray response = new JSONArray();
        Map map = new HashMap();
        try {
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
	            st = cmsCon.createStatement();
	            String sql="",POSDate="";
	            
	            sql = "select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd "
		                + " from tbldayendprocess "
		                + " where strPOSCode='"+ POSCode + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')";
		        //System.out.println(sql);
		        ResultSet rsDay=st.executeQuery(sql);
		        if(rsDay.next())
		        {
		            POSDate=rsDay.getString(1);
		        }
		        rsDay.close();
		        double subTotal=0;
		        sql="select sum(temp.SubTotal) from "
		        	+ "(select sum(a.dblAmount)-sum(a.dblDiscountAmt) as SubTotal from tblbilldtl a,tblbillhd b "
		        	+ "where a.strBillNo=b.strBillNo and date(b.dteBillDate)='2019-04-11' AND b.strClientCode='304.001' "
		        	+ "GROUP BY b.strPOSCode "
		        	+ "union all "
		        	+ "select sum(a.dblAmount)-sum(a.dblDiscountAmt) as SubTotal from tblqbilldtl a,tblqbillhd b "
		        	+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate) BETWEEN DATE(DATE_SUB('2019-04-11', INTERVAL 21 DAY)) AND '2019-04-11' "
		        	+ "GROUP BY b.strPOSCode "
		        	+ ") temp ";
	            ResultSet rsSubTotal=st.executeQuery(sql);
	            while(rsSubTotal.next())
	            {
	            	subTotal=rsSubTotal.getDouble(1);
	            }
	            rsSubTotal.close();
		        // Amount
	            if(dashboardMic)
	            {
	            	sql=" SELECT temp.ItemName, sum(temp.Amount), (sum(temp.Amount)/"+subTotal+")*100 FROM "
		            		+ "( SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount FROM tblbilldtl a, tblbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate)='"+POSDate+"' AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode "
		            		+ "UNION ALL "
		            		+ "SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount FROM tblqbilldtl a, tblqbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate) BETWEEN DATE(DATE_SUB('"+POSDate+"', INTERVAL 21 DAY)) AND '"+POSDate+"' "
		            		+ "AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode) "
		            		+ "temp group by ItemCode ORDER BY amount LIMIT 10; ";
	            }
	            else
	            {
	            	sql=" SELECT temp.ItemName, sum(temp.Amount), (sum(temp.Amount)/"+subTotal+")*100,SUM(temp.Qty) AS Quantity FROM "
		            		+ "( SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount,SUM(a.dblQuantity) as Qty FROM tblbilldtl a, tblbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate)='"+POSDate+"' AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode "
		            		+ "UNION ALL "
		            		+ "SELECT a.strItemCode as ItemCode,a.strItemName AS ItemName, SUM(a.dblAmount) AS Amount,SUM(a.dblQuantity) as Qty FROM tblqbilldtl a, tblqbillhd b "
		            		+ "WHERE a.strBillNo=b.strBillNo AND DATE(b.dteBillDate) BETWEEN DATE(DATE_SUB('"+POSDate+"', INTERVAL 21 DAY)) AND '"+POSDate+"' "
		            		+ "AND b.strClientCode='"+clientCode+"' and a.dblAmount>0 "
		            		+ "GROUP BY a.strItemCode) "
		            		+ "temp group by ItemCode ORDER BY Quantity LIMIT 10; ";
	            }
	            ResultSet rsSellingItems=st.executeQuery(sql);
	            while(rsSellingItems.next())
	            {
	            	if(dashboardMic)
		            {
	            		JSONObject jObj=new JSONObject();
	            		jObj.put("name", rsSellingItems.getString(1));
		            	jObj.put("amount", rsSellingItems.getString(2)); 
		            	jObj.put("percent", rsSellingItems.getString(3));
		            	response.put(jObj);
		            }
	            	else
	            	{
	            		JSONObject jObj=new JSONObject();
	            		jObj.put("name", rsSellingItems.getString(1));
		            	jObj.put("amount", rsSellingItems.getString(4)); // Qty
		            	jObj.put("percent", rsSellingItems.getString(3));
		            	response.put(jObj);
	            	}
	            }
	            rsSellingItems.close();
	            st.close();
	            cmsCon.close();
        	}
        	catch(Exception e)
	        {
	        	e.printStackTrace();	
	        }
		return response;
	}
	
	
	@GET 
	@Path("/funSalesSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray funSalesSummary(@QueryParam("POSDate") String POSDate,@QueryParam("clientCode") String clientCode,@QueryParam("SelectDate") String date)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection con=null;
        Statement st=null;
        JSONArray response = new JSONArray();
        NumberFormat formatter = new DecimalFormat("#0");
        NumberFormat format = new DecimalFormat("#0.0");
        
        try {
        		con=objDb.funOpenAPOSCon("mysql","master");
	            st = con.createStatement();
	            String sql="";
	            /*String posDate[]= date.split("-");
	            String newDate=posDate[2]+"-"+posDate[1]+"-"+posDate[0];*/
	            
		        if(POSDate.equals(date))
		        {
		        	sql="SELECT IFNULL((temp4.homedel/billno)*100,0) AS homdel,IFNULL((temp4.dinein/billno)*100,0) AS dinin,IFNULL((temp4.takeaway/billno)*100,0) AS takaway"
		        		+ " FROM (SELECT temp.billno,temp1.homedel,temp2.dinein,temp3.takeaway FROM ( SELECT COUNT(a.strBillNo) AS billno "
		        		+ " FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery' OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,"
		        		+ " (SELECT COUNT(a.strBillNo) AS homedel FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1,"
		        		+ " (SELECT COUNT(a.strBillNo) AS dinein FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2,"
		        		+ " (SELECT COUNT(a.strBillNo) AS takeaway FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3) temp4 ";
		        	ResultSet rsOrders=st.executeQuery(sql);
			        while(rsOrders.next())
			        {
			        	JSONObject obj =  new JSONObject();
			        	obj.put("OrderHD", format.format(Double.parseDouble(rsOrders.getString(1))));
			        	obj.put("OrderDI", format.format(Double.parseDouble(rsOrders.getString(2))));
			        	obj.put("OrderTA", format.format(Double.parseDouble(rsOrders.getString(3))));
			        	response.put(obj);
			        }
			        rsOrders.close();
			        sql="SELECT IFNULL((temp4.homedel/totalsub)*100,0) AS homdel,IFNULL((temp4.dinein/totalsub)*100,0) AS dinin,IFNULL((temp4.takeaway/totalsub)*100,0) AS takaway"
			        		+ " FROM (SELECT temp.totalsub,temp1.homedel,temp2.dinein,temp3.takeaway FROM ( SELECT COUNT(a.dblSubTotal) AS totalsub "
			        		+ " FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery' OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,"
			        		+ " (SELECT COUNT(a.dblSubTotal) AS homedel FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1,"
			        		+ " (SELECT COUNT(a.dblSubTotal) AS dinein FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2,"
			        		+ " (SELECT COUNT(a.dblSubTotal) AS takeaway FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3) temp4 ";
			        ResultSet rsSubTotal=st.executeQuery(sql);
			        while(rsSubTotal.next())
			        {
			        	JSONObject obj =  new JSONObject();
			        	obj.put("SubHD", format.format(Double.parseDouble(rsSubTotal.getString(1))));
			        	obj.put("SubDI", format.format(Double.parseDouble(rsSubTotal.getString(2))));
			        	obj.put("SubTA", format.format(Double.parseDouble(rsSubTotal.getString(3))));
			        	response.put(obj);
			        }
			        rsSubTotal.close();
			        sql="SELECT IFNULL((temp4.homedel/totaltax)*100,0) AS homdel,IFNULL((temp4.dinein/totaltax)*100,0) AS dinin,IFNULL((temp4.takeaway/totaltax)*100,0) AS takaway"
			        		+ " FROM (SELECT temp.totaltax,temp1.homedel,temp2.dinein,temp3.takeaway FROM ( SELECT COUNT(a.dblTaxAmt) AS totaltax "
			        		+ " FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery' OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,"
			        		+ " (SELECT COUNT(a.dblTaxAmt) AS homedel FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1,"
			        		+ " (SELECT COUNT(a.dblTaxAmt) AS dinein FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2,"
			        		+ " (SELECT COUNT(a.dblTaxAmt) AS takeaway FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3) temp4 ";
			        ResultSet rsTax=st.executeQuery(sql);
			        while(rsTax.next())
			        {
			        	JSONObject obj =  new JSONObject();
			        	obj.put("TaxHD", format.format(Double.parseDouble(rsTax.getString(1))));
			        	obj.put("TaxDI", format.format(Double.parseDouble(rsTax.getString(2))));
			        	obj.put("TaxTA", format.format(Double.parseDouble(rsTax.getString(3))));
			        	response.put(obj);
			        }
			        rsTax.close();
			        sql="SELECT IFNULL((temp1.homedel/temp.total)*100,0),IFNULL((temp2.dinein/temp.total)*100,0),IFNULL((temp3.takeaway/temp.total)*100,0) FROM (SELECT SUM(a.dblGrandTotal) AS total "
			        		+ "	FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery' "
			        		+ "OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,(SELECT SUM(a.dblGrandTotal) AS homedel "
			        		+ "FROM tblbillhd a, tblbillsettlementdtl b	WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1, "
			        		+ "(SELECT SUM(a.dblGrandTotal) AS dinein FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2, "
			        		+ " (SELECT SUM(a.dblGrandTotal) AS takeaway FROM tblbillhd a, tblbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+POSDate+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3 ";
			        ResultSet rsTotal=st.executeQuery(sql);
			        while(rsTotal.next())
			        {
			        	JSONObject obj =  new JSONObject();
			        	obj.put("TotalHD", format.format(Double.parseDouble(rsTotal.getString(1))));
			        	obj.put("TotalDI", format.format(Double.parseDouble(rsTotal.getString(2))));
			        	obj.put("TotalTA", format.format(Double.parseDouble(rsTotal.getString(3))));
			        	response.put(obj);
			        }
			        rsTotal.close();
			        
			        sql=" SELECT IFNULL((temp1.food/temp.total)*100,0),IFNULL((temp2.beverages/temp.total)*100,0),IFNULL((temp3.alcohol/temp.total)*100,0) "
				        	+ "FROM(SELECT SUM(a.dblGrandTotal) AS total FROM tblbillhd a, tblbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+POSDate+"' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp, "
				        	+ "(SELECT SUM(a.dblGrandTotal) AS food FROM tblbillhd a, tblbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+POSDate+"' AND e.strGroupName='FOOD' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp1, "
				        	+ "(SELECT SUM(a.dblGrandTotal) AS beverages FROM tblbillhd a, tblbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+POSDate+"' AND e.strGroupName='BEVERAGES' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp2, "
				        	+ "(SELECT SUM(a.dblGrandTotal) AS alcohol FROM tblbillhd a, tblbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+POSDate+"' AND e.strGroupName='ALCOHOL' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp3 ";
				        ResultSet rsGroup=st.executeQuery(sql);
				        while(rsGroup.next())
				        {
				        	JSONObject obj =  new JSONObject();
				        	obj.put("Food", formatter.format(Double.parseDouble(rsGroup.getString(1))));
				        	obj.put("Beverages", formatter.format(Double.parseDouble(rsGroup.getString(2))));
				        	obj.put("Alcohol", formatter.format(Double.parseDouble(rsGroup.getString(3))));
				        	response.put(obj);
				        }
				        rsGroup.close();
		        }
		        else
		        {
		        	sql="SELECT IFNULL((temp4.homedel/billno)*100,0) AS homdel,IFNULL((temp4.dinein/billno)*100,0) AS dinin,IFNULL((temp4.takeaway/billno)*100,0) AS takaway"
			        		+ " FROM (SELECT temp.billno,temp1.homedel,temp2.dinein,temp3.takeaway FROM ( SELECT COUNT(a.strBillNo) AS billno "
			        		+ " FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery' OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,"
			        		+ " (SELECT COUNT(a.strBillNo) AS homedel FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1,"
			        		+ " (SELECT COUNT(a.strBillNo) AS dinein FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2,"
			        		+ " (SELECT COUNT(a.strBillNo) AS takeaway FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3) temp4 ";
			        	ResultSet rsOrders=st.executeQuery(sql);
				        if(rsOrders.next())
				        {
				        	JSONObject obj =  new JSONObject();
				        	obj.put("OrderHD", format.format(Double.parseDouble(rsOrders.getString(1))));
				        	obj.put("OrderDI", format.format(Double.parseDouble(rsOrders.getString(2))));
				        	obj.put("OrderTA", format.format(Double.parseDouble(rsOrders.getString(3))));
				        	response.put(obj);
				        }
				        rsOrders.close();
				        sql="SELECT IFNULL((temp4.homedel/totalsub)*100,0) AS homdel,IFNULL((temp4.dinein/totalsub)*100,0) AS dinin,IFNULL((temp4.takeaway/totalsub)*100,0) AS takaway"
				        		+ " FROM (SELECT temp.totalsub,temp1.homedel,temp2.dinein,temp3.takeaway FROM ( SELECT COUNT(a.dblSubTotal) AS totalsub "
				        		+ " FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery' OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,"
				        		+ " (SELECT COUNT(a.dblSubTotal) AS homedel FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1,"
				        		+ " (SELECT COUNT(a.dblSubTotal) AS dinein FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2,"
				        		+ " (SELECT COUNT(a.dblSubTotal) AS takeaway FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3) temp4 ";
				        ResultSet rsSubTotal=st.executeQuery(sql);
				        if(rsSubTotal.next())
				        {
				        	JSONObject obj =  new JSONObject();
				        	obj.put("SubHD", format.format(Double.parseDouble(rsSubTotal.getString(1))));
				        	obj.put("SubDI", format.format(Double.parseDouble(rsSubTotal.getString(2))));
				        	obj.put("SubTA", format.format(Double.parseDouble(rsSubTotal.getString(3))));
				        	response.put(obj);
				        }
				        rsSubTotal.close();
				        sql="SELECT IFNULL((temp4.homedel/totaltax)*100,0) AS homdel,IFNULL((temp4.dinein/totaltax)*100,0) AS dinin,IFNULL((temp4.takeaway/totaltax)*100,0) AS takaway"
				        		+ " FROM (SELECT temp.totaltax,temp1.homedel,temp2.dinein,temp3.takeaway FROM ( SELECT COUNT(a.dblTaxAmt) AS totaltax "
				        		+ " FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery' OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,"
				        		+ " (SELECT COUNT(a.dblTaxAmt) AS homedel FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1,"
				        		+ " (SELECT COUNT(a.dblTaxAmt) AS dinein FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2,"
				        		+ " (SELECT COUNT(a.dblTaxAmt) AS takeaway FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3) temp4 ";
				        ResultSet rsTax=st.executeQuery(sql);
				        if(rsTax.next())
				        {
				        	JSONObject obj =  new JSONObject();
				        	obj.put("TaxHD", format.format(Double.parseDouble(rsTax.getString(1))));
				        	obj.put("TaxDI", format.format(Double.parseDouble(rsTax.getString(2))));
				        	obj.put("TaxTA", format.format(Double.parseDouble(rsTax.getString(3))));
				        	response.put(obj);
				        }
				        rsTax.close();
				        sql="SELECT IFNULL((temp1.homedel/temp.total)*100,0),IFNULL((temp2.dinein/temp.total)*100,0),IFNULL((temp3.takeaway/temp.total)*100,0) FROM (SELECT SUM(a.dblGrandTotal) AS total "
				        		+ "	FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery' "
				        		+ "OR a.strOperationType='DineIn' OR a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp,(SELECT IFNULL(SUM(a.dblGrandTotal),0) AS homedel "
				        		+ "FROM tblqbillhd a, tblqbillsettlementdtl b	WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='HomeDelivery') AND a.strClientCode='"+clientCode+"') temp1, "
				        		+ "(SELECT SUM(a.dblGrandTotal) AS dinein FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='DineIn') AND a.strClientCode='"+clientCode+"') temp2, "
				        		+ " (SELECT IFNULL(SUM(a.dblGrandTotal),0) AS takeaway FROM tblqbillhd a, tblqbillsettlementdtl b WHERE a.strBillNo=b.strBillNo AND a.dtBillDate='"+date+"' AND (a.strOperationType='TakeAway') AND a.strClientCode='"+clientCode+"') temp3 ";
				        ResultSet rsTotal=st.executeQuery(sql);
				        while(rsTotal.next())
				        {
				        	JSONObject obj =  new JSONObject();
				        	obj.put("TotalHD", format.format(Double.parseDouble(rsTotal.getString(1))));
				        	obj.put("TotalDI", format.format(Double.parseDouble(rsTotal.getString(2))));
				        	obj.put("TotalTA", format.format(Double.parseDouble(rsTotal.getString(3))));
				        	response.put(obj);
				        }
				        rsTotal.close();
				        
				        sql=" SELECT IFNULL((temp1.food/temp.total)*100,0),IFNULL((temp2.beverages/temp.total)*100,0),IFNULL((temp3.alcohol/temp.total)*100,0) "
				        	+ "FROM(SELECT SUM(a.dblGrandTotal) AS total FROM tblqbillhd a, tblqbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+date+"' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp, "
				        	+ "(SELECT SUM(a.dblGrandTotal) AS food FROM tblqbillhd a, tblqbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+date+"' AND e.strGroupName='FOOD' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp1, "
				        	+ "(SELECT SUM(a.dblGrandTotal) AS beverages FROM tblqbillhd a, tblqbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+date+"' AND e.strGroupName='BEVERAGES' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp2, "
				        	+ "(SELECT SUM(a.dblGrandTotal) AS alcohol FROM tblqbillhd a, tblqbilldtl b, tblitemmaster c, tblsubgrouphd d, tblgrouphd e "
				        	+ "WHERE a.strBillNo=b.strBillNo AND b.strItemCode=c.strItemCode AND c.strSubGroupCode=d.strSubGroupCode AND d.strGroupCode=e.strGroupCode AND a.dtBillDate='"+date+"' AND e.strGroupName='ALCOHOL' AND a.dblGrandTotal>0.0 AND a.strClientCode='"+clientCode+"') temp3 ";
				        ResultSet rsGroup=st.executeQuery(sql);
				        while(rsGroup.next())
				        {
				        	JSONObject obj =  new JSONObject();
				        	obj.put("Food", formatter.format(Double.parseDouble(rsGroup.getString(1))));
				        	obj.put("Beverages", formatter.format(Double.parseDouble(rsGroup.getString(2))));
				        	obj.put("Alcohol", formatter.format(Double.parseDouble(rsGroup.getString(3))));
				        	response.put(obj);
				        }
				        rsGroup.close();
		        }
		        st.close();
		        con.close();
        	}
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        return response;
	}
	
	@GET 
	@Path("/funGetAlerts")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetAlerts(@QueryParam("clientCode") String clientCode,@QueryParam("Date") String date)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection con=null;
        Statement st=null;
        JSONArray response = new JSONArray();
        JSONArray responseComp = new JSONArray();
        JSONObject resp = new JSONObject();
        
        try 
        {
        	con=objDb.funOpenAPOSCon("mysql","master");
	        st = con.createStatement();
	        String sql="";
	        
	        sql="SELECT a.strBillNo,c.strReasonName,a.dblActualAmount,a.strTransType, IFNULL(b.dblDiscountAmt,0),d.strPosName, DATE_FORMAT(a.dteModifyVoidBill,'%d %M %Y %H:%i:%s'),a.strUserEdited "
	        		+ "FROM tblvoidbillhd a LEFT OUTER JOIN tblbillhd b ON a.strBillNo=b.strBillNo LEFT OUTER "
	        		+ "JOIN tblreasonmaster c ON a.strReasonCode=c.strReasonCode LEFT OUTER JOIN tblposmaster d "
	        		+ "ON a.strPosCode=d.strPosCode WHERE DATE(a.dteBillDate)='"+date+"' AND a.strClientCode='"+clientCode+"' ";
	        ResultSet rsAlerts=st.executeQuery(sql);
	        while(rsAlerts.next())
	        {
	        	JSONObject obj =  new JSONObject();
	        	obj.put("BillNo",rsAlerts.getString(1));
	        	obj.put("Reason",rsAlerts.getString(2));
	        	obj.put("Amount",rsAlerts.getString(3));
	        	obj.put("TransType",rsAlerts.getString(4));
	        	obj.put("Discount",rsAlerts.getString(5));
	        	obj.put("POSName",rsAlerts.getString(6));
	        	obj.put("ModifyDate",rsAlerts.getString(7));
	        	obj.put("User",rsAlerts.getString(8));
	        	response.put(obj);
	        }
	        rsAlerts.close();
	        resp.put("Alerts", response);
	        sql="SELECT a.strBillNo,a.strSettelmentMode,c.strPosName,DATE_FORMAT(a.dteDateEdited,'%d %M %Y %H:%i:%s'),SUM(d.dblAmount),a.strUserEdited FROM tblbillhd a,tblposmaster c, tblbillcomplementrydtl d "
	        		+ "WHERE a.strBillNo=d.strBillNo AND a.strPOSCode=c.strPosCode AND a.dblGrandTotal=0.0 AND a.strSettelmentMode='NC' AND a.strClientCode='"+clientCode+"' GROUP BY a.strBillNo ";
	        ResultSet rsComp=st.executeQuery(sql);
	        while(rsComp.next())
	        {
	        	JSONObject objComp =  new JSONObject();
	        	objComp.put("BillNo",rsComp.getString(1));
	        	objComp.put("TransType",rsComp.getString(2));
	        	objComp.put("POSName",rsComp.getString(3));
	        	objComp.put("ModifyDate",rsComp.getString(4));
	        	objComp.put("Amount",rsComp.getString(5));
	        	objComp.put("User",rsComp.getString(6));
	        	responseComp.put(objComp);
	        }
	        rsComp.close();
	        st.close();
	        con.close();
	        resp.put("Comp", responseComp);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return resp;
	}	
		
}
