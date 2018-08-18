package com.onlineordering.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.onlineordering.beans.clsOrderDtlBean;
import com.onlineordering.beans.clsOrderHDBean;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Path("/OnlineOrderIntegration")
public class clsSynchDataWithSanguineOnlineOrder {

	public clsSynchDataWithSanguineOnlineOrder() throws Exception {
		if (clsDatabaseConnection.DBPOSCONNECTION == null) {
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			clsDatabaseConnection.DBPOSCONNECTION = objDb.funOpenPOSCon("mysql", "transaction");
		} else if (clsDatabaseConnection.DBPOSCONNECTION.isClosed()) {
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			clsDatabaseConnection.DBPOSCONNECTION = objDb.funOpenPOSCon("mysql", "transaction");
		}
	}

	static {
		try {
			clsDatabaseConnection objDb = new clsDatabaseConnection();
			clsDatabaseConnection.DBPOSCONNECTION = objDb.funOpenPOSCon("mysql", "transaction");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("/funInvokeOnlineOrderWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetConnectionInfo() {
		String response = "false";
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			response = "true";
		} catch (Exception e) {
			response = "false";
			e.printStackTrace();
		}

		return Response.status(201).entity(response).build();
	}

	@GET
	@Path("/funGetClientDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetClientDetails(@QueryParam("clientCode") String clientCode,
			@QueryParam("clientName") String clientName) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();
			String sql = "";
			sql = " select * from tblclientdetails a where a.strClientCode='" + clientCode + "' "
					+ " and a.strClientName='" + clientName + "' ";
			JSONArray arrObj = new JSONArray();
			ResultSet rsCheck = st.executeQuery(sql);
			if (rsCheck.next()) {
				JSONObject obj = new JSONObject();
				obj.put("Status", "Available");
				obj.put("ClientCode", rsCheck.getString(1));
				obj.put("ClientName", rsCheck.getString(2));
				obj.put("ClientIpAddress", rsCheck.getString(3));
				arrObj.put(obj);
			} else {
				JSONObject obj = new JSONObject();
				obj.put("Status", "Unavailable");
				arrObj.put(obj);
			}

			rsCheck.close();
			jObj.put("ClientDetails", arrObj);
			st.close();
			onlineOrderCon.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return jObj.toString();
		}
	}

	@GET
	@Path("/funGetCityDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetCityDetails() {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();
			String sql = "";
			sql = " select * from tblcitymaster a ";
			JSONArray arrObjCity = new JSONArray();
			ResultSet rsCity = st.executeQuery(sql);
			while (rsCity.next()) {
				JSONObject objCity = new JSONObject();
				objCity.put("CityCode", rsCity.getString(1));
				objCity.put("CityName", rsCity.getString(2));
				objCity.put("ClientCode", rsCity.getString(3));
				arrObjCity.put(objCity);
			}
			rsCity.close();
			jObj.put("CityDetails", arrObjCity);
			st.close();
			onlineOrderCon.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return jObj.toString();
		}
	}

	@GET
	@Path("/funGetCustomerAreaDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetCustomerAreaDetails(@QueryParam("cityCode") String cityCode) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();
			String sql = "";
			sql = " select * from tblareamaster a where a.strCityCode='" + cityCode + "'  ";
			JSONArray arrObjArea = new JSONArray();
			ResultSet rsArea = st.executeQuery(sql);
			while (rsArea.next()) {
				JSONObject objCity = new JSONObject();
				objCity.put("AreaCode", rsArea.getString(1));
				objCity.put("AreaName", rsArea.getString(2));
				objCity.put("CityCode", rsArea.getString(3));
				objCity.put("ClientCode", rsArea.getString(4));
				arrObjArea.put(objCity);
			}
			rsArea.close();
			jObj.put("CustomerAreaDetails", arrObjArea);
			st.close();
			onlineOrderCon.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return jObj.toString();
		}
	}

	@GET
	@Path("/funGetRestaurantDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetRestaurantDetails(@QueryParam("areaCode") String areaCode) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();
			String sql = "";
			sql = " select * from tblrestaurantmaster a where a.strAreaCode='" + areaCode + "'  ";
			JSONArray arrObjResto = new JSONArray();
			ResultSet rsResto = st.executeQuery(sql);
			while (rsResto.next()) {
				JSONObject objResto = new JSONObject();
				objResto.put("RestaurantCode", rsResto.getString(1));
				objResto.put("RestaurantName", rsResto.getString(2));
				objResto.put("ClientCode", rsResto.getString(4));
				arrObjResto.put(objResto);
			}
			rsResto.close();
			jObj.put("RestaurantDetails", arrObjResto);
			st.close();
			onlineOrderCon.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return jObj.toString();
		}
	}

	@GET
	@Path("/funGetItemDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetItemDetails(@QueryParam("clientCode") String clientCode) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();
			String sql = "";
			sql = " select * from tblitemmaster a where a.strClientCode='" + clientCode + "'  ";
			JSONArray arrObjItemDtl = new JSONArray();
			ResultSet rsItem = st.executeQuery(sql);
			while (rsItem.next()) {
				JSONObject objItem = new JSONObject();
				objItem.put("strItemCode", rsItem.getString(1));
				objItem.put("strItemName", rsItem.getString(2));
				objItem.put("strClientCode", rsItem.getString(3));
				objItem.put("dblItemPrice", rsItem.getDouble(4));
				objItem.put("dblItemSalePrice", rsItem.getDouble(5));
				objItem.put("dblDiscPer", rsItem.getDouble(6));
				objItem.put("strItemForSale", rsItem.getString(7));
				objItem.put("strApplyDisc", rsItem.getString(8));
				arrObjItemDtl.put(objItem);
			}
			rsItem.close();
			jObj.put("ItemDetails", arrObjItemDtl);
			st.close();
			onlineOrderCon.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return jObj.toString();
		}
	}

	@SuppressWarnings("finally")
	@GET
	@Path("/funGetMenuAndItemDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funMainCategoryaAndSubCategory(@QueryParam("clientCode") String clientCode) {
		JSONArray arrMenu = new JSONArray();

		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement stMenu = null;

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			stMenu = onlineOrderCon.createStatement();
			Statement stItem = onlineOrderCon.createStatement();
			String sqlMenu = " select a.strMenuCode,a.strMenuName from tblitemmaster a " + " where a.strClientCode='"
					+ clientCode + "' " + " group by a.strMenuCode order by a.strMenuName ";

			ResultSet rsMenuDtl = stMenu.executeQuery(sqlMenu);

			while (rsMenuDtl.next()) {
				JSONObject obj = new JSONObject();
				obj.put("strMenuCode", rsMenuDtl.getString(1));
				obj.put("strMenuName", rsMenuDtl.getString(2));

				String sqlItems = " select * from tblitemmaster a where a.strMenuCode='" + rsMenuDtl.getString(1)
						+ "' ";
				ResultSet rsItem = stItem.executeQuery(sqlItems);
				JSONArray jsonArrItems = new JSONArray();
				while (rsItem.next()) {
					JSONObject objItem = new JSONObject();
					objItem.put("strItemCode", rsItem.getString(1));
					objItem.put("strItemName", rsItem.getString(2));
					objItem.put("strMenuCode", rsItem.getString(3));
					objItem.put("strClientCode", rsItem.getString(4));
					objItem.put("dblItemPrice", rsItem.getDouble(5));
					objItem.put("dblItemSalePrice", rsItem.getDouble(6));
					objItem.put("dblDiscPer", rsItem.getDouble(7));
					objItem.put("strItemForSale", rsItem.getString(8));
					objItem.put("strApplyDisc", rsItem.getString(9));
					jsonArrItems.put(objItem);
				}
				if (jsonArrItems.length() > 0) {
					obj.putOpt("ItemDtls", jsonArrItems);
				}

				rsItem.close();

				arrMenu.put(obj);
			}
			rsMenuDtl.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			onlineOrderCon.close();
		} finally {
			try {
				onlineOrderCon.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// return
			// Response.status(201).entity(jsonCategory.toString()).build();
			return Response.status(201).entity(arrMenu.toString()).build();

		}
	}

	@GET
	@Path("/funGetCustomerDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetCustomerDetail(@QueryParam("MobileNo") String mobileNo,
			@QueryParam("ClientCode") String clientCode) {
		return funGetCustomerDetails(mobileNo, clientCode);
	}

	private String funGetCustomerDetails(String mobileNo, String clientCode) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement st = null;
		JSONObject jObj = new JSONObject();

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();
			JSONArray arrObj = new JSONArray();
			String sql = " select a.strCustomerCode,a.strCustomerName,a.strStreetName,a.strCustomerType,b.strCustType "
					+ " from tblcustomermaster a, tblcustomertypemaster b  "
					+ " where a.strCustomerType=b.strCustTypeCode " + " and a.longMobileNo='" + mobileNo + "' ";
			System.out.println(sql);
			ResultSet rsCustInfo = st.executeQuery(sql);
			if (rsCustInfo.next()) {
				JSONObject obj = new JSONObject();
				obj.put("CustCode", rsCustInfo.getString(1));
				obj.put("CustName", rsCustInfo.getString(2));
				obj.put("BuildingName", rsCustInfo.getString(3));
				obj.put("CustTypeCode", rsCustInfo.getString(4));
				obj.put("CustType", rsCustInfo.getString(5));
				arrObj.put(obj);
			}
			if (arrObj.length() == 0) {
				JSONObject obj = new JSONObject();
				obj.put("CustCode", "No Customer Found");
				arrObj.put(obj);
			}
			jObj.put("CustomerDetails", arrObj);
			rsCustInfo.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jObj.toString();
	}

	@GET
	@Path("/funSaveNewCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	public String funSaveNewCustomerDtl(@QueryParam("CustomerName") String cutomerName,
			@QueryParam("MobileNo") String mobileNo, @QueryParam("Address") String address,
			@QueryParam("ClientCode") String clientCode, @QueryParam("UserCode") String userCode,
			@QueryParam("DateTime") String dateTime, @QueryParam("EmailId") String emailId) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection posCon = null, conOnlineOrder = null;
		Statement st = null, st1 = null;
		int retRows = 0;
		String customerCode = getCustomercode(clientCode);
		JSONObject jObj = new JSONObject();
		try {
			if (funCheckMobileNoForCustomer(mobileNo)) {
				jObj.put("CustomerStatus", "false");
				jObj.put("Reason", "MobileNo");
				System.out.println("Cust");
			} else {

				posCon = objDb.funOpenAPOSCon("mysql", "master");
				conOnlineOrder = objDb.funOpenOnlineOrderCon("mysql", "master");
				st = posCon.createStatement();
				st1 = conOnlineOrder.createStatement();

				String deleteSql = "delete from tblcustomermaster " + "where strCustomerCode='" + customerCode + "' ";
				st.executeUpdate(deleteSql);
				st1.executeUpdate(deleteSql);

				String sql = "insert into tblcustomermaster "
						+ "(strCustomerCode,strCustomerName,strBuldingCode,strBuildingName,strStreetName,strLandmark,strArea,strCity "
						+ ",strState,intPinCode,longMobileNo,longAlternateMobileNo,strOfficeBuildingCode,strOfficeBuildingName "
						+ ",strOfficeStreetName,strOfficeLandmark,strOfficeArea,strOfficeCity,strOfficePinCode,strOfficeState "
						+ ",strOfficeNo,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strDataPostFlag,strClientCode "
						+ ",strOfficeAddress,strExternalCode,strCustomerType,dteDOB,strGender,dteAnniversary,strEmailId,strCRMId) "
						+ "values('" + customerCode + "','" + cutomerName + "','','','" + address + "','','','' "
						+ ",'','','" + mobileNo + "','','','','','','','','','','','" + userCode + "','" + userCode
						+ "','" + dateTime + "' " + ",'" + dateTime + "','N','" + clientCode
						+ "','N','','CT001','','Female','','" + emailId + "','' )";
				System.out.println(sql);

				retRows = st.executeUpdate(sql);
				retRows = st1.executeUpdate(sql);

				if (retRows > 0) {
					jObj.put("CustomerStatus", customerCode);
					jObj.put("Reason", "Success");
				} else {
					jObj.put("CustomerStatus", "false");
					jObj.put("Reason", "Error");
				}

				posCon.close();
				conOnlineOrder.close();
				st.close();
				st1.close();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				jObj.put("CustomerStatus", "false");
				jObj.put("Reason", "Exception");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			return jObj.toString();
		}
	}

	/**
	 * This method is used to check mobile no for customer
	 *
	 * @param mobileNo
	 * @return
	 */
	private boolean funCheckMobileNoForCustomer(String mobileNo) {

		boolean flgCustomerCount = false;
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection con = null;
		Statement st = null;

		try {
			con = objDb.funOpenAPOSCon("mysql", "master");
			st = con.createStatement();

			if (!mobileNo.isEmpty()) {
				String sql = "select longMobileNo from tblcustomermaster where longMobileNo='" + mobileNo + "';";
				System.out.println(sql);
				ResultSet rsCustomerData = st.executeQuery(sql);
				if (rsCustomerData.next()) {
					flgCustomerCount = true;
				}
				rsCustomerData.close();
			}

		} catch (Exception e) {
			flgCustomerCount = true;
			e.printStackTrace();
		} finally {
			return flgCustomerCount;
		}
	}

	private String getCustomercode(String clientCode) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		String customerCode = "", strCode = "", code = "";
		long lastNo = 1;
		String propertCode = clientCode.substring(4);
		try {
			cmsCon = objDb.funOpenAPOSCon("mysql", "master");
			st = cmsCon.createStatement();

			String sql = "select count(*) from tblcustomermaster";
			ResultSet rsCustCode = st.executeQuery(sql);
			rsCustCode.next();
			int cntCustCode = rsCustCode.getInt(1);
			rsCustCode.close();

			if (cntCustCode > 0) {
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
			} else {
				sql = "select longCustSeries from tblsetup";
				ResultSet rsCustSeries = st.executeQuery(sql);
				if (rsCustSeries.next()) {
					lastNo = Long.parseLong(rsCustSeries.getString(1));
				}
				rsCustSeries.close();
				customerCode = propertCode + "C" + String.format("%07d", lastNo);
				// CustCode = "C0000001";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerCode;
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funSaveBill")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostBillHdDataToHOPOS(JSONObject objBillData) {
		String data = "";
		String billNo = "";
		String checkHomeDelivery = "";
		String response = "false";
		Iterator callfunction = objBillData.keys();
		while (callfunction.hasNext()) {
			String key = callfunction.next().toString();
			JSONArray mJsonArray = null;
			try {
				mJsonArray = (JSONArray) objBillData.get(key);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (key.equalsIgnoreCase("BillDtlInfo") && mJsonArray != null) {
				System.out.println("BillDtl= " + key);
				if (funInsertBillDtlData(mJsonArray) > 0) {

					response = "BillDtl";
				} else {
					response = "false";
				}
			} else if (key.equalsIgnoreCase("HomeDeliveryDtl") && mJsonArray != null) {
				System.out.println(key);
				if (funInsertHomeDeliveryData(mJsonArray) > 0) {
					response = "true";
				} else {
					response = "false";
				}
			}

		}

		return Response.status(201).entity(response).build();
	}

	@SuppressWarnings("finally")
	private int funInsertBillDtlData(JSONArray mJsonArray) {
		int res = 0;
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection onlineOrderCon = null;
		Statement st = null;
		String insert_qry = "";
		String sql = "", deleteSql;

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "transaction");
			st = onlineOrderCon.createStatement();

			insert_qry = "INSERT INTO `tblbilldtl` (`strItemCode`, `strItemName`,"
					+ " `strBillNo`, `strAdvBookingNo`, `dblRate`, `dblQuantity`, "
					+ "`dblAmount`, `dblTaxAmount`, `dteBillDate`, `strKOTNo`, "
					+ "`strClientCode`, `strCustomerCode`, `tmeOrderProcessing`, "
					+ "`strDataPostFlag`, `strMMSDataPostFlag`, `strManualKOTNo`, "
					+ "`tdhYN`, `strPromoCode`,`strCounterCode`,`strWaiterNo`,`dblDiscountAmt`,`strOrderNo`) "
					+ "VALUES";

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) {
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String BillNo = getOrdercode();
				String ClientCode = mJsonObject.get("ClientCode").toString();

				deleteSql = "delete from tblbilldtl " + "where strBillNo='" + BillNo + "' and strClientCode='"
						+ ClientCode + "'";
				st.executeUpdate(deleteSql);

				String ItemCode = mJsonObject.get("ItemCode").toString();
				String ItemName = mJsonObject.get("ItemName").toString();
				String AdvBookingNo = mJsonObject.get("AdvBookingNo").toString();
				double Rate = Double.parseDouble(mJsonObject.get("Rate").toString());
				double Quantity = Double.parseDouble(mJsonObject.get("Quantity").toString());
				double Amount = Double.parseDouble(mJsonObject.get("Amount").toString());
				double TaxAmount = Double.parseDouble(mJsonObject.get("TaxAmount").toString());
				String BillDate = mJsonObject.get("BillDate").toString();
				String KOTNo = mJsonObject.get("KOTNo").toString();
				String CustomerCode = mJsonObject.get("CustomerCode").toString();
				String OrderProcessing = mJsonObject.get("OrderProcessing").toString();
				String DataPostFlag = mJsonObject.get("DataPostFlag").toString();
				String MMSDataPostFlag = mJsonObject.get("MMSDataPostFlag").toString();
				String ManualKOTNo = mJsonObject.get("ManualKOTNo").toString();
				String tdhYN = mJsonObject.get("tdhYN").toString();
				String promoCode = mJsonObject.get("PromoCode").toString();
				String counterCode = mJsonObject.get("CounterCode").toString();
				String waiterNo = mJsonObject.get("WaiterNo").toString();
				String discountAmt = mJsonObject.get("DiscountAmt").toString();

				System.out.println(BillDate + " = " + BillNo);

				sql += ",('" + ItemCode + "','" + ItemName + "','" + BillNo + "','" + AdvBookingNo + "','" + Rate + "',"
						+ "'" + Quantity + "','" + Amount + "','" + TaxAmount + "','" + BillDate + "'," + "'" + KOTNo
						+ "','" + ClientCode + "','" + CustomerCode + "','" + OrderProcessing + "','N'," + "'"
						+ MMSDataPostFlag + "','" + ManualKOTNo + "','" + tdhYN + "','" + promoCode + "'," + "'"
						+ counterCode + "','" + waiterNo + "','" + discountAmt + "','" + BillNo + "')";

				flgData = true;
			}

			if (flgData) {
				sql = sql.substring(1, sql.length());
				insert_qry += " " + sql;
				try {
					System.out.println(insert_qry);
					res = st.executeUpdate(insert_qry);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				res = 1;
			}

		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		} finally {
			try {
				st.close();
				onlineOrderCon.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return res;
		}
	}

	@SuppressWarnings("finally")
	private int funInsertHomeDeliveryData(JSONArray mJsonArray) {
		int res = 0;
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		boolean flgData = false;
		Connection onlineOrderCon = null;
		Statement st = null;
		String insert_qry = "";
		String sql = "", deleteSql = "";

		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "transaction");
			st = onlineOrderCon.createStatement();

			insert_qry = "INSERT INTO `tblhomedelivery` " + "(`strBillNo`,`strCustomerCode`, `dteDate`, `tmeTime`"
					+ ", `strCustAddressLine1`, `strCustAddressLine2`, `strCustAddressLine3`"
					+ ",`strCustAddressLine4`, `strCustCity`,`strDataPostFlag`, `strClientCode`,`strOrderNo`) VALUES";

			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) {
				mJsonObject = (JSONObject) mJsonArray.get(i);

				String billNo = getOrdercode();
				String clientCode = mJsonObject.get("ClientCode").toString();

				deleteSql = "delete from tblhomedelivery " + "where strBillNo='" + billNo + "' and strClientCode='"
						+ clientCode + "'";
				st.executeUpdate(deleteSql);

				String custCode = mJsonObject.get("CustomerCode").toString();
				String date = mJsonObject.get("Date").toString();
				String time = mJsonObject.get("Time").toString();
				String custAddr1 = mJsonObject.get("CustAddress1").toString();
				String custAddr2 = mJsonObject.get("CustAddress2").toString();
				String custAddr3 = mJsonObject.get("CustAddress3").toString();
				String custAddr4 = mJsonObject.get("CustAddress4").toString();
				String custCity = mJsonObject.get("CustCity").toString();
				String dataPostFlag = mJsonObject.get("DataPostFlag").toString();

				sql += ",('" + billNo + "','" + custCode + "','" + date + "','" + time + "'" + ",'" + custAddr1 + "','"
						+ custAddr2 + "','" + custAddr3 + "'" + ",'" + custAddr4 + "','" + custCity + "','"
						+ dataPostFlag + "','" + clientCode + "','" + billNo + "')";
				flgData = true;
			}

			if (flgData) {
				sql = sql.substring(1, sql.length());
				insert_qry += " " + sql;
				System.out.println(insert_qry);
				try {
					res = st.executeUpdate(insert_qry);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				res = 1;
			}

		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		} finally {
			try {
				st.close();
				onlineOrderCon.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return res;
		}
	}

	private String getOrdercode() {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection onlineOrderCon = null;
		Statement st = null;
		String orderCode = "", strCode = "", code = "";
		long lastNo = 1;
		// String propertCode=clientCode.substring(4);
		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();

			String sql = "select count(*) from tblhomedelivery";
			ResultSet rsOrderCode = st.executeQuery(sql);
			rsOrderCode.next();
			int cntOrderCode = rsOrderCode.getInt(1);
			rsOrderCode.close();

			if (cntOrderCode > 0) {
				sql = "select max(strOrderNo) from tblhomedelivery";
				rsOrderCode = st.executeQuery(sql);
				rsOrderCode.next();
				code = rsOrderCode.getString(1);
				StringBuilder sb = new StringBuilder(code);

				strCode = sb.substring(1, sb.length());

				lastNo = Long.parseLong(strCode);
				lastNo++;
				orderCode = "O" + String.format("%07d", lastNo);

				rsOrderCode.close();
			} else {
				orderCode = "O0000001";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderCode;
	}

	@GET
	@Path("/funGetUpdatedOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	public String funGetAllUodatedOrder(@QueryParam("clientCode") String strClientCode) {
		return funGetAllUodatedOrderDetails(strClientCode);
	}

	/*
	 * @SuppressWarnings("finally") private String
	 * funGetAllUodatedOrderDetails(String strClientCode) { clsDatabaseConnection
	 * objDb=new clsDatabaseConnection(); JSONObject jObjOrder = new JSONObject();
	 * Connection onlineOrderCon = null; Statement st = null;
	 * 
	 * try { onlineOrderCon=objDb.funOpenPOSCon("mysql","master"); st =
	 * onlineOrderCon.createStatement(); String sql =
	 * " SELECT a.strOrderNo,a.strCustomerCode,b.strCustomerName,a.dteDate " +
	 * " FROM tblorderhd a, tblcustomermaster b " +
	 * " WHERE a.strCustomerCode=b.strCustomerCode" +
	 * " AND a.strClientCode='"+strClientCode+"' AND a.strDataPostFlag ='N'";
	 * JSONArray arrObjOrder = new JSONArray(); ResultSet rsOrder =
	 * st.executeQuery(sql);
	 * 
	 * while (rsOrder.next()) {
	 * 
	 * JSONObject objOrder = new JSONObject(); objOrder.put("OrderCode",
	 * rsOrder.getString(1)); objOrder.put("CustomerCode", rsOrder.getString(2));
	 * objOrder.put("CustomerName", rsOrder.getString(3)); objOrder.put("OrderDate",
	 * rsOrder.getString(4)); objOrder.put("ClientCode", strClientCode);
	 * arrObjOrder.put(objOrder); } rsOrder.close(); jObjOrder.put("OrderDtls",
	 * arrObjOrder);
	 * 
	 * } catch (Exception e) { new clsUtilityFunctions().funWriteErrorLog(e);
	 * e.printStackTrace(); } finally { try { if (null != st) { st.close(); } }
	 * catch (SQLException e) { e.printStackTrace(); } return jObjOrder.toString();
	 * } }
	 */
	@SuppressWarnings("finally")
	private String funGetAllUodatedOrderDetails(String strClientCode) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		JSONObject jObjOrder = new JSONObject();
		Connection onlineOrderCon = null;
		Statement st = null;

		try {

			onlineOrderCon = objDb.funOpenPOSCon("mysql", "master");
			st = onlineOrderCon.createStatement();
			String sql = "", strPOSCode = "";
			// String strPropertyPOSCode = strClientCode + ".P01";

			String posCodeInQuery = "(";
			int i = 0;

			sql = "select a.strPosCode,a.strPosName,a.strPropertyPOSCode " + "from tblposmaster a "
					+ "where left(a.strPropertyPOSCode,7)='" + strClientCode + "' ";
			ResultSet rspos = st.executeQuery(sql);
			while (rspos.next()) {
				if (i == 0) {
					posCodeInQuery = posCodeInQuery + "'" + rspos.getString(1) + "' ";
				} else {
					posCodeInQuery = posCodeInQuery + ",'" + rspos.getString(1) + "' ";
				}
				
				i++;
			}
			posCodeInQuery = posCodeInQuery + ")";

			sql = " SELECT a.strOrderNo,a.dteDate,"
					+ " b.strCustomerName,b.strBuildingName,b.strStreetName,b.strLandmark,b.strArea,b.strCity,"
					+ " b.strState,b.intPinCode,b.longMobileNo,b.longAlternateMobileNo,b.strOfficeBuildingName,"
					+ " b.strOfficeStreetName,b.strOfficeLandmark,b.strOfficeArea,b.strOfficeCity,b.strOfficePinCode,b.strOfficeState"
					+ " ,b.strOfficeNo,b.strOfficeAddress,b.strCustomerType,b.dteDOB ,b.strGender,b.dteAnniversary,b.strEmailId,b.strCRMId"
					+ " ,b.strCustAddress,b.strTempAddress,b.strTempStreet,b.strTempLandmark,b.strGSTNo,a.strCustAddressLine1"
					+ "  FROM tblorderhd a, tblcustomermaster b " + "  WHERE a.strCustomerCode=b.strCustomerCode"
					+ "  AND a.strPOSCode in " + posCodeInQuery + " AND a.strDataPostFlag ='N'";
			JSONArray arrObjOrder = new JSONArray();
			ResultSet rsOrder = st.executeQuery(sql);

			while (rsOrder.next()) {
				JSONObject objOrder = new JSONObject();

				objOrder.put("OrderCode", rsOrder.getString(1));
				objOrder.put("OrderDate", rsOrder.getString(2));
				objOrder.put("CustomerName", rsOrder.getString(3));
				objOrder.put("strBuildingName", rsOrder.getString(4));
				objOrder.put("strStreetName", rsOrder.getString(5));
				objOrder.put("strLandmark", rsOrder.getString(6));
				objOrder.put("strArea", rsOrder.getString(7));
				objOrder.put("strCity", rsOrder.getString(8));
				objOrder.put("strState", rsOrder.getString(9));
				objOrder.put("intPinCode", rsOrder.getString(10));
				objOrder.put("longMobileNo", rsOrder.getString(11));
				objOrder.put("longAlternateMobileNo", rsOrder.getString(12));
				objOrder.put("strOfficeBuildingName", rsOrder.getString(13));
				objOrder.put("strOfficeStreetName", rsOrder.getString(14));
				objOrder.put("strOfficeLandmark", rsOrder.getString(15));
				objOrder.put("strOfficeArea", rsOrder.getString(16));
				objOrder.put("strOfficeCity", rsOrder.getString(17));
				objOrder.put("strOfficePinCode", rsOrder.getString(18));

				objOrder.put("strOfficeState", rsOrder.getString(19));
				objOrder.put("strOfficeNo", rsOrder.getString(20));
				objOrder.put("strOfficeAddress", rsOrder.getString(21));
				objOrder.put("strCustomerType", rsOrder.getString(22));
				objOrder.put("dteDOB", rsOrder.getString(23));
				objOrder.put("strGender", rsOrder.getString(24));
				objOrder.put("dteAnniversary", rsOrder.getString(25));
				objOrder.put("strEmailId", rsOrder.getString(26));
				objOrder.put("strCRMId", rsOrder.getString(27));
				objOrder.put("strCustAddress", rsOrder.getString(28));
				objOrder.put("strTempAddress", rsOrder.getString(29));
				objOrder.put("strTempStreet", rsOrder.getString(30));
				objOrder.put("strTempLandmark", rsOrder.getString(31));
				objOrder.put("strGSTNo", rsOrder.getString(32));
				objOrder.put("SelectCustAddr", rsOrder.getString(33));

				arrObjOrder.put(objOrder);
			}
			rsOrder.close();
			jObjOrder.put("OrderDtls", arrObjOrder);

		} catch (Exception e) {
			new clsUtilityFunctions().funWriteErrorLog(e);
			e.printStackTrace();
		} finally {
			try {
				if (null != st) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return jObjOrder.toString();
		}
	}

	@GET
	@Path("/funGetOrderDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetOrderDetails(@QueryParam("OrderCode") String OrderCode,
			@QueryParam("clientCode") String strClientCode) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		JSONObject jObjOrder = new JSONObject();
		Connection onlineOrderCon = null;
		Statement st = null;

		String sql = " select b.strItemCode,b.strItemName,b.dblQuantity,b.dblAmount,b.dteBillDate "
				+ " from tblorderhd a,tblorderdtl b " + " where a.strOrderNo=b.strOrderNo " + " and a.strOrderNo='"
				+ OrderCode + "' ";

		JSONArray arrObj = new JSONArray();
		try {
			onlineOrderCon = objDb.funOpenPOSCon("mysql", "master");
			st = onlineOrderCon.createStatement();

			ResultSet rsData = st.executeQuery(sql);
			while (rsData.next()) {
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsData.getString(1));
				obj.put("ItemName", rsData.getString(2));
				obj.put("Qty", rsData.getString(3));
				obj.put("Amount", rsData.getString(4));
				obj.put("BillDate", rsData.getString(5));
				arrObj.put(obj);
			}
			rsData.close();

			jObjOrder.put("ItemDtls", arrObj);

		} catch (Exception e) {
			new clsUtilityFunctions().funWriteErrorLog(e);
			e.printStackTrace();
		} finally {
			try {
				if (null != st) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return jObjOrder.toString();
		}
	}

	@GET
	@Path("/funUpdateOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public String funUpdateOrder(@QueryParam("OrderCode") String OrderCode,
			@QueryParam("clientCode") String strClientCode, @QueryParam("BillNo") String BillNo,
			@QueryParam("DelBoy") String DelBoy, @QueryParam("BillTime") String BillTime) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		JSONObject jObjOrder = new JSONObject();
		Connection onlineOrderCon = null;
		Statement st = null;

		try {
			onlineOrderCon = objDb.funOpenPOSCon("mysql", "master");
			st = onlineOrderCon.createStatement();

			String sql = " update tblorderhd a " + " set a.strDataPostFlag ='Y' " + " ,strDPCode='" + DelBoy + "' "
					+ ",strBillNo='" + BillNo + "' " + " where a.strOrderNo='" + OrderCode + "' "
					+ " and a.strDataPostFlag='N' ";
			st.executeUpdate(sql);
			sql = "update tblorderdtl a set a.strDataPostFlag ='Y' where a.strOrderNo='" + OrderCode + "' "
					+ " and a.strDataPostFlag='N' ";
			st.executeUpdate(sql);

			jObjOrder.put("Update", "Done");

		} catch (Exception e) {
			new clsUtilityFunctions().funWriteErrorLog(e);
			e.printStackTrace();
		} finally {
			try {
				if (null != st) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return jObjOrder.toString();
		}
	}

	@POST
	@Path("/funUpdateBillNo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funUpdateBillNo(JSONObject objOrderData) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		JSONObject jObjOrder = new JSONObject();
		Connection onlineOrderCon = null;
		Statement st = null;
		String response = "false";
		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();

			String orderCode = objOrderData.getString("OrderCode");
			String clientCode = objOrderData.getString("ClientCode");
			String billNo = objOrderData.getString("BillNo");

			String sqlUpdateBillDtl = " update tblbilldtl set strBillNo='" + billNo + "' " + " where strOrderNo='"
					+ orderCode + "' ";
			System.out.println("sqlUpdateBillDtl:" + sqlUpdateBillDtl);

			String sqlUpdateHomeDelivery = " update tblhomedelivery set strBillNo='" + billNo
					+ "',strBillGenerated='Y' " + " where strOrderNo='" + orderCode + "' ";
			System.out.println("sqlUpdateHomeDelivery:" + sqlUpdateHomeDelivery);

			st.executeUpdate(sqlUpdateHomeDelivery);
			st.executeUpdate(sqlUpdateBillDtl);
			funBuildSMS(billNo);

			response = "true";

		} catch (Exception e) {
			new clsUtilityFunctions().funWriteErrorLog(e);
			e.printStackTrace();
		} finally {
			try {
				if (null != st) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return Response.status(201).entity(response).build();
		}
	}

	private void funBuildSMS(String billno) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		Connection cmsCon = null;
		Statement st = null;
		try {
			cmsCon = objDb.funOpenAPOSCon("mysql", "transaction");
			st = cmsCon.createStatement();

			String result = "", result1 = "", result2 = "", result3 = "", result4 = "", result5 = "", result6 = "",
					result7 = "", sendSMSYN = "";
			String mainSms = "", sql = "", operationType = "", homeDeliverySMS = "", billSettelementSMS = "",
					sendHomeDeliverySMSYN = "", sendBillSettlementSMSYN = "";
			String SMSType = "", smsData = "", telephoneNo = "", SMSApi = "", toEmailId = "", customerName = "",
					fromEmailId = "", fromEmailPass = "";

			sql = "select a.strHomeDeliverySMS,a.strBillStettlementSMS,a.strSendHomeDelSMS,a.strSendBillSettlementSMS,a.strSMSType,a.intTelephoneNo,a.strSMSApi,a.strSenderEmailId,a.strEmailPassword from tblsetup a where a.strPOSCode='P01' ;";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				homeDeliverySMS = rs.getString(1);
				billSettelementSMS = rs.getString(2);
				sendHomeDeliverySMSYN = rs.getString(3);
				sendBillSettlementSMSYN = rs.getString(4);
				SMSType = rs.getString(5);
				telephoneNo = rs.getString(6);
				SMSApi = rs.getString(7);
				fromEmailId = rs.getString(8);
				fromEmailPass = rs.getString(9);
			}
			rs.close();

			System.out.println(sql);
			sql = " select strOperationType from tblbillhd where strBillNo='" + billno + "' ";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				operationType = rs.getString(1);
				if (operationType.equalsIgnoreCase("Home Delivery")) {
					if (sendHomeDeliverySMSYN.equalsIgnoreCase("Y")) {
						smsData = homeDeliverySMS;
						sendSMSYN = "Y";
					}

				} else {
					if (sendBillSettlementSMSYN.equalsIgnoreCase("Y")) {
						smsData = billSettelementSMS;
						sendSMSYN = "Y";
					}

				}

			}
			rs.close();

			if (sendSMSYN.equalsIgnoreCase("Y")) {
				if (operationType.equalsIgnoreCase("Home Delivery")) {
					sql = "select c.strCustomerName,c.longMobileNo,a.dblGrandTotal "
							+ " ,DATE_FORMAT(a.dteBillDate,'%m-%d-%Y'),time(a.dteBillDate) "
							+ " ,a.strUserCreated,ifnull(d.strDPName,''),strEmailId,a.dblTaxAmt "
							+ " from tblbillhd a,tblcustomermaster c ,tblhomedelivery b "
							+ " left outer join tbldeliverypersonmaster d on b.strDPCode=d.strDPCode "
							+ " where a.strBillNo='" + billno + "' and a.strBillNo=b.strBillNo "
							+ " and a.strCustomerCode=c.strCustomerCode ";
				} else {
					sql = "select ifnull(c.strCustomerName,''),ifnull(c.longMobileNo,'NA')"
							+ " ,a.dblGrandTotal ,DATE_FORMAT(a.dteBillDate,'%m-%d-%Y')"
							+ " ,time(a.dteBillDate),a.strUserCreated,ifnull(d.strDPName,''),strEmailId,a.dblTaxAmt "
							+ " from tblbillhd a left outer join tblhomedelivery b on a.strBillNo=b.strBillNo "
							+ " left outer join tbldeliverypersonmaster d on b.strDPCode=d.strDPCode "
							+ " left outer join tblcustomermaster c on a.strCustomerCode=c.strCustomerCode "
							+ " where a.strBillNo='" + billno + "'";
				}
			}
			System.out.println(sql);
			System.out.println("sms" + smsData);
			String date = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
			ResultSet rs_SqlGetSMSData = st.executeQuery(sql);
			while (rs_SqlGetSMSData.next()) {
				toEmailId = rs_SqlGetSMSData.getString(8);
				customerName = rs_SqlGetSMSData.getString(1);
				JSONArray jsonArray = funGetBillDetails(billno);

				int intIndex = smsData.indexOf("%%BILL NO");
				if (intIndex != -1) {
					result = smsData.replaceAll("%%BILL NO", billno);
					mainSms = result;
				}
				int intIndex1 = mainSms.indexOf("%%CUSTOMER NAME");

				if (intIndex1 != -1) {
					result1 = mainSms.replaceAll("%%CUSTOMER NAME", rs_SqlGetSMSData.getString(1));
					mainSms = result1;
				}
				int intIndex2 = mainSms.indexOf("%%BILL AMT");

				if (intIndex2 != -1) {
					result2 = mainSms.replaceAll("%%BILL AMT", rs_SqlGetSMSData.getString(3));
					mainSms = result2;
				}
				int intIndex3 = mainSms.indexOf("%%DATE");

				if (intIndex3 != -1) {
					result3 = mainSms.replaceAll("%%DATE", rs_SqlGetSMSData.getString(4));
					mainSms = result3;
				}
				int intIndex4 = mainSms.indexOf("%%DELIVERY BOY");

				if (intIndex4 != -1) {
					result4 = mainSms.replaceAll("%%DELIVERY BOY", rs_SqlGetSMSData.getString(7));
					mainSms = result4;
				}
				int intIndex5 = mainSms.indexOf("%%ITEMS");

				if (intIndex5 != -1) {
					result5 = mainSms.replaceAll("%%ITEMS", "");
					mainSms = result5;
				}
				int intIndex6 = mainSms.indexOf("%%USER");

				if (intIndex6 != -1) {
					result6 = mainSms.replaceAll("%%USER", rs_SqlGetSMSData.getString(6));
					mainSms = result6;
				}
				int intIndex7 = mainSms.indexOf("%%TIME");

				if (intIndex7 != -1) {
					result7 = mainSms.replaceAll("%%TIME", rs_SqlGetSMSData.getString(5));
					mainSms = result7;
				}

				String fromTelNo = telephoneNo;
				String[] sp = fromTelNo.split(",");
				if (sp.length > 0) {
					fromTelNo = sp[0];
				}

				if (SMSType.equalsIgnoreCase("Cellx")) {
					if (!rs_SqlGetSMSData.getString(2).isEmpty()) {
						String smsURL = SMSApi.replace("<to>", rs_SqlGetSMSData.getString(2))
								.replace("<from>", fromTelNo).replace("<MSG>", mainSms).replaceAll(" ", "%20");

					}
				} else if (SMSType.equalsIgnoreCase("Sinfini")) {
					String smsURL = SMSApi.replace("<PHONE>", rs_SqlGetSMSData.getString(2)).replace("<MSG>", mainSms)
							.replaceAll(" ", "%20");
					funSendSMS(smsURL);
					String BodyData = funEmailTableData(jsonArray, billno, date, toEmailId, customerName,
							rs_SqlGetSMSData.getDouble(9));
					int ret = funSendMail(BodyData, billno, date, toEmailId, customerName, fromEmailId, fromEmailPass);
				} else if (SMSType.equalsIgnoreCase("Infyflyer")) {
					// http://sms.infiflyer.co.in/httpapi/httpapi?token=a10bad827db08a4eeec726da63813747&sender=IPREMS&number=<PHONE>&route=2&type=1&sms=<MSG>
					String smsURL = SMSApi.replace("<PHONE>", rs_SqlGetSMSData.getString(2)).replace("<MSG>", mainSms)
							.replaceAll(" ", "%20");
					funSendSMS(smsURL);
					String BodyData = funEmailTableData(jsonArray, billno, date, toEmailId, customerName,
							rs_SqlGetSMSData.getDouble(9));
					int ret = funSendMail(BodyData, billno, date, toEmailId, customerName, fromEmailId, fromEmailPass);
				}
			}

		} catch (Exception e) {
			// objUtility.funWriteErrorLog(e);
			e.printStackTrace();
		}
	}

	private String funSendSMS(String url) {
		System.out.println("URL=" + url);
		StringBuilder output = new StringBuilder();
		try {
			URL hp = new URL(url);
			URLConnection hpCon = hp.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(hpCon.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				output.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("SMS=" + output.toString());
		return output.toString();
	}

	private String funEmailTableData(JSONArray mJsonArray, String voucher, String billDate, String strEmailId,
			String customerName, double taxAmount) {
		double totAmt = 0.00;
		String retStrTableData = "";
		String genrateRow = "";
		String bodyStart = "  <table border='1'>  <tr bgcolor='#3366ff'; style='color : white'>     <th style='color: white'>Sr No.</th>     <th style='color: white'>Item Name</th>     <th style='color: white'>Quantity</th>       <th style='color: white'>Price</th>   </tr> ";
		String totalRow = "", taxRow;

		String endBody = "  </table> Thank you! Keep Ordering :-)  </body>  </html> ";

		try {
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.length(); i++) {
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String itemCode = mJsonObject.get("ItemCode").toString();
				int srNo = i + 1;
				String strItemName = mJsonObject.get("ItemName").toString();
				double dblQty = Double.parseDouble(mJsonObject.get("Qty").toString());

				double price = Double.parseDouble(mJsonObject.get("Amount").toString());
				// price=dblQty*price;
				totAmt += price;
				genrateRow += "<tr>     <th style='color: black'>" + srNo + "</th>     <th style='color: black'>"
						+ strItemName + "</th>     <th style='color: black'>" + dblQty
						+ "</th>       <th style='color: black'>" + price + "</th>   </tr>";

			}
			if (mJsonArray.length() > 0) {
				if (taxAmount > 0) {
					double gTotal = 0.0;
					gTotal = taxAmount + totAmt;

					String Header = "<html> <body>  Dear " + customerName
							+ ",	<br><br><tr<tr> We are pleased to inform you that following items in your Order "
							+ voucher + " have been packed " + " and are ready to be shipped. Please Keep Cash Rs. "
							+ gTotal + " ";
					taxRow = "<tr>     <th colspan='3'>Tax Amount</th>     <th style='color: black'>" + taxAmount
							+ "</th>    </tr>";
					totalRow = "<tr>     <th colspan='3'>Total Payble Amount</th>     <th style='color: black'>"
							+ gTotal + "</th>    </tr>";
					retStrTableData = Header + bodyStart + genrateRow + taxRow + totalRow + endBody;
				} else {
					String Header = "<html> <body>  Dear " + customerName
							+ ",	<br><br><tr<tr> We are pleased to inform you that following items in your Order "
							+ voucher + " have been packed " + " and are ready to be shipped. Please Keep Cash Rs. "
							+ totAmt + " ";
					totalRow = "<tr>     <th colspan='3'>Total Payble Amount</th>     <th style='color: black'>"
							+ totAmt + "</th>    </tr>";
					retStrTableData = Header + bodyStart + genrateRow + totalRow + endBody;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return retStrTableData;
	}

	public int funSendMail(String bodyData, String voucher, String billDate, String tomailId, String customerName,
			String strFromEmailId, String strFromEmailPassword) {
		int ret = 0;
		// String to="ingaleprashant8@gmail.com";//change accordingly
		// String to="gadhave.monika27@gmail.com";
		final String from = strFromEmailId;
		final String pass = strFromEmailPassword;
		// Get the session object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, pass);// change
																// accordingly
			}
		});

		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			// message.setFrom(new
			// InternetAddress("paritoshkumar112@gmail.com"));//change
			// accordingly
			message.setFrom(new InternetAddress(from));// change accordingly
			String[] arrRecipient = tomailId.split(",");

			for (int cnt = 0; cnt < arrRecipient.length; cnt++) {
				System.out.println(arrRecipient[cnt]);
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(arrRecipient[cnt]));
			}
			message.setSubject(" POS Order No. " + voucher + " is ready ");

			// message.setText(msgBody);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			String filePath = "";
			DataSource source = new FileDataSource(filePath);
			String dataMessage = bodyData;
			// Fill the message
			messageBodyPart.setText(dataMessage);
			// send message
			message.setContent(dataMessage, "text/html");
			Transport.send(message);
			System.out.println("message sent successfully");
		} catch (MessagingException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ret = 1;
		return ret;
	}

	public JSONArray funGetBillDetails(String BillNo) {
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		JSONObject jObjOrder = new JSONObject();
		Connection onlineOrderCon = null;
		Statement st = null;

		String sql = "select b.strItemCode,b.strItemName,b.dblQuantity,b.dblAmount,b.dteBillDate "
				+ " from tblhomedelivery a,tblbilldtl b " + " where a.strBillNo=b.strBillNo " + " and a.strBillNo='"
				+ BillNo + "' ";

		JSONArray arrObj = new JSONArray();
		try {
			onlineOrderCon = objDb.funOpenOnlineOrderCon("mysql", "master");
			st = onlineOrderCon.createStatement();

			ResultSet rsData = st.executeQuery(sql);
			while (rsData.next()) {
				JSONObject obj = new JSONObject();

				obj.put("ItemCode", rsData.getString(1));
				obj.put("ItemName", rsData.getString(2));
				obj.put("Qty", rsData.getString(3));
				obj.put("Amount", rsData.getString(4));
				obj.put("BillDate", rsData.getString(5));
				arrObj.put(obj);
			}
			rsData.close();

			// jObjOrder.put("ItemDtls", arrObj);

		} catch (Exception e) {
			new clsUtilityFunctions().funWriteErrorLog(e);
			e.printStackTrace();
		} finally {
			try {
				if (null != st) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return arrObj;
		}
	}

	@GET
	@Path("/funGetCallCenterOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetCallCenterOrders(@QueryParam("strClientCode") String strClientCode,
			@QueryParam("strPOSCode") String strPOSCode, @QueryParam("dteFromDate") String dteFromDate,
			@QueryParam("dteToDate") String dteToDate) {
		JSONObject rootJsonObject = new JSONObject();

		StringBuilder sqlBuilder = new StringBuilder();

		try {
			Connection connectionToHOPOS = clsDatabaseConnection.DBPOSCONNECTION;
			Statement statementforHd = connectionToHOPOS.createStatement();
			Statement statementforDtl = connectionToHOPOS.createStatement();
			Statement stForPOSCode = connectionToHOPOS.createStatement();

			String posCodeFromHO = "", posCodeFromOutlet = strPOSCode;
			String propertyPOSCode = strClientCode + "." + posCodeFromOutlet;
			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strPosCode,a.strPosName,a.strPropertyPOSCode "
					+ "from tblposmaster a where a.strPropertyPOSCode='" + propertyPOSCode + "'");
			ResultSet rsPOSCode = stForPOSCode.executeQuery(sqlBuilder.toString());
			if (rsPOSCode.next()) {
				posCodeFromHO = rsPOSCode.getString(1);
			}
			rsPOSCode.close();

			JSONArray columnsHeaderList = new JSONArray();
			columnsHeaderList.put("Item");
			columnsHeaderList.put("Rate");
			columnsHeaderList.put("Qty");
			columnsHeaderList.put("Amount");
			columnsHeaderList.put("Tax Amount");

			rootJsonObject.put("coumnNames", columnsHeaderList);

			JSONArray listOfOrderHdData = new JSONArray();

			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strOrderNo,b.strCustomerName,c.strPosName"
					+ ",DATE_FORMAT(a.dteDate,'%d-%m-%Y')dteDate,a.strCustAddressLine1  "
					+ "from tblorderhd a,tblcustomermaster b,tblposmaster c "
					+ "where a.strCustomerCode=b.strCustomerCode " + "and a.strPOSCode=c.strPosCode "
					+ "and date(a.dteDate) between '" + dteFromDate + "' and  '" + dteToDate + "' "
					+ "and a.strPOSCode='" + posCodeFromHO + "' ");
			ResultSet rsHDOrders = statementforHd.executeQuery(sqlBuilder.toString());
			while (rsHDOrders.next()) {
				String orderNo = rsHDOrders.getString(1);
				String customerName = rsHDOrders.getString(2);
				String posName = rsHDOrders.getString(3);
				String orderDate = rsHDOrders.getString(4);
				String shippingAddress = rsHDOrders.getString(5);

				JSONObject objOrderHDBean = new JSONObject();

				objOrderHDBean.put("strOrderNo", orderNo);
				objOrderHDBean.put("strCustomerName", customerName);
				objOrderHDBean.put("strPosName", posName);
				objOrderHDBean.put("dteDate", orderDate);
				objOrderHDBean.put("strCustAddressLine1", shippingAddress);

				JSONArray listOfOrderDtlData = new JSONArray();

				sqlBuilder.setLength(0);
				sqlBuilder.append("select * " + "from tblorderdtl a " + "where a.strOrderNo='" + orderNo + "' ");
				ResultSet rsdDtrlOrders = statementforDtl.executeQuery(sqlBuilder.toString());
				while (rsdDtrlOrders.next()) {
					String strItemCode = rsdDtrlOrders.getString(1);
					String strItemName = rsdDtrlOrders.getString(2);
					double dblRate = Double.parseDouble(rsdDtrlOrders.getString(5));
					double dblQuantity = Double.parseDouble(rsdDtrlOrders.getString(6));
					double dblAmount = Double.parseDouble(rsdDtrlOrders.getString(7));
					double dblTaxAmount = Double.parseDouble(rsdDtrlOrders.getString(8));

					JSONObject objOrderDtlBean = new JSONObject();
					objOrderDtlBean.put("strItemCode", strItemCode);
					objOrderDtlBean.put("strItemName", strItemName);
					objOrderDtlBean.put("dblRate", dblRate);
					objOrderDtlBean.put("dblQuantity", dblQuantity);
					objOrderDtlBean.put("dblAmount", dblAmount);
					objOrderDtlBean.put("dblTaxAmount", dblTaxAmount);

					listOfOrderDtlData.put(objOrderDtlBean);
				}
				rsdDtrlOrders.close();

				objOrderHDBean.put("listOfOrderDtlData", listOfOrderDtlData);

				listOfOrderHdData.put(objOrderHDBean);
			}
			rsHDOrders.close();

			rootJsonObject.put("listOfOrderHdData", listOfOrderHdData);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return rootJsonObject;
		}
	}

}