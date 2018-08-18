

package com.apos.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.apos.model.clsBuyPromotionDtlHdModel;



@Repository("clsBuyPromotionDtlDao")
@Transactional(value = "webPOSTransactionManager")
public class clsBuyPromotionDtlDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public void funSaveBuyItemPromotionDetails(clsBuyPromotionDtlHdModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}
		
	public void funDeleteBuyItemPromotionDetails(String strPromoCode){
		
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsBuyPromotionDtlHdModel WHERE strPromoCode= :strPromoCode ");
		query.setParameter("strPromoCode", strPromoCode);
		
		query.executeUpdate();
		
		
	}
	
	
}
