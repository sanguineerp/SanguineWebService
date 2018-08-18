package com.apos.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsMenuItemMasterModel;
import com.apos.model.clsPOSTDHModel;
import com.apos.model.clsPricingMasterHdModel;

@Repository("clsTDHMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsTDHMasterDaoImpl implements inftTDHMasterDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public clsPOSTDHModel funLoadTDHMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsPOSTDHModel model=new clsPOSTDHModel();
		if(list.size()>0)
		{
			model=(clsPOSTDHModel)list.get(0);
			model.getListTDHDtl().size();
			
		}
		return model;
		
	}

	@Override
	public clsPricingMasterHdModel funLoadPOSTDHTableData(String sql,Map<String, String> hmParameters) throws Exception {
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsPricingMasterHdModel model=new clsPricingMasterHdModel();
		if(list.size()>0)
		{
			model=(clsPricingMasterHdModel)list.get(0);
			
		}
		return model;
	}
	
	@Override
	public List funLoadItemList(String sql,Map<String, String> hmParameters) throws Exception {
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		List<clsPricingMasterHdModel> listOfModel=new ArrayList<clsPricingMasterHdModel>();
		clsPricingMasterHdModel model=new clsPricingMasterHdModel();
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
			model=(clsPricingMasterHdModel)list.get(i);
			
			listOfModel.add(model);
			}
			
		}
		return listOfModel;
	}
	
	
	
	@Override
	public List funLoadALLItemNameList(String sql,Map<String, String> hmParameters) throws Exception {
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		List<clsMenuItemMasterModel> listOfModel=new ArrayList<clsMenuItemMasterModel>();
		clsMenuItemMasterModel model=new clsMenuItemMasterModel();
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
			model=(clsMenuItemMasterModel)list.get(i);
			
			listOfModel.add(model);
			}
			
		}
		return listOfModel;
	}
	

	
}
