package com.apos.dao;

import java.security.KeyStore.Entry;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsZoneMasterModel;
import com.webservice.util.clsUtilityFunctions;

@Repository("clsPOSBulkItemPricingMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSBulkItemPricingMasterDao {

	@Autowired
	private SessionFactory WebPOSSessionFactory;

	@Autowired
	clsUtilityFunctions objUtilityFunctions;

	public void funUpdateBulkItemPricingMaster(JSONObject jObjMaster)
			throws Exception {
		JSONObject jOBjRet = new JSONObject();
		StringBuilder sqlFilter = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		JSONArray jArr = new JSONArray();
		JSONArray ArrayList = new JSONArray();
		Set<String> setMenuNames = new HashSet<String>();

		String strPOSCode = jObjMaster.getString("posCode");
		String strAreaCode = jObjMaster.getString("areaCode");
		String strCostCenterCode = jObjMaster.getString("costCenterCode");
		String strMenuCode = jObjMaster.getString("menuHeadCode");
		String strSortBy = jObjMaster.getString("strSortBy");
		String expriedItem = jObjMaster.getString("strExpiredItem");
		String User = jObjMaster.getString("User");
		String ClientCode = jObjMaster.getString("ClientCode");
		String dteDateCreated = jObjMaster.getString("dteDateCreated");
		String dteDateEdited = jObjMaster.getString("dteDateEdited");
		ArrayList = jObjMaster.getJSONArray("List");

		sql.setLength(0);
		sql.append("delete from tblmenuitempricingdtl where strPosCode='");
		sql.append(strPOSCode).append("' ");

		if (!strMenuCode.equalsIgnoreCase("All")) {

			sql.append("and strMenuCode='");
			sql.append(strMenuCode).append("' ");
		}

		if (strCostCenterCode.equalsIgnoreCase("All")) {

			sql.append("and strCostCenterCode='");
			sql.append(strCostCenterCode).append("' ");
		}

		if (strAreaCode.equalsIgnoreCase("All")) {

			sql.append("and strAreaCode='");
			sql.append(strAreaCode).append("' ");
		}

		Query query = WebPOSSessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());
		query.executeUpdate();

		sql.setLength(0);

		sql.append("INSERT INTO tblmenuitempricingdtl(strItemCode,strItemName,strPosCode,"
				+ "strMenuCode,strPopular,strPriceSunday,strPriceMonday,strPriceTuesday,strPriceWednesday"
				+ ",strPriceThursday,strPriceFriday,strPriceSaturday,dteFromDate,dteToDate,tmeTimeFrom,"
				+ "strAMPMFrom,tmeTimeTo,strAMPMTo,strCostCenterCode,strTextColor,strUserCreated"
				+ ",strUserEdited,dteDateCreated,dteDateEdited,strAreaCode,strSubMenuHeadCode,strHourlyPricing)"
				+ " VALUES ");
		Map<String, Map<String, String>> map = new HashMap();
		for (int i = 0; i < ArrayList.length(); i++) {

			JSONObject jObj = new JSONObject();
			jObj = ArrayList.getJSONObject(i);

			if (map.containsKey(jObj.getString("posCode"))) {
				Map<String, String> mapOfMenu = map.get(jObj
						.getString("posCode"));
				mapOfMenu.put(jObj.getString("MenuCode"),
						jObj.getString("MenuName"));
			} else {
				Map<String, String> mapOfMenu = new HashMap();
				mapOfMenu.put(jObj.getString("MenuCode"),
						jObj.getString("MenuName"));

				map.put(jObj.getString("posCode"), mapOfMenu);
			}

			String strItemCode = jObj.getString("ItemCode");
			String strItemName = jObj.getString("ItemName");
			String strMenuCode1 = jObj.getString("MenuCode");
			String strMenuName = jObj.getString("MenuName");
			String strPopular = jObj.getString("Popular");
			String strPriceSunday = jObj.getString("PriceSunday");

			String strPriceMonday = jObj.getString("PriceMonday");
			String strPriceTuesday = jObj.getString("PriceTuesday");
			String strPriceWednesday = jObj.getString("PriceWednesday");
			String strPriceThursday = jObj.getString("PriceThursday");
			String strPriceFriday = jObj.getString("PriceFriday");
			String strPriceSaturday = jObj.getString("PriceSaturday");
			String strFromDate = jObj.getString("FromDate");
			String strToDate = jObj.getString("ToDate");
			String strTimeFrom = jObj.getString("TimeFrom");
			String strAMPMFrom = jObj.getString("AMPMFrom");
			String strTimeTo = jObj.getString("TimeTo");
			String strAMPMTo = jObj.getString("AMPMTo");
			String strCostCenterCode1 = jObj.getString("SubMenuHeadCode");
			String strCostCenter = jObj.getString("CostCenter");
			String strTextColor = jObj.getString("TextColor");
			String strAreaCode1 = jObj.getString("Areacode");
			String strArea = jObj.getString("Area");
			String strSubMenuHeadCode = jObj.getString("SubMenuHeadCode");
			String strSubMenuHeadName = jObj.getString("SubMenuHeadName");
			String strHourlyPricing = jObj.getString("HourlyPricing");

			sql.append("('").append(strItemCode).append("','")
					.append(strItemName).append("','").append(strPOSCode);
			sql.append("','").append(strMenuCode1).append("','")
					.append(strPopular).append("',").append(strPriceSunday);
			sql.append(",").append(strPriceMonday).append(",")
					.append(strPriceTuesday);
			sql.append(",").append(strPriceWednesday).append(",")
					.append(strPriceThursday);
			sql.append(",").append(strPriceFriday).append(",")
					.append(strPriceSaturday);
			sql.append(",'").append(strFromDate).append("','")
					.append(strToDate).append("','").append(strTimeFrom);
			sql.append("','").append(strAMPMFrom).append("','")
					.append(strTimeTo).append("','").append(strAMPMTo);
			sql.append("','").append(strCostCenterCode1).append("','")
					.append(strTextColor);
			sql.append("','").append(User).append("','")
					.append(User).append("','")
					.append(dteDateCreated);
			sql.append("','").append(dteDateEdited)
					.append("','");
			sql.append(strAreaCode1);
			sql.append("','").append(strSubMenuHeadCode).append("',");
			sql.append("'").append(strHourlyPricing).append("'),");

		}
		Query query1 = WebPOSSessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());
		query1.executeUpdate();
		for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
			String PosCode = entry.getKey();
			Map<String, String> mapOfMenu = entry.getValue();

			for (Map.Entry<String, String> menuEntry : mapOfMenu.entrySet()) {
				String menuCode = menuEntry.getKey();
				String menuName = menuEntry.getValue();

				sql.setLength(0);
				sql.append("delete from tblmenuitempricinghd where strPosCode='"
						+ PosCode + "' and strMenuCode='" + menuCode + "' ");

				Query query2 = WebPOSSessionFactory.getCurrentSession()
						.createSQLQuery(sql.toString());
				query2.executeUpdate();

				sql.setLength(0);
				sql.append("INSERT INTO tblmenuitempricinghd"
                        + "(strPosCode,strMenuCode,strMenuName,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited)"
                        + "VALUES");
				
				sql.append("('").append(PosCode).append("','");
				sql.append(menuCode).append("','");
				sql.append(menuName).append("','");
				sql.append(User).append("','");
				sql.append(User).append("','");
				sql.append(dteDateCreated).append("','");
				sql.append(dteDateEdited).append("')");

				Query insert = WebPOSSessionFactory.getCurrentSession()
						.createSQLQuery(sql.toString());
				insert.executeUpdate();

			}
		}

	
		
		String sql1 = "update tblmasteroperationstatus set dteDateEdited='" + dteDateCreated + "' "
                + " where strTableName='MenuItemPricing' and strClientCode='" + ClientCode + "'";
		Query query2 = WebPOSSessionFactory.getCurrentSession().createSQLQuery(
				sql1.toString());
		query2.executeUpdate();
	}

	public JSONObject funRetriveBulkItemPricingMaster(JSONObject jObjMaster)
			throws Exception {
		JSONObject jOBjRet = new JSONObject();
		StringBuilder sqlFilter = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		JSONArray jArr = new JSONArray();

		String strPOSCode = jObjMaster.getString("posCode");
		String strAreaCode = jObjMaster.getString("area");
		String strCostCenterCode = jObjMaster.getString("costCenter");
		String strMenuCode = jObjMaster.getString("menuHead");
		String strSortBy = jObjMaster.getString("sortBy");
		String expriedItem = jObjMaster.getString("expriedItem");

		sql.append("SELECT a.strItemCode,b.strItemName,a.strMenuCode,c.strMenuName,a.strPopular,a.strPriceSunday,a.strPriceMonday,a.strPriceTuesday,a.strPriceWednesday, "
				+ "a.strPriceThursday,a.strPriceFriday,a.strPriceSaturday,a.dteFromDate,a.dteToDate,a.tmeTimeFrom, "
				+ "a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,a.strCostCenterCode,d.strCostCenterName,a.strTextColor,a.strAreaCode,f.strAreaName, "
				+ "a.strSubMenuHeadCode,a.strHourlyPricing ,ifNULL(g.strSubMenuHeadName,'ND') as strSubMenuHeadName "
				+ "FROM tblmenuitempricingdtl a  "
				+ "left outer join tblitemmaster b on b.strItemCode=a.strItemCode  "
				+ "left outer join tblmenuhd c on a.strMenuCode=c.strMenuCode "
				+ "left outer join tblcostcentermaster d on a.strCostCenterCode=d.strCostCenterCode "
				+ "left outer join tblareamaster f on a.strAreaCode=f.strAreaCode  "
				+ "left outer join tblsubmenuhead g on a.strSubMenuHeadCode=g.strSubMenuHeadCode");

	
			sqlFilter.append(" Where a.strPosCode='" + strPOSCode + "' ");		

		if (!strMenuCode.equalsIgnoreCase("All")) {
			sqlFilter.append(" and a.strMenuCode='" + strMenuCode + "' ");
		}

		if (!strCostCenterCode.equalsIgnoreCase("All")) {
			sqlFilter.append(" and a.strCostCenterCode='" + strCostCenterCode
					+ "' ");
		}

		if (!strAreaCode.equalsIgnoreCase("All")) {
			sqlFilter.append(" and a.strAreaCode='" + strAreaCode + "' ");
		}

		sqlFilter
				.append(" and c.strMenuName is not null and d.strCostCenterCode is not null and f.strAreaCode is not null ");
		sqlFilter.append(" group by a.strItemCode,a.strHourlyPricing ");

		switch (strSortBy) {

		case "NONE":

			break;
		case "Item Code":
			sqlFilter.append(" order by a.strItemCode ");
			break;
		case "Item Name":
			sqlFilter.append(" order by a.strItemName ");
			break;
		case "Menu Head":
			sqlFilter.append(" order by a.strMenuCode ");
			break;
		case "Cost Center":
			sqlFilter.append(" order by a.strCostCenterCode ");
			break;
		case "Area":
			sqlFilter.append(" order by a.strAreaCode ");
			break;
		case "POS":
			sqlFilter.append(" order by a.strPosCode ");
			break;
		}

		sql.append(sqlFilter);

		Query query = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		List list = query.list();

		if (list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				JSONObject jObj = new JSONObject();
				if (expriedItem.equalsIgnoreCase("No")) {
					if (objUtilityFunctions.funCompareTime(objUtilityFunctions
							.funGetCurrentDateTime("yyyy-MM-dd"), (obj[13]
							.toString())) >= 0) {
						jObj.put("strItemCode", obj[0].toString());
						jObj.put("strItemName", obj[1].toString());
						// jObj.put("strMenuCode",obj[2].toString());
						jObj.put("strMenuName", obj[3].toString());
						jObj.put("strPopular", obj[4].toString());
						jObj.put("strPriceSunday", obj[5].toString());
						jObj.put("strPriceMonday", obj[6].toString());
						jObj.put("strPriceTuesday", obj[7].toString());
						jObj.put("strPriceWednesday", obj[8].toString());
						jObj.put("strPriceThursday", obj[9].toString());
						jObj.put("strPriceFriday", obj[10].toString());
						jObj.put("strPriceSaturday", obj[11].toString());
						jObj.put("dteFromDate", obj[12].toString());
						jObj.put("dteToDate", obj[13].toString());
						jObj.put("tmeTimeFrom", obj[14].toString());
						jObj.put("strAMPMFrom", obj[15].toString());
						jObj.put("tmeTimeTo", obj[16].toString());
						jObj.put("strAMPMTo", obj[17].toString());
						// jObj.put("strCostCenterCode",obj[18].toString());
						jObj.put("strCostCenterName", obj[19].toString());
						jObj.put("strTextColor", obj[20].toString());
						// jObj.put("strAreaCode",obj[21].toString());
						jObj.put("strAreaName", obj[22].toString());
						// jObj.put("strSubMenuHeadCode",obj[23].toString());
						jObj.put("strHourlyPricing", obj[24].toString());
						jObj.put("strSubMenuHeadName", obj[25].toString());
						jObj.put("ISExpired", "NO");
					}
					jArr.put(jObj);
				} else {
					if (objUtilityFunctions.funCompareTime(objUtilityFunctions
							.funGetCurrentDateTime("yyyy-MM-dd"), (obj[13]
							.toString())) >= 0) {
						jObj.put("strItemCode", obj[0].toString());
						jObj.put("strItemName", obj[1].toString());
						// jObj.put("strMenuCode",obj[2].toString());
						jObj.put("strMenuName", obj[3].toString());
						jObj.put("strPopular", obj[4].toString());
						jObj.put("strPriceSunday", obj[5].toString());
						jObj.put("strPriceMonday", obj[6].toString());
						jObj.put("strPriceTuesday", obj[7].toString());
						jObj.put("strPriceWednesday", obj[8].toString());
						jObj.put("strPriceThursday", obj[9].toString());
						jObj.put("strPriceFriday", obj[10].toString());
						jObj.put("strPriceSaturday", obj[11].toString());
						jObj.put("dteFromDate", obj[12].toString());
						jObj.put("dteToDate", obj[13].toString());
						jObj.put("tmeTimeFrom", obj[14].toString());
						jObj.put("strAMPMFrom", obj[15].toString());
						jObj.put("tmeTimeTo", obj[16].toString());
						jObj.put("strAMPMTo", obj[17].toString());
						// jObj.put("strCostCenterCode",obj[18].toString());
						jObj.put("strCostCenterName", obj[19].toString());
						jObj.put("strTextColor", obj[20].toString());
						// jObj.put("strAreaCode",obj[21].toString());
						jObj.put("strAreaName", obj[22].toString());
						// jObj.put("strSubMenuHeadCode",obj[23].toString());
						jObj.put("strHourlyPricing", obj[24].toString());
						jObj.put("strSubMenuHeadName", obj[25].toString());
						jObj.put("ISExpired", "NO");
					} else {
						jObj.put("strItemCode", obj[0].toString());
						jObj.put("strItemName", obj[1].toString());
						// jObj.put("strMenuCode",obj[2].toString());
						jObj.put("strMenuName", obj[3].toString());
						jObj.put("strPopular", obj[4].toString());
						jObj.put("strPriceSunday", obj[5].toString());
						jObj.put("strPriceMonday", obj[6].toString());
						jObj.put("strPriceTuesday", obj[7].toString());
						jObj.put("strPriceWednesday", obj[8].toString());
						jObj.put("strPriceThursday", obj[9].toString());
						jObj.put("strPriceFriday", obj[10].toString());
						jObj.put("strPriceSaturday", obj[11].toString());
						jObj.put("dteFromDate", obj[12].toString());
						jObj.put("dteToDate", obj[13].toString());
						jObj.put("tmeTimeFrom", obj[14].toString());
						jObj.put("strAMPMFrom", obj[15].toString());
						jObj.put("tmeTimeTo", obj[16].toString());
						jObj.put("strAMPMTo", obj[17].toString());
						// jObj.put("strCostCenterCode",obj[18].toString());
						jObj.put("strCostCenterName", obj[19].toString());
						jObj.put("strTextColor", obj[20].toString());
						// jObj.put("strAreaCode",obj[21].toString());
						jObj.put("strAreaName", obj[22].toString());
						// jObj.put("strSubMenuHeadCode",obj[23].toString());
						jObj.put("strHourlyPricing", obj[24].toString());
						jObj.put("strSubMenuHeadName", obj[25].toString());
						jObj.put("ISExpired", "YES");
					}
					jArr.put(jObj);
				}

			}
			jOBjRet.put("jArr", jArr);

		}
		// listRet.add(listSqlQFile);

		return jOBjRet;
	}

}
