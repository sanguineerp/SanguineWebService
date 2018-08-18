package com.apos.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsReorderTimeModel;

@Repository("clsReorderTimeDao")
@Transactional(value = "webPOSTransactionManager")
public class clsReorderTimeDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public void funSaveReorderTime(clsReorderTimeModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}
		
	public void funDeleteReorderTime(String posCode){
		
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsReorderTimeModel WHERE strPOSCode= :posCode ");
		query.setParameter("posCode", posCode);
		
		query.executeUpdate();
		
		
	}

}
