package com.onlineordering.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

//import com.mysql.cj.protocol.Resultset;
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
	        		",a.custName,a.custPhone,a.custAddr1,a.custAddr2,a.custCity,a.instructions,a.order_subtotal,a.order_total,a.channel,a.delivery_type,a.total_charges,a.total_taxes "
	        		+ " from tblonlineorderhd a where a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate) ='"+currDate+"' \r\n" + 
	        		" and a.order_state='Placed';";
	        
	        ResultSet rs=st.executeQuery(sqlPendingOrder);
			while(rs.next())
			{
				double dbltaxAmount=rs.getDouble(18);
				double dblExtraAmount=rs.getDouble(17);
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
				order_info.put("order_type", rs.getString(15));// channel name
				order_info.put("delivery_type", rs.getString(16));// delivery type
				
				jobOrder.put("order_info", order_info);
				
				JSONObject customer=new JSONObject();
		
				
				customer.put("name", rs.getString(7));
				customer.put("mobile", rs.getString(8));
				customer.put("street", rs.getString(9)+","+rs.getString(10));
				customer.put("location", rs.getString(11));
				
				jobOrder.put("customer", customer);
				
				JSONArray jArrCart=new JSONArray();
				
				String sqlCart="select b.itemId,b.itemName,b.quantity,b.price,b.total,b.discount,b.total_with_tax, b.dblExtracharges,b.merchant_id,b.strSequenceNo "
						+ " from tblonlineorderhd a,tblonlineorderdtl b " + 
						"where a.strOrderId=b.strOrderId and a.strClientCode=b.strClientCode and date(a.dtOrderDate)=date(b.dtOrderDate)\r\n" + 
						"and b.strOrderId='"+orderId+"' and a.strClientCode='"+strClientCode+"'  and date(a.dtOrderDate)= '"+currDate+"' ;";
				
				ResultSet rsCart=st1.executeQuery(sqlCart);
				while(rsCart.next()) {
					JSONObject jobCardDtl=new JSONObject();
					//dblExtraAmount=dblExtraAmount+rsCart.getDouble(8);
					
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
		
					String seqNo=rsCart.getString(10);
					jobCardDtl.put("sequanceNo",seqNo );
		
					JSONObject jobSub_item=new JSONObject();
					JSONArray aArrSub_item_content=new JSONArray();
					
					String sqlItemMod="select a.strModifierCode,a.strModifierName,a.strItemCode,a.strItemId,a.dblQuantity,a.dblAmount,a.strSequenceNo from tblonlineordermodifierdtl a,tblonlineorderhd b where a.strOrderId=b.strOrderId and a.strClientCode =b.strClientCode\r\n" + 
							"and date(a.dtOrderDate)=date(b.dtOrderDate) and a.strOrderId='"+orderId+"' " + 
							"and a.strItemCode='"+itemCode+"'  and left(a.strSequenceNo,1)='" + seqNo + "'  and a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate)='"+currDate+"'";
					
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
						"and  a.strClientCode='"+strClientCode+"'  and date(a.dtOrderDate)='"+currDate+"'";
				ResultSet rsDisc=st3.executeQuery(sqlDisc);
				while(rsDisc.next()) {
					JSONObject jobDisc=new JSONObject();
					jobDisc.put("name", rsDisc.getString(1) );
					jobDisc.put("amount", rsDisc.getDouble(2));
					jArrDisc.put(jobDisc);
				}
				
				
				jobOrder.put("discount", jArrDisc);
				
				JSONArray jArrExtra=new JSONArray();
				JSONObject jobExtra=new JSONObject();
				
				jobExtra.put("type", "Packaging Charge");
				jobExtra.put("amount", dblExtraAmount);
				jArrExtra.put(jobExtra);
				
				jobExtra=new JSONObject();
				jobExtra.put("type", "Tax Charge");
				jobExtra.put("amount", dbltaxAmount);
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
			st.close();
			st1.close();
			st2.close();
			st3.close();
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
		finally {
			try {
				posCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
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
	        		",a.custName,a.custPhone,a.custAddr1,a.custAddr2,a.custCity,a.instructions,a.order_subtotal,a.order_total,a.channel,a.delivery_type "
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
				order_info.put("order_type", rs.getString(15));// channel name
				order_info.put("delivery_type", rs.getString(16));// delivery type
				
				jobOrder.put("order_info", order_info);
				
				JSONObject customer=new JSONObject();
		
				
				customer.put("name", rs.getString(7));
				customer.put("mobile", rs.getString(8));
				customer.put("street", rs.getString(9)+","+rs.getString(10));
				customer.put("location", rs.getString(11));
				
				jobOrder.put("customer", customer);
				
				JSONArray jArrCart=new JSONArray();
				
				String sqlCart="select b.itemId,b.itemName,b.quantity,b.price,b.total,b.discount,b.total_with_tax, b.dblExtracharges,b.merchant_id,b.strSequenceNo "
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
					String seqNo=rsCart.getString(10);
					jobCardDtl.put("sequanceNo",seqNo );
					JSONObject jobSub_item=new JSONObject();
					JSONArray aArrSub_item_content=new JSONArray();
					
					String sqlItemMod="select a.strModifierCode,a.strModifierName,a.strItemCode,a.strItemId,a.dblQuantity,a.dblAmount from tblonlineordermodifierdtl a,tblonlineorderhd b where a.strOrderId=b.strOrderId and a.strClientCode =b.strClientCode\r\n" + 
							"and date(a.dtOrderDate)=date(b.dtOrderDate) and a.strOrderId='"+orderId+"' " + 
							"and a.strItemCode='"+itemCode+"' and left(a.strSequenceNo,1)='" + seqNo + "'   and a.strClientCode='"+strClientCode+"' and date(a.dtOrderDate)='"+currDate+"'";
					
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
					jobDisc.put("name", rsDisc.getString(1) );
					jobDisc.put("amount", rsDisc.getDouble(2) );
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
			st.close();
			st1.close();
			st2.close();
			st3.close();
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
		finally {
			try {
				posCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
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
			st.close();
		}
		catch(Exception e) {
			strStatus="failed";
			e.printStackTrace();
		}finally {
			try {
				posCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
        finally {
			try {
				posCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        return strStatus;    
	}

	@SuppressWarnings("finally")
    @POST 
	@Path("/cataloguestatus")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetCatalogueStatus(@RequestBody JSONObject jobData)
	{
		
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jobCatalogue=new JSONObject();
        
       
		try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			
			String strClientCode=jobData.get("clientCode").toString();			
	        String authorization=jobData.get("Authorization").toString();
	        
	        Date date = new Date();
	        String currDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	        
	        JSONObject jobStatus=new JSONObject();
	        
	        String sqlcatalogue="select a.catgUpdate,a.catgCreated,a.catgDeleted,a.catgError,a.itemUpdate,a.itemCreated,a.itemDeleted,a.itemError, "
								+ " a.optionGrpUpdate,a.optionGrpCreated,a.optionGrpDeleted,a.optionGrpError,a.optionUpdate,  "
								+ " a.optionCreated,a.optionDeleted,a.optionError  "
								+ " from tblonlinecatalogueingestion a where DATE(a.dteCurrentDate)='2020-08-22' and a.strClientCode='"+strClientCode+"'";
	        
	       ResultSet rs=st.executeQuery(sqlcatalogue);
			while(rs.next())
			{
				
				jobCatalogue.put("updated", rs.getString(1));
				jobCatalogue.put("created", rs.getString(2));
				jobCatalogue.put("deleted", rs.getString(3));
				jobCatalogue.put("errors", rs.getString(4));
				
				jobCatalogue.put("items updated", rs.getString(5));
				jobCatalogue.put("items created", rs.getString(6));
				jobCatalogue.put("items deleted", rs.getString(7));
				jobCatalogue.put("items errors", rs.getString(8));
				
				jobCatalogue.put("optionGroup updated", rs.getString(9));
				jobCatalogue.put("optionGroup created", rs.getString(10));
				jobCatalogue.put("optionGroup deleted", rs.getString(11));
				jobCatalogue.put("optionGroup errors", rs.getString(12));
				
				jobCatalogue.put("option updated", rs.getString(13));
				jobCatalogue.put("option created", rs.getString(14));
				jobCatalogue.put("option deleted", rs.getString(15));
				jobCatalogue.put("option errors", rs.getString(16));
				
           }	
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		System.out.println("Check Server Config...");
		return jobCatalogue;
	}
	
	@SuppressWarnings("finally")
    @POST 
	@Path("/storeactionstatus")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetStoreActionStatus(@RequestBody JSONObject jobData)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jobStoreAction=new JSONObject();
       
		try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			String strClientCode=jobData.get("clientCode").toString();	
			String authorization=jobData.get("Authorization").toString();
	       
	        Date date = new Date();
	        String currDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	        
	        JSONObject jobStatus=new JSONObject();
	        
	        String sqlstoreaction="select a.strAction,a.strPlatform ,a.strStatus "
	        					+ " from tblonlineorderstoreaction a where Date(a.ts_utc)='2020-08-17' and  a.strClientCode='"+strClientCode+"' ";
	        
	        	        
	        ResultSet rs=st.executeQuery(sqlstoreaction);
			while(rs.next())
			{
				jobStoreAction.put("action", rs.getString(1));
				jobStoreAction.put("platform", rs.getString(2));
				jobStoreAction.put("status", rs.getString(3));
				
		    }	
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	   return jobStoreAction;
	}
	
	@SuppressWarnings("finally")
    @POST 
	@Path("/itemactionstatus")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetItemActionStatus(@RequestBody JSONObject jobData)
	{
		//JSONObject objPendingOrders=new JSONObject();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jobItemAction=new JSONObject();
        JSONObject itemaction=new JSONObject();
		try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			
			String strClientCode=jobData.get("clientCode").toString();			
	        String authorization=jobData.get("Authorization").toString();
	        
	        Date date = new Date();
	        String currDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	        
	        JSONObject jobStatus=new JSONObject();
	        
	        String sqlitemaction="select a.strAction,a.strPlatform,a.strItemStatus  "
	        		+ "from tblonlineorderitemaction a where Date(a.ts_utc)='2019-09-24 ' and a.strClientCode='"+strClientCode+"'  ";
	       
	        ResultSet rs=st.executeQuery(sqlitemaction);
			while(rs.next())
			{
				
				itemaction.put("action", rs.getString(1));
				itemaction.put("platform", rs.getString(2));
				itemaction.put("status", rs.getString(3));
				
				//jobStatus.put("options",option);
           }	
			
			
			//jobItemAction.put("stats",jobStatus);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
      return itemaction;
	}
	
	@SuppressWarnings("finally")
    @POST 
	@Path("/storeaddupdatestatus")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetStoreAddUpdate(@RequestBody JSONObject jobData)
	{
		//JSONObject objPendingOrders=new JSONObject();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject jobItemAction=new JSONObject();
        JSONObject storeAddUpdate=new JSONObject();
		try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			
			String strClientCode=jobData.get("clientCode").toString();			
	        String authorization=jobData.get("Authorization").toString();
	        
	        Date date = new Date();
	        String currDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	        
	        JSONObject jobStatus=new JSONObject();
	        
	        String sqlitemaction="select a.updatedStore,a.errorsStore,a.createdStore "
	        		+ "from tblonlineorderstoreaddupdate a where Date(a.dteCurrentDate)='2020-08-20' and a.strClientCode='"+strClientCode+"' ";
	       
	        ResultSet rs=st.executeQuery(sqlitemaction);
			while(rs.next())
			{
				
				storeAddUpdate.put("action", rs.getString(1));
				storeAddUpdate.put("platform", rs.getString(2));
				storeAddUpdate.put("status", rs.getString(3));
				
				//jobStatus.put("options",option);
           }	
			
			
			//jobItemAction.put("stats",jobStatus);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
      return storeAddUpdate;
	}
	
	@SuppressWarnings("finally")
    @POST 
	@Path("/riderstatus")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetRiderStatus(@RequestBody JSONObject jobData)
	{
		
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        JSONObject riderStatus=new JSONObject();
		try
		{
			posCon=objDb.funOpenPOSCon("mysql","master");
			st=posCon.createStatement();
			
			String strClientCode=jobData.get("clientCode").toString();			
	        String authorization=jobData.get("Authorization").toString();
	        
	        Date date = new Date();
	        String currDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	        
	        JSONObject jobStatus=new JSONObject();
	        
	        String sqlitemaction="select a.channel,a.orderState,a.riderMode,a.assignStatus,a.assignComments,a.unassignStatus,a.unassignComments, "
	        					 + " a.reAssignStatus,a.reassignComments,a.atstoreStatus,a.atStoreCommits,a.outForDelStatus,a.outForDelComments, "
	        					 + " a.deliverdStatus,a.deliveredComments "
	        					 + " from tblonlineriderstatus a where Date(a.dteDelivered)='2019-08-29' and a.strClientCode='"+strClientCode+"'";
	       
	        ResultSet rs=st.executeQuery(sqlitemaction);
			while(rs.next())
			{
				
				riderStatus.put("name", rs.getString(1));
				riderStatus.put("current_state", rs.getString(2));
				riderStatus.put("mode", rs.getString(3));
				riderStatus.put("assign status", rs.getString(4));
				riderStatus.put("assign comments", rs.getString(5));
				riderStatus.put("unassign status", rs.getString(6));
				riderStatus.put("unassign comments", rs.getString(7));
				riderStatus.put("reassign status", rs.getString(8));
				riderStatus.put("reassign comments", rs.getString(9));
				riderStatus.put("atstore status", rs.getString(10));
				riderStatus.put("atstore comments", rs.getString(11));
				riderStatus.put("outfordelivery status ", rs.getString(12));
				riderStatus.put("outfordelivery comments", rs.getString(13));
				riderStatus.put("delivered status", rs.getString(14));
				riderStatus.put("delivered comments", rs.getString(15));
             }	
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
      return riderStatus;
	}
	
}
