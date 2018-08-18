package com.apos.dao;

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

@Repository("clsPOSCostCenterMasterDao")
@Transactional(value = "webPOSTransactionManager")


public class clsCostCenterMasterDao
{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	public String funGeneratePOSCostCenterCode()
    {
	String costCenterCode = "";
	try
	{
	    
	    String sql = "select count(*) from tblcostcentermaster";
	    
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    List list = query.list();
	    
	    if (!list.get(0).toString().equals("0"))
	    {
		
		
		long code = Long.parseLong(list.get(0).toString());
		code = code + 1;
		costCenterCode = "C" + String.format("%02d", code);
		
	    }
	    else
	    {
	    	costCenterCode = "C01";
	    }
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return costCenterCode;
    }
	
	public String funSaveCostCenterMaster(clsCostCenterMasterModel objModel) throws Exception
    {
    	webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
    	return objModel.getStrCostCenterCode();
    }

	public String funGetAllCostCentersForMaster(String clientCode)
	{
		List list = null;

		JSONObject jObjData = new JSONObject();
		JSONArray jArrAreaData = new JSONArray();
		try
		{

			Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsCostCenterMasterModel where strClientCode= :clientCode");
			query.setParameter("clientCode", clientCode);
			list = query.list();
			clsCostCenterMasterModel objCenterMasterModel = null;
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject jobj = new JSONObject();

					objCenterMasterModel = (clsCostCenterMasterModel) list.get(i);
					
					jobj.put("strCostCenterCode", objCenterMasterModel.getStrCostCenterCode());
					jobj.put("strCostCenterName", objCenterMasterModel.getStrCostCenterName());
					jobj.put("strPrinterPort", objCenterMasterModel.getStrPrinterPort());
					jobj.put("strSecondaryPrinterPort", objCenterMasterModel.getStrSecondaryPrinterPort());
					jobj.put("strPrintOnBothPrinters", objCenterMasterModel.getStrPrintOnBothPrinters());
					jobj.put("strUserCreated", objCenterMasterModel.getStrUserCreated());
					jobj.put("strUserEdited", objCenterMasterModel.getStrUserEdited());
					jobj.put("dteDateCreated", objCenterMasterModel.getDteDateCreated());
					jobj.put("dteDateEdited", objCenterMasterModel.getDteDateEdited());
					jobj.put("strClientCode", objCenterMasterModel.getStrClientCode());
					jobj.put("strDataPostFlag", objCenterMasterModel.getStrDataPostFlag());
					jobj.put("strLabelOnKOT", objCenterMasterModel.getStrLabelOnKOT());

					jArrAreaData.put(jobj);
				}
				jObjData.put("CostCenterList", jArrAreaData);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjData.toString();
		}
	}
	public List<clsCostCenterMasterModel> funGetAllCenter(String strClientCode)
	{
		List<clsCostCenterMasterModel> list=null;
		try
		{
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("from clsCostCenterMasterModel where strClientCode=:strClientCode");
		query.setParameter("strClientCode", strClientCode);
		list=query.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
}
