package com.apos.bean;

import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.apos.service.clsSetupService;

public class clsSettelementOptions {
	
	@Autowired
	public SessionFactory	webPOSSessionFactory;
	
	@Autowired 
	public clsSetupService objSetUpService;


	   public static HashMap<String, clsSettelementOptions> hmSettelementOptionsDtl;
	    public static List<String> listSettelmentOptions;
	    public String strSettelmentCode;
	    public String strSettelmentDesc;
	    public String strSettelmentType;
	    public double dblConvertionRatio;
	    public double dblSettlementAmt;
	    public double dblPaidAmt;
	    public String strExpiryDate;
	    public String strCardName;
	    public String strRemark;
	    public double dblActualAmt;
	    public double dblRefundAmt;
	    public String strGiftVoucherCode;
	    public String strBillPrintOnSettlement;
	    public String strFolioNo;
	    public String strRoomNo;
	    public String strGuestCode;
	    
	    
	    public clsSettelementOptions(String strSettlementCode,double dblSettlementAmt,double dblPaidAmt
	            ,String strExpiryDate,String settleName,String strCardName,String strRemark
	            ,double dblActualAmt,double dblRefundAmt,String strGiftVoucherCode
	            ,String strSettelmentDesc,String strSettelmentType){
	            this.strSettelmentCode=strSettlementCode;
	            this.dblSettlementAmt=dblSettlementAmt;
	            this.dblPaidAmt=dblPaidAmt;
	            this.strExpiryDate=strExpiryDate;
	            this.strSettelmentDesc=settleName;
	            this.strCardName=strCardName;
	            this.strRemark=strRemark;
	            this.dblActualAmt=dblActualAmt;
	            this.dblRefundAmt=dblRefundAmt;
	            this.strGiftVoucherCode=strGiftVoucherCode;
	            this.strSettelmentDesc=strSettelmentDesc;
	            this.strSettelmentType=strSettelmentType;
	    }
	    public String getStrBillPrintOnSettlement() {
	        return strBillPrintOnSettlement;
	    }

	    public void setStrBillPrintOnSettlement(String strBillPrintOnSettlement) {
	        this.strBillPrintOnSettlement = strBillPrintOnSettlement;
	    }
	    
	    public clsSettelementOptions(String strSettelmentCode,String strSettelmentType,double dblConvertionRatio
	            ,String strSettelmentDesc,String strBillPrintOnSettlement){
	            this.strSettelmentCode=strSettelmentCode;
	            this.strSettelmentType=strSettelmentType;
	            this.dblConvertionRatio=dblConvertionRatio;
	            this.strSettelmentDesc=strSettelmentDesc;
	            this.strBillPrintOnSettlement=strBillPrintOnSettlement;
	        }
	    
	    
	
	
//		public void funAddSettelementOptions(String clientCode,String posCode,Boolean superUser) {
//	        hmSettelementOptionsDtl= new HashMap<>();
//	        listSettelmentOptions=new ArrayList<>();
//	      
//	        String sqlSettlementModes = "";
//	        try {
//	        	
//	         JSONObject jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gPickSettlementsFromPOSMaster");
//	         String gPickSettlementsFromPOSMaster=jobjgDirectAreaCode.get("gPickSettlementsFromPOSMaster").toString();
//	              
//	              
//	          jobjgDirectAreaCode= objSetUpService.funGetParameterValuePOSWise(clientCode, posCode, "gEnablePMSIntegrationYN");
//	          String gEnablePMSIntegrationYN=jobjgDirectAreaCode.get("gEnablePMSIntegrationYN").toString();
//	         
//	            if(gPickSettlementsFromPOSMaster.equals("Y"))
//	            {
//	                sqlSettlementModes = "select b.strSettelmentCode,b.strSettelmentDesc,b.strSettelmentType"
//	                    + " ,b.dblConvertionRatio,b.strBillPrintOnSettlement "
//	                    + " from tblpossettlementdtl a,tblsettelmenthd b "
//	                    + " where a.strSettlementCode=b.strSettelmentCode and b.strApplicable='Yes' "
//	                    + " and b.strBilling='Yes' and a.strPOSCode='"+posCode+"'";
//	            }
//	            else
//	            {
//	                sqlSettlementModes = "select strSettelmentCode,strSettelmentDesc,strSettelmentType,dblConvertionRatio"
//	                    + " ,strBillPrintOnSettlement "
//	                    + " from tblsettelmenthd where strApplicable='Yes' and strBilling='Yes'";
//	            }
//	           
//	            
//                Query querySqlSettlement = webPOSSessionFactory.getCurrentSession().createSQLQuery(sqlSettlementModes.toString());
//				List listSettlement = querySqlSettlement.list();
//	           
//	            if(listSettlement.size()>0)
//	            {
//	               for(int i=0;i<listSettlement.size();i++)
//	               {
//	            	 Object[] obj=(Object[]) listSettlement.get(i);
//	            
//	       
//	                if(gEnablePMSIntegrationYN.equals("Y"))
//	                {
//	                    if(superUser)
//	                    {
//	                        listSettelmentOptions.add(obj[1].toString());
//	                        hmSettelementOptionsDtl.put(obj[1].toString()
//	                            ,new clsSettelementOptions(obj[0].toString(),obj[2].toString()
//	                            ,Double.parseDouble(obj[3].toString()),obj[1].toString(),obj[4].toString()));
//	                    }
//	                    else
//	                    {
//	                        listSettelmentOptions.add(obj[1].toString());
//	                            hmSettelementOptionsDtl.put(obj[1].toString()
//	                            ,new clsSettelementOptions(obj[0].toString(),obj[2].toString()
//	                            ,Double.parseDouble(obj[3].toString()),obj[1].toString(),obj[4].toString()));
//	                    }
//	                }
//	                else
//	                {
//	                    listSettelmentOptions.add(obj[1].toString());
//	                    hmSettelementOptionsDtl.put(obj[1].toString()
//	                        ,new clsSettelementOptions(obj[0].toString(),obj[2].toString()
//	                        ,Double.parseDouble(obj[3].toString()),obj[1].toString(),obj[4].toString()));
//	                        
//	               
//	                }                                
//	            }
//	           }
//	  
//	            
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
	    
	    
	    public String getStrSettelmentCode() {
	        return strSettelmentCode;
	    }

	    public void setStrSettelmentCode(String strSettelmentCode) {
	        this.strSettelmentCode = strSettelmentCode;
	    }

	    public String getStrSettelmentDesc() {
	        return strSettelmentDesc;
	    }

	    public void setStrSettelmentDesc(String strSettelmentDesc) {
	        this.strSettelmentDesc = strSettelmentDesc;
	    }

	    public String getStrSettelmentType() {
	        return strSettelmentType;
	    }

	    public void setStrSettelmentType(String strSettelmentType) {
	        this.strSettelmentType = strSettelmentType;
	    }

	    public double getDblConvertionRatio() {
	        return dblConvertionRatio;
	    }

	    public void setDblConvertionRatio(double dblConvertionRatio) {
	        this.dblConvertionRatio = dblConvertionRatio;
	    }

	    public double getDblSettlementAmt() {
	        return dblSettlementAmt;
	    }

	    public void setDblSettlementAmt(double dblSettlementAmt) {
	        this.dblSettlementAmt = dblSettlementAmt;
	    }

	    public double getDblPaidAmt() {
	        return dblPaidAmt;
	    }

	    public void setDblPaidAmt(double dblPaidAmt) {
	        this.dblPaidAmt = dblPaidAmt;
	    }

	    public String getStrExpiryDate() {
	        return strExpiryDate;
	    }

	    public void setStrExpiryDate(String strExpiryDate) {
	        this.strExpiryDate = strExpiryDate;
	    }

	    public String getStrCardName() {
	        return strCardName;
	    }

	    public void setStrCardName(String strCardName) {
	        this.strCardName = strCardName;
	    }

	    public String getStrRemark() {
	        return strRemark;
	    }

	    public void setStrRemark(String strRemark) {
	        this.strRemark = strRemark;
	    }

	    public double getDblActualAmt() {
	        return dblActualAmt;
	    }

	    public void setDblActualAmt(double dblActualAmt) {
	        this.dblActualAmt = dblActualAmt;
	    }

	    public double getDblRefundAmt() {
	        return dblRefundAmt;
	    }

	    public void setDblRefundAmt(double dblRefundAmt) {
	        this.dblRefundAmt = dblRefundAmt;
	    }

	    public String getStrGiftVoucherCode() {
	        return strGiftVoucherCode;
	    }

	    public void setStrGiftVoucherCode(String strGiftVoucherCode) {
	        this.strGiftVoucherCode = strGiftVoucherCode;
	    }

	    public String getStrFolioNo()
	    {
	        return strFolioNo;
	    }

	    public void setStrFolioNo(String strFolioNo)
	    {
	        this.strFolioNo = strFolioNo;
	    }

	    public String getStrRoomNo()
	    {
	        return strRoomNo;
	    }

	    public void setStrRoomNo(String strRoomNo)
	    {
	        this.strRoomNo = strRoomNo;
	    }

	    public String getStrGuestCode()
	    {
	        return strGuestCode;
	    }

	    public void setStrGuestCode(String strGuestCode)
	    {
	        this.strGuestCode = strGuestCode;
	    }

	            
	            



}
