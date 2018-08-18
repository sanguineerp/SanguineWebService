package com.apos.dao;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsOperatorDtl;
import com.apos.service.clsSetupService;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.InetAddress;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;

import com.google.gson.reflect.TypeToken;
import com.webservice.util.clsBillDtl;
import com.webservice.util.clsUtilityFunctions;

@Repository("clsCustomerHistoryFlashDao")
@Transactional(value = "webPOSTransactionManager")
public class clsCustomerHistoryFlashDao {
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired
	private clsSetupService objSetupService;
	
	@Autowired 
	private clsUtilityFunctions objUtilityFunctions;
	
	 private String  sql;
	
    public JSONObject funFillAllTables(JSONObject jObjCustomerHistoryFlash)
	{
    	JSONObject jObj=new JSONObject();
    	try
    	{
    	String posCode=jObjCustomerHistoryFlash.getString("posCode");
    	String reportType=jObjCustomerHistoryFlash.getString("reportType");
    	String selectedTab=jObjCustomerHistoryFlash.getString("selectedTab");
    	String fromDate=jObjCustomerHistoryFlash.getString("fromDate");
    	String toDate=jObjCustomerHistoryFlash.getString("toDate");
    	
    	String webStockUserCode=jObjCustomerHistoryFlash.getString("webStockUserCode");
    	
    	
        if (selectedTab.equalsIgnoreCase("Customer Wise"))
        {
        	  String custCode=jObjCustomerHistoryFlash.getString("custCode");
              if (reportType.equalsIgnoreCase("Item Wise")) {
                 
            	  jObj = funCustomerWiseItemSales(posCode,reportType, selectedTab, fromDate, toDate, custCode,webStockUserCode);
              } 
              else 
              {
                  
            	  jObj = funCustomerWiseBillSales(posCode,reportType, selectedTab, fromDate, toDate, custCode,webStockUserCode);
              }
              
              						
          }
          if (selectedTab.equalsIgnoreCase("Top Spenders")) 
          {
        	  String cmbAmount=jObjCustomerHistoryFlash.getString("cmbAmount");
          	String txtAmount=jObjCustomerHistoryFlash.getString("txtAmount");
        	  jObj = funTopSpenderWiseSales(posCode,fromDate,toDate,webStockUserCode,cmbAmount,txtAmount);
              
          }
          if (selectedTab.equalsIgnoreCase("Non Spenders")) 
          {
        	  jObj = funNonSpenderWiseSales(posCode,fromDate,toDate);
              
          }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return jObj;
		
	}
	
    
    private JSONObject funCustomerWiseItemSales(String posCode,String reportType,String selectedTab,String fromDate,String toDate,String custCode,String webStockUserCode) 
    {
//       String sbSqlLiveBill = "";
//       String sbSqlQFileBill = "";
//       String sbSqlFilters = "";
    	 StringBuilder sbSqlLiveBill = new StringBuilder();
         StringBuilder sbSqlQFileBill = new StringBuilder();
         StringBuilder sbSqlFilters = new StringBuilder();
        List list =null;
        JSONObject jObjCustomerWiseTblData=new JSONObject();

        try 
        {
        	JSONArray jArrlistOfBillData=new JSONArray();
        	JSONArray jArrlistOfTotalData=new JSONArray();
            sql = "";
          
            
//            sbSqlLiveBill="select a.strBillNo,date(a.dteBillDate)"
//                    + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
//                    + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + webStockUserCode + "' "
//                    + "from tblbillhd a,tblbilldtl b,tblcustomermaster c,tblitemmaster d "
//                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
//                    + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
//                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'";
//
//            sbSqlQFileBill="select a.strBillNo,date(a.dteBillDate)"
//                    + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
//                    + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + webStockUserCode + "' "
//                    + "from tblqbillhd a,tblqbilldtl b,tblcustomermaster c,tblitemmaster d "
//                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
//                    + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
//                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + fromDate + "'";
//
//            if (!posCode.equals("All")) {
//                sbSqlFilters =" and a.strPOSCode='" + posCode + "' ";
//            }
//
//            sbSqlFilters=sbSqlFilters + " group by d.strItemName";
//
//            boolean flgRecords = false;
//            double qty = 0, amount = 0;
//            double totalAmt = 0,totalAmt1 = 0;
//
//            sbSqlLiveBill=sbSqlLiveBill + sbSqlFilters;
//            sbSqlQFileBill=sbSqlQFileBill + sbSqlFilters;
            sbSqlLiveBill.setLength(0);
            sbSqlQFileBill.setLength(0);
            sbSqlFilters.setLength(0);

            sbSqlLiveBill.append("select a.strBillNo,date(a.dteBillDate)"
                    + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
                    + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + webStockUserCode + "' "
                    + "from tblbillhd a,tblbilldtl b,tblcustomermaster c,tblitemmaster d "
                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
                    + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

            sbSqlQFileBill.append("select a.strBillNo,date(a.dteBillDate)"
                    + ",c.strCustomerCode,c.strCustomerName,d.strItemName"
                    + ",TRUNCATE(sum(b.dblQuantity),0),sum(b.dblAmount),'" + webStockUserCode + "' "
                    + "from tblqbillhd a,tblqbilldtl b,tblcustomermaster c,tblitemmaster d "
                    + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strCustomerCode=c.strCustomerCode "
                    + "and b.strItemCode=d.strItemCode and a.strCustomerCode='" + custCode + "'"
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'");

            if (!posCode.equals("All")) {
                sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sbSqlFilters.append(" group by d.strItemName");

            boolean flgRecords = false;
            double qty = 0, amount = 0;
            double totalAmt = 0,totalAmt1 = 0;

            sbSqlLiveBill.append(sbSqlFilters);
            sbSqlQFileBill.append(sbSqlFilters);
            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
			
			list = query.list();
			JSONObject jobjTotal=new JSONObject();
            if (list!=null)
			{
            	
            	for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					
					JSONObject jobj=new JSONObject();
					
					jobj.put("billNo",Array.get(obj, 0));
					jobj.put("billDate",Array.get(obj, 1));
					jobj.put("customerCode",Array.get(obj, 2));
					jobj.put("customerName",Array.get(obj, 3));
					jobj.put("itemName",Array.get(obj, 4));
					jobj.put("dblQuantity",Array.get(obj, 5));
					jobj.put("dblAmount",Array.get(obj, 6));
					jArrlistOfBillData.put(jobj);
					double grandTotal=Double.parseDouble(obj[6].toString());

                
                totalAmt +=grandTotal ; // Grand Total    
                
            }
				
			}
			 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
				
			List list1 = query.list();
           
			if (list1!=null)
			{
				//JSONObject jobjTotal=new JSONObject();
				for(int i=0; i<list1.size(); i++)
				{
					Object[] obj = (Object[]) list1.get(i);
					
					
					JSONObject jobj=new JSONObject();
					
					jobj.put("billNo",Array.get(obj, 0));
					jobj.put("billDate",Array.get(obj, 1));
					jobj.put("customerCode",Array.get(obj, 2));
					jobj.put("customerName",Array.get(obj, 3));
					jobj.put("itemName",Array.get(obj, 4));
					jobj.put("dblQuantity",Array.get(obj, 5));
					jobj.put("dblAmount",Array.get(obj, 6));
					jArrlistOfBillData.put(jobj);
					double grandTotal=Double.parseDouble(obj[6].toString());

	                
	                totalAmt +=grandTotal ; // Grand Total   
	                
				}
				
			}
			jobjTotal.put("totAmt", totalAmt);
			jobjTotal.put("Total", "Total");
            jArrlistOfTotalData.put(jobjTotal);
            
//			jArrlistOfTotalData.put("Total");
			jObjCustomerWiseTblData.put("CustomerWiseTblData", jArrlistOfBillData);  
			jObjCustomerWiseTblData.put("TotalTblData", jArrlistOfTotalData);  
			jObjCustomerWiseTblData.put("cmbName", "Item Wise"); 
			jObjCustomerWiseTblData.put("tabName", "Customer Wise"); 

         
        } 
         catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            sbSqlLiveBill = null;
            sbSqlQFileBill = null;
            sbSqlFilters = null;
        }
		return jObjCustomerWiseTblData;
    }
	
    
    /**
     * Customer Wise Sales
     */
    private JSONObject funCustomerWiseBillSales(String posCode,String reportType,String selectedTab,String fromDate,String toDate,String custCode,String webStockUserCode) {
//        String sbSqlLiveBill ="" ;
//        String sbSqlQFileBill ="";
//        String sbSqlFilters = "";
    	StringBuilder sbSqlLiveBill = new StringBuilder();
        StringBuilder sbSqlQFileBill = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        JSONObject jObjCustomerWiseTblData=new JSONObject();
        
        try {
           
            sql = "";
            JSONArray jArrlistOfBillData=new JSONArray();
            JSONArray jArrlistOfTotalData=new JSONArray();
//            sbSqlLiveBill="select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
//                    + " ,f.strPOSName"
//                    + ", ifnull(d.strSettelmentDesc,'') as payMode"
//                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
//                    + " ,ifnull(c.dblSettlementAmt,0.00)"
//                    + " ,ifnull(c.strRemark,'')"
//                    + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
//                    + " from tblbillhd  a "
//                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
//                    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
//                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
//                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
//                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
//                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
//                    + " AND a.strCustomerCode='" + custCode + "'";
//
//
//            sbSqlQFileBill="select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
//                    + " ,f.strPOSName"
//                    + ", ifnull(d.strSettelmentDesc,'') as payMode"
//                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
//                    + " ,ifnull(c.dblSettlementAmt,0.00)"
//                    + " ,ifnull(c.strRemark,'')"
//                    + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
//                    + " from tblqbillhd a "
//                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
//                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
//                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
//                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
//                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
//                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
//                    + " AND a.strCustomerCode='" + custCode + "'";
//
//            if (!posCode.equals("All")) 
//            {
//                sbSqlFilters=" and a.strPOSCode='" + posCode + "' ";
//            }
//
//           
//            sbSqlFilters= " order by a.strBillNo desc ";
//            boolean flgRecords = false;
//            double grandTotal = 0;
//            double totalDiscAmt = 0, totalSubTotal = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0;
//
//            sbSqlLiveBill=sbSqlLiveBill + sbSqlFilters;
//            sbSqlQFileBill=sbSqlQFileBill + sbSqlFilters;
            sbSqlLiveBill.setLength(0);
            sbSqlQFileBill.setLength(0);
            sbSqlFilters.setLength(0);

//            sbSqlLiveBill.append("select ifnull(count(a.strBillNo),'0'),,left(a.dteBillDate,10),left(right(a.dteDateCreated,8),5) as BillTime,ifnull(sum(a.dblGrandTotal),'0.00'),'" + clsGlobalVarClass.gUserCode + "' "
//                    + "from tblbillhd a,tblcustomermaster b "
//                    + "where a.strCustomerCode=b.strCustomerCode "
//                    + "and date(a.dteBillDate) between '" + DateFrom + "' and '" + DateTo + "'");
            sbSqlLiveBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
                    + " ,f.strPOSName"
                    + ", ifnull(d.strSettelmentDesc,'') as payMode"
                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
                    + " ,ifnull(c.dblSettlementAmt,0.00)"
                    + " ,ifnull(c.strRemark,'')"
                    + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
                    + " from tblbillhd  a "
                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                    + " left outer join tblbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                    + " AND a.strCustomerCode='" + custCode + "'");

//            sbSqlQFileBill.append("select ifnull(b.strCustomerCode,'ND'),ifnull(b.strCustomerName,'ND')"
//                    + ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00'),'" + clsGlobalVarClass.gUserCode + "' "
//                    + "from tblqbillhd a,tblcustomermaster b "
//                    + "where a.strCustomerCode=b.strCustomerCode "
//                    + "and date(a.dteBillDate) between '" + DateFrom + "' and '" + DateTo + "'");
            sbSqlQFileBill.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate, '%d-%m-%y'),left(right(a.dteDateCreated,8),5) as BillTime"
                    + " ,f.strPOSName"
                    + ", ifnull(d.strSettelmentDesc,'') as payMode"
                    + " ,ifnull(a.dblSubTotal,0.00),IFNULL(a.dblDiscountPer,0), IFNULL(a.dblDiscountAmt,0.00),a.dblTaxAmt"
                    + " ,ifnull(c.dblSettlementAmt,0.00)"
                    + " ,ifnull(c.strRemark,'')"
                    + " ,a.dblTipAmount,a.strDiscountRemark,ifnull(h.strReasonName ,'NA') "
                    + " from tblqbillhd a "
                    + " left outer join tblposmaster f on a.strPOSCode=f.strPOSCode "
                    + " left outer join tblqbillsettlementdtl c on a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
                    + " left outer join tblsettelmenthd d on c.strSettlementCode=d.strSettelmentCode "
                    + " left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode "
                    + " left outer join tblreasonmaster h on a.strReasonCode=h.strReasonCode "
                    + " where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                    + " AND a.strCustomerCode='" + custCode + "'");

            if (!posCode.equals("All")) {
                sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
            }

            // sbSqlFilters.append(" GROUP BY b.strCustomerCode");
            sbSqlFilters.append(" order by a.strBillNo desc ");
            boolean flgRecords = false;
            double grandTotal = 0;
            double totalDiscAmt = 0, totalSubTotal = 0, totalTaxAmt = 0, totalSettleAmt = 0, totalTipAmt = 0;

            sbSqlLiveBill.append(sbSqlFilters);
            sbSqlQFileBill.append(sbSqlFilters);
           
            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
			
			List list = query.list();
			JSONObject jobjTot=new JSONObject();
            if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					//JSONObject jobjTot=new JSONObject();
					JSONObject jobj=new JSONObject();
					
					jobj.put("billNo",Array.get(obj, 0));
					jobj.put("billDate",Array.get(obj, 1));
					jobj.put("dblDateCreated",Array.get(obj, 2));
					jobj.put("posName",Array.get(obj, 3));
					jobj.put("settelmentDesc",Array.get(obj, 4));
					jobj.put("dblSubTotal",Array.get(obj, 5));
					jobj.put("dblDiscountPer",Array.get(obj, 6));
					jobj.put("dblDiscountAmt",Array.get(obj, 7));
					jobj.put("dblTaxAmt",Array.get(obj, 8));
					jobj.put("dblSettlementAmt",Array.get(obj, 9));
					jobj.put("strRemark",Array.get(obj, 10));
					jobj.put("dblTipAmount",Array.get(obj, 11));
					jobj.put("strDiscountRemark",Array.get(obj, 12));
					jobj.put("strReasonName",Array.get(obj, 13));
					jArrlistOfBillData.put(jobj);
					double grandTot=Double.parseDouble(obj[7].toString());

                
					 totalDiscAmt +=grandTot ; // Grand Total     
					 totalSubTotal += Double.parseDouble(obj[5].toString());
		                totalTaxAmt += Double.parseDouble(obj[8].toString());
		                totalSettleAmt += Double.parseDouble(obj[9].toString()); // Grand Total                
		                totalTipAmt += Double.parseDouble(obj[11].toString()); // tip Amt
		                
		               
            }
				 
			}

		 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
				
				List list1 = query.list();
	            if (list1!=null)
				{
					for(int i=0; i<list1.size(); i++)
					{
						Object[] obj = (Object[]) list1.get(i);
						
						//JSONObject jobjTot=new JSONObject();
						JSONObject jobj=new JSONObject();
						
						jobj.put("billNo",Array.get(obj, 0));
						jobj.put("billDate",Array.get(obj, 1));
						jobj.put("dblDateCreated",Array.get(obj, 2));
						jobj.put("posName",Array.get(obj, 3));
						jobj.put("settelmentDesc",Array.get(obj, 4));
						jobj.put("dblSubTotal",Array.get(obj, 5));
						jobj.put("dblDiscountPer",Array.get(obj, 6));
						jobj.put("dblDiscountAmt",Array.get(obj, 7));
						jobj.put("dblTaxAmt",Array.get(obj, 8));
						jobj.put("dblSettlementAmt",Array.get(obj, 9));
						jobj.put("strRemark",Array.get(obj, 10));
						jobj.put("dblTipAmount",Array.get(obj, 11));
						jobj.put("strDiscountRemark",Array.get(obj, 12));
						jobj.put("strReasonName",Array.get(obj, 13));
						jArrlistOfBillData.put(jobj);
						double grandTot=Double.parseDouble(obj[7].toString());

	                
						 totalDiscAmt +=grandTot ; // Grand Total     
						 totalSubTotal += Double.parseDouble(obj[5].toString());
			                totalTaxAmt += Double.parseDouble(obj[8].toString());
			                totalSettleAmt += Double.parseDouble(obj[9].toString()); // Grand Total                
			                totalTipAmt += Double.parseDouble(obj[11].toString()); // tip Amt
 
			                
					}
				}
	            jobjTot.put("Total", "Total");
	            jobjTot.put("totalSubTotal", totalSubTotal);
	            jobjTot.put("blank", "");
                
                jobjTot.put("totalDiscAmt", totalDiscAmt);
                
                jobjTot.put("totalTaxAmt", totalTaxAmt);
                jobjTot.put("totalSettleAmt", totalSettleAmt);
                jobjTot.put("totalTipAmt", totalTipAmt);
                
                jArrlistOfTotalData.put(jobjTot);
            
//	            jArrlistOfTotalData.put("Total");
//	            jArrlistOfTotalData.put("");
	            jObjCustomerWiseTblData.put("CustomerWiseTblData", jArrlistOfBillData); 
	            jObjCustomerWiseTblData.put("TotalTblData", jArrlistOfTotalData); 
	            jObjCustomerWiseTblData.put("cmbName", "Bill Wise"); 
	            jObjCustomerWiseTblData.put("tabName", "Customer Wise"); 


        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            sbSqlLiveBill = null;
            sbSqlQFileBill = null;
            sbSqlFilters = null;
        }
		return jObjCustomerWiseTblData;
    }


    /**
     * Top Spender Details
     */
    private JSONObject funTopSpenderWiseSales(String posCode,String fromDate,String toDate,String webStockUserCode,String cmbAmount,String txtAmount) 
    {
//        String sbSqlLiveBill = "";
//        String sbSqlQFileBill ="" ;
//        String sbSqlFilters = "";
    	StringBuilder sbSqlLiveBill = new StringBuilder();
        StringBuilder sbSqlQFileBill = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        JSONObject jObjTopSpendersTblData=new JSONObject();
        int colCount=4,rowCount=0,listSize=0;
        
        try {
            
            sql = "";
            JSONArray jArrlistOfBillData=new JSONArray();
            JSONArray jArrlistOfTotalData=new JSONArray();
            
            sbSqlLiveBill.setLength(0);
            sbSqlQFileBill.setLength(0);
            sbSqlFilters.setLength(0);

            sbSqlLiveBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
                    + ",count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
                    + "from tblbillhd a,tblcustomermaster b "
                    + "where a.strCustomerCode=b.strCustomerCode "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                    + "and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'");

            sbSqlQFileBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
                    + ",count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
                    + "from tblqbillhd a,tblcustomermaster b "
                    + "where a.strCustomerCode=b.strCustomerCode "
                    + "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
                    + "and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'");

            if (!posCode.equals("All")) {
                sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sbSqlFilters.append(" GROUP BY a.strBillNo");
            sbSqlFilters.append(" order by a.strBillNo desc");
            boolean flgRecords = false;
            double grandTotal = 0;
            double totalSettleAmt = 0;
            sbSqlLiveBill.append(sbSqlFilters);
            sbSqlQFileBill.append(sbSqlFilters);
            
            
//            sbSqlLiveBill="select longMobileNo,ifnull(b.strCustomerName,'ND')"
//                    + " ,count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
//                    + " from tblbillhd a,tblcustomermaster b "
//                    + " where a.strCustomerCode=b.strCustomerCode "
//                    + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
//                    + " and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'";
//
//            sbSqlQFileBill="select longMobileNo,ifnull(b.strCustomerName,'ND')"
//                    + " ,count(a.strBillNo),sum(a.dblGrandTotal),'" + webStockUserCode + "' "
//                    + " from tblqbillhd a,tblcustomermaster b "
//                    + " where a.strCustomerCode=b.strCustomerCode "
//                    + " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'"
//           			+ " and a.dblGrandTotal " + cmbAmount + " '" + txtAmount + "'";
//
//            if (!posCode.equals("All")) {
//                sbSqlFilters=" and a.strPOSCode='" + posCode + "' ";
//            }
//
//            sbSqlFilters=" GROUP BY a.strBillNo";
//            sbSqlFilters=" order by a.strBillNo desc";
//            boolean flgRecords = false;
//            double grandTotal = 0;
//            double totalSettleAmt = 0;
//            sbSqlLiveBill=sbSqlLiveBill + sbSqlFilters;
//            sbSqlQFileBill=sbSqlQFileBill + sbSqlFilters;

           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
			
			List list = query.list();
			
			JSONObject jobjTot=new JSONObject();
            if (list!=null)
			{
            	listSize=list.size();
            	rowCount = listSize;
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					
					JSONObject jobj=new JSONObject();
					
					int billNo=Integer.parseInt(obj[2].toString());
					jobj.put("strBillNo",billNo);
				
					double dblGrandTotal=Double.parseDouble(obj[3].toString());
					jobj.put("dblGrandTotal",dblGrandTotal);
				
					jobj.put("LongMobileNo",Array.get(obj, 0));
					
					jobj.put("StrCustomerName",Array.get(obj, 1));
					
//					jobj.put("strBillNo",Array.get(obj, 2));
//					jobj.put("dblGrandTotal",Array.get(obj, 3));
					jArrlistOfBillData.put(jobj);
           
					totalSettleAmt += Double.parseDouble(obj[3].toString()); // Grand Total 
					colCount++;
//					jobjTot.put("totalSettleAmt", totalSettleAmt);
//					jArrlistOfTotalData.put(jobjTot);
				}
			}
			 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
					
					List list1 = query.list();
		      if (list1!=null)
		      {
		    	  listSize=list1.size();
		    	  rowCount +=listSize;
		    	  for(int i=0; i<list1.size(); i++)
						{
							Object[] obj = (Object[]) list1.get(i);
							
							
							JSONObject jobj=new JSONObject();
							//JSONObject jobjTot=new JSONObject();
							
							int billNo=Integer.parseInt(obj[2].toString());
							jobj.put("strBillNo",billNo);
						
							double dblGrandTotal=Double.parseDouble(obj[3].toString());
							jobj.put("dblGrandTotal",dblGrandTotal);
							
							jobj.put("LongMobileNo",Array.get(obj, 0));
							
							jobj.put("StrCustomerName",Array.get(obj, 1));
						
//							jobj.put("StrBillNo",Array.get(obj, 2));
//							jobj.put("DblGrandTotal",Array.get(obj, 3));
							jArrlistOfBillData.put(jobj);
		           
							totalSettleAmt += Double.parseDouble(obj[3].toString()); // Grand Total 
							
		            }	
				
				}
		      jobjTot.put("totalSettleAmt", totalSettleAmt);
		      jobjTot.put("Total", "Total");
			  jArrlistOfTotalData.put(jobjTot);
//		      jArrlistOfTotalData.put("Total");
		      jObjTopSpendersTblData.put("TopSpendersTblData", jArrlistOfBillData);
		      jObjTopSpendersTblData.put("TotalTblData", jArrlistOfTotalData);
		      jObjTopSpendersTblData.put("tabName", "Top Spenders"); 
		      jObjTopSpendersTblData.put("Col Count", colCount);
		      jObjTopSpendersTblData.put("Row Count", rowCount);
           
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally
        {
            sbSqlLiveBill = null;
            sbSqlQFileBill = null;
            sbSqlFilters = null;
        }
		return jObjTopSpendersTblData;
    }

    /**
     * Non-Spender Details
     */
    private JSONObject funNonSpenderWiseSales(String posCode,String fromDate,String toDate) {
//        String sbSqlLiveBill = "";
//        String sbSqlQFileBill = "";
//        String sbSqlFilters = "";
        StringBuilder sbSqlLiveBill = new StringBuilder();
        StringBuilder sbSqlQFileBill = new StringBuilder();
        StringBuilder sbSqlFilters = new StringBuilder();
        JSONObject jObjNonSpendersTblData=new JSONObject();
        int colCount=3,rowCount=0;
        
        try {
            
            sql = "";
            JSONArray jArrlistOfBillData=new JSONArray();
            
            sbSqlLiveBill.setLength(0);
            sbSqlQFileBill.setLength(0);
            sbSqlFilters.setLength(0);

//            sbSqlLiveBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
//                    + ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00'),'" + clsGlobalVarClass.gUserCode + "' "
//                    + "from tblbillhd a,tblcustomermaster b "
//                    + "where a.strCustomerCode=b.strCustomerCode "
//                    + "and date(a.dteBillDate) between '" + DateFrom + "' and '" + DateTo + "'"
//                    + "and a.dblGrandTotal=0.00");
            sbSqlLiveBill.append("SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
                    + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
                    + "FROM tblbillhd a,tblcustomermaster b\n"
                    + "WHERE a.strCustomerCode=b.strCustomerCode "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + "AND a.dblGrandTotal=0.00");

//            sbSqlQFileBill.append("select longMobileNo,ifnull(b.strCustomerName,'ND')"
//                    + ",ifnull(count(a.strBillNo),'0'),ifnull(sum(a.dblGrandTotal),'0.00'),'" + clsGlobalVarClass.gUserCode + "' "
//                    + "from tblqbillhd a,tblcustomermaster b "
//                    + "where a.strCustomerCode=b.strCustomerCode "
//                    + "and date(a.dteBillDate) between '" + DateFrom + "' and '" + DateTo + "'"
//                    + "and a.dblGrandTotal='0.00'");
            sbSqlQFileBill.append("SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
                    + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
                    + "FROM tblqbillhd a,tblcustomermaster b\n"
                    + "WHERE a.strCustomerCode=b.strCustomerCode "
                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
                    + "AND a.dblGrandTotal=0.00");

            if (!posCode.equals("All")) {
                sbSqlFilters.append(" and a.strPOSCode='" + posCode + "' ");
            }

            sbSqlFilters.append(" GROUP BY b.strCustomerCode");
            sbSqlFilters.append(" order by DATE(a.dteBillDate) desc");
            boolean flgRecords = false;
            double grandTotal = 0;

            sbSqlLiveBill.append(sbSqlFilters);
            sbSqlQFileBill.append(sbSqlFilters);
//            sbSqlLiveBill="SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
//                    + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
//                    + "FROM tblbillhd a,tblcustomermaster b\n"
//                    + "WHERE a.strCustomerCode=b.strCustomerCode "
//                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
//                    + "AND a.dblGrandTotal=0.00";
//
//
//            sbSqlQFileBill="SELECT longMobileNo, IFNULL(b.strCustomerName,'ND'), COUNT(a.strBillNo), SUM(a.dblGrandTotal)"
//                    + ",max(DATE_FORMAT(a.dteBillDate, '%d-%m-%y'))\n"
//                    + "FROM tblqbillhd a,tblcustomermaster b\n"
//                    + "WHERE a.strCustomerCode=b.strCustomerCode "
//                    + "AND DATE(a.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
//                    + "AND a.dblGrandTotal=0.00";
//
//            if (!posCode.equals("All")) {
//                sbSqlFilters=" and a.strPOSCode='" + posCode + "' ";
//            }
//
//            sbSqlFilters=" GROUP BY b.strCustomerCode";
//            sbSqlFilters=" order by DATE(a.dteBillDate) desc";
//            boolean flgRecords = false;
//            double grandTotal = 0;
//
//            sbSqlLiveBill+=sbSqlFilters;
//            sbSqlQFileBill+=sbSqlFilters;

            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
			
			List list = query.list();
            if (list!=null)
			{
            	rowCount=list.size();
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					
					
					JSONObject jobj=new JSONObject();
					
					jobj.put("longMobileNo",Array.get(obj, 0));
				
					jobj.put("strCustomerName",Array.get(obj, 1));
				
					jobj.put("strBillNo",Array.get(obj, 2));
				
					jobj.put("dblGrandTotal",Array.get(obj, 3));
					
					jobj.put("dteBillDate",Array.get(obj, 4));
				
					jArrlistOfBillData.put(jobj);
					
					
				}
			}
            
             query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
			
			List list1 = query.list();
            if (list1!=null)
			{
            	rowCount=list1.size();
            	for(int i=0; i<list1.size(); i++)
				{
					Object[] obj = (Object[]) list1.get(i);
					
					
					JSONObject jobj=new JSONObject();
					
					jobj.put("longMobileNo",Array.get(obj, 0));
					
					jobj.put("strCustomerName",Array.get(obj, 1));
					
					jobj.put("strBillNo",Array.get(obj, 2));
					jobj.put("dblGrandTotal",Array.get(obj, 3));
					jobj.put("dteBillDate",Array.get(obj, 4));
					
					jArrlistOfBillData.put(jobj);
					
   				}
			}
            jObjNonSpendersTblData.put("NonSpendersTblData", jArrlistOfBillData);
            jObjNonSpendersTblData.put("tabName", "Non Spenders"); 
            jObjNonSpendersTblData.put("Col Count", colCount);
            jObjNonSpendersTblData.put("Row Count", rowCount);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            sbSqlLiveBill = null;
            sbSqlQFileBill = null;
            sbSqlFilters = null;
        }
		return jObjNonSpendersTblData;
    }

    
}
