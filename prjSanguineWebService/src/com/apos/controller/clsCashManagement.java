package com.apos.controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsCashManagementBean;
import com.apos.dao.clsSetupDao;

@Controller
@Transactional(value = "webPOSTransactionManager")
public class clsCashManagement {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	@Autowired
	clsSetupDao objSetupDao;
    public Map<String,clsCashManagementBean> funGetCashManagement(String fromDate,String toDate, String POSCode) throws Exception
    {
        Map<String,clsCashManagementBean> hmCashMgmtDtl=new HashMap<String,clsCashManagementBean>();
        StringBuilder sbSql=new StringBuilder();
        sbSql.setLength(0);
        
        StringBuilder sbSqlSale=new StringBuilder();
        Set<String> setUsers=new HashSet<String>();
        sbSqlSale.setLength(0);
        sbSqlSale.append("select time(dteTransDate),a.strUserEdited "
            + " from tblcashmanagement a "
            + " where date(a.dteTransDate) between '"+fromDate+"' and '"+toDate+"' and a.strAgainst='Rolling' "
            + " and a.strPOSCode='"+POSCode+"' "
            + " order by a.strUserEdited ");
        
        Query qRollingEntry=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
        List listRollingEntry=qRollingEntry.list();
        if(listRollingEntry.size()>0)
        {
        
        		for(int i=0;i<listRollingEntry.size();i++)
		        {
        			Object obRoll[]=(Object[])listRollingEntry.get(i);
		            setUsers.add(obRoll[1].toString());
		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) <  '"+obRoll[0].toString()+"' and a.strUserEdited='"+obRoll[1].toString()+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");

		            Query qSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            List listSalesAmt=qSalesAmt.list();
		            if(listSalesAmt.size()>0)
		            {
		            	for(int j=0;j<listSalesAmt.size();j++)
				        {
		        				 Object obj[]=(Object[])listSalesAmt.get(i);
				                 clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
				                if(hmCashMgmtDtl.containsKey(obj[0].toString()))
				                {
				                    objCashMgmtDtl=hmCashMgmtDtl.get(obj[0].toString());
				                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj[1].toString()));
				                    hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
				                }
				                else
				                {
				                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj[1].toString()));
				                    hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
				                }
				            }
		            }
		            
		
		
		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) < '"+obRoll[0].toString()+"' and a.strUserEdited='"+obRoll[1].toString()+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");
		            
		            qSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            listSalesAmt=qSalesAmt.list();
		            if(listSalesAmt.size()>0)
		            {
		            	for(int j=0;j<listSalesAmt.size();j++)
				        {
		        				 Object obj[]=(Object[])listSalesAmt.get(i);
				                 clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
				                if(hmCashMgmtDtl.containsKey(obj[0].toString()))
				                {
				                    objCashMgmtDtl=hmCashMgmtDtl.get(obj[0].toString());
				                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj[1].toString()));
				                    hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
				                }
				                else
				                {
				                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj[1].toString()));
				                    hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
				                }
				            }
		            }
		            
		            Map<String,Double> hmPostRollingSalesAmt=null;
		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) > '"+obRoll[0].toString()+"' and a.strUserEdited='"+obRoll[1].toString()+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");
		            
		            qSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            listSalesAmt=qSalesAmt.list();
		            if(listSalesAmt.size()>0)
		            {
		            	for(int j=0;j<listSalesAmt.size();j++)
				        {
	        				 Object objSales[]=(Object[])listSalesAmt.get(i);
		            		clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
			                if(hmCashMgmtDtl.containsKey(objSales[0].toString()))
			                {
			                    objCashMgmtDtl=hmCashMgmtDtl.get(objSales[0].toString());
			                    hmPostRollingSalesAmt=new HashMap<String,Double>();
			                    if(hmPostRollingSalesAmt.containsKey(obRoll[0].toString()))
			                    {
			                        hmPostRollingSalesAmt.put(obRoll[0].toString(),hmPostRollingSalesAmt.get(obRoll[0].toString())+Double.parseDouble(objSales[1].toString()));
			                    }
			                    else
			                    {
			                        hmPostRollingSalesAmt.put(obRoll[0].toString(),Double.parseDouble(objSales[1].toString()));
			                    }
			                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
			                    hmCashMgmtDtl.put(objSales[0].toString(),objCashMgmtDtl);
			                }
			                else
			                {
			                    hmPostRollingSalesAmt=new HashMap<String,Double>();
			                    hmPostRollingSalesAmt.put(obRoll[0].toString(),Double.parseDouble(objSales[1].toString()));
			                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
			                    hmCashMgmtDtl.put(objSales[0].toString(),objCashMgmtDtl);
			                }
			            }
		            }
			         
		
		
		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) > '"+obRoll[0].toString()+"' and a.strUserEdited='"+obRoll[1].toString()+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");
		   
		            
		            qSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            listSalesAmt=qSalesAmt.list();
		            if(listSalesAmt.size()>0)
		            {
		            	for(int j=0;j<listSalesAmt.size();j++)
				        {
	        				 Object objSales[]=(Object[])listSalesAmt.get(i);
		                        clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
				                if(hmCashMgmtDtl.containsKey(objSales[0].toString()))
				                {
				                    objCashMgmtDtl=hmCashMgmtDtl.get(objSales[0].toString());
				                    hmPostRollingSalesAmt=new HashMap<String,Double>();
				                    if(hmPostRollingSalesAmt.containsKey(obRoll[0].toString()))
				                    {
				                        hmPostRollingSalesAmt.put(obRoll[0].toString(),hmPostRollingSalesAmt.get(obRoll[0].toString())+Double.parseDouble(objSales[1].toString()));
				                    }
				                    else
				                    {
				                        hmPostRollingSalesAmt.put(obRoll[0].toString(),Double.parseDouble(objSales[1].toString()));
				                    }
				                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
				                    hmCashMgmtDtl.put(objSales[0].toString(),objCashMgmtDtl);
				                }
				                else
				                {
				                	hmPostRollingSalesAmt=new HashMap<String,Double>();
				                    hmPostRollingSalesAmt.put(obRoll[0].toString(),Double.parseDouble(objSales[1].toString()));
				                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
				                    hmCashMgmtDtl.put(objSales[0].toString(),objCashMgmtDtl);
				                	
				                }
				            }
				        }
		         }
        }
        
        
        sbSqlSale.setLength(0);
        sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
            + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
            + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
            + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
            + " and a.strPOSCode='"+POSCode+"' "
            + " group by a.strUserEdited");
        
        Query qSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
        List listSalesAmt=qSalesAmt.list();
        if(listSalesAmt.size()>0)
        {
        	for(int j=0;j<listSalesAmt.size();j++)
	        {
				 Object objSalesAmt[]=(Object[])listSalesAmt.get(j);

    	        String user=objSalesAmt[0].toString();
	            if(!setUsers.contains(user))
	            {
	                clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
	                if(hmCashMgmtDtl.containsKey(objSalesAmt[0].toString()))
	                {
	                    objCashMgmtDtl=hmCashMgmtDtl.get(objSalesAmt[0].toString());
	                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(objSalesAmt[1].toString()));
	                    hmCashMgmtDtl.put(objSalesAmt[0].toString(),objCashMgmtDtl);
	                }
	                else
	                {
	                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(objSalesAmt[1].toString()));
	                    hmCashMgmtDtl.put(objSalesAmt[0].toString(),objCashMgmtDtl);
	                }
	            }
	        }
        }	


        sbSqlSale.setLength(0);
        sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
            + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
            + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
            + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
            + " and a.strPOSCode='"+POSCode+"' "
            + " group by a.strUserEdited");
        
         qSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
         listSalesAmt=qSalesAmt.list();
        if(listSalesAmt.size()>0)
        {
        	for(int j=0;j<listSalesAmt.size();j++)
	        {
				 Object objSalesAmt[]=(Object[])listSalesAmt.get(j);

    	        String user=objSalesAmt[0].toString();
	            if(!setUsers.contains(user))
	            {
	                clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
	                if(hmCashMgmtDtl.containsKey(objSalesAmt[0].toString()))
	                {
	                    objCashMgmtDtl=hmCashMgmtDtl.get(objSalesAmt[0].toString());
	                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(objSalesAmt[1].toString()));
	                    hmCashMgmtDtl.put(objSalesAmt[0].toString(),objCashMgmtDtl);
	                }
	                else
	                {
	                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(objSalesAmt[1].toString()));
	                    hmCashMgmtDtl.put(objSalesAmt[0].toString(),objCashMgmtDtl);
	                }
	            }
	        }
        }	
        
        /*
        sbSqlSale.setLength(0);
        sbSqlSale.append("select strUserEdited,sum(dblAdvDeposite) from tbladvancereceipthd "
            + " where strAdvBookingNo not in (select strAdvBookingNo from tblbillhd "
            + " where date(dteBillDate) between '"+fromDate+"' and '"+toDate+"') "
            + " and dtReceiptDate between '"+fromDate+"' and '"+toDate+"' "
            + " group by strUserEdited ");
        ResultSet rsAdvAmt=clsGlobalVarClass.dbMysql.executeResultSet(sbSqlSale.toString());
        while(rsAdvAmt.next())
        {
            clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
            if(hmCashMgmtDtl.containsKey(rsAdvAmt.getString(1)))
            {
                objCashMgmtDtl=hmCashMgmtDtl.get(rsAdvAmt.getString(1));
                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+rsAdvAmt.getDouble(2));
                hmCashMgmtDtl.put(rsAdvAmt.getString(1),objCashMgmtDtl);
            }
            else
            {
                objCashMgmtDtl.setAdvanceAmt(rsAdvAmt.getDouble(2));
                hmCashMgmtDtl.put(rsAdvAmt.getString(1),objCashMgmtDtl);
            }
        }
        rsAdvAmt.close();
        
        
        sbSqlSale.setLength(0);
        sbSqlSale.append("select a.strUserEdited,sum(b.dblAdvDeposite) "
            + " from tblbillhd a,tbladvancereceipthd b "
            + " where a.strAdvBookingNo=b.strAdvBookingNo and b.dtReceiptDate between '"+fromDate+"' and '"+toDate+"' "
            + " group by a.strUserEdited ");
        rsAdvAmt=clsGlobalVarClass.dbMysql.executeResultSet(sbSqlSale.toString());
        while(rsAdvAmt.next())
        {
            clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
            if(hmCashMgmtDtl.containsKey(rsAdvAmt.getString(1)))
            {
                objCashMgmtDtl=hmCashMgmtDtl.get(rsAdvAmt.getString(1));
                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+rsAdvAmt.getDouble(2));
                hmCashMgmtDtl.put(rsAdvAmt.getString(1),objCashMgmtDtl);
            }
            else
            {
                objCashMgmtDtl.setAdvanceAmt(rsAdvAmt.getDouble(2));
                hmCashMgmtDtl.put(rsAdvAmt.getString(1),objCashMgmtDtl);
            }
        }
        rsAdvAmt.close();
                */
        
        
        sbSqlSale.setLength(0);
        sbSqlSale.append("select strUserEdited,sum(dblAdvDeposite) from tbladvancereceipthd "
            + " where dtReceiptDate between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"' "
            + " group by strUserEdited ");
        
        Query qAdvAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
        List listAdvAmt=qAdvAmt.list();
        if(listAdvAmt.size()>0)
        {
        	for(int j=0;j<listAdvAmt.size();j++)
	        {
        			Object objAdvAmt[]=(Object[])listSalesAmt.get(j);
        			clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		            if(hmCashMgmtDtl.containsKey(objAdvAmt[0].toString()))
		            {
		                objCashMgmtDtl=hmCashMgmtDtl.get(objAdvAmt[0].toString());
		                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+Double.parseDouble(objAdvAmt[1].toString()));
		                hmCashMgmtDtl.put(objAdvAmt[0].toString(),objCashMgmtDtl);
		            }
		            else
		            {
		                objCashMgmtDtl.setAdvanceAmt(Double.parseDouble(objAdvAmt[1].toString()));
		                hmCashMgmtDtl.put(objAdvAmt[0].toString(),objCashMgmtDtl);
		            }
		        }
        }
        
        
        sbSqlSale.setLength(0);
        sbSqlSale.append("select strUserEdited,sum(dblAdvDeposite) from tblqadvancereceipthd "
            + " where dtReceiptDate between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"' "
            + " group by strUserEdited ");
        
         qAdvAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
         listAdvAmt=qAdvAmt.list();
        if(listAdvAmt.size()>0)
        {
        	for(int j=0;j<listAdvAmt.size();j++)
	        {
        			Object objAdvAmt[]=(Object[])listSalesAmt.get(j);
        			clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		            if(hmCashMgmtDtl.containsKey(objAdvAmt[0].toString()))
		            {
		                objCashMgmtDtl=hmCashMgmtDtl.get(objAdvAmt[0].toString());
		                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+Double.parseDouble(objAdvAmt[1].toString()));
		                hmCashMgmtDtl.put(objAdvAmt[0].toString(),objCashMgmtDtl);
		            }
		            else
		            {
		                objCashMgmtDtl.setAdvanceAmt(Double.parseDouble(objAdvAmt[1].toString()));
		                hmCashMgmtDtl.put(objAdvAmt[0].toString(),objCashMgmtDtl);
		            }
		        }
        }
     
        
        sbSql.setLength(0);
        sbSql.append("select strUserEdited,strTransType,sum(dblAmount),sum(dblRollingAmt) "
            + " from tblcashmanagement "
            + " where date(dteTransDate) between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"'  "
            + " group by strUserEdited,strTransType "
            + " order by strTransType");
        
        Query qCashMgmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString());
        List listCashMgmt=qCashMgmt.list();
        if(listCashMgmt.size()>0)
        {
        	for(int j=0;j<listCashMgmt.size();j++)
		      {
        		
        		Object obCashMgmt[]=(Object[])listCashMgmt.get(j);
		            double balanceAmt=0;
		            clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		            if(hmCashMgmtDtl.containsKey(obCashMgmt[0].toString()))
		            {
		                objCashMgmtDtl=hmCashMgmtDtl.get(obCashMgmt[0].toString());
		                balanceAmt+=objCashMgmtDtl.getSaleAmt();
		                balanceAmt+=objCashMgmtDtl.getAdvanceAmt();
		                
		                Map<String,Double> hmPostRollingSalesAmt = objCashMgmtDtl.getHmPostRollingSalesAmt();
		                if(null!=hmPostRollingSalesAmt)
		                {
		                    for(Map.Entry<String,Double> entry : hmPostRollingSalesAmt.entrySet())
		                    {
		                        balanceAmt+=entry.getValue();
		                    }
		                }
		
		                if(obCashMgmt[1].toString().equalsIgnoreCase("Float"))
		                {
		                    objCashMgmtDtl.setFloatAmt(objCashMgmtDtl.getFloatAmt()+Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getFloatAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Withdrawal"))
		                {
		                    objCashMgmtDtl.setWithdrawlAmt(objCashMgmtDtl.getWithdrawlAmt()+Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getWithdrawlAmt();
		                    objCashMgmtDtl.setRollingAmt(objCashMgmtDtl.getRollingAmt()+Double.parseDouble(obCashMgmt[3].toString()));
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Refund"))
		                {
		                    objCashMgmtDtl.setRefundAmt(objCashMgmtDtl.getRefundAmt()+Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getRefundAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Payments"))
		                {
		                    objCashMgmtDtl.setPaymentAmt(objCashMgmtDtl.getPaymentAmt()+Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getPaymentAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Transfer In"))
		                {
		                    objCashMgmtDtl.setTransferInAmt(objCashMgmtDtl.getTransferInAmt()+Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getTransferInAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Transfer Out"))
		                {
		                    objCashMgmtDtl.setTransferOutAmt(objCashMgmtDtl.getTransferOutAmt()+Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getTransferOutAmt();
		                }
		                objCashMgmtDtl.setBalanceAmt(balanceAmt);
		                hmCashMgmtDtl.put(obCashMgmt[1].toString(),objCashMgmtDtl);
		            }
		            else
		            {
		                objCashMgmtDtl.setFloatAmt(0);
		                objCashMgmtDtl.setWithdrawlAmt(0);
		                objCashMgmtDtl.setRollingAmt(0);
		                objCashMgmtDtl.setTransferInAmt(0);
		                objCashMgmtDtl.setTransferOutAmt(0);
		                objCashMgmtDtl.setPaymentAmt(0);
		                objCashMgmtDtl.setRefundAmt(0);
		                objCashMgmtDtl.setBalanceAmt(0);
		                objCashMgmtDtl.setSaleAmt(0);
		                balanceAmt+=objCashMgmtDtl.getSaleAmt();
		                balanceAmt+=objCashMgmtDtl.getAdvanceAmt();
		                Map<String,Double> hmPostRollingSalesAmt = objCashMgmtDtl.getHmPostRollingSalesAmt();
		                
		                if(null!=hmPostRollingSalesAmt)
		                {
		                    for(Map.Entry<String,Double> entry : hmPostRollingSalesAmt.entrySet())
		                    {
		                        balanceAmt+=entry.getValue();
		                    }
		                }
		
		                if(obCashMgmt[1].toString().equalsIgnoreCase("Float"))
		                {
		                    objCashMgmtDtl.setFloatAmt(Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getFloatAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Withdrawal"))
		                {
		                    objCashMgmtDtl.setWithdrawlAmt(Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getWithdrawlAmt();
		                    objCashMgmtDtl.setRollingAmt(Double.parseDouble(obCashMgmt[3].toString()));
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Refund"))
		                {
		                    objCashMgmtDtl.setRefundAmt(Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getRefundAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Payments"))
		                {
		                    objCashMgmtDtl.setPaymentAmt(Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getPaymentAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Transfer In"))
		                {
		                    objCashMgmtDtl.setTransferInAmt(Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getTransferInAmt();
		                }
		                else if(obCashMgmt[1].toString().equalsIgnoreCase("Transfer Out"))
		                {
		                    objCashMgmtDtl.setTransferOutAmt(Double.parseDouble(obCashMgmt[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getTransferOutAmt();
		                }
		                objCashMgmtDtl.setBalanceAmt(balanceAmt);
		                hmCashMgmtDtl.put(obCashMgmt[1].toString(),objCashMgmtDtl);
		            }
		        }
        }
        
        
        return hmCashMgmtDtl;
    }
    
    
    public double funGetBalanceUserWise(String fromDate,String toDate,Map<String,clsCashManagementBean> hmCashMgmtDtl,String userCode) throws Exception
    {
        double balanceAmt=0;
        if(hmCashMgmtDtl.containsKey(userCode))
        {
            clsCashManagementBean objCashMgmtDtl=hmCashMgmtDtl.get(userCode);
            balanceAmt=(objCashMgmtDtl.getSaleAmt()+objCashMgmtDtl.getAdvanceAmt()+objCashMgmtDtl.getFloatAmt()+objCashMgmtDtl.getTransferInAmt())-(objCashMgmtDtl.getWithdrawlAmt()+objCashMgmtDtl.getPaymentAmt()+objCashMgmtDtl.getRefundAmt()+objCashMgmtDtl.getTransferOutAmt());
            Map<String,Double> hmPostRollingSalesAmt = objCashMgmtDtl.getHmPostRollingSalesAmt();
            if(null!=hmPostRollingSalesAmt)
            {
                for(Map.Entry<String,Double> entry : hmPostRollingSalesAmt.entrySet())
                {
                    balanceAmt+=entry.getValue();
                }
            }
        }
        return balanceAmt;
    }
    
    
}


