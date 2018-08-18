package com.apos.dao;

import java.lang.reflect.Array;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsPosCounterDetailsModel;
import com.apos.model.clsPosSettlementDetailsModel;



@Repository("clsPosCounterDetailsDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPosCounterDetailsDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public void funSavePosCounterDetails(clsPosCounterDetailsModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}
	
	
}
