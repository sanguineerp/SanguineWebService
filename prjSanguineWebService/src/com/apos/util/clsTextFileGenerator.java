package com.apos.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	@Autowired
	clsTextFormat21ForBill objTextFormat21ForBill ;
	
	@Autowired
	clsTextFormat5ForBill objTextFormat5ForBill ;
	
	@Autowired
	clsTextFormat11ForBill objTextFormat11ForBill ;
	
	@Autowired 
	clsTextFormat1ForBill objTextFormat1ForBill;
	
	@Autowired
	clsTextFormat12ForBill objTextFormat12ForBill;
    
	@Autowired
	clsTextFormatForeignForBill objTextFormatForeignForBill;
	
	@Autowired
	clsTextFormat23ForBill objTextFormat23ForBill;
	
	
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
    
    public void funGenerateAndPrintBill(ResponseBuilder resp,String billNo, String posCode, String clientCode,String reprint,String strServerBillPrinterName) throws Exception
    {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		String sql = null;
		String billFormat = "";
		String multiBillPrint="";
		ResultSet rs = null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
		st = cmsCon.createStatement();
		sql = "select a.strBillFormatType,a.strMultipleBillPrinting from tblsetup a;";
		rs = st.executeQuery(sql);
		if (rs.next())
		{
		    billFormat = rs.getString(1);
		    multiBillPrint = rs.getString(2);
		}
		rs.close();
		
		System.out.println("funGenerateAndPrintBill "+billNo+"\tReprint = "+reprint+"\billFormat = "+billFormat);
		
		if (billFormat.equalsIgnoreCase("Text 1"))
		{
			objTextFormat1ForBill.funGenerateTextFileForBill(billNo, posCode, clientCode,reprint,strServerBillPrinterName,multiBillPrint);
		}
		else if (billFormat.equalsIgnoreCase("Text 5"))
		{
			objTextFormat5ForBill.funGenerateTextFileForBillFormat5(billNo, posCode, clientCode,reprint,strServerBillPrinterName,multiBillPrint);
		}
		else if (billFormat.equalsIgnoreCase("Text 11"))
		{
			objTextFormat11ForBill.funGenerateTextFileForBillFormat11(billNo, posCode, clientCode,reprint,strServerBillPrinterName,multiBillPrint);
		}
		else if (billFormat.equalsIgnoreCase("Text 12"))
		{
			objTextFormat12ForBill.funGenerateTextFileForBillFormat12(billNo, posCode, clientCode,reprint,strServerBillPrinterName,multiBillPrint);
		}
		else if(billFormat.equalsIgnoreCase("Jasper 5")){
			obJasperPrint.funCredateJasper(resp,billNo, posCode, clientCode,reprint,strServerBillPrinterName,multiBillPrint);
		}
		else if (billFormat.equalsIgnoreCase("Text 21"))
		{
			objTextFormat21ForBill.funGenerateTextFileForBillFormat21(billNo, posCode, clientCode,reprint,strServerBillPrinterName,multiBillPrint);
		}
		else if(billFormat.equalsIgnoreCase("Text Foreign"))
		{
			objTextFormatForeignForBill.funGenerateTextFileForForeignBill(billNo, posCode, clientCode,reprint,strServerBillPrinterName,multiBillPrint);
		}
		else if (billFormat.equalsIgnoreCase("Text 23"))
		{
			objTextFormat23ForBill.funGenerateTextFileForBillFormat23(billNo, posCode, clientCode,reprint,strServerBillPrinterName);
		}
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
    
    
    
    public String funPrintKOTTextFile(String primaryPrinterName, String secPrinterName, String type, String multipleKOTPrint, String printKOTYN, String multiBillPrint,String KOTType,int noOfCopiesPrimaryPrinter,int noOfCopiesSecPrinter,String printOnBothPrinter,String reprint)
    {
    	String result="";
	    
	   try
		{
		    if (type.equalsIgnoreCase("kot"))
		    {
				if (printKOTYN.equals("Y"))
				{
					result=funPrintKOTWindows(primaryPrinterName, secPrinterName,KOTType,printOnBothPrinter, noOfCopiesPrimaryPrinter, noOfCopiesSecPrinter,reprint);
					
				}
				else
				{
					result="Kot Printing Not Available!!!";
				}
		    }
		    else if(type.equalsIgnoreCase("Test"))
		    {
		    	result=funPrintKOTWindows(primaryPrinterName, secPrinterName,KOTType,printOnBothPrinter, noOfCopiesPrimaryPrinter, noOfCopiesSecPrinter,reprint);
		    }
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
	   return result;
    }
    
    
    
    
    
    
    private String funPrintKOTWindows(String primaryPrinterName, String secPrinterName,String KOTType,String printOnBothPrinter,int noOfCopiesPrimaryPrinter,int noOfCopiesSecPrinter,String reprint)
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
    	else if(KOTType.equals("Test"))
    	{
    		filename = (filePath + "/Temp/Test_Print.txt");
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
				
				job.print(doc, pras);// 1 st print normal on primary
				
				if(printOnBothPrinter.equalsIgnoreCase("Y")){
					if(noOfCopiesSecPrinter>0){
						result=funPrintOnSecPrinter(secPrinterName, filename,result); //for printing single print
						/*if(!reprint.equalsIgnoreCase("Reprint")){
							reprint="Reprint";
							funAppendDuplicate(filename);	
						}*/
						reprint="Reprint";
						funAppendDuplicate(filename);
						
						for(int i=0;i<noOfCopiesSecPrinter-1;i++){
							job = printService[printerIndex].createPrintJob();
							fis = new FileInputStream(filename);
							das = new HashDocAttributeSet();
							doc = new SimpleDoc(fis, flavor, das);
							result=funPrintOnSecPrinter(secPrinterName, filename,result); //for printing on both printer	
						}
							
					}
					 
				}
				if(noOfCopiesPrimaryPrinter>1){
					if(!reprint.equalsIgnoreCase("Reprint")){
						reprint="Reprint";
						funAppendDuplicate(filename);
					}
					
					for(int i=0;i<noOfCopiesPrimaryPrinter-1;i++){
						job = printService[printerIndex].createPrintJob();
						fis = new FileInputStream(filename);
						das = new HashDocAttributeSet();
						doc = new SimpleDoc(fis, flavor, das);
						
						job.print(doc, pras);	//All primary 
					}	
					
				}
				
				System.out.println("Successfully Print on " + primaryPrinterName);
				String printerInfo = "";
				
				 result="Primary Found";
				
				
		    }
		    else
		    {
		    	for(int j=0;j<noOfCopiesSecPrinter;j++){
		    		//funAppendDuplicate(filename);
			    	result=funPrintOnSecPrinter(secPrinterName, filename,result);	
		    	}
		    	
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
		System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
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
    
    public List funGetTextWithSpecifiedSize(String text, int size)
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
    	    	String strServerBillPrinterName="";
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
    
    
    public void funPrintServiceVatNo( PrintWriter pw, String billNo, String billDate) 
    {
	
	Map<String, String> mapBillNote = new HashMap<>();

	try
	{
		clsDatabaseConnection objDb = new clsDatabaseConnection();
    	Connection cmsCon = null;
    	Statement st = null;
    	ResultSet rs = null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
	    st = cmsCon.createStatement();
	    String billNote = "";
	    String sql = "select a.strTaxCode,a.strTaxDesc,a.strBillNote "
		    + "from tbltaxhd a,tblbilltaxdtl b "
		    + "where a.strTaxCode=b.strTaxCode "
		    + "and b.strBillNo='" + billNo + "' "
		    + "and date(b.dteBillDate)='" + billDate + "' "
		    + "order by a.strBillNote ";
	    ResultSet rsBillNote = st.executeQuery(sql);
	    while (rsBillNote.next())
	    {
		billNote = rsBillNote.getString(3).trim();
		if (!billNote.isEmpty())
		{
		    mapBillNote.put(billNote, billNote);
		}
	    }
	    rsBillNote.close();

	    sql = "select a.strPOSCode,a.strBillSeries,a.strHdBillNo,a.strDtlBillNos,a.dblGrandTotal,b.strBillNote "
		    + "from tblbillseriesbilldtl a,tblbillseries b "
		    + "where a.strBillSeries=b.strBillSeries "
		    + "and a.strHdBillNo='" + billNo + "' "
		    + "and date(a.dteBillDate)='" + billDate + "' ";
	    rsBillNote = st.executeQuery(sql);
	    if (rsBillNote.next())
	    {
		billNote = rsBillNote.getString(6).trim();
		if (!billNote.isEmpty())
		{
		    mapBillNote.put(billNote, billNote);
		}

	    }
	    rsBillNote.close();

	    for (String printBillNote : mapBillNote.values())
	    {
	    	pw.print(funPrintTextWithAlignment(" "+printBillNote, 40, "Left"));
		   
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    
    private void funAppendDuplicate(String fileName)
    {
	try
	{
	    File fileKOTPrint = new File(fileName);
	    clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
	    String filePath = System.getProperty("user.dir");
	    filePath += "/Temp/Temp_KOT2.txt";
	    File fileKOTPrint2 = new File(filePath);
	    BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint2), "UTF8"));
	    objFileIOUtil.funPrintBlankSpace("DUPLICATE", KotOut, 40);
	    KotOut.write("[DUPLICATE]");
	    KotOut.newLine();

	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileKOTPrint)));
	    String line = null;
	    while ((line = br.readLine()) != null)
	    {
		KotOut.write(line);
		KotOut.newLine();
	    }
	    br.close();
	    KotOut.close();

	    String content = new String(Files.readAllBytes(Paths.get(filePath)));
	    Files.write(Paths.get(fileName), content.getBytes(), StandardOpenOption.CREATE);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    
}
