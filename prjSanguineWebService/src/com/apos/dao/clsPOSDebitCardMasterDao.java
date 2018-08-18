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

import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsPOSMasterModel;

@Repository("clsDebitCardMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsPOSDebitCardMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	public String funGenerateDebitCardTypeCode()
    {
	String cardTypeCode = "";
	 
	    String sql = "select ifnull(max(strCardTypeCode),0) from tbldebitcardtype";
	    Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
		List list = query.list();
		
		if (!list.get(0).toString().equals("0"))
		{
			String strCode = "0";
		    String code = list.get(0).toString();
		    StringBuilder sb = new StringBuilder(code);
		    String ss = sb.delete(0, 1).toString();
		    for (int i = 0; i < ss.length(); i++)
		    {
				if (ss.charAt(i) != '0')
				{
				    strCode = ss.substring(i, ss.length());
				    break;
				}
		    }
		    int intCode = Integer.parseInt(strCode);
            intCode++;
            if (intCode < 10)
            {
            	cardTypeCode = "D000000" + intCode;
            }
            else if (intCode < 100)
            {
            	cardTypeCode = "D00000" + intCode;
            }
            else if (intCode < 1000)
            {
            	cardTypeCode = "D0000" + intCode;
            }
            else if (intCode < 10000)
            {
            	cardTypeCode = "D000" + intCode;
            }
            else if (intCode < 100000)
            {
            	cardTypeCode = "D00" + intCode;
            }
            else if (intCode < 100000)
            {
            	cardTypeCode = "D0" + intCode;
            }
            else if (intCode < 1000000)
            {
            	cardTypeCode = "D" + intCode;
            }
        }
        else
        {
        	cardTypeCode = "D0000000";
        }
        return cardTypeCode;
    }

	public void funAddUpdateDebitCardTypeMaster(clsDebitCardMasterHdModel objMaster){
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}

	

		
		
		@SuppressWarnings("finally")
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


