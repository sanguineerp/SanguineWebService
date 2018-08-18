package com.apos.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsFileIOUtil;

@Controller
public class clsTextFileGenerator
{
	@Autowired
	clsJasperBillPrinting obJasperPrint;
    public void funCreateTempFolder()
    {
	try
	{
	    String filePath = System.getProperty("user.dir");
	    File textBill = new File(filePath + "/Temp");
	    if (!textBill.exists())
	    {
	    	textBill.mkdirs();
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    public void funGenerateAndPrintBill(ResponseBuilder resp,String billNo, String posCode, String clientCode,String reprint) throws Exception
    {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		String sql = null;
		String billFormat = "";
		ResultSet rs = null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
		st = cmsCon.createStatement();
		sql = "select a.strBillFormatType from tblsetup a;";
		rs = st.executeQuery(sql);
		if (rs.next())
		{
		    billFormat = rs.getString(1);
		}
		rs.close();
		if (billFormat.equalsIgnoreCase("Text 1"))
		{
		    funGenerateTextFileForBill(billNo, posCode, clientCode,reprint);
		}
		else if (billFormat.equalsIgnoreCase("Text 5"))
		{
		    funGenerateTextFileForBillFormat5(billNo, posCode, clientCode,reprint);
		}
		else if (billFormat.equalsIgnoreCase("Text 11"))
		{
		    funGenerateTextFileForBillFormat11(billNo, posCode, clientCode,reprint);
		}
		else if(billFormat.equalsIgnoreCase("Jasper 5")){
			obJasperPrint.funCredateJasper(resp,billNo, posCode, clientCode,reprint);
		}
    }
    
    void funGenerateTextFileForBill(String billNo, String posCode, String clientCode,String reprint)
    {
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	String sql = null;
	ResultSet rs = null;
	ArrayList<ArrayList<String>> arrListBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrListModifierBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrTaxtBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrSettleBillDtl = new ArrayList<ArrayList<String>>();
	
	try
	{
	    cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
	    st = cmsCon.createStatement();
	    funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File textBill = new File(filePath + "/Temp/TempBill.txt");
	    PrintWriter pw = new PrintWriter(textBill);
	    String billno = " ";
	    String operationType = " ";
	    String waiterNo = "";
	    String tablName = "";
	    Double sbTotal = 0.0;
	    Double dis = 0.0;
	    Double gTotal = 0.0;
	    String bDate = "";
	    String clientName = "";
	    String addressLine1 = "";
	    String addressLine2 = "";
	    String addressLine3 = "";
	    String city = "";
	    String telephone = "";
	    String email = "";
	    String vatNo = "";
	    String serviceTaxNo = "";
	    String billFooter = "";
	    String printVatNo = "";
	    String printServiceTaxNo = "";
	    String multiBillPrint = "N";
	    String customerCode = "";
	    String customerName = "";
	    String mobileNo = "";
	    String address = "";
	    String printInclusiveOfAllTaxesOnBill="";
	    String userName="";
	    String line="----------------------------------------";
	    sql = " select a.strBillNo,ifnull(b.strTableName,''),ifnull(c.strWShortName,''),a.dblGrandTotal,a.dblSubTotal,a.dblDiscountAmt,a.dteBillDate,a.strOperationType,a.strCustomerCode " + ",a.strUserCreated from tblbillhd a left outer join tbltablemaster b " + " on a.strTableNo=b.strTableNo " + " left outer join tblwaitermaster c " + " on a.strWaiterNo=c.strWaiterNo " + " where a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		billno = rs.getString(1);
		tablName = rs.getString(2);
		waiterNo = rs.getString(3);
		sbTotal = rs.getDouble(5);
		dis = rs.getDouble(6);
		bDate = rs.getString(7);
		operationType = rs.getString(8);
		customerCode = rs.getString(9);
		gTotal = rs.getDouble(4);
		userName=rs.getString(10);
	    }
	    rs.close();
	    
	    sql = "  select b.strItemName,b.dblQuantity,b.dblAmount " + "  from tblbillhd a,tblbilldtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
	    System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrListItem = new ArrayList<String>();
		String []arrQtyData=rs.getString(2).split("\\.");
	    int Qty=Integer.parseInt(arrQtyData[0]);
		arrListItem.add(rs.getString(1));
		arrListItem.add(String.valueOf(Qty));
		arrListItem.add(rs.getString(3));
		arrListBillDtl.add(arrListItem);
	    }
	    rs.close();
	    
	    sql = "  select b.strModifierName,b.dblQuantity,b.dblAmount " + "  from tblbillhd a,tblbillmodifierdtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
	    System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrListItem = new ArrayList<String>();
		String []arrQtyData=rs.getString(2).split("\\.");
	    int Qty=Integer.parseInt(arrQtyData[0]);
		arrListItem.add(rs.getString(1));
		arrListItem.add(String.valueOf(Qty));
		arrListItem.add(rs.getString(3));
		arrListModifierBillDtl.add(arrListItem);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo ,c.strTaxDesc,b.dblTaxAmount from tblbillhd a,tblbilltaxdtl b,tbltaxhd c " + " where b.strTaxCode=c.strTaxCode " + "	and a.strBillNo=b.strBillNo " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrTaxListItem = new ArrayList<String>();
		arrTaxListItem.add(rs.getString(2));
		arrTaxListItem.add(rs.getString(3));
		arrTaxtBillDtl.add(arrTaxListItem);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo,b.dblSettlementAmt,b.dblPaidAmt,c.strSettelmentDesc " + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + " where a.strBillNo=b.strBillNo " + " and b.strSettlementCode=c.strSettelmentCode " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrSettleListItem = new ArrayList<String>();
		arrSettleListItem.add(rs.getString(4));
		arrSettleListItem.add(rs.getString(2));
		arrSettleBillDtl.add(arrSettleListItem);
	    }
	    rs.close();
	    
	    sql = " select a.strClientName,a.strAddressLine1,a.strAddressLine2, a.strAddressLine3,a.strCityName,a.intTelephoneNo, a.strEmail,a.strVatNo,a.strServiceTaxNo,a.strBillFooter,a.strPrintServiceTaxNo,a.strPrintVatNo,a.strMultipleBillPrinting ,a.strPrintInclusiveOfAllTaxesOnBill from tblsetup a ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		clientName = rs.getString(1);
		addressLine1 = rs.getString(2);
		addressLine2 = rs.getString(3);
		addressLine3 = rs.getString(4);
		city = rs.getString(5);
		telephone = rs.getString(6);
		email = rs.getString(7);
		vatNo = rs.getString(8);
		serviceTaxNo = rs.getString(9);
		billFooter = rs.getString(10);
		printServiceTaxNo = rs.getString(11);
		printVatNo = rs.getString(12);
		printInclusiveOfAllTaxesOnBill= rs.getString(14);
	    }
	    rs.close();
	    if ("Y".equalsIgnoreCase(reprint))
        {
	    	pw.print(funPrintTextWithAlignment("[DUPLICATE]", 40, "Center"));
	    	pw.println(" ");
	        
        }
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		pw.println(funPrintTextWithAlignment("Home Delivery", 40, "Center"));
	    }
	    else if (operationType.equalsIgnoreCase("TakeAway"))
	    {
		pw.println(funPrintTextWithAlignment("Take Away", 40, "Center"));
	    }
	    
	    pw.println(funPrintTextWithAlignment("TAX INVOICE", 40, "Center"));
	    pw.println(funPrintTextWithAlignment(clientName, 40, "Center"));
	    if (!addressLine1.isEmpty())
	    {
		if (addressLine1.length() > 40)
		{
		    pw.println(funPrintTextWithAlignment(addressLine1.substring(0, 40), 40, "Center"));
		    pw.println(funPrintTextWithAlignment(addressLine1.substring(40), 40, "Center"));
		}
		
		else
		{
		    pw.println(funPrintTextWithAlignment(addressLine1, 40, "Center"));
		}
	    }
	    if (!addressLine2.isEmpty())
	    {
		if (addressLine2.length() > 40)
		{
		    pw.println(funPrintTextWithAlignment(addressLine2.substring(0, 40), 40, "Center"));
		    pw.println(funPrintTextWithAlignment(addressLine2.substring(40), 40, "Center"));
		}
		else
		{
		    pw.println(funPrintTextWithAlignment(addressLine2, 40, "Center"));
		}
	    }
	    
	    if (!addressLine3.isEmpty())
	    {
		if (addressLine3.length() > 40)
		{
		    pw.println(funPrintTextWithAlignment(addressLine3.substring(0, 40), 40, "Center"));
		    pw.println(funPrintTextWithAlignment(addressLine3.substring(40), 40, "Center"));
		}
		else
		{
		    pw.println(funPrintTextWithAlignment(addressLine3, 40, "Center"));
		}
	    }
	    if (!city.isEmpty())
	    {
		pw.println(funPrintTextWithAlignment(city, 40, "Center"));
	    }
	    if (!telephone.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("TEL NO.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(telephone, 26, "Left"));
		pw.println(" ");
	    }
	    if (!email.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("EMAIL ID", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(email, 26, "Left"));
		pw.println(" ");
	    }
	    
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		
		st = cmsCon.createStatement();
		
		sql = "  select a.strCustomerName,a.longMobileNo,a.strBuildingName from tblcustomermaster a  " + "  where a.strCustomerCode='" + customerCode + "' ";
		System.out.println(sql);
		rs = st.executeQuery(sql);
		while (rs.next())
		{
		    customerName = rs.getString(1);
		    mobileNo = rs.getString(2);
		    address = rs.getString(3);
		    
		}
		rs.close();
		st.close();
		pw.print(funPrintTextWithAlignment("Customer Name", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(customerName, 25, "Left"));
		pw.println(" ");
		pw.print(funPrintTextWithAlignment("Mobile No", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(mobileNo, 25, "Left"));
		pw.println(" ");
		pw.print(funPrintTextWithAlignment("Address ", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		
		
		 if (!address.isEmpty())
		    {
			   int totalCharToPrint=25;
		    	int strlen = address.length();
	            String add1 = "";
	            if (strlen < totalCharToPrint)
	            {
	            	pw.print(funPrintTextWithAlignment(address, totalCharToPrint, "Left"));
	            }
	            else
	            {
	            	add1 = address.substring(0, totalCharToPrint);
	                pw.println(funPrintTextWithAlignment(add1, 25, "Left"));
	            }
	            
	            //add1=add.substring(len,56);
	            for (int i = totalCharToPrint; i <= strlen; i++)
	            {
	                int end = 0;
	                end = i + totalCharToPrint;
	                if (strlen > end)
	                {
	                    add1 = address.substring(i, end);
	                    i = end;
	                    pw.println(funPrintTextWithAlignment("              " +add1, 40, "Left"));
	                }
	                else
	                {
	                    add1 = address.substring(i, strlen);
	                    pw.println(funPrintTextWithAlignment("              " +add1, 40, "Left"));
	                    i = strlen + 1;
	                }
	            }
		   
		    }
		
		
		pw.println(" ");
	    }
	    
	    if (!tablName.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("TABLE NAME", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(tablName, 26, "Left"));
		pw.println(" ");
	    }
	    
	    if (!waiterNo.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("STEWARD", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(waiterNo, 26, "Left"));
		pw.println(" ");
	    }
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("BILL NO.", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(billno, 26, "Left"));
	    pw.println(" ");
	    pw.print(funPrintTextWithAlignment("DATE & TIME", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(bDate, 26, "Left"));
	    pw.println(" ");
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("QTY ", 6, "RIGHT"));
	    pw.print(funPrintTextWithAlignment(" ITEMNAME", 26, "Left"));
	    pw.print(funPrintTextWithAlignment("AMT", 8, "RIGHT"));
	    pw.println(" ");
	    pw.println(line);
	    for (int cnt = 0; cnt < arrListBillDtl.size(); cnt++)
	    {
		ArrayList<String> items = arrListBillDtl.get(cnt);
		pw.print(funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
		pw.print(funPrintTextWithAlignment("" + items.get(0), 26, "Left"));
		pw.print(funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
		pw.println(" ");
	    }
	    if (arrListModifierBillDtl.size() > 0)
	    {
		for (int cnt = 0; cnt < arrListModifierBillDtl.size(); cnt++)
		{
		    ArrayList<String> items = arrListModifierBillDtl.get(cnt);
		    pw.print(funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
		    pw.print(funPrintTextWithAlignment("" + items.get(0), 26, "Left"));
		    pw.print(funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
		    pw.println(" ");
		}
	    }
	    
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("SUB TOTAL", 32, "Left"));
	    pw.print(funPrintTextWithAlignment("" + sbTotal, 8, "RIGHT"));
	    pw.println(" ");
	    if(dis>0)
	    {
	    	pw.print(funPrintTextWithAlignment("DISCOUNT  ", 32, "Left"));
		    pw.print(funPrintTextWithAlignment("" + dis, 8, "RIGHT"));
		    pw.println(" "); 	
	    }
	    for (int cnt = 0; cnt < arrTaxtBillDtl.size(); cnt++)
	    {
		ArrayList<String> items = arrTaxtBillDtl.get(cnt);
		pw.print(funPrintTextWithAlignment("" + items.get(0) + " ", 32, "Left"));
		pw.print(funPrintTextWithAlignment("" + items.get(1), 8, "RIGHT"));
		pw.println(" ");
	    }
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("TOTAL(ROUNDED)", 32, "Left"));
	    pw.print(funPrintTextWithAlignment("" + gTotal, 8, "RIGHT"));
	    pw.println(" ");
	    pw.println(line);
	    if(arrSettleBillDtl.size()>0)
	    {
	    	for (int cnt = 0; cnt < arrSettleBillDtl.size(); cnt++)
		    {
			ArrayList<String> items = arrSettleBillDtl.get(cnt);
			pw.println("---------Settlement Information--------");
			pw.print(funPrintTextWithAlignment(items.get(0), 17, "Left"));
			pw.print(funPrintTextWithAlignment("" + items.get(1), 23, "RIGHT"));
			pw.println(" ");
		    }
	    	pw.print(funPrintTextWithAlignment("PAID AMT", 17, "Left"));
			pw.print(funPrintTextWithAlignment("" +gTotal , 23, "RIGHT"));
			pw.println(" ");
	    	pw.println(line);
	    }
   
	    if (printInclusiveOfAllTaxesOnBill.equals("Y"))
        {
	    	pw.println(funPrintTextWithAlignment("(INCLUSIVE OF ALL TAXES)", 40, "Center")); 
        }
	    pw.println(" ");
	    
	    
	    if (printVatNo.equalsIgnoreCase("Y"))
	    {
		pw.print(funPrintTextWithAlignment("Vat No.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(vatNo, 26, "Left"));
		pw.println(" ");
	    }
	    if (printServiceTaxNo.equalsIgnoreCase("Y"))
	    {
		pw.print(funPrintTextWithAlignment("Service Tax No.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(serviceTaxNo, 26, "Left"));
		pw.println(" ");
	    }
	    int num = billFooter.trim().length() / 30;
        int num1 = billFooter.length() % 30;
        int cnt1 = 0;
        for (int cnt = 0; cnt < num; cnt++)
        {
            String footer = billFooter.substring(cnt1, (cnt1 + 30));
            footer = footer.replaceAll("\n", "");
            pw.println(funPrintTextWithAlignment("     " + footer.trim(), 40, "Center"));
            cnt1 += 30;
        }
        pw.println(" ");
	    pw.println(funPrintTextWithAlignment(userName, 40, "Center"));
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println("m");
	    
	    pw.flush();
	    pw.close();
	    
	    st.close();
	    st = cmsCon.createStatement();
	    sql = "select strBillPrinterPort,strAdvReceiptPrinterPort from tblposmaster where strPOSCode='" + posCode + "'";
	    rs = st.executeQuery(sql);
	    if (rs.next())
	    {
		funPrintTextFile(rs.getString(1), rs.getString(1), "Bill", "", "", multiBillPrint,"");
	    }
	    rs.close();
	    cmsCon.close();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    arrListBillDtl = null;
	    arrTaxtBillDtl = null;
	    arrSettleBillDtl = null;
	}
    }
    
    void funGenerateTextFileForBillFormat5(String billNo, String posCode, String clientCode,String reprint)
    {
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	String sql = null;
	ResultSet rs = null;
	ArrayList<ArrayList<String>> arrListBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrListModifierBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrTaxtBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrSettleBillDtl = new ArrayList<ArrayList<String>>();
	
	try
	{
		Date dteBillDate;
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	    cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
	    st = cmsCon.createStatement();
	    funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File textBill = new File(filePath + "/Temp/TempBill.txt");
	    PrintWriter pw = new PrintWriter(textBill);
	    String billno = " ";
	    String operationType = " ";
	    String waiterNo = "";
	    String tablName = "";
	    Double gdTotal = 0.0;
	    Double sbTotal = 0.0;
	    Double dis = 0.0;
	    String strBillDate = "";
	    String clientName = "";
	    String addressLine1 = "";
	    String addressLine2 = "";
	    String addressLine3 = "";
	    String city = "";
	    String telephone = "";
	    String email = "";
	    String vatNo = "";
	    String serviceTaxNo = "";
	    String billFooter = "";
	    String printVatNo = "";
	    String printServiceTaxNo = "";
	    String multiBillPrint = "N";
	    String posName = "";
	    int paxNo = 0;
	    String memberCode = "";
	    String memberName = "";
	    String customerCode = "";
	    String customerName = "";
	    String mobileNo = "";
	    String address = "";
	    String printInclusiveOfAllTaxesOnBill="";
	    String userName="";
	    String line="----------------------------------------";
	    
	    sql = "select strPosName from tblPOSMaster where strPosCode='" + posCode + "'";
	    rs = st.executeQuery(sql);
	    if (rs.next())
	    {
		posName = rs.getString(1);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo,ifnull(b.strTableName,''),ifnull(c.strWShortName,''),a.dblGrandTotal,a.dblSubTotal,a.dblDiscountAmt,a.dteBillDate,a.intPaxNo,a.strOperationType,a.strCustomerCode " + ",a.strUserCreated from tblbillhd a left outer join tbltablemaster b " + " on a.strTableNo=b.strTableNo " + " left outer join tblwaitermaster c " + " on a.strWaiterNo=c.strWaiterNo " + " where a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		billno = rs.getString(1);
		tablName = rs.getString(2);
		waiterNo = rs.getString(3);
		gdTotal = rs.getDouble(4);
		sbTotal = rs.getDouble(5);
		dis = rs.getDouble(6);
		strBillDate = rs.getString(7);
		paxNo = rs.getInt(8);
		operationType = rs.getString(9);
		customerCode = rs.getString(10);
		userName= rs.getString(11);
	    }
	    rs.close();
	    dteBillDate=format1.parse(strBillDate);
	    sql = "  select b.strItemName,b.dblQuantity,b.dblAmount " + "  from tblbillhd a,tblbilldtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
	    System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrListItem = new ArrayList<String>();
		
		String []arrQtyData=rs.getString(2).split("\\.");
	    int Qty=Integer.parseInt(arrQtyData[0]);
		arrListItem.add(rs.getString(1));
		arrListItem.add(String.valueOf(Qty));
		arrListItem.add(rs.getString(3));
		arrListBillDtl.add(arrListItem);
	    }
	    rs.close();
	    
	    sql = "  select b.strModifierName,b.dblQuantity,b.dblAmount " + "  from tblbillhd a,tblbillmodifierdtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
	    System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrListItem = new ArrayList<String>();
		String []arrQtyData=rs.getString(2).split("\\.");
	    int Qty=Integer.parseInt(arrQtyData[0]);
		arrListItem.add(rs.getString(1));
		arrListItem.add(String.valueOf(Qty));
		arrListItem.add(rs.getString(3));
		arrListModifierBillDtl.add(arrListItem);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo ,c.strTaxDesc,b.dblTaxAmount from tblbillhd a,tblbilltaxdtl b,tbltaxhd c " + " where b.strTaxCode=c.strTaxCode " + "	and a.strBillNo=b.strBillNo " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrTaxListItem = new ArrayList<String>();
		arrTaxListItem.add(rs.getString(2));
		arrTaxListItem.add(rs.getString(3));
		arrTaxtBillDtl.add(arrTaxListItem);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo,b.dblSettlementAmt,b.dblPaidAmt,c.strSettelmentDesc " + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + " where a.strBillNo=b.strBillNo " + " and b.strSettlementCode=c.strSettelmentCode " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrSettleListItem = new ArrayList<String>();
		arrSettleListItem.add(rs.getString(4));
		arrSettleListItem.add(rs.getString(2));
		arrSettleBillDtl.add(arrSettleListItem);
		
	    }
	    rs.close();
	    
	    sql = " select a.strClientName,a.strAddressLine1,a.strAddressLine2, a.strAddressLine3,a.strCityName,a.intTelephoneNo, a.strEmail,a.strVatNo,a.strServiceTaxNo,a.strBillFooter,a.strPrintServiceTaxNo,a.strPrintVatNo,a.strMultipleBillPrinting ,a.strPrintInclusiveOfAllTaxesOnBill from tblsetup a ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		clientName = rs.getString(1);
		addressLine1 = rs.getString(2);
		addressLine2 = rs.getString(3);
		addressLine3 = rs.getString(4);
		city = rs.getString(5);
		telephone = rs.getString(6);
		email = rs.getString(7);
		vatNo = rs.getString(8);
		serviceTaxNo = rs.getString(9);
		billFooter = rs.getString(10);
		printServiceTaxNo = rs.getString(11);
		printVatNo = rs.getString(12);
		printInclusiveOfAllTaxesOnBill= rs.getString(14);
		
	    }
	    rs.close();
	    
	    sql = "select b.strCustomerCode,b.strCustomerName " + " from tblbillhd a,tblcustomermaster b " + " where a.strCustomerCode=b.strCustomerCode " + " and a.strBillNo='" + billNo + "'";
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		memberCode = rs.getString(1);
		memberName = rs.getString(2);
		
	    }
	    rs.close();
	    if ("Y".equalsIgnoreCase(reprint))
        {
	    	pw.print(funPrintTextWithAlignment("[DUPLICATE]", 40, "Center"));
	    	pw.println(" ");
	        
        }
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		pw.println(funPrintTextWithAlignment("Home Delivery", 40, "Center"));
	    }
	    else if (operationType.equalsIgnoreCase("TakeAway"))
	    {
		pw.println(funPrintTextWithAlignment("Take Away", 40, "Center"));
	    }
	    
	    pw.println(funPrintTextWithAlignment("TAX INVOICE", 40, "Center"));
	    pw.println(funPrintTextWithAlignment(clientName, 40, "Center"));
	    
	    if (clientCode.equals("047.001"))
	    {
		if (posCode.equals("P03") || posCode.equals("P02"))
		{
		    pw.println(funPrintTextWithAlignment("SHRI SHAM CATERERS", 40, "Center"));
		    
		    String cAddr1 = "Flat No.7, Mon Amour,".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr1, 40, "Center"));
		    
		    String cAddr2 = "Thorat Colony,Prabhat Road,".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr2, 40, "Center"));
		    
		    String cAddr3 = " Erandwane, Pune 411 004.".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr3, 40, "Center"));
		    
		    String cAddr4 = "Approved Caterers of".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr4, 40, "Center"));
		    
		    String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr5, 40, "Center"));
		}
	    }
	    else
	    {
		if (!addressLine1.isEmpty())
		{
		    if (addressLine1.length() > 40)
		    {
			pw.println(funPrintTextWithAlignment(addressLine1.substring(0, 40), 40, "Center"));
			pw.println(funPrintTextWithAlignment(addressLine1.substring(40), 40, "Center"));
		    }
		    
		    else
		    {
			pw.println(funPrintTextWithAlignment(addressLine1, 40, "Center"));
		    }
		}
		if (!addressLine2.isEmpty())
		{
		    if (addressLine2.length() > 40)
		    {
			pw.println(funPrintTextWithAlignment(addressLine2.substring(0, 40), 40, "Center"));
			pw.println(funPrintTextWithAlignment(addressLine2.substring(40), 40, "Center"));
		    }
		    else
		    {
			pw.println(funPrintTextWithAlignment(addressLine2, 40, "Center"));
		    }
		}
		
		if (!addressLine3.isEmpty())
		{
		    if (addressLine3.length() > 40)
		    {
			pw.println(funPrintTextWithAlignment(addressLine3.substring(0, 40), 40, "Center"));
			pw.println(funPrintTextWithAlignment(addressLine3.substring(40), 40, "Center"));
		    }
		    else
		    {
			pw.println(funPrintTextWithAlignment(addressLine3, 40, "Center"));
		    }
		}
		if (!city.isEmpty())
		{
		    pw.println(funPrintTextWithAlignment(city, 40, "Center"));
		}
	    }
	    if (!telephone.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("TEL NO.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(telephone, 26, "Left"));
		pw.println(" ");
	    }
	    if (!email.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("EMAIL ID", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(email, 26, "Left"));
		pw.println(" ");
	    }
	    
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		
		st = cmsCon.createStatement();
		
		sql = "  select a.strCustomerName,a.longMobileNo,a.strBuildingName from tblcustomermaster a  " + "  where a.strCustomerCode='" + customerCode + "' ";
		System.out.println(sql);
		rs = st.executeQuery(sql);
		while (rs.next())
		{
		    customerName = rs.getString(1);
		    mobileNo = rs.getString(2);
		    address = rs.getString(3);
		    
		}
		rs.close();
		//st.close();
		pw.print(funPrintTextWithAlignment("Customer Name", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(customerName, 25, "Left"));
		pw.println(" ");
		pw.print(funPrintTextWithAlignment("Mobile No", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(mobileNo, 25, "Left"));
		pw.println(" ");
		pw.print(funPrintTextWithAlignment("Address ", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(address, 25, "Left"));
		pw.println(" ");
	    }
	    
	    if (!tablName.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("TABLE NAME", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(tablName, 26, "Left"));
		pw.println(" ");
	    }
	    if (!waiterNo.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("STEWARD", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(waiterNo, 26, "Left"));
		pw.println(" ");
	    }
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("POS Name", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(posName, 26, "Left"));
	    pw.println(" ");
	    pw.print(funPrintTextWithAlignment("Pax No.", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(String.valueOf(paxNo), 26, "Left"));
	    pw.println(" ");
	    pw.print(funPrintTextWithAlignment("BILL NO.", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(billno, 26, "Left"));
	    pw.println(" ");
	    pw.print(funPrintTextWithAlignment("DATE & TIME", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(sdf.format(dteBillDate), 26, "Left"));
	    
	    if (!memberCode.isEmpty())
	    {
	    	pw.println(" ");
	    	pw.println(line);
		    pw.print(funPrintTextWithAlignment("Member Code", 12, "Left"));
		    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		    pw.print(funPrintTextWithAlignment(memberCode, 26, "Left"));
		    pw.println(" ");
		    pw.print(funPrintTextWithAlignment("Member Name", 12, "Left"));
		    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		    pw.print(funPrintTextWithAlignment(memberName, 26, "Left"));
	    }
	    
	    pw.println(" "); 
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("QTY ", 6, "RIGHT"));
	    pw.print(funPrintTextWithAlignment(" ITEMNAME", 26, "Left"));
	    pw.print(funPrintTextWithAlignment("AMT", 8, "RIGHT"));
	    pw.println(" ");
	    pw.println(line);
	    for (int cnt = 0; cnt < arrListBillDtl.size(); cnt++)
	    {
		ArrayList<String> items = arrListBillDtl.get(cnt);
		pw.print(funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
		pw.print(funPrintTextWithAlignment("" + items.get(0), 26, "Left"));
		pw.print(funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
		pw.println(" ");
	    }
	    if (arrListModifierBillDtl.size() > 0)
	    {
		for (int cnt = 0; cnt < arrListModifierBillDtl.size(); cnt++)
		{
		    ArrayList<String> items = arrListModifierBillDtl.get(cnt);
		    pw.print(funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
		    pw.print(funPrintTextWithAlignment("" + items.get(0), 26, "Left"));
		    pw.print(funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
		    pw.println(" ");
		}
	    }
	    
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("SUB TOTAL", 32, "Left"));
	    pw.print(funPrintTextWithAlignment("" + sbTotal, 8, "RIGHT"));
	    pw.println(" ");
	    if(dis>0)
	    {
	    	pw.print(funPrintTextWithAlignment("DISCOUNT  ", 32, "Left"));
		    pw.print(funPrintTextWithAlignment("" + dis, 8, "RIGHT"));
		    pw.println(" "); 	
	    }
	    for (int cnt = 0; cnt < arrTaxtBillDtl.size(); cnt++)
	    {
			ArrayList<String> items = arrTaxtBillDtl.get(cnt);
			pw.print(funPrintTextWithAlignment("" + items.get(0) + " ", 32, "Left"));
			pw.print(funPrintTextWithAlignment("" + items.get(1), 8, "RIGHT"));
			pw.println(" ");
	    }
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("TOTAL(ROUNDED)", 32, "Left"));
	    pw.print(funPrintTextWithAlignment("" + gdTotal, 8, "RIGHT"));
	    pw.println(" ");
	    pw.println(line);
	    sql="select a.strEnableBillSeries from tblsetup a where a.strClientCode='"+clientCode+"' and a.strPOSCode='"+posCode+"'";
	    System.out.println(sql);
	    String enableBillSeries="N";
		rs = st.executeQuery(sql);
		if (rs.next())
		{
			enableBillSeries = rs.getString(1);
		}
		rs.close();
		if(enableBillSeries.equals("Y")){
			String sqlPrintGT = "select a.strPrintGTOfOtherBills,b.strDtlBillNos,b.dblGrandTotal "
                    + "from tblbillseries a,tblbillseriesbilldtl b "
                    + "where (a.strposCode=b.strposCode or a.strposCode='All') "
                    + "and a.strBillSeries=b.strBillSeries "
                    + "and b.strHdBillNo='" + billNo + "' "
                    + "and b.strposCode='" + posCode + "' "
                    + "and date(b.dteBillDate)='" + strBillDate.split(" ")[0] + "' ";
            ResultSet rsPrintGTYN = st.executeQuery(sqlPrintGT);
            double dblOtherBillsGT = 0.00;
            if (rsPrintGTYN.next())
            {
                if (rsPrintGTYN.getString(1).equalsIgnoreCase("Y"))
                {
                    String billSeriesDtlBillNos = rsPrintGTYN.getString(2);
                    String[] dtlBillSeriesBillNo = billSeriesDtlBillNos.split(",");
                    dblOtherBillsGT += rsPrintGTYN.getDouble(3);
                    if (dtlBillSeriesBillNo.length > 0)
                    {
                        for (int i = 0; i < dtlBillSeriesBillNo.length; i++)
                        {
                            sqlPrintGT = "select a.strHdBillNo,a.dblGrandTotal "
                                    + "from tblbillseriesbilldtl a "
                                    + "where a.strHdBillNo='" + dtlBillSeriesBillNo[i] + "' "
                                    + "and a.strposCode='" + posCode + "' "
                                    + "and date(a.dteBillDate)='" + strBillDate.split(" ")[0] + "' ";
                            ResultSet rsPrintGT = st.executeQuery(sqlPrintGT);
                            if (rsPrintGT.next())
                            {
                            	 pw.println(line);
                            	 pw.print(funPrintTextWithAlignment(dtlBillSeriesBillNo[i] + " TOTAL(ROUNDED)", 32, "Left"));
                            	 pw.print(funPrintTextWithAlignment("" + rsPrintGT.getDouble(2), 8, "RIGHT"));
                         	     pw.println(" ");
                         	     pw.println(line);
                               // objPrintingUtility.funWriteTotal(dtlBillSeriesBillNo[i] + " TOTAL(ROUNDED)", rsPrintGT.getString(2), BillOut, "Format5");
                                dblOtherBillsGT += rsPrintGT.getDouble(2);
                                //BillOut.newLine();
                            }
                        }
                         pw.println(line);
                   	 	 pw.print(funPrintTextWithAlignment("GRAND TOTAL(ROUNDED)", 32, "Left"));
                   	 	 pw.print(funPrintTextWithAlignment("" + String.valueOf(dblOtherBillsGT), 8, "RIGHT"));
                	     pw.println(" ");
                	     pw.println(line);
                        
                    }
                }
            }
		}
		st.close();
	    if(arrSettleBillDtl.size()>0)
	    {
	    	for (int cnt = 0; cnt < arrSettleBillDtl.size(); cnt++)
		    {
			ArrayList<String> items = arrSettleBillDtl.get(cnt);
			pw.println("---------Settlement Information--------");
			pw.print(funPrintTextWithAlignment(items.get(0), 17, "Left"));
			pw.print(funPrintTextWithAlignment("" + items.get(1), 23, "RIGHT"));
			pw.println(" ");
		    }
	    	
	    	pw.print(funPrintTextWithAlignment("PAID AMT", 17, "Left"));
			pw.print(funPrintTextWithAlignment("" +gdTotal , 23, "RIGHT"));
			pw.println(" ");
		    pw.println(line);
	    }
	    
	    if (printInclusiveOfAllTaxesOnBill.equals("Y"))
        {
	    	pw.println(funPrintTextWithAlignment("(INCLUSIVE OF ALL TAXES)", 40, "Center")); 
        }
	    pw.println(" ");
	    if (printVatNo.equalsIgnoreCase("Y"))
	    {
		pw.print(funPrintTextWithAlignment("Vat No.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(vatNo, 26, "Left"));
		pw.println(" ");
	    }
	    if (printServiceTaxNo.equalsIgnoreCase("Y"))
	    {
		pw.print(funPrintTextWithAlignment("Service Tax No.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(serviceTaxNo, 26, "Left"));
		pw.println(" ");
	    }
	    int num = billFooter.trim().length() / 30;
        int num1 = billFooter.length() % 30;
        int cnt1 = 0;
        for (int cnt = 0; cnt < num; cnt++)
        {
            String footer = billFooter.substring(cnt1, (cnt1 + 30));
            footer = footer.replaceAll("\n", "");
            pw.println(funPrintTextWithAlignment("     " + footer.trim(), 40, "Center"));
     
            cnt1 += 30;
        }
        pw.println(" ");
        pw.println(funPrintTextWithAlignment(userName, 40, "Center"));
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println("m");
	    
	    pw.flush();
	    pw.close();
	    
	    st.close();
	    st = cmsCon.createStatement();
	    sql = "select strBillPrinterPort,strAdvReceiptPrinterPort from tblposmaster where strPOSCode='" + posCode + "'";
	    rs = st.executeQuery(sql);
	    if (rs.next())
	    {
		funPrintTextFile(rs.getString(1), rs.getString(1), "Bill", "", "", multiBillPrint,"");
	    }
	    rs.close();
	    
	    cmsCon.close();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    arrTaxtBillDtl = null;
	    arrListBillDtl = null;
	    arrSettleBillDtl = null;
	}
    }
    
    void funGenerateTextFileForBillFormat12(String billNo, String posCode, String clientCode)
    {
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	String sql = null;
	ResultSet rs = null;
	ArrayList<ArrayList<String>> arrListBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrListModifierBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrTaxtBillDtl = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arrSettleBillDtl = new ArrayList<ArrayList<String>>();
	
	try
	{
	    cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
	    st = cmsCon.createStatement();
	    funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File textBill = new File(filePath + "/Temp/TempBill.txt");
	    PrintWriter pw = new PrintWriter(textBill);
	    String billno = " ";
	    String operationType = " ";
	    String waiterNo = "";
	    String tablName = "";
	    Double gdTotal = 0.0;
	    Double sbTotal = 0.0;
	    Double dis = 0.0;
	    String bDate = "";
	    String clientName = "";
	    String addressLine1 = "";
	    String addressLine2 = "";
	    String addressLine3 = "";
	    String city = "";
	    String telephone = "";
	    String email = "";
	    String vatNo = "";
	    String serviceTaxNo = "";
	    String billFooter = "";
	    String printVatNo = "";
	    String printServiceTaxNo = "";
	    String multiBillPrint = "N";
	    String posName = "";
	    int paxNo = 0;
	    String memberCode = "";
	    String memberName = "";
	    String customerCode = "";
	    String customerName = "";
	    String mobileNo = "";
	    String address = "";
	    String userName="";
	    String printInclusiveOfAllTaxesOnBill="";
	    String line="----------------------------------------";
	    
	    sql = "select strPosName from tblPOSMaster where strPosCode='" + posCode + "'";
	    rs = st.executeQuery(sql);
	    if (rs.next())
	    {
		posName = rs.getString(1);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo,ifnull(b.strTableName,''),ifnull(c.strWShortName,''),a.dblGrandTotal,a.dblSubTotal,a.dblDiscountAmt,a.dteBillDate,a.intPaxNo,a.strOperationType,a.strCustomerCode " + ",a.strUserCreated from tblbillhd a left outer join tbltablemaster b " + " on a.strTableNo=b.strTableNo " + " left outer join tblwaitermaster c " + " on a.strWaiterNo=c.strWaiterNo " + " where a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		billno = rs.getString(1);
		tablName = rs.getString(2);
		waiterNo = rs.getString(3);
		gdTotal = rs.getDouble(4);
		sbTotal = rs.getDouble(5);
		dis = rs.getDouble(6);
		bDate = rs.getString(7);
		paxNo = rs.getInt(8);
		operationType = rs.getString(9);
		customerCode = rs.getString(10);
		userName= rs.getString(11);
	    }
	    rs.close();
	    
	    sql = "  select b.strItemName,b.dblQuantity,b.dblAmount,b.dblRate " + "  from tblbillhd a,tblbilldtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
	    System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrListItem = new ArrayList<String>();
		String []arrQtyData=rs.getString(2).split("\\.");
	    int Qty=Integer.parseInt(arrQtyData[0]);
	   
		
		arrListItem.add(rs.getString(1));
		arrListItem.add(String.valueOf(Qty));
		arrListItem.add(rs.getString(3));
		arrListItem.add(rs.getString(4));
		arrListBillDtl.add(arrListItem);
	    }
	    rs.close();
	    
	    sql = "  select b.strModifierName,b.dblQuantity,b.dblAmount,b.dblRate " + "  from tblbillhd a,tblbillmodifierdtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
	    System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrListItem = new ArrayList<String>();
		String []arrQtyData=rs.getString(2).split("\\.");
	    int Qty=Integer.parseInt(arrQtyData[0]);
	   
		
		arrListItem.add(rs.getString(1));
		arrListItem.add(String.valueOf(Qty));
		arrListItem.add(rs.getString(3));
		arrListItem.add(rs.getString(4));
		arrListModifierBillDtl.add(arrListItem);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo ,c.strTaxDesc,b.dblTaxAmount from tblbillhd a,tblbilltaxdtl b,tbltaxhd c " + " where b.strTaxCode=c.strTaxCode " + "	and a.strBillNo=b.strBillNo " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrTaxListItem = new ArrayList<String>();
		arrTaxListItem.add(rs.getString(2));
		arrTaxListItem.add(rs.getString(3));
		arrTaxtBillDtl.add(arrTaxListItem);
	    }
	    rs.close();
	    
	    sql = " select a.strBillNo,b.dblSettlementAmt,b.dblPaidAmt,c.strSettelmentDesc " + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + " where a.strBillNo=b.strBillNo " + " and b.strSettlementCode=c.strSettelmentCode " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		ArrayList<String> arrSettleListItem = new ArrayList<String>();
		arrSettleListItem.add(rs.getString(4));
		arrSettleListItem.add(rs.getString(2));
		arrSettleBillDtl.add(arrSettleListItem);
		
	    }
	    rs.close();
	    
	    sql = " select a.strClientName,a.strAddressLine1,a.strAddressLine2, a.strAddressLine3,a.strCityName,a.intTelephoneNo, a.strEmail,a.strVatNo,a.strServiceTaxNo,a.strBillFooter,a.strPrintServiceTaxNo,a.strPrintVatNo,a.strMultipleBillPrinting ,a.strPrintInclusiveOfAllTaxesOnBill from tblsetup a ";
	    // System.out.println(sql);
	    
	    st.close();
	    st = cmsCon.createStatement();
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		clientName = rs.getString(1);
		addressLine1 = rs.getString(2);
		addressLine2 = rs.getString(3);
		addressLine3 = rs.getString(4);
		city = rs.getString(5);
		telephone = rs.getString(6);
		email = rs.getString(7);
		vatNo = rs.getString(8);
		serviceTaxNo = rs.getString(9);
		billFooter = rs.getString(10);
		printServiceTaxNo = rs.getString(11);
		printVatNo = rs.getString(12);
		printInclusiveOfAllTaxesOnBill= rs.getString(14);
		
	    }
	    rs.close();
	    
	    sql = "select b.strCustomerCode,b.strCustomerName " + " from tblbillhd a,tblcustomermaster b " + " where a.strCustomerCode=b.strCustomerCode " + " and a.strBillNo='" + billNo + "'";
	    rs = st.executeQuery(sql);
	    while (rs.next())
	    {
		memberCode = rs.getString(1);
		memberName = rs.getString(2);
		
	    }
	    rs.close();
	    
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		pw.println(funPrintTextWithAlignment("Home Delivery", 40, "Center"));
	    }
	    else if (operationType.equalsIgnoreCase("TakeAway"))
	    {
		pw.println(funPrintTextWithAlignment("Take Away", 40, "Center"));
	    }
	    
	    pw.println(funPrintTextWithAlignment("TAX INVOICE", 40, "Center"));
	    pw.println(funPrintTextWithAlignment(clientName, 40, "Center"));
	    
	    if (clientCode.equals("047.001"))
	    {
		if (posCode.equals("P03") || posCode.equals("P02"))
		{
		    pw.println(funPrintTextWithAlignment("SHRI SHAM CATERERS", 40, "Center"));
		    
		    String cAddr1 = "Flat No.7, Mon Amour,".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr1, 40, "Center"));
		    
		    String cAddr2 = "Thorat Colony,Prabhat Road,".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr2, 40, "Center"));
		    
		    String cAddr3 = " Erandwane, Pune 411 004.".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr3, 40, "Center"));
		    
		    String cAddr4 = "Approved Caterers of".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr4, 40, "Center"));
		    
		    String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB".toUpperCase();
		    pw.println(funPrintTextWithAlignment(cAddr5, 40, "Center"));
		}
	    }
	    else
	    {
		if (!addressLine1.isEmpty())
		{
		    if (addressLine1.length() > 40)
		    {
			pw.println(funPrintTextWithAlignment(addressLine1.substring(0, 40), 40, "Center"));
			pw.println(funPrintTextWithAlignment(addressLine1.substring(40), 40, "Center"));
		    }
		    
		    else
		    {
			pw.println(funPrintTextWithAlignment(addressLine1, 40, "Center"));
		    }
		}
		if (!addressLine2.isEmpty())
		{
		    if (addressLine2.length() > 40)
		    {
			pw.println(funPrintTextWithAlignment(addressLine2.substring(0, 40), 40, "Center"));
			pw.println(funPrintTextWithAlignment(addressLine2.substring(40), 40, "Center"));
		    }
		    else
		    {
			pw.println(funPrintTextWithAlignment(addressLine2, 40, "Center"));
		    }
		}
		
		if (!addressLine3.isEmpty())
		{
		    if (addressLine3.length() > 40)
		    {
			pw.println(funPrintTextWithAlignment(addressLine3.substring(0, 40), 40, "Center"));
			pw.println(funPrintTextWithAlignment(addressLine3.substring(40), 40, "Center"));
		    }
		    else
		    {
			pw.println(funPrintTextWithAlignment(addressLine3, 40, "Center"));
		    }
		}
		if (!city.isEmpty())
		{
		    pw.println(funPrintTextWithAlignment(city, 40, "Center"));
		}
	    }
	    if (!telephone.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("TEL NO.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(telephone, 26, "Left"));
		pw.println(" ");
	    }
	    if (!email.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("EMAIL ID", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(email, 26, "Left"));
		pw.println(" ");
	    }
	    
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		
		st = cmsCon.createStatement();
		
		sql = "  select a.strCustomerName,a.longMobileNo,a.strBuildingName from tblcustomermaster a  " + "  where a.strCustomerCode='" + customerCode + "' ";
		System.out.println(sql);
		rs = st.executeQuery(sql);
		while (rs.next())
		{
		    customerName = rs.getString(1);
		    mobileNo = rs.getString(2);
		    address = rs.getString(3);
		    
		}
		rs.close();
		st.close();
		pw.print(funPrintTextWithAlignment("Customer Name", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(customerName, 25, "Left"));
		pw.println(" ");
		pw.print(funPrintTextWithAlignment("Mobile No", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(mobileNo, 25, "Left"));
		pw.println(" ");
		pw.print(funPrintTextWithAlignment("Address ", 13, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(address, 25, "Left"));
		pw.println(" ");
	    }
	    
	    if (!tablName.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("TABLE NAME", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(tablName, 26, "Left"));
		pw.println(" ");
	    }
	    if (!waiterNo.isEmpty())
	    {
		pw.print(funPrintTextWithAlignment("STEWARD", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(waiterNo, 26, "Left"));
		pw.println(" ");
	    }
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("POS Name", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(posName, 26, "Left"));
	    pw.println(" ");
	    pw.print(funPrintTextWithAlignment("Pax No.", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(String.valueOf(paxNo), 26, "Left"));
	    pw.println(" ");
	    pw.print(funPrintTextWithAlignment("BILL NO.", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(billno, 26, "Left"));
	    pw.println(" ");
	    pw.print(funPrintTextWithAlignment("DATE & TIME", 12, "Left"));
	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
	    pw.print(funPrintTextWithAlignment(bDate, 26, "Left"));
	    if (!memberCode.isEmpty())
	    {
	    	pw.println(" ");
	    	pw.println(line);
		    pw.print(funPrintTextWithAlignment("Member Code", 12, "Left"));
		    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		    pw.print(funPrintTextWithAlignment(memberCode, 26, "Left"));
		    pw.println(" ");
		    pw.print(funPrintTextWithAlignment("Member Name", 12, "Left"));
		    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		    pw.print(funPrintTextWithAlignment(memberName, 26, "Left"));
	    }
	    
	    pw.println(" "); 
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("QTY ", 6, "RIGHT"));
	    pw.print(funPrintTextWithAlignment(" ITEMNAME", 20, "Left"));
	    pw.print(funPrintTextWithAlignment("AMT", 8, "RIGHT"));
	    pw.print(funPrintTextWithAlignment("RATE", 6, "RIGHT"));
	    pw.println(" ");
	    pw.println(line);
	    for (int cnt = 0; cnt < arrListBillDtl.size(); cnt++)
	    {
		ArrayList<String> items = arrListBillDtl.get(cnt);
		pw.print(funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
		pw.print(funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
		pw.print(funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
		pw.print(funPrintTextWithAlignment("" + items.get(3), 6, "RIGHT"));
		pw.println(" ");
	    }
	    if (arrListModifierBillDtl.size() > 0)
	    {
		for (int cnt = 0; cnt < arrListModifierBillDtl.size(); cnt++)
		{
		    ArrayList<String> items = arrListModifierBillDtl.get(cnt);
		    pw.print(funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
		    pw.print(funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
		    pw.print(funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
		    pw.print(funPrintTextWithAlignment("" + items.get(3), 6, "RIGHT"));
		    pw.println(" ");
		}
	    }
	    
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("SUB TOTAL", 32, "Left"));
	    pw.print(funPrintTextWithAlignment("" + sbTotal, 8, "RIGHT"));
	    pw.println(" ");
	    if(dis>0)
	    {
	    	pw.print(funPrintTextWithAlignment("DISCOUNT  ", 32, "Left"));
		    pw.print(funPrintTextWithAlignment("" + dis, 8, "RIGHT"));
		    pw.println(" "); 	
	    }
	    for (int cnt = 0; cnt < arrTaxtBillDtl.size(); cnt++)
	    {
		ArrayList<String> items = arrTaxtBillDtl.get(cnt);
		pw.print(funPrintTextWithAlignment("" + items.get(0) + " ", 32, "Left"));
		pw.print(funPrintTextWithAlignment("" + items.get(1), 8, "RIGHT"));
		pw.println(" ");
	    }
	    pw.println(line);
	    pw.print(funPrintTextWithAlignment("TOTAL(ROUNDED)", 32, "Left"));
	    pw.print(funPrintTextWithAlignment("" + gdTotal, 8, "RIGHT"));
	    pw.println(" ");
	    pw.println(line);
	    if(arrSettleBillDtl.size()>0)
	    {
	    	for (int cnt = 0; cnt < arrSettleBillDtl.size(); cnt++)
		    {
			ArrayList<String> items = arrSettleBillDtl.get(cnt);
			pw.println("---------Settlement Information--------");
			pw.print(funPrintTextWithAlignment(items.get(0), 17, "Left"));
			pw.print(funPrintTextWithAlignment("" + items.get(1), 23, "RIGHT"));
			pw.println(" ");
		    }
	    	pw.print(funPrintTextWithAlignment("PAID AMT", 17, "Left"));
			pw.print(funPrintTextWithAlignment("" +gdTotal , 23, "RIGHT"));
			pw.println(" ");
		    pw.println(line);
	    }
	    
	    if (printInclusiveOfAllTaxesOnBill.equals("Y"))
        {
	    	pw.println(funPrintTextWithAlignment("(INCLUSIVE OF ALL TAXES)", 40, "Center")); 
        }
	    pw.println(" ");
	    if (printVatNo.equalsIgnoreCase("Y"))
	    {
		pw.print(funPrintTextWithAlignment("Vat No.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(vatNo, 26, "Left"));
		pw.println(" ");
	    }
	    if (printServiceTaxNo.equalsIgnoreCase("Y"))
	    {
		pw.print(funPrintTextWithAlignment("Service Tax No.", 12, "Left"));
		pw.print(funPrintTextWithAlignment(":", 2, "Left"));
		pw.print(funPrintTextWithAlignment(serviceTaxNo, 26, "Left"));
		pw.println(" ");
	    }
	
	    int num = billFooter.trim().length() / 30;
        int num1 = billFooter.length() % 30;
        int cnt1 = 0;
        for (int cnt = 0; cnt < num; cnt++)
        {
            String footer = billFooter.substring(cnt1, (cnt1 + 30));
            footer = footer.replaceAll("\n", "");
            pw.println(funPrintTextWithAlignment("     " + footer.trim(), 40, "Center"));
            cnt1 += 30;
        }
        pw.println(" ");
        pw.println(funPrintTextWithAlignment(userName, 40, "Center"));
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    pw.println(" ");
	    
	    pw.flush();
	    pw.close();
	    
	    st.close();
	    st = cmsCon.createStatement();
	    sql = "select strBillPrinterPort,strAdvReceiptPrinterPort from tblposmaster where strPOSCode='" + posCode + "'";
	    rs = st.executeQuery(sql);
	    if (rs.next())
	    {
		funPrintTextFile(rs.getString(1), rs.getString(1), "Bill", "", "", multiBillPrint,"");
	    }
	    rs.close();
	    
	    cmsCon.close();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    arrListBillDtl = null;
	    arrTaxtBillDtl = null;
	    arrSettleBillDtl = null;
	}
    }
    
    public PrintWriter funGenerateTextFileForBillFormat11(String billNo,String posCode,String clientCode,String reprint)
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
    	Connection cmsCon = null;
    	Statement st = null;
    	String sql = null;
    	ResultSet rs = null;
    	PrintWriter pw=null;
    	ArrayList<ArrayList<String>> arrListBillDtl = new ArrayList<ArrayList<String>>();
    	ArrayList<ArrayList<String>> arrListModifierBillDtl = new ArrayList<ArrayList<String>>();
    	ArrayList<ArrayList<String>> arrTaxtBillDtl = new ArrayList<ArrayList<String>>();
    	ArrayList<ArrayList<String>> arrSettleBillDtl = new ArrayList<ArrayList<String>>();
    	
    	try
    	{
    	    cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
    	    st = cmsCon.createStatement();
    	    funCreateTempFolder();
    	    String filePath = System.getProperty("user.dir");
    	    File textBill = new File(filePath + "/Temp/TempBill.txt");
    	    pw = new PrintWriter(textBill);
    	    String billno = " ";
    	    String operationType = " ";
    	    String waiterNo = "";
    	    String tablName = "";
    	    Double gdTotal = 0.0;
    	    Double sbTotal = 0.0;
    	    Double dis = 0.0;
    	    String bDate = "";
    	    String clientName = "";
    	    String addressLine1 = "";
    	    String addressLine2 = "";
    	    String addressLine3 = "";
    	    String city = "";
    	    String telephone = "";
    	    String email = "";
    	    String vatNo = "";
    	    String serviceTaxNo = "";
    	    String billFooter = "";
    	    String printVatNo = "";
    	    String printServiceTaxNo = "";
    	    String multiBillPrint = "N";
    	    String posName = "";
    	    int paxNo = 0;
    	    String memberCode = "";
    	    String memberName = "";
    	    String customerCode = "";
    	    String customerName = "";
    	    String mobileNo = "";
    	    String address = "";
    	    String printInclusiveOfAllTaxesOnBill="";
    	    
    	    sql = "select strPosName from tblPOSMaster where strPosCode='" + posCode + "'";
    	    rs = st.executeQuery(sql);
    	    if (rs.next())
    	    {
    		posName = rs.getString(1);
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strBillNo,ifnull(b.strTableName,''),ifnull(c.strWShortName,''),a.dblGrandTotal,a.dblSubTotal,a.dblDiscountAmt,a.dteBillDate,a.intPaxNo,a.strOperationType,a.strCustomerCode " + " from tblbillhd a left outer join tbltablemaster b " + " on a.strTableNo=b.strTableNo " + " left outer join tblwaitermaster c " + " on a.strWaiterNo=c.strWaiterNo " + " where a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
    	    // System.out.println(sql);
    	    
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		billno = rs.getString(1);
    		tablName = rs.getString(2);
    		waiterNo = rs.getString(3);
    		gdTotal = rs.getDouble(4);
    		sbTotal = rs.getDouble(5);
    		dis = rs.getDouble(6);
    		bDate = rs.getString(7);
    		paxNo = rs.getInt(8);
    		operationType = rs.getString(9);
    		customerCode = rs.getString(10);
    	    }
    	    rs.close();
    	    
    	    sql = "  select b.strItemName,b.dblQuantity,b.dblRate,b.dblAmount " + "  from tblbillhd a,tblbilldtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
    	    System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrListItem = new ArrayList<String>();
    		arrListItem.add(rs.getString(1));
    		arrListItem.add(rs.getString(2));
    		arrListItem.add(rs.getString(3));
    		arrListItem.add(rs.getString(4));
    		arrListBillDtl.add(arrListItem);
    	    }
    	    rs.close();
    	    
    	    sql = "  select b.strModifierName,b.dblQuantity,b.dblAmount " + "  from tblbillhd a,tblbillmodifierdtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
    	    System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrListItem = new ArrayList<String>();
    		arrListItem.add(rs.getString(1));
    		arrListItem.add(rs.getString(2));
    		arrListItem.add(rs.getString(3));
    		arrListModifierBillDtl.add(arrListItem);
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strBillNo ,c.strTaxDesc,b.dblTaxAmount from tblbillhd a,tblbilltaxdtl b,tbltaxhd c " + " where b.strTaxCode=c.strTaxCode " + "	and a.strBillNo=b.strBillNo " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
    	    // System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrTaxListItem = new ArrayList<String>();
    		arrTaxListItem.add(rs.getString(2));
    		arrTaxListItem.add(rs.getString(3));
    		arrTaxtBillDtl.add(arrTaxListItem);
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strBillNo,b.dblSettlementAmt,b.dblPaidAmt,c.strSettelmentDesc " + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + " where a.strBillNo=b.strBillNo " + " and b.strSettlementCode=c.strSettelmentCode " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
    	    // System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrSettleListItem = new ArrayList<String>();
    		arrSettleListItem.add(rs.getString(4));
    		arrSettleListItem.add(rs.getString(2));
    		arrSettleBillDtl.add(arrSettleListItem);
    		
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strClientName,a.strAddressLine1,a.strAddressLine2, a.strAddressLine3,a.strCityName,a.intTelephoneNo, a.strEmail,a.strVatNo,a.strServiceTaxNo,a.strBillFooter,a.strPrintServiceTaxNo,a.strPrintVatNo,a.strMultipleBillPrinting ,a.strPrintInclusiveOfAllTaxesOnBill from tblsetup a ";
    	    // System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		clientName = rs.getString(1);
    		addressLine1 = rs.getString(2);
    		addressLine2 = rs.getString(3);
    		addressLine3 = rs.getString(4);
    		city = rs.getString(5);
    		telephone = rs.getString(6);
    		email = rs.getString(7);
    		vatNo = rs.getString(8);
    		serviceTaxNo = rs.getString(9);
    		billFooter = rs.getString(10);
    		printServiceTaxNo = rs.getString(11);
    		printVatNo = rs.getString(12);
    		printInclusiveOfAllTaxesOnBill= rs.getString(14);
    		
    	    }
    	    rs.close();
    	    
    	    sql = "select b.strCustomerCode,b.strCustomerName " + " from tblbillhd a,tblcustomermaster b " + " where a.strCustomerCode=b.strCustomerCode " + " and a.strBillNo='" + billNo + "'";
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		memberCode = rs.getString(1);
    		memberName = rs.getString(2);
    		
    	    }
    	    rs.close();
    	    if ("Y".equalsIgnoreCase(reprint))
            {
    	    	pw.print(funPrintTextWithAlignment("[DUPLICATE]", 20, "Center"));
    	    	pw.println(" ");
    	        
            }
    	    if (operationType.equalsIgnoreCase("HomeDelivery"))
    	    {
    	    pw.println();
    		pw.print(funPrintTextWithAlignment("Home Delivery", 20, "Center"));
    	    }
    	    else if (operationType.equalsIgnoreCase("TakeAway"))
    	    {
        	pw.println();
    		pw.print(funPrintTextWithAlignment("Take Away", 20, "Center"));
    	    }
    	    pw.println();
    	    pw.println(funPrintTextWithAlignment("TAX INVOICE", 20, "Center"));
    	    pw.print(funPrintTextWithAlignment(clientName, 20, "Center"));
    	    
    	    if (clientCode.equals("047.001"))
    	    {
    		if (posCode.equals("P03") || posCode.equals("P02"))
    		{
        	    pw.println("");
    		    pw.println(funPrintTextWithAlignment("SHRI SHAM CATERERS", 20, "Center"));
    		    
    		    String cAddr1 = "Flat No.7, Mon Amour,".toUpperCase();
    		    pw.println(funPrintTextWithAlignment(cAddr1, 20, "Center"));
    		    
    		    String cAddr2 = "Thorat Colony,Prabhat Road,".toUpperCase();
    		    pw.println(funPrintTextWithAlignment(cAddr2, 20, "Center"));
    		    
    		    String cAddr3 = " Erandwane, Pune 411 004.".toUpperCase();
    		    pw.println(funPrintTextWithAlignment(cAddr3, 20, "Center"));
    		    
    		    String cAddr4 = "Approved Caterers of".toUpperCase();
    		    pw.println(funPrintTextWithAlignment(cAddr4, 20, "Center"));
    		    
    		    String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB".toUpperCase();
    		    pw.println(funPrintTextWithAlignment(cAddr5, 20, "Center"));
    		}
    	    }
    	    else
    	    {
    		if (!addressLine1.isEmpty())
    		{
    		    if (addressLine1.length() > 20)
    		    {
    	    	pw.println("");
    			pw.println(funPrintTextWithAlignment(addressLine1.substring(0, 20), 20, "Center"));
    			pw.print(funPrintTextWithAlignment(addressLine1.substring(20), 20, "Center"));
    		    }
    		    
    		    else
    		    {
    	    	pw.println("");
    			pw.print(funPrintTextWithAlignment(addressLine1, 20, "Center"));
    		    }
    		}
    		if (!addressLine2.isEmpty())
    		{
    		    if (addressLine2.length() > 20)
    		    {
    	    	pw.println("");
    			pw.println(funPrintTextWithAlignment(addressLine2.substring(0, 20), 20, "Center"));
    			pw.print(funPrintTextWithAlignment(addressLine2.substring(20), 20, "Center"));
    		    }
    		    else
    		    {
    	    	pw.println("");
    			pw.print(funPrintTextWithAlignment(addressLine2, 20, "Center"));
    		    }
    		}
    		
    		if (!addressLine3.isEmpty())
    		{
    		    if (addressLine3.length() > 20)
    		    {
    	    	pw.println("");
    			pw.println(funPrintTextWithAlignment(addressLine3.substring(0, 20), 20, "Center"));
    			pw.print(funPrintTextWithAlignment(addressLine3.substring(20), 20, "Center"));
    		    }
    		    else
    		    {
		    	pw.println("");   	
    			pw.print(funPrintTextWithAlignment(addressLine3, 20, "Center"));
    		    }
    		}
    		if (!city.isEmpty())
    		{
    			pw.println("");
    		    pw.print(funPrintTextWithAlignment(city, 20, "Center"));
    		}
    	    }
    	    if (!telephone.isEmpty())
    	    {
    			if(telephone.contains(",")){
    	    		String[] arrTelNo = telephone.split(",");
                    for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                    {
                    	pw.println("");
                    	pw.print(funPrintTextWithAlignment(arrTelNo[cnt], 20, "Left"));
                        
                    }
    	    	}else if(telephone.contains("/")){
    	    		String[] arrTelNo = telephone.split(",");
                    for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                    {
                    	pw.println("");
                        pw.print(funPrintTextWithAlignment(arrTelNo[cnt], 20, "Left"));
                        
                    }
    	    	}
    		}
    	    if (!email.isEmpty())
    	    {
    	    	if(email.contains(",")){
    	    		String[] arremail = email.split(",");
                    for (int cnt = 0; cnt < arremail.length; cnt++)
                    {
                        pw.println(funPrintTextWithAlignment(arremail[cnt], 20, "Left"));
                    }
    	    	}else if(email.contains(",")){
    	    		String[] arremail = email.split(",");
                    for (int cnt = 0; cnt < arremail.length; cnt++)
                    {
                        pw.println(funPrintTextWithAlignment(arremail[cnt], 20, "Left"));
                    }
    	    	}
    	    }
    	    
    	    if (operationType.equalsIgnoreCase("HomeDelivery"))
    	    {
    		
    		st = cmsCon.createStatement();
    		
    		sql = "  select a.strCustomerName,a.longMobileNo,a.strBuildingName from tblcustomermaster a  " + "  where a.strCustomerCode='" + customerCode + "' ";
    		System.out.println(sql);
    		rs = st.executeQuery(sql);
    		while (rs.next())
    		{
    		    customerName = rs.getString(1);
    		    mobileNo = rs.getString(2);
    		    address = rs.getString(3);
    		    
    		}
    		rs.close();
    		st.close();
    		pw.println("");
    		pw.println(funPrintTextWithAlignment("HomeDelivery", 20, "Center"));
    		pw.println(funPrintTextWithAlignment("Customer Name :", 20, "Left"));
    		pw.println(funPrintTextWithAlignment(" "+customerName, 20, "Left"));
    		
    		pw.println(funPrintTextWithAlignment("Mobile No :", 20, "Left"));
    		pw.println(funPrintTextWithAlignment(" "+mobileNo, 20, "Left"));
    		pw.println(funPrintTextWithAlignment("Address :", 20, "Left"));
    		pw.print(funPrintTextWithAlignment(" "+address, 20, "Left"));
    		}
    	    
    	    if (!tablName.isEmpty())
    	    {
    	    pw.println("");
    		pw.print(funPrintTextWithAlignment("TABLE NAME:", 12, "Left"));
    		pw.print(funPrintTextWithAlignment(tablName, 10, "Left"));
    		
    	    }
    	    
    	    if (!waiterNo.isEmpty())
    	    {
    	    	
    	    pw.println();
    		pw.println(funPrintTextWithAlignment("STEWARD :", 12, "Left"));
    		pw.print(funPrintTextWithAlignment(waiterNo, 8, "Left"));
    		
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.print(funPrintTextWithAlignment("POS Name", 8, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(funPrintTextWithAlignment(posName, 10, "Left"));
    	    pw.println();
    	    pw.print(funPrintTextWithAlignment("Pax No.", 8, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(funPrintTextWithAlignment(String.valueOf(paxNo), 10, "Left"));
    	    
    	    pw.println();
    	    pw.print(funPrintTextWithAlignment("BILL NO.", 8, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(funPrintTextWithAlignment(billno, 10, "Left"));
    	    pw.println();
    	    pw.print(funPrintTextWithAlignment("DATE ", 8, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(funPrintTextWithAlignment(bDate, 10, "Left"));
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    /*pw.println(funPrintTextWithAlignment("Member Code", 12, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(funPrintTextWithAlignment(memberCode, 10, "Left"));
    	    
    	    pw.print(funPrintTextWithAlignment("Member Name", 12, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
    	    pw.print(funPrintTextWithAlignment(memberName, 26, "Left"));
    	    */
    	    
    	    pw.println(funPrintTextWithAlignment("ITEM NAME ", 20, "Left"));
    	    pw.print(funPrintTextWithAlignment(" QTY   RATE   AMOUNT", 20, "Left"));
    	    
    	    //pw.println("________________________________________");
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    for (int cnt = 0; cnt < arrListBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrListBillDtl.get(cnt);
    		pw.println();
    		if(items.get(0).length()>20){
    			pw.println(funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
    			pw.print(funPrintTextWithAlignment("" + items.get(0).substring(20,items.get(0).length()), 20, "Left"));
    		}else{
    			
    			pw.print(funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
    		}
    			pw.println();
	    		pw.print(funPrintTextWithAlignment(" " + items.get(1) + " ", 6, "RIGHT"));
	    		pw.print(funPrintTextWithAlignment(items.get(2), 7, "RIGHT"));
	    		pw.print(funPrintTextWithAlignment(items.get(3), 7, "RIGHT"));
    		}
    	    if (arrListModifierBillDtl.size() > 0)
    	    {
    		for (int cnt = 0; cnt < arrListModifierBillDtl.size(); cnt++)
    		{
    		    ArrayList<String> items = arrListModifierBillDtl.get(cnt);
    		    if(items.get(0).length()>20){
    		    	pw.println();
        			pw.println(funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
        			pw.print(funPrintTextWithAlignment("" + items.get(0).substring(20,items.get(0).length()), 20, "Left"));
        		}else{
        			pw.println();
        			pw.print(funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
        		}
    		    	pw.println();
    	    		pw.print(funPrintTextWithAlignment(" " + items.get(1) + " ", 6, "RIGHT"));
    	    		pw.print(funPrintTextWithAlignment(items.get(2), 7, "RIGHT"));
    	    		pw.print(funPrintTextWithAlignment(items.get(3), 7, "RIGHT"));
    		}
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.print(funPrintTextWithAlignment("SUB TOTAL", 10, "Left"));
    	    pw.print(funPrintTextWithAlignment("" + sbTotal, 10, "RIGHT"));
    	    pw.println();
    	    if(dis>0)
    	    {
    	    	pw.print(funPrintTextWithAlignment("DISCOUNT  ", 32, "Left"));
    		    pw.print(funPrintTextWithAlignment("" + dis, 8, "RIGHT"));
    		    pw.println(" "); 	
    	    }
    	    for (int cnt = 0; cnt < arrTaxtBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrTaxtBillDtl.get(cnt);
    		pw.println();
    		pw.print(funPrintTextWithAlignment(items.get(0), 20, "Left"));
    		pw.print(funPrintTextWithAlignment(" " + items.get(1), 20, "RIGHT"));
    		
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.print(funPrintTextWithAlignment("TOTAL(ROUNDED)", 12, "Left"));
    	    pw.print(funPrintTextWithAlignment(" " + gdTotal, 8, "RIGHT"));
    	    
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    for (int cnt = 0; cnt < arrSettleBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrSettleBillDtl.get(cnt);
    		/*pw.println("---------Settlement Information--------");*/
    		pw.println();
    		pw.print(funPrintTextWithAlignment(items.get(0), 10, "Left"));
    		pw.print(funPrintTextWithAlignment("" + items.get(1), 10, "RIGHT"));
    		
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.println(funPrintTextWithAlignment("(INCLUSIVE OF ", 20, "Center"));
    	    pw.println(funPrintTextWithAlignment(" ALL TAXES )", 20, "Center"));
    	    
    	    
    	    if (printVatNo.equalsIgnoreCase("Y"))
    	    {
    		pw.println(funPrintTextWithAlignment("Vat No. :", 8, "Left"));
    		pw.println(funPrintTextWithAlignment("  "+vatNo, 20, "Left"));
    		
    	    }
    	    if (printServiceTaxNo.equalsIgnoreCase("Y"))
    	    {
    		pw.println(funPrintTextWithAlignment("Service Tax No. :", 20, "Left"));
    		pw.println(funPrintTextWithAlignment("  "+serviceTaxNo, 20, "Left"));
    		
    	    }
    	    
    	    if(billFooter.length()>20){
    	    	List listTextToPrint = funGetTextWithSpecifiedSize(billFooter.trim(), 2);
                for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                {
                	pw.println();
                	pw.print(funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
                }
    	    }
    	    
    	    pw.println(" ");
    	    pw.println(" ");
    	    pw.println(" ");
    	    pw.println(" ");
    	    pw.println(" ");
    	    pw.println(" ");
    	    pw.println("m");
    	    
    	    pw.flush();
    	    pw.close();
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    sql = "select strBillPrinterPort,strAdvReceiptPrinterPort from tblposmaster where strPOSCode='" + posCode + "'";
    	    rs = st.executeQuery(sql);
    	    if (rs.next())
    	    {
    		funPrintTextFile(rs.getString(1), rs.getString(1), "Bill", "", "", multiBillPrint,"");
    	    }
    	    rs.close();
    	    
    	    cmsCon.close();
    	}
    	catch (Exception e)
    	{
    	    e.printStackTrace();
    	}
    	finally
    	{
    	    arrListBillDtl = null;
    	    arrTaxtBillDtl = null;
    	    arrSettleBillDtl = null;
    	}
        
    	return pw;
    	
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
	    
	    if (totalSpace < 0)
	    {
		sbText.append(text.substring(0, totalLength));
	    }
	    else
	    {
		sbText.append(text);
		
		for (int i = 0; i < totalSpace; i++)
		{
		    sbText.append(" ");
		}
	    }
	    
	}
	else
	{
	    sbText.setLength(0);
	    int textLength = text.length();
	    int totalSpace = (totalLength - textLength);
	    
	    if (totalSpace < 0)
	    {
		sbText.append(text.substring(0, totalLength));
	    }
	    else
	    {
		for (int i = 0; i < totalSpace; i++)
		{
		    sbText.append(" ");
		}
		sbText.append(text);
	    }
	    
	}
	
	return sbText.toString();
    }
    
    public void funPrintTextFile(String primaryPrinterName, String secPrinterName, String type, String multipleKOTPrint, String printKOTYN, String multiBillPrint,String KOTType)
    {
	
	try
	{
	    String reportName = "";
	    
	    if (type.equalsIgnoreCase("dayend"))
	    {
		reportName = "dayend";
	    }
	    else if (type.equalsIgnoreCase("Adv Receipt"))
	    {
		reportName = "Adv Receipt";
	    }
	    else
	    {
		reportName = "bill";
	    }
	    
	    funPrintBillWindows(reportName, primaryPrinterName);
		if (!type.equalsIgnoreCase("dayend"))
	    {
		  if (multiBillPrint.equals("Y"))
		   {
		     funPrintBillWindows(reportName, primaryPrinterName);
		   }
		}
	    
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    
    
    public String funPrintKOTTextFile(String primaryPrinterName, String secPrinterName, String type, String multipleKOTPrint, String printKOTYN, String multiBillPrint,String KOTType)
    {
    	String result="";
	    
	   try
		{
		    if (type.equalsIgnoreCase("kot"))
		    {
				if (printKOTYN.equals("Y"))
				{
				    result=funPrintKOTWindows(primaryPrinterName, secPrinterName,KOTType);
				    if (multipleKOTPrint.equals("Y"))
				    {
				      result=funPrintKOTWindows(primaryPrinterName, secPrinterName,KOTType);
				    }
				}
				else
				{
					result="Kot Printing Not Available!!!";
				}
		    }
		    
		    
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
	   return result;
    }
    
    
    
    
    
    
    
    private String funPrintKOTWindows(String primaryPrinterName, String secPrinterName,String KOTType)
    {
    	String result="";
    	String filePath = System.getProperty("user.dir");
    	String filename ="";
    	
    	if(KOTType.equals("MasterKOT"))
    	{
    		filename = (filePath + "/Temp/Temp_Master_KOT.txt");
    	}
    	else if(KOTType.equals("CheckKOT"))
    	{
    		filename = (filePath + "/Temp/Temp_Check_KOT.txt");
    	}
    	else if(KOTType.equals("ConsolidateKOT"))
    	{
    		filename = (filePath + "/Temp/Temp_Consolidated_KOT.txt");
    	}
    	else if(KOTType.equals("KOT"))
    	{
    		filename = (filePath + "/Temp/Temp_KOT.txt");
    	}
	
		try
		{
		    int printerIndex = 0;
		    String printerStatus = "Not Found";
		    // System.out.println("Primary Name="+primaryPrinterName);
		    // System.out.println("Sec Name="+secPrinterName);
		    
		    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		    primaryPrinterName = primaryPrinterName.replaceAll("#", "\\\\");
		    secPrinterName = secPrinterName.replaceAll("#", "\\\\");
		
		    
		    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		    for (int i = 0; i < printService.length; i++)
		    {
				//System.out.println("Service=" + printService[i].getName() + "\tPrim P=" + primaryPrinterName);
				String printerServiceName = printService[i].getName();
				
				if (primaryPrinterName.equalsIgnoreCase(printerServiceName))
				{
				    System.out.println("Print send on \t" + primaryPrinterName + " printer ");
				    printerIndex = i;
				    printerStatus = "Found";
				    break;
				}
				else
				{
					// System.out.println("Primary Printer not found");
					 result="Primary Not Found";
				}
		    }
		    
		    if (printerStatus.equals("Found"))
		    {
		    	DocPrintJob job = printService[printerIndex].createPrintJob();
				FileInputStream fis = new FileInputStream(filename);
				DocAttributeSet das = new HashDocAttributeSet();
				Doc doc = new SimpleDoc(fis, flavor, das);
				job.print(doc, pras);
				System.out.println("Successfully Print on " + primaryPrinterName);
				String printerInfo = "";
				
				 result="Primary Found";
			
				 result=funPrintOnSecPrinter(secPrinterName, filename,result); //for printing on both printer
		    }
		    else
		    {
		    	result=funPrintOnSecPrinter(secPrinterName, filename,result);
		    }
		    
		}
		catch (Exception e)
		{
		    
		    e.printStackTrace();
		    try
		    {
		    	result=funPrintOnSecPrinter(secPrinterName, filename,result);
		    }
		    catch (Exception ex)
		    {
			ex.printStackTrace();
		    }
		}
		
		return result;
    }
    
    private String funPrintOnSecPrinter(String secPrinterName, String fileName,String primaryPrinterStatus) throws Exception
    {
    	String result="";
		String printerStatus = "Not Found";
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		int printerIndex = 0;
		for (int i = 0; i < printService.length; i++)
		{
		    //System.out.println("Service="+printService[i].getName()+"\tSec P="+secPrinterName);
		    String printerServiceName = printService[i].getName();
		    
		    if (secPrinterName.equalsIgnoreCase(printerServiceName))
		    {
				System.out.println("Sec Printer=" + secPrinterName + "found");
				printerIndex = i;
				printerStatus = "Found";
				break;
		    }
		    else
			{
		    	// System.out.println("Secondary Printer not found");
				 result=primaryPrinterStatus+"#"+"Secondary Not Found";
			}
		}
		if (printerStatus.equals("Found"))
		{
		    String printerInfo = "";
		    DocPrintJob job = printService[printerIndex].createPrintJob();
		    FileInputStream fis = new FileInputStream(fileName);
		    DocAttributeSet das = new HashDocAttributeSet();
		    Doc doc = new SimpleDoc(fis, flavor, das);
		    job.print(doc, pras);
		    
		    result=primaryPrinterStatus+"#"+"Secondary Found";
		}
		else
		{
		    System.out.println("funPrintOnSecPrinter = Printer Not Found " + secPrinterName);
	    }
	
		return result;
		
    }
    
    private void funPrintCheckKOTWindows(String printerName)
    {
	try
	{
	    int printerIndex = 0;
	    String filePath = System.getProperty("user.dir");
	    String filename = (filePath + "/Temp/Temp_KOT.txt");
	    
	    String billPrinterName = printerName;
	    billPrinterName = billPrinterName.replaceAll("#", "\\\\");
	    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
	    for (int i = 0; i < printService.length; i++)
	    {
		System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
		if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
		{
		    System.out.println("Bill Printer Sel=" + billPrinterName);
		    printerIndex = i;
		    break;
		}
	    }
	    
	    DocPrintJob job = printService[printerIndex].createPrintJob();
	    FileInputStream fis = new FileInputStream(filename);
	    DocAttributeSet das = new HashDocAttributeSet();
	    Doc doc = new SimpleDoc(fis, flavor, das);
	    job.print(doc, pras);
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    /**
     * printBillWindows() method print to Default Printer.
     */
    private void funPrintBillWindows(String type, String billPrinterName)
    {
	 try
	 {
	    System.out.println("Print Bill");
	    String filePath = System.getProperty("user.dir");
	    String filename = "";
	    
	    if (type.equalsIgnoreCase("bill"))
	    {
		filename = (filePath + "/Temp/TempBill.txt");
	    }
	    else if (type.equalsIgnoreCase("dayend"))
	    {
		filename = (filePath + "/Temp/Temp_DayEndReport.txt");
	    }
	    /*
	     * else if (type.equalsIgnoreCase("Adv Receipt")) { filename =
	     * (filePath + "/Temp/Temp_Bill.txt");
	     * billPrinterName=clsGlobalVarClass.gAdvReceiptPrinterPort; }
	     */
	    
	    billPrinterName = billPrinterName.replaceAll("#", "\\\\");
	    int printerIndex = 0;
	    PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
	    for (int i = 0; i < printService.length; i++)
	    {
		//System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
		if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
		{
		    System.out.println("Bill Print on \t"+billPrinterName);
		    printerIndex = i;
		    break;
		}
	    }
	    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
	    DocPrintJob job = printService[printerIndex].createPrintJob();
	    FileInputStream fis = new FileInputStream(filename);
	    DocAttributeSet das = new HashDocAttributeSet();
	    Doc doc = new SimpleDoc(fis, flavor, das);
	    job.print(doc, printerReqAtt);
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    private List funGetTextWithSpecifiedSize(String text, int size)
    {
        List listText = new ArrayList();
        try
        {
            System.out.println(text);
            if (size == 2)
            {
                StringBuilder sbText = new StringBuilder();
                StringBuilder sbTempText = new StringBuilder();
                String[] arrTextToPrint = text.split(" ");
                for (int cnt = 0; cnt < arrTextToPrint.length; cnt++)
                {
                    sbTempText.append(arrTextToPrint[cnt] + " ");
                    if (sbTempText.length() > 40)
                    {
                        String tempText = sbText.substring(0, sbText.lastIndexOf(" "));
                        System.out.println("Add To List " + tempText);
                        if (!tempText.isEmpty())
                        {
                            listText.add(tempText);
                        }
                        sbText.setLength(0);
                        sbTempText.setLength(0);

                        sbTempText.append(arrTextToPrint[cnt] + " ");
                        sbText.append(arrTextToPrint[cnt] + " ");
                    }
                    else
                    {
                        sbText.append(arrTextToPrint[cnt] + " ");
                    }

                    if ((cnt == arrTextToPrint.length - 1) && sbTempText.length() > 0)
                    {
                        listText.add(sbText);
                    }
                }
            }
            System.out.println("List= " + listText);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return listText;
    }
    
    
    public StringBuilder funGenerateAndPrintBillForBlueToothPrinter(String billNo, String posCode, String clientCode) throws Exception
    {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		String sql = null;
		String billFormat = "";
		ResultSet rs = null;
		StringBuilder sbPrintBill = new StringBuilder();
		cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
		st = cmsCon.createStatement();
		sql = "select a.strBillFormatType from tblsetup a;";
		rs = st.executeQuery(sql);
		if (rs.next())
		{
		    billFormat = rs.getString(1);
		}
		rs.close();
		sbPrintBill=funGenerateTextFileForBillFormat11ForBluetooth(billNo, posCode, clientCode);
		
		return sbPrintBill;
    }
    
    
    
    public StringBuilder funGenerateTextFileForBillFormat11ForBluetooth(String billNo,String posCode,String clientCode)
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
    	Connection cmsCon = null;
    	Statement st = null;
    	String sql = null;
    	ResultSet rs = null;
    	ArrayList<ArrayList<String>> arrListBillDtl = new ArrayList<ArrayList<String>>();
    	ArrayList<ArrayList<String>> arrListModifierBillDtl = new ArrayList<ArrayList<String>>();
    	ArrayList<ArrayList<String>> arrTaxtBillDtl = new ArrayList<ArrayList<String>>();
    	ArrayList<ArrayList<String>> arrSettleBillDtl = new ArrayList<ArrayList<String>>();
    	StringBuilder sbPrintBill = new StringBuilder();
    	
    	try
    	{
    	    cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
    	    st = cmsCon.createStatement();
    	    funCreateTempFolder();
    	    String filePath = System.getProperty("user.dir");
    	    File textBill = new File(filePath + "/Temp/TempBill.txt");
    	    String billno = " ";
    	    String operationType = " ";
    	    String waiterNo = "";
    	    String tablName = "";
    	    Double gdTotal = 0.0;
    	    Double sbTotal = 0.0;
    	    Double dis = 0.0;
    	    String bDate = "";
    	    String clientName = "";
    	    String addressLine1 = "";
    	    String addressLine2 = "";
    	    String addressLine3 = "";
    	    String city = "";
    	    String telephone = "";
    	    String email = "";
    	    String vatNo = "";
    	    String serviceTaxNo = "";
    	    String billFooter = "";
    	    String printVatNo = "";
    	    String printServiceTaxNo = "";
    	    String multiBillPrint = "N";
    	    String posName = "";
    	    int paxNo = 0;
    	    String memberCode = "";
    	    String memberName = "";
    	    String customerCode = "";
    	    String customerName = "";
    	    String mobileNo = "";
    	    String address = "";
    	    
    	    sql = "select strPosName from tblPOSMaster where strPosCode='" + posCode + "'";
    	    rs = st.executeQuery(sql);
    	    if (rs.next())
    	    {
    		posName = rs.getString(1);
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strBillNo,ifnull(b.strTableName,''),ifnull(c.strWShortName,''),a.dblGrandTotal,a.dblSubTotal,a.dblDiscountAmt,a.dteBillDate,a.intPaxNo,a.strOperationType,a.strCustomerCode " + " from tblbillhd a left outer join tbltablemaster b " + " on a.strTableNo=b.strTableNo " + " left outer join tblwaitermaster c " + " on a.strWaiterNo=c.strWaiterNo " + " where a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
    	    // System.out.println(sql);
    	    
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		billno = rs.getString(1);
    		tablName = rs.getString(2);
    		waiterNo = rs.getString(3);
    		gdTotal = rs.getDouble(4);
    		sbTotal = rs.getDouble(5);
    		dis = rs.getDouble(6);
    		bDate = rs.getString(7);
    		paxNo = rs.getInt(8);
    		operationType = rs.getString(9);
    		customerCode = rs.getString(10);
    	    }
    	    rs.close();
    	    
    	    sql = "  select b.strItemName,b.dblQuantity,b.dblRate,b.dblAmount " + "  from tblbillhd a,tblbilldtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
    	    System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrListItem = new ArrayList<String>();
    		arrListItem.add(rs.getString(1));
    		arrListItem.add(rs.getString(2));
    		arrListItem.add(rs.getString(3));
    		arrListItem.add(rs.getString(4));
    		arrListBillDtl.add(arrListItem);
    	    }
    	    rs.close();
    	    
    	    sql = "  select b.strModifierName,b.dblQuantity,b.dblAmount " + "  from tblbillhd a,tblbillmodifierdtl b,tblitemmaster c " + "  where a.strBillNo=b.strBillNo " + "  and b.strItemCode=c.strItemCode " + "  and a.strBillNo='" + billNo + "' " + "   and a.strPosCode='" + posCode + "' ";
    	    System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrListItem = new ArrayList<String>();
    		arrListItem.add(rs.getString(1));
    		arrListItem.add(rs.getString(2));
    		arrListItem.add(rs.getString(3));
    		arrListModifierBillDtl.add(arrListItem);
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strBillNo ,c.strTaxDesc,b.dblTaxAmount from tblbillhd a,tblbilltaxdtl b,tbltaxhd c " + " where b.strTaxCode=c.strTaxCode " + "	and a.strBillNo=b.strBillNo " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
    	    // System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrTaxListItem = new ArrayList<String>();
    		arrTaxListItem.add(rs.getString(2));
    		arrTaxListItem.add(rs.getString(3));
    		arrTaxtBillDtl.add(arrTaxListItem);
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strBillNo,b.dblSettlementAmt,b.dblPaidAmt,c.strSettelmentDesc " + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c " + " where a.strBillNo=b.strBillNo " + " and b.strSettlementCode=c.strSettelmentCode " + " and a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
    	    // System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		ArrayList<String> arrSettleListItem = new ArrayList<String>();
    		arrSettleListItem.add(rs.getString(4));
    		arrSettleListItem.add(rs.getString(2));
    		arrSettleBillDtl.add(arrSettleListItem);
    		
    	    }
    	    rs.close();
    	    
    	    sql = " select a.strClientName,a.strAddressLine1,a.strAddressLine2, " + " a.strAddressLine3,a.strCityName,a.intTelephoneNo, " + " a.strEmail,a.strVatNo,a.strServiceTaxNo,a.strBillFooter,a.strPrintServiceTaxNo,a.strPrintVatNo" + " ,a.strMultipleBillPrinting " + " from tblsetup a ";
    	    // System.out.println(sql);
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		clientName = rs.getString(1);
    		addressLine1 = rs.getString(2);
    		addressLine2 = rs.getString(3);
    		addressLine3 = rs.getString(4);
    		city = rs.getString(5);
    		telephone = rs.getString(6);
    		email = rs.getString(7);
    		vatNo = rs.getString(8);
    		serviceTaxNo = rs.getString(9);
    		billFooter = rs.getString(10);
    		printServiceTaxNo = rs.getString(11);
    		printVatNo = rs.getString(12);
    		
    	    }
    	    rs.close();
    	    
    	    sql = "select b.strCustomerCode,b.strCustomerName " + " from tblbillhd a,tblcustomermaster b " + " where a.strCustomerCode=b.strCustomerCode " + " and a.strBillNo='" + billNo + "'";
    	    rs = st.executeQuery(sql);
    	    while (rs.next())
    	    {
    		memberCode = rs.getString(1);
    		memberName = rs.getString(2);
    		
    	    }
    	    rs.close();
    	    
    	    if (operationType.equalsIgnoreCase("HomeDelivery"))
    	    {
    	    	sbPrintBill.append("\n");
    	    	sbPrintBill.append(funPrintTextWithAlignment("Home Delivery", 40, "Center"));
    	    }
    	    else if (operationType.equalsIgnoreCase("TakeAway"))
    	    {
    	    	sbPrintBill.append("\n");
    	    	sbPrintBill.append(funPrintTextWithAlignment("Take Away", 40, "Center"));
    	    }
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append(funPrintTextWithAlignment("TAX INVOICE", 40, "Center"));
    	    if(clientName.length()>40)
		    {
		    	sbPrintBill.append("\n");
		    	sbPrintBill.append(funPrintTextWithAlignment(clientName.substring(0, 30), 40, "Center"));
		    	sbPrintBill.append("\n");
		    	sbPrintBill.append(funPrintTextWithAlignment(clientName.substring(30), 40, "Center"));
		    }
    	    else
		    {
		    	sbPrintBill.append("\n");
		    	sbPrintBill.append(funPrintTextWithAlignment(clientName, 40, "Center"));
		    }
    	    
    	    
    	    if (clientCode.equals("047.001"))
    	    {
    		if (posCode.equals("P03") || posCode.equals("P02"))
    		{
    			sbPrintBill.append("\n");
    			sbPrintBill.append(funPrintTextWithAlignment("SHRI SHAM CATERERS", 40, "Center"));
    		    
    		    String cAddr1 = "Flat No.7, Mon Amour,".toUpperCase();
    		    sbPrintBill.append("\n");
    		    sbPrintBill.append(funPrintTextWithAlignment(cAddr1, 40, "Center"));
    		    
    		    String cAddr2 = "Thorat Colony,Prabhat Road,".toUpperCase();
    		    sbPrintBill.append("\n");
    		    sbPrintBill.append(funPrintTextWithAlignment(cAddr2, 40, "Center"));
    		    
    		    String cAddr3 = " Erandwane, Pune 411 004.".toUpperCase();
    		    sbPrintBill.append("\n");
    		    sbPrintBill.append(funPrintTextWithAlignment(cAddr3, 40, "Center"));
    		    
    		    String cAddr4 = "Approved Caterers of".toUpperCase();
    		    sbPrintBill.append("\n");
    		    sbPrintBill.append(funPrintTextWithAlignment(cAddr4, 40, "Center"));
    		    
    		    String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB".toUpperCase();
    		    sbPrintBill.append("\n");
    		    sbPrintBill.append(funPrintTextWithAlignment(cAddr5, 40, "Center"));
    		}
    	    }
    	    else
    	    {
    		if (!addressLine1.isEmpty())
    		{
    		    if (addressLine1.length() > 40)
    		    {
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine1.substring(0, 40), 40, "Center"));
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine1.substring(40), 40, "Center"));
    		    }
    		    
    		    else
    		    {
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine1, 40, "Center"));
    		    }
    		}
    		if (!addressLine2.isEmpty())
    		{
    		    if (addressLine2.length() > 40)
    		    {
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine2.substring(0, 40), 40, "Center"));
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine2.substring(40), 40, "Center"));
    		    }
    		    else
    		    {
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine2, 40, "Center"));
    		    }
    		}
    		
    		if (!addressLine3.isEmpty())
    		{
    		    if (addressLine3.length() > 40)
    		    {
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine3.substring(0, 40), 40, "Center"));
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine3.substring(40), 40, "Center"));
    		    }
    		    else
    		    {
    		    	sbPrintBill.append("\n");  	
    		    	sbPrintBill.append(funPrintTextWithAlignment(addressLine3, 40, "Center"));
    		    }
    		}
    		if (!city.isEmpty())
    		{
    			sbPrintBill.append("\n");
    			sbPrintBill.append(funPrintTextWithAlignment(city, 40, "Center"));
    		}
    	    }
    	    if (!telephone.isEmpty())
    	    {
    			if(telephone.contains(",")){
    	    		String[] arrTelNo = telephone.split(",");
                    for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                    {
                    	sbPrintBill.append("\n");
                    	sbPrintBill.append(funPrintTextWithAlignment(arrTelNo[cnt], 40, "Left"));
                        
                    }
    	    	}else if(telephone.contains("/")){
    	    		String[] arrTelNo = telephone.split(",");
                    for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                    {
                    	sbPrintBill.append("\n");
                    	sbPrintBill.append(funPrintTextWithAlignment(arrTelNo[cnt], 40, "Left"));
                        
                    }
    	    	}
    		}
    	    if (!email.isEmpty())
    	    {
    	    	if(email.contains(",")){
    	    		String[] arremail = email.split(",");
    	    		sbPrintBill.append("\n");
                    for (int cnt = 0; cnt < arremail.length; cnt++)
                    {
                    	sbPrintBill.append(funPrintTextWithAlignment(arremail[cnt], 40, "Left"));
                    }
    	    	}else if(email.contains(",")){
    	    		String[] arremail = email.split(",");
    	    		sbPrintBill.append("\n");
                    for (int cnt = 0; cnt < arremail.length; cnt++)
                    {
                    	sbPrintBill.append(funPrintTextWithAlignment(arremail[cnt], 40, "Left"));
                    }
    	    	}
    	    }
    	    
    	    if (operationType.equalsIgnoreCase("HomeDelivery"))
    	    {
    		
    		st = cmsCon.createStatement();
    		
    		sql = "  select a.strCustomerName,a.longMobileNo,a.strBuildingName from tblcustomermaster a  " + "  where a.strCustomerCode='" + customerCode + "' ";
    		System.out.println(sql);
    		rs = st.executeQuery(sql);
    		while (rs.next())
    		{
    		    customerName = rs.getString(1);
    		    mobileNo = rs.getString(2);
    		    address = rs.getString(3);
    		    
    		}
    		rs.close();
    		st.close();
    		sbPrintBill.append("\n");
    		sbPrintBill.append(funPrintTextWithAlignment("HomeDelivery", 40, "Center"));
    		sbPrintBill.append("\n");
    		sbPrintBill.append(funPrintTextWithAlignment("Customer Name :", 40, "Left"));
    		sbPrintBill.append(funPrintTextWithAlignment(" "+customerName, 40, "Left"));
    		sbPrintBill.append("\n");
    		
    		sbPrintBill.append(funPrintTextWithAlignment("Mobile No :", 40, "Left"));
    		sbPrintBill.append(funPrintTextWithAlignment(" "+mobileNo, 40, "Left"));
    		sbPrintBill.append("\n");
    		sbPrintBill.append(funPrintTextWithAlignment("Address :", 40, "Left"));
    		sbPrintBill.append(funPrintTextWithAlignment(" "+address, 40, "Left"));
    		}
    	    
    	    
    	    
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("----------------------------------------");
    	    sbPrintBill.append("\n");
    	    
    	    
    	    if (!tablName.isEmpty())
    	    {
    	    	sbPrintBill.append("\n");
    	    	sbPrintBill.append(funPrintTextWithAlignment("TABLE NAME:", 12, "Left"));
    	    	sbPrintBill.append(funPrintTextWithAlignment(tablName, 8, "Left"));
    	    	
    	    	if (!waiterNo.isEmpty())
        	    {
        	    	
        	    	sbPrintBill.append(funPrintTextWithAlignment("STEWARD :", 12, "Left"));
        	    	sbPrintBill.append(funPrintTextWithAlignment(waiterNo, 8, "Left"));
        		
        	    }
    	    	 sbPrintBill.append("\n");
    		
    	    }
    	    
    	   
    	    sbPrintBill.append(funPrintTextWithAlignment("POS Name :", 10, "Left"));
    	    sbPrintBill.append(funPrintTextWithAlignment(posName, 18, "Left"));
    	    if (!tablName.isEmpty())
    	    {
    	    	sbPrintBill.append(funPrintTextWithAlignment("Pax No:", 8, "Left"));
        	    sbPrintBill.append(funPrintTextWithAlignment(String.valueOf(paxNo), 4, "Left"));
    	    }
    	    
    	    
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append(funPrintTextWithAlignment("BILL NO :", 10, "Left"));
    	    sbPrintBill.append(funPrintTextWithAlignment(billno, 13, "Left"));
    	    
    	    String []bDateArr=bDate.split(" ");
    	    
    	  
    	    sbPrintBill.append(funPrintTextWithAlignment("DATE :", 6, "Left"));
    	    sbPrintBill.append(funPrintTextWithAlignment(bDateArr[0], 13, "Left"));
    	    
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("----------------------------------------");
    	    sbPrintBill.append("\n");
    	    /*pw.println(funPrintTextWithAlignment("Member Code", 12, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(funPrintTextWithAlignment(memberCode, 10, "Left"));
    	    
    	    pw.print(funPrintTextWithAlignment("Member Name", 12, "Left"));
    	    pw.print(funPrintTextWithAlignment(":", 2, "Left"));
    	    pw.print(funPrintTextWithAlignment(memberName, 26, "Left"));
    	    */
    	    
    	    sbPrintBill.append(funPrintTextWithAlignment("ITEM NAME ", 40, "Left"));
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append(funPrintTextWithAlignment("     QTY        RATE              AMOUNT", 40, "Left"));
    	    
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("----------------------------------------");
    	    
    	    for (int cnt = 0; cnt < arrListBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrListBillDtl.get(cnt);
    		if(items.get(0).length()>40)
    		{
    			sbPrintBill.append(funPrintTextWithAlignment("" + items.get(0), 40, "Left"));
    			sbPrintBill.append("\n");
    			sbPrintBill.append(funPrintTextWithAlignment("" + items.get(0).substring(40,items.get(0).length()), 40, "Left"));
    		}
    		else
    		{
    			sbPrintBill.append("\n");
    			sbPrintBill.append(funPrintTextWithAlignment("" + items.get(0), 40, "Left"));
    		}
    		
    			sbPrintBill.append("\n");
    			sbPrintBill.append(funPrintTextWithAlignment(" " + items.get(1) + " ", 8, "RIGHT"));
    			sbPrintBill.append(funPrintTextWithAlignment(items.get(2), 12, "RIGHT"));
    			sbPrintBill.append(funPrintTextWithAlignment(items.get(3), 20, "RIGHT"));
    		}
    	    if (arrListModifierBillDtl.size() > 0)
    	    {
    		for (int cnt = 0; cnt < arrListModifierBillDtl.size(); cnt++)
    		{
    		    ArrayList<String> items = arrListModifierBillDtl.get(cnt);
    		    if(items.get(0).length()>40)
    		    {
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment("" + items.get(0), 40, "Left"));
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment("" + items.get(0).substring(40,items.get(0).length()), 40, "Left"));
        		}
    		    else
    		    {
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment("" + items.get(0), 40, "Left"));
        		}
    		    	sbPrintBill.append("\n");
    		    	sbPrintBill.append(funPrintTextWithAlignment(" " + items.get(1) + " ", 8, "RIGHT"));
    		    	sbPrintBill.append(funPrintTextWithAlignment(items.get(2), 12, "RIGHT"));
    		    	sbPrintBill.append(funPrintTextWithAlignment(items.get(3), 20, "RIGHT"));
    		}
    	    }
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("----------------------------------------");
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append(funPrintTextWithAlignment("SUB TOTAL", 20, "Left"));
    	    sbPrintBill.append(funPrintTextWithAlignment("" + sbTotal, 20, "RIGHT"));
    	    
    	    if(dis>0)
    	    {
    	    	sbPrintBill.append("\n");
        	    sbPrintBill.append(funPrintTextWithAlignment("DISCOUNT  ", 20, "Left"));
        	    sbPrintBill.append(funPrintTextWithAlignment("" + dis, 20, "RIGHT"));
    	    	
    	    }
    	    
    	    
    	    for (int cnt = 0; cnt < arrTaxtBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrTaxtBillDtl.get(cnt);
    		sbPrintBill.append("\n");
    		sbPrintBill.append(funPrintTextWithAlignment(items.get(0), 20, "Left"));
    		sbPrintBill.append(funPrintTextWithAlignment(" " + items.get(1), 20, "RIGHT"));
    		
    	    }
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("----------------------------------------");
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append(funPrintTextWithAlignment("GRAND TOTAL", 20, "Left"));
    	    sbPrintBill.append(funPrintTextWithAlignment(" " + gdTotal, 20, "RIGHT"));
    	    
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("----------------------------------------");
    	  
    	    if(arrSettleBillDtl.size()>0)
    	    {
    	    	  for (int cnt = 0; cnt < arrSettleBillDtl.size(); cnt++)
    	    	    {
    	    		ArrayList<String> items = arrSettleBillDtl.get(cnt);
    	    		/*pw.println("---------Settlement Information--------");*/
    	    		sbPrintBill.append("\n");
    	    		sbPrintBill.append(funPrintTextWithAlignment(items.get(0), 20, "Left"));
    	    		sbPrintBill.append(funPrintTextWithAlignment("" + items.get(1), 20, "RIGHT"));
    	    		
    	    	    }
	    	      sbPrintBill.append("\n");
	        	  sbPrintBill.append("----------------------------------------");
    	    }
    	  
    	    
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append(funPrintTextWithAlignment("(INCLUSIVE OF ALL TAXES)", 40, "Center"));
    	    sbPrintBill.append("\n");
    	    
    	    
    	    if (printVatNo.equalsIgnoreCase("Y"))
    	    {
    	    	sbPrintBill.append(funPrintTextWithAlignment("Vat No. :", 20, "Left"));
    	    	sbPrintBill.append(funPrintTextWithAlignment("  "+vatNo, 20, "Left"));
    		
    	    }
    	    
    	    if (printServiceTaxNo.equalsIgnoreCase("Y"))
    	    {   
    	    	sbPrintBill.append("\n");
    	    	sbPrintBill.append(funPrintTextWithAlignment("Service Tax No. :", 20, "Left"));
    	    	sbPrintBill.append(funPrintTextWithAlignment("  "+serviceTaxNo, 20, "Left"));
    		
    	    }
    	    
    	    if(billFooter.length()>40){
    	    	List listTextToPrint = funGetTextWithSpecifiedSize(billFooter.trim(), 2);
                for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                {
                	sbPrintBill.append("\n");
                	sbPrintBill.append(funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 40, "Left"));
                }
    	    }
    	    
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("\n");
    	    sbPrintBill.append("\n");
    	    
    	  
    	    
    	    st.close();
    	    st = cmsCon.createStatement();
    	    sql = "select strBillPrinterPort,strAdvReceiptPrinterPort from tblposmaster where strPOSCode='" + posCode + "'";
    	    rs = st.executeQuery(sql);
    	    if (rs.next())
    	    {
    		funPrintTextFile(rs.getString(1), rs.getString(1), "Bill", "", "", multiBillPrint,"");
    	    }
    	    rs.close();
    	    
    	    cmsCon.close();
    	}
    	catch (Exception e)
    	{
    	    e.printStackTrace();
    	}
    	finally
    	{
    	    arrListBillDtl = null;
    	    arrTaxtBillDtl = null;
    	    arrSettleBillDtl = null;
    	}
        
    	return sbPrintBill;
    	
    }
    
    

    public void funGenerateVoidKOT(String kotTableNo,String KotNo, String text, String costCenterCode,String posCode,String deviceName)
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
    	Connection cmsCon = null;
    	Statement st = null,st1=null,st2=null,st3=null,st4=null;
    	String sql = null;
    	ResultSet rs = null;
    	clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
        String sqlVOIDKOT_waiterName = "";
        String dashedLineFor40Chars = "  --------------------------------------";
        //DecimalFormat decimalFormat = new DecimalFormat("#.###");
        int columnSize=40;
        String KOTTableName="";

        try
        {
            
        	funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_KOT = new File(filePath + "/Temp/Temp_KOT.txt");
            FileWriter fstream = new FileWriter(Text_KOT);
            BufferedWriter KotOut = new BufferedWriter(fstream);
            cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
        	st = cmsCon.createStatement();
        	st1 = cmsCon.createStatement();
        	st2= cmsCon.createStatement();
        	st3= cmsCon.createStatement();
        	st4= cmsCon.createStatement();
            KotOut.newLine();
            KotOut.newLine();
            objFileIOUtil.funPrintBlankSpace("VOID KOT", KotOut,columnSize);
            KotOut.write("VOID KOT");
            KotOut.newLine();

            objFileIOUtil.funPrintBlankSpace("KOT", KotOut,columnSize);
            KotOut.write("KOT");

            //item will pickup from tblvoidkot
            String sqlVOIDKOT_Items = "select a.dblItemQuantity,a.strItemName"
                    + ",c.strCostCenterCode,c.strPrinterPort,a.strItemCode,c.strSecondaryPrinterPort "
                    + "from tblvoidkot a,tblmenuitempricingdtl b,tblcostcentermaster c "
                    + "where left(a.strItemCode,7)=b.strItemCode and b.strCostCenterCode=c.strCostCenterCode "
                    + "and a.strKOTNo='" + KotNo + "' ";
            if (!text.equals("Reprint"))
            {
                sqlVOIDKOT_Items += " and a.strPrintKOT='Y' ";
            }
            sqlVOIDKOT_Items += " and b.strCostCenterCode='" + costCenterCode + "' group by a.strItemName";

            ResultSet rs_VOIDKOT_Items = st.executeQuery(sqlVOIDKOT_Items);
            String primaryPrinterName = "", secondaryPrinterName = "";

            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
            KotOut.write("  KOT NO        :");
            KotOut.write("  " + KotNo + "  ");
           

   
            String waiterNo = "select a.strWaiterNo,b.strTableName,c.strPosName from tblvoidkot a,tbltablemaster b,tblposmaster c"
            		+ " where a.strKOTNo='"+KotNo+"' "
            		+ " and a.strTableNo='"+kotTableNo+"' and a.strPOSCode='"+posCode+"' "
            		+ " and a.strPOSCode=b.strPOSCode and a.strTableNo=b.strTableNo "
            		+ " and a.strPOSCode=c.strPosCode ";
            ResultSet rsWaiterNo = st1.executeQuery(waiterNo);
            if (rsWaiterNo.next())
            {
            	 KotOut.newLine();
                 KotOut.write("  TABLE NAME    :");
                 KotOut.write("  " + rsWaiterNo.getString(2) + "     ");
                 KotOut.newLine();
                if (!"null".equalsIgnoreCase(rsWaiterNo.getString(1)) && rsWaiterNo.getString(1).trim().length() > 0)
                {
                    sqlVOIDKOT_waiterName = "select strWShortName from tblwaitermaster where strWaiterNo='" + rsWaiterNo.getString(1) + "'";
                    ResultSet rs_waiterName = st2.executeQuery(sqlVOIDKOT_waiterName);
                    if (rs_waiterName.next())
                    {
                        KotOut.write("  WAITER NAME   :" + "  " + rs_waiterName.getString(1));
                        KotOut.newLine();
                    }
                    rs_waiterName.close();
                }
            }
            rsWaiterNo.close();
            st1.close();

            //Added by Jaichandra
            sqlVOIDKOT_waiterName = "select date(dteDateCreated),time(dteDateCreated) from tblvoidkot where strKOTNo='" + KotNo + "'";
            ResultSet rs_Date = st3.executeQuery(sqlVOIDKOT_waiterName); 
            if (rs_Date.next())
            {
                KotOut.write("  DATE & TIME   :" + " " + rs_Date.getString(1) + " " + rs_Date.getString(2));
            }
            rs_Date.close();
            st1.close();
            KotOut.newLine();

            String sqlVOIDKOT_reasonName = "select b.strReasonName "
                    + "from tblvoidkot a,tblreasonmaster b "
                    + "where a.strReasonCode=b.strReasonCode "
                    + "and a.strKOTNo='" + KotNo + "' "
                    + "group by a.strKOTNo";
            ResultSet rs_Date1 = st4.executeQuery(sqlVOIDKOT_reasonName);
            if (rs_Date1.next())
            {
                KotOut.write("  Reason        :" + " " + rs_Date1.getString(1));
            }
            rs_Date1.close();
            KotOut.newLine();

            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
            KotOut.write("  QTY         ITEM NAME  ");
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);

            int qtyWidth = 6;
            while (rs_VOIDKOT_Items.next())
            {
                KotOut.newLine();
                String itemqty = String.valueOf(Math.rint(rs_VOIDKOT_Items.getDouble(1)));

                primaryPrinterName = rs_VOIDKOT_Items.getString(4);
                secondaryPrinterName = rs_VOIDKOT_Items.getString(6);

                KotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + "       " + rs_VOIDKOT_Items.getString(2).toUpperCase());

            }
            rs_VOIDKOT_Items.close();
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
            KotOut.newLine();
            KotOut.newLine();
            KotOut.newLine();
            KotOut.newLine();
           
            KotOut.write("m");//windows
            KotOut.close();
            fstream.close();
            funPrintKOTTextFile(primaryPrinterName, secondaryPrinterName, "kot", "N", "Y", "N","KOT");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
}
