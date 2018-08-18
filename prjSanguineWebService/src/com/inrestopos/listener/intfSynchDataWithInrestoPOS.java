package com.inrestopos.listener;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;

public interface intfSynchDataWithInrestoPOS 
{
	public Response funGetCustomerSeatData(JSONObject jsoObject);
}
