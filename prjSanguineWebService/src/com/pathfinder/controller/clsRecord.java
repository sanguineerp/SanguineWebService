package com.pathfinder.controller;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Records")
@XmlAccessorType (XmlAccessType.FIELD)
public class clsRecord
{

	//@XmlElementWrapper(name="Record")
	@XmlElement(name="Record")
	private List<clsBillHd> listOfBills;
	
	
	

	public clsRecord()
	{
		super();		
	}
	
	
	

	public List<clsBillHd> getListOfBills()
	{
		return listOfBills;
	}

	public void setListOfBills(List<clsBillHd> listOfBills)
	{
		this.listOfBills = listOfBills;
	}
	
	
}
