

package com.apos.dao;


import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsOrderMasterModel;


@Repository("clsOrderMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsOrderMasterDaoImpl implements intfOrderMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    @Override
	public clsOrderMasterModel funGetOrderMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsOrderMasterModel model=new clsOrderMasterModel();
		if(list.size()>0)
		{
			model=(clsOrderMasterModel)list.get(0);
			
		}
		return model; 
	} 
}
