package com.webservice.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.webservice.controller.clsDatabaseConnection;

public class clsTaxCalculation 
{
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null;
	
	
	
	public List funCalculateTax(List<clsItemDtlForTax> arrListItemDtl,String POSCode
	        ,String dtPOSDate, String billAreaCode, String operationTypeForTax
	        , double subTotal, double discountPer, String transType) throws Exception
	    {
	        return funCheckDateRangeForTax(arrListItemDtl, POSCode, dtPOSDate, billAreaCode, operationTypeForTax, subTotal, discountPer,transType,"S01","Sales");
	    }
	
	
	 private List funCheckDateRangeForTax(List<clsItemDtlForTax> arrListItemDtl, String POSCode, String dtPOSDate, String billAreaCode, String operationTypeForTax, double subTotal, double discountAmt, String transType, String settlementCode, String taxOnSP) throws Exception
	    {
		 
		    cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	    	Statement st1 = cmsCon.createStatement();
		 
		 
	        List<clsTaxCalculationDtls> arrListTaxDtl = new ArrayList<clsTaxCalculationDtls>();
	        String taxCode = "", taxName = "", taxOnGD = "", taxCal = "", taxIndicator = "", taxType = "Percent";
	        String opType = "", taxAreaCodes = "", taxOnTax = "No", taxOnTaxCode = "";
	        double taxPercent = 0.00, taxFixedAmount = 0.00, taxableAmount = 0.00, taxCalAmt = 0.00;
	       

	        StringBuilder sbSql = new StringBuilder();
	        sbSql.setLength(0);
	        sbSql.append("select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent"
	                + ",a.dblAmount,a.strTaxOnGD,a.strTaxCalculation,a.strTaxIndicator,a.strAreaCode,a.strOperationType"
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
	        sbSql.append(" and a.strTaxOnSP='" + taxOnSP + "' "
	                + "order by a.strTaxOnTax,a.strTaxDesc");

	        ResultSet rsTax= st.executeQuery(sbSql.toString());
	        while (rsTax.next())
	        {
	            taxCode = rsTax.getString(1);
	            taxName = rsTax.getString(2);
	            taxOnGD = rsTax.getString(7);
	            taxCal = rsTax.getString(8);
	            taxIndicator = rsTax.getString(9);
	            taxOnTax = rsTax.getString(13);
	            taxOnTaxCode = rsTax.getString(14);
	            taxType = rsTax.getString(4);//taxType
	            taxPercent = Double.parseDouble(rsTax.getString(5));//percent
	            taxFixedAmount = Double.parseDouble(rsTax.getString(6));//fixes amount

	            taxableAmount = 0.00;
	            taxCalAmt = 0.00;

	            boolean flgTax = false;

	            if (taxOnSP.equals("Sales"))
	            {
	                String sqlTaxOn = "select strAreaCode,strOperationType,strItemType "
	                        + "from tbltaxhd where strTaxCode='" + taxCode + "'";
	                ResultSet rsTaxOn= st1.executeQuery(sqlTaxOn);
	                if (rsTaxOn.next())
	                {
	                    taxAreaCodes = rsTaxOn.getString(1);
	                    opType = rsTaxOn.getString(2);
	                }

	                if (funCheckAreaCode(taxAreaCodes, billAreaCode))
	                {
	                    if (funCheckOperationType(opType, operationTypeForTax))
	                    {
	                        if (funFindSettlementForTax(taxCode, settlementCode))
	                        {
	                            flgTax = true;
	                        }
	                    }
	                }
	            }
	            else // Tax on Purchase
	            {
	                flgTax = true;
	            }

	            if (flgTax)
	            {
	                boolean flgTaxOnGrpApplicable = false;
	                taxableAmount = 0;
	                clsTaxCalculationDtls objTaxDtls = new clsTaxCalculationDtls();

	                if (taxOnGD.equals("Gross"))
	                {
	                    //to calculate tax on group of an item
	                    for (int i = 0; i < arrListItemDtl.size(); i++)
	                    {
	                        clsItemDtlForTax objItemDtl = arrListItemDtl.get(i);

	                        boolean isApplicable = isTaxApplicableOnItemGroup(taxCode, objItemDtl.getItemCode().substring(0, 7));
	                        if (isApplicable)
	                        {
	                            flgTaxOnGrpApplicable = true;
	                            taxableAmount = taxableAmount + objItemDtl.getAmount();

	                            if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation new logic only for same group item
	                            {
	                                taxableAmount = taxableAmount + funGetTaxableAmountForTaxOnTax(taxOnTaxCode, objItemDtl.getAmount(), arrListTaxDtl);
	                            }
	                        }
	                    }
	                }
	                else
	                {
	                    subTotal = 0;
	                    double discAmt = 0;
	                    for (clsItemDtlForTax objItemDtl : arrListItemDtl)
	                    {
	                        boolean isApplicable = isTaxApplicableOnItemGroup(taxCode, objItemDtl.getItemCode().substring(0, 7));
	                        if (isApplicable)
	                        {
	                            flgTaxOnGrpApplicable = true;
	                            if (objItemDtl.getDiscAmt() > 0)
	                            {
	                                discAmt += objItemDtl.getDiscAmt();
	                            }
	                            taxableAmount = taxableAmount + objItemDtl.getAmount();

	                            if (taxOnTax.equalsIgnoreCase("Yes")) // For tax On Tax Calculation new logic only for same group item
	                            {
	                                taxableAmount = taxableAmount + funGetTaxableAmountForTaxOnTax(taxOnTaxCode, objItemDtl.getAmount() - objItemDtl.getDiscAmt(), arrListTaxDtl);
	                            }
	                        }
	                    }
	                    if (taxableAmount > 0)
	                    {
	                        taxableAmount = taxableAmount - discAmt;
	                    }
	                }

	                if (flgTaxOnGrpApplicable)
	                {
	                    if (taxCal.equals("Forward")) // Forward Tax Calculation
	                    {
	                        if (taxType.equalsIgnoreCase("Percent"))
	                        {
	                            taxCalAmt = taxableAmount * (taxPercent / 100);
	                        }
	                        else
	                        {
	                            taxCalAmt = taxFixedAmount;
	                        }
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

	        return arrListTaxDtl;
	    }

	
	
	   private boolean isTaxApplicableOnItemGroup(String taxCode, clsItemDtlForTax objItemDtl)throws Exception
	    {
		   cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	       st = cmsCon.createStatement();
		   
	        boolean isApplicable = false;
	        try
	        {
	            String sql = "select a.strItemCode,a.strItemName,b.strSubGroupCode,b.strSubGroupName,c.strGroupCode,c.strGroupName,d.strTaxCode,d.strApplicable "
	                    + "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c,tbltaxongroup d "
	                    + "where a.strSubGroupCode=b.strSubGroupCode "
	                    + "and b.strGroupCode=c.strGroupCode "
	                    + "and c.strGroupCode=d.strGroupCode "
	                    + "and a.strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
	                    + "and d.strTaxCode='" + taxCode + "' "
	                    + "and d.strApplicable='true' ";
	            ResultSet rsTaxApplicable= st.executeQuery(sql);
	            if (rsTaxApplicable.next())
	            {
	                isApplicable = true;
	            }
	            rsTaxApplicable.close();
	           st.close();
	           cmsCon.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            return isApplicable;
	        }
	    }
	   
	   
	   public boolean isTaxApplicableOnItemGroup(String taxCode, String itemCode)throws Exception
	    {
		   cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	       st = cmsCon.createStatement();
		   
	        boolean isApplicable = false;
	        try
	        {
	            String sql = "select a.strItemCode,a.strItemName,b.strSubGroupCode,b.strSubGroupName,c.strGroupCode,c.strGroupName,d.strTaxCode,d.strApplicable "
	                    + "from tblitemmaster a,tblsubgrouphd b,tblgrouphd c,tbltaxongroup d "
	                    + "where a.strSubGroupCode=b.strSubGroupCode "
	                    + "and b.strGroupCode=c.strGroupCode "
	                    + "and c.strGroupCode=d.strGroupCode "
	                    + "and a.strItemCode='" + itemCode + "' "
	                    + "and d.strTaxCode='" + taxCode + "' "
	                    + "and d.strApplicable='true' ";
	            ResultSet rsTaxApplicable= st.executeQuery(sql);
	            if (rsTaxApplicable.next())
	            {
	                isApplicable = true;
	            }
	            rsTaxApplicable.close();
	            st.close();
		        cmsCon.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            return isApplicable;
	        }
	    }
	   
	   
	   
	   private boolean funCheckAreaCode(String taxAreaCodes, String billAreaCode)
	    {
	        boolean flgTaxOn=false;
	        String[] spAreaCode=taxAreaCodes.split(",");
	        for(int cnt=0;cnt<spAreaCode.length;cnt++)
	        {
	            if(spAreaCode[cnt].equals(billAreaCode))
	            {
	            	 System.out.println("taxAreaCodes :" +spAreaCode[cnt]);
	                flgTaxOn=true;
	                break;
	            }
	        }
	     
	        System.out.println("billAreaCode :" +billAreaCode);
	        System.out.println("flgTaxOn of CheckArea is :" +flgTaxOn);
	        
	        return flgTaxOn;
	    }
	   
	   
	 
	   
	   private boolean funFindSettlementForTax(String taxCode,String settlementCode)throws Exception
	    {
	    	cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	        boolean flgTaxSettlement=false;
	       String sql_SettlementTax = "select strSettlementCode,strSettlementName "
	                + "from tblsettlementtax where strTaxCode='" + taxCode + "' "
	                + "and strApplicable='true' and strSettlementCode='" + settlementCode + "'";
	        Statement st2 = cmsCon.createStatement();
	        System.out.println(sql_SettlementTax);
	        ResultSet rsTaxSettlement=st2.executeQuery(sql_SettlementTax);
	        if(rsTaxSettlement.next())
	        {
	            flgTaxSettlement=true;
	        }
	        rsTaxSettlement.close();
	        st.close();
           cmsCon.close();
           System.out.println("flgTaxSettlement is :" +flgTaxSettlement);
	        return flgTaxSettlement;
	    }
	   
	   
	   private boolean funCheckOperationType(String taxOpTypes, String operationTypeForTax)
	    {
	        boolean flgTaxOn=false;
	        String[] spOpType=taxOpTypes.split(",");
	        
	        System.out.println("Operation Type:"+operationTypeForTax);
	        for(int cnt=0;cnt<spOpType.length;cnt++)
	        {
	            if(spOpType[cnt].equals("HomeDelivery") && operationTypeForTax.equalsIgnoreCase("HomeDelivery"))
	            {
	                flgTaxOn=true;
	                break;
	            }
	            if(spOpType[cnt].equals("HomeDelivery") && operationTypeForTax.equalsIgnoreCase("Home Delivery"))
	            {
	                flgTaxOn=true;
	                break;
	            }
	            if(spOpType[cnt].equals("DineIn") && operationTypeForTax.equalsIgnoreCase("DineIn"))
	            {
	                flgTaxOn=true;
	                break;
	            }
	            if(spOpType[cnt].equals("DineIn") && operationTypeForTax.equalsIgnoreCase("Dine In"))
	            {
	                flgTaxOn=true;
	                break;
	            }
	            if(spOpType[cnt].equals("TakeAway") && operationTypeForTax.equalsIgnoreCase("TakeAway"))
	            {
	                flgTaxOn=true;
	                break;
	            }
	        }
	        System.out.println("flgTaxOn of checkoperatn is :" +flgTaxOn);
	        return flgTaxOn;
	    }
	   
	   private double funGetTaxIndicatorTotal(String indicator, List<clsItemDtlForTax> arrListItemDtl)throws Exception
	    {
	    	cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	        String sql_Query="";
	        double indicatorAmount=0.00;
	        for (int cnt=0;cnt<arrListItemDtl.size();cnt++) 
	        {
	            clsItemDtlForTax objItemDtl=arrListItemDtl.get(cnt);
	            sql_Query="select strTaxIndicator from tblitemmaster "
	                + "where strItemCode='"+objItemDtl.getItemCode()+"' "
	                + "and strTaxIndicator='"+indicator+"'";
	            ResultSet rsTaxForDB= st.executeQuery(sql_Query);
	            if(rsTaxForDB.next())
	            {
	                indicatorAmount+=objItemDtl.getAmount();
	            }
	            rsTaxForDB.close();
	          
	        }
	        st.close();
            cmsCon.close();
	        return indicatorAmount;
	    }   
	   
	   
	   
	   private double funGetTaxIndicatorBasedDiscAmtTotal(String indicator, List<clsItemDtlForTax> arrListItemDtl) throws Exception
	    {
	    	cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	        String sql_Query = "";
	        double discAmt = 0.00;
	        for (int cnt = 0; cnt < arrListItemDtl.size(); cnt++)
	        {
	            clsItemDtlForTax objItemDtl = arrListItemDtl.get(cnt);
	            sql_Query = "select strTaxIndicator from tblitemmaster "
	                    + "where strItemCode='" + objItemDtl.getItemCode().substring(0, 7) + "' "
	                    + "and strTaxIndicator='" + indicator + "'";
	            ResultSet rsTaxForDB =st.executeQuery(sql_Query);
	            if (rsTaxForDB.next())
	            {
	                discAmt += objItemDtl.getDiscAmt();
	            }
	            rsTaxForDB.close();
	            st.close();
	            cmsCon.close();
	        }
	        return discAmt;
	    }
	   
	   
	   
	   private double funGetTaxAmountForTaxOnTaxForIndicatorTax(String taxOnTaxCode, double indicatorTaxableAmt, List<clsTaxCalculationDtls> listTaxDtl) throws Exception
	    {
	        double taxAmt = 0;
	        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
	        for (clsTaxCalculationDtls objTaxCalDtl : listTaxDtl)
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
	   
	   
	   
	   private double funGetTaxableAmountForTaxOnTax(String taxOnTaxCode, List<clsTaxCalculationDtls> arrListTaxCal) throws Exception
	    {
	        double taxableAmt=0;
	        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
	        for(int cnt=0;cnt<arrListTaxCal.size();cnt++)
	        {
	            for(int t=0;t<spTaxOnTaxCode.length;t++)
	            {
	                clsTaxCalculationDtls objTaxDtls=arrListTaxCal.get(cnt);
	                if(objTaxDtls.getTaxCode().equals(spTaxOnTaxCode[t]))
	                {
	                    taxableAmt+=objTaxDtls.getTaxableAmount()+objTaxDtls.getTaxAmount();
	                }
	            }
	        }
	        return taxableAmt;
	    }

	   
	   
	   private double funGetTaxOnTaxAmtForIndicatorTax(String taxCode, double taxableAmt) throws Exception
	    {
	    	cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	        double taxAmt = 0;
	        String sql = "select a.strTaxCode,a.strTaxType,a.dblPercent"
	                + " ,a.dblAmount,a.strTaxOnGD,a.strTaxCalculation "
	                + " from tbltaxhd a "
	                + " where a.strTaxOnSP='Sales' and a.strTaxCode='" + taxCode + "'";
	        ResultSet rsTaxOnTax =st.executeQuery(sql);
	        if (rsTaxOnTax.next())
	        {
	            double taxPercent = rsTaxOnTax.getDouble(3);
	            if (rsTaxOnTax.getString(6).equals("Forward")) // Forward Tax Calculation
	            {
	                taxAmt = taxableAmt * (taxPercent / 100);
	            }
	            else // Backward Tax Calculation
	            {
	                taxAmt = taxableAmt * 100 / (100 + taxPercent);
	                taxAmt = taxableAmt - taxAmt;
	            }
	        }
	        rsTaxOnTax.close();
	        st.close();
            cmsCon.close();
	        return taxAmt;
	    }
	   
	   
	 //new logic for tax on tax
	    private double funGetTaxableAmountForTaxOnTax(String taxOnTaxCode, clsItemDtlForTax objItemDtl) throws Exception
	    {
	    	cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	    	
	        double taxAmt = 0;
	        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");

	        for (int t = 0; t < spTaxOnTaxCode.length; t++)
	        {
	            String sqlTaxOnTax = "select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent,a.dblAmount,a.dteValidFrom,a.dteValidTo,a.strTaxOnGD,a.strTaxCalculation,a.strTaxIndicator "
	                    + ",a.strTaxRounded,a.strTaxOnTax,a.strTaxOnTaxCode "
	                    + "from tbltaxhd a "
	                    + "where a.strTaxCode='" + spTaxOnTaxCode[t] + "' ";
	            ResultSet rsTaxOnTax =st.executeQuery(sqlTaxOnTax);
	            if (rsTaxOnTax.next())
	            {
	                String taxCode = rsTaxOnTax.getString(1);
	                String taxName = rsTaxOnTax.getString(2);
	                String taxOnGD = rsTaxOnTax.getString(7);
	                String taxCal = rsTaxOnTax.getString(8);
	                String taxIndicator = rsTaxOnTax.getString(9);
	                String taxOnTax = rsTaxOnTax.getString(13);
	                //String taxOnTaxCode = rsTaxOnTax.getString(14);
	                double taxPercent = Double.parseDouble(rsTaxOnTax.getString(5));

	                if (taxOnGD.equals("Gross"))
	                {
	                    taxAmt = (taxPercent / 100) * objItemDtl.getAmount();
	                }
	                else//discount
	                {
	                    taxAmt = (taxPercent / 100) * (objItemDtl.getAmount() - objItemDtl.getDiscAmt());
	                }
	            }
	            rsTaxOnTax.close();
	        }

	        return taxAmt;
	    }
	    
	    
	    
	   
	    
	    
	  //new logic for tax on tax
	    private double funGetTaxableAmountForTaxOnTax(String taxOnTaxCode, clsItemDtlForTax objItemDtl, String billAreaCode, String operationTypeForTax, String settlementCode,String POSCode) throws Exception
	    {
	    	cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	    	Statement st1 = cmsCon.createStatement();
	    	
	        double taxAmt = 0;
	        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
	        String opType = "", taxAreaCodes = "";

	        for (int t = 0; t < spTaxOnTaxCode.length; t++)
	        {

	            String sqlTaxOn = "select a.strAreaCode,a.strOperationType,a.strItemType  "
	                    + "from tbltaxhd a,tbltaxposdtl b "
	                    + "where a.strTaxCode=b.strTaxCode "
	                    + "and a.strTaxCode='" + spTaxOnTaxCode[t] + "' "
	                    + "and (b.strPOSCode='" + POSCode + "' or b.strPOSCode='All') ";
	            ResultSet rsTaxOn =st.executeQuery(sqlTaxOn);
	            if (rsTaxOn.next())
	            {
	                taxAreaCodes = rsTaxOn.getString(1);
	                opType = rsTaxOn.getString(2);

	                if (funCheckAreaCode(taxAreaCodes, billAreaCode))
	                {
	                    if (funCheckOperationType(opType, operationTypeForTax))
	                    {
	                        if (funFindSettlementForTax(spTaxOnTaxCode[t], settlementCode))
	                        {

	                            String sqlTaxOnTax = "select a.strTaxCode,a.strTaxDesc,a.strTaxOnSP,a.strTaxType,a.dblPercent,a.dblAmount,a.dteValidFrom,a.dteValidTo,a.strTaxOnGD,a.strTaxCalculation,a.strTaxIndicator "
	                                    + ",a.strTaxRounded,a.strTaxOnTax,a.strTaxOnTaxCode "
	                                    + "from tbltaxhd a "
	                                    + "where a.strTaxCode='" + spTaxOnTaxCode[t] + "' ";
	                            ResultSet rsTaxOnTax =st1.executeQuery(sqlTaxOnTax);
	                            if (rsTaxOnTax.next())
	                            {
	                                String taxCode = rsTaxOnTax.getString(1);
	                                String taxName = rsTaxOnTax.getString(2);
	                                String taxOnGD = rsTaxOnTax.getString(7);
	                                String taxCal = rsTaxOnTax.getString(8);
	                                String taxIndicator = rsTaxOnTax.getString(9);
	                                String taxOnTax = rsTaxOnTax.getString(13);
	                                //String taxOnTaxCode = rsTaxOnTax.getString(14);
	                                double taxPercent = Double.parseDouble(rsTaxOnTax.getString(5));

	                                if (taxOnGD.equals("Gross"))
	                                {
	                                    taxAmt += (taxPercent / 100) * objItemDtl.getAmount();
	                                }
	                                else//discount
	                                {
	                                    taxAmt += (taxPercent / 100) * (objItemDtl.getAmount() - objItemDtl.getDiscAmt());
	                                }
	                            }
	                            rsTaxOnTax.close();

	                        }
	                    }
	                }
	            }
	        }
	        return taxAmt;
	    }
	    
	    
	    
	  //new logic for tax on tax
	    private double funGetTaxableAmountForTaxOnTax(String taxOnTaxCode, double taxableAmt, List<clsTaxCalculationDtls> listTaxDtl) throws Exception
	    {
	        double taxAmt = 0;
	        String[] spTaxOnTaxCode = taxOnTaxCode.split(",");
	        for (clsTaxCalculationDtls objTaxCalDtl : listTaxDtl)
	        {
	            for (int t = 0; t < spTaxOnTaxCode.length; t++)
	            {
	                if (objTaxCalDtl.getTaxCode().equals(spTaxOnTaxCode[t]))
	                {
	                    taxAmt += funGetTaxOnTaxAmt(spTaxOnTaxCode[t], taxableAmt);
	                }
	            }
	        }

	        return taxAmt;
	    }
	    
	    
	    
	    private double funGetTaxOnTaxAmt(String taxCode, double taxableAmt) throws Exception
	    {
	    	cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	    	
	        double taxAmt = 0;
	        String sql = "select a.strTaxCode,a.strTaxType,a.dblPercent"
	                + " ,a.dblAmount,a.strTaxOnGD,a.strTaxCalculation "
	                + " from tbltaxhd a "
	                + " where a.strTaxOnSP='Sales' and a.strTaxCode='" + taxCode + "'";
	        ResultSet rsTax = st.executeQuery(sql);
	        if (rsTax.next())
	        {
	            double taxPercent = rsTax.getDouble(3);
	            if (rsTax.getString(6).equals("Forward")) // Forward Tax Calculation
	            {
	                taxAmt = taxableAmt * (taxPercent / 100);
	            }
	            else // Backward Tax Calculation
	            {
	                taxAmt = taxableAmt * 100 / (100 + taxPercent);
	                taxAmt = taxableAmt - taxAmt;
	            }
	        }
	        rsTax.close();
	        st.close();
            cmsCon.close();
	        return taxAmt;
	    }
}
