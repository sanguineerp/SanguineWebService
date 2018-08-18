package com.apos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsGetPromotionDtlModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPromoCode")
	private String strPromoCode;

	@Column(name="strPromoItemCode")
	private String strPromoItemCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsGetPromotionDtlModel_ID(){}
	public clsGetPromotionDtlModel_ID(String strPromoCode,String strPromoItemCode,String strClientCode){
		this.strPromoCode=strPromoCode;
		this.strPromoItemCode=strPromoItemCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = strPromoCode;
	}

	public String getStrPromoItemCode(){
		return strPromoItemCode;
	}
	public void setStrPromoItemCode(String strPromoItemCode){
		this. strPromoItemCode = strPromoItemCode;
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
		clsGetPromotionDtlModel_ID objModelId = (clsGetPromotionDtlModel_ID)obj;
		if(this.strPromoCode.equals(objModelId.getStrPromoCode())&& this.strPromoItemCode.equals(objModelId.getStrPromoItemCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPromoCode.hashCode()+this.strPromoItemCode.hashCode()+this.strClientCode.hashCode();
	}

}
