package com.apos.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Repository("clsImportDatabaseDao")

@Transactional(value = "webPOSTransactionManager")
public class clsImportDatabaseDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public static String clientCode,userCode,dateTime,posCode,dbURL,strUserName,strPassword;
	
	public boolean funImportDatabase()
	    {
		 	boolean flag=false;
	        funEmptyMasterTables();
	        if(funCheckEmptyDB())
	        {
	            if(funFillTempTable()>0)
	            {
	               flag= funGenerateCode();
	            }
	        }
	        return flag;
	    }
	 
	   private int funEmptyMasterTables() 
       {
           try
           {
               String sql="truncate table tblgrouphd";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblsubgrouphd";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblmenuhd";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblsubmenuhead";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblcostcentermaster";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblareamaster";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="update tblinternal set dblLastNo=0 where strTransactionType='Area'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblitemmaster";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblcounterhd";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblmenuitempricinghd";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
               sql="truncate table tblmenuitempricingdtl";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();	
               
           }catch(Exception e)
           {
               e.printStackTrace();
           }
           
           return 1;
       }
       
	   public boolean funCheckEmptyDB()
       {
		   List list=null;
           boolean flgResult=false;
           int groupCount=0,subGroupCount=0,itemMasterCount=0,menuHeadCount=0,subMenuHeadCount=0,counterCount=0;
           int costCenterCount=0,menuItemPricingHd=0,menuItemPricingDtl=0;
                       
           try
           {
               String sql="select count(strGroupCode) from tblgrouphd "
                       + "where strClientCode='"+clientCode+"'";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            {
	            	
               groupCount= ((BigInteger) list.get(0)).intValue();
	            }
               
               sql="select count(strSubGroupCode) from tblsubgrouphd "
                       + "where strClientCode='"+clientCode+"'";
                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	subGroupCount=((BigInteger) list.get(0)).intValue();
               
               sql="select count(strMenuCode) from tblmenuhd "
                       + "where strClientCode='"+clientCode+"'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	menuHeadCount=((BigInteger) list.get(0)).intValue();
               
               sql="select count(strSubMenuHeadCode) from tblsubmenuhead "
                       + "where strClientCode='"+clientCode+"'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	subMenuHeadCount=((BigInteger) list.get(0)).intValue();
               
               sql="select count(strCostCenterCode) from tblcostcentermaster "
                       + "where strClientCode='"+clientCode+"'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	costCenterCount=((BigInteger) list.get(0)).intValue();
               
               sql="select count(strAreaCode) from tblareamaster "
                       + "where strClientCode='"+clientCode+"'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
               
               sql="select count(strItemCode) from tblitemmaster "
                       + "where strClientCode='"+clientCode+"'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	itemMasterCount=((BigInteger) list.get(0)).intValue();
               
               sql="select count(strCounterCode) from tblcounterhd "
                       + "where strClientCode='"+clientCode+"'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	counterCount=((BigInteger) list.get(0)).intValue();
               
               sql="select count(strMenuCode) from tblmenuitempricinghd";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	menuItemPricingHd=((BigInteger) list.get(0)).intValue();
               
               sql="select count(strItemCode) from tblmenuitempricingdtl";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
	            if(list.size()>0)
	            	menuItemPricingDtl=((BigInteger) list.get(0)).intValue();
               
               if(menuHeadCount==0 && groupCount==0 && subGroupCount==0 && subMenuHeadCount==0 
                   && itemMasterCount==0 && costCenterCount==0 && menuItemPricingHd==0 
                   && menuItemPricingDtl==0 && counterCount==0)
               {
                   flgResult=true;
               }
               
           }catch(Exception e)
           {
               flgResult=false;
               e.printStackTrace();
           }
           finally
           {
               return flgResult;
           }
       }   
   
	   @SuppressWarnings("finally")
	private int funFillTempTable()
	   {
	       Connection con=null;
	       Statement st=null;
	       int res=0;
	       String sql="";
	        Date objDate = new Date();
	        int day = objDate.getDate();
	        int month = objDate.getMonth() + 1;
	        int year = objDate.getYear() + 1900;
	        String currentDate = year + "-" + month + "-" + day;
	         try
	 	   {
	        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	 		con= DriverManager.getConnection(dbURL,strUserName,strPassword);
	 		st = con.createStatement();
	 		 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery("truncate table tblimportexcel");
             query.executeUpdate();
	                 sql="select b.MenuItemDescription,'' ShortName,c.MenuHeadDescription,'' SubMenuHeadName"
	                     + ",h.IncomeHeadDescription,d.POSDescription,f.MenuSubGroupDescription"
	                     + ",g.MenuGroupDescription,e.CostCenterDescription,'All' Area"
	                     + ",'' TaxIndicator,'0.00' PurchaseRate,'' ExternalCode,'' ItemDetails"
	                     + ",'Food' ItemType,'Y' ApplyDiscount,'Y' StockInEnable"
	                      + ",a.Sun,a.Mon,a.Tue,a.Wed,a.Thr,a.Fri,a.Sat,'' Counter\n" +
	                      " from TblRateMst a,TblMenuItemMst b,TblMenuHeadMst c,TblPosMst d,TblCostCenterMst e,TblMenuSubGroupMst f,TblMenuGroupMst g,TblIncomeHeadMst h\n" +
	                      " where a.MenuItemCode=b.MenuItemCode and a.MenuHeadCode=c.MenuHeadCode and c.POSCode=d.POSCode\n" +
	                      " and a.POSCode=d.POSCode and a.CostCenterCode=e.CostCenterCode and e.POSCode=d.POSCode\n" +
	                      " and b.MenuSubGroupCode=f.MenuSubGroupCode and f.MenuGroupCode=g.MenuGroupCode and b.IncomeHeadCode=h.IncomeHeadCode "
	                      + " AND '"+currentDate+"' between a.FromDate and a.ToDate;";
	                 
	                
	                 ResultSet rs=st.executeQuery(sql);
	                 while(rs.next())
	                 {
	                     String menuItemName=rs.getString(1).replaceAll("'", "");
	                     String menuGroupName=rs.getString(8).replaceAll("'", "");
	                     String costCenterDesc=rs.getString(9).replaceAll("'", "");
	                     String menuHeadName=rs.getString(3).replaceAll("'", "");
	                     String posName=rs.getString(6).replaceAll("'", "");
	                     String subGroupName=rs.getString(7).replaceAll("'", "");
	                     String incomeHeadDesc=rs.getString(5).replaceAll("'", "");                    
	                                         
	                       sql="insert into tblimportexcel (strItemName,strShortName,strMenuHeadName,strSubMenuHeadName"
	                         + ",strRevenueHead,strPOSName"
	                         + ",strSubGroupName,strGroupName,strCostCenterName,strAreaName,dblTax,dblPurchaseRate"
	                         + ",strExternalCode,strItemDetails,strItemType,strApplyDiscount,strStockInEnable"
	                         + ",dblPriceSunday,dblPriceMonday,dblPriceTuesday,dblPriceWednesday,dblPriceThursday"
	                         + ",dblPriceFriday,dblPriceSaturday,strCounterName) values("
	                         + "'"+menuItemName+"','"+rs.getString(2)+"','"+menuHeadName+"',"
	                         + "'"+rs.getString(4)+"','"+incomeHeadDesc+"','"+posName+"',"
	                         + "'"+subGroupName+"','"+menuGroupName+"','"+costCenterDesc+"',"
	                         + "'"+rs.getString(10)+"','"+rs.getString(11)+"','"+rs.getString(12)+"',"
	                         + "'"+rs.getString(13)+"','"+rs.getString(14)+"','"+rs.getString(15)+"',"
	                         + "'"+rs.getString(16)+"','"+rs.getString(17)+"','"+rs.getDouble(18)+"',"
	                         + "'"+rs.getDouble(19)+"','"+rs.getDouble(20)+"','"+rs.getDouble(21)+"',"
	                         + "'"+rs.getDouble(22)+"','"+rs.getDouble(23)+"','"+rs.getDouble(24)+"','"+rs.getString(25)+"')";
	                       
	                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                       query.executeUpdate();
	                       
	                       
	                     
	                 }
	                 rs.close();
	                 st.close();
	                 con.close();
	                res=1;
	                 
	                 
	            } 
	         catch(Exception e)
	         {
	             res=0;
	             e.printStackTrace();
	         }
	         finally
	           {
	              return res;
	               
	           }
	       
	   }
	   
	   
	   public boolean funGenerateCode()
       {
           boolean flgReturn=false;
          
               funGeneratePOS();
               funGenerateGroup();
               funGenerateSubGroup();
               funGenerateMenuHead();
               funGenerateSubMenuHead();
               funGenerateItemMaster();
               funGenerateCostCenter();
               funGenerateCounterMasterHd();
               funGenerateCounterMasterDtl();
               funGenerateAreaMaster();
               funGenerateMenuItemPriceHD();
               flgReturn=funGenerateMenuItemPriceDTL();
           
        
           return flgReturn;
       }
       

        private boolean funGenerateGroup()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list =null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblgrouphd");
               String sql="select distinct(strGroupName) from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						String gName = (String) list.get(i);
				 
                   if(gName.trim().length()>0)
                   {
                       docNo++;
                       code = "G" + String.format("%07d",docNo);
                       sql="insert into tblgrouphd (strGroupCode,strGroupName,strUserCreated,"
                               + "strUserEdited,dteDateCreated,dteDateEdited,strClientCode)"
                               + "values('"+code+"','"+gName+"','"+userCode+"',"
                               + "'"+userCode+"','"+dateTime+"',"
                               + "'"+dateTime+"','"+clientCode+"')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
       				
                       int insert=query.executeUpdate();
                       if(insert==1)
                       {
                           sql= "update tblimportexcel set strGroupCode='"+code+"' "
                                   + "where strGroupName='"+gName+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();
                       }
                   }
               }
           }
             
               flgReturn=true;
           }
         
           catch(Exception e)
           {
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateSubGroup()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list =null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblsubgrouphd");
               String sql="select distinct(strSubGroupName),strGroupCode from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				 
                   if(obj[0].toString().trim().length()>0)
                   {
                       docNo++;
                       code = "SG" + String.format("%07d",docNo);
                       sql="insert into tblsubgrouphd (strSubGroupCode,strSubGroupName,strGroupCode,"
                               + "strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode)"
                               + "values('"+code+"','"+obj[0].toString()+"','"+obj[1].toString()+"',"
                               + "'"+userCode+"','"+userCode+"',"
                               + "'"+dateTime+"','"
                               +dateTime+"','"+clientCode+"')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
          				
                       int insert=query.executeUpdate();
                       if(insert==1)
                       {
                    	   sql="update tblimportexcel set strSubGroupCode='"+code+"' "
                                   + "where strSubGroupName='"+obj[0].toString()+"'";
                    	   query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();
                       }
                   }
					}
               }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateCostCenter()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblcostcentermaster");
               String sql="select distinct(strCostCenterName) from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						String cName = (String) list.get(i);
				 
                   if(cName.trim().length()>0)
                   {
                       docNo++;
                       code = "C" + String.format("%02d",docNo);
                       sql="insert into tblCostCenterMaster (strCostCenterCode,strCostCenterName,strPrinterPort"
                           + ",strSecondaryPrinterPort,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited"
                           + ",strClientCode,strDataPostFlag)"
                           + " values('"+code+"','"+cName+"','','','"+userCode+"',"
                           + "'"+userCode+"','"+dateTime+"',"
                           + "'"+dateTime+"','"+clientCode+"','N')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
         				
                       int insert=query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strCostCenterCode='"+code+"' "
                                   + "where strCostCenterName='"+cName+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();                       }
                   }
               }
	            }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateCounterMasterHd()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list=null;
           try
           {
               String sql="select distinct(strCounterName) from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				 
                   if(obj[0].toString().trim().length()>0)
                   {
                       docNo++;
                       code = "C" + String.format("%02d",docNo);
                       sql="insert into tblcounterhd (strCounterCode,strCounterName,strPOSCode,"
                           + "strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strDataPostFlag,strOperational)"
                           + " values('"+code+"','"+obj[0].toString()+"','"+posCode+"','"+userCode+"',"
                           + "'"+userCode+"','"+dateTime+"',"
                           + "'"+dateTime+"','"+clientCode+"','N','Yes')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
        				
                       int insert=query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strCounterCode='"+code+"' "
                                   + "where strCounterName='"+obj[0].toString()+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            				
                           query.executeUpdate();
                       }
                   }
               }
	            }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       private boolean funGenerateCounterMasterDtl()
       {
           boolean flgReturn=false;
           List list=null;
           try
           {
               String sql="select distinct(strMenuHeadCode),strCounterCode from tblimportexcel order by strCounterCode";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				 
                  
                   
                   sql="insert into tblcounterdtl (strCounterCode,strMenuCode,strClientCode)"
                       + " values('"+obj[1].toString()+"','"+obj[0].toString()+"','"+clientCode+"')";
                   query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
   				
                   query.executeUpdate();
					}
	            }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateMenuHead()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblmenuhd");
               String sql="select distinct(strMenuHeadName) from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				  
                   if(obj[0].toString().trim().length()>0)
                   {
                       docNo++;
                       code = "M" + String.format("%06d",docNo);
                       sql="insert into tblmenuhd (strMenuCode,strMenuName,strUserCreated,strUserEdited,"
                               + "dteDateCreated,dteDateEdited,strClientCode,strOperational) "
                           + "values('" +code+ "','"+obj[0].toString()+ "','"+ userCode + "'"
                           + ",'" + userCode + "','" + dateTime + "'"
                           + ",'" + dateTime + "','"+clientCode+"','Y' )";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
          				
                       int insert=query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strMenuHeadCode='"+code+"' "
                                   + "where strMenuHeadName='"+obj[0].toString()+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();
                       }
                   }
                   flgReturn=true;
               }
	            }               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGeneratePOS()
       {
           boolean flgReturn=false;
           String code="";
           List list=null;
           long docNo=0;
           try
           {
        	   String sql="truncate table tblposmaster";
               Query  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();
                sql="select distinct(strPOSName) from tblimportexcel";
                query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						String pName = (String) list.get(i);
				  
                   if(pName.trim().length()>0)
                   {                   
                       docNo++;
                       code = "P" + String.format("%02d",docNo);
                       sql="insert into tblposmaster(strPosCode,strPosName,strPosType,strDebitCardTransactionYN,"
                           + "strPropertyPOSCode,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited"
                           + ",strCounterWiseBilling,strPrintVatNo,strPrintServiceTaxNo,strVatNo,strServiceTaxNo) "
                           + "values('" +code+ "','" +pName+ "','Dine In','No',''"
                           + ",'"+userCode+ "','"+userCode+ "',"
                           + "'"+dateTime+"','"+ dateTime + "'"
                           + ",'No','N','N','','')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                       int insert= query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strPOSCode='"+code+"' "
                                   + "where strPOSName='"+pName+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();
                       }
                   }
               }
	            }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateItemMaster()
       {
           boolean flgReturn=false;
           String code="",stkInEnable="N",purchaseRate="0.00";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblitemmaster");
               String sql="select distinct(strItemName),strSubGroupCode,strStockInEnable,dblPurchaseRate"
                           + ",strExternalCode,strItemDetails,strItemType,strApplyDiscount,strShortName,dblTax,strRevenueHead "
                           + "from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				  
                   if(obj[0].toString().trim().length()>0)
                   {
                       if(obj[2].toString().equals("Y"))
                       {
                           stkInEnable="Y";
                       }
                       if(obj[3].toString().trim().length()==0)
                       {
                           purchaseRate="0.00";
                       }else
                       {
                           purchaseRate=obj[3].toString();
                       }
                       docNo++;
                       code = "I" + String.format("%06d",docNo);
                       sql="insert into tblitemmaster (strItemCode,strItemName,strSubGroupCode,strTaxIndicator"
                               + ",strStockInEnable,dblPurchaseRate,strExternalCode,strItemDetails,strUserCreated"
                               + ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strItemType,strDiscountApply"
                               + ",strShortName,strRevenueHead)"
                               + " values('"+code+"','"+obj[0].toString()+"','"+obj[1].toString()+"'"
                               + ",'"+obj[9].toString()+"','"+stkInEnable+"','"+purchaseRate+"','"+obj[4].toString()+"'"
                               + ",'"+obj[5].toString()+"','"+userCode+"'"
                               + ",'"+userCode+"','"+dateTime+"'"
                               + ",'"+dateTime+"','"+clientCode+"'"
                               + ",'"+obj[6].toString()+"','"+obj[7].toString()+"'"
                               + ",'"+obj[8].toString()+"','"+obj[10].toString()+"')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                       int insert= query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strItemCode='"+code+"' "
                                   + "where strItemName='"+obj[0].toString()+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                          query.executeUpdate();
                       }
                   }
               }
	            }
            	   flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateItemMasterForRetail()
       {
           boolean flgReturn=false;
           String code="",stkInEnable="N",purchaseRate="0.00",saleRate="0.00";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblitemmaster");
               String sql="select distinct(strItemName),strSubGroupCode,strStockInEnable,dblPurchaseRate"
                       + ",strExternalCode,strItemDetails,strItemType,strApplyDiscount,strShortName"
                       + ",dblPriceMonday,strRevenueHead "
                       + "from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				  
                   if(obj[0].toString().trim().length()>0)
                   {
                       if(obj[2].toString().equals("Y"))
                       {
                           stkInEnable="Y";
                       }
                       if(obj[3].toString().trim().length()==0)
                       {
                           purchaseRate="0.00";
                       }
                       if(obj[9].toString().trim().length()==0)
                       {
                           saleRate="0.00";
                       }
                       else
                       {
                           saleRate=obj[9].toString();
                       }
                       docNo++;
                       code = "I" + String.format("%06d",docNo);
                       sql="insert into tblitemmaster (strItemCode,strItemName,strSubGroupCode,strTaxIndicator"
                               + ",strStockInEnable,dblPurchaseRate,strExternalCode,strItemDetails,strUserCreated"
                               + ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strItemType,strDiscountApply"
                               + ",strShortName,dblSalePrice,strRevenueHead)"
                               + " values('"+code+"','"+obj[0].toString()+"','"+obj[1].toString()+"'"
                               + ",'','"+stkInEnable+"','"+purchaseRate+"','"+obj[4].toString()+"'"
                               + ",'"+obj[5].toString()+"','"+userCode+"'"
                               + ",'"+userCode+"','"+dateTime+"'"
                               + ",'"+dateTime+"','"+clientCode+"'"
                               + ",'"+obj[6].toString()+"','"+obj[7].toString()+"'"
                               + ",'"+obj[8].toString()+"',"+saleRate+",'"+obj[10].toString()+"')";
                       
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                       int insert= query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strItemCode='"+code+"' "
                               + "where strItemName='"+obj[0].toString()+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();
                       }
                   }
               }
	            }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateAreaMaster()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblareamaster");
               //String sql="select distinct(strAreaName) from tblimportexcel where strAreaName!='All'";
               String sql="select distinct(strAreaName) from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						String aName = (String) list.get(i);
				  
                   if(aName.trim().length()>0)
                   {
                       docNo++;
                       code = "A" + String.format("%03d",docNo);
                       sql="insert into tblareamaster (strAreaCode,strAreaName,strUserCreated,strUserEdited,"
                           + "dteDateCreated,dteDateEdited)"
                           + "values('"+code+"','"+aName+"'"
                           + ",'"+userCode+"','"+userCode+"'"
                           + ",'"+dateTime+"','"+dateTime+"')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                       int insert= query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strAreaCode='"+code+"' "
                               + "where strAreaName='"+aName+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();
                       }
                   }
               }
	            }
               sql="update tblinternal set dblLastNo="+docNo+" where strTransactionType='Area'";
               query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();               
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       private boolean funGenerateSubMenuHead()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblsubmenuhead");
               String sql="select distinct(strSubMenuHeadName),strMenuHeadCode from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				  
                   if(obj[0].toString().trim().length()>0)
                   {
                       docNo++;
                       code = "SM" + String.format("%06d",docNo);
                       sql="insert into tblsubmenuhead (strSubMenuHeadCode,strMenuCode,strSubMenuHeadShortName,"
                              + "strSubMenuHeadName,strSubMenuOperational,strUserCreated,strUserEdited,dteDateCreated,"
                              + "dteDateEdited,strClientCode)"
                              + " values('"+code+"','"+obj[1].toString()+"',''"
                              + ",'"+obj[0].toString().trim()+"','Y','"+userCode+"'"
                              + ",'"+userCode+"','"+dateTime+"'"
                              + ",'"+dateTime+"','"+clientCode+"')";
                       query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                       int insert= query.executeUpdate();
                       if(insert==1)
                       {
                           sql="update tblimportexcel set strSubMenuHeadCode='"+code+"' "
                                   + "where strSubMenuHeadName='"+obj[0].toString()+"'";
                           query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                           query.executeUpdate();
                       }
                   }
					}
					}
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       
       private boolean funGenerateMenuItemPriceHD()
       {
           boolean flgReturn=false;
           String code="";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblmenuitempricinghd");
               String sql="select distinct(strMenuHeadCode),strMenuHeadName,strPOSCode from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				  
                   
                   docNo++;
                   sql="insert into tblmenuitempricinghd(strPosCode,strMenuCode,strMenuName,strUserCreated"
                           + ",strUserEdited,dteDateCreated,dteDateEdited) "
                           + "values('"+obj[2].toString()+"','"+obj[0].toString()+"'"
                           + ",'"+obj[1].toString()+"','"+userCode+"'"
                           + ",'"+ userCode+"','"+dateTime+"'"
                           + ",'"+dateTime+"')";
                   query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                   int insert= query.executeUpdate();
               }
	            }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
       private String funFormatPrice(String price)
       {
           if(price.contains(","))
           {
               price=price.replace(",","");
           }
           return price;
       }
       
       private boolean funGenerateMenuItemPriceDTL()
       {
           boolean flgReturn=false;
           String fromDate="",toDate="",priceMon="",priceTue="",priceWed="",priceThu="",priceFri="",priceSat="";
           String priceSun="";
           Date dt=new Date();
           fromDate=(dt.getYear()+1900)+"-"+(dt.getMonth()+1)+"-"+dt.getDate()+" ";
           fromDate+=dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds();
           
           toDate=(dt.getYear()+1901)+"-"+(dt.getMonth()+1)+"-"+dt.getDate()+" ";
           toDate+=dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds();
           
           String code="";
           long docNo=0;
           List list=null;
           try
           {
               //clsGlobalVarClass.dbMysql.execute("truncate table tblmenuitempricingdtl");
               String sql="select distinct(strItemCode),strItemName,strPOSCode,strMenuHeadCode"
                       + ",dblPriceMonday,dblPriceTuesday,dblPriceWednesday,dblPriceThursday,dblPriceFriday"
                       + ",dblPriceSaturday,dblPriceSunday,strCostCenterCode,strAreaCode,strSubMenuHeadCode "
                       + "from tblimportexcel";
               Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               list = query.list();
               if(list.size()>0)
	            {
            	   for(int i=0; i<list.size(); i++)
					{
						
						Object[] obj = (Object[]) list.get(i);
				  
                   docNo++;
                   if(obj[4].toString().trim().length()==0)
                       priceMon="0.00";
                   else
                       priceMon=funFormatPrice(obj[4].toString().trim());
                   
                   if(obj[5].toString().trim().length()==0)
                       priceTue="0.00";
                   else
                       priceTue=funFormatPrice(obj[5].toString().trim());
                   
                   if(obj[6].toString().trim().length()==0)
                       priceWed="0.00";
                   else
                       priceWed=funFormatPrice(obj[6].toString().trim());
                   
                   if(obj[7].toString().trim().length()==0)
                       priceThu="0.00";
                   else
                       priceThu=funFormatPrice(obj[7].toString().trim());
                   
                   if(obj[8].toString().trim().length()==0)
                       priceFri="0.00";
                   else
                       priceFri=funFormatPrice(obj[8].toString().trim());
                   
                   if(obj[9].toString().trim().length()==0)
                       priceSat="0.00";
                   else
                       priceSat=funFormatPrice(obj[9].toString().trim());
                   
                   if(obj[10].toString().trim().length()==0)
                       priceSun="0.00";
                   else
                       priceSun=funFormatPrice(obj[10].toString().trim());
                   
                   sql="insert into tblmenuitempricingdtl(strItemCode,strItemName,strPosCode,strMenuCode"
                       + ",strPopular,strPriceMonday,strPriceTuesday,strPriceWednesday,strPriceThursday,strPriceFriday"
                       + ",strPriceSaturday,strPriceSunday,dteFromDate,dteToDate,tmeTimeFrom,strAMPMFrom,tmeTimeTo"
                       + ",strAMPMTo,strCostCenterCode,strTextColor,strUserCreated,strUserEdited,dteDateCreated"
                       + ",dteDateEdited,strAreaCode,strSubMenuHeadCode,strHourlyPricing) "
                       + "values('"+obj[0].toString()+"','"+obj[1].toString()+"'"
                       + ",'"+obj[2].toString()+"','"+obj[3].toString()+"'"
                       + ",'N','"+priceMon+"','"+priceTue+"'"+ ",'"+priceWed+"','"+priceThu+"'"+ ",'"+priceFri+"'"
                       + ",'"+priceSat+"'"+ ",'"+priceSun+"','"+fromDate+"','"+toDate+"'"
                       + ",'HH:MM', 'AM', 'HH:MM', 'AM','"+obj[11].toString()+"','Black'"
                       + ",'"+userCode+"','"+userCode+"'"
                       + ",'"+dateTime+"','"+dateTime+"'"
                       + ",'"+obj[12].toString()+"','"+obj[13].toString()+"','No')";
                   query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                   int insert= query.executeUpdate();
               }
	            }
               flgReturn=true;
               
           }catch(Exception e)
           {
               flgReturn=false;
               e.printStackTrace();
           }
           finally
           {
               return flgReturn;
           }
       }
       
}
