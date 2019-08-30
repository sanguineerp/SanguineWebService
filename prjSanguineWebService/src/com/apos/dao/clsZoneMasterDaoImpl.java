package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCustomerTypeMasterModel;
import com.apos.model.clsZoneMasterModel;

@Repository("clsZoneMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsZoneMasterDaoImpl implements inftZoneMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	public clsZoneMasterModel funLoaddZoneMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
	Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	{
		query.setParameter(entrySet.getKey(), entrySet.getValue());
	}
	List list=query.list();
	
	clsZoneMasterModel model=new clsZoneMasterModel();
	if(list.size()>0)
	{
		model=(clsZoneMasterModel)list.get(0);
					
	}
	return model;
	}
	
	
	public String funGetAllZoneForMaster(String clientCode)
	{

		JSONObject jObjZoneList = new JSONObject();
		JSONArray jArrData = new JSONArray();

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsZoneMasterModel.class);
			// cr.add(Restrictions.eq("strClientCode", clientCode));
			List list = cr.list();

			clsZoneMasterModel objZoneMasterModel = null;			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objZoneMasterModel = (clsZoneMasterModel) list.get(cnt);
				JSONObject objZone = new JSONObject();
				objZone.put("strZoneCode", objZoneMasterModel.getStrZoneCode());
				objZone.put("strZoneName", objZoneMasterModel.getStrZoneName());

				jArrData.put(objZone);
			}
			jObjZoneList.put("ZoneList", jArrData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObjZoneList.toString();
		}
	}
}
