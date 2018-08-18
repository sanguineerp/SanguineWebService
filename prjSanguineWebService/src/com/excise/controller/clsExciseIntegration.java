package com.excise.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

import com.webservice.controller.clsDatabaseConnection;

@Path("/ExciseIntegration")
public class clsExciseIntegration 
{
	@SuppressWarnings("finally")
	@GET 
	@Path("/funInvokeExciseWebService")
	@Produces(MediaType.APPLICATION_JSON)
	public Response funGetBillInfo()
	{
		String response = "false";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webExciseCon=null;
		try
		{
			webExciseCon=objDb.funOpenExciseCon("mysql","transaction");
			response="true";
		}
		catch(Exception e)
		{
			response="false";
			e.printStackTrace();
		}
		
		return Response.status(201).entity(response).build();
	}
	
	String sysDate="2015-03-01";

			@POST
			@Path("/funPostPOSSaleData")
			@Consumes(MediaType.APPLICATION_JSON)
			public Response funPostPOSSalesData(JSONObject objCLData)
			{
				String response = "false";
				if(funInsertPOSSalesData(objCLData)>0)
				{
					response = "true";
				}
				return Response.status(201).entity(response).build();
			}
			
			@SuppressWarnings("finally")
			private int funInsertPOSSalesData(JSONObject objCLData)
			{
				int res=0;
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection cmsCon=null;
				try
				{
//					String serverName = clsConfigFile.serverName;
//					String database =clsConfigFile.databaseName;
					cmsCon=objDb.funOpenMMSCon("mysql","transaction");
		            Statement st = cmsCon.createStatement();
		            
					String sql_insertPOSSales="insert into tblexcisepossale "
						+ "(strPOSItemCode,strPOSItemName,intQuantity,dblRate,strPOSCode,dteBillDate"
						+ ",strClientCode,strBrandCode,strBillNo) "
						+ "values ";
					JSONArray mJsonArray=(JSONArray)objCLData.get("MemberPOSSalesInfo");
					String sql="";
					boolean flgData=false;
					JSONObject mJsonObject = new JSONObject();
					for (int i = 0; i < mJsonArray.length(); i++) 
					{
					    mJsonObject =(JSONObject) mJsonArray.get(i);
					    
					    String posItemCode=mJsonObject.get("posItemCode").toString();
					    String posItemName=mJsonObject.get("posItemName").toString();
					    Integer quantity=(int) Double.parseDouble(mJsonObject.get("quantity").toString());
					    double rate=Double.parseDouble(mJsonObject.get("rate").toString());
					    String posCode=mJsonObject.get("posCode").toString();
					    String billDate=mJsonObject.get("billDate").toString();
					    String clientCode=mJsonObject.get("clientCode").toString();
					  
					    sql+=",('"+posItemCode+"','"+posItemName+"','"+quantity+"','"+rate+"','"+posCode+"'"
					    	+ ",'"+billDate+"','"+clientCode+"','','')";
					    flgData=true;
					}
					
					if(flgData)
					{
						sql=sql.substring(1,sql.length());
						sql_insertPOSSales+=" "+sql;
						System.out.println(sql_insertPOSSales);
						res=st.executeUpdate(sql_insertPOSSales);
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
					return res;
				}
			}
			
			
			@SuppressWarnings("finally")
			@GET 
			@Path("/funGetFLR6List")
			@Produces(MediaType.APPLICATION_JSON)
			public String funGetFLR6List(@QueryParam("FromDate") String fromDate,@QueryParam("ToDate") String toDate
					,@QueryParam("ClientCode") String clientCode,@QueryParam("inPeg") String inPeg)
			{
				clsDatabaseConnection objDb=new clsDatabaseConnection();
		        Connection cmsCon=null;
		        Statement st=null;
		        JSONObject jObjFLR6Data=new JSONObject();
		        String tempSizeClientCode=clientCode;
		        String tempBrandClientCode=clientCode;
		        
		        try {
		        	cmsCon=objDb.funOpenExciseCon("mysql","master");
		            st = cmsCon.createStatement();
		            
		            String sql="select a.strLicenceNo,a.strLicenceName,CONCAT(a.strAddress1,',',a.strAddress2,"
		    				+ "',',a.strAddress3,',',b.strCityName,CONCAT('-',a.strPINCode)) as address "
		    				+ "from tbllicencemaster a ,tblcitymaster b "
		    				+ "where a.strCity=b.strCityCode and a.strClientCode='"+clientCode+"'";
		            
		            ResultSet rsLicenceData = st.executeQuery(sql);
		            while (rsLicenceData.next()) 
		            {
		            	jObjFLR6Data.put("LicenceNo", rsLicenceData.getString(1));
		            	jObjFLR6Data.put("CompanyName", rsLicenceData.getString(2));
		            	jObjFLR6Data.put("Address", rsLicenceData.getString(3));
		            }
		            
		            String Global_Masters="select a.strSubCategory,a.strBrandMaster,a.strSizeMaster from tblexcisepropertymaster a "
	            							+ "	where strClientCode='"+clientCode+"' ";
		            ResultSet rsGlobalData = st.executeQuery(Global_Masters);
		            while (rsGlobalData.next()) 
		            {
		            	if(rsGlobalData.getString(2).equalsIgnoreCase("All")){
		    				tempBrandClientCode="All";
		    			}
		            	if(rsGlobalData.getString(3).equalsIgnoreCase("All")){
		            		tempSizeClientCode="All";
		    			}
		            }

		      // Create JSON Data For SubCategory and their sizes
		            String sql_SubCategoryList="select strSubCategoryCode, strSubCategoryName,strCategoryCode,"
		            		+ " intAvailableSizes from tblsubcategorymaster ORDER BY strSubCategoryCode ";      		            
		        
		            JSONArray subCatCode=new JSONArray();
		            JSONArray arrjObjSubCatCode=new JSONArray();
		            JSONArray arrjObjSubCatName=new JSONArray();
		            
		            arrjObjSubCatCode.put("Sr. No.");
		            arrjObjSubCatCode.put("Name Of Permit Holder Purchaser");
		            arrjObjSubCatCode.put("Permit Number");
		            arrjObjSubCatCode.put("Date Of Expiry");
		            arrjObjSubCatCode.put("Where Granted");
		            
		            arrjObjSubCatName.put("Sr. No.");
		            arrjObjSubCatName.put("Name Of Permit Holder Purchaser");
		            arrjObjSubCatName.put("Permit Number");
		            arrjObjSubCatName.put("Date Of Expiry");
		            arrjObjSubCatName.put("Where Granted");
		            
		            ResultSet rsSubCategory = st.executeQuery(sql_SubCategoryList);
		            
		            while (rsSubCategory.next()) 
		            {
		            	subCatCode.put(rsSubCategory.getString(1));
		            	arrjObjSubCatCode.put(rsSubCategory.getString(1));
		            	arrjObjSubCatName.put(rsSubCategory.getString(2));
		            }
		            rsSubCategory.close();
//		            jObjFLR6Data.put("SubCategoryCode", arrjObjSubCatCode);
		            jObjFLR6Data.put("SubCategoryName", arrjObjSubCatName);
		            
		            
		            JSONArray arrjObjSizeCode=new JSONArray();
		            JSONArray arrjObjSizeName=new JSONArray();
		            
		            
		            arrjObjSizeCode.put(" ");
		            arrjObjSizeCode.put(" ");
		            arrjObjSizeCode.put(" ");
		            arrjObjSizeCode.put(" ");
		            arrjObjSizeCode.put(" ");
		            
		            arrjObjSizeName.put(" ");
		            arrjObjSizeName.put(" ");
		            arrjObjSizeName.put(" ");
		            arrjObjSizeName.put(" ");
		            arrjObjSizeName.put(" ");
		            
		           for(int i=0;i<subCatCode.length();i++){
		        	   
		        		String sql_sizeDataList="select DISTINCT c.strSizeCode,c.strSizeName,d.strSubCategoryName,d.intAvailableSizes "
		        				+ " from tblexcisesale a,tblbrandmaster b,tblsizemaster c,tblsubcategorymaster d "
		        				+ " where a.strItemCode=b.strBrandCode and b.strSizeCode=c.strSizeCode "
		        				+ " and b.strSubCategoryCode=d.strSubCategoryCode "
		        				+ " and a.strClientCode='"+clientCode+"' and b.strClientCode='"+tempBrandClientCode+"' and "
		        				+ " c.strClientCode='"+tempSizeClientCode+"' and b.strSubCategoryCode='"+subCatCode.get(i)+"' "
		        				+ " and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' "
		        				+ " ORDER BY c.intQty desc ";
		            
			           Integer maxSizeLength=0;
			           ResultSet rsSizes = st.executeQuery(sql_sizeDataList);
			           int j=0;
			           
			           if(rsSizes.next()){
			        	   
			        	   arrjObjSizeCode.put(subCatCode.getString(i)+"."+rsSizes.getString(1));
			        	   arrjObjSizeName.put(rsSizes.getString(2));
			        	   maxSizeLength=Integer.parseInt(rsSizes.getString(4));
			        	   j++;
			        	   
				            while (rsSizes.next()) 
				            {
								if(j < maxSizeLength){
					            	arrjObjSizeCode.put(subCatCode.getString(i)+"."+rsSizes.getString(1));
					            	arrjObjSizeName.put(rsSizes.getString(2));
								}
				            	j++;
				            }
			           
							while(j < maxSizeLength){
								arrjObjSizeCode.put(" ");
								arrjObjSizeName.put(" ");
								j++;
							}
							
			           }else{
			        	   
			        	   String sql_sizeList="select DISTINCT a.strSubCategoryName,a.intAvailableSizes "
									+ " from tblsubcategorymaster a where a.strSubCategoryCode='"+subCatCode.get(i)+"' ";
			        		 ResultSet rsSizes1 = st.executeQuery(sql_sizeList);
			        		
			        		if(rsSizes1.next()){
								maxSizeLength=Integer.parseInt(rsSizes1.getString(2).toString());
							}
							
							for(int p=0;p<maxSizeLength;p++){
								arrjObjSizeCode.put(" ");
								arrjObjSizeName.put(" ");
							}
			           }
							
		           }
		           
		           jObjFLR6Data.put("SizeName", arrjObjSizeName);
		           
		            
		      // Create JSON Data For Permit Data    
		           JSONArray arrjObjPermit = new JSONArray();
		           if(arrjObjSizeCode.length()>0){
		        	   arrjObjPermit = funGetPermitData(arrjObjSizeCode, fromDate, toDate, clientCode, inPeg, tempBrandClientCode, tempSizeClientCode);
		           }
		            if(arrjObjPermit.length()>0){
		            	
		            	jObjFLR6Data.put("PermitRows", arrjObjPermit);
		            	
		            	//Create JSON Data For Total   
		            	JSONArray totalRow = funCaluculateTotal(arrjObjPermit,inPeg);
		            	jObjFLR6Data.put("TotalQty", totalRow);
		            	
		            	//Create JSON Data For Summery Data  
		            	JSONArray arrObjSummeryList= funGetFLR6SummeryList(arrjObjSizeCode,fromDate,toDate,clientCode,inPeg,tempBrandClientCode,tempSizeClientCode); 
		            	jObjFLR6Data.put("SummeryData", arrObjSummeryList);
		            }
		           
		            st.close();
		            cmsCon.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        finally
		        {
		        	return jObjFLR6Data.toString();
		        }
		    }
			
			public JSONArray funCaluculateTotal(JSONArray rowData,String inPeg){
				JSONArray totalRow= new JSONArray();
				try{
						LinkedHashMap<String,String> hs = new LinkedHashMap<String,String>();
						hs.put("col0", "`");
						hs.put("col1", " ");
						hs.put("col2", " ");
						hs.put("col3", " ");
						hs.put("col4", " ");
						for(int i=0;i<rowData.length();i++){
							String key="col";
							JSONArray ls=(JSONArray) rowData.get(i);
							for(int j=5;j<ls.length();j++){
								if(hs.containsKey(key+j)){
									if(inPeg.equalsIgnoreCase("Peg")){
										String val="0";
										if(ls.get(j).toString().trim().length()>0){
											val=ls.get(j).toString().trim();
										}else{
											val="0";
										}
										Integer tempVal=Integer.parseInt(hs.get(key+j));
										hs.put(key+j,(tempVal+Integer.parseInt(val))+"");
									}else{
										String val="0";
										if(ls.get(j).toString().trim().length()>0){
											val=ls.get(j).toString().trim();
										}else{
											val="0";
										}
										Integer tempVal=Integer.parseInt(hs.get(key+j));
										hs.put(key+j,(tempVal+Integer.parseInt(val))+"");
									}
									
								}else{
									String val="0";
									if(ls.get(j).toString().trim().length()>0){
										val=ls.get(j).toString().trim();
									}else{
										val="0";
									}
									hs.put(key+j,val);
								}
							}
						}
						for(int i=0;i<hs.size();i++){
							if(hs.get("col"+i).toString().trim().length()>0){
								if(hs.get("col"+i).toString().trim().equalsIgnoreCase("0")){
									totalRow.put(" ");
								}else{
									totalRow.put(hs.get("col"+i).toString());
								}
							}else{
								totalRow.put(" ");
							}
						}
			}catch(Exception e){
				e.printStackTrace();
			}
			return totalRow;
		}
			
	public JSONArray funGetPermitData(JSONArray arrjObjSizeCode,String fromDate,String toDate,
			String clientCode,String inPeg,String tempBrandClientCode,String tempSizeClientCode ){
		
		 JSONArray arrjObjPermit=new JSONArray();
		try{
				clsDatabaseConnection objDb1=new clsDatabaseConnection();
				Connection cmsCon1=objDb1.funOpenExciseCon("mysql","master");
			 	Statement st = cmsCon1.createStatement();
			
			 	String sql_salesDataList="select DISTINCT c.strSubCategoryCode,a.intBillNo,b.strPermitName,b.strPermitNo, "
		   				+ " b.dtePermitExp, b.StrPermitPlace,a.intTotalPeg,a.strItemCode,d.intQty as size,c.intPegSize,d.strSizeCode "
		   				+ " from tblexcisesale a, tblpermitmaster b, tblbrandmaster c,tblsizemaster d "
		   				+ "	where a.strPermitCode=b.strPermitCode and a.strItemCode=c.strBrandCode and c.strSizeCode=d.strSizeCode "
		   				+ " and a.strClientCode='"+clientCode+"' and c.strClientCode='"+tempBrandClientCode+"' and d.strClientCode='"+tempSizeClientCode+"' "
		   				+ " and date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' and a.strItemCode=c.strBrandCode ORDER BY a.intBillNo";
		        
		            ResultSet rsPermit = st.executeQuery(sql_salesDataList);
		            while (rsPermit.next()) 
		            {
		            	JSONArray jArrPermit=new JSONArray();
		            	String subCategoryCode=rsPermit.getString(1);
		    			String sizeCode=rsPermit.getString(11);
		    			Integer pegSize=Integer.parseInt(rsPermit.getString(10));
		    			Integer brandSize=Integer.parseInt(rsPermit.getString(9));
		    			if(pegSize<=0){
		    				pegSize=brandSize;
		    			}
		    			String CatSizeCode=subCategoryCode+"."+sizeCode;
		    			
		    			jArrPermit.put(rsPermit.getString(2));
		    			jArrPermit.put(rsPermit.getString(3));
		    			jArrPermit.put(rsPermit.getString(4));
		    			String permitExpDate=rsPermit.getString(5);
		    			permitExpDate=permitExpDate.split("-")[2]+"-"+permitExpDate.split("-")[1]+"-"+permitExpDate.split("-")[0];
		    			jArrPermit.put(permitExpDate);
		    			jArrPermit.put(rsPermit.getString(6));
		    			
		    			Long saleQtyInPeg= Long.parseLong(rsPermit.getString(7));
		    			
		    			for(int j=5;j<arrjObjSizeCode.length();j++){
		    				try{
		    					if(arrjObjSizeCode.get(j) !=null){
		    						if(arrjObjSizeCode.get(j).toString().equalsIgnoreCase(CatSizeCode)){
		    							if(inPeg.equalsIgnoreCase("Peg")){
		    								jArrPermit.put(""+saleQtyInPeg);
		    							}else{
	    									Long saleQtyInML = saleQtyInPeg*pegSize;
	    									jArrPermit.put(""+saleQtyInML);
		    							}
		    					}else{
		    						jArrPermit.put(" ");
		    					}
		    				}else{
		    					jArrPermit.put(" ");
		    				}
		    				}catch(Exception e){
		    					jArrPermit.put(" ");
		    				}
		    			}
		            	arrjObjPermit.put(jArrPermit);			            
		            }		            
		            rsPermit.close();
		            st.close();
		            cmsCon1.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return arrjObjPermit;
	}
			
	public JSONArray funGetFLR6SummeryList(JSONArray sizeCatCode,String fromDate,String toDate,
						String clientCode,String inPeg,String tempBrandClientCode,String tempSizeClientCode )
	{
		clsDatabaseConnection objDb2=new clsDatabaseConnection();
        Connection cmsCon2=null;
        Statement st=null;
        JSONArray jObjFLR6SummeryData=new JSONArray();
        
        try {
        	cmsCon2=objDb2.funOpenExciseCon("mysql","master");
            st = cmsCon2.createStatement();

      // Create JSON Data For SubCategory and their sizes
            
            String summeryData_sql="select DISTINCT a.strSubCategoryName,d.strSizeName,sum(b.intTotalPeg) as sale,"
    				+ " d.intQty as size,c.intPegSize from tblsubcategorymaster a, "
    				+ " tblexcisesale b,tblbrandmaster c,tblsizemaster d "
    				+ " where b.strItemCode=c.strBrandCode and c.strSubCategoryCode=a.strSubCategoryCode "
    				+ " and b.strClientCode='"+clientCode+"'  and c.strClientCode='"+tempBrandClientCode+"'"
    				+ "  and d.strClientCode='"+tempSizeClientCode+"' and date(b.dteBillDate) "
    				+ " between '"+fromDate+"' and '"+toDate+"' "
    				+ " and c.strSizeCode=d.strSizeCode GROUP By d.strSizeName,a.strSubCategoryCode "
    				+ " order by a.strSubCategoryCode, d.intQty desc ";	
            
            ResultSet rsSummery = st.executeQuery(summeryData_sql);
            while (rsSummery.next()) 
            {
            	JSONArray arrSummery=new JSONArray();
            	arrSummery.put("`");
            	arrSummery.put(rsSummery.getString(1));
            	arrSummery.put(rsSummery.getString(2));
            	
            	Integer size = Integer.parseInt(rsSummery.getString(4));
    			Integer pegSize = Integer.parseInt(rsSummery.getString(5));
    			if(pegSize<=0){
    				pegSize=size;
    			}
    			Long saleQtyInPeg= Long.parseLong(rsSummery.getString(3));
    				
    			if(inPeg.equalsIgnoreCase("Peg")){
    				
    				Integer pegMadeInBrand=(int) Math.floor(size/pegSize);
					if(pegMadeInBrand==0){
						pegMadeInBrand=1;
					}
					if (saleQtyInPeg >= pegMadeInBrand) {
						Long bts = saleQtyInPeg/pegMadeInBrand;
						Long pegs = saleQtyInPeg%pegMadeInBrand;
						String decQty = "0";
						if (pegs.toString().length() > 1) {
							decQty = "" + pegs;
						} else {
							decQty = "0" + pegs;
						}
						String total = bts + "." + decQty;
    					arrSummery.put(total);
    				} else {
    					arrSummery.put(0 + "." + saleQtyInPeg);
    				}
    			}else{
    				Long saleQty = saleQtyInPeg*pegSize;
    				if (saleQty >= size) {
    					Double sale = Double.parseDouble(saleQty.toString()) / size;
    					String[] strSaleArr = String.valueOf(sale).split("\\.");
    					Integer bts = Integer.parseInt(strSaleArr[0].toString());
    					Long mlQty = Math.round(Double.parseDouble("0."+ strSaleArr[1].toString())* size);
    					String total = bts + "." + mlQty;
    					arrSummery.put(total);
    				} else {
    					arrSummery.put(0 + "." + saleQty);
    				}
    			}
    			int listSize=arrSummery.length();
    			if(listSize<sizeCatCode.length()){
    				for(int j=0;j<(sizeCatCode.length()-listSize);j++){
    					arrSummery.put(" ");
    				}
    			}
    			jObjFLR6SummeryData.put(arrSummery);
    		}
        	rsSummery.close();
           		            
            st.close();
            cmsCon2.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        	return jObjFLR6SummeryData;
    }
	
	
	

	@GET
	@Path("/funGetBrandMaster")
	@Consumes(MediaType.APPLICATION_JSON)
	public String funGetBrandData(@QueryParam("ClientCode") String clientCode)
	{
		  return funGetBrandDetails(clientCode);
	}
	
	@SuppressWarnings("finally")
	private String funGetBrandDetails(String clientCode)
	{
		JSONObject jObjBrand=new JSONObject();
		String res="";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
       
		try
		{
			cmsCon=objDb.funOpenExciseCon("mysql","master");
            Statement st = cmsCon.createStatement();
			String sql= "select strBrandCode,strBrandName from tblbrandmaster " ;
				
			if(!clientCode.equals("All"))
					{
						sql+="where strClientCode='"+clientCode+"' ";
					}
			
				
			System.out.println(sql);
			
			JSONArray arrObjBrand=new JSONArray();
	        ResultSet rsBrand=st.executeQuery(sql);
	        while(rsBrand.next())
	        {
	        	JSONObject objBrand=new JSONObject();
	        	objBrand.put("BrandCode", rsBrand.getString(1));
	        	objBrand.put("BrandName", rsBrand.getString(2));
	       
	        	arrObjBrand.put(objBrand);
	        }
	        rsBrand.close();
	        st.close();
	        cmsCon.close();
            
	        jObjBrand.put("BrandDtls", arrObjBrand);
    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
        {
            return jObjBrand.toString();
        }
	}

	
	
	
	@GET
	@Path("/funGetLicenceMaster")
	@Consumes(MediaType.APPLICATION_JSON)
	public String funGetLicenceData(@QueryParam("ClientCode") String clientCode)
	{
		  return funGetLicenceDetails(clientCode);
	}
	
	@SuppressWarnings("finally")
	private String funGetLicenceDetails(String clientCode)
	{
		JSONObject jObjLicence=new JSONObject();
		String res="";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
       
		try
		{
			cmsCon=objDb.funOpenExciseCon("mysql","master");
            Statement st = cmsCon.createStatement();
			String sql= " select strLicenceCode,strLicenceName from tbllicencemaster " 
			         + "  where strClientCode='"+clientCode+"' ";
					
			
				
			System.out.println(sql);
			
			JSONArray arrObjLicence=new JSONArray();
	        ResultSet rsLicence=st.executeQuery(sql);
	        while(rsLicence.next())
	        {
	        	JSONObject objLicence=new JSONObject();
	        	objLicence.put("LicenceCode", rsLicence.getString(1));
	        	objLicence.put("LicenceName", rsLicence.getString(2));
	       
	        	arrObjLicence.put(objLicence);
	        }
	        rsLicence.close();
	        st.close();
	        cmsCon.close();
            
	        jObjLicence.put("LicenceDtls", arrObjLicence);
    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
        {
            return jObjLicence.toString();
        }
	}

	
public JSONObject funGetLicenceDtlforPOS(String searchItemName,String clientCode)
	{
		JSONObject jObjLicence=new JSONObject();
		String res="";
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
       
		try
		{
			cmsCon=objDb.funOpenExciseCon("mysql","master");
            Statement st = cmsCon.createStatement();
			String sql= " select strLicenceCode,strLicenceName from tbllicencemaster " 
			         + "  where strClientCode='"+clientCode+"' ";
					
			
				
			System.out.println(sql);
			
			JSONArray arrObjLicence=new JSONArray();
	        ResultSet rsLicence=st.executeQuery(sql);
	        while(rsLicence.next())
	        {
	        	JSONArray objLicence=new JSONArray();
	        	objLicence.put(rsLicence.getString(1));
	        	objLicence.put(rsLicence.getString(2));
	       
	        	arrObjLicence.put(objLicence);
	        }
	        rsLicence.close();
	        st.close();
	        cmsCon.close();
            
	        jObjLicence.put(searchItemName, arrObjLicence);
    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
        {
            return jObjLicence;
        }
	}
	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GET 
	@Path("/funGetFLR3ADataList")
	@Produces(MediaType.APPLICATION_JSON)
	public String funGetFLR3ADataList(@QueryParam("FromDate") String fromDate,@QueryParam("ToDate") String toDate
			,@QueryParam("ClientCode") String clientCode,@QueryParam("inPeg") String inPeg){
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObjFLR3AData=new JSONObject();
        String tempSizeClientCode=clientCode;
        String tempBrandClientCode=clientCode;
        final Integer totalSizeLength=9;	
        
        try {
        	cmsCon=objDb.funOpenExciseCon("mysql","master");
            st = cmsCon.createStatement();
            
            String Licence_sql="select a.strLicenceNo,a.strLicenceName,CONCAT(a.strAddress1,',',a.strAddress2,"
    				+ "',',a.strAddress3,',',b.strCityName,CONCAT('-',a.strPINCode)) as address "
    				+ "from tbllicencemaster a ,tblcitymaster b "
    				+ "where a.strCity=b.strCityCode and a.strClientCode='"+clientCode+"'";
            ResultSet rsLicenceData = st.executeQuery(Licence_sql);
            while (rsLicenceData.next()) 
            {
            	jObjFLR3AData.put("LicenceNo", rsLicenceData.getString(1));
            	jObjFLR3AData.put("CompanyName", rsLicenceData.getString(2));
            	jObjFLR3AData.put("Address", rsLicenceData.getString(3));
            }
            
            String Global_Masters="select a.strSubCategory,a.strBrandMaster,a.strSizeMaster from tblexcisepropertymaster a "
					+ "	where strClientCode='"+clientCode+"' ";
			ResultSet rsGlobalData = st.executeQuery(Global_Masters);
			while (rsGlobalData.next()) 
			{
				if(rsGlobalData.getString(2).equalsIgnoreCase("All")){
					tempBrandClientCode="All";
				}
				if(rsGlobalData.getString(3).equalsIgnoreCase("All")){
					tempSizeClientCode="All";
				}
			}
            
            LinkedList subCateList= new LinkedList();
            LinkedList subCateName= new LinkedList();
            String sql_SubCategoryList="select strSubCategoryCode, strSubCategoryName from tblsubcategorymaster "
      				+ "  ORDER BY strSubCategoryCode";
            ResultSet rsCategoryData = st.executeQuery(sql_SubCategoryList);
            while (rsCategoryData.next()) 
            {
            	subCateList.add(rsCategoryData.getString(1));
            	subCateName.add( rsCategoryData.getString(2));
            }
            
            jObjFLR3AData.put("SubCategoryCode", subCateList);
            jObjFLR3AData.put("SubCategoryName", subCateName);
            
            st.close();
            cmsCon.close();
            
           JSONArray responseData= new JSONArray();
            
            for(int i=0;i<subCateList.size();i++){
            	JSONObject data = funExciseRowData(subCateList.get(i).toString(),subCateName.get(i).toString(),
            			fromDate,toDate,clientCode,inPeg,tempBrandClientCode,tempSizeClientCode,totalSizeLength);
            	responseData.put(data);
            }
            jObjFLR3AData.put("Data", responseData);
        }catch(Exception e){
        	e.printStackTrace();
        }
		
		return jObjFLR3AData.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject funExciseRowData(String subCatCode,String subCatName, String fromDate,String toDate,
			String clientCode,String inPeg,String tempBrandClientCode,String  tempSizeClientCode,Integer totalSizeLength ){
		
		boolean sizeIsGreater=false;
		String lastSizeCode="";
		LinkedList globalSizeList;
		String isDecimal="decimal";
		
		JSONObject finalresponse = new JSONObject();
		
		try{
			clsDatabaseConnection objDb1=new clsDatabaseConnection();
	        Connection cmsCon1=null;
	        Statement st=null;
			cmsCon1=objDb1.funOpenExciseCon("mysql","master");
	        st = cmsCon1.createStatement();
	        
	        LinkedList resSizeCodeRow = new LinkedList();
	        LinkedList resSizeQtyNameRow = new LinkedList();
	        LinkedList resSizeQtyRow = new LinkedList();
	        
	        LinkedList sizeCodeArr = new LinkedList();
	        LinkedList sizeNameArr = new LinkedList();
	        LinkedList sizeQtyArr = new LinkedList();
            
			resSizeCodeRow.add(" ");
			resSizeCodeRow.add(" ");
			resSizeCodeRow.add(" ");
    		
			resSizeQtyNameRow.add("SizeCode");
			resSizeQtyNameRow.add(subCatName);
			resSizeQtyNameRow.add("T.P. NO.");
    		
			resSizeQtyRow.add(" ");
			resSizeQtyRow.add(" ");
			resSizeQtyRow.add(" ");
		
			String sql_SizeList="select DISTINCT a.strSizeCode,a.strSizeName,a.intQty,c.strIsDecimal "
					+ " from tblsizemaster a,tblbrandmaster b,tblsubcategorymaster c  "
					+ " where a.strSizeCode=b.strSizeCode and b.strSubCategoryCode='"+subCatCode+"' "
					+ " and b.strSubCategoryCode=c.strSubCategoryCode and a.strClientCode='"+tempSizeClientCode+"' "
					+ " and b.strClientCode='"+tempBrandClientCode+"' ORDER BY intQty DESC";
			
	        
	        ResultSet rsSizeData = st.executeQuery(sql_SizeList);
	        int x=0;
            while (rsSizeData.next()) 
            {
            	sizeCodeArr.add(rsSizeData.getString(1));
            	if(x<totalSizeLength){
            		sizeNameArr.add( rsSizeData.getString(2));
            		sizeQtyArr.add( rsSizeData.getString(3));
            		isDecimal=rsSizeData.getString(4);
            	}
            	x++;
            	
            }
            
            for(int p=0;p<4;p++){
    			resSizeCodeRow.addAll(sizeCodeArr);
    			resSizeQtyNameRow.addAll(sizeNameArr);
    			resSizeQtyRow.addAll(sizeQtyArr);
    			
    			for(int cnt=x;cnt<totalSizeLength;cnt++){
    				resSizeCodeRow.add(" ");
    				resSizeQtyNameRow.add(" ");
    				resSizeQtyRow.add(" ");
    			}
    		}
    		if(sizeCodeArr.size()>=totalSizeLength){
    			sizeIsGreater=true;
    			lastSizeCode="";
    			lastSizeCode=sizeCodeArr.get(8).toString();
    			globalSizeList= new LinkedList();
    		}else{
    			sizeIsGreater=false;
    			globalSizeList= new LinkedList();
    		}
	        
    		for(int i=0;i<sizeCodeArr.size();i++){
    			globalSizeList.add(sizeCodeArr.get(i).toString());
    		}
    		
    		
    		LinkedList data=funExciseStkManupulation(subCatCode, subCatName, fromDate, toDate, clientCode, 
    				inPeg, tempBrandClientCode, tempSizeClientCode, totalSizeLength, sizeIsGreater, globalSizeList,lastSizeCode);
    		
    		finalresponse.put("SubCatHeader",funListTOArray(resSizeQtyNameRow));
    		
    		JSONArray rowData= new JSONArray();
    		if(data.size()>0){
    			rowData= funGenrateFullRow(sizeCodeArr,data,totalSizeLength,isDecimal);
    			finalresponse.put("RowData",rowData);
    			
    			LinkedList lastRowList=functionTotalStk(sizeCodeArr,sizeQtyArr,data,inPeg,
    					subCatCode, subCatName, totalSizeLength);
    			
    			JSONArray lastRow= new JSONArray();
    			if(lastRowList.size()>0){
    				lastRow=funIsDecimalCheck(lastRowList,isDecimal);
    			}
    			finalresponse.put("TotalQty",lastRow);
    		}
    		
    		st.close();
            cmsCon1.close();
    		
		}catch(Exception e){
			e.printStackTrace();
		}
		return finalresponse;
	}
	
	@SuppressWarnings("rawtypes")
	public JSONArray funListTOArray(LinkedList data){
		JSONArray resData= new JSONArray();
		for(int i=0;i<data.size();i++){
			resData.put(data.get(i));
		}
		return resData;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedList funExciseStkManupulation(String subCatCode,String subCatName,String fromDate,String toDate,
			String clientCode,String inPeg,String tempBrandClientCode,String tempSizeClientCode,
			Integer totalSizeLength,boolean sizeIsGreater,LinkedList globalSizeList,String lastSizeCode){
		
		LinkedList responsebrand= new LinkedList();
		try{
			clsDatabaseConnection objDb2=new clsDatabaseConnection();
	        Connection cmsCon2=null;
	        Statement st=null,st1=null;
			cmsCon2=objDb2.funOpenExciseCon("mysql","master");
	        st = cmsCon2.createStatement();
	        st1 = cmsCon2.createStatement();
	        
	        
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			if(sdf.parse(sysDate).compareTo(sdf.parse(fromDate))>=0){
				
				 String sql_BrandList="select a.strBrandCode,a.strSizeCode,a.strShortName,b.intQty,a.intPegSize "
							+ " from tblbrandmaster a, tblsizemaster b  "
							+ " where a.strBrandCode NOT IN(SELECT distinct strParentCode FROM tblexciserecipermasterhd) "
							+ " and a.strSizeCode=b.strSizeCode and  a.strSubCategoryCode='"+subCatCode+"' "
			  				+ " and  a.strClientCode='"+tempBrandClientCode+"' and b.strClientCode='"+tempSizeClientCode+"'  ORDER BY a.strShortName ";
			        
			        ResultSet rsBrandData = st.executeQuery(sql_BrandList);
		            while (rsBrandData.next()) 
		            {
		            	LinkedList ls= new LinkedList();
						ls.add(rsBrandData.getString(1));
						ls.add(rsBrandData.getString(2));
						ls.add(rsBrandData.getString(3));
						ls.add(rsBrandData.getString(4));
						ls.add(rsBrandData.getString(5));
						
		            	String sql_OpData="select c.intOpBtls,c.intOpPeg,c.intOpML from tblbrandmaster a,tblbrandopeningmaster c "
								+ " where a.strBrandCode='"+ls.get(0)+"' and a.strBrandCode=c.strBrandCode  "
								+ " and  a.strClientCode='"+tempBrandClientCode+"' and  c.strClientCode='"+clientCode+"' ";
		            	ResultSet rsOpData=st1.executeQuery(sql_OpData);
		            	Integer intOpBtls= 0;
						Integer intOpPeg=0;
						Integer intOpML=0;
						while(rsOpData.next()){
							intOpBtls=Integer.parseInt(rsOpData.getString(1));
							intOpPeg=Integer.parseInt(rsOpData.getString(2));
							intOpML=Integer.parseInt(rsOpData.getString(3));
						}
						
						ls.add(intOpBtls);
						ls.add(intOpPeg);
						ls.add(intOpML);
		            	
						LinkedList brandData=funStockList(ls,fromDate,toDate,clientCode,sizeIsGreater,globalSizeList,totalSizeLength,lastSizeCode,inPeg);
						if(Double.parseDouble(brandData.get(3).toString()) >0 || Double.parseDouble(brandData.get(4).toString()) >0){
							responsebrand.add(brandData);
						}
		            	
		            }
		            rsBrandData.close();
			}else{
				
				String tempFromDate=sysDate;
				Date convertedCurrentDate = sdf.parse(fromDate);
			    Date oneDayBefore = new Date(convertedCurrentDate.getTime() - 1);
			    DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
			    String tempToDate = outputFormatter.format(oneDayBefore);
			    
			    String sql_BrandList="select a.strBrandCode,a.strSizeCode,a.strShortName,b.intQty,a.intPegSize "
						+ " from tblbrandmaster a, tblsizemaster b  "
						+ " where a.strBrandCode NOT IN(SELECT distinct strParentCode FROM tblexciserecipermasterhd) "
						+ " and a.strSizeCode=b.strSizeCode and  a.strSubCategoryCode='"+subCatCode+"' "
		  				+ " and  a.strClientCode='"+tempBrandClientCode+"' and b.strClientCode='"+tempSizeClientCode+"'  ORDER BY a.strShortName ";
		        
		        ResultSet rsBrandData = st.executeQuery(sql_BrandList);
			
			    while (rsBrandData.next()) 
	            {
	            	LinkedList ls= new LinkedList();
					ls.add(rsBrandData.getString(1));
					ls.add(rsBrandData.getString(2));
					ls.add(rsBrandData.getString(3));
					ls.add(rsBrandData.getString(4));
					ls.add(rsBrandData.getString(5));
					
	            	String sql_OpData="select c.intOpBtls,c.intOpPeg,c.intOpML from tblbrandmaster a,tblbrandopeningmaster c "
							+ " where a.strBrandCode='"+ls.get(0)+"' and a.strBrandCode=c.strBrandCode  "
							+ " and  a.strClientCode='"+tempBrandClientCode+"' and  c.strClientCode='"+clientCode+"' ";
	            	ResultSet rsOpData=st1.executeQuery(sql_OpData);
	            	Integer intOpBtls= 0;
					Integer intOpPeg=0;
					Integer intOpML=0;
					while(rsOpData.next()){
						intOpBtls=Integer.parseInt(rsOpData.getString(1));
						intOpPeg=Integer.parseInt(rsOpData.getString(2));
						intOpML=Integer.parseInt(rsOpData.getString(3));
					}
					ls.add(intOpBtls);
					ls.add(intOpPeg);
					ls.add(intOpML);
					rsOpData.close();
					
					Integer sizeData=Integer.parseInt(ls.get(3).toString());
					Integer pegSize=Integer.parseInt(ls.get(4).toString());
					if(pegSize<=0){
						pegSize=sizeData;
					}
					LinkedList tempBrandData=funStockList(ls,tempFromDate,tempToDate,clientCode,sizeIsGreater,globalSizeList,totalSizeLength,lastSizeCode,inPeg);
					
					LinkedList brandData= new LinkedList();	
					if(sizeIsGreater){
						if(globalSizeList.indexOf(ls.get(1).toString())>=totalSizeLength){
							brandData.add(lastSizeCode);
						}else{
							brandData.add(ls.get(1).toString());
						}
						
					}else{
						brandData.add(ls.get(1).toString());
					}
					brandData.add(ls.get(2).toString());
					LinkedHashMap tpData=funTpStock(ls.get(0).toString(),fromDate,toDate,clientCode);
					String tpNoString=" ";
					Long tpQty= new Long("0");
					LinkedList<String> tpNoList = new LinkedList<String>();
					tpNoList.addAll(tpData.keySet());
					Iterator it= tpNoList.iterator();
					while(it.hasNext()){
						String tpNo= it.next().toString();
						tpNoString+=","+tpNo;
						tpQty=tpQty+Long.parseLong(tpData.get(tpNo).toString());
					}
					if(tpNoString.length()>2){
						brandData.add(tpNoString.substring(2));
					}else{
						brandData.add(tpNoString);
					}
					
					BigDecimal OpeningQty = new BigDecimal(tempBrandData.get(6).toString().trim());
					brandData.add(OpeningQty+" ");
					
					String[] strOpeningArr=String.valueOf(OpeningQty).split("\\.");
					Long opBtlQty=Long.parseLong(strOpeningArr[0].toString())*sizeData;
					Long opDecimalQty= new Long(0);
					Long openingStk=new Long(0);
					if (inPeg.equalsIgnoreCase("Peg")) {
						if(strOpeningArr.length>1){
							opDecimalQty = new Long(strOpeningArr[1].toString());
							opDecimalQty=opDecimalQty*pegSize;
						}
						openingStk=opBtlQty+opDecimalQty;						
					} else {
						if(strOpeningArr.length>1){
							opDecimalQty = new Long(strOpeningArr[1]);
						}
						openingStk=opBtlQty+opDecimalQty;
					}
										
					Long totalTpInML=tpQty*sizeData;
					
					if(inPeg.equalsIgnoreCase("Peg")){
						brandData.add(tpQty+" ");
					}else{
						brandData.add(tpQty+" ");
					}
						
					Long saleStkMl= funSalesStk(ls.get(0).toString(),sizeData,fromDate,toDate,clientCode);
					Double saleInBtlNMl= (saleStkMl/Double.parseDouble(sizeData+" "));
					String[] strSalesArr=String.valueOf(saleInBtlNMl).split("\\.");
//					Double decimalSalesQty=Double.parseDouble("0."+strSalesArr[1]);
					Integer salesPtToMl= 0;
					if(strSalesArr.length>1){
						Double decimalSalesQty=Double.parseDouble("0."+strSalesArr[1]);
						salesPtToMl= (int) (Math.round(decimalSalesQty* sizeData));
					}
					
					if(inPeg.equalsIgnoreCase("Peg")){
						Integer pegs= salesPtToMl/pegSize;
						String decQty="0";
						if(pegs.toString().length()>1){
							decQty=""+pegs;
						}else{
							decQty="0"+pegs;
						}
						String total = strSalesArr[0].toString()+"."+decQty;
						brandData.add(total+" ");
					}else{
						BigDecimal salesWithML=new BigDecimal(strSalesArr[0]+"."+salesPtToMl);
						brandData.add(salesWithML+" ");
					}
							
					Long closingStk=(openingStk+totalTpInML)-saleStkMl;
					Double closeStkInMl= (closingStk/Double.parseDouble(sizeData+" "));
					String[] strCloseArr=String.valueOf(closeStkInMl).split("\\.");
					Integer closePtToMl=0;
					if(strCloseArr.length>1){
						Double decimalCloseQty=Double.parseDouble("0."+strCloseArr[1]);
						closePtToMl= (int) (Math.round(decimalCloseQty*sizeData));
					}
					if(inPeg.equalsIgnoreCase("Peg")){
						Integer pegs= closePtToMl/pegSize;
						String decQty="0";
						if(pegs.toString().length()>1){
							decQty=""+pegs;
						}else{
							decQty="0"+pegs;
						}
						String total = strCloseArr[0].toString()+"."+decQty;
						brandData.add(total+" ");
					}else{
						BigDecimal closeWithML=new BigDecimal(strCloseArr[0]+"."+closePtToMl);
						brandData.add(closeWithML+" ");
					}
						brandData.add(pegSize.toString());
						
					if(Double.parseDouble(brandData.get(3).toString()) >0 || Double.parseDouble(brandData.get(4).toString()) >0){
						responsebrand.add(brandData);
					}
	            }
			    rsBrandData.close();
			    st.close();
			    st1.close();
	            cmsCon2.close();
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return responsebrand;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedList funStockList(List brandDataList,String fromDate, String toDate,String clientCode,boolean sizeIsGreater,
				LinkedList globalSizeList,Integer totalSizeLength,String lastSizeCode,String inPeg){
			
		LinkedList brandData= new LinkedList();
		try{
			clsDatabaseConnection objDb3=new clsDatabaseConnection();
	        Connection cmsCon3=null;
	        Statement st3=null;
			cmsCon3=objDb3.funOpenExciseCon("mysql","master");
	        st3 = cmsCon3.createStatement();
	        
	        String strBrandCode = brandDataList.get(0).toString();
	        String strSizeCode = brandDataList.get(1).toString();
	        String strShortName = brandDataList.get(2).toString();
	        String intSizeQty = brandDataList.get(3).toString();
	        String intPegSize = brandDataList.get(4).toString();
	        String intOpBtls = brandDataList.get(5).toString();
	        String intOpPeg = brandDataList.get(6).toString();
	        String intOpML = brandDataList.get(7).toString();
	        
	        
	        String sql_RecipeList="select distinct strParentCode FROM tblexciserecipermasterhd a "
				+ "where a.strParentCode='"+strBrandCode+"' and a.strClientCode='"+clientCode+"' ";
	        ResultSet rsrecipeData = st3.executeQuery(sql_RecipeList);
	    	if(rsrecipeData.next()){
	    		return null;
	    	}else{
	    		
				Integer sizeData=Integer.parseInt(intSizeQty);
				Integer pegSize=Integer.parseInt(intPegSize);
	    		
				if(sizeIsGreater){
					if(globalSizeList.indexOf(strSizeCode)>=totalSizeLength){
						brandData.add(lastSizeCode);
					}else{
						brandData.add(strSizeCode);
					}
					
				}else{
					brandData.add(strSizeCode);
				}
				brandData.add(strShortName);
				if(pegSize<=0){
					pegSize=sizeData;
				}
				LinkedHashMap tpData=funTpStock(strBrandCode,fromDate,toDate,clientCode);
				String tpNoString=" ";
				Long tpQty= new Long("0");
				LinkedList<String> tpNoList = new LinkedList<String>();
				tpNoList.addAll(tpData.keySet());
				Iterator it= tpNoList.iterator();
				while(it.hasNext()){
					String tpNo= it.next().toString();
					tpNoString+=","+tpNo;
					tpQty=tpQty+Long.parseLong(tpData.get(tpNo).toString());
				}
				
				if(tpNoString.length()>2){
					brandData.add(tpNoString.substring(2));
				}else{
					brandData.add(tpNoString);
				}
				
				Long bottals = new Long(0);
				Long intpegs = new Long(0);
				Long intML = new Long(0);
				
				if(intOpBtls !=null){
					bottals= Long.parseLong(intOpBtls);
				}else{
					bottals=(long) 0;
				}
				if(intOpPeg !=null){
					intpegs= Long.parseLong(intOpPeg);
				}else{
					intpegs=(long) 0;
				}
				
				if(intOpML !=null){
					intML= Long.parseLong(intOpML);
				}else{
					intML=(long) 0;
				}
				
				Long btsMl= sizeData * bottals;
				Long pegMl= pegSize * intpegs;
				Long openingStk =btsMl+pegMl+intML;
				
				if(inPeg.equalsIgnoreCase("Peg")){
					String decQty="0";
					if(intpegs.toString().length()>1){
						decQty=""+intpegs;
					}else{
						decQty="0"+intpegs;
					}
					String total = bottals+"."+decQty;
					brandData.add(total+" ");
				}else{
					String total = bottals+"."+pegMl;
					brandData.add(total+" ");
				}
				
				Long totalTpInML=tpQty*sizeData;
				if(inPeg.equalsIgnoreCase("Peg")){
					brandData.add(tpQty+" ");
				}else{
					brandData.add(tpQty+" ");
				}
							
							
				Long saleStkMl= funSalesStk(strBrandCode,sizeData,fromDate,toDate,clientCode);
				Double saleInBtlNMl= (saleStkMl/Double.parseDouble(sizeData+""));
				String[] strSalesArr=String.valueOf(saleInBtlNMl).split("\\.");
				Integer salesPtToMl= 0;
				if(strSalesArr.length>1){
					Double decimalSalesQty=Double.parseDouble("0."+strSalesArr[1]);
					salesPtToMl= (int) (Math.round(decimalSalesQty* sizeData));
				}
				
				if(inPeg.equalsIgnoreCase("Peg")){
						Integer pegs= salesPtToMl/pegSize;
						String decQty="0";
						if(pegs.toString().length()>1){
							decQty=""+pegs;
						}else{
							decQty="0"+pegs;
						}
						
						String total = strSalesArr[0].toString()+"."+decQty;
						brandData.add(total+" ");
					}else{
						BigDecimal salesWithML=new BigDecimal(strSalesArr[0]+"."+salesPtToMl);
						brandData.add(salesWithML+" ");
					}
						
					Long closingStk=  (openingStk+totalTpInML)- saleStkMl;					
					Double closeStkInMl= (closingStk/Double.parseDouble(sizeData+""));
					String[] strCloseArr=String.valueOf(closeStkInMl).split("\\.");
					Integer closePtToMl=0;
					if(strCloseArr.length>1){
						Double decimalCloseQty=Double.parseDouble("0."+strCloseArr[1]);
						closePtToMl= (int) (Math.round(decimalCloseQty*sizeData));
					}

					if(inPeg.equalsIgnoreCase("Peg")){
						Integer pegs= closePtToMl/pegSize;
						String decQty="0";
						if(pegs.toString().length()>1){
							decQty=""+pegs;
						}else{
							decQty="0"+pegs;
						}
						String total = strCloseArr[0].toString()+"."+decQty;
						brandData.add(total+" ");
					}else{
						BigDecimal closeWithML=new BigDecimal(strCloseArr[0]+"."+closePtToMl);
						brandData.add(closeWithML+" ");
					}
					brandData.add(intPegSize);
	    	}
	    	rsrecipeData.close();
	    	st3.close();
            cmsCon3.close();
            
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return brandData;
	}
	
	
	public LinkedHashMap<String,String> funTpStock(String brandCode,String fromDate,String toDate,String clientCode){
		
		LinkedHashMap<String,String> result= new LinkedHashMap<String,String>();
		try{
			clsDatabaseConnection objDb4=new clsDatabaseConnection();
	        Connection cmsCon4=null;
	        Statement st4=null;
			cmsCon4=objDb4.funOpenExciseCon("mysql","master");
	        st4 = cmsCon4.createStatement();
	        
	        String sql_TpQty="select a.strTPNo,b.intBottals,a.strTPCode from tbltphd a, tbltpdtl b where b.strTPCode=a.strTPCode and "
				+ "date(a.strTpDate) between '"+fromDate+"' and '"+toDate+"' and b.strBrandCode='"
				+brandCode+"' and b.strClientCode='"+clientCode+"' and a.strClientCode='"+clientCode+"'";
		
	        ResultSet rsTpData = st4.executeQuery(sql_TpQty);
			while(rsTpData.next()){
				result.put(rsTpData.getString(1),rsTpData.getString(2));
			}
			rsTpData.close();
			st4.close();
            cmsCon4.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public Long funSalesStk(String brandCode,Integer brandSize,String fromDate,String toDate,String clientCode){
		
		Long brandSaleDataInPMl= new Long(0);
		try{
			clsDatabaseConnection objDb5=new clsDatabaseConnection();
	        Connection cmsCon5=null;
	        Statement st5=null,st6=null;
			cmsCon5=objDb5.funOpenExciseCon("mysql","master");
	        st5 = cmsCon5.createStatement();
	        st6 = cmsCon5.createStatement();
		
	        String sql_RecipeQty="select a.strParentCode,d.intQty as brandQty,b.dblQty "
				+ "from tblexciserecipermasterhd a, tblexciserecipermasterdtl b,tblbrandmaster c,tblsizemaster d "
				+ "where a.strRecipeCode=b.strRecipeCode and a.strParentCode=c.strBrandCode "
				+ "and c.strSizeCode=d.strSizeCode and b.strBrandCode='"+brandCode+"' and b.strClientCode='"+clientCode+"' "
				+ " and a.strClientCode='"+clientCode+"'";
		
	        ResultSet rsrecipeData = st5.executeQuery(sql_RecipeQty);
	        while(rsrecipeData.next()){
	        		String sql_SalesQty="select ifnull(sum(a.intTotalPeg),'0') as Qty from tblexcisesale a where "
						+ "date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' and "
						+ "a.strItemCode='"+rsrecipeData.getString(1)+"' and a.strClientCode='"+clientCode+"'";
				ResultSet rsSalesData = st6.executeQuery(sql_SalesQty);
				while(rsSalesData.next()){
					Long peg = Long.parseLong(rsSalesData.getString(1));
					Integer qtyInRecipe= (int) Math.floor(brandSize/Double.parseDouble(rsrecipeData.getString(3)));
					Long totalMl=(qtyInRecipe)*(peg);					
					brandSaleDataInPMl += totalMl;
				}
				rsSalesData.close();
			}
	        rsrecipeData.close();
	        
			String sql_SalesQty="select ifnull(sum(a.intQty),'0') as Qty from tblexcisesale a where "
					+ "date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' and "
					+ "a.strItemCode='"+brandCode+"' and a.strClientCode='"+clientCode+"'";
			ResultSet rsSalesData = st5.executeQuery(sql_SalesQty);
			while(rsSalesData.next()){
				st6 = cmsCon5.createStatement();
				String sale=rsSalesData.getString(1);
				if(sale !=null){
					brandSaleDataInPMl+= Long.parseLong((sale));
				}
			}
			rsSalesData.close();
			st5.close();
			st6.close();
            cmsCon5.close();
            
		}catch(Exception e){
			e.printStackTrace();
		}
		return brandSaleDataInPMl;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LinkedList functionTotalStk(LinkedList sizeCodeList,LinkedList sizeQtyList,LinkedList result,String inPeg,
			String subCategory,String subcategoryName,Integer totalSizeLength){
		
		LinkedList totalList= new LinkedList();		
		LinkedHashMap<String,Object> openingList= new LinkedHashMap<String,Object>();
		LinkedHashMap<String,Object> tpList= new LinkedHashMap<String,Object>();
		LinkedHashMap<String,Object> saleList= new LinkedHashMap<String,Object>();
		LinkedHashMap<String,Object> closingList= new LinkedHashMap<String,Object>();
		
		openingList= funTotalCalculate(openingList, (LinkedList<LinkedList<String>>) result, sizeCodeList,3);
		tpList= funTotalCalculate(tpList, (LinkedList<LinkedList<String>>) result, sizeCodeList,4);
		saleList= funTotalCalculate(saleList, (LinkedList<LinkedList<String>>) result, sizeCodeList,5);
		closingList= funTotalCalculate(closingList, (LinkedList<LinkedList<String>>) result, sizeCodeList,6);
		
		totalList.add(" ");
		totalList.add(subcategoryName+" Total");
		totalList.add(" ");
		
		int cnt=sizeCodeList.size();
		if(totalSizeLength<sizeCodeList.size()){
			cnt=totalSizeLength;
		}
		
		for(int i=0;i<cnt;i++){
			if(openingList.containsKey(sizeCodeList.get(i))){
				
				List totallist =(List) (openingList.get(sizeCodeList.get(i)));
				Integer Btls = Integer.parseInt(totallist.get(1).toString());
				Integer decimalQty = Integer.parseInt(totallist.get(2).toString());
				Integer tempPegSize = Integer.parseInt(totallist.get(0).toString());
				Integer size=Integer.parseInt(sizeQtyList.get(i).toString());
				
				if(inPeg.equalsIgnoreCase("Peg")){
					Integer pegMadeInBrand=(int) Math.floor(size/tempPegSize);
					if(pegMadeInBrand==0){
						pegMadeInBrand=1;
					}
					if(pegMadeInBrand<=decimalQty){
						Integer tempBls= (decimalQty/pegMadeInBrand);
						Integer tempPeg=(decimalQty%pegMadeInBrand);
						Btls=Btls + tempBls;
						String decQty="0";
						if(tempPeg.toString().length()==2){
							decQty=""+tempPeg;
						}else if(tempPeg.toString().length()==1){
							decQty="0"+tempPeg;
						}else{
							decQty="00";
						}
						
						String totalStk = Btls+"."+decQty;
						totalList.add(totalStk);
					}else{
						String decPlace="00";
						if(decimalQty.toString().length()>=2){
							decPlace=decimalQty+"";
						}else{
							decPlace="0"+decimalQty;
						}
						String totalStk = Btls+"."+decPlace;
						totalList.add(totalStk);
					}
				
			}else{
				if(decimalQty>=size){
					Integer tempBls= (decimalQty/size);
					Integer tempDec=(decimalQty%size);
					Btls=Btls + tempBls;
					totalList.add(Btls+"."+tempDec+" ");
				}else{
					String totalStk = Btls+"."+decimalQty;
					totalList.add(totalStk);
				}
			}
		} else{
			totalList.add(" ");
		}
	}
		
	if(totalSizeLength >sizeCodeList.size()){
		for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
			totalList.add(" ");
		}
	}
		
		for(int i=0;i<cnt;i++){
			if(tpList.containsKey(sizeCodeList.get(i))){
				
				List totallist =(List) (tpList.get(sizeCodeList.get(i)));
				Integer Btls = Integer.parseInt(totallist.get(1).toString());
				Integer decimalQty = Integer.parseInt(totallist.get(2).toString());
				Integer size=Integer.parseInt(sizeQtyList.get(i).toString());
		
				if(inPeg.equalsIgnoreCase("Peg")){
					if(Btls>0){
						String totalStk = Btls+"";
						totalList.add(totalStk);
					}else{
						totalList.add(" ");
					}
				
			}else{
				if(decimalQty>=size){
					Integer tempBls= (decimalQty/size);
					Integer tempDec=(decimalQty%size);
					Btls=Btls + tempBls;
					totalList.add(Btls+"."+tempDec+" ");
				}else{
					String totalStk = Btls+"."+decimalQty;
					totalList.add(totalStk);
				}
			}
		} else{
			totalList.add(" ");
		}
	}
		
		if(totalSizeLength >sizeCodeList.size()){
			for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
				totalList.add(" ");
			}
		}
		
		for(int i=0;i<cnt;i++){
			if(saleList.containsKey(sizeCodeList.get(i))){
				
				List totallist =(List) (saleList.get(sizeCodeList.get(i)));
				Integer Btls = Integer.parseInt(totallist.get(1).toString());
				Integer decimalQty = Integer.parseInt(totallist.get(2).toString());
				Integer tempPegSize = Integer.parseInt(totallist.get(0).toString());
				Integer size=Integer.parseInt(sizeQtyList.get(i).toString());
			
				if(inPeg.equalsIgnoreCase("Peg")){
					Integer pegMadeInBrand=(int) Math.floor(size/tempPegSize);
					if(pegMadeInBrand==0){
						pegMadeInBrand=1;
					}
					if(pegMadeInBrand<=decimalQty){
						Integer tempBls= (decimalQty/pegMadeInBrand);
						Integer tempPeg=(decimalQty%pegMadeInBrand);
						Btls=Btls + tempBls;
						String decQty="0";
						if(tempPeg.toString().length()==2){
							decQty=""+tempPeg;
						}else if(tempPeg.toString().length()==1){
							decQty="0"+tempPeg;
						}else{
							decQty="00";
						}
						
						String totalStk = Btls+"."+decQty;
						totalList.add(totalStk);
					}else{
						String decPlace="00";
						if(decimalQty.toString().length()>=2){
							decPlace=decimalQty+"";
						}else{
							decPlace="0"+decimalQty;
						}
						String totalStk = Btls+"."+decPlace;
						totalList.add(totalStk);
					}
				
			}else{
				if(decimalQty>=size){
					Integer tempBls= (decimalQty/size);
					Integer tempDec=(decimalQty%size);
					Btls=Btls + tempBls;
					totalList.add(Btls+"."+tempDec+" ");
				}else{
					String totalStk = Btls+"."+decimalQty;
					totalList.add(totalStk);
				}
			}
		} else{
			totalList.add(" ");
		}
	}
		
		if(totalSizeLength >sizeCodeList.size()){
			for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
				totalList.add(" ");
			}
		}
		
		for(int i=0;i<cnt;i++){
			if(closingList.containsKey(sizeCodeList.get(i))){
				
				List totallist =(List) (closingList.get(sizeCodeList.get(i)));
				Integer Btls = Integer.parseInt(totallist.get(1).toString());
				Integer decimalQty = Integer.parseInt(totallist.get(2).toString());
				Integer tempPegSize = Integer.parseInt(totallist.get(0).toString());
				Integer size=Integer.parseInt(sizeQtyList.get(i).toString());
		
				if(inPeg.equalsIgnoreCase("Peg")){
					Integer pegMadeInBrand=(int) Math.floor(size/tempPegSize);
					if(pegMadeInBrand==0){
						pegMadeInBrand=1;
					}
					if(pegMadeInBrand<=decimalQty){
						Integer tempBls= (decimalQty/pegMadeInBrand);
						Integer tempPeg=(decimalQty%pegMadeInBrand);
						Btls=Btls + tempBls;
						String decQty="0";
						if(tempPeg.toString().length()==2){
							decQty=""+tempPeg;
						}else if(tempPeg.toString().length()==1){
							decQty="0"+tempPeg;
						}else{
							decQty="00";
						}
						
						String totalStk = Btls+"."+decQty;
						totalList.add(totalStk);
					}else{
						String decPlace="00";
						if(decimalQty.toString().length()>=2){
							decPlace=decimalQty+"";
						}else{
							decPlace="0"+decimalQty;
						}
						String totalStk = Btls+"."+decPlace;
						totalList.add(totalStk);
					}
				
			}else{
				if(decimalQty>=size){
					Integer tempBls= (decimalQty/size);
					Integer tempDec=(decimalQty%size);
					Btls=Btls + tempBls;
					totalList.add(Btls+"."+tempDec+" ");
				}else{
					String totalStk = Btls+"."+decimalQty;
					totalList.add(totalStk);
				}
			}
		} else{
			totalList.add(" ");
		}
	}
		
		if(totalSizeLength >sizeCodeList.size()){
			for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
				totalList.add(" ");
			}
		}
		return totalList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedHashMap<String,Object> funTotalCalculate(LinkedHashMap<String,Object> dataList,LinkedList<LinkedList<String>> result,LinkedList<String> sizeCodeList,int index ){
		for(int i=0;i<result.size();i++){
			LinkedList ls=result.get(i);
				for(int k=0;k<sizeCodeList.size();k++){
						if(sizeCodeList.get(k).toString().equalsIgnoreCase(ls.get(0).toString())){
							if(dataList.containsKey(sizeCodeList.get(k))){
								
									List tempLs= new LinkedList();
									List temp1= (List) dataList.get(sizeCodeList.get(k));
									Long bts1=Long.parseLong(temp1.get(1).toString());
									Long ml1=Long.parseLong(temp1.get(2).toString());
									
									String temp2[]= String.valueOf(ls.get(index).toString().trim()).split("\\.");
									Long ml2=new Long(0);
									Long bts2=Long.parseLong(temp2[0].toString());
									if(temp2.length>1){
										ml2=Long.parseLong(temp2[1].toString());
									}
									Long totalBts=bts1+bts2;
									Long totalMl=ml1+ml2;
									
									Integer tempPegSize=Integer.parseInt(ls.get(7).toString());
									if(tempPegSize<=0){
										tempPegSize=1;
									}
									
									tempLs.add(tempPegSize);
									tempLs.add(totalBts);
									tempLs.add(totalMl);
									dataList.put(sizeCodeList.get(k),tempLs);
									break;
							}else{
								List tempLs= new LinkedList();
								Integer tempPegSize=Integer.parseInt(ls.get(7).toString());
								if(tempPegSize<=0){
									tempPegSize=1;
								}
								
								tempLs.add(tempPegSize);
								Long ml=new Long(0);
								Long bts=new Long(0);
								
									String temp2[]= String.valueOf(ls.get(index).toString().trim()).split("\\.");
									bts=Long.parseLong(temp2[0].toString());
									if(temp2.length>1){
										ml=Long.parseLong(temp2[1].toString());
									}
									tempLs.add(bts);
									tempLs.add(ml);
									dataList.put(sizeCodeList.get(k),tempLs);
								break;
							}
					}
			}
		}
		return dataList;
	}
	

	@SuppressWarnings({ "rawtypes"})
	public JSONArray funGenrateFullRow(LinkedList sizeCodeList,LinkedList data,Integer totalSizeLength,String isDecimal){
		JSONArray responseData= new JSONArray(); 	
		try{
			for(int p=0;p<data.size();p++){
				LinkedList dataList = (LinkedList) data.get(p);
		
				JSONArray fullRow=new JSONArray();
				fullRow.put(dataList.get(0).toString());
				fullRow.put(dataList.get(1).toString());
				fullRow.put(dataList.get(2).toString());	
		
				int cnt=sizeCodeList.size();
				if(totalSizeLength<sizeCodeList.size()){
					cnt=totalSizeLength;
				}
		
				for(int i=0;i<cnt;i++){
					if(sizeCodeList.get(i).toString().equals((dataList.get(0).toString()))){
						
						if(Double.parseDouble(dataList.get(3).toString()) >0){
							if(isDecimal.equalsIgnoreCase("decimal")){
								fullRow.put(dataList.get(3));
							}else{
								String[] tempArr=dataList.get(3).toString().split("\\.");
								fullRow.put(tempArr[0].toString());
							}
						}else{
							fullRow.put(" ");
						}
						
						}else{
							fullRow.put(" ");
					}
				}
		
				if(totalSizeLength >sizeCodeList.size()){
					for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
						fullRow.put(" ");
					}
				}
		
				for(int i=0;i<cnt;i++){
					if(sizeCodeList.get(i).toString().equals((dataList.get(0).toString()))){
						if(Double.parseDouble(dataList.get(4).toString()) >0){
							if(isDecimal.equalsIgnoreCase("decimal")){
								fullRow.put(dataList.get(4));
							}else{
								String[] tempArr=dataList.get(4).toString().split("\\.");
								fullRow.put(tempArr[0].toString());
							}
						}else{
							fullRow.put(" ");
						}
					}else{
						fullRow.put(" ");
					}
				}
		
				if(totalSizeLength >sizeCodeList.size()){
					for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
						fullRow.put(" ");
					}
				}
		
				for(int i=0;i<cnt;i++){
					if(sizeCodeList.get(i).toString().equals((dataList.get(0).toString()))){
						if(Double.parseDouble(dataList.get(5).toString()) >0){
							if(isDecimal.equalsIgnoreCase("decimal")){
								fullRow.put(dataList.get(5));
							}else{
								String[] tempArr=dataList.get(5).toString().split("\\.");
								fullRow.put(tempArr[0].toString());
							}
						}else{
							fullRow.put(" ");
						}
					}else{
						fullRow.put(" ");
					}
				}
		
				if(totalSizeLength >sizeCodeList.size()){
					for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
						fullRow.put(" ");
					}
				}
		
				for(int i=0;i<cnt;i++){
					if(sizeCodeList.get(i).toString().equals((dataList.get(0).toString()))){
						if(Double.parseDouble(dataList.get(6).toString()) >0){
							if(isDecimal.equalsIgnoreCase("decimal")){
								fullRow.put(dataList.get(6));
							}else{
								String[] tempArr=dataList.get(6).toString().split("\\.");
								fullRow.put(tempArr[0].toString());
							}
						}else{
							fullRow.put(" ");
						}
					}else{
						fullRow.put(" ");
					}
				}
		
				if(totalSizeLength >sizeCodeList.size()){
					for(int i=0;i<(totalSizeLength-sizeCodeList.size());i++){
						fullRow.put(" ");
					}
				}
				responseData.put(fullRow);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return responseData;
	}
	
	@SuppressWarnings("rawtypes")
	public JSONArray funIsDecimalCheck(LinkedList data, String isDecimal) {

		JSONArray returnList = new JSONArray();
		try {
			for (int i = 0; i < data.size(); i++) {
				String value = data.get(i).toString();
				if (value.trim().length() > 0 && (!value.trim().isEmpty())) {
					if (!(value.trim().contentEquals("0.00"))) {
						if (isDecimal.equalsIgnoreCase("decimal")) {
							returnList.put(value);
						} else {
							if ((!(value.trim().isEmpty()))
									&& value.trim().length() > 0) {
								String[] tempArr = value.trim().split("\\.");
								returnList.put(tempArr[0].toString());
							} else {
								returnList.put(value);
							}
						}
					} else {
						returnList.put(" ");
					}
				} else {
					returnList.put(" ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnList;
	}
	
	
	@GET
    @Path("/funGetExciseBrandSearch")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetExciseBrand(@QueryParam("masterName") String masterName, @QueryParam("searchCode") String searchCode, @QueryParam("clientCode") String clientCode)
    {
	
	JSONObject jObjSearchData = new JSONObject();
	
	try
	{
	    jObjSearchData = funGetExciseBrandSearch(masterName, searchCode, clientCode);
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return jObjSearchData;
	
    }
	
	public JSONObject funGetExciseBrandSearch(String masterName,String searchCode,String clientCode)
 	{
 		JSONObject jObjSearchData = new JSONObject();
		JSONArray jArrData = new JSONArray();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st = null;
		String sql="";
		
	try
	{
		
		switch (masterName)
		{
		    case "BrandMasterWeb-Service":
						
		    	webbookCon=objDb.funOpenExciseCon("mysql","master");
		        st = webbookCon.createStatement();	
		    sql = " select a.strBrandCode , a.strBrandName "
		    		+ " from tblbrandmaster a where a.strClientCode = '"+clientCode+"'  " ;	
			
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rs.getString(1));
			    	 jArrtem.put(rs.getString(2));
			    	 jArrData.put(jArrtem);
				}
			    break;
			    
		    case "exciseSupplierWeb-Service":
				
		    	webbookCon=objDb.funOpenExciseCon("mysql","master");
		        st = webbookCon.createStatement();	
		    sql = " select a.strSupplierCode , a.strSupplierName "
		    		+ " from tblsuppliermaster a where a.strClientCode = '"+clientCode+"'  " ;	
			
			    ResultSet rsSupp=st.executeQuery(sql);
			    while(rsSupp.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rsSupp.getString(1));
			    	 jArrtem.put(rsSupp.getString(2));
			    	 jArrData.put(jArrtem);
				}
			    break;    
			    
			    
		}
			jObjSearchData.put(masterName, jArrData);
			
			
	}catch(Exception ex)
	{
		webbookCon.close();
		ex.printStackTrace();
	}finally
	{
		
		return jObjSearchData;
	}
	
 	}
	
	
	@GET
    @Path("/funGetExciseBrandMasterData")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetExciseBrandMasterData(@QueryParam("strBrandCode") String strBrandCode, @QueryParam("clientCode") String clientCode)
    {
		JSONObject jObjData = new JSONObject();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st = null;
		String sql="";
		try
		{
			webbookCon=objDb.funOpenExciseCon("mysql","master");
			st = webbookCon.createStatement();	
		    sql = " select a.strBrandCode , a.strBrandName "
	    		+ " from tblbrandmaster a where  a.strBrandCode = '"+strBrandCode+"' and a.strClientCode = '"+clientCode+"'  " ;	
		
		    ResultSet rs=st.executeQuery(sql);
		    while(rs.next())
			{
		    	jObjData.put("strBrandCode",rs.getString(1));
		    	jObjData.put("strBrandName",rs.getString(2));
			}
		    
		    
		}catch(Exception ex)
		{
			try {
				webbookCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
		}finally
		{
			return jObjData;
		}
		
    }
	
	
	@GET
    @Path("/funGetExciseSupplierMasterData")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetExciseSupplierMasterData(@QueryParam("strSupplierCode") String strSupplierCode, @QueryParam("clientCode") String clientCode)
    {
		JSONObject jObjData = new JSONObject();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection webbookCon=null;
        Statement st = null;
		String sql="";
		try
		{
			webbookCon=objDb.funOpenExciseCon("mysql","master");
			st = webbookCon.createStatement();	
		    sql = " select a.strSupplierCode , a.strSupplierName "
		    		+ " from tblsuppliermaster a where  a.strSupplierCode = '"+strSupplierCode+"' and a.strClientCode = '"+clientCode+"'  " ;	
		
		    ResultSet rs=st.executeQuery(sql);
		    while(rs.next())
			{
		    	jObjData.put("strSupplierCode",rs.getString(1));
		    	jObjData.put("strSupplierName",rs.getString(2));
			}
		    
		    
		}catch(Exception ex)
		{
			try {
				webbookCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
		}finally
		{
			return jObjData;
		}
		
    }
	
	@GET
    @Path("/funGetPOSItemSearch")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funGetPOSItemr(@QueryParam("masterName") String masterName, @QueryParam("searchCode") String searchCode, @QueryParam("clientCode") String clientCode)
    {
	
	JSONObject jObjSearchData = new JSONObject();
	
	try
	{
	    jObjSearchData = funGetPOSItemSearch(masterName, searchCode, clientCode);
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return jObjSearchData;
	
    }
 	
 	
 	public JSONObject funGetPOSItemSearch(String masterName,String searchCode,String clientCode)
 	{
 		JSONObject jObjSearchData = new JSONObject();
		JSONArray jArrData = new JSONArray();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posConnection=null;
        Statement st = null;
		String sql="";
		
	try
	{
		
		switch (masterName)
		{
		    case "POSItemWeb-Service":
						
		    	posConnection=objDb.funOpenPOSCon("mysql","master");
		        st = posConnection.createStatement();	
		    sql = "select a.strItemCode,a.strItemName from tblitemmaster a where a.strItemType='Liquor' and  a.strClientCode = '"+clientCode+"'  " ;	
			
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rs.getString(1));
			    	 jArrtem.put(rs.getString(2));
			    	 jArrData.put(jArrtem);
				}
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

	@GET
    @Path("/funLoadPOSItemSearch")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject funLoadPOSItemSearch(@QueryParam("itemCode") String itemCode, @QueryParam("searchCode") String searchCode, @QueryParam("clientCode") String clientCode)
    {
	
	JSONObject jObjSearchData = new JSONObject();
	
	try
	{
	    jObjSearchData = funLoadPOSItem(itemCode, searchCode, clientCode);
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return jObjSearchData;
	
    }
 	
 	
 	public JSONObject funLoadPOSItem(String itemCode,String searchCode,String clientCode)
 	{
 		JSONObject jObjSearchData = new JSONObject();
		JSONArray jArrData = new JSONArray();
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection posConnection=null;
        Statement st = null;
		String sql="";
		
	try
	{
		
						
		    	posConnection=objDb.funOpenPOSCon("mysql","master");
		        st = posConnection.createStatement();	
		    sql = "select a.strItemCode,a.strItemName from tblitemmaster a where a.strItemType='Liquor' and a.strItemCode='"+itemCode+"' and a.strClientCode = '"+clientCode+"'  " ;	
			
			    ResultSet rs=st.executeQuery(sql);
			    while(rs.next())
				{
			    	JSONArray jArrtem =new JSONArray();
			    	jArrtem.put(rs.getString(1));
			    	 jArrtem.put(rs.getString(2));
			    	 jArrData.put(jArrtem);
				}
		
			jObjSearchData.put(itemCode, jArrData);
			
			
	}catch(Exception ex)
	{
		ex.printStackTrace();
	}finally
	{
		return jObjSearchData;
	}
	
 	}

	

}