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


@Repository("clsMoveTableDao")

@Transactional(value = "webPOSTransactionManager")
public class clsMoveTableDao {
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@SuppressWarnings("finally")
	public JSONObject funGetTableList(String posCode, String tableStatus)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String sql = "select strTableNo,strTableName,strStatus from tbltablemaster ";
			if (!tableStatus.equalsIgnoreCase("All"))
	        {
	            sql += " where strStatus='"+tableStatus+"' and strPOSCode='"+posCode+"' ";
	        }
			else
			{
				 sql += " where strPOSCode='"+posCode+"' ";
			}

	        sql += "  order by intSequence ";
		
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			JSONArray jArrTableData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					
				
						JSONObject objTable=new JSONObject();
						objTable.put("TableNo",Array.get(obj, 0));
						objTable.put("TableName",Array.get(obj, 1));
						objTable.put("TableStatus",Array.get(obj, 2));
						jArrTableData.put(objTable);
					}
					jObjTableData.put("TableListForMoveTable", jArrTableData);
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
	
	
	
	
	public String funSaveMoveTable(JSONObject jObj)
	{
		String movedToTableNo="",movedFromTableNo="",result="";
		List list =null;
		try
		{
			movedFromTableNo = jObj.getString("MovedFromTable");
			movedToTableNo = jObj.getString("MovedToTable");
			String sql="update tblitemrtemp set strTableNo='"+movedToTableNo+"' "
                    + "where strTableNo='"+movedFromTableNo+"'";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();	
			
			
			sql = "select strStatus,intPaxNo,strTableName,strPOSCode "
                    + "from tbltablemaster where strTableNo='"+movedFromTableNo+"' ";
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			JSONArray jArrTableData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					    String status=(String) Array.get(obj, 0);
	                    int pax=(int) Array.get(obj, 1);
	                    String tableName=(String) Array.get(obj, 2);
	                    String pos=(String) Array.get(obj, 3);
	                    
	                    sql="update tbltablemaster set strStatus='"+status+"',intPaxNo="+pax+" "
	                            + "where strTableNo='"+movedToTableNo+"'";
	                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	        			query.executeUpdate();
	                        
                        sql="update tbltablemaster set strStatus='Normal',intPaxNo=0 "
                            + "where strTableNo='"+movedFromTableNo+"'";
                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            			query.executeUpdate();
                        
                        sql="update tblitemrtemp set strPOSCode='"+pos+"' "
                                + " where strTableNo='"+movedToTableNo+"'";
                        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            			query.executeUpdate();
            			result="Tabled Moved From "+movedFromTableNo+" to "+movedToTableNo+" ";
					}
				}
			
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    result="error";
		}
		
		return result; 
   }
	
	
}
