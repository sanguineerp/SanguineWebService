package com.apos.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsMenuHeadMasterModel;
import com.apos.service.clsSetupService;

@Repository("clsDirectBillerDao")
@Transactional(value = "webPOSTransactionManager")
public class clsDirectBillerDao
{
	@Autowired
	SessionFactory		webPOSSessionFactory;

	@Autowired
	clsPOSMasterDao		objPosDao;

	@Autowired
	clsAreaMasterDao	objAreaDao;

	@Autowired
	intfTaxMasterDao		objTaxDao;

	@Autowired
	private clsSetupService objSetupService;

	@SuppressWarnings("finally")

	
	
	
	 public JSONObject funGetItemPricingDtl(String clientCode,String posDate,String posCode)
     {
		JSONObject jObjTableData=new JSONObject();
		List list =null;
		String gAreaCodeForTrans="",sql_ItemDtl;
        try
         {
        	 String sql = "select strAreaCode from tblareamaster where strAreaName='All'";

        	 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
   			 list = query.list();
   			if (list.size() > 0) 
   				gAreaCodeForTrans = (String) list.get(0);
        	 JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing"); 
	    	    if(objSetupParameter.get("gAreaWisePricing").toString().equalsIgnoreCase("N"))
        	
             {
                 sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
                         + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
                         + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
                         + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode "
                         + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
                         + " WHERE  a.strItemCode=b.strItemCode "
                         + " and a.strAreaCode='" + gAreaCodeForTrans + "' "
                         + " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
                         + " and date(dteFromDate)<='" + posDate + "' and date(dteToDate)>='" + posDate + "' "
                         + " ORDER BY b.strItemName ASC";
             }
             else
             {
            	 objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gDirectAreaCode");
            	 
                 sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
                         + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
                         + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
                         + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode "
                         + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
                         + " WHERE a.strAreaCode='" + objSetupParameter.get("gDirectAreaCode").toString() + "' "
                         + "  and a.strItemCode=b.strItemCode "
                         //+ "WHERE (a.strAreaCode='" + clsAreaCode + "') "
                         + " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
                         + " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
                         + " ORDER BY b.strItemName ASC";
             }
            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_ItemDtl);
 			
 			list = query.list();
 			JSONArray jArr=new JSONArray();
 			 if (list.size()>0)
 				{
 					for(int i=0; i<list.size(); i++)
 					{
 						Object[] obj=(Object[])list.get(i);
 					
 						String itemName=obj[1].toString().replace(" ", "&#x00A;");
 						JSONObject objSettle=new JSONObject();
 						objSettle.put("strItemCode",obj[0].toString());
 						objSettle.put("strItemName",itemName);
 						objSettle.put("strTextColor",obj[2].toString());
						objSettle.put("strPriceMonday",obj[3].toString());
						objSettle.put("strPriceTuesday",obj[4].toString());
						objSettle.put("strPriceWednesday",obj[5]);
						
						objSettle.put("strPriceThursday",obj[6].toString());
						objSettle.put("strPriceFriday",obj[7].toString());
						objSettle.put("strPriceSaturday",obj[8].toString());
						objSettle.put("strPriceSunday",obj[9].toString());
						objSettle.put("tmeTimeFrom",obj[10].toString());
						objSettle.put("strAMPMFrom",obj[11].toString());
						objSettle.put("tmeTimeTo",obj[12].toString());
						objSettle.put("strAMPMTo",obj[13].toString());
						objSettle.put("strCostCenterCode",obj[14].toString());
						objSettle.put("strHourlyPricing",obj[15].toString());
						objSettle.put("strSubMenuHeadCode",obj[16].toString());
						objSettle.put("dteFromDate",obj[17].toString());
						objSettle.put("dteToDate",obj[18].toString());
						objSettle.put("strStockInEnable",obj[19].toString());
						objSettle.put("strMenuCode",obj[20].toString());
						
 						jArr.put(objSettle);
 					}
 				}
 			jObjTableData.put("MenuItemPricingDtl",jArr);
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }

         return jObjTableData;
     }
	

	 public JSONObject funGetCustomerHistory(String custCode,String fromDate,String toDate)
    {
	    StringBuilder sbSqlLiveBill=new StringBuilder();
        StringBuilder sbSqlQFileBill=new StringBuilder();
        StringBuilder sbSql=new StringBuilder();
        String strBillNo="";
		JSONObject jObjData=new JSONObject();
		JSONArray jArrBillData=new JSONArray();
		JSONArray jArr=new JSONArray();
		JSONObject jObj=new JSONObject();
		List list =null;
        try
        {
        	sbSqlLiveBill.setLength(0);
            sbSqlQFileBill.setLength(0);
            sbSql.setLength(0);
             sbSqlLiveBill.append("select a.strBillNo,date(a.dteBillDate),DATE_FORMAT(a.dteBillDate,'%H:%i'),a.dblGrandTotal,b.strItemName,"
                     + "b.dblQuantity,b.dblAmount,b.strItemCode\n from tblbillhd a,tblbilldtl b "
                     + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode \n" +                          
                         "and a.strCustomerCode='"+custCode+"' \n" +
                         "and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' ORDER BY date(a.dteBillDate) DESC");
             
             sbSqlQFileBill.append("select a.strBillNo,date(a.dteBillDate),DATE_FORMAT(a.dteBillDate,'%H:%i'),a.dblGrandTotal,b.strItemName,"
                     + "b.dblQuantity,b.dblAmount,b.strItemCode\n from tblqbillhd a,tblqbilldtl b "
                     + "where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode \n" +                           
                         "and a.strCustomerCode='"+custCode+"' \n" +
                         "and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' ORDER BY date(a.dteBillDate) DESC");
            
             Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlLiveBill.toString());
  			 list = query.list();
  			if (list.size() > 0) 
  			{
  				int cnt=0;
  				
  				for(int i=0;i<list.size();i++)
  				{
  					Object[] obj=(Object[])list.get(i);
  					
  					if(!strBillNo.equals(obj[0].toString()))
                    { 
  						jObj.put("strBillNo",obj[0].toString());
  						jObj.put("billDate",obj[1].toString());
  						jObj.put("billTime",obj[2].toString());
  						jObj.put("grandTotal",obj[3]);
  						
  						cnt++;
  						JSONObject jRow=new JSONObject();
  						jRow.put("strItemName", obj[4].toString());
  						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
  						jRow.put("dblAmount", obj[6]);
  						jRow.put("strItemCode", obj[7].toString());
  						jArr.put(jRow);
  						
  						 String sql="select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                 + " from tblbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'";
  						query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
  			  			List modList = query.list();
  			  			if (modList.size() > 0) 
  			  			{
  			  				for(int j=0;j<modList.size();j++)
  			  				{
  			  					Object[] obj1=(Object[])modList.get(j);
  			  					
  			  					jRow=new JSONObject();
  			  					jRow.put("strItemName", obj1[0].toString());
  			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
  			  					jRow.put("dblAmount", obj1[3]);
  			  					jRow.put("strItemCode", obj1[4].toString());
  			  					jArr.put(jRow);
  			  				}
  			  			}
  						
                    }
  					else
  					{
  						JSONObject jRow=new JSONObject();
  						jRow.put("strItemName", obj[4].toString());
  						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
  						jRow.put("dblAmount", obj[6]);
  						jRow.put("strItemCode", obj[7].toString());
  						jArr.put(jRow);
  						
  						 String sql="select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                 + " from tblbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'";
  						query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
  			  			List modList = query.list();
  			  			if (modList.size() > 0) 
  			  			{
  			  				for(int j=0;j<modList.size();j++)
  			  				{
  			  					Object[] obj1=(Object[])modList.get(j);
  			  					
  			  					jRow=new JSONObject();
  			  					jRow.put("strItemName", obj1[0].toString());
  			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
  			  					jRow.put("dblAmount", obj1[3]);
  			  					jRow.put("strItemCode", obj1[4].toString());
  			  					jArr.put(jRow);
  			  				}
  			  			}
  					}
  					if((!strBillNo.equals(obj[0].toString())) && cnt>0)
                    {
  			  			jObj.put("billItemDtl", jArr);
  			  		jArrBillData.put(jObj);
  			  			jArr=new JSONArray();
  			  			jObj=new JSONObject();
                    }
  			  		strBillNo=obj[0].toString();
  				}
  			}
          
  			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlQFileBill.toString());
 			 list = query.list();
 			if (list.size() > 0) 
 			{
 				boolean flag=false;
 				String firstBill="";
 				for(int i=0;i<list.size();i++)
 				{
 					Object[] obj=(Object[])list.get(i);
 					if(strBillNo.equals(""))
							firstBill=obj[0].toString();
 					if(!obj[0].toString().equals(firstBill)&& !flag)
                    {
  			  			jObj.put("billItemDtl", jArr);
  			  			jArrBillData.put(jObj);
  			  			jArr=new JSONArray();
  			  			jObj=new JSONObject();
  			  			flag=true;
                    }
 					else if((!strBillNo.equals(obj[0].toString())) && flag)
                    {
  			  			jObj.put("billItemDtl", jArr);
  			  			jArrBillData.put(jObj);
  			  			jArr=new JSONArray();
  			  			jObj=new JSONObject();
                    }
 					
 					if(!strBillNo.equals(obj[0].toString()))
                   { 
 						
 						
 						jObj.put("strBillNo",obj[0].toString());
 						jObj.put("billDate",obj[1].toString());
 						jObj.put("billTime",obj[2].toString());
 						jObj.put("grandTotal",obj[3]);
 						
 						
 						JSONObject jRow=new JSONObject();
 						jRow.put("strItemName", obj[4].toString());
 						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
 						jRow.put("dblAmount", obj[6]);
 						jRow.put("strItemCode", obj[7].toString());
 						jArr.put(jRow);
 						
 						 String sql="select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                 + " from tblqbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'";
 						query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
 			  			List modList = query.list();
 			  			if (modList.size() > 0) 
 			  			{
 			  				for(int j=0;j<modList.size();j++)
 			  				{
 			  					Object[] obj1=(Object[])modList.get(j);
 			  					
 			  					jRow=new JSONObject();
 			  					jRow.put("strItemName", obj1[0].toString());
 			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
 			  					jRow.put("dblAmount", obj1[3]);
 			  					jRow.put("strItemCode", obj1[4].toString());
 			  					jArr.put(jRow);
 			  				}
 			  			}
 						
                   }
 					else
 					{
 						JSONObject jRow=new JSONObject();
 						jRow.put("strItemName", obj[4].toString());
 						jRow.put("dblQuantity", obj[5].toString().split("\\.")[0]);
 						jRow.put("dblAmount", obj[6]);
 						jRow.put("strItemCode", obj[7].toString());
 						jArr.put(jRow);
 						
 						 String sql="select a.strModifierName,a.dblRate,a.dblQuantity,a.dblAmount,a.strItemCode"
                                 + " from tblqbillmodifierdtl a where a.strBillNo='"+obj[0].toString()+"' and a.strItemCode like '"+obj[7].toString()+"%'";
 						query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
 			  			List modList = query.list();
 			  			if (modList.size() > 0) 
 			  			{
 			  				for(int j=0;j<modList.size();j++)
 			  				{
 			  					Object[] obj1=(Object[])modList.get(j);
 			  					
 			  					jRow=new JSONObject();
 			  					jRow.put("strItemName", obj1[0].toString());
 			  					jRow.put("dblQuantity", obj1[2].toString().split("\\.")[0]);
 			  					jRow.put("dblAmount", obj1[3]);
 			  					jRow.put("strItemCode", obj1[4].toString());
 			  					jArr.put(jRow);
 			  				}
 			  			}
 			  		
 			  		
 					}
 					
 					
  					strBillNo=obj[0].toString();
 					
 					
						
 				}
 			}
			jObjData.put("CustomerHistory",jArrBillData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return jObjData;
    }
}
