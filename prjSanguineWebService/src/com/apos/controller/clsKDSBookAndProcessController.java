package com.apos.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.service.clsKDSBookAndProcessService;


@Controller
@Path("/WebKDSBookAndProcessController")
public class clsKDSBookAndProcessController {
	@Autowired
	clsKDSBookAndProcessService	objKDSBookAndProcessService;
	

	@GET
	@Path("/funInvokeWebKDSBookAndProcessWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funCheckWSConnection()
	{
		String response = "true";
		return response;
	}
	
	
	@GET 
	@Path("/funGetBillHdDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetBillHdDtl()
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objKDSBookAndProcessService.funGetBillDtl();
		
		return jObjTableData;
	}
	
	@GET 
	@Path("/funGetNewBillSize")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetNewBillSize()
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objKDSBookAndProcessService.funGetNewBillSize();
		
		return jObjTableData;
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funBillOrderProcess")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funBillOrderProcess(JSONObject jObj)
	{
		String itemCode = "";
		objKDSBookAndProcessService.funBillOrderProcess(jObj);
		 return Response.status(201).entity(itemCode).build();
	}
}
