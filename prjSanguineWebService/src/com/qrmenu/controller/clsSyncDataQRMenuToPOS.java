package com.qrmenu.controller;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Base64;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Controller
@Path("/QrMenuIntegration")
public class clsSyncDataQRMenuToPOS {

	
	@Autowired
	clsUtilityFunctions objUtilityFunctions;
	
	@GET
	@Path("/funInvokeQRMenuWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public String funInvokeQRMenuWebService()
	{
		String response = "true";
		return response;
	}
	
	/**
	 * 
	 * @param strClientCode
	 * @return
	 */
	@GET
	@Path("/funGetBilldtlWithModDtlData")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject funGetBillHdDtlTaxData(@QueryParam("clientCode") String strClientCode)
	{
		
 		JSONObject objJsonMain= new JSONObject();
		JSONArray objJsonBillDtlArr= new JSONArray();   
		JSONArray objJsonBillModArr= new JSONArray(); 
		JSONArray objJsonBillHdArr=  new JSONArray();   
		JSONArray objJsonBillTaxArr= new JSONArray(); 
		JSONArray objJsonCustomerArr= new JSONArray();
/*
		JSONObject objJsonBillDtl= new JSONObject();   
		JSONObject objJsonBillMod= new JSONObject(); 
		JSONObject objJsonBillHd= new JSONObject();   
		JSONObject objJsonBillTax= new JSONObject();   

*/		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posCon=null;
        Statement st=null;
        ResultSet res=null;
        JSONObject jObj=new JSONObject();
        ResultSet resBill=null;
		
        try {
        	posCon=objDb.funOpenPOSCon("mysql","transaction");
            st = posCon.createStatement();
            String sql="select * from tblbillhd a where a.strClientCode='"+strClientCode+"';";
            resBill=st.executeQuery(sql);
			ResultSetMetaData resultSetMetaData = resBill.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			String strUpdatedBillToPOS="";

			while (resBill.next())
			{
				JSONObject obj = new JSONObject();
			//	objJsonBillHdArr= new JSONArray();
				boolean flagBillUpdatedToPOS=false;
				boolean flagBillUpdatedToQrMenu=false;
				
				String sqlQr="select a.strQRMenuBillNo,a.strIsBillUpadatedToPOS from tblupdatekotandbillfromqrmenu a where a.strQRMenuBillNo='"+resBill.getString(1)+"';";
				ResultSet resQr=posCon.createStatement().executeQuery(sqlQr);
				if(resQr.next())
				{
					flagBillUpdatedToQrMenu=true;
					if(resQr.getString(2).equals("Y"))
					{
						if(strUpdatedBillToPOS.length()>0)
						{
							strUpdatedBillToPOS +=",'"+resQr.getString(1)+"'";
						}
						else
						{
							strUpdatedBillToPOS =" '"+resQr.getString(1)+"'";
						}
						flagBillUpdatedToPOS=true;
					}
				}
				if(!flagBillUpdatedToPOS)
				{
					if(!flagBillUpdatedToQrMenu)
					{
						String sqlQrMenu="insert into tblupdatekotandbillfromqrmenu values ('','"+resBill.getString(1)+"','"+strClientCode+"','"+objUtilityFunctions.funGetCurrentDateTime("yyyy-MM-dd")+"','N');";
						posCon.createStatement().executeUpdate(sqlQrMenu);
					}
					for (int i = 1; i <= columnCount; i++)
					{
						if(i==20)
						{
							JSONObject objCust= new JSONObject();
							if(resBill.getString(20)!=null && resBill.getString(20).length()>0)
							{
								String sqlCust="select * from tblcustomermaster a where a.strCustomerCode='"+resBill.getString(20)+"' and a.strClientCode='"+strClientCode+"';";
								ResultSet resQrCust=posCon.createStatement().executeQuery(sqlCust);
								ResultSetMetaData resultSetCustMetaData = resQrCust.getMetaData();
								int custColumnCount = resultSetCustMetaData.getColumnCount();
								if(resQrCust.next())
								{
									for (int k = 1; k <= custColumnCount; k++)
									{
										objCust.put("Column" + k, resQrCust.getString(k));
									}
									objJsonCustomerArr.add(objCust);
								}
								
							}
						}
						obj.put("Column" + i, resBill.getString(i));					
					}
					objJsonBillHdArr.add(obj);

				}
				
				//objJsonBillHd.put(resBill.getString(1), objJsonBillHdArr);
			}
	        objJsonMain.put("billhd", objJsonBillHdArr);
	        objJsonMain.put("custDtl", objJsonCustomerArr);

	        sql="select * from tblbilldtl a where a.strClientCode='"+strClientCode+"'  " ;
	        if(strUpdatedBillToPOS.length() >0)
	        {
	        	sql +=" and strBillNo not in ("+strUpdatedBillToPOS+"); ";
	        }
	        		
	        resBill=st.executeQuery(sql);
			resultSetMetaData = resBill.getMetaData();
			columnCount = resultSetMetaData.getColumnCount();
			while (resBill.next())
			{
				JSONObject obj = new JSONObject();
				//objJsonBillDtlArr= new JSONArray();
				for (int i = 1; i <= columnCount; i++)
				{
						obj.put("Column" + i, resBill.getString(i));					
				}
				objJsonBillDtlArr.add(obj);
				//objJsonBillDtl.put(resBill.getString(3)+" "+resBill.getString(1), objJsonBillDtlArr);
			}
	        objJsonMain.put("billDtl", objJsonBillDtlArr);
	        
	        sql="select * from tblbillmodifierdtl a where a.strClientCode='"+strClientCode+"' ";
	        if(strUpdatedBillToPOS.length() >0)
	        {
	        	sql +=" and strBillNo not in ("+strUpdatedBillToPOS+"); ";
	        }
	        resBill=st.executeQuery(sql);
			resultSetMetaData = resBill.getMetaData();
		    columnCount = resultSetMetaData.getColumnCount();
			while (resBill.next())
			{
				JSONObject obj = new JSONObject();
			//	objJsonBillModArr=new JSONArray();
				for (int i = 1; i <= columnCount; i++)
				{
											
					
						obj.put("Column" + i, resBill.getString(i));
					
				}
				objJsonBillModArr.add(obj);
				//objJsonBillMod.put(resBill.getString(1)+" "+resBill.getString(2),objJsonBillModArr);
			}
	        objJsonMain.put("billModDtl", objJsonBillModArr);
	       
	        sql="select * from tblbilltaxdtl a where a.strClientCode='"+strClientCode+"' ";
	        if(strUpdatedBillToPOS.length() >0)
	        {
	        	sql +=" and strBillNo not in ("+strUpdatedBillToPOS+"); ";
	        }
	        resBill=st.executeQuery(sql);
		    resultSetMetaData = resBill.getMetaData();
		    columnCount = resultSetMetaData.getColumnCount();
			while (resBill.next())
			{
				JSONObject obj = new JSONObject();
				//objJsonBillTaxArr=new JSONArray();
				for (int i = 1; i <= columnCount; i++)
				{
						obj.put("Column" + i, resBill.getString(i));					
				}
				objJsonBillTaxArr.add(obj);
			//	objJsonBillTax.put(resBill.getString(1)+" "+resBill.getString(2), objJsonBillTaxArr);
			}
			resBill.close();
	        objJsonMain.put("billTaxDtl", objJsonBillTaxArr);
       
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        finally{
        	try
        	{
        		posCon.close();
            	st.close();
            	resBill.close();
            
        	}catch(Exception ex)
            {
            	ex.printStackTrace();
            }
        	
        	
        }
		return objJsonMain;
	}
		@GET
		@Path("/funGetKOTdtlData")
		@Produces(MediaType.APPLICATION_JSON)
		public JSONObject funGetKOTDtlData(@QueryParam("clientCode") String strClientCode)
		{
	 		JSONObject objJsonMain= new JSONObject();
			JSONArray objJsonKOTDtlArr= new JSONArray();   
			//JSONObject objJsonKOTDtl= new JSONObject();   

			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection posCon=null;
	        Statement st=null;
	        ResultSet resKOT=null;
			
	        try {
	        	posCon=objDb.funOpenPOSCon("mysql","transaction");
	            st = posCon.createStatement();
	            String sql="select * from tblitemrtemp a where  a.strClientCode='"+strClientCode+"'";
	            resKOT=st.executeQuery(sql);
				ResultSetMetaData resultSetMetaData = resKOT.getMetaData();
				int columnCount = resultSetMetaData.getColumnCount();
				while (resKOT.next())
				{
					JSONObject obj = new JSONObject();
				//	objJsonKOTDtlArr=new JSONArray();
					boolean flagBillUpdatedToPOS=false;
					boolean flagBillUpdatedToQrMenu=false;
					String sqlQr="select a.strQRMenuBillNo,a.strIsBillUpadatedToPOS from tblupdatekotandbillfromqrmenu a where a.strQRMenuBillNo='"+resKOT.getString(13)+"';";
					ResultSet resQr=posCon.createStatement().executeQuery(sqlQr);
					if(resQr.next())
					{
						flagBillUpdatedToQrMenu=true;
						
					}
					if(!flagBillUpdatedToQrMenu)
					{
						
						String sqlQrMenu="insert into tblupdatekotandbillfromqrmenu values ('','"+resKOT.getString(13)+"','"+strClientCode+"','"+objUtilityFunctions.funGetCurrentDateTime("yyyy-MM-dd")+"','N');";
						posCon.createStatement().executeUpdate(sqlQrMenu);
						

						for (int i = 1; i <= columnCount; i++)
						{						
							obj.put("Column" + i, resKOT.getString(i));							
						}
						objJsonKOTDtlArr.add(obj);

					}
					//objJsonKOTDtl.put(resKOT.getString(13)+" "+resKOT.getString(8), objJsonKOTDtlArr);
				}
				resKOT.close();
		        objJsonMain.put("KOTDtl", objJsonKOTDtlArr);

	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }
	        finally{
	        	try
	        	{
	        		posCon.close();
	            	st.close();
	            	resKOT.close();
	            
	        	}catch(Exception ex)
	            {
	            	ex.printStackTrace();
	            }
	        	

	        }
			return objJsonMain;
		}
		//funPostBillNoAndKOTNoToQRMenu
		
		@SuppressWarnings("rawtypes")
		@POST
		@Path("/funPostBillNoAndKOTNoToQRMenu")
		@Produces(MediaType.APPLICATION_JSON)
		public Response funPostBillNoAndKOTNoToQRMenu(JSONObject jsonObj)
		{
			String response = "Bill Updated to QR Menu DB";
			clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection posCon=null;
	        Statement st=null;
	      
	      try
			{
		        String strClientCode=jsonObj.get("strClientCode").toString();	       

	    	  	posCon=objDb.funOpenPOSCon("mysql","transaction");
	            st = posCon.createStatement();
			
				Iterator<String> keys =jsonObj.keys();
				while(keys.hasNext())
				{
					String key=keys.next();
					System.out.println(jsonObj.get(key));
					if(!key.equals("strClientCode"))
					{
						String sqlUpdate=" update tblupdatekotandbillfromqrmenu a set a.strPOSBillNo='"+jsonObj.get(key)+"' ,a.strIsBillUpadatedToPOS='Y'"
								+ " where a.strQRMenuBillNo='"+key+"' and a.strClientCode='"+strClientCode+"'";
						st.executeUpdate(sqlUpdate);
					}
					
				}
		
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
	        finally
	        {
	        	try
	        	{
	        		posCon.close();;
	 	            st.close();	 	      
	        	}
	        	catch(Exception ex)
	        	{
	        		ex.printStackTrace();
	        	}
	        	  
	        }
			return Response.status(201).entity(response).build();

		}
		
}
/*@GET
@Path("/funGetBilldtlWithModDtlData")
@Produces(MediaType.APPLICATION_JSON)
public JSONObject funGetBillHdDtlTaxData(@QueryParam("clientCode") String strClientCode)
{
	select * from tblbillhd a where a.strClientCode='240.001';
	select * from tblbilldtl a where a.strClientCode='240.001';
	select * from tblbilltaxdtl a where a.strClientCode='240.001';
	
		JSONObject objJsonMain= new JSONObject();
	JSONArray objJsonBillDtlArr= new JSONArray();   
	JSONArray objJsonBillModArr= new JSONArray();   
	clsDatabaseConnection objDb=new clsDatabaseConnection();
    Connection posCon=null;
    Statement st=null;
    ResultSet res=null;
    JSONObject jObj=new JSONObject();
	
    try {
    	posCon=objDb.funOpenPOSCon("mysql","transaction");
        st = posCon.createStatement();
        String sql="select a.strItemCode,a.strItemName,(a.dblQuantity),a.dblRate,a.dblAmount,d.strShortName,a.strSequenceNo "
        		+ " from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
        		+ " where  a.strBillNo='P0100117' and  b.strCostCenterCode=c.strCostCenterCode "
        		+ " and a.strItemCode=d.strItemCode	 and a.strItemCode=b.strItemCode and a.strClientCode='"+strClientCode+"' "
        		+ " And b.strHourlyPricing='No'   "
        		+ " group by a.strItemCode;";
         res=st.executeQuery(sql);
        while(res.next())
        {
        	jObj=new JSONObject();
        	
        	String itemCode=res.getString(1);	
        	jObj.put("itemCode", res.getString(1));
        	jObj.put("itemName", res.getString(2));
        	jObj.put("qty", res.getDouble(3));
        	jObj.put("itemRate", res.getDouble(4));
        	jObj.put("amount", res.getDouble(5));
        	jObj.put("isModifier", false);
        	jObj.put("modifierCode", "");
        	jObj.put("strSeq", "0");
        	objJsonBillDtlArr.add(jObj);
            Statement stMod = posCon.createStatement();

    		String sqlmod="select a.strItemCode,a.strModifierName,a.dblQuantity,a.dblRate,a.dblAmount,ifnull(b.strDefaultModifier,'N'),a.strDefaultModifierDeselectedYN "
        			+ " from tblbillmodifierdtl a "
        			+ " left outer join tblitemmodofier b on left(a.strItemCode,7)=if(b.strItemCode='',a.strItemCode,b.strItemCode) "
        			+ " and a.strModifierCode=if(a.strModifierCode=null,'',b.strModifierCode) "
        			+ " where a.strBillNo='P0100117' and left(a.strItemCode,7)='"+itemCode+"' "
        			+ " and a.strClientCode='"+strClientCode+"'";
            ResultSet resMod=stMod.executeQuery(sqlmod);
            while(resMod.next())
            {
            	jObj=new JSONObject();
            	jObj.put("itemCode",itemCode);
            	jObj.put("itemName", resMod.getString(2));
            	jObj.put("qty", resMod.getDouble(3));
            	jObj.put("itemRate", resMod.getDouble(4));
            	jObj.put("amount", resMod.getDouble(5));
            	jObj.put("isModifier", true);
            	jObj.put("modifierCode", resMod.getString(1));
            	jObj.put("strSeq", "0");
            	objJsonBillDtlArr.add(jObj);

            }
            stMod.close();
            resMod.close();
        }
        objJsonMain.put("billdtl", objJsonBillDtlArr);
    }
    catch(Exception ex)
    {
    	ex.printStackTrace();
    }
    finally{
    	try
    	{
    		posCon.close();
        	st.close();
        	res.close();
        
    	}catch(Exception ex)
        {
        	ex.printStackTrace();
        }
    	
    	
    }
	return objJsonMain;
}
*/