package com.apos.dao;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsBillDiscountDtl;
import com.apos.bean.clsBillDtl;
import com.apos.bean.clsBillHd;
import com.apos.bean.clsBillModifierDtl;
import com.apos.bean.clsBillTaxDtl;
import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.bean.clsTaxCalculationBean;
import com.apos.bean.clsVoidBillDtl;
import com.apos.bean.clsVoidBillHd;
import com.apos.bean.clsVoidBillModifierDtl;
import com.apos.controller.clsUtilityController;
import com.apos.service.clsSetupService;


@Repository("clsVoidBillDao")
@Transactional(value = "webPOSTransactionManager")
public class clsVoidBillDao {
	@Autowired
	private SessionFactory webPOSSessionFactory;
	@Autowired 
	private clsSetupService objSetupService;
	@Autowired 
	private clsUtilityController obj ;
	@Autowired
	private clsSetupService objSetUpService;
	
	   List<clsBillDtl> arrListBillDtl = new ArrayList<clsBillDtl>();
       List<clsBillHd> arrListBillHd= new ArrayList<clsBillHd>();
       List<clsBillModifierDtl> arrListBillModifierDtl= new ArrayList<clsBillModifierDtl>();
       List<clsBillDiscountDtl> arrListBillDiscDtl= new ArrayList<clsBillDiscountDtl>();
       List<clsVoidBillDtl> arrListVoidBillDtl= new ArrayList<clsVoidBillDtl>();
       List<clsVoidBillHd> arrListVoidBillHd= new ArrayList<clsVoidBillHd>();
       List<clsVoidBillModifierDtl> arrListVoidBillModifierDtl= new ArrayList<clsVoidBillModifierDtl>();
       List<clsBillDtl> arrListKOTWiseBillDtl= new ArrayList<clsBillDtl>() ;
       List<clsBillTaxDtl> arrListBillTaxDtl= new ArrayList<clsBillTaxDtl>();
      
    	
	
	 String areaCode="";
	 String operationTypeForTax="";
	  int intShiftNo = 0;
	  
	public JSONObject funLoadBillGrid(String dtPOSDate,String strPOSCode,String searchBillNo)
	{
		JSONObject objReturn =new JSONObject();
		JSONArray jArr=new JSONArray();
		String sql="";
		try{
	        sql = "select a.strBillNo,CONCAT(DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),' ',TIME_FORMAT(time(a.dteBillDate),'%h:%i')) as dteBillDate,a.strSettelmentMode,a.dblTaxAmt,a.dblSubTotal,a.dblGrandTotal"
                    + ",a.strUserCreated,b.strTableName "
                    + " from tblbillhd a left outer join tbltablemaster b "
                    + " on a.strTableNo=b.strTableNo "
                    + " where date(a.dteBillDate)='" + dtPOSDate+ "' "
                    + " and a.strPOSCode='" + strPOSCode + "' "
                    + " and a.strBillNo NOT IN(select b.strBillNo from tblbillsettlementdtl b) ";
            if (searchBillNo.length() > 0)
            {
                sql += " and a.strBillNo LIKE '" + searchBillNo + "%'  or a.strBillNo LIKE '%" + searchBillNo + "' ";
            }
            
            sql+="  and a.strSettelmentMode='' ";
        sql+="  and a.strSettelmentMode='' ";
        
        Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
   	    List listSql= querySql.list();
   	    if(listSql.size()>0)
   	     {
     	    for(int cnt=0;cnt<listSql.size();cnt++)
     	    {
     		
    	    Object[] obj = (Object[]) listSql.get(cnt);
            JSONObject jObj=new JSONObject(); 
//            a.strSettelmentMode,a.dblTaxAmt,a.dblSubTotal,a.dblGrandTotal"
//            rs.getString(1), rs.getString(2), rs.getString(6), rs.getString(8)
            jObj.put("strBillNo",obj[0].toString() );
            jObj.put("dteBillDate",obj[1].toString() );
            jObj.put("dblGrandTotal",obj[5].toString()  );
            jObj.put("strTableName",obj[7].toString() );
//            jObj.put("strTableName",obj[7].toString() );
            jArr.put(jObj);
     	    }
     	    }
   	    objReturn.put("jArr",jArr);
     	    }catch(Exception e)
     	    {
     	    	e.printStackTrace();
     	    }           
              return objReturn;        
     	    }

	public JSONObject funSelectBill(String billNo,String strClientCode, String strPOSCode,String posDate)
    {
	
		JSONObject jObjRrturn=new JSONObject();
	 try
       {
         
    	 String columnName="gDirectAreaCode";
    	 JSONObject jobj=objSetupService.funGetParameterValuePOSWise(strClientCode, strPOSCode,columnName);
    	 String gDirectAreaCode=jobj.get("gDirectAreaCode").toString();
         String sql="";
       
        
      
       
         sql = "select a.strOperationType,a.strTableNo,ifnull(b.strAreaCode,'') "
                  + "from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                  + "where strBillNo='" + billNo + "'";
          
          Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
     	    List listSql= querySql.list();
     	    if(listSql.size()>0)
     	     {
       	    for(int cnt=0;cnt<listSql.size();cnt++)
       	    {
       		
      	    Object[] obj = (Object[]) listSql.get(cnt);
          

              operationTypeForTax = "DineIn";
              if (obj[0].toString().equals("Home Delivery"))
              {
                  operationTypeForTax = "HomeDelivery";
              }
              if (obj[0].toString().equals("Take Away"))
              {
                  operationTypeForTax = "TakeAway";
              }
              if (obj[1].toString().trim().length() > 0)
              {
                  areaCode = obj[2].toString();
              }
              else
              {
                  areaCode = gDirectAreaCode;
              }
          }
     	  }
         
          arrListBillDtl.clear();
          arrListBillHd.clear();
          arrListBillModifierDtl.clear();
          arrListVoidBillHd.clear();
          arrListVoidBillDtl.clear();
          arrListVoidBillModifierDtl.clear();
          arrListBillDiscDtl.clear();
          arrListKOTWiseBillDtl.clear();

          sql = "select * from tblbillhd "
                  + "where strBillNo='" + billNo + "'";
        
        Query querySqlBillHd = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
   	    List listSqlBillHd= querySqlBillHd.list();
   	    if(listSqlBillHd.size()>0)
   	     {
     	    for(int cnt=0;cnt<listSqlBillHd.size();cnt++)
     	    {
     		
    	    Object[] obj = (Object[]) listSqlBillHd.get(cnt);
          
      
              clsBillHd objBillHd = new clsBillHd();
              objBillHd.setStrBillNo(obj[0].toString());
              objBillHd.setStrAdvBookingNo(obj[1].toString());
              objBillHd.setDteBillDate(obj[2].toString());
              objBillHd.setStrPOSCode(obj[3].toString());
              objBillHd.setStrSettelmentMode(obj[4].toString());
              objBillHd.setDblDiscountAmt(Double.parseDouble(obj[5].toString()));
              objBillHd.setDblDiscountPer(Double.parseDouble(obj[6].toString()));
              objBillHd.setDblTaxAmt(Double.parseDouble(obj[7].toString()));
              objBillHd.setDblSubTotal(Double.parseDouble(obj[8].toString()));
              objBillHd.setDblGrandTotal(Double.parseDouble(obj[9].toString()));
              objBillHd.setStrTakeAway(obj[10].toString());
              objBillHd.setStrOperationType(obj[11].toString());
              objBillHd.setStrUserCreated(obj[12].toString());
              objBillHd.setStrUserEdited(obj[13].toString());
              objBillHd.setDteDateCreated(obj[14].toString());
              objBillHd.setDteDateEdited(obj[15].toString());
              objBillHd.setStrClientCode(obj[16].toString());
              objBillHd.setStrTableNo(obj[17].toString());
              objBillHd.setStrWaiterNo(obj[18].toString());
              objBillHd.setStrCustomerCode(obj[19].toString());
              objBillHd.setStrManualBillNo(obj[20].toString());
              objBillHd.setIntShiftCode(Integer.parseInt(obj[21].toString()));
              intShiftNo = Integer.parseInt(obj[21].toString());
              objBillHd.setIntPaxNo(Integer.parseInt(obj[22].toString()));
              objBillHd.setStrDataPostFlag(obj[23].toString());
              objBillHd.setStrReasonCode(obj[24].toString());
              objBillHd.setStrRemarks(obj[25].toString());
              objBillHd.setDblTipAmount(Double.parseDouble(obj[26].toString()));
              objBillHd.setDteSettleDate(obj[27].toString());
              objBillHd.setStrCounterCode(obj[28].toString());
              objBillHd.setDblDeliveryCharges(Double.parseDouble(obj[29].toString()));
              objBillHd.setStrCouponCode(obj[30].toString());
              objBillHd.setStrAreaCode(obj[31].toString());
              objBillHd.setStrDiscountRemark(obj[32].toString());
              objBillHd.setStrTakeAwayRemarks(obj[33].toString());
              objBillHd.setStrDiscountOn(obj[34].toString());
              arrListBillHd.add(objBillHd);
          }
   	     }

          sql = "select a.strItemCode,a.strItemName,a.strBillNo,a.strAdvBookingNo,a.dblRate,sum(a.dblQuantity) "
                  + ",sum(a.dblAmount),sum(a.dblTaxAmount),a.dteBillDate,a.strKOTNo,a.strClientCode,IFNULL(a.strCustomerCode,'') "
                  + ",a.tmeOrderProcessing,a.strDataPostFlag,a.strMMSDataPostFlag,a.strManualKOTNo,a.tdhYN "
                  + ",a.strPromoCode,a.strCounterCode,a.strWaiterNo,a.dblDiscountAmt,a.dblDiscountPer,b.strSubGroupCode "
                  + ",c.strSubGroupName,c.strGroupCode,d.strGroupName "
                  + "from tblbilldtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
                  + "where a.strBillNo='" + billNo + "'  "
                  + "and a.strItemCode=b.strItemCode "
                  + "and b.strSubGroupCode=c.strSubGroupCode "
                  + "and c.strGroupCode=d.strGroupCode "
                  + "group by a.strItemCode,a.strItemName,a.strBillNo;";
          
          Query querySqlBillDtl = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
     	    List listSqlBillDtl= querySqlBillDtl.list();
     	    if(listSqlBillDtl.size()>0)
     	     {
       	    for(int cnt=0;cnt<listSqlBillDtl.size();cnt++)
       	    {
      	    Object[] obj = (Object[]) listSqlBillDtl.get(cnt);
           
              clsBillDtl objBillDtl = new clsBillDtl();
              String itemCode =obj[0].toString();
              objBillDtl.setStrItemCode(obj[0].toString());
              objBillDtl.setStrItemName(obj[1].toString());
              objBillDtl.setStrBillNo(obj[2].toString());
              objBillDtl.setStrAdvBookingNo(obj[3].toString());
              objBillDtl.setDblRate(Double.parseDouble(obj[4].toString()));
              objBillDtl.setDblQuantity(Double.parseDouble(obj[5].toString()));
              objBillDtl.setDblAmount(Double.parseDouble(obj[6].toString()));
              objBillDtl.setDblTaxAmount(Double.parseDouble(obj[7].toString()));
              objBillDtl.setDteBillDate(obj[8].toString());
              objBillDtl.setStrKOTNo(obj[9].toString());
              objBillDtl.setStrClientCode(obj[10].toString());
              objBillDtl.setStrCustomerCode(obj[11].toString());
              objBillDtl.setTmeOrderProcessing(obj[12].toString());
              objBillDtl.setStrDataPostFlag(obj[13].toString());
              objBillDtl.setStrMMSDataPostFlag(obj[14].toString());
              objBillDtl.setStrManualKOTNo(obj[15].toString());
              objBillDtl.setTdhYN(obj[16].toString());
              objBillDtl.setStrPromoCode(obj[17].toString());
              objBillDtl.setStrCounterCode(obj[18].toString());
              objBillDtl.setStrWaiterNo(obj[19].toString());
              objBillDtl.setDblDiscountAmt(Double.parseDouble(obj[20].toString()));
              objBillDtl.setDblDiscountPer(Double.parseDouble(obj[21].toString()));
              objBillDtl.setSubGrouName(obj[23].toString());
              objBillDtl.setGroupName(obj[25].toString());
              arrListBillDtl.add(objBillDtl);

              sql = "select a.strBillNo,a.strItemCode,a.strModifierCode,a.strModifierName "
                      + ",a.dblRate,sum(a.dblQuantity),sum(a.dblAmount),a.strClientCode,IFNULL(a.strCustomerCode,'') "
                      + ",a.strDataPostFlag,a.strMMSDataPostFlag,sum(a.dblDiscPer),sum(a.dblDiscAmt) "
                      + ",b.strSubGroupCode ,c.strSubGroupName,c.strGroupCode,d.strGroupName "
                      + "from tblbillmodifierdtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
                      + "where left(a.strItemCode,7)='" + itemCode + "'  "
                      + "and a.strBillNo='" + billNo + "' "
                      + "and left(a.strItemCode,7)=b.strItemCode "
                      + "and b.strSubGroupCode=c.strSubGroupCode "
                      + "and c.strGroupCode=d.strGroupCode  "
                      + "group by a.strItemCode,a.strModifierName ";
              
            Query querySqlBillMod = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
       	    List listSqlBillMod= querySqlBillMod.list();
       	    if(listSqlBillMod.size()>0)
       	     {
         	    for(int i=0;i<listSqlBillMod.size();i++)
         	    {
        	    Object[] objMod = (Object[]) listSqlBillMod.get(i);
             
            
                  clsBillModifierDtl objBillModDtl = new clsBillModifierDtl();
                  objBillModDtl.setStrBillNo(objMod[0].toString());
                  objBillModDtl.setStrItemCode(objMod[1].toString());
                  objBillModDtl.setStrModifierCode(objMod[2].toString());
                  objBillModDtl.setStrModifierName(objMod[3].toString());
                  objBillModDtl.setDblRate(Double.parseDouble(objMod[4].toString()));
                  objBillModDtl.setDblQuantity(Double.parseDouble(objMod[5].toString()));
                  objBillModDtl.setDblAmount(Double.parseDouble(objMod[6].toString()));
                  objBillModDtl.setStrClientCode(objMod[7].toString());
                  objBillModDtl.setStrCustomerCode(objMod[8].toString());
                  objBillModDtl.setStrDataPostFlag(objMod[9].toString());
                  objBillModDtl.setStrMMSDataPostFlag(objMod[10].toString());
                  objBillModDtl.setDblDiscPer(Double.parseDouble(objMod[11].toString()));
                  objBillModDtl.setDblDiscAmt(Double.parseDouble(objMod[12].toString()));
                  objBillModDtl.setSubGrouName(objMod[14].toString());
                  objBillModDtl.setGroupName(objMod[16].toString());
                  arrListBillModifierDtl.add(objBillModDtl);
              }
       	     }
              
          }
       	 }
          

          sql = "select a.strItemCode,a.strItemName,a.strBillNo,a.strAdvBookingNo,a.dblRate,sum(a.dblQuantity) "
                  + " ,sum(a.dblAmount),sum(a.dblTaxAmount),a.dteBillDate,a.strKOTNo,a.strClientCode,IFNULL(a.strCustomerCode,'') "
                  + " ,a.tmeOrderProcessing,a.strDataPostFlag,a.strMMSDataPostFlag,a.strManualKOTNo,a.tdhYN "
                  + " ,a.strPromoCode,a.strCounterCode,a.strWaiterNo,a.dblDiscountAmt,a.dblDiscountPer,b.strSubGroupCode "
                  + " ,c.strSubGroupName,c.strGroupCode,d.strGroupName "
                  + " from tblbilldtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
                  + " where a.strBillNo='" + billNo + "'  "
                  + " and a.strItemCode=b.strItemCode "
                  + " and b.strSubGroupCode=c.strSubGroupCode "
                  + " and c.strGroupCode=d.strGroupCode "
                  + " group by a.strBillNo,a.strKOTNo,a.strItemCode,a.strItemName "
                  + " order by a.strKOTNo desc,a.strItemCode; ";

           Query querySqlBill = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
     	   List listSqlBill= querySqlBill.list();
     	   if(listSqlBill.size()>0)
     	    {
       	    for(int i=0;i<listSqlBill.size();i++)
       	    {
      	     Object[] obj = (Object[]) listSqlBill.get(i);
          
              clsBillDtl objBillDtl = new clsBillDtl();
              objBillDtl.setStrItemCode(obj[0].toString());
              objBillDtl.setStrItemName(obj[1].toString());
              objBillDtl.setStrBillNo(obj[2].toString());
              objBillDtl.setStrAdvBookingNo(obj[3].toString());
              objBillDtl.setDblRate(Double.parseDouble(obj[4].toString()));
              objBillDtl.setDblQuantity(Double.parseDouble(obj[5].toString()));
              objBillDtl.setDblAmount(Double.parseDouble(obj[6].toString()));
              objBillDtl.setDblTaxAmount(Double.parseDouble(obj[7].toString()));
              objBillDtl.setDteBillDate(obj[8].toString());
              objBillDtl.setStrKOTNo(obj[9].toString());
              objBillDtl.setStrClientCode(obj[10].toString());
              objBillDtl.setStrCustomerCode(obj[11].toString());
              objBillDtl.setTmeOrderProcessing(obj[12].toString());
              objBillDtl.setStrDataPostFlag(obj[13].toString());
              objBillDtl.setStrMMSDataPostFlag(obj[14].toString());
              objBillDtl.setStrManualKOTNo(obj[15].toString());
              objBillDtl.setTdhYN(obj[16].toString());
              objBillDtl.setStrPromoCode(obj[17].toString());
              objBillDtl.setStrCounterCode(obj[18].toString());
              objBillDtl.setStrWaiterNo(obj[19].toString());
              objBillDtl.setDblDiscountAmt(Double.parseDouble(obj[20].toString()));
              objBillDtl.setDblDiscountPer(Double.parseDouble(obj[21].toString()));
              objBillDtl.setSubGrouName(obj[23].toString());
              objBillDtl.setGroupName(obj[25].toString());
              arrListKOTWiseBillDtl.add(objBillDtl);
          }
        
     	    }
          sql = "select * from tblbilldiscdtl where strBillNo='" + billNo + "'";
          Query querySqlBillDisc = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
    	   List listSqlBillDisc= querySqlBillDisc.list();
    	   if(listSqlBillDisc.size()>0)
    	    {
      	    for(int i=0;i<listSqlBillDisc.size();i++)
      	    {
     	     Object[] obj = (Object[]) listSqlBillDisc.get(i);
              clsBillDiscountDtl objBillDiscDtl = new clsBillDiscountDtl();
              objBillDiscDtl.setBillNo(obj[0].toString());
              objBillDiscDtl.setPOSCode(obj[1].toString());
              objBillDiscDtl.setDiscAmt(Double.parseDouble(obj[2].toString()));
              objBillDiscDtl.setDiscPer(Double.parseDouble(obj[3].toString()));
              objBillDiscDtl.setDiscOnAmt(Double.parseDouble(obj[4].toString()));
              objBillDiscDtl.setDiscOnType(obj[5].toString());
              objBillDiscDtl.setDiscOnValue(obj[6].toString());
              objBillDiscDtl.setReason(obj[7].toString());
              objBillDiscDtl.setRemark(obj[8].toString());
              objBillDiscDtl.setUserCreated(obj[9].toString());
              objBillDiscDtl.setUserEdited(obj[10].toString());
              objBillDiscDtl.setDateCreated(obj[11].toString());
              objBillDiscDtl.setDateEdited(obj[12].toString());
              arrListBillDiscDtl.add(objBillDiscDtl);
          }
    	  }

          sql = "select * from tblvoidbillhd where strBillNo='" + billNo+ "'";
          
       Query querySqlVoidBillHd = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
   	   List listSqlVoidBillHd= querySqlVoidBillHd.list();
   	   if(listSqlVoidBillHd.size()>0)
   	    {
     	    for(int i=0;i<listSqlVoidBillHd.size();i++)
     	    {
    	     Object[] obj = (Object[]) listSqlVoidBillHd.get(i);
          
              clsVoidBillHd objVoidBillHd = new clsVoidBillHd();
              objVoidBillHd.setStrPosCode(obj[0].toString());
              objVoidBillHd.setStrReasonCode(obj[1].toString());
              objVoidBillHd.setStrReasonName(obj[2].toString());
              objVoidBillHd.setStrBillNo(obj[3].toString());
              objVoidBillHd.setDblActualAmount(Double.parseDouble(obj[4].toString()));
              objVoidBillHd.setDblModifiedAmount(Double.parseDouble(obj[5].toString()));
              objVoidBillHd.setDteBillDate(obj[6].toString());
              objVoidBillHd.setStrTransType(obj[7].toString());
              objVoidBillHd.setDteModifyVoidBill(obj[8].toString());
              objVoidBillHd.setStrTableNo(obj[9].toString());
              objVoidBillHd.setStrWaiterNo(obj[10].toString());
              objVoidBillHd.setIntShiftCode(Integer.parseInt(obj[11].toString()));
              objVoidBillHd.setStrUserCreated(obj[12].toString());
              objVoidBillHd.setStrUserEdited(obj[13].toString());
              objVoidBillHd.setStrClientCode(obj[14].toString());
              objVoidBillHd.setStrDataPostFlag(obj[15].toString());
              objVoidBillHd.setStrRemark(obj[16].toString());
              arrListVoidBillHd.add(objVoidBillHd);
          }
   	    }

          sql = "select * from tblvoidbilldtl where strBillNo='" + billNo + "'";

          Query querySqlVoidBillDtl = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
   	      List listSqlVoidBillDtl= querySqlVoidBillDtl.list();
   	      if(listSqlVoidBillDtl.size()>0)
   	       {
     	    for(int i=0;i<listSqlVoidBillDtl.size();i++)
     	    {
    	     Object[] obj = (Object[]) listSqlVoidBillDtl.get(i);
          
              clsVoidBillDtl objVoidBillDtl = new clsVoidBillDtl();
              objVoidBillDtl.setStrPosCode(obj[0].toString());
              objVoidBillDtl.setStrReasonCode(obj[1].toString());
              objVoidBillDtl.setStrReasonName(obj[2].toString());
              objVoidBillDtl.setStrItemCode(obj[3].toString());
              objVoidBillDtl.setStrItemName(obj[4].toString());
              objVoidBillDtl.setStrBillNo(obj[5].toString());
              objVoidBillDtl.setIntQuantity(Integer.parseInt(obj[6].toString()));
              objVoidBillDtl.setDblAmount(Double.parseDouble(obj[7].toString()));
              objVoidBillDtl.setDblTaxAmount(Double.parseDouble(obj[8].toString()));
              objVoidBillDtl.setDteBillDate(obj[9].toString());
              objVoidBillDtl.setStrTransType(obj[10].toString());
              objVoidBillDtl.setDteModifyVoidBill(obj[11].toString());
              objVoidBillDtl.setStrSettlementCode(obj[12].toString());
              objVoidBillDtl.setDblSettlementAmt(Double.parseDouble(obj[13].toString()));
              objVoidBillDtl.setDblPaidAmt(Double.parseDouble(obj[14].toString()));
              objVoidBillDtl.setStrTableNo(obj[15].toString());
              objVoidBillDtl.setStrWaiterNo(obj[16].toString());
              objVoidBillDtl.setIntShiftCode(intShiftNo);
              objVoidBillDtl.setStrUserCreated(obj[18].toString());
              objVoidBillDtl.setStrClientCode(obj[19].toString());
              objVoidBillDtl.setStrDataPostFlag(obj[20].toString());
              objVoidBillDtl.setStrKOTNo(obj[21].toString());
              objVoidBillDtl.setStrRemarks(obj[22].toString());
              arrListVoidBillDtl.add(objVoidBillDtl);
          }
   	       }

          sql = "select * from tblvoidmodifierdtl where strBillNo='" + billNo + "'";
          

          Query querySqlModDtl= webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
   	      List listSqlModDtl= querySqlModDtl.list();
   	      if(listSqlModDtl.size()>0)
   	       {
     	    for(int i=0;i<listSqlModDtl.size();i++)
     	    {
    	     Object[] obj = (Object[]) listSqlModDtl.get(i);
          
              clsVoidBillModifierDtl objVoidBillModDtl = new clsVoidBillModifierDtl();
              objVoidBillModDtl.setStrBillNo(obj[0].toString());
              objVoidBillModDtl.setStrItemCode(obj[1].toString());
              objVoidBillModDtl.setStrModifierCode(obj[2].toString());
              objVoidBillModDtl.setStrModifierName(obj[3].toString());
              objVoidBillModDtl.setDblQuantity(Double.parseDouble(obj[4].toString()));
              objVoidBillModDtl.setDblAmount(Double.parseDouble(obj[5].toString()));
              objVoidBillModDtl.setStrClientCode(obj[6].toString());
              objVoidBillModDtl.setStrCustomerCode(obj[7].toString());
              objVoidBillModDtl.setStrDataPostFlag(obj[8].toString());
              objVoidBillModDtl.setStrRemarks(obj[9].toString());
              objVoidBillModDtl.setStrReasonCode(obj[10].toString());
              arrListVoidBillModifierDtl.add(objVoidBillModDtl);
          }
   	       }
   	    jObjRrturn =funFillItemGrid(billNo,strPOSCode,posDate,strClientCode,arrListBillDtl,arrListBillModifierDtl,arrListBillHd);
      }
      catch (Exception e)
      {
         
          e.printStackTrace();
      }	
	finally{
		
		return jObjRrturn;
	}
	
}
     	    
     	    private JSONObject funFillItemGrid(String billNo,String gPOSCode,String dtPOSDate,String clientCode,List<clsBillDtl> arrListBillDtl,
     	    		 List<clsBillModifierDtl> arrListBillModifierDtl,
     	    		List<clsBillHd> arrListBillHd) throws Exception
     	    {
     	     
     	    	 JSONObject jObjReturn=new JSONObject();
     	    	 JSONArray jArr=new JSONArray();
     	    	
     	        double subTotalForTax = 0;
     	        double totalDiscAmt = 0.00;
     	        List<clsPOSItemDetailFrTaxBean> arrListItemDtls = new ArrayList<clsPOSItemDetailFrTaxBean>();
    	        List<clsTaxCalculationBean> arrListTaxCal = new ArrayList<clsTaxCalculationBean>()   ;
     	        List<clsBillTaxDtl> arrListBillTaxDtl = new ArrayList<clsBillTaxDtl>();
     	        for (clsBillDtl objBillItemDtl : arrListBillDtl)
     	        {
     	        	JSONObject jObj=new JSONObject();
     	        	clsPOSItemDetailFrTaxBean objItemDtl = new clsPOSItemDetailFrTaxBean();
     	           jObj.put("strItemName",objBillItemDtl.getStrItemName());
                   jObj.put("dblQuantity", objBillItemDtl.getDblQuantity());
                   jObj.put("dblAmount",objBillItemDtl.getDblAmount());
                   jObj.put("strItemCode",objBillItemDtl.getStrItemCode());
                   jObj.put("strKOTNo",objBillItemDtl.getStrKOTNo());
     	            objItemDtl.setItemCode(objBillItemDtl.getStrItemCode());
     	            objItemDtl.setItemName(objBillItemDtl.getStrItemName());
     	            objItemDtl.setAmount(objBillItemDtl.getDblAmount());
     	            objItemDtl.setDiscAmt(objBillItemDtl.getDblDiscountAmt());
     	            arrListItemDtls.add(objItemDtl);
     	            subTotalForTax += objBillItemDtl.getDblAmount();
     	            totalDiscAmt += objBillItemDtl.getDblDiscountAmt();
     	           JSONArray jArrMod=new JSONArray();   
     	            for (clsBillModifierDtl objBillModDtl : arrListBillModifierDtl)
     	            {
     	                if ((objBillItemDtl.getStrItemCode() + "" + objBillModDtl.getStrModifierCode()).equals(objBillModDtl.getStrItemCode()))
     	                {
     	                    subTotalForTax += objBillModDtl.getDblAmount();
     	                    totalDiscAmt += objBillModDtl.getDblDiscAmt();
                            JSONObject jObjMod=new JSONObject();
                            
                            jObjMod.put("modifierName",objBillModDtl.getStrModifierName());
                            jObjMod.put("dblQuantityMod", objBillModDtl.getDblQuantity());
                            jObjMod.put("dblAmountMod",objBillModDtl.getDblAmount());
                            jObjMod.put("strModifierCode",objBillModDtl.getStrModifierCode());
                            jObjMod.put("strItemCodeMod",objBillModDtl.getStrItemCode());
                            jObjMod.put("strKOTNoMod",objBillItemDtl.getStrKOTNo());
                            jArrMod.put(jObjMod);
     	                    //add modifier items
     	                    clsPOSItemDetailFrTaxBean objModiItemDtl = new clsPOSItemDetailFrTaxBean();
     	                    objModiItemDtl.setItemCode(objBillModDtl.getStrItemCode());
     	                    objModiItemDtl.setItemName(objBillModDtl.getStrModifierName());
     	                    objModiItemDtl.setAmount(objBillModDtl.getDblAmount());
     	                    objModiItemDtl.setDiscAmt(objBillModDtl.getDblDiscAmt());
     	                    arrListItemDtls.add(objModiItemDtl);
     	                
     	                }
     	            }
     	           jObj.put("ModifierData",jArrMod);
     	           jArr.put(jObj);
     	        }
     	        double subTotal = 0;
     	        double grandTotal = 0;
     	        double discountPer = 0;
     	        String userCreated = "";

     	        arrListTaxCal.clear();
     	       
     	        arrListTaxCal = obj.funCalculateTax(arrListItemDtls,gPOSCode, dtPOSDate, areaCode, operationTypeForTax, subTotalForTax, totalDiscAmt, "", "S01");
     	        arrListBillTaxDtl.clear();
     	        double totalTaxAmount = 0;
     	        for (int cnt = 0; cnt < arrListTaxCal.size(); cnt++)
     	        {
     	        	clsTaxCalculationBean objTaxDtl = arrListTaxCal.get(cnt);
     	            totalTaxAmount += objTaxDtl.getTaxAmount();
     	           clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();
     	            objBillTaxDtl.setStrBillNo(billNo);
     	            objBillTaxDtl.setStrTaxCode(objTaxDtl.getTaxCode());
     	            objBillTaxDtl.setDblTaxableAmount(objTaxDtl.getTaxableAmount());
     	            objBillTaxDtl.setDblTaxAmount(objTaxDtl.getTaxAmount());
     	            objBillTaxDtl.setStrClientCode(clientCode);
     	            objBillTaxDtl.setStrDataPostFlag("N");
     	            arrListBillTaxDtl.add(objBillTaxDtl);
     	        }

     	        clsBillHd objBillHd = arrListBillHd.get(0);
     	        subTotal = objBillHd.getDblSubTotal();
     	        grandTotal = objBillHd.getDblGrandTotal();
     	        userCreated = objBillHd.getStrUserCreated();
     	        discountPer = objBillHd.getDblDiscountPer();

     	        grandTotal = (subTotalForTax - objBillHd.getDblDiscountAmt()) + totalTaxAmount;
     	        objBillHd.setDblGrandTotal(subTotalForTax);
     	        objBillHd.setDblTaxAmt(totalTaxAmount);
     	        objBillHd.setDblGrandTotal(grandTotal);
     	        arrListBillHd.set(0, objBillHd);
     	       jObjReturn.put("jArr", jArr);
     	        
     	       jObjReturn.put("userCreated", userCreated);
     	       jObjReturn.put("subTotal", subTotal);
     	       jObjReturn.put("totalTaxAmount", totalTaxAmount);
     	       jObjReturn.put("grandTotal", grandTotal);

     	        
     	        
     	        return jObjReturn;
     	    }

 
     	
     	 
     	public JSONObject funVoidItem(String selectedReasonDesc,String delTableName,String billNo,String remark,
		String userCode,String clientCode,String posDate,double taxAmount,String itemCode,double totalBillQty,double amount,String itemName,String modItemCode,String strPosCode)
     	    {  
     		JSONObject jbObjReturn=new JSONObject();
     	        try
     	        {
     	        	String delTableNo=funLoadTable(strPosCode,delTableName);
     	        	String sql, voidBillDate;
     	        	double selectedVoidQty=totalBillQty, voidedItemQty=0.0;
     	        	
     	            java.util.Date objDate = new java.util.Date();
     	            String time = (objDate.getHours()) + ":" + (objDate.getMinutes()) + ":" + (objDate.getSeconds());
     	            StringBuilder sb = new StringBuilder(posDate);
     	            int seq1 = sb.lastIndexOf(" ");
     	            String split = sb.substring(0, seq1);
     	            voidBillDate = split + " " + time;

//     	            if (tblItemTable.getModel().getRowCount() > 0)
//     	            {
//     	                reasoncount = 0;
//     	                String tableNo = "";
//     	            
//     	                if (Double.parseDouble(tblItemTable.getValueAt(r, 1).toString()) > 1)
//     	                {
//     	                    new frmNumericKeyboard(this, true, "", "VoidBill", "Enter quantity to void").setVisible(true);
//     	                    if (clsGlobalVarClass.gNumerickeyboardValue.length() == 0)
//     	                    {
//     	                        return;
//     	                    }
//     	                    if (Double.parseDouble(clsGlobalVarClass.gNumerickeyboardValue) > Double.parseDouble(tblItemTable.getValueAt(r, 1).toString()))
//     	                    {
//     	                        JOptionPane.showMessageDialog(this, "Please select valid quantity");
//     	                        return;
//     	                    }
//     	                    double quantity = Double.parseDouble(clsGlobalVarClass.gNumerickeyboardValue);
//     	                    if (quantity == 0)
//     	                    {
//     	                        JOptionPane.showMessageDialog(this, "Please select valid quantity");
//     	                        return;
//     	                    }
//     	                    else
//     	                    {
//     	                        selectedVoidQty = Integer.parseInt(clsGlobalVarClass.gNumerickeyboardValue);
//     	                        voidedItemQty = selectedVoidQty;
//     	                    }
//     	                }
//     	                if (r < 0)
//     	                {
//     	                    JOptionPane.showMessageDialog(this, "Please select item to void");
//     	                    return;
//     	                }
     	           voidedItemQty = selectedVoidQty;
     	            int i = 0;    
     	            int reasoncount = 0;
     	            String[] reason;
     	            sql = "select count(strReasonName) from tblreasonmaster where strVoidBill='Y'";
     	               Query querySqlModDtl= webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
     	       	       List listSqlModDtl= querySqlModDtl.list();
     	       	       if(listSqlModDtl.size()>0)
     	       	       {
     	         	    for(int j=0;j<listSqlModDtl.size();j++)
     	         	    {
     	        	     Object obj = (Object) listSqlModDtl.get(j);
     	              
     	                    reasoncount =Integer.parseInt( obj.toString());
     	                }
     	       	       }
     	                if (reasoncount > 0)
     	                {
     	                    String selectedReasonCode = "";
     	                    reason = new String[reasoncount];
     	                    sql = "select strReasonName from tblreasonmaster where strVoidBill='Y'";
     	                   Query querySqlResName= webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
         	       	       List listSqlResName= querySqlResName.list();
         	       	       i = 0;
         	       	       if(listSqlResName.size()>0)
         	       	       {
         	         	    for(int j=0;j<listSqlResName.size();j++)
         	         	    {
         	        	     Object obj = (Object) listSqlResName.get(j);
     	                    
     	                        reason[i] = obj.toString();
     	                        i++;
     	                    }
         	       	       }
//     	                    String selectedReasonDesc = (String) JOptionPane.showInputDialog(this, "Please Select Reason?", "Reason", JOptionPane.QUESTION_MESSAGE, null, reason, reason[0]);
     	                    if (selectedReasonDesc != null)
     	                    {
     	                        sql = "select strReasonCode from tblreasonmaster where strReasonName='" + selectedReasonDesc + "' "
     	                                + "and strVoidBill='Y'";
     	                       Query querySqlResCode= webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
             	       	       List listSqlResCode= querySqlResCode.list();
             	       	       i = 0;
             	       	       if(listSqlResCode.size()>0)
             	       	       {
             	         	    for(int j=0;j<listSqlResCode.size();j++)
             	         	    {
             	         	      Object obj = (Object) listSqlResCode.get(j);
     	                        
     	                            selectedReasonCode = obj.toString();
     	                        }
             	         	    }
     	                        
//     	                        if (choice == JOptionPane.YES_OPTION)
//     	                        {
     	                            
//     	                            if (!clsGlobalVarClass.gTouchScreenMode)
//     	                            {
//     	                                remark = JOptionPane.showInputDialog(null, "Enter Remarks");
//     	                            }
//     	                            else
//     	                            {
//     	                                new frmAlfaNumericKeyBoard(this, true, "1", "Please Enter Remark.").setVisible(true);
//     	                                remark = clsGlobalVarClass.gKeyboardValue;
//     	                            }
     	                         
//     	                            itemCodeForVoid = tblItemTable.getValueAt(r, 3).toString();

     	                            if (itemName.startsWith("-->"))
     	                            {
     	                                
     	                                Iterator<clsBillModifierDtl> billModiIt = arrListBillModifierDtl.iterator();
     	                                while (billModiIt.hasNext())
     	                                {
     	                                    clsBillModifierDtl objBillModDtl = billModiIt.next();
     	                                    if (objBillModDtl.getStrItemCode().equals(itemCode) && objBillModDtl.getStrModifierName().equals(itemName))
     	                                    {
     	                                        clsVoidBillModifierDtl objVoidBillModDtl = new clsVoidBillModifierDtl();
     	                                        objVoidBillModDtl.setStrBillNo(billNo);
     	                                        objVoidBillModDtl.setStrItemCode(objBillModDtl.getStrItemCode());
     	                                        objVoidBillModDtl.setStrModifierCode(objBillModDtl.getStrModifierCode());
     	                                        objVoidBillModDtl.setStrModifierName(objBillModDtl.getStrModifierName());
     	                                        objVoidBillModDtl.setDblQuantity(objBillModDtl.getDblQuantity());
     	                                        objVoidBillModDtl.setDblAmount(objBillModDtl.getDblAmount());
     	                                        objVoidBillModDtl.setStrClientCode(objBillModDtl.getStrClientCode());
     	                                        objVoidBillModDtl.setStrCustomerCode(objBillModDtl.getStrCustomerCode());
     	                                        objVoidBillModDtl.setStrDataPostFlag(objBillModDtl.getStrDataPostFlag());
     	                                        objVoidBillModDtl.setStrRemarks(remark);
     	                                        objVoidBillModDtl.setStrReasonCode(selectedReasonCode);
     	                                        billModiIt.remove();
     	                                        arrListVoidBillModifierDtl.add(objVoidBillModDtl);
     	                                        arrListBillModifierDtl.remove(objBillModDtl);
     	                                    }
     	                                }
     	                            }
     	                            else
     	                            {
     	                                String sqlMaxKOTForItem = "select a.strKOTNo "
     	                                        + "from tblbilldtl a "
     	                                        + "where a.strBillNo='" + billNo + "' and a.strItemCode='" + itemCode + "' "
     	                                        + "order by a.strKOTNo desc ";
     	                             

     	        	                   Query querySqlMakeKot= webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlMaxKOTForItem.toString());
     	            	       	       List listSqlMakeKot= querySqlMakeKot.list();
     	            	       	       i = 0;
     	            	       	       if(listSqlMakeKot.size()>0)
     	            	       	       {
     	            	         	    for(int j=0;j<listSqlMakeKot.size();j++)
     	            	         	    {
     	            	        	     Object obj = (Object) listSqlMakeKot.get(j);
     	                                String maxKOT = "";
//     	                                while (rsMaxKOT.next())
//     	                                {
     	                                    if (selectedVoidQty <= 0)
     	                                    {
     	                                        break;
     	                                    }
     	                                    maxKOT = obj.toString();
     	                                    int cnt = 0;
     	                                    Iterator<clsBillDtl> billDtlIt = arrListKOTWiseBillDtl.iterator();
     	                                    while (billDtlIt.hasNext())
     	                                    {
     	                                        if (selectedVoidQty <= 0)
     	                                        {
     	                                            break;
     	                                        }
     	                                        clsBillDtl objBillDtl = billDtlIt.next();
     	                                        if (objBillDtl.getStrItemCode().equals(itemCode) && objBillDtl.getStrKOTNo().equals(maxKOT))
     	                                        {
     	                                            boolean flgRecordPresent = false;
     	                                            int cntVoid = 0;
     	                                            for (clsVoidBillDtl objVoidBillDtl : arrListVoidBillDtl)
     	                                            {
     	                                                if (objVoidBillDtl.getStrItemCode().equals(objBillDtl.getStrItemCode()) && objVoidBillDtl.getStrKOTNo().equals(maxKOT))
     	                                                {
     	                                                    double voidedQty = 1;
     	                                                    if (objBillDtl.getDblQuantity() < selectedVoidQty)
     	                                                    {
     	                                                        voidedQty = objVoidBillDtl.getIntQuantity() + objBillDtl.getDblQuantity();
     	                                                    }
     	                                                    else
     	                                                    {
     	                                                        voidedQty = objVoidBillDtl.getIntQuantity() + selectedVoidQty;
     	                                                    }

     	                                                    objVoidBillDtl.setIntQuantity(voidedQty);
     	                                                    double voidedAmt = objBillDtl.getDblRate() * voidedQty;
     	                                                    objVoidBillDtl.setDblAmount(voidedAmt);
     	                                                    arrListVoidBillDtl.set(cntVoid, objVoidBillDtl);
     	                                                    flgRecordPresent = true;
     	                                                    break;
     	                                                }
     	                                                cntVoid++;
     	                                            }

     	                                            if (!flgRecordPresent)
     	                                            {
     	                                                clsVoidBillDtl objVoidBillDtl = new clsVoidBillDtl();
     	                                                double voidedQty = 1;
     	                                                if (objBillDtl.getDblQuantity() < selectedVoidQty)
     	                                                {
     	                                                    voidedQty = objVoidBillDtl.getIntQuantity() + objBillDtl.getDblQuantity();
     	                                                }
     	                                                else
     	                                                {
     	                                                    voidedQty = objVoidBillDtl.getIntQuantity() + selectedVoidQty;
     	                                                }
     	                                                double voidedAmt = objBillDtl.getDblRate() * voidedQty;
//     	                                                double totalBillQty = Double.parseDouble(tblItemTable.getValueAt(r, 1).toString());
     	                                                double taxAmt = objBillDtl.getDblTaxAmount() / totalBillQty;
     	                                                objVoidBillDtl.setStrBillNo(billNo);
     	                                                objVoidBillDtl.setStrPosCode(strPosCode);
     	                                                objVoidBillDtl.setStrItemCode(itemCode);
     	                                                objVoidBillDtl.setStrItemName(itemName);
     	                                                objVoidBillDtl.setIntQuantity(voidedQty);
     	                                                objVoidBillDtl.setDblAmount(voidedAmt);
     	                                                objVoidBillDtl.setDblPaidAmt(0);
     	                                                objVoidBillDtl.setDblSettlementAmt(0);
     	                                                objVoidBillDtl.setDblTaxAmount(taxAmt * voidedQty);
     	                                                objVoidBillDtl.setDteBillDate(objBillDtl.getDteBillDate());
     	                                                objVoidBillDtl.setDteModifyVoidBill(voidBillDate);
     	                                                objVoidBillDtl.setStrTransType("VB");
     	                                                objVoidBillDtl.setIntShiftCode(intShiftNo);
     	                                                objVoidBillDtl.setStrClientCode(clientCode);
     	                                                objVoidBillDtl.setStrDataPostFlag("N");
     	                                                objVoidBillDtl.setStrKOTNo(maxKOT);
     	                                                objVoidBillDtl.setStrUserCreated(userCode);
     	                                                objVoidBillDtl.setStrReasonCode(selectedReasonCode);
     	                                                objVoidBillDtl.setStrReasonName(selectedReasonDesc);
     	                                                objVoidBillDtl.setStrRemarks(remark);
     	                                                objVoidBillDtl.setStrSettlementCode("");
     	                                                objVoidBillDtl.setStrWaiterNo("NA");
     	                                                objVoidBillDtl.setStrTableNo("NA");
     	                                                arrListVoidBillDtl.add(objVoidBillDtl);
     	                                            }

     	                                            double qty = objBillDtl.getDblQuantity() - selectedVoidQty;
     	                                            if (qty < 1)
     	                                            {
     	                                                selectedVoidQty = selectedVoidQty - objBillDtl.getDblQuantity();
     	                                                billDtlIt.remove();
     	                                            }
     	                                            else
     	                                            {
     	                                                double amt = objBillDtl.getDblRate() * qty;
     	                                                double discAmtForSingleQty = objBillDtl.getDblDiscountAmt() / objBillDtl.getDblQuantity();
     	                                                double discAmt = discAmtForSingleQty * qty;
//     	                                                double totalBillQty = Double.parseDouble(tblItemTable.getValueAt(r, 1).toString());
     	                                                double taxAmt = objBillDtl.getDblTaxAmount() / totalBillQty;
     	                                                objBillDtl.setDblDiscountAmt(discAmt);
     	                                                objBillDtl.setDblQuantity(qty);
     	                                                objBillDtl.setDblAmount(amt);
     	                                                objBillDtl.setDblTaxAmount(taxAmt * qty);
     	                                                arrListKOTWiseBillDtl.set(cnt, objBillDtl);
     	                                                selectedVoidQty = 0;
     	                                            }

     	                                            Iterator<clsBillModifierDtl> billModiIt = arrListBillModifierDtl.iterator();
     	                                            while (billModiIt.hasNext())
     	                                            {
     	                                                clsBillModifierDtl objBillModDtl = billModiIt.next();
     	                                                boolean isItemExistsInBillItemDtl = false;
     	                                                for (clsBillDtl billDtl : arrListKOTWiseBillDtl)
     	                                                {
     	                                                    if (billDtl.getStrItemCode().equalsIgnoreCase(itemCode))
     	                                                    {
     	                                                        isItemExistsInBillItemDtl = true;
     	                                                        break;
     	                                                    }
     	                                                }
     	                                                if (!isItemExistsInBillItemDtl)
     	                                                {
     	                                                    if (objBillModDtl.getStrItemCode().equals(itemCode + "" + objBillModDtl.getStrModifierCode()))
     	                                                    {
     	                                                        clsVoidBillModifierDtl objVoidBillModDtl = new clsVoidBillModifierDtl();
     	                                                        objVoidBillModDtl.setStrBillNo(billNo);
     	                                                        objVoidBillModDtl.setStrItemCode(objBillModDtl.getStrItemCode());
     	                                                        objVoidBillModDtl.setStrModifierCode(objBillModDtl.getStrModifierCode());
     	                                                        objVoidBillModDtl.setStrModifierName(objBillModDtl.getStrModifierName());
     	                                                        objVoidBillModDtl.setDblQuantity(objBillModDtl.getDblQuantity());
     	                                                        objVoidBillModDtl.setDblAmount(objBillModDtl.getDblAmount());
     	                                                        objVoidBillModDtl.setStrClientCode(objBillModDtl.getStrClientCode());
     	                                                        objVoidBillModDtl.setStrCustomerCode(objBillModDtl.getStrCustomerCode());
     	                                                        objVoidBillModDtl.setStrDataPostFlag(objBillModDtl.getStrDataPostFlag());
     	                                                        objVoidBillModDtl.setStrRemarks(remark);
     	                                                        objVoidBillModDtl.setStrReasonCode(selectedReasonCode);
     	                                                        billModiIt.remove();
     	                                                        arrListVoidBillModifierDtl.add(objVoidBillModDtl);
     	                                                        arrListBillModifierDtl.remove(objBillModDtl);
     	                                                    }
     	                                                }
     	                                            }
     	                                            //1 remove item from billmodifer dtl
     	                                            //2 add item to voidmodifirt dtl
     	                                            break;
     	                                        }
     	                                        cnt++;
     	                                    }
     	                                }
     	            	         	    }
     	                               
     	                            }
     	                       JSONObject   jObjRrturn =funFillItemGrid(billNo,strPosCode,posDate,clientCode,arrListKOTWiseBillDtl,arrListBillModifierDtl,arrListBillHd);
//     	                            funFillItemGrid(billNo);
     	                            double totalDiscAmt = 0;
     	                            double discPer = 0.00;
     	                            double itemSubTotal = 0;

     	                            //re-calculate discount
     	                            if (arrListBillDiscDtl.size() > 0)
     	                            {
     	                                Iterator<clsBillDiscountDtl> billDiscIt = arrListBillDiscDtl.iterator();

     	                                while (billDiscIt.hasNext())
     	                                {
     	                                    clsBillDiscountDtl objBillDiscDtl = billDiscIt.next();
     	                                    String discountOnType = objBillDiscDtl.getDiscOnType();
     	                                    String discountOnValue = objBillDiscDtl.getDiscOnValue();
     	                                    double discPerce = objBillDiscDtl.getDiscPer();
     	                                    double newDiscAmt = 0;
     	                                    double newDiscOnAmt = 0;

     	                                    if (discountOnType.equalsIgnoreCase("Total"))
     	                                    {
     	                                        //bill dtl
     	                                        for (clsBillDtl objBillDtl : arrListKOTWiseBillDtl)
     	                                        {
     	                                            newDiscAmt += objBillDtl.getDblDiscountAmt();
     	                                            newDiscOnAmt += objBillDtl.getDblAmount();
     	                                            itemSubTotal += objBillDtl.getDblAmount();
     	                                        }
     	                                        //modifier  dtl
     	                                        for (clsBillModifierDtl objBillModifierDtl : arrListBillModifierDtl)
     	                                        {
     	                                            newDiscAmt += objBillModifierDtl.getDblDiscAmt();
     	                                            newDiscOnAmt += objBillModifierDtl.getDblAmount();
     	                                            itemSubTotal += objBillModifierDtl.getDblAmount();
     	                                        }
     	                                    }
     	                                    else if (discountOnType.equalsIgnoreCase("ItemWise"))
     	                                    {
     	                                        //bill dtl
     	                                        for (clsBillDtl objBillDtl : arrListKOTWiseBillDtl)
     	                                        {
     	                                            if (objBillDtl.getStrItemName().equalsIgnoreCase(discountOnValue))
     	                                            {
     	                                                newDiscOnAmt += objBillDtl.getDblAmount();
     	                                                itemSubTotal += objBillDtl.getDblAmount();

     	                                                //modifier  dtl
     	                                                for (clsBillModifierDtl objBillModifierDtl : arrListBillModifierDtl)
     	                                                {
     	                                                    if (objBillDtl.getStrItemCode().equals(objBillModifierDtl.getStrItemCode().substring(0, 7)))
     	                                                    {
     	                                                        newDiscOnAmt += objBillModifierDtl.getDblAmount();
     	                                                        itemSubTotal += objBillModifierDtl.getDblAmount();
     	                                                    }
     	                                                }
     	                                            }
     	                                        }
     	                                    }
     	                                    else if (discountOnType.equalsIgnoreCase("GroupWise"))
     	                                    {
     	                                        //bill dtl
     	                                        for (clsBillDtl objBillDtl : arrListKOTWiseBillDtl)
     	                                        {
     	                                            if (objBillDtl.getGroupName().equalsIgnoreCase(discountOnValue))
     	                                            {
     	                                                newDiscAmt += objBillDtl.getDblDiscountAmt();
     	                                                newDiscOnAmt += objBillDtl.getDblAmount();
     	                                                itemSubTotal += objBillDtl.getDblAmount();
     	                                            }
     	                                            else
     	                                            {
     	                                                itemSubTotal += objBillDtl.getDblAmount();
     	                                            }
     	                                        }
     	                                        //modifier  dtl
     	                                        for (clsBillModifierDtl objBillModifierDtl : arrListBillModifierDtl)
     	                                        {
     	                                            if (objBillModifierDtl.getGroupName().equalsIgnoreCase(discountOnValue))
     	                                            {
     	                                                newDiscAmt += objBillModifierDtl.getDblDiscAmt();
     	                                                newDiscOnAmt += objBillModifierDtl.getDblAmount();
     	                                                itemSubTotal += objBillModifierDtl.getDblAmount();
     	                                            }
     	                                            else
     	                                            {
     	                                                itemSubTotal += objBillModifierDtl.getDblAmount();
     	                                            }
     	                                        }
     	                                    }
     	                                    else if (discountOnType.equalsIgnoreCase("SubGroupWise"))
     	                                    {
     	                                        //bill dtl
     	                                        for (clsBillDtl objBillDtl : arrListKOTWiseBillDtl)
     	                                        {
     	                                            if (objBillDtl.getGroupName().equalsIgnoreCase(discountOnValue))
     	                                            {
     	                                                newDiscAmt += objBillDtl.getDblDiscountAmt();
     	                                                newDiscOnAmt += objBillDtl.getDblAmount();
     	                                                itemSubTotal += objBillDtl.getDblAmount();
     	                                            }
     	                                            else
     	                                            {
     	                                                itemSubTotal += objBillDtl.getDblAmount();
     	                                            }
     	                                        }
     	                                        //modifier  dtl
     	                                        for (clsBillModifierDtl objBillModifierDtl : arrListBillModifierDtl)
     	                                        {
     	                                            if (objBillModifierDtl.getSubGrouName().equalsIgnoreCase(discountOnValue))
     	                                            {
     	                                                newDiscAmt += objBillModifierDtl.getDblDiscAmt();
     	                                                newDiscOnAmt += objBillModifierDtl.getDblAmount();
     	                                                itemSubTotal += objBillModifierDtl.getDblAmount();
     	                                            }
     	                                            else
     	                                            {
     	                                                itemSubTotal += objBillModifierDtl.getDblAmount();
     	                                            }
     	                                        }
     	                                    }

     	                                    //update bill discounr
     	                                    if (newDiscOnAmt > 0)
     	                                    {
     	                                        newDiscAmt = (discPerce / 100) * newDiscOnAmt;
     	                                        objBillDiscDtl.setDiscAmt(newDiscAmt);
     	                                        objBillDiscDtl.setDiscOnAmt(newDiscOnAmt);
     	                                        totalDiscAmt += newDiscAmt;
     	                                    }
     	                                    else
     	                                    {
     	                                        billDiscIt.remove();
     	                                    }
     	                                }
     	                            }
     	                            itemSubTotal = 0.00;

     	                            //bill dtl
     	                            for (clsBillDtl objBillDtl : arrListKOTWiseBillDtl)
     	                            {
     	                                itemSubTotal += objBillDtl.getDblAmount();
     	                            }

     	                            //modifier  dtl
     	                            for (clsBillModifierDtl objBillModifierDtl : arrListBillModifierDtl)
     	                            {
     	                                itemSubTotal += objBillModifierDtl.getDblAmount();
     	                            }

     	                            if (itemSubTotal == 0.00)
     	                            {
     	                                discPer = 0.00;
     	                            }
     	                            else
     	                            {
     	                                discPer = (totalDiscAmt / itemSubTotal) * 100;
     	                            }
     	                            clsBillHd objBillHd = arrListBillHd.get(0);
     	                            double totalVoidedAmt = objBillHd.getDblGrandTotal() - itemSubTotal;

     	                            clsVoidBillHd objVoidBillHd = new clsVoidBillHd();
     	                            if (arrListVoidBillHd.size() > 0)
     	                            {
     	                                objVoidBillHd = arrListVoidBillHd.get(0);
     	                                objVoidBillHd.setDblModifiedAmount(objVoidBillHd.getDblModifiedAmount() + totalVoidedAmt);
     	                                arrListVoidBillHd.set(0, objVoidBillHd);
     	                            }
     	                            else
     	                            {
     	                                objVoidBillHd.setStrBillNo(billNo);
     	                                objVoidBillHd.setStrPosCode(objBillHd.getStrPOSCode());
     	                                objVoidBillHd.setStrReasonCode(selectedReasonCode);
     	                                objVoidBillHd.setStrReasonName(selectedReasonDesc);
     	                                objVoidBillHd.setDblActualAmount(objBillHd.getDblGrandTotal());
     	                                objVoidBillHd.setDblModifiedAmount(totalVoidedAmt);
     	                                objVoidBillHd.setDteBillDate(objBillHd.getDteBillDate());
     	                                objVoidBillHd.setStrTransType("VB");
     	                                objVoidBillHd.setDteModifyVoidBill(voidBillDate);
     	                                objVoidBillHd.setStrTableNo(objBillHd.getStrTableNo());
     	                                objVoidBillHd.setStrWaiterNo(objBillHd.getStrWaiterNo());
     	                                objVoidBillHd.setIntShiftCode(objBillHd.getIntShiftCode());
     	                                objVoidBillHd.setStrUserCreated(userCode);
     	                                objVoidBillHd.setStrUserEdited(userCode);
     	                                objVoidBillHd.setStrClientCode(objBillHd.getStrClientCode());
     	                                objVoidBillHd.setStrDataPostFlag(objBillHd.getStrDataPostFlag());
     	                                objVoidBillHd.setStrRemark(remark);
     	                                arrListVoidBillHd.add(objVoidBillHd);
     	                            }

     	                            objBillHd.setDblSubTotal(itemSubTotal);
     	                            objBillHd.setDblDiscountAmt(totalDiscAmt);
     	                            objBillHd.setDblDiscountPer(discPer);
     	                            double grandTotal = (itemSubTotal + objBillHd.getDblTaxAmt()) - totalDiscAmt;
     	                            objBillHd.setDblGrandTotal(grandTotal);
     	                            arrListBillHd.set(0, objBillHd);
//     	                            lblTotalAmt.setText(String.valueOf(Math.rint(objBillHd.getDblGrandTotal())));
//     	                            lblTaxValue.setText(String.valueOf(Math.rint(objBillHd.getDblTaxAmt())));
//     	                            lblSubTotalValue.setText(String.valueOf(Math.rint(objBillHd.getDblSubTotal())));
     	                            //tblBillDetails.setValueAt(Math.rint(objBillHd.getDblGrandTotal()), selectedBillNoRow, 3);

     	                            if (arrListKOTWiseBillDtl.size() > 0)
     	                            {
     	                                funInsertVoidData( billNo,userCode);
     	                                funUpdateBillData( billNo, userCode, voidedItemQty,itemCode,strPosCode, clientCode, voidBillDate);
     	                                //funPrintBill(billNo,clsGlobalVarClass.getPOSDateForTransaction());
//     	                                objUtility.funPrintBill(billNo, clsGlobalVarClass.getPOSDateForTransaction(), false, objBillHd.getStrPOSCode());
     	                            }
     	                            else
     	                            {
     	                                funInsertVoidData(billNo,userCode);
     	                                String updateQuery = "update tbltablemaster set strStatus='Normal',intPaxNo=0 "
     	                                        + "where strTableNo='" + delTableNo + "'";
     	                               Query  queryUpdate = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     	                              queryUpdate.executeUpdate();
     	                                sql="Delete from tblbilldtl where strBillNo='" + billNo + "'";
     	                                Query queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
     	                                queryDel.executeUpdate();
     	                                sql="Delete from tblbillhd where strBillNo='" + billNo + "'";
     	                               queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	                                queryDel.executeUpdate();
     	                                sql="Delete from tblbillmodifierdtl where strBillNo='" + billNo + "'";
     	                               queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	                                queryDel.executeUpdate();
     	                                sql="Delete from tblbilltaxdtl where strBillNo='" + billNo + "'";
     	                               queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	                                queryDel.executeUpdate();
     	                                sql="Delete from tblbilldiscdtl where strBillNo='" + billNo + "'";
     	                               queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	                                queryDel.executeUpdate();
     	                                sql="Delete from tblhomedelivery where strBillNo='" + billNo + "'";
     	                               queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	                                queryDel.executeUpdate();
     	                                sql="Delete from tblbillsettlementdtl where strBillNo='" + billNo + "'";
     	                               queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	                                queryDel.executeUpdate();
     	                            }
     	                            //clsGlobalVarClass.funInvokeHOWebserviceForTrans();
//     	                        }
//     	                        funFillBillNoGrid("");
     	                    }
     	                }
     	                else
     	                {
//     	                    new frmOkPopUp(null, "Please Create Reason First", "Error", 1).setVisible(true);
     	                }
     	               jbObjReturn.put("true", "true");
     	        }
     	        catch (Exception e)
     	        {
     	            
     	            e.printStackTrace();
     	        }
     	        finally {
     	        	return jbObjReturn;
     	        }
     	    }
		  public void funInsertVoidData(String billNo,String userCode) throws Exception
		    {
			  String sql="";
			  String  sqlQuery = "select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='Void Bill'";
		        sql = "delete from tblvoidbillhd where strBillNo='" + billNo + "' and strTransType='VB'";
		        Query queryDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                 queryDel.executeUpdate();
		        clsVoidBillHd objVoidBillHd = arrListVoidBillHd.get(0);
		        sql = "insert into tblvoidbillhd (strPosCode,strReasonCode,strReasonName,strBillNo,"
		                + "dblActualAmount,dblModifiedAmount,dteBillDate,"
		                + "strTransType,dteModifyVoidBill,strTableNo,strWaiterNo,intShiftCode,"
		                + "strUserCreated,strUserEdited,strClientCode,strRemark) values "
		                + "('" + objVoidBillHd.getStrPosCode() + "','" + objVoidBillHd.getStrReasonCode() + "'"
		                + ",'" + objVoidBillHd.getStrReasonName() + "','" + objVoidBillHd.getStrBillNo() + "'"
		                + ",'" + objVoidBillHd.getDblActualAmount() + "'," + objVoidBillHd.getDblModifiedAmount() + ""
		                + ",'" + objVoidBillHd.getDteBillDate() + "','" + objVoidBillHd.getStrTransType() + "'"
		                + ",'" + objVoidBillHd.getDteModifyVoidBill() + "','" + objVoidBillHd.getStrTableNo() + "'"
		                + ",'" + objVoidBillHd.getStrWaiterNo() + "','" + objVoidBillHd.getIntShiftCode() + "'"
		                + ",'" + objVoidBillHd.getStrUserCreated() + "','" + objVoidBillHd.getStrUserEdited() + "'"
		                + ",'" + objVoidBillHd.getStrClientCode() + "','" + objVoidBillHd.getStrRemark() + "')";
		        //System.out.println(sql);
                  Query sqlQue= webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
	       	       List listSql= sqlQue.list();
	       	     
	       	       if(listSql.size()>0)
	       	       {
	         	 
	        	     Object obj = (Object) listSql.get(0);
		     
		            if (Boolean.parseBoolean(obj.toString()))
		            {
		            	webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		            }
		        }

		        sql = "delete from tblvoidbilldtl where strBillNo='" + billNo + "'";
		        webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		        for (clsVoidBillDtl objVoidBillDtl : arrListVoidBillDtl)
		        {
		            sql = "insert into tblvoidbilldtl(strPosCode,strReasonCode,strReasonName,strItemCode"
		                    + ",strItemName,strBillNo,intQuantity,dblAmount,dblTaxAmount,dteBillDate,"
		                    + "strTransType,dteModifyVoidBill,intShiftCode,strUserCreated,strClientCode"
		                    + ",strKOTNo,strRemarks) "
		                    + "values('" + objVoidBillDtl.getStrPosCode() + "','" + objVoidBillDtl.getStrReasonCode() + "'"
		                    + ",'" + objVoidBillDtl.getStrReasonName() + "','" + objVoidBillDtl.getStrItemCode() + "'"
		                    + ",'" + objVoidBillDtl.getStrItemName() + "','" + objVoidBillDtl.getStrBillNo() + "'"
		                    + ",'" + objVoidBillDtl.getIntQuantity() + "','" + objVoidBillDtl.getDblAmount() + "'"
		                    + ",'" + objVoidBillDtl.getDblTaxAmount() + "','" + objVoidBillDtl.getDteBillDate() + "'"
		                    + ",'" + objVoidBillDtl.getStrTransType() + "'" + ",'" + objVoidBillDtl.getDteModifyVoidBill() + "'"
		                    + "," + objVoidBillDtl.getIntShiftCode() + ",'" + objVoidBillDtl.getStrUserCreated() + "'"
		                    + ",'" + objVoidBillDtl.getStrClientCode() + "','" + objVoidBillDtl.getStrKOTNo() + "'"
		                    + ",'" + objVoidBillDtl.getStrRemarks() + "')";
		            System.out.println(sql);
		           
		            if(listSql.size()>0)
		       	       {
		                if (Boolean.parseBoolean(obj.toString()))
		                {
		                	webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		                }
		            }
		        }

		        sql = "delete from tblvoidmodifierdtl where strBillNo='" + billNo + "'";
		        webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		        for (clsVoidBillModifierDtl objVoidBillModDtl : arrListVoidBillModifierDtl)
		        {
		            sql = "insert into tblvoidmodifierdtl(strBillNo,strItemCode,strModifierCode,"
		                    + "strModifierName,dblQuantity,dblAmount,strClientCode,strCustomerCode"
		                    + ",strDataPostFlag,strRemarks,strReasonCode) values "
		                    + "('" + objVoidBillModDtl.getStrBillNo() + "','" + objVoidBillModDtl.getStrItemCode() + "'"
		                    + ",'" + objVoidBillModDtl.getStrModifierCode() + "','" + objVoidBillModDtl.getStrModifierName() + "'"
		                    + ",'" + objVoidBillModDtl.getDblQuantity() + "','" + objVoidBillModDtl.getDblAmount() + "'"
		                    + ",'" + objVoidBillModDtl.getStrClientCode() + "','" + objVoidBillModDtl.getStrCustomerCode() + "'"
		                    + ",'" + objVoidBillModDtl.getStrDataPostFlag() + "','" + objVoidBillModDtl.getStrRemarks() + "'"
		                    + ",'" + objVoidBillModDtl.getStrReasonCode() + "')";
		            //System.out.println("recordset:"+sql);
		       
		            if(listSql.size()>0)
		       	       {
		                if (Boolean.parseBoolean(obj.toString()))
		                {
		                	webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
		                }
		            }
		        }
		  
		  
		    }
	
		  public void funUpdateBillData(String billNo,String userCode,double voidedItemQty,String itemCodeForVoid,
				  String strPOSCode,String strClientCode,String voidBillDate) throws Exception
		    {
		        DecimalFormat objDecFormat = new DecimalFormat("####0.00");

		        String sql="";
		        String sqlDelete = "delete from tblbilldtl where strBillNo='" + billNo + "'";
		        webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete).executeUpdate();
		        String sqlInsertBillDtl = "insert into tblbilldtl "
		                + "(strItemCode,strItemName,strBillNo,strAdvBookingNo,dblRate"
		                + ",dblQuantity,dblAmount,dblTaxAmount,dteBillDate,strKOTNo"
		                + ",strClientCode,strCustomerCode,tmeOrderProcessing,strDataPostFlag"
		                + ",strMMSDataPostFlag,strManualKOTNo,tdhYN,strPromoCode,strCounterCode"
		                + ",strWaiterNo,dblDiscountAmt,dblDiscountPer) "
		                + "values ";
		        for (clsBillDtl objBillDtl : arrListKOTWiseBillDtl)
		        {
		            double amount = objBillDtl.getDblAmount();
		            /*if (objBillDtl.getStrItemCode().equals(itemCodeForVoid))
		             {
		             amount=(objBillDtl.getDblRate()*objBillDtl.getDblQuantity());
		             }*/
		            sqlInsertBillDtl += "('" + objBillDtl.getStrItemCode() + "','" + objBillDtl.getStrItemName() + "'"
		                    + ",'" + objBillDtl.getStrBillNo() + "','" + objBillDtl.getStrAdvBookingNo() + "'," + objBillDtl.getDblRate() + ""
		                    + ",'" + objBillDtl.getDblQuantity() + "','" + amount + "'"
		                    + "," + objBillDtl.getDblTaxAmount() + ",'" + objBillDtl.getDteBillDate() + "'"
		                    + ",'" + objBillDtl.getStrKOTNo() + "','" + objBillDtl.getStrClientCode() + "'"
		                    + ",'" + objBillDtl.getStrCustomerCode() + "','" + objBillDtl.getTmeOrderProcessing() + "'"
		                    + ",'" + objBillDtl.getStrDataPostFlag() + "','" + objBillDtl.getStrMMSDataPostFlag() + "'"
		                    + ",'" + objBillDtl.getStrManualKOTNo() + "','" + objBillDtl.getTdhYN() + "'"
		                    + ",'" + objBillDtl.getStrPromoCode() + "','" + objBillDtl.getStrCounterCode() + "'"
		                    + ",'" + objBillDtl.getStrWaiterNo() + "','" + objBillDtl.getDblDiscountAmt() + "'"
		                    + ",'" + objBillDtl.getDblDiscountPer() + "'),";
		        }
		        if (arrListKOTWiseBillDtl.size() > 0)
		        {
		            StringBuilder sb = new StringBuilder(sqlInsertBillDtl);
		            int index = sb.lastIndexOf(",");
		            sqlInsertBillDtl = sb.delete(index, sb.length()).toString();
		            System.out.println(sqlInsertBillDtl);
		            webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertBillDtl).executeUpdate();
		        }

		        sql = "select strItemCode,dblQuantity,strPromotionCode "
		                + " from tblbillpromotiondtl "
		                + " where strBillNo='" + billNo.trim()+ "' and strItemCode='" + itemCodeForVoid + "' ";
                   Query querySqlProm= webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
 	       	       List listSqlProm= querySqlProm.list();
 	       	     
 	       	       if(listSqlProm.size()>0)
 	       	       {
 	        	     Object[] obj = (Object[]) listSqlProm.get(0);
		            if (voidedItemQty < Integer.parseInt(obj[1].toString()))
		            {
		                double qty = voidedItemQty - Integer.parseInt(obj[1].toString());
		                sql = "update tblbillpromotiondtl set dblQuantity='" + qty + "' "
		                        + " where strBillNo='" + billNo.trim() + "' and strItemCode='" + itemCodeForVoid + "'"
		                        + " and strPromotionCode='" + obj[2].toString()+ "' ";
		                Query  queryUpdate = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                         queryUpdate.executeUpdate();
		            }
		            else
		            {
		                sql = "delete from tblbillpromotiondtl "
		                        + " where strBillNo='" + billNo.trim() + "' and strItemCode='" + itemCodeForVoid + "'"
		                        + " and strPromotionCode='" + obj[2].toString() + "' ";
		                Query  queryUpdate = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
                        queryUpdate.executeUpdate();
		            }
		        }
//		        rsPromoBillItem.close();

		        sql = "select strItemCode from tblbilldtl "
		                + " where strBillNo='" + billNo.trim() + "' and strItemCode='" + itemCodeForVoid + "' ";
		        Query querySqlbill= webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
	       	       List listSqlbill= querySqlbill.list();
	       	     
	       	       if(listSqlbill.size()>0)
	       	       {
	        	     Object[] obj = (Object[]) listSqlbill.get(0);
		         
		            sqlDelete = "delete from tblbillpromotiondtl "
		                    + " where strBillNo='" + billNo + "' and strItemCode='" + itemCodeForVoid + "' ";
		            webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete).executeUpdate();
		        }
		        

		          
		        arrListBillDtl.clear();
		        sql = "select a.strItemCode,a.strItemName,a.strBillNo,a.strAdvBookingNo,a.dblRate,sum(a.dblQuantity) "
		                + ",sum(a.dblAmount),sum(a.dblTaxAmount),a.dteBillDate,a.strKOTNo,a.strClientCode,a.strCustomerCode "
		                + ",a.tmeOrderProcessing,a.strDataPostFlag,a.strMMSDataPostFlag,a.strManualKOTNo,a.tdhYN "
		                + ",a.strPromoCode,a.strCounterCode,a.strWaiterNo,a.dblDiscountAmt,a.dblDiscountPer,b.strSubGroupCode "
		                + ",c.strSubGroupName,c.strGroupCode,d.strGroupName "
		                + "from tblbilldtl a,tblitemmaster b ,tblsubgrouphd c,tblgrouphd d "
		                + "where a.strBillNo='" + billNo.trim() + "' and a.strItemCode=b.strItemCode "
		                + "and b.strSubGroupCode=c.strSubGroupCode and c.strGroupCode=d.strGroupCode "
		                + "group by a.strItemCode,a.strItemName,a.strBillNo;";
		        Query querySql= webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
	       	    List listSql= querySql.list();
	       	       if(listSql.size()>0)
	       	       {
	       	    	   for(int j=0;j<listSql.size();j++ )
	       	    	   {
	        	     Object[] obj = (Object[]) listSql.get(0);
	
	
		            clsBillDtl objBillDtl = new clsBillDtl();
		            objBillDtl.setStrItemCode(obj[0].toString());
		            objBillDtl.setStrItemName(obj[1].toString());
		            objBillDtl.setStrBillNo(obj[2].toString());
		            objBillDtl.setStrAdvBookingNo(obj[3].toString());
		            objBillDtl.setDblRate(Double.parseDouble(obj[4].toString()));
		            objBillDtl.setDblQuantity(Double.parseDouble(obj[5].toString()));
		            objBillDtl.setDblAmount(Double.parseDouble(obj[6].toString()));
		            objBillDtl.setDblTaxAmount(Double.parseDouble(obj[7].toString()));
		            objBillDtl.setDteBillDate(obj[8].toString());
		            objBillDtl.setStrKOTNo(obj[9].toString());
		            objBillDtl.setStrClientCode(obj[10].toString());
		            objBillDtl.setStrCustomerCode(obj[11].toString());
		            objBillDtl.setTmeOrderProcessing(obj[12].toString());
		            objBillDtl.setStrDataPostFlag(obj[13].toString());
		            objBillDtl.setStrMMSDataPostFlag(obj[14].toString());
		            objBillDtl.setStrManualKOTNo(obj[15].toString());
		            objBillDtl.setTdhYN(obj[16].toString());
		            objBillDtl.setStrPromoCode(obj[17].toString());
		            objBillDtl.setStrCounterCode(obj[18].toString());
		            objBillDtl.setStrWaiterNo(obj[19].toString());
		            objBillDtl.setDblDiscountAmt(Double.parseDouble(obj[20].toString()));
		            objBillDtl.setDblDiscountPer(Double.parseDouble(obj[21].toString()));
		            objBillDtl.setSubGrouName(obj[23].toString());
		            objBillDtl.setGroupName(obj[25].toString());
		            arrListBillDtl.add(objBillDtl);
		        }
	       	    	   }
		        

		        sqlDelete = "delete from tblbillhd where strBillNo='" + billNo.trim() + "' "
		                + "and strPOSCode='" + strPOSCode + "'";
		        webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete).executeUpdate();
		        clsBillHd objBillHd = arrListBillHd.get(0);
		        objBillHd.setDblSubTotal(objBillHd.getDblSubTotal());
		        objBillHd.setDblGrandTotal(objBillHd.getDblGrandTotal());
		        //objBillHd.setDblSubTotal(objBillHd.getDblSubTotal()-promoItemAmt);
		        //objBillHd.setDblGrandTotal(objBillHd.getDblGrandTotal()-promoItemAmt);
		        arrListBillHd.set(0, objBillHd);
//		        funFillItemGrid(objBillHd.getStrBillNo());

		        String sqlInsert = "insert into tblbillhd(strBillNo,strAdvBookingNo,dteBillDate,strPOSCode,strSettelmentMode,"
		                + "dblDiscountAmt,dblDiscountPer,dblTaxAmt,dblSubTotal,dblGrandTotal,strTakeAway,strOperationType"
		                + ",strUserCreated,strUserEdited,dteDateCreated,dteDateEdited,strClientCode"
		                + ",strTableNo,strWaiterNo,strCustomerCode,strManualBillNo,intShiftCode"
		                + ",intPaxNo,strDataPostFlag,strReasonCode,strRemarks,dblTipAmount,dteSettleDate"
		                + ",strCounterCode,dblDeliveryCharges,strAreaCode,strDiscountRemark,strTakeAwayRemarks,strDiscountOn ) "
		                + "values('" + objBillHd.getStrBillNo() + "','" + objBillHd.getStrAdvBookingNo() + "'"
		                + ",'" + objBillHd.getDteBillDate() + "','" + objBillHd.getStrPOSCode() + "'"
		                + ",'" + objBillHd.getStrSettelmentMode() + "','" + objDecFormat.format(objBillHd.getDblDiscountAmt()) + "'"
		                + ",'" + objDecFormat.format(objBillHd.getDblDiscountPer()) + "','" + objBillHd.getDblTaxAmt() + "'"
		                + ",'" + objBillHd.getDblSubTotal() + "','" + Math.rint(objBillHd.getDblGrandTotal()) + "'"
		                + ",'" + objBillHd.getStrTakeAway() + "','" + objBillHd.getStrOperationType() + "'"
		                + ",'" + objBillHd.getStrUserCreated() + "','" + objBillHd.getStrUserEdited() + "'"
		                + ",'" + objBillHd.getDteDateCreated() + "','" + objBillHd.getDteDateEdited() + "'"
		                + ",'" + objBillHd.getStrClientCode() + "','" + objBillHd.getStrTableNo() + "'"
		                + ",'" + objBillHd.getStrWaiterNo() + "','" + objBillHd.getStrCustomerCode() + "'"
		                + ",'" + objBillHd.getStrManualBillNo() + "'," + objBillHd.getIntShiftCode() + ""
		                + "," + objBillHd.getIntPaxNo() + ",'" + objBillHd.getStrDataPostFlag() + "','" + objBillHd.getStrReasonCode() + "'"
		                + ",'" + objBillHd.getStrRemarks() + "'," + objBillHd.getDblTipAmount() + ",'" + objBillHd.getDteSettleDate() + "'"
		                + ",'" + objBillHd.getStrCounterCode() + "'," + objBillHd.getDblDeliveryCharges() + ""
		                + ", '" + objBillHd.getStrAreaCode() + "','" + objBillHd.getStrDiscountRemark() + "'"
		                + ",'" + objBillHd.getStrTakeAwayRemarks() + "','" + objBillHd.getStrDiscountOn() + "')";
		        webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsert).executeUpdate();
	       

		        //update billseriesbilldtl grand total
		        webPOSSessionFactory.getCurrentSession().createSQLQuery("update tblbillseriesbilldtl set dblGrandTotal='" + objBillHd.getDblGrandTotal() + "' where strHdBillNo='" + billNo + "' ").executeUpdate();

		        sqlDelete = "delete from tblbillmodifierdtl where strBillNo='" + billNo + "'";
		        webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete).executeUpdate();
		        String sqlInsertBillModDtl = "insert into  tblbillmodifierdtl "
		                + "(strBillNo,strItemCode,strModifierCode,strModifierName,dblRate"
		                + ",dblQuantity,dblAmount,strClientCode,strCustomerCode"
		                + ",strDataPostFlag,strMMSDataPostFlag,dblDiscPer,dblDiscAmt )"
		                + " values ";
		        for (clsBillModifierDtl objBillModDtl : arrListBillModifierDtl)
		        {
		            sqlInsertBillModDtl += "('" + objBillModDtl.getStrBillNo() + "','" + objBillModDtl.getStrItemCode() + "'"
		                    + ",'" + objBillModDtl.getStrModifierCode() + "','" + objBillModDtl.getStrModifierName() + "'"
		                    + "," + objBillModDtl.getDblRate() + "," + objBillModDtl.getDblQuantity() + "," + objBillModDtl.getDblAmount() + ""
		                    + ",'" + objBillModDtl.getStrClientCode() + "','" + objBillModDtl.getStrCustomerCode() + "'"
		                    + ",'" + objBillModDtl.getStrDataPostFlag() + "','" + objBillModDtl.getStrMMSDataPostFlag() + "','" + objBillModDtl.getDblDiscPer() + "','" + objBillModDtl.getDblDiscAmt() + "'),";
		        }

		        if (arrListBillModifierDtl.size() > 0)
		        {
		            StringBuilder sb = new StringBuilder(sqlInsertBillModDtl);
		            int index = sb.lastIndexOf(",");
		            sqlInsertBillModDtl = sb.delete(index, sb.length()).toString();
		            webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertBillModDtl).executeUpdate();
		        }

		        sqlDelete = "delete from tblbilltaxdtl where strBillNo='" + billNo.trim() + "'";
		        webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDelete).executeUpdate();
		        for (clsBillTaxDtl objBillTaxDtl : arrListBillTaxDtl)
		        {
		            String sqlInsertTaxDtl = "insert into tblbilltaxdtl "
		                    + "(strBillNo,strTaxCode,dblTaxableAmount,dblTaxAmount,strClientCode) "
		                    + "values('" + objBillTaxDtl.getStrBillNo() + "','" + objBillTaxDtl.getStrTaxCode() + "'"
		                    + "," + objBillTaxDtl.getDblTaxableAmount() + "," + objBillTaxDtl.getDblTaxAmount() + ""
		                    + ",'" + strClientCode+ "')";
		            webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlInsertTaxDtl).executeUpdate();
		 	       
		        }

		        //delete all discount
		        webPOSSessionFactory.getCurrentSession().createSQLQuery("delete from tblbilldiscdtl where strBillNo='" + billNo + "'  ").executeUpdate();
		        //update bill discount
		        StringBuilder insertDisc = new StringBuilder("insert into tblbilldiscdtl values ");
		        for (int i = 0; i < arrListBillDiscDtl.size(); i++)
		        {
		            clsBillDiscountDtl objBillDiscountDtl = arrListBillDiscDtl.get(i);

		            if (i == 0)
		            {
		                insertDisc.append("('" + billNo + "','" + objBillDiscountDtl.getPOSCode() + "','" + objBillDiscountDtl.getDiscAmt() + "','" + objBillDiscountDtl.getDiscPer() + "','" + objBillDiscountDtl.getDiscOnAmt() + "',"
		                        + "'" + objBillDiscountDtl.getDiscOnType() + "','" + objBillDiscountDtl.getDiscOnValue() + "','" + objBillDiscountDtl.getReason() + "','" + objBillDiscountDtl.getRemark() + "','" + objBillDiscountDtl.getUserCreated() + "',"
		                        + "'" + userCode + "','" + objBillDiscountDtl.getDateCreated() + "','" + voidBillDate + "','" + strClientCode+ "','N')");
		            }
		            else
		            {
		                insertDisc.append(",('" + billNo + "','" + objBillDiscountDtl.getPOSCode() + "','" + objBillDiscountDtl.getDiscAmt() + "','" + objBillDiscountDtl.getDiscPer() + "','" + objBillDiscountDtl.getDiscOnAmt() + "',"
		                        + "'" + objBillDiscountDtl.getDiscOnType() + "','" + objBillDiscountDtl.getDiscOnValue() + "','" + objBillDiscountDtl.getReason() + "','" + objBillDiscountDtl.getRemark() + "','" + objBillDiscountDtl.getUserCreated() + "',"
		                        + "'" + userCode + "','" + objBillDiscountDtl.getDateCreated() + "','" + voidBillDate + "','" + strClientCode+ "','N')");
		            }
		        }
		        //insert new entries
		        if (insertDisc.length() > 35)
		        {
		        	 webPOSSessionFactory.getCurrentSession().createSQLQuery(insertDisc.toString()).executeUpdate();
		        }
		    }
		  
			public String funLoadTable(String strPosCode,String  tableName)
			{
				String tableNo="";
			
				try{
				String sqlFillCombo = "select b.strTableNo,a.strTableName "
		                + "from tbltablemaster a,tblitemrtemp b "
		                + "where a.strTableNo=b.strTableNo "
		                + "and  (a.strPOSCode='" + strPosCode + "' OR a.strPOSCode='All') and strNCKotYN='N' and a.strTableName='"+tableName+"' "
		                + "group by b.strTableNo "
		                + "order by a.strTableName;";
				
				
				   Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlFillCombo.toString());
			  	    List listSql1= querySql1.list();
			  	  if(listSql1.size()>0)
			  	  {
			  	    Object[] obj1 = (Object[]) listSql1.get(0);
			  	    tableNo=obj1[0].toString();
			  	   }
			  
				}catch(Exception e)
				{
					
				}
				return tableNo;
			}
			
			
			public JSONObject funVoidBill(String posDate,String billNo,String favoritereason,String remark,String userCode,String strPOSCode,String strClientCode)
		    {
				JSONObject jObjRetrun=new JSONObject();
				String voidBillDate="";
				String sql="";
				String[] reason;
				String reasoncode = "";
		        try
		        {

		        	String   sqlQuery = "select strAuditing from tbluserdtl where strUserCode='" + userCode + "' and strFormName='Void Bill'";
		            java.util.Date dt = new java.util.Date();
		            String time = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
		            StringBuilder sb = new StringBuilder(posDate);
		            int seq1 = sb.lastIndexOf(" ");
		            String split = sb.substring(0, seq1);
		            voidBillDate = split + " " + time;

		            if (!billNo.isEmpty())
		            {
		            	int i = 0;
		            
		                int reasoncount = 0;
		                sql = "select count(strReasonName) from tblreasonmaster where strVoidBill='Y'";
		         	    Query querySql1 = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
				  	    List listSql1= querySql1.list();
				  	  if(listSql1.size()>0)
				  	  {
				  		  for(Object obj1 :listSql1){
				  			  
//				  	    Object[] obj1 = (Object[]) listSql1.get(0);
		                
		                reasoncount = Integer.parseInt(obj1.toString());
		                }
				  	  }
		                if (reasoncount > 0)
		                {
		                    reason = new String[reasoncount];
		                    sql = "select strReasonName from tblreasonmaster where strVoidBill='Y'";
		                    Query querySqlReasn = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
					  	    List listSqlResn= querySqlReasn.list();
		                    i = 0;
		                    if(listSqlResn.size()>0)
						  	  {
						  		  for(int j=0;j<listSqlResn.size();j++){
						  			 Object obj1 = (Object) listSqlResn.get(j);
		                        reason[i] =obj1.toString();
		                        i++;
		                    }
//		                    String favoritereason = (String) JOptionPane.showInputDialog(this, "Please Select Reason?", "Reason", JOptionPane.QUESTION_MESSAGE, null, reason, reason[0]);
		                    if (favoritereason != null)
		                    {
		                        sql = "select strReasonCode from tblreasonmaster where strReasonName='" + favoritereason + "' "
		                                + "and strVoidBill='Y'";
		                        Query querySqlReasnFav = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
						  	    List listSqlResnFav= querySqlReasnFav.list();
						  	    if(!listSqlResnFav.isEmpty())
						  	    {
						  	    	for(int j=0;j<listSqlResnFav.size();j++)
						  	    	{
						  	    		Object obj=(Object)listSqlResnFav.get(j);
						  	    		reasoncode = obj.toString();
		                        }
						  	    }
//		                      int choice = JOptionPane.showConfirmDialog(this, "Do you want to Void Bill ?", "Void Bill", JOptionPane.YES_NO_OPTION);
//		                        if (choice == JOptionPane.YES_OPTION)
//		                        {
		                           
		                      

		                            String billDate = "";
		                            
		                            String shiftNo = "1";
		                            sql = "select left(dteBillDate,10) ,right(dteDateCreated,8),intShiftCode from tblbillhd"
		                                    + " where strBillNo='" + billNo + "'";
		                            Query querySqlBillNo = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
							  	    List listSqlBillNo= querySqlBillNo.list();
							  	    if(!listSqlBillNo.isEmpty())
							  	    {
							  	    	Object[] obj=(Object[])listSqlBillNo.get(0);
							  	    
							  	    	 billDate = obj[0].toString() + " " + obj[1].toString();
			                             shiftNo = obj[2].toString();
			                            
							  	    }
		                       
		                            sql = "select a.strItemCode,a.strItemName,a.strBillNo,a.dblQuantity,a.dblAmount,"
		                                    + "a.dblTaxAmount,a.dteBillDate,b.strTableNo,a.strKOTNo,b.intShiftCode "
		                                    + "from tblbilldtl a,tblbillhd b "
		                                    + "where a.strBillNo=b.strBillNo and a.strBillNo='" + billNo + "'";
		                            

		                            Query querySqlItem = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
							  	    List listSqlItem= querySqlItem.list();
							  	    if(!listSqlItem.isEmpty())
							  	    {  
							  	    	for(int j=0;j<listSqlItem.size();j++)
							  	    	{	
							  	    	Object[] obj=(Object[])listSqlItem.get(j);
				
			                            String itemCode = obj[0].toString();
		                                String itemname = obj[1].toString();
		                                String billno = obj[2].toString();
		                                String qty =obj[3].toString();
		                                String amount = obj[4].toString();
		                                String taxAmount = obj[5].toString();
		                                billDate = obj[6].toString();
		                                String tableNo =obj[7].toString();
		                                String KOTNo =obj[8].toString();

		                                sql = "insert into tblvoidmodifierdtl(strBillNo,strItemCode,strModifierCode,"
		                                        + "strModifierName,dblQuantity,dblAmount,strClientCode,strCustomerCode"
		                                        + ",strRemarks,strReasonCode)"
		                                        + " (select strBillNo,strItemCode,strModifierCode,strModifierName,dblQuantity,"
		                                        + "dblAmount,strClientCode,strCustomerCode,'" + remark + "','" + reasoncode + "' "
		                                        + "from tblbillmodifierdtl "
		                                        + "where strBillNo='" + billno + "' and left(strItemCode,7)='" + itemCode + "')";
		                                //System.out.println("recordset:"+sql);
		                                if (userCode.equalsIgnoreCase(("super")))
		                                {
		                                	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
		                                }
		                                else
		                                {
		                                    Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
									  	    List listSql= querySql.list();
									  	    if(!listSql.isEmpty())
									  	    {  
									  	    
									  	    	Object obj1=(Object)listSql.get(j);
		                                
		                                        if (Boolean.parseBoolean(obj1.toString()))
		                                        {
		                                        	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
		                                        }
		                                    }
		                                }

		                                sql = "insert into tblvoidbilldtl(strPosCode,strReasonCode,strReasonName,strItemCode"
		                                        + ",strItemName,strBillNo,intQuantity,dblAmount,dblTaxAmount,dteBillDate,"
		                                        + "strTransType,dteModifyVoidBill,intShiftCode,strUserCreated,strClientCode"
		                                        + ",strKOTNo,strRemarks) "
		                                        + "values('" + strPOSCode + "','" + reasoncode + "','"
		                                        + favoritereason + "','" + itemCode + "','" + itemname + "','" + billno + "','"
		                                        + qty + "','" + amount + "','" + taxAmount + "','" + billDate + "','" + "VB" + "','"
		                                        + voidBillDate + "'," + shiftNo + ""
		                                        + ",'" + userCode + "','" +strClientCode+ "'"
		                                        + ",'" + KOTNo + "','" + remark + "')";
		                                //System.out.println("item"+item+"\t"+"quantity"+quantity+"\t"+"amount"+amount);
		                                //System.out.println(sql);

		                                if (userCode.equalsIgnoreCase(("super")))
		                                {
		                                	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
		                                }
		                                else
		                                {
		                                	   Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
										  	    List listSql= querySql.list();
										  	    if(!listSql.isEmpty())
										  	    {  
										  	    	Object obj1=(Object)listSql.get(0);
			                                
			                                        if (Boolean.parseBoolean(obj1.toString()))
			                                        {
			                                        	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
			                                        }
			                                    }
		                                }
		                                String tableStatus = funGetTableStatus(tableNo);
		                                if (tableStatus.equalsIgnoreCase("Normal"))
		                                {
		                                    String updateQuery = "update tbltablemaster set strStatus='Normal',intPaxNo=0 "
		                                            + "where strTableNo='" + tableNo + "'";
		                                    webPOSSessionFactory.getCurrentSession().createSQLQuery( updateQuery.toString()).executeUpdate();
		                                }
		                                else
		                                {
		                                    String updateQuery = "update tbltablemaster set strStatus='" + tableStatus + "' "
		                                            + "where strTableNo='" + tableNo + "'";
		                                    webPOSSessionFactory.getCurrentSession().createSQLQuery( updateQuery.toString()).executeUpdate();
		                                    
		                                }
		                            }
							  	    }
		                            sql = "select * from tblvoidbillhd where strBillNo='" + billNo + "'";
		                            Query querySqlvoidbill = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
							  	    List listSqlvoidbill= querySqlvoidbill.list();
							  	    if(!listSqlvoidbill.isEmpty())
							  	    {  
							  	    	Object[] obj=(Object[])listSqlvoidbill.get(0);
		                                sql = "delete from tblvoidbillhd where strBillNo='" + billNo + "'";
		                                webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();

		                                sql = "insert into tblvoidbillhd (strPosCode,strReasonCode,strReasonName,strBillNo,"
		                                        + "dblActualAmount,dblModifiedAmount,dteBillDate,strTransType,dteModifyVoidBill,strTableNo,strWaiterNo"
		                                        + ",intShiftCode,strUserCreated,strUserEdited,strClientCode,strRemark) "
		                                        + "(select '" + strPOSCode + "','" + reasoncode + "','" + favoritereason + "','"
		                                        + billNo+ "'," + "dblGrandTotal,dblGrandTotal,'" + billDate + "','VB','"
		                                        + voidBillDate + "',strTableNo,strWaiterNo,'" + shiftNo
		                                        + "','" + userCode + "','" + userCode + "',strClientCode,'" + remark + "' "
		                                        + "from tblbillhd where strBillNo='" + billNo + "')";
		                                if (userCode.equalsIgnoreCase(("super")))
		                                {
		                                	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
		                                }
		                                else
		                                {
		                                	  Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
										  	    List listSql= querySql.list();
										  	    if(!listSql.isEmpty())
										  	    {  
										  	    	Object obj1=(Object)listSql.get(0);
			                                
			                                        if (Boolean.parseBoolean(obj1.toString()))
			                                        {
			                                        	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
			                                        }
			                                    }
		                                }

		                                sql = "update tblvoidbillhd set dblActualAmount=dblActualAmount+" +Double.parseDouble(obj[4].toString()) + ""
		                                        + ",dblModifiedAmount=dblModifiedAmount+" + Double.parseDouble(obj[58].toString())+ " "
		                                        + " where strBillNo='" + billNo + "' ";
		                                if (userCode.equalsIgnoreCase(("super")))
		                                {
		                                	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
		                                }
		                                else
		                                {
		                                	  Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
										  	    List listSql= querySql.list();
										  	    if(!listSql.isEmpty())
										  	    {  
										  	    	Object obj1=(Object)listSql.get(0);
			                                
			                                        if (Boolean.parseBoolean(obj1.toString()))
			                                        {
			                                        	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
			                                        }
			                                    }
		                                }
		                            }
		                            else
		                            {
		                                sql = "insert into tblvoidbillhd (strPosCode,strReasonCode,strReasonName,strBillNo,"
		                                        + "dblActualAmount,dblModifiedAmount,dteBillDate,strTransType,dteModifyVoidBill,strTableNo,strWaiterNo"
		                                        + ",intShiftCode,strUserCreated,strUserEdited,strClientCode,strRemark) "
		                                        + "(select '" + strPOSCode + "','" + reasoncode + "','" + favoritereason + "','"
		                                        + billNo+ "'," + "dblGrandTotal,dblGrandTotal,'" + billDate + "','VB','"
		                                        + voidBillDate + "',strTableNo,strWaiterNo,'" + shiftNo
		                                        + "','" + userCode + "','" + userCode + "',strClientCode,'" + remark + "' "
		                                        + "from tblbillhd where strBillNo='" + billNo + "')";
		                                if (userCode.equalsIgnoreCase(("super")))
		                                {
		                                	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
		                                }
		                                else
		                                {
		                                	  Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
										  	    List listSql= querySql.list();
										  	    if(!listSql.isEmpty())
										  	    {  
										  	    	Object obj1=(Object)listSql.get(0);
			                                
			                                        if (Boolean.parseBoolean(obj1.toString()))
			                                        {
			                                        	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
			                                        }
			                                    }
		                                }
		                            }
		                            }
		                            //System.out.println(sql);

		                            sql = "insert into tblvoidbillsettlementdtl"
		                                    + "(strBillNo,strSettlementCode,dblSettlementAmt,"
		                                    + "dblPaidAmt,strExpiryDate,strCardName,"
		                                    + "strRemark,strClientCode,strCustomerCode,"
		                                    + "dblActualAmt,dblRefundAmt,strGiftVoucherCode)"
		                                    + "(select strBillNo,strSettlementCode,dblSettlementAmt,"
		                                    + "dblPaidAmt,strExpiryDate,strCardName,"
		                                    + "strRemark,strClientCode,strCustomerCode,"
		                                    + "dblActualAmt,dblRefundAmt,strGiftVoucherCode "
		                                    + " from tblbillsettlementdtl where strBillNo='" + billNo + "')";
		                            if (userCode.equalsIgnoreCase(("super")))
	                                {
	                                	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
	                                }
	                                else
	                                {
	                                	  Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery( sqlQuery.toString());
									  	    List listSql= querySql.list();
									  	    if(!listSql.isEmpty())
									  	    {  
									  	    	Object obj1=(Object)listSql.get(0);
		                                
		                                        if (Boolean.parseBoolean(obj1.toString()))
		                                        {
		                                        	webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString()).executeUpdate();
		                                        }
		                                    }
	                                }
	                            }

//		                            ResultSet rsDate = clsGlobalVarClass.dbMysql.executeResultSet("select date(dteBillDate),strPOSCode from tblbillhd where strBillNo='" + billNo + "'; ");
		                            
		                    String date = "";
                            String POSCode ="";
		                            Query querySqlDate = webPOSSessionFactory.getCurrentSession().createSQLQuery( "select date(dteBillDate),strPOSCode from tblbillhd where strBillNo='" + billNo + "'; ");
								  	    List listSqlDate= querySqlDate.list();
								  	 if(!listSqlDate.isEmpty())
								  	 {    
								  	 Object[] obj=(Object[])listSqlDate.get(0);
								  	 date = obj[0].toString();
		                             POSCode = obj[1].toString();
								  	 }
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbilldtl where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbillhd where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbillmodifierdtl where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbillsettlementdtl where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblhomedelivery where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbilltaxdtl where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbilldiscdtl where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbillpromotiondtl where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblbillcomplementrydtl where strBillNo='" + billNo + "'").executeUpdate();
								  	webPOSSessionFactory.getCurrentSession().createSQLQuery("Delete from tblhomedeldtl where strBillNo='" + billNo + "'").executeUpdate();
								  	JSONObject jobj=objSetUpService.funGetParameterValuePOSWise(strClientCode, strPOSCode, "gEnableBillSeries");
								  	String gEnableShiftYN=jobj.get("gEnableBillSeries").toString();
								   
								  	jObjRetrun.put("sucessfully", "sucessfully");
								  	  if (gEnableShiftYN.equals("Y"))
		                            {

//		                                objUtility.funPrintBill(billNo, "Void", date, POSCode);

		                                 sql = "select a.strPOSCode,a.strHdBillNo,a.strDtlBillNos "
		                                        + "from tblbillseriesbilldtl a "
		                                        + "where a.strHdBillNo='" + billNo + "'"
		                                        + "and a.strPOSCode='" + POSCode + "'  ";
		                                 Query querySqlbill = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
									  	    List listSqlbill= querySqlbill.list();
									  	    if(!listSqlbill.isEmpty())
									  	    {  
									  	    	Object[] obj=(Object[])listSqlbill.get(0);
		                              
		                                    String dtlBills = obj[2].toString();
		                                    String arrDtlBills[] = dtlBills.split(",");
		                                    for (int j = 0; j < arrDtlBills.length; j++)
		                                    {
//		                                        objUtility.funPrintBill(arrDtlBills[j], "Void", date, POSCode);
		                                    }
		                                }
//		                                rsDtlBillList.close();
		                            }
		                            else
		                            {
//		                                objUtility.funPrintBill(billNo, "Void", date, POSCode);
		                            }

//		                            funResetField();
//		                            funFillBillNoGrid("");
//		                        }
//		                        if (clsGlobalVarClass.gConnectionActive.equals("Y"))
//		                        {
//		                            if (clsGlobalVarClass.gDataSendFrequency.equals("After Every Bill"))
//		                            {
//		                                clsGlobalVarClass.funInvokeHOWebserviceForTrans("Audit", "Void");
//		                            }
//		                        }
		                    }
		                }
		                else
		                {
//		                    new frmOkPopUp(null, "Please Create Reason First", "Error", 1).setVisible(true);
		                }
//		            }
//		            else
//		            {
////		                new frmOkPopUp(null, "Please select Item first", "Error", 1).setVisible(true);
//		            }
		        }
		        catch (Exception e)
		        {
//		            objUtility.funWriteErrorLog(e);
		            e.printStackTrace();
		        }
		        finally
		        {
		        	return jObjRetrun;
		        }
		    }
		        

		        private String funGetTableStatus(String tableNo)
		        {
		            String tableStatus = "Normal";
		            try
		            {
		                String sql = "select strTableNO from tblitemrtemp where strTableNO='" + tableNo + "' ";
		               
		                Query querySqlbill = webPOSSessionFactory.getCurrentSession().createSQLQuery( sql.toString());
				  	    List listSqlbill= querySqlbill.list();
				  	    if(!listSqlbill.isEmpty())
				  	    {  
				  	    	Object[] obj=(Object[])listSqlbill.get(0);
				  
		                    tableStatus = "Occupied";
		                }
		            }
		            catch (Exception e)
		            {
		                e.printStackTrace();
		            }
		            finally
		            {
		                return tableStatus;
		            }
		        }


	}

