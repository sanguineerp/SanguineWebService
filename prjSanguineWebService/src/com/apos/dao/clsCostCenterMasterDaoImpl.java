
package com.apos.dao;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCostCenterMasterModel;


@Repository("clsCostCenterMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsCostCenterMasterDaoImpl implements intfCostCenterMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	@Override
	public clsCostCenterMasterModel funGetSelectedCostCenterMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsCostCenterMasterModel model=new clsCostCenterMasterModel();
		if(list.size()>0)
		{
			model=(clsCostCenterMasterModel)list.get(0);
//			model.getListsettlementDtl().size();
//			model.getListTaxPosDtl().size();
		}
		return model; 
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

					jArrAreaData.add(jobj);
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

	

	
}
