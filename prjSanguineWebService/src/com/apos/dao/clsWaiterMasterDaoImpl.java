package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsWaiterMasterModel;

@Repository("clsWaiterMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsWaiterMasterDaoImpl implements intfWaiterMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	@Override
   	public clsWaiterMasterModel funGetWaiterMasterData(String sql,Map<String,String> hmParameters) throws Exception
   	{
   		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
   		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
   		{
   			query.setParameter(entrySet.getKey(), entrySet.getValue());
   		}
   		List list=query.list();
   		
   		clsWaiterMasterModel model=new clsWaiterMasterModel();
   		if(list.size()>0)
   		{
   			model=(clsWaiterMasterModel)list.get(0);
   			
   		}
   		return model; 
   	} 
    
	
}
