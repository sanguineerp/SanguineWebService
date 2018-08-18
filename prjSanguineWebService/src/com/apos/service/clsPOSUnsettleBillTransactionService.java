package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apos.dao.clsPOSUnsettleBillTransactionDao;
import com.apos.dao.clsZoneMasterDao;
import com.apos.model.clsZoneMasterModel;
import com.apos.model.clsZoneMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsPOSUnsettleBillTransactionService")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSUnsettleBillTransactionService {
	
	
	@Autowired
	private clsPOSUnsettleBillTransactionDao objUnsettleBillTransactionDao;
	
	@Autowired
	private SessionFactory WebPOSSessionFactory;
	
	public String funSaveUnStettleBill(JSONObject jObjUnsettleBill){	
	String BillNo = "";
	StringBuilder sql = new StringBuilder();
	try
	{
		
		BillNo = jObjUnsettleBill.getString("BillNo");
	    String srtReasoneCode = jObjUnsettleBill.getString("ReasonCode");
	    String strReasoneName = jObjUnsettleBill.getString("ReasoneName");
	    String posCode = jObjUnsettleBill.getString("posCode");
	    String user = jObjUnsettleBill.getString("User");
	    String posDate = jObjUnsettleBill.getString("posDate");
	    String clientCode = jObjUnsettleBill.getString("ClientCode");
	    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");

	    
	 
            if(!srtReasoneCode.equalsIgnoreCase(""))
            {
               
               
                sql.setLength(0);
                sql.append("select dteBillDate,dblGrandTotal,strClientCode,strTableNo,strWaiterNo,strAreaCode"
                    + ",strPosCode,strOperationType,strSettelmentMode,intShiftCode "
                    + " from tblbillhd where strBillNo='"+BillNo+"'");
               Query querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
              // querySql.executeUpdate();  
               List listSqlModLive = querySql.list();
	 		   if(listSqlModLive.size()>0)
	 		    {
	 			   int i;
	 		    
	 			   for( i=0 ;i<listSqlModLive.size();i++ )
	 		    
	 		    	{
	 				   Object[] objM = (Object[]) listSqlModLive.get(i);
	 				  
	 		    	   sql.setLength(0);
                   
	 		    	   sql.append("insert into tblvoidbillhd(strPosCode,strReasonCode,strReasonName,strBillNo,"
                        + "dblActualAmount,dteBillDate,strTransType,dteModifyVoidBill,strTableNo,strWaiterNo,"
                        + "intShiftCode,strUserCreated,strUserEdited,strClientCode)"
                        + " values('"+posCode+"','"+srtReasoneCode+"','"+strReasoneName
                        +"','"+BillNo+"','"+objM[1]+"','"+objM[0]
                        +"','USBill','"+dateTime+"','"+objM[3]+"','"+objM[4]
                        +"','"+objM[9]+"','"+user+"','"+user
                        +"','"+clientCode+"')");
	 		    	  querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
	 		    	 querySql.executeUpdate();
                        
	 		    	
	 		    	 
	 		    	 sql.setLength(0);
	 		    	 sql.append("select strBillNo from tblbillsettlementdtl a,tblsettelmenthd b "
                        +" where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='"+BillNo+"' "
                        +" and b.strSettelmentType='Debit Card' ");
                   
	 		    	 querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
	 		        List listSql = querySql.list();
	 	 		   if(listSql.size()>0)
	 	 		    {
	 	 			   for( i=0 ;i<listSqlModLive.size();i++ )
	 	 		       	{
	 	 					 				   
	 	 		    	   sql.setLength(0);
	 		    	
                        sql.append("select strCardNo,dblTransactionAmt,strPOSCode,dteBillDate "
                            + " from tbldebitcardbilldetails where strBillNo='"+BillNo+"' ");
                        
                        
		                       
		                   	 querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		 	 		        List list = querySql.list();
		 	 	 		   if(list.size()>0)
		 	 	 		    {
		 	 	 			   for( i=0 ;i<listSqlModLive.size();i++ )
		 	 	 		    
		 	 	 		    	{	
		 	 	 				
		 	 	 				   Object[] obj = (Object[]) listSqlModLive.get(i);
		 	 	 		    	
		 	 	 				objUnsettleBillTransactionDao.funDebitCardTransaction(BillNo, obj[0].toString(), Double.parseDouble(obj[1].toString()), "Unsettle",posCode,posDate );
		 	 	 				objUnsettleBillTransactionDao.funUpdateDebitCardBalance(obj[0].toString(),Double.parseDouble(obj[1].toString()), "Unsettle");
		                        }//inner for close
		                      
		                    }//inner if close
	 	 		       	}//for close
	 	 		    }//if close
                   
 	 	 		   sql.setLength(0);
                    sql.append("select b.strSettelmentType from tblbillsettlementdtl a,tblsettelmenthd b "
                        +" where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='"+BillNo+"' ");
                    
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    List list1 = querySql.list();
  	 	 		   if(list1.size()>0)
  	 	 		    {
  	 	 			   for( i=0 ;i<listSqlModLive.size();i++ )
  	 	 		    
  	 	 		    	{	
  	 	 				   Object[] obj = (Object[]) listSqlModLive.get(i);
	                        if(obj[0].toString().equals("Complementary"))
	                        {
	                        	objUnsettleBillTransactionDao.funMoveComplimentaryBillToBillDtl(BillNo,obj[6].toString(),obj[5].toString(),obj[7].toString(),clientCode);
	                        }
  	 	 		    	}
                    }
                    
  	 	 	   sql.setLength(0);
                    sql.append("delete from tblbillsettlementdtl where strBillNo='"+BillNo+"'");
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    int unsettleExc=querySql.executeUpdate();
                    
                    sql.setLength(0);          
                    sql.append("update tblbillhd set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    querySql.executeUpdate();
                    
                    sql.setLength(0);
                    sql.append("update tblbilldtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    querySql.executeUpdate();
                         
                    sql.setLength(0); 
                    sql.append("update tblbillmodifierdtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    querySql.executeUpdate();
                            
                    sql.setLength(0);
                    sql.append("update tblbilltaxdtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    querySql.executeUpdate();
                    
                    sql.setLength(0);
                    sql.append("update tblbillseriesbilldtl set strDataPostFlag='N' where strHdBillNo='"+BillNo+"'");
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    querySql.executeUpdate();
                    
                    sql.setLength(0);
                    sql.append("update tblbilldiscdtl set strDataPostFlag='N' where strBillNo='"+BillNo+"'");
                    querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                    querySql.executeUpdate();
                    
                    if(unsettleExc>0)
                    {
                      /*  new frmOkPopUp(this,"Unsettle Successfully", "Success", 1).setVisible(true);
                        */
                    }
	 	 		  
                }//for close
	 		    }//1st sql if close
            }//if close
            else
            {
                //new frmOkPopUp(this, "Please Create Reason First", "Warning", 1).setVisible(true);
            }
        }
        catch(Exception e)
        {
            //clsGlobalVarClass.dbMysql.funRollbackTransaction();
           // objUtility.funWriteErrorLog(e);
            e.printStackTrace();
        }


	return BillNo;
	}

}
