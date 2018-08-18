package com.sanguine.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.model.clsBaseModel;

@Repository("intfBaseDao")
@Transactional(value = "webPOSTransactionManager")
public class clsBaseDaoImpl implements intfBaseDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Override
	public String funSave(clsBaseModel objBaseModel)
	{
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objBaseModel);
		return objBaseModel.getDocCode();
	}
	
	
// Funtion to fetch data from db through hibernate	
	@Override
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key)
	{
		return (clsBaseModel) webPOSSessionFactory.getCurrentSession().load(objBaseModel.getClass(), key);		
	}
	
	@Override
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode)
	{
		Criteria cr=webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass());
		cr.add(Restrictions.eq("strClientCode", clientCode));
		
		return webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass()).list();
	}
	
	@Override
	public List funGetSerachList(String sql,String clientCode) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		query.setParameter("clientCode", clientCode);
		
		return query.list(); 
	}
	
	
	public List funGetList(StringBuilder strQuery,String queryType) throws Exception
	{
		Query query;
		if(queryType.equals("sql"))
		{
			query=webPOSSessionFactory.getCurrentSession().createSQLQuery(strQuery.toString());
			return query.list();
		}
		else{
			query=webPOSSessionFactory.getCurrentSession().createQuery(strQuery.toString());
			return query.list();
		}
		
	}
	
	
	
}
