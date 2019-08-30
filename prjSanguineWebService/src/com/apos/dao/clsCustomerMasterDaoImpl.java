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

import com.apos.model.clsCustomerMasterModel;
import com.apos.model.clsCustomerTypeMasterModel;

@Repository("clsCustomerMasterDaoImpl")
@Transactional(value = "webPOSTransactionManager")
public class clsCustomerMasterDaoImpl implements inftCustomerMasterDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;

	public clsCustomerMasterModel funLoadCustomeMasterData(String sql,Map<String,String> hmParameters) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
		{
			query.setParameter(entrySet.getKey(), entrySet.getValue());
		}
		List list=query.list();
		
		clsCustomerMasterModel model=new clsCustomerMasterModel();
		if(list.size()>0)
		{
			model=(clsCustomerMasterModel)list.get(0);
						
		}
		return model;
	}

	@Override
	public String funGetAllCountryForMaster(String clientCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String funGetAllStateForMaster(String clientCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String funGetAllCityForMaster(String clientCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
		/*public String funGetAllCityForMaster(String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			String sql="select * from dbwebmms.tblcitymaster ";
			JSONArray arrObj=new JSONArray();
			
			 try
		        {
				 aposCon=objDb.funOpenAPOSCon("mysql","master");
		            st = aposCon.createStatement();
		            
		            ResultSet rsMasterData=st.executeQuery(sql);
	                while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strCityCode",rsMasterData.getString(1));
	        	        obj.put("strCityName",rsMasterData.getString(2));
	        	        obj.put("strCountryCode",rsMasterData.getString(3));
	        	        obj.put("strStateCode",rsMasterData.getString(4));
	        	        obj.put("strUserCreated",rsMasterData.getString(5));
	        	        obj.put("dtCreatedDate",rsMasterData.getString(6));
	        	        obj.put("strUserModified",rsMasterData.getString(7));
	        	       // obj.put("dtLastModified",rsMasterData.getString(7));
	        	        obj.put("intGId",rsMasterData.getString(8));
	        	       // obj.put("strClientCode",rsMasterData.getString(9));
	        	       
	        	        arrObj.put(obj);
	                }
	                rsMasterData.close();
	        
	                jObj.put("cityList", arrObj);
	                st.close();
	                aposCon.close();
	        
		     }catch(Exception e)
			 {
		    	 e.printStackTrace();
			 }
			 return jObj.toString();
		}
		
		public String funGetAllStateForMaster(String clientCode)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			String sql="select * from dbwebmms.tblstatemaster ";
			JSONArray arrObj=new JSONArray();
			
			 try
		        {
				 aposCon=objDb.funOpenAPOSCon("mysql","master");
		            st = aposCon.createStatement();
		            
		            ResultSet rsMasterData=st.executeQuery(sql);
	                while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strStateCode",rsMasterData.getString(1));
	        	        obj.put("strStateName",rsMasterData.getString(2));
	        	        obj.put("strStateDesc",rsMasterData.getString(3));
	        	        obj.put("strCountryCode",rsMasterData.getString(4));
	        	        obj.put("strUserCreated",rsMasterData.getString(5));
	        	        obj.put("dtCreatedDate",rsMasterData.getString(6));
	        	        obj.put("strUserModified",rsMasterData.getString(7));
	        	        obj.put("dtLastModified",rsMasterData.getString(8));
	        	        obj.put("strClientCode",rsMasterData.getString(9));
	        	        obj.put("strPropertyCode",rsMasterData.getString(10));
	        	        obj.put("intGId",rsMasterData.getString(11));
	        	        arrObj.put(obj);
	                }
	                rsMasterData.close();
	        
	                jObj.put("stateList", arrObj);
	                st.close();
	                aposCon.close();
	        
		     }catch(Exception e)
			 {
		    	 e.printStackTrace();
			 }
			 return jObj.toString();
		}
		
		public String funGetAllCountryForMaster(String clientCode)
{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        JSONObject jObj=new JSONObject();
			String sql="select * from dbwebmms.tblcountrymaster ";
			JSONArray arrObj=new JSONArray();
			
			 try
		        {
				 aposCon=objDb.funOpenAPOSCon("mysql","master");
		            st = aposCon.createStatement();
		            
		            ResultSet rsMasterData=st.executeQuery(sql);
	                while(rsMasterData.next())
	                {
	        	        JSONObject obj=new JSONObject();
	        	        obj.put("strCountryCode",rsMasterData.getString(1));
	        	        obj.put("strCountryName",rsMasterData.getString(2));
	        	        obj.put("strUserCreated",rsMasterData.getString(3));
	        	        obj.put("dtCreatedDate",rsMasterData.getString(4));
	        	        obj.put("strUserModified",rsMasterData.getString(5));
	        	        obj.put("dtLastModified",rsMasterData.getString(6));
	        	        obj.put("strClientCode",rsMasterData.getString(7));
	        	        obj.put("strPropertyCode",rsMasterData.getString(8));
	        	        obj.put("intGId",rsMasterData.getString(9));
	        	        arrObj.put(obj);
	                }
	                rsMasterData.close();
	        
	                jObj.put("countryList", arrObj);
	                st.close();
	                aposCon.close();
	           
		     }
			 catch(Exception e)
			 {
		    	 e.printStackTrace();
			 }
			 return jObj.toString();
        }*/
	
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	


