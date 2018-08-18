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

import com.apos.model.clsPOSMasterModel;

@Repository("clsPOSMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	public String funGeneratePOSCode()
    {
	String posCode = "";
	try
	{
	    
	    String sql = "select count(*) from tblposmaster";
	    
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	    List list = query.list();
	    
	    if (!list.get(0).toString().equals("0"))
	    {
		
		
		long code = Long.parseLong(list.get(0).toString());
		code = code + 1;
		posCode = "P" + String.format("%02d", code);
		
	    }
	    else
	    {
	    	posCode = "P01";
	    }
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	return posCode;
    }


public void funAddUpdatePOSMaster(clsPOSMasterModel objMaster){
	webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
}

		
		public clsPOSMasterModel funGetPOSNameData(String strPOSName){
			
			clsPOSMasterModel objModel =null;
			try
			{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsPOSMasterModel.class);
			cr.add(Restrictions.eq("strPosName", strPOSName));
			List list = cr.list();
			objModel = (clsPOSMasterModel) list.get(0);	
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return objModel;
		}

		
		
		
		

		public String funGetAllPOSForMaster(String clientCode)
		{

			JSONObject jObjPOSList = new JSONObject();
			JSONArray jArrData = new JSONArray();

			try
			{
				Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsPOSMasterModel.class);
				// cr.add(Restrictions.eq("strClientCode", clientCode));
				List list = cr.list();

				clsPOSMasterModel objPOSMasterModel = null;			
				for (int cnt = 0; cnt < list.size(); cnt++)
				{
					objPOSMasterModel = (clsPOSMasterModel) list.get(cnt);
					JSONObject objPOS = new JSONObject();
					objPOS.put("strPosCode", objPOSMasterModel.getStrPosCode());
					objPOS.put("strPosName", objPOSMasterModel.getStrPosName());

					jArrData.put(objPOS);
				}
				jObjPOSList.put("POSList", jArrData);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				return jObjPOSList.toString();
			}
		}
		
		public String funGetPOSName(String PosCode)
		{

			String posName="";

			try
			{
				Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsPOSMasterModel.class);
				 cr.add(Restrictions.eq("strPosCode", PosCode));
				List list = cr.list();

				clsPOSMasterModel objPOSMasterModel = null;			
				for (int cnt = 0; cnt < list.size(); cnt++)
				{
					objPOSMasterModel = (clsPOSMasterModel) list.get(cnt);
					posName=objPOSMasterModel.getStrPosName();
					
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				return posName;
			}
		}
}
