package com.apos.controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.service.clsVoidBillService;

@Controller
@Path("/WebPOSVoidBill")
public class clsVoidBillController {

	@Autowired 
	clsVoidBillService objVoidBillService;
	
	
	 @POST
	 @Path("/funVoidBill")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funLoadBillGrid(JSONObject jObjFilter)
	{
		 JSONObject jObjRptData=null;
		 try{
		 jObjRptData=objVoidBillService.funLoadBillGrid(jObjFilter.getString("posDate").toString(),jObjFilter.getString("strPosCode").toString(),jObjFilter.getString("SearchBillNo").toString());
		 }catch( Exception e)
		 {
		   e.printStackTrace();	 
		 }
		 return Response.status(201).entity(jObjRptData).build();
	
		 }
	
	 
	 @POST
	 @Path("/funfillBillDtlData")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funfillBillDtlData(JSONObject jObjFilter)
	{
		 JSONObject jObjRptData=null;
		 try{
		 jObjRptData=objVoidBillService.funSelectBill(jObjFilter.getString("billNo").toString(),jObjFilter.getString("strClientCode").toString(),jObjFilter.getString("strPosCode").toString(),jObjFilter.getString("posDate").toString());
		 }catch( Exception e)
		 {
		   e.printStackTrace();	 
		 }
		 return Response.status(201).entity(jObjRptData).build();
	
		 }
	 
		@POST
	    @Path("/funVoidItemData")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response funVoidItemData(JSONObject jObjfillter)
	    {
		JSONObject jObjTableData = new JSONObject();
		try
		{

			jObjTableData =  objVoidBillService.funVoidItem(jObjfillter.getString("reasonCode"),jObjfillter.getString("delTableNo"),jObjfillter.getString("billNo"),jObjfillter.getString("remarks"),
					jObjfillter.getString("userCode"),jObjfillter.getString("clientCode"),jObjfillter.getString("voidedDate"),jObjfillter.getDouble("taxAmt"),
					jObjfillter.getString("itemCode"),jObjfillter.getDouble("quantity"),jObjfillter.getDouble("amount"),jObjfillter.getString("itemName"),
					jObjfillter.getString("modItemCode"),jObjfillter.getString("strPosCode"));
		}
		catch (Exception e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return Response.status(201).entity(jObjTableData).build();

	    }
		@POST
	    @Path("/fullVoidBill")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response funFullVoidBill(JSONObject jObjfillter)
	    {
		JSONObject jObjTableData = new JSONObject();
		try
		{
			

			jObjTableData =  objVoidBillService.funVoidBill(jObjfillter.getString("voidedDate"),jObjfillter.getString("billNo"),jObjfillter.getString("reasonCode"),
					jObjfillter.getString("remarks"),jObjfillter.getString("userCode"),jObjfillter.getString("strPosCode"),jObjfillter.getString("clientCode"));
}
		catch (Exception e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return Response.status(201).entity(jObjTableData).build();
	    }
}


