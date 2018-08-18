package com.apos.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.apos.service.clsMakeBillService;

@Controller
@Path("/clsMakeBillController")
public class clsMakeBillController {

	@Autowired
	clsMakeBillService	objMakeBillService;
	@GET
	@Path("/funInvokeWebMakeBillWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funCheckWSConnection()
	{
		String response = "true";
		return response;
	}
	
	
	@GET 
	@Path("/funLoadOccupiedTableDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funLoadTableDtl(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMakeBillService.funLoadTableDtl(clientCode,posCode);
		
		return jObjTableData;
	}
	@GET 
	@Path("/funLoadTableForArea")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funLoadTableForArea(@QueryParam("areaCode")String areaCode,@QueryParam("posCode")String posCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMakeBillService.funLoadTableForArea(areaCode,posCode);
		
		return jObjTableData;
	}
	@GET 
	@Path("/funFillItemTableDtl")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funFillItemTableDtl(@QueryParam("tableNo")String tableNo,@QueryParam("posCode")String posCode)
	{
		
		JSONObject jObjTableData=new JSONObject();
		
		jObjTableData=objMakeBillService.funFillItemTableDtl(tableNo,posCode);
		
		return jObjTableData;
	}
}
