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

import com.apos.model.clsPOSPromationMasterHdModel;

@Repository("clsPromotionMasterDaoImpl")
@Transactional(value= "webPOSTransactionManager")
public class clsPromotionMasterDaoImpl implements intfPromotionMasterDao {
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	
	@Override
	public clsPOSPromationMasterHdModel funGetPromotionMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsPOSPromationMasterHdModel model=new clsPOSPromationMasterHdModel();
		if(list.size()>0)
		{
			model=(clsPOSPromationMasterHdModel)list.get(0);
			model.getListBuyPromotionDtl().size();
			model.getListDayTimeDtl().size();
			model.getListGetPromotionDtl().size();
		}
		return model; 
	} 

	 public boolean funCheckDuplicateBuyPromoItem(String promoItemCode,String promoCode,String posCode,String areaCode) throws Exception
	    {
	        boolean flgResult = false;

	        String sql = " select strPromoCode "
	                + " from tblpromotionmaster "
	                + " where strPromoItemCode='" + promoItemCode + "' and strPromoCode!='" + promoCode + "' "
	                + " and (strPOSCode='" + posCode + "' or strPOSCode='All') "
	                + " and strAreaCode='"+areaCode+"' ";
	        Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
           List list = query.list();
	            if(list.size()>0)
	            {
	            flgResult = true;
	            }
	        else
	        {
	            sql = "select a.strPromoCode "
	                    + " from tblpromotionmaster a,tblbuypromotiondtl b "
	                    + " where a.strPromoCode=b.strPromoCode and b.strBuyPromoItemCode='" + promoItemCode + "' "
	                    + " and a.strPromoCode!='" + promoCode+ "' "
	                    + " and (a.strPOSCode='" + posCode+ "' or a.strPOSCode='All')";

	            query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
	            list = query.list();
	 	            if(list.size()>0)
	 	            {
	 	            	flgResult = true;
	 	            }
	        }
	        return flgResult;
	    }
}
