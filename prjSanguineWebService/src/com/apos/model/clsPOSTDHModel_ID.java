package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSTDHModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strTDHCode")
	private String strTDHCode;

	@Column(name = "strClientCode")
    private String strClientCode;


	public clsPOSTDHModel_ID(){}
	public clsPOSTDHModel_ID(String strTDHCode, String strClientCode){
		this.strTDHCode=strTDHCode;
		this.strClientCode=strClientCode;
		
	}

//Setter-Getter Methods
	public String getStrTDHCode(){
		return strTDHCode;
	}
	public void setStrTDHCode(String strTDHCode){
		this. strTDHCode = strTDHCode;
	}



public String getStrClientCode() {
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}
	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPOSTDHModel_ID objModelId = (clsPOSTDHModel_ID)obj;
		if(this.strTDHCode.equals(objModelId.getStrTDHCode()) && this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strTDHCode.hashCode()+ this.strClientCode.hashCode();
	}

}
