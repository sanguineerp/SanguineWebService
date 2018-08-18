package com.apos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsmasteroperationstatusModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strTableName")
	private String strTableName;

	@Column(name="strClientCode")
	private String strClientCode;

	public clsmasteroperationstatusModel_ID(){}
	public clsmasteroperationstatusModel_ID(String strTableName,String strClientCode){
		this.strTableName=strTableName;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrTableName(){
		return strTableName;
	}
	public void setStrTableName(String strTableName){
		this. strTableName = strTableName;
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
		clsmasteroperationstatusModel_ID objModelId = (clsmasteroperationstatusModel_ID)obj;
		if(this.strTableName.equals(objModelId.getStrTableName())&& this.strClientCode.equals(objModelId.getStrClientCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strTableName.hashCode()+this.strClientCode.hashCode();
	}

}
