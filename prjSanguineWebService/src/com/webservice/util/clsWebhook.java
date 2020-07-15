package com.webservice.util;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/webhook")
public class clsWebhook {

	@SuppressWarnings("finally")
    @GET 
	@Path("/funInvokeAPOSWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetConnectionInfo()
	{
		String response="ok";
		try
		{
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Check Server Config...");
		return Response.status(201).entity(response).build();
	}
	
	
}
