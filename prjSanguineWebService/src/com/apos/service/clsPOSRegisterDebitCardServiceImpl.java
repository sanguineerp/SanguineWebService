package com.apos.service;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.apos.dao.clsPOSDebitCardMasterDao;
import com.apos.dao.clsPOSRegisterDebitCardDao;
import com.apos.dao.inftZoneMasterDao;
import com.apos.dao.intfRecipeMasterDao;
import com.apos.dao.intfRegisterDebitCardMasterDao;
import com.apos.model.clsCostCenterMasterModel;
import com.apos.model.clsCostCenterMasterModel_ID;
import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsDebitCardMasterModel_ID;
import com.apos.model.clsFactoryMasterHdModel;
import com.apos.model.clsPOSRegisterDebitCardHdModel;
import com.apos.model.clsPOSRegisterDebitCardModel_ID;
import com.apos.model.clsZoneMasterModel;
import com.apos.model.clsZoneMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsPOSRegisterDebitCardServiceImpl")
public class clsPOSRegisterDebitCardServiceImpl implements clsPOSRegisterDebitCardService
{
	
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	clsUtilityFunctions utility;
	
	@Autowired
	intfRegisterDebitCardMasterDao objinftDao;
	

	
	double redeemAmt=0.00, cardValue=0.00, minCharges=0.00;
	String custemerCode = "", extCode = "";
	
	
	
	public JSONObject funRegisterCard(JSONObject jObjRegisterDebitCardMaster)
	{
		String cardStatus = "Active";
		String zoneCode = "",cardNo="",cardCode="";
		JSONObject jObj = new JSONObject();
		try
		{
		   
			 cardCode = jObjRegisterDebitCardMaster.getString("CardTypeCode");
			
			String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
        	String user = jObjRegisterDebitCardMaster.getString("User");
			String clientCode = jObjRegisterDebitCardMaster.getString("ClientCode");
			String customerName = jObjRegisterDebitCardMaster.getString("CustomerName");
			String cardString = jObjRegisterDebitCardMaster.getString("CardString");
			String posCode = jObjRegisterDebitCardMaster.getString("POSCode");
			
			
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("cardTypeCode",cardCode);
			hmParameters.put("clientCode",clientCode);
			
			clsDebitCardMasterHdModel objModel1 = objinftDao.funRegisterCard("getCardDtl", hmParameters);
			
			cardValue = objModel1.getDblCardValueFixed();
			minCharges = objModel1.getDblMinCharge();
			redeemAmt = redeemAmt - (cardValue + minCharges);
			
			String cardTypeCd = objModel1.getStrCardTypeCode();
			String cardTypeCode = cardTypeCd.substring(cardTypeCd.length() - 3);
//         String cardCode="D0000003";
			
			 if (cardNo.trim().isEmpty())
				{
					long code=utility.funGetDebitCardNo("CardNo");
					 if (code>0)
						{
						 cardNo = cardTypeCode + String.format("%06d", code); 
						 jObj.put("cardNo", cardNo);
				        }
				        
						
				}
			 
			 clsPOSRegisterDebitCardHdModel objModel = new clsPOSRegisterDebitCardHdModel(new clsPOSRegisterDebitCardModel_ID(cardNo,clientCode));
			   
		    objModel.setStrCardTypeCode(cardCode);
	        objModel.setStrCardNo(cardNo);
		    objModel.setDblRedeemAmt(redeemAmt);
		    objModel.setStrStatus(cardStatus);
		    objModel.setStrUserCreated(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setStrCustomerCode(customerName);
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrClientCode(clientCode);
		    objModel.setStrCardString(cardString);
		    objModel.setStrReachrgeRemark("");
		    objModel.setStrRefMemberCode("");
		    objDao.funSave(objModel);
		    System.out.println("");
		
		}

		catch (Exception e)
		{
		    e.printStackTrace();
		}

		return jObj;
	
}
	
	public JSONObject funDelistCard(JSONObject jObjRegisterDebitCardMaster)
	{
		String cardStatus = "";
		JSONObject jObj = new JSONObject();
		String zoneCode = "",cardNo="",cardCode="";
		try
		{
		   
			 cardCode = jObjRegisterDebitCardMaster.getString("CardTypeCode");
			 jObj.put("cardCode", cardCode);
			String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
        	String user = jObjRegisterDebitCardMaster.getString("User");
			String clientCode = jObjRegisterDebitCardMaster.getString("ClientCode");
			String customerName = jObjRegisterDebitCardMaster.getString("CustomerName");
			String cardString = jObjRegisterDebitCardMaster.getString("CardString");
			String posCode = jObjRegisterDebitCardMaster.getString("POSCode");
			cardStatus = jObjRegisterDebitCardMaster.getString("StrStatus");
			
			if (cardStatus.equalsIgnoreCase("Register"))
	        {
	            cardStatus = "Active";
	        }
	        else
	        {
	            cardStatus = "Deactive";
	        }
			

			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("cardString",cardString);
			hmParameters.put("clientCode",clientCode);
			
			long count = objinftDao.funDelistCardCount("cardNoToDelist", hmParameters);
			
            if (count > 0)
            {
            	

			clsPOSRegisterDebitCardHdModel model = new clsPOSRegisterDebitCardHdModel(new clsPOSRegisterDebitCardModel_ID(cardStatus,clientCode));
			

			model.setStrStatus(cardStatus);
			model.setStrCardTypeCode(cardCode);
			model.setStrClientCode(clientCode);
			model.setDteDateCreated(dateTime);
			model.setStrCardString(cardString);
			model.setStrDataPostFlag("N");
			model.setStrReachrgeRemark("");
		    model.setStrRefMemberCode("");
		    model.setStrUserCreated(user);
			objDao.funSave(model);

		    System.out.println("");
            }
		}

		catch (Exception e)
		{
		    e.printStackTrace();
		}

		return jObj;
	
	}
	
	
	public JSONObject funCheckCardString(String cardString,String clientCode) throws Exception
	{
		Map<String,String> hmParameters=new HashMap<String,String>();
		hmParameters.put("cardString",cardString);
		hmParameters.put("clientCode",clientCode);
		
		StringBuilder sbSql=new StringBuilder();

		sbSql.append("select registerDebitCard.strCardTypeCode,registerDebitCard.strCardNo, registerDebitCard.strCustomerCode,b.strCustomerName,registerDebitCard.strStatus ");
		sbSql.append("from tbldebitcardmaster registerDebitCard left outer join tblcustomermaster b on registerDebitCard.strCustomerCode = b.strCustomerCode ");
		sbSql.append("where registerDebitCard.strCardString='"+cardString+"' and registerDebitCard.strClientCode='"+clientCode+"'");
		
		List list= (List) objDao.funGetList(sbSql, "sql");
		
		JSONObject jObj = new JSONObject();
		
		if(null!=list)
		{
			for(int i=0;i<list.size();i++)
			{
				Object[] obj = (Object[]) list.get(i);
				
//				jObj.put("cardString", Array.get(obj, 1));
				jObj.put("cardNo", Array.get(obj, 1));
				jObj.put("status", Array.get(obj, 4));
				jObj.put("cardTypeCode", Array.get(obj, 0));
				jObj.put("customerCode", Array.get(obj, 2));
				jObj.put("customerName", Array.get(obj, 3));
				jObj.put("message","Card Already Register");
			}
			
		}
		else
		{
			jObj.put("message","Card is not Register");
		}
		
		
		return jObj;
	}
	
}
