package com.apos.controller;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.hopos.controller.clsPostPOSBillData;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Controller
public class clsAPOSMasterController {

	
	public String funGetUserDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();
		JSONArray arrObj = new JSONArray();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
		
		try
		{
			cmsCon = objDb.funOpenAPOSCon("mysql","master");
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			Map<String, String> hmClientPOSCodes = funGetAllClientPOSCodes(propertyPOSCode);
			String sqlUserMaster = "select a.strUserCode,a.strPOSAccess " + " from tbluserhd a " + " where a.dteDateEdited > '" + lastModifiedDate + "'";
			ResultSet rsUserMaster = st1.executeQuery(sqlUserMaster);
			while (rsUserMaster.next())
			{
				String POSAccessCodes = rsUserMaster.getString(2);
				String userCode = rsUserMaster.getString(1);
				String[] arrPOSAccessCodes = POSAccessCodes.split(",");
				if (arrPOSAccessCodes.length>0)
				{
					if (hmClientPOSCodes.containsKey(arrPOSAccessCodes[0]))
					{
						//String sql = "select a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd,a.strEdit" + ",a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant,a.strTLA,a.strAuditing " + "from " + masterName + " a, tbluserhd b " + "where a.strUserCode = b.strUserCode and b.strUserCode='" + userCode + "' and b.dteDateEdited > '" + lastModifiedDate + "'";
						String sql=" SELECT a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd, "
								+" a.strEdit,a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant,a.strTLA, "
								+" a.strAuditing FROM tbluserdtl a, tbluserhd b "
								+" WHERE a.strUserCode = b.strUserCode and a.strGrant='true' "
 								+" AND b.dteDateEdited > '" + lastModifiedDate + "' "
 								+" and (a.strFormName='Direct Biller' or a.strFormName='Make KOT' " 
								+" or a.strFormName='VoidKot'  "
								+" or a.strFormName='Make Bill' or a.strFormName='Sales Report' " 
								+" or a.strFormName='Reprint' or a.strFormName='SettleBill'  "
								+" or a.strFormName='TableStatusReport' or a.strFormName='NCKOT'  "
								+" or a.strFormName='Take Away' or a.strFormName='Table Reservation'  " 
								+" or a.strFormName='POS Wise Sales' or a.strFormName='Customer Order'  "
								+" or a.strFormName='Day End' or a.strFormName='KDSForKOTBookAndProcess'  "
								+" or a.strFormName='Kitchen Process System' or a.strFormName='Change Settlement'  "
								+" or a.strFormName='Customer Master' or a.strFormName='Move KOT' or a.strFormName='Move Table' "
								+" or a.strFormName='Move Items To Table' or a.strFormName='Billing')";
								
						System.out.println(sql);
						ResultSet rsMasterData = st.executeQuery(sql);
						while (rsMasterData.next())
						{
							JSONObject obj = new JSONObject();

							obj.put("UserCode", rsMasterData.getString(1));
							obj.put("FormName", rsMasterData.getString(2));
							obj.put("ButtonName", rsMasterData.getString(3));
							obj.put("Sequence", rsMasterData.getString(4));
							obj.put("Add", rsMasterData.getString(5));
							obj.put("Edit", rsMasterData.getString(6));
							obj.put("Delete", rsMasterData.getString(7));
							obj.put("View", rsMasterData.getString(8));
							obj.put("Print", rsMasterData.getString(9));
							obj.put("Save", rsMasterData.getString(10));
							obj.put("Grant", rsMasterData.getString(11));
							obj.put("TLA", rsMasterData.getString(12));
							obj.put("Auditing", rsMasterData.getString(13));

							arrObj.put(obj);
						}
						rsMasterData.close();
					}
				}
			}

			jObj.put("tbluserdtl", arrObj);

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != st1)
				{
					st1.close();
				}
				if (null != cmsCon)
				{
					cmsCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}
	
	
	public String funGetSuperUserDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();
		JSONArray arrObj = new JSONArray();

		try
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			cmsCon = objDb.funOpenAPOSCon("mysql","master");
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			Map<String, String> hmClientPOSCodes = funGetAllClientPOSCodes(propertyPOSCode);

			String sqlUserMaster = "select a.strUserCode,a.strPOSAccess " + " from tbluserhd a " + " where a.dteDateEdited > '" + lastModifiedDate + "'  and a.strSuperType='Super' ";
			ResultSet rsUserMaster = st1.executeQuery(sqlUserMaster);
			while (rsUserMaster.next())
			{
				String POSAccessCodes = rsUserMaster.getString(2);
				String userCode = rsUserMaster.getString(1);
				String[] arrPOSAccessCodes = POSAccessCodes.split(",");
				for (int cnt = 0; cnt < arrPOSAccessCodes.length; cnt++)
				{
					if (hmClientPOSCodes.containsKey(arrPOSAccessCodes[cnt]))
					{
						//String sql = "select a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd,a.strEdit" + ",a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant,a.strTLA,a.strAuditing " + "from " + masterName + " a, tbluserhd b " + "where a.strUserCode = b.strUserCode and b.strUserCode='" + userCode + "' and b.dteDateEdited > '" + lastModifiedDate + "'";
						String sql=" SELECT a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd, "
								+" a.strEdit,a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant,a.strTLA, "
								+" a.strAuditing FROM tblsuperuserdtl a, tbluserhd b "
								+" WHERE a.strUserCode = b.strUserCode "
 								+" AND b.strUserCode='"+userCode+"' AND b.dteDateEdited > '" + lastModifiedDate + "' "
 								+" and (a.strFormName='Direct Biller' or a.strFormName='Make KOT' " 
								+" or a.strFormName='VoidKot'  "
								+" or a.strFormName='Make Bill' or a.strFormName='Sales Report' " 
								+" or a.strFormName='Reprint' or a.strFormName='SettleBill'  "
								+" or a.strFormName='TableStatusReport' or a.strFormName='NCKOT'  "
								+" or a.strFormName='Take Away' or a.strFormName='Table Reservation'  " 
								+" or a.strFormName='POS Wise Sales' or a.strFormName='Customer Order'  "
								+" or a.strFormName='Day End' or a.strFormName='KDSForKOTBookAndProcess'  "
								+" or a.strFormName='Kitchen Process System' or a.strFormName='Change Settlement'  "
								+" or a.strFormName='Customer Master' or a.strFormName='Move KOT' or a.strFormName='Move Table' "
								+" or a.strFormName='Move Items To Table' or a.strFormName='Billing')";
						
						System.out.println(sql);
						ResultSet rsMasterData = st.executeQuery(sql);
						while (rsMasterData.next())
						{
							JSONObject obj = new JSONObject();
							obj.put("UserCode", rsMasterData.getString(1));
							obj.put("FormName", rsMasterData.getString(2));
							obj.put("ButtonName", rsMasterData.getString(3));
							obj.put("Sequence", rsMasterData.getString(4));
							obj.put("Add", rsMasterData.getString(5));
							obj.put("Edit", rsMasterData.getString(6));
							obj.put("Delete", rsMasterData.getString(7));
							obj.put("View", rsMasterData.getString(8));
							obj.put("Print", rsMasterData.getString(9));
							obj.put("Save", rsMasterData.getString(10));
							obj.put("TLA", rsMasterData.getString(12));
							obj.put("Auditing", rsMasterData.getString(13));
							arrObj.put(obj);
						}
						rsMasterData.close();
					}
				}
			}
			jObj.put("tblsuperuserdtl", arrObj);

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != st1)
				{
					st1.close();
				}
				/*if (null != cmsCon)
				{
					cmsCon.close();
				}*/
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	
	
	private Map<String, String> funGetAllClientPOSCodes(String propertyPOSCode)
	{
		Map<String, String> hmClientPOSCodes = new HashMap<String, String>();

		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;

		try
		{
			cmsCon = objDb.funOpenPOSCon("mysql", "master");
			//cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String propertyCode = propertyPOSCode.substring(0, 7);

			String sql = "select strPOSCode from tblposmaster where left(strPropertyPOSCOde,7)='" + propertyCode + "' ";
			ResultSet rsPOSCode = st.executeQuery(sql);
			while (rsPOSCode.next())
			{
				sql = "select right(a.strPropertyPOSCode,3),a.strPosCode,a.strPosName from tblposmaster a " + " where a.strPOSCode='" + rsPOSCode.getString(1) + "'";
				ResultSet rsHOPOSCode = st1.executeQuery(sql);
				if (rsHOPOSCode.next())
				{
					hmClientPOSCodes.put(rsPOSCode.getString(1), rsHOPOSCode.getString(1));
				}
				rsHOPOSCode.close();
			}
			rsPOSCode.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != st1)
				{
					st1.close();
				}
				if (null != cmsCon)
				{
					cmsCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return hmClientPOSCodes;
		
	}

	
	public JSONObject funGetMasterData(String masterName,  String propertyPOSCode, String lastModifiedDate)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
	    Connection cmsCon=null;
	    Statement st=null;
	    JSONObject jObj=new JSONObject();
	   try
		{
			cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            
			String sql = "select * from tblmasteroperationstatus where dteDateEdited > '" + lastModifiedDate + "' and strTableName in('Area','BillSeries','Group','Menu','MenuItem','MenuItemPricing','Modifier','Promotion','Reason','Settlement','SubGroup','SubMenuHead','Tax','CostCenter','Waiter','DiscountMaster','Table','Customer')";
			
			if(!masterName.isEmpty()){
				if(masterName.contains(",")){
					String masterName1[]=masterName.split(",");
					String column="";
					for (String col:masterName1){
						column=column+"'"+col+"',";
					}
					column=column.substring(0,column.length()-1);
					sql="select * from tblmasteroperationstatus where dteDateEdited > '" + lastModifiedDate + "' and strTableName in("+column+")";
				}else{
					sql="select * from tblmasteroperationstatus where dteDateEdited > '" + lastModifiedDate + "' and strTableName in('"+masterName+"')";	
				}
				
			}
			System.out.println(sql);
			ResultSet rsMasterStatus = st.executeQuery(sql);
			String tableName="";
			ArrayList<String> detailTablename;
			clsPostPOSBillData objPostPOSBillData  =new clsPostPOSBillData();
			JSONObject jObj1,jObj2;
			while (rsMasterStatus.next())
			{
				detailTablename=new ArrayList<String>();
				switch(rsMasterStatus.getString(1)){
					case "Area":
						tableName="tblareamaster";
						break;
					case "BillSeries":
						tableName="tblbillseries";
						break;
					case "CostCenter":
						tableName="tblcostcentermaster";
						break;
					case "Group":
						tableName="tblgrouphd";
						break;
					case "Menu":
						tableName="tblmenuhd";
						break;
					case "MenuItem":
						tableName="tblitemmaster";
						
						break;
					case "MenuItemPricing":
						tableName="tblmenuitempricingdtl";
						detailTablename.add("tblmenuitempricinghd");
						break;
					case "Modifier":
						
						tableName="tblmodifiermaster";
						detailTablename.add("tblmodifiergrouphd");
						detailTablename.add("tblitemmodofier");
						//tblmodifiermaster  tblmodifiergrouphd  tblitemmodofier
						break;
					case "Promotion":
						tableName="tblpromotionmaster";
						break;
						
					case "Reason":
						tableName="tblreasonmaster";
						break;
						
					case "Settlement":
						tableName="tblsettelmenthd";
						detailTablename.add("tblsettlementtax");
						jObj1=funGetPOSSettlementDtl(propertyPOSCode.substring(0,7), lastModifiedDate);
						jObj.put("tblpossettlementdtl", jObj1.get("tblpossettlementdtl"));
						break;
					case "SubGroup":
						tableName="tblsubgrouphd";
						break;
					case "SubMenuHead":
						tableName="tblsubmenuhead";
						break;

					case "Tax":
						tableName="tbltaxhd";
						detailTablename.add("tbltaxposdtl");
						//detailTablename.add("tbltaxongroup");
						detailTablename.add("tblsettlementtax");
						detailTablename.add("tbltaxongroup");
						
						break;

					case "Waiter":
						tableName="tblwaitermaster";
						break;
						
					case "Table":
						tableName="tbltablemaster";
						break;

					case "DiscountMaster":
						tableName="tbldischd";
						detailTablename.add("tbldiscdtl");
						break;
					case "Customer":
						tableName="tblcustomermaster";
						jObj1=funGetCustomerTypeMaster(propertyPOSCode.substring(0,7), lastModifiedDate);
						jObj.put("tblcustomertypemaster", jObj1.get("tblcustomertypemaster"));
						break;
						
					case "DayEnd":
						jObj1=funGetDayEndProcess(propertyPOSCode.substring(0,7), lastModifiedDate);
						jObj.put("tbldayendprocess", jObj1.get("tbldayendprocess"));
						break;
					
				}
				
				jObj1= new JSONObject(objPostPOSBillData.funFetchMasterData(tableName,  propertyPOSCode,  lastModifiedDate));
				if(jObj1.length()>0){
					jObj.put(tableName, jObj1.get(tableName));
					//arrObj.put(jObj);
				}
				if(detailTablename.size()>0){
					for(String table:detailTablename){
						jObj2= new JSONObject(objPostPOSBillData.funFetchMasterData(table,  propertyPOSCode,  lastModifiedDate));
						if(jObj2.length()>0){
							jObj.put(table, jObj2.get(table));
						}	
					}
				}
			}
			rsMasterStatus.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	   	finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != cmsCon)
				{
					cmsCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj;
	}

	public JSONObject funGetPOSMaster(String clientCode, String lastModifiedDate)
	{
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			posCon = objDb.funOpenAPOSCon("mysql","master");
			st = posCon.createStatement();
			String sql="select * from tblposmaster where dteDateEdited > '" + lastModifiedDate + "' and strClientCode='"+clientCode+"' order by dteDateEdited";	
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			ResultSetMetaData resultSetMetaData = rsMasterData.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= columnCount; i++)
				{
						obj.put("Column" + i, rsMasterData.getString(i));
				}
				arrObj.put(obj);
			}
			rsMasterData.close();
			jObj.put("tblposmaster", arrObj);
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != posCon)
				{
					posCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj;
	}

	
	public JSONObject funGetCustomerTypeMaster(String clientCode, String lastModifiedDate)
	{
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			posCon = objDb.funOpenAPOSCon("mysql","master");
			st = posCon.createStatement();
			String sql="select * from tblcustomertypemaster where dteDateEdited > '" + lastModifiedDate + "' and strClientCode='"+clientCode+"' order by dteDateEdited";	
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			ResultSetMetaData resultSetMetaData = rsMasterData.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= columnCount; i++)
				{
						obj.put("Column" + i, rsMasterData.getString(i));
				}
				arrObj.put(obj);
			}
			rsMasterData.close();
			jObj.put("tblcustomertypemaster", arrObj);
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != posCon)
				{
					posCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj;
	}
	
	public JSONObject funGetDayEndProcess(String clientCode, String lastModifiedDate)
	{
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			posCon = objDb.funOpenAPOSCon("mysql","master");
			st = posCon.createStatement();
			String sql="select dtePOSDate,intShiftCode,strShiftEnd,strDayEnd,strPOSCode,strClientCode from tbldayendprocess where "
					+ "dteDayEndDateTime > '" + lastModifiedDate + "' and strClientCode='"+clientCode+"' order by dteDayEndDateTime DESC LIMIT 2 ";	
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			ResultSetMetaData resultSetMetaData = rsMasterData.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= columnCount; i++)
				{
						obj.put("Column" + i, rsMasterData.getString(i));
				}
				arrObj.put(obj);
			}
			rsMasterData.close();
			jObj.put("tbldayendprocess", arrObj);
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != posCon)
				{
					posCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj;
	}
	
	public JSONObject funGetInternal(String clientCode, String lastModifiedDate)
	{
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			posCon = objDb.funOpenAPOSCon("mysql","master");
			st = posCon.createStatement();
			String sql="SELECT * FROM tblinternal a WHERE a.strTransactionType IN('custtype','KOTNo','CreditReceipt','OrderNo') ";	
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			ResultSetMetaData resultSetMetaData = rsMasterData.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= columnCount; i++)
				{
						obj.put("Column" + i, rsMasterData.getString(i));
				}
				arrObj.put(obj);
			}
			rsMasterData.close();
			jObj.put("tblinternal", arrObj);
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != posCon)
				{
					posCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj;
	}
	
	public JSONObject funGetPOSSettlementDtl(String clientCode, String lastModifiedDate)
	{
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			posCon = objDb.funOpenAPOSCon("mysql","master");
			st = posCon.createStatement();
			String sql="select * from tblpossettlementdtl";	
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			ResultSetMetaData resultSetMetaData = rsMasterData.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= columnCount; i++)
				{
						obj.put("Column" + i, rsMasterData.getString(i));
				}
				arrObj.put(obj);
			}
			rsMasterData.close();
			jObj.put("tblpossettlementdtl", arrObj);
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if (null != posCon)
				{
					posCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj;
	}
	
	public JSONObject funGetLastStoreBill(String clientCode, String lastModifiedDate)
	{
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			posCon = objDb.funOpenAPOSCon("mysql","master");
			st = posCon.createStatement();
			String sql="select * from tblstorelastbill";	
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("posCode", rsMasterData.getString(1));
				obj.put("billNo", rsMasterData.getInt(2));
				arrObj.put(obj);
			}
			rsMasterData.close();
			jObj.put("tblstorelastbill", arrObj);
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != st)
				{
					st.close();
				}
				if(null != posCon)
				{
					posCon.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj;
	}
}
