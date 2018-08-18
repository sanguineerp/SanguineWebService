package com.onlineordering.beans;

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
	private List<clsOrderHDBean> listOfHDOrders;
	
	
	

	public clsRecord()
	{
		super();		
	}




	public List<clsOrderHDBean> getListOfHDOrders()
	{
		return listOfHDOrders;
	}




	public void setListOfHDOrders(List<clsOrderHDBean> listOfHDOrders)
	{
		this.listOfHDOrders = listOfHDOrders;
	}
	
	
	
}
