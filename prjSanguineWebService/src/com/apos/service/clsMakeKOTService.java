package com.apos.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMakeKOTDao;
import com.apos.model.clsMakeKOTHdModel;
import com.apos.model.clsMakeKOTModel_ID;
import com.apos.model.clsNonChargableKOTHdModel;
import com.apos.model.clsNonChargableKOTModel_ID;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsPOSMasterModel_ID;
import com.webservice.util.clsUtilityFunctions;


	@Service("clsMakeKOTService")
	public class clsMakeKOTService {

		@Autowired
		clsMakeKOTDao	objMakeKOTDao;

		public JSONObject funLoadTableDtl(String clientCode,String posCode)
		{
			return objMakeKOTDao.funLoadTableDtl(clientCode,posCode);
		}

		public JSONObject funGetWaiterList(String posCode)
		{
			return objMakeKOTDao.funGetWaiterList(posCode);
		}

		public JSONObject funGetButttonList(String transName,String posCode,String posClientCode)
		{
			return objMakeKOTDao.funGetButttonList(transName,posCode,posClientCode);
		}

		public JSONObject funChekReservation(String tableNo)
		{
			return objMakeKOTDao.funChekReservation(tableNo);
		}

		public JSONObject funChekCMSCustomerDtl(String tableNo)
		{
			return objMakeKOTDao.funChekCMSCustomerDtl(tableNo);
		}
		
		public JSONObject funChekCustomerDtl(String clientCode,String posCode,String tableNo)
		{
			return objMakeKOTDao.funChekCustomerDtl(clientCode,posCode,tableNo);
		}
		
		public JSONObject funChekCardDtl(String tableNo)
		{
			return objMakeKOTDao.funChekCardDtl(tableNo);
		}
		public JSONObject funGetItemPricingDtl(String clientCode,String posDate ,String posCode)
		{
			return objMakeKOTDao.funGetItemPricingDtl(clientCode,posDate,posCode);
		}
		
		public JSONObject funFillOldKOTItems(String clientCode,String posDate,String tableNo,String posCode)
		{
			return objMakeKOTDao.funFillOldKOTItems(clientCode,posDate,tableNo,posCode);
		}
		
		public JSONObject funCheckDebitCardString(String debitCardString,String posCode,String clientCode)
		{
			return objMakeKOTDao.funCheckDebitCardString(debitCardString,posCode,clientCode);
		}

		public JSONObject funGetMenuHeads(String posCode,String userCode)
		{
			return objMakeKOTDao.funGetMenuHeads(posCode,userCode);
		}
		
		public JSONObject funPopularItem(String clientCode,String posDate ,String posCode)
		{
			return objMakeKOTDao.funPopularItem(clientCode,posDate,posCode);
		}
		
		public JSONObject funFillTopButtonList(String menuHeadCode,String posCode,String posDate,String clientCode)
		{
			return objMakeKOTDao.funFillTopButtonList(menuHeadCode,posCode,posDate,clientCode);
		}
		
		public JSONObject funCheckHomeDelivery(String strTableNo,String posCode)
		{
			return objMakeKOTDao.funCheckHomeDelivery(strTableNo,posCode);
		}
		
		public JSONObject funFillitemsSubMenuWise(String strMenuCode,String flag,String selectedButtonCode,String posCode,String posDate,String clientCode)
		{
			return objMakeKOTDao.funFillitemsSubMenuWise(strMenuCode,flag,selectedButtonCode,posCode,posDate,clientCode);
		}
		
		public JSONObject funFillMapWithHappyHourItems(String posCode,String posDate,String clientCode)
		{
			return objMakeKOTDao.funFillMapWithHappyHourItems(posCode,posDate,clientCode);
		}
		
		public JSONObject funGenerateKOTNo()
		{
			return objMakeKOTDao.funGenerateKOTNo();
		}
		
		public JSONObject funCheckMemeberBalance(String strCustomerCode)
		{
			return objMakeKOTDao.funCheckMemeberBalance(strCustomerCode);
		}
		
		public JSONObject funChekCRMCustomerDtl(String strMobNo)
		{
			return objMakeKOTDao.funChekCRMCustomerDtl(strMobNo);
		}
		
		public JSONObject funCheckCustomer(String strMobNo)
		{
			return objMakeKOTDao.funCheckCustomer(strMobNo);
		}
		
		public JSONObject funCheckKOTSave(String strKOTNo)
		{
			return objMakeKOTDao.funCheckKOTSave(strKOTNo);
		}
		
		public JSONObject funFillTopSortingButtonsForModifier(String itemCode)
		{
			return objMakeKOTDao.funFillTopSortingButtonsForModifier(itemCode);
		}
		
		public JSONObject funGetModifierAll(String itemCode)
		{
			return objMakeKOTDao.funGetModifierAll(itemCode);
		}
		
		public JSONObject funCalculateTax(JSONArray arrKOTItemDtlList,String clientCode,String posCode,String posDate)
		{
			return objMakeKOTDao.funCalculateTax(arrKOTItemDtlList,clientCode,posCode,posDate);
		}
		
		public JSONObject funGetCustomerAddress(String mobNo)
		{
			return objMakeKOTDao.funGetCustomerAddress(mobNo);
		}
		
		public void funUpdateCustomerTempAddress(String strTempCustAddress, String strTempStreetName,String strTempLandmark,String strMobileNo)
		{
			 objMakeKOTDao.funUpdateCustomerTempAddress(strTempCustAddress,strTempStreetName,strTempLandmark,strMobileNo);
		}
		
		public String funSaveUpdateKOT(JSONObject jObjPOSMaster){
			  String strKOTNo="";
			try
			{
			    strKOTNo = jObjPOSMaster.getString("strKOTNo");
			    String strTableNo = jObjPOSMaster.getString("strTableNo");
			    String strCustomerCode = jObjPOSMaster.getString("cmsMemberCode");
			    String strCustomerName = jObjPOSMaster.getString("cmsMemberName");
			  
				String strCardNo = jObjPOSMaster.getString("strDeditCardNo");
			    String strReasonCode = jObjPOSMaster.getString("strReasonCode");
			  
			   String strDelBoyCode = jObjPOSMaster.getString("strDelBoyCode");
			    String strHomeDelivery = jObjPOSMaster.getString("strHomeDelivery");
			  
			    
			    String strNCKotYN = jObjPOSMaster.getString("strNCKotYN");
			    String strPOSCode = jObjPOSMaster.getString("strPOSCode");
			    String strWaiterNo = jObjPOSMaster.getString("strWaiter");
			    String strTakeAwayYesNo = jObjPOSMaster.getString("strTakeAwayYesNo");
			  
			    double dblRedeemAmt = jObjPOSMaster.getDouble("strDeditCardBalance");
			    int intPaxNo = jObjPOSMaster.getInt("intPaxNo");
			   
			    String  user = jObjPOSMaster.getString("User");
			    String strClientCode= jObjPOSMaster.getString("strClientCode");
			    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
			    JSONArray billItemDtlList=jObjPOSMaster.getJSONArray("billItemDtlList");
				   
			    
			    for(int i=1;i<billItemDtlList.length();i++)
			    {
			    	if(billItemDtlList.get(i)!=null)
			    	{
			    	JSONObject jObj=billItemDtlList.getJSONObject(i);
			    double dblAmount = jObj.getDouble("dblAmount");
			    String strSerialNo = jObj.getString("strSerialNo");
			    double dblItemQuantity = jObj.getDouble("dblQuantity");
			    String strItemCode = jObj.getString("strItemCode");
			    String strItemName = jObj.getString("strItemName");
			    clsMakeKOTHdModel objModel=new clsMakeKOTHdModel(new clsMakeKOTModel_ID(strSerialNo, strTableNo, strItemCode, strItemName, strKOTNo));
			   
			    objModel.setStrActiveYN("");
			    objModel.setStrCardNo(strCardNo);
			    objModel.setStrCardType(" ");
			    objModel.setStrCounterCode(objMakeKOTDao.strCounterCode);
			    objModel.setStrCustomerCode(strCustomerCode);
			    objModel.setStrCustomerName(strCustomerName);
			    objModel.setStrDelBoyCode(strDelBoyCode);
			    objModel.setStrHomeDelivery(strHomeDelivery);
			    
			    objModel.setStrManualKOTNo(" ");
			    objModel.setStrNCKotYN(strNCKotYN);
			    objModel.setStrOrderBefore(" ");
			    objModel.setStrPOSCode(strPOSCode);
			    objModel.setStrPrintYN("N");
			    objModel.setStrPromoCode(" ");
			    objModel.setStrReason(strReasonCode);
			    objModel.setStrWaiterNo(strWaiterNo);
			    objModel.setStrTakeAwayYesNo(strTakeAwayYesNo);
			    objModel.setDblAmount(dblAmount);
			    objModel.setDblBalance(0.00);
			    objModel.setDblCreditLimit(0.00);
			    objModel.setDblItemQuantity(dblItemQuantity);
			    objModel.setDblRate(dblAmount/dblItemQuantity);
			    objModel.setDblRedeemAmt(dblRedeemAmt);
			    objModel.setDblTaxAmt(objMakeKOTDao.taxAmt);
			    objModel.setIntId(0);
			    objModel.setIntPaxNo(intPaxNo);
			    
			    objModel.setDteDateCreated(dateTime);
			    objModel.setDteDateEdited(dateTime);
			 
			    objModel.setStrUserCreated(user);
			    objModel.setStrUserEdited(user);
			    
			    objMakeKOTDao.funSaveKOT(objModel);
			    if ("Y".equals(strNCKotYN))
	            {
	             
			    clsNonChargableKOTHdModel objNCModel=new clsNonChargableKOTHdModel(new clsNonChargableKOTModel_ID( strTableNo, strItemCode, strKOTNo));
				 
			    objNCModel.setDblQuantity(dblItemQuantity);
			    objNCModel.setDblRate(dblAmount/dblItemQuantity);
			    objNCModel.setDteNCKOTDate(dateTime);
			    objNCModel.setStrClientCode(strClientCode);
			    objNCModel.setStrDataPostFlag("Y");
			    objNCModel.setStrEligibleForVoid("Y");
			    objNCModel.setStrPOSCode(strPOSCode);
			    objNCModel.setStrReasonCode(strReasonCode);
			    objNCModel.setStrRemark("");
			    objNCModel.setStrUserCreated(user);
			    objNCModel.setStrUserEdited(user);
			    
			    objMakeKOTDao.funSaveNCKOT(objNCModel);
				   
			    }
			    }
			}
			    objMakeKOTDao.funUpdateKOT(jObjPOSMaster);
			    objMakeKOTDao.funInsertIntoTblItemRTempBck(strTableNo,strKOTNo);
			    
			}
			catch (Exception e)
			{
			    e.printStackTrace();
			}
			
			return strKOTNo; 
		    }
		
}
