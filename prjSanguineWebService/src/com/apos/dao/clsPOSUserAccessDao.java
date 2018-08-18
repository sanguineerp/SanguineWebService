package com.apos.dao;


import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("clsPOSUserAccessDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSUserAccessDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
		
	public void funDeleteUserAccessDetails(String userCode)
	{
		
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsUserDetailHdModel WHERE strUserCode= :userCode ");
		query.setParameter("userCode", userCode);
		
		query.executeUpdate();
	}
	
	public void funDeleteSuperUserAccessDetails(String userCode)
	{
		Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsSuperUserDetailHdModel WHERE strUserCode= :userCode ");
		query.setParameter("userCode", userCode);
		query.executeUpdate();
	}
	
	@SuppressWarnings("finally")
	public JSONObject funGetAllFormDetails(String clientCode)
	{
		List list =null;
		JSONObject jObjFormsData=new JSONObject();
		try{
		
			String sql="select * from tblforms order by strModuleName";
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
			
			 list = query.list();
			 JSONArray jArrData=new JSONArray();
	          
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					    JSONObject jObj = new JSONObject();
				    	jObj.put("strFormName",obj[0]);
				    	jObj.put("strModuleName",obj[1]);
				    	jObj.put("strModuleType",obj[2]);
				        jArrData.put(jObj);
			    	}
					jObjFormsData.put("tblFormsDtl", jArrData);
			      }
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjFormsData;
			}
	}
	
}
