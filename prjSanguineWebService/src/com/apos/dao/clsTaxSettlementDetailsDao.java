
package com.apos.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.apos.model.clsTaxSettlementDetailsModel;



@Repository("clsTaxSettlementDetailsDao")
@Transactional(value = "webPOSTransactionManager")
public class clsTaxSettlementDetailsDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public void funSaveTaxSettlementDetails(clsTaxSettlementDetailsModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}
		
	public void funDeleteTaxSettlementDetails(String taxCode){
		
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsTaxSettlementDetailsModel WHERE strTaxCode= :taxCode ");
		query.setParameter("taxCode", taxCode);
		
		query.executeUpdate();
		
		
	}
	
	
}
