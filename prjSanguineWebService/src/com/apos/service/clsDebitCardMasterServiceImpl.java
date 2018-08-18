package com.apos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsPOSDebitCardMasterDao;
import com.apos.dao.intfCounterMasterDao;
import com.apos.dao.intfDebitCardMasterDao;
import com.apos.model.clsCounterMasterHdModel;
import com.apos.model.clsDebitCardMasterHdModel;
import com.apos.model.clsDebitCardMasterModel_ID;
import com.apos.model.clsDebitCardSettlementDtlModel;
import com.apos.model.clsPosCounterDtlModel;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsDebitCardMasterServiceImpl")
public class clsDebitCardMasterServiceImpl implements clsDebitCardMasterService
{
	@Autowired
	private intfDebitCardMasterDao objDebitCardMasterDao;
	
	@Autowired
	private intfBaseDao objDao;
	
	@Autowired
	clsUtilityFunctions utility;


	public String funAddUpdatePOSDebitCardMaster(JSONObject jObjDebitCardMaster){
		String cardTypeCode = "";
		try
		{
		    
			cardTypeCode = jObjDebitCardMaster.getString("CardTypeCode");
			String strPayModCash="N",strPayModParty="N",strPayModMember="N",strPayModCreditCard="N",strPayModStaff="N",strPayModCheque="N";
			String cardName = jObjDebitCardMaster.getString("strCardName");
		    String debitOnCredit = jObjDebitCardMaster.getString("strDebitOnCredit");
		    String roomCard = jObjDebitCardMaster.getString("strRoomCard");
		    String complimentary = jObjDebitCardMaster.getString("strComplementary");
		    String autoTopUp = jObjDebitCardMaster.getString("strAutoTopUp");
		    String redeemableCard = jObjDebitCardMaster.getString("strRedeemableCard");
		    String cardTypeInUse = jObjDebitCardMaster.getString("strCardInUse");
		    String entryCharge = jObjDebitCardMaster.getString("strEntryCharge");
		    String coverCharge = jObjDebitCardMaster.getString("strCoverCharge");
		    String diplomate = jObjDebitCardMaster.getString("strDiplomate");
		    String allowTopUp = jObjDebitCardMaster.getString("strAllowTopUp");
		    String extValidityOnTopUp = jObjDebitCardMaster.getString("strExValOnTopUp");
		    String customerCompulsory = jObjDebitCardMaster.getString("strCustomerCompulsory");
		    String  setExpiryDate= jObjDebitCardMaster.getString("strSetExpiryDt");
		    
		    String dteToDate = jObjDebitCardMaster.getString("dteExpiryDt");
		    String  currentFinYear= jObjDebitCardMaster.getString("strCurrentFinacialYr");
		    String  cashCard= jObjDebitCardMaster.getString("strCashCard");
		    long validDays = jObjDebitCardMaster.getInt("strValidityDays");
		    String  authorizeMemberCard= jObjDebitCardMaster.getString("strAuthorizeMemberCard");
		    double cardValueFixed = jObjDebitCardMaster.getDouble("strCardValueFixed");
		    double depositeAmount = jObjDebitCardMaster.getDouble("dblDepositAmt");
		    double maximum = jObjDebitCardMaster.getDouble("dblMaxVal");
		    double minimum = jObjDebitCardMaster.getDouble("dblMinVal");
		    double maxRefAmount = jObjDebitCardMaster.getDouble("dblMaxRefundAmt");
		    double minCharges = jObjDebitCardMaster.getDouble("dblMinCharge");
		    String redemptionLimitType = jObjDebitCardMaster.getString("strRedemptionLimitType");
		    double redumptionValue = jObjDebitCardMaster.getDouble("strRedemptionLimitValue");
		    String user = jObjDebitCardMaster.getString("User");
		    String clientCode = jObjDebitCardMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		   

	    
//		    if (cardTypeCode.trim().isEmpty())
//		    {
//		    	cardTypeCode = objPOSDebitCardMasterDao.funGenerateDebitCardTypeCode();
//		    }
//		    
		    if (cardTypeCode.trim().isEmpty())
			{
				long code=utility.funGetDocumentCodeFromInternal("POSDebitCardMaster");
				 if (code>0)
					{
					 cardTypeCode = "D" + String.format("%07d", code); 
			        }
			        
					
			}
		    
		    clsDebitCardMasterHdModel objModel=new clsDebitCardMasterHdModel(new clsDebitCardMasterModel_ID(cardTypeCode, clientCode));
			   
		    objModel.setStrCardTypeCode(cardTypeCode);
		    objModel.setStrCardName(cardName);
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrDebitOnCredit(debitOnCredit);
		    objModel.setStrRoomCard(roomCard);
		    objModel.setStrComplementary(complimentary);
		    objModel.setStrAutoTopUp(autoTopUp);
		    objModel.setStrRedeemableCard(redeemableCard);
		    objModel.setStrCardInUse(cardTypeInUse);
		    objModel.setStrEntryCharge(entryCharge);
		    objModel.setStrCoverCharge(coverCharge);
		    objModel.setStrDiplomate(diplomate);
		    objModel.setStrAllowTopUp(allowTopUp);
		    objModel.setStrExValOnTopUp(extValidityOnTopUp);
		    objModel.setStrExValOnTopUp(extValidityOnTopUp);
		    objModel.setStrCustomerCompulsory(customerCompulsory);
		    objModel.setStrSetExpiryDt(setExpiryDate);
		    objModel.setDteExpiryDt(dteToDate);
		    
		    objModel.setStrCurrentFinacialYr(currentFinYear);
		    objModel.setStrCashCard(cashCard);
		    objModel.setIntValidityDays(validDays);
		    objModel.setStrAuthorizeMemberCard(authorizeMemberCard);
		    objModel.setDblCardValueFixed(cardValueFixed);
		    objModel.setDblDepositAmt(depositeAmount);
		    objModel.setDblMaxVal(maximum);
		    objModel.setDblMinVal(minimum);
		    objModel.setDblMaxRefundAmt(maxRefAmount);
		    objModel.setDblMinCharge(minCharges);
		   
		    objModel.setStrRedemptionLimitType(redemptionLimitType);
		    objModel.setDblRedemptionLimitValue(redumptionValue);
		   
		    
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		 
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setStrClientCode(clientCode);
		    objModel.setStrPayModCash(strPayModCash);
		    objModel.setStrPayModParty(strPayModParty);
		    objModel.setStrPayModMember(strPayModMember);
		    objModel.setStrPayModCreditCard(strPayModCreditCard);
		    objModel.setStrPayModStaff(strPayModStaff);
		    objModel.setStrPayModCheque(strPayModCheque);

		    
		    JSONArray jArrOb=jObjDebitCardMaster.getJSONArray("SettlementDetails");
		    

		    String applicable="true";
				 JSONObject jObj=new JSONObject();
				 JSONObject jObjItem=new JSONObject();
				 List<clsDebitCardSettlementDtlModel> listDebitCardSettleDtl = new ArrayList<clsDebitCardSettlementDtlModel>();
				    for(int i=0;i<jArrOb.length();i++)
				    {
				    	jObj=jArrOb.getJSONObject(i);
				    	String settlementCode=jObj.getString("SettlementCode");
				    	String settlementDesc=jObj.getString("SettlementDesc");
				    	clsDebitCardSettlementDtlModel objDebitCardSettleModel = new clsDebitCardSettlementDtlModel();
				    	
				    	objDebitCardSettleModel.setStrSettlementCode(settlementCode);
				    	objDebitCardSettleModel.setStrApplicable(applicable);
				    	objDebitCardSettleModel.setStrDataPostFlag("N");
				    	listDebitCardSettleDtl.add(objDebitCardSettleModel);
				    
				    }
				    
				    objModel.setListDebitCardSettleDtl(listDebitCardSettleDtl);
				    objDao.funSave(objModel);
		    		   
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return cardTypeCode; 
	    }
	
	public JSONObject funGetSelectedDebitCardMasterData(String cardTypeCode,String clientCode)
	{
		JSONObject jObjDebitCardMaster=new JSONObject();
		JSONArray jSettleData=new JSONArray();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("cardTypeCode",cardTypeCode);
			hmParameters.put("clientCode",clientCode);
			clsDebitCardMasterHdModel model = objDebitCardMasterDao.funGetSelectedDebitCardMasterData("getDebitCardMaster", hmParameters);
			
			// Write code to convert model into json object.

			List<clsDebitCardSettlementDtlModel> listSettleDtl =model.getListDebitCardSettleDtl();
			 Iterator itr = listSettleDtl.iterator();
		        while(itr.hasNext())
		        {
		        	clsDebitCardSettlementDtlModel objSettle=(clsDebitCardSettlementDtlModel)itr.next();
				JSONObject jObjSettle=new JSONObject();
				jObjSettle.put("strSettlementCode",objSettle.getStrSettlementCode());
				
				jObjSettle.put("strApplicable",true);
				jSettleData.put(jObjSettle);
				
		        }
		        jObjDebitCardMaster.put("strCardTypeCode",model.getStrCardTypeCode());
		        jObjDebitCardMaster.put("strCardName",model.getStrCardName());
		        jObjDebitCardMaster.put("strDebitOnCredit",model.getStrDebitOnCredit());
		        jObjDebitCardMaster.put("strRoomCard",model.getStrRoomCard());
		        jObjDebitCardMaster.put("strComplementary",model.getStrComplementary());
		        jObjDebitCardMaster.put("strAutoTopUp",model.getStrAutoTopUp());
		        jObjDebitCardMaster.put("strRedeemableCard",model.getStrRedeemableCard());
		        jObjDebitCardMaster.put("strCardInUse",model.getStrCardInUse());
		        jObjDebitCardMaster.put("strEntryCharge",model.getStrEntryCharge());
		        jObjDebitCardMaster.put("strCoverCharge",model.getStrCoverCharge());
		        jObjDebitCardMaster.put("strDiplomate",model.getStrDiplomate());
		        jObjDebitCardMaster.put("strAllowTopUp",model.getStrAllowTopUp());
		        jObjDebitCardMaster.put("strExValOnTopUp",model.getStrExValOnTopUp());
		        jObjDebitCardMaster.put("strCustomerCompulsory",model.getStrCustomerCompulsory());
		        jObjDebitCardMaster.put("dteExpiryDt",model.getDteExpiryDt());
		        jObjDebitCardMaster.put("strCurrentFinacialYr",model.getStrCurrentFinacialYr());
		        jObjDebitCardMaster.put("strCashCard",model.getStrCashCard());
		        jObjDebitCardMaster.put("intValidityDays",model.getIntValidityDays());
		        jObjDebitCardMaster.put("strAuthorizeMemberCard",model.getStrAuthorizeMemberCard());
		        jObjDebitCardMaster.put("dblCardValueFixed",model.getDblCardValueFixed());
		        jObjDebitCardMaster.put("dblDepositAmt",model.getDblDepositAmt());
		        jObjDebitCardMaster.put("dblMaxVal",model.getDblMaxVal());
		        jObjDebitCardMaster.put("dDblMinVal",model.getDblMinVal());
		        jObjDebitCardMaster.put("dblMaxRefundAmt",model.getDblMaxRefundAmt());
		        jObjDebitCardMaster.put("dblMinCharge",model.getDblMinCharge());
		        jObjDebitCardMaster.put("dblRedemptionLimitValue",model.getDblRedemptionLimitValue());
		        jObjDebitCardMaster.put("strRedemptionLimitType",model.getStrRedemptionLimitType());
		        jObjDebitCardMaster.put("SettleDtl",jSettleData);
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjDebitCardMaster;
	}
	
	
	public String funGetAllPOSForMaster(String clientCode)
	{
		return objDebitCardMasterDao.funGetAllDebitCardForMaster(clientCode);
	}
}
