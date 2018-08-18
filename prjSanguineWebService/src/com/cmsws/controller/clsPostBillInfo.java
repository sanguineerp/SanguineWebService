package com.cmsws.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.webservice.controller.clsDatabaseConnection;

@Path("/getBillInfo")
public class clsPostBillInfo 
{
	// HTTP Get Method
		@GET 
		@Path("/funGetBillInfo")
		@Produces(MediaType.APPLICATION_JSON)
		public String funGetBillInfo(@QueryParam("dteFromDate") String fromDate, @QueryParam("dteToDate") String toDate)
		{
			String response = "";
			String billData=funGetBillDatFromDB(fromDate, toDate);
			if(billData.equalsIgnoreCase("no data"))
			{
				//response = clsUtitlity.funConstructJSONForErrorMsg("CMSMemberData", false, "Data Not Found").toString();
			}
			else
			{
				response = billData;
			}
			return response;
		}
		
		private String funGetBillDatFromDB(String fromDate,String toDate)
		{
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        String billData="";
	        JSONObject jObj=new JSONObject();
	        Connection posCon=null;
            Statement st = null;
	        
	        try
	        {    
	        	posCon=objDb.funOpenPOSCon("mysql","");
	            st = posCon.createStatement();
	            boolean flgData=false;
	            String sql="select a.strBillNo,a.dteBillDate,a.dblDiscountAmt,a.dblSubTotal,a.dblGrandTotal"
	                + ",b.longCustMobileNo,b.strCustomerId,a.strCouponCode "
	                + "from vqbillhd a,tblcrmpoints b "
	                + "where a.strBillNo=b.strBillNo and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"'";
	            //System.out.println(sql);
	            List  l1 = new LinkedList();
	            JSONArray arrObj=new JSONArray();
	            
	            ResultSet rsBillData=st.executeQuery(sql);
	            while(rsBillData.next())
	            {
	            	JSONObject obj=new JSONObject();
	            	obj.put("UserName",rsBillData.getString(6));
	            	obj.put("TotalAmount",new Double(rsBillData.getString(5)));
	            	obj.put("InvoiceNo",rsBillData.getString(1));
	            	obj.put("InvoiceDate",rsBillData.getString(2));
	            	obj.put("DiscountAmount",new Double(rsBillData.getString(3)));
	            	obj.put("NetAmount",new Double(rsBillData.getString(4)));
	            	obj.put("CouponCode",rsBillData.getString(8));
	            	arrObj.put(obj);
	            	
	                flgData=true;
	            }
	            rsBillData.close();
	            
	            
	            jObj.put("BillInfo", arrObj);
	            //System.out.println(jObj.toString());
	            	            
	            if(!flgData)
	            {
	            	billData="no data";
	            	arrObj.put(billData);
	            }
	            
	            
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	try {
					st.close();
					posCon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	                   	
	        }
	        return jObj.toString();
		}
}
