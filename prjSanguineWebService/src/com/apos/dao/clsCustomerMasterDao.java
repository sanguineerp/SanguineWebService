package com.apos.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsCustomerMasterModel;
import com.apos.model.clsPOSMasterModel;
import com.webservice.controller.clsDatabaseConnection;

/*@Repository("clsCustomerMasterDao")
@Transactional(value = "webPOSTransactionManager")*/
public class clsCustomerMasterDao{

	@Autowired
	private SessionFactory WebPOSSessionFactory;

		public String funSaveCustomerMaster(clsCustomerMasterModel objModel) throws Exception
		{
			WebPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
			return objModel.getStrCustomerCode();
		}
	    public String funGenerateCustomerMasterCode(String clientCode) throws Exception
	    {
	    	clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection cmsCon=null;
	        Statement st=null;
	        String customerMasterCode = "", strCode = "", code = "";
	        long lastNo = 1;
	        String propertCode=clientCode.substring(4);
	       
	        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
		        st = cmsCon.createStatement();
			
	            String sql = "select count(*) from tblcustomermaster";
	            ResultSet rsCustCode = st.executeQuery(sql);
	            rsCustCode.next();
	            int cntCustCode = rsCustCode.getInt(1);
	            rsCustCode.close();

	            if (cntCustCode > 0)
	            {
	                sql = "select max(right(strCustomerCode,8)) from tblcustomermaster";
	                rsCustCode = st.executeQuery(sql);
	                rsCustCode.next();
	                code = rsCustCode.getString(1);
	                StringBuilder sb = new StringBuilder(code);                
	               
	                strCode = sb.substring(1,sb.length());
	                
	                lastNo = Long.parseLong(strCode);
	                lastNo++;
	                customerMasterCode = propertCode+"C" + String.format("%07d", lastNo);

	                rsCustCode.close();
	            }
	            else
	            {
	                sql = "select longCustSeries from tblsetup";
	                ResultSet rsCustSeries = st.executeQuery(sql);
	                if (rsCustSeries.next())
	                {
	                    lastNo = Long.parseLong(rsCustSeries.getString(1));
	                }
	                rsCustSeries.close();
	                customerMasterCode = propertCode+"C" + String.format("%07d", lastNo);
	                
	            }
	     
			return customerMasterCode ;
	    }
	    
		public String funGetAllCityForMaster(String clientCode)
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
        }
		
		
		
		
		
		
}
	


