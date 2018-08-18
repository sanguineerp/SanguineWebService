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
import com.apos.model.clsCounterMasterHdModel;

@Repository("clsCounterMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsCounterMasterDao{

	 
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    public String funGenerateCounterCode()
    {
	String counterCode = "";
	try
	{
	    
	    String sql = "select count(*) from tblcounterhd";
	    
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    List list = query.list();
	    
	    if (!list.get(0).toString().equals("0"))
	    {
		
		
		long code = Long.parseLong(list.get(0).toString());
		code = code + 1;
		counterCode = "CT" + String.format("%02d", code);
		
	    }
	    else
	    {
	    	counterCode = "CT01";
	    }
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return counterCode;
    }

    public void funSaveUpdateCounterMaster(clsCounterMasterHdModel objModel)
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
	public JSONObject funGetCounterList(String clientCode)
	{
		List list =null;
		JSONArray jArrCounterData = new JSONArray();
		JSONObject jObjData=new JSONObject();
		try{
		
    Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsCounterMasterHdModel where strClientCode= :clientCode");
	query.setParameter("clientCode", clientCode);
	 list = query.list();
	 clsCounterMasterHdModel objCounterModel = null;
		if (list.size() > 0)
		{
			for(int i=0;i<list.size(); i++)
			{
				JSONArray jArrData = new JSONArray();
			objCounterModel = (clsCounterMasterHdModel) list.get(i);
		    
		    jArrData.put(objCounterModel.getStrCounterCode());
		    jArrData.put(objCounterModel.getStrCounterName());
		    jArrData.put(objCounterModel.getStrOperational());
		    jArrData.put(objCounterModel.getStrPOSCode());
		    jArrData.put(objCounterModel.getStrUserCode());
		    jArrCounterData.put(jArrData);
			}
			jObjData.put("CounterList", jArrCounterData);
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
    
    public String funGetAllCounterForMaster(String clientCode)
	{
		List list = null;
		
		JSONObject jObjData = new JSONObject();
		JSONArray jArrCounterData = new JSONArray();
		try
		{

			Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsCounterMasterHdModel where strClientCode= :clientCode");
			query.setParameter("clientCode", clientCode);
			list = query.list();
			clsCounterMasterHdModel objCounterModel = null;
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject jobj = new JSONObject();
					
					objCounterModel = (clsCounterMasterHdModel) list.get(i);
					jobj.put("strCounterCode",objCounterModel.getStrCounterCode());
					jobj.put("strCounterName",objCounterModel.getStrCounterName());					
					jobj.put("strOperational",objCounterModel.getStrOperational());	
					jobj.put("strPOSCode",objCounterModel.getStrPOSCode());	
					jobj.put("strUserCode",objCounterModel.getStrUserCode());	
					jArrCounterData.put(jobj);
				}
				jObjData.put("CounterList", jArrCounterData);
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

    
    public String funGetCounterName(String counterCode)
	{

		String counterName="";

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsCounterMasterHdModel.class);
			 cr.add(Restrictions.eq("strCounterCode", counterCode));
			List list = cr.list();

			clsCounterMasterHdModel objCounterMasterHdModel = null;			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objCounterMasterHdModel = (clsCounterMasterHdModel) list.get(cnt);
				counterName=objCounterMasterHdModel.getStrCounterName();
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return counterName;
		}
	}
    
}
