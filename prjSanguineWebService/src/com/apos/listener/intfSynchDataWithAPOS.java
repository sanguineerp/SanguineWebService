package com.apos.listener;

import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.Response;

public interface intfSynchDataWithAPOS 
{
	String funCheckLicenceKey(String deviceId,String clientCode);
	JSONArray funSalesReport(String posCode,String userCode,String fromDate,String toDate,String reportType);


}
