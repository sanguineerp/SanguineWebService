package com.apos.controller;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.dao.clsSetupDao;
import com.apos.service.clsDayEndProcessService;
/*@Repository("clsDayEndProcessController")*/
import com.apos.service.clsSetupService;

@Controller
@Path("/DayEndProcessTransaction")
public class clsDayEndProcessController {

	@Autowired
	clsDayEndProcessService objPOSDayEndProcessService;
	

	@Autowired 
	clsUtilityController objUtility;
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired 
	clsSetupService obSetupService;
	
	@GET 
	@Path("/funShiftStartProcess")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funShiftStartProcess(@QueryParam("strPOSCode")String strPOSCode,@QueryParam("shiftNo")String shiftNo)
	{
		
		JSONObject jObj=new JSONObject();
		try{
			jObj=objPOSDayEndProcessService.funShiftStartProcess(strPOSCode,shiftNo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jObj;
	}
	
	@GET
	@Path("/funGetAllParameterValuesPOSWise")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetAllParameterValuesPOSWise(@QueryParam("POSCode")String posCode,@QueryParam("clientCode")String clientCode)
	{
		JSONObject GlobalData=objPOSDayEndProcessService.funGetAllParameterValuesPOSWise(posCode, clientCode);
		return GlobalData;
	}

	@SuppressWarnings("rawtypes")
	@POST 
	@Path("/funDayEndProcess")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funDayEndProcess(JSONObject jsonData)
	{	
		JSONObject jsonEndDay=new JSONObject();
		try{
				jsonEndDay=objPOSDayEndProcessService.funDayEndProcess(jsonData.getString("strPOSCode"),jsonData.getString("ShiftNo"),jsonData.getString("userCode"),jsonData.getString("strPOSDate"),jsonData.getString("strClientCode"),jsonData.getString("EmailReport"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 return Response.status(201).entity(jsonEndDay).build();
	}

	@GET
	@Path("/funCheckPendingBillsAndTables")
	@Produces(MediaType.APPLICATION_JSON)
	 public JSONObject funCheckPendingBills(@QueryParam("strPOSCode")String POSCode,@QueryParam("POSDate")String POSDate)
	 {
			JSONObject jsPendingBills=new JSONObject();
			boolean pendingBills=objUtility.funCheckPendingBills(POSCode, POSDate);
			boolean busyTable=objUtility.funCheckTableBusy(POSCode);
			try {
				jsPendingBills.put("PendingBills", pendingBills);
				jsPendingBills.put("BusyTables", busyTable);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsPendingBills;
	 }
	 
	 @GET
	 @Path("/funLoadAllReportsName")
	 @Produces(MediaType.APPLICATION_JSON)
	 public JSONObject funLoadAllReportsName(@QueryParam("strPOSCode") String strPosCode,@QueryParam("strClientCode") String strClientCode)
	 {
		 JSONObject jsonReportName=new JSONObject();
		 jsonReportName=objPOSDayEndProcessService.funLoadAllReportsName(strPosCode,strClientCode);
		 return jsonReportName;
	 }

	 @SuppressWarnings("rawtypes")
	 @POST
	 @Path("/funSendDayEndMailReports")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funSendDayEndMailReports(JSONObject jsonData)
	 {
		 JSONObject jsStatus=new JSONObject();
		 try{
			 jsStatus=objPOSDayEndProcessService.funSendDayEndMailReports(jsonData);
			 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		// return jsStatus;
		 return Response.status(201).entity(jsStatus).build();
	 }
	 
}
