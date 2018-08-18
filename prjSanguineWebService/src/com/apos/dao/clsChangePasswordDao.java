
package com.apos.dao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsGroupMasterModel;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsTaxMasterModel;
import com.webservice.util.clsGlobalSingleObject;

@Repository("clsChangePasswordDao")
@Transactional(value = "webPOSTransactionManager")


public class clsChangePasswordDao
{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	public JSONObject funGetOldPassword(String userCode,String oldPass)
	{
		String retRes="";
		JSONObject jObjOldPass=new JSONObject();
		try
		{
			String encKey = "04081977";
		
        oldPass = clsGlobalSingleObject.getObjPasswordEncryptDecreat().encrypt(encKey, oldPass);
        String oldPassword="";

        String sqlOldPass = "select a.strUserCode,a.strUserName,a.strPassword  "
                + "from tbluserhd a "
                + "where a.strUserCode='" + userCode + "' ";
        Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlOldPass.toString());
        List list = query.list();
       
        if (list!=null)
        {
        	for(int i=0; i<list.size(); i++)
			{
				Object[] obj = (Object[]) list.get(i);
				
				

				oldPassword = Array.get(obj, 2).toString();
				
				if (!oldPassword.equals(oldPass))
				{
					
					jObjOldPass.put("oldPass", oldPassword);
				}
			}
        }	
        	
        else
        {
        	jObjOldPass.put("user", "");
        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return jObjOldPass;
	}
	
	
	
	
	@SuppressWarnings("finally")
	public JSONObject funSaveUserPassword(JSONObject jObj)
	{
		 JSONObject jObjRet = new JSONObject(); 
		try
		{
//	        String userCode = jObj.getString("userName");
	        String oldPass = jObj.getString("oldPassword");
	        String newPass = jObj.getString("newPassword");
	        String userCode = jObj.getString("userCode");
	       String oldPassword="";
	        
	        
				
            String sqlUpdatePass = "update tbluserhd set strPassword='" + newPass + "' where strUserCode='" + userCode + "' ";
             Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlUpdatePass);
             query.executeUpdate();
             
            List listAffectedRows = query.list();
            int affectedRows = listAffectedRows.size();
            if (affectedRows > 0)
            {
                
            }
	        
	        
	        
	       System.out.println("success"); 
           
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jObjRet;
		
		}
	
}
