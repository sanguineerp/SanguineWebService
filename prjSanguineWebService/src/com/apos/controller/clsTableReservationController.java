
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

import com.apos.service.clsTableReservationService;


@Controller
@Path("/WebTableReservationController")
public class clsTableReservationController {
	@Autowired
	clsTableReservationService	objTableReservationService;
	

	@GET
	@Path("/funInvokeWebclsTableReservationService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funCheckWSConnection()
	{
		String response = "true";
		return response;
	}
	
	@GET 
	@Path("/funGetReservationDefault")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetReservationDefault(@QueryParam("date") String date,@QueryParam("loginPosCode") String loginPosCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objTableReservationService.funGetReservationDefault(date, loginPosCode);
		
		return jObjTableData;
	}
	
	@GET 
	@Path("/funGetTableReservationDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetTableReservationDtl(@QueryParam("fromDate") String fromDate,@QueryParam("toDate") String toDate,@QueryParam("fromTime") String fromTime,@QueryParam("toTime") String toTime,@QueryParam("loginPosCode") String loginPosCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objTableReservationService.funGetTableReservationDtl(fromDate, toDate, fromTime, toTime, loginPosCode);
		
		return jObjTableData;
	}
	
	@GET 
	@Path("/funCancelTableReservation")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funCancelTableReservation(@QueryParam("reservationNo") String reservationNo,@QueryParam("tableNo") String tableNo)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		objTableReservationService.funCancelTableReservation(reservationNo, tableNo);
		
		return jObjTableData;
	}
	
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funSaveTableReservation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveTableReservation(JSONObject jObj)
	{
		String itemCode = "";
		itemCode=objTableReservationService.funAddUpdateTableReservation(jObj);
		 return Response.status(201).entity(itemCode).build();
	}
}
