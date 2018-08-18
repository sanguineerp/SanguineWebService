


package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsRecipeMasterModel;
import com.apos.model.clsTableMasterModel;

@Repository("clsRecipeMasterDaoImpl")

@Transactional(value = "webPOSTransactionManager")
public class clsRecipeMasterDaoImpl implements intfRecipeMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;


	 @Override
	   	public clsRecipeMasterModel funGetRecipeMasterData(String sql,Map<String,String> hmParameters) throws Exception
	   	{
	   		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	   		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	   		{
	   			query.setParameter(entrySet.getKey(), entrySet.getValue());
	   		}
	   		List list=query.list();
	   		
	   		clsRecipeMasterModel model=new clsRecipeMasterModel();
	   		if(list.size()>0)
	   		{
	   			model=(clsRecipeMasterModel)list.get(0);
	   			model.getListRecipeDtl().size();
	   			
	   		}
	   		return model; 
	   	} 
	
}
