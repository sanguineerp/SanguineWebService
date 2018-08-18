package com.apos.model;

import javax.persistence.Embeddable;



@Embeddable
public class clsRecipeDtlModel{

//Variable Declaration
	

	
	private String strChildItemCode;

	
	private double dblQuantity;

	
	private String strPOSCode;

	private String strDataPostFlag;

//Setter-Getter Methods
	

	public String getStrChildItemCode(){
		return strChildItemCode;
	}
	public void setStrChildItemCode(String strChildItemCode){
		this. strChildItemCode = (String) setDefaultValue( strChildItemCode, "NA");
	}

	public double getDblQuantity(){
		return dblQuantity;
	}
	public void setDblQuantity(double dblQuantity){
		this. dblQuantity = (Double) setDefaultValue( dblQuantity, "0.0000");
	}

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
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
