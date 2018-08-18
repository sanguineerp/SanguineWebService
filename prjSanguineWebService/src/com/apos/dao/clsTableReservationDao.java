
package com.apos.dao;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsTableReservationModel;


@Repository("clsTableReservationDao")

@Transactional(value = "webPOSTransactionManager")
public class clsTableReservationDao {
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@SuppressWarnings("finally")
	public JSONObject funGetReservationDefault(String date, String loginPosCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String    sql="select b.longMobileNo,b.strCustomerName,a.strSmoking,ifnull(c.strTableName,''),a.intPax "
                    + ",a.dteResDate,TIME_FORMAT(a.tmeResTime, '%r'),a.strSpecialInfo,ifnull(c.strTableNo,''),a.strResCode "
                    + "from tblreservation a "
                    + "left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode  "
                    + "left outer join tbltablemaster c on a.strTableNo=c.strTableNo  "
                    + "where date(a.dteResDate) between '" + date + "' and '" + date + "' "
                    + "and a.strPosCode='"+loginPosCode+"'";
        
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
		
			 if (list.size()>0)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						 JSONArray jArrRowData=new JSONArray();
						
						 jArrRowData.put(obj[0]);
						 jArrRowData.put(obj[1]);
						 jArrRowData.put(obj[2]);
						 jArrRowData.put(obj[3]);
						jArrRowData.put(obj[4]);
						jArrRowData.put(obj[5]);
						jArrRowData.put(obj[6]);
						jArrRowData.put(obj[7]);
						jArrRowData.put(obj[8]);
						jArrRowData.put(obj[9]);
						jArrData.put(jArrRowData);
					}
		           	jObjTableData.put("ReservationDtl", jArrData);
		         	
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
	public JSONObject funGetTableReservationDtl(String fromDate,String toDate, String fromTime, String toTime,  String loginPosCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String    sql="select b.longMobileNo,b.strCustomerName,a.strSmoking,c.strTableName,a.intPax ,a.dteResDate,TIME_FORMAT(a.tmeResTime, '%r'),a.strSpecialInfo,c.strTableNo,a.strResCode "
                    + "from tblreservation a "
                    + "left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode  "
                    + "left outer join tbltablemaster c on a.strTableNo=c.strTableNo  "
                    + "where date(a.dteResDate) between '" + fromDate + "' and '" + toDate + "' "
                    + "and a.strPosCode='"+loginPosCode+"'"                    
                    + "and  TIME_FORMAT(a.tmeResTime,'%T') >= '" + fromTime + "'and TIME_FORMAT(a.tmeResTime,'%T') <= '" + toTime + "' ";
        
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
		
			 if (list.size()>0)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						 JSONArray jArrRowData=new JSONArray();
						
						 jArrRowData.put(obj[0]);
						 jArrRowData.put(obj[1]);
						 jArrRowData.put(obj[2]);
						 jArrRowData.put(obj[3]);
						jArrRowData.put(obj[4]);
						jArrRowData.put(obj[5]);
						jArrRowData.put(obj[6]);
						jArrRowData.put(obj[7]);
						jArrRowData.put(obj[8]);
						jArrRowData.put(obj[9]);
						jArrData.put(jArrRowData);
					}
		           
		         	
			      }
			 
				jObjTableData.put("ReservationDtl", jArrData);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	
	public void funCancelTableReservation(String reservationNo, String tableNo)
	{
		
		 try
	        {
	           String sql="delete from tblreservation where strResCode='" + reservationNo + "' ";
	           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				
				query.executeUpdate();	
	         if(tableNo!=null)  
	         {
	            sql="update tbltablemaster set strStatus='Normal' "
                        + " where strTableNo='" + tableNo + "' "
                        + " and strStatus='Reserve' ";
	             query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	             query.executeUpdate();
	         } 
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	     

public void funAddUpdateTableReservation(clsTableReservationModel objMaster){
	webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	
	try
    {
       String sql="update tbltablemaster set strStatus='Reserve' where strTableNo='" + objMaster.getStrTableNo() + "' ";
       Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		
		query.executeUpdate();	
    
    }catch(Exception e)
    {
        e.printStackTrace();
    }
}
	
public String funGenerateReservationCode()
{
String strResCode = "";
try
{
    
    String sql = "select ifnull(max(strResCode),0) from tblreservation";
    
    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    List list = query.list();
    
    
    if (!list.get(0).toString().equals("0"))
	{
		String strCode = "0";
		String code = list.get(0).toString();
		StringBuilder sb = new StringBuilder(code);
		String ss = sb.delete(0, 2).toString();
		for (int i = 0; i < ss.length(); i++)
		{
			if (ss.charAt(i) != '0')
			{
				strCode = ss.substring(i, ss.length());
				break;
			}
		}
		int intCode = Integer.parseInt(strCode);
		intCode++;
		if (intCode < 10)
		{
			strResCode = "RS000000" + intCode;
		}
		else if (intCode < 100)
		{
			strResCode = "RS00000" + intCode;
		}
		else if (intCode < 1000)
		{
			strResCode = "RS0000" + intCode;
		}
		else if (intCode < 10000)
		{
			strResCode = "RS000" + intCode;
		}
		else if (intCode < 100000)
		{
			strResCode = "RS00" + intCode;
		}
		else if (intCode < 1000000)
		{
			strResCode = "RS0" + intCode;
		}
		else if (intCode < 10000000)
		{
			strResCode = "RS" + intCode;
		}
	}
	else
	{
		strResCode = "T01";
	}

}
catch (Exception e)
{
    e.printStackTrace();
}

return strResCode;
}

@SuppressWarnings("finally")
public String funCheckCustomerExist(String contactNo)
{
	
	List list =null;
	String strCustomerCode="";
	try{
	
		String    sql="select strCustomerCode,strCustomerName,strBuldingCode,strBuildingName,strCity from tblcustomermaster  where longMobileNo='" + contactNo + "' ";
    
		
		Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		
		list = query.list();
		 JSONArray jArrData=new JSONArray();
	
		 if (list.size()>0)
			{
			 Object[] obj=(Object[])list.get(0);
			 strCustomerCode=obj[0].toString();
			}
           
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
		finally
		{
			return strCustomerCode;
		}
} 
}
