package com.pathfinder.controller;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Record")
@XmlAccessorType (XmlAccessType.FIELD)
public class clsBillHd implements Serializable
{

//	@XmlElement(name="ClientCode")
//	private String strClientCode;
//	
//	@XmlElement(name="POSCode")
//	private String strPOSCode;
	
	@XmlElement(name="POSName")
	private String strPOSName;
	
	@XmlElement(name="BillNo")
	private String strBillNo;
	
	@XmlElement(name="Date")
	private String strBillDate;
	
	@XmlElement(name="Time")
	private String strBillTime;	
	
	@XmlElement(name="SubTotal")
	private double dblSubTotal;
	
	@XmlElement(name="DiscountAmount")
	private double dblDiscountAmt;
	
//	@XmlElement(name="TAXAmount")
//	private double dblTaxAmt;
	
	@XmlElement(name="GrandTotalAmount")
	private double dblGrandTotal;
	
	
	@XmlElementWrapper(name="Taxes")
	@XmlElement(name="Tax")
	private List<clsBillTaxDtl> listBillTaxDtl; 
	
	@XmlElement(name="SettelementMode")
	private String strSettlementMode;
	
	@XmlElement(name="TransactionStatus")
	private String strTransactionStatus;
	
	
	public clsBillHd()
	{
		super();		
	}


	


	/*public String getStrClientCode()
	{
		return strClientCode;
	}


	public void setStrClientCode(String strClientCode)
	{
		this.strClientCode = strClientCode;
	}


	public String getStrPOSCode()
	{
		return strPOSCode;
	}


	public void setStrPOSCode(String strPOSCode)
	{
		this.strPOSCode = strPOSCode;
	}*/


	public String getStrPOSName()
	{
		return strPOSName;
	}


	public void setStrPOSName(String strPOSName)
	{
		this.strPOSName = strPOSName;
	}


	public String getStrBillNo()
	{
		return strBillNo;
	}


	public void setStrBillNo(String strBillNo)
	{
		this.strBillNo = strBillNo;
	}


	public String getStrBillDate()
	{
		return strBillDate;
	}


	public void setStrBillDate(String strBillDate)
	{
		this.strBillDate = strBillDate;
	}


	public String getStrBillTime()
	{
		return strBillTime;
	}


	public void setStrBillTime(String strBillTime)
	{
		this.strBillTime = strBillTime;
	}


	public String getStrSettlementMode()
	{
		return strSettlementMode;
	}


	public void setStrSettlementMode(String strSettlementMode)
	{
		this.strSettlementMode = strSettlementMode;
	}


	public double getDblSubTotal()
	{
		return dblSubTotal;
	}


	public void setDblSubTotal(double dblSubTotal)
	{
		this.dblSubTotal = dblSubTotal;
	}


	public double getDblDiscountAmt()
	{
		return dblDiscountAmt;
	}


	public void setDblDiscountAmt(double dblDiscountAmt)
	{
		this.dblDiscountAmt = dblDiscountAmt;
	}


	/*public double getDblTaxAmt()
	{
		return dblTaxAmt;
	}


	public void setDblTaxAmt(double dblTaxAmt)
	{
		this.dblTaxAmt = dblTaxAmt;
	}*/


	public double getDblGrandTotal()
	{
		return dblGrandTotal;
	}


	public void setDblGrandTotal(double dblGrandTotal)
	{
		this.dblGrandTotal = dblGrandTotal;
	}


	public List<clsBillTaxDtl> getListBillTaxDtl()
	{
		return listBillTaxDtl;
	}


	public void setListBillTaxDtl(List<clsBillTaxDtl> listBillTaxDtl)
	{
		this.listBillTaxDtl = listBillTaxDtl;
	}


	public String getStrTransactionStatus()
	{
		return strTransactionStatus;
	}


	public void setStrTransactionStatus(String strTransactionStatus)
	{
		this.strTransactionStatus = strTransactionStatus;
	}



	
}
