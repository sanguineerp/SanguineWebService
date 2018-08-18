package com.apos.dao;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsMenuHeadMasterModel;
import com.apos.model.clsSubMenuHeadMasterModel;

@Repository("clsMenuHeadMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsMenuHeadMasterDao
{



	@Autowired
	private SessionFactory	webPOSSessionFactory;


	public clsMenuHeadMasterModel funGetMenuHeadMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsMenuHeadMasterModel model=new clsMenuHeadMasterModel();
		if(list.size()>0)
		{
			model=(clsMenuHeadMasterModel)list.get(0);
			
		}
		return model; 
	} 
	
	public clsSubMenuHeadMasterModel funGetSubMenuHeadMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsSubMenuHeadMasterModel model=new clsSubMenuHeadMasterModel();
		if(list.size()>0)
		{
			model=(clsSubMenuHeadMasterModel)list.get(0);
			
		}
		return model; 
	} 
	


}
