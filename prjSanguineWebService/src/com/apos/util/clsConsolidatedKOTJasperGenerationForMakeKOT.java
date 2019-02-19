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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.bean.clsBillDtl;
import com.itextpdf.text.pdf.codec.Base64.InputStream;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Controller
public class clsConsolidatedKOTJasperGenerationForMakeKOT 
{
	@Autowired
	private ServletContext servletContext;
	 
	private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("hh:mm ");
    clsUtilityFunctions obUtility;
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection Con = null;
	Statement st = null;
	String sql = null;
    
    public void funConsolidatedKOTForMakeKOTJasperFileGeneration(String tableNo,String POSCode,
    	String POSName,String Reprint,String printYN,String kotType,String KOTNo,String strConsolidatePrint)
    {
		HashMap hm = new HashMap();
		List<List<clsBillDtl>> listData = new ArrayList<>();
	        
		try
	    {
		    PreparedStatement pst = null;
		    Con = objDb.funOpenAPOSCon("mysql", "Master");
			st = Con.createStatement();
			
		    hm.put("KOT",kotType);
		    hm.put("POS", POSName);
		    //hm.put("costCenter", CostCenterName);
	
		    String tableName = "";
		    String itemName = "";
		    String serialNo = "";
		    String strKotNo = "";
		    //int pax = 0;
		    String sql_KOT_Dina_tableName = "select strTableName,intPaxNo "
			    + " from tbltablemaster "
			    + " where strTableNo=? and strOperational='Y'";
		    pst = Con.prepareStatement(sql_KOT_Dina_tableName);
		    pst.setString(1, tableNo);
		    ResultSet rs_Dina_Table = pst.executeQuery();
		    while (rs_Dina_Table.next())
		    {
		    	tableName = rs_Dina_Table.getString(1);
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
		    
            List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
            String sql_CheckKot = "select a.strItemName, sum(a.dblItemQuantity),b.strTableName, "
                    + "TIME_FORMAT(time(a.dteDateCreated),'%h:%i'),ifnull(a.strWaiterNo,'') ,ifnull(d.strWFullName,''),a.strKOTNo,a.strSerialNo "
                    + "from tblitemrtemp a "
                    + "join tbltablemaster b on a.strTableNo=b.strTableNo  "
                    + "join tblitemmaster c on left(a.strItemCode,7)=c.strItemCode "
                    + "left outer join tblwaitermaster d on a.strWaiterNo=d.strWaiterNo "
                    + "where a.strTableNo=? "
                    + "and a.strKOTNo=? "
                    + "and a.strNCKotYN='N'  "
                    + "group by a.strItemCode,a.strItemName  "
                    + "order by a.strSerialNo; ";
            System.out.println(sql_CheckKot);
            pst = Con.prepareStatement(sql_CheckKot);
            pst.setString(1, tableNo);
            pst.setString(2, KOTNo);
		    ResultSet rs_checkKOT = pst.executeQuery();
		    while(rs_checkKOT.next())
		    {
				String waiterName = rs_checkKOT.getString(6);
				hm.put("tableName",tableName);
				hm.put("TIME", rs_checkKOT.getString(4));
				hm.put("waiterName", waiterName);
		
				clsBillDtl objBillDtl = new clsBillDtl();
				objBillDtl.setDblQuantity(Double.parseDouble(rs_checkKOT.getString(2)));
				objBillDtl.setStrItemName(rs_checkKOT.getString(1));
				listOfKOTDetail.add(objBillDtl);
		
				itemName = rs_checkKOT.getString(1);
				serialNo = rs_checkKOT.getString(8);
				strKotNo = rs_checkKOT.getString(7);
		    }
		    rs_checkKOT.close();
		    
		    String sql_Modifier = "select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
				+ " where a.strItemCode like'" + itemName + "M%' and a.strKOTNo='" + strKotNo + "' "
				+ " and strSerialNo like'" + serialNo + ".%' "
				+ " group by a.strItemCode,a.strItemName ";
		    //System.out.println(sql_Modifier);
		    ResultSet rsModifierItems = st.executeQuery(sql_Modifier);
		    while (rsModifierItems.next())
		    {
				clsBillDtl objBillDtl = new clsBillDtl();
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
		    rsModifierItems.close();
			
		    for (int cntLines = 0; cntLines < Integer.parseInt(noOfLinesInKOTPrint); cntLines++)
		    {
				clsBillDtl objBillDtl = new clsBillDtl();
				objBillDtl.setDblQuantity(0);
				objBillDtl.setStrItemName("  ");
				listOfKOTDetail.add(objBillDtl);
		    }
	
		    hm.put("listOfItemDtl", listOfKOTDetail);
		    listData.add(listOfKOTDetail);
		    
		    JasperDesign jd1 = JRXmlLoader.load(servletContext.getResourceAsStream("/WEB-INF/kotFormat/rptConsolidatedKOTJasperForMakeKOT.jrxml"));
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
	    	    
	    	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	   	     	DocPrintJob job = printService[0].createPrintJob();
	   		    FileInputStream fis = new FileInputStream(filename);
	   		    DocAttributeSet das = new HashDocAttributeSet();
	   		    Doc doc = new SimpleDoc(fis, flavor, das);
	   		    //job.print(doc, printerReqAtt);

	     } catch (Exception e) {
	    	 
	    	 e.printStackTrace();
	     }
		
	     
	 
    }
}

