package com.webbooks.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.apos.model.clsGroupMasterModel;
import com.cmsws.controller.clsCMSIntegration;
import com.webbooks.bean.clsProfitLossReportBean;
import com.webservice.controller.clsDatabaseConnection;







import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Response.ResponseBuilder;

import net.sf.jasperreports.engine.JasperExportManager;

import org.springframework.stereotype.Controller;

@Path("/WebBooksIntegration")
@Controller
public class clsWebbooksIntegration 
{

	 @Autowired
	 private ServletContext servletContext;

	@SuppressWarnings("finally")
	@GET 
	@Path("/funInvokeWebBooksWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetBillInfo()
	{
		String response = "false";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
		try
		{
			webbookCon=objDb.funOpenWebbooksCon("mysql","transaction");
			response="true";
		}
		catch(Exception e)
		{
			response="false";
			e.printStackTrace();
		}
		
		return Response.status(201).entity(response).build();
	}

	
	@GET 
	@Path("/funGetAccountMaster")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetAccountMaster(@QueryParam("Type") String type,@QueryParam("ClientCode") String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        String response="";

        
        try {
        	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
            st = webbookCon.createStatement();
            String sql="";
            
            sql=" select strAccountCode,strAccountName from tblacmaster "  
	            + " where strClientCode='" + clientCode + "' ";
	        if(!type.equals("All"))
	        {
	            sql+= " and strType='" + type + "'";
	        }      
            JSONArray arrObj=new JSONArray();
            ResultSet rsGLAcc=st.executeQuery(sql);
           
            while (rsGLAcc.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("AccountCode",rsGLAcc.getString(1));
            	obj.put("AccountName",rsGLAcc.getString(2));
            	arrObj.put(obj);            	 
            }
           
            rsGLAcc.close();
	        jObj.put("AccountDtl", arrObj);
	        st.close();
	        webbookCon.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj.toString();
	        }
	    }
	

	public JSONObject funGetAccountDtl(String searchItemName,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
       

        
        try {
        	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
            st = webbookCon.createStatement();
            String sql="";
            
            sql=" select strAccountCode,strAccountName from tblacmaster "  
	            + " where strClientCode='" + clientCode + "' ";
	             
            JSONArray arrObj=new JSONArray();
            ResultSet rs=st.executeQuery(sql);
           
            while (rs.next()) 
            {
            	JSONArray jArrDataRow=new JSONArray();
            	
            	jArrDataRow.put(rs.getString(1));
            	jArrDataRow.put(rs.getString(2)); 
            	
                arrObj.put(jArrDataRow);
            }
           
            rs.close();
	        jObj.put(searchItemName, arrObj);
	        st.close();
	        webbookCon.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	return jObj;
	        }
	    }
	
	
	
	@POST
	@Path("/funPostRevenueToCMS")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostRevenueInfo(JSONObject objBillData)
	{
		String response = "NA";
		response = funInsertRevenueData(objBillData);
		/*if(!funInsertRevenueData(objBillData).equals("NA"))
		{
			
		}*/
		return Response.status(201).entity(response).build();
	}

	@SuppressWarnings("finally")
    private String funInsertRevenueData(JSONObject objBillData)
    {
	int res = 0;
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webbookCon = null;
	Statement st = null;
	String sql = "";
	String billDate = "";
	String sql_insertRV = "";
	String controlAccCode = "";
	String billableAccCode = "";
	String billableAccName = "";
	String suspenceAccCode = "";
	String debtSusoAcctCode = "";
	String sancCode = "";
	String ECSBankcode = "";
	String currentDate = "";
	String clientCode = "";
	String POSCode = "";
	String POSName = "";
	String JVNo = "";
	String userCode = "";
	String propertyCode = "01";
	
	
	boolean flgData = false;
	HashMap<String, clsJVDtlModel> hmJVDtlata = new HashMap<>();
	try
	{
	    webbookCon = objDb.funOpenWebbooksCon("mysql", "master");
	    st = webbookCon.createStatement();
	    Date objDate = new Date();
	    int day = objDate.getDate();
	    int month = objDate.getMonth() + 1;
	    int year = objDate.getYear() + 1900;
	    currentDate = year + "-" + month + "-" + day;
	    propertyCode=objBillData.getString("propertyCode");
	    if(objBillData.has("JVNo")){
	    	JVNo=objBillData.getString("JVNo");	
	    }
	    
	    String sql_Code = " select strControlCode,strBillableCode,strSuspenceCode,strDbtrSuspAcctCode,strSancCode,strECSBankcode" + ",strClientCode,strBillableName from tblpropertysetup  ";
	    ResultSet rs = st.executeQuery(sql_Code);
	    while (rs.next())
	    {
		controlAccCode = rs.getString(1);
		billableAccCode = rs.getString(2);
		suspenceAccCode = rs.getString(3);
		debtSusoAcctCode = rs.getString(4);
		sancCode = rs.getString(5);
		ECSBankcode = rs.getString(6);
		clientCode = rs.getString(7);
		billableAccName = rs.getString(8);
	    }
	    rs.close();
	    
	    Map<String, clsJVDtlModel> hmJVDtlModel = new HashMap<String, clsJVDtlModel>();
	    
	    clientCode = objBillData.getString("ClientCode");
	    POSCode = objBillData.getString("POSCode");
	    if(objBillData.has("POSName")){
	    	POSName = objBillData.getString("POSName");	
	    }
	    
	    billDate = objBillData.getString("POSDate");
	    userCode = objBillData.getString("User");
	    
	    
	    String billMonth=billDate.split("-")[1];
	    
	    
	    JSONArray mJsonSubGroupArray = (JSONArray) objBillData.get("SubGroupwise");
	    // System.out.println(mJsonSubGroupArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Cr", mJsonSubGroupArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
	    	System.out.println("Key=="+entry.getKey()+"--"+entry.getValue().getDblCrAmt());
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
	    }
	    
	    hmJVDtlata.clear();
	    JSONArray mJsonTaxArray = (JSONArray) objBillData.get("Taxwise");
	    // System.out.println(mJsonTaxArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Cr", mJsonTaxArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
	    	System.out.println("Key=="+entry.getKey()+"--"+entry.getValue().getDblCrAmt());
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
	    }
	    
	    hmJVDtlata.clear();
	    JSONArray mJsonDiscountArray = (JSONArray) objBillData.get("Discountwise");
	    // System.out.println(mJsonDiscountArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Dr", mJsonDiscountArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
	    }
	    
	    hmJVDtlata.clear();
	    double totalJVAmt = 0.0;
	    JSONArray mJsonCashwiseArray = (JSONArray) objBillData.get("MemberSettlewise");
	    System.out.println(mJsonCashwiseArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Dr", mJsonCashwiseArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
		totalJVAmt += entry.getValue().getDblDrAmt();
	    }
	    
	    hmJVDtlata.clear();
//	    JSONArray mJsonMemberArray = (JSONArray) objBillData.get("CreditSettlewise");
//	    // System.out.println(mJsonMemberArray);
//	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Dr", mJsonMemberArray);
//	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
//	    {
//		clsJVDtlModel objJVDtlModel = entry.getValue();
//		objJVDtlModel.setStrAccountCode(objJVDtlModel.getStrAccountCode());
//		objJVDtlModel.setStrAccountName(objJVDtlModel.getStrAccountName());
//		hmJVDtlModel.put(entry.getKey(), objJVDtlModel);
//		totalJVAmt += entry.getValue().getDblDrAmt();
//	    }
//	    
//	    hmJVDtlata.clear();
	    
	    System.out.println("Round Off= "+objBillData.has("RoundOffDtl"));
	    
	    if(objBillData.has("RoundOffDtl")){
	    	JSONArray mJsonRoundOffArray = (JSONArray) objBillData.get("RoundOffDtl");
		    if(null!=mJsonRoundOffArray){
			   hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlDataForRounOff("Cr", mJsonRoundOffArray);
			    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
			    {
			    	System.out.println("Key=="+entry.getKey()+"--"+entry.getValue().getDblCrAmt());
			    	hmJVDtlModel.put(entry.getKey(), entry.getValue());
			    }
		   }
	    }
	    
	    
	    //propertyCode = "01";
	    
	    String vouchNoToDel = "";
	    sql = "select strVouchNo from tbljvhd where strMasterPOS='" + POSCode + "' and date(dteVouchDate)='" + billDate + "' ";
    		if(propertyCode!=null && !propertyCode.isEmpty()){
    	    sql+= " and strPropertyCode='"+propertyCode+"' ";	
    	    }
	    		
	    rs = st.executeQuery(sql);
	    if (rs.next())
	    {
		vouchNoToDel = rs.getString(1);
	    }
	    rs.close();
	    
	    sql = " delete from tbljvhd where strVouchNo='" + vouchNoToDel + "' and strClientCode='"+clientCode+"' ";
	    st.execute(sql);
	    
	    sql = " delete from tbljvdtl where strVouchNo='" + vouchNoToDel + "' and strClientCode='"+clientCode+"'  ";
	    st.execute(sql);
	    
	    sql = " delete from tbljvdebtordtl where strVouchNo='" + vouchNoToDel + "' and strClientCode='"+clientCode+"' ";
	    st.execute(sql);
	    
	    if(!JVNo.isEmpty()){
	    	
	    	sql = " delete from tbljvhd where strVouchNo='" + JVNo + "' and strClientCode='"+clientCode+"' ";
		    st.execute(sql);
		    
		    sql = " delete from tbljvdtl where strVouchNo='" + JVNo + "' and strClientCode='"+clientCode+"'  ";
		    st.execute(sql);
		    
		    sql = " delete from tbljvdebtordtl where strVouchNo='" + JVNo + "' and strClientCode='"+clientCode+"' ";
		    st.execute(sql);
		    
	    }else{
	    	JVNo = funGenerateDocumentCode(currentDate, clientCode, propertyCode);
	    }

	    String onlyDate=billDate.split(" ")[0];
	    String daydateonly=onlyDate.split("-")[2];
	    if(daydateonly.length()==1)
	    {
	    	daydateonly = "0"+onlyDate.split("-")[2];
	    }
	   
	    String narration = POSName+" Sales Data for " +daydateonly+"-"+billDate.split("-")[1]+"-"+billDate.split("-")[0];
	     sql = "";
	    
	    String sql_insertJVHd = " insert into tbljvhd (strVouchNo,strNarration,strSancCode,strType,dteVouchDate, " + " intVouchMonth,dblAmt,strTransType,strTransMode,strModuleType,strMasterPOS,strUserCreated,strUserEdited, " + " dteDateCreated ,dteDateEdited,strClientCode,strPropertyCode,intVouchNum) values ";
	    sql_insertJVHd += " ('" + JVNo + "','" + narration + "','" + sancCode + "','None','" + billDate + "','"+billMonth+"','" + totalJVAmt + "' " + ",'R','A','AR','" + POSCode + "','"+userCode+"','"+userCode+"','" + currentDate + "','" + currentDate + "','" + clientCode + "','" + propertyCode + "','')";
	    res = st.executeUpdate(sql_insertJVHd);
	    
	    sql_insertRV = "insert into tbljvdtl (strVouchNo,strAccountCode,strAccountName,strCrDr,dblDrAmt,dblCrAmt,strNarration," + "strOneLine,strClientCode,strPropertyCode) values ";
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlModel.entrySet())
	    {
			clsJVDtlModel obj = entry.getValue();
			System.out.println("KEY== "+entry.getKey());
			sql += ",('" + JVNo + "','" + obj.getStrAccountCode() + "','" + obj.getStrAccountName() + "','" + obj.getStrCrDr() + "','" + obj.getDblDrAmt() + "'" + ",'" + obj.getDblCrAmt() + "','','','" + clientCode + "','" + propertyCode + "')";
			flgData = true;
			
			if(obj.getDblCrAmt()==111526.62)
			{
				System.out.println("KEY== "+entry.getValue().getDblCrAmt());
			}
			
	    }
	    if (flgData)
	    {
			sql = sql.substring(1, sql.length());
			sql_insertRV += " " + sql;
			StringBuilder sqlJV = new StringBuilder(sql_insertRV);
			res = st.executeUpdate(sqlJV.toString());
	    }
	    else
	    {
		res = 1;
	    }
	    
	    flgData = false;
	    
	    JSONArray mJsonMemberCLDataArray = (JSONArray) objBillData.get("MemberCLData");
	    String sql_insertDebtorDtl = "insert into tbljvdebtordtl (strVouchNo,strDebtorCode,strDebtorName,strCrDr,dblAmt  " + ",strBillNo,strInvoiceNo,strNarration,strGuest,strAccountCode,strCreditNo,dteBillDate,dteInvoiceDate,dteDueDate " + ",strClientCode,strPropertyCode,StrPosCode,StrPosName,strRegistrationNo) values ";
	    sql = "";
	    JSONObject mJsonObject = new JSONObject();
	    for (int i = 0; i < mJsonMemberCLDataArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonMemberCLDataArray.get(i);
		String debtorCode = mJsonObject.get("DebtorCode").toString();
		String debtorName = mJsonObject.get("DebtorName").toString();
		String billNo = mJsonObject.get("BillNo").toString();
		billDate = mJsonObject.get("BillDate").toString();
		double billAmt = Double.parseDouble(mJsonObject.get("BillAmt").toString());
		clientCode = mJsonObject.get("ClientCode").toString();
		String cmsPOSCode = mJsonObject.get("CMSPOSCode").toString();
		POSCode = mJsonObject.get("POSCode").toString();
		String AccountCode = mJsonObject.get("AccountCode").toString();
		String posName = mJsonObject.get("POSName").toString();
		sql += ",('" + JVNo + "','" + debtorCode + "','" + debtorName + "','Dr','" + billAmt + "','" + billNo + "','','' " + ",'','" + AccountCode + "','','" + billDate + "','','" + billDate + "','" + clientCode + "','" + propertyCode + "'" + ",'" + POSCode + "','" + posName + "','')";
		flgData = true;
	    }
	    
	    String sendeid=objBillData.getString("senderMailId").toString();
	    String senderPassward=objBillData.getString("senderMailPassward").toString();
	    String reciverid=objBillData.getString("reciverMailId").toString();
	    funProfitAndLossReport(sendeid, senderPassward, reciverid, billDate, clientCode);
	    if (flgData)
	    {
		sql = sql.substring(1, sql.length());
		sql_insertDebtorDtl += " " + sql;
		StringBuilder sqlJVDebtorDtl = new StringBuilder(sql_insertDebtorDtl);
		res = st.executeUpdate(sqlJVDebtorDtl.toString());
		return JVNo;
	    }
	    return JVNo;
	}
	catch (Exception e)
	{
		JVNo = "NA";
	    e.printStackTrace();
	    return JVNo;
	}
	finally
	{
	    try
	    {
		st.close();
		webbookCon.close();
	//	 return  JVNo ;
	    }
	    catch (SQLException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
		// return  JVNo ;
	    }
	 
	}
	 
    }
	
	
	/*	
	@SuppressWarnings("finally")
    private String funInsertRevenueData(JSONObject objBillData)
    {
	int res = 0;
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webbookCon = null;
	Statement st = null;
	String sql = "";
	String billDate = "";
	String sql_insertRV = "";
	String controlAccCode = "";
	String billableAccCode = "";
	String billableAccName = "";
	String suspenceAccCode = "";
	String debtSusoAcctCode = "";
	String sancCode = "";
	String ECSBankcode = "";
	String currentDate = "";
	String clientCode = "";
	String POSCode = "";
	String POSName = "";
	String JVNo = "";
	String userCode = "";
	String propertyCode = "";
	boolean flgData = false;
	HashMap<String, clsJVDtlModel> hmJVDtlata = new HashMap<>();
	try
	{
	    webbookCon = objDb.funOpenWebbooksCon("mysql", "master");
	    st = webbookCon.createStatement();
	    Date objDate = new Date();
	    int day = objDate.getDate();
	    int month = objDate.getMonth() + 1;
	    int year = objDate.getYear() + 1900;
	    currentDate = year + "-" + month + "-" + day;
	    
	    String sql_Code = " select strControlCode,strBillableCode,strSuspenceCode,strDbtrSuspAcctCode,strSancCode,strECSBankcode" + ",strClientCode,strBillableName from tblpropertysetup  ";
	    ResultSet rs = st.executeQuery(sql_Code);
	    while (rs.next())
	    {
		controlAccCode = rs.getString(1);
		billableAccCode = rs.getString(2);
		suspenceAccCode = rs.getString(3);
		debtSusoAcctCode = rs.getString(4);
		sancCode = rs.getString(5);
		ECSBankcode = rs.getString(6);
		clientCode = rs.getString(7);
		billableAccName = rs.getString(8);
	    }
	    rs.close();
	    
	    Map<String, clsJVDtlModel> hmJVDtlModel = new HashMap<String, clsJVDtlModel>();
	    
	    clientCode = objBillData.getString("ClientCode");
	    POSCode = objBillData.getString("POSCode");
	    POSName = objBillData.getString("POSName");
	    billDate = objBillData.getString("POSDate");
	    userCode = objBillData.getString("User");
	    String billMonth=billDate.split("-")[1];
	    
	    JSONArray mJsonSubGroupArray = (JSONArray) objBillData.get("SubGroupwise");
	    // System.out.println(mJsonSubGroupArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Cr", mJsonSubGroupArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
	    	System.out.println("Key=="+entry.getKey()+"--"+entry.getValue().getDblCrAmt());
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
	    }
	    
	    hmJVDtlata.clear();
	    JSONArray mJsonTaxArray = (JSONArray) objBillData.get("Taxwise");
	    // System.out.println(mJsonTaxArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Cr", mJsonTaxArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
	    	System.out.println("Key=="+entry.getKey()+"--"+entry.getValue().getDblCrAmt());
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
	    }
	    
	    hmJVDtlata.clear();
	    JSONArray mJsonDiscountArray = (JSONArray) objBillData.get("Discountwise");
	    // System.out.println(mJsonDiscountArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Dr", mJsonDiscountArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
	    }
	    
	    hmJVDtlata.clear();
	    double totalJVAmt = 0.0;
	    JSONArray mJsonCashwiseArray = (JSONArray) objBillData.get("CashSettlewise");
	    System.out.println(mJsonCashwiseArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Dr", mJsonCashwiseArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
		totalJVAmt += entry.getValue().getDblDrAmt();
	    }
	    
	    hmJVDtlata.clear();
	    JSONArray mJsonMemberArray = (JSONArray) objBillData.get("CreditSettlewise");
	    // System.out.println(mJsonMemberArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Dr", mJsonMemberArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
		clsJVDtlModel objJVDtlModel = entry.getValue();
		objJVDtlModel.setStrAccountCode(objJVDtlModel.getStrAccountCode());
		objJVDtlModel.setStrAccountName(objJVDtlModel.getStrAccountName());
		hmJVDtlModel.put(entry.getKey(), objJVDtlModel);
		totalJVAmt += entry.getValue().getDblDrAmt();
	    }
	    
	    hmJVDtlata.clear();
	    JSONArray mJsonRoundOffArray = (JSONArray) objBillData.get("RoundOffDtl");
	    // System.out.println(mJsonRoundOffArray);
	  //  hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlData("Cr", mJsonRoundOffArray);
	    hmJVDtlata = (HashMap<String, clsJVDtlModel>) funGetJVDtlDataForRounOff("Cr", mJsonRoundOffArray);
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlata.entrySet())
	    {
	    	System.out.println("Key=="+entry.getKey()+"--"+entry.getValue().getDblCrAmt());
		hmJVDtlModel.put(entry.getKey(), entry.getValue());
	    }
	    
	    propertyCode = "01";
	    
	    String vouchNoToDel = "";
	    sql = "select strVouchNo from tbljvhd where strMasterPOS='" + POSCode + "' and date(dteVouchDate)='" + billDate + "' ";
	    rs = st.executeQuery(sql);
	    if (rs.next())
	    {
		vouchNoToDel = rs.getString(1);
	    }
	    rs.close();
	    
	    sql = " delete from tbljvhd where strVouchNo='" + vouchNoToDel + "' and strClientCode='"+clientCode+"' ";
	    st.execute(sql);
	    
	    sql = " delete from tbljvdtl where strVouchNo='" + vouchNoToDel + "' and strClientCode='"+clientCode+"'  ";
	    st.execute(sql);
	    
	    sql = " delete from tbljvdebtordtl where strVouchNo='" + vouchNoToDel + "' and strClientCode='"+clientCode+"' ";
	    st.execute(sql);
	    
	    String daydateonly=billDate.split("-")[2];
	    if(daydateonly.length()==1)
	    {
	    	daydateonly = "0"+billDate.split("-")[2];
	    }
	   
	    
	    sql = "";
	    String narration = "REVENUE POSTED For " +daydateonly+"-"+billDate.split("-")[1]+"-"+billDate.split("-")[0] +" "+POSName;
	    JVNo = funGenerateDocumentCode(currentDate, clientCode, propertyCode);
	    String sql_insertJVHd = " insert into tblJVHd (strVouchNo,strNarration,strSancCode,strType,dteVouchDate, " + " intVouchMonth,dblAmt,strTransType,strTransMode,strModuleType,strMasterPOS,strUserCreated,strUserEdited, " + " dteDateCreated ,dteDateEdited,strClientCode,strPropertyCode,intVouchNum) values ";
	    sql_insertJVHd += " ('" + JVNo + "','" + narration + "','" + sancCode + "','None','" + billDate + "','"+billMonth+"','" + totalJVAmt + "' " + ",'R','A','AR','" + POSCode + "','"+userCode+"','"+userCode+"','" + currentDate + "','" + currentDate + "','" + clientCode + "','" + propertyCode + "','')";
	    res = st.executeUpdate(sql_insertJVHd);
	    
	    sql_insertRV = "insert into tblJVDtl (strVouchNo,strAccountCode,strAccountName,strCrDr,dblDrAmt,dblCrAmt,strNarration," + "strOneLine,strClientCode,strPropertyCode) values ";
	    for (Map.Entry<String, clsJVDtlModel> entry : hmJVDtlModel.entrySet())
	    {
			clsJVDtlModel obj = entry.getValue();
			System.out.println("KEY== "+entry.getKey());
			sql += ",('" + JVNo + "','" + obj.getStrAccountCode() + "','" + obj.getStrAccountName() + "','" + obj.getStrCrDr() + "','" + obj.getDblDrAmt() + "'" + ",'" + obj.getDblCrAmt() + "','','','" + clientCode + "','" + propertyCode + "')";
			flgData = true;
			
			if(obj.getDblCrAmt()==111526.62)
			{
				System.out.println("KEY== "+entry.getValue().getDblCrAmt());
			}
			
	    }
	    if (flgData)
	    {
			sql = sql.substring(1, sql.length());
			sql_insertRV += " " + sql;
			StringBuilder sqlJV = new StringBuilder(sql_insertRV);
			res = st.executeUpdate(sqlJV.toString());
	    }
	    else
	    {
		res = 1;
	    }
	    
	    flgData = false;
	    
	    JSONArray mJsonMemberCLDataArray = (JSONArray) objBillData.get("MemberCLData");
	    String sql_insertDebtorDtl = "insert into tblJVDebtorDtl (strVouchNo,strDebtorCode,strDebtorName,strCrDr,dblAmt  " + ",strBillNo,strInvoiceNo,strNarration,strGuest,strAccountCode,strCreditNo,dteBillDate,dteInvoiceDate,dteDueDate " + ",strClientCode,strPropertyCode,StrPosCode,StrPosName,strRegistrationNo) values ";
	    sql = "";
	    JSONObject mJsonObject = new JSONObject();
	    for (int i = 0; i < mJsonMemberCLDataArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonMemberCLDataArray.get(i);
		String debtorCode = mJsonObject.get("DebtorCode").toString();
		String debtorName = mJsonObject.get("DebtorName").toString();
		String billNo = mJsonObject.get("BillNo").toString();
		billDate = mJsonObject.get("BillDate").toString();
		double billAmt = Double.parseDouble(mJsonObject.get("BillAmt").toString());
		clientCode = mJsonObject.get("ClientCode").toString();
		String cmsPOSCode = mJsonObject.get("CMSPOSCode").toString();
		POSCode = mJsonObject.get("POSCode").toString();
		String AccountCode = mJsonObject.get("AccountCode").toString();
		String posName = mJsonObject.get("POSName").toString();
		sql += ",('" + JVNo + "','" + debtorCode + "','" + debtorName + "','Dr','" + billAmt + "','" + billNo + "','','' " + ",'','" + AccountCode + "','','" + billDate + "','','" + billDate + "','" + clientCode + "','" + propertyCode + "'" + ",'" + POSCode + "','" + posName + "','')";
		flgData = true;
	    }
	    
	    if (flgData)
	    {
		sql = sql.substring(1, sql.length());
		sql_insertDebtorDtl += " " + sql;
		StringBuilder sqlJVDebtorDtl = new StringBuilder(sql_insertDebtorDtl);
		res = st.executeUpdate(sqlJVDebtorDtl.toString());
		return JVNo;
	    }
	    return JVNo;
	}
	catch (Exception e)
	{
		JVNo = "NA";
	    e.printStackTrace();
	    return JVNo;
	}
	finally
	{
	    try
	    {
		st.close();
		webbookCon.close();
	//	 return  JVNo ;
	    }
	    catch (SQLException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
		// return  JVNo ;
	    }
	 
	}
	 
    }
	
*/	
	private Map funGetJVDtlDataForRounOff(String CrDr,JSONArray mJsonJVDataArray)
	{
		Map hmSubgroupData=new HashMap<String, clsJVDtlModel>();
		String sql="",accName="";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st = null;
    	try
		{
    		webbookCon=objDb.funOpenWebbooksCon("mysql","master");
            st = webbookCon.createStatement();
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonJVDataArray.length(); i++) 
			{				
			    mJsonObject =(JSONObject) mJsonJVDataArray.get(i);
			    String accountCode=mJsonObject.get("AccountCode").toString(); 
			    sql= " select ifnull(strAccountName,''),strAccountCode from tblacmaster "  
	               + " where strAccountName like '%ROUNDOFF%' ";
			    ResultSet rs=st.executeQuery(sql);
			    if(rs.next())
			    {
			    	accName=rs.getString(1);
			    	accountCode=rs.getString(2);
			    }			    
			    double crAmt=Double.parseDouble(mJsonObject.get("CRAmt").toString());
			    double drAmt=Double.parseDouble(mJsonObject.get("DRAmt").toString());			  
			    
			    if (hmSubgroupData.containsKey(accountCode))
	            {
			    	clsJVDtlModel objJVDtl = (clsJVDtlModel) hmSubgroupData.get(accountCode);
	                double creditAmt = objJVDtl.getDblCrAmt();
	                double debitAmt=objJVDtl.getDblDrAmt();
	                creditAmt += crAmt;
	                debitAmt += drAmt;
	                objJVDtl.setDblCrAmt(creditAmt);
	                objJVDtl.setDblDrAmt(debitAmt);
	                hmSubgroupData.put(accountCode, objJVDtl);	              
	            }
	            else
	            {
	                clsJVDtlModel objJVDtl=new clsJVDtlModel(accountCode, accName,CrDr,drAmt, crAmt);
	            	hmSubgroupData.put(accountCode, objJVDtl);
	            }
			}
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return hmSubgroupData;
	}
	

	private Map funGetJVDtlData(String CrDr,JSONArray mJsonJVDataArray)
	{
		Map hmSubgroupData=new HashMap<String, clsJVDtlModel>();
		String sql="",accName="";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st = null;
    	try
		{
    		webbookCon=objDb.funOpenWebbooksCon("mysql","master");
            st = webbookCon.createStatement();
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonJVDataArray.length(); i++) 
			{				
			    mJsonObject =(JSONObject) mJsonJVDataArray.get(i);
			    String accountCode=mJsonObject.get("AccountCode").toString(); 
			    sql= " select ifnull(strAccountName,'') from tblacmaster "  
	               + " where strAccountCode='" + accountCode + "' ";
			    ResultSet rs=st.executeQuery(sql);
			    if(rs.next())
			    {
			    	accName=rs.getString(1);
			    }			    
			    double crAmt=Double.parseDouble(mJsonObject.get("CRAmt").toString());
			    double drAmt=Double.parseDouble(mJsonObject.get("DRAmt").toString());			  
			    
			    if (hmSubgroupData.containsKey(accountCode))
	            {
			    	clsJVDtlModel objJVDtl = (clsJVDtlModel) hmSubgroupData.get(accountCode);
	                double creditAmt = objJVDtl.getDblCrAmt();
	                double debitAmt=objJVDtl.getDblDrAmt();
	                creditAmt += crAmt;
	                debitAmt += drAmt;
	                objJVDtl.setDblCrAmt(creditAmt);
	                objJVDtl.setDblDrAmt(debitAmt);
	                hmSubgroupData.put(accountCode, objJVDtl);	              
	            }
	            else
	            {
	                clsJVDtlModel objJVDtl=new clsJVDtlModel(accountCode, accName,CrDr,drAmt, crAmt);
	            	hmSubgroupData.put(accountCode, objJVDtl);
	            }
			}
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return hmSubgroupData;
	}
	
	
	
	public String funGenerateDocumentCode(String dtTransDate,String clientCode,String propCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st = null;
		String[] spDate=dtTransDate.split("-");
		Date dt=new Date();
		String years=String.valueOf((dt.getYear()+1900)-Integer.parseInt(spDate[0]));
		String transYear=funGetAlphabet(Integer.parseInt(years));
		String transMonth=funGetAlphabet(Integer.parseInt(spDate[1])-1);
		String strDocLiteral="";
		String sql="";
		String strDocNo="";
	    strDocLiteral= "JV";
	    
	    try
		{
	    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
	        st = webbookCon.createStatement();
			sql="select ifnull(max(MID(a.strVouchNo,7,6)),'' ) "
				+ " from tbljvhd a where MID(a.strVouchNo,5,1) = '"+transYear+"' "
				+ " and MID(a.strVouchNo,6,1) = '"+transMonth+"' "
				+ " and MID(a.strVouchNo,1,2) = '"+propCode+"' and strClientCode='"+clientCode+"' ";
			
			ResultSet rs=st.executeQuery(sql);
			if(rs.next())
			{
				long lastno=rs.getInt(1);
				strDocNo=propCode+strDocLiteral+transYear+transMonth+String.format("%06d", lastno+1);
			}
			else
			{
				strDocNo=propCode+strDocLiteral+transYear+transMonth+String.format("%06d", 1);
			}
		}
	    catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return strDocNo;
	}
	
	
	private String funGetAlphabet(int no)
	{
		String[] alphabets= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		return alphabets[no];
	}
	
	
	 	@GET
	    @Path("/funGetWebBooksSearch")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funGetSundryDebtor(@QueryParam("masterName") String masterName, @QueryParam("searchCode") String searchCode, 
	    		@QueryParam("clientCode") String clientCode, @QueryParam("propCode") String propCode)
	    {
		
		JSONObject jObjSearchData = new JSONObject();
		
		try
		{
		    jObjSearchData = funGetSundrySearch(masterName, searchCode, clientCode,propCode);
		}
		catch (Exception e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return jObjSearchData;
		
	    }
	 	
	 
		public JSONObject funGetSundrySearch(String masterName,String searchCode,String clientCode,String propCode)
	 	{
	 		JSONObject jObjSearchData = new JSONObject();
			JSONArray jArrData = new JSONArray();
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection webbookCon=null;
	        Statement st = null;
			String sql="";
			
		try
		{
			
			switch (masterName)
			{
			    case "SundryDebtorWeb-Service":
							
			    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
			        st = webbookCon.createStatement();	
			    sql = " select a.strDebtorCode , a.strFirstName "
			    		+ " from tblsundarydebtormaster a where a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
				
				    ResultSet rs=st.executeQuery(sql);
				    while(rs.next())
					{
				    	JSONArray jArrtem =new JSONArray();
				    	jArrtem.put(rs.getString(1));
				    	 jArrtem.put(rs.getString(2));
				    	 jArrData.put(jArrtem);
					}
				    break;
				    
				    
			    case "SundryCreditorWeb-Service":
					
			    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
			        st = webbookCon.createStatement();	
			        sql = " select a.strCreditorCode , a.strFirstName "
			    		+ " from tblsundarycreditormaster a where a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
				
				    ResultSet rsCr=st.executeQuery(sql);
				    while(rsCr.next())
					{
				    	JSONArray jArrtem =new JSONArray();
				    	jArrtem.put(rsCr.getString(1));
				    	 jArrtem.put(rsCr.getString(2));
				    	 jArrData.put(jArrtem);
					}
				    break;    
				    
			    case "AccountMasterWeb-Service":
					
			    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
			        st = webbookCon.createStatement();	
			    sql = " select a.strAccountCode , a.strAccountName "
			    		+ " from tblacmaster a where a.strOperational='Yes' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
				
				    ResultSet rsAcc=st.executeQuery(sql);
				    while(rsAcc.next())
					{
				    	JSONArray jArrtem =new JSONArray();
				    	jArrtem.put(rsAcc.getString(1));
				    	jArrtem.put(rsAcc.getString(2));
				    	jArrData.put(jArrtem);
					}
				    break;	
			    
			    case "AccountMasterGLOnlyWeb-Service":
					
			    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
			        st = webbookCon.createStatement();	
			        sql = " select a.strAccountCode , a.strAccountName "
			    		+ " from tblacmaster a where a.strOperational='Yes' and a.strType='GL Code' and a.strDebtor='No' and strCreditor='No' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;				
				    ResultSet rsAccGL=st.executeQuery(sql);
				    while(rsAccGL.next())
					{
				    	JSONArray jArrtem =new JSONArray();
				    	jArrtem.put(rsAccGL.getString(1));
				    	jArrtem.put(rsAccGL.getString(2));
				    	jArrData.put(jArrtem);
					}
				    break;	
				    
			    case "TaxWeb-Service":
					
			    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
			        st = webbookCon.createStatement();	
			    sql = " select a.strAccountCode , a.strAccountName "
			    		+ " from tblacmaster a where a.strOperational='Yes' and a.strType='GL Code' and a.strDebtor='No' and strCreditor='No' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
				
				    ResultSet rsAcTaxCode=st.executeQuery(sql);
				    while(rsAcTaxCode.next())
					{
				    	JSONArray jArrtem =new JSONArray();
				    	jArrtem.put(rsAcTaxCode.getString(1));
				    	jArrtem.put(rsAcTaxCode.getString(2));
				    	jArrData.put(jArrtem);
					}
				    break;   
				    
				    
		    case "DiscountWeb-Service":
					
			    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
			        st = webbookCon.createStatement();	
			    sql = " select a.strAccountCode , a.strAccountName "
			    		+ " from tblacmaster a where a.strOperational='Yes' and a.strType='GL Code' and a.strDebtor='No' and strCreditor='No' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
				
				    ResultSet rsAcDiscCode=st.executeQuery(sql);
				    while(rsAcDiscCode.next())
					{
				    	JSONArray jArrtem =new JSONArray();
				    	jArrtem.put(rsAcDiscCode.getString(1));
				    	jArrtem.put(rsAcDiscCode.getString(2));
				    	jArrData.put(jArrtem);
					}
				    break;  
				    
				    
		    case "RoundOffWeb-Service":
				
		    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
		        st = webbookCon.createStatement();	
		    sql = " select a.strAccountCode , a.strAccountName "
		    		+ " from tblacmaster a where a.strOperational='Yes' and a.strType='GL Code' and a.strDebtor='No' and strCreditor='No' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
			
			    ResultSet rsAcRoundOffCode=st.executeQuery(sql);
			    while(rsAcRoundOffCode.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rsAcRoundOffCode.getString(1));
			    	jArrtem.put(rsAcRoundOffCode.getString(2));
			    	jArrData.put(jArrtem);
				}
			    break;
			    
          case "ExtraChargeWeb-Service":
				
		    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
		        st = webbookCon.createStatement();	
		    sql = " select a.strAccountCode , a.strAccountName "
		    		+ " from tblacmaster a where a.strOperational='Yes' and a.strType='GL Code' and a.strDebtor='No' and strCreditor='No' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
			
			    ResultSet rsAcExtraCharge=st.executeQuery(sql);
			    while(rsAcExtraCharge.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rsAcExtraCharge.getString(1));
			    	jArrtem.put(rsAcExtraCharge.getString(2));
			    	jArrData.put(jArrtem);
				}
			    rsAcExtraCharge.close();
			    break;
				     
          case "SettlementWeb-Service":
				
		    	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
		        st = webbookCon.createStatement();	
		        sql = " select a.strAccountCode , a.strAccountName "
		    		+ " from tblacmaster a where a.strOperational='Yes' and a.strType='GL Code' and a.strDebtor='No' and strCreditor='No' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
			
			    ResultSet rsAcSettlement=st.executeQuery(sql);
			    while(rsAcSettlement.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rsAcSettlement.getString(1));
			    	jArrtem.put(rsAcSettlement.getString(2));
			    	jArrData.put(jArrtem);
				}
			    rsAcSettlement.close();
			    break;
			    


          case "OtherChargeWeb-Service":
			    
          		webbookCon=objDb.funOpenWebbooksCon("mysql","master");
		        st = webbookCon.createStatement();	
		        sql = " select a.strAccountCode , a.strAccountName "
		    		+ " from tblacmaster a where a.strOperational='Yes' and a.strType='GL Code' and a.strDebtor='No' and strCreditor='No' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
			
			    ResultSet rsAcOtherCharge=st.executeQuery(sql);
			    while(rsAcOtherCharge.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rsAcOtherCharge.getString(1));
			    	jArrtem.put(rsAcOtherCharge.getString(2));
			    	jArrData.put(jArrtem);
				}
			    rsAcOtherCharge.close();
			    break;
			    

			    
          case "SupplierAccCodeWeb-Service" :
        		webbookCon=objDb.funOpenWebbooksCon("mysql","master");
		        st = webbookCon.createStatement();	
		        sql = " select a.strAccountCode , a.strAccountName "
		    		+ " from tblacmaster a where a.strOperational='Yes' and  strCreditor='Yes' and a.strClientCode = '"+clientCode+"' and a.strPropertyCode='"+propCode+"'  " ;	
			
			    ResultSet rsAcSupp=st.executeQuery(sql);
			    while(rsAcSupp.next())
			    {
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rsAcSupp.getString(1));
			    	jArrtem.put(rsAcSupp.getString(2));
			    	jArrData.put(jArrtem);
				}
				rsAcSupp.close();
			    break;
			    
			

			    
			}
				jObjSearchData.put(masterName, jArrData);
				
				
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			return jObjSearchData;
		}
		
	 	}
	 	
	 	@GET
	    @Path("/funGetWebBooksData")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funGeSundryDebtorData(@QueryParam("strAccountCode") String strAccountCode, @QueryParam("clientCode") String clientCode)
	    {
			JSONObject jObjData = new JSONObject();
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection webbookCon=null;
	        Statement st = null;
			String sql="";
			try
			{
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();	
			    sql = " select a.strAccountCode , a.strAccountName "
			    		+ " from tblacmaster a where  a.strAccountCode = '"+strAccountCode+"' and a.strClientCode = '"+clientCode+"'  " ;	
			
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	jObjData.put("strAccountCode",rs.getString(1));
			    	jObjData.put("strAccountName",rs.getString(2));
				}
			    
			    
			}catch(Exception ex)
			{
				try {
					webbookCon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ex.printStackTrace();
			}finally
			{
				return jObjData;
			}
	    }
	 	
	 	@GET
	    @Path("/funGetWebBooksSundryCreditorData")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funGetWebBooksSundryOrDebtorData(@QueryParam("strDocCode") String strDocCode, @QueryParam("clientCode") String clientCode)
	    {
			JSONObject jObjData = new JSONObject();
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection webbookCon=null;
	        Statement st = null;
			String sql="";
			try
			{
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();	
			    sql = " select a.strCreditorCode , a.strFirstName "
			    		+ " from tblsundarycreditormaster a where  a.strCreditorCode = '"+strDocCode+"' and a.strClientCode = '"+clientCode+"'  " ;	
			
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	jObjData.put("strCreditorCode",rs.getString(1));
			    	jObjData.put("strFirstName",rs.getString(2));
				}
			    
			    
			}catch(Exception ex)
			{
				try {
					webbookCon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ex.printStackTrace();
			}finally
			{
				return jObjData;
			}
			
	    }
	 	
	 	@POST
		@Path("/funSaveJVFromComericalTaxInvoice")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveJVFromComericalTaxInvoice(JSONObject objCLData)
		{
	 		JSONObject jObj =new JSONObject();
	 		try
	 		{
			String strJVCode = funInsertJVFromOtherTransections(objCLData);	
			
			jObj.put("strJVCode", strJVCode);
	 		}
	 		catch(Exception ex)
	 		{
	 			ex.printStackTrace();
	 		}
	 		finally
	 		{
	 			return Response.status(201).entity(jObj).build();
	 		}
		}
	
	 	// former code Not used
	 	
	 	public String funInsertJVFromOtherTransections(JSONObject objCLData)
	 	{
	 		String strJVCode ="";
	 		clsDatabaseConnection objDb=new clsDatabaseConnection();
	 		Connection webbookCon=null;
	 		Statement st = null;
			String sql="";
			String strCrDrAcCode="";
			try
			{
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();	
				String narration = objCLData.getString("strNarration");
				String docCode = (objCLData.getString("strNarration")).split(":")[1];
				String hdSql="Select strVouchNo from tbljvhd where strNarration='"+objCLData.getString("strNarration")+"' and strClientCode='"+objCLData.getString("strClientCode")+"' and strPropertyCode = '"+objCLData.getString("strPropertyCode")+"' ";
				 ResultSet rs=st.executeQuery(hdSql);
				    while(rs.next())
					{
				    	strJVCode = rs.getString(1);
					}
				if(strJVCode.length()>0)
				{
					String deleteDtlSql="delete from tbljvdtl  "
							+ " where strVouchNo= '"+strJVCode+"' and strClientCode='"+objCLData.getString("strClientCode")+"' ";
					int i=st.executeUpdate(deleteDtlSql);
					
					
					String deleteHdSql="delete from tbljvhd  "
							+ " where strVouchNo= '"+strJVCode+"' and strClientCode='"+objCLData.getString("strClientCode")+"' ";
					int j=st.executeUpdate(deleteHdSql);
				
					String deleteDebtorDtlSql="delete from tbljvdebtordtl  "
							+ " where strVouchNo= '"+strJVCode+"' and strClientCode='"+objCLData.getString("strClientCode")+"' ";
					int k=st.executeUpdate(deleteDebtorDtlSql);
				
					
				
				}else
				{
					strJVCode=funGenrateJVCode(objCLData.getString("dteVouchDate") ,objCLData.getString("strPropertyCode"),objCLData.getString("strClientCode"));
				}
				
				sql=" Insert into tbljvhd (strVouchNo ,strNarration , strSancCode , strType , dteVouchDate , intVouchMonth, dblAmt, strTransType, strTransMode, "
						+ " strModuleType ,strMasterPOS ,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strPropertyCode,intVouchNum ) "
						+ " Values ( '"+strJVCode+"', '"+objCLData.getString("strNarration")+"' ,'"+objCLData.getString("strSancCode")+"','"+objCLData.getString("strType")+"',"
						+ " '"+objCLData.getString("dteVouchDate")+"','"+objCLData.getString("intVouchMonth")+"','"+objCLData.getString("dblAmt")+"',"
						+ " '"+objCLData.getString("strTransType")+"','"+objCLData.getString("strTransMode")+"' "
						+ " ,'"+objCLData.getString("strModuleType")+"','"+objCLData.getString("strMasterPOS")+"','"+objCLData.getString("strUserCreated")+"',"
						+ " '"+objCLData.getString("strUserEdited")+"', '"+objCLData.getString("dteDateCreated")+"', '"+objCLData.getString("dteDateEdited")+"', "
						+ " '"+objCLData.getString("strClientCode")+"' , '"+objCLData.getString("strPropertyCode")+"',''  ) ";
				
				String propCode= objCLData.getString("strPropertyCode");
			
				
				int val =st.executeUpdate(sql);
				
				JSONArray jArrJVdtl = (JSONArray) objCLData.get("ArrJVDtl");
				
				if(val>0)
				{
					sql=" Insert into tbljvdtl ( strVouchNo, strAccountCode , strAccountName ,strCrDr , dblDrAmt ,dblCrAmt, strNarration, strOneLine, strClientCode ,strPropertyCode) values " ;
							
					for(int i=0; i<jArrJVdtl.length();i++)
					{
						JSONObject dtlJsonObject =(JSONObject) jArrJVdtl.get(i);
						String acCode =dtlJsonObject.getString("strAccountCode");
						String acName =dtlJsonObject.getString("strAccountName");
						if(acCode.equals("") && acName.equals(""))
						{
							String sqlAcCr="";
							if(narration.contains("GRN") || narration.contains("Pur-Ret") )
							{
								sqlAcCr= " select a.strAccountCode,a.strAccountName from tblacmaster a where  a.strCreditor='Yes' and a.strOperational='Yes' and a.strType='GL Code' and a.strClientCode='"+dtlJsonObject.getString("strClientCode")+"' and a.strPropertyCode='"+propCode+"' ";
							}else if(narration.contains("Invoice") || narration.contains("Sales-Ret") )
							{
								sqlAcCr= " select a.strAccountCode,a.strAccountName from tblacmaster a where   a.strDebtor='Yes' and a.strOperational='Yes' and a.strType='GL Code' and a.strClientCode='"+dtlJsonObject.getString("strClientCode")+"' and a.strPropertyCode='"+propCode+"' ";
							}
							
							ResultSet rsAcCr=st.executeQuery(sqlAcCr);
							if(rsAcCr.next())
							{
								acCode = rsAcCr.getString(1);
								acName = rsAcCr.getString(2);
								strCrDrAcCode=acCode;
								rsAcCr.close();
							}
						}
						sql+= " ( '"+strJVCode+"','"+acCode+"','"+acName+"', "
								+ " '"+dtlJsonObject.getString("strCrDr")+"' ,'"+dtlJsonObject.getString("dblDrAmt")+"' ,'"+dtlJsonObject.getString("dblCrAmt")+"', "
								+ " '"+dtlJsonObject.getString("strNarration")+"' ,'"+dtlJsonObject.getString("strOneLine")+"' , "
								+ " '"+dtlJsonObject.getString("strClientCode")+"' ,'"+dtlJsonObject.getString("strPropertyCode")+"' ),";
					}
					sql = sql.substring(0, sql.length()-1);
					
					st.executeUpdate(sql);
				}
				
				JSONArray jArrJVDebtordtl = (JSONArray) objCLData.get("ArrJVDebtordtl");
				
				if(val>0)
				{
					sql=" Insert into tbljvdebtordtl ( strVouchNo, strDebtorCode , strDebtorName ,strCrDr , dblAmt ,strBillNo, strInvoiceNo, "
							+ " strNarration, strGuest, strAccountCode, strCreditNo, dteBillDate, dteInvoiceDate, dteDueDate, strClientCode, "
							+ " strPropertyCode, strPOSCode, strPOSName, strRegistrationNo ) values " ;
							
					for(int i=0; i<jArrJVDebtordtl.length();i++)
					{
						JSONObject dtlJsonObject =(JSONObject) jArrJVDebtordtl.get(i);
						sql+= " ( '"+strJVCode+"','"+dtlJsonObject.getString("strDebtorCode")+"','"+dtlJsonObject.getString("strDebtorName")+"', "
								+ " '"+dtlJsonObject.getString("strCrDr")+"' ,'"+dtlJsonObject.getString("dblAmt")+"' ,'"+dtlJsonObject.getString("strBillNo")+"', "
								+ " '"+dtlJsonObject.getString("strInvoiceNo")+"' ,'"+dtlJsonObject.getString("strNarration")+"' , "
								+ " '"+dtlJsonObject.getString("strGuest")+"' ,'"+strCrDrAcCode+"' , "
								+ " '"+dtlJsonObject.getString("strCreditNo")+"' ,'"+dtlJsonObject.getString("dteBillDate")+"' , "
								+ " '"+dtlJsonObject.getString("dteInvoiceDate")+"' ,'"+dtlJsonObject.getString("dteDueDate")+"' , "
								+ " '"+dtlJsonObject.getString("strClientCode")+"' ,'"+dtlJsonObject.getString("strPropertyCode")+"' , "
								+ " '"+dtlJsonObject.getString("strPOSCode")+"' ,'"+dtlJsonObject.getString("strPOSName")+"' , "
								+ " '"+dtlJsonObject.getString("strRegistrationNo")+"'  ),";
					}
					sql = sql.substring(0, sql.length()-1);
					
					st.executeUpdate(sql);
				}
				
			}	
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				return strJVCode;
			}
	 		
	 		
	 	}
	 	
	 	
	 	public String funGenrateJVCode(String dtTransDate ,String propCode,String clientCode)
		{
	 		
	 		clsDatabaseConnection objDb=new clsDatabaseConnection();
	 		Connection webbookCon=null;
	 		Statement st = null;
	 		String strJVCode = "";
			String[] spDate=dtTransDate.split("-");
			Date dt=new Date();
			//String years=String.valueOf((dt.getYear()+1900)-Integer.parseInt(spDate[2]));
			String years=String.valueOf(Integer.parseInt(spDate[0]));
			String transYear=funGetAlphabet((Integer.parseInt(years)%26));
			String transMonth=funGetAlphabet(Integer.parseInt(spDate[1]));
			
			String strDocLiteral= "JV";
			String   sql="select ifnull(max(MID(a.strVouchNo,7,6)),'' ) "
				   		+ " from tbljvhd a where MID(a.strVouchNo,5,1) = '"+transYear+"' "
				   		+ " and MID(a.strVouchNo,6,1) = '"+transMonth+"' "
				   		+ " and MID(a.strVouchNo,1,2) = '"+propCode+"' and strClientCode='"+clientCode+"' ";
			
			try
			{
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();	
				ResultSet rs=st.executeQuery(sql);
				long lastno=0;
			    while(rs.next())
				{
			    	lastno=rs.getInt(1);
				}
				
			    if(lastno!=0)
				{
					
			    	strJVCode=propCode+strDocLiteral+transYear+transMonth+String.format("%06d", lastno+1);
			    	//strJVCode=strJVCode+"#"+lastno+1;
				}
				else
				{
					strJVCode=propCode+strDocLiteral+transYear+transMonth+String.format("%06d", 1);
					//strJVCode=strJVCode+"#"+1;
				}
			    
			    
				
			}	
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				return strJVCode;
			}
		
		}
	 	
	 	@POST
		@Path("/funGenrateJVforGRN")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funGenrateJVforGRN(JSONObject objCLData)
		{
	 		JSONObject jObj =new JSONObject();
	 		try
	 		{
			String strJVCode = funInsertJVFromOtherTransections(objCLData);	
			
			jObj.put("strJVCode", strJVCode);
	 		}
	 		catch(Exception ex)
	 		{
	 			ex.printStackTrace();
	 		}
	 		finally
	 		{
	 			return Response.status(201).entity(jObj).build();
	 		}
		}
	 	
	 	
	 	@POST
		@Path("/funGetGRNforBill")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funGetGRNforBill(JSONObject objCLData)
		{
	 		JSONObject jObj =new JSONObject();
	 		try
	 		{
	 			 jObj = funGetGRNDataforBill(objCLData);	
			
	 		}
	 		catch(Exception ex)
	 		{
	 			ex.printStackTrace();
	 		}
	 		finally
	 		{
	 			return Response.status(201).entity(jObj).build();
	 		}
		}
	 	
	 	
	 public  JSONObject	funGetGRNDataforBill(JSONObject objCLData)
	 	{
		 	JSONObject objRetData = new JSONObject();
		 	JSONArray objArr = new JSONArray();
		 	clsDatabaseConnection objDb=new clsDatabaseConnection();
	 		Connection webbookCon=null;
	 		Statement st = null;
	 		try
			{
	 		String clientCode = objCLData.getString("clientCode");
	 		String strSuppCode = objCLData.getString("strSuppCode");
		 	String   sql=" select a.strGRNCode,a.strBillNo,a.dblTotal,date(a.dtGRNDate),date(a.dtBillDate),date(a.dtDueDate) from dbwebmms.tblgrnhd a "
		 			+ " where  a.strGRNCode not in(select b.strGRNCode from  dbwebbooks.tblscbillgrndtl b ) and a.strClientCode='"+clientCode+"' and a.strSuppCode='"+strSuppCode+"' ";
		 	
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();	
				ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	JSONObject obj = new JSONObject();
			    	obj.put("strGRNCode",rs.getString(1));
			    	obj.put("strBillNo",rs.getString(2));
			    	obj.put("dblTotal",rs.getDouble(3));
			    	obj.put("dtGRNDate",rs.getString(4));
			    	obj.put("dtBillDate",rs.getString(5));
			    	obj.put("dtDueDate",rs.getString(6));
			    	objArr.put(obj);
				}
				
			    objRetData.put("unBilledGRN",objArr);
			}
		 	catch(Exception ex)
		 	{
		 		ex.printStackTrace();
		 	}
		 	finally
		 	{
		 		return objRetData;
		 	}
	 	}
	 
	 
	 @GET
	 @Path("/funGetWebBooksSundryDebtorData")
	 @Produces(MediaType.APPLICATION_JSON)
	 public JSONObject funGetWebBooksSundryDetorData(@QueryParam("strDocCode") String strDocCode, @QueryParam("clientCode") String clientCode)
	    {
			JSONObject jObjData = new JSONObject();
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection webbookCon=null;
	        Statement st = null;
			String sql="";
			try
			{
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();	
			    sql = " select a.strDebtorCode , a.strFirstName "
			    		+ " from tblsundaryDebtormaster a where  a.strDebtorCode = '"+strDocCode+"' and a.strClientCode = '"+clientCode+"'  " ;	
			
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	jObjData.put("strDetorCode",rs.getString(1));
			    	jObjData.put("strFirstName",rs.getString(2));
				}
			    
			    
			}catch(Exception ex)
			{
				try {
					webbookCon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ex.printStackTrace();
			}finally
			{
				return jObjData;
			}
			
	    }
	 
	 	@POST
		@Path("/funGenrateJVforInvoice")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funGenrateJVforInvoice(JSONObject objCLData)
		{
	 		JSONObject jObj =new JSONObject();
	 		try
	 		{
			String strJVCode = funInsertJVFromOtherTransections(objCLData);	
			
			jObj.put("strJVCode", strJVCode);
	 		}
	 		catch(Exception ex)
	 		{
	 			ex.printStackTrace();
	 		}
	 		finally
	 		{
	 			return Response.status(201).entity(jObj).build();
	 		}
		}
	 
	 	@POST
		@Path("/funGenrateSundryDebtor")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funGenrateSundryDebtor(JSONObject objCLData)
		{
	 		JSONObject jObj =new JSONObject();
	 		try
	 		{
			String strDebtorCode = funCreateUpdateSundryDebtor(objCLData);	
			
			jObj.put("strDebtorCode", strDebtorCode);
	 		}
	 		catch(Exception ex)
	 		{
	 			ex.printStackTrace();
	 		}
	 		finally
	 		{
	 			return Response.status(201).entity(jObj).build();
	 		}
		 
		}
	 	
	 	private  String funCreateUpdateSundryDebtor(JSONObject objCLData)
	 	{
	 		
	 		clsDatabaseConnection objDb=new clsDatabaseConnection();
	 		Connection webbookCon=null;
	 		Statement st = null;
			String sql="";
			String strCrDrAcCode="";
			String lastNo = "";
			try
			{
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();	
				
				 sql="Select strDebtorCode,intGId from tblsundarydebtormaster where strDebtorCode='"+objCLData.getString("debtorCode")+"' and strClientCode='"+objCLData.getString("ClientCode")+"' and strPropertyCode = '"+objCLData.getString("PropertyCode")+"' ";
				 ResultSet rs=st.executeQuery(sql);
				    while(rs.next())
					{
				    	strCrDrAcCode = rs.getString(1);
				    	lastNo = rs.getString(2);
					}
				if(strCrDrAcCode.length()>0)
				{
					String deleteSql="delete from tblsundarydebtormaster  "
							+ " where strDebtorCode= '"+strCrDrAcCode+"' and strClientCode='"+objCLData.getString("ClientCode")+"' and strPropertyCode = '"+objCLData.getString("PropertyCode")+"' ";
					int i=st.executeUpdate(deleteSql);
				}else
				{
					 
					String 	strDocCodeAndLastNo=funGenrateNewMasterCode("strDebtorCode" ,"tblsundarydebtormaster",objCLData.getString("ClientCode"));
					strCrDrAcCode=strDocCodeAndLastNo.split("#")[0];
					lastNo = strDocCodeAndLastNo.split("#")[1];
				}
				
				String sqlInsert= " INSERT INTO `tblsundarydebtormaster` (`intGId`, `strDebtorCode`, `strPrefix`, `strFirstName`, `strMiddleName`, "
						+ " `strLastName`,`strCategoryCode`,  `strAddressLine1`, `strAddressLine2`, `strAddressLine3`, `strCity`, "
						+ " `longZipCode`, `strTelNo1`, `strTelNo2`, `strFax`, "
						+ " `strArea`, `strEmail`,`strAccountNo`,`strHolderName`,`strMICRNo`,`longMobileNo`,`dteStartDate`,`strDebtorFullName`, "
						+ "  `strClientCode`,`strPropertyCode`, `strUserCreated`,`strUserEdited`, `dteDateCreated`, `dteDateEdited`, "
						+ "  `strBlocked`) VALUES  "
					
						+ "	('"+lastNo+"','"+strCrDrAcCode+"', 'Mr.', '"+objCLData.getString("FirstName")+"', '"+objCLData.getString("MiddleName")+"',"
						+ " '"+objCLData.getString("LastName")+"', '','"+objCLData.getString("AddressLine1")+"', '"+objCLData.getString("AddressLine2")+"', '"+objCLData.getString("AddressLine3")+"','"+objCLData.getString("City")+"', "
						+ " '', '"+objCLData.getString("LongZipCode")+"', '"+objCLData.getString("TelNo1")+"', '"+objCLData.getString("TelNo2")+"', '"+objCLData.getString("Fax")+"',"
						+ " '"+objCLData.getString("Area")+"','"+objCLData.getString("Email")+"',   '','','','"+objCLData.getString("DateCreated")+"','"+objCLData.getString("FirstName")+"', "
								+ " '"+objCLData.getString("ClientCode")+"', '"+objCLData.getString("PropertyCode")+"', '"+objCLData.getString("UserCreated")+"','"+objCLData.getString("UserEdited")+"', "
						+ " '"+objCLData.getString("DateCreated")+"', '"+objCLData.getString("DateEdited")+"','No') ";
			
				st.executeUpdate(sqlInsert);
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return strCrDrAcCode;
	 		
	 	}
	 	
	 	private String funGenrateNewMasterCode(String columnName,String tableName,String clientCode)
	 	{
	 		clsDatabaseConnection objDb=new clsDatabaseConnection();
	 		Connection webbookCon=null;
	 		Statement st = null;
			String sql="";
			long lastNo=0;
			String strDocNo="";
			try
			{
				webbookCon=objDb.funOpenWebbooksCon("mysql","master");
				st = webbookCon.createStatement();
				sql = "select ifnull(max("+columnName+"),0),count("+columnName+") from "+tableName+" where strClientCode='"+clientCode+"'";
				ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	lastNo=rs.getLong(2);
					lastNo++;
			    	strDocNo ="D"+String.format("%08d", lastNo);
			    	strDocNo=strDocNo+"#"+lastNo;
				}
			
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return strDocNo; 
			
		 }
	 	
	 	
	 	
	 
	 	@SuppressWarnings("finally")
		@GET 
		@Path("/funGetDebtorMaster")
		@Produces(MediaType.APPLICATION_JSON)
		public String funGetDebtorMaster(@QueryParam("ClientCode") String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection webbookCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
	        String response="";

	        
	        try {
		        	webbookCon=objDb.funOpenWebbooksCon("mysql","master");
		            st = webbookCon.createStatement();
		            String sql="";
		            
		            sql=" select strDebtorCode,strDebtorFullName from tblsundarydebtormaster "  
			            + " where strClientCode='" + clientCode + "' ";
			           
		            JSONArray arrObj=new JSONArray();
		            ResultSet rsGLDebt=st.executeQuery(sql);
	           
		            while (rsGLDebt.next()) 
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("DebtorCode",rsGLDebt.getString(1));
		            	obj.put("DebtorName",rsGLDebt.getString(2));
		            	arrObj.put(obj);            	 
		            }
	           
		            rsGLDebt.close();
			        jObj.put("DebtorDtl", arrObj);
			        st.close();
			        webbookCon.close();
		            
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        finally
		        {
		        	return jObj.toString();
		        }
		    }
	 	
	 	
	 	
	 	
	 	
	 	@GET
		 @Path("/funGetPOSDataAcc")
		 @Produces(MediaType.APPLICATION_JSON)
		 public JSONObject funGetWebBooksTaxAccData(@QueryParam("strDocCode") String strDocCode, @QueryParam("clientCode") String clientCode,@QueryParam("propertyCode") String propertyCode)
		    {
				JSONObject jObjData = new JSONObject();
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection webbookCon=null;
		        Statement st = null;
				String sql="";
				try
				{
					webbookCon=objDb.funOpenWebbooksCon("mysql","master");
					st = webbookCon.createStatement();	
				    sql = " select a.strAccountCode , a.strAccountName "
				    		+ " from tblacmaster a where  a.strAccountCode = '"+strDocCode+"' and a.strClientCode = '"+clientCode+"' and  a.strPropertyCode='"+propertyCode+"'  " ;	
				
				    ResultSet rs=st.executeQuery(sql);
				    while(rs.next())
					{
				    	jObjData.put("strAccountCode",rs.getString(1));
				    	jObjData.put("strAccountName",rs.getString(2));
					}
				    
				    
				}catch(Exception ex)
				{
					try {
						webbookCon.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ex.printStackTrace();
				}finally
				{
					return jObjData;
				}
				
		    }
	
	 	
	 	
	 	
	 	private void funProfitAndLossReport(final String senderId, final String senderPasswrd, String recieveId, String date, String clientCode)
	 	  {
	 	    clsDatabaseConnection objDb = new clsDatabaseConnection();
	 	    Connection cmsCon = null;
	 	    
	 	    try
	 	    {
	 	      String posItemCode = "";String posItemName = "";String posCode = "";String billDate = "";String wsItemCode = "";String locCode = "";String posName = "";
	 	      String retValue = "";
	 	      String strStkCode = "";
	 	      long lastNo = 0L;
	 	      double totAmt = 0.0D;double rate = 0.0D;double itemTotAmt = 0.0D;double itemPer = 0.0D;double itemPerAmt = 0.0D;
	 	      Integer quantity = new Integer(0);
	 	      cmsCon = objDb.funOpenMMSCon("mysql", "transaction");
	 	      Statement st = cmsCon.createStatement();
	 	      

	 	      String companyName = "";
	 	      String userCode = "";
	 	      String propertyCode = "";
	 	      

	 	      String strAdd1 = "";
	 	      String strAdd2 = "";
	 	      String strcity = "";
	 	      String strState = "";
	 	      String strCountry = "";
	 	      String strPin = "";
	 	      st = cmsCon.createStatement();
	 	      String sql = " select a.strAdd1,a.strAdd2,a.strCity,a.strState,a.strCountry,a.strPin from tblpropertysetup a  where a.strPropertyCode='01' and  a.strClientCode='" + 
	 	        clientCode + "'  ";
	 	      ResultSet rsProperty = st.executeQuery(sql);
	 	      while (rsProperty.next())
	 	      {
	 	        strAdd1 = rsProperty.getString(1);
	 	        strAdd2 = rsProperty.getString(2);
	 	        strcity = rsProperty.getString(3);
	 	        strState = rsProperty.getString(4);
	 	        strCountry = rsProperty.getString(5);
	 	        strPin = rsProperty.getString(6);
	 	      }
	 	      
	 	      String dteFromDate = date;
	 	      String dteToDate = date;
	 	      String imagePath = servletContext.getRealPath("/WEB-INF/images/company_Logo.png");
//	 	      String imagePath = "";
	 	      
	 	      List<clsProfitLossReportBean> dataListPaymnet = new ArrayList();
	 	      List<clsProfitLossReportBean> dataListRecipt = new ArrayList();
	 	      List<clsProfitLossReportBean> dataListExtraExpense = new ArrayList();
	 	      List<List<clsProfitLossReportBean>> list = new ArrayList();
	 	      

	 	      double conversionRate = 1.0D;
	 	      

	 	      String prevProdCode = "";
	 	      int cnt = -1;
	 	      
	 	      List<clsProfitLossReportBean> listProduct = new ArrayList();
	 	      JSONObject jObjJVData = new JSONObject();
		      jObjJVData.put("dteFrom", dteFromDate);
		      jObjJVData.put("dteTo", dteToDate);
		      jObjJVData.put("strClientCode", clientCode);
	 	      
	 	     JSONObject jObj =funGetPOSDataFromLivBill(jObjJVData);
	 	      Double dblTotalSale = Double.valueOf(Double.parseDouble(jObj.get("TotalSale").toString()));
		      Double dblTotalPurchase = Double.valueOf(Double.parseDouble(jObj.get("TotalPurchase").toString()));
		      clsProfitLossReportBean objPL = new clsProfitLossReportBean();
		     
		      objPL.setDblPurAmt(dblTotalPurchase.doubleValue());
		      objPL.setDblSaleAmt(dblTotalSale.doubleValue());
		      listProduct.add(objPL);
	 	      
	 	      
	 	      
	 	      
	 	      Map<String, clsProfitLossReportBean> hmSalesIncStmt = new HashMap();
	 	      funCalculateIncomeStmt("Expense", "tbljvhd", "tbljvdtl", dteFromDate, dteToDate, clientCode, hmSalesIncStmt, propertyCode);
	 	      

	 	      funCalculateIncomeStmt("Expense", "tblreceipthd", "tblreceiptdtl", dteFromDate, dteToDate, clientCode, hmSalesIncStmt, propertyCode);
	 	      

	 	      funCalculateIncomeStmt("Expense", "tblpaymenthd", "tblpaymentdtl", dteFromDate, dteToDate, clientCode, hmSalesIncStmt, propertyCode);
	 	      BigDecimal totalExpense = new BigDecimal(0);
	 	      for (Map.Entry<String, clsProfitLossReportBean> entry : hmSalesIncStmt.entrySet())
	 	      {


	 	        clsProfitLossReportBean objProfitLoss = new clsProfitLossReportBean();
	 	        objProfitLoss.setStrAccountName(((clsProfitLossReportBean)entry.getValue()).getStrAccountName());
	 	        objProfitLoss.setDblAmt(((clsProfitLossReportBean)entry.getValue()).getDblAmt());
	 	        totalExpense = totalExpense.add(((clsProfitLossReportBean)entry.getValue()).getDblAmt());
	 	        
	 	        dataListExtraExpense.add(objProfitLoss);
	 	      }
	 	      

	 	      BigDecimal purAmt = new BigDecimal(0);
	 	      BigDecimal dblRevenue = new BigDecimal(0);
	 	      
	 	     BigDecimal perCostOfGoodSold = new BigDecimal(0);
	 	     BigDecimal perTotalExp = new BigDecimal(0);
	 	     BigDecimal perGrossProfit = new BigDecimal(0);
	 	     BigDecimal netProfit = new BigDecimal(0);
	 	     BigDecimal perNetProfit = new BigDecimal(0);
	 	     BigDecimal grossProfit = new BigDecimal(0);
	 	    
	 	      for (clsProfitLossReportBean obj : listProduct)
	 	      {
	 	        purAmt = purAmt.add(BigDecimal.valueOf(obj.getDblPurAmt()));
	 	        dblRevenue = dblRevenue.add(BigDecimal.valueOf(obj.getDblSaleAmt()));
	 	      }
	 	     
	 	      

	 	     if (dblRevenue.compareTo(BigDecimal.ZERO) < 0){
	 	      grossProfit = dblRevenue.subtract(purAmt);
	 	      perCostOfGoodSold = purAmt.divide(dblRevenue, 2, RoundingMode.HALF_UP);
	 	      perCostOfGoodSold = perCostOfGoodSold.multiply(new BigDecimal(100));
	 	      
	 	      perTotalExp = totalExpense.divide(dblRevenue, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
	 	      perGrossProfit = grossProfit.divide(dblRevenue, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
	 	      netProfit = grossProfit.subtract(totalExpense);
	 	      perNetProfit = netProfit.divide(dblRevenue, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
	 	      }
	 	      if (perNetProfit.compareTo(BigDecimal.ZERO) < 0)
	 	      {
	 	        perNetProfit = perNetProfit.multiply(new BigDecimal(-1));
	 	      }
	 	      
	 	      HashMap hm = new HashMap();
	 	      hm.put("strCompanyName", companyName);
	 	      hm.put("strUserCode", userCode);
	 	      hm.put("strImagePath", imagePath);
	 	      hm.put("strAddr1", strAdd1);
	 	      hm.put("strAddr2", strAdd2);
	 	      hm.put("strCity", strcity);
	 	      hm.put("strState", strState);
	 	      hm.put("strCountry", strCountry);
	 	      hm.put("strPin", strPin);
	 	      hm.put("fromDate", dteFromDate);
	 	      hm.put("toDate", dteToDate);
	 	      hm.put("dataListExtraExpense", dataListExtraExpense);
	 	      hm.put("dataListRecipt", dataListRecipt);
	 	      hm.put("totalExpense", totalExpense);
	 	      
	 	      hm.put("dblGrossProfit", grossProfit);
	 	      hm.put("dblRevenue", dblRevenue);
	 	      hm.put("dblCostofGood", purAmt);
	 	      
	 	      hm.put("perCostOfGoodSold", perCostOfGoodSold);
	 	      hm.put("perTotalExp", perTotalExp);
	 	      hm.put("netProfit", netProfit);
	 	      hm.put("perNetProfit", perNetProfit);
	 	      hm.put("perGrossProfit", perGrossProfit);
	 	      
	 	     String strPL = "NET PROFIT";
		      if (netProfit.compareTo(BigDecimal.ZERO) < 0)
		      {
		        strPL = "LOSS";
		      }
		      hm.put("strPL", strPL);

	 	      list.add(dataListExtraExpense);
	 	      JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(list);
	 	      
	 	      JasperDesign jd = JRXmlLoader.load(servletContext.getResourceAsStream("/WEB-INF/billFormat/rptProfitLossReport.jrxml"));
	 	      JasperReport jr = JasperCompileManager.compileReport(jd);
	 	      
	 	      JasperPrint jp1 = JasperFillManager.fillReport(jr, hm, beanColDataSource);
	 	      

	 	      Properties props = new Properties();
	 	      props.put("mail.smtp.host", "smtp.gmail.com");
	 	      props.put("mail.smtp.socketFactory.port", "465");
	 	      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	 	      props.put("mail.smtp.auth", "true");
	 	      props.put("mail.smtp.port", "465");
	 	      
	 	     Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
		        {
		            protected PasswordAuthentication getPasswordAuthentication()
		            {
		                return new PasswordAuthentication(senderId, senderPasswrd);//change accordingly
		            }
		        });






	 	      MimeMessage message = new MimeMessage(session);
	 	      message.setFrom(new InternetAddress(senderId));
	 	      message.addRecipient(Message.RecipientType.TO, new InternetAddress(recieveId));
	 	      
	 	      message.setSubject("PL Report");
	 	      
	 	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	 	      JasperExportManager.exportReportToPdfStream(jp1, baos);
	 	      DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
	 	      
	 	      BodyPart messageBodyPart = new MimeBodyPart();
	 	      Multipart multipart = new MimeMultipart();
	 	      messageBodyPart.setDataHandler(new DataHandler(source));
	 	      multipart.addBodyPart(messageBodyPart);
	 	      message.setContent(multipart);
	 	      Transport.send(message);



	 	    }
	 	    catch (Exception e)
	 	    {
	 	      e.printStackTrace();
	 	      try
	 	      {
	 	        cmsCon.close();
	 	      }
	 	      catch (SQLException e1) {
	 	        e1.printStackTrace();
	 	      }
	 	    }
	 	    finally
	 	    {
	 	      try
	 	      {
	 	        cmsCon.close();
	 	      }
	 	      catch (SQLException e) {
	 	        e.printStackTrace();
	 	      }
	 	    }
	 	  }
	 	  




	 	  private void funCalculateIncomeStmt(String catType, String hdTableName, String dtlTableName, String fromDate, String toDate, String clientCode, Map<String, clsProfitLossReportBean> hmIncomeStatement, String propCode)
	 	    throws Exception
	 	  {
	 	    StringBuilder sbOp = new StringBuilder();
	 	    clsDatabaseConnection objDb = new clsDatabaseConnection();
	 	    Connection cmsCon = null;
	 	    cmsCon = objDb.funOpenWebbooksCon("mysql", "transaction");
	 	    Statement st = cmsCon.createStatement();
	 	    
	 	   /* String sbSql = " select a.strType,b.strGroupCode,b.strGroupName,ifnull(d.strCrDr,''),if((c.strVouchNo is null),0,ifnull(sum(d.dblDrAmt),0)),a.strAccountName,a.strAccountCode,if((c.strVouchNo is null),0,ifnull(sum(d.dblCrAmt),0)) from  tblacgroupmaster b, tblsubgroupmaster s,tblacmaster a,   " + 
	 	      dtlTableName + " d ,  " + 
	 	      "  " + hdTableName + "  c  " + 
	 	      "where a.strSubGroupCode=s.strSubGroupCode "
	 	      + " and s.strGroupCode=b.strGroupCode " + 
	 	      "and b.strCategory like'%EXPENSES' and  a.strAccountCode=d.strAccountCode and c.strVouchNo=d.strVouchNo   and date(c.dteVouchDate) between '" + fromDate + "' and '" + toDate + "' and c.strClientCode='" + clientCode + "' and a.strPropertyCode='" + propCode + "'  " + 
	 	      "and a.strType='GL Code' " + 
	 	      "group by a.strAccountCode  ";*/
	 	   StringBuilder sbSql=new StringBuilder();
			sbSql.setLength(0);
	 		sbSql.append("select a.strType,b.strGroupCode,b.strGroupName,ifnull(d.strCrDr,''),if((c.strVouchNo is null),0,ifnull(sum(d.dblDrAmt),0)),a.strAccountName,a.strAccountCode,if((c.strVouchNo is null),0,ifnull(sum(d.dblCrAmt),0)) "
					+ "from  tblacgroupmaster b,tblsubgroupmaster s,tblacmaster a "
					+" left outer join "+dtlTableName+" d on  a.strAccountCode=d.strAccountCode " 
					+" left outer join "+hdTableName+"  c on c.strVouchNo=d.strVouchNo   and date(c.dteVouchDate) between '" + fromDate + "' and '" + toDate + "' and c.strClientCode='"+clientCode+"' and a.strPropertyCode='"+propCode+"'  "
					+ "where a.strSubGroupCode=s.strSubGroupCode "
					+ " and b.strGroupCode=s.strGroupCode "
					+ "and b.strCategory like'%EXPENSES' "
					+ "and a.strType='GL Code' "
					+ "group by a.strAccountCode  ");
	 	    
	 	    ResultSet rsTrans = st.executeQuery(sbSql.toString());
	 	    
	 	    if (rsTrans.next())
	 	    {
	 	      while (rsTrans.next())
	 	      {


	 	        BigDecimal totalAmount = BigDecimal.valueOf(rsTrans.getDouble(5)).subtract(BigDecimal.valueOf(rsTrans.getDouble(8)));
	 	        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
	 	          String accountCode = rsTrans.getString(7);
	 	          clsProfitLossReportBean objBean = new clsProfitLossReportBean();
	 	          
	 	          if (hmIncomeStatement.containsKey(accountCode))
	 	          {
	 	            objBean = (clsProfitLossReportBean)hmIncomeStatement.get(accountCode);
	 	            objBean.setDblAmt(objBean.getDblAmt().add(totalAmount));

	 	          }
	 	          else
	 	          {
	 	            objBean.setStrAccountName(rsTrans.getString(6));
	 	            objBean.setDblAmt(totalAmount);
	 	          }
	 	          
	 	          hmIncomeStatement.put(accountCode, objBean);
	 	        }
	 	      }
	 	    }
	 	  }
	 	  

	 	  @POST
	 	  @Path("/funGetPOSData")
	 	  @Produces({"application/json"})
	 	  public JSONObject funGetPOSData(JSONObject jObjfillter)
	 	  {
	 	    clsDatabaseConnection objDb = new clsDatabaseConnection();
	 	    Connection POSCon = null;
	 	    Statement st = null;
	 	    JSONObject jObj = new JSONObject();
	 	    String response = "";
	 	    
	 	    try
	 	    {
	 	      String frmDate = jObjfillter.get("dteFrom").toString();
	 	      String toDate = jObjfillter.get("dteTo").toString();
	 	      String clientCode = jObjfillter.get("strClientCode").toString();
	 	      POSCon = objDb.funOpenPOSCon("mysql", "master");
	 	      st = POSCon.createStatement();
	 	      String sql = "";
	 	      
	 	      sql = " select   ifnull(sum(a.dblAmount),0),ifnull(sum(d.dblPurchaseRate),0) from tblqbilldtl a,tblqbillhd b,tblposmaster c,tblitemmaster d   where a.strBillNo=b.strBillNo   AND DATE(a.dteBillDate)=DATE(b.dteBillDate)  and b.strPOSCode=c.strPosCode   and a.strClientCode=b.strClientCode and a.strItemCode=d.strItemCode  "
	 	      	  + " and date(b.dteBillDate)  between '" + frmDate + "' and '" + toDate + "' ";
	 	      


	 	      JSONArray arrObj = new JSONArray();
	 	      ResultSet rsPOSData = st.executeQuery(sql);
	 	      double saleAmt = 0.0D;
	 	      double purAmt = 0.0D;
	 	      while (rsPOSData.next())
	 	      {
	 	        saleAmt += Double.parseDouble(rsPOSData.getString(1));
	 	        purAmt += Double.parseDouble(rsPOSData.getString(2));
	 	      }
	 	      
	 	      rsPOSData.close();
	 	      String sqlMod = " select  ifnull(sum(a.dblAmount),0),ifnull(sum(d.dblPurchaseRate),0) from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d  where a.strBillNo=b.strBillNo   AND DATE(a.dteBillDate)=DATE(b.dteBillDate)  and b.strPOSCode=c.strPosCode   and a.strClientCode=b.strClientCode  and left(a.strItemCode,7)=d.strItemCode  "
	 	      				+ " AND a.dblamount>0   and date(b.dteBillDate)    between '" + frmDate + "' and '" + toDate + "' ";
	 	      
	 	      ResultSet rsPOSModData = st.executeQuery(sqlMod);
	 	      while (rsPOSModData.next())
	 	      {
	 	        saleAmt += Double.parseDouble(rsPOSModData.getString(1));
	 	        purAmt += Double.parseDouble(rsPOSModData.getString(2));
	 	      }
	 	      
	 	      rsPOSModData.close();
	 	      
	 	      jObj.put("TotalSale", Double.toString(saleAmt));
	 	      jObj.put("TotalPurchase", Double.toString(purAmt));
	 	      rsPOSData.close();
	 	      
	 	      st.close();
	 	      POSCon.close();
	 	    }
	 	    catch (Exception e) {
	 	      e.printStackTrace();
	 	    }
	 	    finally
	 	    {
	 	      return jObj;
	 	    }
	 	  }
	 	  
	 	  
	 	 
	 	  public JSONObject funGetPOSDataFromLivBill(JSONObject jObjfillter)
	 	  {
	 	    clsDatabaseConnection objDb = new clsDatabaseConnection();
	 	    Connection POSCon = null;
	 	    Statement st = null;
	 	    JSONObject jObj = new JSONObject();
	 	    String response = "";
	 	    
	 	    try
	 	    {
	 	      String frmDate = jObjfillter.get("dteFrom").toString();
	 	      String toDate = jObjfillter.get("dteTo").toString();
	 	      String clientCode = jObjfillter.get("strClientCode").toString();
	 	      POSCon = objDb.funOpenPOSCon("mysql", "master");
	 	      st = POSCon.createStatement();
	 	      String sql = "";
	 	      
	 	      sql = " select a.dblAmount,d.dblPurchaseRate from tblbilldtl a,tblbillhd b,tblposmaster c,tblitemmaster d   where a.strBillNo=b.strBillNo   AND DATE(a.dteBillDate)=DATE(b.dteBillDate)  and b.strPOSCode=c.strPosCode   and a.strClientCode=b.strClientCode and a.strItemCode=d.strItemCode  "
	 	      	  + " and date(b.dteBillDate)  between '" + frmDate + "' and '" + toDate + "' ";
	 	      


	 	      JSONArray arrObj = new JSONArray();
	 	      ResultSet rsPOSData = st.executeQuery(sql);
	 	      double saleAmt = 0.0D;
	 	      double purAmt = 0.0D;
	 	      while (rsPOSData.next())
	 	      {
	 	        saleAmt += Double.parseDouble(rsPOSData.getString(1));
	 	        purAmt += Double.parseDouble(rsPOSData.getString(2));
	 	      }
	 	      
	 	      rsPOSData.close();
	 	      String sqlMod = " select   a.dblAmount,d.dblPurchaseRate from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c,tblitemmaster d  where a.strBillNo=b.strBillNo   AND DATE(a.dteBillDate)=DATE(b.dteBillDate)  and b.strPOSCode=c.strPosCode   and a.strClientCode=b.strClientCode  and left(a.strItemCode,7)=d.strItemCode  "
	 	      				+ " AND a.dblamount>0   and date(b.dteBillDate)    between '" + frmDate + "' and '" + toDate + "' ";
	 	      
	 	      ResultSet rsPOSModData = st.executeQuery(sql);
	 	      while (rsPOSModData.next())
	 	      {
	 	        saleAmt += Double.parseDouble(rsPOSModData.getString(1));
	 	        purAmt += Double.parseDouble(rsPOSModData.getString(2));
	 	      }
	 	      
	 	      rsPOSModData.close();
	 	      
	 	      jObj.put("TotalSale", Double.toString(saleAmt));
	 	      jObj.put("TotalPurchase", Double.toString(purAmt));
	 	      rsPOSData.close();
	 	      
	 	      st.close();
	 	      POSCon.close();
	 	    }
	 	    catch (Exception e) {
	 	      e.printStackTrace();
	 	    }
	 	    finally
	 	    {
	 	      return jObj;
	 	    }
	 	  }
	}
