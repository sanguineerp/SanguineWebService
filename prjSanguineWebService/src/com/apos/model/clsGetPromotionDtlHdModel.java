package com.apos.model;

import javax.persistence.Embeddable;


@Embeddable
public class clsGetPromotionDtlHdModel{
	
//Variable Declaration


	private String strPromotionOn;

	private String strPromoItemCode;

	private double dblGetQty;

	private String strDiscountType;

	private double dblDiscount;

	private String strDataPostFlag;

//Setter-Getter Methods

	public String getStrPromotionOn(){
		return strPromotionOn;
	}
	public void setStrPromotionOn(String strPromotionOn){
		this. strPromotionOn = (String) setDefaultValue( strPromotionOn, "NA");
	}

	public String getStrPromoItemCode(){
		return strPromoItemCode;
	}
	public void setStrPromoItemCode(String strPromoItemCode){
		this. strPromoItemCode = (String) setDefaultValue( strPromoItemCode, "NA");
	}

	public double getDblGetQty(){
		return dblGetQty;
	}
	public void setDblGetQty(double dblGetQty){
		this. dblGetQty = (Double) setDefaultValue( dblGetQty, "0.0000");
	}

	public String getStrDiscountType(){
		return strDiscountType;
	}
	public void setStrDiscountType(String strDiscountType){
		this. strDiscountType = (String) setDefaultValue( strDiscountType, "NA");
	}

	public double getDblDiscount(){
		return dblDiscount;
	}
	public void setDblDiscount(double dblDiscount){
		this. dblDiscount = (Double) setDefaultValue( dblDiscount, "0.0000");
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
