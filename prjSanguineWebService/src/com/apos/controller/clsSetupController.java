package com.apos.controller;

import javax.ws.rs.Consumes;
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

import com.apos.service.clsSetupService;

@Controller
@Path("/WebPOSSetup")
public class clsSetupController
{
	
	@Autowired
	clsSetupService objSetupService;
	
	
	
	
	@GET
	@Path("/funGetPOSWiseSetup")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetPOSWiseSetup(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode)
	{
		return objSetupService.funGetPOSWiseSetup(clientCode,posCode);
	}
	
	@GET
	@Path("/funGetSetupForAllPOS")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetSetupForAllPOS(@QueryParam("clientCode")String clientCode)
	{
		String response = "true";
		return response;
	}
	
	
	@GET
	@Path("/funGetParameterValuePOSWise")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetParameterValuePOSWise(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode,@QueryParam("parameterName")String parameterName)
	{		
		return objSetupService.funGetParameterValuePOSWise(clientCode,posCode,parameterName);
	}
	
	
	@GET
	@Path("/funGetAllParameterValuesPOSWise")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetAllParameterValuesPOSWise(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode)
	{		
		return objSetupService.funGetAllParameterValuesPOSWise(clientCode,posCode);
	}
	
	@GET
	@Path("/funGetPrinterDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetPrinterDtl()
	{		
		return objSetupService.funGetPrinterDtl();
	}	
	@GET
	@Path("/loadOldSBillSeriesSetup")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject loadOldSBillSeriesSetup(@QueryParam("posCode")String posCode)
	{		
		return objSetupService.funGetOldSBillSeriesSetup(posCode);
	}
	
	@GET
	@Path("/funGetOldBillSeries")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject loadOldBillSeries(@QueryParam("posCode")String posCode)
	{		
		return objSetupService.funGetOldBillSeries(posCode);
	}
	

		@SuppressWarnings("rawtypes")
    @POST
    @Path("/funSaveUpdatePropertySetup")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funSaveUpdatePropertySetup(JSONObject jObjFactoryMaster)
    {
	
	String factoryCode = "";
	
	factoryCode = objSetupService.funSaveUpdatePropertySetup(jObjFactoryMaster);
	
	return Response.status(201).entity(factoryCode).build();
    }


		@GET
		@Path("/funSetToken")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funSetToken(@QueryParam("token")String token,@QueryParam("posCode")String posCode,@QueryParam("mid")String mid)
		{		
			JSONObject jobj=new JSONObject();
			 objSetupService.funSetToken(token,posCode,mid);
			 return jobj;
		}
		
		@GET
		@Path("/funGetPos")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetPos(@QueryParam("posCode")String posCode)
		{		
			return objSetupService.funGetPos(posCode);
		}
		
		
		@GET
		@Path("/funGetPOSClientCode")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetPOSClientCode()
		{
			return objSetupService.funGetPOSClientCode();
		}
		
}
