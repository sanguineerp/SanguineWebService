
package com.apos.dao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsGroupMasterModel;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsTaxMasterModel;

@Repository("clsAssignHomeDeliveryDao")
@Transactional(value = "webPOSTransactionManager")


public class clsAssignHomeDeliveryDao
{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@SuppressWarnings("finally")
	public JSONObject funGetOpenBillAndDeliveryBoyDtl(String zoneCode,String areaCode,String clientCode)
	{
		
		List list =null;
		String delBoyDtl=null;
		String billDtl=null;
		String delBoyCode=null;
		String billNo=null;
		String delBoyName=null;
		int cntIndex = 0;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String    sql = "select  strDPCode, strDPName from tbldeliverypersonmaster order by strDPCode";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj = (Object[]) list.get(i);
						
						
						JSONObject objDeliverBoy=new JSONObject();
						objDeliverBoy.put("strDPName",Array.get(obj, 1));
						objDeliverBoy.put("strDPCode",Array.get(obj, 0));
						jArrData.put(objDeliverBoy);
						
						delBoyCode=obj[0].toString();
						 delBoyName=obj[1].toString(); 
						 
						 
					}
				}
			 
			 sql = "select a.strBillNo,b.strCustomerCode,ifnull(d.strZoneName,''),ifnull(e.strDeliveryTime,'') "
                    + " from tblbillhd a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
                    + " left outer join tblbuildingmaster c on b.strBuldingCode=c.strBuildingCode "
                    + " left outer join tblzonemaster d on c.strZoneCode=d.strZoneCode "
                    + " left outer join tbladvbookbillhd e on a.strAdvBookingNo=e.strAdvBookingNo "
                    + " left outer join tblhomedelivery f on a.strBillNo=f.strBillNo "
                    + " where a.strBillNo not in (select strBillNo from tblbillsettlementdtl) "
                    + " and a.strOperationType='HomeDelivery' "
                    + " and length(f.strDPCode)=0 ";
			 
			 if (!zoneCode.equalsIgnoreCase("All"))
	            {
	                sql = sql + " and c.strZoneCode='" + zoneCode + "' ";
	            }
	            if (!areaCode.equalsIgnoreCase("All"))
	            {
	                sql = sql + " and c.strBuildingCode='" + areaCode + "' ";
	            }
	            sql = sql + " order by d.strZoneName";
	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				
				List list1 = query.list();
				Object[] obj1=null;
				 JSONArray jArrBillNoData=new JSONArray();
				 if (list1!=null)
					{
						for(int i=0; i<list1.size(); i++)
						{
							 obj1 = (Object[]) list1.get(i);
						
					
							JSONObject objBillNo=new JSONObject();
						
							 String zoneWithDelTime =Array.get(obj1, 2)+ " " +Array.get(obj1, 3) ;
							 objBillNo.put("zoneWithDelTime", zoneWithDelTime);
							 objBillNo.put("strCustomerCode", Array.get(obj1, 1));
							 objBillNo.put("strBillNo",Array.get(obj1, 0));
							 jArrBillNoData.put(objBillNo);
						billNo =obj1[0].toString();

						}	

			           	jObjTableData.put("DeliveryBoyDtl", jArrData);
			         	jObjTableData.put("BillNoDtl", jArrBillNoData);
			         	//jObjTableData.put("Count", jArrDelBoyBillNoCount);
			         	
				      }	 
//				 funLoadDeliveryBoyDtl(delBoyCode,clientCode);
//				int countDelBoy=0,countBill=0;
//				countDelBoy = funLoadDelBoyTables(0, list.size(),delBoyCode,clientCode);
//				countBill =  funLoadBillNoTables(0, list1.size(),billNo,clientCode);
//				jObjTableData.put("countDelBoy", countDelBoy);
//				jObjTableData.put("countBill", countBill);
				
				 JSONArray jArr = new JSONArray();	
				 if (list!=null)
					{
						for(int i=0; i<list.size(); i++)
						{
							Object[] obj = (Object[]) list.get(i);
							
							delBoyCode=obj[0].toString();
							
							 
							sql = "select strBillNo from tblhomedeldtl "
	                        + " where strDPCode='" + delBoyCode + "' and strClientCode='" + clientCode+ "' and strSettleYN='N' ";
							query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
							List listDelBoy = query.list();
							if (listDelBoy!=null)
							{
								for(int j=0; j<listDelBoy.size(); j++)
								{
									 JSONObject objDeliverBoy=new JSONObject();
									String statusDelBoyBillNo = ((String)listDelBoy.get(j));
									objDeliverBoy.put("statusDelBoyBillNo",delBoyCode);
									jArr.put(objDeliverBoy);
								}
							}
						}
					}
				 
				
				 JSONArray jArr1 = new JSONArray();	
				 if (list1!=null)
					{
						for(int i=0; i<list1.size(); i++)
						{
							Object[] obj = (Object[]) list1.get(i);
							
							
							billNo=obj[0].toString();
							
							String sql1 = "select strBillNo from tblhomedeldtl "
											+ "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "' ";
				
							Query query1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql1);	
							List listBill = query.list();
							if (listBill!=null)
							{
								for(int k=0; k<listBill.size(); k++)
								{
									 JSONObject objBillNo=new JSONObject();
									String statusBillNo = ((String)listBill.get(k));
									objBillNo.put("statusBillNo", statusBillNo);
									jArr1.put(objBillNo);
								}
							}
				 
						}
					}
				 jObjTableData.put("BillNo", jArr1);
				 jObjTableData.put("delBoy", jArr);
				 
		}
		
			catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	
	public int funLoadDelBoyTables(int startIdex, int length,String delBoyCode,String clientCode)
	{
		int cntIndex = 0;
		
		try
		{
		for (int i = startIdex; i < length; i++)
	    {
	       if (i == length)
	       {
	          break;
	       }	
		if (cntIndex < 15)
        { 
			String sql = "select strBillNo from tblhomedeldtl "
                        + " where strDPCode='" + delBoyCode + "' and strClientCode='" + clientCode+ "' and strSettleYN='N' ";
			
			 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			 List listDelBoy = query.list();
			 if (listDelBoy!=null)
				{
					if(cntIndex < 15)
					{
						
					}
				}
				
				 cntIndex++;
				
			 System.out.println(cntIndex);
		}
	    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cntIndex;
	}
	public int funLoadBillNoTables(int startIndex, int length,String billNo,String clientCode)
    {
		int cntIndex = 0;

		try
		{
		for (int i = startIndex; i < length; i++)
	    {
	       if (i == length)
	       {
	          break;
	       }	
		if (cntIndex < 15)
        { 
			
			 String sql1 = "select strBillNo from tblhomedeldtl "
                     + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "' ";
			
			 Query query1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql1);	
			
			List listBill = query1.list();	
				
				 
				 if (listBill!=null)
				{
					if(cntIndex < 15)
					{
						
					}
				} 
				 cntIndex++;
			 
			 System.out.println(cntIndex);
		}
	    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cntIndex;
    }
	
	
	
	
	@SuppressWarnings("finally")
	public JSONObject funLoadBillAndDeliveryBoyDtl(String delBoyCode,String billno,String clientCode)
	{
		JSONObject jObjTableData1=new JSONObject();
		 JSONObject objBillNo=new JSONObject();	
		JSONArray jArrDelBoyBillNoCount=new JSONArray();
		JSONArray jArrDelBoyBillNoCount1=new JSONArray();
		try
		{
			
			String  sql = "select strBillNo from tblhomedeldtl "
			                        + " where strDPCode='" + delBoyCode + "' and strClientCode='" + clientCode + "' and strSettleYN='N' ";
						Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
						 JSONObject objBillNo1=new JSONObject();	
						
						 Object[] obj1=null;
						 List sList = query.list();
						 if(sList!=null)
				            {
				            	for (int i = 0; i < sList.size(); i++) 
				            	{

									 String delBillNo =(String) sList.get(i);
									 objBillNo.put("DelBoy", delBillNo);
				            	}
				            } 	
							


			                
			           String sql1 = "select strBillNo from tblhomedeldtl "
		                            + "where strBillNo='" + billno + "' and strClientCode='" + clientCode + "' ";
			              Query query1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql1);
							
						 List sList1 = query1.list();
						 if(sList1!=null)
				            {
				            	for (int i = 0; i < sList1.size(); i++) 
				            	{

				            		String BillNo =(String) sList1.get(i);
				            		objBillNo.put("Bill", BillNo);
				            	}
				            }
	

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return objBillNo;
		}
	}
	
	@SuppressWarnings("finally")
	public void funSaveDelBoyBillDtl(JSONObject jObjAssignHomeDeliveryMaster)
	{
		
		String cardString=null;
		String delBoyDetail=null;
		
		int countAffectedRows = 0;
        String dpCode = "";
        String sqltemptable = "";
        StringBuilder sql = new StringBuilder();
        ArrayList deliveryBoyList=new ArrayList();
        JSONArray temp=new JSONArray();
        JSONArray jArrData = new JSONArray();
        JSONObject jObjTaxList = new JSONObject();	
        List list =null;
        String billNo1=null;
        String billNo=null;
        
        try{
        String clientCode = jObjAssignHomeDeliveryMaster.getString("clientCode");
        String posDate = jObjAssignHomeDeliveryMaster.getString("posDate");
        String areaCode = jObjAssignHomeDeliveryMaster.getString("areaCode");
        String zoneCode = jObjAssignHomeDeliveryMaster.getString("zoneCode");
        
        
        JSONArray billNoDtl=(JSONArray)jObjAssignHomeDeliveryMaster.get("BillNoDtl");
        for (int i = 0; i < billNoDtl.length(); i++) 
        { 
        	JSONObject childJSONObject = billNoDtl.getJSONObject(i);
        	 billNo1 = childJSONObject.getString("BillNo");
        	 //billNo.split(billNo);
        	 String[] output = billNo1.split(" ");
        	 billNo=output[0]; 
        }
        
        JSONArray delBoyDtl=(JSONArray)jObjAssignHomeDeliveryMaster.get("DelBoyDtl");
        for (int i = 0; i < delBoyDtl.length(); i++) 
        { 
        	JSONObject childJSONObject = delBoyDtl.getJSONObject(i);
        	dpCode = childJSONObject.getString("DPCode");
        }
        

		for (int cnt = 0; cnt < billNoDtl.length(); cnt++)
		{
			JSONObject billNoDtl1=billNoDtl.getJSONObject(cnt);
		
			JSONObject objMenu = new JSONObject();

			 sql.setLength(0);
			
            sql.append("Update tblhomedelivery set strDPCode='");
            String sql2 = "";
            JSONObject objMenu1 = new JSONObject();

            sql2 += "," + dpCode;
            deliveryBoyList.add(dpCode);
            sql2 =sql2.substring(1, sql2.length());
            sql.append(sql2);
            sql.append("' where strBillNo='" + billNo + "' ");
            String sqldeltemp = "Delete from tblhomedeldtl where strBillNo='" + billNo + "' ";
         Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqldeltemp);
    	    query.executeUpdate();
    	    sqltemptable = "Insert into tblhomedeldtl(strBillNo,strDPCode,strClientCode,strDataPostFlag,dblDBIncentives,dteBillDate) "
                    + " Values ";

            String sqlCustAreaCode = "select c.strBuildingCode "
                    + "from tblbillhd a "
                    + "left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
                    + "left outer join tblbuildingmaster c on b.strBuldingCode=c.strBuildingCode "
                    + "where a.strBillNo='" + billNo + "'; ";
            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlCustAreaCode);
             list=query.list();
            String buildingCode = "";
            if (list.size() > 0)
            {
            	for (int i = 0; i < list.size(); i++) {
            

            	buildingCode=(String) list.get(i);
            }
            }
            if(delBoyDtl!=null)
            {
            	for (int i = 0; i < delBoyDtl.length(); i++) 
            	{
            		
            		
            		 String sqlDBIncenetives = "select d.strCustAreaCode,d.strDeliveryBoyCode,ifnull(d.dblValue,0.00) "
                             + "from tblareawisedelboywisecharges d "
                             + "where d.strCustAreaCode='" + buildingCode + "' "
                             + "and strDeliveryBoyCode='" + dpCode + "'; ";
            		 
            		 Query rsDBIncentives = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDBIncenetives);
            		 list=rsDBIncentives.list();
            		 String dbIncentives = "0.00";
            		 if (list.size() > 0)
                     {
            			 Object[] obj1 = (Object[]) list.get(i);
                         dbIncentives = obj1[3].toString();
                     }
            		 sqltemptable += "('" + billNo + "','" + dpCode + "','" + clientCode + "','N','" + dbIncentives + "','"+posDate+"'), ";
            	}
            }
			jArrData.put(objMenu);
			StringBuilder sb = new StringBuilder(sqltemptable);
            int index = sb.lastIndexOf(",");
            sqltemptable = sb.delete(index, sb.length()).toString();
         Query q1 = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqltemptable);
         q1.executeUpdate();
        

            countAffectedRows += funUpdateBill(sql);
		}
		JSONObject jObj= new JSONObject();
            if (countAffectedRows > 0)
            {

            	            	
            	funGetOpenBillAndDeliveryBoyDtl(areaCode,zoneCode,clientCode);
            	
//            	 cardString = jObj.getString("DelBoy");
//            		
//            		
//            	 delBoyDetail= jObj.getString("Bill");
//            	
            	

            }
            
		jObjTaxList.put("BillNoDtl", jArrData);   	
		jObjTaxList.put("TotCount", temp);   	
		
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{

		}	
	}
	
	private int funUpdateBill(StringBuilder sql)
    {

        try
        {
        	Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
    	    
            int affectedRows = query.executeUpdate();
            return affectedRows;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
	
	public JSONObject funSetBillAmountAndLooseCash(String billNo)
	{
		JSONObject jObj=new JSONObject();
		try
		{
		String sql = "select a.strBillNo,a.dblGrandTotal from tblbillhd a where a.strBillNo='" + billNo + "'";
		Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		List list = query.list();
		if(list!=null)
		{
			for (int i = 0; i < list.size(); i++) 
        	{
				 Object[] obj = (Object[]) list.get(i);
				 
				 jObj.put("dblGrandTotal",Array.get(obj, 1));
        	}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jObj;
	}
	
}
