package com.apos.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.model.clsGroupMasterModel;
import com.apos.service.clsAdvanceOrderTypeMasterService;
import com.apos.service.clsAreaMasterService;
import com.apos.service.clsCostCenterMasterService;
import com.apos.service.clsCounterMasterService;
import com.apos.service.clsCustomerAreaMasterService;
import com.apos.service.clsCustomerMasterService;
import com.apos.service.clsCustomerTypeMasterService;
import com.apos.service.clsDebitCardMasterService;
import com.apos.service.clsDebitCardSettlementDetailsService;
import com.apos.service.clsDeliveryBoyMasterService;
import com.apos.service.clsFactoryMasterService;
import com.apos.service.clsGroupMasterService;
import com.apos.service.clsItemModifierMasterService;
import com.apos.service.clsMenuHeadMasterService;
import com.apos.service.clsMenuItemMasterService;
import com.apos.service.clsModifierGroupMasterService;
import com.apos.service.clsOrderMasterService;
import com.apos.service.clsPOSBulkItemPricingMasterServices;
import com.apos.service.clsPOSMasterService;
import com.apos.service.clsPOSRegisterDebitCardService;
import com.apos.service.clsPOSUserAccessService;
import com.apos.service.clsPOSWiseItemIncentiveServices;
import com.apos.service.clsPromotionMasterService;
import com.apos.service.clsReasonMasterService;
import com.apos.service.clsRecipeMasterService;
import com.apos.service.clsSettlementMasterService;
import com.apos.service.clsShiftMasterService;
import com.apos.service.clsSubMenuHeadMasterService;
import com.apos.service.clsTDHMasterService;
import com.apos.service.clsTableMasterService;
import com.apos.service.clsTaxMasterService;
import com.apos.service.clsUserCardService;
import com.apos.service.clsWaiterMasterService;
import com.apos.service.clsZoneMasterService;
import com.apos.service.intfPricingMasterService;
import com.apos.service.intfSubGroupMasterService;
import com.webservice.util.clsUtilityFunctions;

@Controller
@Path("/APOSMastersIntegration")
public class clsMastersController
{
    
	@Autowired
	clsGroupMasterService objGroupMasterService;
	@Autowired
	clsAreaMasterService objAreaMasterService;
	@Autowired
	clsTableMasterService objTableMasterService;
	@Autowired
	clsMenuHeadMasterService objMenuHeadMasterService;
	@Autowired
	clsSubMenuHeadMasterService objSubMenuHeadMasterService;
	@Autowired
	clsReasonMasterService objReasonMasterService;
	@Autowired
	clsCustomerTypeMasterService objCustomerTypeMasterService;
	@Autowired
	clsWaiterMasterService objWaiterMasterService;
	@Autowired
	clsPOSMasterService objPOSMasterService;

	// @Autowired
	// clsMenuItemMasterService objMenuItemMasterService;
	// @Autowired
	// clsModifierGroupMasterService objModifierGroupMasterService;
	@Autowired
	clsZoneMasterService objZoneMasterService;
	@Autowired
	clsShiftMasterService objShiftMasterService;

	// @Autowired
	// clsItemModifierMasterService objItemModifierMasterService;

	@Autowired
	clsTaxMasterService objTaxMasterService;

	@Autowired
	clsCustomerMasterService objCustomerMasterService;

	@Autowired
	clsCustomerAreaMasterService objCustomerAreaMasterService;

	@Autowired
	clsCostCenterMasterService objCostCenterMasterService;

	@Autowired
	clsUtilityFunctions objclsPOSUtil;

	@Autowired
	clsSettlementMasterService objSettlementMasterService;

	@Autowired
	clsAdvanceOrderTypeMasterService objAdvOrderMasterService;

	@Autowired
	clsDeliveryBoyMasterService objDelBoyMasterService;

	@Autowired
	clsOrderMasterService objOrderMasterService;

	@Autowired
	clsRecipeMasterService objRecipeMasterService;

	@Autowired
	clsPromotionMasterService objPromotionMasterService;

	@Autowired
	clsPOSUserAccessService objUserAccessService;

	@Autowired
	clsDebitCardMasterService objDebitCardMasterService;

	@Autowired
	clsFactoryMasterService objFactoryMasterService;
	@Autowired
	clsPOSRegisterDebitCardService objPOSRegisterDebitCardService;
	@Autowired
	clsDebitCardSettlementDetailsService objDebitCardSettlementDetailsService;

	@Autowired
	clsPOSBulkItemPricingMasterServices objPOSBulkItemPricingMasterServices;

	@Autowired
	clsMenuItemMasterService objMenuItemMasterService;

	@Autowired
	clsItemModifierMasterService objItemModifierMasterService;

	@Autowired
	clsModifierGroupMasterService objModifierGroupMasterService;

	@Autowired
	clsCounterMasterService objCounterMasterService;
	
	 @Autowired
	 intfSubGroupMasterService obSubGroupMasterService;
	 
	 @Autowired
	 intfPricingMasterService objPricingMasterService;
	 
	 @Autowired
	 clsTDHMasterService objTDHMasterService;
	 
	 @Autowired
	 clsPOSWiseItemIncentiveServices  objPOSWiseItemIncentiveServices;
	 
	 @Autowired
	 clsUserCardService objUserCardService;
    
    @GET
    @Path("/funInvokeAPOSMasterWebService")
    @Produces(MediaType.APPLICATION_JSON)
    public String funCheckWSConnection()
    {
	String response = "true";
	return response;
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveGroupMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveGroupMaster(JSONObject jObjGroupMaster)
    {
	String groupCode = "";
	
	groupCode = objGroupMasterService.funSaveGroupMaster(jObjGroupMaster);
	
	return Response.status(201).entity(groupCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveAreaMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveAreaMaster(JSONObject jObjAreaMaster)
    {
	
	String areaCode = "";
	
	areaCode = objAreaMasterService.funSaveUpdateAreaMaster(jObjAreaMaster);
	
	return Response.status(201).entity(areaCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveTableMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveTableMaster(JSONObject jObjTableMaster)
    {
	
	String tableCode = "";
	
	tableCode = objTableMasterService.funAddUpdateTableMaster(jObjTableMaster);
	
	return Response.status(201).entity(tableCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveReasonMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveReasonMaster(JSONObject jObjReasonMaster)
    {	
	String reasonCode = "";
	
	reasonCode = objReasonMasterService.funSaveUpdateReasonMaster(jObjReasonMaster);
	
	return Response.status(201).entity(reasonCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSavePOSMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSavePOSMaster(JSONObject jObjPOSMaster)
    {
	
	String posCode = "";
	
	posCode = objPOSMasterService.funAddUpdatePOSMaster(jObjPOSMaster);
	
	return Response.status(201).entity(posCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveMenuHeadMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveMenuHeadMaster(JSONObject jObjMenuHead)
    {
	String MenuCode="";
	try {
		MenuCode = objMenuHeadMasterService.funSaveMenuHeadMaster(jObjMenuHead);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			
	return Response.status(201).entity(MenuCode).build();
    }
    
    
    @GET
    @Path("/funLoadMenuHeadMaster")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funLoadMenuHeadMaster(@QueryParam("masterName") String masterName, @QueryParam("clientCode") String clientCode)
    {
	JSONObject jObjSearchData = new JSONObject();
	
	try
	{
	    jObjSearchData = objMenuHeadMasterService.funLoadMasterDetails(masterName, clientCode);
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return jObjSearchData;
    }
    
    
    
    @GET 
	@Path("/funGetTableDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetTableDtl(@QueryParam("clientCode") String clientCode)
	{
		
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objTableMasterService.funGetTableDtl(clientCode);
		
		return jObjTableData;
	}
    @GET 
   	@Path("/funGetSettlementDtl")
   	@Produces(MediaType.APPLICATION_JSON)
   	public JSONObject funGetSettlementDtl(@QueryParam("clientCode") String clientCode)
   	{
   		
   		
   		JSONObject jObjSettlementData=new JSONObject();
   		
   		jObjSettlementData=objSettlementMasterService.funGetSettlementDtl(clientCode);
   		
   		return jObjSettlementData;
   	}
 
    @GET 
   	@Path("/funGetWaiterList")
   	@Produces(MediaType.APPLICATION_JSON)
   	public JSONObject funGetWaiterList(@QueryParam("clientCode") String clientCode)
   	{
   		
   		
   		JSONObject jObjData=new JSONObject();
   		
   		jObjData=objWaiterMasterService.funGetWaiterList(clientCode);
   		
   		return jObjData;
   	}
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveCustomerTypeMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveCustomerTypeMaster(JSONObject jObjCustomerTypeMaster)
    {
	String customerTypeCode = "";
	
	customerTypeCode = objCustomerTypeMasterService.funSaveCustomerTypeMaster(jObjCustomerTypeMaster);
	
	return Response.status(201).entity(customerTypeCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveWaiterMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveWaiterMaster(JSONObject jObjWaiterMaster)
    {
	String groupCode = "";
	
	groupCode = objWaiterMasterService.funAddUpdateWaiterMaster(jObjWaiterMaster);
	
	return Response.status(201).entity(groupCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveMenuItemMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveMenuItemMaster(JSONObject jObjItem)
    {
    	String ItemCode = objMenuItemMasterService.funSaveItemMaster(jObjItem);
		return Response.status(201).entity(ItemCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveModifierGroupMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveModifierGroupMaster(JSONObject jObjModGroup)
    {
    	String ModifierGroupCode = objModifierGroupMasterService.funSaveModifierGroupMaster(jObjModGroup);
		return Response.status(201).entity(ModifierGroupCode).build();
    }
    

    @SuppressWarnings("rawtypes")
       @POST
       @Path("/funSaveZoneMaster")
       @Consumes(MediaType.APPLICATION_JSON)
       public Response funSaveZoneMaster(JSONObject jObjZoneMaster)
       {
    		String zoneCode = "";
   	   		zoneCode = objZoneMasterService.funSaveZoneMaster(jObjZoneMaster);
   	   		return Response.status(201).entity(zoneCode).build();
       }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveShiftMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveShiftMaster(JSONObject jObjShiftMaster)
    {
 		String shiftCode = "";
 		shiftCode = objShiftMasterService.funSaveShiftMaster(jObjShiftMaster);
	   		return Response.status(201).entity(shiftCode).build();
    }
    
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveItemModifierMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveItemModifierMaster(JSONObject jObjModGroup)
    {
    	String ModifierCode = objItemModifierMasterService.funSaveItemModifierMaster(jObjModGroup);
		return Response.status(201).entity(ModifierCode).build();
    }
    
    @GET
	@Path("/funGetAllPOSForMaster")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetAllPOSForMaster(@QueryParam("clientCode") String clientCode)
	{
		String strAllPOS = "{}";

		strAllPOS = objPOSMasterService.funGetAllPOSForMaster(clientCode);

		return strAllPOS;
	}

	@GET
	@Path("/funGetAllMenuHeadForMaster")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetAllMenuHeadForMaster(@QueryParam("clientCode") String clientCode)
	{

		String strMenuHeads = "";
		try {
			strMenuHeads = objMenuHeadMasterService.funGetAllMenuHeadForMaster(clientCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strMenuHeads;
	}

	
	@GET
	@Path("/funGetAllAreaForMaster")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetAllAreaForMaster(@QueryParam("clientCode") String clientCode)
	{

		String strAreas = "{}";
		strAreas = objAreaMasterService.funGetAllAreaForMaster(clientCode);

		return strAreas;
	}
	
		
	@GET
	@Path("/funGetAllTaxForMaster")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetAllTaxForMaster(@QueryParam("clientCode") String clientCode)
	{

		String strMenuHeads = "{}";
		strMenuHeads = objTaxMasterService.funGetAllTaxForMaster(clientCode);

		return strMenuHeads;
	}
	
	@SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveTaxMaster")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveTaxMaster(JSONObject jObjTaxMaster)
    {
	
	String taxCode = "";
	
	taxCode = objTaxMasterService.funAddUpdateTaxMaster(jObjTaxMaster);
	
	return Response.status(201).entity(taxCode).build();
    }
	
	 @SuppressWarnings("rawtypes")
	    @POST
	    @Path("/funSaveCustomerMaster")
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Response funSaveCustomerMaster(JSONObject jObjCustomerMaster)
	    {
		String customerCode = "";
		
		customerCode = objCustomerMasterService.funSaveCustomerMaster(jObjCustomerMaster);
		
		return Response.status(201).entity(customerCode).build();
	    }


	 @SuppressWarnings("rawtypes")
	    @POST
	    @Path("/funSaveCustomerAreaMaster")
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Response funSaveCustomerAreaMaster(JSONObject jObjCustomerAreaMaster)
	    {
		String customerAreaCode = "";
		
		customerAreaCode = objCustomerAreaMasterService.funSaveCustomerAreaMaster(jObjCustomerAreaMaster);
		
		return Response.status(201).entity(customerAreaCode).build();
	    }
	    
	
		@POST
		@Path("/funGetAllRevenueHead")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funGetAllRevenueHead()
		{
			
	        JSONObject jObj=new JSONObject();
	       
	        try
	        {
	        	JSONArray objModelList = objMenuItemMasterService.funGetAllRevenueHead();
			
	        	jObj.put("RevenueHead", objModelList);
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }

	        return Response.status(201).entity(jObj).build();
		}
		
		@POST
		@Path("/funGetAllGroup")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funGetAllGroup(JSONObject jSONObj)
		{
			
	        JSONObject jObj=new JSONObject();
	        JSONArray jArryObj=new JSONArray();
	        try
	        {
	        	List objModelList = objGroupMasterService.funGetAllGroup(jSONObj.getString("strClientCode"));
			
	        	for(int i=0; i<objModelList.size();i++)
	        	{
	        		JSONObject jOb = new JSONObject();
	        		clsGroupMasterModel objModel = (clsGroupMasterModel) objModelList.get(i);
	        		
	        		jOb.put("strGroupCode", objModel.getStrGroupCode());
	        		jOb.put("strGroupName", objModel.getStrGroupName());
	        		jOb.put("strUserCreated", objModel.getStrUserCreated());
	        		jOb.put("strUserEdited", objModel.getStrUserEdited());
	        		jOb.put("dteDateCreated", objModel.getDteDateCreated());
	        		jOb.put("dteDateEdited", objModel.getDteDateEdited());
	        		jOb.put("strClientCode", objModel.getStrClientCode());
	        		jOb.put("strDataPostFlag", objModel.getStrDataPostFlag());
	        		
	        		jArryObj.put(jOb);
	        	}
	        	jObj.put("allGroupData", jArryObj);
	   
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }

	        return Response.status(201).entity(jObj).build();
		}
		
		
		

		
		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveCostCenterMaster")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveCostCenterMaster(JSONObject jObjPricingMaster)
		{
			String itemCode = "";

			itemCode = objCostCenterMasterService.funSaveUpdateCostCentersMaster(jObjPricingMaster);

			return Response.status(201).entity(itemCode).build();
		}

		
		@GET
	    @Path("/funCheckName")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funCheckName(@QueryParam("masterName") String masterName, @QueryParam("name") String name, @QueryParam("clientCode") String clientCode,@QueryParam("code")String code)
	    {
		
		JSONObject jObj = new JSONObject();
		
		try
		{
		    jObj = objclsPOSUtil.funCheckName(masterName, name, clientCode,code);
		}
		catch (Exception e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return jObj;
	    }
	
	 

		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveSettlementMaster")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveSettlementMaster(JSONObject jObjSettlementMaster)
		{
			String itemCode = "";

			itemCode = objSettlementMasterService.funSaveUpdateSettlementMaster(jObjSettlementMaster);

			return Response.status(201).entity(itemCode).build();
		}
		
	 

		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveAdvanceOrderMaster")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveAdvanceOrderMaster(JSONObject jObjAdvOrderMaster)
		{
			String itemCode = "";

			itemCode = objAdvOrderMasterService.funSaveUpdateAdvOrderMaster(jObjAdvOrderMaster);

			return Response.status(201).entity(itemCode).build();
		}
	

		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveDeliveryBoyMaster")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveDeliveryBoyMaster(JSONObject jObjAdvOrderMaster)
		{
			String itemCode = "";

			itemCode = objDelBoyMasterService.funAddUpdateDeliveryBoyMaster(jObjAdvOrderMaster);

			return Response.status(201).entity(itemCode).build();
		}
	

		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveOrderMaster")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveOrderMaster(JSONObject jObjOrderMaster)
		{
			String itemCode = "";

			itemCode = objOrderMasterService.funAddUpdatePOSOrderMaster(jObjOrderMaster);

			return Response.status(201).entity(itemCode).build();
		}
		

		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveRecipeMaster")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveRecipeMaster(JSONObject jObjRecipeMaster)
		{
			String itemCode = "";

			itemCode = objRecipeMasterService.funAddUpdateRecipeMaster(jObjRecipeMaster);

			return Response.status(201).entity(itemCode).build();
		}
		
		
	    @GET 
	   	@Path("/funGetMenuHeadDtlForPromotionMaster")
	   	@Produces(MediaType.APPLICATION_JSON)
	   	public JSONObject funGetMenuHeadDtlForPromotionMaster(@QueryParam("menuCode") String menuCode)
	   	{
	   		
	   		
	   		JSONObject jObjSettlementData=new JSONObject();
	   		
	   		try {
				jObjSettlementData=objPricingMasterService.funGetMenuheadDtlForPromotionMaster(menuCode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		
	   		return jObjSettlementData;
	   	}
	    

		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSavePromotionMaster")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSavePromotionMaster(JSONObject jObjPromotionMaster)
		{
			String itemCode = "";

			itemCode = objPromotionMasterService.funAddUpdatePromotionMaster(jObjPromotionMaster);

			return Response.status(201).entity(itemCode).build();
		}
	    
		@GET 
		@Path("/funGetTableList")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetTableList(@QueryParam("posCode") String posCode,@QueryParam("clientCode") String clientCode)
		{
			
			JSONObject jObjTableData=new JSONObject();
			
			jObjTableData=objTableMasterService.funGetTableList(posCode,clientCode);
			
			return jObjTableData;
		}
		
		
		 @SuppressWarnings("rawtypes")
		    @POST
		    @Path("/funSaveUserAccess")
		    @Consumes(MediaType.APPLICATION_JSON)
		    public Response funSaveUserAccess(JSONObject jObjUserAccess)
		    {
			
			String userCode = "";
			
			userCode = objUserAccessService.funAddUpdatePOSUserAccess(jObjUserAccess);
			
			return Response.status(201).entity(userCode).build();
		    }

		
		 @GET 
	    	@Path("/funGetMenuHeadDtlData")
	    	@Produces(MediaType.APPLICATION_JSON)
	    	public JSONObject funGetMenuHeadDtlData(@QueryParam("clientCode") String clientCode)
	    	{
	    		
	    		
	    		JSONObject jObjMenuHeadData=new JSONObject();
	    		
	    	//jObjMenuHeadData=objMenuHeadMasterService.funGetMenuHeadDtlData(clientCode);
	    		
	    		return jObjMenuHeadData;

	    	}


		 	@SuppressWarnings("rawtypes")
		    @POST
		    @Path("/funSaveDebitCardMaster")
		    @Consumes(MediaType.APPLICATION_JSON)
		    public Response funSaveDebitCardMaster(JSONObject jObjDebitCardMaster)
		    {
			
			String cardTypeCode = "";
			
			cardTypeCode = objDebitCardMasterService.funAddUpdatePOSDebitCardMaster(jObjDebitCardMaster);
			
			return Response.status(201).entity(cardTypeCode).build();
		    }


		 		@SuppressWarnings("rawtypes")
			    @POST
			    @Path("/funSaveFactoryMaster")
			    @Consumes(MediaType.APPLICATION_JSON)
			    public Response funSaveFactoryMaster(JSONObject jObjFactoryMaster)
			    {
				
				String factoryCode = "";
				
				factoryCode = objFactoryMasterService.funSaveUpdateFactoryMaster(jObjFactoryMaster);
				
				return Response.status(201).entity(factoryCode).build();
			    }

				@SuppressWarnings("rawtypes")
				@POST
				@Path("/funRegisterCard")
				@Consumes(MediaType.APPLICATION_JSON)
				public Response funRegisterCard(JSONObject jObjPricingMaster)
				{
					

			 		JSONObject JSONObject=objPOSRegisterDebitCardService.funRegisterCard(jObjPricingMaster);

					return Response.status(201).entity(JSONObject).build();
				}


				@SuppressWarnings("rawtypes")
				@POST
				@Path("/funDelistCard")
				@Consumes(MediaType.APPLICATION_JSON)
				public Response funDelistCard(JSONObject jObjPricingMaster)
				{
					

			 		JSONObject JSONObject=objPOSRegisterDebitCardService.funDelistCard(jObjPricingMaster);

					return Response.status(201).entity(JSONObject).build();
				}



			


				@GET 
			   	@Path("/funGetDebitCardSettlementDtl")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetDebitCardSettlementDtl(@QueryParam("cardTypeCode") String cardTypeCode,@QueryParam("clientCode") String clientCode)
			   	{
			   		JSONObject jObjSettlementData=new JSONObject();
			   		
			   		jObjSettlementData=objDebitCardSettlementDetailsService.funGetDebitCardSettlementDtl(cardTypeCode,clientCode);
			   		
			   		return jObjSettlementData;
			   	}


				@GET
			    @Path("/funGetAllForm")
			    @Consumes(MediaType.APPLICATION_JSON)
			    public JSONObject funGetAllFormDetails(@QueryParam("clientCode") String clientCode)
			    {
					JSONObject jObjFormDetails=new JSONObject();
			   		jObjFormDetails=objUserAccessService.funGetAllFormDetails(clientCode);
			   		return jObjFormDetails;
			    }
	 

			 	@GET 
			   	@Path("/funGetAllDeliveryBoy")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetAllDeliveryBoy(@QueryParam("clientCode") String clientCode)
			   	{
			   		JSONObject jObjDeliveryBoyData=new JSONObject();
			   		jObjDeliveryBoyData=objDelBoyMasterService.funGetAllDeliveryBoyMaster(clientCode);
			   		return jObjDeliveryBoyData;
			   	}	
		 		 
		 
			    @SuppressWarnings("rawtypes")
			    @POST
			    @Path("/funRetriveBulkItemPricingMaster")
			    @Consumes(MediaType.APPLICATION_JSON)
			    public Response funRetriveBulkItemPricingMaster(JSONObject jObjMaster)
			    {
			    	JSONObject  obj = new JSONObject ();
				   		try {
							obj = objPOSBulkItemPricingMasterServices.funRetriveBulkItemPricingMaster(jObjMaster);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				   		return Response.status(201).entity(obj).build();
			    }
			    
			    @SuppressWarnings("rawtypes")
			    @POST
			    @Path("/funUpdateBulkItemPricingMaster")
			    @Consumes(MediaType.APPLICATION_JSON)
			    public void funUpdateBulkItemPricingMaster(JSONObject jObjMaster)
			    {
			    	
				   		try {
							 objPOSBulkItemPricingMasterServices.funUpdateBulkItemPricingMaster(jObjMaster);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    }
		 
			    
			    
			    
			    
			    
			  //funLoadModifierGroupMaster
			 	@GET
			    @Path("/funLoadModifierGroupMaster")
			    @Produces(MediaType.APPLICATION_JSON)
			    public JSONObject funLoadModifierGroupMaster(@QueryParam("clientCode") String clientCode)
			    {
				JSONObject jObjItemModifier = new JSONObject();
				try
				{
					jObjItemModifier = objItemModifierMasterService.funLoadModifierGroupMaster(clientCode);
				}
				catch (Exception e)
				{
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
				return jObjItemModifier;
			    }
		 //funLoadItemPricing
			    @GET
			    @Path("/funLoadItemPricing")
			    @Produces(MediaType.APPLICATION_JSON)
			    public JSONObject funLoadItemPricing(@QueryParam("MenuCode") String menuCode)
			    {
				JSONObject jObjItemPricing = new JSONObject();
				try
				{
					jObjItemPricing = objItemModifierMasterService.funLoadItemPricing(menuCode);
				}
				catch (Exception e)
				{
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
				return jObjItemPricing;
			    }		    
		 
		 
			    
			    @GET 
			   	@Path("/funGetTaxMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedTaxMasterData(@QueryParam("taxCode") String taxCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objTaxMasterService.funSelectedTaxMasterData(taxCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
			 	
			 	@GET 
			   	@Path("/funGetPromotionMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedPromotionMasterData(@QueryParam("promoCode") String promoCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objPromotionMasterService.funSelectedPromotionMasterData(promoCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
			 	
				@GET 
			   	@Path("/funGetOrderMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedOrderMasterData(@QueryParam("orderCode") String orderCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objOrderMasterService.funSelectedOrderMasterData(orderCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
				
				@GET 
			   	@Path("/funGetPOSMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedPOSMasterData(@QueryParam("posCode") String posCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objPOSMasterService.funSelectedPOSMasterData(posCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
				
				@GET 
			   	@Path("/funGetDeliveryBoyMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funSelectedDeliveryBoyMasterData(@QueryParam("dpCode") String posCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objDelBoyMasterService.funSelectedDeliveryBoyMasterData(posCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
				
				@GET 
			   	@Path("/funGetAdvOrderMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funSelectedAdvOrderMasterData(@QueryParam("advOrderCode") String advOrderCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objAdvOrderMasterService.funSelectedAdvOrderMasterData(advOrderCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
				
				@GET 
			   	@Path("/funGetSettlementMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funSelectedSettlementMasterData(@QueryParam("settlementCode") String advOrderCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objSettlementMasterService.funSelectedSettlementMasterData(advOrderCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
				
				
				@GET 
			   	@Path("/funLoadCustomerAreaMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funLoadCustomerAreaMasterData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObData=objCustomerAreaMasterService.funLoadCustomerAreaMasterData(searchCode,clientCode);
			   		return jObData;
			   	} 
			    
			    
			    @GET 
			   	@Path("/funLoadCustomeMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funLoadCustomeMasterData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObData=objCustomerMasterService.funLoadCustomeMasterData(searchCode,clientCode);
			   		return jObData;
			   	} 
	
			    
			    @GET 
			   	@Path("/funLoadCustomerTypeMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funLoadCustomerTypeMasterData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObData=objCustomerTypeMasterService.funLoadCustomerTypeMasterData(searchCode,clientCode);
			   		return jObData;
			   	} 
	
			    @GET 
			   	@Path("/funLoadReasoneMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funLoadReasoneMasterData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObData=objReasonMasterService.funLoadReasoneMasterData(searchCode,clientCode);
			   		return jObData;
			   	} 
			    
			    @GET 
			   	@Path("/funLoadShiftMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funLoadShiftMasterData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObData=objShiftMasterService.funLoadShiftMasterData(searchCode,clientCode);
			   		return jObData;
			   	} 
			    
			    @GET 
			   	@Path("/funLoaddZoneMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funLoaddZoneMasterData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObData=objZoneMasterService.funLoaddZoneMasterData(searchCode,clientCode);
			   		return jObData;
			   	} 
			    
			    @GET
			   	@Path("/funGetAllCityForMaster")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public Response funGetAllCityForMaster(@QueryParam("clientCode") String clientCode) throws Exception
			   	{
			   		
			   		JSONObject jObj=new JSONObject();
					
					jObj = objCustomerMasterService.funGetAllCityForMaster(clientCode);

					return Response.status(201).entity(jObj).build();
			   	}
			 
			 
			 
			    @GET
					@Path("/funGetAllReasonMaster")
					@Produces(MediaType.APPLICATION_JSON)
					public Response funGetAllReasonMaster(@QueryParam("clientCode") String clientCode) throws Exception
					{
				    	 JSONObject jObj=new JSONObject();
					
						jObj = objReasonMasterService.funGetAllReasonMaster(clientCode);

						return Response.status(201).entity(jObj).build();
					}
			    
			    @GET
			   	@Path("/funGetAllStateForMaster")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public Response funGetAllStateForMaster(@QueryParam("clientCode") String clientCode) throws Exception
			   	{
			    	 JSONObject jObj=new JSONObject();

			   		jObj = objCustomerMasterService.funGetAllStateForMaster(clientCode);

			   		return Response.status(201).entity(jObj).build();
			   	}
			    

			    @GET
			   	@Path("/funGetAllCustomerType")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public Response funGetAllCustomerType(@QueryParam("clientCode") String clientCode) throws Exception
			   	{	   		
			   		JSONObject jObj=new JSONObject();
					
					jObj = objCustomerTypeMasterService.funGetAllCustomerType(clientCode);

					return Response.status(201).entity(jObj).build();
			   	}
			 
			    @GET
			   	@Path("/funGetAllCountryForMaster")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public Response funGetAllCountryForMaster(@QueryParam("clientCode") String clientCode) throws Exception
			   	{
			    	JSONObject jObj=new JSONObject();

			    	jObj = objCustomerMasterService.funGetAllCountryForMaster(clientCode);

			    	return Response.status(201).entity(jObj).build();
			   	}
			    
			    @GET 
			   	@Path("/funGetCounterMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedCounterMasterData(@QueryParam("counterCode") String counterCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjCounterMasterData=objCounterMasterService.funGetSelectedCounterMasterData(counterCode,clientCode);
			   		return jObjCounterMasterData;
			   	}
		 
		 		@GET 
			   	@Path("/funGetCostCenterMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedCostCenterMasterData(@QueryParam("costCenterCode") String costCenterCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjCostCenterMasterData=objCostCenterMasterService.funGetSelectedCostCenterMasterData(costCenterCode,clientCode);
			   		return jObjCostCenterMasterData;
			   	}
		 		
		 		@GET 
			   	@Path("/funGetFactoryMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedFactoryMasterData(@QueryParam("factoryCode") String factoryCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjFactoryMasterData=objFactoryMasterService.funGetSelectedFactoryMasterData(factoryCode,clientCode);
			   		return jObjFactoryMasterData;
			   	}
		 		
		 		@GET 
			   	@Path("/funGetDebitCardMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funGetSelectedDebitCardMasterData(@QueryParam("cardTypeCode") String cardTypeCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjFactoryMasterData=objDebitCardMasterService.funGetSelectedDebitCardMasterData(cardTypeCode,clientCode);
			   		return jObjFactoryMasterData;
			   	}
		 		
		 		
				@GET 
			   	@Path("/funGetAreaMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funSelectedAreaMasterData(@QueryParam("areaCode") String areaCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objAreaMasterService.funSelectedAreaMasterData(areaCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
			    
				@GET 
			   	@Path("/funGetWaiterMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funSelectedWaiterMasterData(@QueryParam("waiterNo") String waiterNo,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objWaiterMasterService.funSelectedWaiterMasterData(waiterNo,clientCode);
			   		return jObjTaxMasterData;
			   	}
				
				@GET 
			   	@Path("/funGetTableMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funSelectedTableMasterData(@QueryParam("tableNo") String tableNo,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objTableMasterService.funSelectedTableMasterData(tableNo,clientCode);
			   		return jObjTaxMasterData;
			   	}
		 
				
				@GET 
			   	@Path("/funGetRecipeMasterData")
			   	@Produces(MediaType.APPLICATION_JSON)
			   	public JSONObject funSelectedRecipeMasterData(@QueryParam("recipeCode") String recipeCode,@QueryParam("clientCode") String clientCode)
			   	{			   		
			 		JSONObject jObjTaxMasterData=objRecipeMasterService.funSelectedRecipeMasterData(recipeCode,clientCode);
			   		return jObjTaxMasterData;
			   	}
				
				
				
				 @GET 
				   	@Path("/funGetMenuHeadMasterData")
				   	@Produces(MediaType.APPLICATION_JSON)
				   	public JSONObject funGetMenuHeadMasterData(@QueryParam("menuHeadCode") String menuHeadCode,@QueryParam("clientCode") String clientCode)
				   	{	
				    	JSONObject jObjMenuHeadMasterData=new JSONObject();
				    	try{
				    		 jObjMenuHeadMasterData=objMenuHeadMasterService.funGetMenuHeadMasterData(menuHeadCode,clientCode);
					   		
				    	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    	}
				    	return jObjMenuHeadMasterData;	
				   	}
				    //funGetSubMenuHeadMasterData  subMenuHeadCode
				    @GET 
				   	@Path("/funGetSubMenuHeadMasterData")
				   	@Produces(MediaType.APPLICATION_JSON)
				   	public JSONObject funGetSubMenuHeadMasterData(@QueryParam("subMenuHeadCode") String subMenuHeadCode,@QueryParam("clientCode") String clientCode)
				   	{	
				    	JSONObject jObjSubMenuHeadMasterData=new JSONObject();
				    	try{
				    		 jObjSubMenuHeadMasterData=objMenuHeadMasterService.funGetSubMenuHeadMasterData(subMenuHeadCode,clientCode);
					 	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    	}
				    	return jObjSubMenuHeadMasterData;	
				   	}
				    
				    @GET 
				   	@Path("/funGetItemModifierMasterData")
				   	@Produces(MediaType.APPLICATION_JSON)
				   	public JSONObject funGetItemModifierMasterData(@QueryParam("modCode") String modCode,@QueryParam("clientCode") String clientCode)
				   	{	
				    	JSONObject jObjModMasterData=new JSONObject();
				    	try{
				    		jObjModMasterData=objItemModifierMasterService.funGetItemModifierMasterData(modCode,clientCode);
					 	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    	}
				    	return jObjModMasterData;	
				   	}
				    
				    @GET 
				   	@Path("/funGetModifierMasterData")
				   	@Produces(MediaType.APPLICATION_JSON)
				   	public JSONObject funGetModifierGroupMasterData(@QueryParam("modGroupCode") String modGroupCode,@QueryParam("clientCode") String clientCode)
				   	{	
				    	JSONObject jObjModMasterData=new JSONObject();
				    	try{
				    		jObjModMasterData=objModifierGroupMasterService.funGetModifierGroupMasterData(modGroupCode,clientCode);
					 	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    	}
				    	return jObjModMasterData;	
				   	}
				    //  
				    @GET 
				   	@Path("/funGetMenuItemMasterData")
				   	@Produces(MediaType.APPLICATION_JSON)
				   	public JSONObject funGetMenuItemMasterData(@QueryParam("itemCode") String itemCode,@QueryParam("clientCode") String clientCode)
				   	{	
				    	JSONObject jObjItemMasterData=new JSONObject();
				    	try{
				    		jObjItemMasterData=objMenuItemMasterService.funGetMenuItemMasterData(itemCode,clientCode);
					 	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    	}
				    	return jObjItemMasterData;	
				   	}
				    
				    //  
				    @GET 
				   	@Path("/funGetSubGroupMasterData")
				   	@Produces(MediaType.APPLICATION_JSON)
				   	public JSONObject funGetSubGroupMasterData(@QueryParam("subgroupCode") String subgroupCode,@QueryParam("clientCode") String clientCode)
				   	{	
				    	JSONObject jObjItemMasterData=new JSONObject();
				    	try{
				    		jObjItemMasterData=obSubGroupMasterService.funGetSubGroupMasterData(subgroupCode,clientCode);
					 	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    	}
				    	return jObjItemMasterData;	
				   	}
				    @SuppressWarnings("rawtypes")
					@POST
					@Path("/funSaveSubGroupMaster")
					@Consumes(MediaType.APPLICATION_JSON)
					public Response funSaveSubGroupMaster(JSONObject jObjSubGroupMaster)
					{
						String subgroupCode = "";
						try{
							subgroupCode = obSubGroupMasterService.funSaveSubGroupMaster(jObjSubGroupMaster);	
						}
						catch(Exception e ){
							e.printStackTrace();
						}
						
						return Response.status(201).entity(subgroupCode).build();
					}
				    
				 	@GET
				    @Path("/funGetAllSubGroup")
				    @Produces(MediaType.APPLICATION_JSON)
				    public JSONObject funGetAllSubGroup(@QueryParam("clientCode") String clientCode)
				    {
					JSONObject jObjSubGroup = new JSONObject();
					try
					{
						jObjSubGroup = obSubGroupMasterService.funGetAllSubGroupMaster(clientCode);
					}
					catch (Exception e)
					{
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					}
					return jObjSubGroup;
				    }
				 	
				 	 		
			 		//Update
					@SuppressWarnings("rawtypes")
					@POST
					@Path("/funSaveUpdatePricingMaster")
					@Consumes(MediaType.APPLICATION_JSON)
					public Response funSaveUpdatePricingMaster(JSONObject jObjPricingMaster)
					{
						String itemCode = "";
						try{
							itemCode = objPricingMasterService.funSaveUpdatePricingMaster(jObjPricingMaster);	
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						

						return Response.status(201).entity(itemCode).build();
					}

			//update
			@SuppressWarnings("rawtypes")
					@POST
					@Path("/funCheckDuplicateItemPricing")
					@Consumes(MediaType.APPLICATION_JSON)
					public Response funCheckDuplicateItemPricing(JSONObject jObjPricingMaster)
					{
						String isDuplicate = "false";
						try{
							isDuplicate = objPricingMasterService.funCheckDuplicateItemPricing(jObjPricingMaster);	
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						

						return Response.status(201).entity(isDuplicate).build();
					}



						@GET
					 		@Path("/funGetMenuItemPricingMaster")
					 		@Produces(MediaType.APPLICATION_JSON)
					 		public JSONObject funGetMenuItemPricingMaster(@QueryParam("pricingId") String pricingId,@QueryParam("clientCode") String clientCode)
					 		{
					 			JSONObject jObjItemPricing=new JSONObject();
						    	try{
						    		jObjItemPricing=objPricingMasterService.funGetMenuItemPricingMaster(pricingId,clientCode);
					 			}catch(Exception e){
					 				e.printStackTrace();
						    	}
					 			return jObjItemPricing;
					 		}		
			 		
			 		
						@GET
						@Path("/funGetAllSubMenuHeadForMaster")
						@Produces(MediaType.APPLICATION_JSON)
						public String funGetAllSubMenuHeadForMaster(@QueryParam("clientCode") String clientCode)
						{
							String strSubMenuHeads = "";
							try{
								strSubMenuHeads = objSubMenuHeadMasterService.funGetAllSubMenuHeadForMaster(clientCode);	
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							

							return strSubMenuHeads;
						}


					  @SuppressWarnings("rawtypes")
					    @POST
					    @Path("/funSaveSubMenuHead")
					    @Consumes(MediaType.APPLICATION_JSON)
					    public Response funSaveSubMenuHead(JSONObject jObjSubMenuHead)
					    {
					    	String SubMenuCode="";
					    	try{
					    		SubMenuCode = objSubMenuHeadMasterService.funSaveSumMenuMaster(jObjSubMenuHead);
					    	}catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    	return Response.status(201).entity(SubMenuCode).build();
					    }
				
					  @GET 
					   	@Path("/funGetGroupMasterDtl")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funGetGroupMasterDtl(@QueryParam("groupCode") String groupCode,@QueryParam("clientCode") String clientCode)
					   	{	
					    	JSONObject jObjGroupData=new JSONObject();
					    	try{
					    		jObjGroupData=objGroupMasterService.funGetGroupMasterDtl(groupCode,clientCode);
						 	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    	return jObjGroupData;	
					   	}
					  
					  
					    @SuppressWarnings("rawtypes")
					    @POST
					    @Path("/funSaveCounterMaster")
					    @Consumes(MediaType.APPLICATION_JSON)
					    public Response funSaveCounterMaster(JSONObject jObjAreaMaster)
					    {
						
						String counterCode = "";
						
						counterCode = objCounterMasterService.funSaveUpdateCounterMaster(jObjAreaMaster);
						
						return Response.status(201).entity(counterCode).build();
					    }
					  
					  
					    @SuppressWarnings("rawtypes")
					    @POST
					    @Path("/funSaveTableSequence")
					    @Consumes(MediaType.APPLICATION_JSON)
					    public Response funSaveTableSequence(JSONObject jObjTableMaster)
					    {
						
						String tableCode = "";
						
						tableCode = objTableMasterService.funSaveTableSequence(jObjTableMaster);
						
						return Response.status(201).entity(tableCode).build();
					    }
					    
					    @GET 
					   	@Path("/funLoadTDHMasterData")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funLoadTDHMasterData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
					   	{			   		
					 		JSONObject jObData=objTDHMasterService.funLoadTDHMasterData(searchCode,clientCode);
					   		return jObData;
					   	} 
					    
					    @GET 
					   	@Path("/funLoadPOSTDHTableData")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funLoadPOSTDHTableData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
					   	{			   		
					 		JSONObject jObData=objTDHMasterService.funLoadPOSTDHTableData(searchCode,clientCode);
					   		return jObData;
					   	} 
					    
					    @GET 
					   	@Path("/funloadPOSTDHOnItemData")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funloadPOSTDHOnItemData(@QueryParam("searchCode") String searchCode,@QueryParam("clientCode") String clientCode)
					   	{			   		
					 		JSONObject jObData=objTDHMasterService.funLoadItemList(searchCode,clientCode);
					   		return jObData;
					   	} 
					    
					    @GET 
					   	@Path("/funloadPOSAllItemName")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funloadPOSAllItemName(@QueryParam("clientCode") String clientCode)
					   	{			   		
					 		JSONObject jObData=objTDHMasterService.funloadPOSAllItemName(clientCode);
					   		return jObData;
					   	} 
					    
					  
					  
					  
					    @SuppressWarnings("rawtypes")
					    @POST
					    @Path("/funSaveTDH")
					    @Consumes(MediaType.APPLICATION_JSON)
					    public Response funSaveTDH(JSONObject jObjTDH) throws JSONException
					    {
					 		String strTDHCode = "";
					 		strTDHCode = objTDHMasterService.funSaveTDH(jObjTDH);
						   		return Response.status(201).entity(strTDHCode).build();
					    }
					  
					  
					  
					  
						@SuppressWarnings("rawtypes")
						@POST
						@Path("/funSavePOSWiseItemIncentive")
						@Consumes(MediaType.APPLICATION_JSON)
						public Response funSavePOSWiseItemIncentive(JSONObject jObjPromotionMaster)
						{
							String itemCode = "";

							itemCode = objPOSWiseItemIncentiveServices.funSavePOSWiseItemIncentive(jObjPromotionMaster);

							return Response.status(201).entity(itemCode).build();
						}
					  
					  
						@GET
					 	@Path("/funGetAllCustomerAreaForMaster")
					 	@Produces(MediaType.APPLICATION_JSON)
					 	public String funGetAllCustomerAreaForMaster(@QueryParam("clientCode") String clientCode)
					 	{
					 		String strAllZones = "{}";

					 		strAllZones = objCustomerAreaMasterService.funGetAllCustomerAreaForMaster(clientCode);

					 		return strAllZones;
					 	}

						@GET
					 	@Path("/funGetAllZoneForMaster")
					 	@Produces(MediaType.APPLICATION_JSON)
					 	public String funGetAllZoneForMaster(@QueryParam("clientCode") String clientCode)
					 	{
					 		String strAllZones = "{}";

					 		strAllZones = objZoneMasterService.funGetAllZoneForMaster(clientCode);

					 		return strAllZones;
					 	}

						@GET
						@Path("/funGetPOSName")
						@Produces(MediaType.APPLICATION_JSON)
						public String funGetPOSName(@QueryParam("posCode") String posCode)
						{
							String strAllPOS = "{}";

							strAllPOS = objPOSMasterService.funGetPOSName(posCode);

							return strAllPOS;
						}

	    				@GET
						   	@Path("/funPrintVatNoPOS")
						   	@Produces(MediaType.APPLICATION_JSON)
						    public String funPrintVatNoPOS(@QueryParam("posCode") String posCode)
						    {
							 String jObjPrintVatNo="";	
							 jObjPrintVatNo = objPOSMasterService.funGetPrintVatNoPOS(posCode);
						   	return jObjPrintVatNo;
						    }
				
						 @GET
						   	@Path("/funVatNoPOS")
						   	@Produces(MediaType.APPLICATION_JSON)
						    public String funVatNoPOS(@QueryParam("posCode") String posCode)
						    {
						    	String strVatNo="";
						    	strVatNo = objPOSMasterService.funGetVatNoPOS(posCode);
						    	return strVatNo;
						    }

							@GET
						   	@Path("/funPrintServiceTaxNo")
						   	@Produces(MediaType.APPLICATION_JSON)
						    public String funPrintServiceTaxNo(@QueryParam("posCode") String posCode)
						    {
								String strPrintServiceTaxNo="";
								strPrintServiceTaxNo =objPOSMasterService.funGetPrintServiceTaxNoPOS(posCode);
								return strPrintServiceTaxNo;
						    }

					 		@GET
						   	@Path("/funServiceTaxNoPOS")
						   	@Produces(MediaType.APPLICATION_JSON)
						    public String funServiceTaxNoPOS(@QueryParam("posCode") String posCode)
						    {
					 			String strServiceTaxNo="";
					 			strServiceTaxNo = objPOSMasterService.funGetServiceTaxNoPOS(posCode);
					 			return strServiceTaxNo;
						    }	

						@GET
						@Path("/funFillCustTypeCombo")
						@Produces(MediaType.APPLICATION_JSON)
						public String funFillCustTypeCombo(@QueryParam("posCode") String posCode)
						{
							String strAllPOS = "{}";

							strAllPOS = objCustomerTypeMasterService.funFillCustTypeCombo(posCode);

							return strAllPOS;
						}		
					  
						  
						@GET
						@Path("/funFillItemTable")
						@Produces(MediaType.APPLICATION_JSON)
						public JSONObject funFillItemTable(@QueryParam("clientCode") String clientCode)
						{
							JSONObject jObj = new JSONObject();

							//jObj = objMenuItemMasterService.funFillItemTable(clientCode);

							return jObj;
						}
					  

						
						@GET 
					   	@Path("/funGetUserAccessData")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funGetUserAccessData(@QueryParam("searchCode") String userCode)
					   	{	
					    	JSONObject jObjItemMasterData=new JSONObject();
					    	try{
					    		jObjItemMasterData=objUserAccessService.funGetUserAccessData(userCode);
						 	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    	return jObjItemMasterData;	
					   	}

					    @GET 
					   	@Path("/funGetUserCardData")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funGetUserCardData(@QueryParam("searchCode") String subgroupCode,@QueryParam("clientCode") String clientCode)
					   	{	
					    	JSONObject jObjItemMasterData=new JSONObject();
					    	try{
					    		jObjItemMasterData=objUserCardService.funGetUserCardData(subgroupCode,clientCode);
						 	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    	return jObjItemMasterData;	
					   	}		  
					  
					  

				 		@GET
						@Path("/funGetAllDebitCardForMaster")
						@Produces(MediaType.APPLICATION_JSON)
						public String funGetAllDebitCardMaster(@QueryParam("clientCode") String clientCode)
						{

							String strCard = "{}";
							strCard= objDebitCardMasterService.funGetAllPOSForMaster(clientCode);

							return strCard;
						}
				
					  

					    @GET 
					   	@Path("/funCheckDuplicateBuyPromoItem")
					   	@Produces(MediaType.APPLICATION_JSON)
					   	public JSONObject funCheckDuplicateBuyPromoItem(@QueryParam("promoItemCode") String promoItemCode,@QueryParam("strPromoCode") String strPromoCode,@QueryParam("areaCode") String areaCode,@QueryParam("posCode") String posCode)
					   	{	
					    	JSONObject jObjItemMasterData=new JSONObject();
					    	try{
					    		jObjItemMasterData=objPromotionMasterService.funCheckDuplicateBuyPromoItem(promoItemCode, strPromoCode, posCode, areaCode);
						 	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    	return jObjItemMasterData;	
					   	}			  
					  
					    @GET
						@Path("/funGetAllCostCentersForMaster")
						@Produces(MediaType.APPLICATION_JSON)
						public String funGetAllCostCentersForMaster(@QueryParam("clientCode") String clientCode)
						{

							String strAreas = "{}";
							strAreas = objCostCenterMasterService.funGetAllCostCentersForMaster(clientCode);

							return strAreas;
						}
					  
					  
					  
		
				 		
				 		
		
}
