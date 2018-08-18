package com.apos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPromotionMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPromoCode")
	private String strPromoCode;

	public clsPromotionMasterModel_ID(){}
	public clsPromotionMasterModel_ID(String strPromoCode){
		this.strPromoCode=strPromoCode;
	}

//Setter-Getter Methods
	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = strPromoCode;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPromotionMasterModel_ID objModelId = (clsPromotionMasterModel_ID)obj;
		if(this.strPromoCode.equals(objModelId.getStrPromoCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPromoCode.hashCode();
	}

}
