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

import com.apos.model.clsMenuItemPricingHdModel;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.apos.model.clsPricingMasterHdModel;
import com.apos.model.clsSettlementMasterModel;

@Repository("clsPricingMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPricingMasterDao
{

	@Autowired
	private SessionFactory	webPOSSessionFactory;



	public void funDeletePOSWisePricing(String strPosCode, String strItemCode, String strAreaCode, String strHourlyPricing)
	{
		String hql = "delete from clsPricingMasterHdModel where strPosCode<> :strPosCode AND strItemCode= :strItemCode AND strAreaCode= :strAreaCode AND strHourlyPricing= :strHourlyPricing";
		Query query = webPOSSessionFactory.getCurrentSession().createQuery(hql);

		query.setString("strPosCode", "All");// delete all records where posCode is not equal to
												// "All"
		query.setString("strItemCode", strItemCode);
		query.setString("strAreaCode", strAreaCode);
		query.setString("strHourlyPricing", strHourlyPricing);
		query.executeUpdate();
	}

	public void funDeletePricingForAllPOS(String strPosCode, String strItemCode, String strAreaCode, String strHourlyPricing)
	{
		String hql = "delete from clsPricingMasterHdModel where strPosCode= :strPosCode AND strItemCode= :strItemCode AND strAreaCode= :strAreaCode AND strHourlyPricing= :strHourlyPricing";
		Query query = webPOSSessionFactory.getCurrentSession().createQuery(hql);

		query.setString("strPosCode", "All");// delete all records where posCode is equal to "All"
		query.setString("strItemCode", strItemCode);
		query.setString("strAreaCode", strAreaCode);
		query.setString("strHourlyPricing", strHourlyPricing);
		query.executeUpdate();
	}

	public String funCheckDuplicateItemPricing(JSONObject jObjPricingMaster) throws Exception
	{

		String isDuplicate = "false";

		String strItemCode = jObjPricingMaster.getString("strItemCode");
		String strPosCode = jObjPricingMaster.getString("strPosCode");
		String strAreaCode = jObjPricingMaster.getString("strAreaCode");
		String strHourlyPricing = jObjPricingMaster.getString("strHourlyPricing");

		String hql = null;
		Query query = null;

		hql = "from clsPricingMasterHdModel where (strPosCode= :strPosCode OR strPosCode='All' ) AND strItemCode= :strItemCode AND strAreaCode= :strAreaCode AND strHourlyPricing= :strHourlyPricing";

		query = webPOSSessionFactory.getCurrentSession().createQuery(hql);

		query.setString("strPosCode", strPosCode);
		query.setString("strItemCode", strItemCode);
		query.setString("strAreaCode", strAreaCode);
		query.setString("strHourlyPricing", strHourlyPricing);

		List list = query.list();
		if (list == null)
		{
			isDuplicate = "false";
		}
		else if (list.size() == 0)
		{
			isDuplicate = "false";
		}
		else
		{
			isDuplicate = "true";
		}

		return isDuplicate;
	}
	
	
	 @SuppressWarnings("finally")
		public JSONObject funGetMenuheadDtlForPromotionMaster(String menuCode)
		{
			List list =null;
			JSONArray jArrAreaData = new JSONArray();
			JSONObject jObjData=new JSONObject();
			try{
			
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery("select distinct strItemCode,strItemName "
                + "from tblmenuitempricingdtl where strMenuCode='" + menuCode + "' ");
		
		 list = query.list();
		 if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject jobj = new JSONObject();
					
					Object obj=list.get(i);
					
					query = webPOSSessionFactory.getCurrentSession().createSQLQuery("select distinct strPriceSunday from tblmenuitempricingdtl where strItemCode='" + Array.get(obj, 0) + "';");
					
					List slist = query.list();
					if (slist.size() > 0)
					{
						
							Object objM=slist.get(0);
							
							jobj.put("strItemCode",Array.get(obj, 0));
							jobj.put("strItemName",Array.get(obj, 1));					
							jobj.put("strRate",objM);	
							jobj.put("ApplicableYN",true);
					}
					jArrAreaData.put(jobj);
				}
				jObjData.put("MenuHeadDtl", jArrAreaData);
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

	public clsPricingMasterHdModel funGetMenuItemPricingMaster(String sql,long pricingId) {
		// TODO Auto-generated method stub
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		query.setParameter("longPricingId",pricingId);
		List list=query.list();
		
		clsPricingMasterHdModel model=new clsPricingMasterHdModel();
		if(list.size()>0)
		{
			model=(clsPricingMasterHdModel)list.get(0);
			
		}
		return model; 
		
	}
	
}
