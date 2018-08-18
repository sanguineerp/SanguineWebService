package com.apos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

@Embeddable
public class clsPOSTDHDtlModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsPOSTDHDtlModel(){}

	
//Variable Declaration


	@Column(name="strItemCode")
	private String strItemCode;

	@Column(name="strSubItemCode")
	private String strSubItemCode;

	@Column(name="intSubItemQty")
	private long intSubItemQty;

	@Column(name="strDefaultYN")
	private String strDefaultYN;

	@Column(name="strSubItemMenuCode")
	private String strSubItemMenuCode;

//Setter-Getter Methods

	public String getStrItemCode(){
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode){
		this. strItemCode = (String) setDefaultValue( strItemCode, "NA");
	}

	public String getStrSubItemCode(){
		return strSubItemCode;
	}
	public void setStrSubItemCode(String strSubItemCode){
		this. strSubItemCode = (String) setDefaultValue( strSubItemCode, "NA");
	}

	public long getIntSubItemQty(){
		return intSubItemQty;
	}
	public void setIntSubItemQty(long intSubItemQty){
		this. intSubItemQty = (Long) setDefaultValue( intSubItemQty, "0");
	}

	public String getStrDefaultYN(){
		return strDefaultYN;
	}
	public void setStrDefaultYN(String strDefaultYN){
		this. strDefaultYN = (String) setDefaultValue( strDefaultYN, "NA");
	}

	public String getStrSubItemMenuCode(){
		return strSubItemMenuCode;
	}
	public void setStrSubItemMenuCode(String strSubItemMenuCode){
		this. strSubItemMenuCode = (String) setDefaultValue( strSubItemMenuCode, "NA");
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
