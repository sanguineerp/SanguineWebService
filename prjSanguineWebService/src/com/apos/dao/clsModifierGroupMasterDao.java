package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsModifierGroupMasterHdModel;
import com.apos.model.clsModifierMasterHdModel;

@Repository("clsModifierGroupMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsModifierGroupMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	public clsModifierGroupMasterHdModel funGetItemModifierMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsModifierGroupMasterHdModel model=new clsModifierGroupMasterHdModel();
		if(list.size()>0)
		{
			model=(clsModifierGroupMasterHdModel)list.get(0);
			
			
		}
		return model; 
	} 
}
