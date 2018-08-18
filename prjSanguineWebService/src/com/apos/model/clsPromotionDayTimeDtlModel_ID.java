package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPromotionDayTimeDtlModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strPromoCode")
	private String strPromoCode;

	@Column(name="strDay")
	private String strDay;
	
	@Column(name="tmeFromTime")
	private String tmeFromTime;

	@Column(name="tmeToTime")
	private String tmeToTime;
	
	@Column(name="strClientCode")
	private String strClientCode;

	public clsPromotionDayTimeDtlModel_ID(){}
	public clsPromotionDayTimeDtlModel_ID(String strPromoCode,String strDay,String tmeFromTime, String tmeToTime, String strClientCode){
		this.strPromoCode=strPromoCode;
		this.strDay=strDay;
		this.tmeFromTime=tmeFromTime;
		this.tmeToTime=tmeToTime;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = strPromoCode;
	}

	public String getStrDay(){
		return strDay;
	}
	public void setStrDay(String strDay){
		this. strDay = strDay;
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
	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = strClientCode;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPromotionDayTimeDtlModel_ID objModelId = (clsPromotionDayTimeDtlModel_ID)obj;
		if(this.strPromoCode.equals(objModelId.getStrPromoCode())&& this.strDay.equals(objModelId.getStrDay())&& this.tmeFromTime.equals(objModelId.getTmeFromTime())&& this.tmeToTime.equals(objModelId.getTmeToTime())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strPromoCode.hashCode()+this.strDay.hashCode()+this.tmeFromTime.hashCode()+this.tmeToTime.hashCode()+this.strClientCode.hashCode();
	}

}
