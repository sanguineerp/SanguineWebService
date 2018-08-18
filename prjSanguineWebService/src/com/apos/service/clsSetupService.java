package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsSetupDao;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsPOSMasterModel_ID;
import com.apos.model.clsSetupHdModel;
import com.apos.model.clsSetupModel_ID;

import java.sql.Blob;

import com.webservice.util.clsUtilityFunctions;

@Service("clsSetupService")
public class clsSetupService
{
	@Autowired
	private clsSetupDao	objSetupDao;

	public JSONObject funGetPOSWiseSetup(String clientCode,String posCode)
	{
		return objSetupDao.funGetPOSWiseSetup(clientCode,posCode);
	}
	
	private List<clsSetupHdModel> funGetSetupForAllPOS(String clientCode)
	{
		return objSetupDao.funGetSetupForAllPOS(clientCode);
	}

	public JSONObject funGetParameterValuePOSWise(String clientCode, String posCode, String parameterName)
	{
		return objSetupDao.funGetParameterValuePOSWise(clientCode,posCode,parameterName);
	}
	
	public JSONObject funGetAllParameterValuesPOSWise(String clientCode, String posCode)
	{
		return objSetupDao.funGetAllParameterValuesPOSWise(clientCode,posCode);
	}
	
	public JSONObject funGetPrinterDtl()
	{
		return objSetupDao.funGetPrinterDtl();
	}
	
	public JSONObject funGetOldSBillSeriesSetup(String newPropertyPOSCode)
	{
		return objSetupDao.loadOldSBillSeriesSetup(newPropertyPOSCode);
	}

	public JSONObject funGetOldBillSeries(String newPropertyPOSCode)
	{
		return objSetupDao.loadOldBillSeries(newPropertyPOSCode);
	}
	
	public void funSetToken(String token, String posCode, String mid)
	{
		 objSetupDao.funSetToken(token,posCode,mid);
	}
	
	public JSONObject funGetPos(String newPropertyPOSCode)
	{
		return objSetupDao.funGetPos(newPropertyPOSCode);
	}

	public String funSaveUpdatePropertySetup(JSONObject jObj){
		String posCode = "";
		try
		{
		    String strType=jObj.getString("strBillSeriesType");
			posCode = jObj.getString("strPosCode");
		 String  user = jObj.getString("User");
		    String clientCode = jObj.getString("strClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
	    
		   
		    clsSetupHdModel objModel=new clsSetupHdModel(new clsSetupModel_ID(clientCode,posCode));

		    if(jObj.has("ClientImage"))
		    {
			 String strContent = (String)jObj.get("ClientImage");
			    byte[] byteContent = strContent.getBytes();
			    Blob blob = Hibernate.createBlob(byteContent);
		    objModel.setBlobReportImage(blob);
		}
		    objModel.setStrActivePromotions( jObj.getString("chkActivePromotions"));
		    objModel.setDblMaxDiscount( (int) jObj.get("dblMaxDiscount"));
		    objModel.setDteEndDate( jObj.getString("dteEndDate"));
		    objModel.setDteHOServerDate( jObj.getString("dteHOServerDate"));
		    objModel.setDteStartDate( dateTime);
		    objModel.setIntAdvReceiptPrintCount( (int) jObj.get("strAdvRecPrintCount"));
		    objModel.setIntBillPaperSize( (int) jObj.get("intBiilPaperSize"));
		    objModel.setIntColumnSize( (int) jObj.get("intColumnSize"));
		    objModel.setIntDaysBeforeOrderToCancel( (int) jObj.get("intDaysBeforeOrderToCancel"));
		    objModel.setIntNoOfDelDaysForAdvOrder( (int) jObj.get("intNoOfDelDaysForAdvOrder"));
		    objModel.setIntNoOfDelDaysForUrgentOrder( (int) jObj.get("intNoOfDelDaysForUrgentOrder"));
		    objModel.setIntPinCode( (int) jObj.get("strPinCode"));
		    objModel.setIntTelephoneNo( (int) jObj.get("strTelephone"));
		    objModel.setStrAddressLine1( jObj.getString("strAddrLine1"));
		    objModel.setStrAddressLine2( jObj.getString("strAddrLine2"));
		    objModel.setStrAddressLine3( jObj.getString("strAddrLine3"));
		    objModel.setStrAllowNewAreaMasterFromCustMaster( jObj.getString("chkBoxAllowNewAreaMasterFromCustMaster"));
		    objModel.setStrAllowToCalculateItemWeight( jObj.getString("chkAllowToCalculateItemWeight"));
		    objModel.setStrApplyDiscountOn( jObj.getString("strApplyDiscountOn"));
		    objModel.setStrAreaWisePricing( jObj.getString("chkAreaWisePricing"));
		    objModel.setStrBillFooter( jObj.getString("strBillFooter"));
		    objModel.setStrBillFooterStatus( "N");
		    objModel.setStrBillFormatType( jObj.getString("strBillFormat"));
		    objModel.setStrBillStettlementSMS( jObj.getString("strAreaBillSettlementSMS"));
		    objModel.setStrBody( jObj.getString("strBodyPart"));
		    objModel.setStrCalculateDiscItemWise( jObj.getString("chkCalculateDiscItemWise"));
		    objModel.setStrCalculateTaxOnMakeKOT( jObj.getString("chkCalculateTaxOnMakeKOT"));
		    objModel.setStrCardInterfaceType( jObj.getString("strCardIntfType"));
		    objModel.setStrCarryForwardFloatAmtToNextDay( jObj.getString("chkCarryForwardFloatAmtToNextDay"));
		    objModel.setStrChangeQtyForExternalCode( jObj.getString("chkChangeQtyForExternalCode"));
		    objModel.setStrChangeTheme( jObj.getString("strChangeTheme"));
		    objModel.setStrCheckDebitCardBalOnTransactions( jObj.getString("chkCheckDebitCardBalOnTrans"));
		    objModel.setStrCityName( jObj.getString("strCity"));
		    objModel.setStrClientCode( jObj.getString("strClientCode"));
		    objModel.setStrClientName( jObj.getString("strClientName"));
		    objModel.setStrCMSIntegrationYN( jObj.getString("strCMSIntegrationYN"));
		    objModel.setStrCMSMemberForKOTJPOS( jObj.getString("chkMemberCodeForKOTJPOS"));
		    objModel.setStrCMSMemberForKOTMPOS( jObj.getString("chkMemberCodeForKOTMPOS"));
		    objModel.setStrCMSPOSCode( jObj.getString("strPosCode"));
		    objModel.setStrCMSPostingType( jObj.getString("strCMSPostingType"));
		    objModel.setStrCMSWebServiceURL( jObj.getString("strCMSWesServiceURL"));
		    objModel.setStrConfirmEmailPassword( jObj.getString("strEmailPassword"));
		    objModel.setStrCountry( jObj.getString("strCountry"));
		    objModel.setStrCreditCardExpiryDateCompulsoryYN( jObj.getString("chkExpDateForCreditCardBillYN"));
		    objModel.setStrCreditCardSlipNoCompulsoryYN( jObj.getString("chkSlipNoForCreditCardBillYN"));
		    objModel.setStrCRMInterface( jObj.getString("strCRM"));
		    objModel.setStrCustAddressSelectionForBill( jObj.getString("chkSelectCustAddressForBill"));
		    objModel.setStrCustAreaMasterCompulsory( jObj.getString("chkAreaMasterCompulsory"));
		    objModel.setStrCustSeries( jObj.getString("strCustSeries"));
		    objModel.setStrDatabaseName( jObj.getString("strRFIDDatabaseName"));
		    objModel.setStrDataPostFlag( "Y");
		    objModel.setStrDataSendFrequency( jObj.getString("strDataSendFrequency"));
		    objModel.setStrDayEnd( jObj.getString("chkDayEnd"));
		    objModel.setStrDBPassword( jObj.getString("strRFIDPassword"));
		    objModel.setStrDBUserName( jObj.getString("strRFIDUserName"));
		    objModel.setStrDelBoySelCompulsoryOnDirectBiller( jObj.getString("chkDelBoyCompulsoryOnDirectBiller"));
		    objModel.setStrDirectAreaCode( jObj.getString("strDirectArea"));
		    objModel.setStrDirectKOTPrintMakeKOT( jObj.getString("chkDirectKOTPrintMakeKOT"));
		    objModel.setStrDiscountNote( "N");
		    objModel.setStrDontShowAdvOrderInOtherPOS( jObj.getString("chkDontShowAdvOrderInOtherPOS"));
		    objModel.setStrEditHomeDelivery( jObj.getString("chkEditHomeDelivery"));
		    objModel.setStrEffectOnPSP( jObj.getString("chkEffectOnPSP"));
		    objModel.setStrEmail( jObj.getString("strEmail"));
		    objModel.setStrEmailPassword( jObj.getString("strEmailPassword"));
		    objModel.setStrEmailServerName( jObj.getString("strEmailServerName"));
		    objModel.setStrEnableBillSeries( jObj.getString("chkEnableBillSeries"));
		    objModel.setStrEnableBothPrintAndSettleBtnForDB( jObj.getString("chkEnableBothPrintAndSettleBtnForDB"));
		    objModel.setStrEnableKOT( jObj.getString("chkEnableKOT"));
		    objModel.setStrEnableKOTForDirectBiller( jObj.getString("chkPrintKotForDirectBiller"));
		    objModel.setStrEnablePMSIntegrationYN( jObj.getString("chkEnablePMSIntegration"));
		    objModel.setStrFTPAddress( jObj.getString("strFTPAddress"));
		    objModel.setStrFTPServerPass( jObj.getString("strFTPServerPass"));
		    objModel.setStrFTPServerUserName( jObj.getString("strFTPServerUserName"));
		    objModel.setStrGenrateMI( jObj.getString("chkGenrateMI"));
		    objModel.setStrGetWebserviceURL( jObj.getString("strGetWebservice"));
		    objModel.setStrHomeDeliverySMS( jObj.getString("strAreaSendHomeDeliverySMS"));
		    objModel.setStrInrestoPOSId( jObj.getString("strInrestoPOSId"));
		    objModel.setStrInrestoPOSIntegrationYN( jObj.getString("strInrestoPOSIntegrationYN"));
		    objModel.setStrInrestoPOSKey( jObj.getString("strInrestoPOSKey"));
		    objModel.setStrInrestoPOSWebServiceURL( jObj.getString("strInrestoPOSWesServiceURL"));
		    objModel.setStrItemQtyNumpad( jObj.getString("chkItemQtyNumpad"));
		    objModel.setStrItemType( jObj.getString("strItemType"));
		    objModel.setStrItemWiseKOTYN( jObj.getString("chkItemWiseKOTPrintYN"));
		    objModel.setStrJioActivationCode( jObj.getString("strJioActivationCode"));
		    objModel.setStrJioDeviceID( jObj.getString("strJioDeviceID"));
		    objModel.setStrJioMID( jObj.getString("strJioMID"));
		    objModel.setStrJioMoneyIntegration( jObj.getString("strJioPOSIntegrationYN"));
		    objModel.setStrJioTID( jObj.getString("strJioTID"));
		    objModel.setStrJioWebServiceUrl( jObj.getString("strJioPOSWesServiceURL"));
		    objModel.setStrKOTToLocalPrinter( jObj.getString("chkPrintKOTToLocalPrinter"));
		    objModel.setStrLastPOSForDayEnd( jObj.getString("strPOSForDayEnd"));
		    objModel.setStrLockDataOnShift( jObj.getString("chkLockDataOnShift"));
		    objModel.setStrManualAdvOrderNoCompulsory( jObj.getString("chkManualAdvOrderCompulsory"));
		    objModel.setStrManualBillNo( jObj.getString("chkManualBillNo"));
		    objModel.setStrMemberCodeForKotInMposByCardSwipe( jObj.getString("chkMemberCodeForKotInMposByCardSwipe"));
		    objModel.setStrMemberCodeForMakeBillInMPOS( jObj.getString("chkMemberCodeForMakeBillInMPOS"));
		    objModel.setStrMenuItemDispSeq( jObj.getString("strMenuItemDisSeq"));
		    objModel.setStrMenuItemSortingOn( jObj.getString("strMenuItemSortingOn"));
		    objModel.setStrMoveTableToOtherPOS( jObj.getString("chkMoveTableToOtherPOS"));
		    objModel.setStrMoveKOTToOtherPOS( jObj.getString("chkMoveKOTToOtherPOS"));
		    objModel.setStrMultipleBillPrinting( jObj.getString("chkMultiBillPrint"));
		    objModel.setStrMultipleKOTPrintYN( jObj.getString("chkMultiKOTPrint"));
		    objModel.setStrMultiWaiterSelectionOnMakeKOT( jObj.getString("chkMultipleWaiterSelectionOnMakeKOT"));
		    objModel.setStrNatureOfBusinnes( jObj.getString("strNatureOfBussness"));
		    objModel.setStrNegativeBilling( jObj.getString("chkNegBilling"));
		    objModel.setStrNewBillSeriesForNewDay( jObj.getString("chkNewBillSeriesForNewDay"));
		    objModel.setStrNoOfLinesInKOTPrint( (int) jObj.get("intNoOfLinesInKOTPrint"));
		    objModel.setStrOpenCashDrawerAfterBillPrintYN( jObj.getString("chkOpenCashDrawerAfterBillPrint"));
		    objModel.setStrOutletUID( jObj.getString("strOutletUID"));
		    objModel.setStrPointsOnBillPrint( jObj.getString("chkPointsOnBillPrint"));
		    objModel.setStrPopUpToApplyPromotionsOnBill( jObj.getString("chkPopUpToApplyPromotionsOnBill"));
		    objModel.setStrPOSCode( jObj.getString("strPosCode"));
		    objModel.setStrPOSID( jObj.getString("strPOSID"));
		    objModel.setStrPostSalesDataToMMS( jObj.getString("chkPostSalesDataToMMS"));
		    objModel.setStrPostWebserviceURL( jObj.getString("strPostWebservice"));
		    objModel.setStrPOSType( jObj.getString("strPOSType"));
		    objModel.setStrPriceFrom( jObj.getString("strPriceFrom"));
		    objModel.setStrPrintBillYN( jObj.getString("chkPrintBill"));
		    objModel.setStrPrintInclusiveOfAllTaxesOnBill( jObj.getString("chkPrintInclusiveOfAllTaxesOnBill"));
		    objModel.setStrPrintKOTYN( jObj.getString("chkPrintKOTYN"));
		    objModel.setStrPrintManualAdvOrderNoOnBill( jObj.getString("chkPrintManualAdvOrderOnBill"));
		    objModel.setStrPrintMode( jObj.getString("strBillPrintMode"));
		    objModel.setStrPrintModifierQtyOnKOT( jObj.getString("chkPrintModifierQtyOnKOT"));
		    objModel.setStrPrintOnVoidBill( jObj.getString("chkPrintForVoidBill"));
		    objModel.setStrPrintRemarkAndReasonForReprint( jObj.getString("chkPrintRemarkAndReasonForReprint"));
		    objModel.setStrPrintServiceTaxNo( jObj.getString("chkServiceTaxNo"));
		    objModel.setStrPrintShortNameOnKOT( jObj.getString("chkPrintShortNameOnKOT"));
		    objModel.setStrPrintTaxInvoiceOnBill( jObj.getString("chkPrintInvoiceOnBill"));
		    objModel.setStrPrintTDHItemsInBill( jObj.getString("chkPrintTDHItemsInBill"));
		    objModel.setStrPrintTimeOnBill( jObj.getString("chkPrintTimeOnBill"));
		    objModel.setStrPrintType( jObj.getString("strPrintingType"));
		    objModel.setStrPrintVatNo( jObj.getString("chkPrintVatNo"));
		    objModel.setStrPrintZeroAmtModifierInBill( jObj.getString("chkPrintZeroAmtModifierInBill"));
		    objModel.setStrProductionLinkup( jObj.getString("chkProductionLinkup"));
		    objModel.setStrPropertyWiseSalesOrderYN( jObj.getString("chkPropertyWiseSalesOrder"));
		    objModel.setStrReceiverEmailId( jObj.getString("strReceiverEmailId"));
		    objModel.setStrRFID( jObj.getString("strRFIDSetup"));
		    objModel.setStrSelectCustomerCodeFromCardSwipe( jObj.getString("chkSelectCustomerCodeFromCardSwipe"));
		    objModel.setStrSelectWaiterFromCardSwipe( jObj.getString("chkSelectWaiterFromCardSwipe"));
		    objModel.setStrSendBillSettlementSMS( jObj.getString("chkBillSettlementSMS"));
		    objModel.setStrSenderEmailId( jObj.getString("strSenderEmailId"));
		    objModel.setStrSendHomeDelSMS( jObj.getString("chkHomeDelSMS"));
		    objModel.setStrServerName( jObj.getString("strRFIDServerName"));
		    objModel.setStrServiceTaxNo( jObj.getString("strServiceTaxNo"));
		    objModel.setStrSettleBtnForDirectBillerBill( jObj.getString("chkEnableSettleBtnForDirectBillerBill"));
		    objModel.setStrSettlementsFromPOSMaster( jObj.getString("chkSettlementsFromPOSMaster"));
		    objModel.setStrSetUpToTimeForAdvOrder( jObj.getString("chkSetUpToTimeForAdvOrder"));
		    objModel.setStrSetUpToTimeForUrgentOrder( jObj.getString("chkSetUpToTimeForUrgentOrder"));
		    objModel.setStrShiftWiseDayEndYN( jObj.getString("chkShiftWiseDayEnd"));
		    objModel.setStrShowBill( jObj.getString("chkShowBills"));
		    objModel.setStrShowBillsDtlType( jObj.getString("strShowBillsDtlType"));
		    objModel.setStrShowCustHelp( "N");
		    objModel.setStrShowItemDetailsGrid( jObj.getString("chkShowItemDtlsForChangeCustomerOnBill"));
		    objModel.setStrShowItemStkColumnInDB( jObj.getString("chkShowItemStkColumnInDB"));
		    objModel.setStrShowPopUpForNextItemQuantity( jObj.getString("chkShowPopUpForNextItemQuantity"));
		    objModel.setStrShowPrinterErrorMessage( jObj.getString("chkPrinterErrorMessage"));
		    objModel.setStrShowReportsPOSWise( jObj.getString("chkShowReportsPOSWise"));
		    objModel.setStrSkipPax( jObj.getString("chkSkipPaxSelection"));
		    objModel.setStrSkipWaiter( jObj.getString("chkSkipWaiterSelection"));
		    objModel.setStrSkipWaiterAndPax( "N");
		    objModel.setStrSlabBasedHDCharges( jObj.getString("chkSlabBasedHomeDelCharges"));
		    objModel.setStrSMSApi( jObj.getString("strAreaSMSApi"));
		    objModel.setStrSMSType( jObj.getString("strSMSType"));
		    objModel.setStrState( jObj.getString("strState"));
		    objModel.setStrStockInOption( jObj.getString("strStockInOption"));
		    objModel.setStrTakewayCustomerSelection( jObj.getString("chkTakewayCustomerSelection"));
		    objModel.setStrTouchScreenMode( "N");
		    objModel.setStrTreatMemberAsTable( jObj.getString("chkMemberAsTable"));
		    objModel.setStrUpToTimeForAdvOrder( jObj.getString("strUpToTimeForAdvOrder"));
		    objModel.setStrUpToTimeForUrgentOrder( jObj.getString("strUpToTimeForUrgentOrder"));
		    objModel.setStrVatAndServiceTaxFromPos( jObj.getString("chkUseVatAndServiceNoFromPos")); 
		    objModel.setStrVatNo( jObj.getString("strVatNo"));
		    objModel.setStrWebServiceLink( jObj.getString("strWebServiceLink"));
		    objModel.setStrWSClientCode( jObj.getString("strWSClientCode"));
		
		    objModel.setStrEnableDineIn( jObj.getString("chkEnableDineIn"));
		    objModel.setStrAutoAreaSelectionInMakeKOT( jObj.getString("chkAutoAreaSelectionInMakeKOT"));
		
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		 
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    
		    objSetupDao.funSaveUpdatePropertySetup(objModel);
		    
		    JSONArray settleList=jObj.getJSONArray("PrinterDetails");
		    
		    objSetupDao.funSavePrinterSetupData(settleList,dateTime,clientCode,user);
		    
		    JSONArray reorderTimeList=jObj.getJSONArray("BillSeriesDetails");
		    
		    objSetupDao.funSaveBillSeries(reorderTimeList,strType,posCode,dateTime,clientCode,user);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return posCode; 
	    }
	
	public JSONObject funGetPOSClientCode()
	{
		return objSetupDao.funGetPOSClientCode();
	}
	
	
}
