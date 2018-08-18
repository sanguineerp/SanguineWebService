package com.apos.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.bean.clsTaxCalculationBean;
import com.apos.controller.clsUtilityController;
import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsMakeKOTHdModel;
import com.apos.model.clsMenuItemPricingHdModel;
import com.apos.model.clsNonChargableKOTHdModel;
import com.apos.service.clsSetupService;
import com.apos.util.clsGlobalFunctions;
import com.cmsws.controller.clsCMSIntegration;
import com.webservice.controller.clsConfigFile;
import com.webservice.util.clsItemDtlForTax;
import com.webservice.util.clsTaxCalculationDtls;
import com.webservice.util.clsUtilityFunctions;

@Repository("clsMakeKOTDao")
@Transactional(value = "webPOSTransactionManager")
public class clsMakeKOTDao
{
	@Autowired
	private SessionFactory webPOSSessionFactory;

	@Autowired
	private clsSetupService objSetupService;

	@Autowired
	private clsUtilityController objUtility;

	private String clsAreaCode = "";
	private String gAreaCodeForTrans = "";
	ArrayList<String> ListTDHOnModifierItem = new ArrayList<>();
	ArrayList<Double> ListTDHOnModifierItemMaxQTY = new ArrayList<>();
	public String strCounterCode = "",globalTableNo;
	public double taxAmt = 0.00;
	private String gInrestoPOSIntegrationYN = "";

	@SuppressWarnings("finally")
	public JSONObject funGetReservTableDtl(String posDate)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select strTableNo,strCustomerCode "
					+ " from tblreservation where dteResDate = '" + posDate + "'";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();
			JSONArray jArrData = new JSONArray();

			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					String billNo = obj[0].toString();
					String itemCode = obj[1].toString();
					JSONObject objSettle = new JSONObject();
					objSettle.put("strTableNo", obj[0].toString());
					objSettle.put("strCustomerCode", obj[1].toString());
					jArrData.put(objSettle);
				}
			}
			jObjTableData.put("reserveTables", jArrData);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funLoadTableDtl(String clientCode, String posCode)
	{
		List list = null;
		String sql;
		Map<String, Integer> hmTableSeq = new HashMap<String, Integer>();
		JSONObject jObjTableData = new JSONObject();
		try
		{
			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");
			if (objSetupParameter.get("gCMSIntegrationYN").toString().equalsIgnoreCase("Y"))
			{
				objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gTreatMemberAsTable");
				if (objSetupParameter.get("gTreatMemberAsTable").toString().equalsIgnoreCase("Y"))
				{
					sql = "select strTableNo,strTableName from tbltablemaster "
							+ " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
							+ " and strOperational='Y' and strStatus!='Normal' "
							+ " order by strTableName";
				}
				else
				{
					sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
							+ " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
							+ " and strOperational='Y' "
							+ " order by intSequence";
				}
			}
			else
			{
				sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
						+ " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
						+ " and strOperational='Y' "
						+ " order by intSequence";
			}

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();
			JSONArray jArrData = new JSONArray();

			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					hmTableSeq.put(obj[0].toString() + "!" + obj[1].toString(), (int) obj[2]);

				}
			}
			clsGlobalFunctions objGlobal = new clsGlobalFunctions();
			hmTableSeq = objGlobal.funSortMapOnValues(hmTableSeq);
			Object[] arrObjTables = hmTableSeq.entrySet().toArray();
			jArrData = new JSONArray();
			for (int cntTable = 0; cntTable < hmTableSeq.size(); cntTable++)
			{
				// System.out.println("Counter="+cntTable+"\tStart="+startIndex+"\tTotal Size="+totalSize);
				if (cntTable == hmTableSeq.size())
				{
					break;
				}
				String tblInfo = arrObjTables[cntTable].toString().split("=")[0];
				String tblNo = tblInfo.split("!")[0];
				String tableName = tblInfo.split("!")[1];
				sql = "select strTableNo,strStatus,intPaxNo from tbltablemaster "
						+ " where strTableNo='" + tblNo + "' "
						+ " and strOperational='Y' "
						+ " order by intSequence";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

				list = query.list();

				if (list.size() > 0)
				{

					Object[] obj = (Object[]) list.get(0);

					JSONObject jobj = new JSONObject();

					jobj.put("strTableName", tableName);
					jobj.put("strTableNo", obj[0].toString());
					jobj.put("strStatus", obj[1].toString());
					jobj.put("intPaxNo", obj[2].toString());
					jArrData.put(jobj);
				}
			}
			jObjTableData.put("tableDtl", jArrData);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funGetWaiterList(String posCode)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select strWaiterNo,strWShortName,strWFullName "
					+ " from tblwaitermaster where strOperational='Y' and (strPOSCode='All' or strPOSCode='" + posCode + "')  ";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();
			JSONArray jArrData = new JSONArray();

			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					objSettle.put("strWaiterNo", obj[0].toString());
					objSettle.put("strWShortName", obj[1].toString());
					objSettle.put("strWFullName", obj[2].toString());
					jArrData.put(objSettle);
				}
			}
			jObjTableData.put("waiterList", jArrData);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funGetButttonList(String transName, String posCode, String posClientCode)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select strButtonName from tblbuttonsequence where strTransactionName='" + transName + "' and (strPOSCode='All' or strPOSCode='" + posCode + "') and strClientCode='" + posClientCode + "' "
					+ "  order by intSeqNo ";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();
			JSONArray jArrData = new JSONArray();

			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object obj = (Object) list.get(i);

					jArrData.put(obj.toString());
				}
			}
			jObjTableData.put("buttonList", jArrData);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funChekReservation(String tableNo)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select a.strTableNo,a.strTableName,a.strStatus "
					+ "from tbltablemaster a "
					+ "where a.strTableNo='" + tableNo + "' "
					+ "and a.strStatus='Reserve' ";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{
				sql = "select a.strResCode,a.strCustomerCode,b.strCustomerName,b.longMobileNo "
						+ "from tblreservation a,tblcustomermaster b "
						+ "where a.strTableNo='" + tableNo + "' "
						+ "and a.strCustomerCode=b.strCustomerCode "
						+ "order by a.strResCode desc "
						+ "limit 1; ";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

				list = query.list();
				if (list.size() > 0)
				{
					Object[] obj = (Object[]) list.get(0);

					jObjTableData.put("strCustomerCode", obj[1].toString());
					jObjTableData.put("strCustomerName", obj[2].toString());
					jObjTableData.put("MobileNo", obj[3]);
					jObjTableData.put("flag", true);

				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funChekCustomerDtl(String clientCode, String posCode, String tableNo)
	{
		List list = null;
		globalTableNo = tableNo;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = " select a.strWaiterNo,a.intPaxNo,sum(a.dblAmount),a.strCardNo,if(a.strCustomerCode='null','',a.strCustomerCode) "
					+ ",ifnull(b.strCustomerName,''),ifnull(b.strBuldingCode,''),a.strHomeDelivery "
					+ " from tblitemrtemp a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
					+ " where a.strTableNo='" + tableNo + "' and a.strPrintYN='Y' and a.strNCKotYN='N' "
					+ " group by a.strTableNo";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{

				Object[] obj = (Object[]) list.get(0);

				jObjTableData.put("strWaiterNo", obj[0].toString());
				jObjTableData.put("intPaxNo", obj[1].toString());
				jObjTableData.put("strCustomerCode", obj[4].toString());
				jObjTableData.put("strCustomerName", obj[5].toString());
				jObjTableData.put("strHomeDelivery", obj[7].toString());
				jObjTableData.put("flag", true);

				sql = "select strWaiterNo from tbltablemaster where strTableNo='" + tableNo + "'";

				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

				String waiterNo = "";
				list = query.list();
				if (list.size() > 0)
				{
					Object objTime = (Object) list.get(0);
					waiterNo = objTime.toString();
					jObjTableData.put("waiterNo", waiterNo);
				}
			}

			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");

			if (objSetupParameter.get("gCMSIntegrationYN").toString().equalsIgnoreCase("Y"))
			{

				sql = "select strCustomerCode ,strCustomerName "
						+ "from tblitemrtemp where strtableno = '" + tableNo + "' and strCustomerCode <>'' ";

				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

				list = query.list();
				if (list.size() > 0)
				{
					Object[] objTime = (Object[]) list.get(0);
					jObjTableData.put("CustomerCode", objTime[0].toString());
					jObjTableData.put("CustomerName", objTime[1].toString());
				}

			}
			sql = "select a.strAreaCode,b.strAreaName from tbltablemaster a,tblareamaster b "
					+ "where a.strTableNo='" + tableNo + "' and a.strAreaCode=b.strAreaCode";

			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();
			if (list.size() > 0)
			{
				Object[] objTime = (Object[]) list.get(0);
				clsAreaCode = objTime[0].toString();
				jObjTableData.put("AreaName", objTime[1].toString());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funChekCardDtl(String tableNo)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = " select a.strWaiterNo,a.intPaxNo,sum(a.dblAmount),b.dblRedeemAmt,a.strCardNo "
					+ " from tblitemrtemp a,tbldebitcardmaster b "
					+ " where a.strCardNo=b.strCardNo and strTableNo='" + tableNo + "' "
					+ " and strPrintYN='Y' and strNCKotYN='N' "
					+ " group by strTableNo";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);
				if ((double) obj[3] > 0)
				{
					double debitCardBal = (double) obj[3];
					debitCardBal -= objUtility.funGetKOTAmtOnTable(obj[4].toString());

					jObjTableData.put("cardBalnce", debitCardBal);
					jObjTableData.put("kotAmt", (double) obj[2]);
					jObjTableData.put("balAmt", (double) obj[3]);

					jObjTableData.put("flag", true);
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funFillOldKOTItems(String clientCode, String posDate, String tableNo, String posCode)
	{
		double amt = 0.00;
		List<clsPOSItemDetailFrTaxBean> arrListItemDtls = new ArrayList<clsPOSItemDetailFrTaxBean>();
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select distinct(strKOTNo) "
					+ " from tblitemrtemp "
					+ " where (strPosCode='" + posCode + "' or strPosCode='All') "
					+ " and strTableNo='" + tableNo + "' and strPrintYN='Y' and strNCKotYN='N' "
					+ " order by strKOTNo DESC";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			list = query.list();
			if (list.size() > 0)
			{
				JSONArray jArr = new JSONArray();
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject jObjData = new JSONObject();

					String kotNo = (String) list.get(i);
					jObjData.put("kotNo", kotNo);

					String sqlKot = "select DATE_FORMAT(dteDateCreated,'%H:%i') from tblitemrtemp where strKOTNo='" + kotNo + "' limit 1";

					query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlKot);
					List timelist = query.list();
					if (timelist.size() > 0)
					{
						Object objTime = (Object) timelist.get(0);
						jObjData.put("kotTime", objTime.toString());
					}
					jArr.put(jObjData);
				}
				jObjTableData.put("OldKOTTimeDtl", jArr);

				sql = "select strKOTNo,strTableNo,strWaiterNo"
						+ " ,strItemName,strItemCode,dblItemQuantity,dblAmount"
						+ " ,intPaxNo,strPrintYN,tdhComboItemYN,strSerialNo,strNcKotYN,dblRate "
						+ " from tblitemrtemp where strTableNo='" + tableNo + "' "
						+ " and (strPosCode='" + posCode + "' or strPosCode='All') "
						+ " and strNcKotYN='N' "
						+ " order by strKOTNo desc ,strSerialNo";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

				list = query.list();
				if (list.size() > 0)
				{
					boolean flag = false;
					JSONArray jArrData = new JSONArray();

					for (int i = 0; i < list.size(); i++)
					{
						Object[] obj = (Object[]) list.get(i);
						JSONObject objSettle = new JSONObject();
						BigDecimal dblAmount = (BigDecimal) obj[6];
						if (dblAmount.doubleValue() >= 0)
						{
							objSettle.put("strKOTNo", obj[0].toString());
							objSettle.put("strTableNo", obj[1].toString());
							objSettle.put("strWaiterNo", obj[2].toString());
							objSettle.put("strItemName", obj[3].toString());
							objSettle.put("strItemCode", obj[4].toString());
							objSettle.put("dblItemQuantity", obj[5]);
							objSettle.put("dblAmount", dblAmount.doubleValue());
							objSettle.put("intPaxNo", obj[7].toString());
							objSettle.put("strPrintYN", obj[8].toString());
							objSettle.put("tdhComboItemYN", obj[9].toString());
							objSettle.put("strSerialNo", obj[10].toString());
							objSettle.put("strNcKotYN", obj[11].toString());
							objSettle.put("dblRate", obj[12].toString());

							clsPOSItemDetailFrTaxBean objItemDtl = new clsPOSItemDetailFrTaxBean();
							objItemDtl.setItemCode(obj[4].toString());
							objItemDtl.setItemName(obj[3].toString());
							objItemDtl.setAmount(dblAmount.doubleValue());
							objItemDtl.setDiscAmt(0);
							arrListItemDtls.add(objItemDtl);
							amt += dblAmount.doubleValue();
							flag = true;
							jArrData.put(objSettle);
						}
					}
					jObjTableData.put("OldKOTItems", jArrData);
					jObjTableData.put("flag", flag);

					JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gCalculateTaxOnMakeKOT");
					if (objSetupParameter.get("gCalculateTaxOnMakeKOT").toString().equalsIgnoreCase("Y"))
					{
						String dtPOSDate = posDate.split(" ")[0];
						List<clsTaxCalculationBean> listTax = objUtility.funCalculateTax(arrListItemDtls, posCode, dtPOSDate, clsAreaCode, "DineIn", 0, 0, "", "S01");
						double taxAmt = 0;
						for (clsTaxCalculationBean objTaxDtl : listTax)
						{
							if (objTaxDtl.getTaxCalculationType().equalsIgnoreCase("Forward"))
							{
								taxAmt = taxAmt + objTaxDtl.getTaxAmount();
							}
						}
						amt += taxAmt;
					}
					jObjTableData.put("Total", amt);
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funFillMapWithHappyHourItems(String posCode, String posDate, String clientCode)
	{
		List list = null;
		String sql;

		JSONObject jObjTableData = new JSONObject();

		try
		{
			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
			if (objSetupParameter.get("gAreaWisePricing").toString().equalsIgnoreCase("N"))
			{
				sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate"
						+ " ,b.strStockInEnable "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
						+ " WHERE a.strItemCode=b.strItemCode "
						+ " and a.strAreaCode='" + gAreaCodeForTrans + "' "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
						+ " and a.strHourlyPricing='Yes'";
			}

			else
			{
				sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday,"
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate"
						+ ",b.strStockInEnable "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
						+ " WHERE a.strAreaCode='" + gAreaCodeForTrans + "' "
						+ " and a.strItemCode=b.strItemCode "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
						+ " and a.strHourlyPricing='Yes'";
			}
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();
			JSONArray jArrData = new JSONArray();
			JSONArray jArrItemCodeData = new JSONArray();

			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					String itemName = obj[1].toString().replace(" ", "&#x00A;");
					JSONObject objSettle = new JSONObject();
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", itemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());

					jArrData.put(objSettle);
					jArrItemCodeData.put(obj[0].toString());
				}
			}
			jObjTableData.put("ItemPriceDtl", jArrData);
			jObjTableData.put("ItemCode", jArrItemCodeData);

			clsUtilityFunctions utility = new clsUtilityFunctions();
			jObjTableData.put("CurrentDate", utility.funGetCurrentDate());
			jObjTableData.put("CurrentTime", utility.funGetCurrentTime());
			jObjTableData.put("DayForPricing", utility.funGetDayForPricing());
			jObjTableData.put("ListTDHOnModifierItem", ListTDHOnModifierItem);
			jObjTableData.put("ListTDHOnModifierItemMaxQTY", ListTDHOnModifierItemMaxQTY);

			sql = "select strPosCode,strPosName,strPosType,strDebitCardTransactionYN"
					+ " ,strPropertyPOSCode,strCounterWiseBilling,strDelayedSettlementForDB,strBillPrinterPort"
					+ " ,strAdvReceiptPrinterPort,strPrintVatNo,strPrintServiceTaxNo,strVatNo,strServiceTaxNo"
					+ " ,strEnableShift from tblposmaster";

			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);
				jObjTableData.put("gDebitCardPayment", obj[3].toString());
			}
			objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gInrestoPOSIntegrationYN");
			gInrestoPOSIntegrationYN = objSetupParameter.get("gInrestoPOSIntegrationYN").toString();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	public JSONObject funGetMenuHeads(String strPOSCode, String userCode)
	{
		LinkedHashMap<String, ArrayList<JSONObject>> mapBillHd;
		mapBillHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		JSONObject jObjTableData = new JSONObject();
		List list = null;
		String strCounterWiseBilling = "";
		try
		{
			String sql = "select strCounterWiseBilling from tblposmaster";

			Query query = webPOSSessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			list = query.list();
			if (list.size() > 0)
				strCounterWiseBilling = (String) list.get(0);

			sql = "select strCounterCode from tblcounterhd "
					+ " where strUserCode='" + userCode + "' ";
			query = webPOSSessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			list = query.list();
			if (list.size() > 0)
				strCounterCode = (String) list.get(0);

			if (strCounterWiseBilling.equalsIgnoreCase("Yes"))

			{

				sql = "select distinct(a.strMenuCode),b.strMenuName "
						+ "from tblmenuitempricingdtl a left outer join tblmenuhd b on a.strMenuCode=b.strMenuCode "
						+ "left outer join tblcounterdtl c on b.strMenuCode=c.strMenuCode "
						+ "left outer join tblcounterhd d on c.strCounterCode=d.strCounterCode "
						+ "where d.strOperational='Yes' "
						+ "and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='ALL') "
						+ "and c.strCounterCode='" + strCounterCode + "' "
						+ "order by b.intSequence";
			}
			else
			{
				sql = "select distinct(a.strMenuCode),b.strMenuName "
						+ "from tblmenuitempricingdtl a left outer join tblmenuhd b "
						+ "on a.strMenuCode=b.strMenuCode "
						+ "where  b.strOperational='Y' "
						+ "and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='ALL') "
						+ "order by b.intSequence";
			}
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strMenuName = obj[1].toString().replace(" ", "&#x00A;");
					objSettle.put("strMenuCode", obj[0].toString());
					objSettle.put("strMenuName", strMenuName);
					jArr.put(objSettle);
				}
			}
			jObjTableData.put("MenuHeads", jArr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jObjTableData;
	}

	public JSONObject funGetItemPricingDtl(String clientCode, String posDate, String posCode)
	{
		LinkedHashMap<String, ArrayList<JSONObject>> mapBillHd;
		mapBillHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		JSONObject jObjTableData = new JSONObject();
		List list = null;
		String strCounterWiseBilling = "", strCounterCode = "", sql_ItemDtl;
		try
		{
			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
			if (objSetupParameter.get("gAreaWisePricing").toString().equalsIgnoreCase("N"))

			{
				sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
						+ " WHERE  a.strItemCode=b.strItemCode "
						+ " and a.strAreaCode='" + gAreaCodeForTrans + "' "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(dteFromDate)<='" + posDate + "' and date(dteToDate)>='" + posDate + "' "
						+ " ORDER BY b.strItemName ASC";
			}
			else
			{
				sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
						+ " WHERE a.strAreaCode='" + clsAreaCode + "' "
						+ "  and a.strItemCode=b.strItemCode "
						// + "WHERE (a.strAreaCode='" + clsAreaCode + "') "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
						+ " ORDER BY b.strItemName ASC";
			}
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_ItemDtl);

			list = query.list();
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					String itemName = obj[1].toString().replace(" ", "&#x00A;");
					JSONObject objSettle = new JSONObject();
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", itemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());
					objSettle.put("strMenuCode", obj[20].toString());

					jArr.put(objSettle);
				}
			}
			jObjTableData.put("MenuItemPricingDtl", jArr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jObjTableData;
	}

	public JSONObject funCheckDebitCardString(String debitCardString, String posCode, String clientCode)
	{

		String waiterNo = "";
		JSONObject jObj = new JSONObject();
		try
		{

			String sql = "select strWaiterNo,strWShortName,strWFullName,strOperational "
					+ " from tblwaitermaster "
					+ " where strDebitCardString='" + debitCardString + "' and (strPOSCode='All' or strPOSCode='" + posCode + "') ";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			List list = query.list();

			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);

				if (!obj[3].toString().equals("N"))
				{
					waiterNo = obj[0].toString() + "#" + obj[1].toString();
				}
			}

			jObj.put("waiterNo", waiterNo);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			return jObj;
		}
	}

	public JSONObject funPopularItem(String clientCode, String posDate, String strPOSCode)
	{

		JSONObject jObjTableData = new JSONObject();
		List list = null;

		try
		{
			String sql = "select strAreaCode from tblareamaster where strAreaName='All'";

			Query query = webPOSSessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			list = query.list();
			if (list.size() > 0)
				gAreaCodeForTrans = (String) list.get(0);

			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, strPOSCode, "gDirectAreaCode");

			sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
					+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
					+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
					+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable "
					+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
					+ " where a.strPopular='Y' and  a.strItemCode= b.strItemCode "
					+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
					+ " and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='All') "
					+ " and (a.strAreaCode='" + objSetupParameter.get("gDirectAreaCode").toString() + "' or a.strAreaCode='" + gAreaCodeForTrans + "') ";

			query = webPOSSessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			list = query.list();
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strItemName = obj[1].toString().replace(" ", "&#x00A;");
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", strItemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());

					jArr.put(objSettle);
				}
			}
			jObjTableData.put("PopularItems", jArr);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jObjTableData;
	}

	public JSONObject funFillTopButtonList(String menuHeadCode, String posCode, String posDate, String clientCode)
	{

		JSONObject jObj = new JSONObject();
		try
		{
			String sqlItems = "";
			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gMenuItemSortingOn");
			if (objSetupParameter.get("gMenuItemSortingOn").toString().equalsIgnoreCase("subgroupWise"))
			{
				sqlItems = "select c.strSubGroupName,b.strSubGroupCode "
						+ " from tblmenuitempricingdtl a,tblitemmaster b,tblsubgrouphd c "
						+ " where a.strItemCode=b.strItemCode "
						+ " and b.strSubGroupCode=c.strSubGroupCode "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' ";

				if (menuHeadCode.equalsIgnoreCase("Popular"))
				{
					sqlItems += " and a.strPopular='Y' and a.strAreaCode='" + clsAreaCode + "' "
							+ " group by c.strSubGroupCode ORDER by c.strSubGroupName";
				}
				else
				{
					sqlItems += " and a.strMenuCode='" + menuHeadCode + "' "
							+ " group by c.strSubGroupCode ORDER by c.strSubGroupName";
				}
			}
			else if (objSetupParameter.get("gMenuItemSortingOn").toString().equalsIgnoreCase("subMenuHeadWise"))
			{
				sqlItems = "select b.strSubMenuHeadName,a.strSubMenuHeadCode "
						+ " from tblmenuitempricingdtl a left outer join tblsubmenuhead b "
						+ " on a.strSubMenuHeadCode=b.strSubMenuHeadCode and a.strMenuCode=b.strMenuCode "
						+ " where b.strSubMenuHeadName is not null and b.strSubMenuOperational='Y' "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' ";

				if (menuHeadCode.equalsIgnoreCase("Popular"))
				{
					sqlItems += " and a.strPopular='Y' "
							+ "group by a.strSubMenuHeadCode";
				}
				else
				{
					sqlItems += " and a.strMenuCode='" + menuHeadCode + "' group by a.strSubMenuHeadCode";
				}
			}

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlItems);

			List list = query.list();
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					objSettle.put("strCode", obj[1].toString());
					objSettle.put("strName", obj[0].toString());

					jArr.put(objSettle);
				}
			}

			jObj.put("topButtonList", jArr);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			return jObj;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funCheckHomeDelivery(String tableNo, String posCode)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select a.strTableNo,ifnull(a.strCustomerCode,''),ifnull(b.strCustomerName,'ND')"
					+ " ,ifnull(b.strBuldingCode,''),ifnull(a.strDelBoyCode,'NA'),ifnull(c.strDPName,'NA') "
					+ " from tblitemrtemp a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
					+ " left outer join tbldeliverypersonmaster c on a.strDelBoyCode=c.strDPCode "
					+ " where a.strHomeDelivery='Yes' and a.strTableNo='" + tableNo + "' "
					+ " and a.strPOSCode='" + posCode + "' ";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);

				jObjTableData.put("strCustomerCode", obj[1].toString());
				jObjTableData.put("strCustomerName", obj[2].toString());
				jObjTableData.put("strBuldingCode", obj[3].toString());
				jObjTableData.put("strDelBoyCode", obj[4].toString());
				jObjTableData.put("strDPName", obj[5].toString());
				jObjTableData.put("flag", true);

			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	public JSONObject funFillitemsSubMenuWise(String strMenuCode, String flag, String selectedButtonCode, String posCode, String posDate, String clientCode)
	{

		JSONObject jObj = new JSONObject();
		try
		{
			String sqlItems = "";
			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gMenuItemSortingOn");
			if (objSetupParameter.get("gMenuItemSortingOn").toString().equalsIgnoreCase("subgroupWise"))
			{
				sqlItems = "SELECT b.strItemCode,c.strItemName,b.strTextColor,b.strPriceMonday,b.strPriceTuesday,"
						+ "b.strPriceWednesday,b.strPriceThursday,b.strPriceFriday,  "
						+ "b.strPriceSaturday,b.strPriceSunday,b.tmeTimeFrom,b.strAMPMFrom,b.tmeTimeTo,b.strAMPMTo,"
						+ "b.strCostCenterCode,b.strHourlyPricing,b.strSubMenuHeadCode,b.dteFromDate,b.dteToDate,c.strStockInEnable "
						+ "FROM tblmenuhd a LEFT OUTER JOIN tblmenuitempricingdtl b ON a.strMenuCode = b.strMenuCode "
						+ "RIGHT OUTER JOIN tblitemmaster c ON b.strItemCode = c.strItemCode "
						+ "WHERE ";

				if (flag.equalsIgnoreCase("Popular"))
				{
					sqlItems += " b.strPopular = 'Y' and c.strSubGroupCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')   "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";

				}
				else
				{
					sqlItems += " a.strMenuCode = '" + strMenuCode + "' and c.strSubGroupCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')  "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";
				}
				objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
				if (objSetupParameter.get("gAreaWisePricing").toString().equalsIgnoreCase("Y"))
				{
					sqlItems = sqlItems + " and (b.strAreaCode='" + gAreaCodeForTrans + "' or b.strAreaCode='" + clsAreaCode + "')";
				}
				sqlItems = sqlItems + " ORDER BY c.strItemName ASC";
			}
			else if (objSetupParameter.get("gMenuItemSortingOn").toString().equalsIgnoreCase("subMenuHeadWise"))
			{
				sqlItems = "SELECT b.strItemCode,c.strItemName,b.strTextColor,b.strPriceMonday,b.strPriceTuesday,"
						+ "b.strPriceWednesday,b.strPriceThursday,b.strPriceFriday,  "
						+ "b.strPriceSaturday,b.strPriceSunday,b.tmeTimeFrom,b.strAMPMFrom,b.tmeTimeTo,b.strAMPMTo,"
						+ "b.strCostCenterCode,b.strHourlyPricing,b.strSubMenuHeadCode,b.dteFromDate,b.dteToDate,c.strStockInEnable "
						+ "FROM tblmenuitempricingdtl b,tblitemmaster c "
						+ "WHERE ";

				if (flag.equalsIgnoreCase("Popular"))
				{
					sqlItems += " b.strMenuCode = '" + selectedButtonCode + "' and b.strItemCode=c.strItemCode and b.strSubMenuHeadCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')  "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";
				}
				else
				{
					sqlItems += " b.strMenuCode = '" + strMenuCode + "' and b.strItemCode=c.strItemCode and b.strSubMenuHeadCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')   "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";

				}

				objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
				if (objSetupParameter.get("gAreaWisePricing").toString().equalsIgnoreCase("Y"))
				{
					sqlItems = sqlItems + " and (b.strAreaCode='" + gAreaCodeForTrans + "' or b.strAreaCode='" + clsAreaCode + "')";
				}
				sqlItems = sqlItems + " ORDER BY c.strItemName ASC";
			}

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlItems);

			List list = query.list();
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strItemName = obj[1].toString().replace(" ", "&#x00A;");
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", strItemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());

					jArr.put(objSettle);
				}
			}

			jObj.put("SubMenuWiseItemList", jArr);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			return jObj;
		}
	}

	public void addTDHOnModifierItem()
	{

		JSONObject objSettle = new JSONObject();
		String sql = "";
		try
		{
			if (ListTDHOnModifierItem.isEmpty())
			{

				sql = "select strItemCode,intMaxQuantity from tbltdhhd where strApplicable='Y' and strComboItemYN='N'; ";
				Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

				List list = query.list();
				JSONArray jArr = new JSONArray();
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] obj = (Object[]) list.get(i);

						ListTDHOnModifierItem.add(obj[0].toString());
						ListTDHOnModifierItemMaxQTY.add((double) obj[1]);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funGenerateKOTNo()
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		String kotNo = "";
		try
		{
			long code = 0;
			String sql = "select dblLastNo from tblinternal where strTransactionType='KOTNo'";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{
				code = ((BigInteger) list.get(0)).longValue();

				code = code + 1;
				kotNo = "KT" + String.format("%07d", code);

			}
			else
			{
				kotNo = "KT0000001";

			}
			jObjTableData.put("strKOTNo", kotNo);

			sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='KOTNo'";
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();
		}

		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}

	}

	public void funSaveKOT(clsMakeKOTHdModel objModel)
	{
		try
		{
			webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void funSaveNCKOT(clsNonChargableKOTHdModel objNCModel)
	{
		try
		{
			webPOSSessionFactory.getCurrentSession().saveOrUpdate(objNCModel);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void funUpdateKOT(JSONObject jObj)
	{

		try
		{
			String posDate = jObj.getString("posDate");
			String strKOTNo = jObj.getString("strKOTNo");
			String strTableNo = jObj.getString("strTableNo");
			String strCustomerCode = jObj.getString("cmsMemberCode");
			String strHomeDelivery = jObj.getString("strHomeDelivery");
			String strNCKotYN = jObj.getString("strNCKotYN");
			int intPaxNo = jObj.getInt("intPaxNo");
			double KOTAmt = jObj.getDouble("KOTAmt");

			String sql = "insert into tblkottaxdtl "
					+ "values ('" + strTableNo + "','" + strKOTNo + "'," + KOTAmt + "," + taxAmt + ")";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

			String sql_update = "update tblitemrtemp set strPrintYN='Y',dteDateCreated='" + posDate + "' "
					+ "where strKOTNo='" + strKOTNo + "' and strTableNo='" + strTableNo + "'";
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_update);
			query.executeUpdate();

			if (strHomeDelivery.equals("Yes"))
			{
				sql = "update tblitemrtemp set strHomeDelivery='Yes',strCustomerCode='" + strCustomerCode + "' "
						+ "where strTableNo='" + strTableNo + "'";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				query.executeUpdate();
			}

			else
			{
				sql = "update tblitemrtemp set strHomeDelivery='No',strCustomerCode='" + strCustomerCode + "' "
						+ "where strTableNo='" + strTableNo + "'";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				query.executeUpdate();
			}
			sql = "update tbldebitcardtabletemp set strPrintYN='Y' where strTableNo='" + strTableNo + "'";
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

			if ("Y".equals(strNCKotYN))
			{

				sql = "update tbltablemaster set strStatus='Normal' "
						+ " where strTableNo='" + strTableNo + "' and strStatus='Normal' ";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				query.executeUpdate();
			}
			else
			{

				sql = "update tbltablemaster set strStatus='Occupied' where strTableNo='" + strTableNo + "'";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				query.executeUpdate();
			}

			sql = "update tbltablemaster set intPaxNo='" + intPaxNo + "' where strTableNo='" + strTableNo + "'";
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

			if (gInrestoPOSIntegrationYN.equalsIgnoreCase("Y"))
			{

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void funInsertIntoTblItemRTempBck(String tableNo, String kotNo)
	{
		try
		{
			String sql = "delete from tblitemrtemp_bck where strTableNo='" + tableNo + "' and strKOTNo='" + kotNo + "' ";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

			sql = "insert into tblitemrtemp_bck (select * from tblitemrtemp where strTableNo='" + tableNo + "' and strKOTNo='" + kotNo + "' )";
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funChekCMSCustomerDtl(String tableNo)
	{
		List list = null;
		JSONObject objSettle = new JSONObject();
		try
		{

			String sql = "select ifnull(sum(dblAmount),0),ifnull(strCustomerCode,''),ifnull(strCustomerName,'') "
					+ "from tblitemrtemp where strtableno = '" + tableNo + "' and strCustomerCode <>'' ";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list != null)
			{
				Object[] obj = (Object[]) list.get(0);
				objSettle.put("dblAmount", obj[0]);
				objSettle.put("strCustomerCode", obj[1].toString());
				objSettle.put("strCustomerName", obj[2].toString());
				objSettle.put("flag", true);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funCheckCustomer(String strMobNo)
	{
		List list = null;
		JSONObject objSettle = new JSONObject();
		try
		{

			String sql = "select count(strCustomerCode) from tblcustomermaster where longMobileNo like '%" + strMobNo + "%'";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list != null)
			{
				int cnt = ((BigInteger) list.get(0)).intValue();
				if (cnt > 0)
				{
					sql = "select strCustomerCode,strCustomerName,strBuldingCode "
							+ "from tblcustomermaster where longMobileNo like '%" + strMobNo + "%'";
					query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

					list = query.list();

					if (list != null)
					{
						Object[] obj = (Object[]) list.get(0);

						objSettle.put("strCustomerCode", obj[0].toString());
						objSettle.put("strCustomerName", obj[1].toString());
						objSettle.put("strBuldingCode", obj[2].toString());
						objSettle.put("flag", true);
					}
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funCheckMemeberBalance(String strCustomerCode)
	{

		JSONObject objSettle = new JSONObject();
		String memberInfo;
		double balance = 0, creditLimit = 0;
		try
		{
			clsCMSIntegration cmsObj = new clsCMSIntegration();
			String jsonString = cmsObj.funGetMemberDataFromDB(strCustomerCode);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(jsonString);
			JSONObject jObj = (JSONObject) obj;
			JSONArray mJsonArray = (JSONArray) jObj.get("MemberInfo");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				if (mJsonObject.get("DebtorCode").toString().equals("no data"))
				{
					memberInfo = "no data";
				}
				else
				{
					memberInfo = mJsonObject.get("DebtorCode").toString() + "#" + mJsonObject.get("DebtorName").toString();
					balance = Double.parseDouble(mJsonObject.get("BalanceAmt").toString());
					creditLimit = Double.parseDouble(mJsonObject.get("CreditLimit").toString());
					String expired = mJsonObject.get("Expired").toString();
					String stopCredit = mJsonObject.get("StopCredit").toString();
					double settleBalance = creditLimit - balance;
					memberInfo += "#" + balance + "#" + settleBalance + "#" + expired + "#" + creditLimit + "#" + stopCredit;
					objSettle.put("flag", true);

					if (!expired.equalsIgnoreCase("Y"))
					{
						String sql = "update tblitemrtemp set strCustomerCode='" + mJsonObject.get("DebtorCode").toString() + "' "
								+ " ,strCustomerName = '" + mJsonObject.get("DebtorName").toString() + "' "
								+ " where strTableNo='" + globalTableNo + "'";
						Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
						query.executeUpdate();
						funInsertIntoTblItemRTempBck();
					}

				}
				objSettle.put("memberInfo", memberInfo);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}
	}

	public void funInsertIntoTblItemRTempBck()
	{
		try
		{
			String sql = "delete from tblitemrtemp_bck where strTableNo='" + globalTableNo + "'  ";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

			sql = "insert into tblitemrtemp_bck (select * from tblitemrtemp where strTableNo='" + globalTableNo + "'  )";
			query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funChekCRMCustomerDtl(String custMobileNo)
	{
		List list = null;
		JSONObject objSettle = new JSONObject();
		try
		{

			String sql = "select strCustomerCode from tblcustomermaster where longMobileNo=" + custMobileNo;
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list != null)
			{
				Object obj = (Object) list.get(0);

				objSettle.put("strCustomerCode", obj.toString());

			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funCheckKOTSave(String KOTNo)
	{
		List list = null;
		JSONObject objSettle = new JSONObject();
		try
		{

			String sql = "select * from tblitemrtemp where strTableNo='" + globalTableNo + "'";

			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{
				objSettle.put("savedKOT", true);
				sql = "select strPrintYN from tblitemrtemp where strKOTNo='" + KOTNo + "' "
						+ "and strTableNo='" + globalTableNo + "' and strPrintYN='N' group by  strPrintYN";
				query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

				list = query.list();

				if (list.size() > 0)
				{
					objSettle.put("flag", true);
				}

			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funFillTopSortingButtonsForModifier(String itemCode)
	{
		List list = null;
		JSONObject jObj = new JSONObject();
		try
		{

			String sql = "select a.strModifierGroupCode,a.strModifierGroupShortName,a.strApplyMaxItemLimit,"
					+ "a.intItemMaxLimit,a.strApplyMinItemLimit,a.intItemMinLimit  from tblmodifiergrouphd a,tblmodifiermaster b,tblitemmodofier c "
					+ "where a.strOperational='YES' and a.strModifierGroupCode=b.strModifierGroupCode and "
					+ "b.strModifierCode=c.strModifierCode and c.strItemCode='" + itemCode + "' group by a.strModifierGroupCode";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strItemName = obj[1].toString().replace(" ", "&#x00A;");
					objSettle.put("strModifierGroupCode", obj[0].toString());
					objSettle.put("strModifierGroupShortName", strItemName);
					objSettle.put("strApplyMaxItemLimit", obj[2].toString());
					objSettle.put("intItemMaxLimit", obj[3].toString());
					objSettle.put("strApplyMinItemLimit", obj[4].toString());
					objSettle.put("intItemMinLimit", obj[5]);

					jArr.put(objSettle);
				}
			}
			jObj.put("topButtonModifier", jArr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jObj;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funGetModifierAll(String itemCode)
	{
		List list = null;
		JSONObject jObj = new JSONObject();
		try
		{

			String sql = "select a.strModifierName,a.strModifierCode"
					+ " ,b.dblRate,a.strModifierGroupCode,b.strDefaultModifier "
					+ " from tblmodifiermaster a,tblitemmodofier b "
					+ " where a.strModifierCode=b.strModifierCode "
					+ " and b.strItemCode='" + itemCode + "' "
					+ " group by a.strModifierCode;";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					JSONObject objSettle = new JSONObject();
					String strItemName = obj[0].toString().replace(" ", "&#x00A;");
					objSettle.put("strModifierName", obj[0].toString());
					objSettle.put("strModifierCode", obj[1].toString());
					objSettle.put("dblRate", obj[2]);
					objSettle.put("strModifierGroupCode", obj[3].toString());
					objSettle.put("strDefaultModifier", obj[4].toString());

					jArr.put(objSettle);
				}
			}
			jObj.put("Modifiers", jArr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jObj;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funCalculateTax(JSONArray arrKOTItemDtlList, String clientCode, String posCode, String posDate)
	{
		double amt = 0.00;
		List<clsPOSItemDetailFrTaxBean> arrListItemDtls = new ArrayList<clsPOSItemDetailFrTaxBean>();
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			for (int i = 0; i < arrKOTItemDtlList.length(); i++)
			{

				String itemDtl = arrKOTItemDtlList.getString(i);

				String[] arrItemDtl = itemDtl.split("_");
				double dblAmount = Double.parseDouble(arrItemDtl[2]);
				if (dblAmount >= 0)
				{
					clsPOSItemDetailFrTaxBean objItemDtl = new clsPOSItemDetailFrTaxBean();
					objItemDtl.setItemCode(arrItemDtl[0]);
					objItemDtl.setItemName(arrItemDtl[1]);
					objItemDtl.setAmount(dblAmount);
					objItemDtl.setDiscAmt(0);
					arrListItemDtls.add(objItemDtl);

					amt += dblAmount;

				}
			}

			JSONObject objSetupParameter = objSetupService.funGetParameterValuePOSWise(clientCode, posCode, "gCalculateTaxOnMakeKOT");
			if (objSetupParameter.get("gCalculateTaxOnMakeKOT").toString().equalsIgnoreCase("Y"))
			{
				String dtPOSDate = posDate.split(" ")[0];

				List<clsTaxCalculationBean> listTax = objUtility.funCalculateTax(arrListItemDtls, posCode, dtPOSDate, clsAreaCode, "DineIn", 0, 0, "", "S01");
				double taxAmt = 0;
				for (clsTaxCalculationBean objTaxDtl : listTax)
				{
					if (objTaxDtl.getTaxCalculationType().equalsIgnoreCase("Forward"))
					{
						taxAmt = taxAmt + objTaxDtl.getTaxAmount();
					}
				}
				amt += taxAmt;

			}
			jObjTableData.put("Total", String.valueOf(amt));

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	@SuppressWarnings("finally")
	public JSONObject funGetCustomerAddress(String mobileNo)
	{
		List list = null;
		JSONObject objSettle = new JSONObject();
		try
		{

			String sql = "select a.strCustomerCode,a.strCustomerName,a.longMobileNo,a.strBuldingCode,ifnull(a.strCustAddress,'')  "
					+ ",ifnull(a.strStreetName,''),ifnull(a.strLandmark,''),ifnull(a.intPinCode,''),ifnull(a.strCity,''),ifnull(a.strState,'') "
					+ ",a.strOfficeBuildingCode,ifnull(a.strOfficeBuildingName,''),ifnull(a.strOfficeStreetName,''),ifnull(a.strOfficeLandmark,''),ifnull(a.intPinCode,'') "
					+ ",ifnull(a.strOfficeCity,''),ifnull(a.strOfficeState,'') "
					+ ",ifnull(a.strTempAddress,''),ifnull(a.strTempStreet,''),ifnull(a.strTempLandmark,'') "
					+ " ,ifnull(a.strBuildingName,''),ifnull(a.strOfficeBuildingName,''),'' "
					+ "from  tblcustomermaster a "
					+ "where longMobileNo='" + mobileNo + "' ";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);

			list = query.list();

			if (list.size() > 0)
			{

				Object[] obj = (Object[]) list.get(0);

				objSettle.put("strCustomerCode", obj[0].toString());
				objSettle.put("strCustomerName", obj[1].toString());
				objSettle.put("longMobileNo", obj[2].toString());
				objSettle.put("strCustAddress", obj[4].toString());
				objSettle.put("strStreetName", obj[5].toString());
				objSettle.put("strLandmark", obj[6].toString());

				objSettle.put("intPinCode", obj[7].toString());
				objSettle.put("strCity", obj[8].toString());
				objSettle.put("strState", obj[9].toString());
				objSettle.put("strOfficeAddress", obj[11].toString());
				objSettle.put("strOfficeStreetName", obj[12].toString());
				objSettle.put("strOfficeLandmark", obj[13].toString());
				objSettle.put("intOfficePinCode", obj[14].toString());

				objSettle.put("strOfficeCity", obj[15].toString());
				objSettle.put("strOfficeState", obj[16].toString());
				objSettle.put("strTempAddress", obj[17].toString());
				objSettle.put("strTempStreet", obj[18].toString());
				objSettle.put("strTempLandmark", obj[19].toString());

				objSettle.put("strHomeBuildingName", obj[20].toString());// home
																			// building
																			// name
				objSettle.put("strOfficeBuildingName", obj[21].toString());// office
																			// building
																			// name
				objSettle.put("strTempBuildingName", obj[22].toString());// temp
																			// building
																			// name

			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}
	}

	public void funUpdateCustomerTempAddress(String strTempCustAddress, String strTempStreetName, String strTempLandmark, String strMobileNo)
	{
		try
		{
			String sql = "update tblcustomermaster  "
					+ "set strTempAddress='" + strTempCustAddress + "',strTempStreet='" + strTempStreetName + "',strTempLandmark='" + strTempLandmark + "' "
					+ "where longMobileNo='" + strMobileNo + "' ";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			query.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
