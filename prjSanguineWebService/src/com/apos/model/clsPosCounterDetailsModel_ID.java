package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPosCounterDetailsModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCounterCode")
	private String strCounterCode;
	
	@Column(name="strMenuCode")
	private String strMenuCode;
	

	

public clsPosCounterDetailsModel_ID() {
		
	}
public clsPosCounterDetailsModel_ID(String strCounterCode,
			String strMenuCode) {
		super();
		this.strCounterCode = strCounterCode;
		this.strMenuCode = strMenuCode;
	}
	//Setter-Getter Methods
	public String getStrCounterCode() {
		return strCounterCode;
	}
	public void setStrCounterCode(String strCounterCode) {
		this.strCounterCode = strCounterCode;
	}
	public String getStrMenuCode() {
		return strMenuCode;
	}
	public void setStrMenuCode(String strMenuCode) {
		this.strMenuCode = strMenuCode;
	}
	//HashCode and Equals Funtions
	
	@Override
	public int hashCode() {
		return this.strCounterCode.hashCode();
		
	}




	

}
