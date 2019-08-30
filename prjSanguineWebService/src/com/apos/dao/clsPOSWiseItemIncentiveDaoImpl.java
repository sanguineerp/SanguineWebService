package com.apos.dao;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsPOSWiseItemIncentiveModel;
import com.apos.model.clsPOSWiseItemIncentiveModel_ID;
import com.apos.model.clsZoneMasterModel;
import com.sanguine.service.intfBaseService;


@Repository("clsPOSWiseItemIncentiveDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSWiseItemIncentiveDaoImpl implements inftPOSWiseItemIncentiveDao {
	
	@Autowired
	intfBaseService objDao;

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
		@Override
	public boolean funDeleteTruncate(String sql,String StrCode,String ind) throws Exception
	{	
		boolean flg=false;
		if(ind.equalsIgnoreCase("1"))
		{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		query.setParameter("code", StrCode);
		flg=true;
		}
		else
		{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		flg=true;
		}
		return flg;
	}
    
		
		public void funUpdateTable(String sql,Map<String,String> hmParameters) throws Exception
		{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		
		}
}
