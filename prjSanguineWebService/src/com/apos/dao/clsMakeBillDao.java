package com.apos.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.bean.clsTaxCalculationBean;
import com.apos.controller.clsUtilityController;
import com.apos.service.clsSetupService;
import com.apos.util.clsGlobalFunctions;

@Repository("clsMakeBillDao")

@Transactional(value = "webPOSTransactionManager")
public class clsMakeBillDao {
	@Autowired
	private SessionFactory webPOSSessionFactory;
	

	@Autowired
	private clsSetupService objSetupService;

	@Autowired
	private clsUtilityController objUtility;
	
	@SuppressWarnings("finally")
	public JSONObject funLoadTableDtl(String clientCode,String posCode)
	{
		List list =null;
		String    sql;
	   Map<String, Integer> hmTableSeq= new HashMap<String, Integer>();
		JSONObject jObjTableData=new JSONObject();
		try{
			sql = " select a.strTableNo from tblitemrtemp a,tbltablemaster b  "
                    + " where a.strTableNo=b.strTableNo "
                    + " and a.strPosCode='" + posCode + "' and a.strNCKOTYN='N' and a.strTableNo!=null"
                    + " group by a.strTableNo ";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			list = query.list();
			if (list.size()>0)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj=(Object[])list.get(i);
					  String sqlUpdate = " update tbltablemaster set strStatus='Occupied' "
		                        + " where strTableNo='" + obj[0].toString() + "' ";   
					  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlUpdate);
					  query.executeUpdate();
			    }
			}
			String areaName="";
			  String sqlArea = "select strAreaCode from tblareamaster "
	                    + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
	                    + " and strAreaName='All' "
	                    + " order by strAreaCode";
			  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlArea);
				list = query.list();
				if (list.size()>0)
				{
					areaName="All";
				}
			  jObjTableData.put("areaName", areaName);
			JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN"); 
    	    if(objSetupParameter.get("gCMSIntegrationYN").toString().equalsIgnoreCase("Y"))
    	    {
    	    	objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gTreatMemberAsTable"); 
        	    if(objSetupParameter.get("gTreatMemberAsTable").toString().equalsIgnoreCase("Y"))
        	    {
    	                  sql = "select strTableNo,strTableName from tbltablemaster "
    	                          + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
    	                          + " and strOperational='Y'"
    	                          + " AND strStatus='Occupied' "
    	                          + " order by strTableName";
    	         }
    	              else
    	              {
    	                  sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
    	                          + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
    	                          + " and strOperational='Y' "
    	                          + " AND strStatus='Occupied' "
    	                          + " order by intSequence";
    	              }
    	    }
    	    else
            {
                sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
                        + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
                        + " and strOperational='Y' "
                        + " AND strStatus='Occupied' "
                        + " order by intSequence";
            }
			    
			 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
			
			 if (list.size()>0)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						
						   hmTableSeq.put(obj[0].toString() + "!" + obj[1].toString(), (int)obj[2]);
					        
				    }
				}
			 clsGlobalFunctions objGlobal=new clsGlobalFunctions();
			  hmTableSeq = objGlobal.funSortMapOnValues(hmTableSeq);
			   Object[] arrObjTables = hmTableSeq.entrySet().toArray();
			   jArrData=new JSONArray();
	            for (int cntTable = 0; cntTable < hmTableSeq.size(); cntTable++)
	            {
	                
	                if (cntTable == hmTableSeq.size())
	                {
	                    break;
	                }
	                String tblInfo = arrObjTables[cntTable].toString().split("=")[0];
	                String tblNo = tblInfo.split("!")[0];
	                String tableName=tblInfo.split("!")[1];
	               sql = "select strTableNo,strStatus,intPaxNo from tbltablemaster "
	                        + " where strTableNo='" + tblNo + "' "
	                        + " and strOperational='Y' "
	                        + " order by intSequence";
	               
	               
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    			
	    			list = query.list();
	    			 
	    			
	    			 if (list.size()>0)
	    				{
	    					
	    						Object[] obj=(Object[])list.get(0);
	    					
	    						JSONObject jobj=new JSONObject();
	    						   
	    						jobj.put("strTableName",tableName);
	    						jobj.put("strTableNo", obj[0].toString());
	    						jobj.put("strStatus", obj[1].toString());
	    						jobj.put("intPaxNo", obj[2].toString());
	    						jArrData.put(jobj);
	    				}
	            }
	            jObjTableData.put("tableDtl",jArrData);
	             
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				return jObjTableData;
			}
	}

	@SuppressWarnings("finally")
	public JSONObject funLoadTableForArea(String areaCode, String posCode) {
		List list =null;
		String    sql,areaName="";
	   Map<String, Integer> hmTableSeq= new HashMap<String, Integer>();
		JSONObject jObjTableData=new JSONObject();
		try{
		sql = "select strAreaName from tblareamaster where strAreaCode='" + areaCode + "'";
		Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		list = query.list();
		if (list.size()>0)
		{
			areaName=(String)list.get(0);
		}
		if(areaName.equalsIgnoreCase("All"))
	    {
			 sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
                     + " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
                     + " and strStatus='Occupied' and strOperational='Y' "
                     + " order by intSequence ";
	    }
	    else
        {
	    	 sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
                     + " where strAreaCode='" + areaCode + "' "
                     + " and (strPOSCode='" + posCode + "' or strPOSCode='All') "
                     + " and strStatus='Occupied' and strOperational='Y' "
                     + " order by intSequence ";
        }
		    
		 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		
		list = query.list();
		 JSONArray jArrData=new JSONArray();
		
		 if (list.size()>0)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object[] obj=(Object[])list.get(i);
				
					
					   hmTableSeq.put(obj[0].toString() + "!" + obj[1].toString(), (int)obj[2]);
				        
			    }
			}
		 clsGlobalFunctions objGlobal=new clsGlobalFunctions();
		  hmTableSeq = objGlobal.funSortMapOnValues(hmTableSeq);
		   Object[] arrObjTables = hmTableSeq.entrySet().toArray();
		   jArrData=new JSONArray();
            for (int cntTable = 0; cntTable < hmTableSeq.size(); cntTable++)
            {
                //System.out.println("Counter="+cntTable+"\tStart="+startIndex+"\tTotal Size="+totalSize);
                if (cntTable == hmTableSeq.size())
                {
                    break;
                }
                String tblInfo = arrObjTables[cntTable].toString().split("=")[0];
                String tblNo = tblInfo.split("!")[0];
                String tableName=tblInfo.split("!")[1];
               sql = "select strTableNo,strStatus,intPaxNo from tbltablemaster "
                        + " where strTableNo='" + tblNo + "' "
                        + " and strOperational='Y' "
                        + " order by intSequence";
               
               
                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    			
    			list = query.list();
    			 
    			
    			 if (list.size()>0)
    				{
    					
    						Object[] obj=(Object[])list.get(0);
    					
    						JSONObject jobj=new JSONObject();
    						   
    						jobj.put("strTableName",tableName);
    						jobj.put("strTableNo", obj[0].toString());
    						jobj.put("strStatus", obj[1].toString());
    						jobj.put("intPaxNo", obj[2].toString());
    						jArrData.put(jobj);
    				}
            }
            jObjTableData.put("tableDtl",jArrData);
             
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jObjTableData;
		}
	}
	
	@SuppressWarnings("finally")
	public JSONObject funFillItemTableDtl(String tableNo,String posCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
			
			String sql="select strItemName,sum(dblItemQuantity),sum(dblAmount),strWaiterNo,intPaxNo "
                    + " from tblitemrtemp "
                    + " where strPosCode='" + posCode + "' "
                    + " and strTableNo='" + tableNo + "' "
                    + " and strNCKOTYN='N' "
                    + " group by strItemCode "
                    + " order by strSerialNo ";
				Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
					
				 list = query.list();
				 if (list.size()>0)
				 {
					 
					 JSONArray jArrData=new JSONArray();
							
					String strWaiterNo="";
					String intPaxNo="0";
					double total=0;
					 for(int i=0; i<list.size(); i++)
					 {
						 Object[] obj=(Object[])list.get(i);
						 JSONObject objSettle=new JSONObject();
						
						 objSettle.put("strItemName",obj[0].toString());
							 objSettle.put("dblItemQuantity",obj[1]);
							 objSettle.put("dblAmount",obj[2]);
							 
							 BigDecimal dblAmount=(BigDecimal)obj[2];
							 total=total+dblAmount.doubleValue();
							 strWaiterNo=obj[3].toString();
							 intPaxNo=obj[4].toString();
							
						
							 
			                 jArrData.put(objSettle);
						 
					 }
					 if ("null".equalsIgnoreCase(strWaiterNo))
		                {
						 jObjTableData.put("strWaiterName","");
		                }
					 else
					 {
						 query = webPOSSessionFactory.getCurrentSession().createSQLQuery("select strWShortName from tblwaitermaster where strWaiterNo='" + strWaiterNo + "'");
						 list = query.list();
						 if (list.size()>0)
						 {
							 jObjTableData.put("strWaiterName",list.get(0).toString());
						 }
					 }
					 jObjTableData.put("itemDtl",jArrData);
					 jObjTableData.put("intPaxNo",intPaxNo);
					 jObjTableData.put("total",total);
						
				 }		
				
			
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	
}
