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
import com.apos.model.clsFactoryMasterHdModel;

@Repository("clsFactoryMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsFactoryMasterDao{

	 @Autowired
	    private SessionFactory webPOSSessionFactory;
	    
	    public String funGenerateFactoryCode()
	    {
		String areaCode = "";
		try
		{
		    
		    String sql = "select count(*) from tblfactorymaster";
		    
		    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		    List list = query.list();
		    
		    if (!list.get(0).toString().equals("0"))
		    {
			
			
			long code = Long.parseLong(list.get(0).toString());
			code = code + 1;
			areaCode = "F" + String.format("%06d", code);
			
		    }
		    else
		    {
		    	areaCode = "F000001";
		    }
		    
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return areaCode;
	    }

	    public void funSaveUpdateFactoryMaster(clsFactoryMasterHdModel objModel)
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
		public JSONObject funGetFactoryList(String clientCode)
		{
			List list =null;
			JSONArray jArrFactoryData = new JSONArray();
			JSONObject jObjData=new JSONObject();
			try{
			
	    Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsFactoryMasterHdModel where strClientCode= :clientCode");
		query.setParameter("clientCode", clientCode);
		 list = query.list();
		 clsFactoryMasterHdModel objFactoryaModel = null;
			if (list.size() > 0)
			{
				for(int i=0;i<list.size(); i++)
				{
					JSONArray jArrData = new JSONArray();
					objFactoryaModel = (clsFactoryMasterHdModel) list.get(i);
			    
			    jArrData.put(objFactoryaModel.getStrFactoryCode());
			    jArrData.put(objFactoryaModel.getStrFactoryName());
			  
			    jArrFactoryData.put(jArrData);
				}
				jObjData.put("areaList", jArrFactoryData);
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
	    
	    public String funGetAllFactoryForMaster(String clientCode)
		{
			List list = null;
			
			JSONObject jObjData = new JSONObject();
			JSONArray jArrAreaData = new JSONArray();
			try
			{

				Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsFactoryMasterHdModel where strClientCode= :clientCode");
				query.setParameter("clientCode", clientCode);
				list = query.list();
				clsFactoryMasterHdModel objFactoryModel = null;
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						JSONObject jobj = new JSONObject();
						
						objFactoryModel = (clsFactoryMasterHdModel) list.get(i);
						jobj.put("strFactoryCode",objFactoryModel.getStrFactoryCode());
						jobj.put("strFactoryName",objFactoryModel.getStrFactoryName());					
						
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

	    
	    public String funGetFactoryName(String factoryCode)
		{

			String factoryName="";

			try
			{
				Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsFactoryMasterHdModel.class);
				 cr.add(Restrictions.eq("strAreaCode", factoryCode));
				List list = cr.list();

				clsFactoryMasterHdModel objFactoryMasterHdModel = null;			
				for (int cnt = 0; cnt < list.size(); cnt++)
				{
					objFactoryMasterHdModel = (clsFactoryMasterHdModel) list.get(cnt);
					factoryName=objFactoryMasterHdModel.getStrFactoryName();
					
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				return factoryName;
			}
		}
	}
