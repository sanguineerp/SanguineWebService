
package com.apos.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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


@Repository("clsKDSForKOTBookAndProcessDao")

@Transactional(value = "webPOSTransactionManager")
public class clsKDSForKOTBookAndProcessDao {
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@SuppressWarnings("finally")
	public JSONObject funGetKOTHdDtl()
	{
		 LinkedHashMap<String, ArrayList<JSONObject>> mapKOTHd;
		 mapKOTHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
			
			String    sql=" select a.strKOTNo,a.strItemCode,a.strItemName,a.dblRate,sum(a.dblItemQuantity),sum(a.dblAmount) "
                    + ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') as dteKOTDate,time(a.dteDateCreated) as tmeKOTTime "
                    + "from tblitemrtemp a "
                    + "where a.strNCKotYN='N' "
                    + "and a.tdhComboItemYN='N' "
                    + "and a.strKOTNo not in(select strDocNo from tblkdsprocess where strBP='P' and strKDSName='KOT' ) "
                    + "group by a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
                    + "ORDER BY a.dteDateCreated desc,time(a.dteDateCreated) desc ";
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
			
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						 String KOTNo =obj[0].toString();
						 String itemCode =obj[1].toString();
						JSONObject objSettle=new JSONObject();
						objSettle.put("strKOTNo",obj[0].toString());
						objSettle.put("strItemCode",obj[1].toString());
						objSettle.put("strItemName",obj[2].toString());
						objSettle.put("dblRate",(BigDecimal)obj[3]);
						objSettle.put("dblQuantity",(BigDecimal)obj[4]);
						objSettle.put("dblAmount",(BigDecimal)obj[5]);
						objSettle.put("billDateTime",obj[7]);
						if (mapKOTHd.containsKey(KOTNo))
		                {
		                    mapKOTHd.get(KOTNo).add(objSettle);
		              
		                }
						else
						{
							  ArrayList<JSONObject> listBillItemDtl = new ArrayList<JSONObject>();

			                    listBillItemDtl.add(objSettle);

			                    mapKOTHd.put(KOTNo, listBillItemDtl);
			                  
						}
							
					}
					JSONArray jArrBillNo= new JSONArray();
					JSONArray jArrBillDtl= new JSONArray();
					Set< Map.Entry<String, ArrayList<JSONObject>>> st = mapKOTHd.entrySet();    //returns Set view
					  for(Map.Entry<String, ArrayList<JSONObject>> me:st)
					  {
						  jArrBillNo.put(me.getKey());
						  jArrBillDtl.put(me.getValue());
					  }
					  JSONObject jObj= new JSONObject();
					  jObj.put("KOTNo",jArrBillNo);
					  jObj.put("KOTHd",jArrBillDtl);
		           	jObjTableData.put("mapKOTHd", jObj);
		         	
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
	
	
	
	
	 public JSONObject funGetNewKOTSize()
     {
		 LinkedHashMap<String, ArrayList<JSONObject>> mapKOTHd;
		 mapKOTHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		 JSONObject jObjTableData=new JSONObject();
         try
         {
        	
    		List list =null;
             String sql = " select a.strKOTNo,a.strItemCode,a.strItemName,a.dblRate,sum(a.dblItemQuantity),sum(a.dblAmount) "
                            + ",DATE_FORMAT(date(a.dteDateCreated),'%d-%m-%Y') as dteKOTDate,time(a.dteDateCreated) as tmeKOTTime "
                            + "from tblitemrtemp a "
                            + "where a.strNCKotYN='N' "
                            + "and a.tdhComboItemYN='N' "
                            + "and a.strKOTNo not in(select strDocNo from tblkdsprocess where strBP='P' and strKDSName='KOT' ) "
                            + "group by a.strTableNo,a.strKOTNo,a.strItemCode,a.strItemName "
                            + "ORDER BY a.dteDateCreated desc,time(a.dteDateCreated) desc  ";
             //System.out.println("total bills-->"+sqlBillDtl);
             Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
 			
 			list = query.list();
 			
 			 if (list!=null)
 				{
 					for(int i=0; i<list.size(); i++)
 					{
 						Object[] obj=(Object[])list.get(i);
 					
 						 String billNo =obj[0].toString();
 						 String itemCode =obj[1].toString();
 						JSONObject objSettle=new JSONObject();
 						objSettle.put("strKOTNo",obj[0].toString());
 						objSettle.put("strItemCode",obj[1].toString());
 						objSettle.put("strItemName",obj[2].toString());
 						objSettle.put("dblRate",(BigDecimal)obj[3]);
						objSettle.put("dblQuantity",(BigDecimal)obj[4]);
						objSettle.put("dblAmount",(BigDecimal)obj[5]);
						
 						if (mapKOTHd.containsKey(billNo))
 		                {
 		                    mapKOTHd.get(billNo).add(objSettle);
 		                    
 		                }
                 
                 else
                 {
                     ArrayList<JSONObject> listBillItemDtl = new ArrayList<JSONObject>();

                     listBillItemDtl.add(objSettle);

                     mapKOTHd.put(billNo, listBillItemDtl);
                 }
             }
         }
 			jObjTableData.put("newKOTSize",mapKOTHd.size());
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }

         return jObjTableData;
     }

	 

		public void funKOTOrderProcess(JSONArray listOfKOTsToBeProcess,String userCode)
		{
			  Date currentDate = new Date();
		        String strCurrentDate = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
		            + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
			List list=null;
			 try
		        {
				 StringBuilder sqlBillOrderProcess = new StringBuilder();

		            sqlBillOrderProcess.append("delete from tblkdsprocess "
                    + "where strKDSName='KOT' "
                    + "and strDocNo IN ");
		            for (int i = 0; i < listOfKOTsToBeProcess.length(); i++)
		            {
		                if (i == 0)
		                {
		                    sqlBillOrderProcess.append("('" + listOfKOTsToBeProcess.get(i) + "'");
		                }
		                else
		                {
		                    sqlBillOrderProcess.append(",'" + listOfKOTsToBeProcess.get(i) + "'");
		                }
		            }
		            sqlBillOrderProcess.append(")");
		           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillOrderProcess.toString());
					
					query.executeUpdate();	
					
					
					 sqlBillOrderProcess.setLength(0);
			            sqlBillOrderProcess.append("insert into tblkdsprocess values");
			            for (int i = 0; i < listOfKOTsToBeProcess.length(); i++)
			            {
			                if (i == 0)
			                {
			                    sqlBillOrderProcess.append("('" + listOfKOTsToBeProcess.get(i) + "','P','" + strCurrentDate + "','" +strCurrentDate + "','" + userCode + "','" + strCurrentDate + "','" + userCode + "','KOT' )");
			                }
			                else
			                {
			                    sqlBillOrderProcess.append(",('" + listOfKOTsToBeProcess.get(i) + "','P','" + strCurrentDate +"','" +strCurrentDate + "','" + userCode + "','" + strCurrentDate + "','" + userCode + "','KOT' )");
			                }
			            }

		         
		            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillOrderProcess.toString());
					
					query.executeUpdate();
		        }catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		    }
		     
}
