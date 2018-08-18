package com.apos.model;

import javax.persistence.Embeddable;


@Embeddable
public class clsBuyPromotionDtlHdModel {
	

//Variable Declaration

	private String strBuyPromoItemCode;


	private double dblBuyItemQty;

	
	private String strOperator;

	
	private String strDataPostFlag;

//Setter-Getter Methods
	

	public String getStrBuyPromoItemCode(){
		return strBuyPromoItemCode;
	}
	public void setStrBuyPromoItemCode(String strBuyPromoItemCode){
		this. strBuyPromoItemCode = (String) setDefaultValue( strBuyPromoItemCode, "NA");
	}

	public double getDblBuyItemQty(){
		return dblBuyItemQty;
	}
	public void setDblBuyItemQty(double dblBuyItemQty){
		this. dblBuyItemQty = (Double) setDefaultValue( dblBuyItemQty, "0.0000");
	}

	public String getStrOperator(){
		return strOperator;
	}
	public void setStrOperator(String strOperator){
		this. strOperator = (String) setDefaultValue( strOperator, "NA");
	}

	
	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "");
	}


//Function to Set Default Values
	private Object setDefaultValue(Object value, Object defaultValue){
		if(value !=null && (value instanceof String && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Double && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Integer && value.toString().length()>0)){
			return value;
		}
		else if(value !=null && (value instanceof Long && value.toString().length()>0)){
			return value;
		}
		else{
			return defaultValue;
		}
	}

}
