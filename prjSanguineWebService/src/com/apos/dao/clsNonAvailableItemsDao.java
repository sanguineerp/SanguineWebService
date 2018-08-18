
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

@Repository("clsNonAvailableItemsDao")
@Transactional(value = "webPOSTransactionManager")


public class clsNonAvailableItemsDao
{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	
	public void funRemoveNonAvailableItem(JSONObject jObj)
	{
		
		try{
		
		 String itemCode = jObj.getString("itemCode");
	     String clientCode = jObj.getString("clientCode");
	     String posCode = jObj.getString("posCode");
	     
	    String  sql="delete from tblnonavailableitems where"
                 + " strItemCode='"+itemCode+"' and strClientCode='"+clientCode+"' and strPOSCode='"+posCode+"' ";
		Query query= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.executeUpdate();                
		System.out.println("success");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}	
	
}
