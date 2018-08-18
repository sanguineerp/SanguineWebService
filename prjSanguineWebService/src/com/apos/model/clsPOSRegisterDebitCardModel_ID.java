package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSRegisterDebitCardModel_ID implements Serializable{

//Variable Declaration
	
	private String strCardNo;

    private String strClientCode;
	

	
	public clsPOSRegisterDebitCardModel_ID(){}
	
	
	
	public clsPOSRegisterDebitCardModel_ID(String strCardNo,String strClientCode) {
		
		this.strCardNo = strCardNo;
		
		this.strClientCode = strClientCode;
	}



	

//Setter-Getter Methods
	
	
	
	public String getStrClientCode() {
		return strClientCode;
	}

	public String getStrCardNo() {
		return strCardNo;
	}



	public void setStrCardNo(String strCardNo) {
		this.strCardNo = strCardNo;
	}



	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	
	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPOSRegisterDebitCardModel_ID objModelId = (clsPOSRegisterDebitCardModel_ID)obj;
		if(this.strCardNo.equals(objModelId.getStrCardNo()) && this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strCardNo.hashCode()+this.strClientCode.hashCode();
	}

}
