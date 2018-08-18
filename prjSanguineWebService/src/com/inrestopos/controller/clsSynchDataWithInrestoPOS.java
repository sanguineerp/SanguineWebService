package com.inrestopos.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.hopos.controller.clsPostPOSBillData;
import com.inrestopos.listener.intfSynchDataWithInrestoPOS;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsItemDtlForTax;
import com.webservice.util.clsTaxCalculation;
import com.webservice.util.clsTaxCalculationDtls;

@Path("/InrestoPOSIntegration")
public class clsSynchDataWithInrestoPOS implements intfSynchDataWithInrestoPOS
{
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/seatcustomer")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funGetCustomerSeatData(JSONObject objCustomerSeatData)
	{
		System.out.println("Entered seat cust api");
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		String phone = "";
		String customerName = "";
		String customerCode = "";
		JSONObject jObj = new JSONObject();

		try
		{
			System.out.println("Entered seat cust api");
			cmsCon = objDb.funOpenPOSCon("mysql", "master");
			st = cmsCon.createStatement();

			String clientCode = "092.001";
			String sql = "select strClientCode from tblsetup";
			ResultSet rsClientCode = st.executeQuery(sql);
			if (rsClientCode.next())
			{
				clientCode = rsClientCode.getString(1);
			}
			rsClientCode.close();

			String merchantId = objCustomerSeatData.get("merchantId").toString();
			String meachantKey = objCustomerSeatData.get("merchantKey").toString();
			phone = objCustomerSeatData.get("customerPhone").toString();
			customerName = objCustomerSeatData.get("customerName").toString();
			String pax = objCustomerSeatData.get("pax").toString();
			String tables = "";
			if (null != objCustomerSeatData.get("tables"))
			{
				tables = objCustomerSeatData.get("tables").toString();
			}
			customerCode = funCheckCustomer(phone, customerName, "", clientCode);

			funCreateReservationInPOS(tables, "Reserve", phone, customerCode, customerName, clientCode);

			jObj.put("CustCode", customerCode);
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(201).entity(jObj).build();
	}

	private void funCreateReservationInPOS(String tables, String tableStatus, String phone, String customerCode, String customerName, String clientCode)
	{
		try
		{
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection con = null;
			java.sql.PreparedStatement preparedStatement = null;
			con = objDb.funOpenPOSCon("mysql", "transaction");

			Date objDate = new Date();
			String currentDate = objDate.getYear() + 1900 + "-" + (objDate.getMonth() + 1) + "-" + objDate.getDate();
			String reservationDate = currentDate + " " + objDate.getHours() + ":" + objDate.getMinutes() + ":" + objDate.getSeconds();
			SimpleDateFormat hhmmssTimeFormate = new SimpleDateFormat("HH:mm:ss");
			String reservationTime = reservationDate.split(" ")[1];			

			Statement statement = con.createStatement();
			// ********************update Table Status(Reserve) In
			// POS*****************************//
			preparedStatement = con.prepareStatement("select strTableNo from tbltablemaster   where strTableName=? ");
			String tablesArr[] = tables.split(",");
			for (int i = 0; i < tablesArr.length; i++)
			{
				preparedStatement.setString(1, tablesArr[i]);
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next())
				{
					String tableNo = resultSet.getString(1);
					statement.executeUpdate("update tbltablemaster set strStatus='" + tableStatus + "' where strTableNo='" + tableNo + "' ");
					// ********************Add Reservation In
					// POS*********************************************//
					String reservationCode = funGetReservationNo();

					String sqlInsert = "INSERT INTO tblreservation "
							+ "(strResCode,strCustomerCode,intPax,strSmoking,dteResDate,tmeResTime,strAMPM,strSpecialInfo,strTableNo,strUserCreated"
							+ ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode,strPosCode) "
							+ "VALUES " + "('" + reservationCode + "', '" + customerCode + "','1' " + ",'N','" + reservationDate + "', '" + reservationTime + "'"
							+ ",'PM'" + ", '!!!','" + tableNo + "','Sanguine'" + ", 'Sanguine', '" + reservationDate + "'" + ", '" + reservationDate + "'"
							+ ", '" + clientCode + "', 'P01') ";
					statement.execute(sqlInsert);
					System.out.println("TableNo->"+tableNo+" Table->"+tablesArr[i]+" ResCode->"+reservationCode+" CustCode->"+customerCode+" Phone->"+phone);

				}
			}
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private String funGetReservationNo()
	{
		String reservationCode = "", strCode = "", code = "";
		long lastNo = 1;
		try
		{

			clsDatabaseConnection objDb = new clsDatabaseConnection();
			Connection con = null;
			java.sql.PreparedStatement preparedStatement = null;
			con = objDb.funOpenPOSCon("mysql", "transaction");

			Statement statement = con.createStatement();

			StringBuilder sqlQuery = new StringBuilder();

			sqlQuery.setLength(0);
			sqlQuery.append("select count(*) from tblreservation");
			ResultSet rsCustCode = statement.executeQuery(sqlQuery.toString());
			rsCustCode.next();
			int cntReserveCode = rsCustCode.getInt(1);
			rsCustCode.close();

			if (cntReserveCode > 0)
			{
				sqlQuery.setLength(0);
				sqlQuery.append("select max(strResCode) from tblreservation");
				rsCustCode = statement.executeQuery(sqlQuery.toString());
				rsCustCode.next();
				code = rsCustCode.getString(1);
				strCode = code.substring(2, code.length());
				lastNo = Long.parseLong(strCode);
				lastNo++;
				reservationCode = "RS" + String.format("%07d", lastNo);

				rsCustCode.close();
			}
			else
			{
				reservationCode = "RS0000001";
				// reservCode = "RS000001";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return reservationCode;
	}

	private String funGenerateCustCode(String clientCode)
	{
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		String customerCode = "", strCode = "", code = "";
		long lastNo = 1;
		try
		{
			cmsCon = objDb.funOpenPOSCon("mysql", "transaction");
			st = cmsCon.createStatement();

			String propertCode = clientCode.substring(4);

			String sql = "select count(*) from tblcustomermaster";
			ResultSet rsCustCode = st.executeQuery(sql);
			rsCustCode.next();
			int cntCustCode = rsCustCode.getInt(1);
			rsCustCode.close();

			if (cntCustCode > 0)
			{
				sql = "select max(right(strCustomerCode,8)) from tblcustomermaster";
				rsCustCode = st.executeQuery(sql);
				rsCustCode.next();
				code = rsCustCode.getString(1);
				StringBuilder sb = new StringBuilder(code);

				strCode = sb.substring(1, sb.length());

				lastNo = Long.parseLong(strCode);
				lastNo++;
				customerCode = propertCode + "C" + String.format("%07d", lastNo);

				rsCustCode.close();
			}
			else
			{
				sql = "select longCustSeries from tblsetup";
				ResultSet rsCustSeries = st.executeQuery(sql);
				if (rsCustSeries.next())
				{
					lastNo = Long.parseLong(rsCustSeries.getString(1));
				}
				rsCustSeries.close();
				customerCode = propertCode + "C" + String.format("%07d", lastNo);
				// CustCode = "C0000001";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return customerCode;
	}

	@GET
	@Path("/funGetMenuItemPricingDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetMenuItemPricingDtl(@QueryParam("ClientCode") String clientCode, @QueryParam("POSCode") String POSCode)
	{
		JSONObject jObj = new JSONObject();
		jObj = funGetPricingDtlFromDB(clientCode, POSCode);
		return jObj.toString();
	}

	private JSONObject funGetPricingDtlFromDB(String clientCode, String POSCode)
	{
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();
		try
		{
			posCon = objDb.funOpenPOSCon("mysql", "master");
			st = posCon.createStatement();
			String sql = "select e.strPosName,a.strItemCode,b.strItemName,c.strMenuName,d.strSubGroupName,b.strExternalCode " + " ,a.strPriceMonday " + " from tblmenuitempricingdtl a ,tblitemmaster b ,tblmenuhd c ,tblsubgrouphd d,tblposmaster e " + " where a.strItemCode=b.strItemCode and a.strMenuCode=c.strMenuCode and b.strSubGroupCode=d.strSubGroupCode " + " and a.strPosCode=e.strPosCode " + " order by e.strPosName,b.strItemName";
			ResultSet rsPriceDtl = st.executeQuery(sql);
			JSONArray arrObj = new JSONArray();
			while (rsPriceDtl.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("POS", rsPriceDtl.getString(1));
				obj.put("ItemCode", rsPriceDtl.getString(2));
				obj.put("ItemName", rsPriceDtl.getString(3));
				obj.put("MenuHead", rsPriceDtl.getString(4));
				obj.put("SubGroup", rsPriceDtl.getString(5));
				obj.put("ExternalCode", rsPriceDtl.getString(6));
				obj.put("Price", rsPriceDtl.getString(7));

				JSONArray arrJsonModDtl = new JSONArray();
				sql = "select b.strModifierCode,b.strModifierName,a.dblRate " + " from tblitemmodofier a,tblmodifiermaster b " + " where a.strModifierCode=b.strModifierCode and a.strItemCode='" + rsPriceDtl.getString(2) + "'";
				ResultSet rsModifierDtl = st.executeQuery(sql);
				while (rsModifierDtl.next())
				{
					JSONObject jObjModDtl = new JSONObject();
					jObjModDtl.put("ModCode", rsModifierDtl.getString(1));
					jObjModDtl.put("ModName", rsModifierDtl.getString(2));
					jObjModDtl.put("ModRate", rsModifierDtl.getDouble(3));
					arrJsonModDtl.put(jObjModDtl);
				}
				rsModifierDtl.close();
				obj.put("ModDtl", arrJsonModDtl);

				arrObj.put(obj);
			}
			rsPriceDtl.close();
			jObj.put("MenuItemPricingDtl", arrObj);
			st.close();
			posCon.close();
		}
		catch (Exception e)
		{
			JSONObject obj = new JSONObject();
			obj.put("Status", "Error");
			JSONArray arrObj = new JSONArray();
			arrObj.put(obj);
			jObj.put("MenuItemPricingDtl", arrObj);
			e.printStackTrace();
		}
		finally
		{
			return jObj;
		}
	}

	private String funGenerateBillNo(String posCode)
	{
		String voucherNo = "";
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null;
		Statement st = null;

		try
		{
			posCon = objDb.funOpenPOSCon("mysql", "master");
			st = posCon.createStatement();

			long code = 0;
			String sqlGetLastNoForBill = "select strBillNo from tblstorelastbill where strPosCode='" + posCode + "'";
			ResultSet rsLastBillNo = st.executeQuery(sqlGetLastNoForBill);

			if (rsLastBillNo.next())
			{
				code = rsLastBillNo.getLong(1);
				code = code + 1;
				rsLastBillNo.close();
				voucherNo = posCode + String.format("%05d", code);
				System.out.println(voucherNo);
				String sql_Update_lastBill = "update tblstorelastbill set strBillNo='" + code + "' where strPosCode='" + posCode + "'";
				st.executeUpdate(sql_Update_lastBill);
			}
			else
			{
				rsLastBillNo.close();
				voucherNo = posCode + "00001";
				String sql_insert = "insert into tblstorelastbill values('" + posCode + "','1')";
				st.executeUpdate(sql_insert);
			}
			st.close();
			posCon.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return voucherNo;
		}
	}

	private String funCheckCustomer(String mobileNo, String customerName, String customerAddress, String clientCode)
	{
		String customerCode = "";
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection con = null;
		Statement st = null;
		Date objDate = new Date();
		String currentDate = objDate.getYear() + 1900 + "-" + (objDate.getMonth() + 1) + "-" + objDate.getDate();
		String dateTime = currentDate + " " + objDate.getHours() + ":" + objDate.getMinutes() + ":" + objDate.getSeconds();
		try
		{
			con = objDb.funOpenPOSCon("mysql", "transaction");
			st = con.createStatement();

			if (!mobileNo.isEmpty())
			{
				String sql = "select strCustomerCode from tblcustomermaster where longMobileNo='" + mobileNo + "';";
				System.out.println(sql);
				ResultSet rsCustomerData = st.executeQuery(sql);
				if (rsCustomerData.next())
				{
					customerCode = rsCustomerData.getString(1);
				}
				else
				{
					customerCode = funGenerateCustCode(clientCode);
					sql = "insert into tblcustomermaster " + "(strCustomerCode,strCustomerName,strBuldingCode,strBuildingName,strStreetName,strLandmark,strArea,strCity" + ",strState,intPinCode,longMobileNo,longAlternateMobileNo,strOfficeBuildingCode,strOfficeBuildingName" + ",strOfficeStreetName,strOfficeLandmark,strOfficeArea,strOfficeCity,strOfficePinCode,strOfficeState" + ",strOfficeNo,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strDataPostFlag,strClientCode" + ",strOfficeAddress,strExternalCode,strCustomerType,dteDOB,strGender,dteAnniversary,strEmailId,strCRMId) " + "values('" + customerCode + "','" + customerName + "','','" + customerAddress + "','','','','' " + ",'',''" + ",'" + mobileNo + "','','','','','','','','','','','Sanguine','Sanguine','" + dateTime + "' " + ",'" + dateTime + "'" + ",'N','" + clientCode + "','N','','CT001','','Male','','','' )";
					st.executeUpdate(sql);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return customerCode;
		}
	}

	private JSONObject funGetBillTaxDtl(JSONArray arrBillDtl, JSONArray arrBillModifierDtl, String posCode, String billDate, String areaCode, String operationTypeForTax, String billNo, String clientCode)
	{
		double subTotalForTax = 0;
		double discAmount = 0;
		double discPercent = 0;
		double totalTaxAmount = 0;
		JSONObject jObj = new JSONObject();
		List<clsItemDtlForTax> arrListItemDtls = new ArrayList<clsItemDtlForTax>();

		try
		{
			// for bill dtl
			for (int i = 0; i < arrBillDtl.length(); i++)
			{
				JSONObject mJsonObject = (JSONObject) arrBillDtl.get(i);

				String ItemCode = mJsonObject.get("ItemCode").toString();
				String ItemName = mJsonObject.get("ItemName").toString();
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double discAmt = Double.parseDouble(mJsonObject.get("DiscountAmt").toString());
				double discPer = Double.parseDouble(mJsonObject.get("DiscountPer").toString());

				clsItemDtlForTax objItemDtl = new clsItemDtlForTax();
				objItemDtl.setItemCode(ItemCode);
				objItemDtl.setItemName(ItemName);
				objItemDtl.setAmount(Amount);
				objItemDtl.setDiscAmt(discAmt);
				objItemDtl.setDiscPer(discPer);
				arrListItemDtls.add(objItemDtl);
				subTotalForTax += Amount;
				discAmount += discAmt;
			}

			// for billmodifierdtl
			for (int i = 0; i < arrBillModifierDtl.length(); i++)
			{
				JSONObject mJsonObject = (JSONObject) arrBillModifierDtl.get(i);
				String ItemCode = mJsonObject.get("ItemCode").toString();
				String modifierName = mJsonObject.get("ModifierName").toString();
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double discAmt = Double.parseDouble(mJsonObject.get("dblDiscAmt").toString());
				double discPer = Double.parseDouble(mJsonObject.get("dblDiscPer").toString());
				clsItemDtlForTax objItemDtl = new clsItemDtlForTax();
				objItemDtl.setItemCode(ItemCode);
				objItemDtl.setItemName(modifierName);
				objItemDtl.setAmount(Amount);
				objItemDtl.setDiscAmt(discAmt);
				objItemDtl.setDiscPer(discPer);
				arrListItemDtls.add(objItemDtl);
				subTotalForTax += Amount;
				discAmount += discAmt;
			}

			if (subTotalForTax > 0)
			{
				discPercent = (discAmount / subTotalForTax) * 100;
			}

			JSONArray jArrBilTaxDtl = new JSONArray();
			Date dt = new Date();
			String date = (dt.getYear() + 1900) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate();
			clsTaxCalculation objTaxCalculation = new clsTaxCalculation();
			List<clsTaxCalculationDtls> arrListTaxDtl = objTaxCalculation.funCalculateTax(arrListItemDtls, posCode, date, areaCode, operationTypeForTax, subTotalForTax, discPercent, "");

			for (int cnt = 0; cnt < arrListTaxDtl.size(); cnt++)
			{
				clsTaxCalculationDtls obj = arrListTaxDtl.get(cnt);
				System.out.println("Tax Dtl= " + obj.getTaxCode() + "\t" + obj.getTaxName() + "\t" + obj.getTaxAmount());
				JSONObject jObjTaxDtl = new JSONObject();
				jObjTaxDtl.put("BillNo", billNo);
				jObjTaxDtl.put("ClientCode", clientCode);
				jObjTaxDtl.put("DataPostFlag", "N");
				jObjTaxDtl.put("TaxCode", obj.getTaxCode());
				jObjTaxDtl.put("TaxName", obj.getTaxName());
				jObjTaxDtl.put("TaxType", obj.getTaxCalculationType());
				jObjTaxDtl.put("TaxableAmount", obj.getTaxableAmount());
				jObjTaxDtl.put("TaxAmount", Math.rint(obj.getTaxAmount()));
				jArrBilTaxDtl.put(jObjTaxDtl);
				totalTaxAmount += Math.rint(obj.getTaxAmount());
			}
			jObj.put("BillTaxDetails", jArrBilTaxDtl);
			jObj.put("discAmount", discAmount);
			jObj.put("discPercent", discPercent);
			jObj.put("subTotal", subTotalForTax);
			jObj.put("totalTaxAmount", totalTaxAmount);
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

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPostBillHdDataThroughInResto")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveOpenOrder(JSONObject objJson)
	{
		String response = "";
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection con = null;
		Statement st = null;
		Date objDate = new Date();
		String currentDate = objDate.getYear() + 1900 + "-" + (objDate.getMonth() + 1) + "-" + objDate.getDate();
		String currentDateTime = currentDate + " " + objDate.getHours() + ":" + objDate.getMinutes() + ":" + objDate.getSeconds();
		String time = objDate.getHours() + ":" + objDate.getMinutes() + ":" + objDate.getSeconds();

		try
		{
			con = objDb.funOpenPOSCon("mysql", "transaction");
			st = con.createStatement();

			String merchantId = objJson.getString("merchantId");
			String merchantKey = objJson.getString("merchantKey");
			String customerPhone = objJson.getString("customerPhone");
			String customerName = objJson.getString("customerName");
			String customerAddress = objJson.getString("customerAddress");
			String paymentType = objJson.getString("paymentType");
			String operationType = objJson.getString("type");
			double discountAmt = Double.parseDouble(objJson.getString("discount"));
			double deliveryCharges = Double.parseDouble(objJson.getString("deliverycharges"));
			JSONArray jArrItemDtl = objJson.getJSONArray("items");

			String POSCode = "P01", tableNo = "", waiterNo = "";
			String billNo = funGenerateBillNo(POSCode);
			String POSDate = "", areaCode = "A001", clientCode = "117.001", takeAway = "No", userCode = "SANGUINE";
			int shiftCode = 0;
			if (operationType.equalsIgnoreCase("delivery"))
			{
				operationType = "Home Delivery";
			}
			else
			{
				operationType = "Take Away";
				takeAway = "Yes";
			}

			StringBuilder sbSql = new StringBuilder();
			sbSql.setLength(0);
			sbSql.append("select a.strDirectAreaCode,a.strClientCode from tblsetup a");
			ResultSet rsSetupValues = st.executeQuery(sbSql.toString());
			if (rsSetupValues.next())
			{
				areaCode = rsSetupValues.getString(1);
				clientCode = rsSetupValues.getString(2);
			}
			rsSetupValues.close();

			sbSql.setLength(0);
			sbSql.append("select date(max(dtePOSDate)),intShiftCode,strShiftEnd,strDayEnd " + " from tbldayendprocess " + " where strPOSCode='" + POSCode + "' and strDayEnd='N' and (strShiftEnd='' or strShiftEnd='N')");
			ResultSet rsPOSDate = st.executeQuery(sbSql.toString());
			if (rsPOSDate.next())
			{
				String shiftEnd = rsPOSDate.getString(3);
				String dayEnd = rsPOSDate.getString(4);
				if (shiftEnd.equals("") && dayEnd.equals("N"))
				{
					POSDate = "";
				}
				else
				{
					POSDate = rsPOSDate.getString(1);
					shiftCode = rsPOSDate.getInt(2);
				}
			}
			rsPOSDate.close();

			String reasonCode = "", discRemarks = "", takeAwayRemarks = "";
			sbSql.setLength(0);
			sbSql.append("select strReasonCode from tblreasonmaster where strDiscount='Y' ");
			ResultSet rsDiscReason = st.executeQuery(sbSql.toString());
			if (rsDiscReason.next())
			{
				reasonCode = rsDiscReason.getString(1);
			}
			rsDiscReason.close();

			String billDateTime = POSDate + " " + time;
			String settleDateTime = POSDate + " " + time;
			JSONArray arrBillDtl = new JSONArray();
			JSONArray arrBillModifierDtl = new JSONArray();

			double subTotal = 0, grandTotal = 0, taxAmt = 0, tipAmt = 0;
			for (int cnt = 0; cnt < jArrItemDtl.length(); cnt++)
			{
				JSONObject jObjItemDtl = jArrItemDtl.getJSONObject(cnt);
				String itemCode = jObjItemDtl.getString("id");
				double quantity = jObjItemDtl.getDouble("quantity");
				double rate = 0;
				String itemName = "";

				sbSql.setLength(0);
				sbSql.append("select strPriceSunday,strItemName " + " from tblmenuitempricingdtl where strItemCode='" + itemCode + "' " + " and strAreaCode='" + areaCode + "' and (strPOSCOde='" + POSCode + "' or strPOSCode='All') ");
				ResultSet rsItemPrice = st.executeQuery(sbSql.toString());
				if (rsItemPrice.next())
				{
					rate = rsItemPrice.getDouble(1);
					itemName = rsItemPrice.getString(2);
				}
				rsItemPrice.close();
				double amount = rate * quantity;
				subTotal += amount;

				JSONObject objBillDtl = new JSONObject();
				objBillDtl.put("BillNo", billNo);
				objBillDtl.put("ItemCode", itemCode);
				objBillDtl.put("ItemName", itemName);
				objBillDtl.put("AdvBookingNo", "");
				objBillDtl.put("Rate", rate);
				objBillDtl.put("Quantity", quantity);
				objBillDtl.put("Amount", amount);
				objBillDtl.put("TaxAmount", "0.00");
				objBillDtl.put("BillDate", billDateTime);
				objBillDtl.put("KOTNo", "");
				objBillDtl.put("ClientCode", clientCode);
				objBillDtl.put("CustomerCode", "");
				objBillDtl.put("OrderProcessing", "00:00:00");
				objBillDtl.put("DataPostFlag", "N");
				objBillDtl.put("MMSDataPostFlag", "N");
				objBillDtl.put("ManualKOTNo", "");
				objBillDtl.put("tdhYN", "N");
				objBillDtl.put("PromoCode", "");
				objBillDtl.put("CounterCode", "");
				objBillDtl.put("WaiterNo", "");
				objBillDtl.put("DiscountAmt", "0.00");
				objBillDtl.put("DiscountPer", "0.00");
				arrBillDtl.put(objBillDtl);
			}
			double discountPer = (discountAmt / subTotal) * 100;

			int len = arrBillDtl.length();
			for (int cnt = 0; cnt < len; cnt++)
			{
				JSONObject jObjItemDtl = arrBillDtl.getJSONObject(cnt);
				double amt = jObjItemDtl.getDouble("Amount");
				double discAmt = (amt * discountPer) / 100;
				jObjItemDtl.put("DiscountAmt", discAmt);
				jObjItemDtl.put("DiscountPer", discountPer);
				arrBillDtl.put(jObjItemDtl);
			}

			String customerCode = funCheckCustomer(customerPhone, customerName, customerAddress, clientCode);
			JSONObject objBillTaxDtl = funGetBillTaxDtl(arrBillDtl, arrBillModifierDtl, POSCode, billDateTime, areaCode, operationType, billNo, clientCode);
			taxAmt = objBillTaxDtl.getDouble("totalTaxAmount");

			JSONObject objJSONBillHd = funGetBillHdJSONData(billNo, POSCode, billDateTime, discountAmt, discountPer, taxAmt, subTotal, grandTotal, takeAway, operationType, userCode, currentDateTime, tableNo, waiterNo, clientCode, customerCode, shiftCode, 0, reasonCode, "", tipAmt, settleDateTime, deliveryCharges, areaCode, discRemarks, takeAwayRemarks, "Total");
			clsPostPOSBillData objPostBillData = new clsPostPOSBillData();
			int billhdCount = objPostBillData.funPostBillDataToClientPOS(objJSONBillHd);

			JSONObject objJSONBillDtl = new JSONObject();
			objJSONBillDtl.put("BillDtlInfo", arrBillDtl);
			int billdtlCount = objPostBillData.funPostBillDataToClientPOS(objJSONBillDtl);

			JSONObject objJSONBillTaxDtl = new JSONObject();
			objJSONBillTaxDtl.put("BillTaxDtl", objBillTaxDtl.getJSONArray("BillTaxDetails"));
			int billtaxCount = objPostBillData.funPostBillDataToClientPOS(objJSONBillTaxDtl);

			// fill home delivery table
			JSONObject objJSONHomeDel = new JSONObject();
			JSONArray arrObjHomeDel = new JSONArray();
			JSONObject objHomeDelivery = new JSONObject();
			objHomeDelivery.put("BillNo", billNo);
			objHomeDelivery.put("CustomerCode", customerCode);
			objHomeDelivery.put("DPCode", "");
			objHomeDelivery.put("POSCode", POSCode);
			objHomeDelivery.put("Date", billDateTime);
			objHomeDelivery.put("Time", "00:00:00");
			objHomeDelivery.put("CustAddress1", "");
			objHomeDelivery.put("CustAddress2", "");
			objHomeDelivery.put("CustAddress3", "");
			objHomeDelivery.put("CustAddress4", "");
			objHomeDelivery.put("CustCity", "");
			objHomeDelivery.put("DataPostFlag", "N");
			objHomeDelivery.put("ClientCode", clientCode);
			arrObjHomeDel.put(objHomeDelivery);
			objJSONHomeDel.put("HomeDelivery", arrObjHomeDel);
			objPostBillData.funPostBillDataToClientPOS(objJSONHomeDel);

			// fill home deliverydtl table
			JSONObject objJSONHomeDelDtl = new JSONObject();
			JSONArray arrObjHomeDelDtl = new JSONArray();
			JSONObject objRows = new JSONObject();
			objRows.put("BillNo", billNo);
			objRows.put("DPCode", "");
			objRows.put("ClientCode", clientCode);
			objRows.put("DataPostFlag", "N");
			arrObjHomeDelDtl.put(objRows);
			objJSONHomeDelDtl.put("HomeDeliveryDtl", arrObjHomeDelDtl);
			objPostBillData.funPostBillDataToClientPOS(objJSONHomeDelDtl);

			response = billNo;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response = "Failed";
		}
		finally
		{
			return Response.status(201).entity(response).build();
		}
	}

	private JSONObject funGetBillHdJSONData(String billNo, String POSCode, String billDate, double discAmt, double discPer, double taxAmt, double subTotal, double grandTotal, String takeAway, String operationType, String userCode, String currentDate, String tableNo, String waiterNo, String clientCode, String customerCode, int shiftCode, int paxNo, String reasonCode, String remarks, double tipAmt, String settleDate, double deliveryCharges, String areaCode, String discRemarks, String takeAwayRemarks, String discOn) throws Exception
	{

		JSONObject objJSONBillHd = new JSONObject();
		JSONArray arrObj = new JSONArray();
		JSONObject objRows = new JSONObject();

		objRows.put("BillNo", billNo);
		objRows.put("AdvBookingNo", "");
		objRows.put("BillDate", billDate);
		objRows.put("POSCode", POSCode);
		objRows.put("SettelmentMode", "");
		objRows.put("DiscountAmt", discAmt);
		objRows.put("DiscountPer", discPer);
		objRows.put("TaxAmt", taxAmt);
		objRows.put("SubTotal", subTotal);
		objRows.put("GrandTotal", grandTotal);
		objRows.put("TakeAway", takeAway);
		objRows.put("OperationType", operationType);
		objRows.put("UserCreated", userCode);
		objRows.put("UserEdited", userCode);
		objRows.put("DateCreated", currentDate);
		objRows.put("DateEdited", currentDate);
		objRows.put("ClientCode", clientCode);
		objRows.put("TableNo", tableNo);
		objRows.put("WaiterNo", waiterNo);
		objRows.put("CustomerCode", customerCode);
		objRows.put("ManualBillNo", "");
		objRows.put("ShiftCode", shiftCode);
		objRows.put("PaxNo", paxNo);
		objRows.put("DataPostFlag", "N");
		objRows.put("ReasonCode", reasonCode);
		objRows.put("Remarks", remarks);
		objRows.put("TipAmount", tipAmt);
		objRows.put("SettleDate", settleDate);
		objRows.put("CounterCode", "");
		objRows.put("DeliveryCharges", deliveryCharges);
		objRows.put("CouponCode", "");
		objRows.put("AreaCode", areaCode);
		objRows.put("DiscountRemark", discRemarks);
		objRows.put("TakeAwayRemark", takeAwayRemarks);
		objRows.put("DiscountOn", discOn);
		objRows.put("CardNo", "");

		arrObj.put(objRows);
		objJSONBillHd.put("BillHdInfo", arrObj);

		return objJSONBillHd;
	}

}
