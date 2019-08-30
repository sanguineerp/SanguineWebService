package com.apos.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsCashManagementBean;
import com.apos.model.clsPOSCashManagmentTranscationModel;

@Repository("clsPOSCashManagmentTranscationDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSCashManagmentTranscationDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

		public String funSavePOSCashManagmentTranscation(clsPOSCashManagmentTranscationModel objModel) throws Exception
		{
			webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
			return objModel.getStrTransID();
		}
		 public String funGenerateCashManagementCode() throws Exception
		    {
				String customerAreaCode = "";
				String sql = "select ifnull(max(strTransID),0) from tblcashmanagement";
				Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List list = query.list();
				
				if (!list.get(0).toString().equals("0"))
				{
				    String strCode = "00";
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
				    	customerAreaCode = "TR0000" + intCode;
				    }
				    else if (intCode < 100)
				    {
				    	customerAreaCode = "TR000" + intCode;
				    }
				    else if (intCode < 1000)
				    {
				    	customerAreaCode = "TR00" + intCode;
				    }
				    else if (intCode < 10000)
				    {
				    	customerAreaCode = "TR0" + intCode;
				    }
							   
				   
				}
				else
				{
					customerAreaCode = "TR00001";
				}
				return customerAreaCode;
		    }
		
		 public boolean funCheckUserEntryForRolling(String User, String Date) throws Exception
		 {
			 boolean flgResult=false;
		        String sql="select strTransID from tblcashmanagement "
		            + " where strUserCreated='"+User+"' and strAgainst='Rolling' "
		            + "and date(dteTransDate)='"+Date+"'";
		        //System.out.println(sql);
		    	Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		    	List list = query.list();
		        if(list.size()>0)
		        {
		            flgResult=true;
		            
		        }
	
		        
		        return flgResult;
		 }
		 
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
		        Query rsRollingEntry= webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		        List list = rsRollingEntry.list();
		        for(int i=0 ;i<list.size();i++ )
	 	    	{
		        	Object[] obj = (Object[]) list.get(i);
		            setUsers.add(obj[1].toString());
		           
		            
		            
		            
		            
		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) <  '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");
		           
		            
		            
		            
		            Query rsSalesAmt= webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            List listrsSalesAmt = rsSalesAmt.list();
		            for(int i1=0 ;i1<listrsSalesAmt.size();i1++ )
		 	    	{
			        	Object[] obj1 = (Object[]) listrsSalesAmt.get(i1);
			        	
			        	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		                if(hmCashMgmtDtl.containsKey(obj1[0]))
		                {
		                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
		                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		                else
		                {
		                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj[1].toString()));
		                    hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
		                }
		            }
		      


		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) < '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");
		           
		            rsSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            List listrsSalesAmt1 = rsSalesAmt.list();
		            for(int i1=0 ;i1<listrsSalesAmt1.size();i1++ )
		 	    	{
			        	Object[] obj1 = (Object[]) listrsSalesAmt1.get(i1);
			        	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		                if(hmCashMgmtDtl.containsKey(obj1[0]))
		                {
		                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
		                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		                else
		                {
		                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj1[1].toString()));
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		            }
		         
		            
		            Map<String,Double> hmPostRollingSalesAmt=null;
		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) > '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");
		           
		            rsSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            List listrsSalesAmt2 = rsSalesAmt.list();
		            for(int i1=0 ;i1<listrsSalesAmt2.size();i1++ )
		 	    	{
			        	Object[] obj1 = (Object[]) listrsSalesAmt2.get(i1);
			        	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		                if(hmCashMgmtDtl.containsKey(obj1[0]))
		                {
		                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
		                    hmPostRollingSalesAmt=new HashMap<String,Double>();
		                    if(hmPostRollingSalesAmt.containsKey(obj1[1]))
		                    {
		                        hmPostRollingSalesAmt.put(obj[0].toString(),hmPostRollingSalesAmt.get(obj[0])+Double.parseDouble(obj1[1].toString()));
		                    }
		                    else
		                    {
		                        hmPostRollingSalesAmt.put(obj[0].toString(),Double.parseDouble(obj1[1].toString()));
		                    }
		                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		                else
		                {
		                    hmPostRollingSalesAmt=new HashMap<String,Double>();
		                    hmPostRollingSalesAmt.put(obj[0].toString(),Double.parseDouble(obj1[1].toString()));
		                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		            }
		           


		            sbSqlSale.setLength(0);
		            sbSqlSale.append("select a.strUserEdited,sum(b.dblSettlementAmt) "
		                + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
		                + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
		                + " and c.strSettelmentType='Cash' and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		                + " and time(a.dteBillDate) > '"+obj[0]+"' and a.strUserEdited='"+obj[1]+"' "
		                + " and a.strPOSCode='"+POSCode+"' "
		                + " group by a.strUserEdited");
		            
		            rsSalesAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		            List listrsSalesAmt3 = rsSalesAmt.list();
		            for(int i1=0 ;i1<listrsSalesAmt3.size();i1++ )
		 	    	{
			        	Object[] obj1 = (Object[]) listrsSalesAmt3.get(i1);
			        	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		                if(hmCashMgmtDtl.containsKey(obj1[0]))
		                {
		                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
		                    hmPostRollingSalesAmt=new HashMap<String,Double>();
		                    if(hmPostRollingSalesAmt.containsKey(obj[0]))
		                    {
		                        hmPostRollingSalesAmt.put(obj[0].toString(),hmPostRollingSalesAmt.get(obj1[0])+Double.parseDouble(obj1[1].toString()));
		                    }
		                    else
		                    {
		                        hmPostRollingSalesAmt.put(obj1[0].toString(),Double.parseDouble(obj1[1].toString()));
		                    }
		                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		                else
		                {
		                    hmPostRollingSalesAmt=new HashMap<String,Double>();
		                    hmPostRollingSalesAmt.put(obj[0].toString(),Double.parseDouble(obj1[1].toString()));
		                    objCashMgmtDtl.setHmPostRollingSalesAmt(hmPostRollingSalesAmt);
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
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
		       
		        Query rsSalesAmt1=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		        List listrsSalesAmt3 = rsSalesAmt1.list();
	            for(int i1=0 ;i1<listrsSalesAmt3.size();i1++ )
	 	    	{
		        	Object[] obj1 = (Object[]) listrsSalesAmt3.get(i1);
		            
		        	String user=obj1[0].toString();
		            if(!setUsers.contains(user))
		            {
		            	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		                if(hmCashMgmtDtl.containsKey(obj1[0]))
		                {
		                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
		                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		                else
		                {
		                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj1[1].toString()));
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
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
		        
		        rsSalesAmt1=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		        List listrsSalesAmt4 = rsSalesAmt1.list();
	            for(int i1=0 ;i1<listrsSalesAmt4.size();i1++ )
	 	    	{
		        	Object[] obj1 = (Object[]) listrsSalesAmt4.get(i1);
		            String user=obj1[0].toString();
		            if(!setUsers.contains(user))
		            {
		            	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		                if(hmCashMgmtDtl.containsKey(obj1[0]))
		                {
		                    objCashMgmtDtl=hmCashMgmtDtl.get(obj1[0]);
		                    objCashMgmtDtl.setSaleAmt(objCashMgmtDtl.getSaleAmt()+Double.parseDouble(obj1[1].toString()));
		                    hmCashMgmtDtl.put(obj1[0].toString(),objCashMgmtDtl);
		                }
		                else
		                {
		                    objCashMgmtDtl.setSaleAmt(Double.parseDouble(obj1[1].toString()));
		                    hmCashMgmtDtl.put(obj1[1].toString(),objCashMgmtDtl);
		                }
		            }
		        }
		
		       
		        
		        
		        sbSqlSale.setLength(0);
		        sbSqlSale.append("select strUserEdited,sum(dblAdvDeposite) from tbladvancereceipthd "
		            + " where dtReceiptDate between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"' "
		            + " group by strUserEdited ");
		        Query rsAdvAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		        List listrsAdvAmt = rsSalesAmt1.list();
	            for(int i1=0 ;i1<listrsAdvAmt.size();i1++ )
	 	    	{
		        	Object[] obj = (Object[]) listrsAdvAmt.get(i1);
		        	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		            if(hmCashMgmtDtl.containsKey(obj[0]))
		            {
		                objCashMgmtDtl=hmCashMgmtDtl.get(obj[0]);
		                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+Double.parseDouble(obj[1].toString()));
		                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
		            }
		            else
		            {
		                objCashMgmtDtl.setAdvanceAmt(Double.parseDouble(obj[1].toString()));
		                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
		            }
		        }
		       
		        
		        
		        sbSqlSale.setLength(0);
		        sbSqlSale.append("select strUserEdited,sum(dblAdvDeposite) from tblqadvancereceipthd "
		            + " where dtReceiptDate between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"' "
		            + " group by strUserEdited ");
		        rsAdvAmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSqlSale.toString());
		        List listrsAdvAmt1 = rsSalesAmt1.list();
	            for(int i1=0 ;i1<listrsAdvAmt1.size();i1++ )
	 	    	{
		        	Object[] obj = (Object[]) listrsAdvAmt1.get(i1);
		        	clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		            if(hmCashMgmtDtl.containsKey(obj[0]))
		            {
		                objCashMgmtDtl=hmCashMgmtDtl.get(obj[0]);
		                objCashMgmtDtl.setAdvanceAmt(objCashMgmtDtl.getAdvanceAmt()+Double.parseDouble(obj[1].toString()));
		                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
		            }
		            else
		            {
		                objCashMgmtDtl.setAdvanceAmt(Double.parseDouble(obj[1].toString()));
		                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
		            }
		        }
		       
		        
		        
		        sbSql.setLength(0);
		        sbSql.append("select strUserEdited,strTransType,sum(dblAmount),sum(dblRollingAmt) "
		            + " from tblcashmanagement "
		            + " where date(dteTransDate) between '"+fromDate+"' and '"+toDate+"' and strPOSCode='"+POSCode+"'  "
		            + " group by strUserEdited,strTransType "
		            + " order by strTransType");
		        
		        Query rsCashMgmt=webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString());
		        List listrsCashMgmt = rsCashMgmt.list();
	            for(int i1=0 ;i1<listrsCashMgmt.size();i1++ )
	 	    	{
		        	Object[] obj = (Object[]) listrsCashMgmt.get(i1);
		            double balanceAmt=0;
		            clsCashManagementBean objCashMgmtDtl=new clsCashManagementBean();
		            if(hmCashMgmtDtl.containsKey(obj[0]))
		            {
		                objCashMgmtDtl=hmCashMgmtDtl.get(obj[0]);
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

		                if(((String) obj[1]).equalsIgnoreCase("Float"))
		                {
		                    objCashMgmtDtl.setFloatAmt(objCashMgmtDtl.getFloatAmt()+Double.parseDouble(obj[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getFloatAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Withdrawl"))
		                {
		                    objCashMgmtDtl.setWithdrawlAmt(objCashMgmtDtl.getWithdrawlAmt()+Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getWithdrawlAmt();
		                    objCashMgmtDtl.setRollingAmt(objCashMgmtDtl.getRollingAmt()+Double.parseDouble(obj[3].toString()));
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Refund"))
		                {
		                    objCashMgmtDtl.setRefundAmt(objCashMgmtDtl.getRefundAmt()+Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getRefundAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Payments"))
		                {
		                    objCashMgmtDtl.setPaymentAmt(objCashMgmtDtl.getPaymentAmt()+Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getPaymentAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Transfer In"))
		                {
		                    objCashMgmtDtl.setTransferInAmt(objCashMgmtDtl.getTransferInAmt()+Double.parseDouble(obj[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getTransferInAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Transfer Out"))
		                {
		                    objCashMgmtDtl.setTransferOutAmt(objCashMgmtDtl.getTransferOutAmt()+Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getTransferOutAmt();
		                }
		                objCashMgmtDtl.setBalanceAmt(balanceAmt);
		                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
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

		                if(obj[1].toString().equalsIgnoreCase("Float"))
		                {
		                    objCashMgmtDtl.setFloatAmt(Double.parseDouble(obj[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getFloatAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Withdrawl"))
		                {
		                    objCashMgmtDtl.setWithdrawlAmt(Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getWithdrawlAmt();
		                    objCashMgmtDtl.setRollingAmt(Double.parseDouble(obj[3].toString()));
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Refund"))
		                {
		                    objCashMgmtDtl.setRefundAmt(Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getRefundAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Payments"))
		                {
		                    objCashMgmtDtl.setPaymentAmt(Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getPaymentAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Transfer In"))
		                {
		                    objCashMgmtDtl.setTransferInAmt(Double.parseDouble(obj[2].toString()));
		                    balanceAmt+=objCashMgmtDtl.getTransferInAmt();
		                }
		                else if(obj[1].toString().equalsIgnoreCase("Transfer Out"))
		                {
		                    objCashMgmtDtl.setTransferOutAmt(Double.parseDouble(obj[2].toString()));
		                    balanceAmt-=objCashMgmtDtl.getTransferOutAmt();
		                }
		                objCashMgmtDtl.setBalanceAmt(balanceAmt);
		                hmCashMgmtDtl.put(obj[0].toString(),objCashMgmtDtl);
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
