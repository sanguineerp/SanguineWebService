package com.apos.dao;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.apos.bean.clsBillDiscountDtl;
import com.apos.bean.clsBillDtl;
import com.apos.bean.clsPOSItemDetailFrTaxBean;
import com.apos.bean.clsSettelementOptions;
import com.apos.controller.clsUtilityController;
import com.apos.service.clsSetupService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webservice.util.clsBillItemDtl;
import com.webservice.util.clsBuyPromotionItemDtl;
import com.webservice.util.clsDirectBillerItemDtl;
import com.webservice.util.clsGetPromotionItemDtl;
import com.webservice.util.clsItemDtlForTax;
import com.webservice.util.clsPromotionItems;
import com.webservice.util.clsTaxCalculationDtls;


@Repository("clsBillSettlementDao")
@Transactional(value = "webPOSTransactionManager")
public class clsBillSettlementDao {

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Autowired 
	private clsSetupService objSetUpService;
	
	@Autowired
	private clsUtilityController objUtility;
	
	
	public clsSettelementOptions objSettelementOptions;
	
	
	
	//Variable Declaration
	
	  private String voucherNo, amountBox, finalAmount, discountRemarks;
	    private String textValue1, textValue2;
	    boolean printstatus, settleMode;
	    private String discountType;
	    private BigDecimal btnVal, tempVal, billAmount;
//	    private Point PointCash, PointCheque;
	    boolean dyn1, flgEnterBtnPressed, flgUpdateBillTableForDiscount;
	    private String settlementName;
	    private int paxNo;
	    private String settleName, settleType, strButtonClicked = "Print", billPrintOnSettlement;
	    private String tableNo, waiterNo;
	    private boolean flgMakeKot, flgMakeBill, flgUnsettledBills;
	    private double dblTotalTaxAmt, currencyRate;
	    private boolean flgGiftVoucherOK;
	    private static String debitCardNo;
	    private String selectedReasonCode;
	    public static String customerCodeForCredit;
	    private Vector vModifyReasonCode, vModifyReasonName, vComplReasonCode, vComplReasonName, vReasonCodeForDiscount, vReasonNameForDiscount;
	    private ArrayList<String> listItemCode;
	    private ArrayList<String> listSubGroupCode;
	    private ArrayList<String> listSubGroupName;
	    private ArrayList<String> listGroupName;
	    private ArrayList<String> listGroupCode;
	    private HashMap<String, String> hmItemList;
	    private double cmsMemberBalance = 0;
	    private String billType, billTypeForTax;
	    private int disableNext;
	    private String custCode, delPersonCode;
	   private Map<String, clsBillItemDtl> hmBillItemDtl = new HashMap<String, clsBillItemDtl>();
	    private List<clsDirectBillerItemDtl> objListDirectBillerItemDtl = null, objListItemDtlTemp = null;//Used for Direct Biller ONLY
//	    private int noOfSettlementMode = clsSettelementOptions.listSettelmentOptions.size();
	    private HashMap<String, clsSettelementOptions> hmSettlemetnOptions = new HashMap<>();
	    private double dblDiscountAmt = 0.00;
	    private double dblDiscountPer = 0.00;
	    private double dblSettlementAmount = 0.00;
	    private double _paidAmount = 0.00, tipAmount = 0;
	    private double _subTotal = 0.00;
	    private double _netAmount = 0.00;
	    private double _grandTotal = 0.00;
	    private double _balanceAmount = 0.00;
	    private double _refundAmount = 0.00;
	    private String _giftVoucherCode = "", custMobileNoForCRM;
	    private String _giftVoucherSeriesCode = "", advOrderBookingNo = "", couponCode = "";
	    private int _settlementNavigate;
	    private double _deliveryCharge = 0.00, _loyalityPoints = 0.00;
	    private String dtPOSDate, homeDelivery, areaCode, operationTypeForTax, takeAway, callingFormName = "", cmsMemberName;
	    //private ArrayList<ArrayList<Object>> arrListTaxCal;
	    private List<clsTaxCalculationDtls> arrListTaxCal;
	    private boolean flagAddKOTstoBill = false;
	    private Map<String, clsPromotionItems> hmPromoItem;
	    private Map<String, Double> hmAddKOTItems;
//	    private frmMakeKOT kotObj = null, objMakeKOT = null;
//	    private frmMakeBill makeBillObj = null;
//	    private frmDirectBiller objDirectBiller = null;
//	    private panelShowBills objPannelShowBills = null;
//	    private panelShowKOTs objPannelShowKOTs = null;
//	    private frmAddKOTToBill objAddKOTToBill;
//	    private clsCustomerDataModelForSQY obj;
	   /* private List<String> listBillFromKOT = null;
	    //private Map<String, clsPromotionDtl> hmBuyPromoItemDtl;
	    private String takeAwayRemarks, custAddType;*/
	
	    private double cmsMemberCreditLimit;
	    private String cmsStopCredit;
	    Map<String, clsBillDiscountDtl> mapBillDiscDtl = new HashMap<String, clsBillDiscountDtl>();
	   /* private HashMap<String, clsBillItemDtl> mapPromoItemDisc;
	    private boolean isDirectSettleFromMakeBill = false;
	    private Map<String, List<clsBillItemDtl>> hmBillSeriesItemList;
//	    private List<clsBillSeriesBillDtl> listBillSeriesBillDtl;*/
	    private String settlementCode = "";
	    private  int noOfSettlementMode ;
	    List listSettelment;

		//public static HashMap<String, clsSettelementOptions> hmSettelementOptionsDtl;
	    JSONObject jsSettelementOptionsDtl=new JSONObject();
	    List listSettlementObject=new ArrayList<clsSettelementOptions>();
		public static List<String> listSettelmentOptions;
	    
	
		Vector vTableNo;
	 public JSONObject funFillUnsettleBill(String clientCode,String posCode,String posDate)
	    {
		 JSONObject jobjReturn=new JSONObject();
		 JSONArray jArr=new JSONArray();
	        try
	        {
	            vTableNo = new Vector();
	            String sql="";
	            
	           JSONObject jobj= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType");
	           String gShowBillsType=jobj.get("gShowBillsType").toString();
	           JSONObject jobjgCMSIntegrationYN= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");
	           String gCMSIntegrationYN=jobjgCMSIntegrationYN.get("gCMSIntegrationYN").toString();
	           
	           jobjReturn.put("gShowBillsType",gShowBillsType);
	           jobjReturn.put("gCMSIntegrationYN",gCMSIntegrationYN);
	           
	           
	           
	           
	            if(gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
	            {
	                sql = "select a.strBillNo,ifnull(b.strTableNo,''),ifnull(b.strTableName,''),ifnull(c.strWaiterNo,'')"
	                    + " ,ifnull(c.strWShortName,''),ifnull(d.strCustomerCode,''),ifnull(d.strCustomerName,''),a.dblGrandTotal"
	                    + " ,DATE_FORMAT(a.dteBillDate,'%h:%i:%s')  "
	                    + " from tblbillhd a left outer join tbltablemaster b on a.strTableNo=b.strTableNo"
	                    + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo"
	                    + " left outer join tblcustomermaster d on a.strCustomerCode=d.strCustomerCode"
	                    + " where a.strBillNo not in (select strBillNo from tblbillsettlementdtl) "
	                    + " and date(a.dteBillDate)='" + posDate + "' "
	                    + " and a.strPOSCode='" + posCode + "' ";
	            }
	            else//Delivery Detail Wise
	            {
	                sql="SELECT a.strBillNo,IFNULL(d.strCustomerName,''),ifnull(e.strBuildingName,''),ifnull(f.strDPName,'')"
	                    + " ,a.dblGrandTotal,ifnull(g.strTableNo,''),ifnull(g.strTableName,''),DATE_FORMAT(a.dteBillDate,'%h:%i:%s') "
	                    + " FROM tblbillhd a "
	                    + " left outer join tblhomedeldtl b on a.strBillNo=b.strBillNo "
	                    + " LEFT OUTER JOIN tblcustomermaster d ON a.strCustomerCode=d.strCustomerCode "
	                    + " left outer join tblbuildingmaster e on d.strBuldingCode=e.strBuildingCode "
	                    + " left outer join tbldeliverypersonmaster  f on  f.strDPCode=b.strDPCode "
	                    + " left outer join tbltablemaster g on a.strTableNo=g.strTableNo "
	                    + " WHERE a.strBillNo NOT IN (SELECT strBillNo FROM tblbillsettlementdtl) "
	                    + " AND DATE(a.dteBillDate)='" +posDate +"' "
	                    + " AND a.strPOSCode='" +posCode+ "' "
	                    + " group by a.strBillNo";
	            }
	            Query querySql = webPOSSessionFactory.getCurrentSession()
						.createSQLQuery(sql.toString());
				List listPendBillData = querySql.list();
	           
	            if(listPendBillData.size()>0)
	            {
	             for(int i=0;i<listPendBillData.size();i++)
	             {
	            	 Object []obj=(Object[]) listPendBillData.get(i);
	             	
	            	 JSONObject jObj =new JSONObject();
	            	 
	            
	                if(gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
	                {
	                   
	                    jObj.put("strBillNo", obj[0].toString());
	                    jObj.put("strTableName", obj[2].toString());
	                    jObj.put("strWShortName", obj[4].toString());
	                    jObj.put("strCustomerName", obj[6].toString());
	                    jObj.put("dteBillDate", obj[8].toString());
	                    jObj.put("dblGrandTotal", obj[7].toString());
	                    
//	                    dmBills.addRow(ob);
	                    vTableNo.add(obj[1].toString());
	                }
	                else//Delivery Detail Wise
	                {
	                   
	                    jObj.put("strBillNo", obj[0].toString());
	                    jObj.put("strTableName", obj[6].toString());
	                    jObj.put("strCustomerName", obj[1].toString());
	                    jObj.put("strBuildingName", obj[2].toString());
	                    jObj.put("strDPName", obj[3].toString());
	                    jObj.put("dteBillDate", obj[7].toString());
	                    jObj.put("dblGrandTotal", obj[4].toString());
	  
	                    vTableNo.add(obj[5].toString());
	                }
	                jArr.put(jObj);
	            }
	        }
	          
	          jobjReturn.put("jArr",jArr);  
	          
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jobjReturn;
	    }
	 
	 
	 /////////////For Getting Settlement Modes
	 
	 public JSONObject funSettlementMode(String clientCode,String posCode,Boolean superUser)
	 {
		 JSONObject jsonOb=new JSONObject();
			
		 try{
			 listSettelment=new ArrayList();
	         funAddSettelementOptions( clientCode, posCode, superUser);
			 noOfSettlementMode = listSettelmentOptions.size();
			 JSONArray jArrSettlMod=new JSONArray();
			 for(int i=0;i<noOfSettlementMode;i++)
			 {
			     listSettelment.add(listSettelmentOptions.get(i));
			     jArrSettlMod.put(listSettelmentOptions.get(i));
			 }
			 jsonOb.put("SettleDesc", jArrSettlMod);
			 jsonOb.put("SettleObj",jsSettelementOptionsDtl);
			
	 	    Gson gson = new Gson();
	 	    Type type = new TypeToken<List<clsSettelementOptions>>() {}.getType();
            String gsonlistSettlementObject = gson.toJson(listSettlementObject, type);
            jsonOb.put("listSettleObj",gsonlistSettlementObject);
		 	
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		  
		 return jsonOb;
	 }
	 
		public void funAddSettelementOptions(String clientCode,String posCode,Boolean superUser) {
	      //  hmSettelementOptionsDtl= new HashMap<>();
	        listSettelmentOptions=new ArrayList<>();
	        Gson gson = new Gson();
	 	    Type type = new TypeToken<clsSettelementOptions>() {}.getType();
           
	        String sqlSettlementModes = "";
	        try {
	        	
	         JSONObject jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gPickSettlementsFromPOSMaster");
	         String gPickSettlementsFromPOSMaster=jobjgDirectAreaCode.get("gPickSettlementsFromPOSMaster").toString();
	              
	              
	          jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gEnablePMSIntegrationYN");
	          String gEnablePMSIntegrationYN=jobjgDirectAreaCode.get("gEnablePMSIntegrationYN").toString();
	         
	            if(gPickSettlementsFromPOSMaster.equals("Y"))
	            {
	                sqlSettlementModes = "select b.strSettelmentCode,b.strSettelmentDesc,b.strSettelmentType"
	                    + " ,b.dblConvertionRatio,b.strBillPrintOnSettlement "
	                    + " from tblpossettlementdtl a,tblsettelmenthd b "
	                    + " where a.strSettlementCode=b.strSettelmentCode and b.strApplicable='Yes' "
	                    + " and b.strBilling='Yes' and a.strPOSCode='"+posCode+"'";
	            }
	            else
	            {
	                sqlSettlementModes = "select strSettelmentCode,strSettelmentDesc,strSettelmentType,dblConvertionRatio"
	                    + " ,strBillPrintOnSettlement "
	                    + " from tblsettelmenthd where strApplicable='Yes' and strBilling='Yes'";
	            }
	           
	            
                Query querySqlSettlement = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlSettlementModes.toString());
				List listSettlement = querySqlSettlement.list();
				clsSettelementOptions objSettl;
				String gsonSettlementObject="";
	            if(listSettlement.size()>0)
	            {
	            	listSettlementObject=new ArrayList<clsSettelementOptions>();
	               for(int i=0;i<listSettlement.size();i++)
	               {
	            	 Object[] obj=(Object[]) listSettlement.get(i);
	                List listSttleData=new ArrayList();
	       
	                if(gEnablePMSIntegrationYN.equals("Y"))
	                {
	                    if(superUser)
	                    {
	                        listSettelmentOptions.add(obj[1].toString());
	                        objSettl=new clsSettelementOptions(obj[0].toString(),obj[2].toString()
		                            ,Double.parseDouble(obj[3].toString()),obj[1].toString(),obj[4].toString());
	                        listSettlementObject.add(objSettl);
	                        gsonSettlementObject = gson.toJson(objSettl, type);
	                        jsSettelementOptionsDtl.put(obj[1].toString(),gsonSettlementObject);
	                    }
	                    else
	                    {
	                        listSettelmentOptions.add(obj[1].toString());
	                        objSettl=new clsSettelementOptions(obj[0].toString(),obj[2].toString()
		                            ,Double.parseDouble(obj[3].toString()),obj[1].toString(),obj[4].toString());
	                        listSettlementObject.add(objSettl);
	                        gsonSettlementObject = gson.toJson(objSettl, type);
	                        jsSettelementOptionsDtl.put(obj[1].toString(),gsonSettlementObject);
	                    }
	                }
	                else
	                {
	                    listSettelmentOptions.add(obj[1].toString());
	                    objSettl=new clsSettelementOptions(obj[0].toString(),obj[2].toString()
		                        ,Double.parseDouble(obj[3].toString()),obj[1].toString(),obj[4].toString());
	                  
	                    listSettlementObject.add(objSettl);
                        gsonSettlementObject = gson.toJson(objSettl, type);
                        jsSettelementOptionsDtl.put(obj[1].toString(),gsonSettlementObject);
	                }                                
	            }
	           }
	  
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 
	    public JSONObject funTableRowClicked(int rowCount,String clientCode,String posCode,String billNo,String tableNo,String billType,boolean superUser)
	    {
	    	
	    	JSONObject jObjReturn= new JSONObject();
	        try
	        {
//	        	   JSONArray jArrSettlementMode=funSettlementMode(clientCode, posCode, superUser);
	        	   
	        	   
	        	   funAddSettelementOptions( clientCode, posCode, superUser);
	      		   noOfSettlementMode = listSettelmentOptions.size();
	        	 
	        	   jObjReturn.put("jArrSettlementMode", listSettelmentOptions);
	        	   
	        	   
	        	   
	               JSONObject jobj= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType");
		           String gShowBillsType=jobj.get("gShowBillsType").toString();
		           JSONObject jobjgCMSIntegrationYN= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");
		           String gCMSIntegrationYN=jobjgCMSIntegrationYN.get("gCMSIntegrationYN").toString();
		           String areaCode="";
	              if (rowCount > 0)
	              {               
	                if(gShowBillsType.equalsIgnoreCase("Table Detail Wise"))
	                {
	                    int selectedRow = rowCount;
	                  
//	                    clsGlobalVarClass.funCheckHomeDelivery(billNo);
	                    int row = rowCount-1;
	                    if (tableNo.trim().length() == 0)
	                    {
	                        billType = "Direct Biller";
	                        JSONObject jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType");
	     		             areaCode=jobjgDirectAreaCode.get("gShowBillsType").toString();
	                    }
	                    else
	                    {
	                        billType = "DineIn";
	                        String sql = "select strAreaCode from tbltablemaster where strTableNo='" + vTableNo.elementAt(row).toString() + "'";
	                        Query querySql = webPOSSessionFactory.getCurrentSession()
	        						.createSQLQuery(sql.toString());
	        				List listAreaCode = querySql.list();
	        	           
	        	            if(listAreaCode.size()>0)
	        	            {
	        	            
	        	            	 Object obj=(Object) listAreaCode.get(0);
	        	            
	                            areaCode = obj.toString();
	                        }
	                        
	                    }
	                    
	                    setBillData(billNo, billType, areaCode,clientCode,posCode,superUser);
//	                    obj.setBillData(billNo, billType, areaCode);
	                    
	                    funLoadOldDiscount(billNo,clientCode,posCode);
	                 
	                }
	                else//Delivery Detail Wise
	                {
	                    int selectedRow = rowCount;
//	                    String billNo = tblBills.getValueAt(selectedRow, 0).toString();
//	                    String tableNo = tblBills.getValueAt(selectedRow, 1).toString();
//	                    String billType ="";
	               /////////////////     clsGlobalVarClass.funCheckHomeDelivery(billNo);
	                    int row = rowCount;
	                    if (tableNo.trim().length() == 0)
	                    {
	                        billType = "Direct Biller"; 
	                        JSONObject jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gShowBillsType");
	     		             areaCode=jobjgDirectAreaCode.get("gShowBillsType").toString();
	                    }
	                    else
	                    {
	                        billType = "DineIn";
	                        String sql = "select strAreaCode from tbltablemaster where strTableNo='" + vTableNo.elementAt(row).toString() + "'";
	                        Query querySql = webPOSSessionFactory.getCurrentSession()
	        						.createSQLQuery(sql.toString());
	        				List listAreaCode = querySql.list();
	        	           
	        	            if(listAreaCode.size()>0)
	        	            {
	        	            
	        	            	 Object obj=(Object) listAreaCode.get(0);
	        	            
	                            areaCode = obj.toString();
	                        }
	                    }
//	                    obj.setBillData(billNo, billType, areaCode);
	                    
	                  //////////  funLoadOldDiscount(billNo,clientCode,posCode,superUser);
	                }                    
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return jObjReturn;
	    }

	    
	    
	    private void funLoadOldDiscount(String billNo,String clientCode,String posCode)
	    {
	        try
	        {
	        	String areaCode="";
	            JSONObject jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gTransactionType");
	            String gTransactionType=jobjgDirectAreaCode.get("gTransactionType").toString();
	            if(gTransactionType.equalsIgnoreCase("ModifyBill"))
	            {
	                String sqlDisc="select CONCAT(a.strDiscOnType,'!',a.strDiscOnValue) as mapKey,a.strDiscRemarks"
	                    + " ,a.strDiscReasonCode,a.dblDiscPer,a.dblDiscAmt,a.dblDiscOnAmt "
	                    + " from tblbilldiscdtl a "
	                    + " where a.strBillNo='"+billNo+"' ";
	             
	                Query querySql = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlDisc.toString());
    				List listDisc = querySql.list();
    	           
    	            if(listDisc.size()>0)
    	            {
    	               for(int i=0;i<listDisc.size();i++)
    	               {
    	            	 Object[] obj=(Object[]) listDisc.get(i);
	                    clsBillDiscountDtl billDiscountDtl=new clsBillDiscountDtl(obj[1].toString(),obj[2].toString(), Double.parseDouble(obj[3].toString()), Double.parseDouble(obj[4].toString()), Double.parseDouble(obj[5].toString()));
	                   ////// obj.mapBillDiscDtl.put(resultSet.getString("mapKey"),billDiscountDtl);
	                }
    	            }
	         
	            }
	        }
	        catch(Exception e)
	        {
	            
	            e.printStackTrace();
	        }
	    }
	    
	   
	    public void setBillData(String BillNo, String billFrom, String areaCode,String clientCode,String posCode,boolean superUser)
	    {
	        try
	        {

	           

	            settleMode = false;
	           
	            clsSettelementOptions objSettlementOptions = (clsSettelementOptions) jsSettelementOptionsDtl.get(listSettelmentOptions.get(0).toString());
	            settleType = objSettlementOptions.getStrSettelmentType();
	            settlementCode = objSettlementOptions.getStrSettelmentCode();//use while calculating tax for settlement

	            _paidAmount = 0.00;

//	            txtAreaRemark.setText("");
	            hmSettlemetnOptions.clear();
	            this.areaCode = areaCode;
	            operationTypeForTax = "DineIn";

	            this.billType = billType;
	            
	            JSONObject jobj= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gDirectAreaCode");
		        String gDirectAreaCode=jobj.get("gDirectAreaCode").toString();
	            if (billFrom.equals("Direct Biller"))
	            {
	                this.billType = "Direct Biller";
	                this.areaCode = gDirectAreaCode;
	            }
	            else
	            {
	                this.billType = "Make KOT";
	            }
	            voucherNo = BillNo;
//	            lblVoucherNo.setText(BillNo);//////////////////////////////////////////

	            String sql_CustCode = "select strCustomerCode,strRemarks from tblbillhd "
	                    + "where strBillNo='" + BillNo + "'";
	         
	           
	            Query querySqlCustCode = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_CustCode.toString());
				List listCustCode= querySqlCustCode.list();
	           
	            if(listCustCode.size()>0)
	            {
	               
	            	Object[] obj=(Object[]) listCustCode.get(0);
	                custCode = obj[0].toString();
//	                txtAreaRemark.setText(rsCustCode.getString(2));///////////////////////
	            }
	         

	            String sqlHomeDel = "select count(strBillNo) from tblhomedelivery "
	                    + "where strBillNo='" + BillNo + "'";
	            Query querySqlHomeDel = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlHomeDel.toString());
				List listHomeDel= querySqlHomeDel.list();
	           
	            if(listHomeDel.size()>0)
	            {
	            	Object obj=(Object) listHomeDel.get(0);
	                if (Integer.parseInt(obj.toString()) > 0)
	                {
	                    operationTypeForTax = "HomeDelivery";
	                }
	            }
	           

	            String sql_TakeAway = "select strOperationType from tblbillhd "
	                    + "where strBillNo='" + BillNo + "'";
	     

	            Query querySqlTakeAway= webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_TakeAway.toString());
				List listTakeAway= querySqlTakeAway.list();
	           
	            if(listTakeAway.size()>0)
	            {
	            	Object obj=(Object) listTakeAway.get(0); 
	          
	                if (obj.toString().equals("Take Away"))
	                {
	                    operationTypeForTax = "TakeAway";
	                }
	            }
	        

	            if ("Direct Biller".equalsIgnoreCase(billFrom))
	            {
//	                funFillGridForAdvOrderAndDirectBillerBills("Unsettled Bills", BillNo,posCode);
	            }
	            else
	            {
//	                funSetTableNameVisible(true);/////////////////////////////////////
	                String sql1 = "select ifnull(a.strTableNo,''),ifnull(b.strTableName,'') "
	                        + "from tblbillhd a,tbltablemaster b "
	                        + "where a.strTableNo=b.strTableNo and a.strBillNo='" + BillNo + "'";
	         
	                Query querySqlBill = webPOSSessionFactory.getCurrentSession().createSQLQuery(sql1.toString());
					List listBill= querySqlBill.list();
		           
		            if(listBill.size()>0)
		            {
		            	Object[] obj=(Object[]) listBill.get(0);
	                
	                    String tbno = obj[0].toString();
	                    tableNo = tbno;
//	                    lblTableNo.setText(rsBill.getString(2));//////////////////////////////
	                }
	                
//	                funFillGridForMakeKOTTransaction("", false, "Unsettled Bills", BillNo,posCode,posCode);
	            }
	            JSONObject jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gTransactionType");
	            String gTransactionType=jobjgDirectAreaCode.get("gTransactionType").toString();
	            if ("ModifyBill".equalsIgnoreCase(gTransactionType))
	            {
//	                btnPrint.setVisible(true);
//	                btnSettle.setVisible(false);
//	                funDisableSettelementButtons();
//	                funShowDiscountPannel(true);
	            }
	            else if ("SettleBill".equalsIgnoreCase(gTransactionType) || "AddKOTToBill".equalsIgnoreCase(gTransactionType))
	            {
//	                funShowDiscountPannel(false);
	            	
	              ////////  clsSettelementOptions objSettlementList = clsSettelementOptions.hmSettelementOptionsDtl.get(strSettlementMode);
	               ///////// if (!objSettlementList.getStrSettelmentType().equals("Debit Card"))
	                {
//	                    procSettlementBtnClick(objSettlementList);
	                }
//	                btnPrint.setVisible(false);
//	                btnSettle.setVisible(true);
//	                txtPaidAmt.requestFocus();
//	                txtPaidAmt.selectAll();
	            }
//	            lblTipAmount.setVisible(false);
//	            txtTip.setVisible(false);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
		
	    public JSONObject funGetSettleButtons(String posCode,String userCode,String clientCode) 
		 {
	    	JSONObject jsonOb=new JSONObject();
	    	try{
	    		 Boolean user=objUtility.funCheckSuperUser(userCode);
	    		 jsonOb=funSettlementMode(clientCode,posCode,user);
				 
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	 
			 return jsonOb;
		 }
	    
  
	   /**
	    * @author V!NAYAK  
	    * 
	    * below code to get data settlement window
	    * 
	    * for fill combo's on UI
	    * */
	    public JSONObject funFillGroupSubGroupList(ArrayList<String> arrListItemCode)
	    {
	    	JSONObject jsonAllListsData=new JSONObject();
	        StringBuilder sb = new StringBuilder();
	        try
	        {
	            List listSubGroupName = new ArrayList<>();
	            List listSubGroupCode = new ArrayList<>();
	            List listGroupName = new ArrayList<>();
	            List listGroupCode = new ArrayList<>();
	            listSubGroupName.add("--select--");
	            listSubGroupCode.add("--select--");
	            listGroupName.add("--select--");
	            listGroupCode.add("--select--");
	            boolean first = true;
	            for (String test : arrListItemCode)
	            {
	                if (first)
	                {
	                    sb.append("'").append(test).append("");
	                    first = false;
	                }
	                else
	                {
	                    sb.append("','").append(test).append("");
	                }
	            }
	            //String t1 = sb.toString()+"'";
	            sb.append("'");
	            if (sb.toString().trim().length() > 1)
	            {
	                String sql_List = "select a.strSubGroupCode,b.strSubGroupName,c.strGroupCode,c.strGroupName "
	                        + " from tblitemmaster a , tblsubgrouphd b,tblgrouphd c"
	                        + " where a.strItemCode IN (" + sb.toString() + ") and a.strDiscountApply='Y' "
	                        + " and a.strSubGroupCode=b.strSubGroupCode and b.strGroupCode=c.strGroupCode;";

	                List list=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_List).list();
	                if(list.size()>0){
	                	for(int i=0;i<list.size();i++){
	                		Object[] ob=(Object[])list.get(i);
	                		 if (!listSubGroupCode.contains(ob[0].toString()))
	 	                    {
	 	                        listSubGroupCode.add(ob[0].toString());
	 	                        listSubGroupName.add(ob[1].toString());
	 	                    }
	 	                    if (!listGroupCode.contains(ob[2].toString()))
	 	                    {
	 	                        listGroupCode.add(ob[2].toString());
	 	                        listGroupName.add(ob[3].toString());
	 	                    }
	                	}
	                }
	              
	            }
	           
				Gson gson = new Gson();
		 	    Type type = new TypeToken<List<String>>() {}.getType();
	            String strSubGroupCode = gson.toJson(listSubGroupCode, type);
	            String strSubGroupName = gson.toJson(listSubGroupName, type);
	            String strGroupCode = gson.toJson(listGroupCode, type);
	            String strGroupName = gson.toJson(listGroupName, type);
	            
	            jsonAllListsData.put("listSubGroupCode", strSubGroupCode);
	            jsonAllListsData.put("listSubGroupName", strSubGroupName);
	            jsonAllListsData.put("listGroupCode", strGroupCode);
	            jsonAllListsData.put("listGroupName", strGroupName);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            sb = null;
	        }
	        return jsonAllListsData;
	    }

	    
	    public JSONObject funCheckPointsAgainstCustomer(String clientCode,String posCode,String gCRMInterface,String customerMobile,
	    		 String voucherNo,String txtPaidAmt)
	 {
	    	JSONObject jsonCheckPoints=new JSONObject();
	        
	    	 boolean flgResult = false;
	         double totalLoyalityPoints = 0.00, totalReedemedPoints = 0.00;
	         try
	         {
	             if (gCRMInterface.equalsIgnoreCase("PMAM"))
	             {
	             }
	             else
	             {
	                 String mobileNo = "";
	                 if (null != customerMobile)
	                 {
	                     if (customerMobile.length() > 0)
	                     {
	                         mobileNo = customerMobile;
	                     }
	                 }
	                 else
	                 {
	                     String sql_Bill = "select b.longMobileNo from tblbillhd a,tblcustomermaster b "
	                             + "where a.strCustomerCode=b.strCustomerCode and a.strBillNo='" + voucherNo + "'";
	                    
	                    List list=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Bill).list();
	 	                if(list.size()>0){
	 	                		Object ob=(Object)list.get(0);
	 	                		mobileNo = ob.toString();
	 	                }
	                     
	                 }
	                 if (mobileNo.trim().length() > 0)
	                 {
	                     String sql_Points = "select sum(dblPoints),sum(dblRedeemedAmt) "
	                             + "from tblcrmpoints "
	                             + "where longCustMobileNo='" + mobileNo + "' and strClientCode='"+clientCode+"' "
	                             + "group by longCustMobileNo";
	                     	List list=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql_Points).list();
		 	                if(list.size()>0){
		 	                	for(int i=0;i<list.size();i++)
		 	                	{
			                		 Object[] ob=(Object[])list.get(i);
			                		 totalLoyalityPoints = Double.parseDouble(ob[0].toString());
			                         totalReedemedPoints = Double.parseDouble(ob[1].toString());
			                         totalLoyalityPoints = totalLoyalityPoints - totalReedemedPoints;
			                         
			                         jsonCheckPoints.put("totalLoyalityPoints", totalLoyalityPoints);
			                         jsonCheckPoints.put("totalReedemedPoints", totalReedemedPoints);
			                         
			                        /* if (Double.parseDouble(txtPaidAmt) <= totalLoyalityPoints)
			                         {
			                             flgResult = true;
			                             _loyalityPoints = Double.parseDouble(txtPaidAmt);
			                             jsonCheckPoints.put("loyalityPoints", _loyalityPoints);
			                         }
			                         else
			                         {
			                        	 jsonCheckPoints.put("loyalityPoints", _loyalityPoints);
			                             //JOptionPane.showMessageDialog(this, "Your total Loyality points are " + totalLoyalityPoints);
			                         }*/
		 	                	}
		 	                }
	                   
	                 }
	             }
	         }
	         catch (Exception e)
	         {
	             e.printStackTrace();
	         }
	    	
	    	return jsonCheckPoints;
	 }
	    
    public JSONObject funGetDebitCardNo(String clientCode,String posCode,String voucherNo,String tableNo)
	{
    	 String retDebitCardNo = "",retDebitCardString="";
    	 JSONObject jsonretDebitCardNo=new JSONObject();
         try
         {
             String sql = "";
             JSONObject jobjgTransactionType= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gTransactionType");
	          String gTransactionType=jobjgTransactionType.get("gTransactionType").toString();
	            
             if (gTransactionType.equals("SettleBill"))
             {
                 sql = "select a.strCardNo,b.strCardString "
                         + " from tblbillhd a,tbldebitcardmaster b "
                         + " where a.strCardNo=b.strCardNo and a.strBillNo='" + voucherNo.trim()+ "'";
             }
             else
             {
                 sql = "select a.strCardNo,b.strCardString "
                         + " from tblitemrtemp a,tbldebitcardmaster b "
                         + " where a.strCardNo=b.strCardNo and a.strTableNo='" + tableNo + "' "
                         + " group by a.strTableNo ";
             }
             
         	List list=webPOSSessionFactory.getCurrentSession().createSQLQuery(sql).list();
             if(list.size()>0){
             	for(int i=0;i<list.size();i++)
             	{
            		 Object[] ob=(Object[])list.get(i);
            		 retDebitCardNo=ob[0].toString();
            		 retDebitCardString=ob[1].toString();
            		 jsonretDebitCardNo.put("retDebitCardNo", retDebitCardNo);
            		 jsonretDebitCardNo.put("retDebitCardString", retDebitCardString);
             	}
             }
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
         finally
         {
             return jsonretDebitCardNo;
         }
    	
	}
}
