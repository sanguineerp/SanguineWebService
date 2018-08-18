package com.pathfinder.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;

@Repository("clsPathFinderDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPathFinderDao
{

	@Autowired
	private SessionFactory	webPOSSessionFactory;

	public List<clsBillHd>  funGetPOSSalesData(String clientCode, String posCode, String fromDate, String toDate)
	{
		JSONObject jRootObj = new JSONObject();
		List<clsBillHd> listBillHd = new ArrayList<clsBillHd>();

		try
		{

			funGetLiveBillData(listBillHd, clientCode, posCode, fromDate, toDate);
			funGetQFileBillData(listBillHd, clientCode, posCode, fromDate, toDate);

			Comparator<clsBillHd> posNameComparator = new Comparator<clsBillHd>()
			{
				@Override
				public int compare(clsBillHd o1, clsBillHd o2)
				{
					return o1.getStrPOSName().compareToIgnoreCase(o2.getStrPOSName());
				}
			};
			Collections.sort(listBillHd, posNameComparator);

			/*JSONArray jRootArr = new JSONArray();

			if (listBillHd.size() > 0)
			{
				for (int i = 0; i < listBillHd.size(); i++)
				{

					clsBillHd objBillHd = listBillHd.get(i);

					JSONObject jBillHdObj = new JSONObject();

					jBillHdObj.put("POS Name", objBillHd.getStrPOSName());
					jBillHdObj.put("Bill No", objBillHd.getStrBillNo());
					jBillHdObj.put("Date", objBillHd.getStrBillDate());
					jBillHdObj.put("Time", objBillHd.getStrBillTime());
					jBillHdObj.put("Settlement Mode", objBillHd.getStrSettlementMode());
					jBillHdObj.put("Sub Total", objBillHd.getDblSubTotal());
					jBillHdObj.put("Discount Amt", objBillHd.getDblDiscountAmt());
					for (int j = 0; j < objBillHd.getListBillTaxDtl().size(); j++)
					{
						clsBillTaxDtl objBillTaxDtl = objBillHd.getListBillTaxDtl().get(j);
						jBillHdObj.put(objBillTaxDtl.getStrTaxDesc(), objBillTaxDtl.getDblTaxAmt());
					}
					jBillHdObj.put("Grand Total", objBillHd.getDblGrandTotal());
					jBillHdObj.put("Transaction Status", objBillHd.getStrTransactionStatus());

					jRootArr.put(jBillHdObj);
				}
			}

			jRootObj.put("records", jRootArr);*/

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return listBillHd;
		}
	}

	private void funGetLiveBillData(List<clsBillHd> listBillHd, String clientCode, String posCode, String fromDate, String toDate)
	{
		// live bill hd data
		
		StringBuilder sqlLiveQueryBuilder=new  StringBuilder();
		sqlLiveQueryBuilder.append("select a.strClientCode,a.strPOSCode,d.strPosName,a.strBillNo,date(a.dteBillDate),TIME_FORMAT(time(a.dteBillDate),'%r'),a.strSettelmentMode "
						+ ",a.dblSubTotal,a.dblDiscountAmt,a.dblTaxAmt,a.dblGrandTotal "
						+ "from tblbillhd a,tblposmaster d  "
						+ "where a.strPOSCode=d.strPosCode "
						+ "and a.strClientCode='" + clientCode + "' "
					    + "and date(a.dtebilldate) between '"+fromDate+"' and '"+toDate+"'  ");
			if(!posCode.equalsIgnoreCase("All"))
			{
				sqlLiveQueryBuilder.append("and a.strPOSCode='"+posCode+"' ");
			}
			
		
		SQLQuery sqlBillHdQuery = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlLiveQueryBuilder.toString());
		
		
		
		List billHdList = sqlBillHdQuery.list();

		if (billHdList != null && billHdList.size() > 0)
		{
			for (int bill = 0; bill < billHdList.size(); bill++)
			{
				Object[] arrBillHd = (Object[]) billHdList.get(bill);

				clsBillHd objBillHd = new clsBillHd();

				/*objBillHd.setStrClientCode(arrBillHd[0].toString());
				objBillHd.setStrPOSCode(arrBillHd[1].toString());*/
				objBillHd.setStrPOSName(arrBillHd[2].toString());
				objBillHd.setStrBillNo(arrBillHd[3].toString());// billNo
				objBillHd.setStrBillDate(arrBillHd[4].toString());//date
				objBillHd.setStrBillTime(arrBillHd[5].toString());
				objBillHd.setStrSettlementMode(arrBillHd[6].toString());
				objBillHd.setDblSubTotal(Double.parseDouble(arrBillHd[7].toString()));
				objBillHd.setDblDiscountAmt(Double.parseDouble(arrBillHd[8].toString()));
				//objBillHd.setDblTaxAmt(Double.parseDouble(arrBillHd[9].toString()));
				objBillHd.setDblGrandTotal(Double.parseDouble(arrBillHd[10].toString()));
				objBillHd.setStrTransactionStatus("Sale");

				// tax dtl data
				SQLQuery sqlBillTaxDtlQuery = webPOSSessionFactory.getCurrentSession().createSQLQuery(
						"select b.strTaxCode,b.strTaxDesc,a.dblTaxAmount "
								+ "from tblbilltaxdtl a,tbltaxhd b "
								+ "where a.strTaxCode=b.strTaxCode "
								+ "and a.strBillNo='" + objBillHd.getStrBillNo() + "' "
										+ "and date(a.dteBillDate)='"+objBillHd.getStrBillDate()+"' ");
				List billTaxDtlList = sqlBillTaxDtlQuery.list();

				List<clsBillTaxDtl> listBillTaxDtl = new ArrayList<clsBillTaxDtl>();

				if (billTaxDtlList != null && billTaxDtlList.size() > 0)
				{
					for (int t = 0; t < billTaxDtlList.size(); t++)
					{
						Object[] arrBillTaxDtl = (Object[]) billTaxDtlList.get(t);

						clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();

						//objBillTaxDtl.setStrTaxCode(arrBillTaxDtl[0].toString());
						objBillTaxDtl.setStrTaxDesc(arrBillTaxDtl[1].toString());
						objBillTaxDtl.setDblTaxAmt(Double.parseDouble(arrBillTaxDtl[2].toString()));

						listBillTaxDtl.add(objBillTaxDtl);
					}
				}
			
				objBillHd.setListBillTaxDtl(listBillTaxDtl);

				listBillHd.add(objBillHd);
			}
		}
	}

	private void funGetQFileBillData(List<clsBillHd> listBillHd, String clientCode, String posCode, String fromDate, String toDate)
	{
		// QFile bill hd data
		
		StringBuilder sqlQFileQueryBuilder=new  StringBuilder();
		sqlQFileQueryBuilder.append("select a.strClientCode,a.strPOSCode,d.strPosName,a.strBillNo,date(a.dteBillDate),TIME_FORMAT(time(a.dteBillDate),'%r'),a.strSettelmentMode "
						+ ",a.dblSubTotal,a.dblDiscountAmt,a.dblTaxAmt,a.dblGrandTotal "
						+ "from tblqbillhd a,tblposmaster d  "
						+ "where a.strPOSCode=d.strPosCode "
						+ "and a.strClientCode='" + clientCode + "' "
					    + "and date(a.dtebilldate) between '"+fromDate+"' and '"+toDate+"'  ");
			if(!posCode.equalsIgnoreCase("All"))
			{
				sqlQFileQueryBuilder.append("and a.strPOSCode='"+posCode+"' ");
			}
		
		
		SQLQuery sqlBillHdQuery = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlQFileQueryBuilder.toString());
		
		
		List billHdList = sqlBillHdQuery.list();

		if (billHdList != null && billHdList.size() > 0)
		{
			for (int bill = 0; bill < billHdList.size(); bill++)
			{
				Object[] arrBillHd = (Object[]) billHdList.get(bill);

				clsBillHd objBillHd = new clsBillHd();

				/*objBillHd.setStrClientCode(arrBillHd[0].toString());
				objBillHd.setStrPOSCode(arrBillHd[1].toString());*/
				objBillHd.setStrPOSName(arrBillHd[2].toString());
				objBillHd.setStrBillNo(arrBillHd[3].toString());// billNo
				objBillHd.setStrBillDate(arrBillHd[4].toString());
				objBillHd.setStrBillTime(arrBillHd[5].toString());
				objBillHd.setStrSettlementMode(arrBillHd[6].toString());
				objBillHd.setDblSubTotal(Double.parseDouble(arrBillHd[7].toString()));
				objBillHd.setDblDiscountAmt(Double.parseDouble(arrBillHd[8].toString()));
				//objBillHd.setDblTaxAmt(Double.parseDouble(arrBillHd[9].toString()));
				objBillHd.setDblGrandTotal(Double.parseDouble(arrBillHd[10].toString()));
				objBillHd.setStrTransactionStatus("Sale");

				// tax dtl data
				SQLQuery sqlBillTaxDtlQuery = webPOSSessionFactory.getCurrentSession().createSQLQuery(
						"select b.strTaxCode,b.strTaxDesc,a.dblTaxAmount "
								+ "from tblqbilltaxdtl a,tbltaxhd b "
								+ "where a.strTaxCode=b.strTaxCode "
								+ "and a.strBillNo='" + objBillHd.getStrBillNo() + "' "
										+ "and date(a.dteBillDate)='"+objBillHd.getStrBillDate()+"' ");
				List billTaxDtlList = sqlBillTaxDtlQuery.list();

				List<clsBillTaxDtl> listBillTaxDtl = new ArrayList<clsBillTaxDtl>();

				if (billTaxDtlList != null && billTaxDtlList.size() > 0)
				{
					for (int t = 0; t < billTaxDtlList.size(); t++)
					{
						Object[] arrBillTaxDtl = (Object[]) billTaxDtlList.get(t);

						clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();

						//objBillTaxDtl.setStrTaxCode(arrBillTaxDtl[0].toString());
						objBillTaxDtl.setStrTaxDesc(arrBillTaxDtl[1].toString());
						objBillTaxDtl.setDblTaxAmt(Double.parseDouble(arrBillTaxDtl[2].toString()));

						listBillTaxDtl.add(objBillTaxDtl);
					}
				}
				objBillHd.setListBillTaxDtl(listBillTaxDtl);

				listBillHd.add(objBillHd);
			}
		}
	}
}
