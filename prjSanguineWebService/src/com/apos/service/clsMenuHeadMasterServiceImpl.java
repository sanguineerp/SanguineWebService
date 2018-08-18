package com.apos.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.dao.clsMenuHeadMasterDao;
import com.apos.model.clsMenuHeadMasterModel;
import com.apos.model.clsMenuHeadMasterModel_ID;
import com.apos.model.clsSubMenuHeadMasterModel;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsMenuHeadMasterService")
public class clsMenuHeadMasterServiceImpl implements clsMenuHeadMasterService
{

	@Autowired
	private intfBaseService objSer;
	@Autowired
	clsMenuHeadMasterDao objMenuHeadMasterDao;
	
	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	@Override
	public JSONObject funLoadMasterDetails(String masterName, String clientCode) throws Exception
	{
		clsMenuHeadMasterModel objModel = new clsMenuHeadMasterModel();
	    JSONObject jObjLoadData = new JSONObject();
		JSONArray jArrData = new JSONArray();
		    List list =objSer.funLoadAll(objModel,clientCode);
			clsMenuHeadMasterModel objMenuHeadModel = null;
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objMenuHeadModel = (clsMenuHeadMasterModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strMenuCode", objMenuHeadModel.getStrMenuCode());
				objMenu.put("strMenuName", objMenuHeadModel.getStrMenuName());
				jArrData.put(objMenu);
			}
			jObjLoadData.put(masterName, jArrData);
			

			return jObjLoadData;

	}
	
	@Override
	public String funSaveMenuHeadMaster(JSONObject objMenuHeadMaster)
	{
		String menuCode = "";

		try
		{
			menuCode = objMenuHeadMaster.getString("MenuHeadCode");
			String menuHeadName = objMenuHeadMaster.getString("MenuHeadName").toUpperCase();
			String operational = objMenuHeadMaster.getString("Operational");
			String user = objMenuHeadMaster.getString("User");
			String clientCode = objMenuHeadMaster.getString("ClientCode");
			String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");

			if (menuCode.trim().isEmpty())
			{
				//MenuCode = objMenuHeadMasterDao.funGenerateMenuCode();
				List list=objUtilityFunctions.funGetDocumentCode("POSMenuHeadMaster");
				
				if (!list.get(0).toString().equals("0"))
				{
					String strCode = "0";
					String code = list.get(0).toString();
					StringBuilder sb = new StringBuilder(code);
					String ss = sb.delete(0, 1).toString();
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
						menuCode = "M00000" + intCode;
					}
					else if (intCode < 100)
					{
						menuCode = "M0000" + intCode;
					}
					else if (intCode < 1000)
					{
						menuCode = "M000" + intCode;
					}
					else if (intCode < 10000)
					{
						menuCode = "M00" + intCode;
					}
					else if (intCode < 100000)
					{
						menuCode = "M0" + intCode;
					}
					else if (intCode < 1000000)
					{
						menuCode  = "M" + intCode;
					}
				}
				else
				{
					menuCode = "M000001";
				}
			}
			clsMenuHeadMasterModel objModel = new clsMenuHeadMasterModel(new clsMenuHeadMasterModel_ID(menuCode, clientCode));
			objModel.setStrMenuName(menuHeadName);
			objModel.setStrOperational(operational);
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);
			objModel.setStrDataPostFlag("N");
			objModel.setImgImage(funBlankBlob());
			objSer.funSave(objModel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return menuCode;
	}
	
	@Override
	public String funGetAllMenuHeadForMaster(String clientCode)
	{
		//return objMenuHeadMasterDao.funGetAllMenuHeadForMaster(clientCode);
		JSONObject jObjMenuList = new JSONObject();	
		JSONArray jArrData = new JSONArray();
		clsMenuHeadMasterModel objModel = new clsMenuHeadMasterModel();
		try
		{
			 List list =objSer.funLoadAll(objModel,clientCode);
			clsMenuHeadMasterModel objMenuHeadModel = null;
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objMenuHeadModel = (clsMenuHeadMasterModel) list.get(cnt);
				JSONObject objMenu = new JSONObject();
				objMenu.put("strMenuCode", objMenuHeadModel.getStrMenuCode());
				objMenu.put("strMenuName", objMenuHeadModel.getStrMenuName());
				objMenu.put("Operational",false);
				jArrData.put(objMenu);
			}
			jObjMenuList.put("MenuList", jArrData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObjMenuList.toString();
		}
	}
	
	
	@Override
	public JSONObject funGetMenuHeadMasterData(String menuHeadCode,String clientCode) {
		// TODO Auto-generated method stub
		JSONObject jObjMenuHead=new JSONObject();
		JSONArray jArrData = new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("menuCode",menuHeadCode);
			hmParameters.put("clientCode",clientCode);
			clsMenuHeadMasterModel model = objMenuHeadMasterDao.funGetMenuHeadMasterData("getMenuHeadMaster", hmParameters);
			
		    
		    jArrData.put(model.getStrMenuCode());
		    jArrData.put(model.getStrMenuName());
		    jArrData.put(model.getStrOperational());
		    jObjMenuHead.put("POSMenuHeadMaster", jArrData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjMenuHead;
		
	}
	
	@Override
	public JSONObject funGetSubMenuHeadMasterData(String subMenuHeadCode,String clientCode)throws Exception
	{
		// TODO Auto-generated method stub
		JSONObject jObjSubMenuHead=new JSONObject();
		JSONArray jArrData = new JSONArray();
		try
		{
			Map<String,String> hmParameters=new HashMap<String,String>();
			hmParameters.put("subMenuCode",subMenuHeadCode);
			hmParameters.put("clientCode",clientCode);
			clsSubMenuHeadMasterModel model = objMenuHeadMasterDao.funGetSubMenuHeadMasterData("getSubMenuHeadMaster", hmParameters);
			
			jArrData.put(model.getStrSubMenuHeadCode());
			jArrData.put(model.getStrSubMenuHeadName());
			jArrData.put(model.getStrSubMenuHeadShortName());
			jArrData.put(model.getStrMenuCode());
			jArrData.put(model.getStrSubMenuOperational());
		    
		    jObjSubMenuHead.put("POSSubMenuHeadMaster", jArrData);
			// Write code to convert model into json object.
			
			System.out.println();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return jObjSubMenuHead;
		
	}
	
	private Blob funBlankBlob()
	 {
		 Blob blob=new Blob() {
			
			@Override
			public void truncate(long len) throws SQLException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int setBytes(long pos, byte[] bytes, int offset, int len)
					throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int setBytes(long pos, byte[] bytes) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public OutputStream setBinaryStream(long pos) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long position(Blob pattern, long start) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long position(byte[] pattern, long start) throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long length() throws SQLException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public byte[] getBytes(long pos, int length) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InputStream getBinaryStream(long pos, long length)
					throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InputStream getBinaryStream() throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void free() throws SQLException {
				// TODO Auto-generated method stub
				
			}
		};
		 return blob;
	 }
	 

}

