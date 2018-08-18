package com.apos.dao;
import java.util.List;



import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsWaiterMasterModel;

@Repository("clsWaiterMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsWaiterMasterDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	 public String funGenerateWaiterCode()
	    {
		String waiterCode = "";
		try
		{
		    
		    String sql = "select count(*) from tblwaitermaster";
		    
		    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		    List list = query.list();
		    
		    if (!list.get(0).toString().equals("0"))
		    {
			
			
			long code = Long.parseLong(list.get(0).toString());
			code = code + 1;
			waiterCode = "W" + String.format("%02d", code);
			
		    }
		    else
		    {
		    	waiterCode = "W01";
		    }
		    
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return waiterCode;
	    }


	public void funAddUpdateWaiterMaster(clsWaiterMasterModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}

	

		public JSONObject funGetWaiterList(String clientCode)
		{
			List list =null;
			JSONArray jArrWaiterData = new JSONArray();
			JSONObject jObjData=new JSONObject();
			try{
			
	    Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsWaiterMasterModel where strClientCode= :clientCode");
		query.setParameter("clientCode", clientCode);
		 list = query.list();
		 clsWaiterMasterModel objWaiterModel = null;
			if (list.size() > 0)
			{
				for(int i=0;i<list.size(); i++)
				{
					JSONObject jObj = new JSONObject();
					objWaiterModel = (clsWaiterMasterModel) list.get(i);
			    
					jObj.put("strWaiterNo",objWaiterModel.getStrWaiterNo());
					jObj.put("strWShortName",objWaiterModel.getStrWShortName());
					jObj.put("strPOSCode",objWaiterModel.getStrPOSCode());
					jArrWaiterData.put(jObj);
				}
				jObjData.put("waiterList", jArrWaiterData);
			}
			
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			return jObjData;
			
	}
}
