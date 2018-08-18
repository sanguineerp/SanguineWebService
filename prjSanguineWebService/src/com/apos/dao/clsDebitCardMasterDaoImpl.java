
package com.apos.dao;
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

import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsCounterMasterHdModel;
import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsTaxMasterModel;


@Repository("clsDebitCardMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsDebitCardMasterDaoImpl implements intfDebitCardMasterDao
{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	
	@Override
	public clsDebitCardMasterHdModel funGetSelectedDebitCardMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsDebitCardMasterHdModel model=new clsDebitCardMasterHdModel();
		if(list.size()>0)
		{
			model=(clsDebitCardMasterHdModel)list.get(0);
			model.getListDebitCardSettleDtl().size();
//			model.getListTaxPosDtl().size();
		}
		return model; 
	}
	
	public String funGetAllDebitCardForMaster(String clientCode)
	{

		JSONObject jObjDebitCardList = new JSONObject();	
		JSONArray jArrData = new JSONArray();

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsDebitCardMasterHdModel.class);
			cr.add(Restrictions.eq("strClientCode", clientCode));
			List list = cr.list();

			clsDebitCardMasterHdModel objDebitCardModel = null;
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objDebitCardModel = (clsDebitCardMasterHdModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strCardTypeCode", objDebitCardModel.getStrCardTypeCode());
				objMenu.put("strCardName", objDebitCardModel.getStrCardName());
				jArrData.put(objMenu);
			}
			jObjDebitCardList.put("CardTypeList", jArrData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObjDebitCardList.toString();
		}
	}

	


	

	
}
