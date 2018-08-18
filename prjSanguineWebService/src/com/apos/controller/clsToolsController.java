package com.apos.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.apos.service.clsPOSConfigSettingService;

@Controller
@Path("/WebPOSTools")
public class clsToolsController {
	
	
	@Autowired
	clsPOSConfigSettingService objPOSConfigSettingService;
	
	
	@POST
	@Path("/funGetSaveConfigSetting")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funSaveConfigSetting(JSONObject objConfigData)
	{
		JSONObject jObj =new JSONObject();
		String saveData="";
		try {
			saveData = objPOSConfigSettingService.funSavePOSConfigSetting(objConfigData);
			jObj.put("status", saveData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(201).entity(jObj).build();
    }
	
	@POST
	@Path("/funLoadConfigSetting")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funLoadConfigSetting(JSONObject objConfigData)
	{
		JSONObject jObj =new JSONObject();
		String saveData="";
		try {
			jObj = objPOSConfigSettingService.funLoadPOSConfigSetting(objConfigData.get("strClientCode").toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(201).entity(jObj).build();
    }
	

}
