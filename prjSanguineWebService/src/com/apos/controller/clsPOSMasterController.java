package com.apos.controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.model.clsPOSMasterModel;
import com.apos.service.clsPOSMasterService;

@Controller
@Path("/WebPOSPOSMaster")
public class clsPOSMasterController {
	
	 @Autowired
	 clsPOSMasterService objPOSMasterService;
	
	 	@POST
		@Path("/funGetPOSNameData")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funGetPOSCode(JSONObject objSon)
		{
			
	        JSONObject jObj=new JSONObject();
	        try
	        {
	        	clsPOSMasterModel objModel = objPOSMasterService.funGetPOSNameData(objSon.getString("strPOSName"));
			
			jObj.put("strPosCode", objModel.getStrPosCode());
			jObj.put("strPosName", objModel.getStrPosName());
			jObj.put("strDebitCardTransactionYN", objModel.getStrDebitCardTransactionYN());
			jObj.put("strPropertyPOSCode", objModel.getStrPropertyPOSCode());
			jObj.put("strPropertyPOSCode", objModel.getStrPropertyPOSCode());
			jObj.put("strUserCreated", objModel.getStrUserCreated());
			jObj.put("strUserEdited", objModel.getStrUserEdited());
			jObj.put("strCounterWiseBilling", objModel.getStrCounterWiseBilling());
			jObj.put("strDelayedSettlementForDB", objModel.getStrDelayedSettlementForDB());
			jObj.put("strBillPrinterPort", objModel.getStrBillPrinterPort());
			jObj.put("strAdvReceiptPrinterPort", objModel.getStrAdvReceiptPrinterPort());
			jObj.put("strOperationalYN", objModel.getStrOperationalYN());
			jObj.put("strPrintVatNo", objModel.getStrPrintVatNo());
			jObj.put("strPrintServiceTaxNo", objModel.getStrPrintServiceTaxNo());
			jObj.put("strVatNo", objModel.getStrVatNo());
			jObj.put("strServiceTaxNo", objModel.getStrServiceTaxNo());
			jObj.put("strRoundOff", objModel.getStrRoundOff());
			jObj.put("strTip", objModel.getStrTip());
			jObj.put("strDiscount", objModel.getStrDiscount());
			jObj.put("strWSLocationCode", objModel.getStrWSLocationCode());
			jObj.put("strEnableShift", objModel.getStrEnableShift());
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }

			 return  Response.status(201).entity(jObj).build();
		}
		


}
