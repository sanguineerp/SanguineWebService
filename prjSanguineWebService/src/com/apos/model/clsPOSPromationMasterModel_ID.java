package com.apos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSPromationMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPromoCode")
	private String strPromoCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsPOSPromationMasterModel_ID(){}
	public clsPOSPromationMasterModel_ID(String strPromoCode,String strClientCode){
		this.strPromoCode=strPromoCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = strPromoCode;
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
		clsPOSPromationMasterModel_ID objModelId = (clsPOSPromationMasterModel_ID)obj;
		if(this.strPromoCode.equals(objModelId.getStrPromoCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPromoCode.hashCode()+this.strClientCode.hashCode();
	}

}
