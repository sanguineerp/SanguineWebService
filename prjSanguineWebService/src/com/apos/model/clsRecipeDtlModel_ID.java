package com.apos.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;
@Embeddable
@SuppressWarnings("serial")

public class clsRecipeDtlModel_ID implements Serializable{

//Variable Declaration
	@Column(name="strRecipeCode")
	private String strRecipeCode;

	@Column(name="strChildItemCode")
	private String strChildItemCode;
	
	@Column(name="strClientCode")
	private String strClientCode;


	public clsRecipeDtlModel_ID(){}
	public clsRecipeDtlModel_ID(String strRecipeCode,String strChildItemCode,String strClientCode){
		this.strRecipeCode=strRecipeCode;
		this.strChildItemCode=strChildItemCode;
		this.strClientCode=strClientCode;
	}

//Setter-Getter Methods
	public String getStrRecipeCode(){
		return strRecipeCode;
	}
	public void setStrRecipeCode(String strRecipeCode){
		this. strRecipeCode = strRecipeCode;
	}

	public String getStrChildItemCode(){
		return strChildItemCode;
	}
	public void setStrChildItemCode(String strChildItemCode){
		this. strChildItemCode = strChildItemCode;
	}


public String getStrClientCode() {
		return strClientCode;
	}
	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}
	//HashCode and Equals Funtions


}
