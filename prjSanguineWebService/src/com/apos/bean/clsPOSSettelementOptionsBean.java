package com.apos.bean;

import java.util.HashMap;
import java.util.List;

public class clsPOSSettelementOptionsBean {

	
	  public static HashMap<String, clsPOSSettelementOptionsBean> hmSettelementOptionsDtl;
	    public static List<String> listSettelmentOptions;
	    private String strSettelmentCode;
	    private String strSettelmentDesc;
	    private String strSettelmentType;
	    private double dblConvertionRatio;
	    private double dblSettlementAmt;
	    private double dblPaidAmt;
	    private String strExpiryDate;
	    private String strCardName;
	    private String strRemark;
	    private double dblActualAmt;
	    private double dblRefundAmt;
	    private String strGiftVoucherCode;
	    private String strBillPrintOnSettlement;
	    private String strFolioNo;
	    private String strRoomNo;
	    private String strGuestCode;
	    
	    
		public static HashMap<String, clsPOSSettelementOptionsBean> getHmSettelementOptionsDtl() {
			return hmSettelementOptionsDtl;
		}
		public static void setHmSettelementOptionsDtl(
				HashMap<String, clsPOSSettelementOptionsBean> hmSettelementOptionsDtl) {
			clsPOSSettelementOptionsBean.hmSettelementOptionsDtl = hmSettelementOptionsDtl;
		}
		public static List<String> getListSettelmentOptions() {
			return listSettelmentOptions;
		}
		public static void setListSettelmentOptions(List<String> listSettelmentOptions) {
			clsPOSSettelementOptionsBean.listSettelmentOptions = listSettelmentOptions;
		}
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
		public String getStrBillPrintOnSettlement() {
			return strBillPrintOnSettlement;
		}
		public void setStrBillPrintOnSettlement(String strBillPrintOnSettlement) {
			this.strBillPrintOnSettlement = strBillPrintOnSettlement;
		}
		public String getStrFolioNo() {
			return strFolioNo;
		}
		public void setStrFolioNo(String strFolioNo) {
			this.strFolioNo = strFolioNo;
		}
		public String getStrRoomNo() {
			return strRoomNo;
		}
		public void setStrRoomNo(String strRoomNo) {
			this.strRoomNo = strRoomNo;
		}
		public String getStrGuestCode() {
			return strGuestCode;
		}
		public void setStrGuestCode(String strGuestCode) {
			this.strGuestCode = strGuestCode;
		}
}
