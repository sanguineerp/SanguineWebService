package com.apos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.webservice.controller.clsDatabaseConnection;

@Controller
public class clsAPOSUtility {

	public JSONArray funGetTableStatusData(String posCode,String status,String areaCode)
	{
		JSONArray jsArrTableDetails=new JSONArray();
		switch(status)
		{
			case "All":
				jsArrTableDetails=funGetAllTableData(posCode,areaCode);
			break;
				
			case "Normal":
				jsArrTableDetails=funGetNormalTableData(posCode,areaCode);
                break;
                
            case "Occupied":
            	jsArrTableDetails=funGetOccupiedTableData(posCode,areaCode);
              
                break;
                
            case "Billed":
            	jsArrTableDetails=funGetBilledTableData(posCode,areaCode);
              
                break;
		}
		
		return jsArrTableDetails;
	}
	
	private JSONArray funGetAllTableData(String posCode,String areaCode) 
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrObj=new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql="";
           
            sql = " select a.strTableNo, a.strTableName, a.strStatus,a.intPaxNo"
               	+ " from tbltablemaster a left outer join tblitemrtemp b on a.strTableNo=b.strTableNo "
               	+ " where a.strOperational='Y' ";
	            if(!posCode.equals("All"))
	            {
	                sql+= " and a.strPOSCode='" + posCode + "' ";
	            }
	            if(!areaCode.equals("All"))
	            {
	                sql+= " and a.strAreaCode='" + areaCode + "' ";
	            }
	            sql=sql+ " group by a.strTableNo "
               	+ "	order by a.intSequence, a.strTableName  ";
            //System.out.println(sql);
            
            
            ResultSet rsTableInfo = st.executeQuery(sql);
            while (rsTableInfo.next()) 
            {
            	JSONObject obj=new JSONObject();
            	obj.put("TableName",rsTableInfo.getString(2));
            	obj.put("TableNo",rsTableInfo.getString(1));
            	obj.put("TableStatus",rsTableInfo.getString(3));
            	obj.put("PaxNo",rsTableInfo.getString(4));
            	String timeDifference="";
            	if(rsTableInfo.getString(3).equals("Occupied"))
            	{
            		 timeDifference = funGetTimeDiffInFirstKOTAndCurrentTime(rsTableInfo.getString(1));
                     if (timeDifference.startsWith("-"))
                     {
                    	 timeDifference = "";
                     }
            	}
            	else if(rsTableInfo.getString(3).equals("Billed"))
            	{
            		timeDifference = funGetTimeDiffInBilledAndCurrentTime(rsTableInfo.getString(1),posCode);
                    if (timeDifference.startsWith("-"))
                    {
                    	timeDifference = "";
                    }
            	}
            	
            	obj.put("Time",timeDifference);
            	
		        arrObj.put(obj);
            }
            rsTableInfo.close();
            jObj.put("AllTableList", arrObj);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrObj;//jObj.toString();
        }
	}
		
	private JSONArray funGetNormalTableData(String posCode,String areaCode) 
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrjObjNormal= new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
        	String sql=" select strTableNo,strTableName,intPaxNo "
        		+ " from tbltablemaster "
         		+ " where strStatus='Normal' ";
        	
	        	if(!posCode.equals("All"))
	            {
	                sql+= " and strPOSCode='" + posCode + "' ";
	            }
	            if(!areaCode.equals("All"))
	            {
	                sql+= " and strAreaCode='" + areaCode + "' ";
	            }
	            sql+= " order by intSequence,strTableName";
            //System.out.println(sql);
        	
            ResultSet rsNormal= st.executeQuery(sql);
            while (rsNormal.next()) 
		    {
            	JSONObject jObjNormalTable=new JSONObject();
		        jObjNormalTable.put("TableNo",rsNormal.getString(1));
		        jObjNormalTable.put("TableName",rsNormal.getString(2));
		        jObjNormalTable.put("PaxNo",rsNormal.getString(3));
		        jObjNormalTable.put("Time","");
		        arrjObjNormal.put(jObjNormalTable);
		    }
            rsNormal.close();
            jObj.put("NormalTableList",arrjObjNormal);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrjObjNormal;//jObj.toString();
        }
	}
		
	private JSONArray funGetOccupiedTableData(String posCode,String areaCode) 
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrjObjOccupied= new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String sql=" select strTableNo,strTableName,intPaxNo "
            	+ " from tbltablemaster "
         		+ " where strStatus='Occupied' ";
         		if(!posCode.equals("All"))
	            {
	                sql+= " and strPOSCode='" + posCode + "' ";
	            }
	            if(!areaCode.equals("All"))
	            {
	                sql+= " and strAreaCode='" + areaCode + "' ";
	            }
	            sql+= " order by intSequence,strTableName";
            
	        ResultSet rsOccupied= st.executeQuery(sql);
	        while (rsOccupied.next()) 
			{
	        	JSONObject jObjOccupiedTable=new JSONObject();	
	        	jObjOccupiedTable.put("TableNo",rsOccupied.getString(1));
			    jObjOccupiedTable.put("TableName",rsOccupied.getString(2));
			    jObjOccupiedTable.put("PaxNo",rsOccupied.getString(3));
			    String timeDiffInFirstKOTAndCurrentTime = funGetTimeDiffInFirstKOTAndCurrentTime(rsOccupied.getString(1));
                if (timeDiffInFirstKOTAndCurrentTime.startsWith("-"))
                {
                    timeDiffInFirstKOTAndCurrentTime = "";
                }
                jObjOccupiedTable.put("Time",timeDiffInFirstKOTAndCurrentTime);
			    arrjObjOccupied.put(jObjOccupiedTable);
			}
	        rsOccupied.close();
	        jObj.put("OccupiedTableList",arrjObjOccupied);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrjObjOccupied;//jObj.toString();
        }
	}
	
	private String funGetTimeDiffInFirstKOTAndCurrentTime(String tableNo)
	    {
		    clsDatabaseConnection objDb=new clsDatabaseConnection();
	        Connection aposCon=null;
	        Statement st=null;
	        String timeDiffInFirstKOTAndCurrentTime = "";
	        try
	        {
	        	aposCon=objDb.funOpenAPOSCon("mysql","master");
	            st = aposCon.createStatement();
	            
	            String sqlKot = "select TIME_FORMAT(TIMEDIFF(CURRENT_TIME(),time(dteDateCreated)),'%i:%s'),strKOTNo "
	                    + "from tblitemrtemp  "
	                    + "where strTableNo='" + tableNo + "' "
	                    + "group by strKOTNo asc "
	                    + "limit 1 ";
	            ResultSet rsKOTTime = st.executeQuery(sqlKot);
	            if (rsKOTTime.next())
	            {
	                timeDiffInFirstKOTAndCurrentTime = rsKOTTime.getString(1);
	            }
	            rsKOTTime.close();
	            st.close();
	            aposCon.close();
	        }
	        catch (Exception e)
	        {
	            
	            e.printStackTrace();
	        }
	        finally
	        {
	            return timeDiffInFirstKOTAndCurrentTime;
	        }
	    }
	
	private JSONArray funGetBilledTableData(String posCode,String areaCode) 
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
        JSONArray arrjObjBilled= new JSONArray();
        try {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            String posDate="";
            
            // Billed Table
        	String sql="select strTableNo,strTableName,intPaxNo "
        			+ " from tbltablemaster "
        			+ " where strStatus='Billed' ";
	        	if(!posCode.equals("All"))
	            {
	                sql+= " and strPOSCode='" + posCode + "' ";
	            }
	            if(!areaCode.equals("All"))
	            {
	                sql+= " and strAreaCode='" + areaCode + "' ";
	            }
	            sql+= " order by intSequence,strTableName";
            
            
            ResultSet rsBilled= st.executeQuery(sql);
            while (rsBilled.next()) 
		    {
            	JSONObject jObjBilledTable=new JSONObject();
		        jObjBilledTable.put("TableNo",rsBilled.getString(1));
		        jObjBilledTable.put("TableName",rsBilled.getString(2));
		        jObjBilledTable.put("PaxNo",rsBilled.getString(3));
		        String timeDiffInLastBilledAndCurrentTime = funGetTimeDiffInBilledAndCurrentTime(rsBilled.getString(1),posCode);
                if (timeDiffInLastBilledAndCurrentTime.startsWith("-"))
                {
                    timeDiffInLastBilledAndCurrentTime = "";
                }
		        jObjBilledTable.put("Time",timeDiffInLastBilledAndCurrentTime);
		        arrjObjBilled.put(jObjBilledTable);
		    }
            rsBilled.close();
            jObj.put("BilledTableList",arrjObjBilled);
            st.close();
            cmsCon.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
        	return arrjObjBilled;//jObj.toString();
        }
	}
		
	private String funGetTimeDiffInBilledAndCurrentTime(String tableNo,String posCode)
    {
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection aposCon=null;
        Statement st=null;
        String timeDiffInLastBilledAndCurrentTime = "",posDate="";
        try
        {
        	aposCon=objDb.funOpenAPOSCon("mysql","master");
            st = aposCon.createStatement();
            String sql="select date(dtePOSDate) from tbldayendprocess where strPOSCode='"+posCode+"' and strDayEnd='N'";
            ResultSet rs= st.executeQuery(sql);
            if(rs.next())
            {
            	posDate=rs.getString(1);
            }
            rs.close();
            
            
            String sqlKot = "select TIME_FORMAT(TIMEDIFF(CURRENT_TIME(),time(dteBillDate)),'%i:%s'),a.strBillNo "
                    + "from tblbillhd a "
                    + "where date(a.dteBillDate)='" + posDate + "' "
                    + "and a.strTableNo='" + tableNo + "' "
                    + "and a.strBillNo not in(select strBillNo from tblbillsettlementdtl) "
                    + "order by a.dteBillDate desc "
                    + "limit 1; ";
            ResultSet rsKOTTime = st.executeQuery(sqlKot);
            if (rsKOTTime.next())
            {
                timeDiffInLastBilledAndCurrentTime = rsKOTTime.getString(1);
            }
            rsKOTTime.close();
            st.close();
            aposCon.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return timeDiffInLastBilledAndCurrentTime;
        }
    }

	
	public JSONArray funFetchItemPriceDtlForCustomerOrder(String areaWisePricing, String menuHeadCode, String areaCode, String POSCode
			, String fromDate, String toDate, boolean flgAllItems,String menuType)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONObject jObj=new JSONObject();
        String gAreaCodeForTrans="";
        
        System.out.println("flg="+flgAllItems);
        System.out.println("FD="+fromDate);
        System.out.println("TD="+toDate);
        System.out.println("menuhd="+menuHeadCode);
        System.out.println("AreaWisePricing="+areaWisePricing);
        System.out.println("Areacode="+areaCode);
        System.out.println("POS="+POSCode);
        
        JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            
        	String sqlArea = "select strAreaCode from tblareamaster where strAreaName='All'";
	        ResultSet rsArea=st.executeQuery(sqlArea);
	        if (rsArea.next())
	        {
	            gAreaCodeForTrans = rsArea.getString(1);
	        }
	        rsArea.close();
	        
		String sql="",sqlImg="";
		if (areaWisePricing.equals("N")) {
			sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
	                + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
	                + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
	                + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strExternalCode,b.imgImage  "
	                + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
	                + " WHERE a.strItemCode=b.strItemCode and (a.strAreaCode='"+areaCode+"' or a.strAreaCode='"+gAreaCodeForTrans+"' ) "
	                + " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All') "
	                + " and date(a.dteFromDate)<='"+fromDate+"' and date(a.dteToDate)>='"+toDate+"' ";
            
            if(!flgAllItems)
            {
            	if(menuType.equals("MenuHead"))
            	{
            		sql+=" and a.strMenuCode = '" + menuHeadCode + "' ";
            	}
            	else
            	{
            		sql+=" and a.strSubMenuHeadCode = '" + menuHeadCode + "' ";
            	
            	}
            	
            }
            sql+=" order by b.strItemName ";
        }
		else 
        {
			sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
	                + " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday,"
	                + " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
	                + " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strExternalCode,b.imgImage  "
	                + " FROM tblmenuitempricingdtl a ,tblitemmaster b "
	                + " WHERE  (a.strAreaCode='"+areaCode+"' or a.strAreaCode='"+gAreaCodeForTrans+"' ) and a.strItemCode=b.strItemCode "
	                + " and (a.strPosCode='" + POSCode + "' or a.strPosCode='All') "
	                + " and date(a.dteFromDate)<='"+fromDate+"' and date(a.dteToDate)>='"+toDate+"' ";
			
			if(!flgAllItems)
	        {
				sql+=" and a.strMenuCode = '" + menuHeadCode + "' ";
	        }
	        sql+=" order by b.strItemName ";
        }
		
		System.out.println(sql);
		
		
       
		    
            
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("ItemCode",rsMasterData.getString(1));
            	obj.put("ItemName",rsMasterData.getString(2));
            	obj.put("TextColor",rsMasterData.getString(3));
            	obj.put("PriceMonday",rsMasterData.getString(4));
            	obj.put("PriceTuesday",rsMasterData.getString(5));
            	obj.put("PriceWenesday",rsMasterData.getString(6));
            	obj.put("PriceThursday",rsMasterData.getString(7));
            	obj.put("PriceFriday",rsMasterData.getString(8));
            	obj.put("PriceSaturday",rsMasterData.getString(9));
            	obj.put("PriceSunday",rsMasterData.getString(10));
            	obj.put("TimeFrom",rsMasterData.getString(11));
            	obj.put("AMPMFrom",rsMasterData.getString(12));
            	obj.put("TimeTo",rsMasterData.getString(13));
            	obj.put("AMPMTo",rsMasterData.getString(14));
            	obj.put("CostCenterCode",rsMasterData.getString(15));
            	obj.put("HourlyPricing",rsMasterData.getString(16));
            	obj.put("SubMenuHeadCode",rsMasterData.getString(17));
            	obj.put("FromDate",rsMasterData.getString(18));
            	obj.put("ToDate",rsMasterData.getString(19));
            	obj.put("ExternalCode",rsMasterData.getString(20));
            	
            	String filePath = funCreateItemTempFolder();
    		    File file = new File(filePath + "/" + rsMasterData.getString(1) + ".jpg");
    		    Blob blob = rsMasterData.getBlob(21);
                //InputStream inImg = blob.getBinaryStream();

                if (blob.length() > 0)
                {
                    InputStream inImg = blob.getBinaryStream(1, blob.length());
                    byte[] imageBytes = blob.getBytes(1, (int) blob.length());
                    //BufferedImage image = ImageIO.read(inImg);
                    OutputStream outImg = new FileOutputStream(file);
                    int c = 0;
                    while ((c = inImg.read()) > -1)
                    {
                        outImg.write(c);
                    }
                    outImg.close();
                    inImg.close();
                }if (file.exists())
                {
                    FileInputStream fis=new FileInputStream(file);
                	  byte[] bytes = Files.readAllBytes(file .toPath());
                      String encodedImage = Base64.getEncoder().encodeToString(bytes);
                      obj.put("itemImage",encodedImage);
                }
                else
                {
               	 obj.put("itemImage","Image Not Found"); 
                }
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
            jObj.put("tblmenuitempricingdtl", arrObj);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;
	}
	
	public String funCreateItemTempFolder()
    {
	String fileName = "Item Image";
	File theDir = new File(fileName);
	if (!theDir.exists())
	{
	    System.out.println("creating directory: " + "Item Image");
	    boolean result = false;
	    
	    try
	    {
		theDir.mkdir();
		result = true;
	    }
	    catch (SecurityException se)
	    {
		// handle it
	    }
	    if (result)
	    {
		System.out.println("DIR created");
	    }
	}
	return fileName;
    }


	public JSONArray funFetchMenuHeadListForCustomerOrder(String posCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null,st1=null;
        JSONObject jObj=new JSONObject();
		//String sql="select strMenuCode,strMenuName from tblmenuhd where strClientCode='"+clientCode+"'";
        String sql="select distinct(a.strMenuCode),b.strMenuName,b.imgImage "
        		+ " from tblmenuitempricingdtl a left outer join tblmenuhd b on a.strMenuCode=b.strMenuCode "
        		+ " where a.strPosCode='"+posCode+"' "
        		+ " and b.strOperational='Y' "
        		+ " and b.strClientCode='"+clientCode+"' ";
		
		JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            st1 = cmsCon.createStatement();
            
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("strMenuItemCode",rsMasterData.getString(1));
            	obj.put("strMenuItemName",rsMasterData.getString(2));
            	obj.put("strMenuType","MenuHead");
            	
            	//String filePath="E://project//SPOS//prjSPOSStartUp//itemImages//I001261.jpg";
            	//File file = new File(filePath);
            	String filePath = funCreateItemTempFolder();
    		    File file = new File(filePath + "/" + rsMasterData.getString(1) + ".jpg");
    		    Blob blob = rsMasterData.getBlob(3);
                //InputStream inImg = blob.getBinaryStream();

                if (blob.length() > 0)
                {
                    InputStream inImg = blob.getBinaryStream(1, blob.length());
                    byte[] imageBytes = blob.getBytes(1, (int) blob.length());
                    //BufferedImage image = ImageIO.read(inImg);
                    OutputStream outImg = new FileOutputStream(file);
                    int c = 0;
                    while ((c = inImg.read()) > -1)
                    {
                        outImg.write(c);
                    }
                    outImg.close();
                    inImg.close();
                }
            	
            	
            	
            	if(file.exists())
                {
              	  FileInputStream fis=new FileInputStream(file);
              	  byte[] bytes = Files.readAllBytes(file .toPath());
                    String encodedImage = Base64.getEncoder().encodeToString(bytes);
                    obj.put("strItemImage",encodedImage);
                 
                }
                else
                {
               	 obj.put("strItemImage","Image Not Found"); 
                }
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
            jObj.put("MenuHeadList", arrObj);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;//jObj.toString();
	}
	
	
	public JSONArray funFetchModifierList(String itemCode,String clientCode)
	{
		clsDatabaseConnection objDb=new clsDatabaseConnection();
        Connection cmsCon=null;
        Statement st=null;
        JSONObject jObj=new JSONObject();
		String sql="select a.strModifierCode,a.strModifierName,b.strItemCode,b.dblRate "
			+ " from tblmodifiermaster a,tblitemmodofier b "
			+ " where a.strModifierCode=b.strModifierCode and b.strItemCode='"+itemCode+"' "
			+ " and b.strApplicable='Y'";
		
		JSONArray arrObj=new JSONArray();
        try
        {
        	cmsCon=objDb.funOpenAPOSCon("mysql","master");
            st = cmsCon.createStatement();
            
            ResultSet rsMasterData=st.executeQuery(sql);
            while(rsMasterData.next())
            {
            	JSONObject obj=new JSONObject();
            	
            	obj.put("ModifierCode",rsMasterData.getString(1));
            	obj.put("ModifierName",rsMasterData.getString(2));
            	obj.put("ItemCode",rsMasterData.getString(3));
            	obj.put("Rate",rsMasterData.getString(4));
            	
            	arrObj.put(obj);
            }
            rsMasterData.close();
            
           // jObj.put("ModifierList", arrObj);
            st.close();
            cmsCon.close();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return arrObj;//jObj.toString();
	}

	
}
