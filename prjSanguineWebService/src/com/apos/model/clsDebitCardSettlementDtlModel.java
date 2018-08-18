
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


@Embeddable
public class clsDebitCardSettlementDtlModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public clsDebitCardSettlementDtlModel(){}
	
	//Variable Declaration
	@Column(name="strSettlementCode")
	private String strSettlementCode;

	@Column(name="strApplicable")
	private String strApplicable;

	@Column(name="strDataPostFlag")
	private String strDataPostFlag;
	
	public String getStrSettlementCode() {
		return strSettlementCode;
	}

	public void setStrSettlementCode(String strSettlementCode) {
		this.strSettlementCode = strSettlementCode;
	}

	public String getStrApplicable() {
		return strApplicable;
	}
	public void setStrApplicable(String strApplicable) {
		this.strApplicable = strApplicable;
	}
	
	public String getStrDataPostFlag() {
		return strDataPostFlag;
	}

	public void setStrDataPostFlag(String strDataPostFlag) {
		this.strDataPostFlag = strDataPostFlag;
	}
	
}

	