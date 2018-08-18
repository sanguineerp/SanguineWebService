package com.excise.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.webservice.controller.clsDatabaseConnection;



@Path("/ExciseIntegrationAuto")
public class clsSynchDataWithExciseAuto {
	
	//Global Size
	
	boolean sizeIsGreater=false;
	String lastSizeCode="";
	final Integer totalSizeLength=8;	
	List<String> globalSizeList;
	
	@POST
	@Path("/funPostExciseSaleDataAuto")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response funPostExciseSaleDataAuto(JSONObject objCLData)
	{
		String response = "";
//		if(funInsertExciseSalesData(objCLData)>0)
//		{
//			response = "true";
//		}
		response=funInsertExciseSalesData(objCLData);
		return Response.status(201).entity(response).build();
	}
	
	
	@SuppressWarnings("unused")
	private String funInsertExciseSalesData(JSONObject objCLData)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        String res="";
        try
		{
		String posItemCode="",posItemName="",posCode="",billDate="",clientCode="",exciseBrandCode="",licenceCode="";
		
		cmsCon=objDb.funOpenExciseCon("mysql","transaction");
		
		Statement st = cmsCon.createStatement();
		
		licenceCode=objCLData.getString("exciseLicencceCode");
		 
		
//		String sql_insertPOSSales="insert into tblexcisepossale "
//			+ "(strPOSItemCode,strPOSItemName,intQuantity,dblRate,strPOSCode,dteBillDate"
//			+ ",strClientCode,strBrandCode,strBillNo) "
//			+ "values ";
		
		JSONArray mJsonArray=(JSONArray)objCLData.get("MemberPOSSalesInfo");
		int quantity;
		double  rate1;
		 String sucess="";
		 
		JSONObject mJsonObject = new JSONObject();
		for (int i = 0; i < mJsonArray.length(); i++) 
		{
		    mJsonObject =(JSONObject) mJsonArray.get(i);
		    posItemCode=mJsonObject.get("posItemCode").toString();
//		    quantity=Integer.parseInt( mJsonObject.get("quantity").toString());
		    quantity=(int)(Math.round(Double.parseDouble(mJsonObject.get("quantity").toString())));
		    rate1=Double.parseDouble(mJsonObject.get("rate").toString());
		    posCode=mJsonObject.get("posCode").toString();
		    billDate=mJsonObject.get("billDate").toString();
		    clientCode=mJsonObject.get("clientCode").toString();
		    exciseBrandCode=mJsonObject.get("exciseBrandCode").toString();
		    posItemName=mJsonObject.get("posItemName").toString();
		    
		   
		 
		    
		    sucess= funAddUpdateExciseSale(posItemCode,quantity,rate1,posCode,billDate,clientCode,exciseBrandCode,licenceCode,posItemName);
		    //sucess= funAddUpdateExciseSale("I000614",1,40.0,"p036","2016-03-30","074.001","B0001","L001");
		}
          if(!"".equals(sucess))
		
          {
        	  res= sucess;
        	  
          }
		}catch(Exception e)
		{
            res="";			
			e.printStackTrace();
		}
    	finally
		{
			return res;
		}
	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public String funAddUpdateExciseSale(String posItemCode,int intQtyPOS,double rate,String posCode,String billDate,String clientCode,String exciseBrandCode,String licenceCode,String posItemName )
	
	{
		
		
		    clsDatabaseConnection objDb=new clsDatabaseConnection();
		    
		    Connection webexcise=null;
	      	Statement st=null;
	      	Statement st1=null;
	      	Statement st2=null;
	      	Statement st4=null;
	      	ResultSet rs=null;
	      	Statement stUpdate=null;
	      	Connection webmms = null;
	      	Statement stwebmms=null;
	      	String result="";
	      	ResultSet rsBrandData = null;
	      	ResultSet rsData =null;
	      	String strSourceEntry="POS Sale";
	      	String userCode="Super";
	      	
	      	
	      	 String strBtls=""  ,strPeg="" ,strML="";
	      	
	      	 try
             {
			String startDateSql = "select Date(dtStart) from tblcompanymaster where strClientCode='"
					+ clientCode + "' ";

			webexcise = objDb.funOpenExciseCon("mysql", "transection");
			webmms = objDb.funOpenMMSCon("mysql", "transection");

			st = webexcise.createStatement();
			st1 = webexcise.createStatement();
			st2 = webexcise.createStatement();
			st4 = webexcise.createStatement();
			stUpdate = webexcise.createStatement();

			stwebmms = webmms.createStatement();

			ResultSet rsStrtDate = stwebmms.executeQuery(startDateSql);
			String startDate = "";
			if (rsStrtDate.next()) {
				LinkedList ls = new LinkedList();
				startDate = rsStrtDate.getString(1);
			}
			String tempSizeClientCode = clientCode;

			String tempBrandClientCode = clientCode;

			String Global_Masters = "select a.strSubCategory,a.strBrandMaster,a.strSizeMaster from tblexcisepropertymaster a "
					+ "	where strClientCode='" + clientCode + "' ";
			ResultSet rsGlobalData = st.executeQuery(Global_Masters);
			while (rsGlobalData.next()) {
				if (rsGlobalData.getString(2).equalsIgnoreCase("All")) {
					tempBrandClientCode = "All";
				}
				if (rsGlobalData.getString(3).equalsIgnoreCase("All")) {
					tempSizeClientCode = "All";
				}
			}
			exciseBrandCode = "";

			// getting Brand code from recipe master using pos item Code

			String sql = "select b.strBrandCode,b.dblQty from tblexciserecipermasterhd a,tblexciserecipermasterdtl b "
					+ " where a.strParentCode='"
					+ posItemCode
					+ "' and "
					+ " a.strRecipeCode=b.strRecipeCode and a.strClientCode='"
					+ clientCode + "'    ";
			rsData = st4.executeQuery(sql);
			while (rsData.next()) {
				// /
				exciseBrandCode = rsData.getString(1);
				int posItemQty = (int) (Math.round(rsData.getDouble(2)));
				int quantity1 = intQtyPOS * (int) (Math.round(posItemQty));
				strML = String.valueOf(quantity1);
				if (!exciseBrandCode.isEmpty()) {

					Long bottals = new Long(0);
					Long intpegs = new Long(0);
					Long intML = new Long(0);

					if (strBtls != null && !(strBtls.isEmpty())) {
						bottals = Long.parseLong(strBtls);
					}

					if (strPeg != null && !(strPeg.isEmpty())) {
						intpegs = Long.parseLong(strPeg);
					}

					if (strML != null && !(strML.isEmpty())) {
						intML = Long.parseLong(strML);
					}

					List brandDataList = new LinkedList();

					String sql_BrandList = "select a.strBrandCode,a.strSizeCode,a.strShortName,b.intQty,a.intPegSize,  "
							+ " ifnull(d.dblRate,'0') as rate,c.intMaxSaleQty, "
							+ " a.strBrandName from  tblbrandmaster a LEFT OUTER JOIN tblratemaster d "
							+ " ON d.strBrandCode = a.strBrandCode  AND d.strClientCode='"
							+ clientCode
							+ "',"
							+ " tblsizemaster b,tblsubcategorymaster c "
							+ " where a.strBrandCode='"
							+ exciseBrandCode
							+ "' "
							+ " and a.strSizeCode=b.strSizeCode and a.strSubCategoryCode=c.strSubCategoryCode "
							+ " and a.strClientCode='"
							+ tempBrandClientCode
							+ "' and b.strClientCode='"
							+ tempSizeClientCode
							+ "' ";
					rsBrandData = st1.executeQuery(sql_BrandList);

					Integer brandSize1 = 0;
					Integer pegSize1 = 0;
					Integer maxSaleQty1 = 0;
					while (rsBrandData.next()) {

						// strML=rsBrandData.getString(5);

						// LinkedList ls= new LinkedList();
						brandDataList.add(rsBrandData.getString(1));// BrandCode
						brandDataList.add(rsBrandData.getString(2));// SizeCode
						brandDataList.add(rsBrandData.getString(3));// ShortName
						brandDataList.add(rsBrandData.getString(4));// intQty
						brandDataList.add(rsBrandData.getString(5));// intPegSize

						System.out.print(rsBrandData.getString(4));
						Integer brandSize = rsBrandData.getInt(4);
						Integer pegSize = rsBrandData.getInt(5);
						// Double
						// rate=Double.parseDouble(rsBrandData.getString(6));
						Integer maxSaleQty = rsBrandData.getInt(7);

						String sql_OpData = "select c.intOpBtls,c.intOpPeg,c.intOpML from tblbrandmaster a,tblopeningstock c "
								+ " where a.strBrandCode='"
								+ exciseBrandCode
								+ "' and a.strBrandCode=c.strBrandCode  "
								+ " and  a.strClientCode='"
								+ tempBrandClientCode
								+ "' and  c.strClientCode='"
								+ clientCode
								+ "' and c.strLicenceCode='"
								+ licenceCode
								+ "' ";
						ResultSet rsOpData = st2.executeQuery(sql_OpData);
						Integer intOpBtls = 0;
						Integer intOpPeg = 0;
						Integer intOpML = 0;
						while (rsOpData.next()) {
							intOpBtls = Integer.parseInt(rsOpData.getString(1));// intOpBtls
							intOpPeg = Integer.parseInt(rsOpData.getString(2));// intOpPeg
							intOpML = Integer.parseInt(rsOpData.getString(3));// intOpML
						}
						rsOpData.close();
						brandDataList.add(intOpBtls);
						brandDataList.add(intOpPeg);
						brandDataList.add(intOpML);

						// String fromDate='';
						// fromDate=fromDate.split("/")[2]+"-"+fromDate.split("/")[1]+"-"+fromDate.split("/")[0];
						//
						String currentStk = "0.0";

						LinkedList stkData = funStockList(brandDataList,
								startDate, billDate, clientCode, licenceCode);
						if (stkData != null) {
							currentStk = stkData.get(6).toString().trim();
						}

						Long SaleQty = funStockInML(bottals, intpegs, intML,
								brandDataList);
						String[] strCurentArr = String.valueOf(currentStk)
								.split("\\.");
						Long stBls = Long.parseLong(strCurentArr[0].toString()
								.trim());
						Long stkPeg = new Long(0);
						Long stkML = Long.parseLong(strCurentArr[1].toString()
								.trim());
						Long availableStk = funStockInML(stBls, stkPeg, stkML,
								brandDataList);

						if (availableStk >= SaleQty) {

													// // Integer brandSize=
													// Integer.parseInt(rsBrandData.getString(4));
													// // Integer pegSize=
													// Integer.parseInt(rsBrandData.getString(5));
													// // // Double
													// rate=Double.parseDouble(rsBrandData.getString(6));
													// // Integer
													// maxSaleQty=Integer.parseInt(rsBrandData.getString(7));
													//
													//
													// Integer brandSize= brandSize1;
													// Integer pegSize= pegSize1;
													// // Double
													// rate=Double.parseDouble(rsBrandData.getString(6));
													// Integer maxSaleQty=maxSaleQty1;
							Double rateBrand=Double.parseDouble(rsBrandData.getString(6));
													ArrayList<Integer> saleDevidedList = new ArrayList<Integer>();
													Random ObjRandom = new Random();
													Long quantity = (long) 0;
						
													if (pegSize <= 0) {
														pegSize = brandSize;
													}
						
													Long btsMl = brandSize * bottals;
													Long pegMl = pegSize * intpegs;
													quantity = btsMl + pegMl + intML;
						
													int cnt = (int) Math.ceil(quantity / pegSize);
						
													int totalPegSum = 0;
													int intpeg = 0;
													intpeg = (int) Math.floor(maxSaleQty / pegSize);
													Integer minPeg = 0;
													if (intpeg > 20 && cnt > 20) {
														minPeg = intpeg - 10;
													} else if (intpeg > 10 && cnt > 10) {
														minPeg = 10;
													}
						
													for (int i = 0; i < cnt; i++) {
														int randomPeg = ObjRandom.nextInt(intpeg) + 1;
														if (cnt > randomPeg) {
															if (randomPeg >= minPeg) {
																saleDevidedList.add(randomPeg);
																totalPegSum = totalPegSum + randomPeg;
																if (totalPegSum == cnt) {
																	break;
																} else {
																	if ((cnt - totalPegSum) < intpeg + 1) {
																		saleDevidedList
																				.add((int) (cnt - totalPegSum));
																		break;
																	}
																}
															} else {
																i--;
															}
														} else {
															saleDevidedList.add(cnt);
															break;
														}
													}
						
													result = funAddSaleDataBulkly(saleDevidedList,
															exciseBrandCode, pegSize, rate, billDate,
															licenceCode, strSourceEntry, clientCode,
															userCode, stUpdate, st,posItemName,intQtyPOS,posItemCode,rateBrand);

						} else {
							result = "";
						}// Availabel stock check
						
					}// BrandCode Empty
					rsBrandData.close();

				}
			}
			rsData.close();
		}// end of while getting Brand code from recipe master using pos item
			// Code
	      	 catch(Exception e)
	      	 {
	          e.printStackTrace();		 
	      
	      	 }
    		finally{
    			
    			return result;
    			
    			
    		}
	
	}
	
    		@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
    		private String funAddSaleDataBulkly(ArrayList<Integer> saleDevidedList, String strBrandCode, 
    												Integer pegSize, Double rate, 
    												String dteBillDate, String strLicenceCode, 
    												String strSourceEntry,String clientCode, String userCode,Statement stUpdate,Statement st,String posItemName,int intQtyPOS,String posItemCode,double rateBrand
    											) throws SQLException{
    			
    			clsDatabaseConnection objDb=new clsDatabaseConnection();
    	        Connection cmsCon=null;
    	        String salesID ="";
    	        
    			try {
					cmsCon=objDb.funOpenExciseCon("mysql","transaction");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			
    			Statement stPOSData = cmsCon.createStatement();
    			
    			
    			
    			Boolean success = false;
    			
    			
    			
    			Long exciseBillNo=new Long(0);
    			//For one Day Pass permit Holder
    			String currentDateTime,currentDate;
    			Long oneDaypermitFirst= new Long(0);
    			Long oneDaypermitLast= new Long(0);
    			//clsOneDayPassHdModel oneDayPassModel = objclsOneDayPassService.funGetOneDayPassByDate(dteBillDate, clientCode);
    		   
    			String sqlOneDayPassModel="select intId,dteDate,intFromNo,intToNo from tblonedaypass where  dteDate='"+dteBillDate+"' and strClientCode ='"+clientCode+"' ";
    			
    			ResultSet oneDayPassModel=st.executeQuery(sqlOneDayPassModel);
    			List<String> checkDuplicateRecords = new ArrayList<String>();
    			
    			if(oneDayPassModel.next()){
    				
    				
    				oneDaypermitLast = oneDayPassModel.getLong(4);
    				
    				String oneDayPassNoString = " SELECT a.strPermitCode AS maxNumber "
    						+ " from tblexcisesaledata a  where date(a.dteBillDate) ='"+oneDayPassModel.getDate(2)+"' "
    						+ " and  a.strClientCode='"+clientCode+"' order by a.intBillNo desc limit 1  ";
    				
    				
    				ResultSet ObjOPDataList=st.executeQuery(oneDayPassNoString);
    				if(!ObjOPDataList.next()){
    					 oneDaypermitFirst= oneDayPassModel.getLong(3);
    				}else{
    					String maxNumber = ObjOPDataList.getString(1);
    					oneDaypermitFirst= Long.parseLong(maxNumber);
    					oneDaypermitFirst++;
    				}
    			
    			}
    			
//    			Logic For Bill Generation And Save in to tblexcisesaledata
    			Long lastNo=new Long(0);
    			Long lastNoPOSSalesID=new Long(0);
    			try
    			{
    			String sqlpermitLastNo="select ifnull(MAX(intId),0) from tblpermitmaster "; 
    			ResultSet listLastNo=st.executeQuery(sqlpermitLastNo);
    			if(listLastNo.next()){
    		//	System.out.println(listLastNo.getLong(1));
    			lastNo=listLastNo.getLong(1);
    			}
    			//lastNo=Long.parseLong(lastRecord);
    			lastNo++;
    			}catch(Exception e)
    			{
    				lastNo=new Long(0);
    				//logger.error(e);
    				e.printStackTrace();
    			}
    			Integer permitLastNo= Integer.parseInt(""+lastNo);
    		
    			try
    			{
    				
//    				String sqlListLastNo="select ifnull(MAX(intBillNo),0),count(intBillNo) from tblexcisesaledata where strClientCode='"+clientCode+"' and strSourceEntry='POS Sale'";
    				String sqlListLastNo="select count(*) from (select ifnull( COUNT(intBillNo),0),count(intBillNo) from tblexcisesaledata where strClientCode='"+clientCode+"' and strSourceEntry='POS Sale' group by strSalesCode) as b";
    			String sqlListLastNoforPos="select count(*) from (SELECT IFNULL( COUNT(intBillNo),0), COUNT(intBillNo) "
                                    +" FROM tblexcisesaledata " 
                                    +" WHERE strClientCode='"+clientCode+"'  and strSourceEntry='POS Sale'  and dteBillDate='"+dteBillDate+"'  group by strSalesCode ) as dd ";
    			
    			ResultSet listLastNo1=st.executeQuery(sqlListLastNo);
    				ResultSet listLastNoForPOS=stPOSData.executeQuery(sqlListLastNoforPos);
    				if(listLastNo1.next()){
    					if(listLastNoForPOS.next())
    					{  
    						System.out.println(listLastNo1.getLong(1));
							System.out.println(listLastNoForPOS.getLong(1));
    						if(listLastNoForPOS.getLong(1)>0)
    						{
    							
    							lastNoPOSSalesID=listLastNo1.getLong(1)-listLastNoForPOS.getLong(1);
    							if(lastNoPOSSalesID==-1)
    							{
    								lastNoPOSSalesID=lastNoPOSSalesID +2;
    							}else{
    								if(lastNoPOSSalesID==0)
    								{
    									lastNoPOSSalesID=listLastNoForPOS.getLong(1);
    								}else{
    								lastNoPOSSalesID++;
    							}
    							}
    							
    						}
    					else{
    						
    						lastNoPOSSalesID=listLastNo1.getLong(1);
    						lastNoPOSSalesID++;
    					}
    					}    					
    					
    				}
    			
    				
    				String sqlListLastNoForBillNo="select ifnull(MAX(intBillNo),0),count(intBillNo) from tblexcisesaledata where strClientCode='"+clientCode+"' ";	
    				ResultSet listLastNoForbill=st.executeQuery(sqlListLastNoForBillNo);
    				if(listLastNoForbill.next())
    				{
    					lastNo=listLastNoForbill.getLong(1);
    				}
					lastNo++;	
    			}catch(Exception e)
    			{
    				lastNo=new Long(0);
    				//logger.error(e);
    				e.printStackTrace();
    			}
    			Random ObjRandom = new Random();	
    			
    			//One To Many Logic
    			Integer oneToMany=0;
                          			
    				for(int i=0; i<saleDevidedList.size();i++){
    						if(saleDevidedList.get(i) !=0){
    							Date dt=new Date();
    							currentDateTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
    							
    							 salesID =  String.format("%06d", lastNoPOSSalesID);
    							 String[] spDate=dteBillDate.split("-");
    							 String years=String.valueOf(Integer.parseInt(spDate[2]));
    							 String transYear=funGetAlphabet((Integer.parseInt(years)%26));
    						     String transMonth=funGetAlphabet(Integer.parseInt(spDate[1]));
    						     
    						     String licence[]=strLicenceCode.split("L0");
    						     salesID=licence[1]+"PS"+transYear+transMonth+salesID;
    							 
    							String sqlExciseSale="insert into tblexcisesaledata (strUserCreated,dteCreatedDate,dteBillDate,strLicenceCode,strSalesCode,  "
    												+"strUserModified,dteLastModified,strClientCode,intTotalPeg,dblTotalAmt,intQty,strItemCode,strSourceEntry,strPOSItem,strRemark,strPermitCode,intBillNo)"
    												+ " values('"+userCode+"','"+currentDateTime+"','"+dteBillDate+"','"+strLicenceCode+"','"+salesID+"','"+userCode+"','"+currentDateTime+"','"+clientCode+"','"+saleDevidedList.get(i)+"', ";
                               
    							  
    							
    							Double totalAmt= rateBrand*(saleDevidedList.get(i));
    								//objHdModel.setDblTotalAmt(totalAmt);
    												
    								int totalQty =(pegSize)*(saleDevidedList.get(i));
    								String Remarks="posQty:"+intQtyPOS+"posItemPrice:"+rate+"posItemName:"+posItemName;
    								sqlExciseSale=sqlExciseSale+" '"+totalAmt+"','"+totalQty+"','"+strBrandCode+"','"+strSourceEntry+"','"+posItemCode+"','"+Remarks+"' ,  ";
    								//One To Many Logic Implementation
    								int oneToManyCnt = ObjRandom.nextInt(1);
    								if(oneToManyCnt==0){
    									oneToMany++;
    								}
    								
    								if(oneToMany >=3){

    									
    									String oneToManypermitCode ="SELECT a.intBillNo,a.strPermitCode,b.strSubCategoryCode "
    											+ " FROM tblexcisesaledata a,tblbrandmaster b "
    											+ " WHERE DATE(a.dteBillDate)='"+dteBillDate+"' AND a.strItemCode !='"+strBrandCode+"' "
    											+ " AND a.strItemCode =b.strBrandCode AND b.strSubCategoryCode "
    											+ " NOT IN(select p.strSubCategoryCode from tblbrandmaster p where p.strBrandCode='"+strBrandCode+"') "
    											+ " AND a.strClientCode='"+clientCode+"' ";
    									
    									ResultSet rsObjOPDataList=st.executeQuery(oneToManypermitCode);
    									ArrayList ObjOPDataList=new ArrayList();
    									while(rsObjOPDataList.next())
    									{
    										ObjOPDataList.add(rsObjOPDataList.getString(1));
    										ObjOPDataList.add(rsObjOPDataList.getString(2));
    										ObjOPDataList.add(rsObjOPDataList.getString(3));
    									
    										
    									}
//    									List ObjOPDataList=  objGlobalFunctionsService.funGetDataList(oneToManypermitCode, "sql");
    									
    									if(ObjOPDataList.size()>0){
    										int randomNumber = ObjRandom.nextInt(ObjOPDataList.size());	
    										Object[] strPermitArr = (Object[]) ObjOPDataList.get(randomNumber);
    										
    										//Check Duplicates But Still Not Saved in DataBase
    										if(checkDuplicateRecords.contains(strPermitArr[0].toString()+strBrandCode+clientCode)){
    											
    											if(oneDaypermitFirst < oneDaypermitLast){
    											    exciseBillNo=lastNo;
    											    sqlExciseSale=sqlExciseSale+"'"+oneDaypermitFirst+"','"+lastNo+"' )";
    												
    												oneDaypermitFirst++;
    											} else {

    												exciseBillNo=lastNo;
    												sqlExciseSale=sqlExciseSale+"'"+oneDaypermitFirst+"','"+lastNo+"' )";
    											}
    											
    										} else {
    											int randomPermit = ObjRandom.nextInt(permitLastNo);
    											if(randomPermit<=0){
    												randomPermit=1;
    											}
    											String strPermitCode = "PM" + String.format("%06d", randomPermit);
//    											exciseBillNo=lastNo;
    											sqlExciseSale=sqlExciseSale+"'"+strPermitCode+"','"+lastNo+"' )";
    											
    											oneToMany=0;
    										}
    									} else {
    										
    										if(oneDaypermitFirst < oneDaypermitLast){
    											exciseBillNo=lastNo;
    											sqlExciseSale=sqlExciseSale+"'"+oneDaypermitFirst+"','"+lastNo+"' )";
    											oneDaypermitFirst++;
    										} else {
    											int randomPermit = ObjRandom.nextInt(permitLastNo);
    											if(randomPermit<=0){
    												randomPermit=1;
    											}
    											String strPermitCode = "PM" + String.format("%06d", randomPermit);
    											exciseBillNo=lastNo;
    											sqlExciseSale=sqlExciseSale+"'"+strPermitCode+"','"+lastNo+"' )";
    										}//Is One Day Pass
    										
    									}//Is Previous Bill
    									oneToMany=0;
    								} else {
    									
    									if(oneDaypermitFirst < oneDaypermitLast) {
    										
    										exciseBillNo=lastNo;
    										sqlExciseSale=sqlExciseSale+"'"+oneDaypermitFirst+"','"+lastNo+"' )";
    										oneDaypermitFirst++;
    										
    									} else {
    										//System.out.println(permitLastNo);
    										int randomPermit = ObjRandom.nextInt(permitLastNo);
    										if(randomPermit<=0){
    											randomPermit=1;
    										}
    										String strPermitCode = "PM" + String.format("%06d", randomPermit);
    										exciseBillNo=lastNo;
    										sqlExciseSale=sqlExciseSale+"'"+strPermitCode+"','"+lastNo+"' )";
    										
    									}//One Day Permit Logic
    									
    								}//one To Many Logic
    								//System.out.println(sqlExciseSale);
    								stUpdate.executeUpdate(sqlExciseSale);
    								checkDuplicateRecords.add(exciseBillNo+strBrandCode+clientCode);
//    								objList.add(objHdModel);
//    								funAuditDtl(objHdModel);
    								lastNo++;
    								success=true;
    						}//Is Sale Quantity Greater Than Zero
    				}
    	
    			return salesID;
    		}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedList funStockList(List brandDataList,String fromDate, String toDate,String clientCode,String licenceCode){
			
		LinkedList brandData= new LinkedList();
		
		String inPeg="ML";
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
				LinkedHashMap tpData=funTpStock(strBrandCode,fromDate,toDate,clientCode,licenceCode);
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
							
							
				Long saleStkMl= funSalesStk(strBrandCode,sizeData,fromDate,toDate,clientCode,licenceCode);
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
	
	
	public LinkedHashMap<String,String> funTpStock(String brandCode,String fromDate,String toDate,String clientCode,String licenceCode){
		
		LinkedHashMap<String,String> result= new LinkedHashMap<String,String>();
		try{
			clsDatabaseConnection objDb4=new clsDatabaseConnection();
	        Connection cmsCon4=null;
	        Statement st4=null;
			cmsCon4=objDb4.funOpenExciseCon("mysql","master");
	        st4 = cmsCon4.createStatement();
	        
	        String sql_TpQty="select a.strTPNo,b.intBottals,a.strTPCode from tbltphd a, tbltpdtl b where b.strTPCode=a.strTPCode and "
				+ "date(a.strTpDate) between '"+fromDate+"' and '"+toDate+"' and b.strBrandCode='"
				+brandCode+"' and b.strClientCode='"+clientCode+"' and a.strClientCode='"+clientCode+"' and a.strLicenceCode='"+licenceCode+"' ";
		
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
	
	public Long funSalesStk(String brandCode,Integer brandSize,String fromDate,String toDate,String clientCode,String licenceCode){
		
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
	        		String sql_SalesQty="select ifnull(sum(a.intTotalPeg),'0') as Qty from tblexcisesaledata a where "
						+ "date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' and "
						+ "a.strItemCode='"+rsrecipeData.getString(1)+"' and a.strClientCode='"+clientCode+"' and a.strLicenceCode='"+licenceCode+"' ";
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
	        
			String sql_SalesQty="select ifnull(sum(a.intQty),'0') as Qty from tblexcisesaledata a where "
					+ "date(a.dteBillDate) between '"+fromDate+"' and '"+toDate+"' and "
					+ "a.strItemCode='"+brandCode+"' and a.strClientCode='"+clientCode+"' and a.strLicenceCode='"+licenceCode+"' ";
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
	
	

public Long funStockInML(Long bottals,Long intpegs,Long intML,List brandDataList){
		
		Integer brandSize= Integer.parseInt(brandDataList.get(3).toString());
		Integer pegSize= Integer.parseInt(brandDataList.get(4).toString());
		
		Long quantity=(long) 0;
		if(pegSize<=0){
			pegSize=brandSize;
		}
			Long btsMl= brandSize * bottals;
			Long pegMl= pegSize * intpegs;
			quantity=btsMl+pegMl+intML;
		return quantity;
	}

public String funGetAlphabet(int no)
{
	String[] alphabets= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	return alphabets[no];
}
}
