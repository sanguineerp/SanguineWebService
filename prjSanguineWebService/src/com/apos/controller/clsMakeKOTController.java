package com.apos.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.service.clsMakeKOTService;

	@Controller
	@Path("/clsMakeKOTController")
	public class clsMakeKOTController {
		@Autowired
		clsMakeKOTService	objMakeKOTService;
		

		@GET
		@Path("/funInvokeWebMakeKOTWebService")
		@Produces(MediaType.APPLICATION_JSON)
		public String funCheckWSConnection()
		{
			String response = "true";
			return response;
		}
		
		
		@GET 
		@Path("/funLoadTableDtl")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funLoadTableDtl(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funLoadTableDtl(clientCode,posCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funGetWaiterList")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetWaiterList(@QueryParam("posCode")String posCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funGetWaiterList(posCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funGetButttonList")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetButttonList(@QueryParam("transName")String transName,
				@QueryParam("posCode")String posCode, 
				@QueryParam("posClientCode")String posClientCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funGetButttonList(transName,posCode,posClientCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funChekReservation")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funChekReservation(@QueryParam("strTableNo")String strTableNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funChekReservation(strTableNo);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funChekCustomerDtl")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funChekCustomerDtl(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode,@QueryParam("strTableNo")String strTableNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funChekCustomerDtl(clientCode,posCode,strTableNo);
			
			return jObjTableData;
		}
		@GET 
		@Path("/funChekCardDtl")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funChekCardDtl(@QueryParam("strTableNo")String strTableNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funChekCardDtl(strTableNo);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funChekCMSCustomerDtl")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funChekCMSCustomerDtl(@QueryParam("strTableNo")String strTableNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funChekCMSCustomerDtl(strTableNo);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funCheckMemeberBalance")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funCheckMemeberBalance(@QueryParam("strCustomerCode")String strCustomerCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funCheckMemeberBalance(strCustomerCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funChekCRMCustomerDtl")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funChekCRMCustomerDtl(@QueryParam("strMobNo")String strMobNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funChekCRMCustomerDtl(strMobNo);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funCheckCustomer")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funCheckCustomer(@QueryParam("strMobNo")String strMobNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funCheckCustomer(strMobNo);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funCheckKOTSave")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funCheckKOTSave(@QueryParam("strKOTNo")String strKOTNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funCheckKOTSave(strKOTNo);
			
			return jObjTableData;
		}
		@GET 
		@Path("/funFillOldKOTItems")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funFillOldKOTItems(@QueryParam("clientCode")String clientCode,@QueryParam("posDate")String posDate,@QueryParam("strTableNo")String strTableNo,@QueryParam("posCode")String posCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funFillOldKOTItems(clientCode,posDate,strTableNo,posCode);
			
			return jObjTableData;
		}
		

		@GET 
		@Path("/funCheckDebitCardString")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funCheckDebitCardString(@QueryParam("debitCardNo")String debitCardNo,@QueryParam("posCode")String posCode,@QueryParam("clientCode")String clientCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funCheckDebitCardString(debitCardNo,posCode,clientCode);
			
			return jObjTableData;
		}
		

		@GET 
		@Path("/funGetMenuHeads")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetMenuHeads(@QueryParam("posCode")String posCode,@QueryParam("userCode")String userCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funGetMenuHeads(posCode,userCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funPopularItem")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funPopularItem(@QueryParam("clientCode")String clientCode,@QueryParam("posDate")String posDate,@QueryParam("posCode")String posCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funPopularItem(clientCode,posDate,posCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funGetItemPricingDtl")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetItemPricingDtl(@QueryParam("clientCode")String clientCode,@QueryParam("posDate")String posDate,@QueryParam("posCode")String posCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funGetItemPricingDtl(clientCode,posDate,posCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funFillTopButtonList")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funFillTopButtonList(@QueryParam("menuHeadCode")String menuHeadCode,@QueryParam("clientCode")String clientCode,@QueryParam("posDate")String posDate,@QueryParam("posCode")String posCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funFillTopButtonList(menuHeadCode,posCode,posDate,clientCode);
			
			return jObjTableData;
		}
		

		@GET 
		@Path("/funCheckHomeDelivery")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funCheckHomeDelivery(@QueryParam("posCode")String posCode,@QueryParam("strTableNo")String strTableNo)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funCheckHomeDelivery(strTableNo,posCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funFillitemsSubMenuWise")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funFillitemsSubMenuWise(@QueryParam("strMenuCode")String strMenuCode,@QueryParam("posCode")String posCode,@QueryParam("clientCode")String clientCode,@QueryParam("posDate")String posDate,@QueryParam("flag")String flag,@QueryParam("selectedButtonCode")String selectedButtonCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funFillitemsSubMenuWise(strMenuCode,flag,selectedButtonCode,posCode,posDate,clientCode);
			
			return jObjTableData;
		}
		@GET 
		@Path("/funFillMapWithHappyHourItems")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funFillMapWithHappyHourItems(@QueryParam("clientCode")String clientCode,@QueryParam("posDate")String posDate,@QueryParam("posCode")String posCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funFillMapWithHappyHourItems( posCode,posDate,clientCode);
			
			return jObjTableData;
		}
		
		@GET 
		@Path("/funGenerateKOTNo")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGenerateKOTNo()
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objMakeKOTService.funGenerateKOTNo();
			
			return jObjTableData;
		}
		
		 @POST
		    @Path("/funSaveUpdateKOT")
		    @Consumes(MediaType.APPLICATION_JSON)
		    public Response funSaveUpdateKOT(JSONObject jObjFactoryMaster)
		    {
			
			String factoryCode = "";
			
			factoryCode = objMakeKOTService.funSaveUpdateKOT(jObjFactoryMaster);
			
			return Response.status(201).entity(factoryCode).build();
		    }

		/* @GET 
			@Path("/funFillTopSortingButtonsForModifier")
			@Produces(MediaType.APPLICATION_JSON)
			public JSONObject funFillTopSortingButtonsForModifier()
			{
				
				JSONObject jObjTableData=new JSONObject();
				
				jObjTableData=objMakeKOTService.funFillTopSortingButtonsForModifier();
				
				return jObjTableData;
			}
		 */
		/* @GET 
			@Path("/funGetModifierAll")
			@Produces(MediaType.APPLICATION_JSON)
			public JSONObject funGetModifierAll()
			{
				
				JSONObject jObjTableData=new JSONObject();
				
				jObjTableData=objMakeKOTService.funGetModifierAll();
				
				return jObjTableData;
			}*/
		 
			@GET 
			@Path("/funLoadModifiers")
			@Produces(MediaType.APPLICATION_JSON)
			public JSONObject funLoadModifiers(@QueryParam("itemCode")String itemCode)
			{
				
				JSONObject jObjTableData=new JSONObject();
				
				jObjTableData=objMakeKOTService.funGetModifierAll(itemCode);
				
				return jObjTableData;
			}
			
			@GET 
			@Path("/funFillTopModifierButtonList")
			@Produces(MediaType.APPLICATION_JSON)
			public JSONObject funFillTopModifierButtonList(@QueryParam("itemCode")String itemCode)
			{
				
				JSONObject jObjTableData=new JSONObject();
				
				jObjTableData=objMakeKOTService.funFillTopSortingButtonsForModifier(itemCode);
				
				return jObjTableData;
			}

			 @POST
			 @Path("/funCalculateTax")
			 @Produces(MediaType.APPLICATION_JSON)
			    public Response funCalculateTax(JSONObject jObj)
			    {
				 JSONObject jObjTableData=new JSONObject();
				
				
				try{
				
				
				JSONArray arrKOTItemDtlList =jObj.getJSONArray("jArr");
				String clientCode=jObj.getString("clientCode");
				String posCode=jObj.getString("posCode");
				String posDate=jObj.getString("posDate");
				jObjTableData=objMakeKOTService.funCalculateTax(arrKOTItemDtlList,clientCode,posCode,posDate);
				
				
				}
				catch (Exception e)
				{
				    e.printStackTrace();
				}
				 return Response.status(201).entity(jObjTableData).build();
			    }
			 
			 @GET 
				@Path("/funGetCustomerAddress")
				@Produces(MediaType.APPLICATION_JSON)
				public JSONObject funGetCustomerAddress(@QueryParam("strMobNo")String strMobNo)
				{
					
					JSONObject jObjTableData=new JSONObject();
					
					jObjTableData=objMakeKOTService.funGetCustomerAddress(strMobNo);
					
					return jObjTableData;
				}
				
				

				 @POST
				 @Path("/updateHomeDeliveryAddress")
				    @Consumes(MediaType.APPLICATION_JSON)
				    public Response funUpdateCustomerTempAddress(JSONObject jObj)
				    {
					
					String factoryCode = "";
					try{
					String strTempCustAddress=jObj.getString("strTempCustAddress");
					String strTempStreetName=jObj.getString("strTempStreetName");
					String strTempLandmark=jObj.getString("strTempLandmark");
					String strMobileNo=jObj.getString("strMobileNo");
					
					objMakeKOTService.funUpdateCustomerTempAddress(strTempCustAddress,strTempStreetName,strTempLandmark,strMobileNo);
					}
					catch (Exception e)
					{
					    e.printStackTrace();
					}
					return Response.status(201).entity(factoryCode).build();
				    }
}
