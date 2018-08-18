package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsDebitCardMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCardTypeCode")
	private String strCardTypeCode;

	@Column(name="strClientCode")
	private String strClientCode;

	
	public clsDebitCardMasterModel_ID(){}
	public clsDebitCardMasterModel_ID(String strClientCode,String strCardTypeCode){
		this.strCardTypeCode=strCardTypeCode;
		this.strClientCode=strClientCode;
		
	}
//Setter-Getter Methods
	public String getStrCardTypeCode(){
		return strCardTypeCode;
	}
	public void setStrCardTypeCode(String strCardTypeCode){
		this. strCardTypeCode = strCardTypeCode;
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
		clsDebitCardMasterModel_ID objModelId = (clsDebitCardMasterModel_ID)obj;
		if(this.strCardTypeCode.equals(objModelId.getStrCardTypeCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strCardTypeCode.hashCode()+this.strClientCode.hashCode();
	}

}
