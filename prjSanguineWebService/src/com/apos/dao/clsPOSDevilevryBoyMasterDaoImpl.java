package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsDeliveryBoyMasterModel;
import com.apos.model.clsTaxMasterModel;

@Repository("clsPOSDevilevryBoyMasterDaoImpl")
@Transactional(value ="webPOSTransactionManager")
public class clsPOSDevilevryBoyMasterDaoImpl implements intfDeliveryBoyMasterDao{
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	

	@Override
	public clsDeliveryBoyMasterModel funGetDeliveryBoyMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsDeliveryBoyMasterModel model=new clsDeliveryBoyMasterModel();
		if(list.size()>0)
		{
			model=(clsDeliveryBoyMasterModel)list.get(0);
			model.getListDeliveryChargesDtl().size();
		
		}
		return model; 
	} 

}
