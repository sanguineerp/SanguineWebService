package com.apos.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsCashManagementBean;
import com.apos.controller.clsCashManagement;
import com.apos.util.clsSendMail;
import com.apos.util.clsSynchronizePOSDataToHO;
import com.apos.util.clsTextFileGenerationForPrinting2;

@Repository("clsUtilityDao")
@Transactional(value = "webPOSTransactionManager")
public class clsUtilityDaoImpl implements clsUtilityDao
{
	@Autowired
	private SessionFactory WebPOSSessionFactory;
	
	@Autowired 
	clsSendMail obSendMail;
	
	@Autowired
	clsSetupDao objSetupDao;
	@Autowired
	clsSynchronizePOSDataToHO objSynchronizePOSDataToHO;
	@Autowired
	clsTextFileGenerationForPrinting2 obTextFileGenerationForPrinting2;
	
	String gShiftEnd="",gLastPOSForDayEnd="",gCMSIntegrationYN="",gCMSPostingType="";
	String gDayEnd="N";
	double gTotalDiscounts=0, gTotalCashSales=0,gTotalAdvanceAmt=0,gTotalReceipt=0,
			gTotalPayments=0, gTotalCashInHand=0;
	int gNoOfDiscountedBills=0,gTotalBills=0,gShiftNo;
	
	public String funGetPOSWiseDayEndData(String POSCode,String UserCode)
	{

		JSONObject jsonPOSData=new JSONObject();
		try{
				String sql = "select count(*) from tblsettelmenthd";
				Query settque = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List listSet=settque.list();
				if (listSet.size() > 0)
				{
					String cnt=listSet.get(0).toString();
					if(Integer.parseInt(cnt)>0)
					{
						Boolean gShifts = false;
		                   sql = "select count(intShiftCode) from tblshiftmaster where strPOSCode='" + POSCode + "'";
		                   Query qShiftCodeCount=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		                   List listShiftCode=qShiftCodeCount.list();
		                   	if(listShiftCode.size()>0)
			                   {
			                	   if (Integer.parseInt(listShiftCode.get(0).toString()) > 0)
			                           {
			                               gShifts = true;
			                           }
			                           else
			                           {
			                               gShifts = false;
			                           }
			                    }
		                   	jsonPOSData.put("gShifts", gShifts);
					       	
		                    sql = "select count(*) from tbldayendprocess "
			                        + " where strPOSCode='" + POSCode + "' and strDayEnd='N' "
			                        + " and (strShiftEnd='' or strShiftEnd='N')";
			                //System.out.println(sql);
			                
			                settque = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
							listSet=settque.list();
							int countEnd=0;
							if (listSet.size() > 0)
							{
								  countEnd = Integer.parseInt(listSet.get(0).toString());
							}
			                if (countEnd > 0)
			                {
			                	 sql = "select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd"
			                             + " from tbldayendprocess where strPOSCode='"
			                             + POSCode + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')";
			                	settque = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			 					listSet=settque.list();
			 					if (listSet.size() > 0)
			 					{
			 						Object obSett[]=(Object[]) listSet.get(0);
 			 						jsonPOSData.put("startDate", obSett[0].toString());
			 						jsonPOSData.put("ShiftNo", obSett[1].toString());
			 						jsonPOSData.put("ShiftEnd", obSett[2].toString());
			 						jsonPOSData.put("DayEnd", obSett[3].toString());
			 						 
			 						 if (false)//clsGlobalVarClass.gShiftNo == 0)
				                     {
				                         //JOptionPane.showMessageDialog(this,"Please Start the shift");
				                     }
				                     else
				                     {
				                    	 if(gShifts)
				                    	 {
				                    		sql = "select intShiftCode,strBillDateTimeType from tblshiftmaster "
					                                 + "where strPOSCode='" + POSCode + "' and "
					                                 + "intShiftCode=" + Integer.parseInt(obSett[1].toString());
					                        Query qShiftInfo = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
					                        List listShiftInfo=qShiftInfo.list();
					 	 					if (listShiftInfo.size() > 0)
					 	 					{
					 	 						Object obShift[]=(Object[]) listShiftInfo.get(0);
					 	 						jsonPOSData.put("gBillDateTimeType", obShift[1].toString());
					 	 						
					 	 					}
				                    	 }
					                        
					 	 					
					                       
				                     }
				                     /*SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
				                     Date bDate = dFormat.parse(obSett[0].toString());
				                     long posTime = bDate.getTime();
				                     Date currDate = new Date();
				                     long currTime = currDate.getTime();
				                     long diffTime = currTime - posTime;
				                     long diffDays = diffTime / (24 * 60 * 60 * 1000);
				                     if (clsGlobalVarClass.gChangeModule.equals("N"))
				                     {
				                         if (diffDays != 0)
				                         {
				                             new frmOkPopUp(null, "POS Date is not equal to System Date", "Warning", 1).setVisible(true);
				                         }
				                     }*/

			 					}
			 						                	
			                }
			                else{
			                	
			                     int h, m, sec, d, min, y, lblCount, cntNavigate;
			                      String dteCreated, time, date;
			                	  Date endDt = new Date();
			                      String todayDate = (endDt.getYear() + 1900) + "-" + (endDt.getMonth() + 1) + "-" + endDt.getDate();
			                      java.util.Date curDt = new java.util.Date();
			                      d = curDt.getDate();
			                      m = curDt.getMonth() + 1;
			                      y = curDt.getYear() + 1900;
			                      h = curDt.getHours();
			                      min = curDt.getMinutes();
			                      sec = curDt.getSeconds();
			                      time = h + ":" + min + ":" + sec;
			                      date = y + "-" + m + "-" + d;
			                      dteCreated = date + " " + time;
			                      
			                      jsonPOSData.put("startDate", todayDate);
			                     // clsGlobalVarClass.setStartDate(todayDate);
			                      SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
			                      Date bDate = dFormat.parse(todayDate);
			                      long posTime = bDate.getTime();
			                      Date currDate = new Date();
			                      long currTime = currDate.getTime();
			                      long diffTime = currTime - posTime;
			                      long diffDays = diffTime / (24 * 60 * 60 * 1000);
			                      sql = "insert into tbldayendprocess(strPOSCode,dtePOSDate,strDayEnd,strShiftEnd,strUserCreated,dteDateCreated)"
			                              + " values('" + POSCode + "','" + todayDate + "','N','','" + UserCode
			                              + "','" + dteCreated + "')";
			                      WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
			                   
			                     
			 						jsonPOSData.put("ShiftEnd", "");
			 						jsonPOSData.put("DayEnd", "N");
			 						jsonPOSData.put("ShiftNo","0");
			 						
			                     /* if (clsGlobalVarClass.gChangeModule.equals("N"))
			                      {
			                          if (diffDays != 0)
			                          {
			                              new frmOkPopUp(null, "POS Date is not equal to System Date", "Warning", 1).setVisible(true);
			                          }
			                      }*/
		 						
		 					}
					}
					else
					{
						 //new frmOkPopUp(null, "Settlement Type is not Present", "Warning", 1).setVisible(true);
					}
	        	  
	           }
				  
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jsonPOSData.toString();
    
	}

	public boolean funCheckPendingBills(String posCode,String POSDate)
	{
		boolean flgPendingBills = false;
        try
        {
            String sqlPendingBill = "";
            sqlPendingBill = "select count(*) "
                    + "from tblbillhd where date(dteBillDate)='" + POSDate + "' "
                    + "and strBillNo NOT IN(select strBillNo from tblbillsettlementdtl) "
                    + "and strPOSCode='" + posCode + "'";
            //System.out.println(sql_PendingBill);
            Query qPendingBills=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlPendingBill);
            List list=qPendingBills.list();
            int count=0;
            if(list.size()>0)
        	{
	             count=Integer.parseInt(list.get(0).toString());
	            if (count > 0)
	            {
	                flgPendingBills = true;
	            }
        	}
            
            sqlPendingBill = "select count(*) from tblbillhd "
                    + " where date(dteBillDate)='" + POSDate + "' and  strTableNo is not NULL and strBillNo"
                    + " NOT IN(select strBillNo from tblbillsettlementdtl) "
                    + " and strPOSCode='" + posCode + "'";
            //System.out.println(sql_PendingBill);
            qPendingBills=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlPendingBill);
             list=qPendingBills.list();
             if(list.size()>0)
	        	{  
	             count=Integer.parseInt(list.get(0).toString());
		            if (count > 0)
		            {
		                flgPendingBills = true;
		            }
	        	}
            
          
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return flgPendingBills;
	}

	public boolean funCheckTableBusy(String posCode)
    {
        boolean flgPendingBills = false;
        try
        {
        	Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery("delete from tblitemrtemp where strNCKotYN='Y'");
        	query.executeUpdate();
        	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery("delete from tblitemrtemp_bck where strNCKotYN='Y'");
        	query.executeUpdate();
        	
        	String sql = "select count(*) from tblitemrtemp where strNCKotYN='N' "
                    + "and strPOSCode='" + posCode + "'";
        	Query qCheckTableBusy=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
        	List list=qCheckTableBusy.list();
        	int count=0;
        	if(list.size()>0)
        	{
        		count=Integer.parseInt(list.get(0).toString());
	            if (count > 0)
	            {
	                flgPendingBills = true;
	            }
        	}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return flgPendingBills;
    }
	
	public String funGetDBBackUpPath(String clientCode)
	{

		 String strDBbckUpPath="";
		 
		 String sql="select strMySQBackupFilePath from tblconfig where strClientCode='"+clientCode+"'";
		 Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		 List list=query.list();
		 if(list.size()>0){
			 strDBbckUpPath=(String) list.get(0);	 
		 }
		 
		 return strDBbckUpPath;
	 
	}
	
	public int funGetNextShiftNo(String posCode, int shiftNo,String strClientCode,String strUserCode)
    {
        int retvalue = 0;
        try
        {
        	 String billDate="";
            String billDateSql = "select date(max(dtePOSDate)) from tbldayendprocess where strPOSCode='" + posCode + "'";
            Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(billDateSql);

            List list=query.list();
            if(list.size()>0)
            {
                billDate =((Object)list.get(0)).toString();
 
            }

            retvalue = funShiftEndProcess("DayEnd", posCode, shiftNo, billDate,strClientCode,strUserCode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return retvalue;
        }
    }

	public int funGetNextShiftNoForShiftEnd(String posCode, int shiftNo,String strClientCode,String strUserCode)
    {

        int retvalue = 0;
        int shiftCount = 0;
        String billDate="";
        try
        {
            String billDateSql = "select date(max(dtePOSDate)) from tbldayendprocess where strPOSCode='" + posCode + "'";
            Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(billDateSql);
            //ResultSet rsDayEnd = clsGlobalVarClass.dbMysql.executeResultSet(billDateSql);
            List list=query.list();
            if(list.size()>0)
            {
                billDate =((Object)list.get(0)).toString();
 
            }
            String sql = "select count(intShiftCode) from tblshiftmaster where strPOSCode='" + posCode + "'";
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            list=query.list();
            if(list.size()>0)
            {
            	shiftCount =Integer.parseInt(((Object)list.get(0)).toString());
            }
            if (shiftCount > 0)
            {
                if (shiftNo == shiftCount)
                {
                	gShiftNo=1;  //clsGlobalVarClass.gShiftNo=1;
                    retvalue = funShiftEndProcess("DayEnd", posCode, shiftNo, billDate,strClientCode,strUserCode);
                }
                else
                {
                    retvalue = funShiftEndProcess("ShiftEnd", posCode, shiftNo, billDate,strClientCode,strUserCode);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return retvalue;
        }
    
    }


    public int funShiftEndProcess(String status, String posCode, int shiftNo, String billDate,String strClientCode,String strUserCode)
	    {
	        String newStartDate = "";
	        int shiftEnd = 0;
	        int retvalue = 1;
	        try
	        {

	            if (status.equalsIgnoreCase("DayEnd"))//for day end
	            {
	            	
	            	JSONObject JSONgLastPOSForDayEnd = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gLastPOSForDayEnd");
	    			gLastPOSForDayEnd=JSONgLastPOSForDayEnd.get("gLastPOSForDayEnd").toString();
	    			// Transfer Card Balance To Debit Card Revenue Table.
	                if (gLastPOSForDayEnd.equals(posCode))
	                {
	                    Date dt = new Date();
	                    String posDateTemp = billDate + " " + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
	                    funShiftCardBalToRevenueTable(posCode, posDateTemp,strClientCode,strUserCode);
	                }

	                // Post Sales Data to CMS CL and RV Tables.  
	                //commented for Poona Club
	                JSONObject JSONgCMSIntegrationYN = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSIntegrationYN");
	    			gCMSIntegrationYN=JSONgCMSIntegrationYN.get("gCMSIntegrationYN").toString();
	    			
	    			 JSONObject JSONgCMSPostingType = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSPostingType");
	    			 gCMSPostingType=JSONgCMSPostingType.get("gCMSPostingType").toString();
	    			
	                if (gCMSIntegrationYN.equalsIgnoreCase("Y"))
	                {
	                    if (gCMSPostingType.equals("Sanguine CMS"))
	                    {
	                        if (funPostSanguineCMSData(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                    else
	                    {
	                        // Post Sales Data to CMS CL and RV Tables. 
	                        if (funPostBillDataToCMS(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                }

	                // Generate next POS Date / POS Shift Date   
	                String sql = "select count(*) from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'";
	                Query qDayEndRecord=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                List listDayEndRecord=qDayEndRecord.list();
	                if(listDayEndRecord.size()>0)
	                {
	                	for(int i=0;i<listDayEndRecord.size();i++)
	                	{
	                	int count=Integer.parseInt(((Object)listDayEndRecord.get(0)).toString());
	                		//Object ob[]=(Object[])listDayEndRecord.get(i);
	                		if (count > 0)
			                {
			                    String tempPOSDate = "";
			                    
			                    sql = "select date(max(dtePOSDate)) from tbldayendprocess "
			                            + "where strPOSCode='" + posCode + "'";
			                    qDayEndRecord=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			                    listDayEndRecord=qDayEndRecord.list();
			                    Date startDate =new Date();
			                    String shiftDate="";
			                    if(listDayEndRecord.size()>0)
			                    {
			                    	
			                    	tempPOSDate = ((Object)listDayEndRecord.get(0)).toString();
				                    startDate = new SimpleDateFormat("yyyy-MM-dd").parse(tempPOSDate);
				                    shiftDate= ((Object)listDayEndRecord.get(0)).toString();
			                    		
			                    }
			                    if (status.equals("DayEnd"))
			                    {
			                        GregorianCalendar cal = new GregorianCalendar();
			                        cal.setTime(startDate);
			                        cal.add(Calendar.DATE, 1);
			                        newStartDate = (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + (cal.getTime().getDate());
			                      
			                    }
			                    else
			                    {
			                        newStartDate = shiftDate;
			                    }
			
			                    String dayEnd = "N";
			                    int shift = 0;
			                    //clsGlobalVarClass.dbMysql.funStartTransaction();
			                    if (status.equals("DayEnd"))
			                    {
			                        sql = "update tbldayendprocess set strDayEnd='Y',strShiftEnd='Y' "
			                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
			                        qDayEndRecord=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			                        qDayEndRecord.executeUpdate();
			                        dayEnd = "Y";
			                    }
			                    else
			                    {
			                        sql = "update tbldayendprocess set strDayEnd='N',strShiftEnd='Y' "
			                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
			                        qDayEndRecord=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			                        qDayEndRecord.executeUpdate();
			                        
			                        shift = shiftNo;
			                    }
			                    JSONObject jsflgCarryForwardFloatAmtToNextDay = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "flgCarryForwardFloatAmtToNextDay");
			        			String flgCarryForwardFloatAmtToNextDay=jsflgCarryForwardFloatAmtToNextDay.get("flgCarryForwardFloatAmtToNextDay").toString();
			        			
			                    if (flgCarryForwardFloatAmtToNextDay.equalsIgnoreCase("Y"))
			                    {
			                        String reasonCode = "";
			                        sql = "select strReasonCode from tblreasonmaster where strCashMgmt='Y'";
			                      
			                        Query qReason=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			                        List listReason=qReason.list();
			                        
			                        if (listReason.size()>0)
			                        {
			                            reasonCode = ((Object)listReason.get(0)).toString();
			                        }
			                        
			                        Date dt = new Date();
			                        String transDateForCashMgmt = newStartDate.split(" ")[0];
			                        transDateForCashMgmt += " " + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
			                        clsCashManagement objCashMgmt = new clsCashManagement();
			                        Map<String, clsCashManagementBean> hmCashMgmtDtl = objCashMgmt.funGetCashManagement(tempPOSDate, tempPOSDate, posCode);
			
			                        for (Map.Entry<String, clsCashManagementBean> entry : hmCashMgmtDtl.entrySet())
			                        {
			                            String transId = funGenerateNextCode();
			                            clsCashManagementBean objCashMgmtDtl = entry.getValue();
			                            double balanceAmt = (objCashMgmtDtl.getSaleAmt() + objCashMgmtDtl.getAdvanceAmt() + objCashMgmtDtl.getFloatAmt() + objCashMgmtDtl.getTransferInAmt()) - (objCashMgmtDtl.getWithdrawlAmt() + objCashMgmtDtl.getPaymentAmt() + objCashMgmtDtl.getRefundAmt() + objCashMgmtDtl.getTransferOutAmt());
			                            if (null != entry.getValue().getHmPostRollingSalesAmt())
			                            {
			                                for (Map.Entry<String, Double> entryPostRollingSales : entry.getValue().getHmPostRollingSalesAmt().entrySet())
			                                {
			                                    balanceAmt += entryPostRollingSales.getValue();
			                                }
			                            }
			
			                            if (balanceAmt > 0)
			                            {
			                                sql = "insert into tblcashmanagement(strTransID,strTransType,dteTransDate,strReasonCode,strPOSCode"
			                                        + ",dblAmount,strRemarks,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strCurrencyType"
			                                        + ",intShiftCode,strAgainst,dblRollingAmt,strClientCode,strDataPostFlag) "
			                                        + "values ('" + transId + "','Float','" + transDateForCashMgmt + "','" + reasonCode + "'"
			                                        + ",'" + posCode + "','" + balanceAmt + "','Carryforward Float Amt'"
			                                        + ",'" + entry.getKey() + "','" + entry.getKey() + "','" + getCurrentDateTime() + "'"
			                                        + ",'" +getCurrentDateTime() + "','Cash','" + shiftNo + "'"
			                                        + ",'Direct','0','" + strClientCode + "','N')";
			                                
			                                Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			                                query.executeUpdate();
			                                
			                            }
			                        }
			                    }
			
			                    sql = "insert into tbldayendprocess(strPOSCode,dtePOSDate,strDayEnd,intShiftCode,strShiftEnd"
			                            + ",strUserCreated,dteDateCreated) "
			                            + "values('" + posCode + "','" + newStartDate + "','N'," + shift + ",''"
			                            + ",'" + strUserCode + "','" + getCurrentDateTime() + "')";
			                    Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                            query.executeUpdate();
	//*********************	                 
	                            gShiftEnd="";
	                            			   // clsGlobalVarClass.gShiftEnd = "";
			                    gDayEnd="N";	//  clsGlobalVarClass.gDayEnd = "N";
			                  				//  clsGlobalVarClass.setStartDate(newStartDate);
			                  				//  clsGlobalVarClass.funSetPOSDate();
			                  				//   System.out.println("Shift = " + clsGlobalVarClass.gShifts);
			
			                    if (status.equals("ShiftEnd"))
			                    {
			                        //shiftEnd=shiftNo-1;
			                        shiftEnd = shiftNo;
			                    }
			                    else
			                    {
			                        shiftEnd = shiftNo;
			                    }
			
			                    //  Calculate Total Cash Amt, Total Advance Amt, Total Receipts , Total Payments, Ttoal Discount Amt
			                    //  , No of Discounted Bills, No of Total bills.
			                    funCalculateDayEndCash(shiftDate, shiftEnd, posCode);
			
			                    // Update tbldayendprocess table fields  
			                    
			                    funUpdateDayEndFields(shiftDate, shiftEnd, dayEnd, posCode,strUserCode);
			                    String posDate = billDate;
			
			                    JSONObject jsonPostSalesDataToMMS = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gPostSalesDataToMMS");
			        			String gPostSalesDataToMMS=jsonPostSalesDataToMMS.get("gPostSalesDataToMMS").toString();
			        		
			        			 JSONObject jsItemType = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gItemType");
				        		String gItemType=jsItemType.get("gItemType").toString();
				        		
			                    // Post POS Item Sale Data to MMS.
			                    if (gPostSalesDataToMMS.equalsIgnoreCase("Y"))
			                    {
			                        String WSStockAdjustmentCode =objSynchronizePOSDataToHO.funPostPOSItemSalesDataAuto(gItemType,posCode,posDate,posDate,strClientCode); 
			                        		
			//                        sql = "update tbldayendprocess set strWSStockAdjustmentNo='" + WSStockAdjustmentCode + "'" + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
			//                        clsGlobalVarClass.dbMysql.execute(sql);
			                        String exbillGenCode =objSynchronizePOSDataToHO.funPostPOSSalesDataToExciseAuto(gItemType,posCode,posDate,posDate,strClientCode);
			                        	//	clsGlobalVarClass.funPostItemSalesDataExcise(posCode, posDate, posDate);
			                    }
			
			                    // Transfer Billing Data from Live Tables To QFile Tables.
			                    funInsertQBillData(posCode,strClientCode);
			
			                    // Post Sales Transaction Data, Inventory Transaction Data, Audit Transaction Data, Customer Masterok
			                    // and Customer Area Master to HO.
			                    String gConnectionActive="Y";// direct initialized in global 
			                    if (gConnectionActive.equals("Y"))
			                    {
			                        funInvokeHOWebserviceForTrans("All", "Day End",strClientCode,posCode);
			                       funPostCustomerDataToHOPOS(strClientCode,posCode);
			                       funPostCustomerAreaDataToHOPOS(strClientCode,posCode);
			                    }
			
			                    // Post Day End Table Data to HO.    
			                    funPostDayEndData(newStartDate, shift,strClientCode,posCode);
			
			                   if (clsDayEndProcessDao.gTransactionType != null && clsDayEndProcessDao.gTransactionType.equalsIgnoreCase("ShiftEnd"))
			                    {
			                        retvalue = funDayEndflash(strClientCode, posCode, billDate, shiftNo,strUserCode);
			                    }
			                }
	                	}
	                }
	            }
	            else //for shift end
	            {
	            	JSONObject JSONgLastPOSForDayEnd = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gLastPOSForDayEnd");
	    			gLastPOSForDayEnd=JSONgLastPOSForDayEnd.get("gLastPOSForDayEnd").toString();
	                // Transfer Card Balance To Debit Card Revenue Table.
	                if (gLastPOSForDayEnd.equals(posCode))
	                {
	                    Date dt = new Date();
	                    String posDateTemp = billDate + " " + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
	                    funShiftCardBalToRevenueTable(posCode, posDateTemp,strClientCode,strUserCode);
	                }
	                JSONObject JSONgCMSIntegrationYN = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSIntegrationYN");
	    			gCMSIntegrationYN=JSONgCMSIntegrationYN.get("gCMSIntegrationYN").toString();
	    			
	    			 JSONObject JSONgCMSPostingType = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gCMSPostingType");
	    			 gCMSPostingType=JSONgCMSPostingType.get("gCMSPostingType").toString();
	                // Post Sales Data to CMS CL and RV Tables.    
	                if (gCMSIntegrationYN.equals("Y"))
	                {
	                    if (gCMSPostingType.equals("Sanguine CMS"))
	                    {
	                        if (funPostSanguineCMSData(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                    else
	                    {
	                        // Post Sales Data to CMS CL and RV Tables. 
	                        if (funPostBillDataToCMS(posCode, billDate,strClientCode,strUserCode) == 0)
	                        {
	                            return 0;
	                        }
	                    }
	                }

	                // Generate next POS Date / POS Shift Date   
	                String sql = "select count(*) from tbldayendprocess where strPOSCode='" + posCode + "' and strDayEnd='N'";
	                Query qDayEndRecord=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                List listDayEndRecord=qDayEndRecord.list();
	                if(listDayEndRecord.size()>0)
	                {
	                	int count=Integer.parseInt(((Object)listDayEndRecord.get(0)).toString());
	                if (count > 0)
		                {
		                   
		                    sql = "select date(max(dtePOSDate)) from tbldayendprocess "
		                            + "where strPOSCode='" + posCode + "'";
		                    qDayEndRecord=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		                    listDayEndRecord=qDayEndRecord.list();
		                    Date startDate=new Date();
		                    String shiftDate ="",tempPOSDate="";
		                    if(listDayEndRecord.size()>0)
		                    {
		                    	shiftDate = ((Object)listDayEndRecord.get(0)).toString();
			                    startDate = new SimpleDateFormat("yyyy-MM-dd").parse(tempPOSDate);
			                }
		                    if (status.equals("DayEnd"))
		                    {
		                        GregorianCalendar cal = new GregorianCalendar();
		                        cal.setTime(startDate);
		                        cal.add(Calendar.DATE, 1);
		                        newStartDate = (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + (cal.getTime().getDate());
		                        
		                    }
		                    else
		                    {
		                        newStartDate = shiftDate;
		                    }
		
		                    String dayEnd = "N";
		                    int shift = 0;
		                    //clsGlobalVarClass.dbMysql.funStartTransaction();
		                    if (status.equals("DayEnd"))
		                    {
		                        sql = "update tbldayendprocess set strDayEnd='Y',strShiftEnd='Y' "
		                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
		                        WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		                        //clsGlobalVarClass.dbMysql.execute(sql);
		                        dayEnd = "Y";
		                    }
		                    else
		                    {
		                        sql = "update tbldayendprocess set strDayEnd='N',strShiftEnd='Y' "
		                                + "where strPOSCode='" + posCode + "' and strDayEnd='N'";
		                        WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		                        //clsGlobalVarClass.dbMysql.execute(sql);
		                        shift = shiftNo;
		                    }
		                    sql = "insert into tbldayendprocess(strPOSCode,dtePOSDate,strDayEnd,intShiftCode,strShiftEnd"
		                            + ",strUserCreated,dteDateCreated) "
		                            + "values('" + posCode + "','" + newStartDate + "','N'," + (shift + 1)
		                            + ",'','" + strUserCode + "','" + getCurrentDateTime() + "')";
		                    
		                    WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		                    //clsGlobalVarClass.dbMysql.execute(sql);
		                    
		                    gShiftEnd = "";
		                    gDayEnd = "N";
		                    gShiftNo = (shift + 1);
		               //     clsGlobalVarClass.setStartDate(newStartDate);
		                 //   clsGlobalVarClass.funSetPOSDate();
		                 //   System.out.println("Shift = " + gShifts);
		
		                    if (status.equals("ShiftEnd"))
		                    {
		                        //shiftEnd=shiftNo-1;
		                        shiftEnd = shiftNo;
		                    }
		                    else
		                    {
		                        shiftEnd = shiftNo;
		                    }
		
		                    //  Calculate Total Cash Amt, Total Advance Amt, Total Receipts , Total Payments, Ttoal Discount Amt
		                    //  , No of Discounted Bills, No of Total bills.
		                    funCalculateDayEndCash(shiftDate, shiftEnd, posCode);
		
		                    // Update tbldayendprocess table fields     
		                    funUpdateDayEndFields(shiftDate, shiftEnd, dayEnd, posCode,strUserCode);
		                    String posDate = billDate;
		
		                    JSONObject jsonPostSalesDataToMMS = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gPostSalesDataToMMS");
		        			String gPostSalesDataToMMS=jsonPostSalesDataToMMS.get("gPostSalesDataToMMS").toString();
		        		
		        			 JSONObject jsItemType = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gItemType");
			        		String gItemType=jsItemType.get("gItemType").toString();
			        		
		                    // Post POS Item Sale Data to MMS.
		                    if (gPostSalesDataToMMS.equals("Y"))
		                    {
		                    	  String WSStockAdjustmentCode =objSynchronizePOSDataToHO.funPostPOSItemSalesDataAuto(gItemType,posCode,posDate,posDate,strClientCode); 
	                      		//   sql = "update tbldayendprocess set strWSStockAdjustmentNo='" + WSStockAdjustmentCode + "'" + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
		                  		//   clsGlobalVarClass.dbMysql.execute(sql);
		                  		 String exbillGenCode =objSynchronizePOSDataToHO.funPostPOSSalesDataToExciseAuto(gItemType,posCode,posDate,posDate,strClientCode);
		                  		
		                    }
		                    JSONObject JSONEnableShiftYN = objSetupDao.funGetParameterValuePOSWise(strUserCode,posCode, "gEnableShiftYN");
		        			String gEnableShiftYN=JSONEnableShiftYN.get("gEnableShiftYN").toString();
		        			
		        			JSONObject JSONLockDataOnShiftYN = objSetupDao.funGetParameterValuePOSWise(strUserCode,posCode, "gLockDataOnShiftYN");
		        			String gLockDataOnShiftYN=JSONLockDataOnShiftYN.get("gLockDataOnShiftYN").toString();
		                    // Transfer Billing Data from Live Tables To QFile Tables.
		                    if (gEnableShiftYN.equals("Y") && gLockDataOnShiftYN.equals("Y"))
		                    {
		                        funInsertQBillData(posCode,strClientCode);
		                    }
		
		                    // Post Sales Transaction Data, Inventory Transaction Data, Audit Transaction Data, Customer Masterok
		                    // and Customer Area Master to HO.
		                    String gConnectionActive="Y";
		                    if (gConnectionActive.equals("Y"))
		                    {
		                    	
		                    	   funInvokeHOWebserviceForTrans("All", "Day End",strClientCode,posCode);
			                       funPostCustomerDataToHOPOS(strClientCode,posCode);
			                       funPostCustomerAreaDataToHOPOS(strClientCode,posCode);
		                       
		                    }
		
		                    // Post Day End Table Data to HO.    
		                    funPostDayEndData(newStartDate, shift,strClientCode,posCode);
		                    if(clsDayEndProcessDao.gTransactionType.equalsIgnoreCase("ShiftEnd"))
		                    {
		                        retvalue = funDayEndflash(strClientCode, posCode, billDate, shiftNo,strUserCode);
		                      //  retvalue = funDayEndflash(posCode, billDate, shiftNo);
		                    }
		                }
	                }
	            }
	        }
	        catch (Exception ex)
	        {
	            //clsGlobalVarClass.dbMysql.funRollbackTransaction();
	            ex.printStackTrace();
	        }
	        finally
	        {
	            return retvalue;
	        }
	    }

    public int funDayEndflash(String clientCode,String posCode, String billDate, int shiftNo,String strUserCode)
    {
        try
        {
            String filePath = System.getProperty("user.dir");
            filePath = filePath + "/Temp/Temp_DayEndReport.txt";

            JSONObject jsPrintType = objSetupDao.funGetParameterValuePOSWise(clientCode,posCode, "gPrintType");
			String gPrintType=jsPrintType.get("gPrintType").toString();
		
            if (gPrintType.equalsIgnoreCase("Text File"))
            {
                
                obTextFileGenerationForPrinting2.funGenerateTextDayEndReport(posCode, billDate, "", shiftNo,clientCode,strUserCode);
            }
            else
            {
                
            	obTextFileGenerationForPrinting2.funGenerateTextDayEndReport(posCode, billDate, "", shiftNo,clientCode,strUserCode);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return 1;
        }
    }
	
    public int funShiftCardBalToRevenueTable(String posCode, String posDate,String strClientCode,String strUserCode) throws Exception
    {
        String sql = "select a.strCardTypeCode from tbldebitcardtype a "
                + "where a.intValidityDays=1";
        Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
        List list=query.list();
        if(list.size()>0)
        {
            String cardType =((Object)list.get(0)).toString();

            sql = "select a.strCardNo,a.dblRedeemAmt "
                    + " from tbldebitcardmaster a "
                    + "where a.strCardTypeCode='" + cardType+ "' and a.dblRedeemAmt > 0";
            Query qCardDtl=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            List listCD=qCardDtl.list();
            if(listCD.size()>0)
            {
            	for(int i=0;i<listCD.size();i++)
            	{
	            		Object ob[]=(Object[])listCD.get(i);
	            		sql = "insert into tbldebitcardrevenue (strCardNo,dblCardAmt,strPOSCode,dtePOSDate,dteDate"
	                        + ",strClientCode,strDataPostFlag,strUserCreated) values"
	                        + "('" + ob[0].toString() + "','" + ob[1].toString() + "','" + posCode + "','" + posDate + "'"
	                        + ",'" + getCurrentDateTime() + "','" + strClientCode + "','N'"
	                        + ",'" + strUserCode + "')";
	            		
	                //System.out.println(sql);
	            		
	                Query query1=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                query1.executeUpdate();
	                
	                sql = "update tbldebitcardmaster set dblRedeemAmt=0 where strCardNo='" + ob[0].toString() + "'";
	                query1=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	                query1.executeUpdate();
            	}
            }
       
        }
       

        return 1;
    }

    // Function to send bill data to sanguine cms.
    public int funPostSanguineCMSData(String posCode, String billDate,String ClientCode,String userCode)
    {
        int res = 0;
        String roundOffAccCode = "";
        double roundOff = 0, creditAmt = 0, debitAmt = 0;
        try
        {
        	String POSName="";
        	String sqlpos="select strPosName from tblposmaster where strPOSCode='"+posCode+"'";
        	Query qPos=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlpos);
            List listPOS=qPos.list();
            if(listPOS.size()>0)
            {
            	POSName=((Object)listPOS.get(0)).toString();
            }
            JSONObject jObj = new JSONObject();
            JSONObject JSONgCMSPOSCode = objSetupDao.funGetParameterValuePOSWise(ClientCode,posCode, "gCMSPOSCode");
			String gCMSPOSCode=JSONgCMSPOSCode.get("gCMSPOSCode").toString();
			
			jObj.put("POSCode", posCode);
            jObj.put("POSDate", billDate);
            jObj.put("User", userCode);

            String sql_SubGroupWise = "SELECT a.strPOSCode, IFNULL(d.strSubGroupCode,'NA'), IFNULL(d.strSubGroupName,'NA'), SUM(b.dblAmount), DATE(a.dteBillDate),d.strAccountCode "
                    + "FROM tblbillhd a,tblbilldtl b,tblitemmaster c,tblsubgrouphd d  "
                    + "WHERE a.strPOSCode='" + posCode + "' "
                    + "and a.strBillNo=b.strBillNo "
                    + " and b.strItemCode=c.strItemCode "
                    + " and c.strSubGroupCode=d.strSubGroupCode "
                    + "GROUP BY d.strSubGroupCode,d.strSubGroupName ";

            JSONArray arrObjSubGroupwise = new JSONArray();
            Query qSubGroupWise=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SubGroupWise);
            List listSBGroup=qSubGroupWise.list();
            if(listSBGroup.size()>0)
            {
            	
	           for(int i=0;i<listSBGroup.size();i++)
	           {
	        	   Object ob[]=(Object[])listSBGroup.get(i);
	                JSONObject objSubGroupWise = new JSONObject();
	                creditAmt += Double.parseDouble(ob[3].toString());
	                objSubGroupWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objSubGroupWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
	                objSubGroupWise.put("CRAmt", Double.parseDouble(ob[3].toString()));
	                objSubGroupWise.put("DRAmt", 0);
	                objSubGroupWise.put("ClientCode", ClientCode);
	                objSubGroupWise.put("BillDate", ob[4].toString());
	                objSubGroupWise.put("CMSPOSCode", gCMSPOSCode);
	                objSubGroupWise.put("POSCode", posCode);
	                objSubGroupWise.put("BillDateTo",ob[4].toString());
	                objSubGroupWise.put("AccountCode", ob[5].toString());
	                arrObjSubGroupwise.put(objSubGroupWise);
	            }
            }
            jObj.put("SubGroupwise", arrObjSubGroupwise);

            String sql_TaxWise = "SELECT a.strPOSCode,c.strTaxCode,c.strTaxDesc, SUM(b.dblTaxAmount), DATE(a.dteBillDate),c.strAccountCode "
                    + "FROM tblbillhd a,tblbilltaxdtl b , tbltaxhd c "
                    + "where a.strPOSCode='" + posCode + "' "
                    + "and a.strBillNo=b.strBillNo "
                    + "and b.strTaxCode=c.strTaxCode "
                    + "GROUP BY c.strTaxCode ";

            JSONArray arrObjTaxwise = new JSONArray();
            Query qTaxWise=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_TaxWise);
            List listTaxWise=qTaxWise.list();
            if(listTaxWise.size()>0)
            {
            	
	           for(int i=0;i<listTaxWise.size();i++)
	           {
	        	   Object ob[]=(Object[])listTaxWise.get(i);
	               
	               JSONObject objTaxWise = new JSONObject();
	               creditAmt += Double.parseDouble(ob[3].toString());
	                objTaxWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objTaxWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
	                objTaxWise.put("CRAmt", Double.parseDouble(ob[3].toString()));
	                objTaxWise.put("DRAmt", 0);
	                objTaxWise.put("ClientCode", ClientCode);
	                objTaxWise.put("BillDate", ob[4].toString());
	                objTaxWise.put("CMSPOSCode", gCMSPOSCode);
	                objTaxWise.put("POSCode", posCode);
	                objTaxWise.put("BillDateTo",ob[4].toString());
	                objTaxWise.put("AccountCode", ob[5].toString());
	                arrObjTaxwise.put(objTaxWise);
	            }
            }
            jObj.put("Taxwise", arrObjTaxwise);

            String sql_Discount = "select a.strPOSCode,sum(a.dblDiscountAmt),date(a.dteBillDate),b.strRoundOff,b.strTip,b.strDiscount "
                    + "from tblbillhd a,tblposmaster b "
                    + "where a.strPOSCode='" + posCode + "' "
                    + " and a.strPOSCode=b.strPosCode "
                    + "group by a.strPOSCode";

            JSONArray arrObjDiscountwise = new JSONArray();
            Query qDiscount=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Discount);
            List listDiscount=qDiscount.list();
            if(listDiscount.size()>0)
            {
               for(int i=0;i<listDiscount.size();i++)
	           {
	        	   Object ob[]=(Object[])listDiscount.get(i);
	               if (Double.parseDouble(ob[1].toString()) > 0)
		                {
		                    JSONObject objDiscount = new JSONObject();
		                    debitAmt += Double.parseDouble(ob[1].toString());
		                    roundOffAccCode = ob[3].toString();
		                    objDiscount.put("RVCode", ob[0].toString() + "-Discount");
		                    objDiscount.put("RVName", "Discount");
		                    objDiscount.put("CRAmt", 0);
		                    objDiscount.put("DRAmt", Double.parseDouble(ob[1].toString()));
		                    objDiscount.put("ClientCode", ClientCode);
		                    objDiscount.put("BillDate", ob[2].toString());
		                    objDiscount.put("CMSPOSCode", gCMSPOSCode);
		                    objDiscount.put("POSCode", posCode);
		                    objDiscount.put("BillDateTo", ob[2].toString());
		                    objDiscount.put("AccountCode", ob[5].toString());
		                    arrObjDiscountwise.put(objDiscount);
		                }
		            }
            }    
	         jObj.put("Discountwise", arrObjDiscountwise);

            String sql_Settlement = "SELECT a.strPOSCode, IFNULL(b.strSettlementCode,''), IFNULL(c.strSettelmentDesc,''), IFNULL(SUM(b.dblSettlementAmt),0), DATE(a.dteBillDate),c.strAccountCode "
                    + "FROM tblbillhd a,tblbillsettlementdtl b ,tblsettelmenthd c  "
                    + "WHERE c.strSettelmentType='Member' "
                    + "AND a.strPOSCode='" + posCode + "' "
                    + "and a.strBillNo=b.strBillNo "
                    + "and b.strSettlementCode=c.strSettelmentCode "
                    + "GROUP BY a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc ";
            JSONArray arrObjMemberSettlewise = new JSONArray();
            Query qCashSettlement=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Settlement);
            List listCashSettlement=qCashSettlement.list();
            if(listCashSettlement.size()>0)
            {
               for(int i=0;i<listCashSettlement.size();i++)
	           {
	        	   Object ob[]=(Object[])listCashSettlement.get(i);
	                JSONObject objSettlementWise = new JSONObject();
	                debitAmt +=Double.parseDouble(ob[3].toString());
	                
	                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
	                objSettlementWise.put("CRAmt", 0);
	                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
	                objSettlementWise.put("ClientCode", ClientCode);
	                objSettlementWise.put("BillDate", ob[4].toString());
	                objSettlementWise.put("CMSPOSCode", gCMSPOSCode);
	                objSettlementWise.put("POSCode", posCode);
	                objSettlementWise.put("BillDateTo",ob[4].toString());
	                objSettlementWise.put("AccountCode", ob[5].toString());
	                arrObjMemberSettlewise.put(objSettlementWise);
	                
	            }
            }
            
            jObj.put("MemberSettlewise", arrObjMemberSettlewise);

            sql_Settlement = "SELECT a.strPOSCode, IFNULL(b.strSettlementCode,''), IFNULL(c.strSettelmentDesc,''), IFNULL(SUM(b.dblSettlementAmt),0), DATE(a.dteBillDate),c.strAccountCode "
                    + "FROM tblbillhd a,tblbillsettlementdtl b ,tblsettelmenthd c  "
                    + "WHERE c.strSettelmentType='Cash' "
                    + "AND a.strPOSCode='" + posCode + "' "
                    + "and a.strBillNo=b.strBillNo "
                    + "and b.strSettlementCode=c.strSettelmentCode "
                    + "GROUP BY a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc";
            JSONArray arrObjCashSettlewise = new JSONArray();
            Query qMemberSettlement=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Settlement);
            List listMemberSettlement=qMemberSettlement.list();
            if(listMemberSettlement.size()>0)
            {
               for(int i=0;i<listMemberSettlement.size();i++)
	           {
	        	   Object ob[]=(Object[])listMemberSettlement.get(i);
	        
	                JSONObject objSettlementWise = new JSONObject();
	                debitAmt +=Double.parseDouble(ob[3].toString());
	                
	                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
	                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
	                objSettlementWise.put("CRAmt", 0);
	                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
	                objSettlementWise.put("ClientCode", ClientCode);
	                objSettlementWise.put("BillDate", ob[4].toString());
	                objSettlementWise.put("CMSPOSCode", gCMSPOSCode);
	                objSettlementWise.put("POSCode", posCode);
	                objSettlementWise.put("BillDateTo",ob[4].toString());
	                objSettlementWise.put("AccountCode", ob[5].toString());
	                arrObjCashSettlewise.put(objSettlementWise);
	            }
            }
            jObj.put("CashSettlewise", arrObjCashSettlewise);

            String sql_MemberCL = "select left(a.strCustomerCode,8),d.strCustomerName,a.strBillNo,date(a.dteBillDate)"
                    + ",b.dblSettlementAmt,c.strAccountCode "
                    + "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d "
                    + "where a.strBillNo=b.strBillNo "
                    + "and b.strSettlementCode=c.strSettelmentCode "
                    + "and a.strCustomerCode=d.strCustomerCode "
                    + "and a.strPOSCode='" + posCode + "'  "
                    + "and c.strSettelmentType='Member'";
            JSONArray arrObjMemberClData = new JSONArray();
            Query qMemeberCL=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_MemberCL);
            List listMemeberCL=qMemeberCL.list();
            if(listMemeberCL.size()>0)
            {
               for(int i=0;i<listMemeberCL.size();i++)
	           {
	        	   Object ob[]=(Object[])listMemeberCL.get(i);
	       
	                JSONObject objMemeberCL = new JSONObject();
	                objMemeberCL.put("DebtorCode", ob[0].toString().trim());
	                objMemeberCL.put("DebtorName", ob[1].toString());
	                objMemeberCL.put("BillNo", ob[2].toString());
	                objMemeberCL.put("BillDate", ob[3].toString());
	                objMemeberCL.put("BillAmt", ob[4].toString());
	                objMemeberCL.put("ClientCode", ClientCode);
	                objMemeberCL.put("CMSPOSCode", gCMSPOSCode);
	                objMemeberCL.put("POSCode", posCode);
	                objMemeberCL.put("POSName", POSName);//posname
	                objMemeberCL.put("BillDateTo", ob[3].toString());
	                objMemeberCL.put("AccountCode", ob[5].toString());
	                arrObjMemberClData.put(objMemeberCL);
	            }
            }
            
            jObj.put("MemberCLData", arrObjMemberClData);

            String posDate = billDate;
            roundOffAccCode = "";
            String sql = "select strRoundOff from tblposmaster where strPOSCode='" + posCode + "' ";
            Query qRF=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            List listRF=qRF.list();

            if(listRF.size()>0)
            {
            	
            	roundOffAccCode = ((Object)listRF.get(0)).toString();
            }
            
            JSONArray arrObjRoundOff = new JSONArray();
            JSONObject objRoundOff = new JSONObject();
            objRoundOff.put("RVCode", posCode + "-Roff");
            objRoundOff.put("RVName", POSName + "-Roff");
            roundOff = debitAmt - creditAmt;
            if (roundOff < 0)
            {
                roundOff = roundOff * (-1);
                objRoundOff.put("DRAmt", roundOff);
                objRoundOff.put("CRAmt", 0);
            }
            else
            {
                objRoundOff.put("DRAmt", 0);
                objRoundOff.put("CRAmt", roundOff);
            }
            objRoundOff.put("ClientCode", ClientCode);
            objRoundOff.put("BillDate", posDate);
            objRoundOff.put("CMSPOSCode", gCMSPOSCode);
            objRoundOff.put("POSCode", posCode);
            objRoundOff.put("BillDateTo", posDate);
            objRoundOff.put("AccountCode", roundOffAccCode);
            arrObjRoundOff.put(objRoundOff);

            jObj.put("RoundOffDtl", arrObjRoundOff);

            JSONObject JSONWebBooksWebServiceURL = objSetupDao.funGetParameterValuePOSWise(ClientCode,posCode, "gWebBooksWebServiceURL");
			String gWebBooksWebServiceURL=JSONWebBooksWebServiceURL.get("gWebBooksWebServiceURL").toString();
			
            String cmsURL = gWebBooksWebServiceURL + "/funPostRevenueToCMS";
            URL url = new URL(cmsURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(jObj.toString().getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
            {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = "", op = "";
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null)
            {
                op += output;
            }
            System.out.println(op);
            conn.disconnect();
            if (op.equals("false"))
            {
                res = 0;
            }
            else
            {
                res = 1;
            }
        }
        catch (Exception e)
        {
            res = 0;
            e.printStackTrace();
          //  JOptionPane.showMessageDialog(null, "Check CMS Web Service URL and Internet Connection!!!"); // there is this at null postion
        }
        finally
        {
            return res;
        }
    }
  
    
    
    
  // Function to send bill data to others cms.
  public int funPostBillDataToCMS(String posCode, String billDate,String ClientCode,String userCode) throws Exception
   {
       int res = 0;
       double roundOff = 0, creditAmt = 0, debitAmt = 0;
       try
       {
       	String POSName="";
       	String sqlpos="select strPosName from tblposmaster where strPOSCode='"+posCode+"'";
       	Query qPos=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlpos);
           List listPOS=qPos.list();
           if(listPOS.size()>0)
           {
           	POSName=((Object)listPOS.get(0)).toString();
           }
           
           JSONObject JSONgCMSPOSCode = objSetupDao.funGetParameterValuePOSWise(ClientCode,posCode, "gCMSPOSCode");
		String CMSPOSCode=JSONgCMSPOSCode.get("gCMSPOSCode").toString();
		
           JSONObject jObj = new JSONObject();
           JSONArray arrObj = new JSONArray();

           String sql_SubGroupWise = "select a.strPOSCode,ifnull(d.strSubGroupCode,'NA'),ifnull(d.strSubGroupName,'NA')"
                   + ",sum(b.dblAmount),date(a.dteBillDate) "
                   + "from tblbillhd a left outer join tblbilldtl b on a.strBillNo=b.strBillNo "
                   + "left outer join tblitemmaster c on b.strItemCode=c.strItemCode "
                   + "left outer join tblsubgrouphd d on c.strSubGroupCode=d.strSubGroupCode "
                   + "where a.strPOSCode='" + posCode + "' "
                   + "group by d.strSubGroupCode,d.strSubGroupName";
           //System.out.println(sql_SubGroupWise);
           Query qSubGroupWise=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SubGroupWise);
           List listSBGroup=qSubGroupWise.list();
           if(listSBGroup.size()>0)
           {
           	
           for(int i=0;i<listSBGroup.size();i++)
           {
        	   	Object ob[]=(Object[])listSBGroup.get(i);
        	   JSONObject objSubGroupWise = new JSONObject();
                objSubGroupWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
                objSubGroupWise.put("RVName", POSName + "-" + ob[2].toString());
                objSubGroupWise.put("CRAmt", ob[3].toString());//double
                objSubGroupWise.put("DRAmt", 0);
                objSubGroupWise.put("ClientCode", ClientCode);
                objSubGroupWise.put("BillDate", ob[4].toString());
                objSubGroupWise.put("CMSPOSCode", CMSPOSCode);
                objSubGroupWise.put("POSCode", posCode);
                objSubGroupWise.put("BillDateTo", ob[4].toString());
                arrObj.put(objSubGroupWise);
            }
           }

           String sql_TaxWise = "select a.strPOSCode,c.strTaxCode,c.strTaxDesc,sum(b.dblTaxAmount),date(a.dteBillDate) "
                   + "from tblbillhd a left outer join tblbilltaxdtl b on a.strBillNo=b.strBillNo "
                   + "left outer join tbltaxhd c on b.strTaxCode=c.strTaxCode "
                   + "where a.strPOSCode='" + posCode + "' "
                   + "group by c.strTaxCode";
           //System.out.println(sql_TaxWise);

           Query qTaxWise=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_TaxWise);
           List listTaxWise=qTaxWise.list();
           if(listTaxWise.size()>0)
           {
           	
           for(int i=0;i<listTaxWise.size();i++)
           {
        	   Object ob[]=(Object[])listTaxWise.get(i);
               
               JSONObject objTaxWise = new JSONObject();
               creditAmt += Double.parseDouble(ob[3].toString());
                objTaxWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
                objTaxWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
                objTaxWise.put("CRAmt", Double.parseDouble(ob[3].toString()));
                objTaxWise.put("DRAmt", 0);
                objTaxWise.put("ClientCode", ClientCode);
                objTaxWise.put("BillDate", ob[4].toString());
                objTaxWise.put("CMSPOSCode", CMSPOSCode);
                objTaxWise.put("POSCode", posCode);
                objTaxWise.put("BillDateTo",ob[4].toString());
             
                arrObj.put(objTaxWise);
            }
           }
          
           String sql_Discount = "select strPOSCode,sum(dblDiscountAmt),date(dteBillDate) "
                   + "from tblbillhd "
                   + "where strPOSCode='" + posCode + "' "
                   + "group by strPOSCode";
           
           
           Query qDiscount=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Discount);
           List listDiscount=qDiscount.list();
           if(listDiscount.size()>0)
           {
              for(int i=0;i<listDiscount.size();i++)
           {
        	   Object ob[]=(Object[])listDiscount.get(i);
               if (Double.parseDouble(ob[1].toString()) > 0)
	                {
	                    JSONObject objDiscount = new JSONObject();
	                    objDiscount.put("RVCode", ob[0].toString() + "-Discount");
	                    objDiscount.put("RVName", "Discount");
	                    objDiscount.put("CRAmt", 0);
	                    objDiscount.put("DRAmt", Double.parseDouble(ob[1].toString()));
	                    objDiscount.put("ClientCode", ClientCode);
	                    objDiscount.put("BillDate", ob[2].toString());
	                    objDiscount.put("CMSPOSCode",CMSPOSCode);
	                    objDiscount.put("POSCode", posCode);
	                    objDiscount.put("BillDateTo", ob[2].toString());
	                    arrObj.put(objDiscount);
	                }
	            }
           }    
          
           /*
            String sql_RoundOff="SELECT strPOSCode,sum((dbltaxamt + dblsubtotal) - dblgrandtotal)"
            + ",date(dteBillDate) "
            + "from tblbillhd where strPOSCode= '"+clsGlobalVarClass.gPOSCode+"'";
            ResultSet rsRoundOff=clsGlobalVarClass.dbMysql.executeResultSet(sql_RoundOff);
            while(rsRoundOff.next())
            {
            JSONObject objRoundOff=new JSONObject();
            objRoundOff.put("RVCode",rsRoundOff.getString(1)+"-Roff");
            objRoundOff.put("RVName",clsGlobalVarClass.gPOSName+"-Roff");
            objRoundOff.put("CRAmt",0);
            objRoundOff.put("DRAmt",rsRoundOff.getDouble(2));
            objRoundOff.put("ClientCode",clsGlobalVarClass.gClientCode);
            objRoundOff.put("BillDate",rsRoundOff.getString(3));
            objRoundOff.put("CMSPOSCode",clsGlobalVarClass.gCMSPOSCode);
            objRoundOff.put("POSCode",clsGlobalVarClass.gPOSCode);
            objRoundOff.put("BillDateTo",rsRoundOff.getString(3));
            arrObj.add(objRoundOff);
            }
            rsRoundOff.close();*/
           String sql_Settlement = "select a.strPOSCode,ifnull(b.strSettlementCode,'')"
                   + " ,ifnull(c.strSettelmentDesc,''),ifnull(sum(b.dblSettlementAmt),0),date(a.dteBillDate) "
                   + " from tblbillhd a left outer join tblbillsettlementdtl b on a.strBillNo=b.strBillNo "
                   + " left outer join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode "
                   + " where c.strSettelmentType='Member' and a.strPOSCode='" + posCode + "' "
                   + " group by a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc";
           //System.out.println(sql_Settlement);

           Query qCashSettlement=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Settlement);
           List listCashSettlement=qCashSettlement.list();
           if(listCashSettlement.size()>0)
           {
              for(int i=0;i<listCashSettlement.size();i++)
           {
        	   Object ob[]=(Object[])listCashSettlement.get(i);
                JSONObject objSettlementWise = new JSONObject();
                
                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
                objSettlementWise.put("CRAmt", 0);
                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
                objSettlementWise.put("ClientCode", ClientCode);
                objSettlementWise.put("BillDate", ob[4].toString());
                objSettlementWise.put("CMSPOSCode", CMSPOSCode);
                objSettlementWise.put("POSCode", posCode);
                objSettlementWise.put("BillDateTo",ob[4].toString());
               
                arrObj.put(objSettlementWise);
                
            }
           }
          
           sql_Settlement = "select a.strPOSCode,ifnull(b.strSettlementCode,'')"
                   + " ,ifnull(c.strSettelmentDesc,''),ifnull(sum(b.dblSettlementAmt),0),date(a.dteBillDate) "
                   + " from tblbillhd a left outer join tblbillsettlementdtl b on a.strBillNo=b.strBillNo "
                   + " left outer join tblsettelmenthd c on b.strSettlementCode=c.strSettelmentCode "
                   + " where c.strSettelmentType='Cash' and a.strPOSCode='" + posCode + "' "
                   + " group by a.strPOSCode, b.strSettlementCode, c.strSettelmentDesc";
           //System.out.println(sql_Settlement);
           qCashSettlement=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Settlement);
           listCashSettlement=qCashSettlement.list();
           if(listCashSettlement.size()>0)
           {
              for(int i=0;i<listCashSettlement.size();i++)
           {
        	   Object ob[]=(Object[])listCashSettlement.get(i);
                JSONObject objSettlementWise = new JSONObject();
                objSettlementWise.put("RVCode", ob[0].toString() + "-" + ob[1].toString());
                objSettlementWise.put("RVName", POSName + "-" + ob[2].toString()); //posName
                objSettlementWise.put("CRAmt", 0);
                objSettlementWise.put("DRAmt", Double.parseDouble(ob[3].toString()));
                objSettlementWise.put("ClientCode", ClientCode);
                objSettlementWise.put("BillDate", ob[4].toString());
                objSettlementWise.put("CMSPOSCode", CMSPOSCode);
                objSettlementWise.put("POSCode", posCode);
                objSettlementWise.put("BillDateTo",ob[4].toString());
               
                arrObj.put(objSettlementWise);
           }
           }
         
           String posDate = billDate;
           JSONObject objRoundOff = new JSONObject();
           objRoundOff.put("RVCode", posCode + "-Roff");
           objRoundOff.put("RVName", POSName + "-Roff");
           roundOff = debitAmt - creditAmt;
           if (roundOff < 0)
           {
               roundOff = roundOff * (-1);
               objRoundOff.put("DRAmt", roundOff);
               objRoundOff.put("CRAmt", 0);
           }
           else
           {
               objRoundOff.put("DRAmt", 0);
               objRoundOff.put("CRAmt", roundOff);
           }
           objRoundOff.put("ClientCode", ClientCode);
           objRoundOff.put("BillDate", posDate);
           objRoundOff.put("CMSPOSCode", CMSPOSCode);
           objRoundOff.put("POSCode", posCode);
           objRoundOff.put("BillDateTo", posDate);
           arrObj.put(objRoundOff);

           jObj.put("BillInfo", arrObj);
           //System.out.println(jObj);
           JSONObject JSONWebBooksWebServiceURL = objSetupDao.funGetParameterValuePOSWise(ClientCode,posCode, "gWebBooksWebServiceURL");
		String gCMSWebServiceURL=JSONWebBooksWebServiceURL.get("gWebBooksWebServiceURL").toString();
	    String cmsURL = gCMSWebServiceURL + "/funPostRVDataToCMS";
           //System.out.println(cmsURL);
           URL url = new URL(cmsURL);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           conn.setDoOutput(true);
           conn.setRequestMethod("POST");
           conn.setRequestProperty("Content-Type", "application/json");
           OutputStream os = conn.getOutputStream();
           os.write(jObj.toString().getBytes());
           os.flush();

           if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)
           {
               throw new RuntimeException("Failed : HTTP error code : "
                       + conn.getResponseCode());
           }
           BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
           String output = "", op = "";
           System.out.println("Output from Server .... \n");
           while ((output = br.readLine()) != null)
           {
               op += output;
           }
           System.out.println(op);
           conn.disconnect();
           if (op.equals("false"))
           {
               res = 0;
           }
           else
           {
               JSONObject jObjCL = new JSONObject();
               JSONArray arrObjCL = new JSONArray();
               /*String sql_MemberCL="select strCustomerCode,'',strBillNo,date(dteBillDate),dblGrandTotal "
                + "from tblbillhd "
                + "where strPOSCode='"+clsGlobalVarClass.gPOSCode+"' "
                + "and strSettelmentMode='Member'";*/
               String sql_MemberCL = "select left(a.strCustomerCode,8),d.strCustomerName,a.strBillNo,date(a.dteBillDate),b.dblSettlementAmt "
                       + "from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d "
                       + "where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                       + "and a.strCustomerCode=d.strCustomerCode "
                       + "and a.strPOSCode='" + posCode + "' "
                       + "and c.strSettelmentType='Member'";
               //System.out.println(sql_MemberCL);
               Query qMemeberCL=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_MemberCL);
               List listMemeberCL=qMemeberCL.list();
               if(listMemeberCL.size()>0)
               {
                  for(int i=0;i<listMemeberCL.size();i++)
   	           {
   	        	   Object ob[]=(Object[])listMemeberCL.get(i);
   	       
                    JSONObject objMemeberCL = new JSONObject();
                    objMemeberCL.put("DebtorCode", ob[0].toString().trim());
                    objMemeberCL.put("DebtorName", ob[1].toString());
                    objMemeberCL.put("BillNo", ob[2].toString());
                    objMemeberCL.put("BillDate",ob[3].toString());
                    objMemeberCL.put("BillAmt", ob[4].toString());
                    objMemeberCL.put("ClientCode", ClientCode);
                    objMemeberCL.put("CMSPOSCode", CMSPOSCode);
                    objMemeberCL.put("POSCode", posCode);
                    objMemeberCL.put("POSName", POSName);
                    objMemeberCL.put("BillDateTo", ob[3].toString());
                    arrObjCL.put(objMemeberCL);
   	           }
               }
               

               jObjCL.put("MemberCLInfo", arrObjCL);
               //System.out.println(jObjCL);
               
               String cmsURLCL = gCMSWebServiceURL + "/funPostCLDataToCMS";
               //System.out.println(cmsURLCL);
               URL urlCL = new URL(cmsURLCL);
               HttpURLConnection connCL = (HttpURLConnection) urlCL.openConnection();
               connCL.setDoOutput(true);
               connCL.setRequestMethod("POST");
               connCL.setRequestProperty("Content-Type", "application/json");
               OutputStream osCL = connCL.getOutputStream();
               osCL.write(jObjCL.toString().getBytes());
               osCL.flush();

               if (connCL.getResponseCode() != HttpURLConnection.HTTP_CREATED)
               {
                   throw new RuntimeException("Failed : HTTP error code : "
                           + connCL.getResponseCode());
               }
               BufferedReader brCL = new BufferedReader(new InputStreamReader((connCL.getInputStream())));
               String output1 = "", op1 = "";
               System.out.println("Output from Server .... \n");
               while ((output1 = brCL.readLine()) != null)
               {
                   op1 += output1;
               }
               connCL.disconnect();
               System.out.println(op1);
               if (op1.equals("false"))
               {
                   res = 0;
               }
               else
               {
                   res = 1;
               }
           }
       }
       catch (Exception e)
       {
           res = 0;
           e.printStackTrace();
         //  JOptionPane.showMessageDialog(null, "Check CMS Web Service URL and Internet Connection!!!"); // there is this at null postion
       }
       finally
       {
           return res;
       }
   }

       
  public String funGenerateNextCode()
   {
       String code = "", transId = "";
       try
       {
           int cn = 0;
           Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery("select count(*) from tblcashmanagement");
           List list=query.list();
           		if(list.size()>0)
           		{
           			cn=Integer.parseInt(((Object)list.get(0)).toString());
           		}
           if (cn > 0)
           {
           	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery("select max(strTransID) from tblcashmanagement");
               list=query.list();
				if(list.size()>0)
				{
					code=((Object)list.get(0)).toString();		
				}
           	
               int length = code.length();
               String nextCode = code.substring(2, length);
               int nextCount = Integer.parseInt(nextCode);
               nextCount++;
               transId = "TR" + String.format("%05d", nextCount);
           }
           else
           {
               transId = "TR00001";
           }
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
       return transId;
   }
       
   // Function to calculate total settlement amount and assigns global variables, which are shown on day end/shift end form.
   // This function calculate settlement amount from live tables.    
   public int funCalculateDayEndCash(String posDate, int shiftCode, String posCode)
   {
       double sales = 0.00, totalDiscount = 0.00, totalSales = 0.00, noOfDiscountedBills = 0.00;
       double advCash = 0.00, cashIn = 0.00, cashOut = 0.00, totalFloat = 0.00;
       try
       {
           String sql = "SELECT c.strSettelmentDesc,sum(ifnull(b.dblSettlementAmt,0)),sum(a.dblDiscountAmt),c.strSettelmentType"
                   + " FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                   + " Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
                   + " and date(a.dteBillDate ) ='" + posDate + "' and a.strPOSCode='" + posCode + "'"
                   + " and a.intShiftCode=" + shiftCode
                   + " GROUP BY c.strSettelmentDesc,a.strPosCode";
           //System.out.println(sql);
           Query qSettlementAmt=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           List listSettlementAmt=qSettlementAmt.list();
           if(listSettlementAmt.size()>0)
           {
           	for(int i=0;i<listSettlementAmt.size();i++)
           	{
           		Object ob[]=(Object[])listSettlementAmt.get(i);
           		 if (ob[3].toString().equalsIgnoreCase("Cash"))
                    {
                        sales = sales + (Double.parseDouble(ob[1].toString().toString()));
                    }
                    totalDiscount = totalDiscount + (Double.parseDouble(ob[2].toString().toString()));
                    totalSales = totalSales + (Double.parseDouble(ob[1].toString().toString()));
           	}
           }
           
           gTotalDiscounts = totalDiscount;
           gTotalCashSales = totalSales;
          

           sql = "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblbillhd "
                   + "Where date(dteBillDate ) ='" + posDate + "' and strPOSCode='" + posCode + "' "
                   + "and dblDiscountAmt > 0.00 and intShiftCode=" + shiftCode
                   + " GROUP BY strPosCode";
           Query qTotalDiscountBills=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           List listTotalDiscountBills=qTotalDiscountBills.list();
           if(listTotalDiscountBills.size()>0)
           {
           	Object ob[]=(Object[])listTotalDiscountBills.get(0);
               gNoOfDiscountedBills = Integer.parseInt(ob[0].toString());            	
           }
           
           sql = "select count(strBillNo) from tblbillhd where date(dteBillDate ) ='" + posDate + "' and "
                   + "strPOSCode='" + posCode + "' and intShiftCode=" + shiftCode + " "
                   + " GROUP BY strPosCode";
           
           Query qTotalBills=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           List listTotalBills=qTotalBills.list();
           if(listTotalBills.size()>0)
           {
           //	Object ob[]=(Object[])listTotalBills.get(0);
           	gTotalBills = Integer.parseInt(((Object)listTotalBills.get(0)).toString());            	

           }
           gTotalCashSales = sales;
           
           sql = "select count(dblAdvDeposite) from tbladvancereceipthd "
                   + "where dtReceiptDate='" + posDate + "' and intShiftCode=" + shiftCode;
           
           Query qTotalAdvance=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           List listTotalAdvance=qTotalAdvance.list();
           int cntAdvDeposite=0;
           if(listTotalAdvance.size()>0)
           {
           	
           	cntAdvDeposite = Integer.parseInt(((Object)listTotalAdvance.get(0)).toString()); 	
           }
          
           if (cntAdvDeposite > 0)
           {
               //sql="select sum(dblAdvDeposite) from tbladvancereceipthd where dtReceiptDate='"+posDate+"'";
               sql = "select sum(b.dblAdvDepositesettleAmt) from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
                       + "where date(a.dtReceiptDate)='" + posDate + "' and a.strPOSCode='" + posCode + "' "
                       + "and c.strSettelmentCode=b.strSettlementCode and a.strReceiptNo=b.strReceiptNo "
                       + "and c.strSettelmentType='Cash' and a.intShiftCode=" + shiftCode;

               qTotalAdvance=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               listTotalAdvance=qTotalAdvance.list();
              if(listTotalAdvance.size()>0)
            {
            	
           	   advCash = Double.parseDouble(((Object)listTotalAdvance.get(0)).toString());
                  gTotalAdvanceAmt = advCash;
            }

           }
           

           //sql="select strTransType,sum(dblAmount) from tblcashmanagement where dteTransDate='"+posDate+"'"
           //    + " and strPOSCode='"+globalVarClass.gPOSCode+"' group by strTransType";
           sql = "select strTransType,sum(dblAmount),strCurrencyType from tblcashmanagement "
                   + "where date(dteTransDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                   + "and intShiftCode=" + shiftCode + " group by strTransType,strCurrencyType";
           
           
           Query qCashTransaction=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           List listCashTransaction=qCashTransaction.list();
           
           if(listCashTransaction.size()>0)
           {
            for(int i=0;i<listCashTransaction.size();i++)
            {
            	Object ob[]=(Object[])listCashTransaction.get(i);
                if (ob[0].toString().equals("Float"))
                {
                    cashIn = cashIn + (Double.parseDouble(ob[1].toString()));
                }
                if (ob[0].toString().equals("Transfer In"))
                {
                    cashIn = cashIn + (Double.parseDouble(ob[1].toString()));
                }

                if (ob[0].toString().equals("Withdrawal"))
                {
                    cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
                }
                if (ob[0].toString().equals("Transfer Out"))
                {
                    cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
                }
                if (ob[0].toString().equals("Payments"))
                {
                    cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
                }
                if (ob[0].toString().equals("Refund"))
                {
                    cashOut = cashOut + (Double.parseDouble(ob[1].toString()));
                }
            }
           }
           cashIn = cashIn + advCash + sales;
           gTotalReceipt = cashIn;
           gTotalPayments = cashOut;
           double inHandCash = (cashIn) - cashOut;
           gTotalCashInHand = inHandCash;
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
       return 1;
   }
    
   // Function to update values in tbldayendprocess table.
    // This function updates values from Live tables.    
    public int funUpdateDayEndFields(String posDate, int shiftNo, String dayEnd, String posCode,String userCode)
    {
        try
        {
            String sql = "update tbldayendprocess set dblTotalSale = IFNULL((select sum(b.dblSettlementAmt) "
                    + "TotalSale from tblbillhd a,tblbillsettlementdtl b "
                    + "where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '" + posDate + "' and "
                    + "a.strPOSCode = '" + posCode + "' and a.intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + " and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_1=="+sql);
            Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
          
            sql = "update tbldayendprocess set dteDayEndDateTime='" + getCurrentDateTime() + "'"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                    + "and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_2=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set strUserEdited='" + userCode + "'"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_3=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();

            sql = "update tbldayendprocess set dblNoOfBill = IFNULL((select count(*) NoOfBills "
                    + "from tblbillhd where Date(dteBillDate) = '" + posDate + "' and "
                    + "strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "' "
                    + " and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_4=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();

            sql = "update tbldayendprocess set dblNoOfVoidedBill = IFNULL((select count(DISTINCT strBillNo) "
                    + "NoOfVoidBills from tblvoidbillhd where date(dteModifyVoidBill) = " + "'" + posDate + "'"
                    + " and strPOSCode = '" + posCode + "' and strTransType = 'VB'"
                    + " and intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + " and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_5=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();

            sql = "update tbldayendprocess set dblNoOfModifyBill = IFNULL((select count(DISTINCT b.strBillNo) "
                    + "NoOfModifiedBills from tblbillhd a,tblvoidbillhd b where a.strBillNo=b.strBillNo"
                    + " and Date(b.dteModifyVoidBill) = '" + posDate + "' and b.strPOSCode='" + posCode + "'"
                    + " and b.strTransType = 'MB' and a.intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + " and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_6=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();

            sql = "update tbldayendprocess set dblHDAmt=IFNULL((select sum(a.dblGrandTotal) HD from tblbillhd a,"
                    + "tblhomedelivery b where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '" + posDate + "' and "
                    + "a.strPOSCode = '" + posCode + "' and a.intShiftCode=" + shiftNo + "), 0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + " and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_7=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            sql = "update tbldayendprocess set dblDiningAmt=IFNULL(( select sum(dblGrandTotal) Dining"
                    + " from tblbillhd where strTakeAway='No' and date(dteBillDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + "  and strBillNo NOT IN (select strBillNo from tblhomedelivery where strBillNo is not NULL) and intShiftCode=" + shiftNo + "),0)"
                    + "  where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + posCode + "' "
                    + "and intShiftCode=" + shiftNo;
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();             //System.out.println("UpdateDayEndQuery_8==" + sql);

            sql = "update tbldayendprocess set dblTakeAway=IFNULL((select sum(dblGrandTotal) TakeAway from tblbillhd"
                    + " where strTakeAway='Yes' and date(dteBillDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + " and intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;

            //System.out.println("UpdateDayEndQuery_9=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            sql = "update tbldayendprocess set dblFloat=IFNULL((select sum(dblAmount) TotalFloats from tblcashmanagement "
                    + "where strTransType='Float' and date(dteTransDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + " and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_10=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            sql = "update tbldayendprocess set dblTransferIn=IFNULL((select sum(dblAmount) TotalTransferIn from tblcashmanagement "
                    + "where strTransType='Transfer In' and date(dteTransDate) = '" + posDate + "'"
                    + " and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_11=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblTransferOut=IFNULL((select sum(dblAmount) TotalTransferOut from tblcashmanagement "
                    + "where strTransType='Transfer Out' and date(dteTransDate) = '" + posDate + "'"
                    + " and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_12=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblWithdrawal=IFNULL(( select sum(dblAmount) TotalWithdrawals from tblcashmanagement "
                    + "where strTransType='Withdrawal' and date(dteTransDate) = '" + posDate + "' "
                    + "and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_13=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblRefund=IFNULL(( select sum(dblAmount) TotalRefunds from tblcashmanagement "
                    + " where strTransType='Refund' and date(dteTransDate) = '" + posDate + "' and strPOSCode = '" + posCode + "'"
                    + " and intShiftCode=" + shiftNo + " group by strTransType),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_14=="+sql);
            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            sql = "update tbldayendprocess set dblPayments=IFNULL(( select sum(dblAmount) TotalPayments from tblcashmanagement "
                    + "where strTransType='Payments' and date(dteTransDate) = '" + posDate + "'"
                    + " and strPOSCode = '" + posCode + "' and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_15=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblAdvance=IFNULL((select sum(b.dblAdvDepositesettleAmt) "
                    + "from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
                    + "where date(a.dtReceiptDate)='" + posDate + "' and a.strPOSCode='" + posCode + "' "
                    + "and c.strSettelmentCode=b.strSettlementCode and a.strReceiptNo=b.strReceiptNo "
                    + "and c.strSettelmentType='Cash' and intShiftCode=" + shiftNo + "),0)"
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_16=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblTotalReceipt=" + gTotalReceipt
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_17=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblTotalPay=" + gTotalPayments
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_18=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblCashInHand=" + gTotalCashInHand
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_19=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblCash=" + gTotalCashSales
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println(sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblTotalDiscount=" + gTotalDiscounts
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_21=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set dblNoOfDiscountedBill=" + gNoOfDiscountedBills
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_22=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set intTotalPax=IFNULL((select sum(intPaxNo)"
                    + " from tblbillhd where date(dteBillDate ) ='" + posDate + "' and intShiftCode=" + shiftNo + ""
                    + " and strPOSCode='" + posCode + "'),0)"
                    + " where date(dtePOSDate)='" + posDate + "' "
                    + "and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_23=="+sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
            
            sql = "update tbldayendprocess set intNoOfTakeAway=(select count(strTakeAway)"
                    + "from tblbillhd where date(dteBillDate )='" + posDate + "' and intShiftCode=" + shiftNo + ""
                    + " and strPOSCode='" + posCode + "' and strTakeAway='Yes')"
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("update int takeawy==" + sql);

            query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            query.executeUpdate();
                         sql = "update tbldayendprocess set intNoOfHomeDelivery=(select COUNT(strBillNo)from tblhomedelivery where date(dteDate)='" + posDate + "' and strPOSCode='" + posCode + "' )"
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("update int homedelivry:==" + sql);

                         query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                         query.executeUpdate();
                         
            // Update Day End Table with Used Card Balance    
            double debitCardAmtUsed = 0;
            sql = "select sum(b.dblSettlementAmt) "
                    + " from tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c "
                    + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                    + " and date(a.dteBillDate)='" + posDate + "' and a.strPOSCode='" + posCode + "' "
                    + " and c.strSettelmentType='Debit Card' "
                    + " group by a.strPOSCode,date(a.dteBillDate),c.strSettelmentType;";
            Query qUsedDCAmt=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            List listUsedDCAmt=qUsedDCAmt.list();
            	if(listUsedDCAmt.size()>0)
            	{
            		debitCardAmtUsed = Double.parseDouble(((Object)listUsedDCAmt.get(0)).toString());	
            	}
            
            	sql = "update tbldayendprocess set dblUsedDebitCardBalance=" + debitCardAmtUsed + " "
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                    + " and intShiftCode=" + shiftNo;
            	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
               query.executeUpdate();
         
            // Update Day End Table with UnUsed Card Balance    
            double debitCardAmtUnUsed = 0;
            sql = "select sum(dblCardAmt) from tbldebitcardrevenue "
                    + " where strPOSCode='" + posCode + "' and date(dtePOSDate)='" + posDate + "' "
                    + " group by strPOSCode,date(dtePOSDate);";
         
            Query qUnUsedDCAmt=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
            List listUnUsedDCAmt=qUnUsedDCAmt.list();
            	if(listUnUsedDCAmt.size()>0)
            	{
            		debitCardAmtUnUsed = Double.parseDouble(((Object)listUnUsedDCAmt.get(0)).toString());	
            	}
         
            sql = "update tbldayendprocess set dblUnusedDebitCardBalance=" + debitCardAmtUnUsed + " "
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' "
                    + " and intShiftCode=" + shiftNo;
         	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           query.executeUpdate();
     
            sql = "UPDATE tbldayendprocess SET dblTipAmt= IFNULL(( "
                    + "SELECT SUM(dblTipAmount) "
                    + "FROM tblbillhd "
                    + "WHERE DATE(dteBillDate) ='" + posDate + "' AND intShiftCode='" + shiftNo + "' AND strPOSCode='" + posCode + "'),0) "
                    + "WHERE DATE(dtePOSDate)='" + posDate + "' AND strPOSCode='" + posCode + "' AND intShiftCode='" + shiftNo + "' ";
         	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           query.executeUpdate();
     

            //update no. of complementary bills
            sql = "update tbldayendprocess set intNoOfComplimentaryKOT=(select COUNT(distinct(a.strBillNo))"
                    + "from  tblbillhd a,tblbillcomplementrydtl b "
                    + "where a.strBillNo=b.strBillNo "
                    + "and date(b.dteBillDate)='" + posDate + "' and a.strPOSCode='" + posCode + "') "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("intNoOfComplimentaryKOT:==" + sql);
         	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           query.executeUpdate();
     

            //update no. of void KOTs
            sql = "update tbldayendprocess set intNoOfVoidKOT=(select count(distinct(a.strKOTNo)) "
                    + "from tblvoidkot a "
                    + "where a.strPOSCode='" + posCode + "' "
                    + "and date(a.dteVoidedDate)='" + posDate + "') "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("intNoOfVoidKOT:==" + sql);
         	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           query.executeUpdate();
     
            //update no. of NC KOTs
            sql = "update tbldayendprocess set intNoOfNCKOT=(select count(distinct(a.strKOTNo)) "
                    + "from tblnonchargablekot a "
                    + "where a.strPOSCode='" + posCode + "' "
                    + "and date(a.dteNCKOTDate)='" + posDate + "') "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + posCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("intNoOfNCKOT:==" + sql);
         	query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           query.executeUpdate();
     
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }

  
    
  /*  public void funGenerateLinkupTextfile(ArrayList<ArrayList<String>> arrUnLinkedItemDtl, String fromDate, String toDate, String posName,String gClientName)
    {
	
	    try
	    {
	        funCreateTempFolder();
	        String filePath = System.getProperty("user.dir");
	        filePath += "/Temp/Temp_ItemUnLinkedItems.txt";
	        File textFile = new File(filePath);
	        PrintWriter pw = new PrintWriter(textFile);
	        pw.println(funPrintTextWithAlignment(" UnLinked Items ", 40, "Center"));
	        pw.println(funPrintTextWithAlignment(gClientName, 40, "Center"));
	        pw.println(funPrintTextWithAlignment(posName, 40, "Center"));
	        pw.println(" ");
	        pw.print(funPrintTextWithAlignment("FromDate:", 10, "Left"));
	        pw.print(funPrintTextWithAlignment(fromDate, 10, "Left"));
	        pw.print(funPrintTextWithAlignment("", 2, "Left"));
	        pw.print(funPrintTextWithAlignment("ToDate:", 8, "Left"));
	        pw.print(funPrintTextWithAlignment(toDate, 10, "Left"));
	        pw.println(" ");
	        pw.println("________________________________________");
	        pw.print(funPrintTextWithAlignment("ItemCode ", 15, "Left"));
	        pw.print(funPrintTextWithAlignment("ItemName", 25, "Left"));
	        pw.println(" ");
	        pw.println("________________________________________");
	        pw.println(" ");
	
	        if (arrUnLinkedItemDtl.size() > 0)
	        {
	            for (int cnt = 0; cnt < arrUnLinkedItemDtl.size(); cnt++)
	            {
	                ArrayList<String> items = arrUnLinkedItemDtl.get(cnt);
	                pw.print(funPrintTextWithAlignment("" + items.get(0) + " ", 15, "Left"));
	                pw.print(funPrintTextWithAlignment("" + items.get(1), 25, "Left"));
	                pw.println(" ");
	            }
	        }
	
	        pw.println(" ");
	        pw.println(" ");
	        pw.println(" ");
	        pw.println(" ");
	        pw.println("m");
	
	        pw.flush();
	        pw.close();
	
	        clsTextFileGeneratorForPrinting ob = new clsTextFileGeneratorForPrinting();
	        if (clsGlobalVarClass.gShowBill)
	        {
	            ob.funShowTextFile(textFile, "", "");
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
    }
  */

    
    public boolean funInsertQBillData(String posCode,String clientCode)
	 {
	    boolean flgResult = false;
	
	    try
	    {
	        String sqlAdvRecDtl = "delete from tblqadvancereceiptdtl "
	                + " where strReceiptNo in (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvRecDtl);
	        query.executeUpdate();
	       
	        sqlAdvRecDtl = "insert into tblqadvancereceiptdtl "
	                + "(select * from tbladvancereceiptdtl "
	                + " where strReceiptNo in (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')))";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvRecDtl);
	        query.executeUpdate();
	        sqlAdvRecDtl = "delete from tbladvancereceiptdtl "
	                + " where strReceiptNo in (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvRecDtl);
	        query.executeUpdate();        //System.out.println("Adv Rec Dtl");
	
	        String sqlAdvRecHd = "delete from tblqadvancereceipthd where strReceiptNo in "
	                + " (select strReceiptNo from tbladvancereceipthd "
	                + " where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        //System.out.println(sqlAdvRecHd);
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvRecHd);
	        query.executeUpdate();
	        
	        sqlAdvRecHd = "insert into tblqadvancereceipthd "
	                + "(select * from tbladvancereceipthd "
	                + " where strAdvBookingNo in (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        //System.out.println(sqlAdvRecHd);
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvRecHd);
	        query.executeUpdate();
	        sqlAdvRecHd = "delete from tbladvancereceipthd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        //System.out.println(sqlAdvRecHd);
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvRecHd);
	        query.executeUpdate();
	        //System.out.println("Adv Rec Hd");
	
	        String sqlAdvBookDtl = "delete from tblqadvbookbilldtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookDtl);
	        query.executeUpdate();
	    
	        sqlAdvBookDtl = "insert into tblqadvbookbilldtl "
	                + " (select * from tbladvbookbilldtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookDtl);
	        query.executeUpdate();
	        
	        sqlAdvBookDtl = "delete from tbladvbookbilldtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookDtl);
	        query.executeUpdate();
	        //System.out.println("Adv Dtl");
	
	        String sqlAdvBookCharDtl = "delete from tbladvbookbillchardtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookCharDtl);
	        query.executeUpdate();
	        sqlAdvBookCharDtl = "insert into tblqadvbookbillchardtl "
	                + " (select * from tbladvbookbillchardtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookCharDtl);
	        query.executeUpdate();
	        
	        sqlAdvBookCharDtl = "delete from tbladvbookbillchardtl where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookCharDtl);
	        query.executeUpdate();
	        //System.out.println("Adv Char Dtl");
	
	        String sqlAdvBookModDtl = "delete from tblqadvordermodifierdtl where strAdvOrderNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookModDtl);
	        query.executeUpdate();
	
	        sqlAdvBookModDtl = "insert into tblqadvordermodifierdtl "
	                + " (select * from tbladvordermodifierdtl where strAdvOrderNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookModDtl);
	        query.executeUpdate();
	
	        
	        sqlAdvBookModDtl = "delete from tbladvordermodifierdtl where strAdvOrderNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookModDtl);
	        query.executeUpdate();
	
	        //System.out.println("Adv Mod Dtl");
	
	        String sqlAdvBookHd = "delete from tblqadvbookbillhd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookHd);
	        query.executeUpdate();
	
	     
	        sqlAdvBookHd = "insert into tblqadvbookbillhd "
	                + " (select * from tbladvbookbillhd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "'))";
	     
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookHd);
	        query.executeUpdate();
	
	        sqlAdvBookHd = "delete from tbladvbookbillhd where strAdvBookingNo in "
	                + " (select strAdvBookingNo from tblbillhd "
	                + " where strPOSCode='" + posCode + "' "
	                + " and strClientCode='" + clientCode + "')";
	        //System.out.println("Adv Hd");
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlAdvBookHd);
	        query.executeUpdate();
	
	        String qSqlBillDtl = "delete from tblqbilldtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillDtl);
	        query.executeUpdate();
	
	        qSqlBillDtl = "insert into tblqbilldtl (select * from tblbilldtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillDtl);
	        query.executeUpdate();
	
	        qSqlBillDtl = "delete from tblbilldtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillDtl);
	        query.executeUpdate();
	
	        //System.out.println("Bill Dtl");
	
	        String qSqlBillSettDtl = "delete from tblqbillsettlementdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillSettDtl);
	        query.executeUpdate();
	
	        qSqlBillSettDtl = "insert into tblqbillsettlementdtl (select * from tblbillsettlementdtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillSettDtl);
	        query.executeUpdate();
	        
	        qSqlBillSettDtl = "delete from tblbillsettlementdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillSettDtl);
	        query.executeUpdate();
	        //System.out.println("Bill Sett Dtl");
	
	        String qSqlBillModDtl = "delete from tblqbillmodifierdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillModDtl);
	        query.executeUpdate();
	        
	        qSqlBillModDtl = "insert into tblqbillmodifierdtl (select * from tblbillmodifierdtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillModDtl);
	        query.executeUpdate();
	
	        qSqlBillModDtl = "delete from tblbillmodifierdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillModDtl);
	        query.executeUpdate();
	
	        //System.out.println("Bill Mod Dtl");
	
	        String qSqlBillTaxDtl = "delete from tblqbilltaxdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillTaxDtl);
	        query.executeUpdate();
	
	        qSqlBillTaxDtl = "insert into tblqbilltaxdtl (select * from tblbilltaxdtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillTaxDtl);
	        query.executeUpdate();
	        
	        qSqlBillTaxDtl = "delete from tblbilltaxdtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillTaxDtl);
	        query.executeUpdate();        //System.out.println("Bill Tax Dtl");
	
	        //discount dtl tables
	        String qSqlBillDiscDtl = "delete from tblqbilldiscdtl where strPOSCode='" + posCode + "'"
	                + "and strBillNo in (select strBillNo from tblbilldiscdtl where strPOSCode = '" + posCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillDiscDtl);
	        query.executeUpdate();
	        
	        qSqlBillDiscDtl = "insert into tblqbilldiscdtl (select * from tblbilldiscdtl "
	                + "where strPOSCode='" + posCode + "') ";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillDiscDtl);
	        query.executeUpdate();
	        
	        qSqlBillDiscDtl = "delete from tblbilldiscdtl where strPOSCode='" + posCode + "'";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillDiscDtl);
	        query.executeUpdate();
	        
	        String qSqlBillPromoDtl = "delete from tblqbillpromotiondtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillPromoDtl);
	        query.executeUpdate();
	        qSqlBillPromoDtl = "insert into tblqbillpromotiondtl (select * from tblbillpromotiondtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillPromoDtl);
	        query.executeUpdate();
	        qSqlBillPromoDtl = "delete from tblbillpromotiondtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillPromoDtl);
	        query.executeUpdate();
	        //System.out.println("Bill Promo Dtl");
	
	        String qSqlBillComplementoryDtl = "delete from tblqbillcomplementrydtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillComplementoryDtl);
	        query.executeUpdate();
	        
	        qSqlBillComplementoryDtl = "insert into tblqbillcomplementrydtl (select * from tblbillcomplementrydtl "
	                + "where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "'))";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillComplementoryDtl);
	        query.executeUpdate();
	        
	        qSqlBillComplementoryDtl = "delete from tblbillcomplementrydtl where strClientCode='" + clientCode + "' "
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillComplementoryDtl);
	        query.executeUpdate();
	        //System.out.println("Bill Complementory Dtl");
	
	        String qSqlBillHd = "delete from tblqbillhd where strPOSCode='" + posCode + "'"
	                + "and strBillNo in (select strBillNo from tblbillhd where strPOSCode = '" + posCode + "')";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillHd);
	        query.executeUpdate();
	        
	        qSqlBillHd = "insert into tblqbillhd (select * from tblbillhd "
	                + "where strClientCode='" + clientCode + "' and strPOSCode='" + posCode + "') ";
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillHd);
	        query.executeUpdate();
	        
	        qSqlBillHd = "delete from tblbillhd where strPOSCode='" + posCode + "'";
	        
	        query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(qSqlBillHd);
	        query.executeUpdate();
	        //System.out.println("Bill HD");
	        flgResult = true;
	
	    }
	    catch (Exception e)
	    {
	        flgResult = false;
	       // JOptionPane.showMessageDialog(null, "Qfile Data Posting failed!!!");
	        e.printStackTrace();
	    }
	    finally
	    {
	        return flgResult;
	    }
	 }

	public void funInvokeHOWebserviceForTrans(String transType, String formName,String clientCode,String POSCode)
	{

		try{
		 	JSONObject jsHOPOSType = objSetupDao.funGetParameterValuePOSWise(clientCode,POSCode, "gHOPOSType");
			String gHOPOSType=jsHOPOSType.get("gHOPOSType").toString();
				
		    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
		    {
		        return;
		    }
	    	String gHOCommunication="";
	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
	    	Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    	List list=query.list();
	    	if(list.size()>0){
	    		gHOCommunication=(String) list.get(0);	 
	    	}
	 
    	// send client code and pos code for getting global veriable data 
		    if(gHOCommunication.equals("Y"))//true
		    {
		        if (transType.equalsIgnoreCase("Sales"))
		        {
		            //objSynchData.funPostSaleDataToHO(formName);
		        	objSynchronizePOSDataToHO.funPostSalesDataToHOInBulk(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("PlaceOrder"))
		        {
		            objSynchronizePOSDataToHO.funPostPlaceOrderDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("Audit"))
		        {
		        	objSynchronizePOSDataToHO.funPostAuditDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("Inventory"))
		        {
		        	objSynchronizePOSDataToHO.funPostInventoryDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("AdvanceOrder"))
		        {
		        	objSynchronizePOSDataToHO.funPostAdvOrderDataToHO(formName,clientCode,POSCode);
		        }
		        else if (transType.equalsIgnoreCase("All"))
		        {
		        	objSynchronizePOSDataToHO.funPostAuditDataToHO(formName,clientCode,POSCode);
		        	objSynchronizePOSDataToHO.funPostSaleDataToHO(formName,clientCode,POSCode);
		        	objSynchronizePOSDataToHO.funPostInventoryDataToHO(formName,clientCode,POSCode);
		        	
		        	funPostCustomerDataToHOPOS(clientCode,POSCode);
		        	funPostCustomerAreaDataToHOPOS(clientCode,POSCode);
		        }
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
}
	
	public void funPostCustomerDataToHOPOS(String clientCode,String POSCode)
	{
		 try
		 {
			JSONObject jsHOPOSType = objSetupDao.funGetParameterValuePOSWise(clientCode,POSCode, "gHOPOSType");
			String gHOPOSType=jsHOPOSType.get("gHOPOSType").toString();
		    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
		    {
		        return;
		    }
		 	String gHOCommunication="";
	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
	    	Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    	List list=query.list();
	    	if(list.size()>0){
	    		gHOCommunication=(String) list.get(0);	 
	    	}
	    	
	        if(gHOCommunication.equals("Y"))
	        {
	        
	            if (objSynchronizePOSDataToHO.funPostCustomerMasterDataToHO())
	            {
	            	WebPOSSessionFactory.getCurrentSession().createSQLQuery("update tblcustomermaster set "
	            			+ "strDataPostFlag='Y'").executeUpdate();
	            }
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	    finally
	    {
	    }
	}

	public void funPostCustomerAreaDataToHOPOS(String clientCode,String POSCode)
   {
		try
	    {
	          
			JSONObject jsHOPOSType = objSetupDao.funGetParameterValuePOSWise(clientCode,POSCode, "gHOPOSType");
			String gHOPOSType=jsHOPOSType.get("gHOPOSType").toString();
		    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
		    {
		        return;
		    }
	
		    String gHOCommunication="";
	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
	    	Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    	List list=query.list();
	    	if(list.size()>0){
	    		gHOCommunication=(String) list.get(0);	 
	    	}
	    	
	        if(gHOCommunication.equals("Y"))
	        {
	                boolean flgCustAreaMaster = objSynchronizePOSDataToHO.funPostCustomerAreaMaster();
	                if (flgCustAreaMaster)
	                {
	                	WebPOSSessionFactory.getCurrentSession().createSQLQuery("update tblbuildingmaster set strDataPostFlag='Y'").executeUpdate();
	                    System.out.println("cust area master flg=" + flgCustAreaMaster);
	                }
	                boolean flgDelCharges = objSynchronizePOSDataToHO.funPostDelChargesMaster();
	                if (flgDelCharges)
	                {
	                	WebPOSSessionFactory.getCurrentSession().createSQLQuery("update tblareawisedc set strDataPostFlag='Y'").executeUpdate();
	                    System.out.println("cust area Del charges flg=" + flgDelCharges);
	                }
	            }
	      }
       catch (Exception e)
       {
           e.printStackTrace();
       }
   }
	
	public void funPostDayEndData(String newStartDate,int shiftCode,String strClientCode,String posCode)
	  {
		 try
		 {  
			 JSONObject jsHOPOSType = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gHOPOSType");
				String gHOPOSType=jsHOPOSType.get("gHOPOSType").toString();
			    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
			    {
			        return;
			    }
			 	
			    String gHOCommunication="";
		    	String sql="select strHOCommunication from tblconfig where strClientCode='"+strClientCode+"'";
		    	Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		    	List list=query.list();
		    	if(list.size()>0){
		    		gHOCommunication=(String) list.get(0);	 
		    	}
		    	
		        if(gHOCommunication.equals("Y"))
		        {
	                if (objSynchronizePOSDataToHO.funPostDayEndData(newStartDate,shiftCode,strClientCode,posCode))
		             {
	                	WebPOSSessionFactory.getCurrentSession().createSQLQuery("update tbldayendprocess set strDataPostFlag='Y' where strDayEnd='Y'").executeUpdate();
	                 }
	                	objSynchronizePOSDataToHO.funPostCashManagementData(strClientCode,posCode);
		            }
		        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	
	
    
	public void funCreateTempFolder()
		{
		    try
		    {
		        String filePath = System.getProperty("user.dir");
		        File file = new File(filePath + "/Temp");
		        if (!file.exists())
		        {
		            file.mkdirs();
		        }
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		}
	public String getCurrentDateTime()
    {
        Date currentDate = new Date();
        String strCurrentDate = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
            + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
        return strCurrentDate;
    }

	public String funGetConnectionStatus(String clientCode,String posCode)
	{
		String flgHOStatus = "N";
	    String gConnectionActive = "N";
		try
	    {
	   
	    JSONObject jsHOPOSType = objSetupDao.funGetParameterValuePOSWise(clientCode,posCode, "gHOPOSType");
		String gHOPOSType=jsHOPOSType.get("gHOPOSType").toString();
	
	    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
	    {
	        return "N";
	    }
	    
	    	String gHOCommunication="";
	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
	    	Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    	List list=query.list();
	    	if(list.size()>0){
	    		gHOCommunication=(String) list.get(0);	 
	    	}
	    	
	        if(gHOCommunication.equals("Y"))
	        {
	        	  JSONObject jsonSanguineWebServiceURL = objSetupDao.funGetParameterValuePOSWise(clientCode,posCode, "gSanguineWebServiceURL");
	     		 String gSanguineWebServiceURL=jsonSanguineWebServiceURL.get("gSanguineWebServiceURL").toString();
	     		
	            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funInvokeHOWebService";
	            URL url = new URL(hoURL);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.setRequestProperty("Accept", "application/json");
	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	            String output = "", op = "";
	            while ((output = br.readLine()) != null)
	            {
	                op += output;
	            }
	            System.out.println("HO Conn=" + op);
	            conn.disconnect();
	
	            flgHOStatus = op;
	            if (flgHOStatus.equalsIgnoreCase("true"))
	            {
	                gConnectionActive = "Y";
	            }
	        }
	    }
	    catch (Exception e)
	    {
	        flgHOStatus = "N";
	        gConnectionActive = "N";
	        e.printStackTrace();
	    }
	    finally
	    {
	        return gConnectionActive;
	    }
	}
	


}
