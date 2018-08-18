package com.apos.service;

import java.util.Map;

import javax.swing.JOptionPane;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.apos.bean.clsCashManagementBean;
import com.apos.dao.clsPOSCashManagmentTranscationDao;
import com.apos.model.clsPOSCashManagmentTranscationModel;
import com.apos.model.clsPOSCashManagmentTranscationModel_ID;
import com.apos.model.clsZoneMasterModel;
import com.apos.model.clsZoneMasterModel_ID;

import com.webservice.util.clsUtilityFunctions;

@Service("clsPOSCashManagmentTranscationService")
public class clsPOSCashManagmentTranscationService{
	@Autowired
	private clsPOSCashManagmentTranscationDao objPOSCashManagmentTranscationDao;

	public String funSavePOSCashManagmentTranscation(JSONObject jObjPOSCashManagmentTranscation){

		String against="Direct";
		boolean flg=true;
		 double balanceAmt=0;
		  double rollingAmount=0;
		  double rollingAmt=0;		  
		String strCashManagementCode = "";
		try
		{
			   
			strCashManagementCode = jObjPOSCashManagmentTranscation.getString("strTransID");
		    String strTransType = jObjPOSCashManagmentTranscation.getString("strTransType");
		    String strTransDate = jObjPOSCashManagmentTranscation.getString("strTransDate");
		    double dblAmount = Double.parseDouble(jObjPOSCashManagmentTranscation.getString("strAmount"));
		    String strReasonCode = jObjPOSCashManagmentTranscation.getString("strReaSonCode");
		    String strCurrencyType = jObjPOSCashManagmentTranscation.getString("strCurrencyType");
		    String strRemarks = jObjPOSCashManagmentTranscation.getString("strRemarks");
		    String user = jObjPOSCashManagmentTranscation.getString("User");
		    String posCode = jObjPOSCashManagmentTranscation.getString("posCode");
		    String clientCode = jObjPOSCashManagmentTranscation.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		    strCashManagementCode="0000";
		
		    if(strTransType.equalsIgnoreCase("Rolling"))
		    {
		        
                    if(objPOSCashManagmentTranscationDao.funCheckUserEntryForRolling(user,strTransDate))
                    {
                       // JOptionPane.showMessageDialog(null, user+" has already entered rolling amount");
                        
                    flg=false;
               
                        
                    }
                 
                    Map<String,clsCashManagementBean> hmCashMgmtDtl=objPOSCashManagmentTranscationDao.funGetCashManagement(strTransDate.split(" ")[0], strTransDate.split(" ")[0],posCode);
                    balanceAmt=objPOSCashManagmentTranscationDao.funGetBalanceUserWise(strTransDate.split(" ")[0], strTransDate.split(" ")[0], hmCashMgmtDtl, user);
                    
                    dblAmount=((balanceAmt- Double.parseDouble(jObjPOSCashManagmentTranscation.getString("strAmount"))));
                    strTransType="Withdrawl";
                    against="Rolling";
                    rollingAmt= Double.parseDouble(jObjPOSCashManagmentTranscation.getString("strAmount"));
                    rollingAmount= Double.parseDouble(jObjPOSCashManagmentTranscation.getString("strAmount"));
		    }
		    
		    if(flg)
		    {
		      
			    strCashManagementCode = objPOSCashManagmentTranscationDao.funGenerateCashManagementCode();
			    		
		    	clsPOSCashManagmentTranscationModel objModel = new clsPOSCashManagmentTranscationModel(new clsPOSCashManagmentTranscationModel_ID(strCashManagementCode,clientCode));
		   
		    objModel.setStrTransType(strTransType);
		    objModel.setDteTransDate(strTransDate);
		    objModel.setDblAmount(dblAmount);
		    objModel.setStrReasonCode(strReasonCode);
		    objModel.setStrCurrencyType(strCurrencyType);
		    objModel.setStrRemarks(strRemarks);
	        objModel.setStrClientCode(clientCode);
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
		    objModel.setStrDataPostFlag("N");
		    objModel.setStrAgainst(against);
		    objModel.setStrPOSCode(posCode);
		    
		    
		    strCashManagementCode = objPOSCashManagmentTranscationDao.funSavePOSCashManagmentTranscation(objModel);
		   
		
		}
		}
		    
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return strCashManagementCode;

	}


}
