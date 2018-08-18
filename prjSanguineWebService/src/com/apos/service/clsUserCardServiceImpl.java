package com.apos.service;

import java.util.HashMap;
import java.util.List;







import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsUserCardDao;
import com.apos.model.clsSubGroupMasterHdModel;
import com.apos.model.clsUserDetailModel;
import com.apos.model.clsUserHdModel;
//import com.apos.model.clsUserDetailModel_ID;
import com.webservice.util.clsUtilityFunctions;

@Service("clsUserCardServiceImpl")
public class clsUserCardServiceImpl implements clsUserCardService
{

	@Autowired
	private clsUserCardDao objUserCardDao;
	
	
/*	public String funSaveUserCard(JSONObject jObjUserCardSwipe)
    {
	String userCode = "";
	try
	{
        userCode = jObjUserCardSwipe.getString("UserCode");
		String swipeCard = jObjUserCardSwipe.getString("SwipeCard");
		String operational = jObjUserCardSwipe.getString("Operational");
		String user = jObjUserCardSwipe.getString("User");
		String clientCode = jObjUserCardSwipe.getString("ClientCode");
		String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		if (userCode.trim().isEmpty())
		{
		    userCode = objUserCardDao.funGenerateUserCode();
		    //userCode = objUserCardDao.funUpdateUserCardString(userCode,swipeCard);
		    
		}
		clsUserDetailHdModel objModel = new clsUserDetailHdModel(new clsUserDetailModel_ID(userCode, clientCode));
		objModel.setStrDebitCardString(swipeCard);
		objModel.setStrOperationalYN(operational);
		objModel.setStrUserCreated(user);
		objModel.setStrUserEdited(user);
		objModel.setDteDateCreated(dateTime);
		objModel.setDteDateEdited(dateTime);
		objModel.setStrDataPostFlag("N");
		
		
		//userCode = objUserCardDao.funSaveUserCard(objModel);
		
		//userCode = objUserCardDao.funUpdateUserCardString(userCode,swipeCard);
		
		
	}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return userCode;
	    }
	*/    
	
	    
	public String funSaveUserCard(JSONObject jObjUserCardSwipe)
    {
	String userCode = "";
	try
	{
        userCode = jObjUserCardSwipe.getString("UserCode");
		String swipeCard = jObjUserCardSwipe.getString("SwipeCard");
		String superType = jObjUserCardSwipe.getString("SuperType");
		String user = jObjUserCardSwipe.getString("User");
		String clientCode = jObjUserCardSwipe.getString("ClientCode");
		String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
		if (userCode.trim().isEmpty())
		{
		    //userCode = objUserCardDao.funGenerateUserCode();
		    userCode = objUserCardDao.funUpdateUserCardString(userCode,swipeCard);
		    
		}
		clsUserDetailModel objModel = new clsUserDetailModel();
//		objModel.setStrDebitCardString(swipeCard);
//		objModel.setStrSuperType(superType);
//		objModel.setStrUserCreated(user);
//		objModel.setStrUserEdited(user);
//		objModel.setDteDateCreated(dateTime);
//		objModel.setDteDateEdited(dateTime);
//		objModel.setStrDataPostFlag("N");
		
		
		//userCode = objUserCardDao.funSaveUserCard(objModel);
		
		userCode = objUserCardDao.funUpdateUserCardString(userCode,swipeCard);
		
		
	}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		return userCode;
	    } 
	    
	
	
	
	
	



		public String funUpdateUserCardString(JSONObject jObjUserCardSwipe) throws JSONException
		{
			String  userCode = jObjUserCardSwipe.getString("UserCode");
				String swipeCard = jObjUserCardSwipe.getString("SwipeCard");
				
			return objUserCardDao.funUpdateUserCardString(userCode,swipeCard);
		}









		@Override
		public JSONObject funGetUserCardData(String subGroupCode,
				String clientCode) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}









	  /*  
		
		@Override
		public JSONObject funGetUserCardData(String subGroupCode,
				String clientCode) throws Exception {
			// TODO Auto-generated method stub
			JSONObject jObjModGroup=new JSONObject();
			JSONArray jArrData = new JSONArray();
			try
			{
				Map<String,String> hmParameters=new HashMap<String,String>();
				hmParameters.put("subGroupCode",subGroupCode);
				hmParameters.put("clientCode",clientCode);
				clsUserHdModel objSubGroupModel = objUserCardDao.funGetUserCardData("getUserMaster", hmParameters);
			    jArrData.put(objSubGroupModel.getStrSubGroupCode());
			    jArrData.put(objSubGroupModel.getStrSubGroupName());
			    jArrData.put(objSubGroupModel.getStrGroupCode());
			    jArrData.put(objSubGroupModel.getStrIncentives());
				
			    
				jObjModGroup.put("POSSubGroupMaster", jArrData);
				// Write code to convert model into json object.
				
				System.out.println();
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return jObjModGroup;
		}*/
	
}
