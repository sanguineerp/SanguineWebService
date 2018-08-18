package com.apos.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.apos.service.clsDayEndConsolidateService;
import com.apos.service.clsDayEndWithoutDetailService;
import com.apos.service.clsSetupService;

@Controller
@Path("/DayEndProcessConsolidate")
@Transactional(value = "webPOSTransactionManager")
public class clsDayEndProcessConsolidateController {

	@Autowired
	clsDayEndConsolidateService obDayEndConsolidateService;

	@Autowired 
	clsUtilityController objUtility;
	
	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired 
	clsSetupService obSetupService;
	@Autowired 
	clsDayEndWithoutDetailService obDayEndWithoutDetailService;  
	
	@GET 
	@Path("/funShiftStartProcess")
	@Produces(MediaType.APPLICATION_JSON)
   	 public JSONObject funShiftStartProcess(@QueryParam("shiftNo")String shiftNo)
	{
		
		JSONObject jObj=new JSONObject();
		try{
			jObj=obDayEndConsolidateService.funShiftStartProcess(shiftNo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jObj;
	}
	
	@GET
	@Path("/funCheckPendingBillsAndTables")
	@Produces(MediaType.APPLICATION_JSON)
	 public JSONObject funCheckPendingBills(@QueryParam("POSDate")String POSDate)
	 {
			JSONObject jsPendingBills=new JSONObject();
		try{
				String POSCode="";
				String sql = "select strPOSCode from tblposmaster where strOperationalYN='Y' ";
				Query qpos=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
				List listPOS=qpos.list();
				if(listPOS.size()>0)
				{
					JSONArray jArr=new JSONArray();
					for(int i=0;i<listPOS.size();i++)
					{
						JSONObject jOb=new JSONObject();
						POSCode=listPOS.get(i).toString();
						boolean pendingBills=objUtility.funCheckPendingBills(POSCode, POSDate);
						boolean busyTable=objUtility.funCheckTableBusy(POSCode);
						jOb.put("PendingBills", pendingBills);
						jOb.put("BusyTables", busyTable);
						jArr.put(jOb);
						
					}
					jsPendingBills.put("TableAndBill", jArr);
				}
						
				
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			return jsPendingBills;
	 }

	@SuppressWarnings("rawtypes")
	@POST 
	@Path("/funConsolidateDayEndProcess")
	@Produces(MediaType.APPLICATION_JSON)
	 public Response funConsolidateDayEndProcess(JSONObject jsonData)
	{	
		JSONObject jsonConsolidateEndDay=new JSONObject();
		try{
				jsonConsolidateEndDay=obDayEndConsolidateService.funConsolidateDayEndProcess(jsonData.getString("strPOSCode"),jsonData.getString("ShiftNo"),jsonData.getString("userCode"),jsonData.getString("strPOSDate"),jsonData.getString("strClientCode"),jsonData.getString("EmailReport"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 return Response.status(201).entity(jsonConsolidateEndDay).build();
	}

	
	@GET 
	@Path("/funStartDayProcessWithoutDetails")
	@Produces(MediaType.APPLICATION_JSON)
	 public JSONObject funStartDayProcessWithoutDetails(@QueryParam("strPOSCode")String strPOSCode,@QueryParam("shiftNo")String shiftNo)
	 {
		 
		 return obDayEndWithoutDetailService.funStartDayProcessWithoutDetails(strPOSCode,shiftNo);
	 }
	
	//funDayEndProcessWithoutDetails
	@SuppressWarnings("rawtypes")
	@POST 
	@Path("/funDayEndProcessWithoutDetails")
	@Produces(MediaType.APPLICATION_JSON)
	 public Response funDayEndProcessWithoutDetails(JSONObject jsonData)
	{	
		JSONObject jsonBlankEndDay=new JSONObject();
		try{
			jsonBlankEndDay=obDayEndWithoutDetailService.funDayEndProcessWithoutDetails(jsonData.getString("strPOSCode"),jsonData.getString("ShiftNo"),jsonData.getString("userCode"),jsonData.getString("strPOSDate"),jsonData.getString("strClientCode"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 return Response.status(201).entity(jsonBlankEndDay).build();
	}

}
