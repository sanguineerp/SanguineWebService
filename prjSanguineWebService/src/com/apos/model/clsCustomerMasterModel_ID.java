package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsCustomerMasterModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strCustomerCode")
	private String strCustomerCode;

	@Column(name="strOfficeBuildingCode")
	private String strOfficeBuildingCode;
	
	 @Column(name = "strClientCode")
	    private String strClientCode;


	public clsCustomerMasterModel_ID(){}
	public clsCustomerMasterModel_ID(String strCustomerCode,String strOfficeBuildingCode, String strClientCode){
		this.strCustomerCode=strCustomerCode;
		this.strOfficeBuildingCode=strOfficeBuildingCode;
		this.strClientCode = strClientCode;
	}

//Setter-Getter Methods
	public String getStrCustomerCode(){
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode){
		this. strCustomerCode = strCustomerCode;
	}

	public String getStrOfficeBuildingCode(){
		return strOfficeBuildingCode;
	}
	public void setStrOfficeBuildingCode(String strOfficeBuildingCode){
		this. strOfficeBuildingCode = strOfficeBuildingCode;
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
		clsCustomerMasterModel_ID objModelId = (clsCustomerMasterModel_ID)obj;
		if(this.strCustomerCode.equals(objModelId.getStrCustomerCode())&& this.strOfficeBuildingCode.equals(objModelId.getStrOfficeBuildingCode())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strCustomerCode.hashCode()+this.strOfficeBuildingCode.hashCode()+ this.strClientCode.hashCode();
	}

}
