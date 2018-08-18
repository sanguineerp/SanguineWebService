package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsCounterMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCounterCode")
	private String strCounterCode;

	@Column(name="strClientCode")
	private String strClientCode;
	
	public clsCounterMasterModel_ID(){}
	public clsCounterMasterModel_ID(String strCounterCode,String strClientCode){
		this.strCounterCode=strCounterCode;
		this.strClientCode=strClientCode;
		
	}

//Setter-Getter Methods
	public String getStrCounterCode(){
		return strCounterCode;
	}
	public void setStrCounterCode(String strCounterCode){
		this. strCounterCode = strCounterCode;
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = strClientCode;
	}

	


	//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsCounterMasterModel_ID objModelId = (clsCounterMasterModel_ID)obj;
		if(this.strCounterCode.equals(objModelId.getStrCounterCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}
	@Override
	public int hashCode() {
		return this.strCounterCode.hashCode()+this.strClientCode.hashCode();
	}

}
