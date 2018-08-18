package com.apos.dao;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsReasonMasterModel;

@Repository("clsReasonMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsReasonMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    public String funGenerateReasonCode() 
    {
	String reasonCode = "", strCode = "";
	
	String selectQuery = "select count(*) from tblreasonmaster";
	
	List list = webPOSSessionFactory.getCurrentSession().createSQLQuery(selectQuery).list();
	
	if (!list.get(0).toString().equals("0"))
	{
	    selectQuery = "select max(strReasonCode) from tblreasonmaster";
	    list = webPOSSessionFactory.getCurrentSession().createSQLQuery(selectQuery).list();
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
		reasonCode = "R0" + intCode;
	    }
	    else
	    {
		reasonCode = "R" + intCode;
	    }
	}
	else
	{
	    reasonCode = "R01";
	}
	
	return reasonCode;
    }
    
    public void funSaveUpdateReasonMaster(clsReasonMasterModel objModel)
    {
	try
	{
	    webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    

    public String funGetAllReasonMaster(String clientCode)
	{

		JSONObject jObjReasonList = new JSONObject();	
		JSONArray jArrData = new JSONArray();

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsReasonMasterModel.class);
			cr.add(Restrictions.eq("strClientCode", clientCode));
			List list = cr.list();

			clsReasonMasterModel objReasonMasterModel = null;
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objReasonMasterModel = (clsReasonMasterModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strReasonCode", objReasonMasterModel.getStrReasonCode());
				objMenu.put("strReasonName", objReasonMasterModel.getStrReasonName());
				jArrData.put(objMenu);
			}
			jObjReasonList.put("ReasonList", jArrData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObjReasonList.toString();
		}
	}
    
    //delete this dao class
    
    
    
}
