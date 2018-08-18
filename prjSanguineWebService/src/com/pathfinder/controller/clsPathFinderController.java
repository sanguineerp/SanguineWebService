package com.pathfinder.controller;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.codehaus.jettison.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Path("/PathFinderIntegration")
public class clsPathFinderController
{

	@Autowired
	clsPathFinderDao	objPathFinderDao;

	@GET
	@Path("/funInvokePathFinderIntegrationWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funCheckWSConnection()
	{
		String response = "true";
		return response;
	}

	@GET
	@Path("/funGetPOSSalesDataToXML")
	@Produces(MediaType.APPLICATION_XML)
	public List<clsBillHd> funGetPOSSalesDataToXML(@QueryParam("ClientCode") String clientCode, @QueryParam("POSCode") String posCode, @QueryParam("FromDate") String fromDate, @QueryParam("ToDate") String toDate)
	{

		List<clsBillHd> list = objPathFinderDao.funGetPOSSalesData(clientCode, posCode, fromDate, toDate);


		return list;

	}

	@GET
	@Path("/funGetPOSSalesData")
	@Produces(MediaType.APPLICATION_XML)
	public Response funGetPOSSalesData(@QueryParam("ClientCode") String clientCode, @QueryParam("POSCode") String posCode, @QueryParam("FromDate") String fromDate, @QueryParam("ToDate") String toDate)
	{

		StringWriter stringWriter = new StringWriter();
		
		List<clsBillHd> list = objPathFinderDao.funGetPOSSalesData(clientCode, posCode, fromDate, toDate);
		
		clsRecord objRecord=new clsRecord();
		objRecord.setListOfBills(list);
		
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(clsRecord.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(objRecord, stringWriter);
		}
		catch (JAXBException e)
		{
			return Response.serverError().entity(e.getMessage()).build();
		}
		
		
		return Response.ok(stringWriter.toString(), MediaType.APPLICATION_XML).build();


	}
}
