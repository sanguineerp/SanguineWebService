package com.apos.dao;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("clsStockInDao")

@Transactional(value = "webPOSTransactionManager")
public class clsStockInOutDao {
	
@Autowired
private SessionFactory webPOSSessionFactory;


public String funSaveStockIn(JSONObject jObj)
{
	String reasonCode="",clientCode="",posCode="",userCode="",purchaseBillDate="",result="",purchaseBillNo="",posDate="",stockInCode="";
	List list =null;
	
	try
	{
		reasonCode = jObj.getString("ReasonCode");
		clientCode = jObj.getString("ClientCode");
		posCode = jObj.getString("POSCode");
		userCode = jObj.getString("UserCode");
		String []billDate=jObj.getString("PurchaseBillDate").toString().split("-");
		purchaseBillDate=billDate[2]+"-"+billDate[1]+"-"+billDate[0]+" "+"00:00:00";
		purchaseBillNo = jObj.getString("PurchaseBillNo");
		posDate = jObj.getString("POSDate");
		stockInCode= jObj.getString("StockInCode");
		Date objDate=new Date();
		String date=(objDate.getYear()+1900)+"-"+(objDate.getMonth()+1)+"-"+objDate.getDate()
                    +" "+objDate.getHours()+":"+objDate.getMinutes()+":"+objDate.getSeconds();
		JSONArray mJsonArrayItems=jObj.getJSONArray("ItemList");
		
	     
		 if(mJsonArrayItems.length()>0)
		 {
			   if(stockInCode.isEmpty())
				{
				   stockInCode=funGenerateStockInCode();
				}
			    String sqlInsertStockInDtl="";
			      
				sqlInsertStockInDtl= " insert into tblstkindtl (strStkInCode,strItemCode,dblQuantity,dblPurchaseRate,dblAmount,strClientCode,strDataPostFlag)"
                                   + " values";
			        
		        for (int i = 0; i < mJsonArrayItems.length(); i++)
				{
				    JSONObject mJsonObject = (JSONObject) mJsonArrayItems.get(i);
				    sqlInsertStockInDtl+=" ('"+stockInCode+"','"+mJsonObject.getString("ItemCode")+"','"+mJsonObject.getString("Qty")+"',"
			                            +" '"+mJsonObject.getString("PurchaseRate")+"','"+mJsonObject.getString("Amount")+"','"+clientCode+"','N'),";
				} 
		        
		        
		        String sql="delete from tblstkindtl where strStkInCode='"+stockInCode+"' and strClientCode='"+clientCode+"' ";
		        Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    			query.executeUpdate();

		        StringBuilder sb = new StringBuilder(sqlInsertStockInDtl);
		        int index = sb.lastIndexOf(",");
		        sqlInsertStockInDtl = sb.delete(index, sb.length()).toString();
		        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertStockInDtl);
    			query.executeUpdate(); 
    			
    			sql="delete from tblstkinhd where strStkInCode='"+stockInCode+"' and strClientCode='"+clientCode+"'; ";
    			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     			query.executeUpdate();

    	        sql="insert into tblstkinhd (strStkInCode,strPOSCode,dteStkInDate,strReasonCode,strPurchaseBillNo"
                  + ",dtePurchaseBillDate,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode"
                  + ",strInvoiceCode)"
                  + "values('" +stockInCode + "','" + posCode + "','"+posDate+"','"+reasonCode
	              + "','"+purchaseBillNo+"','"+purchaseBillDate+"','"+userCode+"','"+userCode+"'"
	              + ",'"+ date+ "','" +date + "','"+clientCode+"','"+purchaseBillNo+"')";
    	        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     			query.executeUpdate();
     			result=stockInCode;
		 }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    result="error";
	}
	
	return result; 
}





public String funSaveStockOut(JSONObject jObj)
{
	String reasonCode="",clientCode="",posCode="",userCode="",purchaseBillDate="",result="",purchaseBillNo="",posDate="",stockOutCode="";
	List list =null;
	
	try
	{
		reasonCode = jObj.getString("ReasonCode");
		clientCode = jObj.getString("ClientCode");
		posCode = jObj.getString("POSCode");
		userCode = jObj.getString("UserCode");
		String []billDate=jObj.getString("PurchaseBillDate").toString().split("-");
		purchaseBillDate=billDate[2]+"-"+billDate[1]+"-"+billDate[0]+" "+"00:00:00";
		purchaseBillNo = jObj.getString("PurchaseBillNo");
		stockOutCode= jObj.getString("StockOutCode");
		posDate = jObj.getString("POSDate");
		Date objDate=new Date();
		String date=(objDate.getYear()+1900)+"-"+(objDate.getMonth()+1)+"-"+objDate.getDate()
                    +" "+objDate.getHours()+":"+objDate.getMinutes()+":"+objDate.getSeconds();
		JSONArray mJsonArrayItems=jObj.getJSONArray("ItemList");
		
	     
		 if(mJsonArrayItems.length()>0)
		 {
			   if(stockOutCode.isEmpty())
				{
				   stockOutCode=funGenerateStockInCode();
				}
			    String sqlInsertStockOutDtl="";
			      
				sqlInsertStockOutDtl= " insert into tblstkoutdtl (strStkOutCode,strItemCode,dblQuantity,dblPurchaseRate,dblAmount,strClientCode,strDataPostFlag)"
                                    + " values";
			        
		        for (int i = 0; i < mJsonArrayItems.length(); i++)
				{
				    JSONObject mJsonObject = (JSONObject) mJsonArrayItems.get(i);
				    sqlInsertStockOutDtl+=" ('"+stockOutCode+"','"+mJsonObject.getString("ItemCode")+"','"+mJsonObject.getString("Qty")+"',"
			                            +" '"+mJsonObject.getString("PurchaseRate")+"','"+mJsonObject.getString("Amount")+"','"+clientCode+"','N'),";
				} 
		        
		        
		        String sql="delete from tblstkoutdtl where strStkOutCode='"+stockOutCode+"' and strClientCode='"+clientCode+"' ";
		        Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    			query.executeUpdate();

		        StringBuilder sb = new StringBuilder(sqlInsertStockOutDtl);
		        int index = sb.lastIndexOf(",");
		        sqlInsertStockOutDtl = sb.delete(index, sb.length()).toString();
		        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertStockOutDtl);
    			query.executeUpdate(); 
    			
    			sql="delete from tblstkouthd where strStkOutCode='"+stockOutCode+"' and strClientCode='"+clientCode+"'; ";
    			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     			query.executeUpdate();

    	        sql="insert into tblstkouthd (strStkOutCode,strPOSCode,dteStkOutDate"
                   + ",strReasonCode,strPurchaseBillNo,dtePurchaseBillDate,strUserCreated,strUserEdited"
                   + ",dteDateCreated,dteDateEdited,strClientCode)"
                   + " values('" +stockOutCode + "','" + posCode + "','"+posDate+"','"+reasonCode
	              + "','"+purchaseBillNo+"','"+purchaseBillDate+"','"+userCode+"','"+userCode+"'"
	              + ",'"+ date+ "','" +date + "','"+clientCode+"')";
    	        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     			query.executeUpdate();
     			result=stockOutCode;
		 }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    result="error";
	}
	
	return result; 
}





   private String funGenerateStockInCode() 
	{
	    String stockInCode="";
		int lastNo = 0;
		List list =null;
		try 
		{
		   String sql = "select strTransactionType,dblLastNo from tblinternal where strTransactionType='stockInNo'";
		   Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			JSONArray jArrTableData=new JSONArray();
			if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
					lastNo =Integer.parseInt((Array.get(obj, 1).toString())) ;
		            lastNo = lastNo + 1;
		            stockInCode = "SI" + String.format("%07d", lastNo);
		            sql=" update tblinternal set dblLastNo='" + lastNo + "' "
		              +" where strTransactionType='stockinNo'";
		                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		    			query.executeUpdate();       
		        }
			}
				
		    } catch (Exception e) 
		    {
		        e.printStackTrace();
		    }
		    return stockInCode;
	}
   
   
   
   private String funGenerateStockOutCode() 
	{
	    String stockOutCode="";
		int lastNo = 0;
		List list =null;
		try 
		{
		   String sql = "select strTransactionType,dblLastNo from tblinternal where strTransactionType='stockOutNo'";
		   Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			JSONArray jArrTableData=new JSONArray();
			if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
					lastNo =Integer.parseInt((Array.get(obj, 1).toString())) ;
		            lastNo = lastNo + 1;
		            stockOutCode = "SO" + String.format("%07d", lastNo);
		            sql=" update tblinternal set dblLastNo='" + lastNo + "' "
		              +" where strTransactionType='stockOutNo'";
		                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		    			query.executeUpdate();       
		        }
			}
				
		    } catch (Exception e) 
		    {
		        e.printStackTrace();
		    }
		    return stockOutCode;
	  }

   
   public JSONObject funGetStockInData(String masterCode) throws Exception
	{
		JSONArray jArrData = new JSONArray();
		JSONObject jObjSearchData = new JSONObject();
		SQLQuery sqlQuery=webPOSSessionFactory.getCurrentSession().createSQLQuery("select a.strStkInCode,c.strItemName,a.strItemCode,a.dblQuantity,"
				+ " a.dblPurchaseRate,a.dblAmount,b.strPurchaseBillNo,date(b.dtePurchaseBillDate),b.strReasonCode,d.strReasonName,date(b.dteStkInDate)  "
				+ " from tblstkindtl a,tblstkinhd b,tblitemmaster c ,tblreasonmaster d "
				+ " where a.strStkInCode='"+masterCode+"' and a.strStkInCode=b.strStkInCode "
				+ " and b.strReasonCode=d.strReasonCode and a.strItemCode=c.strItemCode "); 
	
		
		List list=sqlQuery.list();					
		
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object[] objArr = (Object[]) list.get(cnt);
		    
		    JSONArray jArrDataRow = new JSONArray();
		    
		    jArrDataRow.put(objArr[0].toString());//stockIn code
		    jArrDataRow.put(objArr[1].toString());//Item Name
		    jArrDataRow.put(objArr[2].toString());//Item Code
		    jArrDataRow.put(objArr[3].toString());//Qty
		    jArrDataRow.put(objArr[4].toString());//Purchase Rate
		    jArrDataRow.put(objArr[5].toString());//Amount
		    jArrDataRow.put(objArr[6].toString());//Purchase Bill No
		    jArrDataRow.put(objArr[7].toString());//Purchase Bill Date
		    jArrDataRow.put(objArr[8].toString());//Reason Code
		    jArrDataRow.put(objArr[9].toString());//Reason Name
		    jArrDataRow.put(objArr[10].toString());//StockIn Date
		    
		    jArrData.put(jArrDataRow);
		}
		jObjSearchData.put("StockIn", jArrData);
		
		return jObjSearchData; 
	} 

   
   public JSONObject funGetStockOutData(String masterCode) throws Exception
	{
		JSONArray jArrData = new JSONArray();
		JSONObject jObjSearchData = new JSONObject();
		SQLQuery sqlQuery=webPOSSessionFactory.getCurrentSession().createSQLQuery(" select a.strStkOutCode,c.strItemName,a.strItemCode,a.dblQuantity,a.dblPurchaseRate,"
				+ " a.dblAmount,b.strPurchaseBillNo,date(b.dtePurchaseBillDate),b.strReasonCode,d.strReasonName,date(b.dteStkOutDate) "
				+ " from tblstkoutdtl a,tblstkouthd b ,tblitemmaster c,tblreasonmaster d "
				+ " where a.strStkOutCode='"+masterCode+"' and a.strStkOutCode=b.strStkOutCode "
				+ " and b.strReasonCode=d.strReasonCode and a.strItemCode=c.strItemCode "); 
		
	
		
		List list=sqlQuery.list();					
		
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object[] objArr = (Object[]) list.get(cnt);
		    
		    JSONArray jArrDataRow = new JSONArray();
		    
		    jArrDataRow.put(objArr[0].toString());//stockOut code
		    jArrDataRow.put(objArr[1].toString());//Item Name
		    jArrDataRow.put(objArr[2].toString());//Item Code
		    jArrDataRow.put(objArr[3].toString());//Qty
		    jArrDataRow.put(objArr[4].toString());//Purchase Rate
		    jArrDataRow.put(objArr[5].toString());//Amount
		    jArrDataRow.put(objArr[6].toString());//Purchase Bill No
		    jArrDataRow.put(objArr[7].toString());//Purchase Bill Date
		    jArrDataRow.put(objArr[8].toString());//Reason Code
		    jArrDataRow.put(objArr[9].toString());//Reason Name
		    jArrDataRow.put(objArr[10].toString());//Stock Out Date
		    
		    jArrData.put(jArrDataRow);
		}
		jObjSearchData.put("StockOut", jArrData);
		
		return jObjSearchData; 
	} 


}
