package com.apos.service;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.model.clsPOSWiseItemIncentiveModel;
import com.apos.model.clsPOSWiseItemIncentiveModel_ID;
import com.sanguine.service.intfBaseService;


@Service("clsPOSWiseItemIncentiveController")
public class clsPOSWiseItemIncentiveServicesImpl implements clsPOSWiseItemIncentiveServices {

	
	@Autowired
	intfBaseService objDao;
	
	@Autowired
	private clsDebitCardSettlementDetailsService objSettle;
	
	
	public String funSavePOSWiseItemIncentive(JSONObject objfunLoadPOSWiseItemIncentive)
	{
		 
			 JSONArray ArrayList=new JSONArray();
				String flg="NOT DONE";
				 try
			        {
			            String posCode =objfunLoadPOSWiseItemIncentive.getString("POSCode");
			            String ClientCode =objfunLoadPOSWiseItemIncentive.getString("ClientCode");
			            String CurrentDateTime =objfunLoadPOSWiseItemIncentive.getString("dteDateCreated");
			            ArrayList =objfunLoadPOSWiseItemIncentive.getJSONArray("List");	            
		/*	            int ch = JOptionPane.showConfirmDialog(new JPanel(), "Do you want to Update All Item?", "Confirmation", JOptionPane.YES_NO_OPTION);
			            if (ch == JOptionPane.YES_OPTION)
			            {
			              if(!posCode.equalsIgnoreCase("All"))
			              {
			                 String deleteQuery = " delete from tblposwiseitemwiseincentives where strPOSCode='"+posCode+"' ";                  
			               sqlQuery= webPOSSessionFactory.getCurrentSession().createSQLQuery(deleteQuery.toString());
			  	            sqlQuery.executeUpdate();	             	          
			              }
			              else
			              {
			                  String deleteQuery = " truncate table tblposwiseitemwiseincentives  ";                  
			                  sqlQuery= webPOSSessionFactory.getCurrentSession().createSQLQuery(deleteQuery.toString());
				  	            sqlQuery.executeUpdate();	
			              }*/
			              
			             
			              for(int i=0; i<ArrayList.length(); i++)
		  			    {	
			            	  
			            	  
			            	  
		  				 JSONObject jObj = new JSONObject();
		  			    	jObj=ArrayList.getJSONObject(i);
		  			    	
		  			    	String strItemCode = jObj.getString("strItemCode");
		  			    	String strItemName = jObj.getString("strItemName");
		  				    String strIncentiveType = jObj.getString("strIncentiveType");
		  				    double strIncentiveValue = Double.parseDouble(jObj.getString("strIncentiveValue"));	
		  				    String strPOSCode = jObj.getString("strPOSCode");
		  				    
		  				    
		  				  clsPOSWiseItemIncentiveModel objModel = new clsPOSWiseItemIncentiveModel(new clsPOSWiseItemIncentiveModel_ID(strPOSCode,strItemCode,ClientCode));
						   
		  				  objModel.setStrPOSCode(strPOSCode);
					        objModel.setStrItemCode(strItemCode);
						    objModel.setStrItemName(strItemName);
						    objModel.setStrIncentiveType(strIncentiveType);
						    objModel.setDblIncentiveValue(strIncentiveValue);
						    objModel.setStrClientCode(ClientCode);
						    objModel.setDteDateCreated(CurrentDateTime);
						    objModel.setDteDateEdited(CurrentDateTime);
						    objModel.setStrDataPostFlag("N");
		  			    
						    objDao.funSave(objModel);
						    
		  			    }
			                   /* String insertQuery = "insert into tblposwiseitemwiseincentives (strPOSCode,strItemCode,strItemName,strIncentiveType,dblIncentiveValue,strClientCode,strDataPostFlag,dteDateCreated,dteDateEdited) values " ;
			                    for(int i=0; i<ArrayList.length(); i++)
			    			    {						    	
			    				 JSONObject jObj = new JSONObject();
			    			    	jObj=ArrayList.getJSONObject(i);
			    			    	
			    			    	String strItemCode = jObj.getString("strItemCode");
			    			    	String strItemName = jObj.getString("strItemName");
			    				    String strIncentiveType = jObj.getString("strIncentiveType");
			    				    double strIncentiveValue = Double.parseDouble(jObj.getString("strIncentiveValue"));	
			    				    String strPOSCode = jObj.getString("strPOSCode");
			    				   if(cnt==0)
			    				   {
			    				    insertQuery += "('" + strPOSCode + "','" +strItemCode+ "', '" +strItemName+ "', '" +strIncentiveType+ "','"+strIncentiveValue+"','"+ClientCode +"','N','"+CurrentDateTime+"','"+CurrentDateTime+"')";	    			   
			    				   }
			    				   else
			    				   {
			    					   insertQuery += ",('" + strPOSCode + "','" +strItemCode+ "', '" +strItemName+ "', '" +strIncentiveType+ "','"+strIncentiveValue+"','"+ClientCode +"','N','"+CurrentDateTime+"','"+CurrentDateTime+"')"; 
			    				   }
			    				   cnt++;
			    			    }
			    	                              	                  
			                    sqlQuery= webPOSSessionFactory.getCurrentSession().createSQLQuery(insertQuery.toString());
				  	            sqlQuery.executeUpdate();                    
			                    */
			                /*    String sql="update tblmasteroperationstatus set dteDateEdited='"+CurrentDateTime+"' "
			                            + " where strTableName='PosWiseItemWiseIncentive' and strClientCode='"+ClientCode+"'";
			                    sqlQuery= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
				  	            sqlQuery.executeUpdate();*/		                        	                  	                   
			               flg="Done";
			           // }   
			            
			        }
				 
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }
				return flg;
	}
	
}
