package com.apos.dao;

import java.lang.reflect.Array;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("clsMoveKOTDao")

@Transactional(value = "webPOSTransactionManager")
public class clsMoveKOTDao {
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@SuppressWarnings("finally")
	public JSONObject funGetOpenKOTDtl(String tableNo, String POSCode, String loginPosCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String    sql="select distinct(strKOTNo),strTableNo from tblitemrtemp ";
            if(!tableNo.equals("All"))
            {
                if(null==POSCode || POSCode.equalsIgnoreCase("All"))
                {
                    sql+= " where strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"' and strNCKOTYN='N' ";
                }
                else
                {
                    sql+= " where strTableNo='"+tableNo+"' and strPOSCode='"+POSCode+"' and strNCKOTYN='N'  ";
                }                
            }
            else
            {        
                if(null==POSCode || POSCode.equalsIgnoreCase("All"))
                {
                    sql+=" where strPOSCode='"+loginPosCode+"' and strNCKOTYN='N' ";
                }
                else
                {
                    sql+=" where strPOSCode='"+POSCode+"' and strNCKOTYN='N' ";
                }                
            }
			
		
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
			 JSONArray jArrKOTData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					
				
						JSONObject objSettle=new JSONObject();
						objSettle.put("KOTNo",Array.get(obj, 0));
						objSettle.put("TableNo",Array.get(obj, 1));
						jArrKOTData.put(Array.get(obj, 0));
						jArrData.put(objSettle);
					}
		           	jObjTableData.put("KOTDtl", jArrData);
		         	jObjTableData.put("KOTList", jArrKOTData);
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
	
	@SuppressWarnings("finally")
	public JSONObject funGetTableDtl(String POSCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String   sql="select strTableNo,strTableName from tbltablemaster "
	                + "where strOperational='Y' ";
	            if(!POSCode.equals("All"))
	            {
	                sql+=" and strPOSCode='"+POSCode+"' ";
	            }
	            sql+= " order by intSequence ;";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
	          
			 if (list.size()>0)
				{
					for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				
						JSONObject objSettle=new JSONObject();
						objSettle.put("TableNo",obj[0].toString());
						objSettle.put("TableName",obj[1].toString());
					
					
						String tableName=obj[1].toString();
						sql="select strTableNo,strStatus from tbltablemaster where strTableNo='"+obj[0].toString()+"'";
						 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
						
						List sList = query.list();
						Object[] objS = (Object[]) sList.get(0);
						  String status=objS[1].toString();
						  objSettle.put("Status",status);
						  int pax=0;
						  if(status.equals("Occupied"))
		                    {
		                        sql="select intPaxNo from tblitemrtemp where strTableNo='"+obj[0].toString()+"' ";
		                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		                        List pList=query.list();
		                        if(pList.size()>0)
		                        {
		                        	pax=(int) pList.get(0);
		                        	
		                        }
		                    }
						  	objSettle.put("Pax",pax);
							jArrData.put(objSettle);
					}
		           	jObjTableData.put("TableDtl", jArrData);
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
	
	public void funSaveMoveKOT(String KOTNo, String tableNo, String openTableNo)
	{
		List list=null;
		 try
	        {
	           String sql="update tblitemrtemp set strTableNo='"+tableNo+"' "
	                + "where strKOTNo='"+KOTNo+"'";
	           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				
				query.executeUpdate();	
	           
	            sql="select strStatus,intPaxNo from tbltablemaster "
	                + " where strTableNo='"+openTableNo+"'";
	             query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	             list = query.list();
	            if(list.size()>0)
	            {
	            	Object[] objS = (Object[]) list.get(0);
					  String status=objS[0].toString();
	                
	                int pax=Integer.parseInt(objS[1].toString());
	                sql="update tbltablemaster set strStatus='"+status+"',intPaxNo="+pax+" "
	                    + " where strTableNo='"+tableNo+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
					
					query.executeUpdate();	
	            }
	           
	            sql="select strPOSCode from tbltablemaster "
	                + " where strTableNo='"+tableNo+"'";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	             list = query.list();
	            if(list.size()>0)
	            {
	            	String posCode = (String) list.get(0);
				
	                sql="update tblitemrtemp set strPOSCode='"+posCode+"' "
	                    + " where strKOTNo='"+KOTNo+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
					
					query.executeUpdate();	
	            }
	            
	            sql="select strKOTNo from tblitemrtemp where strTableNo='"+openTableNo+"' and strNCKotYN='N' ";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	             list = query.list();
	            if(list.size()>0)
	            {
	                sql="update tbltablemaster set strStatus='Normal',intPaxNo=0 "
	                    + "where strTableNo='"+openTableNo+"'";
	                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
					
					query.executeUpdate();
	            }
	             //insert into itemrtempbck tabl
	           
	            sql = "delete from tblitemrtemp_bck where strTableNo='" + tableNo + "'  ";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				
				query.executeUpdate();
	            sql = "insert into tblitemrtemp_bck (select * from tblitemrtemp where strTableNo='" + tableNo + "'  )";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				
				query.executeUpdate();
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	     
	 
}
