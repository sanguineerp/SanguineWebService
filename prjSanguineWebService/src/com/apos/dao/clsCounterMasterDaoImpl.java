
package com.apos.dao;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsCounterMasterHdModel;
import com.apos.model.clsTaxMasterModel;


@Repository("clsCounterMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsCounterMasterDaoImpl implements intfCounterMasterDao
{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	@Override
	public clsCounterMasterHdModel funGetSelectedCounterMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsCounterMasterHdModel model=new clsCounterMasterHdModel();
		if(list.size()>0)
		{
			model=(clsCounterMasterHdModel)list.get(0);
			model.getListMenuDtl().size();
//			model.getListTaxPosDtl().size();
		}
		return model; 
	}


	

	
}
