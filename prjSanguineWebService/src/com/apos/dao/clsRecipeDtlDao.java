
package com.apos.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsRecipeDtlModel;
@Repository("clsRecipeDtlDao")

@Transactional(value = "webPOSTransactionManager")
public class clsRecipeDtlDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	

	public void funSaveAndUpdateRecipeDtl(clsRecipeDtlModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}
		
	public void funDeleteRecipeDtl(String dpCode){
		
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsRecipeDtlModel WHERE strRecipeCode= :dpCode ");
		query.setParameter("dpCode", dpCode);
		
		query.executeUpdate();
		
		
	}


}
