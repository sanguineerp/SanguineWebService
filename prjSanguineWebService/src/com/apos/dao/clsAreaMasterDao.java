package com.apos.dao;


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

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsPOSMasterModel;


@Repository("clsAreaMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsAreaMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    public String funGenerateAreaCode()
    {
	String areaCode = "";
	try
	{
	    
	    String sql = "select count(*) from tblareamaster";
	    
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    List list = query.list();
	    
	    if (!list.get(0).toString().equals("0"))
	    {
		
		
		long code = Long.parseLong(list.get(0).toString());
		code = code + 1;
		areaCode = "A" + String.format("%03d", code);
		
	    }
	    else
	    {
	    	areaCode = "A001";
	    }
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return areaCode;
    }

    public void funSaveUpdateAreaMaster(clsAreaMasterModel objModel)
    {	
	try
	{
	    webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    @SuppressWarnings("finally")
	public JSONObject funGetAreaList(String clientCode)
	{
		List list =null;
		JSONArray jArrAreaData = new JSONArray();
		JSONObject jObjData=new JSONObject();
		try{
		
    Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsAreaMasterModel where strClientCode= :clientCode");
	query.setParameter("clientCode", clientCode);
	 list = query.list();
	 clsAreaMasterModel objAreaModel = null;
		if (list.size() > 0)
		{
			for(int i=0;i<list.size(); i++)
			{
				JSONArray jArrData = new JSONArray();
			objAreaModel = (clsAreaMasterModel) list.get(i);
		    
		    jArrData.put(objAreaModel.getStrAreaCode());
		    jArrData.put(objAreaModel.getStrAreaName());
		    jArrData.put(objAreaModel.getStrPOSCode());
		    jArrAreaData.put(jArrData);
			}
			jObjData.put("areaList", jArrAreaData);
		}
		
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
		finally
		{
			return jObjData;
		}
}
    
    public String funGetAllAreaForMaster(String clientCode)
	{
		List list = null;
		
		JSONObject jObjData = new JSONObject();
		JSONArray jArrAreaData = new JSONArray();
		try
		{

			Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsAreaMasterModel where strClientCode= :clientCode");
			query.setParameter("clientCode", clientCode);
			list = query.list();
			clsAreaMasterModel objAreaModel = null;
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject jobj = new JSONObject();
					
					objAreaModel = (clsAreaMasterModel) list.get(i);
					jobj.put("strAreaCode",objAreaModel.getStrAreaCode());
					jobj.put("strAreaName",objAreaModel.getStrAreaName());					
					
					jArrAreaData.put(jobj);
				}
				jObjData.put("AreaList", jArrAreaData);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjData.toString();
		}
	}

    
    public String funGetAreaName(String areaCode)
	{

		String areaName="";

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsAreaMasterModel.class);
			 cr.add(Restrictions.eq("strAreaCode", areaCode));
			List list = cr.list();

			clsAreaMasterModel objAreaMasterModel = null;			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objAreaMasterModel = (clsAreaMasterModel) list.get(cnt);
				areaName=objAreaMasterModel.getStrAreaName();
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return areaName;
		}
	}
}
