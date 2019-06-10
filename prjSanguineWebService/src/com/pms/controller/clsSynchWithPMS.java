package com.pms.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.webservice.controller.clsDatabaseConnection;

@Path("/PMSIntegration")
public class clsSynchWithPMS {

	@SuppressWarnings("finally")
	@GET 
	@Path("/funInvokePMSWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funCheckPMSWS()
	{
		String response = "false";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection pmsCon=null;
		try
		{
			pmsCon=objDb.funOpenWebPMSCon("mysql","transaction");
			response="true";
		}
		catch(Exception e)
		{
			response="false";
			e.printStackTrace();
		}
		return Response.status(201).entity(response).build();
	}
	
	
	@GET
	@Path("/funGetFolioDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	public String funGetFolioDetails(@QueryParam("ClientCode") String clientCode)
	{
		JSONObject jObjFolio=new JSONObject();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection pmsCon=null;
       
		try
		{
			pmsCon=objDb.funOpenWebPMSCon("mysql","master");
            Statement st = pmsCon.createStatement();
			String sql= " select a.strFolioNo,a.strRoomNo,c.strRoomDesc,concat(b.strFirstName,' ',b.strMiddleName,' ',b.strLastName),b.strGuestCode "
				+ " from tblfoliohd a,tblguestmaster b,tblroom c "
				+ " where a.strGuestCode=b.strGuestCode and a.strRoomNo=c.strRoomCode and a.strClientCode='"+clientCode+"' ";
			System.out.println(sql);
			
			JSONArray arrObjFolioDtl=new JSONArray();
	        ResultSet rsFolio=st.executeQuery(sql);
	        while(rsFolio.next())
	        {
	        	JSONObject objFolio=new JSONObject();
	        	objFolio.put("FolioNo", rsFolio.getString(1));
	        	objFolio.put("RoomNo", rsFolio.getString(2));
	        	objFolio.put("RoomDesc", rsFolio.getString(3));
	        	objFolio.put("GuestName", rsFolio.getString(4));
	        	objFolio.put("GuestCode", rsFolio.getString(5));
	        	arrObjFolioDtl.put(objFolio);
	        }
	        rsFolio.close();
	        st.close();
	        pmsCon.close();
	        jObjFolio.put("FolioDtls", arrObjFolioDtl);
    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
        {
            return jObjFolio.toString();
        }
	}
	
	
	@GET
	@Path("/funGetData")
	@Consumes(MediaType.APPLICATION_JSON)
	public String funGetTestData()
	{
		JSONObject jObjStudent=new JSONObject();
		try
		{
			JSONArray arrObjFolioDtl=new JSONArray();
	        JSONObject objFolio=new JSONObject();
	        objFolio.put("StudCode", "ST00001");
	        objFolio.put("StudName", "ABCD");
	        arrObjFolioDtl.put(objFolio);
	        
	        objFolio=new JSONObject();
	        objFolio.put("StudCode", "ST00002");
	        objFolio.put("StudName", "XYZ");
	        arrObjFolioDtl.put(objFolio);
	        
	        jObjStudent.put("Students", arrObjFolioDtl);
    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
        {
            return jObjStudent.toString();
        }
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPOSTSettlementDtlToPMS")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funInsertFolioDtlInPMS(JSONObject objBillData)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection pmsCon=null;
        Statement st=null;
        StringBuilder sbSql=new StringBuilder();
        String response="false";
       
		try
		{
		
			pmsCon=objDb.funOpenWebPMSCon("mysql","master");
            st = pmsCon.createStatement();
            JSONArray mJsonArray = (JSONArray) objBillData.get("FolioTaxDtl");
            for (int i = 0; i < mJsonArray.length(); i++) {
            	JSONObject mJsonObject =(JSONObject) mJsonArray.get(i);
	            
				String billNo=mJsonObject.get("BillNo").toString().trim();
				String folioNo=mJsonObject.get("FolioNo").toString().trim();
				String POSCode=mJsonObject.get("PosCode").toString().trim();
				String posName=mJsonObject.get("PosName").toString().trim();
				String billDate=mJsonObject.get("Billdate").toString().trim();
				double taxableAmt=Double.parseDouble(mJsonObject.get("TaxableAmt").toString().trim());
				double taxAmt=Double.parseDouble(mJsonObject.get("TaxAmt").toString().trim());
				String taxCode=mJsonObject.get("TaxCode").toString().trim();
				String taxDesc=mJsonObject.get("TaxDesc").toString().trim();
				String roomNo=mJsonObject.get("RoomNo").toString().trim();
				String clientCode=mJsonObject.get("ClientCode").toString().trim();
				String userName=mJsonObject.get("UserName").toString().trim();
				
				sbSql.setLength(0);
				/*sbSql.append("insert into tblposfoliotax(strFolioNo,strBillNo,dteDocDate,strRoomNo,strPosCode,strPosName,strTaxCode,strTaxDesc,dblTaxableAmount,dblTaxAmount,strUserCreated,strClientCode)"
						+ " values ('"+folioNo+"','"+billNo+"','"+billDate+"','"+roomNo+"','"+POSCode+"','"+posName+"','"+taxCode+"','"+taxDesc+"',"
								+ ""+taxableAmt+","+taxAmt+",'"+userName+"','"+clientCode+"');");
				st.execute(sbSql.toString());*/
				sbSql.append("insert into tblfoliotaxdtl(strFolioNo,strDocNo,strTaxCode,strTaxDesc,dblTaxableAmt,dblTaxAmt,strClientCode) "
						+ " values ('"+folioNo+"','"+billNo+"','"+taxCode+"','"+taxDesc+"',"+taxableAmt+","+taxAmt+",'"+clientCode+"')");
				st.execute(sbSql.toString());
				
            }
            
			mJsonArray = (JSONArray) objBillData.get("FolioDtl");
			
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				JSONObject mJsonObject =(JSONObject) mJsonArray.get(i);
		            
				String billNo=mJsonObject.get("BillNo").toString().trim();
				String POSCode=mJsonObject.get("POSCode").toString().trim();
				String billDate=mJsonObject.get("BillDate").toString().trim();
				String folioNo=mJsonObject.get("FolioNo").toString().trim();
				String guestCode=mJsonObject.get("GuestCode").toString().trim();
				String roomNo=mJsonObject.get("RoomNo").toString().trim();
				String settledAmt=mJsonObject.get("SettledAmt").toString().trim();
				String clientCode=mJsonObject.get("ClientCode").toString().trim();
				String billType=mJsonObject.get("BillType").toString().trim();
				String posName=mJsonObject.get("POSName").toString().trim(); // Sumeet
				
				sbSql.setLength(0);
				sbSql.append("delete from tblfoliodtl where strFolioNo='"+folioNo+"' and strDocNo='"+billNo+"' "
						+ "and strClientCode='"+clientCode+"'");
				st.execute(sbSql.toString());
				
				sbSql.setLength(0);
				/*sbSql.append("insert into tblfoliodtl (strFolioNo,dteDocDate,strDocNo,strPerticulars"
					+ ",dblDebitAmt,dblCreditAmt,dblBalanceAmt,strRevenueType,strRevenueCode,strClientCode) "
					+ "values ('"+folioNo+"','"+billDate+"','"+billNo+"','POS Revenue',"+settledAmt+",0,0"
							+ ",'"+billType+"','"+POSCode+"','"+clientCode+"')");*/
				sbSql.append("insert into tblfoliodtl (strFolioNo,dteDocDate,strDocNo,strPerticulars"
						+ ",dblDebitAmt,dblCreditAmt,dblBalanceAmt,strRevenueType,strRevenueCode,strClientCode) "
						+ "values ('"+folioNo+"','"+billDate+"','"+billNo+"','POS Revenue ("+posName+") ',"+settledAmt+",0,0"
								+ ",'"+billType+"','"+POSCode+"','"+clientCode+"')"); // Sumeet
				st.execute(sbSql.toString());
			}
			response = "true";
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				st.close();
				pmsCon.close();
				
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return Response.status(201).entity(response).build();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/funPostPOSDayEnd")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostPOSDayEnd(JSONObject objDayEnd)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection pmsCon=null;
        Statement st=null;
        StringBuilder sbSql=new StringBuilder();
        String response="false";
        try
		{
			pmsCon=objDb.funOpenWebPMSCon("mysql","master");
            st = pmsCon.createStatement();
			JSONArray mJsonArray = (JSONArray) objDayEnd.get("PostDayEnd");
			for (int i = 0; i < mJsonArray.length(); i++) 
			{
				JSONObject mJsonObject =(JSONObject) mJsonArray.get(i);
		        String posCode=mJsonObject.get("POSCode").toString().trim();
				String posName=mJsonObject.get("POSName").toString().trim();
				String dayEndDate=mJsonObject.get("DayEndDate").toString().trim();
				String userCreated=mJsonObject.get("UserCreated").toString().trim();
				String userEdited=mJsonObject.get("UserEdited").toString().trim();
				String dateCreated=mJsonObject.get("DateCreated").toString().trim();
				String dateEdited=mJsonObject.get("DateEdited").toString().trim();
				String status=mJsonObject.get("PostStatus").toString().trim();
				String clientCode=mJsonObject.get("ClientCode").toString().trim();
				dayEndDate = dayEndDate.substring(0,9);
				sbSql.setLength(0);
				sbSql.append(" DELETE FROM tblposdayend WHERE strPOSCode='"+posCode+"' AND DATE(dteDayEndDate)='"+dayEndDate+"'; ");
				st.execute(sbSql.toString());
				
				sbSql.setLength(0);
				sbSql.append(" INSERT INTO tblposdayend (strPOSCode,strPOSName,dteDayEndDate,strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strStatus,strClientCode)"
						+ " VALUES('"+posCode+"','"+posName+"','"+dayEndDate+"','"+userCreated+"','"+userEdited+"','"+dateCreated+"',"
						+ " '"+dateEdited+"','"+status+"','"+clientCode+"') ");
				st.execute(sbSql.toString());
			}
			response = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				st.close();
				pmsCon.close();
				
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return Response.status(201).entity(response).build();
		}
	}
}
