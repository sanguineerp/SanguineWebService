package com.apos.dao;

import java.math.BigInteger;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsBillSeriesHdModel;
import com.apos.model.clsBillSeriesModel_ID;
import com.apos.model.clsPrinterSetupHdModel;
import com.apos.model.clsSetupHdModel;
import com.apos.model.clsPrinterSetupModel_ID;
import com.hopos.controller.clsPostPOSBillData;
@Repository("clsSetupDao")
@Transactional(value = "webPOSTransactionManager")
public class clsSetupDao
{

	@Autowired
	private SessionFactory	webPOSSessionFactory;

	@SuppressWarnings("finally")
	public JSONObject funGetPOSWiseSetup(String clientCode, String posCode)
	{
		clsSetupHdModel objSetupHdModel = null;
		JSONObject objJsonObject=new JSONObject();
		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsSetupHdModel.class);
			 String sql = "select strPOSCode from tblsetup where strPOSCode<>'All' ";
				Query query = webPOSSessionFactory.getCurrentSession()
						.createSQLQuery(sql);
				List list = query.list();
				if(posCode.equalsIgnoreCase("All"))
				{
					cr.add(Restrictions.eq("strClientCode", clientCode));
					
				}
				else if (list.size() > 0) {
					  sql = "select count(*) from tblsetup where strPOSCode='" + posCode + "' ";
					  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
						
						list = query.list();
					
						BigInteger count=new BigInteger("0");
						
							 Object obj=(Object)list.get(0);
								count=(BigInteger) obj;
							
								 if (count==BigInteger.ZERO)
						         {
									 	cr.add(Restrictions.eq("strClientCode", clientCode));
										
						         }
								 else
								 {
									 cr.add(Restrictions.eq("strClientCode", clientCode));
									 cr.add(Restrictions.eq("strPOSCode", posCode));
								 }
		            
				}
		
		
			else
			{
				cr.add(Restrictions.eq("strClientCode", clientCode));
				posCode="All";
			}
				list = cr.list();

			
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				
				objSetupHdModel = (clsSetupHdModel) list.get(cnt);
				objJsonObject.put("posCode", posCode);//clientCode
				objJsonObject.put("gClientCode", objSetupHdModel.getStrClientCode());//clientCode
				objJsonObject.put("gClientName",objSetupHdModel.getStrClientName());//clientName
				objJsonObject.put("gClientAddress1", objSetupHdModel.getStrAddressLine1());
				objJsonObject.put("gClientAddress2", objSetupHdModel.getStrAddressLine2());
				objJsonObject.put("gClientAddress3", objSetupHdModel.getStrAddressLine3());
				objJsonObject.put("gClientEmail", objSetupHdModel.getStrEmail());
				objJsonObject.put("gBillFooter", objSetupHdModel.getStrBillFooter());
				objJsonObject.put("gBillPaperSize", objSetupHdModel.getIntBillPaperSize());
				objJsonObject.put("gNegBilling", objSetupHdModel.getStrNegativeBilling());
				
				objJsonObject.put("chkDayEnd", objSetupHdModel.getStrDayEnd());
				objJsonObject.put("strBillPrintMode", objSetupHdModel.getStrPrintMode());
			//	objJsonObject.put("gBillPaperSize", list.get(12));
				
				objJsonObject.put("gCityName", objSetupHdModel.getStrCityName());
				objJsonObject.put("gStateName",objSetupHdModel.getStrState());
				objJsonObject.put("gCountryName",objSetupHdModel.getStrCountry());
				objJsonObject.put("gClientTelNo",objSetupHdModel.getIntTelephoneNo());
				objJsonObject.put("gMobileNoForSMS",objSetupHdModel.getIntTelephoneNo());
				objJsonObject.put("gStartDate",objSetupHdModel.getDteStartDate());
				objJsonObject.put("gEndDate", objSetupHdModel.getDteEndDate());
				objJsonObject.put("gEndTime",objSetupHdModel.getDteEndDate());
				objJsonObject.put("gNatureOfBusinnes", objSetupHdModel.getStrNatureOfBusinnes());
				objJsonObject.put("gMultiBillPrint",objSetupHdModel.getStrMultipleBillPrinting());
				objJsonObject.put("gEnableKOT",objSetupHdModel.getStrEnableKOT());
				
				objJsonObject.put("gEffectOnPSP",objSetupHdModel.getStrEffectOnPSP());
				objJsonObject.put("gPrintVatNo", objSetupHdModel.getStrPrintVatNo());
				objJsonObject.put("gVatNo",objSetupHdModel.getStrVatNo());
				objJsonObject.put("gShowBill", objSetupHdModel.getStrShowBill());
				objJsonObject.put("gPrintServiceTaxNo", objSetupHdModel.getStrPrintServiceTaxNo());
				objJsonObject.put("gServiceTaxNo", objSetupHdModel.getStrServiceTaxNo());
				objJsonObject.put("gManualBillNo", objSetupHdModel.getStrManualBillNo());
				objJsonObject.put("gMenuItemSequence",objSetupHdModel.getStrMenuItemDispSeq());
				objJsonObject.put("gSenderEmailId", objSetupHdModel.getStrSenderEmailId());
				objJsonObject.put("gSenderMailPassword",objSetupHdModel.getStrEmailPassword());
				
			//	objJsonObject.put("gBillPaperSize", list.get(32));
				
				objJsonObject.put("gEmailMessage", objSetupHdModel.getStrBody());
				objJsonObject.put("gEmailServerName",objSetupHdModel.getStrEmailServerName());
				objJsonObject.put("gSMSApi", objSetupHdModel.getStrSMSApi());
			
				objJsonObject.put("gHOPOSType", objSetupHdModel.getStrPOSType());
				objJsonObject.put("gSanguineWebServiceURL",objSetupHdModel.getStrWebServiceLink());
				objJsonObject.put("gDataSendFrequency",objSetupHdModel.getStrDataSendFrequency());
				objJsonObject.put("gLastModifiedDate",objSetupHdModel.getDteHOServerDate());
				objJsonObject.put("gRFIDInterface",objSetupHdModel.getStrRFID());
				objJsonObject.put("gRFIDDBServerName",objSetupHdModel.getStrServerName());
				objJsonObject.put("gRFIDDBUserName", objSetupHdModel.getStrDBUserName());
				objJsonObject.put("gRFIDDBPassword",objSetupHdModel.getStrDBPassword());
				objJsonObject.put("gRFIDDBName",objSetupHdModel.getStrDatabaseName());
				objJsonObject.put("gKOTPrintingEnableForDirectBiller", objSetupHdModel.getStrEnableKOTForDirectBiller());
				
				objJsonObject.put("pinCode",objSetupHdModel.getIntPinCode());
				
				objJsonObject.put("gTheme", objSetupHdModel.getStrChangeTheme());
				objJsonObject.put("gMaxDiscount",objSetupHdModel.getDblMaxDiscount());
				objJsonObject.put("gAreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
				objJsonObject.put("gMenuItemSortingOn",objSetupHdModel.getStrMenuItemSortingOn());
				objJsonObject.put("gDirectAreaCode", objSetupHdModel.getStrDirectAreaCode());
				objJsonObject.put("gColumnSize", objSetupHdModel.getIntColumnSize());
				objJsonObject.put("gPrintType", objSetupHdModel.getStrPrintType());
				objJsonObject.put("gEditHDCharges", objSetupHdModel.getStrEditHomeDelivery());
				objJsonObject.put("gSlabBasedHDCharges", objSetupHdModel.getStrSlabBasedHDCharges());
				
			//	objJsonObject.put("gBillPaperSize", list.get(60));
				
				objJsonObject.put("gSkipWaiter",objSetupHdModel.getStrSkipWaiter());
				objJsonObject.put("gDirectKOTPrintingFromMakeKOT", objSetupHdModel.getStrDirectKOTPrintMakeKOT());
				objJsonObject.put("gSkipPax",objSetupHdModel.getStrSkipPax());
				objJsonObject.put("gCRMInterface",objSetupHdModel.getStrCRMInterface());
				objJsonObject.put("gGetWebserviceURL",objSetupHdModel.getStrGetWebserviceURL());
				objJsonObject.put("gPostWebserviceURL", objSetupHdModel.getStrPostWebserviceURL());
				objJsonObject.put("gOutletUID",objSetupHdModel.getStrOutletUID());
				objJsonObject.put("gPOSID",objSetupHdModel.getStrPOSID());
				objJsonObject.put("gStockInOption",objSetupHdModel.getStrStockInOption());
				
				objJsonObject.put("custSeries", objSetupHdModel.getStrCustSeries());
				
				objJsonObject.put("gAdvRecPrintCount",objSetupHdModel.getIntAdvReceiptPrintCount());
				objJsonObject.put("gHomeDeliverySMS",objSetupHdModel.getStrHomeDeliverySMS());
				objJsonObject.put("gBillSettlementSMS", objSetupHdModel.getStrBillStettlementSMS());
				objJsonObject.put("gBillFormatType",objSetupHdModel.getStrBillFormatType());
				objJsonObject.put("gActivePromotions",objSetupHdModel.getStrActivePromotions());
				objJsonObject.put("gHomeDelSMSYN",objSetupHdModel.getStrSendHomeDelSMS());
				objJsonObject.put("gBillSettleSMSYN", objSetupHdModel.getStrSendBillSettlementSMS());
				objJsonObject.put("gSMSType", objSetupHdModel.getStrSMSType());
				objJsonObject.put("gPrintShortNameOnKOT",objSetupHdModel.getStrPrintShortNameOnKOT());
				objJsonObject.put("gCustHelpOnTrans",objSetupHdModel.getStrShowCustHelp());
				objJsonObject.put("gPrintOnVoidBill", objSetupHdModel.getStrPrintOnVoidBill());
				objJsonObject.put("gPostSalesDataToMMS", objSetupHdModel.getStrPostSalesDataToMMS());
				objJsonObject.put("gCustAreaCompulsory",objSetupHdModel.getStrCustAreaMasterCompulsory());
				objJsonObject.put("gPriceFrom",objSetupHdModel.getStrPriceFrom());
				objJsonObject.put("gShowPrinterErrorMsg", objSetupHdModel.getStrShowPrinterErrorMessage());
				
			//	objJsonObject.put("gBillPaperSize", list.get(86));
				
				objJsonObject.put("gCardIntfType", objSetupHdModel.getStrCardInterfaceType());
				objJsonObject.put("gCMSIntegrationYN",objSetupHdModel.getStrCMSIntegrationYN());
				objJsonObject.put("gCMSWebServiceURL",objSetupHdModel.getStrCMSWebServiceURL());
				objJsonObject.put("gChangeQtyForExternalCode", objSetupHdModel.getStrChangeQtyForExternalCode());
				objJsonObject.put("gPointsOnBillPrint",objSetupHdModel.getStrPointsOnBillPrint());
				objJsonObject.put("gCMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
				objJsonObject.put("gCompulsoryManualAdvOrderNo",objSetupHdModel.getStrManualAdvOrderNoCompulsory());
				objJsonObject.put("gPrintManualAdvOrderNoOnBill", objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
				objJsonObject.put("gPrintModQtyOnKOT",objSetupHdModel.getStrPrintModifierQtyOnKOT());
				objJsonObject.put("gNoOfLinesInKOTPrint",objSetupHdModel.getStrNoOfLinesInKOTPrint());
				objJsonObject.put("gMultipleKOTPrint",objSetupHdModel.getStrMultipleKOTPrintYN());
				objJsonObject.put("gItemQtyNumpad", objSetupHdModel.getStrItemQtyNumpad());
				objJsonObject.put("gTreatMemberAsTable", objSetupHdModel.getStrTreatMemberAsTable());
				objJsonObject.put("gPrintKotToLocaPrinter",objSetupHdModel.getStrKOTToLocalPrinter());
				
			//	objJsonObject.put("gBillPaperSize", list.get(101));
				
				objJsonObject.put("gEnableSettleBtnForDirectBiller",objSetupHdModel.getStrSettleBtnForDirectBillerBill());
				objJsonObject.put("gDelBoyCompulsoryOnDirectBiller", objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
				objJsonObject.put("gCMSMemberCodeForKOTJPOS",objSetupHdModel.getStrCMSMemberForKOTJPOS());
				objJsonObject.put("gCMSMemberCodeForKOTMPOS", objSetupHdModel.getStrCMSMemberForKOTMPOS());
				objJsonObject.put("gDontShowAdvOrderInOtherPOS",objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
				objJsonObject.put("gPrintZeroAmtModifierOnBill",objSetupHdModel.getStrPrintZeroAmtModifierInBill());
				objJsonObject.put("gPrintKOTYN",objSetupHdModel.getStrPrintKOTYN());
				objJsonObject.put("gCreditCardSlipNo",objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
				objJsonObject.put("gCreditCardExpiryDate", objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
				objJsonObject.put("gSelectWaiterFromCardSwipe",objSetupHdModel.getStrSelectWaiterFromCardSwipe());
				objJsonObject.put("gMultiWaiterSelOnMakeKOT",objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
				objJsonObject.put("gMoveTableToOtherPOS",objSetupHdModel.getStrMoveTableToOtherPOS());
				objJsonObject.put("gMoveKOTToOtherPOS",objSetupHdModel.getStrMoveKOTToOtherPOS());
				objJsonObject.put("gCalculateTaxOnMakeKOT",objSetupHdModel.getStrCalculateTaxOnMakeKOT());
				objJsonObject.put("gReceiverEmailIds", objSetupHdModel.getStrReceiverEmailId());
				objJsonObject.put("gItemWiseDiscount",objSetupHdModel.getStrCalculateDiscItemWise());
				objJsonObject.put("gRemarksOnTakeAway",objSetupHdModel.getStrTakewayCustomerSelection());
				objJsonObject.put("gShowItemStkColumnInDB",objSetupHdModel.getStrShowItemStkColumnInDB());
				objJsonObject.put("gItemType",objSetupHdModel.getStrItemType());
				objJsonObject.put("gAllowNewAreaMasterFromCustMaster", objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
				objJsonObject.put("gCustAddressSelectionForBill", objSetupHdModel.getStrCustAddressSelectionForBill());
				objJsonObject.put("gGenrateMI",objSetupHdModel.getStrGenrateMI());
				objJsonObject.put("gFTPAddress", objSetupHdModel.getStrFTPAddress());
				objJsonObject.put("gFTPServerUserName", objSetupHdModel.getStrFTPServerUserName());
				objJsonObject.put("gFTPServerPass",objSetupHdModel.getStrFTPServerPass());
				objJsonObject.put("gAllowToCalculateItemWeight", objSetupHdModel.getStrAllowToCalculateItemWeight());
				objJsonObject.put("gShowBillsType", objSetupHdModel.getStrShowBillsDtlType());
				objJsonObject.put("gPrintTaxInvoice", objSetupHdModel.getStrPrintTaxInvoiceOnBill());
				objJsonObject.put("gPrintInclusiveOfAllTaxes",objSetupHdModel.getStrPrintInclusiveOfAllTaxesOnBill());
				objJsonObject.put("gApplyDiscountOn",objSetupHdModel.getStrApplyDiscountOn());
				objJsonObject.put("gMemberCodeForKotInMposByCardSwipe",objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
				objJsonObject.put("gPrintBillYN", objSetupHdModel.getStrPrintBillYN());
				objJsonObject.put("gUseVatAndServiceTaxFromPos",objSetupHdModel.getStrVatAndServiceTaxFromPos());
				objJsonObject.put("gMemberCodeForMakeBillInMPOS",objSetupHdModel.getStrMemberCodeForMakeBillInMPOS());
				objJsonObject.put("gItemWiseKOTPrintYN", objSetupHdModel.getStrItemWiseKOTYN());
				objJsonObject.put("gLastPOSForDayEnd", objSetupHdModel.getStrLastPOSForDayEnd());
				objJsonObject.put("gCMSPostingType", objSetupHdModel.getStrCMSPostingType());
				objJsonObject.put("gPopUpToApplyPromotionsOnBill",objSetupHdModel.getStrPopUpToApplyPromotionsOnBill());
				objJsonObject.put("gSelectCustomerCodeFromCardSwipe", objSetupHdModel.getStrSelectCustomerCodeFromCardSwipe());
				objJsonObject.put("gCheckDebitCardBalanceOnTrans", objSetupHdModel.getStrCheckDebitCardBalOnTransactions());
				objJsonObject.put("gPickSettlementsFromPOSMaster", objSetupHdModel.getStrSettlementsFromPOSMaster());
				objJsonObject.put("gEnableShiftYN", objSetupHdModel.getStrShiftWiseDayEndYN());
				objJsonObject.put("gProductionLinkup", objSetupHdModel.getStrProductionLinkup());
				objJsonObject.put("gLockDataOnShiftYN",objSetupHdModel.getStrLockDataOnShift());
				objJsonObject.put("gWSClientCode", objSetupHdModel.getStrWSClientCode());
				
			//	objJsonObject.put("", list.get(147));
				
				objJsonObject.put("gEnableBillSeries",objSetupHdModel.getStrEnableBillSeries());
				objJsonObject.put("gEnablePMSIntegrationYN",objSetupHdModel.getStrEnablePMSIntegrationYN());
				objJsonObject.put("gPrintTimeOnBillYN", objSetupHdModel.getStrPrintTimeOnBill());
				objJsonObject.put("gPrintTDHItemsInBill", objSetupHdModel.getStrPrintTDHItemsInBill());
				objJsonObject.put("gPrintRemarkAndReasonForReprint",objSetupHdModel.getStrPrintRemarkAndReasonForReprint());
				objJsonObject.put("daysBeforeVoidAdvOrder", objSetupHdModel.getIntDaysBeforeOrderToCancel());
				objJsonObject.put("gNoOfDelDaysForAdvOrder", objSetupHdModel.getIntNoOfDelDaysForAdvOrder());
				objJsonObject.put("gNoOfDelDaysForUrgentOrder", objSetupHdModel.getIntNoOfDelDaysForUrgentOrder());
				objJsonObject.put("gSetUpToTimeForAdvOrder",objSetupHdModel.getStrSetUpToTimeForAdvOrder());
				objJsonObject.put("gSetUpToTimeForUrgentOrder", objSetupHdModel.getStrSetUpToTimeForUrgentOrder());
				objJsonObject.put("gUpToTimeForAdvOrder",objSetupHdModel.getStrUpToTimeForAdvOrder());
				objJsonObject.put("gUpToTimeForUrgentOrder", objSetupHdModel.getStrUpToTimeForUrgentOrder());
				objJsonObject.put("gEnablePrintAndSettleBtnForDB",objSetupHdModel.getStrEnableBothPrintAndSettleBtnForDB());
				objJsonObject.put("gInrestoPOSIntegrationYN", objSetupHdModel.getStrInrestoPOSIntegrationYN());
				objJsonObject.put("gInrestoPOSWebServiceURL", objSetupHdModel.getStrInrestoPOSWebServiceURL());
				objJsonObject.put("gInrestoPOSId", objSetupHdModel.getStrInrestoPOSId());
				objJsonObject.put("gInrestoPOSKey",objSetupHdModel.getStrInrestoPOSKey());
				objJsonObject.put("flgCarryForwardFloatAmtToNextDay", objSetupHdModel.getStrCarryForwardFloatAmtToNextDay());
				objJsonObject.put("gOpenCashDrawerAfterBillPrintYN",objSetupHdModel.getStrOpenCashDrawerAfterBillPrintYN());
				objJsonObject.put("gPropertyWiseSalesOrderYN",objSetupHdModel.getStrPropertyWiseSalesOrderYN());
				
			//	objJsonObject.put("", list.get(168));
				
				objJsonObject.put("gShowItemDetailsGrid", objSetupHdModel.getStrShowItemDetailsGrid());
				
				objJsonObject.put("ShowPopUpForNextItemQuantity", objSetupHdModel.getStrShowPopUpForNextItemQuantity());
				
				objJsonObject.put("gJioPOSIntegrationYN", objSetupHdModel.getStrJioMoneyIntegration());
				objJsonObject.put("gJioPOSWesServiceURL",objSetupHdModel.getStrJioWebServiceUrl());
				
				objJsonObject.put("strJioMID", objSetupHdModel.getStrJioMID());
				
				objJsonObject.put("strJioTID", objSetupHdModel.getStrJioTID());
				
				objJsonObject.put("strJioActivationCode", objSetupHdModel.getStrJioActivationCode());
				objJsonObject.put("strJioDeviceID",objSetupHdModel.getStrJioDeviceID());
				objJsonObject.put("ClientImage", objSetupHdModel.getBlobReportImage());
				objJsonObject.put("strNewBillSeriesForNewDay", objSetupHdModel.getStrNewBillSeriesForNewDay());
				objJsonObject.put("strShowReportsPOSWise",objSetupHdModel.getStrShowReportsPOSWise());
				objJsonObject.put("strEnableDineIn", objSetupHdModel.getStrEnableDineIn());
				objJsonObject.put("strAutoAreaSelectionInMakeKOT",objSetupHdModel.getStrAutoAreaSelectionInMakeKOT());
			
			
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return objJsonObject;
		}
	}

	@SuppressWarnings("finally")
	public List<clsSetupHdModel> funGetSetupForAllPOS(String clientCode)
	{
		List<clsSetupHdModel> listOfSetupHdModels = null;

		try
		{
			Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsSetupHdModel.class);
			cr.add(Restrictions.eq("strClientCode", clientCode));			

			List list = cr.list();

			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				clsSetupHdModel objSetupHdModel = (clsSetupHdModel) list.get(cnt);
				
				listOfSetupHdModels.add(objSetupHdModel);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return listOfSetupHdModels;
		}
	}

	public JSONObject funGetParameterValuePOSWise(String clientCode, String posCode, String parameterName)
	{
		JSONObject objJsonObject=new JSONObject();
		
		try
		{
		
			String columnName="";
			
			switch (parameterName)
			{
				case "gClientCode":columnName="strClientCode";						
					break;
					
				case "gClientName":columnName="strClientName";						
					break;							                
		                
				case "gClientAddress1":columnName="strAddressLine1";						
				break;	
				
				case "gClientAddress2":columnName="strAddressLine2";						
				break;	
				
				case "gClientAddress3":columnName="strAddressLine3";						
				break;	
				
				case "gClientEmail":columnName="strEmail";						
				break;	
				
				case "gBillFooter":columnName="strBillFooter";						
				break;	
				
				case "gBillPaperSize":columnName="intBillPaperSize";						
				break;	
				
				case "gRFIDInterface":columnName="strRFID";						
				break;
				
				case "gEnableShiftYN":columnName="strShiftWiseDayEndYN";						
				break;	
				
				case "gEnableBillSeries":columnName="strEnableBillSeries";						
				break;	
				  
				case "gShowBillsType":columnName="strShowBillsDtlType";						
				break;	
				
				case "gCMSIntegrationYN":columnName="strCMSIntegrationYN";						
				break;
				
				case "gTransactionType":columnName="strCMSIntegrationYN";						
				break;
				
				case "gPickSettlementsFromPOSMaster":columnName="strSettlementsFromPOSMaster";						
				break;
				
				case "gEnablePMSIntegrationYN":columnName="strEnablePMSIntegrationYN";						
				break;
				
				case "gActivePromotions":columnName="strActivePromotions";						
				break;
					
				case "gTreatMemberAsTable":columnName="strTreatMemberAsTable";						
				break;
				
				case "gJioMoneyActivationCode":columnName="strJioActivationCode";						
				break;
								
				case "gPopUpToApplyPromotionsOnBill":columnName="strPopUpToApplyPromotionsOnBill";						
				break;
			
				case "gCalculateTaxOnMakeKOT":columnName="strCalculateTaxOnMakeKOT";						
				break;
				
				case "gMultiWaiterSelOnMakeKOT":columnName="strMultiWaiterSelectionOnMakeKOT";						
				break;
				
				case "gSelectWaiterFromCardSwipe":columnName="strMultiWaiterSelectionOnMakeKOT";						
				break;
				
				case "gSkipPax":columnName="strSkipPax";						
				break;
				
				case "gSkipWaiter":columnName="strSkipWaiter";						
				break;
				
				case "gDirectAreaCode":columnName="strDirectAreaCode";						
				break;
				
				case "gMenuItemSortingOn":columnName="strMenuItemSortingOn";						
				break;
				
				case "gAreaWisePricing":columnName="strAreaWisePricing";						
				break;
				
				case "gCheckDebitCardBalanceOnTrans":columnName="strCheckDebitCardBalOnTransactions";						
				break;
				
				case "gCMSMemberCodeForKOTJPOS":columnName="strCMSMemberForKOTJPOS";						
				break;
				
				case "gInrestoPOSIntegrationYN":columnName="strInrestoPOSIntegrationYN";						
				break;
				
				case "gCRMInterface":columnName="strCRMInterface";						
				break;
				
				case "gGetWebserviceURL":columnName="strGetWebserviceURL";						
				break;
				
				case "gOutletUID":columnName="strOutletUID";						
				break;
				
				case "gCustAddressSelectionForBill":columnName="strCustAddressSelectionForBill";						
				break;
				
				case "gPrintType":columnName="strPrintType";						
				break;
				
				case "gCustAreaCompulsory":columnName="strCustAreaMasterCompulsory";						
				break;
				
				case "gNoOfLinesInKOTPrint" : columnName = "strNoOfLinesInKOTPrint";
				break;
				
				case "gShowBill" : columnName = "strShowBill";
				break;
				
				case "gPrintKOTYN" : columnName = "strPrintKOTYN";
				break;
				
				case "gMultipleKOTPrint" : columnName="strMultipleKOTPrintYN";
				break;
				
				case "gMultiBillPrint" : columnName = "strMultipleBillPrinting";
				break;
				
				case "gShowPrinterErrorMsg" : columnName = "strShowPrinterErrorMessage";
				break;
				
				case "gAdvReceiptPrinterPort" : columnName = "strOpenCashDrawerAfterBillPrintYN";
				break;
				
				case "gBillFormatType" : columnName = "strBillFormatType";
				break;
				
				case "gHOPOSType" : columnName ="strPOSType";
				break;
				
				case "gPrintTaxInvoice" : columnName="strPrintTaxInvoiceOnBill";
				break;
				
				case "gCityName" : columnName="strCityName";
				break;
				
				case "gPrintTimeOnBillYN" : columnName="strPrintTimeOnBill";
				break;
				
				case "gPrintZeroAmtModifierOnBill" : columnName="strPrintZeroAmtModifierInBill";
				break;
				
				case "gUseVatAndServiceTaxFromPos" : columnName="strVatAndServiceTaxFromPos";
				break;
				
				case "gPrintVatNo" : columnName="strPrintVatNo";
				break;
				
				case "gVatNo" : columnName="strVatNo";
				break;
				
				case "gPrintServiceTaxNo" : columnName="strPrintServiceTaxNo";
				break;
				
				case "gServiceTaxNo" : columnName="strServiceTaxNo";
				break;
				
				case "gClientTelNo" :columnName="intTelephoneNo";
				break;

				case "gBillSettleSMSYN" : columnName = "strSendBillSettlementSMS";
				break;

				case "gPrintShortNameOnKOT" : columnName ="strPrintShortNameOnKOT";
				break;
				
				case "gColumnSize" : columnName = "intColumnSize";
				break;
				
				case "gPrintModQtyOnKOT" : columnName ="strPrintModifierQtyOnKOT";
				break;
				
				case "gReceiverEmailIds" : columnName ="strReceiverEmailId";
				break;
				
				case "gLastPOSForDayEnd" : columnName ="strLastPOSForDayEnd";
				break;
				
				case "gSenderEmailId" : columnName ="strSenderEmailId";
				break;
				
				
				case "gCMSPostingType" : columnName ="strCMSPostingType";
				break;
				
				case "flgCarryForwardFloatAmtToNextDay" : columnName ="strCarryForwardFloatAmtToNextDay";
				break;
				
				case "gSenderMailPassword" : columnName ="strEmailPassword";
				break;
				
				case "gPostSalesDataToMMS" : columnName ="strPostSalesDataToMMS";
				break;
				
				case "gItemType" : columnName ="strItemType";
				break;
				
				case "gOpenCashDrawerAfterBillPrintYN"  : columnName ="strOpenCashDrawerAfterBillPrintYN";
				break;
				
				case "gSMSType"  : columnName ="strSMSType";
				break;
				
				case "gSMSApi"  : columnName ="strSMSApi";
				break;
				
			
				
				
				
				
				
				
			}
			
			
			
			SQLQuery query=webPOSSessionFactory.getCurrentSession().createSQLQuery("select "+columnName+" from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
			
			List list=query.list();				
			
			if(list!=null && list.size()>0)
			{				
				objJsonObject.put(parameterName, list.get(0));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return objJsonObject;
		}			
	}
	
	
	
	@SuppressWarnings("finally")
	public JSONObject funGetAllParameterValuesPOSWise(String clientCode, String posCode)
	{
		JSONObject objJsonObject=new JSONObject();
		
		try
		{
							
			SQLQuery query=webPOSSessionFactory.getCurrentSession().createSQLQuery("select * from tblsetup where (strPOSCode='"+posCode+"'  OR strPOSCode='All') ");
			
			List list=query.list();				
			
			if(list!=null && list.size()>0)
			{				
				objJsonObject.put("gClientCode", list.get(0));//clientCode
				objJsonObject.put("gClientName", list.get(1));//clientName
				objJsonObject.put("gClientAddress1", list.get(2));
				objJsonObject.put("gClientAddress2", list.get(3));
				objJsonObject.put("gClientAddress3", list.get(4));
				objJsonObject.put("gClientEmail", list.get(5));
				objJsonObject.put("gBillFooter", list.get(6));
				objJsonObject.put("gBillPaperSize", list.get(8));
				objJsonObject.put("gNegBilling", list.get(9));
				
				objJsonObject.put("chkDayEnd", list.get(10));
				objJsonObject.put("strBillPrintMode", list.get(11));
			//	objJsonObject.put("gBillPaperSize", list.get(12));
				
				objJsonObject.put("gCityName", list.get(13));
				objJsonObject.put("gStateName", list.get(14));
				objJsonObject.put("gCountryName", list.get(15));
				objJsonObject.put("gClientTelNo", list.get(16));
				objJsonObject.put("gMobileNoForSMS", list.get(16));
				objJsonObject.put("gStartDate", list.get(17));
				objJsonObject.put("gEndDate", list.get(18));
				objJsonObject.put("gEndTime", list.get(18));
				objJsonObject.put("gNatureOfBusinnes", list.get(19));
				objJsonObject.put("gMultiBillPrint", list.get(20));
				objJsonObject.put("gEnableKOT", list.get(21));
				
				objJsonObject.put("gEffectOnPSP", list.get(22));
				objJsonObject.put("gPrintVatNo", list.get(23));
				objJsonObject.put("gVatNo", list.get(24));
				objJsonObject.put("gShowBill", list.get(25));
				objJsonObject.put("gPrintServiceTaxNo", list.get(26));
				objJsonObject.put("gServiceTaxNo", list.get(27));
				objJsonObject.put("gManualBillNo", list.get(28));
				objJsonObject.put("gMenuItemSequence", list.get(29));
				objJsonObject.put("gSenderEmailId", list.get(30));
				objJsonObject.put("gSenderMailPassword", list.get(31));
				
			//	objJsonObject.put("gBillPaperSize", list.get(32));
				
				objJsonObject.put("gEmailMessage", list.get(33));
				objJsonObject.put("gEmailServerName", list.get(34));
				objJsonObject.put("gSMSApi", list.get(35));
			/*	
				objJsonObject.put("gBillPaperSize", list.get(36));
				objJsonObject.put("gBillPaperSize", list.get(37));
				objJsonObject.put("gBillPaperSize", list.get(38));
				objJsonObject.put("gBillPaperSize", list.get(39));
				*/
				objJsonObject.put("gHOPOSType", list.get(40));
				objJsonObject.put("gSanguineWebServiceURL", list.get(41));
				objJsonObject.put("gDataSendFrequency", list.get(42));
				objJsonObject.put("gLastModifiedDate", list.get(43));
				objJsonObject.put("gRFIDInterface", list.get(44));
				objJsonObject.put("gRFIDDBServerName", list.get(45));
				objJsonObject.put("gRFIDDBUserName", list.get(46));
				objJsonObject.put("gRFIDDBPassword", list.get(47));
				objJsonObject.put("gRFIDDBName", list.get(48));
				objJsonObject.put("gKOTPrintingEnableForDirectBiller", list.get(49));
				
				objJsonObject.put("pinCode", list.get(50));
				
				objJsonObject.put("gTheme", list.get(51));
				objJsonObject.put("gMaxDiscount", list.get(52));
				objJsonObject.put("gAreaWisePricing", list.get(53));
				objJsonObject.put("gMenuItemSortingOn", list.get(54));
				objJsonObject.put("gDirectAreaCode", list.get(55));
				objJsonObject.put("gColumnSize", list.get(56));
				objJsonObject.put("gPrintType", list.get(57));
				objJsonObject.put("gEditHDCharges", list.get(58));
				objJsonObject.put("gSlabBasedHDCharges", list.get(59));
				
			//	objJsonObject.put("gBillPaperSize", list.get(60));
				
				objJsonObject.put("gSkipWaiter", list.get(61));
				objJsonObject.put("gDirectKOTPrintingFromMakeKOT", list.get(62));
				objJsonObject.put("gSkipPax", list.get(63));
				objJsonObject.put("gCRMInterface", list.get(64));
				objJsonObject.put("gGetWebserviceURL", list.get(65));
				objJsonObject.put("gPostWebserviceURL", list.get(66));
				objJsonObject.put("gOutletUID", list.get(67));
				objJsonObject.put("gPOSID", list.get(68));
				objJsonObject.put("gStockInOption", list.get(69));
				
				objJsonObject.put("custSeries", list.get(70));
				
				objJsonObject.put("gAdvRecPrintCount", list.get(71));
				objJsonObject.put("gHomeDeliverySMS", list.get(72));
				objJsonObject.put("gBillSettlementSMS", list.get(73));
				objJsonObject.put("gBillFormatType", list.get(74));
				objJsonObject.put("gActivePromotions", list.get(75));
				objJsonObject.put("gHomeDelSMSYN", list.get(76));
				objJsonObject.put("gBillSettleSMSYN", list.get(77));
				objJsonObject.put("gSMSType", list.get(78));
				objJsonObject.put("gPrintShortNameOnKOT", list.get(79));
				objJsonObject.put("gCustHelpOnTrans", list.get(80));
				objJsonObject.put("gPrintOnVoidBill", list.get(81));
				objJsonObject.put("gPostSalesDataToMMS", list.get(82));
				objJsonObject.put("gCustAreaCompulsory", list.get(83));
				objJsonObject.put("gPriceFrom", list.get(84));
				objJsonObject.put("gShowPrinterErrorMsg", list.get(85));
				
			//	objJsonObject.put("gBillPaperSize", list.get(86));
				
				objJsonObject.put("gCardIntfType", list.get(87));
				objJsonObject.put("gCMSIntegrationYN", list.get(88));
				objJsonObject.put("gCMSWebServiceURL", list.get(89));
				objJsonObject.put("gChangeQtyForExternalCode", list.get(90));
				objJsonObject.put("gPointsOnBillPrint", list.get(91));
				objJsonObject.put("gCMSPOSCode", list.get(92));
				objJsonObject.put("gCompulsoryManualAdvOrderNo", list.get(93));
				objJsonObject.put("gPrintManualAdvOrderNoOnBill", list.get(94));
				objJsonObject.put("gPrintModQtyOnKOT", list.get(95));
				objJsonObject.put("gNoOfLinesInKOTPrint", list.get(96));
				objJsonObject.put("gMultipleKOTPrint", list.get(97));
				objJsonObject.put("gItemQtyNumpad", list.get(98));
				objJsonObject.put("gTreatMemberAsTable", list.get(99));
				objJsonObject.put("gPrintKotToLocaPrinter", list.get(100));
				
			//	objJsonObject.put("gBillPaperSize", list.get(101));
				
				objJsonObject.put("gEnableSettleBtnForDirectBiller", list.get(102));
				objJsonObject.put("gDelBoyCompulsoryOnDirectBiller", list.get(103));
				objJsonObject.put("gCMSMemberCodeForKOTJPOS", list.get(104));
				objJsonObject.put("gCMSMemberCodeForKOTMPOS", list.get(105));
				objJsonObject.put("gDontShowAdvOrderInOtherPOS", list.get(106));
				objJsonObject.put("gPrintZeroAmtModifierOnBill", list.get(107));
				objJsonObject.put("gPrintKOTYN", list.get(108));
				objJsonObject.put("gCreditCardSlipNo", list.get(109));
				objJsonObject.put("gCreditCardExpiryDate", list.get(110));
				objJsonObject.put("gSelectWaiterFromCardSwipe", list.get(111));
				objJsonObject.put("gMultiWaiterSelOnMakeKOT", list.get(112));
				objJsonObject.put("gMoveTableToOtherPOS", list.get(113));
				objJsonObject.put("gMoveKOTToOtherPOS", list.get(114));
				objJsonObject.put("gCalculateTaxOnMakeKOT", list.get(115));
				objJsonObject.put("gReceiverEmailIds", list.get(116));
				objJsonObject.put("gItemWiseDiscount", list.get(117));
				objJsonObject.put("gRemarksOnTakeAway", list.get(118));
				objJsonObject.put("gShowItemStkColumnInDB", list.get(119));
				objJsonObject.put("gItemType", list.get(120));
				objJsonObject.put("gAllowNewAreaMasterFromCustMaster", list.get(121));
				objJsonObject.put("gCustAddressSelectionForBill", list.get(122));
				objJsonObject.put("gGenrateMI", list.get(123));
				objJsonObject.put("gFTPAddress", list.get(124));
				objJsonObject.put("gFTPServerUserName", list.get(125));
				objJsonObject.put("gFTPServerPass", list.get(126));
				objJsonObject.put("gAllowToCalculateItemWeight", list.get(127));
				objJsonObject.put("gShowBillsType", list.get(128));
				objJsonObject.put("gPrintTaxInvoice", list.get(129));
				objJsonObject.put("gPrintInclusiveOfAllTaxes", list.get(130));
				objJsonObject.put("gApplyDiscountOn", list.get(131));
				objJsonObject.put("gMemberCodeForKotInMposByCardSwipe", list.get(132));
				objJsonObject.put("gPrintBillYN", list.get(133));
				objJsonObject.put("gUseVatAndServiceTaxFromPos", list.get(134));
				objJsonObject.put("gMemberCodeForMakeBillInMPOS", list.get(135));
				objJsonObject.put("gItemWiseKOTPrintYN", list.get(136));
				objJsonObject.put("gLastPOSForDayEnd", list.get(137));
				objJsonObject.put("gCMSPostingType", list.get(138));
				objJsonObject.put("gPopUpToApplyPromotionsOnBill", list.get(139));
				objJsonObject.put("gSelectCustomerCodeFromCardSwipe", list.get(140));
				objJsonObject.put("gCheckDebitCardBalanceOnTrans", list.get(141));
				objJsonObject.put("gPickSettlementsFromPOSMaster", list.get(142));
				objJsonObject.put("gEnableShiftYN", list.get(143));
				objJsonObject.put("gProductionLinkup", list.get(144));
				objJsonObject.put("gLockDataOnShiftYN", list.get(145));
				objJsonObject.put("gWSClientCode", list.get(146));
				
			//	objJsonObject.put("", list.get(147));
				
				objJsonObject.put("gEnableBillSeries", list.get(148));
				objJsonObject.put("gEnablePMSIntegrationYN", list.get(149));
				objJsonObject.put("gPrintTimeOnBillYN", list.get(150));
				objJsonObject.put("gPrintTDHItemsInBill", list.get(151));
				objJsonObject.put("gPrintRemarkAndReasonForReprint", list.get(152));
				objJsonObject.put("daysBeforeVoidAdvOrder", list.get(153));
				objJsonObject.put("gNoOfDelDaysForAdvOrder", list.get(154));
				objJsonObject.put("gNoOfDelDaysForUrgentOrder", list.get(155));
				objJsonObject.put("gSetUpToTimeForAdvOrder", list.get(156));
				objJsonObject.put("gSetUpToTimeForUrgentOrder", list.get(157));
				objJsonObject.put("gUpToTimeForAdvOrder", list.get(158));
				objJsonObject.put("gUpToTimeForUrgentOrder", list.get(159));
				objJsonObject.put("gEnablePrintAndSettleBtnForDB", list.get(160));
				objJsonObject.put("gInrestoPOSIntegrationYN", list.get(161));
				objJsonObject.put("gInrestoPOSWebServiceURL", list.get(162));
				objJsonObject.put("gInrestoPOSId", list.get(163));
				objJsonObject.put("gInrestoPOSKey", list.get(164));
				objJsonObject.put("flgCarryForwardFloatAmtToNextDay", list.get(165));
				objJsonObject.put("gOpenCashDrawerAfterBillPrintYN", list.get(166));
				objJsonObject.put("gPropertyWiseSalesOrderYN", list.get(167));
				
			//	objJsonObject.put("", list.get(168));
				
				objJsonObject.put("gShowItemDetailsGrid", list.get(169));
				
				objJsonObject.put("ShowPopUpForNextItemQuantity", list.get(170));
				
				objJsonObject.put("gJioPOSIntegrationYN", list.get(171));
				objJsonObject.put("gJioPOSWesServiceURL", list.get(172));
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return objJsonObject;
		}			
	}

	@SuppressWarnings("finally")
	public JSONObject funGetPrinterDtl()
	{
		List list =null;
		JSONObject jObjTableData=new JSONObject();
		
		
	        StringBuilder sqlStringBuilder = new StringBuilder();
	    

	        try
	        {
	            sqlStringBuilder.append(" select a.strCostCenterCode,a.strCostCenterName,ifnull(b.strPrimaryPrinterPort,'')"
	                    + " ,ifnull(b.strSecondaryPrinterPort,''),ifnull(b.strPrintOnBothPrintersYN,'N')"
	                    + " from tblcostcentermaster a "
	                    + " left outer join tblprintersetup b on a.strCostCenterCode=b.strCostCenterCode");
	         	
				Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlStringBuilder.toString());
				
				list = query.list();
				 JSONArray jArrData=new JSONArray();
				
				 if (list!=null)
					{
						for(int i=0; i<list.size(); i++)
						{
							Object[] obj=(Object[])list.get(i);
						
							JSONObject objSettle=new JSONObject();
							objSettle.put("strCostCenterCode",obj[0].toString());
							objSettle.put("strCostCenterName",obj[1].toString());
							objSettle.put("PrimaryPrinter",obj[2].toString());
							objSettle.put("SecondaryPrinter",obj[3].toString());
							objSettle.put("PrintOnBothPrintersYN",obj[4].toString());
						
							jArrData.put(objSettle);
						}
						
			          	jObjTableData.put("printerSetup", jArrData);
			         	
				      }
	     	}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	
	@SuppressWarnings("finally")
	public JSONObject loadOldSBillSeriesSetup(String newPropertyPOSCode)
	{
		List list =null;
		JSONObject jObjTableData=new JSONObject();
	       try
	        {
	        	  
	        	   String sqlBillSeries = "select a.strType from tblbillseries a where a.strPOSCode='" + newPropertyPOSCode + "' group by a.strType  ";
	              Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillSeries);
					
					list = query.list();
					String strType="";
					
					 if (list.size()>0)
						{
	            
							Object obj=(Object)list.get(0);
						strType=obj.toString();
						}
						
			          	jObjTableData.put("strType", strType);
				
	     	}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	@SuppressWarnings("finally")
	public JSONObject loadOldBillSeries(String newPropertyPOSCode)
	{
		List list =null;
		JSONObject jObjTableData=new JSONObject();
	       try
	        {
	        	  
	        	   String sqlBillSeries = "select a.strType,a.strBillSeries,a.strCodes,a.strNames,a.strPrintGTOfOtherBills,strPrintInclusiveOfTaxOnBill "
	                       + " from tblbillseries a where strPOSCode='" + newPropertyPOSCode + "' ";
	        	   
	              Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillSeries);
					
					list = query.list();
					
					JSONArray jArr=new JSONArray();
					 if (list.size()>0)
						{
						 for(int i=0;i<list.size();i++)
						 {
							Object[] obj=(Object[])list.get(i);
							JSONObject jObj= new JSONObject();
							jObj.put("strBillSeries",obj[1].toString());
							jObj.put("strCodes",obj[2].toString());
							jObj.put("strNames",obj[3].toString());
							jObj.put("strPrintGTOfOtherBills",obj[4].toString());
							jObj.put("strPrintInclusiveOfTaxOnBill",obj[5].toString());
							jArr.put(jObj);
						 }
						}
						
			          	jObjTableData.put("Billseries", jArr);
				
	     	}catch(Exception ex)
			{
				ex.printStackTrace();
				
			}
			finally
			{
				return jObjTableData;
			}
	}
	
	 public void funSaveUpdatePropertySetup(clsSetupHdModel objModel)
	    {	
		 String newPropertyPOSCode=objModel.getStrPOSCode();
		 String strHOPOSType=objModel.getStrPOSType();
		try
		{
			String sql = "select strClientCode from tblsetup where strPOSCode='All' ";
     	   
           Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				
			List list = query.list();
			 if (list.size()>0)
             {
              
               webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
               if (!newPropertyPOSCode.equalsIgnoreCase("All"))
               {
                 sql="delete from tblsetup where strPOSCode='All' ";
                 query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                 query.executeUpdate();
               }
             }
			  else
              {
                 
                  webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
                  if (newPropertyPOSCode.equalsIgnoreCase("All"))
                  {
                	  sql="delete from tblsetup where strPOSCode<>'All' ";
                	  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                      query.executeUpdate();
                  }
                  if (strHOPOSType.equalsIgnoreCase("Client POS"))
                  {
                      funPostPropertySetupDataToHO();
                      funPostBillSeriesDataHO();
                  }
              }
		  
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
	    }
	 public void funSavePrinterSetupData(JSONArray printerList, String Date,String clientCode,String userCode)
	 {try
	    {
		 String sql="truncate table tblprintersetup";
		 Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
         query.executeUpdate();
        
         for(int i=0; i<printerList.length(); i++)
		    {
		    try
		    {
			 JSONObject jObj = new JSONObject();
		    	jObj=printerList.getJSONObject(i);
		    	String costCenterCode=jObj.getString("strCostCenterCode");
		    	String strCostCenterName=jObj.getString("strCostCenterName");
		    	String strPrimaryPrinterPort=jObj.getString("strPrimaryPrinterPort");
		    	String strPrintOnBothPrintersYN=jObj.getString("strPrintOnBothPrintersYN");
		    	String strSecondaryPrinterPort=jObj.getString("strSecondaryPrinterPort");
		    	clsPrinterSetupHdModel objModel= new clsPrinterSetupHdModel(new clsPrinterSetupModel_ID(costCenterCode,clientCode));
		    	objModel.setStrCostCenterCode(costCenterCode);
		    	objModel.setStrCostCenterName(strCostCenterName);
		    	objModel.setStrPrimaryPrinterPort(strPrimaryPrinterPort);
		    	objModel.setStrPrintOnBothPrintersYN(strPrintOnBothPrintersYN);
		    	objModel.setStrSecondaryPrinterPort(strSecondaryPrinterPort);
		    	objModel.setStrUserCreated(userCode);
		    	objModel.setStrUserEdited(userCode);
		    	objModel.setDteDateCreated(Date);
		    	objModel.setDteDateEdited(Date);
		    	
		    	objModel.setStrDataPostFlag("Y");
		    	
		    	 webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
		    }
		    catch (Exception e)
			{
			    e.printStackTrace();
			}
		    }
	    }
	    catch (Exception e)
		{
		    e.printStackTrace();
		}
	 }
	 public void funSaveBillSeries(JSONArray billSeriesList,String strType,String newPropertyPOSCode, String Date,String clientCode,String userCode)
	 {
		  try
	        {
	            //clsGlobalVarClass.dbMysql.funStartTransaction();
			  Query query=webPOSSessionFactory.getCurrentSession().createQuery("DELETE clsBillSeriesHdModel WHERE strClientCode= :clientCode ");
				query.setParameter("clientCode", clientCode);
				
				query.executeUpdate();
				
	            for (int i = 0; i<billSeriesList.length(); i++)
	            {
	              JSONObject jObj= new JSONObject();
	              jObj=(JSONObject) billSeriesList.get(i);
	               String listOfCodes = (String)jObj.get("strCodes");
	               String listOfNames = (String)jObj.get("strNames");
	               String strBillSeries = (String)jObj.get("strBillSeries");
	               String strPrintGTOfOtherBills = (String)jObj.get("strPrintGTOfOtherBills");
	               String strPrintInclusiveOfTaxOnBill = (String)jObj.get("strPrintInclusiveOfTaxOnBill");
	               clsBillSeriesHdModel objModel= new clsBillSeriesHdModel(new clsBillSeriesModel_ID(strBillSeries,newPropertyPOSCode,clientCode));
			    	objModel.setStrBillSeries(strBillSeries);
			    	objModel.setStrCodes(listOfCodes);
			    	objModel.setStrNames(listOfNames);
			    	objModel.setStrPrintGTOfOtherBills(strPrintGTOfOtherBills);
			    	objModel.setStrPrintInclusiveOfTaxOnBill(strPrintInclusiveOfTaxOnBill);
			    	objModel.setStrPropertyCode(clientCode+"."+newPropertyPOSCode);
			    	objModel.setStrType(strType);
			    	objModel.setStrUserCreated(userCode);
			    	objModel.setStrUserEdited(userCode);
			    	objModel.setDteCreatedDate(Date);
			    	objModel.setDteEditedDate(Date);
			    	objModel.setStrDataPostFlag("Y");
			    	objModel.setIntLastNo(0);
			    	
			    	 webPOSSessionFactory.getCurrentSession().saveOrUpdate(objModel);
	            }
	          
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	           
	        }
	 }
	 private void funPostPropertySetupDataToHO()
	 {
		 boolean flgResult = false;
	        StringBuilder sql = new StringBuilder();
	    	clsSetupHdModel objSetupHdModel = null;
try{
		 JSONObject rootObject = new JSONObject();
         JSONArray dataObjectArray = new JSONArray();
         boolean flgAllPOS = false;

         sql.append("select strClientCode from tblsetup where strPOSCode='All' ");
         Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
			
			List list = query.list();
			 if (list.size()>0)
          {
         
             flgAllPOS = true;
         }
        
         sql.setLength(0);
         Criteria cr = webPOSSessionFactory.getCurrentSession().createCriteria(clsSetupHdModel.class);
		 cr.add(Restrictions.eq("strDataPostFlag", "N"));
		 list = cr.list();
		
			 if (list.size()>0)
          { 
		 for(int i=0; i<list.size();i++)
         {

				objSetupHdModel = (clsSetupHdModel) list.get(i);
         
             if (!objSetupHdModel.getStrWSClientCode().trim().isEmpty())
             {
                 if (flgAllPOS)
                 {
                     sql.setLength(0);
                     sql.append("select strPOSCode from tblposmaster ");
                      query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
         			
         			 List poslist = query.list();
         			 for(int j=0; j<poslist.size();j++)
         	         {
                         JSONObject objJsonObject = new JSONObject();
                      
              		  
                         objJsonObject.put("strClientCode", objSetupHdModel.getStrClientCode());//clientCode
         				objJsonObject.put("strClientName",objSetupHdModel.getStrClientName());//clientName
         				objJsonObject.put("strAddressLine1", objSetupHdModel.getStrAddressLine1());
         				objJsonObject.put("strAddressLine2", objSetupHdModel.getStrAddressLine2());
         				objJsonObject.put("strAddressLine3", objSetupHdModel.getStrAddressLine3());
         				objJsonObject.put("strEmail", objSetupHdModel.getStrEmail());
         				objJsonObject.put("strBillFooter", objSetupHdModel.getStrBillFooter());
         				objJsonObject.put("strBillFooterStatus", objSetupHdModel.getStrBillFooterStatus());
             			objJsonObject.put("intBillPaperSize", objSetupHdModel.getIntBillPaperSize());
             			objJsonObject.put("strNegativeBilling", objSetupHdModel.getStrNegativeBilling());
         				objJsonObject.put("strDayEnd", objSetupHdModel.getStrDayEnd());
         				objJsonObject.put("strPrintMode", objSetupHdModel.getStrPrintMode());
         				objJsonObject.put("strDiscountNote", objSetupHdModel.getStrDiscountNote());
         				objJsonObject.put("strCityName", objSetupHdModel.getStrCityName());
         				objJsonObject.put("strState",objSetupHdModel.getStrState());
         				objJsonObject.put("strCountry",objSetupHdModel.getStrCountry());
         				objJsonObject.put("intTelephoneNo",objSetupHdModel.getIntTelephoneNo());
         				objJsonObject.put("gMobileNoForSMS",objSetupHdModel.getIntTelephoneNo());
         				objJsonObject.put("dteStartDate",objSetupHdModel.getDteStartDate());
         				objJsonObject.put("dteEndDate", objSetupHdModel.getDteEndDate());
         				objJsonObject.put("gEndTime",objSetupHdModel.getDteEndDate());
         				objJsonObject.put("strNatureOfBusinnes", objSetupHdModel.getStrNatureOfBusinnes());
         				objJsonObject.put("strMultipleBillPrinting",objSetupHdModel.getStrMultipleBillPrinting());
         				objJsonObject.put("strEnableKOT",objSetupHdModel.getStrEnableKOT());
         				objJsonObject.put("strEffectOnPSP",objSetupHdModel.getStrEffectOnPSP());
         				objJsonObject.put("strPrintVatNo", objSetupHdModel.getStrPrintVatNo());
         				objJsonObject.put("strVatNo",objSetupHdModel.getStrVatNo());
         				objJsonObject.put("strShowBill", objSetupHdModel.getStrShowBill());
         				objJsonObject.put("strPrintServiceTaxNo", objSetupHdModel.getStrPrintServiceTaxNo());
         				objJsonObject.put("strServiceTaxNo", objSetupHdModel.getStrServiceTaxNo());
         				objJsonObject.put("strManualBillNo", objSetupHdModel.getStrManualBillNo());
         				objJsonObject.put("strMenuItemDispSeq",objSetupHdModel.getStrMenuItemDispSeq());
         				objJsonObject.put("strSenderEmailId", objSetupHdModel.getStrSenderEmailId());
         				objJsonObject.put("strEmailPassword",objSetupHdModel.getStrEmailPassword());
         				objJsonObject.put("strConfirmEmailPassword",objSetupHdModel.getStrConfirmEmailPassword());
         				objJsonObject.put("strBody", objSetupHdModel.getStrBody());
         				objJsonObject.put("strEmailServerName",objSetupHdModel.getStrEmailServerName());
         				objJsonObject.put("strSMSApi", objSetupHdModel.getStrSMSApi());
         				objJsonObject.put("strUserCreated", objSetupHdModel.getStrUserCreated());
         				objJsonObject.put("strUserEdited", objSetupHdModel.getStrUserEdited());
         				objJsonObject.put("dteDateCreated", objSetupHdModel.getDteDateCreated());
         				objJsonObject.put("dteDateEdited", objSetupHdModel.getDteDateEdited());
         				objJsonObject.put("strPOSType", objSetupHdModel.getStrPOSType());
         				objJsonObject.put("strWebServiceLink",objSetupHdModel.getStrWebServiceLink());
         				objJsonObject.put("strDataSendFrequency",objSetupHdModel.getStrDataSendFrequency());
         				objJsonObject.put("dteHOServerDate",objSetupHdModel.getDteHOServerDate());
         				objJsonObject.put("strRFID",objSetupHdModel.getStrRFID());
         				objJsonObject.put("strServerName",objSetupHdModel.getStrServerName());
         				objJsonObject.put("strDBUserName", objSetupHdModel.getStrDBUserName());
         				objJsonObject.put("strDBPassword",objSetupHdModel.getStrDBPassword());
         				objJsonObject.put("strDatabaseName",objSetupHdModel.getStrDatabaseName());
         				objJsonObject.put("strEnableKOTForDirectBiller", objSetupHdModel.getStrEnableKOTForDirectBiller());
         				objJsonObject.put("intPinCode",objSetupHdModel.getIntPinCode());
         				objJsonObject.put("strChangeTheme", objSetupHdModel.getStrChangeTheme());
         				objJsonObject.put("dblMaxDiscount",objSetupHdModel.getDblMaxDiscount());
         				objJsonObject.put("strAreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
         				objJsonObject.put("strMenuItemSortingOn",objSetupHdModel.getStrMenuItemSortingOn());
         				objJsonObject.put("strDirectAreaCode", objSetupHdModel.getStrDirectAreaCode());
         				objJsonObject.put("intColumnSize", objSetupHdModel.getIntColumnSize());
         				objJsonObject.put("strPrintType", objSetupHdModel.getStrPrintType());
         				objJsonObject.put("strEditHomeDelivery", objSetupHdModel.getStrEditHomeDelivery());
         				objJsonObject.put("strSlabBasedHDCharges", objSetupHdModel.getStrSlabBasedHDCharges());
         				objJsonObject.put("strSkipWaiterAndPax", objSetupHdModel.getStrSkipWaiterAndPax());
         				objJsonObject.put("strSkipWaiter",objSetupHdModel.getStrSkipWaiter());
         				objJsonObject.put("strDirectKOTPrintMakeKOT", objSetupHdModel.getStrDirectKOTPrintMakeKOT());
         				objJsonObject.put("strSkipPax",objSetupHdModel.getStrSkipPax());
         				objJsonObject.put("strCRMInterface",objSetupHdModel.getStrCRMInterface());
         				objJsonObject.put("strGetWebserviceURL",objSetupHdModel.getStrGetWebserviceURL());
         				objJsonObject.put("strPostWebserviceURL", objSetupHdModel.getStrPostWebserviceURL());
         			//	objJsonObject.put("gOutletUID",objSetupHdModel.getStrOutletUID());
         				objJsonObject.put("strPOSID",objSetupHdModel.getStrPOSID());
         				objJsonObject.put("strStockInOption",objSetupHdModel.getStrStockInOption());
         				objJsonObject.put("longCustSeries", objSetupHdModel.getStrCustSeries());
         				objJsonObject.put("intAdvReceiptPrintCount",objSetupHdModel.getIntAdvReceiptPrintCount());
         				objJsonObject.put("strHomeDeliverySMS",objSetupHdModel.getStrHomeDeliverySMS());
         				objJsonObject.put("strBillStettlementSMS", objSetupHdModel.getStrBillStettlementSMS());
           				objJsonObject.put("strBillFormatType",objSetupHdModel.getStrBillFormatType());
         				objJsonObject.put("strActivePromotions",objSetupHdModel.getStrActivePromotions());
         				objJsonObject.put("strSendHomeDelSMS",objSetupHdModel.getStrSendHomeDelSMS());
         				objJsonObject.put("strSendBillSettlementSMS", objSetupHdModel.getStrSendBillSettlementSMS());
         				objJsonObject.put("strSMSType", objSetupHdModel.getStrSMSType());
         				objJsonObject.put("strPrintShortNameOnKOT",objSetupHdModel.getStrPrintShortNameOnKOT());
         				objJsonObject.put("strShowCustHelp",objSetupHdModel.getStrShowCustHelp());
         				objJsonObject.put("strPrintOnVoidBill", objSetupHdModel.getStrPrintOnVoidBill());
         				objJsonObject.put("strPostSalesDataToMMS", objSetupHdModel.getStrPostSalesDataToMMS());
         				objJsonObject.put("strCustAreaMasterCompulsory",objSetupHdModel.getStrCustAreaMasterCompulsory());
         				objJsonObject.put("strPriceFrom",objSetupHdModel.getStrPriceFrom());
         				objJsonObject.put("strShowPrinterErrorMessage", objSetupHdModel.getStrShowPrinterErrorMessage());
         				objJsonObject.put("strTouchScreenMode", objSetupHdModel.getStrTouchScreenMode());
         				objJsonObject.put("strCardInterfaceType", objSetupHdModel.getStrCardInterfaceType());
         				objJsonObject.put("strCMSIntegrationYN",objSetupHdModel.getStrCMSIntegrationYN());
         				objJsonObject.put("strCMSWebServiceURL",objSetupHdModel.getStrCMSWebServiceURL());
         				objJsonObject.put("strChangeQtyForExternalCode", objSetupHdModel.getStrChangeQtyForExternalCode());
         				objJsonObject.put("strPointsOnBillPrint",objSetupHdModel.getStrPointsOnBillPrint());
         				objJsonObject.put("strCMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
         				objJsonObject.put("strManualAdvOrderNoCompulsory",objSetupHdModel.getStrManualAdvOrderNoCompulsory());
         				objJsonObject.put("strPrintManualAdvOrderNoOnBill", objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
         				objJsonObject.put("strPrintModifierQtyOnKOT",objSetupHdModel.getStrPrintModifierQtyOnKOT());
         				objJsonObject.put("strNoOfLinesInKOTPrint",objSetupHdModel.getStrNoOfLinesInKOTPrint());
         				objJsonObject.put("strMultipleKOTPrintYN",objSetupHdModel.getStrMultipleKOTPrintYN());
         				objJsonObject.put("strItemQtyNumpad", objSetupHdModel.getStrItemQtyNumpad());
         				objJsonObject.put("strTreatMemberAsTable", objSetupHdModel.getStrTreatMemberAsTable());
         				objJsonObject.put("strKOTToLocalPrinter",objSetupHdModel.getStrKOTToLocalPrinter());
         				objJsonObject.put("strSettleBtnForDirectBillerBill",objSetupHdModel.getStrSettleBtnForDirectBillerBill());
         				objJsonObject.put("strDelBoySelCompulsoryOnDirectBiller", objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
         				objJsonObject.put("strCMSMemberForKOTJPOS",objSetupHdModel.getStrCMSMemberForKOTJPOS());
         				objJsonObject.put("strCMSMemberForKOTMPOS", objSetupHdModel.getStrCMSMemberForKOTMPOS());
         				objJsonObject.put("strDontShowAdvOrderInOtherPOS",objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
         				objJsonObject.put("strPrintZeroAmtModifierInBill",objSetupHdModel.getStrPrintZeroAmtModifierInBill());
         				objJsonObject.put("strPrintKOTYN",objSetupHdModel.getStrPrintKOTYN());
         				objJsonObject.put("strCreditCardSlipNoCompulsoryYN",objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
         				objJsonObject.put("strCreditCardExpiryDateCompulsoryYN", objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
         				objJsonObject.put("strSelectWaiterFromCardSwipe",objSetupHdModel.getStrSelectWaiterFromCardSwipe());
         				objJsonObject.put("strMultiWaiterSelectionOnMakeKOT",objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
         				objJsonObject.put("strMoveTableToOtherPOS",objSetupHdModel.getStrMoveTableToOtherPOS());
         				objJsonObject.put("strMoveKOTToOtherPOS",objSetupHdModel.getStrMoveKOTToOtherPOS());
         				objJsonObject.put("strCalculateTaxOnMakeKOT",objSetupHdModel.getStrCalculateTaxOnMakeKOT());
         				objJsonObject.put("strReceiverEmailId", objSetupHdModel.getStrReceiverEmailId());
         				objJsonObject.put("strCalculateDiscItemWise",objSetupHdModel.getStrCalculateDiscItemWise());
         				objJsonObject.put("strTakewayCustomerSelection",objSetupHdModel.getStrTakewayCustomerSelection());
         				objJsonObject.put("StrShowItemStkColumnInDB",objSetupHdModel.getStrShowItemStkColumnInDB());
         				objJsonObject.put("strItemType",objSetupHdModel.getStrItemType());
         				objJsonObject.put("strAllowNewAreaMasterFromCustMaster", objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
         				objJsonObject.put("strCustAddressSelectionForBill", objSetupHdModel.getStrCustAddressSelectionForBill());
         				objJsonObject.put("strGenrateMI",objSetupHdModel.getStrGenrateMI());
         				objJsonObject.put("strFTPAddress", objSetupHdModel.getStrFTPAddress());
         				objJsonObject.put("strFTPServerUserName", objSetupHdModel.getStrFTPServerUserName());
         				objJsonObject.put("strFTPServerPass",objSetupHdModel.getStrFTPServerPass());
         				objJsonObject.put("strAllowToCalculateItemWeight", objSetupHdModel.getStrAllowToCalculateItemWeight());
         				objJsonObject.put("strShowBillsDtlType", objSetupHdModel.getStrShowBillsDtlType());
         				objJsonObject.put("strPrintTaxInvoiceOnBill", objSetupHdModel.getStrPrintTaxInvoiceOnBill());
         				objJsonObject.put("strPrintInclusiveOfAllTaxesOnBill",objSetupHdModel.getStrPrintInclusiveOfAllTaxesOnBill());
         				objJsonObject.put("strApplyDiscountOn",objSetupHdModel.getStrApplyDiscountOn());
         				objJsonObject.put("strMemberCodeForKotInMposByCardSwipe",objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
         				objJsonObject.put("strPrintBillYN", objSetupHdModel.getStrPrintBillYN());
         				objJsonObject.put("strVatAndServiceTaxFromPos",objSetupHdModel.getStrVatAndServiceTaxFromPos());
         				objJsonObject.put("strMemberCodeForMakeBillInMPOS",objSetupHdModel.getStrMemberCodeForMakeBillInMPOS());
         				objJsonObject.put("strItemWiseKOTYN", objSetupHdModel.getStrItemWiseKOTYN());
         				objJsonObject.put("strLastPOSForDayEnd", objSetupHdModel.getStrLastPOSForDayEnd());
         				objJsonObject.put("strCMSPostingType", objSetupHdModel.getStrCMSPostingType());
         				objJsonObject.put("strPopUpToApplyPromotionsOnBill",objSetupHdModel.getStrPopUpToApplyPromotionsOnBill());
         				objJsonObject.put("strSelectCustomerCodeFromCardSwipe", objSetupHdModel.getStrSelectCustomerCodeFromCardSwipe());
         				objJsonObject.put("strCheckDebitCardBalOnTransactions", objSetupHdModel.getStrCheckDebitCardBalOnTransactions());
         				objJsonObject.put("strSettlementsFromPOSMaster", objSetupHdModel.getStrSettlementsFromPOSMaster());
         				objJsonObject.put("strShiftWiseDayEndYN", objSetupHdModel.getStrShiftWiseDayEndYN());
         				objJsonObject.put("strProductionLinkup", objSetupHdModel.getStrProductionLinkup());
         				objJsonObject.put("strLockDataOnShift",objSetupHdModel.getStrLockDataOnShift());
         				objJsonObject.put("strWSClientCode", objSetupHdModel.getStrWSClientCode());
         				objJsonObject.put("strPOSCode", objSetupHdModel.getStrPOSCode());
         				objJsonObject.put("strEnableBillSeries",objSetupHdModel.getStrEnableBillSeries());
         				objJsonObject.put("strEnablePMSIntegrationYN",objSetupHdModel.getStrEnablePMSIntegrationYN());
         				objJsonObject.put("strPrintTimeOnBill", objSetupHdModel.getStrPrintTimeOnBill());
         				objJsonObject.put("strPrintTDHItemsInBill", objSetupHdModel.getStrPrintTDHItemsInBill());
         				objJsonObject.put("strPrintRemarkAndReasonForReprint",objSetupHdModel.getStrPrintRemarkAndReasonForReprint());
         			 
         				objJsonObject.put("intDaysBeforeOrderToCancel", objSetupHdModel.getIntDaysBeforeOrderToCancel());
         				objJsonObject.put("intNoOfDelDaysForAdvOrder", objSetupHdModel.getIntNoOfDelDaysForAdvOrder());
         				objJsonObject.put("intNoOfDelDaysForUrgentOrder", objSetupHdModel.getIntNoOfDelDaysForUrgentOrder());
         				objJsonObject.put("strSetUpToTimeForAdvOrder",objSetupHdModel.getStrSetUpToTimeForAdvOrder());
         				objJsonObject.put("strSetUpToTimeForUrgentOrder", objSetupHdModel.getStrSetUpToTimeForUrgentOrder());
         				objJsonObject.put("strUpToTimeForAdvOrder",objSetupHdModel.getStrUpToTimeForAdvOrder());
         				objJsonObject.put("strUpToTimeForUrgentOrder", objSetupHdModel.getStrUpToTimeForUrgentOrder());
         				objJsonObject.put("strEnableBothPrintAndSettleBtnForDB",objSetupHdModel.getStrEnableBothPrintAndSettleBtnForDB());
         				objJsonObject.put("strInrestoPOSIntegrationYN", objSetupHdModel.getStrInrestoPOSIntegrationYN());
         				objJsonObject.put("strInrestoPOSWebServiceURL", objSetupHdModel.getStrInrestoPOSWebServiceURL());
         				objJsonObject.put("strInrestoPOSId", objSetupHdModel.getStrInrestoPOSId());
         				objJsonObject.put("strInrestoPOSKey",objSetupHdModel.getStrInrestoPOSKey());
         				objJsonObject.put("strCarryForwardFloatAmtToNextDay", objSetupHdModel.getStrCarryForwardFloatAmtToNextDay());
         				objJsonObject.put("strOpenCashDrawerAfterBillPrintYN",objSetupHdModel.getStrOpenCashDrawerAfterBillPrintYN());
         				objJsonObject.put("strPropertyWiseSalesOrderYN",objSetupHdModel.getStrPropertyWiseSalesOrderYN());
         				objJsonObject.put("strDataPostFlag",objSetupHdModel.getStrDataPostFlag());
             			objJsonObject.put("strShowItemDetailsGrid", objSetupHdModel.getStrShowItemDetailsGrid());
         				
         				objJsonObject.put("strShowPopUpForNextItemQuantity", objSetupHdModel.getStrShowPopUpForNextItemQuantity());
         				
         				objJsonObject.put("strJioMoneyIntegration", objSetupHdModel.getStrJioMoneyIntegration());
         				objJsonObject.put("strJioWebServiceUrl",objSetupHdModel.getStrJioWebServiceUrl());
         				
         				objJsonObject.put("strJioMID", objSetupHdModel.getStrJioMID());
         				
         				objJsonObject.put("strJioTID", objSetupHdModel.getStrJioTID());
         				
         				objJsonObject.put("strJioActivationCode", objSetupHdModel.getStrJioActivationCode());
         				objJsonObject.put("strJioDeviceID",objSetupHdModel.getStrJioDeviceID());
         				objJsonObject.put("strNewBillSeriesForNewDay", objSetupHdModel.getStrNewBillSeriesForNewDay());
         				objJsonObject.put("strShowReportsPOSWise",objSetupHdModel.getStrShowReportsPOSWise());
             		   
         				objJsonObject.put("strEnableDineIn", objSetupHdModel.getStrEnableDineIn());
        				objJsonObject.put("strAutoAreaSelectionInMakeKOT",objSetupHdModel.getStrAutoAreaSelectionInMakeKOT());
        			
                         dataObjectArray.put(objJsonObject);
                     }
                    
                 }
                 else
                 {
                    JSONObject objJsonObject = new JSONObject();
                     
             		  
                     objJsonObject.put("strClientCode", objSetupHdModel.getStrClientCode());//clientCode
     				objJsonObject.put("strClientName",objSetupHdModel.getStrClientName());//clientName
     				objJsonObject.put("strAddressLine1", objSetupHdModel.getStrAddressLine1());
     				objJsonObject.put("strAddressLine2", objSetupHdModel.getStrAddressLine2());
     				objJsonObject.put("strAddressLine3", objSetupHdModel.getStrAddressLine3());
     				objJsonObject.put("strEmail", objSetupHdModel.getStrEmail());
     				objJsonObject.put("strBillFooter", objSetupHdModel.getStrBillFooter());
     				objJsonObject.put("strBillFooterStatus", objSetupHdModel.getStrBillFooterStatus());
         			objJsonObject.put("intBillPaperSize", objSetupHdModel.getIntBillPaperSize());
         			objJsonObject.put("strNegativeBilling", objSetupHdModel.getStrNegativeBilling());
     				objJsonObject.put("strDayEnd", objSetupHdModel.getStrDayEnd());
     				objJsonObject.put("strPrintMode", objSetupHdModel.getStrPrintMode());
     				objJsonObject.put("strDiscountNote", objSetupHdModel.getStrDiscountNote());
     				objJsonObject.put("strCityName", objSetupHdModel.getStrCityName());
     				objJsonObject.put("strState",objSetupHdModel.getStrState());
     				objJsonObject.put("strCountry",objSetupHdModel.getStrCountry());
     				objJsonObject.put("intTelephoneNo",objSetupHdModel.getIntTelephoneNo());
     				objJsonObject.put("gMobileNoForSMS",objSetupHdModel.getIntTelephoneNo());
     				objJsonObject.put("dteStartDate",objSetupHdModel.getDteStartDate());
     				objJsonObject.put("dteEndDate", objSetupHdModel.getDteEndDate());
     				objJsonObject.put("gEndTime",objSetupHdModel.getDteEndDate());
     				objJsonObject.put("strNatureOfBusinnes", objSetupHdModel.getStrNatureOfBusinnes());
     				objJsonObject.put("strMultipleBillPrinting",objSetupHdModel.getStrMultipleBillPrinting());
     				objJsonObject.put("strEnableKOT",objSetupHdModel.getStrEnableKOT());
     				objJsonObject.put("strEffectOnPSP",objSetupHdModel.getStrEffectOnPSP());
     				objJsonObject.put("strPrintVatNo", objSetupHdModel.getStrPrintVatNo());
     				objJsonObject.put("strVatNo",objSetupHdModel.getStrVatNo());
     				objJsonObject.put("strShowBill", objSetupHdModel.getStrShowBill());
     				objJsonObject.put("strPrintServiceTaxNo", objSetupHdModel.getStrPrintServiceTaxNo());
     				objJsonObject.put("strServiceTaxNo", objSetupHdModel.getStrServiceTaxNo());
     				objJsonObject.put("strManualBillNo", objSetupHdModel.getStrManualBillNo());
     				objJsonObject.put("strMenuItemDispSeq",objSetupHdModel.getStrMenuItemDispSeq());
     				objJsonObject.put("strSenderEmailId", objSetupHdModel.getStrSenderEmailId());
     				objJsonObject.put("strEmailPassword",objSetupHdModel.getStrEmailPassword());
     				objJsonObject.put("strConfirmEmailPassword",objSetupHdModel.getStrConfirmEmailPassword());
     				objJsonObject.put("strBody", objSetupHdModel.getStrBody());
     				objJsonObject.put("strEmailServerName",objSetupHdModel.getStrEmailServerName());
     				objJsonObject.put("strSMSApi", objSetupHdModel.getStrSMSApi());
     				objJsonObject.put("strUserCreated", objSetupHdModel.getStrUserCreated());
     				objJsonObject.put("strUserEdited", objSetupHdModel.getStrUserEdited());
     				objJsonObject.put("dteDateCreated", objSetupHdModel.getDteDateCreated());
     				objJsonObject.put("dteDateEdited", objSetupHdModel.getDteDateEdited());
     				objJsonObject.put("strPOSType", objSetupHdModel.getStrPOSType());
     				objJsonObject.put("strWebServiceLink",objSetupHdModel.getStrWebServiceLink());
     				objJsonObject.put("strDataSendFrequency",objSetupHdModel.getStrDataSendFrequency());
     				objJsonObject.put("dteHOServerDate",objSetupHdModel.getDteHOServerDate());
     				objJsonObject.put("strRFID",objSetupHdModel.getStrRFID());
     				objJsonObject.put("strServerName",objSetupHdModel.getStrServerName());
     				objJsonObject.put("strDBUserName", objSetupHdModel.getStrDBUserName());
     				objJsonObject.put("strDBPassword",objSetupHdModel.getStrDBPassword());
     				objJsonObject.put("strDatabaseName",objSetupHdModel.getStrDatabaseName());
     				objJsonObject.put("strEnableKOTForDirectBiller", objSetupHdModel.getStrEnableKOTForDirectBiller());
     				objJsonObject.put("intPinCode",objSetupHdModel.getIntPinCode());
     				objJsonObject.put("strChangeTheme", objSetupHdModel.getStrChangeTheme());
     				objJsonObject.put("dblMaxDiscount",objSetupHdModel.getDblMaxDiscount());
     				objJsonObject.put("strAreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
     				objJsonObject.put("strMenuItemSortingOn",objSetupHdModel.getStrMenuItemSortingOn());
     				objJsonObject.put("strDirectAreaCode", objSetupHdModel.getStrDirectAreaCode());
     				objJsonObject.put("intColumnSize", objSetupHdModel.getIntColumnSize());
     				objJsonObject.put("strPrintType", objSetupHdModel.getStrPrintType());
     				objJsonObject.put("strEditHomeDelivery", objSetupHdModel.getStrEditHomeDelivery());
     				objJsonObject.put("strSlabBasedHDCharges", objSetupHdModel.getStrSlabBasedHDCharges());
     				objJsonObject.put("strSkipWaiterAndPax", objSetupHdModel.getStrSkipWaiterAndPax());
     				objJsonObject.put("strSkipWaiter",objSetupHdModel.getStrSkipWaiter());
     				objJsonObject.put("strDirectKOTPrintMakeKOT", objSetupHdModel.getStrDirectKOTPrintMakeKOT());
     				objJsonObject.put("strSkipPax",objSetupHdModel.getStrSkipPax());
     				objJsonObject.put("strCRMInterface",objSetupHdModel.getStrCRMInterface());
     				objJsonObject.put("strGetWebserviceURL",objSetupHdModel.getStrGetWebserviceURL());
     				objJsonObject.put("strPostWebserviceURL", objSetupHdModel.getStrPostWebserviceURL());
     				objJsonObject.put("strOutletUID",objSetupHdModel.getStrOutletUID());
     				objJsonObject.put("strPOSID",objSetupHdModel.getStrPOSID());
     				objJsonObject.put("strStockInOption",objSetupHdModel.getStrStockInOption());
     				objJsonObject.put("longCustSeries", objSetupHdModel.getStrCustSeries());
     				objJsonObject.put("intAdvReceiptPrintCount",objSetupHdModel.getIntAdvReceiptPrintCount());
     				objJsonObject.put("strHomeDeliverySMS",objSetupHdModel.getStrHomeDeliverySMS());
     				objJsonObject.put("strBillStettlementSMS", objSetupHdModel.getStrBillStettlementSMS());
       				objJsonObject.put("strBillFormatType",objSetupHdModel.getStrBillFormatType());
     				objJsonObject.put("strActivePromotions",objSetupHdModel.getStrActivePromotions());
     				objJsonObject.put("strSendHomeDelSMS",objSetupHdModel.getStrSendHomeDelSMS());
     				objJsonObject.put("strSendBillSettlementSMS", objSetupHdModel.getStrSendBillSettlementSMS());
     				objJsonObject.put("strSMSType", objSetupHdModel.getStrSMSType());
     				objJsonObject.put("strPrintShortNameOnKOT",objSetupHdModel.getStrPrintShortNameOnKOT());
     				objJsonObject.put("strShowCustHelp",objSetupHdModel.getStrShowCustHelp());
     				objJsonObject.put("strPrintOnVoidBill", objSetupHdModel.getStrPrintOnVoidBill());
     				objJsonObject.put("strPostSalesDataToMMS", objSetupHdModel.getStrPostSalesDataToMMS());
     				objJsonObject.put("strCustAreaMasterCompulsory",objSetupHdModel.getStrCustAreaMasterCompulsory());
     				objJsonObject.put("strPriceFrom",objSetupHdModel.getStrPriceFrom());
     				objJsonObject.put("strShowPrinterErrorMessage", objSetupHdModel.getStrShowPrinterErrorMessage());
     				objJsonObject.put("strTouchScreenMode", objSetupHdModel.getStrTouchScreenMode());
     				objJsonObject.put("strCardInterfaceType", objSetupHdModel.getStrCardInterfaceType());
     				objJsonObject.put("strCMSIntegrationYN",objSetupHdModel.getStrCMSIntegrationYN());
     				objJsonObject.put("strCMSWebServiceURL",objSetupHdModel.getStrCMSWebServiceURL());
     				objJsonObject.put("strChangeQtyForExternalCode", objSetupHdModel.getStrChangeQtyForExternalCode());
     				objJsonObject.put("strPointsOnBillPrint",objSetupHdModel.getStrPointsOnBillPrint());
     				objJsonObject.put("strCMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
     				objJsonObject.put("strManualAdvOrderNoCompulsory",objSetupHdModel.getStrManualAdvOrderNoCompulsory());
     				objJsonObject.put("strPrintManualAdvOrderNoOnBill", objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
     				objJsonObject.put("strPrintModifierQtyOnKOT",objSetupHdModel.getStrPrintModifierQtyOnKOT());
     				objJsonObject.put("strNoOfLinesInKOTPrint",objSetupHdModel.getStrNoOfLinesInKOTPrint());
     				objJsonObject.put("strMultipleKOTPrintYN",objSetupHdModel.getStrMultipleKOTPrintYN());
     				objJsonObject.put("strItemQtyNumpad", objSetupHdModel.getStrItemQtyNumpad());
     				objJsonObject.put("strTreatMemberAsTable", objSetupHdModel.getStrTreatMemberAsTable());
     				objJsonObject.put("strKOTToLocalPrinter",objSetupHdModel.getStrKOTToLocalPrinter());
     				objJsonObject.put("strSettleBtnForDirectBillerBill",objSetupHdModel.getStrSettleBtnForDirectBillerBill());
     				objJsonObject.put("strDelBoySelCompulsoryOnDirectBiller", objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
     				objJsonObject.put("strCMSMemberForKOTJPOS",objSetupHdModel.getStrCMSMemberForKOTJPOS());
     				objJsonObject.put("strCMSMemberForKOTMPOS", objSetupHdModel.getStrCMSMemberForKOTMPOS());
     				objJsonObject.put("strDontShowAdvOrderInOtherPOS",objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
     				objJsonObject.put("strPrintZeroAmtModifierInBill",objSetupHdModel.getStrPrintZeroAmtModifierInBill());
     				objJsonObject.put("strPrintKOTYN",objSetupHdModel.getStrPrintKOTYN());
     				objJsonObject.put("strCreditCardSlipNoCompulsoryYN",objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
     				objJsonObject.put("strCreditCardExpiryDateCompulsoryYN", objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
     				objJsonObject.put("strSelectWaiterFromCardSwipe",objSetupHdModel.getStrSelectWaiterFromCardSwipe());
     				objJsonObject.put("strMultiWaiterSelectionOnMakeKOT",objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
     				objJsonObject.put("strMoveTableToOtherPOS",objSetupHdModel.getStrMoveTableToOtherPOS());
     				objJsonObject.put("strMoveKOTToOtherPOS",objSetupHdModel.getStrMoveKOTToOtherPOS());
     				objJsonObject.put("strCalculateTaxOnMakeKOT",objSetupHdModel.getStrCalculateTaxOnMakeKOT());
     				objJsonObject.put("strReceiverEmailId", objSetupHdModel.getStrReceiverEmailId());
     				objJsonObject.put("strCalculateDiscItemWise",objSetupHdModel.getStrCalculateDiscItemWise());
     				objJsonObject.put("strTakewayCustomerSelection",objSetupHdModel.getStrTakewayCustomerSelection());
     				objJsonObject.put("StrShowItemStkColumnInDB",objSetupHdModel.getStrShowItemStkColumnInDB());
     				objJsonObject.put("strItemType",objSetupHdModel.getStrItemType());
     				objJsonObject.put("strAllowNewAreaMasterFromCustMaster", objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
     				objJsonObject.put("strCustAddressSelectionForBill", objSetupHdModel.getStrCustAddressSelectionForBill());
     				objJsonObject.put("strGenrateMI",objSetupHdModel.getStrGenrateMI());
     				objJsonObject.put("strFTPAddress", objSetupHdModel.getStrFTPAddress());
     				objJsonObject.put("strFTPServerUserName", objSetupHdModel.getStrFTPServerUserName());
     				objJsonObject.put("strFTPServerPass",objSetupHdModel.getStrFTPServerPass());
     				objJsonObject.put("strAllowToCalculateItemWeight", objSetupHdModel.getStrAllowToCalculateItemWeight());
     				objJsonObject.put("strShowBillsDtlType", objSetupHdModel.getStrShowBillsDtlType());
     				objJsonObject.put("strPrintTaxInvoiceOnBill", objSetupHdModel.getStrPrintTaxInvoiceOnBill());
     				objJsonObject.put("strPrintInclusiveOfAllTaxesOnBill",objSetupHdModel.getStrPrintInclusiveOfAllTaxesOnBill());
     				objJsonObject.put("strApplyDiscountOn",objSetupHdModel.getStrApplyDiscountOn());
     				objJsonObject.put("strMemberCodeForKotInMposByCardSwipe",objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
     				objJsonObject.put("strPrintBillYN", objSetupHdModel.getStrPrintBillYN());
     				objJsonObject.put("strVatAndServiceTaxFromPos",objSetupHdModel.getStrVatAndServiceTaxFromPos());
     				objJsonObject.put("strMemberCodeForMakeBillInMPOS",objSetupHdModel.getStrMemberCodeForMakeBillInMPOS());
     				objJsonObject.put("strItemWiseKOTYN", objSetupHdModel.getStrItemWiseKOTYN());
     				objJsonObject.put("strLastPOSForDayEnd", objSetupHdModel.getStrLastPOSForDayEnd());
     				objJsonObject.put("strCMSPostingType", objSetupHdModel.getStrCMSPostingType());
     				objJsonObject.put("strPopUpToApplyPromotionsOnBill",objSetupHdModel.getStrPopUpToApplyPromotionsOnBill());
     				objJsonObject.put("strSelectCustomerCodeFromCardSwipe", objSetupHdModel.getStrSelectCustomerCodeFromCardSwipe());
     				objJsonObject.put("strCheckDebitCardBalOnTransactions", objSetupHdModel.getStrCheckDebitCardBalOnTransactions());
     				objJsonObject.put("strSettlementsFromPOSMaster", objSetupHdModel.getStrSettlementsFromPOSMaster());
     				objJsonObject.put("strShiftWiseDayEndYN", objSetupHdModel.getStrShiftWiseDayEndYN());
     				objJsonObject.put("strProductionLinkup", objSetupHdModel.getStrProductionLinkup());
     				objJsonObject.put("strLockDataOnShift",objSetupHdModel.getStrLockDataOnShift());
     				objJsonObject.put("strWSClientCode", objSetupHdModel.getStrWSClientCode());
     				objJsonObject.put("strPOSCode", objSetupHdModel.getStrPOSCode());
     				objJsonObject.put("strEnableBillSeries",objSetupHdModel.getStrEnableBillSeries());
     				objJsonObject.put("strEnablePMSIntegrationYN",objSetupHdModel.getStrEnablePMSIntegrationYN());
     				objJsonObject.put("strPrintTimeOnBill", objSetupHdModel.getStrPrintTimeOnBill());
     				objJsonObject.put("strPrintTDHItemsInBill", objSetupHdModel.getStrPrintTDHItemsInBill());
     				objJsonObject.put("strPrintRemarkAndReasonForReprint",objSetupHdModel.getStrPrintRemarkAndReasonForReprint());
     			 
     				objJsonObject.put("intDaysBeforeOrderToCancel", objSetupHdModel.getIntDaysBeforeOrderToCancel());
     				objJsonObject.put("intNoOfDelDaysForAdvOrder", objSetupHdModel.getIntNoOfDelDaysForAdvOrder());
     				objJsonObject.put("intNoOfDelDaysForUrgentOrder", objSetupHdModel.getIntNoOfDelDaysForUrgentOrder());
     				objJsonObject.put("strSetUpToTimeForAdvOrder",objSetupHdModel.getStrSetUpToTimeForAdvOrder());
     				objJsonObject.put("strSetUpToTimeForUrgentOrder", objSetupHdModel.getStrSetUpToTimeForUrgentOrder());
     				objJsonObject.put("strUpToTimeForAdvOrder",objSetupHdModel.getStrUpToTimeForAdvOrder());
     				objJsonObject.put("strUpToTimeForUrgentOrder", objSetupHdModel.getStrUpToTimeForUrgentOrder());
     				objJsonObject.put("strEnableBothPrintAndSettleBtnForDB",objSetupHdModel.getStrEnableBothPrintAndSettleBtnForDB());
     				objJsonObject.put("strInrestoPOSIntegrationYN", objSetupHdModel.getStrInrestoPOSIntegrationYN());
     				objJsonObject.put("strInrestoPOSWebServiceURL", objSetupHdModel.getStrInrestoPOSWebServiceURL());
     				objJsonObject.put("strInrestoPOSId", objSetupHdModel.getStrInrestoPOSId());
     				objJsonObject.put("strInrestoPOSKey",objSetupHdModel.getStrInrestoPOSKey());
     				objJsonObject.put("strCarryForwardFloatAmtToNextDay", objSetupHdModel.getStrCarryForwardFloatAmtToNextDay());
     				objJsonObject.put("strOpenCashDrawerAfterBillPrintYN",objSetupHdModel.getStrOpenCashDrawerAfterBillPrintYN());
     				objJsonObject.put("strPropertyWiseSalesOrderYN",objSetupHdModel.getStrPropertyWiseSalesOrderYN());
     				objJsonObject.put("strDataPostFlag",objSetupHdModel.getStrDataPostFlag());
         			objJsonObject.put("strShowItemDetailsGrid", objSetupHdModel.getStrShowItemDetailsGrid());
     				
     				objJsonObject.put("strShowPopUpForNextItemQuantity", objSetupHdModel.getStrShowPopUpForNextItemQuantity());
     				
     				objJsonObject.put("strJioMoneyIntegration", objSetupHdModel.getStrJioMoneyIntegration());
     				objJsonObject.put("strJioWebServiceUrl",objSetupHdModel.getStrJioWebServiceUrl());
     				
     				objJsonObject.put("strJioMID", objSetupHdModel.getStrJioMID());
     				
     				objJsonObject.put("strJioTID", objSetupHdModel.getStrJioTID());
     				
     				objJsonObject.put("strJioActivationCode", objSetupHdModel.getStrJioActivationCode());
     				objJsonObject.put("strJioDeviceID",objSetupHdModel.getStrJioDeviceID());
     				objJsonObject.put("strNewBillSeriesForNewDay", objSetupHdModel.getStrNewBillSeriesForNewDay());
     				objJsonObject.put("strShowReportsPOSWise",objSetupHdModel.getStrShowReportsPOSWise());
         		   
     				objJsonObject.put("strEnableDineIn", objSetupHdModel.getStrEnableDineIn());
    				objJsonObject.put("strAutoAreaSelectionInMakeKOT",objSetupHdModel.getStrAutoAreaSelectionInMakeKOT());
    			
                     dataObjectArray.put(objJsonObject);
                    
                 }
             }
         }
          }
               rootObject.put("tblsetup", dataObjectArray);
               
               clsPostPOSBillData obj=new clsPostPOSBillData();
               
               obj.funPostPropertySetup(rootObject);
}
catch (Exception e)
{
    e.printStackTrace();
}
	 }
	 private void funPostBillSeriesDataHO()
	 {
	        boolean flgResult = false;
	        StringBuilder sql = new StringBuilder();
	     
	        try
	        {
	            JSONObject rootObject = new JSONObject();
	            JSONArray dataObjectArray = new JSONArray();

	            sql.append("select * from tblbillseries where strDataPostFlag='N'");
	          Query  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
     			
    			 List list = query.list();
    			 for(int j=0; j<list.size();j++)
    	         {	Object[] obj=(Object[])list.get(j);
	                JSONObject dataObject = new JSONObject();
	                dataObject.put("POSCode", obj[0].toString());
	                dataObject.put("Type",  obj[1].toString());
	                dataObject.put("BillSeries", obj[2].toString());
	                dataObject.put("LastNo",  obj[3].toString());
	                dataObject.put("Codes",  obj[4].toString());
	                dataObject.put("Names",  obj[5].toString());
	                dataObject.put("UserCreated",  obj[6].toString());
	                dataObject.put("UserEdited",  obj[7].toString());
	                dataObject.put("DateCreated",  obj[8].toString());
	                dataObject.put("DateEdited", obj[9].toString());
	                dataObject.put("DataPostFlag",  obj[10].toString());
	                dataObject.put("ClientCode",  obj[11].toString());
	                dataObject.put("PropertyCode",  obj[12].toString());
	                dataObject.put("PrintGTOfOtherBills", obj[13].toString());
	                dataObject.put("PrintIncOfTaxOnBill", obj[14].toString());

	                dataObjectArray.put(dataObject);
	            }
	          
	            rootObject.put("tblbillseries", dataObjectArray);
	            clsPostPOSBillData obj=new clsPostPOSBillData();
	               
	               obj.funPostPropertySetup(rootObject);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	       
	    }
	 
	  public void funSetToken(String getToken,String posCode,String strJioMID)
	    {
	        // TODO add your handling code here:
	        try
	        {
	           
	          
	            String sql = "update tblsetup set strJioActivationCode='" + getToken + "' "
	                    + "where strPOSCode='" + posCode + "' and strJioMID='" + strJioMID + "'";
	            Query  querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     		    querySql.executeUpdate(); 

	        }
	        catch (Exception ex)
	        {

	        }
	    }
	

		@SuppressWarnings("finally")
		public JSONObject funGetPos(String newPropertyPOSCode)
		{
			List list =null;
			JSONObject jObjTableData=new JSONObject();
		       try
		        {
		        	  
		        	   String sqlBillSeries = "select count(*) from tblsetup where strPOSCode='" + newPropertyPOSCode + "' ";
		              Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlBillSeries);
						
						list = query.list();
					
						BigInteger count=new BigInteger("0");
						
								Object obj=(Object)list.get(0);
								count=(BigInteger) obj;
							
							
				          	jObjTableData.put("count", count);
					
		     	}catch(Exception ex)
				{
					ex.printStackTrace();
					
				}
				finally
				{
					return jObjTableData;
				}
		}
		
		public JSONObject funGetPOSClientCode()
		{
			List list =null;
			JSONObject jObjClientCode=new JSONObject();
		       try
		        {
					String sqlSetUpClientCode = "select DISTINCT a.strClientCode from tblsetup a ";
		            Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlSetUpClientCode);
						
					list = query.list();
					if(list.size()>0)
					{
						Object obj=(Object)list.get(0);
						String strClientCode=(String) obj;
						jObjClientCode.put("strClientCode", strClientCode);
						
					}else
					{
						jObjClientCode.put("strClientCode", "");
					}
						
		        }catch(Exception ex)
				{
					ex.printStackTrace();
					jObjClientCode.put("strClientCode", "");
					
				}
				finally
				{
					return jObjClientCode;
				}
		}
}
