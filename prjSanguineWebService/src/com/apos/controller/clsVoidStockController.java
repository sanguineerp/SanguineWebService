package com.apos.controller;

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

import com.apos.service.clsVoidStockService;

@Controller
@Path("/webPosVoidStockController")
public class clsVoidStockController {

	@Autowired
	clsVoidStockService objVoidStockService;
	
	@POST
    @Path("/funFillVoidStockGrid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funFillVoidStockGrid(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
			jObjTableData = objVoidStockService.funLoadStockList(jObjfillter.getString("strPosCode"),jObjfillter.getString("transType"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }
	
	@POST
    @Path("/fillStockDtlData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fillStockDtlData(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
			jObjTableData = objVoidStockService.funLoadStockDtlData(jObjfillter.getString("code"),jObjfillter.getString("transType"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }
	

	@POST
    @Path("/funVoidStockIn")
    @Produces(MediaType.APPLICATION_JSON)
    public Response funVoidStockIn(JSONObject jObjfillter)
    {
	JSONObject jObjTableData = new JSONObject();
	try
	{
			objVoidStockService.funVoidStockIn(jObjfillter.getString("voidResaonCode"),jObjfillter.getString("transType"),jObjfillter.getString("stockCode"),jObjfillter.getString("userCode"));
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Response.status(201).entity(jObjTableData).build();

    }

	
	
}
