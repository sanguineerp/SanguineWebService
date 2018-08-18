package com.apos.dao;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.controller.clsUtilityController;
import com.webservice.util.clsUtilityFunctions;


@Repository("clsVoidStockDao")
@Transactional(value = "webPOSTransactionManager")
public class clsVoidStockDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired
	private clsUtilityFunctions objUtility;
	
	public JSONObject funLoadStockList(String posCode,String transType)
	{
		JSONObject jObjReturn=new JSONObject();
		JSONArray jArr=new JSONArray();
		String sql="";
	
		try
        {
			if(transType.equalsIgnoreCase("Stock In"))
			{
            sql = "select a.strStkInCode, Date(a.dteStkInDate),a.strUserCreated,b.strReasonName "
                    + "from tblstkinhd a, tblreasonmaster b "
                    + "where a.strPosCode='"+posCode+"'"
                    + " and a.strReasonCode=b.strReasonCode and b.strStkIn='Y' "
                    + "ORDER BY strStkInCode ASC";
			}
			if(transType.equalsIgnoreCase("Stock Out"))
			{
				 sql = "select a.strStkOutCode, Date(a.dteStkOutDate),a.strUserCreated,b.strReasonName "
		                    + "from tblstkouthd a, tblreasonmaster b "
		                    + "where a.strPosCode='"+posCode+"' and a.strReasonCode=b.strReasonCode "
		                    + "ORDER BY strStkOutCode ASC";
		           
			}
			if(transType.equalsIgnoreCase("PS Posting"))
			{
				 sql ="select a.strPSPCode,a.strStkInCode,a.strStkOutCode,a.strBillNo,a.dblStkInAmt,a.dblSaleAmt "
		                    + "from tblpsphd a where a.strPosCode='"+posCode+"' ORDER BY strPSPCode ASC";
		           
				 Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
			  	    List listSql1= querySql1.list();
			  	    if(listSql1.size()>0)
			  	      {
			    		for(int j=0 ;j<listSql1.size();j++ )
			  	    	{
			    		JSONObject jObj=new JSONObject();
			  	    	Object[] obj1 = (Object[]) listSql1.get(j);
		            
			  	    	jObj.put("strPSPCode", obj1[0].toString());
			  	    	jObj.put("strStkInCode", obj1[1].toString());
			  	    	jObj.put("strStkOutCode", obj1[2].toString());
			  	    	jObj.put("strBillNo", obj1[3].toString());
			  	    	jObj.put("dblStkInAmt", obj1[4].toString());
			  	    	jObj.put("dblSaleAmt", obj1[5].toString());
			  	    	
			  	    	jArr.put(jObj);
			  	   
			  	    	}
			  	    }
			}
			else
			{
            Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
	  	    List listSql1= querySql1.list();
	  	    if(listSql1.size()>0)
	  	      {
	    		for(int j=0 ;j<listSql1.size();j++ )
	  	    	{
	    		JSONObject jObj=new JSONObject();
	  	    	Object[] obj1 = (Object[]) listSql1.get(j);
            
	  	    	jObj.put("strStkCode", obj1[0].toString());
	  	    	jObj.put("dteStkDate", obj1[1].toString());
	  	    	jObj.put("strUserCreated", obj1[2].toString());
	  	    	jObj.put("strReasonName", obj1[3].toString());
	  	    	
	  	    	jArr.put(jObj);
	  	   
	  	    	}
	  	      }
	  	  
			} 
	  	jObjReturn.put("stkList",jArr);
        }
        catch (Exception e)
        {
         
            e.printStackTrace();
        }
		
		return jObjReturn;
	}
	
	public JSONObject funLoadStockDtlData(String stockCode,String transType)
	{
		JSONObject jObjReturn=new JSONObject();
		JSONArray jArr=new JSONArray();
		String sql="";
	
		try
        {
			if(transType.equalsIgnoreCase("Stock In"))
			{
				sql = "select b.strItemName,a.dblQuantity,a.dblAmount from tblstkindtl a,tblitemmaster b where strStkInCode='" + stockCode + "' and a.strItemCode=b.strItemCode";;
			}
			if(transType.equalsIgnoreCase("Stock Out"))
			{
				 sql = "select b.strItemName,a.dblQuantity,a.dblAmount from tblstkoutdtl a,tblitemmaster b where strStkOutCode='" + stockCode + "' and a.strItemCode=b.strItemCode";
			}
			if(transType.equalsIgnoreCase("PS Posting"))
			{
				 sql ="select b.strItemName,a.dblCompStk,a.dblPhyStk,a.dblVariance,a.dblVairanceAmt from tblpspdtl a,tblitemmaster b where strPSPCode='" + stockCode + "' and a.strItemCode=b.strItemCode";
		           
				 Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
			  	    List listSql1= querySql1.list();
			  	    if(listSql1.size()>0)
			  	      {
			    		for(int j=0 ;j<listSql1.size();j++ )
			  	    	{
			    		JSONObject jObj=new JSONObject();
			  	    	Object[] obj1 = (Object[]) listSql1.get(j);
		            
			  	    	jObj.put("strItemName", obj1[0].toString());
			  	    	jObj.put("dblCompStk", obj1[1].toString());
			  	    	jObj.put("dblPhyStk", obj1[2].toString());
			  	    	jObj.put("dblVariance", obj1[3].toString());
			  	    	jObj.put("dblVairanceAmt", obj1[4].toString());
			  	    	
			  	    	jArr.put(jObj);
			  	   
			  	    	}
			  	    }
			}
			else
			{
            Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
	  	    List listSql1= querySql1.list();
	  	    if(listSql1.size()>0)
	  	      {
	    		for(int j=0 ;j<listSql1.size();j++ )
	  	    	{
	    		JSONObject jObj=new JSONObject();
	  	    	Object[] obj1 = (Object[]) listSql1.get(j);
            
	  	    	jObj.put("strItemName", obj1[0].toString());
	  	    	jObj.put("dblQuantity", obj1[1].toString());
	  	    	jObj.put("dblAmount", obj1[2].toString());
	  	    	
	  	    	jArr.put(jObj);
	  	   
	  	    	}
	  	      }
	  	  
			} 
	  	jObjReturn.put("stkDtl",jArr);
        }
        catch (Exception e)
        {
         
            e.printStackTrace();
        }
		
		return jObjReturn;
	}
	
	 public void funVoidStockIn(String voidResaonCode,String transType,String stockCode,String userCode)
	    {
	        try
	        {
	             
	           int exce=0,del=0;
	           String sqlQuery="select strAuditing from tbluserdtl where strUserCode='"+userCode+"' and strFormName='VoidStock'";
	           String voidStockDate=objUtility.funGetCurrentDateTime("yyyy-MM-dd");
	           String selectQuery="select strStkInCode,strPOSCode,dteStkInDate,strReasonCode,strPurchaseBillNo,dtePurchaseBillDate,strUserCreated from tblstkinhd where strStkInCode='"+stockCode+"'";
	           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery( selectQuery);
		  	   List list= query.list();
		  	   Object[] obj = (Object[]) list.get(0);
	           String insertQuery="insert into tblvoidstockhd(strStockCode,strPOSCode,dteStkDate,strReasonCode,"
	                + "strPurchaseBillNo,dtePurchaseBillDate,dteVoidedDate,strTransType,strVoidReasonCode,strUserCreated)"
	                + " values('"+obj[0].toString()+"','"+obj[1].toString()+"','"+obj[2].toString()+"',"
	                + "'"+obj[3].toString()+"','"+obj[4].toString()+"','"+obj[5].toString()+"','"+voidStockDate+"','"+transType+"','"+voidResaonCode+"','"+obj[6].toString()+"')";
	           query = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery);
		  	   list= query.list();
	           int exc=0;
	           if(list.size()>0)
	           {
	            	String strAuditing = (String) list.get(0);
	                if(Boolean.parseBoolean(strAuditing))
	                {
	                   	query = webPOSSessionFactory.getCurrentSession().createSQLQuery( insertQuery);
	                   	exc=query.executeUpdate();
	                }
	           }
	            
	            if(exc>0)
	            {
	                selectQuery="select strItemCode,dblQuantity,dblPurchaseRate,dblAmount from tblstkindtl where strStkInCode='"+stockCode+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery( selectQuery);
	                list= query.list();
	                if(list.size()>0)
                    {
	                for(int i=0 ;i<list.size();i++ )
	                {
	                	Object[] obj1 = (Object[]) list.get(i);
	                    String ItemCode = obj1[0].toString();
	                    String Qty = obj1[1].toString();
	                    String dblPurchaseRate=obj1[2].toString();
	                    String amount = obj1[3].toString();
	                    insertQuery="insert into tblvoidstockdtl(strStockCode,strItemCode,dblQuantity,dblPurchaseRate,dblAmount)"
	                        + " values('"+stockCode+"','"+ItemCode+"','"+Qty+"','"+dblPurchaseRate+"','"+amount+"')";
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery);
	   		  	     	list= query.list();
	   		  	     	if(list.size()>0)
	   	                {
	   		  	     	String strAuditing = (String) list.get(0);
                        if(Boolean.parseBoolean(strAuditing))
                        {
                        	query = webPOSSessionFactory.getCurrentSession().createSQLQuery( insertQuery);
                        	exc=query.executeUpdate();
                        }
	                    }
	                }
                    }
	                if(exce>0)
	                {
	                    selectQuery="select Count(*) from tblstocktaxdtl where strTransactionId='"+stockCode+"'";
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery( selectQuery);
	                    list= query.list();
	                    int rowcnt=(int)list.get(0);
	                    if(rowcnt>0)
	                    {
	                        selectQuery="select strTransactionId,strTaxCode,dblTaxableAmt,dblTaxAmt,strClientCode from tblstocktaxdtl where strTransactionId='"+stockCode+"'";
	                        list= query.list();
	    	                if(list.size()>0)
	                        {
	    	                for(int i=0 ;i<list.size();i++ )
	    	                {
	    	                	Object[] obj1 = (Object[]) list.get(i);
	                            insertQuery="insert into tblvoidstocktaxdtl(strTransactionId,strTaxCode,dblTaxableAmt,dblTaxAmt,strClientCode)"
	                                + " values('"+obj1[0].toString() +"','"+obj1[1].toString()+"','"+obj1[2].toString() +"','"+obj1[3].toString()+"','"+obj1[4].toString() +"')";
	                            query = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery);
	    	   		  	     	list= query.list();
	    	   		  	     	if(list.size()>0)
	    	   	                {
	    	   		  	     	String strAuditing = (String) list.get(0);
	                            if(Boolean.parseBoolean(strAuditing))
	                            {
	                            	query = webPOSSessionFactory.getCurrentSession().createSQLQuery( insertQuery);
	                            	exc=query.executeUpdate();
	                            }
	    	                    }
	                        }
	                        }
	                    }
	                    else
	                    {
	                        del=1;
	                    }
	                }
	                if(del>0)
	                {
	                	query=webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblstkinhd where strStkInCode='" + stockCode + "'");
	                	query.executeUpdate();
	                	query=webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblstkindtl where strStkInCode='" + stockCode + "'");
	                	query.executeUpdate();
	                }                       
	            }
	        }
	        catch(Exception e)
	        {
	           e.printStackTrace();
	                   
	        }
	        
	    }
}
