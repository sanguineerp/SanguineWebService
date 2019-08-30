package com.apos.dao;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCustomerTypeMasterModel;



@Repository("clsCustomerTypeMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsCustomerTypeMasterDaoImpl implements inftCustomerTypeMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	
	public clsCustomerTypeMasterModel funLoadCustomerTypeMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
	
	Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
	for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
	{
		query.setParameter(entrySet.getKey(), entrySet.getValue());
	}
	List list=query.list();
	
	clsCustomerTypeMasterModel model=new clsCustomerTypeMasterModel();
	if(list.size()>0)
	{
		model=(clsCustomerTypeMasterModel)list.get(0);
					
	}
	return model;	
	}



	@Override
	public String funFillCustTypeCombo(String clientCode)
	{

		JSONObject jObjCustomerTypeList = new JSONObject();
		JSONArray jArrData = new JSONArray();

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsCustomerTypeMasterModel.class);
			// cr.add(Restrictions.eq("strClientCode", clientCode));
			
			List list = cr.list();

			clsCustomerTypeMasterModel objCustomerTypeMasterModel = null;			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objCustomerTypeMasterModel = (clsCustomerTypeMasterModel) list.get(cnt);
				JSONObject objCust = new JSONObject();
				objCust.put("strCustomeTypeCode", objCustomerTypeMasterModel.getStrCustTypeCode());
				objCust.put("strCustomeTypeName", objCustomerTypeMasterModel.getStrCustType());
				

				jArrData.put(objCust);
			}
			jObjCustomerTypeList.put("CustomerTypeList", jArrData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObjCustomerTypeList.toString();
		}
	}



	
	
	
	
		
		/*    @SuppressWarnings("finally")
			public String funGetAllCustomerType(String clientCode)
			{
				List list = null;
				
				JSONObject jObjData = new JSONObject();
				JSONArray jArrAreaData = new JSONArray();
				try
				{

					Query query = WebPOSSessionFactory.getCurrentSession().createQuery("from clsCustomerTypeMasterModel where strClientCode= :clientCode");
					query.setParameter("clientCode", clientCode);
					list = query.list();
					clsCustomerTypeMasterModel objAreaModel = null;
					if (list.size() > 0)
					{
						for (int i = 0; i < list.size(); i++)
						{
							JSONObject jobj = new JSONObject();
							
							objAreaModel = (clsCustomerTypeMasterModel) list.get(i);
							jobj.put("strCustomeTypeCode",objAreaModel.getStrCustTypeCode());
							jobj.put("strCustomeTypeName",objAreaModel.getStrCustType());					
							
							jArrAreaData.put(jobj);
						}
						jObjData.put("CustomerTypeList", jArrAreaData);
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
*/		}




