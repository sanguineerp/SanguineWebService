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


@Repository("clsKDSBookAndProcessDao")

@Transactional(value = "webPOSTransactionManager")
public class clsKDSBookAndProcessDao {
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@SuppressWarnings("finally")
	public JSONObject funGetBillDtl()
	{
		 LinkedHashMap<String, ArrayList<JSONObject>> mapBillHd;
		 mapBillHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
			
			String    sql=" SELECT a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,a.tmeOrderProcessing,time(a.dteBillDate)\n"
                    + " FROM tblbilldtl a\n"
                    + " where a.strBillNo not in(select strDocNo from tblkdsprocess where strBP='P' and strKDSName='BILL' ) "
                    + " GROUP BY a.strBillNo,a.strKOTNo,a.strItemCode\n"
                    + " ORDER BY a.dteBillDate desc,time(a.dteBillDate) desc  ";
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
			
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
						 String billNo =obj[0].toString();
						 String itemCode =obj[1].toString();
						JSONObject objSettle=new JSONObject();
						objSettle.put("strBillNo",obj[0].toString());
						objSettle.put("strItemCode",obj[1].toString());
						objSettle.put("strItemName",obj[2].toString());
						objSettle.put("dblRate",(BigDecimal)obj[3]);
						objSettle.put("dblQuantity",(BigDecimal)obj[4]);
						objSettle.put("dblAmount",(BigDecimal)obj[5]);
						objSettle.put("billDateTime",obj[7]);
						if (mapBillHd.containsKey(billNo))
		                {
		                    mapBillHd.get(billNo).add(objSettle);
		                    
		                    String sqlModifierDtl=" SELECT b.strModifierCode,b.strModifierName,b.dblQuantity,b.dblAmount,a.strDefaultModifier,b.strDefaultModifierDeselectedYN  "
		                            +" FROM tblbillmodifierdtl b,tblitemmodofier a " 
		                            +" WHERE " 
		                            +" a.strItemCode=left(b.strItemCode,7) "
		                            +" and a.strModifierCode=b.strModifierCode "
		                            +" and b.strBillNo= :billNo AND LEFT(b.strItemCode,7)= :itemCode ";
		                    
		                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifierDtl);
		                    query.setParameter("billNo", billNo);
		                    query.setParameter("itemCode", itemCode);
		                    
		                 List   mList = query.list();
		       			
		       			
		       			 if (mList!=null)
		       				{
		       					for(int j=0; j<mList.size(); j++)
		       					{
		       						Object[] objM=(Object[])mList.get(j);
		       					
		       					 if(objM[4].toString().equalsIgnoreCase("N"))
		                         {
		                           
		                             JSONObject billItemModiDtl=new JSONObject();
		                             billItemModiDtl.put("strItemCode",objM[0].toString());
		                             billItemModiDtl.put("strItemName",objM[1].toString());
		                             
		                             billItemModiDtl.put("dblQuantity",(BigDecimal)obj[2]);
		                             mapBillHd.get(billNo).add(billItemModiDtl);
		                         }
		                         else if(objM[4].toString().equalsIgnoreCase("Y") && objM[5].toString().equalsIgnoreCase("Y"))
		                         {
		                             
		                             JSONObject billItemModiDtl=new JSONObject();
		                             billItemModiDtl.put("strItemCode",objM[0].toString());
		                             billItemModiDtl.put("strItemName","No"+objM[1].toString());
		                             
		                             billItemModiDtl.put("dblQuantity",(BigDecimal)obj[2]);

		                             mapBillHd.get(billNo).add(billItemModiDtl);
		                         }
		       					}
		       				}
		                }
						else
						{
							  ArrayList<JSONObject> listBillItemDtl = new ArrayList<JSONObject>();

			                    listBillItemDtl.add(objSettle);

			                    mapBillHd.put(billNo, listBillItemDtl);
			                    
			                    String sqlModifierDtl=" SELECT b.strModifierCode,b.strModifierName,b.dblQuantity,b.dblAmount,a.strDefaultModifier,b.strDefaultModifierDeselectedYN  "
			                            +" FROM tblbillmodifierdtl b,tblitemmodofier a " 
			                            +" WHERE " 
			                            +" a.strItemCode=left(b.strItemCode,7) "
			                            +" and a.strModifierCode=b.strModifierCode "
			                            +" and b.strBillNo= :billNo AND LEFT(b.strItemCode,7)= :itemCode ";
			                    
			                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlModifierDtl);
			                    query.setParameter("billNo", billNo);
			                    query.setParameter("itemCode", itemCode);
			                    
			                 List   mList = query.list();
			       			
			       			
			       			 if (mList!=null)
			       				{
			       					for(int j=0; j<mList.size(); j++)
			       					{
			       						Object[] objM=(Object[])mList.get(j);
			       					
			       					 if(objM[4].toString().equalsIgnoreCase("N"))
			                         {
			                           
			                             JSONObject billItemModiDtl=new JSONObject();
			                             billItemModiDtl.put("strItemCode",objM[0].toString());
			                             billItemModiDtl.put("strItemName",objM[1].toString());
			                             
			                             billItemModiDtl.put("dblQuantity",(double)obj[2]);
			                             mapBillHd.get(billNo).add(billItemModiDtl);
			                         }
			                         else if(objM[4].toString().equalsIgnoreCase("Y") && objM[5].toString().equalsIgnoreCase("Y"))
			                         {
			                             
			                             JSONObject billItemModiDtl=new JSONObject();
			                             billItemModiDtl.put("strItemCode",objM[0].toString());
			                             billItemModiDtl.put("strItemName","No"+objM[1].toString());
			                             
			                             billItemModiDtl.put("dblQuantity",(double)obj[2]);

			                             mapBillHd.get(billNo).add(billItemModiDtl);
			                         }
			       					}
			       				}
						}
							
					}
					JSONArray jArrBillNo= new JSONArray();
					JSONArray jArrBillDtl= new JSONArray();
					Set< Map.Entry<String, ArrayList<JSONObject>>> st = mapBillHd.entrySet();    //returns Set view
					  for(Map.Entry<String, ArrayList<JSONObject>> me:st)
					  {
						  jArrBillNo.put(me.getKey());
						  jArrBillDtl.put(me.getValue());
					  }
					  JSONObject jObj= new JSONObject();
					  jObj.put("BillNo",jArrBillNo);
					  jObj.put("BillHd",jArrBillDtl);
		           	jObjTableData.put("mapBillHd", jObj);
		         	
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
	
	
	
	
	 public JSONObject funGetNewBillSize()
     {
		 LinkedHashMap<String, ArrayList<JSONObject>> mapBillHd;
		 mapBillHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		 JSONObject jObjTableData=new JSONObject();
         try
         {
        	
    		List list =null;
             String sql = " SELECT a.strBillNo,a.strItemCode,a.strItemName,a.dblRate,a.dblQuantity,a.dblAmount,a.tmeOrderProcessing\n"
                     + " FROM tblbilldtl a\n"
                     + " where a.strBillNo not in(select strDocNo from tblkdsprocess where strBP='P' and strKDSName='BILL' ) "
                     + " GROUP BY a.strBillNo,a.strKOTNo,a.strItemCode\n"
                     + " ORDER BY a.dteBillDate desc,time(a.dteBillDate) desc  ";
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
 						objSettle.put("strBillNo",obj[0].toString());
 						objSettle.put("strItemCode",obj[1].toString());
 						objSettle.put("strItemName",obj[2].toString());
 						objSettle.put("dblRate",(BigDecimal)obj[3]);
						objSettle.put("dblQuantity",(BigDecimal)obj[4]);
						objSettle.put("dblAmount",(BigDecimal)obj[5]);
						
 						if (mapBillHd.containsKey(billNo))
 		                {
 		                    mapBillHd.get(billNo).add(objSettle);
 		                    
 		                }
                 
                 else
                 {
                     ArrayList<JSONObject> listBillItemDtl = new ArrayList<JSONObject>();

                     listBillItemDtl.add(objSettle);

                     mapBillHd.put(billNo, listBillItemDtl);
                 }
             }
         }
 			jObjTableData.put("newBillSize",mapBillHd.size());
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }

         return jObjTableData;
     }

	 

		public void funBillOrderProcess(JSONArray listOfBillsToBeProcess,String userCode)
		{
			  Date currentDate = new Date();
		        String strCurrentDate = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
		            + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
			List list=null;
			 try
		        {
				 StringBuilder sqlBillOrderProcess = new StringBuilder();

		            sqlBillOrderProcess.append("delete from tblkdsprocess "
		                    + "where strKDSName='BILL' "
		                    + "and strDocNo IN ");
		            for (int i = 0; i < listOfBillsToBeProcess.length(); i++)
		            {
		                if (i == 0)
		                {
		                    sqlBillOrderProcess.append("('" + listOfBillsToBeProcess.get(i) + "'");
		                }
		                else
		                {
		                    sqlBillOrderProcess.append(",'" + listOfBillsToBeProcess.get(i) + "'");
		                }
		            }
		            sqlBillOrderProcess.append(")");
		           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillOrderProcess.toString());
					
					query.executeUpdate();	
					
					
					 sqlBillOrderProcess.setLength(0);
			            sqlBillOrderProcess.append("insert into tblkdsprocess values");
			            for (int i = 0; i < listOfBillsToBeProcess.length(); i++)
			            {
			                if (i == 0)
			                {
			                    sqlBillOrderProcess.append("('" + listOfBillsToBeProcess.get(i) + "','P','" + strCurrentDate + "','" +strCurrentDate + "','" + userCode + "','" + strCurrentDate + "','" + userCode + "','BILL' )");
			                }
			                else
			                {
			                    sqlBillOrderProcess.append(",('" + listOfBillsToBeProcess.get(i) + "','P','" + strCurrentDate +"','" +strCurrentDate + "','" + userCode + "','" + strCurrentDate + "','" + userCode + "','BILL' )");
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
