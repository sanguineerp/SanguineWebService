package com.apos.dao;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsReasonMasterModel;
import com.apos.service.clsSetupService;
import com.webservice.util.clsUtilityFunctions;

@Repository("clsPOSRegisterDebitCardDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSRegisterDebitCardDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired
	private clsSetupService objSetupService;
	
	
	double redeemAmt, cardValue, minCharges;
	String custemerCode = "", extCode = "";
	
	
	
	
	public long funGetDebitCardNo()
    {
	long lastNo = 0;
	int cntDelBoyCategory;
	try
	{
	    
	    String sql = "select count(dblLastNo) from tblinternal where strTransactionType='CardNo'";
	    
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    List list = query.list();
	   
	    cntDelBoyCategory = Integer.parseInt(list.get(0).toString());
	   
	    if (cntDelBoyCategory > 0) {
	    	
	    	
            sql = "select dblLastNo from tblinternal where strTransactionType='CardNo'";
            
            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            list = query.list();
            long code =Long.parseLong(list.get(0).toString());
            code = code + 1;
            lastNo = code;
           
        }
    	else 
    	{
            lastNo = 1;
        }
	   
	    String updateSql = "update tblinternal set dblLastNo=" + lastNo + " "
	            + "where strTransactionType='CardNo'";
	    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(updateSql);
	    query.executeUpdate();

	       
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return lastNo;
    }
	
	@SuppressWarnings("finally")
	public JSONObject funRegisterCard(JSONObject jObjRegisterDebitCardMaster) {
		String cardStatus = "Active";
		JSONObject jOBjRet = new JSONObject();
		JSONArray jArr = new JSONArray();
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			String CardTypeCode = jObjRegisterDebitCardMaster.getString("CardTypeCode");
			
			
			if (cardStatus.equals("Active")) // for register card
			{
				// live
				sqlBuilder.setLength(0);
				sqlBuilder
						.append("select dblCardValueFixed,dblMinCharge,right(strCardTypeCode,3) "
								+ "from tbldebitcardtype "
								+ "where strCardTypeCode='"+CardTypeCode+ "'");
				redeemAmt = 0.0;
				 String cardTypeCode="";
				Query querySqlLiveData = webPOSSessionFactory
						.getCurrentSession().createSQLQuery(
								sqlBuilder.toString());
				List listSqlLiveData = querySqlLiveData.list();
				if (listSqlLiveData.size() > 0) {

					for (int i = 0; i < listSqlLiveData.size(); i++) {
						Object[] obj = (Object[]) listSqlLiveData.get(i);
						
						cardValue=Double.parseDouble(obj[0].toString());
						minCharges=Double.parseDouble(obj[1].toString());
						
						 redeemAmt = redeemAmt - (cardValue + minCharges);
						 cardTypeCode=obj[2].toString();
					}
				}
				long lastNo=funGetDebitCardNo();
	            String cardNo=cardTypeCode+String.format("%06d", lastNo);
	        	String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
	        	String user = jObjRegisterDebitCardMaster.getString("User");
				String clientCode = jObjRegisterDebitCardMaster.getString("ClientCode");
				String customerName = jObjRegisterDebitCardMaster.getString("CustomerName");
				String cardString = jObjRegisterDebitCardMaster.getString("CardString");
				String posCode = jObjRegisterDebitCardMaster.getString("POSCode");
				
	           String sql = "insert into tbldebitcardmaster (strCardTypeCode,strCardNo,dblRedeemAmt,strStatus,"
	                    + "strUserCreated,dteDateCreated,strCustomerCode,strDataPostFlag,strClientCode,strCardString) "
	                    + "values('" + CardTypeCode + "','" + cardNo + "','" + redeemAmt + "','" + cardStatus + "'"
	                    + ",'" + user + "','"+ dateTime+ "'"
	                    + ",'" + customerName + "','N','" + clientCode+ "','"+cardString+"')";
	               
	              Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	        	    query.executeUpdate(); 

	        	    JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gRFIDInterface"); 
	        	    if(objSetupParameter.get("gRFIDInterface").toString().equalsIgnoreCase("Y"))
	        	     {
	                    // Post Registered Debit Card from JPOS to RMS
	                    int rows = funPostDebitCardInfoToRMS(jObjRegisterDebitCardMaster);
	                    if (rows > 0) 
	                    {
	                        
	                    } else {
	                        //clsGlobalVarClass.dbMysql.funRollbackTransaction();
	                    }
	                } else {
	                    //clsGlobalVarClass.dbMysql.funCommitTransaction();
	                    
	                }
	        	    jOBjRet.put("CardString", cardString);
			}
			

		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally
		{
			return jOBjRet;
		}
		

	}



@SuppressWarnings("finally")
public JSONObject funDelistCard(JSONObject jObjRegisterDebitCardMaster) {
	String cardStatus = "";
	JSONObject jOBjRet = new JSONObject();
	JSONArray jArr = new JSONArray();
	
	
	
	try {
		StringBuilder sqlBuilder = new StringBuilder();
		String CardTypeCode = jObjRegisterDebitCardMaster.getString("CardTypeCode");
		String operation = jObjRegisterDebitCardMaster.getString("CardOperation");
		String cardString = jObjRegisterDebitCardMaster.getString("CardString");
		String clientCode = jObjRegisterDebitCardMaster.getString("ClientCode");
		String posCode = jObjRegisterDebitCardMaster.getString("POSCode");
		
		
		
		 if (operation.toString().equalsIgnoreCase("Register")) {
	            cardStatus = "Active";
	        } else {
	            cardStatus = "Deactive";
	        }
		 String cardNoToDelist = "";
		 
		 cardNoToDelist = "select count(*) from tbldebitcardmaster "
		            + "where strCardString='" + cardString + "'and strStatus='Active'";
		 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(cardNoToDelist);
		 List listSqlLiveData = query.list();
			if (listSqlLiveData.size() > 0) 
			{
		int cn1=Integer.parseInt(listSqlLiveData.get(0).toString());
		if (cn1 > 0)
		{
			String sql = "update tbldebitcardmaster set strStatus='" + cardStatus + "' "
                    + "where strCardString='" + cardString + "'";
			 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			    query.executeUpdate();
			    
                
			    
			    JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gRFIDInterface");
			    
			    if(objSetupParameter.get("gRFIDInterface").toString().equalsIgnoreCase("Y"))
                {
                    if (funDelistDebitCardFromRMS(jObjRegisterDebitCardMaster) > 0) 
                    {
                    } 
                    else 
                    {
                        //clsGlobalVarClass.dbMysql.funRollbackTransaction();
                    }
                    
                }
            }
		
		
		jOBjRet.put("CardString", cardString);
		}
			}	
 
	catch (Exception ex) {
		ex.printStackTrace();
	}
	finally
	{
		return jOBjRet;
	}
	
	

}


@SuppressWarnings("finally")
public int funPostDebitCardInfoToRMS(JSONObject jObjRegisterDebitCardMaster) 
{
	
     String status = "E";
     int insertedRows = 0;	
     try {
    	
        // String rmsConURL = "jdbc:sqlserver://" + "Y"+ ":1433;user=" + clsGlobalVarClass.gRFIDDBUserName + ";password=" + clsGlobalVarClass.gRFIDDBPassword + ";database=" + clsGlobalVarClass.gRFIDDBName + "";
    	 String cardString = jObjRegisterDebitCardMaster.getString("CardString");
    	 
         String sql = "select strCustomerCode,strStatus from tbldebitcardmaster where strCardString='" + cardString + "'";
         
         Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
         List listSqlLiveData = query.list();
         if (listSqlLiveData.size() > 0) 
         {
             if (listSqlLiveData.get(1).equals("Active")) 
             {
                 status = "A";
             } 
             else
             {
                 status = "E";
             }
             sql = "insert into tblCustomerDebitCard(strCustomerCode,strDebitCardString,strStatus) "
                     + "values('" + extCode + "','" + cardString + "','" + status + "')";
             query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
             insertedRows =  query.executeUpdate();
			    
         }
         
     }
     catch (Exception e)
     { 
         e.printStackTrace();
     } 
     finally 
     { 
         return insertedRows;
     }
}


@SuppressWarnings("finally")
public int funDelistDebitCardFromRMS(JSONObject jObjRegisterDebitCardMaster) 
{
	
    
     int updatedRows = 0;
     try {
    	
        String cardString = jObjRegisterDebitCardMaster.getString("CardString");
    	 
       //  String rmsConURL = "jdbc:sqlserver://" + clsGlobalVarClass.gRFIDDBServerName + ":1433;user=" + clsGlobalVarClass.gRFIDDBUserName + ";password=" + clsGlobalVarClass.gRFIDDBPassword + ";database=" + clsGlobalVarClass.gRFIDDBName + "";
        String sql = "update tblcustomerdebitcard set strStatus='E' where strDebitCardString='" + cardString + "'";
        Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
         updatedRows =  query.executeUpdate();
        
         
     }
     catch (Exception e)
     { 
         e.printStackTrace();
     } 
     finally 
     { 
         return updatedRows;
     }
}

}
