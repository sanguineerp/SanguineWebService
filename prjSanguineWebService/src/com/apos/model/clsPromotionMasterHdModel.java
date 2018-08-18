package com.apos.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

@Entity
@Table(name="tblpromotionmaster")
@IdClass(clsPromotionMasterModel_ID.class)

public class clsPromotionMasterHdModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPromotionMasterHdModel(){}

	public clsPromotionMasterHdModel(clsPromotionMasterModel_ID objModelID){
		strPromoCode = objModelID.getStrPromoCode();
	}

	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strPromoCode",column=@Column(name="strPromoCode"))
	})

//Variable Declaration
	@Column(name="strPromoCode")
	private String strPromoCode;

	@Column(name="strPromoName")
	private String strPromoName;

	@Column(name="strPromotionOn")
	private String strPromotionOn;

	@Column(name="strPromoItemCode")
	private String strPromoItemCode;

	@Column(name="strOperator")
	private String strOperator;

	@Column(name="dblBuyQty")
	private double dblBuyQty;

	@Column(name="dteFromDate")
	private String dteFromDate;

	@Column(name="dteToDate")
	private String dteToDate;

	@Column(name="strDays")
	private String strDays;

	@Column(name="strType")
	private String strType;

	@Column(name="strPromoNote")
	private String strPromoNote;

	@Column(name="strUserCreated")
	private String strUserCreated;

	@Column(name="strUserEdited")
	private String strUserEdited;

	@Column(name="dteDateCreated")
	private String dteDateCreated;

	@Column(name="dteDateEdited")
	private String dteDateEdited;

	@Column(name="strClientCode")
	private String strClientCode;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;

	@Column(name="strPOSCode")
	private String strPOSCode;

	@Column(name="strGetItemCode")
	private String strGetItemCode;

	@Column(name="strGetPromoOn")
	private String strGetPromoOn;

//Setter-Getter Methods
	public String getStrPromoCode(){
		return strPromoCode;
	}
	public void setStrPromoCode(String strPromoCode){
		this. strPromoCode = (String) setDefaultValue( strPromoCode, "NA");
	}

	public String getStrPromoName(){
		return strPromoName;
	}
	public void setStrPromoName(String strPromoName){
		this. strPromoName = (String) setDefaultValue( strPromoName, "NA");
	}

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

	public String getStrOperator(){
		return strOperator;
	}
	public void setStrOperator(String strOperator){
		this. strOperator = (String) setDefaultValue( strOperator, "NA");
	}

	public double getDblBuyQty(){
		return dblBuyQty;
	}
	public void setDblBuyQty(double dblBuyQty){
		this. dblBuyQty = (Double) setDefaultValue( dblBuyQty, "0.0000");
	}

	public String getDteFromDate(){
		return dteFromDate;
	}
	public void setDteFromDate(String dteFromDate){
		this.dteFromDate=dteFromDate;
	}

	public String getDteToDate(){
		return dteToDate;
	}
	public void setDteToDate(String dteToDate){
		this.dteToDate=dteToDate;
	}

	public String getStrDays(){
		return strDays;
	}
	public void setStrDays(String strDays){
		this. strDays = (String) setDefaultValue( strDays, "NA");
	}

	public String getStrType(){
		return strType;
	}
	public void setStrType(String strType){
		this. strType = (String) setDefaultValue( strType, "NA");
	}

	public String getStrPromoNote(){
		return strPromoNote;
	}
	public void setStrPromoNote(String strPromoNote){
		this. strPromoNote = (String) setDefaultValue( strPromoNote, "NA");
	}

	public String getStrUserCreated(){
		return strUserCreated;
	}
	public void setStrUserCreated(String strUserCreated){
		this. strUserCreated = (String) setDefaultValue( strUserCreated, "NA");
	}

	public String getStrUserEdited(){
		return strUserEdited;
	}
	public void setStrUserEdited(String strUserEdited){
		this. strUserEdited = (String) setDefaultValue( strUserEdited, "NA");
	}

	public String getDteDateCreated(){
		return dteDateCreated;
	}
	public void setDteDateCreated(String dteDateCreated){
		this.dteDateCreated=dteDateCreated;
	}

	public String getDteDateEdited(){
		return dteDateEdited;
	}
	public void setDteDateEdited(String dteDateEdited){
		this.dteDateEdited=dteDateEdited;
	}

	public String getStrClientCode(){
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode){
		this. strClientCode = (String) setDefaultValue( strClientCode, "NA");
	}

	public String getStrDataPostFlag(){
		return strDataPostFlag;
	}
	public void setStrDataPostFlag(String strDataPostFlag){
		this. strDataPostFlag = (String) setDefaultValue( strDataPostFlag, "NA");
	}

	public String getStrPOSCode(){
		return strPOSCode;
	}
	public void setStrPOSCode(String strPOSCode){
		this. strPOSCode = (String) setDefaultValue( strPOSCode, "NA");
	}

	public String getStrGetItemCode(){
		return strGetItemCode;
	}
	public void setStrGetItemCode(String strGetItemCode){
		this. strGetItemCode = (String) setDefaultValue( strGetItemCode, "NA");
	}

	public String getStrGetPromoOn(){
		return strGetPromoOn;
	}
	public void setStrGetPromoOn(String strGetPromoOn){
		this. strGetPromoOn = (String) setDefaultValue( strGetPromoOn, "NA");
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
