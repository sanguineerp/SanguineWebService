package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMailDayEndReportDao;
import com.apos.dao.clsPOSMasterDaoImpl;
import com.apos.dao.clsPOSReportDao;

@Service("clsPOSReportService")
public class clsPOSReportService {

	@Autowired
	private clsPOSReportDao objPOSReportDao;
	
	@Autowired
	private clsMailDayEndReportDao objMailDayEndReportDao;  
	
		public JSONObject funGroupWiseReportDtl(String fromDate,String toDae,String strPOSCode,String strSGCode,String strShiftNo,String strUserCode)
		{
		return objPOSReportDao.funGroupWiseReportDtl(fromDate, toDae, strPOSCode, strSGCode, strShiftNo,strUserCode);
		}
	
	//item wise report
		public JSONObject funItemWiseReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funItemWiseReportDtl(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		
		public JSONObject funTaxWiseReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funTaxWiseReportDtl(fromDate, toDae, strPOSCode,strShiftNo,strUserCode);
		}
		
		public JSONObject funDiscountWiseReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strViewType,String strUserCode)
		{
			return objPOSReportDao.funDiscountWiseReportDtl(fromDate, toDae, strPOSCode,strShiftNo,strViewType,strUserCode);
		}
	
		public JSONObject funComplimentarySettlementReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strReasonCode,String strViewType,String strUserCode)
		{
			return objPOSReportDao.funComplimentarySettlementReportDtl(fromDate, toDae, strPOSCode,strShiftNo,strReasonCode,strViewType,strUserCode);
		}
		
		public JSONObject funBillWiseReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funBillWiseReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		
		public JSONObject funGroupSubGroupWiseReport(String fromDate,String toDae,String strPOSCode,String strSGCode,String strShiftNo,String strUserCode,String gCode)
		{
			return objPOSReportDao.funGroupSubGroupWiseReport(fromDate, toDae, strPOSCode, strSGCode, strShiftNo,strUserCode,gCode);
		}
		
		public JSONObject funDailyCollectionReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funDailyCollectionReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		//funSettlementWiseSales
		public JSONObject funSettlementWiseSales(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funSettlementWiseSales(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		//funAuditorReport
		public JSONObject funAuditorReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funAuditorReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		//funSubGroupWiseSummaryReport
		public JSONObject funSubGroupWiseSummaryReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funSubGroupWiseSummaryReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		//funNCKotReport
		public JSONObject funNCKotReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String strReasonCode)
		{
			return objPOSReportDao.funNCKotReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,strReasonCode);
		}

		public JSONObject funSubGroupWiseReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
		return objPOSReportDao.funSubGroupWiseReportDtl(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
	
		public JSONObject funRevenueHeadWiseItemSalesReportDtl(String fromDate,String toDate,String strPOSCode,String strShiftNo,String revenueHead,String reportType, String strUserCode)
		{
			return objPOSReportDao.funRevenueHeadWiseItemSalesReportDtl(fromDate, toDate, strPOSCode, strShiftNo,revenueHead,reportType,strUserCode);
		}
		public JSONObject funOperatorWiseReportDtl(String fromDate,String toDate,String strPOSCode,String strShiftNo,String strUserCode,String settlementCode)
		{
			return objPOSReportDao.funOperatorWiseReportDtl(fromDate, toDate, strPOSCode, strShiftNo,strUserCode,settlementCode);
		}
		public JSONObject funVoidBillReportDtl(String fromDate,String toDate,String strPOSCode,String strShiftNo,String strUserCode,String reportType)
		{
			return objPOSReportDao.funVoidBillReportDtl(fromDate, toDate, strPOSCode, strShiftNo,strUserCode,reportType);
		}
		public JSONObject funCostCenterWiseSalesReport(String fromDate,String toDate,String strPOSCode,String strShiftNo,String userCode,String clientCode,String reportType,String costCenterCode,String logedInPosCode)
		{
			return objPOSReportDao.funCostCenterWiseSalesReport(fromDate, toDate, strPOSCode, strShiftNo,userCode,clientCode,reportType,costCenterCode,logedInPosCode);
		}
		public JSONObject funGetAllPhysicalStockFlash(String fromDate,String toDate,String strPOSCode)
		{
			return objPOSReportDao.funGetAllPhysicalStockFlash(fromDate, toDate, strPOSCode);
		}
		public JSONObject funGetPhysicalStockFlash(String strPSPCode)
		{
			return objPOSReportDao.funGetPhysicalStockFlash(strPSPCode);
		}
		public JSONObject funGetAuditFlash(String fromDate,String toDate,String strPOSCode,String userCode,String strReportType,String reasonCode,String auditType,String clientCode)
		{
			return objPOSReportDao.funGetAuditFlash(fromDate, toDate, strPOSCode,userCode,strReportType,reasonCode,auditType,clientCode);
		}
		public JSONObject funGetAdvanceOrderFlash(String fromDate,String toDate,String strPOSCode,String strDateFilter,String strReportType,String strCustomerCode,String operationType,String advOrderCode,String strStatus,String userCode,String clientCode,String LogedInPOS)
		{
			return objPOSReportDao.funGetAdvanceOrderFlash(fromDate, toDate, strPOSCode,strDateFilter,strReportType,strCustomerCode,operationType,advOrderCode,strStatus,userCode,clientCode,LogedInPOS);
		}
		
		public JSONObject funSalesReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String field,
				String strPayMode,String strOperator,String strFromBill,String strToBill,String reportType,String Type,String Customer,String ConsolidatePOS,String ReportName,String LoginPOSCode)
		{
			return objPOSReportDao.funSalesReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,field,strPayMode,strOperator,
					strFromBill,strToBill,reportType,Type,Customer,ConsolidatePOS,ReportName,LoginPOSCode);
		}
		//funPromotionFlash
		public JSONObject funPromotionFlash(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String promoCode)
		{
			return objPOSReportDao.funPromotionFlash(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,promoCode);
		}	
		//funCounterWiseSalesReport
		public JSONObject funCounterWiseSalesReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String reportType)
		{
			return objPOSReportDao.funCounterWiseSalesReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,reportType);
		}	
		//funTaxBreakupSummaryReport
		public JSONObject funTaxBreakupSummaryReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funTaxBreakupSummaryReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		//funWaiterWiseIncentiveReport
		public JSONObject funWaiterWiseIncentiveReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String reportType)
		{
			return objPOSReportDao.funWaiterWiseIncentiveReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,reportType);
		}	
		//funDeliveryBoyWiseIncentiveReport
		public JSONObject funDeliveryBoyWiseIncentiveReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String reportType,String DBCode)
		{
			return objPOSReportDao.funDeliveryBoyWiseIncentiveReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,reportType,DBCode);
		}	
		//funAdvanceOrderReport
		public JSONObject funAdvanceOrderReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String orderType)
		{
			return objPOSReportDao.funAdvanceOrderReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,orderType);
		}
		//funItemMasterListingReport
		public JSONObject funItemMasterListingReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funItemMasterListingReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		
		public JSONObject funDayEndFlashReportDtl(String fromDate,String toDae,String strPOSCode)
		{
			return objPOSReportDao.funDayEndFlashReportDtl(fromDate, toDae, strPOSCode);
		}


		public JSONObject funAIPBReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo)
		{
			return objPOSReportDao.funAIPBReportDtl(fromDate, toDae, strPOSCode,strShiftNo);
		}
		public JSONObject funATVReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo)
		{
			return objPOSReportDao.funATVReportDtl(fromDate, toDae, strPOSCode,strShiftNo);
		}
		public JSONObject funAPCReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strPowWise, String strDateWise)
		{
			return objPOSReportDao.funAPCReportDtl(fromDate, toDae, strPOSCode,strShiftNo,strPowWise,strDateWise);
		}

		public JSONObject funDebitCardFlashReportDtl(String fromDate,String toDae,String strPOSCode,String auditType,String strShiftNo)
		{
			return objPOSReportDao.funDebitCardFlashReportDtl(fromDate, toDae, strPOSCode,auditType,strShiftNo);
		}


		public JSONObject funGetPOSWiseSalesReportDtl(String fromDate,String toDae,String strViewType)
		{
			return objPOSReportDao.funGetPOSWiseSalesReportDtl(fromDate,toDae,strViewType);
		}


		public JSONObject funVoidAdvanceOrderReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strOrderType)
		{
			return objPOSReportDao.funVoidAdvanceOrderReportDtl(fromDate, toDae, strPOSCode,strShiftNo,strOrderType);
		}
		public JSONObject funWaiterWiseItemWiseIncentivesReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo)
		{
			return objPOSReportDao.funWaiterWiseItemWiseIncentivesReportDtl(fromDate, toDae, strPOSCode,strShiftNo);
		}
		public JSONObject funItemMasterListingReportDtl(String fromDate,String toDae,String strPOSCode,String strShiftNo)
		{
			return objPOSReportDao.funItemMasterListingReportDtl(fromDate, toDae, strPOSCode,strShiftNo);
		}
		
		public JSONObject funVoidKOTReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funVoidKOTReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}

		/*public JSONObject funGuestCreditReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funGuestCreditReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}*/
		
		public JSONObject funPOSUnusedCardBalanceReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funPOSUnusedCardBalanceReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		
		public JSONObject funPOSItemWiseConsumptionReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String strClientCode,String loginPosCode)
		{
			return objPOSReportDao.funPOSItemWiseConsumptionReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,strClientCode,loginPosCode);
		}
		
		public JSONObject funPostingReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funPostingReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode);
		}
		public JSONObject funReprintDocsReport(String fromDate,String toDae,String strPOSCode,String strUser,String strDocNo,String strType)
		{
			return objPOSReportDao.funReprintDocsReport(fromDate, toDae, strPOSCode, strUser,strDocNo,strType);
		}
	
	
		public JSONObject funSalesSummaryFlash(String fromDate,String toDae,String strPOSCode,String payMode,String strUserCode,String reportType )
		{
			return objPOSReportDao.funSalesSummaryFlash(fromDate, toDae, strPOSCode, payMode,strUserCode,reportType);
		}
		public JSONObject funTableWisePaxReport(String fromDate,String toDae,String strPOSCode,String strShiftNo,String strUserCode,String strClientCode,String loginPosCode)
		{
			return objPOSReportDao.funTableWisePaxReport(fromDate, toDae, strPOSCode, strShiftNo,strUserCode,strClientCode,loginPosCode);
	    } 
		
		public JSONObject funGetDayWiseSalesSummary(String withDiscount,String fromDate,String toDate,String strOperationType,String strSettlementCode,String strPosCode,String strPOSName)
		{
			return objPOSReportDao.funGetDayWiseSalesSummary(withDiscount,fromDate, toDate, strOperationType, strSettlementCode, strPosCode,strPOSName);
		}
		public JSONObject funGetDayWiseSalesSummary2(String withDiscount,String fromDate,String toDate,String strOperationType,String strSettlementCode,String strPosCode,String strPOSName)
		{
			return objPOSReportDao.funGetDayWiseSalesSummary2(withDiscount,fromDate, toDate, strOperationType,strSettlementCode, strPosCode,strPOSName);
		}

		public JSONObject funGetPOSDashboardSalesReportDtl(String fromDate,String toDate,String strReportType,String POSCode)
		{
			return objPOSReportDao.funGetPOSDashboardSalesReportDtl(fromDate,toDate,strReportType,POSCode);
		}
		
		public JSONObject funGetSalePurchaseComparisonDtl(String fromDate,String toDate)
		{
			return objPOSReportDao.funGetSalePurchaseComparisonDtl(fromDate,toDate);
		}
		
		public JSONObject funGetComparisonwiseDashboardDtl(String fromDate,String toDate,String reportType)
		{
			return objPOSReportDao.funGetPOSComparisonwiseSaleDtl(fromDate, toDate, reportType);
		}
		
		public JSONObject funStockFlashReportDtl(String fromDate,String toDate,String posCode,String type,String reportType,String groupName,String balStockSign,String zeroStockBalYN)
		{
			return objPOSReportDao.funStockFlashReportDtl(fromDate, toDate, posCode,type,reportType,groupName,balStockSign,zeroStockBalYN);
		}
		
		public String funGenerateProductionEntry(JSONObject jObj)
		{
			return objPOSReportDao.funGenerateProductionEntry(jObj);
		}
		
		public JSONObject funGetReasonForStockInOutFlash(String clientCode)
		{
			return objPOSReportDao.funGetReasonForStockInOutFlash(clientCode);
		}
		
		public JSONObject funStockInOutFlashReport(String fromDate,String toDate,String posCode,String operationType,String operationTypeCode,String viewType,String searchData,String reasonCode)
		{
			return objPOSReportDao.funStockInOutFlashReport(fromDate,toDate,posCode,operationType,operationTypeCode,viewType,searchData,reasonCode);
		}
		
		public JSONObject funGetBillWiseSettlementSalesSummary(String fromDate,String toDate,String viewBy,String strOperationType,String strSettlementCode,String strPosCode,String strPOSName)
		{
			return objPOSReportDao.funGetBillWiseSettlementSalesSummary(fromDate, toDate,viewBy, strOperationType,strSettlementCode, strPosCode, strPOSName);
		}
		
		public JSONObject funViewButtonPressed(String code,String transactionType,String kotFor,String posCode,String clientCode,String posName,String webStockUserCode,String POSDate,String PrintVatNoPOS,String vatNo,String printServiceTaxNo,String serviceTaxNo)
		{

		  return objPOSReportDao.funViewButtonPressed(code,transactionType,kotFor,posCode,clientCode,posName,webStockUserCode,POSDate,PrintVatNoPOS,vatNo,printServiceTaxNo, serviceTaxNo);
		} 
		
		
		public JSONObject funMailDayEndReport(JSONObject jObjfillter)
		{
			return objMailDayEndReportDao.funMailDayEndReport(jObjfillter);
		}
		
		public JSONObject funDailySaleReport(String fromDate,String toDate,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funDailySaleReport(fromDate, toDate, strPOSCode, strShiftNo,strUserCode);
		}
		
		public JSONObject funGuestCreditReport(String fromDate,String toDate,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funGuestCreditReport(fromDate, toDate, strPOSCode, strShiftNo,strUserCode);
		}
		
		public JSONObject funUnusedCardBalanceReport(String fromDate,String toDate,String strPOSCode,String strShiftNo,String strUserCode)
		{
			return objPOSReportDao.funGuestCreditReport(fromDate, toDate, strPOSCode, strShiftNo,strUserCode);
		}
		
		
		
	
}
