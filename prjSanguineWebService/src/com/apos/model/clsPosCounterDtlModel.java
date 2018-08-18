package com.apos.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;


@Embeddable
public class clsPosCounterDtlModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	

	public clsPosCounterDtlModel()
	{
	}

	 // Variable Declaration
    @Column(name = "strMenuCode")
    private String strMenuCode;



	public String getStrMenuCode() {
		return strMenuCode;
	}

	public void setStrMenuCode(String strMenuCode) {
		this.strMenuCode = strMenuCode;
	}

	
}

	