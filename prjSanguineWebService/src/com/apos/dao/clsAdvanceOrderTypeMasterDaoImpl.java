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
import com.apos.model.clsOrderMasterModel;

@Repository("clsAdvanceOrderTypeMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsAdvanceOrderTypeMasterDaoImpl implements intfAdvanceOrderTypeMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    @Override
	public clsAdvanceOrderMasterModel funGetAdvOrderMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsAdvanceOrderMasterModel model=new clsAdvanceOrderMasterModel();
		if(list.size()>0)
		{
			model=(clsAdvanceOrderMasterModel)list.get(0);
			
		}
		return model; 
	} 
    
}
