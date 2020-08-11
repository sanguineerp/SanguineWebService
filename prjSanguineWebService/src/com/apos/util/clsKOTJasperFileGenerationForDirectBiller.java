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
import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.bean.clsBillDtl;
import com.webservice.controller.clsDatabaseConnection;

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
import net.sf.jasperreports.view.JRViewer;
@Controller
public class clsKOTJasperFileGenerationForDirectBiller
{
	@Autowired
	private ServletContext servletContext;
	
    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");

	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection con = null;
	Statement st = null;
	String sql = null;

	/*
	 * private clsUtility objUtility = new clsUtility(); private clsUtility2
	 * objUtility2 = new clsUtility2(); private clsPrintingUtility
	 * objPrintingUtility = new clsPrintingUtility();
	 */
    /**
     *
     * @param CostCenterCode
     * @param ShowKOT
     * @param AreaCode
     * @param BillNo
     * @param Reprint
     * @param primaryPrinterName
     * @param secondaryPrinterName
     * @param CostCenterName
     * @param labelOnKOT
     */
    //public void funConsolidatedKOTForDirectBillerJasperFileGeneration(String AreaCode, String billNo, String Reprint, String posCode,String strConsolidatePrint)
	
    public void funGenerateJasperForKOTDirectBiller(String CostCenterCode, String ShowKOT, String AreaCode, String BillNo, String Reprint, String primaryPrinterName, String secondaryPrinterName, String CostCenterName, String labelOnKOT,int primaryCopies,int secondaryCopies,String strPOSCode)
    {
        HashMap hm = new HashMap();
        List<List<clsBillDtl>> listData = new ArrayList<>();
        try
        {
        	PreparedStatement pst = null;
			
        	con = objDb.funOpenAPOSCon("mysql", "Master");
			st = con.createStatement();
			
            boolean isReprint = false;
	    String kotToBillNote="";
	    String kotNote="";
	    String delInstruction="";
            if ("Reprint".equalsIgnoreCase(Reprint))
            {
                isReprint = true;
                hm.put("dublicate", "[DUPLICATE]");
            }
	    
            else
            {
                hm.put("dublicate", "");
            }
	    
		    
            String areaWisePricing="N";
		    String printModQtyOnKOT="N";
		    String noOfLinesInKOTPrint="1";
		    String printShortNameOnKOT="N";
		    String printKOTYN="Y";
		    String multipleKOTPrint="N";
		    String multipleBillPrint="N",gPrintOrderNoOnBillYN="N";
		    String clientCode="",userName="",strPrintDeviceAndUserDtlOnKOTYN="Y",strPrintHomeDeliveryYN="N";
	        int columnSize=40;
	       String gHomeDeliveryAreaForDirectBiller="", gTakeAwayAreaForDirectBiller="", gDineInAreaForDirectBiller="";
		    String sql="select strAreaWisePricing,strPrintShortNameOnKOT,strPrintModifierQtyOnKOT,strNoOfLinesInKOTPrint"
	       		+ ",strMultipleKOTPrintYN,strPrintKOTYN,strMultipleBillPrinting,intColumnSize,strClientCode,strPrintDeviceAndUserDtlOnKOTYN,strPrintOrderNoOnBillYN,strPrintHomeDeliveryYN,"
	       		+ "strHomeDeliveryAreaForDirectBiller,strTakeAwayAreaForDirectBiller,strDirectAreaCode "
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
		       gPrintOrderNoOnBillYN=rsSetupValues.getString(11);
		       strPrintHomeDeliveryYN=rsSetupValues.getString(12);
		       gHomeDeliveryAreaForDirectBiller=rsSetupValues.getString(13);
			    gTakeAwayAreaForDirectBiller=rsSetupValues.getString(14);
			    gDineInAreaForDirectBiller=rsSetupValues.getString(15);
		    }
		    rsSetupValues.close();
		    String user="";
			String sql_PrintHomeDelivery = "select strOperationType,strKOTToBillNote,intOrderNo,strUserCreated from tblbillhd where strBillNo=? ";
			pst = con.prepareStatement(sql_PrintHomeDelivery);
			pst.setString(1, BillNo);
			ResultSet rs_PrintHomeDelivery = pst.executeQuery();
			String operationType = "", strBillOnNote = "";
			String orderNo="";
			if (rs_PrintHomeDelivery.next())
			{
				operationType = rs_PrintHomeDelivery.getString(1);
				strBillOnNote = rs_PrintHomeDelivery.getString(2);
				orderNo=rs_PrintHomeDelivery.getString(3);
				user=rs_PrintHomeDelivery.getString(4);
				
				if (gPrintOrderNoOnBillYN.equalsIgnoreCase("Y"))
				{
					hm.put("orderNo", "Your order no is " + rs_PrintHomeDelivery.getString(3));
				 }
			}
			rs_PrintHomeDelivery.close();

            
            
            rs_PrintHomeDelivery.close();
            hm.put("Type", "");
	    
	    if(kotToBillNote.contains("OrderFrom"))
	    {
		if(kotToBillNote.contains("#")) 
		{
		    delInstruction=kotToBillNote.split("#")[1];
		}
		hm.put("delInstruction",delInstruction);
		//print online order 
		if(kotToBillNote.length()>9){
		    kotNote=kotToBillNote.substring(9, kotToBillNote.indexOf(" "));
		}
		if (operationType.equalsIgnoreCase("HomeDelivery"))
		{
		    if (strPrintHomeDeliveryYN.equalsIgnoreCase("Y"))
		    {
			hm.put("Type", "Home Delivery ["+kotNote+"]");
		    }
		}
		else if (operationType.equalsIgnoreCase("TakeAway"))
		{
		    hm.put("Type", "Take Away ["+kotNote+"]");
		}
	    }else{
		if (operationType.equalsIgnoreCase("HomeDelivery"))
		{
		    if (strPrintHomeDeliveryYN.equalsIgnoreCase("Y"))
		    {
			hm.put("Type", "Home Delivery");
		    }
		}
		else if (operationType.equalsIgnoreCase("TakeAway"))
		{
		    hm.put("Type", "Take Away");
		}
	    }
			/*
			 * if(clsGlobalVarClass.gWERAOnlineOrderIntegration)//for online order {
			 * if(clsGlobalVarClass.gStrSelectedOnlineOrderFrom.equalsIgnoreCase("ZOMATO"))
			 * { CostCenterName="ZOMATO"; } else
			 * if(clsGlobalVarClass.gStrSelectedOnlineOrderFrom.equalsIgnoreCase("SWIGGY"))
			 * { CostCenterName="SWIGGY"; } }
			 */

	    String sql_PrintPOSName = "select strPosName from tblposmaster where strPosCode='"+strPOSCode+"' ";
        ResultSet rs_PrintPOSName = st.executeQuery(sql_PrintPOSName);
        if(rs_PrintPOSName.next())
        {
        	String strPOSName = rs_PrintPOSName.getString(1);
        	hm.put("POS", strPOSName);
        }
            hm.put("KOT", labelOnKOT);
            hm.put("CostCenter", CostCenterName);
            hm.put("DIRECT BILLER", "DIRECT BILLER");
            hm.put("BILL No", BillNo);

            //hm.put("kotByUser", clsGlobalVarClass.gUserCode);
	    
	    if (strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y"))
	    {
		InetAddress ipAddress = InetAddress.getLocalHost();
		String hostName = ipAddress.getHostName();
		hm.put("KOT From", hostName);
		hm.put("kotByUser", user);
		//String physicalAddress = objUtility.funGetCurrentMACAddress();
		hm.put("device_id", "");
	    }

            String sql_DirectKOT_Date = "select dteBillDate from tblbilldtl where strBillNo=? ";
            pst = con.prepareStatement(sql_DirectKOT_Date);
            pst.setString(1, BillNo);
            ResultSet rs_DirectKOT_Date = pst.executeQuery();
            if (rs_DirectKOT_Date.next())
            {
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                hm.put("DATE & TIME", dateTimeFormat.format(rs_DirectKOT_Date.getObject(1)));
            }
            rs_DirectKOT_Date.close();
	    
	    
	    
	    String areaCodeForTransaction=AreaCode;//clsGlobalVarClass.gAreaCodeForTrans;
			
			if (operationType.equalsIgnoreCase("HomeDelivery")) {
				areaCodeForTransaction = gHomeDeliveryAreaForDirectBiller;
			} else if (operationType.equalsIgnoreCase("TakeAway")) {
				areaCodeForTransaction = gTakeAwayAreaForDirectBiller;
			} else {
				areaCodeForTransaction = gDineInAreaForDirectBiller;
			}
 
            String sql_DirectKOT_Items = "select a.strItemCode,a.strItemName,sum(a.dblQuantity),d.strShortName,a.strSequenceNo "
                    + "from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
                    + "where  a.strBillNo=? and  b.strCostCenterCode=c.strCostCenterCode "
                    + "and a.strItemCode=d.strItemCode "
                    + "and b.strCostCenterCode=? and (b.strAreaCode=? or b.strAreaCode='" + areaCodeForTransaction + "') "
                    + "and a.strItemCode=b.strItemCode"
		    + " And b.strHourlyPricing='No' "
		    + "and (b.strPOSCode=? OR b.strPOSCode='All')   "
                    + "group by a.strItemCode,a.strSequenceNo "
                    + "ORDER BY a.strSequenceNo;;";
            pst = con.prepareStatement(sql_DirectKOT_Items);
            pst.setString(1, BillNo);
            pst.setString(2, CostCenterCode);
            pst.setString(3, AreaCode);
            pst.setString(4, strPOSCode);
            List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
            ResultSet rs_DirectKOT_Items = pst.executeQuery();
            while (rs_DirectKOT_Items.next())
            {

                String itemName = rs_DirectKOT_Items.getString(2);
                if (printShortNameOnKOT.equalsIgnoreCase("Y") && !rs_DirectKOT_Items.getString(4).trim().isEmpty())
                {
                    itemName = rs_DirectKOT_Items.getString(4);
                }

                clsBillDtl objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(Double.parseDouble(rs_DirectKOT_Items.getString(3)));
                objBillDtl.setStrItemName(itemName);
                listOfKOTDetail.add(objBillDtl);
		String seqNo=rs_DirectKOT_Items.getString(5);
		if(seqNo.length()>1){
		    seqNo=rs_DirectKOT_Items.getString(5).substring(0, 1);
		}
                String sql_Modifier = " select a.strModifierName,a.dblQuantity,ifnull(b.strDefaultModifier,'N'),a.strDefaultModifierDeselectedYN "
                        + "from tblbillmodifierdtl a "
                        + "left outer join tblitemmodofier b on left(a.strItemCode,7)=if(b.strItemCode='',a.strItemCode,b.strItemCode) "
                        + "and a.strModifierCode=if(a.strModifierCode=null,'',b.strModifierCode) "
                        + "where a.strBillNo=? "
			+ "and left(a.strItemCode,7)=? "
			+ " and left(a.strSequenceNo,1)='" + seqNo + "' ";
                //System.out.println(sql_Modifier);
                pst = con.prepareStatement(sql_Modifier);
                pst.setString(1, BillNo);
                pst.setString(2, rs_DirectKOT_Items.getString(1));
                ResultSet rs_Modifier = pst.executeQuery();
		clsBillDtl objBillDtl1=null;
                while (rs_Modifier.next())
                {
                    objBillDtl1 = new clsBillDtl();
                    if (printModQtyOnKOT.equalsIgnoreCase("Y"))//dont't print modifier qty
                    {
                        if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                        {
                            objBillDtl1.setDblQuantity(0);
                            objBillDtl1.setStrItemName("        " + "No " + rs_Modifier.getString(1));
                        }
                        else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
                        {
                            objBillDtl1.setDblQuantity(0);
                            objBillDtl1.setStrItemName(rs_Modifier.getString(1));
                        }else{
			    objBillDtl1.setDblQuantity(0);
                            objBillDtl1.setStrItemName(rs_Modifier.getString(1));
			}
                    }
                    else
                    {
                        if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                        {
                            objBillDtl1.setDblQuantity(Double.parseDouble(rs_Modifier.getString(2)));
                            objBillDtl1.setStrItemName("  " + rs_Modifier.getString(2) + "      " + "No " + rs_Modifier.getString(1));
                        }
                        else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
                        {
                            objBillDtl1.setDblQuantity(Double.parseDouble(rs_Modifier.getString(2)));
                            objBillDtl1.setStrItemName("  " + rs_Modifier.getString(2) + "      " + rs_Modifier.getString(1));
                        }
			else
			{
			    objBillDtl1.setDblQuantity(Double.parseDouble(rs_Modifier.getString(2)));
			    objBillDtl1.setStrItemName("  " + rs_Modifier.getString(2) + "      " + rs_Modifier.getString(1));
			}
			
                    }if(null!=objBillDtl1.getStrItemName()){
			listOfKOTDetail.add(objBillDtl1);
		    }
                }
                rs_Modifier.close();
            }
            rs_DirectKOT_Items.close();
            hm.put("listOfItemDtl", listOfKOTDetail);
            listData.add(listOfKOTDetail);
            String reportName = "WEB-INF/kotFormat/rptGenrateKOTJasperReportForDirectBiller.jrxml";
            JasperDesign jd1 = JRXmlLoader.load(servletContext.getResourceAsStream("/WEB-INF/kotFormat/rptGenrateKOTJasperReportForDirectBiller.jrxml"));
    	    JasperReport jr1 = JasperCompileManager.compileReport(jd1);
		    JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(listData);
            final JasperPrint mainJaperPrint = JasperFillManager.fillReport(jr1, hm, beanColDataSource);
            //final String printer=strConsolidatePrint;
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
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(filePath + "/Temp/kotprint-"+BillNo+".pdf")); // your output goes here
			exporter.exportReport();
			
			pst.close();
			st.close();
			con.close();
			
       
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
		 System.out.println("Print Bill");
		    String filePath = System.getProperty("user.dir");
		    String filename = "";
		    filename = (filePath + "/Temp/KOT.pdf");
		   
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

	    	    int printerIndex = 0;
			    PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
			    //DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			    //PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
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
			   // PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
			    
	    	    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
			    // DocPrintJob job = defaultService.createPrintJob();
			    DocPrintJob job = printService[printerIndex].createPrintJob();
			    FileInputStream fis = new FileInputStream(filename);
			    DocAttributeSet das = new HashDocAttributeSet();
			    Doc doc = new SimpleDoc(fis, flavor, das);
			    //job.print(doc, printerReqAtt);
	     } catch (Exception e) {
	    	 
	    	 e.printStackTrace();
	     }
		
	     
	 
    }
}
