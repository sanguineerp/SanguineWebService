package com.onlineordering.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;

import com.mysql.cj.protocol.Resultset;
import com.webservice.controller.clsDatabaseConnection;

@Path("/OnlineOrder")
public class clsOnlineOrderIntegration {

	
	
	@SuppressWarnings("finally")
    @GET 
	@Path("/funInvokeOnlineOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetConnectionInfo()
	{
		String response = "false";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection aposCon=null;
		try
		{
			aposCon=objDb.funOpenPOSCon("mysql","master");
		    response = "true";
		}
		catch(Exception e)
		{
			response="false";
			e.printStackTrace();
		}
		
		System.out.println("Check Server Config...");
		return Response.status(201).entity(response).build();
	}
	
	
	//
	@SuppressWarnings("finally")
    @POST 
	@Path("/pendingorders")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetPendingOrders(@RequestBody JSONObject jobData)
	{
		//JSONObject objPendingOrders=new JSONObject();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null, st1=null, st2=null, st3=null;
        JSONObject jobAllOrders=new JSONObject();
        int orderCount=0;
        JSONObject jobPending=new JSONObject();
		try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			st1=posCon.createStatement();
			st2=posCon.createStatement();
			st3=posCon.createStatement();
			String strClientCode=jobData.get("clientCode").toString();			
	        String x_api_token=jobData.get("x_api_token").toString();
	        String merchant_id=jobData.get("merchant_id").toString(); 
	        
	        Date date = new Date();
	        String currDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	        
	        
	        JSONArray jArrOrderData=new JSONArray();
	        
	        String sqlPendingOrder="select a.strOrderId,a.dtOrderDate,a.delivery_datetime,a.order_state,a.order_type,a.orderMerchant_ref_id \r\n" + 
	        		",a.custName,a.custPhone,a.custAddr1,a.custAddr2,a.custCity,a.instructions,a.order_subtotal,a.order_total"
	        		+ " from tblonlineorderhd a where a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate) ='"+currDate+"' \r\n" + 
	        		" and a.order_state='Placed';";
	        
	        ResultSet rs=st.executeQuery(sqlPendingOrder);
			while(rs.next())
			{
				double dblExtraAmount=0;
				orderCount++;
				JSONObject jobOrder=new JSONObject();
				JSONObject order_info=new JSONObject();
				String orderId=rs.getString(1);
				order_info.put("order_id", rs.getString(1));
				order_info.put("transaction_date", rs.getString(2));
				order_info.put("delivery_date", rs.getString(3));
				order_info.put("status", rs.getString(4));
				order_info.put("trans_type", rs.getString(5));
				order_info.put("external_order_id", rs.getString(6));
				order_info.put("delivery_instruction", rs.getString(12));
				order_info.put("order_otp", "");
				
				jobOrder.put("order_info", order_info);
				
				JSONObject customer=new JSONObject();
		
				
				customer.put("name", rs.getString(7));
				customer.put("mobile", rs.getString(8));
				customer.put("street", rs.getString(9)+","+rs.getString(10));
				customer.put("location", rs.getString(11));
				
				jobOrder.put("customer", customer);
				
				JSONArray jArrCart=new JSONArray();
				
				String sqlCart="select b.itemId,b.itemName,b.quantity,b.price,b.total,b.discount,b.total_with_tax, b.dblExtracharges,b.merchant_id"
						+ " from tblonlineorderhd a,tblonlineorderdtl b " + 
						"where a.strOrderId=b.strOrderId and a.strClientCode=b.strClientCode and date(a.dtOrderDate)=date(b.dtOrderDate)\r\n" + 
						"and b.strOrderId='"+orderId+"' and a.strClientCode='"+strClientCode+"'  and date(a.dtOrderDate)= '"+currDate+"' ;";
				
				ResultSet rsCart=st1.executeQuery(sqlCart);
				while(rsCart.next()) {
					JSONObject jobCardDtl=new JSONObject();
					dblExtraAmount=dblExtraAmount+rsCart.getDouble(8);
					
					String itemCode=rsCart.getString(9);
					jobCardDtl.put("pos_item_id", rsCart.getString(9));
					jobCardDtl.put("item_id", rsCart.getString(1));
					jobCardDtl.put("item_name", rsCart.getString(2));
					jobCardDtl.put("item_description", "");
					jobCardDtl.put("qty", rsCart.getDouble(3));
					jobCardDtl.put("price", rsCart.getDouble(4));
					jobCardDtl.put("price_pretty", rsCart.getDouble(5));
					jobCardDtl.put("discounted_price", rsCart.getDouble(6));
					jobCardDtl.put("discounted_price_pretty", rsCart.getDouble(6));
					jobCardDtl.put("total_price", rsCart.getDouble(5));
					jobCardDtl.put("total", rsCart.getDouble(7));
					jobCardDtl.put("total_pretty", rsCart.getDouble(7));
					
					JSONObject jobSub_item=new JSONObject();
					JSONArray aArrSub_item_content=new JSONArray();
					
					String sqlItemMod="select a.strModifierCode,a.strModifierName,a.strItemCode,a.strItemId,a.dblQuantity,a.dblAmount from tblonlineordermodifierdtl a,tblonlineorderhd b where a.strOrderId=b.strOrderId and a.strClientCode =b.strClientCode\r\n" + 
							"and date(a.dtOrderDate)=date(b.dtOrderDate) and a.strOrderId='"+orderId+"' " + 
							"and a.strItemCode='"+itemCode+"' and a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate)='"+currDate+"'";
					
					ResultSet rsItemMod=st2.executeQuery(sqlItemMod);
					while(rsItemMod.next()) {
						
						JSONObject jobMod=new JSONObject();
								
						jobMod.put("subcat_id", rsItemMod.getString(1));
						jobMod.put("category_name", rsItemMod.getString(2));

						jobMod.put("sub_item_id", rsItemMod.getString(3));
						jobMod.put("price", rsItemMod.getDouble(6));
						jobMod.put("price_pretty", rsItemMod.getDouble(6));
						jobMod.put("qty", rsItemMod.getDouble(5));
						jobMod.put("total", rsItemMod.getDouble(6));
						
						jobMod.put("total_pretty", rsItemMod.getDouble(6));
						jobMod.put("sub_item_name", rsItemMod.getString(2));
						
						aArrSub_item_content.put(jobMod);
					}
					
					jobSub_item.put("sub_item_content", aArrSub_item_content);
					jobCardDtl.put("sub_item", jobSub_item);
				
				
					
					jArrCart.put(jobCardDtl);
				}
				
				jobOrder.put("cart", jArrCart);
				
				JSONArray jArrDisc=new JSONArray();
				String sqlDisc="select a.title,a.dblDiscAmt from tblonlineorderdiscdtl a ,tblonlineorderhd b where a.strOrderId=b.strOrderId and a.strClientCode =b.strClientCode\r\n" + 
						"and date(a.dtOrderDate)=date(b.dtOrderDate) and  a.strOrderId='"+orderId+"' " + 
						"and  a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate)='"+currDate+"'";
				ResultSet rsDisc=st3.executeQuery(sqlDisc);
				while(rsDisc.next()) {
					JSONObject jobDisc=new JSONObject();
					jobDisc.put("name", rsDisc.getString("1") );
					jobDisc.put("amount", rsDisc.getDouble("2") );
					jArrDisc.put(jobDisc);
				}
				
				
				jobOrder.put("discount", jArrDisc);
				
				JSONArray jArrExtra=new JSONArray();
				JSONObject jobExtra=new JSONObject();
				
				jobExtra.put("type", "Packaging Charge");
				jobExtra.put("amount", dblExtraAmount);
				jArrExtra.put(jobExtra);
				
				jobOrder.put("extra", jArrExtra);
				
				JSONObject subtotal=new JSONObject();
				subtotal.put("amount",rs.getDouble(13));
				subtotal.put("amount_pretty",rs.getDouble(13));
				//subtotal			
			
				jobOrder.put("subtotal", subtotal);
	
				JSONObject grandtotal=new JSONObject();
				grandtotal.put("amount",rs.getDouble(14));
				grandtotal.put("amount_pretty",rs.getDouble(14));
				//subtotal			
			
				jobOrder.put("grand_total", grandtotal);
				jArrOrderData.put(jobOrder);
			}
			jobPending.put("orders",jArrOrderData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		try {
			jobAllOrders.put("count", orderCount);
			jobAllOrders.put("details", jobPending);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Check Server Config...");
		return jobAllOrders;
	}
	
	
	///  ordersbystatus 
	
	

	//
	@SuppressWarnings("finally")
    @POST 
	@Path("/ordersbystatus")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetOrdersByStatus(@RequestBody JSONObject jobData)
	{
		//JSONObject objPendingOrders=new JSONObject();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null, st1=null, st2=null, st3=null;
        JSONObject jobAllOrders=new JSONObject();
        int orderCount=0;
        JSONObject jobPending=new JSONObject();
		try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			st1=posCon.createStatement();
			st2=posCon.createStatement();
			st3=posCon.createStatement();
			String strClientCode=jobData.get("clientCode").toString();			
	        String x_api_token=jobData.get("x_api_token").toString();
	        String merchant_id=jobData.get("merchant_id").toString(); 
	        
	        String status=jobData.get("status").toString();
	        Date date = new Date();
	        String currDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	        
	        
	        JSONArray jArrOrderData=new JSONArray();
	        
	        String sqlPendingOrder="select a.strOrderId,a.dtOrderDate,a.delivery_datetime,a.order_state,a.order_type,a.orderMerchant_ref_id \r\n" + 
	        		",a.custName,a.custPhone,a.custAddr1,a.custAddr2,a.custCity,a.instructions,a.order_subtotal,a.order_total"
	        		+ " from tblonlineorderhd a where a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate) ='"+currDate+"' \r\n" + 
	        		" and a.order_state='"+status+"';";
	        
	        ResultSet rs=st.executeQuery(sqlPendingOrder);
			while(rs.next())
			{
				double dblExtraAmount=0;
				orderCount++;
				JSONObject jobOrder=new JSONObject();
				JSONObject order_info=new JSONObject();
				String orderId=rs.getString(1);
				order_info.put("order_id", rs.getString(1));
				order_info.put("transaction_date", rs.getString(2));
				order_info.put("delivery_date", rs.getString(3));
				order_info.put("status", rs.getString(4));
				order_info.put("trans_type", rs.getString(5));
				order_info.put("external_order_id", rs.getString(6));
				order_info.put("delivery_instruction", rs.getString(12));
				order_info.put("order_otp", "");
				
				jobOrder.put("order_info", order_info);
				
				JSONObject customer=new JSONObject();
		
				
				customer.put("name", rs.getString(7));
				customer.put("mobile", rs.getString(8));
				customer.put("street", rs.getString(9)+","+rs.getString(10));
				customer.put("location", rs.getString(11));
				
				jobOrder.put("customer", customer);
				
				JSONArray jArrCart=new JSONArray();
				
				String sqlCart="select b.itemId,b.itemName,b.quantity,b.price,b.total,b.discount,b.total_with_tax, b.dblExtracharges,b.merchant_id"
						+ " from tblonlineorderhd a,tblonlineorderdtl b " + 
						"where a.strOrderId=b.strOrderId and a.strClientCode=b.strClientCode and date(a.dtOrderDate)=date(b.dtOrderDate)\r\n" + 
						"and b.strOrderId='"+orderId+"' and a.strClientCode='"+strClientCode+"'  and date(a.dtOrderDate)= '"+currDate+"' ;";
				
				ResultSet rsCart=st1.executeQuery(sqlCart);
				while(rsCart.next()) {
					JSONObject jobCardDtl=new JSONObject();
					dblExtraAmount=dblExtraAmount+rsCart.getDouble(8);
					
					String itemCode=rsCart.getString(9);
					jobCardDtl.put("pos_item_id", rsCart.getString(9));
					jobCardDtl.put("item_id", rsCart.getString(1));
					jobCardDtl.put("item_name", rsCart.getString(2));
					jobCardDtl.put("item_description", "");
					jobCardDtl.put("qty", rsCart.getDouble(3));
					jobCardDtl.put("price", rsCart.getDouble(4));
					jobCardDtl.put("price_pretty", rsCart.getDouble(5));
					jobCardDtl.put("discounted_price", rsCart.getDouble(6));
					jobCardDtl.put("discounted_price_pretty", rsCart.getDouble(6));
					jobCardDtl.put("total_price", rsCart.getDouble(5));
					jobCardDtl.put("total", rsCart.getDouble(7));
					jobCardDtl.put("total_pretty", rsCart.getDouble(7));
					
					JSONObject jobSub_item=new JSONObject();
					JSONArray aArrSub_item_content=new JSONArray();
					
					String sqlItemMod="select a.strModifierCode,a.strModifierName,a.strItemCode,a.strItemId,a.dblQuantity,a.dblAmount from tblonlineordermodifierdtl a,tblonlineorderhd b where a.strOrderId=b.strOrderId and a.strClientCode =b.strClientCode\r\n" + 
							"and date(a.dtOrderDate)=date(b.dtOrderDate) and a.strOrderId='"+orderId+"' " + 
							"and a.strItemCode='"+itemCode+"' and a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate)='"+currDate+"'";
					
					ResultSet rsItemMod=st2.executeQuery(sqlItemMod);
					while(rsItemMod.next()) {
						
						JSONObject jobMod=new JSONObject();
								
						jobMod.put("subcat_id", rsItemMod.getString(1));
						jobMod.put("category_name", rsItemMod.getString(2));

						jobMod.put("sub_item_id", rsItemMod.getString(3));
						jobMod.put("price", rsItemMod.getDouble(6));
						jobMod.put("price_pretty", rsItemMod.getDouble(6));
						jobMod.put("qty", rsItemMod.getDouble(5));
						jobMod.put("total", rsItemMod.getDouble(6));
						
						jobMod.put("total_pretty", rsItemMod.getDouble(6));
						jobMod.put("sub_item_name", rsItemMod.getString(2));
						
						aArrSub_item_content.put(jobMod);
					}
					
					jobSub_item.put("sub_item_content", aArrSub_item_content);
					jobCardDtl.put("sub_item", jobSub_item);
				
				
					
					jArrCart.put(jobCardDtl);
				}
				
				jobOrder.put("cart", jArrCart);
				
				JSONArray jArrDisc=new JSONArray();
				String sqlDisc="select a.title,a.dblDiscAmt from tblonlineorderdiscdtl a ,tblonlineorderhd b where a.strOrderId=b.strOrderId and a.strClientCode =b.strClientCode\r\n" + 
						"and date(a.dtOrderDate)=date(b.dtOrderDate) and  a.strOrderId='"+orderId+"' " + 
						"and  a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate)='"+currDate+"'";
				ResultSet rsDisc=st3.executeQuery(sqlDisc);
				while(rsDisc.next()) {
					JSONObject jobDisc=new JSONObject();
					jobDisc.put("name", rsDisc.getString("1") );
					jobDisc.put("amount", rsDisc.getDouble("2") );
					jArrDisc.put(jobDisc);
				}
				
				
				jobOrder.put("discount", jArrDisc);
				
				JSONArray jArrExtra=new JSONArray();
				JSONObject jobExtra=new JSONObject();
				
				jobExtra.put("type", "Packaging Charge");
				jobExtra.put("amount", dblExtraAmount);
				jArrExtra.put(jobExtra);
				
				jobOrder.put("extra", jArrExtra);
				
				JSONObject subtotal=new JSONObject();
				subtotal.put("amount",rs.getDouble(13));
				subtotal.put("amount_pretty",rs.getDouble(13));
				//subtotal			
			
				jobOrder.put("subtotal", subtotal);
	
				JSONObject grandtotal=new JSONObject();
				grandtotal.put("amount",rs.getDouble(14));
				grandtotal.put("amount_pretty",rs.getDouble(14));
				//subtotal			
			
				jobOrder.put("grand_total", grandtotal);
				jArrOrderData.put(jobOrder);
			}
			jobPending.put("orders",jArrOrderData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		try {
			jobAllOrders.put("count", orderCount);
			jobAllOrders.put("details", jobPending);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Check Server Config...");
		return jobAllOrders;
	}
	
	
	@SuppressWarnings("finally")
    @POST 
	@Path("/updateOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public String funUpdateOrder(@RequestBody JSONObject jobData)
	{
		String strStatus="Updated";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			String strClientCode=jobData.get("clientCode").toString();			
	        String status=jobData.get("status").toString();
	        String orderID=jobData.get("orderID").toString(); 
	        
	        String sqlPendingOrder="update tblonlineorderhd a set a.order_state='"+status+"' where a.strOrderId='"+orderID+"' and a.strClientCode='"+strClientCode+"'";
	        
	        st.executeUpdate(sqlPendingOrder);
			
		}
		catch(Exception e) {
			strStatus="failed";
			e.printStackTrace();
		}
        return strStatus;    
	}

	@SuppressWarnings("finally")
    @POST 
	@Path("/updateOrder111")
	@Produces(MediaType.APPLICATION_JSON)
	public String funCheckOrder(@RequestBody JSONObject jobData)
	{
		String strStatus="Updated";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			String strClientCode=jobData.get("clientCode").toString();			
	        String status=jobData.get("status").toString();
	        String orderID=jobData.get("orderID").toString(); 
	        
	        String sqlPendingOrder="update tblonlineorderhd a set a.order_state='"+status+"' where a.strOrderId='"+orderID+"' and a.strClientCode='"+strClientCode+"'";
	        
	        st.executeUpdate(sqlPendingOrder);
			
		}
		catch(Exception e) {
			strStatus="failed";
			e.printStackTrace();
		}
        return strStatus;    
	}

}
