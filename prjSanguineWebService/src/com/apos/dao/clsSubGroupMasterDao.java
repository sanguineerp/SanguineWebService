package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.apos.model.clsSubGroupMasterHdModel;

@Repository("clsSubGroupMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsSubGroupMasterDao implements intfSubGroupMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	 public clsSubGroupMasterHdModel funGetSubGroupMasterData(String sql,Map<String,String> hmParameters) throws Exception
		{
			Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
			for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
			{
				query.setParameter(entrySet.getKey(), entrySet.getValue());
			}
			List list=query.list();
			
			clsSubGroupMasterHdModel model=new clsSubGroupMasterHdModel();
			if(list.size()>0)
			{
				model=(clsSubGroupMasterHdModel)list.get(0);
				
				
			}
			return model; 
		} 
		
		
	
	
	
}
