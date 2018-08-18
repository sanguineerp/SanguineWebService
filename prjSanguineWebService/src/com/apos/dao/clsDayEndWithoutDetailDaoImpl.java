package com.apos.dao;

import java.io.File;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.controller.clsUtilityController;
import com.apos.service.clsPOSConfigSettingService;
import com.apos.util.clsBackupDatabase;
import com.apos.util.clsSendMail;

@Repository("clsDayEndWithoutDetailDao") 
@Transactional(value ="webPOSTransactionManager")
public class clsDayEndWithoutDetailDaoImpl implements clsDayEndWithoutDetailDao{

	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired 
	clsSendMail obSendMail;
	@Autowired 
	clsBackupDatabase obBackupDatabase;
	@Autowired 
	clsUtilityController objUtility;
	@Autowired 
	clsPOSConfigSettingService objPOSConfigSettingService;
	@Autowired
	clsSetupDao objSetupDao;
	String sql="",strPOSDate;
	int shiftNo;
	 private double totalSales, totalDiscount, totalPayments;
	public JSONObject funStartDayProcessWithoutDetails(String strPOSCode,String strShiftNo)
	{
		JSONObject jObj=new JSONObject();
		try{
			int shiftNo=Integer.parseInt(strShiftNo);
			
			sql = "update tbldayendprocess set strShiftEnd='N' "
	                + "where strPOSCode='" + strPOSCode + "' and strDayEnd='N' and strShiftEnd=''";
			webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
			
			if (shiftNo == 0)
	        {
	            shiftNo++;
	        }
	        sql = "update tbldayendprocess set intShiftCode= " + shiftNo + " "
	                + "where strPOSCode='" + strPOSCode + "' and strShiftEnd='N' and strDayEnd='N'";
	        webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
	        jObj.put("shiftEnd", "N");
			jObj.put("DayEnd", "N");
			jObj.put("shiftNo",shiftNo);
	        
			}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jObj;
	}
	
	public JSONObject funDayEndProcessWithoutDetails(String strPOSCode, String strShiftNo,String strUserCode,String POSDate,String strClientCode)
	{
		clsDayEndProcessDao.gTransactionType = "ShiftEnd"; // For DayEndWithoutDetails to create day end textfile
		JSONObject objJSON=new JSONObject();
		try
        {
			JSONObject JSONEnableShiftYN = objSetupDao.funGetParameterValuePOSWise(strUserCode,strPOSCode, "gEnableShiftYN");
			String gEnableShiftYN=JSONEnableShiftYN.get("gEnableShiftYN").toString();
			
            if (gEnableShiftYN.equals("Y"))//for shift wise
            {
                String sqlShift = "select date(max(dtePOSDate)),intShiftCode "
                        + " from tbldayendprocess "
                        + " where strPOSCode='" + strPOSCode + "' and strDayEnd='N' "
                        + " and (strShiftEnd='' or strShiftEnd='N') ";
                Query queryShift=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlShift);
                List listShift=queryShift.list();
                if(listShift.size()>0)
                {
                	for(int i=0;i<listShift.size();i++)
                	{
                		Object[] ob=(Object[])listShift.get(i);
                		shiftNo = Integer.parseInt(ob[1].toString());		
                	}
                }
                
                sql = "delete from tblitemrtemp where strTableNo='null' ";
                webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
                
                clsDayEndProcessDao.gDayEndReportForm = "DayEndReport";
                //here check busy table and pending bills.. those already checked on UI
                objJSON.put("gDayEndReportForm", "DayEndReport");

                String gDayEnd="N";
				if (gEnableShiftYN.equals("N") && gDayEnd.equals("N")) //== if (btnShiftEnd.isEnabled())
			    {
                        String backupFilePath="";
                        if ("Windows".equalsIgnoreCase("Windows"))//clsPosConfigFile.gPrintOS.equalsIgnoreCase("Windows"))
                        {
                            backupFilePath = obBackupDatabase.funTakeBackUpDB(strClientCode);
                        }
                     
                        sql = "update tbltablemaster set strStatus='Normal' "
                                + " where strPOSCode='" + strPOSCode + "'";
                        webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
                        

                        /*if (clsGlobalVarClass.gGenrateMI)
                        {
                            frmGenrateMallInterfaceText objGenrateMallInterfaceText = new frmGenrateMallInterfaceText();
                            objGenrateMallInterfaceText.funWriteToFile(posDate, posDate, "Current", "Y");
                        }*/
                        objUtility.funGetNextShiftNoForShiftEnd(strPOSCode, shiftNo,strClientCode,strUserCode);
                        
                        String filePath = System.getProperty("user.dir");
                        filePath = filePath + "/Temp/Temp_DayEndReport.txt";
//                        File file = new File(filePath);
//                        if (!file.exists())
//                        {
//                            file.mkdir();
//                        }
                        String sqlPOSData="select a.strPOSName ,max(b.dtePOSDate) from tblposmaster a, tbldayendprocess b "
                        		+ "where b.strPOSCode='"+strPOSCode+"' and a.strPosCode='"+strPOSCode+"';";
                        Query qposdata=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlPOSData);
                        List list=qposdata.list();
                        String strPOSName="All";// by default
                        if(list.size()>0)
                        {
                        	Object ob[]=(Object[])list.get(0);
                        	strPOSDate= ob[1].toString().split("// ")[0];
                        	strPOSName= ob[0].toString();
                        }
                        obSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
	                    
                        if ("Windows".equalsIgnoreCase("Windows"))
                        {
                            //objUtility.funBackupAndMailDB(backupFilePath);
                            objUtility.funBackupAndMailDB(backupFilePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
                        }  
                }
				
				objJSON.put("result", "success");
            }
            else
            {
                String sqlShift = "select date(max(dtePOSDate)),intShiftCode"
                        + " from tbldayendprocess "
                        + " where strPOSCode='" + strPOSCode + "' and strDayEnd='N'"
                        + " and (strShiftEnd='' or strShiftEnd='N')";
                
                Query queryShift=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlShift);
                List listShift=queryShift.list();
                if(listShift.size()>0)
                {
                	for(int i=0;i<listShift.size();i++)
                	{
                		Object[] ob=(Object[])listShift.get(i);
                		shiftNo = Integer.parseInt(ob[1].toString());		
                	}
                }
                else
                {
                    shiftNo++;
                }
              
                sql = "delete from tblitemrtemp where strTableNo='null' ";
                webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
                
                clsDayEndProcessDao.gDayEndReportForm = "DayEndReport";
                String gDayEnd="N";
                if (gEnableShiftYN.equals("N") && gDayEnd.equals("N")) //== if (btnShiftEnd.isEnabled())
			    {
                            String backupFilePath="";
                            if ("Windows".equalsIgnoreCase("Windows"))
                            {
                            	backupFilePath = obBackupDatabase.funTakeBackUpDB(strClientCode);
                            }

                            sql = "update tbltablemaster set strStatus='Normal' "
                                    + " where strPOSCode='" + strPOSCode + "'";
                            webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();

                            sql = "update tbldayendprocess set strShiftEnd='Y' "
                                    + " where strPOSCode='" + strPOSCode + "' and strDayEnd='N'";
                            webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
                            /*if (clsGlobalVarClass.gGenrateMI)
                            {
                                frmGenrateMallInterfaceText objGenrateMallInterfaceText = new frmGenrateMallInterfaceText();
                                objGenrateMallInterfaceText.funWriteToFile(posDate, posDate, "Current", "Y");
                            }*/
                            objUtility.funGetNextShiftNo(strPOSCode, shiftNo, strClientCode, strUserCode);
                            //btnShiftEnd.setEnabled(false);

                            String filePath = System.getProperty("user.dir");
                            filePath = filePath + "/Temp/Temp_DayEndReport.txt";
//                            File file = new File(filePath);
//                            if (!file.exists())
//                            {
//                                file.mkdir();
//                            }
                            String sqlPOSData="select a.strPOSName ,max(b.dtePOSDate) from tblposmaster a, tbldayendprocess b "
	                        		+ "where b.strPOSCode='"+strPOSCode+"' and a.strPosCode='"+strPOSCode+"';";
	                        Query qposdata=webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlPOSData);
	                        List list=qposdata.list();
	                        String strPOSName="All";// by default
	                        if(list.size()>0)
	                        {
	                        	Object ob[]=(Object[])list.get(0);
	                        	strPOSDate= ob[1].toString().split("// ")[0];
	                        	strPOSName= ob[0].toString();
	                        }
	                        obSendMail.funSendMail(totalSales, totalDiscount, totalPayments, filePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
	                        if ("Windows".equalsIgnoreCase("Windows"))
	                        {
	                            //objUtility.funBackupAndMailDB(backupFilePath);
	                            objUtility.funBackupAndMailDB(backupFilePath,strClientCode,strPOSCode,strPOSName,strPOSDate);
	                        }
                          
                        }
                objJSON.put("result", "success");
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		return objJSON;
	}
}
