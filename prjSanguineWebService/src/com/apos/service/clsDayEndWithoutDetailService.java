package com.apos.service;

import org.codehaus.jettison.json.JSONObject;

public interface clsDayEndWithoutDetailService {

	 public JSONObject funStartDayProcessWithoutDetails(String strPOSCode,String shiftNo);
	 
	 public JSONObject funDayEndProcessWithoutDetails(String strPOSCode, String shiftNo,String strUserCode,String POSDate,String strClientCode);
}
