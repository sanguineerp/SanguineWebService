package com.webservice.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.webservice.controller.clsDatabaseConnection;

public class clsSalesFlashReport 
{
    public boolean funProcessSalesFlashReport(String liveTableQuery, String qFileTableQuery, String reportName,String userCode) throws Exception
    {
        switch(reportName)
        {
             case "SettlementWiseSales":
                funSettlementWiseSales(liveTableQuery, qFileTableQuery,userCode);
                break;
            
            case "BillWiseSales":
                funBillWiseSales(liveTableQuery, qFileTableQuery,userCode);
                break;
                
            case "MenuHeadWiseSales":
                funMenuHeadWiseSales(liveTableQuery, qFileTableQuery,userCode);
                break;
                
                
           /* case "DeliveryBoyWiseSales":
                funDeliveryBoyWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "CostCenterWiseSales":
                funCostCenterWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "SubGroupWiseSales":
                funSubGroupWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "GroupWiseSales":
                funGroupWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
           
                
            case "WaiterWiseSales":
                funWaiterWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "OperatorWiseSales":
                funOperatorWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "TaxWiseSales":
                funTaxWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "HomeDeliveryWiseSales":
                funHomeDeliveryWiseSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "HourWiseItemSales":
                funHourWiseItemSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "ModifierWiseSales":
                funModifierWiseSales(liveTableQuery, qFileTableQuery);
                break;

            case "CustWiseBillSales":
                funCustWiseBillSales(liveTableQuery, qFileTableQuery);
                break;
                
            case "CustWiseItemSales":
                funCustWiseItemSales(liveTableQuery, qFileTableQuery);
                break;
                */
                
           
        }
        return true;
    }
    
    
   /* public boolean funInsertDataToTempSalesFlash(String liveTableQuery1, String qFileTableQuery1,String liveTableQuery2, String qFileTableQuery2, String reportName) throws Exception
    {
        switch(reportName)
        {
            case "ComplementorySales":
                funComplementorySales(liveTableQuery1, qFileTableQuery1, liveTableQuery2, qFileTableQuery2);
                break;
        }
        return true;
    }
    
    */
    private void funBillWiseSales(String liveTableQuery, String qFileTableQuery,String userCode) throws Exception
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1=null;
		Statement st2=null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "master");
		st = cmsCon.createStatement();
		st1= cmsCon.createStatement();
		st2= cmsCon.createStatement();
		st.executeUpdate("delete from tbltempsalesflash1 where strUser='"+userCode+"';");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1("+qFileTableQuery+");";
        System.out.println(sqlInsertLiveBillSales);   
        System.out.println(sqlInsertQFileBillSales);  
        st.executeUpdate(sqlInsertLiveBillSales);
        st2.executeUpdate(sqlInsertQFileBillSales);
                
        String billNo="";
        String sql="select * from tbltempsalesflash1 "
            + " where strbillno in (select strBillNo "
            + " from tbltempsalesflash1 group by strbillno having count(*) > 1) ";
        System.out.println(sql);  
        ResultSet rsTemp= st.executeQuery(sql);
        while(rsTemp.next())
        {
            if(rsTemp.getString(1).equals(billNo))
            {
                String sqlUpdate="update tbltempsalesflash1 "
                    + " set dblsubtotal='0',dbldiscper='0',dbldiscamt='0'"
                    + " ,dbltaxamt='0',dbltipamt='0' "
                    + " where strUser='"+userCode+"' "
                    + " and strBillNo='"+billNo+"' and strpaymode='"+rsTemp.getString(6)+"' ";
                System.out.println(sqlUpdate);  
                st1.executeUpdate(sqlUpdate);
            }
            billNo=rsTemp.getString(1);
        }
        rsTemp.close();
        st.close();
		st1.close();
		st2.close();
		cmsCon.close();
    }
    
    
    private void funSettlementWiseSales(String liveTableQuery, String qFileTableQuery,String userCode) throws Exception
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1=null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "master");
		st = cmsCon.createStatement();
		st1 = cmsCon.createStatement();
        st.executeUpdate("delete from tbltempsalesflash1 where strUser='"+userCode+"';");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,struser) "
            + "("+qFileTableQuery+");";
        st.executeUpdate(sqlInsertLiveBillSales);
        st1.executeUpdate(sqlInsertQFileBillSales);
        st.close();
        st1.close();
		cmsCon.close();
        
    }
    
    private void funMenuHeadWiseSales(String liveTableQuery, String qFileTableQuery,String userCode) throws Exception
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
    	Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "master");
		st = cmsCon.createStatement();
		st1 = cmsCon.createStatement();
		 //  System.out.println("Live=" +liveTableQuery);
        //   System.out.println("Qfile=" +qFileTableQuery);
		 st.executeUpdate("delete from tbltempsalesflash where strUser='"+userCode+"';");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash "
            + "("+liveTableQuery+");";
       
        String sqlInsertQFileBillSales="insert into tbltempsalesflash "
            + "("+qFileTableQuery+");";
        st.executeUpdate(sqlInsertLiveBillSales);
        st1.executeUpdate(sqlInsertQFileBillSales);
        //System.out.println("liveInser="+sqlInsertLiveBillSales);
       // System.out.println("QfileInser="+sqlInsertQFileBillSales);
        st.close();
        st1.close();
		cmsCon.close();
    }
    
 
    
  /*  private void funModifierWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
    	Connection cmsCon = null;
		Statement st = null;
		Statement st1=null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "master");
		st = cmsCon.createStatement();
		st.executeUpdate("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,struser) "
            + "("+qFileTableQuery+");";
        st.executeUpdate(sqlInsertLiveBillSales);
        st.executeUpdate(sqlInsertQFileBillSales);
        st.close();
      	cmsCon.close();
    }
    */
    
  /*  private void funCustWiseBillSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
    	Connection cmsCon = null;
		Statement st = null;
		Statement st1=null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "master");
		st = cmsCon.createStatement();
		st.executeUpdate("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
            + "("+qFileTableQuery+");";
        st.executeUpdate(sqlInsertLiveBillSales);
        st.executeUpdate(sqlInsertQFileBillSales);
        st.close();
      	cmsCon.close();
    }
    
    private void funCustWiseItemSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
            + ",strpaymode,dblsubtotal,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
            + ",strpaymode,dblsubtotal,struser) "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funHomeDeliveryWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
            + ",strpaymode,dblsubtotal,dbldiscper,dbldiscamt,dbltaxamt"
            + ",dblsettlementamt,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
            + ",strpaymode,dblsubtotal,dbldiscper,dbldiscamt,dbltaxamt"
            + ",dblsettlementamt,struser) "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funDeliveryBoyWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,struser) "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funWaiterWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,struser) "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funOperatorWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,strpaymode,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,strpaymode,struser) "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funHourWiseItemSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode,struser) "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funCostCenterWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash "
            + "("+liveTableQuery+");";
        
        String sqlInsertQFileBillSales="insert into tbltempsalesflash "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funSubGroupWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash "
            + "("+liveTableQuery+");";
        
        String sqlInsertQFileBillSales="insert into tbltempsalesflash "
            + "("+qFileTableQuery+");";
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
    private void funGroupWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash "
            + "("+liveTableQuery+");";
        
        String sqlInsertQFileBillSales="insert into tbltempsalesflash "
            + "("+qFileTableQuery+");";
        
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales);
        clsGlobalVarClass.dbMysql.execute(sqlInsertQFileBillSales);
    }
    
   
    
    private void funComplementorySales(String liveTableQuery1, String qFileTableQuery1, String liveTableQuery2, String qFileTableQuery2) throws Exception
    {
        clsGlobalVarClass.dbMysql.execute("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
            + ",strpaymode,dblsubtotal,dbldiscper,dbldiscamt,dbltaxamt"
            + ",dblsettlementamt,strusercreated,struseredited,dtedatecreated"
            + ",dtedateedited,strclientcode,strwaiterno,strcustomercode,struser) ";
        
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales+"("+liveTableQuery1+")");
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales+"("+qFileTableQuery1+")");
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales+"("+liveTableQuery2+")");
        clsGlobalVarClass.dbMysql.execute(sqlInsertLiveBillSales+"("+qFileTableQuery2+")");
    }
    
       
    
    private void funTaxWiseSales(String liveTableQuery, String qFileTableQuery) throws Exception
    {
    	clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1=null;
		cmsCon = objDb.funOpenAPOSCon("mysql", "master");
		st = cmsCon.createStatement();
		st.executeUpdate("truncate table tbltempsalesflash1;");
        String sqlInsertLiveBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
            + ",strpaymode,dblsubtotal,struser) "
            + "("+liveTableQuery+");";
        String sqlInsertQFileBillSales="insert into tbltempsalesflash1 "
            + "(strbillno,dtebilldate,tmebilltime,strtablename,strposcode"
            + ",strpaymode,dblsubtotal,struser) "
            + "("+qFileTableQuery+");";
        st.executeUpdate(sqlInsertLiveBillSales);
        st.executeUpdate(sqlInsertQFileBillSales);
        st.close();
		cmsCon.close();
    }
    */
}