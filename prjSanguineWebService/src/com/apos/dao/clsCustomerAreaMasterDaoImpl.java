package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCustomerAreaMasterModel;

@Repository("clsCustomerAreaMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsCustomerAreaMasterDaoImpl implements inftCustomerAreaMasterDao{

	@Autowired
	private SessionFactory WebPOSSessionFactory;

	
	public clsCustomerAreaMasterModel funLoadCustomerAreaMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=WebPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsCustomerAreaMasterModel model=new clsCustomerAreaMasterModel();
		if(list.size()>0)
		{
			model=(clsCustomerAreaMasterModel)list.get(0);
			model.getListcustomerDtl().size();
			
		}
		return model;
		
	}
	
	public String funGetAllCustomerAreaForMaster(String clientCode)
	{

		JSONObject jObjZoneList = new JSONObject();
		JSONArray jArrData = new JSONArray();

		try
		{
			Criteria cr = WebPOSSessionFactory.getCurrentSession().createCriteria(clsCustomerAreaMasterModel.class);
			// cr.add(Restrictions.eq("strClientCode", clientCode));
			
			List list = cr.list();

			clsCustomerAreaMasterModel objCustomerAreaMasterModel = null;			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objCustomerAreaMasterModel = (clsCustomerAreaMasterModel) list.get(cnt);
				JSONObject objZone = new JSONObject();
				objZone.put("strBuildingCode", objCustomerAreaMasterModel.getStrBuildingCode());
				objZone.put("strBuildingName", objCustomerAreaMasterModel.getStrBuildingName());
				//objZone.put("strZoneCode", objCustomerAreaMasterModel.getStrZoneCode());

				jArrData.add(objZone);
			}
			jObjZoneList.put("CustomerAreaList", jArrData);
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
