package com.apos.dao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.controller.clsUtilityController;
import com.google.gson.JsonArray;
import com.webservice.util.clsItemDtlForTax;
import com.webservice.util.clsTaxCalculationDtls;


@Repository("clsVoidKotDao")
@Transactional(value = "webPOSTransactionManager")
public class clsVoidKotDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired
	clsUtilityController objUtility;
	String manualKOTNo=""; 
	
	public JSONObject funLoadTable(String strPosCode)
	{
		JSONObject tableData = new JSONObject();
		JSONArray jArr=new JSONArray();
		try{
		String sqlFillCombo = "select b.strTableNo,a.strTableName "
                + "from tbltablemaster a,tblitemrtemp b "
                + "where a.strTableNo=b.strTableNo "
                + "and  (a.strPOSCode='" + strPosCode + "' OR a.strPOSCode='All') and strNCKotYN='N'  "
                + "group by b.strTableNo "
                + "order by a.strTableName;";
		
		
		   Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlFillCombo.toString());
	  	    List listSql1= querySql1.list();
	  	  if(listSql1.size()>0)
	  	      {
	  		
	  	    		
	    		for(int j=0 ;j<listSql1.size();j++ )
	  	    	{
	    		JSONObject jObj=new JSONObject();
	  	    	Object[] obj1 = (Object[]) listSql1.get(j);
	  	    	jObj.put("strTableNo",obj1[0].toString());
	  	    	jObj.put("strTableName",obj1[1].toString());
	  	    	jArr.put(jObj);
	  	        }
	    		
	  		
 	    }
	  	  tableData.put("jArr", jArr);
		}catch(Exception e)
		{
			
		}
		return tableData;
	}

	
	public JSONObject funFillHelpGrid(String tableName,String tableNo,String strPosCode)
	{
		JSONObject jObjReturn=new JSONObject();
		JSONArray jArr=new JSONArray();
		String sql="";
	
		try
        {
            sql = "select a.strKOTNo,a.strTableNo,b.strTableName,c.strWShortName"
                    + ",a.strPrintYN,a.strTakeAwayYesNo,a.strUserCreated,a.intPaxNo"
                    + ",sum(a.dblAmount),a.strManualKOTNo "
                    + "from tblitemrtemp a left outer join tbltablemaster b "
                    + "on a.strTableNo=b.strTableNo  left outer join  tblwaitermaster c "
                    + "on a.strWaiterNo=c.strWaiterNo  "
                    + "where a.strTableNo=b.strTableNo and a.strNCKotYN='N' and a.strPOSCode='" + strPosCode + "'";
            if (!tableName.toString().equals("All Tables"))
            {
//                String tbNo = mapTableCombo.get(ctableName.toString());
                sql += " and a.strTableNo='" + tableNo + "'";
            }
            sql += " group by a.strKOTNo";
         

            Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
	  	    List listSql1= querySql1.list();
	  	    if(listSql1.size()>0)
	  	      {
	    		for(int j=0 ;j<listSql1.size();j++ )
	  	    	{
	    		JSONObject jObj=new JSONObject();
	  	    	Object[] obj1 = (Object[]) listSql1.get(j);
            
	  	    	jObj.put("strKOTNo", obj1[0].toString());
	  	    	jObj.put("strTableNo", obj1[1].toString());
	  	    	jObj.put("strTableName", obj1[2].toString());
	  	    	jObj.put("strWShortName", obj1[3].toString());
	  	    	jObj.put("strPrintYN", obj1[4].toString());
	  	    	jObj.put("strTakeAwayYesNo", obj1[5].toString());
	  	    	jObj.put("strUserCreated", obj1[6].toString());
	  	    	jObj.put("intPaxNo",Integer.parseInt( obj1[7].toString()));
	  	    	jObj.put("dblAmount", Double.parseDouble(obj1[8].toString()));
	  	    	jObj.put("strManualKOTNo", obj1[9].toString());
	  	    	jArr.put(jObj);
	  	    	manualKOTNo=obj1[9].toString();
	  	    	
//                String KotNo = rs.getString("a.strKOTNo");
//                String strTableName = rs.getString("b.strTableName");
//                String waiterName = rs.getString("c.strWShortName");
//                String takeAway = rs.getString("a.strTakeAwayYesNo");
//                String user = rs.getString("a.strUserCreated");
//                String pax = rs.getString("a.intPaxNo");
//                double dblAmount = rs.getDouble("sum(a.dblAmount)");
//                manualKOTNo = rs.getString("a.strManualKOTNo");
       
            }
	  	    }
	  	  
	  	jObjReturn.put("jArr",jArr);
        }
        catch (Exception e)
        {
         
            e.printStackTrace();
        }
		
		return jObjReturn;
	}
	
	
	public JSONObject fillItemTableData(String KotNo,String tableNo1,String strPosCode,String tableName)
	{
		JSONObject jObjReturn=new JSONObject();
		JSONArray jArr=new JSONArray();
		String sql="";
		String POSDate = "";
        String POSCode = "";
        String tableNo="";
        String areaCode="";
		try
        {

	  sql = "select count(*) from tblitemrtemp "
            + "where strKOTNo='" + KotNo + "' and strPOSCode='" + strPosCode + "' and strNCKotYN='N' ";
       
	   Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
 	    List listSql1= querySql1.list();
 	   
   		   JSONObject jObj=new JSONObject();
 	    	Object obj1 = (Object) listSql1.get(0);
 	    
      
       int cnt =Integer.parseInt(obj1.toString());
       if (cnt > 0)
       {
        sql = "select strItemName,dblItemQuantity,dblAmount,strUserCreated,dteDateCreated,strItemCode"
                + " ,strPOSCode,strTableNo "
                + " from tblitemrtemp "
                + " where strKOTNo='" + KotNo + "' and strPOSCode='" + strPosCode + "' and strNCKotYN='N' ";
        double subTotalAmt = 0.00;
        
        

        List<clsPOSItemDetailFrTaxBean> arrListItemDtl = new ArrayList<clsPOSItemDetailFrTaxBean>();
        
        Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
  	    List listSql= querySql.list();
  	    if(listSql.size()>0)
  	     {
    	  for(int j=0 ;j<listSql.size();j++ )
  	       {
        
    		JSONObject jObj1=new JSONObject();
   	    	Object[] obj = (Object []) listSql.get(j);
   	  
            jObj1.put("strItemName", obj[0].toString()) ; 
            jObj1.put("dblItemQuantity", Double.parseDouble( obj[1].toString())) ; 
            jObj1.put("dblAmount", Double.parseDouble(obj[2].toString()) ); 
            jObj1.put("strUserCreated", obj[3].toString()) ; 
            jObj1.put("dteDateCreated", obj[4].toString()) ; 
            jObj1.put("strItemCode", obj[5].toString()) ;
            jObj1.put("POSCode", obj[6].toString()) ; 
            jObj1.put("tableNo", obj[7].toString()) ; 
            
   	    	
            String itemName = obj[0].toString();
            double dblQuantity = Double.parseDouble( obj[1].toString());
            double dblAmount = Double.parseDouble(obj[2].toString());
            String itemCode = obj[3].toString();
            subTotalAmt = subTotalAmt + dblAmount;
//            
            POSDate = obj[4].toString();
            POSCode = obj[6].toString();
            tableNo =obj[7].toString();

            jArr.put(jObj1);   
            
            clsPOSItemDetailFrTaxBean objItemDtlForTax = new clsPOSItemDetailFrTaxBean();
            objItemDtlForTax.setItemCode(itemCode);
            objItemDtlForTax.setItemName(itemName);
            objItemDtlForTax.setAmount(dblAmount);
            objItemDtlForTax.setDiscAmt(0);
            objItemDtlForTax.setDiscPer(0);
            arrListItemDtl.add(objItemDtlForTax);
  	       }
    	  }
  	       
  	  sql = "select strAreaCode from tbltablemaster where strTableNo='" + tableNo + "';";
        Query querySqlAreaCode = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
  	    List listSqlAreaCode= querySqlAreaCode.list();
 	    if(!listSqlAreaCode.isEmpty())
 	    {
  	    Object objAreaCode=(Object)listSqlAreaCode.get(0);
        areaCode = objAreaCode.toString();
 	    }
         
         
        double taxAmt = 0;
        List<clsTaxCalculationDtls> arrListTaxDtl = objUtility.funCalculateTax(arrListItemDtl, POSCode, POSDate, areaCode, "DineIn", subTotalAmt, 0, "Void KOT", "Cash");
        for (clsTaxCalculationDtls objTaxDtl : arrListTaxDtl)
        {
            taxAmt += objTaxDtl.getTaxAmount();
        }
        arrListTaxDtl = null;
      double totalAmount=  subTotalAmt + taxAmt;
       
        jObjReturn.put("totalAmount", totalAmount);        
        jObjReturn.put("taxAmt", taxAmt);        
        jObjReturn.put("subTotalAmt", subTotalAmt);
        
        
        jObjReturn.put("jArr", jArr);
//        lblTaxValue.setText(String.valueOf(Math.rint(taxAmt)));
//        lblSubTotalValue.setText(Double.toString(subTotalAmt));
//        lblTotalAmt.setText(String.valueOf(Math.rint(subTotalAmt + taxAmt)));
//        tblItemTable.setModel(dm);
 
//        tblItemTable.setShowHorizontalLines(true);
//        tblItemTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        tblItemTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
//        tblItemTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
//        tblItemTable.getColumnModel().getColumn(0).setPreferredWidth(230);
//        tblItemTable.getColumnModel().getColumn(1).setPreferredWidth(40);
//        tblItemTable.getColumnModel().getColumn(2).setPreferredWidth(83);
//        delItemName.removeAllElements();
//        delItemCode.removeAllElements();
    }
        }
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		 return jObjReturn;
}

	public JSONObject funDoneButtonClick(JSONObject jservice,String resonCode,String selectedTableNo,String lblKOTNo,String remarks,
			String userCode,String clientCode,String voidedDate,double dblTax) {

		JSONObject jObjReturn =new JSONObject();
		
		
		
		try{
			JSONArray jArr=(JSONArray)jservice.getJSONArray("jarr1");
		String sql="";
		double singleItemAmount,voidedAmount;
		double voidedQty = 0;
		
		String [] delItemCode = new String[jArr.length()];;
		String [] delItemQuantity= new String[jArr.length()];;;
		String [] amount= new String[jArr.length()];;;
		String [] delItemName= new String[jArr.length()];;;
		
		for(int i=0;i<jArr.length();i++){
		JSONObject jsondata=jArr.getJSONObject(i);
		
			
		 delItemCode[i]=jsondata.getString("itemCode").toString();
		delItemQuantity[i]=jsondata.getString("qty").toString();
		amount[i]=jsondata.getString("amount").toString();
		delItemName[i]=jsondata.getString("itemName").toString();
		}
	
		
		
		
		
		
		String sqlQuery = "select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='VoidKot'";
		 for (int cnt = 0; cnt < delItemCode.length; cnt++)
         {
             double qtyAfterDelete = 0;
             String strType = "DVKot";
             sql = "select strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,"
                     + "dblAmount,strWaiterNo,strKOTNo,intPaxNo,strUserCreated,dteDateCreated "
                     + "from tblitemrtemp where strItemCode='" + delItemCode[cnt] + "' "
                     + "and strKOTNo='" + lblKOTNo + "' and strTableNo='" + selectedTableNo + "' and strNCKotYN='N' ";

            
             
             Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
       	    List listSql= querySql.list();
       	    if(listSql.size()>0)
       	     {
         	 
         		
        	    Object[] obj = (Object []) listSql.get(0);
         
            
             qtyAfterDelete = Double.parseDouble(obj[4].toString()) - Double.parseDouble(delItemQuantity[cnt].toString());
             singleItemAmount = Double.parseDouble(obj[5].toString())
                     / Double.parseDouble(obj[4].toString());
             if (qtyAfterDelete > 0)
             {
                 voidedQty = Double.parseDouble(delItemQuantity[cnt].toString());
                 voidedAmount = Double.parseDouble(amount[cnt].toString());
             }
             else
             {
                 voidedQty = Double.parseDouble(delItemQuantity[cnt].toString());
                 voidedAmount = voidedAmount = Double.parseDouble(amount[cnt].toString());
             }

             sql = "insert into tblvoidkot(strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,"
                     + "dblAmount,strWaiterNo,strKOTNo,intPaxNo,strType,strReasonCode,"
                     + "strUserCreated,dteDateCreated,dteVoidedDate,strClientCode,strRemark) "
                     + "values('" + obj[0].toString() + "','" +obj[1].toString() + "'"
                     + ",'" +obj[2].toString()+ "','" + obj[3].toString() + "',"
                     + "'" + voidedQty + "','" + voidedAmount + "','" + obj[6].toString() + "'"
                     + ",'" +obj[7].toString() + "'," + "'" + obj[8].toString() + "'"
                     + ",'" + strType + "','" + resonCode + "','" + userCode + "'"
                     + ",'" + obj[10].toString() + "'," + "'" + voidedDate + "'" + ""
                     + ",'" + clientCode + "','" + remarks + "' ) ";

             int exc = 0;
             if (userCode.equalsIgnoreCase("super")||userCode.equalsIgnoreCase("sanguine"))
             {
            	 exc= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();

             }
             else
             {                            
                Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
                List list=query.list();
                for(int i=0;i<list.size();i++){
                
                	Object[] obj1=(Object[])list.get(i);

                	if (Boolean.parseBoolean(obj1[2].toString()))
                     {
                		 exc= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
                     }
                 
             }
             }
             sql = "Update tblnonchargablekot set dblQuantity='" + qtyAfterDelete + "' where strKOTNo='" + obj[7].toString() + "' and strItemCode='" + obj[2].toString() + "' ";

             Query query = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     		 query.executeUpdate();
             if (qtyAfterDelete == 0)
             {
                 sql = "Delete from tblnonchargablekot where strKOTNo='" +obj[7].toString()  + "' and strItemCode='" + obj[2].toString()  + "' ";

                 Query queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                 queryDel.executeUpdate();
             }
             if (exc > 0)
             {
                 if (qtyAfterDelete > 0)
                 {
                     sql = "insert into tbltempvoidkot(strItemName,strItemCode,dblItemQuantity,strTableNo,strWaiterNo,strKOTNo) "
                             + "values('" + obj[3].toString() + "','"
                             + delItemCode[cnt].toString() + "','" + delItemQuantity[cnt].toString() + "','" + obj[0].toString() + "','" + obj[6].toString() + "','" + obj[7].toString() + "')";

                    webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
                     sql = "update tblitemrtemp set dblItemQuantity=" + qtyAfterDelete + ", "
                             + " dblAmount=" + (singleItemAmount * qtyAfterDelete) + ", dblTaxAmt='" + dblTax + "' "
                             + " where strItemCode='" +  delItemCode[cnt].toString()  + "'"
                             + " and strKOTNo='" + lblKOTNo+ "'  and strNCKotYN='N' ";

                     Query queryupdate = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                     queryupdate.executeUpdate();
                 }
                 else
                 {
                     sql = "insert into tbltempvoidkot(strItemName,strItemCode,dblItemQuantity,strTableNo,strWaiterNo,strKOTNo) "
                             + "values('" +  obj[3].toString() + "','" +  obj[2].toString() + "','"
                             + Double.parseDouble(obj[4].toString()) + "','" + obj[0].toString()  + "','" + obj[6].toString()  + "','" +obj[7].toString()  + "')";
//                     clsGlobalVarClass.dbMysql.execute(sql);
                     webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();

                     sql = "delete from tblitemrtemp where left(strItemCode,7)='" +delItemCode[cnt]+ "' "
                             + " and strKOTNo='" + lblKOTNo + "' and strTableNo='" + selectedTableNo + "' and strNCKotYN='N' ";
//                     clsGlobalVarClass.dbMysql.execute(sql);
                     Query queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                     queryDel.executeUpdate();
                     sql = "select count(*) from tblitemrtemp where strTableNo='" + selectedTableNo + "'  and strNCKotYN='N' ";
                 
                     
                     Query querySqlDel = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
            	    List listSql1= querySql.list();
            	    if(listSql1.size()>0)
            	    {
                	 Object objdel = (Object) listSql1.get(0);
                     if (Integer.parseInt(objdel.toString()) == 0)
                     {
                         sql = "update tbltablemaster set strStatus='Normal',intPaxNo=0 "
                                 + "where strTableNo='" + selectedTableNo + "'";
//                         clsGlobalVarClass.dbMysql.execute(sql);
                         Query queryupdate = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                         queryupdate.executeUpdate();
                     }
                	}
                }
             }
       	   }
       	 jObjReturn.put("true", "true");
       	   
         }
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		 return jObjReturn;
	}


public JSONObject funClickFullVoidKot(String favoritereason,String Kot,String strClientCode,String voidedDate,String userCode,String voidKOTRemark) throws JSONException
{
	JSONObject jObjReturn=new JSONObject();
	 int exc = 0;
	 String sql="";
	 String reasonCode="";
	 String sqlQuery = "select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='VoidKot'";
	
	 
	    if (favoritereason != null)
        {
            sql = "select strReasonCode from tblreasonmaster where strReasonName='" + favoritereason + "'";
            Query querySqlReason = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
       	    List listSqlReason= querySqlReason.list();
       	    if(listSqlReason.size()>0)
       	     {
       	     Object obj = (Object) listSqlReason.get(0);
       	    reasonCode = obj.toString();
            }

    }
	 
	 if (!"".equals(reasonCode))
     {
         String strType = "VKot";
         
         sql = "select strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,dblAmount,strWaiterNo,"
                 + "strKOTNo,intPaxNo,strUserCreated,dteDateCreated from tblitemrtemp "
                 + "where strKOTNo='" + Kot + "' and strNCKotYN='N' ";
    
         Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
    	    List listSql= querySql.list();
    	    if(listSql.size()>0)
    	     {
      	    for(int i=0;i<listSql.size();i++)
      	    {
      		
     	    Object[] obj = (Object []) listSql.get(i);
       
             sql = "insert into tbltempvoidkot(strItemName,strItemCode,dblItemQuantity,strTableNo,strWaiterNo,strKOTNo) "
                     + "values('" + obj[3].toString()+ "','" +  obj[2].toString() + "','"
                     + Double.parseDouble(obj[4].toString()) + "','" + obj[0].toString()+ "','" + obj[6].toString() + "'"
                     + ",'" + obj[7].toString() + "')";
             //System.out.println(sql);
             exc= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();

             sql = "insert into tblvoidkot(strTableNo,strPOSCode,strItemCode,strItemName,dblItemQuantity,"
                     + "dblAmount,strWaiterNo,strKOTNo,intPaxNo,strType,strReasonCode,strUserCreated,"
                     + "dteDateCreated,dteVoidedDate,strClientCode,strManualKOTNo,strRemark) "
                     + "values('" + obj[0].toString() + "','" +  obj[1].toString() + "'"
                     + ",'" +  obj[2].toString() + "','" +  obj[3].toString() + "'"
                     + "," + "'" +  Double.parseDouble(obj[4].toString()) + "','" +  Double.parseDouble(obj[5].toString()) + "'"
                     + ",'" +  obj[6].toString() + "','" +  obj[7].toString() + "'"
                     + "," + "'" +  obj[8].toString()+ "','" + strType + "','" + reasonCode + "'"
                     + ",'" + userCode + "','" +  obj[10].toString() + "'"
                     + "," + "'" + voidedDate + "','" + strClientCode + "','" + manualKOTNo + "','" + voidKOTRemark + "' ) ";
             
             if (userCode.equalsIgnoreCase("super")||userCode.equalsIgnoreCase("sanguine"))
             {
            	 exc= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
             }
             else
             {      
                 Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
         	    List listSql1= querySql1.list();
         	    if(listSql1.size()>0)
         	     {
          	         Object[] obj1 = (Object []) listSql1.get(0);                 
                     if (Boolean.parseBoolean(obj1[0].toString()))
                     {
                    	 exc= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
                     }
                 }
             }

             sql = "Delete from tblnonchargablekot where strKOTNo='" + Kot + "' ";
             Query queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
             queryDel.executeUpdate();
         }
         }
         if (exc > 0)
         {
//             funResetField();
//             funRemotePrint();
//             funFillHelpGrid();
         }
         jObjReturn.put("sucessfully", "sucessfully");
         
     }
     return jObjReturn;
}




public JSONObject funLoadReson()
{
	JSONObject jObjReturn=new  JSONObject(); 
	JSONArray jarr=new JSONArray();
    String favoritereason = null;
    try
    {
    	String sql="";
    	String[] arrReason;
        int reasoncount = 0, i = 0;
        String resonCode="";
        sql = "select count(strReasonName) from tblreasonmaster where strKot='Y'";
        Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
   	    List listSql= querySql.list();
   	    if(listSql.size()>0)
   	     {
     	    for(int cnt=0;cnt<listSql.size();cnt++)
     	    {
     		
    	    Object obj = (Object) listSql.get(cnt);
           reasoncount = Integer.parseInt(obj.toString());
            }
   	     }
        if (reasoncount > 0)
        {
            arrReason = new String[reasoncount];
            sql = "select strReasonName from tblreasonmaster where strKot='Y'";
            Query querySqlReason = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
       	    List listSqlReason= querySqlReason.list();
       	    if(listSqlReason.size()>0)
       	     {
         	    for(int j=0;j<listSqlReason.size();j++)
         	    {
         		JSONObject jobj=new  JSONObject();
        	    Object obj = (Object ) listSqlReason.get(j);
        	    i = 0;
        	    jobj.put("resoncode",obj.toString() );
        	    jarr.put(jobj); 
//        	    arrReason[i] =obj[0].toString();
//                i++;
                }
       	     }
           
            jObjReturn.put("jArr", jarr);
        }
    }
    catch (Exception e)
    {
      
        e.printStackTrace();
    }
    return jObjReturn;
}

}
