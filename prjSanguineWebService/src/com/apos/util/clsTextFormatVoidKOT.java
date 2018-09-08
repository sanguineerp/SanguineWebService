package com.apos.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsFileIOUtil;

@Controller
public class clsTextFormatVoidKOT {
	@Autowired
	clsTextFileGenerator objTextFileGenerator;

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
            
        	objTextFileGenerator.funCreateTempFolder();
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
            objTextFileGenerator. funPrintKOTTextFile(primaryPrinterName, secondaryPrinterName, "kot", "N", "Y", "N","KOT");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
}
