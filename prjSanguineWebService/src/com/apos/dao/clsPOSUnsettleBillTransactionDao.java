package com.apos.dao;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.bean.clsTaxCalculationBean;
import com.apos.controller.clsUtilityController;
import com.apos.model.clsZoneMasterModel;
import com.webservice.util.clsUtilityFunctions;


@Repository("clsPOSUnsettleBillTransactionDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSUnsettleBillTransactionDao {
	
	@Autowired
	private SessionFactory webPOSSessionFactory;

		
		  public int funDebitCardTransaction(String billNo, String debitCardNo, double debitCardSettleAmt, String transType, String posCode, String posDate)
		    {
		        try
		        {
		            String delete = "delete from tbldebitcardbilldetails "
		                    + "where strBillNo='" + billNo + "' and strTransactionType='" + transType + "' ";
		          Query  querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(delete.toString());
		          querySql.executeUpdate(); 
		        

		            String sqlDebitCardDetials = "insert into tbldebitcardbilldetails (strBillNo,strCardNo,"
		                    + "dblTransactionAmt,strPOSCode,dteBillDate,strTransactionType)"
		                    + "values ('" + billNo + "','" + debitCardNo + "','" + debitCardSettleAmt + "'"
		                    + ",'" +posCode + "','" + posDate + "'"
		                    + ",'" + transType + "')";
		             querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDebitCardDetials.toString());
			          querySql.executeUpdate(); 
			        
		        }
		        catch (Exception e)
		        {
		            /*funWriteErrorLog(e);
		            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code: BS-69", JOptionPane.ERROR_MESSAGE);*/
		            e.printStackTrace();
		        }
		        return 1;
		    }
		
		    public int funUpdateDebitCardBalance(String debitCardNo, double debitCardSettleAmt, String transType)
		    {
		        try
		        {
		            String sql = "select dblRedeemAmt from tbldebitcardmaster "
		                    + "where strCardNo='" + debitCardNo + "'";
		          Query  querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		          List listSqlModLive = querySql.list();
		 		   if(listSqlModLive.size()>0)
		 		    {		 			  
		 		    
		 			   for(int i=0 ;i<listSqlModLive.size();i++ )
		 		    
		 		    	{
		 				   Object[] obj = (Object[]) listSqlModLive.get(i);
		 				  
		                double amt = Double.parseDouble(obj[1].toString());
		                double updatedBal = amt - debitCardSettleAmt;
		                if (transType.equals("Unsettle"))
		                {
		                    updatedBal = amt + debitCardSettleAmt;
		                }
		                sql = "update tbldebitcardmaster set dblRedeemAmt='" + updatedBal + "' "
		                        + "where strCardNo='" + debitCardNo + "'";
		                
		                querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		                querySql.executeUpdate(); 
		            }
		 		    }
		        }
		        catch (Exception e)
		        {
		            /*funWriteErrorLog(e);
		            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code: BS-70", JOptionPane.ERROR_MESSAGE);*/
		            e.printStackTrace();
		        }
		        return 1;
		    }
		    
		    public int funMoveComplimentaryBillToBillDtl(String billNo,String POSCode,String billAreaCode, String operationTypeForTax, String clientCode) throws Exception
		    	    {
		    	        String sqlDelete = "delete from tblbilldtl where strBillNo='" + billNo + "'";
		    	       Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete.toString());
		                querySql.executeUpdate(); 
		    	        
		    	        String sqlInsertBillComDtl = "insert into tblbilldtl "
		    	                + " select * from tblbillcomplementrydtl where strBillNo='"+billNo+"' ";
		    	        querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertBillComDtl.toString());
		                querySql.executeUpdate(); 
		    	        
		    	        String sql=" select strItemCode,strItemName,dblAmount,dblDiscountAmt "
		    	                + "from tblbilldtl where strBillNo='"+billNo+"' ";
		    	        double subTotal=0.0;
		    	        double disTotal=0.0;
		    	    
		    	        List<clsPOSItemDetailFrTaxBean> arrListItemDtls=new ArrayList<clsPOSItemDetailFrTaxBean>();
		    	        querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
				          List listSqlModLive = querySql.list();
				          	if(listSqlModLive.size()>0)
				 		    {		 			  
				 		    
				 			   for(int i=0 ;i<listSqlModLive.size();i++ )
				 		    
				 		    	{
				 				   Object[] obj = (Object[]) listSqlModLive.get(i);
				 				  
				 				  clsPOSItemDetailFrTaxBean objItemDtl=new clsPOSItemDetailFrTaxBean();
				    	            objItemDtl.setItemCode(obj[0].toString());
				    	            objItemDtl.setItemName(obj[1].toString());
				    	            objItemDtl.setAmount(Double.parseDouble(obj[2].toString()));
				    	            objItemDtl.setDiscAmt(Double.parseDouble(obj[3].toString()));
				    	            subTotal+=Double.parseDouble(obj[2].toString());
				    	            disTotal+=Double.parseDouble(obj[3].toString());
				    	            arrListItemDtls.add(objItemDtl);
				 		    	}
				 		    }
		    	       
				 			   
				 	    double disper=0.00;
		    	        if(subTotal>0)
		    	        {
		    	            disper=(disTotal/subTotal)*100;
		    	        }
		    	        clsUtilityController obj=new clsUtilityController();
		    	        List<clsTaxCalculationBean> arrListTaxCal = obj.funCalculateTax(arrListItemDtls,POSCode,"",billAreaCode,operationTypeForTax,0,0,"Tax Regen","Cash");
		    	        
		    	        
		    	        sqlDelete = "delete from tblbilltaxdtl where strBillNo='" +billNo + "'";
		    	        double taxAmt=0.0;
		    	        querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete.toString());
		    	        querySql.executeUpdate(); 
		    	       
		    	        for(clsTaxCalculationBean objTaxCalDtl : arrListTaxCal)
		    	        {            
		    	            String sqlInsertTaxDtl = "insert into tblbilltaxdtl "
		    	                + "(strBillNo,strTaxCode,dblTaxableAmount,dblTaxAmount,strClientCode) "
		    	                + "values('" + billNo + "','" + objTaxCalDtl.getTaxCode()+ "'"
		    	                + "," + objTaxCalDtl.getTaxableAmount() + "," + objTaxCalDtl.getTaxAmount() + ""
		    	                + ",'" + clientCode + "')";
		    	            taxAmt+=objTaxCalDtl.getTaxAmount();
		    	            querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertTaxDtl.toString());
			    	        querySql.executeUpdate(); 
		    	        }
		    	        double grandTotal=((subTotal+taxAmt)-disTotal);
		    	      
		    	        sql="update tblbillhd set dblDiscountAmt='"+disTotal+"',dblDiscountPer='"+disper+"',"
		    	            + "dblTaxAmt='"+taxAmt+"',dblSubTotal='"+subTotal+"',dblGrandTotal='"+grandTotal+"' "
		    	            + "where strBillNo='"+billNo+"'";
		    	        querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		    	        querySql.executeUpdate(); 
		    	        
		    	        sqlDelete = "delete from tblbillcomplementrydtl where strBillNo='" + billNo + "'";
		    	        querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete.toString());
		    	        querySql.executeUpdate(); 
		    	        
		    	        return 0;
		    	    }
}
