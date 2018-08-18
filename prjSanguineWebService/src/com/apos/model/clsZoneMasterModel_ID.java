package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsZoneMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strZoneCode")
	private String strZoneCode;
	
	  @Column(name = "strClientCode")
	    private String strClientCode;

	
	public clsZoneMasterModel_ID(String strZoneCode, String strClientCode){
		this.strZoneCode=strZoneCode;
		this.strClientCode=strClientCode;
	}
	 public clsZoneMasterModel_ID()
	    {
	    } 

//Setter-Getter Methods
	public String getStrZoneCode(){
		return strZoneCode;
	}
	public void setStrZoneCode(String strZoneCode){
		this. strZoneCode = strZoneCode;
	}
	
	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}



//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsZoneMasterModel_ID objModelId = (clsZoneMasterModel_ID)obj;
		if(this.strZoneCode.equals(objModelId.getStrZoneCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strZoneCode.hashCode();
	}

	
}
