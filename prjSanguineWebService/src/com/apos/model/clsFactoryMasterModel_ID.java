package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsFactoryMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strFactoryCode")
	private String strFactoryCode;
	
	@Column(name = "strClientCode")
    private String strClientCode;

	public clsFactoryMasterModel_ID(){}
	public clsFactoryMasterModel_ID(String strFactoryCode, String strClientCode){
		this.strFactoryCode=strFactoryCode;
		this.strClientCode = strClientCode;
	}

//Setter-Getter Methods
	public String getStrFactoryCode(){
		return strFactoryCode;
	}
	public void setStrFactoryCode(String strFactoryCode){
		this. strFactoryCode = strFactoryCode;
	}
	public String getStrClientCode()
    {
	return strClientCode;
    }
    
    public void setStrClientCode(String strClientCode)
    {
	this.strClientCode = strClientCode;
    }

//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsFactoryMasterModel_ID objModelId = (clsFactoryMasterModel_ID)obj;
		if(this.strFactoryCode.equals(objModelId.getStrFactoryCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strFactoryCode.hashCode()+ this.strClientCode.hashCode();
	}

}
