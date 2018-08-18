package com.apos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsOrderMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strOrderCode")
	private String strOrderCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsOrderMasterModel_ID(){}
	public clsOrderMasterModel_ID(String strOrderCode,String strClientCode){
		this.strOrderCode=strOrderCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrOrderCode(){
		return strOrderCode;
	}
	public void setStrOrderCode(String strOrderCode){
		this. strOrderCode = strOrderCode;
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
		clsOrderMasterModel_ID objModelId = (clsOrderMasterModel_ID)obj;
		if(this.strOrderCode.equals(objModelId.getStrOrderCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strOrderCode.hashCode()+this.strClientCode.hashCode();
	}

}
