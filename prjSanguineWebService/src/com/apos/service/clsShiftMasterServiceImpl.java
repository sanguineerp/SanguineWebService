package com.apos.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.apos.dao.clsShiftMasterDaoImpl;
import com.apos.dao.inftShiftMasterDao;
import com.apos.model.clsShiftMasterModel;
import com.apos.model.clsShiftMasterModel_ID;
import com.apos.model.clsZoneMasterModel;
import com.sanguine.dao.intfBaseDao;
import com.webservice.util.clsUtilityFunctions;

@Service("clsShiftMasterService")
public class clsShiftMasterServiceImpl implements clsShiftMasterService{
	@Autowired
	private intfBaseDao objDao;  
	
	@Autowired
	clsUtilityFunctions utility;
	
	@Autowired
	inftShiftMasterDao  objinftDao;
	
	public String funSaveShiftMaster(JSONObject jObjShiftMaster){

		String shiftCode = "";
		try
		{
		   
			shiftCode = jObjShiftMaster.getString("ShiftCode");
		    String posCode = jObjShiftMaster.getString("POSCode");
		    String shiftStart = jObjShiftMaster.getString("ShiftStart");
		    String shiftEnd = jObjShiftMaster.getString("ShiftEnd");
		    String AMPMStart = jObjShiftMaster.getString("AMPMStart");
		    String AMPMEnd = jObjShiftMaster.getString("AMPMEnd");
		    String billDateTimeType = jObjShiftMaster.getString("BillDateTimeType");
		    String user = jObjShiftMaster.getString("User");
		    String clientCode = jObjShiftMaster.getString("ClientCode");
		    String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		    String StartTime=shiftStart+" "+AMPMStart;
		    String StartEnd=shiftEnd+" "+AMPMEnd;
		    if (shiftCode.trim().isEmpty())
		    {
		    	List list=utility.funGetDocumentCode("POSShiftMaster");
		    	if (list.size()>(0))
				{
				   
				    int code =  Integer.parseInt(list.get(0).toString());
				    code++;
				    shiftCode=String.valueOf(code);
				}
				else
				{
					shiftCode = "1";
				}	
					

		    }
		    clsShiftMasterModel objModel = new clsShiftMasterModel(new clsShiftMasterModel_ID(shiftCode,clientCode));
		   
		    objModel.setStrPOSCode(posCode);
	        objModel.setTmeShiftStart(StartTime);
	        objModel.setTmeShiftEnd(StartEnd);
	        objModel.setStrBillDateTimeType(billDateTimeType);
	     
		    objModel.setStrUserCreated(user);
		    objModel.setStrUserEdited(user);
		    objModel.setDteDateCreated(dateTime);
		    objModel.setDteDateEdited(dateTime);
	
		    objDao.funSave(objModel);    
		   // shiftCode = objShiftMasterDao.funSaveShiftMaster(objModel);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return shiftCode;


	}
	@Override
	public JSONObject funLoadShiftMasterData(String searchCode, String clientCode) {

		JSONObject jobjData=new JSONObject();
		
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("shiftCode",searchCode);
			hmParameters.put("clientCode",clientCode);
			
			clsShiftMasterModel objModel = objinftDao.funLoadShiftMasterData("getShift", hmParameters);														
			
			jobjData.put("intShiftCode",objModel.getIntShiftCode());
			jobjData.put("strPOSCode",objModel.getStrPOSCode());
			jobjData.put("tmeShiftStart",objModel.getTmeShiftStart());
			jobjData.put("tmeShiftEnd",objModel.getTmeShiftEnd());
			jobjData.put("strBillDateTimeType",objModel.getStrBillDateTimeType());
				
		
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jobjData;
	}


}
