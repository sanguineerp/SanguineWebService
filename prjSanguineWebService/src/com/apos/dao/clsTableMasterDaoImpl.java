package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsTableMasterModel;

@Repository("clsTableMasterDaoImpl")

@Transactional(value = "webPOSTransactionManager")
public class clsTableMasterDaoImpl implements intfTableMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Override
	public JSONObject funGetTableDtl(String clientCode)
	{
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		try{
		
			String hql="select a.strTableNo,a.strTableName,ifnull(b.strAreaName,''),ifnull(c.strPosName,'All') "
                    + " from tbltablemaster a left outer join tblareamaster b on a.strAreaCode=b.strAreaCode "
                    + " left outer join tblposmaster c on a.strPOSCode=c.strPosCode "
                    + "ORDER by a.intSequence";
			
		
			
			Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(hql);
			
			list = query.list();
			 JSONArray jArrData=new JSONArray();
	          
			 if (list!=null)
				{
					for(int i=0; i<list.size(); i++)
					{
						Object[] obj=(Object[])list.get(i);
					
				
						JSONObject objSettle=new JSONObject();
						objSettle.put("TableNo",obj[0]);
						objSettle.put("TableName",obj[1]);
						objSettle.put("AreaName",obj[2]);
						objSettle.put("PosName",obj[3]);
						
						jArrData.put(objSettle);
					}
		           	jObjTableData.put("TableDtl", jArrData);
			      }
			 
	           
			}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			
				return jObjTableData;
	}
	
	 @Override
	   	public clsTableMasterModel funGetTableMasterData(String sql,Map<String,String> hmParameters) throws Exception
	   	{
	   		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	   		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	   		{
	   			query.setParameter(entrySet.getKey(), entrySet.getValue());
	   		}
	   		List list=query.list();
	   		
	   		clsTableMasterModel model=new clsTableMasterModel();
	   		if(list.size()>0)
	   		{
	   			model=(clsTableMasterModel)list.get(0);
	   			
	   		}
	   		return model; 
	   	} 
	
}
