package com.pathfinder.controller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Taxes")
@XmlAccessorType (XmlAccessType.FIELD)
public class clsBillTaxDtl
{
//	@XmlElement(name="TaxCode")
//	private String strTaxCode;
	
	@XmlElement(name="TaxDesciption")
	private String strTaxDesc;
	
	@XmlElement(name="TaxAmount")
	private double dblTaxAmt;
	
	
	public clsBillTaxDtl()
	{
		super();		
	}
	public clsBillTaxDtl(String strTaxDesc, double dblTaxAmt)
	{
		super();
		this.strTaxDesc = strTaxDesc;
		this.dblTaxAmt = dblTaxAmt;
	}
	public String getStrTaxDesc()
	{
		return strTaxDesc;
	}
	public void setStrTaxDesc(String strTaxDesc)
	{
		this.strTaxDesc = strTaxDesc;
	}
	public double getDblTaxAmt()
	{
		return dblTaxAmt;
	}
	public void setDblTaxAmt(double dblTaxAmt)
	{
		this.dblTaxAmt = dblTaxAmt;
	}
	/*public String getStrTaxCode()
	{
		return strTaxCode;
	}
	public void setStrTaxCode(String strTaxCode)
	{
		this.strTaxCode = strTaxCode;
	}*/
	
	
}
