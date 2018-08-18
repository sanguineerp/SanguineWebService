package com.apos.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.service.clsAddKOTToBillService;
import com.apos.service.clsAreaMasterService;
import com.apos.service.clsAssignHomeDeliveryService;
import com.apos.service.clsBillSettlementService;
import com.apos.service.clsChangePasswordService;
import com.apos.service.clsCocktailWorldInterfaceService;
import com.apos.service.clsCostCenterMasterService;
import com.apos.service.clsCustomerAreaMasterService;
import com.apos.service.clsCustomerHistoryFlashService;
import com.apos.service.clsCustomerMasterService;
import com.apos.service.clsCustomerTypeMasterService;
import com.apos.service.clsDayEndConsolidateService;
import com.apos.service.clsDayEndProcessService;
import com.apos.service.clsDirectBillerService;
import com.apos.service.clsGroupMasterService;
import com.apos.service.clsImportDatabaseService;
import com.apos.service.clsItemModifierMasterService;
import com.apos.service.clsMenuHeadMasterService;
import com.apos.service.clsMenuItemMasterService;
import com.apos.service.clsModifierGroupMasterService;
import com.apos.service.clsMoveKOTItemsToTableService;
import com.apos.service.clsMoveKOTService;
import com.apos.service.clsMoveTableService;
import com.apos.service.clsNonAvailableItemsService;
import com.apos.service.clsPOSCashManagmentTranscationService;
import com.apos.service.clsPOSMasterService;
import com.apos.service.clsPOSMultiBillSettleInCashService;
import com.apos.service.clsPOSUnsettleBillTransactionService;
import com.apos.service.clsPhysicalStockService;
import com.apos.service.clsPricingMasterService;
import com.apos.service.clsReasonMasterService;
import com.apos.service.clsReprintService;
import com.apos.service.clsSendBulkSmsService;
import com.apos.service.clsShiftMasterService;
import com.apos.service.clsStockInOutService;
import com.apos.service.clsSubMenuHeadMasterService;
import com.apos.service.clsTableMasterService;
import com.apos.service.clsTaxMasterService;
import com.apos.service.clsWaiterMasterService;
import com.apos.service.clsZoneMasterService;

@Controller
@Path("/WebPOSTransactions")
public class clsTransactionsController
{
	@Autowired
	clsGroupMasterService			objGroupMasterService;
	@Autowired
	clsAreaMasterService			objAreaMasterService;
	@Autowired
	clsTableMasterService			objTableMasterService;
	@Autowired
	clsMenuHeadMasterService		objMenuHeadMasterService;
	@Autowired
	clsSubMenuHeadMasterService		objSubMenuHeadMasterService;
	@Autowired
	clsReasonMasterService			objReasonMasterService;
	@Autowired
	clsCustomerTypeMasterService	objCustomerTypeMasterService;
	@Autowired
	clsWaiterMasterService			objWaiterMasterService;
	@Autowired
	clsPOSMasterService				objPOSMasterService;

	@Autowired
	clsMenuItemMasterService		objMenuItemMasterService;
	@Autowired
	clsModifierGroupMasterService	objModifierGroupMasterService;
	@Autowired
	clsZoneMasterService			objZoneMasterService;
	@Autowired
	clsShiftMasterService			objShiftMasterService;

	@Autowired
	clsItemModifierMasterService	objItemModifierMasterService;

	@Autowired
	clsTaxMasterService				objTaxMasterService;

	@Autowired
	clsCustomerMasterService		objCustomerMasterService;

	@Autowired
	clsCustomerAreaMasterService	objCustomerAreaMasterService;

	@Autowired
	clsCostCenterMasterService		objCostCenterMasterService;
	@Autowired
	clsPricingMasterService			objPricingMasterService;

	@Autowired
	clsMoveKOTService	objMoveKOTService;

	@Autowired
	clsMoveKOTItemsToTableService	objMoveKOTItemsToTableService;
	
	@Autowired
	clsPOSCashManagmentTranscationService  objCashManagmentTranscationService;

	@Autowired
	clsPOSUnsettleBillTransactionService objUnsettleBillTransactionService;
	

	@Autowired
	clsAddKOTToBillService objAddKOTToBillService;
	
	@Autowired
	clsMoveTableService objMoveTableService;
	
	@Autowired
	clsPhysicalStockService objPhysicalStockService;
	
	@Autowired
	clsStockInOutService objStockInOutService;
	
	@Autowired
	private 	clsAssignHomeDeliveryService	objAssignHomeDeliveryService;
		
	@Autowired
	private		clsPOSMultiBillSettleInCashService	objPOSMultiBillSettleInCashService;


	@Autowired
	private		clsCustomerHistoryFlashService objCustomerHistoryFlashService;	
	
	@Autowired
	private 	clsCocktailWorldInterfaceService objCocktailWorldInterfaceService;
	
	@Autowired
	private 	clsSendBulkSmsService objSendBulkSmsService;
	
	@Autowired
	private clsReprintService objReprintService;
	
	@Autowired
	clsDayEndProcessService objPOSDayEndProcessService;
	
	@Autowired
	clsDayEndConsolidateService objDayEndConsolidateService;
	

	@Autowired
	clsImportDatabaseService objImportDatabaseServicee;
	
	@Autowired
	clsChangePasswordService objChangePasswordService;
	
	@Autowired
	clsNonAvailableItemsService objNonAvailableItemsService;
	
	@Autowired
	clsDirectBillerService objDirectBillerService;
	
	@Autowired 
	clsBillSettlementService objBillSettlementService;
	
	@GET
	@Path("/funInvokeWebPOSTransactionWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funCheckWSConnection()
	{
		String response = "true";
		return response;
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funSaveMoveKOT")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveMoveKOT(JSONObject jObj)
	{
		String itemCode = "";
		 objMoveKOTService.funSaveMoveKOT(jObj);
		 return Response.status(201).entity(itemCode).build();
	}
	
	@GET 
	@Path("/funGetBusyTableDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetBusyTableDtl(@QueryParam("loginPosCode") String posCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMoveKOTItemsToTableService.funGetBusyTableDtl(posCode);
		
		return jObjTableData;
	}
	

	@GET 
	@Path("/funGetOpenKOTsForMoveKOTItem")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetOpenKOTsForMoveKOTItem(@QueryParam("tableNo") String tableNo, @QueryParam("loginPosCode")String loginPosCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMoveKOTItemsToTableService.funGetOpenKOTDtl(tableNo,loginPosCode);
		
		return jObjTableData;
	}
	

	@GET 
	@Path("/funGetKOTItemsDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetKOTItemsDtl(@QueryParam("KOTNo") String KOTNo,@QueryParam("tableNo") String tableNo, @QueryParam("loginPosCode")String loginPosCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMoveKOTItemsToTableService.funGetKOTItemsDtl(KOTNo,tableNo,loginPosCode);
		
		return jObjTableData;
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funSaveMoveKOTItemToTable")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveMoveKOTItemToTable(JSONObject jObj)
	{
		String itemCode = "";
		objMoveKOTItemsToTableService.funSaveMoveKOT(jObj);
		 return Response.status(201).entity(itemCode).build();
	}
	
	@SuppressWarnings("rawtypes")
    @POST
    @Path("/funSavePOSCashManagmentTranscation")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSavePOSCashManagmentTranscation(JSONObject jObjCashManagementMaster)
    {
 		String cashManagementCode = "";
 		cashManagementCode = objCashManagmentTranscationService.funSavePOSCashManagmentTranscation(jObjCashManagementMaster);
	   		return Response.status(201).entity(cashManagementCode).build();
    }
	
	
    @SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveUnStettleBill")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveUnStettleBill(JSONObject jObjUnsettleBill)
    {
 		String BillNo = "";
 		BillNo = objUnsettleBillTransactionService.funSaveUnStettleBill(jObjUnsettleBill);
	   		return Response.status(201).entity(BillNo).build();
    }
    
    
    @GET 
	@Path("/funGetOpenKOTDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetOpenKOTDtl(@QueryParam("tableNo") String tableNo,@QueryParam("posCode") String posCode, @QueryParam("loginPosCode")String loginPosCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMoveKOTService.funGetOpenKOTDtl(tableNo,posCode,loginPosCode);
		
		return jObjTableData;
	}
	

	@GET 
	@Path("/funGetTableDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetTableDtl(@QueryParam("posCode") String posCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMoveKOTService.funGetTableDtl(posCode);
		
		return jObjTableData;
	}
	
	@GET 
	@Path("/funGetKOTListForAddKOTToBill")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetKOTList(@QueryParam("posCode") String posCode,@QueryParam("tableName") String tableName)
	{
		
		JSONObject jObjKOTData=new JSONObject();
		jObjKOTData=objAddKOTToBillService.funGetKOTDtlForAddKOTTOBill(posCode,tableName);
		return jObjKOTData;
	}
	
	@GET 
	@Path("/funGetUnsettleBillListForAddKOTToBill")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetUnsettleBillList(@QueryParam("posCode") String posCode)
	{
		
		JSONObject jObjKOTData=new JSONObject();
		jObjKOTData=objAddKOTToBillService.funGetUnsettleBillListForAddKOTTOBill(posCode);
		return jObjKOTData;
	}
	
	@SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveAddKOTToBill")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveAddKOTToBill(JSONObject jObjAddKOTToBill)
    {
	  String billNo = "";
	  billNo = objAddKOTToBillService.funSaveAddKOTToBill(jObjAddKOTToBill);
	  return Response.status(201).entity(billNo).build();
    }
	
	
	@GET 
	@Path("/funGetTableListForMoveTable")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetTableList(@QueryParam("posCode") String posCode,@QueryParam("tableStatus") String tableStatus)
	{
		
		JSONObject jObjKOTData=new JSONObject();
		jObjKOTData=objMoveTableService.funGetTableList(posCode, tableStatus);
		return jObjKOTData;
	}
	
	@SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveMoveTable")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveMoveTable(JSONObject jObjMoveTable)
    {
	  String result = "";
	  result = objMoveTableService.funSaveMoveTable(jObjMoveTable);
	  return Response.status(201).entity(result).build();
    }
	
	
	@GET 
	@Path("/funGetItemDetails")
    @Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funGetItemDetails(@QueryParam("ItemCode") String itemCode,@QueryParam("POSCode") String posCode)
	{
		
		JSONObject jObjStockData=new JSONObject();
		jObjStockData=objPhysicalStockService.funGetItemDetails(itemCode,posCode);
		return jObjStockData;
	}
	
	@GET 
	@Path("/funGetReasonCode")
    @Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funGetReasonCode(@QueryParam("ReasonCode") String reasonCode,@QueryParam("Type") String type)
	{
		
		JSONObject jObjReasonData=new JSONObject();
		jObjReasonData=objPhysicalStockService.funGetReasonCode(reasonCode,type);
		return jObjReasonData;
	}
	
	
	@SuppressWarnings("rawtypes")
    @POST
    @Path("/funSavePhysicalStock")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSavePhysicalStockPosting(JSONObject jObjPhyStk)
    {
	  String result = "";
	  result = objPhysicalStockService.funSavePhysicalStockPosting(jObjPhyStk);
	  return Response.status(201).entity(result).build();
    }
	
	
	@GET 
	@Path("/funGetItemForExport")
    @Consumes(MediaType.APPLICATION_JSON)
	public JSONObject funGetItemForExport()
	{
		
		JSONObject jObjStockData=new JSONObject();
		jObjStockData=objPhysicalStockService.funGetItemForExport();
		return jObjStockData;
	}
	
	@SuppressWarnings("rawtypes")
	@POST 
	@Path("/funSaveStockIn")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveStockIn(JSONObject jObjStkIn)
	{
		
		String result = "";
		result =objStockInOutService.funSaveStockIn(jObjStkIn);
		return Response.status(201).entity(result).build();
	}
	
	
	@SuppressWarnings("rawtypes")
	@POST 
	@Path("/funSaveStockOut")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveStockOut(JSONObject jObjStkOut)
	{
		
		String result = "";
		result =objStockInOutService.funSaveStockOut(jObjStkOut);
		return Response.status(201).entity(result).build();
	}
    


		@GET 
		   	@Path("/funGetOpenBillAndDeliveryBoyDtl")
		   	@Produces(MediaType.APPLICATION_JSON)
		   	public JSONObject funGetOpenBillAndDeliveryBoyDtl(@QueryParam("zoneCode") String zoneCode,@QueryParam("areaCode") String areaCode,@QueryParam("clientCode") String clientCode)
		   	{
		   		JSONObject jObjSettlementData=new JSONObject();
		   		
		   		jObjSettlementData=objAssignHomeDeliveryService.funGetOpenBillAndDeliveryBoyDtl(zoneCode,areaCode,clientCode);
		   		
		   		return jObjSettlementData;
		   	}
		
		
		@SuppressWarnings("rawtypes")
			@POST
			@Path("/funSaveDelBoyBillDtl")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response funSaveDelBoyBillDtl(JSONObject jObjAssignHomeDeliveryMaster)
			{
				String code="";
		
				  objAssignHomeDeliveryService.funSaveDelBoyBillDtl(jObjAssignHomeDeliveryMaster);
		
				 return Response.status(201).entity(code).build();
			}
			
		
		@SuppressWarnings("rawtypes")
			@POST
		 	@Path("/funSettleBills")
		 	@Produces(MediaType.APPLICATION_JSON)
		 	public Response funSettleBills(JSONObject jObjSettleBills)
		 	{
				String code="";
		 		
		 		objPOSMultiBillSettleInCashService.funSettleBills(jObjSettleBills);
		 		return Response.status(201).entity(code).build();
		 	}
		
		@GET 
		 	@Path("/funGetSettleBillDtlData1")
		 	@Produces(MediaType.APPLICATION_JSON)
		 	public JSONObject funGetSettleBillDtlData1(@QueryParam("clientCode") String clientCode,@QueryParam("posCode") String posCode,@QueryParam("posDate") String posDate)
		 	{
		 		JSONObject jObjMenuHeadData=new JSONObject();
		 		jObjMenuHeadData=objPOSMultiBillSettleInCashService.funGetSettleBillDtlData(clientCode,posCode,posDate);
		 		return jObjMenuHeadData;
		 	}
		
		@GET 
		@Path("/funFillCustomerTable")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funFillCustomerTable(@QueryParam("custTypeCode") String custTypeCode,@QueryParam("areaCode") String areaCode,@QueryParam("dobCheck") String dobCheck,@QueryParam("txtSms") String txtSms)
		{
			JSONObject jObjSettlementData=new JSONObject();
			
			jObjSettlementData=objSendBulkSmsService.funFillCustomerTable(custTypeCode,areaCode,dobCheck,txtSms);
			
			return jObjSettlementData;
		}
		
		
		@GET 
		@Path("/funSendBulkSMS")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funSendBulkSMS(@QueryParam("txtTestMobileNo") String txtTestMobileNo,@QueryParam("clientCode") String clientCode,@QueryParam("posCode") String posCode,@QueryParam("txtSms") String txtSms)
		{
		JSONObject jObjSettlementData=new JSONObject();
		
		jObjSettlementData=objSendBulkSmsService.funSendBulkSMS(txtTestMobileNo,clientCode,posCode,txtSms);
		
		return jObjSettlementData;
		}
		
		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSendSMS")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funSendSMS(JSONObject jObj )
		{
		JSONObject jObj1=new JSONObject();
		
		
		jObj1=objSendBulkSmsService.funSendSMS(jObj);
		
		return Response.status(201).entity(jObj1).build();
		}
		
		@SuppressWarnings("rawtypes")
		@POST
			@Path("/funSaveCocktailWorldInterface")
			@Produces(MediaType.APPLICATION_JSON)
			public Response funSaveCocktailWorldInterface(JSONObject jObjCWInterface)
			{
			String code="";
				
			objCocktailWorldInterfaceService.funSaveCocktailWorldInterface(jObjCWInterface);
				return Response.status(201).entity(code).build();
			}
		
		
		@GET 
		@Path("/funExecute")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funExecute(@QueryParam("posCode") String posCode,@QueryParam("oprType") String operationType,@QueryParam("kotFor") String kotFor)
		{
			JSONObject jObjSettlementData=new JSONObject();
			
			jObjSettlementData=objReprintService.funExecute(posCode,operationType,kotFor);
			
			return jObjSettlementData;
		}
		
		
		@POST
		@Path("/funDayEndProcess")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funDayEndProcess(JSONObject jObjUnsettleBill)
		{
			JSONObject jObjData = new JSONObject();
			jObjData = objPOSDayEndProcessService.funDayEndProcessGetUIData(jObjUnsettleBill);
		   		return Response.status(201).entity(jObjData).build();
		}
		
		//funDayEndProcess
		@POST
		@Path("/funDayEndConsolidate")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funDayEndConsolidate(JSONObject jObjUnsettleBill)
		{
			JSONObject jObjData = new JSONObject();
			jObjData = objDayEndConsolidateService.funDayEndConsolidateGetUIData(jObjUnsettleBill);
		   		return Response.status(201).entity(jObjData).build();
		}
		
		
		@POST
		@Path("/funImportDatabase")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funImportDatabase(JSONObject jObjUnsettleBill)
		{
			JSONObject jObjData = new JSONObject();
			try{
			boolean flag=false;
			flag = objImportDatabaseServicee.funImportDatabase(jObjUnsettleBill);
			jObjData.put("flag", flag);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
		   		return Response.status(201).entity(jObjData).build();
		}
		
		
		
		@SuppressWarnings("rawtypes")
		@POST
	   	@Path("/funFillAllTables")
	   	@Produces(MediaType.APPLICATION_JSON)
	   //	public JSONObject FunFillAllTables(@QueryParam("posCode") String posCode,@QueryParam("reportType") String reportType,@QueryParam("selectedTab") String selectedTab,@QueryParam("fromDate") String fromDate,@QueryParam("toDate") String toDate,@QueryParam("custCode") String custCode,@QueryParam("webStockUserCode") String webStockUserCode)
		public Response funFillAllTables(JSONObject jObjCustomerGistory)
		{
	   		JSONObject jObj=new JSONObject();
	   		
	   		//jObjSettlementData=objCustomerHistoryFlashService.FunFillAllTables(posCode,reportType,selectedTab,fromDate,toDate,custCode,webStockUserCode);
	   		jObj=objCustomerHistoryFlashService.funFillAllTables(jObjCustomerGistory);
	   		//return jObj;
	   		return Response.status(201).entity(jObj).build();
	   	}

		@GET 
		@Path("/funSetBillAmountAndLooseCash")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funSetBillAmountAndLooseCash(@QueryParam("billNo") String billNo)
		{
			JSONObject jObjSettlementData=new JSONObject();
			
			jObjSettlementData=objAssignHomeDeliveryService.funSetBillAmountAndLooseCash(billNo);
			
			return jObjSettlementData;
		}

		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funSaveUserPassword")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funSaveUserPassword(JSONObject jObj)
		{
			String code="";
	
			objChangePasswordService.funSaveUserPassword(jObj);
			

			 return Response.status(201).entity(code).build();
		}

@GET 
@Path("/funGetItemList")
@Produces(MediaType.APPLICATION_JSON)
public JSONObject funGetItemList(@QueryParam("searchCode") String searchCode)
{	
	JSONObject jObjItemMasterData=new JSONObject();
	try{
		jObjItemMasterData=objPhysicalStockService.funGetItemList(searchCode);
		}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return jObjItemMasterData;	
}

	@GET 
	@Path("/funGetPhysicalStkData")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetUserPhysicalStkData(@QueryParam("searchCode") String searchCode)
	{	
	JSONObject jObjItemMasterData=new JSONObject();
	try{
		jObjItemMasterData=objPhysicalStockService.funGetPhysicalStkData(searchCode);
 	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return jObjItemMasterData;	
	}
	
	@GET 
	@Path("/funGetStockInData")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetStockInData(@QueryParam("searchCode") String searchCode)
	{	
	JSONObject jObjItemMasterData=new JSONObject();
	try{
		jObjItemMasterData=objStockInOutService.funGetStockInData(searchCode);
 	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return jObjItemMasterData;	
	}
	
	@GET 
	@Path("/funGetStockOutData")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetStockOutData(@QueryParam("searchCode") String searchCode)
	{	
	JSONObject jObjItemMasterData=new JSONObject();
	try{
		jObjItemMasterData=objStockInOutService.funGetStockOutData(searchCode);
 	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return jObjItemMasterData;	
	}

	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funRemoveNonAvailableItem")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funRemoveNonAvailableItem(JSONObject jObj)
	{
		String code="";
		objNonAvailableItemsService.funRemoveNonAvailableItem(jObj);
		 return Response.status(201).entity(code).build();
		
	}
	
	
	/*Services for direct biller
*/	
	@GET 
	@Path("/funGetItemPricingDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetItemPricingDtl(@QueryParam("clientCode")String clientCode,@QueryParam("posDate")String posDate,@QueryParam("posCode")String posCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objDirectBillerService.funGetItemPricingDtl(clientCode,posDate,posCode);
		
		return jObjTableData;
	}
	
	@GET 
	@Path("/funGetCustomerHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetCustomerHistory(@QueryParam("strCustCode")String strCustCode,@QueryParam("fromDate")String fromDate,@QueryParam("toDate")String toDate)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objDirectBillerService.funGetCustomerHistory(strCustCode,fromDate,toDate);
		
		return jObjTableData;
	}
	
	/*
	 */
	 
	@GET 
	@Path("/funGetSettleButtons")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetSettleButtons(@QueryParam("posCode") String posCode,@QueryParam("userCode") String userCode,@QueryParam("clientCode")String clientCode)
	{
		JSONObject jObjSettlementData=new JSONObject();
		
		jObjSettlementData=objBillSettlementService.funGetSettleButtons(posCode,userCode,clientCode);
		
		return jObjSettlementData;
	}
	
	@GET 
	@Path("/funLoadAllReasonMasterData")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funLoadAllReasonMasterData(@QueryParam("clientCode")String clientCode)
	{
		JSONObject jObjReasonData=new JSONObject();
		
		jObjReasonData=objReasonMasterService.funLoadAllReasonMasterData(clientCode);
		
		return jObjReasonData;
	}
}
