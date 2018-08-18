package com.apos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;



import com.apos.dao.clsCustomerAreaMasterDaoImpl;
import com.apos.dao.inftCustomerAreaMasterDao;
import com.apos.model.clsCustomerAreaMasterAmountModel;
import com.apos.model.clsCustomerAreaMasterAmountModel_ID;
import com.apos.model.clsCustomerAreaMasterModel;
import com.apos.model.clsCustomerAreaMasterModel_ID;
import com.apos.model.clsCustomerTypeMasterModel;
import com.apos.model.clsCustomerTypeMasterModel_ID;
import com.apos.model.clsPOSMasterModel;
import com.apos.model.clsTaxMasterModel;
import com.apos.model.clsTaxPosDetailsModel;
import com.apos.model.clsTaxSettlementDetailsModel;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsCustomerAreaMasterService")
public class clsCustomerAreaMasterServiceImpl implements clsCustomerAreaMasterService {
	@Autowired
	private inftCustomerAreaMasterDao objCustomerAreaMasterDao;
	
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	clsUtilityFunctions utility;


	public String funSaveCustomerAreaMaster(JSONObject jObjCustomerAreaMaster)
{
		JSONArray ArrayList=new JSONArray();
		
		String customerAreaCode = "";
		try
		{
		    System.out.println(jObjCustomerAreaMaster);
		    customerAreaCode = jObjCustomerAreaMaster.getString("strCustomerAreaCode");
		    String strCustomerAreaName = jObjCustomerAreaMaster.getString("strCustomerAreaName");
		    String strAddress = jObjCustomerAreaMaster.getString("strAddress");
		    Double strHomeDeliveryCharges = jObjCustomerAreaMaster.getDouble("strHomeDeliveryCharges");
		    String strZone = jObjCustomerAreaMaster.getString("strZone");
		    Double dblDeliveryBoyPayOut = jObjCustomerAreaMaster.getDouble("dblDeliveryBoyPayOut");
		    Double strHelperPayOut = jObjCustomerAreaMaster.getDouble("strHelperPayOut");
		    
		    
		    ArrayList=jObjCustomerAreaMaster.getJSONArray("List");
		    String user = jObjCustomerAreaMaster.getString("User");
		    String clientCode =jObjCustomerAreaMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		    if (customerAreaCode.trim().isEmpty())
		    {
		    	//customerAreaMasterCode = objCustomerAreaMasterDao.funGenerateCustomerAreaCode();
		    	List list=utility.funGetDocumentCode("POSCustAreaMaster");
		    	if (!list.get(0).toString().equals("0"))
				{
				    String strCode = "00";
				    String code = list.get(0).toString();
				    StringBuilder sb = new StringBuilder(code);
				    String ss = sb.delete(0, 2).toString();
				    for (int i = 0; i < ss.length(); i++)
				    {
						if (ss.charAt(i) != '0')
						{
						    strCode = ss.substring(i, ss.length());
						    break;
						}
				    }
				    
				    int intCode = Integer.parseInt(strCode);
				    intCode++;
				    if (intCode < 10)
				    {
				    	customerAreaCode = "B000000" + intCode;
				    }
				    else if (intCode < 100)
				    {
				    	customerAreaCode = "B00000" + intCode;
				    }
				    else if (intCode < 1000)
				    {
				    	customerAreaCode = "B0000" + intCode;
				    }
				    else if (intCode < 10000)
				    {
				    	customerAreaCode = "B000" + intCode;
				    }
				    else if (intCode < 100000)
				    {
				    	customerAreaCode = "B00" + intCode;
				    }
				    else if (intCode < 1000000)
				    {
				    	customerAreaCode = "B0" + intCode;
				    }
				   
				}
				else
				{
					customerAreaCode = "B0000001";
				}

		    }
		    clsCustomerAreaMasterModel objModel = new clsCustomerAreaMasterModel(new clsCustomerAreaMasterModel_ID(customerAreaCode,clientCode));
		    
		    objModel.setStrBuildingName(strCustomerAreaName);
		    objModel.setStrAddress(strAddress);
		    objModel.setDblHomeDeliCharge(strHomeDeliveryCharges);
		    objModel.setStrZoneCode(strZone);
		    objModel.setDblDeliveryBoyPayOut(dblDeliveryBoyPayOut);
		    objModel.setDblHelperPayOut(strHelperPayOut);
		   
		    
		    objModel.setStrClientCode(clientCode);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		   // System.out.println("Length of array"+ArrayList.length());
		    //customerAreaCode = objCustomerAreaMasterDao.funSaveCustomerAreaMaster(objModel);
		    //List<clsCustomerAreaMasterAmountModel> listCustAreaDtl = new ArrayList<clsCustomerAreaMasterAmountModel>();
		    
		    Set<clsCustomerAreaMasterAmountModel> listCustAreaDtl = new HashSet<clsCustomerAreaMasterAmountModel>();
		    	 for(int i=0; i<ArrayList.length(); i++)
				    {
				    try
				    {
				    	
					 JSONObject jObj =ArrayList.getJSONObject(i);
					  clsCustomerAreaMasterAmountModel objModelAmount = new clsCustomerAreaMasterAmountModel();	
				    	
				    					    	
				    					   
				    	 	objModelAmount.setDblBillAmount(jObj.getDouble("amount"));
						    objModelAmount.setDblBillAmount1(jObj.getDouble("amount1"));
						    objModelAmount.setDblDeliveryCharges(jObj.getDouble("deliveryCharges"));
						    objModelAmount.setStrCustTypeCode(jObj.getString("customerType"));						   
						    objModelAmount.setStrUserCreated(user);
						    objModelAmount.setStrUserEdited(user);
						    objModelAmount.setDteDateCreated(dateTime);
						    objModelAmount.setDteDateEdited(dateTime);
						    objModelAmount.setStrDataPostFlag("N");
						    objModelAmount.setStrSymbol("N");
						    objModelAmount.setDblKilometers(0);
						    
						    listCustAreaDtl.add(objModelAmount);
						    //System.out.println("objModelAmount="+objModelAmount);
						    //objCustomerAreaMasterAmountDao.funSaveCustomerAreaMasterAmount(objModelAmount);
						    
				    }
				    catch (Exception e)
					{
					    e.printStackTrace();
					}
	
		       }
		    	 
		    	 objModel.setListcustomerDtl(listCustAreaDtl);
					objDao.funSave(objModel);

				    
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return customerAreaCode;


}


	public JSONObject funLoadCustomerAreaMasterData(String searchCode,String clientCode)
	{
		JSONObject jobjCustomerArea=new JSONObject();
		JSONArray jCustomerDtlData=new JSONArray();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("buildingCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			clsCustomerAreaMasterModel objCustomerAreaMasterModel = objCustomerAreaMasterDao.funLoadCustomerAreaMasterData("getCustomerArea", hmParameters);
			
			
			Set<clsCustomerAreaMasterAmountModel> listCustomerAreaDtl =objCustomerAreaMasterModel.getListcustomerDtl();
			
			Iterator itr = listCustomerAreaDtl.iterator();
		        while(itr.hasNext())
		        {
		        	clsCustomerAreaMasterAmountModel objCustomerArea=(clsCustomerAreaMasterAmountModel)itr.next();
				JSONObject jObjSettle=new JSONObject();
				jObjSettle.put("dblBillAmount",objCustomerArea.getDblBillAmount());
				jObjSettle.put("dblBillAmount1",objCustomerArea.getDblBillAmount1());
				jObjSettle.put("dblDeliveryCharges",objCustomerArea.getDblDeliveryCharges());
				jObjSettle.put("strCustTypeCode",objCustomerArea.getStrCustTypeCode());
			
				jCustomerDtlData.put(jObjSettle);
				
			}
			
		        jobjCustomerArea.put("strBuildingName",objCustomerAreaMasterModel.getStrBuildingName());
		        jobjCustomerArea.put("strAddress",objCustomerAreaMasterModel.getStrAddress());
		        jobjCustomerArea.put("dblHomeDeliCharge",objCustomerAreaMasterModel.getDblHomeDeliCharge());
		        jobjCustomerArea.put("strZoneCode",objCustomerAreaMasterModel.getStrZoneCode());
		        jobjCustomerArea.put("dblDeliveryBoyPayOut",objCustomerAreaMasterModel.getDblDeliveryBoyPayOut());
		        jobjCustomerArea.put("dblHelperPayOut",objCustomerAreaMasterModel.getDblHelperPayOut());		        			 
		        jobjCustomerArea.put("CustomerDtlData",jCustomerDtlData);
		    
		
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjCustomerArea;
	}
	
	public String funGetAllCustomerAreaForMaster(String clientCode)
	{
		return objCustomerAreaMasterDao.funGetAllCustomerAreaForMaster(clientCode);
	}
	
	

}
