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


@Repository("clsAddKOTToBillDao")

@Transactional(value = "webPOSTransactionManager")
public class clsAddKOTToBillDao {
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@SuppressWarnings("finally")
	public JSONObject funGetKOTDtlForAddKOTTOBill(String posCode, String tableName)
	{
		
		List list =null;
		JSONObject jObjKOTData=new JSONObject();
		try{
		
			String sql = "select distinct(a.strKOTNo),a.strTableNo,b.strTableName "
	                + "from tblitemrtemp a,tbltablemaster b "
	                + "where a.strPOSCode='" + posCode + "' "
	                + "and a.strNCKOTYN='N'  "
	                + "and a.strTableNo=b.strTableNo ";
			if (!tableName.equalsIgnoreCase("All"))
	        {
	            sql += "and b.strTableName like '%" + tableName + "%' ";
	        }

	        sql += "order by a.strKOTNo ";
		
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			JSONArray jArrKOTData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					
				
						JSONObject objKOT=new JSONObject();
						objKOT.put("KOTNo",Array.get(obj, 0));
						objKOT.put("TableNo",Array.get(obj, 1));
						objKOT.put("TableName",Array.get(obj, 2));
						jArrKOTData.put(objKOT);
					}
					jObjKOTData.put("KOTListForAddKOTToBill", jArrKOTData);
			      }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjKOTData;
			}
	}
	
	
	@SuppressWarnings("finally")
	public JSONObject funGetUnsettleBillList(String posCode)
	{
		
		List list =null;
		JSONObject jObjKOTData=new JSONObject();
		try{
		
			String sql = "select strBillNo from tblbillhd"
	                + " where strBillNo not in (select strBillNo from tblbillsettlementdtl) and strTableNo<>'' "
	                + " and strOperationType='Dine In' and strPOSCode='" + posCode + "' ";
		
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			JSONArray jArrBillData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					
				
						JSONObject objBill=new JSONObject();
						objBill.put("BillNo",obj);
						jArrBillData.put(objBill);
					}
					jObjKOTData.put("UnsettleBillList", jArrBillData);
			     }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjKOTData;
			}
	}
	
	
	
	public String funAddKOTToBill(JSONObject jObj)
	{
		String billNo="",areaCode="",tableNo="",billDate="";
		List list =null;
		try
		{
		    
			billNo = jObj.getString("BillNo");
			JSONArray masterFormList=jObj.getJSONArray("KOTList");
			String sql="select date(a.dteBillDate),b.strAreaCode,a.strTableNo "
                    + "from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                    + "where a.strBillNo='" + billNo + "'";
		
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			list = query.list();
			JSONArray jArrBillData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj = (Object[]) list.get(i);
						billDate=obj[0].toString();
						areaCode=obj[1].toString();
						tableNo=obj[2].toString();
					}	
			     }
		 
		  
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return billNo; 
   }
	
	
	
}
