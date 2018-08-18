package com.apos.dao;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.math.BigInteger ;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.controller.clsSynchDataWithAPOS;
import com.apos.controller.clsUtilityController;
import com.apos.model.clsMakeKOTHdModel;
import com.apos.model.clsMakeKOTModel_ID;
import com.apos.model.clsNonChargableKOTHdModel;
import com.apos.model.clsNonChargableKOTModel_ID;
import com.webservice.util.clsItemDtlForTax;
import com.webservice.util.clsTaxCalculation;
import com.webservice.util.clsTaxCalculationDtls;
import com.webservice.util.clsUtilityFunctions;

@Repository("clsMoveKOTItemsToTableDao")

@Transactional(value = "webPOSTransactionManager")
public class clsMoveKOTItemsToTableDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired
	private clsUtilityController objUtilityctrl; 
	
	@Autowired
	clsMakeKOTDao	objMakeKOTDao;
	@SuppressWarnings("finally")
	public JSONObject funGetBusyTableDtl(String loginPosCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
			
			String    sql="select strTableNo,strTableName from tbltablemaster "
		            + " where strStatus='Occupied' and strPOSCode='"+loginPosCode+"' ";
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
			
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object obj=list.get(i);
					
				
						JSONObject objSettle=new JSONObject();
						objSettle.put("TableNo",Array.get(obj, 0));
						objSettle.put("TableName",Array.get(obj, 1));
						
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
	

	@SuppressWarnings("finally")
	public JSONObject funGetOpenKOTDtl(String tableNo, String loginPosCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String    sql="select distinct(strKOTNo),strTableNo from tblitemrtemp ";
            if(!tableNo.equals("All"))
            {
                	 sql+= " where strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"'";
                                
            }
            else
            {  
                    sql+=" where strPOSCode='"+loginPosCode+"'";
                              
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
	public JSONObject funGetKOTItemsDtl(String KOTNo,String tableNo, String loginPosCode)
	{
		
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String    sql="select count(*) from tblitemrtemp "
					  + "where strKOTNo='" + KOTNo + "' ";
            if(!tableNo.equals("All"))
            {
                 sql+= " and strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"'";                
            }
            else
            {
                sql+= " and strPOSCode='" + loginPosCode + "' ";
                 
            }
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
			
			 if (list!=null)
				{
				 sql = "select strItemName,dblItemQuantity,dblAmount,strItemCode,strWaiterNo,dteDateCreated"
	                        + " ,strSerialNo,dblRedeemAmt,strCustomerCode,strPOSCode,strTableNo "
	                        + " from tblitemrtemp "
	                        + " where strKOTNo='" + KOTNo + "' ";
	                if(!tableNo.equals("All"))
	                {
	                     sql+= " and strTableNo='"+tableNo+"' and strPOSCode='"+loginPosCode+"'";                
	                }
	                else
	                {
	                    sql+= " and strPOSCode='" + loginPosCode + "' ";

	                }
	                sql+=" order by strSerialNo";
	                 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    			
	    			list = query.list();
				 
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
				
						JSONObject objSettle=new JSONObject();
						objSettle.put("strItemName",obj[0]);
						objSettle.put("dblItemQuantity",obj[1]);
						objSettle.put("dblAmount",obj[2]);
						objSettle.put("strItemCode",obj[3]);
						objSettle.put("strWaiterNo",obj[4]);
						objSettle.put("dteDateCreated",obj[5]);
						objSettle.put("strSerialNo",obj[6]);
						objSettle.put("dblRedeemAmt",obj[7]);
						objSettle.put("strCustomerCode",obj[8]);
						jArrData.put(objSettle);
					}
		           	jObjTableData.put("KOTItemsDtl", jArrData);
		         
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
	private String funGenerateKOTNo()
    {
        String kotNo = "";
        try
        {
        	BigInteger code = BigInteger.valueOf(0);
            String sql = "select dblLastNo from tblinternal where strTransactionType='KOTNo'";
            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			List list = query.list();

			 if (list!=null)
				{
				code = (BigInteger)list.get(0);
                code = code.add(BigInteger.valueOf(1));
                kotNo = "KT" + String.format("%07d", code);
              
            }
            else
            {
                kotNo = "KT0000001";
              
            }
           
            sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='KOTNo'";
            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			query.executeUpdate();	

        }
        catch (Exception e)
        {
         
            e.printStackTrace();
        }
        return kotNo;
    }
    
    
	public void funSaveMoveKOTItemsToTable(String loginPosCode,String userCode, String posDate,String clientCode,String busyTblNo, String tableNo, JSONArray listItemDtl)
	{
		 List<clsPOSItemDetailFrTaxBean> arrListItemDtl = new ArrayList<clsPOSItemDetailFrTaxBean>();
		List list=null;
		 try
	        {
			 double taxAmt = 0,subTotalAmt=0;
			 for (int i = 0; i < listItemDtl.length(); i++) 
				{
				 clsPOSItemDetailFrTaxBean objItemDtl=new clsPOSItemDetailFrTaxBean();
				    JSONObject jObj = (JSONObject) listItemDtl.get(i);
				    String itemCode=jObj.getString("strItemCode");
			    	String itemName=jObj.getString("strItemName");
			    	double itemAmt=jObj.getDouble("dblAmount");
			    	 subTotalAmt=subTotalAmt+itemAmt;
			    	objItemDtl.setItemCode(itemCode);
			    	 objItemDtl.setItemName(itemName);
			    	 objItemDtl.setAmount(itemAmt);
			    	objItemDtl.setDiscAmt(0);
			    	objItemDtl.setDiscPer(0);
			    	arrListItemDtl.add(objItemDtl);
	                
				}   
				   
			 if(arrListItemDtl.size()>0)
             {
                 String areaCode="";
                 String sql = "select strAreaCode from tbltablemaster where strTableNo='" + tableNo + "' ";
                 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     			
     			 list = query.list();

     			 if (list!=null)
     				{
                     areaCode = (String)list.get(0);
                 }
     			  clsTaxCalculation objTaxCalculation=new clsTaxCalculation();
                 List<clsTaxCalculationDtls> arrListTaxDtl = objUtilityctrl.funCalculateTax(arrListItemDtl, loginPosCode, posDate, areaCode, "DineIn", subTotalAmt, 0.0, "Make KOT", "Cash");
//                		 (arrListItemDtl, loginPosCode,userCode ,areaCode, "DineIn", subTotalAmt, 0, "Make KOT", "Cash");
                 for (clsTaxCalculationDtls objTaxDtl : arrListTaxDtl)
            
                 {
                     taxAmt += objTaxDtl.getTaxAmount();
                 }
                 arrListTaxDtl = null;
             }
			 
			 
			 
			  String kotNo=funGenerateKOTNo();
	          
	          
	            double KOTAmt=0;
	            int cnt=0;
	            
	           /* String insertQuery = "insert into tblitemrtemp(strSerialNo,strTableNo,strCardNo,dblRedeemAmt,strPosCode,strItemCode"
	            + ",strHomeDelivery,strCustomerCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo"
	            + ",strKOTNo,intPaxNo,strPrintYN,strUserCreated,strUserEdited,dteDateCreated"
	            + ",dteDateEdited,strTakeAwayYesNo,strNCKotYN,strCustomerName,strCounterCode"
	            + ",dblRate,dblTaxAmt) values ";
	            
	           */ for(int i=0; i<listItemDtl.length(); i++)
			    {
	            	 String redeemAmt="";
				 JSONObject jObj = new JSONObject();
			    	jObj=listItemDtl.getJSONObject(i);
			    	String itemCode=jObj.getString("strItemCode");
			    	String itemName=jObj.getString("strItemName");
			    	double itemQty=jObj.getDouble("dblItemQuantity");
			    	double itemAmt=jObj.getDouble("dblAmount");
			    	String waiterNo=jObj.getString("strWaiterNo");
			    	String createdDate=jObj.getString("dteDateCreated");
			    	String serialNo=jObj.getString("strSerialNo");
			    	clsSynchDataWithAPOS objAPOS= new clsSynchDataWithAPOS();
			    	
			    	 clsMakeKOTHdModel objModel=new clsMakeKOTHdModel(new clsMakeKOTModel_ID(serialNo, tableNo, itemCode, itemName, kotNo));
					   
					    objModel.setStrActiveYN("");
					    objModel.setStrCardNo("");
					    objModel.setStrCardType(" ");
					    objModel.setStrCounterCode("");
					    objModel.setStrCustomerCode("");
					    objModel.setStrCustomerName("");
					    objModel.setStrDelBoyCode("");
					    objModel.setStrHomeDelivery("");
					    
					    objModel.setStrManualKOTNo(" ");
					    objModel.setStrNCKotYN("N");
					    objModel.setStrOrderBefore(" ");
					    objModel.setStrPOSCode(loginPosCode);
					    objModel.setStrPrintYN("Y");
					    objModel.setStrPromoCode(" ");
					    objModel.setStrReason("");
					    objModel.setStrWaiterNo(waiterNo);
					    objModel.setStrTakeAwayYesNo("");
					    objModel.setDblAmount(itemAmt);
					    objModel.setDblBalance(0.00);
					    objModel.setDblCreditLimit(0.00);
					    objModel.setDblItemQuantity(itemQty);
					    objModel.setDblRate(0.00);
					    objModel.setDblRedeemAmt(0);
					    objModel.setDblTaxAmt(taxAmt);
					    objModel.setIntId(0);
					    objModel.setIntPaxNo(1);
					    
					    objModel.setDteDateCreated(posDate);
					    objModel.setDteDateEdited(posDate);
					 
					    objModel.setStrUserCreated(userCode);
					    objModel.setStrUserEdited(userCode);
					    
					    objMakeKOTDao.funSaveKOT(objModel);
			    	/*
	                        if (cnt == 0)
	                        {
	                            insertQuery += "('"+serialNo+"','" +tableNo+ "'"
	                                + ",'','0','" + loginPosCode+ "','" + itemCode+ "',"
	                                + "'','','" +itemName+ "'"
	                                + ",'" +itemQty+ "','" +itemAmt+ "'"
	                                + ",'" +waiterNo+ "','" +kotNo+ "'"
	                                + ",'1','Y'"
	                                + ",'" + userCode+ "','" + userCode + "'"
	                                + ",'" + posDate + "','" + posDate + "'"
	                                + ",'','N','','','0.00',"+taxAmt+")";
	                            
	                        }
	                        else
	                        {
	                            insertQuery += ",('"+serialNo+"','" +tableNo+ "'"
	                                + ",'','0','" + loginPosCode + "','" + itemCode+ "',"
	                                + "'','','" +itemName+ "'"
	                                + ",'" +itemQty+ "','" +itemAmt+ "'"
	                                + ",'" +waiterNo+ "','" +kotNo+ "'"
	                                + ",'1','Y'"
	                                + ",'" + userCode + "','" + userCode + "'"
	                                + ",'" + posDate + "','" + posDate + "'"
	                                + ",'','N','','','0.00',"+taxAmt+")";
	                        } 
	                        cnt++;*/
	                    }
	                
	            
	            
	           /* if(cnt>0)
	              {
	            	Query   query = webPOSSessionFactory.getCurrentSession().createSQLQuery(insertQuery);
	   				
	   				query.executeUpdate();
	              }*/
	            if(taxAmt>0)
	            {
	              String sql="insert into tblkottaxdtl "
	                + "values ('"+tableNo+"','"+kotNo+"',"+KOTAmt+","+taxAmt+")";
	            Query  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	   				
	   				query.executeUpdate();
	            }
	            funUpdateKOT(tableNo,kotNo);
	            //update previous kot details
	          // funUpdatePreviousKOTDetails(posDate,loginPosCode,userCode,clientCode,busyTblNo,listItemDtl,arrKOTNo);
	         
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	     
	 private void funUpdateKOT(String tempTableNO, String KOTNo) 
     {
     try 
       {
           String sql = "update tbltablemaster set strStatus='Occupied' where strTableNo='" + tempTableNO + "'";
           Query  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				
				query.executeUpdate();
       } 
       catch (Exception e) 
       {
          
           e.printStackTrace();
       } 
       finally 
       {
           System.gc();
       }
   }
	 
	 
	/* private void funUpdatePreviousKOTDetails(String posDate,String loginPosCode,String userCode,String clientCode,String tableNo,JSONArray listItemDtl)throws Exception
	    {
	        int cnt=0;
	        String strType = "MVKot";
	        int i=0;
	        if(listItemDtl.length()>0)
	        {
	            String insertQuery = "insert into tblvoidkot(strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity, "
	            + " dblAmount,strWaiterNo,strKOTNo,intPaxNo,strType,strReasonCode, "
	            + " strUserCreated,dteDateCreated,dteVoidedDate,strClientCode,strRemark ) "
	            + " values " ;
	            
	            for (Map.Entry<String, List<String>> entryItemMap : arrKOTNo.entrySet())
                {
	            	List<String> listOfParam = entryItemMap.getValue();
	            	String KOTNo=entryItemMap.getKey();
	            	 for(int j=0;j<listOfParam.size();j++)
	                 {
	            	 String redeemAmt="";
	            	 JSONObject jObj = new JSONObject();
			    	jObj=listItemDtl.getJSONObject(i++);
			    	String itemCode=jObj.getString("strItemCode");
			    	String itemName=jObj.getString("strItemName");
			    	double itemQty=jObj.getDouble("dblItemQuantity");
			    	double itemAmt=jObj.getDouble("dblAmount");
			    	String waiterNo=jObj.getString("strWaiterNo");
			    	String createdDate=jObj.getString("dteDateCreated");
			    	String serialNo=jObj.getString("strSerialNo");
			    	clsSynchDataWithAPOS objAPOS= new clsSynchDataWithAPOS();
			    	 
	                        
	                        String selectQuery="select strItemName,dblItemQuantity,dblAmount,strUserCreated,dteDateCreated,strItemCode "
	                        + " ,strPOSCode,strTableNo,strWaiterNo,strSerialNo,dblRedeemAmt,strCustomerCode "
	                        + " from tblitemrtemp "
	                        + " where strKOTNo='" + KOTNo + "' and strItemCode='"+itemCode+"' ";
	                       Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(selectQuery);
	    	    			
	    	    			List list = query.list();
	    				 
	    					for(int k=0; i<list.size(); i++)
	    					{
	    						Object[] obj=(Object[])list.get(k);
	    						if(itemQty<(double)obj[1])
	                            {
	                                 double qty=(double)obj[1]-itemQty;
	                                 double rate=(double)obj[2]/(double)obj[1];
	                                 double amt=qty*rate;
	                                 String updateQuery = "update tblitemrtemp set dblItemQuantity='"+qty+"' , dblAmount='"+amt+"' where strKOTNo='"+KOTNo+"' and left(strItemCode,7)='"+entryItemMap.getKey()+"' ";
	                                 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(updateQuery);
	                 				
	                 				query.executeUpdate();
	                            }
	                            else
	                            {
	                                String deleteQuery = " delete from tblitemrtemp "
	                                + " where strKOTNo='"+KOTNo+"' and left(strItemCode,7)='"+itemCode+"' ";                  
	                                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(deleteQuery);
	                 				
	                 				query.executeUpdate();
	                            }
	                        
	                        } 
	                       
	                        
	                        if (cnt == 0)
	                        {
	                            insertQuery += " ('" + tableNo + "','" + loginPosCode + "','" + itemCode+ "',"
	                                + "'" + itemName + "','" + itemQty + "','" + itemAmt+ "',"
	                                + "'" + waiterNo + "','" + KOTNo + "','0','" + strType + "','R02',"
	                                + "'" + userCode + "','" + createdDate + "'," + "'" + posDate + "'"
	                                + ",'" + clientCode + "','moved kot') ";
	                        }
	                        else
	                        {
	                            insertQuery += ",('" + tableNo + "','" + loginPosCode+ "','" + itemCode+ "',"
	                                + "'" +itemName + "','" +itemQty + "','" + itemAmt+ "',"
	                                + "'" + waiterNo + "','" + KOTNo + "','0','" + strType + "','R02',"
	                                + "'" + userCode + "','" + createdDate + "'," + "'" + posDate + "'"
	                                + ",'" + clientCode + "','moved kot') ";
	                        } 
	                        cnt++;
	                    }
	                
                }
	            
	            if(cnt>0)
	              {
	            	Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(insertQuery);
     				
     				query.executeUpdate();
	              }
	        }
	        
	           String sql=" select count(strTableNo) from tblitemrtemp "
	                    + " where strTableNo='"+tableNo+"' "
	                    + " and strPOSCode='"+loginPosCode+"' ";                
	          
	           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
   			
   			List list = query.list();
   			int count=0;
			if(list!=null)
				{
	                count=(int)list.get(0);
	            }
	           
	            if(count==0)
	            {
	               sql = "update tbltablemaster set strStatus='Normal' where strTableNo='" + tableNo + "'";
	               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    				
    				query.executeUpdate();
	            }
	            
	      }
	*/
}

