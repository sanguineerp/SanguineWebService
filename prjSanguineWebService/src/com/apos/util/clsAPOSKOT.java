package com.apos.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsFileIOUtil;

@Controller
public class clsAPOSKOT {


	@Autowired
	clsTextFileGenerator obTextFileGenerator;
	
	@Autowired
	clsTextFormatVoidKOT objTextFormatVoidKOT;
	/*@Autowired
	private clsJasperBillPrinting objJasperBillPrinting;*/
	
	@Autowired 
	private clsKOTJasperFileGenerationForMakeKOT objKOTJasperFileGenerationForMakeKOT;
	
	@Autowired 
	private clsConsolidatedKOTJasperGenerationForDirectBiller objConsolidatedKOTJasperGenerationForDirectBiller;
	
	@Autowired
	clsAPOSUtility objAPOSUtility;
    /**
    *
    * @param BillNo
    * @param KOTNo
    * @param billNo
    * @param reprint
    * @param type
    */
	public void funGenerateTextFileForKOTForDirectBiller(String POSCode,String areaCode,String billNo, String reprint, String printYN) 
   {

	   clsDatabaseConnection objDb=new clsDatabaseConnection();
       Connection aposCon=null;
       Statement st=null;
       String sql="";
	   
       try 
       {
    	   String strPrintType="",strConsolidatePrint="";
    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
	       st = aposCon.createStatement();


                   sql = "select a.strItemName,c.strCostCenterCode,c.strPrinterPort"
                       + ",c.strSecondaryPrinterPort,c.strCostCenterName ,c.intPrimaryPrinterNoOfCopies,c.intSecondaryPrinterNoOfCopies "
                       + " from tblbilldtl  a,tblmenuitempricingdtl b,tblcostcentermaster c "
                       + " where a.strBillNo='"+billNo+"' and  a.strItemCode=b.strItemCode "
                       + " and b.strCostCenterCode=c.strCostCenterCode and b.strPosCode='"+POSCode+"' "
                       + " group by c.strCostCenterCode;";
                   
                   System.out.println("Query="+sql);
                   ResultSet rsPrintDirect = st.executeQuery(sql);
                   while (rsPrintDirect.next()) 
                   {
                       funGenerateTextFileForKOTDirectBiller(rsPrintDirect.getString(2), "",areaCode, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5),POSCode,printYN,rsPrintDirect.getInt(6),rsPrintDirect.getInt(7));
                   }
                   rsPrintDirect.close();
                   st.close();
                  
                   if (printYN.equalsIgnoreCase("Y") )//print consolidated KOT only 
	                {
	            	   funGenerateConsolidatedKOTTextFileForDirectBiller(areaCode, billNo,reprint, POSCode,printYN);
	                }
                 
           
           
       } catch (Exception e) 
       {
           e.printStackTrace();
       }
   }
   
	public String funGenerateTextFileForKOTDirectBiller(String CostCenterCode, String ShowKOT, String AreaCode, String BillNo, String Reprint, String primaryPrinterName, String secondaryPrinterName, String CostCenterName,String posCode,String printYN,int noOfCopiesPrimaryPrinter,int noOfCopiesSecPrinter)
   {
	   String result="";
	   clsDatabaseConnection objDb=new clsDatabaseConnection();
       Connection aposCon=null;
       Statement st=null,st2=null;
       clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
       String line = " ________________________________________";
	   
       try 
       {
    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
	       st = aposCon.createStatement();
	       st2=aposCon.createStatement();
           
	       // Get Setup Values for Printing.
	       
	      
	       String areaWisePricing="N";
	       String printModQtyOnKOT="N";
	       String noOfLinesInKOTPrint="1";
	       String printShortNameOnKOT="";
	       String printKOTYN="Y";
	       String multipleKOTPrint="N";
	       String multipleBillPrint="N";
	       String clientCode="";
	       int columnSize=40;
	    
	       obTextFileGenerator.funCreateTempFolder();
           String filePath = System.getProperty("user.dir");
           File textKOTFilePath = new File(filePath + "/Temp/Temp_KOT.txt");
           System.out.println(textKOTFilePath.getAbsolutePath());
           FileWriter fstream = new FileWriter(textKOTFilePath);
           //BufferedWriter KotOut = new BufferedWriter(fstream);
           BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textKOTFilePath),"UTF8"));
           
           if ("Reprint".equalsIgnoreCase(Reprint)) {
        	   objFileIOUtil.funPrintBlankSpace("DUPLICATE", KotOut, columnSize);
               KotOut.write("DUPLICATE");
               KotOut.newLine();
           }
           
           String sql_PrintHomeDelivery = "select strOperationType,strClientCode from tblbillhd where strBillNo='"+BillNo+"' ";
           ResultSet rs_PrintHomeDelivery = st.executeQuery(sql_PrintHomeDelivery);
           String operationType="";
           
           if (rs_PrintHomeDelivery.next())
            {
                operationType=rs_PrintHomeDelivery.getString(1);
                clientCode=rs_PrintHomeDelivery.getString(2);
            }
            rs_PrintHomeDelivery.close();
            if(operationType.equalsIgnoreCase("HomeDelivery"))
            {
            	objFileIOUtil.funPrintBlankSpace("Home Delivery", KotOut, columnSize);
            	KotOut.write(operationType);
                KotOut.newLine();
            }
            else if(operationType.equalsIgnoreCase("TakeAway"))
            {
            	objFileIOUtil.funPrintBlankSpace("Take Away", KotOut, columnSize);
                KotOut.write(operationType);
                KotOut.newLine();
            }
            if(clientCode.equals("047.001"))
            {
            	printShortNameOnKOT="N";
            }
            
            objFileIOUtil.funPrintBlankSpace("KOT", KotOut, columnSize);
	        KotOut.write("KOT");
	        KotOut.newLine();
           
	        String sql_PrintPOSName = "select strPosName from tblposmaster where strPosCode='"+posCode+"' ";
	        ResultSet rs_PrintPOSName = st.executeQuery(sql_PrintPOSName);
	         
	           if (rs_PrintPOSName.next())
	            {
	        	   objFileIOUtil.funPrintBlankSpace(rs_PrintPOSName.getString(1), KotOut, columnSize);
		           KotOut.write(rs_PrintPOSName.getString(1));
	            }
	           rs_PrintPOSName.close();
          
	       
           KotOut.newLine();
           objFileIOUtil.funPrintBlankSpace(CostCenterName, KotOut,columnSize);
           KotOut.write(CostCenterName);
           KotOut.newLine();
      
           
           objFileIOUtil.funPrintBlankSpace("Direct Biller", KotOut,columnSize);
           KotOut.write("Direct Biller");
           KotOut.newLine();
           KotOut.write(line);
           KotOut.newLine();

           
           KotOut.write("  Bill No   :" +  BillNo + " ");
        
           String sql_DirectKOT_Date = "select date(dteBillDate), time(dteBillDate) from tblbilldtl where strBillNo='"+BillNo+"' ";
            ResultSet rs_DirectKOT_Date = st.executeQuery(sql_DirectKOT_Date);
           if (rs_DirectKOT_Date.next())
           {
               KotOut.newLine();
               KotOut.write("  DATE & TIME: " + rs_DirectKOT_Date.getString(1) + " " + rs_DirectKOT_Date.getString(2));
           }

           rs_DirectKOT_Date.close();
          
           
           KotOut.newLine();
           KotOut.write(line);
           KotOut.newLine();
           KotOut.write("  QTY         ITEM NAME  ");
           KotOut.newLine();
           KotOut.write(line);
           
           String itemName="b.strItemName";
           if(printShortNameOnKOT.equals("Y"))
           {
               itemName="d.strShortName";
           }
           
           

           String sql_DirectKOT_Items = "select a.strItemCode," + itemName + ",a.dblQuantity "
               + "from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
               + "where  a.strBillNo='"+BillNo+"' and  b.strCostCenterCode=c.strCostCenterCode "
               + "and a.strItemCode=d.strItemCode "
               + "and b.strCostCenterCode='"+CostCenterCode+"' and (b.strAreaCode='"+AreaCode+"' or b.strAreaCode='" + AreaCode + "') "
               + "and a.strItemCode=b.strItemCode group by a.strItemCode;";
           
          System.out.println("sql_DirectKOT_Items Query="+sql_DirectKOT_Items);
           
           ResultSet rs_DirectKOT_Items = st.executeQuery(sql_DirectKOT_Items);
           while (rs_DirectKOT_Items.next())
           {
        	  // System.out.println("Items\n");
        	  // System.out.println(CostCenterName + "\t" + rs_DirectKOT_Items.getString(2).toUpperCase());
               KotOut.newLine();
               KotOut.write("  " + rs_DirectKOT_Items.getString(3) + "      " + rs_DirectKOT_Items.getString(2).toUpperCase());
             
               
               String sql_Modifier=" select b.strModifierName ,b.dblQuantity,a.strDefaultModifier,b.strDefaultModifierDeselectedYN "
                   +" from tblitemmodofier a,tblbillmodifierdtl b "
                   +" where a.strItemCode=left(b.strItemCode,7) "
                   +" and a.strModifierCode=b.strModifierCode "
                   +" and b.strBillNo='"+BillNo+"' and left(b.strItemCode,7)='"+rs_DirectKOT_Items.getString(1)+"' ";
               
              System.out.println("Mod Items Query="+sql_Modifier);
               ResultSet rs_Modifier = st2.executeQuery(sql_Modifier);
               while (rs_Modifier.next())
               {
            	   if(printShortNameOnKOT.equals("Y"))
            	   {
            		   if(rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                       {
                           KotOut.newLine();
                           KotOut.write("  " + rs_Modifier.getString(2) + "      " +"No "+ rs_Modifier.getString(1).toUpperCase());
                       }
                       else if(rs_Modifier.getString(3).equalsIgnoreCase("N"))
                       {
                           KotOut.newLine();
                           KotOut.write("  " + rs_Modifier.getString(2) + "      " + rs_Modifier.getString(1).toUpperCase());
                       } 
            	   }
            	   else
            	   {
            		   if(rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                       {
                           KotOut.newLine();
                           KotOut.write("       "+ rs_Modifier.getString(1).toUpperCase());
                       }
                       else if(rs_Modifier.getString(3).equalsIgnoreCase("N"))
                       {
                           KotOut.newLine();
                           KotOut.write( "      "+ rs_Modifier.getString(1).toUpperCase());
                       } 
            		   
            	   }
                                         
               }
              
               rs_Modifier.close();
              
           }

           rs_DirectKOT_Items.close();
           
           
           KotOut.newLine();
           KotOut.write(line);
           KotOut.newLine();
           KotOut.newLine();
           KotOut.newLine();
           KotOut.newLine();
           KotOut.newLine();
           KotOut.newLine();
           
           
           for(int cntLines=0;cntLines<Integer.parseInt(noOfLinesInKOTPrint);cntLines++)
           {
               KotOut.newLine();
           }
           
           KotOut.write("m");//windows
           KotOut.close();
           fstream.flush();
           fstream.close();
        
           if(printYN.equals("Y"))
           {
        	   result=obTextFileGenerator.funPrintKOTTextFile(primaryPrinterName,secondaryPrinterName, "kot",multipleKOTPrint, printKOTYN, multipleBillPrint,"KOT", noOfCopiesPrimaryPrinter, noOfCopiesSecPrinter,"N",Reprint);
           }
           
       } 
       catch (Exception e) 
       {
           
           e.printStackTrace();
       }
       
       return result;
	   
   }
   
	public StringBuilder funGenerateTextFileForKOTDirectBillerForBluetooth(String CostCenterCode, String ShowKOT, String AreaCode, String BillNo, String Reprint, String primaryPrinterName, String secondaryPrinterName, String CostCenterName,String posCode,String printYN)
   {
	   String result="";
	   clsDatabaseConnection objDb=new clsDatabaseConnection();
       Connection aposCon=null;
       Statement st=null,st2=null;
       clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
       String line = " ----------------------------------------";
       StringBuilder sbPrintKot = new StringBuilder();
	   
       try 
       {
    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
	       st = aposCon.createStatement();
	       st2=aposCon.createStatement();
           
	       // Get Setup Values for Printing.
	       
	      
	       String areaWisePricing="N";
	       String printModQtyOnKOT="N";
	       String noOfLinesInKOTPrint="1";
	       String printShortNameOnKOT="";
	       String printKOTYN="Y";
	       String multipleKOTPrint="N";
	       String multipleBillPrint="N";
	       String clientCode="";
	       int columnSize=40;
	       //clsTextFileGenerator obTextFileGenerator=new clsTextFileGenerator();
	    
	      
           if ("Reprint".equalsIgnoreCase(Reprint)) 
           {
        	   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("DUPLICATE", 40, "Left"));
               sbPrintKot.append("\n");
           }
           
           String sql_PrintHomeDelivery = "select strOperationType,strClientCode from tblbillhd where strBillNo='"+BillNo+"' ";
           ResultSet rs_PrintHomeDelivery = st.executeQuery(sql_PrintHomeDelivery);
           String operationType="";
           
           if (rs_PrintHomeDelivery.next())
            {
                operationType=rs_PrintHomeDelivery.getString(1);
                clientCode=rs_PrintHomeDelivery.getString(2);
            }
            rs_PrintHomeDelivery.close();
            if(operationType.equalsIgnoreCase("HomeDelivery"))
            {
            	sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("Home Delivery", 40, "Center"));
	            sbPrintKot.append("\n");
            }
            else if(operationType.equalsIgnoreCase("TakeAway"))
            {
            	sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("Take Away", 40, "Center"));
	            sbPrintKot.append("\n");
            }
            if(clientCode.equals("047.001"))
            {
            	printShortNameOnKOT="N";
            }
            
            sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("KOT", 40, "Center"));
            sbPrintKot.append("\n");
           
	        String sql_PrintPOSName = "select strPosName from tblposmaster where strPosCode='"+posCode+"' ";
	        ResultSet rs_PrintPOSName = st.executeQuery(sql_PrintPOSName);
	         
	           if (rs_PrintPOSName.next())
	            {
	        	   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(rs_PrintPOSName.getString(1), 40, "Center"));
		           sbPrintKot.append("\n");
	            }
	           rs_PrintPOSName.close();
          
	         if(!CostCenterName.isEmpty()) 
	         {
	        	 sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(CostCenterName, 40, "Center"));
		         sbPrintKot.append("\n");
	         }
         
	         sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("Direct Biller", 40, "Center"));
	         sbPrintKot.append("\n");
	         sbPrintKot.append(line);
	         sbPrintKot.append("\n");
	         
	        sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(" Bill No : " +  BillNo + " ", 20, "Left"));
	       

            String billDate="";
            String sql_DirectKOT_Date = "select date(dteBillDate), time(dteBillDate) from tblbilldtl where strBillNo='"+BillNo+"' ";
            ResultSet rs_DirectKOT_Date = st.executeQuery(sql_DirectKOT_Date);
            if (rs_DirectKOT_Date.next())
            {
        	   billDate=rs_DirectKOT_Date.getString(1);
            }
            if(!billDate.isEmpty())
            {
        	   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(" DATE : " +  rs_DirectKOT_Date.getString(1) + " ", 20, "Left")); 
            }
            rs_DirectKOT_Date.close();
          
           sbPrintKot.append("\n");
           sbPrintKot.append(line);
           sbPrintKot.append("\n");
           sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(" QTY  ITEM NAME                        ", 40, "Left"));
           sbPrintKot.append("\n");
           sbPrintKot.append(line);
           
         
           
           String itemName="b.strItemName";
           if(printShortNameOnKOT.equals("Y"))
           {
               itemName="d.strShortName";
           }
           
           

           String sql_DirectKOT_Items = "select a.strItemCode," + itemName + ",a.dblQuantity "
               + "from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
               + "where  a.strBillNo='"+BillNo+"' and  b.strCostCenterCode=c.strCostCenterCode "
               + "and a.strItemCode=d.strItemCode "
               + "and b.strCostCenterCode='"+CostCenterCode+"' and (b.strAreaCode='"+AreaCode+"' or b.strAreaCode='" + AreaCode + "') "
               + "and a.strItemCode=b.strItemCode group by a.strItemCode;";
           
          System.out.println("sql_DirectKOT_Items Query="+sql_DirectKOT_Items);
           
           ResultSet rs_DirectKOT_Items = st.executeQuery(sql_DirectKOT_Items);
           while (rs_DirectKOT_Items.next())
           {
        	   sbPrintKot.append("\n");
        	   String []arrData=rs_DirectKOT_Items.getString(3).split("\\.");
               int qty=Integer.parseInt(arrData[0]);
               
               sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(String.valueOf(qty) , 4, "RIGHT"));
           	   if(rs_DirectKOT_Items.getString(2).length()>34)
        		{
           		    sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("  " + rs_DirectKOT_Items.getString(2).toUpperCase(), 34, "Left"));
           		    sbPrintKot.append("\n");
           		    sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("      " + rs_DirectKOT_Items.getString(2).toString().substring(34,rs_DirectKOT_Items.getString(2).toUpperCase().length()), 34, "Left"));
        		}
        		else
        		{
        			sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("  "+rs_DirectKOT_Items.getString(2).toUpperCase(), 34, "Left"));
        		}
        	   
             
               
               String sql_Modifier=" select b.strModifierName ,b.dblQuantity,a.strDefaultModifier,b.strDefaultModifierDeselectedYN "
                   +" from tblitemmodofier a,tblbillmodifierdtl b "
                   +" where a.strItemCode=left(b.strItemCode,7) "
                   +" and a.strModifierCode=b.strModifierCode "
                   +" and b.strBillNo='"+BillNo+"' and left(b.strItemCode,7)='"+rs_DirectKOT_Items.getString(1)+"' ";
               
              System.out.println("Mod Items Query="+sql_Modifier);
               ResultSet rs_Modifier = st2.executeQuery(sql_Modifier);
               while (rs_Modifier.next())
               {
            	   if(printShortNameOnKOT.equals("Y"))
            	   {
            		   String []arrModData=rs_Modifier.getString(2).split("\\.");
                       int modqty=Integer.parseInt(arrData[0]);
                   
                       if(rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                       {
            			   sbPrintKot.append("\n");
            			   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(String.valueOf(modqty) , 4, "RIGHT"));
            			   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("  No "+ rs_Modifier.getString(1).toUpperCase(), 34, "Left"));
            			   
                       }
                       else if(rs_Modifier.getString(3).equalsIgnoreCase("N"))
                       {
                    	   sbPrintKot.append("\n");
                    	   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment(String.valueOf(modqty) , 4, "RIGHT"));
            			   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("  No "+ rs_Modifier.getString(1).toUpperCase(), 34, "Left"));
            			   
                       } 
            	   }
            	   else
            	   {
            		   if(rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                       {
            			   sbPrintKot.append("\n");
                           sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("    ", 4, "RIGHT"));
            			   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("  "+rs_Modifier.getString(1).toUpperCase(), 34, "Left"));
            			   
                       }
                       else if(rs_Modifier.getString(3).equalsIgnoreCase("N"))
                       {
                    	   sbPrintKot.append("\n");
                           sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("    ", 4, "RIGHT"));
            			   sbPrintKot.append(obTextFileGenerator.funPrintTextWithAlignment("  "+rs_Modifier.getString(1).toUpperCase(), 34, "Left"));
                       } 
            		   
            	   }
                                         
               }
              
               rs_Modifier.close();
              
           }

           rs_DirectKOT_Items.close();
           
           
           sbPrintKot.append("\n");
           sbPrintKot.append("\n");
           sbPrintKot.append(line);
           sbPrintKot.append("\n");
           sbPrintKot.append("\n");
           
           
           
           
           for(int cntLines=0;cntLines<Integer.parseInt(noOfLinesInKOTPrint);cntLines++)
           {
        	   sbPrintKot.append("\n");
           }
           
           sbPrintKot.append("m");//windows
          
        
           
       } 
       catch (Exception e) 
       {
           
           e.printStackTrace();
       }
       
       return sbPrintKot;
	   
   }
   
   public String funWriteKOTDetailsToTextFile(String tableNo, String costCenterCode, String showKOT, String areaCode, String KOTNo 
		   , String Reprint, String primaryPrinterName, String secondaryPrinterName, String costCenterName,String printYN 
		   , String POSCode, String POSName,String NCKOT,String deviceName,String macAddress,String labelOnKOT,String fireCommunication,int noOfCopiesPrimaryPrinter,int noOfCopiesSecPrinter,String printOnBothPrinter) 
   {      
	   String result="";
	   clsDatabaseConnection objDb=new clsDatabaseConnection();
       Connection aposCon=null;
       Statement st=null;
       clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
       String line = "  --------------------------------------";
	   String userName="";
       try 
       {
    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
	       st = aposCon.createStatement();
           
	       // Get Setup Values for Printing.
	       
	       String areaWisePricing="N";
	       String printModQtyOnKOT="N";
	       String noOfLinesInKOTPrint="1";
	       String printShortNameOnKOT="N";
	       String printKOTYN="Y";
	       String multipleKOTPrint="N";
	       String multipleBillPrint="N";
	       String clientCode="",strPrintDeviceAndUserDtlOnKOTYN="Y";
	       
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
	       
	       
	       obTextFileGenerator.funCreateTempFolder();
           String filePath = System.getProperty("user.dir");
           File textKOTFilePath = new File(filePath + "/Temp/Temp_KOT.txt");
           System.out.println(textKOTFilePath.getAbsolutePath());
           FileWriter fstream = new FileWriter(textKOTFilePath);
           //BufferedWriter KotOut = new BufferedWriter(fstream);
           BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textKOTFilePath),"UTF8"));
           if ("Reprint".equalsIgnoreCase(Reprint)) {
        	   objFileIOUtil.funPrintBlankSpace("DUPLICATE", KotOut, columnSize);
               KotOut.write("DUPLICATE");
               KotOut.newLine();
           }
           System.out.println(NCKOT);
           if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
        	   if(NCKOT.equalsIgnoreCase("Y"))
	           {
	        	   
	        	   objFileIOUtil.funPrintBlankSpace("NCKOT", KotOut, columnSize);
		           KotOut.write("NCKOT");
		           KotOut.newLine(); 
	           }
	           else
	           {
		           objFileIOUtil.funPrintBlankSpace(labelOnKOT, KotOut, columnSize);
		           KotOut.write(labelOnKOT);
		           KotOut.newLine();
	           } 
        	   objFileIOUtil.funPrintBlankSpace(POSName, KotOut, columnSize);
	           KotOut.write(POSName);
           }
           
           
           KotOut.newLine();
           objFileIOUtil.funPrintBlankSpace(costCenterName, KotOut,columnSize);
           KotOut.write(costCenterName);
           KotOut.newLine();

       //item will pickup from tblitemrtemp
           String tableName = "";
           int pax = 0;
           sql = "select strTableName,intPaxNo from tbltablemaster "
           		+ "where strTableNo='"+tableNo+"' and strOperational='Y'";
           st.close();
           st=aposCon.createStatement();
           ResultSet rs = st.executeQuery(sql);
           while (rs.next()) 
           {
               tableName = rs.getString(1);
               pax = rs.getInt(2);
           }
           rs.close();
           if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
        	   KotOut.write(line);
	           KotOut.newLine();
	           KotOut.write("  KOT NO     :");
	           KotOut.write(KOTNo + "  ");
           }
          
           KotOut.newLine();
           KotOut.write("  TABLE NO :");
           KotOut.write(tableName + "  ");
           
           if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
	           KotOut.write(" PAX   :");
	           KotOut.write(String.valueOf(pax));
	           KotOut.newLine();
           }
           
           st.close();
           st=aposCon.createStatement();
           sql = "select strWaiterNo,strUserCreated from tblitemrtemp where strKOTNo='"+KOTNo+"' and strTableNo='"+tableNo+"' "
	           		+ " group by strKOTNo ;";
	           rs = st.executeQuery(sql);
	           
	           if (rs.next()) 
	           {
	        	   userName=rs.getString(2);
	               if (!"null".equalsIgnoreCase(rs.getString(1)) && rs.getString(1).trim().length() > 0) {
	                   
	            	   Statement st1=aposCon.createStatement();
	            	   sql = "select strWShortName from tblwaitermaster where strWaiterNo='"+rs.getString(1)+"' ;";
	                   ResultSet rsWaiterInfo = st1.executeQuery(sql);
	                   if(rsWaiterInfo.next())
	                   {
	                	   if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
	                		   KotOut.write("  WAITER NAME:" + "   " + rsWaiterInfo.getString(1));
			                   KotOut.newLine();
	                	   }
	                   }
	                   rsWaiterInfo.close();
	                   st1.close();
	               }
	           }
	           rs.close();
           
           sql = "select  DATE_FORMAT(dteDateCreated,\"%d-%m-%Y\"),TIME_FORMAT(time(dteDateCreated),\"%h:%i %p\")"
           		+ " from tblitemrtemp where strKOTNo='"+KOTNo+"' and strTableNo='"+tableNo+"' group by strKOTNo;";
           Statement st1=aposCon.createStatement();
           rs = st1.executeQuery(sql);
           if(rs.next())
           {
        	   KotOut.newLine();
        	   KotOut.write("  DATE & TIME:" + rs.getString(1)+" "+rs.getString(2));
           }
           rs.close();
           st1.close();
         
            if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
            	KotOut.newLine();
	            KotOut.write("  KOT From Device:" + deviceName);
	            KotOut.newLine();
	            KotOut.write("  Device ID:" + macAddress);
	            KotOut.newLine();
	            KotOut.write("  KOT By User :" + userName);
	            KotOut.newLine();
            }
            if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
               KotOut.newLine();
 	           KotOut.write(line);
 	           KotOut.newLine();
 	           KotOut.write("  QTY         ITEM NAME  ");
 	            	
            }
            KotOut.newLine();
            KotOut.write(line);
           
//           String itemName="b.strItemName";
//           if(printShortNameOnKOT.equals("Y") )
//           {
//               itemName="d.strShortName";
//           }
           
         // Code to Print KOT Item details    
    	    String filter = "";
    	    String printItemQty = "a.dblItemQuantity";
    	    if (fireCommunication.equalsIgnoreCase("Y"))
    	    {
    	    	printItemQty = "a.dblPrintQty";
    	    	filter = " and a.dblPrintQty>0 ";
    	    }
           
           if(areaWisePricing.equals("Y"))
           {
               sql = "select LEFT(a.strItemCode,7),b.strItemName,"+ printItemQty + ",a.strKOTNo,a.strSerialNo,d.strShortName "
                   + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
                   + " where a.strTableNo='"+tableNo+"' and a.strKOTNo='"+KOTNo+"' and b.strCostCenterCode=c.strCostCenterCode "
                   + " and b.strCostCenterCode='"+costCenterCode+"' and a.strItemCode=d.strItemCode "
                   + " AND (b.strPOSCode='"+POSCode+"' OR b.strPOSCode='All') "
                   + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' )) "
                   + " and LEFT(a.strItemCode,7)=b.strItemCode AND b.strHourlyPricing='No' "
                   + " "+ filter
                   + " order by a.strSerialNo ";
           }
           else
           {
               sql = "select LEFT(a.strItemCode,7),b.strItemName,"+ printItemQty + ",a.strKOTNo,a.strSerialNo,d.strShortName "
                   + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
                   + " where a.strTableNo='"+tableNo+"' and a.strKOTNo='"+KOTNo+"' and b.strCostCenterCode=c.strCostCenterCode "
                   + " and b.strCostCenterCode='"+costCenterCode+"' and a.strItemCode=d.strItemCode "
                   + " AND (b.strPOSCode='"+POSCode+"' OR b.strPOSCode='All') "
                   + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) "
                   + " OR b.strAreaCode ='"+areaCode+"') "
                   + " and LEFT(a.strItemCode,7)=b.strItemCode AND b.strHourlyPricing='No' "
                   + " " + filter
                   + " order by a.strSerialNo ";
           
           }
           System.out.println(sql);
           
           st.close();
           st=aposCon.createStatement();
           ResultSet rsKOTDetails = st.executeQuery(sql);
           while (rsKOTDetails.next()) 
           {
               KotOut.newLine();
               String itemName = rsKOTDetails.getString(2);//full name
               if (printShortNameOnKOT.equals("Y") && !rsKOTDetails.getString(6).trim().isEmpty())
               {
            	   itemName = rsKOTDetails.getString(6);//short name
               }
               
               String itemqty = rsKOTDetails.getString(3);
               if (itemqty.length() == 5) 
               {
                   KotOut.write(" " + rsKOTDetails.getString(3) + "       " + itemName);
               }
               else if (itemqty.length() == 4) 
               {
                   KotOut.write("  " + rsKOTDetails.getString(3) + "       " + itemName);
               }
               else if (itemqty.length() == 6) 
               {
                   KotOut.write("" + rsKOTDetails.getString(3) + "       " + itemName);
               }
               String itemCode=rsKOTDetails.getString(1);
               String serialNo=rsKOTDetails.getString(5);
               System.out.println("itemCode="+itemCode);
               
               String sqlModifier="select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
	                   + " where a.strItemCode like'"+rsKOTDetails.getString(1)+"M%' and a.strKOTNo='"+KOTNo+"' "
	                   //+ " and strSerialNo like'"+rsKOTDetails.getString(5)+".%' "
	                   + " group by a.strItemCode,a.strItemName ";
               System.out.println("sqlModifier="+sqlModifier);
               
	           Statement st2=aposCon.createStatement();
               ResultSet rsModifierItems=st2.executeQuery(sqlModifier);
               while(rsModifierItems.next())
               {
                   String modQty=rsModifierItems.getString(2);
                   String modifierName=rsModifierItems.getString(1);
                   KotOut.newLine();
                       if(printModQtyOnKOT.equals("Y"))
                       {
                           if (modQty.length() == 5) 
                           {
                               KotOut.write(" " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
                           }
                           else if (modQty.length() == 4) 
                           {
                               KotOut.write("  " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
                           }
                           else if (modQty.length() == 6) 
                           {
                               KotOut.write("" + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
                           }
                       }
                       else
                       {
                           if (modQty.length() == 5) 
                           {
                               KotOut.write("            " + rsModifierItems.getString(1));
                           }
                           else if (modQty.length() == 4) 
                           {
                               KotOut.write("             " + rsModifierItems.getString(1));
                           }
                           else if (modQty.length() == 6) 
                           {
                               KotOut.write("           " + rsModifierItems.getString(1));
                           }
                       }
               }
               st2.close();
               rsModifierItems.close();
           }
           rsKOTDetails.close();
           if(strPrintDeviceAndUserDtlOnKOTYN.equalsIgnoreCase("Y")){
        	   KotOut.newLine();
	           KotOut.write(line);
	           
           }
           
           for(int cntLines=0;cntLines<Integer.parseInt(noOfLinesInKOTPrint);cntLines++)
           {
               KotOut.newLine();
           }
           
           KotOut.write("m");//windows
           KotOut.close();
           fstream.flush();
           fstream.close();
           
           if(printYN.equals("Y"))
           {
        	   String strAreaWiseCostCenterKOTPrintingYN="N";
      			sql="select strAreaWiseCostCenterKOTPrintingYN from tblsetup where strPOSCode='"+POSCode+"' ";
      			ResultSet rsPrinter = st.executeQuery(sql);
              if (rsPrinter.next()) {
           	   strAreaWiseCostCenterKOTPrintingYN=rsPrinter.getString(1);
              }
              rsPrinter.close();
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
           			    + "where (a.strPOSCode='" + POSCode + "' or a.strPOSCode='All') "
           			    + "and a.strAreaCode='" + areaCodeOfTable + "' "
           			    + "and a.strCostCenterCode='" + costCenterCode + "' "
           			    + "and a.strPrinterType='Cost Center' ";
           		    rsPrinter = st.executeQuery(sqlAreaWiseCostCenterKOTPrinting);
           		    if (rsPrinter.next())
           		    {
           		    	primaryPrinterName = rsPrinter.getString(1);
           		    	secondaryPrinterName = rsPrinter.getString(2);
           		    	// = rsPrinter.getString(3);
           		    }
           		    rsPrinter.close();   
               }
               else{
            	   
               }
            	
        	   
        	   result=obTextFileGenerator.funPrintKOTTextFile(primaryPrinterName,secondaryPrinterName, "kot",multipleKOTPrint, printKOTYN, multipleBillPrint,"KOT",noOfCopiesPrimaryPrinter,noOfCopiesSecPrinter,printOnBothPrinter,Reprint);
           }
           
       } 
       catch (Exception e) 
       {
           
           e.printStackTrace();
       }
       return result;
   }
   
   
   public String funWriteMasterKOTDetailsToTextFile(String tableNo,String areaCode,String POSCode,String POSName,String Reprint,String printYN) 
   {       
	   String result="";
	   clsDatabaseConnection objDb=new clsDatabaseConnection();
       Connection aposCon=null;
       Statement st=null;
       clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
       String line = "  --------------------------------------",tableName="",waiterName="",CostCenterName="",createdDate="";
	   int pax=0;
	   ArrayList<String>listKOT=new ArrayList<String>();
       try 
       {
    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
	       st = aposCon.createStatement();
           
	       // Get Setup Values for Printing.
	       
	       String areaWisePricing="N";
	       String printModQtyOnKOT="N";
	       String noOfLinesInKOTPrint="1";
	       String printShortNameOnKOT="N";
	       String printKOTYN="Y";
	       String multipleKOTPrint="N";
	       String multipleBillPrint="N";
	       String clientCode="";
	       String printKOTToLocalPrinter="N";
	       int columnSize=40;
	       
	       String sql="select strAreaWisePricing,strPrintShortNameOnKOT,strPrintModifierQtyOnKOT,strNoOfLinesInKOTPrint"
	       		+ ",strMultipleKOTPrintYN,strPrintKOTYN,strMultipleBillPrinting,intColumnSize,strClientCode,strKOTToLocalPrinter "
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
		       printKOTToLocalPrinter=rsSetupValues.getString(10);
	       }
	       
	       if(printKOTToLocalPrinter.equals("Y"))
	       {
	    	   obTextFileGenerator.funCreateTempFolder();
	           String filePath = System.getProperty("user.dir");
	           File textKOTFilePath = new File(filePath + "/Temp/Temp_Master_KOT.txt");
	           System.out.println(textKOTFilePath.getAbsolutePath());
	           FileWriter fstream = new FileWriter(textKOTFilePath);
	           //BufferedWriter KotOut = new BufferedWriter(fstream);
	           BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textKOTFilePath),"UTF8"));
	           if ("Reprint".equalsIgnoreCase(Reprint)) 
	           {
	        	   objFileIOUtil.funPrintBlankSpace("DUPLICATE", KotOut, columnSize);
	               KotOut.write("DUPLICATE");
	               KotOut.newLine();
	           }
	           
	           
	           sql = " select a.dteDateCreated,a.strKOTNo,c.strTableName,c.intPaxNo,a.strWaiterNo,ifnull(b.strWShortName,'') "
    	           	   + " from tblitemrtemp a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo,tbltablemaster c "
    	           	   + " where a.strTableNo=c.strTableNo "
    	           	   + " and a.strTableNo='"+tableNo+"' and c.strAreaCode ='"+areaCode+"' and a.strPOSCode='"+POSCode+"' and c.strOperational='Y' "
    	           	   + " group by a.strKOTNo "
    	           	   + " order by a.strKOTNo,a.strSerialNo";
	           
		           st.close();
		           st=aposCon.createStatement();
		           ResultSet rs = st.executeQuery(sql);
		           while (rs.next()) 
		           {
		               tableName = rs.getString(3);
		               pax = rs.getInt(4);
		               waiterName=rs.getString(6);
		               createdDate=rs.getString(1);
		               listKOT.add(rs.getString(2));
		           }
	               rs.close();
	           
	           
	           
	           objFileIOUtil.funPrintBlankSpace("KOT", KotOut, columnSize);
	           KotOut.write("KOT");
	           KotOut.newLine();
	           objFileIOUtil.funPrintBlankSpace(POSName, KotOut, columnSize);
	           KotOut.write(POSName);
	           KotOut.newLine();

	           System.out.println(pax);	           
	           KotOut.write(line);
	           KotOut.newLine();
	           KotOut.write("  TABLE NO :");
	           KotOut.write(tableName + "  ");
	           KotOut.write(" PAX   :");
	           System.out.println(pax);
	           KotOut.write(String.valueOf(pax));
	           System.out.println(pax);
	           KotOut.newLine();
	           KotOut.write("  WAITER NAME:" + "   " + waiterName);
               KotOut.newLine();
               KotOut.write("  DATE & TIME:" + createdDate);
	           
	           KotOut.newLine();
	           KotOut.write(line);
	           KotOut.newLine();
	           
	           
	           String itemName="c.strItemName";
	           if(printShortNameOnKOT.equals("Y"))
	           {
	               itemName="b.strShortName";
	           }
	          
	           if(listKOT.size()>0)
	           {
	        	   for(int cnt=0;cnt<listKOT.size();cnt++)
	        	   {
	        		   if(areaWisePricing.equals("Y"))
	    	           {
	    	               sql = " select LEFT(a.strItemCode,7),"+itemName+",a.dblItemQuantity,d.strCostCenterCode,d.strPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName,a.strNCKotYN  "
	    	               	   + " from tblitemrtemp a left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode  "
	    	               	   + " left outer join tblcostcentermaster d on c.strCostCenterCode=d.strCostCenterCode,tblitemmaster b  "
	    	               	   + " where a.strKOTNo='"+listKOT.get(cnt)+"' and a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"'  "
	    	               	   + " and a.strItemCode=b.strItemCode  and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ))  "
	    	               	   + " group by d.strCostCenterCode,a.strItemCode";
	    	           }
	    	           else
	    	           {
	    	        	   sql = " select LEFT(a.strItemCode,7),"+itemName+",a.dblItemQuantity,d.strCostCenterCode,d.strPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName,a.strNCKotYN  "
		    	               	   + " from tblitemrtemp a left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode  "
		    	               	   + " left outer join tblcostcentermaster d on c.strCostCenterCode=d.strCostCenterCode,tblitemmaster b  "
		    	               	   + " where a.strKOTNo='"+listKOT.get(cnt)+"' and a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"'  "
		    	               	   + " and a.strItemCode=b.strItemCode and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' )  OR c.strAreaCode ='"+areaCode+"')  "
		    	               	   + " group by d.strCostCenterCode,a.strItemCode";;
	    	           }
	    	           System.out.println(sql);
	    	           st.close();
	    	           st=aposCon.createStatement();
	    	           ResultSet rsKOTDetails = st.executeQuery(sql);
	    	           int count=0;
	    	           while (rsKOTDetails.next()) 
	    	           {
	    	        	   if(count==0)
	    	        	   {
	    	        		   KotOut.newLine();
	        	               KotOut.write("  KOT NO     :");
	        		           KotOut.write(listKOT.get(cnt) + "  ");
	        		           KotOut.newLine();
	        		           KotOut.write("  CostCenter Name     :");
	        		           KotOut.write(rsKOTDetails.getString(7) + "  ");
	        		           KotOut.newLine();
	        		           KotOut.write(line);
	        		           KotOut.newLine();
	        		           KotOut.write("  QTY         ITEM NAME  ");
	        		           KotOut.newLine();
	        		           KotOut.write(line);
	        		           KotOut.newLine();
	    	        	   }
	    	        	   
	    	        	   String itemqty = rsKOTDetails.getString(3);
	    	               if (itemqty.length() == 5) 
	    	               {
	    	                   KotOut.write(" " + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
	    	               }
	    	               else if (itemqty.length() == 4) 
	    	               {
	    	                   KotOut.write("  " + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
	    	               }
	    	               else if (itemqty.length() == 6) 
	    	               {
	    	                   KotOut.write("" + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
	    	               }
	    	               String itemCode=rsKOTDetails.getString(1);
	    	               String serialNo=rsKOTDetails.getString(5);
	    	               System.out.println("itemCode="+itemCode);
	    	               
	    	               String sqlModifier="select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
	    		                   + " where a.strItemCode like'"+rsKOTDetails.getString(1)+"M%' and a.strKOTNo='"+listKOT.get(cnt)+"' "
	    		                   //+ " and strSerialNo like'"+rsKOTDetails.getString(5)+".%' "
	    		                   + " group by a.strItemCode,a.strItemName ";
	    	               System.out.println("sqlModifier="+sqlModifier);
	    	               
	    		           Statement st2=aposCon.createStatement();
	    	               ResultSet rsModifierItems=st2.executeQuery(sqlModifier);
	    	               while(rsModifierItems.next())
	    	               {
	    	            	   KotOut.newLine();
	    	                   String modQty=rsModifierItems.getString(2);
	    	                   String modifierName=rsModifierItems.getString(1);
	    	                       if(printModQtyOnKOT.equals("Y"))
	    	                       {
	    	                           if (modQty.length() == 5) 
	    	                           {
	    	                               KotOut.write(" " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
	    	                           }
	    	                           else if (modQty.length() == 4) 
	    	                           {
	    	                               KotOut.write("  " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
	    	                           }
	    	                           else if (modQty.length() == 6) 
	    	                           {
	    	                               KotOut.write("" + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
	    	                           }
	    	                       }
	    	                       else
	    	                       {
	    	                           if (modQty.length() == 5) 
	    	                           {
	    	                               KotOut.write("            " + rsModifierItems.getString(1));
	    	                           }
	    	                           else if (modQty.length() == 4) 
	    	                           {
	    	                               KotOut.write("             " + rsModifierItems.getString(1));
	    	                           }
	    	                           else if (modQty.length() == 6) 
	    	                           {
	    	                               KotOut.write("           " + rsModifierItems.getString(1));
	    	                           }
	    	                       }
	    	               }
	    	               st2.close();
	    	               rsModifierItems.close();
	    	               KotOut.newLine();
	    	               count++;
	    	               
	    	           }
	    	           rsKOTDetails.close();
	    	           KotOut.newLine();
	    	           KotOut.write(line);
	        	   }
	           }
	           
	           
	           for(int cntLines=0;cntLines<Integer.parseInt(noOfLinesInKOTPrint);cntLines++)
	           {
	               KotOut.newLine();
	           }
	           
	           KotOut.write("m");//windows
	           KotOut.close();
	           fstream.flush();
	           fstream.close();
	           
	           if(printYN.equals("Y"))
	           {
	        	   sql = "select strBillPrinterPort,strAdvReceiptPrinterPort from tblposmaster where strPOSCode='" + POSCode + "'";
		       	   rs = st.executeQuery(sql);
		       	   if (rs.next())
		       	   {
		       		result=obTextFileGenerator.funPrintKOTTextFile(rs.getString(1), rs.getString(1), "kot", "", printKOTYN, multipleBillPrint,"MasterKOT",0,0,"N",Reprint);
		       	   }
		       	   rs.close();
	              
	           }
	    	   
	       }
           
           
       } 
       catch (Exception e) 
       {
           
           e.printStackTrace();
       }
       return result;
   }
   


   public String funGenerateConsolidatedKOTTextFileForDirectBiller(String AreaCode, String BillNo, String Reprint, String posCode,String printYN)
		 {
	           String result="";
			   clsDatabaseConnection objDb=new clsDatabaseConnection();
		       Connection aposCon=null;
		       Statement st=null,st2=null;
		       clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
		       String line = " ________________________________________",printKOTPrinter="",sql="";
			   
		       try 
		       {
		    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
			       st = aposCon.createStatement();
			       st2=aposCon.createStatement();
		           
			       // Get Setup Values for Printing.
			       
			      
			       String areaWisePricing="N";
			       String printModQtyOnKOT="N";
			       String noOfLinesInKOTPrint="1";
			       String printShortNameOnKOT="";
			       String printKOTYN="Y";
			       String multipleKOTPrint="N";
			       String multipleBillPrint="N";
			       String clientCode="";
			       int columnSize=40;
			    
			       sql="select strConsolidatedKOTPrinterPort from tblsetup where strPOSCode='"+posCode+"'";
				   System.out.println(sql);
				       ResultSet rsSetupValues=st.executeQuery(sql);
				       if(rsSetupValues.next())
				       {
				    	   printKOTPrinter=rsSetupValues.getString(1);
				       }
		          
				   if(!printKOTPrinter.isEmpty())
				   {
					   obTextFileGenerator.funCreateTempFolder();
			           String filePath = System.getProperty("user.dir");
			           File textKOTFilePath = new File(filePath + "/Temp/Temp_Consolidated_KOT.txt");
			           System.out.println(textKOTFilePath.getAbsolutePath());
			           FileWriter fstream = new FileWriter(textKOTFilePath);
			           //BufferedWriter KotOut = new BufferedWriter(fstream);
			           BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textKOTFilePath),"UTF8"));
			           
			           if ("Reprint".equalsIgnoreCase(Reprint)) {
			        	   objFileIOUtil.funPrintBlankSpace("DUPLICATE", KotOut, columnSize);
			               KotOut.write("DUPLICATE");
			               KotOut.newLine();
			           }
			           
			           
			           String sql_PrintHomeDelivery = "select strOperationType,strClientCode from tblbillhd where strBillNo='"+BillNo+"' ";
			           ResultSet rs_PrintHomeDelivery = st.executeQuery(sql_PrintHomeDelivery);
			           String operationType="";
			           
			           if (rs_PrintHomeDelivery.next())
			            {
			                operationType=rs_PrintHomeDelivery.getString(1);
			                clientCode=rs_PrintHomeDelivery.getString(2);
			            }
			            rs_PrintHomeDelivery.close();
			            if(operationType.equalsIgnoreCase("HomeDelivery"))
			            {
			            	objFileIOUtil.funPrintBlankSpace("Home Delivery", KotOut, columnSize);
			            	KotOut.write(operationType);
			                KotOut.newLine();
			            }
			            else if(operationType.equalsIgnoreCase("TakeAway"))
			            {
			            	objFileIOUtil.funPrintBlankSpace("Take Away", KotOut, columnSize);
			                KotOut.write(operationType);
			                KotOut.newLine();
			            }
			            if(clientCode.equals("047.001"))
			            {
			            	printShortNameOnKOT="N";
			            }
			            
			            objFileIOUtil.funPrintBlankSpace("Consolidated KOT", KotOut, columnSize);
				        KotOut.write("Consolidated KOT");
				        KotOut.newLine();
			           
				        String sql_PrintPOSName = "select strPosName from tblposmaster where strPosCode='"+posCode+"' ";
				        ResultSet rs_PrintPOSName = st.executeQuery(sql_PrintPOSName);
				         
				           if (rs_PrintPOSName.next())
				            {
				        	   objFileIOUtil.funPrintBlankSpace(rs_PrintPOSName.getString(1), KotOut, columnSize);
					           KotOut.write(rs_PrintPOSName.getString(1));
				            }
				           rs_PrintPOSName.close();
			          
				       
			           KotOut.newLine();
			          
			           
			           objFileIOUtil.funPrintBlankSpace("Direct Biller", KotOut,columnSize);
			           KotOut.write("Direct Biller");
			           KotOut.newLine();
			           KotOut.write(line);
			           KotOut.newLine();

			           
			           KotOut.write("  Bill No   :" +  BillNo + " ");
			        
			           String sql_DirectKOT_Date = "select date(dteBillDate), time(dteBillDate) from tblbilldtl where strBillNo='"+BillNo+"' ";
		                ResultSet rs_DirectKOT_Date = st.executeQuery(sql_DirectKOT_Date);
		               if (rs_DirectKOT_Date.next())
		               {
		                   KotOut.newLine();
		                   KotOut.write("  DATE & TIME: " + rs_DirectKOT_Date.getString(1) + " " + rs_DirectKOT_Date.getString(2));
		               }

		               rs_DirectKOT_Date.close();
		              
			           
			           KotOut.newLine();
			           KotOut.write(line);
			           KotOut.newLine();
			           KotOut.write("  QTY         ITEM NAME  ");
			           KotOut.newLine();
			           KotOut.write(line);
			           
			           String itemName="b.strItemName";
			           if(printShortNameOnKOT.equals("Y"))
			           {
			               itemName="d.strShortName";
			           }
			           
			           

		               String sql_DirectKOT_Items = "select a.strItemCode," + itemName + ",a.dblQuantity "
		                   + "from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
		                   + "where  a.strBillNo='"+BillNo+"' and  b.strCostCenterCode=c.strCostCenterCode "
		                   + "and a.strItemCode=d.strItemCode "
		                   + "and (b.strAreaCode='"+AreaCode+"' or b.strAreaCode='" + AreaCode + "') "
		                   + "and a.strItemCode=b.strItemCode group by a.strItemCode;";
		               
		              System.out.println("sql_DirectKOT_Items Query="+sql_DirectKOT_Items);
		               
		               ResultSet rs_DirectKOT_Items = st.executeQuery(sql_DirectKOT_Items);
		               while (rs_DirectKOT_Items.next())
		               {
		            	  // System.out.println("Items\n");
		            	  // System.out.println(CostCenterName + "\t" + rs_DirectKOT_Items.getString(2).toUpperCase());
		                   KotOut.newLine();
		                   KotOut.write("  " + rs_DirectKOT_Items.getString(3) + "      " + rs_DirectKOT_Items.getString(2).toUpperCase());
		                 
		                   
		                   String sql_Modifier=" select b.strModifierName ,b.dblQuantity,a.strDefaultModifier,b.strDefaultModifierDeselectedYN "
		                       +" from tblitemmodofier a,tblbillmodifierdtl b "
		                       +" where a.strItemCode=left(b.strItemCode,7) "
		                       +" and a.strModifierCode=b.strModifierCode "
		                       +" and b.strBillNo='"+BillNo+"' and left(b.strItemCode,7)='"+rs_DirectKOT_Items.getString(1)+"' ";
		                   
		                  System.out.println("Mod Items Query="+sql_Modifier);
		                   ResultSet rs_Modifier = st2.executeQuery(sql_Modifier);
		                   while (rs_Modifier.next())
		                   {
		                	   if(printShortNameOnKOT.equals("Y"))
		                	   {
		                		   if(rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
		                           {
		                               KotOut.newLine();
		                               KotOut.write("  " + rs_Modifier.getString(2) + "      " +"No "+ rs_Modifier.getString(1).toUpperCase());
		                           }
		                           else if(rs_Modifier.getString(3).equalsIgnoreCase("N"))
		                           {
		                               KotOut.newLine();
		                               KotOut.write("  " + rs_Modifier.getString(2) + "      " + rs_Modifier.getString(1).toUpperCase());
		                           } 
		                	   }
		                	   else
		                	   {
		                		   if(rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
		                           {
		                               KotOut.newLine();
		                               KotOut.write("       "+ rs_Modifier.getString(1).toUpperCase());
		                           }
		                           else if(rs_Modifier.getString(3).equalsIgnoreCase("N"))
		                           {
		                               KotOut.newLine();
		                               KotOut.write( "      "+ rs_Modifier.getString(1).toUpperCase());
		                           } 
		                		   
		                	   }
		                                             
		                   }
		                  
		                   rs_Modifier.close();
		                  
		               }

		               rs_DirectKOT_Items.close();
		               
		               
		               KotOut.newLine();
		               KotOut.write(line);
		               KotOut.newLine();
		               KotOut.newLine();
		               KotOut.newLine();
		               KotOut.newLine();
		               KotOut.newLine();
		               KotOut.newLine();
			           
			           
			           for(int cntLines=0;cntLines<Integer.parseInt(noOfLinesInKOTPrint);cntLines++)
			           {
			               KotOut.newLine();
			           }
			           
			           KotOut.write("m");//windows
			           KotOut.close();
			           fstream.flush();
			           fstream.close();
			        
			           if(printYN.equals("Y"))
			           {
			        	   result=obTextFileGenerator.funPrintKOTTextFile(printKOTPrinter,"", "kot",multipleKOTPrint, printKOTYN, multipleBillPrint,"ConsolidateKOT",0,0,"N",Reprint);
			           }
				   }
			       
		           
		       } catch (Exception e) {
		           
		           e.printStackTrace();
		       }
		       
		       return result;
			   
		   }

   
   
   public String funConsolidateKOTDetailsToTextFile(String tableNo,String POSCode,String POSName,String Reprint,String printYN,String kotType,String KOTNo,String deviceName) 
		   {       
	           String result="";
			   clsDatabaseConnection objDb=new clsDatabaseConnection();
		       Connection aposCon=null;
		       Statement st=null;
		       clsFileIOUtil objFileIOUtil=new clsFileIOUtil();
		       String line = "  --------------------------------------",tableName="",waiterName="",userName="",CostCenterName="",createdDate="";
			   int pax=0;
			   ArrayList<String>listKOT=new ArrayList<String>();
		       try 
		       {
		    	   aposCon=objDb.funOpenAPOSCon("mysql","master");
			       st = aposCon.createStatement();
		           
			       // Get Setup Values for Printing.
			       
			       String areaWisePricing="N";
			       String printModQtyOnKOT="N";
			       String noOfLinesInKOTPrint="1";
			       String printShortNameOnKOT="N";
			       String printKOTYN="Y";
			       String multipleKOTPrint="N";
			       String multipleBillPrint="N";
			       String clientCode="";
			       String printKOTPrinter="N";
			       int columnSize=40;
			       
			       String AreaCodeForAll = "";
		            String sql = "select strAreaCode from tblareamaster where strAreaName='All';";                   
		            ResultSet rsAreaCode = st.executeQuery(sql);
		            if (rsAreaCode.next()) {
		                AreaCodeForAll = rsAreaCode.getString(1);
		            }
		            rsAreaCode.close();
				   
			       
			       sql="select strAreaWisePricing,strPrintShortNameOnKOT,strPrintModifierQtyOnKOT,strNoOfLinesInKOTPrint"
				       		+ ",strMultipleKOTPrintYN,strPrintKOTYN,strMultipleBillPrinting,intColumnSize,strClientCode,strKOTToLocalPrinter,strConsolidatedKOTPrinterPort "
				       		+ "from tblsetup where (strPOSCode='"+POSCode+"' or strPOSCode='All' ) ";
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
					       printKOTPrinter=rsSetupValues.getString(11);
				       }
			       
			   
			       
			       if(!printKOTPrinter.isEmpty())
			       {
			    	   obTextFileGenerator.funCreateTempFolder();
			           String filePath = System.getProperty("user.dir");
			           File textKOTFilePath;
			           textKOTFilePath = new File(filePath + "/Temp/Temp_Consolidated_KOT.txt");
			           
			           System.out.println(textKOTFilePath.getAbsolutePath());
			           FileWriter fstream = new FileWriter(textKOTFilePath);
			           //BufferedWriter KotOut = new BufferedWriter(fstream);
			           BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textKOTFilePath),"UTF8"));
			           if ("Reprint".equalsIgnoreCase(Reprint)) 
			           {
			        	   objFileIOUtil.funPrintBlankSpace("DUPLICATE", KotOut, columnSize);
			               KotOut.write("DUPLICATE");
			               KotOut.newLine();
			           }
			           
			           
			           sql = " select a.dteDateCreated,a.strKOTNo,c.strTableName,c.intPaxNo,a.strWaiterNo,ifnull(b.strWShortName,''),a.strUserCreated "
		    	           	   + " from tblitemrtemp a left outer join tblwaitermaster b on a.strWaiterNo=b.strWaiterNo,tbltablemaster c "
		    	           	   + " where a.strTableNo=c.strTableNo "
		    	           	   + " and a.strTableNo='"+tableNo+"' and a.strKOTNo='"+KOTNo+"' "
		    	           	   + " and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) OR c.strAreaCode ='"+AreaCodeForAll+"')"
		    	           	   + " and a.strPOSCode='"+POSCode+"' and c.strOperational='Y' "
		    	           	   + " group by a.strKOTNo "
		    	           	   + " order by a.strKOTNo,a.strSerialNo";
			           
				           st.close();
				           st=aposCon.createStatement();
				           ResultSet rs = st.executeQuery(sql);
				           while (rs.next()) 
				           {
				               tableName = rs.getString(3);
				               pax = rs.getInt(4);
				               waiterName=rs.getString(6);
				               userName=rs.getString(7);
				               createdDate=rs.getString(1);
				               listKOT.add(rs.getString(2));
				           }
			               rs.close();
			           
			           objFileIOUtil.funPrintBlankSpace("Consolidated KOT", KotOut, columnSize);
				       KotOut.write("Consolidated KOT");
				       
			           KotOut.newLine();
			           objFileIOUtil.funPrintBlankSpace(POSName, KotOut, columnSize);
			           KotOut.write(POSName);
			           KotOut.newLine();

			           System.out.println(pax);	           
			           KotOut.write(line);
			           KotOut.newLine();
			           KotOut.write("  TABLE NO :");
			           KotOut.write(tableName + "  ");
			           KotOut.write(" PAX   :");
			           System.out.println(pax);
			           KotOut.write(String.valueOf(pax));
			           System.out.println(pax);
			           KotOut.newLine();
			           KotOut.write("  WAITER NAME:" + "   " + waiterName);
		               KotOut.newLine();
		               KotOut.write("  DATE & TIME:" + createdDate);
		               KotOut.newLine();
			           KotOut.write("  KOT From Device:" + deviceName);
			           KotOut.newLine();
			           KotOut.write("  KOT By User      :" + userName);
			            
			           KotOut.newLine();
			           KotOut.write(line);
			           KotOut.newLine();
			           
			           
			           String itemName="c.strItemName";
			           if(printShortNameOnKOT.equals("Y"))
			           {
			               itemName="b.strShortName";
			           }
			           
			          // if(listKOT.size()>0)
			          // {
			        	 //  for(int cnt=0;cnt<listKOT.size();cnt++)
			        	 //  {
			        		   if(areaWisePricing.equals("Y"))
			    	           {
			    	               sql = " select LEFT(a.strItemCode,7),"+itemName+",a.dblItemQuantity,d.strCostCenterCode,d.strPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName,a.strNCKotYN  "
			    	               	   + " from tblitemrtemp a left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode  "
			    	               	   + " left outer join tblcostcentermaster d on c.strCostCenterCode=d.strCostCenterCode,tblitemmaster b  "
			    	               	  // + " where a.strKOTNo='"+listKOT.get(cnt)+"' and a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"'  "
			    	               	 + " where  a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"' and a.strKOTNo='"+KOTNo+"' " 
			    	               	  + " and a.strItemCode=b.strItemCode and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ))  "
			    	               	   + " group by d.strCostCenterCode,a.strItemCode";
			    	           }
			    	           else
			    	           {
			    	        	   sql = " select LEFT(a.strItemCode,7),"+itemName+",a.dblItemQuantity,d.strCostCenterCode,d.strPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName,a.strNCKotYN  "
				    	               	   + " from tblitemrtemp a left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode  "
				    	               	   + " left outer join tblcostcentermaster d on c.strCostCenterCode=d.strCostCenterCode,tblitemmaster b  "
				    	               	  // + " where a.strKOTNo='"+listKOT.get(cnt)+"' and a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"'  "
				    	               	   + " where a.strTableNo='"+tableNo+"' and c.strPosCode='"+POSCode+"' and a.strKOTNo='"+KOTNo+"' "
				    	               	   + " and a.strItemCode=b.strItemCode and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' )  OR c.strAreaCode ='"+AreaCodeForAll+"')  "
				    	               	   + " group by d.strCostCenterCode,a.strItemCode";;
			    	           }
			    	           System.out.println(sql);
			    	           st.close();
			    	           st=aposCon.createStatement();
			    	           ResultSet rsKOTDetails = st.executeQuery(sql);
			    	           int count=0;
			    	           while (rsKOTDetails.next()) 
			    	           {
			    	        	   if(count==0)
			    	        	   {
			    	        		   KotOut.newLine();
			        		           KotOut.write("  QTY         ITEM NAME  ");
			        		           KotOut.newLine();
			        		           KotOut.write(line);
			        		           KotOut.newLine();
			    	        	   }
			    	        	   
			    	        	   String itemqty = rsKOTDetails.getString(3);
			    	               if (itemqty.length() == 5) 
			    	               {
			    	                   KotOut.write(" " + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
			    	               }
			    	               else if (itemqty.length() == 4) 
			    	               {
			    	                   KotOut.write("  " + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
			    	               }
			    	               else if (itemqty.length() == 6) 
			    	               {
			    	                   KotOut.write("" + rsKOTDetails.getString(3) + "       " + rsKOTDetails.getString(2));
			    	               }
			    	               String itemCode=rsKOTDetails.getString(1);
			    	               String serialNo=rsKOTDetails.getString(5);
			    	               System.out.println("itemCode="+itemCode);
			    	               
			    	               String sqlModifier="select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
			    		                    + " where a.strItemCode like'"+rsKOTDetails.getString(1)+"M%' and a.strKOTNo='"+KOTNo+"' "
			    		                   //+ " and strSerialNo like'"+rsKOTDetails.getString(5)+".%' "
			    		                   + " group by a.strItemCode,a.strItemName ";
			    	               System.out.println("sqlModifier="+sqlModifier);
			    	               
			    		           Statement st2=aposCon.createStatement();
			    	               ResultSet rsModifierItems=st2.executeQuery(sqlModifier);
			    	               while(rsModifierItems.next())
			    	               {
			    	            	   KotOut.newLine();
			    	                   String modQty=rsModifierItems.getString(2);
			    	                   String modifierName=rsModifierItems.getString(1);
			    	                       if(printModQtyOnKOT.equals("Y"))
			    	                       {
			    	                           if (modQty.length() == 5) 
			    	                           {
			    	                               KotOut.write(" " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 4) 
			    	                           {
			    	                               KotOut.write("  " + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 6) 
			    	                           {
			    	                               KotOut.write("" + rsModifierItems.getString(2) + "       " + rsModifierItems.getString(1));
			    	                           }
			    	                       }
			    	                       else
			    	                       {
			    	                           if (modQty.length() == 5) 
			    	                           {
			    	                               KotOut.write("            " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 4) 
			    	                           {
			    	                               KotOut.write("             " + rsModifierItems.getString(1));
			    	                           }
			    	                           else if (modQty.length() == 6) 
			    	                           {
			    	                               KotOut.write("           " + rsModifierItems.getString(1));
			    	                           }
			    	                       }
			    	               }
			    	               st2.close();
			    	               rsModifierItems.close();
			    	               KotOut.newLine();
			    	               count++;
			    	               
			    	           }
			    	           rsKOTDetails.close();
			    	           KotOut.newLine();
			    	           KotOut.write(line);
			        	//   }
			          // }
			           
			           
			           for(int cntLines=0;cntLines<Integer.parseInt(noOfLinesInKOTPrint);cntLines++)
			           {
			               KotOut.newLine();
			           }
			           
			           KotOut.write("m");//windows
			           KotOut.close();
			           fstream.flush();
			           fstream.close();
			           
			           if(printYN.equals("Y"))
			           {
			        	  result=obTextFileGenerator.funPrintKOTTextFile(printKOTPrinter, "", "kot", "",printKOTYN, "N","ConsolidateKOT",0,0,"N",Reprint);
			        	   
			           }
			    	   
			       }
		           
		           
		       } 
		       catch (Exception e) 
		       {
		           
		           e.printStackTrace();
		       }
		       return result;
		   }    
	    


}
