package com.apos.util;

import java.io.FileInputStream;
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

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JRViewer;

import com.apos.bean.clsBillDtl;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Controller
public class clsConsolidatedKOTJasperGenerationForDirectBiller 
{
	@Autowired
	private ServletContext servletContext;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#.###");
	private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
	clsUtilityFunctions obUtility;
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection con = null;
	Statement st = null;
	String sql = null;
	
	
	public void funConsolidatedKOTForDirectBillerJasperFileGeneration(String AreaCode, String billNo, String Reprint, String posCode,String strConsolidatePrint)
	{
		try
		{
			HashMap hm = new HashMap();
			List<List<clsBillDtl>> listData = new ArrayList<>();
			PreparedStatement pst = null;
			con = objDb.funOpenAPOSCon("mysql", "Master");
			st = con.createStatement();
			
			hm.put("KOT","CONSOLIDATED KOT" );
			
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
		    rsSetupValues.close();
		    
			String sql_PrintHomeDelivery = "select strOperationType,strKOTToBillNote from tblbillhd where strBillNo=? ";
			pst = con.prepareStatement(sql_PrintHomeDelivery);
			pst.setString(1, billNo);
			ResultSet rs_PrintHomeDelivery = pst.executeQuery();
			String operationType = "", strBillOnNote = "";
			if (rs_PrintHomeDelivery.next())
			{
				operationType = rs_PrintHomeDelivery.getString(1);
				strBillOnNote = rs_PrintHomeDelivery.getString(2);
			}
			rs_PrintHomeDelivery.close();

			hm.put("Type", "");
			if (operationType.equalsIgnoreCase("HomeDelivery"))
			{
				hm.put("Type", "Home Delivery");
			}
			else if (operationType.equalsIgnoreCase("TakeAway"))
			{
				hm.put("Type", "Take Away");
			}
			
			String sql_PrintPOSName = "select strPosName from tblposmaster where strPosCode='"+posCode+"' ";
	        ResultSet rs_PrintPOSName = st.executeQuery(sql_PrintPOSName);
	        if(rs_PrintPOSName.next())
	        {
	        	String strPOSName = rs_PrintPOSName.getString(1);
	        	hm.put("POS", strPOSName);
	        }
			hm.put("DIRECT BILLER", "DIRECT BILLER");
			hm.put("BILL No", billNo);

			if (strBillOnNote.trim().length() > 0)
			{
				if(strBillOnNote.contains("  Manual Token No:  "))
				{
					hm.put("Manual Token No: ", strBillOnNote);
				}
			}
			
			String sql_DirectKOT_Date = "select dteBillDate from tblbilldtl where strBillNo=? ";
			pst = con.prepareStatement(sql_DirectKOT_Date);
			pst.setString(1, billNo);
			ResultSet rs_DirectKOT_Date = pst.executeQuery();
			if (rs_DirectKOT_Date.next())
			{
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
				hm.put("DATE & TIME", dateTimeFormat.format(rs_DirectKOT_Date.getObject(1)));
			}
			rs_DirectKOT_Date.close();

			/*String areaCodeForTransaction=clsGlobalVarClass.gAreaCodeForTrans;
			if(operationType.equalsIgnoreCase("HomeDelivery"))
			{
				areaCodeForTransaction=clsGlobalVarClass.gHomeDeliveryAreaForDirectBiller;
			}
			else if (operationType.equalsIgnoreCase("TakeAway"))
			{
				areaCodeForTransaction=clsGlobalVarClass.gTakeAwayAreaForDirectBiller;
			}
			else
			{
				areaCodeForTransaction=clsGlobalVarClass.gDineInAreaForDirectBiller;
			}*/

			String sql_DirectKOT_Items = "SELECT a.strItemCode,a.strItemName, SUM(a.dblQuantity) "
					+ "FROM tblbilldtl a "
					+ "WHERE a.strBillNo=?  "
					+ "GROUP BY a.strItemCode "
					+ "ORDER BY a.strSequenceNo;";
			//System.out.println(sql_DirectKOT_Items);
			//String areaCode = clsGlobalVarClass.gDineInAreaForDirectBiller;
			pst = con.prepareStatement(sql_DirectKOT_Items);
			pst.setString(1, billNo);
			List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
			ResultSet rs_DirectKOT_Items = pst.executeQuery();
			while (rs_DirectKOT_Items.next())
			{
				String itemName = rs_DirectKOT_Items.getString(2);
//                if (clsGlobalVarClass.gPrintShortNameOnKOT && !rs_DirectKOT_Items.getString(4).trim().isEmpty())
//                {
//                    itemName = rs_DirectKOT_Items.getString(4);
//                }

				clsBillDtl objBillDtl = new clsBillDtl();
				objBillDtl.setDblQuantity(Double.parseDouble(rs_DirectKOT_Items.getString(3)));
				objBillDtl.setStrItemName(itemName);
				listOfKOTDetail.add(objBillDtl);
				//following code called for modifier

				String sql_Modifier = " select a.strModifierName,a.dblQuantity,ifnull(b.strDefaultModifier,'N'),a.strDefaultModifierDeselectedYN "
						+ "from tblbillmodifierdtl a "
						+ "left outer join tblitemmodofier b on left(a.strItemCode,7)=if(b.strItemCode='',a.strItemCode,b.strItemCode) "
						+ "and a.strModifierCode=if(a.strModifierCode=null,'',b.strModifierCode) "
						+ "where a.strBillNo=? and left(a.strItemCode,7)=? ";
				//System.out.println(sql_Modifier);

				pst = con.prepareStatement(sql_Modifier);
				pst.setString(1, billNo);
				pst.setString(2, rs_DirectKOT_Items.getString(1));
				ResultSet rs_Modifier = pst.executeQuery();
				clsBillDtl objBillDtl1=null;
				while (rs_Modifier.next())
				{
					objBillDtl1 = new clsBillDtl();
					if (printModQtyOnKOT.equals("Y"))//dont't print modifier qty
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

			JasperDesign jd1 = JRXmlLoader.load(servletContext.getResourceAsStream("/WEB-INF/kotFormat/rptConsolidatedKOTJasperForDirectBiller.jrxml"));
    	    JasperReport jr1 = JasperCompileManager.compileReport(jd1);
		    JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(listData);
            final JasperPrint mainJaperPrint = JasperFillManager.fillReport(jr1, hm, beanColDataSource);
            final String printer=strConsolidatePrint;
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
	    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService[selectedService]);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printService[selectedService].getAttributes());
	    	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	    	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
	    	    exporter.exportReport();
	    	    
//	    	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
//	   	     	DocPrintJob job = printService[0].createPrintJob();
//	   		    FileInputStream fis = new FileInputStream(filename);
//	   		    DocAttributeSet das = new HashDocAttributeSet();
//	   		    Doc doc = new SimpleDoc(fis, flavor, das);
//	   		    //job.print(doc, printerReqAtt);
	
	     } catch (Exception e) {
	    	 
	    	 e.printStackTrace();
	     }	 
	}

}
