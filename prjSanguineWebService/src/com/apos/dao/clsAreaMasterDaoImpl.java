package com.apos.dao;


import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAdvanceOrderMasterModel;
import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsPOSMasterModel;


@Repository("clsAreaMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsAreaMasterDaoImpl implements intfAreaMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
   
    
    @Override
   	public clsAreaMasterModel funGetAreaMasterData(String sql,Map<String,String> hmParameters) throws Exception
   	{
   		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
   		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
   		{
   			query.setParameter(entrySet.getKey(), entrySet.getValue());
   		}
   		List list=query.list();
   		
   		clsAreaMasterModel model=new clsAreaMasterModel();
   		if(list.size()>0)
   		{
   			model=(clsAreaMasterModel)list.get(0);
   			
   		}
   		return model; 
   	} 
    
    
   
  
    
  
}
