package com.apos.dao;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsSubMenuHeadMasterModel;

@Repository("clsSubMenuHeadMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsSubMenuHeadMasterDao implements intfSubMenuHeadMasterDao
{

	@Autowired
	private SessionFactory	webPOSSessionFactory;

	
	public String funGetAllSubMenuHeadForMaster(String clientCode)throws Exception
	{
		JSONObject jObjSubMenuList = new JSONObject();
		JSONArray jArrData = new JSONArray();

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsSubMenuHeadMasterModel.class);
			cr.add(Restrictions.eq("strClientCode", clientCode));
			List list = cr.list();

			clsSubMenuHeadMasterModel objSubMenuHeadModel = null;
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objSubMenuHeadModel = (clsSubMenuHeadMasterModel) list.get(cnt);
				JSONObject objSubMenuHead = new JSONObject();
				objSubMenuHead.put("strSubMenuHeadCode", objSubMenuHeadModel.getStrSubMenuHeadCode());
				objSubMenuHead.put("strSubMenuheadName", objSubMenuHeadModel.getStrSubMenuHeadName());

				jArrData.put(objSubMenuHead);
			}
			jObjSubMenuList.put("SubMenuList", jArrData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObjSubMenuList.toString();
		}

	}
}
