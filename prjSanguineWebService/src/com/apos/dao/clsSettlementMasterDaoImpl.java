
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

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsOrderMasterModel;
import com.apos.model.clsSettlementMasterModel;


@Repository("clsSettlementMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsSettlementMasterDaoImpl implements intfSettlementMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
   
    @Override
   	public clsSettlementMasterModel funGetSettlementMasterData(String sql,Map<String,String> hmParameters) throws Exception
   	{
   		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
   		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
   		{
   			query.setParameter(entrySet.getKey(), entrySet.getValue());
   		}
   		List list=query.list();
   		
   		clsSettlementMasterModel model=new clsSettlementMasterModel();
   		if(list.size()>0)
   		{
   			model=(clsSettlementMasterModel)list.get(0);
   			
   		}
   		return model; 
   	} 
   
    
  
}
    
   

