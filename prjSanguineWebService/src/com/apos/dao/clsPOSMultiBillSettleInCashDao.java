
package com.apos.dao;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.apos.model.clsMenuHeadMasterModel;
import com.apos.model.clsPosSettlementDetailsModel;
import com.apos.service.clsSetupService;
import com.webservice.util.clsUtilityFunctions;

@Repository("clsPOSMultiBillSettleInCashDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSMultiBillSettleInCashDao
{

	@Autowired
	private SessionFactory	webPOSSessionFactory;
	
	@Autowired
	private clsSetupService objSetupService;
	
	private boolean billPrintOnSettlement = false;
	
	private clsUtilityFunctions objUtility;
	@SuppressWarnings("finally")
	public JSONObject funGetSettleBillDtlData(String clientCode,String posCode,String posDate)
	{
		List list =null;
		JSONObject jObjData=new JSONObject();
		JSONObject jObjMenuHeadData=new JSONObject();
		try{
			String sql = "";
//			String sql_getPOSDate = "select a.dtePOSDate from tbldayendprocess a "
//            		+ " where a.strPosCode='"+posCode+"' and a.strDayEnd='N' ";
//			Query posDateQuery = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_getPOSDate);
			
			 JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType"); 
			if (objSetupParameter.get("gShowBillsType").toString().equalsIgnoreCase("Table Detail Wise"))
            {
			sql = "select a.strBillNo,ifnull(b.strTableNo,''),ifnull(b.strTableName,''),ifnull(c.strWaiterNo,'') "
                    + " ,ifnull(c.strWShortName,''),ifnull(d.strCustomerCode,''),ifnull(d.strCustomerName,''),a.dblGrandTotal "
                    + " ,DATE_FORMAT(a.dteBillDate,'%h:%i:%s')  "
                    + " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                    + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo "
                    + " left outer join tblcustomermaster d on a.strCustomerCode=d.strCustomerCode "
                    + " where a.strBillNo not in (select strBillNo from tblbillsettlementdtl) "
                    + " and date(a.dteBillDate)='" + posDate+ "' "
                    + " and a.strPOSCode='" +posCode+ "' ";
            }
			else
			{
				 sql = "SELECT a.strBillNo,IFNULL(d.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(f.strDPName,'')"
	                        + " ,a.dblGrandTotal,ifnull(g.strTableNo,''),ifnull(g.strTableName,''),DATE_FORMAT(a.dteBillDate,'%h:%i:%s') "
	                        + " FROM tblbillhd a "
	                        + " left outer join tblhomedeldtl b on a.strBillNo=b.strBillNo "
	                        + " LEFT OUTER JOIN tblcustomermaster d ON a.strCustomerCode=d.strCustomerCode "
	                        + " left outer join tblbuildingmaster e on d.strBuldingCode=e.strBuildingCode "
	                        + " left outer join tbldeliverypersonmaster  f on  f.strDPCode=b.strDPCode "
	                        + " left outer join tbltablemaster g on a.strTableNo=g.strTableNo "
	                        + " WHERE a.strBillNo NOT IN (SELECT strBillNo FROM tblbillsettlementdtl) "
	                        + " AND DATE(a.dteBillDate)='" + posDate+ "' "
	                        + " AND a.strPOSCode='" + posCode + "' "
	                        + " group by a.strBillNo";
			}
			
			
			Query rsPendingBills = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			list = rsPendingBills.list();
			//JSONObject jArrData1=new JSONObject();
			
			Object[] obj=null ;
			 JSONArray jArrData=new JSONArray();
			 if (objSetupParameter.get("gShowBillsType").toString().equalsIgnoreCase("Table Detail Wise"))
             {
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						obj = (Object[]) list.get(i);
						JSONObject objDeliverBoy=new JSONObject();
						
						
						objDeliverBoy.put("strBillNo",Array.get(obj, 0));
						objDeliverBoy.put("strTableName",Array.get(obj, 2));
						objDeliverBoy.put("strWShortName",Array.get(obj, 4));
						objDeliverBoy.put("strTableNo",Array.get(obj, 1));
						objDeliverBoy.put("strCustomerName",Array.get(obj, 6));
						objDeliverBoy.put("dteBillDate",Array.get(obj, 8));
						objDeliverBoy.put("dblGrandTotal",Array.get(obj, 7));
						jArrData.put(objDeliverBoy);
					}
					jObjData.put("UnsettleBillDtl", jArrData);
					jObjData.put("DataType", "TableDetailWise");
				}
			 
             }
			 else
			 {
				 if (list!=null)
					{
						for(int i=0; i<list.size(); i++)
						{
							obj = (Object[]) list.get(i);
							JSONObject objDeliverBoy=new JSONObject();	
					objDeliverBoy.put("strBillNo",Array.get(obj, 0));
					objDeliverBoy.put("strTableName",Array.get(obj, 6));
					objDeliverBoy.put("strTableNo",Array.get(obj, 5));
					objDeliverBoy.put("strCustomerName",Array.get(obj, 1));
					objDeliverBoy.put("strBuildingName",Array.get(obj, 2));
					objDeliverBoy.put("strDPName",Array.get(obj, 3));
					objDeliverBoy.put("dteBillDate",Array.get(obj, 7));
					objDeliverBoy.put("dblGrandTotal",Array.get(obj, 4));
					
					jArrData.put(objDeliverBoy);
			 }
						jObjData.put("UnsettleBillDtl", jArrData);
						jObjData.put("DataType", "DeliveryBoyDetailWise");
			}
			 }
			 
			rsPendingBills = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
		}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjData;
			}
	}
	
	
	@SuppressWarnings("finally")
	public void funSettleBills(JSONObject jObjSettleBills)
	{
		
		try
		{JSONObject objCashSettlementDtl;
		String billNo="";
		String dblSettleAmt="";
		String tableName="";
		String tableNo="";
			//String billNo = jObjSettleBills.getString("BillNo");
			String posDate = jObjSettleBills.getString("POSDate");
			String clientCode = jObjSettleBills.getString("ClientCode");
			//String dblSettleAmt = jObjSettleBills.getString("dblSettleAmt");
			//String tableName = jObjSettleBills.getString("TableName");
			//String tableNo = jObjSettleBills.getString("TableNo");
			String userCode = jObjSettleBills.getString("User");
			String posCode = jObjSettleBills.getString("POSCode");
			
			JSONArray unsettleBillDtl=(JSONArray)jObjSettleBills.get("UnsettleBillDetails");
	        for (int i = 0; i < unsettleBillDtl.length(); i++) 
	        { 
	        	JSONObject childJSONObject = unsettleBillDtl.getJSONObject(i);
	        	billNo= childJSONObject.getString("BillNo");
	        	dblSettleAmt=childJSONObject.getString("dblSettleAmt");
	        	tableName=childJSONObject.getString("TableName");
	        	tableNo=childJSONObject.getString("TableNo");
	        	
	        }
	        
			
		String rsCashSttlementDtl="select a.strSettelmentCode,a.strSettelmentDesc,a.strBillPrintOnSettlement "
                + "from tblsettelmenthd a "
                + "where a.strSettelmentType='Cash' "
                + "limit 1;";
		Query rsCashSttlementDtl1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(rsCashSttlementDtl);
		List list = rsCashSttlementDtl1.list();
		String cashSettlementCode=null;
		String strExpiryDate="";
		String strCardName="";
		String strRemark="Multi Bill Settle";
		String strCustomerCode="";
		String dblRefundAmt="0.00";
		String strGiftVoucherCode="";
		String strDataPostFlag="N";
		if (list!=null)
		{
			for(int i=0; i<list.size(); i++)
			{
				Object[] obj = (Object[]) list.get(i);
				cashSettlementCode = obj[0].toString();
				String strBillPrintOnSettlement = obj[2].toString();
				
				 objCashSettlementDtl=new JSONObject();	
				
				objCashSettlementDtl.put("cashSettlementCode",Array.get(obj, 0));
				objCashSettlementDtl.put("cashSettlementName",Array.get(obj, 1));
				if (strBillPrintOnSettlement.toString().equalsIgnoreCase("Y"))
                {
                    billPrintOnSettlement = true;
                }
                else
                {
                    billPrintOnSettlement = false;
                }
        }
		}
		String sqlDelete = "delete from tblbillsettlementdtl where strBillNo='" + billNo + "'";
		Query q1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete);
       
        String sqlInsertBillSettlementDtl = "insert into tblbillsettlementdtl"
                + "(strBillNo,strSettlementCode,dblSettlementAmt,dblPaidAmt,strExpiryDate"
                + ",strCardName,strRemark,strClientCode,strCustomerCode,dblActualAmt"
                + ",dblRefundAmt,strGiftVoucherCode,strDataPostFlag,dteBillDate) "
                + "values ";
        if(list!=null)
        {
        	for (int i = 0; i < list.size(); i++) 
        	{
        		sqlInsertBillSettlementDtl += "('" + billNo + "'"
                        + ",'" + cashSettlementCode + "'," + dblSettleAmt + ""
                        + "," + dblSettleAmt + ",'" + strExpiryDate + "'"
                        + ",'" + strCardName + "','" + strRemark + "'"
                        + ",'" + clientCode + "','" + strCustomerCode + "'"
                        + "," + dblSettleAmt + "," + dblRefundAmt + ""
                        + ",'" + strGiftVoucherCode + "','" + strDataPostFlag + "','"+posDate+"'),";
       	}
		}
        StringBuilder sb1 = new StringBuilder(sqlInsertBillSettlementDtl);
        int index1 = sb1.lastIndexOf(",");
        sqlInsertBillSettlementDtl = sb1.delete(index1, sb1.length()).toString();
        Query q2 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertBillSettlementDtl);
        q2.executeUpdate();
        
        String sql = "update tblbillhd set strSettelmentMode='CASH' "
                + ",strUserEdited='" + userCode + "', dteSettleDate='" + posDate + "' "
                + ",strRemarks='Multi Bill Settle' "
                + "where strBillNo='" + billNo + "'";
        Query qUpdate = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
        qUpdate.executeUpdate();
        
        if (!tableNo.isEmpty() && !tableName.isEmpty())
        {
        	String tableStatus = "Normal";
        	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        	   String sqlDate = "select a.strCustomerCode,CONCAT(a.tmeResTime,' ',a.strAMPM) as reservationtime from tblreservation a "
	                    + " where a.strTableNo='" + tableNo + "' "
	                    + " and date(a.dteResDate)='" + posDate + "' "
	                    + " order by a.strResCode desc "
	                    + " limit 1 ";
	           Query rsReserve = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDate) ;
	            		List listReserve = rsReserve.list();
	            		if(listReserve!=null)
	                    {
	                    	for (int i = 0; i < listReserve.size(); i++) 
	                    	{
	                    		Object[] obj = (Object[]) list.get(i);
	                    		 objCashSettlementDtl=new JSONObject();	
	                    		 String res=(String) listReserve.get(1);
	                    		 Date reservationDateTime = simpleDateFormat.parse(res);
	                             Date posDateTime = new Date();
	                             String strPOSTime = String.format("%tr", posDateTime);
	                             posDateTime = simpleDateFormat.parse(strPOSTime);

	                             if (posDateTime.getTime() > reservationDateTime.getTime())
	                             {
	                                 tableStatus = "Normal";
	                             }
	                             else
	                             {
	                                 tableStatus = "Reserve";
	                             }
		                    }
		                }
	            		 Query rsCount =null;
	            		String sql_updateTableStatus = "";
	            		  JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gInrestoPOSIntegrationYN"); 
	  	        	    if(objSetupParameter.get("gInrestoPOSIntegrationYN").equals("Y"))
	  	        	    {
	                         if (tableStatus.equalsIgnoreCase("Reserve"))
	                         {
	                        	 tableStatus = "Normal";
	                         }
	                     }
	  	        	  if ("Normal".equalsIgnoreCase(tableStatus))
	  	            {
	  	                sql_updateTableStatus = "select count(*) from tblitemrtemp where strTableNo='" + tableNo + "';";
	  	            rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	        	List listCount = rsCount.list();
	  	                BigInteger cnt = (BigInteger) listCount.get(0);
	  	                int count = cnt.intValue();
	  	                if (count == 0)
	  	                {
	  	                    // no table present in tblitemrtemp so update it to normal
	  	                    sql_updateTableStatus = "update tbltablemaster set strStatus='" + tableStatus + "',intPaxNo=0 where strTableNo='" + tableNo + "'";
	  	                   
	  	                  rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	                }
	  	                else
	  	                {
	  	                	tableStatus = "Occupied";
	  	                    sql_updateTableStatus = "update tbltablemaster set strStatus='" + tableStatus + "' where strTableNo='" + tableNo + "'";
	  	                  rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	                }
	  	            }
	  	        	else
	  	            {
	  	                sql_updateTableStatus = "update tbltablemaster set strStatus='" + tableStatus + "',intPaxNo=0 where strTableNo='" + tableNo + "'";
	  	              rsCount = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_updateTableStatus) ;
	  	            }
	  	        	if(objSetupParameter.get("gInrestoPOSIntegrationYN").toString().equalsIgnoreCase("Y"))
	  	            {
	  	                objUtility.funUpdateTableStatusToInrestoApp(tableNo, tableName, tableStatus,clientCode,posCode);
	  	            }

	        }
        JSONObject objSetupParameter=objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gBillSettleSMSYN"); 
  	    
        if (objSetupParameter.get("gBillSettleSMSYN").toString().equalsIgnoreCase("Y"))
        {
        	String billSettleSMS=(String) objSetupParameter.get("gBillSettleSMSYN");
            objUtility.funSendSMS(billNo, billSettleSMS, "",clientCode,posCode);
        }

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
//			return jObjAssignHomeDeliveryMaster;
		}	
	}
	
}
