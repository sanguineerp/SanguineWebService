package com.apos.service;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apos.model.clsSubMenuHeadMasterModel;
import com.apos.model.clsSubMenuHeadMasterModel_ID;
import com.sanguine.dao.intfBaseDao;
import com.sanguine.service.intfBaseService;
import com.webservice.util.clsUtilityFunctions;

@Service("clsSubMenuHeadMasterService")
public class clsSubMenuHeadMasterService implements intfSubMenuHeadMasterService
{
	@Autowired
	intfBaseService	objSer;

	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	
	public String funSaveSumMenuMaster(JSONObject jObjSubMenuMaster)
	{
		String subMenuCode = "";
		try
		{
			subMenuCode = jObjSubMenuMaster.getString("SubMenuHeadCode");
			String subMenuName = jObjSubMenuMaster.getString("SubMenuHeadName");
			String MenuCode = jObjSubMenuMaster.getString("MenuCode");
			String subMenuHeadShortName = jObjSubMenuMaster.getString("SubMenuHeadShortName");
			String operational = jObjSubMenuMaster.getString("Operational");
			String user = jObjSubMenuMaster.getString("User");
			String clientCode = jObjSubMenuMaster.getString("ClientCode");
			String dateTime = new clsUtilityFunctions().funGetCurrentDateTime("yyyy-MM-dd");
			if (subMenuCode.trim().isEmpty())
			{
				//subMenuCode = objSubMenuHeadMasterDao.funGenerateSubMenuCode();
				List list=objUtilityFunctions.funGetDocumentCode("POSSubMenuHead");
				if (!list.get(0).toString().equals("0"))
				{
					String strCode = "0";
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
						subMenuCode = "SM00000" + intCode;
					}
					else if (intCode < 100)
					{
						subMenuCode = "SM0000" + intCode;
					}
					else if (intCode < 1000)
					{
						subMenuCode = "SM000" + intCode;
					}
					else if (intCode < 10000)
					{
						subMenuCode = "SM00" + intCode;
					}
					else if (intCode < 100000)
					{
						subMenuCode = "SM0" + intCode;
					}
					else if (intCode < 1000000)
					{
						subMenuCode = "SM" + intCode;
					}
				}
				else
				{
					subMenuCode = "SM000001";
				}
			}
			clsSubMenuHeadMasterModel objModel = new clsSubMenuHeadMasterModel(new clsSubMenuHeadMasterModel_ID(subMenuCode, clientCode));
			objModel.setStrSubMenuHeadCode(subMenuCode);
			objModel.setStrSubMenuHeadName(subMenuName);
			objModel.setStrSubMenuHeadShortName(subMenuHeadShortName);
			objModel.setStrMenuCode(MenuCode);
			objModel.setStrSubMenuOperational(operational);
			objModel.setStrUserCreated(user);
			objModel.setStrUserEdited(user);
			objModel.setDteDateCreated(dateTime);
			objModel.setDteDateEdited(dateTime);

			subMenuCode = objSer.funSave(objModel);//funSaveSubMenuMaster(objModel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return subMenuCode;
	}

	public String funGetAllSubMenuHeadForMaster(String clientCode)throws Exception
	{
		//return objSubMenuHeadMasterDao.funGetAllSubMenuHeadForMaster(clientCode);
		JSONObject jObjSubMenuList = new JSONObject();
		JSONArray jArrData = new JSONArray();

		try
		{
			clsSubMenuHeadMasterModel objSubMenuHeadModel = new clsSubMenuHeadMasterModel();
			 List list =objSer.funLoadAll(objSubMenuHeadModel,clientCode);
			for (int cnt = 0; cnt < list.size(); cnt++)
			{
				objSubMenuHeadModel = (clsSubMenuHeadMasterModel) list.get(cnt);
				JSONObject objSubMenuHead = new JSONObject();
				objSubMenuHead.put("strSubMenuHeadCode", objSubMenuHeadModel.getStrSubMenuHeadCode());
				objSubMenuHead.put("strSubMenuheadName", objSubMenuHeadModel.getStrSubMenuHeadName());

				jArrData.put(objSubMenuHead);
			}
			jObjSubMenuList.put("SubMenuList", jArrData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObjSubMenuList.toString();
		}
	}


}
