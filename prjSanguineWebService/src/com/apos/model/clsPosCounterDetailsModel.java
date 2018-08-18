package com.apos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="tblcounterdtl")

@IdClass(clsPosCounterDetailsModel_ID.class)
public class clsPosCounterDetailsModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	

	public clsPosCounterDetailsModel()
	{
	}

	public clsPosCounterDetailsModel(clsPosCounterDetailsModel_ID objModelID)
	{
		strCounterCode = objModelID.getStrCounterCode();
		strMenuCode= objModelID.getStrMenuCode();
	}


	@Id
	@AttributeOverrides({
		@AttributeOverride(name="strCounterCode",column=@Column(name="strCounterCode")), @AttributeOverride(name = "strMenuCode", column = @Column(name = "strMenuCode"))
	})
	
//Variable Declaration
	@Column(name="strCounterCode")
	private String strCounterCode;

	@Column(name="strMenuCode")
	private String strMenuCode;

	

	@Column(name="strClientCode")
	private String strClientCode;
	
	@Transient
	private String strMenuName;
	
	
	
	
	
	public String getStrMenuName() {
		return strMenuName;
	}

	public void setStrMenuName(String strMenuName) {
		this.strMenuName = strMenuName;
	}

	public String getStrCounterCode() {
		return strCounterCode;
	}
	public void setStrCounterCode(String strCounterCode) {
		this.strCounterCode = strCounterCode;
	}
	public String getStrMenuCode() {
		return strMenuCode;
	}
	public void setStrMenuCode(String strMenuCode) {
		this.strMenuCode = strMenuCode;
	}
	public String getStrClientCode() {
		return strClientCode;
	}

	public void setStrClientCode(String strClientCode) {
		this.strClientCode = strClientCode;
	}

	
}

	