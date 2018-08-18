package com.apos.dao;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCustomerTypeMasterModel;
import com.apos.model.clsMenuItemMasterModel;
import com.apos.model.clsMenuItemPricingHdModel;
import com.apos.model.clsModifierGroupMasterHdModel;
import com.webservice.controller.clsDatabaseConnection;

@Repository("clsMenuItemMasterDao")
@Transactional(value = "webPOSTransactionManager")
public class clsMenuItemMasterDao {
	

	@Autowired
	private SessionFactory webPOSSessionFactory;


	public clsMenuItemMasterModel funGetMenuItemMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsMenuItemMasterModel model=new clsMenuItemMasterModel();
		if(list.size()>0)
		{
			model=(clsMenuItemMasterModel)list.get(0);
			
			
		}
		return model; 
	} 
	 
	 public JSONArray  funGetAllRevenueHead(){
		 
		 	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
		 String sql="select distinct(strRevenueHead) from tblitemmaster order by strRevenueHead; ";
			JSONArray arrObj=new JSONArray();
			
			 try
		        {
				 aposCon=objDb.funOpenAPOSCon("mysql","master");
		            st = aposCon.createStatement();
		            
		            ResultSet rsMasterData=st.executeQuery(sql);
	                while(rsMasterData.next())
	                {
	        	      
	        	        arrObj.put(rsMasterData.getString(1));
	                }
	                rsMasterData.close();
	        
	               
	                st.close();
	                aposCon.close();
	        
		     }catch(Exception e)
			 {
		    	 e.printStackTrace();
			 }
		 
			return arrObj;
			
		}
	 public JSONObject funFillItemTable(String clientCode)
	 {
		 JSONObject jObjItemList = new JSONObject();
			JSONArray jArrData = new JSONArray();

			try
			{
				String sql="select strItemCode ,strItemName from tblitemmaster"
	                    + " where strClientCode ='"+clientCode+"' ORDER BY strItemName ASC";
				Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List itemList = query.list();
	            
				if(itemList!=null)
				{
				for (int i = 0; i < itemList.size(); i++)
				{
					Object[] obj = (Object[]) itemList.get(i);
						
					JSONObject objCust = new JSONObject();
					objCust.put("strItemCode", Array.get(obj, 0));
					objCust.put("strItemName",Array.get(obj, 1));
					

					jArrData.put(objCust);
				}
				}
				jObjItemList.put("itemList", jArrData);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				return jObjItemList;
			}
	 }
	 
}


