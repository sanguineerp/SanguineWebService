
package com.apos.dao;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsFactoryMasterHdModel;
import com.apos.model.clsTaxMasterModel;


@Repository("clsFactoryMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsFactoryMasterDaoImpl implements intfFactoryMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	@Override
	public clsFactoryMasterHdModel funGetSelectedFactoryMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsFactoryMasterHdModel model=new clsFactoryMasterHdModel();
		if(list.size()>0)
		{
			model=(clsFactoryMasterHdModel)list.get(0);
//			model.getListsettlementDtl().size();
//			model.getListTaxPosDtl().size();
		}
		return model; 
	}


	

	
}
