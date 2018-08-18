package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsShiftMasterModel;
import com.apos.model.clsZoneMasterModel;
import com.apos.service.clsShiftMasterService;

@Repository("clsShiftMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsShiftMasterDaoImpl implements inftShiftMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	public clsShiftMasterModel funLoadShiftMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
	Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	{
		query.setParameter(entrySet.getKey(), entrySet.getValue());
	}
	List list=query.list();
	
	clsShiftMasterModel model=new clsShiftMasterModel();
	if(list.size()>0)
	{
		model=(clsShiftMasterModel)list.get(0);
					
	}
	return model;
	}
}
