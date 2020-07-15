/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
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
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletContext;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.bean.clsBillDtl;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

@Controller
public class clsJasperFormat4ForBill
{

    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private DecimalFormat stdDecimalFormat = new DecimalFormat("######.##");
    private final String dashedLineFor40Chars = "  --------------------------------------";
    @Autowired
	 private ServletContext servletContext;
	 
	 @Autowired
	 clsTextFileGenerator obTextFileGenerator;
	 
	 
    private double dblCountMultiBillPrint;
    clsUtilityFunctions obUtility;
	 clsDatabaseConnection objDb = new clsDatabaseConnection();
	 Connection cmsCon = null;
	 Statement st = null;
	 String sql = null;
	 String billFormat = "",strPOSCode="",strBillPrinterPort="";
	 ResultSet rs =null;
	 private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    
    /**
     *
     * @param billNo
     * @param reprint
     * @param formName
     * @param transType
     * @param billDate
     * @param posCode
     * @param viewORprint
     */
   
    public void funCreateJasper4(ResponseBuilder resp, String billNo,String posCode,String clientCode,String reprint, String strServerBillPrinterName,String multiBillPrint)
    {

    	strPOSCode=posCode;
	HashMap hm = new HashMap();
	DecimalFormat decimalFormat = new DecimalFormat("#.###");
	String Linefor5 = "  --------------------------------------";
	try
	{
	    PreparedStatement pst = null;
        
	    String user = "";
	    String billType = " ";

	    String billDscFrom = "tblbilldiscdtl";	   
	    String billhd = "tblbillhd";
	    String billdtl = "tblbilldtl";
	    String billModifierdtl = "tblbillmodifierdtl";
	    String billSettlementdtl = "tblbillsettlementdtl";
	    String billtaxdtl = "tblbilltaxdtl";
	    String billPromoDtl = "tblbillpromotiondtl";
	    
	    String subTotal = "";
	    String grandTotal = "";
	    String advAmount = "";
	    String deliveryCharge = "";
	    String customerCode = "";
	    boolean flag_DirectBiller = false;
	    Date dteBillDate;
	    obUtility=new clsUtilityFunctions();
	    
	    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		 SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		 cmsCon = objDb.funOpenAPOSCon("mysql", "Master");
		 st = cmsCon.createStatement();
		 funGetPrinterDetails(strServerBillPrinterName);
		 
		 	String billno = " ",operationType = " ", waiterNo = "",tablName = "";
		    Double gdTotal = 0.0, sbTotal = 0.0,dis = 0.0;
		    String strBillDate = "",remark="";
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
		    String customerName = "";
		    String mobileNo = "";
		    String address = "";
		    String printInclusiveOfAllTaxesOnBill="";
		    String userName="";
		    String line="----------------------------------------";
		    double dblUSDConverionRate=0;
		    int noOfDecimalPlace=2;
		    String isPrintInvoice="";
		    String printHomeDel="";
		    String orderNoPrint="";
		    String timeOnBill="";
		    String orderNoMakeKot="";
		    String printOpenItem="";
		    String printZeroMod="";
		    sql = "select strPosName from tblPOSMaster where strPosCode='" + posCode + "'";
		    rs = st.executeQuery(sql);
		    if (rs.next())
		    {
			posName = rs.getString(1);
		    }
		    rs.close();
		    
		    sql = " select a.strClientName,a.strAddressLine1,a.strAddressLine2, a.strAddressLine3,a.strCityName,a.intTelephoneNo, a.strEmail,a.strVatNo,a.strServiceTaxNo,a.strBillFooter,a.strPrintServiceTaxNo,a.strPrintVatNo,a.strMultipleBillPrinting ,a.strPrintInclusiveOfAllTaxesOnBill,a.dblUSDConverionRate,a.dblNoOfDecimalPlace,a.strPrintHomeDeliveryYN,a.strPrintTaxInvoiceOnBill,a.strPrintOrderNoOnBillYN ,a.strPrintTimeOnBill,a.strPrintOrderNoOnMakeKot,a.strPrintOpenItemsOnBill ,a.strPrintZeroAmtModifierInBill "//23     
		    		+ " from tblsetup a where a.strClientCode='"+clientCode+"' and a.strPOSCode='"+posCode+"' ";
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
				dblUSDConverionRate= rs.getDouble(15);
				noOfDecimalPlace=rs.getInt(16);
				printHomeDel=rs.getString(17);
				isPrintInvoice=rs.getString(18);
				orderNoPrint=rs.getString(19);
				timeOnBill=rs.getString(20);
				orderNoMakeKot=rs.getString(21);
				printOpenItem=rs.getString(22);
		    }
		    rs.close();
		    
		    sql = " select a.strBillNo,ifnull(b.strTableName,''),ifnull(c.strWShortName,''),a.dblGrandTotal,a.dblSubTotal,a.dblDiscountAmt,a.dteBillDate,a.intPaxNo,a.strOperationType,a.strCustomerCode " + ",a.strUserCreated from tblbillhd a left outer join tbltablemaster b " + " on a.strTableNo=b.strTableNo " + " left outer join tblwaitermaster c " + " on a.strWaiterNo=c.strWaiterNo " + " where a.strBillNo='" + billNo + "' " + " and a.strPosCode='" + posCode + "' ";
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
		    if(!strBillDate.isEmpty()){
		    	strBillDate=strBillDate.split(" ")[0];
		    }
		    String gDecimalFormatString= clsGlobalFunctions.funGetGlobalDecimalFormatString(noOfDecimalPlace);

	    if (clientCode.equals("117.001"))
	    {
		if (posCode.equals("P01"))
		{
		    hm.put("posWiseHeading", "THE PREM'S HOTEL");
		}
		else if (posCode.equals("P02"))
		{
		    hm.put("posWiseHeading", "SWIG");
		}
	    }

	   boolean isReprint = false;
	   /*if(isOriginal)
	   {
		 hm.put("duplicate", "[ORIGINAL]");
	   }*/
	  
	    if ("reprint".equalsIgnoreCase(reprint))
	    {
		   isReprint = true;
		   hm.put("duplicate", "[DUPLICATE]");
	    }
	    /*if (transType.equals("Void"))
	    {
		hm.put("voidedBill", "VOIDED BILL");
	    }
*/
	    boolean flag_isHomeDelvBill = false;
	    String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
		    + "from tblhomedelivery where strBillNo=? and date(dteDate)=? ;";
	    pst =cmsCon.prepareStatement(SQL_HomeDelivery);
	    pst.setString(1, billNo);
	    pst.setString(2, strBillDate);
	    ResultSet rs_HomeDelivery = pst.executeQuery();

	    List<clsBillDtl> listOfHomeDeliveryDtl = new ArrayList<>();
	    clsBillDtl objBillDtl = new clsBillDtl();

	    if (rs_HomeDelivery.next())
	    {
		flag_isHomeDelvBill = true;
		if (printHomeDel.equalsIgnoreCase("Y"))
		{
		    billType = "HOME DELIVERY";
		}
		
		
		customerCode = rs_HomeDelivery.getString(2);

		String SQL_CustomerDtl = "";

		if (rs_HomeDelivery.getString(5).equals("Temporary"))
		{
		    SQL_CustomerDtl = "select a.strCustomerName,a.strTempAddress,a.strTempStreet"
			    + " ,a.strTempLandmark,a.strBuildingName,a.strCity,a.intPinCode,a.longMobileNo "
			    + " from tblcustomermaster a left outer join tblbuildingmaster b "
			    + " on a.strBuldingCode=b.strBuildingCode "
			    + " where a.strCustomerCode=? ;";
		}
		else if (rs_HomeDelivery.getString(5).equals("Office"))
		{
		    SQL_CustomerDtl = "select a.strCustomerName,a.strOfficeBuildingName,a.strOfficeStreetName"
			    + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
			    + " from tblcustomermaster a "
			    + " where a.strCustomerCode=? ";
		}
		else
		{
		    SQL_CustomerDtl = "select a.strCustomerName,a.strCustAddress,a.strStreetName"
			    + " ,a.strLandmark,a.strBuildingName,a.strCity,a.intPinCode,a.longMobileNo "
			    + " from tblcustomermaster a left outer join tblbuildingmaster b "
			    + " on a.strBuldingCode=b.strBuildingCode "
			    + " where a.strCustomerCode=? ;";
		}
		pst = cmsCon.prepareStatement(SQL_CustomerDtl);
		pst.setString(1, rs_HomeDelivery.getString(2));
		ResultSet rs_CustomerDtl = pst.executeQuery();
		while (rs_CustomerDtl.next())
		{
		    StringBuilder fullAddress = new StringBuilder();

		    hm.put("NAME", rs_CustomerDtl.getString(1));
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Name         : " + rs_CustomerDtl.getString(1).toUpperCase());
		    fullAddress.append(objBillDtl.getStrItemName());
		    listOfHomeDeliveryDtl.add(objBillDtl);

		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("ADDRESS    :" + rs_CustomerDtl.getString(2).toUpperCase());
		    fullAddress.append(objBillDtl.getStrItemName());
		    listOfHomeDeliveryDtl.add(objBillDtl);

		    if (rs_CustomerDtl.getString(3).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(3).toUpperCase());//"Street    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    if (rs_CustomerDtl.getString(4).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(4).toUpperCase());//"Landmark    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    if (rs_CustomerDtl.getString(6).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(6).toUpperCase());//"City    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    if (rs_CustomerDtl.getString(7).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(7).toUpperCase());//"Pin    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    hm.put("FullAddress", fullAddress);

		    if (rs_CustomerDtl.getString(8).isEmpty())
		    {
			hm.put("MOBILE_NO", "");
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("MOBILE_NO  :" + " ");
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }
		    else
		    {
			hm.put("MOBILE_NO", rs_CustomerDtl.getString(8));
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("Mobile No    : " + rs_CustomerDtl.getString(8));
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }
		}
		rs_CustomerDtl.close();

		if (null != rs_HomeDelivery.getString(3) && rs_HomeDelivery.getString(3).trim().length() > 0)
		{
		    String[] delBoys = rs_HomeDelivery.getString(3).split(",");
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
		    pst = cmsCon.prepareStatement(SQL_DeliveryBoyDtl);

		    ResultSet rs_DeliveryBoyDtl = pst.executeQuery();
		    strIN.setLength(0);
		    for (int i = 0; rs_DeliveryBoyDtl.next(); i++)
		    {
			if (i == 0)
			{
			    strIN.append(rs_DeliveryBoyDtl.getString(1).toUpperCase());
			}
			else
			{
			    strIN.append("," + rs_DeliveryBoyDtl.getString(1).toUpperCase());
			}
		    }

		    if (strIN.toString().isEmpty())
		    {
			hm.put("DELV BOY", "");
		    }
		    else
		    {
			hm.put("DELV BOY", "Delivery Boy : " + strIN);
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("Delivery Boy : " + strIN);
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }
		    rs_DeliveryBoyDtl.close();
		}
		else
		{
		    hm.put("DELV BOY", "");
		}
	    }
	    rs_HomeDelivery.close();
	    int result = funPrintTakeAwayForJasper(billhd, billNo);
	    if (result == 1)
	    {
		billType = "Take Away";
		String sql = "select a.strBillNo,a.dteBillDate,a.strCustomerCode,b.strCustomerName,b.longMobileNo "
			+ "from " + billhd + " a,tblcustomermaster b "
			+ "where a.strCustomerCode=b.strCustomerCode "
			+ "and a.strBillNo='" + billNo + "' "
			+ "and date(a.dteBillDate)='" + strBillDate+ "' ";
		ResultSet rsCustomer = st.executeQuery(sql);
		if (rsCustomer.next())
		{
		    hm.put("NAME", rsCustomer.getString(4));
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Name         : " + rsCustomer.getString(4).toUpperCase());
		    listOfHomeDeliveryDtl.add(objBillDtl);

		    hm.put("MOBILE_NO", rsCustomer.getString(5));
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Mobile No    : " + rsCustomer.getString(5));
		    listOfHomeDeliveryDtl.add(objBillDtl);
		}
		rsCustomer.close();
	    }
	    if (isPrintInvoice.equalsIgnoreCase("Y"))
	    {
		       hm.put("TAX_INVOICE", "TAX INVOICE");
	    }
	    if (clientCode.equals("047.001") && posCode.equals("P03"))
	    {
		hm.put("ClientName", "SHRI SHAM CATERERS");
		String cAddr1 = "Flat No.7, Mon Amour,";
		String cAddr2 = "Thorat Colony,Prabhat Road,";
		String cAddr3 = " Erandwane, Pune 411 004.";
		String cAddr4 = "Approved Caterers of";
		String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
		hm.put("ClientAddress1", cAddr1 + cAddr2);
		hm.put("ClientAddress2", cAddr3 + cAddr4);
		hm.put("ClientAddress3", cAddr5);
	    }
	    else if (clientCode.equals("047.001") && posCode.equals("P02"))
	    {
		hm.put("ClientName", "SHRI SHAM CATERERS");
		String cAddr1 = "Flat No.7, Mon Amour,";
		String cAddr2 = "Thorat Colony,Prabhat Road,";
		String cAddr3 = " Erandwane, Pune 411 004.";
		String cAddr4 = "Approved Caterers of";
		String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
		hm.put("ClientAddress1", cAddr1 + cAddr2);
		hm.put("ClientAddress2", cAddr3 + cAddr4);
		hm.put("ClientAddress3", cAddr5);
	    }
	    else if (clientCode.equals("092.001") || clientCode.equals("092.002") || clientCode.equals("092.003"))//Shree Sound Pvt. Ltd.
	    {
		hm.put("ClientName", "SSPL");
		hm.put("ClientAddress1", addressLine1);
		hm.put("ClientAddress2", addressLine2);
		hm.put("ClientAddress3",addressLine3);

		if (city.trim().length() > 0)
		{
		    hm.put("ClientCity", city);
		}
	    }
	    else if (clientCode.equals("190.001"))
	    {
		String licenseName = clientName;
		if (billNo.startsWith("L"))//liqour bill license name
		{
		    licenseName = "STEP-IN-AGENCIES & ESTATE PVT LTD";
		}
		else
		{
		    licenseName = clientName;
		}

		hm.put("ClientName", licenseName);
		hm.put("ClientAddress1", addressLine1);
		hm.put("ClientAddress2", addressLine2);
		hm.put("ClientAddress3", addressLine3);

		if (city.trim().length() > 0)
		{
		    hm.put("ClientCity", city);
		}
	    }
	    else
	    {
		hm.put("ClientName", clientName);
		hm.put("ClientAddress1", addressLine1);
		hm.put("ClientAddress2", addressLine2);
		hm.put("ClientAddress3", addressLine3);

		if (city.trim().length() > 0)
		{
		    hm.put("ClientCity", city);
		}
	    }

	    hm.put("TEL NO", telephone);
	    hm.put("EMAIL ID", email);
	    hm.put("Line", Linefor5);

	    String query = "";
	    String SQL_BillHD = "";
	    String waiterName = "";
	    String tblName = "";
	    ResultSet rsQuery = null;
	    ResultSet rs_BillHD = null;
	    ResultSet rsTblName = null;
	    String sqlTblName = "";
	    String tabNo = "";
	    boolean flag_DirectBillerBlill = false;
	    boolean flgComplimentaryBill = false;
	    String sql = "select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
		    + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' "
		    + "and date(a.dteBillDate)='" + strBillDate + "' ";
	    ResultSet rsSettlementType = st.executeQuery(sql);
	    if (rsSettlementType.next())
	    {
		flgComplimentaryBill = true;
	    }
	    rsSettlementType.close();

	    if (funIsDirectBillerBill(billNo, billhd))
	    {
		flag_DirectBillerBlill = true;
		SQL_BillHD = "select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
			+ "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
			+ ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName,a.intOrderNo "
			+ ",a.strKOTToBillNote "
			+ "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
			+ "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
			+ "where a.strBillNo=?  "
			+ "and date(a.dteBillDate)=? ";
		flag_DirectBiller = true;
		pst = cmsCon.prepareStatement(SQL_BillHD);
		pst.setString(1, billNo);
		pst.setString(2, strBillDate);
		rs_BillHD = pst.executeQuery();
		rs_BillHD.next();
	    }
	    else
	    {
		SQL_BillHD = "select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
			+ "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
			+ ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo,a.intOrderNo "
			+ ",a.strKOTToBillNote "
			+ "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
			+ "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
			+ "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
			+ "where a.strBillNo=? and b.strOperational='Y' and date(a.dteBillDate)=? ";
		pst = cmsCon.prepareStatement(SQL_BillHD);
		pst.setString(1, billNo);
		pst.setString(2, strBillDate);
		rs_BillHD = pst.executeQuery();
		if (rs_BillHD.next())
		{
		    tabNo = rs_BillHD.getString(1);
		    if (rs_BillHD.getString(2).equalsIgnoreCase("null") || rs_BillHD.getString(2).equalsIgnoreCase(""))
		    {
			waiterNo = "";
		    }
		    else
		    {
			waiterNo = rs_BillHD.getString(2);
			query = "select strWShortName from tblwaitermaster where strWaiterNo=? ;";
			pst = cmsCon.prepareStatement(query);
			pst.setString(1, waiterNo);
			rsQuery = pst.executeQuery();
			if (rsQuery.next())
			{
			    waiterName = rsQuery.getString(1);
			}
			rsQuery.close();
			pst.close();
		    }
		}

		sqlTblName = "select strTableName from tbltablemaster where strTableNo=? ;";
		pst = cmsCon.prepareStatement(sqlTblName);
		pst.setString(1, tabNo);
		rsTblName = pst.executeQuery();
		if (rsTblName.next())
		{
		    tblName = rsTblName.getString(1);
		}
		rsTblName.close();
		pst.close();
	    }

	    // funPrintTakeAway(billhd, billNo, BillOut);
	    if (flag_DirectBillerBlill)
	    {
		hm.put("POS", rs_BillHD.getString(14));
		hm.put("BillNo", billNo);

		String orderNo = rs_BillHD.getString(15);
		if (orderNoPrint.equalsIgnoreCase("Y"))
		{
		    hm.put("orderNo", "Your order no is " + orderNo);
		}
	
		String lblBillNote="BillNote : ";
		String billNote = rs_BillHD.getString(16);
		if (billNote.trim().length() > 0)
	        {
		    if(billNote.contains("OrderFrom"))
		    {
		        //get online Order service provider name and Code (For Wera service) //OrderFromONLINE 304595
			lblBillNote=billNote.substring(9, billNote.indexOf(" "))+" CODE : " ;
                        billNote=billNote.split(" ")[1];
		    }
		    else
		    {
			billNote="  ZOMATO CODE : " + billNote;
		        
		    }	
	        }
		
		
		hm.put("lblBillNote", lblBillNote);
		hm.put("strBillNote", billNote);

		if (timeOnBill.equalsIgnoreCase("Y"))
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(1)));
		}
		else
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(1)));
		}

		subTotal = rs_BillHD.getString(4);
		grandTotal = rs_BillHD.getString(6);
		user = rs_BillHD.getString(10);
		deliveryCharge = rs_BillHD.getString(11);
		advAmount = rs_BillHD.getString(12);
	    }
	    else
	    {
		hm.put("TABLE NAME", tblName);

		if (waiterName.trim().length() > 0)
		{
		    hm.put("waiterName", waiterName);
		}
		hm.put("POS", rs_BillHD.getString(16));
		hm.put("BillNo", billNo);
		hm.put("PaxNo", rs_BillHD.getString(17));

		String orderNo = rs_BillHD.getString(18);
		if(orderNoMakeKot.equalsIgnoreCase("Y"))
		{
		     hm.put("orderNo", "Your Order No. Is:" + orderNo);
		}
		String billNote = rs_BillHD.getString(19);
		hm.put("strBillNote", billNote);

		if (timeOnBill.equalsIgnoreCase("Y"))
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(3)));
		}
		else
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(3)));
		}

		subTotal = rs_BillHD.getString(6);
		grandTotal = rs_BillHD.getString(8);
		user = rs_BillHD.getString(12);
		deliveryCharge = rs_BillHD.getString(13);
		advAmount = rs_BillHD.getString(14);
	    }

	    List<clsBillDtl> listOfBillDetail = new ArrayList<>();
	    String SQL_BillDtl = "select sum(a.dblQuantity),a.strItemName as ItemLine1"
		    + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) as ItemLine2"
		    + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo "
		    + " from " + billdtl + " a "
		    + " where a.strBillNo=? and a.tdhYN='N' and date(a.dteBillDate)=?";
	    if (!printOpenItem.equalsIgnoreCase("Y"))
	    {
		SQL_BillDtl += "and a.dblAmount>0 ";
	    }
	    SQL_BillDtl += " group by a.strItemCode  ";
	    SQL_BillDtl += "  order by a.strSequenceNo asc ;";
	   
	    pst = cmsCon.prepareStatement(SQL_BillDtl);
	    pst.setString(1, billNo);
	    pst.setString(2, strBillDate);
	    ResultSet rs_BillDtl = pst.executeQuery();
	    while (rs_BillDtl.next())
	    {
		double saleQty = rs_BillDtl.getDouble(1);
		String sqlPromoBills = "select dblQuantity from " + billPromoDtl + " "
			+ " where strBillNo='" + billNo + "' and strItemCode='" + rs_BillDtl.getString(5) + "' "
			+ " and strPromoType='ItemWise' and date(dteBillDate)='" + strBillDate + "'";
		ResultSet rsPromoItems = st.executeQuery(sqlPromoBills);
		if (rsPromoItems.next())
		{
		    saleQty -= rsPromoItems.getDouble(1);
		}
		rsPromoItems.close();

		String qty = String.valueOf(saleQty);
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
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setDblQuantity(Double.parseDouble(decimalFormat.format(Double.parseDouble(qty))));
		    objBillDtl.setDblAmount(rs_BillDtl.getDouble(4));
		    objBillDtl.setStrItemName(rs_BillDtl.getString(2));
		    listOfBillDetail.add(objBillDtl);

		    String sqlModifier = "select count(*) "
			    + "from " + billModifierdtl + " where strBillNo=? and left(strItemCode,7)=? and date(dteBillDate)=?";
		    if (!printZeroMod.equalsIgnoreCase("Y"))
		    {
			sqlModifier += " and  dblAmount !=0.00 ";
		    }
		    pst = cmsCon.prepareStatement(sqlModifier);

		    pst.setString(1, billNo);
		    pst.setString(2, rs_BillDtl.getString(5));
		    pst.setString(3, strBillDate);
		    ResultSet rs_count = pst.executeQuery();
		    rs_count.next();
		    int cntRecord = rs_count.getInt(1);
		    rs_count.close();
		    if (cntRecord > 0)
		    {
			sqlModifier = "select strModifierName,dblQuantity,dblAmount "
				+ " from " + billModifierdtl + " "
				+ " where strBillNo=? and left(strItemCode,7)=? and date(dteBillDate)=?";
			if (!printZeroMod.equalsIgnoreCase("Y"))
			{
			    sqlModifier += " and  dblAmount !=0.00 ";
			}
			pst = cmsCon.prepareStatement(sqlModifier);

			pst.setString(1, billNo);
			pst.setString(2, rs_BillDtl.getString(5));
			pst.setString(3, strBillDate);
			ResultSet rs_modifierRecord = pst.executeQuery();
			while (rs_modifierRecord.next())
			{
			    if (flgComplimentaryBill)
			    {
				objBillDtl = new clsBillDtl();
				objBillDtl.setDblQuantity(Double.parseDouble(decimalFormat.format(rs_modifierRecord.getDouble(2))));
				objBillDtl.setDblAmount(0);
				objBillDtl.setStrItemName(rs_modifierRecord.getString(1).toUpperCase());
				listOfBillDetail.add(objBillDtl);
			    }
			    else
			    {
				objBillDtl = new clsBillDtl();
				objBillDtl.setDblQuantity(Double.parseDouble(decimalFormat.format(rs_modifierRecord.getDouble(2))));
				objBillDtl.setDblAmount(rs_modifierRecord.getDouble(3));
				objBillDtl.setStrItemName(rs_modifierRecord.getString(1).toUpperCase());
				listOfBillDetail.add(objBillDtl);
			    }
			}
			rs_modifierRecord.close();
		    }
		}
	    }
	    rs_BillDtl.close();

	    funPrintPromoItemsInBill(billNo, 4, listOfBillDetail);  // Print Promotion Items in Bill for this billno.

	    List<clsBillDtl> listOfDiscountDtl = new ArrayList<>();
	    sql = "select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
		    + "from " + billDscFrom + " a ,tblreasonmaster b "
		    + "where  a.strDiscReasonCode=b.strReasonCode "
		    + "and a.strBillNo='" + billNo + "' and date(a.dteBillDate)='" + strBillDate + "'";
	    ResultSet rsDisc = st.executeQuery(sql);

	    boolean flag = true;
	    while (rsDisc.next())
	    {
		if (flag)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Discount");
		    listOfDiscountDtl.add(objBillDtl);
		    flag = false;
		}
		double dbl = Double.parseDouble(rsDisc.getString("dblDiscPer"));
		String discText = String.format("%.1f", dbl) + "%" + " On " + rsDisc.getString("strDiscOnValue") + "";
		if (discText.length() > 30)
		{
		    discText = discText.substring(0, 30);
		}
		else
		{
		    discText = String.format("%-30s", discText);
		}

		String discountOnItem = funPrintTextWithAlignment(rsDisc.getString("dblDiscAmt"), 8, "Right");
		hm.put("Discount", discText + " " + discountOnItem);
		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName(discText);
		objBillDtl.setDblAmount(rsDisc.getDouble("dblDiscAmt"));
		listOfDiscountDtl.add(objBillDtl);

		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName("Reason :" + " " + rsDisc.getString("strReasonName"));
		listOfDiscountDtl.add(objBillDtl);

		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName("Remark :" + " " + rsDisc.getString("strDiscRemarks"));
		listOfDiscountDtl.add(objBillDtl);
	    }

	    List<clsBillDtl> listOfTaxDetail = new ArrayList<>();
	    String sql_Tax = "select b.strTaxDesc,sum(a.dblTaxAmount),b.strBillNote "
		    + " from " + billtaxdtl + " a,tbltaxhd b "
		    + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
		    + " group by a.strTaxCode";
	    ResultSet rsTax = st.executeQuery(sql_Tax);

	    while (rsTax.next())
	    {
		if (flgComplimentaryBill)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setDblAmount(0);
		    objBillDtl.setStrItemName(rsTax.getString(1));
		    listOfTaxDetail.add(objBillDtl);
		    hm.put("GSTNo", rsTax.getString(3));
		}
		else
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setDblAmount(rsTax.getDouble(2));
		    objBillDtl.setStrItemName(rsTax.getString(1));
		    listOfTaxDetail.add(objBillDtl);
		    hm.put("GSTNo", rsTax.getString(3));
		}
	    }
	    rsTax.close();

	    //add del charges
	    double delCharges = Double.parseDouble(deliveryCharge);
	    objBillDtl = new clsBillDtl();
	    objBillDtl.setDblAmount(delCharges);
	    objBillDtl.setStrItemName("Del. Charges");
	    if (delCharges > 0)
	    {
		listOfTaxDetail.add(objBillDtl);
	    }

	    List<clsBillDtl> listOfGrandTotalDtl = new ArrayList<>();
	    if (Double.parseDouble(grandTotal) > 0)
	    {
		objBillDtl = new clsBillDtl();
		objBillDtl.setDblAmount(Double.parseDouble(grandTotal));
		listOfGrandTotalDtl.add(objBillDtl);
	    }

	    List<clsBillDtl> listOfSettlementDetail = new ArrayList<>();
	    //settlement breakup part
	    String sqlSettlementBreakup = "select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
		    + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
		    + "where a.strBillNo=? and a.strSettlementCode=b.strSettelmentCode"
		    + " and date(a.dteBillDate)=?";
	    pst = cmsCon.prepareStatement(sqlSettlementBreakup);
	    pst.setString(1, billNo);
	    pst.setString(2, strBillDate);
	    ResultSet rs_Bill_Settlement = pst.executeQuery();
	    while (rs_Bill_Settlement.next())
	    {
		if (flgComplimentaryBill)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName(rs_Bill_Settlement.getString(2));
		    objBillDtl.setDblAmount(0.00);
		    listOfSettlementDetail.add(objBillDtl);
		}
		else
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName(rs_Bill_Settlement.getString(2));
		    objBillDtl.setDblAmount(rs_Bill_Settlement.getDouble(1));
		    listOfSettlementDetail.add(objBillDtl);
		}
	    }
	    rs_Bill_Settlement.close();

	    String sqlTenderAmt = "select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
		    + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
		    + " and date(dteBillDate)='" + strBillDate + "'"
		    + " group by strBillNo";
	    ResultSet rsTenderAmt = st.executeQuery(sqlTenderAmt);
	    if (rsTenderAmt.next())
	    {
		if (flgComplimentaryBill)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("PAID AMT");
		    objBillDtl.setDblAmount(0.00);
		    listOfSettlementDetail.add(objBillDtl);
		}
		else
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("PAID AMT");
		    objBillDtl.setDblAmount(rsTenderAmt.getDouble(1));
		    listOfSettlementDetail.add(objBillDtl);
		    if (rsTenderAmt.getDouble(3) > 0)
		    {

			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("REFUND AMT");
			objBillDtl.setDblAmount(rsTenderAmt.getDouble(3));
			listOfSettlementDetail.add(objBillDtl);
		    }
		}
	    }
	    rsTenderAmt.close();

	    if (flag_isHomeDelvBill)
	    {
		String sql_count = "select count(*) from tblhomedelivery where strCustomerCode=?";
		pst = cmsCon.prepareStatement(sql_count);
		pst.setString(1, customerCode);
		ResultSet rs_Count = pst.executeQuery();
		rs_Count.next();
		hm.put("CUSTOMER_COUNT", rs_Count.getString(1));
	    }

	    List<clsBillDtl> listOfServiceVatDetail = funPrintServiceVatNoForJasper(billNo, strBillDate, billtaxdtl);
	    List<clsBillDtl> listOfFooterDtl = new ArrayList<>();
	    
	    List<clsBillDtl> listOfServiceVatWithDebitCardDtl=new ArrayList<>();
	    String sqlCardDtl = "select a.strCardNo,b.dblTransactionAmt,a.dblRedeemAmt "
                    + " from tbldebitcardmaster a ,tbldebitcardbilldetails b "
                    + " where a.strCardNo=b.strCardNo and b.strBillNo='" + billNo + "' "
                    + "and date(b.dteBillDate)='"+strBillDate+"'";
            ResultSet rsCardDtl = st.executeQuery(sqlCardDtl);
            if (rsCardDtl.next())
            {
		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName("Card No :"+rsCardDtl.getString(1));
		listOfServiceVatWithDebitCardDtl.add(objBillDtl);
		
		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName("Balance Amt :"+rsCardDtl.getDouble(3));
		listOfServiceVatWithDebitCardDtl.add(objBillDtl);
		
		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName("");
		listOfServiceVatWithDebitCardDtl.add(objBillDtl);
	    }
	    listOfServiceVatWithDebitCardDtl.addAll(listOfServiceVatDetail);
	    
	    String sqlFooter = " select a.strBillFooter from tblsetup a where (a.strPOSCode='" + posCode + "' or strPOSCode='All');";
	    pst = cmsCon.prepareStatement(sqlFooter);
	    ResultSet rsFooter = pst.executeQuery();
	    if (rsFooter.next())
	    {
		billFooter = rsFooter.getString(1);

		String[] footers = billFooter.split("\n");
		for (int i = 0; i < footers.length; i++)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName(footers[i]);
		    listOfFooterDtl.add(objBillDtl);
		}
	    }
	    rsFooter.close();

	    hm.put("user", user);

        hm.put("ch", "m");

	    hm.put("BillType", billType);
	    hm.put("listOfItemDtl", listOfBillDetail);
	    hm.put("listOfTaxDtl", listOfTaxDetail);
	    hm.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
	    hm.put("listOfServiceVatDetail", listOfServiceVatWithDebitCardDtl);
	    hm.put("listOfFooterDtl", listOfFooterDtl);
	    hm.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
	    hm.put("listOfDiscountDtl", listOfDiscountDtl);
	    hm.put("listOfSettlementDetail", listOfSettlementDetail);

	    hm.put("decimalFormaterForDoubleValue", gDecimalFormatString);
	    hm.put("decimalFormaterForIntegerValue", "0");

	    String imagePath = System.getProperty("user.dir");
	    imagePath = imagePath + File.separator + "ReportImage";
	    imagePath = imagePath + File.separator + "imgBillLogo.jpg";
	    System.out.println("imgBillLogo=" + imagePath);
	    hm.put("imgBillLogo", imagePath);

	    List<List<clsBillDtl>> listData = new ArrayList<>();
	    listData.add(listOfBillDetail);

	    String reportName = "";
/*	    reportName = "com/POSGlobal/reports/rptBillFormat4JasperReport.jasper";

	    InputStream is = this.getClass().getClassLoader().getResourceAsStream(reportName);
	    JasperPrint print = JasperFillManager.fillReport(is, hm, beanColDataSource);
	    */
	    JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(listData);
	 
	    JasperDesign jd1 = JRXmlLoader.load(servletContext.getResourceAsStream("/WEB-INF/billFormat/rptBillFormat4JasperReport.jrxml"));
	    JasperReport jr1 = JasperCompileManager.compileReport(jd1);
        final JasperPrint print = JasperFillManager.fillReport(jr1,  hm, beanColDataSource);



        
//            objPrintingUtility.funShowJasperFile(formName, print);

        JRViewer viewer = new JRViewer(print);
        JFrame jf = new JFrame();
        jf.getContentPane().add(viewer);
        jf.validate();
        new Thread()
        {
            @Override
            public void run()
            {
               funPrintJasperExporterInThread(print);
            }
        }.start();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    
     /**
     *
     * @param print
     */
   	public void funPrintJasperExporterInThread(JasperPrint print)
    {

		 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		
		 int selectedService = 0;
	     String billPrinterName = strBillPrinterPort;
	     
	     billPrinterName = billPrinterName.replaceAll("#", "\\\\");
	     printServiceAttributeSet.add(new PrinterName(billPrinterName, null));

	     PrintService[] printService = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);

	     try {
	       
	    	    JRPrintServiceExporter exporter;
	    	    PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	    	    printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
	    	    printRequestAttributeSet.add(new Copies(1));

	    	    // these are deprecated
	    	    exporter = new JRPrintServiceExporter();
	    	    exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService[selectedService]);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printService[selectedService].getAttributes());
	    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
	    	    exporter.exportReport();
	    	    
//
//	            String filePath = System.getProperty("user.dir");
//				 File pdfrpt = new File(filePath + "/Temp");
//				    if (!pdfrpt.exists())
//				    {
//				    	pdfrpt.mkdirs();
//				    }
//				    
//				JRExporter exporter1 = new JRPdfExporter();
//				exporter1.setParameter(JRPdfExporterParameter.JASPER_PRINT, print);
//				exporter1.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(filePath + "/Temp/jasper4bill.pdf")); // your output goes here
//				exporter1.exportReport();

	     } catch (Exception e) {
	    	 
	    	 e.printStackTrace();
	     }
		

	 
    }
	 	 
	 public void funGetPrinterDetails(String strServerBillPrinterName){
		 try{
			 
			 String sql = "select strBillPrinterPort,strAdvReceiptPrinterPort from tblposmaster where strPOSCode='" + strPOSCode + "'";
			 rs = st.executeQuery(sql);
			    if (rs.next())
			    {
			    	if(strServerBillPrinterName.equalsIgnoreCase("") || strServerBillPrinterName.equalsIgnoreCase("No Printer Installed") ){
			    		strBillPrinterPort=rs.getString(1);
			    	}else{
			    		strBillPrinterPort=strServerBillPrinterName;
			    	}
			    }
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }
	
	 
	 public boolean funIsDirectBillerBill(String billNo, String billhd)
	    {
	        boolean flgIsDirectBillerBill = false;
	        try
	        {
	            String sql_checkDirectBillerBill = "select strTableNo,strOperationType "
	                    + " from " + billhd + " where strBillNo=?  ";
	            PreparedStatement pst = cmsCon.prepareStatement(sql_checkDirectBillerBill);
	            pst.setString(1, billNo);
	            ResultSet rsIsDirectBillBill = pst.executeQuery();
	            if (rsIsDirectBillBill.next())
	            {
	                if (rsIsDirectBillBill.getString(1) != null && rsIsDirectBillBill.getString(1).trim().isEmpty())
	                {
	                    flgIsDirectBillerBill = true;
	                }
	            }
	            rsIsDirectBillBill.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return flgIsDirectBillerBill;
	    }
	 public int funPrintPromoItemsInBill(String billNo, int billPrintSize, List<clsBillDtl> listOfBillDetail) throws Exception
	    {
	        String sqlBillPromoDtl = "select b.strItemName,a.dblQuantity,'0',dblRate "
	                + " from tblbillpromotiondtl a,tblitemmaster b "
	                + " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ";
	        ResultSet rsBillPromoItemDtl = st.executeQuery(sqlBillPromoDtl);
	        clsBillDtl objBillDtl = null;
	        while (rsBillPromoItemDtl.next())
	        {
	            objBillDtl = new clsBillDtl();
	            objBillDtl.setDblQuantity(rsBillPromoItemDtl.getDouble(2));
	            objBillDtl.setDblAmount(rsBillPromoItemDtl.getDouble(3));
	            objBillDtl.setStrItemName(rsBillPromoItemDtl.getString(1).toUpperCase());
	            listOfBillDetail.add(objBillDtl);
	        }
	        rsBillPromoItemDtl.close();
	        return 1;
	    }
	 
	 public List<clsBillDtl> funPrintServiceVatNoForJasper(String billNo, String billDate, String billTaxDtl) throws IOException
	    {
		List<clsBillDtl> listOfServiceVatDetail = new ArrayList<>();
		clsBillDtl objBillDtl = null;
		Map<String, String> mapBillNote = new HashMap<>();

		try
		{
		    String billNote = "";
		    String sql = "select a.strTaxCode,a.strTaxDesc,a.strBillNote "
			    + "from tbltaxhd a," + billTaxDtl + " b "
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
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(printBillNote);
			listOfServiceVatDetail.add(objBillDtl);
		    }

		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}

		return listOfServiceVatDetail;
	    }
	 
	 private void funPrintBillWindows(String billPrinterName)
	    {
		 try
		 {
		    System.out.println("Print Bill");
		    String filePath = System.getProperty("user.dir");
		    String filename = "";
		    filename = (filePath + "/Temp/TempBill1.pdf");
		   
		   		    
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
			    // System.out.println("Bill Printer Sel="+billPrinterName);
			    printerIndex = i;
			    break;
			}
		    }
		    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		    // DocPrintJob job = defaultService.createPrintJob();
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
	 
	 public int funPrintTakeAwayForJasper(String billHdTableName, String billNo) throws Exception
	    {
		int res = 0;
		String sqlTakeAway = "select strOperationType from " + billHdTableName + " "
			+ " where strBillNo='" + billNo + "'";
		ResultSet rsBill = st.executeQuery(sqlTakeAway);
		if (rsBill.next())
		{
		    if (rsBill.getString(1).equals("TakeAway"))
		    {
			res = 1;
		    }
		}
		rsBill.close();
		return res;
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


}
