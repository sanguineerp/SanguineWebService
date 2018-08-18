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

import com.apos.service.clsVoidKotService;


@Controller
@Path("/webPosVoidKot")
public class clsVoidKotContrroller {
	@Autowired
	clsVoidKotService objVoidKotService;
	
	@POST
    @Path("/funLoadTable")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funLoadTable(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
		jObjTableData = objVoidKotService.funLoadTable(jObjfillter.getString("strPosCode"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }
	
	
	@POST
    @Path("/funLoadReson")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funLoadReson(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
		jObjTableData = objVoidKotService.funLoadReson();
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }
	
	
	@POST
    @Path("/funFillHelpGrid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funFillHelpGrid(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
		jObjTableData = objVoidKotService.funFillHelpGrid(jObjfillter.getString("tableName"),jObjfillter.getString("tableNo"),
				jObjfillter.getString("strPosCode"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }
	
	@POST
    @Path("/fillItemTableData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fillItemTableData(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
		jObjTableData = objVoidKotService.fillItemTableData(jObjfillter.getString("KotNo"),jObjfillter.getString("tableNo"),
				jObjfillter.getString("strPosCode"),jObjfillter.getString("tableName"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }
	
	
	@POST
    @Path("/funDoneBtnclick")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funDoneBtnclick(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
	
		jObjTableData =  objVoidKotService.funDoneButtonClick(jObjfillter.getJSONObject("jservice"),jObjfillter.getString("reasonCode"),jObjfillter.getString("delTableNo"),jObjfillter.getString("delKotNo"),jObjfillter.getString("remarks"),
				jObjfillter.getString("userCode"),jObjfillter.getString("clientCode"),
				jObjfillter.getString("voidedDate"),jObjfillter.getDouble("taxAmt"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }
	
	
	@POST
    @Path("/funClickFullVoidKot")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funClickFullVoidKot(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
	
//		String reasonCode,String Kot,String strClientCode,String voidedDate,String userCode,String voidKOTRemark
		jObjTableData =  objVoidKotService.funClickFullVoidKot(jObjfillter.getString("reasonCode"),jObjfillter.getString("delKotNo"),jObjfillter.getString("clientCode"),
				jObjfillter.getString("voidedDate"),jObjfillter.getString("userCode"),jObjfillter.getString("remarks"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();
    }
	
}
