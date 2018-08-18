package com.apos.dao;


import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.model.clsAreaMasterModel;
import com.apos.model.clsPOSMasterModel;


@Repository("clsCocktailWorldInterfaceDao")
@Transactional(value = "webPOSTransactionManager")
public class clsCocktailWorldInterfaceDao
{
    
    @Autowired
    private SessionFactory webPOSSessionFactory;
    
    
	public void funSaveCocktailWorldInterface(JSONObject jObj)
	{
		
	    
    	String selectedItem;
		try {
			selectedItem = jObj.getString("reportType");
		
    	String  POSCode=jObj.getString("POSName");	
    	
    	String fromDate=jObj.getString("date");
    	  if(selectedItem.toString().equals("Sale Data File"))
            {
                funGenerateSaleDataFile(fromDate,POSCode);
            }
            else
            {
                funGenerateMenuItemCodeFile();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    
	}
    
    private String funGetCalenderDate(Date objDate)
    {
        return (objDate.getYear() + 1900) + "-" + (objDate.getMonth() + 1) + "-" + (objDate.getDate());
    }
    
    /**
     * create Temp Folder for text report
     */
    private void funCreateTempFolder()
    {
        String filePath = System.getProperty("user.dir");
        File file = new File(filePath + "/Temp");
        if (!file.exists())
        {
            file.mkdirs();
        }
    }
    
    public void funGenerateSaleDataFile(String fromDate,String POSCode) throws FileNotFoundException
  {
      String selectedPOSCode=POSCode;
      JSONObject jObjData=new JSONObject();
      
     // String fromDate=funGetCalenderDate(dteFromDate.getDate());
      //String toDate=funGetCalenderDate(dteToDate.getDate());
      
      String[] arrFileName=fromDate.split("-");
      String fileName="CW"+arrFileName[0]+arrFileName[1]+arrFileName[2].substring(1,3);
      funCreateTempFolder();
      String filePath = System.getProperty("user.dir");
      File file = new File(filePath + "/Temp/"+fileName+".txt");
      PrintWriter pw = new PrintWriter(file);
      
      
      
      try 
		{
			
      	JSONArray jArrData=new JSONArray();
      String text="";
      String sbSql="";
      
      Map<Integer,List<String>> hmSalesData=new HashMap<Integer,List<String>>();
      
    //  sbSql.setLength(0);
      sbSql="select a.strPOSCode,a.strBillNo,b.strItemCode,sum(b.dblQuantity),b.dblRate,a.strSettelmentMode "
          + " from tblbillhd a,tblbilldtl b,tblitemmaster c "
          + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '"+fromDate+"' and b.strItemCode=c.strItemCode ";
      if(!POSCode.equals("All"))
      {
          sbSql=sbSql + " and a.strPOSCode='"+POSCode+"' ";
      }
      sbSql=sbSql + " and c.strRevenueHead='Liquor' ";
      sbSql=sbSql + " group by b.strItemCode order by a.strBillNo ";
      Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql);
      List rsSaleData = query.list();
      
      if(rsSaleData!=null)
      {
      	for(int i=0; i<rsSaleData.size(); i++)
			{
				Object[] obj = (Object[]) rsSaleData.get(i);
				
				JSONObject objSaleDataDtl=new JSONObject();
				
				String strPOSCode=obj[0].toString();
				String strBillNo=obj[1].toString(); 
				String strItemCode=obj[2].toString();  
				String dblQuantity=obj[3].toString();  
				String dblRate=obj[4].toString();  
				 
           text=strPOSCode+" "+strBillNo+" "+strItemCode+" "+dblQuantity+" "+dblRate;
           
        pw.println(text);
      }
      
      }
      
     
      sbSql= " select a.strPOSCode,a.strBillNo,b.strItemCode,sum(b.dblQuantity),b.dblRate,a.strSettelmentMode "
          + " from tblqbillhd a,tblqbilldtl b,tblitemmaster c "
          + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '"+fromDate+"' and b.strItemCode=c.strItemCode ";
      if(!POSCode.equals("All"))
      {
          sbSql=sbSql + " and a.strPOSCode='"+POSCode+"' ";
      }
      sbSql=sbSql + " and c.strRevenueHead='Liquor' ";
      sbSql=sbSql + " group by b.strItemCode order by a.strBillNo";
      
    query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql);
    List rsSaleData1 = query.list();
      if(rsSaleData1!=null)
      {
      	for(int i=0; i<rsSaleData1.size(); i++)
			{
				Object[] obj = (Object[]) rsSaleData1.get(i);
				
				JSONObject objSaleDataDtl=new JSONObject();
				
				String strPOSCode=obj[0].toString();
				String strBillNo=obj[1].toString(); 
				String strItemCode=obj[2].toString();  
				String dblQuantity=obj[3].toString();  
				String dblRate=obj[4].toString();
				 text=strPOSCode+" "+strBillNo+" "+strItemCode+" "+dblQuantity+" "+dblRate;
				 
         pw.println(text);
      }
      	
      }
      pw.flush();
      pw.close();
      
      
      Desktop dt = Desktop.getDesktop();
      dt.open(file);
		} 
      catch(Exception e)
      {
   	   e.printStackTrace();
      }
		
  }
    public void funGenerateMenuItemCodeFile() throws FileNotFoundException 
  {
//  	HttpServletRequest request;
      

  	String fileName="CWMENU";
    funCreateTempFolder();
    String filePath = System.getProperty("user.dir");
    File file = new File(filePath + "/Temp/"+fileName+".txt");
    PrintWriter pw = new PrintWriter(file);
    
      String sbSql;
      String text="",strItemCode="",strItemName="";
      JSONObject jObjData=new JSONObject();
 try
 {
	   JSONArray jArrData=new JSONArray();
	   
      sbSql="select strItemCode,strItemName from tblitemmaster where strRevenueHead='Liquor' ";
     Query  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql);  
     List rsMenuItemCodeData = query.list();
     if(rsMenuItemCodeData!=null)
     {
     	for(int i=0; i<rsMenuItemCodeData.size(); i++)
			{
				Object[] obj = (Object[]) rsMenuItemCodeData.get(i);
				
				
				JSONObject objDeliverBoy=new JSONObject();
				 strItemCode=obj[0].toString();
				 strItemName=obj[1].toString();
				 text=strItemCode+" "+strItemName;
//				 objDeliverBoy.put("stringDtl",text);
//				 jArrData.put(objDeliverBoy);
	           pw.println(text);
//	            os.write(bytes, 0, read);
			}
//     	jObjData.put("list", jArrData);
//      text+=text;
     }

 
     pw.flush();
     pw.close();
     
              
    
      
      Desktop dt = Desktop.getDesktop();
      dt.open(file);
		} 
		catch(Exception e)
		{
			   e.printStackTrace();
		}
 }
    
    
    
    
//   public JSONObject funGenerateSaleDataFile(String fromDate,String POSCode) throws IOException, JSONException
//    {
//        String selectedPOSCode=POSCode;
//        JSONObject jObjData=new JSONObject();
//        
//       // String fromDate=funGetCalenderDate(dteFromDate.getDate());
//        //String toDate=funGetCalenderDate(dteToDate.getDate());
//        
////        String[] arrFileName=fromDate.split("-");
////        String fileName="CW"+arrFileName[2]+arrFileName[1]+arrFileName[0].substring(1,3);
////        funCreateTempFolder();
////        String filePath = System.getProperty("user.dir");
////        File file = new File(filePath + "/Temp/"+fileName+".txt");
////        PrintWriter pw;
//        try 
//		{
//			//pw = new PrintWriter(file);
//        	JSONArray jArrData=new JSONArray();
//        String text="";
//        String sbSql="";
//        
//        Map<Integer,List<String>> hmSalesData=new HashMap<Integer,List<String>>();
//        
//      //  sbSql.setLength(0);
//        sbSql="select a.strPOSCode,a.strBillNo,b.strItemCode,sum(b.dblQuantity),b.dblRate,a.strSettelmentMode "
//            + " from tblbillhd a,tblbilldtl b,tblitemmaster c "
//            + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '"+fromDate+"' and b.strItemCode=c.strItemCode ";
//        if(!POSCode.equals("All"))
//        {
//            sbSql=sbSql + " and a.strPOSCode='"+POSCode+"' ";
//        }
//        sbSql=sbSql + " and c.strRevenueHead='Liquor' ";
//        sbSql=sbSql + " group by b.strItemCode order by a.strBillNo ";
//        Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql);
//        List rsSaleData = query.list();
//        
//        if(rsSaleData!=null)
//        {
//        	for(int i=0; i<rsSaleData.size(); i++)
//			{
//				Object[] obj = (Object[]) rsSaleData.get(i);
//				
//				JSONObject objSaleDataDtl=new JSONObject();
//				
//				String strPOSCode=obj[0].toString();
//				String strBillNo=obj[1].toString(); 
//				String strItemCode=obj[2].toString();  
//				String dblQuantity=obj[3].toString();  
//				String dblRate=obj[4].toString();  
//				 
//             text=strPOSCode+" "+strBillNo+" "+strItemCode+" "+dblQuantity+" "+dblRate;
//             objSaleDataDtl.put("stringDetails",text);
//			 jArrData.put(objSaleDataDtl);
////            pw.println(text);
//        }
//        	jObjData.put("listOfSaleData", jArrData);
//        }
//        
//       
//        sbSql= " select a.strPOSCode,a.strBillNo,b.strItemCode,sum(b.dblQuantity),b.dblRate,a.strSettelmentMode "
//            + " from tblqbillhd a,tblqbilldtl b,tblitemmaster c "
//            + " where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '"+fromDate+"' and b.strItemCode=c.strItemCode ";
//        if(!POSCode.equals("All"))
//        {
//            sbSql=sbSql + " and a.strPOSCode='"+POSCode+"' ";
//        }
//        sbSql=sbSql + " and c.strRevenueHead='Liquor' ";
//        sbSql=sbSql + " group by b.strItemCode order by a.strBillNo";
//        
//      query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql);
//      List rsSaleData1 = query.list();
//        if(rsSaleData1!=null)
//        {
//        	for(int i=0; i<rsSaleData1.size(); i++)
//			{
//				Object[] obj = (Object[]) rsSaleData1.get(i);
//				
//				JSONObject objSaleDataDtl=new JSONObject();
//				
//				String strPOSCode=obj[0].toString();
//				String strBillNo=obj[1].toString(); 
//				String strItemCode=obj[2].toString();  
//				String dblQuantity=obj[3].toString();  
//				String dblRate=obj[4].toString();
//				 text=strPOSCode+" "+strBillNo+" "+strItemCode+" "+dblQuantity+" "+dblRate;
//				 objSaleDataDtl.put("stringDetails",text);
//				 jArrData.put(objSaleDataDtl);
////             pw.println(text);
//        }
//        	jObjData.put("listOfSaleData", jArrData);
//        }
//        
//        
//        
////        Desktop dt = Desktop.getDesktop();
////        dt.open(file);
//		} 
//        catch(Exception e)
//        {
//     	   e.printStackTrace();
//        }
//		return jObjData;  
//    }
    
    
//    public static final int BYTES_DOWNLOAD = 1024;
//	
//    
//   public JSONObject funGenerateMenuItemCodeFile() 
//    {
//    	HttpServletRequest request;
//        
//
//    	
////        String filePath = System.getProperty("user.dir");
////        File file = new File(filePath + "/Temp/"+fileName+".txt");
////        PrintWriter pw = new PrintWriter(file);
//        
//        String sbSql;
//        String text="",strItemCode="",strItemName="";
//        JSONObject jObjData=new JSONObject();
//   try
//   {
//	   JSONArray jArrData=new JSONArray();
//	   
//        sbSql="select strItemCode,strItemName from tblitemmaster where strRevenueHead='Liquor' ";
//       Query  query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql);  
//       List rsMenuItemCodeData = query.list();
//       if(rsMenuItemCodeData!=null)
//       {
//       	for(int i=0; i<rsMenuItemCodeData.size(); i++)
//			{
//				Object[] obj = (Object[]) rsMenuItemCodeData.get(i);
//				
//				
//				JSONObject objDeliverBoy=new JSONObject();
//				 strItemCode=obj[0].toString();
//				 strItemName=obj[1].toString();
//				 text=strItemCode+" "+strItemName;
//				 objDeliverBoy.put("stringDtl",text);
//				 jArrData.put(objDeliverBoy);
//	           // pw.println(text);
////	            os.write(bytes, 0, read);
//			}
//       	jObjData.put("list", jArrData);
//        //text+=text;
//       }
//   }
//   catch(Exception e)
//   {
//	   e.printStackTrace();
//   }
//	return jObjData;
//
//       
//   		
//   	}
//       
                
      
        
//        Desktop dt = Desktop.getDesktop();
//        dt.open(file);
        
    }

