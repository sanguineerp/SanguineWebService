package com.apos.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsPOSTDHDtlModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strTDHCode")
	private String strTDHCode;

	public clsPOSTDHDtlModel_ID(){}
	public clsPOSTDHDtlModel_ID(String strTDHCode){
		this.strTDHCode=strTDHCode;
	}

//Setter-Getter Methods
	public String getStrTDHCode(){
		return strTDHCode;
	}
	public void setStrTDHCode(String strTDHCode){
		this. strTDHCode = strTDHCode;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj) {
		clsPOSTDHDtlModel_ID objModelId = (clsPOSTDHDtlModel_ID)obj;
		if(this.strTDHCode.equals(objModelId.getStrTDHCode())){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.strTDHCode.hashCode();
	}

}
