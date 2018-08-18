package com.apos.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCustomerAreaMasterAmountModel;

@Repository("clsCustomerAreaMasterAmountDao")
@Transactional(value = "webPOSTransactionManager")
public class clsCustomerAreaMasterAmountDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

		public void funSaveCustomerAreaMasterAmount(clsCustomerAreaMasterAmountModel objModel) throws Exception
		{
			webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
			
		}


}
