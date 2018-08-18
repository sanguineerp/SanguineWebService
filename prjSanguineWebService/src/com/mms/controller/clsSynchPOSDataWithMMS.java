package com.mms.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Path("/MMSIntegration")
public class clsSynchPOSDataWithMMS
{
    
    public clsSynchPOSDataWithMMS() throws Exception
    {
	if (clsDatabaseConnection.DBMMSCONNECTION == null)
	{
	    clsDatabaseConnection objDb = new clsDatabaseConnection();
	    clsDatabaseConnection.DBMMSCONNECTION = objDb.funOpenMMSCon("mysql", "transaction");
	}
	else if (clsDatabaseConnection.DBMMSCONNECTION.isClosed())
	{
	    clsDatabaseConnection objDb = new clsDatabaseConnection();
	    clsDatabaseConnection.DBMMSCONNECTION = objDb.funOpenMMSCon("mysql", "transaction");
	}
    }
    
    static
    {
	try
	{
	    clsDatabaseConnection objDb = new clsDatabaseConnection();
	    clsDatabaseConnection.DBMMSCONNECTION = objDb.funOpenMMSCon("mysql", "transaction");
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
        
    
    
    
    @SuppressWarnings("finally")
    @GET
    @Path("/funInvokeMMSWebService")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funGetBillInfo()
    {
	String response = "false";
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	try
	{
	    cmsCon = clsDatabaseConnection.DBMMSCONNECTION;
	    response = "true";
	}
	catch (Exception e)
	{
	    response = "false";
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    if (null != cmsCon)
	    {
		try
		{
//		    cmsCon.close();
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	    }
	    return Response.status(201).entity(response).build();
	}
	
    }
    
    @POST
    @Path("/funPostPOSSaleData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funPostPOSSalesData(JSONObject objCLData)
    {
	String response = "false";
	if (funInsertPOSSalesData(objCLData) > 0)
	{
	    response = "true";
	}
	return Response.status(201).entity(response).build();
    }
    
    @SuppressWarnings("finally")
    private int funInsertPOSSalesData(JSONObject objCLData)
    {
	int res = 0;
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	
	try
	{
	    cmsCon = clsDatabaseConnection.DBMMSCONNECTION;
	    st = cmsCon.createStatement();
	    
	    String sql_insertPOSSales = "insert into tblpossalesdtl " + "(strPOSItemCode,strPOSItemName,dblQuantity,dblRate,strPOSCode,dteBillDate" + ",strClientCode,strWSItemCode,strSACode) " + "values ";
	    JSONArray mJsonArray = (JSONArray) objCLData.get("MemberPOSSalesInfo");
	    String sql = "";
	    boolean flgData = false;
	    JSONObject mJsonObject = new JSONObject();
	    for (int i = 0; i < mJsonArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonArray.get(i);
		String posItemCode = mJsonObject.get("posItemCode").toString();
		String posItemName = mJsonObject.get("posItemName").toString();
		Integer quantity = (int) Double.parseDouble(mJsonObject.get("quantity").toString());
		double rate = Double.parseDouble(mJsonObject.get("rate").toString());
		String posCode = mJsonObject.get("posCode").toString();
		String billDate = mJsonObject.get("billDate").toString();
		String clientCode = mJsonObject.get("clientCode").toString();
		
		sql += ",('" + posItemCode + "','" + posItemName + "','" + quantity + "','" + rate + "','" + posCode + "'" + ",'" + billDate + "','" + clientCode + "','','')";
		flgData = true;
	    }
	    if (flgData)
	    {
		sql = sql.substring(1, sql.length());
		sql_insertPOSSales += " " + sql;
		res = st.executeUpdate(sql_insertPOSSales);
	    }
	    else
	    {
		res = 1;
	    }
	}
	catch (Exception e)
	{
	    res = 0;
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != cmsCon)
//		{
//		    cmsCon.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return res;
	}
    }
    
    @GET
    @Path("/funGetProductMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public String funGetProductData(@QueryParam("ClientCode") String clientCode)
    {
	return funGetProductDetails(clientCode);
    }
    
    @SuppressWarnings("finally")
    private String funGetProductDetails(String clientCode)
    {
	JSONObject jObjProduct = new JSONObject();
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	
	try
	{
	    cmsCon = clsDatabaseConnection.DBMMSCONNECTION;
	    st = cmsCon.createStatement();
	    String sql = " select a.strProdCode,a.strProdName from tblproductmaster a " + " where a.strClientCode='" + clientCode + "' ";
	    JSONArray arrObjProduct = new JSONArray();
	    ResultSet rsProduct = st.executeQuery(sql);
	    while (rsProduct.next())
	    {
		JSONObject objProduct = new JSONObject();
		objProduct.put("ProdcutCode", rsProduct.getString(1));
		objProduct.put("ProdcutName", rsProduct.getString(2));
		arrObjProduct.put(objProduct);
	    }
	    rsProduct.close();
	    jObjProduct.put("ProductDtls", arrObjProduct);
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != cmsCon)
//		{
//		    cmsCon.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return jObjProduct.toString();
	}
    }
    
    @GET
    @Path("/funGetLocationMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public String funGetLocationData(@QueryParam("ClientCode") String clientCode)
    {
	return funGetLocationDetails(clientCode);
    }
    
    @SuppressWarnings("finally")
    private String funGetLocationDetails(String clientCode)
    {
	JSONObject jObjLocation = new JSONObject();
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	
	try
	{
	    cmsCon = clsDatabaseConnection.DBMMSCONNECTION;
	    st = cmsCon.createStatement();
	    String sql = "select a.strLocCode,a.strLocName from tbllocationmaster a " + " where a.strClientCode='" + clientCode + "' ";
	    // System.out.println(sql);
	    JSONArray arrObjLocation = new JSONArray();
	    ResultSet rsLocation = st.executeQuery(sql);
	    while (rsLocation.next())
	    {
		JSONObject objLocation = new JSONObject();
		objLocation.put("LocationCode", rsLocation.getString(1));
		objLocation.put("LocationName", rsLocation.getString(2));
		arrObjLocation.put(objLocation);
	    }
	    rsLocation.close();
	    jObjLocation.put("LocationDtls", arrObjLocation);
	    
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != cmsCon)
//		{
//		    cmsCon.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return jObjLocation.toString();
	}
    }
    
    @POST
    @Path("/funPlaceOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funPlaceOrder(JSONObject objSalesData)
    {
	String response = "";
	response = funPlaceSalesOrder(objSalesData);
	return Response.status(201).entity(response).build();
    }
    
    private String funPlaceSalesOrder(JSONObject objSalesData)
    {
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webmms = null;
	Statement st = null;
	String res = "";
	
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    String sql = "", existOrderCode = "", SOCode = "";
	    JSONArray mJsonArray = (JSONArray) objSalesData.get("OrderData");
	    JSONArray mJsonArraySOChar = (JSONArray) objSalesData.get("OrderCharData");
	    String locCode = objSalesData.getString("LocCode");
	    String clientCode = objSalesData.getString("WSClientCode");
	    String fullFillmentDate = objSalesData.getString("fullFillmentDate");
	    String SOOrderDate = objSalesData.getString("SODate");
	    String orderType = objSalesData.getString("OrderType");
	    // String existSOCode=objSalesData.getString("SOCode");
	    existOrderCode = objSalesData.getString("OrderCode");
	    String POSClientCode = objSalesData.getString("ClientCode");
	    
	    String custCode = "";
	    int index = 0;
	    sql = "select strPCode from tblpartymaster " + " where strManualCode='" + POSClientCode + "' and strPType='cust'";
	    ResultSet rsCust = st.executeQuery(sql);
	    if (rsCust.next())
	    {
		custCode = rsCust.getString(1);
	    }
	    rsCust.close();
	    
	    double totalAmt = 0;
	    SOCode = funSalesOrderCode(clientCode, locCode);
	    index = Integer.parseInt(SOCode.split(",")[1]);
	    SOCode = SOCode.split(",")[0];
	    
	    sql = "delete from tblsalesorderhd where strSOCode='" + SOCode + "'";
	    st.execute(sql);
	    
	    sql = "delete from tblsalesorderdtl where strSOCode='" + SOCode + "'";
	    st.execute(sql);
	    
	    sql = "delete from tblsaleschar where strSOCode='" + SOCode + "'";
	    st.execute(sql);
	    sql = "";
	    
	    clsUtilityFunctions objUtility = new clsUtilityFunctions();
	    String date = objUtility.funGetCurrentDateTime("yyyy-MM-dd");
	    String narration = "Sales Order From POS";
	    
	    String sqlInsertSODtl = "insert into tblsalesorderdtl " + "(strSOCode,strProdCode,dblQty,dblWeight,strRemarks,intindex,strClientCode,dblAcceptQty) " + "values ";
	    JSONObject mJsonObject = new JSONObject();
	    for (int i = 0; i < mJsonArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonArray.get(i);
		// System.out.println(mJsonObject);
		// String remarks="Stock="+mJsonObject.getString("StockQty");
		
		String remarks = "";
		if (orderType.equals("Normal Order"))
		{
		    remarks = "Stock=" + mJsonObject.getString("StockQty");
		}
		else
		{
		    remarks = mJsonObject.getString("AdvOrderNo");
		}
		
		if (i == 0)
		{
		    sqlInsertSODtl += "('" + SOCode + "','" + mJsonObject.getString("ProductCode") + "'" + ",'" + mJsonObject.getString("OrderQty") + "','" + mJsonObject.getString("Weight") + "','" + remarks + "'," + index + "" + ",'" + clientCode + "','" + mJsonObject.getString("OrderQty") + "')";
		}
		else
		{
		    sqlInsertSODtl += ",('" + SOCode + "','" + mJsonObject.getString("ProductCode") + "','" + mJsonObject.getString("OrderQty") + "'" + ",'" + mJsonObject.getString("Weight") + "','" + remarks + "'," + index + ",'" + clientCode + "','" + mJsonObject.getString("OrderQty") + "')";
		}
		totalAmt += Double.parseDouble(mJsonObject.getString("OrderQty"));
	    }
	    // System.out.println("SODtl Data:="+sqlInsertSODtl);
	    st.executeUpdate(sqlInsertSODtl);
	    
	    if (mJsonArraySOChar.length() > 0)
	    {
		Set setAdvOrderDtl = new HashSet();
		String sqlInsertSOCharDtl = "insert into tblsaleschar " + "(strSOCode,strProdCode,strCharCode,strCharValue,strAdvOrderNo) values ";
		
		for (int i = 0; i < mJsonArraySOChar.length(); i++)
		{
		    JSONObject mJsonCharObject = (JSONObject) mJsonArraySOChar.get(i);
		    String productCode = mJsonCharObject.getString("ProductCode");
		    String advanceOrderNo = mJsonCharObject.getString("AdvOrderNo");
		    String[] characterstics = mJsonCharObject.getString("Charcterstics").split(":");
		    
		    String data = productCode + "!" + advanceOrderNo + "!" + characterstics[0];
		    if (setAdvOrderDtl.add(data))
		    {
			if (i == 0)
			{
			    sqlInsertSOCharDtl += "('" + SOCode + "','" + productCode + "','" + characterstics[0] + "','" + characterstics[1] + "','" + advanceOrderNo + "')";
			}
			else
			{
			    sqlInsertSOCharDtl += ",('" + SOCode + "','" + productCode + "','" + characterstics[0] + "','" + characterstics[1] + "','" + advanceOrderNo + "')";
			}
		    }
		}
		// System.out.println("SOCharDtl Data:="+sqlInsertSOCharDtl);
		st.executeUpdate(sqlInsertSOCharDtl);
	    }
	    
	    String sqlInsertSOHd = "insert into tblsalesorderhd (strSOCode,intid,dteSODate,strCustCode" + ",strStatus,strLocCode,strPayMode,dblTotal,strUserCreated,strUserModified,dteDateCreated,dteLastModified" + ",strNarration,strCurrency,strAgainst,intwarmonth,strClientCode,dteFulmtDate,strSettlementCode) " + "values" + "('" + SOCode + "',0,'" + SOOrderDate + "','" + custCode + "','" + orderType + "','" + locCode + "','Cash','" + totalAmt + "'" + ",'SUPER','SUPER','" + date + "','" + date + "','" + narration + "','RS','Direct',0,'" + clientCode + "','" + fullFillmentDate + "','')";
	    st.executeUpdate(sqlInsertSOHd);
	    if (existOrderCode.isEmpty())
	    {
		existOrderCode = "New";
	    }
	    res = SOCode + "#" + fullFillmentDate + "#" + existOrderCode;
	    // System.out.println("res:" +res);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    new clsUtilityFunctions().funWriteErrorLog(e);
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webmms)
//		{
//		    webmms.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return res;
	}
    }
    
    private String funSalesOrderCode(String clientCode, String locCode)
    {
	String propCode = "01";
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	clsUtilityFunctions objUtility = new clsUtilityFunctions();
	Connection webmms = null;
	Statement st = null;
	ResultSet rs = null;
	String startDate = "";
	String salesOrderCode = "";
	String retValue = "";
	String sql = "";
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    sql = "select a.strPropertyCode from tbllocationmaster a " + " where a.strLocCode='" + locCode + "' and a.strClientCode='" + clientCode + "'";
	    // System.out.println("sql:"+sql);
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		propCode = rs.getString(1);
	    }
	    rs.close();
	    
	    sql = " select a.dtStart from tblcompanymaster a  " + " where a.strClientCode='" + clientCode + "' ";
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		startDate = rs.getString(1);
	    }
	    rs.close();
	    
	    long lastNo = funGetLastNo("tblsalesorderhd", "SOCode", "strSOCode", clientCode);
	    // System.out.println("lastNo:"+lastNo);
	    String year = startDate.split("-")[0];
	    // System.out.println("propCode:="+propCode+"year:="+year);
	    String cd = objUtility.funGetTransactionCode("SO", propCode, year);
	    salesOrderCode = cd + String.format("%06d", lastNo);
	    
	    retValue = salesOrderCode + "," + lastNo;
	    
	}
	catch (Exception ex)
	{
	    new clsUtilityFunctions().funWriteErrorLog(ex);
	    ex.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webmms)
//		{
//		    webmms.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return retValue;
	}
    }
    
    @POST
    @Path("/funInsertSalesReturn")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funInsertSalesReturn(JSONObject objSalesData)
    {
	String response = "";
	response = funInsertSalesReturnEntry(objSalesData);
	return Response.status(201).entity(response).build();
    }
    
    private String funInsertSalesReturnEntry(JSONObject objSalesReturnData)
    {
	String SRCode = "";
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webmms = null;
	Statement st = null;
	Statement st1 = null;
	
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    st1 = webmms.createStatement();
	    String sql = "";
	    JSONArray mJsonArray = (JSONArray) objSalesReturnData.get("SaleReturnData");
	    String locCode = objSalesReturnData.getString("LocCode");
	    String clientCode = objSalesReturnData.getString("ClientCode");
	    String saleReturnDate = objSalesReturnData.getString("SaleReturnDate");
	    String DCCode = objSalesReturnData.getString("InvoiceCode");
	    String custCode = "";
	    sql = "select strPCode from tblpartymaster " + " where strManualCode='" + clientCode + "' and strPType='cust'";
	    ResultSet rsCust = st.executeQuery(sql);
	    if (rsCust.next())
	    {
		custCode = rsCust.getString(1);
	    }
	    rsCust.close();
	    
	    double totalAmt = 0;
	    String narration = "Sales Return From POS";
	    clsUtilityFunctions objUtility = new clsUtilityFunctions();
	    String date = objUtility.funGetCurrentDateTime("yyyy-MM-dd");
	    SRCode = funSalesReturnCode(clientCode, locCode);
	    int index = Integer.parseInt(SRCode.split(",")[1]);
	    SRCode = SRCode.split(",")[0];
	    String sqlInsertSODtl = "insert into tblsalesreturndtl " + "(strSRCode,strProdCode,dblQty,dblPrice,dblWeight,strRemarks,strClientCode) " + "values ";
	    JSONObject mJsonObject = new JSONObject();
	    for (int i = 0; i < mJsonArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonArray.get(i);
		double price = 0, weight = 0;
		ResultSet rsProductInfo = st1.executeQuery("select dblCostRM,dblWeight " + " from tblproductmaster where strProdCode='" + mJsonObject.getString("ProductCode") + "'");
		if (rsProductInfo.next())
		{
		    price = rsProductInfo.getDouble(1);
		    weight = rsProductInfo.getDouble(2);
		}
		rsProductInfo.close();
		
		String remarks = mJsonObject.getString("Remarks");
		if (i == 0)
		{
		    sqlInsertSODtl += "('" + SRCode + "','" + mJsonObject.getString("ProductCode") + "'" + ",'" + mJsonObject.getString("ReturnQty") + "'," + price + "," + weight + "" + ",'" + remarks + "','" + clientCode + "')";
		    totalAmt += Double.parseDouble(mJsonObject.getString("ReturnQty")) * price;
		}
		else
		{
		    sqlInsertSODtl += ",('" + SRCode + "','" + mJsonObject.getString("ProductCode") + "'" + ",'" + mJsonObject.getString("ReturnQty") + "'," + price + "," + weight + "" + ",'" + remarks + "','" + clientCode + "')";
		    totalAmt += Double.parseDouble(mJsonObject.getString("ReturnQty")) * price;
		}
	    }
	    st.executeUpdate(sqlInsertSODtl);
	    
	    String sqlInsertSRHd = "insert into tblsalesreturnhd (strSRCode,dteSRDate,strAgainst" + ",strDCCode,strCustCode,strLocCode,dblTotalAmt,strUserCreated,strUserEdited" + ",dteDateCreated,dteDateEdited,strClientCode) " + "values" + "('" + SRCode + "','" + saleReturnDate + "','Invoice','" + DCCode + "','" + custCode + "','" + locCode + "'" + ",'" + totalAmt + "','SUPER','SUPER','" + date + "','" + date + "','" + clientCode + "')";
	    st.executeUpdate(sqlInsertSRHd);
	}
	catch (Exception e)
	{
	    SRCode = "";
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
		if (null != st1)
		{
		    st1.close();
		}
//		if (null != webmms)
//		{
//		    webmms.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return SRCode;
	}
    }
    
    private String funSalesReturnCode(String clientCode, String locCode)
    {
	String propCode = "01";
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	clsUtilityFunctions objUtility = new clsUtilityFunctions();
	Connection webmms = null;
	Statement st = null;
	ResultSet rs = null;
	String startDate = "";
	String salesReturnCode = "";
	String retValue = "";
	String sql = "";
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    sql = "select a.strPropertyCode from tbllocationmaster a " + " where a.strLocCode='" + locCode + "' and a.strClientCode='" + clientCode + "'";
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		propCode = rs.getString(1);
	    }
	    rs.close();
	    
	    sql = "select dtStart from tblcompanymaster where strClientCode='" + clientCode + "'";
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		startDate = rs.getString(1);
	    }
	    rs.close();
	    
	    long lastNo = funGetLastNo("tblsalesreturnhd", "SRCode", "strSRCode", clientCode);
	    // System.out.println("lastNo:"+lastNo);
	    String year = startDate.split("-")[0];
	    // String year = funGetSplitedDate(startDate)[2];
	    String cd = objUtility.funGetTransactionCode("SR", propCode, year);
	    salesReturnCode = cd + String.format("%06d", lastNo);
	    
	    retValue = salesReturnCode + "," + lastNo;
	}
	catch (Exception ex)
	{
	    new clsUtilityFunctions().funWriteErrorLog(ex);
	    ex.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webmms)
//		{
//		    webmms.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return retValue;
	}
    }
    
    @GET
    @Path("/funGetCharacteristicsMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public String funGetCharacteristicsData(@QueryParam("ClientCode") String clientCode)
    {
	return funGetProductCharacteristicsDetails(clientCode);
    }
    
    @SuppressWarnings("finally")
    private String funGetProductCharacteristicsDetails(String clientCode)
    {
	JSONObject jObjProductChar = new JSONObject();
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webmms = null;
	Statement st = null;
	
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    String sql = " select a.strCharCode,a.strCharName from tblcharacteristics a  " + " where a.strClientCode='" + clientCode + "' ";
	    // System.out.println(sql);
	    JSONArray arrObjProductChar = new JSONArray();
	    ResultSet rsProductChar = st.executeQuery(sql);
	    while (rsProductChar.next())
	    {
		JSONObject objProductChar = new JSONObject();
		objProductChar.put("ProdcutCharCode", rsProductChar.getString(1));
		objProductChar.put("ProdcutCharName", rsProductChar.getString(2));
		arrObjProductChar.put(objProductChar);
	    }
	    rsProductChar.close();
	    jObjProductChar.put("ProductCharDtls", arrObjProductChar);
	    
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webmms)
//		{
//		    webmms.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return jObjProductChar.toString();
	}
    }
    
    @SuppressWarnings("finally")
    public long funGetLastNo(String tableName, String masterName, String columnName, String clientCode)
    {
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webMMS = null;
	Statement st = null;
	long lastNo = 0;
	try
	{
	    webMMS = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webMMS.createStatement();
	    String code1 = "";
	    String sql = "select ifnull(max(" + columnName + "),0),count(" + columnName + ") from " + tableName + " where strClientCode='" + clientCode + "'";
	    // System.out.println("Max no:"+sql);
	    ResultSet rslastNo = st.executeQuery(sql);
	    while (rslastNo.next())
	    {
		code1 = rslastNo.getString(1);
	    }
	    rslastNo.close();
	    
	    if (code1.equals("0"))
	    {
		lastNo = 1;
	    }
	    else
	    {
		StringBuilder sb = new StringBuilder(code1);
		String code = sb.delete(0, 6).toString();
		
		for (int i = 0; i < code.length(); i++)
		{
		    if (code.charAt(i) != '0')
		    {
			code = code.substring(i, code.length());
			break;
		    }
		}
		
		lastNo = Long.parseLong(code);
		lastNo++;
	    }
	    
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webMMS)
//		{
//		    webMMS.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return lastNo;
	}
    }
    
    private void funInsertSOBOMEntry(String SOCode, String clientCode, JSONObject objSalesData) throws Exception
    {
	JSONArray mJsonArray = (JSONArray) objSalesData.get("OrderData");
	
	JSONObject mJsonObject = new JSONObject();
	for (int i = 0; i < mJsonArray.length(); i++)
	{
	    mJsonObject = (JSONObject) mJsonArray.get(i);
	    String productCode = mJsonObject.getString("ProductCode");
	    double orderQty = Double.parseDouble(mJsonObject.getString("OrderQty"));
	}
    }
    
    @GET
    @Path("/funPullOrder")
    @Produces(MediaType.APPLICATION_JSON)
    public String funPullOrder(@QueryParam("InvoiceCode") String InvoiceCode, @QueryParam("ClientCode") String clientCode)
    {
	String SOCode = "";
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webMMS = null;
	Statement st = null;
	JSONObject jObj = new JSONObject();
	
	String sql = "select b.strProdCode,b.dblQty,a.strInvCode,a.strSOCode " + " from tblinvoicehd a,tblinvoicedtl b " + " where a.strInvCode=b.strInvCode  " + " and a.strInvCode='" + InvoiceCode + "' ";
	
	JSONArray arrObj = new JSONArray();
	try
	{
	    webMMS = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webMMS.createStatement();
	    
	    ResultSet rsMasterData = st.executeQuery(sql);
	    while (rsMasterData.next())
	    {
		JSONObject obj = new JSONObject();
		
		obj.put("ProductCode", rsMasterData.getString(1));
		obj.put("Qty", rsMasterData.getString(2));
		SOCode = rsMasterData.getString(4);
		arrObj.put(obj);
	    }
	    rsMasterData.close();
	    
	    jObj.put("OrderRec", arrObj);
	    jObj.put("SOCode", SOCode);
	    
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webMMS)
//		{
//		    webMMS.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return jObj.toString();
	}
    }
    
    @GET
    @Path("/funGetInvoiceData")
    @Produces(MediaType.APPLICATION_JSON)
    public String funGetInvoiceDetails(@QueryParam("InvoiceDate") String invoiceDate, @QueryParam("ClientCode") String clientCode, @QueryParam("WSClientCode") String wsClientCode)
    {
	String SOCode = "";
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webMMS = null;
	Statement st = null;
	JSONObject jObj = new JSONObject();
	
	String sql = " select a.strInvCode,date(a.dteInvDate),a.strSOCode,c.strPName  " + " from tblinvoicehd a,tblinvoicedtl b,tblpartymaster c " + " where a.strInvCode=b.strInvCode and a.strCustCode=c.strPCode  " + " and a.strClientCode=c.strClientCode " + " and a.strClientCode='" + wsClientCode + "' and c.strManualCode='" + clientCode + "' " + " and date(a.dteInvDate)='" + invoiceDate + "' " + " group by a.strInvCode; ";
	
	JSONArray arrObj = new JSONArray();
	try
	{
	    webMMS = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webMMS.createStatement();
	    
	    ResultSet rsInvoicerData = st.executeQuery(sql);
	    while (rsInvoicerData.next())
	    {
		JSONObject obj = new JSONObject();
		
		obj.put("InvoiveCode", rsInvoicerData.getString(1));
		obj.put("InvoiceDate", rsInvoicerData.getString(2));
		obj.put("SOCode", rsInvoicerData.getString(3));
		obj.put("CustomerName", rsInvoicerData.getString(4));
		arrObj.put(obj);
	    }
	    rsInvoicerData.close();
	    jObj.put("InvoiceDtl", arrObj);
	    
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webMMS)
//		{
//		    webMMS.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return jObj.toString();
	}
    }
    
    @POST
    @Path("/funSaveImage")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveImage(JSONObject objImageData)
    {
	String response = "";
	response = funSaveImageData(objImageData);
	return Response.status(201).entity(response).build();
    }
    
    private String funSaveImageData(JSONObject objImageData)
    {
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webmms = null;
	Statement st = null;
	String res = "";
	
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    String originalString = "";
	    String imgName = "";
	    Date objDate = new Date();
	    int day = objDate.getDate();
	    int month = objDate.getMonth() + 1;
	    int year = objDate.getYear() + 1900;
	    String currentDate = year + "-" + month + "-" + day;
	    JSONArray mJsonArray = (JSONArray) objImageData.get("ImageData");
	    String clientCode = objImageData.getString("ClientCode");
	    String SOCode = "";
	    
	    /*
	     * String sqlInsertImageDtl="insert into tblsaveimage " +
	     * "(strName,strImage) " + "values ";
	     */
	    String sqlInsertAttachedImageDtl = "insert into tblattachdocument " + "(strTrans,strCode,strActualFileName,strChangedFileName,binContent,strContentType,strUserCreated,dtCreatedDate,strClientCode,strModuleName) " + "values ";
	    JSONObject mJsonObject = new JSONObject();
	    
	    int cnt = 0;
	    for (int i = 0; i < mJsonArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonArray.get(i);
		originalString = mJsonObject.getString("itemImage");
		// System.out.println(originalString.getBytes());
		imgName = mJsonObject.getString("itemName");
		SOCode = mJsonObject.getString("soCode");
		// System.out.println(mJsonObject);
		if (i == 0)
		{
		    cnt++;
		    // sqlInsertImageDtl+="('"+mJsonObject.getString("itemName")+"','"+mJsonObject.getString("itemImage")+"')";
		    sqlInsertAttachedImageDtl += "('sales Order','" + mJsonObject.getString("soCode") + "','" + mJsonObject.getString("itemName") + "','" + mJsonObject.getString("itemName") + "','" + mJsonObject.getString("itemImage") + "','','sanguine','" + currentDate + "','" + clientCode + "','T' )";
		}
		else
		{
		    cnt++;
		    // sqlInsertImageDtl+=",('"+mJsonObject.getString("itemName")+"','"+mJsonObject.getString("itemImage")+"')";
		    sqlInsertAttachedImageDtl += ",('sales Order','" + mJsonObject.getString("soCode") + "','" + mJsonObject.getString("itemName") + "','" + mJsonObject.getString("itemName") + "','" + mJsonObject.getString("itemImage") + "','','sanguine','" + currentDate + "','" + clientCode + "','T' )";
		}
	    }
	    // System.out.println("ImageDataDtl Data:="+sqlInsertImageDtl);
	    // System.out.println("AttachedImageDataDtl Data:="+sqlInsertAttachedImageDtl);
	    
	    if (cnt > 0)
	    {
		String sql = "delete from tblattachdocument where strCode='" + SOCode + "'";
		st.execute(sql);
		
		// res="successfully"+"#"+originalString+"#"+imgName;
		res = "successfully" + "#" + imgName;
		
		// st.executeUpdate(sqlInsertImageDtl);
		st.executeUpdate(sqlInsertAttachedImageDtl);
		for (int i = 0; i < mJsonArray.length(); i++)
		{
		    mJsonObject = (JSONObject) mJsonArray.get(i);
		    
		    originalString = mJsonObject.getString("itemImage");
		    imgName = mJsonObject.getString("advOrderNo") + "!" + mJsonObject.getString("itemName");
		    byte[] decoded = Base64.getDecoder().decode(originalString);
		    // System.out.println(new String(decoded,
		    // StandardCharsets.UTF_8));
		    String filePath = funCreateTempFolder();
		    File file = new File(filePath + "/" + imgName + ".jpg");
		    file.createNewFile();
		    FileOutputStream fos = new FileOutputStream(file);
		    fos.write(decoded);
		    fos.flush();
		    fos.close();
		}
	    }
	    else
	    {
		// res="unsuccessfully"+"#"+originalString+"#"+imgName;
		res = "unsuccessfully" + "#" + imgName;
	    }
	    // System.out.println("res:" +res);
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webmms)
//		{
//		    webmms.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return res;
	}
    }
    
    private String funCreateTempFolder()
    {
	String fileName = "Download Image";
	File theDir = new File(fileName);
	if (!theDir.exists())
	{
	    System.out.println("creating directory: " + "Download Image");
	    boolean result = false;
	    
	    try
	    {
		theDir.mkdir();
		result = true;
	    }
	    catch (SecurityException se)
	    {
		// handle it
	    }
	    if (result)
	    {
		System.out.println("DIR created");
	    }
	}
	return fileName;
    }
    
    @POST
    @Path("/funUpdateOrderCode")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funUpdateOrderCode(JSONObject objOrderData)
    {
	String response = "";
	try
	{
	    response = funUpdateOrderCodeData(objOrderData);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	return Response.status(201).entity(response).build();
    }
    
    private String funUpdateOrderCodeData(JSONObject objOrderData) throws Exception
    {
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webmms = null;
	Statement st = null;
	String res = "";
	
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    
	    JSONArray mJsonArray = (JSONArray) objOrderData.get("OrderDtl");
	    JSONObject mJsonObject = new JSONObject();
	    
	    for (int i = 0; i < mJsonArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonArray.get(i);
		String sqlUpdateOrderCodeDtl = " update tblsalesorderdtl set strRemarks='" + mJsonObject.getString("orderCode") + "' " + " where strSOCode='" + mJsonObject.getString("soCode") + "' ";
		st.executeUpdate(sqlUpdateOrderCodeDtl);
		res = "updated";
	    }
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    if (null != webmms)
	    {
		try
		{
		    st.close();
//		    webmms.close();
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	    }
	    return res;
	}
    }
    
    @DELETE
    @Path("/funDeleteAdvanceOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funDeleteAdvOrder(JSONObject objOrderData)
    {
	boolean flgResponse = false;
	String response = "";
	flgResponse = funDeleteAdvOrderSO(objOrderData);
	
	if (flgResponse)
	{
	    response = "Deleted Successfully";
	}
	else
	{
	    response = "Failed to Delete";
	}
	return Response.status(200).entity(response).build();
    }
    
    private boolean funDeleteAdvOrderSO(JSONObject objDataToDelete)
    {
	StringBuilder sbSql = new StringBuilder();
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection webmms = null;
	Statement st = null;
	boolean flgResponse = false;
	
	try
	{
	    webmms = clsDatabaseConnection.DBMMSCONNECTION;
	    st = webmms.createStatement();
	    String SOCode = "", clientCode = "";
	    JSONArray mJsonArray = (JSONArray) objDataToDelete.get("SODetails");
	    JSONObject mJsonObject = new JSONObject();
	    System.out.println("mJsonArray= " + mJsonArray);
	    for (int i = 0; i < mJsonArray.length(); i++)
	    {
		mJsonObject = (JSONObject) mJsonArray.get(i);
		sbSql.setLength(0);
		sbSql.append("delete from tblsalesorderdtl where strSOCode='" + mJsonObject.getString("SOCode") + "' " + " and strProdCode='" + mJsonObject.getString("ProdCode") + "' and strRemarks= '" + mJsonObject.getString("AdvOrderNo") + "' " + " and strClientCode='" + mJsonObject.getString("WSClientCode") + "' ");
		st.executeUpdate(sbSql.toString());
		// System.out.println("Delete SO= "+sbSql.toString());
		
		sbSql.setLength(0);
		sbSql.append("delete from tblsaleschar where strSOCode='" + mJsonObject.getString("SOCode") + "' " + " and strProdCode='" + mJsonObject.getString("ProdCode") + "' and strAdvOrderNo= '" + mJsonObject.getString("AdvOrderNo") + "' ");
		st.executeUpdate(sbSql.toString());
		
		sbSql.setLength(0);
		sbSql.append("delete from tblattachdocument where strCode='" + mJsonObject.getString("SOCode") + "' " + " and strActualFileName='" + mJsonObject.getString("ProdCode") + "' and strClientCode='" + mJsonObject.getString("WSClientCode") + "' " + " and strTrans='sales Order' ");
		st.executeUpdate(sbSql.toString());
		
		SOCode = mJsonObject.getString("SOCode");
		clientCode = mJsonObject.getString("WSClientCode");
	    }
	    
	    sbSql.setLength(0);
	    sbSql.append("select strSOCode from tblsalesorderdtl where strSOCode='" + SOCode + "' " + " and strClientCode='" + clientCode + "' ");
	    ResultSet rsSODetails = st.executeQuery(sbSql.toString());
	    if (rsSODetails.next())
	    {
	    }
	    else
	    {
		sbSql.setLength(0);
		sbSql.append("delete from tblsalesorderhd where strSOCode='" + SOCode + "' " + " and strClientCode='" + clientCode + "' ");
		st.executeUpdate(sbSql.toString());
		// System.out.println("Delete SO= "+sbSql.toString());
	    }
	    rsSODetails.close();
	    flgResponse = true;
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    sbSql = null;
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != webmms)
//		{
//		    webmms.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return flgResponse;
	}
    }
    
    @GET
    @Path("/funGetPropertyMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public String funGetPropertyData(@QueryParam("ClientCode") String clientCode)
    {
	return funGetPropertyDetails(clientCode);
    }
    
    @SuppressWarnings("finally")
    private String funGetPropertyDetails(String clientCode)
    {
	JSONObject jObjProperty = new JSONObject();
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	
	try
	{
	    cmsCon = clsDatabaseConnection.DBMMSCONNECTION;
	    st = cmsCon.createStatement();
	    String sql = " select a.strPropertyCode,a.strPropertyName from tblpropertymaster a  " + " where a.strClientCode='" + clientCode + "' ";
	    // System.out.println(sql);
	    JSONArray arrObjProperty = new JSONArray();
	    ResultSet rsProperty = st.executeQuery(sql);
	    while (rsProperty.next())
	    {
		JSONObject objProperty = new JSONObject();
		objProperty.put("PropertyCode", rsProperty.getString(1));
		objProperty.put("PropertyName", rsProperty.getString(2));
		arrObjProperty.put(objProperty);
	    }
	    rsProperty.close();
	    jObjProperty.put("PropertyDtls", arrObjProperty);
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != cmsCon)
//		{
//		    cmsCon.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return jObjProperty.toString();
	}
    }
    
    @GET
    @Path("/funGetUserMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public String funGetUserDetails(@QueryParam("ClientCode") String clientCode)
    {
	return funGetUserMasterDetails(clientCode);
    }
    
    @SuppressWarnings("finally")
    private String funGetUserMasterDetails(String clientCode)
    {
      JSONObject jObjSearchData = new JSONObject();
	  JSONArray jArrData = new JSONArray();
	//clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	
	try
	{
	    cmsCon = clsDatabaseConnection.DBMMSCONNECTION;
	    st = cmsCon.createStatement();
	    String sql = "select a.strUserCode,a.strUserName,a.strSuperUser from tbluserhd a " + " where a.strClientCode='" + clientCode + "' ";
	    // System.out.println(sql);
	    JSONArray arrObjUser = new JSONArray();
	    ResultSet rsUser = st.executeQuery(sql);
	    while (rsUser.next())
	    {
	    JSONArray jArrDataRow = new JSONArray();
	    jArrDataRow.put(rsUser.getString(1));
	    jArrDataRow.put(rsUser.getString(2));
	    jArrDataRow.put(rsUser.getString(3));
	    jArrData.put(jArrDataRow);
	    }
	    rsUser.close();
	    jObjSearchData.put("webstockusermaster", jArrData);
	}
	catch (Exception e)
	{
	    new clsUtilityFunctions().funWriteErrorLog(e);
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		if (null != st)
		{
		    st.close();
		}
//		if (null != cmsCon)
//		{
//		    cmsCon.close();
//		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	    return jObjSearchData.toString();
	}
    }
    
    
}
