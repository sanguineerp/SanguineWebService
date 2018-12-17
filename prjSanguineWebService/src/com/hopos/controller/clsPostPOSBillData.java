package com.hopos.controller;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.Base64Variants;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsConfigFile;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Path("/POSIntegration")
public class clsPostPOSBillData
{

	public clsPostPOSBillData() throws Exception
	{
		if (clsDatabaseConnection.DBPOSCONNECTION == null)
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			clsDatabaseConnection.DBPOSCONNECTION = objDb.funOpenPOSCon("mysql", "transaction");
		}
		else if (clsDatabaseConnection.DBPOSCONNECTION.isClosed())
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			clsDatabaseConnection.DBPOSCONNECTION = objDb.funOpenPOSCon("mysql", "transaction");
		}
	}

	static
	{
		try
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			clsDatabaseConnection.DBPOSCONNECTION = objDb.funOpenPOSCon("mysql", "transaction");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@GET
	@Path("/funInvokeHOWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetBillInfo()
	{
		String response = "true";
		return response;
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPostTransactionDataToHOPOS")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostBillHdDataToHOPOS(JSONObject objBillData)
	{
		String response = "false";
		Iterator callfunction = objBillData.keys();
		while (callfunction.hasNext())
		{
			String key = callfunction.next().toString();
			JSONArray mJsonArray = null;
			try
			{
				mJsonArray = (JSONArray) objBillData.get(key);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (key.equalsIgnoreCase("BillHdInfo") && mJsonArray != null)
			{
				System.out.println("BillHd= " + key);
				if (funInsertBillHdData(mJsonArray, "tblqbillhd") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillDtlInfo") && mJsonArray != null)
			{
				System.out.println("BillDtl= " + key);
				if (funInsertBillDtlData(mJsonArray, "tblqbilldtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillModifierDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillModifierDtlData(mJsonArray, "tblqbillmodifierdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillDiscountDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillDiscountDtlData(mJsonArray, "tblqbilldiscdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillSettlementDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillSettlementDtlData(mJsonArray, "tblqbillsettlementdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillTaxDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillTaxDtlData(mJsonArray, "tblqbilltaxdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillPromotionDtl") && mJsonArray != null)
			{
				if (funInsertBillPromotionDtlData(mJsonArray, "tblqbillpromotiondtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillComplimentryDtl") && mJsonArray != null)
			{
				if (funInsertBillComplimentryDtlData(mJsonArray, "tblqbillcomplementrydtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillSeriesDtlInfo") && mJsonArray != null)
			{
				if (funInsertBillSeriesDtlData(mJsonArray, "tblbillseriesbilldtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("CRMBillPoints") && mJsonArray != null)
			{
				if (funInsertCRMPointsData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidBillHd") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertVoidBillHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidBillDtl") && mJsonArray != null)
			{
				if (funInsertVoidBillDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidBillModifierDtl") && mJsonArray != null)
			{
				if (funInsertVoidBillModDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("StkInHd") && mJsonArray != null)
			{
				if (funInsertStkInHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("StkInDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertStkInDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("StkOutHd") && mJsonArray != null)
			{
				if (funInsertStkOutHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("StkOutDtl") && mJsonArray != null)
			{
				if (funInsertStkOutDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidKot") && mJsonArray != null)
			{
				if (funInsertVoidKotData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("PspHd") && mJsonArray != null)
			{
				if (funInsertPspHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("PspDtl") && mJsonArray != null)
			{
				if (funInsertPspDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("AdvanceReceiptHd") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertAdvanceReceiptHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("AdvanceReceiptDtl") && mJsonArray != null)
			{
				if (funInsertAdvanceReceiptDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("AdvBookBillHd") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertAdvBookBillHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("AdvBookBillDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertAdvBookBillDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("AdvBookBillModifierDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertAdvBookBillModiferDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("AdvBookBillCharDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertAdvBookBillCharDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("HomeDelivery") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertHomeDeliveryData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("HomeDeliveryDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertHomeDeliveryDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidAdvOrderReceiptHd") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertVoidAdvanceReceiptHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidAdvOrderReceiptDtl") && mJsonArray != null)
			{
				if (funInsertVoidAdvanceReceiptDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidAdvOrderBillHd") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertVoidAdvBookBillHdData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidAdvOrderBillDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertVoidAdvBookBillDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidAdvOrderModifierDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertVoidAdvBookBillModiferDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("VoidAdvOrderBillCharDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertVoidAdvBookBillcharDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}

			/*
			 * else if (key.equalsIgnoreCase("PlaceOrderHd") && mJsonArray
			 * !=null) { if (funInsertPlaceOrderHdData(mJsonArray) > 0) {
			 * response = "true"; } else{ response = "false"; } } else if
			 * (key.equalsIgnoreCase("PlaceOrderDtl") && mJsonArray !=null) { if
			 * (funInsertPlaceOrderDtlData(mJsonArray) > 0) { response = "true";
			 * } else{ response = "false"; } } else if
			 * (key.equalsIgnoreCase("PlaceAdvOrderDtl") && mJsonArray !=null) {
			 * if (funInsertPlaceAdvanceOrderDtlData(mJsonArray) > 0) { response
			 * = "true"; } else{ response = "false"; } }
			 */

			else if (key.equalsIgnoreCase("CashManagementData") && mJsonArray != null)
			{
				if (funInsertCashManagementData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			if (key.equalsIgnoreCase("tbldayendprocess") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertDayEndDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
		}
		return Response.status(201).entity(response).build();
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPostSalesDataToHOPOS")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostSalesDataToHOPOS(JSONObject objBillData)
	{
		// String response = "false";
		/*
		 * if (funInsertSalesData(objBillData) > 0) { response = "true"; } else{
		 * response = "false"; }
		 */

		String response = funInsertSalesData(objBillData);
		return Response.status(201).entity(response).build();
	}

	@SuppressWarnings("finally")
	private String funInsertSalesData(JSONObject objSalesData)
	{
		int res = 0;
		String tableUpdated = "";
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;

		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		Map<String, String> hmPOSMaster = new HashMap<String, String>();
		Set<String> setBillInfo = new HashSet<String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			// Code to insert tblbillhd Table
			if (null != objSalesData.get("BillHdInfo"))
			{
				JSONArray jsonArrBillHd = objSalesData.getJSONArray("BillHdInfo");
				flgData = false;
				int cnt = 0;

				String sqlPOSCode = "select strPOSCode,strPropertyPOSCode from tblposmaster";
				ResultSet rsPOS = st.executeQuery(sqlPOSCode);
				while (rsPOS.next())
				{
					hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
				}
				rsPOS.close();

				sbSql.setLength(0);
				sbSql.append("INSERT INTO tblqbillhd (`strBillNo`,`strAdvBookingNo`, `dteBillDate`, `strPOSCode`" + ",`strSettelmentMode`, `dblDiscountAmt`,`dblDiscountPer`, `dblTaxAmt`, `dblSubTotal`,`dblGrandTotal`" + ", `strTakeAway`, `strOperationType`,`strUserCreated`, `strUserEdited`, `dteDateCreated`,`dteDateEdited`" + ", `strClientCode`, `strTableNo`,`strWaiterNo`, `strCustomerCode`, `strManualBillNo`,`intShiftCode`" + ", `intPaxNo`, `strDataPostFlag`,`strReasonCode`, `strRemarks`, `dblTipAmount`,`dteSettleDate`" + ", `strCounterCode`, `dblDeliveryCharges`,`strCouponCode`,`strAreaCode`,`strDiscountRemark`" + ",`strTakeAwayRemarks`,`strDiscountOn`,`strCardNo`,`strTransactionType`"
						+ ",strJioMoneyRRefNo,strJioMoneyAuthCode,strJioMoneyTxnId,strJioMoneyTxnDateTime,strJioMoneyCardNo,strJioMoneyCardType,dblRoundOff,intBillSeriesPaxNo,dtBillDate) " + "VALUES");
				JSONObject mJsonObject = new JSONObject();

				for (int i = 0; i < jsonArrBillHd.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrBillHd.get(i);

					String billNo = mJsonObject.get("BillNo").toString().trim();
					String clientCode = mJsonObject.get("ClientCode").toString().trim();
					String POSCode = mJsonObject.get("POSCode").toString().trim();
					String billDate = mJsonObject.get("BillDate").toString();

					String propertyPOSCode = clientCode + "." + POSCode;
					POSCode = hmPOSMaster.get(propertyPOSCode);

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillhd " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "' and strPOSCode='" + POSCode + "' and date(dteBillDate)='" + billDate + "' ");
					st.executeUpdate(sbSqlDelete.toString());

					String billInfo = billNo + "#" + POSCode + "#" + clientCode;
					boolean flgResult = setBillInfo.add(billInfo);

					if (flgResult)
					{
						String advBookingNo = mJsonObject.get("AdvBookingNo").toString();

						String settelmentMode = mJsonObject.get("SettelmentMode").toString();
						double discountAmt = Double.parseDouble(mJsonObject.get("DiscountAmt").toString());
						double discountPer = Double.parseDouble(mJsonObject.get("DiscountPer").toString());
						double taxAmt = Double.parseDouble(mJsonObject.get("TaxAmt").toString());
						double subTotal = Double.parseDouble(mJsonObject.get("SubTotal").toString());
						double grandTotal = Double.parseDouble(mJsonObject.get("GrandTotal").toString());
						String takeAway = mJsonObject.get("TakeAway").toString();
						String operationType = mJsonObject.get("OperationType").toString();
						String userCreated = mJsonObject.get("UserCreated").toString();
						String userEdited = mJsonObject.get("UserEdited").toString();
						String dateCreated = mJsonObject.get("DateCreated").toString();
						String dateEdited = mJsonObject.get("DateEdited").toString();
						String tableNo = mJsonObject.get("TableNo").toString();
						String waiterNo = mJsonObject.get("WaiterNo").toString();
						String customerCode = mJsonObject.get("CustomerCode").toString();
						String manualBillNo = mJsonObject.get("ManualBillNo").toString();
						int shiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
						int paxNo = Integer.parseInt(mJsonObject.get("PaxNo").toString());
						String reasonCode = mJsonObject.get("ReasonCode").toString();
						String remarks = mJsonObject.get("Remarks").toString();
						double tipAmount = Double.parseDouble(mJsonObject.get("TipAmount").toString());
						String settleDate = mJsonObject.get("SettleDate").toString();
						String counterCode = mJsonObject.get("CounterCode").toString();
						double deliveryCharges = Double.parseDouble(mJsonObject.get("DeliveryCharges").toString());
						String couponCode = mJsonObject.get("CouponCode").toString();
						String areaCode = mJsonObject.get("AreaCode").toString();
						String discRemark = mJsonObject.get("DiscountRemark").toString();
						String takeAwayRemark = mJsonObject.get("TakeAwayRemark").toString();
						String discountOn = mJsonObject.get("DiscountOn").toString();
						String cardNo = mJsonObject.get("CardNo").toString();
						String billTransType = mJsonObject.get("TransType").toString();

						String strJioMoneyRRefNo = mJsonObject.get("strJioMoneyRRefNo").toString();
						String strJioMoneyAuthCode = mJsonObject.get("strJioMoneyAuthCode").toString();
						String strJioMoneyTxnId = mJsonObject.get("strJioMoneyTxnId").toString();
						String strJioMoneyTxnDateTime = mJsonObject.get("strJioMoneyTxnDateTime").toString();
						String strJioMoneyCardNo = mJsonObject.get("strJioMoneyCardNo").toString();
						String strJioMoneyCardType = mJsonObject.get("strJioMoneyCardType").toString();
						String dblRoundOff = mJsonObject.get("dblRoundOff").toString();
						String intBillSeriesPaxNo = mJsonObject.get("intBillSeriesPaxNo").toString();
						String dtBillDate = mJsonObject.get("dtBillDate").toString();

						if (cnt == 0)
						{
							sbSql.append("('" + billNo + "','" + advBookingNo + "','" + billDate + "','" + POSCode + "','" + settelmentMode + "'," + "'" + discountAmt + "','" + discountPer + "','" + taxAmt + "','" + subTotal + "','" + grandTotal + "'," + "'" + takeAway + "','" + operationType + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "'," + "'" + dateEdited + "','" + clientCode + "','" + tableNo + "','" + waiterNo + "','" + customerCode + "'," + "'" + manualBillNo + "','" + shiftCode + "','" + paxNo + "','N','" + reasonCode + "'," + "'" + remarks + "','" + tipAmount + "','" + settleDate + "','" + counterCode + "','" + deliveryCharges + "'," + "'" + couponCode + "','" + areaCode + "','" + discRemark + "','" + takeAwayRemark + "'," + "'" + discountOn + "','" + cardNo + "','" + billTransType + "'"
									+ ",'" + strJioMoneyRRefNo + "','" + strJioMoneyAuthCode + "','" + strJioMoneyTxnId + "','" + strJioMoneyTxnDateTime + "','" + strJioMoneyCardNo + "','" + strJioMoneyCardType + "','" + dblRoundOff + "','" + intBillSeriesPaxNo + "','" + dtBillDate + "')");
						}
						else
						{
							sbSql.append(",('" + billNo + "','" + advBookingNo + "','" + billDate + "','" + POSCode + "','" + settelmentMode + "'," + "'" + discountAmt + "','" + discountPer + "','" + taxAmt + "','" + subTotal + "','" + grandTotal + "'," + "'" + takeAway + "','" + operationType + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "'," + "'" + dateEdited + "','" + clientCode + "','" + tableNo + "','" + waiterNo + "','" + customerCode + "'," + "'" + manualBillNo + "','" + shiftCode + "','" + paxNo + "','N','" + reasonCode + "'," + "'" + remarks + "','" + tipAmount + "','" + settleDate + "','" + counterCode + "','" + deliveryCharges + "'," + "'" + couponCode + "','" + areaCode + "','" + discRemark + "','" + takeAwayRemark + "'," + "'" + discountOn + "','" + cardNo + "','" + billTransType + "'"
									+ ",'" + strJioMoneyRRefNo + "','" + strJioMoneyAuthCode + "','" + strJioMoneyTxnId + "','" + strJioMoneyTxnDateTime + "','" + strJioMoneyCardNo + "','" + strJioMoneyCardType + "','" + dblRoundOff + "','" + intBillSeriesPaxNo + "','" + dtBillDate + "')");
						}
						cnt++;
						flgData = true;
					}
				}

				if (flgData)
				{
					/*
					 * sql=sql.substring(1,sql.length()); insert_qry+=" "+sql;
					 * res=st.executeUpdate(insert_qry);
					 */

					/*
					 * sbSqlDelete.setLength(0);
					 * sbSqlDelete.append("delete from "+tableName+" " +
					 * "where strBillNo in ("
					 * +appBillNo+") and strClientCode='"+clCode
					 * +"' and strPOSCode='"+pCode+"' ");
					 * System.out.println(sbSqlDelete); int
					 * del=st.executeUpdate(sbSql.toString());
					 */

					res = st.executeUpdate(sbSql.toString());
					tableUpdated += ",BillHd";
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblbilldtl Table
			if (null != objSalesData.get("BillDtlInfo"))
			{
				JSONArray jsonArrBillDtl = objSalesData.getJSONArray("BillDtlInfo");

				flgData = false;
				int cnt = 0;
				Set setBillDtl = new HashSet();
				sbSql.setLength(0);
				sbSql.append("INSERT INTO tblqbilldtl (`strItemCode`, `strItemName`," + " `strBillNo`, `strAdvBookingNo`, `dblRate`, `dblQuantity`, " + "`dblAmount`, `dblTaxAmount`, `dteBillDate`, `strKOTNo`, " + "`strClientCode`, `strCustomerCode`, `tmeOrderProcessing`, " + "`strDataPostFlag`, `strMMSDataPostFlag`, `strManualKOTNo`, " + "`tdhYN`, `strPromoCode`,`strCounterCode`,`strWaiterNo`,`dblDiscountAmt`, " + "`dblDiscountPer`"
						+ ",strSequenceNo,dtBillDate,tmeOrderPickup) VALUES");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrBillDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrBillDtl.get(i);

					String BillNo = mJsonObject.get("BillNo").toString().trim();
					String ClientCode = mJsonObject.get("ClientCode").toString().trim();
					String ItemCode = mJsonObject.get("ItemCode").toString().trim();
					String KOTNo = mJsonObject.get("KOTNo").toString().trim();
					String BillDate = mJsonObject.get("BillDate").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbilldtl " + " where strBillNo='" + BillNo + "' and strItemCode='" + ItemCode + "' and strKOTNo='" + KOTNo + "' " + " and strClientCode='" + ClientCode + "'  and date(dteBillDate)='" + BillDate + "' ");
					st.executeUpdate(sbSqlDelete.toString());
					if (KOTNo.isEmpty())
					{
						sbSqlDelete.setLength(0);
						sbSqlDelete.append("delete from tblqbilldtl " + " where strBillNo='" + BillNo + "' and strItemCode='" + ItemCode + "' and strClientCode='" + ClientCode + "'  and date(dteBillDate)='" + BillDate + "' ");
					}
					st.executeUpdate(sbSqlDelete.toString());

					String ItemName = mJsonObject.get("ItemName").toString();
					String AdvBookingNo = mJsonObject.get("AdvBookingNo").toString();
					double Rate = Double.parseDouble(mJsonObject.get("Rate").toString());
					double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
					double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
					double TaxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());

					String CustomerCode = mJsonObject.get("CustomerCode").toString();
					String OrderProcessing = mJsonObject.get("OrderProcessing").toString();
					String MMSDataPostFlag = mJsonObject.get("MMSDataPostFlag").toString();
					String ManualKOTNo = mJsonObject.get("ManualKOTNo").toString();
					String tdhYN = mJsonObject.get("tdhYN").toString();
					String promoCode = mJsonObject.get("PromoCode").toString();
					String counterCode = mJsonObject.get("CounterCode").toString();
					String waiterNo = mJsonObject.get("WaiterNo").toString();
					String discAmt = mJsonObject.get("DiscountAmt").toString();
					String discPer = mJsonObject.get("DiscountPer").toString();

					String strSequenceNo = mJsonObject.get("strSequenceNo").toString();
					String dtBillDate = mJsonObject.get("dtBillDate").toString();
					String tmeOrderPickup = mJsonObject.get("tmeOrderPickup").toString();

					String key = BillNo + "#" + ClientCode + "#" + ItemCode + "#" + KOTNo;
					boolean flgResult = setBillDtl.add(key);

					if (flgResult)
					{

						if (cnt == 0)
						{
							sbSql.append("('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "'"
									+ ",'" + strSequenceNo + "','" + dtBillDate + "','" + tmeOrderPickup + "')");
						}
						else
						{
							sbSql.append(",('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "'"
									+ ",'" + strSequenceNo + "','" + dtBillDate + "','" + tmeOrderPickup + "')");
						}
						cnt++;
						flgData = true;
					}
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
					tableUpdated += ",BillDtl";
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblqbillmodifierdtl Table
			if (null != objSalesData.get("BillModifierDtl"))
			{
				JSONArray jsonArrBillModDtl = objSalesData.getJSONArray("BillModifierDtl");

				flgData = false;
				int cnt = 0;
				sbSql.setLength(0);
				sbSql.append("INSERT INTO tblqbillmodifierdtl (`strBillNo`, `strItemCode`, " + "`strModifierCode`, `strModifierName`, `dblRate`, `dblQuantity`, " + "`dblAmount`, `strClientCode`, `strCustomerCode`, `strDataPostFlag`, " + "`strMMSDataPostFlag`,`strDefaultModifierDeselectedYN`,`strSequenceNo`," + "`dblDiscPer`,`dblDiscAmt`,dteBillDate)"
						+ " VALUES ");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrBillModDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrBillModDtl.get(i);
					String billNo = mJsonObject.get("BillNo").toString().trim();
					String clientCode = mJsonObject.get("ClientCode").toString().trim();
					String itemCode = mJsonObject.get("ItemCode").toString().trim();
					String modifierCode = mJsonObject.get("ModifierCode").toString().trim();
					String dteBillDate = mJsonObject.get("dteBillDate").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillmodifierdtl " + "where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "' and strModifierCode='" + modifierCode + "' " + " and strClientCode='" + clientCode + "' and date(dteBillDate)='" + dteBillDate + "' ");
					st.executeUpdate(sbSqlDelete.toString());

					String modifierName = mJsonObject.get("ModifierName").toString();
					double rate = Double.parseDouble(mJsonObject.get("Rate").toString());
					double quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
					double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
					String customerCode = mJsonObject.get("CustomerCode").toString();
					String MMSDataPostFlag = mJsonObject.get("MMSDataPostFlag").toString();
					String defaultModifierDeselectedYN = mJsonObject.get("DefaultModifierDeselectedYN").toString();
					String seqNo = mJsonObject.get("strSequenceNo").toString();
					String discPer = mJsonObject.get("dblDiscPer").toString();
					String discAmt = mJsonObject.get("dblDiscAmt").toString();

					if (cnt == 0)
					{
						sbSql.append("('" + billNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + rate + "','" + quantity + "','" + amount + "','" + clientCode + "','" + customerCode + "'" + ",'N','" + MMSDataPostFlag + "','" + defaultModifierDeselectedYN + "'" + ",'" + seqNo + "','" + discPer + "','" + discAmt + "','" + dteBillDate + "')");
					}
					else
					{
						sbSql.append(",('" + billNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + rate + "','" + quantity + "','" + amount + "','" + clientCode + "','" + customerCode + "'" + ",'N','" + MMSDataPostFlag + "','" + defaultModifierDeselectedYN + "'" + ",'" + seqNo + "','" + discPer + "','" + discAmt + "','" + dteBillDate + "')");
					}
					cnt++;
					flgData = true;
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
					tableUpdated += ",BillModDtl";
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblqbilldiscdtl Table
			if (null != objSalesData.get("BillDiscountDtl"))
			{
				JSONArray jsonArrBillDiscDtl = objSalesData.getJSONArray("BillDiscountDtl");
				flgData = false;
				int cnt = 0;
				sbSql.setLength(0);
				sbSql.append("INSERT INTO tblqbilldiscdtl (`strBillNo`, `strPOSCode`," + " `dblDiscAmt`, `dblDiscPer`, `dblDiscOnAmt`, `strDiscOnType`," + " `strDiscOnValue`, `strDiscReasonCode`, `strDiscRemarks`, `strUserCreated`, " + " `strUserEdited`, `dteDateCreated`, `dteDateEdited`,`strClientCode`,dteBillDate)"
						+ " VALUES");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrBillDiscDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrBillDiscDtl.get(i);

					String billNo = mJsonObject.get("BillNo").toString().trim();
					String clientCode = mJsonObject.get("ClientCode").toString().trim();
					String dteBillDate = mJsonObject.get("dteBillDate").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbilldiscdtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "'  and date(dteBillDate)='" + dteBillDate + "'  ");
					st.executeUpdate(sbSqlDelete.toString());

					String POSCode = mJsonObject.get("POSCode").toString();
					String discAmt = mJsonObject.get("DiscAmt").toString();
					String discPer = mJsonObject.get("DiscPer").toString();
					String discOnAmt = mJsonObject.get("DiscOnAmt").toString();
					String discOnType = mJsonObject.get("DiscOnType").toString();
					String discOnValue = mJsonObject.get("DiscOnValue").toString();
					String discOnReasonCode = mJsonObject.get("DiscOnReasonCode").toString();
					String discRemarks = mJsonObject.get("DiscRemarks").toString();
					String userCreated = mJsonObject.get("UserCreated").toString();
					String userEdited = mJsonObject.get("UserEdited").toString();
					String dateCreated = mJsonObject.get("DateCreated").toString();
					String dateEdited = mJsonObject.get("DateEdited").toString();

					if (cnt == 0)
					{
						sbSql.append("('" + billNo + "','" + POSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + discOnReasonCode + "','" + discRemarks + "','" + userCreated + "'" + ",'" + userEdited + "','" + dateCreated + "','" + dateEdited + "','" + clientCode + "','" + dteBillDate + "')");
					}
					else
					{
						sbSql.append(",('" + billNo + "','" + POSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + discOnReasonCode + "','" + discRemarks + "','" + userCreated + "'" + ",'" + userEdited + "','" + dateCreated + "','" + dateEdited + "','" + clientCode + "','" + dteBillDate + "')");
					}
					cnt++;
					flgData = true;
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
					tableUpdated += ",BillDiscDtl";
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblqbilltaxdtl Table
			if (null != objSalesData.get("BillTaxDtl"))
			{
				Set<String> setBillTaxDtl = new HashSet<String>();
				JSONArray jsonArrBillTaxDtl = objSalesData.getJSONArray("BillTaxDtl");
				flgData = false;
				int cnt = 0;
				sbSql.setLength(0);
				sbSql.append("INSERT INTO tblqbilltaxdtl (`strBillNo`, `strTaxCode`," + " `dblTaxableAmount`, `dblTaxAmount`, `strClientCode`, " + "`strDataPostFlag`,dteBillDate)"
						+ " VALUES");
				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrBillTaxDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrBillTaxDtl.get(i);
					String billNo = mJsonObject.get("BillNo").toString().trim();
					String clientCode = mJsonObject.get("ClientCode").toString().trim();
					String taxCode = mJsonObject.get("TaxCode").toString().trim();
					String dteBillDate = mJsonObject.get("dteBillDate").toString().trim();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbilltaxdtl " + "where strBillNo='" + billNo + "' and strTaxCode='" + taxCode + "' and strClientCode='" + clientCode + "'  and date(dteBillDate)='" + dteBillDate + "' ");
					st.executeUpdate(sbSqlDelete.toString());

					double taxableAmount = Double.parseDouble(mJsonObject.get("TaxableAmount").toString());
					double taxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
					String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

					String key = billNo + "#" + taxCode + "#" + clientCode;
					boolean flgResult = setBillTaxDtl.add(key);

					if (flgResult)
					{
						if (cnt == 0)
						{
							sbSql.append("('" + billNo + "','" + taxCode + "','" + taxableAmount + "','" + taxAmount + "','" + clientCode + "','" + dataPostFlag + "','" + dteBillDate + "')");
						}
						else
						{
							sbSql.append(",('" + billNo + "','" + taxCode + "','" + taxableAmount + "','" + taxAmount + "','" + clientCode + "','" + dataPostFlag + "','" + dteBillDate + "')");
						}
						cnt++;
						flgData = true;
					}
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
					tableUpdated += ",BillTaxDtl";
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblqbillcomplimentrydtl Table
			if (null != objSalesData.get("BillComplimentryDtl"))
			{
				JSONArray jsonArrBillComplDtl = objSalesData.getJSONArray("BillComplimentryDtl");
				flgData = false;
				int cnt = 0;
				sbSql.setLength(0);
				sbSql.append("INSERT INTO tblqbillcomplementrydtl (`strItemCode`, `strItemName`," + " `strBillNo`, `strAdvBookingNo`, `dblRate`, `dblQuantity`, " + "`dblAmount`, `dblTaxAmount`, `dteBillDate`, `strKOTNo`, " + "`strClientCode`, `strCustomerCode`, `tmeOrderProcessing`, " + "`strDataPostFlag`, `strMMSDataPostFlag`, `strManualKOTNo`, " + "`tdhYN`, `strPromoCode`,`strCounterCode`,`strWaiterNo`,`dblDiscountAmt`, " + "`dblDiscountPer`"
						+ ",strSequenceNo,strType,dtBillDate,tmeOrderPickup) VALUES");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrBillComplDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrBillComplDtl.get(i);

					String BillNo = mJsonObject.get("BillNo").toString().trim();
					String ClientCode = mJsonObject.get("ClientCode").toString().trim();
					String ItemCode = mJsonObject.get("ItemCode").toString().trim();
					String KOTNo = mJsonObject.get("KOTNo").toString().trim();
					String BillDate = mJsonObject.get("BillDate").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillcomplementrydtl " + " where strBillNo='" + BillNo + "' and strItemCode='" + ItemCode + "' and strKOTNo='" + KOTNo + "' " + " and strClientCode='" + ClientCode + "' and date(dteBillDate)='" + BillDate + "' ");
					st.executeUpdate(sbSqlDelete.toString());

					String ItemName = mJsonObject.get("ItemName").toString();
					String AdvBookingNo = mJsonObject.get("AdvBookingNo").toString();
					double Rate = Double.parseDouble(mJsonObject.get("Rate").toString());
					double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
					double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
					double TaxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());

					String CustomerCode = mJsonObject.get("CustomerCode").toString();
					String OrderProcessing = mJsonObject.get("OrderProcessing").toString();
					String MMSDataPostFlag = mJsonObject.get("MMSDataPostFlag").toString();
					String ManualKOTNo = mJsonObject.get("ManualKOTNo").toString();
					String tdhYN = mJsonObject.get("tdhYN").toString();
					String promoCode = mJsonObject.get("PromoCode").toString();
					String counterCode = mJsonObject.get("CounterCode").toString();
					String waiterNo = mJsonObject.get("WaiterNo").toString();
					String discAmt = mJsonObject.get("DiscountAmt").toString();
					String discPer = mJsonObject.get("DiscountPer").toString();

					String strSequenceNo = mJsonObject.get("strSequenceNo").toString();
					String strType = mJsonObject.get("strType").toString();
					String dtBillDate = mJsonObject.get("dtBillDate").toString();
					String tmeOrderPickup = mJsonObject.get("tmeOrderPickup").toString();

					// System.out.println(BillDate+" = "+BillNo);
					if (cnt == 0)
					{
						sbSql.append("('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "'"
								+ ",'" + strSequenceNo + "','" + strType + "','" + dtBillDate + "','" + tmeOrderPickup + "')");
					}
					else
					{
						sbSql.append(",('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "'"
								+ ",'" + strSequenceNo + "','" + strType + "','" + dtBillDate + "','" + tmeOrderPickup + "')");
					}
					cnt++;
					flgData = true;
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
					tableUpdated += ",BillComplDtl";
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblqbillsettlementdtl Table
			if (null != objSalesData.get("BillSettlementDtl"))
			{
				Set<String> setBillSettlementDtl = new HashSet<String>();
				JSONArray jsonArrBillSettleDtl = objSalesData.getJSONArray("BillSettlementDtl");
				int cnt = 0;

				sbSql.setLength(0);
				sbSql.append("INSERT INTO tblqbillsettlementdtl (`strBillNo`, `strSettlementCode`," + " `dblSettlementAmt`, `dblPaidAmt`, `strExpiryDate`, `strCardName`," + " `strRemark`, `strClientCode`, `strCustomerCode`, `dblActualAmt`, " + "`dblRefundAmt`, `strGiftVoucherCode`, `strDataPostFlag`,`strFolioNo`,`strRoomNo`,dteBillDate) VALUES");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrBillSettleDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrBillSettleDtl.get(i);

					String BillNo = mJsonObject.get("BillNo").toString().trim();
					String ClientCode = mJsonObject.get("ClientCode").toString().trim();
					String SettlementCode = mJsonObject.get("SettlementCode").toString().trim();
					String dteBillDate = mJsonObject.get("dteBillDate").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("  delete from tblqbillsettlementdtl " + "where strBillNo='" + BillNo + "' and strClientCode='" + ClientCode + "' and date(dteBillDate)='" + dteBillDate + "' ");
					st.executeUpdate(sbSqlDelete.toString());

					double SettlementAmt = Double.parseDouble(mJsonObject.get("SettlementAmt").toString());
					double PaidAmt = Double.parseDouble(mJsonObject.get("PaidAmt").toString());
					String ExpiryDate = mJsonObject.get("ExpiryDate").toString();
					String CardName = mJsonObject.get("CardName").toString();
					String Remark = mJsonObject.get("Remark").toString();
					String CustomerCode = mJsonObject.get("CustomerCode").toString();
					double ActualAmt = Double.parseDouble(mJsonObject.get("ActualAmt").toString());
					double RefundAmt = Double.parseDouble(mJsonObject.get("RefundAmt").toString());
					String GiftVoucherCode = mJsonObject.get("GiftVoucherCode").toString();
					String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
					String folioNo = mJsonObject.get("FolioNo").toString();
					String roomNo = mJsonObject.get("RoomNo").toString();

					String key = BillNo + "#" + ClientCode + "#" + SettlementCode;
					boolean flgResult = setBillSettlementDtl.add(key);

					if (flgResult)
					{
						if (cnt == 0)
						{
							sbSql.append("('" + BillNo + "','" + SettlementCode + "','" + SettlementAmt + "','" + PaidAmt + "','" + ExpiryDate + "','" + CardName + "','" + Remark + "','" + ClientCode + "','" + CustomerCode + "','" + ActualAmt + "','" + RefundAmt + "'" + ",'" + GiftVoucherCode + "','" + DataPostFlag + "','" + folioNo + "','" + roomNo + "','" + dteBillDate + "')");
						}
						else
						{
							sbSql.append(",('" + BillNo + "','" + SettlementCode + "','" + SettlementAmt + "','" + PaidAmt + "','" + ExpiryDate + "','" + CardName + "','" + Remark + "','" + ClientCode + "','" + CustomerCode + "','" + ActualAmt + "','" + RefundAmt + "'" + ",'" + GiftVoucherCode + "','" + DataPostFlag + "','" + folioNo + "','" + roomNo + "','" + dteBillDate + "')");
						}
						cnt++;
						flgData = true;
					}
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
					tableUpdated += ",BillSettleDtl";
				}
				else
				{
					res = 1;
				}
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return tableUpdated;
		}
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPostPlaceOrderDataToHOPOS")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostPlaceOrderToHOPOS(JSONObject objBillData)
	{
		String response = "false";

		if (funInsertPlaceOrderData(objBillData) > 0)
		{
			response = "true";
		}
		else
		{
			response = "false";
		}
		return Response.status(201).entity(response).build();
	}

	@SuppressWarnings("finally")
	private int funInsertBillHdData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		Map<String, String> hmPOSMaster = new HashMap<String, String>();
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		Set<String> setBillInfo = new HashSet<String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			// st1 = cmsCon.createStatement();

			String sqlPOSCode = "select strPOSCode,strPropertyPOSCode from tblposmaster";
			ResultSet rsPOS = st.executeQuery(sqlPOSCode);
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();
			int cnt = 0;

			sbSql.append("INSERT INTO " + tableName + " (`strBillNo`,`strAdvBookingNo`, `dteBillDate`, `strPOSCode`" + ",`strSettelmentMode`, `dblDiscountAmt`,`dblDiscountPer`, `dblTaxAmt`, `dblSubTotal`,`dblGrandTotal`" + ", `strTakeAway`, `strOperationType`,`strUserCreated`, `strUserEdited`, `dteDateCreated`,`dteDateEdited`" + ", `strClientCode`, `strTableNo`,`strWaiterNo`, `strCustomerCode`, `strManualBillNo`,`intShiftCode`" + ", `intPaxNo`, `strDataPostFlag`,`strReasonCode`, `strRemarks`, `dblTipAmount`,`dteSettleDate`" + ", `strCounterCode`, `dblDeliveryCharges`,`strCouponCode`,`strAreaCode`,`strDiscountRemark`" + ",`strTakeAwayRemarks`,`strDiscountOn`,`strCardNo`,`strTransactionType`"
					+ ",strJioMoneyRRefNo,strJioMoneyAuthCode,strJioMoneyTxnId,strJioMoneyTxnDateTime,strJioMoneyCardNo,strJioMoneyCardType) " + "VALUES");
			JSONObject mJsonObject = new JSONObject();

			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String POSCode = mJsonObject.get("POSCode").toString().trim();

				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "' and strPOSCode='" + POSCode + "' ");
				st.executeUpdate(sbSqlDelete.toString());

				String billInfo = billNo + "#" + POSCode + "#" + clientCode;
				boolean flgResult = setBillInfo.add(billInfo);

				if (flgResult)
				{
					String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
					String billDate = mJsonObject.get("BillDate").toString();

					String settelmentMode = mJsonObject.get("SettelmentMode").toString();
					double discountAmt = Double.parseDouble(mJsonObject.get("DiscountAmt").toString());
					double discountPer = Double.parseDouble(mJsonObject.get("DiscountPer").toString());
					double taxAmt = Double.parseDouble(mJsonObject.get("TaxAmt").toString());
					double subTotal = Double.parseDouble(mJsonObject.get("SubTotal").toString());
					double grandTotal = Double.parseDouble(mJsonObject.get("GrandTotal").toString());
					String takeAway = mJsonObject.get("TakeAway").toString();
					String operationType = mJsonObject.get("OperationType").toString();
					String userCreated = mJsonObject.get("UserCreated").toString();
					String userEdited = mJsonObject.get("UserEdited").toString();
					String dateCreated = mJsonObject.get("DateCreated").toString();
					String dateEdited = mJsonObject.get("DateEdited").toString();
					String tableNo = mJsonObject.get("TableNo").toString();
					String waiterNo = mJsonObject.get("WaiterNo").toString();
					String customerCode = mJsonObject.get("CustomerCode").toString();
					String manualBillNo = mJsonObject.get("ManualBillNo").toString();
					int shiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
					int paxNo = Integer.parseInt(mJsonObject.get("PaxNo").toString());
					String reasonCode = mJsonObject.get("ReasonCode").toString();
					String remarks = mJsonObject.get("Remarks").toString();
					double tipAmount = Double.parseDouble(mJsonObject.get("TipAmount").toString());
					String settleDate = mJsonObject.get("SettleDate").toString();
					String counterCode = mJsonObject.get("CounterCode").toString();
					double deliveryCharges = Double.parseDouble(mJsonObject.get("DeliveryCharges").toString());
					String couponCode = mJsonObject.get("CouponCode").toString();
					String areaCode = mJsonObject.get("AreaCode").toString();
					String discRemark = mJsonObject.get("DiscountRemark").toString();
					String takeAwayRemark = mJsonObject.get("TakeAwayRemark").toString();
					String discountOn = mJsonObject.get("DiscountOn").toString();
					String cardNo = mJsonObject.get("CardNo").toString();
					String billTransType = mJsonObject.get("TransType").toString();

					String strJioMoneyRRefNo = mJsonObject.get("strJioMoneyRRefNo").toString();
					String strJioMoneyAuthCode = mJsonObject.get("strJioMoneyAuthCode").toString();
					String strJioMoneyTxnId = mJsonObject.get("strJioMoneyTxnId").toString();
					String strJioMoneyTxnDateTime = mJsonObject.get("strJioMoneyTxnDateTime").toString();
					String strJioMoneyCardNo = mJsonObject.get("strJioMoneyCardNo").toString();
					String strJioMoneyCardType = mJsonObject.get("strJioMoneyCardType").toString();

					if (cnt == 0)
					{
						sbSql.append("('" + billNo + "','" + advBookingNo + "','" + billDate + "','" + POSCode + "','" + settelmentMode + "'," + "'" + discountAmt + "','" + discountPer + "','" + taxAmt + "','" + subTotal + "','" + grandTotal + "'," + "'" + takeAway + "','" + operationType + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "'," + "'" + dateEdited + "','" + clientCode + "','" + tableNo + "','" + waiterNo + "','" + customerCode + "'," + "'" + manualBillNo + "','" + shiftCode + "','" + paxNo + "','N','" + reasonCode + "'," + "'" + remarks + "','" + tipAmount + "','" + settleDate + "','" + counterCode + "','" + deliveryCharges + "'," + "'" + couponCode + "','" + areaCode + "','" + discRemark + "','" + takeAwayRemark + "'," + "'" + discountOn + "','" + cardNo + "','" + billTransType + "'"
								+ ",'" + strJioMoneyRRefNo + "','" + strJioMoneyAuthCode + "','" + strJioMoneyTxnId + "','" + strJioMoneyTxnDateTime + "','" + strJioMoneyCardNo + "','" + strJioMoneyCardType + "')");
					}
					else
					{
						sbSql.append(",('" + billNo + "','" + advBookingNo + "','" + billDate + "','" + POSCode + "','" + settelmentMode + "'," + "'" + discountAmt + "','" + discountPer + "','" + taxAmt + "','" + subTotal + "','" + grandTotal + "'," + "'" + takeAway + "','" + operationType + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "'," + "'" + dateEdited + "','" + clientCode + "','" + tableNo + "','" + waiterNo + "','" + customerCode + "'," + "'" + manualBillNo + "','" + shiftCode + "','" + paxNo + "','N','" + reasonCode + "'," + "'" + remarks + "','" + tipAmount + "','" + settleDate + "','" + counterCode + "','" + deliveryCharges + "'," + "'" + couponCode + "','" + areaCode + "','" + discRemark + "','" + takeAwayRemark + "'," + "'" + discountOn + "','" + cardNo + "','" + billTransType + "'"
								+ ",'" + strJioMoneyRRefNo + "','" + strJioMoneyAuthCode + "','" + strJioMoneyTxnId + "','" + strJioMoneyTxnDateTime + "','" + strJioMoneyCardNo + "','" + strJioMoneyCardType + "')");
					}
					cnt++;
					flgData = true;
				}
			}

			if (flgData)
			{
				/*
				 * sql=sql.substring(1,sql.length()); insert_qry+=" "+sql;
				 * res=st.executeUpdate(insert_qry);
				 */

				/*
				 * sbSqlDelete.setLength(0);
				 * sbSqlDelete.append("delete from "+tableName+" " +
				 * "where strBillNo in ("
				 * +appBillNo+") and strClientCode='"+clCode
				 * +"' and strPOSCode='"+pCode+"' ");
				 * System.out.println(sbSqlDelete); int
				 * del=st.executeUpdate(sbSql.toString());
				 */

				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				// st1.close();

				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertBillDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql;
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			Set setBillDtl = new HashSet();
			int cnt = 0;

			sbSql.append("INSERT INTO " + tableName + " (`strItemCode`, `strItemName`," + " `strBillNo`, `strAdvBookingNo`, `dblRate`, `dblQuantity`, " + "`dblAmount`, `dblTaxAmount`, `dteBillDate`, `strKOTNo`, " + "`strClientCode`, `strCustomerCode`, `tmeOrderProcessing`, " + "`strDataPostFlag`, `strMMSDataPostFlag`, `strManualKOTNo`, " + "`tdhYN`, `strPromoCode`,`strCounterCode`,`strWaiterNo`,`dblDiscountAmt`, " + "`dblDiscountPer`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String BillNo = mJsonObject.get("BillNo").toString().trim();
				String ClientCode = mJsonObject.get("ClientCode").toString().trim();
				String ItemCode = mJsonObject.get("ItemCode").toString().trim();
				String KOTNo = mJsonObject.get("KOTNo").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + " where strBillNo='" + BillNo + "' and strItemCode='" + ItemCode + "' and strKOTNo='" + KOTNo + "' " + " and strClientCode='" + ClientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());
				if (KOTNo.isEmpty())
				{
					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbilldtl " + " where strBillNo='" + BillNo + "' and strItemCode='" + ItemCode + "' and strClientCode='" + ClientCode + "'");
				}
				st.executeUpdate(sbSqlDelete.toString());

				String ItemName = mJsonObject.get("ItemName").toString();
				String AdvBookingNo = mJsonObject.get("AdvBookingNo").toString();
				double Rate = Double.parseDouble(mJsonObject.get("Rate").toString());
				double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double TaxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String BillDate = mJsonObject.get("BillDate").toString();
				String CustomerCode = mJsonObject.get("CustomerCode").toString();
				String OrderProcessing = mJsonObject.get("OrderProcessing").toString();
				String MMSDataPostFlag = mJsonObject.get("MMSDataPostFlag").toString();
				String ManualKOTNo = mJsonObject.get("ManualKOTNo").toString();
				String tdhYN = mJsonObject.get("tdhYN").toString();
				String promoCode = mJsonObject.get("PromoCode").toString();
				String counterCode = mJsonObject.get("CounterCode").toString();
				String waiterNo = mJsonObject.get("WaiterNo").toString();
				String discAmt = mJsonObject.get("DiscountAmt").toString();
				String discPer = mJsonObject.get("DiscountPer").toString();

				String key = BillNo + "#" + ClientCode + "#" + ItemCode + "#" + KOTNo;
				boolean flgResult = setBillDtl.add(key);

				if (flgResult)
				{

					if (cnt == 0)
					{
						sbSql.append("('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "')");
					}
					else
					{
						sbSql.append(",('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "')");
					}
					cnt++;
					flgData = true;
				}
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertBillSeriesDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;

		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			int cnt = 0;

			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			String sqlPOSCode = "select strPOSCode,strPropertyPOSCode from tblposmaster";
			ResultSet rsPOS = st.executeQuery(sqlPOSCode);
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			sbSql.append("INSERT INTO " + tableName + " (`strItemCode`, `strItemName`," + " `strBillNo`, `strAdvBookingNo`, `dblRate`, `dblQuantity`, " + "`dblAmount`, `dblTaxAmount`, `dteBillDate`, `strKOTNo`, " + "`strClientCode`, `strCustomerCode`, `tmeOrderProcessing`, " + "`strDataPostFlag`, `strMMSDataPostFlag`, `strManualKOTNo`, " + "`tdhYN`, `strPromoCode`,`strCounterCode`,`strWaiterNo`,`dblDiscountAmt`, " + "`dblDiscountPer`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String billNo = mJsonObject.get("strHdBillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String POSCode = mJsonObject.get("POSCode").toString().trim();

				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + " where strHdBillNo='" + billNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String billSeries = mJsonObject.get("BillSeries").toString().trim();
				String dtlBillNo = mJsonObject.get("DtlBillNo").toString().trim();
				double grandTotal = Double.parseDouble(mJsonObject.get("GrandTotal").toString().trim());
				String userCreated = mJsonObject.get("UserCreated").toString().trim();
				String createdDate = mJsonObject.get("CreatedDate").toString().trim();
				String userEdited = mJsonObject.get("UserEdited").toString().trim();
				String editedDate = mJsonObject.get("EditedDate").toString().trim();

				if (cnt == 0)
				{
					sbSql.append("('" + POSCode + "','" + billSeries + "','" + billNo + "','" + dtlBillNo + "','" + grandTotal + "'," + "'" + clientCode + "','N','" + userCreated + "','" + createdDate + "'," + "'" + userEdited + "','" + editedDate + "')");
				}
				else
				{
					sbSql.append(",('" + POSCode + "','" + billSeries + "','" + billNo + "','" + dtlBillNo + "','" + grandTotal + "'," + "'" + clientCode + "','N','" + userCreated + "','" + createdDate + "'," + "'" + userEdited + "','" + editedDate + "')");
				}
				flgData = true;
				cnt++;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertBillComplimentryDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql;
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO " + tableName + " (`strItemCode`, `strItemName`," + " `strBillNo`, `strAdvBookingNo`, `dblRate`, `dblQuantity`, " + "`dblAmount`, `dblTaxAmount`, `dteBillDate`, `strKOTNo`, " + "`strClientCode`, `strCustomerCode`, `tmeOrderProcessing`, " + "`strDataPostFlag`, `strMMSDataPostFlag`, `strManualKOTNo`, " + "`tdhYN`, `strPromoCode`,`strCounterCode`,`strWaiterNo`,`dblDiscountAmt`, " + "`dblDiscountPer`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String BillNo = mJsonObject.get("BillNo").toString().trim();
				String ClientCode = mJsonObject.get("ClientCode").toString().trim();
				String ItemCode = mJsonObject.get("ItemCode").toString().trim();
				String KOTNo = mJsonObject.get("KOTNo").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + " where strBillNo='" + BillNo + "' and strItemCode='" + ItemCode + "' and strKOTNo='" + KOTNo + "' " + " and strClientCode='" + ClientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String ItemName = mJsonObject.get("ItemName").toString();
				String AdvBookingNo = mJsonObject.get("AdvBookingNo").toString();
				double Rate = Double.parseDouble(mJsonObject.get("Rate").toString());
				double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double TaxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String BillDate = mJsonObject.get("BillDate").toString();
				String CustomerCode = mJsonObject.get("CustomerCode").toString();
				String OrderProcessing = mJsonObject.get("OrderProcessing").toString();
				String MMSDataPostFlag = mJsonObject.get("MMSDataPostFlag").toString();
				String ManualKOTNo = mJsonObject.get("ManualKOTNo").toString();
				String tdhYN = mJsonObject.get("tdhYN").toString();
				String promoCode = mJsonObject.get("PromoCode").toString();
				String counterCode = mJsonObject.get("CounterCode").toString();
				String waiterNo = mJsonObject.get("WaiterNo").toString();
				String discAmt = mJsonObject.get("DiscountAmt").toString();
				String discPer = mJsonObject.get("DiscountPer").toString();

				// System.out.println(BillDate+" = "+BillNo);
				if (cnt == 0)
				{
					sbSql.append("('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "')");
				}
				else
				{
					sbSql.append(",('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "'," + "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo + "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'" + MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'" + counterCode + "','" + waiterNo + "','" + discAmt + "','" + discPer + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{

				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertBillModifierDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			int cnt = 0;

			sbSql.append("INSERT INTO " + tableName + " (`strBillNo`, `strItemCode`, " + "`strModifierCode`, `strModifierName`, `dblRate`, `dblQuantity`, " + "`dblAmount`, `strClientCode`, `strCustomerCode`, `strDataPostFlag`, " + "`strMMSDataPostFlag`,`strDefaultModifierDeselectedYN`,`strSequenceNo`," + "`dblDiscPer`,`dblDiscAmt`,dteBillDate) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String itemCode = mJsonObject.get("ItemCode").toString().trim();
				String modifierCode = mJsonObject.get("ModifierCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + "where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "' and strModifierCode='" + modifierCode + "' " + " and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String modifierName = mJsonObject.get("ModifierName").toString();
				double rate = Double.parseDouble(mJsonObject.get("Rate").toString());
				double quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String customerCode = mJsonObject.get("CustomerCode").toString();
				String MMSDataPostFlag = mJsonObject.get("MMSDataPostFlag").toString();
				String defaultModifierDeselectedYN = mJsonObject.get("DefaultModifierDeselectedYN").toString();
				String seqNo = mJsonObject.get("strSequenceNo").toString();
				String discPer = mJsonObject.get("dblDiscPer").toString();
				String discAmt = mJsonObject.get("dblDiscAmt").toString();
				String dteBillDate = mJsonObject.get("dteBillDate").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + billNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + rate + "','" + quantity + "','" + amount + "','" + clientCode + "','" + customerCode + "'" + ",'N','" + MMSDataPostFlag + "','" + defaultModifierDeselectedYN + "'" + ",'" + seqNo + "','" + discPer + "','" + discAmt + "','" + dteBillDate + "')");
				}
				else
				{
					sbSql.append(",('" + billNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + rate + "','" + quantity + "','" + amount + "','" + clientCode + "','" + customerCode + "'" + ",'N','" + MMSDataPostFlag + "','" + defaultModifierDeselectedYN + "'" + ",'" + seqNo + "','" + discPer + "','" + discAmt + "','" + dteBillDate + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertBillDiscountDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			int cnt = 0;

			sbSql.append("INSERT INTO " + tableName + " (`strBillNo`, `strPOSCode`," + " `dblDiscAmt`, `dblDiscPer`, `dblDiscOnAmt`, `strDiscOnType`," + " `strDiscOnValue`, `strDiscReasonCode`, `strDiscRemarks`, `strUserCreated`, " + " `strUserEdited`, `dteDateCreated`, `dteDateEdited`,`strClientCode`,dteBillDate)"
					+ " VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String POSCode = mJsonObject.get("POSCode").toString();
				String discAmt = mJsonObject.get("DiscAmt").toString();
				String discPer = mJsonObject.get("DiscPer").toString();
				String discOnAmt = mJsonObject.get("DiscOnAmt").toString();
				String discOnType = mJsonObject.get("DiscOnType").toString();
				String discOnValue = mJsonObject.get("DiscOnValue").toString();
				String discOnReasonCode = mJsonObject.get("DiscOnReasonCode").toString();
				String discRemarks = mJsonObject.get("DiscRemarks").toString();
				String userCreated = mJsonObject.get("UserCreated").toString();
				String userEdited = mJsonObject.get("UserEdited").toString();
				String dateCreated = mJsonObject.get("DateCreated").toString();
				String dateEdited = mJsonObject.get("DateEdited").toString();
				String dteBillDate = mJsonObject.get("dteBillDate").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + billNo + "','" + POSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + discOnReasonCode + "','" + discRemarks + "','" + userCreated + "'" + ",'" + userEdited + "','" + dateCreated + "','" + dateEdited + "','" + clientCode + "','" + dteBillDate + "')");
				}
				else
				{
					sbSql.append(",('" + billNo + "','" + POSCode + "','" + discAmt + "','" + discPer + "','" + discOnAmt + "','" + discOnType + "','" + discOnValue + "','" + discOnReasonCode + "','" + discRemarks + "','" + userCreated + "'" + ",'" + userEdited + "','" + dateCreated + "','" + dateEdited + "','" + clientCode + "','" + dteBillDate + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	public int funInsertBillPromotionDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO " + tableName + " " + "(`strBillNo`, `strItemCode`, `strPromotionCode`, `dblQuantity`, `dblRate`, `strClientCode`,`strDataPostFlag`" + ",`strPromoType`,`dblAmount`,`dblDiscountPer`,`dblDiscountAmt`,dteBillDate) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String itemCode = mJsonObject.get("ItemCode").toString().trim();
				String promotionCode = mJsonObject.get("PromotionCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + " where strBillNo='" + billNo + "' and strItemCode='" + itemCode + "' and strPromotionCode='" + promotionCode + "' " + " and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				double quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double rate = Double.parseDouble(mJsonObject.get("Rate").toString());
				String promotionType = mJsonObject.get("PromoType").toString();
				double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double discPer = Double.parseDouble(mJsonObject.get("DiscPer").toString());
				double discAmt = Double.parseDouble(mJsonObject.get("DiscAmount").toString());
				String dteBillDate = mJsonObject.get("dteBillDate").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + billNo + "','" + itemCode + "','" + promotionCode + "','" + quantity + "','" + rate + "'" + ",'" + clientCode + "','N','" + promotionType + "','" + amount + "','" + discPer + "','" + discAmt + "','" + dteBillDate + "')");
				}
				else
				{
					sbSql.append(",('" + billNo + "','" + itemCode + "','" + promotionCode + "','" + quantity + "','" + rate + "'" + ",'" + clientCode + "','N','" + promotionType + "','" + amount + "','" + discPer + "','" + discAmt + "','" + dteBillDate + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertCRMPointsData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insertQuery="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblcrmpoints` (`strBillNo`, `dblPoints`,`strTransactionId`" + ", `strOutletUID`, `dblRedeemedAmt`, `longCustMobileNo`,`strClientCode`" + ",`strDataPostFlag`,`dblValue`,`strCustomerId`,`dteBillDate`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblcrmpoints " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String crmPoints = mJsonObject.get("CRMPoints").toString();
				String transId = mJsonObject.get("TransactionId").toString();
				String outletUId = mJsonObject.get("OutletUID").toString();
				String redeemedAmt = mJsonObject.get("RedeemedAmt").toString();
				String custMobileNo = mJsonObject.get("CustomerMobileNo").toString();
				String value = mJsonObject.get("Value").toString();
				String custId = mJsonObject.get("CustomerId").toString();
				String billDate = mJsonObject.get("BillDate").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + billNo + "','" + crmPoints + "','" + transId + "','" + outletUId + "','" + redeemedAmt + "'" + ",'" + custMobileNo + "','" + clientCode + "','N','" + value + "','" + custId + "','" + billDate + "')");
				}
				else
				{
					sbSql.append(",('" + billNo + "','" + crmPoints + "','" + transId + "','" + outletUId + "','" + redeemedAmt + "'" + ",'" + custMobileNo + "','" + clientCode + "','N','" + value + "','" + custId + "','" + billDate + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertBillSettlementDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		Set<String> setBillSettlementDtl = new HashSet<String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO " + tableName + " (`strBillNo`, `strSettlementCode`," + " `dblSettlementAmt`, `dblPaidAmt`, `strExpiryDate`, `strCardName`," + " `strRemark`, `strClientCode`, `strCustomerCode`, `dblActualAmt`, " + "`dblRefundAmt`, `strGiftVoucherCode`, `strDataPostFlag`,`strFolioNo`,`strRoomNo`,dteBillDate) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String BillNo = mJsonObject.get("BillNo").toString().trim();
				String ClientCode = mJsonObject.get("ClientCode").toString().trim();
				String SettlementCode = mJsonObject.get("SettlementCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("  delete from " + tableName + " " + "where strBillNo='" + BillNo + "' and strClientCode='" + ClientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				double SettlementAmt = Double.parseDouble(mJsonObject.get("SettlementAmt").toString());
				double PaidAmt = Double.parseDouble(mJsonObject.get("PaidAmt").toString());
				String ExpiryDate = mJsonObject.get("ExpiryDate").toString();
				String CardName = mJsonObject.get("CardName").toString();
				String Remark = mJsonObject.get("Remark").toString();
				String CustomerCode = mJsonObject.get("CustomerCode").toString();
				double ActualAmt = Double.parseDouble(mJsonObject.get("ActualAmt").toString());
				double RefundAmt = Double.parseDouble(mJsonObject.get("RefundAmt").toString());
				String GiftVoucherCode = mJsonObject.get("GiftVoucherCode").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String folioNo = mJsonObject.get("FolioNo").toString();
				String roomNo = mJsonObject.get("RoomNo").toString();
				String dteBillDate = mJsonObject.get("dteBillDate").toString();

				String key = BillNo + "#" + ClientCode + "#" + SettlementCode;
				boolean flgResult = setBillSettlementDtl.add(key);

				if (flgResult)
				{
					if (cnt == 0)
					{
						sbSql.append("('" + BillNo + "','" + SettlementCode + "','" + SettlementAmt + "','" + PaidAmt + "','" + ExpiryDate + "','" + CardName + "','" + Remark + "','" + ClientCode + "','" + CustomerCode + "','" + ActualAmt + "','" + RefundAmt + "'" + ",'" + GiftVoucherCode + "','" + DataPostFlag + "','" + folioNo + "','" + roomNo + "','" + dteBillDate + "')");
					}
					else
					{
						sbSql.append(",('" + BillNo + "','" + SettlementCode + "','" + SettlementAmt + "','" + PaidAmt + "','" + ExpiryDate + "','" + CardName + "','" + Remark + "','" + ClientCode + "','" + CustomerCode + "','" + ActualAmt + "','" + RefundAmt + "'" + ",'" + GiftVoucherCode + "','" + DataPostFlag + "','" + folioNo + "','" + roomNo + "','" + dteBillDate + "')");
					}
					cnt++;
					flgData = true;
				}
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertBillTaxDtlData(JSONArray mJsonArray, String tableName)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		Set<String> setBillTaxDtl = new HashSet<String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			int cnt = 0;
			sbSql.append("INSERT INTO " + tableName + " (`strBillNo`, `strTaxCode`," + " `dblTaxableAmount`, `dblTaxAmount`, `strClientCode`, " + "`strDataPostFlag`) VALUES");
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String taxCode = mJsonObject.get("TaxCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from " + tableName + " " + "where strBillNo='" + billNo + "' and strTaxCode='" + taxCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				double taxableAmount = Double.parseDouble(mJsonObject.get("TaxableAmount").toString());
				double taxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				String key = billNo + "#" + taxCode + "#" + clientCode;
				boolean flgResult = setBillTaxDtl.add(key);

				if (flgResult)
				{
					if (cnt == 0)
					{
						sbSql.append("('" + billNo + "','" + taxCode + "','" + taxableAmount + "','" + taxAmount + "','" + clientCode + "','" + dataPostFlag + "')");
					}
					else
					{
						sbSql.append(",('" + billNo + "','" + taxCode + "','" + taxableAmount + "','" + taxAmount + "','" + clientCode + "','" + dataPostFlag + "')");
					}
					cnt++;
					flgData = true;
				}
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertVoidBillHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		List<String> arrListBills = new ArrayList<String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String clientCodeToDelete = "";
			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidbillhd` (`strPosCode`, `strReasonCode`, " + "`strReasonName`, `strBillNo`, `dblActualAmount`, `dblModifiedAmount`, " + "`dteBillDate`, `strTransType`, `dteModifyVoidBill`, `strTableNo`, " + "`strWaiterNo`, `intShiftCode`, `strUserCreated`,`strUserEdited`" + ",`strClientCode`, `strDataPostFlag`,`strRemark`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				clientCodeToDelete = mJsonObject.get("ClientCode").toString().trim();

				arrListBills.add(billNo);
				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidbillhd " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
				st.executeUpdate(sbSqlDelete.toString());

				if (mJsonObject.get("TransType").toString().trim().equalsIgnoreCase("USBill"))
				{
					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillsettlementdtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());
				}

				String posCode = mJsonObject.get("PosCode").toString();
				String reasonCode = mJsonObject.get("ReasonCode").toString();
				String reasonName = mJsonObject.get("ReasonName").toString();
				double actualAmount = Double.parseDouble(mJsonObject.get("ActualAmount").toString());
				double modifiedAmount = Double.parseDouble(mJsonObject.get("ModifiedAmount").toString());
				String billDate = mJsonObject.get("BillDate").toString();
				String transType = mJsonObject.get("TransType").toString();
				String modifyVoidBill = mJsonObject.get("ModifyVoidBill").toString();
				String tableNo = mJsonObject.get("TableNo").toString();
				String waiterNo = mJsonObject.get("WaiterNo").toString();
				int shiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String userCreated = mJsonObject.get("UserCreated").toString();
				String userEdited = mJsonObject.get("UserEdited").toString();
				String remark = mJsonObject.get("Remark").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + posCode + "','" + reasonCode + "','" + reasonName + "','" + billNo + "'" + ",'" + actualAmount + "','" + modifiedAmount + "','" + billDate + "'" + ",'" + transType + "','" + modifyVoidBill + "','" + tableNo + "','" + waiterNo + "'" + ",'" + shiftCode + "','" + userCreated + "','" + userEdited + "','" + clientCode + "'" + ",'N','" + remark + "')");
				}
				else
				{
					sbSql.append(",('" + posCode + "','" + reasonCode + "','" + reasonName + "','" + billNo + "'" + ",'" + actualAmount + "','" + modifiedAmount + "','" + billDate + "'" + ",'" + transType + "','" + modifyVoidBill + "','" + tableNo + "','" + waiterNo + "'" + ",'" + shiftCode + "','" + userCreated + "','" + userEdited + "','" + clientCode + "'" + ",'N','" + remark + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());

				for (String billNo : arrListBills)
				{
					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillhd " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbilldtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillmodifierdtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbilltaxdtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbilldiscdtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillpromotiondtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqbillcomplementrydtl " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCodeToDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertVoidBillDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidbilldtl` (`strPosCode`, `strReasonCode`, " + "`strReasonName`, `strItemCode`, `strItemName`, `strBillNo`, " + "`intQuantity`, `dblAmount`, `dblTaxAmount`, `dteBillDate`, " + "`strTransType`, `dteModifyVoidBill`, `strSettlementCode`, " + "`dblSettlementAmt`, `dblPaidAmt`, `strTableNo`, `strWaiterNo`, " + "`intShiftCode`, `strUserCreated`, `strClientCode`, `strDataPostFlag`," + " `strKOTNo`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String TransType = mJsonObject.get("TransType").toString().trim();
				String ItemCode = mJsonObject.get("ItemCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidbilldtl " + " where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "' and strItemCode='" + ItemCode + "' " + " and strTransType='" + TransType + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String PosCode = mJsonObject.get("PosCode").toString();
				String ReasonCode = mJsonObject.get("ReasonCode").toString();
				String ReasonName = mJsonObject.get("ReasonName").toString();
				String ItemName = mJsonObject.get("ItemName").toString();
				double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double TaxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String BillDate = mJsonObject.get("BillDate").toString();
				String ModifyVoidBill = mJsonObject.get("ModifyVoidBill").toString();
				String SettlementCode = mJsonObject.get("SettlementCode").toString();
				double SettlementAmt = Double.parseDouble(mJsonObject.get("SettlementAmt").toString());
				double PaidAmt = Double.parseDouble(mJsonObject.get("PaidAmt").toString());
				String TableNo = mJsonObject.get("TableNo").toString();
				String WaiterNo = mJsonObject.get("WaiterNo").toString();
				String ShiftCode = mJsonObject.get("ShiftCode").toString();
				String UserCreated = mJsonObject.get("UserCreated").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String KOTNo = mJsonObject.get("KOTNo").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + PosCode + "','" + ReasonCode + "','" + ReasonName + "','" + ItemCode + "','" + ItemName + "','" + billNo + "','" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "','" + TransType + "','" + ModifyVoidBill + "','" + SettlementCode + "','" + SettlementAmt + "','" + PaidAmt + "','" + TableNo + "','" + WaiterNo + "','" + ShiftCode + "','" + UserCreated + "','" + clientCode + "','" + DataPostFlag + "','" + KOTNo + "')");
				}
				else
				{
					sbSql.append(",('" + PosCode + "','" + ReasonCode + "','" + ReasonName + "','" + ItemCode + "','" + ItemName + "','" + billNo + "','" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "','" + TransType + "','" + ModifyVoidBill + "','" + SettlementCode + "','" + SettlementAmt + "','" + PaidAmt + "','" + TableNo + "','" + WaiterNo + "','" + ShiftCode + "','" + UserCreated + "','" + clientCode + "','" + DataPostFlag + "','" + KOTNo + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertVoidBillModDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidmodifierdtl` (`strBillNo`, `strItemCode`, " + "`strModifierCode`, `strModifierName`, `dblQuantity`, `dblAmount`, " + "`strClientCode`, `strCustomerCode`, `strDataPostFlag`, `strRemarks`, " + "`strReasonCode`,dteBillDate) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String billNo = mJsonObject.get("BillNo").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String itemCode = mJsonObject.get("ItemCode").toString().trim();
				String modifierCode = mJsonObject.get("ModifierCode").toString().trim();
				String dteBillDate = mJsonObject.get("dteBillDate").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append(" delete from tblvoidmodifierdtl " + " where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "' and strItemCode='" + itemCode + "' ");
				st.executeUpdate(sbSqlDelete.toString());

				String modifierName = mJsonObject.get("ModifierName").toString();
				double quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String customerCode = mJsonObject.get("CustomerCode").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String remarks = mJsonObject.get("Remarks").toString();
				String reasonCode = mJsonObject.get("ReasonCode").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + billNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "','" + quantity + "','" + amount + "'" + ",'" + clientCode + "','" + customerCode + "','" + dataPostFlag + "','" + remarks + "','" + reasonCode + "','" + dteBillDate + "')");
				}
				else
				{
					sbSql.append(",('" + billNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "','" + quantity + "','" + amount + "'" + ",'" + clientCode + "','" + customerCode + "','" + dataPostFlag + "','" + remarks + "','" + reasonCode + "','" + dteBillDate + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertVoidKotData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidkot` (`strTableNo`, `strPOSCode`," + " `strItemCode`, `strItemName`, `dblItemQuantity`, `dblAmount`, " + "`strWaiterNo`, `strKOTNo`, `intPaxNo`, `strType`, " + "`strReasonCode`, `strUserCreated`, `dteDateCreated`, " + "`dteVoidedDate`, `strDataPostFlag`, `strClientCode`," + " `strManualKOTNo`, `strPrintKOT`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String KOTNo = mJsonObject.get("KOTNo").toString().trim();
				String ClientCode = mJsonObject.get("ClientCode").toString().trim();
				String ItemCode = mJsonObject.get("ItemCode").toString().trim();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidkot " + "where strKOTNo='" + KOTNo + "' and strItemCode='" + ItemCode + "' and strClientCode='" + ClientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String TableNo = mJsonObject.get("TableNo").toString();
				String POSCode = mJsonObject.get("POSCode").toString();
				String ItemName = mJsonObject.get("ItemName").toString();
				double ItemQuantity = Double.parseDouble(mJsonObject.get("ItemQuantity").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String WaiterNo = mJsonObject.get("WaiterNo").toString();
				int PaxNo = Integer.parseInt(mJsonObject.get("PaxNo").toString());
				String Type = mJsonObject.get("Type").toString();
				String ReasonCode = mJsonObject.get("ReasonCode").toString();
				String UserCreated = mJsonObject.get("UserCreated").toString();
				String DateCreated = mJsonObject.get("DateCreated").toString();
				String VoidedDate = mJsonObject.get("VoidedDate").toString();
				String ManualKOTNo = mJsonObject.get("ManualKOTNo").toString();
				String PrintKOT = mJsonObject.get("PrintKOT").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + TableNo + "','" + POSCode + "','" + ItemCode + "','" + ItemName + "'" + ",'" + ItemQuantity + "','" + Amount + "','" + WaiterNo + "','" + KOTNo + "'" + ",'" + PaxNo + "','" + Type + "','" + ReasonCode + "','" + UserCreated + "'" + ",'" + DateCreated + "','" + VoidedDate + "','N','" + ClientCode + "'" + ",'" + ManualKOTNo + "','" + PrintKOT + "')");
				}
				else
				{
					sbSql.append(",('" + TableNo + "','" + POSCode + "','" + ItemCode + "','" + ItemName + "'" + ",'" + ItemQuantity + "','" + Amount + "','" + WaiterNo + "','" + KOTNo + "'" + ",'" + PaxNo + "','" + Type + "','" + ReasonCode + "','" + UserCreated + "'" + ",'" + DateCreated + "','" + VoidedDate + "','N','" + ClientCode + "'" + ",'" + ManualKOTNo + "','" + PrintKOT + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSql.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertStkInHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblstkinhd` (`strStkInCode`, `strPOSCode`, `dteStkInDate`," + " `strReasonCode`, `strPurchaseBillNo`, `dtePurchaseBillDate`, `intShiftCode`," + " `strUserCreated`, `strUserEdited`, `dteDateCreated`, `dteDateEdited`, " + "`strClientCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String stkInCode = mJsonObject.get("StkInCode").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblstkinhd " + "where strStkInCode='" + stkInCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String POSCode = mJsonObject.get("POSCode").toString();
				String StkInDate = mJsonObject.get("StkInDate").toString();
				String ReasonCode = mJsonObject.get("ReasonCode").toString();
				String PurchaseBillNo = mJsonObject.get("PurchaseBillNo").toString();
				String PurchaseBillDate = mJsonObject.get("PurchaseBillDate").toString();
				int ShiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String UserCreated = mJsonObject.get("UserCreated").toString();
				String UserEdited = mJsonObject.get("UserEdited").toString();
				String DateCreated = mJsonObject.get("DateCreated").toString();
				String DateEdited = mJsonObject.get("DateEdited").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + stkInCode + "','" + POSCode + "','" + StkInDate + "','" + ReasonCode + "','" + PurchaseBillNo + "','" + PurchaseBillDate + "','" + ShiftCode + "','" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + stkInCode + "','" + POSCode + "','" + StkInDate + "','" + ReasonCode + "','" + PurchaseBillNo + "','" + PurchaseBillDate + "','" + ShiftCode + "','" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSql.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertStkInDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblstkindtl` (`strStkInCode`, `strItemCode`, `dblQuantity`, " + "`dblPurchaseRate`, `dblAmount`, `strClientCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String stkInCode = mJsonObject.get("StkInCode").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblstkindtl " + "where strStkInCode='" + stkInCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String ItemCode = mJsonObject.get("ItemCode").toString();
				double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double PurchaseRate = Double.parseDouble(mJsonObject.get("PurchaseRate").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + stkInCode + "','" + ItemCode + "','" + Quantity + "','" + PurchaseRate + "','" + Amount + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + stkInCode + "','" + ItemCode + "','" + Quantity + "','" + PurchaseRate + "','" + Amount + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSql.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertStkOutHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblstkouthd` (`strStkOutCode`, `strPOSCode`," + " `dteStkOutDate`, `strReasonCode`, `strPurchaseBillNo`, " + "`dtePurchaseBillDate`, `intShiftCode`, `strUserCreated`," + " `strUserEdited`, `dteDateCreated`, `dteDateEdited`, " + "`strClientCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String stkOutCode = mJsonObject.get("StkOutCode").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblstkouthd " + "where strStkOutCode='" + stkOutCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String POSCode = mJsonObject.get("POSCode").toString();
				String StkOutDate = mJsonObject.get("StkOutDate").toString();
				String ReasonCode = mJsonObject.get("ReasonCode").toString();
				String PurchaseBillNo = mJsonObject.get("PurchaseBillNo").toString();
				String PurchaseBillDate = mJsonObject.get("PurchaseBillDate").toString();
				int ShiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String UserCreated = mJsonObject.get("UserCreated").toString();
				String UserEdited = mJsonObject.get("UserEdited").toString();
				String DateCreated = mJsonObject.get("DateCreated").toString();
				String DateEdited = mJsonObject.get("DateEdited").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + stkOutCode + "','" + POSCode + "','" + StkOutDate + "','" + ReasonCode + "','" + PurchaseBillNo + "','" + PurchaseBillDate + "','" + ShiftCode + "','" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + stkOutCode + "','" + POSCode + "','" + StkOutDate + "','" + ReasonCode + "','" + PurchaseBillNo + "','" + PurchaseBillDate + "','" + ShiftCode + "','" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSql.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertStkOutDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblstkoutdtl` (`strStkOutCode`, `strItemCode`, `dblQuantity`, " + "`dblPurchaseRate`, `dblAmount`, `strClientCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String stkOutCode = mJsonObject.get("StkOutCode").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblstkoutdtl " + "where strStkOutCode='" + stkOutCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String ItemCode = mJsonObject.get("ItemCode").toString();
				double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double PurchaseRate = Double.parseDouble(mJsonObject.get("PurchaseRate").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + stkOutCode + "','" + ItemCode + "','" + Quantity + "','" + PurchaseRate + "','" + Amount + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + stkOutCode + "','" + ItemCode + "','" + Quantity + "','" + PurchaseRate + "','" + Amount + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertPspHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";

		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblpsphd` (`strPSPCode`, `strPOSCode`, " + "`strStkInCode`, `strStkOutCode`, `strBillNo`, `dblStkInAmt`, " + "`dblSaleAmt`, `intShiftCode`, `strUserCreated`, `strUserEdited`, " + "`dteDateCreated`, `dteDateEdited`, `strClientCode`, `strDataPostFlag`," + "`strReasonCode`,`strRemarks`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String phyStkCode = mJsonObject.get("PSPCode").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblpsphd " + "where strPSPCode='" + phyStkCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String POSCode = mJsonObject.get("POSCode").toString();
				String StkInCode = mJsonObject.get("StkInCode").toString();
				String StkOutCode = mJsonObject.get("StkOutCode").toString();
				String BillNo = mJsonObject.get("BillNo").toString();
				double StkInAmt = Double.parseDouble(mJsonObject.get("StkInAmt").toString());
				double SaleAmt = Double.parseDouble(mJsonObject.get("SaleAmt").toString());
				int ShiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String UserCreated = mJsonObject.get("UserCreated").toString();
				String UserEdited = mJsonObject.get("UserEdited").toString();
				String DateCreated = mJsonObject.get("DateCreated").toString();
				String DateEdited = mJsonObject.get("DateEdited").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String ReasonCode = mJsonObject.get("ReasonCode").toString();
				String Remarks = mJsonObject.get("Remarks").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + phyStkCode + "','" + POSCode + "','" + StkInCode + "','" + StkOutCode + "'" + ",'" + BillNo + "','" + StkInAmt + "','" + SaleAmt + "','" + ShiftCode + "'" + ",'" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "'" + ",'" + clientCode + "','" + DataPostFlag + "','" + ReasonCode + "','" + Remarks + "')");
				}
				else
				{
					sbSql.append(",('" + phyStkCode + "','" + POSCode + "','" + StkInCode + "','" + StkOutCode + "'" + ",'" + BillNo + "','" + StkInAmt + "','" + SaleAmt + "','" + ShiftCode + "'" + ",'" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "'" + ",'" + clientCode + "','" + DataPostFlag + "','" + ReasonCode + "','" + Remarks + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertPspDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";

		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblpspdtl` (`strPSPCode`, `strItemCode`," + " `dblPhyStk`, `dblCompStk`, `dblVariance`, `dblVairanceAmt`," + " `strClientCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String phyStkCode = mJsonObject.get("PSPCode").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblpspdtl " + "where strPSPCode='" + phyStkCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String ItemCode = mJsonObject.get("ItemCode").toString();
				double PhyStk = Double.parseDouble(mJsonObject.get("PhyStk").toString());
				double CompStk = Double.parseDouble(mJsonObject.get("CompStk").toString());
				double Variance = Double.parseDouble(mJsonObject.get("Variance").toString());
				double VarianceAmt = Double.parseDouble(mJsonObject.get("VarianceAmt").toString());
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + phyStkCode + "','" + ItemCode + "','" + PhyStk + "','" + CompStk + "','" + Variance + "','" + VarianceAmt + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + phyStkCode + "','" + ItemCode + "','" + PhyStk + "','" + CompStk + "','" + Variance + "','" + VarianceAmt + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertAdvanceReceiptHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// Statement st1 = null;
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			// st1 = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("select strPOSCode,strPropertyPOSCode from tblposmaster");
			ResultSet rsPOS = st.executeQuery(sbSql.toString());
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			sbSql.setLength(0);
			sbSql.append("INSERT INTO `tblqadvancereceipthd` (`strReceiptNo`, `strAdvBookingNo`," + " `strPOSCode`, `strSettelmentMode`, `dtReceiptDate`, `dblAdvDeposite`," + " `intShiftCode`, `strUserCreated`, `strUserEdited`, `dtDateCreated`," + " `dtDateEdited`, `strClientCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String receiptNo = mJsonObject.get("ReceiptNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();
				String POSCode = mJsonObject.get("POSCode").toString();

				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvancereceipthd " + "where strReceiptNo='" + receiptNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String ItemCode = mJsonObject.get("AdvBookingNo").toString();
				String SettelmentMode = mJsonObject.get("SettelmentMode").toString();
				String ReceiptDate = mJsonObject.get("ReceiptDate").toString();
				double AdvDeposite = Double.parseDouble(mJsonObject.get("AdvDeposite").toString());
				int ShiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String UserCreated = mJsonObject.get("UserCreated").toString();
				String UserEdited = mJsonObject.get("UserEdited").toString();
				String DateCreated = mJsonObject.get("DateCreated").toString();
				String DateEdited = mJsonObject.get("DateEdited").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + receiptNo + "','" + ItemCode + "','" + POSCode + "','" + SettelmentMode + "','" + ReceiptDate + "','" + AdvDeposite + "','" + ShiftCode + "','" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + receiptNo + "','" + ItemCode + "','" + POSCode + "','" + SettelmentMode + "','" + ReceiptDate + "','" + AdvDeposite + "','" + ShiftCode + "','" + UserCreated + "','" + UserEdited + "','" + DateCreated + "','" + DateEdited + "','" + clientCode + "','" + DataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;

				// st1.close();
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertAdvanceReceiptDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblqadvancereceiptdtl` (`strReceiptNo`," + " `strSettlementCode`, `strCardNo`, `strExpirydate`, " + "`strChequeNo`, `dteCheque`, `strBankName`, " + "`dblAdvDepositesettleAmt`, `strRemark`, `dblPaidAmt`, " + "`strClientCode`, `strDataPostFlag`, `dteInstallment`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String receiptNo = mJsonObject.get("ReceiptNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvancereceiptdtl " + "where strReceiptNo='" + receiptNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String SettlementCode = mJsonObject.get("SettlementCode").toString();
				String CardNo = mJsonObject.get("CardNo").toString();
				String Expirydate = mJsonObject.get("Expirydate").toString();
				String ChequeNo = mJsonObject.get("ChequeNo").toString();
				String ChequeDate = mJsonObject.get("ChequeDate").toString();
				String BankName = mJsonObject.get("BankName").toString();
				double AdvDepositesettleAmt = Double.parseDouble(mJsonObject.get("AdvDepositesettleAmt").toString());
				String Remark = mJsonObject.get("Remark").toString();
				double PaidAmt = Double.parseDouble(mJsonObject.get("PaidAmt").toString());
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String Installment = mJsonObject.get("Installment").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + receiptNo + "','" + SettlementCode + "','" + CardNo + "','" + Expirydate + "','" + ChequeNo + "','" + ChequeDate + "','" + BankName + "','" + AdvDepositesettleAmt + "','" + Remark + "','" + PaidAmt + "','" + clientCode + "','" + DataPostFlag + "','" + Installment + "')");
				}
				else
				{
					sbSql.append(",('" + receiptNo + "','" + SettlementCode + "','" + CardNo + "','" + Expirydate + "','" + ChequeNo + "','" + ChequeDate + "','" + BankName + "','" + AdvDepositesettleAmt + "','" + Remark + "','" + PaidAmt + "','" + clientCode + "','" + DataPostFlag + "','" + Installment + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertAdvBookBillHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// Statement st1 = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			// st1 = cmsCon.createStatement();
			int cnt = 0;
			sbSql.append("select strPOSCode,strPropertyPOSCode from tblposmaster");
			ResultSet rsPOS = st.executeQuery(sbSql.toString());
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();
			sbSql.setLength(0);

			sbSql.append("INSERT INTO `tblqadvbookbillhd` (`strAdvBookingNo`, " + "`dteAdvBookingDate`, `dteOrderFor`, `strPOSCode`, " + "`strSettelmentMode`, `dblDiscountAmt`, `dblDiscountPer`," + " `dblTaxAmt`, `dblSubTotal`, `dblGrandTotal`," + " `strUserCreated`, `strUserEdited`, `dteDateCreated`, " + "`dteDateEdited`, `strClientCode`, `strCustomerCode`, " + "`intShiftCode`, `strMessage`, `strShape`, `strNote`, " + "`strDataPostFlag`, `strDeliveryTime`, `strWaiterNo`, " + "`strHomeDelivery`, `dblHomeDelCharges`, `strOrderType`, " + "`strManualAdvOrderNo`, `strImageName`, `strSpecialsymbolImage`,`strUrgentOrder`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();
				String POSCode = mJsonObject.get("POSCode").toString();

				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvbookbillhd " + "where strAdvBookingNo='" + advBookingNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String AdvBookingDate = mJsonObject.get("AdvBookingDate").toString();
				String OrderFor = mJsonObject.get("OrderFor").toString();
				String SettelmentMode = mJsonObject.get("SettelmentMode").toString();
				double DiscountAmt = Double.parseDouble(mJsonObject.get("DiscountAmt").toString());
				double DiscountPer = Double.parseDouble(mJsonObject.get("DiscountPer").toString());
				double TaxAmt = Double.parseDouble(mJsonObject.get("TaxAmt").toString());
				double SubTotal = Double.parseDouble(mJsonObject.get("SubTotal").toString());
				double GrandTotal = Double.parseDouble(mJsonObject.get("GrandTotal").toString());
				String UserCreated = mJsonObject.get("UserCreated").toString();
				String UserEdited = mJsonObject.get("UserEdited").toString();
				String Installment = mJsonObject.get("DateCreated").toString();
				String DateEdited = mJsonObject.get("DateEdited").toString();
				String CustomerCode = mJsonObject.get("CustomerCode").toString();
				int ShiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String Message = mJsonObject.get("Message").toString();
				String Shape = mJsonObject.get("Shape").toString();
				String Note = mJsonObject.get("Note").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String DeliveryTime = mJsonObject.get("DeliveryTime").toString();
				String WaiterNo = mJsonObject.get("WaiterNo").toString();
				String HomeDelivery = mJsonObject.get("HomeDelivery").toString();
				double HomeDelCharges = Double.parseDouble(mJsonObject.get("HomeDelCharges").toString());
				String OrderType = mJsonObject.get("OrderType").toString();
				String ManualAdvOrderNo = mJsonObject.get("ManualAdvOrderNo").toString();
				String ImageName = mJsonObject.get("ImageName").toString();
				String SpecialsymbolImage = mJsonObject.get("SpecialsymbolImage").toString();
				String urgentOrderYN = mJsonObject.get("UrgentOrderYN").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + advBookingNo + "','" + AdvBookingDate + "','" + OrderFor + "','" + POSCode + "','" + SettelmentMode + "','" + DiscountAmt + "','" + DiscountPer + "','" + TaxAmt + "','" + SubTotal + "','" + GrandTotal + "','" + UserCreated + "','" + UserEdited + "','" + Installment + "','" + DateEdited + "','" + clientCode + "','" + CustomerCode + "','" + ShiftCode + "','" + Message + "','" + Shape + "','" + Note + "','" + DataPostFlag + "','" + DeliveryTime + "','" + WaiterNo + "','" + HomeDelivery + "','" + HomeDelCharges + "','" + OrderType + "','" + ManualAdvOrderNo + "','" + ImageName + "','" + SpecialsymbolImage + "','" + urgentOrderYN + "')");
				}
				else
				{
					sbSql.append(",('" + advBookingNo + "','" + AdvBookingDate + "','" + OrderFor + "','" + POSCode + "','" + SettelmentMode + "','" + DiscountAmt + "','" + DiscountPer + "','" + TaxAmt + "','" + SubTotal + "','" + GrandTotal + "','" + UserCreated + "','" + UserEdited + "','" + Installment + "','" + DateEdited + "','" + clientCode + "','" + CustomerCode + "','" + ShiftCode + "','" + Message + "','" + Shape + "','" + Note + "','" + DataPostFlag + "','" + DeliveryTime + "','" + WaiterNo + "','" + HomeDelivery + "','" + HomeDelCharges + "','" + OrderType + "','" + ManualAdvOrderNo + "','" + ImageName + "','" + SpecialsymbolImage + "','" + urgentOrderYN + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{

				sbSql = null;
				sbSqlDelete = null;

				// st1.close();
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertAdvBookBillDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblqadvbookbilldtl` (`strItemCode`, " + "`strItemName`, `strAdvBookingNo`, `dblQuantity`, `dblAmount`, " + "`dblTaxAmount`, `dteAdvBookingDate`, `dteOrderFor`, `strClientCode`," + " `strCustomerCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvbookbilldtl " + "where strAdvBookingNo='" + advBookingNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String ItemCode = mJsonObject.get("ItemCode").toString();
				String ItemName = mJsonObject.get("ItemName").toString();
				double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double TaxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String AdvBookingDate = mJsonObject.get("AdvBookingDate").toString();
				String OrderFor = mJsonObject.get("OrderFor").toString();
				String CustomerCode = mJsonObject.get("CustomerCode").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + ItemCode + "','" + ItemName + "','" + advBookingNo + "','" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + AdvBookingDate + "','" + OrderFor + "','" + clientCode + "','" + CustomerCode + "','" + DataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + ItemCode + "','" + ItemName + "','" + advBookingNo + "','" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + AdvBookingDate + "','" + OrderFor + "','" + clientCode + "','" + CustomerCode + "','" + DataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertAdvBookBillCharDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblqadvbookbillchardtl` (`strItemCode`, `strAdvBookingNo`, `strCharCode`, `strCharValues`, " + "`strClientCode`,`strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvbookbillchardtl " + "where strAdvBookingNo='" + advBookingNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String itemCode = mJsonObject.get("ItemCode").toString();
				String charCode = mJsonObject.get("CharCode").toString();
				String charValues = mJsonObject.get("CharValues").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + itemCode + "','" + advBookingNo + "','" + charCode + "','" + charValues + "','" + clientCode + "','" + dataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + itemCode + "','" + advBookingNo + "','" + charCode + "','" + charValues + "','" + clientCode + "','" + dataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				sbSql = null;
				sbSqlDelete = null;
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertAdvBookBillModiferDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblqadvordermodifierdtl` " + "(`strAdvOrderNo`,`strItemCode`,`strModifierCode`, `strModifierName`" + ", `dblQuantity`, `dblAmount`,`strClientCode`, `strCustomerCode`" + ", `strUserCreated`, `strUserEdited`,`dteDateCreated`, `dteDateEdited`" + ",`strDataPostFlag`) " + "VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvordermodifierdtl " + "where strAdvOrderNo='" + advBookingNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String itemCode = mJsonObject.get("ItemCode").toString();
				String modifierCode = mJsonObject.get("ModifierCode").toString();
				String modifierName = mJsonObject.get("ModifierName").toString();
				double quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String custCode = mJsonObject.get("CustomerCode").toString();
				String userCreated = mJsonObject.get("UserCreated").toString();
				String userEdited = mJsonObject.get("UserEdited").toString();
				String dateCreated = mJsonObject.get("DateCreated").toString();
				String dateEdited = mJsonObject.get("DateEdited").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + advBookingNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + quantity + "','" + amount + "','" + clientCode + "','" + custCode + "'" + ",'" + userCreated + "','" + userEdited + "','" + dateCreated + "','" + dateEdited + "'" + ",'" + dataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + advBookingNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + quantity + "','" + amount + "','" + clientCode + "','" + custCode + "'" + ",'" + userCreated + "','" + userEdited + "','" + dateCreated + "','" + dateEdited + "'" + ",'" + dataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				// System.out.println(insert_qry);
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertHomeDeliveryData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblhomedelivery` " + "(`strBillNo`,`strCustomerCode`, `strDPCode`, `dteDate`, `tmeTime`" + ",`strPOSCode`, `strCustAddressLine1`, `strCustAddressLine2`, `strCustAddressLine3`" + ",`strCustAddressLine4`, `strCustCity`,`strDataPostFlag`, `strClientCode`,dblHomeDeliCharge,dblLooseCashAmt) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String billNo = mJsonObject.get("BillNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblhomedelivery " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String custCode = mJsonObject.get("CustomerCode").toString();
				String dpCode = mJsonObject.get("DPCode").toString();
				String date = mJsonObject.get("Date").toString();
				String time = mJsonObject.get("Time").toString();
				String posCode = mJsonObject.get("POSCode").toString();
				String custAddr1 = mJsonObject.get("CustAddress1").toString();
				String custAddr2 = mJsonObject.get("CustAddress2").toString();
				String custAddr3 = mJsonObject.get("CustAddress3").toString();
				String custAddr4 = mJsonObject.get("CustAddress4").toString();
				String custCity = mJsonObject.get("CustCity").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String dblHomeDeliCharge = mJsonObject.get("dblHomeDeliCharge").toString();
				String dblLooseCashAmt = mJsonObject.get("dblLooseCashAmt").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + billNo + "','" + custCode + "','" + dpCode + "','" + date + "','" + time + "'" + ",'" + posCode + "','" + custAddr1 + "','" + custAddr2 + "','" + custAddr3 + "'" + ",'" + custAddr4 + "','" + custCity + "','" + dataPostFlag + "','" + clientCode + "','" + dblHomeDeliCharge + "','" + dblLooseCashAmt + "')");
				}
				else
				{
					sbSql.append(",('" + billNo + "','" + custCode + "','" + dpCode + "','" + date + "','" + time + "'" + ",'" + posCode + "','" + custAddr1 + "','" + custAddr2 + "','" + custAddr3 + "'" + ",'" + custAddr4 + "','" + custCity + "','" + dataPostFlag + "','" + clientCode + "','" + dblHomeDeliCharge + "','" + dblLooseCashAmt + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertHomeDeliveryDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry="";
		// String sql="",deleteSql="";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblhomedeldtl` " + "(`strBillNo`,`strDPCode`, `strClientCode`,`strDataPostFlag`,strSettleYN,dblDBIncentives,dteBillDate) VALUES");

			JSONObject mJsonObject = new JSONObject();

			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String billNo = mJsonObject.get("BillNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblhomedelivery " + "where strBillNo='" + billNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String dpCode = mJsonObject.get("DPCode").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String strSettleYN = mJsonObject.get("strSettleYN").toString();
				String dblDBIncentives = mJsonObject.get("dblDBIncentives").toString();
				String dteBillDate = mJsonObject.get("dteBillDate").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + billNo + "','" + dpCode + "','" + clientCode + "','" + dataPostFlag + "','" + strSettleYN + "','" + dblDBIncentives + "','" + dteBillDate + "')");
				}
				else
				{
					sbSql.append(",('" + billNo + "','" + dpCode + "','" + clientCode + "','" + dataPostFlag + "','" + strSettleYN + "','" + dblDBIncentives + "','" + dteBillDate + "')");
				}
				cnt++;
				flgData = true;

			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertVoidAdvanceReceiptHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// Statement st1 = null;
		// String insert_qry = "";
		// String sql = "", deleteSql = "";

		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			// st1 = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("select strPOSCode,strPropertyPOSCode from tblposmaster");
			ResultSet rsPOS = st.executeQuery(sbSql.toString());
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			sbSql.setLength(0);
			sbSql.append("INSERT INTO `tblvoidadvancereceipthd` (`strReceiptNo`, `strAdvBookingNo`," + " `strPOSCode`, " + " `strSettelmentMode`, `dtReceiptDate`, `dblAdvDeposite`," + " `intShiftCode`, `strUserCreated`, " + " `strUserEdited`, `dtDateCreated`," + " `dtDateEdited`, `strClientCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String receiptNo = mJsonObject.get("ReceiptNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();
				String POSCode = mJsonObject.get("POSCode").toString();
				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidadvancereceipthd " + " where strReceiptNo='" + receiptNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvancereceipthd " + " where strReceiptNo='" + receiptNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblqadvancereceiptdtl " + " where strReceiptNo='" + receiptNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String itemCode = mJsonObject.get("AdvBookingNo").toString();
				String settelmentMode = mJsonObject.get("SettelmentMode").toString();
				String receiptDate = mJsonObject.get("ReceiptDate").toString();
				double advDeposite = Double.parseDouble(mJsonObject.get("AdvDeposite").toString());
				int shiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String userCreated = mJsonObject.get("UserCreated").toString();
				String userEdited = mJsonObject.get("UserEdited").toString();
				String dateCreated = mJsonObject.get("DateCreated").toString();
				String dateEdited = mJsonObject.get("DateEdited").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + receiptNo + "','" + itemCode + "','" + POSCode + "','" + settelmentMode + "','" + receiptDate + "'" + ",'" + advDeposite + "','" + shiftCode + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "'" + ",'" + dateEdited + "','" + clientCode + "','" + dataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + receiptNo + "','" + itemCode + "','" + POSCode + "','" + settelmentMode + "','" + receiptDate + "'" + ",'" + advDeposite + "','" + shiftCode + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "'" + ",'" + dateEdited + "','" + clientCode + "','" + dataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertVoidAdvanceReceiptDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry = "";
		// String sql = "", deleteSql = "";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidadvancereceiptdtl` (`strReceiptNo`," + " `strSettlementCode`, `strCardNo`, `strExpirydate`" + ", " + "`strChequeNo`, `dteCheque`, `strBankName`, " + "`dblAdvDepositesettleAmt`, `strRemark`, `dblPaidAmt`" + ",`strClientCode`, `strDataPostFlag`, `dteInstallment`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String receiptNo = mJsonObject.get("ReceiptNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidadvancereceiptdtl " + " where strReceiptNo='" + receiptNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String settlementCode = mJsonObject.get("SettlementCode").toString();
				String cardNo = mJsonObject.get("CardNo").toString();
				String expirydate = mJsonObject.get("Expirydate").toString();
				String chequeNo = mJsonObject.get("ChequeNo").toString();
				String chequeDate = mJsonObject.get("ChequeDate").toString();
				String bankName = mJsonObject.get("BankName").toString();
				double advDepositesettleAmt = Double.parseDouble(mJsonObject.get("AdvDepositesettleAmt").toString());
				String remark = mJsonObject.get("Remark").toString();
				double paidAmt = Double.parseDouble(mJsonObject.get("PaidAmt").toString());
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String installment = mJsonObject.get("Installment").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + receiptNo + "','" + settlementCode + "','" + cardNo + "','" + expirydate + "','" + chequeNo + "'" + ",'" + chequeDate + "','" + bankName + "','" + advDepositesettleAmt + "','" + remark + "','" + paidAmt + "'" + ",'" + clientCode + "','" + dataPostFlag + "','" + installment + "')");
				}
				else
				{
					sbSql.append(",('" + receiptNo + "','" + settlementCode + "','" + cardNo + "','" + expirydate + "','" + chequeNo + "'" + ",'" + chequeDate + "','" + bankName + "','" + advDepositesettleAmt + "','" + remark + "','" + paidAmt + "'" + ",'" + clientCode + "','" + dataPostFlag + "','" + installment + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertVoidAdvBookBillHdData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// Statement st1 = null;
		// String insert_qry = "";
		// String sql = "", deleteSql = "";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			String clientCodeForDelete = "";
			List<String> arrListAdvOrders = new ArrayList<String>();

			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			// st1 = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("select strPOSCode,strPropertyPOSCode from tblposmaster");
			ResultSet rsPOS = st.executeQuery(sbSql.toString());
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			sbSql.setLength(0);
			sbSql.append("INSERT INTO `tblvoidadvbookbillhd` (`strAdvBookingNo`, `dteAdvBookingDate`, `dteOrderFor`, `strPOSCode`" + ", `strSettelmentMode`, `dblDiscountAmt`, `dblDiscountPer`, `dblTaxAmt`, `dblSubTotal`, `dblGrandTotal`" + ", `strUserCreated`, `strUserEdited`, `dteDateCreated`, " + "`dteDateEdited`, `strClientCode`" + ", `strCustomerCode`, `intShiftCode`, `strMessage`, `strShape`, `strNote`, `strDataPostFlag`" + ", `strDeliveryTime`, `strWaiterNo`,`strHomeDelivery`, `dblHomeDelCharges`, `strOrderType`, `strManualAdvOrderNo`" + ", `strImageName`, `strSpecialsymbolImage`,`strUrgentOrder`,`strReasonCode`,`strRemark`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();
				clientCodeForDelete = mJsonObject.get("ClientCode").toString();
				String POSCode = mJsonObject.get("POSCode").toString();
				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);
				arrListAdvOrders.add(advBookingNo);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidadvbookbillhd where strAdvBookingNo='" + advBookingNo + "' " + " and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String advBookingDate = mJsonObject.get("AdvBookingDate").toString();
				String orderFor = mJsonObject.get("OrderFor").toString();
				String settelmentMode = mJsonObject.get("SettelmentMode").toString();
				double discountAmt = Double.parseDouble(mJsonObject.get("DiscountAmt").toString());
				double discountPer = Double.parseDouble(mJsonObject.get("DiscountPer").toString());
				double taxAmt = Double.parseDouble(mJsonObject.get("TaxAmt").toString());
				double subTotal = Double.parseDouble(mJsonObject.get("SubTotal").toString());
				double grandTotal = Double.parseDouble(mJsonObject.get("GrandTotal").toString());
				String userCreated = mJsonObject.get("UserCreated").toString();
				String userEdited = mJsonObject.get("UserEdited").toString();
				String installment = mJsonObject.get("DateCreated").toString();
				String dateEdited = mJsonObject.get("DateEdited").toString();
				String customerCode = mJsonObject.get("CustomerCode").toString();
				int shiftCode = Integer.parseInt(mJsonObject.get("ShiftCode").toString());
				String message = mJsonObject.get("Message").toString();
				String shape = mJsonObject.get("Shape").toString();
				String note = mJsonObject.get("Note").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String deliveryTime = mJsonObject.get("DeliveryTime").toString();
				String waiterNo = mJsonObject.get("WaiterNo").toString();
				String homeDelivery = mJsonObject.get("HomeDelivery").toString();
				double homeDelCharges = Double.parseDouble(mJsonObject.get("HomeDelCharges").toString());
				String orderType = mJsonObject.get("OrderType").toString();
				String manualAdvOrderNo = mJsonObject.get("ManualAdvOrderNo").toString();
				String imageName = mJsonObject.get("ImageName").toString();
				String specialsymbolImage = mJsonObject.get("SpecialsymbolImage").toString();
				String urgentOrder = mJsonObject.get("UrgentOrder").toString();
				String reasonCode = mJsonObject.get("ReasonCode").toString();
				String remark = mJsonObject.get("Remark").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + advBookingNo + "','" + advBookingDate + "','" + orderFor + "','" + POSCode + "','" + settelmentMode + "'" + ",'" + discountAmt + "','" + discountPer + "','" + taxAmt + "','" + subTotal + "','" + grandTotal + "'" + ",'" + userCreated + "','" + userEdited + "','" + installment + "','" + dateEdited + "'" + ",'" + clientCode + "','" + customerCode + "','" + shiftCode + "','" + message + "','" + shape + "','" + note + "'" + ",'" + dataPostFlag + "','" + deliveryTime + "','" + waiterNo + "','" + homeDelivery + "','" + homeDelCharges + "'" + ",'" + orderType + "','" + manualAdvOrderNo + "','" + imageName + "','" + specialsymbolImage + "'" + ",'" + urgentOrder + "','" + reasonCode + "','" + remark + "')");
				}
				else
				{
					sbSql.append(",('" + advBookingNo + "','" + advBookingDate + "','" + orderFor + "','" + POSCode + "','" + settelmentMode + "'" + ",'" + discountAmt + "','" + discountPer + "','" + taxAmt + "','" + subTotal + "','" + grandTotal + "'" + ",'" + userCreated + "','" + userEdited + "','" + installment + "','" + dateEdited + "'" + ",'" + clientCode + "','" + customerCode + "','" + shiftCode + "','" + message + "','" + shape + "','" + note + "'" + ",'" + dataPostFlag + "','" + deliveryTime + "','" + waiterNo + "','" + homeDelivery + "','" + homeDelCharges + "'" + ",'" + orderType + "','" + manualAdvOrderNo + "','" + imageName + "','" + specialsymbolImage + "'" + ",'" + urgentOrder + "','" + reasonCode + "','" + remark + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				// sql = sql.substring(1, sql.length());
				// insert_qry += " " + sql;

				res = st.executeUpdate(sbSql.toString());
				for (String advOrderNo : arrListAdvOrders)
				{
					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqadvbookbillhd " + " where strAdvBookingNo='" + advOrderNo + "' and strClientCode='" + clientCodeForDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqadvbookbilldtl " + " where strAdvBookingNo='" + advOrderNo + "' and strClientCode='" + clientCodeForDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tbladvbookbillchardtl " + " where strAdvBookingNo='" + advOrderNo + "' and strClientCode='" + clientCodeForDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblqadvordermodifierdtl " + " where strAdvOrderNo='" + advOrderNo + "' and strClientCode='" + clientCodeForDelete + "'");
					st.executeUpdate(sbSqlDelete.toString());
				}
			}
			else
			{
				res = 1;
			}
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				// st1.close();
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertVoidAdvBookBillDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry = "";
		// String sql = "", deleteSql = "";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidadvbookbilldtl` (`strItemCode`, " + "`strItemName`, `strAdvBookingNo`, " + "`dblQuantity`, `dblAmount`, " + "`dblTaxAmount`, `dteAdvBookingDate`, `dteOrderFor`, " + "`strClientCode`," + " `strCustomerCode`, `strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidadvbookbilldtl " + "where strAdvBookingNo='" + advBookingNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String itemCode = mJsonObject.get("ItemCode").toString();
				String itemName = mJsonObject.get("ItemName").toString();
				double quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double taxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String advBookingDate = mJsonObject.get("AdvBookingDate").toString();
				String orderFor = mJsonObject.get("OrderFor").toString();
				String customerCode = mJsonObject.get("CustomerCode").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + itemCode + "','" + itemName + "','" + advBookingNo + "','" + quantity + "','" + amount + "','" + taxAmount + "'" + ",'" + advBookingDate + "','" + orderFor + "','" + clientCode + "','" + customerCode + "','" + dataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + itemCode + "','" + itemName + "','" + advBookingNo + "','" + quantity + "','" + amount + "','" + taxAmount + "'" + ",'" + advBookingDate + "','" + orderFor + "','" + clientCode + "','" + customerCode + "','" + dataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				// sql = sql.substring(1, sql.length());
				// insert_qry += " " + sql;
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertVoidAdvBookBillModiferDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry = "";
		// String sql = "", deleteSql = "";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidadvordermodifierdtl` " + "(`strAdvOrderNo`,`strItemCode`,`strModifierCode`, `strModifierName`, `dblQuantity`, `dblAmount`,`strClientCode`" + ", `strCustomerCode`, `strUserCreated`, `strUserEdited`,`dteDateCreated`, `dteDateEdited`,`strDataPostFlag`) VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidadvordermodifierdtl " + " where strAdvOrderNo='" + advBookingNo + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String itemCode = mJsonObject.get("ItemCode").toString();
				String modifierCode = mJsonObject.get("ModifierCode").toString();
				String modifierName = mJsonObject.get("ModifierName").toString();
				double quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String custCode = mJsonObject.get("CustomerCode").toString();
				String userCreated = mJsonObject.get("UserCreated").toString();
				String userEdited = mJsonObject.get("UserEdited").toString();
				String dateCreated = mJsonObject.get("DateCreated").toString();
				String dateEdited = mJsonObject.get("DateEdited").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + advBookingNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + quantity + "'" + ",'" + amount + "','" + clientCode + "','" + custCode + "'" + ",'" + userCreated + "','" + userEdited + "'" + ",'" + dateCreated + "','" + dateEdited + "'" + ",'" + dataPostFlag + "')");
				}
				else
				{
					sbSql.append(",('" + advBookingNo + "','" + itemCode + "','" + modifierCode + "','" + modifierName + "'" + ",'" + quantity + "'" + ",'" + amount + "','" + clientCode + "','" + custCode + "'" + ",'" + userCreated + "','" + userEdited + "'" + ",'" + dateCreated + "','" + dateEdited + "'" + ",'" + dataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				// sql = sql.substring(1, sql.length());
				// insert_qry += " " + sql;
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertVoidAdvBookBillcharDtlData(JSONArray mJsonArray)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String insert_qry = "";
		// String sql = "", deleteSql = "";
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			int cnt = 0;
			sbSql.append("INSERT INTO `tblvoidadvbookbillchardtl` " + "(`strItemCode`,`strAdvBookingNo`," + " `strCharCode`, `strCharValues`" + ", `strClientCode`, `strDataPostFlag`) " + "VALUES");

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String strItemCode = mJsonObject.get("ItemCode").toString();
				String advBookingNo = mJsonObject.get("AdvBookingNo").toString();
				String clientCode = mJsonObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblvoidadvbookbillchardtl " + "where strAdvBookingNo='" + advBookingNo + "' " + " and strItemCode='" + strItemCode + "' and strClientCode='" + clientCode + "'");
				st.executeUpdate(sbSqlDelete.toString());

				String strCharCode = mJsonObject.get("CharCode").toString();
				String strCharValues = mJsonObject.get("CharValues").toString();
				String strDataPostFlag = mJsonObject.get("DataPostFlag").toString();

				if (cnt == 0)
				{
					sbSql.append("('" + strItemCode + "','" + advBookingNo + "', " + " '" + strCharCode + "','" + strCharValues + "'" + ",'" + clientCode + "','" + strDataPostFlag + "' )");
				}
				else
				{
					sbSql.append(",('" + strItemCode + "','" + advBookingNo + "', " + " '" + strCharCode + "','" + strCharValues + "'" + ",'" + clientCode + "','" + strDataPostFlag + "')");
				}
				cnt++;
				flgData = true;
			}

			if (flgData)
			{
				// sql = sql.substring(1, sql.length());
				// insert_qry += " " + sql;
				res = st.executeUpdate(sbSql.toString());
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertPlaceOrderData(JSONObject objPlaceOrderData)
	{
		int res = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;

		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		StringBuilder sbSqlDelete = new StringBuilder();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			// Code to insert tblplaceorderhd Table
			if (null != objPlaceOrderData.get("PlaceOrderHd"))
			{
				JSONArray jsonArrPlaceOrderHd = objPlaceOrderData.getJSONArray("PlaceOrderHd");
				int cnt = 0;

				sbSql.setLength(0);
				sbSql.append("INSERT INTO `tblplaceorderhd` (`strOrderCode`, `strSOCode`," + " `dteSODate`, `dteOrderDate`, `strUserCreated`, " + " `dteDateCreated`, `strClientCode`, `strCloseSO`," + " `strDCCode`, `strOrderTypeCode`, `strDataPostFlag`, " + " `strOrderType`) VALUES");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrPlaceOrderHd.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrPlaceOrderHd.get(i);

					String OrderCode = mJsonObject.get("OrderCode").toString();
					String ClientCode = mJsonObject.get("ClientCode").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblplaceorderhd " + "where strOrderCode='" + OrderCode + "' and strClientCode='" + ClientCode + "'");
					st.executeUpdate(sbSqlDelete.toString());

					String SOCode = mJsonObject.get("SOCode").toString();
					String SODate = mJsonObject.get("SODate").toString();
					String OrderDate = mJsonObject.get("OrderDate").toString();
					String UserCreated = mJsonObject.get("UserCreated").toString();
					String DateCreated = mJsonObject.get("DateCreated").toString();
					String CloseSO = mJsonObject.get("CloseSO").toString();
					String DCCode = mJsonObject.get("DCCode").toString();
					String OrderTypeCode = mJsonObject.get("OrderTypeCode").toString();
					String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
					String OrderType = mJsonObject.get("OrderType").toString();

					if (cnt == 0)
					{
						sbSql.append("('" + OrderCode + "','" + SOCode + "','" + SODate + "','" + OrderDate + "','" + UserCreated + "','" + DateCreated + "','" + ClientCode + "','" + CloseSO + "'," + "'" + DCCode + "','" + OrderTypeCode + "','" + DataPostFlag + "','" + OrderType + "')");
					}
					else
					{
						sbSql.append(",('" + OrderCode + "','" + SOCode + "','" + SODate + "','" + OrderDate + "','" + UserCreated + "','" + DateCreated + "','" + ClientCode + "','" + CloseSO + "'," + "'" + DCCode + "','" + OrderTypeCode + "','" + DataPostFlag + "','" + OrderType + "')");
					}
					cnt++;
					flgData = true;
				}

				if (flgData)
				{
					// sql=sql.substring(1,sql.length());
					// insert_qry+=" "+sql;
					res = st.executeUpdate(sbSql.toString());
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblplaceorderdtl Table
			if (null != objPlaceOrderData.get("PlaceOrderDtl"))
			{
				JSONArray jsonArrPlaceOrderDtl = objPlaceOrderData.getJSONArray("PlaceOrderDtl");

				flgData = false;
				int cnt = 0;

				sbSql.setLength(0);
				sbSql.append("INSERT INTO `tblplaceorderdtl` (`strOrderCode`, `strProductCode`, `strItemCode`, " + "`dblQty`, `dblStockQty`, `strClientCode`, `strDataPostFlag`,`strAdvOrderNo`) VALUES");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrPlaceOrderDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrPlaceOrderDtl.get(i);

					String orderCode = mJsonObject.get("OrderCode").toString();
					String clientCode = mJsonObject.get("ClientCode").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblplaceorderdtl " + "where strOrderCode='" + orderCode + "' and strClientCode='" + clientCode + "'");
					st.executeUpdate(sbSqlDelete.toString());
					System.out.println(sbSqlDelete.toString());

					String productCode = mJsonObject.get("ProductCode").toString();
					String itemCode = mJsonObject.get("ItemCode").toString();
					double orderQty = Double.parseDouble(mJsonObject.get("Quantity").toString());
					double stkQty = Double.parseDouble(mJsonObject.get("StockQty").toString());
					String dataPostFlag = mJsonObject.get("DataPostFlag").toString();
					String advanceOrderNo = mJsonObject.get("AdvOrderNo").toString();

					if (cnt == 0)
					{
						sbSql.append("('" + orderCode + "','" + productCode + "','" + itemCode + "','" + orderQty + "','" + stkQty + "','" + clientCode + "','" + dataPostFlag + "','" + advanceOrderNo + "')");
					}
					else
					{
						sbSql.append(",('" + orderCode + "','" + productCode + "','" + itemCode + "','" + orderQty + "','" + stkQty + "','" + clientCode + "','" + dataPostFlag + "','" + advanceOrderNo + "')");
					}
					cnt++;
					flgData = true;
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
				}
				else
				{
					res = 1;
				}
			}

			// Code to insert tblplaceorderadvorderdtl Table
			if (null != objPlaceOrderData.get("PlaceAdvOrderDtl"))
			{
				JSONArray jsonArrPlaceOrderAdvOrderDtl = objPlaceOrderData.getJSONArray("PlaceAdvOrderDtl");

				flgData = false;
				int cnt = 0;

				sbSql.setLength(0);
				sbSql.append("INSERT INTO `tblplaceorderadvorderdtl` (`strAdvOrderNo`, `dteOrderDate`, `strClientCode`, " + "`strDataPostFlag`, `strOrderType`) VALUES");

				JSONObject mJsonObject = new JSONObject();
				for (int i = 0; i < jsonArrPlaceOrderAdvOrderDtl.length(); i++)
				{
					mJsonObject = (JSONObject) jsonArrPlaceOrderAdvOrderDtl.get(i);
					String advanceOrderNo = mJsonObject.get("AdvOrderNo").toString();
					String clientCode = mJsonObject.get("ClientCode").toString();

					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblplaceorderadvorderdtl " + "where strAdvOrderNo='" + advanceOrderNo + "' and strClientCode='" + clientCode + "'");
					st.executeUpdate(sbSqlDelete.toString());

					String orderDate = mJsonObject.get("OrderDate").toString();
					String dataPostFlag = mJsonObject.get("DataPostFlag").toString();
					String orderType = mJsonObject.get("OrderType").toString();

					if (cnt == 0)
					{
						sbSql.append("('" + advanceOrderNo + "','" + orderDate + "','" + clientCode + "','" + dataPostFlag + "','" + orderType + "')");
					}
					else
					{
						sbSql.append(",('" + advanceOrderNo + "','" + orderDate + "','" + clientCode + "','" + dataPostFlag + "','" + orderType + "')");
					}
					cnt++;
					flgData = true;
				}

				if (flgData)
				{
					res = st.executeUpdate(sbSql.toString());
				}
				else
				{
					res = 1;
				}
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSql = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	/*
	 * 
	 * @SuppressWarnings("finally") private int
	 * funInsertPlaceOrderHdData(JSONArray mJsonArray) { int res=0;
	 * clsDatabaseConnection objDb=new clsDatabaseConnection(); boolean
	 * flgData=false; Connection cmsCon=null; Statement st = null; //String
	 * insert_qry=""; //String sql="",deleteSql=""; StringBuilder sbSql=new
	 * StringBuilder(); sbSql.setLength(0); StringBuilder sbSqlDelete=new
	 * StringBuilder();
	 * 
	 * try { cmsCon=objDb.funOpenPOSCon("mysql","transaction"); st =
	 * cmsCon.createStatement();
	 * 
	 * int cnt=0;
	 * sbSql.append("INSERT INTO `tblplaceorderhd` (`strOrderCode`, `strSOCode`,"
	 * + " `dteSODate`, `dteOrderDate`, `strUserCreated`, " +
	 * " `dteDateCreated`, `strClientCode`, `strCloseSO`," +
	 * " `strDCCode`, `strOrderTypeCode`, `strDataPostFlag`, " +
	 * " `strOrderType`) VALUES");
	 * 
	 * JSONObject mJsonObject = new JSONObject(); for (int i = 0; i <
	 * mJsonArray.length(); i++) { mJsonObject =(JSONObject) mJsonArray.get(i);
	 * 
	 * String OrderCode=mJsonObject.get("OrderCode").toString(); String
	 * ClientCode=mJsonObject.get("ClientCode").toString();
	 * 
	 * sbSqlDelete.setLength(0);
	 * sbSqlDelete.append("delete from tblplaceorderhd " +
	 * "where strOrderCode='"+OrderCode+"' and strClientCode='"+ClientCode+"'");
	 * st.executeUpdate(sbSqlDelete.toString());
	 * 
	 * String SOCode=mJsonObject.get("SOCode").toString(); String
	 * SODate=mJsonObject.get("SODate").toString(); String
	 * OrderDate=mJsonObject.get("OrderDate").toString(); String
	 * UserCreated=mJsonObject.get("UserCreated").toString(); String
	 * DateCreated=mJsonObject.get("DateCreated").toString(); String
	 * CloseSO=mJsonObject.get("CloseSO").toString(); String
	 * DCCode=mJsonObject.get("DCCode").toString(); String
	 * OrderTypeCode=mJsonObject.get("OrderTypeCode").toString(); String
	 * DataPostFlag=mJsonObject.get("DataPostFlag").toString(); String
	 * OrderType=mJsonObject.get("OrderType").toString();
	 * 
	 * if(cnt==0) {
	 * sbSql.append("('"+OrderCode+"','"+SOCode+"','"+SODate+"','"+OrderDate
	 * +"','" +
	 * UserCreated+"','"+DateCreated+"','"+ClientCode+"','"+CloseSO+"'," +
	 * "'"+DCCode+"','"+OrderTypeCode+"','"+DataPostFlag+"','"+OrderType+"')");
	 * } else {
	 * sbSql.append(",('"+OrderCode+"','"+SOCode+"','"+SODate+"','"+OrderDate
	 * +"','" +
	 * UserCreated+"','"+DateCreated+"','"+ClientCode+"','"+CloseSO+"'," +
	 * "'"+DCCode+"','"+OrderTypeCode+"','"+DataPostFlag+"','"+OrderType+"')");
	 * } cnt++; flgData=true; }
	 * 
	 * if(flgData) { //sql=sql.substring(1,sql.length()); //insert_qry+=" "+sql;
	 * res=st.executeUpdate(sbSql.toString()); } else { res=1; }
	 * 
	 * } catch(Exception e) { clsUtilityFunctions objUtility=new
	 * clsUtilityFunctions(); objUtility.funWriteErrorLog(e); res=0;
	 * e.printStackTrace(); } finally { sbSql=null; sbSqlDelete=null; try {
	 * if(null!=st) { st.close(); } if(null!=cmsCon) { cmsCon.close(); } } catch
	 * (SQLException e) { e.printStackTrace(); } return res; } }
	 * 
	 * 
	 * @SuppressWarnings("finally") private int
	 * funInsertPlaceOrderDtlData(JSONArray mJsonArray) { int res=0;
	 * clsDatabaseConnection objDb=new clsDatabaseConnection(); boolean
	 * flgData=false; Connection cmsCon=null; Statement st = null; //String
	 * insert_qry=""; //String sql="",deleteSql=""; StringBuilder sbSql=new
	 * StringBuilder(); sbSql.setLength(0); StringBuilder sbSqlDelete=new
	 * StringBuilder();
	 * 
	 * try { cmsCon=objDb.funOpenPOSCon("mysql","transaction"); st =
	 * cmsCon.createStatement();
	 * 
	 * int cnt=0; sbSql.append(
	 * "INSERT INTO `tblplaceorderdtl` (`strOrderCode`, `strProductCode`, `strItemCode`, "
	 * +
	 * "`dblQty`, `dblStockQty`, `strClientCode`, `strDataPostFlag`,`strAdvOrderNo`) VALUES"
	 * );
	 * 
	 * JSONObject mJsonObject = new JSONObject(); for (int i = 0; i <
	 * mJsonArray.length(); i++) { mJsonObject =(JSONObject) mJsonArray.get(i);
	 * 
	 * String orderCode=mJsonObject.get("OrderCode").toString(); String
	 * clientCode=mJsonObject.get("ClientCode").toString();
	 * 
	 * sbSqlDelete.setLength(0);
	 * sbSqlDelete.append("delete from tblplaceorderdtl " +
	 * "where strOrderCode='"+orderCode+"' and strClientCode='"+clientCode+"'");
	 * st.executeUpdate(sbSqlDelete.toString());
	 * System.out.println(sbSqlDelete.toString());
	 * 
	 * String productCode=mJsonObject.get("ProductCode").toString(); String
	 * itemCode=mJsonObject.get("ItemCode").toString(); double
	 * orderQty=Double.parseDouble(mJsonObject.get("Quantity").toString());
	 * double stkQty=Double.parseDouble(mJsonObject.get("StockQty").toString());
	 * String dataPostFlag=mJsonObject.get("DataPostFlag").toString(); String
	 * advanceOrderNo=mJsonObject.get("AdvOrderNo").toString();
	 * 
	 * if(cnt==0) {
	 * sbSql.append("('"+orderCode+"','"+productCode+"','"+itemCode+
	 * "','"+orderQty
	 * +"','"+stkQty+"','"+clientCode+"','"+dataPostFlag+"','"+advanceOrderNo
	 * +"')"); } else {
	 * sbSql.append(",('"+orderCode+"','"+productCode+"','"+itemCode
	 * +"','"+orderQty
	 * +"','"+stkQty+"','"+clientCode+"','"+dataPostFlag+"','"+advanceOrderNo
	 * +"')"); } cnt++; flgData=true; }
	 * 
	 * if(flgData) { //sql=sql.substring(1,sql.length()); //insert_qry+=" "+sql;
	 * try { res=st.executeUpdate(sbSql.toString()); } catch(Exception e) {
	 * e.printStackTrace(); } } else { res=1; }
	 * 
	 * } catch(Exception e) { clsUtilityFunctions objUtility=new
	 * clsUtilityFunctions(); objUtility.funWriteErrorLog(e); res=0;
	 * e.printStackTrace(); } finally { sbSql=null; sbSqlDelete=null; try {
	 * st.close(); cmsCon.close(); } catch (SQLException e) {
	 * e.printStackTrace(); } return res; } }
	 * 
	 * 
	 * 
	 * @SuppressWarnings("finally") private int
	 * funInsertPlaceAdvanceOrderDtlData(JSONArray mJsonArray) { int res=0;
	 * clsDatabaseConnection objDb=new clsDatabaseConnection(); boolean
	 * flgData=false; Connection cmsCon=null; Statement st = null; //String
	 * insert_qry=""; //String sql="",deleteSql=""; StringBuilder sbSql=new
	 * StringBuilder(); sbSql.setLength(0); StringBuilder sbSqlDelete=new
	 * StringBuilder();
	 * 
	 * try { cmsCon=objDb.funOpenPOSCon("mysql","transaction"); st =
	 * cmsCon.createStatement(); int cnt=0; sbSql.append(
	 * "INSERT INTO `tblplaceorderadvorderdtl` (`strAdvOrderNo`, `dteOrderDate`, `strClientCode`, "
	 * + "`strDataPostFlag`, `strOrderType`) VALUES");
	 * 
	 * JSONObject mJsonObject = new JSONObject(); for (int i = 0; i <
	 * mJsonArray.length(); i++) { mJsonObject =(JSONObject) mJsonArray.get(i);
	 * String advanceOrderNo=mJsonObject.get("AdvOrderNo").toString(); String
	 * clientCode=mJsonObject.get("ClientCode").toString();
	 * 
	 * sbSqlDelete.setLength(0);
	 * sbSqlDelete.append("delete from tblplaceorderadvorderdtl " +
	 * "where strAdvOrderNo='"
	 * +advanceOrderNo+"' and strClientCode='"+clientCode+"'");
	 * st.executeUpdate(sbSqlDelete.toString());
	 * 
	 * String orderDate=mJsonObject.get("OrderDate").toString(); String
	 * dataPostFlag=mJsonObject.get("DataPostFlag").toString(); String
	 * orderType=mJsonObject.get("OrderType").toString();
	 * 
	 * if(cnt==0) {
	 * sbSql.append("('"+advanceOrderNo+"','"+orderDate+"','"+clientCode
	 * +"','"+dataPostFlag+"','"+orderType+"')"); } else {
	 * sbSql.append(",('"+advanceOrderNo
	 * +"','"+orderDate+"','"+clientCode+"','"+dataPostFlag
	 * +"','"+orderType+"')"); } cnt++; flgData=true; }
	 * 
	 * if(flgData) { //sql=sql.substring(1,sql.length()); //insert_qry+=" "+sql;
	 * res=st.executeUpdate(sbSql.toString()); } else { res=1; }
	 * 
	 * } catch(Exception e) { clsUtilityFunctions objUtility=new
	 * clsUtilityFunctions(); objUtility.funWriteErrorLog(e); res=0;
	 * e.printStackTrace(); } finally { sbSql=null; sbSqlDelete=null; try {
	 * st.close(); cmsCon.close(); } catch (SQLException e) {
	 * e.printStackTrace(); } return res; } }
	 */

	@GET
	@Path("/funGetEditedMasterList")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetEditedMasterList(@QueryParam("strLastModifiedDate") String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select * from tblmasteroperationstatus where dteDateEdited > '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterStatus = st.executeQuery(sql);
			while (rsMasterStatus.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("TableName", rsMasterStatus.getString(1));
				obj.put("Date", rsMasterStatus.getString(2));
				arrObj.put(obj);
			}
			rsMasterStatus.close();

			jObj.put("updatedmasterlist", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	@GET
	@Path("/funGetMasterData")
	@Produces(MediaType.APPLICATION_JSON)
	public String funFetchMasterData(@QueryParam("strMasterName") String masterName, @QueryParam("strPropertyPOSCode") String propertyPOSCode, @QueryParam("strLastModifiedDate") String lastModifiedDate)
	{
		String responseData = masterName + " " + propertyPOSCode + " " + lastModifiedDate;

		if (masterName.equalsIgnoreCase("tblmenuitempricingdtl"))
		{
			responseData = funGetMenuItemPricingDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblmenuitempricinghd"))
		{
			responseData = funGetMenuItemPricingHD(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblitemmodofier"))
		{
			responseData = funGetItemModifier(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tbltaxhd"))
		{
			responseData = funGetTaxHd(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblsettlementtax"))
		{
			responseData = funGetSettlementTax(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tbltaxposdtl"))
		{
			responseData = funGetTaxPOSDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tbltaxongroup"))
		{
			responseData = funGetTaxGroupDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tbltablemaster"))
		{
			responseData = funGetTableMaster(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblwaitermaster"))
		{
			responseData = funGetWaiterMaster(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblsuperuserdtl"))
		{
			responseData = funGetSuperUserDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tbluserdtl"))
		{
			responseData = funGetUserDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tbluserhd"))
		{
			responseData = funGetUserHD(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblrecipedtl"))
		{
			responseData = funGetRecipeDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblcounterhd"))
		{
			responseData = funGetCounterMaster(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblcounterdtl"))
		{
			responseData = funGetCounterDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblpromotionmaster"))
		{
			responseData = funGetPromotionMaster(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblpromotiondtl"))
		{
			responseData = funGetPromotionDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblbuypromotiondtl"))
		{
			responseData = funGetBuyPromotionDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblpromotiondaytimedtl"))
		{
			responseData = funGetPromotionDayTimeDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblloyaltypointcustomerdtl"))
		{
			responseData = funGetLoyaltyPointsCustomerDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}

		else if (masterName.equalsIgnoreCase("tblloyaltypointmenuhddtl"))
		{
			responseData = funGetLoyaltyPointsMenuHdDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblloyaltypointposdtl"))
		{
			responseData = funGetLoyaltyPointsPOSDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblloyaltypointsubgroupdtl"))
		{
			responseData = funGetLoyaltyPointsSubGroupDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblareamaster"))
		{
			responseData = funGetAreaMasterDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblordermaster"))
		{
			responseData = funGetOrderMaster(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblitemorderingdtl"))
		{
			responseData = funGetItemOrderingDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblcharactersticsmaster"))
		{
			responseData = funGetCharacteristicMaster(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblitemcharctersticslinkupdtl"))
		{
			responseData = funGetItemCharLinkupDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblcharvalue"))
		{
			responseData = funGetCharValuesDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblitemmasterlinkupdtl"))
		{
			responseData = funGetItemMasterLinkupDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblcustomermaster"))
		{
			String clientCode = propertyPOSCode.substring(0, 7);
			responseData = funGetCustomerMaster(masterName, clientCode, lastModifiedDate);
			return responseData;
		}
		else if (masterName.equalsIgnoreCase("tblposwiseitemwiseincentives"))
		{
			String clientCode = propertyPOSCode.substring(0, 7);
			responseData = funGetPosWiseItemWiseIncentiveValuesDtl(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		// else if (masterName.equalsIgnoreCase("tblitemmaster"))
		// {
		// String clientCode = propertyPOSCode.substring(0, 7);
		// long increamentSize=1000;
		// long size = funGetItemMasterDataLimit(masterName, propertyPOSCode,
		// lastModifiedDate);
		// long startIndex = 0;
		// long endIndex = increamentSize;
		// if (size <= endIndex)
		// {
		// endIndex = size;
		// }
		//
		// for (; endIndex <= size;)
		// {
		//
		// responseData = funGetMasterDetail(masterName, propertyPOSCode,
		// lastModifiedDate, startIndex, endIndex);
		//
		// startIndex = endIndex;
		// endIndex = endIndex + increamentSize;
		// if (size <= endIndex)
		// {
		// endIndex = size;
		// }
		//
		// return responseData.toString();
		// }
		//
		// }
		else
		{
			responseData = funGetMasterDetail(masterName, propertyPOSCode, lastModifiedDate);
			return responseData;
		}
		// return responseData.toString();

	}

	private long funGetItemMasterDataLimit(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		long size = 0;
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master");
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			String sql = "select count(*) from " + masterName + " where dteDateEdited > '" + lastModifiedDate + "' " + "order by dteDateEdited";
			System.out.println(sql);
			ResultSet rsMasterData = st.executeQuery(sql);
			if (rsMasterData.next())
			{
				size = rsMasterData.getLong(1);
			}
			rsMasterData.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return size;
		}
	}

	private String funGetAreaMasterDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master");
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			String sql = "select a.strAreaCode,a.strAreaName,a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode "
					+ ",a.strDataPostFlag,right(b.strPropertyPOSCode,3) as strPOSCode,strMACAddress " + "from tblareamaster a,tblposmaster b "
					+ "where  (a.strPOSCode=b.strPosCode or a.strPOSCode='All') "
					+ "and a.dteDateEdited > '" + lastModifiedDate + "' "
					+ "group by a.strAreaCode,a.strAreaName "
					+ "order by a.dteDateEdited";
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

			jObj.put(masterName, arrObj);

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
				// if (null != posCon)
				// {
				// posCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetMenuItemPricingDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select a.strItemCode,a.strItemName,right(b.strPropertyPOSCode,3),a.strMenuCode,a.strPopular" + " ,a.strPriceMonday,a.strPriceTuesday,a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday" + " ,a.strPriceSaturday,a.strPriceSunday,a.dteFromDate,a.dteToDate" + " ,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,a.strCostCenterCode,a.strTextColor" + " ,a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strAreaCode" + " ,a.strSubMenuHeadCode,a.strHourlyPricing,a.longPricingId,a.strClientCode " + " from tblmenuitempricingdtl a, tblposmaster b " + " where a.strPOSCode = b.strPOSCode and (b.strPropertyPOSCode ='" + propertyPOSCode + "'" + " OR a.strPOSCode = 'All') and a.dteDateEdited > '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();

			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsMasterData.getString(1));
				obj.put("ItemName", rsMasterData.getString(2));
				obj.put("PropertyPOSCode", rsMasterData.getString(3));
				obj.put("MenuCode", rsMasterData.getString(4));
				obj.put("Popular", rsMasterData.getString(5));
				obj.put("PriceMonday", rsMasterData.getString(6));
				obj.put("PriceTuesday", rsMasterData.getString(7));
				obj.put("PriceWenesday", rsMasterData.getString(8));
				obj.put("PriceThursday", rsMasterData.getString(9));
				obj.put("PriceFriday", rsMasterData.getString(10));
				obj.put("PriceSaturday", rsMasterData.getString(11));
				obj.put("PriceSunday", rsMasterData.getString(12));
				obj.put("FromDate", rsMasterData.getString(13));
				obj.put("ToDate", rsMasterData.getString(14));
				obj.put("TimeFrom", rsMasterData.getString(15));
				obj.put("AMPMFrom", rsMasterData.getString(16));
				obj.put("TimeTo", rsMasterData.getString(17));
				obj.put("AMPMTo", rsMasterData.getString(18));
				obj.put("CostCenterCode", rsMasterData.getString(19));
				obj.put("TextColor", rsMasterData.getString(20));
				obj.put("UserCreated", rsMasterData.getString(21));
				obj.put("UserEdited", rsMasterData.getString(22));
				obj.put("DateCreated", rsMasterData.getString(23));
				obj.put("DateEdited", rsMasterData.getString(24));
				obj.put("AreaCode", rsMasterData.getString(25));
				obj.put("SubMenuHeadCode", rsMasterData.getString(26));
				obj.put("HourlyPricing", rsMasterData.getString(27));
				obj.put("PricingId", rsMasterData.getString(28));
				obj.put("ClientCode", rsMasterData.getString(29));

				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tblmenuitempricingdtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetMenuItemPricingHD(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select right(b.strPropertyPOSCode,3),a.strMenuCode,a.strMenuName,a.strUserCreated," + "a.strUserEdited,a.dteDateCreated,a.dteDateEdited from tblmenuitempricinghd a, tblposmaster b " + "where a.strPOSCode = b.strPOSCode and (b.strPropertyPOSCode = '" + propertyPOSCode + "' " + "OR a.strPOSCode = 'All')" + "and a.dteDateEdited > '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("PropertyPOSCode", rsMasterData.getString(1));
				obj.put("MenuCode", rsMasterData.getString(2));
				obj.put("MenuName", rsMasterData.getString(3));
				obj.put("UserCreated", rsMasterData.getString(4));
				obj.put("UserEdited", rsMasterData.getString(5));
				obj.put("DateCreated", rsMasterData.getString(6));
				obj.put("DateEdited", rsMasterData.getString(7));

				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tblmenuitempricinghd", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetItemModifier(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select a.strItemCode,a.strModifierCode,a.strChargable,a.dblRate,a.strApplicable,a.strDefaultModifier" + " from " + masterName + " a, tblmodifiermaster b " + "where a.strModifierCode = b.strModifierCode and b.dteDateEdited > '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsMasterData.getString(1));
				obj.put("ModifierCode", rsMasterData.getString(2));
				obj.put("Chargable", rsMasterData.getString(3));
				obj.put("Rate", rsMasterData.getString(4));
				obj.put("Applicable", rsMasterData.getString(5));
				obj.put("DefaultModifier", rsMasterData.getString(6));
				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tblitemmodofier", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetTaxHd(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			String clientPOSCodeinHO = funGetClientPOSCode(propertyPOSCode);
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select a.* " + " from tbltaxhd a,tbltaxposdtl b " + " where a.strTaxCode=b.strTaxCode and b.strPOSCode='" + clientPOSCodeinHO + "' " + " and a.dteDateEdited > '" + lastModifiedDate + "' order by a.dteDateEdited";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);

			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("TaxCode", rsMasterData.getString(1));
				obj.put("TaxDesc", rsMasterData.getString(2));
				obj.put("TaxOnSP", rsMasterData.getString(3));
				obj.put("TaxType", rsMasterData.getString(4));
				obj.put("Percent", rsMasterData.getString(5));
				obj.put("Amount", rsMasterData.getString(6));
				obj.put("ValidFrom", rsMasterData.getString(7));
				obj.put("ValidTo", rsMasterData.getString(8));
				obj.put("TaxOnGD", rsMasterData.getString(9));
				obj.put("TaxCalculation", rsMasterData.getString(10));
				obj.put("TaxIndicator", rsMasterData.getString(11));
				obj.put("TaxRounded", rsMasterData.getString(12));
				obj.put("TaxOnTax", rsMasterData.getString(13));
				obj.put("TaxOnTaxCode", rsMasterData.getString(14));
				obj.put("UserCreated", rsMasterData.getString(15));
				obj.put("UserEdited", rsMasterData.getString(16));
				obj.put("DateCreated", rsMasterData.getString(17));
				obj.put("DateEdited", rsMasterData.getString(18));
				obj.put("AreaCode", rsMasterData.getString(19));
				obj.put("OperationType", rsMasterData.getString(20));
				obj.put("ItemType", rsMasterData.getString(21));
				obj.put("DataPostFlag", rsMasterData.getString(23));
				obj.put("AccountCode", rsMasterData.getString(24));
				obj.put("TaxShortName", rsMasterData.getString(25));
				obj.put("strBillNote", rsMasterData.getString(26));

				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tbltaxhd", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetSettlementTax(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select a.strTaxCode,a.strSettlementCode,a.strSettlementName,a.strApplicable,a.dteFrom,a.dteTo," + "a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited " + "from " + masterName + " a, tbltaxhd b " + "where a.strTaxCode = b.strTaxCode and b.dteDateEdited > '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);

			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("TaxCode", rsMasterData.getString(1));
				obj.put("SettlementCode", rsMasterData.getString(2));
				obj.put("SettlementName", rsMasterData.getString(3));
				obj.put("Applicable", rsMasterData.getString(4));
				obj.put("FromDate", rsMasterData.getString(5));
				obj.put("ToDate", rsMasterData.getString(6));
				obj.put("UserCreated", rsMasterData.getString(7));
				obj.put("UserEdited", rsMasterData.getString(8));
				obj.put("DateCreated", rsMasterData.getString(9));
				obj.put("DateEdited", rsMasterData.getString(10));

				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tblsettlementtax", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetTaxPOSDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String clientPOSCodeinHO = funGetClientPOSCode(propertyPOSCode);
			String sql = " select a.strTaxCode,right(c.strPropertyPOSCode,3),a.strTaxDesc " + " from tbltaxposdtl a, tbltaxhd b ,tblposmaster c " + " where a.strTaxCode = b.strTaxCode and a.strPOSCode=c.strPosCode and a.strPOSCode='" + clientPOSCodeinHO + "' ";
			System.out.println(sql);

			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("TaxCode", rsMasterData.getString(1));
				obj.put("POSCode", rsMasterData.getString(2));
				obj.put("TaxDesc", rsMasterData.getString(3));
				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tbltaxposdtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetTaxGroupDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String clientPOSCodeinHO = funGetClientPOSCode(propertyPOSCode);
			String sql = " select c.* "
					+ "from tbltaxhd a,tbltaxposdtl b,tbltaxongroup c "
					+ "where a.strTaxCode=b.strTaxCode  "
					+ "and b.strTaxCode=c.strTaxCode "
					+ "and b.strPOSCode='" + clientPOSCodeinHO + "' "
					+ "and c.dteDateEdited>'" + lastModifiedDate + "' ";
			System.out.println(sql);

			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("strTaxCode", rsMasterData.getString(1));
				obj.put("strGroupCode", rsMasterData.getString(2));
				obj.put("strGroupName", rsMasterData.getString(3));
				obj.put("strApplicable", rsMasterData.getString(4));
				obj.put("dteFrom", rsMasterData.getString(5));
				obj.put("dteTo", rsMasterData.getString(6));
				obj.put("strUserCreated", rsMasterData.getString(7));
				obj.put("strUserEdited", rsMasterData.getString(8));
				obj.put("dteDateCreated", rsMasterData.getString(9));
				obj.put("dteDateEdited", rsMasterData.getString(10));
				obj.put("strClientCode", rsMasterData.getString(11));

				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tbltaxongroup", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetSuperUserDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();
		JSONArray arrObj = new JSONArray();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
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
				for (int cnt = 0; cnt < arrPOSAccessCodes.length; cnt++)
				{
					if (hmClientPOSCodes.containsKey(arrPOSAccessCodes[cnt]))
					{
						String sql = "select a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd,a.strEdit" + ",a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant,a.strTLA,a.strAuditing " + "from " + masterName + " a, tbluserhd b " + "where a.strUserCode = b.strUserCode and b.strUserCode='" + userCode + "' and b.dteDateEdited > '" + lastModifiedDate + "'";
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetUserDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();
		JSONArray arrObj = new JSONArray();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
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
				for (int cnt = 0; cnt < arrPOSAccessCodes.length; cnt++)
				{
					if (hmClientPOSCodes.containsKey(arrPOSAccessCodes[cnt]))
					{
						String sql = "select a.strUserCode,a.strFormName,a.strButtonName,a.intSequence,a.strAdd,a.strEdit" + ",a.strDelete,a.strView,a.strPrint,a.strSave,a.strGrant,a.strTLA,a.strAuditing " + "from " + masterName + " a, tbluserhd b " + "where a.strUserCode = b.strUserCode and b.strUserCode='" + userCode + "' and b.dteDateEdited > '" + lastModifiedDate + "'";
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetClientPOSCode(String propertyPOSCode)
	{
		String clientPOSCodeInHO = "";

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select strPOSCode from tblposmaster where strPropertyPOSCOde='" + propertyPOSCode + "'";
			ResultSet rsPOSCode = st.executeQuery(sql);
			if (rsPOSCode.next())
			{
				clientPOSCodeInHO = rsPOSCode.getString(1);
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return clientPOSCodeInHO;
		}
	}

	private Map<String, String> funGetAllClientPOSCodes(String propertyPOSCode)
	{
		Map<String, String> hmClientPOSCodes = new HashMap<String, String>();

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String propertyCode = propertyPOSCode.substring(0, 7);

			String sql = "select strPOSCode from tblposmaster where left(strPropertyPOSCOde,7)='" + propertyCode + "'";
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return hmClientPOSCodes;
		}
	}

	private String funGetUserHD(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();
		JSONArray arrObj = new JSONArray();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			Map<String, String> hmClientPOSCodes = funGetAllClientPOSCodes(propertyPOSCode);
			// String
			// clientPOSCode=propertyPOSCode.substring(8,propertyPOSCode.length());
			String clientPOSCode = "";

			String sqlUserMaster = "select a.strUserCode,a.strPOSAccess " + " from tbluserhd a " + " where a.dteDateEdited > '" + lastModifiedDate + "'";
			ResultSet rsUserMaster = st1.executeQuery(sqlUserMaster);
			while (rsUserMaster.next())
			{
				clientPOSCode = "";
				String POSAccessCodes = rsUserMaster.getString(2);
				String userCode = rsUserMaster.getString(1);
				String[] arrPOSAccessCodes = POSAccessCodes.split(",");
				for (int cnt = 0; cnt < arrPOSAccessCodes.length; cnt++)
				{
					if (hmClientPOSCodes.containsKey(arrPOSAccessCodes[cnt]))
					{
						clientPOSCode += "," + hmClientPOSCodes.get(arrPOSAccessCodes[cnt]);
					}
				}

				if (clientPOSCode.length() > 0)
				{
					clientPOSCode = clientPOSCode.substring(1, clientPOSCode.length());
					String sql = "select a.strUserCode,a.strUserName,a.strPassword,a.strSuperType,a.dteValidDate," + " '" + clientPOSCode + "',a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited, " + " a.strClientCode,a.strDataPostFlag,a.imgUserIcon,a.strImgUserIconPath,a.strDebitCardString " + " from " + masterName + " a " + " where a.strUserCode='" + userCode + "' and a.dteDateEdited > '" + lastModifiedDate + "' ";
					System.out.println(sql);
					ResultSet rsMasterData = st.executeQuery(sql);
					while (rsMasterData.next())
					{
						JSONObject obj = new JSONObject();

						obj.put("UserCode", rsMasterData.getString(1));
						obj.put("UserName", rsMasterData.getString(2));
						obj.put("Password", rsMasterData.getString(3));
						obj.put("SuperType", rsMasterData.getString(4));
						obj.put("ValidDate", rsMasterData.getString(5));
						obj.put("POSAccess", rsMasterData.getString(6));
						obj.put("UserCreated", rsMasterData.getString(7));
						obj.put("UserEdited", rsMasterData.getString(8));
						obj.put("DateCreated", rsMasterData.getString(9));
						obj.put("DateEdited", rsMasterData.getString(10));
						obj.put("ClientCode", rsMasterData.getString(11));
						obj.put("DataPostFlag", rsMasterData.getString(12));
						obj.put("ImageIcon", rsMasterData.getString(13));
						obj.put("ImagePath", rsMasterData.getString(14));
						obj.put("DebitCardString", rsMasterData.getString(15));

						arrObj.put(obj);
					}
					rsMasterData.close();
				}
			}

			jObj.put("tbluserhd", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetRecipeDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select a.strRecipeCode,a.strChildItemCode,a.dblQuantity,right(c.strPropertyPOSCode,3)" + ",a.strClientCode,a.strDataPostFlag " + "from tblrecipedtl a, tblrecipehd b ,tblposmaster c " + "where a.strrecipeCode = b.strrecipeCode and a.strPOSCode=c.strPosCode " + "and b.dteDateEdited > '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("RecipeCode", rsMasterData.getString(1));
				obj.put("ChildItemCode", rsMasterData.getString(2));
				obj.put("Quantity", rsMasterData.getString(3));
				obj.put("POSCode", rsMasterData.getString(4));
				obj.put("ClientCode", rsMasterData.getString(5));
				obj.put("DataPostFlag", rsMasterData.getString(6));
				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tblrecipedtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetCounterMaster(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select a.*,right(b.strPropertyPOSCode,3) " + " from tblcounterhd a,tblposmaster b" + " where a.strPOSCode=b.strPosCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and b.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsCounterMasterData = st.executeQuery(sql);
			while (rsCounterMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("strCounterCode", rsCounterMasterData.getString(1));
				obj.put("strCounterName", rsCounterMasterData.getString(2));
				obj.put("strPOSCode", rsCounterMasterData.getString(3));
				obj.put("strUserCreated", rsCounterMasterData.getString(4));
				obj.put("strUserEdited", rsCounterMasterData.getString(5));
				obj.put("dteDateCreated", rsCounterMasterData.getString(6));
				obj.put("dteDateEdited", rsCounterMasterData.getString(7));
				obj.put("strClientCode", rsCounterMasterData.getString(8));
				obj.put("strDataPostFlag", rsCounterMasterData.getString(9));
				obj.put("strOperational", rsCounterMasterData.getString(10));
				obj.put("strUserCode", rsCounterMasterData.getString(11));

				arrObj.put(obj);
			}
			rsCounterMasterData.close();

			sql = " select a.* " + " from tblcounterhd a " + " where a.dteDateEdited >= '" + lastModifiedDate + "' and a.strPOSCode='All' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			rsCounterMasterData = st1.executeQuery(sql);
			while (rsCounterMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("strCounterCode", rsCounterMasterData.getString(1));
				obj.put("strCounterName", rsCounterMasterData.getString(2));
				obj.put("strPOSCode", "All");
				obj.put("strUserCreated", rsCounterMasterData.getString(4));
				obj.put("strUserEdited", rsCounterMasterData.getString(5));
				obj.put("dteDateCreated", rsCounterMasterData.getString(6));
				obj.put("dteDateEdited", rsCounterMasterData.getString(7));
				obj.put("strClientCode", rsCounterMasterData.getString(8));
				obj.put("strDataPostFlag", rsCounterMasterData.getString(9));
				obj.put("strOperational", rsCounterMasterData.getString(10));
				obj.put("strUserCode", rsCounterMasterData.getString(11));

				arrObj.put(obj);
			}
			rsCounterMasterData.close();

			jObj.put("tblcounterhd", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetCounterDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.*  from tblcounterhd a,tblcounterdtl b,tblposmaster c " + " where a.strCounterCode=b.strCounterCode and a.strPOSCode=c.strPosCode " + " and c.strPropertyPOSCode ='" + propertyPOSCode + "' and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);

			JSONArray arrObj = new JSONArray();
			ResultSet rsCounterDtlData = st.executeQuery(sql);
			while (rsCounterDtlData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("CounterCode", rsCounterDtlData.getString(1));
				obj.put("MenuCode", rsCounterDtlData.getString(2));
				obj.put("ClientCode", rsCounterDtlData.getString(3));
				arrObj.put(obj);
			}
			rsCounterDtlData.close();

			sql = " select b.*  from tblcounterhd a,tblcounterdtl b " + " where a.strCounterCode=b.strCounterCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and a.strPOSCode='All' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			rsCounterDtlData = st1.executeQuery(sql);
			while (rsCounterDtlData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("CounterCode", rsCounterDtlData.getString(1));
				obj.put("MenuCode", rsCounterDtlData.getString(2));
				obj.put("ClientCode", rsCounterDtlData.getString(3));
				arrObj.put(obj);
			}
			rsCounterDtlData.close();

			jObj.put("tblcounterdtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetTableMaster(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select a.strTableNo,a.strTableName,a.strStatus,a.strAreaCode,a.strWaiterNo,a.intPaxNo,a.strOperational "
					+ " ,a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strDataPostFlag"
					+ " ,a.intSequence,right(b.strPropertyPOSCode,3),a.strNCTable "
					+ " from tbltablemaster a,tblposmaster b "
					+ " where a.strPOSCode=b.strPosCode and (b.strPropertyPOSCode ='" + propertyPOSCode + "' OR a.strPOSCode = 'All') "
					+ " and a.dteDateEdited >= '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("TableNo", rsMasterData.getString(1));
				obj.put("TableName", rsMasterData.getString(2));
				obj.put("Status", rsMasterData.getString(3));
				obj.put("AreaCode", rsMasterData.getString(4));
				obj.put("WaiterNo", rsMasterData.getString(5));
				obj.put("PaxNo", rsMasterData.getString(6));
				obj.put("Operational", rsMasterData.getString(7));
				obj.put("UserCreated", rsMasterData.getString(8));
				obj.put("UserEdited", rsMasterData.getString(9));
				obj.put("DateCreated", rsMasterData.getString(10));
				obj.put("DateEdited", rsMasterData.getString(11));
				obj.put("DataPostFlag", rsMasterData.getString(13));
				obj.put("Sequence", rsMasterData.getString(14));
				obj.put("POSCode", rsMasterData.getString(15));
				obj.put("strNCTable", rsMasterData.getString(16));
				arrObj.put(obj);
			}
			rsMasterData.close();

			sql = "select a.strTableNo,a.strTableName,a.strStatus,a.strAreaCode,a.strWaiterNo,a.intPaxNo,a.strOperational "
					+ " ,a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strDataPostFlag"
					+ " ,a.intSequence,a.strPOSCode,a.strNCTable "
					+ " from tbltablemaster a where a.dteDateEdited >= '" + lastModifiedDate + "' "
					+ "and a.strPOSCode='All' ";
			System.out.println(sql);
			rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("TableNo", rsMasterData.getString(1));
				obj.put("TableName", rsMasterData.getString(2));
				obj.put("Status", rsMasterData.getString(3));
				obj.put("AreaCode", rsMasterData.getString(4));
				obj.put("WaiterNo", rsMasterData.getString(5));
				obj.put("PaxNo", rsMasterData.getString(6));
				obj.put("Operational", rsMasterData.getString(7));
				obj.put("UserCreated", rsMasterData.getString(8));
				obj.put("UserEdited", rsMasterData.getString(9));
				obj.put("DateCreated", rsMasterData.getString(10));
				obj.put("DateEdited", rsMasterData.getString(11));
				obj.put("DataPostFlag", rsMasterData.getString(13));
				obj.put("Sequence", rsMasterData.getString(14));
				obj.put("POSCode", rsMasterData.getString(15));
				obj.put("strNCTable", rsMasterData.getString(16));
				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tbltablemaster", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetWaiterMaster(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = "select a.strWaiterNo,a.strWShortName,a.strWFullName,a.strStatus,a.strOperational,a.strDebitCardString" + " ,a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strDataPostFlag " + " ,right(b.strPropertyPOSCode,3) " + " from tblwaitermaster a,tblposmaster b   " + " where a.strPOSCode=b.strPosCode and (b.strPropertyPOSCode ='" + propertyPOSCode + "' OR a.strPOSCode = 'All') " + " and a.dteDateEdited >= '" + lastModifiedDate + "'";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("WaiterNo", rsMasterData.getString(1));
				obj.put("WaiterShortName", rsMasterData.getString(2));
				obj.put("WaiterFullName", rsMasterData.getString(3));
				obj.put("Status", rsMasterData.getString(4));
				obj.put("Operational", rsMasterData.getString(5));
				obj.put("DebitCardString", rsMasterData.getString(6));
				obj.put("UserCreated", rsMasterData.getString(7));
				obj.put("UserEdited", rsMasterData.getString(8));
				obj.put("DateCreated", rsMasterData.getString(9));
				obj.put("DateEdited", rsMasterData.getString(10));
				obj.put("DataPostFlag", rsMasterData.getString(12));
				obj.put("POSCode", rsMasterData.getString(13));
				arrObj.put(obj);
			}
			rsMasterData.close();

			sql = "select a.strWaiterNo,a.strWShortName,a.strWFullName,a.strStatus,a.strOperational,a.strDebitCardString " + " ,a.strUserCreated,a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strClientCode,a.strDataPostFlag " + " ,a.strPOSCode " + " from tblwaitermaster a where a.dteDateEdited >= '" + lastModifiedDate + "' and a.strPOSCode='All' ";
			System.out.println(sql);
			rsMasterData = st.executeQuery(sql);
			while (rsMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("WaiterNo", rsMasterData.getString(1));
				obj.put("WaiterShortName", rsMasterData.getString(2));
				obj.put("WaiterFullName", rsMasterData.getString(3));
				obj.put("Status", rsMasterData.getString(4));
				obj.put("Operational", rsMasterData.getString(5));
				obj.put("DebitCardString", rsMasterData.getString(6));
				obj.put("UserCreated", rsMasterData.getString(7));
				obj.put("UserEdited", rsMasterData.getString(8));
				obj.put("DateCreated", rsMasterData.getString(9));
				obj.put("DateEdited", rsMasterData.getString(10));
				obj.put("DataPostFlag", rsMasterData.getString(12));
				obj.put("POSCode", rsMasterData.getString(13));
				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put("tblwaitermaster", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetPromotionMaster(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			String sql = " select a.strPromoCode,a.strPromoName,a.strPromotionOn,a.strPromoItemCode,a.strOperator,a.dblBuyQty"
					+ ",a.dteFromDate,a.dteToDate,a.tmeFromTime,a.tmeToTime,a.strDays,a.strType,a.strPromoNote,a.strUserCreated"
					+ ",a.strUserEdited,a.dteDateCreated,a.dteDateEdited,a.strDataPostFlag,right(b.strPropertyPOSCode,3)"
					+ ",a.strGetItemCode,a.strGetPromoOn,strAreaCode,strPromoGroupType,longKOTTimeBound "
					+ " from tblpromotionmaster a,tblposmaster b "
					+ " where a.strPOSCode=b.strPosCode and b.strPropertyPOSCode ='" + propertyPOSCode + "' "
					+ " and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited;  ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("PromoCode", rsPromoMasterData.getString(1));
				obj.put("PromoName", rsPromoMasterData.getString(2));
				obj.put("BuyPromoOn", rsPromoMasterData.getString(3));
				obj.put("BuyPromoItemCode", rsPromoMasterData.getString(4));
				obj.put("Operator", rsPromoMasterData.getString(5));
				obj.put("BuyItemQty", rsPromoMasterData.getString(6));
				obj.put("FromDate", rsPromoMasterData.getString(7));
				obj.put("ToDate", rsPromoMasterData.getString(8));
				obj.put("FromTime", rsPromoMasterData.getString(9));
				obj.put("ToTime", rsPromoMasterData.getString(10));
				obj.put("Days", rsPromoMasterData.getString(11));
				obj.put("Type", rsPromoMasterData.getString(12));
				obj.put("PromoNote", rsPromoMasterData.getString(13));
				obj.put("UserCreated", rsPromoMasterData.getString(14));
				obj.put("UserEdited", rsPromoMasterData.getString(15));
				obj.put("DateCreated", rsPromoMasterData.getString(16));
				obj.put("DateEdited", rsPromoMasterData.getString(17));
				obj.put("DataPostFlag", rsPromoMasterData.getString(18));
				obj.put("POSCode", rsPromoMasterData.getString(19));
				obj.put("GetItemCode", rsPromoMasterData.getString(20));
				obj.put("GetPromoOn", rsPromoMasterData.getString(21));
				obj.put("strAreaCode", rsPromoMasterData.getString(22));
				obj.put("strPromoGroupType", rsPromoMasterData.getString(23));
				obj.put("longKOTTimeBound", rsPromoMasterData.getString(24));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();
			//
			// sql = " select a.* from tblpromotionmaster a " +
			// " where a.strPOSCode = 'All' and a.dteDateEdited >= '" +
			// lastModifiedDate + "' "
			// + " order by a.dteDateEdited;  ";
			// System.out.println(sql);
			// ResultSet rsPromoMasterData1 = st1.executeQuery(sql);
			// while (rsPromoMasterData1.next())
			// {
			// JSONObject obj = new JSONObject();
			//
			// System.out.println("Promo Code= " +
			// rsPromoMasterData1.getString(1));
			// obj.put("PromoCode", rsPromoMasterData1.getString(1));
			// obj.put("PromoName", rsPromoMasterData1.getString(2));
			// obj.put("BuyPromoOn", rsPromoMasterData1.getString(3));
			// obj.put("BuyPromoItemCode", rsPromoMasterData1.getString(4));
			// obj.put("Operator", rsPromoMasterData1.getString(5));
			// obj.put("BuyItemQty", rsPromoMasterData1.getString(6));
			// obj.put("FromDate", rsPromoMasterData1.getString(7));
			// obj.put("ToDate", rsPromoMasterData1.getString(8));
			// obj.put("FromTime", rsPromoMasterData1.getString(9));
			// obj.put("ToTime", rsPromoMasterData1.getString(10));
			// obj.put("Days", rsPromoMasterData1.getString(11));
			// obj.put("Type", rsPromoMasterData1.getString(12));
			// obj.put("PromoNote", rsPromoMasterData1.getString(13));
			// obj.put("UserCreated", rsPromoMasterData1.getString(14));
			// obj.put("UserEdited", rsPromoMasterData1.getString(15));
			// obj.put("DateCreated", rsPromoMasterData1.getString(16));
			// obj.put("DateEdited", rsPromoMasterData1.getString(17));
			// obj.put("DataPostFlag", rsPromoMasterData1.getString(19));
			// obj.put("POSCode", rsPromoMasterData1.getString(20));
			// obj.put("GetItemCode", rsPromoMasterData1.getString(21));
			// obj.put("GetPromoOn", rsPromoMasterData1.getString(22));
			//
			// arrObj.put(obj);
			// }
			// rsPromoMasterData1.close();

			jObj.put("tblpromotionmaster", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetPromotionDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.* " + " from tblpromotionmaster a,tblpromotiondtl b,tblposmaster c " + " where a.strPromoCode=b.strPromoCode and a.strPOSCode=c.strPosCode " + " and c.strPropertyPOSCode ='" + propertyPOSCode + "' and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("PromoCode", rsPromoMasterData.getString(1));
				obj.put("GetPromoOn", rsPromoMasterData.getString(2));
				obj.put("GetPromoItemCode", rsPromoMasterData.getString(3));
				obj.put("GetItemQty", rsPromoMasterData.getString(4));
				obj.put("DiscountType", rsPromoMasterData.getString(5));
				obj.put("Discount", rsPromoMasterData.getString(6));
				obj.put("ClientCode", rsPromoMasterData.getString(7));
				obj.put("DataPostFlag", rsPromoMasterData.getString(8));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();

			sql = " select b.* " + " from tblpromotionmaster a,tblpromotiondtl b " + " where a.strPromoCode=b.strPromoCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and a.strPOSCode='All' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			ResultSet rsPromoMasterData1 = st1.executeQuery(sql);
			while (rsPromoMasterData1.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("PromoCode", rsPromoMasterData1.getString(1));
				obj.put("GetPromoOn", rsPromoMasterData1.getString(2));
				obj.put("GetPromoItemCode", rsPromoMasterData1.getString(3));
				obj.put("GetItemQty", rsPromoMasterData1.getString(4));
				obj.put("DiscountType", rsPromoMasterData1.getString(5));
				obj.put("Discount", rsPromoMasterData1.getString(6));
				obj.put("ClientCode", rsPromoMasterData1.getString(7));
				obj.put("DataPostFlag", rsPromoMasterData1.getString(8));

				arrObj.put(obj);
			}
			rsPromoMasterData1.close();
			jObj.put("tblpromotiondtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetBuyPromotionDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.* " + " from tblpromotionmaster a,tblbuypromotiondtl b,tblposmaster c " + " where a.strPromoCode=b.strPromoCode and a.strPOSCode=c.strPosCode " + " and c.strPropertyPOSCode ='" + propertyPOSCode + "' and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("PromoCode", rsPromoMasterData.getString(1));
				obj.put("BuyPromoItemCode", rsPromoMasterData.getString(2));
				obj.put("BuyItemQty", rsPromoMasterData.getString(3));
				obj.put("Operator", rsPromoMasterData.getString(4));
				obj.put("ClientCode", rsPromoMasterData.getString(5));
				obj.put("DataPostFlag", rsPromoMasterData.getString(6));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();

			sql = " select b.* " + " from tblpromotionmaster a,tblbuypromotiondtl b " + " where a.strPromoCode=b.strPromoCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and a.strPOSCode='All' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			ResultSet rsPromoMasterData1 = st1.executeQuery(sql);
			while (rsPromoMasterData1.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("PromoCode", rsPromoMasterData1.getString(1));
				obj.put("BuyPromoItemCode", rsPromoMasterData1.getString(2));
				obj.put("BuyItemQty", rsPromoMasterData1.getString(3));
				obj.put("Operator", rsPromoMasterData1.getString(4));
				obj.put("ClientCode", rsPromoMasterData1.getString(5));
				obj.put("DataPostFlag", rsPromoMasterData1.getString(6));

				arrObj.put(obj);
			}
			rsPromoMasterData1.close();

			jObj.put("tblbuypromotiondtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetPromotionDayTimeDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.* " + " from tblpromotionmaster a,tblpromotiondaytimedtl b,tblposmaster c " + " where a.strPromoCode=b.strPromoCode and a.strPOSCode=c.strPosCode " + " and c.strPropertyPOSCode ='" + propertyPOSCode + "' " + " and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("PromoCode", rsPromoMasterData.getString(1));
				obj.put("strDay", rsPromoMasterData.getString(2));
				obj.put("tmeFromTime", rsPromoMasterData.getString(3));
				obj.put("tmeToTime", rsPromoMasterData.getString(4));
				obj.put("ClientCode", rsPromoMasterData.getString(5));
				obj.put("DataPostFlag", rsPromoMasterData.getString(6));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();

			sql = " select b.* " + " from tblpromotionmaster a,tblpromotiondaytimedtl b " + " where a.strPromoCode=b.strPromoCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and a.strPOSCode='All' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			ResultSet rsPromoMasterData1 = st1.executeQuery(sql);
			while (rsPromoMasterData1.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("PromoCode", rsPromoMasterData1.getString(1));
				obj.put("strDay", rsPromoMasterData1.getString(2));
				obj.put("tmeFromTime", rsPromoMasterData1.getString(3));
				obj.put("tmeToTime", rsPromoMasterData1.getString(4));
				obj.put("ClientCode", rsPromoMasterData1.getString(5));
				obj.put("DataPostFlag", rsPromoMasterData1.getString(6));

				arrObj.put(obj);
			}
			rsPromoMasterData1.close();

			jObj.put("tblpromotiondaytimedtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetLoyaltyPointsPOSDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master");
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			st1 = posCon.createStatement();

			String sql = " select b.* " + " from tblloyaltypoints a,tblloyaltypointposdtl b,tblposmaster c " + " where a.strLoyaltyCode=b.strLoyaltyCode and b.strPOSCode=c.strPosCode " + " and c.strPropertyPOSCode ='" + propertyPOSCode + "' and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited  ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("LoyaltyCode", rsPromoMasterData.getString(1));
				obj.put("POSCode", rsPromoMasterData.getString(2));
				obj.put("ClientCode", rsPromoMasterData.getString(3));
				obj.put("DataPostFlag", rsPromoMasterData.getString(4));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();
			jObj.put("tblloyaltypointposdtl", arrObj);

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
				// if (null != posCon)
				// {
				// posCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetLoyaltyPointsCustomerDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master"); //
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			st1 = posCon.createStatement();

			String sql = " select b.* " + " from tblloyaltypoints a,tblloyaltypointcustomerdtl b " + " where a.strLoyaltyCode=b.strLoyaltyCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited  ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("LoyaltyCode", rsPromoMasterData.getString(1));
				obj.put("CustTypeCode", rsPromoMasterData.getString(2));
				obj.put("ClientCode", rsPromoMasterData.getString(3));
				obj.put("DataPostFlag", rsPromoMasterData.getString(4));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();
			jObj.put("tblloyaltypointcustomerdtl", arrObj);

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
				// if (null != posCon)
				// {
				// posCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetLoyaltyPointsMenuHdDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master");
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			st1 = posCon.createStatement();

			String sql = " select b.* " + " from tblloyaltypoints a,tblloyaltypointmenuhddtl b " + " where a.strLoyaltyCode=b.strLoyaltyCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited  ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("LoyaltyCode", rsPromoMasterData.getString(1));
				obj.put("MenuCode", rsPromoMasterData.getString(2));
				obj.put("ClientCode", rsPromoMasterData.getString(3));
				obj.put("DataPostFlag", rsPromoMasterData.getString(4));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();

			jObj.put("tblloyaltypointmenuhddtl", arrObj);

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
				// if (null != posCon)
				// {
				// posCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetLoyaltyPointsSubGroupDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master");
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			st1 = posCon.createStatement();

			String sql = " select b.* " + " from tblloyaltypoints a,tblloyaltypointsubgroupdtl b " + " where a.strLoyaltyCode=b.strLoyaltyCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " order by a.dteDateEdited  ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsPromoMasterData = st.executeQuery(sql);
			while (rsPromoMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("LoyaltyCode", rsPromoMasterData.getString(1));
				obj.put("SGCode", rsPromoMasterData.getString(2));
				obj.put("ClientCode", rsPromoMasterData.getString(3));
				obj.put("DataPostFlag", rsPromoMasterData.getString(4));

				arrObj.put(obj);
			}
			rsPromoMasterData.close();
			jObj.put("tblloyaltypointsubgroupdtl", arrObj);

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
				// if (null != posCon)
				// {
				// posCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetOrderMaster(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			JSONArray arrObj = new JSONArray();
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select a.*,right(b.strPropertyPOSCode,3) " + " from tblordermaster a,tblposmaster b" + " where a.strPOSCode=b.strPosCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and b.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			ResultSet rsItemOrderingMasterData = st.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("strOrderCode", rsItemOrderingMasterData.getString(1));
				obj.put("strOrderDesc", rsItemOrderingMasterData.getString(2));
				obj.put("tmeUpToTime", rsItemOrderingMasterData.getString(3));
				obj.put("strUserCreated", rsItemOrderingMasterData.getString(4));
				obj.put("strUserEdited", rsItemOrderingMasterData.getString(5));
				obj.put("dteDateCreated", rsItemOrderingMasterData.getString(6));
				obj.put("dteDateEdited", rsItemOrderingMasterData.getString(7));
				obj.put("strClientCode", rsItemOrderingMasterData.getString(8));
				obj.put("strDataPostFlag", rsItemOrderingMasterData.getString(9));
				obj.put("strPOSCode", rsItemOrderingMasterData.getString(11));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			sql = " select a.* from tblordermaster a " + " where a.dteDateEdited >= '" + lastModifiedDate + "' and a.strPOSCode='All' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			rsItemOrderingMasterData = st1.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("strOrderCode", rsItemOrderingMasterData.getString(1));
				obj.put("strOrderDesc", rsItemOrderingMasterData.getString(2));
				obj.put("tmeUpToTime", rsItemOrderingMasterData.getString(3));
				obj.put("strUserCreated", rsItemOrderingMasterData.getString(4));
				obj.put("strUserEdited", rsItemOrderingMasterData.getString(5));
				obj.put("dteDateCreated", rsItemOrderingMasterData.getString(6));
				obj.put("dteDateEdited", rsItemOrderingMasterData.getString(7));
				obj.put("strClientCode", rsItemOrderingMasterData.getString(8));
				obj.put("strDataPostFlag", rsItemOrderingMasterData.getString(9));
				obj.put("strPOSCode", "All");

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			jObj.put("tblordermaster", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetItemOrderingDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select a.*,right(c.strPropertyPOSCode,3) " + " from tblitemorderingdtl a,tblitemmaster b,tblposmaster c " + " where a.strItemCode=b.strItemCode and a.strPOSCode=c.strPosCode " + " and b.dteDateEdited >= '" + lastModifiedDate + "' and c.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by b.dteDateEdited; ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsItemOrderingMasterData = st.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsItemOrderingMasterData.getString(1));
				obj.put("POSCode", rsItemOrderingMasterData.getString(6));
				obj.put("OrderCode", rsItemOrderingMasterData.getString(3));
				obj.put("ClientCode", rsItemOrderingMasterData.getString(4));
				obj.put("DataPostFlag", rsItemOrderingMasterData.getString(5));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			sql = " select a.* " + " from tblitemorderingdtl a,tblitemmaster b " + " where a.strItemCode=b.strItemCode and b.dteDateEdited >= '" + lastModifiedDate + "' " + " and a.strPOSCode='All' " + " order by b.dteDateEdited; ";
			System.out.println(sql);
			rsItemOrderingMasterData = st1.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsItemOrderingMasterData.getString(1));
				obj.put("POSCode", "All");
				obj.put("OrderCode", rsItemOrderingMasterData.getString(3));
				obj.put("ClientCode", rsItemOrderingMasterData.getString(4));
				obj.put("DataPostFlag", rsItemOrderingMasterData.getString(5));
				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			jObj.put("tblitemorderingdtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetCharacteristicMaster(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select a.*,right(b.strPropertyPOSCode,3) " + " from tblcharactersticsmaster a,tblposmaster b" + " where a.strPOSCode=b.strPosCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and b.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsItemOrderingMasterData = st.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("strCharCode", rsItemOrderingMasterData.getString(1));
				obj.put("strCharName", rsItemOrderingMasterData.getString(2));
				obj.put("strCharType", rsItemOrderingMasterData.getString(3));
				obj.put("strWSCharCode", rsItemOrderingMasterData.getString(4));
				obj.put("strValue", rsItemOrderingMasterData.getString(5));
				obj.put("strUserCreated", rsItemOrderingMasterData.getString(6));
				obj.put("strUserEdited", rsItemOrderingMasterData.getString(7));
				obj.put("dteDateCreated", rsItemOrderingMasterData.getString(8));
				obj.put("dteDateEdited", rsItemOrderingMasterData.getString(9));
				obj.put("strClientCode", rsItemOrderingMasterData.getString(10));
				obj.put("strDataPostFlag", rsItemOrderingMasterData.getString(11));
				obj.put("strPOSCode", rsItemOrderingMasterData.getString(13));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			sql = " select a.* " + " from tblcharactersticsmaster a " + " where a.dteDateEdited >= '" + lastModifiedDate + "' and a.strPOSCode='All' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			rsItemOrderingMasterData = st1.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("strCharCode", rsItemOrderingMasterData.getString(1));
				obj.put("strCharName", rsItemOrderingMasterData.getString(2));
				obj.put("strCharType", rsItemOrderingMasterData.getString(3));
				obj.put("strWSCharCode", rsItemOrderingMasterData.getString(4));
				obj.put("strValue", rsItemOrderingMasterData.getString(5));
				obj.put("strUserCreated", rsItemOrderingMasterData.getString(6));
				obj.put("strUserEdited", rsItemOrderingMasterData.getString(7));
				obj.put("dteDateCreated", rsItemOrderingMasterData.getString(8));
				obj.put("dteDateEdited", rsItemOrderingMasterData.getString(9));
				obj.put("strClientCode", rsItemOrderingMasterData.getString(10));
				obj.put("strDataPostFlag", rsItemOrderingMasterData.getString(11));
				obj.put("strPOSCode", "All");

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			jObj.put("tblcharactersticsmaster", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetItemCharLinkupDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.*,right(c.strPropertyPOSCode,3) " + " from tblitemmaster a,tblitemcharctersticslinkupdtl b,tblposmaster c " + " where a.strItemCode=b.strItemCode and b.strPOSCode=c.strPosCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and c.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by a.dteDateEdited; ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsItemOrderingMasterData = st.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsItemOrderingMasterData.getString(1));
				obj.put("CharCode", rsItemOrderingMasterData.getString(2));
				obj.put("CharValue", rsItemOrderingMasterData.getString(3));
				obj.put("POSCode", rsItemOrderingMasterData.getString(7));
				obj.put("ClientCode", rsItemOrderingMasterData.getString(5));
				obj.put("DataPostFlag", rsItemOrderingMasterData.getString(6));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			sql = " select b.*" + " from tblitemmaster a,tblitemcharctersticslinkupdtl b " + " where a.strItemCode=b.strItemCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and b.strPOSCode='All' " + " order by a.dteDateEdited; ";
			rsItemOrderingMasterData = st1.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsItemOrderingMasterData.getString(1));
				obj.put("CharCode", rsItemOrderingMasterData.getString(2));
				obj.put("CharValue", rsItemOrderingMasterData.getString(3));
				obj.put("POSCode", "All");
				obj.put("ClientCode", rsItemOrderingMasterData.getString(5));
				obj.put("DataPostFlag", rsItemOrderingMasterData.getString(6));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			jObj.put("tblitemcharctersticslinkupdtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetItemMasterLinkupDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.*,right(c.strPropertyPOSCode,3) " + " from tblitemmaster a,tblitemmasterlinkupdtl b,tblposmaster c " + " where a.strItemCode=b.strItemCode and b.strPOSCode=c.strPosCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and c.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by a.dteDateEdited ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsItemMasterLinkUpDtl = st.executeQuery(sql);
			while (rsItemMasterLinkUpDtl.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsItemMasterLinkUpDtl.getString(1));
				obj.put("POSCode", rsItemMasterLinkUpDtl.getString(7));
				obj.put("WSProductCode", rsItemMasterLinkUpDtl.getString(3));
				obj.put("WSProductName", rsItemMasterLinkUpDtl.getString(4));
				obj.put("strExciseBrandCode", rsItemMasterLinkUpDtl.getString(5));
				obj.put("strExciseBrandName", rsItemMasterLinkUpDtl.getString(6));
				obj.put("ClientCode", rsItemMasterLinkUpDtl.getString(7));
				obj.put("DataPostFlag", rsItemMasterLinkUpDtl.getString(8));

				arrObj.put(obj);
			}
			rsItemMasterLinkUpDtl.close();

			sql = " select b.* " + " from tblitemmaster a,tblitemmasterlinkupdtl b " + " where a.strItemCode=b.strItemCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and b.strPOSCode='All' " + " order by a.dteDateEdited ";
			rsItemMasterLinkUpDtl = st1.executeQuery(sql);
			while (rsItemMasterLinkUpDtl.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsItemMasterLinkUpDtl.getString(1));
				obj.put("POSCode", "All");
				obj.put("WSProductCode", rsItemMasterLinkUpDtl.getString(3));
				obj.put("WSProductName", rsItemMasterLinkUpDtl.getString(4));
				obj.put("strExciseBrandCode", rsItemMasterLinkUpDtl.getString(5));
				obj.put("strExciseBrandName", rsItemMasterLinkUpDtl.getString(6));
				obj.put("ClientCode", rsItemMasterLinkUpDtl.getString(7));
				obj.put("DataPostFlag", rsItemMasterLinkUpDtl.getString(8));

				arrObj.put(obj);
			}
			rsItemMasterLinkUpDtl.close();

			jObj.put("tblitemmasterlinkupdtl", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetCharValuesDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.*,right(c.strPropertyPOSCode,3) " + " from tblcharactersticsmaster a,tblcharvalue b,tblposmaster c " + " where a.strCharCode=b.strCharCode and a.strPOSCode=c.strPosCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and c.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by a.dteDateEdited ";
			System.out.println(sql);
			JSONArray arrObj = new JSONArray();
			ResultSet rsItemOrderingMasterData = st.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("CharCode", rsItemOrderingMasterData.getString(1));
				obj.put("CharName", rsItemOrderingMasterData.getString(2));
				obj.put("CharValue", rsItemOrderingMasterData.getString(3));
				obj.put("POSCode", rsItemOrderingMasterData.getString(7));
				obj.put("ClientCode", rsItemOrderingMasterData.getString(4));
				obj.put("DataPostFlag", rsItemOrderingMasterData.getString(6));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			sql = " select b.* " + " from tblcharactersticsmaster a,tblcharvalue b " + " where a.strCharCode=b.strCharCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and a.strPOSCode='All' " + " order by a.dteDateEdited ";
			System.out.println(sql);
			rsItemOrderingMasterData = st1.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("CharCode", rsItemOrderingMasterData.getString(1));
				obj.put("CharName", rsItemOrderingMasterData.getString(2));
				obj.put("CharValue", rsItemOrderingMasterData.getString(3));
				obj.put("POSCode", "All");
				obj.put("ClientCode", rsItemOrderingMasterData.getString(4));
				obj.put("DataPostFlag", rsItemOrderingMasterData.getString(6));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			jObj.put("tblcharvalue", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetCustomerMaster(String masterName, String clientCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			JSONArray arrObj = new JSONArray();
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			String sql = " select * from tblcustomermaster " + " where dteDateEdited >= '" + lastModifiedDate + "' and strClientCode='" + clientCode + "' " + " order by dteDateEdited; ";
			System.out.println(sql);
			ResultSet rsItemOrderingMasterData = st.executeQuery(sql);
			while (rsItemOrderingMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("customerCode", rsItemOrderingMasterData.getString(1));
				obj.put("customerName", rsItemOrderingMasterData.getString(2));
				obj.put("buldingCode", rsItemOrderingMasterData.getString(3));
				obj.put("buldingName", rsItemOrderingMasterData.getString(4));
				obj.put("streetName", rsItemOrderingMasterData.getString(5));
				obj.put("landmark", rsItemOrderingMasterData.getString(6));
				obj.put("area", rsItemOrderingMasterData.getString(7));
				obj.put("city", rsItemOrderingMasterData.getString(8));
				obj.put("state", rsItemOrderingMasterData.getString(9));
				obj.put("pinCode", rsItemOrderingMasterData.getString(10));
				obj.put("mobileNo", rsItemOrderingMasterData.getString(11));
				obj.put("alternateMobileNo", rsItemOrderingMasterData.getString(12));
				obj.put("officeBuildingCode", rsItemOrderingMasterData.getString(13));
				obj.put("officeBuildingName", rsItemOrderingMasterData.getString(14));
				obj.put("officeStreetName", rsItemOrderingMasterData.getString(15));
				obj.put("officeLandmark", rsItemOrderingMasterData.getString(16));
				obj.put("officeArea", rsItemOrderingMasterData.getString(17));
				obj.put("officeCity", rsItemOrderingMasterData.getString(18));
				obj.put("officePinCode", rsItemOrderingMasterData.getString(19));
				obj.put("officeState", rsItemOrderingMasterData.getString(20));
				obj.put("officeNo", rsItemOrderingMasterData.getString(21));
				obj.put("userCreated", rsItemOrderingMasterData.getString(22));
				obj.put("userEdited", rsItemOrderingMasterData.getString(23));
				obj.put("dateCreated", rsItemOrderingMasterData.getString(24));
				obj.put("dateEdited", rsItemOrderingMasterData.getString(25));
				obj.put("dataPostFlag", rsItemOrderingMasterData.getString(26));
				obj.put("clientCode", rsItemOrderingMasterData.getString(27));
				obj.put("officeAddress", rsItemOrderingMasterData.getString(28));
				obj.put("externalCode", rsItemOrderingMasterData.getString(29));
				obj.put("customerType", rsItemOrderingMasterData.getString(30));
				obj.put("dob", rsItemOrderingMasterData.getString(31));
				obj.put("gender", rsItemOrderingMasterData.getString(32));
				obj.put("anniversary", rsItemOrderingMasterData.getString(33));
				obj.put("emailId", rsItemOrderingMasterData.getString(34));
				obj.put("CRMId", rsItemOrderingMasterData.getString(35));
				obj.put("CustAdress", rsItemOrderingMasterData.getString(36));
				obj.put("strTempAddress", rsItemOrderingMasterData.getString(37));
				obj.put("strTempStreet", rsItemOrderingMasterData.getString(38));
				obj.put("strTempLandmark", rsItemOrderingMasterData.getString(39));

				arrObj.put(obj);
			}
			rsItemOrderingMasterData.close();

			jObj.put("tblcustomermaster", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetPosWiseItemWiseIncentiveValuesDtl(String masterName, String propertyPOSCode, String lastModifiedDate)
	{
		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();
			String sql = " select b.*,right(c.strPropertyPOSCode,3)  from tblitemmaster a,tblposwiseitemwiseincentives b,tblposmaster c " + " where a.strItemCode=b.strItemCode and b.strPOSCode=c.strPosCode " + " and b.dteDateEdited >= '" + lastModifiedDate + "' and c.strPropertyPOSCode='" + propertyPOSCode + "' " + " order by b.strItemCode,a.dteDateEdited ";
			System.out.println(sql);

			JSONArray arrObj = new JSONArray();
			ResultSet rsItemWiseIncentiveMasterData = st.executeQuery(sql);
			while (rsItemWiseIncentiveMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("POSCode", rsItemWiseIncentiveMasterData.getString(1));
				obj.put("ItemCode", rsItemWiseIncentiveMasterData.getString(2));
				obj.put("ItemName", rsItemWiseIncentiveMasterData.getString(3));
				obj.put("IncentiveType", rsItemWiseIncentiveMasterData.getString(4));
				obj.put("IncentiveValue", rsItemWiseIncentiveMasterData.getString(5));
				obj.put("ClientCode", rsItemWiseIncentiveMasterData.getString(6));
				obj.put("DataPostFlag", rsItemWiseIncentiveMasterData.getString(7));
				obj.put("DateCreated", rsItemWiseIncentiveMasterData.getString(8));
				obj.put("DateEdited", rsItemWiseIncentiveMasterData.getString(9));

				arrObj.put(obj);
			}
			rsItemWiseIncentiveMasterData.close();

			sql = "select a.* from tblposwiseitemwiseincentives a,tblitemmaster b " + " where a.strItemCode=b.strItemCode and a.dteDateEdited >= '" + lastModifiedDate + "' " + " and a.strPOSCode='All'  order by b.strItemCode,a.dteDateEdited ";
			System.out.println(sql);
			rsItemWiseIncentiveMasterData = st1.executeQuery(sql);
			while (rsItemWiseIncentiveMasterData.next())
			{
				JSONObject obj = new JSONObject();

				obj.put("POSCode", rsItemWiseIncentiveMasterData.getString(1));
				obj.put("ItemCode", rsItemWiseIncentiveMasterData.getString(2));
				obj.put("ItemName", rsItemWiseIncentiveMasterData.getString(3));
				obj.put("IncentiveType", rsItemWiseIncentiveMasterData.getString(4));
				obj.put("IncentiveValue", rsItemWiseIncentiveMasterData.getString(5));
				obj.put("ClientCode", rsItemWiseIncentiveMasterData.getString(6));
				obj.put("DataPostFlag", rsItemWiseIncentiveMasterData.getString(7));
				obj.put("DateCreated", rsItemWiseIncentiveMasterData.getString(8));
				obj.put("DateEdited", rsItemWiseIncentiveMasterData.getString(9));

				arrObj.put(obj);
			}
			rsItemWiseIncentiveMasterData.close();

			jObj.put("tblposwiseitemwiseincentives", arrObj);

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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetMasterDetail(String masterName, String propertyPOSCode, String lastModifiedDate)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master");
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			String sql = "select * from " + masterName + " where dteDateEdited > '" + lastModifiedDate + "' " + "order by dteDateEdited";

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
					if (masterName.equalsIgnoreCase("tblmenuhd"))
					{
						if (i == 12)
						{
							Blob blob = rsMasterData.getBlob(i);
							byte[] imageBytes = blob.getBytes(1, (int) blob.length());

							String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

							// String encodedImage
							// =DatatypeConverter.printBase64Binary(imageBytes);

							obj.put("Column" + i, encodedImage);
						}
						else
						{
							obj.put("Column" + i, rsMasterData.getString(i));
						}
					}
					else if (masterName.equalsIgnoreCase("tblitemmaster"))
					{
						if (i == 38)
						{
							Blob blob = rsMasterData.getBlob(i);
							byte[] imageBytes = blob.getBytes(1, (int) blob.length());

							String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

							obj.put("Column" + i, encodedImage);
						}
						else
						{
							obj.put("Column" + i, rsMasterData.getString(i));
						}
					}
					else
					{
						obj.put("Column" + i, rsMasterData.getString(i));
					}
				}
				arrObj.put(obj);
			}
			rsMasterData.close();

			jObj.put(masterName, arrObj);

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
				// if (null != posCon)
				// {
				// posCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	private String funGetMasterDetail(String masterName, String propertyPOSCode, String lastModifiedDate, long startIndex, long endIndex)
	{

		// //clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try
		{
			// posCon = objDb.funOpenPOSCon("mysql", "master");
			posCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = posCon.createStatement();
			String sql = "select * from " + masterName + " where dteDateEdited > '" + lastModifiedDate + "' " + "order by dteDateEdited limit " + startIndex + "," + endIndex + " ";
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

			jObj.put(masterName, arrObj);

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
				// if (null != posCon)
				// {
				// posCon.close();
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jObj.toString();
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPostMasterData")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostMastersData(JSONObject rootObject)
	{
		String response = "true";
		Iterator keyIterator = rootObject.keys();
		while (keyIterator.hasNext())
		{
			String key = keyIterator.next().toString();
			JSONArray dataArrayObject = null;
			try
			{
				dataArrayObject = (JSONArray) rootObject.get(key);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (key.equalsIgnoreCase("tbldayendprocess") && dataArrayObject != null)
			{
				System.out.println(key);
				if (funInsertDayEndProcessData(dataArrayObject) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			if (key.equalsIgnoreCase("tblcustomermaster") && dataArrayObject != null)
			{
				System.out.println(key);
				if (funInsertCustomerMasterData(dataArrayObject) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			if (key.equalsIgnoreCase("tblbuildingmaster") && dataArrayObject != null)
			{
				System.out.println(key);
				if (funInsertCustomerAreaData(dataArrayObject) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			if (key.equalsIgnoreCase("tblareawisedc") && dataArrayObject != null)
			{
				System.out.println(key);
				if (funInsertDelChargesData(dataArrayObject) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
		}
		return Response.status(201).entity(response).build();
	}

	private int funInsertDayEndProcessData(JSONArray dataArrayObject)
	{
		StringBuilder sbSqlInsert = new StringBuilder();
		StringBuilder sbSqlDelete = new StringBuilder();

		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null, st1 = null;
		String posCode = "";
		String startDate = "";
		String newShiftCode = "";
		String userCreated = "";
		String dateCreated = "";
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			String sqlPOSCode = "select strPOSCode,strPropertyPOSCode from tblposmaster";
			ResultSet rsPOS = st1.executeQuery(sqlPOSCode);
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			sbSqlInsert.setLength(0);
			sbSqlInsert.append("INSERT INTO tbldayendprocess  (strPOSCode, dtePOSDate, strDayEnd, dblTotalSale," + "dblNoOfBill, dblNoOfVoidedBill , dblNoOfModifyBill ,  dblHDAmt, dblDiningAmt," + "dblTakeAway, dblFloat, dblCash, dblAdvance, dblTransferIn, dblTotalReceipt," + "dblPayments,dblWithdrawal,dblTransferOut,dblTotalPay,  dblCashInHand,dblRefund," + "dblTotalDiscount,dblNoOfDiscountedBill, intShiftCode, strShiftEnd,intTotalPax, " + "intNoOfTakeAway,  intNoOfHomeDelivery, strUserCreated, dteDateCreated, dteDayEndDateTime," + "strUserEdited,  strDataPostFlag,intNoOfNCKOT,intNoOfComplimentaryKOT,intNoOfVoidKOT,dblUsedDebitCardBalance" + ",dblUnusedDebitCardBalance,strWSStockAdjustmentNo,dblTipAmt,strExciseBillGeneration)" + "VALUES ");
			JSONObject dataObject = new JSONObject();
			for (int i = 0; i < dataArrayObject.length(); i++)
			{
				dataObject = (JSONObject) dataArrayObject.get(i);
				posCode = dataObject.get("POSCode").toString();
				String posDate = dataObject.get("POSDate").toString();
				String clientCode = dataObject.get("ClientCode").toString();
				String propertyPOSCode = clientCode + "." + posCode;
				posCode = hmPOSMaster.get(propertyPOSCode);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tbldayendprocess " + "where dtePOSDate='" + posDate + "' and strPOSCode='" + posCode + "'");
				st.execute(sbSqlDelete.toString());

				String dayEnd = dataObject.get("DayEnd").toString();
				String totalSale = dataObject.get("TotalSale").toString();
				String noOfBill = dataObject.get("NoOfBill").toString();
				String noOfVoidedBill = dataObject.get("NoOfVoidedBill").toString();
				String noOfModifyBill = dataObject.get("NoOfModifyBill").toString();
				String hdAmount = dataObject.get("HDAmt").toString();
				String diningAmount = dataObject.get("DiningAmt").toString();
				String takeAway = dataObject.get("TakeAway").toString();
				String floated = dataObject.get("Float").toString();
				String cash = dataObject.get("Cash").toString();
				String Advance = dataObject.get("Advance").toString();
				String transferIn = dataObject.get("TransferIn").toString();
				String totalReceipt = dataObject.get("TotalReceipt").toString();
				String payments = dataObject.get("Payments").toString();
				String withdrwal = dataObject.get("Withdrawal").toString();
				String transferOut = dataObject.get("TransferOut").toString();
				String totalPay = dataObject.get("TotalPay").toString();
				String cashInHand = dataObject.get("CashInHand").toString();
				String refund = dataObject.get("Refund").toString();
				String totalDiscount = dataObject.get("TotalDiscount").toString();
				String noOfDiscountedBill = dataObject.get("NoOfDiscountedBill").toString();
				String shiftCode = dataObject.get("ShiftCode").toString();
				String shiftEnd = dataObject.get("ShiftEnd").toString();
				String totalPax = dataObject.get("TotalPax").toString();
				String noOfTakeAway = dataObject.get("NoOfTakeAway").toString();
				String noOfHomeDelivery = dataObject.get("NoOfHomeDelivery").toString();
				userCreated = dataObject.get("UserCreated").toString();
				dateCreated = dataObject.get("DateCreated").toString();
				String dayEndTime = dataObject.get("DayEndDateTime").toString();
				String userEdited = dataObject.get("UserEdited").toString();
				String dataPostFlag = dataObject.get("DataPostFlag").toString();

				String intNoOfNCKOT = dataObject.get("NoOfNCKOT").toString();
				String intNoOfComplimentaryKOT = dataObject.get("NoOfComplimentaryKOT").toString();
				String intNoOfVoidKOT = dataObject.get("NoOfVoidKOT").toString();
				String dblUsedDebitCardBalance = dataObject.get("UsedDebitCardBalance").toString();
				String dblUnusedDebitCardBalance = dataObject.get("UnusedDebitCardBalance").toString();
				String strWSStockAdjustmentNo = dataObject.get("WSStockAdjustmentNo").toString();
				String dblTipAmt = dataObject.get("TipAmt").toString();
				String strExciseBillGeneration = dataObject.get("strExciseBillGeneration").toString();

				startDate = dataObject.get("StartDate").toString();
				newShiftCode = dataObject.get("NewShiftCode").toString();

				if (i == 0)
				{
					sbSqlInsert.append("(");
				}
				else
				{
					sbSqlInsert.append(",(");
				}
				sbSqlInsert.append("'" + posCode + "','" + posDate + "', '" + dayEnd + "', '" + totalSale + "', '" + noOfBill + "', '" + noOfVoidedBill + "', '" + noOfModifyBill + "', " + " '" + hdAmount + "','" + diningAmount + "', '" + takeAway + "', '" + floated + "', '" + cash + "', '" + Advance + "', '" + transferIn + "', '" + totalReceipt + "', '" + payments + "', '" + withdrwal + "', " + " '" + transferOut + "', '" + totalPay + "', '" + cashInHand + "', '" + refund + "', '" + totalDiscount + "', '" + noOfDiscountedBill + "', '" + shiftCode + "', '" + shiftEnd + "', '" + totalPax + "', " + " '" + noOfTakeAway + "', '" + noOfHomeDelivery + "', '" + userCreated + "', '" + dateCreated + "', '" + dayEndTime + "', '" + userEdited + "', '" + dataPostFlag + "'," + " '" + intNoOfNCKOT + "','" + intNoOfComplimentaryKOT + "','" + intNoOfVoidKOT + "','" + dblUsedDebitCardBalance + "','" + dblUnusedDebitCardBalance + "','" + strWSStockAdjustmentNo + "','" + dblTipAmt + "','" + strExciseBillGeneration + "')");
				flgData = true;
			}
			if (flgData)
			{
				// sql=sql.substring(1,sql.length());
				// sqlInsert+=" "+sql;
				sbSqlDelete.setLength(0);
				sbSqlDelete.append(" delete from tbldayendprocess " + " where dtePOSDate='" + startDate + "' and strPOSCode='" + posCode + "'");
				st.execute(sbSqlDelete.toString());

				String sql = " insert into tbldayendprocess(strPOSCode,dtePOSDate,strDayEnd,intShiftCode,strShiftEnd,strUserCreated" + ",dteDateCreated) " + " values('" + posCode + "','" + startDate + "','N'," + newShiftCode + " ,'','" + userCreated + "','" + dateCreated + "')";
				st1.executeUpdate(sql);
				res = st.executeUpdate(sbSqlInsert.toString());

			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSqlDelete = null;
			sbSqlInsert = null;
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertCustomerMasterData(JSONArray dataArrayObject)
	{
		StringBuilder sbSqlInsert = new StringBuilder();
		StringBuilder sbSqlDelete = new StringBuilder();

		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		// String sqlInsert="";
		// String sql="",deleteSql;

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();

			sbSqlInsert.setLength(0);
			sbSqlInsert.append("INSERT INTO tblcustomermaster (strCustomerCode, strCustomerName, strBuldingCode, strBuildingName" + ", strStreetName, strLandmark, strArea, strCity, strState, intPinCode, longMobileNo" + ", longAlternateMobileNo, strOfficeBuildingCode, strOfficeBuildingName, strOfficeStreetName" + ", strOfficeLandmark, strOfficeArea, strOfficeCity,strOfficePinCode, strOfficeState" + ", strOfficeNo, strUserCreated, strUserEdited, dteDateCreated, dteDateEdited, strDataPostFlag, strClientCode,strOfficeAddress" + ", strExternalCode, strCustomerType, dteDOB, strGender,dteAnniversary,strEmailId, strCRMId) VALUES ");
			JSONObject dataObject = new JSONObject();
			for (int i = 0; i < dataArrayObject.length(); i++)
			{
				dataObject = (JSONObject) dataArrayObject.get(i);
				String customerCode = dataObject.get("CustomerCode").toString();
				String clientCode = dataObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblcustomermaster " + "where strCustomerCode='" + customerCode + "' and strClientCode='" + clientCode + "'");
				st.execute(sbSqlDelete.toString());

				String customerName = dataObject.get("CustomerName").toString();
				String buildingCode = dataObject.get("BuldingCode").toString();
				String buildingName = dataObject.get("BuildingName").toString();
				String streetName = dataObject.get("StreetName").toString();
				String landmark = dataObject.get("Landmark").toString();
				String area = dataObject.get("Area").toString();
				String city = dataObject.get("City").toString();
				String state = dataObject.get("State").toString();
				String pinCode = dataObject.get("PinCode").toString();
				String mobileNumber = dataObject.get("MobileNo").toString();
				String alternateMobileNumber = dataObject.get("AlternateMobileNo").toString();
				String officeBuildingCode = dataObject.get("OfficeBuildingCode").toString();
				String officeBuildingName = dataObject.get("OfficeBuildingName").toString();
				String officeStreetName = dataObject.get("OfficeStreetName").toString();
				String officeLandmark = dataObject.get("OfficeLandmark").toString();
				String officeArea = dataObject.get("OfficeArea").toString();
				String officeCity = dataObject.get("OfficeCity").toString();
				String officePinCode = dataObject.get("OfficePinCode").toString();
				String officeState = dataObject.get("OfficeState").toString();
				String officeNo = dataObject.get("OfficeNo").toString();
				String userCreated = dataObject.get("UserCreated").toString();
				String userEdited = dataObject.get("UserEdited").toString();
				String dateCreated = dataObject.get("DateCreated").toString();
				String dateEdited = dataObject.get("DateEdited").toString();
				String dataPostFlag = dataObject.get("DataPostFlag").toString();
				String officeAddress = dataObject.get("OfficeAddress").toString();
				String externalCode = dataObject.get("ExternalCode").toString();
				String customerType = dataObject.get("CustomerType").toString();
				String dob = dataObject.get("DOB").toString();
				String gender = dataObject.get("Gender").toString();
				String anniversary = dataObject.get("Anniversary").toString();
				String eMailId = dataObject.get("EmailId").toString();
				String crmId = dataObject.get("CRMId").toString();

				if (i == 0)
				{
					sbSqlInsert.append("(");
				}
				else
				{
					sbSqlInsert.append(",(");
				}
				sbSqlInsert.append("'" + customerCode + "','" + customerName + "', '" + buildingCode + "', '" + buildingName + "'" + ", '" + streetName + "', '" + landmark + "', '" + area + "', '" + city + "','" + state + "', '" + pinCode + "'" + ", '" + mobileNumber + "', '" + alternateMobileNumber + "', '" + officeBuildingCode + "', '" + officeBuildingName + "'" + ", '" + officeStreetName + "', '" + officeLandmark + "', '" + officeArea + "', '" + officeCity + "'" + ", '" + officePinCode + "', '" + officeState + "', '" + officeNo + "', '" + userCreated + "', '" + userEdited + "'" + ", '" + dateCreated + "', '" + dateEdited + "', '" + dataPostFlag + "', '" + clientCode + "','" + officeAddress + "'" + ", '" + externalCode + "', '" + customerType + "', '" + dob + "', '" + gender + "', '" + anniversary + "','" + eMailId + "', '" + crmId + "')");
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSqlInsert.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSqlDelete = null;
			sbSqlInsert = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertCustomerAreaData(JSONArray dataArrayObject)
	{
		StringBuilder sbSqlInsert = new StringBuilder();
		StringBuilder sbSqlDelete = new StringBuilder();

		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			JSONObject dataObject = new JSONObject();

			sbSqlInsert.setLength(0);
			sbSqlInsert.append("INSERT INTO tblbuildingmaster (strBuildingCode, strBuildingName " + ", strAddress, strUserCreated, strUserEdited, dteDateCreated, dteDateEdited" + ", dblHomeDeliCharge, strClientCode, strDataPostFlag) VALUES ");
			for (int i = 0; i < dataArrayObject.length(); i++)
			{
				dataObject = (JSONObject) dataArrayObject.get(i);
				String buildingCode = dataObject.get("BuildingCode").toString();
				String clientCode = dataObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblbuildingmaster " + " where strBuildingCode='" + buildingCode + "' and strClientCode='" + clientCode + "'");
				st.execute(sbSqlDelete.toString());

				String buildingName = dataObject.get("BuildingName").toString();
				String address = dataObject.get("Address").toString();
				String userCreated = dataObject.get("UserCreated").toString();
				String userEdited = dataObject.get("UserEdited").toString();
				String dateCreated = dataObject.get("DateCreated").toString();
				String dateEdited = dataObject.get("DateEdited").toString();
				String homeDeliCharge = dataObject.get("HomeDeliCharge").toString();
				String dataPostFlag = dataObject.get("DataPostFlag").toString();

				if (i == 0)
				{
					sbSqlInsert.append("(");
				}
				else
				{
					sbSqlInsert.append(",(");
				}
				sbSqlInsert.append("'" + buildingCode + "','" + buildingName + "','" + address + "','" + userCreated + "','" + userEdited + "'," + "'" + dateCreated + "','" + dateEdited + "','" + homeDeliCharge + "','" + clientCode + "','" + dataPostFlag + "' )");
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSqlInsert.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSqlDelete = null;
			sbSqlInsert = null;

			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertDelChargesData(JSONArray dataArrayObject)
	{
		StringBuilder sbSqlInsert = new StringBuilder();
		StringBuilder sbSqlDelete = new StringBuilder();

		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			JSONObject dataObject = new JSONObject();

			sbSqlInsert.setLength(0);
			sbSqlInsert.append("INSERT INTO tblareawisedc (strBuildingCode, dblKilometers, strSymbol,dblBillAmount" + ", dblBillAmount1,dblDeliveryCharges,strUserCreated, strUserEdited, dteDateCreated, dteDateEdited" + ", strClientCode, strDataPostFlag) VALUES ");
			for (int i = 0; i < dataArrayObject.length(); i++)
			{
				dataObject = (JSONObject) dataArrayObject.get(i);
				String buildingCode = dataObject.get("BuildingCode").toString();
				String clientCode = dataObject.get("ClientCode").toString();

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblareawisedc " + " where strBuildingCode='" + buildingCode + "' and strClientCode='" + clientCode + "'");
				st.execute(sbSqlDelete.toString());

				String kilometers = dataObject.get("Kilometers").toString();
				String symbol = dataObject.get("Symbol").toString();
				String billAmt = dataObject.get("BillAmount").toString();
				String billAmt1 = dataObject.get("BillAmount1").toString();
				String delCharges = dataObject.get("DeliveryCharges").toString();
				String userCreated = dataObject.get("UserCreated").toString();
				String userEdited = dataObject.get("UserEdited").toString();
				String dateCreated = dataObject.get("DateCreated").toString();
				String dateEdited = dataObject.get("DateEdited").toString();
				String dataPostFlag = dataObject.get("DataPostFlag").toString();

				if (i == 0)
				{
					sbSqlInsert.append("(");
				}
				else
				{
					sbSqlInsert.append(",(");
				}
				sbSqlInsert.append("'" + buildingCode + "','" + kilometers + "','" + symbol + "','" + billAmt + "','" + billAmt1 + "'" + ",'" + delCharges + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "','" + dateEdited + "'" + ",'" + clientCode + "','" + dataPostFlag + "' )");
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSqlInsert.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSqlInsert = null;
			sbSqlDelete = null;
			try
			{
				if (null != st)
				{
					st.close();
				}
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	@GET
	@Path("/funGetSystemTime")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetSystemTime()
	{
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		String systemTime = "No";
		JSONObject jObj = new JSONObject();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			ResultSet rs = st.executeQuery("select sysdate()");
			if (rs.next())
			{
				systemTime = rs.getString(1);
			}

			jObj.put("SystemTime", systemTime);

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
		finally
		{
			if (null != cmsCon)
			{
				try
				{
					if (null != st)
					{
						st.close();
					}
					// if (null != cmsCon)
					// {
					// cmsCon.close();
					// }
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
			return jObj.toString();
		}
	}

	private int funInsertDayEndDtlData(JSONArray dataArrayObject)
	{
		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null, st1 = null;
		String insert_qry = "";
		String sql = "", deleteSql = "";
		String posCode = "";
		String userCreated = "";
		String dateCreated = "";
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			String sqlPOSCode = "select strPOSCode,strPropertyPOSCode from tblposmaster";
			ResultSet rsPOS = st1.executeQuery(sqlPOSCode);
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			insert_qry = "INSERT INTO tbldayendprocess  (strPOSCode, dtePOSDate, strDayEnd, dblTotalSale,dblNoOfBill" + ", dblNoOfVoidedBill , dblNoOfModifyBill ,  dblHDAmt, dblDiningAmt,dblTakeAway, dblFloat, dblCash" + ", dblAdvance, dblTransferIn, dblTotalReceipt,dblPayments,dblWithdrawal,dblTransferOut,dblTotalPay" + ",dblCashInHand,dblRefund,dblTotalDiscount,dblNoOfDiscountedBill, intShiftCode, strShiftEnd,intTotalPax" + ",intNoOfTakeAway,  intNoOfHomeDelivery, strUserCreated, dteDateCreated, dteDayEndDateTime,strUserEdited" + ",strDataPostFlag,intNoOfNCKOT,intNoOfComplimentaryKOT,intNoOfVoidKOT,dblUsedDebitCardBalance,dblUnusedDebitCardBalance" + ",strWSStockAdjustmentNo,dblTipAmt) VALUES ";

			JSONObject dataObject = new JSONObject();
			for (int i = 0; i < dataArrayObject.length(); i++)
			{
				dataObject = (JSONObject) dataArrayObject.get(i);
				posCode = dataObject.get("POSCode").toString();
				posCode = dataObject.get("POSCode").toString();
				String clientCode = dataObject.get("ClientCode").toString();
				String propertyPOSCode = clientCode + "." + posCode;
				posCode = hmPOSMaster.get(propertyPOSCode);

				String posDate = dataObject.get("POSDate").toString();

				deleteSql = "delete from tbldayendprocess where dtePOSDate='" + posDate + "' and strPOSCode='" + posCode + "'";
				st.execute(deleteSql);
				deleteSql = "delete from tbldayendprocess where strDayEnd='N' and dtePOSDate='" + posDate + "' and strPOSCode='" + posCode + "'";
				st.execute(deleteSql);

				String dayEnd = dataObject.get("DayEnd").toString();
				String totalSale = dataObject.get("TotalSale").toString();
				String noOfBill = dataObject.get("NoOfBill").toString();
				String noOfVoidedBill = dataObject.get("NoOfVoidedBill").toString();
				String noOfModifyBill = dataObject.get("NoOfModifyBill").toString();
				String hdAmount = dataObject.get("HDAmt").toString();
				String diningAmount = dataObject.get("DiningAmt").toString();
				String takeAway = dataObject.get("TakeAway").toString();
				String floated = dataObject.get("Float").toString();
				String cash = dataObject.get("Cash").toString();
				String Advance = dataObject.get("Advance").toString();
				String transferIn = dataObject.get("TransferIn").toString();
				String totalReceipt = dataObject.get("TotalReceipt").toString();
				String payments = dataObject.get("Payments").toString();
				String withdrwal = dataObject.get("Withdrawal").toString();
				String transferOut = dataObject.get("TransferOut").toString();
				String totalPay = dataObject.get("TotalPay").toString();
				String cashInHand = dataObject.get("CashInHand").toString();
				String refund = dataObject.get("Refund").toString();
				String totalDiscount = dataObject.get("TotalDiscount").toString();
				String noOfDiscountedBill = dataObject.get("NoOfDiscountedBill").toString();
				String shiftCode = dataObject.get("ShiftCode").toString();
				String shiftEnd = dataObject.get("ShiftEnd").toString();
				String totalPax = dataObject.get("TotalPax").toString();
				String noOfTakeAway = dataObject.get("NoOfTakeAway").toString();
				String noOfHomeDelivery = dataObject.get("NoOfHomeDelivery").toString();
				userCreated = dataObject.get("UserCreated").toString();
				dateCreated = dataObject.get("DateCreated").toString();
				String dayEndTime = dataObject.get("DayEndDateTime").toString();
				String userEdited = dataObject.get("UserEdited").toString();
				String dataPostFlag = dataObject.get("DataPostFlag").toString();
				String intNoOfNCKOT = dataObject.get("NoOfNCKOT").toString();
				String intNoOfComplimentaryKOT = dataObject.get("NoOfComplimentaryKOT").toString();
				String intNoOfVoidKOT = dataObject.get("NoOfVoidKOT").toString();
				String dblUsedDebitCardBalance = dataObject.get("UsedDebitCardBalance").toString();
				String dblUnusedDebitCardBalance = dataObject.get("UnusedDebitCardBalance").toString();
				String strWSStockAdjustmentNo = dataObject.get("WSStockAdjustmentNo").toString();
				String dblTipAmt = dataObject.get("TipAmt").toString();

				sql += ",('" + posCode + "','" + posDate + "', '" + dayEnd + "', '" + totalSale + "', '" + noOfBill + "'" + ", '" + noOfVoidedBill + "', '" + noOfModifyBill + "', " + " '" + hdAmount + "','" + diningAmount + "'" + ", '" + takeAway + "', '" + floated + "', '" + cash + "', '" + Advance + "', '" + transferIn + "'" + ", '" + totalReceipt + "', '" + payments + "', '" + withdrwal + "', " + " '" + transferOut + "'" + ", '" + totalPay + "', '" + cashInHand + "', '" + refund + "', '" + totalDiscount + "'" + ", '" + noOfDiscountedBill + "', '" + shiftCode + "', '" + shiftEnd + "', '" + totalPax + "'" + ",'" + noOfTakeAway + "', '" + noOfHomeDelivery + "', '" + userCreated + "', '" + dateCreated + "'" + ", '" + dayEndTime + "', '" + userEdited + "', '" + dataPostFlag + "', " + " '" + intNoOfNCKOT + "'" + ",'" + intNoOfComplimentaryKOT + "','" + intNoOfVoidKOT + "','" + dblUsedDebitCardBalance + "'" + ",'" + dblUnusedDebitCardBalance + "','" + strWSStockAdjustmentNo + "','" + dblTipAmt + "' )";
				flgData = true;
			}

			if (flgData)
			{
				sql = sql.substring(1, sql.length());
				insert_qry += " " + sql;
				try
				{
					res = st.executeUpdate(insert_qry);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}
		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertCashManagementData(JSONArray mJsonArray)
	{
		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null;
		Statement st1 = null;
		String insert_qry = "";
		String sql = "", deleteSql = "";
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			sql = "select strPOSCode,strPropertyPOSCode from tblposmaster";
			ResultSet rsPOS = st1.executeQuery(sql);
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();
			sql = "";

			insert_qry = "INSERT INTO tblcashmanagement (`strTransID`,`strTransType`, `dteTransDate`, `strReasonCode`" + ",`strPOSCode`, `dblAmount`,`strRemarks`,`strUserCreated`, `strUserEdited`, `dteDateCreated`" + ",`dteDateEdited`, `strCurrencyType`,`intShiftCode`, `strAgainst`,`dblRollingAmt`,`strClientCode`" + ",`strDataPostFlag` ) " + "VALUES";
			JSONObject mJsonObject = new JSONObject();

			for (int i = 0; i < mJsonArray.length(); i++)
			{
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String transId = mJsonObject.get("TransId").toString().trim();
				String clientCode = mJsonObject.get("ClientCode").toString().trim();
				String POSCode = mJsonObject.get("POSCode").toString().trim();

				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				deleteSql = "delete from tblcashmanagement " + "where strTransId='" + transId + "' and strClientCode='" + clientCode + "' and strPOSCode='" + POSCode + "' ";
				st.executeUpdate(deleteSql);

				String transType = mJsonObject.get("TransType").toString();
				String transDate = mJsonObject.get("TransDate").toString();
				String reasonCode = mJsonObject.get("ReasonCode").toString();
				double amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				String remarks = mJsonObject.get("Remarks").toString();
				String userCreated = mJsonObject.get("UserCreated").toString();
				String userEdited = mJsonObject.get("UserEdited").toString();
				String dateCreated = mJsonObject.get("DateCreated").toString();
				String dateEdited = mJsonObject.get("DateEdited").toString();
				String currencyType = mJsonObject.get("CurrencyType").toString();
				String shiftCode = mJsonObject.get("ShiftCode").toString();
				String against = mJsonObject.get("Against").toString();
				double rollingAmount = Double.parseDouble(mJsonObject.get("RollingAmt").toString());
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				sql += ",('" + transId + "','" + transType + "','" + transDate + "','" + reasonCode + "','" + POSCode + "'," + "'" + amount + "','" + remarks + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "'," + "'" + dateEdited + "','" + currencyType + "','" + shiftCode + "','" + against + "','" + rollingAmount + "'," + "'" + clientCode + "','" + dataPostFlag + "')";
				flgData = true;
			}

			if (flgData)
			{
				sql = sql.substring(1, sql.length());
				insert_qry += " " + sql;
				res = st.executeUpdate(insert_qry);
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPostPropertySetup")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostPropertySetup(JSONObject rootObject)
	{
		String response = "false";

		Iterator keyIterator = rootObject.keys();
		while (keyIterator.hasNext())
		{
			String key = keyIterator.next().toString();
			JSONArray dataArrayObject = null;
			try
			{
				dataArrayObject = (JSONArray) rootObject.get(key);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (key.equalsIgnoreCase("tblsetup") && dataArrayObject != null)
			{
				if (funInsertPropertySetupData(dataArrayObject) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("tblbillseries") && dataArrayObject != null)
			{
				if (funInsertBillSeriesData(dataArrayObject) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
		}

		return Response.status(201).entity(response).build();
	}

	private int funInsertPropertySetupData(JSONArray dataArrayObject)
	{
		StringBuilder sbSqlInsert = new StringBuilder();
		StringBuilder sbSqlDelete = new StringBuilder();

		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null, st1 = null;
		Map<String, String> hmPOSMaster = new HashMap<String, String>();
		String hoClientCode = "";
		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			String sqlPOSCode = "select strPOSCode,strPropertyPOSCode from tblposmaster";
			ResultSet rsPOS = st1.executeQuery(sqlPOSCode);
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			sbSqlInsert.setLength(0);
			sbSqlInsert.append("insert into tblsetup(strClientCode,strClientName,strAddressLine1,strAddressLine2 "// 4
					+ ",strAddressLine3,strEmail,strBillFooter,strBillFooterStatus,intBillPaperSize "// 9
					+ ",strNegativeBilling,strDayEnd,strPrintMode,strDiscountNote,strCityName "// 14
					+ ",strState,strCountry,intTelephoneNo,dteStartDate,dteEndDate "// 19
					+ ",strNatureOfBusinnes,strMultipleBillPrinting,strEnableKOT,strEffectOnPSP,strPrintVatNo "// 24
					+ ",strVatNo,strShowBill,strPrintServiceTaxNo,strServiceTaxNo,strManualBillNo "// 29
					+ ",strMenuItemDispSeq,strSenderEmailId,strEmailPassword,strConfirmEmailPassword,strBody "// 34
					+ ",strEmailServerName,strSMSApi,strUserCreated,strUserEdited,dteDateCreated "// 39
					+ ",dteDateEdited ,strPOSType,strWebServiceLink,strDataSendFrequency,dteHOServerDate "// 44
					+ ",strRFID,strServerName,strDBUserName,strDBPassword,strDatabaseName "// 49
					+ ",strEnableKOTForDirectBiller,intPinCode,strChangeTheme,dblMaxDiscount,strAreaWisePricing "// 54
					+ ",strMenuItemSortingOn,strDirectAreaCode,intColumnSize,strPrintType,strEditHomeDelivery "// 59
					+ ",strSlabBasedHDCharges,strSkipWaiterAndPax,strSkipWaiter,strDirectKOTPrintMakeKOT,strSkipPax "// 64
					+ ",strCRMInterface,strGetWebserviceURL,strPostWebserviceURL,strOutletUID,strPOSID "// 69
					+ ",strStockInOption,longCustSeries,intAdvReceiptPrintCount,strHomeDeliverySMS,strBillStettlementSMS "// 74
					+ ",strBillFormatType,strActivePromotions,strSendHomeDelSMS,strSendBillSettlementSMS,strSMSType "// 79
					+ ",strPrintShortNameOnKOT,strShowCustHelp,strPrintOnVoidBill,strPostSalesDataToMMS,strCustAreaMasterCompulsory "// 84
					+ ",strPriceFrom,strShowPrinterErrorMessage,strTouchScreenMode,strCardInterfaceType,strCMSIntegrationYN "// 89
					+ ",strCMSWebServiceURL,strChangeQtyForExternalCode,strPointsOnBillPrint,strCMSPOSCode,strManualAdvOrderNoCompulsory "// 94
					+ ",strPrintManualAdvOrderNoOnBill,strPrintModifierQtyOnKOT,strNoOfLinesInKOTPrint,strMultipleKOTPrintYN,strItemQtyNumpad "// 99
					+ ",strTreatMemberAsTable,strKOTToLocalPrinter,blobReportImage,strSettleBtnForDirectBillerBill,strDelBoySelCompulsoryOnDirectBiller "// 104
					+ ",strCMSMemberForKOTJPOS,strCMSMemberForKOTMPOS,strDontShowAdvOrderInOtherPOS,strPrintZeroAmtModifierInBill,strPrintKOTYN "// 109
					+ ",strCreditCardSlipNoCompulsoryYN,strCreditCardExpiryDateCompulsoryYN,strSelectWaiterFromCardSwipe,strMultiWaiterSelectionOnMakeKOT,strMoveTableToOtherPOS "// 114
					+ ",strMoveKOTToOtherPOS,strCalculateTaxOnMakeKOT,strReceiverEmailId,strCalculateDiscItemWise,strTakewayCustomerSelection "// 119
					+ ",StrShowItemStkColumnInDB,strItemType,strAllowNewAreaMasterFromCustMaster,strCustAddressSelectionForBill,strGenrateMI "// 124
					+ ",strFTPAddress,strFTPServerUserName,strFTPServerPass,strAllowToCalculateItemWeight,strShowBillsDtlType "// 129
					+ ",strPrintTaxInvoiceOnBill,strPrintInclusiveOfAllTaxesOnBill,strApplyDiscountOn,strMemberCodeForKotInMposByCardSwipe,strPrintBillYN "// 134
					+ ",strVatAndServiceTaxFromPos,strMemberCodeForMakeBillInMPOS,strItemWiseKOTYN,strLastPOSForDayEnd,strCMSPostingType "// 139
					+ ",strPopUpToApplyPromotionsOnBill,strSelectCustomerCodeFromCardSwipe,strCheckDebitCardBalOnTransactions,strSettlementsFromPOSMaster,strShiftWiseDayEndYN "// 144
					+ ",strProductionLinkup,strLockDataOnShift,strWSClientCode,strPOSCode,strEnableBillSeries" // 149
					+ ",strEnablePMSIntegrationYN,strPrintTimeOnBill,strPrintTDHItemsInBill,strPrintRemarkAndReasonForReprint" // 153
					+ ",intDaysBeforeOrderToCancel,intNoOfDelDaysForAdvOrder,intNoOfDelDaysForUrgentOrder,strSetUpToTimeForAdvOrder" // 157
					+ ",strSetUpToTimeForUrgentOrder,strUpToTimeForAdvOrder,strUpToTimeForUrgentOrder,strEnableBothPrintAndSettleBtnForDB" // 161
					+ ",strInrestoPOSIntegrationYN,strInrestoPOSWebServiceURL,strInrestoPOSId,strInrestoPOSKey" // 165
					+ ",strCarryForwardFloatAmtToNextDay,strOpenCashDrawerAfterBillPrintYN,strPropertyWiseSalesOrderYN,strDataPostFlag,strShowItemDetailsGrid"
					+ ",strShowPopUpForNextItemQuantity,strJioMoneyIntegration,strJioWebServiceUrl "// 173
					+ ",strJioMID,strJioTID,strJioActivationCode,strJioDeviceID,strNewBillSeriesForNewDay,strShowReportsPOSWise,strEnableDineIn,strAutoAreaSelectionInMakeKOT "// 181
					+ ",strConsolidatedKOTPrinterPort,dblRoundOff,strShowUnSettlementForm,strPrintOpenItemsOnBill,strPrintHomeDeliveryYN,strScanQRYN,strAreaWisePromotions"// 188
					+ ",strPrintItemsOnMoveKOTMoveTable,strShowPurRateInDirectBiller "// 190
					+ ",strEnableTableReservationForCustomer,strAutoShowPopItems,intShowPopItemsOfDays,strPostSalesCostOrLoc,strEffectOfSales  "// 195
					+ ",strPOSWiseItemToMMSProductLinkUpYN,strEnableMasterDiscount,strEnableNFCInterface,strBenowIntegrationYN,strXEmail  "// 200
					+ ",strMerchantCode,strAuthenticationKey,strSalt,strEnableLockTable,strHomeDeliveryAreaForDirectBiller  "// 205
					+ ",strTakeAwayAreaForDirectBiller,strRoundOffBillFinalAmt,dblNoOfDecimalPlace,strSendDBBackupOnClientMail,strPrintOrderNoOnBillYN,strPrintDeviceAndUserDtlOnKOTYN "//211
					+ ",strRemoveSCTaxCode,strAutoAddKOTToBill,strAreaWiseCostCenterKOTPrintingYN,strWERAOnlineOrderIntegration,strWERAMerchantOutletId,strWERAAuthenticationAPIKey"//217
					+ ",strFireCommunication,dblUSDConverionRate,strDBBackupMailReceiver,strPrintMoveTableMoveKOTYN,strPrintQtyTotal"//222
					+ ",strShowReportsInCurrency,strPOSToMMSPostingCurrency,strPOSToWebBooksPostingCurrency,strLockTableForWaiter,strReprintOnSettleBill,strTableReservationSMS,strSendTableReservationSMS) "// 229
					+ "values  ");

			JSONObject dataObject = new JSONObject();
			for (int i = 0; i < dataArrayObject.length(); i++)
			{
				dataObject = (JSONObject) dataArrayObject.get(i);

				String POSCode = dataObject.get("strPOSCode").toString();
				String clientClientCode = dataObject.get("strClientCode").toString();
				hoClientCode = dataObject.get("strWSClientCode").toString();
				String propertyPOSCode = clientClientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				if (!hoClientCode.trim().isEmpty())
				{
					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblsetup " + "where strClientCode='" + hoClientCode + "' and strPOSCode='" + POSCode + "'");
					st.execute(sbSqlDelete.toString());

					String clientName = dataObject.get("strClientName").toString().trim(); // 1
					String address1 = dataObject.get("strAddressLine1").toString().trim(); // 2
					String address2 = dataObject.get("strAddressLine2").toString().trim(); // 3
					String address3 = dataObject.get("strAddressLine3").toString().trim(); // 4
					String email = dataObject.get("strEmail").toString().trim(); // 5
					String billFooter = dataObject.get("strBillFooter").toString().trim(); // 6
					String billFooterStatus = dataObject.get("strBillFooterStatus").toString().trim(); // 7
					String billPaperSize = dataObject.get("intBillPaperSize").toString().trim(); // 8
					String negativeBilling = dataObject.get("strNegativeBilling").toString().trim(); // 9
					String dayEnd = dataObject.get("strDayEnd").toString().trim(); // 10
					String printMode = dataObject.get("strPrintMode").toString().trim(); // 11
					String discountNote = dataObject.get("strDiscountNote").toString().trim(); // 12
					String city = dataObject.get("strCityName").toString().trim(); // 13
					String state = dataObject.get("strState").toString().trim(); // 14
					String country = dataObject.get("strCountry").toString().trim(); // 15
					String telephoneNo = dataObject.get("intTelephoneNo").toString().trim(); // 16
					String startDate = dataObject.get("dteStartDate").toString().trim(); // 17
					String endDate = dataObject.get("dteEndDate").toString().trim(); // 18
					String natureOfBusiness = dataObject.get("strNatureOfBusinnes").toString().trim(); // 19
					String multipleBillPrinting = dataObject.get("strMultipleBillPrinting").toString().trim(); // 20
					String enableKOT = dataObject.get("strEnableKOT").toString().trim(); // 21
					String effectOnPSP = dataObject.get("strEffectOnPSP").toString().trim(); // 22
					String printVatNo = dataObject.get("strPrintVatNo").toString().trim(); // 23
					String vatNo = dataObject.get("strVatNo").toString().trim(); // 24
					String showBill = dataObject.get("strShowBill").toString().trim(); // 25
					String printServiceTaxNo = dataObject.get("strPrintServiceTaxNo").toString().trim(); // 26
					String serviceTaxNo = dataObject.get("strServiceTaxNo").toString().trim(); // 27
					String manualBillNo = dataObject.get("strManualBillNo").toString().trim(); // 28
					String menuItemDisplaySeq = dataObject.get("strMenuItemDispSeq").toString().trim(); // 29
					String senderEmailId = dataObject.get("strSenderEmailId").toString().trim(); // 30

					String emailPassword = dataObject.get("strEmailPassword").toString().trim(); // 31
					String confirmEmailPassword = dataObject.get("strConfirmEmailPassword").toString().trim(); // 32
					String body = dataObject.get("strBody").toString().trim(); // 33
					String emailServerName = dataObject.get("strEmailServerName").toString().trim(); // 34
					String smsApi = dataObject.get("strSMSApi").toString().trim(); // 35
					String userCreated = dataObject.get("strUserCreated").toString().trim(); // 36
					String userEdited = dataObject.get("strUserEdited").toString().trim(); // 37
					String dateCreated = dataObject.get("dteDateCreated").toString().trim(); // 38
					String dateEdited = dataObject.get("dteDateEdited").toString().trim(); // 39
					String POSType = "HOPOS"; // 40
					String webServiceLink = dataObject.get("strWebServiceLink").toString().trim(); // 41
					String dataSendFrequency = dataObject.get("strDataSendFrequency").toString().trim(); // 42
					String HOServerDate = dataObject.get("dteHOServerDate").toString().trim(); // 43
					String RFID = dataObject.get("strRFID").toString().trim(); // 44
					String sqlServerName = dataObject.get("strServerName").toString().trim(); // 45
					String sqlUserName = dataObject.get("strDBUserName").toString().trim(); // 46
					String sqlPassword = dataObject.get("strDBPassword").toString().trim(); // 47
					String sqlDBName = dataObject.get("strDatabaseName").toString().trim(); // 48
					String enableKOTForDirectBiller = dataObject.get("strEnableKOTForDirectBiller").toString().trim(); // 49
					String pinCode = dataObject.get("intPinCode").toString().trim(); // 50
					String changeTheme = dataObject.get("strChangeTheme").toString().trim(); // 51
					String maxDiscount = dataObject.get("dblMaxDiscount").toString().trim(); // 52
					String areaWisePricing = dataObject.get("strAreaWisePricing").toString().trim(); // 53
					String menuItemSortingOn = dataObject.get("strMenuItemSortingOn").toString().trim(); // 54
					String directBillerAreaCode = dataObject.get("strDirectAreaCode").toString().trim(); // 55
					String columnSize = dataObject.get("intColumnSize").toString().trim(); // 56
					String printType = dataObject.get("strPrintType").toString().trim(); // 57
					String editHomeDel = dataObject.get("strEditHomeDelivery").toString().trim(); // 58
					String slabBasedHDChanrges = dataObject.get("strSlabBasedHDCharges").toString().trim(); // 59
					String skipWaiterAndPax = dataObject.get("strSkipWaiterAndPax").toString().trim(); // 60

					String skipWaiter = dataObject.get("strSkipWaiter").toString().trim(); // 61
					String directKOTPrintFromMakeKOT = dataObject.get("strDirectKOTPrintMakeKOT").toString().trim(); // 62
					String skipPax = dataObject.get("strSkipPax").toString().trim(); // 63
					String CRMInterface = dataObject.get("strCRMInterface").toString().trim(); // 64
					String getWebServiceURL = dataObject.get("strGetWebserviceURL").toString().trim(); // 65
					String postWebServiceURL = dataObject.get("strPostWebserviceURL").toString().trim(); // 66
					String outletUID = dataObject.get("strOutletUID").toString().trim(); // 67 
					String POSID = dataObject.get("strPOSID").toString().trim(); // 68
					String stockInOption = dataObject.get("strStockInOption").toString().trim(); // 69
					String customerSeries = dataObject.get("longCustSeries").toString().trim(); // 70
					String advRecPrintCount = dataObject.get("intAdvReceiptPrintCount").toString().trim(); // 71
					String homeDelSMS = dataObject.get("strHomeDeliverySMS").toString().trim(); // 72
					String billSettlementSMS = dataObject.get("strBillStettlementSMS").toString().trim(); // 73
					String billFormatType = dataObject.get("strBillFormatType").toString().trim(); // 74
					String activePromotions = dataObject.get("strActivePromotions").toString().trim(); // 75
					String sendHomeDelSMS = dataObject.get("strSendHomeDelSMS").toString().trim(); // 76
					String sendBillSettlmentSMS = dataObject.get("strSendBillSettlementSMS").toString().trim(); // 77
					String SMSType = dataObject.get("strSMSType").toString().trim(); // 78
					String printShortNameOnKOT = dataObject.get("strPrintShortNameOnKOT").toString().trim(); // 79
					String showCustHelp = dataObject.get("strShowCustHelp").toString().trim(); // 80
					String printVoid = dataObject.get("strPrintOnVoidBill").toString().trim(); // 81
					String postSalesDataToMMS = dataObject.get("strPostSalesDataToMMS").toString().trim(); // 82
					String custAreaMasterCompulsory = dataObject.get("strCustAreaMasterCompulsory").toString().trim();// 83
					String priceFrom = dataObject.get("strPriceFrom").toString().trim(); // 84
					String showPrinterErrorMsg = dataObject.get("strShowPrinterErrorMessage").toString().trim(); // 85
					String touchScreenMode = dataObject.get("strTouchScreenMode").toString().trim(); // 86
					String cardInterfaceType = dataObject.get("strCardInterfaceType").toString().trim(); // 87
					String CMSIntegrationYN = dataObject.get("strCMSIntegrationYN").toString().trim(); // 88
					String CMSWebServiceURL = dataObject.get("strCMSWebServiceURL").toString().trim(); // 89
					String changeQtyForExtCode = dataObject.get("strChangeQtyForExternalCode").toString().trim(); // 90

					String pointsOnBillPrint = dataObject.get("strPointsOnBillPrint").toString().trim(); // 91
					String CMSPOSCode = dataObject.get("strCMSPOSCode").toString().trim(); // 92
					String manualAdvOrderCompulsory = dataObject.get("strManualAdvOrderNoCompulsory").toString().trim(); // 93
					String printManualAdvOrderOnBill = dataObject.get("strPrintManualAdvOrderNoOnBill").toString().trim();// 94
					String printModifierQtyOnKOT = dataObject.get("strPrintModifierQtyOnKOT").toString().trim(); // 95
					String noOfLinesInKOTPrint = dataObject.get("strNoOfLinesInKOTPrint").toString().trim(); // 96
					String multipleKOTPrintYN = dataObject.get("strMultipleKOTPrintYN").toString().trim(); // 97
					String itemQtyPad = dataObject.get("strItemQtyNumpad").toString().trim(); // 98
					String treatMemberAsTable = dataObject.get("strTreatMemberAsTable").toString().trim(); // 99
					String KOTToLocalPrinter = dataObject.get("strKOTToLocalPrinter").toString().trim(); // 100
					// String
					// reportImage=dataObject.get("blobReportImage").toString().trim();
					// //101
					String reportImage = null; // 101
					String settleBtnForDirectBillerBill = dataObject.get("strSettleBtnForDirectBillerBill").toString().trim(); // 102
					String delBoyCompulsoryOnDirectBiller = dataObject.get("strDelBoySelCompulsoryOnDirectBiller").toString().trim(); // 103
					String CMSMemberForJPOSKOT = dataObject.get("strCMSMemberForKOTJPOS").toString().trim(); // 104
					String CMSMemberForMPOSKOT = dataObject.get("strCMSMemberForKOTMPOS").toString().trim(); // 105
					String dontShowAdvOrderInOtherPOS = dataObject.get("strDontShowAdvOrderInOtherPOS").toString().trim(); // 106
					String printZeroAmtModifierInBill = dataObject.get("strPrintZeroAmtModifierInBill").toString().trim(); // 107
					String printKOTYN = dataObject.get("strPrintKOTYN").toString().trim(); // 108
					String creditCardSlipNoCompulsoryYN = dataObject.get("strCreditCardSlipNoCompulsoryYN").toString().trim();// 109
					String creditCardExpDateCompulsoryYN = dataObject.get("strCreditCardExpiryDateCompulsoryYN").toString().trim(); // 110
					String selectWaiterFromCardSwipe = dataObject.get("strSelectWaiterFromCardSwipe").toString().trim(); // 111
					String multipleWaiterSelOnMakeKOT = dataObject.get("strMultiWaiterSelectionOnMakeKOT").toString().trim(); // 112
					String moveTableToOtherPOS = dataObject.get("strMoveTableToOtherPOS").toString().trim(); // 113
					String moveKOTToOtherPOS = dataObject.get("strMoveKOTToOtherPOS").toString().trim(); // 114
					String calTaxOnMakeKOT = dataObject.get("strCalculateTaxOnMakeKOT").toString().trim(); // 115
					String receiverEmailID = dataObject.get("strReceiverEmailId").toString().trim(); // 116
					String calItemWiseDiscount = dataObject.get("strCalculateDiscItemWise").toString().trim(); // 117
					String takeAwayCustSelection = dataObject.get("strTakewayCustomerSelection").toString().trim(); // 118
					String showItemStkColInDB = dataObject.get("StrShowItemStkColumnInDB").toString().trim(); // 119
					String itemType = dataObject.get("strItemType").toString().trim(); // 120

					String allowNewAreaMasterFromCustMaster = dataObject.get("strAllowNewAreaMasterFromCustMaster").toString().trim(); // 121
					String custAddressSelForBill = dataObject.get("strCustAddressSelectionForBill").toString().trim(); // 122
					String generateMI = dataObject.get("strGenrateMI").toString().trim(); // 123
					String ftpAddress = dataObject.get("strFTPAddress").toString().trim(); // 124
					String ftpServerName = dataObject.get("strFTPServerUserName").toString().trim(); // 125
					String ftpServerPass = dataObject.get("strFTPServerPass").toString().trim(); // 126
					String allowToCalItemWeight = dataObject.get("strAllowToCalculateItemWeight").toString().trim(); // 127
					String showBillsDtlType = dataObject.get("strShowBillsDtlType").toString().trim(); // 128
					String printTaxInvoiceOnBill = dataObject.get("strPrintTaxInvoiceOnBill").toString().trim(); // 129
					String printIncOfAllTaxesOnBill = dataObject.get("strPrintInclusiveOfAllTaxesOnBill").toString().trim(); // 130
					String applyDiscOn = dataObject.get("strApplyDiscountOn").toString().trim(); // 131
					String memberCodeForKOTInMPOSByCardSwipe = dataObject.get("strMemberCodeForKotInMposByCardSwipe").toString().trim(); // 132
					String printBillYN = dataObject.get("strPrintBillYN").toString().trim(); // 133
					String vatAndServiceTaxFromPOS = dataObject.get("strVatAndServiceTaxFromPos").toString().trim(); // 134
					String memberCodeForMakeBillInMPOS = dataObject.get("strMemberCodeForMakeBillInMPOS").toString().trim(); // 135
					String itemWiseKOTYN = dataObject.get("strItemWiseKOTYN").toString().trim(); // 136
					String lastPOSForDayEnd = dataObject.get("strLastPOSForDayEnd").toString().trim(); // 137
					String CMSPostingType = dataObject.get("strCMSPostingType").toString().trim(); // 138
					String popUpToApplyPromotionsOnBill = dataObject.get("strPopUpToApplyPromotionsOnBill").toString().trim();// 139
					String selectCustCodeFromCardSwipe = dataObject.get("strSelectCustomerCodeFromCardSwipe").toString().trim();// 140
					String chkDebitCardBalFromCardSwipe = dataObject.get("strCheckDebitCardBalOnTransactions").toString().trim();// 141
					String pickSettlementsFromPOSMaster = dataObject.get("strSettlementsFromPOSMaster").toString().trim(); // 142
					String shiftWiseDayEndYN = dataObject.get("strShiftWiseDayEndYN").toString().trim(); // 143
					String productionLinkup = dataObject.get("strProductionLinkup").toString().trim(); // 144
					String lockDataOnShift = dataObject.get("strLockDataOnShift").toString().trim(); // 145
					String wsClientCode = dataObject.get("strWSClientCode").toString().trim(); // 146
					String enableBillSeries = dataObject.get("strEnableBillSeries").toString().trim(); // 147
					String enablePMSIntegration = dataObject.get("strEnablePMSIntegrationYN").toString().trim(); // 150
					String printTimeOnBill = dataObject.get("strPrintTimeOnBill").toString().trim(); // 151
					String printTDHItemsOnBill = dataObject.get("strPrintTDHItemsInBill").toString().trim(); // 152

					String printRemarksAndReasonForReprint = dataObject.get("strPrintRemarkAndReasonForReprint").toString().trim(); // 153
					String daysBeforeOrderToCancel = dataObject.get("intDaysBeforeOrderToCancel").toString().trim(); // 154
					String noOfDelDaysForAdvOrder = dataObject.get("intNoOfDelDaysForAdvOrder").toString().trim(); // 155
					String noOfDelDaysForUrgentOrder = dataObject.get("intNoOfDelDaysForUrgentOrder").toString().trim(); // 156
					String setUpToTimeForAdvOrder = dataObject.get("strSetUpToTimeForAdvOrder").toString().trim(); // 157
					String setUpToTimeForUrgentOrder = dataObject.get("strSetUpToTimeForUrgentOrder").toString().trim(); // 158
					String upToTimeForAdvOrder = dataObject.get("strUpToTimeForAdvOrder").toString().trim(); // 159
					String upToTimeForUrgentOrder = dataObject.get("strUpToTimeForUrgentOrder").toString().trim(); // 160
					String enableBothPrintAndSettleForDB = dataObject.get("strEnableBothPrintAndSettleBtnForDB").toString().trim(); // 161
					String inrestoPOSIntegration = dataObject.get("strInrestoPOSIntegrationYN").toString().trim(); // 162
					String inrestoPOSWebServiceURL = dataObject.get("strInrestoPOSWebServiceURL").toString().trim(); // 163
					String inrestoPOSID = dataObject.get("strInrestoPOSId").toString().trim(); // 164
					String inrestoPOSKey = dataObject.get("strInrestoPOSKey").toString().trim(); // 165
					String carryFwdFloatAmtToNextDay = dataObject.get("strCarryForwardFloatAmtToNextDay").toString().trim(); // 166
					String openCashDrawerAfterBillPrint = dataObject.get("strOpenCashDrawerAfterBillPrintYN").toString().trim(); // 167
					String propertyWiseSalesOrder = dataObject.get("strPropertyWiseSalesOrderYN").toString().trim(); // 168
					String dataPostFlag = dataObject.get("strDataPostFlag").toString().trim(); // 169
					String showItemDetailGrid = dataObject.get("strShowItemDetailsGrid").toString().trim(); // 170
					String ShowPopUpForNextItemQuantity = dataObject.get("strShowPopUpForNextItemQuantity").toString().trim();// 171
					String strJioMoneyIntegration = dataObject.get("strJioMoneyIntegration").toString().trim();// 172
					String strJioWebServiceUrl = dataObject.get("strJioWebServiceUrl").toString().trim();// 173

					String strJioMID = dataObject.get("strJioMID").toString().trim();// 174
					String strJioTID = dataObject.get("strJioTID").toString().trim();// 175
					String strJioActivationCode = dataObject.get("strJioActivationCode").toString().trim();// 176
					String strJioDeviceID = dataObject.get("strJioDeviceID").toString().trim();// 177
					String strNewBillSeriesForNewDay = dataObject.get("strNewBillSeriesForNewDay").toString().trim();// 178
					String strShowReportsPOSWise = dataObject.get("strShowReportsPOSWise").toString().trim();// 179
					String strEnableDineIn = dataObject.get("strEnableDineIn").toString().trim();// 180
					String strAutoAreaSelectionInMakeKOT = dataObject.get("strAutoAreaSelectionInMakeKOT").toString().trim();// 181
					String strConsolidatedKOTPrinterPort = dataObject.get("strConsolidatedKOTPrinterPort").toString().trim();// 182
					String dblRoundOff = dataObject.get("dblRoundOff").toString().trim();// 183
					String strShowUnSettlementForm = dataObject.get("strShowUnSettlementForm").toString().trim();// 184
					String strPrintOpenItemsOnBill = dataObject.get("strPrintOpenItemsOnBill").toString().trim();// 185
					String strPrintHomeDeliveryYN = dataObject.get("strPrintHomeDeliveryYN").toString().trim();// 186
					String strScanQRYN = dataObject.get("strScanQRYN").toString().trim();// 187
					String strAreaWisePromotions = dataObject.get("strAreaWisePromotions").toString().trim();// 188
					String strPrintItemsOnMoveKOTMoveTable = dataObject.get("strPrintItemsOnMoveKOTMoveTable").toString().trim();// 189
					String strShowPurRateInDirectBiller = dataObject.get("strShowPurRateInDirectBiller").toString().trim();// 190

					String strEnableTableReservationForCustomer = dataObject.get("strEnableTableReservationForCustomer").toString().trim();// 191
					String strAutoShowPopItems = dataObject.get("strAutoShowPopItems").toString().trim();// 192
					String intShowPopItemsOfDays = dataObject.get("intShowPopItemsOfDays").toString().trim();// 193
					String strPostSalesCostOrLoc = dataObject.get("strPostSalesCostOrLoc").toString().trim();// 194
					String strEffectOfSales = dataObject.get("strEffectOfSales").toString().trim();// 195
					String strPOSWiseItemToMMSProductLinkUpYN = dataObject.get("strPOSWiseItemToMMSProductLinkUpYN").toString().trim();// 196
					String strEnableMasterDiscount = dataObject.get("strEnableMasterDiscount").toString().trim();// 197
					String strEnableNFCInterface = dataObject.get("strEnableNFCInterface").toString().trim();// 198
					String strBenowIntegrationYN = dataObject.get("strBenowIntegrationYN").toString().trim();// 199
					String strXEmail = dataObject.get("strXEmail").toString().trim();// 200
					String strMerchantCode = dataObject.get("strMerchantCode").toString().trim();// 201
					String strAuthenticationKey = dataObject.get("strAuthenticationKey").toString().trim();// 202
					String strSalt = dataObject.get("strSalt").toString().trim();// 203
					String strEnableLockTable = dataObject.get("strEnableLockTable").toString().trim();// 204
					String strHomeDeliveryAreaForDirectBiller = dataObject.get("strHomeDeliveryAreaForDirectBiller").toString().trim();// 205
					String strTakeAwayAreaForDirectBiller = dataObject.get("strTakeAwayAreaForDirectBiller").toString().trim();// 206
					String strRoundOffBillFinalAmt = dataObject.get("strRoundOffBillFinalAmt").toString().trim();// 207
					String dblNoOfDecimalPlace = dataObject.get("dblNoOfDecimalPlace").toString().trim();// 208
					String strSendDBBackupOnClientMail = dataObject.get("strSendDBBackupOnClientMail").toString().trim();// 209
					String strPrintOrderNoOnBillYN = dataObject.get("strPrintOrderNoOnBillYN").toString().trim();// 210
					String strPrintDeviceAndUserDtlOnKOTYN = dataObject.get("strPrintDeviceAndUserDtlOnKOTYN").toString().trim();// 211
					String strRemoveSCTaxCode = dataObject.get("strRemoveSCTaxCode").toString().trim();// 212
					String strAutoAddKOTToBill = dataObject.get("strAutoAddKOTToBill").toString().trim();// 213
					String strAreaWiseCostCenterKOTPrintingYN = dataObject.get("strAreaWiseCostCenterKOTPrintingYN").toString().trim();// 214
					String strWERAOnlineOrderIntegration = dataObject.get("strWERAOnlineOrderIntegration").toString().trim();// 215
					String strWERAMerchantOutletId = dataObject.get("strWERAMerchantOutletId").toString().trim();// 216
					String strWERAAuthenticationAPIKey = dataObject.get("strWERAAuthenticationAPIKey").toString().trim();// 217
					String strFireCommunication = dataObject.get("strFireCommunication").toString().trim();// 218
					String dblUSDConverionRate = dataObject.get("dblUSDConverionRate").toString().trim();// 219
					String strDBBackupMailReceiver = dataObject.get("strDBBackupMailReceiver").toString().trim();// 220
					String strPrintMoveTableMoveKOTYN = dataObject.get("strPrintMoveTableMoveKOTYN").toString().trim();// 221
					String strPrintQtyTotal = dataObject.get("strPrintQtyTotal").toString().trim();// 222
					String strShowReportsInCurrency = dataObject.get("strShowReportsInCurrency").toString().trim();// 223
					String strPOSToMMSPostingCurrency = dataObject.get("strPOSToMMSPostingCurrency").toString().trim();// 224
					String strPOSToWebBooksPostingCurrency = dataObject.get("strPOSToWebBooksPostingCurrency").toString().trim();// 225
					String strLockTableForWaiter = dataObject.get("strLockTableForWaiter").toString().trim();// 226
					String strReprintOnSettleBill = dataObject.get("strReprintOnSettleBill").toString().trim();// 227
					String strTableReservationSMS = dataObject.get("strTableReservationSMS").toString().trim();// 228
					String strSendTableReservationSMS = dataObject.get("strSendTableReservationSMS").toString().trim();// 229
					
					if (i == 0)
					{
						sbSqlInsert.append("(");
					}
					else
					{
						sbSqlInsert.append(",(");
					}

					sbSqlInsert.append("'" + hoClientCode + "','" + clientName + "','" + address1 + "','" + address2 + "','" + address3 + "'" // 5
							+ ",'" + email + "','" + billFooter + "','" + billFooterStatus + "','" + billPaperSize + "','" + negativeBilling + "'" + ",'" + dayEnd + "','" + printMode + "','" + discountNote + "','" + city + "','" + state + "'" + ",'" + country + "','" + telephoneNo + "','" + startDate + "','" + endDate + "','" + natureOfBusiness + "'" + ",'" + multipleBillPrinting + "','" + enableKOT + "','" + effectOnPSP + "','" + printVatNo + "','" + vatNo + "'" + ",'" + showBill + "','" + printServiceTaxNo + "','" + serviceTaxNo + "','" + manualBillNo + "','" + menuItemDisplaySeq + "'" // 30
							+ ",'" + senderEmailId + "','" + emailPassword + "','" + confirmEmailPassword + "','" + body + "','" + emailServerName + "'" + ",'" + smsApi + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "','" + dateEdited + "'" + ",'" + POSType + "','" + webServiceLink + "','" + dataSendFrequency + "','" + HOServerDate + "','" + RFID + "'" + ",'" + sqlServerName + "','" + sqlUserName + "','" + sqlPassword + "','" + sqlDBName + "','" + enableKOTForDirectBiller + "'" + ",'" + pinCode + "','" + changeTheme + "','" + maxDiscount + "','" + areaWisePricing + "','" + menuItemSortingOn + "'" + ",'" + directBillerAreaCode + "','" + columnSize + "','" + printType + "','" + editHomeDel + "','" + slabBasedHDChanrges + "'" // 60
							+ ",'" + skipWaiterAndPax + "','" + skipWaiter + "','" + directKOTPrintFromMakeKOT + "','" + skipPax + "','" + CRMInterface + "'" + ",'" + getWebServiceURL + "','" + postWebServiceURL + "','" + outletUID + "','" + POSID + "','" + stockInOption + "'" + ",'" + customerSeries + "','" + advRecPrintCount + "','" + homeDelSMS + "','" + billSettlementSMS + "','" + billFormatType + "'" + ",'" + activePromotions + "','" + sendHomeDelSMS + "','" + sendBillSettlmentSMS + "','" + SMSType + "','" + printShortNameOnKOT + "'" + ",'" + showCustHelp + "','" + printVoid + "','" + postSalesDataToMMS + "','" + custAreaMasterCompulsory + "','" + priceFrom + "'" + ",'" + showPrinterErrorMsg + "','" + touchScreenMode + "','" + cardInterfaceType + "','" + CMSIntegrationYN + "','" + CMSWebServiceURL + "'" // 90
							+ ",'" + changeQtyForExtCode + "','" + pointsOnBillPrint + "','" + CMSPOSCode + "','" + manualAdvOrderCompulsory + "','" + printManualAdvOrderOnBill + "'" + ",'" + printModifierQtyOnKOT + "','" + noOfLinesInKOTPrint + "','" + multipleKOTPrintYN + "','" + itemQtyPad + "','" + treatMemberAsTable + "'" + ",'" + KOTToLocalPrinter + "','" + reportImage + "','" + settleBtnForDirectBillerBill + "','" + delBoyCompulsoryOnDirectBiller + "','" + CMSMemberForJPOSKOT + "'" + ",'" + CMSMemberForMPOSKOT + "','" + dontShowAdvOrderInOtherPOS + "','" + printZeroAmtModifierInBill + "','" + printKOTYN + "','" + creditCardSlipNoCompulsoryYN + "'" + ",'" + creditCardExpDateCompulsoryYN + "','" + selectWaiterFromCardSwipe + "','" + multipleWaiterSelOnMakeKOT + "','" + moveTableToOtherPOS + "','" + moveKOTToOtherPOS + "'" + ",'" + calTaxOnMakeKOT + "','" + receiverEmailID + "','" + calItemWiseDiscount + "','" + takeAwayCustSelection + "','" + showItemStkColInDB + "'" // 120
							+ ",'" + itemType + "','" + allowNewAreaMasterFromCustMaster + "','" + custAddressSelForBill + "','" + generateMI + "','" + ftpAddress + "'" + ",'" + ftpServerName + "','" + ftpServerPass + "','" + allowToCalItemWeight + "','" + showBillsDtlType + "','" + printTaxInvoiceOnBill + "'" + ",'" + printIncOfAllTaxesOnBill + "','" + applyDiscOn + "','" + memberCodeForKOTInMPOSByCardSwipe + "','" + printBillYN + "','" + vatAndServiceTaxFromPOS + "'" + ",'" + memberCodeForMakeBillInMPOS + "','" + itemWiseKOTYN + "','" + lastPOSForDayEnd + "','" + CMSPostingType + "','" + popUpToApplyPromotionsOnBill + "'" + ",'" + selectCustCodeFromCardSwipe + "','" + chkDebitCardBalFromCardSwipe + "','" + pickSettlementsFromPOSMaster + "','" + shiftWiseDayEndYN + "','" + productionLinkup + "'" + ",'" + lockDataOnShift + "','" + wsClientCode + "','" + POSCode + "','" + enableBillSeries + "','" + enablePMSIntegration + "'" // 150
							+ ",'" + printTimeOnBill + "','" + printTDHItemsOnBill + "','" + printRemarksAndReasonForReprint + "','" + daysBeforeOrderToCancel + "','" + noOfDelDaysForAdvOrder + "'" + ",'" + noOfDelDaysForUrgentOrder + "','" + setUpToTimeForAdvOrder + "','" + setUpToTimeForUrgentOrder + "','" + upToTimeForAdvOrder + "','" + upToTimeForUrgentOrder + "'" + ",'" + enableBothPrintAndSettleForDB + "','" + inrestoPOSIntegration + "','" + inrestoPOSWebServiceURL + "','" + inrestoPOSID + "','" + inrestoPOSKey + "'" + ",'" + carryFwdFloatAmtToNextDay + "','" + openCashDrawerAfterBillPrint + "','" + propertyWiseSalesOrder + "','N','" + showItemDetailGrid + "'"
							+ ",'" + ShowPopUpForNextItemQuantity + "','" + strJioMoneyIntegration + "','" + strJioWebServiceUrl + "'"
							+ ",'" + strJioMID + "','" + strJioTID + "','" + strJioActivationCode + "','" + strJioDeviceID + "','" + strNewBillSeriesForNewDay + "','" + strShowReportsPOSWise + "','" + strEnableDineIn + "','" + strAutoAreaSelectionInMakeKOT + "' "// 181
							+ ",'" + strConsolidatedKOTPrinterPort + "','" + dblRoundOff + "','" + strShowUnSettlementForm + "','" + strPrintOpenItemsOnBill + "','" + strPrintHomeDeliveryYN + "','" + strScanQRYN + "','" + strAreaWisePromotions + "' "// 188
							+ ",'" + strPrintItemsOnMoveKOTMoveTable + "','" + strShowPurRateInDirectBiller + "' "// 190
							+ ",'" + strEnableTableReservationForCustomer + "','" + strAutoShowPopItems + "','" + intShowPopItemsOfDays + "','" + strPostSalesCostOrLoc + "','" + strEffectOfSales + "' " // 195
							+ ",'" + strPOSWiseItemToMMSProductLinkUpYN + "','" + strEnableMasterDiscount + "','" + strEnableNFCInterface + "','" + strBenowIntegrationYN + "','" + strXEmail + "' " // 200
							+ ",'" + strMerchantCode + "','" + strAuthenticationKey + "','" + strSalt + "','" + strEnableLockTable + "','" + strHomeDeliveryAreaForDirectBiller + "' " // 205
							+ ",'" + strTakeAwayAreaForDirectBiller + "','" + strRoundOffBillFinalAmt + "','" + dblNoOfDecimalPlace + "','" + strSendDBBackupOnClientMail + "','" + strPrintOrderNoOnBillYN + "','" + strPrintDeviceAndUserDtlOnKOTYN + "','" + strRemoveSCTaxCode + "','" + strAutoAddKOTToBill + "'" // 213
							+ ",'" + strAreaWiseCostCenterKOTPrintingYN + "','" + strWERAOnlineOrderIntegration + "','" + strWERAMerchantOutletId + "','" + strWERAAuthenticationAPIKey + "','" + strFireCommunication + "'" + ",'" + dblUSDConverionRate + "','" + strDBBackupMailReceiver + "','" + strPrintMoveTableMoveKOTYN + "','" + strPrintQtyTotal + "','" + strShowReportsInCurrency + "'" 
							+ ",'" + strPOSToMMSPostingCurrency + "','" + strPOSToWebBooksPostingCurrency + "','" + strLockTableForWaiter + "','" + strReprintOnSettleBill + "','" + strTableReservationSMS + "'" + ",'" + strSendTableReservationSMS + "') "); // 229
					flgData = true;
				}
			}

			if (flgData)
			{
				try
				{
					sbSqlDelete.setLength(0);
					sbSqlDelete.append("delete from tblsetup where strPOSCode='All'");
					st.execute(sbSqlDelete.toString());
					System.out.println(sbSqlInsert.toString());
					res = st.executeUpdate(sbSqlInsert.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSqlDelete = null;
			sbSqlInsert = null;
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	private int funInsertBillSeriesData(JSONArray dataArrayObject)
	{
		StringBuilder sbSqlInsert = new StringBuilder();
		StringBuilder sbSqlDelete = new StringBuilder();

		int res = 0;
		// clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection cmsCon = null;
		Statement st = null, st1 = null;
		Map<String, String> hmPOSMaster = new HashMap<String, String>();

		try
		{
			// cmsCon = objDb.funOpenPOSCon("mysql", "master");
			cmsCon = clsDatabaseConnection.DBPOSCONNECTION;
			st = cmsCon.createStatement();
			st1 = cmsCon.createStatement();

			String sqlPOSCode = "select strPOSCode,strPropertyPOSCode from tblposmaster";
			ResultSet rsPOS = st1.executeQuery(sqlPOSCode);
			while (rsPOS.next())
			{
				hmPOSMaster.put(rsPOS.getString(2), rsPOS.getString(1));
			}
			rsPOS.close();

			sbSqlInsert.setLength(0);
			sbSqlInsert.append("INSERT INTO tblbillseries " + "(strPOSCode,strType,strBillSeries,intLastNo,strCodes,strNames,strUserCreated,strUserEdited" + ",dteCreatedDate,dteEditedDate,strDataPostFlag,strClientCode,strPropertyCode,strPrintGTOfOtherBills" + ",strPrintInclusiveOfTaxOnBill) values ");
			JSONObject dataObject = new JSONObject();
			for (int i = 0; i < dataArrayObject.length(); i++)
			{
				dataObject = (JSONObject) dataArrayObject.get(i);

				String POSCode = dataObject.get("POSCode").toString().trim();
				String clientCode = dataObject.get("ClientCode").toString();
				String propertyPOSCode = clientCode + "." + POSCode;
				POSCode = hmPOSMaster.get(propertyPOSCode);

				sbSqlDelete.setLength(0);
				sbSqlDelete.append("delete from tblbillseries " + "where strClientCode='" + clientCode + "' and strPOSCode='" + POSCode + "'");
				st.execute(sbSqlDelete.toString());

				if (i == 0)
				{
					sbSqlInsert.append("(");
				}
				else
				{
					sbSqlInsert.append(",(");
				}

				String type = dataObject.get("Type").toString();
				String billSeries = dataObject.get("BillSeries").toString();
				String lastNo = dataObject.get("LastNo").toString();
				String codes = dataObject.get("Codes").toString();
				String names = dataObject.get("Names").toString();
				String userCreated = dataObject.get("UserCreated").toString();
				String userEdited = dataObject.get("UserEdited").toString();
				String dateCreated = dataObject.get("DateCreated").toString();
				String dateEdited = dataObject.get("DateEdited").toString();
				String propertyCode = dataObject.get("PropertyCode").toString();
				String printGTOfOtherBill = dataObject.get("PrintGTOfOtherBills").toString();
				String printIncOfTaxOnBill = dataObject.get("PrintIncOfTaxOnBill").toString();

				sbSqlInsert.append("'" + POSCode + "','" + type + "','" + billSeries + "','" + lastNo + "','" + codes + "'" + ",'" + names + "','" + userCreated + "','" + userEdited + "','" + dateCreated + "','" + dateEdited + "'" + ",'N','" + clientCode + "','" + propertyCode + "','" + printGTOfOtherBill + "','" + printIncOfTaxOnBill + "' )");
				flgData = true;
			}

			if (flgData)
			{
				try
				{
					res = st.executeUpdate(sbSqlInsert.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				res = 1;
			}

		}
		catch (Exception e)
		{
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
			objUtility.funWriteErrorLog(e);
			res = 0;
			e.printStackTrace();
		}
		finally
		{
			sbSqlDelete = null;
			sbSqlInsert = null;
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
				// if (null != cmsCon)
				// {
				// cmsCon.close();
				// }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return res;
		}
	}

	// This function is invoked from inresto web service.
	public int funPostBillDataToClientPOS(JSONObject objBillData)
	{
		String response = "false";
		Iterator callfunction = objBillData.keys();
		while (callfunction.hasNext())
		{
			String key = callfunction.next().toString();
			JSONArray mJsonArray = null;
			try
			{
				mJsonArray = (JSONArray) objBillData.get(key);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (key.equalsIgnoreCase("BillHdInfo") && mJsonArray != null)
			{
				System.out.println("BillHd= " + key);
				if (funInsertBillHdData(mJsonArray, "tblbillhd") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillDtlInfo") && mJsonArray != null)
			{
				System.out.println("BillDtl= " + key);
				if (funInsertBillDtlData(mJsonArray, "tblbilldtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillModifierDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillModifierDtlData(mJsonArray, "tblbillmodifierdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillDiscountDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillDiscountDtlData(mJsonArray, "tblbilldiscdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillSettlementDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillSettlementDtlData(mJsonArray, "tblbillsettlementdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillTaxDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertBillTaxDtlData(mJsonArray, "tblbilltaxdtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillPromotionDtl") && mJsonArray != null)
			{
				if (funInsertBillPromotionDtlData(mJsonArray, "tblbillpromotiondtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("BillComplimentryDtl") && mJsonArray != null)
			{
				if (funInsertBillComplimentryDtlData(mJsonArray, "tblbillcomplementrydtl") > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("HomeDelivery") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertHomeDeliveryData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
			else if (key.equalsIgnoreCase("HomeDeliveryDtl") && mJsonArray != null)
			{
				System.out.println(key);
				if (funInsertHomeDeliveryDtlData(mJsonArray) > 0)
				{
					response = "true";
				}
				else
				{
					response = "false";
				}
			}
		}
		return 1;
	}

}
