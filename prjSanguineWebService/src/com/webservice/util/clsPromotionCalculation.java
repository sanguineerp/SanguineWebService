package com.webservice.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.webservice.controller.clsDatabaseConnection;

public class clsPromotionCalculation 
{
	clsDatabaseConnection objDb = new clsDatabaseConnection();
	Connection cmsCon = null;
	Statement st = null,st1=null,st2=null,st3=null;
	clsUtilityFunctions objUtility=new clsUtilityFunctions();
	
	public Map<String, clsPromotionItems> funCalculatePromotions(String transType, String billFromKOTsList, String billNo, List<clsBillDtl> listOfSplitedGridItems,String gPOSStartDate,String posCode,String tableNo,List<clsDirectBillerItemDtl> listDirectBillerItemDtl,String gAreaWisePromotions,String areaCode) throws Exception
    {
		boolean flgPromotionOnDiscount = false, flgPromotionOnItems = false;
	    String dtPOSDate;
	    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date posDateForTrans = dFormat.parse(gPOSStartDate);
	    dtPOSDate = (posDateForTrans.getYear() + 1900) + "-" + (posDateForTrans.getMonth() + 1) + "-" + posDateForTrans.getDate();
	    
        cmsCon = objDb.funOpenAPOSCon("mysql", "master");
    	st = cmsCon.createStatement();
    	st1 = cmsCon.createStatement();
    	st2 = cmsCon.createStatement();
    	st3 = cmsCon.createStatement();
    	Statement st1 = cmsCon.createStatement();
    	
        Map<String, List<clsBuyPromotionItemDtl>> hmBuyPromoItems = new HashMap<String, List<clsBuyPromotionItemDtl>>();
        Map<String, List<clsGetPromotionItemDtl>> hmGetPromoItems = new HashMap<String, List<clsGetPromotionItemDtl>>();
        Map<String, clsPromotionItems> hmPromoItems = new HashMap<String, clsPromotionItems>();

        String day = funGetDayOfWeek(posDateForTrans.getDay());
        List<String> listBillItems = new ArrayList<String>();

        StringBuilder sbPromo = new StringBuilder();

        if (transType.equals("MakeKOT"))
        {
            listBillItems.clear();
            sbPromo.setLength(0);
            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblItemQuantity),c.strDays"
                    + " ,c.tmeFromTime,c.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,sum(a.dblAmount) "
                    + " ,c.strPromotionOn,c.strGetPromoOn "
                    + " from tblitemrtemp a,tblbuypromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode "
                    + " and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') "
                    + " and c.strPromoCode=d.strPromoCode "
                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
                    + " and a.strTableNo='" + tableNo + "' ");
            if (gAreaWisePromotions.equals("Y"))
            {
            	sbPromo.append("and c.strAreaCode='" + areaCode + "'");
            }
            sbPromo.append(" and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') "
                    + " and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
                    + " group by a.strItemCode,c.strPromoCode "
                    + " order by a.dblAmount desc");

            ResultSet rsBuyPromoItems = st.executeQuery(sbPromo.toString());
            while (rsBuyPromoItems.next())
            {
                listBillItems.add(rsBuyPromoItems.getString(2));
                double kotItemQty = rsBuyPromoItems.getDouble(5);
                double buyPromoItemQty = rsBuyPromoItems.getDouble(4);

                if (rsBuyPromoItems.getString(12).equals("MenuHead"))
                {
                    List<clsBuyPromotionItemDtl> listBuyItemDtl = null;
                    if (null != hmBuyPromoItems.get(rsBuyPromoItems.getString(1)))
                    {
                        listBuyItemDtl = hmBuyPromoItems.get(rsBuyPromoItems.getString(1));

                        clsBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
                        objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + rsBuyPromoItems.getDouble(5));
                        objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + rsBuyPromoItems.getDouble(11));
                        listBuyItemDtl.set(0, objBuyPromoItemDtl);
                    }
                    else
                    {
                        listBuyItemDtl = new ArrayList<clsBuyPromotionItemDtl>();
                        clsBuyPromotionItemDtl objBuyPromoItemDtl = new clsBuyPromotionItemDtl();
                        objBuyPromoItemDtl.setItemCode(rsBuyPromoItems.getString(2));           // Item Code. 
                        objBuyPromoItemDtl.setBuyPromoItemQty(rsBuyPromoItems.getDouble(4));    // Buy Promo Item Qty. 
                        objBuyPromoItemDtl.setTotalItemQty(rsBuyPromoItems.getDouble(5));       // Total Item Qty. 
                        objBuyPromoItemDtl.setTotalAmount(rsBuyPromoItems.getDouble(11));       // Total Amt. 
                        objBuyPromoItemDtl.setBuyPromoOn(rsBuyPromoItems.getString(12));
                        objBuyPromoItemDtl.setGetPromoOn(rsBuyPromoItems.getString(13));
                        listBuyItemDtl.add(objBuyPromoItemDtl);

                        //hmBuyPromoItems.put(rsBuyPromoItems.getString(1),listBuyItemDtl);
                    }
                    if (null != listBuyItemDtl)
                    {
                        flgPromotionOnItems = true;
                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
                        hmBuyPromoItems.put(rsBuyPromoItems.getString(1), listBuyItemDtl);
                    }
                }
                else
                {

                    List<clsBuyPromotionItemDtl> listBuyItemDtl = null;
                    if (null != hmBuyPromoItems.get(rsBuyPromoItems.getString(1)))
                    {
                        listBuyItemDtl = hmBuyPromoItems.get(rsBuyPromoItems.getString(1));

                        clsBuyPromotionItemDtl objBuyPromoItemDtl = new clsBuyPromotionItemDtl();
                        objBuyPromoItemDtl.setItemCode(rsBuyPromoItems.getString(2));           // Item Code. 
                        objBuyPromoItemDtl.setBuyPromoItemQty(rsBuyPromoItems.getDouble(4));    // Buy Promo Item Qty. 
                        objBuyPromoItemDtl.setTotalItemQty(rsBuyPromoItems.getDouble(5));       // Total Item Qty. 
                        objBuyPromoItemDtl.setTotalAmount(rsBuyPromoItems.getDouble(11));       // Total Amt. 
                        objBuyPromoItemDtl.setBuyPromoOn(rsBuyPromoItems.getString(12));
                        objBuyPromoItemDtl.setGetPromoOn(rsBuyPromoItems.getString(13));
                        listBuyItemDtl.add(objBuyPromoItemDtl);
                    }
                    else
                    {
                        listBuyItemDtl = new ArrayList<clsBuyPromotionItemDtl>();
                        clsBuyPromotionItemDtl objBuyPromoItemDtl = new clsBuyPromotionItemDtl();
                        objBuyPromoItemDtl.setItemCode(rsBuyPromoItems.getString(2));           // Item Code. 
                        objBuyPromoItemDtl.setBuyPromoItemQty(rsBuyPromoItems.getDouble(4));    // Buy Promo Item Qty. 
                        objBuyPromoItemDtl.setTotalItemQty(rsBuyPromoItems.getDouble(5));       // Total Item Qty. 
                        objBuyPromoItemDtl.setTotalAmount(rsBuyPromoItems.getDouble(11));       // Total Amt. 
                        objBuyPromoItemDtl.setBuyPromoOn(rsBuyPromoItems.getString(12));
                        objBuyPromoItemDtl.setGetPromoOn(rsBuyPromoItems.getString(13));
                        listBuyItemDtl.add(objBuyPromoItemDtl);

                        //hmBuyPromoItems.put(rsBuyPromoItems.getString(1),listBuyItemDtl);
                    }
                    if (null != listBuyItemDtl)
                    {
                        flgPromotionOnItems = true;
                        Collections.sort(listBuyItemDtl, COMPARATORBUY);
                        hmBuyPromoItems.put(rsBuyPromoItems.getString(1), listBuyItemDtl);
                    }

                }
            }
            rsBuyPromoItems.close();

            sbPromo.setLength(0);
            sbPromo.append("select c.strPromoCode,a.strItemCode,a.strItemName,b.dblGetQty,sum(a.dblItemQuantity),c.strDays,c.tmeFromTime"
                    + ",c.tmeToTime,a.dteDateCreated,sum(a.dblAmount),b.strPromotionOn,c.longKOTTimeBound "
                    + " from tblitemrtemp a,tblpromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl d "
                    + " where a.strItemCode=b.strPromoItemCode and b.strPromoCode=c.strPromoCode "
                    + " and (a.strPOSCode=c.strPOSCode or c.strPOSCode='All') and c.strPromoCode=d.strPromoCode "
                    + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
                    + " and a.strTableNo='" + tableNo + "' ");
            if (gAreaWisePromotions.equals("Y"))
            {
            	sbPromo.append("and c.strAreaCode='" + areaCode + "'");
            }
            sbPromo.append(" and (c.strPOSCode='" + posCode + "' or c.strPOSCode='All') "
                    + " and d.strDay='" + day + "' and time(a.dteDateCreated) between d.tmeFromTime and d.tmeToTime "
                    + " group by a.strItemCode,c.strPromoCode "
                    + " order by a.dblAmount desc ");
            //System.out.println(sql);
            ResultSet rsGetPromoItems = st1.executeQuery(sbPromo.toString()); 
            while (rsGetPromoItems.next())
            {
                if (rsGetPromoItems.getString(11).equals("PromoGroup"))
                {
                    sbPromo.setLength(0);
                    String intervalTime = "INTERVAL " + rsGetPromoItems.getString(12) + " HOUR";
                    sbPromo.append("select sum(dblItemQuantity),sum(dblAmount) "
                            + " from tblitemrtemp where strTableno='" + tableNo + "' "
                            + " and dtedatecreated between (select min(dtedatecreated) from tblitemrtemp where strTableno='" + tableNo + "' ) "
                            + " and (select date_add(min(dtedatecreated)," + intervalTime + ") from tblitemrtemp where strTableno='" + tableNo + "') "
                            + " and strItemCode='" + rsGetPromoItems.getString(2) + "' "
                            + " group by strItemCode "
                            + " order by dtedatecreated");
                    ResultSet rsPGType = st2.executeQuery(sbPromo.toString());
                    while (rsPGType.next())
                    {
                        double getPromoItemQty = rsPGType.getDouble(1);
                        double kotItemQty1 = rsPGType.getDouble(2);
                        if (getPromoItemQty <= kotItemQty1)
                        {
                            String promoCode = rsGetPromoItems.getString(1);
                            List<clsGetPromotionItemDtl> listGetItemDtl = null;
                            if (null != hmGetPromoItems.get(promoCode))
                            {
                                listGetItemDtl = hmGetPromoItems.get(promoCode);
                                clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                                objGetPromoItemDtl.setItemCode(rsGetPromoItems.getString(2));               // Get Item Code
                                objGetPromoItemDtl.setGetPromoItemQty(rsPGType.getDouble(1));               // Get Promo Item Qty.
                                objGetPromoItemDtl.setTotalItemQty(rsPGType.getDouble(1));                  // Total Item Qty.
                                objGetPromoItemDtl.setTotalAmount(rsPGType.getDouble(2));                   // Total Amount.
                                listGetItemDtl.add(objGetPromoItemDtl);
                            }
                            else
                            {
                                listGetItemDtl = new ArrayList<clsGetPromotionItemDtl>();
                                clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                                objGetPromoItemDtl.setItemCode(rsGetPromoItems.getString(2));               // Get Item Code
                                objGetPromoItemDtl.setGetPromoItemQty(rsPGType.getDouble(1));               // Get Promo Item Qty.
                                objGetPromoItemDtl.setTotalItemQty(rsPGType.getDouble(1));                  // Total Item Qty.
                                objGetPromoItemDtl.setTotalAmount(rsPGType.getDouble(2));                   // Total Amount.
                                listGetItemDtl.add(objGetPromoItemDtl);
                            }
                            if (null != listGetItemDtl)
                            {
                                Collections.sort(listGetItemDtl, COMPARATORGET);
                                hmGetPromoItems.put(promoCode, listGetItemDtl);
                            }
                        }
                    }
                    rsPGType.close();
                }
                else
                {
                    double getPromoItemQty = rsGetPromoItems.getDouble(4);
                    double kotItemQty1 = rsGetPromoItems.getDouble(5);
                    if (rsGetPromoItems.getString(11).equals("MenuHead"))
                    {
                        String promoCode = rsGetPromoItems.getString(1);
                        List<clsGetPromotionItemDtl> listGetItemDtl = null;
                        if (null != hmGetPromoItems.get(promoCode))
                        {
                            listGetItemDtl = hmGetPromoItems.get(promoCode);
                            clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                            objGetPromoItemDtl.setItemCode(rsGetPromoItems.getString(2));               // Get Item Code
                            objGetPromoItemDtl.setGetPromoItemQty(rsGetPromoItems.getDouble(4));        // Get Promo Item Qty.
                            objGetPromoItemDtl.setTotalItemQty(rsGetPromoItems.getDouble(5));           // Total Item Qty.
                            objGetPromoItemDtl.setTotalAmount(rsGetPromoItems.getDouble(10));           // Total Amount.
                            listGetItemDtl.add(objGetPromoItemDtl);
                        }
                        else
                        {
                            listGetItemDtl = new ArrayList<clsGetPromotionItemDtl>();
                            clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                            objGetPromoItemDtl.setItemCode(rsGetPromoItems.getString(2));               // Get Item Code
                            objGetPromoItemDtl.setGetPromoItemQty(rsGetPromoItems.getDouble(4));        // Get Promo Item Qty.
                            objGetPromoItemDtl.setTotalItemQty(rsGetPromoItems.getDouble(5));           // Total Item Qty.
                            objGetPromoItemDtl.setTotalAmount(rsGetPromoItems.getDouble(10));           // Total Amount.
                            listGetItemDtl.add(objGetPromoItemDtl);
                        }
                        if (null != listGetItemDtl)
                        {
                            flgPromotionOnItems = true;
                            Collections.sort(listGetItemDtl, COMPARATORGET);
                            hmGetPromoItems.put(promoCode, listGetItemDtl);
                        }
                    }
                    else
                    {
                        if (getPromoItemQty <= kotItemQty1)
                        {
                            String promoCode = rsGetPromoItems.getString(1);
                            List<clsGetPromotionItemDtl> listGetItemDtl = null;
                            if (null != hmGetPromoItems.get(promoCode))
                            {
                                listGetItemDtl = hmGetPromoItems.get(promoCode);
                                clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                                objGetPromoItemDtl.setItemCode(rsGetPromoItems.getString(2));               // Get Item Code
                                objGetPromoItemDtl.setGetPromoItemQty(rsGetPromoItems.getDouble(4));        // Get Promo Item Qty.
                                objGetPromoItemDtl.setTotalItemQty(rsGetPromoItems.getDouble(5));           // Total Item Qty.
                                objGetPromoItemDtl.setTotalAmount(rsGetPromoItems.getDouble(10));           // Total Amount.
                                listGetItemDtl.add(objGetPromoItemDtl);
                            }
                            else
                            {
                                listGetItemDtl = new ArrayList<clsGetPromotionItemDtl>();
                                clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                                objGetPromoItemDtl.setItemCode(rsGetPromoItems.getString(2));               // Get Item Code
                                objGetPromoItemDtl.setGetPromoItemQty(rsGetPromoItems.getDouble(4));        // Get Promo Item Qty.
                                objGetPromoItemDtl.setTotalItemQty(rsGetPromoItems.getDouble(5));           // Total Item Qty.
                                objGetPromoItemDtl.setTotalAmount(rsGetPromoItems.getDouble(10));           // Total Amount.
                                listGetItemDtl.add(objGetPromoItemDtl);
                            }
                            if (null != listGetItemDtl)
                            {
                                flgPromotionOnItems = true;
                                Collections.sort(listGetItemDtl, COMPARATORGET);
                                hmGetPromoItems.put(promoCode, listGetItemDtl);
                            }
                        }
                    }
                }
            }
            rsGetPromoItems.close();

            sbPromo.setLength(0);
            sbPromo.append("select d.strPromoCode,a.strItemCode,a.strItemName,b.dblBuyItemQty,sum(a.dblItemQuantity),d.strDays "
                    + " ,d.tmeFromTime,d.tmeToTime,time(a.dteDateCreated),a.dteDateCreated,c.strDiscountType,c.dblDiscount "
                    + " from tblitemrtemp a,tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d,tblpromotiondaytimedtl e "
                    + " where a.strItemCode=b.strBuyPromoItemCode and b.strPromoCode=c.strPromoCode and c.strPromoCode=d.strPromoCode  "
                    + " and d.strPromoCode=e.strPromoCode and date(d.dteFromDate) <= '" + dtPOSDate + "' and date(d.dteToDate) >= '" + dtPOSDate + "' "
                    + " and a.strTableNo='" + tableNo + "' and (a.strPOSCode='" + posCode + "' or  a.strPOSCode='All') "
                    + " and c.dblDiscount > 0 and e.strDay='" + day + "' and time(a.dteDateCreated) between e.tmeFromTime and e.tmeToTime ");
            if (gAreaWisePromotions.equals("Y"))
            {
            	 sbPromo.append("and d.strAreaCode='" + areaCode + "'");
            }
            sbPromo.append(" group by a.strItemCode,c.strPromoCode order by a.dblAmount desc");
            //System.out.println(sql);
            ResultSet rsDiscountPromoItems = st3.executeQuery(sbPromo.toString());
            while (rsDiscountPromoItems.next())
            {
                String[] arrKOTDate = rsDiscountPromoItems.getString(10).split(" ");
                String KOTDate = arrKOTDate[0].split("-")[2] + "-" + arrKOTDate[0].split("-")[1] + "-" + arrKOTDate[0].split("-")[0];
                String KOTDateTime = KOTDate + " " + arrKOTDate[1];

                double kotItemQty = rsDiscountPromoItems.getDouble(5);
                double buyPromoItemQty = rsDiscountPromoItems.getDouble(4);

                if (buyPromoItemQty <= kotItemQty)
                {
                    //String getItemDtl=rsDiscountPromoItems.getString(12)+"#"+rsDiscountPromoItems.getString(1)+"#Free";
                    clsPromotionItems objPromoItems = new clsPromotionItems();
                    objPromoItems.setItemCode(rsDiscountPromoItems.getString(2));
                    objPromoItems.setPromoType("Discount");
                    objPromoItems.setPromoCode(rsDiscountPromoItems.getString(1));
                    objPromoItems.setFreeItemQty(rsDiscountPromoItems.getDouble(5));
                    objPromoItems.setDiscType(rsDiscountPromoItems.getString(11));
                    if (rsDiscountPromoItems.getString(11).equals("Value"))
                    {
                        objPromoItems.setDiscAmt(rsDiscountPromoItems.getDouble(12));
                    }
                    else
                    {
                        objPromoItems.setDiscPer(rsDiscountPromoItems.getDouble(12));
                    }
                    hmPromoItems.put(rsDiscountPromoItems.getString(2), objPromoItems);
                    flgPromotionOnDiscount = true;
                }
            }
            rsDiscountPromoItems.close();
        }
        else   // Direct Biller
        {
            listBillItems.clear();
            Date dt = new Date();
            //String currTime = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
            String hours = String.valueOf(dt.getHours());
            String minutes = String.valueOf(dt.getMinutes());
            String seconds = String.valueOf(dt.getSeconds());
            if (hours.length() == 1)
            {
                hours = "0" + hours;
            }
            if (minutes.length() == 1)
            {
                minutes = "0" + minutes;
            }
            if (seconds.length() == 1)
            {
                seconds = "0" + seconds;
            }
            listBillItems.clear();

            String currTime = hours + ":" + minutes + ":" + seconds;
           // List<clsDirectBillerItemDtl> listDirectBillerItemDtl = objDirectBiller.getObj_List_ItemDtl();
            for (clsDirectBillerItemDtl objDirectBillerItems : listDirectBillerItemDtl)
            {
                sbPromo.setLength(0);
                sbPromo.append("select c.strPromoCode,b.strBuyPromoItemCode,b.dblBuyItemQty,c.strDays"
                        + ",c.tmeFromTime,c.tmeToTime,c.strPromotionOn,c.strGetPromoOn "
                        + " from tblbuypromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl e "
                        + " where b.strPromoCode=c.strPromoCode and c.strPromoCode=e.strPromoCode "
                        + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
                        + " and (c.strPOSCode='" +posCode + "' or c.strPOSCode='All') "
                        + " and b.strBuyPromoItemCode='" + objDirectBillerItems.getItemCode() + "' "
                        + " and e.strDay='" + day + "' ");
                if (gAreaWisePromotions.equals("Y"))
                {
                	sbPromo.append(" and c.strAreaCode='" + areaCode + "' ");
                }
                sbPromo.append(" AND TIME(e.tmeFromTime) <='" + currTime + "' "//added
                        + " AND TIME(e.tmeToTime) >='" + currTime + "' ");//added
                ResultSet rsDirectBillerItems = st.executeQuery(sbPromo.toString());
                while (rsDirectBillerItems.next())
                {
                    listBillItems.add(objDirectBillerItems.getItemCode());
                    double billItemQty = objDirectBillerItems.getQty();
                    double buyPromoItemQty = rsDirectBillerItems.getDouble(3);

                    if (rsDirectBillerItems.getString(7).equals("MenuHead"))
                    {
                        List<clsBuyPromotionItemDtl> listBuyItemDtl = null;
                        if (null != hmBuyPromoItems.get(rsDirectBillerItems.getString(1)))
                        {
                            listBuyItemDtl = hmBuyPromoItems.get(rsDirectBillerItems.getString(1));

                            clsBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
                            objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + objDirectBillerItems.getQty());
                            objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + objDirectBillerItems.getAmt());
                            listBuyItemDtl.set(0, objBuyPromoItemDtl);
                        }
                        else
                        {
                            listBuyItemDtl = new ArrayList<clsBuyPromotionItemDtl>();
                            clsBuyPromotionItemDtl objBuyPromoItemDtl = new clsBuyPromotionItemDtl();
                            objBuyPromoItemDtl.setItemCode(rsDirectBillerItems.getString(2));           // Item Code. 
                            objBuyPromoItemDtl.setBuyPromoItemQty(rsDirectBillerItems.getDouble(3));    // Buy Promo Item Qty. 
                            objBuyPromoItemDtl.setTotalItemQty(objDirectBillerItems.getQty());          // Total Item Qty. 
                            objBuyPromoItemDtl.setTotalAmount(objDirectBillerItems.getAmt());           // Total Amt. 
                            objBuyPromoItemDtl.setBuyPromoOn(rsDirectBillerItems.getString(7));
                            objBuyPromoItemDtl.setGetPromoOn(rsDirectBillerItems.getString(8));
                            listBuyItemDtl.add(objBuyPromoItemDtl);
                        }
                        if (null != listBuyItemDtl)
                        {
                            flgPromotionOnItems = true;
                            Collections.sort(listBuyItemDtl, COMPARATORBUY);
                            hmBuyPromoItems.put(rsDirectBillerItems.getString(1), listBuyItemDtl);
                        }
                    }
                    else
                    {
                        List<clsBuyPromotionItemDtl> listBuyItemDtl = null;
                        if (null != hmBuyPromoItems.get(rsDirectBillerItems.getString(1)))
                        {
                            listBuyItemDtl = hmBuyPromoItems.get(rsDirectBillerItems.getString(1));

                            if (rsDirectBillerItems.getString(7).equals("MenuHead"))
                            {
                                clsBuyPromotionItemDtl objBuyPromoItemDtl = listBuyItemDtl.get(0);
                                objBuyPromoItemDtl.setTotalItemQty(objBuyPromoItemDtl.getTotalItemQty() + objDirectBillerItems.getQty());
                                objBuyPromoItemDtl.setTotalAmount(objBuyPromoItemDtl.getTotalAmount() + objDirectBillerItems.getAmt());
                                listBuyItemDtl.set(0, objBuyPromoItemDtl);
                            }
                            else
                            {
                                clsBuyPromotionItemDtl objBuyPromoItemDtl = new clsBuyPromotionItemDtl();
                                objBuyPromoItemDtl.setItemCode(rsDirectBillerItems.getString(2));           // Item Code. 
                                objBuyPromoItemDtl.setBuyPromoItemQty(rsDirectBillerItems.getDouble(3));    // Buy Promo Item Qty. 
                                objBuyPromoItemDtl.setTotalItemQty(objDirectBillerItems.getQty());          // Total Item Qty. 
                                objBuyPromoItemDtl.setTotalAmount(objDirectBillerItems.getAmt());           // Total Amt. 
                                objBuyPromoItemDtl.setBuyPromoOn(rsDirectBillerItems.getString(7));
                                objBuyPromoItemDtl.setGetPromoOn(rsDirectBillerItems.getString(8));
                                listBuyItemDtl.add(objBuyPromoItemDtl);
                            }
                        }
                        else
                        {
                            listBuyItemDtl = new ArrayList<clsBuyPromotionItemDtl>();
                            clsBuyPromotionItemDtl objBuyPromoItemDtl = new clsBuyPromotionItemDtl();
                            objBuyPromoItemDtl.setItemCode(rsDirectBillerItems.getString(2));           // Item Code. 
                            objBuyPromoItemDtl.setBuyPromoItemQty(rsDirectBillerItems.getDouble(3));    // Buy Promo Item Qty. 
                            objBuyPromoItemDtl.setTotalItemQty(objDirectBillerItems.getQty());          // Total Item Qty. 
                            objBuyPromoItemDtl.setTotalAmount(objDirectBillerItems.getAmt());           // Total Amt. 
                            objBuyPromoItemDtl.setBuyPromoOn(rsDirectBillerItems.getString(7));
                            objBuyPromoItemDtl.setGetPromoOn(rsDirectBillerItems.getString(8));
                            listBuyItemDtl.add(objBuyPromoItemDtl);
                        }
                        if (null != listBuyItemDtl)
                        {
                            flgPromotionOnItems = true;
                            Collections.sort(listBuyItemDtl, COMPARATORBUY);
                            hmBuyPromoItems.put(rsDirectBillerItems.getString(1), listBuyItemDtl);
                        }
                    }
                }
                rsDirectBillerItems.close();
            }

            for (clsDirectBillerItemDtl objDirectBillerItems : listDirectBillerItemDtl)
            {
                sbPromo.setLength(0);
                sbPromo.append("select c.strPromoCode,b.strPromoItemCode,b.dblGetQty,c.strDays,c.tmeFromTime,c.tmeToTime"
                        + ",b.strPromotionOn "
                        + " from tblpromotiondtl b,tblpromotionmaster c,tblpromotiondaytimedtl e "
                        + " where b.strPromoCode=c.strPromoCode and b.strPromoItemCode='" + objDirectBillerItems.getItemCode() + "'"
                        + " and c.strPromoCode=e.strPromoCode "
                        + " and date(c.dteFromDate) <= '" + dtPOSDate + "' and date(c.dteToDate) >= '" + dtPOSDate + "' "
                        + " and (c.strPOSCode='" +posCode + "' or c.strPOSCode='All') ");
                if (gAreaWisePromotions.equals("Y"))
                {
                	sbPromo.append(" and c.strAreaCode='" + areaCode + "' ");
                }
                sbPromo.append(" and e.strDay='" + day + "' AND TIME(e.tmeFromTime) <='" + currTime + "' AND TIME(e.tmeToTime) >='" + currTime + "' ");
                ResultSet rsDirectBillerItems = st1.executeQuery(sbPromo.toString());
                		
                while (rsDirectBillerItems.next())
                {
                    double billItemQty = objDirectBillerItems.getQty();
                    double getPromoItemQty = rsDirectBillerItems.getDouble(3);
                    if (rsDirectBillerItems.getString(7).equals("MenuHead"))
                    {
                        String promoCode = rsDirectBillerItems.getString(1);
                        List<clsGetPromotionItemDtl> listGetItemDtl = null;
                        if (null != hmGetPromoItems.get(promoCode))
                        {
                            listGetItemDtl = hmGetPromoItems.get(promoCode);
                            clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                            objGetPromoItemDtl.setItemCode(rsDirectBillerItems.getString(2));               // Get Item Code
                            objGetPromoItemDtl.setGetPromoItemQty(rsDirectBillerItems.getDouble(3));        // Get Promo Item Qty.
                            objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getQty());              // Total Item Qty.
                            objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getAmt());               // Total Amount.
                            listGetItemDtl.add(objGetPromoItemDtl);
                        }
                        else
                        {
                            listGetItemDtl = new ArrayList<clsGetPromotionItemDtl>();
                            clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                            objGetPromoItemDtl.setItemCode(rsDirectBillerItems.getString(2));               // Get Item Code
                            objGetPromoItemDtl.setGetPromoItemQty(rsDirectBillerItems.getDouble(3));        // Get Promo Item Qty.
                            objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getQty());              // Total Item Qty.
                            objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getAmt());               // Total Amount.
                            listGetItemDtl.add(objGetPromoItemDtl);
                        }
                        if (null != listGetItemDtl)
                        {
                            flgPromotionOnItems = true;
                            Collections.sort(listGetItemDtl, COMPARATORGET);
                            hmGetPromoItems.put(promoCode, listGetItemDtl);
                        }
                    }
                    else
                    {
                        if (getPromoItemQty <= billItemQty)
                        {
                            String promoCode = rsDirectBillerItems.getString(1);
                            List<clsGetPromotionItemDtl> listGetItemDtl = null;
                            if (null != hmGetPromoItems.get(promoCode))
                            {
                                listGetItemDtl = hmGetPromoItems.get(promoCode);
                                clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                                objGetPromoItemDtl.setItemCode(rsDirectBillerItems.getString(2));               // Get Item Code
                                objGetPromoItemDtl.setGetPromoItemQty(rsDirectBillerItems.getDouble(3));        // Get Promo Item Qty.
                                objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getQty());              // Total Item Qty.
                                objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getAmt());               // Total Amount.
                                listGetItemDtl.add(objGetPromoItemDtl);
                            }
                            else
                            {
                                listGetItemDtl = new ArrayList<clsGetPromotionItemDtl>();
                                clsGetPromotionItemDtl objGetPromoItemDtl = new clsGetPromotionItemDtl();
                                objGetPromoItemDtl.setItemCode(rsDirectBillerItems.getString(2));               // Get Item Code
                                objGetPromoItemDtl.setGetPromoItemQty(rsDirectBillerItems.getDouble(3));        // Get Promo Item Qty.
                                objGetPromoItemDtl.setTotalItemQty(objDirectBillerItems.getQty());              // Total Item Qty.
                                objGetPromoItemDtl.setTotalAmount(objDirectBillerItems.getAmt());               // Total Amount.
                                listGetItemDtl.add(objGetPromoItemDtl);
                            }
                            if (null != listGetItemDtl)
                            {
                                flgPromotionOnItems = true;
                                Collections.sort(listGetItemDtl, COMPARATORGET);
                                hmGetPromoItems.put(promoCode, listGetItemDtl);
                            }
                        }
                    }
                }
                rsDirectBillerItems.close();
            }
            for (clsDirectBillerItemDtl objDirectBillerItems : listDirectBillerItemDtl)
            {
                sbPromo.setLength(0);
                sbPromo.append("select d.strPromoCode,b.dblBuyItemQty,d.strDays,d.tmeFromTime,d.tmeToTime,c.strDiscountType"
                        + ",c.dblDiscount,b.strBuyPromoItemCode "
                        + " from tblbuypromotiondtl b,tblpromotiondtl c,tblpromotionmaster d,tblpromotiondaytimedtl e "
                        + " where b.strPromoCode=c.strPromoCode and c.strPromoCode=d.strPromoCode and d.strPromoCode=e.strPromoCode "
                        + " and date(d.dteFromDate) <= '" + dtPOSDate + "' and date(d.dteToDate) >= '" + dtPOSDate + "' "
                        + " and c.dblDiscount > 0 and d.strPromotionOn!='BillAmount' ");
               if (gAreaWisePromotions.equals("Y"))
                {
                	sbPromo.append(" and d.strAreaCode='" + areaCode + "' ");
                }
                sbPromo.append(" and b.strBuyPromoItemCode='" + objDirectBillerItems.getItemCode() + "' and e.strDay='" + day + "' "
                        + " AND TIME(e.tmeFromTime) <='" + currTime + "' AND TIME(e.tmeToTime) >='" + currTime + "' ");
                //System.out.println(sql);
                ResultSet rsDiscountPromoItems = st2.executeQuery(sbPromo.toString());
                while (rsDiscountPromoItems.next())
                {
                    double billItemQty = objDirectBillerItems.getQty();
                    double buyPromoItemQty = rsDiscountPromoItems.getDouble(2);
                    if (buyPromoItemQty <= billItemQty)
                    {
                        clsPromotionItems objPromoItems = new clsPromotionItems();
                        objPromoItems.setItemCode(rsDiscountPromoItems.getString(8));
                        objPromoItems.setPromoType("Discount");
                        objPromoItems.setPromoCode(rsDiscountPromoItems.getString(1));
                        objPromoItems.setFreeItemQty(billItemQty);
                        objPromoItems.setDiscType(rsDiscountPromoItems.getString(6));
                        if (rsDiscountPromoItems.getString(6).equals("Value"))
                        {
                            objPromoItems.setDiscAmt(rsDiscountPromoItems.getDouble(7));
                        }
                        else
                        {
                            objPromoItems.setDiscPer(rsDiscountPromoItems.getDouble(7));
                        }
                        hmPromoItems.put(rsDiscountPromoItems.getString(8), objPromoItems);
                        flgPromotionOnDiscount = true;
                    }
                }
                rsDiscountPromoItems.close();
            }
        }

        sbPromo = null;

        if (flgPromotionOnItems && !flgPromotionOnDiscount)
    	{
    	    //Start loop on Buy Promotion items map     
    	    for (Map.Entry<String, List<clsBuyPromotionItemDtl>> entry : hmBuyPromoItems.entrySet())
    	    {
    		String promoCode = entry.getKey();
    		List<clsBuyPromotionItemDtl> listBuyItemDtl = entry.getValue();
    		double totalBuyQty = 0;
    		for (clsBuyPromotionItemDtl objBuyPromoItemDtl : listBuyItemDtl)
    		{
    		    double buyQty = objBuyPromoItemDtl.getBuyPromoItemQty();   // Buy Qty defined in buy promotion master.
    		    totalBuyQty = objBuyPromoItemDtl.getTotalItemQty();        // Buy Qty defined in buy promotion master.

    		    if (objBuyPromoItemDtl.getBuyPromoOn().equals("Item") && objBuyPromoItemDtl.getGetPromoOn().equals("PromoGroup"))
    		    {
    			if (null != hmGetPromoItems.get(promoCode))
    			{
    			    List<clsGetPromotionItemDtl> listGetItemDtl = hmGetPromoItems.get(promoCode);
    			    for (clsGetPromotionItemDtl objGetPromoItemDtl : listGetItemDtl)
    			    {
    				clsPromotionItems objPromoItems = new clsPromotionItems();
    				objPromoItems.setItemCode(objGetPromoItemDtl.getItemCode());
    				objPromoItems.setPromoType("ItemWise");
    				objPromoItems.setPromoCode(promoCode);
    				objPromoItems.setDiscType("");
    				objPromoItems.setDiscAmt(0);
    				objPromoItems.setDiscPer(0);
    				objPromoItems.setFreeItemQty(objGetPromoItemDtl.getTotalItemQty());

    				hmPromoItems.put(objGetPromoItemDtl.getItemCode(), objPromoItems);
    			    }
    			}
    		    }
    		    else // For Menuheads
    		    {
    			if (null != hmGetPromoItems.get(promoCode))
    			{
    			    int freeQty = 0, checkFreeQty = 0, totalFreeQty = 0;
    			    List<clsGetPromotionItemDtl> listGetItemDtl = hmGetPromoItems.get(promoCode);
    			    if (listGetItemDtl.size() > 0)
    			    {
    				clsGetPromotionItemDtl objGetPromoItemDtlTemp = listGetItemDtl.get(0);
    			    }
    			    for (clsGetPromotionItemDtl objGetPromoItemDtl : listGetItemDtl)
    			    {
	    				double getQty = objGetPromoItemDtl.getGetPromoItemQty();
	    				double promoQty = buyQty + getQty;
	    				if (listBillItems.contains(objGetPromoItemDtl.getItemCode()))
	    				{
	    				    if (getQty > buyQty)
	    				    {
	    					int totalGetQty = (int) objGetPromoItemDtl.getTotalItemQty();
	    					for (int cn = 0; cn < totalBuyQty; cn++)
	    					{
	    					    if (totalGetQty >= promoQty)
	    					    {
	    						freeQty += getQty;
	    						totalGetQty -= getQty;
	    					    }
	    					    else
	    					    {
	    						break;
	    					    }
	    					}
	    				    }
	    				    else if (getQty == buyQty && totalBuyQty>=promoQty)
	    				    {
	    					if(getQty==1 && buyQty==1)
	    					{
	    					    freeQty = (int) totalBuyQty / (int) promoQty;
	    					}
	    					else
	    					{
	    					    int counter = (int) totalBuyQty/ (int) buyQty;
	    					    for(int cn=1;cn<=counter;cn++)
	    					    {
	    						if(cn%buyQty==0)
	    						{
	    						    freeQty+= (int) buyQty;
	    						}
	    					    }
	    					}
	    				    }
	    				    else
	    				    {
	    					freeQty = (int) totalBuyQty / (int) promoQty;
	    				    }
	    				}
	    				else
	    				{
	    				    if (getQty > buyQty)
	    				    {
	    					int totalGetQty = (int) objGetPromoItemDtl.getTotalItemQty();
	    					for (int cn = 0; cn < totalBuyQty; cn++)
	    					{
	    					    if (totalGetQty > 0)
	    					    {
	    						freeQty += getQty;
	    						totalGetQty -= getQty;
	    					    }
	    					    else
	    					    {
	    						break;
	    					    }
	    					}
	    				    }
	    				    else
	    				    {
	    					freeQty = (int) totalBuyQty / (int) buyQty;
	    				    }
	    				}
	    				freeQty = freeQty - totalFreeQty;
	
	    				clsPromotionItems objPromoItems = new clsPromotionItems();
	    				objPromoItems.setItemCode(objGetPromoItemDtl.getItemCode());
	    				objPromoItems.setPromoType("ItemWise");
	    				objPromoItems.setPromoCode(promoCode);
	    				objPromoItems.setDiscType("");
	    				objPromoItems.setDiscAmt(0);
	    				objPromoItems.setDiscPer(0);
	
	    				int itemQty = (int) objGetPromoItemDtl.getTotalItemQty();
	    				if (itemQty < freeQty)
	    				{
	    				    checkFreeQty = freeQty - itemQty;
	    				    totalFreeQty = totalFreeQty + itemQty;
	    				    objPromoItems.setFreeItemQty(itemQty);
	    				}
	    				else
	    				{
	    				    totalFreeQty = totalFreeQty + freeQty;
	    				    objPromoItems.setFreeItemQty(freeQty);
	    				    checkFreeQty = 0;
	    				}
	    				if (objPromoItems.getFreeItemQty() > 0)
	    				{
	
	    				    //added if
	    				    if (hmPromoItems.containsKey(objGetPromoItemDtl.getItemCode()))
	    				    {
	    					clsPromotionItems objOldPromoItems = hmPromoItems.get(objGetPromoItemDtl.getItemCode());
	    					if (itemQty >= (objPromoItems.getFreeItemQty() + objOldPromoItems.getFreeItemQty()))
	    					{
	    					    objPromoItems.setFreeItemQty(objPromoItems.getFreeItemQty() + objOldPromoItems.getFreeItemQty());
	    					    hmPromoItems.put(objGetPromoItemDtl.getItemCode(), objPromoItems);
	    					}
	    				    }
	    				    else
	    				    {
	    					hmPromoItems.put(objGetPromoItemDtl.getItemCode(), objPromoItems);
	    				    }
	
	    				}
	
	    				if (checkFreeQty < 1)
	    				{
	    				    break;
	    				}
    			    }
    			 }
    		    }
    		 }
    	   }
    	}
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
             return hmPromoItems;
    }
	 

	 
	   
	  
	 
	 
	 
	 private String funGetDayOfWeek(int day)
	    {
	        String dayOfWeek = "";
	        switch (day)
	        {
	            case 0:
	                dayOfWeek = "Sunday";
	                break;

	            case 1:
	                dayOfWeek = "Monday";
	                break;

	            case 2:
	                dayOfWeek = "Tuesday";
	                break;

	            case 3:
	                dayOfWeek = "Wednesday";
	                break;

	            case 4:
	                dayOfWeek = "Thursday";
	                break;

	            case 5:
	                dayOfWeek = "Friday";
	                break;

	            case 6:
	                dayOfWeek = "Saturday";
	                break;
	        }

	        return dayOfWeek;
	    }
	 
	 
	 
	 private double funGetFreeQty(double buyQty, double getQty, double totalQty)
	    {
	        double freeQty = 0;
	        
	        double totalBuyGetQty = buyQty + getQty;
	        
	        if(buyQty<getQty)
	        {
	            int count = (int) (totalQty / totalBuyGetQty);
	            freeQty=getQty*count;
	        }
	        else
	        {
	            int counter = (int) (totalQty / totalBuyGetQty);
	            for (int cnt = 0; cnt < counter; cnt++)
	            {
	                freeQty++;
	            }
	        }
	        return freeQty;
	    }



	 
	 
	 
	 private double funGetFreeQty(double buyQty, double getQty, double totalBuyQty, double totalGetQty)
	    {
	        double freeQty = 0;
	        
	        while(totalGetQty>0)
	        {
	            if(totalBuyQty==0)
	                break;
	            
	            totalGetQty=totalGetQty-getQty;
	            totalBuyQty=totalBuyQty-buyQty;
	            freeQty=freeQty+getQty;
	        }
	        return freeQty;
	    }
	 
	 
	 
	 private boolean funCheckDayForPromotion(String days, String promoCode, String operationTime, String opDate) throws Exception
	    {
		    cmsCon = objDb.funOpenAPOSCon("mysql", "master");
	    	st = cmsCon.createStatement();
	    	Statement st1 = cmsCon.createStatement();
	        boolean flgDays = false;
	        Calendar calendar = Calendar.getInstance();
	        Date date = calendar.getTime();
	        String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

	        String sql = "select * from tblpromotiondaytimedtl where strDay='" + currentDay + "' and strPromoCode='" + promoCode + "'";
	        ResultSet rsDayTime =  st.executeQuery(sql);
	        while (rsDayTime.next())
	        {
	            String fromTime = funConvertTime(rsDayTime.getString(3));
	            String toTime = funConvertTime(rsDayTime.getString(4));

	            fromTime = opDate + " " + fromTime;
	            toTime = opDate + " " + toTime;

	            long diff1 = objUtility.funCompareTime(fromTime, operationTime);
	            long diff2 = objUtility.funCompareTime(operationTime, toTime);
	            if (diff1 > 0 && diff2 > 0)
	            {
	                flgDays = true;
	                break;
	            }
	        }
	        rsDayTime.close();
	        return flgDays;
	    }
	 
	 
	 
	 
	 private String funConvertTime(String time)
	    {
	        String[] arrTime = time.split(":");
	        String convertedTime = "";
	        int hr = Integer.parseInt(arrTime[0]);
	        String min = arrTime[1].split(" ")[0];
	        String ampm = arrTime[1].split(" ")[1];

	        if (hr == 12)
	        {
	            if (ampm.equals("AM"))
	            {
	                hr += 12;
	            }
	        }
	        else
	        {
	            if (ampm.equals("PM"))
	            {
	                hr += 12;
	            }
	        }
	        String hours = String.valueOf(hr);
	        if (hr < 10)
	        {
	            hours = "0" + hours;
	        }
	        convertedTime = hours + ":" + min + ":00";
	        return convertedTime;
	    }
	
	
	
	 private static Comparator<clsBuyPromotionItemDtl> COMPARATORBUY = new Comparator<clsBuyPromotionItemDtl>()
	   {
		 // This is where the sorting happens.
		  public int compare(clsBuyPromotionItemDtl o1, clsBuyPromotionItemDtl o2)
			{
			   return (int) (o2.getTotalAmount() - o1.getTotalAmount());
		    }
	  };
    
	 private static Comparator<clsGetPromotionItemDtl> COMPARATORGET = new Comparator<clsGetPromotionItemDtl>()
	  {
	    // This is where the sorting happens.
		  public int compare(clsGetPromotionItemDtl o1, clsGetPromotionItemDtl o2)
			 {
			   return (int) (o1.getTotalAmount() - o2.getTotalAmount());
			 }
	  };
	
	
	/*   
        //if (!flgPromotionOnDiscount)
        if (flgPromotionOnItems && !flgPromotionOnDiscount)
        {
            //Start loop on Buy Promotion items map     
            for (Map.Entry<String, List<clsBuyPromotionItemDtl>> entry : hmBuyPromoItems.entrySet())
            {
                String promoCode = entry.getKey();
                List<clsBuyPromotionItemDtl> listBuyItemDtl = entry.getValue();
                double totalBuyQty = 0;
                for (clsBuyPromotionItemDtl objBuyPromoItemDtl : listBuyItemDtl)
                {
                    double buyQty = objBuyPromoItemDtl.getBuyPromoItemQty();   // Buy Qty defined in buy promotion master.
                    totalBuyQty = objBuyPromoItemDtl.getTotalItemQty();        // Buy Qty defined in buy promotion master.

                    if (objBuyPromoItemDtl.getBuyPromoOn().equals("Item") && objBuyPromoItemDtl.getGetPromoOn().equals("PromoGroup"))
                    {
                        if (null != hmGetPromoItems.get(promoCode))
                        {
                            List<clsGetPromotionItemDtl> listGetItemDtl = hmGetPromoItems.get(promoCode);
                            for (clsGetPromotionItemDtl objGetPromoItemDtl : listGetItemDtl)
                            {
                                clsPromotionItems objPromoItems = new clsPromotionItems();
                                objPromoItems.setItemCode(objGetPromoItemDtl.getItemCode());
                                objPromoItems.setPromoType("ItemWise");
                                objPromoItems.setPromoCode(promoCode);
                                objPromoItems.setDiscType("");
                                objPromoItems.setDiscAmt(0);
                                objPromoItems.setDiscPer(0);
                                objPromoItems.setFreeItemQty(objGetPromoItemDtl.getTotalItemQty());

                                hmPromoItems.put(objGetPromoItemDtl.getItemCode(), objPromoItems);
                            }
                        }
                    }
                    else // For Menuheads
                    {
                        if (null != hmGetPromoItems.get(promoCode))
                        {
                            int freeQty = 0, checkFreeQty = 0, totalFreeQty = 0;
                            List<clsGetPromotionItemDtl> listGetItemDtl = hmGetPromoItems.get(promoCode);
                            if (listGetItemDtl.size() > 0)
                            {
                                clsGetPromotionItemDtl objGetPromoItemDtlTemp = listGetItemDtl.get(0);
                            }
                            for (clsGetPromotionItemDtl objGetPromoItemDtl : listGetItemDtl)
                            {
                                double getQty = objGetPromoItemDtl.getGetPromoItemQty();
                                double promoQty = buyQty + getQty;
                                if (listBillItems.contains(objGetPromoItemDtl.getItemCode()))
                                {
                                    if (getQty > buyQty)
                                    {
                                        int totalGetQty = (int) objGetPromoItemDtl.getTotalItemQty();
                                        for (int cn = 0; cn < totalBuyQty; cn++)
                                        {
                                            if (totalGetQty >= promoQty)
                                            {
                                                freeQty += getQty;
                                                totalGetQty -= getQty;
                                            }
                                            else
                                            {
                                                break;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        freeQty = (int) totalBuyQty / (int) promoQty;
                                    }
                                }
                                else
                                {
                                    if (getQty > buyQty)
                                    {
                                        int totalGetQty = (int) objGetPromoItemDtl.getTotalItemQty();
                                        for (int cn = 0; cn < totalBuyQty; cn++)
                                        {
                                            if (totalGetQty > 0)
                                            {
                                                freeQty += getQty;
                                                totalGetQty -= getQty;
                                            }
                                            else
                                            {
                                                break;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        freeQty = (int) totalBuyQty / (int) buyQty;
                                    }
                                }
                                freeQty = freeQty - totalFreeQty;

                                clsPromotionItems objPromoItems = new clsPromotionItems();
                                objPromoItems.setItemCode(objGetPromoItemDtl.getItemCode());
                                objPromoItems.setPromoType("ItemWise");
                                objPromoItems.setPromoCode(promoCode);
                                objPromoItems.setDiscType("");
                                objPromoItems.setDiscAmt(0);
                                objPromoItems.setDiscPer(0);

                                int itemQty = (int) objGetPromoItemDtl.getTotalItemQty();
                                if (itemQty < freeQty)
                                {
                                    checkFreeQty = freeQty - itemQty;
                                    totalFreeQty = totalFreeQty + itemQty;
                                    objPromoItems.setFreeItemQty(itemQty);
                                }
                                else
                                {
                                    totalFreeQty = totalFreeQty + freeQty;
                                    objPromoItems.setFreeItemQty(freeQty);
                                    checkFreeQty = 0;
                                }
                                if (objPromoItems.getFreeItemQty() > 0)
                                {
                                    hmPromoItems.put(objGetPromoItemDtl.getItemCode(), objPromoItems);
                                }

                                if (checkFreeQty < 1)
                                {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
*/

}
