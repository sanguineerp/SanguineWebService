package com.apos.util;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsDatabaseConnection;

@Controller
public class clsTextFormat1ForBill {

	@Autowired
	clsTextFileGenerator objTextFileGenerator;
	
	   public void funGenerateTextFileForBill(String billNo, String posCode, String clientCode,String reprint,String strServerBillPrinterName,String multiBillPrint)
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
		    objTextFileGenerator.funCreateTempFolder();
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
		    if ("Y".equalsIgnoreCase(reprint))
	        {
		    	pw.print(objTextFileGenerator.funPrintTextWithAlignment("[DUPLICATE]", 40, "Center"));
		    	pw.println(" ");
		        
	        }
		    if (operationType.equalsIgnoreCase("HomeDelivery"))
		    {
			pw.println(objTextFileGenerator.funPrintTextWithAlignment("Home Delivery", 40, "Center"));
		    }
		    else if (operationType.equalsIgnoreCase("TakeAway"))
		    {
			pw.println(objTextFileGenerator.funPrintTextWithAlignment("Take Away", 40, "Center"));
		    }
		    
		    pw.println(objTextFileGenerator.funPrintTextWithAlignment("TAX INVOICE", 40, "Center"));
		    pw.println(objTextFileGenerator.funPrintTextWithAlignment(clientName, 40, "Center"));
		    if (!addressLine1.isEmpty())
		    {
			if (addressLine1.length() > 40)
			{
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine1.substring(0, 40), 40, "Center"));
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine1.substring(40), 40, "Center"));
			}
			
			else
			{
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine1, 40, "Center"));
			}
		    }
		    if (!addressLine2.isEmpty())
		    {
			if (addressLine2.length() > 40)
			{
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine2.substring(0, 40), 40, "Center"));
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine2.substring(40), 40, "Center"));
			}
			else
			{
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine2, 40, "Center"));
			}
		    }
		    
		    if (!addressLine3.isEmpty())
		    {
			if (addressLine3.length() > 40)
			{
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine3.substring(0, 40), 40, "Center"));
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine3.substring(40), 40, "Center"));
			}
			else
			{
			    pw.println(objTextFileGenerator.funPrintTextWithAlignment(addressLine3, 40, "Center"));
			}
		    }
		    if (!city.isEmpty())
		    {
			pw.println(objTextFileGenerator.funPrintTextWithAlignment(city, 40, "Center"));
		    }
		    if (!telephone.isEmpty())
		    {
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("TEL NO.", 12, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(telephone, 26, "Left"));
			pw.println(" ");
		    }
		    if (!email.isEmpty())
		    {
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("EMAIL ID", 12, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(email, 26, "Left"));
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
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("Customer Name", 13, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(customerName, 25, "Left"));
			pw.println(" ");
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("Mobile No", 13, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(mobileNo, 25, "Left"));
			pw.println(" ");
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("Address ", 13, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			
			
			 if (!address.isEmpty())
			    {
				   int totalCharToPrint=25;
			    	int strlen = address.length();
		            String add1 = "";
		            if (strlen < totalCharToPrint)
		            {
		            	pw.print(objTextFileGenerator.funPrintTextWithAlignment(address, totalCharToPrint, "Left"));
		            }
		            else
		            {
		            	add1 = address.substring(0, totalCharToPrint);
		                pw.println(objTextFileGenerator.funPrintTextWithAlignment(add1, 25, "Left"));
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
		                    pw.println(objTextFileGenerator.funPrintTextWithAlignment("              " +add1, 40, "Left"));
		                }
		                else
		                {
		                    add1 = address.substring(i, strlen);
		                    pw.println(objTextFileGenerator.funPrintTextWithAlignment("              " +add1, 40, "Left"));
		                    i = strlen + 1;
		                }
		            }
			   
			    }
			
			
			pw.println(" ");
		    }
		    
		    if (!tablName.isEmpty())
		    {
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("TABLE NAME", 12, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(tablName, 26, "Left"));
			pw.println(" ");
		    }
		    
		    if (!waiterNo.isEmpty())
		    {
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("STEWARD", 12, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(waiterNo, 26, "Left"));
			pw.println(" ");
		    }
		    pw.println(line);
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("BILL NO.", 12, "Left"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment(billno, 26, "Left"));
		    pw.println(" ");
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("DATE & TIME", 12, "Left"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment(bDate, 26, "Left"));
		    pw.println(" ");
		    pw.println(line);
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("QTY ", 6, "RIGHT"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment(" ITEMNAME", 26, "Left"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("AMT", 8, "RIGHT"));
		    pw.println(" ");
		    pw.println(line);
		    for (int cnt = 0; cnt < arrListBillDtl.size(); cnt++)
		    {
			ArrayList<String> items = arrListBillDtl.get(cnt);
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0), 26, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
			pw.println(" ");
		    }
		    if (arrListModifierBillDtl.size() > 0)
		    {
			for (int cnt = 0; cnt < arrListModifierBillDtl.size(); cnt++)
			{
			    ArrayList<String> items = arrListModifierBillDtl.get(cnt);
			    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(1) + " ", 6, "RIGHT"));
			    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0), 26, "Left"));
			    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(2), 8, "RIGHT"));
			    pw.println(" ");
			}
		    }
		    
		    pw.println(line);
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("SUB TOTAL", 32, "Left"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + sbTotal, 8, "RIGHT"));
		    pw.println(" ");
		    if(dis>0)
		    {
		    	pw.print(objTextFileGenerator.funPrintTextWithAlignment("DISCOUNT  ", 32, "Left"));
			    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + dis, 8, "RIGHT"));
			    pw.println(" "); 	
		    }
		    for (int cnt = 0; cnt < arrTaxtBillDtl.size(); cnt++)
		    {
			ArrayList<String> items = arrTaxtBillDtl.get(cnt);
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(0) + " ", 32, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(1), 8, "RIGHT"));
			pw.println(" ");
		    }
		    pw.println(line);
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("TOTAL(ROUNDED)", 32, "Left"));
		    pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + gTotal, 8, "RIGHT"));
		    pw.println(" ");
		    pw.println(line);
		    if(arrSettleBillDtl.size()>0)
		    {
		    	for (int cnt = 0; cnt < arrSettleBillDtl.size(); cnt++)
			    {
				ArrayList<String> items = arrSettleBillDtl.get(cnt);
				pw.println("---------Settlement Information--------");
				pw.print(objTextFileGenerator.funPrintTextWithAlignment(items.get(0), 17, "Left"));
				pw.print(objTextFileGenerator.funPrintTextWithAlignment("" + items.get(1), 23, "RIGHT"));
				pw.println(" ");
			    }
		    	pw.print(objTextFileGenerator.funPrintTextWithAlignment("PAID AMT", 17, "Left"));
				pw.print(objTextFileGenerator.funPrintTextWithAlignment("" +gTotal , 23, "RIGHT"));
				pw.println(" ");
		    	pw.println(line);
		    }
	   
		    if (printInclusiveOfAllTaxesOnBill.equals("Y"))
	        {
		    	pw.println(objTextFileGenerator.funPrintTextWithAlignment("(INCLUSIVE OF ALL TAXES)", 40, "Center")); 
	        }
		    pw.println(" ");
		    
		    
		    if (printVatNo.equalsIgnoreCase("Y"))
		    {
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("Vat No.", 12, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(vatNo, 26, "Left"));
			pw.println(" ");
		    }
		    if (printServiceTaxNo.equalsIgnoreCase("Y"))
		    {
			pw.print(objTextFileGenerator.funPrintTextWithAlignment("Service Tax No.", 12, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(":", 2, "Left"));
			pw.print(objTextFileGenerator.funPrintTextWithAlignment(serviceTaxNo, 26, "Left"));
			pw.println(" ");
		    }
		    int num = billFooter.trim().length() / 30;
	        int num1 = billFooter.length() % 30;
	        int cnt1 = 0;
	        for (int cnt = 0; cnt < num; cnt++)
	        {
	            String footer = billFooter.substring(cnt1, (cnt1 + 30));
	            footer = footer.replaceAll("\n", "");
	            pw.println(objTextFileGenerator.funPrintTextWithAlignment("     " + footer.trim(), 40, "Center"));
	            cnt1 += 30;
	        }
	        pw.println(" ");
		    pw.println(objTextFileGenerator.funPrintTextWithAlignment(userName, 40, "Center"));
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
	 
}
