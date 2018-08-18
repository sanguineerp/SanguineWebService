
package com.apos.dao;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsTaxMasterModel;


@Repository("clsTaxMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsTaxMasterDaoImpl implements intfTaxMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	@Override
	public clsTaxMasterModel funGetTaxMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsTaxMasterModel model=new clsTaxMasterModel();
		if(list.size()>0)
		{
			model=(clsTaxMasterModel)list.get(0);
			model.getListsettlementDtl().size();
			model.getListTaxPosDtl().size();
			model.getListTaxGroupDtl().size();
		}
		return model; 
	} 

	
}
