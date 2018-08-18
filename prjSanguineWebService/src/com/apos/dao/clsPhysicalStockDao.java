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

import com.apos.model.clsUserDetailHdModel;


@Repository("clsPhysicalStockDao")

@Transactional(value = "webPOSTransactionManager")
public class clsPhysicalStockDao {
	
@Autowired
private SessionFactory webPOSSessionFactory;

@SuppressWarnings("finally")
public JSONObject funGetItemDetails(String itemCode,String posCode)
{
	String itemStock="";
	String[] dayPrice = {"strPriceSunday", "strPriceMonday", "strPriceTuesday", "strPriceWednesday", "strPriceThursday", "strPriceFriday", "strPriceSaturday"};
    int dayNo = new Date().getDay();
    String transDay = dayPrice[dayNo];
	List list =null;
	JSONObject jObjStockData=new JSONObject();
	try
	{
	
		String sql = "select intBalance from tblitemcurrentstk where strItemCode='"+itemCode+"'";
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    list = query.list();
		JSONArray jArrStkData=new JSONArray();
		if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
				    JSONObject objStk=new JSONObject();
				    objStk.put("Stock",obj);
					jArrStkData.put(objStk);
				}
			}
		 
		 
		 sql="select dblPurchaseRate from tblitemmaster where strItemCode='"+itemCode+"' ";
		 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	     list = query.list();
		 JSONArray jArrPurchaseData=new JSONArray();
		 if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
				    JSONObject objPurchase=new JSONObject();
				    objPurchase.put("PurchaseRate",obj);
				    jArrPurchaseData.put(objPurchase);
				}
			}  
			 
			 
			 
			 sql="select " + transDay + " from tblmenuitempricingdtl "
			                + "where strItemCode='" +itemCode+ "' and strPosCode='"+posCode+"' ";
			 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		     list = query.list();
			 JSONArray jArrSaleData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					    JSONObject objSale=new JSONObject();
					    objSale.put("SaleRate",obj);
					    jArrSaleData.put(objSale);
					}
				} 
			 if(list.isEmpty())
			 {
				 JSONObject objSale=new JSONObject();
				 objSale.put("SaleRate",0);
				 jArrSaleData.put(objSale);
			 }
				 
			
		 
		 jObjStockData.put("ItemStock", jArrStkData);
		 jObjStockData.put("ItemPurchaseRate", jArrPurchaseData);
		 jObjStockData.put("ItemSaleRate", jArrSaleData);
           
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
		finally
		{
			return jObjStockData;
		}
}
	
@SuppressWarnings("finally")
public JSONObject funGetReasonCode(String reasonCode,String type)
{
	String itemStock="";
	List list =null;
	JSONObject jObjReasonData=new JSONObject();
	try
	{
		String sql = "select strReasonCode,strReasonName from tblreasonmaster where "+type+"='Y' ";
		if (!reasonCode.equalsIgnoreCase("All"))
        {
            sql += " and strReasonCode='"+reasonCode+"'  ";
        }
		
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    
	    list = query.list();
		JSONArray jArrReasonData=new JSONArray();
		 if (list!=null)
			{
				for(int i=0; i<list.size(); i++)
				{
					Object obj=list.get(i);
				    JSONObject objReason=new JSONObject();
				    objReason.put("ReasonCode",Array.get(obj, 0));
				    objReason.put("ReasonName",Array.get(obj, 1));
				    jArrReasonData.put(objReason);
				}
				jObjReasonData.put("ReasonList", jArrReasonData);
		     }
		 
           
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
		finally
		{
			return jObjReasonData;
		}
  }


   public String funSavePhysicalStockPosting(JSONObject jObj)
	{
		String remark="",reasonCode="",clientCode="",posCode="",userCode="",saleAmt="",result="",stockoutAmt="",PSPCode="";
		List list =null;
		try
		{
			remark = jObj.getString("Remark");
			reasonCode = jObj.getString("ReasonCode");
			reasonCode = jObj.getString("ReasonCode");
			clientCode = jObj.getString("ClientCode");
			posCode = jObj.getString("POSCode");
			userCode = jObj.getString("UserCode");
			saleAmt = jObj.getString("SaleAmount");
			stockoutAmt = jObj.getString("StockOutAmount");
			PSPCode=jObj.getString("PSPCode"); 
			Date objDate=new Date();
			String date=(objDate.getYear()+1900)+"-"+(objDate.getMonth()+1)+"-"+objDate.getDate()
	                    +" "+objDate.getHours()+":"+objDate.getMinutes()+":"+objDate.getSeconds();
			JSONArray mJsonArrayItems=jObj.getJSONArray("ItemList");
			
		     
			 if(mJsonArrayItems.length()>0)
			 {
				 StringBuilder items = new StringBuilder("(");
			        for (int i = 0; i < mJsonArrayItems.length(); i++)
					{
					    JSONObject mJsonObject = (JSONObject) mJsonArrayItems.get(i);
					    String itemCode = mJsonObject.getString("ItemCode");
					    if (i == 0)
		                {
					    	items.append("'" + itemCode + "'");
		                }
		                else
		                {
		                	items.append(",'" + itemCode + "'");
		                }
					}
			        items.append(")");
			        String sql = "select * from tblitemcurrentstk where strItemCode not in "+items+" and intBalance>0";
					Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
					
					list = query.list();
					JSONArray jArrTableData=new JSONArray();
					 if (list!=null)
						{
							for(int i=0; i<list.size(); i++)
							{
								Object obj=list.get(i);
								double compQty=(Double) Array.get(obj, 10);
								double phyQty=(Double) Array.get(obj, 10);
			                    double dblVarQty=((compQty)-(phyQty));
			                    JSONObject jObjItemDtl=new JSONObject();
			                    jObjItemDtl.put("ItemCode", (String) Array.get(obj, 3));
			                    jObjItemDtl.put("ItemName",(String) Array.get(obj, 4));
			                    jObjItemDtl.put("CompQty",compQty);
			                    jObjItemDtl.put("PhyQty",phyQty);
			                    jObjItemDtl.put("Variance", dblVarQty);
			                    jObjItemDtl.put("VarianceAmt",0);
			                    ((List) mJsonArrayItems).add(jObjItemDtl);
			                }
						}
					
					if(PSPCode.isEmpty())
					{
						PSPCode=funGeneratePhysicalStockCode();
					}
					String sqlInsertPSPDtl="";
				      
				    sqlInsertPSPDtl= " insert into tblpspdtl (strPSPCode,strItemCode,dblPhyStk,dblCompStk,dblVariance,"
				            + " dblVairanceAmt,strClientCode,strDataPostFlag) "
				            + " values ";
				        
			        for (int i = 0; i < mJsonArrayItems.length(); i++)
					{
					    JSONObject mJsonObject = (JSONObject) mJsonArrayItems.get(i);
					    sqlInsertPSPDtl+=" ('"+PSPCode+"','"+mJsonObject.getString("ItemCode")+"','"+mJsonObject.getString("PhyQty")+"',"
				                + " '"+mJsonObject.getString("CompQty")+"','"+mJsonObject.getString("Variance")+"','"+mJsonObject.getString("VarianceAmt")+"',"
				                + " '"+clientCode+"','N'),";
					} 
			        
			        
			        sql="delete from tblpspdtl where strPSPCode='"+PSPCode+"' and strClientCode='"+clientCode+"' ";
			        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
        			query.executeUpdate();

			        StringBuilder sb = new StringBuilder(sqlInsertPSPDtl);
			        int index = sb.lastIndexOf(",");
			        sqlInsertPSPDtl = sb.delete(index, sb.length()).toString();
			        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertPSPDtl);
        			query.executeUpdate(); 
        			
        			sql="delete from tblpsphd where strPSPCode='"+PSPCode+"' and strClientCode='"+clientCode+"'; ";
        			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
         			query.executeUpdate();

        	        sql="insert into tblpsphd (strPSPCode,strPOSCode,strStkInCode,strStkOutCode,strBillNo,"
        	            + "dblStkInAmt,dblSaleAmt,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited"
        	            + ",strClientCode,strDataPostFlag,strReasonCode,strRemarks)"
        	            + " values('"+PSPCode+"','"+posCode+"','','','','"+stockoutAmt+"','"+saleAmt+"','"+userCode+"'"
        	            + ",'"+userCode+"','"+date+"','"+date+"','"+clientCode+"','N','"+reasonCode+"','"+remark+"')";
        	        query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
         			query.executeUpdate();
         			result=PSPCode;
			 }
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    result="error";
		}
		
		return result; 
	}

   
	   private String funGeneratePhysicalStockCode() 
	   {
	       String pspCode="";
	       int lastNo = 0;
	       List list =null;
	       try 
	       {
	           String sql = "select strTransactionType,dblLastNo from tblinternal where strTransactionType='Physicalstock'";
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
				            pspCode = "PS" + String.format("%07d", lastNo);
				            sql=" update tblinternal set dblLastNo='" + lastNo + "' "
					           +" where strTransactionType='Physicalstock'";
		                    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		        			query.executeUpdate();       
		                    
						}
					}
	
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	       return pspCode;
	   }
	   
	   
	   @SuppressWarnings("finally")
	   public JSONObject funGetItemForExport()
	   {
	   	List list =null;
	   	JSONObject jObjItemsData=new JSONObject();
	   	try
	   	{
	   	
	   		String sql = "select a.strItemCode,a.strSubgroupName,a.strItemName,a.intBalance "
                       + " from tblitemcurrentstk  a,tblitemmaster b "
                       + " where a.strItemCode=b.strItemCode "
                       + " order by a.strSubgroupName,a.strItemName";
	   	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	   	    list = query.list();
	   		JSONArray jArrItemDetails=new JSONArray();
	   		if (list!=null)
	   			{
	   				for(int i=0; i<list.size(); i++)
	   				{
	   					Object obj=list.get(i);
	   				    JSONObject objItems=new JSONObject();
	   				    objItems.put("ItemCode",Array.get(obj, 0));
	   				    objItems.put("SubGroupName",Array.get(obj, 1));
	   				    objItems.put("ItemName",Array.get(obj, 2));
	   				    objItems.put("CompStock",Array.get(obj, 3));
	   				    jArrItemDetails.put(objItems);
	   				}
	   			}
	   		
	   		jObjItemsData.put("PhysicalStockExportItems", jArrItemDetails);
	              
	   		}catch(Exception ex)
	   		{
	   			ex.printStackTrace();
	   			
	   		}
	   		finally
	   		{
	   			return jObjItemsData;
	   		}
	   }
	   
	   public JSONObject funGetItemList(String masterCode) throws Exception
	 		{
	 			JSONArray jArrData = new JSONArray();
	 			JSONObject jObjSearchData = new JSONObject();
	 			SQLQuery sqlQuery=webPOSSessionFactory.getCurrentSession().createSQLQuery("select a.strItemCode,a.strItemName, "
	     				+ " a.strItemType ,a.strExternalCode "
	     				+ " from tblitemmaster a,tblsubgrouphd b "
	     				+ " where a.strItemCode='"+masterCode+"' and a.strSubGroupCode=b.strSubGroupCode "
	     				+ " order by a.strItemName "); 
	 			
	     		List list=sqlQuery.list();					
	 									
	 			for(int cnt=0;cnt<list.size();cnt++)
	 			{
	 				Object[] objArr = (Object[]) list.get(cnt);
	 			    
	 			    JSONArray jArrDataRow = new JSONArray();
	 			    
	 			    jArrDataRow.put(objArr[0].toString());//item code
	 			    jArrDataRow.put(objArr[1].toString());//item name
	 			    jArrDataRow.put(objArr[2].toString());//item type
	 			    jArrDataRow.put(objArr[3].toString());//external code
	 			    
	 			    jArrData.put(jArrDataRow);
	 			}
	 			jObjSearchData.put("POSItemList", jArrData);
	 			
	 			return jObjSearchData; 
	 		} 
	 	
	   
	   public JSONObject funGetPhysicalStkData(String masterCode) throws Exception
		{
			JSONArray jArrData = new JSONArray();
			JSONObject jObjSearchData = new JSONObject();
			SQLQuery sqlQuery=webPOSSessionFactory.getCurrentSession().createSQLQuery("select b.strPSPCode,c.strItemName,b.strItemCode,b.dblCompStk,b.dblPhyStk, "
					+ " b.dblVariance,b.dblVairanceAmt,a.strReasonCode,d.strReasonName,a.strPOSCode,e.strPosName "
					+ " from  tblpsphd a ,tblPSPdtl b, tblItemMaster c,tblreasonmaster d ,tblposmaster e "
					+ " where a.strPSPCode='"+masterCode+"' and a.strPSPCode=b.strPSPCode   and a.strPOSCode=e.strPosCode "
					+ " and b.strItemCode=c.strItemCode and a.strReasonCode=d.strReasonCode "); 
		
			
			List list=sqlQuery.list();					
			
			for(int cnt=0;cnt<list.size();cnt++)
			{
				Object[] objArr = (Object[]) list.get(cnt);
			    
			    JSONArray jArrDataRow = new JSONArray();
			    
			    jArrDataRow.put(objArr[0].toString());//psp code
			    jArrDataRow.put(objArr[1].toString());//item name
			    jArrDataRow.put(objArr[2].toString());//item code
			    jArrDataRow.put(objArr[3].toString());//Comp StockQty
			    jArrDataRow.put(objArr[4].toString());//Phy StockQty
			    jArrDataRow.put(objArr[5].toString());//variance
			    jArrDataRow.put(objArr[6].toString());//variance Amt
			    jArrDataRow.put(objArr[7].toString());//reason Code
			    jArrDataRow.put(objArr[8].toString());//reason name
			    jArrDataRow.put(objArr[9].toString());//posCode
			    jArrDataRow.put(objArr[10].toString());//posName
			    
			    jArrData.put(jArrDataRow);
			}
			jObjSearchData.put("PhysicalStock", jArrData);
			
			return jObjSearchData; 
		} 
	

}