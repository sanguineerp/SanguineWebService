package com.webbooks.bean;

import java.math.BigDecimal;


public class clsProfitLossReportBean
{
  private String dteFromDate;
  private String dteToDate;
  private String strVouchNo;
  private BigDecimal dblAmt;
  private String strNarration;
  private String strAccountName;
  private String strCurrency;
  private double dblPurAmt;
  private double dblSaleAmt;
  private String strDocType;
  
  public clsProfitLossReportBean() {}
  
  public String getStrCurrency()
  {
    return strCurrency;
  }
  
  public void setStrCurrency(String strCurrency) {
    this.strCurrency = strCurrency;
  }
  
  public String getDteFromDate() {
    return dteFromDate;
  }
  
  public void setDteFromDate(String dteFromDate) {
    this.dteFromDate = dteFromDate;
  }
  
  public String getDteToDate() {
    return dteToDate;
  }
  
  public void setDteToDate(String dteToDate) {
    this.dteToDate = dteToDate;
  }
  
  public String getStrVouchNo() {
    return strVouchNo;
  }
  
  public void setStrVouchNo(String strVouchNo) {
    this.strVouchNo = strVouchNo;
  }
  

  public String getStrNarration()
  {
    return strNarration;
  }
  
  public void setStrNarration(String strNarration) {
    this.strNarration = strNarration;
  }
  
  public String getStrAccountName() {
    return strAccountName;
  }
  
  public void setStrAccountName(String strAccountName) {
    this.strAccountName = strAccountName;
  }
  
  public double getDblPurAmt() {
    return dblPurAmt;
  }
  
  public void setDblPurAmt(double dblPurAmt) {
    this.dblPurAmt = dblPurAmt;
  }
  
  public double getDblSaleAmt() {
    return dblSaleAmt;
  }
  
  public void setDblSaleAmt(double dblSaleAmt) {
    this.dblSaleAmt = dblSaleAmt;
  }
  
  public BigDecimal getDblAmt() {
    return dblAmt;
  }
  
  public void setDblAmt(BigDecimal dblAmt) {
    this.dblAmt = dblAmt;
  }
  
  public String getStrDocType() {
    return strDocType;
  }
  
  public void setStrDocType(String strDocType) {
    this.strDocType = strDocType;
  }
}