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

import com.apos.model.clsItemModifierMasterModel;
import com.apos.model.clsMenuHeadMasterModel;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.apos.model.clsModifierMasterHdModel;

@Repository("clsItemModifierMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsItemModifierMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	public clsModifierMasterHdModel funGetItemModifierMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsModifierMasterHdModel model=new clsModifierMasterHdModel();
		if(list.size()>0)
		{
			model=(clsModifierMasterHdModel)list.get(0);
			model.getSetItemModifierDtl().size();
			
		}
		return model; 
	} 

}
