package com.mms.controller;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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










import com.webservice.controller.clsConfigFile;
import com.webservice.controller.clsDatabaseConnection;
import com.webservice.util.clsUtilityFunctions;

@Path("/MMSIntegrationAuto")
public class clsSynchPOSDataWithMMSAuto {

	String unicode= "?useUnicode=yes&characterEncoding=UTF-8";
	
	@SuppressWarnings("finally")
	@GET 
	@Path("/funInvokeMMSWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetBillInfo()
	{
		String response = "false";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
		try
		{
			cmsCon=objDb.funOpenMMSCon("mysql","transaction");
			response="true";
		}
		catch(Exception e)
		{
			response="false";
			e.printStackTrace();
		}
		
		return Response.status(201).entity(response).build();
	}
	
	@POST
	@Path("/funPostPOSSaleDataAuto")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostPOSSalesData(JSONObject objCLData)
	{
		String SACode = funInsertPOSSalesData(objCLData);		
		return Response.status(201).entity(SACode).build();
	}
	
	//Save or Update POSSalesDtl
	
	@SuppressWarnings("finally")
	private String funInsertPOSSalesData(JSONObject objCLData)
	{
		String SACodes="";
		int res=0;
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        
		try
		{
			String posItemCode="",posItemName="",posCode="",billDate="",clientCode="",wsItemCode="",locCode="",posName="";
			String retValue="";
			String strStkCode = "";
			long lastNo=0;
			double totAmt=0.00,rate=0.00,itemTotAmt=0.00,itemPer=0.00,itemPerAmt=0.00;
			Integer quantity =new Integer(0);
			cmsCon=objDb.funOpenMMSCon("mysql","transaction");
            Statement st = cmsCon.createStatement();
            
            JSONArray rootMMSArray=(JSONArray)objCLData.get("RootMMS");
            
            String oldSACodes=objCLData.get("DayEndWSStockAdjNo").toString();
			if(oldSACodes.equalsIgnoreCase("NA"))
			{
				oldSACodes="";
			}
			String stkAdjCodeLoatNo="";
			
			if(!oldSACodes.equals(""))
			{
				int l=0;
				////////////////////////// for Old POST Data(like POS wise Linkup) RePOST again costcenterwise   ////////////////////////////////////				
				String[] saCod2 = oldSACodes.split(",");
				String[] saCodes = new String[saCod2.length]; // for Number of Costcenter == number of SA Codes
				
				
				for(int sa=0; sa<saCod2.length;sa++)
				{
					saCodes[sa] =saCod2[sa];
				}
				
			//	String[] saCodes = oldSACodes.split(",");
				for(String oldSACode :saCodes)
				{
					
					JSONObject jsonlocWiseData=(JSONObject) rootMMSArray.get(l);
					JSONArray mJsonArray=(JSONArray)jsonlocWiseData.get("MemberPOSSalesInfo");
        			String sql="";
        			boolean flgData=false;
        			JSONObject mJsonObject = new JSONObject();
        			for (int i = 0; i < mJsonArray.length(); i++) 
        			{
        				mJsonObject =(JSONObject) mJsonArray.get(i);
        				clientCode=mJsonObject.get("clientCode").toString();
        				rate=Double.parseDouble(mJsonObject.get("rate").toString());
        				 billDate=mJsonObject.get("billDate").toString();
        				 posCode=mJsonObject.get("posCode").toString();
        				 posName=mJsonObject.get("posName").toString();
        				 itemTotAmt=Double.parseDouble(mJsonObject.get("Amount").toString());
        				 itemPerAmt=Double.parseDouble(mJsonObject.get("DiscAmt").toString());
        				totAmt=totAmt+(itemTotAmt-itemPerAmt);
        				
        			}
        			String deletePoSSales = " delete from tblpossalesdtl where strSACode='"+oldSACode+"' and strClientCode='"+clientCode+"'  ";
					int delPoSSales=st.executeUpdate(deletePoSSales);
					
	            	String sql_insertPOSSales="insert into tblpossalesdtl "
	        				+ "(strPOSItemCode,strPOSItemName,dblQuantity,dblRate,strPOSCode,dteBillDate"
	        				+ ",strClientCode,strWSItemCode,strSACode,strCostCenterCode,strCostCenterName,strLocationCode,dblAmount,dblPercent,dblPercentAmt) "
	        				+ "values ";
	        			
					String SACode = "";
					if(oldSACode==null)
					{
						stkAdjCodeLoatNo=funStockAdjumentCode(clientCode, billDate,locCode);
	    				String code[] =stkAdjCodeLoatNo.split(",");
	    				strStkCode=code[0];
	    				oldSACode=code[0];
	    				lastNo=Integer.parseInt(code[1]);
					}else
					{
						SACode = oldSACode;
					}
					//////////////////////////////////////////////
					
					
					String deleteDtlSql="delete from tblstockadjustmentdtl  "
							+ " where strSACode= '"+oldSACode+"' and strClientCode='"+clientCode+"' ";
					int dtl=st.executeUpdate(deleteDtlSql);
					
					
					String deleteHdSql="delete from tblstockadjustmenthd  "
							+ " where strSACode= '"+oldSACode+"' and strClientCode='"+clientCode+"' ";
					int hd=st.executeUpdate(deleteHdSql);
					System.out.println(oldSACode+" StcAdjHd Delete Rows "+ hd+" StcAdjDtl Delete Rows "+dtl);
					
					stkAdjCodeLoatNo=oldSACode;
					String ln=(String) stkAdjCodeLoatNo.subSequence(6, 12);
					SACode=stkAdjCodeLoatNo;
					lastNo=Integer.parseInt(ln);
					
//					 for(int l=0;l<rootMMSArray.length(); l++) 
//			            {
			            	
								locCode=jsonlocWiseData.getString("WSLocation");
			        			String costCenterCode=jsonlocWiseData.getString("costCenterCode");
			        			String costCenterName=jsonlocWiseData.getString("costCenterName"); 
			        			System.out.println("billDate:"+billDate);
			        			
			        		    // for HD Data Save in webmms
			        		    boolean flSave=funAddUpdateHD(posCode, billDate,clientCode, SACode,lastNo,totAmt,locCode,posName,costCenterCode,costCenterName);
			        			
			        			for (int k = 0; k < mJsonArray.length(); k++) 
			        			{
			        			    mJsonObject =(JSONObject) mJsonArray.get(k);
			        			     posItemCode=mJsonObject.get("posItemCode").toString();
			        			     posItemName=mJsonObject.get("posItemName").toString();
			        			     quantity=(int) Double.parseDouble(mJsonObject.get("quantity").toString());
			        			     rate=Double.parseDouble(mJsonObject.get("rate").toString());
			        			     posCode=mJsonObject.get("posCode").toString();
			        			     billDate=mJsonObject.get("billDate").toString();
			        			     clientCode=mJsonObject.get("clientCode").toString();
			        			     wsItemCode=mJsonObject.get("wsProdCode").toString();
			        			    
			        			     itemTotAmt=Double.parseDouble(mJsonObject.get("Amount").toString());
			        			     itemPer=Double.parseDouble(mJsonObject.get("DiscPer").toString());
			        			     itemPerAmt=Double.parseDouble(mJsonObject.get("DiscAmt").toString());
			        			     
			        			       
			        			    sql+=",('"+posItemCode+"','"+posItemName+"','"+quantity+"','"+rate+"','"+posCode+"'"
			        			    	+ ",'"+billDate+"','"+clientCode+"','"+wsItemCode+"','"+SACode+"','"+costCenterCode+"','"+costCenterName+"','"+locCode+"','"+itemTotAmt+"','"+itemPer+"','"+itemPerAmt+"')";
			        			    flgData=true;
			        			    if(flSave)
			        			    {
			        			    	funAddUpdateDtl( posItemCode,posItemName,quantity,rate,posCode,billDate,clientCode,wsItemCode,SACode,lastNo );
			        			    }
			        			}
			        			if(flgData)
			        			{
			        				sql=sql.substring(1,sql.length());
			        				sql_insertPOSSales+=" "+sql;
			        				System.out.println(sql_insertPOSSales);
			        				res=st.executeUpdate(sql_insertPOSSales);
			        				// for Details Data Save in webmms
			        			}
			        			else
			        			{
			        				res=1;
			        			}
			        			SACodes+=SACode+",";
			        			l++;
//			            }
					
				}
			}
			else
			{
				
				for(int l=0;l<rootMMSArray.length(); l++) 
	            {
	            	JSONObject jsonlocWiseData=(JSONObject) rootMMSArray.get(l);
	            	String sql_insertPOSSales="insert into tblpossalesdtl "
	        				+ "(strPOSItemCode,strPOSItemName,dblQuantity,dblRate,strPOSCode,dteBillDate"
	        				+ ",strClientCode,strWSItemCode,strSACode,strCostCenterCode,strCostCenterName,strLocationCode,dblAmount,dblPercent,dblPercentAmt) "
	        				+ "values ";
	        			JSONArray mJsonArray=(JSONArray)jsonlocWiseData.get("MemberPOSSalesInfo");
	        			String sql="";
	        			boolean flgData=false;
	        			JSONObject mJsonObject = new JSONObject();
	        			for (int i = 0; i < mJsonArray.length(); i++) 
	        			{
	        				mJsonObject =(JSONObject) mJsonArray.get(i);
	        				clientCode=mJsonObject.get("clientCode").toString();
	        				rate=Double.parseDouble(mJsonObject.get("rate").toString());
	        				 billDate=mJsonObject.get("billDate").toString();
	        				 posCode=mJsonObject.get("posCode").toString();
	        				 posName=mJsonObject.get("posName").toString();
	        				 itemTotAmt=Double.parseDouble(mJsonObject.get("Amount").toString());
	        				 itemPerAmt=Double.parseDouble(mJsonObject.get("DiscAmt").toString());
	        				totAmt=totAmt+(itemTotAmt-itemPerAmt);
	        			}
	            	locCode=jsonlocWiseData.getString("WSLocation");
	            	
					stkAdjCodeLoatNo=funStockAdjumentCode(clientCode, billDate,locCode);
    				String code[] =stkAdjCodeLoatNo.split(",");
    				strStkCode=code[0];
    				String SACode=code[0];
    				lastNo=Integer.parseInt(code[1]);
	            	
	            	
	        			
	        			String costCenterCode=jsonlocWiseData.getString("costCenterCode");
	        			String costCenterName=jsonlocWiseData.getString("costCenterName");
	        			System.out.println("billDate:"+billDate);
	        			
	        		    // for HD Data Save in webmms
	        		    boolean flSave=funAddUpdateHD(posCode, billDate,clientCode, SACode,lastNo,totAmt,locCode,posName,costCenterCode,costCenterName);
	        			
	        			for (int k = 0; k < mJsonArray.length(); k++) 
	        			{
	        			    mJsonObject =(JSONObject) mJsonArray.get(k);
	        			     posItemCode=mJsonObject.get("posItemCode").toString();
	        			     posItemName=mJsonObject.get("posItemName").toString();
	        			     quantity=(int) Double.parseDouble(mJsonObject.get("quantity").toString());
	        			     rate=Double.parseDouble(mJsonObject.get("rate").toString());
	        			     posCode=mJsonObject.get("posCode").toString();
	        			     billDate=mJsonObject.get("billDate").toString();
	        			     clientCode=mJsonObject.get("clientCode").toString();
	        			     wsItemCode=mJsonObject.get("wsProdCode").toString();
	        			  
	        			     itemTotAmt=Double.parseDouble(mJsonObject.get("Amount").toString());
	        			     itemPer=Double.parseDouble(mJsonObject.get("DiscPer").toString());
	        			     itemPerAmt=Double.parseDouble(mJsonObject.get("DiscAmt").toString());
	        			     
	        			     sql+=",('"+posItemCode+"','"+posItemName+"','"+quantity+"','"+rate+"','"+posCode+"'"
			        			    	+ ",'"+billDate+"','"+clientCode+"','"+wsItemCode+"','"+SACode+"','"+costCenterCode+"','"+costCenterName+"','"+locCode+"','"+itemTotAmt+"','"+itemPer+"','"+itemPerAmt+"')";
	        			    flgData=true;
	        			    if(flSave)
	        			    {
	        			    	funAddUpdateDtl( posItemCode,posItemName,quantity,rate,posCode,billDate,clientCode,wsItemCode,SACode,lastNo );
	        			    }
	        			}
	        			if(flgData)
	        			{
	        				sql=sql.substring(1,sql.length());
	        				sql_insertPOSSales+=" "+sql;
	        				System.out.println(sql_insertPOSSales);
	        				res=st.executeUpdate(sql_insertPOSSales);
	        				// for Details Data Save in webmms
	        			}
	        			else
	        			{
	        				res=1;
	        			}
	        			SACodes+=SACode+",";
	            }
				
			}
			SACodes = SACodes.substring(0, SACodes.length()-1);
			
		}catch(Exception e)
		{
			SACodes="NA";
			e.printStackTrace();
		}
		finally
		{
			return SACodes;
		}
	}
	
	
	//Save or Update POSSalesDtl
	
		@SuppressWarnings("finally")
		public String funAddUpdateDtl( String posItemCode,String posItemName,int quantity,double rate, String posCode,String billDate,String clientCode ,String wsProdCode,String strStkCode,long lastNo){
			
			clsDatabaseConnection objDb=new clsDatabaseConnection();
			clsUtilityFunctions objUtility = new clsUtilityFunctions();
		    Connection webmms=null;
		    Statement st=null;
		   // Statement stUpdate=null;
		    PreparedStatement stUpdate=null;
		    ResultSet rs=null;
		    String startDate="";
			String strReceivedUOM="";
			String retValue="";
			String strIssueUOM="";
			String strRecipeUOM="";
			boolean flgSACode=false;
			double dblTotalAmt=0.00;
			
			String dtUserCreate=objUtility.funGetCurrentDateTime("yyyy-MM-dd");
			BigDecimal recipe=new BigDecimal(0.00);
			 try
			 { 
				webmms=objDb.funOpenMMSCon("mysql","transection");
		        st = webmms.createStatement();
		        


		        String postSADate = billDate;
		        String wsProductType="";
			
			// Check product Type	
		        String sqlProducedProd=" select a.strProdType from tblproductmaster a "
					+ " where a.strProdCode='"+wsProdCode+"' and a.strClientCode='"+clientCode+"' ";
		        st = webmms.createStatement();
		        rs=st.executeQuery(sqlProducedProd);
				while(rs.next())
				{
					wsProductType=rs.getString(1);
				}
				rs.close();
				
			// Check product is Recipe or Not	
				if(wsProductType.equals("Produced"))
				{
					String sql_ProducedItems="select a.strChildCode,a.dblQty,b.dblCostRM,c.strBOMCode,c.strParentCode"
						+ " from tblbommasterdtl a,tblproductmaster b ,tblbommasterhd c "
						+ " where a.strChildCode=b.strProdCode and a.strBOMCode=c.strBOMCode "
						+ " and (c.strParentCode='"+wsProdCode+"' or c.strBOMCode='') "
						+ " and a.strClientCode='"+clientCode+"' ";
					st = webmms.createStatement();
					ResultSet	rsBOM=st.executeQuery(sql_ProducedItems);
					while(rsBOM.next())
					{
						String childProdCode=rsBOM.getString(1);
						double dblChildQty=rsBOM.getDouble(2);
						double dblCostRM=rsBOM.getDouble(3);
						String strBOMCode = rsBOM.getString(4);
						String strParentCode = rsBOM.getString(5);
														
					// for Recipe Conversions
						double conversionRatio=1;
						String sql_Conversion="select dblReceiveConversion,dblIssueConversion,dblRecipeConversion, "
							+ " strReceivedUOM,strIssueUOM,strRecipeUOM "
							+ " from tblproductmaster where strProdCode='"+childProdCode+"' "
							+ " and strClientCode='"+clientCode+"' ";
						st = webmms.createStatement();
						ResultSet rsConv=st.executeQuery(sql_Conversion);
						strReceivedUOM="";
						strIssueUOM="";
						strRecipeUOM="";
						recipe=new BigDecimal(0.00);
						while(rsConv.next())
						{
							BigDecimal issue=rsConv.getBigDecimal(1);
							recipe=rsConv.getBigDecimal(3);
							conversionRatio=1/issue.doubleValue()/recipe.doubleValue();
							strReceivedUOM=rsConv.getString(4);
							strIssueUOM=rsConv.getString(5);
							strRecipeUOM=rsConv.getString(6);
						}
						rsConv.close();
						Integer objInt = new Integer(quantity);
						Double qty = objInt.doubleValue();
						Double finalQty = qty*dblChildQty*conversionRatio;
						double finalRate = dblCostRM*finalQty ;	
						String tempDisQty[]=finalQty.toString().split("\\.");
						String Displyqty="";
						if(tempDisQty[0].equals("0"))
						{
							Displyqty=Math.round(Float.parseFloat("0."+tempDisQty[1])*(recipe.floatValue()))+" "+strRecipeUOM;
						}else
						{
							Displyqty=tempDisQty[0]+" "+strReceivedUOM+"."+Math.round(Float.parseFloat("0."+tempDisQty[1])*(recipe.floatValue()))+" "+strRecipeUOM;
						}	          	           	
													
						String sqlDtl=" INSERT INTO `tblstockadjustmentdtl` ( `strSACode`, `strProdCode`, `dblQty`, `strType`, `dblPrice`, `dblWeight`, `strProdChar`, `intIndex`, `strRemark`, `strClientCode`, `dblRate`, `strDisplayQty`,`strWSLinkedProdCode`,`dblParentQty`) VALUES"
								+" ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) ";	
						
					//		+" ( '"+strStkCode+"', '"+childProdCode+"', "+finalQty+", 'Out', "+finalRate+", 0.00, ' ', 0, 'BOM Code:"+strBOMCode+":Parent Code:"+strParentCode+"', '"+clientCode+"', "+rate+", '"+Displyqty+"') ";
						
						stUpdate= webmms.prepareStatement(sqlDtl);
						stUpdate.setString(1, strStkCode);
						stUpdate.setString(2,childProdCode);
						stUpdate.setDouble(3,finalQty);
						stUpdate.setString(4,"Out");
						stUpdate.setDouble(5,finalRate);
						stUpdate.setDouble(6,0.00);
						stUpdate.setString(7," ");
						stUpdate.setInt(8,0);
						stUpdate.setString(9,"BOM Code:"+strBOMCode+":Parent Code:"+strParentCode+":Qty:"+qty+":ItemName:"+posItemName);
						stUpdate.setString(10,clientCode);
						stUpdate.setDouble(11,rate);
						stUpdate.setString(12,Displyqty);
						stUpdate.setString(13,strParentCode);
						stUpdate.setDouble(14,qty);
						int i=stUpdate.executeUpdate();  
					
						String updateSACode="update tblpossalesdtl set strSACode='"+strStkCode+"' "
								+ "where strPOSItemCode='"+posItemCode+"' and strPOSCode='"+posCode+"' and strClientCode='"+clientCode+"' "
								+ " and date(dteBillDate) between '"+billDate+"' and '"+billDate+"'   ";
						st = webmms.createStatement();
						st.executeUpdate(updateSACode);	
						
					}
					rsBOM.close();
									
				
			}
			else
			{
				 String sql_Conversion="select dblReceiveConversion,dblIssueConversion,dblRecipeConversion, "
						+ " strReceivedUOM,strIssueUOM,strRecipeUOM "
						+ " from tblproductmaster where strProdCode='"+wsProdCode+"'";
				 rs=st.executeQuery(sql_Conversion);	
					
				 double conversionRatio=1;
				while(rs.next())
				{
					BigDecimal issue= rs.getBigDecimal(1);
					recipe=rs.getBigDecimal(3);
					conversionRatio=1/issue.doubleValue()/recipe.doubleValue();
					strReceivedUOM=rs.getString(4);
					strIssueUOM=rs.getString(5);
					strRecipeUOM=rs.getString(6);
				}
				rs.close();
				Integer objint = new Integer(quantity);
				Double qty = objint.doubleValue();
				String tempDisQty[]=qty.toString().split("\\.");
				String Displyqty=tempDisQty[0]+" "+strReceivedUOM+"."+Math.round(Float.parseFloat("0."+tempDisQty[1])*(recipe.floatValue()))+" "+strRecipeUOM;
	           	
				String sqlDtl=" INSERT INTO `tblstockadjustmentdtl` ( `strSACode`, `strProdCode`, `dblQty`, `strType`, `dblPrice`, `dblWeight`, `strProdChar`, `intIndex`, `strRemark`, `strClientCode`, `dblRate`, `strDisplayQty`,`strWSLinkedProdCode`,`dblParentQty`) VALUES "
						+" ( '"+strStkCode+"', '"+wsProdCode+"', "+quantity+", 'Out', "+rate+", 0.00, ' ', 0, 'BOM Code:"+wsProdCode+":Parent Code:"+wsProdCode+":Qty:"+qty+":ItemName:"+posItemName+"', '"+clientCode+"', "+rate+", '"+Displyqty+"','"+wsProdCode+"','"+qty+"') ";
				st.executeUpdate(sqlDtl);
				retValue=strStkCode+","+lastNo;		        	
				String updateSACode="update tblpossalesdtl set strSACode='"+strStkCode+"' "
					+ "where strPOSItemCode='"+posItemCode+"' and strPOSCode='"+posCode+"' and strClientCode='"+clientCode+"' "
					+ " and date(dteBillDate) between '"+billDate+"' and '"+billDate+"'   ";
				st.executeUpdate(updateSACode);
				
			}
		}catch(Exception ex)
			 {
				ex.printStackTrace();
			 }
			 
		finally
			{
			
					try {
						st.close();
						webmms.close();
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
				return retValue;		        		
			}		        		
			 }
	
	// save HD Data in webmmm
	@SuppressWarnings("finally")
	public boolean funAddUpdateHD( String posCode, String billDate,String clientCode, String strStkCode,long lastNo,double totAmt,String locCode,String posName,String costCenterCode,String costCenterName)
	{
		boolean flgSave=false;
		clsDatabaseConnection objDb = new clsDatabaseConnection();
		clsUtilityFunctions objUtility = new clsUtilityFunctions();
		Connection webmms = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			webmms = objDb.funOpenMMSCon("mysql", "transection");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		try {
			st = webmms.createStatement();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		String userCode = "super";
		String dtUserCreate = objUtility.funGetCurrentDateTime("yyyy-MM-dd");
		String dispBillDate = objUtility.funGetDate("dd-MM-yyyy",billDate);
		System.out.println("billDate:"+billDate);
		
	try {
		
			sql=" INSERT INTO `tblstockadjustmenthd` (`strSACode`, `intId`, `dtSADate`, `strLocCode`, `strNarration`, `strAuthorise`,"
					+ " `strUserCreated`, `dtCreatedDate`, `strUserModified`, `dtLastModified`, "
					+ " `strClientCode`, `intLevel`, `strReasonCode`, `dblTotalAmt`, `strConversionUOM`) "
			+ " VALUES "  
			+ " ('"+strStkCode+"', '"+lastNo+"', '"+billDate+"', '"+locCode+"', '"+posCode+" : "+posName+" Sales Data For : "+dispBillDate+" : "+costCenterCode+" : "+costCenterName+"', 'Yes', '"+userCode+"', '"+dtUserCreate+"', '"+userCode+"', '"+dtUserCreate+"', '"+clientCode+"', 0, 'R0000001', '"+totAmt+"', 'RecipeUOM') ";
		System.out.println(sql);
			st.executeUpdate(sql);
			flgSave=true;
		} catch (SQLException e) {
			flgSave=false;
			e.printStackTrace();
		}finally
		{
			return flgSave;
		}

	}

	
/*	
	@SuppressWarnings("finally")
	public long funGetLastNo(String tableName,String masterName,String columnName,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
	    Connection webMMS=null;
	    Statement st=null;
		long lastNo=0;
		try
		{	
			webMMS=objDb.funOpenMMSCon("mysql","transection");
	        st = webMMS.createStatement();
	        String sql="select ifnull(max("+columnName+"),0),count("+columnName+") from "+tableName+" where strClientCode='"+clientCode+"'" ;			
	        System.out.println("Max no:"+sql);
	        ResultSet rslastNo=st.executeQuery(sql);
	        
	        while (rslastNo.next()) 
	        {
	        	lastNo=rslastNo.getLong(1);
	        }
	        rslastNo.close();	        
	        lastNo++;
	        
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				st.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				webMMS.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return lastNo;
		}
	}
	*/
	
	
	@SuppressWarnings("finally")
	public String funStockAdjumentCode(String clientCode,String billDate,String locCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
	    Connection webMMS=null;
	    Statement stProp=null;
	    Statement st=null;
	    Statement stAudi=null;
		long lastNo=0;
		String[] arrYearAnMonth = billDate.split("-");
		clsUtilityFunctions objUtill= new clsUtilityFunctions();
		String years=String.valueOf(Integer.parseInt(arrYearAnMonth[0]));
		String transYear=objUtill.funGetAlphabet((Integer.parseInt(years)%26));
		String transMonth=objUtill.funGetAlphabet(Integer.parseInt(arrYearAnMonth[1]));
		String lastNum="0";
		int lastNoAudi=0;
		String retValue ="";
		String propCode="01";
		
		try
		{
			webMMS=objDb.funOpenMMSCon("mysql","transection");
			stProp = webMMS.createStatement();
			String sql="select a.strPropertyCode from tbllocationmaster a where a.strLocCode='"+locCode+"' and a.strClientCode='"+clientCode+"'";
			ResultSet rsProp=stProp.executeQuery(sql);
			if(rsProp.next())
			{
				propCode=rsProp.getString(1);
			}
			rsProp.close();
	
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		try
		{	
			webMMS=objDb.funOpenMMSCon("mysql","transection");
	        st = webMMS.createStatement();
	        String strDocNo="";
	        String   strDocLiteral= "SA";
		    String  sql="select ifnull(max(MID(a.strSACode,7,6)),'000000' ) "
				   		+ " from tblstockadjustmenthd a where MID(a.strSACode,5,1) = '"+transYear+"' "
				   		+ " and MID(a.strSACode,6,1) = '"+transMonth+"' "
				   		+ " and MID(a.strSACode,1,2) = '"+propCode+"' and strClientCode='"+clientCode+"' ";
		    
		    String  sqlAudit = " select ifnull(max(MID(a.strTransCode,7,6)),'' ) "
			   		+ " from tblaudithd a where MID(a.strTransCode,5,1) = '"+transYear+"' "
			   		+ " and MID(a.strTransCode,6,1) = '"+transMonth+"' "
			   		+ " and MID(a.strTransCode,1,2) = '"+propCode+"' and strClientCode='"+clientCode+"' "
			   		+ "and a.strTransType='Stock Adjustment' ;  ";
		    stAudi = webMMS.createStatement();
		    ResultSet rslastNoAudi=stAudi.executeQuery(sqlAudit);
		    while (rslastNoAudi.next()) 
	        {
            	lastNoAudi=rslastNoAudi.getInt(1);
	        }
		    
		    ResultSet rslastNo=st.executeQuery(sql);
            while (rslastNo.next()) 
	        {
            	lastNum=rslastNo.getString(1);
            	lastNo=Integer.parseInt(String.valueOf(lastNum));
            	if(lastNo>lastNoAudi)
            	{
            		strDocNo=propCode+strDocLiteral+transYear+transMonth+String.format("%06d", lastNo+1);
            		retValue =strDocNo+","+(lastNo+1); 
            	}else
            	{
            		strDocNo=propCode+strDocLiteral+transYear+transMonth+String.format("%06d", lastNoAudi+1);
            		retValue =strDocNo+","+(lastNoAudi+1); 
            	}
	        }
             
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
			return retValue;
		}
	}
	
	
	
	
	
	
	
	public String[] funGetSplitedDate(String date)
	{
		return date.split("/");
	}	
	
// private String funStockAdjumentCode(String clientCode,String locCode,String billDate)
// {
//	 String propCode = "01";
//	 clsDatabaseConnection objDb = new clsDatabaseConnection();
//		clsUtilityFunctions objUtility = new clsUtilityFunctions();
//		Connection webmms = null;
//		Statement st = null;
//		ResultSet rs = null;
//		String startDate = "";
//		String strStkCode="";
//		String retValue="";
//		String sql="";
//	try
//	{
//		webmms=objDb.funOpenMMSCon("mysql","transection");
//        st = webmms.createStatement();
//		sql="select a.strPropertyCode from tbllocationmaster a where a.strLocCode='"+locCode+"' and a.strClientCode='"+clientCode+"'";
//		rs=st.executeQuery(sql);
//		while(rs.next())
//		{
//			propCode=rs.getString(1);
//		}
//
//	}catch(Exception ex)
//	{
//		ex.printStackTrace();
//	}
//		
//	 try
//	 { 
//		webmms=objDb.funOpenMMSCon("mysql","transection");
//        st = webmms.createStatement();
//         
//        sql="select dtStart from tblcompanymaster where strClientCode='"+clientCode+"'" ;
//        rs=st.executeQuery(sql);
//		while(rs.next())
//		{
//			startDate=rs.getString(1);
//		}
//		
//		long lastNo = funGetLastNo("tblstockadjustmenthd", "SACode", "strSACode",clientCode,billDate,propCode);
//		System.out.println("lastNo:"+lastNo);
//		String year=startDate.split("-")[0];
//		//String year = funGetSplitedDate(startDate)[2];
//		String cd = objUtility.funGetTransactionCode("SA", propCode, year);
//		strStkCode = cd + String.format("%06d", lastNo);
//		/*if(lastNo==1)
//		{
//			strStkCode = cd + String.format("%06d", 1);
//		}
//		else
//		{
//			strStkCode = cd + String.format("%06d", lastNo+1);
//		}
//		*/
//		
//		
//		
//		
//		retValue =strStkCode+","+lastNo;
//    }catch(Exception ex)
//    { 
//    	ex.printStackTrace();
//    }
//	
//	 return retValue;
// }
	
	
	@GET
    @Path("/funGetWebStockSearch")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetWebStockSearch(@QueryParam("masterName") String masterName, @QueryParam("searchCode") String searchCode, @QueryParam("clientCode") String clientCode)
    {
	
	JSONObject jObjSearchData = new JSONObject();
	
	try
	{
	    jObjSearchData = funGetWebStockSearchData(masterName, searchCode, clientCode);
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return jObjSearchData;
	
    }
	
	
	public JSONObject funGetWebStockSearchData(String masterName,String searchCode,String clientCode)
 	{
 		JSONObject jObjSearchData = new JSONObject();
		JSONArray jArrData = new JSONArray();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webmms=null;
        Statement st = null;
		String sql="";
		
	try
	{
		
		switch (masterName)
		{
		    case "suppMasterWeb-Service":
						
		    	webmms=objDb.funOpenMMSCon("mysql","transection");
		        st = webmms.createStatement();
		        sql = " select a.strPCode , a.strPName "
		    		+ " from tblpartymaster a where a.strClientCode = '"+clientCode+"' and a.strPType='supp'  " ;	
			
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rs.getString(1));
			    	 jArrtem.put(rs.getString(2));
			    	 jArrData.put(jArrtem);
				}
			    rs.close();
			    break;
			    
		    case "suppLinkedWeb-Service":
				
		    	webmms=objDb.funOpenMMSCon("mysql","transection");
		        st = webmms.createStatement();
		        sql = " select a.strSGCode , a.strGDes ,a.strAccountCode,a.strSGName  from tbllinkup a,tblpartymaster b where a.strSGCode=b.strPCode "
		        	+ "  and a.strClientCode =b.strClientCode and a.strClientCode = '"+clientCode+"' and b.strPType='supp'  " ;	
			
			    ResultSet rs2=st.executeQuery(sql);
			    while(rs2.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rs2.getString(1));
			    	 jArrtem.put(rs2.getString(2));
			    	 jArrtem.put(rs2.getString(3));
			    	 jArrtem.put(rs2.getString(4));
			    	 jArrData.put(jArrtem);
				}
			    rs2.close();
			    break;    
			    
		}
			jObjSearchData.put(masterName, jArrData);
	}catch(Exception ex)
	{
		ex.printStackTrace();
	}finally
	{
		return jObjSearchData;
	}
	
 	}
	
	
	@POST
	@Path("/funLoadWSLinkedSupp")
	@Consumes(MediaType.APPLICATION_JSON)
    public  Response funLoadWSLinkedSupp(JSONObject objCLData)
    {
	
	JSONObject jObjData = new JSONObject();
	clsDatabaseConnection objDb=new clsDatabaseConnection();
//	JSONArray jArrData = new JSONArray();
    Connection webmms=null;
    Statement st = null;
	String sql="";
	
	JSONObject jsonObj =new JSONObject();
	try
	{
		String clientCode = objCLData.get("clientCode").toString();
		String suppCode = objCLData.get("suppCode").toString();
		webmms=objDb.funOpenMMSCon("mysql","transection");
        st = webmms.createStatement();
        sql = " select a.strSGCode , a.strGDes ,a.strAccountCode,a.strSGName  from tbllinkup a,tblpartymaster b where a.strSGCode=b.strPCode "
	        	+ "  and a.strClientCode =b.strClientCode and a.strClientCode = '"+clientCode+"' and b.strPType='supp' and  a.strSGCode='"+suppCode+"' " ;	
		
	    ResultSet rs=st.executeQuery(sql);
	    while(rs.next())
		{
	    	
	    	jsonObj.put("strSuppCode",rs.getString(1));
	    	jsonObj.put("strSuppName",rs.getString(2));
	    	jsonObj.put("strCreditorCodeOrAccountCode",rs.getString(3));
	    	jsonObj.put("strCreditorNameOrAccountName",rs.getString(2));
	    	
		}
	    rs.close();
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		return   Response.status(201).entity(jsonObj).build();
	
    }
	
	@POST
	@Path("/funLoadUnBilledGRNDateWise")
	@Consumes(MediaType.APPLICATION_JSON)
    public  Response funLoadUnBilledGRNDateWise(JSONObject objCLData)
    {
	
	JSONObject jObjData = new JSONObject();
	clsDatabaseConnection objDb=new clsDatabaseConnection();
	JSONArray jArrData = new JSONArray();
    Connection webmms=null;
    Statement st = null;
	String sql="";
	try
	{
		String clientCode = objCLData.get("clientCode").toString();
		String suppCode = objCLData.get("strSuppCode").toString();
		String fromdate = objCLData.get("fromDate").toString();
		String todate = objCLData.get("toDate").toString();
		webmms=objDb.funOpenMMSCon("mysql","transection");
        st = webmms.createStatement();
        sql = " select a.strGRNCode,a.strBillNo,a.dblTotal,DATE_FORMAT(a.dtGRNDate,'%d-%m-%Y'),DATE_FORMAT(a.dtBillDate,'%d-%m-%Y'),DATE_FORMAT(a.dtDueDate,'%d-%m-%Y') "
        		+ " from "+clsConfigFile.mmsDatabaseName+".tblgrnhd a where a.strGRNCode not in(select b.strGRNCode from  "+clsConfigFile.webbooksDatabaseName+".tblscbillgrndtl b ) "
        		+ " and a.strClientCode='"+clientCode+"'  and a.strSuppCode='"+suppCode+"' "
        				+ "  AND date(a.dtGRNDate) between '"+fromdate+"' and  '"+todate+"' ";
        		    ResultSet rs=st.executeQuery(sql);
	    while(rs.next())
		{
	    	JSONObject jsonObj =new JSONObject();
	    	jsonObj.put("strGRNCode",rs.getString(1));
	    	jsonObj.put("strBillNo",rs.getString(2));
	    	jsonObj.put("dblTotal",rs.getString(3));
	    	jsonObj.put("dtGRNDate",rs.getString(4));
	    	jsonObj.put("dtBillDate",rs.getString(5));
	    	jsonObj.put("dtDueDate",rs.getString(6));
	    	jArrData.put(jsonObj);
	    	
		}
	    jObjData.put("unBilledGRN", jArrData);
	    rs.close();
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		return   Response.status(201).entity(jObjData).build();
	
    }
	
	
	@POST
	@Path("/funLoadUnBilledGRN")
	@Consumes(MediaType.APPLICATION_JSON)
    public  Response funLoadUnBilledGRN(JSONObject objCLData)
    {
	
	JSONObject jObjData = new JSONObject();
	clsDatabaseConnection objDb=new clsDatabaseConnection();
	JSONArray jArrData = new JSONArray();
    Connection webmms=null;
    Statement st = null;
	String sql="";
	try
	{
		String clientCode = objCLData.get("clientCode").toString();
		String suppCode = objCLData.get("strSuppCode").toString();
		webmms=objDb.funOpenMMSCon("mysql","transection");
        st = webmms.createStatement();
        sql = " select a.strGRNCode,a.strBillNo,a.dblTotal,DATE_FORMAT(a.dtGRNDate,'%d-%m-%Y'),DATE_FORMAT(a.dtBillDate,'%d-%m-%Y'),DATE_FORMAT(a.dtDueDate,'%d-%m-%Y') "
        		+ " from "+clsConfigFile.mmsDatabaseName+".tblgrnhd a where a.strGRNCode not in(select b.strGRNCode from  "+clsConfigFile.webbooksDatabaseName+".tblscbillgrndtl b ) "
        		+ " and a.strClientCode='"+clientCode+"'  and a.strSuppCode='"+suppCode+"' ";
        		    ResultSet rs=st.executeQuery(sql);
	    while(rs.next())
		{
	    	JSONObject jsonObj =new JSONObject();
	    	jsonObj.put("strGRNCode",rs.getString(1));
	    	jsonObj.put("strBillNo",rs.getString(2));
	    	jsonObj.put("dblTotal",rs.getString(3));
	    	jsonObj.put("dtGRNDate",rs.getString(4));
	    	jsonObj.put("dtBillDate",rs.getString(5));
	    	jsonObj.put("dtDueDate",rs.getString(6));
	    	jArrData.put(jsonObj);
	    	
		}
	    jObjData.put("unBilledGRN", jArrData);
	    rs.close();
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		return   Response.status(201).entity(jObjData).build();
	
    }
	
	@POST
	@Path("/funLoadGrnWiseAccountAmt")
	@Consumes(MediaType.APPLICATION_JSON)
    public  Response funLoadGrnWiseAccountAmt(JSONObject objCLData)
    {
	
	JSONObject jObjData = new JSONObject();
	clsDatabaseConnection objDb=new clsDatabaseConnection();
	JSONArray jArrData = new JSONArray();
    Connection webmms=null;
    Statement stGrn = null;
    Statement stGrnTax = null;
    Statement stGrnDisc = null;
    Statement stAccDisc = null;
	String sql="";
	try
	{
		String clientCode = objCLData.get("clientCode").toString();
		String grnCodes = objCLData.get("grnCodes").toString();
		String strStart="";
		String[] tempgrn = grnCodes.split(",");
		
		for(String grn :tempgrn)
		{
			strStart+="a.strGRNCode='"+grn+"' or ";
		}
		if(strStart.length()>2)
		{
			strStart=(String) strStart.subSequence(0, strStart.length()-3);
			strStart =" and ( "+strStart+" )";
		}
		
		webmms=objDb.funOpenMMSCon("mysql","transection");
        stGrn = webmms.createStatement();
        sql = " select sum(a.dblTotalPrice),c.strSGCode,c.strSGName ,d.strAccountCode,d.strGDes "
        		+ " FROM tblgrndtl a,tblproductmaster b,tblsubgroupmaster c ,tbllinkup d "
        		+ " where a.strProdCode=b.strProdCode  and b.strSGCode=c.strSGCode and c.strSGCode=d.strSGCode "
        	    + strStart
        		+ " and a.strClientCode=b.strClientCode and b.strClientCode=c.strClientCode "
        		+ " and c.strClientCode=d.strClientCode and a.strClientCode='"+clientCode+"' group by  d.strAccountCode ";
        		    ResultSet rsGrn=stGrn.executeQuery(sql);
	    while(rsGrn.next())
		{
	    	JSONObject jsonObj =new JSONObject();
	    	jsonObj.put("dblTotalPrice",rsGrn.getString(1));
	    	jsonObj.put("strSGCode",rsGrn.getString(2));
	    	jsonObj.put("strSGName",rsGrn.getString(3));
	    	jsonObj.put("strAccountCode",rsGrn.getString(4));
	    	jsonObj.put("strAccountName",rsGrn.getString(5));
	    	jsonObj.put("CrDr","Dr");
	    	
	    	jArrData.put(jsonObj);
	    	
		}
	    stGrn.close();
	    rsGrn.close();
	   
	    
	    stGrnTax = webmms.createStatement();
	    sql = " select sum(a.strTaxAmt),b.strTaxCode,b.strTaxDesc ,c.strAccountCode,c.strGDes  "
	    		+ " FROM tblgrntaxdtl a,tbltaxhd b ,tbllinkup c "
	    		+ " where a.strTaxCode=b.strTaxCode and a.strTaxCode = c.strSGCode  "
	    		+ strStart
	    		+ " and a.strClientCode=b.strClientCode and b.strClientCode=c.strClientCode  "
	    		+ "  and a.strClientCode='"+clientCode+"' group by  c.strAccountCode ";
	    ResultSet  rsGrnTax=stGrnTax.executeQuery(sql);
        while(rsGrnTax.next())
      		{
      	    	JSONObject jsonObj =new JSONObject();
      	    	jsonObj.put("dblTotalPrice",rsGrnTax.getString(1));
      	    	jsonObj.put("strSGCode",rsGrnTax.getString(2));
      	    	jsonObj.put("strSGName",rsGrnTax.getString(3));
      	    	jsonObj.put("strAccountCode",rsGrnTax.getString(4));
      	    	jsonObj.put("strAccountName",rsGrnTax.getString(5));
      	    	jsonObj.put("CrDr","Dr");
      	    	
      	    	jArrData.put(jsonObj);
      		}
        stGrnTax.close();
        rsGrnTax.close();
        
        sql="select sum(a.dblDisAmt)  FROM tblgrnhd a   "
        		+ " where a.strClientCode='"+clientCode+"' "
        		+ strStart ;
        stGrnDisc = webmms.createStatement();
        ResultSet  rsGrnDisc=stGrnDisc.executeQuery(sql);
        double discAmt=0.00;
        if(rsGrnDisc.next())
  		{
        	discAmt=rsGrnDisc.getDouble(1);
  		}	
        stGrnDisc.close();
    	rsGrnDisc.close();
        if(discAmt>0)
        { 
        		 sql="  select a.strSGCode,a.strSGName ,a.strAccountCode ,a.strGDes "
        		 	+ " from tbllinkup a   where a.strSGCode='Disc' and a.strClientCode='"+clientCode+"' "; 
        		 stAccDisc = webmms.createStatement();
        		 ResultSet  rsAccDisc=stAccDisc.executeQuery(sql);
        	        while(rsAccDisc.next())
        	  		{
        	        	JSONObject jsonObj =new JSONObject();
        	        	jsonObj.put("dblTotalPrice",discAmt);
        	  	    	jsonObj.put("strSGCode",rsAccDisc.getString(1));
        	  	    	jsonObj.put("strSGName",rsAccDisc.getString(2));
        	  	    	jsonObj.put("strAccountCode",rsAccDisc.getString(3));
        	  	    	jsonObj.put("strAccountName",rsAccDisc.getString(4));
        	  	    	jsonObj.put("CrDr","Cr");
        	  	    	
        	  	    	jArrData.put(jsonObj);
        	  		}
        	        stAccDisc.close();
        	        rsAccDisc.close();
        	       
       }
      
        
        jObjData.put("GrnWiseAccountAmt", jArrData);
	    
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		return   Response.status(201).entity(jObjData).build();
	
    }
	
	@POST
    @Path("/funCreateProductInMMS")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response funCreateProductInMMS(JSONObject objCLData)
    {
    	String prodCode = funCreateProduct(objCLData);;		
		return Response.status(201).entity(prodCode).build();
	
    }
    @SuppressWarnings("finally")
    public String funCreateProduct(JSONObject objCLData){
    	clsDatabaseConnection mmsCon =new clsDatabaseConnection();
    	Connection webmms=null;
    	Statement st = null;
    	String res = "";
    	
    	try
    	{
//    		 mmsCon = clsDatabaseConnection.DBMMSCONNECTION;
    		 
    		webmms = mmsCon.funOpenMMSCon("mysql","master");
    		st = webmms.createStatement();
    	    
    	    String itemCode =  (String) objCLData.get("itemCode");
    	    String itemName =  (String) objCLData.get("itemName");
    	    String wsProductCode =  (String) objCLData.get("wsProductCode");
    	    
    	    
    	    String clientCode =  (String) objCLData.get("clientCode");
    	    String userCode =  (String) objCLData.get("userCode");
    	    String createDate =  (String) objCLData.get("createDate");
    	    JSONObject mJsonObject = new JSONObject();
    	  
    	    String sqlProd = " select strProdCode,strProdName from tblproductmaster  where strProdCode='"+wsProductCode+"' "
    	    		+ " and  strClientCode='"+clientCode+"' ";
    	    ResultSet rsProdCreated = st.executeQuery(sqlProd);
    	    if(rsProdCreated.next())
            {
    	    	String prodCode=rsProdCreated.getString(1);
    	    	String prodName=rsProdCreated.getString(2);
    	    	
    	    	String sqlUpdate = " update tblproductmaster  set strProdName='"+itemName+"',strUserModified='"+userCode+"',dtLastModified='"+createDate+"' ,strPartNo='"+itemCode+"' "
    	    			+ "  where strProdCode='"+prodCode+"' and strClientCode='"+clientCode+"' ";
    	    	st.executeUpdate(sqlUpdate);
    	    	res=prodCode;
            }else
            {
	            String sql="select ifnull(max(intId),0),count(intId) from tblproductmaster where strClientCode='"+clientCode+"';";
	    	    ResultSet rsProduct = st.executeQuery(sql);
	    	    int lastNo=0;
	            if(rsProduct.next())
	            {
	    	     lastNo=rsProduct.getInt(1);
	            }rsProduct.close();
	            lastNo=lastNo+1;
	    	    String productCode = "P" + String.format("%07d", lastNo);
	    	    
	    		String sqlInsertIntoProd = "insert INTO tblproductmaster  (intId,strProdCode,strPartNo,strProdName,strUOM,strSGCode,  strProdType,strLocCode,strProductImage,strUserCreated,strUserModified,dtCreatedDate,"
	    									  + "dtLastModified,strClientCode)values ('"+lastNo+"','"+productCode+"','"+itemCode+"','"+itemName+"','NOS','SG000001','Produced','','','"+userCode+"','"+userCode+"','"+createDate+"','"+createDate+"','"+clientCode+"') ";
	    		st.executeUpdate(sqlInsertIntoProd);
	    		res=productCode;
    		}
    	}
    	catch (Exception e)
    	{
    		
    		
			e.printStackTrace();
    	}
    	
    	finally {
    	    try
    	    {
    		if (null != st)
    		{
    		    st.close();
    		}
//    		if (null != cmsCon)
//    		{
//    		    cmsCon.close();
//    		}
    	    }
    	    catch (SQLException e)
    	    {
    		e.printStackTrace();
    	    }
    		return res;
    		
    	}
    }
    
    
    
    @POST
	@Path("/funLoadUnPayedGRN")
	@Consumes(MediaType.APPLICATION_JSON)
    public  Response funLoadUnPayedGRN(JSONObject objCLData)
    {
	
	JSONObject jObjData = new JSONObject();
	clsDatabaseConnection objDb=new clsDatabaseConnection();
	JSONArray jArrData = new JSONArray();
    Connection webmms=null;
    Connection webbooks=null;
    Statement st = null;
    Statement st2 = null;
	String sql="";
	try
	{
		DecimalFormat df = new DecimalFormat("#.00");
		String clientCode = objCLData.get("clientCode").toString();
		String strDebtorCode = objCLData.get("strDebtorCode").toString();
		double currConversion  = Double.parseDouble(objCLData.get("currConversion").toString());
		String propCode = objCLData.get("propCode").toString();
		webmms=objDb.funOpenMMSCon("mysql","transection");
		webbooks=objDb.funOpenWebbooksCon("mysql","transection");
		String grnCodes="";
		String querygrnCodes="";
		String sqlJVGRN = " select a.strNarration from tbljvhd a where a.strNarration like '%JV Generated by GRN%' and a.strClientCode='"+clientCode+"' and a.strPropertyCode='"+propCode+"' ";
		st2 = webbooks.createStatement();
		ResultSet rsjv=st2.executeQuery(sqlJVGRN);
	    while(rsjv.next())
		{
	    	grnCodes = rsjv.getString(1).split(":")[1].toString();
	    	querygrnCodes += " a.strGRNCode = '"+grnCodes+"' or";
		}
		
	    querygrnCodes = querygrnCodes.substring(0, querygrnCodes.length()-2);
        st = webmms.createStatement();
        sql = " select a.strGRNCode,a.strBillNo,a.dblTotal,DATE_FORMAT(a.dtGRNDate,'%d-%m-%Y'),DATE_FORMAT(a.dtBillDate,'%d-%m-%Y'),DATE_FORMAT(a.dtDueDate,'%d-%m-%Y') "
        		+ " from "+clsConfigFile.mmsDatabaseName+".tblgrnhd a,"+clsConfigFile.mmsDatabaseName+".tblpartymaster b where a.strGRNCode not in "
        		+ " (select b.strGRNCode from  "+clsConfigFile.webbooksDatabaseName+".tblpaymentgrndtl b where b.dblGRNAmt=b.dblPayedAmt and b.strClientCode='"+clientCode+"' )   and a.strClientCode='"+clientCode+"' "
        		+ " and a.strSuppCode=b.strPCode and b.strDebtorCode='"+strDebtorCode+"' and ( "+querygrnCodes+" )  ";
        
        ResultSet rs=st.executeQuery(sql);
	    while(rs.next())
		{
	    	double totAmt =Double.parseDouble(rs.getString(3));
	    	JSONObject jsonObj =new JSONObject();
	    	jsonObj.put("strGRNCode",rs.getString(1));
	    	jsonObj.put("strBillNo",rs.getString(2));
	    	jsonObj.put("dblTotal",df.format(totAmt));
	    	jsonObj.put("dtGRNDate",rs.getString(4));
	    	jsonObj.put("dtBillDate",rs.getString(5));
	    	jsonObj.put("dtDueDate",rs.getString(6));
	    	jArrData.put(jsonObj);
	    	
		}
	    jObjData.put("unPayedGRN", jArrData);
	    rs.close();
        
        String saSql=" select strSACode  docNo,strSACode  BillNo,dblTotalAmt  TotalAmt,  DATE_FORMAT(dtSADate,'%d-%m-%Y')  docDate, "
        		+ " DATE_FORMAT(dtSADate,'%d-%m-%Y')  BillDate, DATE_FORMAT(dtSADate,'%d-%m-%Y')  dueDate "
        		+ " from "+clsConfigFile.mmsDatabaseName+".tblstockadjustmenthd  where strClientCode='"+clientCode+"' "
        		+ " and strNarration like '%SCCode:"+strDebtorCode+"%' and  strSACode NOT IN ( "
        		+ " SELECT pp.strGRNCode  FROM "+clsConfigFile.webbooksDatabaseName+".tblpaymentgrndtl pp "
        		+ " WHERE pp.dblGRNAmt=pp.dblPayedAmt AND pp.strClientCode='"+clientCode+"')  ";    
        
        ResultSet rs2=st.executeQuery(saSql);
	    while(rs2.next())
		{
	    	double totAmt =Double.parseDouble(rs2.getString(3));
	    	JSONObject jsonObj =new JSONObject();
	    	jsonObj.put("strGRNCode",rs2.getString(1));
	    	jsonObj.put("strBillNo",rs2.getString(2));
	    	jsonObj.put("dblTotal",df.format(totAmt));
	    	jsonObj.put("dtGRNDate",rs2.getString(4));
	    	jsonObj.put("dtBillDate",rs2.getString(5));
	    	jsonObj.put("dtDueDate",rs2.getString(6));
	    	jArrData.put(jsonObj);
	    	
		}
	    jObjData.put("unPayedGRN", jArrData);
	    rs2.close();
        
       /* sql = " select docNo,BillNo,TotalAmt,docDate,BillDate,dueDate "
        		+ " from ( "
        		+ " select strSACode  docNo,strSACode  BillNo,dblTotalAmt  TotalAmt,"
        		+ "  DATE_FORMAT(dtSADate,'%d-%m-%Y')  docDate,  DATE_FORMAT(dtSADate,'%d-%m-%Y')"
        		+ "  BillDate, DATE_FORMAT(dtSADate,'%d-%m-%Y')  dueDate "
        		+ " from "+clsConfigFile.mmsDatabaseName+".tblstockadjustmenthd  where strClientCode='"+clientCode+"' "
        		+ " and strNarration like '%SCCode:"+strDebtorCode+"%' "
        		+ " and  strSACode NOT IN ( "
        		+ " SELECT pp.strGRNCode  FROM "+clsConfigFile.webbooksDatabaseName+".tblpaymentgrndtl pp "
        		+ "  WHERE pp.dblGRNAmt=pp.dblPayedAmt AND pp.strClientCode='"+clientCode+"') "
        		+ " UNION all "
        		+ " SELECT a.strGRNCode  docNo,a.strBillNo  BillNo,a.dblTotal  TotalAmt, "
        		+ " DATE_FORMAT(a.dtGRNDate,'%d-%m-%Y')  docDate,  DATE_FORMAT(a.dtBillDate,'%d-%m-%Y') "
        		+ " BillDate,  DATE_FORMAT(a.dtDueDate,'%d-%m-%Y')  dueDate "
        		+ " FROM "+clsConfigFile.mmsDatabaseName+".tblgrnhd a,dbwebmms.tblpartymaster b "
        		+ " WHERE a.strGRNCode NOT IN "
        		+ " (  SELECT b.strGRNCode  FROM "+clsConfigFile.webbooksDatabaseName+".tblpaymentgrndtl b "
        		+ " WHERE b.dblGRNAmt=b.dblPayedAmt AND b.strClientCode='"+clientCode+"') "
        		+ " AND a.strClientCode='"+clientCode+"' AND a.strSuppCode=b.strPCode AND b.strDebtorCode='"+strDebtorCode+"')  tblGRN " ;  */
        
        
        
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		return   Response.status(201).entity(jObjData).build();
	
    }
    
    
    @POST
   	@Path("/funLoadUnPayedInv")
   	@Consumes(MediaType.APPLICATION_JSON)
       public  Response funLoadUnPayedInv(JSONObject objCLData)
       {
   	
   	JSONObject jObjData = new JSONObject();
   	clsDatabaseConnection objDb=new clsDatabaseConnection();
   	JSONArray jArrData = new JSONArray();
       Connection webmms=null;
       Statement st = null;
       System.out.print("Webservice call");
   	String sql="";
   	try
   	{
   		String clientCode = objCLData.get("clientCode").toString();
   		String strDebtorCode = objCLData.get("strDebtorCode").toString();

   		DecimalFormat df = new DecimalFormat("#.00");
   		double currConversion  = Double.parseDouble(objCLData.get("currConversion").toString());
   		webmms=objDb.funOpenMMSCon("mysql","transection");
           st = webmms.createStatement();
           sql = "  SELECT a.strInvCode,a.strPackNo,a.dblGrandTotal,DATE_FORMAT(a.dteInvDate,'%d-%m-%Y'),DATE_FORMAT(a.dteInvDate,'%d-%m-%Y'),DATE_FORMAT(a.dteInvDate,'%d-%m-%Y') "
           		+ " FROM "+clsConfigFile.mmsDatabaseName+".tblinvoicehd a,"+clsConfigFile.mmsDatabaseName+".tblpartymaster b "
           		+ " WHERE a.strInvCode NOT IN "
           		+ " ( SELECT b.strInvCode FROM "+clsConfigFile.webbooksDatabaseName+".tblreceiptinvdtl b "
           		+ " WHERE b.dblInvAmt=b.dblPayedAmt AND b.strClientCode='"+clientCode+"') "
           		+ " AND a.strClientCode='"+clientCode+"' AND a.strCustCode=b.strPCode "
           		+ " AND b.strDebtorCode='"+strDebtorCode+"'  "; 
           System.out.print(sql);
           ResultSet rs=st.executeQuery(sql);
   	    while(rs.next())
   		{

   	    	double totAmt =Double.parseDouble(rs.getString(3));
   	    	JSONObject jsonObj =new JSONObject();
   	    	jsonObj.put("strInvCode",rs.getString(1));
   	    	jsonObj.put("strBillNo",rs.getString(2));
   	    	jsonObj.put("dblGrandTotal",df.format(totAmt));
   	    	jsonObj.put("dteInvDate",rs.getString(4));
   	    	jsonObj.put("dteBillDate",rs.getString(5));
   	    	jsonObj.put("dteDueDate",rs.getString(6));
   	    	jArrData.put(jsonObj);
   	    	
   		}
   	    jObjData.put("unPayedInv", jArrData);
   	    rs.close();
   	}
   	catch (Exception e)
   	{
   	    // TODO Auto-generated catch block
   	    e.printStackTrace();
   	}
   		return   Response.status(201).entity(jObjData).build();
   	
       }
    
    
    @POST
	@Path("/funLoadUnPayedScBill")
	@Consumes(MediaType.APPLICATION_JSON)
    public  Response funLoadUnPayedScBill(JSONObject objCLData)
    {
	
	JSONObject jObjData = new JSONObject();
	clsDatabaseConnection objDb=new clsDatabaseConnection();
	JSONArray jArrData = new JSONArray();
    Connection webmms=null;
    Statement st = null;
    Statement stLinkUp = null;
	String sql="";
	try
	{	
		DecimalFormat df = new DecimalFormat("#.00");
		String clientCode = objCLData.get("clientCode").toString();
		String []fromDate = objCLData.get("fromDate").toString().split("-");
		String []toDate = objCLData.get("toDate").toString().split("-");
		String debtorCode = objCLData.get("debtorCode").toString();
		double currConversion  = Double.parseDouble(objCLData.get("currConversion").toString());
		webmms=objDb.funOpenMMSCon("mysql","transection");
        st = webmms.createStatement();
        String fDate=fromDate[2]+"-"+fromDate[1]+"-"+fromDate[0];
        String tDate=toDate[2]+"-"+toDate[1]+"-"+toDate[0];
	
        String sqlLinuKp="select strSGCode from tbllinkup where strAccountCode='"+debtorCode+"' and strClientCode='"+clientCode+"'";
        stLinkUp=webmms.createStatement();
        ResultSet rsLinkup=stLinkUp.executeQuery(sqlLinuKp);
        while(rsLinkup.next())
		{
        String saSql=" select a.strVoucherNo,a.dblTotalAmount,a.dteBillDate,a.dteDueDate,a.strBillNo,a.strSuppCode,a.strAcCode  "   
        			+" from dbwebbooks.tblscbillhd a where  a.strClientCode='"+clientCode+"'and a.strSuppCode='"+rsLinkup.getString(1)+"' and a.dteBillDate between '"+fDate+"' and '"+tDate+"' and a.strVoucherNo NOT IN(SELECT pp.strScCode "
        			+" FROM dbwebbooks.tblpaymentScBilldtl pp "
        			+" WHERE pp.dblScBillAmt=pp.dblPayedAmt AND pp.strClientCode='"+clientCode+"') ";    
        
        ResultSet rs2=st.executeQuery(saSql);
	    while(rs2.next())
		{
	    	double totAmt =Double.parseDouble(rs2.getString(2));
	    	JSONObject jsonObj =new JSONObject();
	    	jsonObj.put("strGRNCode",rs2.getString(1));
	    	jsonObj.put("strBillNo",rs2.getString(5));
	    	jsonObj.put("dblTotal",df.format(totAmt));
	    	jsonObj.put("dtGRNDate",rs2.getString(4));
	    	jsonObj.put("dtBillDate",rs2.getString(3));
	    	jsonObj.put("dtDueDate",rs2.getString(4));
	    	jArrData.put(jsonObj);
	    	
		}
	    rs2.close();
		}
        
        rsLinkup.close();
	    jObjData.put("unPayedGRN", jArrData);
	   
        
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		return   Response.status(201).entity(jObjData).build();
	
    }
    
	
	
	
}
