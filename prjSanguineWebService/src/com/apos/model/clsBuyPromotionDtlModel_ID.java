package com.apos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsBuyPromotionDtlModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPromoCode")
	private String strPromoCode;

	@Column(name="strBuyPromoItemCode")
	private String strBuyPromoItemCode;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsBuyPromotionDtlModel_ID(){}
	public clsBuyPromotionDtlModel_ID(String strPromoCode,String strBuyPromoItemCode,String strClientCode){
		this.strPromoCode=strPromoCode;
		this.strBuyPromoItemCode=strBuyPromoItemCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = strPromoCode;
	}

	public String getStrBuyPromoItemCode(){
		return strBuyPromoItemCode;
	}
	public void setStrBuyPromoItemCode(String strBuyPromoItemCode){
		this. strBuyPromoItemCode = strBuyPromoItemCode;
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
		clsBuyPromotionDtlModel_ID objModelId = (clsBuyPromotionDtlModel_ID)obj;
		if(this.strPromoCode.equals(objModelId.getStrPromoCode())&& this.strBuyPromoItemCode.equals(objModelId.getStrBuyPromoItemCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPromoCode.hashCode()+this.strBuyPromoItemCode.hashCode()+this.strClientCode.hashCode();
	}

}
