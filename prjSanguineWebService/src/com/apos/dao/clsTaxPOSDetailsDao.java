

package com.apos.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsTaxPosDetailsModel;




@Repository("clsTaxPOSDetailsDao")
@Transactional(value = "webPOSTransactionManager")
public class clsTaxPOSDetailsDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public void funSaveTaxPOSDetails(clsTaxPosDetailsModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}
		
	public void funDeleteTaxPOSDetails(String taxCode){
		
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsTaxPosDetailsModel WHERE strTaxCode= :taxCode ");
		query.setParameter("taxCode", taxCode);
		
		query.executeUpdate();
		
		
	}
	
	
}
