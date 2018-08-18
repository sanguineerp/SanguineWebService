package com.apos.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Time;
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
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JOptionPane;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;
import com.apos.service.clsSetupService;
import com.webservice.util.clsBillDtl;


@Repository("clsReprintDao")
@Transactional(value = "webPOSTransactionManager")
public class clsReprintDao
{
	@Autowired
	private clsSetupService objSetupService;
	
    @Autowired
    private SessionFactory webPOSSessionFactory;
    private String kotFor;
    private String KOTType, sql;
    private final String Line = "  --------------------------------------";
    private static Map<String, String> hmTakeAway;
    public static String gCounterWise, gCounterName;
    
    public JSONObject funExecute(String posCode,String operationType,String kotFor)
   	{
    	JSONObject jobjResult=new JSONObject();
    	JSONObject jobjOperation=new JSONObject();
    	JSONArray jArrData = new JSONArray();

    	List list = null;
    	JSONObject jObjData=new JSONObject();
   		
   	 
    	try
    	{
    	if (operationType.equalsIgnoreCase("KOT"))
        {
    		//kotFor="Dina";
            if (kotFor.equalsIgnoreCase("Dina"))
            {
            	String sql = "select a.strKOTNo,time(a.dteDateCreated)"
                        + ",IFNULL(c.strWShortName,'NA'),b.strTableName"
                        + ",a.intPaxNo,a.strUserEdited ,a.dblAmount "
                        + "from tblitemrtemp a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                        + "left outer join tblwaitermaster c  on a.strWaiterNo=c.strWaiterNo "
                        + "where a.strPOSCode='" + posCode + "' "
                        + "group by a.strKOTNo,a.strTableNo  "
                        + "order by a.strKOTNo desc";
       			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
       			
       			list = query.list();
    			
    			 if (list!=null)
    				{
    					for(int i=0; i<list.size(); i++)
    					{
    						Object[] obj = (Object[]) list.get(i);
    						
    						
    						JSONObject jObj1=new JSONObject();
    						jObj1.put("KOTNo",Array.get(obj, 0));
    						jObj1.put("Time",Array.get(obj, 1));
    						jObj1.put("WaiterName",Array.get(obj, 2));
    						jObj1.put("TableName",Array.get(obj, 3));
    						jObj1.put("PaxNo",Array.get(obj, 4));
    						jObj1.put("UserCreated",Array.get(obj, 5));
    						jObj1.put("Amount",Array.get(obj, 6));
    						jArrData.put(jObj1);
    						
    					}
    					jObjData.put("TblData", jArrData);
    					jObjData.put("strOperation",kotFor);
       				}
            }
            else if (kotFor.equalsIgnoreCase("DirectBiller"))
            {
            	String sql = "select a.strbillno ,time(a.dteBillDate) ,b.strPOSName "
                        + ",a.dblGrandTotal "
                        + " from tblbillhd a ,tblposmaster b "
                        + " where a.strPOSCode='" + posCode + "' and a.strPOSCode=b.strPOSCode "
                        + " and a.strTableNo='' Or a.strTableNo='TB0000'  "
                        + " order by a.strbillno DESC";
       			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
       			
       			list = query.list();
    			
    			 if (list!=null)
    				{
    					for(int i=0; i<list.size(); i++)
    					{
    						Object[] obj = (Object[]) list.get(i);
    						
    						
    						JSONObject jObj1=new JSONObject();
    						jObj1.put("BillNo",Array.get(obj, 0));
    						jObj1.put("Time",Array.get(obj, 1));
    						jObj1.put("POS",Array.get(obj, 2));
    						jObj1.put("TotalAmount",Array.get(obj, 3));
    						jArrData.put(jObj1);
    						
    					}
    					jObjData.put("TblData", jArrData);
    					jObjData.put("strOperation",kotFor);
    				}
            }
        }
    	else if (operationType.equalsIgnoreCase("Bill"))
        {
    		String sql = "select a.strbillno ,ifnull(b.strTableName,'ND'),time(a.dteBillDate) "
                    + ",a.strPOSCode ,a.dblGrandTotal  "
                    + "from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                    + "where a.strPOSCode='" + posCode + "' "
                    + "order by a.strbillno DESC";
   			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
   			
   			list = query.list();
			 
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj = (Object[]) list.get(i);
						
						
						JSONObject jObj1=new JSONObject();
						jObj1.put("BillNo",Array.get(obj, 0));
						jObj1.put("TableName",Array.get(obj, 1));
						jObj1.put("Time",Array.get(obj, 2));
						jObj1.put("TotalAmount",Array.get(obj, 3));
						jArrData.put(jObj1);
						
					}
					jObjData.put("TblData", jArrData);
					jObjData.put("strOperation",operationType);
				}

        }
//    	jobjOperation.put("TblData", jArrData);
//    	jobjOperation.put("strOperation",operationType);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    return jObjData;
    	}
   	}
    
    public void funViewButtonPressed(String code,String transactionType,String kotFor,String posCode,String clientCode,String posName,String webStockUserCode)
   	{
    	
    	 List list = null;
    	 String TableNo =null;
    	 if (transactionType.equalsIgnoreCase("KOT"))
         {
             if (kotFor.equalsIgnoreCase("Dina"))
             {
            	 try
                 {
                     String sql = "select strTableNo from tblitemrtemp "
                             + "where strKOTNo='" + code + "' "
                             + "group by strKOTNo ;";
                     Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            			
            			list = query.list();
         			 
         			 if (list!=null)
         				{
         					for(int i=0; i<list.size(); i++)
         					{
         						
         						TableNo =(String) list.get(i);
                         
         					}
         				}
         			JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gPrintType");
     			    
     			    if(objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Text File"))
     			    {
                         funRemotePrintUsingTextFile(TableNo, code.trim(), "", "Reprint", "Dina", "N",posCode,clientCode,posName,webStockUserCode);
                     }
                     else
                     {
                        funRemotePrintUsingTextFile(TableNo, code.trim(), "", "Reprint", "Dina", "N",posCode,clientCode,posName,webStockUserCode);
                     }
                 }
            	 catch (Exception e)
                 {
                     e.printStackTrace();
                 }
            	 code = "";
             }
             else if (kotFor.equalsIgnoreCase("DirectBiller"))
             {
                
//            	 	if(objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Text File"))
//            	 	{
//                         // funTextFilePrintingBill(selectedBill.trim(),clsGlobalVarClass.getPOSDateForTransaction());                          
//                       //  objUtility.funPrintBill(code.trim(), clsGlobalVarClass.getPOSDateForTransaction(), true, posCode);
//                     }
//                     else
//                     {
//                         objUtility.funPrintBill(selectedBill.trim(), clsGlobalVarClass.getPOSDateForTransaction(), true, posCode);
//                     }
            	 code = "";
              }
             else if (transactionType.equalsIgnoreCase("Bill"))
             {
                
//                     if (clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File"))
//                     {
//                         funTextFilePreviewBill(docNo);
//                     }
//                     else
//                     {
//                         funTextFilePreviewBill(docNo);
//                     }
            	 code = "";
             }
             else 
             {
                 //clsGlobalVarClass.gDayEndReportForm = "ReprintDayEndReport";
                 try
                 {
//                	 JCalendar dteForDayEnd;
//                	 Date dt1 = dteForDayEnd.getDate();
//                     int d = dt1.getDate();
//                     int m = dt1.getMonth() + 1;
//                     int y = dt1.getYear() + 1900;
//                     String dayEndDate = y + "-" + m + "-" + d;
//
//                     String dayEndDate = (dteForDayEnd.getDate().getYear() + 1900) + "-" + (dteForDayEnd.getDate().getMonth() + 1)
//                             + "-" + (dteForDayEnd.getDate().getDate());
//
//                     if ("Text File".equalsIgnoreCase(clsGlobalVarClass.gPrintType))
//                     {
//                         clsTextFileGeneratorForPrinting obj = new clsTextFileGeneratorForPrinting();
//                         obj.funGenerateTextDayEndReportPreview(selectedPOSCode, dayEndDate, "reprint");
//                     }
//                     else
//                     {
//                         clsTextFileGeneratorForPrinting obj = new clsTextFileGeneratorForPrinting();
//                         obj.funGenerateTextDayEndReportPreview(selectedPOSCode, dayEndDate, "reprint");
//                     }
                 }
                 catch (Exception e)
                 {
                     e.printStackTrace();
                 }
             }
             
         }
    	 
}
    
    public void funRemotePrintUsingTextFile(String tableNo, String KOTNo, String billNo, String reprint, String type, String printYN,String POSCode,String clientCode,String posName,String webStockUserCode)
    {
        try
        {
            String sql = "";
            String p2="",p3="",p4="",p5="",p6="",p7="";
            String c2="",c3="",c4="",c5="",c6="";
            switch (type)
            {
                case "Dina":

                    String areaCodeForAll = "";
                    String sql_AreaCode = "select strAreaCode from tblareamaster where strAreaName='All';";
                    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_AreaCode);
           			
           			List rsAreaCode = query.list();
                   
           		 if (rsAreaCode!=null)
 				{
 					for(int i=0; i<rsAreaCode.size(); i++)
 					{
                    
                        areaCodeForAll =(String) rsAreaCode.get(i);
                    }
 				}
                    sql = "select a.strItemName,a.strNCKotYN,d.strCostCenterCode,d.strPrimaryPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName "
                            + " ,ifnull(e.strLabelOnKOT,'KOT') strLabelOnKOT "
                            + " from tblitemrtemp a "
                            + " left outer join tblmenuitempricingdtl c on a.strItemCode = c.strItemCode "
                            + " left outer join tblprintersetup d on c.strCostCenterCode=d.strCostCenterCode "
                            + " left outer join tblcostcentermaster e on c.strCostCenterCode=e.strCostCenterCode  "
                            + " where a.strKOTNo='"+KOTNo+"' and a.strTableNo='"+tableNo+"' and (c.strPosCode='"+POSCode+"' or c.strPosCode='All') "
                            + " and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) "
                            + " OR c.strAreaCode ='"+areaCodeForAll+"') "
                            + " group by d.strCostCenterCode";
                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
              
                  List list1 =  query.list();
                  if (list1!=null)
   					{
                	  for(int i=0; i<list1.size(); i++)
   						{
                		  Object[] obj = (Object[]) list1.get(i);
  						
  						
                	  	p3 = (String) Array.get(obj, 2);
                	  	p2 = (String) Array.get(obj, 1);
                	  	p4 = (String) Array.get(obj, 3);
                	  	p5 = (String) Array.get(obj, 4);
                	  	p6 = (String) Array.get(obj, 5);
                	  	p7 = (String) Array.get(obj, 6);
   						}	
   				 	
                	  	
                	  	JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintType");
         			    
         			    if(objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Jasper"))
                         {
         			    	funGenerateJasperForTableWiseKOT("Dina", tableNo, p3, "", areaCodeForAll, KOTNo, reprint, p4, p5, p6, printYN, p2, p7,posName,POSCode,clientCode,webStockUserCode);
                         }
                        else
                        {
                            //funGenerateTextFileForTableWiseKOT(tableNo,p3, areaCodeForAll, KOTNo, reprint, p4, p5, p6, printYN, p2, p7,posName,POSCode,clientCode,webStockUserCode);
                        }
                    }
                   
                    break;

                case "DirectBiller":

                    sql = "select a.strItemName,c.strCostCenterCode,c.strPrimaryPrinterPort "
                            + ",c.strSecondaryPrinterPort,c.strCostCenterName,d.strLabelOnKOT "
                            + " from tblbilldtl  a,tblmenuitempricingdtl b,tblprintersetup c,tblcostcentermaster d   "
                            + " where a.strBillNo='"+billNo+"' "
                            + " and  a.strItemCode=b.strItemCode "
                            + " and b.strCostCenterCode=c.strCostCenterCode "
                            + " and b.strCostCenterCode=d.strCostCenterCode "
                            + " and (b.strPosCode='"+POSCode+"' or b.strPosCode='All') "
                            + " group by c.strCostCenterCode;";
                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                   
                  List rsPrintDirect =query.list();
                  if (rsPrintDirect!=null)
 					{
                	  for(int i=0; i<rsPrintDirect.size(); i++)
 						{
              		  Object[] obj = (Object[]) rsPrintDirect.get(i);
						
                		 c2 = (String) Array.get(obj,2);
                	  	 c3 = (String) Array.get(obj,3);
                	  	c4 = (String) Array.get(obj,4);
                	  	 c5 = (String) Array.get(obj,5);
                	  	 c6 = (String) Array.get(obj,6);
 						} 	
                	  	JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gDirectAreaCode");
         			    
         			   String directAreaCode = objSetupParameter.get("gDirectAreaCode").toString();
                        //funGenerateTextFileForKOTDirectBiller(rsPrintDirect.getString(2), "", clsGlobalVarClass.gDirectAreaCode, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5));
                	  	objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintType");
         			    
         			    if(objSetupParameter.get("gPrintType").toString().equalsIgnoreCase("Jasper"))
                        {
                          
                        }
                        else
                        {
                            //funGenerateTextFileForKOTDirectBiller(c2, directAreaCode, billNo, reprint, c3, c4, c5, c6,posName,POSCode,clientCode,webStockUserCode);
                        }
                    }
                    
                    break;
            }
        }
            catch (Exception ex)
    		{
    			ex.printStackTrace();
    		}
    }
    private void funCreateTempFolder()
    {
        try
        {
            String filePath = System.getProperty("user.dir");
            File Text_KOT = new File(filePath + "/Temp");
            if (!Text_KOT.exists())
            {
                Text_KOT.mkdirs();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void funPrintBlankSpace(String printWord, BufferedWriter BWOut,String POSCode,String clientCode)
    {
        try
        {
            int wordSize = printWord.length();
            JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gColumnSize");
			    
		int directAreaCode = Integer.parseInt(objSetupParameter.get("gColumnSize").toString());
            int actualPrintingSize = directAreaCode;
            int availableBlankSpace = actualPrintingSize - wordSize;

            int leftSideSpace = availableBlankSpace / 2;
            if (leftSideSpace > 0)
            {
                for (int i = 0; i < leftSideSpace; i++)
                {
                    BWOut.write(" ");
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void funGenerateJasperForTableWiseKOT(String billingType, String tableNo, String CostCenterCode, String ShowKOT, String AreaCode, String KOTNO, String Reprint, String primaryPrinterName, String secondaryPrinterName, String CostCenterName, String printYN, String NCKotYN, String labelOnKOT,String posName,String POSCode,String clientCode,String webStockUserCode)
    {
        HashMap hm = new HashMap();
        String sql="";
        List<List<clsBillDtl>> listData = new ArrayList<>();
        try
        {
            
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
            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(SQL_KOT_Dina_tableName);
           List rs_Dina_Table = query.list();
           
            if(rs_Dina_Table!=null)
            {
            	for(int i=0; i<rs_Dina_Table.size(); i++)
					{
      		  Object[] obj = (Object[]) rs_Dina_Table.get(i);
				
        		
                tableName = (String) Array.get(obj,0);
                pax = (int) Array.get(obj,1);
                }
            }
            String itemName = "b.strItemName";
            JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintShortNameOnKOT");
		    String pringShortNameOnKOT = (String) objSetupParameter.get("gPrintShortNameOnKOT");
		    if ("gPrintShortNameOnKOT".equalsIgnoreCase(pringShortNameOnKOT))
            {
                itemName = "d.strShortName";
            }
            String sqlKOTItems = "";
            List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gAreaWisePricing");
		    
           
            if (objSetupParameter.get("gAreaWisePricing").equals("Y"))
            {
                sqlKOTItems = "select LEFT(a.strItemCode,7)," + itemName + ",a.dblItemQuantity,a.strKOTNo,a.strSerialNo "
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
                sqlKOTItems = "select LEFT(a.strItemCode,7)," + itemName + ",a.dblItemQuantity,a.strKOTNo,a.strSerialNo "
                        + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
                        + " where a.strTableNo='"+tableNo+"' and a.strKOTNo='"+KOTNO+"' and b.strCostCenterCode=c.strCostCenterCode "
                        + " and b.strCostCenterCode='"+CostCenterCode+"' and a.strItemCode=d.strItemCode "
                        + " and (b.strPOSCode='"+POSCode+"' or b.strPOSCode='All') "
                        + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) "
                        + " OR b.strAreaCode ='" + AreaCode + "') "
                        + " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
                        + " order by a.strSerialNo ";
            }
         Query query1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlKOTItems);
            KOTType = "DINE";
          
            hmTakeAway = new HashMap<String, String>();
            sql = "select strTableNo from tblitemrtemp where strTakeAwayYesNo='Yes'";
          query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
          List rsTakeAway = query.list();
          if(rsTakeAway!=null)
          { 	
              hmTakeAway.put((String) rsTakeAway.get(0), "Yes");
         }
          if (null != hmTakeAway.get(tableNo))
           {
                KOTType = "Take Away";
            }
          	String gCounterWise = "No";
            hm.put("KOTType", KOTType);
            if (gCounterWise.equals("Yes"))
            {
                hm.put("CounterName", gCounterName);
            }
            hm.put("KOT", KOTNO);
            hm.put("tableNo", tableName);
            if (clientCode.equals("124.001"))
            {
                hm.put("124.001", tableName);
            }
            hm.put("PAX", String.valueOf(pax));

            String sqlWaiterDtl = "select strWaiterNo from tblitemrtemp where strKOTNo='"+KOTNO+"'  and strTableNo='"+tableNo+"' group by strKOTNo ;";
            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlWaiterDtl);
           List rsWaiterDtl =query.list();
           String waiterNo="";
            if (rsWaiterDtl!=null)
            {
            	for(int i=0; i<rsWaiterDtl.size(); i++)
				{
            		waiterNo =(String) rsWaiterDtl.get(i);
			
   				}
                if (!"null".equalsIgnoreCase(waiterNo) && waiterNo.trim().length() > 0)
                {
                    sqlWaiterDtl = "select strWShortName from tblwaitermaster where strWaiterNo='"+waiterNo+"' ;";
                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlWaiterDtl);
                    
                   List rs = query.list();
                 
                    hm.put("waiterName",waiterNo);
                   
                }
            }
            
            String sql_KOTDate = "select date(dteDateCreated),time(dteDateCreated) from tblitemrtemp where strKOTNo='"+KOTNO+"'  and strTableNo='"+tableNo+"' group by strKOTNo ;";
            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_KOTDate);
            List rs_KOTDate = query.list();
            Date dt = null;
            Time ti = null;
            if(rs_KOTDate!=null)
            {
            	for(int i=0; i<rs_KOTDate.size(); i++)
				{
            		Object[] obj = (Object[]) rs_KOTDate.get(i);
			
    		
            		dt = (Date) Array.get(obj,0);
            		ti=(Time)Array.get(obj,1);
            	}
            hm.put("DATE_TIME", dt + " " + ti);
            }
            InetAddress ipAddress = InetAddress.getLocalHost();
            String hostName = ipAddress.getHostName();
            hm.put("KOT From", hostName);
            
            BigDecimal itemQty=null;
            String itemNam="",serialNo="",itemCode="";
            String modifierName="";
            List rs_KOT_Items = query1.list();
           if (rs_KOT_Items!=null)
            {
        	   for(int i=0; i<rs_KOT_Items.size(); i++)
				{
           		Object[] obj = (Object[]) rs_KOT_Items.get(i);
			
   		
           		itemQty = (BigDecimal) Array.get(obj,2);
        	   itemNam=(String)Array.get(obj,1);
        	   serialNo=(String)Array.get(obj,4);
        	   itemCode=(String)Array.get(obj, 0);
				}
        	   double d1=itemQty.doubleValue();
                clsBillDtl objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(d1);
                objBillDtl.setStrItemName(itemNam);
                listOfKOTDetail.add(objBillDtl);
                String sql_Modifier = "select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
                        + " where a.strItemCode like'" + itemCode + "M%' and a.strKOTNo='" + KOTNO + "' "
                        + " and strSerialNo like'" + serialNo + ".%' "
                        + " group by a.strItemCode,a.strItemName ";
                //System.out.println(sql_Modifier);
                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Modifier);
               List rsModifierItems = query.list();
               if (rsModifierItems!=null)
                {
            	   for(int i=0; i<rsModifierItems.size(); i++)
   				{
              		Object[] obj = (Object[]) rsModifierItems.get(i);
   			
      		
              		modifierName = (String) Array.get(obj,0);
              		itemQty = (BigDecimal) Array.get(obj,1);
                }
            	   d1=itemQty.doubleValue();
                    objBillDtl = new clsBillDtl();
                   
                    if (modifierName.startsWith("-->"))
                    {
                    	
                    	objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintModQtyOnKOT");
            		    
                       
                      
                      if ((boolean)objSetupParameter.get("gPrintModQtyOnKOT"))
                       {
                            objBillDtl.setDblQuantity(d1);
                            objBillDtl.setStrItemName(modifierName);
                        }
                        else
                        {
                            objBillDtl.setDblQuantity(0);
                            objBillDtl.setStrItemName(modifierName);
                        }
                    }
                    listOfKOTDetail.add(objBillDtl);
                }
            }
         objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gNoOfLinesInKOTPrint");
	    String noOfLinesOnKOTPrint = (String) objSetupParameter.get("gNoOfLinesInKOTPrint");
         
		    String gPrintOS = "windows";
		    String gPrinterType="Inbuild";
            for (int cntLines = 0; cntLines < Integer.parseInt(noOfLinesOnKOTPrint); cntLines++)
            {
                clsBillDtl objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(0);
                objBillDtl.setStrItemName("");
                listOfKOTDetail.add(objBillDtl);
            }

            hm.put("listOfItemDtl", listOfKOTDetail);
            listData.add(listOfKOTDetail);
           
            
            //--- Set print properties
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.ISO_A4);
            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gMultipleKOTPrint");
		    String multipleKOTPrint = (String) objSetupParameter.get("gMultipleKOTPrint");
           
            if ("gMultipleKOTPrint".equalsIgnoreCase(multipleKOTPrint))
            {
                printRequestAttributeSet.add(new Copies(2));
            }

            //----------------------------------------------------     
            //printRequestAttributeSet.add(new Destination(new java.net.URI("file:d:/output/report.ps")));
            //----------------------------------------------------     
            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();

          
         sql="select a.strPrinterPort,a.strSecondaryPrinterPort,a.strPrintOnBothPrinters from tblcostcentermaster  a where a.strCostCenterCode='" + CostCenterCode + "' ";
         query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           List rsPrinter=query.list();
         String kotPrinterName = "";
            if (rsPrinter!=null)
            {
            	 for(int i=0; i<rsPrinter.size(); i++)
    				{
               		Object[] obj = (Object[]) rsPrinter.get(i);
    			
       		
               		kotPrinterName = (String) Array.get(obj,0);
            
    				}
            }
           

            kotPrinterName = kotPrinterName.replaceAll("#", "\\\\");
            printServiceAttributeSet.add(new PrinterName(kotPrinterName, null));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
//    public void funGenerateTextFileForTableWiseKOT(String tableNo, String costCenterCode, String areaCode, String KOTNO, String Reprint, String primaryPrinterName, String secondaryPrinterName, String costCenterName, String printYN, String NCKotYN, String labelOnKOT,String posName,String POSCode,String clientCode,String webStockUserCode)
//    {
//    	 String sql = "";
//    	
//		int p3=0;
//		String p2="",p4="";
//		Time p6 = null;
//		Date p5 = null;
//    	 String gCounterWise = "No";
//    
//    	 BigDecimal itemqty = null;
//		String kotItemName="",item1="",item5="";
//    	 
//        try
//        {
//           
//            funCreateTempFolder();
//            String filePath = System.getProperty("user.dir");
//            File fileKOTPrint = new File(filePath + "/Temp/Temp_KOT.txt");
//            FileWriter fstream = new FileWriter(fileKOTPrint);
//            BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint), "UTF8"));
//
//            boolean isReprint = false;
//            if ("Reprint".equalsIgnoreCase(Reprint))
//            {
//                isReprint = true;
//                funPrintBlankSpace("[DUPLICATE]", KotOut,POSCode,clientCode);
//                KotOut.write("[DUPLICATE]");
//                KotOut.newLine();
//            }
//
//            if ("Y".equalsIgnoreCase(NCKotYN))
//            {
//                funPrintBlankSpace("NCKOT", KotOut,POSCode,clientCode);
//                KotOut.write("NCKOT");
//                KotOut.newLine();
//            }
//            else
//            {
//                funPrintBlankSpace(labelOnKOT, KotOut,POSCode,clientCode);//write KOT
//                KotOut.write(labelOnKOT);//write KOT
//                KotOut.newLine();
//            }
//            funPrintBlankSpace(posName, KotOut,POSCode,clientCode);
//            KotOut.write(posName);
//            KotOut.newLine();
//            funPrintBlankSpace(costCenterName, KotOut,POSCode,clientCode);
//            KotOut.write(costCenterName);
//            KotOut.newLine();
//
//            String itemName = "b.strItemName";
//            JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintShortNameOnKOT");
//		    String pringShortNameOnKOT = (String) objSetupParameter.get("gPrintShortNameOnKOT");
//    
//            if ("gPrintShortNameOnKOT".equalsIgnoreCase(pringShortNameOnKOT))
//            {
//                itemName = "d.strShortName";
//            }
//
//            KOTType = "DINE";
//           
//            hmTakeAway = new HashMap<String, String>();
//            sql = "select strTableNo from tblitemrtemp where strTakeAwayYesNo='Yes'";
//            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
//            List rsTakeAway = query.list();
//            if(rsTakeAway!=null)
//            { 	
//                hmTakeAway.put((String) rsTakeAway.get(0), "Yes");
//            }
//            
//            if (null != hmTakeAway.get(tableNo))
//            {
//                KOTType = "Take Away";
//            }
//            funPrintBlankSpace(KOTType, KotOut,POSCode,clientCode);
//            KotOut.write(KOTType);
//            KotOut.newLine();
//
//            if (gCounterWise.equals("Yes"))
//            {
//                funPrintBlankSpace(gCounterName, KotOut,POSCode,clientCode);
//                KotOut.write(gCounterName);
//                KotOut.newLine();
//            }
//
//            KotOut.write(Line);
//            KotOut.newLine();
//            KotOut.write("  KOT NO     :");
//            KotOut.write(KOTNO + "  ");
//            KotOut.newLine();
//
//            String sqlKOTDtl = "select a.strWaiterNo,b.strTableName,b.intPaxNo,ifnull(c.strWShortName,''),date(a.dteDateCreated),time(a.dteDateCreated) "
//                    + " from tblitemrtemp a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
//                    + " and b.strOperational='Y' "
//                    + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo "
//                    + " where a.strKOTNo='"+KOTNO+"' and a.strTableNo='"+tableNo+"' group by a.strKOTNo ;";
//            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlKOTDtl);
//            
//           
//            List rsKOTDetails = query.list();
//            if (rsKOTDetails!=null)
//            {
//                if (clientCode.equalsIgnoreCase("136.001"))//KINKI
//                {
//                    KotOut.write("  TABLE No   :");
//                }
//                else
//                {
//                    KotOut.write("  TABLE NAME :");
//                }
//                for(int i=0; i<rsKOTDetails.size(); i++)
//					{
//        		  Object[] obj = (Object[]) rsKOTDetails.get(i);
//				
//                p2=(String) Array.get(obj, 1);
//                p3=(int) Array.get(obj, 2);
//                p4=(String) Array.get(obj,3);
//                p5=(Date) Array.get(obj,4);
//                p6=(Time) Array.get(obj,5);
//					}
//                KotOut.write(p2 + "  ");
//                KotOut.write(" PAX   :");
//                KotOut.write(p3);
//                KotOut.newLine();
//               
//                if (!p4.isEmpty())
//                {
//                    KotOut.write("  WAITER NAME:" + "   " + p4);
//                    KotOut.newLine();
//                }
//                KotOut.write("  DATE & TIME:" + p5 + " " + p6);
//            }
//           
//
//            KotOut.newLine();
//            if ("Y".equalsIgnoreCase(NCKotYN))
//            {
//               sql = "select a.strRemark from tblnonchargablekot a where a.strKOTNo='" + KOTNO + "' "
//                        + "group by a.strKOTNo ";
//                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
//               List rsRemark = query.list();
//               if(rsRemark!=null)
//               {
//            	String remark=(String) rsRemark.get(0);   
//               
//                if (remark.trim().length() > 0)
//                {
//                    KotOut.write("  Remark     :" + remark);
//                }
//            }
//            } 
//            InetAddress ipAddress = InetAddress.getLocalHost();
//            String hostName = ipAddress.getHostName();
//            KotOut.newLine();
//            KotOut.write("  KOT From Computer:" + hostName);
//            KotOut.newLine();
//            KotOut.write("  KOT By User      :" + webStockUserCode);
//            KotOut.newLine();
//
//            KotOut.write(Line);
//            KotOut.newLine();
//            KotOut.write("  QTY         ITEM NAME  ");
//            KotOut.newLine();
//            KotOut.write(Line);
//
//            // Code to Print KOT Item details    
//            String sqlKOTItems = "";
//
//            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gAreaWisePricing");
//		    
//            
//            if (objSetupParameter.get("gAreaWisePricing").equals("Y"))
//            {
//                sqlKOTItems = "select LEFT(a.strItemCode,7)," + itemName + ",a.dblItemQuantity,a.strKOTNo,a.strSerialNo "
//                        + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
//                        + " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
//                        + " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
//                        + " and (b.strPOSCode=? or b.strPOSCode='All') "
//                        + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? )) "
//                        + " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
//                        + " order by a.strSerialNo ";
//            }
//            else
//            {
//                sqlKOTItems = "select LEFT(a.strItemCode,7)," + itemName + ",a.dblItemQuantity,a.strKOTNo,a.strSerialNo "
//                        + " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
//                        + " where a.strTableNo='"+tableNo+"' and a.strKOTNo='"+KOTNO+"' and b.strCostCenterCode=c.strCostCenterCode "
//                        + " and b.strCostCenterCode='"+costCenterCode+"' and a.strItemCode=d.strItemCode "
//                        + " and (b.strPOSCode='"+POSCode+"' or b.strPOSCode='All') "
//                        + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo='"+tableNo+"' ) "
//                        + " OR b.strAreaCode ='" + areaCode + "') "
//                        + " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
//                        + " order by a.strSerialNo ";
//            }
//            //System.out.println(sqlKOTItems);
//          Query  pstKOTItems = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlKOTItems);
//           
//           
//        
//            List rsKOTItems = pstKOTItems.list();
//            if(rsKOTItems!=null)
//            {
//                KotOut.newLine();
//                for(int i=0; i<rsKOTItems.size(); i++)
//				{
//    		  Object[] obj = (Object[]) rsKOTItems.get(i);
//			
//            
//                 itemqty =(BigDecimal) Array.get(obj,2);
//                 kotItemName=(String) Array.get(obj,3);
//                 item1=(String) Array.get(obj,0);
//                 item5=(String) Array.get(obj,4);
//				}  
//                int i1=itemqty.intValue();
//                if (Integer.toString(i1).length() == 6)
//                {
//                    KotOut.write("" +itemqty+ "       ");
//                    if(kotItemName.length()<=25)
//                    {
//                        KotOut.write(kotItemName);
//                    }
//                    else
//                    {
//                        KotOut.write(kotItemName.substring(0, 25));
//                        KotOut.newLine();
//                        KotOut.write("             "+kotItemName.substring(25,kotItemName.length()));
//                    }
//                }
//                else if (Integer.toString(i1).length() == 5)
//                {
//                    KotOut.write(" " +itemqty + "       ");
//                    if(kotItemName.length()<=25)
//                    {
//                        KotOut.write(kotItemName);
//                    }
//                    else
//                    {
//                        KotOut.write(kotItemName.substring(0, 25));
//                        KotOut.newLine();
//                        KotOut.write("             "+kotItemName.substring(25,kotItemName.length()));
//                    }
//                }
//                else if (Integer.toString(i1).length() == 4)
//                {
//                    KotOut.write("  " +itemqty+ "       ");
//                    if(kotItemName.length()<=25)
//                    {
//                        KotOut.write(kotItemName);
//                    }
//                    else
//                    {
//                        KotOut.write(kotItemName.substring(0, 25));
//                        KotOut.newLine();
//                        KotOut.write("             "+kotItemName.substring(25,kotItemName.length()));
//                    }
//                }
//                 
//
////                String sqlModifier = "select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
////                        + " where a.strItemCode like'" +item1 + "M%' and a.strKOTNo='" + KOTNO + "' "
////                        + " and strSerialNo like'" + item5 + ".%' "
////                        + " group by a.strItemCode,a.strItemName ";
//                String sqlModifier = "select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
//                        + " where a.strKOTNo='" + KOTNO + "' "
//                       
//                        + " group by a.strItemCode,a.strItemName ";
//                Query  rsModifierItems = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifier);
//                List list2 = rsModifierItems.list();
//                
//                BigDecimal modQty=null;
//                String modifierName="";
//                if(list2!=null)
//                {
//                    
//                	for(int i=0; i<list2.size(); i++)
//    				{
//        		  Object[] obj = (Object[]) list2.get(i);
//    			
//                	 modQty = (BigDecimal)Array.get(obj,1);
//                     modifierName = (String)Array.get(obj,0);
//                    
//    				} 
//                	 int i2=modQty.intValue();
//                    if (modifierName.startsWith("-->"))
//                    {
//                        KotOut.newLine();
//                        
//                        objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintModQtyOnKOT");
//            		    
//                        
//                       
//                        if ((boolean)objSetupParameter.get("gPrintModQtyOnKOT"))
//                        {
//                            if (Integer.toString(i2).length() == 5)
//                            {
//                                KotOut.write(" " + modQty + "       " + modifierName);
//                            }
//                            else if (Integer.toString(i2).length() == 4)
//                            {
//                                KotOut.write("  " + modQty + "       " + modifierName);
//                            }
//                            else if (Integer.toString(i2).length() == 6)
//                            {
//                                KotOut.write("" + modQty + "       " + modifierName);
//                            }
//                        }
//                        else
//                        {
//                            if (Integer.toString(i2).length() == 5)
//                            {
//                                KotOut.write("               " + modifierName);
//                            }
//                            else if (Integer.toString(i2).length() == 4)
//                            {
//                                KotOut.write("                " + modifierName);
//                            }
//                            else if (Integer.toString(i2).length() == 6)
//                            {
//                                KotOut.write("              " + modifierName);
//                            }
//                        }
//                    }
//                }
//            }
//           
//
//            KotOut.newLine();
//            KotOut.write(Line);
//            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gNoOfLinesInKOTPrint");
//		    String noOfLinesOnKOTPrint = (String) objSetupParameter.get("gNoOfLinesInKOTPrint");
//            
//		    String gPrintOS = "windows";
//		    String gPrinterType="Inbuild";
//		    
//            for (int cntLines = 0; cntLines < Integer.parseInt(noOfLinesOnKOTPrint); cntLines++)
//            {
//                KotOut.newLine();
//            }
//            if ("linux".equalsIgnoreCase(gPrintOS))
//            {
//                KotOut.write("V");//Linux
//            }
//            else if ("windows".equalsIgnoreCase(gPrintOS))
//            {
//                if ("Inbuild".equalsIgnoreCase(gPrinterType))
//                {
//                    KotOut.write("V");
//                }
//                else
//                {
//                    KotOut.write("m");//windows
//                }
//            }
//
//           
//            if ("Reprint".equalsIgnoreCase(Reprint))
//            {
//                funShowTextFile(fileKOTPrint, "", "");
//            }
//            
//            objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gShowBill");
//		    String showBill=(String) objSetupParameter.get("gShowBill");
//            if ("gShowBill".equalsIgnoreCase(showBill))
//            {
//                funShowTextFile(fileKOTPrint, "", "Printer Info!2");
//            }
//
//            if (printYN.equals("Y"))
//            {
//                sql = "select strPrintOnBothPrinters from tblcostcentermaster where strCostCenterCode='" + costCenterCode + "' ";
//                Query  rsCostCenter = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
//                List rsCostCenter1 =rsCostCenter.list();
//                if (rsCostCenter1!=null)
//                {
//                	
//                	String printOnBothSides=(String)rsCostCenter1.get(0);
//                    funPrintToPrinter(primaryPrinterName, secondaryPrinterName, "kot", printOnBothSides, isReprint,clientCode, POSCode);
//                }
//                
//            }
//        }
//        catch (Exception e)
//        {
//           
//            e.printStackTrace();
//        }
//    }

    public void funShowTextFile(File file, String formName, String printerInfo)
    {
        try
        {
            String data = "";
            FileReader fread = new FileReader(file);
            //BufferedReader KOTIn = new BufferedReader(fread);
            FileInputStream fis = new FileInputStream(file);
            BufferedReader KOTIn = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line = "";
            while ((line = KOTIn.readLine()) != null)
            {
                data = data + line + "\n";
            }
            String fileName = file.getName();
            String name = "";
            if (formName.trim().length() > 0)
            {
                name = formName;
            }
            if ("Temp_DayEndReport.txt".equalsIgnoreCase(fileName))
            {
                name = "DayEnd";
            }
           
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void funPrintToPrinter(String primaryPrinterName, String secPrinterName, String type, String printOnBothPrinters, boolean isReprint,String clientCode, String POSCode)
    {
        try
        {
            String reportname = "";
            String gBillPrintPrinterPort = "";
            String fileName = "";
            JSONObject objSetupParameter=new JSONObject();
            if (type.equalsIgnoreCase("kot") || type.equalsIgnoreCase("checkkot"))
            {
                fileName = "Temp/Temp_KOT.txt";
                //fileName = "Temp/Temp_KOT.rtf";
            }
            else if (type.equalsIgnoreCase("dayend"))
            {
                fileName = "Temp/Temp_DayEndReport.txt";
                reportname = "dayend";
            }
            else if (type.equalsIgnoreCase("Adv Receipt"))
            {
                reportname = "Adv Receipt";
            }
            else if (type.equalsIgnoreCase("ItemWiseKOT"))
            {
                fileName = "/Temp/" + fileName + ".txt";
            }
            else
            {
                fileName = "Temp/Temp_Bill.txt";
                reportname = "bill";
            }
            
            String gPrintOS="windows";
            String gPrintType="Text File";
            
            if ("windows".equalsIgnoreCase(gPrintOS))//&& clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File")
            {
                if (type.equalsIgnoreCase("kot"))
                {
                    //System.out.println("G Print YN="+clsGlobalVarClass.gPrintKOTYN);
                	 objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gPrintKOTYN");
        		    String printKOTYN=(String) objSetupParameter.get("gPrintKOTYN");
                    if ("gPrintKOTYN".equalsIgnoreCase(printKOTYN))
                    {	
                        funPrintKOTWindows(primaryPrinterName, secPrinterName, printOnBothPrinters,clientCode,POSCode);
                        
                      objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gMultipleKOTPrint");
            		    String multipleKOTPrint = (String) objSetupParameter.get("gMultipleKOTPrint");
                        if ("gMultipleKOTPrint".equalsIgnoreCase(multipleKOTPrint))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName,POSCode,clientCode);
                            }
                            funPrintKOTWindows(primaryPrinterName, secPrinterName, printOnBothPrinters,clientCode,POSCode);
                        }
                    }
                }
                else if (type.equalsIgnoreCase("checkkot"))
                {
                    funPrintCheckKOTWindows(primaryPrinterName);
                }
                else if (type.equalsIgnoreCase("ItemWiseKOT"))
                {
                    funPrintItemWiseKOT(primaryPrinterName, secPrinterName, fileName);
                }
                else
                {
                    funPrintBillWindows(reportname,clientCode,POSCode);
                    
                    objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gMultiBillPrint");
        		    String multiBillPrint = (String) objSetupParameter.get("gMultiBillPrint");                 	
                    //Avoid Muliple Bill Printing
                    if (!type.equalsIgnoreCase("dayend"))
                    {
                        if ("gMultiBillPrint".equalsIgnoreCase(multiBillPrint))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName,POSCode,clientCode);
                            }
                            funPrintBillWindows(reportname,clientCode,POSCode);
                        }
                    }
                }
            }
            else if ("linux".equalsIgnoreCase(gPrintOS) && gPrintType.equalsIgnoreCase("Text File"))
            {
                if (type.equalsIgnoreCase("kot"))
                {
                    //System.out.println("G Print YN="+clsGlobalVarClass.gPrintKOTYN);
                    if ((boolean) objSetupParameter.get("gPrintKOTYN"))
                    {
                        Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);

                        if ((boolean) objSetupParameter.get("gMultipleKOTPrint"))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName,POSCode,clientCode);
                            }
                            process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                        }
                    }
                }
                else if (type.equalsIgnoreCase("checkkot"))
                {
                    Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                }
                else if (type.equalsIgnoreCase("ItemWiseKOT"))
                {
                    Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                }
                else
                {
                    //Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
                    Process process = Runtime.getRuntime().exec("lpr -P " + gBillPrintPrinterPort + " " + fileName, null);
                    if (!type.equalsIgnoreCase("dayend"))
                    {
                    	 if ((boolean) objSetupParameter.get("gMultiBillPrint"))
                        {
                            if (!isReprint)
                            {
                                funAppendDuplicate(fileName,POSCode,clientCode);
                            }
                            process = Runtime.getRuntime().exec("lpr -P " + gBillPrintPrinterPort + " " + fileName, null);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
   
private void funPrintKOTWindows(String primaryPrinterName, String secPrinterName, String printOnBothPrinters,String clientCode,String POSCode) throws JSONException
{
    String filePath = System.getProperty("user.dir");
    String fileName = (filePath + "/Temp/Temp_KOT.txt");
    //String fileName = (filePath + "/Temp/Temp_KOT.rtf");
    try
    {
        int printerIndex = 0;
        String printerStatus = "Not Found";
        System.out.println("Primary Name=" + primaryPrinterName);
        System.out.println("Sec Name=" + secPrinterName);

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        primaryPrinterName = primaryPrinterName.replaceAll("#", "\\\\");
        secPrinterName = secPrinterName.replaceAll("#", "\\\\");

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        for (int i = 0; i < printService.length; i++)
        {
            System.out.println("Service=" + printService[i].getName() + "\tPrim P=" + primaryPrinterName);
            String printerServiceName = printService[i].getName();

            if (primaryPrinterName.equalsIgnoreCase(printerServiceName))
            {
                System.out.println("Printer=" + primaryPrinterName);
                printerIndex = i;
                printerStatus = "Found";
                break;
            }
        }

        if (printerStatus.equals("Found"))
        {
            DocPrintJob job = printService[printerIndex].createPrintJob();
            FileInputStream fis = new FileInputStream(fileName);
            DocAttributeSet das = new HashDocAttributeSet();
            Doc doc = new SimpleDoc(fis, flavor, das);
            job.print(doc, pras);
            String printerInfo = "";
            String gPrinterQueueStatus="";
            PrintServiceAttributeSet att = printService[printerIndex].getAttributes();
            for (Attribute a : att.toArray())
            {
                String attributeName;
                String attributeValue;
                attributeName = a.getName();
                attributeValue = att.get(a.getClass()).toString();
                if (attributeName.trim().equalsIgnoreCase("queued-job-count"))
                {
                    gPrinterQueueStatus = attributeValue;
                    printerInfo = primaryPrinterName + "!" + attributeValue;
                    //System.out.println(attributeName + " : " + attributeValue);
                }
            }
            if (printOnBothPrinters.equals("Y"))
            {
                funPrintOnSecPrinter(secPrinterName, fileName,clientCode,POSCode);
            }
        }
        else
        {
            funPrintOnSecPrinter(secPrinterName, fileName,clientCode,POSCode);
            //JOptionPane.showMessageDialog(null,primaryPrinterName+" Printer Not Found");
        }
    }
    catch (Exception e)
    {
        e.printStackTrace();
        JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gShowPrinterErrorMsg");
	    
        
        if ((boolean)objSetupParameter.get("gShowPrinterErrorMsg"))
        {
            try
            {
                funPrintOnSecPrinter(secPrinterName, fileName,clientCode,POSCode);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            
        }
    }
}

private void funAppendDuplicate(String fileName,String POSCode,String clientCode)
{
    try
    {
        File fileKOTPrint = new File(fileName);
//        RandomAccessFile f = new RandomAccessFile(fileKOTPrint, "rw");
//        f.seek(0); // to the beginning                  
//        BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint), "UTF8"));            
//        funPrintBlankSpace("[DUPLICATE]", KotOut);            
//        KotOut.write("[DUPLICATE]");              
//        KotOut.newLine();            
//        KotOut.close();
//        f.close();                                    

        String filePath = System.getProperty("user.dir");
        filePath += "/Temp/Temp_KOT2.txt";
        File fileKOTPrint2 = new File(filePath);
        BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint2), "UTF8"));
        funPrintBlankSpace("[DUPLICATE]", KotOut,POSCode,clientCode);
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


private void funPrintCheckKOTWindows(String printerName)
{
    try
    {
    	String gBillPrintPrinterPort="";
        int printerIndex = 0;
        String filePath = System.getProperty("user.dir");
        String filename = (filePath + "/Temp/Temp_KOT.txt");
        String billPrinterName = gBillPrintPrinterPort;
        billPrinterName = billPrinterName.replaceAll("#", "\\\\");
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        for (int i = 0; i < printService.length; i++)
        {
            System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
            if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
            {
                System.out.println("Bill Printer Sel=" + billPrinterName);
                printerIndex = i;
                break;
            }
        }

        DocPrintJob job = printService[printerIndex].createPrintJob();
        FileInputStream fis = new FileInputStream(filename);
        DocAttributeSet das = new HashDocAttributeSet();
        Doc doc = new SimpleDoc(fis, flavor, das);
        job.print(doc, pras);
    }
    catch (Exception e)
    {
       e.printStackTrace();
    }
}


private void funPrintItemWiseKOT(String primaryPrinterName, String secPrinterName, String fileName)
{
    String filePath = System.getProperty("user.dir");
    fileName = (filePath + "/Temp/" + fileName + ".txt");
    String gBillPrintPrinterPort="";
    try
    {
        String billPrinterName = gBillPrintPrinterPort;

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
                System.out.println("ItemWise KOT Printer found=>" + billPrinterName);
                printerIndex = i;
                break;
            }
        }
        //PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        //DocPrintJob job = defaultService.createPrintJob();
        DocPrintJob job = printService[printerIndex].createPrintJob();
        FileInputStream fis = new FileInputStream(fileName);

        DocAttributeSet das = new HashDocAttributeSet();
        Doc doc = new SimpleDoc(fis, flavor, das);
        job.print(doc, printerReqAtt);
        System.out.println("Print Job Sent->" + fileName);

    }
    catch (Exception e)
    {
        e.printStackTrace();
        
    }
}

/**
 * printBillWindows() method print to Default Printer. No Parameter required
 */
private void funPrintBillWindows(String type,String clientCode,String POSCode)
{
    try
    {
        //System.out.println("Print Bill");
        String filePath = System.getProperty("user.dir");
        String fileName = "";
        String gBillPrintPrinterPort="";
        String billPrinterName = gBillPrintPrinterPort;
        String gAdvReceiptPrinterPort="";

        if (type.equalsIgnoreCase("bill"))
        {
            fileName = (filePath + "/Temp/Temp_Bill.txt");
        }
        else if (type.equalsIgnoreCase("Adv Receipt"))
        {
            fileName = (filePath + "/Temp/Temp_Bill.txt");
            billPrinterName = gAdvReceiptPrinterPort;
        }
        else if (type.equalsIgnoreCase("dayend"))
        {
            fileName = (filePath + "/Temp/Temp_DayEndReport.txt");
        }

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
                System.out.println("Bill Printer Sel=" + billPrinterName);
                printerIndex = i;
                break;
            }
        }
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        //DocPrintJob job = defaultService.createPrintJob();
        DocPrintJob job = printService[printerIndex].createPrintJob();
        FileInputStream fis = new FileInputStream(fileName);
        DocAttributeSet das = new HashDocAttributeSet();
        Doc doc = new SimpleDoc(fis, flavor, das);
        job.print(doc, printerReqAtt);

        
        JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gOpenCashDrawerAfterBillPrintYN");
		String openCashDrawerAfterBillPrintYN = (String) objSetupParameter.get("gOpenCashDrawerAfterBillPrintYN");   
        if ("gOpenCashDrawerAfterBillPrintYN".equalsIgnoreCase(openCashDrawerAfterBillPrintYN))
        {
            //objUtility.funInvokeSampleJasper();
        }
    }
    catch (Exception e)
    {
        e.printStackTrace();
        
    }
}

private void funPrintOnSecPrinter(String secPrinterName, String fileName,String clientCode,String POSCode) throws Exception
{
    String printerStatus = "Not Found";
    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
    int printerIndex = 0;
    for (int i = 0; i < printService.length; i++)
    {
        System.out.println("Service=" + printService[i].getName() + "\tSec P=" + secPrinterName);
        String printerServiceName = printService[i].getName();

        if (secPrinterName.equalsIgnoreCase(printerServiceName))
        {
            System.out.println("Sec Printer=" + secPrinterName);
            printerIndex = i;
            printerStatus = "Found";
            break;
        }
    }
    if (printerStatus.equals("Found"))
    {
        String printerInfo = "";
        String gPrinterQueueStatus="";
        
        DocPrintJob job = printService[printerIndex].createPrintJob();
        FileInputStream fis = new FileInputStream(fileName);
        DocAttributeSet das = new HashDocAttributeSet();
        Doc doc = new SimpleDoc(fis, flavor, das);
//        job.addPrintJobListener(new MyPrintJobListener());
        job.print(doc, pras);

        PrintServiceAttributeSet att = printService[printerIndex].getAttributes();
        for (Attribute a : att.toArray())
        {
            String attributeName;
            String attributeValue;
            attributeName = a.getName();
            attributeValue = att.get(a.getClass()).toString();
            if (attributeName.trim().equalsIgnoreCase("queued-job-count"))
            {
                gPrinterQueueStatus = attributeValue;
                printerInfo = secPrinterName + "!" + attributeValue;
            }
            System.out.println(attributeName + " : " + attributeValue);
        }
        JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, POSCode, "gShowBill");
	    String showBill = (String) objSetupParameter.get("gShowBill");
        if ("gShowBill".equalsIgnoreCase(showBill))
        {
            funShowTextFile(new File(fileName), "", printerInfo);
        }
    }
    else
    {
        JOptionPane.showMessageDialog(null, secPrinterName + " Printer Not Found");
    }
}
}