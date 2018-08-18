package com.apos.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import com.apos.dao.clsSetupDao;
import com.apos.service.clsBillSettlementService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
@Path("/WebPOSBillSettlement")
public class clsBillSettlementController {

	@Autowired 
	clsBillSettlementService objBillSettlementService;
	@Autowired
	clsSetupDao objSetupDao;
	
	 @POST
	 @Path("/funFillUnsettleBillData")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funFillUnsettleBill1(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
			jObjRptData = objBillSettlementService.funFillUnsettleBill(jObjfillter.get("clientCode").toString(),
						jObjfillter.get("strPosCode").toString(), jObjfillter.get("posDate").toString());
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	 
	 
	 @POST
	 @Path("/fillRowSelected")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response funRowSelected(JSONObject jObjfillter)
	{
		 JSONObject jObjRptData = new JSONObject();
		 List listRet=null;;
		try {
		
//			funRowSelected(int rowCount,String clientCode,String posCode,String billNo,String tableNo,String billType)
			
			jObjRptData = objBillSettlementService.funRowSelected(Integer.parseInt(jObjfillter.get("selectedRowIndex").toString()),
						jObjfillter.get("clientCode").toString(), jObjfillter.get("posCode").toString(),jObjfillter.get("billNo").toString(),jObjfillter.get("selectedTableNo").toString(),
						jObjfillter.get("selectedRowIndex").toString(),jObjfillter.getBoolean("superuser"));
		 //jObjRptData.put("listRptData", listRet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return Response.status(201).entity(jObjRptData).build();
	}
	
	 @POST
	 @Path("/funLoadItemsGroupSubGroupData")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response funLoadItemsGroupSubGroupData(JSONObject jsonItemListData)
	 {
		 JSONObject jsonAllListsData=new JSONObject();
		 try{
			 	ArrayList<String> alItemCode=new ArrayList<String>();
			 	Gson gson = new Gson();
				Type listType = new TypeToken<List<String>>() {}.getType();
				alItemCode= gson.fromJson(jsonItemListData.get("ItemCodeList").toString(), listType);
				
				jsonAllListsData=objBillSettlementService.funFillGroupSubGroupList(alItemCode);
		 }
		 catch(Exception e){
			 
		 }
		 
		 return Response.status(201).entity(jsonAllListsData).build();
	 }
	
		@GET
	    @Path("/funCheckPointsAgainstCustomer")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funCheckPointsAgainstCustomer(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode,
	    		@QueryParam("CRMInterface") String CRMInterface,@QueryParam("customerMobile") String customerMobile,
	    		@QueryParam("voucherNo") String voucherNo,@QueryParam("txtPaidAmt") String txtPaidAmt)
	    {
			JSONObject jsonCheckPoints=new JSONObject();
			jsonCheckPoints=objBillSettlementService.funCheckPointsAgainstCustomer(clientCode, posCode, CRMInterface, customerMobile,voucherNo,txtPaidAmt);
			
			return jsonCheckPoints;
	     }
		
		@GET
	    @Path("/funGetDebitCardNo")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funGetDebitCardNo(@QueryParam("clientCode")String clientCode,@QueryParam("posCode")String posCode,
	    		@QueryParam("voucherNo") String voucherNo,@QueryParam("tableNo") String tableNo)
	    {
			JSONObject jsonCheckPoints=new JSONObject();
			jsonCheckPoints=objBillSettlementService.funGetDebitCardNo(clientCode, posCode, voucherNo,tableNo);
			
			return jsonCheckPoints;
	     }
		
	 /*	@GET
	    @Path("/funPopupPromotionOnBill")
	    @Produces(MediaType.APPLICATION_JSON)
	    public JSONObject funCheckWSConnection(@QueryParam("strClientCode")String strClientCode,@QueryParam("posCode")String posCode)
	    {
	 		
	 		JSONObject jsonob=new JSONObject();
	 		String response = "N";
	 		try{
	 			JSONObject jsItemType = objSetupDao.funGetParameterValuePOSWise(strClientCode,posCode, "gPopUpToApplyPromotionsOnBill");
	 			response=jsItemType.get("gPopUpToApplyPromotionsOnBill").toString();
	 			jsonob.put("POPUPWindow",response);
	 		}catch(Exception e){
	 			response="N";
	 			//jsonob.put("POPUPWindow",response);
	 			e.printStackTrace();
	 		}
	 		
	 	 		return jsonob;
	    }*/
	 
	 
	 	
}
