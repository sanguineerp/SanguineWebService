package com.apos.util;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsDatabaseConnection;

@Controller
public class clsTextFormat11ForBill {
	@Autowired
	clsTextFileGenerator objTextFileGenerator;
	
	  
    public PrintWriter funGenerateTextFileForBillFormat11(String billNo,String posCode,String clientCode,String reprint,String strServerBillPrinterName,String multiBillPrint)
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
    	    objTextFileGenerator.funCreateTempFolder();
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
    	    
    	    sql = " select a.strClientName,a.strAddressLine1,a.strAddressLine2, a.strAddressLine3,a.strCityName,a.intTelephoneNo, a.strEmail,a.strVatNo,a.strServiceTaxNo,a.strBillFooter,a.strPrintServiceTaxNo,a.strPrintVatNo,a.strMultipleBillPrinting ,a.strPrintInclusiveOfAllTaxesOnBill from tblsetup a where a.strClientCode='"+clientCode+"' and a.strPOSCode='"+posCode+"'; ";
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
    	    	pw.print(objTextFileGenerator.funPrintTextWithAlignment("[DUPLICATE]", 20, "Center"));
    	    	pw.println(" ");
    	        
            }
    	    if (operationType.equalsIgnoreCase("HomeDelivery"))
    	    {
    	    pw.println();
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment("Home Delivery", 20, "Center"));
    	    }
    	    else if (operationType.equalsIgnoreCase("TakeAway"))
    	    {
        	pw.println();
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment("Take Away", 20, "Center"));
    	    }
    	    pw.println();
    	    pw.println(objTextFileGenerator.funPrintTextWithAlignment("TAX INVOICE", 20, "Center"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(clientName, 20, "Center"));
    	    
    	    if (clientCode.equals("047.001"))
    	    {
    		if (posCode.equals("P03") || posCode.equals("P02"))
    		{
        	    pw.println("");
    		    pw.println(objTextFileGenerator.funPrintTextWithAlignment("SHRI SHAM CATERERS", 20, "Center"));
    		    
    		    String cAddr1 = "Flat No.7, Mon Amour,".toUpperCase();
    		    pw.println(objTextFileGenerator.funPrintTextWithAlignment(cAddr1, 20, "Center"));
    		    
    		    String cAddr2 = "Thorat Colony,Prabhat Road,".toUpperCase();
    		    pw.println(objTextFileGenerator.funPrintTextWithAlignment(cAddr2, 20, "Center"));
    		    
    		    String cAddr3 = " Erandwane, Pune 411 004.".toUpperCase();
    		    pw.println(objTextFileGenerator.funPrintTextWithAlignment(cAddr3, 20, "Center"));
    		    
    		    String cAddr4 = "Approved Caterers of".toUpperCase();
    		    pw.println(objTextFileGenerator.funPrintTextWithAlignment(cAddr4, 20, "Center"));
    		    
    		    String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB".toUpperCase();
    		    pw.println(objTextFileGenerator.funPrintTextWithAlignment(cAddr5, 20, "Center"));
    		}
    	    }
    	    else
    	    {
    		if (!addressLine1.isEmpty())
    		{
    		    if (addressLine1.length() > 20)
    		    {
    	    	pw.println("");
    			pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine1.substring(0, 20), 20, "Center"));
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment(addressLine1.substring(20), 20, "Center"));
    		    }
    		    
    		    else
    		    {
    	    	pw.println("");
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment(addressLine1, 20, "Center"));
    		    }
    		}
    		if (!addressLine2.isEmpty())
    		{
    		    if (addressLine2.length() > 20)
    		    {
    	    	pw.println("");
    			pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine2.substring(0, 20), 20, "Center"));
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment(addressLine2.substring(20), 20, "Center"));
    		    }
    		    else
    		    {
    	    	pw.println("");
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment(addressLine2, 20, "Center"));
    		    }
    		}
    		
    		if (!addressLine3.isEmpty())
    		{
    		    if (addressLine3.length() > 20)
    		    {
    	    	pw.println("");
    			pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine3.substring(0, 20), 20, "Center"));
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment(addressLine3.substring(20), 20, "Center"));
    		    }
    		    else
    		    {
		    	pw.println("");   	
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment(addressLine3, 20, "Center"));
    		    }
    		}
    		if (!city.isEmpty())
    		{
    			pw.println("");
    		    pw.print(objTextFileGenerator.funPrintTextWithAlignment(city, 20, "Center"));
    		}
    	    }
    	    if (!telephone.isEmpty())
    	    {
    			if(telephone.contains(",")){
    	    		String[] arrTelNo = telephone.split(",");
                    for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                    {
                    	pw.println("");
                    	pw.print(objTextFileGenerator.funPrintTextWithAlignment(arrTelNo[cnt], 20, "Left"));
                        
                    }
    	    	}else if(telephone.contains("/")){
    	    		String[] arrTelNo = telephone.split(",");
                    for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                    {
                    	pw.println("");
                        pw.print(objTextFileGenerator.funPrintTextWithAlignment(arrTelNo[cnt], 20, "Left"));
                        
                    }
    	    	}
    		}
    	    if (!email.isEmpty())
    	    {
    	    	if(email.contains(",")){
    	    		String[] arremail = email.split(",");
                    for (int cnt = 0; cnt < arremail.length; cnt++)
                    {
                        pw.println(objTextFileGenerator.funPrintTextWithAlignment(arremail[cnt], 20, "Left"));
                    }
    	    	}else if(email.contains(",")){
    	    		String[] arremail = email.split(",");
                    for (int cnt = 0; cnt < arremail.length; cnt++)
                    {
                        pw.println(objTextFileGenerator.funPrintTextWithAlignment(arremail[cnt], 20, "Left"));
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
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("HomeDelivery", 20, "Center"));
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("Customer Name :", 20, "Left"));
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment(" "+customerName, 20, "Left"));
    		
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("Mobile No :", 20, "Left"));
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment(" "+mobileNo, 20, "Left"));
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("Address :", 20, "Left"));
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(" "+address, 20, "Left"));
    		}
    	    
    	    if (!tablName.isEmpty())
    	    {
    	    pw.println("");
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment("TABLE NAME:", 12, "Left"));
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(tablName, 10, "Left"));
    		
    	    }
    	    
    	    if (!waiterNo.isEmpty())
    	    {
    	    	
    	    pw.println();
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("STEWARD :", 12, "Left"));
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(waiterNo, 8, "Left"));
    		
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("POS Name", 8, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(posName, 10, "Left"));
    	    pw.println();
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("Pax No.", 8, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(String.valueOf(paxNo), 10, "Left"));
    	    
    	    pw.println();
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("BILL NO.", 8, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(billno, 10, "Left"));
    	    pw.println();
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("DATE ", 8, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(bDate, 10, "Left"));
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    /*pw.println(objTextFileGenerator.funPrintTextWithAlignment("Member Code", 12, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 1, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(memberCode, 10, "Left"));
    	    
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("Member Name", 12, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(memberName, 26, "Left"));
    	    */
    	    
    	    pw.println(objTextFileGenerator.funPrintTextWithAlignment("ITEM NAME ", 20, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(" QTY   RATE   AMOUNT", 20, "Left"));
    	    
    	    //pw.println("________________________________________");
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    for (int cnt = 0; cnt < arrListBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrListBillDtl.get(cnt);
    		pw.println();
    		if(items.get(0).length()>20){
    			pw.println(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0).substring(20,items.get(0).length()), 20, "Left"));
    		}else{
    			
    			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
    		}
    			pw.println();
	    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(" " + items.get(1) + " ", 6, "RIGHT"));
	    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(items.get(2), 7, "RIGHT"));
	    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(items.get(3), 7, "RIGHT"));
    		}
    	    if (arrListModifierBillDtl.size() > 0)
    	    {
    		for (int cnt = 0; cnt < arrListModifierBillDtl.size(); cnt++)
    		{
    		    ArrayList<String> items = arrListModifierBillDtl.get(cnt);
    		    if(items.get(0).length()>20){
    		    	pw.println();
        			pw.println(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
        			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0).substring(20,items.get(0).length()), 20, "Left"));
        		}else{
        			pw.println();
        			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0), 20, "Left"));
        		}
    		    	pw.println();
    	    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(" " + items.get(1) + " ", 6, "RIGHT"));
    	    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(items.get(2), 7, "RIGHT"));
    	    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(items.get(3), 7, "RIGHT"));
    		}
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("SUB TOTAL", 10, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + sbTotal, 10, "RIGHT"));
    	    pw.println();
    	    if(dis>0)
    	    {
    	    	pw.print(objTextFileGenerator.funPrintTextWithAlignment("DISCOUNT  ", 32, "Left"));
    		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + dis, 8, "RIGHT"));
    		    pw.println(" "); 	
    	    }
    	    for (int cnt = 0; cnt < arrTaxtBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrTaxtBillDtl.get(cnt);
    		pw.println();
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(items.get(0), 20, "Left"));
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(" " + items.get(1), 20, "RIGHT"));
    		
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment("TOTAL(ROUNDED)", 12, "Left"));
    	    pw.print(objTextFileGenerator.funPrintTextWithAlignment(" " + gdTotal, 8, "RIGHT"));
    	    
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    for (int cnt = 0; cnt < arrSettleBillDtl.size(); cnt++)
    	    {
    		ArrayList<String> items = arrSettleBillDtl.get(cnt);
    		/*pw.println("---------Settlement Information--------");*/
    		pw.println();
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment(items.get(0), 10, "Left"));
    		pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(1), 10, "RIGHT"));
    		
    	    }
    	    pw.println();
    	    pw.println("----------------------------------------");
    	    pw.println(objTextFileGenerator.funPrintTextWithAlignment("(INCLUSIVE OF ", 20, "Center"));
    	    pw.println(objTextFileGenerator.funPrintTextWithAlignment(" ALL TAXES )", 20, "Center"));
    	    
    	    
    	    if (printVatNo.equalsIgnoreCase("Y"))
    	    {
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("Vat No. :", 8, "Left"));
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("  "+vatNo, 20, "Left"));
    		
    	    }
    	    if (printServiceTaxNo.equalsIgnoreCase("Y"))
    	    {
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("Service Tax No. :", 20, "Left"));
    		pw.println(objTextFileGenerator.funPrintTextWithAlignment("  "+serviceTaxNo, 20, "Left"));
    		
    	    }
    	    
    	    if(billFooter.length()>20){
    	    	List listTextToPrint = objTextFileGenerator.funGetTextWithSpecifiedSize(billFooter.trim(), 2);
                for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                {
                	pw.println();
                	pw.print(objTextFileGenerator.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
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
    	    	if(strServerBillPrinterName.equalsIgnoreCase("") || strServerBillPrinterName.equalsIgnoreCase("No Printer Installed") ){
		    		objTextFileGenerator.funPrintTextFile(rs.getString(1), rs.getString(1), "Bill", "", "", multiBillPrint,"");
		    	}else{
		    		objTextFileGenerator.funPrintTextFile(strServerBillPrinterName, strServerBillPrinterName, "Bill", "", "", multiBillPrint,"");
		    	}
    	    	//objTextFileGenerator.funPrintTextFile(rs.getString(1), rs.getString(1), "Bill", "", "", multiBillPrint,"");
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
    
  
}
