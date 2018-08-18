package com.apos.model;

import javax.persistence.Embeddable;


@Embeddable
public class clsPromotionDayTimeDtlHdModel{


//Variable Declaration
	
	private String strDay;

	private String tmeFromTime;

	
	private String tmeToTime;
	
	
	private String strDataPostFlag;

//Setter-Getter Methods
	

	public String getStrDay(){
		return strDay;
	}
	public void setStrDay(String strDay){
		this. strDay = (String) setDefaultValue( strDay, "NA");
	}

	public String getTmeFromTime() {
		return tmeFromTime;
	}

	public void setTmeFromTime(String tmeFromTime) {
		this.tmeFromTime = tmeFromTime;
	}

	public String getTmeToTime() {
		return tmeToTime;
	}

	public void setTmeToTime(String tmeToTime) {
		this.tmeToTime = tmeToTime;
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
