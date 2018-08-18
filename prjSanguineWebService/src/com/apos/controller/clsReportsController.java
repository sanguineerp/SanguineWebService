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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.service.clsPOSReportService;

@Controller
@Path("/WebPOSReport")
public class clsReportsController {
	
	
	 @Autowired
	 clsPOSReportService objPOSReportService;
	
	
	
	 @POST
	 @Path("/funGroupWiseSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funGroupWiseSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funGroupWiseReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), jObjfillter.get("sgCode").toString(),
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	

	 @POST
	 @Path("/funItemWiseSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funItemWiseSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funItemWiseReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	 
	 
	 @POST
	 @Path("/funTaxWiseSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funTaxWiseSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funTaxWiseReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(),
						 jObjfillter.get("strShiftNo").toString(),
						 jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
//	 @POST
//	 @Path("/funDiscountWiseSalesReport")
//	 @Produces(MediaType.APPLICATION_JSON)
//	public Response funDiscountWiseSalesReport(JSONObject jObjfillter)
//	{
//		 JSONObject jObjRptData = new JSONObject();
//		 List listRet=null;;
//		try {
//			jObjRptData = objPOSReportService.funDiscountWiseReportDtl(jObjfillter.get("strFromdate").toString(),
//						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
//						jObjfillter.get("strShiftNo").toString(), 
//						jObjfillter.get("strViewType").toString(), 
//						 jObjfillter.get("userCode").toString());
//		 //jObjRptData.put("listRptData", listRet);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//		 return Response.status(201).entity(jObjRptData).build();
//	}

//	 @POST
//	 @Path("/funComplimentarySettlementReport")
//	 @Produces(MediaType.APPLICATION_JSON)
//	public Response funComplimentarySettlementReport(JSONObject jObjfillter)
//	{
//		 JSONObject jObjRptData = new JSONObject();
//		 List listRet=null;;
//		try {
//			jObjRptData = objPOSReportService.funComplimentarySettlementReportDtl(jObjfillter.get("strFromdate").toString(),
//						jObjfillter.get("strToDate").toString(),
//						jObjfillter.get("posCode").toString(),
//						jObjfillter.get("strShiftNo").toString(),
//						jObjfillter.get("strReasonCode").toString(),
//						jObjfillter.get("strViewType").toString(), 
//						jObjfillter.get("userCode").toString());
//		 //jObjRptData.put("listRptData", listRet);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block 
//			e.printStackTrace();
//		}
//		 
//		 return Response.status(201).entity(jObjRptData).build();
//	}
	 
	 
	
	 
	// funSettlementWiseSales
	 @POST
	 @Path("/funSettlementWiseSales")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funSettlementWiseSales(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funSettlementWiseSales(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	 //funAuditorReport
	 @POST
	 @Path("/funAuditorReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funAuditorReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funAuditorReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 //funSubGroupWiseSummaryReport

	 @POST
	 @Path("/funSubGroupWiseSummaryReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funSubGroupWiseSummaryReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funSubGroupWiseSummaryReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	 //funNCKotReport
	 @POST
	 @Path("/funNCKotReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funNCKotReoprt(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funNCKotReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("strReasonCode").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	

		
		
	 @POST
	 @Path("/funSubGroupWiseSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funSubGroupWiseSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funSubGroupWiseReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	

	 @POST
	 @Path("/funRevenueHeadWiseItemSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funRevenueHeadWiseItemSalesReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			 List listRet=null;;
			try {
				jObjRptData = objPOSReportService.funRevenueHeadWiseItemSalesReportDtl(jObjfillter.get("strFromdate").toString(),
							jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
							jObjfillter.get("strShiftNo").toString(),jObjfillter.get("revenueHead").toString(),jObjfillter.get("reportType").toString(), jObjfillter.get("userCode").toString());
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 return Response.status(201).entity(jObjRptData).build();
		}
	 @POST
	 @Path("/funOperatorWiseReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funOperatorWiseReportDtl(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funOperatorWiseReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("settlementType").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 @POST
	 @Path("/funVoidBillReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funVoidBillReportDtl(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funVoidBillReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("reportType").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 @POST
	 @Path("/funCostCenterWiseSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funCostCenterWiseSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funCostCenterWiseSalesReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("clientCode").toString(),jObjfillter.get("reportType").toString(),jObjfillter.get("CostCenterCode").toString(),jObjfillter.get("LogedInPOSCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	 @POST
	 @Path("/funGetAllPhysicalStockFlash")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funGetAllPhysicalStockFlash(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funGetAllPhysicalStockFlash(jObjfillter.get("fromDate").toString(),
						jObjfillter.get("toDate").toString(), jObjfillter.get("posCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 @POST
	 @Path("/funGetPhysicalStockFlash")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funGetPhysicalStockFlash(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funGetPhysicalStockFlash(jObjfillter.get("pspCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 @POST
	 @Path("/funGetAuditFlash")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funGetAuditFlash(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funGetAuditFlash(jObjfillter.get("fromDate").toString(),
						jObjfillter.get("toDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("userCode").toString(), jObjfillter.get("strReportType").toString(),jObjfillter.get("reasonCode").toString(),jObjfillter.get("auditType").toString(),jObjfillter.get("clientCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 @POST
	 @Path("/funGetAdvanceOrderFlash")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funGetAdvanceOrderFlash(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funGetAdvanceOrderFlash(jObjfillter.get("fromDate").toString(),
						jObjfillter.get("toDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strDateFilter").toString(), jObjfillter.get("strReportType").toString(),jObjfillter.get("strCustomerCode").toString(),jObjfillter.get("operationType").toString(), 
						jObjfillter.get("advOrderCode").toString(),jObjfillter.get("strStatus").toString(),jObjfillter.get("userCode").toString(),jObjfillter.get("clientCode").toString(),jObjfillter.get("LogedInPOSCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 
	 @POST
	 @Path("/funSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		
		try {
			
			jObjRptData = objPOSReportService.funSalesReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),
						jObjfillter.get("field").toString(),
						jObjfillter.get("strPayMode").toString(),jObjfillter.get("strOperator").toString(),
						jObjfillter.get("strFromBill").toString(),jObjfillter.get("strToBill").toString(),
						jObjfillter.get("reportType").toString(),jObjfillter.get("Type").toString(),
						jObjfillter.get("Customer").toString(),jObjfillter.get("ConsolidatePOS").toString(),
						jObjfillter.get("ReportName").toString(),jObjfillter.get("LoginPOSCode").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	 //funPromotionFlash
	 
	 @POST
	 @Path("/funPromotionFlash")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funPromotionFlash(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 
		try {
			jObjRptData = objPOSReportService.funPromotionFlash(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("PromoCode").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	//funCounterWiseSalesReport
	 
	 @POST
	 @Path("/funCounterWiseSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funCounterWiseSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		
		try {
			jObjRptData = objPOSReportService.funCounterWiseSalesReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("reportType").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	 //funTaxBreakupSummaryReport
	 @POST
	 @Path("/funTaxBreakupSummaryReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funTaxBreakupSummaryReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		
		try {
			jObjRptData = objPOSReportService.funTaxBreakupSummaryReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	 //funWaiterWiseIncentiveReport
	 @POST
	 @Path("/funWaiterWiseIncentiveReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funWaiterWiseIncentiveReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 
		try {
			jObjRptData = objPOSReportService.funWaiterWiseIncentiveReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("reportType").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	 //funDeliveryBoyWiseIncentiveReport
	 @POST
	 @Path("/funDeliveryBoyWiseIncentiveReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funDeliveryBoyWiseIncentiveReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		
		try {
			jObjRptData = objPOSReportService.funDeliveryBoyWiseIncentiveReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("reportType").toString(),jObjfillter.get("DBCode").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	 //funAdvanceOrderReport
	 @POST
	 @Path("/funAdvanceOrderReport")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funAdvanceOrderReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		try {
			jObjRptData = objPOSReportService.funAdvanceOrderReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("ordereType").toString()); 
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Response.status(201).entity(jObjRptData).build();
	}
	 //funItemMasterListingReport
	// @POST
	// @Path("/funItemMasterListingReport")
	// @Produces(MediaType.APPLICATION_JSON)
	//public Response funItemMasterListingReport(JSONObject jObjfillter)
	//{
	//	 JSONObject jObjRptData = new JSONObject();
	//	
	//	try {
	//		jObjRptData = objPOSReportService.funItemMasterListingReport(jObjfillter.get("strFromdate").toString(),
	//					jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
	//					jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
	//	 //jObjRptData.put("listRptData", listRet);
	//	} catch (JSONException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	//	 return Response.status(201).entity(jObjRptData).build();
	//}
	 
	 
	 @POST
	 @Path("/funDayEndFlashReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funDayEndFlashReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;
		try {
			jObjRptData = objPOSReportService.funDayEndFlashReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	@POST
	 @Path("/funAIPBReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funAIPBReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funAIPBReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), 
						jObjfillter.get("posCode").toString(),
						 jObjfillter.get("strShiftNo").toString());
						
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 @POST
	 @Path("/funATVReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funATVReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData =objPOSReportService.funATVReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), 
						jObjfillter.get("posCode").toString(),
						 jObjfillter.get("strShiftNo").toString());
			
						
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 @POST
	 @Path("/funAPCReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funAPCReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData =objPOSReportService.funAPCReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), 
						jObjfillter.get("posCode").toString(),
						 jObjfillter.get("strShiftNo").toString(),
						 jObjfillter.get("strPowWise").toString(),
						 jObjfillter.get("strDateWise").toString());
						
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	@POST
	 @Path("/funDebitCardFlashReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funDebitCardFlashReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;
		try {
			jObjRptData = objPOSReportService.funDebitCardFlashReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(),jObjfillter.get("auditType").toString(),
						 jObjfillter.get("strShiftNo").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	@POST
	 @Path("/funDiscountWiseSalesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funDiscountWiseSalesReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funDiscountWiseReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), 
						jObjfillter.get("strViewType").toString(), 
						 jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	 @POST
	 @Path("/funComplimentarySettlementReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funComplimentarySettlementReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funComplimentarySettlementReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(),
						jObjfillter.get("posCode").toString(),
						jObjfillter.get("strShiftNo").toString(),
						jObjfillter.get("strReasonCode").toString(),
						jObjfillter.get("strViewType").toString(), 
						jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	  @POST
		 @Path("/funGetPOSWiseSalesReport")
		 @Produces(MediaType.APPLICATION_JSON)
		public Response funGetPOSWiseSalesReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			 List listRet=null;;
			try {
				jObjRptData = objPOSReportService.funGetPOSWiseSalesReportDtl(jObjfillter.get("fromDate").toString(),
							jObjfillter.get("toDate").toString(), 
			
							jObjfillter.get("strViewType").toString());
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
			 
			 return Response.status(201).entity(jObjRptData).build();
		}
	
	
	
	
	
	@POST
	 @Path("/funVoidAdvanceOrderReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funVoidAdvanceOrderReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;
		try {
			jObjRptData = objPOSReportService.funVoidAdvanceOrderReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), 
						jObjfillter.get("posCode").toString(),
						 jObjfillter.get("strShiftNo").toString(),
						 jObjfillter.get("strOrderType").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 @POST
	 @Path("/funWaiterWiseItemWiseIncentivesReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funWaiterWiseItemWiseIncentivesrReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funWaiterWiseItemWiseIncentivesReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), 
						jObjfillter.get("posCode").toString(),
						 jObjfillter.get("strShiftNo").toString());
						
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	@POST
	 @Path("/funItemMasterListingReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funItemMasterListingReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funItemMasterListingReportDtl(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), 
						jObjfillter.get("posCode").toString(),
						 jObjfillter.get("strShiftNo").toString());
						
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	@POST
	@Path("/funVoidKOTReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funVoidKOTReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funVoidKOTReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	
	@POST
	@Path("/funPOSUnusedCardBalanceReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funPOSUnusedCardBalanceReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funPOSUnusedCardBalanceReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	
	@POST
	@Path("/funPOSItemWiseConsumptionReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funPOSItemWiseConsumptionReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funPOSItemWiseConsumptionReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("strClientCode").toString(),jObjfillter.get("loginPosCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	@POST
	@Path("/funPostingReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funPostingReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funPostingReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	@POST
	@Path("/funReprintDocsReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funReprintDocsReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funReprintDocsReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strUser").toString(), jObjfillter.get("strDocNo").toString(), jObjfillter.get("strType").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	@POST
	@Path("/funTableWisePaxReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funTableWisePaxReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funTableWisePaxReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("strClientCode").toString(),jObjfillter.get("loginPosCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	@POST
	@Path("/funSalesSummaryFlash")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funSalesSummaryFlash(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funSalesSummaryFlash(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("payMode").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("reportType").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	
	
	@POST
	@Path("/funBillWiseReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funBillWiseReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funBillWiseReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	
	@POST
	@Path("/funDailyCollectionReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funDailyCollectionReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funDailyCollectionReport(jObjfillter.get("strFromdate").toString(),
						jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
						jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	@POST
	@Path("/funGetDayWiseSalesSummary1")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetDayWiseSalesSummary1(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funGetDayWiseSalesSummary(jObjfillter.get("withDiscount").toString(),jObjfillter.get("fromDate").toString(),
						jObjfillter.get("toDate").toString(), jObjfillter.get("operationType").toString(),jObjfillter.get("settlementName").toString(), jObjfillter.get("posCode").toString(),jObjfillter.get("posName").toString());
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	@POST
	@Path("/funGetDayWiseSalesSummary2")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetDayWiseSalesSummary2(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objPOSReportService.funGetDayWiseSalesSummary2(jObjfillter.get("withDiscount").toString(),jObjfillter.get("fromDate").toString(),
						jObjfillter.get("toDate").toString(), jObjfillter.get("operationType").toString(),jObjfillter.get("settlementName").toString(), jObjfillter.get("posCode").toString(),jObjfillter.get("posName").toString());
		
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	
	
	
	@POST
	@Path("/funGetPOSDashboardSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetPOSDashboardSalesReportDtl(JSONObject jObjfillter)
	{
		JSONObject jObjRptData = new JSONObject();
		List listRet=null;;
		try 
		{
			jObjRptData = objPOSReportService.funGetPOSDashboardSalesReportDtl(jObjfillter.get("fromDate").toString(),
						jObjfillter.get("toDate").toString(),jObjfillter.get("strReportType").toString(),jObjfillter.get("POSCode").toString());
		 //jObjRptData.put("listRptData", listRet);
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(201).entity(jObjRptData).build();
	}
	
	
	@POST
	@Path("/funGetSalePurchaseComparisonDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetSalePurchaseComparisonDtl(JSONObject jObjfillter)
	{
		JSONObject jObjRptData = new JSONObject();
		List listRet=null;;
		try 
		{
			jObjRptData = objPOSReportService.funGetSalePurchaseComparisonDtl(jObjfillter.get("fromDate").toString(),jObjfillter.get("toDate").toString());
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(201).entity(jObjRptData).build();
	}
	
	
	
	@POST
	@Path("/funGetComparisonwiseDashboardDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetComparisonwiseDashboardDtl(JSONObject jObjfillter)
	{
		JSONObject jObjRptData = new JSONObject();
		List listRet=null;;
		try 
		{
			jObjRptData = objPOSReportService.funGetComparisonwiseDashboardDtl(jObjfillter.get("fromDate").toString(),jObjfillter.get("toDate").toString(),jObjfillter.get("reportType").toString());
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(201).entity(jObjRptData).build();
	}


		@POST
		@Path("/funStockFlashReport")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funStockFlashReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			 List listRet=null;
			try {
				jObjRptData = objPOSReportService.funStockFlashReportDtl(jObjfillter.get("fromdate").toString(),jObjfillter.get("toDate").toString(), 
						jObjfillter.get("posCode").toString(),jObjfillter.get("type").toString(),jObjfillter.get("reportType").toString(),jObjfillter.get("groupName").toString(),jObjfillter.get("balStockSign").toString(),jObjfillter.get("zeroStockBalYN").toString());
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 return Response.status(201).entity(jObjRptData).build();
		}



		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funGenerateProductionEntry")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response funGenerateProductionEntry(JSONObject jObjStock)
		{
		  String result = "";
		  result = objPOSReportService.funGenerateProductionEntry(jObjStock);
		  return Response.status(201).entity(result).build();
		}
		
		
		
		
		@GET 
		@Path("/funGetReasonForStockInOutFlash")
		@Consumes(MediaType.APPLICATION_JSON)
		public JSONObject funGetReasonCode(@QueryParam("ClientCode") String clientCode)
		{
			
			JSONObject jObjReasonData=new JSONObject();
			jObjReasonData=objPOSReportService.funGetReasonForStockInOutFlash(clientCode);
			return jObjReasonData;
		}
		
		
		
		@POST
		@Path("/funStockInOutFlashReport")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funStockInOutFlashReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			 List listRet=null;
			try {
				jObjRptData = objPOSReportService.funStockInOutFlashReport(jObjfillter.get("fromdate").toString(),jObjfillter.get("toDate").toString(), 
						jObjfillter.get("posCode").toString(),jObjfillter.get("operationType").toString(),jObjfillter.get("operationTypeCode").toString(),jObjfillter.get("viewType").toString(),jObjfillter.get("searchData").toString(),jObjfillter.get("reasonCode").toString());
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 return Response.status(201).entity(jObjRptData).build();
		}



		@POST
		@Path("/funGetBillWiseSettlementSalesSummary")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funGetBillWiseSettlementSalesSummary(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			 List listRet=null;;
			try {
				jObjRptData = objPOSReportService.funGetBillWiseSettlementSalesSummary(jObjfillter.get("fromDate").toString(),
							jObjfillter.get("toDate").toString(),jObjfillter.get("viewBy").toString(), jObjfillter.get("operationType").toString(),jObjfillter.get("settlementName").toString(), jObjfillter.get("posCode").toString(),jObjfillter.get("posName").toString());
			
			} catch (JSONException e) {
			
				e.printStackTrace();
			}
			 
			 return Response.status(201).entity(jObjRptData).build();
		}
		
		@POST
		@Path("/funGroupSubGroupWiseReport")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funGroupSubGroupWiseReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			 List listRet=null;;
			try {
				jObjRptData = objPOSReportService.funGroupSubGroupWiseReport(jObjfillter.get("strFromdate").toString(),
							jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), jObjfillter.get("sgCode").toString(),
							jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString(),jObjfillter.get("gCode").toString());
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 return Response.status(201).entity(jObjRptData).build();
		}



		@POST
		@Path("/funMailDayEndReport")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funMailDayEndReport(JSONObject jObjfillter)
		{
			JSONObject jObjRptData = new JSONObject();
			try{
				objPOSReportService.funMailDayEndReport(jObjfillter);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return Response.status(201).entity(jObjRptData).build();
		}
		//funDailySaleReport
		@POST
		 @Path("/funDailySaleReport")
		 @Produces(MediaType.APPLICATION_JSON)
		 public Response funDailySaleReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			
			try {
				jObjRptData = objPOSReportService.funDailySaleReport(jObjfillter.get("strFromdate").toString(),
							jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
							jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString()); 
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return Response.status(201).entity(jObjRptData).build();
		}
		
		@POST
		 @Path("/funGuestCreditReport")
		 @Produces(MediaType.APPLICATION_JSON)
		 public Response funGuestCreditReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			
			try {
				jObjRptData = objPOSReportService.funGuestCreditReport(jObjfillter.get("strFromdate").toString(),
							jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
							jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString()); 
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return Response.status(201).entity(jObjRptData).build();
		}

		@POST
		 @Path("/funUnusedCardBalanceReport")
		 @Produces(MediaType.APPLICATION_JSON)
		 public Response funUnusedCardBalanceReport(JSONObject jObjfillter)
		{
			 JSONObject jObjRptData = new JSONObject();
			
			try {
				jObjRptData = objPOSReportService.funUnusedCardBalanceReport(jObjfillter.get("strFromdate").toString(),
							jObjfillter.get("strToDate").toString(), jObjfillter.get("posCode").toString(), 
							jObjfillter.get("strShiftNo").toString(), jObjfillter.get("userCode").toString()); 
			 //jObjRptData.put("listRptData", listRet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return Response.status(201).entity(jObjRptData).build();
		}



}


	


