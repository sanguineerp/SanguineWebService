package com.apos.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsBillItemTaxDtl;
import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.bean.clsTaxCalculationBean;
import com.apos.dao.clsSetupDao;
import com.apos.service.clsUtilityService;
import com.apos.util.clsSendMail;


@Controller
@Path("/clsUtilityController")
@Transactional(value = "webPOSTransactionManager")
public class clsUtilityController {

	@Autowired
	private SessionFactory WebPOSSessionFactory;
	
	@Autowired 
	clsSendMail obSendMail;
	
	@Autowired
	clsUtilityService obUtilityService;
	@Autowired
	clsSetupDao objSetupDao;


    public List funCalculateTax(List<clsPOSItemDetailFrTaxBean> arrListItemDtl, String POSCode, String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountAmt, String transType, String settlementCode) throws Exception
    {
        return funCheckDateRangeForTax(arrListItemDtl, POSCode, dtPOSDate, billAreaCode, operationTypeForTax, subTotal, discountAmt, transType, settlementCode);
    }

    private List funCheckDateRangeForTax(List<clsPOSItemDetailFrTaxBean> arrListItemDtl, String POSCode, String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountAmt, String transType, String settlementCode) throws Exception
    {
        List<clsTaxCalculationBean> arrListTaxDtl = new ArrayList<clsTaxCalculationBean>();
        String taxCode = "", taxName = "", taxOnGD = "", taxCal = "", taxIndicator = "";
        String opType = "", taxAreaCodes = "", taxOnTax = "No", taxOnTaxCode = "";
        double taxPercent = 0.00, taxableAmount = 0.00, taxCalAmt = 0.00;
      Query  querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery("truncate table tbltaxtemp;");// Empty Tax Temp Table
      querySql.executeUpdate(); 

        StringBuilder sbSql = new StringBuilder();
        sbSql.setLength(0);
        sbSql.append("select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent"
                + ",a.dblAmount,a.strTaxOnGD,a.strTaxCalculation,ifnull(a.strTaxIndicator,'NA'),a.strAreaCode,a.strOperationType"
                + ",a.strItemType,a.strTaxOnTax,a.strTaxOnTaxCode "
                + "from tbltaxhd a,tbltaxposdtl b "
                + "where a.strTaxCode=b.strTaxCode and b.strPOSCode='" + POSCode + "' ");
        if (transType.equals("Tax Regen"))
        {
            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
        }
        else
        {
            sbSql.append(" and date(a.dteValidFrom) <='" + dtPOSDate + "' and date(a.dteValidTo)>='" + dtPOSDate + "' ");
        }
        sbSql.append(" and a.strTaxOnSP='Sales' "
                + "order by a.strTaxOnTax,a.strTaxDesc");
        querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sbSql.toString());

        List listSqlModLive = querySql.list();
		   if(listSqlModLive.size()>0)
		    {
			  
		    
			   for(int i=0 ;i<listSqlModLive.size();i++ )
		    
		    	{
				   Object[] objM = (Object[]) listSqlModLive.get(i);
				  
            taxCode = objM[0].toString();
            taxName = objM[1].toString();
            taxOnGD =objM[6].toString();
            taxCal = objM[7].toString();
            taxIndicator = objM[8].toString();
            taxOnTax = objM[12].toString();
            taxOnTaxCode = objM[13].toString();

            taxPercent = Double.parseDouble(objM[4].toString());
            taxableAmount = 0.00;
            taxCalAmt = 0.00;

            String sqlTaxOn = "select strAreaCode,strOperationType,strItemType "
                    + "from tbltaxhd where strTaxCode='" + taxCode + "'";
            querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sqlTaxOn.toString());
      
            List listSql = querySql.list();
 		   if(listSql.size()>0)
 		    {
 		 
 				   Object[] obj = (Object[]) listSql.get(0);
               
 				   taxAreaCodes =obj[0].toString();
                opType =obj[1].toString();
            
 		    }
            if (funCheckAreaCode(taxAreaCodes, billAreaCode))
            {
                if (funCheckOperationType(opType, operationTypeForTax))
                {
                    if (funFindSettlementForTax(taxCode, settlementCode))
                    {
                    	clsTaxCalculationBean objTaxDtls = new clsTaxCalculationBean();
                        if (taxIndicator.trim().length() > 0) // For Indicator Based Tax
                        {
                            double taxIndicatorTotal = funGetTaxIndicatorTotal(taxIndicator, arrListItemDtl);
                            if (taxIndicatorTotal > 0)
                            {
                                double discAmt = 0, discPer = 0;//                              
                                discAmt = funGetTaxIndicatorBasedDiscAmtTotal(taxIndicator, arrListItemDtl);
                                if (taxIndicatorTotal > 0)
                                {
                                    discPer = (discAmt / taxIndicatorTotal) * 100;
                                }

                                if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation
                                {
                                    taxIndicatorTotal += funGetTaxAmountForTaxOnTaxForIndicatorTax(taxOnTaxCode, taxIndicatorTotal, arrListTaxDtl);
                                }
                                if (taxOnGD.equals("Gross"))
                                {
                                    taxableAmount = taxIndicatorTotal;
                                }
                                else
                                {
                                    taxableAmount = taxIndicatorTotal - ((taxIndicatorTotal * discPer) / 100);
                                }

                                if (taxCal.equals("Forward")) // Forward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * (taxPercent / 100);
                                }
                                else // Backward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * 100 / (100 + taxPercent);
                                    taxCalAmt = taxableAmount - taxCalAmt;
                                }
                                objTaxDtls.setTaxCode(taxCode);
                                objTaxDtls.setTaxName(taxName);
                                objTaxDtls.setTaxableAmount(taxableAmount);
                                objTaxDtls.setTaxAmount(taxCalAmt);
                                objTaxDtls.setTaxCalculationType(taxCal);
                                arrListTaxDtl.add(objTaxDtls);
                            }
                        }
                        else // For Blank Indicator
                        {
                            if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation
                            {
                                if (taxOnGD.equals("Gross"))
                                {
                                    taxableAmount = subTotal + funGetTaxableAmountForTaxOnTax(taxOnTaxCode, arrListTaxDtl);
                                }
                                else
                                {
                                    subTotal = 0;
                                    double discAmt = 0;
                                    for (clsPOSItemDetailFrTaxBean objItemDtl : arrListItemDtl)
                                    {
                                        if (objItemDtl.getDiscAmt() > 0)
                                        {
                                            discAmt += objItemDtl.getDiscAmt();
                                        }
                                        subTotal += objItemDtl.getAmount();
                                    }
                                    taxableAmount = subTotal - discAmt;
                                    taxableAmount += funGetTaxableAmountForTaxOnTax(taxOnTaxCode, arrListTaxDtl);
                                }

                                if (taxCal.equals("Forward")) // Forward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * (taxPercent / 100);
                                }
                                else // Backward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount - (taxableAmount * 100 / (100 + taxPercent));
                                }
                                objTaxDtls.setTaxCode(taxCode);
                                objTaxDtls.setTaxName(taxName);
                                objTaxDtls.setTaxableAmount(taxableAmount);
                                objTaxDtls.setTaxAmount(taxCalAmt);
                                objTaxDtls.setTaxCalculationType(taxCal);
                                arrListTaxDtl.add(objTaxDtls);
                            }
                            else
                            {
                                if (taxOnGD.equals("Gross"))
                                {
                                    taxableAmount = subTotal;
                                }
                                else
                                {
                                    subTotal = 0;
                                    double discAmt = 0;
                                    for (int cn = 0; cn < arrListItemDtl.size(); cn++)
                                    {
                                    	clsPOSItemDetailFrTaxBean objItemDtl = arrListItemDtl.get(cn);
                                        //System.out.println("Name= "+objItemDtl.getItemName()+"\tDisc Amt= "+objItemDtl.getDiscAmt());
                                        discAmt += objItemDtl.getDiscAmt();
                                        subTotal += objItemDtl.getAmount();
                                    }
                                    taxableAmount = subTotal - discAmt;
                                }

                                if (taxCal.equals("Forward")) // Forward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount * (taxPercent / 100);
                                }
                                else // Backward Tax Calculation
                                {
                                    taxCalAmt = taxableAmount - (taxableAmount * 100 / (100 + taxPercent));
                                }
                                objTaxDtls.setTaxCode(taxCode);
                                objTaxDtls.setTaxName(taxName);
                                objTaxDtls.setTaxableAmount(taxableAmount);
                                objTaxDtls.setTaxAmount(taxCalAmt);
                                objTaxDtls.setTaxCalculationType(taxCal);
                                arrListTaxDtl.add(objTaxDtls);
                            }
                        }
                    }
                }
            }
        }
    }

        return arrListTaxDtl;
    }
    
    
    
    private boolean funCheckAreaCode(String taxAreaCodes, String billAreaCode)
    {
        boolean flgTaxOn = false;
        String[] spAreaCode = taxAreaCodes.split(",");
        for (int cnt = 0; cnt < spAreaCode.length; cnt++)
        {
            if (spAreaCode[cnt].equals(billAreaCode))
            {
                flgTaxOn = true;
                break;
            }
        }

        return flgTaxOn;
    }
    
    
    private boolean funCheckOperationType(String taxOpTypes, String operationTypeForTax)
    {
        boolean flgTaxOn = false;
        String[] spOpType = taxOpTypes.split(",");
        for (int cnt = 0; cnt < spOpType.length; cnt++)
        {
            if (spOpType[cnt].equals("HomeDelivery") && operationTypeForTax.equalsIgnoreCase("HomeDelivery"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("HomeDelivery") && operationTypeForTax.equalsIgnoreCase("Home Delivery"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("DineIn") && operationTypeForTax.equalsIgnoreCase("DineIn"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("DineIn") && operationTypeForTax.equalsIgnoreCase("Dine In"))
            {
                flgTaxOn = true;
                break;
            }
            if (spOpType[cnt].equals("TakeAway") && operationTypeForTax.equalsIgnoreCase("TakeAway"))
            {
                flgTaxOn = true;
                break;
            }
        }
        return flgTaxOn;
    }
    
    
    private boolean funFindSettlementForTax(String taxCode, String settlementCode) throws Exception
    {
        boolean flgTaxSettlement = false;
        String sql_SettlementTax = "select strSettlementCode,strSettlementName "
                + "from tblsettlementtax where strTaxCode='" + taxCode + "' "
                + "and strApplicable='true' and strSettlementCode='" + settlementCode + "'";
      Query  querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_SettlementTax.toString());
        
        List listSql = querySql.list();
		   if(listSql.size()>0)
		    {    
		
            flgTaxSettlement = true;
        
		    }
      
        return flgTaxSettlement;
    }
    
    private double funGetTaxIndicatorTotal(String indicator, List<clsPOSItemDetailFrTaxBean> arrListItemDtl) throws Exception
    {
        String sql_Query = "";
        double indicatorAmount = 0.00;
        for (int cnt = 0; cnt < arrListItemDtl.size(); cnt++)
        {
        	clsPOSItemDetailFrTaxBean objItemDtl = arrListItemDtl.get(cnt);
            sql_Query = "select strTaxIndicator from tblitemmaster "
                    + "where strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
                    + "and strTaxIndicator='" + indicator + "'";
            Query  querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Query.toString());
            
            List listSql = querySql.list();
    		   if(listSql.size()>0)
    		    {    
    			   for(int i=0 ;i<listSql.size();i++ )
    		       	{
         
                indicatorAmount += objItemDtl.getAmount();
            }
    		}
          
        }
        return indicatorAmount;
    }
    
    
    private double funGetTaxIndicatorBasedDiscAmtTotal(String indicator, List<clsPOSItemDetailFrTaxBean> arrListItemDtl) throws Exception
    {
        String sql_Query = "";
        double discAmt = 0.00;
        for (int cnt = 0; cnt < arrListItemDtl.size(); cnt++)
        {
        	clsPOSItemDetailFrTaxBean objItemDtl = arrListItemDtl.get(cnt);
            sql_Query = "select strTaxIndicator from tblitemmaster "
                    + "where strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
                    + "and strTaxIndicator='" + indicator + "'";
  Query  querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Query.toString());
            
            List listSql = querySql.list();
    		   if(listSql.size()>0)
    		    {    
    			   for(int i=0 ;i<listSql.size();i++ )
    		       	{
          
                discAmt += objItemDtl.getDiscAmt();
            }
    	    }
        
        }
        return discAmt;
    }
    

    private double funGetTaxAmountForTaxOnTaxForIndicatorTax(String taxOnTaxCode, double indicatorTaxableAmt, List<clsTaxCalculationBean> listTaxDtl) throws Exception
    {
        double taxAmt = 0;
        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
        for (clsTaxCalculationBean objTaxCalDtl : listTaxDtl)
        {
            for (int t = 0; t < spTaxOnTaxCode.length; t++)
            {
                if (objTaxCalDtl.getTaxCode().equals(spTaxOnTaxCode[t]))
                {
                    taxAmt += funGetTaxOnTaxAmtForIndicatorTax(spTaxOnTaxCode[t], indicatorTaxableAmt);
                }
            }
        }

        return taxAmt;
    }
    
    private double funGetTaxOnTaxAmtForIndicatorTax(String taxCode, double taxableAmt) throws Exception
    {
        double taxAmt = 0;
        String sql = "select a.strTaxCode,a.strTaxType,a.dblPercent"
                + " ,a.dblAmount,a.strTaxOnGD,a.strTaxCalculation "
                + " from tbltaxhd a "
                + " where a.strTaxOnSP='Sales' and a.strTaxCode='" + taxCode + "'";
        Query  querySql = WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        
        List listSql = querySql.list();
		   if(listSql.size()>0)
		    {    
			   for(int i=0 ;i<listSql.size();i++ )
		       	{
				   Object[] obj = (Object[]) listSql.get(i);
            
				   double taxPercent = Double.parseDouble(obj[2].toString());
		            if (obj[5].toString().equals("Forward")) // Forward Tax Calculation
		            {
		                taxAmt = taxableAmt * (taxPercent / 100);
		            }
		            else // Backward Tax Calculation
		            {
		                taxAmt = taxableAmt * 100 / (100 + taxPercent);
		                taxAmt = taxableAmt - taxAmt;
		            }
		       	}
		    }

        return taxAmt;
    }
    
    
    private double funGetTaxableAmountForTaxOnTax(String taxOnTaxCode, List<clsTaxCalculationBean> arrListTaxCal) throws Exception
    {
        double taxAmt = 0;
        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
        for (int cnt = 0; cnt < arrListTaxCal.size(); cnt++)
        {
            for (int t = 0; t < spTaxOnTaxCode.length; t++)
            {
            	clsTaxCalculationBean objTaxDtls = arrListTaxCal.get(cnt);
                if (objTaxDtls.getTaxCode().equals(spTaxOnTaxCode[t]))
                {
                    taxAmt += objTaxDtls.getTaxAmount();
                }
            }
        }
        return taxAmt;
    }


    public int funUpdateBillDtlWithTaxValues(String billNo, String billType) throws Exception
    {
        Map<String, clsBillItemTaxDtl> hmBillItemTaxDtl = new HashMap<String, clsBillItemTaxDtl>();
        Map<String, clsBillItemTaxDtl> hmBillTaxDtl = new HashMap<String, clsBillItemTaxDtl>();

        Query sqlQuery;
	    List list;
        String billDtl = "tblbilldtl";
        String billTaxDtl = "tblbilltaxdtl";
        String billModifierDtl = "tblbillmodifierdtl";

        if (billType.equalsIgnoreCase("QFile"))
        {
            billDtl = "tblqbilldtl";
            billTaxDtl = "tblqbilltaxdtl";
            billModifierDtl = "tblqbillmodifierdtl";
        }

        String sql = "select a.strTaxCode,b.dblPercent,ifnull(b.strTaxIndicator,'NA'),b.strTaxCalculation,b.strTaxOnGD,b.strTaxOnTax "
                + " ,b.strTaxOnTaxCode,a.dblTaxAmount,a.dblTaxableAmount "
                + " from " + billTaxDtl + " a,tbltaxhd b "
                + " where a.strTaxCode=b.strTaxCode and a.strBillNo='" + billNo + "' and a.dblTaxAmount>0 "
                + " and a.dblTaxableAmount>0 "
                + " order by b.strTaxOnTax,b.strTaxCode; ";
        sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        list = sqlQuery.list();
        for(int k=0 ;k<list.size();k++ )
	    	{
        	Object[] objBillTaxDtl = (Object[]) list.get(k);	
     
            String taxCode = objBillTaxDtl[0].toString();
            String taxIndicator = objBillTaxDtl[2].toString();
            double taxPercentage = Double.parseDouble(objBillTaxDtl[1].toString());
            String taxCalculation = objBillTaxDtl[3].toString();
            String taxOnGD = objBillTaxDtl[4].toString();
            String taxOnTax = objBillTaxDtl[5].toString();
            String taxOnTaxCode = objBillTaxDtl[6].toString();
            double billTaxAmt = Double.parseDouble(objBillTaxDtl[7].toString());
            double billTaxableAmt = Double.parseDouble(objBillTaxDtl[8].toString());

            sql = "select a.strItemCode,a.dblAmount,ifnull(b.strTaxIndicator,'NA'),a.strKOTNo,a.dblDiscountAmt "
                    + " from " + billDtl + " a,tblitemmaster b "
                    + " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "'; ";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            for(int i=0 ;i<list.size();i++ )
    	    	{
            	Object[] objBillDtl = (Object[]) list.get(i);	
         
                String itemCode = objBillDtl[0].toString();
                double itemAmt = Double.parseDouble(objBillDtl[1].toString());
                double itemDiscAmt = Double.parseDouble(objBillDtl[4].toString());
                String KOTNo =objBillDtl[3].toString();
                double taxAmt = 0;
                if (taxOnGD.equalsIgnoreCase("Discount"))
                {
                    itemAmt -= itemDiscAmt;
                }

                sql = "select sum(dblAmount) "
                        + " from tblbillmodifierdtl "
                        + " where strBillNo='" + billNo + "' and left(strItemCode,7)='" + itemCode + "' "
                        + " group by left(strItemCode,7)";
                
                sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                list = sqlQuery.list();
                if (list.size()>0)
                {
                	Object objModifierAmt = (Object) list.get(0);	
                    itemAmt += Double.parseDouble(objModifierAmt.toString());
                }
            

                if (taxOnTax.equals("Yes"))
                {
                    String keyForTaxOnTax = itemCode + "," + KOTNo + "," + taxOnTaxCode;
                    if (hmBillTaxDtl.containsKey(keyForTaxOnTax))
                    {
                        clsBillItemTaxDtl objBillItemTaxDtl1 = hmBillTaxDtl.get(keyForTaxOnTax);
                        if (taxIndicator.isEmpty())
                        {
                            taxAmt = (billTaxAmt / billTaxableAmt) * (itemAmt + objBillItemTaxDtl1.getDblTaxAmt());
                        }
                        else
                        {
                            if (objBillDtl[2].toString().equals(taxIndicator))
                            {
                                taxAmt = (billTaxAmt / billTaxableAmt) * (itemAmt + objBillItemTaxDtl1.getDblTaxAmt());
                            }
                        }
                    }
                    else
                    {
                        if (taxIndicator.isEmpty())
                        {
                            taxAmt = (billTaxAmt / billTaxableAmt) * (itemAmt);
                        }
                        else
                        {
                            if (objBillDtl[2].toString().equals(taxIndicator))
                            {
                                taxAmt = (billTaxAmt / billTaxableAmt) * (itemAmt);
                            }
                        }
                    }

                    clsBillItemTaxDtl objItemTaxDtl = new clsBillItemTaxDtl();
                    objItemTaxDtl.setStrItemCode(itemCode);
                    objItemTaxDtl.setDblTaxAmt(taxAmt);
                    objItemTaxDtl.setStrKOTNo(KOTNo);
                    objItemTaxDtl.setStrBillNo(billNo);

                    String key2 = itemCode + "," + KOTNo + "," + taxCode;
                    String key1 = itemCode + "," + KOTNo;
                    hmBillTaxDtl.put(key2, objItemTaxDtl);

                    clsBillItemTaxDtl objBillItemTaxDtl = new clsBillItemTaxDtl();
                    objBillItemTaxDtl.setStrItemCode(itemCode);
                    objBillItemTaxDtl.setDblTaxAmt(taxAmt);
                    objBillItemTaxDtl.setStrKOTNo(KOTNo);
                    objBillItemTaxDtl.setStrBillNo(billNo);
                    if (hmBillItemTaxDtl.containsKey(key1))
                    {
                        objBillItemTaxDtl = hmBillItemTaxDtl.get(key1);
                        objBillItemTaxDtl.setDblTaxAmt(objBillItemTaxDtl.getDblTaxAmt() + taxAmt);
                    }
                    hmBillItemTaxDtl.put(key1, objBillItemTaxDtl);
                }
                else
                {
                    if (taxIndicator.isEmpty())
                    {
                        taxAmt = (billTaxAmt / billTaxableAmt) * itemAmt;
                    }
                    else
                    {
                        if (objBillDtl[2].toString().equals(taxIndicator))
                        {
                            taxAmt = (billTaxAmt / billTaxableAmt) * itemAmt;
                        }
                    }
                    clsBillItemTaxDtl objItemTaxDtl = new clsBillItemTaxDtl();
                    objItemTaxDtl.setStrItemCode(itemCode);
                    objItemTaxDtl.setDblTaxAmt(taxAmt);
                    objItemTaxDtl.setStrKOTNo(KOTNo);
                    objItemTaxDtl.setStrBillNo(billNo);

                    String key2 = itemCode + "," + KOTNo + "," + taxCode;
                    String key1 = itemCode + "," + KOTNo;
                    hmBillTaxDtl.put(key2, objItemTaxDtl);

                    clsBillItemTaxDtl objBillItemTaxDtl = new clsBillItemTaxDtl();
                    objBillItemTaxDtl.setStrItemCode(itemCode);
                    objBillItemTaxDtl.setDblTaxAmt(taxAmt);
                    objBillItemTaxDtl.setStrKOTNo(KOTNo);
                    objBillItemTaxDtl.setStrBillNo(billNo);

                    if (hmBillItemTaxDtl.containsKey(key1))
                    {
                        objBillItemTaxDtl = hmBillItemTaxDtl.get(key1);
                        objBillItemTaxDtl.setDblTaxAmt(objBillItemTaxDtl.getDblTaxAmt() + taxAmt);
                    }
                    hmBillItemTaxDtl.put(key1, objBillItemTaxDtl);
                }
            }
           
        }
      

        for (Map.Entry<String, clsBillItemTaxDtl> entry : hmBillItemTaxDtl.entrySet())
        {
            sql = "update " + billDtl + " set dblTaxAmount = " + entry.getValue().getDblTaxAmt() + " "
                    + " where strBillNo='" + billNo + "' and strItemCode='" + entry.getValue().getStrItemCode() + "' "
                    + " and strKOTNo='" + entry.getValue().getStrKOTNo() + "'";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();
            //System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue().getStrItemCode() + " " + entry.getValue().getDblTaxAmt());
        }
        return 1;
    }


    public JSONObject funCalculateDayEndCashForQFile(String posDate, int shiftCode, String POSCode)
    {
        double sales = 0.00, totalDiscount = 0.00, totalSales = 0.00, noOfDiscountedBills = 0.00;
        double advCash = 0.00, cashIn = 0.00, cashOut = 0.00;
        int noOfDiscountBills=0;
        Query sqlQuery;
        JSONObject jobj=new JSONObject();
	    List list;
        try
        {
            String sql = "SELECT c.strSettelmentDesc,sum(b.dblSettlementAmt),sum(a.dblDiscountAmt),c.strSettelmentType"
                    + " FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                    + "Where a.strBillNo = b.strBillNo and b.strSettlementCode = c.strSettelmentCode "
                    + " and date(a.dteBillDate ) ='" + posDate + "' and a.strPOSCode='" + POSCode + "'"
                    + " and a.intShiftCode=" + shiftCode
                    + " GROUP BY c.strSettelmentDesc,a.strPosCode";
            //System.out.println(sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            for(int k=0 ;k<list.size();k++ )
 	    	{
            	Object[] objSettlementAmt = (Object[]) list.get(k);	
         
                //records[1]=rsSettlementAmt.getString(2);
                if (objSettlementAmt[3].toString().equalsIgnoreCase("Cash"))
                {
                    sales = sales + (Double.parseDouble(objSettlementAmt[1].toString()));
                }
                totalDiscount = totalDiscount + (Double.parseDouble(objSettlementAmt[2].toString()));
                totalSales = totalSales + (Double.parseDouble(objSettlementAmt[1].toString()));
            }
         /*   gTotalDiscounts = totalDiscount;
            gTotalCashSales = totalSales;*/
            jobj.put("gTotalDiscounts", totalDiscount);
            jobj.put("gTotalCashSales_totalSales", totalSales);
     

            sql = "SELECT count(strBillNo),sum(dblDiscountAmt) FROM tblqbillhd "
                    + "Where date(dteBillDate ) ='" + posDate + "' and strPOSCode='" + POSCode + "' "
                    + "and dblDiscountAmt > 0.00 and intShiftCode=" + shiftCode
                    + " GROUP BY strPosCode";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            if (list.size()>0)
            {
            	
            	Object[] objTotalDiscountBills = (Object[]) list.get(0);	
            	noOfDiscountBills = Integer.parseInt(objTotalDiscountBills[0].toString());
            
            }
            jobj.put("gNoOfDiscountedBills", noOfDiscountBills);
        

            sql = "select count(strBillNo) from tblqbillhd where date(dteBillDate ) ='" + posDate + "' and "
                    + "strPOSCode='" + POSCode + " and intShiftCode=" + shiftCode + "' "
                    + "GROUP BY strPosCode";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            if (list.size()>0)
            {
            	int totalBill;
            	Object[] objTotalBills = (Object[]) list.get(0);	
                 totalBill=Integer.parseInt(objTotalBills[0].toString());
                jobj.put("gTotalBills", totalBill);
            }
         

           // gTotalCashSales =sales ;
            jobj.put("gTotalCashSales", sales);
            
            sql = "select count(dblAdvDeposite) from tbladvancereceipthd "
                    + "where dtReceiptDate='" + posDate + "' and intShiftCode=" + shiftCode;
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
          list= sqlQuery.list();
         
            int cntAdvDeposite = Integer.valueOf(list.get(0).toString());
            if (cntAdvDeposite > 0)
            {
                //sql="select sum(dblAdvDeposite) from tbladvancereceipthd where dtReceiptDate='"+posDate+"'";
                sql = "select sum(b.dblAdvDepositesettleAmt) "
                        + "from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
                        + "where date(a.dtReceiptDate)='" + posDate + "' and a.strPOSCode='" + POSCode + "' "
                        + "and c.strSettelmentCode=b.strSettlementCode and a.strReceiptNo=b.strReceiptNo "
                        + "and c.strSettelmentType='Cash' and a.intShiftCode=" + shiftCode;
                sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
                list = sqlQuery.list();
                Object[]  objTotalAdvance1 = (Object[]) list.get(0);
                advCash = Double.parseDouble(objTotalAdvance1[0].toString());
               // gTotalAdvanceAmt = advCash;
                jobj.put("gTotalAdvanceAmt", advCash);
            }
       

            //sql="select strTransType,sum(dblAmount) from tblcashmanagement where dteTransDate='"+posDate+"'"
            //    + " and strPOSCode='"+globalVarClass.gPOSCode+"' group by strTransType";
            sql = "select strTransType,sum(dblAmount),strCurrencyType from tblcashmanagement "
                    + "where dteTransDate='" + posDate + "' and strPOSCode='" + POSCode + "' "
                    + "and intShiftCode=" + shiftCode + " group by strTransType,strCurrencyType";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            for(int f=0 ;f<list.size();f++ )
 	    	{
            	Object[] objsqlCashTransaction = (Object[]) list.get(f);	

         
                if (objsqlCashTransaction[0].toString().equals("Float") || objsqlCashTransaction[0].toString().equals("Transfer In"))
                {
                    cashIn = cashIn + (Double.parseDouble(objsqlCashTransaction[1].toString()));
                }
                if (objsqlCashTransaction[0].toString().equals("Withdrawl") || objsqlCashTransaction[0].toString().equals("Transfer Out") || objsqlCashTransaction[0].toString().equals("Payments"))
                {
                    cashOut = cashOut + (Double.parseDouble(objsqlCashTransaction[1].toString()));
                }
            }
            cashIn = cashIn + advCash + sales;
          //  gTotalReceipt = cashIn;
          //  gTotalPayments = cashOut;
            double inHandCash = (cashIn) - cashOut;
           // gTotalCashInHand = inHandCash;
            
            jobj.put("gTotalReceipt", cashIn);
            jobj.put("gTotalPayments", cashOut);
            jobj.put("gTotalCashInHand", inHandCash);
       
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return jobj;
    }
    


    public int funUpdateDayEndFieldsForQFile(String posDate, int shiftNo, String dayEnd, String POSCode,String currentDate,String user,JSONObject jobj)
    {
        try
        {
        	 Query sqlQuery;
 		    List list;
 		    
        	
            String sql = "update tbldayendprocess set dblTotalSale = IFNULL((select sum(b.dblSettlementAmt) "
                    + "TotalSale from tblqbillhd a,tblqbillsettlementdtl b "
                    + "where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '" + posDate + "' and "
                    + "a.strPOSCode = '" + POSCode + "' and a.intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode = '" + POSCode + "'"
                    + " and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_1=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();
            sql = "update tbldayendprocess set dteDayEndDateTime='" + currentDate + "'"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_2=="+sql);

            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();
            sql = "update tbldayendprocess set strUserEdited='" + user + "'"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_3=="+sql);

            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblNoOfBill = IFNULL((select count(*) NoOfBills "
                    + "from tblqbillhd where Date(dteBillDate) = '" + posDate + "' and "
                    + "strPOSCode = '" +POSCode + "' and intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_4=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblNoOfVoidedBill = IFNULL((select count(DISTINCT strBillNo) "
                    + "NoOfVoidBills from tblvoidbillhd where date(dteModifyVoidBill) = " + "'" + posDate + "'"
                    + " and strPOSCode = '" + POSCode + "' and strTransType = 'VB'"
                    + " and intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_5=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblNoOfModifyBill = IFNULL((select count(DISTINCT b.strBillNo) "
                    + "NoOfModifiedBills from tblqbillhd a,tblvoidbillhd b where a.strBillNo=b.strBillNo"
                    + " and Date(b.dteModifyVoidBill) = '" + posDate + "' and b.strPOSCode='" + POSCode + "'"
                    + " and b.strTransType = 'MB' and a.intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_6=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblHDAmt=IFNULL((select sum(a.dblGrandTotal) HD from tblqbillhd a,"
                    + "tblhomedelivery b where a.strBillNo=b.strBillNo and date(a.dteBillDate) = '" + posDate + "' and "
                    + "a.strPOSCode = '" + POSCode + "' and a.intShiftCode=" + shiftNo + "), 0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" + POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_7=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblDiningAmt=IFNULL(( select sum(dblGrandTotal) Dining"
                    + " from tblqbillhd where strTakeAway='No' and date(dteBillDate) = '" + posDate + "' and strPOSCode = '" +POSCode + "'"
                    + "  and strBillNo NOT IN (select strBillNo from tblhomedelivery where strBillNo is not NULL) and intShiftCode=" + shiftNo + "),0)"
                    + "  where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();
            //System.out.println("UpdateDayEndQuery_8=="+sql);

            sql = "update tbldayendprocess set dblTakeAway=IFNULL((select sum(dblGrandTotal) TakeAway from tblqbillhd"
                    + " where strTakeAway='Yes' and date(dteBillDate) = '" + posDate + "' and strPOSCode = '" +POSCode + "'"
                    + " and intShiftCode=" + shiftNo + "),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;

            //System.out.println("UpdateDayEndQuery_9=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblFloat=IFNULL((select sum(dblAmount) TotalFloats from tblcashmanagement "
                    + "where strTransType='Float' and date(dteTransDate) = '" + posDate + "' and strPOSCode = '" +POSCode + "'"
                    + " and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_10=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblTransferIn=IFNULL((select sum(dblAmount) TotalTransferIn from tblcashmanagement "
                    + "where strTransType='Transfer In' and dteTransDate = '" + posDate + "'"
                    + " and strPOSCode = '" +POSCode + "' and intShiftCode=" + shiftNo
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_11=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblTransferOut=IFNULL((select sum(dblAmount) TotalTransferOut from tblcashmanagement "
                    + "where strTransType='Transfer Out' and date(dteTransDate) = '" + posDate + "'"
                    + " and strPOSCode = '" +POSCode + "' and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_12=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblWithdrawal=IFNULL(( select sum(dblAmount) TotalWithdrawals from tblcashmanagement "
                    + "where strTransType='Withdrawal' and date(dteTransDate) = '" + posDate + "' "
                    + "and strPOSCode = '" +POSCode + "' and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_13=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblRefund=IFNULL(( select sum(dblAmount) TotalRefunds from tblcashmanagement "
                    + " where strTransType='Refund' and date(dteTransDate) = '" + posDate + "' and strPOSCode = '" +POSCode + "'"
                    + " and intShiftCode=" + shiftNo + " group by strTransType),0)"
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_14=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblPayments=IFNULL(( select sum(dblAmount) TotalPayments from tblcashmanagement "
                    + "where strTransType='Payments' and date(dteTransDate) = '" + posDate + "'"
                    + " and strPOSCode = '" +POSCode + "' and intShiftCode=" + shiftNo + ""
                    + " group by strTransType),0) "
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_15=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblAdvance=IFNULL((select sum(b.dblAdvDepositesettleAmt) "
                    + "from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c "
                    + "where date(a.dtReceiptDate)='" + posDate + "' and a.strPOSCode='" +POSCode + "' "
                    + "and c.strSettelmentCode=b.strSettlementCode and a.strReceiptNo=b.strReceiptNo "
                    + "and c.strSettelmentType='Cash' and intShiftCode=" + shiftNo + "),0)"
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_16=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

           // gTotalReceipt  gTotalPayments  gTotalCashInHand  gTotalCashSales  gTotalDiscounts gNoOfDiscountedBills  
           
            sql = "update tbldayendprocess set dblTotalReceipt=" + jobj.getDouble("gTotalReceipt") 
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_17=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblTotalPay=" + jobj.getDouble("gTotalPayments") 
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_18=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblCashInHand=" + jobj.getDouble("gTotalCashInHand") 
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_19=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblCash=" + jobj.getDouble("gTotalCashSales") 
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println(sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblTotalDiscount=" +jobj.getDouble("gTotalDiscounts")  
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_21=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set dblNoOfDiscountedBill=" + jobj.getDouble("gNoOfDiscountedBills") 
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_22=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set intTotalPax=IFNULL((select sum(intPaxNo)"
                    + " from tblqbillhd where date(dteBillDate ) ='" + posDate + "' and intShiftCode=" + shiftNo + ""
                    + " and strPOSCode='" +POSCode + "'),0)"
                    + " where date(dtePOSDate)='" + posDate + "' "
                    + "and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("UpdateDayEndQuery_23=="+sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set intNoOfTakeAway=(select count(strTakeAway)"
                    + "from tblqbillhd where date(dteBillDate )='" + posDate + "' and intShiftCode=" + shiftNo + ""
                    + " and strPOSCode='" +POSCode + "' and strTakeAway='Yes')"
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("update int takeawy==" + sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();
            sql = "update tbldayendprocess set intNoOfHomeDelivery=(select COUNT(strBillNo)from tblhomedelivery where date(dteDate)='" + posDate + "' and strPOSCode='" +POSCode + "' )"
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
            //System.out.println("update int homedelivry:==" + sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            // Update Day End Table with Used Card Balance    
            double debitCardAmtUsed = 0;
            sql = "select sum(b.dblSettlementAmt) "
                    + " from tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c "
                    + " where a.strBillNo=b.strBillNo and b.strSettlementCode=c.strSettelmentCode "
                    + " and date(dteBillDate)='" + posDate + "' and a.strPOSCode='" +POSCode + "' "
                    + " and c.strSettelmentType='Debit Card' "
                    + " group by a.strPOSCode,date(a.dteBillDate),c.strSettelmentType;";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            if (list.size()>0)
            {
            	Object[] objsql = (Object[]) list.get(0);	
                debitCardAmtUsed = Double.parseDouble(objsql[0].toString());
            }
            
            sql = "update tbldayendprocess set dblUsedDebitCardBalance=" + debitCardAmtUsed + " "
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' "
                    + " and intShiftCode=" + shiftNo;
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            // Update Day End Table with UnUsed Card Balance    
            double debitCardAmtUnUsed = 0;
            sql = "select sum(dblCardAmt) from tbldebitcardrevenue "
                    + " where strPOSCode='" +POSCode + "' and date(dtePOSDate)='" + posDate + "' "
                    + " group by strPOSCode,date(dtePOSDate);";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            if (list.size()>0)
            {
            	Object[] objsql = (Object[]) list.get(0);	
                debitCardAmtUnUsed = Double.parseDouble(objsql[0].toString());
            }
           
            sql = "update tbldayendprocess set dblUnusedDebitCardBalance=" + debitCardAmtUnUsed + " "
                    + " where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' "
                    + " and intShiftCode=" + shiftNo;
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "UPDATE tbldayendprocess SET dblTipAmt= IFNULL(( "
                    + "SELECT SUM(dblTipAmount) "
                    + "FROM tblbillhd "
                    + "WHERE DATE(dteBillDate) ='" + posDate + "' AND intShiftCode='" + shiftNo + "' AND strPOSCode='" +POSCode + "'),0) "
                    + "WHERE DATE(dtePOSDate)='" + posDate + "' AND strPOSCode='" +POSCode + "' AND intShiftCode='" + shiftNo + "' ";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();

            sql = "update tbldayendprocess set intNoOfComplimentaryKOT=(select COUNT(a.strBillNo)"
                    + "from  tblqbillhd a,tblqbillcomplementrydtl b "
                    + "where a.strBillNo=b.strBillNo "
                    + "and date(b.dteBillDate)='" + posDate + "' and a.strPOSCode='" +POSCode + "') "
                    + "where date(dtePOSDate)='" + posDate + "' and strPOSCode='" +POSCode + "' and intShiftCode=" + shiftNo;
//            System.out.println("intNoOfComplimentaryKOT:==" + sql);
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            sqlQuery.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 1;
    }
    

    public double funGetKOTAmtOnTable(String cardNo) throws Exception
    {
        double KOTAmt = 0;
        Query sqlQuery;
        List list;
        String tableNo = "";
        String sql = "select sum(dblAmount),strTableNo "
                + " from tblitemrtemp "
                + " where strCardNo='" + cardNo + "' and strNCKotYN='N' "
                + " group by strTableNo;";
        sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
           list = sqlQuery.list();
        if (list.size()>0)
        {
        	Object[] obj = (Object[]) list.get(0);	
            KOTAmt +=Double.parseDouble(obj[0].toString());
            tableNo = obj[1].toString();
        }
    
        if (!cardNo.isEmpty())
        {
            sql = "select sum(dblTaxAmt) "
                    + " from tblkottaxdtl "
                    + " where strTableNo='" + tableNo + "' "
                    + " group by strTableNo;";
            sqlQuery= WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            list = sqlQuery.list();
            if (list.size()>0)
            {
            	Object[] obj = (Object[]) list.get(0);
                KOTAmt += Double.parseDouble(obj[0].toString());
            }       
        }
        return KOTAmt;
    }
    
    
    /* //frmMainMenu()  get data
     *  get pos all data and Shift data 
     * */
   
    
    	
    	
    	@GET
        @Path("/funGetPOSWiseDayEndData")
        @Produces(MediaType.APPLICATION_JSON)
        public String funGetPOSWiseDayEndData(@QueryParam("POSCode") String POSCode,@QueryParam("UserCode") String UserCode)
    	{
    		
    		return obUtilityService.funGetPOSWiseDayEndData(POSCode, UserCode);
    	}
        

    	public boolean funCheckPendingBills(String posCode,String POSDate)
        {
            return obUtilityService.funCheckPendingBills(posCode, POSDate);
        }

    	public boolean funCheckTableBusy(String posCode)
        {
    		return obUtilityService.funCheckTableBusy(posCode);
        }
    	
    	public String funGetDBBackUpPath(String clientCode)
    	{
    		return obUtilityService.funGetDBBackUpPath(clientCode);
    	}
    	
    	//for day end
    	
        public int funGetNextShiftNo(String posCode, int shiftNo,String strClientCode,String strUserCode)
        {
        	return obUtilityService.funGetNextShiftNo(posCode, shiftNo, strClientCode, strUserCode);
        }
    	
    	
    	
    	//for shift end
        public int funGetNextShiftNoForShiftEnd(String posCode, int shiftNo,String strClientCode,String strUserCode)
        {
        	return obUtilityService.funGetNextShiftNoForShiftEnd(posCode, shiftNo, strClientCode, strUserCode);
        }
        
        public int funShiftEndProcess(String status, String posCode, int shiftNo, String billDate,String strClientCode,String strUserCode)
        {
        	return obUtilityService.funShiftEndProcess(status, posCode, shiftNo, billDate, strClientCode, strUserCode);
        }
        
        public int funDayEndflash(String clientCode,String posCode, String billDate, int shiftNo,String strUserCode)
        {
        	return obUtilityService.funDayEndflash(clientCode, posCode, billDate, shiftNo, strUserCode);
        }

       
        public int funShiftCardBalToRevenueTable(String posCode, String posDate,String strClientCode,String strUserCode) throws Exception
        {
        	return obUtilityService.funShiftCardBalToRevenueTable(posCode, posDate, strClientCode, strUserCode);
        }

     // Function to send bill data to sanguine cms.
        public int funPostSanguineCMSData(String posCode, String billDate,String ClientCode,String userCode)
        {
        	return obUtilityService.funPostSanguineCMSData(posCode, billDate, ClientCode, userCode);
        }
        
       
     // Function to send bill data to others cms.
        public int funPostBillDataToCMS(String posCode, String billDate,String ClientCode,String userCode) throws Exception
        {
        	return obUtilityService.funPostBillDataToCMS(posCode, billDate, ClientCode, userCode);
        }

        
        public String funGenerateNextCode()
        {
        	return obUtilityService.funGenerateNextCode();
    	}
        
        // Function to calculate total settlement amount and assigns global variables, which are shown on day end/shift end form.
    // This function calculate settlement amount from live tables.    
        public int funCalculateDayEndCash(String posDate, int shiftCode, String posCode)
        {
        	return obUtilityService.funCalculateDayEndCash(posDate, shiftCode, posCode);
        }
        
     // Function to update values in tbldayendprocess table.
     // This function updates values from Live tables.    
         public int funUpdateDayEndFields(String posDate, int shiftNo, String dayEnd, String posCode,String userCode)
         {
        	 return obUtilityService.funUpdateDayEndFields(posDate, shiftNo, dayEnd, posCode, userCode);
         }

     
         public void funGenerateLinkupTextfile(ArrayList<ArrayList<String>> arrUnLinkedItemDtl, String fromDate, String toDate, String posName,String gClientName)
         {
    	
    	    try
    	    {
    	        funCreateTempFolder();
    	        String filePath = System.getProperty("user.dir");
    	        filePath += "/Temp/Temp_ItemUnLinkedItems.txt";
    	        File textFile = new File(filePath);
    	        PrintWriter pw = new PrintWriter(textFile);
    	        pw.println(funPrintTextWithAlignment(" UnLinked Items ", 40, "Center"));
    	        pw.println(funPrintTextWithAlignment(gClientName, 40, "Center"));
    	        pw.println(funPrintTextWithAlignment(posName, 40, "Center"));
    	        pw.println(" ");
    	        pw.print(funPrintTextWithAlignment("FromDate:", 10, "Left"));
    	        pw.print(funPrintTextWithAlignment(fromDate, 10, "Left"));
    	        pw.print(funPrintTextWithAlignment("", 2, "Left"));
    	        pw.print(funPrintTextWithAlignment("ToDate:", 8, "Left"));
    	        pw.print(funPrintTextWithAlignment(toDate, 10, "Left"));
    	        pw.println(" ");
    	        pw.println("________________________________________");
    	        pw.print(funPrintTextWithAlignment("ItemCode ", 15, "Left"));
    	        pw.print(funPrintTextWithAlignment("ItemName", 25, "Left"));
    	        pw.println(" ");
    	        pw.println("________________________________________");
    	        pw.println(" ");
    	
    	        if (arrUnLinkedItemDtl.size() > 0)
    	        {
    	            for (int cnt = 0; cnt < arrUnLinkedItemDtl.size(); cnt++)
    	            {
    	                ArrayList<String> items = arrUnLinkedItemDtl.get(cnt);
    	                pw.print(funPrintTextWithAlignment("" + items.get(0) + " ", 15, "Left"));
    	                pw.print(funPrintTextWithAlignment("" + items.get(1), 25, "Left"));
    	                pw.println(" ");
    	            }
    	        }
    	
    	        pw.println(" ");
    	        pw.println(" ");
    	        pw.println(" ");
    	        pw.println(" ");
    	        pw.println("m");
    	
    	        pw.flush();
    	        pw.close();
    	
    	       /* clsTextFileGeneratorForPrinting ob = new clsTextFileGeneratorForPrinting();
    	        if (clsGlobalVarClass.gShowBill)
    	        {
    	            ob.funShowTextFile(textFile, "", "");
    	        }*/
    	    }
    	    catch (Exception e)
    	    {
    	        e.printStackTrace();
    	    }
         }
       
         public String funPrintTextWithAlignment(String text, int totalLength, String alignment)
         {
    	    StringBuilder sbText = new StringBuilder();
    	    if (alignment.equalsIgnoreCase("Center"))
    	    {
    	        int textLength = text.length();
    	        int totalSpace = (totalLength - textLength) / 2;
    	
    	        for (int i = 0; i < totalSpace; i++)
    	        {
    	            sbText.append(" ");
    	        }
    	        sbText.append(text);
    	    }
    	    else if (alignment.equalsIgnoreCase("Left"))
    	    {
    	        sbText.setLength(0);
    	        int textLength = text.length();
    	        int totalSpace = (totalLength - textLength);
    	        sbText.append(text);
    	        for (int i = 0; i < totalSpace; i++)
    	        {
    	            sbText.append(" ");
    	        }
    	    }
    	    else
    	    {
    	        sbText.setLength(0);
    	        int textLength = text.length();
    	        int totalSpace = (totalLength - textLength);
    	        for (int i = 0; i < totalSpace; i++)
    	        {
    	            sbText.append(" ");
    	        }
    	        sbText.append(text);
    	    }
    	
    	    return sbText.toString();
         }

         public boolean funInsertQBillData(String posCode,String clientCode)
    	 {
        	 return obUtilityService.funInsertQBillData(posCode, clientCode);
    	 }

    	public void funInvokeHOWebserviceForTrans(String transType, String formName,String clientCode,String POSCode)
    	{
    		obUtilityService.funInvokeHOWebserviceForTrans(transType, formName, clientCode, POSCode);
    	}
    	
    	public void funPostCustomerDataToHOPOS(String clientCode,String POSCode)
    	{
    		obUtilityService.funPostCustomerDataToHOPOS(clientCode, POSCode);
    	}

    	
    	public void funPostDayEndData(String newStartDate,int shiftCode,String strClientCode,String posCode)
    	  {
    		obUtilityService.funPostDayEndData(newStartDate, shiftCode, strClientCode, posCode);
    	  }
    	
    	 public int funBackupAndMailDB(String backupFilePath,String strClientCode,String posCode,String strPOSName,String strPOSDate) throws Exception
    	    {
    	        String filePath = System.getProperty("user.dir") + "\\DBBackup\\" + backupFilePath + ".sql";
    	        //String filePath = System.getProperty("user.dir")+"/DBBackup/1.sql";
    	        File file = new File(filePath);
    	        double bytes = file.length();
    	        double kilobytes = (bytes / 1024);
    	        double megabytes = (kilobytes / 1024);

    	        if (megabytes < 25)
    	        {
    	        	try{
    	        		obSendMail.funSendMail("sanguineapos@gmail.com", filePath,strClientCode,posCode,strPOSName,strPOSDate);	
    	        	}catch(Exception e){
    	        		e.printStackTrace();
    	        	}
    	            
    	        }
    	        return 1;
    	    }
    	
    	
    	public void funCreateTempFolder()
    	{
    	    try
    	    {
    	        String filePath = System.getProperty("user.dir");
    	        File file = new File(filePath + "/Temp");
    	        if (!file.exists())
    	        {
    	            file.mkdirs();
    	        }
    	    }
    	    catch (Exception e)
    	    {
    	        e.printStackTrace();
    	    }
    	}
    	public String getCurrentDateTime()
        {
            Date currentDate = new Date();
            String strCurrentDate = ((currentDate.getYear() + 1900) + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate())
                + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
            return strCurrentDate;
        }

    	public String funGetConnectionStatus(String clientCode,String posCode)
    	{
    		String flgHOStatus = "N";
    	    String gConnectionActive = "N";
    		try
    	    {
    	   
    	    JSONObject jsHOPOSType = objSetupDao.funGetParameterValuePOSWise(clientCode,posCode, "gHOPOSType");
    		String gHOPOSType=jsHOPOSType.get("gHOPOSType").toString();
    	
    	    if (gHOPOSType.equals("Stand Alone") || gHOPOSType.equals("HOPOS"))
    	    {
    	        return "N";
    	    }
    	    
    	    	String gHOCommunication="";
    	    	String sql="select strHOCommunication from tblconfig where strClientCode='"+clientCode+"'";
    	    	Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    	    	List list=query.list();
    	    	if(list.size()>0){
    	    		gHOCommunication=(String) list.get(0);	 
    	    	}
    	    	
    	        if(gHOCommunication.equals("Y"))
    	        {
    	        	  JSONObject jsonSanguineWebServiceURL = objSetupDao.funGetParameterValuePOSWise(clientCode,posCode, "gSanguineWebServiceURL");
    	     		 String gSanguineWebServiceURL=jsonSanguineWebServiceURL.get("gSanguineWebServiceURL").toString();
    	     		
    	            String hoURL = gSanguineWebServiceURL + "/POSIntegration/funInvokeHOWebService";
    	            URL url = new URL(hoURL);
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	            conn.setRequestMethod("GET");
    	            conn.setRequestProperty("Accept", "application/json");
    	            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
    	            String output = "", op = "";
    	            while ((output = br.readLine()) != null)
    	            {
    	                op += output;
    	            }
    	            System.out.println("HO Conn=" + op);
    	            conn.disconnect();
    	
    	            flgHOStatus = op;
    	            if (flgHOStatus.equalsIgnoreCase("true"))
    	            {
    	                gConnectionActive = "Y";
    	            }
    	        }
    	    }
    	    catch (Exception e)
    	    {
    	        flgHOStatus = "N";
    	        gConnectionActive = "N";
    	        e.printStackTrace();
    	    }
    	    finally
    	    {
    	        return gConnectionActive;
    	    }
    	}
    	
    	public Boolean funCheckSuperUser(String userCode)
    	{
    		Boolean user=false;
    		String sql="select strSuperType from tbluserhd where strUserCode='"+userCode+"';";
    		Query query=WebPOSSessionFactory.getCurrentSession().createSQLQuery(sql);
    		List list=query.list();
			  if (list.size()>0)
	          {
	          	String strUserType = (String) list.get(0);
	          	if(strUserType.equalsIgnoreCase("SUPER")){
	          		user=true;
	          	}
	          	
	          }else if(userCode.equalsIgnoreCase("Sanguine")){
	        	  user=true;
	          }
	          else{
	        	  user=false;
	          }
			  return user;
    	}

}
