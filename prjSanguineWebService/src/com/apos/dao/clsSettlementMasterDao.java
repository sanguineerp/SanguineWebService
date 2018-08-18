
package com.apos.dao;


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

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsSettlementMasterModel;


@Repository("clsSettlementMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsSettlementMasterDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    public String funGenerateSettlementCode()
    {
		String strSettelmentCode = "";
		try
		{
		    
		    String sql = "select ifnull(max(strSettelmentCode),0) from tblsettelmenthd";
		    
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
					strSettelmentCode = "S0" + intCode;
				}
				else if (intCode < 100)
				{
					strSettelmentCode = "S" + intCode;
				}
				
			}
			else
			{
				strSettelmentCode = "S01";
			}

		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return strSettelmentCode;
	    }


    public void funSaveUpdateSettlementMaster(clsSettlementMasterModel objModel)
    {	
	try
	{
	    webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    @SuppressWarnings("finally")
	public JSONObject funGetSettlementDtl(String clientCode)
	{
		List list =null;
		JSONArray jArrAreaData = new JSONArray();
		JSONObject jObjData=new JSONObject();
		try{
		
    Query query = webPOSSessionFactory.getCurrentSession().createQuery("from clsSettlementMasterModel where strClientCode= :clientCode");
	query.setParameter("clientCode", clientCode);
	 list = query.list();
	 clsSettlementMasterModel objAreaModel = null;
	 if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				JSONObject jobj = new JSONObject();
				
				objAreaModel = (clsSettlementMasterModel) list.get(i);
				jobj.put("SettlementCode",objAreaModel.getStrSettelmentCode());
				jobj.put("SettlementDesc",objAreaModel.getStrSettelmentDesc());					
				jobj.put("ApplicableYN",objAreaModel.getStrApplicable());					
				
				jArrAreaData.put(jobj);
			}
			jObjData.put("SettlementDtl", jArrAreaData);
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
    
   
}
