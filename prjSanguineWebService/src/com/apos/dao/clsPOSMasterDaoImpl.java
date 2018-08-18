package com.apos.dao;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

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

@Repository("clsPOSMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSMasterDaoImpl implements intfPOSMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	@Override
	public clsPOSMasterModel funGetPOSMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsPOSMasterModel model=new clsPOSMasterModel();
		if(list.size()>0)
		{
			model=(clsPOSMasterModel)list.get(0);
			model.getListReorderTimeDtl().size();
			model.getListsettlementDtl().size();
			
		}
		return model; 
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
	
	
	public String funGetPOSName(String PosCode)
	{

		String posName="";
		JSONArray jArrData = new JSONArray();
		JSONObject jObjPOSList = new JSONObject();
		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsPOSMasterModel.class);
			 cr.add(Restrictions.eq("strPosCode", PosCode));
			List list = cr.list();

			clsPOSMasterModel objPOSMasterModel = null;			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objPOSMasterModel = (clsPOSMasterModel) list.get(cnt);
				JSONObject objPOS = new JSONObject();
				objPOS.put("strPosName", objPOSMasterModel.getStrPosName());
				jArrData.put(objPOS);
//				posName=objPOSMasterModel.getStrPosName();
				
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

	
	
}
