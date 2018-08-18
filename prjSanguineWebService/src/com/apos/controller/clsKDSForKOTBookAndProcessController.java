
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

import com.apos.service.clsKDSForKOTBookAndProcessService;


@Controller
@Path("/WebKDSForKOTBookAndProcessController")
public class clsKDSForKOTBookAndProcessController {
	@Autowired
	clsKDSForKOTBookAndProcessService	objKDSBookAndProcessService;
	

	@GET
	@Path("/funInvokeWebKDSForKOTBookAndProcessWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funCheckWSConnection()
	{
		String response = "true";
		return response;
	}
	
	
	@GET 
	@Path("/funGetKOTHdDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetKOTHdDtl()
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objKDSBookAndProcessService.funGetKOTHdDtl();
		
		return jObjTableData;
	}
	
	@GET 
	@Path("/funGetNewKOTSize")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetNewKOTSize()
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objKDSBookAndProcessService.funGetNewKOTSize();
		
		return jObjTableData;
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funKOTOrderProcess")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funKOTOrderProcess(JSONObject jObj)
	{
		String itemCode = "";
		objKDSBookAndProcessService.funKOTOrderProcess(jObj);
		 return Response.status(201).entity(itemCode).build();
	}
}
