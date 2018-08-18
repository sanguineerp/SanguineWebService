package com.cmsws.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.crmws.listener.intfSyncDataWithCMS;
import com.webservice.controller.clsConfigFile;
import com.webservice.controller.clsDatabaseConnection;

//192.168.1.126:8080/PrjCRMWebService/CMSIntegration
//   /funGetCMSMember?strMemberCode=1234

@Path("/CMSIntegration")
public class clsCMSIntegration implements intfSyncDataWithCMS 
{

	// HTTP Get Method
			@GET 
			@Path("/funGetCMSMember")
			@Produces(MediaType.APPLICATION_JSON)
			public String funGetCMSMember(@QueryParam("strMemberCode") String memberCode)
			{
				String response = "";
				String memberData=funGetMemberDataFromDB(memberCode);
				if(memberData.equalsIgnoreCase("no data"))
				{
//					response = clsUtitlity.funConstructJSONForErrorMsg("CMSMemberData", false, "Data Not Found").toString();
				}
				else
				{
					response = memberData;
				}
				return response;
			}
			
			
			public String funGetMemberDataFromDB(String memberCode)
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        String memberData="";
		        Connection cmsCon=null;
		        JSONObject jObj=new JSONObject();
		        Statement st = null;
		        
		        try
		        {
		        	cmsCon=objDb.funOpenCMSCon("mssql","trans");
		        	st = cmsCon.createStatement();
		        	
		        	String dbMaster="",dbTrans="";
		        	String[] spDbName=clsConfigFile.cmsDBName.split(",");
		            dbMaster=spDbName[0];
		            dbTrans=spDbName[1];
		            
		            /*
		            String sql="select a.strDebtorCode,a.strDebtorFullName,a.strExpired,isnull(b.strAddReasDesc ,'') "
		            		+ " as Reason, dbo.calculateBalance('0015-01-00','"+memberCode+"', getdate()) as balance,"
		            		+ " c.intCreditLimit "
		            		+ " from "+dbMaster+"..tblDebtorMaster a left outer join "+dbMaster+"..tblAddReason b "
		            		+ " on a.strExpiryReasonCode=b.strAddReasCode "
		            		+ "left outer join "+dbMaster+"..tblCategoryMaster c on a.strCatCode=c.strCatCode "
		            		+ "where  a.strDebtorCode='"+memberCode+"'";*/
		            
		            String debtorCode=memberCode.trim();
		            memberCode=funGenerateMemberCodeForCMS(memberCode);		            
		            
		            if(memberCode.contains(" "))
		            {
		            	int index=memberCode.indexOf(" ");
		            	debtorCode=memberCode.substring(0, index);
		            }
		            System.out.println("Dbtr Code="+debtorCode);
		            
		            String sql="select h.strMemberCode,h.strFullName,a.strExpired,isnull(b.strAddReasDesc ,'') "
		            	+ " as Reason, dbo.calculateBalance('0015-01-00','"+memberCode+"', getdate()) as balance,"
		            	+ " c.intCreditLimit,h.strStopCredit "
		            	+ " from "+dbMaster+"..tblMemberMaster h left outer join "+dbMaster+"..tblDebtorMaster a  "
		            	+ " on h.strDebtorCode=a.strDebtorCode "
		            	+ " left outer join "+dbMaster+"..tblAddReason b "
		            	+ " on a.strExpiryReasonCode=b.strAddReasCode "
		            	+ "left outer join "+dbMaster+"..tblCategoryMaster c on a.strCatCode=c.strCatCode "
		            	+ "where  h.strMemberCode='"+memberCode+"'";
 
		            System.out.println(sql);
		            JSONArray arrObj=new JSONArray();
		            
		            ResultSet rsMemeberData=st.executeQuery(sql);
		            if(rsMemeberData.next())
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("DebtorCode",rsMemeberData.getString(1).trim());
		            	obj.put("DebtorName",rsMemeberData.getString(2).trim());
		            	obj.put("Expired",rsMemeberData.getString(3).trim());
		            	obj.put("Reason",rsMemeberData.getString(4).trim());
		            	obj.put("BalanceAmt",Double.parseDouble(rsMemeberData.getString(5).trim()));
		            	obj.put("CreditLimit",Double.parseDouble(rsMemeberData.getString(6).trim()));
		            	obj.put("StopCredit",rsMemeberData.getString(7).trim());
		            	arrObj.put(obj);
		            }
		            else
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("DebtorCode","no data");
		            	arrObj.put(obj);
		            }
		            rsMemeberData.close();
		            jObj.put("MemberInfo", arrObj);
		            cmsCon.close();
		            
		        }catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        finally
		        {
		        }
		        return jObj.toString();
			}
			
			
			
			private String funGenerateMemberCodeForCMS(String memCode)
			{
				String memberCode=memCode;
				if(memCode.contains(" "))
	            {
					int index=memCode.indexOf(" ");
	            	memberCode=memCode.substring(0, index);
	            }
				
				int spaces=8-memberCode.length();
            	for(int cnt=0;cnt<spaces;cnt++)
            	{
            		memberCode+=" ";
            	}
				memberCode+="01";
				return memberCode;
			}
			
			
			
			@GET 
			@Path("/funGetCMSMemberData")
			@Produces(MediaType.APPLICATION_JSON)
			public String funGetCMSMemeberInfo(@QueryParam("strMemberCode") String memberCode)
			{
				String response = "";
				String memberData=funGetCMSMemberInfoFromDB(memberCode);
				if(memberData.equalsIgnoreCase("no data"))
				{
//					response = clsUtitlity.funConstructJSONForErrorMsg("CMSMemberData", false, "Data Not Found").toString();
				}
				else
				{
					response = memberData;
				}
				return response;
			}
			
			private String funGetCMSMemberInfoFromDB(String memberCode)
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        String memberData="";
		        Connection cmsCon=null;
		        JSONObject jObj=new JSONObject();
		        
		        try
		        {
		        	cmsCon=objDb.funOpenCMSCon("mssql","master");
		            Statement st = cmsCon.createStatement();
		            String sql="select strDebtorFullName,strTel1,strTel2,strEmail,strAddrLine1,strAddrLine2"
		            	+ ",strAddrLine3,strCity,strState,strArea "
		                + "from tblDebtorMaster "
		                + "where strDebtorCode='"+memberCode+"'";
		            //System.out.println(sql);
		            JSONArray arrObj=new JSONArray();
		            
		            ResultSet rsMemeberData=st.executeQuery(sql);
		            if(rsMemeberData.next())
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("MemberFullName",rsMemeberData.getString(1));
		            	obj.put("TelNo1",rsMemeberData.getString(2));
		            	obj.put("TelNo2",rsMemeberData.getString(3).trim());
		            	obj.put("Email",rsMemeberData.getString(4));
		            	obj.put("Address1",rsMemeberData.getString(5));
		            	obj.put("Address2",rsMemeberData.getString(6));
		            	obj.put("Address3",rsMemeberData.getString(7));
		            	obj.put("City",rsMemeberData.getString(8));
		            	obj.put("State",rsMemeberData.getString(9));
		            	obj.put("Area",rsMemeberData.getString(10));
		            	arrObj.put(obj);
		            }
		            else
		            {
		            	memberData="no data";
		            	arrObj.put(memberData);
		            }
		            rsMemeberData.close();
		            
		            jObj.put("CMSMemberInfo", arrObj);
		            st.close();
		            cmsCon.close();
		            
		        }catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        return jObj.toString();
			}
		
			
			
			
			public String funGetMemberDtlsFromCMS(String memberCode)
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        String memberData="";
		        Connection cmsCon=null;
		        JSONObject jObj=new JSONObject();
		        
		        try
		        {
		        	cmsCon=objDb.funOpenCMSCon("mssql","master");
		            Statement st = cmsCon.createStatement();
		            String sql= " select b.strMemberCode,b.strFullName from tblMemberMaster b "
		            		  + " where b.strMemberCode='"+memberCode+"'";
		            System.out.println(sql);
		            JSONArray arrObj=new JSONArray();
		            
		            ResultSet rsMemberCardDtl=st.executeQuery(sql);
		            if(rsMemberCardDtl.next())
		            {
		            	JSONObject obj=new JSONObject();
		            
		            	obj.put("MemberCode",rsMemberCardDtl.getString(1));
		            	obj.put("MemberName",rsMemberCardDtl.getString(2));
		            
		            	arrObj.put(obj);
		            }
		            else
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("MemberCode","No Data");
		            	arrObj.put(obj);
		            }
		            rsMemberCardDtl.close();
		            
		            jObj.put("CMSMemberDtl", arrObj);
		            st.close();
		            cmsCon.close();
		            
		        }catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        return jObj.toString();
			}
			
			
			
			
			
			@GET 
			@Path("/funValidateMemberCard")
			@Produces(MediaType.APPLICATION_JSON)
			public String funValidateMemberCard(@QueryParam("strCardNo") String cardNo)
			{
				String response = "";
				String memberData=funGetMemberCardDtlsFromCMS(cardNo);
				if(memberData.equalsIgnoreCase("no data"))
				{
				}
				else
				{
					response = memberData;
				}
				return response;
			}
			
			public String funGetMemberCardDtlsFromCMS(String cardNo)
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        String memberData="";
		        Connection cmsCon=null;
		        JSONObject jObj=new JSONObject();
		        
		        try
		        {
		        	cmsCon=objDb.funOpenCMSCon("mssql","master");
		            Statement st = cmsCon.createStatement();
		            String sql="select a.strCustomerCode,a.strCardNo,a.strCardString"
		            	+ ",a.strCardStatus,a.strBlocked,b.strMemberCode"
		            	+ ",b.strFullName "
		            	+ "from tblCardDetail a,tblMemberMaster b "
		            	+ "where a.strCustomerCode=b.strCustomerCode "
		            	+ "and a.strCardString='"+cardNo+"'";
		            System.out.println(sql);
		            JSONArray arrObj=new JSONArray();
		            
		            ResultSet rsMemberCardDtl=st.executeQuery(sql);
		            if(rsMemberCardDtl.next())
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("CustomerCode",rsMemberCardDtl.getString(1));
		            	obj.put("CardNo",rsMemberCardDtl.getString(2));
		            	obj.put("CardString",rsMemberCardDtl.getString(3).trim());
		            	obj.put("CardStatus",rsMemberCardDtl.getString(4));
		            	obj.put("Blocked",rsMemberCardDtl.getString(5));
		            	obj.put("MemberCode",rsMemberCardDtl.getString(6));
		            	obj.put("MemberName",rsMemberCardDtl.getString(7));
		            	System.out.println(rsMemberCardDtl.getString(7));
		            	arrObj.put(obj);
		            }
		            else
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("CustomerCode","No Data");
		            	arrObj.put(obj);
		            }
		            rsMemberCardDtl.close();
		            
		            jObj.put("CMSMemberCardDtl", arrObj);
		            st.close();
		            cmsCon.close();
		            
		        }catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        return jObj.toString();
			}
			
			
			
			@GET 
			@Path("/funValidateMemberCardNo")
			@Produces(MediaType.APPLICATION_JSON)
			public String funValidateMemberCardNo(@QueryParam("strMemberCode") String memberCode)
			{
				String response = "";
				String memberData=funGetMemberCardNoDtlsFromCMS(memberCode);
				if(memberData.equalsIgnoreCase("no data"))
				{
				}
				else
				{
					response = memberData;
				}
				return response;
			}
			
			public String funGetMemberCardNoDtlsFromCMS(String memberCode)
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        String memberData="";
		        Connection cmsCon=null;
		        JSONObject jObj=new JSONObject();
		        
		        try
		        {
		        	cmsCon=objDb.funOpenCMSCon("mssql","master");
		            Statement st = cmsCon.createStatement();
		            String sql="select a.strCustomerCode,a.strCardNo,a.strCardString"
		            	+ ",a.strCardStatus,a.strBlocked,b.strMemberCode"
		            	+ ",b.strFullName "
		            	+ "from tblCardDetail a,tblMemberMaster b "
		            	+ "where a.strCustomerCode=b.strCustomerCode "
		            	+ "and b.strMemberCode='"+memberCode+"'";
		            System.out.println(sql);
		            JSONArray arrObj=new JSONArray();
		            
		            ResultSet rsMemberCardDtl=st.executeQuery(sql);
		            if(rsMemberCardDtl.next())
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("CustomerCode",rsMemberCardDtl.getString(1));
		            	obj.put("CardNo",rsMemberCardDtl.getString(2));
		            	obj.put("CardString",rsMemberCardDtl.getString(3).trim());
		            	obj.put("CardStatus",rsMemberCardDtl.getString(4));
		            	obj.put("Blocked",rsMemberCardDtl.getString(5));
		            	obj.put("MemberCode",rsMemberCardDtl.getString(6));
		            	obj.put("MemberName",rsMemberCardDtl.getString(7));
		            	System.out.println(rsMemberCardDtl.getString(7));
		            	arrObj.put(obj);
		            }
		            else
		            {
		            	JSONObject obj=new JSONObject();
		            	obj.put("CustomerCode","No Data");
		            	arrObj.put(obj);
		            }
		            rsMemberCardDtl.close();
		            
		            jObj.put("CMSMemberCardDtl", arrObj);
		            st.close();
		            cmsCon.close();
		            
		        }catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        return jObj.toString();
			}
			
			
			
			
			
			
			public String funGetMemberCodeFromCMS(String cardNo)
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        String memberCode="";
		        Connection cmsCon=null;
		        
		        try
		        {
		        	cmsCon=objDb.funOpenCMSCon("mssql","master");
		            Statement st = cmsCon.createStatement();
		            String sql="select b.strMemberCode "
		            	+ "from tblCardDetail a,tblMemberMaster b "
		            	+ "where a.strCustomerCode=b.strCustomerCode "
		            	+ "and a.strCardString='"+cardNo+"'";
		            System.out.println(sql);
		            ResultSet rsMemberCardDtl=st.executeQuery(sql);
		            if(rsMemberCardDtl.next())
		            {
		            	memberCode=rsMemberCardDtl.getString(1);
		            }
		            rsMemberCardDtl.close();
		            st.close();
		            cmsCon.close();
		            
		        }catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        return memberCode;
			}
			
			
			
			public List funGetMemberInfoFromCMS(String memberCode)
			{
				List listMemberInfo=new ArrayList<String>();
				
				String sql="select b.strMemberCode,b.strFullName,a.strCardNo "
					+ "from tblCardDetail a,tblMemberMaster b  "
					+ "where a.strCustomerCode=b.strCustomerCode and b.strMemberCode='"+memberCode+"'";
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection cmsCon=null;
		        
		        try
		        {
		        	cmsCon=objDb.funOpenCMSCon("mssql","master");
		            Statement st = cmsCon.createStatement();
				
		            ResultSet rsMemberInfo=st.executeQuery(sql);
		            if(rsMemberInfo.next())
		            {
		            	listMemberInfo.add(rsMemberInfo.getString(1));  // Member Code
		            	listMemberInfo.add(rsMemberInfo.getString(2));  // Member Full Name
		            	listMemberInfo.add(rsMemberInfo.getString(3));  // Member Card No
		            }
		            rsMemberInfo.close();
					
		        }catch(Exception e)
		        {
		        	e.printStackTrace();
		        }
				return listMemberInfo;
			}
			
			
			
		//tblrv_170315 and tblcl_170315
			@POST
			@Path("/funPostRVDataToCMS")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response funPostBillInfo(JSONObject objBillData)
			{
				String response = "false";
				if(funInsertRVData(objBillData)>0)
				{
					response = "true";
				}
				return Response.status(201).entity(response).build();
			}
			
			@SuppressWarnings("finally")
			private int funInsertRVData(JSONObject objBillData)
			{
				int res=0;
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection cmsCon=null;
		        Statement st = null;
				try
				{
					cmsCon=objDb.funOpenCMSCon("mssql","transaction");
					st = cmsCon.createStatement();
		            //tblrv_170315
					
					JSONArray mJsonArray=(JSONArray)objBillData.get("BillInfo");
					JSONObject mJsonObject = new JSONObject();
										
					String rvNameToDel="";
					String rvCodeToDel="";
					String billDateToDel="";
					String cmsPOSCodeToDel="";
					String POSCodeToDel="";
					
					for (int i = 0; i < mJsonArray.length(); i++) 
					{
					    mJsonObject =(JSONObject) mJsonArray.get(i);
					    rvNameToDel=mJsonObject.get("RVName").toString();
					    rvCodeToDel=mJsonObject.get("RVCode").toString();
					    billDateToDel=mJsonObject.get("BillDate").toString();
					    cmsPOSCodeToDel=mJsonObject.get("CMSPOSCode").toString();
					    POSCodeToDel=mJsonObject.get("POSCode").toString();
					}
					String sqlDelete="delete from tblrv "
						+ "where dtRVDate='"+billDateToDel+"' and strPosCode='"+POSCodeToDel+"' ";
					System.out.println(sqlDelete);
					res=st.executeUpdate(sqlDelete);
					
					String sql_insertRV="insert into tblrv (strRVCode,strRVName,strNarration,dblCrAmt,dblDrAmt,"
						+ "strMasterPOSCd,strPosCode,dtRVDate,strVouchNo,strInterfaceAcctLink,strInterfaceAcctLinkName,"
						+ "strClientId,strPropertyId,DRCR,PayMode,IsAdvance) values ";
					
					String sql="";
					boolean flgData=false;
					mJsonObject = new JSONObject();
					for (int i = 0; i < mJsonArray.length(); i++) 
					{
					    mJsonObject =(JSONObject) mJsonArray.get(i);
					    String rvName=mJsonObject.get("RVName").toString();
					    String rvCode=mJsonObject.get("RVCode").toString();
					    double drAmt=Double.parseDouble(mJsonObject.get("DRAmt").toString());
					    double crAmt=Double.parseDouble(mJsonObject.get("CRAmt").toString());
					    String clientCode=mJsonObject.get("ClientCode").toString();
					    String billDate=mJsonObject.get("BillDate").toString();
					    String cmsPOSCode=mJsonObject.get("CMSPOSCode").toString();
					    String POSCode=mJsonObject.get("POSCode").toString();
					    sql+=",('"+rvCode+"','"+rvName+"','"+rvName+"','"+crAmt+"','"+drAmt+"','"+cmsPOSCode+"','"+POSCode+"'"
					    	+ ",'"+billDate+"',NULL,'','','','','','','')";
					    flgData=true;
					}
					
					if(flgData)
					{
						sql=sql.substring(1,sql.length());
						sql_insertRV+=" "+sql;
						res=st.executeUpdate(sql_insertRV);
					}
					else
					{
						res=1;
					}
					
				}catch(Exception e)
				{
					res=0;
					e.printStackTrace();
				}
				finally
		        {
		        	try {
						st.close();
						cmsCon.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            return res;
		        }
			}
			
			@POST
			@Path("/funPostCLDataToCMS")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response funPostCL(JSONObject objCLData)
			{
				String response = "false";
				if(funInsertCLData(objCLData)>0)
				{
					response = "true";
				}
				return Response.status(201).entity(response).build();
			}
			
			
			@SuppressWarnings("finally")
			private int funInsertCLData(JSONObject objCLData)
			{
				int res=0;
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection cmsCon=null;
		        Statement st = null;
				try
				{
					cmsCon=objDb.funOpenCMSCon("mssql","transaction");
		            st = cmsCon.createStatement();
		            //tblcl_170315		            
					
					String debtorCodeToDel="";
					String billNoToDel="";
					String billDateToDel="";
					String cmsPOSCodeToDel="";
					String POSCodeToDel="";
					
					JSONArray mJsonArray=(JSONArray)objCLData.get("MemberCLInfo");
					JSONObject mJsonObject = new JSONObject();
					for (int i = 0; i < mJsonArray.length(); i++) 
					{
					    mJsonObject =(JSONObject) mJsonArray.get(i);
					    debtorCodeToDel=mJsonObject.get("DebtorCode").toString();
					    billNoToDel=mJsonObject.get("BillNo").toString();
					    billDateToDel=mJsonObject.get("BillDate").toString();
					    cmsPOSCodeToDel=mJsonObject.get("CMSPOSCode").toString();
					    POSCodeToDel=mJsonObject.get("POSCode").toString();
					}
					String sqlDelete="delete from tblcl "
						+ "where dtBillDate='"+billDateToDel+"' and strPosCode='"+POSCodeToDel+"' ";
					System.out.println(sqlDelete);
					res=st.executeUpdate(sqlDelete);
		            
					String sql_insertCL="insert into tblcl (strPosCode,strDebtorCode,strDebtorName,strBillNo,dtBillDate"
						+ ",dblBillAmt,strGuestName,strPosNarration,strMasterPOSCd,dtCLDate,strVouchNo,"
						+ "strClientId,strPropertyId,PayMode) values ";
					
					String sql="";
					boolean flgData=false;
					mJsonObject = new JSONObject();
					for (int i = 0; i < mJsonArray.length(); i++) 
					{
					    mJsonObject =(JSONObject) mJsonArray.get(i);
					    String debtorCode=mJsonObject.get("DebtorCode").toString();
					    String debtorName=mJsonObject.get("DebtorName").toString();
					    String billNo=mJsonObject.get("BillNo").toString();
					    String billDate=mJsonObject.get("BillDate").toString();
					    double billAmt=Double.parseDouble(mJsonObject.get("BillAmt").toString());
					    String clientCode=mJsonObject.get("ClientCode").toString();
					    String cmsPOSCode=mJsonObject.get("CMSPOSCode").toString();
					    String POSCode=mJsonObject.get("POSCode").toString();
					    sql+=",('"+POSCode+"','"+debtorCode+"','"+debtorName+"','"+billNo+"','"+billDate+"'"
					    	+ ",'"+billAmt+"','','','"+cmsPOSCode+"','"+billDate+"',NULL,'','','')";
					    flgData=true;
					}
					
					if(flgData)
					{
						sql=sql.substring(1,sql.length());
						sql_insertCL+=" "+sql;
						System.out.println(sql_insertCL);
						res=st.executeUpdate(sql_insertCL);
					}
					else
					{
						res=1;
					}
					
				}catch(Exception e)
				{
					res=0;
					e.printStackTrace();
				}
				finally
		        {
		        	try {
						st.close();
						cmsCon.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            return res;
		        }
			}
			
			
			@GET
			@Path("/funGetAccountMaster")
			@Consumes(MediaType.APPLICATION_JSON)
			public String funGetAccountData(@QueryParam("clientCode") String clientCode,@QueryParam("propertyCode") String propertyCode,@QueryParam("Type") String type)
			{
				  return funGetAccountDetails(clientCode,propertyCode,type);
			}
			
			@SuppressWarnings("finally")
			private String funGetAccountDetails(String clientCode,String propertyCode,String type)
			{
				JSONObject jObjAccount=new JSONObject();
				String res="";
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection cmsCon=null;
		        Statement st = null;
				try
				{
					cmsCon=objDb.funOpenCMSCon("mssql","master");
					st = cmsCon.createStatement();
					String sql= " select * from tblAccountsMaster "
						+ " where strClientID='"+clientCode+"' "
						+ " AND strPropertyID='"+propertyCode+"' " 
						+ " AND strOperational='Yes' ";
					if(!type.equals("All"))
					{
						sql+="AND strType='"+type+"' ";
					}
					
					JSONArray arrObjAccount=new JSONArray();
			        ResultSet rsAcc=st.executeQuery(sql);
			        while(rsAcc.next())
			        {
			        	JSONObject objAccount=new JSONObject();
			        	objAccount.put("AccountCode", rsAcc.getString(1));
			        	objAccount.put("AccountName", rsAcc.getString(2));
			        	objAccount.put("Type", rsAcc.getString(4));
			        	arrObjAccount.put(objAccount);
			        }
			        rsAcc.close();
			        st.close();
		            cmsCon.close();
		            
			        jObjAccount.put("AccountDtls", arrObjAccount);
		    
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
		        {
		            return jObjAccount.toString();
		        }
			}
	
}
