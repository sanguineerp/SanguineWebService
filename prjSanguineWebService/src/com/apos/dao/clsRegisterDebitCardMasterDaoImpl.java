package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCustomerTypeMasterModel;
import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsPOSRegisterDebitCardHdModel;
import com.apos.model.clsZoneMasterModel;

@Repository("clsRegisterDebitCardMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsRegisterDebitCardMasterDaoImpl implements intfRegisterDebitCardMasterDao{

	@Autowired
	private SessionFactory WebPOSSessionFactory;

	public clsDebitCardMasterHdModel funRegisterCard(String sql,Map<String,String> hmParameters) throws Exception
	{
	Query query=WebPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	{
		query.setParameter(entrySet.getKey(), entrySet.getValue());
	}
	List list=query.list();
	
	clsDebitCardMasterHdModel model=new clsDebitCardMasterHdModel();
	if(list.size()>0)
	{
		model=(clsDebitCardMasterHdModel)list.get(0);
					
	}
	return model;
	}
	
	@Override
	public clsPOSRegisterDebitCardHdModel funDelistCardForUpdate(String sql,Map<String,String> hmParameters) throws Exception
	{
	Query query=WebPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	{
		query.setParameter(entrySet.getKey(), entrySet.getValue());
	}
	List list=query.list();
	
	clsPOSRegisterDebitCardHdModel model=new clsPOSRegisterDebitCardHdModel();
	if(list.size()>0)
	{
		model=(clsPOSRegisterDebitCardHdModel)list.get(0);
					
	}
	return model;
	}
	
	
	
	public long funDelistCardCount(String sql,Map<String,String> hmParameters) throws Exception
	{
	Query query=WebPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	{
		query.setParameter(entrySet.getKey(), entrySet.getValue());
	}
	List list=query.list();
	
//	clsPOSRegisterDebitCardHdModel model=new clsPOSRegisterDebitCardHdModel();
	long count=0;
	if(list.size()>0)
	{
		count=(long)list.get(0);
					
	}
	return count;
	}
	
	
	public List funCheckCardString(String sql,Map<String,String> hmParameters) throws Exception
	{
	Query query=WebPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	{ 
		
		query.setParameter(entrySet.getKey(), entrySet.getValue());
	}
	List list=query.list();
	
	
	return list;
	}
	

}
