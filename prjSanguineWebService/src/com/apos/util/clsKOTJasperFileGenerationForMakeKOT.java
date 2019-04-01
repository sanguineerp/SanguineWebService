package com.apos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletContext;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.bean.clsBillDtl;
import com.itextpdf.awt.geom.Dimension;


import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

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
public class clsKOTJasperFileGenerationForMakeKOT {

	 @Autowired
	 private ServletContext servletContext;
	 
	 @Autowired
	 clsTextFileGenerator obTextFileGenerator;
	 
	 clsUtilityFunctions obUtility;
	 clsDatabaseConnection objDb = new clsDatabaseConnection();
	 Connection Con = null;
	 Statement st = null;
	 String sql = null;

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");


    public void funGenerateJasperForTableWiseKOT(String tableNo, 
    		String CostCenterCode, String AreaCode, String KOTNO, String Reprint,
    		String primaryPrinterName, String secondaryPrinterName, String CostCenterName,
    		String printYN, String NCKotYN, String labelOnKOT,String posName,String posCode,int noOfCopiesPrimaryPrinter,int noOfCopiesSecPrinter,String printOnBothPrinter,String deviceName,String macAddress)
    {
        HashMap hm = new HashMap();
        List<List<clsBillDtl>> listData = new ArrayList<>();

        try
        {
        	PreparedStatement pst = null;
        	 Con = objDb.funOpenAPOSCon("mysql", "Master");
			 st = Con.createStatement();
            boolean isReprint = false;
            if ("Reprint".equalsIgnoreCase(Reprint))
            {
                isReprint = true;
                hm.put("dublicate", "[DUPLICATE]");
            }
            if ("Y".equalsIgnoreCase(NCKotYN))
            {
                hm.put("KOTorNC", "NCKOT");
            }
            else
            {
                hm.put("KOTorNC", labelOnKOT);
            }
            hm.put("POS", posName);
            hm.put("costCenter", CostCenterName);

            String tableName = "";
            int pax = 0;
            String SQL_KOT_Dina_tableName = "select strTableName,intPaxNo "
                    + " from tbltablemaster "
                    + " where strTableNo='"+tableNo+"' and strOperational='Y'";
          
          
            ResultSet rs_Dina_Table = st.executeQuery(SQL_KOT_Dina_tableName);
            while (rs_Dina_Table.next())
            {
                tableName = rs_Dina_Table.getString(1);
                pax = rs_Dina_Table.getInt(2);
            }
            rs_Dina_Table.close();

            String areaWisePricing="N";
		       String printModQtyOnKOT="N";
		       String noOfLinesInKOTPrint="1";
		       String printShortNameOnKOT="N";
		       String printKOTYN="Y";
		       String multipleKOTPrint="N";
		       String multipleBillPrint="N";
		       String clientCode="",userName="",strPrintDeviceAndUserDtlOnKOTYN="Y";
		       
		       int columnSize=40;
		       
		       String sql="select strAreaWisePricing,strPrintShortNameOnKOT,strPrintModifierQtyOnKOT,strNoOfLinesInKOTPrint"
		       		+ ",strMultipleKOTPrintYN,strPrintKOTYN,strMultipleBillPrinting,intColumnSize,strClientCode,strPrintDeviceAndUserDtlOnKOTYN "
		       		+ "from tblsetup ";
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
			       strPrintDeviceAndUserDtlOnKOTYN=rsSetupValues.getString(10);
		       }
		       sql = "select strWaiterNo,strUserCreated from tblitemrtemp where strKOTNo='"+KOTNO+"' and strTableNo='"+tableNo+"' "
		           		+ " group by strKOTNo ;";
		       rsSetupValues = st.executeQuery(sql);
		       if (rsSetupValues.next()) 
	           {
		    	   userName=rsSetupValues.getString(2);
	           }
		       
            String sqlKOTItems = "";
            List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
            if (areaWisePricing.equals("Y"))
            {
                sqlKOTItems = "select LEFT(a.strItemCode,7),b.strItemName,a.dblItemQuantity,a.strKOTNo,a.strSerialNo,d.strShortName "
                        + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
                        + " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
                        + " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
                        + " and (b.strPOSCode=? or b.strPOSCode='All') "
                        + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? )) "
                        + " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
                        + " order by a.strSerialNo ";
            }
            else
            {
                sqlKOTItems = "select LEFT(a.strItemCode,7),b.strItemName,a.dblItemQuantity,a.strKOTNo,a.strSerialNo,d.strShortName "
                        + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
                        + " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
                        + " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
                        + " and (b.strPOSCode=? or b.strPOSCode='All') "
                        + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? ) "
                        + " OR b.strAreaCode ='" + AreaCode + "') "
                        + " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
                        + " order by a.strSerialNo ";
            }
            //System.out.println(sqlKOTItems);

            PreparedStatement pst_KOT_Items = Con.prepareStatement(sqlKOTItems);
            pst_KOT_Items.setString(1, tableNo);
            pst_KOT_Items.setString(2, KOTNO);
            pst_KOT_Items.setString(3, CostCenterCode);
            pst_KOT_Items.setString(4, posCode);
            pst_KOT_Items.setString(5, tableNo);
            //pst_KOT_Items.setString(5, AreaCode);
            String KOTType = "DINE";
           /* if (null != clsGlobalVarClass.hmTakeAway.get(tableNo))
            {
                KOTType = "Take Away";
            }*/
            //hm.put("KOTType", KOTType);
            /*if (clsGlobalVarClass.gCounterWise.equals("Yes"))
            {
                hm.put("CounterName", clsGlobalVarClass.gCounterName);
            }*/
            hm.put("KOT", KOTNO);
            hm.put("tableNo", tableName);
            if (clientCode.equals("124.001"))
            {
                hm.put("124.001", tableName);
            }
            hm.put("PAX", String.valueOf(pax));

            String sqlWaiterDtl = "select strWaiterNo from tblitemrtemp where strKOTNo=?  and strTableNo=? group by strKOTNo ;";
            pst = Con.prepareStatement(sqlWaiterDtl);
            pst.setString(1, KOTNO);
            pst.setString(2, tableNo);
            ResultSet rsWaiterDtl = pst.executeQuery();
            if (rsWaiterDtl.next())
            {
                if (!"null".equalsIgnoreCase(rsWaiterDtl.getString(1)) && rsWaiterDtl.getString(1).trim().length() > 0)
                {
                    sqlWaiterDtl = "select strWShortName from tblwaitermaster where strWaiterNo=? ;";
                    pst = Con.prepareStatement(sqlWaiterDtl);
                    pst.setString(1, rsWaiterDtl.getString(1));
                    ResultSet rs = pst.executeQuery();
                    rs.next();
                    hm.put("waiterName", rs.getString(1));
                    rs.close();
                }
            }
            rsWaiterDtl.close();
            String sql_KOTDate = "select dteDateCreated from tblitemrtemp where strKOTNo=?  and strTableNo=? group by strKOTNo ;";
            pst = Con.prepareStatement(sql_KOTDate);
            pst.setString(1, KOTNO);
            pst.setString(2, tableNo);
            ResultSet rs_KOTDate = pst.executeQuery();
            rs_KOTDate.next();

            hm.put("DATE_TIME", ddMMyyyyAMPMDateFormat.format(rs_KOTDate.getObject(1)));
            rs_KOTDate.close();
            InetAddress ipAddress = InetAddress.getLocalHost();
            //String hostName = ipAddress.getHostName();
            if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
            	hm.put("KOT From", deviceName);
                hm.put("kotByUser", userName);	
            }
            

            ResultSet rs_KOT_Items = pst_KOT_Items.executeQuery();
            while (rs_KOT_Items.next())
            {

                String itemName = rs_KOT_Items.getString(2);
                if (printShortNameOnKOT.equals("Y") && !rs_KOT_Items.getString(6).trim().isEmpty())
                {
                    itemName = rs_KOT_Items.getString(6);
                }

                clsBillDtl objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(Double.parseDouble(rs_KOT_Items.getString(3)));
                objBillDtl.setStrItemName(itemName);
                listOfKOTDetail.add(objBillDtl);
                String sql_Modifier = "select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
                        + " where a.strItemCode like'" + rs_KOT_Items.getString(1) + "M%' and a.strKOTNo='" + KOTNO + "' "
                        + " and strSerialNo like'" + rs_KOT_Items.getString(5) + ".%' "
                        + " group by a.strItemCode,a.strItemName ";
                //System.out.println(sql_Modifier);
                ResultSet rsModifierItems =st.executeQuery(sql_Modifier);
                while (rsModifierItems.next())
                {
                    objBillDtl = new clsBillDtl();
                    String modifierName = rsModifierItems.getString(1);
                    if (modifierName.startsWith("-->"))
                    {
                        if (printModQtyOnKOT.equals("Y"))
                        {
                            objBillDtl.setDblQuantity(Double.parseDouble(rsModifierItems.getString(2)));
                            objBillDtl.setStrItemName(rsModifierItems.getString(1));
                        }
                        else
                        {
                            objBillDtl.setDblQuantity(0);
                            objBillDtl.setStrItemName(rsModifierItems.getString(1));
                        }
                    }
                    listOfKOTDetail.add(objBillDtl);
                }
            }
            rs_KOT_Items.close();
            pst_KOT_Items.close();
            pst.close();

            for (int cntLines = 0; cntLines < Integer.parseInt(noOfLinesInKOTPrint); cntLines++)
            {
                clsBillDtl objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(0);
                objBillDtl.setStrItemName("");
                listOfKOTDetail.add(objBillDtl);
            }
            
            String strAreaWiseCostCenterKOTPrintingYN="N";
   			sql="select strAreaWiseCostCenterKOTPrintingYN from tblsetup where strPOSCode='"+posCode+"' or   strPOSCode='All'";
   			ResultSet rsPrinter = st.executeQuery(sql);
           if (rsPrinter.next()) {
        	   strAreaWiseCostCenterKOTPrintingYN=rsPrinter.getString(1);
           }
           rsPrinter.close();
           String secondary="",printOnBothPrinters="";
           if(strAreaWiseCostCenterKOTPrintingYN.equalsIgnoreCase("Y"))
           {
        	    String areaCodeOfTable = "";
           	    String sqlArea = "select strTableName,intPaxNo,strAreaCode "
           		    + " from tbltablemaster "
           		    + " where strTableNo='"+tableNo+"' "
           		    + " and strOperational='Y' ";
           	    ResultSet rsArea = st.executeQuery(sqlArea);
           	    if (rsArea.next())
           	    {
           	    	areaCodeOfTable = rsArea.getString(3);
           	    }
           	    rsArea.close();
        	   String sqlAreaWiseCostCenterKOTPrinting = "select a.strPrimaryPrinterPort,a.strSecondaryPrinterPort,a.strPrintOnBothPrintersYN "
       			    + "from tblprintersetupmaster a "
       			    + "where (a.strPOSCode='" + posCode + "' or a.strPOSCode='All') "
       			    + "and a.strAreaCode='" + areaCodeOfTable + "' "
       			    + "and a.strCostCenterCode='" + CostCenterCode + "' "
       			    + "and a.strPrinterType='Cost Center' ";
       		    rsPrinter = st.executeQuery(sqlAreaWiseCostCenterKOTPrinting);
       		    if (rsPrinter.next())
       		    {
       		    	primaryPrinterName = rsPrinter.getString(1);
       		    	secondaryPrinterName = rsPrinter.getString(2);
           			 printOnBothPrinters = rsPrinter.getString(3);
       		    }
       		    rsPrinter.close();   
           }
           else{
        	 
        	   rsPrinter = st.executeQuery("select a.strPrinterPort,a.strSecondaryPrinterPort,a.strPrintOnBothPrinters from tblcostcentermaster  a where a.strCostCenterCode='" + CostCenterCode + "' ");
               
               if (rsPrinter.next())
               {
            	   primaryPrinterName = rsPrinter.getString(1);
            	   secondaryPrinterName = rsPrinter.getString(2);
                    printOnBothPrinters = rsPrinter.getString(3);
               }
               rsPrinter.close();   
           }
            
           
            hm.put("listOfItemDtl", listOfKOTDetail);
            listData.add(listOfKOTDetail);
            
            JasperDesign jd1 = JRXmlLoader.load(servletContext.getResourceAsStream("/WEB-INF/kotFormat/rptGenrateKOTJasperReport.jrxml"));
    	    JasperReport jr1 = JasperCompileManager.compileReport(jd1);
           
            
            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(listData);
            //InputStream is = this.getClass().getClassLoader().getResourceAsStream(reportName);
            final JasperPrint mainJaperPrint = JasperFillManager.fillReport(jr1, hm, beanColDataSource);
            String printerName=primaryPrinterName;
            if(primaryPrinterName.equalsIgnoreCase("Not")){
            	printerName=secondaryPrinterName;
            }
            final String printer=printerName;
            System.out.println("jasper printer "+printer);
            JRViewer viewer = new JRViewer(mainJaperPrint);
            JFrame jf = new JFrame();
            jf.getContentPane().add(viewer);
            jf.validate();
            new Thread()
            {
                @Override
                public void run()
                {
                   funPrintJasperExporterInThread(mainJaperPrint,printer);// printer name
                }
            }.start();
            
            String filePath = System.getProperty("user.dir");
			 File pdfrpt = new File(filePath + "/Temp");
			    if (!pdfrpt.exists())
			    {
			    	pdfrpt.mkdirs();
			    }
			    
			JRExporter exporter = new JRPdfExporter();
		    exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, mainJaperPrint);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(filePath + "/Temp/kotprint-"+KOTNO+".pdf")); // your output goes here
			exporter.exportReport();
       
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void funPrintJasperExporterInThread(JasperPrint print,String billPrinterName)
    {

		 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		
		 int selectedService = 0;
	     //String billPrinterName = strBillPrinterPort;
		 String filePath = System.getProperty("user.dir");
		    String filename = "";
		    filename = (filePath + "/Temp/TempBill1.pdf");
		    
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
	    	    if(printService.length>0){
	    	    	exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService[selectedService]);
		    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printService[selectedService].getAttributes());
		    	    
	    	    }
	    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
	    	    exporter.exportReport();
	    	    
	    	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    	    if(printService.length>0){
	    	    	DocPrintJob job = printService[0].createPrintJob();	
	    	    }
	   		    /*FileInputStream fis = new FileInputStream(filename);
	   		    DocAttributeSet das = new HashDocAttributeSet();
	   		    Doc doc = new SimpleDoc(fis, flavor, das);*/
	   		    //job.print(doc, printerReqAtt);

	     } catch (Exception e) {
	    	 
	    	 e.printStackTrace();
	     }
		
	     
	 
    }
    
   /* public void funPrintJasperExporterInThread(JasperPrint print,String billPrinterName)
    {

		 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		
		 int selectedService = 0;
	     //String billPrinterName = strBillPrinterPort;
		 String filePath = System.getProperty("user.dir");
		    String filename = "";
		    filename = (filePath + "/Temp/TempBill1.pdf");
		    
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
	    	    
	    	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	   	     	DocPrintJob job = printService[0].createPrintJob();
	   		    FileInputStream fis = new FileInputStream(filename);
	   		    DocAttributeSet das = new HashDocAttributeSet();
	   		    Doc doc = new SimpleDoc(fis, flavor, das);
	   		    //job.print(doc, printerReqAtt);

	     } catch (Exception e) {
	    	 
	    	 e.printStackTrace();
	     }
		
    }*/

}
