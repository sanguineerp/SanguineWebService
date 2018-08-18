package com.apos.controller;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.service.clsPOSReportService;


@Controller
@Path("/WebPOSReprintDocuments")
public class clsReprintDocumentsReportController {

	
	 @Autowired
	 clsPOSReportService objPOSReportService;
	 
	 @POST
	 @Path("/funViewButtonPressedReport")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funViewButtonPressedReport(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData=objPOSReportService.funViewButtonPressed(jObjfillter.get("code").toString(),
						jObjfillter.get("transactionType").toString(), jObjfillter.get("kotFor").toString(), jObjfillter.get("loginPOS").toString(),
						jObjfillter.get("clientCode").toString(), jObjfillter.get("strPosName").toString(),jObjfillter.get("usercode").toString(),
						jObjfillter.get("POSDate").toString(),jObjfillter.get("PrintVatNoPOS").toString(),jObjfillter.get("vatNo").toString(),jObjfillter.get("printServiceTaxNo").toString(),jObjfillter.get("serviceTaxNo").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
}
